/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.cglib.core.NamingPolicy;
/*     */ import org.logicalcobwebs.cglib.core.Predicate;
/*     */ import org.logicalcobwebs.cglib.proxy.Callback;
/*     */ import org.logicalcobwebs.cglib.proxy.Enhancer;
/*     */ import org.logicalcobwebs.cglib.proxy.Factory;
/*     */ 
/*     */ class ProxyFactory
/*     */ {
/*  37 */   private static final Log LOG = LogFactory.getLog(ProxyFactory.class);
/*     */ 
/*  39 */   private static Map interfaceMap = new HashMap();
/*     */ 
/*  50 */   private static NamingPolicy NAMING_POLICY = new NamingPolicy() {
/*     */     public String getClassName(String prefix, String source, Object key, Predicate names) {
/*  52 */       StringBuffer sb = new StringBuffer();
/*  53 */       sb.append(prefix != null ? prefix : prefix.startsWith("java") ? "$" + prefix : "net.sf.cglib.empty.Object");
/*     */ 
/*  61 */       sb.append("$$");
/*  62 */       sb.append(source.substring(source.lastIndexOf('.') + 1));
/*  63 */       sb.append("ByProxool$$");
/*  64 */       sb.append(Integer.toHexString(key.hashCode()));
/*  65 */       String base = sb.toString();
/*  66 */       String attempt = base;
/*  67 */       int index = 2;
/*  68 */       while (names.evaluate(attempt)) {
/*  69 */         attempt = base + "_" + index++;
/*     */       }
/*     */ 
/*  72 */       return attempt;
/*     */     }
/*  50 */   };
/*     */ 
/*     */   protected static Connection getWrappedConnection(ProxyConnection proxyConnection)
/*     */   {
/*  85 */     return (Connection)getProxy(proxyConnection.getConnection(), new WrappedConnection(proxyConnection), proxyConnection.getDefinition());
/*     */   }
/*     */ 
/*     */   protected static Statement getStatement(Statement delegate, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection, String sqlStatement)
/*     */   {
/*  97 */     return (Statement)getProxy(delegate, new ProxyStatement(delegate, connectionPool, proxyConnection, sqlStatement), proxyConnection.getDefinition());
/*     */   }
/*     */ 
/*     */   protected static DatabaseMetaData getDatabaseMetaData(DatabaseMetaData databaseMetaData, Connection wrappedConnection)
/*     */   {
/* 107 */     return (DatabaseMetaData)getProxy(databaseMetaData, new ProxyDatabaseMetaData(databaseMetaData, wrappedConnection), null);
/*     */   }
/*     */ 
/*     */   private static Object getProxy(Object delegate, Callback callback, ConnectionPoolDefinitionIF def) {
/* 111 */     Enhancer e = new Enhancer();
/* 112 */     e.setNamingPolicy(NAMING_POLICY);
/* 113 */     e.setInterfaces(getInterfaces(delegate.getClass(), def));
/* 114 */     e.setCallback(callback);
/* 115 */     e.setClassLoader(ProxyFactory.class.getClassLoader());
/* 116 */     return e.create();
/*     */   }
/*     */ 
/*     */   protected static Statement getDelegateStatement(Statement statement)
/*     */   {
/* 127 */     Statement ds = statement;
/* 128 */     ProxyStatement ps = (ProxyStatement)((Factory)statement).getCallback(0);
/* 129 */     ds = ps.getDelegateStatement();
/* 130 */     return ds;
/*     */   }
/*     */ 
/*     */   protected static Connection getDelegateConnection(Connection connection)
/*     */   {
/* 141 */     WrappedConnection wc = (WrappedConnection)((Factory)connection).getCallback(0);
/* 142 */     return wc.getProxyConnection().getConnection();
/*     */   }
/*     */ 
/*     */   private static Class[] getInterfaces(Class clazz, ConnectionPoolDefinitionIF cpd)
/*     */   {
/* 153 */     Class[] interfaceArray = (Class[])(Class[])interfaceMap.get(clazz);
/* 154 */     if (interfaceArray == null) {
/* 155 */       Set interfaces = new HashSet();
/* 156 */       traverseInterfacesRecursively(interfaces, clazz);
/* 157 */       if (cpd != null)
/*     */       {
/* 161 */         if (Connection.class.isAssignableFrom(clazz)) {
/* 162 */           Class injectableClass = cpd.getInjectableConnectionInterface();
/*     */ 
/* 164 */           if (injectableClass != null) {
/* 165 */             interfaces.add(injectableClass);
/* 166 */             if (LOG.isDebugEnabled()) {
/* 167 */               LOG.debug("Injecting " + injectableClass + " into " + clazz);
/*     */             }
/*     */           }
/*     */         }
/* 171 */         if (CallableStatement.class.isAssignableFrom(clazz)) {
/* 172 */           if (LOG.isDebugEnabled()) {
/* 173 */             LOG.debug("Getting injectableCallableStatementInterface");
/*     */           }
/* 175 */           Class injectableClass = cpd.getInjectableCallableStatementInterface();
/*     */ 
/* 177 */           if (injectableClass != null) {
/* 178 */             interfaces.add(injectableClass);
/* 179 */             if (LOG.isDebugEnabled()) {
/* 180 */               LOG.debug("Injecting " + injectableClass + " into " + clazz);
/*     */             }
/*     */           }
/*     */         }
/* 184 */         if (PreparedStatement.class.isAssignableFrom(clazz)) {
/* 185 */           Class injectableClass = cpd.getInjectablePreparedStatementInterface();
/*     */ 
/* 187 */           if (injectableClass != null) {
/* 188 */             interfaces.add(injectableClass);
/* 189 */             if (LOG.isDebugEnabled()) {
/* 190 */               LOG.debug("Injecting " + injectableClass + " into " + clazz);
/*     */             }
/*     */           }
/*     */         }
/* 194 */         if (Statement.class.isAssignableFrom(clazz)) {
/* 195 */           Class injectableClass = cpd.getInjectableStatementInterface();
/*     */ 
/* 197 */           if (injectableClass != null) {
/* 198 */             interfaces.add(injectableClass);
/* 199 */             if (LOG.isDebugEnabled()) {
/* 200 */               LOG.debug("Injecting " + injectableClass + " into " + clazz);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 205 */       interfaceArray = (Class[])(Class[])interfaces.toArray(new Class[interfaces.size()]);
/* 206 */       if (LOG.isDebugEnabled()) {
/* 207 */         for (int i = 0; i < interfaceArray.length; i++) {
/* 208 */           Class aClass = interfaceArray[i];
/* 209 */           LOG.debug("Implementing " + aClass);
/*     */         }
/*     */       }
/* 212 */       interfaceMap.put(clazz, interfaceArray);
/*     */     }
/*     */ 
/* 220 */     return interfaceArray;
/*     */   }
/*     */ 
/*     */   private static void traverseInterfacesRecursively(Set interfaces, Class clazz)
/*     */   {
/* 232 */     if (!interfaces.contains(clazz))
/*     */     {
/* 245 */       Class[] interfaceArray = clazz.getInterfaces();
/* 246 */       for (int i = 0; i < interfaceArray.length; i++)
/*     */       {
/* 252 */         traverseInterfacesRecursively(interfaces, interfaceArray[i]);
/*     */ 
/* 255 */         if (Modifier.isPublic(interfaceArray[i].getModifiers())) {
/* 256 */           interfaces.add(interfaceArray[i]);
/*     */         }
/*     */       }
/* 259 */       Class superClazz = clazz.getSuperclass();
/* 260 */       if (superClazz != null)
/* 261 */         traverseInterfacesRecursively(interfaces, superClazz);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static WrappedConnection getWrappedConnection(Connection connection)
/*     */   {
/* 277 */     return (WrappedConnection)((Factory)connection).getCallback(0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxyFactory
 * JD-Core Version:    0.6.0
 */
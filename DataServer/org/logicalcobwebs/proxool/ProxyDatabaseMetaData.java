/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodInterceptor;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodProxy;
/*     */ 
/*     */ class ProxyDatabaseMetaData
/*     */   implements MethodInterceptor
/*     */ {
/*  28 */   private static final Log LOG = LogFactory.getLog(ProxyDatabaseMetaData.class);
/*     */   private static final String GET_CONNECTION_METHOD = "getConnection";
/*     */   private static final String EQUALS_METHOD = "equals";
/*     */   private static final String FINALIZE_METHOD = "finalize";
/*     */   private DatabaseMetaData databaseMetaData;
/*     */   private Connection wrappedConnection;
/*     */ 
/*     */   public ProxyDatabaseMetaData(DatabaseMetaData databaseMetaData, Connection wrappedConnection)
/*     */   {
/*  45 */     this.databaseMetaData = databaseMetaData;
/*  46 */     this.wrappedConnection = wrappedConnection;
/*     */   }
/*     */ 
/*     */   public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
/*  50 */     Object result = null;
/*  51 */     int argCount = args != null ? args.length : 0;
/*     */     try {
/*  53 */       if (method.getName().equals("getConnection"))
/*  54 */         result = getConnection();
/*  55 */       else if ((method.getName().equals("equals")) && (argCount == 1))
/*  56 */         result = new Boolean(equals(args[0]));
/*  57 */       else if (method.getName().equals("finalize"))
/*  58 */         super.finalize();
/*     */       else
/*  60 */         result = method.invoke(getDatabaseMetaData(), args);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/*  63 */       throw e.getTargetException();
/*     */     } catch (Exception e) {
/*  65 */       LOG.error("Unexpected invocation exception", e);
/*  66 */       throw new RuntimeException("Unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */ 
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  80 */     return this.databaseMetaData.hashCode() == obj.hashCode();
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/*  90 */     return this.wrappedConnection;
/*     */   }
/*     */ 
/*     */   protected DatabaseMetaData getDatabaseMetaData()
/*     */   {
/*  98 */     return this.databaseMetaData;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 105 */     return this.databaseMetaData.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxyDatabaseMetaData
 * JD-Core Version:    0.6.0
 */
/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodInterceptor;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodProxy;
/*     */ import org.logicalcobwebs.proxool.proxy.InvokerFacade;
/*     */ 
/*     */ public class WrappedConnection
/*     */   implements MethodInterceptor
/*     */ {
/*  30 */   private static final Log LOG = LogFactory.getLog(WrappedConnection.class);
/*     */   private static final String CLOSE_METHOD = "close";
/*     */   private static final String IS_CLOSED_METHOD = "isClosed";
/*     */   private static final String EQUALS_METHOD = "equals";
/*     */   private static final String GET_META_DATA_METHOD = "getMetaData";
/*     */   private static final String FINALIZE_METHOD = "finalize";
/*     */   private static final String HASH_CODE_METHOD = "hashCode";
/*     */   private static final String TO_STRING_METHOD = "toString";
/*     */   private ProxyConnection proxyConnection;
/*     */   private long id;
/*     */   private String alias;
/*     */   private boolean manuallyClosed;
/*     */ 
/*     */   public WrappedConnection(ProxyConnection proxyConnection)
/*     */   {
/*  69 */     this.proxyConnection = proxyConnection;
/*  70 */     this.id = proxyConnection.getId();
/*  71 */     this.alias = proxyConnection.getDefinition().getAlias();
/*     */   }
/*     */ 
/*     */   public ProxyConnection getProxyConnection()
/*     */   {
/*  79 */     return this.proxyConnection;
/*     */   }
/*     */ 
/*     */   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
/*     */     throws Throwable
/*     */   {
/*  87 */     return invoke(proxy, method, args);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 107 */     Object result = null;
/* 108 */     int argCount = args != null ? args.length : 0;
/* 109 */     Method concreteMethod = method;
/* 110 */     if ((this.proxyConnection != null) && (this.proxyConnection.getConnection() != null))
/* 111 */       concreteMethod = InvokerFacade.getConcreteMethod(this.proxyConnection.getConnection().getClass(), method);
/*     */     try
/*     */     {
/* 114 */       if ((this.proxyConnection != null) && (this.proxyConnection.isReallyClosed()))
/*     */       {
/* 116 */         if (!concreteMethod.getName().equals("isClosed"))
/*     */         {
/* 118 */           if (!concreteMethod.getName().equals("close"))
/*     */           {
/* 120 */             if (this.manuallyClosed)
/*     */             {
/* 123 */               throw new SQLException("You can't perform any operations on a connection after you've called close()");
/*     */             }
/*     */ 
/* 127 */             throw new SQLException("You can't perform any operations on this connection. It has been automatically closed by Proxool for some reason (see logs).");
/*     */           }
/*     */         }
/*     */       }
/* 130 */       if (concreteMethod.getName().equals("close"))
/*     */       {
/* 133 */         if ((this.proxyConnection != null) && (!this.proxyConnection.isReallyClosed())) {
/* 134 */           this.proxyConnection.close();
/*     */ 
/* 136 */           this.proxyConnection = null;
/* 137 */           this.manuallyClosed = true;
/*     */         }
/* 139 */       } else if ((concreteMethod.getName().equals("equals")) && (argCount == 1)) {
/* 140 */         result = equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
/* 141 */       } else if ((concreteMethod.getName().equals("hashCode")) && (argCount == 0)) {
/* 142 */         result = new Integer(hashCode());
/* 143 */       } else if ((concreteMethod.getName().equals("isClosed")) && (argCount == 0)) {
/* 144 */         result = (this.proxyConnection == null) || (this.proxyConnection.isClosed()) ? Boolean.TRUE : Boolean.FALSE;
/* 145 */       } else if ((concreteMethod.getName().equals("getMetaData")) && (argCount == 0)) {
/* 146 */         if (this.proxyConnection != null) {
/* 147 */           Connection connection = ProxyFactory.getWrappedConnection(this.proxyConnection);
/* 148 */           result = ProxyFactory.getDatabaseMetaData(this.proxyConnection.getConnection().getMetaData(), connection);
/*     */         } else {
/* 150 */           throw new SQLException("You can't perform a " + concreteMethod.getName() + " operation after the connection has been closed");
/*     */         }
/* 152 */       } else if (concreteMethod.getName().equals("finalize")) {
/* 153 */         super.finalize();
/* 154 */       } else if (concreteMethod.getName().equals("toString")) {
/* 155 */         result = toString();
/*     */       }
/* 157 */       else if (this.proxyConnection != null) {
/* 158 */         if (concreteMethod.getName().startsWith("set"))
/* 159 */           this.proxyConnection.setNeedToReset(true);
/*     */         try
/*     */         {
/* 162 */           result = concreteMethod.invoke(this.proxyConnection.getConnection(), args);
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/* 169 */           LOG.debug("Ignoring IllegalAccessException whilst invoking the " + concreteMethod + " concrete method and trying the " + method + " method directly.");
/*     */ 
/* 172 */           InvokerFacade.overrideConcreteMethod(this.proxyConnection.getConnection().getClass(), method, method);
/* 173 */           result = method.invoke(this.proxyConnection.getConnection(), args);
/*     */         }
/*     */       } else {
/* 176 */         throw new SQLException("You can't perform a " + concreteMethod.getName() + " operation after the connection has been closed");
/*     */       }
/*     */ 
/* 182 */       if ((result instanceof Statement))
/*     */       {
/* 188 */         String sqlStatement = null;
/* 189 */         if ((argCount > 0) && ((args[0] instanceof String))) {
/* 190 */           sqlStatement = (String)args[0];
/*     */         }
/*     */ 
/* 194 */         this.proxyConnection.addOpenStatement((Statement)result);
/*     */ 
/* 196 */         result = ProxyFactory.getStatement((Statement)result, this.proxyConnection.getConnectionPool(), this.proxyConnection, sqlStatement);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 202 */       if (FatalSqlExceptionHelper.testException(this.proxyConnection.getDefinition(), e.getTargetException())) {
/* 203 */         FatalSqlExceptionHelper.throwFatalSQLException(this.proxyConnection.getDefinition().getFatalSqlExceptionWrapper(), e.getTargetException());
/*     */       }
/* 205 */       throw e.getTargetException();
/*     */     } catch (SQLException e) {
/* 207 */       throw new SQLException("Couldn't perform the operation " + concreteMethod.getName() + ": " + e.getMessage());
/*     */     } catch (Exception e) {
/* 209 */       LOG.error("Unexpected invocation exception", e);
/* 210 */       if (FatalSqlExceptionHelper.testException(this.proxyConnection.getDefinition(), e)) {
/* 211 */         FatalSqlExceptionHelper.throwFatalSQLException(this.proxyConnection.getDefinition().getFatalSqlExceptionWrapper(), e);
/*     */       }
/* 213 */       throw new RuntimeException("Unexpected invocation exception: " + e.getMessage());
/*     */     }
/*     */ 
/* 216 */     return result;
/*     */   }
/*     */ 
/*     */   public long getId()
/*     */   {
/* 225 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getAlias()
/*     */   {
/* 233 */     return this.alias;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 243 */     if ((obj instanceof Connection)) {
/* 244 */       WrappedConnection wc = ProxyFactory.getWrappedConnection((Connection)obj);
/* 245 */       if ((wc != null) && (wc.getId() > 0L) && (getId() > 0L)) {
/* 246 */         return wc.getId() == getId();
/*     */       }
/* 248 */       return false;
/*     */     }
/*     */ 
/* 251 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 259 */     if (this.proxyConnection != null) {
/* 260 */       return hashCode() + "(" + this.proxyConnection.getConnection().toString() + ")";
/*     */     }
/* 262 */     return hashCode() + "(out of scope)";
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.WrappedConnection
 * JD-Core Version:    0.6.0
 */
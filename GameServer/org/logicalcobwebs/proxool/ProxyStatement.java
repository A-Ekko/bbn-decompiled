/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Statement;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodInterceptor;
/*     */ import org.logicalcobwebs.cglib.proxy.MethodProxy;
/*     */ import org.logicalcobwebs.proxool.proxy.InvokerFacade;
/*     */ 
/*     */ class ProxyStatement extends AbstractProxyStatement
/*     */   implements MethodInterceptor
/*     */ {
/*  32 */   private static final Log LOG = LogFactory.getLog(ProxyStatement.class);
/*     */   private static final String EXECUTE_FRAGMENT = "execute";
/*     */   private static final String EXECUTE_BATCH_METHOD = "executeBatch";
/*     */   private static final String ADD_BATCH_METHOD = "addBatch";
/*     */   private static final String EQUALS_METHOD = "equals";
/*     */   private static final String CLOSE_METHOD = "close";
/*     */   private static final String GET_CONNECTION_METHOD = "getConnection";
/*     */   private static final String FINALIZE_METHOD = "finalize";
/*     */   private static final String SET_NULL_METHOD = "setNull";
/*     */   private static final String SET_PREFIX = "set";
/*     */ 
/*     */   public ProxyStatement(Statement statement, ConnectionPool connectionPool, ProxyConnectionIF proxyConnection, String sqlStatement)
/*     */   {
/*  53 */     super(statement, connectionPool, proxyConnection, sqlStatement);
/*     */   }
/*     */ 
/*     */   public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
/*  57 */     return invoke(proxy, method, args);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
/*  61 */     Object result = null;
/*  62 */     long startTime = System.currentTimeMillis();
/*  63 */     int argCount = args != null ? args.length : 0;
/*     */ 
/*  65 */     Method concreteMethod = InvokerFacade.getConcreteMethod(getStatement().getClass(), method);
/*     */ 
/*  68 */     if (concreteMethod.getName().equals("addBatch"))
/*     */     {
/*  70 */       if ((argCount > 0) && ((args[0] instanceof String))) {
/*  71 */         setSqlStatementIfNull((String)args[0]);
/*     */       }
/*  73 */       appendToSqlLog();
/*  74 */     } else if (concreteMethod.getName().equals("executeBatch"))
/*     */     {
/*  76 */       startExecute();
/*  77 */     } else if (concreteMethod.getName().startsWith("execute"))
/*     */     {
/*  79 */       if ((argCount > 0) && ((args[0] instanceof String))) {
/*  80 */         setSqlStatementIfNull((String)args[0]);
/*     */       }
/*  82 */       appendToSqlLog();
/*  83 */       startExecute();
/*     */     }
/*     */ 
/*  88 */     Exception exception = null;
/*     */     try {
/*  90 */       if ((concreteMethod.getName().equals("equals")) && (argCount == 1))
/*  91 */         result = equals(args[0]) ? Boolean.TRUE : Boolean.FALSE;
/*  92 */       else if ((concreteMethod.getName().equals("close")) && (argCount == 0))
/*  93 */         close();
/*  94 */       else if ((concreteMethod.getName().equals("getConnection")) && (argCount == 0))
/*  95 */         result = getConnection();
/*  96 */       else if ((concreteMethod.getName().equals("finalize")) && (argCount == 0))
/*  97 */         finalize();
/*     */       else {
/*     */         try {
/* 100 */           result = concreteMethod.invoke(getStatement(), args);
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/* 107 */           LOG.debug("Ignoring IllegalAccessException whilst invoking the " + concreteMethod + " concrete method and trying the " + method + " method directly.");
/*     */ 
/* 110 */           InvokerFacade.overrideConcreteMethod(getStatement().getClass(), method, method);
/* 111 */           result = method.invoke(getStatement(), args);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 116 */       if (isTrace())
/*     */       {
/*     */         try
/*     */         {
/* 120 */           if ((concreteMethod.getName().equals("setNull")) && (argCount > 0) && ((args[0] instanceof Integer))) {
/* 121 */             int index = ((Integer)args[0]).intValue();
/* 122 */             putParameter(index, null);
/* 123 */           } else if ((concreteMethod.getName().startsWith("set")) && (argCount > 1) && ((args[0] instanceof Integer))) {
/* 124 */             int index = ((Integer)args[0]).intValue();
/* 125 */             putParameter(index, args[1]);
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 130 */           LOG.error("Ignoring error during dump", e);
/*     */         }
/*     */       }
/*     */     } catch (InvocationTargetException e) {
/* 134 */       if ((e.getTargetException() instanceof Exception))
/* 135 */         exception = (Exception)e.getTargetException();
/*     */       else {
/* 137 */         exception = e;
/*     */       }
/* 139 */       if (testException(e.getTargetException()))
/*     */       {
/* 141 */         FatalSqlExceptionHelper.throwFatalSQLException(getConnectionPool().getDefinition().getFatalSqlExceptionWrapper(), e.getTargetException());
/*     */       }
/* 143 */       throw e.getTargetException();
/*     */     } catch (Exception e) {
/* 145 */       exception = e;
/* 146 */       if (testException(e))
/*     */       {
/* 148 */         FatalSqlExceptionHelper.throwFatalSQLException(getConnectionPool().getDefinition().getFatalSqlExceptionWrapper(), e);
/*     */       }
/* 150 */       throw e;
/*     */     }
/*     */     finally
/*     */     {
/* 154 */       if ((concreteMethod.getName().equals("executeBatch")) || (concreteMethod.getName().startsWith("execute"))) {
/* 155 */         trace(startTime, exception);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 160 */     return result;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxyStatement
 * JD-Core Version:    0.6.0
 */
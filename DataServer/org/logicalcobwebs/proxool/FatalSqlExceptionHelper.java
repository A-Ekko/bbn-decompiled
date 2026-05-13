/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ class FatalSqlExceptionHelper
/*     */ {
/*  25 */   private static final Log LOG = LogFactory.getLog(FatalSqlExceptionHelper.class);
/*     */ 
/*     */   protected static void throwFatalSQLException(String className, Throwable originalException)
/*     */     throws ProxoolException, SQLException, RuntimeException
/*     */   {
/*  37 */     if ((className != null) && (className.trim().length() > 0)) {
/*  38 */       Class clazz = null;
/*     */       try {
/*  40 */         clazz = Class.forName(className);
/*     */       } catch (ClassNotFoundException e) {
/*  42 */         throw new ProxoolException("Couldn't find class " + className);
/*     */       }
/*  44 */       if (!SQLException.class.isAssignableFrom(clazz))
/*     */       {
/*  46 */         if (!RuntimeException.class.isAssignableFrom(clazz))
/*     */         {
/*  49 */           throw new ProxoolException("Couldn't wrap up using " + clazz.getName() + " because it isn't either a RuntimeException or an SQLException");
/*     */         }
/*     */       }
/*  51 */       Constructor toUse = null;
/*  52 */       Object[] args = null;
/*  53 */       String argDescription = "";
/*  54 */       Constructor[] constructors = clazz.getConstructors();
/*  55 */       for (int i = 0; i < constructors.length; i++) {
/*  56 */         Constructor constructor = constructors[i];
/*  57 */         Class[] parameterTypes = constructor.getParameterTypes();
/*  58 */         if ((toUse == null) && (parameterTypes.length == 0)) {
/*  59 */           toUse = constructor;
/*     */         }
/*  61 */         if ((parameterTypes.length == 1) && (Exception.class.isAssignableFrom(parameterTypes[0]))) {
/*  62 */           toUse = constructor;
/*  63 */           args = new Object[] { originalException };
/*  64 */           argDescription = "Exception";
/*  65 */           break;
/*     */         }
/*     */       }
/*     */       try {
/*  69 */         Object exceptionToThrow = toUse.newInstance(args);
/*  70 */         if ((exceptionToThrow instanceof RuntimeException)) {
/*  71 */           LOG.debug("Wrapping up a fatal exception: " + originalException.getMessage(), originalException);
/*  72 */           throw ((RuntimeException)exceptionToThrow);
/*  73 */         }if ((exceptionToThrow instanceof SQLException)) {
/*  74 */           throw ((SQLException)exceptionToThrow);
/*     */         }
/*  76 */         throw new ProxoolException("Couldn't throw " + clazz.getName() + " because it isn't either a RuntimeException or an SQLException");
/*     */       }
/*     */       catch (InstantiationException e) {
/*  79 */         throw new ProxoolException("Couldn't create " + clazz.getName() + "(" + argDescription + ")", e);
/*     */       } catch (IllegalAccessException e) {
/*  81 */         throw new ProxoolException("Couldn't create " + clazz.getName() + "(" + argDescription + ")", e);
/*     */       } catch (InvocationTargetException e) {
/*  83 */         throw new ProxoolException("Couldn't create " + clazz.getName() + "(" + argDescription + ")", e);
/*     */       }
/*     */     }
/*  86 */     if ((originalException instanceof SQLException))
/*  87 */       throw ((SQLException)originalException);
/*  88 */     if ((originalException instanceof RuntimeException)) {
/*  89 */       throw ((RuntimeException)originalException);
/*     */     }
/*  91 */     throw new RuntimeException("Unexpected exception:" + originalException.getMessage());
/*     */   }
/*     */ 
/*     */   protected static boolean testException(ConnectionPoolDefinitionIF cpd, Throwable t)
/*     */   {
/* 103 */     return testException(cpd, t, 0);
/*     */   }
/*     */ 
/*     */   protected static boolean testException(ConnectionPoolDefinitionIF cpd, Throwable t, int level)
/*     */   {
/* 114 */     boolean fatalSqlExceptionDetected = false;
/* 115 */     Iterator i = cpd.getFatalSqlExceptions().iterator();
/* 116 */     while (i.hasNext()) {
/* 117 */       if ((t.getMessage() == null) || (t.getMessage().indexOf((String)i.next()) <= -1))
/*     */         continue;
/* 119 */       fatalSqlExceptionDetected = true;
/*     */     }
/*     */ 
/* 124 */     if ((!fatalSqlExceptionDetected) && (level < 20)) {
/* 125 */       Throwable cause = getCause(t);
/* 126 */       if (cause != null) {
/* 127 */         fatalSqlExceptionDetected = testException(cpd, cause, level + 1);
/*     */       }
/*     */     }
/*     */ 
/* 131 */     return fatalSqlExceptionDetected;
/*     */   }
/*     */ 
/*     */   protected static Throwable getCause(Throwable t)
/*     */   {
/* 143 */     Throwable cause = null;
/* 144 */     Method causeMethod = null;
/*     */     try
/*     */     {
/* 148 */       if (causeMethod == null) {
/* 149 */         causeMethod = getMethod(t, "getCause");
/*     */       }
/* 151 */       if (causeMethod == null) {
/* 152 */         causeMethod = getMethod(t, "getTargetException");
/*     */       }
/* 154 */       if (causeMethod == null) {
/* 155 */         causeMethod = getMethod(t, "getRootCause");
/*     */       }
/* 157 */       if (causeMethod == null) {
/* 158 */         causeMethod = getMethod(t, "getOriginalException");
/*     */       }
/*     */ 
/* 162 */       if (causeMethod != null)
/*     */         try {
/* 164 */           cause = (Throwable)causeMethod.invoke(t, null);
/*     */         } catch (IllegalAccessException e) {
/* 166 */           LOG.warn("Problem invoking " + t.getClass().getName() + "." + causeMethod.getName() + ". Ignoring.", e);
/*     */         } catch (IllegalArgumentException e) {
/* 168 */           LOG.warn("Problem invoking " + t.getClass().getName() + "." + causeMethod.getName() + ". Ignoring.", e);
/*     */         } catch (InvocationTargetException e) {
/* 170 */           LOG.warn("Problem invoking " + t.getClass().getName() + "." + causeMethod.getName() + ". Ignoring.", e);
/*     */         }
/*     */     }
/*     */     catch (Exception e) {
/* 174 */       LOG.warn("Unexpected exception drilling into exception. Ignoring.", e);
/*     */     }
/* 176 */     return cause;
/*     */   }
/*     */ 
/*     */   private static Method getMethod(Object o, String methodName) {
/* 180 */     Method m = null;
/*     */     try {
/* 182 */       m = o.getClass().getMethod(methodName, null);
/*     */ 
/* 184 */       if (!Throwable.class.isAssignableFrom(m.getReturnType()))
/* 185 */         m = null;
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*     */     }
/*     */     catch (SecurityException e) {
/* 190 */       LOG.warn("Problem finding method " + methodName, e);
/*     */     }
/* 192 */     return m;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.FatalSqlExceptionHelper
 * JD-Core Version:    0.6.0
 */
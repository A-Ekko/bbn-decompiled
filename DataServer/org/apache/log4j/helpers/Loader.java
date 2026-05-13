/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class Loader
/*     */ {
/*     */   static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
/*  29 */   private static boolean java1 = true;
/*     */ 
/*  31 */   private static boolean ignoreTCL = false;
/*     */ 
/*     */   public static URL getResource(String resource)
/*     */   {
/*  69 */     ClassLoader classLoader = null;
/*  70 */     URL url = null;
/*     */     try
/*     */     {
/*  73 */       if (!java1) {
/*  74 */         classLoader = getTCL();
/*  75 */         if (classLoader != null) {
/*  76 */           LogLog.debug("Trying to find [" + resource + "] using context classloader " + classLoader + ".");
/*     */ 
/*  78 */           url = classLoader.getResource(resource);
/*  79 */           if (url != null) {
/*  80 */             return url;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  87 */       classLoader = Loader.class.getClassLoader();
/*  88 */       if (classLoader != null) {
/*  89 */         LogLog.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.");
/*     */ 
/*  91 */         url = classLoader.getResource(resource);
/*  92 */         if (url != null)
/*  93 */           return url;
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/*  97 */       LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
/*     */     }
/*     */ 
/* 104 */     LogLog.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
/*     */ 
/* 106 */     return ClassLoader.getSystemResource(resource);
/*     */   }
/*     */ 
/*     */   public static boolean isJava1()
/*     */   {
/* 115 */     return java1;
/*     */   }
/*     */ 
/*     */   private static ClassLoader getTCL()
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 128 */     Method method = null;
/*     */     try {
/* 130 */       method = Thread.class.getMethod("getContextClassLoader", null);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 133 */       return null;
/*     */     }
/*     */ 
/* 136 */     return (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */   }
/*     */ 
/*     */   public static Class loadClass(String clazz)
/*     */     throws ClassNotFoundException
/*     */   {
/* 151 */     if ((java1) || (ignoreTCL))
/* 152 */       return Class.forName(clazz);
/*     */     try
/*     */     {
/* 155 */       return getTCL().loadClass(clazz);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/* 160 */     return Class.forName(clazz);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  34 */     String prop = OptionConverter.getSystemProperty("java.version", null);
/*     */ 
/*  36 */     if (prop != null) {
/*  37 */       int i = prop.indexOf('.');
/*  38 */       if ((i != -1) && 
/*  39 */         (prop.charAt(i + 1) != '1')) {
/*  40 */         java1 = false;
/*     */       }
/*     */     }
/*  43 */     String ignoreTCLProp = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
/*  44 */     if (ignoreTCLProp != null)
/*  45 */       ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.Loader
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.commons.logging;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public abstract class LogFactory
/*     */ {
/*     */   public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";
/*     */   public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";
/*     */   public static final String FACTORY_PROPERTIES = "commons-logging.properties";
/*     */   protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
/* 228 */   protected static Hashtable factories = new Hashtable();
/*     */ 
/*     */   public abstract Object getAttribute(String paramString);
/*     */ 
/*     */   public abstract String[] getAttributeNames();
/*     */ 
/*     */   public abstract Log getInstance(Class paramClass)
/*     */     throws LogConfigurationException;
/*     */ 
/*     */   public abstract Log getInstance(String paramString)
/*     */     throws LogConfigurationException;
/*     */ 
/*     */   public abstract void release();
/*     */ 
/*     */   public abstract void removeAttribute(String paramString);
/*     */ 
/*     */   public abstract void setAttribute(String paramString, Object paramObject);
/*     */ 
/*     */   public static LogFactory getFactory()
/*     */     throws LogConfigurationException
/*     */   {
/* 262 */     ClassLoader contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 266 */         return LogFactory.getContextClassLoader();
/*     */       }
/*     */     });
/* 271 */     LogFactory factory = getCachedFactory(contextClassLoader);
/* 272 */     if (factory != null) {
/* 273 */       return factory;
/*     */     }
/*     */ 
/* 279 */     Properties props = null;
/*     */     try {
/* 281 */       InputStream stream = getResourceAsStream(contextClassLoader, "commons-logging.properties");
/*     */ 
/* 284 */       if (stream != null) {
/* 285 */         props = new Properties();
/* 286 */         props.load(stream);
/* 287 */         stream.close();
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     try {
/* 296 */       String factoryClass = System.getProperty("org.apache.commons.logging.LogFactory");
/* 297 */       if (factoryClass != null) {
/* 298 */         factory = newFactory(factoryClass, contextClassLoader);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/*     */     }
/*     */ 
/* 311 */     if (factory == null) {
/*     */       try {
/* 313 */         InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/org.apache.commons.logging.LogFactory");
/*     */ 
/* 316 */         if (is != null)
/*     */         {
/*     */           BufferedReader rd;
/*     */           try {
/* 321 */             rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */           } catch (UnsupportedEncodingException e) {
/* 323 */             rd = new BufferedReader(new InputStreamReader(is));
/*     */           }
/*     */ 
/* 326 */           String factoryClassName = rd.readLine();
/* 327 */           rd.close();
/*     */ 
/* 329 */           if ((factoryClassName != null) && (!"".equals(factoryClassName)))
/*     */           {
/* 332 */             factory = newFactory(factoryClassName, contextClassLoader);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Exception ex)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 349 */     if ((factory == null) && (props != null)) {
/* 350 */       String factoryClass = props.getProperty("org.apache.commons.logging.LogFactory");
/* 351 */       if (factoryClass != null) {
/* 352 */         factory = newFactory(factoryClass, contextClassLoader);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 359 */     if (factory == null) {
/* 360 */       factory = newFactory("org.apache.commons.logging.impl.LogFactoryImpl", LogFactory.class.getClassLoader());
/*     */     }
/*     */ 
/* 363 */     if (factory != null)
/*     */     {
/* 367 */       cacheFactory(contextClassLoader, factory);
/*     */ 
/* 369 */       if (props != null) {
/* 370 */         Enumeration names = props.propertyNames();
/* 371 */         while (names.hasMoreElements()) {
/* 372 */           String name = (String)names.nextElement();
/* 373 */           String value = props.getProperty(name);
/* 374 */           factory.setAttribute(name, value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 379 */     return factory;
/*     */   }
/*     */ 
/*     */   public static Log getLog(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/* 395 */     return getFactory().getInstance(clazz);
/*     */   }
/*     */ 
/*     */   public static Log getLog(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 414 */     return getFactory().getInstance(name);
/*     */   }
/*     */ 
/*     */   public static void release(ClassLoader classLoader)
/*     */   {
/* 429 */     synchronized (factories) {
/* 430 */       LogFactory factory = (LogFactory)factories.get(classLoader);
/* 431 */       if (factory != null) {
/* 432 */         factory.release();
/* 433 */         factories.remove(classLoader);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void releaseAll()
/*     */   {
/* 450 */     synchronized (factories) {
/* 451 */       Enumeration elements = factories.elements();
/* 452 */       while (elements.hasMoreElements()) {
/* 453 */         LogFactory element = (LogFactory)elements.nextElement();
/* 454 */         element.release();
/*     */       }
/* 456 */       factories.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static ClassLoader getContextClassLoader()
/*     */     throws LogConfigurationException
/*     */   {
/* 478 */     ClassLoader classLoader = null;
/*     */     try
/*     */     {
/* 482 */       Method method = Thread.class.getMethod("getContextClassLoader", null);
/*     */       try
/*     */       {
/* 486 */         classLoader = (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */       } catch (IllegalAccessException e) {
/* 488 */         throw new LogConfigurationException("Unexpected IllegalAccessException", e);
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/* 507 */         if (!(e.getTargetException() instanceof SecurityException))
/*     */         {
/* 512 */           throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 518 */       classLoader = LogFactory.class.getClassLoader();
/*     */     }
/*     */ 
/* 522 */     return classLoader;
/*     */   }
/*     */ 
/*     */   private static LogFactory getCachedFactory(ClassLoader contextClassLoader)
/*     */   {
/* 530 */     LogFactory factory = null;
/*     */ 
/* 532 */     if (contextClassLoader != null) {
/* 533 */       factory = (LogFactory)factories.get(contextClassLoader);
/*     */     }
/* 535 */     return factory;
/*     */   }
/*     */ 
/*     */   private static void cacheFactory(ClassLoader classLoader, LogFactory factory)
/*     */   {
/* 540 */     if ((classLoader != null) && (factory != null))
/* 541 */       factories.put(classLoader, factory);
/*     */   }
/*     */ 
/*     */   protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader)
/*     */     throws LogConfigurationException
/*     */   {
/* 561 */     Object result = AccessController.doPrivileged(new PrivilegedAction(classLoader, factoryClass) { private final ClassLoader val$classLoader;
/*     */       private final String val$factoryClass;
/*     */ 
/*     */       public Object run() { try { if (this.val$classLoader != null)
/*     */           {
/*     */             try
/*     */             {
/* 571 */               return (LogFactory)this.val$classLoader.loadClass(this.val$factoryClass).newInstance();
/*     */             } catch (ClassNotFoundException ex) {
/* 573 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 575 */                 throw ex;
/*     */               }
/*     */             }
/*     */             catch (NoClassDefFoundError e) {
/* 579 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 581 */                 throw e;
/*     */               }
/*     */             }
/*     */             catch (ClassCastException e)
/*     */             {
/* 586 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 588 */                 throw e;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 607 */           return (LogFactory)Class.forName(this.val$factoryClass).newInstance(); } catch (Exception e) {
/*     */         }
/* 609 */         return new LogConfigurationException(e);
/*     */       }
/*     */     });
/* 614 */     if ((result instanceof LogConfigurationException)) {
/* 615 */       throw ((LogConfigurationException)result);
/*     */     }
/* 617 */     return (LogFactory)result;
/*     */   }
/*     */ 
/*     */   private static InputStream getResourceAsStream(ClassLoader loader, String name)
/*     */   {
/* 623 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction(loader, name) { private final ClassLoader val$loader;
/*     */       private final String val$name;
/*     */ 
/* 626 */       public Object run() { if (this.val$loader != null) {
/* 627 */           return this.val$loader.getResourceAsStream(this.val$name);
/*     */         }
/* 629 */         return ClassLoader.getSystemResourceAsStream(this.val$name);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.LogFactory
 * JD-Core Version:    0.6.0
 */
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
/* 184 */   protected static Hashtable factories = new Hashtable();
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
/* 218 */     ClassLoader contextClassLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
/*     */     {
/*     */       public Object run()
/*     */       {
/* 222 */         return LogFactory.getContextClassLoader();
/*     */       }
/*     */     });
/* 227 */     LogFactory factory = getCachedFactory(contextClassLoader);
/* 228 */     if (factory != null) {
/* 229 */       return factory;
/*     */     }
/*     */ 
/* 235 */     Properties props = null;
/*     */     try {
/* 237 */       InputStream stream = getResourceAsStream(contextClassLoader, "commons-logging.properties");
/*     */ 
/* 240 */       if (stream != null) {
/* 241 */         props = new Properties();
/* 242 */         props.load(stream);
/* 243 */         stream.close();
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     try {
/* 252 */       String factoryClass = System.getProperty("org.apache.commons.logging.LogFactory");
/* 253 */       if (factoryClass != null) {
/* 254 */         factory = newFactory(factoryClass, contextClassLoader);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SecurityException e)
/*     */     {
/*     */     }
/*     */ 
/* 267 */     if (factory == null) {
/*     */       try {
/* 269 */         InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/org.apache.commons.logging.LogFactory");
/*     */ 
/* 272 */         if (is != null)
/*     */         {
/*     */           BufferedReader rd;
/*     */           try {
/* 277 */             rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */           } catch (UnsupportedEncodingException e) {
/* 279 */             rd = new BufferedReader(new InputStreamReader(is));
/*     */           }
/*     */ 
/* 282 */           String factoryClassName = rd.readLine();
/* 283 */           rd.close();
/*     */ 
/* 285 */           if ((factoryClassName != null) && (!"".equals(factoryClassName)))
/*     */           {
/* 288 */             factory = newFactory(factoryClassName, contextClassLoader);
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
/* 305 */     if ((factory == null) && (props != null)) {
/* 306 */       String factoryClass = props.getProperty("org.apache.commons.logging.LogFactory");
/* 307 */       if (factoryClass != null) {
/* 308 */         factory = newFactory(factoryClass, contextClassLoader);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 315 */     if (factory == null) {
/* 316 */       factory = newFactory("org.apache.commons.logging.impl.LogFactoryImpl", LogFactory.class.getClassLoader());
/*     */     }
/*     */ 
/* 319 */     if (factory != null)
/*     */     {
/* 323 */       cacheFactory(contextClassLoader, factory);
/*     */ 
/* 325 */       if (props != null) {
/* 326 */         Enumeration names = props.propertyNames();
/* 327 */         while (names.hasMoreElements()) {
/* 328 */           String name = (String)names.nextElement();
/* 329 */           String value = props.getProperty(name);
/* 330 */           factory.setAttribute(name, value);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 335 */     return factory;
/*     */   }
/*     */ 
/*     */   public static Log getLog(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/* 351 */     return getFactory().getInstance(clazz);
/*     */   }
/*     */ 
/*     */   public static Log getLog(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 370 */     return getFactory().getInstance(name);
/*     */   }
/*     */ 
/*     */   public static void release(ClassLoader classLoader)
/*     */   {
/* 385 */     synchronized (factories) {
/* 386 */       LogFactory factory = (LogFactory)factories.get(classLoader);
/* 387 */       if (factory != null) {
/* 388 */         factory.release();
/* 389 */         factories.remove(classLoader);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void releaseAll()
/*     */   {
/* 406 */     synchronized (factories) {
/* 407 */       Enumeration elements = factories.elements();
/* 408 */       while (elements.hasMoreElements()) {
/* 409 */         LogFactory element = (LogFactory)elements.nextElement();
/* 410 */         element.release();
/*     */       }
/* 412 */       factories.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static ClassLoader getContextClassLoader()
/*     */     throws LogConfigurationException
/*     */   {
/* 434 */     ClassLoader classLoader = null;
/*     */     try
/*     */     {
/* 438 */       Method method = Thread.class.getMethod("getContextClassLoader", null);
/*     */       try
/*     */       {
/* 442 */         classLoader = (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */       } catch (IllegalAccessException e) {
/* 444 */         throw new LogConfigurationException("Unexpected IllegalAccessException", e);
/*     */       }
/*     */       catch (InvocationTargetException e)
/*     */       {
/* 463 */         if (!(e.getTargetException() instanceof SecurityException))
/*     */         {
/* 468 */           throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/* 474 */       classLoader = LogFactory.class.getClassLoader();
/*     */     }
/*     */ 
/* 478 */     return classLoader;
/*     */   }
/*     */ 
/*     */   private static LogFactory getCachedFactory(ClassLoader contextClassLoader)
/*     */   {
/* 486 */     LogFactory factory = null;
/*     */ 
/* 488 */     if (contextClassLoader != null) {
/* 489 */       factory = (LogFactory)factories.get(contextClassLoader);
/*     */     }
/* 491 */     return factory;
/*     */   }
/*     */ 
/*     */   private static void cacheFactory(ClassLoader classLoader, LogFactory factory)
/*     */   {
/* 496 */     if ((classLoader != null) && (factory != null))
/* 497 */       factories.put(classLoader, factory);
/*     */   }
/*     */ 
/*     */   protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader)
/*     */     throws LogConfigurationException
/*     */   {
/* 517 */     Object result = AccessController.doPrivileged(new PrivilegedAction(classLoader, factoryClass) { private final ClassLoader val$classLoader;
/*     */       private final String val$factoryClass;
/*     */ 
/* 522 */       public Object run() { Class logFactoryClass = null;
/*     */         try {
/* 524 */           if (this.val$classLoader != null)
/*     */           {
/*     */             try
/*     */             {
/* 530 */               logFactoryClass = this.val$classLoader.loadClass(this.val$factoryClass);
/* 531 */               return (LogFactory)logFactoryClass.newInstance();
/*     */             }
/*     */             catch (ClassNotFoundException ex) {
/* 534 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 536 */                 throw ex;
/*     */               }
/*     */             }
/*     */             catch (NoClassDefFoundError e) {
/* 540 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 542 */                 throw e;
/*     */               }
/*     */             }
/*     */             catch (ClassCastException e)
/*     */             {
/* 547 */               if (this.val$classLoader == LogFactory.class.getClassLoader())
/*     */               {
/* 549 */                 throw e;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 568 */           logFactoryClass = Class.forName(this.val$factoryClass);
/* 569 */           return (LogFactory)logFactoryClass.newInstance();
/*     */         }
/*     */         catch (Exception e) {
/* 572 */           if ((logFactoryClass != null) && (!LogFactory.class.isAssignableFrom(logFactoryClass)))
/*     */           {
/* 574 */             return new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", e);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 579 */         return new LogConfigurationException(e);
/*     */       }
/*     */     });
/* 584 */     if ((result instanceof LogConfigurationException)) {
/* 585 */       throw ((LogConfigurationException)result);
/*     */     }
/* 587 */     return (LogFactory)result;
/*     */   }
/*     */ 
/*     */   private static InputStream getResourceAsStream(ClassLoader loader, String name)
/*     */   {
/* 593 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction(loader, name) { private final ClassLoader val$loader;
/*     */       private final String val$name;
/*     */ 
/* 596 */       public Object run() { if (this.val$loader != null) {
/* 597 */           return this.val$loader.getResourceAsStream(this.val$name);
/*     */         }
/* 599 */         return ClassLoader.getSystemResourceAsStream(this.val$name);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.LogFactory
 * JD-Core Version:    0.6.0
 */
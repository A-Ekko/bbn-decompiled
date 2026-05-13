/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogConfigurationException;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class LogFactoryImpl extends LogFactory
/*     */ {
/*     */   public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
/*     */   protected static final String LOG_PROPERTY_OLD = "org.apache.commons.logging.log";
/* 151 */   protected Hashtable attributes = new Hashtable();
/*     */ 
/* 158 */   protected Hashtable instances = new Hashtable();
/*     */   private String logClassName;
/* 174 */   protected Constructor logConstructor = null;
/*     */ 
/* 180 */   protected Class[] logConstructorSignature = { String.class };
/*     */ 
/* 188 */   protected Method logMethod = null;
/*     */ 
/* 194 */   protected Class[] logMethodSignature = { LogFactory.class };
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/* 209 */     return this.attributes.get(name);
/*     */   }
/*     */ 
/*     */   public String[] getAttributeNames()
/*     */   {
/* 221 */     Vector names = new Vector();
/* 222 */     Enumeration keys = this.attributes.keys();
/* 223 */     while (keys.hasMoreElements()) {
/* 224 */       names.addElement((String)keys.nextElement());
/*     */     }
/* 226 */     String[] results = new String[names.size()];
/* 227 */     for (int i = 0; i < results.length; i++) {
/* 228 */       results[i] = ((String)names.elementAt(i));
/*     */     }
/* 230 */     return results;
/*     */   }
/*     */ 
/*     */   public Log getInstance(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/* 246 */     return getInstance(clazz.getName());
/*     */   }
/*     */ 
/*     */   public Log getInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 270 */     Log instance = (Log)this.instances.get(name);
/* 271 */     if (instance == null) {
/* 272 */       instance = newInstance(name);
/* 273 */       this.instances.put(name, instance);
/*     */     }
/* 275 */     return instance;
/*     */   }
/*     */ 
/*     */   public void release()
/*     */   {
/* 290 */     this.instances.clear();
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String name)
/*     */   {
/* 302 */     this.attributes.remove(name);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String name, Object value)
/*     */   {
/* 318 */     if (value == null)
/* 319 */       this.attributes.remove(name);
/*     */     else
/* 321 */       this.attributes.put(name, value);
/*     */   }
/*     */ 
/*     */   protected String getLogClassName()
/*     */   {
/* 338 */     if (this.logClassName != null) {
/* 339 */       return this.logClassName;
/*     */     }
/*     */ 
/* 342 */     this.logClassName = ((String)getAttribute("org.apache.commons.logging.Log"));
/*     */ 
/* 344 */     if (this.logClassName == null) {
/* 345 */       this.logClassName = ((String)getAttribute("org.apache.commons.logging.log"));
/*     */     }
/*     */ 
/* 348 */     if (this.logClassName == null) {
/*     */       try {
/* 350 */         this.logClassName = System.getProperty("org.apache.commons.logging.Log");
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*     */       }
/*     */     }
/* 356 */     if (this.logClassName == null) {
/*     */       try {
/* 358 */         this.logClassName = System.getProperty("org.apache.commons.logging.log");
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*     */       }
/*     */     }
/* 364 */     if ((this.logClassName == null) && (isLog4JAvailable())) {
/* 365 */       this.logClassName = "org.apache.commons.logging.impl.Log4JLogger";
/*     */     }
/*     */ 
/* 368 */     if ((this.logClassName == null) && (isJdk14Available())) {
/* 369 */       this.logClassName = "org.apache.commons.logging.impl.Jdk14Logger";
/*     */     }
/*     */ 
/* 372 */     if (this.logClassName == null) {
/* 373 */       this.logClassName = "org.apache.commons.logging.impl.SimpleLog";
/*     */     }
/*     */ 
/* 376 */     return this.logClassName;
/*     */   }
/*     */ 
/*     */   protected Constructor getLogConstructor()
/*     */     throws LogConfigurationException
/*     */   {
/* 397 */     if (this.logConstructor != null) {
/* 398 */       return this.logConstructor;
/*     */     }
/*     */ 
/* 401 */     String logClassName = getLogClassName();
/*     */ 
/* 404 */     Class logClass = null;
/*     */     try {
/* 406 */       logClass = loadClass(logClassName);
/* 407 */       if (logClass == null) {
/* 408 */         throw new LogConfigurationException("No suitable Log implementation for " + logClassName);
/*     */       }
/*     */ 
/* 411 */       if (!Log.class.isAssignableFrom(logClass))
/* 412 */         throw new LogConfigurationException("Class " + logClassName + " does not implement Log");
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 416 */       throw new LogConfigurationException(t);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 421 */       this.logMethod = logClass.getMethod("setLogFactory", this.logMethodSignature);
/*     */     }
/*     */     catch (Throwable t) {
/* 424 */       this.logMethod = null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 429 */       this.logConstructor = logClass.getConstructor(this.logConstructorSignature);
/* 430 */       return this.logConstructor; } catch (Throwable t) {
/*     */     }
/* 432 */     throw new LogConfigurationException("No suitable Log constructor " + this.logConstructorSignature + " for " + logClassName, t);
/*     */   }
/*     */ 
/*     */   private static Class loadClass(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/* 454 */     Object result = AccessController.doPrivileged(new PrivilegedAction(name) { private final String val$name;
/*     */ 
/* 457 */       public Object run() { ClassLoader threadCL = LogFactoryImpl.access$000();
/* 458 */         if (threadCL != null)
/*     */           try {
/* 460 */             return threadCL.loadClass(this.val$name);
/*     */           }
/*     */           catch (ClassNotFoundException ex)
/*     */           {
/*     */           }
/*     */         try {
/* 466 */           return Class.forName(this.val$name); } catch (ClassNotFoundException e) {
/*     */         }
/* 468 */         return e;
/*     */       }
/*     */     });
/* 473 */     if ((result instanceof Class)) {
/* 474 */       return (Class)result;
/*     */     }
/* 476 */     throw ((ClassNotFoundException)result);
/*     */   }
/*     */ 
/*     */   protected boolean isJdk14Available()
/*     */   {
/*     */     try
/*     */     {
/* 486 */       loadClass("java.util.logging.Logger");
/* 487 */       loadClass("org.apache.commons.logging.impl.Jdk14Logger");
/* 488 */       return true; } catch (Throwable t) {
/*     */     }
/* 490 */     return false;
/*     */   }
/*     */ 
/*     */   protected boolean isLog4JAvailable()
/*     */   {
/*     */     try
/*     */     {
/* 501 */       loadClass("org.apache.log4j.Logger");
/* 502 */       loadClass("org.apache.commons.logging.impl.Log4JLogger");
/* 503 */       return true; } catch (Throwable t) {
/*     */     }
/* 505 */     return false;
/*     */   }
/*     */ 
/*     */   protected Log newInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 521 */     Log instance = null;
/*     */     try {
/* 523 */       Object[] params = new Object[1];
/* 524 */       params[0] = name;
/* 525 */       instance = (Log)getLogConstructor().newInstance(params);
/* 526 */       if (this.logMethod != null) {
/* 527 */         params[0] = this;
/* 528 */         this.logMethod.invoke(instance, params);
/*     */       }
/* 530 */       return instance; } catch (Throwable t) {
/*     */     }
/* 532 */     throw new LogConfigurationException(t);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.LogFactoryImpl
 * JD-Core Version:    0.6.0
 */
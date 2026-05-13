/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
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
/*     */   private static final String LOG_INTERFACE = "org.apache.commons.logging.Log";
/* 114 */   protected Hashtable attributes = new Hashtable();
/*     */ 
/* 121 */   protected Hashtable instances = new Hashtable();
/*     */   private String logClassName;
/* 137 */   protected Constructor logConstructor = null;
/*     */ 
/* 143 */   protected Class[] logConstructorSignature = { String.class };
/*     */ 
/* 151 */   protected Method logMethod = null;
/*     */ 
/* 157 */   protected Class[] logMethodSignature = { LogFactory.class };
/*     */ 
/*     */   public Object getAttribute(String name)
/*     */   {
/* 172 */     return this.attributes.get(name);
/*     */   }
/*     */ 
/*     */   public String[] getAttributeNames()
/*     */   {
/* 184 */     Vector names = new Vector();
/* 185 */     Enumeration keys = this.attributes.keys();
/* 186 */     while (keys.hasMoreElements()) {
/* 187 */       names.addElement((String)keys.nextElement());
/*     */     }
/* 189 */     String[] results = new String[names.size()];
/* 190 */     for (int i = 0; i < results.length; i++) {
/* 191 */       results[i] = ((String)names.elementAt(i));
/*     */     }
/* 193 */     return results;
/*     */   }
/*     */ 
/*     */   public Log getInstance(Class clazz)
/*     */     throws LogConfigurationException
/*     */   {
/* 209 */     return getInstance(clazz.getName());
/*     */   }
/*     */ 
/*     */   public Log getInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 233 */     Log instance = (Log)this.instances.get(name);
/* 234 */     if (instance == null) {
/* 235 */       instance = newInstance(name);
/* 236 */       this.instances.put(name, instance);
/*     */     }
/* 238 */     return instance;
/*     */   }
/*     */ 
/*     */   public void release()
/*     */   {
/* 253 */     this.instances.clear();
/*     */   }
/*     */ 
/*     */   public void removeAttribute(String name)
/*     */   {
/* 265 */     this.attributes.remove(name);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String name, Object value)
/*     */   {
/* 281 */     if (value == null)
/* 282 */       this.attributes.remove(name);
/*     */     else
/* 284 */       this.attributes.put(name, value);
/*     */   }
/*     */ 
/*     */   protected String getLogClassName()
/*     */   {
/* 301 */     if (this.logClassName != null) {
/* 302 */       return this.logClassName;
/*     */     }
/*     */ 
/* 305 */     this.logClassName = ((String)getAttribute("org.apache.commons.logging.Log"));
/*     */ 
/* 307 */     if (this.logClassName == null) {
/* 308 */       this.logClassName = ((String)getAttribute("org.apache.commons.logging.log"));
/*     */     }
/*     */ 
/* 311 */     if (this.logClassName == null) {
/*     */       try {
/* 313 */         this.logClassName = System.getProperty("org.apache.commons.logging.Log");
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*     */       }
/*     */     }
/* 319 */     if (this.logClassName == null) {
/*     */       try {
/* 321 */         this.logClassName = System.getProperty("org.apache.commons.logging.log");
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/*     */       }
/*     */     }
/* 327 */     if ((this.logClassName == null) && (isLog4JAvailable())) {
/* 328 */       this.logClassName = "org.apache.commons.logging.impl.Log4JLogger";
/*     */     }
/*     */ 
/* 331 */     if ((this.logClassName == null) && (isJdk14Available())) {
/* 332 */       this.logClassName = "org.apache.commons.logging.impl.Jdk14Logger";
/*     */     }
/*     */ 
/* 335 */     if ((this.logClassName == null) && (isJdk13LumberjackAvailable())) {
/* 336 */       this.logClassName = "org.apache.commons.logging.impl.Jdk13LumberjackLogger";
/*     */     }
/*     */ 
/* 339 */     if (this.logClassName == null) {
/* 340 */       this.logClassName = "org.apache.commons.logging.impl.SimpleLog";
/*     */     }
/*     */ 
/* 343 */     return this.logClassName;
/*     */   }
/*     */ 
/*     */   protected Constructor getLogConstructor()
/*     */     throws LogConfigurationException
/*     */   {
/* 364 */     if (this.logConstructor != null) {
/* 365 */       return this.logConstructor;
/*     */     }
/*     */ 
/* 368 */     String logClassName = getLogClassName();
/*     */ 
/* 371 */     Class logClass = null;
/* 372 */     Class logInterface = null;
/*     */     try {
/* 374 */       logInterface = getClass().getClassLoader().loadClass("org.apache.commons.logging.Log");
/*     */ 
/* 376 */       logClass = loadClass(logClassName);
/* 377 */       if (logClass == null) {
/* 378 */         throw new LogConfigurationException("No suitable Log implementation for " + logClassName);
/*     */       }
/*     */ 
/* 381 */       if (!logInterface.isAssignableFrom(logClass)) {
/* 382 */         Class[] interfaces = logClass.getInterfaces();
/* 383 */         for (int i = 0; i < interfaces.length; i++) {
/* 384 */           if ("org.apache.commons.logging.Log".equals(interfaces[i].getName())) {
/* 385 */             throw new LogConfigurationException("Invalid class loader hierarchy.  You have more than one version of 'org.apache.commons.logging.Log' visible, which is not allowed.");
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 392 */         throw new LogConfigurationException("Class " + logClassName + " does not implement '" + "org.apache.commons.logging.Log" + "'.");
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 397 */       throw new LogConfigurationException(t);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 402 */       this.logMethod = logClass.getMethod("setLogFactory", this.logMethodSignature);
/*     */     }
/*     */     catch (Throwable t) {
/* 405 */       this.logMethod = null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 410 */       this.logConstructor = logClass.getConstructor(this.logConstructorSignature);
/* 411 */       return this.logConstructor; } catch (Throwable t) {
/*     */     }
/* 413 */     throw new LogConfigurationException("No suitable Log constructor " + this.logConstructorSignature + " for " + logClassName, t);
/*     */   }
/*     */ 
/*     */   private static Class loadClass(String name)
/*     */     throws ClassNotFoundException
/*     */   {
/* 435 */     Object result = AccessController.doPrivileged(new PrivilegedAction(name) { private final String val$name;
/*     */ 
/* 438 */       public Object run() { ClassLoader threadCL = LogFactoryImpl.access$001();
/* 439 */         if (threadCL != null)
/*     */           try {
/* 441 */             return threadCL.loadClass(this.val$name);
/*     */           }
/*     */           catch (ClassNotFoundException ex)
/*     */           {
/*     */           }
/*     */         try {
/* 447 */           return Class.forName(this.val$name); } catch (ClassNotFoundException e) {
/*     */         }
/* 449 */         return e;
/*     */       }
/*     */     });
/* 454 */     if ((result instanceof Class)) {
/* 455 */       return (Class)result;
/*     */     }
/* 457 */     throw ((ClassNotFoundException)result);
/*     */   }
/*     */ 
/*     */   protected boolean isJdk13LumberjackAvailable()
/*     */   {
/*     */     try
/*     */     {
/* 467 */       loadClass("java.util.logging.Logger");
/* 468 */       loadClass("org.apache.commons.logging.impl.Jdk13LumberjackLogger");
/* 469 */       return true; } catch (Throwable t) {
/*     */     }
/* 471 */     return false;
/*     */   }
/*     */ 
/*     */   protected boolean isJdk14Available()
/*     */   {
/*     */     try
/*     */     {
/* 486 */       loadClass("java.util.logging.Logger");
/* 487 */       loadClass("org.apache.commons.logging.impl.Jdk14Logger");
/* 488 */       Class throwable = loadClass("java.lang.Throwable");
/*     */ 
/* 490 */       return throwable.getDeclaredMethod("getStackTrace", null) != null;
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */     }
/* 494 */     return false;
/*     */   }
/*     */ 
/*     */   protected boolean isLog4JAvailable()
/*     */   {
/*     */     try
/*     */     {
/* 505 */       loadClass("org.apache.log4j.Logger");
/* 506 */       loadClass("org.apache.commons.logging.impl.Log4JLogger");
/* 507 */       return true; } catch (Throwable t) {
/*     */     }
/* 509 */     return false;
/*     */   }
/*     */ 
/*     */   protected Log newInstance(String name)
/*     */     throws LogConfigurationException
/*     */   {
/* 525 */     Log instance = null;
/*     */     try {
/* 527 */       Object[] params = new Object[1];
/* 528 */       params[0] = name;
/* 529 */       instance = (Log)getLogConstructor().newInstance(params);
/* 530 */       if (this.logMethod != null) {
/* 531 */         params[0] = this;
/* 532 */         this.logMethod.invoke(instance, params);
/*     */       }
/* 534 */       return instance;
/*     */     } catch (InvocationTargetException e) {
/* 536 */       Throwable c = e.getTargetException();
/* 537 */       if (c != null) {
/* 538 */         throw new LogConfigurationException(c);
/*     */       }
/* 540 */       throw new LogConfigurationException(e);
/*     */     } catch (Throwable t) {
/*     */     }
/* 543 */     throw new LogConfigurationException(t);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.LogFactoryImpl
 * JD-Core Version:    0.6.0
 */
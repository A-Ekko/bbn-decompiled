/*     */ package org.apache.commons.logging;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.impl.NoOpLog;
/*     */ 
/*     */ /** @deprecated */
/*     */ public class LogSource
/*     */ {
/* 106 */   protected static Hashtable logs = new Hashtable();
/*     */ 
/* 109 */   protected static boolean log4jIsAvailable = false;
/*     */ 
/* 112 */   protected static boolean jdk14IsAvailable = false;
/*     */ 
/* 115 */   protected static Constructor logImplctor = null;
/*     */ 
/*     */   public static void setLogImplementation(String classname)
/*     */     throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException, ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 213 */       Class logclass = Class.forName(classname);
/* 214 */       Class[] argtypes = new Class[1];
/* 215 */       argtypes[0] = "".getClass();
/* 216 */       logImplctor = logclass.getConstructor(argtypes);
/*     */     } catch (Throwable t) {
/* 218 */       logImplctor = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setLogImplementation(Class logclass)
/*     */     throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException
/*     */   {
/* 232 */     Class[] argtypes = new Class[1];
/* 233 */     argtypes[0] = "".getClass();
/* 234 */     logImplctor = logclass.getConstructor(argtypes);
/*     */   }
/*     */ 
/*     */   public static Log getInstance(String name)
/*     */   {
/* 240 */     Log log = (Log)logs.get(name);
/* 241 */     if (null == log) {
/* 242 */       log = makeNewLogInstance(name);
/* 243 */       logs.put(name, log);
/*     */     }
/* 245 */     return log;
/*     */   }
/*     */ 
/*     */   public static Log getInstance(Class clazz)
/*     */   {
/* 251 */     return getInstance(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Log makeNewLogInstance(String name)
/*     */   {
/* 281 */     Log log = null;
/*     */     try {
/* 283 */       Object[] args = new Object[1];
/* 284 */       args[0] = name;
/* 285 */       log = (Log)logImplctor.newInstance(args);
/*     */     } catch (Throwable t) {
/* 287 */       log = null;
/*     */     }
/* 289 */     if (null == log) {
/* 290 */       log = new NoOpLog(name);
/*     */     }
/* 292 */     return log;
/*     */   }
/*     */ 
/*     */   public static String[] getLogNames()
/*     */   {
/* 302 */     return (String[])logs.keySet().toArray(new String[logs.size()]);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 124 */       if (null != Class.forName("org.apache.log4j.Logger"))
/* 125 */         log4jIsAvailable = true;
/*     */       else
/* 127 */         log4jIsAvailable = false;
/*     */     }
/*     */     catch (Throwable t) {
/* 130 */       log4jIsAvailable = false;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 135 */       if ((null != Class.forName("java.util.logging.Logger")) && (null != Class.forName("org.apache.commons.logging.impl.Jdk14Logger")))
/*     */       {
/* 137 */         jdk14IsAvailable = true;
/*     */       }
/* 139 */       else jdk14IsAvailable = false; 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 142 */       jdk14IsAvailable = false;
/*     */     }
/*     */ 
/* 146 */     String name = null;
/*     */     try {
/* 148 */       name = System.getProperty("org.apache.commons.logging.log");
/* 149 */       if (name == null)
/* 150 */         name = System.getProperty("org.apache.commons.logging.Log");
/*     */     }
/*     */     catch (Throwable t) {
/*     */     }
/* 154 */     if (name != null)
/*     */       try {
/* 156 */         setLogImplementation(name);
/*     */       } catch (Throwable t) {
/*     */         try {
/* 159 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */         }
/*     */         catch (Throwable u)
/*     */         {
/*     */         }
/*     */       }
/*     */     else
/*     */       try {
/* 167 */         if (log4jIsAvailable) {
/* 168 */           setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
/*     */         }
/* 170 */         else if (jdk14IsAvailable) {
/* 171 */           setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
/*     */         }
/*     */         else
/* 174 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */         try {
/* 179 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */         }
/*     */         catch (Throwable u)
/*     */         {
/*     */         }
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.LogSource
 * JD-Core Version:    0.6.0
 */
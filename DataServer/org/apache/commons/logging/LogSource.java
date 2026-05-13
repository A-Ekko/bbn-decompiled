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
/*  61 */   protected static Hashtable logs = new Hashtable();
/*     */ 
/*  64 */   protected static boolean log4jIsAvailable = false;
/*     */ 
/*  67 */   protected static boolean jdk14IsAvailable = false;
/*     */ 
/*  70 */   protected static Constructor logImplctor = null;
/*     */ 
/*     */   public static void setLogImplementation(String classname)
/*     */     throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException, ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 168 */       Class logclass = Class.forName(classname);
/* 169 */       Class[] argtypes = new Class[1];
/* 170 */       argtypes[0] = "".getClass();
/* 171 */       logImplctor = logclass.getConstructor(argtypes);
/*     */     } catch (Throwable t) {
/* 173 */       logImplctor = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void setLogImplementation(Class logclass)
/*     */     throws LinkageError, ExceptionInInitializerError, NoSuchMethodException, SecurityException
/*     */   {
/* 187 */     Class[] argtypes = new Class[1];
/* 188 */     argtypes[0] = "".getClass();
/* 189 */     logImplctor = logclass.getConstructor(argtypes);
/*     */   }
/*     */ 
/*     */   public static Log getInstance(String name)
/*     */   {
/* 195 */     Log log = (Log)logs.get(name);
/* 196 */     if (null == log) {
/* 197 */       log = makeNewLogInstance(name);
/* 198 */       logs.put(name, log);
/*     */     }
/* 200 */     return log;
/*     */   }
/*     */ 
/*     */   public static Log getInstance(Class clazz)
/*     */   {
/* 206 */     return getInstance(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Log makeNewLogInstance(String name)
/*     */   {
/* 236 */     Log log = null;
/*     */     try {
/* 238 */       Object[] args = new Object[1];
/* 239 */       args[0] = name;
/* 240 */       log = (Log)logImplctor.newInstance(args);
/*     */     } catch (Throwable t) {
/* 242 */       log = null;
/*     */     }
/* 244 */     if (null == log) {
/* 245 */       log = new NoOpLog(name);
/*     */     }
/* 247 */     return log;
/*     */   }
/*     */ 
/*     */   public static String[] getLogNames()
/*     */   {
/* 257 */     return (String[])logs.keySet().toArray(new String[logs.size()]);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  79 */       if (null != Class.forName("org.apache.log4j.Logger"))
/*  80 */         log4jIsAvailable = true;
/*     */       else
/*  82 */         log4jIsAvailable = false;
/*     */     }
/*     */     catch (Throwable t) {
/*  85 */       log4jIsAvailable = false;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  90 */       if ((null != Class.forName("java.util.logging.Logger")) && (null != Class.forName("org.apache.commons.logging.impl.Jdk14Logger")))
/*     */       {
/*  92 */         jdk14IsAvailable = true;
/*     */       }
/*  94 */       else jdk14IsAvailable = false; 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*  97 */       jdk14IsAvailable = false;
/*     */     }
/*     */ 
/* 101 */     String name = null;
/*     */     try {
/* 103 */       name = System.getProperty("org.apache.commons.logging.log");
/* 104 */       if (name == null)
/* 105 */         name = System.getProperty("org.apache.commons.logging.Log");
/*     */     }
/*     */     catch (Throwable t) {
/*     */     }
/* 109 */     if (name != null)
/*     */       try {
/* 111 */         setLogImplementation(name);
/*     */       } catch (Throwable t) {
/*     */         try {
/* 114 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */         }
/*     */         catch (Throwable u)
/*     */         {
/*     */         }
/*     */       }
/*     */     else
/*     */       try {
/* 122 */         if (log4jIsAvailable) {
/* 123 */           setLogImplementation("org.apache.commons.logging.impl.Log4JLogger");
/*     */         }
/* 125 */         else if (jdk14IsAvailable) {
/* 126 */           setLogImplementation("org.apache.commons.logging.impl.Jdk14Logger");
/*     */         }
/*     */         else
/* 129 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */         try {
/* 134 */           setLogImplementation("org.apache.commons.logging.impl.NoOpLog");
/*     */         }
/*     */         catch (Throwable u)
/*     */         {
/*     */         }
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.LogSource
 * JD-Core Version:    0.6.0
 */
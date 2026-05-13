/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogConfigurationException;
/*     */ 
/*     */ public class SimpleLog
/*     */   implements Log
/*     */ {
/*     */   protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
/* 123 */   protected static final Properties simpleLogProps = new Properties();
/*     */ 
/* 126 */   protected static boolean showLogName = false;
/*     */ 
/* 131 */   protected static boolean showShortName = true;
/*     */ 
/* 133 */   protected static boolean showDateTime = false;
/*     */ 
/* 135 */   protected static DateFormat dateFormatter = null;
/*     */   public static final int LOG_LEVEL_TRACE = 1;
/*     */   public static final int LOG_LEVEL_DEBUG = 2;
/*     */   public static final int LOG_LEVEL_INFO = 3;
/*     */   public static final int LOG_LEVEL_WARN = 4;
/*     */   public static final int LOG_LEVEL_ERROR = 5;
/*     */   public static final int LOG_LEVEL_FATAL = 6;
/*     */   public static final int LOG_LEVEL_ALL = 0;
/*     */   public static final int LOG_LEVEL_OFF = 7;
/* 207 */   protected String logName = null;
/*     */   protected int currentLogLevel;
/* 211 */   private String prefix = null;
/*     */ 
/*     */   private static String getStringProperty(String name)
/*     */   {
/* 162 */     String prop = System.getProperty(name);
/* 163 */     return prop == null ? simpleLogProps.getProperty(name) : prop;
/*     */   }
/*     */ 
/*     */   private static String getStringProperty(String name, String dephault) {
/* 167 */     String prop = getStringProperty(name);
/* 168 */     return prop == null ? dephault : prop;
/*     */   }
/*     */ 
/*     */   private static boolean getBooleanProperty(String name, boolean dephault) {
/* 172 */     String prop = getStringProperty(name);
/* 173 */     return prop == null ? dephault : "true".equalsIgnoreCase(prop);
/*     */   }
/*     */ 
/*     */   public SimpleLog(String name)
/*     */   {
/* 223 */     this.logName = name;
/*     */ 
/* 228 */     setLevel(3);
/*     */ 
/* 231 */     String lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);
/* 232 */     int i = String.valueOf(name).lastIndexOf(".");
/* 233 */     while ((null == lvl) && (i > -1)) {
/* 234 */       name = name.substring(0, i);
/* 235 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + name);
/* 236 */       i = String.valueOf(name).lastIndexOf(".");
/*     */     }
/*     */ 
/* 239 */     if (null == lvl) {
/* 240 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
/*     */     }
/*     */ 
/* 243 */     if ("all".equalsIgnoreCase(lvl))
/* 244 */       setLevel(0);
/* 245 */     else if ("trace".equalsIgnoreCase(lvl))
/* 246 */       setLevel(1);
/* 247 */     else if ("debug".equalsIgnoreCase(lvl))
/* 248 */       setLevel(2);
/* 249 */     else if ("info".equalsIgnoreCase(lvl))
/* 250 */       setLevel(3);
/* 251 */     else if ("warn".equalsIgnoreCase(lvl))
/* 252 */       setLevel(4);
/* 253 */     else if ("error".equalsIgnoreCase(lvl))
/* 254 */       setLevel(5);
/* 255 */     else if ("fatal".equalsIgnoreCase(lvl))
/* 256 */       setLevel(6);
/* 257 */     else if ("off".equalsIgnoreCase(lvl))
/* 258 */       setLevel(7);
/*     */   }
/*     */ 
/*     */   public void setLevel(int currentLogLevel)
/*     */   {
/* 273 */     this.currentLogLevel = currentLogLevel;
/*     */   }
/*     */ 
/*     */   public int getLevel()
/*     */   {
/* 283 */     return this.currentLogLevel;
/*     */   }
/*     */ 
/*     */   protected void log(int type, Object message, Throwable t)
/*     */   {
/* 297 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 300 */     if (showDateTime) {
/* 301 */       buf.append(dateFormatter.format(new Date()));
/* 302 */       buf.append(" ");
/*     */     }
/*     */ 
/* 306 */     switch (type) { case 1:
/* 307 */       buf.append("[TRACE] "); break;
/*     */     case 2:
/* 308 */       buf.append("[DEBUG] "); break;
/*     */     case 3:
/* 309 */       buf.append("[INFO] "); break;
/*     */     case 4:
/* 310 */       buf.append("[WARN] "); break;
/*     */     case 5:
/* 311 */       buf.append("[ERROR] "); break;
/*     */     case 6:
/* 312 */       buf.append("[FATAL] ");
/*     */     }
/*     */ 
/* 316 */     if (showShortName) {
/* 317 */       if (this.prefix == null)
/*     */       {
/* 319 */         this.prefix = (this.logName.substring(this.logName.lastIndexOf(".") + 1) + " - ");
/* 320 */         this.prefix = (this.prefix.substring(this.prefix.lastIndexOf("/") + 1) + "-");
/*     */       }
/* 322 */       buf.append(this.prefix);
/* 323 */     } else if (showLogName) {
/* 324 */       buf.append(String.valueOf(this.logName)).append(" - ");
/*     */     }
/*     */ 
/* 328 */     buf.append(String.valueOf(message));
/*     */ 
/* 331 */     if (t != null) {
/* 332 */       buf.append(" <");
/* 333 */       buf.append(t.toString());
/* 334 */       buf.append(">");
/*     */ 
/* 336 */       StringWriter sw = new StringWriter(1024);
/* 337 */       PrintWriter pw = new PrintWriter(sw);
/* 338 */       t.printStackTrace(pw);
/* 339 */       pw.close();
/* 340 */       buf.append(sw.toString());
/*     */     }
/*     */ 
/* 344 */     System.err.println(buf.toString());
/*     */   }
/*     */ 
/*     */   protected boolean isLevelEnabled(int logLevel)
/*     */   {
/* 356 */     return logLevel >= this.currentLogLevel;
/*     */   }
/*     */ 
/*     */   public final void debug(Object message)
/*     */   {
/* 368 */     if (isLevelEnabled(2))
/* 369 */       log(2, message, null);
/*     */   }
/*     */ 
/*     */   public final void debug(Object message, Throwable t)
/*     */   {
/* 379 */     if (isLevelEnabled(2))
/* 380 */       log(2, message, t);
/*     */   }
/*     */ 
/*     */   public final void trace(Object message)
/*     */   {
/* 390 */     if (isLevelEnabled(1))
/* 391 */       log(1, message, null);
/*     */   }
/*     */ 
/*     */   public final void trace(Object message, Throwable t)
/*     */   {
/* 401 */     if (isLevelEnabled(1))
/* 402 */       log(1, message, t);
/*     */   }
/*     */ 
/*     */   public final void info(Object message)
/*     */   {
/* 412 */     if (isLevelEnabled(3))
/* 413 */       log(3, message, null);
/*     */   }
/*     */ 
/*     */   public final void info(Object message, Throwable t)
/*     */   {
/* 423 */     if (isLevelEnabled(3))
/* 424 */       log(3, message, t);
/*     */   }
/*     */ 
/*     */   public final void warn(Object message)
/*     */   {
/* 434 */     if (isLevelEnabled(4))
/* 435 */       log(4, message, null);
/*     */   }
/*     */ 
/*     */   public final void warn(Object message, Throwable t)
/*     */   {
/* 445 */     if (isLevelEnabled(4))
/* 446 */       log(4, message, t);
/*     */   }
/*     */ 
/*     */   public final void error(Object message)
/*     */   {
/* 456 */     if (isLevelEnabled(5))
/* 457 */       log(5, message, null);
/*     */   }
/*     */ 
/*     */   public final void error(Object message, Throwable t)
/*     */   {
/* 467 */     if (isLevelEnabled(5))
/* 468 */       log(5, message, t);
/*     */   }
/*     */ 
/*     */   public final void fatal(Object message)
/*     */   {
/* 478 */     if (isLevelEnabled(6))
/* 479 */       log(6, message, null);
/*     */   }
/*     */ 
/*     */   public final void fatal(Object message, Throwable t)
/*     */   {
/* 489 */     if (isLevelEnabled(6))
/* 490 */       log(6, message, t);
/*     */   }
/*     */ 
/*     */   public final boolean isDebugEnabled()
/*     */   {
/* 504 */     return isLevelEnabled(2);
/*     */   }
/*     */ 
/*     */   public final boolean isErrorEnabled()
/*     */   {
/* 517 */     return isLevelEnabled(5);
/*     */   }
/*     */ 
/*     */   public final boolean isFatalEnabled()
/*     */   {
/* 530 */     return isLevelEnabled(6);
/*     */   }
/*     */ 
/*     */   public final boolean isInfoEnabled()
/*     */   {
/* 543 */     return isLevelEnabled(3);
/*     */   }
/*     */ 
/*     */   public final boolean isTraceEnabled()
/*     */   {
/* 556 */     return isLevelEnabled(1);
/*     */   }
/*     */ 
/*     */   public final boolean isWarnEnabled()
/*     */   {
/* 569 */     return isLevelEnabled(4);
/*     */   }
/*     */ 
/*     */   private static ClassLoader getContextClassLoader()
/*     */   {
/* 585 */     ClassLoader classLoader = null;
/*     */ 
/* 587 */     if (classLoader == null) {
/*     */       try
/*     */       {
/* 590 */         Method method = Thread.class.getMethod("getContextClassLoader", null);
/*     */         try
/*     */         {
/* 594 */           classLoader = (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/*     */         }
/*     */         catch (InvocationTargetException e)
/*     */         {
/* 614 */           if (!(e.getTargetException() instanceof SecurityException))
/*     */           {
/* 619 */             throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 629 */     if (classLoader == null) {
/* 630 */       classLoader = SimpleLog.class.getClassLoader();
/*     */     }
/*     */ 
/* 634 */     return classLoader;
/*     */   }
/*     */ 
/*     */   private static InputStream getResourceAsStream(String name)
/*     */   {
/* 639 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction(name) { private final String val$name;
/*     */ 
/* 642 */       public Object run() { ClassLoader threadCL = SimpleLog.access$000();
/*     */ 
/* 644 */         if (threadCL != null) {
/* 645 */           return threadCL.getResourceAsStream(this.val$name);
/*     */         }
/* 647 */         return ClassLoader.getSystemResourceAsStream(this.val$name);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 181 */     InputStream in = getResourceAsStream("simplelog.properties");
/* 182 */     if (null != in) {
/*     */       try {
/* 184 */         simpleLogProps.load(in);
/* 185 */         in.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */     }
/* 191 */     showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
/* 192 */     showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", showShortName);
/* 193 */     showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", showDateTime);
/* 194 */     showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
/*     */ 
/* 196 */     if (showDateTime)
/* 197 */       dateFormatter = new SimpleDateFormat(getStringProperty("org.apache.commons.logging.simplelog.dateformat", "yyyy/MM/dd HH:mm:ss:SSS zzz"));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.SimpleLog
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
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
/*     */   implements Log, Serializable
/*     */ {
/*     */   protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
/*  85 */   protected static final Properties simpleLogProps = new Properties();
/*     */   protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/*  92 */   protected static boolean showLogName = false;
/*     */ 
/*  97 */   protected static boolean showShortName = true;
/*     */ 
/*  99 */   protected static boolean showDateTime = false;
/*     */ 
/* 101 */   protected static String dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/*     */ 
/* 103 */   protected static DateFormat dateFormatter = null;
/*     */   public static final int LOG_LEVEL_TRACE = 1;
/*     */   public static final int LOG_LEVEL_DEBUG = 2;
/*     */   public static final int LOG_LEVEL_INFO = 3;
/*     */   public static final int LOG_LEVEL_WARN = 4;
/*     */   public static final int LOG_LEVEL_ERROR = 5;
/*     */   public static final int LOG_LEVEL_FATAL = 6;
/*     */   public static final int LOG_LEVEL_ALL = 0;
/*     */   public static final int LOG_LEVEL_OFF = 7;
/* 185 */   protected String logName = null;
/*     */   protected int currentLogLevel;
/* 189 */   private String shortLogName = null;
/*     */ 
/*     */   private static String getStringProperty(String name)
/*     */   {
/* 130 */     String prop = null;
/*     */     try {
/* 132 */       prop = System.getProperty(name);
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/* 136 */     return prop == null ? simpleLogProps.getProperty(name) : prop;
/*     */   }
/*     */ 
/*     */   private static String getStringProperty(String name, String dephault) {
/* 140 */     String prop = getStringProperty(name);
/* 141 */     return prop == null ? dephault : prop;
/*     */   }
/*     */ 
/*     */   private static boolean getBooleanProperty(String name, boolean dephault) {
/* 145 */     String prop = getStringProperty(name);
/* 146 */     return prop == null ? dephault : "true".equalsIgnoreCase(prop);
/*     */   }
/*     */ 
/*     */   public SimpleLog(String name)
/*     */   {
/* 201 */     this.logName = name;
/*     */ 
/* 206 */     setLevel(3);
/*     */ 
/* 209 */     String lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);
/* 210 */     int i = String.valueOf(name).lastIndexOf(".");
/* 211 */     while ((null == lvl) && (i > -1)) {
/* 212 */       name = name.substring(0, i);
/* 213 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + name);
/* 214 */       i = String.valueOf(name).lastIndexOf(".");
/*     */     }
/*     */ 
/* 217 */     if (null == lvl) {
/* 218 */       lvl = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
/*     */     }
/*     */ 
/* 221 */     if ("all".equalsIgnoreCase(lvl))
/* 222 */       setLevel(0);
/* 223 */     else if ("trace".equalsIgnoreCase(lvl))
/* 224 */       setLevel(1);
/* 225 */     else if ("debug".equalsIgnoreCase(lvl))
/* 226 */       setLevel(2);
/* 227 */     else if ("info".equalsIgnoreCase(lvl))
/* 228 */       setLevel(3);
/* 229 */     else if ("warn".equalsIgnoreCase(lvl))
/* 230 */       setLevel(4);
/* 231 */     else if ("error".equalsIgnoreCase(lvl))
/* 232 */       setLevel(5);
/* 233 */     else if ("fatal".equalsIgnoreCase(lvl))
/* 234 */       setLevel(6);
/* 235 */     else if ("off".equalsIgnoreCase(lvl))
/* 236 */       setLevel(7);
/*     */   }
/*     */ 
/*     */   public void setLevel(int currentLogLevel)
/*     */   {
/* 251 */     this.currentLogLevel = currentLogLevel;
/*     */   }
/*     */ 
/*     */   public int getLevel()
/*     */   {
/* 261 */     return this.currentLogLevel;
/*     */   }
/*     */ 
/*     */   protected void log(int type, Object message, Throwable t)
/*     */   {
/* 279 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 282 */     if (showDateTime) {
/* 283 */       buf.append(dateFormatter.format(new Date()));
/* 284 */       buf.append(" ");
/*     */     }
/*     */ 
/* 288 */     switch (type) { case 1:
/* 289 */       buf.append("[TRACE] "); break;
/*     */     case 2:
/* 290 */       buf.append("[DEBUG] "); break;
/*     */     case 3:
/* 291 */       buf.append("[INFO] "); break;
/*     */     case 4:
/* 292 */       buf.append("[WARN] "); break;
/*     */     case 5:
/* 293 */       buf.append("[ERROR] "); break;
/*     */     case 6:
/* 294 */       buf.append("[FATAL] ");
/*     */     }
/*     */ 
/* 298 */     if (showShortName) {
/* 299 */       if (this.shortLogName == null)
/*     */       {
/* 301 */         this.shortLogName = this.logName.substring(this.logName.lastIndexOf(".") + 1);
/* 302 */         this.shortLogName = this.shortLogName.substring(this.shortLogName.lastIndexOf("/") + 1);
/*     */       }
/*     */ 
/* 305 */       buf.append(String.valueOf(this.shortLogName)).append(" - ");
/* 306 */     } else if (showLogName) {
/* 307 */       buf.append(String.valueOf(this.logName)).append(" - ");
/*     */     }
/*     */ 
/* 311 */     buf.append(String.valueOf(message));
/*     */ 
/* 314 */     if (t != null) {
/* 315 */       buf.append(" <");
/* 316 */       buf.append(t.toString());
/* 317 */       buf.append(">");
/*     */ 
/* 319 */       StringWriter sw = new StringWriter(1024);
/* 320 */       PrintWriter pw = new PrintWriter(sw);
/* 321 */       t.printStackTrace(pw);
/* 322 */       pw.close();
/* 323 */       buf.append(sw.toString());
/*     */     }
/*     */ 
/* 327 */     write(buf);
/*     */   }
/*     */ 
/*     */   protected void write(StringBuffer buffer)
/*     */   {
/* 342 */     System.err.println(buffer.toString());
/*     */   }
/*     */ 
/*     */   protected boolean isLevelEnabled(int logLevel)
/*     */   {
/* 355 */     return logLevel >= this.currentLogLevel;
/*     */   }
/*     */ 
/*     */   public final void debug(Object message)
/*     */   {
/* 367 */     if (isLevelEnabled(2))
/* 368 */       log(2, message, null);
/*     */   }
/*     */ 
/*     */   public final void debug(Object message, Throwable t)
/*     */   {
/* 378 */     if (isLevelEnabled(2))
/* 379 */       log(2, message, t);
/*     */   }
/*     */ 
/*     */   public final void trace(Object message)
/*     */   {
/* 389 */     if (isLevelEnabled(1))
/* 390 */       log(1, message, null);
/*     */   }
/*     */ 
/*     */   public final void trace(Object message, Throwable t)
/*     */   {
/* 400 */     if (isLevelEnabled(1))
/* 401 */       log(1, message, t);
/*     */   }
/*     */ 
/*     */   public final void info(Object message)
/*     */   {
/* 411 */     if (isLevelEnabled(3))
/* 412 */       log(3, message, null);
/*     */   }
/*     */ 
/*     */   public final void info(Object message, Throwable t)
/*     */   {
/* 422 */     if (isLevelEnabled(3))
/* 423 */       log(3, message, t);
/*     */   }
/*     */ 
/*     */   public final void warn(Object message)
/*     */   {
/* 433 */     if (isLevelEnabled(4))
/* 434 */       log(4, message, null);
/*     */   }
/*     */ 
/*     */   public final void warn(Object message, Throwable t)
/*     */   {
/* 444 */     if (isLevelEnabled(4))
/* 445 */       log(4, message, t);
/*     */   }
/*     */ 
/*     */   public final void error(Object message)
/*     */   {
/* 455 */     if (isLevelEnabled(5))
/* 456 */       log(5, message, null);
/*     */   }
/*     */ 
/*     */   public final void error(Object message, Throwable t)
/*     */   {
/* 466 */     if (isLevelEnabled(5))
/* 467 */       log(5, message, t);
/*     */   }
/*     */ 
/*     */   public final void fatal(Object message)
/*     */   {
/* 477 */     if (isLevelEnabled(6))
/* 478 */       log(6, message, null);
/*     */   }
/*     */ 
/*     */   public final void fatal(Object message, Throwable t)
/*     */   {
/* 488 */     if (isLevelEnabled(6))
/* 489 */       log(6, message, t);
/*     */   }
/*     */ 
/*     */   public final boolean isDebugEnabled()
/*     */   {
/* 503 */     return isLevelEnabled(2);
/*     */   }
/*     */ 
/*     */   public final boolean isErrorEnabled()
/*     */   {
/* 516 */     return isLevelEnabled(5);
/*     */   }
/*     */ 
/*     */   public final boolean isFatalEnabled()
/*     */   {
/* 529 */     return isLevelEnabled(6);
/*     */   }
/*     */ 
/*     */   public final boolean isInfoEnabled()
/*     */   {
/* 542 */     return isLevelEnabled(3);
/*     */   }
/*     */ 
/*     */   public final boolean isTraceEnabled()
/*     */   {
/* 555 */     return isLevelEnabled(1);
/*     */   }
/*     */ 
/*     */   public final boolean isWarnEnabled()
/*     */   {
/* 568 */     return isLevelEnabled(4);
/*     */   }
/*     */ 
/*     */   private static ClassLoader getContextClassLoader()
/*     */   {
/* 584 */     ClassLoader classLoader = null;
/*     */ 
/* 586 */     if (classLoader == null) {
/*     */       try
/*     */       {
/* 589 */         Method method = Thread.class.getMethod("getContextClassLoader", null);
/*     */         try
/*     */         {
/* 593 */           classLoader = (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */         }
/*     */         catch (IllegalAccessException e)
/*     */         {
/*     */         }
/*     */         catch (InvocationTargetException e)
/*     */         {
/* 613 */           if (!(e.getTargetException() instanceof SecurityException))
/*     */           {
/* 618 */             throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (NoSuchMethodException e)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 628 */     if (classLoader == null) {
/* 629 */       classLoader = SimpleLog.class.getClassLoader();
/*     */     }
/*     */ 
/* 633 */     return classLoader;
/*     */   }
/*     */ 
/*     */   private static InputStream getResourceAsStream(String name)
/*     */   {
/* 638 */     return (InputStream)AccessController.doPrivileged(new PrivilegedAction(name) { private final String val$name;
/*     */ 
/* 641 */       public Object run() { ClassLoader threadCL = SimpleLog.access$000();
/*     */ 
/* 643 */         if (threadCL != null) {
/* 644 */           return threadCL.getResourceAsStream(this.val$name);
/*     */         }
/* 646 */         return ClassLoader.getSystemResourceAsStream(this.val$name);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 154 */     InputStream in = getResourceAsStream("simplelog.properties");
/* 155 */     if (null != in) {
/*     */       try {
/* 157 */         simpleLogProps.load(in);
/* 158 */         in.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */     }
/* 164 */     showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", showLogName);
/* 165 */     showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", showShortName);
/* 166 */     showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", showDateTime);
/*     */ 
/* 168 */     if (showDateTime) {
/* 169 */       dateTimeFormat = getStringProperty("org.apache.commons.logging.simplelog.dateTimeFormat", dateTimeFormat);
/*     */       try
/*     */       {
/* 172 */         dateFormatter = new SimpleDateFormat(dateTimeFormat);
/*     */       }
/*     */       catch (IllegalArgumentException e) {
/* 175 */         dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
/* 176 */         dateFormatter = new SimpleDateFormat(dateTimeFormat);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.SimpleLog
 * JD-Core Version:    0.6.0
 */
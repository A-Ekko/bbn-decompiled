/*     */ package com.mysql.jdbc.log;
/*     */ 
/*     */ import com.mysql.jdbc.Util;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class StandardLogger
/*     */   implements Log
/*     */ {
/*     */   private static final int FATAL = 0;
/*     */   private static final int ERROR = 1;
/*     */   private static final int WARN = 2;
/*     */   private static final int INFO = 3;
/*     */   private static final int DEBUG = 4;
/*     */   private static final int TRACE = 5;
/*  53 */   public static StringBuffer bufferedLog = null;
/*     */ 
/*  55 */   private boolean logLocationInfo = true;
/*     */ 
/*     */   public StandardLogger(String name)
/*     */   {
/*  64 */     this(name, false);
/*     */   }
/*     */ 
/*     */   public StandardLogger(String name, boolean logLocationInfo) {
/*  68 */     this.logLocationInfo = logLocationInfo;
/*     */   }
/*     */ 
/*     */   public static void saveLogsToBuffer() {
/*  72 */     if (bufferedLog == null)
/*  73 */       bufferedLog = new StringBuffer();
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/*  81 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/*  88 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/*  95 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 102 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 116 */     return true;
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message)
/*     */   {
/* 126 */     logInternal(4, message, null);
/*     */   }
/*     */ 
/*     */   public void logDebug(Object message, Throwable exception)
/*     */   {
/* 138 */     logInternal(4, message, exception);
/*     */   }
/*     */ 
/*     */   public void logError(Object message)
/*     */   {
/* 148 */     logInternal(1, message, null);
/*     */   }
/*     */ 
/*     */   public void logError(Object message, Throwable exception)
/*     */   {
/* 160 */     logInternal(1, message, exception);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message)
/*     */   {
/* 170 */     logInternal(0, message, null);
/*     */   }
/*     */ 
/*     */   public void logFatal(Object message, Throwable exception)
/*     */   {
/* 182 */     logInternal(0, message, exception);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message)
/*     */   {
/* 192 */     logInternal(3, message, null);
/*     */   }
/*     */ 
/*     */   public void logInfo(Object message, Throwable exception)
/*     */   {
/* 204 */     logInternal(3, message, exception);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message)
/*     */   {
/* 214 */     logInternal(5, message, null);
/*     */   }
/*     */ 
/*     */   public void logTrace(Object message, Throwable exception)
/*     */   {
/* 226 */     logInternal(5, message, exception);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message)
/*     */   {
/* 236 */     logInternal(2, message, null);
/*     */   }
/*     */ 
/*     */   public void logWarn(Object message, Throwable exception)
/*     */   {
/* 248 */     logInternal(2, message, exception);
/*     */   }
/*     */ 
/*     */   private void logInternal(int level, Object msg, Throwable exception) {
/* 252 */     StringBuffer msgBuf = new StringBuffer();
/* 253 */     msgBuf.append(new Date().toString());
/* 254 */     msgBuf.append(" ");
/*     */ 
/* 256 */     switch (level) {
/*     */     case 0:
/* 258 */       msgBuf.append("FATAL: ");
/*     */ 
/* 260 */       break;
/*     */     case 1:
/* 263 */       msgBuf.append("ERROR: ");
/*     */ 
/* 265 */       break;
/*     */     case 2:
/* 268 */       msgBuf.append("WARN: ");
/*     */ 
/* 270 */       break;
/*     */     case 3:
/* 273 */       msgBuf.append("INFO: ");
/*     */ 
/* 275 */       break;
/*     */     case 4:
/* 278 */       msgBuf.append("DEBUG: ");
/*     */ 
/* 280 */       break;
/*     */     case 5:
/* 283 */       msgBuf.append("TRACE: ");
/*     */     }
/*     */ 
/* 288 */     if ((msg instanceof ProfilerEvent)) {
/* 289 */       msgBuf.append(LogUtils.expandProfilerEventIfNecessary(msg));
/*     */     }
/*     */ 
/* 302 */     if (msg != null) {
/* 303 */       msgBuf.append(String.valueOf(msg));
/*     */     }
/*     */ 
/* 306 */     if (exception != null) {
/* 307 */       msgBuf.append("\n");
/* 308 */       msgBuf.append("\n");
/* 309 */       msgBuf.append("EXCEPTION STACK TRACE:");
/* 310 */       msgBuf.append("\n");
/* 311 */       msgBuf.append("\n");
/* 312 */       msgBuf.append(Util.stackTraceToString(exception));
/*     */     }
/*     */ 
/* 315 */     String messageAsString = msgBuf.toString();
/*     */ 
/* 317 */     System.err.println(messageAsString);
/*     */ 
/* 319 */     if (bufferedLog != null)
/* 320 */       bufferedLog.append(messageAsString);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.log.StandardLogger
 * JD-Core Version:    0.6.0
 */
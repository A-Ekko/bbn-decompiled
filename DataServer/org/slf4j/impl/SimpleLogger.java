/*     */ package org.slf4j.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.slf4j.helpers.MarkerIgnoringBase;
/*     */ import org.slf4j.helpers.MessageFormatter;
/*     */ 
/*     */ public class SimpleLogger extends MarkerIgnoringBase
/*     */ {
/*     */   private static final long serialVersionUID = -6560244151660620173L;
/*  71 */   private static long startTime = System.currentTimeMillis();
/*  72 */   public static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  74 */   private static String INFO_STR = "INFO";
/*  75 */   private static String WARN_STR = "WARN";
/*  76 */   private static String ERROR_STR = "ERROR";
/*     */ 
/*     */   SimpleLogger(String name)
/*     */   {
/*  83 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   public void trace(String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object param1)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object param1, Object param2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String format, Object[] argArray)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(String msg, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 137 */     return false;
/*     */   }
/*     */ 
/*     */   public void debug(String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object param1)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object param1, Object param2)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String format, Object[] argArray)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(String msg, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void log(String level, String message, Throwable t)
/*     */   {
/* 186 */     StringBuffer buf = new StringBuffer();
/*     */ 
/* 188 */     long millis = System.currentTimeMillis();
/* 189 */     buf.append(millis - startTime);
/*     */ 
/* 191 */     buf.append(" [");
/* 192 */     buf.append(Thread.currentThread().getName());
/* 193 */     buf.append("] ");
/*     */ 
/* 195 */     buf.append(level);
/* 196 */     buf.append(" ");
/*     */ 
/* 198 */     buf.append(this.name);
/* 199 */     buf.append(" - ");
/*     */ 
/* 201 */     buf.append(message);
/*     */ 
/* 203 */     buf.append(LINE_SEPARATOR);
/*     */ 
/* 205 */     System.err.print(buf.toString());
/* 206 */     if (t != null) {
/* 207 */       t.printStackTrace(System.err);
/*     */     }
/* 209 */     System.err.flush();
/*     */   }
/*     */ 
/*     */   private void formatAndLog(String level, String format, Object arg1, Object arg2)
/*     */   {
/* 222 */     String message = MessageFormatter.format(format, arg1, arg2);
/* 223 */     log(level, message, null);
/*     */   }
/*     */ 
/*     */   private void formatAndLog(String level, String format, Object[] argArray)
/*     */   {
/* 234 */     String message = MessageFormatter.arrayFormat(format, argArray);
/* 235 */     log(level, message, null);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 242 */     return true;
/*     */   }
/*     */ 
/*     */   public void info(String msg)
/*     */   {
/* 250 */     log(INFO_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object arg)
/*     */   {
/* 258 */     formatAndLog(INFO_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object arg1, Object arg2)
/*     */   {
/* 266 */     formatAndLog(INFO_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void info(String format, Object[] argArray)
/*     */   {
/* 274 */     formatAndLog(INFO_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void info(String msg, Throwable t)
/*     */   {
/* 282 */     log(INFO_STR, msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 289 */     return true;
/*     */   }
/*     */ 
/*     */   public void warn(String msg)
/*     */   {
/* 297 */     log(WARN_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object arg)
/*     */   {
/* 305 */     formatAndLog(WARN_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object arg1, Object arg2)
/*     */   {
/* 313 */     formatAndLog(WARN_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void warn(String format, Object[] argArray)
/*     */   {
/* 321 */     formatAndLog(WARN_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void warn(String msg, Throwable t)
/*     */   {
/* 328 */     log(WARN_STR, msg, t);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 335 */     return true;
/*     */   }
/*     */ 
/*     */   public void error(String msg)
/*     */   {
/* 343 */     log(ERROR_STR, msg, null);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object arg)
/*     */   {
/* 351 */     formatAndLog(ERROR_STR, format, arg, null);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object arg1, Object arg2)
/*     */   {
/* 359 */     formatAndLog(ERROR_STR, format, arg1, arg2);
/*     */   }
/*     */ 
/*     */   public void error(String format, Object[] argArray)
/*     */   {
/* 367 */     formatAndLog(ERROR_STR, format, argArray);
/*     */   }
/*     */ 
/*     */   public void error(String msg, Throwable t)
/*     */   {
/* 375 */     log(ERROR_STR, msg, t);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.impl.SimpleLogger
 * JD-Core Version:    0.6.0
 */
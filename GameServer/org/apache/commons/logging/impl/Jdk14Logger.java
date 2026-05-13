/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public final class Jdk14Logger
/*     */   implements Log
/*     */ {
/* 107 */   protected Logger logger = null;
/*     */ 
/*     */   public Jdk14Logger(String name)
/*     */   {
/*  96 */     this.logger = Logger.getLogger(name);
/*     */   }
/*     */ 
/*     */   private void log(Level level, String msg, Throwable ex)
/*     */   {
/* 113 */     if (this.logger.isLoggable(level))
/*     */     {
/* 115 */       Throwable dummyException = new Throwable();
/* 116 */       StackTraceElement[] locations = dummyException.getStackTrace();
/*     */ 
/* 118 */       String cname = "unknown";
/* 119 */       String method = "unknown";
/* 120 */       if ((locations != null) && (locations.length > 2)) {
/* 121 */         StackTraceElement caller = locations[2];
/* 122 */         cname = caller.getClassName();
/* 123 */         method = caller.getMethodName();
/*     */       }
/* 125 */       if (ex == null)
/* 126 */         this.logger.logp(level, cname, method, msg);
/*     */       else
/* 128 */         this.logger.logp(level, cname, method, msg, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 137 */     log(Level.FINE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable exception)
/*     */   {
/* 145 */     log(Level.FINE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 153 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable exception)
/*     */   {
/* 161 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 169 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable exception)
/*     */   {
/* 177 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/* 185 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 193 */     log(Level.INFO, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable exception)
/*     */   {
/* 201 */     log(Level.INFO, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 209 */     return this.logger.isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 217 */     return this.logger.isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 225 */     return this.logger.isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 233 */     return this.logger.isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 241 */     return this.logger.isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 249 */     return this.logger.isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 257 */     log(Level.FINEST, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable exception)
/*     */   {
/* 265 */     log(Level.FINEST, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 273 */     log(Level.WARNING, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable exception)
/*     */   {
/* 281 */     log(Level.WARNING, String.valueOf(message), exception);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.Jdk14Logger
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ public final class Log4JLogger
/*     */   implements Log
/*     */ {
/*  86 */   private static final String FQCN = Log4JLogger.class.getName();
/*     */ 
/*  89 */   private Logger logger = null;
/*     */ 
/*     */   public Log4JLogger()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Log4JLogger(String name)
/*     */   {
/* 102 */     this.logger = Logger.getLogger(name);
/*     */   }
/*     */ 
/*     */   public Log4JLogger(Logger logger)
/*     */   {
/* 108 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 120 */     this.logger.log(FQCN, Priority.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/* 129 */     this.logger.log(FQCN, Priority.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 137 */     this.logger.log(FQCN, Priority.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 144 */     this.logger.log(FQCN, Priority.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 152 */     this.logger.log(FQCN, Priority.INFO, message, null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 160 */     this.logger.log(FQCN, Priority.INFO, message, t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 168 */     this.logger.log(FQCN, Priority.WARN, message, null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 176 */     this.logger.log(FQCN, Priority.WARN, message, t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 184 */     this.logger.log(FQCN, Priority.ERROR, message, null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 192 */     this.logger.log(FQCN, Priority.ERROR, message, t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 200 */     this.logger.log(FQCN, Priority.FATAL, message, null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 208 */     this.logger.log(FQCN, Priority.FATAL, message, t);
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/* 216 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 224 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 232 */     return this.logger.isEnabledFor(Priority.ERROR);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 240 */     return this.logger.isEnabledFor(Priority.FATAL);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 248 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 257 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 264 */     return this.logger.isEnabledFor(Priority.WARN);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4JLogger
 * JD-Core Version:    0.6.0
 */
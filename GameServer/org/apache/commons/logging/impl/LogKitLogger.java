/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log.Hierarchy;
/*     */ import org.apache.log.Logger;
/*     */ 
/*     */ public final class LogKitLogger
/*     */   implements Log
/*     */ {
/*  91 */   protected Logger logger = null;
/*     */ 
/*     */   public LogKitLogger(String name)
/*     */   {
/* 104 */     this.logger = Hierarchy.getDefaultHierarchy().getLoggerFor(name);
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 115 */     debug(message);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/* 123 */     debug(message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 131 */     if (message != null)
/* 132 */       this.logger.debug(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 141 */     if (message != null)
/* 142 */       this.logger.debug(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 151 */     if (message != null)
/* 152 */       this.logger.info(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 161 */     if (message != null)
/* 162 */       this.logger.info(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 171 */     if (message != null)
/* 172 */       this.logger.warn(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 181 */     if (message != null)
/* 182 */       this.logger.warn(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 191 */     if (message != null)
/* 192 */       this.logger.error(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 201 */     if (message != null)
/* 202 */       this.logger.error(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 211 */     if (message != null)
/* 212 */       this.logger.fatalError(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 221 */     if (message != null)
/* 222 */       this.logger.fatalError(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 231 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 239 */     return this.logger.isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 247 */     return this.logger.isFatalErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 255 */     return this.logger.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 263 */     return this.logger.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 271 */     return this.logger.isWarnEnabled();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.LogKitLogger
 * JD-Core Version:    0.6.0
 */
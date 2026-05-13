/*     */ package org.apache.commons.logging;
/*     */ 
/*     */ public class LogConfigurationException extends RuntimeException
/*     */ {
/* 129 */   protected Throwable cause = null;
/*     */ 
/*     */   public LogConfigurationException()
/*     */   {
/*     */   }
/*     */ 
/*     */   public LogConfigurationException(String message)
/*     */   {
/*  94 */     super(message);
/*     */   }
/*     */ 
/*     */   public LogConfigurationException(Throwable cause)
/*     */   {
/* 107 */     this(cause == null ? null : cause.toString(), cause);
/*     */   }
/*     */ 
/*     */   public LogConfigurationException(String message, Throwable cause)
/*     */   {
/* 120 */     super(message);
/* 121 */     this.cause = cause;
/*     */   }
/*     */ 
/*     */   public Throwable getCause()
/*     */   {
/* 137 */     return this.cause;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.LogConfigurationException
 * JD-Core Version:    0.6.0
 */
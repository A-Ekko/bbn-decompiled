/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public final class NoOpLog
/*     */   implements Log
/*     */ {
/*     */   public NoOpLog()
/*     */   {
/*     */   }
/*     */ 
/*     */   public NoOpLog(String name)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final boolean isDebugEnabled()
/*     */   {
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isErrorEnabled()
/*     */   {
/* 120 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isFatalEnabled()
/*     */   {
/* 127 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isInfoEnabled()
/*     */   {
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isTraceEnabled()
/*     */   {
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isWarnEnabled()
/*     */   {
/* 148 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.NoOpLog
 * JD-Core Version:    0.6.0
 */
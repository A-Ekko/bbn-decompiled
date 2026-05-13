/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class NoOpLog
/*     */   implements Log, Serializable
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
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isErrorEnabled()
/*     */   {
/*  76 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isFatalEnabled()
/*     */   {
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isInfoEnabled()
/*     */   {
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isTraceEnabled()
/*     */   {
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isWarnEnabled()
/*     */   {
/* 104 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.NoOpLog
 * JD-Core Version:    0.6.0
 */
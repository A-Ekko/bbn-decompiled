/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.TriggeringEventEvaluator;
/*     */ 
/*     */ class DefaultEvaluator
/*     */   implements TriggeringEventEvaluator
/*     */ {
/*     */   public boolean isTriggeringEvent(LoggingEvent event)
/*     */   {
/* 602 */     return event.getLevel().isGreaterOrEqual(Level.ERROR);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.net.DefaultEvaluator
 * JD-Core Version:    0.6.0
 */
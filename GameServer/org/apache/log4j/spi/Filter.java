/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ public abstract class Filter
/*     */   implements OptionHandler
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public Filter next;
/*     */   public static final int DENY = -1;
/*     */   public static final int NEUTRAL = 0;
/*     */   public static final int ACCEPT = 1;
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public abstract int decide(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */   public void setNext(Filter next)
/*     */   {
/* 115 */     this.next = next;
/*     */   }
/*     */ 
/*     */   public Filter getNext()
/*     */   {
/* 122 */     return this.next;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.Filter
 * JD-Core Version:    0.6.0
 */
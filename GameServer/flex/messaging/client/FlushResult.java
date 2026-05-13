/*     */ package flex.messaging.client;
/*     */ 
/*     */ import java.util.List;
/*     */ 
/*     */ public class FlushResult
/*     */ {
/*     */   private List messages;
/*  81 */   private int nextFlushWaitTimeMillis = 0;
/*     */ 
/*     */   public List getMessages()
/*     */   {
/*  64 */     return this.messages;
/*     */   }
/*     */ 
/*     */   public void setMessages(List value)
/*     */   {
/*  74 */     this.messages = value;
/*     */   }
/*     */ 
/*     */   public int getNextFlushWaitTimeMillis()
/*     */   {
/*  98 */     return this.nextFlushWaitTimeMillis;
/*     */   }
/*     */ 
/*     */   public void setNextFlushWaitTimeMillis(int value)
/*     */   {
/* 115 */     this.nextFlushWaitTimeMillis = (value < 1 ? 0 : value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlushResult
 * JD-Core Version:    0.6.0
 */
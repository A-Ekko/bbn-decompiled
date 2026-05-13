/*    */ package flex.messaging.client;
/*    */ 
/*    */ public class PollFlushResult extends FlushResult
/*    */ {
/*    */   private boolean avoidBusyPolling;
/*    */   private boolean clientProcessingSuppressed;
/*    */ 
/*    */   public boolean isAvoidBusyPolling()
/*    */   {
/* 50 */     return this.avoidBusyPolling;
/*    */   }
/*    */ 
/*    */   public void setAvoidBusyPolling(boolean value)
/*    */   {
/* 62 */     this.avoidBusyPolling = value;
/*    */   }
/*    */ 
/*    */   public boolean isClientProcessingSuppressed()
/*    */   {
/* 83 */     return this.clientProcessingSuppressed;
/*    */   }
/*    */ 
/*    */   public void setClientProcessingSuppressed(boolean value)
/*    */   {
/* 97 */     this.clientProcessingSuppressed = value;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.PollFlushResult
 * JD-Core Version:    0.6.0
 */
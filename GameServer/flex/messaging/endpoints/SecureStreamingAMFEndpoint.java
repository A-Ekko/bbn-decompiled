/*    */ package flex.messaging.endpoints;
/*    */ 
/*    */ public class SecureStreamingAMFEndpoint extends StreamingAMFEndpoint
/*    */ {
/*    */   public SecureStreamingAMFEndpoint()
/*    */   {
/* 34 */     this(false);
/*    */   }
/*    */ 
/*    */   public SecureStreamingAMFEndpoint(boolean enableManagement)
/*    */   {
/* 45 */     super(enableManagement);
/*    */   }
/*    */ 
/*    */   public boolean isSecure()
/*    */   {
/* 61 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.SecureStreamingAMFEndpoint
 * JD-Core Version:    0.6.0
 */
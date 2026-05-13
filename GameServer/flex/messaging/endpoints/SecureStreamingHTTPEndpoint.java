/*    */ package flex.messaging.endpoints;
/*    */ 
/*    */ public class SecureStreamingHTTPEndpoint extends StreamingHTTPEndpoint
/*    */ {
/*    */   public SecureStreamingHTTPEndpoint()
/*    */   {
/* 34 */     this(false);
/*    */   }
/*    */ 
/*    */   public SecureStreamingHTTPEndpoint(boolean enableManagement)
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
 * Qualified Name:     flex.messaging.endpoints.SecureStreamingHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
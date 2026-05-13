/*    */ package flex.messaging.endpoints;
/*    */ 
/*    */ public class SecureHTTPEndpoint extends HTTPEndpoint
/*    */ {
/*    */   public SecureHTTPEndpoint()
/*    */   {
/* 34 */     this(false);
/*    */   }
/*    */ 
/*    */   public SecureHTTPEndpoint(boolean enableManagement)
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
 * Qualified Name:     flex.messaging.endpoints.SecureHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.endpoints;
/*    */ 
/*    */ public class SecureAMFEndpoint extends AMFEndpoint
/*    */ {
/*    */   public SecureAMFEndpoint()
/*    */   {
/* 34 */     this(false);
/*    */   }
/*    */ 
/*    */   public SecureAMFEndpoint(boolean enableManagement)
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
 * Qualified Name:     flex.messaging.endpoints.SecureAMFEndpoint
 * JD-Core Version:    0.6.0
 */
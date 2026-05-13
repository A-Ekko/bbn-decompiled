/*    */ package flex.messaging;
/*    */ 
/*    */ public class FlexRemoteCredentials
/*    */ {
/*    */   private String service;
/*    */   private String destination;
/*    */   private String username;
/*    */   private Object credentials;
/*    */ 
/*    */   public FlexRemoteCredentials(String service, String destination, String username, Object credentials)
/*    */   {
/* 17 */     this.service = service;
/* 18 */     this.destination = destination;
/* 19 */     this.username = username;
/* 20 */     this.credentials = credentials;
/*    */   }
/*    */ 
/*    */   public String getUsername()
/*    */   {
/* 25 */     return this.username;
/*    */   }
/*    */ 
/*    */   public Object getCredentials()
/*    */   {
/* 30 */     return this.credentials;
/*    */   }
/*    */ 
/*    */   public String getService()
/*    */   {
/* 35 */     return this.service;
/*    */   }
/*    */ 
/*    */   public String getDestination()
/*    */   {
/* 40 */     return this.destination;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexRemoteCredentials
 * JD-Core Version:    0.6.0
 */
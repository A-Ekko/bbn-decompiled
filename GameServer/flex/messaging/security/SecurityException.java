/*    */ package flex.messaging.security;
/*    */ 
/*    */ import flex.messaging.MessageException;
/*    */ import flex.messaging.messages.ErrorMessage;
/*    */ import flex.messaging.messages.Message;
/*    */ import flex.messaging.util.ResourceLoader;
/*    */ 
/*    */ public class SecurityException extends MessageException
/*    */ {
/*    */   static final long serialVersionUID = -3168212117963624230L;
/*    */   public static final String CLIENT_AUTHENTICATION_CODE = "Client.Authentication";
/*    */   public static final String CLIENT_AUTHORIZATION_CODE = "Client.Authorization";
/*    */   public static final String SERVER_AUTHENTICATION_CODE = "Server.Authentication";
/*    */   public static final String SERVER_AUTHORIZATION_CODE = "Server.Authorization";
/*    */   private Message failingMessage;
/*    */ 
/*    */   public SecurityException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SecurityException(ResourceLoader resourceLoader)
/*    */   {
/* 62 */     super(resourceLoader);
/*    */   }
/*    */ 
/*    */   public Message getFailingMessage()
/*    */   {
/* 67 */     return this.failingMessage;
/*    */   }
/*    */ 
/*    */   public void setFailingMessage(Message failingMessage)
/*    */   {
/* 72 */     this.failingMessage = failingMessage;
/*    */   }
/*    */ 
/*    */   public ErrorMessage createErrorMessage()
/*    */   {
/* 77 */     ErrorMessage msg = super.createErrorMessage();
/* 78 */     if (this.failingMessage != null)
/*    */     {
/* 80 */       msg.setCorrelationId(this.failingMessage.getMessageId());
/* 81 */       msg.setDestination(this.failingMessage.getDestination());
/*    */     }
/* 83 */     return msg;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.security.SecurityException
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.endpoints.amf;
/*    */ 
/*    */ import flex.messaging.io.RecoverableSerializationException;
/*    */ import flex.messaging.io.amf.ActionContext;
/*    */ import flex.messaging.io.amf.ActionMessage;
/*    */ import flex.messaging.io.amf.MessageBody;
/*    */ 
/*    */ public class BatchProcessFilter extends AMFFilter
/*    */ {
/*    */   public void invoke(ActionContext context)
/*    */   {
/* 40 */     int bodyCount = context.getRequestMessage().getBodyCount();
/*    */ 
/* 45 */     for (context.setMessageNumber(0); context.getMessageNumber() < bodyCount; context.incrementMessageNumber())
/*    */     {
/*    */       try
/*    */       {
/* 50 */         MessageBody responseBody = new MessageBody();
/* 51 */         responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());
/*    */ 
/* 54 */         context.getResponseMessage().addBody(responseBody);
/*    */ 
/* 57 */         Object o = context.getRequestMessageBody().getData();
/*    */ 
/* 59 */         if ((o != null) && ((o instanceof RecoverableSerializationException)))
/*    */         {
/* 61 */           context.getResponseMessageBody().setData(((RecoverableSerializationException)o).createErrorMessage());
/* 62 */           context.getResponseMessageBody().setReplyMethod("/onStatus");
/*    */         }
/*    */         else
/*    */         {
/* 67 */           this.next.invoke(context);
/*    */         }
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.BatchProcessFilter
 * JD-Core Version:    0.6.0
 */
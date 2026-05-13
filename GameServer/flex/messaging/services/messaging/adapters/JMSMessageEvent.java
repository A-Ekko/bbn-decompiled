/*    */ package flex.messaging.services.messaging.adapters;
/*    */ 
/*    */ import java.util.EventObject;
/*    */ import javax.jms.Message;
/*    */ 
/*    */ public class JMSMessageEvent extends EventObject
/*    */ {
/*    */   private Message message;
/*    */ 
/*    */   JMSMessageEvent(JMSConsumer source, Message message)
/*    */   {
/* 24 */     super(source);
/* 25 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public Message getJMSMessage()
/*    */   {
/* 35 */     return this.message;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSMessageEvent
 * JD-Core Version:    0.6.0
 */
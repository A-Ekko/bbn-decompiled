/*    */ package flex.messaging.services.messaging.adapters;
/*    */ 
/*    */ import javax.jms.ExceptionListener;
/*    */ import javax.jms.JMSException;
/*    */ import javax.jms.Message;
/*    */ import javax.jms.MessageListener;
/*    */ 
/*    */ class AsyncMessageReceiver
/*    */   implements MessageReceiver, ExceptionListener, MessageListener
/*    */ {
/*    */   private JMSConsumer jmsConsumer;
/*    */ 
/*    */   public AsyncMessageReceiver(JMSConsumer jmsConsumer)
/*    */   {
/* 40 */     this.jmsConsumer = jmsConsumer;
/*    */   }
/*    */ 
/*    */   public void startReceive()
/*    */     throws JMSException
/*    */   {
/* 48 */     this.jmsConsumer.setMessageListener(this);
/*    */   }
/*    */ 
/*    */   public void stopReceive()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void onException(JMSException exception)
/*    */   {
/* 66 */     this.jmsConsumer.onException(exception);
/*    */   }
/*    */ 
/*    */   public void onMessage(Message message)
/*    */   {
/* 76 */     this.jmsConsumer.onMessage(message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.AsyncMessageReceiver
 * JD-Core Version:    0.6.0
 */
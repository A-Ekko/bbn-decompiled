/*    */ package flex.messaging.services.messaging.adapters;
/*    */ 
/*    */ import flex.messaging.MessageException;
/*    */ import javax.jms.JMSException;
/*    */ import javax.jms.Queue;
/*    */ import javax.jms.QueueConnection;
/*    */ import javax.jms.QueueConnectionFactory;
/*    */ import javax.jms.QueueSession;
/*    */ import javax.naming.NamingException;
/*    */ 
/*    */ public class JMSQueueConsumer extends JMSConsumer
/*    */ {
/*    */   public void start()
/*    */     throws NamingException, JMSException
/*    */   {
/* 49 */     super.start();
/*    */ 
/* 52 */     Queue queue = null;
/*    */     try
/*    */     {
/* 55 */       queue = (Queue)this.destination;
/*    */     }
/*    */     catch (ClassCastException cce)
/*    */     {
/* 60 */       MessageException me = new MessageException();
/* 61 */       me.setMessage(10813, new Object[] { this.destinationJndiName, this.destination.getClass().getName() });
/* 62 */       throw me;
/*    */     }
/*    */ 
/*    */     try
/*    */     {
/* 68 */       QueueConnectionFactory queueFactory = (QueueConnectionFactory)this.connectionFactory;
/* 69 */       if (this.connectionCredentials != null)
/* 70 */         this.connection = queueFactory.createQueueConnection(this.connectionCredentials.getUsername(), this.connectionCredentials.getPassword());
/*    */       else {
/* 72 */         this.connection = queueFactory.createQueueConnection();
/*    */       }
/*    */     }
/*    */     catch (ClassCastException cce)
/*    */     {
/* 77 */       MessageException me = new MessageException();
/* 78 */       me.setMessage(10814, new Object[] { this.destinationJndiName, this.connectionFactory.getClass().getName() });
/* 79 */       throw me;
/*    */     }
/*    */ 
/* 82 */     QueueConnection queueConnection = (QueueConnection)this.connection;
/*    */ 
/* 85 */     this.session = queueConnection.createQueueSession(false, getAcknowledgeMode());
/*    */ 
/* 88 */     QueueSession queueSession = (QueueSession)this.session;
/*    */ 
/* 91 */     if (this.selectorExpression != null)
/* 92 */       this.consumer = queueSession.createReceiver(queue, this.selectorExpression);
/*    */     else {
/* 94 */       this.consumer = queueSession.createReceiver(queue);
/*    */     }
/* 96 */     startMessageReceiver();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSQueueConsumer
 * JD-Core Version:    0.6.0
 */
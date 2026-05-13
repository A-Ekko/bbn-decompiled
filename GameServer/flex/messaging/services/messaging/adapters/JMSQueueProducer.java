/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Queue;
/*     */ import javax.jms.QueueConnection;
/*     */ import javax.jms.QueueConnectionFactory;
/*     */ import javax.jms.QueueSender;
/*     */ import javax.jms.QueueSession;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public class JMSQueueProducer extends JMSProducer
/*     */ {
/*     */   private QueueSender sender;
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/*  49 */     super.start();
/*     */ 
/*  52 */     Queue queue = null;
/*     */     try
/*     */     {
/*  55 */       queue = (Queue)this.destination;
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/*  60 */       MessageException me = new MessageException();
/*  61 */       me.setMessage(10813, new Object[] { this.destinationJndiName, this.destination.getClass().getName() });
/*  62 */       throw me;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  68 */       QueueConnectionFactory queueFactory = (QueueConnectionFactory)this.connectionFactory;
/*  69 */       if (this.connectionCredentials != null)
/*  70 */         this.connection = queueFactory.createQueueConnection(this.connectionCredentials.getUsername(), this.connectionCredentials.getPassword());
/*     */       else {
/*  72 */         this.connection = queueFactory.createQueueConnection();
/*     */       }
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/*  77 */       MessageException me = new MessageException();
/*  78 */       me.setMessage(10814, new Object[] { this.destinationJndiName, this.connectionFactory.getClass().getName() });
/*  79 */       throw me;
/*     */     }
/*     */ 
/*  83 */     QueueConnection queueConnection = (QueueConnection)this.connection;
/*  84 */     this.session = queueConnection.createQueueSession(false, getAcknowledgeMode());
/*     */ 
/*  87 */     QueueSession queueSession = (QueueSession)this.session;
/*  88 */     this.sender = queueSession.createSender(queue);
/*  89 */     this.producer = this.sender;
/*     */ 
/*  92 */     this.connection.start();
/*     */   }
/*     */ 
/*     */   void sendTextMessage(String text, Map properties) throws JMSException
/*     */   {
/*  97 */     if (text != null)
/*     */     {
/*  99 */       TextMessage message = this.session.createTextMessage();
/* 100 */       message.setText(text);
/* 101 */       copyHeadersToProperties(properties, message);
/* 102 */       long timeToLive = getTimeToLive(properties);
/* 103 */       this.sender.send(message, getDeliveryMode(), this.messagePriority, timeToLive);
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendObjectMessage(Serializable obj, Map properties) throws JMSException
/*     */   {
/* 109 */     if (obj != null)
/*     */     {
/* 111 */       ObjectMessage message = this.session.createObjectMessage();
/* 112 */       message.setObject(obj);
/* 113 */       copyHeadersToProperties(properties, message);
/* 114 */       long timeToLive = getTimeToLive(properties);
/* 115 */       this.sender.send(message, getDeliveryMode(), this.messagePriority, timeToLive);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSQueueProducer
 * JD-Core Version:    0.6.0
 */
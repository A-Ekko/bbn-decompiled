/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import javax.jms.Connection;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.ObjectMessage;
/*     */ import javax.jms.Session;
/*     */ import javax.jms.TextMessage;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicPublisher;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public class JMSTopicProducer extends JMSProducer
/*     */ {
/*     */   private TopicPublisher publisher;
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/*  48 */     super.start();
/*     */ 
/*  51 */     Topic topic = null;
/*     */     try
/*     */     {
/*  54 */       topic = (Topic)this.destination;
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/*  59 */       MessageException me = new MessageException();
/*  60 */       me.setMessage(10815, new Object[] { this.destinationJndiName, this.destination.getClass().getName() });
/*  61 */       throw me;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  67 */       TopicConnectionFactory topicFactory = (TopicConnectionFactory)this.connectionFactory;
/*  68 */       if (this.connectionCredentials != null)
/*  69 */         this.connection = topicFactory.createTopicConnection(this.connectionCredentials.getUsername(), this.connectionCredentials.getPassword());
/*     */       else {
/*  71 */         this.connection = topicFactory.createTopicConnection();
/*     */       }
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/*  76 */       MessageException me = new MessageException();
/*  77 */       me.setMessage(10816, new Object[] { this.destinationJndiName, this.connectionFactory.getClass().getName() });
/*  78 */       throw me;
/*     */     }
/*     */ 
/*  82 */     TopicConnection topicConnection = (TopicConnection)this.connection;
/*  83 */     this.session = topicConnection.createTopicSession(false, getAcknowledgeMode());
/*     */ 
/*  86 */     TopicSession topicSession = (TopicSession)this.session;
/*  87 */     this.publisher = topicSession.createPublisher(topic);
/*  88 */     this.producer = this.publisher;
/*     */ 
/*  91 */     this.connection.start();
/*     */   }
/*     */ 
/*     */   void sendObjectMessage(Serializable obj, Map properties) throws JMSException
/*     */   {
/*  96 */     if (obj != null)
/*     */     {
/*  98 */       ObjectMessage message = this.session.createObjectMessage();
/*  99 */       message.setObject(obj);
/* 100 */       copyHeadersToProperties(properties, message);
/* 101 */       long timeToLive = getTimeToLive(properties);
/* 102 */       this.publisher.publish(message, getDeliveryMode(), this.messagePriority, timeToLive);
/*     */     }
/*     */   }
/*     */ 
/*     */   void sendTextMessage(String text, Map properties) throws JMSException
/*     */   {
/* 108 */     if (text != null)
/*     */     {
/* 110 */       TextMessage message = this.session.createTextMessage();
/* 111 */       message.setText(text);
/* 112 */       copyHeadersToProperties(properties, message);
/* 113 */       long timeToLive = getTimeToLive(properties);
/* 114 */       this.publisher.publish(message, getDeliveryMode(), this.messagePriority, timeToLive);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSTopicProducer
 * JD-Core Version:    0.6.0
 */
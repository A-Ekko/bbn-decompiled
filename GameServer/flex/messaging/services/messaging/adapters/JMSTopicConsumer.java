/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Callable;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Executors;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Future;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MessageConsumer;
/*     */ import javax.jms.Topic;
/*     */ import javax.jms.TopicConnection;
/*     */ import javax.jms.TopicConnectionFactory;
/*     */ import javax.jms.TopicSession;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public class JMSTopicConsumer extends JMSConsumer
/*     */ {
/*     */   protected boolean durableConsumers;
/*     */   protected String durableSubscriptionName;
/*     */ 
/*     */   public void initialize(JMSSettings settings)
/*     */   {
/*  59 */     super.initialize(settings);
/*  60 */     this.durableConsumers = settings.useDurableConsumers();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/*  70 */     super.validate();
/*     */ 
/*  72 */     if ((this.durableConsumers) && (this.durableSubscriptionName == null))
/*     */     {
/*  75 */       ConfigurationException ce = new ConfigurationException();
/*  76 */       ce.setMessage(10823, new Object[] { this.destinationJndiName });
/*  77 */       throw ce;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/*  89 */     super.start();
/*     */ 
/*  92 */     Topic topic = null;
/*     */     try
/*     */     {
/*  95 */       topic = (Topic)this.destination;
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/* 100 */       MessageException me = new MessageException();
/* 101 */       me.setMessage(10815, new Object[] { this.destinationJndiName, this.destination.getClass().getName() });
/* 102 */       throw me;
/*     */     }
/*     */ 
/*     */     TopicConnectionFactory topicFactory;
/*     */     try
/*     */     {
/* 109 */       topicFactory = (TopicConnectionFactory)this.connectionFactory;
/* 110 */       if (this.connectionCredentials != null)
/* 111 */         this.connection = topicFactory.createTopicConnection(this.connectionCredentials.getUsername(), this.connectionCredentials.getPassword());
/*     */       else {
/* 113 */         this.connection = topicFactory.createTopicConnection();
/*     */       }
/*     */     }
/*     */     catch (ClassCastException cce)
/*     */     {
/* 118 */       MessageException me = new MessageException();
/* 119 */       me.setMessage(10816, new Object[] { this.destinationJndiName, this.connectionFactory.getClass().getName() });
/* 120 */       throw me;
/*     */     }
/*     */ 
/* 123 */     TopicConnection topicConnection = (TopicConnection)this.connection;
/*     */ 
/* 125 */     if (this.durableConsumers)
/*     */     {
/*     */       try
/*     */       {
/* 129 */         if (Log.isDebug()) {
/* 130 */           Log.getLogger("Service.Message.JMS").debug("JMS consumer for JMS destination '" + this.destinationJndiName + "' is setting its underlying connection's client id to " + this.durableSubscriptionName + " for durable subscription.");
/*     */         }
/*     */ 
/* 134 */         topicConnection.setClientID(this.durableSubscriptionName);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 139 */         ExecutorService clientIdSetter = Executors.newSingleThreadExecutor();
/* 140 */         ClientIdSetterCallable cisc = new ClientIdSetterCallable(topicFactory, this.durableSubscriptionName);
/* 141 */         Future future = clientIdSetter.submit(cisc);
/*     */         try
/*     */         {
/* 144 */           topicConnection = (TopicConnection)future.get();
/*     */         }
/*     */         catch (InterruptedException ie)
/*     */         {
/* 148 */           if (Log.isWarn()) {
/* 149 */             Log.getLogger("Service.Message.JMS").warn("The proxied durable JMS subscription with name, " + this.durableSubscriptionName + " could not set its client id " + "on the topic connection because it was interrupted: " + ie.toString());
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (ExecutionException ee)
/*     */         {
/* 157 */           MessageException me = new MessageException();
/* 158 */           me.setMessage(10819, new Object[] { this.destinationJndiName });
/* 159 */           throw me;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 165 */     this.session = topicConnection.createTopicSession(false, getAcknowledgeMode());
/* 166 */     TopicSession topicSession = (TopicSession)this.session;
/*     */ 
/* 169 */     if (this.selectorExpression != null)
/*     */     {
/* 171 */       if ((this.durableConsumers) && (this.durableSubscriptionName != null))
/* 172 */         this.consumer = topicSession.createDurableSubscriber(topic, this.durableSubscriptionName, this.selectorExpression, false);
/*     */       else {
/* 174 */         this.consumer = topicSession.createSubscriber(topic, this.selectorExpression, false);
/*     */       }
/*     */ 
/*     */     }
/* 178 */     else if ((this.durableConsumers) && (this.durableSubscriptionName != null))
/* 179 */       this.consumer = topicSession.createDurableSubscriber(topic, this.durableSubscriptionName);
/*     */     else {
/* 181 */       this.consumer = topicSession.createSubscriber(topic);
/*     */     }
/*     */ 
/* 184 */     startMessageReceiver();
/*     */   }
/*     */ 
/*     */   public void stop(boolean unsubscribe)
/*     */   {
/* 196 */     if (unsubscribe)
/*     */     {
/* 198 */       stopMessageReceiver();
/*     */       try
/*     */       {
/* 202 */         if (this.consumer != null)
/* 203 */           this.consumer.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 207 */         if (Log.isWarn()) {
/* 208 */           Log.getLogger("Service.Message.JMS").warn("JMS consumer for JMS destination '" + this.destinationJndiName + "' received an error while closing its underlying MessageConsumer: " + e.getMessage());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 213 */       if (this.durableConsumers)
/*     */       {
/*     */         try
/*     */         {
/* 217 */           TopicSession topicSession = (TopicSession)this.session;
/* 218 */           topicSession.unsubscribe(this.durableSubscriptionName);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 222 */           if (Log.isWarn()) {
/* 223 */             Log.getLogger("Service.Message.JMS").warn("The proxied durable JMS subscription with name, " + this.durableSubscriptionName + " failed to unsubscribe : " + e.toString());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 229 */     super.stop();
/*     */   }
/*     */ 
/*     */   public boolean isDurableConsumers()
/*     */   {
/* 246 */     return this.durableConsumers;
/*     */   }
/*     */ 
/*     */   public void setDurableConsumers(boolean durableConsumers)
/*     */   {
/* 257 */     this.durableConsumers = durableConsumers;
/*     */   }
/*     */ 
/*     */   public String getDurableSubscriptionName()
/*     */   {
/* 267 */     return this.durableSubscriptionName;
/*     */   }
/*     */ 
/*     */   public void setDurableSubscriptionName(String durableSubscriptionName)
/*     */   {
/* 279 */     this.durableSubscriptionName = durableSubscriptionName;
/*     */   }
/*     */ 
/*     */   class ClientIdSetterCallable
/*     */     implements Callable
/*     */   {
/*     */     private TopicConnectionFactory tcf;
/*     */     private String clientId;
/*     */     private TopicConnection topicConnection;
/*     */ 
/*     */     public ClientIdSetterCallable(TopicConnectionFactory tcf, String clientId)
/*     */     {
/* 302 */       this.tcf = tcf;
/* 303 */       this.clientId = clientId;
/*     */     }
/*     */ 
/*     */     public Object call() throws JMSException
/*     */     {
/* 308 */       this.topicConnection = this.tcf.createTopicConnection();
/* 309 */       this.topicConnection.setClientID(this.clientId);
/* 310 */       return this.topicConnection;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSTopicConsumer
 * JD-Core Version:    0.6.0
 */
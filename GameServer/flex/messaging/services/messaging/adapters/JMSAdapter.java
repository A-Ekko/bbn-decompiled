/*      */ package flex.messaging.services.messaging.adapters;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*      */ import flex.management.runtime.messaging.services.messaging.adapters.JMSAdapterControl;
/*      */ import flex.messaging.Destination;
/*      */ import flex.messaging.MessageClient;
/*      */ import flex.messaging.MessageClientListener;
/*      */ import flex.messaging.MessageDestination;
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.config.ConfigMap;
/*      */ import flex.messaging.config.ConfigurationException;
/*      */ import flex.messaging.config.ServerSettings;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.AsyncMessage;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.ErrorMessage;
/*      */ import flex.messaging.messages.MessagePerformanceInfo;
/*      */ import flex.messaging.messages.MessagePerformanceUtils;
/*      */ import flex.messaging.services.MessageService;
/*      */ import java.lang.reflect.Field;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.jms.JMSException;
/*      */ import javax.jms.ObjectMessage;
/*      */ import javax.jms.TextMessage;
/*      */ import javax.naming.Context;
/*      */ 
/*      */ public class JMSAdapter extends MessagingAdapter
/*      */   implements JMSConfigConstants, JMSExceptionListener, JMSMessageListener, MessageClientListener
/*      */ {
/*      */   static final String LOG_CATEGORY = "Service.Message.JMS";
/*      */   private static final String DURABLE_SUBSCRIBER_NAME_PREFIX = "FlexClient_";
/*      */   private Map consumerToClientId;
/*      */   private Map messageClients;
/*      */   private LinkedList topicProducers;
/*      */   private Map topicConsumers;
/*      */   private LinkedList queueProducers;
/*      */   private Map queueConsumers;
/*      */   private JMSSettings settings;
/*      */   private JMSAdapterControl controller;
/*      */ 
/*      */   public JMSAdapter()
/*      */   {
/*   86 */     this(false);
/*      */   }
/*      */ 
/*      */   public JMSAdapter(boolean enableManagement)
/*      */   {
/*   97 */     super(enableManagement);
/*   98 */     this.consumerToClientId = new ConcurrentHashMap();
/*   99 */     this.messageClients = new ConcurrentHashMap();
/*  100 */     this.topicProducers = new LinkedList();
/*  101 */     this.topicConsumers = new ConcurrentHashMap();
/*  102 */     this.queueProducers = new LinkedList();
/*  103 */     this.queueConsumers = new ConcurrentHashMap();
/*  104 */     this.settings = new JMSSettings();
/*      */   }
/*      */ 
/*      */   public void initialize(String id, ConfigMap properties)
/*      */   {
/*  120 */     super.initialize(id, properties);
/*      */ 
/*  122 */     if ((properties == null) || (properties.size() == 0)) {
/*  123 */       return;
/*      */     }
/*      */ 
/*  126 */     jms(properties);
/*      */   }
/*      */ 
/*      */   protected void validate()
/*      */   {
/*  135 */     if (isValid()) {
/*  136 */       return;
/*      */     }
/*  138 */     super.validate();
/*      */ 
/*  140 */     if (this.settings.getConnectionFactory() == null)
/*      */     {
/*  143 */       ConfigurationException ce = new ConfigurationException();
/*  144 */       ce.setMessage(10804);
/*  145 */       throw ce;
/*      */     }
/*      */ 
/*  148 */     if (this.settings.getDestinationJNDIName() == null)
/*      */     {
/*  151 */       ConfigurationException ce = new ConfigurationException();
/*  152 */       ce.setMessage(10807);
/*  153 */       throw ce;
/*      */     }
/*      */ 
/*  156 */     if (this.settings.getMessageType() == null)
/*      */     {
/*  159 */       ConfigurationException ce = new ConfigurationException();
/*  160 */       ce.setMessage(10811, new Object[] { null });
/*  161 */       throw ce;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  170 */     if (isStarted()) {
/*  171 */       return;
/*      */     }
/*  173 */     super.start();
/*      */ 
/*  177 */     MessageClient.addMessageClientCreatedListener(this);
/*      */   }
/*      */ 
/*      */   public void stop()
/*      */   {
/*  185 */     if (!isStarted()) {
/*  186 */       return;
/*      */     }
/*  188 */     super.stop();
/*      */ 
/*  190 */     stopConsumers(this.topicConsumers.values());
/*  191 */     stopConsumers(this.queueConsumers.values());
/*      */   }
/*      */ 
/*      */   public void setDestination(Destination destination)
/*      */   {
/*  208 */     Destination dest = (MessageDestination)destination;
/*  209 */     super.setDestination(dest);
/*      */   }
/*      */ 
/*      */   public JMSSettings getJMSSettings()
/*      */   {
/*  219 */     return this.settings;
/*      */   }
/*      */ 
/*      */   public void setJMSSettings(JMSSettings jmsSettings)
/*      */   {
/*  229 */     this.settings = jmsSettings;
/*      */   }
/*      */ 
/*      */   public int getQueueConsumerCount()
/*      */   {
/*  239 */     return this.queueConsumers.size();
/*      */   }
/*      */ 
/*      */   public String[] getQueueConsumerIds()
/*      */   {
/*  249 */     Set consumerIds = this.queueConsumers.keySet();
/*  250 */     if (consumerIds != null)
/*      */     {
/*  252 */       String[] ids = new String[consumerIds.size()];
/*  253 */       return (String[])(String[])consumerIds.toArray(ids);
/*      */     }
/*  255 */     return new String[0];
/*      */   }
/*      */ 
/*      */   public int getTopicConsumerCount()
/*      */   {
/*  265 */     return this.topicConsumers.size();
/*      */   }
/*      */ 
/*      */   public String[] getTopicConsumerIds()
/*      */   {
/*  275 */     Set consumerIds = this.topicConsumers.keySet();
/*  276 */     if (consumerIds != null)
/*      */     {
/*  278 */       String[] ids = new String[consumerIds.size()];
/*  279 */       return (String[])(String[])consumerIds.toArray(ids);
/*      */     }
/*  281 */     return new String[0];
/*      */   }
/*      */ 
/*      */   public int getTopicProducerCount()
/*      */   {
/*  291 */     return this.topicProducers.size();
/*      */   }
/*      */ 
/*      */   public int getQueueProducerCount()
/*      */   {
/*  301 */     return this.queueProducers.size();
/*      */   }
/*      */ 
/*      */   public void exceptionThrown(JMSExceptionEvent evt)
/*      */   {
/*  312 */     JMSConsumer consumer = (JMSConsumer)evt.getSource();
/*  313 */     JMSException jmsEx = evt.getJMSException();
/*      */ 
/*  316 */     MessageException messageEx = new MessageException();
/*  317 */     messageEx.setMessage(10820, new Object[] { consumer.getDestinationJndiName(), jmsEx.getMessage() });
/*  318 */     removeConsumer(consumer, true, true, messageEx.createErrorMessage());
/*      */   }
/*      */ 
/*      */   public boolean handlesSubscriptions()
/*      */   {
/*  326 */     return true;
/*      */   }
/*      */ 
/*      */   public Object invoke(flex.messaging.messages.Message message)
/*      */   {
/*  334 */     JMSProducer producer = null;
/*      */ 
/*  337 */     Map msgProps = message.getHeaders();
/*  338 */     msgProps.put("timeToLive", new Long(message.getTimeToLive()));
/*      */ 
/*  340 */     if (this.settings.getDestinationType().equals("topic"))
/*      */     {
/*  342 */       synchronized (this.topicProducers)
/*      */       {
/*  344 */         if (this.topicProducers.size() < this.settings.getMaxProducers())
/*      */         {
/*  346 */           producer = new JMSTopicProducer();
/*      */           try
/*      */           {
/*  349 */             producer.initialize(this.settings);
/*  350 */             producer.start();
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*  354 */             throw new MessageException(e);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  359 */           producer = (JMSProducer)this.topicProducers.removeFirst();
/*      */         }
/*      */ 
/*  362 */         this.topicProducers.addLast(producer);
/*      */       }
/*      */     }
/*  365 */     else if (this.settings.getDestinationType().equals("queue"))
/*      */     {
/*  367 */       synchronized (this.queueProducers)
/*      */       {
/*  369 */         if (this.queueProducers.size() < this.settings.getMaxProducers())
/*      */         {
/*  371 */           producer = new JMSQueueProducer();
/*      */           try
/*      */           {
/*  374 */             producer.initialize(this.settings);
/*  375 */             producer.start();
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*  379 */             throw new MessageException(e);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  384 */           producer = (JMSProducer)this.queueProducers.removeFirst();
/*      */         }
/*      */ 
/*  387 */         this.queueProducers.addLast(producer);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  393 */       producer.sendMessage(message);
/*      */     }
/*      */     catch (JMSException jmsEx)
/*      */     {
/*  399 */       if (this.settings.getDestinationType().equals("topic"))
/*      */       {
/*  401 */         synchronized (this.topicProducers)
/*      */         {
/*  403 */           producer.stop();
/*  404 */           this.topicProducers.remove(producer);
/*      */         }
/*      */       }
/*  407 */       else if (this.settings.getDestinationType().equals("queue"))
/*      */       {
/*  409 */         synchronized (this.queueProducers)
/*      */         {
/*  411 */           producer.stop();
/*  412 */           this.queueProducers.remove(producer);
/*      */         }
/*      */       }
/*      */ 
/*  416 */       throw new MessageException(jmsEx);
/*      */     }
/*      */ 
/*  419 */     return null;
/*      */   }
/*      */ 
/*      */   public Object manage(CommandMessage commandMessage)
/*      */   {
/*  427 */     JMSConsumer consumer = null;
/*  428 */     Object clientId = commandMessage.getClientId();
/*      */ 
/*  430 */     if (commandMessage.getOperation() == 0)
/*      */     {
/*  433 */       Object selectorExpression = commandMessage.getHeaders().get("DSSelector");
/*      */ 
/*  436 */       if (this.settings.getDestinationType().equals("topic"))
/*      */       {
/*  438 */         if (this.topicConsumers.containsKey(clientId))
/*      */         {
/*  440 */           consumer = (JMSConsumer)this.topicConsumers.get(clientId);
/*  441 */           consumer.stop(true);
/*      */         }
/*      */ 
/*  444 */         consumer = new JMSTopicConsumer();
/*  445 */         consumer.initialize(this.settings);
/*  446 */         if (selectorExpression != null) {
/*  447 */           consumer.setSelectorExpression((String)selectorExpression);
/*      */         }
/*  449 */         ((JMSTopicConsumer)consumer).setDurableSubscriptionName(buildSubscriptionName(clientId));
/*  450 */         consumer.setMessageReceiver(buildMessageReceiver(consumer));
/*      */ 
/*  453 */         consumer.addJMSExceptionListener(this);
/*  454 */         consumer.addJMSMessageListener(this);
/*  455 */         this.topicConsumers.put(clientId, consumer);
/*  456 */         this.consumerToClientId.put(consumer, clientId);
/*      */       }
/*  461 */       else if (this.settings.getDestinationType().equals("queue"))
/*      */       {
/*  463 */         if (this.queueConsumers.containsKey(clientId))
/*      */         {
/*  465 */           consumer = (JMSConsumer)this.queueConsumers.get(clientId);
/*  466 */           consumer.stop();
/*      */         }
/*      */ 
/*  469 */         consumer = new JMSQueueConsumer();
/*  470 */         consumer.initialize(this.settings);
/*  471 */         if (selectorExpression != null)
/*  472 */           consumer.setSelectorExpression((String)selectorExpression);
/*  473 */         consumer.setMessageReceiver(buildMessageReceiver(consumer));
/*      */ 
/*  476 */         consumer.addJMSExceptionListener(this);
/*  477 */         consumer.addJMSMessageListener(this);
/*  478 */         this.queueConsumers.put(clientId, consumer);
/*  479 */         this.consumerToClientId.put(consumer, clientId);
/*      */       }
/*      */ 
/*      */     }
/*  486 */     else if (commandMessage.getOperation() == 1)
/*      */     {
/*  490 */       boolean unsubscribe = true;
/*      */ 
/*  492 */       boolean preserveDurable = false;
/*  493 */       if (commandMessage.getHeader("DSPreserveDurable") != null) {
/*  494 */         preserveDurable = ((Boolean)(Boolean)commandMessage.getHeader("DSPreserveDurable")).booleanValue();
/*      */       }
/*      */ 
/*  498 */       if (((commandMessage.getHeader("DSSubscriptionInvalidated") != null) && (((Boolean)commandMessage.getHeader("DSSubscriptionInvalidated")).booleanValue())) || (preserveDurable))
/*      */       {
/*  501 */         unsubscribe = false;
/*      */       }
/*  503 */       removeConsumer((String)clientId, unsubscribe, false, null);
/*      */     }
/*      */ 
/*  509 */     return null;
/*      */   }
/*      */ 
/*      */   public void messageClientCreated(MessageClient messageClient)
/*      */   {
/*  524 */     Object clientId = messageClient.getClientId();
/*      */ 
/*  526 */     JMSConsumer consumer = null;
/*  527 */     if (this.topicConsumers.containsKey(clientId))
/*  528 */       consumer = (JMSConsumer)this.topicConsumers.get(clientId);
/*  529 */     else if (this.queueConsumers.containsKey(clientId)) {
/*  530 */       consumer = (JMSConsumer)this.queueConsumers.get(clientId);
/*      */     }
/*      */ 
/*  534 */     if (consumer != null)
/*      */     {
/*  536 */       this.messageClients.put(clientId, messageClient);
/*      */       try
/*      */       {
/*  539 */         consumer.start();
/*      */       }
/*      */       catch (MessageException messageEx)
/*      */       {
/*  543 */         removeConsumer(consumer, true, true, messageEx.createErrorMessage());
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*  547 */         removeConsumer(consumer, true, true, new MessageException(ex).createErrorMessage());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void messageClientDestroyed(MessageClient messageClient)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void messageReceived(JMSMessageEvent evt)
/*      */   {
/*  572 */     JMSConsumer consumer = (JMSConsumer)evt.getSource();
/*  573 */     javax.jms.Message jmsMessage = evt.getJMSMessage();
/*      */ 
/*  575 */     AsyncMessage flexMessage = convertToFlexMessage(jmsMessage, consumer);
/*  576 */     if (flexMessage != null)
/*      */     {
/*  578 */       MessagePerformanceUtils.markServerPostAdapterExternalTime(flexMessage);
/*  579 */       ((MessageService)getDestination().getService()).serviceMessageFromAdapter(flexMessage, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeConsumer(String clientId)
/*      */   {
/*  593 */     MessageException messageEx = new MessageException();
/*  594 */     messageEx.setMessage(10821);
/*  595 */     removeConsumer(clientId, true, true, messageEx.createErrorMessage());
/*      */   }
/*      */ 
/*      */   protected void removeConsumer(String clientId, boolean unsubscribe, boolean invalidate, ErrorMessage invalidateMessage)
/*      */   {
/*  616 */     JMSConsumer consumer = null;
/*  617 */     if (this.topicConsumers.containsKey(clientId))
/*  618 */       consumer = (JMSConsumer)this.topicConsumers.get(clientId);
/*  619 */     else if (this.queueConsumers.containsKey(clientId)) {
/*  620 */       consumer = (JMSConsumer)this.queueConsumers.get(clientId);
/*      */     }
/*  622 */     removeConsumer(consumer, unsubscribe, invalidate, invalidateMessage);
/*      */   }
/*      */ 
/*      */   protected void removeConsumer(JMSConsumer consumer, boolean unsubscribe, boolean invalidate, ErrorMessage invalidateMessage)
/*      */   {
/*  637 */     if (consumer == null) {
/*  638 */       return;
/*      */     }
/*  640 */     String clientId = (String)this.consumerToClientId.get(consumer);
/*  641 */     if (clientId == null) {
/*  642 */       return;
/*      */     }
/*  644 */     if (Log.isInfo())
/*      */     {
/*  646 */       String logMessage = "JMS consumer for JMS destination '" + consumer.getDestinationJndiName() + "' is being removed from the JMS adapter";
/*      */ 
/*  649 */       if (invalidateMessage != null) {
/*  650 */         logMessage = logMessage + " due to the following error: " + invalidateMessage.faultString;
/*      */       }
/*  652 */       Log.getLogger("Service.Message.JMS").info(logMessage);
/*      */     }
/*      */ 
/*  655 */     consumer.removeJMSExceptionListener(this);
/*  656 */     consumer.removeJMSMessageListener(this);
/*  657 */     consumer.stop(unsubscribe);
/*  658 */     if (invalidate)
/*  659 */       invalidateMessageClient(consumer, invalidateMessage);
/*  660 */     if ((consumer instanceof JMSTopicConsumer))
/*  661 */       this.topicConsumers.remove(clientId);
/*      */     else
/*  663 */       this.queueConsumers.remove(clientId);
/*  664 */     this.consumerToClientId.remove(consumer);
/*      */   }
/*      */ 
/*      */   protected void setupAdapterControl(Destination destination)
/*      */   {
/*  675 */     this.controller = new JMSAdapterControl(this, destination.getControl());
/*  676 */     this.controller.register();
/*  677 */     setControl(this.controller);
/*      */   }
/*      */ 
/*      */   private MessageReceiver buildMessageReceiver(JMSConsumer consumer)
/*      */   {
/*  687 */     JMSSettings.DeliverySettings deliverySettings = this.settings.getDeliverySettings();
/*  688 */     if (deliverySettings.getMode().equals("async"))
/*  689 */       return new AsyncMessageReceiver(consumer);
/*  690 */     SyncMessageReceiver syncMessageReceiver = new SyncMessageReceiver(consumer);
/*  691 */     syncMessageReceiver.setSyncReceiveIntervalMillis(deliverySettings.getSyncReceiveIntervalMillis());
/*  692 */     syncMessageReceiver.setSyncReceiveWaitMillis(deliverySettings.getSyncReceiveWaitMillis());
/*  693 */     return syncMessageReceiver;
/*      */   }
/*      */ 
/*      */   private String buildSubscriptionName(Object clientId)
/*      */   {
/*  702 */     return "FlexClient_" + clientId.toString();
/*      */   }
/*      */ 
/*      */   private AsyncMessage convertToFlexMessage(javax.jms.Message jmsMessage, JMSConsumer consumer)
/*      */   {
/*  713 */     AsyncMessage flexMessage = null;
/*  714 */     flexMessage = new AsyncMessage();
/*      */ 
/*  716 */     String clientId = (String)this.consumerToClientId.get(consumer);
/*  717 */     if (clientId == null)
/*      */     {
/*  719 */       if (Log.isWarn()) {
/*  720 */         Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered a null clientId during JMS to Flex message conversion");
/*      */       }
/*  722 */       return null;
/*      */     }
/*  724 */     flexMessage.setClientId(clientId);
/*      */ 
/*  727 */     flexMessage.setDestination(getDestination().getId());
/*      */     try
/*      */     {
/*  732 */       flexMessage.setMessageId(jmsMessage.getJMSMessageID());
/*      */     }
/*      */     catch (JMSException jmsEx)
/*      */     {
/*  736 */       if (Log.isWarn()) {
/*  737 */         Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS message id during JMS to Flex message conversion: " + jmsEx.getMessage());
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  743 */       flexMessage.setTimestamp(jmsMessage.getJMSTimestamp());
/*      */     }
/*      */     catch (JMSException jmsEx)
/*      */     {
/*  747 */       if (Log.isWarn()) {
/*  748 */         Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS timestamp during JMS to Flex message conversion: " + jmsEx.getMessage());
/*      */       }
/*      */     }
/*      */ 
/*  752 */     if (this.settings.isPreserveJMSHeaders())
/*      */     {
/*      */       try
/*      */       {
/*  758 */         flexMessage.setHeader("JMSCorrelationID", jmsMessage.getJMSCorrelationID());
/*  759 */         flexMessage.setHeader("JMSDeliveryMode", Integer.toString(jmsMessage.getJMSDeliveryMode()));
/*  760 */         flexMessage.setHeader("JMSDestination", jmsMessage.getJMSDestination().toString());
/*  761 */         flexMessage.setHeader("JMSExpiration", Long.toString(jmsMessage.getJMSExpiration()));
/*  762 */         flexMessage.setHeader("JMSPriority", Integer.toString(jmsMessage.getJMSPriority()));
/*  763 */         flexMessage.setHeader("JMSRedelivered", Boolean.toString(jmsMessage.getJMSRedelivered()));
/*  764 */         flexMessage.setHeader("JMSReplyTo", jmsMessage.getJMSReplyTo());
/*  765 */         flexMessage.setHeader("JMSType", jmsMessage.getJMSType());
/*      */       }
/*      */       catch (JMSException jmsEx)
/*      */       {
/*  770 */         if (Log.isWarn()) {
/*  771 */           Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS headers during JMS to Flex conversion: " + jmsEx.getMessage());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  781 */     MessagePerformanceInfo mpi = null;
/*      */     try
/*      */     {
/*  785 */       for (Enumeration propEnum = jmsMessage.getPropertyNames(); propEnum.hasMoreElements(); )
/*      */       {
/*  787 */         String propName = (String)propEnum.nextElement();
/*      */         try
/*      */         {
/*  790 */           Object propValue = jmsMessage.getObjectProperty(propName);
/*  791 */           if (propName.startsWith("DSMPII"))
/*      */           {
/*  793 */             if (mpi == null)
/*  794 */               mpi = new MessagePerformanceInfo();
/*  795 */             propName = propName.substring("DSMPII".length());
/*      */             try
/*      */             {
/*  799 */               Field field = mpi.getClass().getField(propName);
/*  800 */               field.set(mpi, propValue);
/*      */             }
/*      */             catch (Exception ignore)
/*      */             {
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  809 */             flexMessage.setHeader(propName, propValue);
/*      */           }
/*      */         }
/*      */         catch (JMSException jmsEx)
/*      */         {
/*  814 */           if (Log.isWarn()) {
/*  815 */             Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS properties during JMS to Flex conversion: " + jmsEx.getMessage());
/*      */           }
/*      */         }
/*      */       }
/*  819 */       if (mpi != null)
/*  820 */         flexMessage.setHeader("DSMPII", mpi);
/*      */     }
/*      */     catch (JMSException jmsEx)
/*      */     {
/*  824 */       if (Log.isWarn()) {
/*  825 */         Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS properties during JMS to Flex conversion: " + jmsEx.getMessage());
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  831 */       if ((jmsMessage instanceof TextMessage))
/*      */       {
/*  833 */         TextMessage textMessage = (TextMessage)jmsMessage;
/*  834 */         flexMessage.setBody(textMessage.getText());
/*      */       }
/*  836 */       else if ((jmsMessage instanceof ObjectMessage))
/*      */       {
/*  838 */         ObjectMessage objMessage = (ObjectMessage)jmsMessage;
/*  839 */         flexMessage.setBody(objMessage.getObject());
/*      */       }
/*      */     }
/*      */     catch (JMSException jmsEx)
/*      */     {
/*  844 */       if (Log.isWarn()) {
/*  845 */         Log.getLogger("Service.Message.JMS").warn("JMSAdapter encountered an error while retrieving JMS message body during JMS to Flex conversion: " + jmsEx.getMessage());
/*      */       }
/*      */     }
/*  848 */     return flexMessage;
/*      */   }
/*      */ 
/*      */   private void invalidateMessageClient(JMSConsumer consumer, flex.messaging.messages.Message message)
/*      */   {
/*  862 */     String clientId = (String)this.consumerToClientId.get(consumer);
/*  863 */     if ((clientId != null) && (this.messageClients.containsKey(clientId)))
/*      */     {
/*  865 */       MessageClient messageClient = (MessageClient)this.messageClients.get(clientId);
/*      */ 
/*  867 */       if (Log.isInfo()) {
/*  868 */         Log.getLogger("Service.Message.JMS").info("The corresponding MessageClient for JMS consumer for JMS destination '" + consumer.getDestinationJndiName() + "' is being invalidated");
/*      */       }
/*      */ 
/*  871 */       messageClient.invalidate(message);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void jms(ConfigMap properties)
/*      */   {
/*  880 */     ConfigMap jms = properties.getPropertyAsMap("jms", null);
/*  881 */     if (jms != null)
/*      */     {
/*  883 */       String destType = jms.getPropertyAsString("destination-type", "topic");
/*  884 */       this.settings.setDestinationType(destType);
/*      */ 
/*  886 */       String msgType = jms.getPropertyAsString("message-type", null);
/*  887 */       this.settings.setMessageType(msgType);
/*      */ 
/*  889 */       String factory = jms.getPropertyAsString("connection-factory", null);
/*  890 */       this.settings.setConnectionFactory(factory);
/*      */ 
/*  892 */       ConfigMap connectionCredentials = jms.getPropertyAsMap("connection-credentials", null);
/*  893 */       if (connectionCredentials != null)
/*      */       {
/*  895 */         String username = connectionCredentials.getPropertyAsString("username", null);
/*  896 */         this.settings.setConnectionUsername(username);
/*  897 */         String password = connectionCredentials.getPropertyAsString("password", null);
/*  898 */         this.settings.setConnectionPassword(password);
/*      */       }
/*      */ 
/*  901 */       ConfigMap deliverySettings = jms.getPropertyAsMap("delivery-settings", null);
/*  902 */       if (deliverySettings != null)
/*      */       {
/*  905 */         JMSSettings.DeliverySettings ds = this.settings.getDeliverySettings();
/*      */ 
/*  907 */         String mode = deliverySettings.getPropertyAsString("mode", "sync");
/*  908 */         ds.setMode(mode);
/*      */ 
/*  910 */         long receiveIntervalMillis = deliverySettings.getPropertyAsLong("sync-receive-interval-millis", 100L);
/*  911 */         ds.setSyncReceiveIntervalMillis(receiveIntervalMillis);
/*      */ 
/*  913 */         long receiveWaitMillis = deliverySettings.getPropertyAsLong("sync-receive-wait-millis", 0L);
/*  914 */         ds.setSyncReceiveWaitMillis(receiveWaitMillis);
/*      */       }
/*      */ 
/*  917 */       String destJNDI = jms.getPropertyAsString("destination-jndi-name", null);
/*  918 */       this.settings.setDestinationJNDIName(destJNDI);
/*      */ 
/*  920 */       String dest = jms.getPropertyAsString("destination-name", null);
/*  921 */       if ((dest != null) && (Log.isWarn())) {
/*  922 */         Log.getLogger("Service.Message.JMS").warn("The <destination-name> configuration option is deprecated and non-functional. Please remove this from your configuration file.");
/*      */       }
/*  924 */       boolean durable = (getDestination() instanceof MessageDestination) ? ((MessageDestination)getDestination()).getServerSettings().isDurable() : false;
/*      */ 
/*  926 */       this.settings.setDurableConsumers(durable);
/*      */ 
/*  928 */       String deliveryMode = jms.getPropertyAsString("delivery-mode", null);
/*  929 */       this.settings.setDeliveryMode(deliveryMode);
/*      */ 
/*  931 */       boolean preserveJMSHeaders = jms.getPropertyAsBoolean("preserve-jms-headers", this.settings.isPreserveJMSHeaders());
/*  932 */       this.settings.setPreserveJMSHeaders(preserveJMSHeaders);
/*      */ 
/*  934 */       String defPriority = jms.getPropertyAsString("message-priority", null);
/*  935 */       if ((defPriority != null) && (!defPriority.equalsIgnoreCase("default-priority")))
/*      */       {
/*  937 */         int priority = jms.getPropertyAsInt("message-priority", this.settings.getMessagePriority());
/*  938 */         this.settings.setMessagePriority(priority);
/*      */       }
/*      */ 
/*  941 */       String ackMode = jms.getPropertyAsString("acknowledge-mode", "auto_acknowledge");
/*  942 */       this.settings.setAcknowledgeMode(ackMode);
/*      */ 
/*  944 */       boolean transMode = jms.getPropertyAsBoolean("transacted-sessions", false);
/*  945 */       if ((transMode) && (Log.isWarn())) {
/*  946 */         Log.getLogger("Service.Message.JMS").warn("The <transacted-sessions> configuration option is deprecated and non-functional. Please remove this from your configuration file.");
/*      */       }
/*  948 */       int maxProducers = jms.getPropertyAsInt("max-producers", 1);
/*  949 */       this.settings.setMaxProducers(maxProducers);
/*      */ 
/*  952 */       ConfigMap env = jms.getPropertyAsMap("initial-context-environment", null);
/*  953 */       if (env != null)
/*      */       {
/*  955 */         List props = env.getPropertyAsList("property", null);
/*  956 */         if (props != null)
/*      */         {
/*  958 */           Class contextClass = Context.class;
/*  959 */           Hashtable envProps = new Hashtable();
/*  960 */           for (Iterator iter = props.iterator(); iter.hasNext(); )
/*      */           {
/*  962 */             Object prop = iter.next();
/*  963 */             if ((prop instanceof ConfigMap))
/*      */             {
/*  965 */               ConfigMap pair = (ConfigMap)prop;
/*  966 */               String name = pair.getProperty("name");
/*  967 */               String value = pair.getProperty("value");
/*  968 */               if ((name == null) || (value == null))
/*      */               {
/*  971 */                 MessageException messageEx = new MessageException();
/*  972 */                 messageEx.setMessage(10800, new Object[] { getDestination().getId() });
/*  973 */                 throw messageEx;
/*      */               }
/*      */ 
/*  977 */               if (name.startsWith("Context."))
/*      */               {
/*  979 */                 String fieldName = name.substring(name.indexOf(".") + 1);
/*  980 */                 Field field = null;
/*      */                 try
/*      */                 {
/*  983 */                   field = contextClass.getDeclaredField(fieldName);
/*      */                 }
/*      */                 catch (NoSuchFieldException nsfe)
/*      */                 {
/*  988 */                   MessageException messageEx = new MessageException();
/*  989 */                   messageEx.setMessage(10801, new Object[] { getDestination().getId(), fieldName });
/*  990 */                   throw messageEx;
/*      */                 }
/*  992 */                 String fieldValue = null;
/*      */                 try
/*      */                 {
/*  995 */                   fieldValue = (String)field.get(null);
/*      */                 }
/*      */                 catch (IllegalAccessException iae)
/*      */                 {
/* 1000 */                   MessageException messageEx = new MessageException();
/* 1001 */                   messageEx.setMessage(10802, new Object[] { getDestination().getId(), fieldName });
/* 1002 */                   throw messageEx;
/*      */                 }
/* 1004 */                 envProps.put(fieldValue, value);
/*      */               }
/*      */               else
/*      */               {
/* 1008 */                 envProps.put(name, value);
/*      */               }
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/* 1014 */               MessageException messageEx = new MessageException();
/* 1015 */               messageEx.setMessage(10800, new Object[] { getDestination().getId() });
/* 1016 */               throw messageEx;
/*      */             }
/*      */           }
/* 1019 */           this.settings.setInitialContextEnvironment(envProps);
/*      */         }
/*      */         else
/*      */         {
/* 1024 */           MessageException messageEx = new MessageException();
/* 1025 */           messageEx.setMessage(10803, new Object[] { getDestination().getId() });
/* 1026 */           throw messageEx;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopConsumers(Collection consumers)
/*      */   {
/* 1034 */     Iterator itr = consumers.iterator();
/* 1035 */     while (itr.hasNext())
/*      */     {
/* 1037 */       JMSConsumer consumer = (JMSConsumer)itr.next();
/*      */ 
/* 1039 */       MessageException me = new MessageException();
/* 1040 */       me.setMessage(10822, new Object[] { consumer.getDestinationJndiName() });
/* 1041 */       consumer.stop(true);
/* 1042 */       invalidateMessageClient(consumer, me.createErrorMessage());
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSAdapter
 * JD-Core Version:    0.6.0
 */
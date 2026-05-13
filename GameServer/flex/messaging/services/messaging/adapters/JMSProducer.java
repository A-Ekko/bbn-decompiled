/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.MessagePerformanceUtils;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.MessageProducer;
/*     */ import javax.naming.NamingException;
/*     */ 
/*     */ public abstract class JMSProducer extends JMSProxy
/*     */ {
/*     */   protected MessageProducer producer;
/*     */   protected int deliveryMode;
/*     */   protected int messagePriority;
/*     */   protected String messageType;
/*     */ 
/*     */   public JMSProducer()
/*     */   {
/*  60 */     this.deliveryMode = 2;
/*  61 */     this.messagePriority = 4;
/*     */   }
/*     */ 
/*     */   public void initialize(JMSSettings settings)
/*     */   {
/*  77 */     super.initialize(settings);
/*     */ 
/*  79 */     String deliveryString = settings.getDeliveryMode();
/*  80 */     if (deliveryString.equals("default_delivery_mode"))
/*  81 */       this.deliveryMode = 2;
/*  82 */     else if (deliveryString.equals("persistent"))
/*  83 */       this.deliveryMode = 2;
/*  84 */     else if (deliveryString.equals("non_persistent")) {
/*  85 */       this.deliveryMode = 1;
/*     */     }
/*  87 */     this.messagePriority = settings.getMessagePriority();
/*  88 */     this.messageType = settings.getMessageType();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/*  98 */     super.validate();
/*     */ 
/* 100 */     if ((this.messageType == null) || ((!this.messageType.equals("javax.jms.TextMessage")) && (!this.messageType.equals("javax.jms.ObjectMessage"))))
/*     */     {
/* 104 */       ConfigurationException ce = new ConfigurationException();
/* 105 */       ce.setMessage(10811, new Object[] { this.messageType });
/* 106 */       throw ce;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */     throws NamingException, JMSException
/*     */   {
/* 115 */     super.start();
/*     */ 
/* 117 */     if (Log.isInfo())
/* 118 */       Log.getLogger("Service.Message.JMS").info("JMS producer for JMS destination '" + this.destinationJndiName + "' is starting.");
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 129 */     if (Log.isInfo()) {
/* 130 */       Log.getLogger("Service.Message.JMS").info("JMS producer for JMS destination '" + this.destinationJndiName + "' is stopping.");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 135 */       if (this.producer != null)
/* 136 */         this.producer.close();
/*     */     }
/*     */     catch (JMSException e)
/*     */     {
/* 140 */       if (Log.isWarn()) {
/* 141 */         Log.getLogger("Service.Message.JMS").warn("JMS producer for JMS destination '" + this.destinationJndiName + "' received an error while closing" + " its underlying MessageProducer: " + e.getMessage());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 146 */     super.stop();
/*     */   }
/*     */ 
/*     */   public int getDeliveryMode()
/*     */   {
/* 162 */     return this.deliveryMode;
/*     */   }
/*     */ 
/*     */   public void setDeliveryMode(int deliveryMode)
/*     */   {
/* 174 */     if ((deliveryMode == 2) || (deliveryMode == 1) || (deliveryMode == 2))
/*     */     {
/* 177 */       this.deliveryMode = deliveryMode;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMessagePriority()
/*     */   {
/* 187 */     return this.messagePriority;
/*     */   }
/*     */ 
/*     */   public void setMessagePriority(int messagePriority)
/*     */   {
/* 198 */     this.messagePriority = messagePriority;
/*     */   }
/*     */ 
/*     */   public String getMessageType()
/*     */   {
/* 208 */     return this.messageType;
/*     */   }
/*     */ 
/*     */   public void setMessageType(String messageType)
/*     */   {
/* 220 */     this.messageType = messageType;
/*     */   }
/*     */ 
/*     */   protected void copyHeadersToProperties(Map properties, javax.jms.Message message)
/*     */     throws JMSException
/*     */   {
/* 232 */     for (Iterator iter = properties.keySet().iterator(); iter.hasNext(); )
/*     */     {
/* 234 */       String propName = (String)iter.next();
/* 235 */       Object propValue = properties.get(propName);
/*     */ 
/* 238 */       if (!propName.equals("timeToLive"))
/*     */       {
/* 244 */         if (propName.equals("DSMPII"))
/*     */         {
/* 246 */           Field[] fields = propValue.getClass().getFields();
/* 247 */           for (int i = 0; i < fields.length; i++)
/*     */           {
/* 249 */             Field field = fields[i];
/*     */ 
/* 253 */             String mpiPropertyName = "DSMPII" + field.getName();
/* 254 */             Object mpiPropertyValue = null;
/*     */             try
/*     */             {
/* 257 */               mpiPropertyValue = field.get(propValue);
/* 258 */               message.setObjectProperty(mpiPropertyName, mpiPropertyValue);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 262 */               if (Log.isWarn()) {
/* 263 */                 Log.getLogger("Service.Message.JMS").warn("JMSProducer could not retrieve the value of MessagePerformanceUtils property '" + propValue + "' from the Flex message, therefore it will not be set on the JMS message.");
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 268 */         else if (propValue != null)
/*     */         {
/* 270 */           message.setObjectProperty(propName, propValue);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected long getTimeToLive(Map properties) throws JMSException
/*     */   {
/* 278 */     long timeToLive = this.producer.getTimeToLive();
/* 279 */     if (properties.containsKey("timeToLive"))
/*     */     {
/* 281 */       long l = ((Long)properties.get("timeToLive")).longValue();
/* 282 */       if (l != 0L)
/*     */       {
/* 286 */         timeToLive = l;
/*     */       }
/*     */     }
/* 289 */     return timeToLive;
/*     */   }
/*     */ 
/*     */   void sendMessage(flex.messaging.messages.Message flexMessage) throws JMSException
/*     */   {
/* 294 */     if (this.messageType.equals("javax.jms.TextMessage"))
/*     */     {
/* 296 */       MessagePerformanceUtils.markServerPreAdapterExternalTime(flexMessage);
/* 297 */       sendTextMessage(flexMessage.getBody().toString(), flexMessage.getHeaders());
/*     */     }
/* 299 */     else if (this.messageType.equals("javax.jms.ObjectMessage"))
/*     */     {
/*     */       try
/*     */       {
/* 303 */         MessagePerformanceUtils.markServerPreAdapterExternalTime(flexMessage);
/* 304 */         sendObjectMessage((Serializable)flexMessage.getBody(), flexMessage.getHeaders());
/*     */       }
/*     */       catch (ClassCastException ce)
/*     */       {
/* 309 */         MessageException me = new MessageException();
/* 310 */         me.setMessage(10810);
/* 311 */         throw me;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract void sendObjectMessage(Serializable paramSerializable, Map paramMap)
/*     */     throws JMSException;
/*     */ 
/*     */   abstract void sendTextMessage(String paramString, Map paramMap)
/*     */     throws JMSException;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSProducer
 * JD-Core Version:    0.6.0
 */
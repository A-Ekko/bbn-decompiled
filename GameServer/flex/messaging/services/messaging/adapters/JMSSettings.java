/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import java.util.Hashtable;
/*     */ 
/*     */ public class JMSSettings
/*     */ {
/*     */   private String acknowledgeMode;
/*     */   private String connectionFactory;
/*     */   private String connectionUsername;
/*     */   private String connectionPassword;
/*     */   private String deliveryMode;
/*     */   private String destinationJNDIName;
/*     */   private DeliverySettings deliverySettings;
/*     */   private String destinationType;
/*     */   private boolean durableConsumers;
/*     */   private Hashtable initialContextEnvironment;
/*     */   private int maxProducers;
/*     */   private int messagePriority;
/*     */   private String messageType;
/*     */   private boolean preserveJMSHeaders;
/*     */ 
/*     */   public JMSSettings()
/*     */   {
/*  53 */     this.acknowledgeMode = "auto_acknowledge";
/*  54 */     this.deliveryMode = "default_delivery_mode";
/*  55 */     this.destinationType = "topic";
/*  56 */     this.deliverySettings = new DeliverySettings();
/*  57 */     this.maxProducers = 1;
/*  58 */     this.messagePriority = 4;
/*  59 */     this.preserveJMSHeaders = true;
/*     */   }
/*     */ 
/*     */   public String getAcknowledgeMode()
/*     */   {
/*  69 */     return this.acknowledgeMode;
/*     */   }
/*     */ 
/*     */   public void setAcknowledgeMode(String mode)
/*     */   {
/*  86 */     if (mode == null)
/*     */     {
/*  88 */       this.acknowledgeMode = "auto_acknowledge";
/*  89 */       return;
/*     */     }
/*     */ 
/*  92 */     mode = mode.toLowerCase();
/*     */ 
/*  94 */     if ((!mode.equals("auto_acknowledge")) && (!mode.equals("dups_ok_acknowledge")) && (!mode.equals("client_acknowledge")))
/*     */     {
/*  99 */       ConfigurationException ce = new ConfigurationException();
/* 100 */       ce.setMessage(10808, new Object[] { mode });
/* 101 */       throw ce;
/*     */     }
/* 103 */     this.acknowledgeMode = mode;
/*     */   }
/*     */ 
/*     */   public String getConnectionFactory()
/*     */   {
/* 113 */     return this.connectionFactory;
/*     */   }
/*     */ 
/*     */   public void setConnectionFactory(String factory)
/*     */   {
/* 125 */     if (factory == null)
/*     */     {
/* 128 */       ConfigurationException ce = new ConfigurationException();
/* 129 */       ce.setMessage(10804);
/* 130 */       throw ce;
/*     */     }
/* 132 */     this.connectionFactory = factory;
/*     */   }
/*     */ 
/*     */   public String getConnectionUsername()
/*     */   {
/* 142 */     return this.connectionUsername;
/*     */   }
/*     */ 
/*     */   public void setConnectionUsername(String connectionUsername)
/*     */   {
/* 154 */     this.connectionUsername = connectionUsername;
/*     */   }
/*     */ 
/*     */   public String getConnectionPassword()
/*     */   {
/* 164 */     return this.connectionPassword;
/*     */   }
/*     */ 
/*     */   public void setConnectionPassword(String connectionPassword)
/*     */   {
/* 176 */     this.connectionPassword = connectionPassword;
/*     */   }
/*     */ 
/*     */   public String getDeliveryMode()
/*     */   {
/* 186 */     return this.deliveryMode;
/*     */   }
/*     */ 
/*     */   public void setDeliveryMode(String mode)
/*     */   {
/* 198 */     if (mode == null)
/*     */     {
/* 200 */       this.deliveryMode = "default_delivery_mode";
/* 201 */       return;
/*     */     }
/*     */ 
/* 204 */     mode = mode.toLowerCase();
/*     */ 
/* 206 */     if ((!mode.equals("default_delivery_mode")) && (!mode.equals("persistent")) && (!mode.equals("non_persistent")))
/*     */     {
/* 211 */       ConfigurationException ce = new ConfigurationException();
/* 212 */       ce.setMessage(10809, new Object[] { mode });
/* 213 */       throw ce;
/*     */     }
/* 215 */     this.deliveryMode = mode;
/*     */   }
/*     */ 
/*     */   public DeliverySettings getDeliverySettings()
/*     */   {
/* 225 */     return this.deliverySettings;
/*     */   }
/*     */ 
/*     */   public void setDeliverySettings(DeliverySettings deliverySettings)
/*     */   {
/* 237 */     this.deliverySettings = deliverySettings;
/*     */   }
/*     */ 
/*     */   public String getDestinationJNDIName()
/*     */   {
/* 247 */     return this.destinationJNDIName;
/*     */   }
/*     */ 
/*     */   public void setDestinationJNDIName(String name)
/*     */   {
/* 258 */     if (name == null)
/*     */     {
/* 261 */       ConfigurationException ce = new ConfigurationException();
/* 262 */       ce.setMessage(10807);
/* 263 */       throw ce;
/*     */     }
/* 265 */     this.destinationJNDIName = name;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public String getDestinationName()
/*     */   {
/* 273 */     return null;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setDestinationName(String name)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String getDestinationType()
/*     */   {
/* 291 */     return this.destinationType;
/*     */   }
/*     */ 
/*     */   public void setDestinationType(String type)
/*     */   {
/* 303 */     if (type == null)
/*     */     {
/* 305 */       this.destinationType = "topic";
/* 306 */       return;
/*     */     }
/*     */ 
/* 309 */     type = type.toLowerCase();
/*     */ 
/* 311 */     if ((!type.equals("topic")) && (!type.equals("queue")))
/*     */     {
/* 314 */       ConfigurationException ce = new ConfigurationException();
/* 315 */       ce.setMessage(10805);
/* 316 */       throw ce;
/*     */     }
/* 318 */     this.destinationType = type;
/*     */   }
/*     */ 
/*     */   public boolean useDurableConsumers()
/*     */   {
/* 329 */     return this.durableConsumers;
/*     */   }
/*     */ 
/*     */   public void setDurableConsumers(boolean durable)
/*     */   {
/* 340 */     this.durableConsumers = durable;
/*     */   }
/*     */ 
/*     */   public Hashtable getInitialContextEnvironment()
/*     */   {
/* 350 */     return this.initialContextEnvironment;
/*     */   }
/*     */ 
/*     */   public void setInitialContextEnvironment(Hashtable env)
/*     */   {
/* 361 */     this.initialContextEnvironment = env;
/*     */   }
/*     */ 
/*     */   public int getMaxProducers()
/*     */   {
/* 371 */     return this.maxProducers;
/*     */   }
/*     */ 
/*     */   public void setMaxProducers(int value)
/*     */   {
/* 385 */     if (value < 1)
/* 386 */       value = 1;
/* 387 */     this.maxProducers = value;
/*     */   }
/*     */ 
/*     */   public int getMessagePriority()
/*     */   {
/* 397 */     return this.messagePriority;
/*     */   }
/*     */ 
/*     */   public void setMessagePriority(int priority)
/*     */   {
/* 409 */     this.messagePriority = priority;
/*     */   }
/*     */ 
/*     */   public String getMessageType()
/*     */   {
/* 419 */     return this.messageType;
/*     */   }
/*     */ 
/*     */   public void setMessageType(String type)
/*     */   {
/* 432 */     if ((type == null) || ((!type.equals("javax.jms.TextMessage")) && (!type.equals("javax.jms.ObjectMessage"))))
/*     */     {
/* 436 */       ConfigurationException ce = new ConfigurationException();
/* 437 */       ce.setMessage(10811, new Object[] { type });
/* 438 */       throw ce;
/*     */     }
/* 440 */     this.messageType = type;
/*     */   }
/*     */ 
/*     */   public boolean isPreserveJMSHeaders()
/*     */   {
/* 450 */     return this.preserveJMSHeaders;
/*     */   }
/*     */ 
/*     */   public void setPreserveJMSHeaders(boolean preserveJMSHeaders)
/*     */   {
/* 461 */     this.preserveJMSHeaders = preserveJMSHeaders;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public boolean isTransactedSessions()
/*     */   {
/* 469 */     return false;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setTransactedSessions(boolean mode)
/*     */   {
/*     */   }
/*     */ 
/*     */   public static class DeliverySettings
/*     */   {
/*     */     private String mode;
/*     */     private long syncReceiveIntervalMillis;
/*     */     private long syncReceiveWaitMillis;
/*     */ 
/*     */     public DeliverySettings()
/*     */     {
/* 501 */       this.mode = "sync";
/* 502 */       this.syncReceiveIntervalMillis = 100L;
/* 503 */       this.syncReceiveWaitMillis = 0L;
/*     */     }
/*     */ 
/*     */     public String getMode()
/*     */     {
/* 513 */       return this.mode;
/*     */     }
/*     */ 
/*     */     public void setMode(String mode)
/*     */     {
/* 523 */       if (mode == null)
/*     */       {
/* 525 */         mode = "sync";
/* 526 */         return;
/*     */       }
/*     */ 
/* 529 */       mode = mode.toLowerCase();
/*     */ 
/* 531 */       if ((!mode.equals("async")) && (!mode.equals("sync")))
/*     */       {
/* 534 */         ConfigurationException ce = new ConfigurationException();
/* 535 */         ce.setMessage(10817, new Object[] { mode });
/* 536 */         throw ce;
/*     */       }
/* 538 */       this.mode = mode;
/*     */     }
/*     */ 
/*     */     public long getSyncReceiveIntervalMillis()
/*     */     {
/* 548 */       return this.syncReceiveIntervalMillis;
/*     */     }
/*     */ 
/*     */     public void setSyncReceiveIntervalMillis(long syncReceiveIntervalMillis)
/*     */     {
/* 560 */       if (syncReceiveIntervalMillis < 1L)
/* 561 */         syncReceiveIntervalMillis = 100L;
/* 562 */       this.syncReceiveIntervalMillis = syncReceiveIntervalMillis;
/*     */     }
/*     */ 
/*     */     public long getSyncReceiveWaitMillis()
/*     */     {
/* 572 */       return this.syncReceiveWaitMillis;
/*     */     }
/*     */ 
/*     */     public void setSyncReceiveWaitMillis(long syncReceiveWaitMillis)
/*     */     {
/* 585 */       if (syncReceiveWaitMillis < -1L)
/* 586 */         syncReceiveWaitMillis = 0L;
/* 587 */       this.syncReceiveWaitMillis = syncReceiveWaitMillis;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.JMSSettings
 * JD-Core Version:    0.6.0
 */
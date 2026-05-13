/*     */ package flex.messaging.services;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class AbstractBootstrapService
/*     */   implements Service
/*     */ {
/*     */   private static final int NULL_COMPONENT_PROPERTY = 11116;
/*     */   protected String id;
/*     */   protected MessageBroker broker;
/*     */ 
/*     */   public String getId()
/*     */   {
/*  52 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*  62 */     String oldId = getId();
/*     */ 
/*  64 */     if (id == null)
/*     */     {
/*  67 */       ConfigurationException ce = new ConfigurationException();
/*  68 */       ce.setMessage(11116, new Object[] { "id" });
/*  69 */       throw ce;
/*     */     }
/*     */ 
/*  72 */     this.id = id;
/*     */ 
/*  75 */     MessageBroker broker = getMessageBroker();
/*  76 */     if (broker != null)
/*     */     {
/*  79 */       broker.removeService(oldId);
/*  80 */       broker.addService(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageBroker getMessageBroker()
/*     */   {
/*  91 */     return this.broker;
/*     */   }
/*     */ 
/*     */   public void setMessageBroker(MessageBroker broker)
/*     */   {
/* 103 */     MessageBroker oldBroker = getMessageBroker();
/*     */ 
/* 105 */     this.broker = broker;
/*     */ 
/* 107 */     if (oldBroker != null)
/*     */     {
/* 109 */       oldBroker.removeService(getId());
/*     */     }
/*     */ 
/* 113 */     if (broker.getService(getId()) != this)
/* 114 */       broker.addService(this);
/*     */   }
/*     */ 
/*     */   public boolean isManaged()
/*     */   {
/* 124 */     return false;
/*     */   }
/*     */ 
/*     */   public void setManaged(boolean enableManagement)
/*     */   {
/*     */   }
/*     */ 
/*     */   public abstract void initialize(String paramString, ConfigMap paramConfigMap);
/*     */ 
/*     */   public abstract void start();
/*     */ 
/*     */   public abstract void stop();
/*     */ 
/*     */   public ConfigMap describeService(Endpoint endpoint)
/*     */   {
/* 161 */     return null;
/*     */   }
/*     */ 
/*     */   public BaseControl getControl()
/*     */   {
/* 167 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void setControl(BaseControl control)
/*     */   {
/* 173 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void addDefaultChannel(String id)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDefaultChannels(List ids)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean removeDefaultChannel(String id)
/*     */   {
/* 191 */     return false;
/*     */   }
/*     */ 
/*     */   public void addDestination(Destination destination)
/*     */   {
/* 197 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Destination createDestination(String destId)
/*     */   {
/* 203 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Destination removeDestination(String id)
/*     */   {
/* 209 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public String getDefaultAdapter()
/*     */   {
/* 215 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void setDefaultAdapter(String id)
/*     */   {
/* 221 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public List getDefaultChannels()
/*     */   {
/* 227 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Destination getDestination(Message message)
/*     */   {
/* 233 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Destination getDestination(String id)
/*     */   {
/* 239 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Map getDestinations()
/*     */   {
/* 245 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Map getRegisteredAdapters()
/*     */   {
/* 251 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean isStarted()
/*     */   {
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSupportedMessage(Message message)
/*     */   {
/* 263 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSupportedMessageType(String messageClassName)
/*     */   {
/* 269 */     return false;
/*     */   }
/*     */ 
/*     */   public String registerAdapter(String id, String className)
/*     */   {
/* 275 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public String unregisterAdapter(String id)
/*     */   {
/* 281 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Object serviceCommand(CommandMessage message)
/*     */   {
/* 287 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Object serviceMessage(Message message)
/*     */   {
/* 293 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public List getMessageTypes()
/*     */   {
/* 299 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void addMessageType(String messageType)
/*     */   {
/* 305 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void setMessageTypes(List messageTypes)
/*     */   {
/* 311 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean removeMessageType(String messageType)
/*     */   {
/* 317 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.AbstractBootstrapService
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.services;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.MessageBrokerControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.cluster.ClusterManager;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class AbstractService extends ManageableComponent
/*     */   implements Service
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Service.General";
/*     */   public static final String LOG_CATEGORY_STARTUP_DESTINATION = "Startup.Destination";
/*     */   protected static final int UNKNOWN_MESSAGE_TYPE = 10454;
/*     */   protected Map adapterClasses;
/*     */   protected String defaultAdapterId;
/*     */   protected List defaultChannels;
/*     */   protected Map destinations;
/*     */ 
/*     */   public AbstractService()
/*     */   {
/*  77 */     this(false);
/*     */   }
/*     */ 
/*     */   public AbstractService(boolean enableManagement)
/*     */   {
/*  88 */     super(enableManagement);
/*     */ 
/*  90 */     this.adapterClasses = new HashMap();
/*  91 */     this.destinations = new HashMap();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 106 */     if (isValid()) {
/* 107 */       return;
/*     */     }
/* 109 */     super.validate();
/*     */     Iterator iter;
/* 111 */     if (this.defaultChannels != null)
/*     */     {
/* 113 */       for (iter = this.defaultChannels.iterator(); iter.hasNext(); )
/*     */       {
/* 115 */         String id = (String)iter.next();
/* 116 */         if (!getMessageBroker().getChannelIds().contains(id))
/*     */         {
/* 118 */           iter.remove();
/* 119 */           if (Log.isWarn())
/*     */           {
/* 121 */             Log.getLogger(getLogCategory()).warn("Removing the Channel " + id + " from Destination " + getId() + "as MessageBroker does not know the channel");
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 129 */       this.defaultChannels = getMessageBroker().getDefaultChannels();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 141 */     if (isStarted())
/*     */     {
/* 144 */       startDestinations();
/* 145 */       return;
/*     */     }
/*     */ 
/* 149 */     MessageBroker broker = getMessageBroker();
/* 150 */     if (!broker.isStarted())
/*     */     {
/* 152 */       if (Log.isWarn())
/*     */       {
/* 154 */         Log.getLogger(getLogCategory()).warn("Service with id '{0}' cannot be started when the MessageBroker is not started.", new Object[] { getId() });
/*     */       }
/*     */ 
/* 158 */       return;
/*     */     }
/*     */ 
/* 162 */     if ((isManaged()) && (broker.isManaged()))
/*     */     {
/* 164 */       setupServiceControl(broker);
/* 165 */       MessageBrokerControl controller = (MessageBrokerControl)broker.getControl();
/* 166 */       if (getControl() != null) {
/* 167 */         controller.addService(getControl().getObjectName());
/*     */       }
/*     */     }
/* 170 */     super.start();
/*     */ 
/* 172 */     startDestinations();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 182 */     if (!isStarted())
/*     */     {
/* 184 */       return;
/*     */     }
/*     */ 
/* 187 */     stopDestinations();
/*     */ 
/* 189 */     super.stop();
/*     */ 
/* 192 */     if ((isManaged()) && (getMessageBroker().isManaged()))
/*     */     {
/* 194 */       if (getControl() != null)
/*     */       {
/* 196 */         getControl().unregister();
/* 197 */         setControl(null);
/*     */       }
/* 199 */       setManaged(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map getRegisteredAdapters()
/*     */   {
/* 216 */     return this.adapterClasses;
/*     */   }
/*     */ 
/*     */   public String registerAdapter(String id, String adapterClass)
/*     */   {
/* 228 */     return (String)this.adapterClasses.put(id, adapterClass);
/*     */   }
/*     */ 
/*     */   public String unregisterAdapter(String id)
/*     */   {
/* 240 */     if ((id != null) && (id.equals(this.defaultAdapterId))) {
/* 241 */       this.defaultAdapterId = null;
/*     */     }
/* 243 */     return (String)this.adapterClasses.remove(id);
/*     */   }
/*     */ 
/*     */   public String getDefaultAdapter()
/*     */   {
/* 254 */     return this.defaultAdapterId;
/*     */   }
/*     */ 
/*     */   public void setDefaultAdapter(String id)
/*     */   {
/* 264 */     if (this.adapterClasses.get(id) == null)
/*     */     {
/* 267 */       ConfigurationException ex = new ConfigurationException();
/* 268 */       ex.setMessage(11114, new Object[] { id, getId() });
/* 269 */       throw ex;
/*     */     }
/* 271 */     this.defaultAdapterId = id;
/*     */   }
/*     */ 
/*     */   public List getDefaultChannels()
/*     */   {
/* 279 */     return this.defaultChannels;
/*     */   }
/*     */ 
/*     */   public void addDefaultChannel(String id)
/*     */   {
/* 291 */     if (this.defaultChannels == null)
/* 292 */       this.defaultChannels = new ArrayList();
/* 293 */     else if (this.defaultChannels.contains(id)) {
/* 294 */       return;
/*     */     }
/* 296 */     if (isStarted())
/*     */     {
/* 298 */       List channelIds = getMessageBroker().getChannelIds();
/* 299 */       if ((channelIds == null) || (!channelIds.contains(id)))
/*     */       {
/* 302 */         if (Log.isWarn())
/*     */         {
/* 304 */           Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*     */         }
/*     */ 
/* 308 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 313 */     this.defaultChannels.add(id);
/*     */   }
/*     */ 
/*     */   public void setDefaultChannels(List ids)
/*     */   {
/*     */     List channelIds;
/*     */     Iterator iter;
/* 325 */     if ((ids != null) && (isStarted()))
/*     */     {
/* 327 */       channelIds = getMessageBroker().getChannelIds();
/* 328 */       for (iter = ids.iterator(); iter.hasNext(); )
/*     */       {
/* 330 */         String id = (String)iter.next();
/* 331 */         if ((channelIds == null) || (!channelIds.contains(id)))
/*     */         {
/* 333 */           iter.remove();
/* 334 */           if (Log.isWarn())
/*     */           {
/* 336 */             Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 344 */     this.defaultChannels = ids;
/*     */   }
/*     */ 
/*     */   public boolean removeDefaultChannel(String id)
/*     */   {
/* 355 */     if (this.defaultChannels == null)
/* 356 */       return false;
/* 357 */     return this.defaultChannels.remove(id);
/*     */   }
/*     */ 
/*     */   public Destination getDestination(Message message)
/*     */   {
/* 368 */     String id = message.getDestination();
/* 369 */     Destination result = getDestination(id);
/* 370 */     if (result == null)
/*     */     {
/* 372 */       throw new MessageException("No destination '" + id + "' exists in service " + getClass().getName());
/*     */     }
/*     */ 
/* 375 */     return result;
/*     */   }
/*     */ 
/*     */   public Destination getDestination(String id)
/*     */   {
/* 386 */     Destination result = (Destination)this.destinations.get(id);
/* 387 */     return result;
/*     */   }
/*     */ 
/*     */   public Map getDestinations()
/*     */   {
/* 397 */     return this.destinations;
/*     */   }
/*     */ 
/*     */   public Destination createDestination(String id)
/*     */   {
/* 411 */     Destination destination = new Destination();
/* 412 */     destination.setId(id);
/* 413 */     destination.setManaged(isManaged());
/* 414 */     destination.setService(this);
/*     */ 
/* 416 */     return destination;
/*     */   }
/*     */ 
/*     */   public void addDestination(Destination destination)
/*     */   {
/* 434 */     if (destination == null)
/*     */     {
/* 437 */       ConfigurationException ex = new ConfigurationException();
/* 438 */       ex.setMessage(11110, new Object[] { "Destination", "Service" });
/* 439 */       throw ex;
/*     */     }
/*     */ 
/* 442 */     String id = destination.getId();
/*     */ 
/* 444 */     if (id == null)
/*     */     {
/* 447 */       ConfigurationException ex = new ConfigurationException();
/* 448 */       ex.setMessage(11111, new Object[] { "Destination", "Service" });
/* 449 */       throw ex;
/*     */     }
/*     */ 
/* 452 */     if (getDestination(id) == destination)
/*     */     {
/* 454 */       return;
/*     */     }
/*     */ 
/* 459 */     getMessageBroker().registerDestination(id, getId());
/*     */ 
/* 461 */     this.destinations.put(id, destination);
/*     */ 
/* 463 */     if ((destination.getService() == null) || (destination.getService() != this))
/*     */     {
/* 465 */       destination.setService(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Destination removeDestination(String id)
/*     */   {
/* 478 */     Destination destination = (Destination)this.destinations.get(id);
/* 479 */     if (destination != null)
/*     */     {
/* 481 */       destination.stop();
/* 482 */       this.destinations.remove(id);
/* 483 */       getMessageBroker().unregisterDestination(id);
/*     */     }
/* 485 */     return destination;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 495 */     String oldId = getId();
/*     */ 
/* 497 */     super.setId(id);
/*     */ 
/* 500 */     MessageBroker broker = getMessageBroker();
/* 501 */     if (broker != null)
/*     */     {
/* 504 */       broker.removeService(oldId);
/* 505 */       broker.addService(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageBroker getMessageBroker()
/*     */   {
/* 516 */     return (MessageBroker)getParent();
/*     */   }
/*     */ 
/*     */   public void setMessageBroker(MessageBroker broker)
/*     */   {
/* 528 */     MessageBroker oldBroker = getMessageBroker();
/*     */ 
/* 530 */     setParent(broker);
/*     */ 
/* 532 */     if (oldBroker != null)
/*     */     {
/* 534 */       oldBroker.removeService(getId());
/*     */     }
/*     */ 
/* 538 */     if (broker.getService(getId()) != this)
/* 539 */       broker.addService(this);
/*     */   }
/*     */ 
/*     */   public ConfigMap describeService(Endpoint endpoint)
/*     */   {
/* 558 */     return null;
/*     */   }
/*     */ 
/*     */   public abstract Object serviceMessage(Message paramMessage);
/*     */ 
/*     */   public Object serviceCommand(CommandMessage message)
/*     */   {
/* 569 */     Object result = serviceCommonCommands(message);
/* 570 */     if (result != null)
/*     */     {
/* 579 */       return result;
/*     */     }
/* 581 */     throw new MessageException("Service Does Not Support Command Type " + message.getOperation());
/*     */   }
/*     */ 
/*     */   protected Object serviceCommonCommands(CommandMessage message)
/*     */   {
/* 592 */     Object commandResult = null;
/* 593 */     if (message.getOperation() == 5)
/*     */     {
/* 595 */       commandResult = Boolean.TRUE;
/*     */     }
/* 597 */     else if (message.getOperation() == 7)
/*     */     {
/* 599 */       ClusterManager clusterManager = getMessageBroker().getClusterManager();
/* 600 */       String serviceType = getClass().getName();
/* 601 */       String destinationName = message.getDestination();
/* 602 */       if (clusterManager.isDestinationClustered(serviceType, destinationName))
/*     */       {
/* 604 */         commandResult = clusterManager.getEndpointsForDestination(serviceType, destinationName);
/*     */       }
/*     */       else
/*     */       {
/* 610 */         commandResult = Boolean.FALSE;
/*     */       }
/*     */     }
/* 613 */     return commandResult;
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 624 */     return "Service.General";
/*     */   }
/*     */ 
/*     */   protected abstract void setupServiceControl(MessageBroker paramMessageBroker);
/*     */ 
/*     */   private void startDestinations()
/*     */   {
/* 641 */     for (Iterator iter = this.destinations.values().iterator(); iter.hasNext(); )
/*     */     {
/* 643 */       Destination destination = (Destination)iter.next();
/*     */ 
/* 645 */       long timeBeforeStartup = 0L;
/* 646 */       if (Log.isDebug()) {
/* 647 */         timeBeforeStartup = System.currentTimeMillis();
/*     */       }
/* 649 */       destination.start();
/*     */ 
/* 651 */       if (Log.isDebug())
/*     */       {
/* 653 */         long timeAfterStartup = System.currentTimeMillis();
/* 654 */         Long diffMillis = new Long(timeAfterStartup - timeBeforeStartup);
/* 655 */         Log.getLogger("Startup.Destination").debug("Destination with id '{0}' is ready (startup time: '{1}' ms)", new Object[] { destination.getId(), diffMillis });
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void stopDestinations()
/*     */   {
/* 666 */     for (Iterator iter = this.destinations.values().iterator(); iter.hasNext(); )
/*     */     {
/* 668 */       Destination destination = (Destination)iter.next();
/* 669 */       destination.stop();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.AbstractService
 * JD-Core Version:    0.6.0
 */
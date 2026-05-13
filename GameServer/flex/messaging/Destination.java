/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.services.ServiceControl;
/*     */ import flex.messaging.cluster.ClusterManager;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.config.NetworkSettings;
/*     */ import flex.messaging.config.SecurityConstraint;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.services.ServiceAdapter;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Destination extends ManageableComponent
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = -977001797620881435L;
/*     */   public static final String LOG_CATEGORY = "Service.General";
/*     */   private static final int NO_SERVICE = 11117;
/*     */   protected ServiceAdapter adapter;
/*     */   protected List channelIds;
/*     */   protected NetworkSettings networkSettings;
/*     */   protected SecurityConstraint securityConstraint;
/*     */   protected String securityConstraintRef;
/*     */   protected HashMap extraProperties;
/*     */ 
/*     */   public Destination()
/*     */   {
/*  75 */     this(false);
/*     */   }
/*     */ 
/*     */   public Destination(boolean enableManagement)
/*     */   {
/*  86 */     super(enableManagement);
/*     */ 
/*  88 */     this.networkSettings = new NetworkSettings();
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/* 105 */     super.initialize(id, properties);
/*     */ 
/* 107 */     if ((properties == null) || (properties.size() == 0)) {
/* 108 */       return;
/*     */     }
/* 110 */     ConfigMap network = properties.getPropertyAsMap("network", null);
/*     */ 
/* 112 */     if (network != null)
/*     */     {
/* 114 */       ConfigMap clusterInfo = network.getPropertyAsMap("cluster", null);
/* 115 */       if (clusterInfo != null)
/*     */       {
/* 118 */         network.allowProperty("cluster");
/* 119 */         clusterInfo.allowProperty("ref");
/* 120 */         clusterInfo.allowProperty("shared-backend");
/*     */ 
/* 122 */         String clusterId = clusterInfo.getPropertyAsString("ref", null);
/* 123 */         String coordinatorPolicy = clusterInfo.getPropertyAsString("shared-backend", null);
/* 124 */         if (coordinatorPolicy != null) {
/* 125 */           this.networkSettings.setSharedBackend(Boolean.valueOf(coordinatorPolicy).booleanValue());
/*     */         }
/* 127 */         this.networkSettings.setClusterId(clusterId);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 138 */     if (isValid()) {
/* 139 */       return;
/*     */     }
/* 141 */     super.validate();
/*     */ 
/* 143 */     if (getAdapter() == null)
/*     */     {
/* 145 */       String defaultAdapterId = getService().getDefaultAdapter();
/* 146 */       if (defaultAdapterId != null)
/*     */       {
/* 148 */         createAdapter(defaultAdapterId);
/*     */       }
/*     */       else
/*     */       {
/* 152 */         invalidate();
/*     */ 
/* 154 */         ConfigurationException ex = new ConfigurationException();
/* 155 */         ex.setMessage(10127, new Object[] { getId() });
/* 156 */         throw ex;
/*     */       }
/*     */     }
/*     */     List brokerChannelIds;
/*     */     Iterator iter;
/* 160 */     if (this.channelIds != null)
/*     */     {
/* 162 */       brokerChannelIds = getService().getMessageBroker().getChannelIds();
/* 163 */       for (iter = this.channelIds.iterator(); iter.hasNext(); )
/*     */       {
/* 165 */         String id = (String)iter.next();
/* 166 */         if ((brokerChannelIds == null) || (!brokerChannelIds.contains(id)))
/*     */         {
/* 168 */           iter.remove();
/* 169 */           if (Log.isWarn())
/*     */           {
/* 171 */             Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker. Removing the channel.", new Object[] { id });
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 180 */     if (this.channelIds == null)
/*     */     {
/* 182 */       List defaultChannelIds = getService().getDefaultChannels();
/* 183 */       if ((defaultChannelIds != null) && (defaultChannelIds.size() > 0))
/*     */       {
/* 185 */         setChannels(defaultChannelIds);
/*     */       }
/*     */       else
/*     */       {
/* 189 */         invalidate();
/*     */ 
/* 191 */         ConfigurationException ex = new ConfigurationException();
/* 192 */         ex.setMessage(10123, new Object[] { getId() });
/* 193 */         throw ex;
/*     */       }
/*     */     }
/*     */ 
/* 197 */     MessageBroker broker = getService().getMessageBroker();
/*     */ 
/* 200 */     if ((this.securityConstraint == null) && (this.securityConstraintRef != null))
/*     */     {
/* 202 */       this.securityConstraint = broker.getSecurityConstraint(this.securityConstraintRef);
/*     */     }
/*     */ 
/* 207 */     ClusterManager cm = broker.getClusterManager();
/*     */ 
/* 210 */     if ((getNetworkSettings().getClusterId() != null) || (cm.getDefaultClusterId() != null))
/*     */     {
/* 212 */       cm.clusterDestination(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 224 */     if (isStarted())
/*     */     {
/* 227 */       getAdapter().start();
/* 228 */       return;
/*     */     }
/*     */ 
/* 232 */     Service service = getService();
/* 233 */     if (!service.isStarted())
/*     */     {
/* 235 */       if (Log.isWarn())
/*     */       {
/* 237 */         Log.getLogger(getLogCategory()).warn("Destination with id '{0}' cannot be started when its Service with id '{1}' is not started.", new Object[] { getId(), service.getId() });
/*     */       }
/*     */ 
/* 241 */       return;
/*     */     }
/*     */ 
/* 245 */     if ((isManaged()) && (service.isManaged()))
/*     */     {
/* 247 */       setupDestinationControl(service);
/* 248 */       ServiceControl controller = (ServiceControl)service.getControl();
/* 249 */       if (getControl() != null) {
/* 250 */         controller.addDestination(getControl().getObjectName());
/*     */       }
/*     */     }
/* 253 */     super.start();
/*     */ 
/* 255 */     getAdapter().start();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 266 */     if (!isStarted())
/*     */     {
/* 268 */       return;
/*     */     }
/*     */ 
/* 271 */     getAdapter().stop();
/*     */ 
/* 273 */     super.stop();
/*     */ 
/* 276 */     if ((isManaged()) && (getService().isManaged()))
/*     */     {
/* 278 */       if (getControl() != null)
/*     */       {
/* 280 */         getControl().unregister();
/* 281 */         setControl(null);
/*     */       }
/* 283 */       setManaged(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServiceAdapter getAdapter()
/*     */   {
/* 301 */     return this.adapter;
/*     */   }
/*     */ 
/*     */   public ServiceAdapter createAdapter(String id)
/*     */   {
/* 319 */     if (getService() == null)
/*     */     {
/* 322 */       ConfigurationException ex = new ConfigurationException();
/* 323 */       ex.setMessage(11117, new Object[] { id });
/* 324 */       throw ex;
/*     */     }
/* 326 */     Map adapterClasses = getService().getRegisteredAdapters();
/* 327 */     if (!adapterClasses.containsKey(id))
/*     */     {
/* 330 */       ConfigurationException ex = new ConfigurationException();
/* 331 */       ex.setMessage(11114, new Object[] { id, getService().getId() });
/* 332 */       throw ex;
/*     */     }
/*     */ 
/* 335 */     String adapterClassName = (String)adapterClasses.get(id);
/* 336 */     Class adapterClass = ClassUtil.createClass(adapterClassName, FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader());
/*     */ 
/* 340 */     ServiceAdapter adapter = (ServiceAdapter)ClassUtil.createDefaultInstance(adapterClass, ServiceAdapter.class);
/* 341 */     adapter.setId(id);
/* 342 */     adapter.setManaged(isManaged());
/* 343 */     adapter.setDestination(this);
/*     */ 
/* 345 */     return adapter;
/*     */   }
/*     */ 
/*     */   public void setAdapter(ServiceAdapter adapter)
/*     */   {
/* 359 */     if (getAdapter() == adapter)
/*     */     {
/* 361 */       return;
/*     */     }
/* 363 */     if (adapter == null)
/*     */     {
/* 365 */       removeAdapter();
/* 366 */       return;
/*     */     }
/* 368 */     addAdapter(adapter);
/*     */   }
/*     */ 
/*     */   private void addAdapter(ServiceAdapter adapter)
/*     */   {
/* 379 */     removeAdapter();
/*     */ 
/* 381 */     this.adapter = adapter;
/*     */ 
/* 383 */     if ((adapter.getDestination() == null) || (adapter.getDestination() != this))
/*     */     {
/* 385 */       adapter.setDestination(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void removeAdapter()
/*     */   {
/* 395 */     ServiceAdapter adapter = getAdapter();
/* 396 */     if (adapter != null)
/*     */     {
/* 398 */       adapter.stop();
/*     */     }
/* 400 */     this.adapter = null;
/*     */   }
/*     */ 
/*     */   public boolean isBackendShared()
/*     */   {
/* 417 */     if (!isStarted()) {
/* 418 */       return false;
/*     */     }
/* 420 */     ClusterManager clm = getService().getMessageBroker().getClusterManager();
/* 421 */     boolean backendShared = clm.isBackendShared(getService().getClass().getName(), getId());
/* 422 */     return backendShared;
/*     */   }
/*     */ 
/*     */   public List getChannels()
/*     */   {
/* 430 */     return this.channelIds;
/*     */   }
/*     */ 
/*     */   public void addChannel(String id)
/*     */   {
/* 442 */     if (this.channelIds == null)
/* 443 */       this.channelIds = new ArrayList();
/* 444 */     else if (this.channelIds.contains(id)) {
/* 445 */       return;
/*     */     }
/* 447 */     if (isStarted())
/*     */     {
/* 449 */       List brokerChannelIds = getService().getMessageBroker().getChannelIds();
/* 450 */       if ((brokerChannelIds == null) || (!brokerChannelIds.contains(id)))
/*     */       {
/* 452 */         if (Log.isWarn())
/*     */         {
/* 454 */           Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*     */         }
/*     */ 
/* 458 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 463 */     this.channelIds.add(id);
/*     */   }
/*     */ 
/*     */   public boolean removeChannel(String id)
/*     */   {
/* 474 */     if (this.channelIds == null)
/* 475 */       return false;
/* 476 */     return this.channelIds.remove(id);
/*     */   }
/*     */ 
/*     */   public void setChannels(List ids)
/*     */   {
/*     */     List brokerChannelIds;
/*     */     Iterator iter;
/* 488 */     if ((ids != null) && (isStarted()))
/*     */     {
/* 490 */       brokerChannelIds = getService().getMessageBroker().getChannelIds();
/* 491 */       for (iter = ids.iterator(); iter.hasNext(); )
/*     */       {
/* 493 */         String id = (String)iter.next();
/* 494 */         if ((brokerChannelIds == null) || (!brokerChannelIds.contains(id)))
/*     */         {
/* 496 */           iter.remove();
/* 497 */           if (Log.isWarn())
/*     */           {
/* 499 */             Log.getLogger(getLogCategory()).warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 507 */     this.channelIds = ids;
/*     */   }
/*     */ 
/*     */   public boolean isClustered()
/*     */   {
/* 521 */     if (!isStarted()) {
/* 522 */       return false;
/*     */     }
/* 524 */     ClusterManager clm = getService().getMessageBroker().getClusterManager();
/* 525 */     boolean clusterReplicated = clm.isDestinationClustered(getService().getClass().getName(), getId());
/* 526 */     return clusterReplicated;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/* 536 */     String oldId = getId();
/*     */ 
/* 538 */     super.setId(id);
/*     */ 
/* 541 */     Service service = getService();
/* 542 */     if (service != null)
/*     */     {
/* 544 */       service.getMessageBroker().unregisterDestination(oldId);
/* 545 */       service.getDestinations().remove(oldId);
/* 546 */       service.getMessageBroker().registerDestination(id, service.getId());
/* 547 */       service.getDestinations().put(id, this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public NetworkSettings getNetworkSettings()
/*     */   {
/* 558 */     return this.networkSettings;
/*     */   }
/*     */ 
/*     */   public void setNetworkSettings(NetworkSettings networkSettings)
/*     */   {
/* 568 */     this.networkSettings = networkSettings;
/*     */   }
/*     */ 
/*     */   public Service getService()
/*     */   {
/* 578 */     return (Service)getParent();
/*     */   }
/*     */ 
/*     */   public void setService(Service service)
/*     */   {
/* 590 */     Service oldService = getService();
/*     */ 
/* 592 */     setParent(service);
/*     */ 
/* 594 */     if (oldService != null) {
/* 595 */       oldService.removeDestination(getId());
/*     */     }
/*     */ 
/* 598 */     if (service.getDestination(getId()) != this)
/* 599 */       service.addDestination(this);
/*     */   }
/*     */ 
/*     */   public String getServiceType()
/*     */   {
/* 611 */     Service service = getService();
/* 612 */     if (service == null)
/*     */     {
/* 614 */       return null;
/*     */     }
/*     */ 
/* 618 */     return service.getClass().getName();
/*     */   }
/*     */ 
/*     */   public SecurityConstraint getSecurityConstraint()
/*     */   {
/* 630 */     return this.securityConstraint;
/*     */   }
/*     */ 
/*     */   public void setSecurityConstraint(SecurityConstraint securityConstraint)
/*     */   {
/* 640 */     this.securityConstraint = securityConstraint;
/*     */   }
/*     */ 
/*     */   public void setSecurityConstraint(String ref)
/*     */   {
/* 653 */     if (isStarted())
/*     */     {
/* 655 */       MessageBroker msgBroker = getService().getMessageBroker();
/* 656 */       this.securityConstraint = msgBroker.getSecurityConstraint(ref);
/*     */     }
/*     */ 
/* 660 */     this.securityConstraintRef = ref;
/*     */   }
/*     */ 
/*     */   public ConfigMap describeDestination()
/*     */   {
/* 679 */     ConfigMap destinationConfig = new ConfigMap();
/* 680 */     destinationConfig.addProperty("id", getId());
/*     */ 
/* 682 */     ConfigMap channelsConfig = new ConfigMap();
/* 683 */     for (Iterator iter = this.channelIds.iterator(); iter.hasNext(); )
/*     */     {
/* 685 */       String id = (String)iter.next();
/* 686 */       ConfigMap channelConfig = new ConfigMap();
/* 687 */       channelConfig.addProperty("ref", id);
/* 688 */       channelsConfig.addProperty("channel", channelConfig);
/*     */     }
/*     */ 
/* 691 */     if (channelsConfig.size() > 0)
/*     */     {
/* 693 */       destinationConfig.addProperty("channels", channelsConfig);
/*     */     }
/*     */ 
/* 696 */     return destinationConfig;
/*     */   }
/*     */ 
/*     */   public void addExtraProperty(String name, Object value)
/*     */   {
/* 704 */     if (this.extraProperties == null)
/*     */     {
/* 706 */       this.extraProperties = new HashMap();
/*     */     }
/*     */ 
/* 709 */     this.extraProperties.put(name, value);
/*     */   }
/*     */ 
/*     */   public Object getExtraProperty(String name)
/*     */   {
/* 717 */     if (this.extraProperties != null)
/*     */     {
/* 719 */       return this.extraProperties.get(name);
/*     */     }
/*     */ 
/* 723 */     return null;
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 741 */     return "Service.General";
/*     */   }
/*     */ 
/*     */   protected void setupDestinationControl(Service service)
/*     */   {
/* 753 */     setManaged(false);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.Destination
 * JD-Core Version:    0.6.0
 */
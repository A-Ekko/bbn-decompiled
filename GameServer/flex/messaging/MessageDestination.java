/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.management.runtime.messaging.MessageDestinationControl;
/*     */ import flex.management.runtime.messaging.services.messaging.SubscriptionManagerControl;
/*     */ import flex.management.runtime.messaging.services.messaging.ThrottleManagerControl;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.config.NetworkSettings;
/*     */ import flex.messaging.config.ServerSettings;
/*     */ import flex.messaging.config.ThrottleSettings;
/*     */ import flex.messaging.services.MessageService;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.services.messaging.RemoteSubscriptionManager;
/*     */ import flex.messaging.services.messaging.SubscriptionManager;
/*     */ import flex.messaging.services.messaging.ThrottleManager;
/*     */ 
/*     */ public class MessageDestination extends FactoryDestination
/*     */ {
/*     */   static final long serialVersionUID = -2016911808141319012L;
/*     */   public static final String LOG_CATEGORY = "Service.Message";
/*     */   private static final int UNSUPPORTED_POLICY = 10124;
/*     */   private ServerSettings serverSettings;
/*     */   private SubscriptionManager subscriptionManager;
/*     */   private RemoteSubscriptionManager remoteSubscriptionManager;
/*     */   private ThrottleManager throttleManager;
/*     */   private MessageDestinationControl controller;
/*     */ 
/*     */   public MessageDestination()
/*     */   {
/*  74 */     this(false);
/*     */   }
/*     */ 
/*     */   public MessageDestination(boolean enableManagement)
/*     */   {
/*  85 */     super(enableManagement);
/*     */ 
/*  87 */     this.serverSettings = new ServerSettings();
/*     */ 
/*  90 */     this.subscriptionManager = new SubscriptionManager(this);
/*  91 */     this.remoteSubscriptionManager = new RemoteSubscriptionManager(this);
/*  92 */     this.throttleManager = new ThrottleManager();
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/* 109 */     super.initialize(id, properties);
/*     */ 
/* 111 */     if ((properties == null) || (properties.size() == 0)) {
/* 112 */       return;
/*     */     }
/*     */ 
/* 115 */     network(properties);
/*     */ 
/* 118 */     server(properties);
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 126 */     super.stop();
/*     */ 
/* 129 */     this.subscriptionManager.destroy();
/* 130 */     this.remoteSubscriptionManager.destroy();
/*     */   }
/*     */ 
/*     */   public void setNetworkSettings(NetworkSettings networkSettings)
/*     */   {
/* 146 */     super.setNetworkSettings(networkSettings);
/*     */ 
/* 149 */     if (networkSettings.getThrottleSettings() != null)
/*     */     {
/* 151 */       ThrottleSettings settings = networkSettings.getThrottleSettings();
/* 152 */       settings.setDestinationName(getId());
/* 153 */       this.throttleManager.setThrottleSettings(settings);
/*     */     }
/* 155 */     if (networkSettings.getSubscriptionTimeoutMinutes() > 0)
/*     */     {
/* 157 */       long subscriptionTimeoutMillis = networkSettings.getSubscriptionTimeoutMinutes() * 60 * 1000;
/* 158 */       this.subscriptionManager.setSubscriptionTimeoutMillis(subscriptionTimeoutMillis);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ServerSettings getServerSettings()
/*     */   {
/* 169 */     return this.serverSettings;
/*     */   }
/*     */ 
/*     */   public void setServerSettings(ServerSettings serverSettings)
/*     */   {
/* 179 */     this.serverSettings = serverSettings;
/*     */   }
/*     */ 
/*     */   public void setService(Service service)
/*     */   {
/* 190 */     MessageService messageService = (MessageService)service;
/* 191 */     super.setService(messageService);
/*     */   }
/*     */ 
/*     */   public SubscriptionManager getSubscriptionManager()
/*     */   {
/* 203 */     return this.subscriptionManager;
/*     */   }
/*     */ 
/*     */   public RemoteSubscriptionManager getRemoteSubscriptionManager()
/*     */   {
/* 209 */     return this.remoteSubscriptionManager;
/*     */   }
/*     */ 
/*     */   public ThrottleManager getThrottleManager()
/*     */   {
/* 215 */     return this.throttleManager;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 221 */     if ((o instanceof Destination))
/*     */     {
/* 223 */       Destination d = (Destination)o;
/* 224 */       if ((d != null) && (d.getServiceType().equals(getServiceType())) && (d.getId().equals(getId())))
/*     */       {
/* 226 */         return true;
/*     */       }
/*     */     }
/* 229 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 235 */     return (getServiceType() == null ? 0 : getServiceType().hashCode()) * 100003 + (getId() == null ? 0 : getId().hashCode());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 242 */     return getServiceType() + "#" + getId();
/*     */   }
/*     */ 
/*     */   protected void network(ConfigMap properties)
/*     */   {
/* 253 */     ConfigMap network = properties.getPropertyAsMap("network", null);
/* 254 */     if (network != null)
/*     */     {
/* 257 */       NetworkSettings ns = getNetworkSettings();
/*     */ 
/* 260 */       int useLegacyPropertyToken = -999999;
/* 261 */       int subscriptionTimeoutMinutes = network.getPropertyAsInt("subscription-timeout-minutes", useLegacyPropertyToken);
/* 262 */       if (subscriptionTimeoutMinutes == useLegacyPropertyToken)
/* 263 */         subscriptionTimeoutMinutes = network.getPropertyAsInt("session-timeout", 0);
/* 264 */       ns.setSubscriptionTimeoutMinutes(subscriptionTimeoutMinutes);
/*     */ 
/* 267 */       throttle(ns.getThrottleSettings(), network);
/*     */ 
/* 269 */       setNetworkSettings(ns);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void throttle(ThrottleSettings ts, ConfigMap network)
/*     */   {
/* 275 */     ConfigMap inbound = network.getPropertyAsMap("throttle-inbound", null);
/* 276 */     if (inbound != null)
/*     */     {
/* 278 */       int policy = getPolicyFromThrottleSettings(inbound, "NONE");
/* 279 */       ts.setInboundPolicy(policy);
/* 280 */       int destFreq = inbound.getPropertyAsInt("max-frequency", 0);
/* 281 */       ts.setIncomingDestinationFrequency(destFreq);
/* 282 */       int clientFreq = inbound.getPropertyAsInt("max-client-frequency", 0);
/* 283 */       ts.setIncomingClientFrequency(clientFreq);
/*     */     }
/*     */ 
/* 286 */     ConfigMap outbound = network.getPropertyAsMap("throttle-outbound", null);
/* 287 */     if (outbound != null)
/*     */     {
/* 289 */       int policy = getPolicyFromThrottleSettings(outbound, "NONE");
/* 290 */       ts.setOutboundPolicy(policy);
/* 291 */       int destFreq = outbound.getPropertyAsInt("max-frequency", 0);
/* 292 */       ts.setOutgoingDestinationFrequency(destFreq);
/* 293 */       int clientFreq = outbound.getPropertyAsInt("max-client-frequency", 0);
/* 294 */       ts.setOutgoingClientFrequency(clientFreq);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getPolicyFromThrottleSettings(ConfigMap settings, String defaultPolicy)
/*     */   {
/* 300 */     String policy = settings.getPropertyAsString("policy", defaultPolicy);
/* 301 */     if (policy.equals("NONE"))
/*     */     {
/* 303 */       return 0;
/*     */     }
/* 305 */     if (policy.equals("ERROR"))
/*     */     {
/* 307 */       return 1;
/*     */     }
/* 309 */     if (policy.equals("IGNORE"))
/*     */     {
/* 311 */       return 2;
/*     */     }
/* 313 */     if (policy.equals("REPLACE"))
/*     */     {
/* 315 */       return 3;
/*     */     }
/*     */ 
/* 319 */     ConfigurationException ce = new ConfigurationException();
/* 320 */     ce.setMessage(10124, new Object[] { getId(), policy });
/* 321 */     throw ce;
/*     */   }
/*     */ 
/*     */   protected void server(ConfigMap properties)
/*     */   {
/* 327 */     ConfigMap server = properties.getPropertyAsMap("server", null);
/* 328 */     if (server != null)
/*     */     {
/* 330 */       int max = server.getPropertyAsInt("max-cache-size", 0);
/* 331 */       this.serverSettings.setMaxCacheSize(max);
/*     */ 
/* 333 */       long ttl = server.getPropertyAsLong("message-time-to-live", -1L);
/* 334 */       this.serverSettings.setMessageTTL(ttl);
/*     */ 
/* 336 */       boolean durable = server.getPropertyAsBoolean("durable", false);
/* 337 */       this.serverSettings.setDurable(durable);
/*     */ 
/* 339 */       boolean allowSubtopics = server.getPropertyAsBoolean("allow-subtopics", false);
/* 340 */       this.serverSettings.setAllowSubtopics(allowSubtopics);
/*     */ 
/* 342 */       String subtopicSeparator = server.getPropertyAsString("subtopic-separator", ".");
/* 343 */       this.serverSettings.setSubtopicSeparator(subtopicSeparator);
/*     */ 
/* 345 */       String routingMode = server.getPropertyAsString("cluster-message-routing", "server-to-server");
/* 346 */       this.serverSettings.setBroadcastRoutingMode(routingMode);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 357 */     return "Service.Message";
/*     */   }
/*     */ 
/*     */   protected void setupDestinationControl(Service service)
/*     */   {
/* 368 */     this.controller = new MessageDestinationControl(this, service.getControl());
/* 369 */     this.controller.register();
/* 370 */     setControl(this.controller);
/* 371 */     setupThrottleManagerControl(this.controller);
/* 372 */     setupSubscriptionManagerControl(this.controller);
/*     */   }
/*     */ 
/*     */   private void setupThrottleManagerControl(MessageDestinationControl destinationControl)
/*     */   {
/* 378 */     ThrottleManagerControl throttleManagerControl = new ThrottleManagerControl(getThrottleManager(), destinationControl);
/* 379 */     throttleManagerControl.register();
/* 380 */     getThrottleManager().setControl(throttleManagerControl);
/* 381 */     getThrottleManager().setManaged(true);
/* 382 */     destinationControl.setThrottleManager(throttleManagerControl.getObjectName());
/*     */   }
/*     */ 
/*     */   private void setupSubscriptionManagerControl(MessageDestinationControl destinationControl)
/*     */   {
/* 387 */     SubscriptionManagerControl subscriptionManagerControl = new SubscriptionManagerControl(getSubscriptionManager(), destinationControl);
/* 388 */     subscriptionManagerControl.register();
/* 389 */     getSubscriptionManager().setControl(subscriptionManagerControl);
/* 390 */     getSubscriptionManager().setManaged(true);
/* 391 */     destinationControl.setSubscriptionManager(subscriptionManagerControl.getObjectName());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageDestination
 * JD-Core Version:    0.6.0
 */
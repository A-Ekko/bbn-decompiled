/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.Server;
/*     */ import flex.messaging.client.FlexClientManager;
/*     */ import flex.messaging.cluster.ClusterManager;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.endpoints.Endpoint2;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.log.Target;
/*     */ import flex.messaging.security.LoginCommand;
/*     */ import flex.messaging.security.LoginManager;
/*     */ import flex.messaging.services.AuthenticationService;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.services.ServiceAdapter;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import flex.messaging.util.RedeployManager;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import flex.messaging.util.ToStringPrettyPrinter;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class MessagingConfiguration
/*     */   implements ServicesConfiguration
/*     */ {
/*     */   private final Map channelSettings;
/*     */   private final List defaultChannels;
/*     */   private final SecuritySettings securitySettings;
/*     */   private final List serviceSettings;
/*     */   private final List sharedServerSettings;
/*     */   private LoggingSettings loggingSettings;
/*     */   private SystemSettings systemSettings;
/*     */   private FlexClientSettings flexClientSettings;
/*     */   private final Map clusterSettings;
/*     */   private final Map factorySettings;
/*     */ 
/*     */   public MessagingConfiguration()
/*     */   {
/*  75 */     this.channelSettings = new HashMap();
/*  76 */     this.defaultChannels = new ArrayList(4);
/*  77 */     this.clusterSettings = new HashMap();
/*  78 */     this.factorySettings = new HashMap();
/*  79 */     this.serviceSettings = new ArrayList();
/*  80 */     this.sharedServerSettings = new ArrayList();
/*  81 */     this.securitySettings = new SecuritySettings();
/*     */   }
/*     */ 
/*     */   public void configureBroker(MessageBroker broker)
/*     */   {
/*  86 */     broker.setChannelSettings(this.channelSettings);
/*  87 */     broker.setSecuritySettings(this.securitySettings);
/*  88 */     broker.setSystemSettings(this.systemSettings);
/*  89 */     broker.setFlexClientSettings(this.flexClientSettings);
/*  90 */     createAuthorizationManager(broker);
/*  91 */     createFlexClientManager(broker);
/*  92 */     createRedeployManager(broker);
/*  93 */     createFactories(broker);
/*  94 */     createSharedServers(broker);
/*  95 */     createEndpoints(broker);
/*     */ 
/*  97 */     broker.setDefaultChannels(this.defaultChannels);
/*  98 */     prepareClusters(broker);
/*  99 */     createServices(broker);
/*     */   }
/*     */ 
/*     */   public MessageBroker createBroker(String id, ClassLoader loader)
/*     */   {
/* 104 */     return new MessageBroker(this.systemSettings.isManageable(), id, loader);
/*     */   }
/*     */ 
/*     */   private void createFactories(MessageBroker broker)
/*     */   {
/* 109 */     for (Iterator iter = this.factorySettings.entrySet().iterator(); iter.hasNext(); )
/*     */     {
/* 111 */       Map.Entry entry = (Map.Entry)iter.next();
/* 112 */       String id = (String)entry.getKey();
/* 113 */       FactorySettings factorySetting = (FactorySettings)entry.getValue();
/* 114 */       broker.addFactory(id, factorySetting.createFactory());
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createFlexClientManager(MessageBroker broker)
/*     */   {
/* 120 */     FlexClientManager flexClientManager = new FlexClientManager(broker.isManaged(), broker);
/* 121 */     broker.setFlexClientManager(flexClientManager);
/*     */   }
/*     */ 
/*     */   private void createRedeployManager(MessageBroker broker)
/*     */   {
/* 126 */     RedeployManager redeployManager = new RedeployManager();
/* 127 */     redeployManager.setEnabled(this.systemSettings.getRedeployEnabled());
/* 128 */     redeployManager.setWatchInterval(this.systemSettings.getWatchInterval());
/* 129 */     redeployManager.setTouchFiles(this.systemSettings.getTouchFiles());
/* 130 */     redeployManager.setWatchFiles(this.systemSettings.getWatchFiles());
/* 131 */     broker.setRedeployManager(redeployManager);
/*     */   }
/*     */ 
/*     */   private void createAuthorizationManager(MessageBroker broker)
/*     */   {
/* 136 */     LoginManager loginManager = new LoginManager();
/*     */ 
/* 139 */     LoginCommand loginCommand = null;
/*     */ 
/* 141 */     Map loginCommands = this.securitySettings.getLoginCommands();
/*     */ 
/* 144 */     LoginCommandSettings loginCommandSettings = (LoginCommandSettings)loginCommands.get("all");
/*     */     String serverInfo;
/*     */     Iterator iterator;
/* 145 */     if (loginCommandSettings != null)
/*     */     {
/* 147 */       loginCommand = initLoginCommand(loginCommandSettings);
/*     */     }
/*     */     else
/*     */     {
/* 152 */       serverInfo = this.securitySettings.getServerInfo();
/* 153 */       loginCommandSettings = (LoginCommandSettings)loginCommands.get(serverInfo);
/*     */ 
/* 155 */       if (loginCommandSettings != null)
/*     */       {
/* 157 */         loginCommand = initLoginCommand(loginCommandSettings);
/*     */       }
/*     */       else
/*     */       {
/* 162 */         serverInfo = serverInfo.toLowerCase();
/* 163 */         for (iterator = loginCommands.keySet().iterator(); iterator.hasNext(); )
/*     */         {
/* 165 */           String serverMatch = (String)iterator.next();
/* 166 */           loginCommandSettings = (LoginCommandSettings)loginCommands.get(serverMatch);
/*     */ 
/* 168 */           if (serverInfo.indexOf(serverMatch.toLowerCase()) != -1)
/*     */           {
/* 171 */             loginCommands.put(serverInfo, loginCommandSettings);
/* 172 */             loginCommand = initLoginCommand(loginCommandSettings);
/* 173 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 178 */     loginManager.setLoginCommand(loginCommand);
/*     */ 
/* 180 */     if (loginCommandSettings != null) {
/* 181 */       loginManager.setPerClientAuthentication(loginCommandSettings.isPerClientAuthentication());
/*     */     }
/* 183 */     broker.setLoginManager(loginManager);
/*     */   }
/*     */ 
/*     */   private LoginCommand initLoginCommand(LoginCommandSettings loginCommandSettings)
/*     */   {
/* 188 */     String loginClass = loginCommandSettings.getClassName();
/* 189 */     Class c = ClassUtil.createClass(loginClass, FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader());
/*     */ 
/* 192 */     LoginCommand loginCommand = (LoginCommand)ClassUtil.createDefaultInstance(c, LoginCommand.class);
/*     */ 
/* 194 */     return loginCommand;
/*     */   }
/*     */ 
/*     */   private void createSharedServers(MessageBroker broker)
/*     */   {
/* 199 */     int n = this.sharedServerSettings.size();
/* 200 */     for (int i = 0; i < n; i++)
/*     */     {
/* 202 */       SharedServerSettings settings = (SharedServerSettings)this.sharedServerSettings.get(i);
/* 203 */       String id = settings.getId();
/* 204 */       String className = settings.getClassName();
/* 205 */       Class serverClass = ClassUtil.createClass(className, broker.getClassLoader());
/* 206 */       Server server = (Server)ClassUtil.createDefaultInstance(serverClass, Server.class);
/* 207 */       server.initialize(id, settings.getProperties());
/* 208 */       if ((broker.isManaged()) && ((server instanceof ManageableComponent)))
/*     */       {
/* 210 */         ManageableComponent manageableServer = (ManageableComponent)server;
/* 211 */         manageableServer.setManaged(true);
/* 212 */         manageableServer.setParent(broker);
/*     */       }
/* 214 */       broker.addServer(server);
/*     */ 
/* 216 */       if (!Log.isInfo())
/*     */         continue;
/* 218 */       Log.getLogger("Configuration").info("Server " + id + " of type " + className + " created.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createEndpoints(MessageBroker broker)
/*     */   {
/* 226 */     for (Iterator iter = this.channelSettings.keySet().iterator(); iter.hasNext(); )
/*     */     {
/* 228 */       String id = (String)iter.next();
/* 229 */       ChannelSettings chanSettings = (ChannelSettings)this.channelSettings.get(id);
/* 230 */       String url = chanSettings.getUri();
/* 231 */       String endpointClassName = chanSettings.getEndpointType();
/*     */ 
/* 234 */       if (chanSettings.isRemote())
/*     */       {
/*     */         continue;
/*     */       }
/* 238 */       Endpoint endpoint = broker.createEndpoint(id, url, endpointClassName);
/* 239 */       endpoint.setSecurityConstraint(chanSettings.getConstraint());
/* 240 */       endpoint.setClientType(chanSettings.getClientType());
/*     */ 
/* 243 */       String referencedServerId = chanSettings.getServerId();
/* 244 */       if ((referencedServerId != null) && ((endpoint instanceof Endpoint2)))
/*     */       {
/* 246 */         Server server = broker.getServer(referencedServerId);
/* 247 */         if (server == null)
/*     */         {
/* 249 */           ConfigurationException ce = new ConfigurationException();
/* 250 */           ce.setMessage(11128, new Object[] { chanSettings.getId(), referencedServerId });
/* 251 */           throw ce;
/*     */         }
/* 253 */         ((Endpoint2)endpoint).setServer(broker.getServer(referencedServerId));
/*     */       }
/*     */ 
/* 257 */       endpoint.initialize(id, chanSettings.getProperties());
/*     */ 
/* 259 */       if (Log.isInfo())
/*     */       {
/* 261 */         String endpointURL = endpoint.getUrl();
/* 262 */         String endpointSecurity = EndpointControl.getSecurityConstraintOf(endpoint);
/* 263 */         if (StringUtils.isEmpty(endpointSecurity))
/* 264 */           endpointSecurity = "None";
/* 265 */         Log.getLogger("Configuration").info("Endpoint " + id + " created with security: " + endpointSecurity + StringUtils.NEWLINE + "at URL: " + endpointURL);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createServices(MessageBroker broker)
/*     */   {
/* 276 */     AuthenticationService authService = new AuthenticationService();
/* 277 */     authService.setMessageBroker(broker);
/*     */ 
/* 279 */     for (Iterator iter = this.serviceSettings.iterator(); iter.hasNext(); )
/*     */     {
/* 281 */       svcSettings = (ServiceSettings)iter.next();
/* 282 */       String svcId = svcSettings.getId();
/* 283 */       String svcClassName = svcSettings.getClassName();
/*     */ 
/* 286 */       service = broker.createService(svcId, svcClassName);
/*     */ 
/* 291 */       service.initialize(svcId, svcSettings.getProperties());
/*     */ 
/* 294 */       for (Iterator chanIter = svcSettings.getDefaultChannels().iterator(); chanIter.hasNext(); )
/*     */       {
/* 296 */         ChannelSettings chanSettings = (ChannelSettings)chanIter.next();
/* 297 */         service.addDefaultChannel(chanSettings.getId());
/*     */       }
/*     */ 
/* 301 */       Map svcAdapterSettings = svcSettings.getAllAdapterSettings();
/* 302 */       for (Iterator asIter = svcAdapterSettings.values().iterator(); asIter.hasNext(); )
/*     */       {
/* 304 */         AdapterSettings as = (AdapterSettings)asIter.next();
/* 305 */         service.registerAdapter(as.getId(), as.getClassName());
/* 306 */         if (as.isDefault())
/*     */         {
/* 308 */           service.setDefaultAdapter(as.getId());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 313 */       destinationSettings = svcSettings.getDestinationSettings();
/* 314 */       for (destSettingsIter = destinationSettings.keySet().iterator(); destSettingsIter.hasNext(); )
/*     */       {
/* 316 */         String destName = (String)destSettingsIter.next();
/* 317 */         DestinationSettings destSettings = (DestinationSettings)destinationSettings.get(destName);
/*     */ 
/* 319 */         createDestination(destSettings, service, svcSettings); }  } ServiceSettings svcSettings;
/*     */     Service service;
/*     */     Map destinationSettings;
/*     */     Iterator destSettingsIter; } 
/* 326 */   private void createDestination(DestinationSettings destSettings, Service service, ServiceSettings svcSettings) { String destId = destSettings.getId();
/* 327 */     Destination destination = service.createDestination(destId);
/*     */ 
/* 330 */     List chanSettings = destSettings.getChannelSettings();
/* 331 */     if (chanSettings.size() > 0)
/*     */     {
/* 333 */       List channelIds = new ArrayList(2);
/* 334 */       for (Iterator iter = chanSettings.iterator(); iter.hasNext(); ) {
/* 335 */         ChannelSettings cs = (ChannelSettings)iter.next();
/* 336 */         channelIds.add(cs.getId());
/*     */       }
/* 338 */       destination.setChannels(channelIds);
/*     */     }
/*     */ 
/* 342 */     SecurityConstraint constraint = destSettings.getConstraint();
/* 343 */     destination.setSecurityConstraint(constraint);
/*     */ 
/* 346 */     destination.initialize(destId, svcSettings.getProperties());
/* 347 */     destination.initialize(destId, destSettings.getAdapterSettings().getProperties());
/* 348 */     destination.initialize(destId, destSettings.getProperties());
/*     */ 
/* 351 */     createAdapter(destination, destSettings, svcSettings);
/*     */   }
/*     */ 
/*     */   private void createAdapter(Destination destination, DestinationSettings destSettings, ServiceSettings svcSettings)
/*     */   {
/* 356 */     AdapterSettings adapterSettings = destSettings.getAdapterSettings();
/* 357 */     String adapterId = adapterSettings.getId();
/*     */ 
/* 359 */     ServiceAdapter adapter = destination.createAdapter(adapterId);
/*     */ 
/* 362 */     adapter.initialize(adapterId, svcSettings.getProperties());
/* 363 */     adapter.initialize(adapterId, adapterSettings.getProperties());
/* 364 */     adapter.initialize(adapterId, destSettings.getProperties());
/*     */   }
/*     */ 
/*     */   public void createLogAndTargets()
/*     */   {
/* 378 */     if (this.loggingSettings == null)
/*     */     {
/* 380 */       Log.setPrettyPrinterClass(ToStringPrettyPrinter.class.getName());
/* 381 */       return;
/*     */     }
/*     */ 
/* 384 */     Log.createLog();
/*     */ 
/* 386 */     ConfigMap properties = this.loggingSettings.getProperties();
/*     */ 
/* 389 */     if (properties.getPropertyAsString("pretty-printer", null) == null)
/*     */     {
/* 391 */       Log.setPrettyPrinterClass(ToStringPrettyPrinter.class.getName());
/*     */     }
/*     */ 
/* 394 */     Log.initialize(null, properties);
/*     */ 
/* 397 */     List targets = this.loggingSettings.getTargets();
/* 398 */     Iterator it = targets.iterator();
/* 399 */     while (it.hasNext())
/*     */     {
/* 401 */       TargetSettings targetSettings = (TargetSettings)it.next();
/* 402 */       String className = targetSettings.getClassName();
/*     */ 
/* 404 */       Class c = ClassUtil.createClass(className, FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader());
/*     */       try
/*     */       {
/* 409 */         Target target = (Target)c.newInstance();
/* 410 */         target.setLevel(Log.readLevel(targetSettings.getLevel()));
/* 411 */         target.setFilters(targetSettings.getFilters());
/* 412 */         target.initialize(null, targetSettings.getProperties());
/* 413 */         Log.addTarget(target);
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 418 */         if ((t instanceof InvocationTargetException)) {
/* 419 */           t = ((InvocationTargetException)t).getCause();
/*     */         }
/* 421 */         System.err.println("*** Error setting up logging system");
/* 422 */         t.printStackTrace();
/*     */ 
/* 424 */         ConfigurationException cx = new ConfigurationException();
/* 425 */         cx.setMessage(10126, new Object[] { className });
/* 426 */         cx.setRootCause(t);
/* 427 */         throw cx;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void prepareClusters(MessageBroker broker)
/*     */   {
/* 434 */     ClusterManager clusterManager = broker.getClusterManager();
/* 435 */     for (Iterator iter = this.clusterSettings.keySet().iterator(); iter.hasNext(); )
/*     */     {
/* 437 */       String clusterId = (String)iter.next();
/* 438 */       ClusterSettings cs = (ClusterSettings)this.clusterSettings.get(clusterId);
/* 439 */       clusterManager.prepareCluster(cs);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addSharedServerSettings(SharedServerSettings settings)
/*     */   {
/* 448 */     this.sharedServerSettings.add(settings);
/*     */   }
/*     */ 
/*     */   public void addChannelSettings(String id, ChannelSettings settings)
/*     */   {
/* 457 */     this.channelSettings.put(id, settings);
/*     */   }
/*     */ 
/*     */   public ChannelSettings getChannelSettings(String ref)
/*     */   {
/* 462 */     return (ChannelSettings)this.channelSettings.get(ref);
/*     */   }
/*     */ 
/*     */   public Map getAllChannelSettings()
/*     */   {
/* 467 */     return this.channelSettings;
/*     */   }
/*     */ 
/*     */   public void addDefaultChannel(String id)
/*     */   {
/* 475 */     this.defaultChannels.add(id);
/*     */   }
/*     */ 
/*     */   public List getDefaultChannels()
/*     */   {
/* 480 */     return this.defaultChannels;
/*     */   }
/*     */ 
/*     */   public SecuritySettings getSecuritySettings()
/*     */   {
/* 489 */     return this.securitySettings;
/*     */   }
/*     */ 
/*     */   public void addServiceSettings(ServiceSettings settings)
/*     */   {
/* 498 */     this.serviceSettings.add(settings);
/*     */   }
/*     */ 
/*     */   public ServiceSettings getServiceSettings(String id)
/*     */   {
/* 503 */     for (Iterator iter = this.serviceSettings.iterator(); iter.hasNext(); )
/*     */     {
/* 505 */       ServiceSettings serviceSettings = (ServiceSettings)iter.next();
/* 506 */       if (serviceSettings.getId().equals(id))
/* 507 */         return serviceSettings;
/*     */     }
/* 509 */     return null;
/*     */   }
/*     */ 
/*     */   public List getAllServiceSettings()
/*     */   {
/* 514 */     return this.serviceSettings;
/*     */   }
/*     */ 
/*     */   public LoggingSettings getLoggingSettings()
/*     */   {
/* 522 */     return this.loggingSettings;
/*     */   }
/*     */ 
/*     */   public void setLoggingSettings(LoggingSettings loggingSettings)
/*     */   {
/* 527 */     this.loggingSettings = loggingSettings;
/*     */   }
/*     */ 
/*     */   public void setSystemSettings(SystemSettings ss)
/*     */   {
/* 535 */     this.systemSettings = ss;
/*     */   }
/*     */ 
/*     */   public SystemSettings getSystemSettings()
/*     */   {
/* 540 */     return this.systemSettings;
/*     */   }
/*     */ 
/*     */   public void setFlexClientSettings(FlexClientSettings value)
/*     */   {
/* 548 */     this.flexClientSettings = value;
/*     */   }
/*     */ 
/*     */   public FlexClientSettings getFlexClientSettings()
/*     */   {
/* 553 */     return this.flexClientSettings;
/*     */   }
/*     */ 
/*     */   public void addClusterSettings(ClusterSettings settings)
/*     */   {
/*     */     Iterator it;
/* 562 */     if (settings.isDefault())
/*     */     {
/* 564 */       for (it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */       {
/* 566 */         ClusterSettings cs = (ClusterSettings)it.next();
/*     */ 
/* 568 */         if (cs.isDefault())
/*     */         {
/* 570 */           ConfigurationException cx = new ConfigurationException();
/* 571 */           cx.setMessage(10214, new Object[] { settings.getClusterName(), cs.getClusterName() });
/* 572 */           throw cx;
/*     */         }
/*     */       }
/*     */     }
/* 576 */     if (this.clusterSettings.containsKey(settings.getClusterName()))
/*     */     {
/* 578 */       ConfigurationException cx = new ConfigurationException();
/* 579 */       cx.setMessage(10206, new Object[] { settings.getClusterName() });
/* 580 */       throw cx;
/*     */     }
/* 582 */     this.clusterSettings.put(settings.getClusterName(), settings);
/*     */   }
/*     */ 
/*     */   public ClusterSettings getClusterSettings(String clusterId)
/*     */   {
/* 587 */     for (Iterator it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */     {
/* 589 */       ClusterSettings cs = (ClusterSettings)it.next();
/* 590 */       if (cs.getClusterName() == clusterId)
/* 591 */         return cs;
/* 592 */       if ((cs.getClusterName() != null) && (cs.getClusterName().equals(clusterId)))
/* 593 */         return cs;
/*     */     }
/* 595 */     return null;
/*     */   }
/*     */ 
/*     */   public ClusterSettings getDefaultCluster()
/*     */   {
/* 600 */     for (Iterator it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */     {
/* 602 */       ClusterSettings cs = (ClusterSettings)it.next();
/* 603 */       if (cs.isDefault())
/* 604 */         return cs;
/*     */     }
/* 606 */     return null;
/*     */   }
/*     */ 
/*     */   public void addFactorySettings(String id, FactorySettings settings)
/*     */   {
/* 611 */     this.factorySettings.put(id, settings);
/*     */   }
/*     */ 
/*     */   public void reportUnusedProperties()
/*     */   {
/* 620 */     ArrayList findings = new ArrayList();
/*     */ 
/* 622 */     Iterator serviceItr = this.serviceSettings.iterator();
/* 623 */     while (serviceItr.hasNext())
/*     */     {
/* 625 */       ServiceSettings serviceSettings = (ServiceSettings)serviceItr.next();
/* 626 */       gatherUnusedProperties(serviceSettings.getId(), serviceSettings.getSourceFile(), "service", serviceSettings, findings);
/*     */ 
/* 628 */       Iterator destinationItr = serviceSettings.getDestinationSettings().values().iterator();
/* 629 */       while (destinationItr.hasNext())
/*     */       {
/* 631 */         DestinationSettings destinationSettings = (DestinationSettings)destinationItr.next();
/* 632 */         gatherUnusedProperties(destinationSettings.getId(), destinationSettings.getSourceFile(), "destination", destinationSettings, findings);
/*     */ 
/* 636 */         AdapterSettings adapterSettings = destinationSettings.getAdapterSettings();
/* 637 */         if (adapterSettings != null)
/*     */         {
/* 639 */           gatherUnusedProperties(adapterSettings.getId(), adapterSettings.getSourceFile(), "adapter", adapterSettings, findings);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 646 */     Iterator channelItr = this.channelSettings.values().iterator();
/* 647 */     while (channelItr.hasNext())
/*     */     {
/* 649 */       ChannelSettings channelSettings = (ChannelSettings)channelItr.next();
/*     */ 
/* 651 */       if (channelSettings.isRemote()) {
/*     */         continue;
/*     */       }
/* 654 */       gatherUnusedProperties(channelSettings.getId(), channelSettings.getSourceFile(), "channel", channelSettings, findings);
/*     */     }
/*     */ 
/* 658 */     if (!findings.isEmpty())
/*     */     {
/* 660 */       int errorNumber = 10149;
/* 661 */       ConfigurationException exception = new ConfigurationException();
/* 662 */       StringBuffer allDetails = new StringBuffer();
/* 663 */       for (int i = 0; i < findings.size(); i++)
/*     */       {
/* 665 */         allDetails.append(StringUtils.NEWLINE);
/* 666 */         allDetails.append("  ");
/* 667 */         exception.setDetails(errorNumber, "pattern", (Object[])(Object[])findings.get(i));
/* 668 */         allDetails.append(exception.getDetails());
/* 669 */         exception.setDetails(null);
/*     */       }
/* 671 */       exception.setMessage(errorNumber, new Object[] { allDetails });
/* 672 */       throw exception;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void gatherUnusedProperties(String settingsId, String settingsSource, String settingsType, PropertiesSettings settings, Collection result)
/*     */   {
/* 680 */     List unusedProperties = settings.getProperties().findAllUnusedProperties();
/* 681 */     int size = unusedProperties.size();
/* 682 */     if (size > 0)
/*     */     {
/* 684 */       for (int i = 0; i < size; i++)
/*     */       {
/* 686 */         String path = (String)unusedProperties.get(i);
/* 687 */         result.add(new Object[] { path, settingsType, settingsId, settingsSource });
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.MessagingConfiguration
 * JD-Core Version:    0.6.0
 */
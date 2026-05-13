/*      */ package flex.messaging;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*      */ import flex.management.BaseControl;
/*      */ import flex.management.ManageableComponent;
/*      */ import flex.management.runtime.messaging.MessageBrokerControl;
/*      */ import flex.management.runtime.messaging.log.LogManager;
/*      */ import flex.messaging.client.FlexClient;
/*      */ import flex.messaging.client.FlexClientManager;
/*      */ import flex.messaging.cluster.ClusterManager;
/*      */ import flex.messaging.config.ChannelSettings;
/*      */ import flex.messaging.config.ConfigMap;
/*      */ import flex.messaging.config.ConfigurationException;
/*      */ import flex.messaging.config.FlexClientSettings;
/*      */ import flex.messaging.config.SecurityConstraint;
/*      */ import flex.messaging.config.SecuritySettings;
/*      */ import flex.messaging.config.SystemSettings;
/*      */ import flex.messaging.endpoints.Endpoint;
/*      */ import flex.messaging.factories.JavaFactory;
/*      */ import flex.messaging.io.BeanProxy;
/*      */ import flex.messaging.io.PropertyProxyRegistry;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.AbstractMessage;
/*      */ import flex.messaging.messages.AcknowledgeMessage;
/*      */ import flex.messaging.messages.AsyncMessage;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.security.LoginManager;
/*      */ import flex.messaging.security.SecurityException;
/*      */ import flex.messaging.services.Service;
/*      */ import flex.messaging.services.ServiceException;
/*      */ import flex.messaging.util.Base64.Decoder;
/*      */ import flex.messaging.util.ClassUtil;
/*      */ import flex.messaging.util.ExceptionUtil;
/*      */ import flex.messaging.util.PrettyPrinter;
/*      */ import flex.messaging.util.RedeployManager;
/*      */ import flex.messaging.util.StringUtils;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import javax.servlet.ServletContext;
/*      */ 
/*      */ public final class MessageBroker extends ManageableComponent
/*      */ {
/*      */   public static final String LOG_CATEGORY = "Message.General";
/*      */   public static final String LOG_CATEGORY_STARTUP_SERVICE = "Startup.Service";
/*      */   public static final String TYPE = "MessageBroker";
/*      */   private static final String LOG_MANAGER_ID = "log";
/*      */   private static final int NULL_ENDPOINT_URL = 10128;
/*      */   private static final int SERVICE_TYPE_EXISTS = 11113;
/*      */   private static final int NO_SERVICE_FOR_DEST = 10004;
/*      */   private static final int SERVICE_CMD_NOT_SUPPORTED = 10451;
/*      */   private static final int DESTINATION_UNACCESSIBLE = 10005;
/*      */   private static final int UNKNOWN_REMOTE_CREDENTIALS_FORMAT = 10020;
/*      */   private static final int URI_ALREADY_REGISTERED = 11109;
/*      */   private static final int NULL_MESSAGE_ID = 10029;
/*  103 */   private static final Integer INTEGER_ONE = new Integer(1);
/*      */   private InternalPathResolver internalPathResolver;
/*      */   private String writePath;
/*      */   private Map attributes;
/*      */   private Map endpoints;
/*      */   private Map services;
/*      */   private Map servers;
/*      */   private Map factories;
/*      */   private Map registeredEndpoints;
/*      */   private ClusterManager clusterManager;
/*      */   private Map destinationToService;
/*      */   private FlexClientManager flexClientManager;
/*      */   private LoginManager loginManager;
/*      */   private RedeployManager redeployManager;
/*      */   private Map channelSettings;
/*      */   private List defaultChannels;
/*      */   private SecuritySettings securitySettings;
/*      */   private SessionMetricsTracker sessionMetricsTracker;
/*      */   private FlexClientSettings flexClientSettings;
/*  124 */   private static ThreadLocal systemSettingsThreadLocal = new ThreadLocal();
/*      */   private SystemSettings systemSettings;
/*      */   private ServletContext initServletContext;
/*  128 */   private final ConcurrentHashMap serviceValidationListeners = new ConcurrentHashMap();
/*      */   private ClassLoader classLoader;
/*      */   private Log log;
/*      */   private LogManager logManager;
/*      */   static final String DEFAULT_BROKER_ID = "__default__";
/*  136 */   static Map messageBrokers = new HashMap();
/*      */   private MessageBrokerControl controller;
/*  142 */   private Map attributeIdRefCounts = new HashMap();
/*      */ 
/*      */   public MessageBroker()
/*      */   {
/*  153 */     this(true, null);
/*      */   }
/*      */ 
/*      */   public MessageBroker(boolean enableManagement)
/*      */   {
/*  159 */     this(enableManagement, null);
/*      */   }
/*      */ 
/*      */   public MessageBroker(boolean enableManagement, String mbid)
/*      */   {
/*  165 */     this(enableManagement, mbid, MessageBroker.class.getClassLoader());
/*      */   }
/*      */ 
/*      */   public MessageBroker(boolean enableManagement, String mbid, ClassLoader loader)
/*      */   {
/*  171 */     super(enableManagement);
/*  172 */     this.classLoader = loader;
/*  173 */     this.attributes = new ConcurrentHashMap();
/*  174 */     this.destinationToService = new HashMap();
/*  175 */     this.endpoints = new LinkedHashMap();
/*  176 */     this.services = new LinkedHashMap();
/*  177 */     this.servers = new LinkedHashMap();
/*  178 */     this.factories = new HashMap();
/*  179 */     this.registeredEndpoints = new HashMap();
/*      */ 
/*  182 */     addFactory("java", new JavaFactory());
/*      */ 
/*  184 */     setId(mbid);
/*      */ 
/*  186 */     this.log = Log.createLog();
/*      */ 
/*  188 */     this.clusterManager = new ClusterManager(this);
/*  189 */     this.systemSettings = new SystemSettings();
/*  190 */     systemSettingsThreadLocal.set(this.systemSettings);
/*  191 */     this.clusterManager = new ClusterManager(this);
/*  192 */     this.sessionMetricsTracker = new SessionMetricsTracker(this);
/*      */ 
/*  194 */     if (isManaged())
/*      */     {
/*  196 */       this.controller = new MessageBrokerControl(this);
/*  197 */       this.controller.register();
/*  198 */       setControl(this.controller);
/*      */ 
/*  200 */       this.logManager = new LogManager();
/*  201 */       this.logManager.setLog(this.log);
/*  202 */       this.logManager.setParent(this);
/*  203 */       this.logManager.setupLogControl();
/*  204 */       this.logManager.initialize("log", null);
/*      */     }
/*      */ 
/*  207 */     this.flexClientManager = new FlexClientManager(isManaged(), this);
/*      */   }
/*      */ 
/*      */   public void setId(String id)
/*      */   {
/*  218 */     if (id == null)
/*      */     {
/*  220 */       id = "__default__";
/*      */     }
/*  222 */     super.setId(id);
/*      */   }
/*      */ 
/*      */   public static MessageBroker getMessageBroker(String id)
/*      */   {
/*  232 */     if (id == null)
/*  233 */       id = "__default__";
/*  234 */     return (MessageBroker)messageBrokers.get(id);
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  243 */     if (isStarted()) {
/*  244 */       return;
/*      */     }
/*      */ 
/*  250 */     if (Log.isDebug())
/*      */     {
/*  252 */       StringBuffer sb = new StringBuffer();
/*  253 */       if (this.classLoader == MessageBroker.class.getClassLoader())
/*  254 */         sb.append(" the MessageBroker's class loader");
/*  255 */       if (this.classLoader == Thread.currentThread().getContextClassLoader())
/*      */       {
/*  257 */         if (sb.length() > 0) sb.append(" and");
/*  258 */         sb.append(" the context class loader");
/*      */       }
/*  260 */       if (sb.length() == 0)
/*  261 */         sb.append(" not the context or the message broker's class loader");
/*  262 */       Log.getLogger("Configuration").debug("MessageBroker id: " + getId() + " classLoader is:" + sb.toString() + " (" + "classLoader " + ClassUtil.classLoaderToString(this.classLoader));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  272 */       setStarted(true);
/*      */ 
/*  274 */       registerMessageBroker();
/*  275 */       this.sessionMetricsTracker.start();
/*  276 */       this.flexClientManager.start();
/*  277 */       startServices();
/*  278 */       this.loginManager.start();
/*  279 */       startEndpoints();
/*  280 */       startServers();
/*  281 */       this.redeployManager.start();
/*      */ 
/*  283 */       if (isManaged())
/*      */       {
/*  285 */         for (iter = this.services.values().iterator(); iter.hasNext(); )
/*      */         {
/*  287 */           Service service = (Service)iter.next();
/*  288 */           if (service.isManaged())
/*      */           {
/*  290 */             this.controller.addService(service.getControl().getObjectName());
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */       Iterator iter;
/*  297 */       if (Log.isError()) {
/*  298 */         Log.getLogger("Configuration").error("MessageBroker failed to start: " + ExceptionUtil.exceptionToString(e));
/*      */       }
/*      */ 
/*  301 */       RuntimeException re = new RuntimeException(e.getMessage(), e);
/*  302 */       throw re;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void stop()
/*      */   {
/*  312 */     if (!isStarted()) {
/*  313 */       return;
/*      */     }
/*  315 */     if (Log.isDebug()) {
/*  316 */       Log.getLogger("Configuration").debug("MessageBroker stopping: " + getId());
/*      */     }
/*  318 */     this.serviceValidationListeners.clear();
/*      */ 
/*  320 */     this.sessionMetricsTracker.stop();
/*  321 */     this.flexClientManager.stop();
/*  322 */     stopServers();
/*  323 */     stopEndpoints();
/*      */ 
/*  326 */     FlexContext.setThreadLocalObjects(null, null, this);
/*  327 */     stopServices();
/*  328 */     FlexContext.setThreadLocalObjects(null, null, null);
/*      */ 
/*  330 */     if (this.loginManager != null)
/*  331 */       this.loginManager.stop();
/*      */     try
/*      */     {
/*  334 */       if (this.redeployManager != null)
/*  335 */         this.redeployManager.stop();
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*  339 */       t.printStackTrace();
/*      */     }
/*  341 */     this.clusterManager.destroyClusters();
/*      */ 
/*  343 */     super.stop();
/*  344 */     unRegisterMessageBroker();
/*      */ 
/*  347 */     BeanProxy.clear();
/*  348 */     PropertyProxyRegistry.getRegistry().clear();
/*      */ 
/*  351 */     Log.clear();
/*  352 */     FlexContext.clear();
/*      */ 
/*  355 */     this.systemSettings.clear();
/*  356 */     this.systemSettings = null;
/*  357 */     systemSettingsThreadLocal.set(null);
/*      */ 
/*  359 */     if (Log.isDebug())
/*  360 */       Log.getLogger("Configuration").debug("MessageBroker stopped: " + getId());
/*      */   }
/*      */ 
/*      */   public Iterator getAttributeNames()
/*      */   {
/*  370 */     return this.attributes.keySet().iterator();
/*      */   }
/*      */ 
/*      */   public Object getAttribute(String name)
/*      */   {
/*  380 */     return this.attributes.get(name);
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, Object value)
/*      */   {
/*  391 */     if (value == null)
/*  392 */       removeAttribute(name);
/*      */     else
/*  394 */       this.attributes.put(name, value);
/*      */   }
/*      */ 
/*      */   public void removeAttribute(String name)
/*      */   {
/*  404 */     this.attributes.remove(name);
/*      */   }
/*      */ 
/*      */   public void setInternalPathResolver(InternalPathResolver internalPathResolver)
/*      */   {
/*  410 */     this.internalPathResolver = internalPathResolver;
/*      */   }
/*      */ 
/*      */   public InputStream resolveInternalPath(String filename)
/*      */     throws IOException
/*      */   {
/*  416 */     return this.internalPathResolver.resolve(filename);
/*      */   }
/*      */ 
/*      */   public void setFlexWritePath(String p)
/*      */   {
/*  428 */     this.writePath = p;
/*      */   }
/*      */ 
/*      */   public String getFlexWritePath()
/*      */   {
/*  437 */     return this.writePath;
/*      */   }
/*      */ 
/*      */   public ClusterManager getClusterManager()
/*      */   {
/*  443 */     return this.clusterManager;
/*      */   }
/*      */ 
/*      */   public void addServer(Server server)
/*      */   {
/*  454 */     if (server == null)
/*      */     {
/*  457 */       ConfigurationException ex = new ConfigurationException();
/*  458 */       ex.setMessage(11110, new Object[] { "Server", "MessageBroker" });
/*  459 */       throw ex;
/*      */     }
/*      */ 
/*  462 */     String id = server.getId();
/*      */ 
/*  464 */     if (id == null)
/*      */     {
/*  467 */       ConfigurationException ex = new ConfigurationException();
/*  468 */       ex.setMessage(11111, new Object[] { "Server", "MessageBroker" });
/*  469 */       throw ex;
/*      */     }
/*      */ 
/*  473 */     Server currentServer = getServer(id);
/*  474 */     if (currentServer == server) {
/*  475 */       return;
/*      */     }
/*      */ 
/*  478 */     if (currentServer != null)
/*      */     {
/*  481 */       ConfigurationException ex = new ConfigurationException();
/*  482 */       ex.setMessage(11112, new Object[] { "Server", id, "MessageBroker" });
/*  483 */       throw ex;
/*      */     }
/*      */ 
/*  486 */     this.servers.put(id, server);
/*      */   }
/*      */ 
/*      */   public Server getServer(String id)
/*      */   {
/*  499 */     return (Server)this.servers.get(id);
/*      */   }
/*      */ 
/*      */   public Server removeServer(String id)
/*      */   {
/*  510 */     Server server = (Server)this.servers.get(id);
/*  511 */     if (server != null)
/*      */     {
/*  513 */       server.stop();
/*  514 */       this.servers.remove(id);
/*      */     }
/*  516 */     return server;
/*      */   }
/*      */ 
/*      */   public Endpoint createEndpoint(String id, String url, String className)
/*      */   {
/*  534 */     Class endpointClass = ClassUtil.createClass(className, getClassLoader());
/*      */ 
/*  536 */     Endpoint endpoint = (Endpoint)ClassUtil.createDefaultInstance(endpointClass, Endpoint.class);
/*  537 */     endpoint.setId(id);
/*  538 */     endpoint.setUrl(url);
/*  539 */     endpoint.setManaged(isManaged());
/*  540 */     endpoint.setMessageBroker(this);
/*      */ 
/*  542 */     return endpoint;
/*      */   }
/*      */ 
/*      */   public void addEndpoint(Endpoint endpoint)
/*      */   {
/*  555 */     if (endpoint == null)
/*      */     {
/*  558 */       ConfigurationException ex = new ConfigurationException();
/*  559 */       ex.setMessage(11110, new Object[] { "Endpoint", "MessageBroker" });
/*  560 */       throw ex;
/*      */     }
/*      */ 
/*  563 */     String id = endpoint.getId();
/*      */ 
/*  565 */     if (id == null)
/*      */     {
/*  568 */       ConfigurationException ex = new ConfigurationException();
/*  569 */       ex.setMessage(11111, new Object[] { "Endpoint", "MessageBroker" });
/*  570 */       throw ex;
/*      */     }
/*      */ 
/*  574 */     if (getEndpoint(id) == endpoint) {
/*  575 */       return;
/*      */     }
/*      */ 
/*  578 */     if (getEndpoint(id) != null)
/*      */     {
/*  581 */       ConfigurationException ex = new ConfigurationException();
/*  582 */       ex.setMessage(11112, new Object[] { "Endpoint", id, "MessageBroker" });
/*  583 */       throw ex;
/*      */     }
/*      */ 
/*  588 */     checkEndpointUrl(id, endpoint.getUrl());
/*      */ 
/*  591 */     this.endpoints.put(id, endpoint);
/*      */   }
/*      */ 
/*      */   private void checkEndpointUrl(String id, String endpointUrl)
/*      */   {
/*  597 */     if (endpointUrl == null)
/*      */     {
/*  600 */       ConfigurationException ex = new ConfigurationException();
/*  601 */       ex.setMessage(10128, new Object[] { "Endpoint", "MessageBroker" });
/*  602 */       throw ex;
/*      */     }
/*      */ 
/*  605 */     String parsedEndpointURI = ChannelSettings.removeTokens(endpointUrl);
/*      */ 
/*  608 */     if (this.registeredEndpoints.containsKey(parsedEndpointURI))
/*      */     {
/*  610 */       ConfigurationException ce = new ConfigurationException();
/*  611 */       ce.setMessage(11109, new Object[] { id, parsedEndpointURI, this.registeredEndpoints.get(parsedEndpointURI) });
/*      */ 
/*  613 */       throw ce;
/*      */     }
/*      */ 
/*  617 */     this.registeredEndpoints.put(parsedEndpointURI, id);
/*      */ 
/*  620 */     int nextSlash = parsedEndpointURI.indexOf('/', 1);
/*  621 */     if (nextSlash > 0)
/*      */     {
/*  623 */       String parsedEndpointURI2 = parsedEndpointURI.substring(nextSlash);
/*  624 */       if (this.registeredEndpoints.containsKey(parsedEndpointURI2))
/*      */       {
/*  626 */         ConfigurationException ce = new ConfigurationException();
/*  627 */         ce.setMessage(11109, new Object[] { parsedEndpointURI2, id, this.registeredEndpoints.get(parsedEndpointURI2) });
/*      */ 
/*  630 */         throw ce;
/*      */       }
/*  632 */       this.registeredEndpoints.put(parsedEndpointURI2, id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Endpoint getEndpoint(String id)
/*      */   {
/*  646 */     return (Endpoint)this.endpoints.get(id);
/*      */   }
/*      */ 
/*      */   public Map getEndpoints()
/*      */   {
/*  655 */     return this.endpoints;
/*      */   }
/*      */ 
/*      */   public Endpoint getEndpoint(String path, String contextPath)
/*      */   {
/*  665 */     for (Iterator iter = this.endpoints.keySet().iterator(); iter.hasNext(); )
/*      */     {
/*  667 */       Object id = iter.next();
/*  668 */       Endpoint e = (Endpoint)this.endpoints.get(id);
/*      */ 
/*  670 */       if (matchEndpoint(path, contextPath, e))
/*  671 */         return e;
/*      */     }
/*  673 */     MessageException lme = new MessageException();
/*  674 */     lme.setMessage(10003, new Object[] { path });
/*  675 */     throw lme;
/*      */   }
/*      */ 
/*      */   public Endpoint removeEndpoint(String id)
/*      */   {
/*  687 */     Endpoint endpoint = getEndpoint(id);
/*  688 */     if (endpoint != null)
/*      */     {
/*  690 */       endpoint.stop();
/*  691 */       this.endpoints.remove(id);
/*      */     }
/*  693 */     return endpoint;
/*      */   }
/*      */ 
/*      */   private boolean matchEndpoint(String path, String contextPath, Endpoint endpoint)
/*      */   {
/*  709 */     boolean match = false;
/*  710 */     String channelEndpoint = endpoint.getParsedUrl(contextPath);
/*      */ 
/*  712 */     if (path.endsWith("/"))
/*      */     {
/*  714 */       path = path.substring(0, path.length() - 1);
/*      */     }
/*      */ 
/*  717 */     if (path.equalsIgnoreCase(channelEndpoint))
/*      */     {
/*  719 */       match = true;
/*      */     }
/*      */ 
/*  722 */     return match;
/*      */   }
/*      */ 
/*      */   public FlexFactory getFactory(String id)
/*      */   {
/*  734 */     return (FlexFactory)this.factories.get(id);
/*      */   }
/*      */ 
/*      */   public Map getFactories()
/*      */   {
/*  744 */     return this.factories;
/*      */   }
/*      */ 
/*      */   public void addFactory(String id, FlexFactory factory)
/*      */   {
/*  755 */     if (id == null)
/*      */     {
/*  758 */       ConfigurationException ex = new ConfigurationException();
/*  759 */       ex.setMessage(11111, new Object[] { "FlexFactory", "MessageBroker" });
/*  760 */       throw ex;
/*      */     }
/*      */ 
/*  763 */     if (getFactory(id) == factory)
/*      */     {
/*  765 */       return;
/*      */     }
/*      */ 
/*  768 */     if (getFactory(id) != null)
/*      */     {
/*  771 */       ConfigurationException ex = new ConfigurationException();
/*  772 */       ex.setMessage(11112, new Object[] { "FlexFactory", id, "MessageBroker" });
/*  773 */       throw ex;
/*      */     }
/*  775 */     this.factories.put(id, factory);
/*      */   }
/*      */ 
/*      */   public FlexFactory removeFactory(String id)
/*      */   {
/*  787 */     FlexFactory factory = getFactory(id);
/*  788 */     if (factory != null)
/*      */     {
/*  790 */       this.factories.remove(id);
/*      */     }
/*  792 */     return factory;
/*      */   }
/*      */ 
/*      */   public Service getService(String id)
/*      */   {
/*  804 */     return (Service)this.services.get(id);
/*      */   }
/*      */ 
/*      */   public Service getServiceByType(String type)
/*      */   {
/*  810 */     for (Iterator serviceIter = this.services.values().iterator(); serviceIter.hasNext(); )
/*      */     {
/*  812 */       Service svc = (Service)serviceIter.next();
/*  813 */       if (svc.getClass().getName().equals(type))
/*      */       {
/*  815 */         return svc;
/*      */       }
/*      */     }
/*  818 */     return null;
/*      */   }
/*      */ 
/*      */   public Map getServices()
/*      */   {
/*  828 */     return this.services;
/*      */   }
/*      */ 
/*      */   public ConfigMap describeServices(Endpoint endpoint)
/*      */   {
/*      */     Enumeration iter;
/*  839 */     if (!this.serviceValidationListeners.isEmpty())
/*      */     {
/*  841 */       for (iter = this.serviceValidationListeners.elements(); iter.hasMoreElements(); )
/*      */       {
/*  843 */         ((ServiceValidationListener)iter.nextElement()).validateServices();
/*      */       }
/*      */     }
/*      */ 
/*  847 */     ConfigMap servicesConfig = new ConfigMap();
/*      */ 
/*  851 */     ArrayList channelIds = new ArrayList();
/*  852 */     channelIds.add(endpoint.getId());
/*      */ 
/*  854 */     if (this.defaultChannels != null)
/*      */     {
/*  856 */       ConfigMap defaultChannelsMap = new ConfigMap();
/*  857 */       for (Iterator iter = this.defaultChannels.iterator(); iter.hasNext(); )
/*      */       {
/*  859 */         String id = (String)iter.next();
/*  860 */         ConfigMap channelConfig = new ConfigMap();
/*  861 */         channelConfig.addProperty("ref", id);
/*  862 */         defaultChannelsMap.addProperty("channel", channelConfig);
/*  863 */         if (!channelIds.contains(id))
/*  864 */           channelIds.add(id);
/*      */       }
/*  866 */       if (defaultChannelsMap.size() > 0) {
/*  867 */         servicesConfig.addProperty("default-channels", defaultChannelsMap);
/*      */       }
/*      */     }
/*  870 */     for (Iterator iter = this.services.values().iterator(); iter.hasNext(); )
/*      */     {
/*  872 */       Service service = (Service)iter.next();
/*  873 */       ConfigMap serviceConfig = service.describeService(endpoint);
/*  874 */       if ((serviceConfig != null) && (serviceConfig.size() > 0)) {
/*  875 */         servicesConfig.addProperty("service", serviceConfig);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  881 */     ConfigMap channels = new ConfigMap();
/*  882 */     for (Iterator iter = channelIds.iterator(); iter.hasNext(); )
/*      */     {
/*  884 */       String id = (String)iter.next();
/*  885 */       Endpoint currentEndpoint = getEndpoint(id);
/*      */ 
/*  887 */       ConfigMap channel = currentEndpoint.describeEndpoint();
/*  888 */       if (channel.size() > 0)
/*  889 */         channels.addProperty("channel", channel);
/*      */     }
/*  891 */     if (channels.size() > 0) {
/*  892 */       servicesConfig.addProperty("channels", channels);
/*      */     }
/*  894 */     if (Log.isDebug()) {
/*  895 */       Log.getLogger("Configuration").debug("Returning service description for endpoint: " + endpoint + " config: " + servicesConfig);
/*      */     }
/*      */ 
/*  898 */     return servicesConfig;
/*      */   }
/*      */ 
/*      */   public void addServiceValidationListener(String id, ServiceValidationListener listener)
/*      */   {
/*  910 */     if (listener != null)
/*      */     {
/*  912 */       this.serviceValidationListeners.putIfAbsent(id, listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeServiceValidationListener(String id)
/*      */   {
/*  923 */     if (this.serviceValidationListeners.containsKey(id))
/*      */     {
/*  925 */       this.serviceValidationListeners.remove(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Service createService(String id, String className)
/*      */   {
/*  942 */     Class svcClass = ClassUtil.createClass(className, getClassLoader());
/*      */ 
/*  944 */     Service service = (Service)ClassUtil.createDefaultInstance(svcClass, Service.class);
/*  945 */     service.setId(id);
/*  946 */     service.setManaged(isManaged());
/*  947 */     service.setMessageBroker(this);
/*      */ 
/*  949 */     return service;
/*      */   }
/*      */ 
/*      */   public void addService(Service service)
/*      */   {
/*  970 */     if (service == null)
/*      */     {
/*  973 */       ConfigurationException ex = new ConfigurationException();
/*  974 */       ex.setMessage(11110, new Object[] { "Service", "MessageBroker" });
/*  975 */       throw ex;
/*      */     }
/*      */ 
/*  978 */     String id = service.getId();
/*      */ 
/*  980 */     if (id == null)
/*      */     {
/*  983 */       ConfigurationException ex = new ConfigurationException();
/*  984 */       ex.setMessage(11111, new Object[] { "Service", "MessageBroker" });
/*  985 */       throw ex;
/*      */     }
/*      */ 
/*  988 */     if (getService(id) == service)
/*      */     {
/*  990 */       return;
/*      */     }
/*      */ 
/*  993 */     if (getService(id) != null)
/*      */     {
/*  996 */       ConfigurationException ex = new ConfigurationException();
/*  997 */       ex.setMessage(11112, new Object[] { "Service", id, "MessageBroker" });
/*  998 */       throw ex;
/*      */     }
/*      */ 
/* 1001 */     String type = service.getClass().getName();
/* 1002 */     if (getServiceByType(type) != null)
/*      */     {
/* 1004 */       ConfigurationException ce = new ConfigurationException();
/* 1005 */       ce.setMessage(11113, new Object[] { type });
/* 1006 */       throw ce;
/*      */     }
/*      */ 
/* 1009 */     this.services.put(id, service);
/*      */ 
/* 1011 */     if ((service.getMessageBroker() == null) || (service.getMessageBroker() != this))
/*      */     {
/* 1013 */       service.setMessageBroker(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Service removeService(String id)
/*      */   {
/* 1026 */     Service service = getService(id);
/* 1027 */     if (service != null)
/*      */     {
/* 1029 */       service.stop();
/* 1030 */       this.services.remove(id);
/*      */     }
/* 1032 */     return service;
/*      */   }
/*      */ 
/*      */   public Log getLog()
/*      */   {
/* 1042 */     return this.log;
/*      */   }
/*      */ 
/*      */   public LoginManager getLoginManager()
/*      */   {
/* 1048 */     return this.loginManager;
/*      */   }
/*      */ 
/*      */   public void setLoginManager(LoginManager loginManager)
/*      */   {
/* 1054 */     if ((this.loginManager != null) && (this.loginManager.isStarted())) {
/* 1055 */       this.loginManager.stop();
/*      */     }
/* 1057 */     this.loginManager = loginManager;
/*      */ 
/* 1059 */     if (isStarted())
/* 1060 */       loginManager.start();
/*      */   }
/*      */ 
/*      */   public FlexClientManager getFlexClientManager()
/*      */   {
/* 1066 */     return this.flexClientManager;
/*      */   }
/*      */ 
/*      */   public void setFlexClientManager(FlexClientManager value)
/*      */   {
/* 1072 */     this.flexClientManager = value;
/*      */   }
/*      */ 
/*      */   public RedeployManager getRedeployManager()
/*      */   {
/* 1078 */     return this.redeployManager;
/*      */   }
/*      */ 
/*      */   public void setRedeployManager(RedeployManager redeployManager)
/*      */   {
/* 1084 */     if ((this.redeployManager != null) && (this.redeployManager.isStarted())) {
/* 1085 */       this.redeployManager.stop();
/*      */     }
/* 1087 */     this.redeployManager = redeployManager;
/*      */ 
/* 1089 */     if (isStarted())
/* 1090 */       redeployManager.start();
/*      */   }
/*      */ 
/*      */   public List getChannelIds()
/*      */   {
/* 1098 */     return (this.endpoints == null) || (this.endpoints.size() == 0) ? null : new ArrayList(this.endpoints.keySet());
/*      */   }
/*      */ 
/*      */   public ChannelSettings getChannelSettings(String ref)
/*      */   {
/* 1105 */     return (ChannelSettings)this.channelSettings.get(ref);
/*      */   }
/*      */ 
/*      */   public Map getAllChannelSettings()
/*      */   {
/* 1111 */     return this.channelSettings;
/*      */   }
/*      */ 
/*      */   public void setChannelSettings(Map channelSettings)
/*      */   {
/* 1117 */     this.channelSettings = channelSettings;
/*      */   }
/*      */ 
/*      */   public List getDefaultChannels()
/*      */   {
/* 1128 */     return this.defaultChannels;
/*      */   }
/*      */ 
/*      */   public void addDefaultChannel(String id)
/*      */   {
/* 1138 */     if (this.defaultChannels == null)
/* 1139 */       this.defaultChannels = new ArrayList();
/* 1140 */     else if (this.defaultChannels.contains(id)) {
/* 1141 */       return;
/*      */     }
/* 1143 */     List channelIds = getChannelIds();
/* 1144 */     if ((channelIds == null) || (!channelIds.contains(id)))
/*      */     {
/* 1147 */       if (Log.isWarn())
/*      */       {
/* 1149 */         Log.getLogger("Message.General").warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*      */       }
/*      */ 
/* 1153 */       return;
/*      */     }
/* 1155 */     this.defaultChannels.add(id);
/*      */   }
/*      */ 
/*      */   public void setDefaultChannels(List ids)
/*      */   {
/*      */     List channelIds;
/*      */     Iterator iter;
/* 1165 */     if (ids != null)
/*      */     {
/* 1167 */       channelIds = getChannelIds();
/* 1168 */       for (iter = ids.iterator(); iter.hasNext(); )
/*      */       {
/* 1170 */         String id = (String)iter.next();
/* 1171 */         if ((channelIds == null) || (!channelIds.contains(id)))
/*      */         {
/* 1173 */           iter.remove();
/* 1174 */           if (Log.isWarn())
/*      */           {
/* 1176 */             Log.getLogger("Message.General").warn("No channel with id '{0}' is known by the MessageBroker. Not adding the channel.", new Object[] { id });
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1183 */     this.defaultChannels = ids;
/*      */   }
/*      */ 
/*      */   public boolean removeDefaultChannel(String id)
/*      */   {
/* 1194 */     if (this.defaultChannels == null)
/* 1195 */       return false;
/* 1196 */     return this.defaultChannels.remove(id);
/*      */   }
/*      */ 
/*      */   public SecurityConstraint getSecurityConstraint(String ref)
/*      */   {
/* 1208 */     return getSecuritySettings().getConstraint(ref);
/*      */   }
/*      */ 
/*      */   public ServletContext getInitServletContext()
/*      */   {
/* 1214 */     return this.initServletContext;
/*      */   }
/*      */ 
/*      */   protected void setInitServletContext(ServletContext initServletContext)
/*      */   {
/* 1220 */     this.initServletContext = initServletContext;
/*      */   }
/*      */ 
/*      */   public SecuritySettings getSecuritySettings()
/*      */   {
/* 1226 */     return this.securitySettings;
/*      */   }
/*      */ 
/*      */   public void setSecuritySettings(SecuritySettings securitySettings)
/*      */   {
/* 1232 */     this.securitySettings = securitySettings;
/*      */   }
/*      */ 
/*      */   public SystemSettings getLocalSystemSettings()
/*      */   {
/* 1238 */     return this.systemSettings;
/*      */   }
/*      */ 
/*      */   public static SystemSettings getSystemSettings()
/*      */   {
/* 1244 */     SystemSettings ss = (SystemSettings)systemSettingsThreadLocal.get();
/* 1245 */     if (ss == null)
/*      */     {
/* 1247 */       ss = new SystemSettings();
/* 1248 */       systemSettingsThreadLocal.set(ss);
/*      */     }
/* 1250 */     return ss;
/*      */   }
/*      */ 
/*      */   public void setSystemSettings(SystemSettings l)
/*      */   {
/* 1256 */     if (l != null)
/*      */     {
/* 1258 */       systemSettingsThreadLocal.set(l);
/* 1259 */       this.systemSettings = l;
/*      */     }
/*      */   }
/*      */ 
/*      */   public FlexClientSettings getFlexClientSettings()
/*      */   {
/* 1266 */     return this.flexClientSettings;
/*      */   }
/*      */ 
/*      */   public void setFlexClientSettings(FlexClientSettings value)
/*      */   {
/* 1272 */     this.flexClientSettings = value;
/*      */   }
/*      */ 
/*      */   public void initThreadLocals()
/*      */   {
/* 1279 */     setSystemSettings(this.systemSettings);
/*      */   }
/*      */ 
/*      */   private void startServers()
/*      */   {
/* 1287 */     for (Iterator iter = this.servers.entrySet().iterator(); iter.hasNext(); )
/*      */     {
/* 1289 */       Server server = (Server)((Map.Entry)iter.next()).getValue();
/* 1290 */       server.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopServers()
/*      */   {
/* 1299 */     for (Iterator iter = this.servers.entrySet().iterator(); iter.hasNext(); )
/*      */     {
/* 1301 */       Server server = (Server)((Map.Entry)iter.next()).getValue();
/* 1302 */       server.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startEndpoints()
/*      */   {
/* 1313 */     for (Iterator iter = this.endpoints.values().iterator(); iter.hasNext(); )
/*      */     {
/* 1315 */       Endpoint endpoint = (Endpoint)iter.next();
/* 1316 */       endpoint.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopEndpoints()
/*      */   {
/* 1325 */     for (Iterator iter = this.endpoints.values().iterator(); iter.hasNext(); )
/*      */     {
/* 1327 */       Endpoint endpoint = (Endpoint)iter.next();
/* 1328 */       endpoint.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startServices()
/*      */   {
/* 1339 */     for (Iterator iter = this.services.values().iterator(); iter.hasNext(); )
/*      */     {
/* 1341 */       Service svc = (Service)iter.next();
/*      */ 
/* 1343 */       long timeBeforeStartup = 0L;
/* 1344 */       if (Log.isDebug())
/*      */       {
/* 1346 */         timeBeforeStartup = System.currentTimeMillis();
/* 1347 */         Log.getLogger("Startup.Service").debug("Service with id '{0}' is starting.", new Object[] { svc.getId() });
/*      */       }
/*      */ 
/* 1351 */       svc.start();
/*      */ 
/* 1353 */       if (Log.isDebug())
/*      */       {
/* 1355 */         long timeAfterStartup = System.currentTimeMillis();
/* 1356 */         Long diffMillis = new Long(timeAfterStartup - timeBeforeStartup);
/* 1357 */         Log.getLogger("Startup.Service").debug("Service with id '{0}' is ready (startup time: '{1}' ms)", new Object[] { svc.getId(), diffMillis });
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopServices()
/*      */   {
/* 1370 */     for (Iterator iter = this.services.values().iterator(); iter.hasNext(); )
/*      */     {
/* 1372 */       Service svc = (Service)iter.next();
/* 1373 */       svc.stop();
/*      */     }
/*      */   }
/*      */ 
/*      */   public AcknowledgeMessage routeMessageToService(Message message, Endpoint endpoint)
/*      */   {
/* 1392 */     checkMessageId(message);
/*      */ 
/* 1394 */     Object serviceResult = null;
/* 1395 */     boolean serviced = false;
/* 1396 */     Service service = null;
/* 1397 */     String destId = message.getDestination();
/*      */     try
/*      */     {
/* 1400 */       String serviceId = (String)this.destinationToService.get(destId);
/* 1401 */       if (serviceId != null)
/*      */       {
/* 1403 */         service = (Service)this.services.get(serviceId);
/* 1404 */         serviced = true;
/* 1405 */         Destination destination = service.getDestination(destId);
/* 1406 */         inspectOperation(message, destination);
/*      */ 
/* 1408 */         if (message.headerExists("DSValidateEndpoint")) {
/* 1409 */           message.getHeaders().remove("DSValidateEndpoint");
/*      */         }
/* 1411 */         if (Log.isDebug()) {
/* 1412 */           Log.getLogger(getLogCategory(message)).debug("Before invoke service: " + service.getId() + StringUtils.NEWLINE + "  incomingMessage: " + message + StringUtils.NEWLINE);
/*      */         }
/*      */ 
/* 1416 */         extractRemoteCredentials(service, message);
/* 1417 */         serviceResult = service.serviceMessage(message);
/*      */       }
/*      */ 
/* 1420 */       if (!serviced)
/*      */       {
/* 1422 */         MessageException lme = new MessageException();
/*      */ 
/* 1424 */         lme.setMessage(10004, new Object[] { destId });
/* 1425 */         throw lme;
/*      */       }
/*      */ 
/* 1428 */       if (Log.isDebug())
/*      */       {
/* 1430 */         String debugServiceResult = Log.getPrettyPrinter().prettify(serviceResult);
/* 1431 */         Log.getLogger(getLogCategory(message)).debug("After invoke service: " + service.getId() + StringUtils.NEWLINE + "  reply: " + debugServiceResult + StringUtils.NEWLINE);
/*      */       }
/*      */ 
/* 1436 */       AcknowledgeMessage ack = null;
/* 1437 */       if ((serviceResult instanceof AcknowledgeMessage))
/*      */       {
/* 1441 */         ack = (AcknowledgeMessage)serviceResult;
/*      */       }
/*      */       else
/*      */       {
/* 1447 */         ack = new AcknowledgeMessage();
/* 1448 */         ack.setBody(serviceResult);
/*      */       }
/* 1450 */       ack.setCorrelationId(message.getMessageId());
/* 1451 */       ack.setClientId(message.getClientId());
/* 1452 */       return ack;
/*      */     }
/*      */     catch (MessageException exc)
/*      */     {
/* 1456 */       Log.getLogger("Message.General").error("Exception when invoking service: " + (service == null ? "(none)" : service.getId()) + StringUtils.NEWLINE + "  with message: " + message + StringUtils.NEWLINE + "  exception: " + exc + StringUtils.NEWLINE);
/*      */ 
/* 1463 */       if (exc.getRootCause() != null) {
/* 1464 */         Log.getLogger("Message.General").error("Root cause: " + ExceptionUtil.toString(exc.getRootCause()));
/*      */       }
/*      */ 
/* 1468 */       throw exc;
/*      */     }
/*      */     catch (RuntimeException exc)
/*      */     {
/* 1472 */       Log.getLogger("Message.General").error("Exception when invoking service: " + (service == null ? "(none)" : service.getId()) + StringUtils.NEWLINE + "  with message: " + message + StringUtils.NEWLINE + "  exception: " + ExceptionUtil.toString(exc) + StringUtils.NEWLINE);
/*      */ 
/* 1479 */       throw exc;
/*      */     }
/*      */     catch (Error exc)
/*      */     {
/* 1483 */       Log.getLogger("Message.General").error("Error when invoking service: " + (service == null ? "(none)" : service.getId()) + StringUtils.NEWLINE + "  with message: " + message + StringUtils.NEWLINE + "  error: " + ExceptionUtil.toString(exc) + StringUtils.NEWLINE);
/*      */     }
/*      */ 
/* 1490 */     throw exc;
/*      */   }
/*      */ 
/*      */   public AsyncMessage routeCommandToService(CommandMessage command, Endpoint endpoint)
/*      */   {
/* 1498 */     checkMessageId(command);
/*      */ 
/* 1500 */     String destId = command.getDestination();
/*      */ 
/* 1502 */     AsyncMessage replyMessage = null;
/* 1503 */     Service service = null;
/* 1504 */     String serviceId = null;
/* 1505 */     Object commandResult = null;
/* 1506 */     boolean serviced = false;
/*      */ 
/* 1509 */     if ((command.getOperation() == 8) || (command.getOperation() == 9))
/*      */     {
/* 1511 */       serviceId = "authentication-service";
/*      */     }
/* 1513 */     else serviceId = (String)this.destinationToService.get(destId);
/*      */ 
/* 1515 */     service = (Service)this.services.get(serviceId);
/* 1516 */     if (service != null)
/*      */     {
/* 1520 */       Destination destination = service.getDestination(destId);
/* 1521 */       if (destination != null) {
/* 1522 */         inspectOperation(command, destination);
/*      */       }
/*      */       try
/*      */       {
/* 1526 */         extractRemoteCredentials(service, command);
/* 1527 */         commandResult = service.serviceCommand(command);
/* 1528 */         serviced = true;
/*      */       }
/*      */       catch (UnsupportedOperationException e)
/*      */       {
/* 1532 */         ServiceException se = new ServiceException();
/* 1533 */         se.setMessage(10451, new Object[] { service.getClass().getName() });
/* 1534 */         throw se;
/*      */       }
/*      */       catch (SecurityException se)
/*      */       {
/* 1541 */         if (serviceId.equals("authentication-service"))
/*      */         {
/* 1543 */           commandResult = se.createErrorMessage();
/* 1544 */           if (Log.isDebug()) {
/* 1545 */             Log.getLogger("Message.General").debug("Security error for message: " + se.toString() + StringUtils.NEWLINE + "  incomingMessage: " + command + StringUtils.NEWLINE + "  errorReply: " + commandResult);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1552 */           throw se;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1557 */     if (commandResult == null)
/*      */     {
/* 1559 */       replyMessage = new AcknowledgeMessage();
/*      */     }
/* 1561 */     else if ((commandResult instanceof AsyncMessage))
/*      */     {
/* 1563 */       replyMessage = (AsyncMessage)commandResult;
/*      */     }
/*      */     else
/*      */     {
/* 1567 */       replyMessage = new AcknowledgeMessage();
/* 1568 */       replyMessage.setBody(commandResult);
/*      */     }
/*      */ 
/* 1573 */     if ((command.getOperation() == 5) || (command.getOperation() == 8))
/*      */     {
/* 1576 */       boolean needsConfig = false;
/* 1577 */       if (command.getHeader("DSNeedsConfig") != null) {
/* 1578 */         needsConfig = ((Boolean)(Boolean)command.getHeader("DSNeedsConfig")).booleanValue();
/*      */       }
/*      */ 
/* 1581 */       if (needsConfig)
/*      */       {
/* 1583 */         ConfigMap serverConfig = describeServices(endpoint);
/* 1584 */         if (serverConfig.size() > 0) {
/* 1585 */           replyMessage.setBody(serverConfig);
/*      */         }
/*      */       }
/*      */ 
/* 1589 */       double msgVersion = endpoint.getMessagingVersion();
/* 1590 */       if (msgVersion > 0.0D) {
/* 1591 */         replyMessage.setHeader("DSMessagingVersion", new Double(msgVersion));
/*      */       }
/*      */ 
/* 1594 */       FlexClient flexClient = FlexContext.getFlexClient();
/* 1595 */       if (flexClient != null)
/* 1596 */         replyMessage.setHeader("DSId", flexClient.getId());
/*      */     }
/* 1598 */     else if (!serviced)
/*      */     {
/* 1600 */       MessageException lme = new MessageException();
/*      */ 
/* 1602 */       lme.setMessage(10004, new Object[] { destId });
/* 1603 */       throw lme;
/*      */     }
/*      */ 
/* 1606 */     replyMessage.setCorrelationId(command.getMessageId());
/* 1607 */     replyMessage.setClientId(command.getClientId());
/* 1608 */     if ((replyMessage.getBody() instanceof List))
/*      */     {
/* 1610 */       replyMessage.setBody(((List)replyMessage.getBody()).toArray());
/*      */     }
/*      */ 
/* 1613 */     if (Log.isDebug()) {
/* 1614 */       Log.getLogger(getLogCategory(command)).debug("Executed command: " + (service == null ? "(default service)" : new StringBuffer().append("service=").append(service.getId()).toString()) + StringUtils.NEWLINE + "  commandMessage: " + command + StringUtils.NEWLINE + "  replyMessage: " + replyMessage + StringUtils.NEWLINE);
/*      */     }
/*      */ 
/* 1621 */     return replyMessage;
/*      */   }
/*      */ 
/*      */   public void routeMessageToMessageClient(Message message, MessageClient messageClient)
/*      */   {
/* 1633 */     checkMessageId(message);
/*      */ 
/* 1635 */     if (Log.isDebug()) {
/* 1636 */       Log.getLogger(getLogCategory(message)).debug("Queuing message to send to MessageClient: " + messageClient.getClientId() + StringUtils.NEWLINE + " for FlexClient: " + messageClient.getFlexClient().getId() + StringUtils.NEWLINE + "  message: " + message + StringUtils.NEWLINE);
/*      */     }
/*      */ 
/* 1647 */     FlexClient requestFlexClient = FlexContext.getFlexClient();
/* 1648 */     FlexSession requestFlexSession = FlexContext.getFlexSession();
/*      */ 
/* 1650 */     FlexClient pushFlexClient = messageClient.getFlexClient();
/* 1651 */     FlexContext.setThreadLocalFlexClient(pushFlexClient);
/* 1652 */     FlexContext.setThreadLocalSession(null);
/*      */ 
/* 1654 */     pushFlexClient.push(message, messageClient);
/*      */ 
/* 1657 */     FlexContext.setThreadLocalFlexClient(requestFlexClient);
/* 1658 */     FlexContext.setThreadLocalSession(requestFlexSession);
/*      */   }
/*      */ 
/*      */   private void checkMessageId(Message message)
/*      */   {
/* 1667 */     if (message.getMessageId() == null)
/*      */     {
/* 1669 */       MessageException lme = new MessageException();
/* 1670 */       lme.setMessage(10029);
/* 1671 */       throw lme;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void inspectOperation(Message message, Destination destination)
/*      */   {
/* 1682 */     inspectChannel(message, destination);
/* 1683 */     this.loginManager.checkConstraint(destination.getSecurityConstraint());
/*      */   }
/*      */ 
/*      */   public void inspectChannel(Message message, Destination destination)
/*      */   {
/* 1692 */     if (message.getHeader("DSValidateEndpoint") != null)
/*      */     {
/* 1694 */       String messageChannel = (String)message.getHeader("DSEndpoint");
/* 1695 */       for (Iterator iter = destination.getChannels().iterator(); iter.hasNext(); )
/*      */       {
/* 1697 */         String channelId = (String)iter.next();
/* 1698 */         if (channelId.equals(messageChannel))
/*      */         {
/* 1700 */           return;
/*      */         }
/*      */       }
/* 1703 */       MessageException lme = new MessageException();
/* 1704 */       lme.setMessage(10005, new Object[] { destination.getId(), messageChannel });
/* 1705 */       throw lme;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void extractRemoteCredentials(Service service, Message message)
/*      */   {
/* 1718 */     if (message.headerExists("DSRemoteCredentials"))
/*      */     {
/* 1720 */       boolean setting = false;
/* 1721 */       String username = null;
/* 1722 */       Object credentials = null;
/* 1723 */       if ((message.getHeader("DSRemoteCredentials") instanceof String))
/*      */       {
/* 1725 */         String encoded = (String)message.getHeader("DSRemoteCredentials");
/* 1726 */         if (encoded.length() > 0)
/*      */         {
/* 1728 */           setting = true;
/* 1729 */           Base64.Decoder decoder = new Base64.Decoder();
/* 1730 */           decoder.decode(encoded);
/* 1731 */           byte[] decodedBytes = decoder.drain();
/* 1732 */           String decoded = "";
/*      */ 
/* 1734 */           String charset = (String)message.getHeader("DSRemoteCredentialsCharset");
/* 1735 */           if (charset != null)
/*      */           {
/*      */             try
/*      */             {
/* 1739 */               decoded = new String(decodedBytes, charset);
/*      */             }
/*      */             catch (UnsupportedEncodingException ex)
/*      */             {
/* 1743 */               MessageException lme = new MessageException();
/* 1744 */               lme.setMessage(10020);
/* 1745 */               throw lme;
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1750 */             decoded = new String(decodedBytes);
/*      */           }
/*      */ 
/* 1753 */           int colon = decoded.indexOf(":");
/* 1754 */           if ((colon > 0) && (colon < decoded.length() - 1))
/*      */           {
/* 1756 */             username = decoded.substring(0, colon);
/* 1757 */             credentials = decoded.substring(colon + 1);
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1763 */         MessageException lme = new MessageException();
/* 1764 */         lme.setMessage(10020);
/* 1765 */         throw lme;
/*      */       }
/*      */ 
/* 1768 */       if (setting)
/*      */       {
/* 1770 */         FlexContext.getFlexSession().putRemoteCredentials(new FlexRemoteCredentials(service.getId(), message.getDestination(), username, credentials));
/*      */       }
/*      */       else
/*      */       {
/* 1776 */         FlexContext.getFlexSession().clearRemoteCredentials(service.getId(), message.getDestination());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getLogCategory(Message message)
/*      */   {
/* 1788 */     if ((message instanceof AbstractMessage)) {
/* 1789 */       return ((AbstractMessage)message).logCategory();
/*      */     }
/* 1791 */     return "Message.General";
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader()
/*      */   {
/* 1799 */     return this.classLoader;
/*      */   }
/*      */ 
/*      */   public void registerDestination(String destId, String svcId)
/*      */   {
/* 1813 */     if (this.destinationToService.containsKey(destId))
/*      */     {
/* 1816 */       ConfigurationException ex = new ConfigurationException();
/* 1817 */       ex.setMessage(11119, new Object[] { destId, svcId, this.destinationToService.get(destId) });
/* 1818 */       throw ex;
/*      */     }
/* 1820 */     this.destinationToService.put(destId, svcId);
/*      */   }
/*      */ 
/*      */   public void unregisterDestination(String destId)
/*      */   {
/* 1832 */     this.destinationToService.remove(destId);
/*      */   }
/*      */ 
/*      */   private void registerMessageBroker()
/*      */   {
/* 1837 */     String mbid = getId();
/*      */ 
/* 1839 */     synchronized (messageBrokers)
/*      */     {
/* 1841 */       if (messageBrokers.get(mbid) != null)
/*      */       {
/* 1843 */         ConfigurationException ce = new ConfigurationException();
/* 1844 */         ce.setMessage(10137, new Object[] { getId() == null ? "(no value supplied)" : mbid });
/* 1845 */         throw ce;
/*      */       }
/* 1847 */       messageBrokers.put(mbid, this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void unRegisterMessageBroker()
/*      */   {
/* 1853 */     String mbid = getId();
/*      */ 
/* 1855 */     synchronized (messageBrokers)
/*      */     {
/* 1857 */       messageBrokers.remove(mbid);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String getLogCategory()
/*      */   {
/* 1863 */     return "Message.General";
/*      */   }
/*      */ 
/*      */   public void incrementAttributeIdRefCount(String attributeId)
/*      */   {
/* 1873 */     synchronized (this.attributeIdRefCounts)
/*      */     {
/* 1875 */       Integer currentCount = (Integer)this.attributeIdRefCounts.get(attributeId);
/* 1876 */       if (currentCount == null)
/*      */       {
/* 1878 */         this.attributeIdRefCounts.put(attributeId, INTEGER_ONE);
/*      */       }
/*      */       else
/*      */       {
/* 1882 */         this.attributeIdRefCounts.put(attributeId, new Integer(currentCount.intValue() + 1));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int decrementAttributeIdRefCount(String attributeId)
/*      */   {
/* 1894 */     synchronized (this.attributeIdRefCounts)
/*      */     {
/* 1896 */       Integer currentCount = (Integer)this.attributeIdRefCounts.get(attributeId);
/* 1897 */       if (currentCount == null)
/*      */       {
/* 1899 */         return 0;
/*      */       }
/*      */ 
/* 1903 */       int newValue = currentCount.intValue() - 1;
/* 1904 */       this.attributeIdRefCounts.put(attributeId, new Integer(newValue));
/* 1905 */       return newValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface InternalPathResolver
/*      */   {
/*      */     public abstract InputStream resolve(String paramString)
/*      */       throws IOException;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageBroker
 * JD-Core Version:    0.6.0
 */
/*      */ package flex.messaging.endpoints;
/*      */ 
/*      */ import flex.management.BaseControl;
/*      */ import flex.management.ManageableComponent;
/*      */ import flex.management.runtime.messaging.MessageBrokerControl;
/*      */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*      */ import flex.messaging.FlexContext;
/*      */ import flex.messaging.FlexSession;
/*      */ import flex.messaging.MessageBroker;
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.Server;
/*      */ import flex.messaging.client.FlexClient;
/*      */ import flex.messaging.client.FlexClientManager;
/*      */ import flex.messaging.client.FlexClientOutboundQueueProcessor;
/*      */ import flex.messaging.client.FlushResult;
/*      */ import flex.messaging.client.PollFlushResult;
/*      */ import flex.messaging.config.ConfigMap;
/*      */ import flex.messaging.config.ConfigurationConstants;
/*      */ import flex.messaging.config.ConfigurationException;
/*      */ import flex.messaging.config.SecurityConstraint;
/*      */ import flex.messaging.io.ClassAliasRegistry;
/*      */ import flex.messaging.io.SerializationContext;
/*      */ import flex.messaging.io.TypeMarshaller;
/*      */ import flex.messaging.io.TypeMarshallingContext;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.AcknowledgeMessage;
/*      */ import flex.messaging.messages.AcknowledgeMessageExt;
/*      */ import flex.messaging.messages.AsyncMessage;
/*      */ import flex.messaging.messages.AsyncMessageExt;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.CommandMessageExt;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.messages.SmallMessage;
/*      */ import flex.messaging.security.LoginManager;
/*      */ import flex.messaging.security.SecurityException;
/*      */ import flex.messaging.util.ClassUtil;
/*      */ import flex.messaging.util.PrettyPrinter;
/*      */ import flex.messaging.util.StringUtils;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ 
/*      */ public abstract class AbstractEndpoint extends ManageableComponent
/*      */   implements Endpoint2, ConfigurationConstants
/*      */ {
/*      */   public static final String LOG_CATEGORY = "Endpoint.General";
/*      */   private static final int NONSECURE_PROTOCOL = 10066;
/*      */   private static final int REQUIRES_FLEXCLIENT_SUPPORT = 10030;
/*      */   private static final String SERIALIZATION = "serialization";
/*      */   private static final String CUSTOM_DESERIALIZER = "custom-deserializer";
/*      */   private static final String CUSTOM_SERIALIZER = "custom-serializer";
/*      */   private static final String ENABLE_SMALL_MESSAGES = "enable-small-messages";
/*      */   private static final String TYPE_MARSHALLER = "type-marshaller";
/*      */   private static final String RESTORE_REFERENCES = "restore-references";
/*      */   private static final String INSTANTIATE_TYPES = "instantiate-types";
/*      */   private static final String SUPPORT_REMOTE_CLASS = "support-remote-class";
/*      */   private static final String LEGACY_COLLECTION = "legacy-collection";
/*      */   private static final String LEGACY_MAP = "legacy-map";
/*      */   private static final String LEGACY_XML = "legacy-xml";
/*      */   private static final String LEGACY_XML_NAMESPACES = "legacy-xml-namespaces";
/*      */   private static final String LEGACY_THROWABLE = "legacy-throwable";
/*      */   private static final String LEGACY_BIG_NUMBERS = "legacy-big-numbers";
/*      */   private static final String LOG_PROPERTY_ERRORS = "log-property-errors";
/*      */   private static final String IGNORE_PROPERTY_ERRORS = "ignore-property-errors";
/*      */   private static final String CONNECT_TIMEOUT_SECONDS = "connect-timeout-seconds";
/*      */   private static final String FLEX_CLIENT_OUTBOUND_QUEUE_PROCESSOR = "flex-client-outbound-queue-processor";
/*      */   private static final String SHOW_STACKTRACES = "show-stacktraces";
/*      */   protected String clientType;
/*      */   protected int connectTimeoutSeconds;
/*      */   protected FlexClientOutboundQueueProcessor flexClientOutboundQueueProcessor;
/*      */   protected SerializationContext serializationContext;
/*      */   protected Class deserializerClass;
/*      */   protected Class serializerClass;
/*      */   protected TypeMarshaller typeMarshaller;
/*      */   protected int port;
/*      */   private SecurityConstraint securityConstraint;
/*      */   protected String url;
/*      */   protected boolean recordMessageSizes;
/*      */   protected boolean recordMessageTimes;
/*      */   protected Server server;
/*      */   protected String parsedUrl;
/*      */   protected boolean contextParsed;
/*      */   protected boolean clientContextParsed;
/*      */   protected String parsedClientUrl;
/*      */   protected Logger log;
/*      */   protected Class flexClientOutboundQueueProcessClass;
/*      */   protected ConfigMap flexClientOutboundQueueProcessorConfig;
/*  131 */   protected double messagingVersion = 1.0D;
/*      */ 
/*      */   public AbstractEndpoint()
/*      */   {
/*  144 */     this(false);
/*      */   }
/*      */ 
/*      */   public AbstractEndpoint(boolean enableManagement)
/*      */   {
/*  155 */     super(enableManagement);
/*  156 */     this.log = Log.getLogger(getLogCategory());
/*  157 */     this.serializationContext = new SerializationContext();
/*      */   }
/*      */ 
/*      */   public void initialize(String id, ConfigMap properties)
/*      */   {
/*  175 */     super.initialize(id, properties);
/*      */ 
/*  177 */     if ((properties == null) || (properties.size() == 0)) {
/*  178 */       return;
/*      */     }
/*      */ 
/*  181 */     this.connectTimeoutSeconds = properties.getPropertyAsInt("connect-timeout-seconds", 0);
/*      */ 
/*  184 */     ConfigMap outboundQueueConfig = properties.getPropertyAsMap("flex-client-outbound-queue-processor", null);
/*  185 */     if (outboundQueueConfig != null)
/*      */     {
/*  188 */       this.flexClientOutboundQueueProcessorConfig = outboundQueueConfig.getPropertyAsMap("properties", null);
/*      */ 
/*  190 */       String pClassName = outboundQueueConfig.getPropertyAsString("class", null);
/*  191 */       if (pClassName != null)
/*      */       {
/*      */         try
/*      */         {
/*  195 */           this.flexClientOutboundQueueProcessClass = createClass(pClassName);
/*      */ 
/*  197 */           setFlexClientOutboundQueueProcessorConfig(this.flexClientOutboundQueueProcessorConfig);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  201 */           if (Log.isWarn()) {
/*  202 */             this.log.warn("Cannot register custom FlexClient outbound queue processor class {1}", new Object[] { pClassName }, t);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  207 */     ConfigMap serialization = properties.getPropertyAsMap("serialization", null);
/*  208 */     if (serialization != null)
/*      */     {
/*  211 */       List deserializers = serialization.getPropertyAsList("custom-deserializer", null);
/*  212 */       if ((deserializers != null) && (Log.isWarn()))
/*      */       {
/*  214 */         this.log.warn("Endpoint <custom-deserializer> functionality is no longer available. Please remove this entry from your configuration.");
/*      */       }
/*      */ 
/*  218 */       List serializers = serialization.getPropertyAsList("custom-serializer", null);
/*  219 */       if ((serializers != null) && (Log.isWarn()))
/*      */       {
/*  221 */         this.log.warn("Endpoint <custom-serializer> functionality is no longer available. Please remove this entry from your configuration.");
/*      */       }
/*      */ 
/*  225 */       String typeMarshallerClassName = serialization.getPropertyAsString("type-marshaller", null);
/*  226 */       if ((typeMarshallerClassName != null) && (typeMarshallerClassName.length() > 0))
/*      */       {
/*      */         try
/*      */         {
/*  230 */           Class tmc = createClass(typeMarshallerClassName);
/*  231 */           this.typeMarshaller = ((TypeMarshaller)ClassUtil.createDefaultInstance(tmc, TypeMarshaller.class));
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  235 */           if (Log.isWarn()) {
/*  236 */             this.log.warn("Cannot register custom type marshaller for type {0}", new Object[] { typeMarshallerClassName }, t);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  241 */       this.serializationContext.enableSmallMessages = serialization.getPropertyAsBoolean("enable-small-messages", true);
/*  242 */       this.serializationContext.instantiateTypes = serialization.getPropertyAsBoolean("instantiate-types", true);
/*  243 */       this.serializationContext.supportRemoteClass = serialization.getPropertyAsBoolean("support-remote-class", false);
/*  244 */       this.serializationContext.legacyCollection = serialization.getPropertyAsBoolean("legacy-collection", false);
/*  245 */       this.serializationContext.legacyMap = serialization.getPropertyAsBoolean("legacy-map", false);
/*  246 */       this.serializationContext.legacyXMLDocument = serialization.getPropertyAsBoolean("legacy-xml", false);
/*  247 */       this.serializationContext.legacyXMLNamespaces = serialization.getPropertyAsBoolean("legacy-xml-namespaces", false);
/*  248 */       this.serializationContext.legacyThrowable = serialization.getPropertyAsBoolean("legacy-throwable", false);
/*  249 */       this.serializationContext.legacyBigNumbers = serialization.getPropertyAsBoolean("legacy-big-numbers", false);
/*  250 */       boolean showStacktraces = serialization.getPropertyAsBoolean("show-stacktraces", false);
/*  251 */       if ((showStacktraces) && (Log.isWarn()))
/*  252 */         this.log.warn("The show-stacktraces configuration option is deprecated and non-functional. Please remove this from your configuration file.");
/*  253 */       this.serializationContext.restoreReferences = serialization.getPropertyAsBoolean("restore-references", false);
/*  254 */       this.serializationContext.logPropertyErrors = serialization.getPropertyAsBoolean("log-property-errors", false);
/*  255 */       this.serializationContext.ignorePropertyErrors = serialization.getPropertyAsBoolean("ignore-property-errors", true);
/*      */     }
/*      */ 
/*  258 */     this.recordMessageSizes = properties.getPropertyAsBoolean("record-message-sizes", false);
/*      */ 
/*  260 */     if ((this.recordMessageSizes) && (Log.isWarn())) {
/*  261 */       this.log.warn("Setting <record-message-sizes> to true affects application performance and should only be used for debugging");
/*      */     }
/*  263 */     this.recordMessageTimes = properties.getPropertyAsBoolean("record-message-times", false);
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  273 */     if (isStarted()) {
/*  274 */       return;
/*      */     }
/*      */ 
/*  277 */     MessageBroker broker = getMessageBroker();
/*  278 */     if (!broker.isStarted())
/*      */     {
/*  280 */       if (Log.isWarn())
/*      */       {
/*  282 */         Log.getLogger(getLogCategory()).warn("Endpoint with id '{0}' cannot be started when the MessageBroker is not started.", new Object[] { getId() });
/*      */       }
/*      */ 
/*  286 */       return;
/*      */     }
/*      */ 
/*  290 */     if ((isManaged()) && (broker.isManaged()))
/*      */     {
/*  292 */       setupEndpointControl(broker);
/*  293 */       MessageBrokerControl controller = (MessageBrokerControl)broker.getControl();
/*  294 */       if (getControl() != null) {
/*  295 */         controller.addEndpoint(this);
/*      */       }
/*      */     }
/*      */ 
/*  299 */     if (this.deserializerClass == null)
/*      */     {
/*  301 */       this.deserializerClass = createClass(getDeserializerClassName());
/*      */     }
/*      */ 
/*  304 */     if (this.serializerClass == null)
/*      */     {
/*  306 */       String serializerClassName = null;
/*      */       try
/*      */       {
/*  309 */         serializerClassName = getSerializerJava15ClassName();
/*  310 */         this.serializerClass = createClass(serializerClassName);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  314 */         serializerClassName = getSerializerClassName();
/*  315 */         this.serializerClass = createClass(serializerClassName);
/*      */       }
/*      */     }
/*      */ 
/*  319 */     this.serializationContext.setDeserializerClass(this.deserializerClass);
/*  320 */     this.serializationContext.setSerializerClass(this.serializerClass);
/*      */ 
/*  323 */     ClassAliasRegistry registry = ClassAliasRegistry.getRegistry();
/*  324 */     registry.registerAlias("DSA", AsyncMessageExt.class.getName());
/*  325 */     registry.registerAlias("DSK", AcknowledgeMessageExt.class.getName());
/*  326 */     registry.registerAlias("DSC", CommandMessageExt.class.getName());
/*  327 */     super.start();
/*      */   }
/*      */ 
/*      */   public void stop()
/*      */   {
/*  336 */     if (!isStarted()) {
/*  337 */       return;
/*      */     }
/*  339 */     super.stop();
/*      */ 
/*  342 */     if ((isManaged()) && (getMessageBroker().isManaged()))
/*      */     {
/*  344 */       if (getControl() != null)
/*      */       {
/*  346 */         getControl().unregister();
/*  347 */         setControl(null);
/*      */       }
/*  349 */       setManaged(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getClientType()
/*      */   {
/*  366 */     return this.clientType;
/*      */   }
/*      */ 
/*      */   public void setClientType(String type)
/*      */   {
/*  376 */     this.clientType = type;
/*      */   }
/*      */ 
/*      */   public Class getFlexClientOutboundQueueProcessorClass()
/*      */   {
/*  386 */     return this.flexClientOutboundQueueProcessClass;
/*      */   }
/*      */ 
/*      */   public void setFlexClientOutboundQueueProcessorClass(Class flexClientOutboundQueueProcessorClass)
/*      */   {
/*  396 */     this.flexClientOutboundQueueProcessClass = flexClientOutboundQueueProcessorClass;
/*  397 */     if ((this.flexClientOutboundQueueProcessClass != null) && (this.flexClientOutboundQueueProcessorConfig != null))
/*      */     {
/*  399 */       FlexClientOutboundQueueProcessor processor = (FlexClientOutboundQueueProcessor)ClassUtil.createDefaultInstance(this.flexClientOutboundQueueProcessClass, null);
/*  400 */       processor.initialize(this.flexClientOutboundQueueProcessorConfig);
/*      */     }
/*      */   }
/*      */ 
/*      */   public ConfigMap getFlexClientOutboundQueueProcessorConfig()
/*      */   {
/*  411 */     return this.flexClientOutboundQueueProcessorConfig;
/*      */   }
/*      */ 
/*      */   public void setFlexClientOutboundQueueProcessorConfig(ConfigMap flexClientOutboundQueueProcessorConfig)
/*      */   {
/*  421 */     this.flexClientOutboundQueueProcessorConfig = flexClientOutboundQueueProcessorConfig;
/*  422 */     if ((flexClientOutboundQueueProcessorConfig != null) && (this.flexClientOutboundQueueProcessClass != null))
/*      */     {
/*  424 */       FlexClientOutboundQueueProcessor processor = (FlexClientOutboundQueueProcessor)ClassUtil.createDefaultInstance(this.flexClientOutboundQueueProcessClass, null);
/*  425 */       processor.initialize(flexClientOutboundQueueProcessorConfig);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setId(String id)
/*      */   {
/*  436 */     String oldId = getId();
/*      */ 
/*  438 */     if ((oldId != null) && (oldId.equals(id))) {
/*  439 */       return;
/*      */     }
/*  441 */     super.setId(id);
/*      */ 
/*  444 */     MessageBroker broker = getMessageBroker();
/*  445 */     if (broker != null)
/*      */     {
/*  448 */       broker.removeEndpoint(oldId);
/*  449 */       broker.addEndpoint(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public MessageBroker getMessageBroker()
/*      */   {
/*  460 */     return (MessageBroker)getParent();
/*      */   }
/*      */ 
/*      */   public void setMessageBroker(MessageBroker broker)
/*      */   {
/*  472 */     MessageBroker oldBroker = getMessageBroker();
/*      */ 
/*  474 */     setParent(broker);
/*      */ 
/*  476 */     if (oldBroker != null) {
/*  477 */       oldBroker.removeEndpoint(getId());
/*      */     }
/*      */ 
/*  480 */     if (broker.getEndpoint(getId()) != this)
/*  481 */       broker.addEndpoint(this);
/*      */   }
/*      */ 
/*      */   public double getMessagingVersion()
/*      */   {
/*  490 */     return this.messagingVersion;
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  502 */     return this.port;
/*      */   }
/*      */ 
/*      */   public boolean isSecure()
/*      */   {
/*  512 */     return false;
/*      */   }
/*      */ 
/*      */   public Server getServer()
/*      */   {
/*  521 */     return this.server;
/*      */   }
/*      */ 
/*      */   public void setServer(Server server)
/*      */   {
/*  529 */     this.server = server;
/*      */   }
/*      */ 
/*      */   public SecurityConstraint getSecurityConstraint()
/*      */   {
/*  539 */     return this.securityConstraint;
/*      */   }
/*      */ 
/*      */   public void setSecurityConstraint(SecurityConstraint securityConstraint)
/*      */   {
/*  549 */     this.securityConstraint = securityConstraint;
/*      */   }
/*      */ 
/*      */   public SerializationContext getSerializationContext()
/*      */   {
/*  559 */     return this.serializationContext;
/*      */   }
/*      */ 
/*      */   public void setSerializationContext(SerializationContext serializationContext)
/*      */   {
/*  569 */     this.serializationContext = serializationContext;
/*      */   }
/*      */ 
/*      */   public TypeMarshaller getTypeMarshaller()
/*      */   {
/*  579 */     if (this.typeMarshaller == null)
/*      */     {
/*  581 */       String typeMarshallerClassName = null;
/*  582 */       Class typeMarshallerClass = null;
/*      */       try
/*      */       {
/*  585 */         typeMarshallerClassName = "flex.messaging.io.Java15TypeMarshaller";
/*  586 */         typeMarshallerClass = createClass(typeMarshallerClassName);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*  590 */         typeMarshallerClassName = "flex.messaging.io.amf.translator.ASTranslator";
/*  591 */         typeMarshallerClass = createClass(typeMarshallerClassName);
/*      */       }
/*  593 */       this.typeMarshaller = ((TypeMarshaller)ClassUtil.createDefaultInstance(typeMarshallerClass, TypeMarshaller.class));
/*      */     }
/*      */ 
/*  596 */     return this.typeMarshaller;
/*      */   }
/*      */ 
/*      */   public void setTypeMarshaller(TypeMarshaller typeMarshaller)
/*      */   {
/*  606 */     this.typeMarshaller = typeMarshaller;
/*      */   }
/*      */ 
/*      */   public String getUrl()
/*      */   {
/*  616 */     return this.url;
/*      */   }
/*      */ 
/*      */   public void setUrl(String url)
/*      */   {
/*  626 */     this.url = url;
/*  627 */     this.port = parsePort(url);
/*  628 */     this.contextParsed = false;
/*  629 */     this.clientContextParsed = false;
/*      */   }
/*      */ 
/*      */   public String getUrlForClient()
/*      */   {
/*  640 */     if (!this.clientContextParsed)
/*      */     {
/*  642 */       HttpServletRequest req = FlexContext.getHttpRequest();
/*  643 */       if (req != null)
/*      */       {
/*  645 */         String contextPath = req.getContextPath();
/*  646 */         parseClientUrl(contextPath);
/*      */       }
/*      */       else
/*      */       {
/*  650 */         return this.url;
/*      */       }
/*      */     }
/*  653 */     return this.parsedClientUrl;
/*      */   }
/*      */ 
/*      */   public long getThroughput()
/*      */   {
/*  664 */     EndpointControl control = (EndpointControl)getControl();
/*      */ 
/*  666 */     return control.getBytesDeserialized().longValue() + control.getBytesSerialized().longValue();
/*      */   }
/*      */ 
/*      */   public static void addNoCacheHeaders(HttpServletRequest req, HttpServletResponse res)
/*      */   {
/*  678 */     res.setHeader("Cache-Control", "no-cache");
/*  679 */     res.setDateHeader("Expires", 946080000000L);
/*      */ 
/*  682 */     String userAgent = req.getHeader("User-Agent");
/*  683 */     if ((!req.isSecure()) || (userAgent == null) || (userAgent.indexOf("MSIE") == -1))
/*      */     {
/*  685 */       res.setHeader("Pragma", "no-cache");
/*      */     }
/*      */   }
/*      */ 
/*      */   public Message convertToSmallMessage(Message message)
/*      */   {
/*  694 */     if ((message instanceof SmallMessage))
/*      */     {
/*  696 */       Message smallMessage = ((SmallMessage)message).getSmallMessage();
/*  697 */       if (smallMessage != null) {
/*  698 */         message = smallMessage;
/*      */       }
/*      */     }
/*  701 */     return message;
/*      */   }
/*      */ 
/*      */   public ConfigMap describeEndpoint()
/*      */   {
/*  715 */     ConfigMap channelConfig = new ConfigMap();
/*      */ 
/*  717 */     channelConfig.addProperty("id", getId());
/*  718 */     channelConfig.addProperty("type", getClientType());
/*      */ 
/*  720 */     ConfigMap endpointConfig = new ConfigMap();
/*  721 */     endpointConfig.addProperty("uri", getUrlForClient());
/*  722 */     channelConfig.addProperty("endpoint", endpointConfig);
/*      */ 
/*  724 */     ConfigMap properties = new ConfigMap();
/*  725 */     if (this.connectTimeoutSeconds > 0)
/*      */     {
/*  727 */       ConfigMap connectTimeoutConfig = new ConfigMap();
/*  728 */       connectTimeoutConfig.addProperty("", String.valueOf(this.connectTimeoutSeconds));
/*  729 */       properties.addProperty("connect-timeout-seconds", connectTimeoutConfig);
/*      */     }
/*      */ 
/*  732 */     if (this.recordMessageTimes)
/*      */     {
/*  734 */       ConfigMap recordMessageTimesMap = new ConfigMap();
/*      */ 
/*  736 */       recordMessageTimesMap.addProperty("", "true");
/*  737 */       properties.addProperty("record-message-times", recordMessageTimesMap);
/*      */     }
/*      */ 
/*  740 */     if (this.recordMessageSizes)
/*      */     {
/*  742 */       ConfigMap recordMessageSizessMap = new ConfigMap();
/*      */ 
/*  744 */       recordMessageSizessMap.addProperty("", "true");
/*  745 */       properties.addProperty("record-message-sizes", recordMessageSizessMap);
/*      */     }
/*      */ 
/*  748 */     ConfigMap serialization = new ConfigMap();
/*  749 */     serialization.addProperty("enable-small-messages", Boolean.toString(this.serializationContext.enableSmallMessages));
/*  750 */     properties.addProperty("serialization", serialization);
/*      */ 
/*  752 */     if (properties.size() > 0) {
/*  753 */       channelConfig.addProperty("properties", properties);
/*      */     }
/*  755 */     return channelConfig;
/*      */   }
/*      */ 
/*      */   public String getParsedUrl(String contextPath)
/*      */   {
/*  764 */     if (!this.contextParsed) {
/*  765 */       parseUrl(contextPath);
/*      */     }
/*  767 */     return this.parsedUrl;
/*      */   }
/*      */ 
/*      */   public void handleClientMessagingVersion(Number version)
/*      */   {
/*  775 */     if (version != null)
/*      */     {
/*  777 */       boolean clientSupportsSmallMessages = version.doubleValue() >= this.messagingVersion;
/*  778 */       if ((clientSupportsSmallMessages) && (getSerializationContext().enableSmallMessages))
/*      */       {
/*  780 */         FlexSession session = FlexContext.getFlexSession();
/*  781 */         if (session != null)
/*  782 */           session.setUseSmallMessages(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void service(HttpServletRequest req, HttpServletResponse res)
/*      */   {
/*  794 */     validateRequestProtocol(req);
/*      */   }
/*      */ 
/*      */   public Message serviceMessage(Message message)
/*      */   {
/*  803 */     if (isManaged())
/*      */     {
/*  805 */       ((EndpointControl)getControl()).incrementServiceMessageCount();
/*      */     }
/*      */ 
/*  808 */     Message ack = null;
/*      */ 
/*  811 */     if (message.getTimestamp() == 0L)
/*      */     {
/*  813 */       message.setTimestamp(System.currentTimeMillis());
/*      */     }
/*      */ 
/*  820 */     if (message.getHeader("DSEndpoint") != null)
/*  821 */       message.setHeader("DSValidateEndpoint", Boolean.TRUE);
/*  822 */     message.setHeader("DSEndpoint", getId());
/*      */ 
/*  824 */     if ((message instanceof CommandMessage))
/*      */     {
/*  826 */       CommandMessage command = (CommandMessage)message;
/*      */ 
/*  829 */       if (command.getOperation() != 8) {
/*  830 */         checkSecurityConstraint(message);
/*      */       }
/*      */ 
/*  835 */       int operation = command.getOperation();
/*  836 */       if ((operation == 2) && (message.getClientId() == null))
/*      */       {
/*  838 */         verifyFlexClientSupport(command);
/*      */ 
/*  841 */         FlexClient flexClient = FlexContext.getFlexClient();
/*  842 */         ack = handleFlexClientPollCommand(flexClient, command);
/*      */       }
/*  844 */       else if (operation == 12)
/*      */       {
/*  846 */         ack = handleChannelDisconnect(command);
/*      */       }
/*      */       else
/*      */       {
/*  852 */         if ((operation == 0) || (operation == 2)) {
/*  853 */           verifyFlexClientSupport(command);
/*      */         }
/*  855 */         ack = getMessageBroker().routeCommandToService((CommandMessage)message, this);
/*      */ 
/*  858 */         if ((operation == 5) || (operation == 8))
/*      */         {
/*  860 */           Number clientVersion = (Number)command.getHeader("DSMessagingVersion");
/*  861 */           handleClientMessagingVersion(clientVersion);
/*      */ 
/*  865 */           ack.setHeader("DSMessagingVersion", new Double(this.messagingVersion));
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  872 */       if ((message instanceof AsyncMessage)) {
/*  873 */         verifyFlexClientSupport(message);
/*      */       }
/*      */ 
/*  876 */       checkSecurityConstraint(message);
/*      */ 
/*  878 */       ack = getMessageBroker().routeMessageToService(message, this);
/*      */     }
/*      */ 
/*  881 */     return ack;
/*      */   }
/*      */ 
/*      */   public FlexClient setupFlexClient(Message message)
/*      */   {
/*  896 */     FlexClient flexClient = null;
/*  897 */     if (message.getHeaders().containsKey("DSId"))
/*      */     {
/*  899 */       String id = (String)message.getHeaders().get("DSId");
/*      */ 
/*  902 */       if (id == null)
/*  903 */         id = "nil";
/*  904 */       flexClient = setupFlexClient(id);
/*      */     }
/*  906 */     return flexClient;
/*      */   }
/*      */ 
/*      */   public FlexClient setupFlexClient(String id)
/*      */   {
/*  921 */     FlexClient flexClient = null;
/*  922 */     if (id != null)
/*      */     {
/*  926 */       if (id.equals("nil")) {
/*  927 */         id = null;
/*      */       }
/*  929 */       flexClient = getMessageBroker().getFlexClientManager().getFlexClient(id);
/*      */ 
/*  931 */       FlexSession session = FlexContext.getFlexSession();
/*  932 */       flexClient.registerFlexSession(session);
/*      */ 
/*  934 */       FlexContext.setThreadLocalFlexClient(flexClient);
/*      */     }
/*  936 */     return flexClient;
/*      */   }
/*      */ 
/*      */   public boolean isRecordMessageSizes()
/*      */   {
/*  945 */     return this.recordMessageSizes;
/*      */   }
/*      */ 
/*      */   public boolean isRecordMessageTimes()
/*      */   {
/*  954 */     return this.recordMessageTimes;
/*      */   }
/*      */ 
/*      */   protected String getLogCategory()
/*      */   {
/*  971 */     return "Endpoint.General";
/*      */   }
/*      */ 
/*      */   protected Message handleChannelDisconnect(CommandMessage disconnectCommand)
/*      */   {
/*  984 */     return new AcknowledgeMessage();
/*      */   }
/*      */ 
/*      */   protected FlushResult handleFlexClientPoll(FlexClient flexClient, CommandMessage pollCommand)
/*      */   {
/*  999 */     return flexClient.poll(getId());
/*      */   }
/*      */ 
/*      */   protected Message handleFlexClientPollCommand(FlexClient flexClient, CommandMessage pollCommand)
/*      */   {
/* 1012 */     if (Log.isDebug()) {
/* 1013 */       Log.getLogger(getMessageBroker().getLogCategory(pollCommand)).debug("Before handling general client poll request. " + StringUtils.NEWLINE + "  incomingMessage: " + pollCommand + StringUtils.NEWLINE);
/*      */     }
/*      */ 
/* 1017 */     FlushResult flushResult = handleFlexClientPoll(flexClient, pollCommand);
/* 1018 */     Message pollResponse = null;
/*      */ 
/* 1022 */     if (((flushResult instanceof PollFlushResult)) && (((PollFlushResult)flushResult).isClientProcessingSuppressed()))
/*      */     {
/* 1024 */       pollResponse = new CommandMessage(4);
/* 1025 */       pollResponse.setHeader("DSNoOpPoll", Boolean.TRUE);
/*      */     }
/*      */ 
/* 1028 */     if (pollResponse == null)
/*      */     {
/* 1030 */       List messagesToReturn = flushResult != null ? flushResult.getMessages() : null;
/* 1031 */       if ((messagesToReturn != null) && (!messagesToReturn.isEmpty()))
/*      */       {
/* 1033 */         pollResponse = new CommandMessage(4);
/* 1034 */         pollResponse.setBody(messagesToReturn.toArray());
/*      */       }
/*      */       else
/*      */       {
/* 1038 */         pollResponse = new AcknowledgeMessage();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1043 */     if (flushResult != null)
/*      */     {
/* 1045 */       int nextFlushWaitTime = flushResult.getNextFlushWaitTimeMillis();
/* 1046 */       if (nextFlushWaitTime > 0) {
/* 1047 */         pollResponse.setHeader("DSPollWait", new Integer(nextFlushWaitTime));
/*      */       }
/*      */     }
/* 1050 */     if (Log.isDebug())
/*      */     {
/* 1052 */       String debugPollResult = Log.getPrettyPrinter().prettify(pollResponse);
/* 1053 */       Log.getLogger(getMessageBroker().getLogCategory(pollCommand)).debug("After handling general client poll request. " + StringUtils.NEWLINE + "  reply: " + debugPollResult + StringUtils.NEWLINE);
/*      */     }
/*      */ 
/* 1058 */     return pollResponse;
/*      */   }
/*      */ 
/*      */   protected void checkSecurityConstraint(Message message)
/*      */   {
/* 1063 */     if (this.securityConstraint != null)
/*      */     {
/* 1065 */       getMessageBroker().getLoginManager().checkConstraint(this.securityConstraint);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setThreadLocals()
/*      */   {
/* 1071 */     if (this.serializationContext != null) {
/* 1072 */       SerializationContext.setSerializationContext((SerializationContext)this.serializationContext.clone());
/*      */     }
/* 1074 */     TypeMarshallingContext.setTypeMarshaller(getTypeMarshaller());
/*      */   }
/*      */ 
/*      */   protected void clearThreadLocals()
/*      */   {
/* 1079 */     SerializationContext.setSerializationContext(null);
/* 1080 */     TypeMarshallingContext.setTypeMarshaller(null);
/*      */   }
/*      */ 
/*      */   protected abstract String getDeserializerClassName();
/*      */ 
/*      */   protected abstract String getSerializerClassName();
/*      */ 
/*      */   protected abstract String getSerializerJava15ClassName();
/*      */ 
/*      */   protected abstract void setupEndpointControl(MessageBroker paramMessageBroker);
/*      */ 
/*      */   protected void validateRequestProtocol(HttpServletRequest req)
/*      */   {
/* 1119 */     boolean secure = req.isSecure();
/* 1120 */     if ((!secure) && (isSecure()))
/*      */     {
/* 1123 */       String endpointPath = req.getServletPath() + req.getPathInfo();
/* 1124 */       SecurityException se = new SecurityException();
/* 1125 */       se.setMessage(10066, new Object[] { endpointPath });
/* 1126 */       throw se;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void verifyFlexClientSupport(Message message)
/*      */   {
/* 1139 */     if (FlexContext.getFlexClient() == null)
/*      */     {
/* 1141 */       MessageException me = new MessageException();
/* 1142 */       me.setMessage(10030, new Object[] { message.getDestination() });
/* 1143 */       throw me;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Class createClass(String className)
/*      */   {
/* 1152 */     Class c = ClassUtil.createClass(className, FlexContext.getMessageBroker() == null ? null : FlexContext.getMessageBroker().getClassLoader());
/*      */ 
/* 1155 */     return c;
/*      */   }
/*      */ 
/*      */   private void parseClientUrl(String contextPath)
/*      */   {
/* 1161 */     if (!this.clientContextParsed)
/*      */     {
/* 1163 */       String channelEndpoint = this.url.trim();
/*      */ 
/* 1166 */       channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", "{context.root}");
/*      */ 
/* 1168 */       if ((contextPath == null) && (channelEndpoint.indexOf("{context.root}") != -1))
/*      */       {
/* 1171 */         ConfigurationException e = new ConfigurationException();
/* 1172 */         e.setMessage(11120, new Object[] { getId() });
/* 1173 */         throw e;
/*      */       }
/*      */ 
/* 1178 */       if ((contextPath != null) && (!contextPath.startsWith("/")))
/*      */       {
/* 1180 */         contextPath = "/" + contextPath;
/*      */       }
/*      */ 
/* 1185 */       if (channelEndpoint.indexOf("/{context.root}") != -1)
/*      */       {
/* 1189 */         if (("/".equals(contextPath)) && (!"/{context.root}".equals(channelEndpoint))) {
/* 1190 */           contextPath = "";
/*      */         }
/* 1192 */         channelEndpoint = StringUtils.substitute(channelEndpoint, "/{context.root}", contextPath);
/*      */       }
/*      */       else
/*      */       {
/* 1199 */         if (("/".equals(contextPath)) && (!"{context.root}".equals(channelEndpoint))) {
/* 1200 */           contextPath = "";
/*      */         }
/* 1202 */         channelEndpoint = StringUtils.substitute(channelEndpoint, "{context.root}", contextPath);
/*      */       }
/*      */ 
/* 1205 */       this.parsedClientUrl = channelEndpoint;
/* 1206 */       this.clientContextParsed = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int parsePort(String url)
/*      */   {
/* 1221 */     int port = 0;
/*      */ 
/* 1225 */     int start = url.indexOf(":/");
/* 1226 */     if (start > 0)
/*      */     {
/* 1229 */       start += 3;
/* 1230 */       int end = url.indexOf('/', start);
/*      */ 
/* 1233 */       String snp = end == -1 ? url.substring(start) : url.substring(start, end);
/*      */ 
/* 1236 */       int delim = snp.indexOf("]");
/* 1237 */       delim = delim > -1 ? snp.indexOf(":", delim) : snp.indexOf(":");
/*      */ 
/* 1239 */       if (delim > 0)
/*      */       {
/*      */         try
/*      */         {
/* 1243 */           int p = Integer.parseInt(snp.substring(delim + 1));
/* 1244 */           if (p > 0) {
/* 1245 */             port = p;
/*      */           }
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */ 
/*      */       }
/* 1253 */       else if (delim == -1)
/*      */       {
/* 1255 */         if (Log.isWarn()) {
/* 1256 */           this.log.warn("No port specified in channel URL:  {1}", new Object[] { url });
/*      */         }
/*      */       }
/*      */     }
/* 1260 */     return port;
/*      */   }
/*      */ 
/*      */   private void parseUrl(String contextPath)
/*      */   {
/* 1266 */     if (!this.contextParsed)
/*      */     {
/* 1268 */       String channelEndpoint = this.url.toLowerCase().trim();
/*      */ 
/* 1271 */       if ((channelEndpoint.startsWith("http://")) || (channelEndpoint.startsWith("https://")))
/*      */       {
/* 1273 */         int nextSlash = channelEndpoint.indexOf('/', 8);
/* 1274 */         if (nextSlash > 0)
/*      */         {
/* 1276 */           channelEndpoint = channelEndpoint.substring(nextSlash);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1281 */       channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", "{context.root}");
/*      */ 
/* 1284 */       if (channelEndpoint.startsWith("{context.root}"))
/*      */       {
/* 1286 */         channelEndpoint = channelEndpoint.substring("{context.root}".length());
/*      */       }
/* 1288 */       else if (channelEndpoint.startsWith("/{context.root}"))
/*      */       {
/* 1290 */         channelEndpoint = channelEndpoint.substring("/{context.root}".length());
/*      */       }
/* 1292 */       else if (contextPath.length() > 0)
/*      */       {
/* 1294 */         if (channelEndpoint.startsWith(contextPath.toLowerCase()))
/*      */         {
/* 1296 */           channelEndpoint = channelEndpoint.substring(contextPath.length());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1301 */       if (channelEndpoint.endsWith("/"))
/*      */       {
/* 1303 */         channelEndpoint = channelEndpoint.substring(0, channelEndpoint.length() - 1);
/*      */       }
/*      */ 
/* 1306 */       this.parsedUrl = channelEndpoint;
/* 1307 */       this.contextParsed = true;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.AbstractEndpoint
 * JD-Core Version:    0.6.0
 */
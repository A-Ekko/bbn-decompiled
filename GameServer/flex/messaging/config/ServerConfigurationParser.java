/*      */ package flex.messaging.config;
/*      */ 
/*      */ import flex.messaging.util.LocaleUtils;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public abstract class ServerConfigurationParser extends AbstractConfigurationParser
/*      */ {
/*      */   protected void parseTopLevelConfig(Document doc)
/*      */   {
/*   45 */     Node root = selectSingleNode(doc, "/services-config");
/*      */ 
/*   47 */     if (root != null)
/*      */     {
/*   50 */       allowedChildElements(root, SERVICES_CONFIG_CHILDREN);
/*      */ 
/*   53 */       securitySection(root);
/*      */ 
/*   56 */       serversSection(root);
/*      */ 
/*   59 */       channelsSection(root);
/*      */ 
/*   62 */       services(root);
/*      */ 
/*   65 */       clusters(root);
/*      */ 
/*   68 */       logging(root);
/*      */ 
/*   71 */       system(root);
/*      */ 
/*   74 */       flexClient(root);
/*      */ 
/*   77 */       factories(root);
/*      */     }
/*      */     else
/*      */     {
/*   82 */       ConfigurationException e = new ConfigurationException();
/*   83 */       e.setMessage(10103, new Object[] { "services-config" });
/*   84 */       throw e;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void clusters(Node root)
/*      */   {
/*   90 */     Node clusteringNode = selectSingleNode(root, "clusters");
/*   91 */     if (clusteringNode != null)
/*      */     {
/*   93 */       allowedAttributesOrElements(clusteringNode, CLUSTERING_CHILDREN);
/*      */ 
/*   95 */       NodeList clusters = selectNodeList(clusteringNode, "cluster");
/*   96 */       for (int i = 0; i < clusters.getLength(); i++)
/*      */       {
/*   98 */         Node cluster = clusters.item(i);
/*   99 */         requiredAttributesOrElements(cluster, CLUSTER_DEFINITION_CHILDREN);
/*  100 */         String clusterName = getAttributeOrChildElement(cluster, "id");
/*  101 */         if (!isValidID(clusterName))
/*      */           continue;
/*  103 */         String propsFileName = getAttributeOrChildElement(cluster, "properties");
/*  104 */         ClusterSettings clusterSettings = new ClusterSettings();
/*  105 */         clusterSettings.setClusterName(clusterName);
/*  106 */         clusterSettings.setPropsFileName(propsFileName);
/*  107 */         String className = getAttributeOrChildElement(cluster, "class");
/*  108 */         if ((className != null) && (className.length() > 0))
/*      */         {
/*  110 */           clusterSettings.setImplementationClass(className);
/*      */         }
/*  112 */         String defaultValue = getAttributeOrChildElement(cluster, "default");
/*  113 */         if ((defaultValue != null) && (defaultValue.length() > 0))
/*      */         {
/*  115 */           if (defaultValue.equalsIgnoreCase("true")) {
/*  116 */             clusterSettings.setDefault(true);
/*  117 */           } else if (!defaultValue.equalsIgnoreCase("false"))
/*      */           {
/*  119 */             ConfigurationException e = new ConfigurationException();
/*  120 */             e.setMessage(10215, new Object[] { clusterName, defaultValue });
/*  121 */             throw e;
/*      */           }
/*      */         }
/*  124 */         String ulb = getAttributeOrChildElement(cluster, "url-load-balancing");
/*  125 */         if ((ulb != null) && (ulb.length() > 0))
/*      */         {
/*  127 */           if (ulb.equalsIgnoreCase("false")) {
/*  128 */             clusterSettings.setURLLoadBalancing(false);
/*  129 */           } else if (!ulb.equalsIgnoreCase("true"))
/*      */           {
/*  131 */             ConfigurationException e = new ConfigurationException();
/*  132 */             e.setMessage(10216, new Object[] { clusterName, ulb });
/*  133 */             throw e;
/*      */           }
/*      */         }
/*  136 */         ((MessagingConfiguration)this.config).addClusterSettings(clusterSettings);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void securitySection(Node root)
/*      */   {
/*  145 */     Node security = selectSingleNode(root, "security");
/*      */ 
/*  147 */     if (security != null)
/*      */     {
/*  150 */       allowedChildElements(security, SECURITY_CHILDREN);
/*      */ 
/*  153 */       NodeList list = selectNodeList(security, "security-constraint");
/*  154 */       for (int i = 0; i < list.getLength(); i++)
/*      */       {
/*  156 */         Node constraint = list.item(i);
/*  157 */         securityConstraint(constraint, false);
/*      */       }
/*      */ 
/*  161 */       list = selectNodeList(security, "login-command");
/*  162 */       for (int i = 0; i < list.getLength(); i++)
/*      */       {
/*  164 */         Node login = list.item(i);
/*  165 */         LoginCommandSettings loginCommandSettings = new LoginCommandSettings();
/*  166 */         requiredAttributesOrElements(login, LOGIN_COMMAND_REQ_CHILDREN);
/*  167 */         allowedAttributesOrElements(login, LOGIN_COMMAND_CHILDREN);
/*      */ 
/*  169 */         String server = getAttributeOrChildElement(login, "server");
/*  170 */         if (server.length() == 0)
/*      */         {
/*  173 */           ConfigurationException e = new ConfigurationException();
/*  174 */           e.setMessage(10105, new Object[] { "server", "login-command" });
/*  175 */           throw e;
/*      */         }
/*  177 */         loginCommandSettings.setServer(server);
/*      */ 
/*  179 */         String loginClass = getAttributeOrChildElement(login, "class");
/*  180 */         if (loginClass.length() == 0)
/*      */         {
/*  183 */           ConfigurationException e = new ConfigurationException();
/*  184 */           e.setMessage(10105, new Object[] { "class", "login-command" });
/*  185 */           throw e;
/*      */         }
/*  187 */         loginCommandSettings.setClassName(loginClass);
/*      */ 
/*  189 */         boolean isPerClientAuth = Boolean.valueOf(getAttributeOrChildElement(login, "per-client-authentication")).booleanValue();
/*  190 */         loginCommandSettings.setPerClientAuthentication(isPerClientAuth);
/*      */ 
/*  192 */         ((MessagingConfiguration)this.config).getSecuritySettings().addLoginCommandSettings(loginCommandSettings);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private SecurityConstraint securityConstraint(Node constraint, boolean inline)
/*      */   {
/*  202 */     allowedAttributesOrElements(constraint, SECURITY_CONSTRAINT_DEFINITION_CHILDREN);
/*      */ 
/*  205 */     String ref = getAttributeOrChildElement(constraint, "ref");
/*      */     SecurityConstraint sc;
/*  206 */     if (ref.length() > 0)
/*      */     {
/*  208 */       allowedAttributesOrElements(constraint, new String[] { "ref" });
/*      */ 
/*  210 */       SecurityConstraint sc = ((MessagingConfiguration)this.config).getSecuritySettings().getConstraint(ref);
/*  211 */       if (sc == null)
/*      */       {
/*  214 */         ConfigurationException e = new ConfigurationException();
/*  215 */         e.setMessage(10109, new Object[] { "security-constraint", ref });
/*  216 */         throw e;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  222 */       String id = getAttributeOrChildElement(constraint, "id");
/*      */       SecurityConstraint sc;
/*  225 */       if (inline)
/*      */       {
/*  227 */         sc = new SecurityConstraint("");
/*      */       }
/*  229 */       else if (isValidID(id))
/*      */       {
/*  231 */         SecurityConstraint sc = new SecurityConstraint(id);
/*  232 */         ((MessagingConfiguration)this.config).getSecuritySettings().addConstraint(sc);
/*      */       }
/*      */       else
/*      */       {
/*  237 */         ConfigurationException ex = new ConfigurationException();
/*  238 */         ex.setMessage(10110, new Object[] { "security-constraint", id });
/*  239 */         ex.setDetails(10110);
/*  240 */         throw ex;
/*      */       }
/*      */ 
/*  244 */       String method = getAttributeOrChildElement(constraint, "auth-method");
/*  245 */       sc.setMethod(method);
/*      */ 
/*  248 */       Node rolesNode = selectSingleNode(constraint, "roles");
/*  249 */       if (rolesNode != null)
/*      */       {
/*  251 */         allowedChildElements(rolesNode, ROLES_CHILDREN);
/*  252 */         NodeList roles = selectNodeList(rolesNode, "role");
/*  253 */         for (int r = 0; r < roles.getLength(); r++)
/*      */         {
/*  255 */           Node roleNode = roles.item(r);
/*  256 */           String role = evaluateExpression(roleNode, ".").toString().trim();
/*  257 */           if (role.length() <= 0)
/*      */             continue;
/*  259 */           sc.addRole(role);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  265 */     return sc;
/*      */   }
/*      */ 
/*      */   private void serversSection(Node root)
/*      */   {
/*  272 */     if (!(this.config instanceof MessagingConfiguration)) {
/*  273 */       return;
/*      */     }
/*  275 */     Node serversNode = selectSingleNode(root, "servers");
/*  276 */     if (serversNode != null)
/*      */     {
/*  279 */       allowedAttributesOrElements(serversNode, SERVERS_CHILDREN);
/*      */ 
/*  281 */       NodeList servers = selectNodeList(serversNode, "server");
/*  282 */       for (int i = 0; i < servers.getLength(); i++)
/*      */       {
/*  284 */         Node server = servers.item(i);
/*  285 */         serverDefinition(server);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverDefinition(Node server)
/*      */   {
/*  293 */     requiredAttributesOrElements(server, SERVER_REQ_CHILDREN);
/*      */ 
/*  295 */     String id = getAttributeOrChildElement(server, "id");
/*  296 */     if (isValidID(id))
/*      */     {
/*  298 */       SharedServerSettings settings = new SharedServerSettings();
/*  299 */       settings.setId(id);
/*  300 */       settings.setClassName(getAttributeOrChildElement(server, "class"));
/*      */ 
/*  302 */       NodeList properties = selectNodeList(server, "properties/*");
/*  303 */       if (properties.getLength() > 0)
/*      */       {
/*  305 */         ConfigMap map = properties(properties, getSourceFileOf(server));
/*  306 */         settings.setProperties(map);
/*      */       }
/*  308 */       ((MessagingConfiguration)this.config).addSharedServerSettings(settings);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void channelsSection(Node root)
/*      */   {
/*  314 */     Node channelsNode = selectSingleNode(root, "channels");
/*  315 */     if (channelsNode != null)
/*      */     {
/*  318 */       allowedAttributesOrElements(channelsNode, CHANNELS_CHILDREN);
/*      */ 
/*  320 */       NodeList channels = selectNodeList(channelsNode, "channel-definition");
/*  321 */       for (int i = 0; i < channels.getLength(); i++)
/*      */       {
/*  323 */         Node channel = channels.item(i);
/*  324 */         channelDefinition(channel);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void channelDefinition(Node channel)
/*      */   {
/*  332 */     requiredAttributesOrElements(channel, CHANNEL_DEFINITION_REQ_CHILDREN);
/*  333 */     allowedAttributesOrElements(channel, CHANNEL_DEFINITION_CHILDREN);
/*      */ 
/*  335 */     String id = getAttributeOrChildElement(channel, "id");
/*  336 */     if (isValidID(id))
/*      */     {
/*  339 */       if (this.config.getChannelSettings(id) != null)
/*      */       {
/*  342 */         ConfigurationException e = new ConfigurationException();
/*  343 */         e.setMessage(11127, new Object[] { id });
/*  344 */         throw e;
/*      */       }
/*      */ 
/*  347 */       ChannelSettings channelSettings = new ChannelSettings(id);
/*  348 */       channelSettings.setSourceFile(getSourceFileOf(channel));
/*      */ 
/*  350 */       String clientType = getAttributeOrChildElement(channel, "class");
/*  351 */       channelSettings.setClientType(clientType);
/*      */ 
/*  354 */       String remote = getAttributeOrChildElement(channel, "remote");
/*  355 */       channelSettings.setRemote(Boolean.valueOf(remote).booleanValue());
/*      */ 
/*  358 */       Node endpoint = selectSingleNode(channel, "endpoint");
/*  359 */       if (endpoint != null)
/*      */       {
/*  362 */         allowedAttributesOrElements(endpoint, ENDPOINT_CHILDREN);
/*      */ 
/*  364 */         String type = getAttributeOrChildElement(endpoint, "class");
/*  365 */         channelSettings.setEndpointType(type);
/*      */ 
/*  368 */         String uri = getAttributeOrChildElement(endpoint, "url");
/*  369 */         if ((uri == null) || ("".equals(uri)))
/*  370 */           uri = getAttributeOrChildElement(endpoint, "uri");
/*  371 */         channelSettings.setUri(uri);
/*      */ 
/*  373 */         this.config.addChannelSettings(id, channelSettings);
/*      */       }
/*      */ 
/*  377 */       Node server = selectSingleNode(channel, "server");
/*  378 */       if (server != null)
/*      */       {
/*  380 */         requiredAttributesOrElements(server, CHANNEL_DEFINITION_SERVER_REQ_CHILDREN);
/*      */ 
/*  382 */         String serverId = getAttributeOrChildElement(server, "ref");
/*  383 */         channelSettings.setServerId(serverId);
/*      */       }
/*      */ 
/*  387 */       NodeList properties = selectNodeList(channel, "properties/*");
/*  388 */       if (properties.getLength() > 0)
/*      */       {
/*  390 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/*  391 */         channelSettings.addProperties(map);
/*      */       }
/*      */ 
/*  397 */       String ref = evaluateExpression(channel, "@security-constraint").toString().trim();
/*  398 */       if (ref.length() > 0)
/*      */       {
/*  400 */         SecurityConstraint sc = ((MessagingConfiguration)this.config).getSecuritySettings().getConstraint(ref);
/*  401 */         if (sc != null)
/*      */         {
/*  403 */           channelSettings.setConstraint(sc);
/*      */         }
/*      */         else
/*      */         {
/*  408 */           ConfigurationException ex = new ConfigurationException();
/*  409 */           ex.setMessage(10132, new Object[] { "security-constraint", ref, id });
/*  410 */           throw ex;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  416 */         Node security = selectSingleNode(channel, "security");
/*  417 */         if (security != null)
/*      */         {
/*  419 */           allowedChildElements(security, EMBEDDED_SECURITY_CHILDREN);
/*  420 */           Node constraint = selectSingleNode(security, "security-constraint");
/*  421 */           if (constraint != null)
/*      */           {
/*  423 */             SecurityConstraint sc = securityConstraint(constraint, true);
/*  424 */             channelSettings.setConstraint(sc);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  432 */       ConfigurationException ex = new ConfigurationException();
/*  433 */       ex.setMessage(10110, new Object[] { "channel-definition", id });
/*  434 */       ex.setDetails(10110);
/*  435 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void services(Node root)
/*      */   {
/*  441 */     Node servicesNode = selectSingleNode(root, "services");
/*  442 */     if (servicesNode != null)
/*      */     {
/*  445 */       allowedChildElements(servicesNode, SERVICES_CHILDREN);
/*      */ 
/*  448 */       Node defaultChannels = selectSingleNode(servicesNode, "default-channels");
/*  449 */       if (defaultChannels != null)
/*      */       {
/*  451 */         allowedChildElements(defaultChannels, DEFAULT_CHANNELS_CHILDREN);
/*  452 */         NodeList channels = selectNodeList(defaultChannels, "channel");
/*  453 */         for (int c = 0; c < channels.getLength(); c++)
/*      */         {
/*  455 */           Node chan = channels.item(c);
/*  456 */           allowedAttributes(chan, new String[] { "ref" });
/*  457 */           defaultChannel(chan);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  462 */       NodeList services = selectNodeList(servicesNode, "service-include");
/*  463 */       for (int i = 0; i < services.getLength(); i++)
/*      */       {
/*  465 */         Node service = services.item(i);
/*  466 */         serviceInclude(service);
/*      */       }
/*      */ 
/*  470 */       services = selectNodeList(servicesNode, "service");
/*  471 */       for (int i = 0; i < services.getLength(); i++)
/*      */       {
/*  473 */         Node service = services.item(i);
/*  474 */         service(service);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serviceInclude(Node serviceInclude)
/*      */   {
/*  482 */     requiredAttributesOrElements(serviceInclude, SERVICE_INCLUDE_CHILDREN);
/*      */ 
/*  484 */     String src = getAttributeOrChildElement(serviceInclude, "file-path");
/*  485 */     if (src.length() > 0)
/*      */     {
/*  487 */       Document doc = loadDocument(src, this.fileResolver.getIncludedFile(src));
/*  488 */       doc.getDocumentElement().normalize();
/*      */ 
/*  490 */       Node service = selectSingleNode(doc, "/service");
/*  491 */       if (service != null)
/*      */       {
/*  493 */         service(service);
/*  494 */         this.fileResolver.popIncludedFile();
/*      */       }
/*      */       else
/*      */       {
/*  499 */         ConfigurationException ex = new ConfigurationException();
/*  500 */         ex.setMessage(10112, new Object[] { "service" });
/*  501 */         throw ex;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void service(Node service)
/*      */   {
/*  509 */     requiredAttributesOrElements(service, SERVICE_REQ_CHILDREN);
/*  510 */     allowedAttributesOrElements(service, SERVICE_CHILDREN);
/*      */ 
/*  512 */     String id = getAttributeOrChildElement(service, "id");
/*  513 */     if (isValidID(id))
/*      */     {
/*  515 */       ServiceSettings serviceSettings = this.config.getServiceSettings(id);
/*  516 */       if (serviceSettings == null)
/*      */       {
/*  518 */         serviceSettings = new ServiceSettings(id);
/*  519 */         serviceSettings.setSourceFile(getSourceFileOf(service));
/*  520 */         this.config.addServiceSettings(serviceSettings);
/*      */       }
/*      */       else
/*      */       {
/*  525 */         ConfigurationException e = new ConfigurationException();
/*  526 */         e.setMessage(10113, new Object[] { id });
/*  527 */         throw e;
/*      */       }
/*      */ 
/*  531 */       String className = getAttributeOrChildElement(service, "class");
/*  532 */       if (className.length() > 0)
/*      */       {
/*  534 */         serviceSettings.setClassName(className);
/*      */       }
/*      */       else
/*      */       {
/*  539 */         ConfigurationException ex = new ConfigurationException();
/*  540 */         ex.setMessage(10114, new Object[] { "service", id });
/*  541 */         throw ex;
/*      */       }
/*      */ 
/*  547 */       NodeList properties = selectNodeList(service, "properties/*");
/*  548 */       if (properties.getLength() > 0)
/*      */       {
/*  550 */         ConfigMap map = properties(properties, getSourceFileOf(service));
/*  551 */         serviceSettings.addProperties(map);
/*      */       }
/*      */ 
/*  555 */       Node defaultChannels = selectSingleNode(service, "default-channels");
/*      */       Iterator iter;
/*  556 */       if (defaultChannels != null)
/*      */       {
/*  558 */         allowedChildElements(defaultChannels, DEFAULT_CHANNELS_CHILDREN);
/*  559 */         NodeList channels = selectNodeList(defaultChannels, "channel");
/*  560 */         for (int c = 0; c < channels.getLength(); c++)
/*      */         {
/*  562 */           Node chan = channels.item(c);
/*  563 */           allowedAttributes(chan, new String[] { "ref" });
/*  564 */           defaultChannel(chan, serviceSettings);
/*      */         }
/*      */ 
/*      */       }
/*  568 */       else if (this.config.getDefaultChannels().size() > 0)
/*      */       {
/*  570 */         for (iter = this.config.getDefaultChannels().iterator(); iter.hasNext(); )
/*      */         {
/*  572 */           String channelId = (String)iter.next();
/*  573 */           ChannelSettings channel = this.config.getChannelSettings(channelId);
/*  574 */           serviceSettings.addDefaultChannel(channel);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  579 */       Node defaultSecurityConstraint = selectSingleNode(service, "default-security-constraint");
/*  580 */       if (defaultSecurityConstraint != null)
/*      */       {
/*  583 */         requiredAttributesOrElements(defaultSecurityConstraint, new String[] { "ref" });
/*  584 */         allowedAttributesOrElements(defaultSecurityConstraint, new String[] { "ref" });
/*      */ 
/*  586 */         String ref = getAttributeOrChildElement(defaultSecurityConstraint, "ref");
/*  587 */         if (ref.length() > 0)
/*      */         {
/*  589 */           SecurityConstraint sc = ((MessagingConfiguration)this.config).getSecuritySettings().getConstraint(ref);
/*  590 */           if (sc == null)
/*      */           {
/*  593 */             ConfigurationException e = new ConfigurationException();
/*  594 */             e.setMessage(10109, new Object[] { "security-constraint", ref });
/*  595 */             throw e;
/*      */           }
/*  597 */           serviceSettings.setConstraint(sc);
/*      */         }
/*      */         else
/*      */         {
/*  602 */           ConfigurationException ex = new ConfigurationException();
/*  603 */           ex.setMessage(11124, new Object[] { ref, id });
/*  604 */           throw ex;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  609 */       Node adapters = selectSingleNode(service, "adapters");
/*  610 */       if (adapters != null)
/*      */       {
/*  612 */         allowedChildElements(adapters, ADAPTERS_CHILDREN);
/*  613 */         NodeList serverAdapters = selectNodeList(adapters, "adapter-definition");
/*  614 */         for (int a = 0; a < serverAdapters.getLength(); a++)
/*      */         {
/*  616 */           Node adapter = serverAdapters.item(a);
/*  617 */           adapterDefinition(adapter, serviceSettings);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  622 */       NodeList list = selectNodeList(service, "destination");
/*  623 */       for (int i = 0; i < list.getLength(); i++)
/*      */       {
/*  625 */         Node dest = list.item(i);
/*  626 */         destination(dest, serviceSettings);
/*      */       }
/*      */ 
/*  630 */       list = selectNodeList(service, "destination-include");
/*  631 */       for (int i = 0; i < list.getLength(); i++)
/*      */       {
/*  633 */         Node dest = list.item(i);
/*  634 */         destinationInclude(dest, serviceSettings);
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  640 */       ConfigurationException ex = new ConfigurationException();
/*  641 */       ex.setMessage(10110, new Object[] { "service", id });
/*  642 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void defaultChannel(Node chan)
/*      */   {
/*  657 */     String ref = getAttributeOrChildElement(chan, "ref");
/*      */ 
/*  659 */     if (ref.length() > 0)
/*      */     {
/*  661 */       ChannelSettings channel = this.config.getChannelSettings(ref);
/*  662 */       if (channel != null)
/*      */       {
/*  664 */         this.config.addDefaultChannel(channel.getId());
/*      */       }
/*      */       else
/*      */       {
/*  669 */         ConfigurationException e = new ConfigurationException();
/*  670 */         e.setMessage(10109, new Object[] { "channel", ref });
/*  671 */         throw e;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  677 */       ConfigurationException ex = new ConfigurationException();
/*  678 */       ex.setMessage(10116, new Object[] { "MessageBroker" });
/*  679 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void defaultChannel(Node chan, ServiceSettings serviceSettings)
/*      */   {
/*  694 */     String ref = getAttributeOrChildElement(chan, "ref");
/*      */ 
/*  696 */     if (ref.length() > 0)
/*      */     {
/*  698 */       ChannelSettings channel = this.config.getChannelSettings(ref);
/*  699 */       if (channel != null)
/*      */       {
/*  701 */         serviceSettings.addDefaultChannel(channel);
/*      */       }
/*      */       else
/*      */       {
/*  706 */         ConfigurationException e = new ConfigurationException();
/*  707 */         e.setMessage(10109, new Object[] { "channel", ref });
/*  708 */         throw e;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  714 */       ConfigurationException ex = new ConfigurationException();
/*  715 */       ex.setMessage(10116, new Object[] { serviceSettings.getId() });
/*  716 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void adapterDefinition(Node adapter, ServiceSettings serviceSettings)
/*      */   {
/*  723 */     requiredAttributesOrElements(adapter, ADAPTER_DEFINITION_REQ_CHILDREN);
/*  724 */     allowedChildElements(adapter, ADAPTER_DEFINITION_CHILDREN);
/*      */ 
/*  726 */     String serviceId = serviceSettings.getId();
/*      */ 
/*  728 */     String id = getAttributeOrChildElement(adapter, "id");
/*  729 */     if (isValidID(id))
/*      */     {
/*  731 */       AdapterSettings adapterSettings = new AdapterSettings(id);
/*  732 */       adapterSettings.setSourceFile(getSourceFileOf(adapter));
/*  733 */       String className = getAttributeOrChildElement(adapter, "class");
/*      */ 
/*  735 */       if (className.length() > 0)
/*      */       {
/*  737 */         adapterSettings.setClassName(className);
/*      */ 
/*  740 */         boolean isDefault = Boolean.valueOf(getAttributeOrChildElement(adapter, "default")).booleanValue();
/*  741 */         if (isDefault)
/*      */         {
/*  743 */           adapterSettings.setDefault(isDefault);
/*      */ 
/*  746 */           AdapterSettings defaultAdapter = serviceSettings.getDefaultAdapter();
/*      */ 
/*  748 */           if (defaultAdapter != null)
/*      */           {
/*  751 */             ConfigurationException ex = new ConfigurationException();
/*  752 */             ex.setMessage(10117, new Object[] { id, serviceId, defaultAdapter.getId() });
/*  753 */             throw ex;
/*      */           }
/*      */         }
/*      */ 
/*  757 */         serviceSettings.addAdapterSettings(adapterSettings);
/*      */ 
/*  760 */         NodeList properties = selectNodeList(adapter, "properties/*");
/*  761 */         if (properties.getLength() > 0)
/*      */         {
/*  763 */           ConfigMap map = properties(properties, getSourceFileOf(adapter));
/*  764 */           adapterSettings.addProperties(map);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  770 */         ConfigurationException ex = new ConfigurationException();
/*  771 */         ex.setMessage(10114, new Object[] { "adapter-definition", id });
/*  772 */         throw ex;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  778 */       ConfigurationException ex = new ConfigurationException();
/*  779 */       ex.setMessage(10119, new Object[] { "adapter-definition", id, serviceId });
/*  780 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void destinationInclude(Node destInclude, ServiceSettings serviceSettings)
/*      */   {
/*  787 */     requiredAttributesOrElements(destInclude, DESTINATION_INCLUDE_CHILDREN);
/*      */ 
/*  789 */     String src = getAttributeOrChildElement(destInclude, "file-path");
/*  790 */     if (src.length() > 0)
/*      */     {
/*  792 */       Document doc = loadDocument(src, this.fileResolver.getIncludedFile(src));
/*  793 */       doc.getDocumentElement().normalize();
/*      */ 
/*  795 */       Node dest = selectSingleNode(doc, "/destination");
/*  796 */       if (dest != null)
/*      */       {
/*  798 */         destination(dest, serviceSettings);
/*  799 */         this.fileResolver.popIncludedFile();
/*      */       }
/*      */       else
/*      */       {
/*  804 */         ConfigurationException ex = new ConfigurationException();
/*  805 */         ex.setMessage(10118, new Object[] { "destination" });
/*  806 */         throw ex;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void destination(Node dest, ServiceSettings serviceSettings)
/*      */   {
/*  814 */     requiredAttributesOrElements(dest, DESTINATION_REQ_CHILDREN);
/*  815 */     allowedAttributes(dest, DESTINATION_ATTR);
/*  816 */     allowedChildElements(dest, DESTINATION_CHILDREN);
/*      */ 
/*  818 */     String serviceId = serviceSettings.getId();
/*      */ 
/*  821 */     String id = getAttributeOrChildElement(dest, "id");
/*  822 */     if (isValidID(id))
/*      */     {
/*  824 */       DestinationSettings destinationSettings = (DestinationSettings)serviceSettings.getDestinationSettings().get(id);
/*  825 */       if (destinationSettings != null)
/*      */       {
/*  828 */         ConfigurationException e = new ConfigurationException();
/*  829 */         e.setMessage(10122, new Object[] { id, serviceId });
/*  830 */         throw e;
/*      */       }
/*      */ 
/*  833 */       destinationSettings = new DestinationSettings(id);
/*  834 */       destinationSettings.setSourceFile(getSourceFileOf(dest));
/*  835 */       serviceSettings.addDestinationSettings(destinationSettings);
/*      */     }
/*      */     else
/*      */     {
/*  840 */       ConfigurationException ex = new ConfigurationException();
/*  841 */       ex.setMessage(10119, new Object[] { "destination", id, serviceId });
/*  842 */       throw ex;
/*      */     }
/*      */     DestinationSettings destinationSettings;
/*  846 */     NodeList properties = selectNodeList(dest, "properties/*");
/*  847 */     if (properties.getLength() > 0)
/*      */     {
/*  849 */       ConfigMap map = properties(properties, getSourceFileOf(dest));
/*  850 */       destinationSettings.addProperties(map);
/*      */     }
/*      */ 
/*  854 */     destinationChannels(dest, destinationSettings, serviceSettings);
/*      */ 
/*  857 */     destinationSecurity(dest, destinationSettings, serviceSettings);
/*      */ 
/*  860 */     destinationAdapter(dest, destinationSettings, serviceSettings);
/*      */   }
/*      */ 
/*      */   private void destinationChannels(Node dest, DestinationSettings destinationSettings, ServiceSettings serviceSettings)
/*      */   {
/*  865 */     String destId = destinationSettings.getId();
/*      */ 
/*  868 */     String channelsList = evaluateExpression(dest, "@channels").toString().trim();
/*  869 */     if (channelsList.length() > 0)
/*      */     {
/*  871 */       StringTokenizer st = new StringTokenizer(channelsList, ",;:");
/*  872 */       while (st.hasMoreTokens())
/*      */       {
/*  874 */         String ref = st.nextToken().trim();
/*  875 */         ChannelSettings channel = this.config.getChannelSettings(ref);
/*  876 */         if (channel != null)
/*      */         {
/*  878 */           destinationSettings.addChannelSettings(channel);
/*      */         }
/*      */         else
/*      */         {
/*  883 */           ConfigurationException ex = new ConfigurationException();
/*  884 */           ex.setMessage(10120, new Object[] { "channel", ref, destId });
/*  885 */           throw ex;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  892 */       Node channelsNode = selectSingleNode(dest, "channels");
/*  893 */       if (channelsNode != null)
/*      */       {
/*  895 */         allowedChildElements(channelsNode, DESTINATION_CHANNELS_CHILDREN);
/*  896 */         NodeList channels = selectNodeList(channelsNode, "channel");
/*  897 */         for (int c = 0; c < channels.getLength(); c++)
/*      */         {
/*  899 */           Node chan = channels.item(c);
/*      */ 
/*  902 */           requiredAttributesOrElements(chan, DESTINATION_CHANNEL_REQ_CHILDREN);
/*      */ 
/*  904 */           String ref = getAttributeOrChildElement(chan, "ref");
/*  905 */           if (ref.length() > 0)
/*      */           {
/*  907 */             ChannelSettings channel = this.config.getChannelSettings(ref);
/*  908 */             if (channel != null)
/*      */             {
/*  910 */               destinationSettings.addChannelSettings(channel);
/*      */             }
/*      */             else
/*      */             {
/*  915 */               ConfigurationException ex = new ConfigurationException();
/*  916 */               ex.setMessage(10120, new Object[] { "channel", ref, destId });
/*  917 */               throw ex;
/*      */             }
/*      */ 
/*      */           }
/*      */           else
/*      */           {
/*  923 */             ConfigurationException ex = new ConfigurationException();
/*  924 */             ex.setMessage(10121, new Object[] { "channel", ref, destId });
/*  925 */             throw ex;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  932 */         List defaultChannels = serviceSettings.getDefaultChannels();
/*  933 */         Iterator it = defaultChannels.iterator();
/*  934 */         while (it.hasNext())
/*      */         {
/*  936 */           ChannelSettings channel = (ChannelSettings)it.next();
/*  937 */           destinationSettings.addChannelSettings(channel);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  942 */     if (destinationSettings.getChannelSettings().size() <= 0)
/*      */     {
/*  945 */       ConfigurationException ex = new ConfigurationException();
/*  946 */       ex.setMessage(10123, new Object[] { destId });
/*  947 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void destinationSecurity(Node dest, DestinationSettings destinationSettings, ServiceSettings serviceSettings)
/*      */   {
/*  953 */     String destId = destinationSettings.getId();
/*      */ 
/*  956 */     String ref = evaluateExpression(dest, "@security-constraint").toString().trim();
/*  957 */     if (ref.length() > 0)
/*      */     {
/*  959 */       SecurityConstraint sc = ((MessagingConfiguration)this.config).getSecuritySettings().getConstraint(ref);
/*  960 */       if (sc != null)
/*      */       {
/*  962 */         destinationSettings.setConstraint(sc);
/*      */       }
/*      */       else
/*      */       {
/*  967 */         ConfigurationException ex = new ConfigurationException();
/*  968 */         ex.setMessage(10120, new Object[] { "security-constraint", ref, destId });
/*  969 */         throw ex;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  975 */       Node security = selectSingleNode(dest, "security");
/*  976 */       if (security != null)
/*      */       {
/*  978 */         allowedChildElements(security, EMBEDDED_SECURITY_CHILDREN);
/*  979 */         Node constraint = selectSingleNode(security, "security-constraint");
/*  980 */         if (constraint != null)
/*      */         {
/*  982 */           SecurityConstraint sc = securityConstraint(constraint, true);
/*  983 */           destinationSettings.setConstraint(sc);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  989 */         SecurityConstraint sc = serviceSettings.getConstraint();
/*  990 */         if (sc != null)
/*      */         {
/*  992 */           destinationSettings.setConstraint(sc);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void destinationAdapter(Node dest, DestinationSettings destinationSettings, ServiceSettings serviceSettings)
/*      */   {
/* 1000 */     String destId = destinationSettings.getId();
/*      */ 
/* 1003 */     String ref = evaluateExpression(dest, "@adapter").toString().trim();
/* 1004 */     if (ref.length() > 0)
/*      */     {
/* 1006 */       adapterReference(ref, destinationSettings, serviceSettings);
/*      */     }
/*      */     else
/*      */     {
/* 1010 */       Node adapter = selectSingleNode(dest, "adapter");
/*      */ 
/* 1013 */       if (adapter != null)
/*      */       {
/* 1015 */         allowedAttributesOrElements(adapter, DESTINATION_ADAPTER_CHILDREN);
/* 1016 */         ref = getAttributeOrChildElement(adapter, "ref");
/* 1017 */         adapterReference(ref, destinationSettings, serviceSettings);
/*      */       }
/*      */       else
/*      */       {
/* 1022 */         AdapterSettings adapterSettings = serviceSettings.getDefaultAdapter();
/* 1023 */         if (adapterSettings != null)
/*      */         {
/* 1025 */           destinationSettings.setAdapterSettings(adapterSettings);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1030 */     if (destinationSettings.getAdapterSettings() == null)
/*      */     {
/* 1033 */       ConfigurationException ex = new ConfigurationException();
/* 1034 */       ex.setMessage(10127, new Object[] { destId });
/* 1035 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void adapterReference(String ref, DestinationSettings destinationSettings, ServiceSettings serviceSettings)
/*      */   {
/* 1041 */     String destId = destinationSettings.getId();
/* 1042 */     if (ref.length() > 0)
/*      */     {
/* 1044 */       AdapterSettings adapterSettings = serviceSettings.getAdapterSettings(ref);
/* 1045 */       if (adapterSettings != null)
/*      */       {
/* 1047 */         destinationSettings.setAdapterSettings(adapterSettings);
/*      */       }
/*      */       else
/*      */       {
/* 1052 */         ConfigurationException ex = new ConfigurationException();
/* 1053 */         ex.setMessage(10120, new Object[] { "adapter", ref, destId });
/* 1054 */         throw ex;
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1060 */       ConfigurationException ex = new ConfigurationException();
/* 1061 */       ex.setMessage(10121, new Object[] { "adapter", ref, destId });
/* 1062 */       throw ex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void logging(Node root)
/*      */   {
/* 1068 */     Node logging = selectSingleNode(root, "logging");
/* 1069 */     if (logging != null)
/*      */     {
/* 1072 */       allowedAttributesOrElements(logging, LOGGING_CHILDREN);
/*      */ 
/* 1074 */       LoggingSettings settings = new LoggingSettings();
/*      */ 
/* 1077 */       NodeList properties = selectNodeList(logging, "properties/*");
/* 1078 */       if (properties.getLength() > 0)
/*      */       {
/* 1080 */         ConfigMap map = properties(properties, getSourceFileOf(logging));
/* 1081 */         settings.addProperties(map);
/*      */       }
/*      */ 
/* 1084 */       NodeList targets = selectNodeList(logging, "target");
/* 1085 */       for (int i = 0; i < targets.getLength(); i++)
/*      */       {
/* 1087 */         Node targetNode = targets.item(i);
/*      */ 
/* 1090 */         requiredAttributesOrElements(targetNode, TARGET_REQ_CHILDREN);
/* 1091 */         allowedAttributesOrElements(targetNode, TARGET_CHILDREN);
/*      */ 
/* 1093 */         String className = getAttributeOrChildElement(targetNode, "class");
/*      */ 
/* 1095 */         if (className.length() <= 0)
/*      */           continue;
/* 1097 */         TargetSettings targetSettings = new TargetSettings(className);
/* 1098 */         String targetLevel = getAttributeOrChildElement(targetNode, "level");
/*      */ 
/* 1100 */         if (targetLevel.length() > 0) {
/* 1101 */           targetSettings.setLevel(targetLevel);
/*      */         }
/*      */ 
/* 1104 */         Node filtersNode = selectSingleNode(targetNode, "filters");
/* 1105 */         if (filtersNode != null)
/*      */         {
/* 1107 */           allowedChildElements(filtersNode, FILTERS_CHILDREN);
/* 1108 */           NodeList filters = selectNodeList(filtersNode, "pattern");
/* 1109 */           for (int f = 0; f < filters.getLength(); f++)
/*      */           {
/* 1111 */             Node pattern = filters.item(f);
/* 1112 */             String filter = evaluateExpression(pattern, ".").toString().trim();
/* 1113 */             targetSettings.addFilter(filter);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1118 */         properties = selectNodeList(targetNode, "properties/*");
/* 1119 */         if (properties.getLength() > 0)
/*      */         {
/* 1121 */           ConfigMap map = properties(properties, getSourceFileOf(targetNode));
/* 1122 */           targetSettings.addProperties(map);
/*      */         }
/*      */ 
/* 1125 */         settings.addTarget(targetSettings);
/*      */       }
/*      */ 
/* 1129 */       this.config.setLoggingSettings(settings);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void system(Node root)
/*      */   {
/* 1135 */     Node system = selectSingleNode(root, "system");
/* 1136 */     if (system != null)
/*      */     {
/* 1139 */       allowedAttributesOrElements(system, SYSTEM_CHILDREN);
/*      */ 
/* 1141 */       SystemSettings settings = new SystemSettings();
/*      */ 
/* 1144 */       Node localeNode = selectSingleNode(system, "locale");
/*      */ 
/* 1146 */       if (localeNode != null)
/*      */       {
/* 1148 */         allowedAttributesOrElements(localeNode, LOCALE_CHILDREN);
/* 1149 */         String defaultLocaleString = getAttributeOrChildElement(localeNode, "default-locale");
/*      */         Locale defaultLocale;
/*      */         Locale defaultLocale;
/* 1151 */         if (defaultLocaleString.length() > 0)
/* 1152 */           defaultLocale = LocaleUtils.buildLocale(defaultLocaleString);
/*      */         else {
/* 1154 */           defaultLocale = LocaleUtils.buildLocale(null);
/*      */         }
/* 1156 */         settings.setDefaultLocale(defaultLocale);
/*      */       }
/*      */ 
/* 1160 */       String manageable = getAttributeOrChildElement(system, "manageable");
/* 1161 */       settings.setManageable(manageable);
/*      */ 
/* 1164 */       Node redeployNode = selectSingleNode(system, "redeploy");
/*      */ 
/* 1166 */       if (redeployNode != null)
/*      */       {
/* 1168 */         allowedAttributesOrElements(redeployNode, REDEPLOY_CHILDREN);
/*      */ 
/* 1170 */         String enabled = getAttributeOrChildElement(redeployNode, "enabled");
/* 1171 */         settings.setRedeployEnabled(enabled);
/*      */ 
/* 1173 */         String interval = getAttributeOrChildElement(redeployNode, "watch-interval");
/* 1174 */         if (interval.length() > 0)
/*      */         {
/* 1176 */           settings.setWatchInterval(interval);
/*      */         }
/*      */ 
/* 1179 */         NodeList watches = selectNodeList(redeployNode, "watch-file");
/* 1180 */         for (int i = 0; i < watches.getLength(); i++)
/*      */         {
/* 1182 */           Node watchNode = watches.item(i);
/* 1183 */           String watch = evaluateExpression(watchNode, ".").toString().trim();
/* 1184 */           if (watch.length() <= 0)
/*      */             continue;
/* 1186 */           settings.addWatchFile(watch);
/*      */         }
/*      */ 
/* 1190 */         NodeList touches = selectNodeList(redeployNode, "touch-file");
/* 1191 */         for (int i = 0; i < touches.getLength(); i++)
/*      */         {
/* 1193 */           Node touchNode = touches.item(i);
/* 1194 */           String touch = evaluateExpression(touchNode, ".").toString().trim();
/* 1195 */           if (touch.length() <= 0)
/*      */             continue;
/* 1197 */           settings.addTouchFile(touch);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1202 */       ((MessagingConfiguration)this.config).setSystemSettings(settings);
/*      */     }
/*      */     else
/*      */     {
/* 1208 */       ((MessagingConfiguration)this.config).setSystemSettings(new SystemSettings());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void flexClient(Node root)
/*      */   {
/* 1214 */     Node flexClient = selectSingleNode(root, "flex-client");
/* 1215 */     if (flexClient != null)
/*      */     {
/* 1217 */       FlexClientSettings flexClientSettings = new FlexClientSettings();
/*      */ 
/* 1219 */       String timeout = getAttributeOrChildElement(flexClient, "timeout-minutes");
/* 1220 */       if (timeout.length() > 0)
/*      */       {
/*      */         try
/*      */         {
/* 1224 */           long timeoutMinutes = Long.parseLong(timeout);
/* 1225 */           if (timeoutMinutes < 0L)
/*      */           {
/* 1228 */             ConfigurationException e = new ConfigurationException();
/* 1229 */             e.setMessage(11123, new Object[] { timeout });
/* 1230 */             throw e;
/*      */           }
/* 1232 */           flexClientSettings.setTimeoutMinutes(timeoutMinutes);
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/* 1237 */           ConfigurationException e = new ConfigurationException();
/* 1238 */           e.setMessage(11123, new Object[] { timeout });
/* 1239 */           throw e;
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1244 */         flexClientSettings.setTimeoutMinutes(0L);
/*      */       }
/*      */ 
/* 1247 */       ((MessagingConfiguration)this.config).setFlexClientSettings(flexClientSettings);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void factories(Node root)
/*      */   {
/* 1253 */     Node factories = selectSingleNode(root, "factories");
/* 1254 */     if (factories != null)
/*      */     {
/* 1257 */       allowedAttributesOrElements(factories, FACTORIES_CHILDREN);
/*      */ 
/* 1259 */       NodeList factoryList = selectNodeList(factories, "factory");
/* 1260 */       for (int i = 0; i < factoryList.getLength(); i++)
/*      */       {
/* 1262 */         Node factory = factoryList.item(i);
/* 1263 */         factory(factory);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void factory(Node factory)
/*      */   {
/* 1271 */     requiredAttributesOrElements(factory, FACTORY_REQ_CHILDREN);
/*      */ 
/* 1273 */     String id = getAttributeOrChildElement(factory, "id");
/* 1274 */     String className = getAttributeOrChildElement(factory, "class");
/* 1275 */     if (isValidID(id))
/*      */     {
/* 1277 */       FactorySettings factorySettings = new FactorySettings(id, className);
/*      */ 
/* 1280 */       NodeList properties = selectNodeList(factory, "properties/*");
/* 1281 */       if (properties.getLength() > 0)
/*      */       {
/* 1283 */         ConfigMap map = properties(properties, getSourceFileOf(factory));
/* 1284 */         factorySettings.addProperties(map);
/*      */       }
/* 1286 */       ((MessagingConfiguration)this.config).addFactorySettings(id, factorySettings);
/*      */     }
/*      */     else
/*      */     {
/* 1291 */       ConfigurationException ex = new ConfigurationException();
/* 1292 */       ex.setMessage(10110, new Object[] { "factory", id });
/* 1293 */       ex.setDetails(10110);
/* 1294 */       throw ex;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServerConfigurationParser
 * JD-Core Version:    0.6.0
 */
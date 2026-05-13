/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public abstract class ClientConfigurationParser extends AbstractConfigurationParser
/*     */ {
/*     */   protected void parseTopLevelConfig(Document doc)
/*     */   {
/*  41 */     Node root = selectSingleNode(doc, "/services-config");
/*     */ 
/*  43 */     if (root != null)
/*     */     {
/*  46 */       allowedChildElements(root, SERVICES_CONFIG_CHILDREN);
/*     */ 
/*  49 */       channelsSection(root);
/*     */ 
/*  52 */       services(root);
/*     */ 
/*  55 */       clusters(root);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void channelsSection(Node root)
/*     */   {
/*  61 */     Node channelsNode = selectSingleNode(root, "channels");
/*  62 */     if (channelsNode != null)
/*     */     {
/*  65 */       allowedAttributesOrElements(channelsNode, CHANNELS_CHILDREN);
/*     */ 
/*  67 */       NodeList channels = selectNodeList(channelsNode, "channel-definition");
/*  68 */       for (int i = 0; i < channels.getLength(); i++)
/*     */       {
/*  70 */         Node channel = channels.item(i);
/*  71 */         channelDefinition(channel);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void channelDefinition(Node channel)
/*     */   {
/*  79 */     requiredAttributesOrElements(channel, CHANNEL_DEFINITION_REQ_CHILDREN);
/*  80 */     allowedAttributesOrElements(channel, CHANNEL_DEFINITION_CHILDREN);
/*     */ 
/*  82 */     String id = getAttributeOrChildElement(channel, "id").toString().trim();
/*  83 */     if (isValidID(id))
/*     */     {
/*  86 */       if (this.config.getChannelSettings(id) != null)
/*     */       {
/*  89 */         ConfigurationException e = new ConfigurationException();
/*  90 */         e.setMessage(11127, new Object[] { id });
/*  91 */         throw e;
/*     */       }
/*     */ 
/*  94 */       ChannelSettings channelSettings = new ChannelSettings(id);
/*     */ 
/*  96 */       String clientType = getAttributeOrChildElement(channel, "class");
/*  97 */       channelSettings.setClientType(clientType);
/*     */ 
/* 100 */       Node endpoint = selectSingleNode(channel, "endpoint");
/* 101 */       if (endpoint != null)
/*     */       {
/* 104 */         allowedAttributesOrElements(endpoint, ENDPOINT_CHILDREN);
/*     */ 
/* 107 */         String uri = getAttributeOrChildElement(endpoint, "url");
/* 108 */         if ((uri == null) || ("".equals(uri)))
/* 109 */           uri = getAttributeOrChildElement(endpoint, "uri");
/* 110 */         channelSettings.setUri(uri);
/*     */ 
/* 112 */         this.config.addChannelSettings(id, channelSettings);
/*     */       }
/*     */ 
/* 116 */       NodeList properties = selectNodeList(channel, "properties/polling-enabled");
/* 117 */       if (properties.getLength() > 0)
/*     */       {
/* 119 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 120 */         channelSettings.addProperties(map);
/*     */       }
/* 122 */       properties = selectNodeList(channel, "properties/polling-interval-millis");
/* 123 */       if (properties.getLength() > 0)
/*     */       {
/* 125 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 126 */         channelSettings.addProperties(map);
/*     */       }
/* 128 */       properties = selectNodeList(channel, "properties/piggybacking-enabled");
/* 129 */       if (properties.getLength() > 0)
/*     */       {
/* 131 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 132 */         channelSettings.addProperties(map);
/*     */       }
/*     */ 
/* 135 */       properties = selectNodeList(channel, "properties/login-after-disconnect");
/* 136 */       if (properties.getLength() > 0)
/*     */       {
/* 138 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 139 */         channelSettings.addProperties(map);
/*     */       }
/*     */ 
/* 142 */       properties = selectNodeList(channel, "properties/serialization");
/* 143 */       if (properties.getLength() > 0)
/*     */       {
/* 145 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 146 */         ConfigMap serialization = map.getPropertyAsMap("serialization", null);
/* 147 */         if (serialization != null)
/*     */         {
/* 150 */           String enableSmallMessages = serialization.getProperty("enable-small-messages");
/* 151 */           if (enableSmallMessages != null)
/*     */           {
/* 153 */             ConfigMap clientMap = new ConfigMap();
/* 154 */             clientMap.addProperty("enable-small-messages", enableSmallMessages);
/* 155 */             channelSettings.addProperty("serialization", clientMap);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 160 */       properties = selectNodeList(channel, "properties/record-message-sizes");
/* 161 */       if (properties.getLength() > 0)
/*     */       {
/* 163 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 164 */         channelSettings.addProperties(map);
/*     */       }
/*     */ 
/* 167 */       properties = selectNodeList(channel, "properties/record-message-times");
/* 168 */       if (properties.getLength() > 0)
/*     */       {
/* 170 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 171 */         channelSettings.addProperties(map);
/*     */       }
/*     */ 
/* 174 */       properties = selectNodeList(channel, "properties/polling-interval-seconds");
/* 175 */       if (properties.getLength() > 0)
/*     */       {
/* 177 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 178 */         channelSettings.addProperties(map);
/*     */       }
/* 180 */       properties = selectNodeList(channel, "properties/connect-timeout-seconds");
/* 181 */       if (properties.getLength() > 0)
/*     */       {
/* 183 */         ConfigMap map = properties(properties, getSourceFileOf(channel));
/* 184 */         channelSettings.addProperties(map);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 190 */       ConfigurationException ex = new ConfigurationException();
/* 191 */       ex.setMessage(10110, new Object[] { "channel-definition", id });
/* 192 */       String details = "An id must be non-empty and not contain any list delimiter characters, i.e. commas, semi-colons or colons.";
/* 193 */       ex.setDetails(details);
/* 194 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void services(Node root)
/*     */   {
/* 200 */     Node servicesNode = selectSingleNode(root, "services");
/* 201 */     if (servicesNode != null)
/*     */     {
/* 204 */       allowedChildElements(servicesNode, SERVICES_CHILDREN);
/*     */ 
/* 207 */       Node defaultChannels = selectSingleNode(servicesNode, "default-channels");
/* 208 */       if (defaultChannels != null)
/*     */       {
/* 210 */         allowedChildElements(defaultChannels, DEFAULT_CHANNELS_CHILDREN);
/* 211 */         NodeList channels = selectNodeList(defaultChannels, "channel");
/* 212 */         for (int c = 0; c < channels.getLength(); c++)
/*     */         {
/* 214 */           Node chan = channels.item(c);
/* 215 */           allowedAttributes(chan, new String[] { "ref" });
/* 216 */           defaultChannel(chan);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 221 */       NodeList services = selectNodeList(servicesNode, "service-include");
/* 222 */       for (int i = 0; i < services.getLength(); i++)
/*     */       {
/* 224 */         Node service = services.item(i);
/* 225 */         serviceInclude(service);
/*     */       }
/*     */ 
/* 229 */       services = selectNodeList(servicesNode, "service");
/* 230 */       for (int i = 0; i < services.getLength(); i++)
/*     */       {
/* 232 */         Node service = services.item(i);
/* 233 */         service(service);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void clusters(Node root)
/*     */   {
/* 240 */     Node clusteringNode = selectSingleNode(root, "clusters");
/* 241 */     if (clusteringNode != null)
/*     */     {
/* 243 */       allowedAttributesOrElements(clusteringNode, CLUSTERING_CHILDREN);
/*     */ 
/* 245 */       NodeList clusters = selectNodeList(clusteringNode, "cluster");
/* 246 */       for (int i = 0; i < clusters.getLength(); i++)
/*     */       {
/* 248 */         Node cluster = clusters.item(i);
/* 249 */         requiredAttributesOrElements(cluster, CLUSTER_DEFINITION_CHILDREN);
/* 250 */         String clusterName = getAttributeOrChildElement(cluster, "id");
/* 251 */         if (!isValidID(clusterName))
/*     */           continue;
/* 253 */         String propsFileName = getAttributeOrChildElement(cluster, "properties");
/* 254 */         ClusterSettings clusterSettings = new ClusterSettings();
/* 255 */         clusterSettings.setClusterName(clusterName);
/* 256 */         clusterSettings.setPropsFileName(propsFileName);
/* 257 */         String defaultValue = getAttributeOrChildElement(cluster, "default");
/* 258 */         if ((defaultValue != null) && (defaultValue.length() > 0))
/*     */         {
/* 260 */           if (defaultValue.equalsIgnoreCase("true")) {
/* 261 */             clusterSettings.setDefault(true);
/* 262 */           } else if (!defaultValue.equalsIgnoreCase("false"))
/*     */           {
/* 264 */             ConfigurationException e = new ConfigurationException();
/* 265 */             e.setMessage(10215, new Object[] { clusterName, defaultValue });
/* 266 */             throw e;
/*     */           }
/*     */         }
/* 269 */         String ulb = getAttributeOrChildElement(cluster, "url-load-balancing");
/* 270 */         if ((ulb != null) && (ulb.length() > 0))
/*     */         {
/* 272 */           if (ulb.equalsIgnoreCase("false")) {
/* 273 */             clusterSettings.setURLLoadBalancing(false);
/* 274 */           } else if (!ulb.equalsIgnoreCase("true"))
/*     */           {
/* 276 */             ConfigurationException e = new ConfigurationException();
/* 277 */             e.setMessage(10216, new Object[] { clusterName, ulb });
/* 278 */             throw e;
/*     */           }
/*     */         }
/* 281 */         ((ClientConfiguration)this.config).addClusterSettings(clusterSettings);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void serviceInclude(Node serviceInclude)
/*     */   {
/* 290 */     requiredAttributesOrElements(serviceInclude, SERVICE_INCLUDE_CHILDREN);
/*     */ 
/* 292 */     String src = getAttributeOrChildElement(serviceInclude, "file-path");
/* 293 */     if (src.length() > 0)
/*     */     {
/* 295 */       Document doc = loadDocument(src, this.fileResolver.getIncludedFile(src));
/* 296 */       if ((this.fileResolver instanceof LocalFileResolver))
/*     */       {
/* 298 */         LocalFileResolver local = (LocalFileResolver)this.fileResolver;
/* 299 */         ((ClientConfiguration)this.config).addConfigPath(local.getIncludedPath(src), local.getIncludedLastModified(src));
/*     */       }
/*     */ 
/* 302 */       doc.getDocumentElement().normalize();
/*     */ 
/* 304 */       Node service = selectSingleNode(doc, "/service");
/* 305 */       if (service != null)
/*     */       {
/* 307 */         service(service);
/* 308 */         this.fileResolver.popIncludedFile();
/*     */       }
/*     */       else
/*     */       {
/* 313 */         ConfigurationException ex = new ConfigurationException();
/* 314 */         ex.setMessage(10112, new Object[] { "service" });
/* 315 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void service(Node service)
/*     */   {
/* 323 */     requiredAttributesOrElements(service, SERVICE_REQ_CHILDREN);
/* 324 */     allowedAttributesOrElements(service, SERVICE_CHILDREN);
/*     */ 
/* 326 */     String id = getAttributeOrChildElement(service, "id");
/* 327 */     if (isValidID(id))
/*     */     {
/* 329 */       ServiceSettings serviceSettings = this.config.getServiceSettings(id);
/* 330 */       if (serviceSettings == null)
/*     */       {
/* 332 */         serviceSettings = new ServiceSettings(id);
/* 333 */         this.config.addServiceSettings(serviceSettings);
/*     */       }
/*     */       else
/*     */       {
/* 338 */         ConfigurationException e = new ConfigurationException();
/* 339 */         e.setMessage(10113, new Object[] { id });
/* 340 */         throw e;
/*     */       }
/*     */ 
/* 346 */       Node defaultChannels = selectSingleNode(service, "default-channels");
/*     */       Iterator iter;
/* 347 */       if (defaultChannels != null)
/*     */       {
/* 349 */         allowedChildElements(defaultChannels, DEFAULT_CHANNELS_CHILDREN);
/* 350 */         NodeList channels = selectNodeList(defaultChannels, "channel");
/* 351 */         for (int c = 0; c < channels.getLength(); c++)
/*     */         {
/* 353 */           Node chan = channels.item(c);
/* 354 */           allowedAttributes(chan, new String[] { "ref" });
/* 355 */           defaultChannel(chan, serviceSettings);
/*     */         }
/*     */ 
/*     */       }
/* 359 */       else if (this.config.getDefaultChannels().size() > 0)
/*     */       {
/* 361 */         for (iter = this.config.getDefaultChannels().iterator(); iter.hasNext(); )
/*     */         {
/* 363 */           String channelId = (String)iter.next();
/* 364 */           ChannelSettings channel = this.config.getChannelSettings(channelId);
/* 365 */           serviceSettings.addDefaultChannel(channel);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 370 */       NodeList list = selectNodeList(service, "destination");
/* 371 */       for (int i = 0; i < list.getLength(); i++)
/*     */       {
/* 373 */         Node dest = list.item(i);
/* 374 */         destination(dest, serviceSettings);
/*     */       }
/*     */ 
/* 378 */       list = selectNodeList(service, "destination-include");
/* 379 */       for (int i = 0; i < list.getLength(); i++)
/*     */       {
/* 381 */         Node dest = list.item(i);
/* 382 */         destinationInclude(dest, serviceSettings);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 388 */       ConfigurationException ex = new ConfigurationException();
/* 389 */       ex.setMessage(10110, new Object[] { "service", id });
/* 390 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void defaultChannel(Node chan)
/*     */   {
/* 405 */     String ref = getAttributeOrChildElement(chan, "ref");
/*     */ 
/* 407 */     if (ref.length() > 0)
/*     */     {
/* 409 */       ChannelSettings channel = this.config.getChannelSettings(ref);
/* 410 */       if (channel != null)
/*     */       {
/* 412 */         this.config.addDefaultChannel(channel.getId());
/*     */       }
/*     */       else
/*     */       {
/* 417 */         ConfigurationException e = new ConfigurationException();
/* 418 */         e.setMessage(10109, new Object[] { "channel", ref });
/* 419 */         throw e;
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 425 */       ConfigurationException ex = new ConfigurationException();
/* 426 */       ex.setMessage(10116, new Object[] { "MessageBroker" });
/* 427 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void defaultChannel(Node chan, ServiceSettings serviceSettings)
/*     */   {
/* 442 */     String ref = getAttributeOrChildElement(chan, "ref").toString().trim();
/*     */ 
/* 444 */     if (ref.length() > 0)
/*     */     {
/* 446 */       ChannelSettings channel = this.config.getChannelSettings(ref);
/* 447 */       if (channel != null)
/*     */       {
/* 449 */         serviceSettings.addDefaultChannel(channel);
/*     */       }
/*     */       else
/*     */       {
/* 454 */         ConfigurationException e = new ConfigurationException();
/* 455 */         e.setMessage(10109, new Object[] { "channel", ref });
/* 456 */         throw e;
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 462 */       ConfigurationException ex = new ConfigurationException();
/* 463 */       ex.setMessage(10116, new Object[] { serviceSettings.getId() });
/* 464 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void destinationInclude(Node destInclude, ServiceSettings serviceSettings)
/*     */   {
/* 471 */     requiredAttributesOrElements(destInclude, DESTINATION_INCLUDE_CHILDREN);
/*     */ 
/* 473 */     String src = getAttributeOrChildElement(destInclude, "file-path");
/* 474 */     if (src.length() > 0)
/*     */     {
/* 476 */       Document doc = loadDocument(src, this.fileResolver.getIncludedFile(src));
/* 477 */       if ((this.fileResolver instanceof LocalFileResolver))
/*     */       {
/* 479 */         LocalFileResolver local = (LocalFileResolver)this.fileResolver;
/* 480 */         ((ClientConfiguration)this.config).addConfigPath(local.getIncludedPath(src), local.getIncludedLastModified(src));
/*     */       }
/*     */ 
/* 483 */       doc.getDocumentElement().normalize();
/*     */ 
/* 485 */       Node dest = selectSingleNode(doc, "/destination");
/* 486 */       if (dest != null)
/*     */       {
/* 488 */         destination(dest, serviceSettings);
/* 489 */         this.fileResolver.popIncludedFile();
/*     */       }
/*     */       else
/*     */       {
/* 494 */         ConfigurationException ex = new ConfigurationException();
/* 495 */         ex.setMessage(10118, new Object[] { "destination" });
/* 496 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void destination(Node dest, ServiceSettings serviceSettings)
/*     */   {
/* 504 */     requiredAttributesOrElements(dest, DESTINATION_REQ_CHILDREN);
/* 505 */     allowedAttributes(dest, DESTINATION_ATTR);
/* 506 */     allowedChildElements(dest, DESTINATION_CHILDREN);
/*     */ 
/* 508 */     String serviceId = serviceSettings.getId();
/*     */ 
/* 510 */     DestinationSettings destinationSettings = null;
/* 511 */     String id = getAttributeOrChildElement(dest, "id");
/* 512 */     if (isValidID(id))
/*     */     {
/* 514 */       destinationSettings = (DestinationSettings)serviceSettings.getDestinationSettings().get(id);
/* 515 */       if (destinationSettings != null)
/*     */       {
/* 518 */         ConfigurationException e = new ConfigurationException();
/* 519 */         e.setMessage(10122, new Object[] { id, serviceId });
/* 520 */         throw e;
/*     */       }
/*     */ 
/* 523 */       destinationSettings = new DestinationSettings(id);
/* 524 */       serviceSettings.addDestinationSettings(destinationSettings);
/*     */     }
/*     */     else
/*     */     {
/* 529 */       ConfigurationException ex = new ConfigurationException();
/* 530 */       ex.setMessage(10119, new Object[] { "destination", id, serviceId });
/* 531 */       throw ex;
/*     */     }
/*     */ 
/* 535 */     NodeList properties = selectNodeList(dest, "properties/*");
/* 536 */     if (properties.getLength() > 0)
/*     */     {
/* 538 */       ConfigMap map = properties(properties, getSourceFileOf(dest));
/* 539 */       destinationSettings.addProperties(map);
/*     */     }
/*     */ 
/* 543 */     destinationChannels(dest, destinationSettings, serviceSettings);
/*     */   }
/*     */ 
/*     */   private void destinationChannels(Node dest, DestinationSettings destinationSettings, ServiceSettings serviceSettings)
/*     */   {
/* 549 */     String destId = destinationSettings.getId();
/*     */ 
/* 552 */     String channelsList = evaluateExpression(dest, "@channels").toString().trim();
/* 553 */     if (channelsList.length() > 0)
/*     */     {
/* 555 */       StringTokenizer st = new StringTokenizer(channelsList, ",;:");
/* 556 */       while (st.hasMoreTokens())
/*     */       {
/* 558 */         String ref = st.nextToken().trim();
/* 559 */         ChannelSettings channel = this.config.getChannelSettings(ref);
/* 560 */         if (channel != null)
/*     */         {
/* 562 */           destinationSettings.addChannelSettings(channel);
/*     */         }
/*     */         else
/*     */         {
/* 567 */           ConfigurationException ex = new ConfigurationException();
/* 568 */           ex.setMessage(10120, new Object[] { "channel", ref, destId });
/* 569 */           throw ex;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 576 */       Node channelsNode = selectSingleNode(dest, "channels");
/* 577 */       if (channelsNode != null)
/*     */       {
/* 579 */         allowedChildElements(channelsNode, DESTINATION_CHANNELS_CHILDREN);
/*     */ 
/* 581 */         NodeList channels = selectNodeList(channelsNode, "channel");
/* 582 */         if (channels.getLength() > 0)
/*     */         {
/* 584 */           for (int c = 0; c < channels.getLength(); c++)
/*     */           {
/* 586 */             Node chan = channels.item(c);
/*     */ 
/* 589 */             requiredAttributesOrElements(chan, DESTINATION_CHANNEL_REQ_CHILDREN);
/*     */ 
/* 591 */             String ref = getAttributeOrChildElement(chan, "ref").toString().trim();
/* 592 */             if (ref.length() > 0)
/*     */             {
/* 594 */               ChannelSettings channel = this.config.getChannelSettings(ref);
/* 595 */               if (channel != null)
/*     */               {
/* 597 */                 destinationSettings.addChannelSettings(channel);
/*     */               }
/*     */               else
/*     */               {
/* 602 */                 ConfigurationException ex = new ConfigurationException();
/* 603 */                 ex.setMessage(10120, new Object[] { "channel", ref, destId });
/* 604 */                 throw ex;
/*     */               }
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 610 */               ConfigurationException ex = new ConfigurationException();
/* 611 */               ex.setMessage(10121, new Object[] { "channel", ref, destId });
/* 612 */               throw ex;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 620 */         List defaultChannels = serviceSettings.getDefaultChannels();
/* 621 */         Iterator it = defaultChannels.iterator();
/* 622 */         while (it.hasNext())
/*     */         {
/* 624 */           ChannelSettings channel = (ChannelSettings)it.next();
/* 625 */           destinationSettings.addChannelSettings(channel);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 630 */     if (destinationSettings.getChannelSettings().size() <= 0)
/*     */     {
/* 633 */       ConfigurationException ex = new ConfigurationException();
/* 634 */       ex.setMessage(10123, new Object[] { destId });
/* 635 */       throw ex;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ClientConfigurationParser
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.LocalizedException;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ServicesDependencies
/*     */ {
/*  22 */   private String xmlInit = "";
/*  23 */   private StringBuffer imports = new StringBuffer();
/*  24 */   private StringBuffer references = new StringBuffer();
/*     */   private List channelClasses;
/*     */   private Map configPaths;
/*     */   private Map lazyAssociations;
/*  29 */   private static final List channel_excludes = new ArrayList();
/*     */   public static final boolean traceConfig;
/*     */ 
/*     */   public ServicesDependencies(String path, String parserClass, String contextRoot)
/*     */   {
/*  39 */     ClientConfiguration config = getClientConfiguration(path, parserClass);
/*     */ 
/*  41 */     if (config != null)
/*     */     {
/*  43 */       Map importMap = new HashMap();
/*  44 */       this.lazyAssociations = new HashMap();
/*  45 */       this.configPaths = config.getConfigPaths();
/*  46 */       this.xmlInit = codegenXmlInit(config, contextRoot, importMap);
/*  47 */       codegenServiceImportsAndReferences(importMap, this.imports, this.references);
/*  48 */       this.channelClasses = listChannelClasses(config);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set getLazyAssociations(String destination)
/*     */   {
/*  54 */     if (this.lazyAssociations == null)
/*     */     {
/*  56 */       this.lazyAssociations = new HashMap();
/*     */     }
/*     */ 
/*  59 */     return (Set)this.lazyAssociations.get(destination);
/*     */   }
/*     */ 
/*     */   public void addLazyAssociation(String destination, String associationProp)
/*     */   {
/*  64 */     Set la = getLazyAssociations(destination);
/*  65 */     if (la == null)
/*     */     {
/*  67 */       la = new HashSet();
/*  68 */       this.lazyAssociations.put(destination, la);
/*     */     }
/*  70 */     la.add(associationProp);
/*     */   }
/*     */ 
/*     */   public String getServerConfigXmlInit()
/*     */   {
/*  75 */     return this.xmlInit;
/*     */   }
/*     */ 
/*     */   public String getImports()
/*     */   {
/*  80 */     return this.imports.toString();
/*     */   }
/*     */ 
/*     */   public String getReferences()
/*     */   {
/*  85 */     return this.references.toString();
/*     */   }
/*     */ 
/*     */   public List getChannelClasses()
/*     */   {
/*  90 */     return this.channelClasses;
/*     */   }
/*     */ 
/*     */   public void addChannelClass(String className)
/*     */   {
/*  95 */     this.channelClasses.add(className);
/*     */   }
/*     */ 
/*     */   public void addConfigPath(String path, long modified)
/*     */   {
/* 100 */     this.configPaths.put(path, new Long(modified));
/*     */   }
/*     */ 
/*     */   public Map getConfigPaths()
/*     */   {
/* 105 */     return this.configPaths;
/*     */   }
/*     */ 
/*     */   public static ClientConfiguration getClientConfiguration(String path, String parserClass)
/*     */   {
/* 110 */     ClientConfiguration config = new ClientConfiguration();
/*     */ 
/* 112 */     ConfigurationParser parser = getConfigurationParser(parserClass);
/*     */ 
/* 114 */     if (parser == null)
/*     */     {
/* 117 */       LocalizedException lme = new LocalizedException();
/* 118 */       lme.setMessage(10138);
/* 119 */       throw lme;
/*     */     }
/*     */ 
/* 122 */     LocalFileResolver local = new LocalFileResolver();
/* 123 */     parser.parse(path, local, config);
/*     */ 
/* 125 */     config.addConfigPath(path, new File(path).lastModified());
/*     */ 
/* 127 */     return config;
/*     */   }
/*     */ 
/*     */   static ConfigurationParser getConfigurationParser(String className)
/*     */   {
/* 132 */     ConfigurationParser parser = null;
/* 133 */     Class parserClass = null;
/*     */ 
/* 136 */     if (className != null)
/*     */     {
/*     */       try
/*     */       {
/* 140 */         parserClass = Class.forName(className);
/* 141 */         parser = (ConfigurationParser)parserClass.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 145 */         if (traceConfig)
/*     */         {
/* 147 */           System.out.println("Could not load services configuration parser as: " + className);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 153 */     if (parser == null)
/*     */     {
/*     */       try
/*     */       {
/* 157 */         Class.forName("org.apache.xpath.CachedXPathAPI");
/* 158 */         className = "flex.messaging.config.ApacheXPathClientConfigurationParser";
/* 159 */         parserClass = Class.forName(className);
/* 160 */         parser = (ConfigurationParser)parserClass.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 164 */         if (traceConfig)
/*     */         {
/* 166 */           System.out.println("Could not load configuration parser as: " + className);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 172 */     if (parser == null)
/*     */     {
/*     */       try
/*     */       {
/* 176 */         className = "flex.messaging.config.XPathClientConfigurationParser";
/* 177 */         parserClass = Class.forName(className);
/*     */ 
/* 179 */         Class.forName("javax.xml.xpath.XPathExpressionException");
/*     */ 
/* 181 */         parser = (ConfigurationParser)parserClass.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 185 */         if (traceConfig)
/*     */         {
/* 187 */           System.out.println("Could not load configuration parser as: " + className);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 192 */     if ((traceConfig) && (parser != null))
/*     */     {
/* 194 */       System.out.println("Services Configuration Parser: " + parser.getClass().getName());
/*     */     }
/*     */ 
/* 197 */     return parser;
/*     */   }
/*     */ 
/*     */   private static List listChannelClasses(ServicesConfiguration config)
/*     */   {
/* 202 */     List channelList = new ArrayList();
/* 203 */     Iterator it = config.getAllChannelSettings().values().iterator();
/* 204 */     while (it.hasNext())
/*     */     {
/* 206 */       ChannelSettings settings = (ChannelSettings)it.next();
/* 207 */       String clientType = settings.getClientType();
/* 208 */       channelList.add(clientType);
/*     */     }
/*     */ 
/* 211 */     return channelList;
/*     */   }
/*     */ 
/*     */   private String codegenXmlInit(ServicesConfiguration config, String contextRoot, Map serviceImportMap)
/*     */   {
/* 219 */     StringBuffer e4x = new StringBuffer();
/*     */ 
/* 221 */     e4x.append("<services>\n");
/*     */ 
/* 224 */     if (config.getDefaultChannels().size() > 0)
/*     */     {
/* 226 */       e4x.append("\t<default-channels>\n");
/* 227 */       for (Iterator chanIter = config.getDefaultChannels().iterator(); chanIter.hasNext(); )
/*     */       {
/* 229 */         String id = (String)chanIter.next();
/* 230 */         e4x.append("\t\t<channel ref=\"" + id + "\"/>\n");
/*     */       }
/* 232 */       e4x.append("\t</default-channels>\n");
/*     */     }
/*     */ 
/* 235 */     ClusterSettings defaultCluster = config.getDefaultCluster();
/*     */ 
/* 238 */     if ((defaultCluster != null) && (!defaultCluster.getURLLoadBalancing())) {
/* 239 */       defaultCluster = null;
/*     */     }
/* 241 */     for (Iterator servIter = config.getAllServiceSettings().iterator(); servIter.hasNext(); )
/*     */     {
/* 243 */       ServiceSettings entry = (ServiceSettings)servIter.next();
/*     */ 
/* 253 */       String serviceType = entry.getId();
/* 254 */       e4x.append("\t<service id=\"");
/* 255 */       e4x.append(serviceType);
/* 256 */       e4x.append("\"");
/* 257 */       e4x.append(">\n");
/*     */ 
/* 259 */       for (Iterator destIter = entry.getDestinationSettings().values().iterator(); destIter.hasNext(); )
/*     */       {
/* 261 */         DestinationSettings dest = (DestinationSettings)destIter.next();
/* 262 */         String destination = dest.getId();
/* 263 */         e4x.append("\t\t<destination id=\"" + destination + "\">\n");
/*     */ 
/* 266 */         ConfigMap metadata = dest.getProperties().getPropertyAsMap("metadata", null);
/* 267 */         boolean closePropTag = false;
/* 268 */         if (metadata != null)
/*     */         {
/* 270 */           e4x.append("\t\t\t<properties>\n\t\t\t\t<metadata\n");
/* 271 */           String extendsStr = metadata.getPropertyAsString("extends", null);
/* 272 */           if (extendsStr != null)
/*     */           {
/* 274 */             e4x.append(" extends=\"");
/* 275 */             e4x.append(extendsStr);
/* 276 */             e4x.append("\"");
/*     */           }
/* 278 */           e4x.append(">");
/* 279 */           closePropTag = true;
/* 280 */           List identities = metadata.getPropertyAsList("identity", null);
/* 281 */           if (identities != null)
/*     */           {
/* 283 */             Iterator it = identities.iterator();
/* 284 */             while (it.hasNext())
/*     */             {
/* 286 */               Object o = it.next();
/* 287 */               String identityName = null;
/* 288 */               String undefinedValue = null;
/* 289 */               if ((o instanceof String))
/*     */               {
/* 291 */                 identityName = (String)o;
/*     */               }
/* 293 */               else if ((o instanceof ConfigMap))
/*     */               {
/* 295 */                 identityName = ((ConfigMap)o).getPropertyAsString("property", null);
/* 296 */                 undefinedValue = ((ConfigMap)o).getPropertyAsString("undefined-value", null);
/*     */               }
/*     */ 
/* 299 */               if (identityName != null)
/*     */               {
/* 301 */                 e4x.append("\t\t\t\t\t<identity property=\"");
/* 302 */                 e4x.append(identityName);
/* 303 */                 e4x.append("\"");
/* 304 */                 if (undefinedValue != null)
/*     */                 {
/* 306 */                   e4x.append(" undefined-value=\"");
/* 307 */                   e4x.append(undefinedValue);
/* 308 */                   e4x.append("\"");
/*     */                 }
/* 310 */                 e4x.append("/>\n");
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 315 */           codegenServiceAssociations(metadata, e4x, destination, "one-to-many");
/* 316 */           codegenServiceAssociations(metadata, e4x, destination, "many-to-many");
/* 317 */           codegenServiceAssociations(metadata, e4x, destination, "one-to-one");
/* 318 */           codegenServiceAssociations(metadata, e4x, destination, "many-to-one");
/*     */ 
/* 320 */           e4x.append("\t\t\t\t</metadata>\n");
/*     */         }
/*     */ 
/* 323 */         String itemClass = dest.getProperties().getPropertyAsString("item-class", null);
/* 324 */         if (itemClass != null)
/*     */         {
/* 326 */           if (!closePropTag)
/*     */           {
/* 328 */             e4x.append("\t\t\t<properties>\n");
/* 329 */             closePropTag = true;
/*     */           }
/*     */ 
/* 332 */           e4x.append("\t\t\t\t<item-class>");
/* 333 */           e4x.append(itemClass);
/* 334 */           e4x.append("</item-class>\n");
/*     */         }
/*     */ 
/* 338 */         ConfigMap network = dest.getProperties().getPropertyAsMap("network", null);
/* 339 */         ConfigMap clusterInfo = null;
/* 340 */         ConfigMap pagingInfo = null;
/* 341 */         ConfigMap reconnectInfo = null;
/* 342 */         if ((network != null) || (defaultCluster != null))
/*     */         {
/* 344 */           if (!closePropTag)
/*     */           {
/* 346 */             e4x.append("\t\t\t<properties>\n");
/* 347 */             closePropTag = true;
/*     */           }
/* 349 */           e4x.append("\t\t\t\t<network>\n");
/*     */ 
/* 351 */           if (network != null)
/* 352 */             pagingInfo = network.getPropertyAsMap("paging", null);
/* 353 */           if (pagingInfo != null)
/*     */           {
/* 355 */             String enabled = pagingInfo.getPropertyAsString("enabled", "false");
/* 356 */             e4x.append("\t\t\t\t\t<paging enabled=\"");
/* 357 */             e4x.append(enabled);
/* 358 */             e4x.append("\"");
/*     */ 
/* 362 */             String size = pagingInfo.getPropertyAsString("page-size", pagingInfo.getPropertyAsString("pageSize", null));
/* 363 */             if (size != null)
/*     */             {
/* 365 */               e4x.append(" page-size=\"");
/* 366 */               e4x.append(size);
/* 367 */               e4x.append("\"");
/*     */ 
/* 370 */               e4x.append(" pageSize=\"");
/* 371 */               e4x.append(size);
/* 372 */               e4x.append("\"");
/*     */             }
/* 374 */             e4x.append("/>\n");
/*     */           }
/*     */ 
/* 377 */           if (network != null)
/* 378 */             reconnectInfo = network.getPropertyAsMap("reconnect", null);
/* 379 */           if (reconnectInfo != null)
/*     */           {
/* 381 */             String fetchOption = reconnectInfo.getPropertyAsString("fetch", "IDENTITY");
/* 382 */             e4x.append("\t\t\t\t\t<reconnect fetch=\"");
/* 383 */             e4x.append(fetchOption.toUpperCase());
/* 384 */             e4x.append("\" />\n");
/*     */           }
/*     */ 
/* 387 */           if (network != null)
/* 388 */             clusterInfo = network.getPropertyAsMap("cluster", null);
/* 389 */           if (clusterInfo != null)
/*     */           {
/* 391 */             String clusterId = clusterInfo.getPropertyAsString("ref", null);
/*     */ 
/* 393 */             ClusterSettings clusterSettings = config.getClusterSettings(clusterId);
/* 394 */             if ((clusterSettings != null) && (clusterSettings.getURLLoadBalancing()))
/*     */             {
/* 397 */               e4x.append("\t\t\t\t\t<cluster ref=\"");
/* 398 */               e4x.append(clusterId);
/* 399 */               e4x.append("\"/>\n");
/*     */             }
/*     */           }
/* 402 */           else if (defaultCluster != null)
/*     */           {
/* 404 */             e4x.append("\t\t\t\t\t<cluster");
/* 405 */             if (defaultCluster.getClusterName() != null)
/*     */             {
/* 407 */               e4x.append(" ref=\"");
/* 408 */               e4x.append(defaultCluster.getClusterName());
/* 409 */               e4x.append("\"");
/*     */             }
/* 411 */             e4x.append("/>\n");
/*     */           }
/* 413 */           e4x.append("\t\t\t\t</network>\n");
/*     */         }
/*     */ 
/* 416 */         String useTransactions = dest.getProperties().getPropertyAsString("use-transactions", "true");
/*     */ 
/* 418 */         if (useTransactions.equalsIgnoreCase("false"))
/*     */         {
/* 420 */           if (!closePropTag)
/*     */           {
/* 422 */             e4x.append("\t\t\t<properties>\n");
/* 423 */             closePropTag = true;
/*     */           }
/* 425 */           e4x.append("\t\t\t\t<use-transactions>false</use-transactions>\n");
/*     */         }
/*     */ 
/* 428 */         String autoSyncEnabled = dest.getProperties().getPropertyAsString("auto-sync-enabled", "true");
/*     */ 
/* 430 */         if (autoSyncEnabled.equalsIgnoreCase("false"))
/*     */         {
/* 432 */           if (!closePropTag)
/*     */           {
/* 434 */             e4x.append("\t\t\t<properties>\n");
/* 435 */             closePropTag = true;
/*     */           }
/* 437 */           e4x.append("\t\t\t\t<auto-sync-enabled>false</auto-sync-enabled>\n");
/*     */         }
/*     */ 
/* 440 */         if (closePropTag)
/*     */         {
/* 442 */           e4x.append("\t\t\t</properties>\n");
/*     */         }
/*     */ 
/* 445 */         e4x.append("\t\t\t<channels>\n");
/* 446 */         for (Iterator chanIter = dest.getChannelSettings().iterator(); chanIter.hasNext(); )
/*     */         {
/* 448 */           e4x.append("\t\t\t\t<channel ref=\"" + ((ChannelSettings)chanIter.next()).getId() + "\"/>\n");
/*     */         }
/* 450 */         e4x.append("\t\t\t</channels>\n");
/* 451 */         e4x.append("\t\t</destination>\n");
/*     */       }
/* 453 */       e4x.append("\t</service>\n");
/*     */     }
/*     */ 
/* 456 */     e4x.append("\t<channels>\n");
/*     */ 
/* 458 */     for (Iterator chanIter = config.getAllChannelSettings().values().iterator(); chanIter.hasNext(); )
/*     */     {
/* 460 */       ChannelSettings chan = (ChannelSettings)chanIter.next();
/* 461 */       String channelType = chan.getClientType();
/* 462 */       serviceImportMap.put(channelType, channelType);
/* 463 */       e4x.append("\t\t<channel id=\"" + chan.getId() + "\" type=\"" + channelType + "\">\n");
/* 464 */       e4x.append("\t\t\t<endpoint uri=\"" + chan.getClientParsedUri(contextRoot) + "\"/>\n");
/* 465 */       e4x.append("\t\t\t<properties>\n");
/* 466 */       channelProperties(chan.getProperties(), e4x, "\t\t\t\t");
/* 467 */       e4x.append("\t\t\t</properties>\n");
/* 468 */       e4x.append("\t\t</channel>\n");
/*     */     }
/* 470 */     e4x.append("\t</channels>\n");
/* 471 */     e4x.append("</services>");
/*     */ 
/* 473 */     return "\nServerConfig.xml =\n" + e4x.toString() + ";\n";
/*     */   }
/*     */ 
/*     */   private void channelProperties(ConfigMap properties, StringBuffer buf, String indent)
/*     */   {
/* 481 */     for (Iterator nameIter = properties.propertyNames().iterator(); nameIter.hasNext(); )
/*     */     {
/* 483 */       String name = (String)nameIter.next();
/* 484 */       Object value = properties.get(name);
/* 485 */       if ((value instanceof String))
/*     */       {
/* 487 */         if (channel_excludes.contains(name))
/*     */           continue;
/* 489 */         buf.append(indent);
/* 490 */         buf.append("<" + name + ">" + (String)value + "</" + name + ">\n");
/*     */       }
/* 492 */       else if ((value instanceof ConfigMap))
/*     */       {
/* 494 */         ConfigMap childProperties = (ConfigMap)value;
/* 495 */         buf.append(indent);
/* 496 */         buf.append("<" + name + ">\n");
/* 497 */         channelProperties(childProperties, buf, indent + "\t");
/* 498 */         buf.append(indent);
/* 499 */         buf.append("</" + name + ">\n");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void codegenServiceAssociations(ConfigMap metadata, StringBuffer e4x, String destination, String relation)
/*     */   {
/* 509 */     List references = metadata.getPropertyAsList(relation, null);
/* 510 */     if (references != null)
/*     */     {
/* 512 */       Iterator it = references.iterator();
/* 513 */       while (it.hasNext())
/*     */       {
/* 515 */         Object ref = it.next();
/* 516 */         if ((ref instanceof ConfigMap))
/*     */         {
/* 518 */           ConfigMap refMap = (ConfigMap)ref;
/* 519 */           String name = refMap.getPropertyAsString("property", null);
/* 520 */           String associatedDestination = refMap.getPropertyAsString("destination", null);
/* 521 */           String lazy = refMap.getPropertyAsString("lazy", null);
/* 522 */           String loadOnDemand = refMap.getPropertyAsString("load-on-demand", null);
/* 523 */           String hierarchicalEvents = refMap.getPropertyAsString("hierarchical-events", null);
/* 524 */           String pageSize = refMap.getPropertyAsString("page-size", refMap.getPropertyAsString("pageSize", null));
/* 525 */           String pagedUpdates = refMap.getPropertyAsString("paged-updates", null);
/* 526 */           String cascade = refMap.getPropertyAsString("cascade", null);
/* 527 */           String ordered = refMap.getPropertyAsString("ordered", null);
/* 528 */           e4x.append("\t\t\t\t\t<");
/* 529 */           e4x.append(relation);
/* 530 */           if (lazy != null)
/*     */           {
/* 532 */             e4x.append(" lazy=\"");
/* 533 */             e4x.append(lazy);
/* 534 */             e4x.append("\"");
/*     */ 
/* 536 */             if (Boolean.valueOf(lazy.toLowerCase().trim()).booleanValue())
/*     */             {
/* 538 */               addLazyAssociation(destination, name);
/*     */             }
/*     */           }
/* 541 */           e4x.append(" property=\"");
/* 542 */           e4x.append(name);
/* 543 */           e4x.append("\" destination=\"");
/* 544 */           e4x.append(associatedDestination);
/* 545 */           e4x.append("\"");
/* 546 */           String readOnly = refMap.getPropertyAsString("read-only", null);
/* 547 */           if ((readOnly != null) && (readOnly.equalsIgnoreCase("true")))
/*     */           {
/* 549 */             e4x.append(" read-only=\"true\"");
/*     */           }
/* 551 */           if ((loadOnDemand != null) && (loadOnDemand.equalsIgnoreCase("true")))
/* 552 */             e4x.append(" load-on-demand=\"true\"");
/* 553 */           if ((hierarchicalEvents != null) && (hierarchicalEvents.equalsIgnoreCase("true")))
/* 554 */             e4x.append(" hierarchical-events=\"true\"");
/* 555 */           if (pagedUpdates != null)
/* 556 */             e4x.append(" paged-updates=\"" + pagedUpdates + "\"");
/* 557 */           if (pageSize != null)
/* 558 */             e4x.append(" page-size=\"" + pageSize + "\"");
/* 559 */           if (cascade != null)
/* 560 */             e4x.append(" cascade=\"" + cascade + "\"");
/* 561 */           if (ordered != null)
/* 562 */             e4x.append(" ordered=\"" + ordered + "\"");
/* 563 */           e4x.append("/>\n");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void codegenServiceImportsAndReferences(Map map, StringBuffer imports, StringBuffer references)
/*     */   {
/* 579 */     imports.append("import mx.messaging.config.ServerConfig;\n");
/* 580 */     references.append("   // static references for configured channels\n");
/* 581 */     for (Iterator chanIter = map.values().iterator(); chanIter.hasNext(); )
/*     */     {
/* 583 */       String type = (String)chanIter.next();
/* 584 */       imports.append("import ");
/* 585 */       imports.append(type);
/* 586 */       imports.append(";\n");
/* 587 */       references.append("   private static var ");
/* 588 */       references.append(type.replace('.', '_'));
/* 589 */       references.append("_ref:");
/* 590 */       references.append(type.substring(type.lastIndexOf(".") + 1) + ";\n");
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  32 */     channel_excludes.add("redirect-url");
/*     */ 
/*  35 */     traceConfig = System.getProperty("trace.config") != null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServicesDependencies
 * JD-Core Version:    0.6.0
 */
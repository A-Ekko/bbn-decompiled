/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.log4j.config.PropertySetter;
/*     */ import org.apache.log4j.helpers.FileWatchdog;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ 
/*     */ public class PropertyConfigurator
/*     */   implements Configurator
/*     */ {
/*  81 */   protected Hashtable registry = new Hashtable(11);
/*  82 */   protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
/*     */   static final String CATEGORY_PREFIX = "log4j.category.";
/*     */   static final String LOGGER_PREFIX = "log4j.logger.";
/*     */   static final String FACTORY_PREFIX = "log4j.factory";
/*     */   static final String ADDITIVITY_PREFIX = "log4j.additivity.";
/*     */   static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
/*     */   static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
/*     */   static final String APPENDER_PREFIX = "log4j.appender.";
/*     */   static final String RENDERER_PREFIX = "log4j.renderer.";
/*     */   static final String THRESHOLD_PREFIX = "log4j.threshold";
/*     */   public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
/*     */   private static final String INTERNAL_ROOT_NAME = "root";
/*     */ 
/*     */   public void doConfigure(String configFileName, LoggerRepository hierarchy)
/*     */   {
/* 295 */     Properties props = new Properties();
/*     */     try {
/* 297 */       FileInputStream istream = new FileInputStream(configFileName);
/* 298 */       props.load(istream);
/* 299 */       istream.close();
/*     */     }
/*     */     catch (IOException e) {
/* 302 */       LogLog.error("Could not read configuration file [" + configFileName + "].", e);
/* 303 */       LogLog.error("Ignoring configuration file [" + configFileName + "].");
/* 304 */       return;
/*     */     }
/*     */ 
/* 307 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   public static void configure(String configFilename)
/*     */   {
/* 315 */     new PropertyConfigurator().doConfigure(configFilename, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(URL configURL)
/*     */   {
/* 327 */     new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(Properties properties)
/*     */   {
/* 340 */     new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename)
/*     */   {
/* 355 */     configureAndWatch(configFilename, 60000L);
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename, long delay)
/*     */   {
/* 373 */     PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
/* 374 */     pdog.setDelay(delay);
/* 375 */     pdog.start();
/*     */   }
/*     */ 
/*     */   public void doConfigure(Properties properties, LoggerRepository hierarchy)
/*     */   {
/* 387 */     String value = properties.getProperty("log4j.debug");
/* 388 */     if (value == null) {
/* 389 */       value = properties.getProperty("log4j.configDebug");
/* 390 */       if (value != null) {
/* 391 */         LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
/*     */       }
/*     */     }
/* 394 */     if (value != null) {
/* 395 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
/*     */     }
/*     */ 
/* 398 */     String thresholdStr = OptionConverter.findAndSubst("log4j.threshold", properties);
/*     */ 
/* 400 */     if (thresholdStr != null) {
/* 401 */       hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
/*     */ 
/* 403 */       LogLog.debug("Hierarchy threshold set to [" + hierarchy.getThreshold() + "].");
/*     */     }
/*     */ 
/* 406 */     configureRootCategory(properties, hierarchy);
/* 407 */     configureLoggerFactory(properties);
/* 408 */     parseCatsAndRenderers(properties, hierarchy);
/*     */ 
/* 410 */     LogLog.debug("Finished configuring.");
/*     */ 
/* 413 */     this.registry.clear();
/*     */   }
/*     */ 
/*     */   public void doConfigure(URL configURL, LoggerRepository hierarchy)
/*     */   {
/* 421 */     Properties props = new Properties();
/* 422 */     LogLog.debug("Reading configuration from URL " + configURL);
/*     */     try {
/* 424 */       props.load(configURL.openStream());
/*     */     }
/*     */     catch (IOException e) {
/* 427 */       LogLog.error("Could not read configuration file from URL [" + configURL + "].", e);
/*     */ 
/* 429 */       LogLog.error("Ignoring configuration file [" + configURL + "].");
/* 430 */       return;
/*     */     }
/* 432 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   protected void configureLoggerFactory(Properties props)
/*     */   {
/* 451 */     String factoryClassName = OptionConverter.findAndSubst("log4j.loggerFactory", props);
/*     */ 
/* 453 */     if (factoryClassName != null) {
/* 454 */       LogLog.debug("Setting category factory to [" + factoryClassName + "].");
/* 455 */       this.loggerFactory = ((LoggerFactory)OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, this.loggerFactory));
/*     */ 
/* 459 */       PropertySetter.setProperties(this.loggerFactory, props, "log4j.factory.");
/*     */     }
/*     */   }
/*     */ 
/*     */   void configureRootCategory(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 487 */     String effectiveFrefix = "log4j.rootLogger";
/* 488 */     String value = OptionConverter.findAndSubst("log4j.rootLogger", props);
/*     */ 
/* 490 */     if (value == null) {
/* 491 */       value = OptionConverter.findAndSubst("log4j.rootCategory", props);
/* 492 */       effectiveFrefix = "log4j.rootCategory";
/*     */     }
/*     */ 
/* 495 */     if (value == null) {
/* 496 */       LogLog.debug("Could not find root logger information. Is this OK?");
/*     */     } else {
/* 498 */       Logger root = hierarchy.getRootLogger();
/* 499 */       synchronized (root) {
/* 500 */         parseCategory(props, root, effectiveFrefix, "root", value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 511 */     Enumeration enum = props.propertyNames();
/* 512 */     while (enum.hasMoreElements()) {
/* 513 */       String key = (String)enum.nextElement();
/* 514 */       if ((key.startsWith("log4j.category.")) || (key.startsWith("log4j.logger."))) {
/* 515 */         String loggerName = null;
/* 516 */         if (key.startsWith("log4j.category."))
/* 517 */           loggerName = key.substring("log4j.category.".length());
/* 518 */         else if (key.startsWith("log4j.logger.")) {
/* 519 */           loggerName = key.substring("log4j.logger.".length());
/*     */         }
/* 521 */         String value = OptionConverter.findAndSubst(key, props);
/* 522 */         Logger logger = hierarchy.getLogger(loggerName, this.loggerFactory);
/* 523 */         synchronized (logger) {
/* 524 */           parseCategory(props, logger, key, loggerName, value);
/* 525 */           parseAdditivityForLogger(props, logger, loggerName);
/*     */         }
/* 527 */       } else if (key.startsWith("log4j.renderer.")) {
/* 528 */         String renderedClass = key.substring("log4j.renderer.".length());
/* 529 */         String renderingClass = OptionConverter.findAndSubst(key, props);
/* 530 */         if ((hierarchy instanceof RendererSupport))
/* 531 */           RendererMap.addRenderer((RendererSupport)hierarchy, renderedClass, renderingClass);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseAdditivityForLogger(Properties props, Logger cat, String loggerName)
/*     */   {
/* 543 */     String value = OptionConverter.findAndSubst("log4j.additivity." + loggerName, props);
/*     */ 
/* 545 */     LogLog.debug("Handling log4j.additivity." + loggerName + "=[" + value + "]");
/*     */ 
/* 547 */     if ((value != null) && (!value.equals(""))) {
/* 548 */       boolean additivity = OptionConverter.toBoolean(value, true);
/* 549 */       LogLog.debug("Setting additivity for \"" + loggerName + "\" to " + additivity);
/*     */ 
/* 551 */       cat.setAdditivity(additivity);
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value)
/*     */   {
/* 561 */     LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
/*     */ 
/* 563 */     StringTokenizer st = new StringTokenizer(value, ",");
/*     */ 
/* 568 */     if ((!value.startsWith(",")) && (!value.equals("")))
/*     */     {
/* 571 */       if (!st.hasMoreTokens()) {
/* 572 */         return;
/*     */       }
/* 574 */       String levelStr = st.nextToken();
/* 575 */       LogLog.debug("Level token is [" + levelStr + "].");
/*     */ 
/* 580 */       if (("inherited".equalsIgnoreCase(levelStr)) || ("null".equalsIgnoreCase(levelStr)))
/*     */       {
/* 582 */         if (loggerName.equals("root"))
/* 583 */           LogLog.warn("The root logger cannot be set to null.");
/*     */         else
/* 585 */           logger.setLevel(null);
/*     */       }
/*     */       else {
/* 588 */         logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
/*     */       }
/* 590 */       LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
/*     */     }
/*     */ 
/* 594 */     logger.removeAllAppenders();
/*     */ 
/* 598 */     while (st.hasMoreTokens()) {
/* 599 */       String appenderName = st.nextToken().trim();
/* 600 */       if ((appenderName == null) || (appenderName.equals(",")))
/*     */         continue;
/* 602 */       LogLog.debug("Parsing appender named \"" + appenderName + "\".");
/* 603 */       Appender appender = parseAppender(props, appenderName);
/* 604 */       if (appender != null)
/* 605 */         logger.addAppender(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   Appender parseAppender(Properties props, String appenderName)
/*     */   {
/* 611 */     Appender appender = registryGet(appenderName);
/* 612 */     if (appender != null) {
/* 613 */       LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
/* 614 */       return appender;
/*     */     }
/*     */ 
/* 617 */     String prefix = "log4j.appender." + appenderName;
/* 618 */     String layoutPrefix = prefix + ".layout";
/*     */ 
/* 620 */     appender = (Appender)OptionConverter.instantiateByKey(props, prefix, Appender.class, null);
/*     */ 
/* 623 */     if (appender == null) {
/* 624 */       LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
/*     */ 
/* 626 */       return null;
/*     */     }
/* 628 */     appender.setName(appenderName);
/*     */ 
/* 630 */     if ((appender instanceof OptionHandler)) {
/* 631 */       if (appender.requiresLayout()) {
/* 632 */         Layout layout = (Layout)OptionConverter.instantiateByKey(props, layoutPrefix, Layout.class, null);
/*     */ 
/* 636 */         if (layout != null) {
/* 637 */           appender.setLayout(layout);
/* 638 */           LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
/*     */ 
/* 640 */           PropertySetter.setProperties(layout, props, layoutPrefix + ".");
/* 641 */           LogLog.debug("End of parsing for \"" + appenderName + "\".");
/*     */         }
/*     */       }
/*     */ 
/* 645 */       PropertySetter.setProperties(appender, props, prefix + ".");
/* 646 */       LogLog.debug("Parsed \"" + appenderName + "\" options.");
/*     */     }
/* 648 */     registryPut(appender);
/* 649 */     return appender;
/*     */   }
/*     */ 
/*     */   void registryPut(Appender appender)
/*     */   {
/* 654 */     this.registry.put(appender.getName(), appender);
/*     */   }
/*     */ 
/*     */   Appender registryGet(String name) {
/* 658 */     return (Appender)this.registry.get(name);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.PropertyConfigurator
 * JD-Core Version:    0.6.0
 */
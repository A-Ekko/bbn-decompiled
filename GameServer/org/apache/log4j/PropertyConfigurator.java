/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
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
/*  89 */   protected Hashtable registry = new Hashtable(11);
/*  90 */   protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
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
/*     */   private static final String RESET_KEY = "log4j.reset";
/*     */   private static final String INTERNAL_ROOT_NAME = "root";
/*     */ 
/*     */   public void doConfigure(String configFileName, LoggerRepository hierarchy)
/*     */   {
/* 313 */     Properties props = new Properties();
/* 314 */     FileInputStream istream = null;
/*     */     try {
/* 316 */       istream = new FileInputStream(configFileName);
/* 317 */       props.load(istream);
/* 318 */       istream.close(); } catch (Exception e) { LogLog.error("Could not read configuration file [" + configFileName + "].", e);
/* 322 */       LogLog.error("Ignoring configuration file [" + configFileName + "].");
/*     */       return;
/*     */     } finally {
/* 325 */       if (istream != null) {
/*     */         try {
/* 327 */           istream.close();
/*     */         }
/*     */         catch (Throwable ignore)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 334 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   public static void configure(String configFilename)
/*     */   {
/* 342 */     new PropertyConfigurator().doConfigure(configFilename, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(URL configURL)
/*     */   {
/* 354 */     new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(Properties properties)
/*     */   {
/* 367 */     new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename)
/*     */   {
/* 382 */     configureAndWatch(configFilename, 60000L);
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename, long delay)
/*     */   {
/* 400 */     PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
/* 401 */     pdog.setDelay(delay);
/* 402 */     pdog.start();
/*     */   }
/*     */ 
/*     */   public void doConfigure(Properties properties, LoggerRepository hierarchy)
/*     */   {
/* 413 */     String value = properties.getProperty("log4j.debug");
/* 414 */     if (value == null) {
/* 415 */       value = properties.getProperty("log4j.configDebug");
/* 416 */       if (value != null) {
/* 417 */         LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
/*     */       }
/*     */     }
/* 420 */     if (value != null) {
/* 421 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
/*     */     }
/*     */ 
/* 427 */     String reset = properties.getProperty("log4j.reset");
/* 428 */     if ((reset != null) && (OptionConverter.toBoolean(reset, false))) {
/* 429 */       hierarchy.resetConfiguration();
/*     */     }
/*     */ 
/* 432 */     String thresholdStr = OptionConverter.findAndSubst("log4j.threshold", properties);
/*     */ 
/* 434 */     if (thresholdStr != null) {
/* 435 */       hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
/*     */ 
/* 437 */       LogLog.debug("Hierarchy threshold set to [" + hierarchy.getThreshold() + "].");
/*     */     }
/*     */ 
/* 440 */     configureRootCategory(properties, hierarchy);
/* 441 */     configureLoggerFactory(properties);
/* 442 */     parseCatsAndRenderers(properties, hierarchy);
/*     */ 
/* 444 */     LogLog.debug("Finished configuring.");
/*     */ 
/* 447 */     this.registry.clear();
/*     */   }
/*     */ 
/*     */   public void doConfigure(URL configURL, LoggerRepository hierarchy)
/*     */   {
/* 455 */     Properties props = new Properties();
/* 456 */     LogLog.debug("Reading configuration from URL " + configURL);
/* 457 */     InputStream istream = null;
/*     */     try {
/* 459 */       istream = configURL.openStream();
/* 460 */       props.load(istream);
/*     */     } catch (Exception e) { LogLog.error("Could not read configuration file from URL [" + configURL + "].", e);
/*     */ 
/* 465 */       LogLog.error("Ignoring configuration file [" + configURL + "].");
/*     */       return;
/*     */     } finally {
/* 469 */       if (istream != null)
/*     */         try {
/* 471 */           istream.close();
/*     */         }
/*     */         catch (Exception ignore) {
/*     */         }
/*     */     }
/* 476 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   protected void configureLoggerFactory(Properties props)
/*     */   {
/* 495 */     String factoryClassName = OptionConverter.findAndSubst("log4j.loggerFactory", props);
/*     */ 
/* 497 */     if (factoryClassName != null) {
/* 498 */       LogLog.debug("Setting category factory to [" + factoryClassName + "].");
/* 499 */       this.loggerFactory = ((LoggerFactory)OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, this.loggerFactory));
/*     */ 
/* 503 */       PropertySetter.setProperties(this.loggerFactory, props, "log4j.factory.");
/*     */     }
/*     */   }
/*     */ 
/*     */   void configureRootCategory(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 531 */     String effectiveFrefix = "log4j.rootLogger";
/* 532 */     String value = OptionConverter.findAndSubst("log4j.rootLogger", props);
/*     */ 
/* 534 */     if (value == null) {
/* 535 */       value = OptionConverter.findAndSubst("log4j.rootCategory", props);
/* 536 */       effectiveFrefix = "log4j.rootCategory";
/*     */     }
/*     */ 
/* 539 */     if (value == null) {
/* 540 */       LogLog.debug("Could not find root logger information. Is this OK?");
/*     */     } else {
/* 542 */       Logger root = hierarchy.getRootLogger();
/* 543 */       synchronized (root) {
/* 544 */         parseCategory(props, root, effectiveFrefix, "root", value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 555 */     Enumeration enumeration = props.propertyNames();
/* 556 */     while (enumeration.hasMoreElements()) {
/* 557 */       String key = (String)enumeration.nextElement();
/* 558 */       if ((key.startsWith("log4j.category.")) || (key.startsWith("log4j.logger."))) {
/* 559 */         String loggerName = null;
/* 560 */         if (key.startsWith("log4j.category."))
/* 561 */           loggerName = key.substring("log4j.category.".length());
/* 562 */         else if (key.startsWith("log4j.logger.")) {
/* 563 */           loggerName = key.substring("log4j.logger.".length());
/*     */         }
/* 565 */         String value = OptionConverter.findAndSubst(key, props);
/* 566 */         Logger logger = hierarchy.getLogger(loggerName, this.loggerFactory);
/* 567 */         synchronized (logger) {
/* 568 */           parseCategory(props, logger, key, loggerName, value);
/* 569 */           parseAdditivityForLogger(props, logger, loggerName);
/*     */         }
/* 571 */       } else if (key.startsWith("log4j.renderer.")) {
/* 572 */         String renderedClass = key.substring("log4j.renderer.".length());
/* 573 */         String renderingClass = OptionConverter.findAndSubst(key, props);
/* 574 */         if ((hierarchy instanceof RendererSupport))
/* 575 */           RendererMap.addRenderer((RendererSupport)hierarchy, renderedClass, renderingClass);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseAdditivityForLogger(Properties props, Logger cat, String loggerName)
/*     */   {
/* 587 */     String value = OptionConverter.findAndSubst("log4j.additivity." + loggerName, props);
/*     */ 
/* 589 */     LogLog.debug("Handling log4j.additivity." + loggerName + "=[" + value + "]");
/*     */ 
/* 591 */     if ((value != null) && (!value.equals(""))) {
/* 592 */       boolean additivity = OptionConverter.toBoolean(value, true);
/* 593 */       LogLog.debug("Setting additivity for \"" + loggerName + "\" to " + additivity);
/*     */ 
/* 595 */       cat.setAdditivity(additivity);
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value)
/*     */   {
/* 605 */     LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
/*     */ 
/* 607 */     StringTokenizer st = new StringTokenizer(value, ",");
/*     */ 
/* 612 */     if ((!value.startsWith(",")) && (!value.equals("")))
/*     */     {
/* 615 */       if (!st.hasMoreTokens()) {
/* 616 */         return;
/*     */       }
/* 618 */       String levelStr = st.nextToken();
/* 619 */       LogLog.debug("Level token is [" + levelStr + "].");
/*     */ 
/* 624 */       if (("inherited".equalsIgnoreCase(levelStr)) || ("null".equalsIgnoreCase(levelStr)))
/*     */       {
/* 626 */         if (loggerName.equals("root"))
/* 627 */           LogLog.warn("The root logger cannot be set to null.");
/*     */         else
/* 629 */           logger.setLevel(null);
/*     */       }
/*     */       else {
/* 632 */         logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
/*     */       }
/* 634 */       LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
/*     */     }
/*     */ 
/* 638 */     logger.removeAllAppenders();
/*     */ 
/* 642 */     while (st.hasMoreTokens()) {
/* 643 */       String appenderName = st.nextToken().trim();
/* 644 */       if ((appenderName == null) || (appenderName.equals(",")))
/*     */         continue;
/* 646 */       LogLog.debug("Parsing appender named \"" + appenderName + "\".");
/* 647 */       Appender appender = parseAppender(props, appenderName);
/* 648 */       if (appender != null)
/* 649 */         logger.addAppender(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   Appender parseAppender(Properties props, String appenderName)
/*     */   {
/* 655 */     Appender appender = registryGet(appenderName);
/* 656 */     if (appender != null) {
/* 657 */       LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
/* 658 */       return appender;
/*     */     }
/*     */ 
/* 661 */     String prefix = "log4j.appender." + appenderName;
/* 662 */     String layoutPrefix = prefix + ".layout";
/*     */ 
/* 664 */     appender = (Appender)OptionConverter.instantiateByKey(props, prefix, Appender.class, null);
/*     */ 
/* 667 */     if (appender == null) {
/* 668 */       LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
/*     */ 
/* 670 */       return null;
/*     */     }
/* 672 */     appender.setName(appenderName);
/*     */ 
/* 674 */     if ((appender instanceof OptionHandler)) {
/* 675 */       if (appender.requiresLayout()) {
/* 676 */         Layout layout = (Layout)OptionConverter.instantiateByKey(props, layoutPrefix, Layout.class, null);
/*     */ 
/* 680 */         if (layout != null) {
/* 681 */           appender.setLayout(layout);
/* 682 */           LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
/*     */ 
/* 684 */           PropertySetter.setProperties(layout, props, layoutPrefix + ".");
/* 685 */           LogLog.debug("End of parsing for \"" + appenderName + "\".");
/*     */         }
/*     */       }
/*     */ 
/* 689 */       PropertySetter.setProperties(appender, props, prefix + ".");
/* 690 */       LogLog.debug("Parsed \"" + appenderName + "\" options.");
/*     */     }
/* 692 */     registryPut(appender);
/* 693 */     return appender;
/*     */   }
/*     */ 
/*     */   void registryPut(Appender appender)
/*     */   {
/* 698 */     this.registry.put(appender.getName(), appender);
/*     */   }
/*     */ 
/*     */   Appender registryGet(String name) {
/* 702 */     return (Appender)this.registry.get(name);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.PropertyConfigurator
 * JD-Core Version:    0.6.0
 */
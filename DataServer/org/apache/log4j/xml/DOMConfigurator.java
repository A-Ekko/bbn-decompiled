/*     */ package org.apache.log4j.xml;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.FactoryConfigurationError;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.config.PropertySetter;
/*     */ import org.apache.log4j.helpers.FileWatchdog;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.AppenderAttachable;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public class DOMConfigurator
/*     */   implements Configurator
/*     */ {
/*     */   static final String CONFIGURATION_TAG = "log4j:configuration";
/*     */   static final String OLD_CONFIGURATION_TAG = "configuration";
/*     */   static final String RENDERER_TAG = "renderer";
/*     */   static final String APPENDER_TAG = "appender";
/*     */   static final String APPENDER_REF_TAG = "appender-ref";
/*     */   static final String PARAM_TAG = "param";
/*     */   static final String LAYOUT_TAG = "layout";
/*     */   static final String CATEGORY = "category";
/*     */   static final String LOGGER = "logger";
/*     */   static final String LOGGER_REF = "logger-ref";
/*     */   static final String CATEGORY_FACTORY_TAG = "categoryFactory";
/*     */   static final String NAME_ATTR = "name";
/*     */   static final String CLASS_ATTR = "class";
/*     */   static final String VALUE_ATTR = "value";
/*     */   static final String ROOT_TAG = "root";
/*     */   static final String ROOT_REF = "root-ref";
/*     */   static final String LEVEL_TAG = "level";
/*     */   static final String PRIORITY_TAG = "priority";
/*     */   static final String FILTER_TAG = "filter";
/*     */   static final String ERROR_HANDLER_TAG = "errorHandler";
/*     */   static final String REF_ATTR = "ref";
/*     */   static final String ADDITIVITY_ATTR = "additivity";
/*     */   static final String THRESHOLD_ATTR = "threshold";
/*     */   static final String CONFIG_DEBUG_ATTR = "configDebug";
/*     */   static final String INTERNAL_DEBUG_ATTR = "debug";
/*     */   static final String RENDERING_CLASS_ATTR = "renderingClass";
/*     */   static final String RENDERED_CLASS_ATTR = "renderedClass";
/*     */   static final String EMPTY_STR = "";
/*  90 */   static final Class[] ONE_STRING_PARAM = { String.class };
/*     */   static final String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";
/*     */   Hashtable appenderBag;
/*     */   Properties props;
/*     */   LoggerRepository repository;
/*     */ 
/*     */   public DOMConfigurator()
/*     */   {
/* 106 */     this.appenderBag = new Hashtable();
/*     */   }
/*     */ 
/*     */   protected Appender findAppenderByName(Document doc, String appenderName)
/*     */   {
/* 114 */     Appender appender = (Appender)this.appenderBag.get(appenderName);
/*     */ 
/* 116 */     if (appender != null) {
/* 117 */       return appender;
/*     */     }
/*     */ 
/* 123 */     Element element = null;
/* 124 */     NodeList list = doc.getElementsByTagName("appender");
/* 125 */     for (int t = 0; t < list.getLength(); t++) {
/* 126 */       Node node = list.item(t);
/* 127 */       NamedNodeMap map = node.getAttributes();
/* 128 */       Node attrNode = map.getNamedItem("name");
/* 129 */       if (appenderName.equals(attrNode.getNodeValue())) {
/* 130 */         element = (Element)node;
/* 131 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 136 */     if (element == null) {
/* 137 */       LogLog.error("No appender named [" + appenderName + "] could be found.");
/* 138 */       return null;
/*     */     }
/* 140 */     appender = parseAppender(element);
/* 141 */     this.appenderBag.put(appenderName, appender);
/* 142 */     return appender;
/*     */   }
/*     */ 
/*     */   protected Appender findAppenderByReference(Element appenderRef)
/*     */   {
/* 151 */     String appenderName = subst(appenderRef.getAttribute("ref"));
/* 152 */     Document doc = appenderRef.getOwnerDocument();
/* 153 */     return findAppenderByName(doc, appenderName);
/*     */   }
/*     */ 
/*     */   protected Appender parseAppender(Element appenderElement)
/*     */   {
/* 161 */     String className = subst(appenderElement.getAttribute("class"));
/* 162 */     LogLog.debug("Class name: [" + className + ']');
/*     */     try {
/* 164 */       Object instance = Loader.loadClass(className).newInstance();
/* 165 */       Appender appender = (Appender)instance;
/* 166 */       PropertySetter propSetter = new PropertySetter(appender);
/*     */ 
/* 168 */       appender.setName(subst(appenderElement.getAttribute("name")));
/*     */ 
/* 170 */       NodeList children = appenderElement.getChildNodes();
/* 171 */       int length = children.getLength();
/*     */ 
/* 173 */       for (int loop = 0; loop < length; loop++) {
/* 174 */         Node currentNode = children.item(loop);
/*     */ 
/* 177 */         if (currentNode.getNodeType() == 1) {
/* 178 */           Element currentElement = (Element)currentNode;
/*     */ 
/* 181 */           if (currentElement.getTagName().equals("param")) {
/* 182 */             setParameter(currentElement, propSetter);
/*     */           }
/* 185 */           else if (currentElement.getTagName().equals("layout")) {
/* 186 */             appender.setLayout(parseLayout(currentElement));
/*     */           }
/* 189 */           else if (currentElement.getTagName().equals("filter")) {
/* 190 */             parseFilters(currentElement, appender);
/*     */           }
/* 192 */           else if (currentElement.getTagName().equals("errorHandler")) {
/* 193 */             parseErrorHandler(currentElement, appender);
/*     */           }
/* 195 */           else if (currentElement.getTagName().equals("appender-ref")) {
/* 196 */             String refName = subst(currentElement.getAttribute("ref"));
/* 197 */             if ((appender instanceof AppenderAttachable)) {
/* 198 */               AppenderAttachable aa = (AppenderAttachable)appender;
/* 199 */               LogLog.debug("Attaching appender named [" + refName + "] to appender named [" + appender.getName() + "].");
/*     */ 
/* 201 */               aa.addAppender(findAppenderByReference(currentElement));
/*     */             } else {
/* 203 */               LogLog.error("Requesting attachment of appender named [" + refName + "] to appender named [" + appender.getName() + "] which does not implement org.apache.log4j.spi.AppenderAttachable.");
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 210 */       propSetter.activate();
/* 211 */       return appender;
/*     */     }
/*     */     catch (Exception oops)
/*     */     {
/* 216 */       LogLog.error("Could not create an Appender. Reported error follows.", oops);
/*     */     }
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */   protected void parseErrorHandler(Element element, Appender appender)
/*     */   {
/* 227 */     ErrorHandler eh = (ErrorHandler)OptionConverter.instantiateByClassName(subst(element.getAttribute("class")), ErrorHandler.class, null);
/*     */ 
/* 232 */     if (eh != null) {
/* 233 */       eh.setAppender(appender);
/*     */ 
/* 235 */       PropertySetter propSetter = new PropertySetter(eh);
/* 236 */       NodeList children = element.getChildNodes();
/* 237 */       int length = children.getLength();
/*     */ 
/* 239 */       for (int loop = 0; loop < length; loop++) {
/* 240 */         Node currentNode = children.item(loop);
/* 241 */         if (currentNode.getNodeType() == 1) {
/* 242 */           Element currentElement = (Element)currentNode;
/* 243 */           String tagName = currentElement.getTagName();
/* 244 */           if (tagName.equals("param")) {
/* 245 */             setParameter(currentElement, propSetter);
/* 246 */           } else if (tagName.equals("appender-ref")) {
/* 247 */             eh.setBackupAppender(findAppenderByReference(currentElement));
/* 248 */           } else if (tagName.equals("logger-ref")) {
/* 249 */             String loggerName = currentElement.getAttribute("ref");
/* 250 */             Logger logger = this.repository.getLogger(loggerName);
/* 251 */             eh.setLogger(logger);
/* 252 */           } else if (tagName.equals("root-ref")) {
/* 253 */             Logger root = this.repository.getRootLogger();
/* 254 */             eh.setLogger(root);
/*     */           }
/*     */         }
/*     */       }
/* 258 */       propSetter.activate();
/* 259 */       appender.setErrorHandler(eh);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseFilters(Element element, Appender appender)
/*     */   {
/* 268 */     String clazz = subst(element.getAttribute("class"));
/* 269 */     Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
/*     */ 
/* 272 */     if (filter != null) {
/* 273 */       PropertySetter propSetter = new PropertySetter(filter);
/* 274 */       NodeList children = element.getChildNodes();
/* 275 */       int length = children.getLength();
/*     */ 
/* 277 */       for (int loop = 0; loop < length; loop++) {
/* 278 */         Node currentNode = children.item(loop);
/* 279 */         if (currentNode.getNodeType() == 1) {
/* 280 */           Element currentElement = (Element)currentNode;
/* 281 */           String tagName = currentElement.getTagName();
/* 282 */           if (tagName.equals("param")) {
/* 283 */             setParameter(currentElement, propSetter);
/*     */           }
/*     */         }
/*     */       }
/* 287 */       propSetter.activate();
/* 288 */       LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
/*     */ 
/* 290 */       appender.addFilter(filter);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseCategory(Element loggerElement)
/*     */   {
/* 300 */     String catName = subst(loggerElement.getAttribute("name"));
/*     */ 
/* 304 */     String className = subst(loggerElement.getAttribute("class"));
/*     */     Logger cat;
/* 307 */     if ("".equals(className)) {
/* 308 */       LogLog.debug("Retreiving an instance of org.apache.log4j.Logger.");
/* 309 */       cat = this.repository.getLogger(catName);
/*     */     }
/*     */     else {
/* 312 */       LogLog.debug("Desired logger sub-class: [" + className + ']');
/*     */       try {
/* 314 */         Class clazz = Loader.loadClass(className);
/* 315 */         Method getInstanceMethod = clazz.getMethod("getLogger", ONE_STRING_PARAM);
/*     */ 
/* 317 */         cat = (Logger)getInstanceMethod.invoke(null, new Object[] { catName });
/*     */       } catch (Exception clazz) {
/* 319 */         LogLog.error("Could not retrieve category [" + catName + "]. Reported error follows.", ???);
/*     */ 
/* 321 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 328 */     synchronized (cat) {
/* 329 */       boolean additivity = OptionConverter.toBoolean(subst(loggerElement.getAttribute("additivity")), true);
/*     */ 
/* 333 */       LogLog.debug("Setting [" + cat.getName() + "] additivity to [" + additivity + "].");
/* 334 */       cat.setAdditivity(additivity);
/* 335 */       parseChildrenOfLoggerElement(loggerElement, cat, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseCategoryFactory(Element factoryElement)
/*     */   {
/* 345 */     String className = subst(factoryElement.getAttribute("class"));
/*     */ 
/* 347 */     if ("".equals(className)) {
/* 348 */       LogLog.error("Category Factory tag class attribute not found.");
/* 349 */       LogLog.debug("No Category Factory configured.");
/*     */     }
/*     */     else {
/* 352 */       LogLog.debug("Desired category factory: [" + className + ']');
/* 353 */       Object catFactory = OptionConverter.instantiateByClassName(className, LoggerFactory.class, null);
/*     */ 
/* 356 */       PropertySetter propSetter = new PropertySetter(catFactory);
/*     */ 
/* 358 */       Element currentElement = null;
/* 359 */       Node currentNode = null;
/* 360 */       NodeList children = factoryElement.getChildNodes();
/* 361 */       int length = children.getLength();
/*     */ 
/* 363 */       for (int loop = 0; loop < length; loop++) {
/* 364 */         currentNode = children.item(loop);
/* 365 */         if (currentNode.getNodeType() == 1) {
/* 366 */           currentElement = (Element)currentNode;
/* 367 */           if (currentElement.getTagName().equals("param"))
/* 368 */             setParameter(currentElement, propSetter);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseRoot(Element rootElement)
/*     */   {
/* 381 */     Logger root = this.repository.getRootLogger();
/*     */ 
/* 383 */     synchronized (root) {
/* 384 */       parseChildrenOfLoggerElement(rootElement, root, true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseChildrenOfLoggerElement(Element catElement, Logger cat, boolean isRoot)
/*     */   {
/* 396 */     PropertySetter propSetter = new PropertySetter(cat);
/*     */ 
/* 400 */     cat.removeAllAppenders();
/*     */ 
/* 403 */     NodeList children = catElement.getChildNodes();
/* 404 */     int length = children.getLength();
/*     */ 
/* 406 */     for (int loop = 0; loop < length; loop++) {
/* 407 */       Node currentNode = children.item(loop);
/*     */ 
/* 409 */       if (currentNode.getNodeType() == 1) {
/* 410 */         Element currentElement = (Element)currentNode;
/* 411 */         String tagName = currentElement.getTagName();
/*     */ 
/* 413 */         if (tagName.equals("appender-ref")) {
/* 414 */           Element appenderRef = (Element)currentNode;
/* 415 */           Appender appender = findAppenderByReference(appenderRef);
/* 416 */           String refName = subst(appenderRef.getAttribute("ref"));
/* 417 */           if (appender != null) {
/* 418 */             LogLog.debug("Adding appender named [" + refName + "] to category [" + cat.getName() + "].");
/*     */           }
/*     */           else {
/* 421 */             LogLog.debug("Appender named [" + refName + "] not found.");
/*     */           }
/* 423 */           cat.addAppender(appender);
/*     */         }
/* 425 */         else if (tagName.equals("level")) {
/* 426 */           parseLevel(currentElement, cat, isRoot);
/* 427 */         } else if (tagName.equals("priority")) {
/* 428 */           parseLevel(currentElement, cat, isRoot);
/* 429 */         } else if (tagName.equals("param")) {
/* 430 */           setParameter(currentElement, propSetter);
/*     */         }
/*     */       }
/*     */     }
/* 434 */     propSetter.activate();
/*     */   }
/*     */ 
/*     */   protected Layout parseLayout(Element layout_element)
/*     */   {
/* 442 */     String className = subst(layout_element.getAttribute("class"));
/* 443 */     LogLog.debug("Parsing layout of class: \"" + className + "\"");
/*     */     try {
/* 445 */       Object instance = Loader.loadClass(className).newInstance();
/* 446 */       Layout layout = (Layout)instance;
/* 447 */       PropertySetter propSetter = new PropertySetter(layout);
/*     */ 
/* 449 */       NodeList params = layout_element.getChildNodes();
/* 450 */       int length = params.getLength();
/*     */ 
/* 452 */       for (int loop = 0; loop < length; loop++) {
/* 453 */         Node currentNode = params.item(loop);
/* 454 */         if (currentNode.getNodeType() == 1) {
/* 455 */           Element currentElement = (Element)currentNode;
/* 456 */           String tagName = currentElement.getTagName();
/* 457 */           if (tagName.equals("param")) {
/* 458 */             setParameter(currentElement, propSetter);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 463 */       propSetter.activate();
/* 464 */       return layout;
/*     */     }
/*     */     catch (Exception oops) {
/* 467 */       LogLog.error("Could not create the Layout. Reported error follows.", oops);
/*     */     }
/* 469 */     return null;
/*     */   }
/*     */ 
/*     */   protected void parseRenderer(Element element)
/*     */   {
/* 475 */     String renderingClass = subst(element.getAttribute("renderingClass"));
/* 476 */     String renderedClass = subst(element.getAttribute("renderedClass"));
/* 477 */     if ((this.repository instanceof RendererSupport))
/* 478 */       RendererMap.addRenderer((RendererSupport)this.repository, renderedClass, renderingClass);
/*     */   }
/*     */ 
/*     */   protected void parseLevel(Element element, Logger logger, boolean isRoot)
/*     */   {
/* 488 */     String catName = logger.getName();
/* 489 */     if (isRoot) {
/* 490 */       catName = "root";
/*     */     }
/*     */ 
/* 493 */     String priStr = subst(element.getAttribute("value"));
/* 494 */     LogLog.debug("Level value for " + catName + " is  [" + priStr + "].");
/*     */ 
/* 496 */     if (("inherited".equalsIgnoreCase(priStr)) || ("null".equalsIgnoreCase(priStr))) {
/* 497 */       if (isRoot)
/* 498 */         LogLog.error("Root level cannot be inherited. Ignoring directive.");
/*     */       else
/* 500 */         logger.setLevel(null);
/*     */     }
/*     */     else {
/* 503 */       String className = subst(element.getAttribute("class"));
/* 504 */       if ("".equals(className)) {
/* 505 */         logger.setLevel(OptionConverter.toLevel(priStr, Level.DEBUG));
/*     */       } else {
/* 507 */         LogLog.debug("Desired Level sub-class: [" + className + ']');
/*     */         try {
/* 509 */           Class clazz = Loader.loadClass(className);
/* 510 */           Method toLevelMethod = clazz.getMethod("toLevel", ONE_STRING_PARAM);
/*     */ 
/* 512 */           Level pri = (Level)toLevelMethod.invoke(null, new Object[] { priStr });
/*     */ 
/* 514 */           logger.setLevel(pri);
/*     */         } catch (Exception oops) {
/* 516 */           LogLog.error("Could not create level [" + priStr + "]. Reported error follows.", oops);
/*     */ 
/* 518 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 522 */     LogLog.debug(catName + " level set to " + logger.getLevel());
/*     */   }
/*     */ 
/*     */   protected void setParameter(Element elem, PropertySetter propSetter)
/*     */   {
/* 527 */     String name = subst(elem.getAttribute("name"));
/* 528 */     String value = elem.getAttribute("value");
/* 529 */     value = subst(OptionConverter.convertSpecialChars(value));
/* 530 */     propSetter.setProperty(name, value);
/*     */   }
/*     */ 
/*     */   public static void configure(Element element)
/*     */   {
/* 542 */     DOMConfigurator configurator = new DOMConfigurator();
/* 543 */     configurator.doConfigure(element, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename)
/*     */   {
/* 557 */     configureAndWatch(configFilename, 60000L);
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename, long delay)
/*     */   {
/* 574 */     XMLWatchdog xdog = new XMLWatchdog(configFilename);
/* 575 */     xdog.setDelay(delay);
/* 576 */     xdog.start();
/*     */   }
/*     */ 
/*     */   public void doConfigure(String filename, LoggerRepository repository)
/*     */   {
/* 581 */     FileInputStream fis = null;
/*     */     try {
/* 583 */       fis = new FileInputStream(filename);
/* 584 */       doConfigure(fis, repository);
/*     */     } catch (IOException e) {
/* 586 */       LogLog.error("Could not open [" + filename + "].", e);
/*     */     } finally {
/* 588 */       if (fis != null)
/*     */         try {
/* 590 */           fis.close();
/*     */         } catch (IOException e) {
/* 592 */           LogLog.error("Could not close [" + filename + "].", e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doConfigure(URL url, LoggerRepository repository)
/*     */   {
/*     */     try
/*     */     {
/* 602 */       doConfigure(url.openStream(), repository);
/*     */     } catch (IOException e) {
/* 604 */       LogLog.error("Could not open [" + url + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doConfigure(InputStream inputStream, LoggerRepository repository)
/*     */     throws FactoryConfigurationError
/*     */   {
/* 616 */     doConfigure(new InputSource(inputStream), repository);
/*     */   }
/*     */ 
/*     */   public void doConfigure(Reader reader, LoggerRepository repository)
/*     */     throws FactoryConfigurationError
/*     */   {
/* 627 */     doConfigure(new InputSource(reader), repository);
/*     */   }
/*     */ 
/*     */   protected void doConfigure(InputSource inputSource, LoggerRepository repository)
/*     */     throws FactoryConfigurationError
/*     */   {
/* 638 */     DocumentBuilderFactory dbf = null;
/* 639 */     this.repository = repository;
/*     */     try {
/* 641 */       LogLog.debug("System property is :" + OptionConverter.getSystemProperty("javax.xml.parsers.DocumentBuilderFactory", null));
/*     */ 
/* 644 */       dbf = DocumentBuilderFactory.newInstance();
/* 645 */       LogLog.debug("Standard DocumentBuilderFactory search succeded.");
/* 646 */       LogLog.debug("DocumentBuilderFactory is: " + dbf.getClass().getName());
/*     */     } catch (FactoryConfigurationError fce) {
/* 648 */       Exception e = fce.getException();
/* 649 */       LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e);
/* 650 */       throw fce;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 656 */       dbf.setValidating(true);
/*     */ 
/* 659 */       DocumentBuilder docBuilder = dbf.newDocumentBuilder();
/* 660 */       docBuilder.setErrorHandler(new SAXErrorHandler());
/*     */ 
/* 662 */       Class clazz = getClass();
/* 663 */       URL dtdURL = clazz.getResource("/org/apache/log4j/xml/log4j.dtd");
/* 664 */       if (dtdURL == null) {
/* 665 */         LogLog.error("Could not find [log4j.dtd]. Used [" + clazz.getClassLoader() + "] class loader in the search.");
/*     */       }
/*     */       else
/*     */       {
/* 669 */         LogLog.debug("URL to log4j.dtd is [" + dtdURL.toString() + "].");
/* 670 */         inputSource.setSystemId(dtdURL.toString());
/*     */       }
/* 672 */       Document doc = docBuilder.parse(inputSource);
/* 673 */       parse(doc.getDocumentElement());
/*     */     }
/*     */     catch (Exception e) {
/* 676 */       LogLog.error("Could not parse input source [" + inputSource + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void doConfigure(Element element, LoggerRepository repository)
/*     */   {
/* 684 */     this.repository = repository;
/* 685 */     parse(element);
/*     */   }
/*     */ 
/*     */   public static void configure(String filename)
/*     */     throws FactoryConfigurationError
/*     */   {
/* 694 */     new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(URL url)
/*     */     throws FactoryConfigurationError
/*     */   {
/* 704 */     new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   protected void parse(Element element)
/*     */   {
/* 716 */     String rootElementName = element.getTagName();
/*     */ 
/* 718 */     if (!rootElementName.equals("log4j:configuration")) {
/* 719 */       if (rootElementName.equals("configuration")) {
/* 720 */         LogLog.warn("The <configuration> element has been deprecated.");
/*     */ 
/* 722 */         LogLog.warn("Use the <log4j:configuration> element instead.");
/*     */       } else {
/* 724 */         LogLog.error("DOM element is - not a <log4j:configuration> element.");
/* 725 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 729 */     String debugAttrib = subst(element.getAttribute("debug"));
/*     */ 
/* 731 */     LogLog.debug("debug attribute= \"" + debugAttrib + "\".");
/*     */ 
/* 734 */     if ((!debugAttrib.equals("")) && (!debugAttrib.equals("null")))
/* 735 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(debugAttrib, true));
/*     */     else {
/* 737 */       LogLog.debug("Ignoring debug attribute.");
/*     */     }
/*     */ 
/* 741 */     String confDebug = subst(element.getAttribute("configDebug"));
/* 742 */     if ((!confDebug.equals("")) && (!confDebug.equals("null"))) {
/* 743 */       LogLog.warn("The \"configDebug\" attribute is deprecated.");
/* 744 */       LogLog.warn("Use the \"debug\" attribute instead.");
/* 745 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
/*     */     }
/*     */ 
/* 748 */     String thresholdStr = subst(element.getAttribute("threshold"));
/* 749 */     LogLog.debug("Threshold =\"" + thresholdStr + "\".");
/* 750 */     if ((!"".equals(thresholdStr)) && (!"null".equals(thresholdStr))) {
/* 751 */       this.repository.setThreshold(thresholdStr);
/*     */     }
/*     */ 
/* 763 */     String tagName = null;
/* 764 */     Element currentElement = null;
/* 765 */     Node currentNode = null;
/* 766 */     NodeList children = element.getChildNodes();
/* 767 */     int length = children.getLength();
/*     */ 
/* 769 */     for (int loop = 0; loop < length; loop++) {
/* 770 */       currentNode = children.item(loop);
/* 771 */       if (currentNode.getNodeType() == 1) {
/* 772 */         currentElement = (Element)currentNode;
/* 773 */         tagName = currentElement.getTagName();
/*     */ 
/* 775 */         if (tagName.equals("categoryFactory")) {
/* 776 */           parseCategoryFactory(currentElement);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 781 */     for (int loop = 0; loop < length; loop++) {
/* 782 */       currentNode = children.item(loop);
/* 783 */       if (currentNode.getNodeType() == 1) {
/* 784 */         currentElement = (Element)currentNode;
/* 785 */         tagName = currentElement.getTagName();
/*     */ 
/* 787 */         if ((tagName.equals("category")) || (tagName.equals("logger")))
/* 788 */           parseCategory(currentElement);
/* 789 */         else if (tagName.equals("root"))
/* 790 */           parseRoot(currentElement);
/* 791 */         else if (tagName.equals("renderer"))
/* 792 */           parseRenderer(currentElement);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String subst(String value)
/*     */   {
/*     */     try
/*     */     {
/* 802 */       return OptionConverter.substVars(value, this.props);
/*     */     } catch (IllegalArgumentException e) {
/* 804 */       LogLog.warn("Could not perform variable substitution.", e);
/* 805 */     }return value;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.xml.DOMConfigurator
 * JD-Core Version:    0.6.0
 */
/*      */ package org.apache.log4j.xml;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import javax.xml.parsers.FactoryConfigurationError;
/*      */ import org.apache.log4j.Appender;
/*      */ import org.apache.log4j.Category;
/*      */ import org.apache.log4j.Layout;
/*      */ import org.apache.log4j.Level;
/*      */ import org.apache.log4j.LogManager;
/*      */ import org.apache.log4j.Logger;
/*      */ import org.apache.log4j.config.PropertySetter;
/*      */ import org.apache.log4j.helpers.FileWatchdog;
/*      */ import org.apache.log4j.helpers.Loader;
/*      */ import org.apache.log4j.helpers.LogLog;
/*      */ import org.apache.log4j.helpers.OptionConverter;
/*      */ import org.apache.log4j.or.RendererMap;
/*      */ import org.apache.log4j.spi.AppenderAttachable;
/*      */ import org.apache.log4j.spi.Configurator;
/*      */ import org.apache.log4j.spi.ErrorHandler;
/*      */ import org.apache.log4j.spi.Filter;
/*      */ import org.apache.log4j.spi.LoggerFactory;
/*      */ import org.apache.log4j.spi.LoggerRepository;
/*      */ import org.apache.log4j.spi.RendererSupport;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NamedNodeMap;
/*      */ import org.w3c.dom.Node;
/*      */ import org.w3c.dom.NodeList;
/*      */ import org.xml.sax.InputSource;
/*      */ import org.xml.sax.SAXException;
/*      */ 
/*      */ public class DOMConfigurator
/*      */   implements Configurator
/*      */ {
/*      */   static final String CONFIGURATION_TAG = "log4j:configuration";
/*      */   static final String OLD_CONFIGURATION_TAG = "configuration";
/*      */   static final String RENDERER_TAG = "renderer";
/*      */   static final String APPENDER_TAG = "appender";
/*      */   static final String APPENDER_REF_TAG = "appender-ref";
/*      */   static final String PARAM_TAG = "param";
/*      */   static final String LAYOUT_TAG = "layout";
/*      */   static final String CATEGORY = "category";
/*      */   static final String LOGGER = "logger";
/*      */   static final String LOGGER_REF = "logger-ref";
/*      */   static final String CATEGORY_FACTORY_TAG = "categoryFactory";
/*      */   static final String LOGGER_FACTORY_TAG = "loggerFactory";
/*      */   static final String NAME_ATTR = "name";
/*      */   static final String CLASS_ATTR = "class";
/*      */   static final String VALUE_ATTR = "value";
/*      */   static final String ROOT_TAG = "root";
/*      */   static final String ROOT_REF = "root-ref";
/*      */   static final String LEVEL_TAG = "level";
/*      */   static final String PRIORITY_TAG = "priority";
/*      */   static final String FILTER_TAG = "filter";
/*      */   static final String ERROR_HANDLER_TAG = "errorHandler";
/*      */   static final String REF_ATTR = "ref";
/*      */   static final String ADDITIVITY_ATTR = "additivity";
/*      */   static final String THRESHOLD_ATTR = "threshold";
/*      */   static final String CONFIG_DEBUG_ATTR = "configDebug";
/*      */   static final String INTERNAL_DEBUG_ATTR = "debug";
/*      */   private static final String RESET_ATTR = "reset";
/*      */   static final String RENDERING_CLASS_ATTR = "renderingClass";
/*      */   static final String RENDERED_CLASS_ATTR = "renderedClass";
/*      */   static final String EMPTY_STR = "";
/*  119 */   static final Class[] ONE_STRING_PARAM = { String.class };
/*      */   static final String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";
/*      */   Hashtable appenderBag;
/*      */   Properties props;
/*      */   LoggerRepository repository;
/*  130 */   protected LoggerFactory catFactory = null;
/*      */ 
/*      */   public DOMConfigurator()
/*      */   {
/*  137 */     this.appenderBag = new Hashtable();
/*      */   }
/*      */ 
/*      */   protected Appender findAppenderByName(Document doc, String appenderName)
/*      */   {
/*  145 */     Appender appender = (Appender)this.appenderBag.get(appenderName);
/*      */ 
/*  147 */     if (appender != null) {
/*  148 */       return appender;
/*      */     }
/*      */ 
/*  154 */     Element element = null;
/*  155 */     NodeList list = doc.getElementsByTagName("appender");
/*  156 */     for (int t = 0; t < list.getLength(); t++) {
/*  157 */       Node node = list.item(t);
/*  158 */       NamedNodeMap map = node.getAttributes();
/*  159 */       Node attrNode = map.getNamedItem("name");
/*  160 */       if (appenderName.equals(attrNode.getNodeValue())) {
/*  161 */         element = (Element)node;
/*  162 */         break;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  167 */     if (element == null) {
/*  168 */       LogLog.error("No appender named [" + appenderName + "] could be found.");
/*  169 */       return null;
/*      */     }
/*  171 */     appender = parseAppender(element);
/*  172 */     this.appenderBag.put(appenderName, appender);
/*  173 */     return appender;
/*      */   }
/*      */ 
/*      */   protected Appender findAppenderByReference(Element appenderRef)
/*      */   {
/*  182 */     String appenderName = subst(appenderRef.getAttribute("ref"));
/*  183 */     Document doc = appenderRef.getOwnerDocument();
/*  184 */     return findAppenderByName(doc, appenderName);
/*      */   }
/*      */ 
/*      */   private static void parseUnrecognizedElement(Object instance, Element element, Properties props)
/*      */     throws Exception
/*      */   {
/*  200 */     boolean recognized = false;
/*  201 */     if ((instance instanceof UnrecognizedElementHandler)) {
/*  202 */       recognized = ((UnrecognizedElementHandler)instance).parseUnrecognizedElement(element, props);
/*      */     }
/*      */ 
/*  205 */     if (!recognized)
/*  206 */       LogLog.warn("Unrecognized element " + element.getNodeName());
/*      */   }
/*      */ 
/*      */   private static void quietParseUnrecognizedElement(Object instance, Element element, Properties props)
/*      */   {
/*      */     try
/*      */     {
/*  223 */       parseUnrecognizedElement(instance, element, props);
/*      */     } catch (Exception ex) {
/*  225 */       LogLog.error("Error in extension content: ", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Appender parseAppender(Element appenderElement)
/*      */   {
/*  234 */     String className = subst(appenderElement.getAttribute("class"));
/*  235 */     LogLog.debug("Class name: [" + className + ']');
/*      */     try {
/*  237 */       Object instance = Loader.loadClass(className).newInstance();
/*  238 */       Appender appender = (Appender)instance;
/*  239 */       PropertySetter propSetter = new PropertySetter(appender);
/*      */ 
/*  241 */       appender.setName(subst(appenderElement.getAttribute("name")));
/*      */ 
/*  243 */       NodeList children = appenderElement.getChildNodes();
/*  244 */       int length = children.getLength();
/*      */ 
/*  246 */       for (int loop = 0; loop < length; loop++) {
/*  247 */         Node currentNode = children.item(loop);
/*      */ 
/*  250 */         if (currentNode.getNodeType() == 1) {
/*  251 */           Element currentElement = (Element)currentNode;
/*      */ 
/*  254 */           if (currentElement.getTagName().equals("param")) {
/*  255 */             setParameter(currentElement, propSetter);
/*      */           }
/*  258 */           else if (currentElement.getTagName().equals("layout")) {
/*  259 */             appender.setLayout(parseLayout(currentElement));
/*      */           }
/*  262 */           else if (currentElement.getTagName().equals("filter")) {
/*  263 */             parseFilters(currentElement, appender);
/*      */           }
/*  265 */           else if (currentElement.getTagName().equals("errorHandler")) {
/*  266 */             parseErrorHandler(currentElement, appender);
/*      */           }
/*  268 */           else if (currentElement.getTagName().equals("appender-ref")) {
/*  269 */             String refName = subst(currentElement.getAttribute("ref"));
/*  270 */             if ((appender instanceof AppenderAttachable)) {
/*  271 */               AppenderAttachable aa = (AppenderAttachable)appender;
/*  272 */               LogLog.debug("Attaching appender named [" + refName + "] to appender named [" + appender.getName() + "].");
/*      */ 
/*  274 */               aa.addAppender(findAppenderByReference(currentElement));
/*      */             } else {
/*  276 */               LogLog.error("Requesting attachment of appender named [" + refName + "] to appender named [" + appender.getName() + "] which does not implement org.apache.log4j.spi.AppenderAttachable.");
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  281 */             parseUnrecognizedElement(instance, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  285 */       propSetter.activate();
/*  286 */       return appender;
/*      */     }
/*      */     catch (Exception oops)
/*      */     {
/*  291 */       LogLog.error("Could not create an Appender. Reported error follows.", oops);
/*      */     }
/*  293 */     return null;
/*      */   }
/*      */ 
/*      */   protected void parseErrorHandler(Element element, Appender appender)
/*      */   {
/*  302 */     ErrorHandler eh = (ErrorHandler)OptionConverter.instantiateByClassName(subst(element.getAttribute("class")), ErrorHandler.class, null);
/*      */ 
/*  307 */     if (eh != null) {
/*  308 */       eh.setAppender(appender);
/*      */ 
/*  310 */       PropertySetter propSetter = new PropertySetter(eh);
/*  311 */       NodeList children = element.getChildNodes();
/*  312 */       int length = children.getLength();
/*      */ 
/*  314 */       for (int loop = 0; loop < length; loop++) {
/*  315 */         Node currentNode = children.item(loop);
/*  316 */         if (currentNode.getNodeType() == 1) {
/*  317 */           Element currentElement = (Element)currentNode;
/*  318 */           String tagName = currentElement.getTagName();
/*  319 */           if (tagName.equals("param")) {
/*  320 */             setParameter(currentElement, propSetter);
/*  321 */           } else if (tagName.equals("appender-ref")) {
/*  322 */             eh.setBackupAppender(findAppenderByReference(currentElement));
/*  323 */           } else if (tagName.equals("logger-ref")) {
/*  324 */             String loggerName = currentElement.getAttribute("ref");
/*  325 */             Logger logger = this.catFactory == null ? this.repository.getLogger(loggerName) : this.repository.getLogger(loggerName, this.catFactory);
/*      */ 
/*  327 */             eh.setLogger(logger);
/*  328 */           } else if (tagName.equals("root-ref")) {
/*  329 */             Logger root = this.repository.getRootLogger();
/*  330 */             eh.setLogger(root);
/*      */           } else {
/*  332 */             quietParseUnrecognizedElement(eh, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  336 */       propSetter.activate();
/*  337 */       appender.setErrorHandler(eh);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseFilters(Element element, Appender appender)
/*      */   {
/*  346 */     String clazz = subst(element.getAttribute("class"));
/*  347 */     Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
/*      */ 
/*  350 */     if (filter != null) {
/*  351 */       PropertySetter propSetter = new PropertySetter(filter);
/*  352 */       NodeList children = element.getChildNodes();
/*  353 */       int length = children.getLength();
/*      */ 
/*  355 */       for (int loop = 0; loop < length; loop++) {
/*  356 */         Node currentNode = children.item(loop);
/*  357 */         if (currentNode.getNodeType() == 1) {
/*  358 */           Element currentElement = (Element)currentNode;
/*  359 */           String tagName = currentElement.getTagName();
/*  360 */           if (tagName.equals("param"))
/*  361 */             setParameter(currentElement, propSetter);
/*      */           else {
/*  363 */             quietParseUnrecognizedElement(filter, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*  367 */       propSetter.activate();
/*  368 */       LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
/*      */ 
/*  370 */       appender.addFilter(filter);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseCategory(Element loggerElement)
/*      */   {
/*  380 */     String catName = subst(loggerElement.getAttribute("name"));
/*      */ 
/*  384 */     String className = subst(loggerElement.getAttribute("class"));
/*      */     Logger cat;
/*      */     Logger cat;
/*  387 */     if ("".equals(className)) {
/*  388 */       LogLog.debug("Retreiving an instance of org.apache.log4j.Logger.");
/*  389 */       cat = this.catFactory == null ? this.repository.getLogger(catName) : this.repository.getLogger(catName, this.catFactory);
/*      */     }
/*      */     else {
/*  392 */       LogLog.debug("Desired logger sub-class: [" + className + ']');
/*      */       try {
/*  394 */         Class clazz = Loader.loadClass(className);
/*  395 */         Method getInstanceMethod = clazz.getMethod("getLogger", ONE_STRING_PARAM);
/*      */ 
/*  397 */         cat = (Logger)getInstanceMethod.invoke(null, new Object[] { catName });
/*      */       } catch (Exception oops) {
/*  399 */         LogLog.error("Could not retrieve category [" + catName + "]. Reported error follows.", oops);
/*      */ 
/*  401 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  408 */     synchronized (cat) {
/*  409 */       boolean additivity = OptionConverter.toBoolean(subst(loggerElement.getAttribute("additivity")), true);
/*      */ 
/*  413 */       LogLog.debug("Setting [" + cat.getName() + "] additivity to [" + additivity + "].");
/*  414 */       cat.setAdditivity(additivity);
/*  415 */       parseChildrenOfLoggerElement(loggerElement, cat, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseCategoryFactory(Element factoryElement)
/*      */   {
/*  425 */     String className = subst(factoryElement.getAttribute("class"));
/*      */ 
/*  427 */     if ("".equals(className)) {
/*  428 */       LogLog.error("Category Factory tag class attribute not found.");
/*  429 */       LogLog.debug("No Category Factory configured.");
/*      */     }
/*      */     else {
/*  432 */       LogLog.debug("Desired category factory: [" + className + ']');
/*  433 */       Object factory = OptionConverter.instantiateByClassName(className, LoggerFactory.class, null);
/*      */ 
/*  436 */       if ((factory instanceof LoggerFactory))
/*  437 */         this.catFactory = ((LoggerFactory)factory);
/*      */       else {
/*  439 */         LogLog.error("Category Factory class " + className + " does not implement org.apache.log4j.LoggerFactory");
/*      */       }
/*  441 */       PropertySetter propSetter = new PropertySetter(factory);
/*      */ 
/*  443 */       Element currentElement = null;
/*  444 */       Node currentNode = null;
/*  445 */       NodeList children = factoryElement.getChildNodes();
/*  446 */       int length = children.getLength();
/*      */ 
/*  448 */       for (int loop = 0; loop < length; loop++) {
/*  449 */         currentNode = children.item(loop);
/*  450 */         if (currentNode.getNodeType() == 1) {
/*  451 */           currentElement = (Element)currentNode;
/*  452 */           if (currentElement.getTagName().equals("param"))
/*  453 */             setParameter(currentElement, propSetter);
/*      */           else
/*  455 */             quietParseUnrecognizedElement(factory, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseRoot(Element rootElement)
/*      */   {
/*  468 */     Logger root = this.repository.getRootLogger();
/*      */ 
/*  470 */     synchronized (root) {
/*  471 */       parseChildrenOfLoggerElement(rootElement, root, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void parseChildrenOfLoggerElement(Element catElement, Logger cat, boolean isRoot)
/*      */   {
/*  483 */     PropertySetter propSetter = new PropertySetter(cat);
/*      */ 
/*  487 */     cat.removeAllAppenders();
/*      */ 
/*  490 */     NodeList children = catElement.getChildNodes();
/*  491 */     int length = children.getLength();
/*      */ 
/*  493 */     for (int loop = 0; loop < length; loop++) {
/*  494 */       Node currentNode = children.item(loop);
/*      */ 
/*  496 */       if (currentNode.getNodeType() == 1) {
/*  497 */         Element currentElement = (Element)currentNode;
/*  498 */         String tagName = currentElement.getTagName();
/*      */ 
/*  500 */         if (tagName.equals("appender-ref")) {
/*  501 */           Element appenderRef = (Element)currentNode;
/*  502 */           Appender appender = findAppenderByReference(appenderRef);
/*  503 */           String refName = subst(appenderRef.getAttribute("ref"));
/*  504 */           if (appender != null) {
/*  505 */             LogLog.debug("Adding appender named [" + refName + "] to category [" + cat.getName() + "].");
/*      */           }
/*      */           else {
/*  508 */             LogLog.debug("Appender named [" + refName + "] not found.");
/*      */           }
/*  510 */           cat.addAppender(appender);
/*      */         }
/*  512 */         else if (tagName.equals("level")) {
/*  513 */           parseLevel(currentElement, cat, isRoot);
/*  514 */         } else if (tagName.equals("priority")) {
/*  515 */           parseLevel(currentElement, cat, isRoot);
/*  516 */         } else if (tagName.equals("param")) {
/*  517 */           setParameter(currentElement, propSetter);
/*      */         } else {
/*  519 */           quietParseUnrecognizedElement(cat, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*  523 */     propSetter.activate();
/*      */   }
/*      */ 
/*      */   protected Layout parseLayout(Element layout_element)
/*      */   {
/*  531 */     String className = subst(layout_element.getAttribute("class"));
/*  532 */     LogLog.debug("Parsing layout of class: \"" + className + "\"");
/*      */     try {
/*  534 */       Object instance = Loader.loadClass(className).newInstance();
/*  535 */       Layout layout = (Layout)instance;
/*  536 */       PropertySetter propSetter = new PropertySetter(layout);
/*      */ 
/*  538 */       NodeList params = layout_element.getChildNodes();
/*  539 */       int length = params.getLength();
/*      */ 
/*  541 */       for (int loop = 0; loop < length; loop++) {
/*  542 */         Node currentNode = params.item(loop);
/*  543 */         if (currentNode.getNodeType() == 1) {
/*  544 */           Element currentElement = (Element)currentNode;
/*  545 */           String tagName = currentElement.getTagName();
/*  546 */           if (tagName.equals("param"))
/*  547 */             setParameter(currentElement, propSetter);
/*      */           else {
/*  549 */             parseUnrecognizedElement(instance, currentElement, this.props);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  554 */       propSetter.activate();
/*  555 */       return layout;
/*      */     }
/*      */     catch (Exception oops) {
/*  558 */       LogLog.error("Could not create the Layout. Reported error follows.", oops);
/*      */     }
/*  560 */     return null;
/*      */   }
/*      */ 
/*      */   protected void parseRenderer(Element element)
/*      */   {
/*  566 */     String renderingClass = subst(element.getAttribute("renderingClass"));
/*  567 */     String renderedClass = subst(element.getAttribute("renderedClass"));
/*  568 */     if ((this.repository instanceof RendererSupport))
/*  569 */       RendererMap.addRenderer((RendererSupport)this.repository, renderedClass, renderingClass);
/*      */   }
/*      */ 
/*      */   protected void parseLevel(Element element, Logger logger, boolean isRoot)
/*      */   {
/*  579 */     String catName = logger.getName();
/*  580 */     if (isRoot) {
/*  581 */       catName = "root";
/*      */     }
/*      */ 
/*  584 */     String priStr = subst(element.getAttribute("value"));
/*  585 */     LogLog.debug("Level value for " + catName + " is  [" + priStr + "].");
/*      */ 
/*  587 */     if (("inherited".equalsIgnoreCase(priStr)) || ("null".equalsIgnoreCase(priStr))) {
/*  588 */       if (isRoot)
/*  589 */         LogLog.error("Root level cannot be inherited. Ignoring directive.");
/*      */       else
/*  591 */         logger.setLevel(null);
/*      */     }
/*      */     else {
/*  594 */       String className = subst(element.getAttribute("class"));
/*  595 */       if ("".equals(className)) {
/*  596 */         logger.setLevel(OptionConverter.toLevel(priStr, Level.DEBUG));
/*      */       } else {
/*  598 */         LogLog.debug("Desired Level sub-class: [" + className + ']');
/*      */         try {
/*  600 */           Class clazz = Loader.loadClass(className);
/*  601 */           Method toLevelMethod = clazz.getMethod("toLevel", ONE_STRING_PARAM);
/*      */ 
/*  603 */           Level pri = (Level)toLevelMethod.invoke(null, new Object[] { priStr });
/*      */ 
/*  605 */           logger.setLevel(pri);
/*      */         } catch (Exception oops) {
/*  607 */           LogLog.error("Could not create level [" + priStr + "]. Reported error follows.", oops);
/*      */ 
/*  609 */           return;
/*      */         }
/*      */       }
/*      */     }
/*  613 */     LogLog.debug(catName + " level set to " + logger.getLevel());
/*      */   }
/*      */ 
/*      */   protected void setParameter(Element elem, PropertySetter propSetter)
/*      */   {
/*  618 */     setParameter(elem, propSetter, this.props);
/*      */   }
/*      */ 
/*      */   public static void configure(Element element)
/*      */   {
/*  630 */     DOMConfigurator configurator = new DOMConfigurator();
/*  631 */     configurator.doConfigure(element, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   public static void configureAndWatch(String configFilename)
/*      */   {
/*  645 */     configureAndWatch(configFilename, 60000L);
/*      */   }
/*      */ 
/*      */   public static void configureAndWatch(String configFilename, long delay)
/*      */   {
/*  662 */     XMLWatchdog xdog = new XMLWatchdog(configFilename);
/*  663 */     xdog.setDelay(delay);
/*  664 */     xdog.start();
/*      */   }
/*      */ 
/*      */   public void doConfigure(String filename, LoggerRepository repository)
/*      */   {
/*  674 */     ParseAction action = new ParseAction(filename) { private final String val$filename;
/*      */ 
/*  676 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { return parser.parse(new File(this.val$filename)); }
/*      */ 
/*      */       public String toString() {
/*  679 */         return "file [" + this.val$filename + "]";
/*      */       }
/*      */     };
/*  682 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(URL url, LoggerRepository repository)
/*      */   {
/*  688 */     ParseAction action = new ParseAction(url) { private final URL val$url;
/*      */ 
/*  690 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { return parser.parse(this.val$url.toString()); }
/*      */ 
/*      */       public String toString() {
/*  693 */         return "url [" + this.val$url.toString() + "]";
/*      */       }
/*      */     };
/*  696 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(InputStream inputStream, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  707 */     ParseAction action = new ParseAction(inputStream) { private final InputStream val$inputStream;
/*      */ 
/*  709 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { InputSource inputSource = new InputSource(this.val$inputStream);
/*  710 */         inputSource.setSystemId("dummy://log4j.dtd");
/*  711 */         return parser.parse(inputSource); }
/*      */ 
/*      */       public String toString() {
/*  714 */         return "input stream [" + this.val$inputStream.toString() + "]";
/*      */       }
/*      */     };
/*  717 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   public void doConfigure(Reader reader, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  728 */     ParseAction action = new ParseAction(reader) { private final Reader val$reader;
/*      */ 
/*  730 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { InputSource inputSource = new InputSource(this.val$reader);
/*  731 */         inputSource.setSystemId("dummy://log4j.dtd");
/*  732 */         return parser.parse(inputSource); }
/*      */ 
/*      */       public String toString() {
/*  735 */         return "reader [" + this.val$reader.toString() + "]";
/*      */       }
/*      */     };
/*  738 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   protected void doConfigure(InputSource inputSource, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  749 */     if (inputSource.getSystemId() == null) {
/*  750 */       inputSource.setSystemId("dummy://log4j.dtd");
/*      */     }
/*  752 */     ParseAction action = new ParseAction(inputSource) { private final InputSource val$inputSource;
/*      */ 
/*  754 */       public Document parse(DocumentBuilder parser) throws SAXException, IOException { return parser.parse(this.val$inputSource); }
/*      */ 
/*      */       public String toString() {
/*  757 */         return "input source [" + this.val$inputSource.toString() + "]";
/*      */       }
/*      */     };
/*  760 */     doConfigure(action, repository);
/*      */   }
/*      */ 
/*      */   private final void doConfigure(ParseAction action, LoggerRepository repository)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  766 */     DocumentBuilderFactory dbf = null;
/*  767 */     this.repository = repository;
/*      */     try {
/*  769 */       LogLog.debug("System property is :" + OptionConverter.getSystemProperty("javax.xml.parsers.DocumentBuilderFactory", null));
/*      */ 
/*  772 */       dbf = DocumentBuilderFactory.newInstance();
/*  773 */       LogLog.debug("Standard DocumentBuilderFactory search succeded.");
/*  774 */       LogLog.debug("DocumentBuilderFactory is: " + dbf.getClass().getName());
/*      */     } catch (FactoryConfigurationError fce) {
/*  776 */       Exception e = fce.getException();
/*  777 */       LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e);
/*  778 */       throw fce;
/*      */     }
/*      */     try
/*      */     {
/*  782 */       dbf.setValidating(true);
/*      */ 
/*  784 */       DocumentBuilder docBuilder = dbf.newDocumentBuilder();
/*      */ 
/*  786 */       docBuilder.setErrorHandler(new SAXErrorHandler());
/*  787 */       docBuilder.setEntityResolver(new Log4jEntityResolver());
/*      */ 
/*  789 */       Document doc = action.parse(docBuilder);
/*  790 */       parse(doc.getDocumentElement());
/*      */     }
/*      */     catch (Exception e) {
/*  793 */       LogLog.error("Could not parse " + action.toString() + ".", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void doConfigure(Element element, LoggerRepository repository)
/*      */   {
/*  801 */     this.repository = repository;
/*  802 */     parse(element);
/*      */   }
/*      */ 
/*      */   public static void configure(String filename)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  811 */     new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   public static void configure(URL url)
/*      */     throws FactoryConfigurationError
/*      */   {
/*  821 */     new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
/*      */   }
/*      */ 
/*      */   protected void parse(Element element)
/*      */   {
/*  833 */     String rootElementName = element.getTagName();
/*      */ 
/*  835 */     if (!rootElementName.equals("log4j:configuration")) {
/*  836 */       if (rootElementName.equals("configuration")) {
/*  837 */         LogLog.warn("The <configuration> element has been deprecated.");
/*      */ 
/*  839 */         LogLog.warn("Use the <log4j:configuration> element instead.");
/*      */       } else {
/*  841 */         LogLog.error("DOM element is - not a <log4j:configuration> element.");
/*  842 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  847 */     String debugAttrib = subst(element.getAttribute("debug"));
/*      */ 
/*  849 */     LogLog.debug("debug attribute= \"" + debugAttrib + "\".");
/*      */ 
/*  852 */     if ((!debugAttrib.equals("")) && (!debugAttrib.equals("null")))
/*  853 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(debugAttrib, true));
/*      */     else {
/*  855 */       LogLog.debug("Ignoring debug attribute.");
/*      */     }
/*      */ 
/*  862 */     String resetAttrib = subst(element.getAttribute("reset"));
/*  863 */     LogLog.debug("reset attribute= \"" + resetAttrib + "\".");
/*  864 */     if ((!"".equals(resetAttrib)) && 
/*  865 */       (OptionConverter.toBoolean(resetAttrib, false))) {
/*  866 */       this.repository.resetConfiguration();
/*      */     }
/*      */ 
/*  872 */     String confDebug = subst(element.getAttribute("configDebug"));
/*  873 */     if ((!confDebug.equals("")) && (!confDebug.equals("null"))) {
/*  874 */       LogLog.warn("The \"configDebug\" attribute is deprecated.");
/*  875 */       LogLog.warn("Use the \"debug\" attribute instead.");
/*  876 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
/*      */     }
/*      */ 
/*  879 */     String thresholdStr = subst(element.getAttribute("threshold"));
/*  880 */     LogLog.debug("Threshold =\"" + thresholdStr + "\".");
/*  881 */     if ((!"".equals(thresholdStr)) && (!"null".equals(thresholdStr))) {
/*  882 */       this.repository.setThreshold(thresholdStr);
/*      */     }
/*      */ 
/*  894 */     String tagName = null;
/*  895 */     Element currentElement = null;
/*  896 */     Node currentNode = null;
/*  897 */     NodeList children = element.getChildNodes();
/*  898 */     int length = children.getLength();
/*      */ 
/*  900 */     for (int loop = 0; loop < length; loop++) {
/*  901 */       currentNode = children.item(loop);
/*  902 */       if (currentNode.getNodeType() == 1) {
/*  903 */         currentElement = (Element)currentNode;
/*  904 */         tagName = currentElement.getTagName();
/*      */ 
/*  906 */         if ((tagName.equals("categoryFactory")) || (tagName.equals("loggerFactory"))) {
/*  907 */           parseCategoryFactory(currentElement);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  912 */     for (int loop = 0; loop < length; loop++) {
/*  913 */       currentNode = children.item(loop);
/*  914 */       if (currentNode.getNodeType() == 1) {
/*  915 */         currentElement = (Element)currentNode;
/*  916 */         tagName = currentElement.getTagName();
/*      */ 
/*  918 */         if ((tagName.equals("category")) || (tagName.equals("logger"))) {
/*  919 */           parseCategory(currentElement);
/*  920 */         } else if (tagName.equals("root")) {
/*  921 */           parseRoot(currentElement);
/*  922 */         } else if (tagName.equals("renderer")) {
/*  923 */           parseRenderer(currentElement); } else {
/*  924 */           if ((tagName.equals("appender")) || (tagName.equals("categoryFactory")) || (tagName.equals("loggerFactory"))) {
/*      */             continue;
/*      */           }
/*  927 */           quietParseUnrecognizedElement(this.repository, currentElement, this.props);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String subst(String value)
/*      */   {
/*  936 */     return subst(value, this.props);
/*      */   }
/*      */ 
/*      */   public static String subst(String value, Properties props)
/*      */   {
/*      */     try
/*      */     {
/*  951 */       return OptionConverter.substVars(value, props);
/*      */     } catch (IllegalArgumentException e) {
/*  953 */       LogLog.warn("Could not perform variable substitution.", e);
/*  954 */     }return value;
/*      */   }
/*      */ 
/*      */   public static void setParameter(Element elem, PropertySetter propSetter, Properties props)
/*      */   {
/*  970 */     String name = subst(elem.getAttribute("name"), props);
/*  971 */     String value = elem.getAttribute("value");
/*  972 */     value = subst(OptionConverter.convertSpecialChars(value), props);
/*  973 */     propSetter.setProperty(name, value);
/*      */   }
/*      */ 
/*      */   public static Object parseElement(Element element, Properties props, Class expectedClass)
/*      */     throws Exception
/*      */   {
/*  993 */     String clazz = subst(element.getAttribute("class"), props);
/*  994 */     Object instance = OptionConverter.instantiateByClassName(clazz, expectedClass, null);
/*      */ 
/*  997 */     if (instance != null) {
/*  998 */       PropertySetter propSetter = new PropertySetter(instance);
/*  999 */       NodeList children = element.getChildNodes();
/* 1000 */       int length = children.getLength();
/*      */ 
/* 1002 */       for (int loop = 0; loop < length; loop++) {
/* 1003 */         Node currentNode = children.item(loop);
/* 1004 */         if (currentNode.getNodeType() == 1) {
/* 1005 */           Element currentElement = (Element)currentNode;
/* 1006 */           String tagName = currentElement.getTagName();
/* 1007 */           if (tagName.equals("param"))
/* 1008 */             setParameter(currentElement, propSetter, props);
/*      */           else {
/* 1010 */             parseUnrecognizedElement(instance, currentElement, props);
/*      */           }
/*      */         }
/*      */       }
/* 1014 */       return instance;
/*      */     }
/* 1016 */     return null;
/*      */   }
/*      */ 
/*      */   private static abstract interface ParseAction
/*      */   {
/*      */     public abstract Document parse(DocumentBuilder paramDocumentBuilder)
/*      */       throws SAXException, IOException;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.xml.DOMConfigurator
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.or.ObjectRenderer;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.HierarchyEventListener;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ 
/*     */ public class Hierarchy
/*     */   implements LoggerRepository, RendererSupport
/*     */ {
/*     */   private LoggerFactory defaultFactory;
/*     */   private Vector listeners;
/*     */   Hashtable ht;
/*     */   Logger root;
/*     */   RendererMap rendererMap;
/*     */   int thresholdInt;
/*     */   Level threshold;
/*  67 */   boolean emittedNoAppenderWarning = false;
/*  68 */   boolean emittedNoResourceBundleWarning = false;
/*     */ 
/*     */   public Hierarchy(Logger root)
/*     */   {
/*  78 */     this.ht = new Hashtable();
/*  79 */     this.listeners = new Vector(1);
/*  80 */     this.root = root;
/*     */ 
/*  82 */     setThreshold(Level.ALL);
/*  83 */     this.root.setHierarchy(this);
/*  84 */     this.rendererMap = new RendererMap();
/*  85 */     this.defaultFactory = new DefaultCategoryFactory();
/*     */   }
/*     */ 
/*     */   public void addRenderer(Class classToRender, ObjectRenderer or)
/*     */   {
/*  93 */     this.rendererMap.put(classToRender, or);
/*     */   }
/*     */ 
/*     */   public void addHierarchyEventListener(HierarchyEventListener listener)
/*     */   {
/*  98 */     if (this.listeners.contains(listener))
/*  99 */       LogLog.warn("Ignoring attempt to add an existent listener.");
/*     */     else
/* 101 */       this.listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 117 */     this.ht.clear();
/*     */   }
/*     */ 
/*     */   public void emitNoAppenderWarning(Category cat)
/*     */   {
/* 123 */     if (!this.emittedNoAppenderWarning) {
/* 124 */       LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
/*     */ 
/* 126 */       LogLog.warn("Please initialize the log4j system properly.");
/* 127 */       this.emittedNoAppenderWarning = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Logger exists(String name)
/*     */   {
/* 140 */     Object o = this.ht.get(new CategoryKey(name));
/* 141 */     if ((o instanceof Logger)) {
/* 142 */       return (Logger)o;
/*     */     }
/* 144 */     return null;
/*     */   }
/*     */ 
/*     */   public void setThreshold(String levelStr)
/*     */   {
/* 153 */     Level l = Level.toLevel(levelStr, null);
/* 154 */     if (l != null)
/* 155 */       setThreshold(l);
/*     */     else
/* 157 */       LogLog.warn("Could not convert [" + levelStr + "] to Level.");
/*     */   }
/*     */ 
/*     */   public void setThreshold(Level l)
/*     */   {
/* 170 */     if (l != null) {
/* 171 */       this.thresholdInt = l.level;
/* 172 */       this.threshold = l;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireAddAppenderEvent(Category logger, Appender appender)
/*     */   {
/* 178 */     if (this.listeners != null) {
/* 179 */       int size = this.listeners.size();
/*     */ 
/* 181 */       for (int i = 0; i < size; i++) {
/* 182 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 183 */         listener.addAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void fireRemoveAppenderEvent(Category logger, Appender appender) {
/* 189 */     if (this.listeners != null) {
/* 190 */       int size = this.listeners.size();
/*     */ 
/* 192 */       for (int i = 0; i < size; i++) {
/* 193 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 194 */         listener.removeAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Level getThreshold()
/*     */   {
/* 206 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name)
/*     */   {
/* 233 */     return getLogger(name, this.defaultFactory);
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 252 */     CategoryKey key = new CategoryKey(name);
/*     */ 
/* 258 */     synchronized (this.ht) {
/* 259 */       Object o = this.ht.get(key);
/*     */       Logger logger;
/*     */       Logger localLogger1;
/* 260 */       if (o == null) {
/* 261 */         logger = factory.makeNewLoggerInstance(name);
/* 262 */         logger.setHierarchy(this);
/* 263 */         this.ht.put(key, logger);
/* 264 */         updateParents(logger);
/* 265 */         return logger;
/* 266 */       }if ((o instanceof Logger))
/* 267 */         return (Logger)o;
/* 268 */       if ((o instanceof ProvisionNode))
/*     */       {
/* 270 */         logger = factory.makeNewLoggerInstance(name);
/* 271 */         logger.setHierarchy(this);
/* 272 */         this.ht.put(key, logger);
/* 273 */         updateChildren((ProvisionNode)o, logger);
/* 274 */         updateParents(logger);
/* 275 */         return logger;
/*     */       }
/*     */ 
/* 279 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Enumeration getCurrentLoggers()
/*     */   {
/* 295 */     Vector v = new Vector(this.ht.size());
/*     */ 
/* 297 */     Enumeration elems = this.ht.elements();
/* 298 */     while (elems.hasMoreElements()) {
/* 299 */       Object o = elems.nextElement();
/* 300 */       if ((o instanceof Logger)) {
/* 301 */         v.addElement(o);
/*     */       }
/*     */     }
/* 304 */     return v.elements();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Enumeration getCurrentCategories()
/*     */   {
/* 312 */     return getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public RendererMap getRendererMap()
/*     */   {
/* 321 */     return this.rendererMap;
/*     */   }
/*     */ 
/*     */   public Logger getRootLogger()
/*     */   {
/* 332 */     return this.root;
/*     */   }
/*     */ 
/*     */   public boolean isDisabled(int level)
/*     */   {
/* 342 */     return this.thresholdInt > level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void overrideAsNeeded(String override)
/*     */   {
/* 350 */     LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void resetConfiguration()
/*     */   {
/* 370 */     getRootLogger().setLevel(Level.DEBUG);
/* 371 */     this.root.setResourceBundle(null);
/* 372 */     setThreshold(Level.ALL);
/*     */ 
/* 376 */     synchronized (this.ht) {
/* 377 */       shutdown();
/*     */ 
/* 379 */       Enumeration cats = getCurrentLoggers();
/* 380 */       while (cats.hasMoreElements()) {
/* 381 */         Logger c = (Logger)cats.nextElement();
/* 382 */         c.setLevel(null);
/* 383 */         c.setAdditivity(true);
/* 384 */         c.setResourceBundle(null);
/*     */       }
/*     */     }
/* 387 */     this.rendererMap.clear();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setDisableOverride(String override)
/*     */   {
/* 397 */     LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void setRenderer(Class renderedClass, ObjectRenderer renderer)
/*     */   {
/* 407 */     this.rendererMap.put(renderedClass, renderer);
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 429 */     Logger root = getRootLogger();
/*     */ 
/* 432 */     root.closeNestedAppenders();
/*     */ 
/* 434 */     synchronized (this.ht) {
/* 435 */       Enumeration cats = getCurrentLoggers();
/* 436 */       while (cats.hasMoreElements()) {
/* 437 */         Logger c = (Logger)cats.nextElement();
/* 438 */         c.closeNestedAppenders();
/*     */       }
/*     */ 
/* 442 */       root.removeAllAppenders();
/* 443 */       cats = getCurrentLoggers();
/* 444 */       while (cats.hasMoreElements()) {
/* 445 */         Logger c = (Logger)cats.nextElement();
/* 446 */         c.removeAllAppenders();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void updateParents(Logger cat)
/*     */   {
/* 475 */     String name = cat.name;
/* 476 */     int length = name.length();
/* 477 */     boolean parentFound = false;
/*     */ 
/* 482 */     for (int i = name.lastIndexOf('.', length - 1); i >= 0; )
/*     */     {
/* 484 */       String substr = name.substring(0, i);
/*     */ 
/* 487 */       CategoryKey key = new CategoryKey(substr);
/* 488 */       Object o = this.ht.get(key);
/*     */ 
/* 490 */       if (o == null)
/*     */       {
/* 492 */         ProvisionNode pn = new ProvisionNode(cat);
/* 493 */         this.ht.put(key, pn); } else {
/* 494 */         if ((o instanceof Category)) {
/* 495 */           parentFound = true;
/* 496 */           cat.parent = ((Category)o);
/*     */ 
/* 498 */           break;
/* 499 */         }if ((o instanceof ProvisionNode)) {
/* 500 */           ((ProvisionNode)o).addElement(cat);
/*     */         } else {
/* 502 */           Exception e = new IllegalStateException("unexpected object type " + o.getClass() + " in ht.");
/*     */ 
/* 504 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 483 */       i = name.lastIndexOf('.', i - 1);
/*     */     }
/*     */ 
/* 508 */     if (!parentFound)
/* 509 */       cat.parent = this.root;
/*     */   }
/*     */ 
/*     */   private final void updateChildren(ProvisionNode pn, Logger logger)
/*     */   {
/* 531 */     int last = pn.size();
/*     */ 
/* 533 */     for (int i = 0; i < last; i++) {
/* 534 */       Logger l = (Logger)pn.elementAt(i);
/*     */ 
/* 539 */       if (!l.parent.name.startsWith(logger.name)) {
/* 540 */         logger.parent = l.parent;
/* 541 */         l.parent = logger;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Hierarchy
 * JD-Core Version:    0.6.0
 */
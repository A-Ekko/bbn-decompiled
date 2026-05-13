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
/*  77 */   boolean emittedNoAppenderWarning = false;
/*  78 */   boolean emittedNoResourceBundleWarning = false;
/*     */ 
/*     */   public Hierarchy(Logger root)
/*     */   {
/*  88 */     this.ht = new Hashtable();
/*  89 */     this.listeners = new Vector(1);
/*  90 */     this.root = root;
/*     */ 
/*  92 */     setThreshold(Level.ALL);
/*  93 */     this.root.setHierarchy(this);
/*  94 */     this.rendererMap = new RendererMap();
/*  95 */     this.defaultFactory = new DefaultCategoryFactory();
/*     */   }
/*     */ 
/*     */   public void addRenderer(Class classToRender, ObjectRenderer or)
/*     */   {
/* 103 */     this.rendererMap.put(classToRender, or);
/*     */   }
/*     */ 
/*     */   public void addHierarchyEventListener(HierarchyEventListener listener)
/*     */   {
/* 108 */     if (this.listeners.contains(listener))
/* 109 */       LogLog.warn("Ignoring attempt to add an existent listener.");
/*     */     else
/* 111 */       this.listeners.addElement(listener);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 127 */     this.ht.clear();
/*     */   }
/*     */ 
/*     */   public void emitNoAppenderWarning(Category cat)
/*     */   {
/* 133 */     if (!this.emittedNoAppenderWarning) {
/* 134 */       LogLog.warn("No appenders could be found for logger (" + cat.getName() + ").");
/*     */ 
/* 136 */       LogLog.warn("Please initialize the log4j system properly.");
/* 137 */       this.emittedNoAppenderWarning = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Logger exists(String name)
/*     */   {
/* 150 */     Object o = this.ht.get(new CategoryKey(name));
/* 151 */     if ((o instanceof Logger)) {
/* 152 */       return (Logger)o;
/*     */     }
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */   public void setThreshold(String levelStr)
/*     */   {
/* 163 */     Level l = Level.toLevel(levelStr, null);
/* 164 */     if (l != null)
/* 165 */       setThreshold(l);
/*     */     else
/* 167 */       LogLog.warn("Could not convert [" + levelStr + "] to Level.");
/*     */   }
/*     */ 
/*     */   public void setThreshold(Level l)
/*     */   {
/* 180 */     if (l != null) {
/* 181 */       this.thresholdInt = l.level;
/* 182 */       this.threshold = l;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void fireAddAppenderEvent(Category logger, Appender appender)
/*     */   {
/* 188 */     if (this.listeners != null) {
/* 189 */       int size = this.listeners.size();
/*     */ 
/* 191 */       for (int i = 0; i < size; i++) {
/* 192 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 193 */         listener.addAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void fireRemoveAppenderEvent(Category logger, Appender appender) {
/* 199 */     if (this.listeners != null) {
/* 200 */       int size = this.listeners.size();
/*     */ 
/* 202 */       for (int i = 0; i < size; i++) {
/* 203 */         HierarchyEventListener listener = (HierarchyEventListener)this.listeners.elementAt(i);
/* 204 */         listener.removeAppenderEvent(logger, appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Level getThreshold()
/*     */   {
/* 216 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name)
/*     */   {
/* 243 */     return getLogger(name, this.defaultFactory);
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 262 */     CategoryKey key = new CategoryKey(name);
/*     */ 
/* 268 */     synchronized (this.ht) {
/* 269 */       Object o = this.ht.get(key);
/* 270 */       if (o == null) {
/* 271 */         Logger logger = factory.makeNewLoggerInstance(name);
/* 272 */         logger.setHierarchy(this);
/* 273 */         this.ht.put(key, logger);
/* 274 */         updateParents(logger);
/* 275 */         return logger;
/* 276 */       }if ((o instanceof Logger))
/* 277 */         return (Logger)o;
/* 278 */       if ((o instanceof ProvisionNode))
/*     */       {
/* 280 */         Logger logger = factory.makeNewLoggerInstance(name);
/* 281 */         logger.setHierarchy(this);
/* 282 */         this.ht.put(key, logger);
/* 283 */         updateChildren((ProvisionNode)o, logger);
/* 284 */         updateParents(logger);
/* 285 */         return logger;
/*     */       }
/*     */ 
/* 289 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Enumeration getCurrentLoggers()
/*     */   {
/* 305 */     Vector v = new Vector(this.ht.size());
/*     */ 
/* 307 */     Enumeration elems = this.ht.elements();
/* 308 */     while (elems.hasMoreElements()) {
/* 309 */       Object o = elems.nextElement();
/* 310 */       if ((o instanceof Logger)) {
/* 311 */         v.addElement(o);
/*     */       }
/*     */     }
/* 314 */     return v.elements();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Enumeration getCurrentCategories()
/*     */   {
/* 322 */     return getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public RendererMap getRendererMap()
/*     */   {
/* 331 */     return this.rendererMap;
/*     */   }
/*     */ 
/*     */   public Logger getRootLogger()
/*     */   {
/* 342 */     return this.root;
/*     */   }
/*     */ 
/*     */   public boolean isDisabled(int level)
/*     */   {
/* 352 */     return this.thresholdInt > level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void overrideAsNeeded(String override)
/*     */   {
/* 360 */     LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void resetConfiguration()
/*     */   {
/* 380 */     getRootLogger().setLevel(Level.DEBUG);
/* 381 */     this.root.setResourceBundle(null);
/* 382 */     setThreshold(Level.ALL);
/*     */ 
/* 386 */     synchronized (this.ht) {
/* 387 */       shutdown();
/*     */ 
/* 389 */       Enumeration cats = getCurrentLoggers();
/* 390 */       while (cats.hasMoreElements()) {
/* 391 */         Logger c = (Logger)cats.nextElement();
/* 392 */         c.setLevel(null);
/* 393 */         c.setAdditivity(true);
/* 394 */         c.setResourceBundle(null);
/*     */       }
/*     */     }
/* 397 */     this.rendererMap.clear();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setDisableOverride(String override)
/*     */   {
/* 407 */     LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
/*     */   }
/*     */ 
/*     */   public void setRenderer(Class renderedClass, ObjectRenderer renderer)
/*     */   {
/* 417 */     this.rendererMap.put(renderedClass, renderer);
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 439 */     Logger root = getRootLogger();
/*     */ 
/* 442 */     root.closeNestedAppenders();
/*     */ 
/* 444 */     synchronized (this.ht) {
/* 445 */       Enumeration cats = getCurrentLoggers();
/* 446 */       while (cats.hasMoreElements()) {
/* 447 */         Logger c = (Logger)cats.nextElement();
/* 448 */         c.closeNestedAppenders();
/*     */       }
/*     */ 
/* 452 */       root.removeAllAppenders();
/* 453 */       cats = getCurrentLoggers();
/* 454 */       while (cats.hasMoreElements()) {
/* 455 */         Logger c = (Logger)cats.nextElement();
/* 456 */         c.removeAllAppenders();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private final void updateParents(Logger cat)
/*     */   {
/* 485 */     String name = cat.name;
/* 486 */     int length = name.length();
/* 487 */     boolean parentFound = false;
/*     */ 
/* 492 */     for (int i = name.lastIndexOf('.', length - 1); i >= 0; )
/*     */     {
/* 494 */       String substr = name.substring(0, i);
/*     */ 
/* 497 */       CategoryKey key = new CategoryKey(substr);
/* 498 */       Object o = this.ht.get(key);
/*     */ 
/* 500 */       if (o == null)
/*     */       {
/* 502 */         ProvisionNode pn = new ProvisionNode(cat);
/* 503 */         this.ht.put(key, pn); } else {
/* 504 */         if ((o instanceof Category)) {
/* 505 */           parentFound = true;
/* 506 */           cat.parent = ((Category)o);
/*     */ 
/* 508 */           break;
/* 509 */         }if ((o instanceof ProvisionNode)) {
/* 510 */           ((ProvisionNode)o).addElement(cat);
/*     */         } else {
/* 512 */           Exception e = new IllegalStateException("unexpected object type " + o.getClass() + " in ht.");
/*     */ 
/* 514 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 493 */       i = name.lastIndexOf('.', i - 1);
/*     */     }
/*     */ 
/* 518 */     if (!parentFound)
/* 519 */       cat.parent = this.root;
/*     */   }
/*     */ 
/*     */   private final void updateChildren(ProvisionNode pn, Logger logger)
/*     */   {
/* 541 */     int last = pn.size();
/*     */ 
/* 543 */     for (int i = 0; i < last; i++) {
/* 544 */       Logger l = (Logger)pn.elementAt(i);
/*     */ 
/* 549 */       if (!l.parent.name.startsWith(logger.name)) {
/* 550 */         logger.parent = l.parent;
/* 551 */         l.parent = logger;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.Hierarchy
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.MDC;
/*     */ import org.apache.log4j.NDC;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ 
/*     */ public class LoggingEvent
/*     */   implements Serializable
/*     */ {
/*  51 */   private static long startTime = System.currentTimeMillis();
/*     */   public final transient String fqnOfCategoryClass;
/*     */ 
/*     */   /** @deprecated */
/*     */   private transient Category logger;
/*     */ 
/*     */   /** @deprecated */
/*     */   public final String categoryName;
/*     */ 
/*     */   /** @deprecated */
/*     */   public transient Priority level;
/*     */   private String ndc;
/*     */   private Hashtable mdcCopy;
/* 103 */   private boolean ndcLookupRequired = true;
/*     */ 
/* 109 */   private boolean mdcCopyLookupRequired = true;
/*     */   private transient Object message;
/*     */   private String renderedMessage;
/*     */   private String threadName;
/*     */   private ThrowableInformation throwableInfo;
/*     */   public final long timeStamp;
/*     */   private LocationInfo locationInfo;
/*     */   static final long serialVersionUID = -868428216207166145L;
/* 136 */   static final Integer[] PARAM_ARRAY = new Integer[1];
/*     */   static final String TO_LEVEL = "toLevel";
/* 138 */   static final Class[] TO_LEVEL_PARAMS = { Integer.TYPE };
/* 139 */   static final Hashtable methodCache = new Hashtable(3);
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable)
/*     */   {
/* 153 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 154 */     this.logger = logger;
/* 155 */     this.categoryName = logger.getName();
/* 156 */     this.level = level;
/* 157 */     this.message = message;
/* 158 */     if (throwable != null) {
/* 159 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/* 161 */     this.timeStamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable)
/*     */   {
/* 178 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 179 */     this.logger = logger;
/* 180 */     this.categoryName = logger.getName();
/* 181 */     this.level = level;
/* 182 */     this.message = message;
/* 183 */     if (throwable != null) {
/* 184 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/*     */ 
/* 187 */     this.timeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Level level, Object message, String threadName, ThrowableInformation throwable, String ndc, LocationInfo info, Map properties)
/*     */   {
/* 216 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 217 */     this.logger = logger;
/* 218 */     if (logger != null)
/* 219 */       this.categoryName = logger.getName();
/*     */     else {
/* 221 */       this.categoryName = null;
/*     */     }
/* 223 */     this.level = level;
/* 224 */     this.message = message;
/* 225 */     if (throwable != null) {
/* 226 */       this.throwableInfo = throwable;
/*     */     }
/*     */ 
/* 229 */     this.timeStamp = timeStamp;
/* 230 */     this.threadName = threadName;
/* 231 */     this.ndcLookupRequired = false;
/* 232 */     this.ndc = ndc;
/* 233 */     this.locationInfo = info;
/* 234 */     this.mdcCopyLookupRequired = false;
/* 235 */     if (properties != null)
/* 236 */       this.mdcCopy = new Hashtable(properties);
/*     */   }
/*     */ 
/*     */   public LocationInfo getLocationInformation()
/*     */   {
/* 246 */     if (this.locationInfo == null) {
/* 247 */       this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
/*     */     }
/* 249 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public Level getLevel()
/*     */   {
/* 256 */     return (Level)this.level;
/*     */   }
/*     */ 
/*     */   public String getLoggerName()
/*     */   {
/* 264 */     return this.categoryName;
/*     */   }
/*     */ 
/*     */   public Category getLogger()
/*     */   {
/* 273 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public Object getMessage()
/*     */   {
/* 287 */     if (this.message != null) {
/* 288 */       return this.message;
/*     */     }
/* 290 */     return getRenderedMessage();
/*     */   }
/*     */ 
/*     */   public String getNDC()
/*     */   {
/* 301 */     if (this.ndcLookupRequired) {
/* 302 */       this.ndcLookupRequired = false;
/* 303 */       this.ndc = NDC.get();
/*     */     }
/* 305 */     return this.ndc;
/*     */   }
/*     */ 
/*     */   public Object getMDC(String key)
/*     */   {
/* 326 */     if (this.mdcCopy != null) {
/* 327 */       Object r = this.mdcCopy.get(key);
/* 328 */       if (r != null) {
/* 329 */         return r;
/*     */       }
/*     */     }
/* 332 */     return MDC.get(key);
/*     */   }
/*     */ 
/*     */   public void getMDCCopy()
/*     */   {
/* 341 */     if (this.mdcCopyLookupRequired) {
/* 342 */       this.mdcCopyLookupRequired = false;
/*     */ 
/* 345 */       Hashtable t = MDC.getContext();
/* 346 */       if (t != null)
/* 347 */         this.mdcCopy = ((Hashtable)t.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getRenderedMessage()
/*     */   {
/* 354 */     if ((this.renderedMessage == null) && (this.message != null)) {
/* 355 */       if ((this.message instanceof String)) {
/* 356 */         this.renderedMessage = ((String)this.message);
/*     */       } else {
/* 358 */         LoggerRepository repository = this.logger.getLoggerRepository();
/*     */ 
/* 360 */         if ((repository instanceof RendererSupport)) {
/* 361 */           RendererSupport rs = (RendererSupport)repository;
/* 362 */           this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
/*     */         } else {
/* 364 */           this.renderedMessage = this.message.toString();
/*     */         }
/*     */       }
/*     */     }
/* 368 */     return this.renderedMessage;
/*     */   }
/*     */ 
/*     */   public static long getStartTime()
/*     */   {
/* 375 */     return startTime;
/*     */   }
/*     */ 
/*     */   public String getThreadName()
/*     */   {
/* 380 */     if (this.threadName == null)
/* 381 */       this.threadName = Thread.currentThread().getName();
/* 382 */     return this.threadName;
/*     */   }
/*     */ 
/*     */   public ThrowableInformation getThrowableInformation()
/*     */   {
/* 395 */     return this.throwableInfo;
/*     */   }
/*     */ 
/*     */   public String[] getThrowableStrRep()
/*     */   {
/* 404 */     if (this.throwableInfo == null) {
/* 405 */       return null;
/*     */     }
/* 407 */     return this.throwableInfo.getThrowableStrRep();
/*     */   }
/*     */ 
/*     */   private void readLevel(ObjectInputStream ois)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 415 */     int p = ois.readInt();
/*     */     try {
/* 417 */       String className = (String)ois.readObject();
/* 418 */       if (className == null) {
/* 419 */         this.level = Level.toLevel(p);
/*     */       } else {
/* 421 */         Method m = (Method)methodCache.get(className);
/* 422 */         if (m == null) {
/* 423 */           Class clazz = Loader.loadClass(className);
/*     */ 
/* 430 */           m = clazz.getDeclaredMethod("toLevel", TO_LEVEL_PARAMS);
/* 431 */           methodCache.put(className, m);
/*     */         }
/* 433 */         PARAM_ARRAY[0] = new Integer(p);
/* 434 */         this.level = ((Level)m.invoke(null, PARAM_ARRAY));
/*     */       }
/*     */     } catch (Exception e) {
/* 437 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 438 */       this.level = Level.toLevel(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 444 */     ois.defaultReadObject();
/* 445 */     readLevel(ois);
/*     */ 
/* 448 */     if (this.locationInfo == null)
/* 449 */       this.locationInfo = new LocationInfo(null, null);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 456 */     getThreadName();
/*     */ 
/* 459 */     getRenderedMessage();
/*     */ 
/* 463 */     getNDC();
/*     */ 
/* 467 */     getMDCCopy();
/*     */ 
/* 470 */     getThrowableStrRep();
/*     */ 
/* 472 */     oos.defaultWriteObject();
/*     */ 
/* 475 */     writeLevel(oos);
/*     */   }
/*     */ 
/*     */   private void writeLevel(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 481 */     oos.writeInt(this.level.toInt());
/*     */ 
/* 483 */     Class clazz = this.level.getClass();
/* 484 */     if (clazz == Level.class) {
/* 485 */       oos.writeObject(null);
/*     */     }
/*     */     else
/*     */     {
/* 490 */       oos.writeObject(clazz.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setProperty(String propName, String propValue)
/*     */   {
/* 506 */     if (this.mdcCopy == null) {
/* 507 */       getMDCCopy();
/*     */     }
/* 509 */     if (this.mdcCopy == null) {
/* 510 */       this.mdcCopy = new Hashtable();
/*     */     }
/* 512 */     this.mdcCopy.put(propName, propValue);
/*     */   }
/*     */ 
/*     */   public final String getProperty(String key)
/*     */   {
/* 526 */     Object value = getMDC(key);
/* 527 */     String retval = null;
/* 528 */     if (value != null) {
/* 529 */       retval = value.toString();
/*     */     }
/* 531 */     return retval;
/*     */   }
/*     */ 
/*     */   public final boolean locationInformationExists()
/*     */   {
/* 541 */     return this.locationInfo != null;
/*     */   }
/*     */ 
/*     */   public final long getTimeStamp()
/*     */   {
/* 552 */     return this.timeStamp;
/*     */   }
/*     */ 
/*     */   public Set getPropertyKeySet()
/*     */   {
/* 567 */     return getProperties().keySet();
/*     */   }
/*     */ 
/*     */   public Map getProperties()
/*     */   {
/* 582 */     getMDCCopy();
/*     */     Map properties;
/*     */     Map properties;
/* 584 */     if (this.mdcCopy == null)
/* 585 */       properties = new HashMap();
/*     */     else {
/* 587 */       properties = this.mdcCopy;
/*     */     }
/* 589 */     return Collections.unmodifiableMap(properties);
/*     */   }
/*     */ 
/*     */   public String getFQNOfLoggerClass()
/*     */   {
/* 599 */     return this.fqnOfCategoryClass;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.LoggingEvent
 * JD-Core Version:    0.6.0
 */
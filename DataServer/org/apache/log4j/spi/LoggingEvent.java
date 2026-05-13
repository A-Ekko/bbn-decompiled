/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
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
/*  37 */   private static long startTime = System.currentTimeMillis();
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
/*  89 */   private boolean ndcLookupRequired = true;
/*     */ 
/*  95 */   private boolean mdcCopyLookupRequired = true;
/*     */   private transient Object message;
/*     */   private String renderedMessage;
/*     */   private String threadName;
/*     */   private ThrowableInformation throwableInfo;
/*     */   public final long timeStamp;
/*     */   private LocationInfo locationInfo;
/*     */   static final long serialVersionUID = -868428216207166145L;
/* 122 */   static final Integer[] PARAM_ARRAY = new Integer[1];
/*     */   static final String TO_LEVEL = "toLevel";
/* 124 */   static final Class[] TO_LEVEL_PARAMS = { Integer.TYPE };
/* 125 */   static final Hashtable methodCache = new Hashtable(3);
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority priority, Object message, Throwable throwable)
/*     */   {
/* 139 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 140 */     this.logger = logger;
/* 141 */     this.categoryName = logger.getName();
/* 142 */     this.level = priority;
/* 143 */     this.message = message;
/* 144 */     if (throwable != null) {
/* 145 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/* 147 */     this.timeStamp = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority priority, Object message, Throwable throwable)
/*     */   {
/* 164 */     this.fqnOfCategoryClass = fqnOfCategoryClass;
/* 165 */     this.logger = logger;
/* 166 */     this.categoryName = logger.getName();
/* 167 */     this.level = priority;
/* 168 */     this.message = message;
/* 169 */     if (throwable != null) {
/* 170 */       this.throwableInfo = new ThrowableInformation(throwable);
/*     */     }
/*     */ 
/* 173 */     this.timeStamp = timeStamp;
/*     */   }
/*     */ 
/*     */   public LocationInfo getLocationInformation()
/*     */   {
/* 181 */     if (this.locationInfo == null) {
/* 182 */       this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
/*     */     }
/* 184 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public Level getLevel()
/*     */   {
/* 191 */     return (Level)this.level;
/*     */   }
/*     */ 
/*     */   public String getLoggerName()
/*     */   {
/* 199 */     return this.categoryName;
/*     */   }
/*     */ 
/*     */   public Object getMessage()
/*     */   {
/* 213 */     if (this.message != null) {
/* 214 */       return this.message;
/*     */     }
/* 216 */     return getRenderedMessage();
/*     */   }
/*     */ 
/*     */   public String getNDC()
/*     */   {
/* 227 */     if (this.ndcLookupRequired) {
/* 228 */       this.ndcLookupRequired = false;
/* 229 */       this.ndc = NDC.get();
/*     */     }
/* 231 */     return this.ndc;
/*     */   }
/*     */ 
/*     */   public Object getMDC(String key)
/*     */   {
/* 252 */     if (this.mdcCopy != null) {
/* 253 */       Object r = this.mdcCopy.get(key);
/* 254 */       if (r != null) {
/* 255 */         return r;
/*     */       }
/*     */     }
/* 258 */     return MDC.get(key);
/*     */   }
/*     */ 
/*     */   public void getMDCCopy()
/*     */   {
/* 267 */     if (this.mdcCopyLookupRequired) {
/* 268 */       this.mdcCopyLookupRequired = false;
/*     */ 
/* 271 */       Hashtable t = MDC.getContext();
/* 272 */       if (t != null)
/* 273 */         this.mdcCopy = ((Hashtable)t.clone());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getRenderedMessage()
/*     */   {
/* 280 */     if ((this.renderedMessage == null) && (this.message != null)) {
/* 281 */       if ((this.message instanceof String)) {
/* 282 */         this.renderedMessage = ((String)this.message);
/*     */       } else {
/* 284 */         LoggerRepository repository = this.logger.getHierarchy();
/*     */ 
/* 286 */         if ((repository instanceof RendererSupport)) {
/* 287 */           RendererSupport rs = (RendererSupport)repository;
/* 288 */           this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
/*     */         } else {
/* 290 */           this.renderedMessage = this.message.toString();
/*     */         }
/*     */       }
/*     */     }
/* 294 */     return this.renderedMessage;
/*     */   }
/*     */ 
/*     */   public static long getStartTime()
/*     */   {
/* 301 */     return startTime;
/*     */   }
/*     */ 
/*     */   public String getThreadName()
/*     */   {
/* 306 */     if (this.threadName == null)
/* 307 */       this.threadName = Thread.currentThread().getName();
/* 308 */     return this.threadName;
/*     */   }
/*     */ 
/*     */   public ThrowableInformation getThrowableInformation()
/*     */   {
/* 321 */     return this.throwableInfo;
/*     */   }
/*     */ 
/*     */   public String[] getThrowableStrRep()
/*     */   {
/* 330 */     if (this.throwableInfo == null) {
/* 331 */       return null;
/*     */     }
/* 333 */     return this.throwableInfo.getThrowableStrRep();
/*     */   }
/*     */ 
/*     */   private void readLevel(ObjectInputStream ois)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 341 */     int p = ois.readInt();
/*     */     try {
/* 343 */       String className = (String)ois.readObject();
/* 344 */       if (className == null) {
/* 345 */         this.level = Level.toLevel(p);
/*     */       } else {
/* 347 */         Method m = (Method)methodCache.get(className);
/* 348 */         if (m == null) {
/* 349 */           Class clazz = Loader.loadClass(className);
/*     */ 
/* 356 */           m = clazz.getDeclaredMethod("toLevel", TO_LEVEL_PARAMS);
/* 357 */           methodCache.put(className, m);
/*     */         }
/* 359 */         PARAM_ARRAY[0] = new Integer(p);
/* 360 */         this.level = ((Level)m.invoke(null, PARAM_ARRAY));
/*     */       }
/*     */     } catch (Exception e) {
/* 363 */       LogLog.warn("Level deserialization failed, reverting to default.", e);
/* 364 */       this.level = Level.toLevel(p);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
/*     */   {
/* 370 */     ois.defaultReadObject();
/* 371 */     readLevel(ois);
/*     */ 
/* 374 */     if (this.locationInfo == null)
/* 375 */       this.locationInfo = new LocationInfo(null, null);
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 382 */     getThreadName();
/*     */ 
/* 385 */     getRenderedMessage();
/*     */ 
/* 389 */     getNDC();
/*     */ 
/* 393 */     getMDCCopy();
/*     */ 
/* 396 */     getThrowableStrRep();
/*     */ 
/* 398 */     oos.defaultWriteObject();
/*     */ 
/* 401 */     writeLevel(oos);
/*     */   }
/*     */ 
/*     */   private void writeLevel(ObjectOutputStream oos)
/*     */     throws IOException
/*     */   {
/* 407 */     oos.writeInt(this.level.toInt());
/*     */ 
/* 409 */     Class clazz = this.level.getClass();
/* 410 */     if (clazz == Level.class) {
/* 411 */       oos.writeObject(null);
/*     */     }
/*     */     else
/*     */     {
/* 416 */       oos.writeObject(clazz.getName());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.LoggingEvent
 * JD-Core Version:    0.6.0
 */
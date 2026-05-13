/*     */ package flex.messaging.log;
/*     */ 
/*     */ import flex.messaging.LocalizedException;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class AbstractTarget
/*     */   implements Target
/*     */ {
/*     */   private static final int INVALID_FILTER_CHARS = 10016;
/*     */   private static final int INVALID_FILTER_STAR = 10017;
/*     */   protected final String id;
/*     */   protected List filters;
/*     */   protected volatile short level;
/*     */   protected volatile int loggerCount;
/*  38 */   private final Object lock = new Object();
/*  39 */   private boolean usingDefaultFilter = false;
/*     */ 
/*     */   public AbstractTarget()
/*     */   {
/*  52 */     this.id = UUIDUtils.createUUID();
/*  53 */     this.level = 8;
/*  54 */     this.filters = new ArrayList();
/*  55 */     this.filters.add("*");
/*  56 */     this.usingDefaultFilter = true;
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*     */   }
/*     */ 
/*     */   public List getFilters()
/*     */   {
/*  89 */     return Collections.unmodifiableList(new ArrayList(this.filters));
/*     */   }
/*     */ 
/*     */   public void addFilter(String value)
/*     */   {
/*  99 */     if (value != null)
/* 100 */       validateFilter(value);
/*     */     else {
/* 102 */       value = "*";
/*     */     }
/* 104 */     boolean filterWasAdded = false;
/* 105 */     synchronized (this.lock)
/*     */     {
/* 107 */       if (!this.filters.contains(value))
/*     */       {
/* 110 */         if (this.usingDefaultFilter)
/*     */         {
/* 112 */           removeFilter("*");
/* 113 */           this.usingDefaultFilter = false;
/*     */         }
/* 115 */         this.filters.add(value);
/* 116 */         filterWasAdded = true;
/*     */       }
/*     */     }
/* 119 */     if (filterWasAdded)
/* 120 */       Log.processTargetFilterAdd(this, value);
/*     */   }
/*     */ 
/*     */   public void removeFilter(String value)
/*     */   {
/* 130 */     boolean filterWasRemoved = false;
/* 131 */     synchronized (this.lock)
/*     */     {
/* 133 */       filterWasRemoved = this.filters.remove(value);
/*     */     }
/* 135 */     if (filterWasRemoved)
/* 136 */       Log.processTargetFilterRemove(this, value);
/*     */   }
/*     */ 
/*     */   public void setFilters(List value)
/*     */   {
/* 146 */     if ((value != null) && (value.size() > 0))
/*     */     {
/* 151 */       for (int i = 0; i < value.size(); i++)
/*     */       {
/* 153 */         validateFilter((String)value.get(i));
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 159 */       value = new ArrayList();
/* 160 */       value.add("*");
/*     */     }
/*     */ 
/* 163 */     Log.removeTarget(this);
/* 164 */     synchronized (this.lock)
/*     */     {
/* 166 */       this.filters = value;
/* 167 */       this.usingDefaultFilter = false;
/*     */     }
/* 169 */     Log.addTarget(this);
/*     */   }
/*     */ 
/*     */   public short getLevel()
/*     */   {
/* 177 */     return this.level;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 185 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setLevel(short value)
/*     */   {
/* 193 */     this.level = value;
/* 194 */     Log.resetTargetLevel();
/*     */   }
/*     */ 
/*     */   public void addLogger(Logger logger)
/*     */   {
/* 205 */     if (logger != null)
/*     */     {
/* 207 */       synchronized (this.lock)
/*     */       {
/* 209 */         this.loggerCount += 1;
/*     */       }
/* 211 */       logger.addTarget(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeLogger(Logger logger)
/*     */   {
/* 222 */     if (logger != null)
/*     */     {
/* 224 */       synchronized (this.lock)
/*     */       {
/* 226 */         this.loggerCount -= 1;
/*     */       }
/* 228 */       logger.removeTarget(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean containsFilter(String filter)
/*     */   {
/* 238 */     return this.filters.contains(filter);
/*     */   }
/*     */ 
/*     */   private void validateFilter(String value)
/*     */   {
/* 250 */     if (Log.hasIllegalCharacters(value))
/*     */     {
/* 253 */       LocalizedException ex = new LocalizedException();
/* 254 */       ex.setMessage(10016, new Object[] { value, "[]~$^&\\/(){}<>+=`!#%?,:;'\"@" });
/* 255 */       throw ex;
/*     */     }
/*     */ 
/* 258 */     int index = value.indexOf("*");
/* 259 */     if ((index >= 0) && (index != value.length() - 1))
/*     */     {
/* 262 */       LocalizedException ex = new LocalizedException();
/* 263 */       ex.setMessage(10017, new Object[] { value });
/* 264 */       throw ex;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.log.AbstractTarget
 * JD-Core Version:    0.6.0
 */
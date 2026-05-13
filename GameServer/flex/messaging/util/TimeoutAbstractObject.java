/*     */ package flex.messaging.util;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Future;
/*     */ 
/*     */ public abstract class TimeoutAbstractObject
/*     */   implements TimeoutCapable
/*     */ {
/*     */   private long lastUse;
/*     */   private volatile boolean timeoutCanceled;
/*     */   private TimeoutManager timeoutManager;
/*     */   private Runnable timeoutTask;
/*     */   private Future timeoutFuture;
/*     */   private long timeoutPeriod;
/*  37 */   private final Object lock = new Object();
/*     */ 
/*     */   public void cancelTimeout()
/*     */   {
/*  41 */     if (this.timeoutCanceled) {
/*  42 */       return;
/*     */     }
/*  44 */     boolean purged = false;
/*  45 */     if ((this.timeoutManager != null) && (this.timeoutTask != null) && (this.timeoutFuture != null)) {
/*  46 */       purged = this.timeoutManager.unscheduleTimeout(this);
/*     */     }
/*  48 */     if ((!purged) && (this.timeoutFuture != null))
/*     */     {
/*  50 */       this.timeoutFuture.cancel(false);
/*     */     }
/*     */ 
/*  53 */     this.timeoutCanceled = true;
/*     */   }
/*     */ 
/*     */   public long getLastUse()
/*     */   {
/*  58 */     synchronized (this.lock)
/*     */     {
/*  60 */       return this.lastUse;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLastUse(long lastUse)
/*     */   {
/*  66 */     synchronized (this.lock)
/*     */     {
/*  68 */       this.lastUse = lastUse;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateLastUse()
/*     */   {
/*  74 */     synchronized (this.lock)
/*     */     {
/*  76 */       this.lastUse = System.currentTimeMillis();
/*     */     }
/*     */   }
/*     */ 
/*     */   public TimeoutManager getTimeoutManager()
/*     */   {
/*  82 */     synchronized (this.lock)
/*     */     {
/*  84 */       return this.timeoutManager;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimeoutManager(TimeoutManager timeoutManager)
/*     */   {
/*  90 */     synchronized (this.lock)
/*     */     {
/*  92 */       this.timeoutManager = timeoutManager;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Runnable getTimeoutTask()
/*     */   {
/*  98 */     synchronized (this.lock)
/*     */     {
/* 100 */       return this.timeoutTask;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimeoutTask(Runnable timeoutTask)
/*     */   {
/* 106 */     synchronized (this.lock)
/*     */     {
/* 108 */       this.timeoutTask = timeoutTask;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Future getTimeoutFuture()
/*     */   {
/* 114 */     synchronized (this.lock)
/*     */     {
/* 116 */       return this.timeoutFuture;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimeoutFuture(Future timeoutFuture)
/*     */   {
/* 122 */     synchronized (this.lock)
/*     */     {
/* 124 */       this.timeoutFuture = timeoutFuture;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getTimeoutPeriod()
/*     */   {
/* 130 */     synchronized (this.lock)
/*     */     {
/* 132 */       return this.timeoutPeriod;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTimeoutPeriod(long timeoutPeriod)
/*     */   {
/* 138 */     synchronized (this.lock)
/*     */     {
/* 140 */       this.timeoutPeriod = timeoutPeriod;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.TimeoutAbstractObject
 * JD-Core Version:    0.6.0
 */
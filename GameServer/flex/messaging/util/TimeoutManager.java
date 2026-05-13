/*     */ package flex.messaging.util;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Future;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ 
/*     */ public class TimeoutManager
/*     */ {
/*     */   private static final String LOG_CATEGORY = "Timeout";
/*     */   private ScheduledThreadPoolExecutor timeoutService;
/*     */ 
/*     */   public TimeoutManager()
/*     */   {
/*  44 */     this(null);
/*     */   }
/*     */ 
/*     */   public TimeoutManager(ThreadFactory tf)
/*     */   {
/*  49 */     if (tf == null)
/*     */     {
/*  51 */       tf = new MonitorThreadFactory();
/*     */     }
/*  53 */     this.timeoutService = new ScheduledThreadPoolExecutor(1, tf);
/*     */   }
/*     */ 
/*     */   public Future scheduleTimeout(TimeoutCapable t)
/*     */   {
/*  58 */     Future future = null;
/*  59 */     if (t.getTimeoutPeriod() > 0L)
/*     */     {
/*  61 */       Runnable timeoutTask = new TimeoutTask(t);
/*  62 */       future = this.timeoutService.schedule(timeoutTask, t.getTimeoutPeriod(), TimeUnit.MILLISECONDS);
/*  63 */       t.setTimeoutFuture(future);
/*  64 */       if ((t instanceof TimeoutAbstractObject))
/*     */       {
/*  66 */         TimeoutAbstractObject timeoutAbstract = (TimeoutAbstractObject)t;
/*  67 */         timeoutAbstract.setTimeoutManager(this);
/*  68 */         timeoutAbstract.setTimeoutTask(timeoutTask);
/*     */       }
/*  70 */       if (Log.isDebug()) {
/*  71 */         Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(this) + "' has scheduled instance '" + System.identityHashCode(t) + "' of type '" + t.getClass().getName() + "' to be timed out in " + t.getTimeoutPeriod() + " milliseconds. Task queue size: " + this.timeoutService.getQueue().size());
/*     */       }
/*     */     }
/*  74 */     return future;
/*     */   }
/*     */ 
/*     */   public boolean unscheduleTimeout(TimeoutAbstractObject timeoutAbstract)
/*     */   {
/*  79 */     if (this.timeoutService.remove(timeoutAbstract.getTimeoutTask()))
/*     */     {
/*  81 */       if (Log.isDebug()) {
/*  82 */         Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(this) + "' has removed the timeout task for instance '" + System.identityHashCode(timeoutAbstract) + "' of type '" + timeoutAbstract.getClass().getName() + "' that has requested its timeout be cancelled. Task queue size: " + this.timeoutService.getQueue().size());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  87 */       Future timeoutFuture = timeoutAbstract.getTimeoutFuture();
/*  88 */       timeoutFuture.cancel(false);
/*  89 */       if (Log.isDebug()) {
/*  90 */         Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(this) + "' cancelling timeout task for instance '" + System.identityHashCode(timeoutAbstract) + "' of type '" + timeoutAbstract.getClass().getName() + "' that has requested its timeout be cancelled. Task queue size: " + this.timeoutService.getQueue().size());
/*     */       }
/*  92 */       if (timeoutFuture.isDone())
/*     */       {
/*  94 */         this.timeoutService.purge();
/*  95 */         if (Log.isDebug())
/*  96 */           Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(this) + "' purged queue of any cancelled or completed tasks. Task queue size: " + this.timeoutService.getQueue().size());
/*     */       }
/*     */     }
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 104 */     this.timeoutService.shutdown();
/*     */   }
/*     */ 
/*     */   class TimeoutTask
/*     */     implements Runnable
/*     */   {
/*     */     private TimeoutCapable timeoutObject;
/*     */ 
/*     */     public TimeoutTask(TimeoutCapable timeoutObject)
/*     */     {
/* 124 */       this.timeoutObject = timeoutObject;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 129 */       long inactiveMillis = System.currentTimeMillis() - this.timeoutObject.getLastUse();
/* 130 */       if (inactiveMillis >= this.timeoutObject.getTimeoutPeriod())
/*     */       {
/* 132 */         this.timeoutObject.timeout();
/* 133 */         if (Log.isDebug()) {
/* 134 */           Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(TimeoutManager.this) + "' has run the timeout task for instance '" + System.identityHashCode(this.timeoutObject) + "' of type '" + this.timeoutObject.getClass().getName() + "'. Task queue size: " + TimeoutManager.this.timeoutService.getQueue().size());
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 140 */         this.timeoutObject.setTimeoutFuture(TimeoutManager.this.timeoutService.schedule(this, this.timeoutObject.getTimeoutPeriod() - inactiveMillis, TimeUnit.MILLISECONDS));
/* 141 */         if (Log.isDebug())
/* 142 */           Log.getLogger("Timeout").debug("TimeoutManager '" + System.identityHashCode(TimeoutManager.this) + "' has rescheduled a timeout for the active instance '" + System.identityHashCode(this.timeoutObject) + "' of type '" + this.timeoutObject.getClass().getName() + "'. Task queue size: " + TimeoutManager.this.timeoutService.getQueue().size());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class MonitorThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/*     */     MonitorThreadFactory()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Thread newThread(Runnable r)
/*     */     {
/* 111 */       Thread t = new Thread(r);
/* 112 */       t.setDaemon(true);
/* 113 */       t.setName("TimeoutManager");
/* 114 */       return t;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.TimeoutManager
 * JD-Core Version:    0.6.0
 */
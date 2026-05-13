/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IoEventQueueThrottle
/*     */   implements IoEventQueueHandler
/*     */ {
/*  36 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */   private final IoEventSizeEstimator eventSizeEstimator;
/*     */   private volatile int threshold;
/*  43 */   private final Object lock = new Object();
/*  44 */   private final AtomicInteger counter = new AtomicInteger();
/*     */   private int waiters;
/*     */ 
/*     */   public IoEventQueueThrottle()
/*     */   {
/*  48 */     this(new DefaultIoEventSizeEstimator(), 65536);
/*     */   }
/*     */ 
/*     */   public IoEventQueueThrottle(int threshold) {
/*  52 */     this(new DefaultIoEventSizeEstimator(), threshold);
/*     */   }
/*     */ 
/*     */   public IoEventQueueThrottle(IoEventSizeEstimator eventSizeEstimator, int threshold) {
/*  56 */     if (eventSizeEstimator == null) {
/*  57 */       throw new NullPointerException("eventSizeEstimator");
/*     */     }
/*  59 */     this.eventSizeEstimator = eventSizeEstimator;
/*     */ 
/*  61 */     setThreshold(threshold);
/*     */   }
/*     */ 
/*     */   public IoEventSizeEstimator getEventSizeEstimator() {
/*  65 */     return this.eventSizeEstimator;
/*     */   }
/*     */ 
/*     */   public int getThreshold() {
/*  69 */     return this.threshold;
/*     */   }
/*     */ 
/*     */   public int getCounter() {
/*  73 */     return this.counter.get();
/*     */   }
/*     */ 
/*     */   public void setThreshold(int threshold) {
/*  77 */     if (threshold <= 0) {
/*  78 */       throw new IllegalArgumentException("threshold: " + threshold);
/*     */     }
/*  80 */     this.threshold = threshold;
/*     */   }
/*     */ 
/*     */   public boolean accept(Object source, IoEvent event) {
/*  84 */     return true;
/*     */   }
/*     */ 
/*     */   public void offered(Object source, IoEvent event) {
/*  88 */     int eventSize = estimateSize(event);
/*  89 */     int currentCounter = this.counter.addAndGet(eventSize);
/*  90 */     logState();
/*     */ 
/*  92 */     if (currentCounter >= this.threshold)
/*  93 */       block();
/*     */   }
/*     */ 
/*     */   public void polled(Object source, IoEvent event)
/*     */   {
/*  98 */     int eventSize = estimateSize(event);
/*  99 */     int currentCounter = this.counter.addAndGet(-eventSize);
/*     */ 
/* 101 */     logState();
/*     */ 
/* 103 */     if (currentCounter < this.threshold)
/* 104 */       unblock();
/*     */   }
/*     */ 
/*     */   private int estimateSize(IoEvent event)
/*     */   {
/* 109 */     int size = getEventSizeEstimator().estimateSize(event);
/* 110 */     if (size < 0) {
/* 111 */       throw new IllegalStateException(IoEventSizeEstimator.class.getSimpleName() + " returned " + "a negative value (" + size + "): " + event);
/*     */     }
/*     */ 
/* 115 */     return size;
/*     */   }
/*     */ 
/*     */   private void logState() {
/* 119 */     if (this.logger.isDebugEnabled())
/* 120 */       this.logger.debug(Thread.currentThread().getName() + " state: " + this.counter.get() + " / " + getThreshold());
/*     */   }
/*     */ 
/*     */   protected void block()
/*     */   {
/* 125 */     if (this.logger.isDebugEnabled()) {
/* 126 */       this.logger.debug(Thread.currentThread().getName() + " blocked: " + this.counter.get() + " >= " + this.threshold);
/*     */     }
/*     */ 
/* 129 */     synchronized (this.lock) {
/* 130 */       while (this.counter.get() >= this.threshold) {
/* 131 */         this.waiters += 1;
/*     */         try {
/* 133 */           this.lock.wait();
/*     */         } catch (InterruptedException e) {
/*     */         }
/*     */         finally {
/* 137 */           this.waiters -= 1;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 142 */     if (this.logger.isDebugEnabled())
/* 143 */       this.logger.debug(Thread.currentThread().getName() + " unblocked: " + this.counter.get() + " < " + this.threshold);
/*     */   }
/*     */ 
/*     */   protected void unblock()
/*     */   {
/* 148 */     synchronized (this.lock) {
/* 149 */       if (this.waiters > 0)
/* 150 */         this.lock.notify();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.executor.IoEventQueueThrottle
 * JD-Core Version:    0.6.0
 */
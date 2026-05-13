/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ public class LinkedQueue
/*     */   implements Channel
/*     */ {
/*     */   protected LinkedNode head_;
/*  43 */   protected final Object putLock_ = new Object();
/*     */   protected LinkedNode last_;
/*  57 */   protected int waitingForTake_ = 0;
/*     */ 
/*     */   public LinkedQueue() {
/*  60 */     this.head_ = new LinkedNode(null);
/*  61 */     this.last_ = this.head_;
/*     */   }
/*     */ 
/*     */   protected void insert(Object x)
/*     */   {
/*  66 */     synchronized (this.putLock_) {
/*  67 */       LinkedNode p = new LinkedNode(x);
/*  68 */       synchronized (this.last_) {
/*  69 */         this.last_.next = p;
/*  70 */         this.last_ = p;
/*     */       }
/*  72 */       if (this.waitingForTake_ > 0)
/*  73 */         this.putLock_.notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized Object extract()
/*     */   {
/*  79 */     synchronized (this.head_) {
/*  80 */       Object x = null;
/*  81 */       LinkedNode first = this.head_.next;
/*  82 */       if (first != null) {
/*  83 */         x = first.value;
/*  84 */         first.value = null;
/*  85 */         this.head_ = first;
/*     */       }
/*  87 */       return x;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(Object x) throws InterruptedException
/*     */   {
/*  93 */     if (x == null) throw new IllegalArgumentException();
/*  94 */     if (Thread.interrupted()) throw new InterruptedException();
/*  95 */     insert(x);
/*     */   }
/*     */ 
/*     */   public boolean offer(Object x, long msecs) throws InterruptedException {
/*  99 */     if (x == null) throw new IllegalArgumentException();
/* 100 */     if (Thread.interrupted()) throw new InterruptedException();
/* 101 */     insert(x);
/* 102 */     return true;
/*     */   }
/*     */ 
/*     */   public Object take() throws InterruptedException {
/* 106 */     if (Thread.interrupted()) throw new InterruptedException();
/*     */ 
/* 108 */     Object x = extract();
/* 109 */     if (x != null) {
/* 110 */       return x;
/*     */     }
/* 112 */     synchronized (this.putLock_) {
/*     */       try {
/* 114 */         this.waitingForTake_ += 1;
/*     */         while (true) {
/* 116 */           x = extract();
/* 117 */           if (x != null) {
/* 118 */             this.waitingForTake_ -= 1;
/* 119 */             return x;
/*     */           }
/* 121 */           this.putLock_.wait();
/*     */         }
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 125 */         this.waitingForTake_ -= 1;
/* 126 */         this.putLock_.notify();
/* 127 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object peek()
/*     */   {
/* 134 */     synchronized (this.head_) {
/* 135 */       LinkedNode first = this.head_.next;
/* 136 */       if (first != null) {
/* 137 */         return first.value;
/*     */       }
/* 139 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 145 */     synchronized (this.head_) {
/* 146 */       return this.head_.next == null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object poll(long msecs) throws InterruptedException {
/* 151 */     if (Thread.interrupted()) throw new InterruptedException();
/* 152 */     Object x = extract();
/* 153 */     if (x != null) {
/* 154 */       return x;
/*     */     }
/* 156 */     synchronized (this.putLock_) {
/*     */       try {
/* 158 */         long waitTime = msecs;
/* 159 */         long start = msecs <= 0L ? 0L : System.currentTimeMillis();
/* 160 */         this.waitingForTake_ += 1;
/*     */         while (true) {
/* 162 */           x = extract();
/* 163 */           if ((x != null) || (waitTime <= 0L)) {
/* 164 */             this.waitingForTake_ -= 1;
/* 165 */             return x;
/*     */           }
/* 167 */           this.putLock_.wait(waitTime);
/* 168 */           waitTime = msecs - (System.currentTimeMillis() - start);
/*     */         }
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 172 */         this.waitingForTake_ -= 1;
/* 173 */         this.putLock_.notify();
/* 174 */         throw ex;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.LinkedQueue
 * JD-Core Version:    0.6.0
 */
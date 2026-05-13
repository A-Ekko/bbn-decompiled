/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ 
/*     */ public class FutureResult
/*     */ {
/*  50 */   protected Object value_ = null;
/*     */ 
/*  53 */   protected boolean ready_ = false;
/*     */ 
/*  56 */   protected InvocationTargetException exception_ = null;
/*     */ 
/*     */   public Runnable setter(Callable function)
/*     */   {
/*  74 */     return new Runnable(function) {
/*     */       public void run() {
/*     */         try {
/*  77 */           FutureResult.this.set(this.val$function.call());
/*     */         } catch (Throwable ex) {
/*  79 */           FutureResult.this.setException(ex);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected Object doGet() throws InvocationTargetException {
/*  87 */     if (this.exception_ != null) {
/*  88 */       throw this.exception_;
/*     */     }
/*  90 */     return this.value_;
/*     */   }
/*     */ 
/*     */   public synchronized Object get()
/*     */     throws InterruptedException, InvocationTargetException
/*     */   {
/* 102 */     while (!this.ready_) wait();
/* 103 */     return doGet();
/*     */   }
/*     */ 
/*     */   public synchronized Object timedGet(long msecs)
/*     */     throws TimeoutException, InterruptedException, InvocationTargetException
/*     */   {
/* 117 */     long startTime = msecs <= 0L ? 0L : System.currentTimeMillis();
/* 118 */     long waitTime = msecs;
/* 119 */     if (this.ready_)
/* 120 */       return doGet();
/* 121 */     if (waitTime <= 0L)
/* 122 */       throw new TimeoutException(msecs);
/*     */     do
/*     */     {
/* 125 */       wait(waitTime);
/* 126 */       if (this.ready_) {
/* 127 */         return doGet();
/*     */       }
/* 129 */       waitTime = msecs - (System.currentTimeMillis() - startTime);
/* 130 */     }while (waitTime > 0L);
/* 131 */     throw new TimeoutException(msecs);
/*     */   }
/*     */ 
/*     */   public synchronized void set(Object newValue)
/*     */   {
/* 144 */     this.value_ = newValue;
/* 145 */     this.ready_ = true;
/* 146 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized void setException(Throwable ex)
/*     */   {
/* 155 */     this.exception_ = new InvocationTargetException(ex);
/* 156 */     this.ready_ = true;
/* 157 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized InvocationTargetException getException()
/*     */   {
/* 169 */     return this.exception_;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isReady()
/*     */   {
/* 177 */     return this.ready_;
/*     */   }
/*     */ 
/*     */   public synchronized Object peek()
/*     */   {
/* 185 */     return this.value_;
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 197 */     this.value_ = null;
/* 198 */     this.exception_ = null;
/* 199 */     this.ready_ = false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.FutureResult
 * JD-Core Version:    0.6.0
 */
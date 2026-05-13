/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ public class SynchronizedVariable
/*     */   implements Executor
/*     */ {
/*     */   protected final Object lock_;
/*     */ 
/*     */   public SynchronizedVariable(Object lock)
/*     */   {
/* 185 */     this.lock_ = lock;
/*     */   }
/*     */ 
/*     */   public SynchronizedVariable()
/*     */   {
/* 190 */     this.lock_ = this;
/*     */   }
/*     */ 
/*     */   public Object getLock()
/*     */   {
/* 197 */     return this.lock_;
/*     */   }
/*     */ 
/*     */   public void execute(Runnable command)
/*     */     throws InterruptedException
/*     */   {
/* 206 */     if (Thread.interrupted()) throw new InterruptedException();
/* 207 */     synchronized (this.lock_) {
/* 208 */       command.run();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.SynchronizedVariable
 * JD-Core Version:    0.6.0
 */
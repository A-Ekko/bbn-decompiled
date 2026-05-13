/*     */ package org.apache.mina.core.future;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.core.polling.AbstractPollingIoProcessor;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public class DefaultIoFuture
/*     */   implements IoFuture
/*     */ {
/*     */   private static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;
/*     */   private final IoSession session;
/*     */   private final Object lock;
/*     */   private IoFutureListener<?> firstListener;
/*     */   private List<IoFutureListener<?>> otherListeners;
/*     */   private Object result;
/*     */   private boolean ready;
/*     */   private int waiters;
/*     */ 
/*     */   public DefaultIoFuture(IoSession session)
/*     */   {
/*  61 */     this.session = session;
/*  62 */     this.lock = this;
/*     */   }
/*     */ 
/*     */   public IoSession getSession()
/*     */   {
/*  69 */     return this.session;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void join()
/*     */   {
/*  77 */     awaitUninterruptibly();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean join(long timeoutMillis)
/*     */   {
/*  85 */     return awaitUninterruptibly(timeoutMillis);
/*     */   }
/*     */ 
/*     */   public IoFuture await()
/*     */     throws InterruptedException
/*     */   {
/*  92 */     synchronized (this.lock) {
/*  93 */       while (!this.ready) {
/*  94 */         this.waiters += 1;
/*     */         try
/*     */         {
/*  99 */           this.lock.wait(5000L);
/*     */         } finally {
/* 101 */           this.waiters -= 1;
/* 102 */           if (!this.ready) {
/* 103 */             checkDeadLock();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean await(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 116 */     return await(unit.toMillis(timeout));
/*     */   }
/*     */ 
/*     */   public boolean await(long timeoutMillis)
/*     */     throws InterruptedException
/*     */   {
/* 123 */     return await0(timeoutMillis, true);
/*     */   }
/*     */ 
/*     */   public IoFuture awaitUninterruptibly()
/*     */   {
/*     */     try
/*     */     {
/* 131 */       await0(9223372036854775807L, false);
/*     */     }
/*     */     catch (InterruptedException ie)
/*     */     {
/*     */     }
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean awaitUninterruptibly(long timeout, TimeUnit unit)
/*     */   {
/* 143 */     return awaitUninterruptibly(unit.toMillis(timeout));
/*     */   }
/*     */ 
/*     */   public boolean awaitUninterruptibly(long timeoutMillis)
/*     */   {
/*     */     try
/*     */     {
/* 151 */       return await0(timeoutMillis, false); } catch (InterruptedException e) {
/*     */     }
/* 153 */     throw new InternalError();
/*     */   }
/*     */ 
/*     */   private boolean await0(long timeoutMillis, boolean interruptable)
/*     */     throws InterruptedException
/*     */   {
/* 171 */     long endTime = System.currentTimeMillis() + timeoutMillis;
/*     */ 
/* 173 */     synchronized (this.lock) {
/* 174 */       if (this.ready)
/* 175 */         return this.ready;
/* 176 */       if (timeoutMillis <= 0L) {
/* 177 */         return this.ready;
/*     */       }
/*     */ 
/* 180 */       this.waiters += 1;
/*     */       try {
/*     */         do {
/*     */           try {
/* 184 */             long timeOut = Math.min(timeoutMillis, 5000L);
/* 185 */             this.lock.wait(timeOut);
/*     */           } catch (InterruptedException e) {
/* 187 */             if (interruptable) {
/* 188 */               throw e;
/*     */             }
/*     */           }
/*     */ 
/* 192 */           if (this.ready) {
/* 193 */             e = 1;
/*     */ 
/* 201 */             this.waiters -= 1;
/* 202 */             if (!this.ready)
/* 203 */               checkDeadLock(); return e;
/*     */           }
/*     */         }
/* 195 */         while (endTime >= System.currentTimeMillis());
/* 196 */         e = this.ready;
/*     */ 
/* 201 */         this.waiters -= 1;
/* 202 */         if (!this.ready)
/* 203 */           checkDeadLock(); return e;
/*     */       }
/*     */       finally
/*     */       {
/* 201 */         this.waiters -= 1;
/* 202 */         if (!this.ready)
/* 203 */           checkDeadLock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkDeadLock()
/*     */   {
/* 217 */     if ((!(this instanceof CloseFuture)) && (!(this instanceof WriteFuture)) && (!(this instanceof ReadFuture)) && (!(this instanceof ConnectFuture)))
/*     */     {
/* 219 */       return;
/*     */     }
/*     */ 
/* 228 */     StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
/*     */ 
/* 231 */     for (StackTraceElement s : stackTrace) {
/* 232 */       if (AbstractPollingIoProcessor.class.getName().equals(s.getClassName())) {
/* 233 */         IllegalStateException e = new IllegalStateException("t");
/* 234 */         e.getStackTrace();
/* 235 */         throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 244 */     for (StackTraceElement s : stackTrace)
/*     */       try {
/* 246 */         Class cls = DefaultIoFuture.class.getClassLoader().loadClass(s.getClassName());
/* 247 */         if (IoProcessor.class.isAssignableFrom(cls))
/* 248 */           throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
/*     */       }
/*     */       catch (Exception cnfe)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/* 264 */     synchronized (this.lock) {
/* 265 */       return this.ready;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setValue(Object newValue)
/*     */   {
/* 273 */     synchronized (this.lock)
/*     */     {
/* 275 */       if (this.ready) {
/* 276 */         return;
/*     */       }
/*     */ 
/* 279 */       this.result = newValue;
/* 280 */       this.ready = true;
/* 281 */       if (this.waiters > 0) {
/* 282 */         this.lock.notifyAll();
/*     */       }
/*     */     }
/*     */ 
/* 286 */     notifyListeners();
/*     */   }
/*     */ 
/*     */   protected Object getValue()
/*     */   {
/* 293 */     synchronized (this.lock) {
/* 294 */       return this.result;
/*     */     }
/*     */   }
/*     */ 
/*     */   public IoFuture addListener(IoFutureListener<?> listener)
/*     */   {
/* 302 */     if (listener == null) {
/* 303 */       throw new NullPointerException("listener");
/*     */     }
/*     */ 
/* 306 */     boolean notifyNow = false;
/* 307 */     synchronized (this.lock) {
/* 308 */       if (this.ready) {
/* 309 */         notifyNow = true;
/*     */       }
/* 311 */       else if (this.firstListener == null) {
/* 312 */         this.firstListener = listener;
/*     */       } else {
/* 314 */         if (this.otherListeners == null) {
/* 315 */           this.otherListeners = new ArrayList(1);
/*     */         }
/* 317 */         this.otherListeners.add(listener);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 322 */     if (notifyNow) {
/* 323 */       notifyListener(listener);
/*     */     }
/* 325 */     return this;
/*     */   }
/*     */ 
/*     */   public IoFuture removeListener(IoFutureListener<?> listener)
/*     */   {
/* 332 */     if (listener == null) {
/* 333 */       throw new NullPointerException("listener");
/*     */     }
/*     */ 
/* 336 */     synchronized (this.lock) {
/* 337 */       if (!this.ready) {
/* 338 */         if (listener == this.firstListener) {
/* 339 */           if ((this.otherListeners != null) && (!this.otherListeners.isEmpty()))
/* 340 */             this.firstListener = ((IoFutureListener)this.otherListeners.remove(0));
/*     */           else
/* 342 */             this.firstListener = null;
/*     */         }
/* 344 */         else if (this.otherListeners != null) {
/* 345 */           this.otherListeners.remove(listener);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 350 */     return this;
/*     */   }
/*     */ 
/*     */   private void notifyListeners()
/*     */   {
/* 357 */     if (this.firstListener != null) {
/* 358 */       notifyListener(this.firstListener);
/* 359 */       this.firstListener = null;
/*     */ 
/* 361 */       if (this.otherListeners != null) {
/* 362 */         for (IoFutureListener l : this.otherListeners) {
/* 363 */           notifyListener(l);
/*     */         }
/* 365 */         this.otherListeners = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyListener(IoFutureListener l)
/*     */   {
/*     */     try {
/* 373 */       l.operationComplete(this);
/*     */     } catch (Throwable t) {
/* 375 */       ExceptionMonitor.getInstance().exceptionCaught(t);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.DefaultIoFuture
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ 
/*     */ public class UnorderedThreadPoolExecutor extends ThreadPoolExecutor
/*     */ {
/*  57 */   private static final Runnable EXIT_SIGNAL = new Runnable() {
/*     */     public void run() {
/*  59 */       throw new Error("This method shouldn't be called. Please file a bug report."); }  } ;
/*     */ 
/*  65 */   private final Set<Worker> workers = new HashSet();
/*     */   private volatile int corePoolSize;
/*     */   private volatile int maximumPoolSize;
/*     */   private volatile int largestPoolSize;
/*  70 */   private final AtomicInteger idleWorkers = new AtomicInteger();
/*     */   private long completedTaskCount;
/*     */   private volatile boolean shutdown;
/*     */   private final IoEventQueueHandler queueHandler;
/*     */ 
/*  78 */   public UnorderedThreadPoolExecutor() { this(16); }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int maximumPoolSize)
/*     */   {
/*  82 */     this(0, maximumPoolSize);
/*     */   }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
/*  86 */     this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
/*     */   {
/*  91 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory());
/*     */   }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler)
/*     */   {
/*  98 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
/*     */   }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
/*     */   {
/* 105 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
/*     */   }
/*     */ 
/*     */   public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler)
/*     */   {
/* 112 */     super(0, 1, keepAliveTime, unit, new LinkedBlockingQueue(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
/* 113 */     if (corePoolSize < 0) {
/* 114 */       throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
/*     */     }
/*     */ 
/* 117 */     if ((maximumPoolSize == 0) || (maximumPoolSize < corePoolSize)) {
/* 118 */       throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
/*     */     }
/*     */ 
/* 121 */     if (queueHandler == null) {
/* 122 */       queueHandler = IoEventQueueHandler.NOOP;
/*     */     }
/*     */ 
/* 125 */     this.corePoolSize = corePoolSize;
/* 126 */     this.maximumPoolSize = maximumPoolSize;
/* 127 */     this.queueHandler = queueHandler;
/*     */   }
/*     */ 
/*     */   public IoEventQueueHandler getQueueHandler() {
/* 131 */     return this.queueHandler;
/*     */   }
/*     */ 
/*     */   public void setRejectedExecutionHandler(RejectedExecutionHandler handler)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void addWorker()
/*     */   {
/* 140 */     synchronized (this.workers) {
/* 141 */       if (this.workers.size() >= this.maximumPoolSize) {
/* 142 */         return;
/*     */       }
/*     */ 
/* 145 */       Worker worker = new Worker(null);
/* 146 */       Thread thread = getThreadFactory().newThread(worker);
/* 147 */       this.idleWorkers.incrementAndGet();
/* 148 */       thread.start();
/* 149 */       this.workers.add(worker);
/*     */ 
/* 151 */       if (this.workers.size() > this.largestPoolSize)
/* 152 */         this.largestPoolSize = this.workers.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addWorkerIfNecessary()
/*     */   {
/* 158 */     if (this.idleWorkers.get() == 0)
/* 159 */       synchronized (this.workers) {
/* 160 */         if ((this.workers.isEmpty()) || (this.idleWorkers.get() == 0))
/* 161 */           addWorker();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void removeWorker()
/*     */   {
/* 168 */     synchronized (this.workers) {
/* 169 */       if (this.workers.size() <= this.corePoolSize) {
/* 170 */         return;
/*     */       }
/* 172 */       getQueue().offer(EXIT_SIGNAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaximumPoolSize()
/*     */   {
/* 178 */     return this.maximumPoolSize;
/*     */   }
/*     */ 
/*     */   public void setMaximumPoolSize(int maximumPoolSize)
/*     */   {
/* 183 */     if ((maximumPoolSize <= 0) || (maximumPoolSize < this.corePoolSize)) {
/* 184 */       throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
/*     */     }
/*     */ 
/* 188 */     synchronized (this.workers) {
/* 189 */       this.maximumPoolSize = maximumPoolSize;
/* 190 */       int difference = this.workers.size() - maximumPoolSize;
/* 191 */       while (difference > 0) {
/* 192 */         removeWorker();
/* 193 */         difference--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 202 */     long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
/*     */ 
/* 204 */     synchronized (this.workers) {
/* 205 */       while (!isTerminated()) {
/* 206 */         long waitTime = deadline - System.currentTimeMillis();
/* 207 */         if (waitTime <= 0L)
/*     */         {
/*     */           break;
/*     */         }
/* 211 */         this.workers.wait(waitTime);
/*     */       }
/*     */     }
/* 214 */     return isTerminated();
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/* 219 */     return this.shutdown;
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 224 */     if (!this.shutdown) {
/* 225 */       return false;
/*     */     }
/*     */ 
/* 228 */     synchronized (this.workers) {
/* 229 */       return this.workers.isEmpty();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 235 */     if (this.shutdown) {
/* 236 */       return;
/*     */     }
/*     */ 
/* 239 */     this.shutdown = true;
/*     */ 
/* 241 */     synchronized (this.workers) {
/* 242 */       for (int i = this.workers.size(); i > 0; i--)
/* 243 */         getQueue().offer(EXIT_SIGNAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/* 250 */     shutdown();
/*     */ 
/* 252 */     List answer = new ArrayList();
/*     */     Runnable task;
/* 254 */     while ((task = (Runnable)getQueue().poll()) != null) {
/* 255 */       if (task == EXIT_SIGNAL) {
/* 256 */         getQueue().offer(EXIT_SIGNAL);
/* 257 */         Thread.yield();
/* 258 */         continue;
/*     */       }
/*     */ 
/* 261 */       getQueueHandler().polled(this, (IoEvent)task);
/* 262 */       answer.add(task);
/*     */     }
/*     */ 
/* 265 */     return answer;
/*     */   }
/*     */ 
/*     */   public void execute(Runnable task)
/*     */   {
/* 270 */     if (this.shutdown) {
/* 271 */       rejectTask(task);
/*     */     }
/*     */ 
/* 274 */     checkTaskType(task);
/*     */ 
/* 276 */     IoEvent e = (IoEvent)task;
/* 277 */     boolean offeredEvent = this.queueHandler.accept(this, e);
/* 278 */     if (offeredEvent) {
/* 279 */       getQueue().offer(e);
/*     */     }
/*     */ 
/* 282 */     addWorkerIfNecessary();
/*     */ 
/* 284 */     if (offeredEvent)
/* 285 */       this.queueHandler.offered(this, e);
/*     */   }
/*     */ 
/*     */   private void rejectTask(Runnable task)
/*     */   {
/* 290 */     getRejectedExecutionHandler().rejectedExecution(task, this);
/*     */   }
/*     */ 
/*     */   private void checkTaskType(Runnable task) {
/* 294 */     if (!(task instanceof IoEvent))
/* 295 */       throw new IllegalArgumentException("task must be an IoEvent or its subclass.");
/*     */   }
/*     */ 
/*     */   public int getActiveCount()
/*     */   {
/* 301 */     synchronized (this.workers) {
/* 302 */       return this.workers.size() - this.idleWorkers.get();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getCompletedTaskCount()
/*     */   {
/* 308 */     synchronized (this.workers) {
/* 309 */       long answer = this.completedTaskCount;
/* 310 */       for (Worker w : this.workers) {
/* 311 */         answer += w.completedTaskCount;
/*     */       }
/*     */ 
/* 314 */       return answer;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLargestPoolSize()
/*     */   {
/* 320 */     return this.largestPoolSize;
/*     */   }
/*     */ 
/*     */   public int getPoolSize()
/*     */   {
/* 325 */     synchronized (this.workers) {
/* 326 */       return this.workers.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getTaskCount()
/*     */   {
/* 332 */     return getCompletedTaskCount();
/*     */   }
/*     */ 
/*     */   public boolean isTerminating()
/*     */   {
/* 337 */     synchronized (this.workers) {
/* 338 */       return (isShutdown()) && (!isTerminated());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int prestartAllCoreThreads()
/*     */   {
/* 344 */     int answer = 0;
/* 345 */     synchronized (this.workers) {
/* 346 */       for (int i = this.corePoolSize - this.workers.size(); i > 0; i--) {
/* 347 */         addWorker();
/* 348 */         answer++;
/*     */       }
/*     */     }
/* 351 */     return answer;
/*     */   }
/*     */ 
/*     */   public boolean prestartCoreThread()
/*     */   {
/* 356 */     synchronized (this.workers) {
/* 357 */       if (this.workers.size() < this.corePoolSize) {
/* 358 */         addWorker();
/* 359 */         return true;
/*     */       }
/* 361 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void purge()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean remove(Runnable task)
/*     */   {
/* 373 */     boolean removed = super.remove(task);
/* 374 */     if (removed) {
/* 375 */       getQueueHandler().polled(this, (IoEvent)task);
/*     */     }
/* 377 */     return removed;
/*     */   }
/*     */ 
/*     */   public int getCorePoolSize()
/*     */   {
/* 382 */     return this.corePoolSize;
/*     */   }
/*     */ 
/*     */   public void setCorePoolSize(int corePoolSize)
/*     */   {
/* 387 */     if (corePoolSize < 0) {
/* 388 */       throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
/*     */     }
/* 390 */     if (corePoolSize > this.maximumPoolSize) {
/* 391 */       throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
/*     */     }
/*     */ 
/* 394 */     synchronized (this.workers) {
/* 395 */       if (this.corePoolSize > corePoolSize) {
/* 396 */         for (int i = this.corePoolSize - corePoolSize; i > 0; i--) {
/* 397 */           removeWorker();
/*     */         }
/*     */       }
/* 400 */       this.corePoolSize = corePoolSize;
/*     */     }
/*     */   }
/*     */   private class Worker implements Runnable {
/*     */     private volatile long completedTaskCount;
/*     */     private Thread thread;
/*     */ 
/*     */     private Worker() {
/*     */     }
/* 410 */     public void run() { this.thread = Thread.currentThread();
/*     */       try
/*     */       {
/*     */         while (true) {
/* 414 */           Runnable task = fetchTask();
/*     */ 
/* 416 */           UnorderedThreadPoolExecutor.this.idleWorkers.decrementAndGet();
/*     */ 
/* 418 */           if (task == null) {
/* 419 */             synchronized (UnorderedThreadPoolExecutor.this.workers) {
/* 420 */               if (UnorderedThreadPoolExecutor.this.workers.size() > UnorderedThreadPoolExecutor.this.corePoolSize)
/*     */               {
/* 422 */                 UnorderedThreadPoolExecutor.this.workers.remove(this);
/* 423 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 428 */           if (task == UnorderedThreadPoolExecutor.EXIT_SIGNAL) {
/*     */             break;
/*     */           }
/*     */           try
/*     */           {
/* 433 */             if (task != null) {
/* 434 */               UnorderedThreadPoolExecutor.this.queueHandler.polled(UnorderedThreadPoolExecutor.this, (IoEvent)task);
/* 435 */               runTask(task);
/*     */             }
/*     */           } finally {
/* 438 */             UnorderedThreadPoolExecutor.this.idleWorkers.incrementAndGet();
/*     */           }
/*     */         }
/*     */       } finally {
/* 442 */         synchronized (UnorderedThreadPoolExecutor.this.workers) {
/* 443 */           UnorderedThreadPoolExecutor.this.workers.remove(this);
/* 444 */           UnorderedThreadPoolExecutor.access$714(UnorderedThreadPoolExecutor.this, this.completedTaskCount);
/* 445 */           UnorderedThreadPoolExecutor.this.workers.notifyAll();
/*     */         }
/*     */       } }
/*     */ 
/*     */     private Runnable fetchTask()
/*     */     {
/* 451 */       Runnable task = null;
/* 452 */       long currentTime = System.currentTimeMillis();
/* 453 */       long deadline = currentTime + UnorderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
/*     */       while (true) {
/*     */         try {
/* 456 */           long waitTime = deadline - currentTime;
/* 457 */           if (waitTime <= 0L) {
/*     */             break;
/*     */           }
/*     */           try
/*     */           {
/* 462 */             task = (Runnable)UnorderedThreadPoolExecutor.this.getQueue().poll(waitTime, TimeUnit.MILLISECONDS);
/*     */           }
/*     */           finally {
/* 465 */             if (task == null) {
/* 466 */               currentTime = System.currentTimeMillis();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*     */       }
/* 474 */       return task;
/*     */     }
/*     */ 
/*     */     private void runTask(Runnable task) {
/* 478 */       UnorderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
/* 479 */       boolean ran = false;
/*     */       try {
/* 481 */         task.run();
/* 482 */         ran = true;
/* 483 */         UnorderedThreadPoolExecutor.this.afterExecute(task, null);
/* 484 */         this.completedTaskCount += 1L;
/*     */       } catch (RuntimeException e) {
/* 486 */         if (!ran) {
/* 487 */           UnorderedThreadPoolExecutor.this.afterExecute(task, e);
/*     */         }
/* 489 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.UnorderedThreadPoolExecutor
 * JD-Core Version:    0.6.0
 */
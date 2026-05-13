/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.RejectedExecutionHandler;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.DummySession;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class OrderedThreadPoolExecutor extends ThreadPoolExecutor
/*     */ {
/*     */   private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;
/*     */   private static final int DEFAULT_MAX_THREAD_POOL = 16;
/*     */   private static final int DEFAULT_KEEP_ALIVE = 30;
/*  63 */   private static final IoSession EXIT_SIGNAL = new DummySession();
/*     */ 
/*  66 */   private final AttributeKey TASKS_QUEUE = new AttributeKey(getClass(), "tasksQueue");
/*     */ 
/*  68 */   private final BlockingQueue<IoSession> waitingSessions = new LinkedBlockingQueue();
/*     */ 
/*  70 */   private final Set<Worker> workers = new HashSet();
/*     */   private volatile int largestPoolSize;
/*  73 */   private final AtomicInteger idleWorkers = new AtomicInteger();
/*     */   private long completedTaskCount;
/*     */   private volatile boolean shutdown;
/*     */   private final IoEventQueueHandler eventQueueHandler;
/*     */ 
/*     */   public OrderedThreadPoolExecutor()
/*     */   {
/*  89 */     this(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int maximumPoolSize)
/*     */   {
/* 103 */     this(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize)
/*     */   {
/* 117 */     this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
/*     */   {
/* 133 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler)
/*     */   {
/* 151 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
/*     */   {
/* 169 */     this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
/*     */   }
/*     */ 
/*     */   public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler)
/*     */   {
/* 189 */     super(0, 1, keepAliveTime, unit, new SynchronousQueue(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
/*     */ 
/* 192 */     if (corePoolSize < 0) {
/* 193 */       throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
/*     */     }
/*     */ 
/* 196 */     if ((maximumPoolSize == 0) || (maximumPoolSize < corePoolSize)) {
/* 197 */       throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
/*     */     }
/*     */ 
/* 201 */     super.setCorePoolSize(corePoolSize);
/* 202 */     super.setMaximumPoolSize(maximumPoolSize);
/*     */ 
/* 205 */     this.eventQueueHandler = queueHandler;
/*     */   }
/*     */ 
/*     */   public IoEventQueueHandler getQueueHandler()
/*     */   {
/* 213 */     return this.eventQueueHandler;
/*     */   }
/*     */ 
/*     */   public void setRejectedExecutionHandler(RejectedExecutionHandler handler)
/*     */   {
/*     */   }
/*     */ 
/*     */   private void addWorker()
/*     */   {
/* 229 */     synchronized (this.workers) {
/* 230 */       if (this.workers.size() >= super.getMaximumPoolSize()) {
/* 231 */         return;
/*     */       }
/*     */ 
/* 235 */       Worker worker = new Worker(null);
/* 236 */       Thread thread = getThreadFactory().newThread(worker);
/*     */ 
/* 239 */       this.idleWorkers.incrementAndGet();
/*     */ 
/* 242 */       thread.start();
/* 243 */       this.workers.add(worker);
/*     */ 
/* 245 */       if (this.workers.size() > this.largestPoolSize)
/* 246 */         this.largestPoolSize = this.workers.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addWorkerIfNecessary()
/*     */   {
/* 255 */     if (this.idleWorkers.get() == 0)
/* 256 */       synchronized (this.workers) {
/* 257 */         if ((this.workers.isEmpty()) || (this.idleWorkers.get() == 0))
/* 258 */           addWorker();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void removeWorker()
/*     */   {
/* 265 */     synchronized (this.workers) {
/* 266 */       if (this.workers.size() <= super.getCorePoolSize()) {
/* 267 */         return;
/*     */       }
/* 269 */       this.waitingSessions.offer(EXIT_SIGNAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaximumPoolSize()
/*     */   {
/* 278 */     return super.getMaximumPoolSize();
/*     */   }
/*     */ 
/*     */   public void setMaximumPoolSize(int maximumPoolSize)
/*     */   {
/* 286 */     if ((maximumPoolSize <= 0) || (maximumPoolSize < super.getCorePoolSize())) {
/* 287 */       throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
/*     */     }
/*     */ 
/* 291 */     synchronized (this.workers) {
/* 292 */       super.setMaximumPoolSize(maximumPoolSize);
/* 293 */       int difference = this.workers.size() - maximumPoolSize;
/* 294 */       while (difference > 0) {
/* 295 */         removeWorker();
/* 296 */         difference--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 308 */     long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
/*     */ 
/* 310 */     synchronized (this.workers) {
/* 311 */       while (!isTerminated()) {
/* 312 */         long waitTime = deadline - System.currentTimeMillis();
/* 313 */         if (waitTime <= 0L)
/*     */         {
/*     */           break;
/*     */         }
/* 317 */         this.workers.wait(waitTime);
/*     */       }
/*     */     }
/* 320 */     return isTerminated();
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/* 328 */     return this.shutdown;
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/* 336 */     if (!this.shutdown) {
/* 337 */       return false;
/*     */     }
/*     */ 
/* 340 */     synchronized (this.workers) {
/* 341 */       return this.workers.isEmpty();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 350 */     if (this.shutdown) {
/* 351 */       return;
/*     */     }
/*     */ 
/* 354 */     this.shutdown = true;
/*     */ 
/* 356 */     synchronized (this.workers) {
/* 357 */       for (int i = this.workers.size(); i > 0; i--)
/* 358 */         this.waitingSessions.offer(EXIT_SIGNAL);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/* 368 */     shutdown();
/*     */ 
/* 370 */     List answer = new ArrayList();
/*     */     IoSession session;
/* 373 */     while ((session = (IoSession)this.waitingSessions.poll()) != null) {
/* 374 */       if (session == EXIT_SIGNAL) {
/* 375 */         this.waitingSessions.offer(EXIT_SIGNAL);
/* 376 */         Thread.yield();
/* 377 */         continue;
/*     */       }
/*     */ 
/* 380 */       Queue tasksQueue = (Queue)session.getAttribute(this.TASKS_QUEUE);
/*     */ 
/* 382 */       synchronized (tasksQueue)
/*     */       {
/* 384 */         for (Runnable task : tasksQueue) {
/* 385 */           getQueueHandler().polled(this, (IoEvent)task);
/* 386 */           answer.add(task);
/*     */         }
/*     */ 
/* 389 */         tasksQueue.clear();
/*     */       }
/*     */     }
/*     */ 
/* 393 */     return answer;
/*     */   }
/*     */ 
/*     */   public void execute(Runnable task)
/*     */   {
/* 401 */     if (this.shutdown) {
/* 402 */       rejectTask(task);
/*     */     }
/*     */ 
/* 406 */     checkTaskType(task);
/*     */ 
/* 408 */     IoEvent event = (IoEvent)task;
/* 409 */     IoSession session = event.getSession();
/*     */ 
/* 412 */     Queue tasksQueue = getTasksQueue(session);
/*     */ 
/* 414 */     boolean offerEvent = true;
/*     */ 
/* 419 */     if (this.eventQueueHandler != null)
/* 420 */       offerEvent = this.eventQueueHandler.accept(this, event);
/*     */     boolean offerSession;
/* 423 */     if (offerEvent)
/*     */     {
/* 425 */       synchronized (tasksQueue) {
/* 426 */         boolean offerSession = tasksQueue.isEmpty();
/*     */ 
/* 429 */         tasksQueue.offer(event);
/*     */       }
/*     */     }
/* 432 */     else offerSession = false;
/*     */ 
/* 435 */     if (offerSession) {
/* 436 */       this.waitingSessions.offer(session);
/*     */     }
/*     */ 
/* 439 */     addWorkerIfNecessary();
/*     */ 
/* 441 */     if ((offerEvent) && 
/* 442 */       (this.eventQueueHandler != null))
/* 443 */       this.eventQueueHandler.offered(this, event);
/*     */   }
/*     */ 
/*     */   private void rejectTask(Runnable task)
/*     */   {
/* 449 */     getRejectedExecutionHandler().rejectedExecution(task, this);
/*     */   }
/*     */ 
/*     */   private void checkTaskType(Runnable task) {
/* 453 */     if (!(task instanceof IoEvent))
/* 454 */       throw new IllegalArgumentException("task must be an IoEvent or its subclass.");
/*     */   }
/*     */ 
/*     */   public int getActiveCount()
/*     */   {
/* 463 */     synchronized (this.workers) {
/* 464 */       return this.workers.size() - this.idleWorkers.get();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getCompletedTaskCount()
/*     */   {
/* 473 */     synchronized (this.workers) {
/* 474 */       long answer = this.completedTaskCount;
/* 475 */       for (Worker w : this.workers) {
/* 476 */         answer += w.completedTaskCount;
/*     */       }
/*     */ 
/* 479 */       return answer;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getLargestPoolSize()
/*     */   {
/* 488 */     return this.largestPoolSize;
/*     */   }
/*     */ 
/*     */   public int getPoolSize()
/*     */   {
/* 496 */     synchronized (this.workers) {
/* 497 */       return this.workers.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getTaskCount()
/*     */   {
/* 506 */     return getCompletedTaskCount();
/*     */   }
/*     */ 
/*     */   public boolean isTerminating()
/*     */   {
/* 514 */     synchronized (this.workers) {
/* 515 */       return (isShutdown()) && (!isTerminated());
/*     */     }
/*     */   }
/*     */ 
/*     */   public int prestartAllCoreThreads()
/*     */   {
/* 524 */     int answer = 0;
/* 525 */     synchronized (this.workers) {
/* 526 */       for (int i = super.getCorePoolSize() - this.workers.size(); i > 0; i--) {
/* 527 */         addWorker();
/* 528 */         answer++;
/*     */       }
/*     */     }
/* 531 */     return answer;
/*     */   }
/*     */ 
/*     */   public boolean prestartCoreThread()
/*     */   {
/* 539 */     synchronized (this.workers) {
/* 540 */       if (this.workers.size() < super.getCorePoolSize()) {
/* 541 */         addWorker();
/* 542 */         return true;
/*     */       }
/* 544 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public BlockingQueue<Runnable> getQueue()
/*     */   {
/* 554 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void purge()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean remove(Runnable task)
/*     */   {
/* 570 */     checkTaskType(task);
/* 571 */     IoEvent event = (IoEvent)task;
/* 572 */     IoSession session = event.getSession();
/* 573 */     Queue tasksQueue = (Queue)session.getAttribute(this.TASKS_QUEUE);
/*     */ 
/* 575 */     if (tasksQueue == null)
/* 576 */       return false;
/*     */     boolean removed;
/* 581 */     synchronized (tasksQueue) {
/* 582 */       removed = tasksQueue.remove(task);
/*     */     }
/*     */ 
/* 585 */     if (removed) {
/* 586 */       getQueueHandler().polled(this, event);
/*     */     }
/*     */ 
/* 589 */     return removed;
/*     */   }
/*     */ 
/*     */   public int getCorePoolSize()
/*     */   {
/* 597 */     return super.getCorePoolSize();
/*     */   }
/*     */ 
/*     */   public void setCorePoolSize(int corePoolSize)
/*     */   {
/* 605 */     if (corePoolSize < 0) {
/* 606 */       throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
/*     */     }
/* 608 */     if (corePoolSize > super.getMaximumPoolSize()) {
/* 609 */       throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
/*     */     }
/*     */ 
/* 612 */     synchronized (this.workers) {
/* 613 */       if (super.getCorePoolSize() > corePoolSize) {
/* 614 */         for (int i = super.getCorePoolSize() - corePoolSize; i > 0; i--) {
/* 615 */           removeWorker();
/*     */         }
/*     */       }
/* 618 */       super.setCorePoolSize(corePoolSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Queue<Runnable> getTasksQueue(IoSession session) {
/* 623 */     Queue tasksQueue = (Queue)session.getAttribute(this.TASKS_QUEUE);
/*     */ 
/* 625 */     if (tasksQueue == null) {
/* 626 */       tasksQueue = new ConcurrentLinkedQueue();
/* 627 */       Queue oldTasksQueue = (Queue)session.setAttributeIfAbsent(this.TASKS_QUEUE, tasksQueue);
/*     */ 
/* 629 */       if (oldTasksQueue != null) {
/* 630 */         tasksQueue = oldTasksQueue;
/*     */       }
/*     */     }
/*     */ 
/* 634 */     return tasksQueue;
/*     */   }
/*     */   private class Worker implements Runnable {
/*     */     private volatile long completedTaskCount;
/*     */     private Thread thread;
/*     */ 
/*     */     private Worker() {  }
/*     */ 
/* 643 */     public void run() { this.thread = Thread.currentThread();
/*     */       try
/*     */       {
/*     */         while (true) {
/* 647 */           IoSession session = fetchSession();
/*     */ 
/* 649 */           OrderedThreadPoolExecutor.this.idleWorkers.decrementAndGet();
/*     */ 
/* 651 */           if (session == null) {
/* 652 */             synchronized (OrderedThreadPoolExecutor.this.workers) {
/* 653 */               if (OrderedThreadPoolExecutor.this.workers.size() > OrderedThreadPoolExecutor.this.getCorePoolSize())
/*     */               {
/* 655 */                 OrderedThreadPoolExecutor.this.workers.remove(this);
/* 656 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 661 */           if (session == OrderedThreadPoolExecutor.EXIT_SIGNAL) {
/*     */             break;
/*     */           }
/*     */           try
/*     */           {
/* 666 */             if (session != null)
/* 667 */               runTasks(OrderedThreadPoolExecutor.this.getTasksQueue(session));
/*     */           }
/*     */           finally {
/* 670 */             OrderedThreadPoolExecutor.this.idleWorkers.incrementAndGet();
/*     */           }
/*     */         }
/*     */       } finally {
/* 674 */         synchronized (OrderedThreadPoolExecutor.this.workers) {
/* 675 */           OrderedThreadPoolExecutor.this.workers.remove(this);
/* 676 */           OrderedThreadPoolExecutor.access$614(OrderedThreadPoolExecutor.this, this.completedTaskCount);
/* 677 */           OrderedThreadPoolExecutor.this.workers.notifyAll();
/*     */         }
/*     */       } }
/*     */ 
/*     */     private IoSession fetchSession()
/*     */     {
/* 683 */       IoSession session = null;
/* 684 */       long currentTime = System.currentTimeMillis();
/* 685 */       long deadline = currentTime + OrderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
/*     */       while (true) {
/*     */         try {
/* 688 */           long waitTime = deadline - currentTime;
/* 689 */           if (waitTime <= 0L) {
/*     */             break;
/*     */           }
/*     */           try
/*     */           {
/* 694 */             session = (IoSession)OrderedThreadPoolExecutor.this.waitingSessions.poll(waitTime, TimeUnit.MILLISECONDS);
/*     */           }
/*     */           finally {
/* 697 */             if (session == null) {
/* 698 */               currentTime = System.currentTimeMillis();
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/*     */       }
/* 706 */       return session;
/*     */     }
/*     */ 
/*     */     private void runTasks(Queue<Runnable> tasksQueue)
/*     */     {
/*     */       while (true)
/*     */       {
/*     */         Runnable task;
/* 713 */         synchronized (tasksQueue) {
/* 714 */           if (tasksQueue.isEmpty())
/*     */           {
/*     */             break;
/*     */           }
/* 718 */           task = (Runnable)tasksQueue.poll();
/*     */ 
/* 720 */           if (task == null) {
/* 721 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 725 */         if (OrderedThreadPoolExecutor.this.eventQueueHandler != null) {
/* 726 */           OrderedThreadPoolExecutor.this.eventQueueHandler.polled(OrderedThreadPoolExecutor.this, (IoEvent)task);
/*     */         }
/*     */ 
/* 729 */         runTask(task);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void runTask(Runnable task) {
/* 734 */       OrderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
/* 735 */       boolean ran = false;
/*     */       try {
/* 737 */         task.run();
/* 738 */         ran = true;
/* 739 */         OrderedThreadPoolExecutor.this.afterExecute(task, null);
/* 740 */         this.completedTaskCount += 1L;
/*     */       } catch (RuntimeException e) {
/* 742 */         if (!ran) {
/* 743 */           OrderedThreadPoolExecutor.this.afterExecute(task, e);
/*     */         }
/* 745 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.OrderedThreadPoolExecutor
 * JD-Core Version:    0.6.0
 */
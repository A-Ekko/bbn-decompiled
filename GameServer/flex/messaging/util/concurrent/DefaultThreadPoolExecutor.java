/*     */ package flex.messaging.util.concurrent;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionException;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.RejectedExecutionHandler;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.SynchronousQueue;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ 
/*     */ public class DefaultThreadPoolExecutor extends ThreadPoolExecutor
/*     */   implements Executor
/*     */ {
/*  94 */   private final Object lock = new Object();
/*     */   private FailedExecutionHandler handler;
/*     */ 
/*     */   public DefaultThreadPoolExecutor()
/*     */   {
/*  50 */     super(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue());
/*     */   }
/*     */ 
/*     */   public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue)
/*     */   {
/*  58 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
/*     */   }
/*     */ 
/*     */   public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, RejectedExecutionHandler handler)
/*     */   {
/*  66 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
/*     */   }
/*     */ 
/*     */   public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory)
/*     */   {
/*  74 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
/*     */   }
/*     */ 
/*     */   public DefaultThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
/*     */   {
/*  82 */     super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
/*     */   }
/*     */ 
/*     */   public FailedExecutionHandler getFailedExecutionHandler()
/*     */   {
/* 113 */     synchronized (this.lock)
/*     */     {
/* 115 */       return this.handler;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFailedExecutionHandler(FailedExecutionHandler value)
/*     */   {
/* 124 */     synchronized (this.lock)
/*     */     {
/* 126 */       this.handler = value;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void execute(Runnable command)
/*     */   {
/*     */     try
/*     */     {
/* 145 */       super.execute(command);
/*     */     }
/*     */     catch (RejectedExecutionException e)
/*     */     {
/* 149 */       FailedExecutionHandler handler = getFailedExecutionHandler();
/* 150 */       if (handler != null)
/*     */       {
/* 152 */         handler.failedExecution(command, this, e);
/*     */       }
/* 154 */       else if (Log.isError())
/*     */       {
/* 156 */         Log.getLogger("Executor").error("DefaultThreadPoolExecutor hit a RejectedExecutionException but no FailedExecutionHandler is registered to handle the error.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.concurrent.DefaultThreadPoolExecutor
 * JD-Core Version:    0.6.0
 */
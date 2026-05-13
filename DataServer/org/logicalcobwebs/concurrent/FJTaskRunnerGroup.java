/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class FJTaskRunnerGroup
/*     */   implements Executor
/*     */ {
/*     */   protected final FJTaskRunner[] threads;
/* 128 */   protected final LinkedQueue entryQueue = new LinkedQueue();
/*     */ 
/* 131 */   protected int activeCount = 0;
/*     */ 
/* 136 */   protected int nstarted = 0;
/*     */   static final boolean COLLECT_STATS = true;
/* 151 */   long initTime = 0L;
/*     */ 
/* 154 */   int entries = 0;
/*     */   static final int DEFAULT_SCAN_PRIORITY = 2;
/*     */   static final long SCANS_PER_SLEEP = 15L;
/*     */   static final long MAX_SLEEP_TIME = 100L;
/*     */ 
/*     */   public FJTaskRunnerGroup(int groupSize)
/*     */   {
/* 169 */     this.threads = new FJTaskRunner[groupSize];
/* 170 */     initializeThreads();
/* 171 */     this.initTime = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   public void execute(Runnable r)
/*     */     throws InterruptedException
/*     */   {
/* 184 */     if ((r instanceof FJTask))
/* 185 */       this.entryQueue.put((FJTask)r);
/*     */     else {
/* 187 */       this.entryQueue.put(new FJTask.Wrap(r));
/*     */     }
/* 189 */     signalNewTask();
/*     */   }
/*     */ 
/*     */   public void executeTask(FJTask t)
/*     */   {
/*     */     try
/*     */     {
/* 198 */       this.entryQueue.put(t);
/* 199 */       signalNewTask();
/*     */     } catch (InterruptedException ex) {
/* 201 */       Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invoke(Runnable r)
/*     */     throws InterruptedException
/*     */   {
/* 213 */     InvokableFJTask w = new InvokableFJTask(r);
/* 214 */     this.entryQueue.put(w);
/* 215 */     signalNewTask();
/* 216 */     w.awaitTermination();
/*     */   }
/*     */ 
/*     */   public void interruptAll()
/*     */   {
/* 235 */     Thread current = Thread.currentThread();
/* 236 */     boolean stopCurrent = false;
/*     */ 
/* 238 */     for (int i = 0; i < this.threads.length; i++) {
/* 239 */       Thread t = this.threads[i];
/* 240 */       if (t == current)
/* 241 */         stopCurrent = true;
/*     */       else
/* 243 */         t.interrupt();
/*     */     }
/* 245 */     if (stopCurrent)
/* 246 */       current.interrupt();
/*     */   }
/*     */ 
/*     */   public synchronized void setScanPriorities(int pri)
/*     */   {
/* 258 */     for (int i = 0; i < this.threads.length; i++) {
/* 259 */       FJTaskRunner t = this.threads[i];
/* 260 */       t.setScanPriority(pri);
/* 261 */       if (t.active) continue; t.setPriority(pri);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setRunPriorities(int pri)
/*     */   {
/* 275 */     for (int i = 0; i < this.threads.length; i++) {
/* 276 */       FJTaskRunner t = this.threads[i];
/* 277 */       t.setRunPriority(pri);
/* 278 */       if (!t.active) continue; t.setPriority(pri);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 286 */     return this.threads.length;
/*     */   }
/*     */ 
/*     */   public synchronized int getActiveCount()
/*     */   {
/* 298 */     return this.activeCount;
/*     */   }
/*     */ 
/*     */   public void stats()
/*     */   {
/* 369 */     long time = System.currentTimeMillis() - this.initTime;
/* 370 */     double secs = time / 1000.0D;
/* 371 */     long totalRuns = 0L;
/* 372 */     long totalScans = 0L;
/* 373 */     long totalSteals = 0L;
/*     */ 
/* 375 */     System.out.print("Thread\tQ Cap\tScans\tNew\tRuns\n");
/*     */ 
/* 382 */     for (int i = 0; i < this.threads.length; i++) {
/* 383 */       FJTaskRunner t = this.threads[i];
/* 384 */       int truns = t.runs;
/* 385 */       totalRuns += truns;
/*     */ 
/* 387 */       int tscans = t.scans;
/* 388 */       totalScans += tscans;
/*     */ 
/* 390 */       int tsteals = t.steals;
/* 391 */       totalSteals += tsteals;
/*     */ 
/* 393 */       String star = getActive(t) ? "*" : " ";
/*     */ 
/* 396 */       System.out.print("T" + i + star + "\t" + t.deqSize() + "\t" + tscans + "\t" + tsteals + "\t" + truns + "\n");
/*     */     }
/*     */ 
/* 404 */     System.out.print("Total\t    \t" + totalScans + "\t" + totalSteals + "\t" + totalRuns + "\n");
/*     */ 
/* 411 */     System.out.print("Execute: " + this.entries);
/*     */ 
/* 413 */     System.out.print("\tTime: " + secs);
/*     */ 
/* 415 */     long rps = 0L;
/* 416 */     if (secs != 0.0D) rps = Math.round(totalRuns / secs);
/*     */ 
/* 418 */     System.out.println("\tRate: " + rps);
/*     */   }
/*     */ 
/*     */   protected FJTaskRunner[] getArray()
/*     */   {
/* 431 */     return this.threads;
/*     */   }
/*     */ 
/*     */   protected FJTask pollEntryQueue()
/*     */   {
/*     */     try
/*     */     {
/* 442 */       FJTask t = (FJTask)(FJTask)this.entryQueue.poll(0L);
/* 443 */       return t;
/*     */     } catch (InterruptedException ex) {
/* 445 */       Thread.currentThread().interrupt();
/* 446 */     }return null;
/*     */   }
/*     */ 
/*     */   protected synchronized boolean getActive(FJTaskRunner t)
/*     */   {
/* 458 */     return t.active;
/*     */   }
/*     */ 
/*     */   protected synchronized void setActive(FJTaskRunner t)
/*     */   {
/* 468 */     if (!t.active) {
/* 469 */       t.active = true;
/* 470 */       this.activeCount += 1;
/* 471 */       if (this.nstarted < this.threads.length)
/* 472 */         this.threads[(this.nstarted++)].start();
/*     */       else
/* 474 */         notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void setInactive(FJTaskRunner t)
/*     */   {
/* 483 */     if (t.active) {
/* 484 */       t.active = false;
/* 485 */       this.activeCount -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void checkActive(FJTaskRunner t, long scans)
/*     */   {
/* 538 */     setInactive(t);
/*     */     try
/*     */     {
/* 542 */       if ((this.activeCount == 0) && (this.entryQueue.peek() == null)) {
/* 543 */         wait();
/*     */       }
/*     */       else
/*     */       {
/* 548 */         long msecs = scans / 15L;
/* 549 */         if (msecs > 100L) msecs = 100L;
/* 550 */         int nsecs = msecs == 0L ? 1 : 0;
/* 551 */         wait(msecs, nsecs);
/*     */       }
/*     */     } catch (InterruptedException ex) {
/* 554 */       notify();
/* 555 */       Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void signalNewTask()
/*     */   {
/* 566 */     this.entries += 1;
/* 567 */     if (this.nstarted < this.threads.length)
/* 568 */       this.threads[(this.nstarted++)].start();
/*     */     else
/* 570 */       notify();
/*     */   }
/*     */ 
/*     */   protected void initializeThreads()
/*     */   {
/* 578 */     for (int i = 0; i < this.threads.length; i++) this.threads[i] = new FJTaskRunner(this);
/*     */   }
/*     */ 
/*     */   protected static final class InvokableFJTask extends FJTask
/*     */   {
/*     */     protected final Runnable wrapped;
/* 588 */     protected boolean terminated = false;
/*     */ 
/*     */     protected InvokableFJTask(Runnable r) {
/* 591 */       this.wrapped = r;
/*     */     }
/*     */ 
/*     */     public void run() {
/*     */       try {
/* 596 */         if ((this.wrapped instanceof FJTask))
/* 597 */           FJTask.invoke((FJTask)(FJTask)this.wrapped);
/*     */         else
/* 599 */           this.wrapped.run();
/*     */       } finally {
/* 601 */         setTerminated();
/*     */       }
/*     */     }
/*     */ 
/*     */     protected synchronized void setTerminated() {
/* 606 */       this.terminated = true;
/* 607 */       notifyAll();
/*     */     }
/*     */ 
/*     */     protected synchronized void awaitTermination() throws InterruptedException {
/* 611 */       while (!this.terminated) wait();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.FJTaskRunnerGroup
 * JD-Core Version:    0.6.0
 */
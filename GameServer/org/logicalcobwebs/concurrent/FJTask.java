/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ public abstract class FJTask
/*     */   implements Runnable
/*     */ {
/*     */   private volatile boolean done;
/*     */ 
/*     */   public static FJTaskRunner getFJTaskRunner()
/*     */   {
/* 154 */     return (FJTaskRunner)(FJTaskRunner)Thread.currentThread();
/*     */   }
/*     */ 
/*     */   public static FJTaskRunnerGroup getFJTaskRunnerGroup()
/*     */   {
/* 163 */     return getFJTaskRunner().getGroup();
/*     */   }
/*     */ 
/*     */   public final boolean isDone()
/*     */   {
/* 177 */     return this.done;
/*     */   }
/*     */ 
/*     */   protected final void setDone()
/*     */   {
/* 187 */     this.done = true;
/*     */   }
/*     */ 
/*     */   public void cancel()
/*     */   {
/* 202 */     setDone();
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 217 */     this.done = false;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 234 */     getFJTaskRunnerGroup().executeTask(this);
/*     */   }
/*     */ 
/*     */   public void fork()
/*     */   {
/* 261 */     getFJTaskRunner().push(this);
/*     */   }
/*     */ 
/*     */   public static void yield()
/*     */   {
/* 292 */     getFJTaskRunner().taskYield();
/*     */   }
/*     */ 
/*     */   public void join()
/*     */   {
/* 303 */     getFJTaskRunner().taskJoin(this);
/*     */   }
/*     */ 
/*     */   public static void invoke(FJTask t)
/*     */   {
/* 318 */     if (!t.isDone()) {
/* 319 */       t.run();
/* 320 */       t.setDone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void coInvoke(FJTask task1, FJTask task2)
/*     */   {
/* 384 */     getFJTaskRunner().coInvoke(task1, task2);
/*     */   }
/*     */ 
/*     */   public static void coInvoke(FJTask[] tasks)
/*     */   {
/* 398 */     getFJTaskRunner().coInvoke(tasks);
/*     */   }
/*     */ 
/*     */   public static FJTask seq(FJTask[] tasks)
/*     */   {
/* 455 */     return new Seq(tasks);
/*     */   }
/*     */ 
/*     */   public static FJTask par(FJTask[] tasks)
/*     */   {
/* 495 */     return new Par(tasks);
/*     */   }
/*     */ 
/*     */   public static FJTask seq(FJTask task1, FJTask task2)
/*     */   {
/* 525 */     return new Seq2(task1, task2);
/*     */   }
/*     */ 
/*     */   public static FJTask par(FJTask task1, FJTask task2)
/*     */   {
/* 554 */     return new Par2(task1, task2);
/*     */   }
/*     */ 
/*     */   public static class Par2 extends FJTask
/*     */   {
/*     */     protected final FJTask fst;
/*     */     protected final FJTask snd;
/*     */ 
/*     */     public Par2(FJTask task1, FJTask task2)
/*     */     {
/* 539 */       this.fst = task1;
/* 540 */       this.snd = task2;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 544 */       FJTask.coInvoke(this.fst, this.snd);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Seq2 extends FJTask
/*     */   {
/*     */     protected final FJTask fst;
/*     */     protected final FJTask snd;
/*     */ 
/*     */     public Seq2(FJTask task1, FJTask task2)
/*     */     {
/* 509 */       this.fst = task1;
/* 510 */       this.snd = task2;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 514 */       FJTask.invoke(this.fst);
/* 515 */       FJTask.invoke(this.snd);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Par extends FJTask
/*     */   {
/*     */     protected final FJTask[] tasks;
/*     */ 
/*     */     public Par(FJTask[] tasks)
/*     */     {
/* 473 */       this.tasks = tasks;
/*     */     }
/*     */ 
/*     */     public Par(FJTask task1, FJTask task2)
/*     */     {
/* 480 */       this.tasks = new FJTask[] { task1, task2 };
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 485 */       FJTask.coInvoke(this.tasks);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Seq extends FJTask
/*     */   {
/*     */     protected final FJTask[] tasks;
/*     */ 
/*     */     public Seq(FJTask[] tasks)
/*     */     {
/* 434 */       this.tasks = tasks;
/*     */     }
/*     */ 
/*     */     public Seq(FJTask task1, FJTask task2)
/*     */     {
/* 441 */       this.tasks = new FJTask[] { task1, task2 };
/*     */     }
/*     */ 
/*     */     public void run() {
/* 445 */       for (int i = 0; i < this.tasks.length; i++) FJTask.invoke(this.tasks[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Wrap extends FJTask
/*     */   {
/*     */     protected final Runnable runnable;
/*     */ 
/*     */     public Wrap(Runnable r)
/*     */     {
/* 411 */       this.runnable = r;
/*     */     }
/*     */ 
/*     */     public void run() {
/* 415 */       this.runnable.run();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.FJTask
 * JD-Core Version:    0.6.0
 */
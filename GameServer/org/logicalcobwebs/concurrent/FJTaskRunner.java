/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ import java.util.Random;
/*     */ 
/*     */ public class FJTaskRunner extends Thread
/*     */ {
/*     */   protected final FJTaskRunnerGroup group;
/*     */   protected static final int INITIAL_CAPACITY = 4096;
/*     */   protected static final int MAX_CAPACITY = 1073741824;
/* 297 */   protected VolatileTaskRef[] deq = VolatileTaskRef.newArray(4096);
/*     */ 
/* 314 */   protected volatile int top = 0;
/*     */ 
/* 322 */   protected volatile int base = 0;
/*     */ 
/* 330 */   protected final Object barrier = new Object();
/*     */ 
/* 341 */   protected boolean active = false;
/*     */   protected final Random victimRNG;
/* 348 */   protected int scanPriority = 2;
/*     */   protected int runPriority;
/*     */   static final boolean COLLECT_STATS = true;
/* 388 */   protected int runs = 0;
/*     */ 
/* 391 */   protected int scans = 0;
/*     */ 
/* 394 */   protected int steals = 0;
/*     */ 
/*     */   protected FJTaskRunner(FJTaskRunnerGroup g)
/*     */   {
/* 219 */     this.group = g;
/* 220 */     this.victimRNG = new Random(System.identityHashCode(this));
/* 221 */     this.runPriority = getPriority();
/* 222 */     setDaemon(true);
/*     */   }
/*     */ 
/*     */   protected final FJTaskRunnerGroup getGroup()
/*     */   {
/* 230 */     return this.group;
/*     */   }
/*     */ 
/*     */   protected int deqSize()
/*     */   {
/* 301 */     return this.deq.length;
/*     */   }
/*     */ 
/*     */   protected void setScanPriority(int pri)
/*     */   {
/* 362 */     this.scanPriority = pri;
/*     */   }
/*     */ 
/*     */   protected void setRunPriority(int pri)
/*     */   {
/* 371 */     this.runPriority = pri;
/*     */   }
/*     */ 
/*     */   protected final void push(FJTask r)
/*     */   {
/* 408 */     int t = this.top;
/*     */ 
/* 418 */     if (t < (this.base & this.deq.length - 1) + this.deq.length)
/*     */     {
/* 420 */       this.deq[(t & this.deq.length - 1)].put(r);
/* 421 */       this.top = (t + 1);
/*     */     } else {
/* 423 */       slowPush(r);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void slowPush(FJTask r)
/*     */   {
/* 432 */     checkOverflow();
/* 433 */     push(r);
/*     */   }
/*     */ 
/*     */   protected final synchronized void put(FJTask r)
/*     */   {
/*     */     while (true)
/*     */     {
/* 447 */       int b = this.base - 1;
/* 448 */       if (this.top < b + this.deq.length)
/*     */       {
/* 450 */         int newBase = b & this.deq.length - 1;
/* 451 */         this.deq[newBase].put(r);
/* 452 */         this.base = newBase;
/*     */ 
/* 454 */         if (b != newBase) {
/* 455 */           int newTop = this.top & this.deq.length - 1;
/* 456 */           if (newTop < newBase) newTop += this.deq.length;
/* 457 */           this.top = newTop;
/*     */         }
/* 459 */         return;
/*     */       }
/* 461 */       checkOverflow();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final FJTask pop()
/*     */   {
/* 485 */     int t = --this.top;
/*     */ 
/* 496 */     if (this.base + 1 < t) {
/* 497 */       return this.deq[(t & this.deq.length - 1)].take();
/*     */     }
/* 499 */     return confirmPop(t);
/*     */   }
/*     */ 
/*     */   protected final synchronized FJTask confirmPop(int provisionalTop)
/*     */   {
/* 510 */     if (this.base <= provisionalTop) {
/* 511 */       return this.deq[(provisionalTop & this.deq.length - 1)].take();
/*     */     }
/*     */ 
/* 519 */     this.top = (this.base = 0);
/* 520 */     return null;
/*     */   }
/*     */ 
/*     */   protected final synchronized FJTask take()
/*     */   {
/* 537 */     int b = this.base++;
/*     */ 
/* 539 */     if (b < this.top) {
/* 540 */       return confirmTake(b);
/*     */     }
/*     */ 
/* 543 */     this.base = b;
/* 544 */     return null;
/*     */   }
/*     */ 
/*     */   protected FJTask confirmTake(int oldBase)
/*     */   {
/* 561 */     synchronized (this.barrier) {
/* 562 */       if (oldBase < this.top)
/*     */       {
/* 572 */         return this.deq[(oldBase & this.deq.length - 1)].get();
/*     */       }
/* 574 */       this.base = oldBase;
/* 575 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void checkOverflow()
/*     */   {
/* 589 */     int t = this.top;
/* 590 */     int b = this.base;
/*     */ 
/* 592 */     if (t - b < this.deq.length - 1)
/*     */     {
/* 594 */       int newBase = b & this.deq.length - 1;
/* 595 */       int newTop = this.top & this.deq.length - 1;
/* 596 */       if (newTop < newBase) newTop += this.deq.length;
/* 597 */       this.top = newTop;
/* 598 */       this.base = newBase;
/*     */ 
/* 605 */       int i = newBase;
/* 606 */       while ((i != newTop) && (this.deq[i].ref != null)) {
/* 607 */         this.deq[i].ref = null;
/* 608 */         i = i - 1 & this.deq.length - 1;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 613 */       int newTop = t - b;
/* 614 */       int oldcap = this.deq.length;
/* 615 */       int newcap = oldcap * 2;
/*     */ 
/* 617 */       if (newcap >= 1073741824) {
/* 618 */         throw new Error("FJTask queue maximum capacity exceeded");
/*     */       }
/* 620 */       VolatileTaskRef[] newdeq = new VolatileTaskRef[newcap];
/*     */ 
/* 623 */       for (int j = 0; j < oldcap; j++) newdeq[j] = this.deq[(b++ & oldcap - 1)];
/*     */ 
/* 626 */       for (int j = oldcap; j < newcap; j++) newdeq[j] = new VolatileTaskRef();
/*     */ 
/* 628 */       this.deq = newdeq;
/* 629 */       this.base = 0;
/* 630 */       this.top = newTop;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void scan(FJTask waitingFor)
/*     */   {
/* 653 */     FJTask task = null;
/*     */ 
/* 656 */     boolean lowered = false;
/*     */ 
/* 668 */     FJTaskRunner[] ts = this.group.getArray();
/* 669 */     int idx = this.victimRNG.nextInt(ts.length);
/*     */ 
/* 671 */     for (int i = 0; i < ts.length; i++)
/*     */     {
/* 673 */       FJTaskRunner t = ts[idx];
/* 674 */       idx++; if (idx >= ts.length) idx = 0;
/*     */ 
/* 676 */       if ((t == null) || (t == this))
/*     */         continue;
/* 678 */       if ((waitingFor != null) && (waitingFor.isDone())) {
/*     */         break;
/*     */       }
/* 681 */       this.scans += 1;
/* 682 */       task = t.take();
/* 683 */       if (task != null) {
/* 684 */         this.steals += 1;
/* 685 */         break;
/* 686 */       }if (isInterrupted())
/*     */         break;
/* 688 */       if (!lowered) {
/* 689 */         lowered = true;
/* 690 */         setPriority(this.scanPriority);
/*     */       } else {
/* 692 */         yield();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 699 */     if (task == null) {
/* 700 */       this.scans += 1;
/* 701 */       task = this.group.pollEntryQueue();
/* 702 */       if (task != null) this.steals += 1;
/*     */     }
/*     */ 
/* 705 */     if (lowered) setPriority(this.runPriority);
/*     */ 
/* 707 */     if ((task != null) && (!task.isDone())) {
/* 708 */       this.runs += 1;
/* 709 */       task.run();
/* 710 */       task.setDone();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void scanWhileIdling()
/*     */   {
/* 727 */     FJTask task = null;
/*     */ 
/* 729 */     boolean lowered = false;
/* 730 */     long iters = 0L;
/*     */ 
/* 732 */     FJTaskRunner[] ts = this.group.getArray();
/* 733 */     int idx = this.victimRNG.nextInt(ts.length);
/*     */     do
/*     */     {
/* 736 */       for (int i = 0; i < ts.length; i++)
/*     */       {
/* 738 */         FJTaskRunner t = ts[idx];
/* 739 */         idx++; if (idx >= ts.length) idx = 0;
/*     */ 
/* 741 */         if ((t != null) && (t != this)) {
/* 742 */           this.scans += 1;
/*     */ 
/* 744 */           task = t.take();
/* 745 */           if (task != null) {
/* 746 */             this.steals += 1;
/* 747 */             if (lowered) setPriority(this.runPriority);
/* 748 */             this.group.setActive(this);
/* 749 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 754 */       if (task == null) {
/* 755 */         if (isInterrupted()) {
/* 756 */           return;
/*     */         }
/* 758 */         this.scans += 1;
/* 759 */         task = this.group.pollEntryQueue();
/*     */ 
/* 761 */         if (task != null) {
/* 762 */           this.steals += 1;
/* 763 */           if (lowered) setPriority(this.runPriority);
/* 764 */           this.group.setActive(this);
/*     */         } else {
/* 766 */           iters += 1L;
/*     */ 
/* 768 */           if (iters >= 15L) {
/* 769 */             this.group.checkActive(this, iters);
/* 770 */             if (isInterrupted())
/* 771 */               return;
/* 772 */           } else if (!lowered) {
/* 773 */             lowered = true;
/* 774 */             setPriority(this.scanPriority);
/*     */           } else {
/* 776 */             yield();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 780 */     while (task == null);
/*     */ 
/* 783 */     if (!task.isDone()) {
/* 784 */       this.runs += 1;
/* 785 */       task.run();
/* 786 */       task.setDone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 800 */       while (!interrupted())
/*     */       {
/* 802 */         FJTask task = pop();
/* 803 */         if (task != null) {
/* 804 */           if (!task.isDone())
/*     */           {
/* 806 */             this.runs += 1;
/* 807 */             task.run();
/* 808 */             task.setDone();
/*     */           }
/*     */         }
/* 811 */         else scanWhileIdling(); 
/*     */       }
/*     */     }
/*     */     finally {
/* 814 */       this.group.setInactive(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void taskYield()
/*     */   {
/* 825 */     FJTask task = pop();
/* 826 */     if (task != null) {
/* 827 */       if (!task.isDone()) {
/* 828 */         this.runs += 1;
/* 829 */         task.run();
/* 830 */         task.setDone();
/*     */       }
/*     */     }
/* 833 */     else scan(null);
/*     */   }
/*     */ 
/*     */   protected final void taskJoin(FJTask w)
/*     */   {
/* 844 */     while (!w.isDone())
/*     */     {
/* 846 */       FJTask task = pop();
/* 847 */       if (task != null) {
/* 848 */         if (!task.isDone()) {
/* 849 */           this.runs += 1;
/* 850 */           task.run();
/* 851 */           task.setDone();
/* 852 */           if (task == w) return; 
/*     */         }
/*     */       }
/*     */       else
/* 855 */         scan(w);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void coInvoke(FJTask w, FJTask v)
/*     */   {
/* 869 */     int t = this.top;
/* 870 */     if (t < (this.base & this.deq.length - 1) + this.deq.length)
/*     */     {
/* 872 */       this.deq[(t & this.deq.length - 1)].put(w);
/* 873 */       this.top = (t + 1);
/*     */ 
/* 877 */       if (!v.isDone()) {
/* 878 */         this.runs += 1;
/* 879 */         v.run();
/* 880 */         v.setDone();
/*     */       }
/*     */ 
/* 885 */       while (!w.isDone()) {
/* 886 */         FJTask task = pop();
/* 887 */         if (task != null) {
/* 888 */           if (!task.isDone()) {
/* 889 */             this.runs += 1;
/* 890 */             task.run();
/* 891 */             task.setDone();
/* 892 */             if (task == w) return; 
/*     */           }
/*     */         }
/*     */         else
/* 895 */           scan(w);
/*     */       }
/*     */     }
/* 898 */     slowCoInvoke(w, v);
/*     */   }
/*     */ 
/*     */   protected void slowCoInvoke(FJTask w, FJTask v)
/*     */   {
/* 907 */     push(w);
/* 908 */     FJTask.invoke(v);
/* 909 */     taskJoin(w);
/*     */   }
/*     */ 
/*     */   protected final void coInvoke(FJTask[] tasks)
/*     */   {
/* 918 */     int nforks = tasks.length - 1;
/*     */ 
/* 922 */     int t = this.top;
/*     */ 
/* 924 */     if ((nforks >= 0) && (t + nforks < (this.base & this.deq.length - 1) + this.deq.length)) {
/* 925 */       for (int i = 0; i < nforks; i++) {
/* 926 */         this.deq[(t++ & this.deq.length - 1)].put(tasks[i]);
/* 927 */         this.top = t;
/*     */       }
/*     */ 
/* 931 */       FJTask v = tasks[nforks];
/* 932 */       if (!v.isDone()) {
/* 933 */         this.runs += 1;
/* 934 */         v.run();
/* 935 */         v.setDone();
/*     */       }
/*     */ 
/* 940 */       for (int i = 0; i < nforks; i++) {
/* 941 */         FJTask w = tasks[i];
/* 942 */         while (!w.isDone())
/*     */         {
/* 944 */           FJTask task = pop();
/* 945 */           if (task != null) {
/* 946 */             if (!task.isDone()) {
/* 947 */               this.runs += 1;
/* 948 */               task.run();
/* 949 */               task.setDone();
/*     */             }
/*     */           }
/* 952 */           else scan(w); 
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 956 */       slowCoInvoke(tasks);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void slowCoInvoke(FJTask[] tasks)
/*     */   {
/* 964 */     for (int i = 0; i < tasks.length; i++) push(tasks[i]);
/* 965 */     for (int i = 0; i < tasks.length; i++) taskJoin(tasks[i]);
/*     */   }
/*     */ 
/*     */   protected static final class VolatileTaskRef
/*     */   {
/*     */     protected volatile FJTask ref;
/*     */ 
/*     */     protected final void put(FJTask r)
/*     */     {
/* 265 */       this.ref = r;
/*     */     }
/*     */ 
/*     */     protected final FJTask get()
/*     */     {
/* 270 */       return this.ref;
/*     */     }
/*     */ 
/*     */     protected final FJTask take()
/*     */     {
/* 275 */       FJTask r = this.ref;
/* 276 */       this.ref = null;
/* 277 */       return r;
/*     */     }
/*     */ 
/*     */     protected static VolatileTaskRef[] newArray(int cap)
/*     */     {
/* 286 */       VolatileTaskRef[] a = new VolatileTaskRef[cap];
/* 287 */       for (int k = 0; k < cap; k++) a[k] = new VolatileTaskRef();
/* 288 */       return a;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.FJTaskRunner
 * JD-Core Version:    0.6.0
 */
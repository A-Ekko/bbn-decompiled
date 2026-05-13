/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class PrototyperThread extends Thread
/*    */ {
/* 21 */   private static final ThreadGroup PROTOTYPER_THREAD_GROUP = new ThreadGroup("PROTOTYPER_THREAD_GROUP");
/*    */ 
/* 23 */   private static final Log LOG = LogFactory.getLog(PrototyperThread.class);
/*    */   private boolean stop;
/*    */ 
/*    */   public PrototyperThread(String name)
/*    */   {
/* 28 */     super(PROTOTYPER_THREAD_GROUP, name);
/* 29 */     setDaemon(true);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 34 */     while (!this.stop) {
/* 35 */       int sweptCount = 0;
/* 36 */       while ((PrototyperController.isKeepSweeping()) && (!this.stop)) {
/* 37 */         PrototyperController.sweepStarted();
/* 38 */         ConnectionPool[] cps = ConnectionPoolManager.getInstance().getConnectionPools();
/* 39 */         for (int i = 0; (i < cps.length) && (!this.stop); i++) {
/* 40 */           Prototyper p = cps[i].getPrototyper();
/*    */           try {
/* 42 */             cps[i].acquirePrimaryReadLock();
/* 43 */             if ((cps[i].isConnectionPoolUp()) && (p.isSweepNeeded())) {
/* 44 */               p.sweep();
/* 45 */               sweptCount++;
/*    */             }
/*    */           } catch (InterruptedException e) {
/* 48 */             LOG.error("Couldn't acquire primary read lock", e);
/*    */           } finally {
/* 50 */             cps[i].releasePrimaryReadLock();
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 58 */       doWait();
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void cancel() {
/* 63 */     this.stop = true;
/* 64 */     doNotify();
/*    */   }
/*    */ 
/*    */   private synchronized void doWait() {
/*    */     try {
/* 69 */       wait();
/*    */     } catch (InterruptedException e) {
/* 71 */       LOG.debug("Expected interruption of sleep");
/*    */     }
/*    */   }
/*    */ 
/*    */   protected synchronized void doNotify() {
/* 76 */     notifyAll();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.PrototyperThread
 * JD-Core Version:    0.6.0
 */
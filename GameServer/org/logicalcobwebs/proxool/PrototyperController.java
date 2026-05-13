/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class PrototyperController
/*    */ {
/* 22 */   private static final Log LOG = LogFactory.getLog(PrototyperController.class);
/*    */   private static PrototyperThread prototyperThread;
/*    */   private static boolean keepSweeping;
/*    */   private static final String LOCK = "LOCK";
/*    */ 
/*    */   private static void startPrototyper()
/*    */   {
/* 31 */     if (prototyperThread == null)
/* 32 */       synchronized ("LOCK") {
/* 33 */         if (prototyperThread == null) {
/* 34 */           prototyperThread = new PrototyperThread("Prototyper");
/* 35 */           prototyperThread.start();
/*    */         }
/*    */       }
/*    */   }
/*    */ 
/*    */   protected static void triggerSweep(String alias)
/*    */   {
/*    */     try
/*    */     {
/* 49 */       ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*    */       try {
/* 51 */         cp.acquirePrimaryReadLock();
/* 52 */         cp.getPrototyper().triggerSweep();
/*    */       } catch (InterruptedException e) {
/* 54 */         LOG.error("Couldn't acquire primary read lock", e);
/*    */       } finally {
/* 56 */         cp.releasePrimaryReadLock();
/*    */       }
/*    */     } catch (ProxoolException e) {
/* 59 */       if (LOG.isDebugEnabled()) {
/* 60 */         LOG.debug("Couldn't trigger prototyper triggerSweep for '" + alias + "'  - maybe it's just been shutdown");
/*    */       }
/*    */     }
/* 63 */     startPrototyper();
/*    */     try
/*    */     {
/* 67 */       keepSweeping = true;
/*    */ 
/* 69 */       if (prototyperThread != null)
/* 70 */         prototyperThread.doNotify();
/*    */     }
/*    */     catch (IllegalMonitorStateException e) {
/* 73 */       LOG.debug("Hmm", e);
/* 74 */       if ((Thread.activeCount() > 10) && (LOG.isInfoEnabled())) {
/* 75 */         LOG.info("Suspicious thread count of " + Thread.activeCount());
/*    */       }
/*    */     }
/*    */     catch (IllegalThreadStateException e)
/*    */     {
/* 80 */       if (LOG.isDebugEnabled())
/* 81 */         LOG.debug("Ignoring attempt to prototype whilst already prototyping");
/*    */     }
/*    */   }
/*    */ 
/*    */   public static boolean isKeepSweeping()
/*    */   {
/* 87 */     return keepSweeping;
/*    */   }
/*    */ 
/*    */   public static void sweepStarted() {
/* 91 */     keepSweeping = false;
/*    */   }
/*    */ 
/*    */   protected static void shutdown()
/*    */   {
/* 98 */     synchronized ("LOCK") {
/* 99 */       if (prototyperThread != null) {
/* 100 */         LOG.info("Stopping " + prototyperThread.getName() + " thread");
/* 101 */         prototyperThread.cancel();
/* 102 */         prototyperThread = null;
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.PrototyperController
 * JD-Core Version:    0.6.0
 */
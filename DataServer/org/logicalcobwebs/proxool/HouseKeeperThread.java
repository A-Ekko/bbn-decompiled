/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class HouseKeeperThread extends Thread
/*    */ {
/* 21 */   private static final Log LOG = LogFactory.getLog(HouseKeeperThread.class);
/*    */   private boolean stop;
/*    */ 
/*    */   public HouseKeeperThread(String name)
/*    */   {
/* 26 */     setDaemon(true);
/* 27 */     setName(name);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 32 */     while (!this.stop) {
/* 33 */       HouseKeeper hk = HouseKeeperController.getHouseKeeperToRun();
/* 34 */       while ((hk != null) && (!this.stop))
/*    */       {
/*    */         try
/*    */         {
/* 39 */           hk.sweep();
/*    */         } catch (ProxoolException e) {
/* 41 */           LOG.error("Couldn't sweep " + hk.getAlias(), e);
/*    */         }
/* 43 */         hk = HouseKeeperController.getHouseKeeperToRun();
/*    */       }
/*    */       try {
/* 46 */         Thread.sleep(5000L);
/*    */       } catch (InterruptedException e) {
/* 48 */         LOG.error("Interrupted", e);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void cancel()
/*    */   {
/* 55 */     this.stop = true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.HouseKeeperThread
 * JD-Core Version:    0.6.0
 */
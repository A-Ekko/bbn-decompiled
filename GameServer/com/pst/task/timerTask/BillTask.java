/*    */ package com.pst.task.timerTask;
/*    */ 
/*    */ import com.pst.db.dao.BillDao;
/*    */ import java.util.TimerTask;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class BillTask extends TimerTask
/*    */ {
/* 10 */   private BillDao billDao = new BillDao();
/* 11 */   private Logger logger = Logger.getLogger(BillTask.class);
/*    */ 
/*    */   public void run()
/*    */   {
/* 15 */     this.billDao.bill(100);
/* 16 */     this.billDao.arena(100);
/* 17 */     this.billDao.burst(100);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.timerTask.BillTask
 * JD-Core Version:    0.6.0
 */
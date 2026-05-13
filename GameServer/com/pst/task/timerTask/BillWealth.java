/*    */ package com.pst.task.timerTask;
/*    */ 
/*    */ import com.pst.db.dao.BillDao;
/*    */ import java.util.TimerTask;
/*    */ 
/*    */ public class BillWealth extends TimerTask
/*    */ {
/* 12 */   private BillDao billDao = new BillDao();
/*    */ 
/*    */   public void run()
/*    */   {
/* 16 */     this.billDao.wealth(100);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.timerTask.BillWealth
 * JD-Core Version:    0.6.0
 */
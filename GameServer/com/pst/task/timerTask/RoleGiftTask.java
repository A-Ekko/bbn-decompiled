/*    */ package com.pst.task.timerTask;
/*    */ 
/*    */ import com.pst.db.dao.GRoleGiftDao;
/*    */ import java.util.TimerTask;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class RoleGiftTask extends TimerTask
/*    */ {
/* 10 */   private Logger logger = Logger.getLogger(RoleGiftTask.class);
/*    */ 
/*    */   public void run() {
/* 13 */     new GRoleGiftDao().clearRoleGiftData();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.timerTask.RoleGiftTask
 * JD-Core Version:    0.6.0
 */
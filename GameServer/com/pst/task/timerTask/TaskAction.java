/*    */ package com.pst.task.timerTask;
/*    */ 
/*    */ import java.util.Calendar;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
/*    */ 
/*    */ public class TaskAction
/*    */ {
/*    */   public void playerOnlineByMap()
/*    */   {
/* 15 */     Calendar cal = Calendar.getInstance();
/* 16 */     cal.add(11, 1);
/*    */ 
/* 18 */     int year = cal.get(1);
/* 19 */     int month = cal.get(2);
/* 20 */     int day = cal.get(5);
/* 21 */     int hour = cal.get(11);
/*    */ 
/* 23 */     cal.set(year, month, day, hour, 0, 0);
/*    */ 
/* 25 */     long period = 3600000L;
/* 26 */     Timer timer = new Timer();
/* 27 */     TimerTask task = new PlayerOnlineCount();
/* 28 */     timer.schedule(task, cal.getTime(), period);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.timerTask.TaskAction
 * JD-Core Version:    0.6.0
 */
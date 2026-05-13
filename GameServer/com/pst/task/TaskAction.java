/*     */ package com.pst.task;
/*     */ 
/*     */ import com.pst.task.timerTask.BillTask;
/*     */ import com.pst.task.timerTask.BillWealth;
/*     */ import com.pst.task.timerTask.PlayerOnlineCount;
/*     */ import com.pst.task.timerTask.RoleGiftTask;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ public class TaskAction
/*     */ {
/*     */   public void playerOnlineByMap()
/*     */   {
/*  19 */     Calendar cal = Calendar.getInstance();
/*  20 */     cal.add(11, 1);
/*     */ 
/*  22 */     int year = cal.get(1);
/*  23 */     int month = cal.get(2);
/*  24 */     int day = cal.get(5);
/*  25 */     int hour = cal.get(11);
/*     */ 
/*  27 */     cal.set(year, month, day, hour, 0, 0);
/*     */ 
/*  29 */     long period = 3600000L;
/*  30 */     Timer timer = new Timer();
/*  31 */     TimerTask task = new PlayerOnlineCount();
/*  32 */     timer.schedule(task, cal.getTime(), period);
/*     */   }
/*     */ 
/*     */   public void taskClearGiftAction()
/*     */   {
/*  40 */     int hour = 0;
/*  41 */     Calendar calendar = Calendar.getInstance();
/*  42 */     calendar.add(5, 1);
/*     */ 
/*  44 */     int year = calendar.get(1);
/*  45 */     int month = calendar.get(2);
/*  46 */     int day = calendar.get(5);
/*     */ 
/*  48 */     calendar.set(year, month, day, hour, 0, 0);
/*     */ 
/*  50 */     Date startDate = calendar.getTime();
/*  51 */     long period = 86400000L;
/*     */ 
/*  53 */     Timer timer = new Timer();
/*  54 */     TimerTask task = new RoleGiftTask();
/*     */ 
/*  56 */     timer.schedule(task, startDate, period);
/*     */   }
/*     */ 
/*     */   public void taskAction()
/*     */   {
/*  63 */     Calendar calendar = Calendar.getInstance();
/*     */ 
/*  79 */     Date startDate = calendar.getTime();
/*     */ 
/*  81 */     long period = 300000L;
/*     */ 
/*  83 */     Timer timer = new Timer();
/*  84 */     TimerTask task = new BillTask();
/*     */ 
/*  86 */     timer.schedule(task, startDate, period);
/*     */   }
/*     */ 
/*     */   public void wealthAction()
/*     */   {
/*  93 */     Calendar calendar = Calendar.getInstance();
/*  94 */     calendar.add(5, 1);
/*     */ 
/*  96 */     int year = calendar.get(1);
/*  97 */     int month = calendar.get(2);
/*  98 */     int day = calendar.get(5);
/*     */ 
/* 100 */     int hour = 2;
/* 101 */     calendar.set(year, month, day, hour, 0, 0);
/*     */ 
/* 103 */     long period = 86400000L;
/*     */ 
/* 105 */     Timer timer = new Timer();
/* 106 */     TimerTask task = new BillWealth();
/* 107 */     timer.schedule(task, calendar.getTime(), period);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.TaskAction
 * JD-Core Version:    0.6.0
 */
/*     */ package org.logicalcobwebs.proxool.admin;
/*     */ 
/*     */ import java.util.Date;
/*     */ 
/*     */ class Statistics
/*     */   implements StatisticsIF
/*     */ {
/*     */   private Date startDate;
/*     */   private Date stopDate;
/*     */   private long servedCount;
/*     */   private long refusedCount;
/*     */   private long totalActiveTime;
/*     */ 
/*     */   protected Statistics(Date startDate)
/*     */   {
/*  34 */     this.startDate = startDate;
/*     */   }
/*     */ 
/*     */   protected void connectionReturned(long activeTime)
/*     */   {
/*  41 */     this.totalActiveTime += activeTime;
/*  42 */     this.servedCount += 1L;
/*     */   }
/*     */ 
/*     */   protected void connectionRefused()
/*     */   {
/*  49 */     this.refusedCount += 1L;
/*     */   }
/*     */ 
/*     */   protected void setStopDate(Date stopDate)
/*     */   {
/*  56 */     this.stopDate = stopDate;
/*     */   }
/*     */ 
/*     */   public Date getStartDate()
/*     */   {
/*  63 */     return this.startDate;
/*     */   }
/*     */ 
/*     */   public Date getStopDate()
/*     */   {
/*  70 */     return this.stopDate;
/*     */   }
/*     */ 
/*     */   public long getPeriod()
/*     */   {
/*  77 */     if (this.stopDate != null) {
/*  78 */       return this.stopDate.getTime() - this.startDate.getTime();
/*     */     }
/*  80 */     return System.currentTimeMillis() - this.startDate.getTime();
/*     */   }
/*     */ 
/*     */   public double getAverageActiveTime()
/*     */   {
/*  88 */     if (this.servedCount > 0L) {
/*  89 */       return this.totalActiveTime / this.servedCount;
/*     */     }
/*  91 */     return 0.0D;
/*     */   }
/*     */ 
/*     */   public double getAverageActiveCount()
/*     */   {
/*  99 */     return this.totalActiveTime / getPeriod();
/*     */   }
/*     */ 
/*     */   public double getServedPerSecond()
/*     */   {
/* 106 */     return this.servedCount / (getPeriod() / 1000.0D);
/*     */   }
/*     */ 
/*     */   public double getRefusedPerSecond()
/*     */   {
/* 113 */     return this.refusedCount / (getPeriod() / 1000.0D);
/*     */   }
/*     */ 
/*     */   public long getServedCount()
/*     */   {
/* 120 */     return this.servedCount;
/*     */   }
/*     */ 
/*     */   public long getRefusedCount()
/*     */   {
/* 127 */     return this.refusedCount;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.Statistics
 * JD-Core Version:    0.6.0
 */
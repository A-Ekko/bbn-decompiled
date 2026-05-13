/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class LevelRangeFilter extends Filter
/*     */ {
/*  50 */   boolean acceptOnMatch = false;
/*     */   Level levelMin;
/*     */   Level levelMax;
/*     */ 
/*     */   public int decide(LoggingEvent event)
/*     */   {
/*  61 */     if ((this.levelMin != null) && 
/*  62 */       (!event.getLevel().isGreaterOrEqual(this.levelMin)))
/*     */     {
/*  64 */       return -1;
/*     */     }
/*     */ 
/*  68 */     if ((this.levelMax != null) && 
/*  69 */       (event.getLevel().toInt() > this.levelMax.toInt()))
/*     */     {
/*  74 */       return -1;
/*     */     }
/*     */ 
/*  78 */     if (this.acceptOnMatch)
/*     */     {
/*  81 */       return 1;
/*     */     }
/*     */ 
/*  85 */     return 0;
/*     */   }
/*     */ 
/*     */   public Level getLevelMax()
/*     */   {
/*  93 */     return this.levelMax;
/*     */   }
/*     */ 
/*     */   public Level getLevelMin()
/*     */   {
/* 101 */     return this.levelMin;
/*     */   }
/*     */ 
/*     */   public boolean getAcceptOnMatch()
/*     */   {
/* 109 */     return this.acceptOnMatch;
/*     */   }
/*     */ 
/*     */   public void setLevelMax(Level levelMax)
/*     */   {
/* 117 */     this.levelMax = levelMax;
/*     */   }
/*     */ 
/*     */   public void setLevelMin(Level levelMin)
/*     */   {
/* 125 */     this.levelMin = levelMin;
/*     */   }
/*     */ 
/*     */   public void setAcceptOnMatch(boolean acceptOnMatch)
/*     */   {
/* 133 */     this.acceptOnMatch = acceptOnMatch;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.LevelRangeFilter
 * JD-Core Version:    0.6.0
 */
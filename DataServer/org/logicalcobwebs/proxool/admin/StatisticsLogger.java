/*    */ package org.logicalcobwebs.proxool.admin;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.DecimalFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import org.apache.commons.logging.Log;
/*    */ 
/*    */ public class StatisticsLogger
/*    */   implements StatisticsListenerIF
/*    */ {
/* 24 */   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
/*    */ 
/* 26 */   private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
/*    */   private Log log;
/*    */   private String logLevel;
/*    */ 
/*    */   public StatisticsLogger(Log log, String logLevel)
/*    */   {
/* 33 */     this.log = log;
/* 34 */     this.logLevel = logLevel;
/*    */   }
/*    */ 
/*    */   public void statistics(String alias, StatisticsIF statistics)
/*    */   {
/* 39 */     if ((statistics != null) && (this.logLevel != null))
/*    */     {
/* 41 */       StringBuffer out = new StringBuffer();
/*    */ 
/* 43 */       out.append(TIME_FORMAT.format(statistics.getStartDate()));
/* 44 */       out.append(" - ");
/* 45 */       out.append(TIME_FORMAT.format(statistics.getStopDate()));
/* 46 */       out.append(", s:");
/* 47 */       out.append(statistics.getServedCount());
/* 48 */       out.append(":");
/* 49 */       out.append(DECIMAL_FORMAT.format(statistics.getServedPerSecond()));
/*    */ 
/* 51 */       out.append("/s, r:");
/* 52 */       out.append(statistics.getRefusedCount());
/* 53 */       out.append(":");
/* 54 */       out.append(DECIMAL_FORMAT.format(statistics.getRefusedPerSecond()));
/*    */ 
/* 56 */       out.append("/s, a:");
/* 57 */       out.append(DECIMAL_FORMAT.format(statistics.getAverageActiveTime()));
/* 58 */       out.append("ms/");
/* 59 */       out.append(DECIMAL_FORMAT.format(statistics.getAverageActiveCount()));
/*    */ 
/* 61 */       if (this.logLevel.equals("TRACE"))
/* 62 */         this.log.trace(out.toString());
/* 63 */       else if (this.logLevel.equals("DEBUG"))
/* 64 */         this.log.debug(out.toString());
/* 65 */       else if (this.logLevel.equals("INFO"))
/* 66 */         this.log.info(out.toString());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.StatisticsLogger
 * JD-Core Version:    0.6.0
 */
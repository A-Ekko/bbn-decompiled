/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.FieldPosition;
/*    */ import java.text.ParsePosition;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class RelativeTimeDateFormat extends DateFormat
/*    */ {
/*    */   protected final long startTime;
/*    */ 
/*    */   public RelativeTimeDateFormat()
/*    */   {
/* 30 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*    */   {
/* 43 */     return sbuf.append(date.getTime() - this.startTime);
/*    */   }
/*    */ 
/*    */   public Date parse(String s, ParsePosition pos)
/*    */   {
/* 51 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.RelativeTimeDateFormat
 * JD-Core Version:    0.6.0
 */
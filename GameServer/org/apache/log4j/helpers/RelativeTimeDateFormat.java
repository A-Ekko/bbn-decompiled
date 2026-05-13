/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.FieldPosition;
/*    */ import java.text.ParsePosition;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class RelativeTimeDateFormat extends DateFormat
/*    */ {
/*    */   private static final long serialVersionUID = 7055751607085611984L;
/*    */   protected final long startTime;
/*    */ 
/*    */   public RelativeTimeDateFormat()
/*    */   {
/* 42 */     this.startTime = System.currentTimeMillis();
/*    */   }
/*    */ 
/*    */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*    */   {
/* 55 */     return sbuf.append(date.getTime() - this.startTime);
/*    */   }
/*    */ 
/*    */   public Date parse(String s, ParsePosition pos)
/*    */   {
/* 63 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.helpers.RelativeTimeDateFormat
 * JD-Core Version:    0.6.0
 */
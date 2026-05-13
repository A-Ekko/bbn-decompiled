/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.DateFormatSymbols;
/*    */ import java.text.FieldPosition;
/*    */ import java.text.ParsePosition;
/*    */ import java.util.Calendar;
/*    */ import java.util.Date;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class DateTimeDateFormat extends AbsoluteTimeDateFormat
/*    */ {
/*    */   String[] shortMonths;
/*    */ 
/*    */   public DateTimeDateFormat()
/*    */   {
/* 31 */     this.shortMonths = new DateFormatSymbols().getShortMonths();
/*    */   }
/*    */ 
/*    */   public DateTimeDateFormat(TimeZone timeZone)
/*    */   {
/* 36 */     this();
/* 37 */     setCalendar(Calendar.getInstance(timeZone));
/*    */   }
/*    */ 
/*    */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*    */   {
/* 50 */     this.calendar.setTime(date);
/*    */ 
/* 52 */     int day = this.calendar.get(5);
/* 53 */     if (day < 10)
/* 54 */       sbuf.append('0');
/* 55 */     sbuf.append(day);
/* 56 */     sbuf.append(' ');
/* 57 */     sbuf.append(this.shortMonths[this.calendar.get(2)]);
/* 58 */     sbuf.append(' ');
/*    */ 
/* 60 */     int year = this.calendar.get(1);
/* 61 */     sbuf.append(year);
/* 62 */     sbuf.append(' ');
/*    */ 
/* 64 */     return super.format(date, sbuf, fieldPosition);
/*    */   }
/*    */ 
/*    */   public Date parse(String s, ParsePosition pos)
/*    */   {
/* 72 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.DateTimeDateFormat
 * JD-Core Version:    0.6.0
 */
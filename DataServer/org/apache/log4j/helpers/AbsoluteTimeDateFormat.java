/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class AbsoluteTimeDateFormat extends DateFormat
/*     */ {
/*     */   public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";
/*     */   public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";
/*     */   public static final String ISO8601_DATE_FORMAT = "ISO8601";
/*     */   private static long previousTime;
/*  60 */   private static char[] previousTimeWithoutMillis = new char[9];
/*     */ 
/*     */   public AbsoluteTimeDateFormat()
/*     */   {
/*  51 */     setCalendar(Calendar.getInstance());
/*     */   }
/*     */ 
/*     */   public AbsoluteTimeDateFormat(TimeZone timeZone)
/*     */   {
/*  56 */     setCalendar(Calendar.getInstance(timeZone));
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*     */   {
/*  74 */     long now = date.getTime();
/*  75 */     int millis = (int)(now % 1000L);
/*     */ 
/*  77 */     if (now - millis != previousTime)
/*     */     {
/*  82 */       this.calendar.setTime(date);
/*     */ 
/*  84 */       int start = sbuf.length();
/*     */ 
/*  86 */       int hour = this.calendar.get(11);
/*  87 */       if (hour < 10) {
/*  88 */         sbuf.append('0');
/*     */       }
/*  90 */       sbuf.append(hour);
/*  91 */       sbuf.append(':');
/*     */ 
/*  93 */       int mins = this.calendar.get(12);
/*  94 */       if (mins < 10) {
/*  95 */         sbuf.append('0');
/*     */       }
/*  97 */       sbuf.append(mins);
/*  98 */       sbuf.append(':');
/*     */ 
/* 100 */       int secs = this.calendar.get(13);
/* 101 */       if (secs < 10) {
/* 102 */         sbuf.append('0');
/*     */       }
/* 104 */       sbuf.append(secs);
/* 105 */       sbuf.append(',');
/*     */ 
/* 108 */       sbuf.getChars(start, sbuf.length(), previousTimeWithoutMillis, 0);
/*     */ 
/* 110 */       previousTime = now - millis;
/*     */     }
/*     */     else {
/* 113 */       sbuf.append(previousTimeWithoutMillis);
/*     */     }
/*     */ 
/* 118 */     if (millis < 100)
/* 119 */       sbuf.append('0');
/* 120 */     if (millis < 10) {
/* 121 */       sbuf.append('0');
/*     */     }
/* 123 */     sbuf.append(millis);
/* 124 */     return sbuf;
/*     */   }
/*     */ 
/*     */   public Date parse(String s, ParsePosition pos)
/*     */   {
/* 132 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.AbsoluteTimeDateFormat
 * JD-Core Version:    0.6.0
 */
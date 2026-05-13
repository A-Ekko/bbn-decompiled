/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class ISO8601DateFormat extends AbsoluteTimeDateFormat
/*     */ {
/*     */   private static long lastTime;
/*  45 */   private static char[] lastTimeString = new char[20];
/*     */ 
/*     */   public ISO8601DateFormat()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ISO8601DateFormat(TimeZone timeZone)
/*     */   {
/*  41 */     super(timeZone);
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*     */   {
/*  57 */     long now = date.getTime();
/*  58 */     int millis = (int)(now % 1000L);
/*     */ 
/*  60 */     if (now - millis != lastTime)
/*     */     {
/*  65 */       this.calendar.setTime(date);
/*     */ 
/*  67 */       int start = sbuf.length();
/*     */ 
/*  69 */       int year = this.calendar.get(1);
/*  70 */       sbuf.append(year);
/*     */       String month;
/*  73 */       switch (this.calendar.get(2)) { case 0:
/*  74 */         month = "-01-"; break;
/*     */       case 1:
/*  75 */         month = "-02-"; break;
/*     */       case 2:
/*  76 */         month = "-03-"; break;
/*     */       case 3:
/*  77 */         month = "-04-"; break;
/*     */       case 4:
/*  78 */         month = "-05-"; break;
/*     */       case 5:
/*  79 */         month = "-06-"; break;
/*     */       case 6:
/*  80 */         month = "-07-"; break;
/*     */       case 7:
/*  81 */         month = "-08-"; break;
/*     */       case 8:
/*  82 */         month = "-09-"; break;
/*     */       case 9:
/*  83 */         month = "-10-"; break;
/*     */       case 10:
/*  84 */         month = "-11-"; break;
/*     */       case 11:
/*  85 */         month = "-12-"; break;
/*     */       default:
/*  86 */         month = "-NA-";
/*     */       }
/*  88 */       sbuf.append(month);
/*     */ 
/*  90 */       int day = this.calendar.get(5);
/*  91 */       if (day < 10)
/*  92 */         sbuf.append('0');
/*  93 */       sbuf.append(day);
/*     */ 
/*  95 */       sbuf.append(' ');
/*     */ 
/*  97 */       int hour = this.calendar.get(11);
/*  98 */       if (hour < 10) {
/*  99 */         sbuf.append('0');
/*     */       }
/* 101 */       sbuf.append(hour);
/* 102 */       sbuf.append(':');
/*     */ 
/* 104 */       int mins = this.calendar.get(12);
/* 105 */       if (mins < 10) {
/* 106 */         sbuf.append('0');
/*     */       }
/* 108 */       sbuf.append(mins);
/* 109 */       sbuf.append(':');
/*     */ 
/* 111 */       int secs = this.calendar.get(13);
/* 112 */       if (secs < 10) {
/* 113 */         sbuf.append('0');
/*     */       }
/* 115 */       sbuf.append(secs);
/*     */ 
/* 117 */       sbuf.append(',');
/*     */ 
/* 120 */       sbuf.getChars(start, sbuf.length(), lastTimeString, 0);
/* 121 */       lastTime = now - millis;
/*     */     }
/*     */     else {
/* 124 */       sbuf.append(lastTimeString);
/*     */     }
/*     */ 
/* 128 */     if (millis < 100)
/* 129 */       sbuf.append('0');
/* 130 */     if (millis < 10) {
/* 131 */       sbuf.append('0');
/*     */     }
/* 133 */     sbuf.append(millis);
/* 134 */     return sbuf;
/*     */   }
/*     */ 
/*     */   public Date parse(String s, ParsePosition pos)
/*     */   {
/* 142 */     return null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.ISO8601DateFormat
 * JD-Core Version:    0.6.0
 */
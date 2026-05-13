/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public abstract class DateLayout extends Layout
/*     */ {
/*     */   public static final String NULL_DATE_FORMAT = "NULL";
/*     */   public static final String RELATIVE_TIME_DATE_FORMAT = "RELATIVE";
/*  55 */   protected FieldPosition pos = new FieldPosition(0);
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DATE_FORMAT_OPTION = "DateFormat";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String TIMEZONE_OPTION = "TimeZone";
/*     */   private String timeZoneID;
/*     */   private String dateFormatOption;
/*     */   protected DateFormat dateFormat;
/*  75 */   protected Date date = new Date();
/*     */ 
/*     */   /** @deprecated */
/*     */   public String[] getOptionStrings()
/*     */   {
/*  83 */     return new String[] { "DateFormat", "TimeZone" };
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setOption(String option, String value)
/*     */   {
/*  92 */     if (option.equalsIgnoreCase("DateFormat"))
/*  93 */       this.dateFormatOption = value.toUpperCase();
/*  94 */     else if (option.equalsIgnoreCase("TimeZone"))
/*  95 */       this.timeZoneID = value;
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormat)
/*     */   {
/* 107 */     if (dateFormat != null) {
/* 108 */       this.dateFormatOption = dateFormat;
/*     */     }
/* 110 */     setDateFormat(this.dateFormatOption, TimeZone.getDefault());
/*     */   }
/*     */ 
/*     */   public String getDateFormat()
/*     */   {
/* 118 */     return this.dateFormatOption;
/*     */   }
/*     */ 
/*     */   public void setTimeZone(String timeZone)
/*     */   {
/* 127 */     this.timeZoneID = timeZone;
/*     */   }
/*     */ 
/*     */   public String getTimeZone()
/*     */   {
/* 135 */     return this.timeZoneID;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 140 */     setDateFormat(this.dateFormatOption);
/* 141 */     if ((this.timeZoneID != null) && (this.dateFormat != null))
/* 142 */       this.dateFormat.setTimeZone(TimeZone.getTimeZone(this.timeZoneID));
/*     */   }
/*     */ 
/*     */   public void dateFormat(StringBuffer buf, LoggingEvent event)
/*     */   {
/* 148 */     if (this.dateFormat != null) {
/* 149 */       this.date.setTime(event.timeStamp);
/* 150 */       this.dateFormat.format(this.date, buf, this.pos);
/* 151 */       buf.append(' ');
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDateFormat(DateFormat dateFormat, TimeZone timeZone)
/*     */   {
/* 161 */     this.dateFormat = dateFormat;
/* 162 */     this.dateFormat.setTimeZone(timeZone);
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormatType, TimeZone timeZone)
/*     */   {
/* 181 */     if (dateFormatType == null) {
/* 182 */       this.dateFormat = null;
/* 183 */       return;
/*     */     }
/*     */ 
/* 186 */     if (dateFormatType.equalsIgnoreCase("NULL")) {
/* 187 */       this.dateFormat = null;
/* 188 */     } else if (dateFormatType.equalsIgnoreCase("RELATIVE")) {
/* 189 */       this.dateFormat = new RelativeTimeDateFormat();
/* 190 */     } else if (dateFormatType.equalsIgnoreCase("ABSOLUTE"))
/*     */     {
/* 192 */       this.dateFormat = new AbsoluteTimeDateFormat(timeZone);
/* 193 */     } else if (dateFormatType.equalsIgnoreCase("DATE"))
/*     */     {
/* 195 */       this.dateFormat = new DateTimeDateFormat(timeZone);
/* 196 */     } else if (dateFormatType.equalsIgnoreCase("ISO8601"))
/*     */     {
/* 198 */       this.dateFormat = new ISO8601DateFormat(timeZone);
/*     */     } else {
/* 200 */       this.dateFormat = new SimpleDateFormat(dateFormatType);
/* 201 */       this.dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.helpers.DateLayout
 * JD-Core Version:    0.6.0
 */
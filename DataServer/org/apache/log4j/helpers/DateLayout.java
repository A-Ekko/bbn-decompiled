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
/*  46 */   protected FieldPosition pos = new FieldPosition(0);
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DATE_FORMAT_OPTION = "DateFormat";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String TIMEZONE_OPTION = "TimeZone";
/*     */   private String timeZoneID;
/*     */   private String dateFormatOption;
/*     */   protected DateFormat dateFormat;
/*  66 */   protected Date date = new Date();
/*     */ 
/*     */   /** @deprecated */
/*     */   public String[] getOptionStrings()
/*     */   {
/*  74 */     return new String[] { "DateFormat", "TimeZone" };
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setOption(String option, String value)
/*     */   {
/*  83 */     if (option.equalsIgnoreCase("DateFormat"))
/*  84 */       this.dateFormatOption = value.toUpperCase();
/*  85 */     else if (option.equalsIgnoreCase("TimeZone"))
/*  86 */       this.timeZoneID = value;
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormat)
/*     */   {
/*  98 */     if (dateFormat != null) {
/*  99 */       this.dateFormatOption = dateFormat;
/*     */     }
/* 101 */     setDateFormat(this.dateFormatOption, TimeZone.getDefault());
/*     */   }
/*     */ 
/*     */   public String getDateFormat()
/*     */   {
/* 109 */     return this.dateFormatOption;
/*     */   }
/*     */ 
/*     */   public void setTimeZone(String timeZone)
/*     */   {
/* 118 */     this.timeZoneID = timeZone;
/*     */   }
/*     */ 
/*     */   public String getTimeZone()
/*     */   {
/* 126 */     return this.timeZoneID;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 131 */     setDateFormat(this.dateFormatOption);
/* 132 */     if ((this.timeZoneID != null) && (this.dateFormat != null))
/* 133 */       this.dateFormat.setTimeZone(TimeZone.getTimeZone(this.timeZoneID));
/*     */   }
/*     */ 
/*     */   public void dateFormat(StringBuffer buf, LoggingEvent event)
/*     */   {
/* 139 */     if (this.dateFormat != null) {
/* 140 */       this.date.setTime(event.timeStamp);
/* 141 */       this.dateFormat.format(this.date, buf, this.pos);
/* 142 */       buf.append(' ');
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDateFormat(DateFormat dateFormat, TimeZone timeZone)
/*     */   {
/* 152 */     this.dateFormat = dateFormat;
/* 153 */     this.dateFormat.setTimeZone(timeZone);
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormatType, TimeZone timeZone)
/*     */   {
/* 172 */     if (dateFormatType == null) {
/* 173 */       this.dateFormat = null;
/* 174 */       return;
/*     */     }
/*     */ 
/* 177 */     if (dateFormatType.equalsIgnoreCase("NULL")) {
/* 178 */       this.dateFormat = null;
/* 179 */     } else if (dateFormatType.equalsIgnoreCase("RELATIVE")) {
/* 180 */       this.dateFormat = new RelativeTimeDateFormat();
/* 181 */     } else if (dateFormatType.equalsIgnoreCase("ABSOLUTE"))
/*     */     {
/* 183 */       this.dateFormat = new AbsoluteTimeDateFormat(timeZone);
/* 184 */     } else if (dateFormatType.equalsIgnoreCase("DATE"))
/*     */     {
/* 186 */       this.dateFormat = new DateTimeDateFormat(timeZone);
/* 187 */     } else if (dateFormatType.equalsIgnoreCase("ISO8601"))
/*     */     {
/* 189 */       this.dateFormat = new ISO8601DateFormat(timeZone);
/*     */     } else {
/* 191 */       this.dateFormat = new SimpleDateFormat(dateFormatType);
/* 192 */       this.dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.DateLayout
 * JD-Core Version:    0.6.0
 */
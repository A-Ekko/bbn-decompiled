/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class DateFormatManager
/*     */ {
/*  41 */   private TimeZone _timeZone = null;
/*  42 */   private Locale _locale = null;
/*     */ 
/*  44 */   private String _pattern = null;
/*  45 */   private DateFormat _dateFormat = null;
/*     */ 
/*     */   public DateFormatManager()
/*     */   {
/*  52 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone)
/*     */   {
/*  58 */     this._timeZone = timeZone;
/*  59 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale)
/*     */   {
/*  65 */     this._locale = locale;
/*  66 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(String pattern)
/*     */   {
/*  72 */     this._pattern = pattern;
/*  73 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale)
/*     */   {
/*  79 */     this._timeZone = timeZone;
/*  80 */     this._locale = locale;
/*  81 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, String pattern)
/*     */   {
/*  87 */     this._timeZone = timeZone;
/*  88 */     this._pattern = pattern;
/*  89 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale, String pattern)
/*     */   {
/*  95 */     this._locale = locale;
/*  96 */     this._pattern = pattern;
/*  97 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale, String pattern)
/*     */   {
/* 103 */     this._timeZone = timeZone;
/* 104 */     this._locale = locale;
/* 105 */     this._pattern = pattern;
/* 106 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized TimeZone getTimeZone()
/*     */   {
/* 114 */     if (this._timeZone == null) {
/* 115 */       return TimeZone.getDefault();
/*     */     }
/* 117 */     return this._timeZone;
/*     */   }
/*     */ 
/*     */   public synchronized void setTimeZone(TimeZone timeZone)
/*     */   {
/* 122 */     timeZone = timeZone;
/* 123 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized Locale getLocale() {
/* 127 */     if (this._locale == null) {
/* 128 */       return Locale.getDefault();
/*     */     }
/* 130 */     return this._locale;
/*     */   }
/*     */ 
/*     */   public synchronized void setLocale(Locale locale)
/*     */   {
/* 135 */     this._locale = locale;
/* 136 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized String getPattern() {
/* 140 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   public synchronized void setPattern(String pattern)
/*     */   {
/* 147 */     this._pattern = pattern;
/* 148 */     configure();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized String getOutputFormat()
/*     */   {
/* 157 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized void setOutputFormat(String pattern)
/*     */   {
/* 165 */     this._pattern = pattern;
/* 166 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized DateFormat getDateFormatInstance() {
/* 170 */     return this._dateFormat;
/*     */   }
/*     */ 
/*     */   public synchronized void setDateFormatInstance(DateFormat dateFormat) {
/* 174 */     this._dateFormat = dateFormat;
/*     */   }
/*     */ 
/*     */   public String format(Date date)
/*     */   {
/* 179 */     return getDateFormatInstance().format(date);
/*     */   }
/*     */ 
/*     */   public String format(Date date, String pattern) {
/* 183 */     DateFormat formatter = null;
/* 184 */     formatter = getDateFormatInstance();
/* 185 */     if ((formatter instanceof SimpleDateFormat)) {
/* 186 */       formatter = (SimpleDateFormat)formatter.clone();
/* 187 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 189 */     return formatter.format(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date)
/*     */     throws ParseException
/*     */   {
/* 196 */     return getDateFormatInstance().parse(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date, String pattern)
/*     */     throws ParseException
/*     */   {
/* 203 */     DateFormat formatter = null;
/* 204 */     formatter = getDateFormatInstance();
/* 205 */     if ((formatter instanceof SimpleDateFormat)) {
/* 206 */       formatter = (SimpleDateFormat)formatter.clone();
/* 207 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 209 */     return formatter.parse(date);
/*     */   }
/*     */ 
/*     */   private synchronized void configure()
/*     */   {
/* 220 */     this._dateFormat = DateFormat.getDateTimeInstance(0, 0, getLocale());
/*     */ 
/* 223 */     this._dateFormat.setTimeZone(getTimeZone());
/*     */ 
/* 225 */     if (this._pattern != null)
/* 226 */       ((SimpleDateFormat)this._dateFormat).applyPattern(this._pattern);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.DateFormatManager
 * JD-Core Version:    0.6.0
 */
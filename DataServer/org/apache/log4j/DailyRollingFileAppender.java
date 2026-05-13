/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class DailyRollingFileAppender extends FileAppender
/*     */ {
/*     */   static final int TOP_OF_TROUBLE = -1;
/*     */   static final int TOP_OF_MINUTE = 0;
/*     */   static final int TOP_OF_HOUR = 1;
/*     */   static final int HALF_DAY = 2;
/*     */   static final int TOP_OF_DAY = 3;
/*     */   static final int TOP_OF_WEEK = 4;
/*     */   static final int TOP_OF_MONTH = 5;
/* 143 */   private String datePattern = "'.'yyyy-MM-dd";
/*     */   private String scheduledFilename;
/* 159 */   private long nextCheck = System.currentTimeMillis() - 1L;
/*     */ 
/* 161 */   Date now = new Date();
/*     */   SimpleDateFormat sdf;
/* 165 */   RollingCalendar rc = new RollingCalendar();
/*     */ 
/* 167 */   int checkPeriod = -1;
/*     */ 
/* 170 */   static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
/*     */ 
/*     */   public DailyRollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DailyRollingFileAppender(Layout layout, String filename, String datePattern)
/*     */     throws IOException
/*     */   {
/* 186 */     super(layout, filename, true);
/* 187 */     this.datePattern = datePattern;
/* 188 */     activateOptions();
/*     */   }
/*     */ 
/*     */   public void setDatePattern(String pattern)
/*     */   {
/* 197 */     this.datePattern = pattern;
/*     */   }
/*     */ 
/*     */   public String getDatePattern()
/*     */   {
/* 202 */     return this.datePattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions() {
/* 206 */     super.activateOptions();
/* 207 */     if ((this.datePattern != null) && (this.fileName != null)) {
/* 208 */       this.now.setTime(System.currentTimeMillis());
/* 209 */       this.sdf = new SimpleDateFormat(this.datePattern);
/* 210 */       int type = computeCheckPeriod();
/* 211 */       printPeriodicity(type);
/* 212 */       this.rc.setType(type);
/* 213 */       File file = new File(this.fileName);
/* 214 */       this.scheduledFilename = (this.fileName + this.sdf.format(new Date(file.lastModified())));
/*     */     }
/*     */     else {
/* 217 */       LogLog.error("Either File or DatePattern options are not set for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   void printPeriodicity(int type)
/*     */   {
/* 223 */     switch (type) {
/*     */     case 0:
/* 225 */       LogLog.debug("Appender [" + this.name + "] to be rolled every minute.");
/* 226 */       break;
/*     */     case 1:
/* 228 */       LogLog.debug("Appender [" + this.name + "] to be rolled on top of every hour.");
/*     */ 
/* 230 */       break;
/*     */     case 2:
/* 232 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midday and midnight.");
/*     */ 
/* 234 */       break;
/*     */     case 3:
/* 236 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midnight.");
/*     */ 
/* 238 */       break;
/*     */     case 4:
/* 240 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of week.");
/*     */ 
/* 242 */       break;
/*     */     case 5:
/* 244 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of every month.");
/*     */ 
/* 246 */       break;
/*     */     default:
/* 248 */       LogLog.warn("Unknown periodicity for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   int computeCheckPeriod()
/*     */   {
/* 263 */     RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
/*     */ 
/* 265 */     Date epoch = new Date(0L);
/* 266 */     if (this.datePattern != null) {
/* 267 */       for (int i = 0; i <= 5; i++) {
/* 268 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
/* 269 */         simpleDateFormat.setTimeZone(gmtTimeZone);
/* 270 */         String r0 = simpleDateFormat.format(epoch);
/* 271 */         rollingCalendar.setType(i);
/* 272 */         Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
/* 273 */         String r1 = simpleDateFormat.format(next);
/*     */ 
/* 275 */         if ((r0 != null) && (r1 != null) && (!r0.equals(r1))) {
/* 276 */           return i;
/*     */         }
/*     */       }
/*     */     }
/* 280 */     return -1;
/*     */   }
/*     */ 
/*     */   void rollOver()
/*     */     throws IOException
/*     */   {
/* 289 */     if (this.datePattern == null) {
/* 290 */       this.errorHandler.error("Missing DatePattern option in rollOver().");
/* 291 */       return;
/*     */     }
/*     */ 
/* 294 */     String datedFilename = this.fileName + this.sdf.format(this.now);
/*     */ 
/* 298 */     if (this.scheduledFilename.equals(datedFilename)) {
/* 299 */       return;
/*     */     }
/*     */ 
/* 303 */     closeFile();
/*     */ 
/* 305 */     File target = new File(this.scheduledFilename);
/* 306 */     if (target.exists()) {
/* 307 */       target.delete();
/*     */     }
/*     */ 
/* 310 */     File file = new File(this.fileName);
/* 311 */     boolean result = file.renameTo(target);
/* 312 */     if (result)
/* 313 */       LogLog.debug(this.fileName + " -> " + this.scheduledFilename);
/*     */     else {
/* 315 */       LogLog.error("Failed to rename [" + this.fileName + "] to [" + this.scheduledFilename + "].");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 321 */       setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
/*     */     }
/*     */     catch (IOException e) {
/* 324 */       this.errorHandler.error("setFile(" + this.fileName + ", false) call failed.");
/*     */     }
/* 326 */     this.scheduledFilename = datedFilename;
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 338 */     long n = System.currentTimeMillis();
/* 339 */     if (n >= this.nextCheck) {
/* 340 */       this.now.setTime(n);
/* 341 */       this.nextCheck = this.rc.getNextCheckMillis(this.now);
/*     */       try {
/* 343 */         rollOver();
/*     */       }
/*     */       catch (IOException ioe) {
/* 346 */         LogLog.error("rollOver() failed.", ioe);
/*     */       }
/*     */     }
/* 349 */     super.subAppend(event);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.DailyRollingFileAppender
 * JD-Core Version:    0.6.0
 */
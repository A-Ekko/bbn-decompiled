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
/* 153 */   private String datePattern = "'.'yyyy-MM-dd";
/*     */   private String scheduledFilename;
/* 169 */   private long nextCheck = System.currentTimeMillis() - 1L;
/*     */ 
/* 171 */   Date now = new Date();
/*     */   SimpleDateFormat sdf;
/* 175 */   RollingCalendar rc = new RollingCalendar();
/*     */ 
/* 177 */   int checkPeriod = -1;
/*     */ 
/* 180 */   static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
/*     */ 
/*     */   public DailyRollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DailyRollingFileAppender(Layout layout, String filename, String datePattern)
/*     */     throws IOException
/*     */   {
/* 196 */     super(layout, filename, true);
/* 197 */     this.datePattern = datePattern;
/* 198 */     activateOptions();
/*     */   }
/*     */ 
/*     */   public void setDatePattern(String pattern)
/*     */   {
/* 207 */     this.datePattern = pattern;
/*     */   }
/*     */ 
/*     */   public String getDatePattern()
/*     */   {
/* 212 */     return this.datePattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions() {
/* 216 */     super.activateOptions();
/* 217 */     if ((this.datePattern != null) && (this.fileName != null)) {
/* 218 */       this.now.setTime(System.currentTimeMillis());
/* 219 */       this.sdf = new SimpleDateFormat(this.datePattern);
/* 220 */       int type = computeCheckPeriod();
/* 221 */       printPeriodicity(type);
/* 222 */       this.rc.setType(type);
/* 223 */       File file = new File(this.fileName);
/* 224 */       this.scheduledFilename = (this.fileName + this.sdf.format(new Date(file.lastModified())));
/*     */     }
/*     */     else {
/* 227 */       LogLog.error("Either File or DatePattern options are not set for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   void printPeriodicity(int type)
/*     */   {
/* 233 */     switch (type) {
/*     */     case 0:
/* 235 */       LogLog.debug("Appender [" + this.name + "] to be rolled every minute.");
/* 236 */       break;
/*     */     case 1:
/* 238 */       LogLog.debug("Appender [" + this.name + "] to be rolled on top of every hour.");
/*     */ 
/* 240 */       break;
/*     */     case 2:
/* 242 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midday and midnight.");
/*     */ 
/* 244 */       break;
/*     */     case 3:
/* 246 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midnight.");
/*     */ 
/* 248 */       break;
/*     */     case 4:
/* 250 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of week.");
/*     */ 
/* 252 */       break;
/*     */     case 5:
/* 254 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of every month.");
/*     */ 
/* 256 */       break;
/*     */     default:
/* 258 */       LogLog.warn("Unknown periodicity for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   int computeCheckPeriod()
/*     */   {
/* 273 */     RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
/*     */ 
/* 275 */     Date epoch = new Date(0L);
/* 276 */     if (this.datePattern != null) {
/* 277 */       for (int i = 0; i <= 5; i++) {
/* 278 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
/* 279 */         simpleDateFormat.setTimeZone(gmtTimeZone);
/* 280 */         String r0 = simpleDateFormat.format(epoch);
/* 281 */         rollingCalendar.setType(i);
/* 282 */         Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
/* 283 */         String r1 = simpleDateFormat.format(next);
/*     */ 
/* 285 */         if ((r0 != null) && (r1 != null) && (!r0.equals(r1))) {
/* 286 */           return i;
/*     */         }
/*     */       }
/*     */     }
/* 290 */     return -1;
/*     */   }
/*     */ 
/*     */   void rollOver()
/*     */     throws IOException
/*     */   {
/* 299 */     if (this.datePattern == null) {
/* 300 */       this.errorHandler.error("Missing DatePattern option in rollOver().");
/* 301 */       return;
/*     */     }
/*     */ 
/* 304 */     String datedFilename = this.fileName + this.sdf.format(this.now);
/*     */ 
/* 308 */     if (this.scheduledFilename.equals(datedFilename)) {
/* 309 */       return;
/*     */     }
/*     */ 
/* 313 */     closeFile();
/*     */ 
/* 315 */     File target = new File(this.scheduledFilename);
/* 316 */     if (target.exists()) {
/* 317 */       target.delete();
/*     */     }
/*     */ 
/* 320 */     File file = new File(this.fileName);
/* 321 */     boolean result = file.renameTo(target);
/* 322 */     if (result)
/* 323 */       LogLog.debug(this.fileName + " -> " + this.scheduledFilename);
/*     */     else {
/* 325 */       LogLog.error("Failed to rename [" + this.fileName + "] to [" + this.scheduledFilename + "].");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 331 */       setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
/*     */     }
/*     */     catch (IOException e) {
/* 334 */       this.errorHandler.error("setFile(" + this.fileName + ", false) call failed.");
/*     */     }
/* 336 */     this.scheduledFilename = datedFilename;
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 348 */     long n = System.currentTimeMillis();
/* 349 */     if (n >= this.nextCheck) {
/* 350 */       this.now.setTime(n);
/* 351 */       this.nextCheck = this.rc.getNextCheckMillis(this.now);
/*     */       try {
/* 353 */         rollOver();
/*     */       }
/*     */       catch (IOException ioe) {
/* 356 */         LogLog.error("rollOver() failed.", ioe);
/*     */       }
/*     */     }
/* 359 */     super.subAppend(event);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.DailyRollingFileAppender
 * JD-Core Version:    0.6.0
 */
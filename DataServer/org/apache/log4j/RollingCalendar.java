/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ class RollingCalendar extends GregorianCalendar
/*     */ {
/* 360 */   int type = -1;
/*     */ 
/*     */   RollingCalendar()
/*     */   {
/*     */   }
/*     */ 
/*     */   RollingCalendar(TimeZone tz, Locale locale) {
/* 367 */     super(tz, locale);
/*     */   }
/*     */ 
/*     */   void setType(int type) {
/* 371 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public long getNextCheckMillis(Date now) {
/* 375 */     return getNextCheckDate(now).getTime();
/*     */   }
/*     */ 
/*     */   public Date getNextCheckDate(Date now) {
/* 379 */     setTime(now);
/*     */ 
/* 381 */     switch (this.type) {
/*     */     case 0:
/* 383 */       set(13, 0);
/* 384 */       set(14, 0);
/* 385 */       add(12, 1);
/* 386 */       break;
/*     */     case 1:
/* 388 */       set(12, 0);
/* 389 */       set(13, 0);
/* 390 */       set(14, 0);
/* 391 */       add(11, 1);
/* 392 */       break;
/*     */     case 2:
/* 394 */       set(12, 0);
/* 395 */       set(13, 0);
/* 396 */       set(14, 0);
/* 397 */       int hour = get(11);
/* 398 */       if (hour < 12) {
/* 399 */         set(11, 12);
/*     */       } else {
/* 401 */         set(11, 0);
/* 402 */         add(5, 1);
/*     */       }
/* 404 */       break;
/*     */     case 3:
/* 406 */       set(11, 0);
/* 407 */       set(12, 0);
/* 408 */       set(13, 0);
/* 409 */       set(14, 0);
/* 410 */       add(5, 1);
/* 411 */       break;
/*     */     case 4:
/* 413 */       set(7, getFirstDayOfWeek());
/* 414 */       set(11, 0);
/* 415 */       set(13, 0);
/* 416 */       set(14, 0);
/* 417 */       add(3, 1);
/* 418 */       break;
/*     */     case 5:
/* 420 */       set(5, 1);
/* 421 */       set(11, 0);
/* 422 */       set(13, 0);
/* 423 */       set(14, 0);
/* 424 */       add(2, 1);
/* 425 */       break;
/*     */     default:
/* 427 */       throw new IllegalStateException("Unknown periodicity type.");
/*     */     }
/* 429 */     return getTime();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.RollingCalendar
 * JD-Core Version:    0.6.0
 */
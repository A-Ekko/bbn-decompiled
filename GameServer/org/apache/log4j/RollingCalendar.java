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
/*     */   private static final long serialVersionUID = -3560331770601814177L;
/* 371 */   int type = -1;
/*     */ 
/*     */   RollingCalendar()
/*     */   {
/*     */   }
/*     */ 
/*     */   RollingCalendar(TimeZone tz, Locale locale) {
/* 378 */     super(tz, locale);
/*     */   }
/*     */ 
/*     */   void setType(int type) {
/* 382 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public long getNextCheckMillis(Date now) {
/* 386 */     return getNextCheckDate(now).getTime();
/*     */   }
/*     */ 
/*     */   public Date getNextCheckDate(Date now) {
/* 390 */     setTime(now);
/*     */ 
/* 392 */     switch (this.type) {
/*     */     case 0:
/* 394 */       set(13, 0);
/* 395 */       set(14, 0);
/* 396 */       add(12, 1);
/* 397 */       break;
/*     */     case 1:
/* 399 */       set(12, 0);
/* 400 */       set(13, 0);
/* 401 */       set(14, 0);
/* 402 */       add(11, 1);
/* 403 */       break;
/*     */     case 2:
/* 405 */       set(12, 0);
/* 406 */       set(13, 0);
/* 407 */       set(14, 0);
/* 408 */       int hour = get(11);
/* 409 */       if (hour < 12) {
/* 410 */         set(11, 12);
/*     */       } else {
/* 412 */         set(11, 0);
/* 413 */         add(5, 1);
/*     */       }
/* 415 */       break;
/*     */     case 3:
/* 417 */       set(11, 0);
/* 418 */       set(12, 0);
/* 419 */       set(13, 0);
/* 420 */       set(14, 0);
/* 421 */       add(5, 1);
/* 422 */       break;
/*     */     case 4:
/* 424 */       set(7, getFirstDayOfWeek());
/* 425 */       set(11, 0);
/* 426 */       set(12, 0);
/* 427 */       set(13, 0);
/* 428 */       set(14, 0);
/* 429 */       add(3, 1);
/* 430 */       break;
/*     */     case 5:
/* 432 */       set(5, 1);
/* 433 */       set(11, 0);
/* 434 */       set(12, 0);
/* 435 */       set(13, 0);
/* 436 */       set(14, 0);
/* 437 */       add(2, 1);
/* 438 */       break;
/*     */     default:
/* 440 */       throw new IllegalStateException("Unknown periodicity type.");
/*     */     }
/* 442 */     return getTime();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.RollingCalendar
 * JD-Core Version:    0.6.0
 */
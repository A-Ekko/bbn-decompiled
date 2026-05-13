/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ class EventDetails
/*     */ {
/*     */   private final long mTimeStamp;
/*     */   private final Priority mPriority;
/*     */   private final String mCategoryName;
/*     */   private final String mNDC;
/*     */   private final String mThreadName;
/*     */   private final String mMessage;
/*     */   private final String[] mThrowableStrRep;
/*     */   private final String mLocationDetails;
/*     */ 
/*     */   EventDetails(long aTimeStamp, Priority aPriority, String aCategoryName, String aNDC, String aThreadName, String aMessage, String[] aThrowableStrRep, String aLocationDetails)
/*     */   {
/*  58 */     this.mTimeStamp = aTimeStamp;
/*  59 */     this.mPriority = aPriority;
/*  60 */     this.mCategoryName = aCategoryName;
/*  61 */     this.mNDC = aNDC;
/*  62 */     this.mThreadName = aThreadName;
/*  63 */     this.mMessage = aMessage;
/*  64 */     this.mThrowableStrRep = aThrowableStrRep;
/*  65 */     this.mLocationDetails = aLocationDetails;
/*     */   }
/*     */ 
/*     */   EventDetails(LoggingEvent aEvent)
/*     */   {
/*  75 */     this(aEvent.timeStamp, aEvent.getLevel(), aEvent.getLoggerName(), aEvent.getNDC(), aEvent.getThreadName(), aEvent.getRenderedMessage(), aEvent.getThrowableStrRep(), aEvent.getLocationInformation() == null ? null : aEvent.getLocationInformation().fullInfo);
/*     */   }
/*     */ 
/*     */   long getTimeStamp()
/*     */   {
/*  88 */     return this.mTimeStamp;
/*     */   }
/*     */ 
/*     */   Priority getPriority()
/*     */   {
/*  93 */     return this.mPriority;
/*     */   }
/*     */ 
/*     */   String getCategoryName()
/*     */   {
/*  98 */     return this.mCategoryName;
/*     */   }
/*     */ 
/*     */   String getNDC()
/*     */   {
/* 103 */     return this.mNDC;
/*     */   }
/*     */ 
/*     */   String getThreadName()
/*     */   {
/* 108 */     return this.mThreadName;
/*     */   }
/*     */ 
/*     */   String getMessage()
/*     */   {
/* 113 */     return this.mMessage;
/*     */   }
/*     */ 
/*     */   String getLocationDetails()
/*     */   {
/* 118 */     return this.mLocationDetails;
/*     */   }
/*     */ 
/*     */   String[] getThrowableStrRep()
/*     */   {
/* 123 */     return this.mThrowableStrRep;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.EventDetails
 * JD-Core Version:    0.6.0
 */
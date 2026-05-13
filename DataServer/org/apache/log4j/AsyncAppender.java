/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*     */ import org.apache.log4j.helpers.BoundedFIFO;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.AppenderAttachable;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class AsyncAppender extends AppenderSkeleton
/*     */   implements AppenderAttachable
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 128;
/*  48 */   BoundedFIFO bf = new BoundedFIFO(128);
/*     */   AppenderAttachableImpl aai;
/*     */   Dispatcher dispatcher;
/*  52 */   boolean locationInfo = false;
/*     */ 
/*  54 */   boolean interruptedWarningMessage = false;
/*     */ 
/*     */   public AsyncAppender()
/*     */   {
/*  59 */     this.aai = new AppenderAttachableImpl();
/*  60 */     this.dispatcher = new Dispatcher(this.bf, this);
/*  61 */     this.dispatcher.start();
/*     */   }
/*     */ 
/*     */   public void addAppender(Appender newAppender)
/*     */   {
/*  66 */     synchronized (this.aai) {
/*  67 */       this.aai.addAppender(newAppender);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/*  74 */     event.getNDC();
/*  75 */     event.getThreadName();
/*     */ 
/*  77 */     event.getMDCCopy();
/*  78 */     if (this.locationInfo) {
/*  79 */       event.getLocationInformation();
/*     */     }
/*  81 */     synchronized (this.bf) {
/*  82 */       while (this.bf.isFull()) {
/*     */         try
/*     */         {
/*  85 */           this.bf.wait();
/*     */         } catch (InterruptedException e) {
/*  87 */           if (!this.interruptedWarningMessage) {
/*  88 */             this.interruptedWarningMessage = true;
/*  89 */             LogLog.warn("AsyncAppender interrupted.", e);
/*     */           } else {
/*  91 */             LogLog.warn("AsyncAppender interrupted again.");
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  97 */       this.bf.put(event);
/*  98 */       if (this.bf.wasEmpty())
/*     */       {
/* 100 */         this.bf.notify();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 111 */     synchronized (this)
/*     */     {
/* 113 */       if (this.closed) {
/* 114 */         return;
/*     */       }
/* 116 */       this.closed = true;
/*     */     }
/*     */ 
/* 123 */     this.dispatcher.close();
/*     */     try {
/* 125 */       this.dispatcher.join();
/*     */     } catch (InterruptedException e) {
/* 127 */       LogLog.error("Got an InterruptedException while waiting for the dispatcher to finish.", e);
/*     */     }
/*     */ 
/* 130 */     this.dispatcher = null;
/* 131 */     this.bf = null;
/*     */   }
/*     */ 
/*     */   public Enumeration getAllAppenders() {
/* 135 */     synchronized (this.aai) {
/* 136 */       return this.aai.getAllAppenders();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Appender getAppender(String name) {
/* 141 */     synchronized (this.aai) {
/* 142 */       return this.aai.getAppender(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 150 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean isAttached(Appender appender)
/*     */   {
/* 157 */     return this.aai.isAttached(appender);
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 166 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAllAppenders() {
/* 170 */     synchronized (this.aai) {
/* 171 */       this.aai.removeAllAppenders();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(Appender appender)
/*     */   {
/* 177 */     synchronized (this.aai) {
/* 178 */       this.aai.removeAppender(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(String name) {
/* 183 */     synchronized (this.aai) {
/* 184 */       this.aai.removeAppender(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/* 199 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int size)
/*     */   {
/* 216 */     this.bf.resize(size);
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 223 */     return this.bf.getMaxSize();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.AsyncAppender
 * JD-Core Version:    0.6.0
 */
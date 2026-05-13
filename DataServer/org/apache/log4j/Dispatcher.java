/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*     */ import org.apache.log4j.helpers.BoundedFIFO;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ class Dispatcher extends Thread
/*     */ {
/*     */   BoundedFIFO bf;
/*     */   AppenderAttachableImpl aai;
/* 234 */   boolean interrupted = false;
/*     */   AsyncAppender container;
/*     */ 
/*     */   Dispatcher(BoundedFIFO bf, AsyncAppender container)
/*     */   {
/* 238 */     this.bf = bf;
/* 239 */     this.container = container;
/* 240 */     this.aai = container.aai;
/*     */ 
/* 243 */     setDaemon(true);
/*     */ 
/* 245 */     setPriority(1);
/* 246 */     setName("Dispatcher-" + getName());
/*     */   }
/*     */ 
/*     */   void close()
/*     */   {
/* 255 */     synchronized (this.bf) {
/* 256 */       this.interrupted = true;
/*     */ 
/* 259 */       if (this.bf.length() == 0)
/* 260 */         this.bf.notify();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true)
/*     */     {
/*     */       LoggingEvent event;
/* 284 */       synchronized (this.bf) {
/* 285 */         if (this.bf.length() != 0)
/*     */           continue;
/* 287 */         if (this.interrupted)
/*     */         {
/*     */           break;
/*     */         }
/*     */         try
/*     */         {
/* 293 */           this.bf.wait();
/*     */         } catch (java.lang.InterruptedException ) {
/* 295 */           LogLog.error("The dispathcer should not be interrupted.");
/* 296 */           break;
/*     */         }
/*     */ 
/* 299 */         event = this.bf.get();
/* 300 */         if (!this.bf.wasFull())
/*     */           continue;
/* 302 */         this.bf.notify();
/*     */       }
/*     */ 
/* 308 */       synchronized (this.container.aai) {
/* 309 */         if ((this.aai != null) && (event != null)) {
/* 310 */           this.aai.appendLoopOnAppenders(event);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 316 */     this.aai.removeAllAppenders();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Dispatcher
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.IoEvent;
/*     */ import org.apache.mina.core.session.IoEventType;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class WriteRequestFilter extends IoFilterAdapter
/*     */ {
/*     */   private final IoEventQueueHandler queueHandler;
/*     */ 
/*     */   public WriteRequestFilter()
/*     */   {
/*  72 */     this(new IoEventQueueThrottle());
/*     */   }
/*     */ 
/*     */   public WriteRequestFilter(IoEventQueueHandler queueHandler)
/*     */   {
/*  79 */     if (queueHandler == null) {
/*  80 */       throw new NullPointerException("queueHandler");
/*     */     }
/*  82 */     this.queueHandler = queueHandler;
/*     */   }
/*     */ 
/*     */   public IoEventQueueHandler getQueueHandler()
/*     */   {
/*  90 */     return this.queueHandler;
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/*  98 */     IoEvent e = new IoEvent(IoEventType.WRITE, session, writeRequest);
/*     */ 
/* 100 */     if (this.queueHandler.accept(this, e)) {
/* 101 */       nextFilter.filterWrite(session, writeRequest);
/* 102 */       WriteFuture writeFuture = writeRequest.getFuture();
/* 103 */       if (writeFuture == null) {
/* 104 */         return;
/*     */       }
/*     */ 
/* 108 */       this.queueHandler.offered(this, e);
/* 109 */       writeFuture.addListener(new IoFutureListener(e) {
/*     */         public void operationComplete(WriteFuture future) {
/* 111 */           WriteRequestFilter.this.queueHandler.polled(WriteRequestFilter.this, this.val$e);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.executor.WriteRequestFilter
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.core.future;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ public class CompositeIoFuture<E extends IoFuture> extends DefaultIoFuture
/*    */ {
/* 40 */   private final CompositeIoFuture<E>.NotifyingListener listener = new NotifyingListener(null);
/* 41 */   private final AtomicInteger unnotified = new AtomicInteger();
/*    */   private volatile boolean constructionFinished;
/*    */ 
/*    */   public CompositeIoFuture(Iterable<E> children)
/*    */   {
/* 45 */     super(null);
/*    */ 
/* 47 */     for (IoFuture f : children) {
/* 48 */       f.addListener(this.listener);
/* 49 */       this.unnotified.incrementAndGet();
/*    */     }
/*    */ 
/* 52 */     this.constructionFinished = true;
/* 53 */     if (this.unnotified.get() == 0)
/* 54 */       setValue(Boolean.valueOf(true)); 
/*    */   }
/*    */   private class NotifyingListener implements IoFutureListener<IoFuture> {
/*    */     private NotifyingListener() {
/*    */     }
/*    */ 
/*    */     public void operationComplete(IoFuture future) {
/* 60 */       if ((CompositeIoFuture.this.unnotified.decrementAndGet() == 0) && (CompositeIoFuture.this.constructionFinished))
/* 61 */         CompositeIoFuture.this.setValue(Boolean.valueOf(true));
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.future.CompositeIoFuture
 * JD-Core Version:    0.6.0
 */
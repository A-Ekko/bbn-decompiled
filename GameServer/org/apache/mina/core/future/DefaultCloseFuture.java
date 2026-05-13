/*    */ package org.apache.mina.core.future;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class DefaultCloseFuture extends DefaultIoFuture
/*    */   implements CloseFuture
/*    */ {
/*    */   public DefaultCloseFuture(IoSession session)
/*    */   {
/* 36 */     super(session);
/*    */   }
/*    */ 
/*    */   public boolean isClosed() {
/* 40 */     if (isDone()) {
/* 41 */       return ((Boolean)getValue()).booleanValue();
/*    */     }
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   public void setClosed()
/*    */   {
/* 48 */     setValue(Boolean.TRUE);
/*    */   }
/*    */ 
/*    */   public CloseFuture await() throws InterruptedException
/*    */   {
/* 53 */     return (CloseFuture)super.await();
/*    */   }
/*    */ 
/*    */   public CloseFuture awaitUninterruptibly()
/*    */   {
/* 58 */     return (CloseFuture)super.awaitUninterruptibly();
/*    */   }
/*    */ 
/*    */   public CloseFuture addListener(IoFutureListener<?> listener)
/*    */   {
/* 63 */     return (CloseFuture)super.addListener(listener);
/*    */   }
/*    */ 
/*    */   public CloseFuture removeListener(IoFutureListener<?> listener)
/*    */   {
/* 68 */     return (CloseFuture)super.removeListener(listener);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.DefaultCloseFuture
 * JD-Core Version:    0.6.0
 */
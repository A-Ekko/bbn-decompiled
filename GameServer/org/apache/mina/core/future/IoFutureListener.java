/*    */ package org.apache.mina.core.future;
/*    */ 
/*    */ import java.util.EventListener;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public abstract interface IoFutureListener<F extends IoFuture> extends EventListener
/*    */ {
/* 38 */   public static final IoFutureListener<IoFuture> CLOSE = new IoFutureListener() {
/*    */     public void operationComplete(IoFuture future) {
/* 40 */       future.getSession().close(true);
/*    */     }
/* 38 */   };
/*    */ 
/*    */   public abstract void operationComplete(F paramF);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.future.IoFutureListener
 * JD-Core Version:    0.6.0
 */
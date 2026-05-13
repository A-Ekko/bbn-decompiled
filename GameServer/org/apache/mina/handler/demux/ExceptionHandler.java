/*    */ package org.apache.mina.handler.demux;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public abstract interface ExceptionHandler<E extends Throwable>
/*    */ {
/* 38 */   public static final ExceptionHandler<Throwable> NOOP = new ExceptionHandler() {
/*    */     public void exceptionCaught(IoSession session, Throwable cause) {  } } ;
/*    */ 
/* 47 */   public static final ExceptionHandler<Throwable> CLOSE = new ExceptionHandler() {
/*    */     public void exceptionCaught(IoSession session, Throwable cause) {
/* 49 */       session.close(true);
/*    */     }
/* 47 */   };
/*    */ 
/*    */   public abstract void exceptionCaught(IoSession paramIoSession, E paramE)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.demux.ExceptionHandler
 * JD-Core Version:    0.6.0
 */
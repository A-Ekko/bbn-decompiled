/*    */ package org.apache.mina.handler.demux;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public abstract interface MessageHandler<E>
/*    */ {
/* 39 */   public static final MessageHandler<Object> NOOP = new MessageHandler() {
/*    */     public void handleMessage(IoSession session, Object message) {  } } ;
/*    */ 
/*    */   public abstract void handleMessage(IoSession paramIoSession, E paramE)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.demux.MessageHandler
 * JD-Core Version:    0.6.0
 */
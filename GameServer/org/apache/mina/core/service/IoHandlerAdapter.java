/*    */ package org.apache.mina.core.service;
/*    */ 
/*    */ import org.apache.mina.core.session.IdleStatus;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class IoHandlerAdapter
/*    */   implements IoHandler
/*    */ {
/* 38 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*    */ 
/*    */   public void sessionCreated(IoSession session) throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sessionOpened(IoSession session) throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void sessionClosed(IoSession session) throws Exception {
/*    */   }
/*    */ 
/*    */   public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
/*    */   }
/*    */ 
/*    */   public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
/* 55 */     if (this.logger.isWarnEnabled())
/* 56 */       this.logger.warn("EXCEPTION, please implement " + getClass().getName() + ".exceptionCaught() for proper handling:", cause);
/*    */   }
/*    */ 
/*    */   public void messageReceived(IoSession session, Object message)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ 
/*    */   public void messageSent(IoSession session, Object message)
/*    */     throws Exception
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.IoHandlerAdapter
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.handler.multiton;
/*     */ 
/*     */ import org.apache.mina.core.service.IoHandler;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class SingleSessionIoHandlerDelegate
/*     */   implements IoHandler
/*     */ {
/*  41 */   public static final AttributeKey HANDLER = new AttributeKey(SingleSessionIoHandlerDelegate.class, "handler");
/*     */   private final SingleSessionIoHandlerFactory factory;
/*     */ 
/*     */   public SingleSessionIoHandlerDelegate(SingleSessionIoHandlerFactory factory)
/*     */   {
/*  57 */     if (factory == null) {
/*  58 */       throw new NullPointerException("factory");
/*     */     }
/*  60 */     this.factory = factory;
/*     */   }
/*     */ 
/*     */   public SingleSessionIoHandlerFactory getFactory()
/*     */   {
/*  68 */     return this.factory;
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoSession session)
/*     */     throws Exception
/*     */   {
/*  79 */     SingleSessionIoHandler handler = this.factory.getHandler(session);
/*  80 */     session.setAttribute(HANDLER, handler);
/*  81 */     handler.sessionCreated();
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoSession session)
/*     */     throws Exception
/*     */   {
/*  90 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/*  92 */     handler.sessionOpened();
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoSession session)
/*     */     throws Exception
/*     */   {
/* 101 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/* 103 */     handler.sessionClosed();
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 113 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/* 115 */     handler.sessionIdle(status);
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 125 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/* 127 */     handler.exceptionCaught(cause);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 137 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/* 139 */     handler.messageReceived(message);
/*     */   }
/*     */ 
/*     */   public void messageSent(IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 148 */     SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
/*     */ 
/* 150 */     handler.messageSent(message);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.multiton.SingleSessionIoHandlerDelegate
 * JD-Core Version:    0.6.0
 */
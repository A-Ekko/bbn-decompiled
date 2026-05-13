/*    */ package org.apache.mina.handler.chain;
/*    */ 
/*    */ import org.apache.mina.core.service.IoHandlerAdapter;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class ChainedIoHandler extends IoHandlerAdapter
/*    */ {
/*    */   private final IoHandlerChain chain;
/*    */ 
/*    */   public ChainedIoHandler()
/*    */   {
/* 40 */     this.chain = new IoHandlerChain();
/*    */   }
/*    */ 
/*    */   public ChainedIoHandler(IoHandlerChain chain)
/*    */   {
/* 50 */     if (chain == null) {
/* 51 */       throw new NullPointerException("chain");
/*    */     }
/* 53 */     this.chain = chain;
/*    */   }
/*    */ 
/*    */   public IoHandlerChain getChain()
/*    */   {
/* 61 */     return this.chain;
/*    */   }
/*    */ 
/*    */   public void messageReceived(IoSession session, Object message)
/*    */     throws Exception
/*    */   {
/* 72 */     this.chain.execute(null, session, message);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.chain.ChainedIoHandler
 * JD-Core Version:    0.6.0
 */
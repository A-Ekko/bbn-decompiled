/*    */ package org.apache.mina.proxy.handlers.socks;
/*    */ 
/*    */ import org.apache.mina.proxy.AbstractProxyLogicHandler;
/*    */ import org.apache.mina.proxy.session.ProxyIoSession;
/*    */ 
/*    */ public abstract class AbstractSocksLogicHandler extends AbstractProxyLogicHandler
/*    */ {
/*    */   protected final SocksProxyRequest request;
/*    */ 
/*    */   public AbstractSocksLogicHandler(ProxyIoSession proxyIoSession)
/*    */   {
/* 47 */     super(proxyIoSession);
/* 48 */     this.request = ((SocksProxyRequest)proxyIoSession.getRequest());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.socks.AbstractSocksLogicHandler
 * JD-Core Version:    0.6.0
 */
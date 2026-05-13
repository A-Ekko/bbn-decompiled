/*    */ package org.apache.mina.proxy;
/*    */ 
/*    */ import org.apache.mina.core.service.IoHandlerAdapter;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
/*    */ import org.apache.mina.proxy.session.ProxyIoSession;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractProxyIoHandler extends IoHandlerAdapter
/*    */ {
/* 38 */   private static final Logger logger = LoggerFactory.getLogger(AbstractProxyIoHandler.class);
/*    */ 
/*    */   public abstract void proxySessionOpened(IoSession paramIoSession)
/*    */     throws Exception;
/*    */ 
/*    */   public final void sessionOpened(IoSession session)
/*    */     throws Exception
/*    */   {
/* 51 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*    */ 
/* 54 */     if (((proxyIoSession.getRequest() instanceof SocksProxyRequest)) || (proxyIoSession.isAuthenticationFailed()) || (proxyIoSession.getHandler().isHandshakeComplete()))
/*    */     {
/* 57 */       proxySessionOpened(session);
/*    */     }
/* 59 */     else logger.debug("Filtered session opened event !");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.AbstractProxyIoHandler
 * JD-Core Version:    0.6.0
 */
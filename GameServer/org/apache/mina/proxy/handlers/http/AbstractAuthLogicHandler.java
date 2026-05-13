/*    */ package org.apache.mina.proxy.handlers.http;
/*    */ 
/*    */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*    */ import org.apache.mina.proxy.ProxyAuthException;
/*    */ import org.apache.mina.proxy.handlers.ProxyRequest;
/*    */ import org.apache.mina.proxy.session.ProxyIoSession;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractAuthLogicHandler
/*    */ {
/* 38 */   private static final Logger logger = LoggerFactory.getLogger(AbstractAuthLogicHandler.class);
/*    */   protected ProxyRequest request;
/*    */   protected ProxyIoSession proxyIoSession;
/* 54 */   protected int step = 0;
/*    */ 
/*    */   protected AbstractAuthLogicHandler(ProxyIoSession proxyIoSession)
/*    */     throws ProxyAuthException
/*    */   {
/* 64 */     this.proxyIoSession = proxyIoSession;
/* 65 */     this.request = proxyIoSession.getRequest();
/*    */   }
/*    */ 
/*    */   public abstract void doHandshake(IoFilter.NextFilter paramNextFilter)
/*    */     throws ProxyAuthException;
/*    */ 
/*    */   public abstract void handleResponse(HttpProxyResponse paramHttpProxyResponse)
/*    */     throws ProxyAuthException;
/*    */ 
/*    */   protected void writeRequest(IoFilter.NextFilter nextFilter, HttpProxyRequest request)
/*    */     throws ProxyAuthException
/*    */   {
/* 95 */     logger.debug("  sending HTTP request");
/*    */ 
/* 97 */     ((AbstractHttpLogicHandler)this.proxyIoSession.getHandler()).writeRequest(nextFilter, request);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler
 * JD-Core Version:    0.6.0
 */
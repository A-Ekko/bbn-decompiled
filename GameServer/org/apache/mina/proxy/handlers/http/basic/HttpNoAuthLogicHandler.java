/*    */ package org.apache.mina.proxy.handlers.http.basic;
/*    */ 
/*    */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*    */ import org.apache.mina.proxy.ProxyAuthException;
/*    */ import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
/*    */ import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
/*    */ import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
/*    */ import org.apache.mina.proxy.session.ProxyIoSession;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class HttpNoAuthLogicHandler extends AbstractAuthLogicHandler
/*    */ {
/* 39 */   private static final Logger logger = LoggerFactory.getLogger(HttpNoAuthLogicHandler.class);
/*    */ 
/*    */   public HttpNoAuthLogicHandler(ProxyIoSession proxyIoSession)
/*    */     throws ProxyAuthException
/*    */   {
/* 47 */     super(proxyIoSession);
/*    */   }
/*    */ 
/*    */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*    */     throws ProxyAuthException
/*    */   {
/* 56 */     logger.debug(" doHandshake()");
/*    */ 
/* 59 */     writeRequest(nextFilter, (HttpProxyRequest)this.request);
/* 60 */     this.step += 1;
/*    */   }
/*    */ 
/*    */   public void handleResponse(HttpProxyResponse response)
/*    */     throws ProxyAuthException
/*    */   {
/* 70 */     throw new ProxyAuthException("Received error response code (" + response.getStatusLine() + ").");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.basic.HttpNoAuthLogicHandler
 * JD-Core Version:    0.6.0
 */
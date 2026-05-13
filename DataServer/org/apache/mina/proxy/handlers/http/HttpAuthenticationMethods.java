/*    */ package org.apache.mina.proxy.handlers.http;
/*    */ 
/*    */ import org.apache.mina.proxy.ProxyAuthException;
/*    */ import org.apache.mina.proxy.handlers.http.basic.HttpBasicAuthLogicHandler;
/*    */ import org.apache.mina.proxy.handlers.http.basic.HttpNoAuthLogicHandler;
/*    */ import org.apache.mina.proxy.handlers.http.digest.HttpDigestAuthLogicHandler;
/*    */ import org.apache.mina.proxy.handlers.http.ntlm.HttpNTLMAuthLogicHandler;
/*    */ import org.apache.mina.proxy.session.ProxyIoSession;
/*    */ 
/*    */ public enum HttpAuthenticationMethods
/*    */ {
/* 38 */   NO_AUTH(1), BASIC(2), NTLM(3), DIGEST(4);
/*    */ 
/*    */   private final int id;
/*    */ 
/* 43 */   private HttpAuthenticationMethods(int id) { this.id = id;
/*    */   }
/*    */ 
/*    */   public int getId()
/*    */   {
/* 51 */     return this.id;
/*    */   }
/*    */ 
/*    */   public AbstractAuthLogicHandler getNewHandler(ProxyIoSession proxyIoSession)
/*    */     throws ProxyAuthException
/*    */   {
/* 62 */     switch (1.$SwitchMap$org$apache$mina$proxy$handlers$http$HttpAuthenticationMethods[ordinal()]) {
/*    */     case 1:
/* 64 */       return new HttpBasicAuthLogicHandler(proxyIoSession);
/*    */     case 2:
/* 67 */       return new HttpDigestAuthLogicHandler(proxyIoSession);
/*    */     case 3:
/* 70 */       return new HttpNTLMAuthLogicHandler(proxyIoSession);
/*    */     case 4:
/* 73 */       return new HttpNoAuthLogicHandler(proxyIoSession);
/*    */     }
/*    */ 
/* 76 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.HttpAuthenticationMethods
 * JD-Core Version:    0.6.0
 */
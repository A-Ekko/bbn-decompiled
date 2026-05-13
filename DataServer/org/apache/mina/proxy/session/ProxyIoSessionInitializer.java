/*    */ package org.apache.mina.proxy.session;
/*    */ 
/*    */ import org.apache.mina.core.future.ConnectFuture;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.core.session.IoSessionInitializer;
/*    */ 
/*    */ public class ProxyIoSessionInitializer<T extends ConnectFuture>
/*    */   implements IoSessionInitializer<T>
/*    */ {
/*    */   private final IoSessionInitializer<T> wrappedSessionInitializer;
/*    */   private final ProxyIoSession proxyIoSession;
/*    */ 
/*    */   public ProxyIoSessionInitializer(IoSessionInitializer<T> wrappedSessionInitializer, ProxyIoSession proxyIoSession)
/*    */   {
/* 44 */     this.wrappedSessionInitializer = wrappedSessionInitializer;
/* 45 */     this.proxyIoSession = proxyIoSession;
/*    */   }
/*    */ 
/*    */   public ProxyIoSession getProxySession() {
/* 49 */     return this.proxyIoSession;
/*    */   }
/*    */ 
/*    */   public void initializeSession(IoSession session, T future) {
/* 53 */     if (this.wrappedSessionInitializer != null) {
/* 54 */       this.wrappedSessionInitializer.initializeSession(session, future);
/*    */     }
/*    */ 
/* 57 */     if (this.proxyIoSession != null) {
/* 58 */       this.proxyIoSession.setSession(session);
/* 59 */       session.setAttribute(ProxyIoSession.PROXY_SESSION, this.proxyIoSession);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.session.ProxyIoSessionInitializer
 * JD-Core Version:    0.6.0
 */
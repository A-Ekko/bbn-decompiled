/*     */ package org.apache.mina.proxy.session;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.proxy.ProxyConnector;
/*     */ import org.apache.mina.proxy.ProxyLogicHandler;
/*     */ import org.apache.mina.proxy.event.IoSessionEventQueue;
/*     */ import org.apache.mina.proxy.filter.ProxyFilter;
/*     */ import org.apache.mina.proxy.handlers.ProxyRequest;
/*     */ import org.apache.mina.proxy.handlers.http.HttpAuthenticationMethods;
/*     */ 
/*     */ public class ProxyIoSession
/*     */ {
/*  44 */   public static final String PROXY_SESSION = ProxyConnector.class.getName() + ".ProxySession";
/*     */   private static final String DEFAULT_ENCODING = "ISO-8859-1";
/*     */   private List<HttpAuthenticationMethods> preferedOrder;
/*     */   private ProxyRequest request;
/*     */   private ProxyLogicHandler handler;
/*     */   private ProxyFilter proxyFilter;
/*     */   private IoSession session;
/*     */   private ProxyConnector connector;
/*  83 */   private InetSocketAddress proxyAddress = null;
/*     */ 
/*  89 */   private boolean reconnectionNeeded = false;
/*     */   private String charsetName;
/*     */   private IoSessionEventQueue eventQueue;
/*     */   private boolean authenticationFailed;
/*     */ 
/*     */   public IoSessionEventQueue getEventQueue()
/*     */   {
/* 107 */     return this.eventQueue;
/*     */   }
/*     */ 
/*     */   public ProxyIoSession(InetSocketAddress proxyAddress, ProxyRequest request) {
/* 111 */     setProxyAddress(proxyAddress);
/* 112 */     setRequest(request);
/*     */   }
/*     */ 
/*     */   public List<HttpAuthenticationMethods> getPreferedOrder() {
/* 116 */     return this.preferedOrder;
/*     */   }
/*     */ 
/*     */   public void setPreferedOrder(List<HttpAuthenticationMethods> preferedOrder) {
/* 120 */     this.preferedOrder = preferedOrder;
/*     */   }
/*     */ 
/*     */   public ProxyLogicHandler getHandler() {
/* 124 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public void setHandler(ProxyLogicHandler handler) {
/* 128 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   public ProxyFilter getProxyFilter() {
/* 132 */     return this.proxyFilter;
/*     */   }
/*     */ 
/*     */   public void setProxyFilter(ProxyFilter proxyFilter)
/*     */   {
/* 140 */     this.proxyFilter = proxyFilter;
/*     */   }
/*     */ 
/*     */   public ProxyRequest getRequest() {
/* 144 */     return this.request;
/*     */   }
/*     */ 
/*     */   public void setRequest(ProxyRequest request) {
/* 148 */     if (request == null) {
/* 149 */       throw new NullPointerException("request cannot be null");
/*     */     }
/*     */ 
/* 152 */     this.request = request;
/*     */   }
/*     */ 
/*     */   public IoSession getSession() {
/* 156 */     return this.session;
/*     */   }
/*     */ 
/*     */   public void setSession(IoSession session)
/*     */   {
/* 164 */     this.session = session;
/* 165 */     this.eventQueue = new IoSessionEventQueue(this);
/*     */   }
/*     */ 
/*     */   public ProxyConnector getConnector() {
/* 169 */     return this.connector;
/*     */   }
/*     */ 
/*     */   public void setConnector(ProxyConnector connector)
/*     */   {
/* 177 */     this.connector = connector;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getProxyAddress() {
/* 181 */     return this.proxyAddress;
/*     */   }
/*     */ 
/*     */   public void setProxyAddress(InetSocketAddress proxyAddress) {
/* 185 */     if (proxyAddress == null) {
/* 186 */       throw new IllegalArgumentException("proxyAddress cannot be null");
/*     */     }
/*     */ 
/* 189 */     if (!(proxyAddress instanceof InetSocketAddress)) {
/* 190 */       throw new NullPointerException("Unsupported proxyAddress type " + proxyAddress.getClass().getName());
/*     */     }
/*     */ 
/* 194 */     this.proxyAddress = proxyAddress;
/*     */   }
/*     */ 
/*     */   public boolean isReconnectionNeeded() {
/* 198 */     return this.reconnectionNeeded;
/*     */   }
/*     */ 
/*     */   public void setReconnectionNeeded(boolean reconnectionNeeded)
/*     */   {
/* 206 */     this.reconnectionNeeded = reconnectionNeeded;
/*     */   }
/*     */ 
/*     */   public Charset getCharset() {
/* 210 */     return Charset.forName(getCharsetName());
/*     */   }
/*     */ 
/*     */   public synchronized String getCharsetName() {
/* 214 */     if (this.charsetName == null) {
/* 215 */       this.charsetName = "ISO-8859-1";
/*     */     }
/*     */ 
/* 218 */     return this.charsetName;
/*     */   }
/*     */ 
/*     */   public void setCharsetName(String charsetName) {
/* 222 */     this.charsetName = charsetName;
/*     */   }
/*     */ 
/*     */   public boolean isAuthenticationFailed() {
/* 226 */     return this.authenticationFailed;
/*     */   }
/*     */ 
/*     */   public void setAuthenticationFailed(boolean authenticationFailed) {
/* 230 */     this.authenticationFailed = authenticationFailed;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.session.ProxyIoSession
 * JD-Core Version:    0.6.0
 */
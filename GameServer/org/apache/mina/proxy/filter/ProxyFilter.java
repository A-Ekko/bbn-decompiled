/*     */ package org.apache.mina.proxy.filter;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.proxy.ProxyAuthException;
/*     */ import org.apache.mina.proxy.ProxyLogicHandler;
/*     */ import org.apache.mina.proxy.event.IoSessionEvent;
/*     */ import org.apache.mina.proxy.event.IoSessionEventQueue;
/*     */ import org.apache.mina.proxy.event.IoSessionEventType;
/*     */ import org.apache.mina.proxy.handlers.ProxyRequest;
/*     */ import org.apache.mina.proxy.handlers.http.HttpSmartProxyHandler;
/*     */ import org.apache.mina.proxy.handlers.socks.Socks4LogicHandler;
/*     */ import org.apache.mina.proxy.handlers.socks.Socks5LogicHandler;
/*     */ import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ProxyFilter extends IoFilterAdapter
/*     */ {
/*  59 */   private static final Logger logger = LoggerFactory.getLogger(ProxyFilter.class);
/*     */ 
/*     */   public void onPreAdd(IoFilterChain chain, String name, IoFilter.NextFilter nextFilter)
/*     */   {
/*  81 */     if (chain.contains(ProxyFilter.class))
/*  82 */       throw new IllegalStateException("A filter chain cannot contain more than one ProxyFilter.");
/*     */   }
/*     */ 
/*     */   public void onPreRemove(IoFilterChain chain, String name, IoFilter.NextFilter nextFilter)
/*     */   {
/*  98 */     IoSession session = chain.getSession();
/*  99 */     session.removeAttribute(ProxyIoSession.PROXY_SESSION);
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 114 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */ 
/* 116 */     proxyIoSession.setAuthenticationFailed(true);
/* 117 */     super.exceptionCaught(nextFilter, session, cause);
/*     */   }
/*     */ 
/*     */   private ProxyLogicHandler getProxyHandler(IoSession session)
/*     */   {
/* 127 */     ProxyLogicHandler handler = ((ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION)).getHandler();
/*     */ 
/* 130 */     if (handler == null) {
/* 131 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 135 */     if (handler.getProxyIoSession().getProxyFilter() != this) {
/* 136 */       throw new IllegalArgumentException("Not managed by this filter.");
/*     */     }
/*     */ 
/* 139 */     return handler;
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws ProxyAuthException
/*     */   {
/* 154 */     ProxyLogicHandler handler = getProxyHandler(session);
/*     */ 
/* 156 */     synchronized (handler) {
/* 157 */       IoBuffer buf = (IoBuffer)message;
/*     */ 
/* 159 */       if (handler.isHandshakeComplete())
/*     */       {
/* 161 */         nextFilter.messageReceived(session, buf);
/*     */       }
/*     */       else {
/* 164 */         logger.debug(" Data Read: {} ({})", handler, buf);
/*     */ 
/* 168 */         while ((buf.hasRemaining()) && (!handler.isHandshakeComplete())) {
/* 169 */           logger.debug(" Pre-handshake - passing to handler");
/*     */ 
/* 171 */           int pos = buf.position();
/* 172 */           handler.messageReceived(nextFilter, buf);
/*     */ 
/* 175 */           if ((buf.position() == pos) || (session.isClosing())) {
/* 176 */             return;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 181 */         if (buf.hasRemaining()) {
/* 182 */           logger.debug(" Passing remaining data to next filter");
/*     */ 
/* 184 */           nextFilter.messageReceived(session, buf);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */   {
/* 201 */     writeData(nextFilter, session, writeRequest, false);
/*     */   }
/*     */ 
/*     */   public void writeData(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest, boolean isHandshakeData)
/*     */   {
/* 215 */     ProxyLogicHandler handler = getProxyHandler(session);
/*     */ 
/* 217 */     synchronized (handler) {
/* 218 */       if (handler.isHandshakeComplete())
/*     */       {
/* 220 */         nextFilter.filterWrite(session, writeRequest);
/* 221 */       } else if (isHandshakeData) {
/* 222 */         logger.debug("   handshake data: {}", writeRequest.getMessage());
/*     */ 
/* 225 */         nextFilter.filterWrite(session, writeRequest);
/*     */       }
/* 228 */       else if (!session.isConnected())
/*     */       {
/* 230 */         logger.debug(" Write request on closed session. Request ignored.");
/*     */       }
/*     */       else {
/* 233 */         logger.debug(" Handshaking is not complete yet. Buffering write request.");
/* 234 */         handler.enqueueWriteRequest(nextFilter, writeRequest);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 252 */     if ((writeRequest.getMessage() != null) && ((writeRequest.getMessage() instanceof ProxyHandshakeIoBuffer)))
/*     */     {
/* 255 */       return;
/*     */     }
/*     */ 
/* 258 */     nextFilter.messageSent(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 278 */     logger.debug("Session created: " + session);
/* 279 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */ 
/* 281 */     logger.debug("  get proxyIoSession: " + proxyIoSession);
/* 282 */     proxyIoSession.setProxyFilter(this);
/*     */ 
/* 285 */     ProxyLogicHandler handler = proxyIoSession.getHandler();
/*     */ 
/* 289 */     if (handler == null) {
/* 290 */       ProxyRequest request = proxyIoSession.getRequest();
/*     */ 
/* 292 */       if ((request instanceof SocksProxyRequest)) {
/* 293 */         SocksProxyRequest req = (SocksProxyRequest)request;
/* 294 */         if (req.getProtocolVersion() == 4)
/* 295 */           handler = new Socks4LogicHandler(proxyIoSession);
/*     */         else
/* 297 */           handler = new Socks5LogicHandler(proxyIoSession);
/*     */       }
/*     */       else {
/* 300 */         handler = new HttpSmartProxyHandler(proxyIoSession);
/*     */       }
/*     */ 
/* 303 */       proxyIoSession.setHandler(handler);
/* 304 */       handler.doHandshake(nextFilter);
/*     */     }
/*     */ 
/* 307 */     proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.CREATED));
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 323 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */ 
/* 325 */     proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.OPENED));
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 341 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */ 
/* 343 */     proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, status));
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 358 */     ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */ 
/* 360 */     proxyIoSession.getEventQueue().enqueueEventIfNecessary(new IoSessionEvent(nextFilter, session, IoSessionEventType.CLOSED));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.filter.ProxyFilter
 * JD-Core Version:    0.6.0
 */
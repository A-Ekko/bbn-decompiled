/*     */ package org.apache.mina.proxy;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.DefaultWriteFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.proxy.event.IoSessionEventQueue;
/*     */ import org.apache.mina.proxy.filter.ProxyFilter;
/*     */ import org.apache.mina.proxy.filter.ProxyHandshakeIoBuffer;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractProxyLogicHandler
/*     */   implements ProxyLogicHandler
/*     */ {
/*  51 */   private static final Logger logger = LoggerFactory.getLogger(AbstractProxyLogicHandler.class);
/*     */   private ProxyIoSession proxyIoSession;
/*  62 */   private Queue<Event> writeRequestQueue = null;
/*     */ 
/*  67 */   private boolean handshakeComplete = false;
/*     */ 
/*     */   public AbstractProxyLogicHandler(ProxyIoSession proxyIoSession)
/*     */   {
/*  75 */     this.proxyIoSession = proxyIoSession;
/*     */   }
/*     */ 
/*     */   protected ProxyFilter getProxyFilter()
/*     */   {
/*  82 */     return this.proxyIoSession.getProxyFilter();
/*     */   }
/*     */ 
/*     */   protected IoSession getSession()
/*     */   {
/*  89 */     return this.proxyIoSession.getSession();
/*     */   }
/*     */ 
/*     */   public ProxyIoSession getProxyIoSession()
/*     */   {
/*  96 */     return this.proxyIoSession;
/*     */   }
/*     */ 
/*     */   protected WriteFuture writeData(IoFilter.NextFilter nextFilter, IoBuffer data)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 108 */     ProxyHandshakeIoBuffer writeBuffer = new ProxyHandshakeIoBuffer(data);
/*     */ 
/* 110 */     logger.debug("   session write: {}", writeBuffer);
/*     */ 
/* 112 */     WriteFuture writeFuture = new DefaultWriteFuture(getSession());
/* 113 */     getProxyFilter().writeData(nextFilter, getSession(), new DefaultWriteRequest(writeBuffer, writeFuture), true);
/*     */ 
/* 116 */     return writeFuture;
/*     */   }
/*     */ 
/*     */   public boolean isHandshakeComplete()
/*     */   {
/* 124 */     synchronized (this) {
/* 125 */       return this.handshakeComplete;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void setHandshakeComplete()
/*     */   {
/* 133 */     synchronized (this) {
/* 134 */       this.handshakeComplete = true;
/*     */     }
/*     */ 
/* 137 */     ProxyIoSession proxyIoSession = getProxyIoSession();
/* 138 */     proxyIoSession.getConnector().fireConnected(proxyIoSession.getSession()).awaitUninterruptibly();
/*     */ 
/* 142 */     logger.debug("  handshake completed");
/*     */     try
/*     */     {
/* 146 */       proxyIoSession.getEventQueue().flushPendingSessionEvents();
/* 147 */       flushPendingWriteRequests();
/*     */     } catch (Exception ex) {
/* 149 */       logger.error("Unable to flush pending write requests", ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected synchronized void flushPendingWriteRequests()
/*     */     throws Exception
/*     */   {
/* 157 */     logger.debug(" flushPendingWriteRequests()");
/*     */ 
/* 159 */     if (this.writeRequestQueue == null)
/* 160 */       return;
/*     */     Event scheduledWrite;
/* 164 */     while ((scheduledWrite = (Event)this.writeRequestQueue.poll()) != null) {
/* 165 */       logger.debug(" Flushing buffered write request: {}", scheduledWrite.data);
/*     */ 
/* 168 */       getProxyFilter().filterWrite(scheduledWrite.nextFilter, getSession(), (WriteRequest)scheduledWrite.data);
/*     */     }
/*     */ 
/* 173 */     this.writeRequestQueue = null;
/*     */   }
/*     */ 
/*     */   public synchronized void enqueueWriteRequest(IoFilter.NextFilter nextFilter, WriteRequest writeRequest)
/*     */   {
/* 181 */     if (this.writeRequestQueue == null) {
/* 182 */       this.writeRequestQueue = new LinkedList();
/*     */     }
/*     */ 
/* 185 */     this.writeRequestQueue.offer(new Event(nextFilter, writeRequest));
/*     */   }
/*     */ 
/*     */   protected void closeSession(String message, Throwable t)
/*     */   {
/* 192 */     if (t != null) {
/* 193 */       logger.error(message, t);
/* 194 */       this.proxyIoSession.setAuthenticationFailed(true);
/*     */     } else {
/* 196 */       logger.error(message);
/*     */     }
/*     */ 
/* 199 */     getSession().close(true);
/*     */   }
/*     */ 
/*     */   protected void closeSession(String message)
/*     */   {
/* 208 */     closeSession(message, null);
/*     */   }
/*     */ 
/*     */   private static final class Event
/*     */   {
/*     */     private final IoFilter.NextFilter nextFilter;
/*     */     private final Object data;
/*     */ 
/*     */     Event(IoFilter.NextFilter nextFilter, Object data)
/*     */     {
/* 220 */       this.nextFilter = nextFilter;
/* 221 */       this.data = data;
/*     */     }
/*     */ 
/*     */     public Object getData() {
/* 225 */       return this.data;
/*     */     }
/*     */ 
/*     */     public IoFilter.NextFilter getNextFilter() {
/* 229 */       return this.nextFilter;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.AbstractProxyLogicHandler
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.proxy.event;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.proxy.ProxyConnector;
/*     */ import org.apache.mina.proxy.ProxyLogicHandler;
/*     */ import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IoSessionEventQueue
/*     */ {
/*  39 */   private static final Logger logger = LoggerFactory.getLogger(IoSessionEventQueue.class);
/*     */   private ProxyIoSession proxyIoSession;
/*  50 */   private Queue<IoSessionEvent> sessionEventsQueue = new LinkedList();
/*     */ 
/*     */   public IoSessionEventQueue(ProxyIoSession proxyIoSession) {
/*  53 */     this.proxyIoSession = proxyIoSession;
/*     */   }
/*     */ 
/*     */   private void discardSessionQueueEvents()
/*     */   {
/*  60 */     synchronized (this.sessionEventsQueue)
/*     */     {
/*  62 */       this.sessionEventsQueue.clear();
/*  63 */       logger.debug("Event queue CLEARED");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enqueueEventIfNecessary(IoSessionEvent evt)
/*     */   {
/*  79 */     logger.debug("??? >> Enqueue {}", evt);
/*     */ 
/*  81 */     if ((this.proxyIoSession.getRequest() instanceof SocksProxyRequest))
/*     */     {
/*  83 */       evt.deliverEvent();
/*  84 */       return;
/*     */     }
/*     */ 
/*  87 */     if (this.proxyIoSession.getHandler().isHandshakeComplete()) {
/*  88 */       evt.deliverEvent();
/*     */     }
/*  90 */     else if (evt.getType() == IoSessionEventType.CLOSED) {
/*  91 */       if (this.proxyIoSession.isAuthenticationFailed()) {
/*  92 */         this.proxyIoSession.getConnector().cancelConnectFuture();
/*  93 */         discardSessionQueueEvents();
/*  94 */         evt.deliverEvent();
/*     */       } else {
/*  96 */         discardSessionQueueEvents();
/*     */       }
/*  98 */     } else if (evt.getType() == IoSessionEventType.OPENED)
/*     */     {
/* 101 */       enqueueSessionEvent(evt);
/* 102 */       evt.deliverEvent();
/*     */     } else {
/* 104 */       enqueueSessionEvent(evt);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flushPendingSessionEvents()
/*     */     throws Exception
/*     */   {
/* 115 */     synchronized (this.sessionEventsQueue)
/*     */     {
/*     */       IoSessionEvent evt;
/* 118 */       while ((evt = (IoSessionEvent)this.sessionEventsQueue.poll()) != null) {
/* 119 */         logger.debug(" Flushing buffered event: {}", evt);
/* 120 */         evt.deliverEvent();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void enqueueSessionEvent(IoSessionEvent evt)
/*     */   {
/* 131 */     synchronized (this.sessionEventsQueue) {
/* 132 */       logger.debug("Enqueuing event: {}", evt);
/* 133 */       this.sessionEventsQueue.offer(evt);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.event.IoSessionEventQueue
 * JD-Core Version:    0.6.0
 */
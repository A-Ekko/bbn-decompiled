/*     */ package org.apache.mina.filter.keepalive;
/*     */ 
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class KeepAliveFilter extends IoFilterAdapter
/*     */ {
/* 143 */   private final AttributeKey WAITING_FOR_RESPONSE = new AttributeKey(getClass(), "waitingForResponse");
/*     */ 
/* 145 */   private final AttributeKey IGNORE_READER_IDLE_ONCE = new AttributeKey(getClass(), "ignoreReaderIdleOnce");
/*     */   private final KeepAliveMessageFactory messageFactory;
/*     */   private final IdleStatus interestedIdleStatus;
/*     */   private volatile KeepAliveRequestTimeoutHandler requestTimeoutHandler;
/*     */   private volatile int requestInterval;
/*     */   private volatile int requestTimeout;
/*     */   private volatile boolean forwardEvent;
/*     */ 
/*     */   public KeepAliveFilter(KeepAliveMessageFactory messageFactory)
/*     */   {
/* 166 */     this(messageFactory, IdleStatus.READER_IDLE, KeepAliveRequestTimeoutHandler.CLOSE);
/*     */   }
/*     */ 
/*     */   public KeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus)
/*     */   {
/* 181 */     this(messageFactory, interestedIdleStatus, KeepAliveRequestTimeoutHandler.CLOSE, 60, 30);
/*     */   }
/*     */ 
/*     */   public KeepAliveFilter(KeepAliveMessageFactory messageFactory, KeepAliveRequestTimeoutHandler policy)
/*     */   {
/* 195 */     this(messageFactory, IdleStatus.READER_IDLE, policy, 60, 30);
/*     */   }
/*     */ 
/*     */   public KeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus, KeepAliveRequestTimeoutHandler policy)
/*     */   {
/* 209 */     this(messageFactory, interestedIdleStatus, policy, 60, 30);
/*     */   }
/*     */ 
/*     */   public KeepAliveFilter(KeepAliveMessageFactory messageFactory, IdleStatus interestedIdleStatus, KeepAliveRequestTimeoutHandler policy, int keepAliveRequestInterval, int keepAliveRequestTimeout)
/*     */   {
/* 219 */     if (messageFactory == null) {
/* 220 */       throw new NullPointerException("messageFactory");
/*     */     }
/* 222 */     if (interestedIdleStatus == null) {
/* 223 */       throw new NullPointerException("interestedIdleStatus");
/*     */     }
/* 225 */     if (policy == null) {
/* 226 */       throw new NullPointerException("policy");
/*     */     }
/*     */ 
/* 229 */     this.messageFactory = messageFactory;
/* 230 */     this.interestedIdleStatus = interestedIdleStatus;
/* 231 */     this.requestTimeoutHandler = policy;
/*     */ 
/* 233 */     setRequestInterval(keepAliveRequestInterval);
/* 234 */     setRequestTimeout(keepAliveRequestTimeout);
/*     */   }
/*     */ 
/*     */   public IdleStatus getInterestedIdleStatus() {
/* 238 */     return this.interestedIdleStatus;
/*     */   }
/*     */ 
/*     */   public KeepAliveRequestTimeoutHandler getRequestTimeoutHandler() {
/* 242 */     return this.requestTimeoutHandler;
/*     */   }
/*     */ 
/*     */   public void setRequestTimeoutHandler(KeepAliveRequestTimeoutHandler timeoutHandler) {
/* 246 */     if (timeoutHandler == null) {
/* 247 */       throw new NullPointerException("timeoutHandler");
/*     */     }
/* 249 */     this.requestTimeoutHandler = timeoutHandler;
/*     */   }
/*     */ 
/*     */   public int getRequestInterval() {
/* 253 */     return this.requestInterval;
/*     */   }
/*     */ 
/*     */   public void setRequestInterval(int keepAliveRequestInterval) {
/* 257 */     if (keepAliveRequestInterval <= 0) {
/* 258 */       throw new IllegalArgumentException("keepAliveRequestInterval must be a positive integer: " + keepAliveRequestInterval);
/*     */     }
/*     */ 
/* 262 */     this.requestInterval = keepAliveRequestInterval;
/*     */   }
/*     */ 
/*     */   public int getRequestTimeout() {
/* 266 */     return this.requestTimeout;
/*     */   }
/*     */ 
/*     */   public void setRequestTimeout(int keepAliveRequestTimeout) {
/* 270 */     if (keepAliveRequestTimeout <= 0) {
/* 271 */       throw new IllegalArgumentException("keepAliveRequestTimeout must be a positive integer: " + keepAliveRequestTimeout);
/*     */     }
/*     */ 
/* 275 */     this.requestTimeout = keepAliveRequestTimeout;
/*     */   }
/*     */ 
/*     */   public KeepAliveMessageFactory getMessageFactory() {
/* 279 */     return this.messageFactory;
/*     */   }
/*     */ 
/*     */   public boolean isForwardEvent()
/*     */   {
/* 288 */     return this.forwardEvent;
/*     */   }
/*     */ 
/*     */   public void setForwardEvent(boolean forwardEvent)
/*     */   {
/* 297 */     this.forwardEvent = forwardEvent;
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 303 */     if (parent.contains(this))
/* 304 */       throw new IllegalArgumentException("You can't add the same filter instance more than once. Create another instance and add it.");
/*     */   }
/*     */ 
/*     */   public void onPostAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 313 */     resetStatus(parent.getSession());
/*     */   }
/*     */ 
/*     */   public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 319 */     resetStatus(parent.getSession());
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 326 */       if (this.messageFactory.isRequest(session, message)) {
/* 327 */         Object pongMessage = this.messageFactory.getResponse(session, message);
/*     */ 
/* 330 */         if (pongMessage != null) {
/* 331 */           nextFilter.filterWrite(session, new DefaultWriteRequest(pongMessage));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 336 */       if (this.messageFactory.isResponse(session, message))
/* 337 */         resetStatus(session);
/*     */     }
/*     */     finally {
/* 340 */       if (!isKeepAliveMessage(session, message))
/* 341 */         nextFilter.messageReceived(session, message);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 349 */     Object message = writeRequest.getMessage();
/* 350 */     if (!isKeepAliveMessage(session, message))
/* 351 */       nextFilter.messageSent(session, writeRequest);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 358 */     if (status == this.interestedIdleStatus) {
/* 359 */       if (!session.containsAttribute(this.WAITING_FOR_RESPONSE)) {
/* 360 */         Object pingMessage = this.messageFactory.getRequest(session);
/* 361 */         if (pingMessage != null) {
/* 362 */           nextFilter.filterWrite(session, new DefaultWriteRequest(pingMessage));
/*     */ 
/* 368 */           if (getRequestTimeoutHandler() != KeepAliveRequestTimeoutHandler.DEAF_SPEAKER) {
/* 369 */             markStatus(session);
/* 370 */             if (this.interestedIdleStatus == IdleStatus.BOTH_IDLE)
/* 371 */               session.setAttribute(this.IGNORE_READER_IDLE_ONCE);
/*     */           }
/*     */           else {
/* 374 */             resetStatus(session);
/*     */           }
/*     */         }
/*     */       } else {
/* 378 */         handlePingTimeout(session);
/*     */       }
/* 380 */     } else if ((status == IdleStatus.READER_IDLE) && 
/* 381 */       (session.removeAttribute(this.IGNORE_READER_IDLE_ONCE) == null) && 
/* 382 */       (session.containsAttribute(this.WAITING_FOR_RESPONSE))) {
/* 383 */       handlePingTimeout(session);
/*     */     }
/*     */ 
/* 388 */     if (this.forwardEvent)
/* 389 */       nextFilter.sessionIdle(session, status);
/*     */   }
/*     */ 
/*     */   private void handlePingTimeout(IoSession session) throws Exception
/*     */   {
/* 394 */     resetStatus(session);
/* 395 */     KeepAliveRequestTimeoutHandler handler = getRequestTimeoutHandler();
/* 396 */     if (handler == KeepAliveRequestTimeoutHandler.DEAF_SPEAKER) {
/* 397 */       return;
/*     */     }
/*     */ 
/* 400 */     handler.keepAliveRequestTimedOut(this, session);
/*     */   }
/*     */ 
/*     */   private void markStatus(IoSession session) {
/* 404 */     session.getConfig().setIdleTime(this.interestedIdleStatus, 0);
/* 405 */     session.getConfig().setReaderIdleTime(getRequestTimeout());
/* 406 */     session.setAttribute(this.WAITING_FOR_RESPONSE);
/*     */   }
/*     */ 
/*     */   private void resetStatus(IoSession session) {
/* 410 */     session.getConfig().setReaderIdleTime(0);
/* 411 */     session.getConfig().setWriterIdleTime(0);
/* 412 */     session.getConfig().setIdleTime(this.interestedIdleStatus, getRequestInterval());
/*     */ 
/* 414 */     session.removeAttribute(this.WAITING_FOR_RESPONSE);
/*     */   }
/*     */ 
/*     */   private boolean isKeepAliveMessage(IoSession session, Object message) {
/* 418 */     return (this.messageFactory.isRequest(session, message)) || (this.messageFactory.isResponse(session, message));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.keepalive.KeepAliveFilter
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.ssl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLHandshakeException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.future.DefaultWriteFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.IoFutureListener;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestWrapper;
/*     */ import org.apache.mina.core.write.WriteToClosedSessionException;
/*     */ 
/*     */ public class SslFilter extends IoFilterAdapter
/*     */ {
/*  92 */   public static final AttributeKey SSL_SESSION = new AttributeKey(SslFilter.class, "session");
/*     */ 
/* 104 */   public static final AttributeKey DISABLE_ENCRYPTION_ONCE = new AttributeKey(SslFilter.class, "disableOnce");
/*     */ 
/* 114 */   public static final AttributeKey USE_NOTIFICATION = new AttributeKey(SslFilter.class, "useNotification");
/*     */ 
/* 128 */   public static final AttributeKey PEER_ADDRESS = new AttributeKey(SslFilter.class, "peerAddress");
/*     */ 
/* 135 */   public static final SslFilterMessage SESSION_SECURED = new SslFilterMessage("SESSION_SECURED", null);
/*     */ 
/* 143 */   public static final SslFilterMessage SESSION_UNSECURED = new SslFilterMessage("SESSION_UNSECURED", null);
/*     */ 
/* 146 */   private static final AttributeKey NEXT_FILTER = new AttributeKey(SslFilter.class, "nextFilter");
/* 147 */   private static final AttributeKey SSL_HANDLER = new AttributeKey(SslFilter.class, "handler");
/*     */   private final SSLContext sslContext;
/*     */   private final boolean autoStart;
/*     */   private static final boolean START_HANDSHAKE = true;
/*     */   private boolean client;
/*     */   private boolean needClientAuth;
/*     */   private boolean wantClientAuth;
/*     */   private String[] enabledCipherSuites;
/*     */   private String[] enabledProtocols;
/*     */ 
/*     */   public SslFilter(SSLContext sslContext)
/*     */   {
/* 173 */     this(sslContext, true);
/*     */   }
/*     */ 
/*     */   public SslFilter(SSLContext sslContext, boolean autoStart)
/*     */   {
/* 182 */     if (sslContext == null) {
/* 183 */       throw new NullPointerException("sslContext");
/*     */     }
/*     */ 
/* 186 */     this.sslContext = sslContext;
/* 187 */     this.autoStart = autoStart;
/*     */   }
/*     */ 
/*     */   public SSLSession getSslSession(IoSession session)
/*     */   {
/* 196 */     return (SSLSession)session.getAttribute(SSL_SESSION);
/*     */   }
/*     */ 
/*     */   public boolean startSsl(IoSession session)
/*     */     throws SSLException
/*     */   {
/* 208 */     SslHandler handler = getSslSessionHandler(session);
/*     */     boolean started;
/* 210 */     synchronized (handler)
/*     */     {
/*     */       boolean started;
/* 211 */       if (handler.isOutboundDone()) {
/* 212 */         IoFilter.NextFilter nextFilter = (IoFilter.NextFilter)session.getAttribute(NEXT_FILTER);
/*     */ 
/* 214 */         handler.destroy();
/* 215 */         handler.init();
/* 216 */         handler.handshake(nextFilter);
/* 217 */         started = true;
/*     */       } else {
/* 219 */         started = false;
/*     */       }
/*     */     }
/*     */ 
/* 223 */     handler.flushScheduledEvents();
/* 224 */     return started;
/*     */   }
/*     */ 
/*     */   public boolean isSslStarted(IoSession session)
/*     */   {
/* 234 */     SslHandler handler = (SslHandler)session.getAttribute(SSL_HANDLER);
/*     */ 
/* 236 */     if (handler == null) {
/* 237 */       return false;
/*     */     }
/*     */ 
/* 240 */     synchronized (handler) {
/* 241 */       return !handler.isOutboundDone();
/*     */     }
/*     */   }
/*     */ 
/*     */   public WriteFuture stopSsl(IoSession session)
/*     */     throws SSLException
/*     */   {
/* 254 */     SslHandler handler = getSslSessionHandler(session);
/* 255 */     IoFilter.NextFilter nextFilter = (IoFilter.NextFilter)session.getAttribute(NEXT_FILTER);
/*     */     WriteFuture future;
/* 257 */     synchronized (handler) {
/* 258 */       future = initiateClosure(nextFilter, session);
/*     */     }
/*     */ 
/* 261 */     handler.flushScheduledEvents();
/*     */ 
/* 263 */     return future;
/*     */   }
/*     */ 
/*     */   public boolean isUseClientMode()
/*     */   {
/* 271 */     return this.client;
/*     */   }
/*     */ 
/*     */   public void setUseClientMode(boolean clientMode)
/*     */   {
/* 278 */     this.client = clientMode;
/*     */   }
/*     */ 
/*     */   public boolean isNeedClientAuth()
/*     */   {
/* 286 */     return this.needClientAuth;
/*     */   }
/*     */ 
/*     */   public void setNeedClientAuth(boolean needClientAuth)
/*     */   {
/* 294 */     this.needClientAuth = needClientAuth;
/*     */   }
/*     */ 
/*     */   public boolean isWantClientAuth()
/*     */   {
/* 302 */     return this.wantClientAuth;
/*     */   }
/*     */ 
/*     */   public void setWantClientAuth(boolean wantClientAuth)
/*     */   {
/* 310 */     this.wantClientAuth = wantClientAuth;
/*     */   }
/*     */ 
/*     */   public String[] getEnabledCipherSuites()
/*     */   {
/* 320 */     return this.enabledCipherSuites;
/*     */   }
/*     */ 
/*     */   public void setEnabledCipherSuites(String[] cipherSuites)
/*     */   {
/* 330 */     this.enabledCipherSuites = cipherSuites;
/*     */   }
/*     */ 
/*     */   public String[] getEnabledProtocols()
/*     */   {
/* 340 */     return this.enabledProtocols;
/*     */   }
/*     */ 
/*     */   public void setEnabledProtocols(String[] protocols)
/*     */   {
/* 350 */     this.enabledProtocols = protocols;
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/* 356 */     if (parent.contains(SslFilter.class)) {
/* 357 */       throw new IllegalStateException("Only one " + SslFilter.class.getName() + " is permitted.");
/*     */     }
/*     */ 
/* 361 */     IoSession session = parent.getSession();
/* 362 */     session.setAttribute(NEXT_FILTER, nextFilter);
/*     */ 
/* 365 */     SslHandler handler = new SslHandler(this, this.sslContext, session);
/* 366 */     session.setAttribute(SSL_HANDLER, handler);
/*     */   }
/*     */ 
/*     */   public void onPostAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/* 372 */     if (this.autoStart)
/* 373 */       initiateHandshake(nextFilter, parent.getSession());
/*     */   }
/*     */ 
/*     */   public void onPreRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/* 380 */     IoSession session = parent.getSession();
/* 381 */     stopSsl(session);
/* 382 */     session.removeAttribute(NEXT_FILTER);
/* 383 */     session.removeAttribute(SSL_HANDLER);
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws SSLException
/*     */   {
/* 390 */     SslHandler handler = getSslSessionHandler(session);
/*     */     try {
/* 392 */       synchronized (handler)
/*     */       {
/* 394 */         handler.destroy();
/*     */       }
/*     */ 
/* 397 */       handler.flushScheduledEvents();
/*     */     }
/*     */     finally {
/* 400 */       nextFilter.sessionClosed(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws SSLException
/*     */   {
/* 407 */     SslHandler handler = getSslSessionHandler(session);
/* 408 */     synchronized (handler) {
/* 409 */       if ((!isSslStarted(session)) && (handler.isInboundDone())) {
/* 410 */         handler.scheduleMessageReceived(nextFilter, message);
/*     */       } else {
/* 412 */         IoBuffer buf = (IoBuffer)message;
/*     */         try
/*     */         {
/* 415 */           handler.messageReceived(nextFilter, buf.buf());
/*     */ 
/* 418 */           handleSslData(nextFilter, handler);
/*     */ 
/* 420 */           if (handler.isInboundDone()) {
/* 421 */             if (handler.isOutboundDone())
/* 422 */               handler.destroy();
/*     */             else {
/* 424 */               initiateClosure(nextFilter, session);
/*     */             }
/*     */ 
/* 427 */             if (buf.hasRemaining())
/*     */             {
/* 429 */               handler.scheduleMessageReceived(nextFilter, buf);
/*     */             }
/*     */           }
/*     */         } catch (SSLException ssle) {
/* 433 */           if (!handler.isHandshakeComplete()) {
/* 434 */             SSLException newSsle = new SSLHandshakeException("SSL handshake failed.");
/*     */ 
/* 436 */             newSsle.initCause(ssle);
/* 437 */             ssle = newSsle;
/*     */           }
/*     */ 
/* 440 */           throw ssle;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 445 */     handler.flushScheduledEvents();
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */   {
/* 451 */     if ((writeRequest instanceof EncryptedWriteRequest)) {
/* 452 */       EncryptedWriteRequest wrappedRequest = (EncryptedWriteRequest)writeRequest;
/* 453 */       nextFilter.messageSent(session, wrappedRequest.getParentRequest());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */     throws Exception
/*     */   {
/* 463 */     if ((cause instanceof WriteToClosedSessionException))
/*     */     {
/* 466 */       WriteToClosedSessionException e = (WriteToClosedSessionException)cause;
/* 467 */       List failedRequests = e.getRequests();
/* 468 */       boolean containsCloseNotify = false;
/* 469 */       for (WriteRequest r : failedRequests) {
/* 470 */         if (isCloseNotify(r.getMessage())) {
/* 471 */           containsCloseNotify = true;
/* 472 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 476 */       if (containsCloseNotify) {
/* 477 */         if (failedRequests.size() == 1)
/*     */         {
/* 479 */           return;
/*     */         }
/*     */ 
/* 482 */         List newFailedRequests = new ArrayList(failedRequests.size() - 1);
/*     */ 
/* 484 */         for (WriteRequest r : failedRequests) {
/* 485 */           if (!isCloseNotify(r.getMessage())) {
/* 486 */             newFailedRequests.add(r);
/*     */           }
/*     */         }
/*     */ 
/* 490 */         if (newFailedRequests.isEmpty())
/*     */         {
/* 492 */           return;
/*     */         }
/*     */ 
/* 495 */         cause = new WriteToClosedSessionException(newFailedRequests, cause.getMessage(), cause.getCause());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 500 */     nextFilter.exceptionCaught(session, cause);
/*     */   }
/*     */ 
/*     */   private boolean isCloseNotify(Object message) {
/* 504 */     if (!(message instanceof IoBuffer)) {
/* 505 */       return false;
/*     */     }
/*     */ 
/* 508 */     IoBuffer buf = (IoBuffer)message;
/* 509 */     int offset = buf.position();
/* 510 */     return (buf.remaining() == 23) && (buf.get(offset + 0) == 21) && (buf.get(offset + 1) == 3) && (buf.get(offset + 2) == 1) && (buf.get(offset + 3) == 0) && (buf.get(offset + 4) == 18);
/*     */   }
/*     */ 
/*     */   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws SSLException
/*     */   {
/* 519 */     boolean needsFlush = true;
/* 520 */     SslHandler handler = getSslSessionHandler(session);
/* 521 */     synchronized (handler) {
/* 522 */       if (!isSslStarted(session)) {
/* 523 */         handler.scheduleFilterWrite(nextFilter, writeRequest);
/*     */       }
/* 527 */       else if (session.containsAttribute(DISABLE_ENCRYPTION_ONCE))
/*     */       {
/* 529 */         session.removeAttribute(DISABLE_ENCRYPTION_ONCE);
/* 530 */         handler.scheduleFilterWrite(nextFilter, writeRequest);
/*     */       }
/*     */       else
/*     */       {
/* 534 */         IoBuffer buf = (IoBuffer)writeRequest.getMessage();
/*     */ 
/* 536 */         if (handler.isWritingEncryptedData())
/*     */         {
/* 538 */           handler.scheduleFilterWrite(nextFilter, writeRequest);
/* 539 */         } else if (handler.isHandshakeComplete())
/*     */         {
/* 541 */           int pos = buf.position();
/* 542 */           handler.encrypt(buf.buf());
/* 543 */           buf.position(pos);
/* 544 */           IoBuffer encryptedBuffer = handler.fetchOutNetBuffer();
/* 545 */           handler.scheduleFilterWrite(nextFilter, new EncryptedWriteRequest(writeRequest, encryptedBuffer, null));
/*     */         }
/*     */         else
/*     */         {
/* 550 */           if (session.isConnected())
/*     */           {
/* 552 */             handler.schedulePreHandshakeWriteRequest(nextFilter, writeRequest);
/*     */           }
/*     */ 
/* 555 */           needsFlush = false;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 560 */     if (needsFlush)
/* 561 */       handler.flushScheduledEvents();
/*     */   }
/*     */ 
/*     */   public void filterClose(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws SSLException
/*     */   {
/* 568 */     SslHandler handler = (SslHandler)session.getAttribute(SSL_HANDLER);
/* 569 */     if (handler == null)
/*     */     {
/* 572 */       nextFilter.filterClose(session);
/* 573 */       return;
/*     */     }
/*     */ 
/* 576 */     WriteFuture future = null;
/*     */     try {
/* 578 */       synchronized (handler) {
/* 579 */         if (isSslStarted(session)) {
/* 580 */           future = initiateClosure(nextFilter, session);
/* 581 */           future.addListener(new IoFutureListener(nextFilter, session) {
/*     */             public void operationComplete(IoFuture future) {
/* 583 */               this.val$nextFilter.filterClose(this.val$session);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/* 589 */       handler.flushScheduledEvents();
/*     */     } finally {
/* 591 */       if (future == null)
/* 592 */         nextFilter.filterClose(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initiateHandshake(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws SSLException
/*     */   {
/* 599 */     SslHandler handler = getSslSessionHandler(session);
/*     */ 
/* 601 */     synchronized (handler) {
/* 602 */       handler.handshake(nextFilter);
/*     */     }
/*     */ 
/* 605 */     handler.flushScheduledEvents();
/*     */   }
/*     */ 
/*     */   private WriteFuture initiateClosure(IoFilter.NextFilter nextFilter, IoSession session) throws SSLException
/*     */   {
/* 610 */     SslHandler handler = getSslSessionHandler(session);
/*     */ 
/* 612 */     if (!handler.closeOutbound()) {
/* 613 */       return DefaultWriteFuture.newNotWrittenFuture(session, new IllegalStateException("SSL session is shut down already."));
/*     */     }
/*     */ 
/* 618 */     WriteFuture future = handler.writeNetBuffer(nextFilter);
/* 619 */     if (future == null) {
/* 620 */       future = DefaultWriteFuture.newWrittenFuture(session);
/*     */     }
/*     */ 
/* 623 */     if (handler.isInboundDone()) {
/* 624 */       handler.destroy();
/*     */     }
/*     */ 
/* 627 */     if (session.containsAttribute(USE_NOTIFICATION)) {
/* 628 */       handler.scheduleMessageReceived(nextFilter, SESSION_UNSECURED);
/*     */     }
/*     */ 
/* 631 */     return future;
/*     */   }
/*     */ 
/*     */   private void handleSslData(IoFilter.NextFilter nextFilter, SslHandler handler)
/*     */     throws SSLException
/*     */   {
/* 639 */     if (handler.isHandshakeComplete()) {
/* 640 */       handler.flushPreHandshakeEvents();
/*     */     }
/*     */ 
/* 644 */     handler.writeNetBuffer(nextFilter);
/*     */ 
/* 647 */     handleAppDataRead(nextFilter, handler);
/*     */   }
/*     */ 
/*     */   private void handleAppDataRead(IoFilter.NextFilter nextFilter, SslHandler handler)
/*     */   {
/* 652 */     IoBuffer readBuffer = handler.fetchAppBuffer();
/* 653 */     if (readBuffer.hasRemaining())
/* 654 */       handler.scheduleMessageReceived(nextFilter, readBuffer);
/*     */   }
/*     */ 
/*     */   private SslHandler getSslSessionHandler(IoSession session)
/*     */   {
/* 659 */     SslHandler handler = (SslHandler)session.getAttribute(SSL_HANDLER);
/*     */ 
/* 661 */     if (handler == null) {
/* 662 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 665 */     if (handler.getParent() != this) {
/* 666 */       throw new IllegalArgumentException("Not managed by this filter.");
/*     */     }
/*     */ 
/* 669 */     return handler;
/*     */   }
/*     */ 
/*     */   private static class EncryptedWriteRequest extends WriteRequestWrapper
/*     */   {
/*     */     private final IoBuffer encryptedMessage;
/*     */ 
/*     */     private EncryptedWriteRequest(WriteRequest writeRequest, IoBuffer encryptedMessage)
/*     */     {
/* 697 */       super();
/* 698 */       this.encryptedMessage = encryptedMessage;
/*     */     }
/*     */ 
/*     */     public Object getMessage()
/*     */     {
/* 703 */       return this.encryptedMessage;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class SslFilterMessage
/*     */   {
/*     */     private final String name;
/*     */ 
/*     */     private SslFilterMessage(String name)
/*     */     {
/* 683 */       this.name = name;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 688 */       return this.name;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.ssl.SslFilter
 * JD-Core Version:    0.6.0
 */
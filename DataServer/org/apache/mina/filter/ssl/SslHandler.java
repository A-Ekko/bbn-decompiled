/*     */ package org.apache.mina.filter.ssl;
/*     */ 
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLEngine;
/*     */ import javax.net.ssl.SSLEngineResult;
/*     */ import javax.net.ssl.SSLEngineResult.HandshakeStatus;
/*     */ import javax.net.ssl.SSLEngineResult.Status;
/*     */ import javax.net.ssl.SSLException;
/*     */ import javax.net.ssl.SSLHandshakeException;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterEvent;
/*     */ import org.apache.mina.core.future.DefaultWriteFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.IoEventType;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.DefaultWriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.util.CircularQueue;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class SslHandler
/*     */ {
/*  59 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */   private final SslFilter parent;
/*     */   private final SSLContext sslContext;
/*     */   private final IoSession session;
/*  63 */   private final Queue<IoFilterEvent> preHandshakeEventQueue = new CircularQueue();
/*  64 */   private final Queue<IoFilterEvent> filterWriteEventQueue = new ConcurrentLinkedQueue();
/*  65 */   private final Queue<IoFilterEvent> messageReceivedEventQueue = new ConcurrentLinkedQueue();
/*     */   private SSLEngine sslEngine;
/*     */   private IoBuffer inNetBuffer;
/*     */   private IoBuffer outNetBuffer;
/*     */   private IoBuffer appBuffer;
/*  86 */   private final IoBuffer emptyBuffer = IoBuffer.allocate(0);
/*     */   private SSLEngineResult.HandshakeStatus handshakeStatus;
/*     */   private boolean initialHandshakeComplete;
/*     */   private boolean handshakeComplete;
/*     */   private boolean writingEncryptedData;
/*     */ 
/*     */   public SslHandler(SslFilter parent, SSLContext sslContext, IoSession session)
/*     */     throws SSLException
/*     */   {
/* 101 */     this.parent = parent;
/* 102 */     this.session = session;
/* 103 */     this.sslContext = sslContext;
/* 104 */     init();
/*     */   }
/*     */ 
/*     */   public void init()
/*     */     throws SSLException
/*     */   {
/* 113 */     if (this.sslEngine != null)
/*     */     {
/* 115 */       return;
/*     */     }
/*     */ 
/* 118 */     InetSocketAddress peer = (InetSocketAddress)this.session.getAttribute(SslFilter.PEER_ADDRESS);
/*     */ 
/* 122 */     if (peer == null)
/* 123 */       this.sslEngine = this.sslContext.createSSLEngine();
/*     */     else {
/* 125 */       this.sslEngine = this.sslContext.createSSLEngine(peer.getHostName(), peer.getPort());
/*     */     }
/*     */ 
/* 129 */     this.sslEngine.setUseClientMode(this.parent.isUseClientMode());
/*     */ 
/* 132 */     if (this.parent.isWantClientAuth()) {
/* 133 */       this.sslEngine.setWantClientAuth(true);
/*     */     }
/*     */ 
/* 136 */     if (this.parent.isNeedClientAuth()) {
/* 137 */       this.sslEngine.setNeedClientAuth(true);
/*     */     }
/*     */ 
/* 140 */     if (this.parent.getEnabledCipherSuites() != null) {
/* 141 */       this.sslEngine.setEnabledCipherSuites(this.parent.getEnabledCipherSuites());
/*     */     }
/*     */ 
/* 144 */     if (this.parent.getEnabledProtocols() != null) {
/* 145 */       this.sslEngine.setEnabledProtocols(this.parent.getEnabledProtocols());
/*     */     }
/*     */ 
/* 149 */     this.sslEngine.beginHandshake();
/*     */ 
/* 152 */     this.handshakeStatus = this.sslEngine.getHandshakeStatus();
/*     */ 
/* 154 */     this.handshakeComplete = false;
/* 155 */     this.initialHandshakeComplete = false;
/* 156 */     this.writingEncryptedData = false;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 163 */     if (this.sslEngine == null) {
/* 164 */       return;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 169 */       this.sslEngine.closeInbound();
/*     */     } catch (SSLException e) {
/* 171 */       this.logger.debug("Unexpected exception from SSLEngine.closeInbound().", e);
/*     */     }
/*     */ 
/* 176 */     if (this.outNetBuffer != null)
/* 177 */       this.outNetBuffer.capacity(this.sslEngine.getSession().getPacketBufferSize());
/*     */     else
/* 179 */       createOutNetBuffer(0);
/*     */     try
/*     */     {
/*     */       do
/* 183 */         this.outNetBuffer.clear();
/* 184 */       while (this.sslEngine.wrap(this.emptyBuffer.buf(), this.outNetBuffer.buf()).bytesProduced() > 0);
/*     */     } catch (SSLException e) {
/*     */     }
/*     */     finally {
/* 188 */       destroyOutNetBuffer();
/*     */     }
/*     */ 
/* 191 */     this.sslEngine.closeOutbound();
/* 192 */     this.sslEngine = null;
/*     */ 
/* 194 */     this.preHandshakeEventQueue.clear();
/*     */   }
/*     */ 
/*     */   private void destroyOutNetBuffer() {
/* 198 */     this.outNetBuffer.free();
/* 199 */     this.outNetBuffer = null;
/*     */   }
/*     */ 
/*     */   public SslFilter getParent() {
/* 203 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public IoSession getSession() {
/* 207 */     return this.session;
/*     */   }
/*     */ 
/*     */   public boolean isWritingEncryptedData()
/*     */   {
/* 214 */     return this.writingEncryptedData;
/*     */   }
/*     */ 
/*     */   public boolean isHandshakeComplete()
/*     */   {
/* 221 */     return this.handshakeComplete;
/*     */   }
/*     */ 
/*     */   public boolean isInboundDone() {
/* 225 */     return (this.sslEngine == null) || (this.sslEngine.isInboundDone());
/*     */   }
/*     */ 
/*     */   public boolean isOutboundDone() {
/* 229 */     return (this.sslEngine == null) || (this.sslEngine.isOutboundDone());
/*     */   }
/*     */ 
/*     */   public boolean needToCompleteHandshake()
/*     */   {
/* 236 */     return (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) && (!isInboundDone());
/*     */   }
/*     */ 
/*     */   public void schedulePreHandshakeWriteRequest(IoFilter.NextFilter nextFilter, WriteRequest writeRequest)
/*     */   {
/* 241 */     this.preHandshakeEventQueue.add(new IoFilterEvent(nextFilter, IoEventType.WRITE, this.session, writeRequest));
/*     */   }
/*     */ 
/*     */   public void flushPreHandshakeEvents()
/*     */     throws SSLException
/*     */   {
/*     */     IoFilterEvent scheduledWrite;
/* 248 */     while ((scheduledWrite = (IoFilterEvent)this.preHandshakeEventQueue.poll()) != null)
/* 249 */       this.parent.filterWrite(scheduledWrite.getNextFilter(), this.session, (WriteRequest)scheduledWrite.getParameter());
/*     */   }
/*     */ 
/*     */   public void scheduleFilterWrite(IoFilter.NextFilter nextFilter, WriteRequest writeRequest)
/*     */   {
/* 255 */     this.filterWriteEventQueue.add(new IoFilterEvent(nextFilter, IoEventType.WRITE, this.session, writeRequest));
/*     */   }
/*     */ 
/*     */   public void scheduleMessageReceived(IoFilter.NextFilter nextFilter, Object message) {
/* 259 */     this.messageReceivedEventQueue.add(new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, this.session, message));
/*     */   }
/*     */ 
/*     */   public void flushScheduledEvents()
/*     */   {
/* 264 */     if (Thread.holdsLock(this))
/* 265 */       return;
/*     */     IoFilterEvent e;
/* 272 */     synchronized (this) {
/* 273 */       while ((e = (IoFilterEvent)this.filterWriteEventQueue.poll()) != null) {
/* 274 */         e.getNextFilter().filterWrite(this.session, (WriteRequest)e.getParameter());
/*     */       }
/*     */     }
/*     */ 
/* 278 */     while ((e = (IoFilterEvent)this.messageReceivedEventQueue.poll()) != null)
/* 279 */       e.getNextFilter().messageReceived(this.session, e.getParameter());
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, ByteBuffer buf)
/*     */     throws SSLException
/*     */   {
/* 294 */     if (this.inNetBuffer == null) {
/* 295 */       this.inNetBuffer = IoBuffer.allocate(buf.remaining()).setAutoExpand(true);
/*     */     }
/*     */ 
/* 298 */     this.inNetBuffer.put(buf);
/* 299 */     if (!this.handshakeComplete)
/* 300 */       handshake(nextFilter);
/*     */     else {
/* 302 */       decrypt(nextFilter);
/*     */     }
/*     */ 
/* 305 */     if (isInboundDone())
/*     */     {
/* 307 */       int inNetBufferPosition = this.inNetBuffer == null ? 0 : this.inNetBuffer.position();
/* 308 */       buf.position(buf.position() - inNetBufferPosition);
/* 309 */       this.inNetBuffer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public IoBuffer fetchAppBuffer()
/*     */   {
/* 319 */     IoBuffer appBuffer = this.appBuffer.flip();
/* 320 */     this.appBuffer = null;
/* 321 */     return appBuffer;
/*     */   }
/*     */ 
/*     */   public IoBuffer fetchOutNetBuffer()
/*     */   {
/* 330 */     IoBuffer answer = this.outNetBuffer;
/* 331 */     if (answer == null) {
/* 332 */       return this.emptyBuffer;
/*     */     }
/*     */ 
/* 335 */     this.outNetBuffer = null;
/* 336 */     return answer.shrink();
/*     */   }
/*     */ 
/*     */   public void encrypt(ByteBuffer src)
/*     */     throws SSLException
/*     */   {
/* 346 */     if (!this.handshakeComplete) {
/* 347 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 350 */     if (!src.hasRemaining()) {
/* 351 */       if (this.outNetBuffer == null) {
/* 352 */         this.outNetBuffer = this.emptyBuffer;
/*     */       }
/* 354 */       return;
/*     */     }
/*     */ 
/* 357 */     createOutNetBuffer(src.remaining());
/*     */ 
/* 360 */     while (src.hasRemaining())
/*     */     {
/* 362 */       SSLEngineResult result = this.sslEngine.wrap(src, this.outNetBuffer.buf());
/* 363 */       if (result.getStatus() == SSLEngineResult.Status.OK) {
/* 364 */         if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK)
/* 365 */           doTasks();
/*     */       }
/* 367 */       else if (result.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/* 368 */         this.outNetBuffer.capacity(this.outNetBuffer.capacity() << 1);
/* 369 */         this.outNetBuffer.limit(this.outNetBuffer.capacity());
/*     */       } else {
/* 371 */         throw new SSLException("SSLEngine error during encrypt: " + result.getStatus() + " src: " + src + "outNetBuffer: " + this.outNetBuffer);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 377 */     this.outNetBuffer.flip();
/*     */   }
/*     */ 
/*     */   public boolean closeOutbound()
/*     */     throws SSLException
/*     */   {
/* 388 */     if ((this.sslEngine == null) || (this.sslEngine.isOutboundDone())) {
/* 389 */       return false; } 
/*     */ this.sslEngine.closeOutbound();
/*     */ 
/* 394 */     createOutNetBuffer(0);
/*     */     SSLEngineResult result;
/*     */     while (true) { result = this.sslEngine.wrap(this.emptyBuffer.buf(), this.outNetBuffer.buf());
/* 398 */       if (result.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW) break;
/* 399 */       this.outNetBuffer.capacity(this.outNetBuffer.capacity() << 1);
/* 400 */       this.outNetBuffer.limit(this.outNetBuffer.capacity());
/*     */     }
/*     */ 
/* 406 */     if (result.getStatus() != SSLEngineResult.Status.CLOSED) {
/* 407 */       throw new SSLException("Improper close state: " + result);
/*     */     }
/* 409 */     this.outNetBuffer.flip();
/* 410 */     return true;
/*     */   }
/*     */ 
/*     */   private void decrypt(IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/* 420 */     if (!this.handshakeComplete) {
/* 421 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 424 */     unwrap(nextFilter);
/*     */   }
/*     */ 
/*     */   private void checkStatus(SSLEngineResult res)
/*     */     throws SSLException
/*     */   {
/* 434 */     SSLEngineResult.Status status = res.getStatus();
/*     */ 
/* 444 */     if ((status != SSLEngineResult.Status.OK) && (status != SSLEngineResult.Status.CLOSED) && (status != SSLEngineResult.Status.BUFFER_UNDERFLOW))
/*     */     {
/* 447 */       throw new SSLException("SSLEngine error during decrypt: " + status + " inNetBuffer: " + this.inNetBuffer + "appBuffer: " + this.appBuffer);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handshake(IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/*     */     while (true)
/* 458 */       switch (1.$SwitchMap$javax$net$ssl$SSLEngineResult$HandshakeStatus[this.handshakeStatus.ordinal()]) {
/*     */       case 1:
/* 460 */         this.session.setAttribute(SslFilter.SSL_SESSION, this.sslEngine.getSession());
/*     */ 
/* 462 */         this.handshakeComplete = true;
/*     */ 
/* 464 */         if ((!this.initialHandshakeComplete) && (this.session.containsAttribute(SslFilter.USE_NOTIFICATION)))
/*     */         {
/* 468 */           this.initialHandshakeComplete = true;
/* 469 */           scheduleMessageReceived(nextFilter, SslFilter.SESSION_SECURED);
/*     */         }
/*     */ 
/* 473 */         return;
/*     */       case 2:
/* 476 */         this.handshakeStatus = doTasks();
/* 477 */         break;
/*     */       case 3:
/* 481 */         SSLEngineResult.Status status = unwrapHandshake(nextFilter);
/*     */ 
/* 483 */         if (((status != SSLEngineResult.Status.BUFFER_UNDERFLOW) || (this.handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED)) && (!isInboundDone()))
/*     */         {
/*     */           break;
/*     */         }
/* 487 */         return;
/*     */       case 4:
/* 495 */         if ((this.outNetBuffer != null) && (this.outNetBuffer.hasRemaining())) {
/* 496 */           return;
/* 500 */         }
/*     */ createOutNetBuffer(0);
/*     */         SSLEngineResult result;
/*     */         while (true) {
/* 503 */           result = this.sslEngine.wrap(this.emptyBuffer.buf(), this.outNetBuffer.buf());
/* 504 */           if (result.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW) break;
/* 505 */           this.outNetBuffer.capacity(this.outNetBuffer.capacity() << 1);
/* 506 */           this.outNetBuffer.limit(this.outNetBuffer.capacity());
/*     */         }
/*     */ 
/* 512 */         this.outNetBuffer.flip();
/* 513 */         this.handshakeStatus = result.getHandshakeStatus();
/* 514 */         writeNetBuffer(nextFilter);
/* 515 */         break;
/*     */       default:
/* 518 */         throw new IllegalStateException("Invalid Handshaking State" + this.handshakeStatus);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void createOutNetBuffer(int expectedRemaining)
/*     */   {
/* 527 */     int capacity = Math.max(expectedRemaining, this.sslEngine.getSession().getPacketBufferSize());
/*     */ 
/* 531 */     if (this.outNetBuffer != null)
/* 532 */       this.outNetBuffer.capacity(capacity);
/*     */     else
/* 534 */       this.outNetBuffer = IoBuffer.allocate(capacity).minimumCapacity(0);
/*     */   }
/*     */ 
/*     */   public WriteFuture writeNetBuffer(IoFilter.NextFilter nextFilter)
/*     */     throws SSLException
/*     */   {
/* 541 */     if ((this.outNetBuffer == null) || (!this.outNetBuffer.hasRemaining()))
/*     */     {
/* 543 */       return null;
/*     */     }
/*     */ 
/* 548 */     this.writingEncryptedData = true;
/*     */ 
/* 551 */     WriteFuture writeFuture = null;
/*     */     try
/*     */     {
/* 554 */       IoBuffer writeBuffer = fetchOutNetBuffer();
/* 555 */       writeFuture = new DefaultWriteFuture(this.session);
/* 556 */       this.parent.filterWrite(nextFilter, this.session, new DefaultWriteRequest(writeBuffer, writeFuture));
/*     */ 
/* 560 */       while (needToCompleteHandshake()) {
/*     */         try {
/* 562 */           handshake(nextFilter);
/*     */         } catch (SSLException ssle) {
/* 564 */           SSLException newSsle = new SSLHandshakeException("SSL handshake failed.");
/*     */ 
/* 566 */           newSsle.initCause(ssle);
/* 567 */           throw newSsle;
/*     */         }
/*     */ 
/* 570 */         IoBuffer outNetBuffer = fetchOutNetBuffer();
/* 571 */         if ((outNetBuffer != null) && (outNetBuffer.hasRemaining())) {
/* 572 */           writeFuture = new DefaultWriteFuture(this.session);
/* 573 */           this.parent.filterWrite(nextFilter, this.session, new DefaultWriteRequest(outNetBuffer, writeFuture));
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 578 */       this.writingEncryptedData = false;
/*     */     }
/*     */ 
/* 581 */     return writeFuture;
/*     */   }
/*     */ 
/*     */   private void unwrap(IoFilter.NextFilter nextFilter) throws SSLException
/*     */   {
/* 586 */     if (this.inNetBuffer != null) {
/* 587 */       this.inNetBuffer.flip();
/*     */     }
/*     */ 
/* 590 */     if ((this.inNetBuffer == null) || (!this.inNetBuffer.hasRemaining())) {
/* 591 */       return;
/*     */     }
/*     */ 
/* 594 */     SSLEngineResult res = unwrap0();
/*     */ 
/* 597 */     if (this.inNetBuffer.hasRemaining())
/* 598 */       this.inNetBuffer.compact();
/*     */     else {
/* 600 */       this.inNetBuffer = null;
/*     */     }
/*     */ 
/* 603 */     checkStatus(res);
/*     */ 
/* 605 */     renegotiateIfNeeded(nextFilter, res);
/*     */   }
/*     */ 
/*     */   private SSLEngineResult.Status unwrapHandshake(IoFilter.NextFilter nextFilter) throws SSLException
/*     */   {
/* 610 */     if (this.inNetBuffer != null) {
/* 611 */       this.inNetBuffer.flip();
/*     */     }
/*     */ 
/* 614 */     if ((this.inNetBuffer == null) || (!this.inNetBuffer.hasRemaining()))
/*     */     {
/* 616 */       return SSLEngineResult.Status.BUFFER_UNDERFLOW;
/*     */     }
/*     */ 
/* 619 */     SSLEngineResult res = unwrap0();
/* 620 */     this.handshakeStatus = res.getHandshakeStatus();
/*     */ 
/* 622 */     checkStatus(res);
/*     */ 
/* 626 */     if ((this.handshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) && (res.getStatus() == SSLEngineResult.Status.OK) && (this.inNetBuffer.hasRemaining()))
/*     */     {
/* 629 */       res = unwrap0();
/*     */ 
/* 632 */       if (this.inNetBuffer.hasRemaining())
/* 633 */         this.inNetBuffer.compact();
/*     */       else {
/* 635 */         this.inNetBuffer = null;
/*     */       }
/*     */ 
/* 638 */       renegotiateIfNeeded(nextFilter, res);
/*     */     }
/* 641 */     else if (this.inNetBuffer.hasRemaining()) {
/* 642 */       this.inNetBuffer.compact();
/*     */     } else {
/* 644 */       this.inNetBuffer = null;
/*     */     }
/*     */ 
/* 648 */     return res.getStatus();
/*     */   }
/*     */ 
/*     */   private void renegotiateIfNeeded(IoFilter.NextFilter nextFilter, SSLEngineResult res) throws SSLException
/*     */   {
/* 653 */     if ((res.getStatus() != SSLEngineResult.Status.CLOSED) && (res.getStatus() != SSLEngineResult.Status.BUFFER_UNDERFLOW) && (res.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING))
/*     */     {
/* 657 */       this.handshakeComplete = false;
/* 658 */       this.handshakeStatus = res.getHandshakeStatus();
/* 659 */       handshake(nextFilter);
/*     */     }
/*     */   }
/*     */ 
/*     */   private SSLEngineResult unwrap0() throws SSLException {
/* 664 */     if (this.appBuffer == null)
/* 665 */       this.appBuffer = IoBuffer.allocate(this.inNetBuffer.remaining());
/*     */     else
/* 667 */       this.appBuffer.expand(this.inNetBuffer.remaining());
/*     */     SSLEngineResult res;
/*     */     do
/*     */     {
/* 672 */       res = this.sslEngine.unwrap(this.inNetBuffer.buf(), this.appBuffer.buf());
/* 673 */       if (res.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
/* 674 */         this.appBuffer.capacity(this.appBuffer.capacity() << 1);
/* 675 */         this.appBuffer.limit(this.appBuffer.capacity());
/*     */       }
/*     */     }
/* 678 */     while (((res.getStatus() == SSLEngineResult.Status.OK) || (res.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW)) && (((this.handshakeComplete) && (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING)) || (res.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP)));
/*     */ 
/* 682 */     return res;
/*     */   }
/*     */ 
/*     */   private SSLEngineResult.HandshakeStatus doTasks()
/*     */   {
/*     */     Runnable runnable;
/* 694 */     while ((runnable = this.sslEngine.getDelegatedTask()) != null)
/*     */     {
/* 696 */       runnable.run();
/*     */     }
/* 698 */     return this.sslEngine.getHandshakeStatus();
/*     */   }
/*     */ 
/*     */   public static IoBuffer copy(ByteBuffer src)
/*     */   {
/* 709 */     IoBuffer copy = IoBuffer.allocate(src.remaining());
/* 710 */     copy.put(src);
/* 711 */     copy.flip();
/* 712 */     return copy;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.ssl.SslHandler
 * JD-Core Version:    0.6.0
 */
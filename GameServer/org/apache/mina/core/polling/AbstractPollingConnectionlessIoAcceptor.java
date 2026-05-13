/*     */ package org.apache.mina.core.polling;
/*     */ 
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChainBuilder;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor.AcceptorOperationFuture;
/*     */ import org.apache.mina.core.service.AbstractIoService.ServiceOperationFuture;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.ExpiringSessionRecycler;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionRecycler;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public abstract class AbstractPollingConnectionlessIoAcceptor<T extends AbstractIoSession, H> extends AbstractIoAcceptor
/*     */ {
/*  60 */   private static final IoSessionRecycler DEFAULT_RECYCLER = new ExpiringSessionRecycler();
/*     */ 
/*  62 */   private final Object lock = new Object();
/*  63 */   private final IoProcessor<T> processor = new ConnectionlessAcceptorProcessor(null);
/*  64 */   private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue = new ConcurrentLinkedQueue();
/*     */ 
/*  66 */   private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue = new ConcurrentLinkedQueue();
/*     */ 
/*  68 */   private final Queue<T> flushingSessions = new ConcurrentLinkedQueue();
/*  69 */   private final Map<SocketAddress, H> boundHandles = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  72 */   private IoSessionRecycler sessionRecycler = DEFAULT_RECYCLER;
/*     */ 
/*  74 */   private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
/*     */   private volatile boolean selectable;
/*     */   private AbstractPollingConnectionlessIoAcceptor<T, H>.Acceptor acceptor;
/*     */   private long lastIdleCheckTime;
/*     */ 
/*     */   protected AbstractPollingConnectionlessIoAcceptor(IoSessionConfig sessionConfig)
/*     */   {
/*  87 */     this(sessionConfig, null);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingConnectionlessIoAcceptor(IoSessionConfig sessionConfig, Executor executor)
/*     */   {
/*  94 */     super(sessionConfig, executor);
/*     */     try
/*     */     {
/*  97 */       init();
/*  98 */       this.selectable = true;
/*     */     } catch (RuntimeException e) {
/* 100 */       throw e;
/*     */     } catch (Exception e) {
/* 102 */       throw new RuntimeIoException("Failed to initialize.", e);
/*     */     } finally {
/* 104 */       if (!this.selectable)
/*     */         try {
/* 106 */           destroy();
/*     */         } catch (Exception e) {
/* 108 */           ExceptionMonitor.getInstance().exceptionCaught(e); }   }  } 
/*     */   protected abstract void init() throws Exception;
/*     */ 
/*     */   protected abstract void destroy() throws Exception;
/*     */ 
/*     */   protected abstract int select() throws Exception;
/*     */ 
/*     */   protected abstract int select(int paramInt) throws Exception;
/*     */ 
/*     */   protected abstract void wakeup();
/*     */ 
/*     */   protected abstract Iterator<H> selectedHandles();
/*     */ 
/*     */   protected abstract H open(SocketAddress paramSocketAddress) throws Exception;
/*     */ 
/*     */   protected abstract void close(H paramH) throws Exception;
/*     */ 
/*     */   protected abstract SocketAddress localAddress(H paramH) throws Exception;
/*     */ 
/*     */   protected abstract boolean isReadable(H paramH);
/*     */ 
/*     */   protected abstract boolean isWritable(H paramH);
/*     */ 
/*     */   protected abstract SocketAddress receive(H paramH, IoBuffer paramIoBuffer) throws Exception;
/*     */ 
/*     */   protected abstract int send(T paramT, IoBuffer paramIoBuffer, SocketAddress paramSocketAddress) throws Exception;
/*     */ 
/*     */   protected abstract T newSession(IoProcessor<T> paramIoProcessor, H paramH, SocketAddress paramSocketAddress) throws Exception;
/*     */ 
/*     */   protected abstract void setInterestedInWrite(T paramT, boolean paramBoolean) throws Exception;
/*     */ 
/* 135 */   protected IoFuture dispose0() throws Exception { unbind();
/* 136 */     if (!this.disposalFuture.isDone()) {
/* 137 */       startupAcceptor();
/* 138 */       wakeup();
/*     */     }
/* 140 */     return this.disposalFuture;
/*     */   }
/*     */ 
/*     */   protected final Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses)
/*     */     throws Exception
/*     */   {
/* 151 */     AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
/*     */ 
/* 155 */     this.registerQueue.add(request);
/*     */ 
/* 159 */     startupAcceptor();
/*     */ 
/* 164 */     wakeup();
/*     */ 
/* 167 */     request.awaitUninterruptibly();
/*     */ 
/* 169 */     if (request.getException() != null) {
/* 170 */       throw request.getException();
/*     */     }
/*     */ 
/* 176 */     Set newLocalAddresses = new HashSet();
/*     */ 
/* 178 */     for (Iterator i$ = this.boundHandles.values().iterator(); i$.hasNext(); ) { Object handle = i$.next();
/* 179 */       newLocalAddresses.add(localAddress(handle));
/*     */     }
/*     */ 
/* 182 */     return newLocalAddresses;
/*     */   }
/*     */ 
/*     */   protected final void unbind0(List<? extends SocketAddress> localAddresses)
/*     */     throws Exception
/*     */   {
/* 191 */     AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
/*     */ 
/* 193 */     this.cancelQueue.add(request);
/* 194 */     startupAcceptor();
/* 195 */     wakeup();
/*     */ 
/* 197 */     request.awaitUninterruptibly();
/*     */ 
/* 199 */     if (request.getException() != null)
/* 200 */       throw request.getException();
/*     */   }
/*     */ 
/*     */   public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress)
/*     */   {
/* 208 */     if (isDisposing()) {
/* 209 */       throw new IllegalStateException("Already disposed.");
/*     */     }
/*     */ 
/* 212 */     if (remoteAddress == null) {
/* 213 */       throw new NullPointerException("remoteAddress");
/*     */     }
/*     */ 
/* 216 */     synchronized (this.bindLock) {
/* 217 */       if (!isActive()) {
/* 218 */         throw new IllegalStateException("Can't create a session from a unbound service.");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 223 */         return newSessionWithoutLock(remoteAddress, localAddress);
/*     */       } catch (RuntimeException e) {
/* 225 */         throw e;
/*     */       } catch (Error e) {
/* 227 */         throw e;
/*     */       } catch (Exception e) {
/* 229 */         throw new RuntimeIoException("Failed to create a session.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private IoSession newSessionWithoutLock(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception
/*     */   {
/* 236 */     Object handle = this.boundHandles.get(localAddress);
/* 237 */     if (handle == null) {
/* 238 */       throw new IllegalArgumentException("Unknown local address: " + localAddress);
/*     */     }
/*     */ 
/* 242 */     IoSessionRecycler sessionRecycler = getSessionRecycler();
/*     */     IoSession session;
/* 243 */     synchronized (sessionRecycler) {
/* 244 */       session = sessionRecycler.recycle(localAddress, remoteAddress);
/* 245 */       if (session != null) {
/* 246 */         return session;
/*     */       }
/*     */ 
/* 250 */       AbstractIoSession newSession = newSession(this.processor, handle, remoteAddress);
/* 251 */       getSessionRecycler().put(newSession);
/* 252 */       session = newSession;
/*     */     }
/*     */ 
/* 255 */     initSession(session, null, null);
/*     */     try
/*     */     {
/* 258 */       getFilterChainBuilder().buildFilterChain(session.getFilterChain());
/* 259 */       getListeners().fireSessionCreated(session);
/*     */     } catch (Throwable t) {
/* 261 */       ExceptionMonitor.getInstance().exceptionCaught(t);
/*     */     }
/*     */ 
/* 264 */     return session;
/*     */   }
/*     */ 
/*     */   public final IoSessionRecycler getSessionRecycler() {
/* 268 */     return this.sessionRecycler;
/*     */   }
/*     */ 
/*     */   public final void setSessionRecycler(IoSessionRecycler sessionRecycler) {
/* 272 */     synchronized (this.bindLock) {
/* 273 */       if (isActive()) {
/* 274 */         throw new IllegalStateException("sessionRecycler can't be set while the acceptor is bound.");
/*     */       }
/*     */ 
/* 278 */       if (sessionRecycler == null) {
/* 279 */         sessionRecycler = DEFAULT_RECYCLER;
/*     */       }
/* 281 */       this.sessionRecycler = sessionRecycler;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startupAcceptor()
/*     */   {
/* 321 */     if (!this.selectable) {
/* 322 */       this.registerQueue.clear();
/* 323 */       this.cancelQueue.clear();
/* 324 */       this.flushingSessions.clear();
/*     */     }
/*     */ 
/* 327 */     synchronized (this.lock) {
/* 328 */       if (this.acceptor == null) {
/* 329 */         this.acceptor = new Acceptor(null);
/* 330 */         executeWorker(this.acceptor);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean scheduleFlush(T session) {
/* 336 */     if (session.setScheduledForFlush(true)) {
/* 337 */       this.flushingSessions.add(session);
/* 338 */       return true;
/*     */     }
/* 340 */     return false;
/*     */   }
/*     */ 
/*     */   private void processReadySessions(Iterator<H> handles)
/*     */   {
/* 403 */     while (handles.hasNext()) {
/* 404 */       Object h = handles.next();
/* 405 */       handles.remove();
/*     */       try {
/* 407 */         if (isReadable(h)) {
/* 408 */           readHandle(h);
/*     */         }
/*     */ 
/* 411 */         if (isWritable(h))
/* 412 */           for (IoSession session : getManagedSessions().values())
/* 413 */             scheduleFlush((AbstractIoSession)session);
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 417 */         ExceptionMonitor.getInstance().exceptionCaught(t);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readHandle(H handle) throws Exception {
/* 423 */     IoBuffer readBuf = IoBuffer.allocate(getSessionConfig().getReadBufferSize());
/*     */ 
/* 426 */     SocketAddress remoteAddress = receive(handle, readBuf);
/* 427 */     if (remoteAddress != null) {
/* 428 */       IoSession session = newSessionWithoutLock(remoteAddress, localAddress(handle));
/*     */ 
/* 431 */       readBuf.flip();
/*     */ 
/* 433 */       IoBuffer newBuf = IoBuffer.allocate(readBuf.limit());
/* 434 */       newBuf.put(readBuf);
/* 435 */       newBuf.flip();
/*     */ 
/* 437 */       session.getFilterChain().fireMessageReceived(newBuf);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flushSessions(long currentTime) {
/*     */     while (true) {
/* 443 */       AbstractIoSession session = (AbstractIoSession)this.flushingSessions.poll();
/* 444 */       if (session == null)
/*     */       {
/*     */         break;
/*     */       }
/* 448 */       session.setScheduledForFlush(false);
/*     */       try
/*     */       {
/* 451 */         boolean flushedAll = flush(session, currentTime);
/* 452 */         if ((flushedAll) && (!session.getWriteRequestQueue().isEmpty(session)) && (!session.isScheduledForFlush()))
/*     */         {
/* 454 */           scheduleFlush(session);
/*     */         }
/*     */       } catch (Exception e) {
/* 457 */         session.getFilterChain().fireExceptionCaught(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean flush(T session, long currentTime) throws Exception
/*     */   {
/* 464 */     setInterestedInWrite(session, false);
/*     */ 
/* 466 */     WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
/* 467 */     int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
/*     */ 
/* 471 */     int writtenBytes = 0;
/*     */     try {
/*     */       while (true) {
/* 474 */         WriteRequest req = session.getCurrentWriteRequest();
/* 475 */         if (req == null) {
/* 476 */           req = writeRequestQueue.poll(session);
/* 477 */           if (req == null) {
/*     */             break;
/*     */           }
/* 480 */           session.setCurrentWriteRequest(req);
/*     */         }
/*     */ 
/* 483 */         IoBuffer buf = (IoBuffer)req.getMessage();
/* 484 */         if (buf.remaining() == 0)
/*     */         {
/* 486 */           session.setCurrentWriteRequest(null);
/* 487 */           buf.reset();
/* 488 */           session.getFilterChain().fireMessageSent(req);
/* 489 */           continue;
/*     */         }
/*     */ 
/* 492 */         SocketAddress destination = req.getDestination();
/* 493 */         if (destination == null) {
/* 494 */           destination = session.getRemoteAddress();
/*     */         }
/*     */ 
/* 497 */         int localWrittenBytes = send(session, buf, destination);
/* 498 */         if ((localWrittenBytes == 0) || (writtenBytes >= maxWrittenBytes))
/*     */         {
/* 500 */           setInterestedInWrite(session, true);
/* 501 */           int i = 0;
/*     */           return i;
/*     */         }
/* 503 */         setInterestedInWrite(session, false);
/*     */ 
/* 506 */         session.setCurrentWriteRequest(null);
/* 507 */         writtenBytes += localWrittenBytes;
/* 508 */         buf.reset();
/* 509 */         session.getFilterChain().fireMessageSent(req);
/*     */       }
/*     */     }
/*     */     finally {
/* 513 */       session.increaseWrittenBytes(writtenBytes, currentTime);
/*     */     }
/*     */ 
/* 516 */     return true;
/*     */   }
/*     */ 
/*     */   private int registerHandles() {
/*     */     while (true) {
/* 521 */       AbstractIoAcceptor.AcceptorOperationFuture req = (AbstractIoAcceptor.AcceptorOperationFuture)this.registerQueue.poll();
/* 522 */       if (req == null)
/*     */       {
/*     */         break;
/*     */       }
/* 526 */       Map newHandles = new HashMap();
/* 527 */       List localAddresses = req.getLocalAddresses();
/*     */       try {
/* 529 */         for (SocketAddress a : localAddresses) {
/* 530 */           Object handle = open(a);
/* 531 */           newHandles.put(localAddress(handle), handle);
/*     */         }
/* 533 */         this.boundHandles.putAll(newHandles);
/*     */ 
/* 535 */         getListeners().fireServiceActivated();
/* 536 */         req.setDone();
/* 537 */         ??? = newHandles.size();
/*     */         Iterator i$;
/*     */         Object handle;
/*     */         return ???;
/*     */       }
/*     */       catch (Exception i$)
/*     */       {
/* 539 */         req.setException(e);
/*     */       }
/*     */       finally
/*     */       {
/*     */         Iterator i$;
/*     */         Object handle;
/* 542 */         if (req.getException() != null) {
/* 543 */           for (Iterator i$ = newHandles.values().iterator(); i$.hasNext(); ) { Object handle = i$.next();
/*     */             try {
/* 545 */               close(handle);
/*     */             } catch (Exception e) {
/* 547 */               ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */             }
/*     */           }
/* 550 */           wakeup();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 555 */     return 0;
/*     */   }
/*     */ 
/*     */   private int unregisterHandles() {
/* 559 */     int nHandles = 0;
/*     */     while (true) {
/* 561 */       AbstractIoAcceptor.AcceptorOperationFuture request = (AbstractIoAcceptor.AcceptorOperationFuture)this.cancelQueue.poll();
/* 562 */       if (request == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 567 */       for (SocketAddress a : request.getLocalAddresses()) {
/* 568 */         Object handle = this.boundHandles.remove(a);
/* 569 */         if (handle == null) {
/*     */           continue;
/*     */         }
/*     */         try
/*     */         {
/* 574 */           close(handle);
/* 575 */           wakeup();
/*     */         } catch (Throwable e) {
/* 577 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         } finally {
/* 579 */           nHandles++;
/*     */         }
/*     */       }
/*     */ 
/* 583 */       request.setDone();
/*     */     }
/*     */ 
/* 586 */     return nHandles;
/*     */   }
/*     */ 
/*     */   private void notifyIdleSessions(long currentTime)
/*     */   {
/* 591 */     if (currentTime - this.lastIdleCheckTime >= 1000L) {
/* 592 */       this.lastIdleCheckTime = currentTime;
/* 593 */       AbstractIoSession.notifyIdleness(getListeners().getManagedSessions().values().iterator(), currentTime);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Acceptor
/*     */     implements Runnable
/*     */   {
/*     */     private Acceptor()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 351 */       int nHandles = 0;
/* 352 */       AbstractPollingConnectionlessIoAcceptor.access$302(AbstractPollingConnectionlessIoAcceptor.this, System.currentTimeMillis());
/*     */ 
/* 354 */       while (AbstractPollingConnectionlessIoAcceptor.this.selectable)
/*     */         try {
/* 356 */           int selected = AbstractPollingConnectionlessIoAcceptor.this.select();
/*     */ 
/* 358 */           nHandles += AbstractPollingConnectionlessIoAcceptor.this.registerHandles();
/*     */ 
/* 360 */           if (selected > 0) {
/* 361 */             AbstractPollingConnectionlessIoAcceptor.this.processReadySessions(AbstractPollingConnectionlessIoAcceptor.this.selectedHandles());
/*     */           }
/*     */ 
/* 364 */           long currentTime = System.currentTimeMillis();
/* 365 */           AbstractPollingConnectionlessIoAcceptor.this.flushSessions(currentTime);
/* 366 */           nHandles -= AbstractPollingConnectionlessIoAcceptor.this.unregisterHandles();
/*     */ 
/* 368 */           AbstractPollingConnectionlessIoAcceptor.this.notifyIdleSessions(currentTime);
/*     */ 
/* 370 */           if (nHandles == 0)
/* 371 */             synchronized (AbstractPollingConnectionlessIoAcceptor.this.lock) {
/* 372 */               if ((AbstractPollingConnectionlessIoAcceptor.this.registerQueue.isEmpty()) && (AbstractPollingConnectionlessIoAcceptor.this.cancelQueue.isEmpty())) {
/* 373 */                 AbstractPollingConnectionlessIoAcceptor.access$1302(AbstractPollingConnectionlessIoAcceptor.this, null);
/* 374 */                 break;
/*     */               }
/*     */             }
/*     */         }
/*     */         catch (Exception e) {
/* 379 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */           try
/*     */           {
/* 382 */             Thread.sleep(1000L);
/*     */           }
/*     */           catch (InterruptedException e1)
/*     */           {
/*     */           }
/*     */         }
/* 388 */       if ((AbstractPollingConnectionlessIoAcceptor.this.selectable) && (AbstractPollingConnectionlessIoAcceptor.this.isDisposing())) {
/* 389 */         AbstractPollingConnectionlessIoAcceptor.access$402(AbstractPollingConnectionlessIoAcceptor.this, false);
/*     */         try {
/* 391 */           AbstractPollingConnectionlessIoAcceptor.this.destroy();
/*     */         } catch (Exception e) {
/* 393 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         } finally {
/* 395 */           AbstractPollingConnectionlessIoAcceptor.this.disposalFuture.setValue(Boolean.valueOf(true));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ConnectionlessAcceptorProcessor
/*     */     implements IoProcessor<T>
/*     */   {
/*     */     private ConnectionlessAcceptorProcessor()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void add(T session)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void flush(T session)
/*     */     {
/* 291 */       if (AbstractPollingConnectionlessIoAcceptor.this.scheduleFlush(session))
/* 292 */         AbstractPollingConnectionlessIoAcceptor.this.wakeup();
/*     */     }
/*     */ 
/*     */     public void remove(T session)
/*     */     {
/* 297 */       AbstractPollingConnectionlessIoAcceptor.this.getSessionRecycler().remove(session);
/* 298 */       AbstractPollingConnectionlessIoAcceptor.this.getListeners().fireSessionDestroyed(session);
/*     */     }
/*     */ 
/*     */     public void updateTrafficControl(T session) {
/* 302 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void dispose() {
/*     */     }
/*     */ 
/*     */     public boolean isDisposed() {
/* 309 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isDisposing() {
/* 313 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.polling.AbstractPollingConnectionlessIoAcceptor
 * JD-Core Version:    0.6.0
 */
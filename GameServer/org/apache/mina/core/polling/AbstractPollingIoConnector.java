/*     */ package org.apache.mina.core.polling;
/*     */ 
/*     */ import java.net.ConnectException;
/*     */ import java.net.SocketAddress;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.DefaultConnectFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoConnector;
/*     */ import org.apache.mina.core.service.AbstractIoService.ServiceOperationFuture;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.SimpleIoProcessorPool;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public abstract class AbstractPollingIoConnector<T extends AbstractIoSession, H> extends AbstractIoConnector
/*     */ {
/*  68 */   private final Object lock = new Object();
/*  69 */   private final Queue<AbstractPollingIoConnector<T, H>.ConnectionRequest> connectQueue = new ConcurrentLinkedQueue();
/*  70 */   private final Queue<AbstractPollingIoConnector<T, H>.ConnectionRequest> cancelQueue = new ConcurrentLinkedQueue();
/*     */   private final IoProcessor<T> processor;
/*     */   private final boolean createdProcessor;
/*  74 */   private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
/*     */   private volatile boolean selectable;
/*     */   private AbstractPollingIoConnector<T, H>.Connector connector;
/*     */ 
/*     */   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass)
/*     */   {
/*  95 */     this(sessionConfig, null, new SimpleIoProcessorPool(processorClass), true);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass, int processorCount)
/*     */   {
/* 113 */     this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount), true);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, IoProcessor<T> processor)
/*     */   {
/* 129 */     this(sessionConfig, null, processor, false);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor)
/*     */   {
/* 149 */     this(sessionConfig, executor, processor, false);
/*     */   }
/*     */ 
/*     */   private AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor, boolean createdProcessor)
/*     */   {
/* 170 */     super(sessionConfig, executor);
/*     */ 
/* 172 */     if (processor == null) {
/* 173 */       throw new NullPointerException("processor");
/*     */     }
/*     */ 
/* 176 */     this.processor = processor;
/* 177 */     this.createdProcessor = createdProcessor;
/*     */     try
/*     */     {
/* 180 */       init();
/* 181 */       this.selectable = true;
/*     */     } catch (RuntimeException e) {
/* 183 */       throw e;
/*     */     } catch (Exception e) {
/* 185 */       throw new RuntimeIoException("Failed to initialize.", e);
/*     */     } finally {
/* 187 */       if (!this.selectable)
/*     */         try {
/* 189 */           destroy();
/*     */         } catch (Exception e) {
/* 191 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void init()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void destroy()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract H newHandle(SocketAddress paramSocketAddress)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract boolean connect(H paramH, SocketAddress paramSocketAddress)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract boolean finishConnect(H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract T newSession(IoProcessor<T> paramIoProcessor, H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void close(H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void wakeup();
/*     */ 
/*     */   protected abstract int select(int paramInt)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract Iterator<H> selectedHandles();
/*     */ 
/*     */   protected abstract Iterator<H> allHandles();
/*     */ 
/*     */   protected abstract void register(H paramH, AbstractPollingIoConnector<T, H>.ConnectionRequest paramAbstractPollingIoConnector)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract AbstractPollingIoConnector<T, H>.ConnectionRequest getConnectionRequest(H paramH);
/*     */ 
/*     */   protected final IoFuture dispose0()
/*     */     throws Exception
/*     */   {
/* 305 */     if (!this.disposalFuture.isDone()) {
/* 306 */       startupWorker();
/* 307 */       wakeup();
/*     */     }
/* 309 */     return this.disposalFuture;
/*     */   }
/*     */ 
/*     */   protected final ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer)
/*     */   {
/* 320 */     Object handle = null;
/* 321 */     boolean success = false;
/*     */     try {
/* 323 */       handle = newHandle(localAddress);
/* 324 */       if (connect(handle, remoteAddress)) {
/* 325 */         ConnectFuture future = new DefaultConnectFuture();
/* 326 */         session = newSession(this.processor, handle);
/* 327 */         initSession(session, future, sessionInitializer);
/*     */ 
/* 329 */         session.getProcessor().add(session);
/* 330 */         success = true;
/* 331 */         ConnectFuture localConnectFuture1 = future;
/*     */         return localConnectFuture1;
/*     */       }
/* 334 */       success = true;
/*     */     } catch (Exception e) {
/* 336 */       AbstractIoSession session = DefaultConnectFuture.newFailedFuture(e);
/*     */       return session;
/*     */     }
/*     */     finally
/*     */     {
/* 338 */       if ((!success) && (handle != null)) {
/*     */         try {
/* 340 */           close(handle);
/*     */         } catch (Exception e) {
/* 342 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 347 */     ConnectionRequest request = new ConnectionRequest(handle, sessionInitializer);
/* 348 */     this.connectQueue.add(request);
/* 349 */     startupWorker();
/* 350 */     wakeup();
/*     */ 
/* 352 */     return request;
/*     */   }
/*     */ 
/*     */   private void startupWorker() {
/* 356 */     if (!this.selectable) {
/* 357 */       this.connectQueue.clear();
/* 358 */       this.cancelQueue.clear();
/*     */     }
/*     */ 
/* 361 */     synchronized (this.lock) {
/* 362 */       if (this.connector == null) {
/* 363 */         this.connector = new Connector(null);
/* 364 */         executeWorker(this.connector);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int registerNew() {
/* 370 */     int nHandles = 0;
/*     */     while (true) {
/* 372 */       ConnectionRequest req = (ConnectionRequest)this.connectQueue.poll();
/* 373 */       if (req == null)
/*     */       {
/*     */         break;
/*     */       }
/* 377 */       Object handle = req.handle;
/*     */       try {
/* 379 */         register(handle, req);
/* 380 */         nHandles++;
/*     */       } catch (Exception e) {
/* 382 */         req.setException(e);
/*     */         try {
/* 384 */           close(handle);
/*     */         } catch (Exception e2) {
/* 386 */           ExceptionMonitor.getInstance().exceptionCaught(e2);
/*     */         }
/*     */       }
/*     */     }
/* 390 */     return nHandles;
/*     */   }
/*     */ 
/*     */   private int cancelKeys() {
/* 394 */     int nHandles = 0;
/*     */     while (true) {
/* 396 */       ConnectionRequest req = (ConnectionRequest)this.cancelQueue.poll();
/* 397 */       if (req == null)
/*     */       {
/*     */         break;
/*     */       }
/* 401 */       Object handle = req.handle;
/*     */       try {
/* 403 */         close(handle);
/*     */       } catch (Exception e) {
/* 405 */         ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */       } finally {
/* 407 */         nHandles++;
/*     */       }
/*     */     }
/* 410 */     return nHandles;
/*     */   }
/*     */ 
/*     */   private int processConnections(Iterator<H> handlers)
/*     */   {
/* 418 */     int nHandles = 0;
/*     */ 
/* 421 */     while (handlers.hasNext()) {
/* 422 */       Object handle = handlers.next();
/* 423 */       handlers.remove();
/*     */ 
/* 425 */       ConnectionRequest connectionRequest = getConnectionRequest(handle);
/*     */ 
/* 427 */       if (connectionRequest == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 431 */       boolean success = false;
/*     */       try {
/* 433 */         if (finishConnect(handle)) {
/* 434 */           AbstractIoSession session = newSession(this.processor, handle);
/* 435 */           initSession(session, connectionRequest, connectionRequest.getSessionInitializer());
/*     */ 
/* 437 */           session.getProcessor().add(session);
/* 438 */           nHandles++;
/*     */         }
/* 440 */         success = true;
/*     */       } catch (Throwable e) {
/* 442 */         connectionRequest.setException(e);
/*     */       } finally {
/* 444 */         if (!success)
/*     */         {
/* 446 */           this.cancelQueue.offer(connectionRequest);
/*     */         }
/*     */       }
/*     */     }
/* 450 */     return nHandles;
/*     */   }
/*     */ 
/*     */   private void processTimedOutSessions(Iterator<H> handles) {
/* 454 */     long currentTime = System.currentTimeMillis();
/*     */ 
/* 456 */     while (handles.hasNext()) {
/* 457 */       Object handle = handles.next();
/* 458 */       ConnectionRequest connectionRequest = getConnectionRequest(handle);
/*     */ 
/* 460 */       if ((connectionRequest != null) && (currentTime >= connectionRequest.deadline)) {
/* 461 */         connectionRequest.setException(new ConnectException("Connection timed out."));
/*     */ 
/* 463 */         this.cancelQueue.offer(connectionRequest);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public final class ConnectionRequest extends DefaultConnectFuture
/*     */   {
/*     */     private final H handle;
/*     */     private final long deadline;
/*     */     private final IoSessionInitializer<? extends ConnectFuture> sessionInitializer;
/*     */ 
/*     */     public ConnectionRequest(IoSessionInitializer<? extends ConnectFuture> handle)
/*     */     {
/* 537 */       this.handle = handle;
/* 538 */       long timeout = AbstractPollingIoConnector.this.getConnectTimeoutMillis();
/* 539 */       if (timeout <= 0L)
/* 540 */         this.deadline = 9223372036854775807L;
/*     */       else {
/* 542 */         this.deadline = (System.currentTimeMillis() + timeout);
/*     */       }
/* 544 */       this.sessionInitializer = callback;
/*     */     }
/*     */ 
/*     */     public H getHandle() {
/* 548 */       return this.handle;
/*     */     }
/*     */ 
/*     */     public long getDeadline() {
/* 552 */       return this.deadline;
/*     */     }
/*     */ 
/*     */     public IoSessionInitializer<? extends ConnectFuture> getSessionInitializer() {
/* 556 */       return this.sessionInitializer;
/*     */     }
/*     */ 
/*     */     public void cancel()
/*     */     {
/* 561 */       if (!isDone()) {
/* 562 */         super.cancel();
/* 563 */         AbstractPollingIoConnector.this.cancelQueue.add(this);
/* 564 */         AbstractPollingIoConnector.this.startupWorker();
/* 565 */         AbstractPollingIoConnector.this.wakeup();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Connector
/*     */     implements Runnable
/*     */   {
/*     */     private Connector()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 471 */       int nHandles = 0;
/* 472 */       while (AbstractPollingIoConnector.this.selectable)
/*     */       {
/*     */         try
/*     */         {
/* 476 */           int timeout = (int)Math.min(AbstractPollingIoConnector.this.getConnectTimeoutMillis(), 1000L);
/* 477 */           int selected = AbstractPollingIoConnector.this.select(timeout);
/*     */ 
/* 479 */           nHandles += AbstractPollingIoConnector.this.registerNew();
/*     */ 
/* 481 */           if (selected > 0) {
/* 482 */             nHandles -= AbstractPollingIoConnector.this.processConnections(AbstractPollingIoConnector.this.selectedHandles());
/*     */           }
/*     */ 
/* 485 */           AbstractPollingIoConnector.this.processTimedOutSessions(AbstractPollingIoConnector.this.allHandles());
/*     */ 
/* 487 */           nHandles -= AbstractPollingIoConnector.this.cancelKeys();
/*     */ 
/* 489 */           if (nHandles == 0)
/* 490 */             synchronized (AbstractPollingIoConnector.this.lock) {
/* 491 */               if (AbstractPollingIoConnector.this.connectQueue.isEmpty()) {
/* 492 */                 AbstractPollingIoConnector.access$1002(AbstractPollingIoConnector.this, null);
/* 493 */                 break;
/*     */               }
/*     */             }
/*     */         }
/*     */         catch (Throwable e) {
/* 498 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */           try
/*     */           {
/* 501 */             Thread.sleep(1000L);
/*     */           } catch (InterruptedException e1) {
/* 503 */             ExceptionMonitor.getInstance().exceptionCaught(e1);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 508 */       if ((AbstractPollingIoConnector.this.selectable) && (AbstractPollingIoConnector.this.isDisposing())) {
/* 509 */         AbstractPollingIoConnector.access$302(AbstractPollingIoConnector.this, false);
/*     */         try {
/* 511 */           if (AbstractPollingIoConnector.this.createdProcessor)
/* 512 */             AbstractPollingIoConnector.this.processor.dispose();
/*     */         }
/*     */         finally {
/*     */           try {
/* 516 */             synchronized (AbstractPollingIoConnector.this.disposalLock) {
/* 517 */               if (AbstractPollingIoConnector.this.isDisposing())
/* 518 */                 AbstractPollingIoConnector.this.destroy();
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/* 522 */             ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */           } finally {
/* 524 */             AbstractPollingIoConnector.this.disposalFuture.setDone();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.polling.AbstractPollingIoConnector
 * JD-Core Version:    0.6.0
 */
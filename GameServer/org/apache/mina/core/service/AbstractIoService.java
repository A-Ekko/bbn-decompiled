/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.IoUtil;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChain;
/*     */ import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
/*     */ import org.apache.mina.core.filterchain.IoFilterChainBuilder;
/*     */ import org.apache.mina.core.future.ConnectFuture;
/*     */ import org.apache.mina.core.future.DefaultIoFuture;
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.DefaultIoSessionDataStructureFactory;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.session.IoSessionDataStructureFactory;
/*     */ import org.apache.mina.core.session.IoSessionInitializationException;
/*     */ import org.apache.mina.core.session.IoSessionInitializer;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ import org.apache.mina.util.NamePreservingRunnable;
/*     */ 
/*     */ public abstract class AbstractIoService
/*     */   implements IoService
/*     */ {
/*  66 */   private static final AtomicInteger id = new AtomicInteger();
/*     */   private final String threadName;
/*     */   private final Executor executor;
/*     */   private final boolean createdExecutor;
/*     */   private IoHandler handler;
/*     */   private final IoSessionConfig sessionConfig;
/*  98 */   private final IoServiceListener serviceActivationListener = new IoServiceListener()
/*     */   {
/*     */     public void serviceActivated(IoService service) {
/* 101 */       AbstractIoService s = (AbstractIoService)service;
/* 102 */       IoServiceStatistics _stats = s.getStatistics();
/* 103 */       _stats.setLastReadTime(s.getActivationTime());
/* 104 */       _stats.setLastWriteTime(s.getActivationTime());
/* 105 */       _stats.setLastThroughputCalculationTime(s.getActivationTime());
/*     */     }
/*     */ 
/*     */     public void serviceDeactivated(IoService service)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void serviceIdle(IoService service, IdleStatus idleStatus)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void sessionCreated(IoSession session)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void sessionDestroyed(IoSession session)
/*     */     {
/*     */     }
/*  98 */   };
/*     */ 
/* 125 */   private IoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
/*     */ 
/* 127 */   private IoSessionDataStructureFactory sessionDataStructureFactory = new DefaultIoSessionDataStructureFactory();
/*     */   private final IoServiceListenerSupport listeners;
/* 138 */   protected final Object disposalLock = new Object();
/*     */   private volatile boolean disposing;
/*     */   private volatile boolean disposed;
/*     */   private IoFuture disposalFuture;
/* 149 */   private IoServiceStatistics stats = new IoServiceStatistics(this);
/*     */ 
/*     */   protected AbstractIoService(IoSessionConfig sessionConfig, Executor executor)
/*     */   {
/* 165 */     if (sessionConfig == null) {
/* 166 */       throw new NullPointerException("sessionConfig");
/*     */     }
/*     */ 
/* 169 */     if (getTransportMetadata() == null) {
/* 170 */       throw new NullPointerException("TransportMetadata");
/*     */     }
/*     */ 
/* 173 */     if (!getTransportMetadata().getSessionConfigType().isAssignableFrom(sessionConfig.getClass()))
/*     */     {
/* 175 */       throw new IllegalArgumentException("sessionConfig type: " + sessionConfig.getClass() + " (expected: " + getTransportMetadata().getSessionConfigType() + ")");
/*     */     }
/*     */ 
/* 182 */     this.listeners = new IoServiceListenerSupport(this);
/* 183 */     this.listeners.add(this.serviceActivationListener);
/*     */ 
/* 186 */     this.sessionConfig = sessionConfig;
/*     */ 
/* 190 */     ExceptionMonitor.getInstance();
/*     */ 
/* 192 */     if (executor == null) {
/* 193 */       this.executor = Executors.newCachedThreadPool();
/* 194 */       this.createdExecutor = true;
/*     */     } else {
/* 196 */       this.executor = executor;
/* 197 */       this.createdExecutor = false;
/*     */     }
/*     */ 
/* 200 */     this.threadName = (getClass().getSimpleName() + '-' + id.incrementAndGet());
/*     */   }
/*     */ 
/*     */   public final IoFilterChainBuilder getFilterChainBuilder()
/*     */   {
/* 207 */     return this.filterChainBuilder;
/*     */   }
/*     */ 
/*     */   public final void setFilterChainBuilder(IoFilterChainBuilder builder)
/*     */   {
/* 214 */     if (builder == null) {
/* 215 */       builder = new DefaultIoFilterChainBuilder();
/*     */     }
/* 217 */     this.filterChainBuilder = builder;
/*     */   }
/*     */ 
/*     */   public final DefaultIoFilterChainBuilder getFilterChain()
/*     */   {
/* 224 */     if ((this.filterChainBuilder instanceof DefaultIoFilterChainBuilder)) {
/* 225 */       return (DefaultIoFilterChainBuilder)this.filterChainBuilder;
/*     */     }
/* 227 */     throw new IllegalStateException("Current filter chain builder is not a DefaultIoFilterChainBuilder.");
/*     */   }
/*     */ 
/*     */   public final void addListener(IoServiceListener listener)
/*     */   {
/* 236 */     this.listeners.add(listener);
/*     */   }
/*     */ 
/*     */   public final void removeListener(IoServiceListener listener)
/*     */   {
/* 243 */     this.listeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public final boolean isActive()
/*     */   {
/* 250 */     return this.listeners.isActive();
/*     */   }
/*     */ 
/*     */   public final boolean isDisposing()
/*     */   {
/* 257 */     return this.disposing;
/*     */   }
/*     */ 
/*     */   public final boolean isDisposed()
/*     */   {
/* 264 */     return this.disposed;
/*     */   }
/*     */ 
/*     */   public final void dispose()
/*     */   {
/* 271 */     if (this.disposed)
/* 272 */       return;
/*     */     IoFuture disposalFuture;
/* 276 */     synchronized (this.disposalLock) {
/* 277 */       disposalFuture = this.disposalFuture;
/* 278 */       if (!this.disposing) {
/* 279 */         this.disposing = true;
/*     */         try {
/* 281 */           this.disposalFuture = (disposalFuture = dispose0());
/*     */         } catch (Exception e) {
/* 283 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         } finally {
/* 285 */           if (disposalFuture == null) {
/* 286 */             this.disposed = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 292 */     if (disposalFuture != null) {
/* 293 */       disposalFuture.awaitUninterruptibly();
/*     */     }
/*     */ 
/* 296 */     if (this.createdExecutor) {
/* 297 */       ExecutorService e = (ExecutorService)this.executor;
/* 298 */       e.shutdown();
/* 299 */       while (!e.isTerminated()) {
/*     */         try {
/* 301 */           e.awaitTermination(2147483647L, TimeUnit.SECONDS);
/*     */         }
/*     */         catch (InterruptedException e1)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 308 */     this.disposed = true;
/*     */   }
/*     */ 
/*     */   protected abstract IoFuture dispose0()
/*     */     throws Exception;
/*     */ 
/*     */   public final Map<Long, IoSession> getManagedSessions()
/*     */   {
/* 321 */     return this.listeners.getManagedSessions();
/*     */   }
/*     */ 
/*     */   public final int getManagedSessionCount()
/*     */   {
/* 328 */     return this.listeners.getManagedSessionCount();
/*     */   }
/*     */ 
/*     */   public final IoHandler getHandler()
/*     */   {
/* 335 */     return this.handler;
/*     */   }
/*     */ 
/*     */   public final void setHandler(IoHandler handler)
/*     */   {
/* 342 */     if (handler == null) {
/* 343 */       throw new NullPointerException("handler cannot be null");
/*     */     }
/*     */ 
/* 346 */     if (isActive()) {
/* 347 */       throw new IllegalStateException("handler cannot be set while the service is active.");
/*     */     }
/*     */ 
/* 351 */     this.handler = handler;
/*     */   }
/*     */ 
/*     */   public IoSessionConfig getSessionConfig()
/*     */   {
/* 358 */     return this.sessionConfig;
/*     */   }
/*     */ 
/*     */   public final IoSessionDataStructureFactory getSessionDataStructureFactory()
/*     */   {
/* 365 */     return this.sessionDataStructureFactory;
/*     */   }
/*     */ 
/*     */   public final void setSessionDataStructureFactory(IoSessionDataStructureFactory sessionDataStructureFactory)
/*     */   {
/* 373 */     if (sessionDataStructureFactory == null) {
/* 374 */       throw new NullPointerException("sessionDataStructureFactory");
/*     */     }
/*     */ 
/* 377 */     if (isActive()) {
/* 378 */       throw new IllegalStateException("sessionDataStructureFactory cannot be set while the service is active.");
/*     */     }
/*     */ 
/* 382 */     this.sessionDataStructureFactory = sessionDataStructureFactory;
/*     */   }
/*     */ 
/*     */   public IoServiceStatistics getStatistics()
/*     */   {
/* 389 */     return this.stats;
/*     */   }
/*     */ 
/*     */   public final long getActivationTime()
/*     */   {
/* 396 */     return this.listeners.getActivationTime();
/*     */   }
/*     */ 
/*     */   public final Set<WriteFuture> broadcast(Object message)
/*     */   {
/* 406 */     List futures = IoUtil.broadcast(message, getManagedSessions().values());
/*     */ 
/* 408 */     return new AbstractSet(futures)
/*     */     {
/*     */       public Iterator<WriteFuture> iterator() {
/* 411 */         return this.val$futures.iterator();
/*     */       }
/*     */ 
/*     */       public int size()
/*     */       {
/* 416 */         return this.val$futures.size();
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public final IoServiceListenerSupport getListeners() {
/* 422 */     return this.listeners;
/*     */   }
/*     */ 
/*     */   protected final void executeWorker(Runnable worker)
/*     */   {
/* 427 */     executeWorker(worker, null);
/*     */   }
/*     */ 
/*     */   protected final void executeWorker(Runnable worker, String suffix) {
/* 431 */     String actualThreadName = this.threadName;
/* 432 */     if (suffix != null) {
/* 433 */       actualThreadName = actualThreadName + '-' + suffix;
/*     */     }
/* 435 */     this.executor.execute(new NamePreservingRunnable(worker, actualThreadName));
/*     */   }
/*     */ 
/*     */   protected final void initSession(IoSession session, IoFuture future, IoSessionInitializer sessionInitializer)
/*     */   {
/* 443 */     if (this.stats.getLastReadTime() == 0L) {
/* 444 */       this.stats.setLastReadTime(getActivationTime());
/*     */     }
/*     */ 
/* 447 */     if (this.stats.getLastWriteTime() == 0L) {
/* 448 */       this.stats.setLastWriteTime(getActivationTime());
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 456 */       ((AbstractIoSession)session).setAttributeMap(session.getService().getSessionDataStructureFactory().getAttributeMap(session));
/*     */     }
/*     */     catch (IoSessionInitializationException e) {
/* 459 */       throw e;
/*     */     } catch (Exception e) {
/* 461 */       throw new IoSessionInitializationException("Failed to initialize an attributeMap.", e);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 466 */       ((AbstractIoSession)session).setWriteRequestQueue(session.getService().getSessionDataStructureFactory().getWriteRequestQueue(session));
/*     */     }
/*     */     catch (IoSessionInitializationException e)
/*     */     {
/* 470 */       throw e;
/*     */     } catch (Exception e) {
/* 472 */       throw new IoSessionInitializationException("Failed to initialize a writeRequestQueue.", e);
/*     */     }
/*     */ 
/* 476 */     if ((future != null) && ((future instanceof ConnectFuture)))
/*     */     {
/* 478 */       session.setAttribute(DefaultIoFilterChain.SESSION_CREATED_FUTURE, future);
/*     */     }
/*     */ 
/* 482 */     if (sessionInitializer != null) {
/* 483 */       sessionInitializer.initializeSession(session, future);
/*     */     }
/*     */ 
/* 486 */     finishSessionInitialization0(session, future);
/*     */   }
/*     */ 
/*     */   protected void finishSessionInitialization0(IoSession session, IoFuture future)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getScheduledWriteBytes()
/*     */   {
/* 532 */     return this.stats.getScheduledWriteBytes();
/*     */   }
/*     */ 
/*     */   public int getScheduledWriteMessages()
/*     */   {
/* 539 */     return this.stats.getScheduledWriteMessages();
/*     */   }
/*     */ 
/*     */   protected static class ServiceOperationFuture extends DefaultIoFuture
/*     */   {
/*     */     public ServiceOperationFuture()
/*     */     {
/* 501 */       super();
/*     */     }
/*     */ 
/*     */     public final boolean isDone() {
/* 505 */       return getValue() == Boolean.TRUE;
/*     */     }
/*     */ 
/*     */     public final void setDone() {
/* 509 */       setValue(Boolean.TRUE);
/*     */     }
/*     */ 
/*     */     public final Exception getException() {
/* 513 */       if ((getValue() instanceof Exception)) {
/* 514 */         return (Exception)getValue();
/*     */       }
/* 516 */       return null;
/*     */     }
/*     */ 
/*     */     public final void setException(Exception exception)
/*     */     {
/* 521 */       if (exception == null) {
/* 522 */         throw new NullPointerException("exception");
/*     */       }
/* 524 */       setValue(exception);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.AbstractIoService
 * JD-Core Version:    0.6.0
 */
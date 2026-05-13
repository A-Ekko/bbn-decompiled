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
/*     */ import org.apache.mina.core.future.IoFuture;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor;
/*     */ import org.apache.mina.core.service.AbstractIoAcceptor.AcceptorOperationFuture;
/*     */ import org.apache.mina.core.service.AbstractIoService.ServiceOperationFuture;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.SimpleIoProcessorPool;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ 
/*     */ public abstract class AbstractPollingIoAcceptor<T extends AbstractIoSession, H> extends AbstractIoAcceptor
/*     */ {
/*     */   private final IoProcessor<T> processor;
/*     */   private final boolean createdProcessor;
/*  74 */   private final Object lock = new Object();
/*     */ 
/*  76 */   private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue = new ConcurrentLinkedQueue();
/*     */ 
/*  78 */   private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue = new ConcurrentLinkedQueue();
/*     */ 
/*  80 */   private final Map<SocketAddress, H> boundHandles = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  83 */   private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
/*     */   private volatile boolean selectable;
/*     */   private AbstractPollingIoAcceptor<T, H>.Acceptor acceptor;
/*     */ 
/*     */   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass)
/*     */   {
/* 106 */     this(sessionConfig, null, new SimpleIoProcessorPool(processorClass), true);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass, int processorCount)
/*     */   {
/* 126 */     this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount), true);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, IoProcessor<T> processor)
/*     */   {
/* 144 */     this(sessionConfig, null, processor, false);
/*     */   }
/*     */ 
/*     */   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor)
/*     */   {
/* 165 */     this(sessionConfig, executor, processor, false);
/*     */   }
/*     */ 
/*     */   private AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor, boolean createdProcessor)
/*     */   {
/* 190 */     super(sessionConfig, executor);
/*     */ 
/* 192 */     if (processor == null) {
/* 193 */       throw new NullPointerException("processor");
/*     */     }
/*     */ 
/* 196 */     this.processor = processor;
/* 197 */     this.createdProcessor = createdProcessor;
/*     */     try
/*     */     {
/* 201 */       init();
/*     */ 
/* 205 */       this.selectable = true;
/*     */     } catch (RuntimeException e) {
/* 207 */       throw e;
/*     */     } catch (Exception e) {
/* 209 */       throw new RuntimeIoException("Failed to initialize.", e);
/*     */     } finally {
/* 211 */       if (!this.selectable)
/*     */         try {
/* 213 */           destroy();
/*     */         } catch (Exception e) {
/* 215 */           ExceptionMonitor.getInstance().exceptionCaught(e);
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
/*     */   protected abstract int select()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void wakeup();
/*     */ 
/*     */   protected abstract Iterator<H> selectedHandles();
/*     */ 
/*     */   protected abstract H open(SocketAddress paramSocketAddress)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract SocketAddress localAddress(H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract T accept(IoProcessor<T> paramIoProcessor, H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void close(H paramH)
/*     */     throws Exception;
/*     */ 
/*     */   protected IoFuture dispose0()
/*     */     throws Exception
/*     */   {
/* 293 */     unbind();
/* 294 */     if (!this.disposalFuture.isDone()) {
/* 295 */       startupAcceptor();
/* 296 */       wakeup();
/*     */     }
/* 298 */     return this.disposalFuture;
/*     */   }
/*     */ 
/*     */   protected final Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses)
/*     */     throws Exception
/*     */   {
/* 309 */     AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
/*     */ 
/* 314 */     this.registerQueue.add(request);
/*     */ 
/* 318 */     startupAcceptor();
/*     */ 
/* 323 */     wakeup();
/*     */ 
/* 326 */     request.awaitUninterruptibly();
/*     */ 
/* 328 */     if (request.getException() != null) {
/* 329 */       throw request.getException();
/*     */     }
/*     */ 
/* 335 */     Set newLocalAddresses = new HashSet();
/*     */ 
/* 337 */     for (Iterator i$ = this.boundHandles.values().iterator(); i$.hasNext(); ) { Object handle = i$.next();
/* 338 */       newLocalAddresses.add(localAddress(handle));
/*     */     }
/*     */ 
/* 341 */     return newLocalAddresses;
/*     */   }
/*     */ 
/*     */   private void startupAcceptor()
/*     */   {
/* 355 */     if (!this.selectable) {
/* 356 */       this.registerQueue.clear();
/* 357 */       this.cancelQueue.clear();
/*     */     }
/*     */ 
/* 361 */     synchronized (this.lock) {
/* 362 */       if (this.acceptor == null) {
/* 363 */         this.acceptor = new Acceptor(null);
/* 364 */         executeWorker(this.acceptor);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void unbind0(List<? extends SocketAddress> localAddresses)
/*     */     throws Exception
/*     */   {
/* 375 */     AbstractIoAcceptor.AcceptorOperationFuture future = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
/*     */ 
/* 378 */     this.cancelQueue.add(future);
/* 379 */     startupAcceptor();
/* 380 */     wakeup();
/*     */ 
/* 382 */     future.awaitUninterruptibly();
/* 383 */     if (future.getException() != null)
/* 384 */       throw future.getException();
/*     */   }
/*     */ 
/*     */   private int registerHandles()
/*     */   {
/*     */     while (true)
/*     */     {
/* 510 */       AbstractIoAcceptor.AcceptorOperationFuture future = (AbstractIoAcceptor.AcceptorOperationFuture)this.registerQueue.poll();
/*     */ 
/* 512 */       if (future == null) {
/* 513 */         return 0;
/*     */       }
/*     */ 
/* 519 */       Map newHandles = new HashMap();
/* 520 */       List localAddresses = future.getLocalAddresses();
/*     */       try
/*     */       {
/* 524 */         for (SocketAddress a : localAddresses) {
/* 525 */           Object handle = open(a);
/* 526 */           newHandles.put(localAddress(handle), handle);
/*     */         }
/*     */ 
/* 531 */         this.boundHandles.putAll(newHandles);
/*     */ 
/* 534 */         future.setDone();
/* 535 */         ??? = newHandles.size();
/*     */         Iterator i$;
/*     */         Object handle;
/*     */         return ???;
/*     */       }
/*     */       catch (Exception i$)
/*     */       {
/* 538 */         future.setException(e);
/*     */       }
/*     */       finally
/*     */       {
/*     */         Iterator i$;
/*     */         Object handle;
/* 541 */         if (future.getException() != null) {
/* 542 */           for (Iterator i$ = newHandles.values().iterator(); i$.hasNext(); ) { Object handle = i$.next();
/*     */             try {
/* 544 */               close(handle);
/*     */             } catch (Exception e) {
/* 546 */               ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 551 */           wakeup();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int unregisterHandles()
/*     */   {
/* 564 */     int cancelledHandles = 0;
/*     */     while (true) {
/* 566 */       AbstractIoAcceptor.AcceptorOperationFuture future = (AbstractIoAcceptor.AcceptorOperationFuture)this.cancelQueue.poll();
/* 567 */       if (future == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 572 */       for (SocketAddress a : future.getLocalAddresses()) {
/* 573 */         Object handle = this.boundHandles.remove(a);
/* 574 */         if (handle == null) {
/*     */           continue;
/*     */         }
/*     */         try
/*     */         {
/* 579 */           close(handle);
/* 580 */           wakeup();
/*     */         } catch (Throwable e) {
/* 582 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         } finally {
/* 584 */           cancelledHandles++;
/*     */         }
/*     */       }
/*     */ 
/* 588 */       future.setDone();
/*     */     }
/*     */ 
/* 591 */     return cancelledHandles;
/*     */   }
/*     */ 
/*     */   public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress)
/*     */   {
/* 599 */     throw new UnsupportedOperationException();
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
/* 396 */       int nHandles = 0;
/*     */ 
/* 398 */       while (AbstractPollingIoAcceptor.this.selectable)
/*     */       {
/*     */         try
/*     */         {
/* 404 */           int selected = AbstractPollingIoAcceptor.this.select();
/*     */ 
/* 409 */           nHandles += AbstractPollingIoAcceptor.this.registerHandles();
/*     */ 
/* 411 */           if (selected > 0)
/*     */           {
/* 414 */             processHandles(AbstractPollingIoAcceptor.this.selectedHandles());
/*     */           }
/*     */ 
/* 418 */           nHandles -= AbstractPollingIoAcceptor.this.unregisterHandles();
/*     */ 
/* 423 */           if (nHandles == 0)
/* 424 */             synchronized (AbstractPollingIoAcceptor.this.lock) {
/* 425 */               if ((AbstractPollingIoAcceptor.this.registerQueue.isEmpty()) && (AbstractPollingIoAcceptor.this.cancelQueue.isEmpty()))
/*     */               {
/* 427 */                 AbstractPollingIoAcceptor.access$702(AbstractPollingIoAcceptor.this, null);
/* 428 */                 break;
/*     */               }
/*     */             }
/*     */         }
/*     */         catch (Throwable e) {
/* 433 */           ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */           try
/*     */           {
/* 436 */             Thread.sleep(1000L);
/*     */           } catch (InterruptedException e1) {
/* 438 */             ExceptionMonitor.getInstance().exceptionCaught(e1);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 444 */       if ((AbstractPollingIoAcceptor.this.selectable) && (AbstractPollingIoAcceptor.this.isDisposing())) {
/* 445 */         AbstractPollingIoAcceptor.access$102(AbstractPollingIoAcceptor.this, false);
/*     */         try {
/* 447 */           if (AbstractPollingIoAcceptor.this.createdProcessor)
/* 448 */             AbstractPollingIoAcceptor.this.processor.dispose();
/*     */         }
/*     */         finally {
/*     */           try {
/* 452 */             synchronized (AbstractPollingIoAcceptor.this.disposalLock) {
/* 453 */               if (AbstractPollingIoAcceptor.this.isDisposing())
/* 454 */                 AbstractPollingIoAcceptor.this.destroy();
/*     */             }
/*     */           }
/*     */           catch (Exception e) {
/* 458 */             ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */           } finally {
/* 460 */             AbstractPollingIoAcceptor.this.disposalFuture.setDone();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void processHandles(Iterator<H> handles)
/*     */       throws Exception
/*     */     {
/* 477 */       while (handles.hasNext()) {
/* 478 */         Object handle = handles.next();
/* 479 */         handles.remove();
/*     */ 
/* 483 */         AbstractIoSession session = AbstractPollingIoAcceptor.this.accept(AbstractPollingIoAcceptor.this.processor, handle);
/*     */ 
/* 485 */         if (session == null)
/*     */         {
/*     */           break;
/*     */         }
/* 489 */         AbstractPollingIoAcceptor.this.initSession(session, null, null);
/*     */ 
/* 492 */         session.getProcessor().add(session);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.polling.AbstractPollingIoAcceptor
 * JD-Core Version:    0.6.0
 */
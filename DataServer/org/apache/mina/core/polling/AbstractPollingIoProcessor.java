/*     */ package org.apache.mina.core.polling;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.file.FileRegion;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterChainBuilder;
/*     */ import org.apache.mina.core.future.DefaultIoFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.service.AbstractIoService;
/*     */ import org.apache.mina.core.service.IoProcessor;
/*     */ import org.apache.mina.core.service.IoService;
/*     */ import org.apache.mina.core.service.IoServiceListenerSupport;
/*     */ import org.apache.mina.core.service.TransportMetadata;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.IoSessionConfig;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ import org.apache.mina.core.write.WriteToClosedSessionException;
/*     */ import org.apache.mina.util.ExceptionMonitor;
/*     */ import org.apache.mina.util.NamePreservingRunnable;
/*     */ 
/*     */ public abstract class AbstractPollingIoProcessor<T extends AbstractIoSession>
/*     */   implements IoProcessor<T>
/*     */ {
/*     */   private static final int WRITE_SPIN_COUNT = 256;
/*     */   private static final long SELECT_TIMEOUT = 1000L;
/*  70 */   private static final Map<Class<?>, AtomicInteger> threadIds = new HashMap();
/*     */ 
/*  73 */   private final Object lock = new Object();
/*     */   private final String threadName;
/*     */   private final Executor executor;
/*  78 */   private final Queue<T> newSessions = new ConcurrentLinkedQueue();
/*  79 */   private final Queue<T> removingSessions = new ConcurrentLinkedQueue();
/*  80 */   private final Queue<T> flushingSessions = new ConcurrentLinkedQueue();
/*  81 */   private final Queue<T> trafficControllingSessions = new ConcurrentLinkedQueue();
/*     */   private AbstractPollingIoProcessor<T>.Processor processor;
/*     */   private long lastIdleCheckTime;
/*  88 */   private final Object disposalLock = new Object();
/*     */   private volatile boolean disposing;
/*     */   private volatile boolean disposed;
/*  91 */   private final DefaultIoFuture disposalFuture = new DefaultIoFuture(null);
/*     */ 
/*     */   protected AbstractPollingIoProcessor(Executor executor)
/*     */   {
/* 100 */     if (executor == null) {
/* 101 */       throw new NullPointerException("executor");
/*     */     }
/*     */ 
/* 104 */     this.threadName = nextThreadName();
/* 105 */     this.executor = executor;
/*     */   }
/*     */ 
/*     */   private String nextThreadName()
/*     */   {
/* 117 */     Class cls = getClass();
/*     */     int newThreadId;
/* 123 */     synchronized (threadIds)
/*     */     {
/* 125 */       AtomicInteger threadId = (AtomicInteger)threadIds.get(cls);
/*     */ 
/* 127 */       if (threadId == null)
/*     */       {
/* 131 */         int newThreadId = 1;
/* 132 */         threadIds.put(cls, new AtomicInteger(newThreadId));
/*     */       }
/*     */       else {
/* 135 */         newThreadId = threadId.incrementAndGet();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 140 */     return cls.getSimpleName() + '-' + newThreadId;
/*     */   }
/*     */ 
/*     */   public final boolean isDisposing()
/*     */   {
/* 147 */     return this.disposing;
/*     */   }
/*     */ 
/*     */   public final boolean isDisposed()
/*     */   {
/* 154 */     return this.disposed;
/*     */   }
/*     */ 
/*     */   public final void dispose()
/*     */   {
/* 161 */     if (this.disposed) {
/* 162 */       return;
/*     */     }
/*     */ 
/* 165 */     synchronized (this.disposalLock) {
/* 166 */       if (!this.disposing) {
/* 167 */         this.disposing = true;
/* 168 */         startupProcessor();
/*     */       }
/*     */     }
/*     */ 
/* 172 */     this.disposalFuture.awaitUninterruptibly();
/* 173 */     this.disposed = true;
/*     */   }
/*     */ 
/*     */   protected abstract void dispose0()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract int select(long paramLong)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract int select()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract boolean isSelectorEmpty();
/*     */ 
/*     */   protected abstract void wakeup();
/*     */ 
/*     */   protected abstract Iterator<T> allSessions();
/*     */ 
/*     */   protected abstract Iterator<T> selectedSessions();
/*     */ 
/*     */   protected abstract SessionState state(T paramT);
/*     */ 
/*     */   protected abstract boolean isWritable(T paramT);
/*     */ 
/*     */   protected abstract boolean isReadable(T paramT);
/*     */ 
/*     */   protected abstract void setInterestedInWrite(T paramT, boolean paramBoolean)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void setInterestedInRead(T paramT, boolean paramBoolean)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract boolean isInterestedInRead(T paramT);
/*     */ 
/*     */   protected abstract boolean isInterestedInWrite(T paramT);
/*     */ 
/*     */   protected abstract void init(T paramT)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void destroy(T paramT)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract int read(T paramT, IoBuffer paramIoBuffer)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract int write(T paramT, IoBuffer paramIoBuffer, int paramInt)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract int transferFile(T paramT, FileRegion paramFileRegion, int paramInt)
/*     */     throws Exception;
/*     */ 
/*     */   public final void add(T session)
/*     */   {
/* 327 */     if (isDisposing()) {
/* 328 */       throw new IllegalStateException("Already disposed.");
/*     */     }
/*     */ 
/* 332 */     this.newSessions.add(session);
/* 333 */     startupProcessor();
/*     */   }
/*     */ 
/*     */   public final void remove(T session)
/*     */   {
/* 340 */     scheduleRemove(session);
/* 341 */     startupProcessor();
/*     */   }
/*     */ 
/*     */   private void scheduleRemove(T session) {
/* 345 */     this.removingSessions.add(session);
/*     */   }
/*     */ 
/*     */   public final void flush(T session)
/*     */   {
/* 352 */     boolean needsWakeup = this.flushingSessions.isEmpty();
/* 353 */     if ((scheduleFlush(session)) && (needsWakeup))
/* 354 */       wakeup();
/*     */   }
/*     */ 
/*     */   private boolean scheduleFlush(T session)
/*     */   {
/* 359 */     if (session.setScheduledForFlush(true)) {
/* 360 */       this.flushingSessions.add(session);
/* 361 */       return true;
/*     */     }
/* 363 */     return false;
/*     */   }
/*     */ 
/*     */   public final void updateTrafficMask(T session)
/*     */   {
/* 370 */     scheduleTrafficControl(session);
/* 371 */     wakeup();
/*     */   }
/*     */ 
/*     */   private void scheduleTrafficControl(T session) {
/* 375 */     this.trafficControllingSessions.add(session);
/*     */   }
/*     */ 
/*     */   private void startupProcessor()
/*     */   {
/* 383 */     synchronized (this.lock) {
/* 384 */       if (this.processor == null) {
/* 385 */         this.processor = new Processor(null);
/* 386 */         this.executor.execute(new NamePreservingRunnable(this.processor, this.threadName));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 392 */     wakeup();
/*     */   }
/*     */ 
/*     */   private int handleNewSessions()
/*     */   {
/* 400 */     int addedSessions = 0;
/*     */     while (true)
/*     */     {
/* 405 */       AbstractIoSession session = (AbstractIoSession)this.newSessions.poll();
/*     */ 
/* 407 */       if (session == null)
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 413 */       if (addNow(session))
/*     */       {
/* 415 */         addedSessions++;
/*     */       }
/*     */     }
/*     */ 
/* 419 */     return addedSessions;
/*     */   }
/*     */ 
/*     */   private boolean addNow(T session)
/*     */   {
/* 424 */     boolean registered = false;
/* 425 */     boolean notified = false;
/*     */     try {
/* 427 */       init(session);
/* 428 */       registered = true;
/*     */ 
/* 431 */       session.getService().getFilterChainBuilder().buildFilterChain(session.getFilterChain());
/*     */ 
/* 436 */       ((AbstractIoService)session.getService()).getListeners().fireSessionCreated(session);
/* 437 */       notified = true;
/*     */     } catch (Throwable e) {
/* 439 */       if (notified)
/*     */       {
/* 442 */         scheduleRemove(session);
/* 443 */         IoFilterChain filterChain = session.getFilterChain();
/* 444 */         filterChain.fireExceptionCaught(e);
/* 445 */         wakeup();
/*     */       } else {
/* 447 */         ExceptionMonitor.getInstance().exceptionCaught(e);
/*     */         try {
/* 449 */           destroy(session);
/*     */         } catch (Exception e1) {
/* 451 */           ExceptionMonitor.getInstance().exceptionCaught(e1);
/*     */         } finally {
/* 453 */           registered = false;
/*     */         }
/*     */       }
/*     */     }
/* 457 */     return registered;
/*     */   }
/*     */ 
/*     */   private int remove() {
/* 461 */     int removedSessions = 0;
/*     */     while (true) {
/* 463 */       AbstractIoSession session = (AbstractIoSession)this.removingSessions.poll();
/*     */ 
/* 465 */       if (session == null)
/*     */       {
/*     */         break;
/*     */       }
/* 469 */       SessionState state = state(session);
/* 470 */       switch (1.$SwitchMap$org$apache$mina$core$polling$AbstractPollingIoProcessor$SessionState[state.ordinal()]) {
/*     */       case 1:
/* 472 */         if (!removeNow(session)) break;
/* 473 */         removedSessions++; break;
/*     */       case 2:
/* 478 */         break;
/*     */       case 3:
/* 482 */         scheduleRemove(session);
/* 483 */         return removedSessions;
/*     */       default:
/* 485 */         throw new IllegalStateException(String.valueOf(state));
/*     */       }
/*     */     }
/*     */ 
/* 489 */     return removedSessions;
/*     */   }
/*     */ 
/*     */   private boolean removeNow(T session) {
/* 493 */     clearWriteRequestQueue(session);
/*     */     try
/*     */     {
/* 496 */       destroy(session);
/* 497 */       int i = 1;
/*     */       return i;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 499 */       IoFilterChain filterChain = session.getFilterChain();
/* 500 */       filterChain.fireExceptionCaught(e);
/*     */     } finally {
/* 502 */       clearWriteRequestQueue(session);
/* 503 */       ((AbstractIoService)session.getService()).getListeners().fireSessionDestroyed(session);
/*     */     }
/* 505 */     return false;
/*     */   }
/*     */ 
/*     */   private void clearWriteRequestQueue(T session) {
/* 509 */     WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
/*     */ 
/* 512 */     List failedRequests = new ArrayList();
/*     */     WriteRequest req;
/* 514 */     if ((req = writeRequestQueue.poll(session)) != null) {
/* 515 */       Object m = req.getMessage();
/* 516 */       if ((m instanceof IoBuffer)) {
/* 517 */         IoBuffer buf = (IoBuffer)req.getMessage();
/*     */ 
/* 521 */         if (buf.hasRemaining()) {
/* 522 */           buf.reset();
/* 523 */           failedRequests.add(req);
/*     */         } else {
/* 525 */           IoFilterChain filterChain = session.getFilterChain();
/* 526 */           filterChain.fireMessageSent(req);
/*     */         }
/*     */       } else {
/* 529 */         failedRequests.add(req);
/*     */       }
/*     */ 
/* 533 */       while ((req = writeRequestQueue.poll(session)) != null) {
/* 534 */         failedRequests.add(req);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 539 */     if (!failedRequests.isEmpty()) {
/* 540 */       WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);
/* 541 */       for (WriteRequest r : failedRequests) {
/* 542 */         session.decreaseScheduledBytesAndMessages(r);
/* 543 */         r.getFuture().setException(cause);
/*     */       }
/* 545 */       IoFilterChain filterChain = session.getFilterChain();
/* 546 */       filterChain.fireExceptionCaught(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void process() throws Exception {
/* 551 */     for (Iterator i = selectedSessions(); i.hasNext(); ) {
/* 552 */       AbstractIoSession session = (AbstractIoSession)i.next();
/* 553 */       process(session);
/* 554 */       i.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void process(T session)
/*     */   {
/* 563 */     if ((isReadable(session)) && (!session.isReadSuspended())) {
/* 564 */       read(session);
/*     */     }
/*     */ 
/* 568 */     if ((isWritable(session)) && (!session.isWriteSuspended()))
/* 569 */       scheduleFlush(session);
/*     */   }
/*     */ 
/*     */   private void read(T session)
/*     */   {
/* 574 */     IoSessionConfig config = session.getConfig();
/* 575 */     IoBuffer buf = IoBuffer.allocate(config.getReadBufferSize());
/*     */ 
/* 577 */     boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
/*     */     try
/*     */     {
/* 581 */       int readBytes = 0;
/*     */       int ret;
/*     */       try {
/* 585 */         if (hasFragmentation)
/*     */         {
/*     */           do
/*     */           {
/*     */             int ret;
/* 586 */             if ((ret = read(session, buf)) <= 0) break;
/* 587 */             readBytes += ret;
/* 588 */           }while (buf.hasRemaining());
/*     */         }
/*     */         else
/*     */         {
/* 593 */           ret = read(session, buf);
/* 594 */           if (ret > 0)
/* 595 */             readBytes = ret;
/*     */         }
/*     */       }
/*     */       finally {
/* 599 */         buf.flip();
/*     */       }
/*     */ 
/* 602 */       if (readBytes > 0) {
/* 603 */         IoFilterChain filterChain = session.getFilterChain();
/* 604 */         filterChain.fireMessageReceived(buf);
/* 605 */         buf = null;
/*     */ 
/* 607 */         if (hasFragmentation) {
/* 608 */           if (readBytes << 1 < config.getReadBufferSize())
/* 609 */             session.decreaseReadBufferSize();
/* 610 */           else if (readBytes == config.getReadBufferSize()) {
/* 611 */             session.increaseReadBufferSize();
/*     */           }
/*     */         }
/*     */       }
/* 615 */       if (ret < 0)
/* 616 */         scheduleRemove(session);
/*     */     }
/*     */     catch (Throwable e) {
/* 619 */       if ((e instanceof IOException)) {
/* 620 */         scheduleRemove(session);
/*     */       }
/* 622 */       IoFilterChain filterChain = session.getFilterChain();
/* 623 */       filterChain.fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notifyIdleSessions(long currentTime) throws Exception
/*     */   {
/* 629 */     if (currentTime - this.lastIdleCheckTime >= 1000L) {
/* 630 */       this.lastIdleCheckTime = currentTime;
/* 631 */       AbstractIoSession.notifyIdleness(allSessions(), currentTime);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void flush(long currentTime) {
/* 636 */     AbstractIoSession firstSession = (AbstractIoSession)this.flushingSessions.peek();
/* 637 */     if (firstSession == null) {
/* 638 */       return;
/*     */     }
/*     */ 
/* 641 */     AbstractIoSession session = (AbstractIoSession)this.flushingSessions.poll();
/*     */     while (true) {
/* 643 */       session.setScheduledForFlush(false);
/* 644 */       SessionState state = state(session);
/*     */ 
/* 646 */       switch (1.$SwitchMap$org$apache$mina$core$polling$AbstractPollingIoProcessor$SessionState[state.ordinal()]) {
/*     */       case 1:
/*     */         try {
/* 649 */           boolean flushedAll = flushNow(session, currentTime);
/* 650 */           if ((flushedAll) && (!session.getWriteRequestQueue().isEmpty(session)) && (!session.isScheduledForFlush()))
/*     */           {
/* 652 */             scheduleFlush(session);
/*     */           }
/*     */         } catch (Exception e) {
/* 655 */           scheduleRemove(session);
/* 656 */           IoFilterChain filterChain = session.getFilterChain();
/* 657 */           filterChain.fireExceptionCaught(e);
/*     */         }
/*     */ 
/*     */       case 2:
/* 662 */         break;
/*     */       case 3:
/* 666 */         scheduleFlush(session);
/* 667 */         return;
/*     */       default:
/* 669 */         throw new IllegalStateException(String.valueOf(state));
/*     */       }
/*     */ 
/* 672 */       session = (AbstractIoSession)this.flushingSessions.peek();
/* 673 */       if ((session == null) || (session == firstSession)) {
/*     */         break;
/*     */       }
/* 676 */       session = (AbstractIoSession)this.flushingSessions.poll();
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean flushNow(T session, long currentTime) {
/* 681 */     if (!session.isConnected()) {
/* 682 */       scheduleRemove(session);
/* 683 */       return false;
/*     */     }
/*     */ 
/* 686 */     boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
/*     */ 
/* 689 */     WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
/*     */ 
/* 694 */     int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
/*     */ 
/* 696 */     int writtenBytes = 0;
/*     */     try
/*     */     {
/* 699 */       setInterestedInWrite(session, false);
/*     */       do
/*     */       {
/* 702 */         WriteRequest req = session.getCurrentWriteRequest();
/* 703 */         if (req == null) {
/* 704 */           req = writeRequestQueue.poll(session);
/* 705 */           if (req == null) {
/*     */             break;
/*     */           }
/* 708 */           session.setCurrentWriteRequest(req);
/*     */         }
/*     */ 
/* 711 */         int localWrittenBytes = 0;
/* 712 */         Object message = req.getMessage();
/* 713 */         if ((message instanceof IoBuffer)) {
/* 714 */           localWrittenBytes = writeBuffer(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
/*     */ 
/* 718 */           if ((localWrittenBytes > 0) && (((IoBuffer)message).hasRemaining()))
/*     */           {
/* 720 */             writtenBytes += localWrittenBytes;
/* 721 */             setInterestedInWrite(session, true);
/* 722 */             return false;
/*     */           }
/* 724 */         } else if ((message instanceof FileRegion)) {
/* 725 */           localWrittenBytes = writeFile(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
/*     */ 
/* 733 */           if ((localWrittenBytes > 0) && (((FileRegion)message).getRemainingBytes() > 0L)) {
/* 734 */             writtenBytes += localWrittenBytes;
/* 735 */             setInterestedInWrite(session, true);
/* 736 */             return false;
/*     */           }
/*     */         } else {
/* 739 */           throw new IllegalStateException("Don't know how to handle message of type '" + message.getClass().getName() + "'.  Are you missing a protocol encoder?");
/*     */         }
/*     */ 
/* 742 */         if (localWrittenBytes == 0)
/*     */         {
/* 744 */           setInterestedInWrite(session, true);
/* 745 */           return false;
/*     */         }
/*     */ 
/* 748 */         writtenBytes += localWrittenBytes;
/*     */ 
/* 750 */         if (writtenBytes < maxWrittenBytes)
/*     */           continue;
/* 752 */         scheduleFlush(session);
/* 753 */         return false;
/*     */       }
/* 755 */       while (writtenBytes < maxWrittenBytes);
/*     */     } catch (Exception e) {
/* 757 */       IoFilterChain filterChain = session.getFilterChain();
/* 758 */       filterChain.fireExceptionCaught(e);
/* 759 */       return false;
/*     */     }
/*     */ 
/* 762 */     return true;
/*     */   }
/*     */ 
/*     */   private int writeBuffer(T session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception
/*     */   {
/* 767 */     IoBuffer buf = (IoBuffer)req.getMessage();
/* 768 */     int localWrittenBytes = 0;
/* 769 */     if (buf.hasRemaining())
/*     */     {
/*     */       int length;
/*     */       int length;
/* 771 */       if (hasFragmentation)
/* 772 */         length = Math.min(buf.remaining(), maxLength);
/*     */       else {
/* 774 */         length = buf.remaining();
/*     */       }
/* 776 */       for (int i = 256; i > 0; i--) {
/* 777 */         localWrittenBytes = write(session, buf, length);
/* 778 */         if (localWrittenBytes != 0)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 784 */     session.increaseWrittenBytes(localWrittenBytes, currentTime);
/*     */ 
/* 786 */     if ((!buf.hasRemaining()) || ((!hasFragmentation) && (localWrittenBytes != 0)))
/*     */     {
/* 789 */       buf.reset();
/* 790 */       fireMessageSent(session, req);
/*     */     }
/* 792 */     return localWrittenBytes;
/*     */   }
/*     */ 
/*     */   private int writeFile(T session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime)
/*     */     throws Exception
/*     */   {
/* 798 */     FileRegion region = (FileRegion)req.getMessage();
/*     */     int localWrittenBytes;
/* 799 */     if (region.getRemainingBytes() > 0L)
/*     */     {
/*     */       int length;
/*     */       int length;
/* 801 */       if (hasFragmentation)
/* 802 */         length = (int)Math.min(region.getRemainingBytes(), maxLength);
/*     */       else {
/* 804 */         length = (int)Math.min(2147483647L, region.getRemainingBytes());
/*     */       }
/* 806 */       int localWrittenBytes = transferFile(session, region, length);
/* 807 */       region.update(localWrittenBytes);
/*     */     } else {
/* 809 */       localWrittenBytes = 0;
/*     */     }
/*     */ 
/* 812 */     session.increaseWrittenBytes(localWrittenBytes, currentTime);
/*     */ 
/* 814 */     if ((region.getRemainingBytes() <= 0L) || ((!hasFragmentation) && (localWrittenBytes != 0)))
/*     */     {
/* 816 */       fireMessageSent(session, req);
/*     */     }
/*     */ 
/* 819 */     return localWrittenBytes;
/*     */   }
/*     */ 
/*     */   private void fireMessageSent(T session, WriteRequest req) {
/* 823 */     session.setCurrentWriteRequest(null);
/* 824 */     IoFilterChain filterChain = session.getFilterChain();
/* 825 */     filterChain.fireMessageSent(req);
/*     */   }
/*     */ 
/*     */   private void updateTrafficMask() {
/*     */     while (true) {
/* 830 */       AbstractIoSession session = (AbstractIoSession)this.trafficControllingSessions.poll();
/*     */ 
/* 832 */       if (session == null)
/*     */       {
/*     */         break;
/*     */       }
/* 836 */       SessionState state = state(session);
/* 837 */       switch (1.$SwitchMap$org$apache$mina$core$polling$AbstractPollingIoProcessor$SessionState[state.ordinal()]) {
/*     */       case 1:
/* 839 */         updateTrafficControl(session);
/* 840 */         break;
/*     */       case 2:
/* 842 */         break;
/*     */       case 3:
/* 847 */         scheduleTrafficControl(session);
/* 848 */         return;
/*     */       default:
/* 850 */         throw new IllegalStateException(String.valueOf(state));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateTrafficControl(T session) {
/*     */     try {
/* 857 */       setInterestedInRead(session, !session.isReadSuspended());
/*     */     } catch (Exception e) {
/* 859 */       IoFilterChain filterChain = session.getFilterChain();
/* 860 */       filterChain.fireExceptionCaught(e);
/*     */     }
/*     */     try {
/* 863 */       setInterestedInWrite(session, (!session.getWriteRequestQueue().isEmpty(session)) && (!session.isWriteSuspended()));
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 868 */       IoFilterChain filterChain = session.getFilterChain();
/* 869 */       filterChain.fireExceptionCaught(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static enum SessionState
/*     */   {
/* 943 */     OPEN, 
/* 944 */     CLOSED, 
/* 945 */     PREPARING;
/*     */   }
/*     */ 
/*     */   private class Processor
/*     */     implements Runnable
/*     */   {
/*     */     private Processor()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 875 */       int nSessions = 0;
/* 876 */       AbstractPollingIoProcessor.access$102(AbstractPollingIoProcessor.this, System.currentTimeMillis());
/*     */       while (true)
/*     */       {
/*     */         try
/*     */         {
/* 884 */           int selected = AbstractPollingIoProcessor.this.select(1000L);
/*     */ 
/* 886 */           nSessions += AbstractPollingIoProcessor.this.handleNewSessions();
/* 887 */           AbstractPollingIoProcessor.this.updateTrafficMask();
/*     */ 
/* 891 */           if (selected > 0) {
/* 892 */             AbstractPollingIoProcessor.this.process();
/*     */           }
/*     */ 
/* 895 */           long currentTime = System.currentTimeMillis();
/* 896 */           AbstractPollingIoProcessor.this.flush(currentTime);
/* 897 */           nSessions -= AbstractPollingIoProcessor.this.remove();
/* 898 */           AbstractPollingIoProcessor.this.notifyIdleSessions(currentTime);
/*     */ 
/* 900 */           if (nSessions == 0) {
/* 901 */             synchronized (AbstractPollingIoProcessor.this.lock) {
/* 902 */               if ((AbstractPollingIoProcessor.this.newSessions.isEmpty()) && (AbstractPollingIoProcessor.this.isSelectorEmpty())) {
/* 903 */                 AbstractPollingIoProcessor.access$1002(AbstractPollingIoProcessor.this, null);
/* 904 */                 break;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 911 */           if (AbstractPollingIoProcessor.this.isDisposing()) {
/* 912 */             Iterator i = AbstractPollingIoProcessor.this.allSessions(); if (i.hasNext()) {
/* 913 */               AbstractPollingIoProcessor.this.scheduleRemove((AbstractIoSession)i.next()); continue;
/*     */             }
/* 915 */             AbstractPollingIoProcessor.this.wakeup();
/*     */           }
/*     */ 
/* 925 */           continue;
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 918 */           ExceptionMonitor.getInstance().exceptionCaught(t);
/*     */           try
/*     */           {
/* 921 */             Thread.sleep(1000L);
/*     */           } catch (InterruptedException e1) {
/* 923 */             ExceptionMonitor.getInstance().exceptionCaught(e1);
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 929 */         synchronized (AbstractPollingIoProcessor.this.disposalLock) {
/* 930 */           if (AbstractPollingIoProcessor.this.isDisposing())
/* 931 */             AbstractPollingIoProcessor.this.dispose0();
/*     */         }
/*     */       }
/*     */       catch (Throwable t) {
/* 935 */         ExceptionMonitor.getInstance().exceptionCaught(t);
/*     */       } finally {
/* 937 */         AbstractPollingIoProcessor.this.disposalFuture.setValue(Boolean.valueOf(true));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.polling.AbstractPollingIoProcessor
 * JD-Core Version:    0.6.0
 */
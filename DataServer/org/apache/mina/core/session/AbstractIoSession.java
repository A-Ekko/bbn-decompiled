/*      */ package org.apache.mina.core.session;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.net.SocketAddress;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.util.Iterator;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicBoolean;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import org.apache.mina.core.buffer.IoBuffer;
/*      */ import org.apache.mina.core.file.DefaultFileRegion;
/*      */ import org.apache.mina.core.filterchain.IoFilterChain;
/*      */ import org.apache.mina.core.future.CloseFuture;
/*      */ import org.apache.mina.core.future.DefaultCloseFuture;
/*      */ import org.apache.mina.core.future.DefaultReadFuture;
/*      */ import org.apache.mina.core.future.DefaultWriteFuture;
/*      */ import org.apache.mina.core.future.IoFutureListener;
/*      */ import org.apache.mina.core.future.ReadFuture;
/*      */ import org.apache.mina.core.future.WriteFuture;
/*      */ import org.apache.mina.core.service.AbstractIoService;
/*      */ import org.apache.mina.core.service.IoAcceptor;
/*      */ import org.apache.mina.core.service.IoProcessor;
/*      */ import org.apache.mina.core.service.IoService;
/*      */ import org.apache.mina.core.service.IoServiceStatistics;
/*      */ import org.apache.mina.core.service.TransportMetadata;
/*      */ import org.apache.mina.core.write.DefaultWriteRequest;
/*      */ import org.apache.mina.core.write.WriteException;
/*      */ import org.apache.mina.core.write.WriteRequest;
/*      */ import org.apache.mina.core.write.WriteRequestQueue;
/*      */ import org.apache.mina.core.write.WriteTimeoutException;
/*      */ import org.apache.mina.core.write.WriteToClosedSessionException;
/*      */ import org.apache.mina.util.CircularQueue;
/*      */ import org.apache.mina.util.ExceptionMonitor;
/*      */ 
/*      */ public abstract class AbstractIoSession
/*      */   implements IoSession
/*      */ {
/*   67 */   private static final AttributeKey READY_READ_FUTURES_KEY = new AttributeKey(AbstractIoSession.class, "readyReadFutures");
/*      */ 
/*   70 */   private static final AttributeKey WAITING_READ_FUTURES_KEY = new AttributeKey(AbstractIoSession.class, "waitingReadFutures");
/*      */ 
/*   73 */   private static final IoFutureListener<CloseFuture> SCHEDULED_COUNTER_RESETTER = new IoFutureListener()
/*      */   {
/*      */     public void operationComplete(CloseFuture future) {
/*   76 */       AbstractIoSession s = (AbstractIoSession)future.getSession();
/*   77 */       s.scheduledWriteBytes.set(0);
/*   78 */       s.scheduledWriteMessages.set(0);
/*   79 */       AbstractIoSession.access$202(s, 0.0D);
/*   80 */       AbstractIoSession.access$302(s, 0.0D);
/*   81 */       AbstractIoSession.access$402(s, 0.0D);
/*   82 */       AbstractIoSession.access$502(s, 0.0D);
/*      */     }
/*   73 */   };
/*      */ 
/*   90 */   private static final WriteRequest CLOSE_REQUEST = new DefaultWriteRequest(new Object());
/*      */ 
/*   93 */   private final Object lock = new Object();
/*      */   private IoSessionAttributeMap attributes;
/*      */   private WriteRequestQueue writeRequestQueue;
/*      */   private WriteRequest currentWriteRequest;
/*      */   private final long creationTime;
/*  103 */   private static AtomicLong idGenerator = new AtomicLong(0L);
/*      */   private long sessionId;
/*  111 */   private final CloseFuture closeFuture = new DefaultCloseFuture(this);
/*      */   private volatile boolean closing;
/*  116 */   private boolean readSuspended = false;
/*  117 */   private boolean writeSuspended = false;
/*      */ 
/*  120 */   private final AtomicBoolean scheduledForFlush = new AtomicBoolean();
/*  121 */   private final AtomicInteger scheduledWriteBytes = new AtomicInteger();
/*  122 */   private final AtomicInteger scheduledWriteMessages = new AtomicInteger();
/*      */   private long readBytes;
/*      */   private long writtenBytes;
/*      */   private long readMessages;
/*      */   private long writtenMessages;
/*      */   private long lastReadTime;
/*      */   private long lastWriteTime;
/*      */   private long lastThroughputCalculationTime;
/*      */   private long lastReadBytes;
/*      */   private long lastWrittenBytes;
/*      */   private long lastReadMessages;
/*      */   private long lastWrittenMessages;
/*      */   private double readBytesThroughput;
/*      */   private double writtenBytesThroughput;
/*      */   private double readMessagesThroughput;
/*      */   private double writtenMessagesThroughput;
/*      */   private int idleCountForBoth;
/*      */   private int idleCountForRead;
/*      */   private int idleCountForWrite;
/*      */   private long lastIdleTimeForBoth;
/*      */   private long lastIdleTimeForRead;
/*      */   private long lastIdleTimeForWrite;
/*  149 */   private boolean deferDecreaseReadBuffer = true;
/*      */ 
/*      */   protected AbstractIoSession()
/*      */   {
/*  156 */     long currentTime = System.currentTimeMillis();
/*  157 */     this.creationTime = currentTime;
/*  158 */     this.lastThroughputCalculationTime = currentTime;
/*  159 */     this.lastReadTime = currentTime;
/*  160 */     this.lastWriteTime = currentTime;
/*  161 */     this.lastIdleTimeForBoth = currentTime;
/*  162 */     this.lastIdleTimeForRead = currentTime;
/*  163 */     this.lastIdleTimeForWrite = currentTime;
/*      */ 
/*  166 */     this.closeFuture.addListener(SCHEDULED_COUNTER_RESETTER);
/*      */ 
/*  169 */     this.sessionId = idGenerator.incrementAndGet();
/*      */   }
/*      */ 
/*      */   public final long getId()
/*      */   {
/*  179 */     return this.sessionId;
/*      */   }
/*      */ 
/*      */   public abstract IoProcessor getProcessor();
/*      */ 
/*      */   public final boolean isConnected()
/*      */   {
/*  191 */     return !this.closeFuture.isClosed();
/*      */   }
/*      */ 
/*      */   public final boolean isClosing()
/*      */   {
/*  198 */     return (this.closing) || (this.closeFuture.isClosed());
/*      */   }
/*      */ 
/*      */   public final CloseFuture getCloseFuture()
/*      */   {
/*  205 */     return this.closeFuture;
/*      */   }
/*      */ 
/*      */   public final boolean isScheduledForFlush()
/*      */   {
/*  212 */     return this.scheduledForFlush.get();
/*      */   }
/*      */ 
/*      */   public final boolean setScheduledForFlush(boolean flag)
/*      */   {
/*  219 */     if (flag) {
/*  220 */       return this.scheduledForFlush.compareAndSet(false, true);
/*      */     }
/*  222 */     this.scheduledForFlush.set(false);
/*  223 */     return true;
/*      */   }
/*      */ 
/*      */   public final CloseFuture close(boolean rightNow)
/*      */   {
/*  231 */     if (rightNow) {
/*  232 */       return close();
/*      */     }
/*  234 */     return closeOnFlush();
/*      */   }
/*      */ 
/*      */   public final CloseFuture close()
/*      */   {
/*  242 */     synchronized (this.lock) {
/*  243 */       if (isClosing()) {
/*  244 */         return this.closeFuture;
/*      */       }
/*  246 */       this.closing = true;
/*      */     }
/*      */ 
/*  250 */     getFilterChain().fireFilterClose();
/*  251 */     return this.closeFuture;
/*      */   }
/*      */ 
/*      */   private final CloseFuture closeOnFlush() {
/*  255 */     getWriteRequestQueue().offer(this, CLOSE_REQUEST);
/*  256 */     getProcessor().flush(this);
/*  257 */     return this.closeFuture;
/*      */   }
/*      */ 
/*      */   public final ReadFuture read()
/*      */   {
/*  264 */     if (!getConfig().isUseReadOperation()) {
/*  265 */       throw new IllegalStateException("useReadOperation is not enabled.");
/*      */     }
/*      */ 
/*  268 */     Queue readyReadFutures = getReadyReadFutures();
/*      */     ReadFuture future;
/*  270 */     synchronized (readyReadFutures) {
/*  271 */       future = (ReadFuture)readyReadFutures.poll();
/*  272 */       if (future != null) {
/*  273 */         if (future.isClosed())
/*      */         {
/*  275 */           readyReadFutures.offer(future);
/*      */         }
/*      */       } else {
/*  278 */         future = new DefaultReadFuture(this);
/*  279 */         getWaitingReadFutures().offer(future);
/*      */       }
/*      */     }
/*      */ 
/*  283 */     return future;
/*      */   }
/*      */ 
/*      */   public final void offerReadFuture(Object message)
/*      */   {
/*  290 */     newReadFuture().setRead(message);
/*      */   }
/*      */ 
/*      */   public final void offerFailedReadFuture(Throwable exception)
/*      */   {
/*  297 */     newReadFuture().setException(exception);
/*      */   }
/*      */ 
/*      */   public final void offerClosedReadFuture()
/*      */   {
/*  304 */     Queue readyReadFutures = getReadyReadFutures();
/*  305 */     synchronized (readyReadFutures) {
/*  306 */       newReadFuture().setClosed();
/*      */     }
/*      */   }
/*      */ 
/*      */   private ReadFuture newReadFuture()
/*      */   {
/*  314 */     Queue readyReadFutures = getReadyReadFutures();
/*  315 */     Queue waitingReadFutures = getWaitingReadFutures();
/*      */     ReadFuture future;
/*  317 */     synchronized (readyReadFutures) {
/*  318 */       future = (ReadFuture)waitingReadFutures.poll();
/*  319 */       if (future == null) {
/*  320 */         future = new DefaultReadFuture(this);
/*  321 */         readyReadFutures.offer(future);
/*      */       }
/*      */     }
/*  324 */     return future;
/*      */   }
/*      */ 
/*      */   private Queue<ReadFuture> getReadyReadFutures()
/*      */   {
/*  331 */     Queue readyReadFutures = (Queue)getAttribute(READY_READ_FUTURES_KEY);
/*      */ 
/*  333 */     if (readyReadFutures == null) {
/*  334 */       readyReadFutures = new CircularQueue();
/*      */ 
/*  336 */       Queue oldReadyReadFutures = (Queue)setAttributeIfAbsent(READY_READ_FUTURES_KEY, readyReadFutures);
/*      */ 
/*  339 */       if (oldReadyReadFutures != null) {
/*  340 */         readyReadFutures = oldReadyReadFutures;
/*      */       }
/*      */     }
/*  343 */     return readyReadFutures;
/*      */   }
/*      */ 
/*      */   private Queue<ReadFuture> getWaitingReadFutures()
/*      */   {
/*  350 */     Queue waitingReadyReadFutures = (Queue)getAttribute(WAITING_READ_FUTURES_KEY);
/*      */ 
/*  352 */     if (waitingReadyReadFutures == null) {
/*  353 */       waitingReadyReadFutures = new CircularQueue();
/*      */ 
/*  355 */       Queue oldWaitingReadyReadFutures = (Queue)setAttributeIfAbsent(WAITING_READ_FUTURES_KEY, waitingReadyReadFutures);
/*      */ 
/*  358 */       if (oldWaitingReadyReadFutures != null) {
/*  359 */         waitingReadyReadFutures = oldWaitingReadyReadFutures;
/*      */       }
/*      */     }
/*  362 */     return waitingReadyReadFutures;
/*      */   }
/*      */ 
/*      */   public WriteFuture write(Object message)
/*      */   {
/*  369 */     return write(message, null);
/*      */   }
/*      */ 
/*      */   public WriteFuture write(Object message, SocketAddress remoteAddress)
/*      */   {
/*  376 */     if (message == null) {
/*  377 */       throw new NullPointerException("message");
/*      */     }
/*      */ 
/*  382 */     if ((!getTransportMetadata().isConnectionless()) && (remoteAddress != null))
/*      */     {
/*  384 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*  391 */     if ((isClosing()) || (!isConnected())) {
/*  392 */       WriteFuture future = new DefaultWriteFuture(this);
/*  393 */       WriteRequest request = new DefaultWriteRequest(message, future, remoteAddress);
/*  394 */       WriteException writeException = new WriteToClosedSessionException(request);
/*  395 */       future.setException(writeException);
/*  396 */       return future;
/*      */     }
/*      */ 
/*  399 */     FileChannel openedFileChannel = null;
/*      */     try
/*      */     {
/*  404 */       if (((message instanceof IoBuffer)) && (!((IoBuffer)message).hasRemaining()))
/*      */       {
/*  407 */         throw new IllegalArgumentException("message is empty. Forgot to call flip()?");
/*      */       }
/*  409 */       if ((message instanceof FileChannel)) {
/*  410 */         FileChannel fileChannel = (FileChannel)message;
/*  411 */         message = new DefaultFileRegion(fileChannel, 0L, fileChannel.size());
/*  412 */       } else if ((message instanceof File)) {
/*  413 */         File file = (File)message;
/*  414 */         openedFileChannel = new FileInputStream(file).getChannel();
/*  415 */         message = new DefaultFileRegion(openedFileChannel, 0L, openedFileChannel.size());
/*      */       }
/*      */     } catch (IOException e) {
/*  418 */       ExceptionMonitor.getInstance().exceptionCaught(e);
/*  419 */       return DefaultWriteFuture.newNotWrittenFuture(this, e);
/*      */     }
/*      */ 
/*  423 */     WriteFuture writeFuture = new DefaultWriteFuture(this);
/*  424 */     WriteRequest writeRequest = new DefaultWriteRequest(message, writeFuture, remoteAddress);
/*      */ 
/*  427 */     IoFilterChain filterChain = getFilterChain();
/*  428 */     filterChain.fireFilterWrite(writeRequest);
/*      */ 
/*  432 */     if (openedFileChannel != null)
/*      */     {
/*  434 */       FileChannel finalChannel = openedFileChannel;
/*  435 */       writeFuture.addListener(new IoFutureListener(finalChannel) {
/*      */         public void operationComplete(WriteFuture future) {
/*      */           try {
/*  438 */             this.val$finalChannel.close();
/*      */           } catch (IOException e) {
/*  440 */             ExceptionMonitor.getInstance().exceptionCaught(e);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*  447 */     return writeFuture;
/*      */   }
/*      */ 
/*      */   public final Object getAttachment()
/*      */   {
/*  454 */     return getAttribute("");
/*      */   }
/*      */ 
/*      */   public final Object setAttachment(Object attachment)
/*      */   {
/*  461 */     return setAttribute("", attachment);
/*      */   }
/*      */ 
/*      */   public final Object getAttribute(Object key)
/*      */   {
/*  468 */     return getAttribute(key, null);
/*      */   }
/*      */ 
/*      */   public final Object getAttribute(Object key, Object defaultValue)
/*      */   {
/*  475 */     return this.attributes.getAttribute(this, key, defaultValue);
/*      */   }
/*      */ 
/*      */   public final Object setAttribute(Object key, Object value)
/*      */   {
/*  482 */     return this.attributes.setAttribute(this, key, value);
/*      */   }
/*      */ 
/*      */   public final Object setAttribute(Object key)
/*      */   {
/*  489 */     return setAttribute(key, Boolean.TRUE);
/*      */   }
/*      */ 
/*      */   public final Object setAttributeIfAbsent(Object key, Object value)
/*      */   {
/*  496 */     return this.attributes.setAttributeIfAbsent(this, key, value);
/*      */   }
/*      */ 
/*      */   public final Object setAttributeIfAbsent(Object key)
/*      */   {
/*  503 */     return setAttributeIfAbsent(key, Boolean.TRUE);
/*      */   }
/*      */ 
/*      */   public final Object removeAttribute(Object key)
/*      */   {
/*  510 */     return this.attributes.removeAttribute(this, key);
/*      */   }
/*      */ 
/*      */   public final boolean removeAttribute(Object key, Object value)
/*      */   {
/*  517 */     return this.attributes.removeAttribute(this, key, value);
/*      */   }
/*      */ 
/*      */   public final boolean replaceAttribute(Object key, Object oldValue, Object newValue)
/*      */   {
/*  524 */     return this.attributes.replaceAttribute(this, key, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public final boolean containsAttribute(Object key)
/*      */   {
/*  531 */     return this.attributes.containsAttribute(this, key);
/*      */   }
/*      */ 
/*      */   public final Set<Object> getAttributeKeys()
/*      */   {
/*  538 */     return this.attributes.getAttributeKeys(this);
/*      */   }
/*      */ 
/*      */   public final IoSessionAttributeMap getAttributeMap()
/*      */   {
/*  545 */     return this.attributes;
/*      */   }
/*      */ 
/*      */   public final void setAttributeMap(IoSessionAttributeMap attributes)
/*      */   {
/*  552 */     this.attributes = attributes;
/*      */   }
/*      */ 
/*      */   public final void setWriteRequestQueue(WriteRequestQueue writeRequestQueue)
/*      */   {
/*  561 */     this.writeRequestQueue = new CloseAwareWriteQueue(writeRequestQueue);
/*      */   }
/*      */ 
/*      */   public final void suspendRead()
/*      */   {
/*  570 */     this.readSuspended = true;
/*  571 */     if ((isClosing()) || (!isConnected())) {
/*  572 */       return;
/*      */     }
/*  574 */     getProcessor().updateTrafficControl(this);
/*      */   }
/*      */ 
/*      */   public final void suspendWrite()
/*      */   {
/*  581 */     this.writeSuspended = true;
/*  582 */     if ((isClosing()) || (!isConnected())) {
/*  583 */       return;
/*      */     }
/*  585 */     getProcessor().updateTrafficControl(this);
/*      */   }
/*      */ 
/*      */   public final void resumeRead()
/*      */   {
/*  593 */     this.readSuspended = false;
/*  594 */     if ((isClosing()) || (!isConnected())) {
/*  595 */       return;
/*      */     }
/*  597 */     getProcessor().updateTrafficControl(this);
/*      */   }
/*      */ 
/*      */   public final void resumeWrite()
/*      */   {
/*  605 */     this.writeSuspended = false;
/*  606 */     if ((isClosing()) || (!isConnected())) {
/*  607 */       return;
/*      */     }
/*  609 */     getProcessor().updateTrafficControl(this);
/*      */   }
/*      */ 
/*      */   public boolean isReadSuspended()
/*      */   {
/*  616 */     return this.readSuspended;
/*      */   }
/*      */ 
/*      */   public boolean isWriteSuspended()
/*      */   {
/*  623 */     return this.writeSuspended;
/*      */   }
/*      */ 
/*      */   public final long getReadBytes()
/*      */   {
/*  630 */     return this.readBytes;
/*      */   }
/*      */ 
/*      */   public final long getWrittenBytes()
/*      */   {
/*  637 */     return this.writtenBytes;
/*      */   }
/*      */ 
/*      */   public final long getReadMessages()
/*      */   {
/*  644 */     return this.readMessages;
/*      */   }
/*      */ 
/*      */   public final long getWrittenMessages()
/*      */   {
/*  651 */     return this.writtenMessages;
/*      */   }
/*      */ 
/*      */   public final double getReadBytesThroughput()
/*      */   {
/*  658 */     return this.readBytesThroughput;
/*      */   }
/*      */ 
/*      */   public final double getWrittenBytesThroughput()
/*      */   {
/*  665 */     return this.writtenBytesThroughput;
/*      */   }
/*      */ 
/*      */   public final double getReadMessagesThroughput()
/*      */   {
/*  672 */     return this.readMessagesThroughput;
/*      */   }
/*      */ 
/*      */   public final double getWrittenMessagesThroughput()
/*      */   {
/*  679 */     return this.writtenMessagesThroughput;
/*      */   }
/*      */ 
/*      */   public final void updateThroughput(long currentTime, boolean force)
/*      */   {
/*  686 */     int interval = (int)(currentTime - this.lastThroughputCalculationTime);
/*      */ 
/*  688 */     long minInterval = getConfig().getThroughputCalculationIntervalInMillis();
/*  689 */     if (((minInterval == 0L) || (interval < minInterval)) && 
/*  690 */       (!force)) {
/*  691 */       return;
/*      */     }
/*      */ 
/*  695 */     this.readBytesThroughput = ((this.readBytes - this.lastReadBytes) * 1000.0D / interval);
/*  696 */     this.writtenBytesThroughput = ((this.writtenBytes - this.lastWrittenBytes) * 1000.0D / interval);
/*  697 */     this.readMessagesThroughput = ((this.readMessages - this.lastReadMessages) * 1000.0D / interval);
/*  698 */     this.writtenMessagesThroughput = ((this.writtenMessages - this.lastWrittenMessages) * 1000.0D / interval);
/*      */ 
/*  700 */     this.lastReadBytes = this.readBytes;
/*  701 */     this.lastWrittenBytes = this.writtenBytes;
/*  702 */     this.lastReadMessages = this.readMessages;
/*  703 */     this.lastWrittenMessages = this.writtenMessages;
/*      */ 
/*  705 */     this.lastThroughputCalculationTime = currentTime;
/*      */   }
/*      */ 
/*      */   public final long getScheduledWriteBytes()
/*      */   {
/*  712 */     return this.scheduledWriteBytes.get();
/*      */   }
/*      */ 
/*      */   public final int getScheduledWriteMessages()
/*      */   {
/*  719 */     return this.scheduledWriteMessages.get();
/*      */   }
/*      */ 
/*      */   protected void setScheduledWriteBytes(int byteCount)
/*      */   {
/*  726 */     this.scheduledWriteBytes.set(byteCount);
/*      */   }
/*      */ 
/*      */   protected void setScheduledWriteMessages(int messages)
/*      */   {
/*  733 */     this.scheduledWriteMessages.set(messages);
/*      */   }
/*      */ 
/*      */   public final void increaseReadBytes(long increment, long currentTime)
/*      */   {
/*  740 */     if (increment <= 0L) {
/*  741 */       return;
/*      */     }
/*      */ 
/*  744 */     this.readBytes += increment;
/*  745 */     this.lastReadTime = currentTime;
/*  746 */     this.idleCountForBoth = 0;
/*  747 */     this.idleCountForRead = 0;
/*      */ 
/*  749 */     if ((getService() instanceof AbstractIoService))
/*  750 */       ((AbstractIoService)getService()).getStatistics().increaseReadBytes(increment, currentTime);
/*      */   }
/*      */ 
/*      */   public final void increaseReadMessages(long currentTime)
/*      */   {
/*  758 */     this.readMessages += 1L;
/*  759 */     this.lastReadTime = currentTime;
/*  760 */     this.idleCountForBoth = 0;
/*  761 */     this.idleCountForRead = 0;
/*      */ 
/*  763 */     if ((getService() instanceof AbstractIoService))
/*  764 */       ((AbstractIoService)getService()).getStatistics().increaseReadMessages(currentTime);
/*      */   }
/*      */ 
/*      */   public final void increaseWrittenBytes(int increment, long currentTime)
/*      */   {
/*  772 */     if (increment <= 0) {
/*  773 */       return;
/*      */     }
/*      */ 
/*  776 */     this.writtenBytes += increment;
/*  777 */     this.lastWriteTime = currentTime;
/*  778 */     this.idleCountForBoth = 0;
/*  779 */     this.idleCountForWrite = 0;
/*      */ 
/*  781 */     if ((getService() instanceof AbstractIoService)) {
/*  782 */       ((AbstractIoService)getService()).getStatistics().increaseWrittenBytes(increment, currentTime);
/*      */     }
/*      */ 
/*  785 */     increaseScheduledWriteBytes(-increment);
/*      */   }
/*      */ 
/*      */   public final void increaseWrittenMessages(WriteRequest request, long currentTime)
/*      */   {
/*  793 */     Object message = request.getMessage();
/*  794 */     if ((message instanceof IoBuffer)) {
/*  795 */       IoBuffer b = (IoBuffer)message;
/*  796 */       if (b.hasRemaining()) {
/*  797 */         return;
/*      */       }
/*      */     }
/*      */ 
/*  801 */     this.writtenMessages += 1L;
/*  802 */     this.lastWriteTime = currentTime;
/*  803 */     if ((getService() instanceof AbstractIoService)) {
/*  804 */       ((AbstractIoService)getService()).getStatistics().increaseWrittenMessages(currentTime);
/*      */     }
/*      */ 
/*  807 */     decreaseScheduledWriteMessages();
/*      */   }
/*      */ 
/*      */   public final void increaseScheduledWriteBytes(int increment)
/*      */   {
/*  814 */     this.scheduledWriteBytes.addAndGet(increment);
/*  815 */     if ((getService() instanceof AbstractIoService))
/*  816 */       ((AbstractIoService)getService()).getStatistics().increaseScheduledWriteBytes(increment);
/*      */   }
/*      */ 
/*      */   public final void increaseScheduledWriteMessages()
/*      */   {
/*  824 */     this.scheduledWriteMessages.incrementAndGet();
/*  825 */     if ((getService() instanceof AbstractIoService))
/*  826 */       ((AbstractIoService)getService()).getStatistics().increaseScheduledWriteMessages();
/*      */   }
/*      */ 
/*      */   private void decreaseScheduledWriteMessages()
/*      */   {
/*  834 */     this.scheduledWriteMessages.decrementAndGet();
/*  835 */     if ((getService() instanceof AbstractIoService))
/*  836 */       ((AbstractIoService)getService()).getStatistics().decreaseScheduledWriteMessages();
/*      */   }
/*      */ 
/*      */   public final void decreaseScheduledBytesAndMessages(WriteRequest request)
/*      */   {
/*  844 */     Object message = request.getMessage();
/*  845 */     if ((message instanceof IoBuffer)) {
/*  846 */       IoBuffer b = (IoBuffer)message;
/*  847 */       if (b.hasRemaining())
/*  848 */         increaseScheduledWriteBytes(-((IoBuffer)message).remaining());
/*      */       else
/*  850 */         decreaseScheduledWriteMessages();
/*      */     }
/*      */     else {
/*  853 */       decreaseScheduledWriteMessages();
/*      */     }
/*      */   }
/*      */ 
/*      */   public final WriteRequestQueue getWriteRequestQueue()
/*      */   {
/*  861 */     if (this.writeRequestQueue == null) {
/*  862 */       throw new IllegalStateException();
/*      */     }
/*  864 */     return this.writeRequestQueue;
/*      */   }
/*      */ 
/*      */   public final WriteRequest getCurrentWriteRequest()
/*      */   {
/*  871 */     return this.currentWriteRequest;
/*      */   }
/*      */ 
/*      */   public final Object getCurrentWriteMessage()
/*      */   {
/*  878 */     WriteRequest req = getCurrentWriteRequest();
/*  879 */     if (req == null) {
/*  880 */       return null;
/*      */     }
/*  882 */     return req.getMessage();
/*      */   }
/*      */ 
/*      */   public final void setCurrentWriteRequest(WriteRequest currentWriteRequest)
/*      */   {
/*  889 */     this.currentWriteRequest = currentWriteRequest;
/*      */   }
/*      */ 
/*      */   public final void increaseReadBufferSize()
/*      */   {
/*  896 */     int newReadBufferSize = getConfig().getReadBufferSize() << 1;
/*  897 */     if (newReadBufferSize <= getConfig().getMaxReadBufferSize())
/*  898 */       getConfig().setReadBufferSize(newReadBufferSize);
/*      */     else {
/*  900 */       getConfig().setReadBufferSize(getConfig().getMaxReadBufferSize());
/*      */     }
/*      */ 
/*  903 */     this.deferDecreaseReadBuffer = true;
/*      */   }
/*      */ 
/*      */   public final void decreaseReadBufferSize()
/*      */   {
/*  910 */     if (this.deferDecreaseReadBuffer) {
/*  911 */       this.deferDecreaseReadBuffer = false;
/*  912 */       return;
/*      */     }
/*      */ 
/*  915 */     if (getConfig().getReadBufferSize() > getConfig().getMinReadBufferSize()) {
/*  916 */       getConfig().setReadBufferSize(getConfig().getReadBufferSize() >>> 1);
/*      */     }
/*      */ 
/*  919 */     this.deferDecreaseReadBuffer = true;
/*      */   }
/*      */ 
/*      */   public final long getCreationTime()
/*      */   {
/*  926 */     return this.creationTime;
/*      */   }
/*      */ 
/*      */   public final long getLastIoTime()
/*      */   {
/*  933 */     return Math.max(this.lastReadTime, this.lastWriteTime);
/*      */   }
/*      */ 
/*      */   public final long getLastReadTime()
/*      */   {
/*  940 */     return this.lastReadTime;
/*      */   }
/*      */ 
/*      */   public final long getLastWriteTime()
/*      */   {
/*  947 */     return this.lastWriteTime;
/*      */   }
/*      */ 
/*      */   public final boolean isIdle(IdleStatus status)
/*      */   {
/*  954 */     if (status == IdleStatus.BOTH_IDLE) {
/*  955 */       return this.idleCountForBoth > 0;
/*      */     }
/*      */ 
/*  958 */     if (status == IdleStatus.READER_IDLE) {
/*  959 */       return this.idleCountForRead > 0;
/*      */     }
/*      */ 
/*  962 */     if (status == IdleStatus.WRITER_IDLE) {
/*  963 */       return this.idleCountForWrite > 0;
/*      */     }
/*      */ 
/*  966 */     throw new IllegalArgumentException("Unknown idle status: " + status);
/*      */   }
/*      */ 
/*      */   public final boolean isBothIdle()
/*      */   {
/*  973 */     return isIdle(IdleStatus.BOTH_IDLE);
/*      */   }
/*      */ 
/*      */   public final boolean isReaderIdle()
/*      */   {
/*  980 */     return isIdle(IdleStatus.READER_IDLE);
/*      */   }
/*      */ 
/*      */   public final boolean isWriterIdle()
/*      */   {
/*  987 */     return isIdle(IdleStatus.WRITER_IDLE);
/*      */   }
/*      */ 
/*      */   public final int getIdleCount(IdleStatus status)
/*      */   {
/*  994 */     if (getConfig().getIdleTime(status) == 0) {
/*  995 */       if (status == IdleStatus.BOTH_IDLE) {
/*  996 */         this.idleCountForBoth = 0;
/*      */       }
/*      */ 
/*  999 */       if (status == IdleStatus.READER_IDLE) {
/* 1000 */         this.idleCountForRead = 0;
/*      */       }
/*      */ 
/* 1003 */       if (status == IdleStatus.WRITER_IDLE) {
/* 1004 */         this.idleCountForWrite = 0;
/*      */       }
/*      */     }
/*      */ 
/* 1008 */     if (status == IdleStatus.BOTH_IDLE) {
/* 1009 */       return this.idleCountForBoth;
/*      */     }
/*      */ 
/* 1012 */     if (status == IdleStatus.READER_IDLE) {
/* 1013 */       return this.idleCountForRead;
/*      */     }
/*      */ 
/* 1016 */     if (status == IdleStatus.WRITER_IDLE) {
/* 1017 */       return this.idleCountForWrite;
/*      */     }
/*      */ 
/* 1020 */     throw new IllegalArgumentException("Unknown idle status: " + status);
/*      */   }
/*      */ 
/*      */   public final long getLastIdleTime(IdleStatus status)
/*      */   {
/* 1027 */     if (status == IdleStatus.BOTH_IDLE) {
/* 1028 */       return this.lastIdleTimeForBoth;
/*      */     }
/*      */ 
/* 1031 */     if (status == IdleStatus.READER_IDLE) {
/* 1032 */       return this.lastIdleTimeForRead;
/*      */     }
/*      */ 
/* 1035 */     if (status == IdleStatus.WRITER_IDLE) {
/* 1036 */       return this.lastIdleTimeForWrite;
/*      */     }
/*      */ 
/* 1039 */     throw new IllegalArgumentException("Unknown idle status: " + status);
/*      */   }
/*      */ 
/*      */   public final void increaseIdleCount(IdleStatus status, long currentTime)
/*      */   {
/* 1046 */     if (status == IdleStatus.BOTH_IDLE) {
/* 1047 */       this.idleCountForBoth += 1;
/* 1048 */       this.lastIdleTimeForBoth = currentTime;
/* 1049 */     } else if (status == IdleStatus.READER_IDLE) {
/* 1050 */       this.idleCountForRead += 1;
/* 1051 */       this.lastIdleTimeForRead = currentTime;
/* 1052 */     } else if (status == IdleStatus.WRITER_IDLE) {
/* 1053 */       this.idleCountForWrite += 1;
/* 1054 */       this.lastIdleTimeForWrite = currentTime;
/*      */     } else {
/* 1056 */       throw new IllegalArgumentException("Unknown idle status: " + status);
/*      */     }
/*      */   }
/*      */ 
/*      */   public final int getBothIdleCount()
/*      */   {
/* 1064 */     return getIdleCount(IdleStatus.BOTH_IDLE);
/*      */   }
/*      */ 
/*      */   public final long getLastBothIdleTime()
/*      */   {
/* 1071 */     return getLastIdleTime(IdleStatus.BOTH_IDLE);
/*      */   }
/*      */ 
/*      */   public final long getLastReaderIdleTime()
/*      */   {
/* 1078 */     return getLastIdleTime(IdleStatus.READER_IDLE);
/*      */   }
/*      */ 
/*      */   public final long getLastWriterIdleTime()
/*      */   {
/* 1085 */     return getLastIdleTime(IdleStatus.WRITER_IDLE);
/*      */   }
/*      */ 
/*      */   public final int getReaderIdleCount()
/*      */   {
/* 1092 */     return getIdleCount(IdleStatus.READER_IDLE);
/*      */   }
/*      */ 
/*      */   public final int getWriterIdleCount()
/*      */   {
/* 1099 */     return getIdleCount(IdleStatus.WRITER_IDLE);
/*      */   }
/*      */ 
/*      */   public SocketAddress getServiceAddress()
/*      */   {
/* 1106 */     IoService service = getService();
/* 1107 */     if ((service instanceof IoAcceptor)) {
/* 1108 */       return ((IoAcceptor)service).getLocalAddress();
/*      */     }
/* 1110 */     return getRemoteAddress();
/*      */   }
/*      */ 
/*      */   public final int hashCode()
/*      */   {
/* 1119 */     return super.hashCode();
/*      */   }
/*      */ 
/*      */   public final boolean equals(Object o)
/*      */   {
/* 1128 */     return super.equals(o);
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1136 */     if ((isConnected()) || (isClosing())) {
/* 1137 */       if ((getService() instanceof IoAcceptor)) {
/* 1138 */         return "(" + getIdAsString() + ": " + getServiceName() + ", server, " + getRemoteAddress() + " => " + getLocalAddress() + ')';
/*      */       }
/*      */ 
/* 1141 */       return "(" + getIdAsString() + ": " + getServiceName() + ", client, " + getLocalAddress() + " => " + getRemoteAddress() + ')';
/*      */     }
/*      */ 
/* 1145 */     return "Session disconnected ...";
/*      */   }
/*      */ 
/*      */   private String getIdAsString()
/*      */   {
/* 1154 */     String id = Long.toHexString(getId()).toUpperCase();
/*      */ 
/* 1158 */     while (id.length() < 8) {
/* 1159 */       id = '0' + id;
/*      */     }
/* 1161 */     id = "0x" + id;
/*      */ 
/* 1163 */     return id;
/*      */   }
/*      */ 
/*      */   private String getServiceName()
/*      */   {
/* 1170 */     TransportMetadata tm = getTransportMetadata();
/* 1171 */     if (tm == null) {
/* 1172 */       return "null";
/*      */     }
/* 1174 */     return tm.getProviderName() + ' ' + tm.getName();
/*      */   }
/*      */ 
/*      */   public static void notifyIdleness(Iterator<? extends IoSession> sessions, long currentTime)
/*      */   {
/* 1185 */     IoSession s = null;
/* 1186 */     while (sessions.hasNext()) {
/* 1187 */       s = (IoSession)sessions.next();
/* 1188 */       notifyIdleSession(s, currentTime);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void notifyIdleSession(IoSession session, long currentTime)
/*      */   {
/* 1199 */     notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.BOTH_IDLE), IdleStatus.BOTH_IDLE, Math.max(session.getLastIoTime(), session.getLastIdleTime(IdleStatus.BOTH_IDLE)));
/*      */ 
/* 1206 */     notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.READER_IDLE), IdleStatus.READER_IDLE, Math.max(session.getLastReadTime(), session.getLastIdleTime(IdleStatus.READER_IDLE)));
/*      */ 
/* 1213 */     notifyIdleSession0(session, currentTime, session.getConfig().getIdleTimeInMillis(IdleStatus.WRITER_IDLE), IdleStatus.WRITER_IDLE, Math.max(session.getLastWriteTime(), session.getLastIdleTime(IdleStatus.WRITER_IDLE)));
/*      */ 
/* 1220 */     notifyWriteTimeout(session, currentTime);
/*      */   }
/*      */ 
/*      */   private static void notifyIdleSession0(IoSession session, long currentTime, long idleTime, IdleStatus status, long lastIoTime)
/*      */   {
/* 1226 */     if ((idleTime > 0L) && (lastIoTime != 0L) && (currentTime - lastIoTime >= idleTime))
/*      */     {
/* 1228 */       session.getFilterChain().fireSessionIdle(status);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void notifyWriteTimeout(IoSession session, long currentTime)
/*      */   {
/* 1235 */     long writeTimeout = session.getConfig().getWriteTimeoutInMillis();
/* 1236 */     if ((writeTimeout > 0L) && (currentTime - session.getLastWriteTime() >= writeTimeout) && (!session.getWriteRequestQueue().isEmpty(session)))
/*      */     {
/* 1239 */       WriteRequest request = session.getCurrentWriteRequest();
/* 1240 */       if (request != null) {
/* 1241 */         session.setCurrentWriteRequest(null);
/* 1242 */         WriteTimeoutException cause = new WriteTimeoutException(request);
/* 1243 */         request.getFuture().setException(cause);
/* 1244 */         session.getFilterChain().fireExceptionCaught(cause);
/*      */ 
/* 1246 */         session.close(true);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CloseAwareWriteQueue
/*      */     implements WriteRequestQueue
/*      */   {
/*      */     private final WriteRequestQueue q;
/*      */ 
/*      */     public CloseAwareWriteQueue(WriteRequestQueue q)
/*      */     {
/* 1267 */       this.q = q;
/*      */     }
/*      */ 
/*      */     public synchronized WriteRequest poll(IoSession session)
/*      */     {
/* 1274 */       WriteRequest answer = this.q.poll(session);
/*      */ 
/* 1276 */       if (answer == AbstractIoSession.CLOSE_REQUEST) {
/* 1277 */         AbstractIoSession.this.close();
/* 1278 */         dispose(session);
/* 1279 */         answer = null;
/*      */       }
/*      */ 
/* 1282 */       return answer;
/*      */     }
/*      */ 
/*      */     public void offer(IoSession session, WriteRequest e)
/*      */     {
/* 1289 */       this.q.offer(session, e);
/*      */     }
/*      */ 
/*      */     public boolean isEmpty(IoSession session)
/*      */     {
/* 1296 */       return this.q.isEmpty(session);
/*      */     }
/*      */ 
/*      */     public void clear(IoSession session)
/*      */     {
/* 1303 */       this.q.clear(session);
/*      */     }
/*      */ 
/*      */     public void dispose(IoSession session)
/*      */     {
/* 1310 */       this.q.dispose(session);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.AbstractIoSession
 * JD-Core Version:    0.6.0
 */
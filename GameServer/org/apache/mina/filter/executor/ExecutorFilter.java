/*     */ package org.apache.mina.filter.executor;
/*     */ 
/*     */ import java.util.EnumSet;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.filterchain.IoFilterChain;
/*     */ import org.apache.mina.core.filterchain.IoFilterEvent;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoEventType;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class ExecutorFilter extends IoFilterAdapter
/*     */ {
/*     */   private EnumSet<IoEventType> eventTypes;
/*     */   private Executor executor;
/*     */   private boolean manageableExecutor;
/*     */   private static final int DEFAULT_MAX_POOL_SIZE = 16;
/*     */   private static final int BASE_THREAD_NUMBER = 0;
/*     */   private static final long DEFAULT_KEEPALIVE_TIME = 30L;
/*     */   private static final boolean MANAGEABLE_EXECUTOR = true;
/*     */   private static final boolean NOT_MANAGEABLE_EXECUTOR = false;
/* 141 */   private static IoEventType[] DEFAULT_EVENT_SET = { IoEventType.EXCEPTION_CAUGHT, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT, IoEventType.SESSION_CLOSED, IoEventType.SESSION_IDLE, IoEventType.SESSION_OPENED };
/*     */ 
/*     */   public ExecutorFilter()
/*     */   {
/* 159 */     Executor executor = createDefaultExecutor(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 168 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int maximumPoolSize)
/*     */   {
/* 181 */     Executor executor = createDefaultExecutor(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 190 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize)
/*     */   {
/* 204 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 213 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit)
/*     */   {
/* 228 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
/*     */ 
/* 237 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler)
/*     */   {
/* 255 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
/*     */ 
/* 264 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory)
/*     */   {
/* 282 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
/*     */ 
/* 291 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler)
/*     */   {
/* 310 */     Executor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
/*     */ 
/* 313 */     init(executor, true, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(IoEventType[] eventTypes)
/*     */   {
/* 324 */     Executor executor = createDefaultExecutor(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 333 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int maximumPoolSize, IoEventType[] eventTypes)
/*     */   {
/* 345 */     Executor executor = createDefaultExecutor(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 354 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, IoEventType[] eventTypes)
/*     */   {
/* 367 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
/*     */ 
/* 376 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventType[] eventTypes)
/*     */   {
/* 393 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
/*     */ 
/* 402 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler, IoEventType[] eventTypes)
/*     */   {
/* 421 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
/*     */ 
/* 430 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventType[] eventTypes)
/*     */   {
/* 449 */     Executor executor = createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
/*     */ 
/* 458 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler, IoEventType[] eventTypes)
/*     */   {
/* 479 */     Executor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
/*     */ 
/* 483 */     init(executor, true, eventTypes);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(Executor executor)
/*     */   {
/* 493 */     init(executor, false, DEFAULT_EVENT_SET);
/*     */   }
/*     */ 
/*     */   public ExecutorFilter(Executor executor, IoEventType[] eventTypes)
/*     */   {
/* 504 */     init(executor, false, eventTypes);
/*     */   }
/*     */ 
/*     */   private Executor createDefaultExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler)
/*     */   {
/* 521 */     Executor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
/*     */ 
/* 524 */     return executor;
/*     */   }
/*     */ 
/*     */   private void initEventTypes(IoEventType[] eventTypes)
/*     */   {
/* 534 */     if ((eventTypes == null) || (eventTypes.length == 0)) {
/* 535 */       eventTypes = DEFAULT_EVENT_SET;
/*     */     }
/*     */ 
/* 539 */     this.eventTypes = EnumSet.of(eventTypes[0], eventTypes);
/*     */ 
/* 542 */     if (this.eventTypes.contains(IoEventType.SESSION_CREATED)) {
/* 543 */       this.eventTypes = null;
/* 544 */       throw new IllegalArgumentException(IoEventType.SESSION_CREATED + " is not allowed.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void init(Executor executor, boolean manageableExecutor, IoEventType[] eventTypes)
/*     */   {
/* 559 */     if (executor == null) {
/* 560 */       throw new NullPointerException("executor");
/*     */     }
/*     */ 
/* 563 */     initEventTypes(eventTypes);
/* 564 */     this.executor = executor;
/* 565 */     this.manageableExecutor = manageableExecutor;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 574 */     if (this.manageableExecutor)
/* 575 */       ((ExecutorService)this.executor).shutdown();
/*     */   }
/*     */ 
/*     */   public final Executor getExecutor()
/*     */   {
/* 585 */     return this.executor;
/*     */   }
/*     */ 
/*     */   protected void fireEvent(IoFilterEvent event)
/*     */   {
/* 592 */     getExecutor().execute(event);
/*     */   }
/*     */ 
/*     */   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter)
/*     */     throws Exception
/*     */   {
/* 609 */     if (parent.contains(this))
/* 610 */       throw new IllegalArgumentException("You can't add the same filter instance more than once.  Create another instance and add it.");
/*     */   }
/*     */ 
/*     */   public final void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */   {
/* 620 */     if (this.eventTypes.contains(IoEventType.SESSION_OPENED)) {
/* 621 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_OPENED, session, null);
/*     */ 
/* 623 */       fireEvent(event);
/*     */     } else {
/* 625 */       nextFilter.sessionOpened(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */   {
/* 634 */     if (this.eventTypes.contains(IoEventType.SESSION_CLOSED)) {
/* 635 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_CLOSED, session, null);
/*     */ 
/* 637 */       fireEvent(event);
/*     */     } else {
/* 639 */       nextFilter.sessionClosed(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */   {
/* 649 */     if (this.eventTypes.contains(IoEventType.SESSION_IDLE)) {
/* 650 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_IDLE, session, status);
/*     */ 
/* 652 */       fireEvent(event);
/*     */     } else {
/* 654 */       nextFilter.sessionIdle(session, status);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause)
/*     */   {
/* 664 */     if (this.eventTypes.contains(IoEventType.EXCEPTION_CAUGHT)) {
/* 665 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.EXCEPTION_CAUGHT, session, cause);
/*     */ 
/* 667 */       fireEvent(event);
/*     */     } else {
/* 669 */       nextFilter.exceptionCaught(session, cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */   {
/* 679 */     if (this.eventTypes.contains(IoEventType.MESSAGE_RECEIVED)) {
/* 680 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, session, message);
/*     */ 
/* 682 */       fireEvent(event);
/*     */     } else {
/* 684 */       nextFilter.messageReceived(session, message);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */   {
/* 694 */     if (this.eventTypes.contains(IoEventType.MESSAGE_SENT)) {
/* 695 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.MESSAGE_SENT, session, writeRequest);
/*     */ 
/* 697 */       fireEvent(event);
/*     */     } else {
/* 699 */       nextFilter.messageSent(session, writeRequest);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */   {
/* 709 */     if (this.eventTypes.contains(IoEventType.WRITE)) {
/* 710 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.WRITE, session, writeRequest);
/*     */ 
/* 712 */       fireEvent(event);
/*     */     } else {
/* 714 */       nextFilter.filterWrite(session, writeRequest);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void filterClose(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 724 */     if (this.eventTypes.contains(IoEventType.CLOSE)) {
/* 725 */       IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.CLOSE, session, null);
/*     */ 
/* 727 */       fireEvent(event);
/*     */     } else {
/* 729 */       nextFilter.filterClose(session);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.executor.ExecutorFilter
 * JD-Core Version:    0.6.0
 */
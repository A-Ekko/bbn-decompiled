/*     */ package org.apache.mina.filter.statistic;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IdleStatus;
/*     */ import org.apache.mina.core.session.IoEventType;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ 
/*     */ public class ProfilerTimerFilter extends IoFilterAdapter
/*     */ {
/*     */   private volatile TimeUnit timeUnit;
/*     */   private TimerWorker messageReceivedTimerWorker;
/*  69 */   private boolean profileMessageReceived = false;
/*     */   private TimerWorker messageSentTimerWorker;
/*  75 */   private boolean profileMessageSent = false;
/*     */   private TimerWorker sessionCreatedTimerWorker;
/*  81 */   private boolean profileSessionCreated = false;
/*     */   private TimerWorker sessionOpenedTimerWorker;
/*  87 */   private boolean profileSessionOpened = false;
/*     */   private TimerWorker sessionIdleTimerWorker;
/*  93 */   private boolean profileSessionIdle = false;
/*     */   private TimerWorker sessionClosedTimerWorker;
/*  99 */   private boolean profileSessionClosed = false;
/*     */ 
/*     */   public ProfilerTimerFilter()
/*     */   {
/* 108 */     this(TimeUnit.MILLISECONDS, new IoEventType[] { IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT });
/*     */   }
/*     */ 
/*     */   public ProfilerTimerFilter(TimeUnit timeUnit)
/*     */   {
/* 121 */     this(timeUnit, new IoEventType[] { IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT });
/*     */   }
/*     */ 
/*     */   public ProfilerTimerFilter(TimeUnit timeUnit, IoEventType[] eventTypes)
/*     */   {
/* 143 */     this.timeUnit = timeUnit;
/*     */ 
/* 145 */     setProfilers(eventTypes);
/*     */   }
/*     */ 
/*     */   private void setProfilers(IoEventType[] eventTypes)
/*     */   {
/* 154 */     for (IoEventType type : eventTypes)
/* 155 */       switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */       case 1:
/* 157 */         this.messageReceivedTimerWorker = new TimerWorker();
/* 158 */         this.profileMessageReceived = true;
/* 159 */         break;
/*     */       case 2:
/* 162 */         this.messageSentTimerWorker = new TimerWorker();
/* 163 */         this.profileMessageSent = true;
/* 164 */         break;
/*     */       case 3:
/* 167 */         this.sessionCreatedTimerWorker = new TimerWorker();
/* 168 */         this.profileSessionCreated = true;
/* 169 */         break;
/*     */       case 4:
/* 172 */         this.sessionOpenedTimerWorker = new TimerWorker();
/* 173 */         this.profileSessionOpened = true;
/* 174 */         break;
/*     */       case 5:
/* 177 */         this.sessionIdleTimerWorker = new TimerWorker();
/* 178 */         this.profileSessionIdle = true;
/* 179 */         break;
/*     */       case 6:
/* 182 */         this.sessionClosedTimerWorker = new TimerWorker();
/* 183 */         this.profileSessionClosed = true;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setTimeUnit(TimeUnit timeUnit)
/*     */   {
/* 195 */     this.timeUnit = timeUnit;
/*     */   }
/*     */ 
/*     */   public void profile(IoEventType type)
/*     */   {
/* 204 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 206 */       this.profileMessageReceived = true;
/*     */ 
/* 208 */       if (this.messageReceivedTimerWorker == null) {
/* 209 */         this.messageReceivedTimerWorker = new TimerWorker();
/*     */       }
/*     */ 
/* 212 */       return;
/*     */     case 2:
/* 215 */       this.profileMessageSent = true;
/*     */ 
/* 217 */       if (this.messageSentTimerWorker == null) {
/* 218 */         this.messageSentTimerWorker = new TimerWorker();
/*     */       }
/*     */ 
/* 221 */       return;
/*     */     case 3:
/* 224 */       this.profileSessionCreated = true;
/*     */ 
/* 226 */       if (this.sessionCreatedTimerWorker != null) break;
/* 227 */       this.sessionCreatedTimerWorker = new TimerWorker();
/*     */     case 4:
/* 231 */       this.profileSessionOpened = true;
/*     */ 
/* 233 */       if (this.sessionOpenedTimerWorker == null) {
/* 234 */         this.sessionOpenedTimerWorker = new TimerWorker();
/*     */       }
/*     */ 
/*     */     case 5:
/* 238 */       this.profileSessionIdle = true;
/*     */ 
/* 240 */       if (this.sessionIdleTimerWorker == null) {
/* 241 */         this.sessionIdleTimerWorker = new TimerWorker();
/*     */       }
/*     */ 
/*     */     case 6:
/* 245 */       this.profileSessionClosed = true;
/*     */ 
/* 247 */       if (this.sessionClosedTimerWorker == null)
/* 248 */         this.sessionClosedTimerWorker = new TimerWorker();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stopProfile(IoEventType type)
/*     */   {
/* 259 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 261 */       this.profileMessageReceived = false;
/* 262 */       return;
/*     */     case 2:
/* 265 */       this.profileMessageSent = false;
/* 266 */       return;
/*     */     case 3:
/* 269 */       this.profileSessionCreated = false;
/* 270 */       return;
/*     */     case 4:
/* 273 */       this.profileSessionOpened = false;
/* 274 */       return;
/*     */     case 5:
/* 277 */       this.profileSessionIdle = false;
/* 278 */       return;
/*     */     case 6:
/* 281 */       this.profileSessionClosed = false;
/* 282 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Set<IoEventType> getEventsToProfile()
/*     */   {
/* 292 */     Set set = new HashSet();
/*     */ 
/* 294 */     if (this.profileMessageReceived) {
/* 295 */       set.add(IoEventType.MESSAGE_RECEIVED);
/*     */     }
/*     */ 
/* 298 */     if (this.profileMessageSent) {
/* 299 */       set.add(IoEventType.MESSAGE_SENT);
/*     */     }
/*     */ 
/* 302 */     if (this.profileSessionCreated) {
/* 303 */       set.add(IoEventType.SESSION_CREATED);
/*     */     }
/*     */ 
/* 306 */     if (this.profileSessionOpened) {
/* 307 */       set.add(IoEventType.SESSION_OPENED);
/*     */     }
/*     */ 
/* 310 */     if (this.profileSessionIdle) {
/* 311 */       set.add(IoEventType.SESSION_IDLE);
/*     */     }
/*     */ 
/* 314 */     if (this.profileSessionClosed) {
/* 315 */       set.add(IoEventType.SESSION_CLOSED);
/*     */     }
/*     */ 
/* 318 */     return set;
/*     */   }
/*     */ 
/*     */   public void setEventsToProfile(IoEventType[] eventTypes)
/*     */   {
/* 327 */     setProfilers(eventTypes);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message)
/*     */     throws Exception
/*     */   {
/* 346 */     if (this.profileMessageReceived) {
/* 347 */       long start = timeNow();
/* 348 */       nextFilter.messageReceived(session, message);
/* 349 */       long end = timeNow();
/* 350 */       this.messageReceivedTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 352 */       nextFilter.messageReceived(session, message);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest)
/*     */     throws Exception
/*     */   {
/* 372 */     if (this.profileMessageSent) {
/* 373 */       long start = timeNow();
/* 374 */       nextFilter.messageSent(session, writeRequest);
/* 375 */       long end = timeNow();
/* 376 */       this.messageSentTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 378 */       nextFilter.messageSent(session, writeRequest);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 397 */     if (this.profileSessionCreated) {
/* 398 */       long start = timeNow();
/* 399 */       nextFilter.sessionCreated(session);
/* 400 */       long end = timeNow();
/* 401 */       this.sessionCreatedTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 403 */       nextFilter.sessionCreated(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 422 */     if (this.profileSessionOpened) {
/* 423 */       long start = timeNow();
/* 424 */       nextFilter.sessionOpened(session);
/* 425 */       long end = timeNow();
/* 426 */       this.sessionOpenedTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 428 */       nextFilter.sessionOpened(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status)
/*     */     throws Exception
/*     */   {
/* 448 */     if (this.profileSessionIdle) {
/* 449 */       long start = timeNow();
/* 450 */       nextFilter.sessionIdle(session, status);
/* 451 */       long end = timeNow();
/* 452 */       this.sessionIdleTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 454 */       nextFilter.sessionIdle(session, status);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 473 */     if (this.profileSessionClosed) {
/* 474 */       long start = timeNow();
/* 475 */       nextFilter.sessionClosed(session);
/* 476 */       long end = timeNow();
/* 477 */       this.sessionClosedTimerWorker.addNewDuration(end - start);
/*     */     } else {
/* 479 */       nextFilter.sessionClosed(session);
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getAverageTime(IoEventType type)
/*     */   {
/* 492 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 494 */       if (!this.profileMessageReceived) break;
/* 495 */       return this.messageReceivedTimerWorker.getAverage();
/*     */     case 2:
/* 501 */       if (!this.profileMessageSent) break;
/* 502 */       return this.messageSentTimerWorker.getAverage();
/*     */     case 3:
/* 508 */       if (!this.profileSessionCreated) break;
/* 509 */       return this.sessionCreatedTimerWorker.getAverage();
/*     */     case 4:
/* 515 */       if (!this.profileSessionOpened) break;
/* 516 */       return this.sessionOpenedTimerWorker.getAverage();
/*     */     case 5:
/* 522 */       if (!this.profileSessionIdle) break;
/* 523 */       return this.sessionIdleTimerWorker.getAverage();
/*     */     case 6:
/* 529 */       if (!this.profileSessionClosed) break;
/* 530 */       return this.sessionClosedTimerWorker.getAverage();
/*     */     }
/*     */ 
/* 536 */     throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
/*     */   }
/*     */ 
/*     */   public long getTotalCalls(IoEventType type)
/*     */   {
/* 550 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 552 */       if (!this.profileMessageReceived) break;
/* 553 */       return this.messageReceivedTimerWorker.getCallsNumber();
/*     */     case 2:
/* 559 */       if (!this.profileMessageSent) break;
/* 560 */       return this.messageSentTimerWorker.getCallsNumber();
/*     */     case 3:
/* 566 */       if (!this.profileSessionCreated) break;
/* 567 */       return this.sessionCreatedTimerWorker.getCallsNumber();
/*     */     case 4:
/* 573 */       if (!this.profileSessionOpened) break;
/* 574 */       return this.sessionOpenedTimerWorker.getCallsNumber();
/*     */     case 5:
/* 580 */       if (!this.profileSessionIdle) break;
/* 581 */       return this.sessionIdleTimerWorker.getCallsNumber();
/*     */     case 6:
/* 587 */       if (!this.profileSessionClosed) break;
/* 588 */       return this.sessionClosedTimerWorker.getCallsNumber();
/*     */     }
/*     */ 
/* 594 */     throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
/*     */   }
/*     */ 
/*     */   public long getTotalTime(IoEventType type)
/*     */   {
/* 608 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 610 */       if (!this.profileMessageReceived) break;
/* 611 */       return this.messageReceivedTimerWorker.getTotal();
/*     */     case 2:
/* 617 */       if (!this.profileMessageSent) break;
/* 618 */       return this.messageSentTimerWorker.getTotal();
/*     */     case 3:
/* 624 */       if (!this.profileSessionCreated) break;
/* 625 */       return this.sessionCreatedTimerWorker.getTotal();
/*     */     case 4:
/* 631 */       if (!this.profileSessionOpened) break;
/* 632 */       return this.sessionOpenedTimerWorker.getTotal();
/*     */     case 5:
/* 638 */       if (!this.profileSessionIdle) break;
/* 639 */       return this.sessionIdleTimerWorker.getTotal();
/*     */     case 6:
/* 645 */       if (!this.profileSessionClosed) break;
/* 646 */       return this.sessionClosedTimerWorker.getTotal();
/*     */     }
/*     */ 
/* 652 */     throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
/*     */   }
/*     */ 
/*     */   public long getMinimumTime(IoEventType type)
/*     */   {
/* 666 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 668 */       if (!this.profileMessageReceived) break;
/* 669 */       return this.messageReceivedTimerWorker.getMinimum();
/*     */     case 2:
/* 675 */       if (!this.profileMessageSent) break;
/* 676 */       return this.messageSentTimerWorker.getMinimum();
/*     */     case 3:
/* 682 */       if (!this.profileSessionCreated) break;
/* 683 */       return this.sessionCreatedTimerWorker.getMinimum();
/*     */     case 4:
/* 689 */       if (!this.profileSessionOpened) break;
/* 690 */       return this.sessionOpenedTimerWorker.getMinimum();
/*     */     case 5:
/* 696 */       if (!this.profileSessionIdle) break;
/* 697 */       return this.sessionIdleTimerWorker.getMinimum();
/*     */     case 6:
/* 703 */       if (!this.profileSessionClosed) break;
/* 704 */       return this.sessionClosedTimerWorker.getMinimum();
/*     */     }
/*     */ 
/* 710 */     throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
/*     */   }
/*     */ 
/*     */   public long getMaximumTime(IoEventType type)
/*     */   {
/* 724 */     switch (1.$SwitchMap$org$apache$mina$core$session$IoEventType[type.ordinal()]) {
/*     */     case 1:
/* 726 */       if (!this.profileMessageReceived) break;
/* 727 */       return this.messageReceivedTimerWorker.getMaximum();
/*     */     case 2:
/* 733 */       if (!this.profileMessageSent) break;
/* 734 */       return this.messageSentTimerWorker.getMaximum();
/*     */     case 3:
/* 740 */       if (!this.profileSessionCreated) break;
/* 741 */       return this.sessionCreatedTimerWorker.getMaximum();
/*     */     case 4:
/* 747 */       if (!this.profileSessionOpened) break;
/* 748 */       return this.sessionOpenedTimerWorker.getMaximum();
/*     */     case 5:
/* 754 */       if (!this.profileSessionIdle) break;
/* 755 */       return this.sessionIdleTimerWorker.getMaximum();
/*     */     case 6:
/* 761 */       if (!this.profileSessionClosed) break;
/* 762 */       return this.sessionClosedTimerWorker.getMaximum();
/*     */     }
/*     */ 
/* 768 */     throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
/*     */   }
/*     */ 
/*     */   private long timeNow()
/*     */   {
/* 881 */     switch (1.$SwitchMap$java$util$concurrent$TimeUnit[this.timeUnit.ordinal()]) {
/*     */     case 1:
/* 883 */       return System.currentTimeMillis() / 1000L;
/*     */     case 2:
/* 886 */       return System.nanoTime() / 1000L;
/*     */     case 3:
/* 889 */       return System.nanoTime();
/*     */     }
/*     */ 
/* 892 */     return System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   private class TimerWorker
/*     */   {
/*     */     private final AtomicLong total;
/*     */     private final AtomicLong callsNumber;
/*     */     private final AtomicLong minimum;
/*     */     private final AtomicLong maximum;
/* 791 */     private final Object lock = new Object();
/*     */ 
/*     */     public TimerWorker()
/*     */     {
/* 798 */       this.total = new AtomicLong();
/* 799 */       this.callsNumber = new AtomicLong();
/* 800 */       this.minimum = new AtomicLong();
/* 801 */       this.maximum = new AtomicLong();
/*     */     }
/*     */ 
/*     */     public void addNewDuration(long duration)
/*     */     {
/* 812 */       this.callsNumber.incrementAndGet();
/* 813 */       this.total.addAndGet(duration);
/*     */ 
/* 815 */       synchronized (this.lock)
/*     */       {
/* 817 */         if (duration < this.minimum.longValue()) {
/* 818 */           this.minimum.set(duration);
/*     */         }
/*     */ 
/* 822 */         if (duration > this.maximum.longValue())
/* 823 */           this.maximum.set(duration);
/*     */       }
/*     */     }
/*     */ 
/*     */     public double getAverage()
/*     */     {
/* 834 */       synchronized (this.lock)
/*     */       {
/* 836 */         return this.total.longValue() / this.callsNumber.longValue();
/*     */       }
/*     */     }
/*     */ 
/*     */     public long getCallsNumber()
/*     */     {
/* 846 */       return this.callsNumber.longValue();
/*     */     }
/*     */ 
/*     */     public long getTotal()
/*     */     {
/* 855 */       return this.total.longValue();
/*     */     }
/*     */ 
/*     */     public long getMinimum()
/*     */     {
/* 864 */       return this.minimum.longValue();
/*     */     }
/*     */ 
/*     */     public long getMaximum()
/*     */     {
/* 873 */       return this.maximum.longValue();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.statistic.ProfilerTimerFilter
 * JD-Core Version:    0.6.0
 */
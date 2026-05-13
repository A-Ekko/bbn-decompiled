/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.AppenderAttachable;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class AsyncAppender extends AppenderSkeleton
/*     */   implements AppenderAttachable
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 128;
/*  68 */   private final List buffer = new ArrayList();
/*     */ 
/*  73 */   private final Map discardMap = new HashMap();
/*     */ 
/*  78 */   private int bufferSize = 128;
/*     */   AppenderAttachableImpl aai;
/*     */   private final AppenderAttachableImpl appenders;
/*     */   private final Thread dispatcher;
/*  96 */   private boolean locationInfo = false;
/*     */ 
/* 101 */   private boolean blocking = true;
/*     */ 
/*     */   public AsyncAppender()
/*     */   {
/* 107 */     this.appenders = new AppenderAttachableImpl();
/*     */ 
/* 111 */     this.aai = this.appenders;
/*     */ 
/* 113 */     this.dispatcher = new Thread(new Dispatcher(this, this.buffer, this.discardMap, this.appenders));
/*     */ 
/* 118 */     this.dispatcher.setDaemon(true);
/*     */ 
/* 122 */     this.dispatcher.setName("Dispatcher-" + this.dispatcher.getName());
/* 123 */     this.dispatcher.start();
/*     */   }
/*     */ 
/*     */   public void addAppender(Appender newAppender)
/*     */   {
/* 132 */     synchronized (this.appenders) {
/* 133 */       this.appenders.addAppender(newAppender);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 145 */     if ((this.dispatcher == null) || (!this.dispatcher.isAlive()) || (this.bufferSize <= 0)) {
/* 146 */       synchronized (this.appenders) {
/* 147 */         this.appenders.appendLoopOnAppenders(event);
/*     */       }
/*     */ 
/* 150 */       return;
/*     */     }
/*     */ 
/* 155 */     event.getNDC();
/* 156 */     event.getThreadName();
/*     */ 
/* 158 */     event.getMDCCopy();
/* 159 */     if (this.locationInfo) {
/* 160 */       event.getLocationInformation();
/*     */     }
/*     */ 
/* 163 */     synchronized (this.buffer) {
/*     */       while (true) {
/* 165 */         int previousSize = this.buffer.size();
/*     */ 
/* 167 */         if (previousSize < this.bufferSize) {
/* 168 */           this.buffer.add(event);
/*     */ 
/* 175 */           if (previousSize != 0) break;
/* 176 */           this.buffer.notifyAll(); break;
/*     */         }
/*     */ 
/* 189 */         boolean discard = true;
/* 190 */         if ((this.blocking) && (!Thread.interrupted()) && (Thread.currentThread() != this.dispatcher))
/*     */         {
/*     */           try
/*     */           {
/* 194 */             this.buffer.wait();
/* 195 */             discard = false;
/*     */           }
/*     */           catch (InterruptedException e)
/*     */           {
/* 201 */             Thread.currentThread().interrupt();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 209 */         if (discard) {
/* 210 */           String loggerName = event.getLoggerName();
/* 211 */           DiscardSummary summary = (DiscardSummary)this.discardMap.get(loggerName);
/*     */ 
/* 213 */           if (summary == null) {
/* 214 */             summary = new DiscardSummary(event);
/* 215 */             this.discardMap.put(loggerName, summary); break;
/*     */           }
/* 217 */           summary.add(event);
/*     */ 
/* 220 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 235 */     synchronized (this.buffer) {
/* 236 */       this.closed = true;
/* 237 */       this.buffer.notifyAll();
/*     */     }
/*     */     try
/*     */     {
/* 241 */       this.dispatcher.join();
/*     */     } catch (InterruptedException e) {
/* 243 */       Thread.currentThread().interrupt();
/* 244 */       LogLog.error("Got an InterruptedException while waiting for the dispatcher to finish.", e);
/*     */     }
/*     */ 
/* 252 */     synchronized (this.appenders) {
/* 253 */       Enumeration iter = this.appenders.getAllAppenders();
/*     */ 
/* 255 */       if (iter != null)
/* 256 */         while (iter.hasMoreElements()) {
/* 257 */           Object next = iter.nextElement();
/*     */ 
/* 259 */           if ((next instanceof Appender))
/* 260 */             ((Appender)next).close();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Enumeration getAllAppenders()
/*     */   {
/* 272 */     synchronized (this.appenders) {
/* 273 */       return this.appenders.getAllAppenders();
/*     */     }
/*     */   }
/*     */ 
/*     */   public Appender getAppender(String name)
/*     */   {
/* 284 */     synchronized (this.appenders) {
/* 285 */       return this.appenders.getAppender(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 296 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean isAttached(Appender appender)
/*     */   {
/* 305 */     synchronized (this.appenders) {
/* 306 */       return this.appenders.isAttached(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 314 */     return false;
/*     */   }
/*     */ 
/*     */   public void removeAllAppenders()
/*     */   {
/* 321 */     synchronized (this.appenders) {
/* 322 */       this.appenders.removeAllAppenders();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(Appender appender)
/*     */   {
/* 331 */     synchronized (this.appenders) {
/* 332 */       this.appenders.removeAppender(appender);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAppender(String name)
/*     */   {
/* 341 */     synchronized (this.appenders) {
/* 342 */       this.appenders.removeAppender(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean flag)
/*     */   {
/* 360 */     this.locationInfo = flag;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int size)
/*     */   {
/* 376 */     if (size < 0) {
/* 377 */       throw new NegativeArraySizeException("size");
/*     */     }
/*     */ 
/* 380 */     synchronized (this.buffer)
/*     */     {
/* 384 */       this.bufferSize = (size < 1 ? 1 : size);
/* 385 */       this.buffer.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 394 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setBlocking(boolean value)
/*     */   {
/* 404 */     synchronized (this.buffer) {
/* 405 */       this.blocking = value;
/* 406 */       this.buffer.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getBlocking()
/*     */   {
/* 418 */     return this.blocking;
/*     */   }
/*     */ 
/*     */   private static class Dispatcher
/*     */     implements Runnable
/*     */   {
/*     */     private final AsyncAppender parent;
/*     */     private final List buffer;
/*     */     private final Map discardMap;
/*     */     private final AppenderAttachableImpl appenders;
/*     */ 
/*     */     public Dispatcher(AsyncAppender parent, List buffer, Map discardMap, AppenderAttachableImpl appenders)
/*     */     {
/* 514 */       this.parent = parent;
/* 515 */       this.buffer = buffer;
/* 516 */       this.appenders = appenders;
/* 517 */       this.discardMap = discardMap;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 524 */       boolean isActive = true;
/*     */       try
/*     */       {
/* 533 */         while (isActive) {
/* 534 */           LoggingEvent[] events = null;
/*     */ 
/* 540 */           synchronized (this.buffer) {
/* 541 */             int bufferSize = this.buffer.size();
/* 542 */             isActive = !this.parent.closed;
/*     */ 
/* 544 */             while ((bufferSize == 0) && (isActive)) {
/* 545 */               this.buffer.wait();
/* 546 */               bufferSize = this.buffer.size();
/* 547 */               isActive = !this.parent.closed;
/*     */             }
/*     */ 
/* 550 */             if (bufferSize > 0) {
/* 551 */               events = new LoggingEvent[bufferSize + this.discardMap.size()];
/* 552 */               this.buffer.toArray(events);
/*     */ 
/* 557 */               int index = bufferSize;
/*     */ 
/* 560 */               Iterator iter = this.discardMap.values().iterator();
/* 561 */               while (iter.hasNext()) {
/* 562 */                 events[(index++)] = ((AsyncAppender.DiscardSummary)iter.next()).createEvent();
/*     */               }
/*     */ 
/* 568 */               this.buffer.clear();
/* 569 */               this.discardMap.clear();
/*     */ 
/* 573 */               this.buffer.notifyAll();
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 580 */           if (events != null)
/* 581 */             for (int i = 0; i < events.length; i++)
/* 582 */               synchronized (this.appenders) {
/* 583 */                 this.appenders.appendLoopOnAppenders(events[i]);
/*     */               }
/*     */         }
/*     */       }
/*     */       catch (InterruptedException ex)
/*     */       {
/* 589 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class DiscardSummary
/*     */   {
/*     */     private LoggingEvent maxEvent;
/*     */     private int count;
/*     */ 
/*     */     public DiscardSummary(LoggingEvent event)
/*     */     {
/* 441 */       this.maxEvent = event;
/* 442 */       this.count = 1;
/*     */     }
/*     */ 
/*     */     public void add(LoggingEvent event)
/*     */     {
/* 451 */       if (event.getLevel().toInt() > this.maxEvent.getLevel().toInt()) {
/* 452 */         this.maxEvent = event;
/*     */       }
/*     */ 
/* 455 */       this.count += 1;
/*     */     }
/*     */ 
/*     */     public LoggingEvent createEvent()
/*     */     {
/* 464 */       String msg = MessageFormat.format("Discarded {0} messages due to full event buffer including: {1}", new Object[] { new Integer(this.count), this.maxEvent.getMessage() });
/*     */ 
/* 469 */       return new LoggingEvent("org.apache.log4j.AsyncAppender.DONT_REPORT_LOCATION", Logger.getLogger(this.maxEvent.getLoggerName()), this.maxEvent.getLevel(), msg, null);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.AsyncAppender
 * JD-Core Version:    0.6.0
 */
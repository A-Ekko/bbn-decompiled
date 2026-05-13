/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public class IoServiceStatistics
/*     */ {
/*     */   private AbstractIoService service;
/*     */   private double readBytesThroughput;
/*     */   private double writtenBytesThroughput;
/*     */   private double readMessagesThroughput;
/*     */   private double writtenMessagesThroughput;
/*     */   private double largestReadBytesThroughput;
/*     */   private double largestWrittenBytesThroughput;
/*     */   private double largestReadMessagesThroughput;
/*     */   private double largestWrittenMessagesThroughput;
/*  46 */   private final AtomicLong readBytes = new AtomicLong();
/*  47 */   private final AtomicLong writtenBytes = new AtomicLong();
/*  48 */   private final AtomicLong readMessages = new AtomicLong();
/*  49 */   private final AtomicLong writtenMessages = new AtomicLong();
/*     */   private long lastReadTime;
/*     */   private long lastWriteTime;
/*     */   private long lastReadBytes;
/*     */   private long lastWrittenBytes;
/*     */   private long lastReadMessages;
/*     */   private long lastWrittenMessages;
/*     */   private long lastThroughputCalculationTime;
/*  59 */   private final AtomicInteger scheduledWriteBytes = new AtomicInteger();
/*  60 */   private final AtomicInteger scheduledWriteMessages = new AtomicInteger();
/*     */ 
/*  62 */   private int throughputCalculationInterval = 3;
/*     */ 
/*  64 */   private final Object throughputCalculationLock = new Object();
/*     */ 
/*     */   public IoServiceStatistics(AbstractIoService service) {
/*  67 */     this.service = service;
/*     */   }
/*     */ 
/*     */   public final int getLargestManagedSessionCount()
/*     */   {
/*  75 */     return this.service.getListeners().getLargestManagedSessionCount();
/*     */   }
/*     */ 
/*     */   public final long getCumulativeManagedSessionCount()
/*     */   {
/*  84 */     return this.service.getListeners().getCumulativeManagedSessionCount();
/*     */   }
/*     */ 
/*     */   public final long getLastIoTime()
/*     */   {
/*  91 */     return Math.max(this.lastReadTime, this.lastWriteTime);
/*     */   }
/*     */ 
/*     */   public final long getLastReadTime()
/*     */   {
/*  98 */     return this.lastReadTime;
/*     */   }
/*     */ 
/*     */   public final long getLastWriteTime()
/*     */   {
/* 105 */     return this.lastWriteTime;
/*     */   }
/*     */ 
/*     */   public final long getReadBytes()
/*     */   {
/* 115 */     return this.readBytes.get();
/*     */   }
/*     */ 
/*     */   public final long getWrittenBytes()
/*     */   {
/* 125 */     return this.writtenBytes.get();
/*     */   }
/*     */ 
/*     */   public final long getReadMessages()
/*     */   {
/* 135 */     return this.readMessages.get();
/*     */   }
/*     */ 
/*     */   public final long getWrittenMessages()
/*     */   {
/* 145 */     return this.writtenMessages.get();
/*     */   }
/*     */ 
/*     */   public final double getReadBytesThroughput()
/*     */   {
/* 152 */     resetThroughput();
/* 153 */     return this.readBytesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getWrittenBytesThroughput()
/*     */   {
/* 160 */     resetThroughput();
/* 161 */     return this.writtenBytesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getReadMessagesThroughput()
/*     */   {
/* 168 */     resetThroughput();
/* 169 */     return this.readMessagesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getWrittenMessagesThroughput()
/*     */   {
/* 176 */     resetThroughput();
/* 177 */     return this.writtenMessagesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getLargestReadBytesThroughput()
/*     */   {
/* 184 */     return this.largestReadBytesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getLargestWrittenBytesThroughput()
/*     */   {
/* 191 */     return this.largestWrittenBytesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getLargestReadMessagesThroughput()
/*     */   {
/* 198 */     return this.largestReadMessagesThroughput;
/*     */   }
/*     */ 
/*     */   public final double getLargestWrittenMessagesThroughput()
/*     */   {
/* 205 */     return this.largestWrittenMessagesThroughput;
/*     */   }
/*     */ 
/*     */   public final int getThroughputCalculationInterval()
/*     */   {
/* 213 */     return this.throughputCalculationInterval;
/*     */   }
/*     */ 
/*     */   public final long getThroughputCalculationIntervalInMillis()
/*     */   {
/* 221 */     return this.throughputCalculationInterval * 1000L;
/*     */   }
/*     */ 
/*     */   public final void setThroughputCalculationInterval(int throughputCalculationInterval)
/*     */   {
/* 230 */     if (throughputCalculationInterval < 0) {
/* 231 */       throw new IllegalArgumentException("throughputCalculationInterval: " + throughputCalculationInterval);
/*     */     }
/*     */ 
/* 236 */     this.throughputCalculationInterval = throughputCalculationInterval;
/*     */   }
/*     */ 
/*     */   protected final void setLastReadTime(long lastReadTime)
/*     */   {
/* 243 */     this.lastReadTime = lastReadTime;
/*     */   }
/*     */ 
/*     */   protected final void setLastWriteTime(long lastWriteTime)
/*     */   {
/* 250 */     this.lastWriteTime = lastWriteTime;
/*     */   }
/*     */ 
/*     */   private void resetThroughput()
/*     */   {
/* 257 */     if (this.service.getManagedSessionCount() == 0) {
/* 258 */       this.readBytesThroughput = 0.0D;
/* 259 */       this.writtenBytesThroughput = 0.0D;
/* 260 */       this.readMessagesThroughput = 0.0D;
/* 261 */       this.writtenMessagesThroughput = 0.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateThroughput(long currentTime)
/*     */   {
/* 269 */     synchronized (this.throughputCalculationLock) {
/* 270 */       int interval = (int)(currentTime - this.lastThroughputCalculationTime);
/* 271 */       long minInterval = getThroughputCalculationIntervalInMillis();
/* 272 */       if ((minInterval == 0L) || (interval < minInterval)) {
/* 273 */         return;
/*     */       }
/*     */ 
/* 276 */       long readBytes = this.readBytes.get();
/* 277 */       long writtenBytes = this.writtenBytes.get();
/* 278 */       long readMessages = this.readMessages.get();
/* 279 */       long writtenMessages = this.writtenMessages.get();
/*     */ 
/* 281 */       this.readBytesThroughput = ((readBytes - this.lastReadBytes) * 1000.0D / interval);
/*     */ 
/* 283 */       this.writtenBytesThroughput = ((writtenBytes - this.lastWrittenBytes) * 1000.0D / interval);
/*     */ 
/* 285 */       this.readMessagesThroughput = ((readMessages - this.lastReadMessages) * 1000.0D / interval);
/*     */ 
/* 287 */       this.writtenMessagesThroughput = ((writtenMessages - this.lastWrittenMessages) * 1000.0D / interval);
/*     */ 
/* 290 */       if (this.readBytesThroughput > this.largestReadBytesThroughput) {
/* 291 */         this.largestReadBytesThroughput = this.readBytesThroughput;
/*     */       }
/* 293 */       if (this.writtenBytesThroughput > this.largestWrittenBytesThroughput) {
/* 294 */         this.largestWrittenBytesThroughput = this.writtenBytesThroughput;
/*     */       }
/* 296 */       if (this.readMessagesThroughput > this.largestReadMessagesThroughput) {
/* 297 */         this.largestReadMessagesThroughput = this.readMessagesThroughput;
/*     */       }
/* 299 */       if (this.writtenMessagesThroughput > this.largestWrittenMessagesThroughput) {
/* 300 */         this.largestWrittenMessagesThroughput = this.writtenMessagesThroughput;
/*     */       }
/*     */ 
/* 303 */       this.lastReadBytes = readBytes;
/* 304 */       this.lastWrittenBytes = writtenBytes;
/* 305 */       this.lastReadMessages = readMessages;
/* 306 */       this.lastWrittenMessages = writtenMessages;
/*     */ 
/* 308 */       this.lastThroughputCalculationTime = currentTime;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void increaseReadBytes(long increment, long currentTime)
/*     */   {
/* 316 */     this.readBytes.addAndGet(increment);
/* 317 */     this.lastReadTime = currentTime;
/*     */   }
/*     */ 
/*     */   public final void increaseReadMessages(long currentTime)
/*     */   {
/* 324 */     this.readMessages.incrementAndGet();
/* 325 */     this.lastReadTime = currentTime;
/*     */   }
/*     */ 
/*     */   public final void increaseWrittenBytes(int increment, long currentTime)
/*     */   {
/* 332 */     this.writtenBytes.addAndGet(increment);
/* 333 */     this.lastWriteTime = currentTime;
/*     */   }
/*     */ 
/*     */   public final void increaseWrittenMessages(long currentTime)
/*     */   {
/* 340 */     this.writtenMessages.incrementAndGet();
/* 341 */     this.lastWriteTime = currentTime;
/*     */   }
/*     */ 
/*     */   public final int getScheduledWriteBytes()
/*     */   {
/* 348 */     return this.scheduledWriteBytes.get();
/*     */   }
/*     */ 
/*     */   public final void increaseScheduledWriteBytes(int increment)
/*     */   {
/* 355 */     this.scheduledWriteBytes.addAndGet(increment);
/*     */   }
/*     */ 
/*     */   public final int getScheduledWriteMessages()
/*     */   {
/* 362 */     return this.scheduledWriteMessages.get();
/*     */   }
/*     */ 
/*     */   public final void increaseScheduledWriteMessages()
/*     */   {
/* 369 */     this.scheduledWriteMessages.incrementAndGet();
/*     */   }
/*     */ 
/*     */   public final void decreaseScheduledWriteMessages()
/*     */   {
/* 376 */     this.scheduledWriteMessages.decrementAndGet();
/*     */   }
/*     */ 
/*     */   protected void setLastThroughputCalculationTime(long lastThroughputCalculationTime)
/*     */   {
/* 384 */     this.lastThroughputCalculationTime = lastThroughputCalculationTime;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.service.IoServiceStatistics
 * JD-Core Version:    0.6.0
 */
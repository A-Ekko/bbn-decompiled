/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ public abstract class AbstractIoSessionConfig
/*     */   implements IoSessionConfig
/*     */ {
/*  32 */   private int minReadBufferSize = 64;
/*  33 */   private int readBufferSize = 2048;
/*  34 */   private int maxReadBufferSize = 65536;
/*     */   private int idleTimeForRead;
/*     */   private int idleTimeForWrite;
/*     */   private int idleTimeForBoth;
/*  38 */   private int writeTimeout = 60;
/*     */   private boolean useReadOperation;
/*  40 */   private int throughputCalculationInterval = 3;
/*     */ 
/*     */   public final void setAll(IoSessionConfig config)
/*     */   {
/*  49 */     if (config == null) {
/*  50 */       throw new NullPointerException("config");
/*     */     }
/*     */ 
/*  53 */     setReadBufferSize(config.getReadBufferSize());
/*  54 */     setMinReadBufferSize(config.getMinReadBufferSize());
/*  55 */     setMaxReadBufferSize(config.getMaxReadBufferSize());
/*  56 */     setIdleTime(IdleStatus.BOTH_IDLE, config.getIdleTime(IdleStatus.BOTH_IDLE));
/*  57 */     setIdleTime(IdleStatus.READER_IDLE, config.getIdleTime(IdleStatus.READER_IDLE));
/*  58 */     setIdleTime(IdleStatus.WRITER_IDLE, config.getIdleTime(IdleStatus.WRITER_IDLE));
/*  59 */     setWriteTimeout(config.getWriteTimeout());
/*  60 */     setUseReadOperation(config.isUseReadOperation());
/*  61 */     setThroughputCalculationInterval(config.getThroughputCalculationInterval());
/*     */ 
/*  63 */     doSetAll(config);
/*     */   }
/*     */ 
/*     */   protected abstract void doSetAll(IoSessionConfig paramIoSessionConfig);
/*     */ 
/*     */   public int getReadBufferSize()
/*     */   {
/*  76 */     return this.readBufferSize;
/*     */   }
/*     */ 
/*     */   public void setReadBufferSize(int readBufferSize)
/*     */   {
/*  83 */     if (readBufferSize <= 0) {
/*  84 */       throw new IllegalArgumentException("readBufferSize: " + readBufferSize + " (expected: 1+)");
/*     */     }
/*  86 */     this.readBufferSize = readBufferSize;
/*     */   }
/*     */ 
/*     */   public int getMinReadBufferSize()
/*     */   {
/*  93 */     return this.minReadBufferSize;
/*     */   }
/*     */ 
/*     */   public void setMinReadBufferSize(int minReadBufferSize)
/*     */   {
/* 100 */     if (minReadBufferSize <= 0) {
/* 101 */       throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: 1+)");
/*     */     }
/* 103 */     if (minReadBufferSize > this.maxReadBufferSize) {
/* 104 */       throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: smaller than " + this.maxReadBufferSize + ')');
/*     */     }
/*     */ 
/* 107 */     this.minReadBufferSize = minReadBufferSize;
/*     */   }
/*     */ 
/*     */   public int getMaxReadBufferSize()
/*     */   {
/* 114 */     return this.maxReadBufferSize;
/*     */   }
/*     */ 
/*     */   public void setMaxReadBufferSize(int maxReadBufferSize)
/*     */   {
/* 121 */     if (maxReadBufferSize <= 0) {
/* 122 */       throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: 1+)");
/*     */     }
/*     */ 
/* 125 */     if (maxReadBufferSize < this.minReadBufferSize) {
/* 126 */       throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: greater than " + this.minReadBufferSize + ')');
/*     */     }
/*     */ 
/* 129 */     this.maxReadBufferSize = maxReadBufferSize;
/*     */   }
/*     */ 
/*     */   public int getIdleTime(IdleStatus status)
/*     */   {
/* 136 */     if (status == IdleStatus.BOTH_IDLE) {
/* 137 */       return this.idleTimeForBoth;
/*     */     }
/*     */ 
/* 140 */     if (status == IdleStatus.READER_IDLE) {
/* 141 */       return this.idleTimeForRead;
/*     */     }
/*     */ 
/* 144 */     if (status == IdleStatus.WRITER_IDLE) {
/* 145 */       return this.idleTimeForWrite;
/*     */     }
/*     */ 
/* 148 */     throw new IllegalArgumentException("Unknown idle status: " + status);
/*     */   }
/*     */ 
/*     */   public long getIdleTimeInMillis(IdleStatus status)
/*     */   {
/* 155 */     return getIdleTime(status) * 1000L;
/*     */   }
/*     */ 
/*     */   public void setIdleTime(IdleStatus status, int idleTime)
/*     */   {
/* 162 */     if (idleTime < 0) {
/* 163 */       throw new IllegalArgumentException("Illegal idle time: " + idleTime);
/*     */     }
/*     */ 
/* 166 */     if (status == IdleStatus.BOTH_IDLE)
/* 167 */       this.idleTimeForBoth = idleTime;
/* 168 */     else if (status == IdleStatus.READER_IDLE)
/* 169 */       this.idleTimeForRead = idleTime;
/* 170 */     else if (status == IdleStatus.WRITER_IDLE)
/* 171 */       this.idleTimeForWrite = idleTime;
/*     */     else
/* 173 */       throw new IllegalArgumentException("Unknown idle status: " + status);
/*     */   }
/*     */ 
/*     */   public final int getBothIdleTime()
/*     */   {
/* 181 */     return getIdleTime(IdleStatus.BOTH_IDLE);
/*     */   }
/*     */ 
/*     */   public final long getBothIdleTimeInMillis()
/*     */   {
/* 188 */     return getIdleTimeInMillis(IdleStatus.BOTH_IDLE);
/*     */   }
/*     */ 
/*     */   public final int getReaderIdleTime()
/*     */   {
/* 195 */     return getIdleTime(IdleStatus.READER_IDLE);
/*     */   }
/*     */ 
/*     */   public final long getReaderIdleTimeInMillis()
/*     */   {
/* 202 */     return getIdleTimeInMillis(IdleStatus.READER_IDLE);
/*     */   }
/*     */ 
/*     */   public final int getWriterIdleTime()
/*     */   {
/* 209 */     return getIdleTime(IdleStatus.WRITER_IDLE);
/*     */   }
/*     */ 
/*     */   public final long getWriterIdleTimeInMillis()
/*     */   {
/* 216 */     return getIdleTimeInMillis(IdleStatus.WRITER_IDLE);
/*     */   }
/*     */ 
/*     */   public void setBothIdleTime(int idleTime)
/*     */   {
/* 223 */     setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
/*     */   }
/*     */ 
/*     */   public void setReaderIdleTime(int idleTime)
/*     */   {
/* 230 */     setIdleTime(IdleStatus.READER_IDLE, idleTime);
/*     */   }
/*     */ 
/*     */   public void setWriterIdleTime(int idleTime)
/*     */   {
/* 237 */     setIdleTime(IdleStatus.WRITER_IDLE, idleTime);
/*     */   }
/*     */ 
/*     */   public int getWriteTimeout()
/*     */   {
/* 244 */     return this.writeTimeout;
/*     */   }
/*     */ 
/*     */   public long getWriteTimeoutInMillis()
/*     */   {
/* 251 */     return this.writeTimeout * 1000L;
/*     */   }
/*     */ 
/*     */   public void setWriteTimeout(int writeTimeout)
/*     */   {
/* 258 */     if (writeTimeout < 0) {
/* 259 */       throw new IllegalArgumentException("Illegal write timeout: " + writeTimeout);
/*     */     }
/*     */ 
/* 262 */     this.writeTimeout = writeTimeout;
/*     */   }
/*     */ 
/*     */   public boolean isUseReadOperation()
/*     */   {
/* 269 */     return this.useReadOperation;
/*     */   }
/*     */ 
/*     */   public void setUseReadOperation(boolean useReadOperation)
/*     */   {
/* 276 */     this.useReadOperation = useReadOperation;
/*     */   }
/*     */ 
/*     */   public int getThroughputCalculationInterval()
/*     */   {
/* 283 */     return this.throughputCalculationInterval;
/*     */   }
/*     */ 
/*     */   public void setThroughputCalculationInterval(int throughputCalculationInterval)
/*     */   {
/* 290 */     if (throughputCalculationInterval < 0) {
/* 291 */       throw new IllegalArgumentException("throughputCalculationInterval: " + throughputCalculationInterval);
/*     */     }
/*     */ 
/* 295 */     this.throughputCalculationInterval = throughputCalculationInterval;
/*     */   }
/*     */ 
/*     */   public long getThroughputCalculationIntervalInMillis()
/*     */   {
/* 302 */     return this.throughputCalculationInterval * 1000L;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.AbstractIoSessionConfig
 * JD-Core Version:    0.6.0
 */
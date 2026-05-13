/*     */ package org.apache.mina.core.service;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.mina.core.RuntimeIoException;
/*     */ import org.apache.mina.core.session.AbstractIoSession;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class SimpleIoProcessorPool<T extends AbstractIoSession>
/*     */   implements IoProcessor<T>
/*     */ {
/*  79 */   private static final int DEFAULT_SIZE = Runtime.getRuntime().availableProcessors() + 1;
/*  80 */   private static final AttributeKey PROCESSOR = new AttributeKey(SimpleIoProcessorPool.class, "processor");
/*     */ 
/*  82 */   private final Logger logger = LoggerFactory.getLogger(getClass());
/*     */   private final IoProcessor<T>[] pool;
/*  85 */   private final AtomicInteger processorDistributor = new AtomicInteger();
/*     */   private final Executor executor;
/*     */   private final boolean createdExecutor;
/*  89 */   private final Object disposalLock = new Object();
/*     */   private volatile boolean disposing;
/*     */   private volatile boolean disposed;
/*     */ 
/*     */   public SimpleIoProcessorPool(Class<? extends IoProcessor<T>> processorType)
/*     */   {
/*  94 */     this(processorType, null, DEFAULT_SIZE);
/*     */   }
/*     */ 
/*     */   public SimpleIoProcessorPool(Class<? extends IoProcessor<T>> processorType, int size) {
/*  98 */     this(processorType, null, size);
/*     */   }
/*     */ 
/*     */   public SimpleIoProcessorPool(Class<? extends IoProcessor<T>> processorType, Executor executor) {
/* 102 */     this(processorType, executor, DEFAULT_SIZE);
/*     */   }
/*     */ 
/*     */   public SimpleIoProcessorPool(Class<? extends IoProcessor<T>> processorType, Executor executor, int size)
/*     */   {
/* 107 */     if (processorType == null) {
/* 108 */       throw new NullPointerException("processorType");
/*     */     }
/* 110 */     if (size <= 0) {
/* 111 */       throw new IllegalArgumentException("size: " + size + " (expected: positive integer)");
/*     */     }
/*     */ 
/* 115 */     if (executor == null) {
/* 116 */       this.executor = (executor = Executors.newCachedThreadPool());
/* 117 */       this.createdExecutor = true;
/*     */     } else {
/* 119 */       this.executor = executor;
/* 120 */       this.createdExecutor = false;
/*     */     }
/*     */ 
/* 123 */     this.pool = new IoProcessor[size];
/*     */ 
/* 125 */     boolean success = false;
/*     */     try {
/* 127 */       for (int i = 0; i < this.pool.length; i++) {
/* 128 */         IoProcessor processor = null;
/*     */         try
/*     */         {
/*     */           try
/*     */           {
/* 133 */             processor = (IoProcessor)processorType.getConstructor(new Class[] { ExecutorService.class }).newInstance(new Object[] { executor });
/*     */           }
/*     */           catch (NoSuchMethodException e)
/*     */           {
/*     */           }
/* 138 */           if (processor == null) {
/*     */             try {
/* 140 */               processor = (IoProcessor)processorType.getConstructor(new Class[] { Executor.class }).newInstance(new Object[] { executor });
/*     */             }
/*     */             catch (NoSuchMethodException e)
/*     */             {
/*     */             }
/*     */           }
/* 146 */           if (processor == null)
/*     */             try {
/* 148 */               processor = (IoProcessor)processorType.getConstructor(new Class[0]).newInstance(new Object[0]);
/*     */             }
/*     */             catch (NoSuchMethodException e) {
/*     */             }
/*     */         }
/*     */         catch (RuntimeException e) {
/* 154 */           throw e;
/*     */         } catch (Exception e) {
/* 156 */           throw new RuntimeIoException("Failed to create a new instance of " + processorType.getName(), e);
/*     */         }
/*     */ 
/* 161 */         if (processor == null) {
/* 162 */           throw new IllegalArgumentException(String.valueOf(processorType) + " must have a public constructor " + "with one " + ExecutorService.class.getSimpleName() + " parameter, " + "a public constructor with one " + Executor.class.getSimpleName() + " parameter or a public default constructor.");
/*     */         }
/*     */ 
/* 169 */         this.pool[i] = processor;
/*     */       }
/*     */ 
/* 172 */       success = true;
/*     */     } finally {
/* 174 */       if (!success)
/* 175 */         dispose();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void add(T session)
/*     */   {
/* 181 */     getProcessor(session).add(session);
/*     */   }
/*     */ 
/*     */   public final void flush(T session) {
/* 185 */     getProcessor(session).flush(session);
/*     */   }
/*     */ 
/*     */   public final void remove(T session) {
/* 189 */     getProcessor(session).remove(session);
/*     */   }
/*     */ 
/*     */   public final void updateTrafficControl(T session) {
/* 193 */     getProcessor(session).updateTrafficControl(session);
/*     */   }
/*     */ 
/*     */   public boolean isDisposed() {
/* 197 */     return this.disposed;
/*     */   }
/*     */ 
/*     */   public boolean isDisposing() {
/* 201 */     return this.disposing;
/*     */   }
/*     */ 
/*     */   public final void dispose() {
/* 205 */     if (this.disposed) {
/* 206 */       return;
/*     */     }
/*     */ 
/* 209 */     synchronized (this.disposalLock) {
/* 210 */       if (!this.disposing) {
/* 211 */         this.disposing = true;
/* 212 */         for (int i = this.pool.length - 1; i >= 0; i--) {
/* 213 */           if ((this.pool[i] == null) || (this.pool[i].isDisposing())) {
/*     */             continue;
/*     */           }
/*     */           try
/*     */           {
/* 218 */             this.pool[i].dispose();
/*     */           } catch (Exception e) {
/* 220 */             this.logger.warn("Failed to dispose a " + this.pool[i].getClass().getSimpleName() + " at index " + i + ".", e);
/*     */           }
/*     */           finally
/*     */           {
/* 225 */             this.pool[i] = null;
/*     */           }
/*     */         }
/*     */ 
/* 229 */         if (this.createdExecutor) {
/* 230 */           ((ExecutorService)this.executor).shutdown();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 235 */     this.disposed = true;
/*     */   }
/*     */ 
/*     */   private IoProcessor<T> getProcessor(T session)
/*     */   {
/* 240 */     IoProcessor p = (IoProcessor)session.getAttribute(PROCESSOR);
/* 241 */     if (p == null) {
/* 242 */       p = nextProcessor();
/* 243 */       IoProcessor oldp = (IoProcessor)session.setAttributeIfAbsent(PROCESSOR, p);
/*     */ 
/* 245 */       if (oldp != null) {
/* 246 */         p = oldp;
/*     */       }
/*     */     }
/*     */ 
/* 250 */     return p;
/*     */   }
/*     */ 
/*     */   private IoProcessor<T> nextProcessor() {
/* 254 */     checkDisposal();
/* 255 */     return this.pool[(java.lang.Math.abs(this.processorDistributor.getAndIncrement()) % this.pool.length)];
/*     */   }
/*     */ 
/*     */   private void checkDisposal() {
/* 259 */     if (this.disposed)
/* 260 */       throw new IllegalStateException("A disposed processor cannot be accessed.");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.service.SimpleIoProcessorPool
 * JD-Core Version:    0.6.0
 */
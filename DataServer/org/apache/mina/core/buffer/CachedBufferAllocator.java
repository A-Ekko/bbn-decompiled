/*     */ package org.apache.mina.core.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.util.CircularQueue;
/*     */ 
/*     */ public class CachedBufferAllocator
/*     */   implements IoBufferAllocator
/*     */ {
/*     */   private static final int DEFAULT_MAX_POOL_SIZE = 8;
/*     */   private static final int DEFAULT_MAX_CACHED_BUFFER_SIZE = 262144;
/*     */   private final int maxPoolSize;
/*     */   private final int maxCachedBufferSize;
/*     */   private final ThreadLocal<Map<Integer, Queue<CachedBuffer>>> heapBuffers;
/*     */   private final ThreadLocal<Map<Integer, Queue<CachedBuffer>>> directBuffers;
/*     */ 
/*     */   public CachedBufferAllocator()
/*     */   {
/*  78 */     this(8, 262144);
/*     */   }
/*     */ 
/*     */   public CachedBufferAllocator(int maxPoolSize, int maxCachedBufferSize)
/*     */   {
/*  91 */     if (maxPoolSize < 0) {
/*  92 */       throw new IllegalArgumentException("maxPoolSize: " + maxPoolSize);
/*     */     }
/*  94 */     if (maxCachedBufferSize < 0) {
/*  95 */       throw new IllegalArgumentException("maxCachedBufferSize: " + maxCachedBufferSize);
/*     */     }
/*     */ 
/*  98 */     this.maxPoolSize = maxPoolSize;
/*  99 */     this.maxCachedBufferSize = maxCachedBufferSize;
/*     */ 
/* 101 */     this.heapBuffers = new ThreadLocal()
/*     */     {
/*     */       protected Map<Integer, Queue<CachedBufferAllocator.CachedBuffer>> initialValue() {
/* 104 */         return CachedBufferAllocator.this.newPoolMap();
/*     */       }
/*     */     };
/* 107 */     this.directBuffers = new ThreadLocal()
/*     */     {
/*     */       protected Map<Integer, Queue<CachedBufferAllocator.CachedBuffer>> initialValue() {
/* 110 */         return CachedBufferAllocator.this.newPoolMap();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public int getMaxPoolSize()
/*     */   {
/* 120 */     return this.maxPoolSize;
/*     */   }
/*     */ 
/*     */   public int getMaxCachedBufferSize()
/*     */   {
/* 129 */     return this.maxCachedBufferSize;
/*     */   }
/*     */ 
/*     */   private Map<Integer, Queue<CachedBuffer>> newPoolMap() {
/* 133 */     Map poolMap = new HashMap();
/*     */ 
/* 135 */     int poolSize = this.maxPoolSize == 0 ? 8 : this.maxPoolSize;
/* 136 */     for (int i = 0; i < 31; i++) {
/* 137 */       poolMap.put(Integer.valueOf(1 << i), new CircularQueue(poolSize));
/*     */     }
/* 139 */     poolMap.put(Integer.valueOf(0), new CircularQueue(poolSize));
/* 140 */     poolMap.put(Integer.valueOf(2147483647), new CircularQueue(poolSize));
/* 141 */     return poolMap;
/*     */   }
/*     */ 
/*     */   public IoBuffer allocate(int requestedCapacity, boolean direct) {
/* 145 */     int actualCapacity = IoBuffer.normalizeCapacity(requestedCapacity);
/*     */     IoBuffer buf;
/*     */     IoBuffer buf;
/* 147 */     if ((this.maxCachedBufferSize != 0) && (actualCapacity > this.maxCachedBufferSize))
/*     */     {
/*     */       IoBuffer buf;
/* 148 */       if (direct)
/* 149 */         buf = wrap(ByteBuffer.allocateDirect(actualCapacity));
/*     */       else
/* 151 */         buf = wrap(ByteBuffer.allocate(actualCapacity));
/*     */     }
/*     */     else
/*     */     {
/*     */       Queue pool;
/*     */       Queue pool;
/* 155 */       if (direct)
/* 156 */         pool = (Queue)((Map)this.directBuffers.get()).get(Integer.valueOf(actualCapacity));
/*     */       else {
/* 158 */         pool = (Queue)((Map)this.heapBuffers.get()).get(Integer.valueOf(actualCapacity));
/*     */       }
/*     */ 
/* 162 */       buf = (IoBuffer)pool.poll();
/* 163 */       if (buf != null) {
/* 164 */         buf.clear();
/* 165 */         buf.setAutoExpand(false);
/* 166 */         buf.order(ByteOrder.BIG_ENDIAN);
/*     */       }
/* 168 */       else if (direct) {
/* 169 */         buf = wrap(ByteBuffer.allocateDirect(actualCapacity));
/*     */       } else {
/* 171 */         buf = wrap(ByteBuffer.allocate(actualCapacity));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     buf.limit(requestedCapacity);
/* 177 */     return buf;
/*     */   }
/*     */ 
/*     */   public ByteBuffer allocateNioBuffer(int capacity, boolean direct) {
/* 181 */     return allocate(capacity, direct).buf();
/*     */   }
/*     */ 
/*     */   public IoBuffer wrap(ByteBuffer nioBuffer) {
/* 185 */     return new CachedBuffer(nioBuffer);
/*     */   }
/*     */   public void dispose() {
/*     */   }
/*     */   private class CachedBuffer extends AbstractIoBuffer {
/*     */     private final Thread ownerThread;
/*     */     private ByteBuffer buf;
/*     */ 
/*     */     protected CachedBuffer(ByteBuffer buf) {
/* 196 */       super(buf.capacity());
/* 197 */       this.ownerThread = Thread.currentThread();
/* 198 */       this.buf = buf;
/* 199 */       buf.order(ByteOrder.BIG_ENDIAN);
/*     */     }
/*     */ 
/*     */     protected CachedBuffer(CachedBuffer parent, ByteBuffer buf) {
/* 203 */       super();
/* 204 */       this.ownerThread = Thread.currentThread();
/* 205 */       this.buf = buf;
/*     */     }
/*     */ 
/*     */     public ByteBuffer buf()
/*     */     {
/* 210 */       if (this.buf == null) {
/* 211 */         throw new IllegalStateException("Buffer has been freed already.");
/*     */       }
/* 213 */       return this.buf;
/*     */     }
/*     */ 
/*     */     protected void buf(ByteBuffer buf)
/*     */     {
/* 218 */       ByteBuffer oldBuf = this.buf;
/* 219 */       this.buf = buf;
/* 220 */       free(oldBuf);
/*     */     }
/*     */ 
/*     */     protected IoBuffer duplicate0()
/*     */     {
/* 225 */       return new CachedBuffer(CachedBufferAllocator.this, this, buf().duplicate());
/*     */     }
/*     */ 
/*     */     protected IoBuffer slice0()
/*     */     {
/* 230 */       return new CachedBuffer(CachedBufferAllocator.this, this, buf().slice());
/*     */     }
/*     */ 
/*     */     protected IoBuffer asReadOnlyBuffer0()
/*     */     {
/* 235 */       return new CachedBuffer(CachedBufferAllocator.this, this, buf().asReadOnlyBuffer());
/*     */     }
/*     */ 
/*     */     public byte[] array()
/*     */     {
/* 240 */       return buf().array();
/*     */     }
/*     */ 
/*     */     public int arrayOffset()
/*     */     {
/* 245 */       return buf().arrayOffset();
/*     */     }
/*     */ 
/*     */     public boolean hasArray()
/*     */     {
/* 250 */       return buf().hasArray();
/*     */     }
/*     */ 
/*     */     public void free()
/*     */     {
/* 255 */       free(this.buf);
/* 256 */       this.buf = null;
/*     */     }
/*     */ 
/*     */     private void free(ByteBuffer oldBuf) {
/* 260 */       if ((oldBuf == null) || (oldBuf.capacity() > CachedBufferAllocator.this.maxCachedBufferSize) || (oldBuf.isReadOnly()) || (isDerived()) || (Thread.currentThread() != this.ownerThread))
/*     */       {
/* 263 */         return;
/*     */       }
/*     */       Queue pool;
/*     */       Queue pool;
/* 268 */       if (oldBuf.isDirect())
/* 269 */         pool = (Queue)((Map)CachedBufferAllocator.this.directBuffers.get()).get(Integer.valueOf(oldBuf.capacity()));
/*     */       else {
/* 271 */         pool = (Queue)((Map)CachedBufferAllocator.this.heapBuffers.get()).get(Integer.valueOf(oldBuf.capacity()));
/*     */       }
/*     */ 
/* 274 */       if (pool == null) {
/* 275 */         return;
/*     */       }
/*     */ 
/* 279 */       if ((CachedBufferAllocator.this.maxPoolSize == 0) || (pool.size() < CachedBufferAllocator.this.maxPoolSize))
/* 280 */         pool.offer(new CachedBuffer(CachedBufferAllocator.this, oldBuf));
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.buffer.CachedBufferAllocator
 * JD-Core Version:    0.6.0
 */
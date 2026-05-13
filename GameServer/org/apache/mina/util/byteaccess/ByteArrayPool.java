/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Stack;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class ByteArrayPool
/*     */   implements ByteArrayFactory
/*     */ {
/*  41 */   private final int MAX_BITS = 32;
/*     */   private boolean freed;
/*     */   private final boolean direct;
/*     */   private ArrayList<Stack<DirectBufferByteArray>> freeBuffers;
/*  46 */   private int freeBufferCount = 0;
/*  47 */   private long freeMemory = 0L;
/*     */   private final int maxFreeBuffers;
/*     */   private final int maxFreeMemory;
/*     */ 
/*     */   public ByteArrayPool(boolean direct, int maxFreeBuffers, int maxFreeMemory)
/*     */   {
/*  63 */     this.direct = direct;
/*  64 */     this.freeBuffers = new ArrayList();
/*  65 */     for (int i = 0; i < 32; i++)
/*     */     {
/*  67 */       this.freeBuffers.add(new Stack());
/*     */     }
/*  69 */     this.maxFreeBuffers = maxFreeBuffers;
/*  70 */     this.maxFreeMemory = maxFreeMemory;
/*  71 */     this.freed = false;
/*     */   }
/*     */ 
/*     */   public ByteArray create(int size)
/*     */   {
/*  82 */     if (size < 1)
/*     */     {
/*  84 */       throw new IllegalArgumentException("Buffer size must be at least 1: " + size);
/*     */     }
/*  86 */     int bits = bits(size);
/*  87 */     synchronized (this)
/*     */     {
/*  89 */       if (!this.freeBuffers.isEmpty())
/*     */       {
/*  91 */         DirectBufferByteArray ba = (DirectBufferByteArray)((Stack)this.freeBuffers.get(bits)).pop();
/*  92 */         ba.setFreed(false);
/*  93 */         ba.getSingleIoBuffer().limit(size);
/*  94 */         return ba;
/*     */       }
/*     */     }
/*     */ 
/*  98 */     int bbSize = 1 << bits;
/*  99 */     IoBuffer bb = IoBuffer.allocate(bbSize, this.direct);
/* 100 */     bb.limit(size);
/* 101 */     DirectBufferByteArray ba = new DirectBufferByteArray(bb);
/* 102 */     ba.setFreed(false);
/* 103 */     return ba;
/*     */   }
/*     */ 
/*     */   private int bits(int index)
/*     */   {
/* 109 */     int bits = 0;
/* 110 */     while (1 << bits < index)
/*     */     {
/* 112 */       bits++;
/*     */     }
/* 114 */     return bits;
/*     */   }
/*     */ 
/*     */   public void free()
/*     */   {
/* 123 */     synchronized (this)
/*     */     {
/* 125 */       if (this.freed)
/*     */       {
/* 127 */         throw new IllegalStateException("Already freed.");
/*     */       }
/* 129 */       this.freed = true;
/* 130 */       this.freeBuffers.clear();
/* 131 */       this.freeBuffers = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class DirectBufferByteArray extends BufferByteArray
/*     */   {
/*     */     public boolean freed;
/*     */ 
/*     */     public DirectBufferByteArray(IoBuffer bb)
/*     */     {
/* 143 */       super();
/*     */     }
/*     */ 
/*     */     public void setFreed(boolean freed)
/*     */     {
/* 149 */       this.freed = freed;
/*     */     }
/*     */ 
/*     */     public void free()
/*     */     {
/* 156 */       synchronized (this)
/*     */       {
/* 158 */         if (this.freed)
/*     */         {
/* 160 */           throw new IllegalStateException("Already freed.");
/*     */         }
/* 162 */         this.freed = true;
/*     */       }
/* 164 */       int bits = ByteArrayPool.this.bits(last());
/* 165 */       synchronized (ByteArrayPool.this)
/*     */       {
/* 167 */         if ((ByteArrayPool.this.freeBuffers != null) && (ByteArrayPool.this.freeBufferCount < ByteArrayPool.this.maxFreeBuffers) && (ByteArrayPool.this.freeMemory + last() <= ByteArrayPool.this.maxFreeMemory))
/*     */         {
/* 169 */           ((Stack)ByteArrayPool.this.freeBuffers.get(bits)).push(this);
/* 170 */           ByteArrayPool.access$208(ByteArrayPool.this);
/* 171 */           ByteArrayPool.access$414(ByteArrayPool.this, last());
/* 172 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.ByteArrayPool
 * JD-Core Version:    0.6.0
 */
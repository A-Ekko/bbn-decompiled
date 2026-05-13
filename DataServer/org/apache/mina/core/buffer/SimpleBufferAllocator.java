/*     */ package org.apache.mina.core.buffer;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ 
/*     */ public class SimpleBufferAllocator
/*     */   implements IoBufferAllocator
/*     */ {
/*     */   public IoBuffer allocate(int capacity, boolean direct)
/*     */   {
/*  37 */     return wrap(allocateNioBuffer(capacity, direct));
/*     */   }
/*     */ 
/*     */   public ByteBuffer allocateNioBuffer(int capacity, boolean direct)
/*     */   {
/*     */     ByteBuffer nioBuffer;
/*     */     ByteBuffer nioBuffer;
/*  42 */     if (direct)
/*  43 */       nioBuffer = ByteBuffer.allocateDirect(capacity);
/*     */     else {
/*  45 */       nioBuffer = ByteBuffer.allocate(capacity);
/*     */     }
/*  47 */     return nioBuffer;
/*     */   }
/*     */ 
/*     */   public IoBuffer wrap(ByteBuffer nioBuffer) {
/*  51 */     return new SimpleBuffer(nioBuffer);
/*     */   }
/*     */   public void dispose() {
/*     */   }
/*     */ 
/*     */   private class SimpleBuffer extends AbstractIoBuffer {
/*     */     private ByteBuffer buf;
/*     */ 
/*     */     protected SimpleBuffer(ByteBuffer buf) {
/*  61 */       super(buf.capacity());
/*  62 */       this.buf = buf;
/*  63 */       buf.order(ByteOrder.BIG_ENDIAN);
/*     */     }
/*     */ 
/*     */     protected SimpleBuffer(SimpleBuffer parent, ByteBuffer buf) {
/*  67 */       super();
/*  68 */       this.buf = buf;
/*     */     }
/*     */ 
/*     */     public ByteBuffer buf()
/*     */     {
/*  73 */       return this.buf;
/*     */     }
/*     */ 
/*     */     protected void buf(ByteBuffer buf)
/*     */     {
/*  78 */       this.buf = buf;
/*     */     }
/*     */ 
/*     */     protected IoBuffer duplicate0()
/*     */     {
/*  83 */       return new SimpleBuffer(SimpleBufferAllocator.this, this, this.buf.duplicate());
/*     */     }
/*     */ 
/*     */     protected IoBuffer slice0()
/*     */     {
/*  88 */       return new SimpleBuffer(SimpleBufferAllocator.this, this, this.buf.slice());
/*     */     }
/*     */ 
/*     */     protected IoBuffer asReadOnlyBuffer0()
/*     */     {
/*  93 */       return new SimpleBuffer(SimpleBufferAllocator.this, this, this.buf.asReadOnlyBuffer());
/*     */     }
/*     */ 
/*     */     public byte[] array()
/*     */     {
/*  98 */       return this.buf.array();
/*     */     }
/*     */ 
/*     */     public int arrayOffset()
/*     */     {
/* 103 */       return this.buf.arrayOffset();
/*     */     }
/*     */ 
/*     */     public boolean hasArray()
/*     */     {
/* 108 */       return this.buf.hasArray();
/*     */     }
/*     */ 
/*     */     public void free()
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.buffer.SimpleBufferAllocator
 * JD-Core Version:    0.6.0
 */
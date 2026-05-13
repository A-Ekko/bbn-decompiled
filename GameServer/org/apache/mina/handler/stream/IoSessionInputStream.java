/*     */ package org.apache.mina.handler.stream;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ class IoSessionInputStream extends InputStream
/*     */ {
/*  37 */   private final Object mutex = new Object();
/*     */   private final IoBuffer buf;
/*     */   private volatile boolean closed;
/*     */   private volatile boolean released;
/*     */   private IOException exception;
/*     */ 
/*     */   public IoSessionInputStream()
/*     */   {
/*  48 */     this.buf = IoBuffer.allocate(16);
/*  49 */     this.buf.setAutoExpand(true);
/*  50 */     this.buf.limit(0);
/*     */   }
/*     */ 
/*     */   public int available()
/*     */   {
/*  55 */     if (this.released) {
/*  56 */       return 0;
/*     */     }
/*  58 */     synchronized (this.mutex) {
/*  59 */       return this.buf.remaining();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*  66 */     if (this.closed) {
/*  67 */       return;
/*     */     }
/*     */ 
/*  70 */     synchronized (this.mutex) {
/*  71 */       this.closed = true;
/*  72 */       releaseBuffer();
/*     */ 
/*  74 */       this.mutex.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/*  80 */     synchronized (this.mutex) {
/*  81 */       if (!waitForData()) {
/*  82 */         return -1;
/*     */       }
/*     */ 
/*  85 */       return this.buf.get() & 0xFF;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException
/*     */   {
/*  91 */     synchronized (this.mutex) {
/*  92 */       if (!waitForData())
/*  93 */         return -1;
/*     */       int readBytes;
/*     */       int readBytes;
/*  98 */       if (len > this.buf.remaining())
/*  99 */         readBytes = this.buf.remaining();
/*     */       else {
/* 101 */         readBytes = len;
/*     */       }
/*     */ 
/* 104 */       this.buf.get(b, off, readBytes);
/*     */ 
/* 106 */       return readBytes;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean waitForData() throws IOException {
/* 111 */     if (this.released) {
/* 112 */       return false;
/*     */     }
/*     */ 
/* 115 */     synchronized (this.mutex) {
/* 116 */       while ((!this.released) && (this.buf.remaining() == 0) && (this.exception == null)) {
/*     */         try {
/* 118 */           this.mutex.wait();
/*     */         } catch (InterruptedException e) {
/* 120 */           IOException ioe = new IOException("Interrupted while waiting for more data");
/*     */ 
/* 122 */           ioe.initCause(e);
/* 123 */           throw ioe;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 128 */     if (this.exception != null) {
/* 129 */       releaseBuffer();
/* 130 */       throw this.exception;
/*     */     }
/*     */ 
/* 133 */     if ((this.closed) && (this.buf.remaining() == 0)) {
/* 134 */       releaseBuffer();
/*     */ 
/* 136 */       return false;
/*     */     }
/*     */ 
/* 139 */     return true;
/*     */   }
/*     */ 
/*     */   private void releaseBuffer() {
/* 143 */     if (this.released) {
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     this.released = true;
/*     */   }
/*     */ 
/*     */   public void write(IoBuffer src) {
/* 151 */     synchronized (this.mutex) {
/* 152 */       if (this.closed) {
/* 153 */         return;
/*     */       }
/*     */ 
/* 156 */       if (this.buf.hasRemaining()) {
/* 157 */         this.buf.compact();
/* 158 */         this.buf.put(src);
/* 159 */         this.buf.flip();
/*     */       } else {
/* 161 */         this.buf.clear();
/* 162 */         this.buf.put(src);
/* 163 */         this.buf.flip();
/* 164 */         this.mutex.notifyAll();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void throwException(IOException e) {
/* 170 */     synchronized (this.mutex) {
/* 171 */       if (this.exception == null) {
/* 172 */         this.exception = e;
/*     */ 
/* 174 */         this.mutex.notifyAll();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.handler.stream.IoSessionInputStream
 * JD-Core Version:    0.6.0
 */
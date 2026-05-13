/*     */ package org.apache.mina.proxy.utils;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class IoBufferDecoder
/*     */ {
/*  86 */   private DecodingContext ctx = new DecodingContext();
/*     */ 
/*     */   public IoBufferDecoder(byte[] delimiter)
/*     */   {
/*  93 */     setDelimiter(delimiter, true);
/*     */   }
/*     */ 
/*     */   public IoBufferDecoder(int contentLength)
/*     */   {
/* 100 */     setContentLength(contentLength, false);
/*     */   }
/*     */ 
/*     */   public void setContentLength(int contentLength, boolean resetMatchCount)
/*     */   {
/* 110 */     if (contentLength <= 0) {
/* 111 */       throw new IllegalArgumentException("contentLength: " + contentLength);
/*     */     }
/*     */ 
/* 115 */     this.ctx.setContentLength(contentLength);
/* 116 */     if (resetMatchCount)
/* 117 */       this.ctx.setMatchCount(0);
/*     */   }
/*     */ 
/*     */   public void setDelimiter(byte[] delim, boolean resetMatchCount)
/*     */   {
/* 130 */     if (delim == null) {
/* 131 */       throw new NullPointerException("Null delimiter not allowed");
/*     */     }
/*     */ 
/* 135 */     IoBuffer delimiter = IoBuffer.allocate(delim.length);
/* 136 */     delimiter.put(delim);
/* 137 */     delimiter.flip();
/*     */ 
/* 139 */     this.ctx.setDelimiter(delimiter);
/* 140 */     this.ctx.setContentLength(-1);
/* 141 */     if (resetMatchCount)
/* 142 */       this.ctx.setMatchCount(0);
/*     */   }
/*     */ 
/*     */   public IoBuffer decodeFully(IoBuffer in)
/*     */   {
/* 153 */     int contentLength = this.ctx.getContentLength();
/* 154 */     IoBuffer decodedBuffer = this.ctx.getDecodedBuffer();
/*     */ 
/* 156 */     int oldLimit = in.limit();
/*     */ 
/* 159 */     if (contentLength > -1) {
/* 160 */       if (decodedBuffer == null) {
/* 161 */         decodedBuffer = IoBuffer.allocate(contentLength).setAutoExpand(true);
/*     */       }
/*     */ 
/* 165 */       if (in.remaining() < contentLength) {
/* 166 */         int readBytes = in.remaining();
/* 167 */         decodedBuffer.put(in);
/* 168 */         this.ctx.setDecodedBuffer(decodedBuffer);
/* 169 */         this.ctx.setContentLength(contentLength - readBytes);
/* 170 */         return null;
/*     */       }
/*     */ 
/* 173 */       int newLimit = in.position() + contentLength;
/* 174 */       in.limit(newLimit);
/* 175 */       decodedBuffer.put(in);
/* 176 */       decodedBuffer.flip();
/* 177 */       in.limit(oldLimit);
/* 178 */       this.ctx.clean();
/*     */ 
/* 180 */       return decodedBuffer;
/*     */     }
/*     */ 
/* 185 */     int oldPos = in.position();
/* 186 */     int matchCount = this.ctx.getMatchCount();
/* 187 */     IoBuffer delimiter = this.ctx.getDelimiter();
/*     */ 
/* 189 */     while (in.hasRemaining()) {
/* 190 */       byte b = in.get();
/* 191 */       if (delimiter.get(matchCount) == b) {
/* 192 */         matchCount++;
/* 193 */         if (matchCount == delimiter.limit())
/*     */         {
/* 195 */           int pos = in.position();
/* 196 */           in.position(oldPos);
/*     */ 
/* 198 */           in.limit(pos);
/*     */ 
/* 200 */           if (decodedBuffer == null) {
/* 201 */             decodedBuffer = IoBuffer.allocate(in.remaining()).setAutoExpand(true);
/*     */           }
/*     */ 
/* 205 */           decodedBuffer.put(in);
/* 206 */           decodedBuffer.flip();
/*     */ 
/* 208 */           in.limit(oldLimit);
/* 209 */           this.ctx.clean();
/*     */ 
/* 211 */           return decodedBuffer;
/*     */         }
/*     */       } else {
/* 214 */         in.position(Math.max(0, in.position() - matchCount));
/* 215 */         matchCount = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 220 */     if (in.remaining() > 0) {
/* 221 */       in.position(oldPos);
/* 222 */       decodedBuffer.put(in);
/* 223 */       in.position(in.limit());
/*     */     }
/*     */ 
/* 227 */     this.ctx.setMatchCount(matchCount);
/* 228 */     this.ctx.setDecodedBuffer(decodedBuffer);
/*     */ 
/* 230 */     return decodedBuffer;
/*     */   }
/*     */ 
/*     */   public class DecodingContext
/*     */   {
/*     */     private IoBuffer decodedBuffer;
/*     */     private IoBuffer delimiter;
/*  43 */     private int contentLength = -1;
/*     */ 
/*  45 */     private int matchCount = 0;
/*     */ 
/*     */     public DecodingContext() {  }
/*     */ 
/*  48 */     public void clean() { this.contentLength = -1;
/*  49 */       this.matchCount = 0;
/*  50 */       this.decodedBuffer = null; }
/*     */ 
/*     */     public int getContentLength()
/*     */     {
/*  54 */       return this.contentLength;
/*     */     }
/*     */ 
/*     */     public void setContentLength(int contentLength) {
/*  58 */       this.contentLength = contentLength;
/*     */     }
/*     */ 
/*     */     public int getMatchCount() {
/*  62 */       return this.matchCount;
/*     */     }
/*     */ 
/*     */     public void setMatchCount(int matchCount) {
/*  66 */       this.matchCount = matchCount;
/*     */     }
/*     */ 
/*     */     public IoBuffer getDecodedBuffer() {
/*  70 */       return this.decodedBuffer;
/*     */     }
/*     */ 
/*     */     public void setDecodedBuffer(IoBuffer decodedBuffer) {
/*  74 */       this.decodedBuffer = decodedBuffer;
/*     */     }
/*     */ 
/*     */     public IoBuffer getDelimiter() {
/*  78 */       return this.delimiter;
/*     */     }
/*     */ 
/*     */     public void setDelimiter(IoBuffer delimiter) {
/*  82 */       this.delimiter = delimiter;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.utils.IoBufferDecoder
 * JD-Core Version:    0.6.0
 */
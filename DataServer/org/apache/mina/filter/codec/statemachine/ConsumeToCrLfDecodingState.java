/*     */ package org.apache.mina.filter.codec.statemachine;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ 
/*     */ public abstract class ConsumeToCrLfDecodingState
/*     */   implements DecodingState
/*     */ {
/*     */   private static final byte CR = 13;
/*     */   private static final byte LF = 10;
/*     */   private boolean lastIsCR;
/*     */   private IoBuffer buffer;
/*     */ 
/*     */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*  56 */     int beginPos = in.position();
/*  57 */     int limit = in.limit();
/*  58 */     int terminatorPos = -1;
/*     */ 
/*  60 */     for (int i = beginPos; i < limit; i++) {
/*  61 */       byte b = in.get(i);
/*  62 */       if (b == 13) {
/*  63 */         this.lastIsCR = true;
/*     */       } else {
/*  65 */         if ((b == 10) && (this.lastIsCR)) {
/*  66 */           terminatorPos = i;
/*  67 */           break;
/*     */         }
/*  69 */         this.lastIsCR = false;
/*     */       }
/*     */     }
/*     */ 
/*  73 */     if (terminatorPos >= 0)
/*     */     {
/*  76 */       int endPos = terminatorPos - 1;
/*     */       IoBuffer product;
/*  78 */       if (beginPos < endPos) {
/*  79 */         in.limit(endPos);
/*     */         IoBuffer product;
/*  81 */         if (this.buffer == null) {
/*  82 */           product = in.slice();
/*     */         } else {
/*  84 */           this.buffer.put(in);
/*  85 */           IoBuffer product = this.buffer.flip();
/*  86 */           this.buffer = null;
/*     */         }
/*     */ 
/*  89 */         in.limit(limit);
/*     */       }
/*     */       else
/*     */       {
/*     */         IoBuffer product;
/*  92 */         if (this.buffer == null) {
/*  93 */           product = IoBuffer.allocate(0);
/*     */         } else {
/*  95 */           product = this.buffer.flip();
/*  96 */           this.buffer = null;
/*     */         }
/*     */       }
/*  99 */       in.position(terminatorPos + 1);
/* 100 */       return finishDecode(product, out);
/*     */     }
/* 102 */     in.position(beginPos);
/* 103 */     if (this.buffer == null) {
/* 104 */       this.buffer = IoBuffer.allocate(in.remaining());
/* 105 */       this.buffer.setAutoExpand(true);
/*     */     }
/*     */ 
/* 108 */     this.buffer.put(in);
/* 109 */     if (this.lastIsCR) {
/* 110 */       this.buffer.position(this.buffer.position() - 1);
/*     */     }
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*     */     IoBuffer product;
/*     */     IoBuffer product;
/* 122 */     if (this.buffer == null) {
/* 123 */       product = IoBuffer.allocate(0);
/*     */     } else {
/* 125 */       product = this.buffer.flip();
/* 126 */       this.buffer = null;
/*     */     }
/* 128 */     return finishDecode(product, out);
/*     */   }
/*     */ 
/*     */   protected abstract DecodingState finishDecode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*     */     throws Exception;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ConsumeToCrLfDecodingState
 * JD-Core Version:    0.6.0
 */
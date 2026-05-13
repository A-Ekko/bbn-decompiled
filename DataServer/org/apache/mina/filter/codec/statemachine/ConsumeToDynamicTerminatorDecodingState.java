/*     */ package org.apache.mina.filter.codec.statemachine;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ 
/*     */ public abstract class ConsumeToDynamicTerminatorDecodingState
/*     */   implements DecodingState
/*     */ {
/*     */   private IoBuffer buffer;
/*     */ 
/*     */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*  42 */     int beginPos = in.position();
/*  43 */     int terminatorPos = -1;
/*  44 */     int limit = in.limit();
/*     */ 
/*  46 */     for (int i = beginPos; i < limit; i++) {
/*  47 */       byte b = in.get(i);
/*  48 */       if (isTerminator(b)) {
/*  49 */         terminatorPos = i;
/*  50 */         break;
/*     */       }
/*     */     }
/*     */ 
/*  54 */     if (terminatorPos >= 0)
/*     */     {
/*     */       IoBuffer product;
/*  57 */       if (beginPos < terminatorPos) {
/*  58 */         in.limit(terminatorPos);
/*     */         IoBuffer product;
/*  60 */         if (this.buffer == null) {
/*  61 */           product = in.slice();
/*     */         } else {
/*  63 */           this.buffer.put(in);
/*  64 */           IoBuffer product = this.buffer.flip();
/*  65 */           this.buffer = null;
/*     */         }
/*     */ 
/*  68 */         in.limit(limit);
/*     */       }
/*     */       else
/*     */       {
/*     */         IoBuffer product;
/*  71 */         if (this.buffer == null) {
/*  72 */           product = IoBuffer.allocate(0);
/*     */         } else {
/*  74 */           product = this.buffer.flip();
/*  75 */           this.buffer = null;
/*     */         }
/*     */       }
/*  78 */       in.position(terminatorPos + 1);
/*  79 */       return finishDecode(product, out);
/*     */     }
/*  81 */     if (this.buffer == null) {
/*  82 */       this.buffer = IoBuffer.allocate(in.remaining());
/*  83 */       this.buffer.setAutoExpand(true);
/*     */     }
/*  85 */     this.buffer.put(in);
/*  86 */     return this;
/*     */   }
/*     */ 
/*     */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*     */     IoBuffer product;
/*     */     IoBuffer product;
/*  97 */     if (this.buffer == null) {
/*  98 */       product = IoBuffer.allocate(0);
/*     */     } else {
/* 100 */       product = this.buffer.flip();
/* 101 */       this.buffer = null;
/*     */     }
/* 103 */     return finishDecode(product, out);
/*     */   }
/*     */ 
/*     */   protected abstract boolean isTerminator(byte paramByte);
/*     */ 
/*     */   protected abstract DecodingState finishDecode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*     */     throws Exception;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ConsumeToDynamicTerminatorDecodingState
 * JD-Core Version:    0.6.0
 */
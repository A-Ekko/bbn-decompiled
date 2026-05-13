/*     */ package org.apache.mina.filter.codec.statemachine;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ 
/*     */ public abstract class ConsumeToTerminatorDecodingState
/*     */   implements DecodingState
/*     */ {
/*     */   private final byte terminator;
/*     */   private IoBuffer buffer;
/*     */ 
/*     */   public ConsumeToTerminatorDecodingState(byte terminator)
/*     */   {
/*  44 */     this.terminator = terminator;
/*     */   }
/*     */ 
/*     */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*  52 */     int terminatorPos = in.indexOf(this.terminator);
/*     */ 
/*  54 */     if (terminatorPos >= 0) {
/*  55 */       int limit = in.limit();
/*     */       IoBuffer product;
/*  58 */       if (in.position() < terminatorPos) {
/*  59 */         in.limit(terminatorPos);
/*     */         IoBuffer product;
/*  61 */         if (this.buffer == null) {
/*  62 */           product = in.slice();
/*     */         } else {
/*  64 */           this.buffer.put(in);
/*  65 */           IoBuffer product = this.buffer.flip();
/*  66 */           this.buffer = null;
/*     */         }
/*     */ 
/*  69 */         in.limit(limit);
/*     */       }
/*     */       else
/*     */       {
/*     */         IoBuffer product;
/*  72 */         if (this.buffer == null) {
/*  73 */           product = IoBuffer.allocate(0);
/*     */         } else {
/*  75 */           product = this.buffer.flip();
/*  76 */           this.buffer = null;
/*     */         }
/*     */       }
/*  79 */       in.position(terminatorPos + 1);
/*  80 */       return finishDecode(product, out);
/*     */     }
/*  82 */     if (this.buffer == null) {
/*  83 */       this.buffer = IoBuffer.allocate(in.remaining());
/*  84 */       this.buffer.setAutoExpand(true);
/*     */     }
/*  86 */     this.buffer.put(in);
/*  87 */     return this;
/*     */   }
/*     */ 
/*     */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*     */     IoBuffer product;
/*     */     IoBuffer product;
/*  98 */     if (this.buffer == null) {
/*  99 */       product = IoBuffer.allocate(0);
/*     */     } else {
/* 101 */       product = this.buffer.flip();
/* 102 */       this.buffer = null;
/*     */     }
/* 104 */     return finishDecode(product, out);
/*     */   }
/*     */ 
/*     */   protected abstract DecodingState finishDecode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*     */     throws Exception;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ConsumeToTerminatorDecodingState
 * JD-Core Version:    0.6.0
 */
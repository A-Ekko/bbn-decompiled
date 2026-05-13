/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class FixedLengthDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   private final int length;
/*    */   private IoBuffer buffer;
/*    */ 
/*    */   public FixedLengthDecodingState(int length)
/*    */   {
/* 46 */     this.length = length;
/*    */   }
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 54 */     if (this.buffer == null) {
/* 55 */       if (in.remaining() >= this.length) {
/* 56 */         int limit = in.limit();
/* 57 */         in.limit(in.position() + this.length);
/* 58 */         IoBuffer product = in.slice();
/* 59 */         in.position(in.position() + this.length);
/* 60 */         in.limit(limit);
/* 61 */         return finishDecode(product, out);
/*    */       }
/* 63 */       this.buffer = IoBuffer.allocate(this.length);
/* 64 */       this.buffer.put(in);
/* 65 */       return this;
/*    */     }
/*    */ 
/* 68 */     if (in.remaining() >= this.length - this.buffer.position()) {
/* 69 */       int limit = in.limit();
/* 70 */       in.limit(in.position() + this.length - this.buffer.position());
/* 71 */       this.buffer.put(in);
/* 72 */       in.limit(limit);
/* 73 */       IoBuffer product = this.buffer;
/* 74 */       this.buffer = null;
/* 75 */       return finishDecode(product.flip(), out);
/*    */     }
/* 77 */     this.buffer.put(in);
/* 78 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/*    */     IoBuffer readData;
/*    */     IoBuffer readData;
/* 89 */     if (this.buffer == null) {
/* 90 */       readData = IoBuffer.allocate(0);
/*    */     } else {
/* 92 */       readData = this.buffer.flip();
/* 93 */       this.buffer = null;
/*    */     }
/* 95 */     return finishDecode(readData, out);
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.FixedLengthDecodingState
 * JD-Core Version:    0.6.0
 */
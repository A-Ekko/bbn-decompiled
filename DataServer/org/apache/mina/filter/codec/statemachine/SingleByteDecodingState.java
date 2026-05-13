/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class SingleByteDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 36 */     if (in.hasRemaining()) {
/* 37 */       return finishDecode(in.get(), out);
/*    */     }
/* 39 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 48 */     throw new ProtocolDecoderException("Unexpected end of session while waiting for a single byte.");
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(byte paramByte, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.SingleByteDecodingState
 * JD-Core Version:    0.6.0
 */
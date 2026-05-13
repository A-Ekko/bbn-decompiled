/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class IntegerDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   private int firstByte;
/*    */   private int secondByte;
/*    */   private int thirdByte;
/*    */   private int counter;
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 45 */     while (in.hasRemaining()) {
/* 46 */       switch (this.counter) {
/*    */       case 0:
/* 48 */         this.firstByte = in.getUnsigned();
/* 49 */         break;
/*    */       case 1:
/* 51 */         this.secondByte = in.getUnsigned();
/* 52 */         break;
/*    */       case 2:
/* 54 */         this.thirdByte = in.getUnsigned();
/* 55 */         break;
/*    */       case 3:
/* 57 */         this.counter = 0;
/* 58 */         return finishDecode(this.firstByte << 24 | this.secondByte << 16 | this.thirdByte << 8 | in.getUnsigned(), out);
/*    */       default:
/* 62 */         throw new InternalError();
/*    */       }
/* 64 */       this.counter += 1;
/*    */     }
/*    */ 
/* 67 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 75 */     throw new ProtocolDecoderException("Unexpected end of session while waiting for an integer.");
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(int paramInt, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.IntegerDecodingState
 * JD-Core Version:    0.6.0
 */
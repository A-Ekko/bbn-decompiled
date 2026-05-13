/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class ShortIntegerDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   private int highByte;
/*    */   private int counter;
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 44 */     while (in.hasRemaining()) {
/* 45 */       switch (this.counter) {
/*    */       case 0:
/* 47 */         this.highByte = in.getUnsigned();
/* 48 */         break;
/*    */       case 1:
/* 50 */         this.counter = 0;
/* 51 */         return finishDecode((short)(this.highByte << 8 | in.getUnsigned()), out);
/*    */       default:
/* 53 */         throw new InternalError();
/*    */       }
/*    */ 
/* 56 */       this.counter += 1;
/*    */     }
/* 58 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 66 */     throw new ProtocolDecoderException("Unexpected end of session while waiting for a short integer.");
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(short paramShort, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ShortIntegerDecodingState
 * JD-Core Version:    0.6.0
 */
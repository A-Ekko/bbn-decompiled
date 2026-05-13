/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class SkippingState
/*    */   implements DecodingState
/*    */ {
/*    */   private int skippedBytes;
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 41 */     int beginPos = in.position();
/* 42 */     int limit = in.limit();
/* 43 */     for (int i = beginPos; i < limit; i++) {
/* 44 */       byte b = in.get(i);
/* 45 */       if (!canSkip(b)) {
/* 46 */         in.position(i);
/* 47 */         int answer = this.skippedBytes;
/* 48 */         this.skippedBytes = 0;
/* 49 */         return finishDecode(answer);
/*    */       }
/* 51 */       this.skippedBytes += 1;
/*    */     }
/*    */ 
/* 55 */     in.position(limit);
/* 56 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 64 */     return finishDecode(this.skippedBytes);
/*    */   }
/*    */ 
/*    */   protected abstract boolean canSkip(byte paramByte);
/*    */ 
/*    */   protected abstract DecodingState finishDecode(int paramInt)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.SkippingState
 * JD-Core Version:    0.6.0
 */
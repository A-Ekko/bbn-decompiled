/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class CrLfDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   private static final byte CR = 13;
/*    */   private static final byte LF = 10;
/*    */   private boolean hasCR;
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 56 */     boolean found = false;
/* 57 */     boolean finished = false;
/* 58 */     while (in.hasRemaining()) {
/* 59 */       byte b = in.get();
/* 60 */       if (!this.hasCR) {
/* 61 */         if (b == 13) {
/* 62 */           this.hasCR = true;
/*    */         } else {
/* 64 */           if (b == 10) {
/* 65 */             found = true;
/*    */           } else {
/* 67 */             in.position(in.position() - 1);
/* 68 */             found = false;
/*    */           }
/* 70 */           finished = true;
/* 71 */           break;
/*    */         }
/*    */       } else {
/* 74 */         if (b == 10) {
/* 75 */           found = true;
/* 76 */           finished = true;
/* 77 */           break;
/*    */         }
/* 79 */         throw new ProtocolDecoderException("Expected LF after CR but was: " + (b & 0xFF));
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 85 */     if (finished) {
/* 86 */       this.hasCR = false;
/* 87 */       return finishDecode(found, out);
/*    */     }
/* 89 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 98 */     return finishDecode(false, out);
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(boolean paramBoolean, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.CrLfDecodingState
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.filter.codec.statemachine;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract class ConsumeToEndOfSessionDecodingState
/*    */   implements DecodingState
/*    */ {
/*    */   private IoBuffer buffer;
/*    */   private final int maxLength;
/*    */ 
/*    */   public ConsumeToEndOfSessionDecodingState(int maxLength)
/*    */   {
/* 46 */     this.maxLength = maxLength;
/*    */   }
/*    */ 
/*    */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/* 54 */     if (this.buffer == null) {
/* 55 */       this.buffer = IoBuffer.allocate(256).setAutoExpand(true);
/*    */     }
/*    */ 
/* 58 */     if (this.buffer.position() + in.remaining() > this.maxLength) {
/* 59 */       throw new ProtocolDecoderException("Received data exceeds " + this.maxLength + " byte(s).");
/*    */     }
/* 61 */     this.buffer.put(in);
/* 62 */     return this;
/*    */   }
/*    */ 
/*    */   public DecodingState finishDecode(ProtocolDecoderOutput out)
/*    */     throws Exception
/*    */   {
/*    */     try
/*    */     {
/* 71 */       if (this.buffer == null) {
/* 72 */         this.buffer = IoBuffer.allocate(0);
/*    */       }
/* 74 */       this.buffer.flip();
/* 75 */       DecodingState localDecodingState = finishDecode(this.buffer, out);
/*    */       return localDecodingState; } finally { this.buffer = null; } throw localObject;
/*    */   }
/*    */ 
/*    */   protected abstract DecodingState finishDecode(IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.ConsumeToEndOfSessionDecodingState
 * JD-Core Version:    0.6.0
 */
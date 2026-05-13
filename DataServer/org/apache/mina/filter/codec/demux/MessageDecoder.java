/*    */ package org.apache.mina.filter.codec.demux;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*    */ 
/*    */ public abstract interface MessageDecoder
/*    */ {
/* 45 */   public static final MessageDecoderResult OK = MessageDecoderResult.OK;
/*    */ 
/* 52 */   public static final MessageDecoderResult NEED_DATA = MessageDecoderResult.NEED_DATA;
/*    */ 
/* 59 */   public static final MessageDecoderResult NOT_OK = MessageDecoderResult.NOT_OK;
/*    */ 
/*    */   public abstract MessageDecoderResult decodable(IoSession paramIoSession, IoBuffer paramIoBuffer);
/*    */ 
/*    */   public abstract MessageDecoderResult decode(IoSession paramIoSession, IoBuffer paramIoBuffer, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ 
/*    */   public abstract void finishDecode(IoSession paramIoSession, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*    */     throws Exception;
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.MessageDecoder
 * JD-Core Version:    0.6.0
 */
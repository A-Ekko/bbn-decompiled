/*    */ package com.pst.core.protocol;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*    */ 
/*    */ public class F002ProtocolEncoder extends ProtocolEncoderAdapter
/*    */ {
/*    */   public void encode(IoSession _session, Object _message, ProtocolEncoderOutput _feout)
/*    */   {
/* 12 */     byte[] messagebytes = (byte[])_message;
/* 13 */     IoBuffer bytebuff = IoBuffer.allocate(messagebytes.length);
/* 14 */     bytebuff.put(messagebytes);
/* 15 */     bytebuff.flip();
/* 16 */     _feout.write(bytebuff);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.core.protocol.F002ProtocolEncoder
 * JD-Core Version:    0.6.0
 */
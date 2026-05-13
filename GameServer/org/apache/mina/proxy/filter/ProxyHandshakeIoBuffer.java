/*    */ package org.apache.mina.proxy.filter;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.buffer.IoBufferWrapper;
/*    */ 
/*    */ public class ProxyHandshakeIoBuffer extends IoBufferWrapper
/*    */ {
/*    */   public ProxyHandshakeIoBuffer(IoBuffer buf)
/*    */   {
/* 35 */     super(buf);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.filter.ProxyHandshakeIoBuffer
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ import org.apache.mina.core.buffer.IoBuffer;
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class SynchronizedProtocolDecoder
/*    */   implements ProtocolDecoder
/*    */ {
/*    */   private final ProtocolDecoder decoder;
/*    */ 
/*    */   public SynchronizedProtocolDecoder(ProtocolDecoder decoder)
/*    */   {
/* 44 */     if (decoder == null) {
/* 45 */       throw new NullPointerException("decoder");
/*    */     }
/* 47 */     this.decoder = decoder;
/*    */   }
/*    */ 
/*    */   public ProtocolDecoder getDecoder()
/*    */   {
/* 54 */     return this.decoder;
/*    */   }
/*    */ 
/*    */   public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
/*    */   {
/* 59 */     synchronized (this.decoder) {
/* 60 */       this.decoder.decode(session, in, out);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception
/*    */   {
/* 66 */     synchronized (this.decoder) {
/* 67 */       this.decoder.finishDecode(session, out);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void dispose(IoSession session) throws Exception {
/* 72 */     synchronized (this.decoder) {
/* 73 */       this.decoder.dispose(session);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.SynchronizedProtocolDecoder
 * JD-Core Version:    0.6.0
 */
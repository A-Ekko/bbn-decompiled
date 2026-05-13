/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ 
/*    */ public class SynchronizedProtocolEncoder
/*    */   implements ProtocolEncoder
/*    */ {
/*    */   private final ProtocolEncoder encoder;
/*    */ 
/*    */   public SynchronizedProtocolEncoder(ProtocolEncoder encoder)
/*    */   {
/* 42 */     if (encoder == null) {
/* 43 */       throw new NullPointerException("encoder");
/*    */     }
/* 45 */     this.encoder = encoder;
/*    */   }
/*    */ 
/*    */   public ProtocolEncoder getEncoder()
/*    */   {
/* 52 */     return this.encoder;
/*    */   }
/*    */ 
/*    */   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
/*    */   {
/* 57 */     synchronized (this.encoder) {
/* 58 */       this.encoder.encode(session, message, out);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void dispose(IoSession session) throws Exception {
/* 63 */     synchronized (this.encoder) {
/* 64 */       this.encoder.dispose(session);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.SynchronizedProtocolEncoder
 * JD-Core Version:    0.6.0
 */
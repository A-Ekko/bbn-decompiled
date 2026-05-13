/*     */ package org.apache.mina.filter.codec;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.future.DefaultWriteFuture;
/*     */ import org.apache.mina.core.future.WriteFuture;
/*     */ import org.apache.mina.core.session.DummySession;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class ProtocolCodecSession extends DummySession
/*     */ {
/*  63 */   private final WriteFuture notWrittenFuture = DefaultWriteFuture.newNotWrittenFuture(this, new UnsupportedOperationException());
/*     */ 
/*  66 */   private final AbstractProtocolEncoderOutput encoderOutput = new AbstractProtocolEncoderOutput()
/*     */   {
/*     */     public WriteFuture flush() {
/*  69 */       return ProtocolCodecSession.this.notWrittenFuture;
/*     */     }
/*  66 */   };
/*     */ 
/*  73 */   private final AbstractProtocolDecoderOutput decoderOutput = new AbstractProtocolDecoderOutput() {
/*     */     public void flush(IoFilter.NextFilter nextFilter, IoSession session) {  } } ;
/*     */ 
/*     */   public ProtocolEncoderOutput getEncoderOutput()
/*     */   {
/*  90 */     return this.encoderOutput;
/*     */   }
/*     */ 
/*     */   public Queue<Object> getEncoderOutputQueue()
/*     */   {
/*  97 */     return this.encoderOutput.getMessageQueue();
/*     */   }
/*     */ 
/*     */   public ProtocolDecoderOutput getDecoderOutput()
/*     */   {
/* 105 */     return this.decoderOutput;
/*     */   }
/*     */ 
/*     */   public Queue<Object> getDecoderOutputQueue()
/*     */   {
/* 112 */     return this.decoderOutput.getMessageQueue();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolCodecSession
 * JD-Core Version:    0.6.0
 */
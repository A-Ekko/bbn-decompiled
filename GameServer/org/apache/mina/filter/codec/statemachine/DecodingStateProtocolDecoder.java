/*     */ package org.apache.mina.filter.codec.statemachine;
/*     */ 
/*     */ import java.util.Queue;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ import org.apache.mina.util.CircularQueue;
/*     */ 
/*     */ public class DecodingStateProtocolDecoder
/*     */   implements ProtocolDecoder
/*     */ {
/*     */   private final DecodingState state;
/*  43 */   private final Queue<IoBuffer> undecodedBuffers = new CircularQueue();
/*     */   private IoSession session;
/*     */ 
/*     */   public DecodingStateProtocolDecoder(DecodingState state)
/*     */   {
/*  54 */     if (state == null) {
/*  55 */       throw new NullPointerException("state");
/*     */     }
/*  57 */     this.state = state;
/*     */   }
/*     */ 
/*     */   public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/*  65 */     if (this.session == null)
/*  66 */       this.session = session;
/*  67 */     else if (this.session != session) {
/*  68 */       throw new IllegalStateException(getClass().getSimpleName() + " is a stateful decoder.  " + "You have to create one per session.");
/*     */     }
/*     */ 
/*  73 */     this.undecodedBuffers.offer(in);
/*     */     while (true) {
/*  75 */       IoBuffer b = (IoBuffer)this.undecodedBuffers.peek();
/*  76 */       if (b == null)
/*     */       {
/*     */         break;
/*     */       }
/*  80 */       int oldRemaining = b.remaining();
/*  81 */       this.state.decode(b, out);
/*  82 */       int newRemaining = b.remaining();
/*  83 */       if (newRemaining != 0) {
/*  84 */         if (oldRemaining == newRemaining) {
/*  85 */           throw new IllegalStateException(DecodingState.class.getSimpleName() + " must " + "consume at least one byte per decode().");
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*  90 */         this.undecodedBuffers.poll();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void finishDecode(IoSession session, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/* 100 */     this.state.finishDecode(out);
/*     */   }
/*     */ 
/*     */   public void dispose(IoSession session)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.DecodingStateProtocolDecoder
 * JD-Core Version:    0.6.0
 */
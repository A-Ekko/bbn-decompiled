/*     */ package org.apache.mina.filter.codec.statemachine;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class DecodingStateMachine
/*     */   implements DecodingState
/*     */ {
/*  52 */   private final Logger log = LoggerFactory.getLogger(DecodingStateMachine.class);
/*     */ 
/*  55 */   private final List<Object> childProducts = new ArrayList();
/*     */ 
/*  57 */   private final ProtocolDecoderOutput childOutput = new ProtocolDecoderOutput() {
/*     */     public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
/*     */     }
/*     */ 
/*     */     public void write(Object message) {
/*  62 */       DecodingStateMachine.this.childProducts.add(message);
/*     */     }
/*  57 */   };
/*     */   private DecodingState currentState;
/*     */   private boolean initialized;
/*     */ 
/*     */   protected abstract DecodingState init()
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract DecodingState finishDecode(List<Object> paramList, ProtocolDecoderOutput paramProtocolDecoderOutput)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract void destroy()
/*     */     throws Exception;
/*     */ 
/*     */   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/* 100 */     DecodingState state = getCurrentState();
/*     */ 
/* 102 */     int limit = in.limit();
/* 103 */     int pos = in.position();
/*     */     try
/*     */     {
/* 108 */       while (pos != limit)
/*     */       {
/* 112 */         oldState = state;
/* 113 */         state = state.decode(in, this.childOutput);
/*     */ 
/* 116 */         if (state == null) {
/* 117 */           DecodingState localDecodingState1 = finishDecode(this.childProducts, out);
/*     */           return localDecodingState1;
/*     */         }
/* 120 */         int newPos = in.position();
/*     */ 
/* 123 */         if ((newPos == pos) && (oldState == state)) {
/*     */           break;
/*     */         }
/* 126 */         pos = newPos;
/*     */       }
/*     */ 
/* 129 */       DecodingState oldState = this;
/*     */       return oldState;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 131 */       state = null;
/* 132 */       throw e;
/*     */     } finally {
/* 134 */       this.currentState = state;
/*     */ 
/* 137 */       if (state == null)
/* 138 */         cleanup(); 
/* 138 */     }throw localObject;
/*     */   }
/*     */   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
/* 149 */     DecodingState state = getCurrentState();
/*     */     DecodingState nextState;
/*     */     try {
/*     */       while (true) {
/* 152 */         DecodingState oldState = state;
/* 153 */         state = state.finishDecode(this.childOutput);
/* 154 */         if (state == null)
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/* 160 */         if (oldState == state)
/*     */           break;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 165 */       state = null;
/* 166 */       this.log.debug("Ignoring the exception caused by a closed session.", e);
/*     */     }
/*     */     finally {
/* 169 */       this.currentState = state;
/* 170 */       nextState = finishDecode(this.childProducts, out);
/* 171 */       if (state == null) {
/* 172 */         cleanup();
/*     */       }
/*     */     }
/* 175 */     return nextState;
/*     */   }
/*     */ 
/*     */   private void cleanup() {
/* 179 */     if (!this.initialized) {
/* 180 */       throw new IllegalStateException();
/*     */     }
/*     */ 
/* 183 */     this.initialized = false;
/* 184 */     this.childProducts.clear();
/*     */     try {
/* 186 */       destroy();
/*     */     } catch (Exception e2) {
/* 188 */       this.log.warn("Failed to destroy a decoding state machine.", e2);
/*     */     }
/*     */   }
/*     */ 
/*     */   private DecodingState getCurrentState() throws Exception {
/* 193 */     DecodingState state = this.currentState;
/* 194 */     if (state == null) {
/* 195 */       state = init();
/* 196 */       this.initialized = true;
/*     */     }
/* 198 */     return state;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.statemachine.DecodingStateMachine
 * JD-Core Version:    0.6.0
 */
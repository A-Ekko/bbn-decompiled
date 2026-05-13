/*     */ package org.apache.mina.filter.codec.demux;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ 
/*     */ public class DemuxingProtocolDecoder extends CumulativeProtocolDecoder
/*     */ {
/*  76 */   private final AttributeKey STATE = new AttributeKey(getClass(), "state");
/*     */ 
/*  78 */   private MessageDecoderFactory[] decoderFactories = new MessageDecoderFactory[0];
/*  79 */   private static final Class<?>[] EMPTY_PARAMS = new Class[0];
/*     */ 
/*     */   public void addMessageDecoder(Class<? extends MessageDecoder> decoderClass)
/*     */   {
/*  85 */     if (decoderClass == null) {
/*  86 */       throw new NullPointerException("decoderClass");
/*     */     }
/*     */     try
/*     */     {
/*  90 */       decoderClass.getConstructor(EMPTY_PARAMS);
/*     */     } catch (NoSuchMethodException e) {
/*  92 */       throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
/*     */     }
/*     */ 
/*  96 */     boolean registered = false;
/*  97 */     if (MessageDecoder.class.isAssignableFrom(decoderClass)) {
/*  98 */       addMessageDecoder(new DefaultConstructorMessageDecoderFactory(decoderClass, null));
/*  99 */       registered = true;
/*     */     }
/*     */ 
/* 102 */     if (!registered)
/* 103 */       throw new IllegalArgumentException("Unregisterable type: " + decoderClass);
/*     */   }
/*     */ 
/*     */   public void addMessageDecoder(MessageDecoder decoder)
/*     */   {
/* 109 */     addMessageDecoder(new SingletonMessageDecoderFactory(decoder, null));
/*     */   }
/*     */ 
/*     */   public void addMessageDecoder(MessageDecoderFactory factory) {
/* 113 */     if (factory == null) {
/* 114 */       throw new NullPointerException("factory");
/*     */     }
/* 116 */     MessageDecoderFactory[] decoderFactories = this.decoderFactories;
/* 117 */     MessageDecoderFactory[] newDecoderFactories = new MessageDecoderFactory[decoderFactories.length + 1];
/* 118 */     System.arraycopy(decoderFactories, 0, newDecoderFactories, 0, decoderFactories.length);
/*     */ 
/* 120 */     newDecoderFactories[decoderFactories.length] = factory;
/* 121 */     this.decoderFactories = newDecoderFactories;
/*     */   }
/*     */ 
/*     */   protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/* 127 */     State state = getState(session);
/*     */ 
/* 129 */     if (state.currentDecoder == null) {
/* 130 */       MessageDecoder[] decoders = state.decoders;
/* 131 */       int undecodables = 0;
/*     */ 
/* 133 */       for (int i = decoders.length - 1; i >= 0; i--) { MessageDecoder decoder = decoders[i];
/* 135 */         int limit = in.limit();
/* 136 */         int pos = in.position();
/*     */         MessageDecoderResult result;
/*     */         try { result = decoder.decodable(session, in);
/*     */         } finally {
/* 143 */           in.position(pos);
/* 144 */           in.limit(limit);
/*     */         }
/*     */ 
/* 147 */         if (result == MessageDecoder.OK) {
/* 148 */           State.access$202(state, decoder);
/* 149 */           break;
/* 150 */         }if (result == MessageDecoder.NOT_OK)
/* 151 */           undecodables++;
/* 152 */         else if (result != MessageDecoder.NEED_DATA) {
/* 153 */           throw new IllegalStateException("Unexpected decode result (see your decodable()): " + result);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 159 */       if (undecodables == decoders.length)
/*     */       {
/* 161 */         String dump = in.getHexDump();
/* 162 */         in.position(in.limit());
/* 163 */         ProtocolDecoderException e = new ProtocolDecoderException("No appropriate message decoder: " + dump);
/*     */ 
/* 165 */         e.setHexdump(dump);
/* 166 */         throw e;
/*     */       }
/*     */ 
/* 169 */       if (state.currentDecoder == null)
/*     */       {
/* 171 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 175 */     MessageDecoderResult result = state.currentDecoder.decode(session, in, out);
/*     */ 
/* 177 */     if (result == MessageDecoder.OK) {
/* 178 */       State.access$202(state, null);
/* 179 */       return true;
/* 180 */     }if (result == MessageDecoder.NEED_DATA)
/* 181 */       return false;
/* 182 */     if (result == MessageDecoder.NOT_OK) {
/* 183 */       State.access$202(state, null);
/* 184 */       throw new ProtocolDecoderException("Message decoder returned NOT_OK.");
/*     */     }
/*     */ 
/* 187 */     State.access$202(state, null);
/* 188 */     throw new IllegalStateException("Unexpected decode result (see your decode()): " + result);
/*     */   }
/*     */ 
/*     */   public void finishDecode(IoSession session, ProtocolDecoderOutput out)
/*     */     throws Exception
/*     */   {
/* 197 */     super.finishDecode(session, out);
/* 198 */     State state = getState(session);
/* 199 */     MessageDecoder currentDecoder = state.currentDecoder;
/* 200 */     if (currentDecoder == null) {
/* 201 */       return;
/*     */     }
/*     */ 
/* 204 */     currentDecoder.finishDecode(session, out);
/*     */   }
/*     */ 
/*     */   public void dispose(IoSession session) throws Exception
/*     */   {
/* 209 */     super.dispose(session);
/* 210 */     session.removeAttribute(this.STATE);
/*     */   }
/*     */ 
/*     */   private State getState(IoSession session) throws Exception {
/* 214 */     State state = (State)session.getAttribute(this.STATE);
/*     */ 
/* 216 */     if (state == null) {
/* 217 */       state = new State(null);
/* 218 */       State oldState = (State)session.setAttributeIfAbsent(this.STATE, state);
/*     */ 
/* 220 */       if (oldState != null) {
/* 221 */         state = oldState;
/*     */       }
/*     */     }
/*     */ 
/* 225 */     return state;
/*     */   }
/*     */ 
/*     */   private static class DefaultConstructorMessageDecoderFactory
/*     */     implements MessageDecoderFactory
/*     */   {
/*     */     private final Class<?> decoderClass;
/*     */ 
/*     */     private DefaultConstructorMessageDecoderFactory(Class<?> decoderClass)
/*     */     {
/* 262 */       if (decoderClass == null) {
/* 263 */         throw new NullPointerException("decoderClass");
/*     */       }
/*     */ 
/* 266 */       if (!MessageDecoder.class.isAssignableFrom(decoderClass)) {
/* 267 */         throw new IllegalArgumentException("decoderClass is not assignable to MessageDecoder");
/*     */       }
/*     */ 
/* 270 */       this.decoderClass = decoderClass;
/*     */     }
/*     */ 
/*     */     public MessageDecoder getDecoder() throws Exception {
/* 274 */       return (MessageDecoder)this.decoderClass.newInstance();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SingletonMessageDecoderFactory
/*     */     implements MessageDecoderFactory
/*     */   {
/*     */     private final MessageDecoder decoder;
/*     */ 
/*     */     private SingletonMessageDecoderFactory(MessageDecoder decoder)
/*     */     {
/* 246 */       if (decoder == null) {
/* 247 */         throw new NullPointerException("decoder");
/*     */       }
/* 249 */       this.decoder = decoder;
/*     */     }
/*     */ 
/*     */     public MessageDecoder getDecoder() {
/* 253 */       return this.decoder;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class State
/*     */   {
/*     */     private final MessageDecoder[] decoders;
/*     */     private MessageDecoder currentDecoder;
/*     */ 
/*     */     private State()
/*     */       throws Exception
/*     */     {
/* 233 */       MessageDecoderFactory[] decoderFactories = DemuxingProtocolDecoder.this.decoderFactories;
/* 234 */       this.decoders = new MessageDecoder[decoderFactories.length];
/* 235 */       for (int i = decoderFactories.length - 1; i >= 0; i--)
/* 236 */         this.decoders[i] = decoderFactories[i].getDecoder();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.DemuxingProtocolDecoder
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.codec.demux;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.core.session.UnknownMessageTypeException;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*     */ import org.apache.mina.util.CopyOnWriteMap;
/*     */ import org.apache.mina.util.IdentityHashSet;
/*     */ 
/*     */ public class DemuxingProtocolEncoder
/*     */   implements ProtocolEncoder
/*     */ {
/*  51 */   private final AttributeKey STATE = new AttributeKey(getClass(), "state");
/*     */ 
/*  53 */   private final Map<Class<?>, MessageEncoderFactory> type2encoderFactory = new CopyOnWriteMap();
/*     */ 
/*  56 */   private static final Class<?>[] EMPTY_PARAMS = new Class[0];
/*     */ 
/*     */   public void addMessageEncoder(Class<?> messageType, Class<? extends MessageEncoder> encoderClass)
/*     */   {
/*  63 */     if (encoderClass == null) {
/*  64 */       throw new NullPointerException("encoderClass");
/*     */     }
/*     */     try
/*     */     {
/*  68 */       encoderClass.getConstructor(EMPTY_PARAMS);
/*     */     } catch (NoSuchMethodException e) {
/*  70 */       throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
/*     */     }
/*     */ 
/*  74 */     boolean registered = false;
/*  75 */     if (MessageEncoder.class.isAssignableFrom(encoderClass)) {
/*  76 */       addMessageEncoder(messageType, new DefaultConstructorMessageEncoderFactory(encoderClass, null));
/*  77 */       registered = true;
/*     */     }
/*     */ 
/*  80 */     if (!registered)
/*  81 */       throw new IllegalArgumentException("Unregisterable type: " + encoderClass);
/*     */   }
/*     */ 
/*     */   public <T> void addMessageEncoder(Class<T> messageType, MessageEncoder<? super T> encoder)
/*     */   {
/*  88 */     addMessageEncoder(messageType, new SingletonMessageEncoderFactory(encoder, null));
/*     */   }
/*     */ 
/*     */   public <T> void addMessageEncoder(Class<T> messageType, MessageEncoderFactory<? super T> factory) {
/*  92 */     if (messageType == null) {
/*  93 */       throw new NullPointerException("messageType");
/*     */     }
/*     */ 
/*  96 */     if (factory == null) {
/*  97 */       throw new NullPointerException("factory");
/*     */     }
/*     */ 
/* 100 */     synchronized (this.type2encoderFactory) {
/* 101 */       if (this.type2encoderFactory.containsKey(messageType)) {
/* 102 */         throw new IllegalStateException("The specified message type (" + messageType.getName() + ") is registered already.");
/*     */       }
/*     */ 
/* 106 */       this.type2encoderFactory.put(messageType, factory);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addMessageEncoder(Iterable<Class<?>> messageTypes, Class<? extends MessageEncoder> encoderClass)
/*     */   {
/* 112 */     for (Class messageType : messageTypes)
/* 113 */       addMessageEncoder(messageType, encoderClass);
/*     */   }
/*     */ 
/*     */   public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoder<? super T> encoder)
/*     */   {
/* 118 */     for (Class messageType : messageTypes)
/* 119 */       addMessageEncoder(messageType, encoder);
/*     */   }
/*     */ 
/*     */   public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoderFactory<? super T> factory)
/*     */   {
/* 124 */     for (Class messageType : messageTypes)
/* 125 */       addMessageEncoder(messageType, factory);
/*     */   }
/*     */ 
/*     */   public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
/*     */     throws Exception
/*     */   {
/* 131 */     State state = getState(session);
/* 132 */     MessageEncoder encoder = findEncoder(state, message.getClass());
/* 133 */     if (encoder != null)
/* 134 */       encoder.encode(session, message, out);
/*     */     else
/* 136 */       throw new UnknownMessageTypeException("No message encoder found for message: " + message);
/*     */   }
/*     */ 
/*     */   protected MessageEncoder<Object> findEncoder(State state, Class<?> type)
/*     */   {
/* 142 */     return findEncoder(state, type, null);
/*     */   }
/*     */ 
/*     */   private MessageEncoder<Object> findEncoder(State state, Class type, Set<Class> triedClasses)
/*     */   {
/* 148 */     MessageEncoder encoder = null;
/*     */ 
/* 150 */     if ((triedClasses != null) && (triedClasses.contains(type))) {
/* 151 */       return null;
/*     */     }
/*     */ 
/* 157 */     encoder = (MessageEncoder)state.findEncoderCache.get(type);
/* 158 */     if (encoder != null) {
/* 159 */       return encoder;
/*     */     }
/*     */ 
/* 165 */     encoder = (MessageEncoder)state.type2encoder.get(type);
/*     */ 
/* 167 */     if (encoder == null)
/*     */     {
/* 172 */       if (triedClasses == null) {
/* 173 */         triedClasses = new IdentityHashSet();
/*     */       }
/* 175 */       triedClasses.add(type);
/*     */ 
/* 177 */       Class[] interfaces = type.getInterfaces();
/* 178 */       for (Class element : interfaces) {
/* 179 */         encoder = findEncoder(state, element, triedClasses);
/* 180 */         if (encoder != null)
/*     */         {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/* 186 */     if (encoder == null)
/*     */     {
/* 192 */       Class superclass = type.getSuperclass();
/* 193 */       if (superclass != null) {
/* 194 */         encoder = findEncoder(state, superclass);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 203 */     if (encoder != null) {
/* 204 */       state.findEncoderCache.put(type, encoder);
/*     */     }
/*     */ 
/* 207 */     return encoder;
/*     */   }
/*     */ 
/*     */   public void dispose(IoSession session) throws Exception {
/* 211 */     session.removeAttribute(this.STATE);
/*     */   }
/*     */ 
/*     */   private State getState(IoSession session) throws Exception {
/* 215 */     State state = (State)session.getAttribute(this.STATE);
/* 216 */     if (state == null) {
/* 217 */       state = new State(null);
/* 218 */       State oldState = (State)session.setAttributeIfAbsent(this.STATE, state);
/* 219 */       if (oldState != null) {
/* 220 */         state = oldState;
/*     */       }
/*     */     }
/* 223 */     return state;
/*     */   }
/*     */ 
/*     */   private static class DefaultConstructorMessageEncoderFactory<T>
/*     */     implements MessageEncoderFactory<T>
/*     */   {
/*     */     private final Class<MessageEncoder<T>> encoderClass;
/*     */ 
/*     */     private DefaultConstructorMessageEncoderFactory(Class<MessageEncoder<T>> encoderClass)
/*     */     {
/* 262 */       if (encoderClass == null) {
/* 263 */         throw new NullPointerException("encoderClass");
/*     */       }
/*     */ 
/* 266 */       if (!MessageEncoder.class.isAssignableFrom(encoderClass)) {
/* 267 */         throw new IllegalArgumentException("encoderClass is not assignable to MessageEncoder");
/*     */       }
/*     */ 
/* 270 */       this.encoderClass = encoderClass;
/*     */     }
/*     */ 
/*     */     public MessageEncoder<T> getEncoder() throws Exception {
/* 274 */       return (MessageEncoder)this.encoderClass.newInstance();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SingletonMessageEncoderFactory<T>
/*     */     implements MessageEncoderFactory<T>
/*     */   {
/*     */     private final MessageEncoder<T> encoder;
/*     */ 
/*     */     private SingletonMessageEncoderFactory(MessageEncoder<T> encoder)
/*     */     {
/* 246 */       if (encoder == null) {
/* 247 */         throw new NullPointerException("encoder");
/*     */       }
/* 249 */       this.encoder = encoder;
/*     */     }
/*     */ 
/*     */     public MessageEncoder<T> getEncoder() {
/* 253 */       return this.encoder;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class State
/*     */   {
/* 227 */     private final Map<Class<?>, MessageEncoder> findEncoderCache = new HashMap();
/*     */ 
/* 230 */     private final Map<Class<?>, MessageEncoder> type2encoder = new HashMap();
/*     */ 
/*     */     private State()
/*     */       throws Exception
/*     */     {
/* 235 */       for (Map.Entry e : DemuxingProtocolEncoder.this.type2encoderFactory.entrySet())
/* 236 */         this.type2encoder.put(e.getKey(), ((MessageEncoderFactory)e.getValue()).getEncoder());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.DemuxingProtocolEncoder
 * JD-Core Version:    0.6.0
 */
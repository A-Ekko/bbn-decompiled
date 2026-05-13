/*    */ package org.apache.mina.filter.codec.demux;
/*    */ 
/*    */ import org.apache.mina.core.session.IoSession;
/*    */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*    */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*    */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*    */ 
/*    */ public class DemuxingProtocolCodecFactory
/*    */   implements ProtocolCodecFactory
/*    */ {
/* 40 */   private final DemuxingProtocolEncoder encoder = new DemuxingProtocolEncoder();
/* 41 */   private final DemuxingProtocolDecoder decoder = new DemuxingProtocolDecoder();
/*    */ 
/*    */   public ProtocolEncoder getEncoder(IoSession session)
/*    */     throws Exception
/*    */   {
/* 47 */     return this.encoder;
/*    */   }
/*    */ 
/*    */   public ProtocolDecoder getDecoder(IoSession session) throws Exception {
/* 51 */     return this.decoder;
/*    */   }
/*    */ 
/*    */   public void addMessageEncoder(Class<?> messageType, Class<? extends MessageEncoder> encoderClass)
/*    */   {
/* 56 */     this.encoder.addMessageEncoder(messageType, encoderClass);
/*    */   }
/*    */ 
/*    */   public <T> void addMessageEncoder(Class<T> messageType, MessageEncoder<? super T> encoder) {
/* 60 */     this.encoder.addMessageEncoder(messageType, encoder);
/*    */   }
/*    */ 
/*    */   public <T> void addMessageEncoder(Class<T> messageType, MessageEncoderFactory<? super T> factory) {
/* 64 */     this.encoder.addMessageEncoder(messageType, factory);
/*    */   }
/*    */ 
/*    */   public void addMessageEncoder(Iterable<Class<?>> messageTypes, Class<? extends MessageEncoder> encoderClass)
/*    */   {
/* 69 */     for (Class messageType : messageTypes)
/* 70 */       addMessageEncoder(messageType, encoderClass);
/*    */   }
/*    */ 
/*    */   public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoder<? super T> encoder)
/*    */   {
/* 75 */     for (Class messageType : messageTypes)
/* 76 */       addMessageEncoder(messageType, encoder);
/*    */   }
/*    */ 
/*    */   public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoderFactory<? super T> factory)
/*    */   {
/* 81 */     for (Class messageType : messageTypes)
/* 82 */       addMessageEncoder(messageType, factory);
/*    */   }
/*    */ 
/*    */   public void addMessageDecoder(Class<? extends MessageDecoder> decoderClass)
/*    */   {
/* 87 */     this.decoder.addMessageDecoder(decoderClass);
/*    */   }
/*    */ 
/*    */   public void addMessageDecoder(MessageDecoder decoder) {
/* 91 */     this.decoder.addMessageDecoder(decoder);
/*    */   }
/*    */ 
/*    */   public void addMessageDecoder(MessageDecoderFactory factory) {
/* 95 */     this.decoder.addMessageDecoder(factory);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory
 * JD-Core Version:    0.6.0
 */
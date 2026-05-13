/*     */ package org.apache.mina.filter.codec.serialization;
/*     */ 
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*     */ 
/*     */ public class ObjectSerializationCodecFactory
/*     */   implements ProtocolCodecFactory
/*     */ {
/*     */   private final ObjectSerializationEncoder encoder;
/*     */   private final ObjectSerializationDecoder decoder;
/*     */ 
/*     */   public ObjectSerializationCodecFactory()
/*     */   {
/*  46 */     this(Thread.currentThread().getContextClassLoader());
/*     */   }
/*     */ 
/*     */   public ObjectSerializationCodecFactory(ClassLoader classLoader)
/*     */   {
/*  53 */     this.encoder = new ObjectSerializationEncoder();
/*  54 */     this.decoder = new ObjectSerializationDecoder(classLoader);
/*     */   }
/*     */ 
/*     */   public ProtocolEncoder getEncoder(IoSession session) {
/*  58 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   public ProtocolDecoder getDecoder(IoSession session) {
/*  62 */     return this.decoder;
/*     */   }
/*     */ 
/*     */   public int getEncoderMaxObjectSize()
/*     */   {
/*  74 */     return this.encoder.getMaxObjectSize();
/*     */   }
/*     */ 
/*     */   public void setEncoderMaxObjectSize(int maxObjectSize)
/*     */   {
/*  86 */     this.encoder.setMaxObjectSize(maxObjectSize);
/*     */   }
/*     */ 
/*     */   public int getDecoderMaxObjectSize()
/*     */   {
/*  98 */     return this.decoder.getMaxObjectSize();
/*     */   }
/*     */ 
/*     */   public void setDecoderMaxObjectSize(int maxObjectSize)
/*     */   {
/* 110 */     this.decoder.setMaxObjectSize(maxObjectSize);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory
 * JD-Core Version:    0.6.0
 */
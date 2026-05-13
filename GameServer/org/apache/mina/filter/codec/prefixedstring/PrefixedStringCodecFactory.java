/*     */ package org.apache.mina.filter.codec.prefixedstring;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*     */ 
/*     */ public class PrefixedStringCodecFactory
/*     */   implements ProtocolCodecFactory
/*     */ {
/*     */   private final PrefixedStringEncoder encoder;
/*     */   private final PrefixedStringDecoder decoder;
/*     */ 
/*     */   public PrefixedStringCodecFactory(Charset charset)
/*     */   {
/*  44 */     this.encoder = new PrefixedStringEncoder(charset);
/*  45 */     this.decoder = new PrefixedStringDecoder(charset);
/*     */   }
/*     */ 
/*     */   public PrefixedStringCodecFactory() {
/*  49 */     this(Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */   public int getEncoderMaxDataLength()
/*     */   {
/*  63 */     return this.encoder.getMaxDataLength();
/*     */   }
/*     */ 
/*     */   public void setEncoderMaxDataLength(int maxDataLength)
/*     */   {
/*  77 */     this.encoder.setMaxDataLength(maxDataLength);
/*     */   }
/*     */ 
/*     */   public int getDecoderMaxDataLength()
/*     */   {
/*  90 */     return this.decoder.getMaxDataLength();
/*     */   }
/*     */ 
/*     */   public void setDecoderMaxDataLength(int maxDataLength)
/*     */   {
/* 107 */     this.decoder.setMaxDataLength(maxDataLength);
/*     */   }
/*     */ 
/*     */   public void setDecoderPrefixLength(int prefixLength)
/*     */   {
/* 116 */     this.decoder.setPrefixLength(prefixLength);
/*     */   }
/*     */ 
/*     */   public int getDecoderPrefixLength()
/*     */   {
/* 125 */     return this.decoder.getPrefixLength();
/*     */   }
/*     */ 
/*     */   public void setEncoderPrefixLength(int prefixLength)
/*     */   {
/* 134 */     this.encoder.setPrefixLength(prefixLength);
/*     */   }
/*     */ 
/*     */   public int getEncoderPrefixLength()
/*     */   {
/* 143 */     return this.encoder.getPrefixLength();
/*     */   }
/*     */ 
/*     */   public ProtocolEncoder getEncoder(IoSession session) throws Exception {
/* 147 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   public ProtocolDecoder getDecoder(IoSession session) throws Exception {
/* 151 */     return this.decoder;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory
 * JD-Core Version:    0.6.0
 */
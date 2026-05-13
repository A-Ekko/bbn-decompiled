/*     */ package org.apache.mina.filter.codec.textline;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoder;
/*     */ 
/*     */ public class TextLineCodecFactory
/*     */   implements ProtocolCodecFactory
/*     */ {
/*     */   private final TextLineEncoder encoder;
/*     */   private final TextLineDecoder decoder;
/*     */ 
/*     */   public TextLineCodecFactory()
/*     */   {
/*  47 */     this(Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */   public TextLineCodecFactory(Charset charset)
/*     */   {
/*  59 */     this.encoder = new TextLineEncoder(charset, LineDelimiter.UNIX);
/*  60 */     this.decoder = new TextLineDecoder(charset, LineDelimiter.AUTO);
/*     */   }
/*     */ 
/*     */   public TextLineCodecFactory(Charset charset, String encodingDelimiter, String decodingDelimiter)
/*     */   {
/*  76 */     this.encoder = new TextLineEncoder(charset, encodingDelimiter);
/*  77 */     this.decoder = new TextLineDecoder(charset, decodingDelimiter);
/*     */   }
/*     */ 
/*     */   public TextLineCodecFactory(Charset charset, LineDelimiter encodingDelimiter, LineDelimiter decodingDelimiter)
/*     */   {
/*  93 */     this.encoder = new TextLineEncoder(charset, encodingDelimiter);
/*  94 */     this.decoder = new TextLineDecoder(charset, decodingDelimiter);
/*     */   }
/*     */ 
/*     */   public ProtocolEncoder getEncoder(IoSession session) {
/*  98 */     return this.encoder;
/*     */   }
/*     */ 
/*     */   public ProtocolDecoder getDecoder(IoSession session) {
/* 102 */     return this.decoder;
/*     */   }
/*     */ 
/*     */   public int getEncoderMaxLineLength()
/*     */   {
/* 114 */     return this.encoder.getMaxLineLength();
/*     */   }
/*     */ 
/*     */   public void setEncoderMaxLineLength(int maxLineLength)
/*     */   {
/* 126 */     this.encoder.setMaxLineLength(maxLineLength);
/*     */   }
/*     */ 
/*     */   public int getDecoderMaxLineLength()
/*     */   {
/* 138 */     return this.decoder.getMaxLineLength();
/*     */   }
/*     */ 
/*     */   public void setDecoderMaxLineLength(int maxLineLength)
/*     */   {
/* 150 */     this.decoder.setMaxLineLength(maxLineLength);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.textline.TextLineCodecFactory
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.codec.prefixedstring;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ 
/*     */ public class PrefixedStringDecoder extends CumulativeProtocolDecoder
/*     */ {
/*     */   public static final int DEFAULT_PREFIX_LENGTH = 4;
/*     */   public static final int DEFAULT_MAX_DATA_LENGTH = 2048;
/*     */   private final Charset charset;
/*  45 */   private int prefixLength = 4;
/*     */ 
/*  47 */   private int maxDataLength = 2048;
/*     */ 
/*     */   public PrefixedStringDecoder(Charset charset, int prefixLength, int maxDataLength)
/*     */   {
/*  55 */     this.charset = charset;
/*  56 */     this.prefixLength = prefixLength;
/*  57 */     this.maxDataLength = maxDataLength;
/*     */   }
/*     */ 
/*     */   public PrefixedStringDecoder(Charset charset, int prefixLength) {
/*  61 */     this(charset, prefixLength, 2048);
/*     */   }
/*     */ 
/*     */   public PrefixedStringDecoder(Charset charset) {
/*  65 */     this(charset, 4);
/*     */   }
/*     */ 
/*     */   public void setPrefixLength(int prefixLength)
/*     */   {
/*  74 */     this.prefixLength = prefixLength;
/*     */   }
/*     */ 
/*     */   public int getPrefixLength()
/*     */   {
/*  83 */     return this.prefixLength;
/*     */   }
/*     */ 
/*     */   public void setMaxDataLength(int maxDataLength)
/*     */   {
/*  98 */     this.maxDataLength = maxDataLength;
/*     */   }
/*     */ 
/*     */   public int getMaxDataLength()
/*     */   {
/* 107 */     return this.maxDataLength;
/*     */   }
/*     */ 
/*     */   protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
/* 111 */     if (in.prefixedDataAvailable(this.prefixLength, this.maxDataLength)) {
/* 112 */       String msg = in.getPrefixedString(this.prefixLength, this.charset.newDecoder());
/* 113 */       out.write(msg);
/* 114 */       return true;
/*     */     }
/* 116 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.prefixedstring.PrefixedStringDecoder
 * JD-Core Version:    0.6.0
 */
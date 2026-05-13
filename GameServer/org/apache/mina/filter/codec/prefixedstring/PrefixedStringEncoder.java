/*     */ package org.apache.mina.filter.codec.prefixedstring;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*     */ 
/*     */ public class PrefixedStringEncoder extends ProtocolEncoderAdapter
/*     */ {
/*     */   public static final int DEFAULT_PREFIX_LENGTH = 4;
/*     */   public static final int DEFAULT_MAX_DATA_LENGTH = 2048;
/*     */   private final Charset charset;
/*  45 */   private int prefixLength = 4;
/*     */ 
/*  47 */   private int maxDataLength = 2048;
/*     */ 
/*     */   public PrefixedStringEncoder(Charset charset, int prefixLength, int maxDataLength) {
/*  50 */     this.charset = charset;
/*  51 */     this.prefixLength = prefixLength;
/*  52 */     this.maxDataLength = maxDataLength;
/*     */   }
/*     */ 
/*     */   public PrefixedStringEncoder(Charset charset, int prefixLength) {
/*  56 */     this(charset, prefixLength, 2048);
/*     */   }
/*     */ 
/*     */   public PrefixedStringEncoder(Charset charset) {
/*  60 */     this(charset, 4);
/*     */   }
/*     */ 
/*     */   public PrefixedStringEncoder() {
/*  64 */     this(Charset.defaultCharset());
/*     */   }
/*     */ 
/*     */   public void setPrefixLength(int prefixLength)
/*     */   {
/*  73 */     if ((prefixLength != 1) && (prefixLength != 2) && (prefixLength != 4)) {
/*  74 */       throw new IllegalArgumentException("prefixLength: " + prefixLength);
/*     */     }
/*  76 */     this.prefixLength = prefixLength;
/*     */   }
/*     */ 
/*     */   public int getPrefixLength()
/*     */   {
/*  85 */     return this.prefixLength;
/*     */   }
/*     */ 
/*     */   public void setMaxDataLength(int maxDataLength)
/*     */   {
/* 100 */     this.maxDataLength = maxDataLength;
/*     */   }
/*     */ 
/*     */   public int getMaxDataLength()
/*     */   {
/* 109 */     return this.maxDataLength;
/*     */   }
/*     */ 
/*     */   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
/*     */   {
/* 114 */     String value = (String)message;
/* 115 */     IoBuffer buffer = IoBuffer.allocate(value.length()).setAutoExpand(true);
/* 116 */     buffer.putPrefixedString(value, this.prefixLength, this.charset.newEncoder());
/* 117 */     if (buffer.position() > this.maxDataLength) {
/* 118 */       throw new IllegalArgumentException("Data length: " + buffer.position());
/*     */     }
/* 120 */     buffer.flip();
/* 121 */     out.write(buffer);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.prefixedstring.PrefixedStringEncoder
 * JD-Core Version:    0.6.0
 */
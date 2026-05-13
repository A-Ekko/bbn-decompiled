/*     */ package org.apache.mina.filter.codec.textline;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
/*     */ import org.apache.mina.filter.codec.ProtocolEncoderOutput;
/*     */ 
/*     */ public class TextLineEncoder extends ProtocolEncoderAdapter
/*     */ {
/*  40 */   private final AttributeKey ENCODER = new AttributeKey(getClass(), "encoder");
/*     */   private final Charset charset;
/*     */   private final LineDelimiter delimiter;
/*  46 */   private int maxLineLength = 2147483647;
/*     */ 
/*     */   public TextLineEncoder()
/*     */   {
/*  53 */     this(Charset.defaultCharset(), LineDelimiter.UNIX);
/*     */   }
/*     */ 
/*     */   public TextLineEncoder(String delimiter)
/*     */   {
/*  61 */     this(new LineDelimiter(delimiter));
/*     */   }
/*     */ 
/*     */   public TextLineEncoder(LineDelimiter delimiter)
/*     */   {
/*  69 */     this(Charset.defaultCharset(), delimiter);
/*     */   }
/*     */ 
/*     */   public TextLineEncoder(Charset charset)
/*     */   {
/*  77 */     this(charset, LineDelimiter.UNIX);
/*     */   }
/*     */ 
/*     */   public TextLineEncoder(Charset charset, String delimiter)
/*     */   {
/*  85 */     this(charset, new LineDelimiter(delimiter));
/*     */   }
/*     */ 
/*     */   public TextLineEncoder(Charset charset, LineDelimiter delimiter)
/*     */   {
/*  93 */     if (charset == null) {
/*  94 */       throw new NullPointerException("charset");
/*     */     }
/*  96 */     if (delimiter == null) {
/*  97 */       throw new NullPointerException("delimiter");
/*     */     }
/*  99 */     if (LineDelimiter.AUTO.equals(delimiter)) {
/* 100 */       throw new IllegalArgumentException("AUTO delimiter is not allowed for encoder.");
/*     */     }
/*     */ 
/* 104 */     this.charset = charset;
/* 105 */     this.delimiter = delimiter;
/*     */   }
/*     */ 
/*     */   public int getMaxLineLength()
/*     */   {
/* 115 */     return this.maxLineLength;
/*     */   }
/*     */ 
/*     */   public void setMaxLineLength(int maxLineLength)
/*     */   {
/* 125 */     if (maxLineLength <= 0) {
/* 126 */       throw new IllegalArgumentException("maxLineLength: " + maxLineLength);
/*     */     }
/*     */ 
/* 130 */     this.maxLineLength = maxLineLength;
/*     */   }
/*     */ 
/*     */   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
/*     */   {
/* 135 */     CharsetEncoder encoder = (CharsetEncoder)session.getAttribute(this.ENCODER);
/* 136 */     if (encoder == null) {
/* 137 */       encoder = this.charset.newEncoder();
/* 138 */       session.setAttribute(this.ENCODER, encoder);
/*     */     }
/*     */ 
/* 141 */     String value = message.toString();
/* 142 */     IoBuffer buf = IoBuffer.allocate(value.length()).setAutoExpand(true);
/*     */ 
/* 144 */     buf.putString(value, encoder);
/* 145 */     if (buf.position() > this.maxLineLength) {
/* 146 */       throw new IllegalArgumentException("Line length: " + buf.position());
/*     */     }
/* 148 */     buf.putString(this.delimiter.getValue(), encoder);
/* 149 */     buf.flip();
/* 150 */     out.write(buf);
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.textline.TextLineEncoder
 * JD-Core Version:    0.6.0
 */
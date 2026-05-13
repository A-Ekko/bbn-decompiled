/*     */ package org.apache.mina.filter.codec.textline;
/*     */ 
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ import org.apache.mina.core.session.AttributeKey;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoder;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderException;
/*     */ import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/*     */ import org.apache.mina.filter.codec.RecoverableProtocolDecoderException;
/*     */ 
/*     */ public class TextLineDecoder
/*     */   implements ProtocolDecoder
/*     */ {
/*  42 */   private final AttributeKey CONTEXT = new AttributeKey(getClass(), "context");
/*     */   private final Charset charset;
/*     */   private final LineDelimiter delimiter;
/*     */   private IoBuffer delimBuf;
/*  50 */   private int maxLineLength = 1024;
/*     */ 
/*     */   public TextLineDecoder()
/*     */   {
/*  57 */     this(LineDelimiter.AUTO);
/*     */   }
/*     */ 
/*     */   public TextLineDecoder(String delimiter)
/*     */   {
/*  65 */     this(new LineDelimiter(delimiter));
/*     */   }
/*     */ 
/*     */   public TextLineDecoder(LineDelimiter delimiter)
/*     */   {
/*  73 */     this(Charset.defaultCharset(), delimiter);
/*     */   }
/*     */ 
/*     */   public TextLineDecoder(Charset charset)
/*     */   {
/*  81 */     this(charset, LineDelimiter.AUTO);
/*     */   }
/*     */ 
/*     */   public TextLineDecoder(Charset charset, String delimiter)
/*     */   {
/*  89 */     this(charset, new LineDelimiter(delimiter));
/*     */   }
/*     */ 
/*     */   public TextLineDecoder(Charset charset, LineDelimiter delimiter)
/*     */   {
/*  97 */     if (charset == null) {
/*  98 */       throw new NullPointerException("charset");
/*     */     }
/* 100 */     if (delimiter == null) {
/* 101 */       throw new NullPointerException("delimiter");
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
/*     */   public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
/*     */   {
/* 135 */     Context ctx = getContext(session);
/*     */ 
/* 137 */     if (LineDelimiter.AUTO.equals(this.delimiter))
/* 138 */       decodeAuto(ctx, session, in, out);
/*     */     else
/* 140 */       decodeNormal(ctx, session, in, out);
/*     */   }
/*     */ 
/*     */   private Context getContext(IoSession session)
/*     */   {
/* 146 */     Context ctx = (Context)session.getAttribute(this.CONTEXT);
/* 147 */     if (ctx == null) {
/* 148 */       ctx = new Context(null);
/* 149 */       session.setAttribute(this.CONTEXT, ctx);
/*     */     }
/* 151 */     return ctx;
/*     */   }
/*     */ 
/*     */   public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void dispose(IoSession session) throws Exception {
/* 159 */     Context ctx = (Context)session.getAttribute(this.CONTEXT);
/* 160 */     if (ctx != null)
/* 161 */       session.removeAttribute(this.CONTEXT);
/*     */   }
/*     */ 
/*     */   private void decodeAuto(Context ctx, IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws CharacterCodingException, ProtocolDecoderException
/*     */   {
/* 168 */     int matchCount = ctx.getMatchCount();
/*     */ 
/* 171 */     int oldPos = in.position();
/* 172 */     int oldLimit = in.limit();
/* 173 */     while (in.hasRemaining()) {
/* 174 */       byte b = in.get();
/* 175 */       boolean matched = false;
/* 176 */       switch (b)
/*     */       {
/*     */       case 13:
/* 180 */         matchCount++;
/* 181 */         break;
/*     */       case 10:
/* 184 */         matchCount++;
/* 185 */         matched = true;
/* 186 */         break;
/*     */       default:
/* 188 */         matchCount = 0;
/*     */       }
/*     */ 
/* 191 */       if (matched)
/*     */       {
/* 193 */         int pos = in.position();
/* 194 */         in.limit(pos);
/* 195 */         in.position(oldPos);
/*     */ 
/* 197 */         ctx.append(in);
/*     */ 
/* 199 */         in.limit(oldLimit);
/* 200 */         in.position(pos);
/*     */ 
/* 202 */         if (ctx.getOverflowPosition() == 0) {
/* 203 */           IoBuffer buf = ctx.getBuffer();
/* 204 */           buf.flip();
/* 205 */           buf.limit(buf.limit() - matchCount);
/*     */           try {
/* 207 */             writeText(session, buf.getString(ctx.getDecoder()), out);
/*     */           } finally {
/* 209 */             buf.clear();
/*     */           }
/*     */         } else {
/* 212 */           int overflowPosition = ctx.getOverflowPosition();
/* 213 */           ctx.reset();
/* 214 */           throw new RecoverableProtocolDecoderException("Line is too long: " + overflowPosition);
/*     */         }
/*     */ 
/* 218 */         oldPos = pos;
/* 219 */         matchCount = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 224 */     in.position(oldPos);
/* 225 */     ctx.append(in);
/*     */ 
/* 227 */     ctx.setMatchCount(matchCount);
/*     */   }
/*     */ 
/*     */   private void decodeNormal(Context ctx, IoSession session, IoBuffer in, ProtocolDecoderOutput out)
/*     */     throws CharacterCodingException, ProtocolDecoderException
/*     */   {
/* 233 */     int matchCount = ctx.getMatchCount();
/*     */ 
/* 236 */     if (this.delimBuf == null) {
/* 237 */       IoBuffer tmp = IoBuffer.allocate(2).setAutoExpand(true);
/* 238 */       tmp.putString(this.delimiter.getValue(), this.charset.newEncoder());
/* 239 */       tmp.flip();
/* 240 */       this.delimBuf = tmp;
/*     */     }
/*     */ 
/* 244 */     int oldPos = in.position();
/* 245 */     int oldLimit = in.limit();
/* 246 */     while (in.hasRemaining()) {
/* 247 */       byte b = in.get();
/* 248 */       if (this.delimBuf.get(matchCount) == b) {
/* 249 */         matchCount++;
/* 250 */         if (matchCount == this.delimBuf.limit())
/*     */         {
/* 252 */           int pos = in.position();
/* 253 */           in.limit(pos);
/* 254 */           in.position(oldPos);
/*     */ 
/* 256 */           ctx.append(in);
/*     */ 
/* 258 */           in.limit(oldLimit);
/* 259 */           in.position(pos);
/* 260 */           if (ctx.getOverflowPosition() == 0) {
/* 261 */             IoBuffer buf = ctx.getBuffer();
/* 262 */             buf.flip();
/* 263 */             buf.limit(buf.limit() - matchCount);
/*     */             try {
/* 265 */               writeText(session, buf.getString(ctx.getDecoder()), out);
/*     */             } finally {
/* 267 */               buf.clear();
/*     */             }
/*     */           } else {
/* 270 */             int overflowPosition = ctx.getOverflowPosition();
/* 271 */             ctx.reset();
/* 272 */             throw new RecoverableProtocolDecoderException("Line is too long: " + overflowPosition);
/*     */           }
/*     */ 
/* 276 */           oldPos = pos;
/* 277 */           matchCount = 0;
/*     */         }
/*     */       }
/*     */       else {
/* 281 */         in.position(Math.max(0, in.position() - matchCount));
/* 282 */         matchCount = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 287 */     in.position(oldPos);
/* 288 */     ctx.append(in);
/*     */ 
/* 290 */     ctx.setMatchCount(matchCount);
/*     */   }
/*     */ 
/*     */   protected void writeText(IoSession session, String text, ProtocolDecoderOutput out)
/*     */   {
/* 303 */     out.write(text);
/*     */   }
/*     */ 
/*     */   private class Context
/*     */   {
/*     */     private final CharsetDecoder decoder;
/*     */     private final IoBuffer buf;
/* 309 */     private int matchCount = 0;
/* 310 */     private int overflowPosition = 0;
/*     */ 
/*     */     private Context() {
/* 313 */       this.decoder = TextLineDecoder.this.charset.newDecoder();
/* 314 */       this.buf = IoBuffer.allocate(80).setAutoExpand(true);
/*     */     }
/*     */ 
/*     */     public CharsetDecoder getDecoder() {
/* 318 */       return this.decoder;
/*     */     }
/*     */ 
/*     */     public IoBuffer getBuffer() {
/* 322 */       return this.buf;
/*     */     }
/*     */ 
/*     */     public int getOverflowPosition() {
/* 326 */       return this.overflowPosition;
/*     */     }
/*     */ 
/*     */     public int getMatchCount() {
/* 330 */       return this.matchCount;
/*     */     }
/*     */ 
/*     */     public void setMatchCount(int matchCount) {
/* 334 */       this.matchCount = matchCount;
/*     */     }
/*     */ 
/*     */     public void reset() {
/* 338 */       this.overflowPosition = 0;
/* 339 */       this.matchCount = 0;
/* 340 */       this.decoder.reset();
/*     */     }
/*     */ 
/*     */     public void append(IoBuffer in) {
/* 344 */       if (this.overflowPosition != 0) {
/* 345 */         discard(in);
/* 346 */       } else if (this.buf.position() > TextLineDecoder.this.maxLineLength - in.remaining()) {
/* 347 */         this.overflowPosition = this.buf.position();
/* 348 */         this.buf.clear();
/* 349 */         discard(in);
/*     */       } else {
/* 351 */         getBuffer().put(in);
/*     */       }
/*     */     }
/*     */ 
/*     */     private void discard(IoBuffer in) {
/* 356 */       if (2147483647 - in.remaining() < this.overflowPosition)
/* 357 */         this.overflowPosition = 2147483647;
/*     */       else {
/* 359 */         this.overflowPosition += in.remaining();
/*     */       }
/* 361 */       in.position(in.limit());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.textline.TextLineDecoder
 * JD-Core Version:    0.6.0
 */
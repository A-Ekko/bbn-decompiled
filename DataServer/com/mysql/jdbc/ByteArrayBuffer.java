/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ class ByteArrayBuffer extends Buffer
/*     */ {
/*  41 */   private int bufLength = 0;
/*     */   private byte[] byteBuffer;
/*  45 */   private int position = 0;
/*     */ 
/*     */   ByteArrayBuffer(byte[] buf) {
/*  48 */     this.byteBuffer = buf;
/*  49 */     setBufLength(buf.length);
/*     */   }
/*     */ 
/*     */   ByteArrayBuffer(int size) {
/*  53 */     this.byteBuffer = new byte[size];
/*  54 */     setBufLength(this.byteBuffer.length);
/*  55 */     this.position = 4;
/*     */   }
/*     */ 
/*     */   final void clear() {
/*  59 */     this.position = 4;
/*     */   }
/*     */ 
/*     */   final void ensureCapacity(int additionalData) throws SQLException {
/*  63 */     if (this.position + additionalData > getBufLength())
/*  64 */       if (this.position + additionalData < this.byteBuffer.length)
/*     */       {
/*  70 */         setBufLength(this.byteBuffer.length);
/*     */       }
/*     */       else
/*     */       {
/*  76 */         int newLength = (int)(this.byteBuffer.length * 1.25D);
/*     */ 
/*  78 */         if (newLength < this.byteBuffer.length + additionalData) {
/*  79 */           newLength = this.byteBuffer.length + (int)(additionalData * 1.25D);
/*     */         }
/*     */ 
/*  83 */         if (newLength < this.byteBuffer.length) {
/*  84 */           newLength = this.byteBuffer.length + additionalData;
/*     */         }
/*     */ 
/*  87 */         byte[] newBytes = new byte[newLength];
/*     */ 
/*  89 */         System.arraycopy(this.byteBuffer, 0, newBytes, 0, this.byteBuffer.length);
/*     */ 
/*  91 */         this.byteBuffer = newBytes;
/*  92 */         setBufLength(this.byteBuffer.length);
/*     */       }
/*     */   }
/*     */ 
/*     */   public int fastSkipLenString()
/*     */   {
/* 103 */     long len = readFieldLength();
/*     */ 
/* 105 */     this.position = (int)(this.position + len);
/*     */ 
/* 107 */     return (int)len;
/*     */   }
/*     */ 
/*     */   protected final byte[] getBufferSource() {
/* 111 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */   int getBufLength() {
/* 115 */     return this.bufLength;
/*     */   }
/*     */ 
/*     */   public byte[] getByteBuffer()
/*     */   {
/* 124 */     return this.byteBuffer;
/*     */   }
/*     */ 
/*     */   final byte[] getBytes(int len) {
/* 128 */     byte[] b = new byte[len];
/* 129 */     System.arraycopy(this.byteBuffer, this.position, b, 0, len);
/* 130 */     this.position += len;
/*     */ 
/* 132 */     return b;
/*     */   }
/*     */ 
/*     */   byte[] getBytes(int offset, int len)
/*     */   {
/* 141 */     byte[] dest = new byte[len];
/* 142 */     System.arraycopy(this.byteBuffer, offset, dest, 0, len);
/*     */ 
/* 144 */     return dest;
/*     */   }
/*     */ 
/*     */   int getCapacity() {
/* 148 */     return this.byteBuffer.length;
/*     */   }
/*     */ 
/*     */   public ByteBuffer getNioBuffer() {
/* 152 */     throw new IllegalArgumentException(Messages.getString("ByteArrayBuffer.0"));
/*     */   }
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 162 */     return this.position;
/*     */   }
/*     */ 
/*     */   final boolean isLastDataPacket()
/*     */   {
/* 167 */     return (getBufLength() < 9) && ((this.byteBuffer[0] & 0xFF) == 254);
/*     */   }
/*     */ 
/*     */   final long newReadLength() {
/* 171 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 173 */     switch (sw) {
/*     */     case 251:
/* 175 */       return 0L;
/*     */     case 252:
/* 178 */       return readInt();
/*     */     case 253:
/* 181 */       return readLongInt();
/*     */     case 254:
/* 184 */       return readLongLong();
/*     */     }
/*     */ 
/* 187 */     return sw;
/*     */   }
/*     */ 
/*     */   final byte readByte()
/*     */   {
/* 192 */     return this.byteBuffer[(this.position++)];
/*     */   }
/*     */ 
/*     */   final byte readByte(int readAt) {
/* 196 */     return this.byteBuffer[readAt];
/*     */   }
/*     */ 
/*     */   final long readFieldLength() {
/* 200 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 202 */     switch (sw) {
/*     */     case 251:
/* 204 */       return -1L;
/*     */     case 252:
/* 207 */       return readInt();
/*     */     case 253:
/* 210 */       return readLongInt();
/*     */     case 254:
/* 213 */       return readLongLong();
/*     */     }
/*     */ 
/* 216 */     return sw;
/*     */   }
/*     */ 
/*     */   final int readInt()
/*     */   {
/* 222 */     byte[] b = this.byteBuffer;
/*     */ 
/* 224 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8;
/*     */   }
/*     */ 
/*     */   final int readIntAsLong() {
/* 228 */     byte[] b = this.byteBuffer;
/*     */ 
/* 230 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   final byte[] readLenByteArray(int offset)
/*     */   {
/* 236 */     long len = readFieldLength();
/*     */ 
/* 238 */     if (len == -1L) {
/* 239 */       return null;
/*     */     }
/*     */ 
/* 242 */     if (len == 0L) {
/* 243 */       return Constants.EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 246 */     this.position += offset;
/*     */ 
/* 248 */     return getBytes((int)len);
/*     */   }
/*     */ 
/*     */   final long readLength() {
/* 252 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 254 */     switch (sw) {
/*     */     case 251:
/* 256 */       return 0L;
/*     */     case 252:
/* 259 */       return readInt();
/*     */     case 253:
/* 262 */       return readLongInt();
/*     */     case 254:
/* 265 */       return readLong();
/*     */     }
/*     */ 
/* 268 */     return sw;
/*     */   }
/*     */ 
/*     */   final long readLong()
/*     */   {
/* 274 */     byte[] b = this.byteBuffer;
/*     */ 
/* 276 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24;
/*     */   }
/*     */ 
/*     */   final int readLongInt()
/*     */   {
/* 283 */     byte[] b = this.byteBuffer;
/*     */ 
/* 285 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16;
/*     */   }
/*     */ 
/*     */   final long readLongLong()
/*     */   {
/* 291 */     byte[] b = this.byteBuffer;
/*     */ 
/* 293 */     return b[(this.position++)] & 0xFF | (b[(this.position++)] & 0xFF) << 8 | (b[(this.position++)] & 0xFF) << 16 | (b[(this.position++)] & 0xFF) << 24 | (b[(this.position++)] & 0xFF) << 32 | (b[(this.position++)] & 0xFF) << 40 | (b[(this.position++)] & 0xFF) << 48 | (b[(this.position++)] & 0xFF) << 56;
/*     */   }
/*     */ 
/*     */   final int readnBytes()
/*     */   {
/* 304 */     int sw = this.byteBuffer[(this.position++)] & 0xFF;
/*     */ 
/* 306 */     switch (sw) {
/*     */     case 1:
/* 308 */       return this.byteBuffer[(this.position++)] & 0xFF;
/*     */     case 2:
/* 311 */       return readInt();
/*     */     case 3:
/* 314 */       return readLongInt();
/*     */     case 4:
/* 317 */       return (int)readLong();
/*     */     }
/*     */ 
/* 320 */     return 255;
/*     */   }
/*     */ 
/*     */   final String readString()
/*     */   {
/* 331 */     int i = this.position;
/* 332 */     int len = 0;
/* 333 */     int maxLen = getBufLength();
/*     */ 
/* 335 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 336 */       len++;
/* 337 */       i++;
/*     */     }
/*     */ 
/* 340 */     String s = new String(this.byteBuffer, this.position, len);
/* 341 */     this.position += len + 1;
/*     */ 
/* 343 */     return s;
/*     */   }
/*     */ 
/*     */   final String readString(String encoding) throws SQLException {
/* 347 */     int i = this.position;
/* 348 */     int len = 0;
/* 349 */     int maxLen = getBufLength();
/*     */ 
/* 351 */     while ((i < maxLen) && (this.byteBuffer[i] != 0)) {
/* 352 */       len++;
/* 353 */       i++;
/*     */     }
/*     */     try
/*     */     {
/* 357 */       String str = new String(this.byteBuffer, this.position, len, encoding);
/*     */       return str;
/*     */     }
/*     */     catch (UnsupportedEncodingException uEE)
/*     */     {
/* 359 */       throw new SQLException(Messages.getString("ByteArrayBuffer.1") + encoding + "'", "S1009");
/*     */     }
/*     */     finally {
/* 362 */       this.position += len + 1; } throw localObject;
/*     */   }
/*     */ 
/*     */   void setBufLength(int bufLengthToSet)
/*     */   {
/* 367 */     this.bufLength = bufLengthToSet;
/*     */   }
/*     */ 
/*     */   public void setByteBuffer(byte[] byteBufferToSet)
/*     */   {
/* 377 */     this.byteBuffer = byteBufferToSet;
/*     */   }
/*     */ 
/*     */   public void setPosition(int positionToSet)
/*     */   {
/* 387 */     this.position = positionToSet;
/*     */   }
/*     */ 
/*     */   final void writeByte(byte b) throws SQLException {
/* 391 */     ensureCapacity(1);
/*     */ 
/* 393 */     this.byteBuffer[(this.position++)] = b;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes) throws SQLException
/*     */   {
/* 398 */     int len = bytes.length;
/* 399 */     ensureCapacity(len);
/* 400 */     System.arraycopy(bytes, 0, this.byteBuffer, this.position, len);
/* 401 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 407 */     ensureCapacity(length);
/* 408 */     System.arraycopy(bytes, offset, this.byteBuffer, this.position, length);
/* 409 */     this.position += length;
/*     */   }
/*     */ 
/*     */   final void writeDouble(double d) throws SQLException {
/* 413 */     long l = Double.doubleToLongBits(d);
/* 414 */     writeLongLong(l);
/*     */   }
/*     */ 
/*     */   final void writeFieldLength(long length) throws SQLException {
/* 418 */     if (length < 251L) {
/* 419 */       writeByte((byte)(int)length);
/* 420 */     } else if (length < 65536L) {
/* 421 */       ensureCapacity(3);
/* 422 */       writeByte(-4);
/* 423 */       writeInt((int)length);
/* 424 */     } else if (length < 16777216L) {
/* 425 */       ensureCapacity(4);
/* 426 */       writeByte(-3);
/* 427 */       writeLongInt((int)length);
/*     */     } else {
/* 429 */       ensureCapacity(9);
/* 430 */       writeByte(-2);
/* 431 */       writeLongLong(length);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeFloat(float f) throws SQLException {
/* 436 */     ensureCapacity(4);
/*     */ 
/* 438 */     int i = Float.floatToIntBits(f);
/* 439 */     byte[] b = this.byteBuffer;
/* 440 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 441 */     b[(this.position++)] = (byte)(i >>> 8);
/* 442 */     b[(this.position++)] = (byte)(i >>> 16);
/* 443 */     b[(this.position++)] = (byte)(i >>> 24);
/*     */   }
/*     */ 
/*     */   final void writeInt(int i) throws SQLException
/*     */   {
/* 448 */     ensureCapacity(2);
/*     */ 
/* 450 */     byte[] b = this.byteBuffer;
/* 451 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 452 */     b[(this.position++)] = (byte)(i >>> 8);
/*     */   }
/*     */ 
/*     */   final void writeLenBytes(byte[] b)
/*     */     throws SQLException
/*     */   {
/* 458 */     int len = b.length;
/* 459 */     ensureCapacity(len + 9);
/* 460 */     writeFieldLength(len);
/* 461 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 462 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 470 */     byte[] b = null;
/*     */ 
/* 472 */     if (converter != null)
/* 473 */       b = converter.toBytes(s);
/*     */     else {
/* 475 */       b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode);
/*     */     }
/*     */ 
/* 479 */     int len = b.length;
/* 480 */     ensureCapacity(len + 9);
/* 481 */     writeFieldLength(len);
/* 482 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 483 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeLong(long i) throws SQLException
/*     */   {
/* 488 */     ensureCapacity(4);
/*     */ 
/* 490 */     byte[] b = this.byteBuffer;
/* 491 */     b[(this.position++)] = (byte)(int)(i & 0xFF);
/* 492 */     b[(this.position++)] = (byte)(int)(i >>> 8);
/* 493 */     b[(this.position++)] = (byte)(int)(i >>> 16);
/* 494 */     b[(this.position++)] = (byte)(int)(i >>> 24);
/*     */   }
/*     */ 
/*     */   final void writeLongInt(int i) throws SQLException
/*     */   {
/* 499 */     ensureCapacity(3);
/* 500 */     byte[] b = this.byteBuffer;
/* 501 */     b[(this.position++)] = (byte)(i & 0xFF);
/* 502 */     b[(this.position++)] = (byte)(i >>> 8);
/* 503 */     b[(this.position++)] = (byte)(i >>> 16);
/*     */   }
/*     */ 
/*     */   final void writeLongLong(long i) throws SQLException {
/* 507 */     ensureCapacity(8);
/* 508 */     byte[] b = this.byteBuffer;
/* 509 */     b[(this.position++)] = (byte)(int)(i & 0xFF);
/* 510 */     b[(this.position++)] = (byte)(int)(i >>> 8);
/* 511 */     b[(this.position++)] = (byte)(int)(i >>> 16);
/* 512 */     b[(this.position++)] = (byte)(int)(i >>> 24);
/* 513 */     b[(this.position++)] = (byte)(int)(i >>> 32);
/* 514 */     b[(this.position++)] = (byte)(int)(i >>> 40);
/* 515 */     b[(this.position++)] = (byte)(int)(i >>> 48);
/* 516 */     b[(this.position++)] = (byte)(int)(i >>> 56);
/*     */   }
/*     */ 
/*     */   final void writeString(String s) throws SQLException
/*     */   {
/* 521 */     ensureCapacity(s.length() * 2 + 1);
/* 522 */     writeStringNoNull(s);
/* 523 */     this.byteBuffer[(this.position++)] = 0;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s) throws SQLException
/*     */   {
/* 528 */     int len = s.length();
/* 529 */     ensureCapacity(len * 2);
/* 530 */     System.arraycopy(s.getBytes(), 0, this.byteBuffer, this.position, len);
/* 531 */     this.position += len;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 544 */     byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode);
/*     */ 
/* 547 */     int len = b.length;
/* 548 */     ensureCapacity(len);
/* 549 */     System.arraycopy(b, 0, this.byteBuffer, this.position, len);
/* 550 */     this.position += len;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.ByteArrayBuffer
 * JD-Core Version:    0.6.0
 */
/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ class ChannelBuffer extends Buffer
/*     */ {
/*  40 */   private byte[] asBytes = null;
/*     */ 
/*  44 */   private int bufLength = 0;
/*     */   private ByteBuffer directBuffer;
/*  48 */   private boolean dirty = true;
/*     */ 
/*     */   ChannelBuffer(byte[] buf) {
/*  51 */     this.directBuffer = ByteBuffer.wrap(buf);
/*  52 */     setBufLength(buf.length);
/*     */   }
/*     */ 
/*     */   ChannelBuffer(int size, boolean direct)
/*     */   {
/*  57 */     if (direct)
/*  58 */       this.directBuffer = ByteBuffer.allocateDirect(size);
/*     */     else {
/*  60 */       this.directBuffer = ByteBuffer.allocate(size);
/*     */     }
/*     */ 
/*  64 */     setBufLength(size);
/*     */ 
/*  66 */     this.directBuffer.position(4);
/*     */   }
/*     */ 
/*     */   private byte[] bufferToArray() {
/*  70 */     if (!this.dirty)
/*  71 */       return this.asBytes;
/*  72 */     if (this.directBuffer.hasArray()) {
/*  73 */       this.asBytes = this.directBuffer.array();
/*  74 */       this.dirty = false;
/*     */ 
/*  76 */       return this.asBytes;
/*     */     }
/*  78 */     int bufferLength = this.directBuffer.limit();
/*     */ 
/*  80 */     this.asBytes = new byte[bufferLength];
/*     */ 
/*  82 */     int oldPosition = getPosition();
/*     */ 
/*  84 */     this.directBuffer.position(0);
/*  85 */     this.directBuffer.get(this.asBytes, 0, bufferLength);
/*  86 */     this.directBuffer.position(oldPosition);
/*  87 */     this.dirty = false;
/*     */ 
/*  89 */     return this.asBytes;
/*     */   }
/*     */ 
/*     */   final void clear()
/*     */   {
/*  94 */     this.directBuffer.position(4);
/*     */   }
/*     */ 
/*     */   final void ensureCapacity(int additionalData) throws SQLException {
/*  98 */     int bufferCapacity = this.directBuffer.capacity();
/*     */ 
/* 100 */     int currentPosition = this.directBuffer.position();
/*     */ 
/* 102 */     if (currentPosition + additionalData > getBufLength())
/* 103 */       if (currentPosition + additionalData < bufferCapacity)
/*     */       {
/* 109 */         setBufLength(currentPosition + additionalData);
/*     */       }
/*     */       else
/*     */       {
/* 116 */         int newLength = (int)(bufferCapacity * 1.25D);
/*     */ 
/* 118 */         if (newLength < 4096) {
/* 119 */           newLength = 4096;
/*     */         }
/*     */ 
/* 122 */         if (newLength < bufferCapacity + additionalData) {
/* 123 */           newLength = bufferCapacity + (int)(additionalData * 1.25D);
/*     */         }
/*     */ 
/* 126 */         if (newLength < bufferCapacity) {
/* 127 */           newLength = bufferCapacity + additionalData;
/*     */         }
/*     */ 
/* 130 */         ByteBuffer largerBuffer = ByteBuffer.allocateDirect(newLength);
/*     */ 
/* 132 */         this.directBuffer.position(0);
/* 133 */         largerBuffer.put(this.directBuffer);
/* 134 */         this.directBuffer = largerBuffer;
/* 135 */         this.directBuffer.position(currentPosition);
/*     */ 
/* 137 */         bufferCapacity = this.directBuffer.capacity();
/* 138 */         setBufLength(bufferCapacity);
/*     */       }
/*     */   }
/*     */ 
/*     */   public int fastSkipLenString()
/*     */   {
/* 150 */     long len = readFieldLength();
/*     */ 
/* 154 */     this.directBuffer.position((int)(this.directBuffer.position() + len));
/*     */ 
/* 156 */     return (int)len;
/*     */   }
/*     */ 
/*     */   int getBufLength() {
/* 160 */     return this.directBuffer.limit();
/*     */   }
/*     */ 
/*     */   public byte[] getByteBuffer()
/*     */   {
/* 169 */     return bufferToArray();
/*     */   }
/*     */ 
/*     */   final byte[] getBytes(int len) {
/* 173 */     byte[] b = new byte[len];
/* 174 */     byte[] nioByteBuffer = bufferToArray();
/*     */     try
/*     */     {
/* 178 */       System.arraycopy(nioByteBuffer, this.directBuffer.position(), b, 0, len);
/*     */ 
/* 181 */       this.directBuffer.position(this.directBuffer.position() + len);
/*     */     } catch (ArrayIndexOutOfBoundsException aiobex) {
/* 183 */       throw aiobex;
/*     */     }
/*     */ 
/* 186 */     return b;
/*     */   }
/*     */ 
/*     */   byte[] getBytes(int offset, int len)
/*     */   {
/* 195 */     byte[] b = new byte[len];
/* 196 */     byte[] nioByteBuffer = bufferToArray();
/*     */     try
/*     */     {
/* 200 */       System.arraycopy(nioByteBuffer, offset, b, 0, len);
/*     */ 
/* 202 */       this.directBuffer.position(offset + len);
/*     */     } catch (ArrayIndexOutOfBoundsException aiobex) {
/* 204 */       throw aiobex;
/*     */     }
/*     */ 
/* 207 */     return b;
/*     */   }
/*     */ 
/*     */   int getCapacity() {
/* 211 */     return this.directBuffer.capacity();
/*     */   }
/*     */ 
/*     */   public ByteBuffer getNioBuffer() {
/* 215 */     return this.directBuffer;
/*     */   }
/*     */ 
/*     */   public int getPosition()
/*     */   {
/* 228 */     return this.directBuffer.position();
/*     */   }
/*     */ 
/*     */   final boolean isLastDataPacket()
/*     */   {
/* 233 */     boolean hasMarker = (this.directBuffer.get(0) & 0xFF) == 254;
/*     */ 
/* 235 */     return (hasMarker) && (this.bufLength < 9);
/*     */   }
/*     */ 
/*     */   final long newReadLength() {
/* 239 */     int sw = this.directBuffer.get(this.directBuffer.position()) & 0xFF;
/* 240 */     this.directBuffer.position(this.directBuffer.position() + 1);
/*     */ 
/* 242 */     switch (sw) {
/*     */     case 251:
/* 244 */       return 0L;
/*     */     case 252:
/* 247 */       return readInt();
/*     */     case 253:
/* 250 */       return readLongInt();
/*     */     case 254:
/* 253 */       return readLongLong();
/*     */     }
/*     */ 
/* 256 */     return sw;
/*     */   }
/*     */ 
/*     */   final byte readByte()
/*     */   {
/* 261 */     byte b = this.directBuffer.get();
/*     */ 
/* 263 */     return b;
/*     */   }
/*     */ 
/*     */   final byte readByte(int readAt) {
/* 267 */     return this.directBuffer.get(readAt);
/*     */   }
/*     */ 
/*     */   final long readFieldLength() {
/* 271 */     int sw = this.directBuffer.get() & 0xFF;
/*     */ 
/* 273 */     switch (sw) {
/*     */     case 251:
/* 275 */       return -1L;
/*     */     case 252:
/* 278 */       return readInt();
/*     */     case 253:
/* 281 */       return readLongInt();
/*     */     case 254:
/* 284 */       return readLongLong();
/*     */     }
/*     */ 
/* 287 */     return sw;
/*     */   }
/*     */ 
/*     */   final int readInt()
/*     */   {
/* 292 */     return this.directBuffer.get() & 0xFF | (this.directBuffer.get() & 0xFF) << 8;
/*     */   }
/*     */ 
/*     */   final int readIntAsLong()
/*     */   {
/* 297 */     int i = this.directBuffer.get() & 0xFF | (this.directBuffer.get() & 0xFF) << 8 | (this.directBuffer.get() & 0xFF) << 16 | (this.directBuffer.get() & 0xFF) << 24;
/*     */ 
/* 304 */     return i;
/*     */   }
/*     */ 
/*     */   final byte[] readLenByteArray(int offset) {
/* 308 */     long len = readFieldLength();
/*     */ 
/* 310 */     if (len == -1L) {
/* 311 */       return null;
/*     */     }
/*     */ 
/* 314 */     if (len == 0L) {
/* 315 */       return Constants.EMPTY_BYTE_ARRAY;
/*     */     }
/*     */ 
/* 318 */     this.directBuffer.position(this.directBuffer.position() + offset);
/*     */ 
/* 321 */     return getBytes((int)len);
/*     */   }
/*     */ 
/*     */   final long readLength() {
/* 325 */     int sw = this.directBuffer.get() & 0xFF;
/*     */ 
/* 328 */     switch (sw) {
/*     */     case 251:
/* 330 */       return 0L;
/*     */     case 252:
/* 333 */       return readInt();
/*     */     case 253:
/* 336 */       return readLongInt();
/*     */     case 254:
/* 339 */       return readLong();
/*     */     }
/*     */ 
/* 342 */     return sw;
/*     */   }
/*     */ 
/*     */   final long readLong()
/*     */   {
/* 347 */     long l = this.directBuffer.get() & 0xFF | (this.directBuffer.get() & 0xFF) << 8 | (this.directBuffer.get() & 0xFF) << 16 | (this.directBuffer.get() & 0xFF) << 24;
/*     */ 
/* 354 */     return l;
/*     */   }
/*     */ 
/*     */   final int readLongInt()
/*     */   {
/* 359 */     int i = this.directBuffer.get() & 0xFF | (this.directBuffer.get() & 0xFF) << 8 | (this.directBuffer.get() & 0xFF) << 16;
/*     */ 
/* 365 */     return i;
/*     */   }
/*     */ 
/*     */   final long readLongLong()
/*     */   {
/* 371 */     long l = this.directBuffer.get() & 0xFF | (this.directBuffer.get() & 0xFF) << 8 | (this.directBuffer.get() & 0xFF) << 16 | (this.directBuffer.get() & 0xFF) << 24 | (this.directBuffer.get() & 0xFF) << 32 | (this.directBuffer.get() & 0xFF) << 40 | (this.directBuffer.get() & 0xFF) << 48 | (this.directBuffer.get() & 0xFF) << 56;
/*     */ 
/* 382 */     return l;
/*     */   }
/*     */ 
/*     */   final int readnBytes() {
/* 386 */     int sw = this.directBuffer.get() & 0xFF;
/*     */ 
/* 389 */     switch (sw) {
/*     */     case 1:
/* 391 */       return this.directBuffer.get() & 0xFF;
/*     */     case 2:
/* 394 */       return readInt();
/*     */     case 3:
/* 397 */       return readLongInt();
/*     */     case 4:
/* 400 */       return (int)readLong();
/*     */     }
/*     */ 
/* 403 */     return 255;
/*     */   }
/*     */ 
/*     */   final String readString()
/*     */   {
/* 415 */     int len = 0;
/* 416 */     int maxLen = getBufLength();
/* 417 */     int oldPosition = getPosition();
/*     */ 
/* 419 */     while ((getPosition() < maxLen) && (this.directBuffer.get() != 0)) {
/* 420 */       len++;
/*     */     }
/*     */ 
/* 423 */     setPosition(oldPosition);
/*     */ 
/* 425 */     String s = new String(bufferToArray(), getPosition(), len);
/*     */ 
/* 427 */     this.directBuffer.position(getPosition() + len + 1);
/*     */ 
/* 429 */     return s;
/*     */   }
/*     */ 
/*     */   final String readString(String encoding) throws SQLException
/*     */   {
/* 434 */     int len = 0;
/*     */ 
/* 436 */     int maxLen = getBufLength();
/*     */ 
/* 438 */     while ((getPosition() < maxLen) && (this.directBuffer.get() != 0)) {
/* 439 */       len++;
/*     */     }
/*     */     try
/*     */     {
/* 443 */       str = new String(bufferToArray(), getPosition(), len, encoding);
/*     */     }
/*     */     catch (UnsupportedEncodingException uEE)
/*     */     {
/*     */       String str;
/* 445 */       throw new SQLException(Messages.getString("ChannelBuffer.0") + encoding + Messages.getString("ChannelBuffer.1"), "S1009");
/*     */     }
/*     */     finally
/*     */     {
/* 449 */       this.directBuffer.position(getPosition() + len + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   void setBufLength(int bufLengthToSet)
/*     */   {
/* 455 */     this.bufLength = bufLengthToSet;
/* 456 */     this.directBuffer.limit(this.bufLength);
/* 457 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   public void setByteBuffer(byte[] byteBuffer)
/*     */   {
/* 467 */     this.directBuffer = ByteBuffer.wrap(byteBuffer);
/*     */   }
/*     */ 
/*     */   public void setPosition(int position)
/*     */   {
/* 478 */     this.directBuffer.position(position);
/*     */   }
/*     */ 
/*     */   final void writeByte(byte b) throws SQLException {
/* 482 */     ensureCapacity(1);
/*     */ 
/* 484 */     this.directBuffer.put(b);
/* 485 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes) throws SQLException
/*     */   {
/* 490 */     int len = bytes.length;
/* 491 */     ensureCapacity(len);
/*     */ 
/* 493 */     this.directBuffer.put(bytes, 0, len);
/* 494 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeBytesNoNull(byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 500 */     ensureCapacity(length);
/*     */ 
/* 502 */     this.directBuffer.put(bytes, offset, length);
/*     */ 
/* 504 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeDouble(double d) throws SQLException {
/* 508 */     long l = Double.doubleToLongBits(d);
/* 509 */     writeLongLong(l);
/* 510 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeFieldLength(long length) throws SQLException {
/* 514 */     if (length < 251L) {
/* 515 */       writeByte((byte)(int)length);
/* 516 */     } else if (length < 65536L) {
/* 517 */       ensureCapacity(3);
/* 518 */       writeByte(-4);
/* 519 */       writeInt((int)length);
/* 520 */     } else if (length < 16777216L) {
/* 521 */       ensureCapacity(4);
/* 522 */       writeByte(-3);
/* 523 */       writeLongInt((int)length);
/*     */     } else {
/* 525 */       ensureCapacity(9);
/* 526 */       writeByte(-2);
/* 527 */       writeLongLong(length);
/*     */     }
/*     */   }
/*     */ 
/*     */   final void writeFloat(float f) throws SQLException
/*     */   {
/* 533 */     ensureCapacity(4);
/*     */ 
/* 535 */     int i = Float.floatToIntBits(f);
/*     */ 
/* 537 */     this.directBuffer.put((byte)(i & 0xFF));
/* 538 */     this.directBuffer.put((byte)(i >>> 8));
/* 539 */     this.directBuffer.put((byte)(i >>> 16));
/* 540 */     this.directBuffer.put((byte)(i >>> 24));
/*     */ 
/* 542 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeInt(int i) throws SQLException
/*     */   {
/* 547 */     ensureCapacity(2);
/* 548 */     this.directBuffer.put((byte)(i & 0xFF));
/* 549 */     this.directBuffer.put((byte)(i >>> 8));
/*     */ 
/* 551 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeLenBytes(byte[] b)
/*     */     throws SQLException
/*     */   {
/* 557 */     int len = b.length;
/* 558 */     ensureCapacity(len + 9);
/* 559 */     writeFieldLength(len);
/* 560 */     this.directBuffer.put(b, 0, len);
/*     */ 
/* 562 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeLenString(String s, String encoding, String serverEncoding, SingleByteCharsetConverter converter, boolean parserKnowsUnicode)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 570 */     byte[] b = null;
/*     */ 
/* 572 */     if (converter != null)
/* 573 */       b = converter.toBytes(s);
/*     */     else {
/* 575 */       b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode);
/*     */     }
/*     */ 
/* 579 */     int len = b.length;
/* 580 */     ensureCapacity(len + 9);
/* 581 */     writeFieldLength(len);
/* 582 */     this.directBuffer.put(b, 0, len);
/*     */ 
/* 584 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeLong(long i) throws SQLException
/*     */   {
/* 589 */     ensureCapacity(4);
/*     */ 
/* 591 */     this.directBuffer.put((byte)(int)(i & 0xFF));
/* 592 */     this.directBuffer.put((byte)(int)(i >>> 8));
/* 593 */     this.directBuffer.put((byte)(int)(i >>> 16));
/* 594 */     this.directBuffer.put((byte)(int)(i >>> 24));
/*     */ 
/* 596 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeLongInt(int i) throws SQLException
/*     */   {
/* 601 */     ensureCapacity(3);
/*     */ 
/* 603 */     this.directBuffer.put((byte)(i & 0xFF));
/* 604 */     this.directBuffer.put((byte)(i >>> 8));
/* 605 */     this.directBuffer.put((byte)(i >>> 16));
/*     */ 
/* 607 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeLongLong(long i) throws SQLException {
/* 611 */     ensureCapacity(8);
/*     */ 
/* 613 */     this.directBuffer.put((byte)(int)(i & 0xFF));
/* 614 */     this.directBuffer.put((byte)(int)(i >>> 8));
/* 615 */     this.directBuffer.put((byte)(int)(i >>> 16));
/* 616 */     this.directBuffer.put((byte)(int)(i >>> 24));
/* 617 */     this.directBuffer.put((byte)(int)(i >>> 32));
/* 618 */     this.directBuffer.put((byte)(int)(i >>> 40));
/* 619 */     this.directBuffer.put((byte)(int)(i >>> 48));
/* 620 */     this.directBuffer.put((byte)(int)(i >>> 56));
/*     */ 
/* 622 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeString(String s) throws SQLException
/*     */   {
/* 627 */     ensureCapacity(s.length() * 2 + 1);
/*     */ 
/* 629 */     writeStringNoNull(s);
/* 630 */     this.directBuffer.put(0);
/*     */ 
/* 632 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s) throws SQLException
/*     */   {
/* 637 */     int len = s.length();
/* 638 */     ensureCapacity(len * 2);
/*     */ 
/* 640 */     this.directBuffer.put(s.getBytes(), 0, len);
/*     */ 
/* 642 */     this.dirty = true;
/*     */   }
/*     */ 
/*     */   final void writeStringNoNull(String s, String encoding, String serverEncoding, boolean parserKnowsUnicode)
/*     */     throws UnsupportedEncodingException, SQLException
/*     */   {
/* 650 */     byte[] b = StringUtils.getBytes(s, encoding, serverEncoding, parserKnowsUnicode);
/*     */ 
/* 653 */     int len = b.length;
/* 654 */     ensureCapacity(len);
/*     */ 
/* 656 */     this.directBuffer.put(b, 0, len);
/*     */ 
/* 658 */     this.dirty = true;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.ChannelBuffer
 * JD-Core Version:    0.6.0
 */
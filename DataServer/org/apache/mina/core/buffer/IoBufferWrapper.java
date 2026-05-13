/*     */ package org.apache.mina.core.buffer;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.DoubleBuffer;
/*     */ import java.nio.FloatBuffer;
/*     */ import java.nio.IntBuffer;
/*     */ import java.nio.LongBuffer;
/*     */ import java.nio.ShortBuffer;
/*     */ import java.nio.charset.CharacterCodingException;
/*     */ import java.nio.charset.CharsetDecoder;
/*     */ import java.nio.charset.CharsetEncoder;
/*     */ import java.util.EnumSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class IoBufferWrapper extends IoBuffer
/*     */ {
/*     */   private final IoBuffer buf;
/*     */ 
/*     */   protected IoBufferWrapper(IoBuffer buf)
/*     */   {
/*  61 */     if (buf == null) {
/*  62 */       throw new NullPointerException("buf");
/*     */     }
/*  64 */     this.buf = buf;
/*     */   }
/*     */ 
/*     */   public IoBuffer getParentBuffer()
/*     */   {
/*  71 */     return this.buf;
/*     */   }
/*     */ 
/*     */   public boolean isDirect()
/*     */   {
/*  76 */     return this.buf.isDirect();
/*     */   }
/*     */ 
/*     */   public ByteBuffer buf()
/*     */   {
/*  81 */     return this.buf.buf();
/*     */   }
/*     */ 
/*     */   public int capacity()
/*     */   {
/*  86 */     return this.buf.capacity();
/*     */   }
/*     */ 
/*     */   public int position()
/*     */   {
/*  91 */     return this.buf.position();
/*     */   }
/*     */ 
/*     */   public IoBuffer position(int newPosition)
/*     */   {
/*  96 */     this.buf.position(newPosition);
/*  97 */     return this;
/*     */   }
/*     */ 
/*     */   public int limit()
/*     */   {
/* 102 */     return this.buf.limit();
/*     */   }
/*     */ 
/*     */   public IoBuffer limit(int newLimit)
/*     */   {
/* 107 */     this.buf.limit(newLimit);
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer mark()
/*     */   {
/* 113 */     this.buf.mark();
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer reset()
/*     */   {
/* 119 */     this.buf.reset();
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer clear()
/*     */   {
/* 125 */     this.buf.clear();
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer sweep()
/*     */   {
/* 131 */     this.buf.sweep();
/* 132 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer sweep(byte value)
/*     */   {
/* 137 */     this.buf.sweep(value);
/* 138 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer flip()
/*     */   {
/* 143 */     this.buf.flip();
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer rewind()
/*     */   {
/* 149 */     this.buf.rewind();
/* 150 */     return this;
/*     */   }
/*     */ 
/*     */   public int remaining()
/*     */   {
/* 155 */     return this.buf.remaining();
/*     */   }
/*     */ 
/*     */   public boolean hasRemaining()
/*     */   {
/* 160 */     return this.buf.hasRemaining();
/*     */   }
/*     */ 
/*     */   public byte get()
/*     */   {
/* 165 */     return this.buf.get();
/*     */   }
/*     */ 
/*     */   public short getUnsigned()
/*     */   {
/* 170 */     return this.buf.getUnsigned();
/*     */   }
/*     */ 
/*     */   public IoBuffer put(byte b)
/*     */   {
/* 175 */     this.buf.put(b);
/* 176 */     return this;
/*     */   }
/*     */ 
/*     */   public byte get(int index)
/*     */   {
/* 181 */     return this.buf.get(index);
/*     */   }
/*     */ 
/*     */   public short getUnsigned(int index)
/*     */   {
/* 186 */     return this.buf.getUnsigned(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer put(int index, byte b)
/*     */   {
/* 191 */     this.buf.put(index, b);
/* 192 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer get(byte[] dst, int offset, int length)
/*     */   {
/* 197 */     this.buf.get(dst, offset, length);
/* 198 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer getSlice(int index, int length)
/*     */   {
/* 203 */     return this.buf.getSlice(index, length);
/*     */   }
/*     */ 
/*     */   public IoBuffer getSlice(int length)
/*     */   {
/* 208 */     return this.buf.getSlice(length);
/*     */   }
/*     */ 
/*     */   public IoBuffer get(byte[] dst)
/*     */   {
/* 213 */     this.buf.get(dst);
/* 214 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer put(IoBuffer src)
/*     */   {
/* 219 */     this.buf.put(src);
/* 220 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer put(ByteBuffer src)
/*     */   {
/* 225 */     this.buf.put(src);
/* 226 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer put(byte[] src, int offset, int length)
/*     */   {
/* 231 */     this.buf.put(src, offset, length);
/* 232 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer put(byte[] src)
/*     */   {
/* 237 */     this.buf.put(src);
/* 238 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer compact()
/*     */   {
/* 243 */     this.buf.compact();
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 249 */     return this.buf.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 254 */     return this.buf.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object ob)
/*     */   {
/* 259 */     return this.buf.equals(ob);
/*     */   }
/*     */ 
/*     */   public int compareTo(IoBuffer that) {
/* 263 */     return this.buf.compareTo(that);
/*     */   }
/*     */ 
/*     */   public ByteOrder order()
/*     */   {
/* 268 */     return this.buf.order();
/*     */   }
/*     */ 
/*     */   public IoBuffer order(ByteOrder bo)
/*     */   {
/* 273 */     this.buf.order(bo);
/* 274 */     return this;
/*     */   }
/*     */ 
/*     */   public char getChar()
/*     */   {
/* 279 */     return this.buf.getChar();
/*     */   }
/*     */ 
/*     */   public IoBuffer putChar(char value)
/*     */   {
/* 284 */     this.buf.putChar(value);
/* 285 */     return this;
/*     */   }
/*     */ 
/*     */   public char getChar(int index)
/*     */   {
/* 290 */     return this.buf.getChar(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putChar(int index, char value)
/*     */   {
/* 295 */     this.buf.putChar(index, value);
/* 296 */     return this;
/*     */   }
/*     */ 
/*     */   public CharBuffer asCharBuffer()
/*     */   {
/* 301 */     return this.buf.asCharBuffer();
/*     */   }
/*     */ 
/*     */   public short getShort()
/*     */   {
/* 306 */     return this.buf.getShort();
/*     */   }
/*     */ 
/*     */   public int getUnsignedShort()
/*     */   {
/* 311 */     return this.buf.getUnsignedShort();
/*     */   }
/*     */ 
/*     */   public IoBuffer putShort(short value)
/*     */   {
/* 316 */     this.buf.putShort(value);
/* 317 */     return this;
/*     */   }
/*     */ 
/*     */   public short getShort(int index)
/*     */   {
/* 322 */     return this.buf.getShort(index);
/*     */   }
/*     */ 
/*     */   public int getUnsignedShort(int index)
/*     */   {
/* 327 */     return this.buf.getUnsignedShort(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putShort(int index, short value)
/*     */   {
/* 332 */     this.buf.putShort(index, value);
/* 333 */     return this;
/*     */   }
/*     */ 
/*     */   public ShortBuffer asShortBuffer()
/*     */   {
/* 338 */     return this.buf.asShortBuffer();
/*     */   }
/*     */ 
/*     */   public int getInt()
/*     */   {
/* 343 */     return this.buf.getInt();
/*     */   }
/*     */ 
/*     */   public long getUnsignedInt()
/*     */   {
/* 348 */     return this.buf.getUnsignedInt();
/*     */   }
/*     */ 
/*     */   public IoBuffer putInt(int value)
/*     */   {
/* 353 */     this.buf.putInt(value);
/* 354 */     return this;
/*     */   }
/*     */ 
/*     */   public int getInt(int index)
/*     */   {
/* 359 */     return this.buf.getInt(index);
/*     */   }
/*     */ 
/*     */   public long getUnsignedInt(int index)
/*     */   {
/* 364 */     return this.buf.getUnsignedInt(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putInt(int index, int value)
/*     */   {
/* 369 */     this.buf.putInt(index, value);
/* 370 */     return this;
/*     */   }
/*     */ 
/*     */   public IntBuffer asIntBuffer()
/*     */   {
/* 375 */     return this.buf.asIntBuffer();
/*     */   }
/*     */ 
/*     */   public long getLong()
/*     */   {
/* 380 */     return this.buf.getLong();
/*     */   }
/*     */ 
/*     */   public IoBuffer putLong(long value)
/*     */   {
/* 385 */     this.buf.putLong(value);
/* 386 */     return this;
/*     */   }
/*     */ 
/*     */   public long getLong(int index)
/*     */   {
/* 391 */     return this.buf.getLong(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putLong(int index, long value)
/*     */   {
/* 396 */     this.buf.putLong(index, value);
/* 397 */     return this;
/*     */   }
/*     */ 
/*     */   public LongBuffer asLongBuffer()
/*     */   {
/* 402 */     return this.buf.asLongBuffer();
/*     */   }
/*     */ 
/*     */   public float getFloat()
/*     */   {
/* 407 */     return this.buf.getFloat();
/*     */   }
/*     */ 
/*     */   public IoBuffer putFloat(float value)
/*     */   {
/* 412 */     this.buf.putFloat(value);
/* 413 */     return this;
/*     */   }
/*     */ 
/*     */   public float getFloat(int index)
/*     */   {
/* 418 */     return this.buf.getFloat(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putFloat(int index, float value)
/*     */   {
/* 423 */     this.buf.putFloat(index, value);
/* 424 */     return this;
/*     */   }
/*     */ 
/*     */   public FloatBuffer asFloatBuffer()
/*     */   {
/* 429 */     return this.buf.asFloatBuffer();
/*     */   }
/*     */ 
/*     */   public double getDouble()
/*     */   {
/* 434 */     return this.buf.getDouble();
/*     */   }
/*     */ 
/*     */   public IoBuffer putDouble(double value)
/*     */   {
/* 439 */     this.buf.putDouble(value);
/* 440 */     return this;
/*     */   }
/*     */ 
/*     */   public double getDouble(int index)
/*     */   {
/* 445 */     return this.buf.getDouble(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putDouble(int index, double value)
/*     */   {
/* 450 */     this.buf.putDouble(index, value);
/* 451 */     return this;
/*     */   }
/*     */ 
/*     */   public DoubleBuffer asDoubleBuffer()
/*     */   {
/* 456 */     return this.buf.asDoubleBuffer();
/*     */   }
/*     */ 
/*     */   public String getHexDump()
/*     */   {
/* 461 */     return this.buf.getHexDump();
/*     */   }
/*     */ 
/*     */   public String getString(int fieldSize, CharsetDecoder decoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 467 */     return this.buf.getString(fieldSize, decoder);
/*     */   }
/*     */ 
/*     */   public String getString(CharsetDecoder decoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 473 */     return this.buf.getString(decoder);
/*     */   }
/*     */ 
/*     */   public String getPrefixedString(CharsetDecoder decoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 479 */     return this.buf.getPrefixedString(decoder);
/*     */   }
/*     */ 
/*     */   public String getPrefixedString(int prefixLength, CharsetDecoder decoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 485 */     return this.buf.getPrefixedString(prefixLength, decoder);
/*     */   }
/*     */ 
/*     */   public IoBuffer putString(CharSequence in, int fieldSize, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 491 */     this.buf.putString(in, fieldSize, encoder);
/* 492 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putString(CharSequence in, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 498 */     this.buf.putString(in, encoder);
/* 499 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putPrefixedString(CharSequence in, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 505 */     this.buf.putPrefixedString(in, encoder);
/* 506 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putPrefixedString(CharSequence in, int prefixLength, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 512 */     this.buf.putPrefixedString(in, prefixLength, encoder);
/* 513 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 520 */     this.buf.putPrefixedString(in, prefixLength, padding, encoder);
/* 521 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, byte padValue, CharsetEncoder encoder)
/*     */     throws CharacterCodingException
/*     */   {
/* 528 */     this.buf.putPrefixedString(in, prefixLength, padding, padValue, encoder);
/* 529 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer skip(int size)
/*     */   {
/* 534 */     this.buf.skip(size);
/* 535 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer fill(byte value, int size)
/*     */   {
/* 540 */     this.buf.fill(value, size);
/* 541 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer fillAndReset(byte value, int size)
/*     */   {
/* 546 */     this.buf.fillAndReset(value, size);
/* 547 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer fill(int size)
/*     */   {
/* 552 */     this.buf.fill(size);
/* 553 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer fillAndReset(int size)
/*     */   {
/* 558 */     this.buf.fillAndReset(size);
/* 559 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isAutoExpand()
/*     */   {
/* 564 */     return this.buf.isAutoExpand();
/*     */   }
/*     */ 
/*     */   public IoBuffer setAutoExpand(boolean autoExpand)
/*     */   {
/* 569 */     this.buf.setAutoExpand(autoExpand);
/* 570 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer expand(int pos, int expectedRemaining)
/*     */   {
/* 575 */     this.buf.expand(pos, expectedRemaining);
/* 576 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer expand(int expectedRemaining)
/*     */   {
/* 581 */     this.buf.expand(expectedRemaining);
/* 582 */     return this;
/*     */   }
/*     */ 
/*     */   public Object getObject() throws ClassNotFoundException
/*     */   {
/* 587 */     return this.buf.getObject();
/*     */   }
/*     */ 
/*     */   public Object getObject(ClassLoader classLoader)
/*     */     throws ClassNotFoundException
/*     */   {
/* 593 */     return this.buf.getObject(classLoader);
/*     */   }
/*     */ 
/*     */   public IoBuffer putObject(Object o)
/*     */   {
/* 598 */     this.buf.putObject(o);
/* 599 */     return this;
/*     */   }
/*     */ 
/*     */   public InputStream asInputStream()
/*     */   {
/* 604 */     return this.buf.asInputStream();
/*     */   }
/*     */ 
/*     */   public OutputStream asOutputStream()
/*     */   {
/* 609 */     return this.buf.asOutputStream();
/*     */   }
/*     */ 
/*     */   public IoBuffer duplicate()
/*     */   {
/* 614 */     return this.buf.duplicate();
/*     */   }
/*     */ 
/*     */   public IoBuffer slice()
/*     */   {
/* 619 */     return this.buf.slice();
/*     */   }
/*     */ 
/*     */   public IoBuffer asReadOnlyBuffer()
/*     */   {
/* 624 */     return this.buf.asReadOnlyBuffer();
/*     */   }
/*     */ 
/*     */   public byte[] array()
/*     */   {
/* 629 */     return this.buf.array();
/*     */   }
/*     */ 
/*     */   public int arrayOffset()
/*     */   {
/* 634 */     return this.buf.arrayOffset();
/*     */   }
/*     */ 
/*     */   public int minimumCapacity()
/*     */   {
/* 639 */     return this.buf.minimumCapacity();
/*     */   }
/*     */ 
/*     */   public IoBuffer minimumCapacity(int minimumCapacity)
/*     */   {
/* 644 */     this.buf.minimumCapacity(minimumCapacity);
/* 645 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer capacity(int newCapacity)
/*     */   {
/* 650 */     this.buf.capacity(newCapacity);
/* 651 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/* 656 */     return this.buf.isReadOnly();
/*     */   }
/*     */ 
/*     */   public int markValue()
/*     */   {
/* 661 */     return this.buf.markValue();
/*     */   }
/*     */ 
/*     */   public boolean hasArray()
/*     */   {
/* 666 */     return this.buf.hasArray();
/*     */   }
/*     */ 
/*     */   public void free()
/*     */   {
/* 671 */     this.buf.free();
/*     */   }
/*     */ 
/*     */   public boolean isDerived()
/*     */   {
/* 676 */     return this.buf.isDerived();
/*     */   }
/*     */ 
/*     */   public boolean isAutoShrink()
/*     */   {
/* 681 */     return this.buf.isAutoShrink();
/*     */   }
/*     */ 
/*     */   public IoBuffer setAutoShrink(boolean autoShrink)
/*     */   {
/* 686 */     this.buf.setAutoShrink(autoShrink);
/* 687 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer shrink()
/*     */   {
/* 692 */     this.buf.shrink();
/* 693 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMediumInt()
/*     */   {
/* 698 */     return this.buf.getMediumInt();
/*     */   }
/*     */ 
/*     */   public int getUnsignedMediumInt()
/*     */   {
/* 703 */     return this.buf.getUnsignedMediumInt();
/*     */   }
/*     */ 
/*     */   public int getMediumInt(int index)
/*     */   {
/* 708 */     return this.buf.getMediumInt(index);
/*     */   }
/*     */ 
/*     */   public int getUnsignedMediumInt(int index)
/*     */   {
/* 713 */     return this.buf.getUnsignedMediumInt(index);
/*     */   }
/*     */ 
/*     */   public IoBuffer putMediumInt(int value)
/*     */   {
/* 718 */     this.buf.putMediumInt(value);
/* 719 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putMediumInt(int index, int value)
/*     */   {
/* 724 */     this.buf.putMediumInt(index, value);
/* 725 */     return this;
/*     */   }
/*     */ 
/*     */   public String getHexDump(int lengthLimit)
/*     */   {
/* 730 */     return this.buf.getHexDump(lengthLimit);
/*     */   }
/*     */ 
/*     */   public boolean prefixedDataAvailable(int prefixLength)
/*     */   {
/* 735 */     return this.buf.prefixedDataAvailable(prefixLength);
/*     */   }
/*     */ 
/*     */   public boolean prefixedDataAvailable(int prefixLength, int maxDataLength)
/*     */   {
/* 740 */     return this.buf.prefixedDataAvailable(prefixLength, maxDataLength);
/*     */   }
/*     */ 
/*     */   public int indexOf(byte b)
/*     */   {
/* 745 */     return this.buf.indexOf(b);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnum(Class<E> enumClass)
/*     */   {
/* 750 */     return this.buf.getEnum(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnum(int index, Class<E> enumClass)
/*     */   {
/* 755 */     return this.buf.getEnum(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnumShort(Class<E> enumClass)
/*     */   {
/* 760 */     return this.buf.getEnumShort(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnumShort(int index, Class<E> enumClass)
/*     */   {
/* 765 */     return this.buf.getEnumShort(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnumInt(Class<E> enumClass)
/*     */   {
/* 770 */     return this.buf.getEnumInt(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> E getEnumInt(int index, Class<E> enumClass)
/*     */   {
/* 775 */     return this.buf.getEnumInt(index, enumClass);
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnum(Enum<?> e)
/*     */   {
/* 780 */     this.buf.putEnum(e);
/* 781 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnum(int index, Enum<?> e)
/*     */   {
/* 786 */     this.buf.putEnum(index, e);
/* 787 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnumShort(Enum<?> e)
/*     */   {
/* 792 */     this.buf.putEnumShort(e);
/* 793 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnumShort(int index, Enum<?> e)
/*     */   {
/* 798 */     this.buf.putEnumShort(index, e);
/* 799 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnumInt(Enum<?> e)
/*     */   {
/* 804 */     this.buf.putEnumInt(e);
/* 805 */     return this;
/*     */   }
/*     */ 
/*     */   public IoBuffer putEnumInt(int index, Enum<?> e)
/*     */   {
/* 810 */     this.buf.putEnumInt(index, e);
/* 811 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass)
/*     */   {
/* 816 */     return this.buf.getEnumSet(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSet(int index, Class<E> enumClass)
/*     */   {
/* 821 */     return this.buf.getEnumSet(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetShort(Class<E> enumClass)
/*     */   {
/* 826 */     return this.buf.getEnumSetShort(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetShort(int index, Class<E> enumClass)
/*     */   {
/* 831 */     return this.buf.getEnumSetShort(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetInt(Class<E> enumClass)
/*     */   {
/* 836 */     return this.buf.getEnumSetInt(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetInt(int index, Class<E> enumClass)
/*     */   {
/* 841 */     return this.buf.getEnumSetInt(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetLong(Class<E> enumClass)
/*     */   {
/* 846 */     return this.buf.getEnumSetLong(enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> EnumSet<E> getEnumSetLong(int index, Class<E> enumClass)
/*     */   {
/* 851 */     return this.buf.getEnumSetLong(index, enumClass);
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSet(Set<E> set)
/*     */   {
/* 856 */     this.buf.putEnumSet(set);
/* 857 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSet(int index, Set<E> set)
/*     */   {
/* 862 */     this.buf.putEnumSet(index, set);
/* 863 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetShort(Set<E> set)
/*     */   {
/* 868 */     this.buf.putEnumSetShort(set);
/* 869 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetShort(int index, Set<E> set)
/*     */   {
/* 874 */     this.buf.putEnumSetShort(index, set);
/* 875 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetInt(Set<E> set)
/*     */   {
/* 880 */     this.buf.putEnumSetInt(set);
/* 881 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetInt(int index, Set<E> set)
/*     */   {
/* 886 */     this.buf.putEnumSetInt(index, set);
/* 887 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetLong(Set<E> set)
/*     */   {
/* 892 */     this.buf.putEnumSetLong(set);
/* 893 */     return this;
/*     */   }
/*     */ 
/*     */   public <E extends Enum<E>> IoBuffer putEnumSetLong(int index, Set<E> set)
/*     */   {
/* 898 */     this.buf.putEnumSetLong(index, set);
/* 899 */     return this;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.buffer.IoBufferWrapper
 * JD-Core Version:    0.6.0
 */
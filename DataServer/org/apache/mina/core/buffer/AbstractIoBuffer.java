/*      */ package org.apache.mina.core.buffer;
/*      */ 
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.ObjectStreamClass;
/*      */ import java.io.OutputStream;
/*      */ import java.io.StreamCorruptedException;
/*      */ import java.nio.BufferOverflowException;
/*      */ import java.nio.BufferUnderflowException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.ByteOrder;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.DoubleBuffer;
/*      */ import java.nio.FloatBuffer;
/*      */ import java.nio.IntBuffer;
/*      */ import java.nio.LongBuffer;
/*      */ import java.nio.ShortBuffer;
/*      */ import java.nio.charset.CharacterCodingException;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.CharsetDecoder;
/*      */ import java.nio.charset.CharsetEncoder;
/*      */ import java.nio.charset.CoderResult;
/*      */ import java.util.EnumSet;
/*      */ import java.util.Set;
/*      */ 
/*      */ public abstract class AbstractIoBuffer extends IoBuffer
/*      */ {
/*      */   private final boolean derived;
/*      */   private boolean autoExpand;
/*      */   private boolean autoShrink;
/*   68 */   private boolean recapacityAllowed = true;
/*      */   private int minimumCapacity;
/*      */   private static final long BYTE_MASK = 255L;
/*      */   private static final long SHORT_MASK = 65535L;
/*      */   private static final long INT_MASK = 4294967295L;
/*   86 */   private int mark = -1;
/*      */ 
/*      */   protected AbstractIoBuffer(IoBufferAllocator allocator, int initialCapacity)
/*      */   {
/*   95 */     setAllocator(allocator);
/*   96 */     this.recapacityAllowed = true;
/*   97 */     this.derived = false;
/*   98 */     this.minimumCapacity = initialCapacity;
/*      */   }
/*      */ 
/*      */   protected AbstractIoBuffer(AbstractIoBuffer parent)
/*      */   {
/*  108 */     setAllocator(getAllocator());
/*  109 */     this.recapacityAllowed = false;
/*  110 */     this.derived = true;
/*  111 */     this.minimumCapacity = parent.minimumCapacity;
/*      */   }
/*      */ 
/*      */   public final boolean isDirect()
/*      */   {
/*  119 */     return buf().isDirect();
/*      */   }
/*      */ 
/*      */   public final boolean isReadOnly()
/*      */   {
/*  127 */     return buf().isReadOnly();
/*      */   }
/*      */ 
/*      */   protected abstract void buf(ByteBuffer paramByteBuffer);
/*      */ 
/*      */   public final int minimumCapacity()
/*      */   {
/*  142 */     return this.minimumCapacity;
/*      */   }
/*      */ 
/*      */   public final IoBuffer minimumCapacity(int minimumCapacity)
/*      */   {
/*  150 */     if (minimumCapacity < 0) {
/*  151 */       throw new IllegalArgumentException("minimumCapacity: " + minimumCapacity);
/*      */     }
/*      */ 
/*  154 */     this.minimumCapacity = minimumCapacity;
/*  155 */     return this;
/*      */   }
/*      */ 
/*      */   public final int capacity()
/*      */   {
/*  163 */     return buf().capacity();
/*      */   }
/*      */ 
/*      */   public final IoBuffer capacity(int newCapacity)
/*      */   {
/*  171 */     if (!this.recapacityAllowed) {
/*  172 */       throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
/*      */     }
/*      */ 
/*  177 */     if (newCapacity > capacity())
/*      */     {
/*  180 */       int pos = position();
/*  181 */       int limit = limit();
/*  182 */       ByteOrder bo = order();
/*      */ 
/*  185 */       ByteBuffer oldBuf = buf();
/*  186 */       ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, isDirect());
/*      */ 
/*  188 */       oldBuf.clear();
/*  189 */       newBuf.put(oldBuf);
/*  190 */       buf(newBuf);
/*      */ 
/*  193 */       buf().limit(limit);
/*  194 */       if (this.mark >= 0) {
/*  195 */         buf().position(this.mark);
/*  196 */         buf().mark();
/*      */       }
/*  198 */       buf().position(pos);
/*  199 */       buf().order(bo);
/*      */     }
/*      */ 
/*  202 */     return this;
/*      */   }
/*      */ 
/*      */   public final boolean isAutoExpand()
/*      */   {
/*  210 */     return (this.autoExpand) && (this.recapacityAllowed);
/*      */   }
/*      */ 
/*      */   public final boolean isAutoShrink()
/*      */   {
/*  218 */     return (this.autoShrink) && (this.recapacityAllowed);
/*      */   }
/*      */ 
/*      */   public final boolean isDerived()
/*      */   {
/*  226 */     return this.derived;
/*      */   }
/*      */ 
/*      */   public final IoBuffer setAutoExpand(boolean autoExpand)
/*      */   {
/*  234 */     if (!this.recapacityAllowed) {
/*  235 */       throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
/*      */     }
/*      */ 
/*  238 */     this.autoExpand = autoExpand;
/*  239 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer setAutoShrink(boolean autoShrink)
/*      */   {
/*  247 */     if (!this.recapacityAllowed) {
/*  248 */       throw new IllegalStateException("Derived buffers and their parent can't be shrinked.");
/*      */     }
/*      */ 
/*  251 */     this.autoShrink = autoShrink;
/*  252 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer expand(int expectedRemaining)
/*      */   {
/*  260 */     return expand(position(), expectedRemaining, false);
/*      */   }
/*      */ 
/*      */   private IoBuffer expand(int expectedRemaining, boolean autoExpand) {
/*  264 */     return expand(position(), expectedRemaining, autoExpand);
/*      */   }
/*      */ 
/*      */   public final IoBuffer expand(int pos, int expectedRemaining)
/*      */   {
/*  272 */     return expand(pos, expectedRemaining, false);
/*      */   }
/*      */ 
/*      */   private IoBuffer expand(int pos, int expectedRemaining, boolean autoExpand) {
/*  276 */     if (!this.recapacityAllowed) {
/*  277 */       throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
/*      */     }
/*      */ 
/*  281 */     int end = pos + expectedRemaining;
/*      */     int newCapacity;
/*      */     int newCapacity;
/*  283 */     if (autoExpand)
/*  284 */       newCapacity = IoBuffer.normalizeCapacity(end);
/*      */     else {
/*  286 */       newCapacity = end;
/*      */     }
/*  288 */     if (newCapacity > capacity())
/*      */     {
/*  290 */       capacity(newCapacity);
/*      */     }
/*      */ 
/*  293 */     if (end > limit())
/*      */     {
/*  295 */       buf().limit(end);
/*      */     }
/*  297 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer shrink()
/*      */   {
/*  306 */     if (!this.recapacityAllowed) {
/*  307 */       throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
/*      */     }
/*      */ 
/*  311 */     int position = position();
/*  312 */     int capacity = capacity();
/*  313 */     int limit = limit();
/*  314 */     if (capacity == limit) {
/*  315 */       return this;
/*      */     }
/*      */ 
/*  318 */     int newCapacity = capacity;
/*  319 */     int minCapacity = Math.max(this.minimumCapacity, limit);
/*      */ 
/*  321 */     while (newCapacity >>> 1 >= minCapacity)
/*      */     {
/*  324 */       newCapacity >>>= 1;
/*      */     }
/*      */ 
/*  327 */     newCapacity = Math.max(minCapacity, newCapacity);
/*      */ 
/*  329 */     if (newCapacity == capacity) {
/*  330 */       return this;
/*      */     }
/*      */ 
/*  335 */     ByteOrder bo = order();
/*      */ 
/*  338 */     ByteBuffer oldBuf = buf();
/*  339 */     ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, isDirect());
/*      */ 
/*  341 */     oldBuf.position(0);
/*  342 */     oldBuf.limit(limit);
/*  343 */     newBuf.put(oldBuf);
/*  344 */     buf(newBuf);
/*      */ 
/*  347 */     buf().position(position);
/*  348 */     buf().limit(limit);
/*  349 */     buf().order(bo);
/*  350 */     this.mark = -1;
/*      */ 
/*  352 */     return this;
/*      */   }
/*      */ 
/*      */   public final int position()
/*      */   {
/*  360 */     return buf().position();
/*      */   }
/*      */ 
/*      */   public final IoBuffer position(int newPosition)
/*      */   {
/*  368 */     autoExpand(newPosition, 0);
/*  369 */     buf().position(newPosition);
/*  370 */     if (this.mark > newPosition) {
/*  371 */       this.mark = -1;
/*      */     }
/*  373 */     return this;
/*      */   }
/*      */ 
/*      */   public final int limit()
/*      */   {
/*  381 */     return buf().limit();
/*      */   }
/*      */ 
/*      */   public final IoBuffer limit(int newLimit)
/*      */   {
/*  389 */     autoExpand(newLimit, 0);
/*  390 */     buf().limit(newLimit);
/*  391 */     if (this.mark > newLimit) {
/*  392 */       this.mark = -1;
/*      */     }
/*  394 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer mark()
/*      */   {
/*  402 */     buf().mark();
/*  403 */     this.mark = position();
/*  404 */     return this;
/*      */   }
/*      */ 
/*      */   public final int markValue()
/*      */   {
/*  412 */     return this.mark;
/*      */   }
/*      */ 
/*      */   public final IoBuffer reset()
/*      */   {
/*  420 */     buf().reset();
/*  421 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer clear()
/*      */   {
/*  429 */     buf().clear();
/*  430 */     this.mark = -1;
/*  431 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer sweep()
/*      */   {
/*  439 */     clear();
/*  440 */     return fillAndReset(remaining());
/*      */   }
/*      */ 
/*      */   public final IoBuffer sweep(byte value)
/*      */   {
/*  448 */     clear();
/*  449 */     return fillAndReset(value, remaining());
/*      */   }
/*      */ 
/*      */   public final IoBuffer flip()
/*      */   {
/*  457 */     buf().flip();
/*  458 */     this.mark = -1;
/*  459 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer rewind()
/*      */   {
/*  467 */     buf().rewind();
/*  468 */     this.mark = -1;
/*  469 */     return this;
/*      */   }
/*      */ 
/*      */   public final int remaining()
/*      */   {
/*  477 */     return limit() - position();
/*      */   }
/*      */ 
/*      */   public final boolean hasRemaining()
/*      */   {
/*  485 */     return limit() > position();
/*      */   }
/*      */ 
/*      */   public final byte get()
/*      */   {
/*  493 */     return buf().get();
/*      */   }
/*      */ 
/*      */   public final short getUnsigned()
/*      */   {
/*  501 */     return (short)(get() & 0xFF);
/*      */   }
/*      */ 
/*      */   public final IoBuffer put(byte b)
/*      */   {
/*  509 */     autoExpand(1);
/*  510 */     buf().put(b);
/*  511 */     return this;
/*      */   }
/*      */ 
/*      */   public final byte get(int index)
/*      */   {
/*  519 */     return buf().get(index);
/*      */   }
/*      */ 
/*      */   public final short getUnsigned(int index)
/*      */   {
/*  527 */     return (short)(get(index) & 0xFF);
/*      */   }
/*      */ 
/*      */   public final IoBuffer put(int index, byte b)
/*      */   {
/*  535 */     autoExpand(index, 1);
/*  536 */     buf().put(index, b);
/*  537 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer get(byte[] dst, int offset, int length)
/*      */   {
/*  545 */     buf().get(dst, offset, length);
/*  546 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer put(ByteBuffer src)
/*      */   {
/*  554 */     autoExpand(src.remaining());
/*  555 */     buf().put(src);
/*  556 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer put(byte[] src, int offset, int length)
/*      */   {
/*  564 */     autoExpand(length);
/*  565 */     buf().put(src, offset, length);
/*  566 */     return this;
/*      */   }
/*      */ 
/*      */   public final IoBuffer compact()
/*      */   {
/*  574 */     int remaining = remaining();
/*  575 */     int capacity = capacity();
/*      */ 
/*  577 */     if (capacity == 0) {
/*  578 */       return this;
/*      */     }
/*      */ 
/*  581 */     if ((isAutoShrink()) && (remaining <= capacity >>> 2) && (capacity > this.minimumCapacity))
/*      */     {
/*  583 */       int newCapacity = capacity;
/*  584 */       int minCapacity = Math.max(this.minimumCapacity, remaining << 1);
/*      */ 
/*  586 */       while (newCapacity >>> 1 >= minCapacity)
/*      */       {
/*  589 */         newCapacity >>>= 1;
/*      */       }
/*      */ 
/*  592 */       newCapacity = Math.max(minCapacity, newCapacity);
/*      */ 
/*  594 */       if (newCapacity == capacity) {
/*  595 */         return this;
/*      */       }
/*      */ 
/*  600 */       ByteOrder bo = order();
/*      */ 
/*  603 */       if (remaining > newCapacity) {
/*  604 */         throw new IllegalStateException("The amount of the remaining bytes is greater than the new capacity.");
/*      */       }
/*      */ 
/*  610 */       ByteBuffer oldBuf = buf();
/*  611 */       ByteBuffer newBuf = getAllocator().allocateNioBuffer(newCapacity, isDirect());
/*      */ 
/*  613 */       newBuf.put(oldBuf);
/*  614 */       buf(newBuf);
/*      */ 
/*  617 */       buf().order(bo);
/*      */     } else {
/*  619 */       buf().compact();
/*      */     }
/*  621 */     this.mark = -1;
/*  622 */     return this;
/*      */   }
/*      */ 
/*      */   public final ByteOrder order()
/*      */   {
/*  630 */     return buf().order();
/*      */   }
/*      */ 
/*      */   public final IoBuffer order(ByteOrder bo)
/*      */   {
/*  638 */     buf().order(bo);
/*  639 */     return this;
/*      */   }
/*      */ 
/*      */   public final char getChar()
/*      */   {
/*  647 */     return buf().getChar();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putChar(char value)
/*      */   {
/*  655 */     autoExpand(2);
/*  656 */     buf().putChar(value);
/*  657 */     return this;
/*      */   }
/*      */ 
/*      */   public final char getChar(int index)
/*      */   {
/*  665 */     return buf().getChar(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putChar(int index, char value)
/*      */   {
/*  673 */     autoExpand(index, 2);
/*  674 */     buf().putChar(index, value);
/*  675 */     return this;
/*      */   }
/*      */ 
/*      */   public final CharBuffer asCharBuffer()
/*      */   {
/*  683 */     return buf().asCharBuffer();
/*      */   }
/*      */ 
/*      */   public final short getShort()
/*      */   {
/*  691 */     return buf().getShort();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putShort(short value)
/*      */   {
/*  699 */     autoExpand(2);
/*  700 */     buf().putShort(value);
/*  701 */     return this;
/*      */   }
/*      */ 
/*      */   public final short getShort(int index)
/*      */   {
/*  709 */     return buf().getShort(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putShort(int index, short value)
/*      */   {
/*  717 */     autoExpand(index, 2);
/*  718 */     buf().putShort(index, value);
/*  719 */     return this;
/*      */   }
/*      */ 
/*      */   public final ShortBuffer asShortBuffer()
/*      */   {
/*  727 */     return buf().asShortBuffer();
/*      */   }
/*      */ 
/*      */   public final int getInt()
/*      */   {
/*  735 */     return buf().getInt();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putInt(int value)
/*      */   {
/*  743 */     autoExpand(4);
/*  744 */     buf().putInt(value);
/*  745 */     return this;
/*      */   }
/*      */ 
/*      */   public final int getInt(int index)
/*      */   {
/*  753 */     return buf().getInt(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putInt(int index, int value)
/*      */   {
/*  761 */     autoExpand(index, 4);
/*  762 */     buf().putInt(index, value);
/*  763 */     return this;
/*      */   }
/*      */ 
/*      */   public final IntBuffer asIntBuffer()
/*      */   {
/*  771 */     return buf().asIntBuffer();
/*      */   }
/*      */ 
/*      */   public final long getLong()
/*      */   {
/*  779 */     return buf().getLong();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putLong(long value)
/*      */   {
/*  787 */     autoExpand(8);
/*  788 */     buf().putLong(value);
/*  789 */     return this;
/*      */   }
/*      */ 
/*      */   public final long getLong(int index)
/*      */   {
/*  797 */     return buf().getLong(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putLong(int index, long value)
/*      */   {
/*  805 */     autoExpand(index, 8);
/*  806 */     buf().putLong(index, value);
/*  807 */     return this;
/*      */   }
/*      */ 
/*      */   public final LongBuffer asLongBuffer()
/*      */   {
/*  815 */     return buf().asLongBuffer();
/*      */   }
/*      */ 
/*      */   public final float getFloat()
/*      */   {
/*  823 */     return buf().getFloat();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putFloat(float value)
/*      */   {
/*  831 */     autoExpand(4);
/*  832 */     buf().putFloat(value);
/*  833 */     return this;
/*      */   }
/*      */ 
/*      */   public final float getFloat(int index)
/*      */   {
/*  841 */     return buf().getFloat(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putFloat(int index, float value)
/*      */   {
/*  849 */     autoExpand(index, 4);
/*  850 */     buf().putFloat(index, value);
/*  851 */     return this;
/*      */   }
/*      */ 
/*      */   public final FloatBuffer asFloatBuffer()
/*      */   {
/*  859 */     return buf().asFloatBuffer();
/*      */   }
/*      */ 
/*      */   public final double getDouble()
/*      */   {
/*  867 */     return buf().getDouble();
/*      */   }
/*      */ 
/*      */   public final IoBuffer putDouble(double value)
/*      */   {
/*  875 */     autoExpand(8);
/*  876 */     buf().putDouble(value);
/*  877 */     return this;
/*      */   }
/*      */ 
/*      */   public final double getDouble(int index)
/*      */   {
/*  885 */     return buf().getDouble(index);
/*      */   }
/*      */ 
/*      */   public final IoBuffer putDouble(int index, double value)
/*      */   {
/*  893 */     autoExpand(index, 8);
/*  894 */     buf().putDouble(index, value);
/*  895 */     return this;
/*      */   }
/*      */ 
/*      */   public final DoubleBuffer asDoubleBuffer()
/*      */   {
/*  903 */     return buf().asDoubleBuffer();
/*      */   }
/*      */ 
/*      */   public final IoBuffer asReadOnlyBuffer()
/*      */   {
/*  911 */     this.recapacityAllowed = false;
/*  912 */     return asReadOnlyBuffer0();
/*      */   }
/*      */ 
/*      */   protected abstract IoBuffer asReadOnlyBuffer0();
/*      */ 
/*      */   public final IoBuffer duplicate()
/*      */   {
/*  926 */     this.recapacityAllowed = false;
/*  927 */     return duplicate0();
/*      */   }
/*      */ 
/*      */   protected abstract IoBuffer duplicate0();
/*      */ 
/*      */   public final IoBuffer slice()
/*      */   {
/*  941 */     this.recapacityAllowed = false;
/*  942 */     return slice0();
/*      */   }
/*      */ 
/*      */   public final IoBuffer getSlice(int index, int length)
/*      */   {
/*  950 */     if (length < 0) {
/*  951 */       throw new IllegalArgumentException("length: " + length);
/*      */     }
/*      */ 
/*  954 */     int limit = limit();
/*      */ 
/*  956 */     if (index > limit) {
/*  957 */       throw new IllegalArgumentException("index: " + index);
/*      */     }
/*      */ 
/*  960 */     int endIndex = index + length;
/*      */ 
/*  962 */     if (capacity() < endIndex) {
/*  963 */       throw new IndexOutOfBoundsException("index + length (" + endIndex + ") is greater " + "than capacity (" + capacity() + ").");
/*      */     }
/*      */ 
/*  967 */     clear();
/*  968 */     position(index);
/*  969 */     limit(endIndex);
/*      */ 
/*  971 */     IoBuffer slice = slice();
/*  972 */     position(index);
/*  973 */     limit(limit);
/*  974 */     return slice;
/*      */   }
/*      */ 
/*      */   public final IoBuffer getSlice(int length)
/*      */   {
/*  982 */     if (length < 0) {
/*  983 */       throw new IllegalArgumentException("length: " + length);
/*      */     }
/*  985 */     int pos = position();
/*  986 */     int limit = limit();
/*  987 */     int nextPos = pos + length;
/*  988 */     if (limit < nextPos) {
/*  989 */       throw new IndexOutOfBoundsException("position + length (" + nextPos + ") is greater " + "than limit (" + limit + ").");
/*      */     }
/*      */ 
/*  993 */     limit(pos + length);
/*  994 */     IoBuffer slice = slice();
/*  995 */     position(nextPos);
/*  996 */     limit(limit);
/*  997 */     return slice;
/*      */   }
/*      */ 
/*      */   protected abstract IoBuffer slice0();
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1011 */     int h = 1;
/* 1012 */     int p = position();
/* 1013 */     for (int i = limit() - 1; i >= p; i--) {
/* 1014 */       h = 31 * h + get(i);
/*      */     }
/* 1016 */     return h;
/*      */   }
/*      */ 
/*      */   public boolean equals(Object o)
/*      */   {
/* 1024 */     if (!(o instanceof IoBuffer)) {
/* 1025 */       return false;
/*      */     }
/*      */ 
/* 1028 */     IoBuffer that = (IoBuffer)o;
/* 1029 */     if (remaining() != that.remaining()) {
/* 1030 */       return false;
/*      */     }
/*      */ 
/* 1033 */     int p = position();
/* 1034 */     int i = limit() - 1; for (int j = that.limit() - 1; i >= p; j--) {
/* 1035 */       byte v1 = get(i);
/* 1036 */       byte v2 = that.get(j);
/* 1037 */       if (v1 != v2)
/* 1038 */         return false;
/* 1034 */       i--;
/*      */     }
/*      */ 
/* 1041 */     return true;
/*      */   }
/*      */ 
/*      */   public int compareTo(IoBuffer that)
/*      */   {
/* 1048 */     int n = position() + Math.min(remaining(), that.remaining());
/* 1049 */     int i = position(); for (int j = that.position(); i < n; j++) {
/* 1050 */       byte v1 = get(i);
/* 1051 */       byte v2 = that.get(j);
/* 1052 */       if (v1 != v2)
/*      */       {
/* 1055 */         if (v1 < v2) {
/* 1056 */           return -1;
/*      */         }
/*      */ 
/* 1059 */         return 1;
/*      */       }
/* 1049 */       i++;
/*      */     }
/*      */ 
/* 1061 */     return remaining() - that.remaining();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1069 */     StringBuilder buf = new StringBuilder();
/* 1070 */     if (isDirect())
/* 1071 */       buf.append("DirectBuffer");
/*      */     else {
/* 1073 */       buf.append("HeapBuffer");
/*      */     }
/* 1075 */     buf.append("[pos=");
/* 1076 */     buf.append(position());
/* 1077 */     buf.append(" lim=");
/* 1078 */     buf.append(limit());
/* 1079 */     buf.append(" cap=");
/* 1080 */     buf.append(capacity());
/* 1081 */     buf.append(": ");
/* 1082 */     buf.append(getHexDump(16));
/* 1083 */     buf.append(']');
/* 1084 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public IoBuffer get(byte[] dst)
/*      */   {
/* 1092 */     return get(dst, 0, dst.length);
/*      */   }
/*      */ 
/*      */   public IoBuffer put(IoBuffer src)
/*      */   {
/* 1100 */     return put(src.buf());
/*      */   }
/*      */ 
/*      */   public IoBuffer put(byte[] src)
/*      */   {
/* 1108 */     return put(src, 0, src.length);
/*      */   }
/*      */ 
/*      */   public int getUnsignedShort()
/*      */   {
/* 1116 */     return getShort() & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public int getUnsignedShort(int index)
/*      */   {
/* 1124 */     return getShort(index) & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public long getUnsignedInt()
/*      */   {
/* 1132 */     return getInt() & 0xFFFFFFFF;
/*      */   }
/*      */ 
/*      */   public int getMediumInt()
/*      */   {
/* 1140 */     byte b1 = get();
/* 1141 */     byte b2 = get();
/* 1142 */     byte b3 = get();
/* 1143 */     if (ByteOrder.BIG_ENDIAN.equals(order())) {
/* 1144 */       return getMediumInt(b1, b2, b3);
/*      */     }
/* 1146 */     return getMediumInt(b3, b2, b1);
/*      */   }
/*      */ 
/*      */   public int getUnsignedMediumInt()
/*      */   {
/* 1155 */     int b1 = getUnsigned();
/* 1156 */     int b2 = getUnsigned();
/* 1157 */     int b3 = getUnsigned();
/* 1158 */     if (ByteOrder.BIG_ENDIAN.equals(order())) {
/* 1159 */       return b1 << 16 | b2 << 8 | b3;
/*      */     }
/* 1161 */     return b3 << 16 | b2 << 8 | b1;
/*      */   }
/*      */ 
/*      */   public int getMediumInt(int index)
/*      */   {
/* 1170 */     byte b1 = get(index);
/* 1171 */     byte b2 = get(index + 1);
/* 1172 */     byte b3 = get(index + 2);
/* 1173 */     if (ByteOrder.BIG_ENDIAN.equals(order())) {
/* 1174 */       return getMediumInt(b1, b2, b3);
/*      */     }
/* 1176 */     return getMediumInt(b3, b2, b1);
/*      */   }
/*      */ 
/*      */   public int getUnsignedMediumInt(int index)
/*      */   {
/* 1185 */     int b1 = getUnsigned(index);
/* 1186 */     int b2 = getUnsigned(index + 1);
/* 1187 */     int b3 = getUnsigned(index + 2);
/* 1188 */     if (ByteOrder.BIG_ENDIAN.equals(order())) {
/* 1189 */       return b1 << 16 | b2 << 8 | b3;
/*      */     }
/* 1191 */     return b3 << 16 | b2 << 8 | b1;
/*      */   }
/*      */ 
/*      */   private int getMediumInt(byte b1, byte b2, byte b3)
/*      */   {
/* 1199 */     int ret = b1 << 16 & 0xFF0000 | b2 << 8 & 0xFF00 | b3 & 0xFF;
/*      */ 
/* 1201 */     if ((b1 & 0x80) == 128)
/*      */     {
/* 1203 */       ret |= -16777216;
/*      */     }
/* 1205 */     return ret;
/*      */   }
/*      */ 
/*      */   public IoBuffer putMediumInt(int value)
/*      */   {
/* 1213 */     byte b1 = (byte)(value >> 16);
/* 1214 */     byte b2 = (byte)(value >> 8);
/* 1215 */     byte b3 = (byte)value;
/*      */ 
/* 1217 */     if (ByteOrder.BIG_ENDIAN.equals(order()))
/* 1218 */       put(b1).put(b2).put(b3);
/*      */     else {
/* 1220 */       put(b3).put(b2).put(b1);
/*      */     }
/*      */ 
/* 1223 */     return this;
/*      */   }
/*      */ 
/*      */   public IoBuffer putMediumInt(int index, int value)
/*      */   {
/* 1231 */     byte b1 = (byte)(value >> 16);
/* 1232 */     byte b2 = (byte)(value >> 8);
/* 1233 */     byte b3 = (byte)value;
/*      */ 
/* 1235 */     if (ByteOrder.BIG_ENDIAN.equals(order()))
/* 1236 */       put(index, b1).put(index + 1, b2).put(index + 2, b3);
/*      */     else {
/* 1238 */       put(index, b3).put(index + 1, b2).put(index + 2, b1);
/*      */     }
/*      */ 
/* 1241 */     return this;
/*      */   }
/*      */ 
/*      */   public long getUnsignedInt(int index)
/*      */   {
/* 1249 */     return getInt(index) & 0xFFFFFFFF;
/*      */   }
/*      */ 
/*      */   public InputStream asInputStream()
/*      */   {
/* 1257 */     return new InputStream()
/*      */     {
/*      */       public int available() {
/* 1260 */         return AbstractIoBuffer.this.remaining();
/*      */       }
/*      */ 
/*      */       public synchronized void mark(int readlimit)
/*      */       {
/* 1265 */         AbstractIoBuffer.this.mark();
/*      */       }
/*      */ 
/*      */       public boolean markSupported()
/*      */       {
/* 1270 */         return true;
/*      */       }
/*      */ 
/*      */       public int read()
/*      */       {
/* 1275 */         if (AbstractIoBuffer.this.hasRemaining()) {
/* 1276 */           return AbstractIoBuffer.this.get() & 0xFF;
/*      */         }
/* 1278 */         return -1;
/*      */       }
/*      */ 
/*      */       public int read(byte[] b, int off, int len)
/*      */       {
/* 1284 */         int remaining = AbstractIoBuffer.this.remaining();
/* 1285 */         if (remaining > 0) {
/* 1286 */           int readBytes = Math.min(remaining, len);
/* 1287 */           AbstractIoBuffer.this.get(b, off, readBytes);
/* 1288 */           return readBytes;
/*      */         }
/* 1290 */         return -1;
/*      */       }
/*      */ 
/*      */       public synchronized void reset()
/*      */       {
/* 1296 */         AbstractIoBuffer.this.reset();
/*      */       }
/*      */ 
/*      */       public long skip(long n)
/*      */       {
/*      */         int bytes;
/*      */         int bytes;
/* 1302 */         if (n > 2147483647L)
/* 1303 */           bytes = AbstractIoBuffer.this.remaining();
/*      */         else {
/* 1305 */           bytes = Math.min(AbstractIoBuffer.this.remaining(), (int)n);
/*      */         }
/*      */ 
/* 1308 */         AbstractIoBuffer.this.skip(bytes);
/* 1309 */         return bytes;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public OutputStream asOutputStream()
/*      */   {
/* 1319 */     return new OutputStream()
/*      */     {
/*      */       public void write(byte[] b, int off, int len) {
/* 1322 */         AbstractIoBuffer.this.put(b, off, len);
/*      */       }
/*      */ 
/*      */       public void write(int b)
/*      */       {
/* 1327 */         AbstractIoBuffer.this.put((byte)b);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public String getHexDump()
/*      */   {
/* 1337 */     return getHexDump(2147483647);
/*      */   }
/*      */ 
/*      */   public String getHexDump(int lengthLimit)
/*      */   {
/* 1345 */     return IoBufferHexDumper.getHexdump(this, lengthLimit);
/*      */   }
/*      */ 
/*      */   public String getString(CharsetDecoder decoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1354 */     if (!hasRemaining()) {
/* 1355 */       return "";
/*      */     }
/*      */ 
/* 1358 */     boolean utf16 = decoder.charset().name().startsWith("UTF-16");
/*      */ 
/* 1360 */     int oldPos = position();
/* 1361 */     int oldLimit = limit();
/* 1362 */     int end = -1;
/*      */     int newPos;
/*      */     int newPos;
/* 1365 */     if (!utf16) {
/* 1366 */       end = indexOf(0);
/*      */       int newPos;
/* 1367 */       if (end < 0)
/* 1368 */         newPos = end = oldLimit;
/*      */       else
/* 1370 */         newPos = end + 1;
/*      */     }
/*      */     else {
/* 1373 */       int i = oldPos;
/*      */       while (true) {
/* 1375 */         boolean wasZero = get(i) == 0;
/* 1376 */         i++;
/*      */ 
/* 1378 */         if (i >= oldLimit)
/*      */         {
/*      */           break;
/*      */         }
/* 1382 */         if (get(i) != 0) {
/* 1383 */           i++;
/* 1384 */           if (i >= oldLimit) {
/* 1385 */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1391 */         if (wasZero) {
/* 1392 */           end = i - 1;
/* 1393 */           break;
/*      */         }
/*      */       }
/*      */       int newPos;
/* 1397 */       if (end < 0) {
/* 1398 */         newPos = end = oldPos + (oldLimit - oldPos & 0xFFFFFFFE);
/*      */       }
/*      */       else
/*      */       {
/*      */         int newPos;
/* 1400 */         if (end + 2 <= oldLimit)
/* 1401 */           newPos = end + 2;
/*      */         else {
/* 1403 */           newPos = end;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1408 */     if (oldPos == end) {
/* 1409 */       position(newPos);
/* 1410 */       return "";
/*      */     }
/*      */ 
/* 1413 */     limit(end);
/* 1414 */     decoder.reset();
/*      */ 
/* 1416 */     int expectedLength = (int)(remaining() * decoder.averageCharsPerByte()) + 1;
/* 1417 */     CharBuffer out = CharBuffer.allocate(expectedLength);
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1420 */       if (hasRemaining())
/* 1421 */         cr = decoder.decode(buf(), out, true);
/*      */       else {
/* 1423 */         cr = decoder.flush(out);
/*      */       }
/*      */ 
/* 1426 */       if (cr.isUnderflow())
/*      */       {
/*      */         break;
/*      */       }
/* 1430 */       if (cr.isOverflow()) {
/* 1431 */         CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
/*      */ 
/* 1433 */         out.flip();
/* 1434 */         o.put(out);
/* 1435 */         out = o;
/* 1436 */         continue;
/*      */       }
/*      */ 
/* 1439 */       if (cr.isError())
/*      */       {
/* 1441 */         limit(oldLimit);
/* 1442 */         position(oldPos);
/* 1443 */         cr.throwException();
/*      */       }
/*      */     }
/*      */ 
/* 1447 */     limit(oldLimit);
/* 1448 */     position(newPos);
/* 1449 */     return out.flip().toString();
/*      */   }
/*      */ 
/*      */   public String getString(int fieldSize, CharsetDecoder decoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1458 */     checkFieldSize(fieldSize);
/*      */ 
/* 1460 */     if (fieldSize == 0) {
/* 1461 */       return "";
/*      */     }
/*      */ 
/* 1464 */     if (!hasRemaining()) {
/* 1465 */       return "";
/*      */     }
/*      */ 
/* 1468 */     boolean utf16 = decoder.charset().name().startsWith("UTF-16");
/*      */ 
/* 1470 */     if ((utf16) && ((fieldSize & 0x1) != 0)) {
/* 1471 */       throw new IllegalArgumentException("fieldSize is not even.");
/*      */     }
/*      */ 
/* 1474 */     int oldPos = position();
/* 1475 */     int oldLimit = limit();
/* 1476 */     int end = oldPos + fieldSize;
/*      */ 
/* 1478 */     if (oldLimit < end) {
/* 1479 */       throw new BufferUnderflowException();
/*      */     }
/*      */ 
/* 1484 */     if (!utf16) {
/* 1485 */       for (int i = oldPos; (i < end) && 
/* 1486 */         (get(i) != 0); i++);
/* 1491 */       if (i == end)
/* 1492 */         limit(end);
/*      */       else
/* 1494 */         limit(i);
/*      */     }
/*      */     else {
/* 1497 */       for (int i = oldPos; (i < end) && (
/* 1498 */         (get(i) != 0) || (get(i + 1) != 0)); i += 2);
/* 1503 */       if (i == end)
/* 1504 */         limit(end);
/*      */       else {
/* 1506 */         limit(i);
/*      */       }
/*      */     }
/*      */ 
/* 1510 */     if (!hasRemaining()) {
/* 1511 */       limit(oldLimit);
/* 1512 */       position(end);
/* 1513 */       return "";
/*      */     }
/* 1515 */     decoder.reset();
/*      */ 
/* 1517 */     int expectedLength = (int)(remaining() * decoder.averageCharsPerByte()) + 1;
/* 1518 */     CharBuffer out = CharBuffer.allocate(expectedLength);
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1521 */       if (hasRemaining())
/* 1522 */         cr = decoder.decode(buf(), out, true);
/*      */       else {
/* 1524 */         cr = decoder.flush(out);
/*      */       }
/*      */ 
/* 1527 */       if (cr.isUnderflow())
/*      */       {
/*      */         break;
/*      */       }
/* 1531 */       if (cr.isOverflow()) {
/* 1532 */         CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
/*      */ 
/* 1534 */         out.flip();
/* 1535 */         o.put(out);
/* 1536 */         out = o;
/* 1537 */         continue;
/*      */       }
/*      */ 
/* 1540 */       if (cr.isError())
/*      */       {
/* 1542 */         limit(oldLimit);
/* 1543 */         position(oldPos);
/* 1544 */         cr.throwException();
/*      */       }
/*      */     }
/*      */ 
/* 1548 */     limit(oldLimit);
/* 1549 */     position(end);
/* 1550 */     return out.flip().toString();
/*      */   }
/*      */ 
/*      */   public IoBuffer putString(CharSequence val, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1559 */     if (val.length() == 0) {
/* 1560 */       return this;
/*      */     }
/*      */ 
/* 1563 */     CharBuffer in = CharBuffer.wrap(val);
/* 1564 */     encoder.reset();
/*      */ 
/* 1566 */     int expandedState = 0;
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1570 */       if (in.hasRemaining())
/* 1571 */         cr = encoder.encode(in, buf(), true);
/*      */       else {
/* 1573 */         cr = encoder.flush(buf());
/*      */       }
/*      */ 
/* 1576 */       if (cr.isUnderflow()) {
/*      */         break;
/*      */       }
/* 1579 */       if (cr.isOverflow()) {
/* 1580 */         if (isAutoExpand()) {
/* 1581 */           switch (expandedState) {
/*      */           case 0:
/* 1583 */             autoExpand((int)Math.ceil(in.remaining() * encoder.averageBytesPerChar()));
/*      */ 
/* 1585 */             expandedState++;
/* 1586 */             break;
/*      */           case 1:
/* 1588 */             autoExpand((int)Math.ceil(in.remaining() * encoder.maxBytesPerChar()));
/*      */ 
/* 1590 */             expandedState++;
/* 1591 */             break;
/*      */           default:
/* 1593 */             throw new RuntimeException("Expanded by " + (int)Math.ceil(in.remaining() * encoder.maxBytesPerChar()) + " but that wasn't enough for '" + val + "'");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1601 */         expandedState = 0;
/*      */       }
/* 1603 */       cr.throwException();
/*      */     }
/* 1605 */     return this;
/*      */   }
/*      */ 
/*      */   public IoBuffer putString(CharSequence val, int fieldSize, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1614 */     checkFieldSize(fieldSize);
/*      */ 
/* 1616 */     if (fieldSize == 0) {
/* 1617 */       return this;
/*      */     }
/*      */ 
/* 1620 */     autoExpand(fieldSize);
/*      */ 
/* 1622 */     boolean utf16 = encoder.charset().name().startsWith("UTF-16");
/*      */ 
/* 1624 */     if ((utf16) && ((fieldSize & 0x1) != 0)) {
/* 1625 */       throw new IllegalArgumentException("fieldSize is not even.");
/*      */     }
/*      */ 
/* 1628 */     int oldLimit = limit();
/* 1629 */     int end = position() + fieldSize;
/*      */ 
/* 1631 */     if (oldLimit < end) {
/* 1632 */       throw new BufferOverflowException();
/*      */     }
/*      */ 
/* 1635 */     if (val.length() == 0) {
/* 1636 */       if (!utf16) {
/* 1637 */         put(0);
/*      */       } else {
/* 1639 */         put(0);
/* 1640 */         put(0);
/*      */       }
/* 1642 */       position(end);
/* 1643 */       return this;
/*      */     }
/*      */ 
/* 1646 */     CharBuffer in = CharBuffer.wrap(val);
/* 1647 */     limit(end);
/* 1648 */     encoder.reset();
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1652 */       if (in.hasRemaining())
/* 1653 */         cr = encoder.encode(in, buf(), true);
/*      */       else {
/* 1655 */         cr = encoder.flush(buf());
/*      */       }
/*      */ 
/* 1658 */       if ((cr.isUnderflow()) || (cr.isOverflow())) {
/*      */         break;
/*      */       }
/* 1661 */       cr.throwException();
/*      */     }
/*      */ 
/* 1664 */     limit(oldLimit);
/*      */ 
/* 1666 */     if (position() < end) {
/* 1667 */       if (!utf16) {
/* 1668 */         put(0);
/*      */       } else {
/* 1670 */         put(0);
/* 1671 */         put(0);
/*      */       }
/*      */     }
/*      */ 
/* 1675 */     position(end);
/* 1676 */     return this;
/*      */   }
/*      */ 
/*      */   public String getPrefixedString(CharsetDecoder decoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1685 */     return getPrefixedString(2, decoder);
/*      */   }
/*      */ 
/*      */   public String getPrefixedString(int prefixLength, CharsetDecoder decoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1701 */     if (!prefixedDataAvailable(prefixLength)) {
/* 1702 */       throw new BufferUnderflowException();
/*      */     }
/*      */ 
/* 1705 */     int fieldSize = 0;
/*      */ 
/* 1707 */     switch (prefixLength) {
/*      */     case 1:
/* 1709 */       fieldSize = getUnsigned();
/* 1710 */       break;
/*      */     case 2:
/* 1712 */       fieldSize = getUnsignedShort();
/* 1713 */       break;
/*      */     case 4:
/* 1715 */       fieldSize = getInt();
/*      */     case 3:
/*      */     }
/*      */ 
/* 1719 */     if (fieldSize == 0) {
/* 1720 */       return "";
/*      */     }
/*      */ 
/* 1723 */     boolean utf16 = decoder.charset().name().startsWith("UTF-16");
/*      */ 
/* 1725 */     if ((utf16) && ((fieldSize & 0x1) != 0)) {
/* 1726 */       throw new BufferDataException("fieldSize is not even for a UTF-16 string.");
/*      */     }
/*      */ 
/* 1730 */     int oldLimit = limit();
/* 1731 */     int end = position() + fieldSize;
/*      */ 
/* 1733 */     if (oldLimit < end) {
/* 1734 */       throw new BufferUnderflowException();
/*      */     }
/*      */ 
/* 1737 */     limit(end);
/* 1738 */     decoder.reset();
/*      */ 
/* 1740 */     int expectedLength = (int)(remaining() * decoder.averageCharsPerByte()) + 1;
/* 1741 */     CharBuffer out = CharBuffer.allocate(expectedLength);
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1744 */       if (hasRemaining())
/* 1745 */         cr = decoder.decode(buf(), out, true);
/*      */       else {
/* 1747 */         cr = decoder.flush(out);
/*      */       }
/*      */ 
/* 1750 */       if (cr.isUnderflow())
/*      */       {
/*      */         break;
/*      */       }
/* 1754 */       if (cr.isOverflow()) {
/* 1755 */         CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
/*      */ 
/* 1757 */         out.flip();
/* 1758 */         o.put(out);
/* 1759 */         out = o;
/* 1760 */         continue;
/*      */       }
/*      */ 
/* 1763 */       cr.throwException();
/*      */     }
/*      */ 
/* 1766 */     limit(oldLimit);
/* 1767 */     position(end);
/* 1768 */     return out.flip().toString();
/*      */   }
/*      */ 
/*      */   public IoBuffer putPrefixedString(CharSequence in, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1777 */     return putPrefixedString(in, 2, 0, encoder);
/*      */   }
/*      */ 
/*      */   public IoBuffer putPrefixedString(CharSequence in, int prefixLength, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1786 */     return putPrefixedString(in, prefixLength, 0, encoder);
/*      */   }
/*      */ 
/*      */   public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/* 1796 */     return putPrefixedString(in, prefixLength, padding, 0, encoder);
/*      */   }
/*      */ 
/*      */   public IoBuffer putPrefixedString(CharSequence val, int prefixLength, int padding, byte padValue, CharsetEncoder encoder)
/*      */     throws CharacterCodingException
/*      */   {
/*      */     int maxLength;
/* 1807 */     switch (prefixLength) {
/*      */     case 1:
/* 1809 */       maxLength = 255;
/* 1810 */       break;
/*      */     case 2:
/* 1812 */       maxLength = 65535;
/* 1813 */       break;
/*      */     case 4:
/* 1815 */       maxLength = 2147483647;
/* 1816 */       break;
/*      */     case 3:
/*      */     default:
/* 1818 */       throw new IllegalArgumentException("prefixLength: " + prefixLength);
/*      */     }
/*      */ 
/* 1821 */     if (val.length() > maxLength) {
/* 1822 */       throw new IllegalArgumentException("The specified string is too long.");
/*      */     }
/*      */ 
/* 1825 */     if (val.length() == 0) {
/* 1826 */       switch (prefixLength) {
/*      */       case 1:
/* 1828 */         put(0);
/* 1829 */         break;
/*      */       case 2:
/* 1831 */         putShort(0);
/* 1832 */         break;
/*      */       case 4:
/* 1834 */         putInt(0);
/*      */       case 3:
/*      */       }
/* 1837 */       return this;
/*      */     }
/*      */     int padMask;
/* 1841 */     switch (padding) {
/*      */     case 0:
/*      */     case 1:
/* 1844 */       padMask = 0;
/* 1845 */       break;
/*      */     case 2:
/* 1847 */       padMask = 1;
/* 1848 */       break;
/*      */     case 4:
/* 1850 */       padMask = 3;
/* 1851 */       break;
/*      */     case 3:
/*      */     default:
/* 1853 */       throw new IllegalArgumentException("padding: " + padding);
/*      */     }
/*      */ 
/* 1856 */     CharBuffer in = CharBuffer.wrap(val);
/* 1857 */     skip(prefixLength);
/* 1858 */     int oldPos = position();
/* 1859 */     encoder.reset();
/*      */ 
/* 1861 */     int expandedState = 0;
/*      */     while (true)
/*      */     {
/*      */       CoderResult cr;
/*      */       CoderResult cr;
/* 1865 */       if (in.hasRemaining())
/* 1866 */         cr = encoder.encode(in, buf(), true);
/*      */       else {
/* 1868 */         cr = encoder.flush(buf());
/*      */       }
/*      */ 
/* 1871 */       if (position() - oldPos > maxLength) {
/* 1872 */         throw new IllegalArgumentException("The specified string is too long.");
/*      */       }
/*      */ 
/* 1876 */       if (cr.isUnderflow()) {
/*      */         break;
/*      */       }
/* 1879 */       if (cr.isOverflow()) {
/* 1880 */         if (isAutoExpand()) {
/* 1881 */           switch (expandedState) {
/*      */           case 0:
/* 1883 */             autoExpand((int)Math.ceil(in.remaining() * encoder.averageBytesPerChar()));
/*      */ 
/* 1885 */             expandedState++;
/* 1886 */             break;
/*      */           case 1:
/* 1888 */             autoExpand((int)Math.ceil(in.remaining() * encoder.maxBytesPerChar()));
/*      */ 
/* 1890 */             expandedState++;
/* 1891 */             break;
/*      */           default:
/* 1893 */             throw new RuntimeException("Expanded by " + (int)Math.ceil(in.remaining() * encoder.maxBytesPerChar()) + " but that wasn't enough for '" + val + "'");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1901 */         expandedState = 0;
/*      */       }
/* 1903 */       cr.throwException();
/*      */     }
/*      */ 
/* 1907 */     fill(padValue, padding - (position() - oldPos & padMask));
/* 1908 */     int length = position() - oldPos;
/* 1909 */     switch (prefixLength) {
/*      */     case 1:
/* 1911 */       put(oldPos - 1, (byte)length);
/* 1912 */       break;
/*      */     case 2:
/* 1914 */       putShort(oldPos - 2, (short)length);
/* 1915 */       break;
/*      */     case 4:
/* 1917 */       putInt(oldPos - 4, length);
/*      */     case 3:
/*      */     }
/* 1920 */     return this;
/*      */   }
/*      */ 
/*      */   public Object getObject()
/*      */     throws ClassNotFoundException
/*      */   {
/* 1928 */     return getObject(Thread.currentThread().getContextClassLoader());
/*      */   }
/*      */ 
/*      */   public Object getObject(ClassLoader classLoader)
/*      */     throws ClassNotFoundException
/*      */   {
/* 1937 */     if (!prefixedDataAvailable(4)) {
/* 1938 */       throw new BufferUnderflowException();
/*      */     }
/*      */ 
/* 1941 */     int length = getInt();
/* 1942 */     if (length <= 4) {
/* 1943 */       throw new BufferDataException("Object length should be greater than 4: " + length);
/*      */     }
/*      */ 
/* 1947 */     int oldLimit = limit();
/* 1948 */     limit(position() + length);
/*      */     try {
/* 1950 */       ObjectInputStream in = new ObjectInputStream(asInputStream(), classLoader)
/*      */       {
/*      */         protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
/*      */         {
/* 1954 */           int type = read();
/* 1955 */           if (type < 0) {
/* 1956 */             throw new EOFException();
/*      */           }
/* 1958 */           switch (type) {
/*      */           case 0:
/* 1960 */             return super.readClassDescriptor();
/*      */           case 1:
/* 1962 */             String className = readUTF();
/* 1963 */             Class clazz = Class.forName(className, true, this.val$classLoader);
/*      */ 
/* 1965 */             return ObjectStreamClass.lookup(clazz);
/*      */           }
/* 1967 */           throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
/*      */         }
/*      */ 
/*      */         protected Class<?> resolveClass(ObjectStreamClass desc)
/*      */           throws IOException, ClassNotFoundException
/*      */         {
/* 1975 */           String name = desc.getName();
/*      */           try {
/* 1977 */             return Class.forName(name, false, this.val$classLoader); } catch (ClassNotFoundException ex) {
/*      */           }
/* 1979 */           return super.resolveClass(desc);
/*      */         }
/*      */       };
/* 1983 */       Object localObject1 = in.readObject();
/*      */       return localObject1;
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 1985 */       throw new BufferDataException(e);
/*      */     } finally {
/* 1987 */       limit(oldLimit); } throw localObject2;
/*      */   }
/*      */ 
/*      */   public IoBuffer putObject(Object o)
/*      */   {
/* 1996 */     int oldPos = position();
/* 1997 */     skip(4);
/*      */     try {
/* 1999 */       ObjectOutputStream out = new ObjectOutputStream(asOutputStream())
/*      */       {
/*      */         protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException
/*      */         {
/* 2003 */           if (desc.forClass().isPrimitive()) {
/* 2004 */             write(0);
/* 2005 */             super.writeClassDescriptor(desc);
/*      */           } else {
/* 2007 */             write(1);
/* 2008 */             writeUTF(desc.getName());
/*      */           }
/*      */         }
/*      */       };
/* 2012 */       out.writeObject(o);
/* 2013 */       out.flush();
/*      */     } catch (IOException e) {
/* 2015 */       throw new BufferDataException(e);
/*      */     }
/*      */ 
/* 2019 */     int newPos = position();
/* 2020 */     position(oldPos);
/* 2021 */     putInt(newPos - oldPos - 4);
/* 2022 */     position(newPos);
/* 2023 */     return this;
/*      */   }
/*      */ 
/*      */   public boolean prefixedDataAvailable(int prefixLength)
/*      */   {
/* 2031 */     return prefixedDataAvailable(prefixLength, 2147483647);
/*      */   }
/*      */ 
/*      */   public boolean prefixedDataAvailable(int prefixLength, int maxDataLength)
/*      */   {
/* 2039 */     if (remaining() < prefixLength)
/* 2040 */       return false;
/*      */     int dataLength;
/* 2044 */     switch (prefixLength) {
/*      */     case 1:
/* 2046 */       dataLength = getUnsigned(position());
/* 2047 */       break;
/*      */     case 2:
/* 2049 */       dataLength = getUnsignedShort(position());
/* 2050 */       break;
/*      */     case 4:
/* 2052 */       dataLength = getInt(position());
/* 2053 */       break;
/*      */     case 3:
/*      */     default:
/* 2055 */       throw new IllegalArgumentException("prefixLength: " + prefixLength);
/*      */     }
/*      */ 
/* 2058 */     if ((dataLength < 0) || (dataLength > maxDataLength)) {
/* 2059 */       throw new BufferDataException("dataLength: " + dataLength);
/*      */     }
/*      */ 
/* 2062 */     return remaining() - prefixLength >= dataLength;
/*      */   }
/*      */ 
/*      */   public int indexOf(byte b)
/*      */   {
/* 2070 */     if (hasArray()) {
/* 2071 */       int arrayOffset = arrayOffset();
/* 2072 */       int beginPos = arrayOffset + position();
/* 2073 */       int limit = arrayOffset + limit();
/* 2074 */       byte[] array = array();
/*      */ 
/* 2076 */       for (int i = beginPos; i < limit; i++)
/* 2077 */         if (array[i] == b)
/* 2078 */           return i - arrayOffset;
/*      */     }
/*      */     else
/*      */     {
/* 2082 */       int beginPos = position();
/* 2083 */       int limit = limit();
/*      */ 
/* 2085 */       for (int i = beginPos; i < limit; i++) {
/* 2086 */         if (get(i) == b) {
/* 2087 */           return i;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2092 */     return -1;
/*      */   }
/*      */ 
/*      */   public IoBuffer skip(int size)
/*      */   {
/* 2100 */     autoExpand(size);
/* 2101 */     return position(position() + size);
/*      */   }
/*      */ 
/*      */   public IoBuffer fill(byte value, int size)
/*      */   {
/* 2109 */     autoExpand(size);
/* 2110 */     int q = size >>> 3;
/* 2111 */     int r = size & 0x7;
/*      */ 
/* 2113 */     if (q > 0) {
/* 2114 */       int intValue = value | value << 8 | value << 16 | value << 24;
/* 2115 */       long longValue = intValue;
/* 2116 */       longValue <<= 32;
/* 2117 */       longValue |= intValue;
/*      */ 
/* 2119 */       for (int i = q; i > 0; i--) {
/* 2120 */         putLong(longValue);
/*      */       }
/*      */     }
/*      */ 
/* 2124 */     q = r >>> 2;
/* 2125 */     r &= 3;
/*      */ 
/* 2127 */     if (q > 0) {
/* 2128 */       int intValue = value | value << 8 | value << 16 | value << 24;
/* 2129 */       putInt(intValue);
/*      */     }
/*      */ 
/* 2132 */     q = r >> 1;
/* 2133 */     r &= 1;
/*      */ 
/* 2135 */     if (q > 0) {
/* 2136 */       short shortValue = (short)(value | value << 8);
/* 2137 */       putShort(shortValue);
/*      */     }
/*      */ 
/* 2140 */     if (r > 0) {
/* 2141 */       put(value);
/*      */     }
/*      */ 
/* 2144 */     return this;
/*      */   }
/*      */ 
/*      */   public IoBuffer fillAndReset(byte value, int size)
/*      */   {
/* 2152 */     autoExpand(size);
/* 2153 */     int pos = position();
/*      */     try {
/* 2155 */       fill(value, size);
/*      */     } finally {
/* 2157 */       position(pos);
/*      */     }
/* 2159 */     return this;
/*      */   }
/*      */ 
/*      */   public IoBuffer fill(int size)
/*      */   {
/* 2167 */     autoExpand(size);
/* 2168 */     int q = size >>> 3;
/* 2169 */     int r = size & 0x7;
/*      */ 
/* 2171 */     for (int i = q; i > 0; i--) {
/* 2172 */       putLong(0L);
/*      */     }
/*      */ 
/* 2175 */     q = r >>> 2;
/* 2176 */     r &= 3;
/*      */ 
/* 2178 */     if (q > 0) {
/* 2179 */       putInt(0);
/*      */     }
/*      */ 
/* 2182 */     q = r >> 1;
/* 2183 */     r &= 1;
/*      */ 
/* 2185 */     if (q > 0) {
/* 2186 */       putShort(0);
/*      */     }
/*      */ 
/* 2189 */     if (r > 0) {
/* 2190 */       put(0);
/*      */     }
/*      */ 
/* 2193 */     return this;
/*      */   }
/*      */ 
/*      */   public IoBuffer fillAndReset(int size)
/*      */   {
/* 2201 */     autoExpand(size);
/* 2202 */     int pos = position();
/*      */     try {
/* 2204 */       fill(size);
/*      */     } finally {
/* 2206 */       position(pos);
/*      */     }
/*      */ 
/* 2209 */     return this;
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnum(Class<E> enumClass)
/*      */   {
/* 2217 */     return (Enum)toEnum(enumClass, getUnsigned());
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnum(int index, Class<E> enumClass)
/*      */   {
/* 2225 */     return (Enum)toEnum(enumClass, getUnsigned(index));
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnumShort(Class<E> enumClass)
/*      */   {
/* 2233 */     return (Enum)toEnum(enumClass, getUnsignedShort());
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnumShort(int index, Class<E> enumClass)
/*      */   {
/* 2241 */     return (Enum)toEnum(enumClass, getUnsignedShort(index));
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnumInt(Class<E> enumClass)
/*      */   {
/* 2249 */     return (Enum)toEnum(enumClass, getInt());
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> E getEnumInt(int index, Class<E> enumClass)
/*      */   {
/* 2256 */     return (Enum)toEnum(enumClass, getInt(index));
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnum(Enum<?> e)
/*      */   {
/* 2264 */     if (e.ordinal() > 255L) {
/* 2265 */       throw new IllegalArgumentException(enumConversionErrorMessage(e, "byte"));
/*      */     }
/*      */ 
/* 2268 */     return put((byte)e.ordinal());
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnum(int index, Enum<?> e)
/*      */   {
/* 2276 */     if (e.ordinal() > 255L) {
/* 2277 */       throw new IllegalArgumentException(enumConversionErrorMessage(e, "byte"));
/*      */     }
/*      */ 
/* 2280 */     return put(index, (byte)e.ordinal());
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnumShort(Enum<?> e)
/*      */   {
/* 2288 */     if (e.ordinal() > 65535L) {
/* 2289 */       throw new IllegalArgumentException(enumConversionErrorMessage(e, "short"));
/*      */     }
/*      */ 
/* 2292 */     return putShort((short)e.ordinal());
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnumShort(int index, Enum<?> e)
/*      */   {
/* 2300 */     if (e.ordinal() > 65535L) {
/* 2301 */       throw new IllegalArgumentException(enumConversionErrorMessage(e, "short"));
/*      */     }
/*      */ 
/* 2304 */     return putShort(index, (short)e.ordinal());
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnumInt(Enum<?> e)
/*      */   {
/* 2312 */     return putInt(e.ordinal());
/*      */   }
/*      */ 
/*      */   public IoBuffer putEnumInt(int index, Enum<?> e)
/*      */   {
/* 2320 */     return putInt(index, e.ordinal());
/*      */   }
/*      */ 
/*      */   private <E> E toEnum(Class<E> enumClass, int i) {
/* 2324 */     Object[] enumConstants = enumClass.getEnumConstants();
/* 2325 */     if (i > enumConstants.length) {
/* 2326 */       throw new IndexOutOfBoundsException(String.format("%d is too large of an ordinal to convert to the enum %s", new Object[] { Integer.valueOf(i), enumClass.getName() }));
/*      */     }
/*      */ 
/* 2330 */     return enumConstants[i];
/*      */   }
/*      */ 
/*      */   private String enumConversionErrorMessage(Enum<?> e, String type) {
/* 2334 */     return String.format("%s.%s has an ordinal value too large for a %s", new Object[] { e.getClass().getName(), e.name(), type });
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass)
/*      */   {
/* 2343 */     return toEnumSet(enumClass, get() & 0xFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSet(int index, Class<E> enumClass)
/*      */   {
/* 2352 */     return toEnumSet(enumClass, get(index) & 0xFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetShort(Class<E> enumClass)
/*      */   {
/* 2360 */     return toEnumSet(enumClass, getShort() & 0xFFFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetShort(int index, Class<E> enumClass)
/*      */   {
/* 2369 */     return toEnumSet(enumClass, getShort(index) & 0xFFFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetInt(Class<E> enumClass)
/*      */   {
/* 2377 */     return toEnumSet(enumClass, getInt() & 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetInt(int index, Class<E> enumClass)
/*      */   {
/* 2386 */     return toEnumSet(enumClass, getInt(index) & 0xFFFFFFFF);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetLong(Class<E> enumClass)
/*      */   {
/* 2394 */     return toEnumSet(enumClass, getLong());
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> EnumSet<E> getEnumSetLong(int index, Class<E> enumClass)
/*      */   {
/* 2403 */     return toEnumSet(enumClass, getLong(index));
/*      */   }
/*      */ 
/*      */   private <E extends Enum<E>> EnumSet<E> toEnumSet(Class<E> clazz, long vector) {
/* 2407 */     EnumSet set = EnumSet.noneOf(clazz);
/* 2408 */     long mask = 1L;
/* 2409 */     for (Enum e : (Enum[])clazz.getEnumConstants()) {
/* 2410 */       if ((mask & vector) == mask) {
/* 2411 */         set.add(e);
/*      */       }
/* 2413 */       mask <<= 1;
/*      */     }
/* 2415 */     return set;
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSet(Set<E> set)
/*      */   {
/* 2423 */     long vector = toLong(set);
/* 2424 */     if ((vector & 0xFFFFFF00) != 0L) {
/* 2425 */       throw new IllegalArgumentException("The enum set is too large to fit in a byte: " + set);
/*      */     }
/*      */ 
/* 2428 */     return put((byte)(int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSet(int index, Set<E> set)
/*      */   {
/* 2436 */     long vector = toLong(set);
/* 2437 */     if ((vector & 0xFFFFFF00) != 0L) {
/* 2438 */       throw new IllegalArgumentException("The enum set is too large to fit in a byte: " + set);
/*      */     }
/*      */ 
/* 2441 */     return put(index, (byte)(int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetShort(Set<E> set)
/*      */   {
/* 2449 */     long vector = toLong(set);
/* 2450 */     if ((vector & 0xFFFF0000) != 0L) {
/* 2451 */       throw new IllegalArgumentException("The enum set is too large to fit in a short: " + set);
/*      */     }
/*      */ 
/* 2454 */     return putShort((short)(int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetShort(int index, Set<E> set)
/*      */   {
/* 2462 */     long vector = toLong(set);
/* 2463 */     if ((vector & 0xFFFF0000) != 0L) {
/* 2464 */       throw new IllegalArgumentException("The enum set is too large to fit in a short: " + set);
/*      */     }
/*      */ 
/* 2467 */     return putShort(index, (short)(int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetInt(Set<E> set)
/*      */   {
/* 2475 */     long vector = toLong(set);
/* 2476 */     if ((vector & 0x0) != 0L) {
/* 2477 */       throw new IllegalArgumentException("The enum set is too large to fit in an int: " + set);
/*      */     }
/*      */ 
/* 2480 */     return putInt((int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetInt(int index, Set<E> set)
/*      */   {
/* 2488 */     long vector = toLong(set);
/* 2489 */     if ((vector & 0x0) != 0L) {
/* 2490 */       throw new IllegalArgumentException("The enum set is too large to fit in an int: " + set);
/*      */     }
/*      */ 
/* 2493 */     return putInt(index, (int)vector);
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetLong(Set<E> set)
/*      */   {
/* 2501 */     return putLong(toLong(set));
/*      */   }
/*      */ 
/*      */   public <E extends Enum<E>> IoBuffer putEnumSetLong(int index, Set<E> set)
/*      */   {
/* 2509 */     return putLong(index, toLong(set));
/*      */   }
/*      */ 
/*      */   private <E extends Enum<E>> long toLong(Set<E> set) {
/* 2513 */     long vector = 0L;
/* 2514 */     for (Enum e : set) {
/* 2515 */       if (e.ordinal() >= 64) {
/* 2516 */         throw new IllegalArgumentException("The enum set is too large to fit in a bit vector: " + set);
/*      */       }
/*      */ 
/* 2520 */       vector |= 1L << e.ordinal();
/*      */     }
/* 2522 */     return vector;
/*      */   }
/*      */ 
/*      */   private IoBuffer autoExpand(int expectedRemaining)
/*      */   {
/* 2530 */     if (isAutoExpand()) {
/* 2531 */       expand(expectedRemaining, true);
/*      */     }
/* 2533 */     return this;
/*      */   }
/*      */ 
/*      */   private IoBuffer autoExpand(int pos, int expectedRemaining)
/*      */   {
/* 2541 */     if (isAutoExpand()) {
/* 2542 */       expand(pos, expectedRemaining, true);
/*      */     }
/* 2544 */     return this;
/*      */   }
/*      */ 
/*      */   private static void checkFieldSize(int fieldSize) {
/* 2548 */     if (fieldSize < 0)
/* 2549 */       throw new IllegalArgumentException("fieldSize cannot be negative: " + fieldSize);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.buffer.AbstractIoBuffer
 * JD-Core Version:    0.6.0
 */
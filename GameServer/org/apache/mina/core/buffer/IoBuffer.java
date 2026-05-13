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
/*     */ public abstract class IoBuffer
/*     */   implements Comparable<IoBuffer>
/*     */ {
/* 149 */   private static IoBufferAllocator allocator = new SimpleBufferAllocator();
/*     */ 
/* 152 */   private static boolean useDirectBuffer = false;
/*     */ 
/*     */   public static IoBufferAllocator getAllocator()
/*     */   {
/* 158 */     return allocator;
/*     */   }
/*     */ 
/*     */   public static void setAllocator(IoBufferAllocator newAllocator)
/*     */   {
/* 165 */     if (newAllocator == null) {
/* 166 */       throw new NullPointerException("allocator");
/*     */     }
/*     */ 
/* 169 */     IoBufferAllocator oldAllocator = allocator;
/*     */ 
/* 171 */     allocator = newAllocator;
/*     */ 
/* 173 */     if (null != oldAllocator)
/* 174 */       oldAllocator.dispose();
/*     */   }
/*     */ 
/*     */   public static boolean isUseDirectBuffer()
/*     */   {
/* 184 */     return useDirectBuffer;
/*     */   }
/*     */ 
/*     */   public static void setUseDirectBuffer(boolean useDirectBuffer)
/*     */   {
/* 193 */     useDirectBuffer = useDirectBuffer;
/*     */   }
/*     */ 
/*     */   public static IoBuffer allocate(int capacity)
/*     */   {
/* 205 */     return allocate(capacity, useDirectBuffer);
/*     */   }
/*     */ 
/*     */   public static IoBuffer allocate(int capacity, boolean direct)
/*     */   {
/* 216 */     if (capacity < 0) {
/* 217 */       throw new IllegalArgumentException("capacity: " + capacity);
/*     */     }
/*     */ 
/* 220 */     return allocator.allocate(capacity, direct);
/*     */   }
/*     */ 
/*     */   public static IoBuffer wrap(ByteBuffer nioBuffer)
/*     */   {
/* 227 */     return allocator.wrap(nioBuffer);
/*     */   }
/*     */ 
/*     */   public static IoBuffer wrap(byte[] byteArray)
/*     */   {
/* 234 */     return wrap(ByteBuffer.wrap(byteArray));
/*     */   }
/*     */ 
/*     */   public static IoBuffer wrap(byte[] byteArray, int offset, int length)
/*     */   {
/* 241 */     return wrap(ByteBuffer.wrap(byteArray, offset, length));
/*     */   }
/*     */ 
/*     */   protected static int normalizeCapacity(int requestedCapacity)
/*     */   {
/* 251 */     switch (requestedCapacity) {
/*     */     case 0:
/*     */     case 1:
/*     */     case 2:
/*     */     case 4:
/*     */     case 8:
/*     */     case 16:
/*     */     case 32:
/*     */     case 64:
/*     */     case 128:
/*     */     case 256:
/*     */     case 512:
/*     */     case 1024:
/*     */     case 2048:
/*     */     case 4096:
/*     */     case 8192:
/*     */     case 16384:
/*     */     case 32768:
/*     */     case 65536:
/*     */     case 131072:
/*     */     case 262144:
/*     */     case 524288:
/*     */     case 2097152:
/*     */     case 4194304:
/*     */     case 8388608:
/*     */     case 16777216:
/*     */     case 33554432:
/*     */     case 67108864:
/*     */     case 134217728:
/*     */     case 268435456:
/*     */     case 536870912:
/*     */     case 1073741824:
/*     */     case 2147483647:
/* 284 */       return requestedCapacity;
/*     */     }
/*     */ 
/* 287 */     int newCapacity = 1;
/* 288 */     while (newCapacity < requestedCapacity) {
/* 289 */       newCapacity <<= 1;
/* 290 */       if (newCapacity < 0) {
/* 291 */         return 2147483647;
/*     */       }
/*     */     }
/* 294 */     return newCapacity;
/*     */   }
/*     */ 
/*     */   public abstract void free();
/*     */ 
/*     */   public abstract ByteBuffer buf();
/*     */ 
/*     */   public abstract boolean isDirect();
/*     */ 
/*     */   public abstract boolean isDerived();
/*     */ 
/*     */   public abstract boolean isReadOnly();
/*     */ 
/*     */   public abstract int minimumCapacity();
/*     */ 
/*     */   public abstract IoBuffer minimumCapacity(int paramInt);
/*     */ 
/*     */   public abstract int capacity();
/*     */ 
/*     */   public abstract IoBuffer capacity(int paramInt);
/*     */ 
/*     */   public abstract boolean isAutoExpand();
/*     */ 
/*     */   public abstract IoBuffer setAutoExpand(boolean paramBoolean);
/*     */ 
/*     */   public abstract boolean isAutoShrink();
/*     */ 
/*     */   public abstract IoBuffer setAutoShrink(boolean paramBoolean);
/*     */ 
/*     */   public abstract IoBuffer expand(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer expand(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IoBuffer shrink();
/*     */ 
/*     */   public abstract int position();
/*     */ 
/*     */   public abstract IoBuffer position(int paramInt);
/*     */ 
/*     */   public abstract int limit();
/*     */ 
/*     */   public abstract IoBuffer limit(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer mark();
/*     */ 
/*     */   public abstract int markValue();
/*     */ 
/*     */   public abstract IoBuffer reset();
/*     */ 
/*     */   public abstract IoBuffer clear();
/*     */ 
/*     */   public abstract IoBuffer sweep();
/*     */ 
/*     */   public abstract IoBuffer sweep(byte paramByte);
/*     */ 
/*     */   public abstract IoBuffer flip();
/*     */ 
/*     */   public abstract IoBuffer rewind();
/*     */ 
/*     */   public abstract int remaining();
/*     */ 
/*     */   public abstract boolean hasRemaining();
/*     */ 
/*     */   public abstract IoBuffer duplicate();
/*     */ 
/*     */   public abstract IoBuffer slice();
/*     */ 
/*     */   public abstract IoBuffer asReadOnlyBuffer();
/*     */ 
/*     */   public abstract boolean hasArray();
/*     */ 
/*     */   public abstract byte[] array();
/*     */ 
/*     */   public abstract int arrayOffset();
/*     */ 
/*     */   public abstract byte get();
/*     */ 
/*     */   public abstract short getUnsigned();
/*     */ 
/*     */   public abstract IoBuffer put(byte paramByte);
/*     */ 
/*     */   public abstract byte get(int paramInt);
/*     */ 
/*     */   public abstract short getUnsigned(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer put(int paramInt, byte paramByte);
/*     */ 
/*     */   public abstract IoBuffer get(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IoBuffer get(byte[] paramArrayOfByte);
/*     */ 
/*     */   public abstract IoBuffer getSlice(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IoBuffer getSlice(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer put(ByteBuffer paramByteBuffer);
/*     */ 
/*     */   public abstract IoBuffer put(IoBuffer paramIoBuffer);
/*     */ 
/*     */   public abstract IoBuffer put(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IoBuffer put(byte[] paramArrayOfByte);
/*     */ 
/*     */   public abstract IoBuffer compact();
/*     */ 
/*     */   public abstract ByteOrder order();
/*     */ 
/*     */   public abstract IoBuffer order(ByteOrder paramByteOrder);
/*     */ 
/*     */   public abstract char getChar();
/*     */ 
/*     */   public abstract IoBuffer putChar(char paramChar);
/*     */ 
/*     */   public abstract char getChar(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putChar(int paramInt, char paramChar);
/*     */ 
/*     */   public abstract CharBuffer asCharBuffer();
/*     */ 
/*     */   public abstract short getShort();
/*     */ 
/*     */   public abstract int getUnsignedShort();
/*     */ 
/*     */   public abstract IoBuffer putShort(short paramShort);
/*     */ 
/*     */   public abstract short getShort(int paramInt);
/*     */ 
/*     */   public abstract int getUnsignedShort(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putShort(int paramInt, short paramShort);
/*     */ 
/*     */   public abstract ShortBuffer asShortBuffer();
/*     */ 
/*     */   public abstract int getInt();
/*     */ 
/*     */   public abstract long getUnsignedInt();
/*     */ 
/*     */   public abstract int getMediumInt();
/*     */ 
/*     */   public abstract int getUnsignedMediumInt();
/*     */ 
/*     */   public abstract int getMediumInt(int paramInt);
/*     */ 
/*     */   public abstract int getUnsignedMediumInt(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putMediumInt(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putMediumInt(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IoBuffer putInt(int paramInt);
/*     */ 
/*     */   public abstract int getInt(int paramInt);
/*     */ 
/*     */   public abstract long getUnsignedInt(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putInt(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract IntBuffer asIntBuffer();
/*     */ 
/*     */   public abstract long getLong();
/*     */ 
/*     */   public abstract IoBuffer putLong(long paramLong);
/*     */ 
/*     */   public abstract long getLong(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putLong(int paramInt, long paramLong);
/*     */ 
/*     */   public abstract LongBuffer asLongBuffer();
/*     */ 
/*     */   public abstract float getFloat();
/*     */ 
/*     */   public abstract IoBuffer putFloat(float paramFloat);
/*     */ 
/*     */   public abstract float getFloat(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putFloat(int paramInt, float paramFloat);
/*     */ 
/*     */   public abstract FloatBuffer asFloatBuffer();
/*     */ 
/*     */   public abstract double getDouble();
/*     */ 
/*     */   public abstract IoBuffer putDouble(double paramDouble);
/*     */ 
/*     */   public abstract double getDouble(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer putDouble(int paramInt, double paramDouble);
/*     */ 
/*     */   public abstract DoubleBuffer asDoubleBuffer();
/*     */ 
/*     */   public abstract InputStream asInputStream();
/*     */ 
/*     */   public abstract OutputStream asOutputStream();
/*     */ 
/*     */   public abstract String getHexDump();
/*     */ 
/*     */   public abstract String getHexDump(int paramInt);
/*     */ 
/*     */   public abstract String getString(CharsetDecoder paramCharsetDecoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract String getString(int paramInt, CharsetDecoder paramCharsetDecoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putString(CharSequence paramCharSequence, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putString(CharSequence paramCharSequence, int paramInt, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract String getPrefixedString(CharsetDecoder paramCharsetDecoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract String getPrefixedString(int paramInt, CharsetDecoder paramCharsetDecoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putPrefixedString(CharSequence paramCharSequence, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putPrefixedString(CharSequence paramCharSequence, int paramInt, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putPrefixedString(CharSequence paramCharSequence, int paramInt1, int paramInt2, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract IoBuffer putPrefixedString(CharSequence paramCharSequence, int paramInt1, int paramInt2, byte paramByte, CharsetEncoder paramCharsetEncoder)
/*     */     throws CharacterCodingException;
/*     */ 
/*     */   public abstract Object getObject()
/*     */     throws ClassNotFoundException;
/*     */ 
/*     */   public abstract Object getObject(ClassLoader paramClassLoader)
/*     */     throws ClassNotFoundException;
/*     */ 
/*     */   public abstract IoBuffer putObject(Object paramObject);
/*     */ 
/*     */   public abstract boolean prefixedDataAvailable(int paramInt);
/*     */ 
/*     */   public abstract boolean prefixedDataAvailable(int paramInt1, int paramInt2);
/*     */ 
/*     */   public abstract int indexOf(byte paramByte);
/*     */ 
/*     */   public abstract IoBuffer skip(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer fill(byte paramByte, int paramInt);
/*     */ 
/*     */   public abstract IoBuffer fillAndReset(byte paramByte, int paramInt);
/*     */ 
/*     */   public abstract IoBuffer fill(int paramInt);
/*     */ 
/*     */   public abstract IoBuffer fillAndReset(int paramInt);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnum(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnum(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnumShort(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnumShort(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnumInt(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> E getEnumInt(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract IoBuffer putEnum(Enum<?> paramEnum);
/*     */ 
/*     */   public abstract IoBuffer putEnum(int paramInt, Enum<?> paramEnum);
/*     */ 
/*     */   public abstract IoBuffer putEnumShort(Enum<?> paramEnum);
/*     */ 
/*     */   public abstract IoBuffer putEnumShort(int paramInt, Enum<?> paramEnum);
/*     */ 
/*     */   public abstract IoBuffer putEnumInt(Enum<?> paramEnum);
/*     */ 
/*     */   public abstract IoBuffer putEnumInt(int paramInt, Enum<?> paramEnum);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSet(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetShort(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetShort(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetInt(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetInt(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetLong(Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> EnumSet<E> getEnumSetLong(int paramInt, Class<E> paramClass);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSet(Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSet(int paramInt, Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetShort(Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetShort(int paramInt, Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetInt(Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetInt(int paramInt, Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetLong(Set<E> paramSet);
/*     */ 
/*     */   public abstract <E extends Enum<E>> IoBuffer putEnumSetLong(int paramInt, Set<E> paramSet);
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.buffer.IoBuffer
 * JD-Core Version:    0.6.0
 */
/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ abstract class Buffer
/*     */ {
/*     */   static final int MAX_BYTES_TO_DUMP = 512;
/*     */   static final int NO_LENGTH_LIMIT = -1;
/*     */   static final long NULL_LENGTH = -1L;
/*  70 */   protected boolean wasMultiPacket = false;
/*     */ 
/*     */   public static Buffer allocateDirect(int size, boolean useNewIo)
/*     */   {
/*  47 */     if (!useNewIo) {
/*  48 */       return allocateNew(size, useNewIo);
/*     */     }
/*     */ 
/*  51 */     return new ChannelBuffer(size, true);
/*     */   }
/*     */ 
/*     */   public static Buffer allocateNew(byte[] buf, boolean useNewIo) {
/*  55 */     if (!useNewIo) {
/*  56 */       return new ByteArrayBuffer(buf);
/*     */     }
/*     */ 
/*  59 */     return new ChannelBuffer(buf);
/*     */   }
/*     */ 
/*     */   public static Buffer allocateNew(int size, boolean useNewIo) {
/*  63 */     if (!useNewIo) {
/*  64 */       return new ByteArrayBuffer(size);
/*     */     }
/*     */ 
/*  67 */     return new ChannelBuffer(size, true);
/*     */   }
/*     */ 
/*     */   abstract void clear();
/*     */ 
/*     */   final void dump()
/*     */   {
/*  75 */     dump(getBufLength());
/*     */   }
/*     */ 
/*     */   final String dump(int numBytes) {
/*  79 */     return StringUtils.dumpAsHex(getBytes(0, numBytes > getBufLength() ? getBufLength() : numBytes), numBytes > getBufLength() ? getBufLength() : numBytes);
/*     */   }
/*     */ 
/*     */   final String dumpClampedBytes(int numBytes)
/*     */   {
/*  85 */     int numBytesToDump = numBytes < 512 ? numBytes : 512;
/*     */ 
/*  88 */     String dumped = StringUtils.dumpAsHex(getBytes(0, numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump), numBytesToDump > getBufLength() ? getBufLength() : numBytesToDump);
/*     */ 
/*  94 */     if (numBytesToDump < numBytes) {
/*  95 */       return dumped + " ....(packet exceeds max. dump length)";
/*     */     }
/*     */ 
/*  98 */     return dumped;
/*     */   }
/*     */ 
/*     */   final void dumpHeader() {
/* 102 */     for (int i = 0; i < 4; i++) {
/* 103 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */ 
/* 105 */       if (hexVal.length() == 1) {
/* 106 */         hexVal = "0" + hexVal;
/*     */       }
/*     */ 
/* 109 */       System.out.print(hexVal + " ");
/*     */     }
/*     */   }
/*     */ 
/*     */   final void dumpNBytes(int start, int nBytes) {
/* 114 */     StringBuffer asciiBuf = new StringBuffer();
/*     */ 
/* 116 */     for (int i = start; (i < start + nBytes) && (i < getBufLength()); i++) {
/* 117 */       String hexVal = Integer.toHexString(readByte(i) & 0xFF);
/*     */ 
/* 119 */       if (hexVal.length() == 1) {
/* 120 */         hexVal = "0" + hexVal;
/*     */       }
/*     */ 
/* 123 */       System.out.print(hexVal + " ");
/*     */ 
/* 125 */       if ((readByte(i) > 32) && (readByte(i) < 127))
/* 126 */         asciiBuf.append((char)readByte(i));
/*     */       else {
/* 128 */         asciiBuf.append(".");
/*     */       }
/*     */ 
/* 131 */       asciiBuf.append(" ");
/*     */     }
/*     */ 
/* 134 */     System.out.println("    " + asciiBuf.toString());
/*     */   }
/*     */ 
/*     */   abstract void ensureCapacity(int paramInt)
/*     */     throws SQLException;
/*     */ 
/*     */   public abstract int fastSkipLenString();
/*     */ 
/*     */   abstract int getBufLength();
/*     */ 
/*     */   public abstract byte[] getByteBuffer();
/*     */ 
/*     */   abstract byte[] getBytes(int paramInt);
/*     */ 
/*     */   abstract byte[] getBytes(int paramInt1, int paramInt2);
/*     */ 
/*     */   abstract int getCapacity();
/*     */ 
/*     */   public abstract ByteBuffer getNioBuffer();
/*     */ 
/*     */   public abstract int getPosition();
/*     */ 
/*     */   abstract boolean isLastDataPacket();
/*     */ 
/*     */   abstract long newReadLength();
/*     */ 
/*     */   abstract byte readByte();
/*     */ 
/*     */   abstract byte readByte(int paramInt);
/*     */ 
/*     */   abstract long readFieldLength();
/*     */ 
/*     */   abstract int readInt();
/*     */ 
/*     */   abstract int readIntAsLong();
/*     */ 
/*     */   abstract byte[] readLenByteArray(int paramInt);
/*     */ 
/*     */   abstract long readLength();
/*     */ 
/*     */   abstract long readLong();
/*     */ 
/*     */   abstract int readLongInt();
/*     */ 
/*     */   abstract long readLongLong();
/*     */ 
/*     */   abstract int readnBytes();
/*     */ 
/*     */   abstract String readString();
/*     */ 
/*     */   abstract String readString(String paramString)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void setBufLength(int paramInt);
/*     */ 
/*     */   public abstract void setByteBuffer(byte[] paramArrayOfByte);
/*     */ 
/*     */   public abstract void setPosition(int paramInt);
/*     */ 
/*     */   public void setWasMultiPacket(boolean flag)
/*     */   {
/* 238 */     this.wasMultiPacket = flag;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 242 */     return dumpClampedBytes(getPosition());
/*     */   }
/*     */ 
/*     */   public String toSuperString() {
/* 246 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public boolean wasMultiPacket()
/*     */   {
/* 255 */     return this.wasMultiPacket;
/*     */   }
/*     */ 
/*     */   abstract void writeByte(byte paramByte)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeBytesNoNull(byte[] paramArrayOfByte)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeBytesNoNull(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeDouble(double paramDouble)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeFieldLength(long paramLong)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeFloat(float paramFloat)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeInt(int paramInt)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeLenBytes(byte[] paramArrayOfByte)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeLenString(String paramString1, String paramString2, String paramString3, SingleByteCharsetConverter paramSingleByteCharsetConverter, boolean paramBoolean)
/*     */     throws UnsupportedEncodingException, SQLException;
/*     */ 
/*     */   abstract void writeLong(long paramLong)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeLongInt(int paramInt)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeLongLong(long paramLong)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeString(String paramString)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeStringNoNull(String paramString)
/*     */     throws SQLException;
/*     */ 
/*     */   abstract void writeStringNoNull(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
/*     */     throws UnsupportedEncodingException, SQLException;
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.Buffer
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.filter.codec.serialization;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.StreamCorruptedException;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class ObjectSerializationInputStream extends InputStream
/*     */   implements ObjectInput
/*     */ {
/*     */   private final DataInputStream in;
/*     */   private final ClassLoader classLoader;
/*  46 */   private int maxObjectSize = 1048576;
/*     */ 
/*     */   public ObjectSerializationInputStream(InputStream in) {
/*  49 */     this(in, null);
/*     */   }
/*     */ 
/*     */   public ObjectSerializationInputStream(InputStream in, ClassLoader classLoader)
/*     */   {
/*  54 */     if (in == null) {
/*  55 */       throw new NullPointerException("in");
/*     */     }
/*  57 */     if (classLoader == null) {
/*  58 */       classLoader = Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */ 
/*  61 */     if ((in instanceof DataInputStream))
/*  62 */       this.in = ((DataInputStream)in);
/*     */     else {
/*  64 */       this.in = new DataInputStream(in);
/*     */     }
/*     */ 
/*  67 */     this.classLoader = classLoader;
/*     */   }
/*     */ 
/*     */   public int getMaxObjectSize()
/*     */   {
/*  77 */     return this.maxObjectSize;
/*     */   }
/*     */ 
/*     */   public void setMaxObjectSize(int maxObjectSize)
/*     */   {
/*  87 */     if (maxObjectSize <= 0) {
/*  88 */       throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
/*     */     }
/*     */ 
/*  92 */     this.maxObjectSize = maxObjectSize;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/*  97 */     return this.in.read();
/*     */   }
/*     */ 
/*     */   public Object readObject() throws ClassNotFoundException, IOException {
/* 101 */     int objectSize = this.in.readInt();
/* 102 */     if (objectSize <= 0) {
/* 103 */       throw new StreamCorruptedException("Invalid objectSize: " + objectSize);
/*     */     }
/*     */ 
/* 106 */     if (objectSize > this.maxObjectSize) {
/* 107 */       throw new StreamCorruptedException("ObjectSize too big: " + objectSize + " (expected: <= " + this.maxObjectSize + ')');
/*     */     }
/*     */ 
/* 111 */     IoBuffer buf = IoBuffer.allocate(objectSize + 4, false);
/* 112 */     buf.putInt(objectSize);
/* 113 */     this.in.readFully(buf.array(), 4, objectSize);
/* 114 */     buf.position(0);
/* 115 */     buf.limit(objectSize + 4);
/*     */ 
/* 117 */     return buf.getObject(this.classLoader);
/*     */   }
/*     */ 
/*     */   public boolean readBoolean() throws IOException {
/* 121 */     return this.in.readBoolean();
/*     */   }
/*     */ 
/*     */   public byte readByte() throws IOException {
/* 125 */     return this.in.readByte();
/*     */   }
/*     */ 
/*     */   public char readChar() throws IOException {
/* 129 */     return this.in.readChar();
/*     */   }
/*     */ 
/*     */   public double readDouble() throws IOException {
/* 133 */     return this.in.readDouble();
/*     */   }
/*     */ 
/*     */   public float readFloat() throws IOException {
/* 137 */     return this.in.readFloat();
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] b) throws IOException {
/* 141 */     this.in.readFully(b);
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] b, int off, int len) throws IOException {
/* 145 */     this.in.readFully(b, off, len);
/*     */   }
/*     */ 
/*     */   public int readInt() throws IOException {
/* 149 */     return this.in.readInt();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public String readLine()
/*     */     throws IOException
/*     */   {
/* 158 */     return this.in.readLine();
/*     */   }
/*     */ 
/*     */   public long readLong() throws IOException {
/* 162 */     return this.in.readLong();
/*     */   }
/*     */ 
/*     */   public short readShort() throws IOException {
/* 166 */     return this.in.readShort();
/*     */   }
/*     */ 
/*     */   public String readUTF() throws IOException {
/* 170 */     return this.in.readUTF();
/*     */   }
/*     */ 
/*     */   public int readUnsignedByte() throws IOException {
/* 174 */     return this.in.readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public int readUnsignedShort() throws IOException {
/* 178 */     return this.in.readUnsignedShort();
/*     */   }
/*     */ 
/*     */   public int skipBytes(int n) throws IOException {
/* 182 */     return this.in.skipBytes(n);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.serialization.ObjectSerializationInputStream
 * JD-Core Version:    0.6.0
 */
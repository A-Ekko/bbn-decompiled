/*     */ package org.apache.mina.filter.codec.serialization;
/*     */ 
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectOutput;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class ObjectSerializationOutputStream extends OutputStream
/*     */   implements ObjectOutput
/*     */ {
/*     */   private final DataOutputStream out;
/*  41 */   private int maxObjectSize = 2147483647;
/*     */ 
/*     */   public ObjectSerializationOutputStream(OutputStream out) {
/*  44 */     if (out == null) {
/*  45 */       throw new NullPointerException("out");
/*     */     }
/*     */ 
/*  48 */     if ((out instanceof DataOutputStream))
/*  49 */       this.out = ((DataOutputStream)out);
/*     */     else
/*  51 */       this.out = new DataOutputStream(out);
/*     */   }
/*     */ 
/*     */   public int getMaxObjectSize()
/*     */   {
/*  62 */     return this.maxObjectSize;
/*     */   }
/*     */ 
/*     */   public void setMaxObjectSize(int maxObjectSize)
/*     */   {
/*  72 */     if (maxObjectSize <= 0) {
/*  73 */       throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
/*     */     }
/*     */ 
/*  77 */     this.maxObjectSize = maxObjectSize;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  82 */     this.out.close();
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException
/*     */   {
/*  87 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   public void write(int b) throws IOException
/*     */   {
/*  92 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b) throws IOException
/*     */   {
/*  97 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int off, int len) throws IOException
/*     */   {
/* 102 */     this.out.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public void writeObject(Object obj) throws IOException {
/* 106 */     IoBuffer buf = IoBuffer.allocate(64, false);
/* 107 */     buf.setAutoExpand(true);
/* 108 */     buf.putObject(obj);
/*     */ 
/* 110 */     int objectSize = buf.position() - 4;
/* 111 */     if (objectSize > this.maxObjectSize) {
/* 112 */       throw new IllegalArgumentException("The encoded object is too big: " + objectSize + " (> " + this.maxObjectSize + ')');
/*     */     }
/*     */ 
/* 117 */     this.out.write(buf.array(), 0, buf.position());
/*     */   }
/*     */ 
/*     */   public void writeBoolean(boolean v) throws IOException {
/* 121 */     this.out.writeBoolean(v);
/*     */   }
/*     */ 
/*     */   public void writeByte(int v) throws IOException {
/* 125 */     this.out.writeByte(v);
/*     */   }
/*     */ 
/*     */   public void writeBytes(String s) throws IOException {
/* 129 */     this.out.writeBytes(s);
/*     */   }
/*     */ 
/*     */   public void writeChar(int v) throws IOException {
/* 133 */     this.out.writeChar(v);
/*     */   }
/*     */ 
/*     */   public void writeChars(String s) throws IOException {
/* 137 */     this.out.writeChars(s);
/*     */   }
/*     */ 
/*     */   public void writeDouble(double v) throws IOException {
/* 141 */     this.out.writeDouble(v);
/*     */   }
/*     */ 
/*     */   public void writeFloat(float v) throws IOException {
/* 145 */     this.out.writeFloat(v);
/*     */   }
/*     */ 
/*     */   public void writeInt(int v) throws IOException {
/* 149 */     this.out.writeInt(v);
/*     */   }
/*     */ 
/*     */   public void writeLong(long v) throws IOException {
/* 153 */     this.out.writeLong(v);
/*     */   }
/*     */ 
/*     */   public void writeShort(int v) throws IOException {
/* 157 */     this.out.writeShort(v);
/*     */   }
/*     */ 
/*     */   public void writeUTF(String str) throws IOException {
/* 161 */     this.out.writeUTF(str);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.serialization.ObjectSerializationOutputStream
 * JD-Core Version:    0.6.0
 */
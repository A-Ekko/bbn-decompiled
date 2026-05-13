/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class CompositeByteArrayRelativeReader extends CompositeByteArrayRelativeBase
/*     */   implements IoRelativeReader
/*     */ {
/*     */   private final boolean autoFree;
/*     */ 
/*     */   public CompositeByteArrayRelativeReader(CompositeByteArray cba, boolean autoFree)
/*     */   {
/*  57 */     super(cba);
/*  58 */     this.autoFree = autoFree;
/*     */   }
/*     */ 
/*     */   protected void cursorPassedFirstComponent()
/*     */   {
/*  65 */     if (this.autoFree)
/*     */     {
/*  67 */       this.cba.removeFirst().free();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void skip(int length)
/*     */   {
/*  77 */     this.cursor.skip(length);
/*     */   }
/*     */ 
/*     */   public ByteArray slice(int length)
/*     */   {
/*  86 */     return this.cursor.slice(length);
/*     */   }
/*     */ 
/*     */   public byte get()
/*     */   {
/*  95 */     return this.cursor.get();
/*     */   }
/*     */ 
/*     */   public void get(IoBuffer bb)
/*     */   {
/* 104 */     this.cursor.get(bb);
/*     */   }
/*     */ 
/*     */   public short getShort()
/*     */   {
/* 113 */     return this.cursor.getShort();
/*     */   }
/*     */ 
/*     */   public int getInt()
/*     */   {
/* 122 */     return this.cursor.getInt();
/*     */   }
/*     */ 
/*     */   public long getLong()
/*     */   {
/* 131 */     return this.cursor.getLong();
/*     */   }
/*     */ 
/*     */   public float getFloat()
/*     */   {
/* 140 */     return this.cursor.getFloat();
/*     */   }
/*     */ 
/*     */   public double getDouble()
/*     */   {
/* 149 */     return this.cursor.getDouble();
/*     */   }
/*     */ 
/*     */   public char getChar()
/*     */   {
/* 158 */     return this.cursor.getChar();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.CompositeByteArrayRelativeReader
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ import java.util.Collections;
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public abstract class BufferByteArray extends AbstractByteArray
/*     */ {
/*     */   protected IoBuffer bb;
/*     */ 
/*     */   public BufferByteArray(IoBuffer bb)
/*     */   {
/*  56 */     this.bb = bb;
/*     */   }
/*     */ 
/*     */   public Iterable<IoBuffer> getIoBuffers()
/*     */   {
/*  65 */     return Collections.singletonList(this.bb);
/*     */   }
/*     */ 
/*     */   public IoBuffer getSingleIoBuffer()
/*     */   {
/*  74 */     return this.bb;
/*     */   }
/*     */ 
/*     */   public ByteArray slice(int index, int length)
/*     */   {
/*  85 */     int oldLimit = this.bb.limit();
/*  86 */     this.bb.position(index);
/*  87 */     this.bb.limit(index + length);
/*  88 */     IoBuffer slice = this.bb.slice();
/*  89 */     this.bb.limit(oldLimit);
/*  90 */     return new BufferByteArray(slice)
/*     */     {
/*     */       public void free()
/*     */       {
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public abstract void free();
/*     */ 
/*     */   public ByteArray.Cursor cursor()
/*     */   {
/* 113 */     return new CursorImpl();
/*     */   }
/*     */ 
/*     */   public ByteArray.Cursor cursor(int index)
/*     */   {
/* 122 */     return new CursorImpl(index);
/*     */   }
/*     */ 
/*     */   public int first()
/*     */   {
/* 131 */     return 0;
/*     */   }
/*     */ 
/*     */   public int last()
/*     */   {
/* 140 */     return this.bb.limit();
/*     */   }
/*     */ 
/*     */   public ByteOrder order()
/*     */   {
/* 149 */     return this.bb.order();
/*     */   }
/*     */ 
/*     */   public void order(ByteOrder order)
/*     */   {
/* 158 */     this.bb.order(order);
/*     */   }
/*     */ 
/*     */   public byte get(int index)
/*     */   {
/* 167 */     return this.bb.get(index);
/*     */   }
/*     */ 
/*     */   public void put(int index, byte b)
/*     */   {
/* 176 */     this.bb.put(index, b);
/*     */   }
/*     */ 
/*     */   public void get(int index, IoBuffer other)
/*     */   {
/* 185 */     this.bb.position(index);
/* 186 */     other.put(this.bb);
/*     */   }
/*     */ 
/*     */   public void put(int index, IoBuffer other)
/*     */   {
/* 195 */     this.bb.position(index);
/* 196 */     this.bb.put(other);
/*     */   }
/*     */ 
/*     */   public short getShort(int index)
/*     */   {
/* 205 */     return this.bb.getShort(index);
/*     */   }
/*     */ 
/*     */   public void putShort(int index, short s)
/*     */   {
/* 214 */     this.bb.putShort(index, s);
/*     */   }
/*     */ 
/*     */   public int getInt(int index)
/*     */   {
/* 223 */     return this.bb.getInt(index);
/*     */   }
/*     */ 
/*     */   public void putInt(int index, int i)
/*     */   {
/* 232 */     this.bb.putInt(index, i);
/*     */   }
/*     */ 
/*     */   public long getLong(int index)
/*     */   {
/* 241 */     return this.bb.getLong(index);
/*     */   }
/*     */ 
/*     */   public void putLong(int index, long l)
/*     */   {
/* 250 */     this.bb.putLong(index, l);
/*     */   }
/*     */ 
/*     */   public float getFloat(int index)
/*     */   {
/* 259 */     return this.bb.getFloat(index);
/*     */   }
/*     */ 
/*     */   public void putFloat(int index, float f)
/*     */   {
/* 268 */     this.bb.putFloat(index, f);
/*     */   }
/*     */ 
/*     */   public double getDouble(int index)
/*     */   {
/* 277 */     return this.bb.getDouble(index);
/*     */   }
/*     */ 
/*     */   public void putDouble(int index, double d)
/*     */   {
/* 286 */     this.bb.putDouble(index, d);
/*     */   }
/*     */ 
/*     */   public char getChar(int index)
/*     */   {
/* 295 */     return this.bb.getChar(index);
/*     */   }
/*     */ 
/*     */   public void putChar(int index, char c)
/*     */   {
/* 304 */     this.bb.putChar(index, c);
/*     */   }
/*     */ 
/*     */   private class CursorImpl
/*     */     implements ByteArray.Cursor
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     public CursorImpl()
/*     */     {
/*     */     }
/*     */ 
/*     */     public CursorImpl(int index)
/*     */     {
/* 321 */       setIndex(index);
/*     */     }
/*     */ 
/*     */     public int getRemaining()
/*     */     {
/* 330 */       return BufferByteArray.this.last() - this.index;
/*     */     }
/*     */ 
/*     */     public boolean hasRemaining()
/*     */     {
/* 339 */       return getRemaining() > 0;
/*     */     }
/*     */ 
/*     */     public int getIndex()
/*     */     {
/* 348 */       return this.index;
/*     */     }
/*     */ 
/*     */     public void setIndex(int index)
/*     */     {
/* 357 */       if ((index < 0) || (index > BufferByteArray.this.last()))
/*     */       {
/* 359 */         throw new IndexOutOfBoundsException();
/*     */       }
/* 361 */       this.index = index;
/*     */     }
/*     */ 
/*     */     public void skip(int length)
/*     */     {
/* 367 */       setIndex(this.index + length);
/*     */     }
/*     */ 
/*     */     public ByteArray slice(int length)
/*     */     {
/* 373 */       ByteArray slice = BufferByteArray.this.slice(this.index, length);
/* 374 */       this.index += length;
/* 375 */       return slice;
/*     */     }
/*     */ 
/*     */     public ByteOrder order()
/*     */     {
/* 384 */       return BufferByteArray.this.order();
/*     */     }
/*     */ 
/*     */     public byte get()
/*     */     {
/* 393 */       byte b = BufferByteArray.this.get(this.index);
/* 394 */       this.index += 1;
/* 395 */       return b;
/*     */     }
/*     */ 
/*     */     public void put(byte b)
/*     */     {
/* 404 */       BufferByteArray.this.put(this.index, b);
/* 405 */       this.index += 1;
/*     */     }
/*     */ 
/*     */     public void get(IoBuffer bb)
/*     */     {
/* 414 */       int size = Math.min(getRemaining(), bb.remaining());
/* 415 */       BufferByteArray.this.get(this.index, bb);
/* 416 */       this.index += size;
/*     */     }
/*     */ 
/*     */     public void put(IoBuffer bb)
/*     */     {
/* 425 */       int size = bb.remaining();
/* 426 */       BufferByteArray.this.put(this.index, bb);
/* 427 */       this.index += size;
/*     */     }
/*     */ 
/*     */     public short getShort()
/*     */     {
/* 436 */       short s = BufferByteArray.this.getShort(this.index);
/* 437 */       this.index += 2;
/* 438 */       return s;
/*     */     }
/*     */ 
/*     */     public void putShort(short s)
/*     */     {
/* 447 */       BufferByteArray.this.putShort(this.index, s);
/* 448 */       this.index += 2;
/*     */     }
/*     */ 
/*     */     public int getInt()
/*     */     {
/* 457 */       int i = BufferByteArray.this.getInt(this.index);
/* 458 */       this.index += 4;
/* 459 */       return i;
/*     */     }
/*     */ 
/*     */     public void putInt(int i)
/*     */     {
/* 468 */       BufferByteArray.this.putInt(this.index, i);
/* 469 */       this.index += 4;
/*     */     }
/*     */ 
/*     */     public long getLong()
/*     */     {
/* 478 */       long l = BufferByteArray.this.getLong(this.index);
/* 479 */       this.index += 8;
/* 480 */       return l;
/*     */     }
/*     */ 
/*     */     public void putLong(long l)
/*     */     {
/* 489 */       BufferByteArray.this.putLong(this.index, l);
/* 490 */       this.index += 8;
/*     */     }
/*     */ 
/*     */     public float getFloat()
/*     */     {
/* 499 */       float f = BufferByteArray.this.getFloat(this.index);
/* 500 */       this.index += 4;
/* 501 */       return f;
/*     */     }
/*     */ 
/*     */     public void putFloat(float f)
/*     */     {
/* 510 */       BufferByteArray.this.putFloat(this.index, f);
/* 511 */       this.index += 4;
/*     */     }
/*     */ 
/*     */     public double getDouble()
/*     */     {
/* 520 */       double d = BufferByteArray.this.getDouble(this.index);
/* 521 */       this.index += 8;
/* 522 */       return d;
/*     */     }
/*     */ 
/*     */     public void putDouble(double d)
/*     */     {
/* 531 */       BufferByteArray.this.putDouble(this.index, d);
/* 532 */       this.index += 8;
/*     */     }
/*     */ 
/*     */     public char getChar()
/*     */     {
/* 541 */       char c = BufferByteArray.this.getChar(this.index);
/* 542 */       this.index += 2;
/* 543 */       return c;
/*     */     }
/*     */ 
/*     */     public void putChar(char c)
/*     */     {
/* 552 */       BufferByteArray.this.putChar(this.index, c);
/* 553 */       this.index += 2;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.BufferByteArray
 * JD-Core Version:    0.6.0
 */
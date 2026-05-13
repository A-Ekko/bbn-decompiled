/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import org.apache.mina.core.buffer.IoBuffer;
/*     */ 
/*     */ public class CompositeByteArrayRelativeWriter extends CompositeByteArrayRelativeBase
/*     */   implements IoRelativeWriter
/*     */ {
/*     */   private final Expander expander;
/*     */   private final Flusher flusher;
/*     */   private final boolean autoFlush;
/*     */ 
/*     */   public CompositeByteArrayRelativeWriter(CompositeByteArray cba, Expander expander, Flusher flusher, boolean autoFlush)
/*     */   {
/* 141 */     super(cba);
/* 142 */     this.expander = expander;
/* 143 */     this.flusher = flusher;
/* 144 */     this.autoFlush = autoFlush;
/*     */   }
/*     */ 
/*     */   private void prepareForAccess(int size)
/*     */   {
/* 150 */     int underflow = this.cursor.getIndex() + size - last();
/* 151 */     if (underflow > 0)
/*     */     {
/* 153 */       this.expander.expand(this.cba, underflow);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/* 163 */     flushTo(this.cursor.getIndex());
/*     */   }
/*     */ 
/*     */   public void flushTo(int index)
/*     */   {
/* 172 */     ByteArray removed = this.cba.removeTo(index);
/* 173 */     this.flusher.flush(removed);
/*     */   }
/*     */ 
/*     */   public void skip(int length)
/*     */   {
/* 182 */     this.cursor.skip(length);
/*     */   }
/*     */ 
/*     */   protected void cursorPassedFirstComponent()
/*     */   {
/* 189 */     if (this.autoFlush)
/*     */     {
/* 191 */       flushTo(this.cba.first() + this.cba.getFirst().length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void put(byte b)
/*     */   {
/* 201 */     prepareForAccess(1);
/* 202 */     this.cursor.put(b);
/*     */   }
/*     */ 
/*     */   public void put(IoBuffer bb)
/*     */   {
/* 211 */     prepareForAccess(bb.remaining());
/* 212 */     this.cursor.put(bb);
/*     */   }
/*     */ 
/*     */   public void putShort(short s)
/*     */   {
/* 221 */     prepareForAccess(2);
/* 222 */     this.cursor.putShort(s);
/*     */   }
/*     */ 
/*     */   public void putInt(int i)
/*     */   {
/* 231 */     prepareForAccess(4);
/* 232 */     this.cursor.putInt(i);
/*     */   }
/*     */ 
/*     */   public void putLong(long l)
/*     */   {
/* 241 */     prepareForAccess(8);
/* 242 */     this.cursor.putLong(l);
/*     */   }
/*     */ 
/*     */   public void putFloat(float f)
/*     */   {
/* 251 */     prepareForAccess(4);
/* 252 */     this.cursor.putFloat(f);
/*     */   }
/*     */ 
/*     */   public void putDouble(double d)
/*     */   {
/* 261 */     prepareForAccess(8);
/* 262 */     this.cursor.putDouble(d);
/*     */   }
/*     */ 
/*     */   public void putChar(char c)
/*     */   {
/* 271 */     prepareForAccess(2);
/* 272 */     this.cursor.putChar(c);
/*     */   }
/*     */ 
/*     */   public static abstract interface Flusher
/*     */   {
/*     */     public abstract void flush(ByteArray paramByteArray);
/*     */   }
/*     */ 
/*     */   public static class ChunkedExpander
/*     */     implements CompositeByteArrayRelativeWriter.Expander
/*     */   {
/*     */     private final ByteArrayFactory baf;
/*     */     private final int newComponentSize;
/*     */ 
/*     */     public ChunkedExpander(ByteArrayFactory baf, int newComponentSize)
/*     */     {
/*  81 */       this.baf = baf;
/*  82 */       this.newComponentSize = newComponentSize;
/*     */     }
/*     */ 
/*     */     public void expand(CompositeByteArray cba, int minSize)
/*     */     {
/*  88 */       int remaining = minSize;
/*  89 */       while (remaining > 0)
/*     */       {
/*  91 */         ByteArray component = this.baf.create(this.newComponentSize);
/*  92 */         cba.addLast(component);
/*  93 */         remaining -= this.newComponentSize;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class NopExpander
/*     */     implements CompositeByteArrayRelativeWriter.Expander
/*     */   {
/*     */     public void expand(CompositeByteArray cba, int minSize)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface Expander
/*     */   {
/*     */     public abstract void expand(CompositeByteArray paramCompositeByteArray, int paramInt);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.CompositeByteArrayRelativeWriter
 * JD-Core Version:    0.6.0
 */
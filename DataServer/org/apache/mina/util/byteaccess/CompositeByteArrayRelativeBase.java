/*     */ package org.apache.mina.util.byteaccess;
/*     */ 
/*     */ import java.nio.ByteOrder;
/*     */ 
/*     */ abstract class CompositeByteArrayRelativeBase
/*     */ {
/*     */   protected final CompositeByteArray cba;
/*     */   protected final ByteArray.Cursor cursor;
/*     */ 
/*     */   public CompositeByteArrayRelativeBase(CompositeByteArray cba)
/*     */   {
/*  61 */     this.cba = cba;
/*  62 */     this.cursor = cba.cursor(cba.first(), new CompositeByteArray.CursorListener()
/*     */     {
/*     */       public void enteredFirstComponent(int componentIndex, ByteArray component)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void enteredLastComponent(int componentIndex, ByteArray component)
/*     */       {
/*  73 */         if (!$assertionsDisabled) throw new AssertionError();
/*     */       }
/*     */ 
/*     */       public void enteredNextComponent(int componentIndex, ByteArray component)
/*     */       {
/*  79 */         CompositeByteArrayRelativeBase.this.cursorPassedFirstComponent();
/*     */       }
/*     */ 
/*     */       public void enteredPreviousComponent(int componentIndex, ByteArray component)
/*     */       {
/*  85 */         if (!$assertionsDisabled) throw new AssertionError();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public final int getRemaining()
/*     */   {
/*  97 */     return this.cursor.getRemaining();
/*     */   }
/*     */ 
/*     */   public final boolean hasRemaining()
/*     */   {
/* 106 */     return this.cursor.hasRemaining();
/*     */   }
/*     */ 
/*     */   public ByteOrder order()
/*     */   {
/* 115 */     return this.cba.order();
/*     */   }
/*     */ 
/*     */   public final void append(ByteArray ba)
/*     */   {
/* 124 */     this.cba.addLast(ba);
/*     */   }
/*     */ 
/*     */   public final void free()
/*     */   {
/* 133 */     this.cba.free();
/*     */   }
/*     */ 
/*     */   public final int getIndex()
/*     */   {
/* 142 */     return this.cursor.getIndex();
/*     */   }
/*     */ 
/*     */   public final int last()
/*     */   {
/* 151 */     return this.cba.last();
/*     */   }
/*     */ 
/*     */   protected abstract void cursorPassedFirstComponent();
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.byteaccess.CompositeByteArrayRelativeBase
 * JD-Core Version:    0.6.0
 */
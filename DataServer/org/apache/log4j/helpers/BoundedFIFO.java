/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class BoundedFIFO
/*     */ {
/*     */   LoggingEvent[] buf;
/*  25 */   int numElements = 0;
/*  26 */   int first = 0;
/*  27 */   int next = 0;
/*     */   int maxSize;
/*     */ 
/*     */   public BoundedFIFO(int maxSize)
/*     */   {
/*  35 */     if (maxSize < 1) {
/*  36 */       throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
/*     */     }
/*     */ 
/*  39 */     this.maxSize = maxSize;
/*  40 */     this.buf = new LoggingEvent[maxSize];
/*     */   }
/*     */ 
/*     */   public LoggingEvent get()
/*     */   {
/*  48 */     if (this.numElements == 0) {
/*  49 */       return null;
/*     */     }
/*  51 */     LoggingEvent r = this.buf[this.first];
/*  52 */     this.buf[this.first] = null;
/*     */ 
/*  54 */     if (++this.first == this.maxSize) {
/*  55 */       this.first = 0;
/*     */     }
/*  57 */     this.numElements -= 1;
/*  58 */     return r;
/*     */   }
/*     */ 
/*     */   public void put(LoggingEvent o)
/*     */   {
/*  67 */     if (this.numElements != this.maxSize) {
/*  68 */       this.buf[this.next] = o;
/*  69 */       if (++this.next == this.maxSize) {
/*  70 */         this.next = 0;
/*     */       }
/*  72 */       this.numElements += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  81 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/*  89 */     return this.numElements == this.maxSize;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/*  99 */     return this.numElements;
/*     */   }
/*     */ 
/*     */   int min(int a, int b)
/*     */   {
/* 104 */     return a < b ? a : b;
/*     */   }
/*     */ 
/*     */   public synchronized void resize(int newSize)
/*     */   {
/* 117 */     if (newSize == this.maxSize) {
/* 118 */       return;
/*     */     }
/*     */ 
/* 121 */     LoggingEvent[] tmp = new LoggingEvent[newSize];
/*     */ 
/* 124 */     int len1 = this.maxSize - this.first;
/*     */ 
/* 127 */     len1 = min(len1, newSize);
/*     */ 
/* 131 */     len1 = min(len1, this.numElements);
/*     */ 
/* 134 */     System.arraycopy(this.buf, this.first, tmp, 0, len1);
/*     */ 
/* 137 */     int len2 = 0;
/* 138 */     if ((len1 < this.numElements) && (len1 < newSize)) {
/* 139 */       len2 = this.numElements - len1;
/* 140 */       len2 = min(len2, newSize - len1);
/* 141 */       System.arraycopy(this.buf, 0, tmp, len1, len2);
/*     */     }
/*     */ 
/* 144 */     this.buf = tmp;
/* 145 */     this.maxSize = newSize;
/* 146 */     this.first = 0;
/* 147 */     this.numElements = (len1 + len2);
/* 148 */     this.next = this.numElements;
/* 149 */     if (this.next == this.maxSize)
/* 150 */       this.next = 0;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty()
/*     */   {
/* 160 */     return this.numElements == 1;
/*     */   }
/*     */ 
/*     */   public boolean wasFull()
/*     */   {
/* 169 */     return this.numElements + 1 == this.maxSize;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.BoundedFIFO
 * JD-Core Version:    0.6.0
 */
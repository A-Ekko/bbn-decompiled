/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class CyclicBuffer
/*     */ {
/*     */   LoggingEvent[] ea;
/*     */   int first;
/*     */   int last;
/*     */   int numElems;
/*     */   int maxSize;
/*     */ 
/*     */   public CyclicBuffer(int maxSize)
/*     */     throws IllegalArgumentException
/*     */   {
/*  41 */     if (maxSize < 1) {
/*  42 */       throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
/*     */     }
/*     */ 
/*  45 */     this.maxSize = maxSize;
/*  46 */     this.ea = new LoggingEvent[maxSize];
/*  47 */     this.first = 0;
/*  48 */     this.last = 0;
/*  49 */     this.numElems = 0;
/*     */   }
/*     */ 
/*     */   public void add(LoggingEvent event)
/*     */   {
/*  58 */     this.ea[this.last] = event;
/*  59 */     if (++this.last == this.maxSize) {
/*  60 */       this.last = 0;
/*     */     }
/*  62 */     if (this.numElems < this.maxSize)
/*  63 */       this.numElems += 1;
/*  64 */     else if (++this.first == this.maxSize)
/*  65 */       this.first = 0;
/*     */   }
/*     */ 
/*     */   public LoggingEvent get(int i)
/*     */   {
/*  78 */     if ((i < 0) || (i >= this.numElems)) {
/*  79 */       return null;
/*     */     }
/*  81 */     return this.ea[((this.first + i) % this.maxSize)];
/*     */   }
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  86 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public LoggingEvent get()
/*     */   {
/*  95 */     LoggingEvent r = null;
/*  96 */     if (this.numElems > 0) {
/*  97 */       this.numElems -= 1;
/*  98 */       r = this.ea[this.first];
/*  99 */       this.ea[this.first] = null;
/* 100 */       if (++this.first == this.maxSize)
/* 101 */         this.first = 0;
/*     */     }
/* 103 */     return r;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 113 */     return this.numElems;
/*     */   }
/*     */ 
/*     */   public void resize(int newSize)
/*     */   {
/* 123 */     if (newSize < 0) {
/* 124 */       throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
/*     */     }
/*     */ 
/* 127 */     if (newSize == this.numElems) {
/* 128 */       return;
/*     */     }
/* 130 */     LoggingEvent[] temp = new LoggingEvent[newSize];
/*     */ 
/* 132 */     int loopLen = newSize < this.numElems ? newSize : this.numElems;
/*     */ 
/* 134 */     for (int i = 0; i < loopLen; i++) {
/* 135 */       temp[i] = this.ea[this.first];
/* 136 */       this.ea[this.first] = null;
/* 137 */       if (++this.first == this.numElems)
/* 138 */         this.first = 0;
/*     */     }
/* 140 */     this.ea = temp;
/* 141 */     this.first = 0;
/* 142 */     this.numElems = loopLen;
/* 143 */     this.maxSize = newSize;
/* 144 */     if (loopLen == newSize)
/* 145 */       this.last = 0;
/*     */     else
/* 147 */       this.last = loopLen;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.CyclicBuffer
 * JD-Core Version:    0.6.0
 */
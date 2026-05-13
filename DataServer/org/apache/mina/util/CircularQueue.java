/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ public class CircularQueue<E> extends AbstractList<E>
/*     */   implements List<E>, Queue<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3993421269224511264L;
/*     */   private static final int DEFAULT_CAPACITY = 4;
/*     */   private final int initialCapacity;
/*     */   private volatile Object[] items;
/*     */   private int mask;
/*  50 */   private int first = 0;
/*  51 */   private int last = 0;
/*     */   private boolean full;
/*     */   private int shrinkThreshold;
/*     */ 
/*     */   public CircularQueue()
/*     */   {
/*  59 */     this(4);
/*     */   }
/*     */ 
/*     */   public CircularQueue(int initialCapacity) {
/*  63 */     int actualCapacity = normalizeCapacity(initialCapacity);
/*  64 */     this.items = new Object[actualCapacity];
/*  65 */     this.mask = (actualCapacity - 1);
/*  66 */     this.initialCapacity = actualCapacity;
/*  67 */     this.shrinkThreshold = 0;
/*     */   }
/*     */ 
/*     */   private static int normalizeCapacity(int initialCapacity)
/*     */   {
/*  74 */     int actualCapacity = 1;
/*     */ 
/*  76 */     while (actualCapacity < initialCapacity) {
/*  77 */       actualCapacity <<= 1;
/*  78 */       if (actualCapacity < 0) {
/*  79 */         actualCapacity = 1073741824;
/*     */       }
/*     */     }
/*     */ 
/*  83 */     return actualCapacity;
/*     */   }
/*     */ 
/*     */   public int capacity()
/*     */   {
/*  90 */     return this.items.length;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  95 */     if (!isEmpty()) {
/*  96 */       Arrays.fill(this.items, null);
/*  97 */       this.first = 0;
/*  98 */       this.last = 0;
/*  99 */       this.full = false;
/* 100 */       shrinkIfNeeded();
/*     */     }
/*     */   }
/*     */ 
/*     */   public E poll()
/*     */   {
/* 106 */     if (isEmpty()) {
/* 107 */       return null;
/*     */     }
/*     */ 
/* 110 */     Object ret = this.items[this.first];
/* 111 */     this.items[this.first] = null;
/* 112 */     decreaseSize();
/*     */ 
/* 114 */     if (this.first == this.last) {
/* 115 */       this.first = (this.last = 0);
/*     */     }
/*     */ 
/* 118 */     shrinkIfNeeded();
/* 119 */     return ret;
/*     */   }
/*     */ 
/*     */   public boolean offer(E item) {
/* 123 */     if (item == null) {
/* 124 */       throw new NullPointerException("item");
/*     */     }
/*     */ 
/* 127 */     expandIfNeeded();
/* 128 */     this.items[this.last] = item;
/* 129 */     increaseSize();
/* 130 */     return true;
/*     */   }
/*     */ 
/*     */   public E peek()
/*     */   {
/* 135 */     if (isEmpty()) {
/* 136 */       return null;
/*     */     }
/*     */ 
/* 139 */     return this.items[this.first];
/*     */   }
/*     */ 
/*     */   public E get(int idx)
/*     */   {
/* 145 */     checkIndex(idx);
/* 146 */     return this.items[getRealIndex(idx)];
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 151 */     return (this.first == this.last) && (!this.full);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 156 */     if (this.full) {
/* 157 */       return capacity();
/*     */     }
/*     */ 
/* 160 */     if (this.last >= this.first) {
/* 161 */       return this.last - this.first;
/*     */     }
/* 163 */     return this.last - this.first + capacity();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 169 */     return "first=" + this.first + ", last=" + this.last + ", size=" + size() + ", mask = " + this.mask;
/*     */   }
/*     */ 
/*     */   private void checkIndex(int idx)
/*     */   {
/* 174 */     if ((idx < 0) || (idx >= size()))
/* 175 */       throw new IndexOutOfBoundsException(String.valueOf(idx));
/*     */   }
/*     */ 
/*     */   private int getRealIndex(int idx)
/*     */   {
/* 180 */     return this.first + idx & this.mask;
/*     */   }
/*     */ 
/*     */   private void increaseSize() {
/* 184 */     this.last = (this.last + 1 & this.mask);
/* 185 */     this.full = (this.first == this.last);
/*     */   }
/*     */ 
/*     */   private void decreaseSize() {
/* 189 */     this.first = (this.first + 1 & this.mask);
/* 190 */     this.full = false;
/*     */   }
/*     */ 
/*     */   private void expandIfNeeded() {
/* 194 */     if (this.full)
/*     */     {
/* 196 */       int oldLen = this.items.length;
/* 197 */       int newLen = oldLen << 1;
/* 198 */       Object[] tmp = new Object[newLen];
/*     */ 
/* 200 */       if (this.first < this.last) {
/* 201 */         System.arraycopy(this.items, this.first, tmp, 0, this.last - this.first);
/*     */       } else {
/* 203 */         System.arraycopy(this.items, this.first, tmp, 0, oldLen - this.first);
/* 204 */         System.arraycopy(this.items, 0, tmp, oldLen - this.first, this.last);
/*     */       }
/*     */ 
/* 207 */       this.first = 0;
/* 208 */       this.last = oldLen;
/* 209 */       this.items = tmp;
/* 210 */       this.mask = (tmp.length - 1);
/* 211 */       if (newLen >>> 3 > this.initialCapacity)
/* 212 */         this.shrinkThreshold = (newLen >>> 3);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void shrinkIfNeeded()
/*     */   {
/* 218 */     int size = size();
/* 219 */     if (size <= this.shrinkThreshold)
/*     */     {
/* 221 */       int oldLen = this.items.length;
/* 222 */       int newLen = normalizeCapacity(size);
/* 223 */       if (size == newLen) {
/* 224 */         newLen <<= 1;
/*     */       }
/*     */ 
/* 227 */       if (newLen >= oldLen) {
/* 228 */         return;
/*     */       }
/*     */ 
/* 231 */       if (newLen < this.initialCapacity) {
/* 232 */         if (oldLen == this.initialCapacity) {
/* 233 */           return;
/*     */         }
/* 235 */         newLen = this.initialCapacity;
/*     */       }
/*     */ 
/* 239 */       Object[] tmp = new Object[newLen];
/*     */ 
/* 242 */       if (size > 0) {
/* 243 */         if (this.first < this.last) {
/* 244 */           System.arraycopy(this.items, this.first, tmp, 0, this.last - this.first);
/*     */         } else {
/* 246 */           System.arraycopy(this.items, this.first, tmp, 0, oldLen - this.first);
/* 247 */           System.arraycopy(this.items, 0, tmp, oldLen - this.first, this.last);
/*     */         }
/*     */       }
/*     */ 
/* 251 */       this.first = 0;
/* 252 */       this.last = size;
/* 253 */       this.items = tmp;
/* 254 */       this.mask = (tmp.length - 1);
/* 255 */       this.shrinkThreshold = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean add(E o)
/*     */   {
/* 261 */     return offer(o);
/*     */   }
/*     */ 
/*     */   public E set(int idx, E o)
/*     */   {
/* 267 */     checkIndex(idx);
/*     */ 
/* 269 */     int realIdx = getRealIndex(idx);
/* 270 */     Object old = this.items[realIdx];
/* 271 */     this.items[realIdx] = o;
/* 272 */     return old;
/*     */   }
/*     */ 
/*     */   public void add(int idx, E o)
/*     */   {
/* 277 */     if (idx == size()) {
/* 278 */       offer(o);
/* 279 */       return;
/*     */     }
/*     */ 
/* 282 */     checkIndex(idx);
/* 283 */     expandIfNeeded();
/*     */ 
/* 285 */     int realIdx = getRealIndex(idx);
/*     */ 
/* 288 */     if (this.first < this.last) {
/* 289 */       System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.last - realIdx);
/*     */     }
/* 293 */     else if (realIdx >= this.first) {
/* 294 */       System.arraycopy(this.items, 0, this.items, 1, this.last);
/* 295 */       this.items[0] = this.items[(this.items.length - 1)];
/* 296 */       System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.items.length - realIdx - 1);
/*     */     }
/*     */     else {
/* 299 */       System.arraycopy(this.items, realIdx, this.items, realIdx + 1, this.last - realIdx);
/*     */     }
/*     */ 
/* 304 */     this.items[realIdx] = o;
/* 305 */     increaseSize();
/*     */   }
/*     */ 
/*     */   public E remove(int idx)
/*     */   {
/* 311 */     if (idx == 0) {
/* 312 */       return poll();
/*     */     }
/*     */ 
/* 315 */     checkIndex(idx);
/*     */ 
/* 317 */     int realIdx = getRealIndex(idx);
/* 318 */     Object removed = this.items[realIdx];
/*     */ 
/* 321 */     if (this.first < this.last) {
/* 322 */       System.arraycopy(this.items, this.first, this.items, this.first + 1, realIdx - this.first);
/*     */     }
/* 324 */     else if (realIdx >= this.first) {
/* 325 */       System.arraycopy(this.items, this.first, this.items, this.first + 1, realIdx - this.first);
/*     */     }
/*     */     else {
/* 328 */       System.arraycopy(this.items, 0, this.items, 1, realIdx);
/* 329 */       this.items[0] = this.items[(this.items.length - 1)];
/* 330 */       System.arraycopy(this.items, this.first, this.items, this.first + 1, this.items.length - this.first - 1);
/*     */     }
/*     */ 
/* 335 */     this.items[this.first] = null;
/* 336 */     decreaseSize();
/*     */ 
/* 338 */     shrinkIfNeeded();
/* 339 */     return removed;
/*     */   }
/*     */ 
/*     */   public E remove() {
/* 343 */     if (isEmpty()) {
/* 344 */       throw new NoSuchElementException();
/*     */     }
/* 346 */     return poll();
/*     */   }
/*     */ 
/*     */   public E element() {
/* 350 */     if (isEmpty()) {
/* 351 */       throw new NoSuchElementException();
/*     */     }
/* 353 */     return peek();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.CircularQueue
 * JD-Core Version:    0.6.0
 */
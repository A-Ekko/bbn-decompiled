/*     */ package org.logicalcobwebs.concurrent;
/*     */ 
/*     */ public class SynchronizedInt extends SynchronizedVariable
/*     */   implements Comparable, Cloneable
/*     */ {
/*     */   protected int value_;
/*     */ 
/*     */   public SynchronizedInt(int initialValue)
/*     */   {
/*  32 */     this.value_ = initialValue;
/*     */   }
/*     */ 
/*     */   public SynchronizedInt(int initialValue, Object lock)
/*     */   {
/*  40 */     super(lock);
/*  41 */     this.value_ = initialValue;
/*     */   }
/*     */ 
/*     */   public final int get()
/*     */   {
/*  48 */     synchronized (this.lock_) {
/*  49 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int set(int newValue)
/*     */   {
/*  59 */     synchronized (this.lock_) {
/*  60 */       int old = this.value_;
/*  61 */       this.value_ = newValue;
/*  62 */       return old;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean commit(int assumedValue, int newValue)
/*     */   {
/*  71 */     synchronized (this.lock_) {
/*  72 */       boolean success = assumedValue == this.value_;
/*  73 */       if (success) this.value_ = newValue;
/*  74 */       return success;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int swap(SynchronizedInt other)
/*     */   {
/*  89 */     if (other == this) return get();
/*  90 */     SynchronizedInt fst = this;
/*  91 */     SynchronizedInt snd = other;
/*  92 */     if (System.identityHashCode(fst) > System.identityHashCode(snd)) {
/*  93 */       fst = other;
/*  94 */       snd = this;
/*     */     }
/*  96 */     synchronized (fst.lock_) {
/*  97 */       synchronized (snd.lock_) {
/*  98 */         fst.set(snd.set(fst.get()));
/*  99 */         return get();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int increment()
/*     */   {
/* 109 */     synchronized (this.lock_) {
/* 110 */       return ++this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int decrement()
/*     */   {
/* 119 */     synchronized (this.lock_) {
/* 120 */       return --this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int add(int amount)
/*     */   {
/* 129 */     synchronized (this.lock_) {
/* 130 */       return this.value_ += amount;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int subtract(int amount)
/*     */   {
/* 139 */     synchronized (this.lock_) {
/* 140 */       return this.value_ -= amount;
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized int multiply(int factor)
/*     */   {
/* 149 */     synchronized (this.lock_) {
/* 150 */       return this.value_ *= factor;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int divide(int factor)
/*     */   {
/* 159 */     synchronized (this.lock_) {
/* 160 */       return this.value_ /= factor;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int negate()
/*     */   {
/* 169 */     synchronized (this.lock_) {
/* 170 */       this.value_ = (-this.value_);
/* 171 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int complement()
/*     */   {
/* 180 */     synchronized (this.lock_) {
/* 181 */       this.value_ ^= -1;
/* 182 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int and(int b)
/*     */   {
/* 191 */     synchronized (this.lock_) {
/* 192 */       this.value_ &= b;
/* 193 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int or(int b)
/*     */   {
/* 202 */     synchronized (this.lock_) {
/* 203 */       this.value_ |= b;
/* 204 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int xor(int b)
/*     */   {
/* 214 */     synchronized (this.lock_) {
/* 215 */       this.value_ ^= b;
/* 216 */       return this.value_;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int compareTo(int other) {
/* 221 */     int val = get();
/* 222 */     return val == other ? 0 : val < other ? -1 : 1;
/*     */   }
/*     */ 
/*     */   public int compareTo(SynchronizedInt other) {
/* 226 */     return compareTo(other.get());
/*     */   }
/*     */ 
/*     */   public int compareTo(Object other) {
/* 230 */     return compareTo((SynchronizedInt)other);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object other) {
/* 234 */     if ((other != null) && ((other instanceof SynchronizedInt)))
/*     */     {
/* 236 */       return get() == ((SynchronizedInt)other).get();
/*     */     }
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 242 */     return get();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 246 */     return String.valueOf(get());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.SynchronizedInt
 * JD-Core Version:    0.6.0
 */
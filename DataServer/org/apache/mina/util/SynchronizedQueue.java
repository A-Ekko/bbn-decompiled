/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Queue;
/*     */ 
/*     */ public class SynchronizedQueue<E>
/*     */   implements Queue<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -1439242290701194806L;
/*     */   private final Queue<E> q;
/*     */ 
/*     */   public SynchronizedQueue(Queue<E> q)
/*     */   {
/*  41 */     this.q = q;
/*     */   }
/*     */ 
/*     */   public synchronized boolean add(E e) {
/*  45 */     return this.q.add(e);
/*     */   }
/*     */ 
/*     */   public synchronized E element() {
/*  49 */     return this.q.element();
/*     */   }
/*     */ 
/*     */   public synchronized boolean offer(E e) {
/*  53 */     return this.q.offer(e);
/*     */   }
/*     */ 
/*     */   public synchronized E peek() {
/*  57 */     return this.q.peek();
/*     */   }
/*     */ 
/*     */   public synchronized E poll() {
/*  61 */     return this.q.poll();
/*     */   }
/*     */ 
/*     */   public synchronized E remove() {
/*  65 */     return this.q.remove();
/*     */   }
/*     */ 
/*     */   public synchronized boolean addAll(Collection<? extends E> c) {
/*  69 */     return this.q.addAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized void clear() {
/*  73 */     this.q.clear();
/*     */   }
/*     */ 
/*     */   public synchronized boolean contains(Object o) {
/*  77 */     return this.q.contains(o);
/*     */   }
/*     */ 
/*     */   public synchronized boolean containsAll(Collection<?> c) {
/*  81 */     return this.q.containsAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized boolean isEmpty() {
/*  85 */     return this.q.isEmpty();
/*     */   }
/*     */ 
/*     */   public synchronized Iterator<E> iterator() {
/*  89 */     return this.q.iterator();
/*     */   }
/*     */ 
/*     */   public synchronized boolean remove(Object o) {
/*  93 */     return this.q.remove(o);
/*     */   }
/*     */ 
/*     */   public synchronized boolean removeAll(Collection<?> c) {
/*  97 */     return this.q.removeAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized boolean retainAll(Collection<?> c) {
/* 101 */     return this.q.retainAll(c);
/*     */   }
/*     */ 
/*     */   public synchronized int size() {
/* 105 */     return this.q.size();
/*     */   }
/*     */ 
/*     */   public synchronized Object[] toArray() {
/* 109 */     return this.q.toArray();
/*     */   }
/*     */ 
/*     */   public synchronized <T> T[] toArray(T[] a) {
/* 113 */     return this.q.toArray(a);
/*     */   }
/*     */ 
/*     */   public synchronized boolean equals(Object obj)
/*     */   {
/* 118 */     return this.q.equals(obj);
/*     */   }
/*     */ 
/*     */   public synchronized int hashCode()
/*     */   {
/* 123 */     return this.q.hashCode();
/*     */   }
/*     */ 
/*     */   public synchronized String toString()
/*     */   {
/* 128 */     return this.q.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.SynchronizedQueue
 * JD-Core Version:    0.6.0
 */
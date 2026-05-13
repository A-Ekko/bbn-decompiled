/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CopyOnWriteMap<K, V>
/*     */   implements Map<K, V>, Cloneable
/*     */ {
/*     */   private volatile Map<K, V> internalMap;
/*     */ 
/*     */   public CopyOnWriteMap()
/*     */   {
/*  47 */     this.internalMap = new HashMap();
/*     */   }
/*     */ 
/*     */   public CopyOnWriteMap(int initialCapacity)
/*     */   {
/*  57 */     this.internalMap = new HashMap(initialCapacity);
/*     */   }
/*     */ 
/*     */   public CopyOnWriteMap(Map<K, V> data)
/*     */   {
/*  70 */     this.internalMap = new HashMap(data);
/*     */   }
/*     */ 
/*     */   public V put(K key, V value)
/*     */   {
/*  79 */     synchronized (this) {
/*  80 */       Map newMap = new HashMap(this.internalMap);
/*  81 */       Object val = newMap.put(key, value);
/*  82 */       this.internalMap = newMap;
/*  83 */       return val;
/*     */     }
/*     */   }
/*     */ 
/*     */   public V remove(Object key)
/*     */   {
/*  94 */     synchronized (this) {
/*  95 */       Map newMap = new HashMap(this.internalMap);
/*  96 */       Object val = newMap.remove(key);
/*  97 */       this.internalMap = newMap;
/*  98 */       return val;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> newData)
/*     */   {
/* 109 */     synchronized (this) {
/* 110 */       Map newMap = new HashMap(this.internalMap);
/* 111 */       newMap.putAll(newData);
/* 112 */       this.internalMap = newMap;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 122 */     synchronized (this) {
/* 123 */       this.internalMap = new HashMap();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 137 */     return this.internalMap.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 146 */     return this.internalMap.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 156 */     return this.internalMap.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/* 166 */     return this.internalMap.containsValue(value);
/*     */   }
/*     */ 
/*     */   public V get(Object key)
/*     */   {
/* 176 */     return this.internalMap.get(key);
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 183 */     return this.internalMap.keySet();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 190 */     return this.internalMap.values();
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 197 */     return this.internalMap.entrySet();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try {
/* 203 */       return super.clone(); } catch (CloneNotSupportedException e) {
/*     */     }
/* 205 */     throw new InternalError();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.util.CopyOnWriteMap
 * JD-Core Version:    0.6.0
 */
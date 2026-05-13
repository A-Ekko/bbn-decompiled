/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ public class LazyInitializedCacheMap<K, V>
/*     */   implements Map<K, V>
/*     */ {
/*     */   private ConcurrentMap<K, LazyInitializer<V>> cache;
/*     */ 
/*     */   public LazyInitializedCacheMap()
/*     */   {
/*  68 */     this.cache = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public LazyInitializedCacheMap(ConcurrentHashMap<K, LazyInitializer<V>> map)
/*     */   {
/*  76 */     this.cache = map;
/*     */   }
/*     */ 
/*     */   public V get(Object key)
/*     */   {
/*  83 */     LazyInitializer c = (LazyInitializer)this.cache.get(key);
/*  84 */     if (c != null) {
/*  85 */       return c.get();
/*     */     }
/*     */ 
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   public V remove(Object key)
/*     */   {
/*  95 */     LazyInitializer c = (LazyInitializer)this.cache.remove(key);
/*  96 */     if (c != null) {
/*  97 */       return c.get();
/*     */     }
/*     */ 
/* 100 */     return null;
/*     */   }
/*     */ 
/*     */   public V putIfAbsent(K key, LazyInitializer<V> value)
/*     */   {
/* 122 */     LazyInitializer v = (LazyInitializer)this.cache.get(key);
/* 123 */     if (v == null) {
/* 124 */       v = (LazyInitializer)this.cache.putIfAbsent(key, value);
/* 125 */       if (v == null) {
/* 126 */         return value.get();
/*     */       }
/*     */     }
/*     */ 
/* 130 */     return v.get();
/*     */   }
/*     */ 
/*     */   public V put(K key, V value)
/*     */   {
/* 137 */     LazyInitializer c = (LazyInitializer)this.cache.put(key, new NoopInitializer(value));
/* 138 */     if (c != null) {
/* 139 */       return c.get();
/*     */     }
/*     */ 
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value)
/*     */   {
/* 150 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 158 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet()
/*     */   {
/* 166 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/* 173 */     for (Map.Entry e : m.entrySet())
/* 174 */       this.cache.put(e.getKey(), new NoopInitializer(e.getValue()));
/*     */   }
/*     */ 
/*     */   public Collection<LazyInitializer<V>> getValues()
/*     */   {
/* 182 */     return this.cache.values();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 189 */     this.cache.clear();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 196 */     return this.cache.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 203 */     return this.cache.isEmpty();
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/* 210 */     return this.cache.keySet();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 217 */     return this.cache.size();
/*     */   }
/*     */ 
/*     */   public class NoopInitializer extends LazyInitializer<V>
/*     */   {
/*     */     private V value;
/*     */ 
/*     */     public NoopInitializer()
/*     */     {
/*  55 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public V init() {
/*  59 */       return this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.LazyInitializedCacheMap
 * JD-Core Version:    0.6.0
 */
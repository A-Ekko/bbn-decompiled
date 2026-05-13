/*     */ package org.apache.mina.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReadWriteLock;
/*     */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*     */ 
/*     */ public class ExpiringMap<K, V>
/*     */   implements Map<K, V>
/*     */ {
/*     */   public static final int DEFAULT_TIME_TO_LIVE = 60;
/*     */   public static final int DEFAULT_EXPIRATION_INTERVAL = 1;
/*  50 */   private static volatile int expirerCount = 1;
/*     */   private final ConcurrentHashMap<K, ExpiringMap<K, V>.ExpiringObject> delegate;
/*     */   private final CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners;
/*     */   private final ExpiringMap<K, V>.Expirer expirer;
/*     */ 
/*     */   public ExpiringMap()
/*     */   {
/*  64 */     this(60, 1);
/*     */   }
/*     */ 
/*     */   public ExpiringMap(int timeToLive)
/*     */   {
/*  75 */     this(timeToLive, 1);
/*     */   }
/*     */ 
/*     */   public ExpiringMap(int timeToLive, int expirationInterval)
/*     */   {
/*  88 */     this(new ConcurrentHashMap(), new CopyOnWriteArrayList(), timeToLive, expirationInterval);
/*     */   }
/*     */ 
/*     */   private ExpiringMap(ConcurrentHashMap<K, ExpiringMap<K, V>.ExpiringObject> delegate, CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners, int timeToLive, int expirationInterval)
/*     */   {
/*  96 */     this.delegate = delegate;
/*  97 */     this.expirationListeners = expirationListeners;
/*     */ 
/*  99 */     this.expirer = new Expirer();
/* 100 */     this.expirer.setTimeToLive(timeToLive);
/* 101 */     this.expirer.setExpirationInterval(expirationInterval);
/*     */   }
/*     */ 
/*     */   public V put(K key, V value) {
/* 105 */     ExpiringObject answer = (ExpiringObject)this.delegate.put(key, new ExpiringObject(key, value, System.currentTimeMillis()));
/*     */ 
/* 107 */     if (answer == null) {
/* 108 */       return null;
/*     */     }
/*     */ 
/* 111 */     return answer.getValue();
/*     */   }
/*     */ 
/*     */   public V get(Object key) {
/* 115 */     ExpiringObject object = (ExpiringObject)this.delegate.get(key);
/*     */ 
/* 117 */     if (object != null) {
/* 118 */       object.setLastAccessTime(System.currentTimeMillis());
/*     */ 
/* 120 */       return object.getValue();
/*     */     }
/*     */ 
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   public V remove(Object key) {
/* 127 */     ExpiringObject answer = (ExpiringObject)this.delegate.remove(key);
/* 128 */     if (answer == null) {
/* 129 */       return null;
/*     */     }
/*     */ 
/* 132 */     return answer.getValue();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key) {
/* 136 */     return this.delegate.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value) {
/* 140 */     return this.delegate.containsValue(value);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 144 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 148 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 152 */     this.delegate.clear();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 157 */     return this.delegate.hashCode();
/*     */   }
/*     */ 
/*     */   public Set<K> keySet() {
/* 161 */     return this.delegate.keySet();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 166 */     return this.delegate.equals(obj);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> inMap) {
/* 170 */     for (Map.Entry e : inMap.entrySet())
/* 171 */       put(e.getKey(), e.getValue());
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 176 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entrySet() {
/* 180 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void addExpirationListener(ExpirationListener<V> listener) {
/* 184 */     this.expirationListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void removeExpirationListener(ExpirationListener<V> listener)
/*     */   {
/* 189 */     this.expirationListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public ExpiringMap<K, V>.Expirer getExpirer() {
/* 193 */     return this.expirer;
/*     */   }
/*     */ 
/*     */   public int getExpirationInterval() {
/* 197 */     return this.expirer.getExpirationInterval();
/*     */   }
/*     */ 
/*     */   public int getTimeToLive() {
/* 201 */     return this.expirer.getTimeToLive();
/*     */   }
/*     */ 
/*     */   public void setExpirationInterval(int expirationInterval) {
/* 205 */     this.expirer.setExpirationInterval(expirationInterval);
/*     */   }
/*     */ 
/*     */   public void setTimeToLive(int timeToLive) {
/* 209 */     this.expirer.setTimeToLive(timeToLive);
/*     */   }
/*     */ 
/*     */   public class Expirer
/*     */     implements Runnable
/*     */   {
/* 277 */     private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
/*     */     private long timeToLiveMillis;
/*     */     private long expirationIntervalMillis;
/* 283 */     private boolean running = false;
/*     */     private final Thread expirerThread;
/*     */ 
/*     */     public Expirer()
/*     */     {
/* 292 */       this.expirerThread = new Thread(this, "ExpiringMapExpirer-" + ExpiringMap.access$008());
/*     */ 
/* 294 */       this.expirerThread.setDaemon(true);
/*     */     }
/*     */ 
/*     */     public void run() {
/* 298 */       while (this.running) {
/* 299 */         processExpires();
/*     */         try
/*     */         {
/* 302 */           Thread.sleep(this.expirationIntervalMillis);
/*     */         } catch (InterruptedException e) {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     private void processExpires() {
/* 309 */       long timeNow = System.currentTimeMillis();
/*     */ 
/* 311 */       for (Iterator i$ = ExpiringMap.this.delegate.values().iterator(); i$.hasNext(); ) { o = (ExpiringMap.ExpiringObject)i$.next();
/*     */ 
/* 313 */         if (this.timeToLiveMillis <= 0L)
/*     */         {
/*     */           continue;
/*     */         }
/* 317 */         long timeIdle = timeNow - o.getLastAccessTime();
/*     */ 
/* 319 */         if (timeIdle >= this.timeToLiveMillis) {
/* 320 */           ExpiringMap.this.delegate.remove(o.getKey());
/*     */ 
/* 322 */           for (ExpirationListener listener : ExpiringMap.this.expirationListeners)
/* 323 */             listener.expired(o.getValue());
/*     */         }
/*     */       }
/*     */       ExpiringMap.ExpiringObject o;
/*     */     }
/*     */ 
/*     */     public void startExpiring()
/*     */     {
/* 334 */       this.stateLock.writeLock().lock();
/*     */       try
/*     */       {
/* 337 */         if (!this.running) {
/* 338 */           this.running = true;
/* 339 */           this.expirerThread.start();
/*     */         }
/*     */       } finally {
/* 342 */         this.stateLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void startExpiringIfNotStarted()
/*     */     {
/* 351 */       this.stateLock.readLock().lock();
/*     */       try {
/* 353 */         if (this.running) return;
/*     */       }
/*     */       finally {
/* 357 */         this.stateLock.readLock().unlock();
/*     */       }
/*     */ 
/* 360 */       this.stateLock.writeLock().lock();
/*     */       try {
/* 362 */         if (!this.running) {
/* 363 */           this.running = true;
/* 364 */           this.expirerThread.start();
/*     */         }
/*     */       } finally {
/* 367 */         this.stateLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void stopExpiring()
/*     */     {
/* 375 */       this.stateLock.writeLock().lock();
/*     */       try
/*     */       {
/* 378 */         if (this.running) {
/* 379 */           this.running = false;
/* 380 */           this.expirerThread.interrupt();
/*     */         }
/*     */       } finally {
/* 383 */         this.stateLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isRunning()
/*     */     {
/* 394 */       this.stateLock.readLock().lock();
/*     */       try
/*     */       {
/* 397 */         boolean bool = this.running;
/*     */         return bool; } finally { this.stateLock.readLock().unlock(); } throw localObject;
/*     */     }
/*     */ 
/*     */     public int getTimeToLive()
/*     */     {
/* 410 */       this.stateLock.readLock().lock();
/*     */       try
/*     */       {
/* 413 */         int i = (int)this.timeToLiveMillis / 1000;
/*     */         return i; } finally { this.stateLock.readLock().unlock(); } throw localObject;
/*     */     }
/*     */ 
/*     */     public void setTimeToLive(long timeToLive)
/*     */     {
/* 426 */       this.stateLock.writeLock().lock();
/*     */       try
/*     */       {
/* 429 */         this.timeToLiveMillis = (timeToLive * 1000L);
/*     */       } finally {
/* 431 */         this.stateLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public int getExpirationInterval()
/*     */     {
/* 443 */       this.stateLock.readLock().lock();
/*     */       try
/*     */       {
/* 446 */         int i = (int)this.expirationIntervalMillis / 1000;
/*     */         return i; } finally { this.stateLock.readLock().unlock(); } throw localObject;
/*     */     }
/*     */ 
/*     */     public void setExpirationInterval(long expirationInterval)
/*     */     {
/* 460 */       this.stateLock.writeLock().lock();
/*     */       try
/*     */       {
/* 463 */         this.expirationIntervalMillis = (expirationInterval * 1000L);
/*     */       } finally {
/* 465 */         this.stateLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ExpiringObject
/*     */   {
/*     */     private K key;
/*     */     private V value;
/*     */     private long lastAccessTime;
/* 219 */     private final ReadWriteLock lastAccessTimeLock = new ReentrantReadWriteLock();
/*     */ 
/*     */     ExpiringObject(V key, long value) {
/* 222 */       if (value == null) {
/* 223 */         throw new IllegalArgumentException("An expiring object cannot be null.");
/*     */       }
/*     */ 
/* 227 */       this.key = key;
/* 228 */       this.value = value;
/* 229 */       this.lastAccessTime = lastAccessTime;
/*     */     }
/*     */ 
/*     */     public long getLastAccessTime() {
/* 233 */       this.lastAccessTimeLock.readLock().lock();
/*     */       try
/*     */       {
/* 236 */         long l = this.lastAccessTime;
/*     */         return l; } finally { this.lastAccessTimeLock.readLock().unlock(); } throw localObject;
/*     */     }
/*     */ 
/*     */     public void setLastAccessTime(long lastAccessTime)
/*     */     {
/* 243 */       this.lastAccessTimeLock.writeLock().lock();
/*     */       try
/*     */       {
/* 246 */         this.lastAccessTime = lastAccessTime;
/*     */       } finally {
/* 248 */         this.lastAccessTimeLock.writeLock().unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public K getKey() {
/* 253 */       return this.key;
/*     */     }
/*     */ 
/*     */     public V getValue() {
/* 257 */       return this.value;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 262 */       return this.value.equals(obj);
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 267 */       return this.value.hashCode();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.ExpiringMap
 * JD-Core Version:    0.6.0
 */
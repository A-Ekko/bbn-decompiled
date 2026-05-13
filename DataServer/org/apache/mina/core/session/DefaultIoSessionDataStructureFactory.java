/*     */ package org.apache.mina.core.session;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.core.write.WriteRequest;
/*     */ import org.apache.mina.core.write.WriteRequestQueue;
/*     */ import org.apache.mina.util.CircularQueue;
/*     */ 
/*     */ public class DefaultIoSessionDataStructureFactory
/*     */   implements IoSessionDataStructureFactory
/*     */ {
/*     */   public IoSessionAttributeMap getAttributeMap(IoSession session)
/*     */     throws Exception
/*     */   {
/*  47 */     return new DefaultIoSessionAttributeMap(null);
/*     */   }
/*     */ 
/*     */   public WriteRequestQueue getWriteRequestQueue(IoSession session) throws Exception
/*     */   {
/*  52 */     return new DefaultWriteRequestQueue(null);
/*     */   }
/*     */ 
/*     */   private static class DefaultWriteRequestQueue
/*     */     implements WriteRequestQueue
/*     */   {
/* 163 */     private final Queue<WriteRequest> q = new CircularQueue(16);
/*     */ 
/*     */     public void dispose(IoSession session)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void clear(IoSession session)
/*     */     {
/* 175 */       this.q.clear();
/*     */     }
/*     */ 
/*     */     public synchronized boolean isEmpty(IoSession session)
/*     */     {
/* 182 */       return this.q.isEmpty();
/*     */     }
/*     */ 
/*     */     public synchronized void offer(IoSession session, WriteRequest writeRequest)
/*     */     {
/* 189 */       this.q.offer(writeRequest);
/*     */     }
/*     */ 
/*     */     public synchronized WriteRequest poll(IoSession session)
/*     */     {
/* 196 */       return (WriteRequest)this.q.poll();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 201 */       return this.q.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DefaultIoSessionAttributeMap
/*     */     implements IoSessionAttributeMap
/*     */   {
/*  57 */     private final Map<Object, Object> attributes = Collections.synchronizedMap(new HashMap(4));
/*     */ 
/*     */     public Object getAttribute(IoSession session, Object key, Object defaultValue)
/*     */     {
/*  61 */       if (key == null) {
/*  62 */         throw new NullPointerException("key");
/*     */       }
/*     */ 
/*  65 */       Object answer = this.attributes.get(key);
/*  66 */       if (answer == null) {
/*  67 */         return defaultValue;
/*     */       }
/*  69 */       return answer;
/*     */     }
/*     */ 
/*     */     public Object setAttribute(IoSession session, Object key, Object value)
/*     */     {
/*  74 */       if (key == null) {
/*  75 */         throw new NullPointerException("key");
/*     */       }
/*     */ 
/*  78 */       if (value == null) {
/*  79 */         return this.attributes.remove(key);
/*     */       }
/*  81 */       return this.attributes.put(key, value);
/*     */     }
/*     */ 
/*     */     public Object setAttributeIfAbsent(IoSession session, Object key, Object value)
/*     */     {
/*  86 */       if (key == null) {
/*  87 */         throw new NullPointerException("key");
/*     */       }
/*     */ 
/*  90 */       if (value == null)
/*  91 */         return null;
/*     */       Object oldValue;
/*  95 */       synchronized (this.attributes) {
/*  96 */         oldValue = this.attributes.get(key);
/*  97 */         if (oldValue == null) {
/*  98 */           this.attributes.put(key, value);
/*     */         }
/*     */       }
/* 101 */       return oldValue;
/*     */     }
/*     */ 
/*     */     public Object removeAttribute(IoSession session, Object key) {
/* 105 */       if (key == null) {
/* 106 */         throw new NullPointerException("key");
/*     */       }
/*     */ 
/* 109 */       return this.attributes.remove(key);
/*     */     }
/*     */ 
/*     */     public boolean removeAttribute(IoSession session, Object key, Object value) {
/* 113 */       if (key == null) {
/* 114 */         throw new NullPointerException("key");
/*     */       }
/*     */ 
/* 117 */       if (value == null) {
/* 118 */         return false;
/*     */       }
/*     */ 
/* 121 */       synchronized (this.attributes) {
/* 122 */         if (value.equals(this.attributes.get(key))) {
/* 123 */           this.attributes.remove(key);
/* 124 */           return true;
/*     */         }
/*     */       }
/*     */ 
/* 128 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean replaceAttribute(IoSession session, Object key, Object oldValue, Object newValue) {
/* 132 */       synchronized (this.attributes) {
/* 133 */         Object actualOldValue = this.attributes.get(key);
/* 134 */         if (actualOldValue == null) {
/* 135 */           return false;
/*     */         }
/*     */ 
/* 138 */         if (actualOldValue.equals(oldValue)) {
/* 139 */           this.attributes.put(key, newValue);
/* 140 */           return true;
/*     */         }
/* 142 */         return false;
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean containsAttribute(IoSession session, Object key)
/*     */     {
/* 148 */       return this.attributes.containsKey(key);
/*     */     }
/*     */ 
/*     */     public Set<Object> getAttributeKeys(IoSession session) {
/* 152 */       synchronized (this.attributes) {
/* 153 */         return new HashSet(this.attributes.keySet());
/*     */       }
/*     */     }
/*     */ 
/*     */     public void dispose(IoSession session)
/*     */       throws Exception
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.session.DefaultIoSessionDataStructureFactory
 * JD-Core Version:    0.6.0
 */
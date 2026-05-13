/*     */ package org.apache.mina.filter.util;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.core.filterchain.IoFilterAdapter;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ 
/*     */ public class SessionAttributeInitializingFilter extends IoFilterAdapter
/*     */ {
/*  43 */   private final Map<String, Object> attributes = new ConcurrentHashMap();
/*     */ 
/*     */   public SessionAttributeInitializingFilter()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SessionAttributeInitializingFilter(Map<String, ? extends Object> attributes)
/*     */   {
/*  60 */     setAttributes(attributes);
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String key)
/*     */   {
/*  70 */     return this.attributes.get(key);
/*     */   }
/*     */ 
/*     */   public Object setAttribute(String key, Object value)
/*     */   {
/*  81 */     if (value == null) {
/*  82 */       return removeAttribute(key);
/*     */     }
/*  84 */     return this.attributes.put(key, value);
/*     */   }
/*     */ 
/*     */   public Object setAttribute(String key)
/*     */   {
/*  97 */     return this.attributes.put(key, Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public Object removeAttribute(String key)
/*     */   {
/* 106 */     return this.attributes.remove(key);
/*     */   }
/*     */ 
/*     */   boolean containsAttribute(String key)
/*     */   {
/* 114 */     return this.attributes.containsKey(key);
/*     */   }
/*     */ 
/*     */   public Set<String> getAttributeKeys()
/*     */   {
/* 121 */     return this.attributes.keySet();
/*     */   }
/*     */ 
/*     */   public void setAttributes(Map<String, ? extends Object> attributes)
/*     */   {
/* 130 */     if (attributes == null) {
/* 131 */       attributes = new HashMap();
/*     */     }
/*     */ 
/* 134 */     this.attributes.clear();
/* 135 */     this.attributes.putAll(attributes);
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session)
/*     */     throws Exception
/*     */   {
/* 145 */     for (Map.Entry e : this.attributes.entrySet()) {
/* 146 */       session.setAttribute(e.getKey(), e.getValue());
/*     */     }
/*     */ 
/* 149 */     nextFilter.sessionCreated(session);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.util.SessionAttributeInitializingFilter
 * JD-Core Version:    0.6.0
 */
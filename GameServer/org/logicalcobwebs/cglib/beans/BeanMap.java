/*     */ package org.logicalcobwebs.cglib.beans;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ 
/*     */ public abstract class BeanMap
/*     */   implements Map
/*     */ {
/*     */   public static final int REQUIRE_GETTER = 1;
/*     */   public static final int REQUIRE_SETTER = 2;
/*     */   protected Object bean;
/*     */ 
/*     */   public static BeanMap create(Object bean)
/*     */   {
/*  56 */     Generator gen = new Generator();
/*  57 */     gen.setBean(bean);
/*  58 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public abstract BeanMap newInstance(Object paramObject);
/*     */ 
/*     */   public abstract Class getPropertyType(String paramString);
/*     */ 
/*     */   protected BeanMap()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected BeanMap(Object bean)
/*     */   {
/* 159 */     setBean(bean);
/*     */   }
/*     */ 
/*     */   public Object get(Object key) {
/* 163 */     return get(this.bean, key);
/*     */   }
/*     */ 
/*     */   public Object put(Object key, Object value) {
/* 167 */     return put(this.bean, key, value);
/*     */   }
/*     */ 
/*     */   public abstract Object get(Object paramObject1, Object paramObject2);
/*     */ 
/*     */   public abstract Object put(Object paramObject1, Object paramObject2, Object paramObject3);
/*     */ 
/*     */   public void setBean(Object bean)
/*     */   {
/* 196 */     this.bean = bean;
/*     */   }
/*     */ 
/*     */   public Object getBean()
/*     */   {
/* 205 */     return this.bean;
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 209 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key) {
/* 213 */     return keySet().contains(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(Object value) {
/* 217 */     for (Iterator it = keySet().iterator(); it.hasNext(); ) {
/* 218 */       Object v = get(it.next());
/* 219 */       if (((value == null) && (v == null)) || (value.equals(v)))
/* 220 */         return true;
/*     */     }
/* 222 */     return false;
/*     */   }
/*     */ 
/*     */   public int size() {
/* 226 */     return keySet().size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 230 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public Object remove(Object key) {
/* 234 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void putAll(Map t) {
/* 238 */     for (Iterator it = t.keySet().iterator(); it.hasNext(); ) {
/* 239 */       Object key = it.next();
/* 240 */       put(key, t.get(key));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 245 */     if ((o == null) || (!(o instanceof Map))) {
/* 246 */       return false;
/*     */     }
/* 248 */     Map other = (Map)o;
/* 249 */     if (size() != other.size()) {
/* 250 */       return false;
/*     */     }
/* 252 */     for (Iterator it = keySet().iterator(); it.hasNext(); ) {
/* 253 */       Object key = it.next();
/* 254 */       if (!other.containsKey(key)) {
/* 255 */         return false;
/*     */       }
/* 257 */       Object v1 = get(key);
/* 258 */       Object v2 = other.get(key);
/* 259 */       if (v1 == null ? v2 != null : !v1.equals(v2)) {
/* 260 */         return false;
/*     */       }
/*     */     }
/* 263 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 267 */     int code = 0;
/* 268 */     for (Iterator it = keySet().iterator(); it.hasNext(); ) {
/* 269 */       Object key = it.next();
/* 270 */       Object value = get(key);
/* 271 */       code += ((key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode()));
/*     */     }
/*     */ 
/* 274 */     return code;
/*     */   }
/*     */ 
/*     */   public Set entrySet()
/*     */   {
/* 279 */     HashMap copy = new HashMap();
/* 280 */     for (Iterator it = keySet().iterator(); it.hasNext(); ) {
/* 281 */       Object key = it.next();
/* 282 */       copy.put(key, get(key));
/*     */     }
/* 284 */     return Collections.unmodifiableMap(copy).entrySet();
/*     */   }
/*     */ 
/*     */   public Collection values() {
/* 288 */     Set keys = keySet();
/* 289 */     List values = new ArrayList(keys.size());
/* 290 */     for (Iterator it = keys.iterator(); it.hasNext(); ) {
/* 291 */       values.add(get(it.next()));
/*     */     }
/* 293 */     return Collections.unmodifiableCollection(values);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 301 */     StringBuffer sb = new StringBuffer();
/* 302 */     sb.append('{');
/* 303 */     for (Iterator it = keySet().iterator(); it.hasNext(); ) {
/* 304 */       Object key = it.next();
/* 305 */       sb.append(key);
/* 306 */       sb.append('=');
/* 307 */       sb.append(get(key));
/* 308 */       if (it.hasNext()) {
/* 309 */         sb.append(", ");
/*     */       }
/*     */     }
/* 312 */     sb.append('}');
/* 313 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator
/*     */   {
/*  62 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanMap.class.getName());
/*     */ 
/*  64 */     private static final BeanMapKey KEY_FACTORY = (BeanMapKey)KeyFactory.create(BeanMapKey.class, KeyFactory.CLASS_BY_NAME);
/*     */     private Object bean;
/*     */     private Class beanClass;
/*     */     private int require;
/*     */ 
/*     */     public Generator()
/*     */     {
/*  76 */       super();
/*     */     }
/*     */ 
/*     */     public void setBean(Object bean)
/*     */     {
/*  87 */       this.bean = bean;
/*  88 */       if (bean != null)
/*  89 */         this.beanClass = bean.getClass();
/*     */     }
/*     */ 
/*     */     public void setBeanClass(Class beanClass)
/*     */     {
/*  98 */       this.beanClass = beanClass;
/*     */     }
/*     */ 
/*     */     public void setRequire(int require)
/*     */     {
/* 107 */       this.require = require;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 111 */       return this.beanClass.getClassLoader();
/*     */     }
/*     */ 
/*     */     public BeanMap create()
/*     */     {
/* 119 */       if (this.beanClass == null)
/* 120 */         throw new IllegalArgumentException("Class of bean unknown");
/* 121 */       setNamePrefix(this.beanClass.getName());
/* 122 */       return (BeanMap)super.create(KEY_FACTORY.newInstance(this.beanClass, this.require));
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/* 126 */       new BeanMapEmitter(v, getClassName(), this.beanClass, this.require);
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 130 */       return ((BeanMap)ReflectUtils.newInstance(type)).newInstance(this.bean);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 134 */       return ((BeanMap)instance).newInstance(this.bean);
/*     */     }
/*     */ 
/*     */     static abstract interface BeanMapKey
/*     */     {
/*     */       public abstract Object newInstance(Class paramClass, int paramInt);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.BeanMap
 * JD-Core Version:    0.6.0
 */
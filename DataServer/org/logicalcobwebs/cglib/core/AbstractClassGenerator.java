/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*     */ 
/*     */ public abstract class AbstractClassGenerator
/*     */   implements ClassGenerator
/*     */ {
/*  35 */   private static final Object NAME_KEY = new Object();
/*  36 */   private static final ThreadLocal CURRENT = new ThreadLocal();
/*     */ 
/*  38 */   private GeneratorStrategy strategy = DefaultGeneratorStrategy.INSTANCE;
/*  39 */   private NamingPolicy namingPolicy = DefaultNamingPolicy.INSTANCE;
/*     */   private Source source;
/*     */   private ClassLoader classLoader;
/*     */   private String namePrefix;
/*     */   private Object key;
/*  44 */   private boolean useCache = true;
/*     */   private String className;
/*     */   private boolean attemptLoad;
/*     */ 
/*     */   protected AbstractClassGenerator(Source source)
/*     */   {
/*  57 */     this.source = source;
/*     */   }
/*     */ 
/*     */   protected void setNamePrefix(String namePrefix) {
/*  61 */     this.namePrefix = namePrefix;
/*     */   }
/*     */ 
/*     */   protected final String getClassName() {
/*  65 */     if (this.className == null)
/*  66 */       this.className = getClassName(getClassLoader());
/*  67 */     return this.className;
/*     */   }
/*     */ 
/*     */   private String getClassName(ClassLoader loader) {
/*  71 */     Set nameCache = getClassNameCache(loader);
/*  72 */     return this.namingPolicy.getClassName(this.namePrefix, this.source.name, this.key, new Predicate(nameCache) {
/*     */       public boolean evaluate(Object arg) {
/*  74 */         return this.val$nameCache.contains(arg);
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private Set getClassNameCache(ClassLoader loader) {
/*  80 */     return (Set)((Map)this.source.cache.get(loader)).get(NAME_KEY);
/*     */   }
/*     */ 
/*     */   public void setClassLoader(ClassLoader classLoader)
/*     */   {
/*  93 */     this.classLoader = classLoader;
/*     */   }
/*     */ 
/*     */   public void setNamingPolicy(NamingPolicy namingPolicy)
/*     */   {
/* 102 */     if (namingPolicy == null)
/* 103 */       namingPolicy = DefaultNamingPolicy.INSTANCE;
/* 104 */     this.namingPolicy = namingPolicy;
/*     */   }
/*     */ 
/*     */   public NamingPolicy getNamingPolicy()
/*     */   {
/* 111 */     return this.namingPolicy;
/*     */   }
/*     */ 
/*     */   public void setUseCache(boolean useCache)
/*     */   {
/* 119 */     this.useCache = useCache;
/*     */   }
/*     */ 
/*     */   public boolean getUseCache()
/*     */   {
/* 126 */     return this.useCache;
/*     */   }
/*     */ 
/*     */   public void setAttemptLoad(boolean attemptLoad)
/*     */   {
/* 135 */     this.attemptLoad = attemptLoad;
/*     */   }
/*     */ 
/*     */   public boolean getAttemptLoad() {
/* 139 */     return this.attemptLoad;
/*     */   }
/*     */ 
/*     */   public void setStrategy(GeneratorStrategy strategy)
/*     */   {
/* 147 */     if (strategy == null)
/* 148 */       strategy = DefaultGeneratorStrategy.INSTANCE;
/* 149 */     this.strategy = strategy;
/*     */   }
/*     */ 
/*     */   public GeneratorStrategy getStrategy()
/*     */   {
/* 156 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public static AbstractClassGenerator getCurrent()
/*     */   {
/* 164 */     return (AbstractClassGenerator)CURRENT.get();
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader() {
/* 168 */     ClassLoader t = this.classLoader;
/* 169 */     if (t == null) {
/* 170 */       t = getDefaultClassLoader();
/*     */     }
/* 172 */     if (t == null) {
/* 173 */       t = getClass().getClassLoader();
/*     */     }
/* 175 */     if (t == null) {
/* 176 */       t = Thread.currentThread().getContextClassLoader();
/*     */     }
/* 178 */     if (t == null) {
/* 179 */       throw new IllegalStateException("Cannot determine classloader");
/*     */     }
/* 181 */     return t;
/*     */   }
/*     */   protected abstract ClassLoader getDefaultClassLoader();
/*     */ 
/*     */   protected Object create(Object key) {
/*     */     try {
/* 188 */       Class gen = null;
/*     */ 
/* 190 */       synchronized (this.source) {
/* 191 */         ClassLoader loader = getClassLoader();
/* 192 */         Map cache2 = null;
/* 193 */         cache2 = (Map)this.source.cache.get(loader);
/* 194 */         if (cache2 == null) {
/* 195 */           cache2 = new HashMap();
/* 196 */           cache2.put(NAME_KEY, new HashSet());
/* 197 */           this.source.cache.put(loader, cache2);
/* 198 */         } else if (this.useCache) {
/* 199 */           Reference ref = (Reference)cache2.get(key);
/* 200 */           gen = (Class)(ref == null ? null : ref.get());
/*     */         }
/* 202 */         if (gen == null) {
/* 203 */           Object save = CURRENT.get();
/* 204 */           CURRENT.set(this);
/*     */           try {
/* 206 */             this.key = key;
/*     */ 
/* 208 */             if (this.attemptLoad)
/*     */               try {
/* 210 */                 gen = loader.loadClass(getClassName());
/*     */               }
/*     */               catch (ClassNotFoundException e)
/*     */               {
/*     */               }
/* 215 */             if (gen == null) {
/* 216 */               b = this.strategy.generate(this);
/* 217 */               String className = ClassNameReader.getClassName(new ClassReader(b));
/* 218 */               getClassNameCache(loader).add(className);
/* 219 */               gen = ReflectUtils.defineClass(className, b, loader);
/*     */             }
/*     */ 
/* 222 */             if (this.useCache) {
/* 223 */               cache2.put(key, new WeakReference(gen));
/*     */             }
/* 225 */             byte[] b = firstInstance(gen);
/*     */ 
/* 227 */             CURRENT.set(save);
/* 228 */             return b;
/*     */           }
/*     */           finally
/*     */           {
/* 227 */             CURRENT.set(save);
/*     */           }
/*     */         }
/*     */       }
/* 231 */       return firstInstance(gen);
/*     */     } catch (RuntimeException e) {
/* 233 */       throw e;
/*     */     } catch (Error e) {
/* 235 */       throw e; } catch (Exception e) {
/*     */     }
/* 237 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   protected abstract Object firstInstance(Class paramClass)
/*     */     throws Exception;
/*     */ 
/*     */   protected abstract Object nextInstance(Object paramObject)
/*     */     throws Exception;
/*     */ 
/*     */   protected static class Source
/*     */   {
/*     */     String name;
/*  50 */     Map cache = new WeakHashMap();
/*     */ 
/*  52 */     public Source(String name) { this.name = name;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.AbstractClassGenerator
 * JD-Core Version:    0.6.0
 */
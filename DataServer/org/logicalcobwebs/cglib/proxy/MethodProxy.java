/*     */ package org.logicalcobwebs.cglib.proxy;
/*     */ 
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.reflect.FastClass;
/*     */ import org.logicalcobwebs.cglib.reflect.FastClass.Generator;
/*     */ 
/*     */ public class MethodProxy
/*     */ {
/*     */   private Signature sig;
/*     */   private String superName;
/*     */   private FastClass f1;
/*     */   private FastClass f2;
/*     */   private int i1;
/*     */   private int i2;
/*     */ 
/*     */   public static MethodProxy create(ClassLoader loader, Class c1, Class c2, String desc, String name1, String name2)
/*     */   {
/*  44 */     Signature sig1 = new Signature(name1, desc);
/*  45 */     Signature sig2 = new Signature(name2, desc);
/*  46 */     FastClass f1 = helper(loader, c1);
/*  47 */     FastClass f2 = helper(loader, c2);
/*  48 */     int i1 = f1.getIndex(sig1);
/*  49 */     int i2 = f2.getIndex(sig2);
/*     */     MethodProxy proxy;
/*     */     MethodProxy proxy;
/*  52 */     if (i1 < 0)
/*  53 */       proxy = new MethodProxy(sig1) {
/*     */         public Object invoke(Object obj, Object[] args) throws Throwable {
/*  55 */           throw new IllegalArgumentException("Protected method: " + this.val$sig1);
/*     */         }
/*     */       };
/*  59 */     else proxy = new MethodProxy();
/*     */ 
/*  62 */     proxy.f1 = f1;
/*  63 */     proxy.f2 = f2;
/*  64 */     proxy.i1 = i1;
/*  65 */     proxy.i2 = i2;
/*  66 */     proxy.sig = sig1;
/*  67 */     proxy.superName = name2;
/*  68 */     return proxy;
/*     */   }
/*     */ 
/*     */   private static FastClass helper(ClassLoader loader, Class type) {
/*  72 */     FastClass.Generator g = new FastClass.Generator();
/*  73 */     g.setType(type);
/*  74 */     g.setClassLoader(loader);
/*  75 */     AbstractClassGenerator fromEnhancer = AbstractClassGenerator.getCurrent();
/*  76 */     if (fromEnhancer != null) {
/*  77 */       g.setNamingPolicy(fromEnhancer.getNamingPolicy());
/*  78 */       g.setStrategy(fromEnhancer.getStrategy());
/*  79 */       g.setAttemptLoad(fromEnhancer.getAttemptLoad());
/*     */     }
/*  81 */     return g.create();
/*     */   }
/*     */ 
/*     */   public Signature getSignature()
/*     */   {
/*  91 */     return this.sig;
/*     */   }
/*     */ 
/*     */   public String getSuperName()
/*     */   {
/* 101 */     return this.superName;
/*     */   }
/*     */ 
/*     */   public int getSuperIndex()
/*     */   {
/* 112 */     return this.i2;
/*     */   }
/*     */ 
/*     */   public static MethodProxy find(Class type, Signature sig)
/*     */   {
/*     */     try
/*     */     {
/* 125 */       Method m = type.getDeclaredMethod("CGLIB$findMethodProxy", MethodInterceptorGenerator.FIND_PROXY_TYPES);
/*     */ 
/* 127 */       return (MethodProxy)m.invoke(null, new Object[] { sig });
/*     */     } catch (NoSuchMethodException e) {
/* 129 */       throw new IllegalArgumentException("Class " + type + " does not use a MethodInterceptor");
/*     */     } catch (IllegalAccessException e) {
/* 131 */       throw new CodeGenerationException(e); } catch (InvocationTargetException e) {
/*     */     }
/* 133 */     throw new CodeGenerationException(e);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object obj, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 149 */       return this.f1.invoke(this.i1, obj, args); } catch (InvocationTargetException e) {
/*     */     }
/* 151 */     throw e.getTargetException();
/*     */   }
/*     */ 
/*     */   public Object invokeSuper(Object obj, Object[] args)
/*     */     throws Throwable
/*     */   {
/*     */     try
/*     */     {
/* 167 */       return this.f2.invoke(this.i2, obj, args); } catch (InvocationTargetException e) {
/*     */     }
/* 169 */     throw e.getTargetException();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MethodProxy
 * JD-Core Version:    0.6.0
 */
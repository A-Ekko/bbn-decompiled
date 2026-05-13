/*     */ package org.logicalcobwebs.cglib.reflect;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ 
/*     */ public abstract class FastClass
/*     */ {
/*     */   private Class type;
/*     */ 
/*     */   protected FastClass()
/*     */   {
/*  30 */     throw new Error("Using the FastClass empty constructor--please report to the cglib-devel mailing list");
/*     */   }
/*     */ 
/*     */   protected FastClass(Class type) {
/*  34 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public static FastClass create(Class type)
/*     */   {
/*  39 */     return create(type.getClassLoader(), type);
/*     */   }
/*     */ 
/*     */   public static FastClass create(ClassLoader loader, Class type) {
/*  43 */     Generator gen = new Generator();
/*  44 */     gen.setType(type);
/*  45 */     gen.setClassLoader(loader);
/*  46 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public Object invoke(String name, Class[] parameterTypes, Object obj, Object[] args)
/*     */     throws InvocationTargetException
/*     */   {
/*  87 */     return invoke(getIndex(name, parameterTypes), obj, args);
/*     */   }
/*     */ 
/*     */   public Object newInstance() throws InvocationTargetException {
/*  91 */     return newInstance(getIndex(Constants.EMPTY_CLASS_ARRAY), null);
/*     */   }
/*     */ 
/*     */   public Object newInstance(Class[] parameterTypes, Object[] args) throws InvocationTargetException {
/*  95 */     return newInstance(getIndex(parameterTypes), args);
/*     */   }
/*     */ 
/*     */   public FastMethod getMethod(Method method) {
/*  99 */     return new FastMethod(this, method);
/*     */   }
/*     */ 
/*     */   public FastConstructor getConstructor(Constructor constructor) {
/* 103 */     return new FastConstructor(this, constructor);
/*     */   }
/*     */ 
/*     */   public FastMethod getMethod(String name, Class[] parameterTypes) {
/*     */     try {
/* 108 */       return getMethod(this.type.getMethod(name, parameterTypes)); } catch (NoSuchMethodException e) {
/*     */     }
/* 110 */     throw new NoSuchMethodError(e.getMessage());
/*     */   }
/*     */ 
/*     */   public FastConstructor getConstructor(Class[] parameterTypes)
/*     */   {
/*     */     try {
/* 116 */       return getConstructor(this.type.getConstructor(parameterTypes)); } catch (NoSuchMethodException e) {
/*     */     }
/* 118 */     throw new NoSuchMethodError(e.getMessage());
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/* 123 */     return this.type.getName();
/*     */   }
/*     */ 
/*     */   public Class getJavaClass() {
/* 127 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 131 */     return this.type.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 135 */     return this.type.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o) {
/* 139 */     if ((o == null) || (!(o instanceof FastClass))) {
/* 140 */       return false;
/*     */     }
/* 142 */     return this.type.equals(((FastClass)o).type);
/*     */   }
/*     */ 
/*     */   public abstract int getIndex(String paramString, Class[] paramArrayOfClass);
/*     */ 
/*     */   public abstract int getIndex(Class[] paramArrayOfClass);
/*     */ 
/*     */   public abstract Object invoke(int paramInt, Object paramObject, Object[] paramArrayOfObject)
/*     */     throws InvocationTargetException;
/*     */ 
/*     */   public abstract Object newInstance(int paramInt, Object[] paramArrayOfObject)
/*     */     throws InvocationTargetException;
/*     */ 
/*     */   public abstract int getIndex(Signature paramSignature);
/*     */ 
/*     */   public abstract int getMaxIndex();
/*     */ 
/*     */   protected static String getSignatureWithoutReturnType(String name, Class[] parameterTypes)
/*     */   {
/* 193 */     StringBuffer sb = new StringBuffer();
/* 194 */     sb.append(name);
/* 195 */     sb.append('(');
/* 196 */     for (int i = 0; i < parameterTypes.length; i++) {
/* 197 */       sb.append(Type.getDescriptor(parameterTypes[i]));
/*     */     }
/* 199 */     sb.append(')');
/* 200 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static class Generator extends AbstractClassGenerator
/*     */   {
/*  51 */     private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(FastClass.class.getName());
/*     */     private Class type;
/*     */ 
/*     */     public Generator()
/*     */     {
/*  55 */       super();
/*     */     }
/*     */ 
/*     */     public void setType(Class type) {
/*  59 */       this.type = type;
/*     */     }
/*     */ 
/*     */     public FastClass create() {
/*  63 */       setNamePrefix(this.type.getName());
/*  64 */       return (FastClass)super.create(this.type.getName());
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/*  68 */       return this.type.getClassLoader();
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws Exception {
/*  72 */       new FastClassEmitter(v, getClassName(), this.type);
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/*  76 */       return ReflectUtils.newInstance(type, new Class[] { Class.class }, new Object[] { this.type });
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance)
/*     */     {
/*  82 */       return instance;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.FastClass
 * JD-Core Version:    0.6.0
 */
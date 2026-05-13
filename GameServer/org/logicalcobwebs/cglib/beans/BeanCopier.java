/*     */ package org.logicalcobwebs.cglib.beans;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.Converter;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.Local;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ public abstract class BeanCopier
/*     */ {
/*  30 */   private static final BeanCopierKey KEY_FACTORY = (BeanCopierKey)KeyFactory.create(BeanCopierKey.class);
/*     */ 
/*  32 */   private static final Type CONVERTER = TypeUtils.parseType("org.logicalcobwebs.cglib.core.Converter");
/*     */ 
/*  34 */   private static final Type BEAN_COPIER = TypeUtils.parseType("org.logicalcobwebs.cglib.beans.BeanCopier");
/*     */ 
/*  36 */   private static final Signature COPY = new Signature("copy", Type.VOID_TYPE, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_OBJECT, CONVERTER });
/*     */ 
/*  38 */   private static final Signature CONVERT = TypeUtils.parseSignature("Object convert(Object, Class, Object)");
/*     */ 
/*     */   public static BeanCopier create(Class source, Class target, boolean useConverter)
/*     */   {
/*  46 */     Generator gen = new Generator();
/*  47 */     gen.setSource(source);
/*  48 */     gen.setTarget(target);
/*  49 */     gen.setUseConverter(useConverter);
/*  50 */     return gen.create(); } 
/*     */   public abstract void copy(Object paramObject1, Object paramObject2, Converter paramConverter);
/*     */ 
/*  56 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(BeanCopier.class.getName());
/*     */     private Class source;
/*     */     private Class target;
/*     */     private boolean useConverter;
/*     */ 
/*  62 */     public Generator() { super(); }
/*     */ 
/*     */     public void setSource(Class source)
/*     */     {
/*  66 */       if (!Modifier.isPublic(source.getModifiers())) {
/*  67 */         setNamePrefix(source.getName());
/*     */       }
/*  69 */       this.source = source;
/*     */     }
/*     */ 
/*     */     public void setTarget(Class target) {
/*  73 */       if (!Modifier.isPublic(target.getModifiers())) {
/*  74 */         setNamePrefix(target.getName());
/*     */       }
/*     */ 
/*  77 */       this.target = target;
/*     */     }
/*     */ 
/*     */     public void setUseConverter(boolean useConverter) {
/*  81 */       this.useConverter = useConverter;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/*  85 */       return this.source.getClassLoader();
/*     */     }
/*     */ 
/*     */     public BeanCopier create() {
/*  89 */       Object key = BeanCopier.KEY_FACTORY.newInstance(this.source.getName(), this.target.getName(), this.useConverter);
/*  90 */       return (BeanCopier)super.create(key);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/*  94 */       Type sourceType = Type.getType(this.source);
/*  95 */       Type targetType = Type.getType(this.target);
/*  96 */       ClassEmitter ce = new ClassEmitter(v);
/*  97 */       ce.begin_class(46, 1, getClassName(), BeanCopier.BEAN_COPIER, null, "<generated>");
/*     */ 
/* 104 */       EmitUtils.null_constructor(ce);
/* 105 */       CodeEmitter e = ce.begin_method(1, BeanCopier.COPY, null, null);
/* 106 */       PropertyDescriptor[] getters = ReflectUtils.getBeanGetters(this.source);
/* 107 */       PropertyDescriptor[] setters = ReflectUtils.getBeanGetters(this.target);
/*     */ 
/* 109 */       Map names = new HashMap();
/* 110 */       for (int i = 0; i < getters.length; i++) {
/* 111 */         names.put(getters[i].getName(), getters[i]);
/*     */       }
/* 113 */       Local targetLocal = e.make_local();
/* 114 */       Local sourceLocal = e.make_local();
/* 115 */       if (this.useConverter) {
/* 116 */         e.load_arg(1);
/* 117 */         e.checkcast(targetType);
/* 118 */         e.store_local(targetLocal);
/* 119 */         e.load_arg(0);
/* 120 */         e.checkcast(sourceType);
/* 121 */         e.store_local(sourceLocal);
/*     */       } else {
/* 123 */         e.load_arg(1);
/* 124 */         e.checkcast(targetType);
/* 125 */         e.load_arg(0);
/* 126 */         e.checkcast(sourceType);
/*     */       }
/* 128 */       for (int i = 0; i < setters.length; i++) {
/* 129 */         PropertyDescriptor setter = setters[i];
/* 130 */         PropertyDescriptor getter = (PropertyDescriptor)names.get(setter.getName());
/* 131 */         if (getter != null) {
/* 132 */           MethodInfo read = ReflectUtils.getMethodInfo(getter.getReadMethod());
/* 133 */           MethodInfo write = ReflectUtils.getMethodInfo(setter.getWriteMethod());
/* 134 */           if (this.useConverter) {
/* 135 */             Type setterType = write.getSignature().getArgumentTypes()[0];
/* 136 */             e.load_local(targetLocal);
/* 137 */             e.load_arg(2);
/* 138 */             e.load_local(sourceLocal);
/* 139 */             e.invoke(read);
/* 140 */             e.box(read.getSignature().getReturnType());
/* 141 */             EmitUtils.load_class(e, setterType);
/* 142 */             e.push(write.getSignature().getName());
/* 143 */             e.invoke_interface(BeanCopier.CONVERTER, BeanCopier.CONVERT);
/* 144 */             e.unbox_or_zero(setterType);
/* 145 */             e.invoke(write);
/* 146 */           } else if (compatible(getter, setter)) {
/* 147 */             e.dup2();
/* 148 */             e.invoke(read);
/* 149 */             e.invoke(write);
/*     */           }
/*     */         }
/*     */       }
/* 153 */       e.return_value();
/* 154 */       e.end_method();
/* 155 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     private static boolean compatible(PropertyDescriptor getter, PropertyDescriptor setter)
/*     */     {
/* 160 */       return setter.getPropertyType().isAssignableFrom(getter.getPropertyType());
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 164 */       return ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 168 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface BeanCopierKey
/*     */   {
/*     */     public abstract Object newInstance(String paramString1, String paramString2, boolean paramBoolean);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.BeanCopier
 * JD-Core Version:    0.6.0
 */
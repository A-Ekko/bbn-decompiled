/*     */ package org.logicalcobwebs.cglib.beans;
/*     */ 
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.lang.reflect.Method;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ public class ImmutableBean
/*     */ {
/*  29 */   private static final Type ILLEGAL_STATE_EXCEPTION = TypeUtils.parseType("IllegalStateException");
/*     */ 
/*  31 */   private static final Signature CSTRUCT_OBJECT = TypeUtils.parseConstructor("Object");
/*     */ 
/*  33 */   private static final Class[] OBJECT_CLASSES = { Object.class };
/*     */   private static final String FIELD_NAME = "CGLIB$RWBean";
/*     */ 
/*     */   public static Object create(Object bean)
/*     */   {
/*  40 */     Generator gen = new Generator();
/*  41 */     gen.setBean(bean);
/*  42 */     return gen.create();
/*     */   }
/*  46 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(ImmutableBean.class.getName());
/*     */     private Object bean;
/*     */     private Class target;
/*     */ 
/*  51 */     public Generator() { super(); }
/*     */ 
/*     */     public void setBean(Object bean)
/*     */     {
/*  55 */       this.bean = bean;
/*  56 */       this.target = bean.getClass();
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/*  60 */       return this.target.getClassLoader();
/*     */     }
/*     */ 
/*     */     public Object create() {
/*  64 */       String name = this.target.getName();
/*  65 */       setNamePrefix(name);
/*  66 */       return super.create(name);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/*  70 */       Type targetType = Type.getType(this.target);
/*  71 */       ClassEmitter ce = new ClassEmitter(v);
/*  72 */       ce.begin_class(46, 1, getClassName(), targetType, null, "<generated>");
/*     */ 
/*  79 */       ce.declare_field(18, "CGLIB$RWBean", targetType, null, null);
/*     */ 
/*  81 */       CodeEmitter e = ce.begin_method(1, ImmutableBean.CSTRUCT_OBJECT, null, null);
/*  82 */       e.load_this();
/*  83 */       e.super_invoke_constructor();
/*  84 */       e.load_this();
/*  85 */       e.load_arg(0);
/*  86 */       e.checkcast(targetType);
/*  87 */       e.putfield("CGLIB$RWBean");
/*  88 */       e.return_value();
/*  89 */       e.end_method();
/*     */ 
/*  91 */       PropertyDescriptor[] descriptors = ReflectUtils.getBeanProperties(this.target);
/*  92 */       Method[] getters = ReflectUtils.getPropertyMethods(descriptors, true, false);
/*  93 */       Method[] setters = ReflectUtils.getPropertyMethods(descriptors, false, true);
/*     */ 
/*  95 */       for (int i = 0; i < getters.length; i++) {
/*  96 */         MethodInfo getter = ReflectUtils.getMethodInfo(getters[i]);
/*  97 */         e = EmitUtils.begin_method(ce, getter, 1);
/*  98 */         e.load_this();
/*  99 */         e.getfield("CGLIB$RWBean");
/* 100 */         e.invoke(getter);
/* 101 */         e.return_value();
/* 102 */         e.end_method();
/*     */       }
/*     */ 
/* 105 */       for (int i = 0; i < setters.length; i++) {
/* 106 */         MethodInfo setter = ReflectUtils.getMethodInfo(setters[i]);
/* 107 */         e = EmitUtils.begin_method(ce, setter, 1);
/* 108 */         e.throw_exception(ImmutableBean.ILLEGAL_STATE_EXCEPTION, "Bean is immutable");
/* 109 */         e.end_method();
/*     */       }
/*     */ 
/* 112 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 116 */       return ReflectUtils.newInstance(type, ImmutableBean.OBJECT_CLASSES, new Object[] { this.bean });
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance)
/*     */     {
/* 121 */       return firstInstance(instance.getClass());
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.ImmutableBean
 * JD-Core Version:    0.6.0
 */
/*     */ package org.logicalcobwebs.cglib.reflect;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.Local;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.ProcessArrayCallback;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ public abstract class MulticastDelegate
/*     */   implements Cloneable
/*     */ {
/*  26 */   protected Object[] targets = new Object[0];
/*     */ 
/*     */   public List getTargets()
/*     */   {
/*  32 */     return new ArrayList(Arrays.asList(this.targets));
/*     */   }
/*     */   public abstract MulticastDelegate add(Object paramObject);
/*     */ 
/*     */   protected MulticastDelegate addHelper(Object target) {
/*  38 */     MulticastDelegate copy = newInstance();
/*  39 */     copy.targets = new Object[this.targets.length + 1];
/*  40 */     System.arraycopy(this.targets, 0, copy.targets, 0, this.targets.length);
/*  41 */     copy.targets[this.targets.length] = target;
/*  42 */     return copy;
/*     */   }
/*     */ 
/*     */   public MulticastDelegate remove(Object target) {
/*  46 */     for (int i = this.targets.length - 1; i >= 0; i--) {
/*  47 */       if (this.targets[i].equals(target)) {
/*  48 */         MulticastDelegate copy = newInstance();
/*  49 */         copy.targets = new Object[this.targets.length - 1];
/*  50 */         System.arraycopy(this.targets, 0, copy.targets, 0, i);
/*  51 */         System.arraycopy(this.targets, i + 1, copy.targets, i, this.targets.length - i - 1);
/*  52 */         return copy;
/*     */       }
/*     */     }
/*  55 */     return this;
/*     */   }
/*     */   public abstract MulticastDelegate newInstance();
/*     */ 
/*     */   public static MulticastDelegate create(Class iface) {
/*  61 */     Generator gen = new Generator();
/*  62 */     gen.setInterface(iface);
/*  63 */     return gen.create(); } 
/*  67 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(MulticastDelegate.class.getName());
/*  68 */     private static final Type MULTICAST_DELEGATE = TypeUtils.parseType("org.logicalcobwebs.cglib.reflect.MulticastDelegate");
/*     */ 
/*  70 */     private static final Signature NEW_INSTANCE = new Signature("newInstance", MULTICAST_DELEGATE, new Type[0]);
/*     */ 
/*  72 */     private static final Signature ADD_DELEGATE = new Signature("add", MULTICAST_DELEGATE, new Type[] { Constants.TYPE_OBJECT });
/*     */ 
/*  74 */     private static final Signature ADD_HELPER = new Signature("addHelper", MULTICAST_DELEGATE, new Type[] { Constants.TYPE_OBJECT });
/*     */     private Class iface;
/*     */ 
/*  80 */     public Generator() { super(); }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader()
/*     */     {
/*  84 */       return this.iface.getClassLoader();
/*     */     }
/*     */ 
/*     */     public void setInterface(Class iface) {
/*  88 */       this.iface = iface;
/*     */     }
/*     */ 
/*     */     public MulticastDelegate create() {
/*  92 */       setNamePrefix(MulticastDelegate.class.getName());
/*  93 */       return (MulticastDelegate)super.create(this.iface.getName());
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor cv) {
/*  97 */       MethodInfo method = ReflectUtils.getMethodInfo(ReflectUtils.findInterfaceMethod(this.iface));
/*     */ 
/*  99 */       ClassEmitter ce = new ClassEmitter(cv);
/* 100 */       ce.begin_class(46, 1, getClassName(), MULTICAST_DELEGATE, new Type[] { Type.getType(this.iface) }, "<generated>");
/*     */ 
/* 106 */       EmitUtils.null_constructor(ce);
/*     */ 
/* 109 */       emitProxy(ce, method);
/*     */ 
/* 112 */       CodeEmitter e = ce.begin_method(1, NEW_INSTANCE, null, null);
/* 113 */       e.new_instance_this();
/* 114 */       e.dup();
/* 115 */       e.invoke_constructor_this();
/* 116 */       e.return_value();
/* 117 */       e.end_method();
/*     */ 
/* 120 */       e = ce.begin_method(1, ADD_DELEGATE, null, null);
/* 121 */       e.load_this();
/* 122 */       e.load_arg(0);
/* 123 */       e.checkcast(Type.getType(this.iface));
/* 124 */       e.invoke_virtual_this(ADD_HELPER);
/* 125 */       e.return_value();
/* 126 */       e.end_method();
/*     */ 
/* 128 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     private void emitProxy(ClassEmitter ce, MethodInfo method) {
/* 132 */       CodeEmitter e = EmitUtils.begin_method(ce, method, 1);
/* 133 */       Type returnType = method.getSignature().getReturnType();
/* 134 */       boolean returns = returnType != Type.VOID_TYPE;
/* 135 */       Local result = null;
/* 136 */       if (returns) {
/* 137 */         result = e.make_local(returnType);
/* 138 */         e.zero_or_null(returnType);
/* 139 */         e.store_local(result);
/*     */       }
/* 141 */       e.load_this();
/* 142 */       e.super_getfield("targets", Constants.TYPE_OBJECT_ARRAY);
/* 143 */       Local result2 = result;
/* 144 */       EmitUtils.process_array(e, Constants.TYPE_OBJECT_ARRAY, new ProcessArrayCallback(e, method, returns, result2) {
/*     */         public void processElement(Type type) {
/* 146 */           this.val$e.checkcast(Type.getType(MulticastDelegate.Generator.this.iface));
/* 147 */           this.val$e.load_args();
/* 148 */           this.val$e.invoke(this.val$method);
/* 149 */           if (this.val$returns)
/* 150 */             this.val$e.store_local(this.val$result2);
/*     */         }
/*     */       });
/* 154 */       if (returns) {
/* 155 */         e.load_local(result);
/*     */       }
/* 157 */       e.return_value();
/* 158 */       e.end_method();
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type)
/*     */     {
/* 163 */       return ((MulticastDelegate)ReflectUtils.newInstance(type)).newInstance();
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 167 */       return ((MulticastDelegate)instance).newInstance();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.MulticastDelegate
 * JD-Core Version:    0.6.0
 */
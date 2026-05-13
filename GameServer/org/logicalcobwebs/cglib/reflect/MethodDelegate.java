/*     */ package org.logicalcobwebs.cglib.reflect;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator;
/*     */ import org.logicalcobwebs.cglib.core.AbstractClassGenerator.Source;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.ClassInfo;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.KeyFactory;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ public abstract class MethodDelegate
/*     */ {
/* 108 */   private static final MethodDelegateKey KEY_FACTORY = (MethodDelegateKey)KeyFactory.create(MethodDelegateKey.class, KeyFactory.CLASS_BY_NAME);
/*     */   protected Object target;
/*     */   protected String eqMethod;
/*     */ 
/*     */   public static MethodDelegate createStatic(Class targetClass, String methodName, Class iface)
/*     */   {
/* 119 */     Generator gen = new Generator();
/* 120 */     gen.setTargetClass(targetClass);
/* 121 */     gen.setMethodName(methodName);
/* 122 */     gen.setInterface(iface);
/* 123 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public static MethodDelegate create(Object target, String methodName, Class iface) {
/* 127 */     Generator gen = new Generator();
/* 128 */     gen.setTarget(target);
/* 129 */     gen.setMethodName(methodName);
/* 130 */     gen.setInterface(iface);
/* 131 */     return gen.create();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 135 */     MethodDelegate other = (MethodDelegate)obj;
/* 136 */     return (this.target == other.target) && (this.eqMethod.equals(other.eqMethod));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 140 */     return this.target.hashCode() ^ this.eqMethod.hashCode();
/*     */   }
/*     */ 
/*     */   public Object getTarget() {
/* 144 */     return this.target; } 
/*     */   public abstract MethodDelegate newInstance(Object paramObject);
/*     */ 
/* 150 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(MethodDelegate.class.getName());
/* 151 */     private static final Type METHOD_DELEGATE = TypeUtils.parseType("org.logicalcobwebs.cglib.reflect.MethodDelegate");
/*     */ 
/* 153 */     private static final Signature NEW_INSTANCE = new Signature("newInstance", METHOD_DELEGATE, new Type[] { Constants.TYPE_OBJECT });
/*     */     private Object target;
/*     */     private Class targetClass;
/*     */     private String methodName;
/*     */     private Class iface;
/*     */ 
/* 162 */     public Generator() { super(); }
/*     */ 
/*     */     public void setTarget(Object target)
/*     */     {
/* 166 */       this.target = target;
/* 167 */       this.targetClass = target.getClass();
/*     */     }
/*     */ 
/*     */     public void setTargetClass(Class targetClass) {
/* 171 */       this.targetClass = targetClass;
/*     */     }
/*     */ 
/*     */     public void setMethodName(String methodName) {
/* 175 */       this.methodName = methodName;
/*     */     }
/*     */ 
/*     */     public void setInterface(Class iface) {
/* 179 */       this.iface = iface;
/*     */     }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader() {
/* 183 */       return this.targetClass.getClassLoader();
/*     */     }
/*     */ 
/*     */     public MethodDelegate create() {
/* 187 */       setNamePrefix(this.targetClass.getName());
/* 188 */       Object key = MethodDelegate.KEY_FACTORY.newInstance(this.targetClass, this.methodName, this.iface);
/* 189 */       return (MethodDelegate)super.create(key);
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 193 */       return ((MethodDelegate)ReflectUtils.newInstance(type)).newInstance(this.target);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 197 */       return ((MethodDelegate)instance).newInstance(this.target);
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) throws NoSuchMethodException {
/* 201 */       Method proxy = ReflectUtils.findInterfaceMethod(this.iface);
/* 202 */       Method method = this.targetClass.getMethod(this.methodName, proxy.getParameterTypes());
/* 203 */       if (!proxy.getReturnType().isAssignableFrom(method.getReturnType())) {
/* 204 */         throw new IllegalArgumentException("incompatible return types");
/*     */       }
/*     */ 
/* 207 */       MethodInfo methodInfo = ReflectUtils.getMethodInfo(method);
/*     */ 
/* 209 */       boolean isStatic = TypeUtils.isStatic(methodInfo.getModifiers());
/* 210 */       if ((this.target == null ^ isStatic)) {
/* 211 */         throw new IllegalArgumentException("Static method " + (isStatic ? "not " : "") + "expected");
/*     */       }
/*     */ 
/* 214 */       ClassEmitter ce = new ClassEmitter(v);
/*     */ 
/* 216 */       ce.begin_class(46, 1, getClassName(), METHOD_DELEGATE, new Type[] { Type.getType(this.iface) }, "<generated>");
/*     */ 
/* 222 */       ce.declare_field(26, "eqMethod", Constants.TYPE_STRING, null, null);
/* 223 */       EmitUtils.null_constructor(ce);
/*     */ 
/* 226 */       MethodInfo proxied = ReflectUtils.getMethodInfo(this.iface.getDeclaredMethods()[0]);
/* 227 */       CodeEmitter e = EmitUtils.begin_method(ce, proxied, 1);
/* 228 */       e.load_this();
/* 229 */       e.super_getfield("target", Constants.TYPE_OBJECT);
/* 230 */       e.checkcast(methodInfo.getClassInfo().getType());
/* 231 */       e.load_args();
/* 232 */       e.invoke(methodInfo);
/* 233 */       e.return_value();
/* 234 */       e.end_method();
/*     */ 
/* 237 */       e = ce.begin_method(1, NEW_INSTANCE, null, null);
/* 238 */       e.new_instance_this();
/* 239 */       e.dup();
/* 240 */       e.dup2();
/* 241 */       e.invoke_constructor_this();
/* 242 */       e.getfield("eqMethod");
/* 243 */       e.super_putfield("eqMethod", Constants.TYPE_STRING);
/* 244 */       e.load_arg(0);
/* 245 */       e.super_putfield("target", Constants.TYPE_OBJECT);
/* 246 */       e.return_value();
/* 247 */       e.end_method();
/*     */ 
/* 250 */       e = ce.begin_static();
/* 251 */       e.push(methodInfo.getSignature().toString());
/* 252 */       e.putfield("eqMethod");
/* 253 */       e.return_value();
/* 254 */       e.end_method();
/*     */ 
/* 256 */       ce.end_class();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface MethodDelegateKey
/*     */   {
/*     */     public abstract Object newInstance(Class paramClass1, String paramString, Class paramClass2);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.MethodDelegate
 * JD-Core Version:    0.6.0
 */
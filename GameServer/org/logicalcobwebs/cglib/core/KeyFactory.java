/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public abstract class KeyFactory
/*     */ {
/*  54 */   private static final Signature GET_NAME = TypeUtils.parseSignature("String getName()");
/*     */ 
/*  56 */   private static final Signature GET_CLASS = TypeUtils.parseSignature("Class getClass()");
/*     */ 
/*  58 */   private static final Signature HASH_CODE = TypeUtils.parseSignature("int hashCode()");
/*     */ 
/*  60 */   private static final Signature EQUALS = TypeUtils.parseSignature("boolean equals(Object)");
/*     */ 
/*  62 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  64 */   private static final Signature APPEND_STRING = TypeUtils.parseSignature("StringBuffer append(String)");
/*     */ 
/*  66 */   private static final Type KEY_FACTORY = TypeUtils.parseType("org.logicalcobwebs.cglib.core.KeyFactory");
/*     */ 
/*  70 */   private static final int[] PRIMES = { 11, 73, 179, 331, 521, 787, 1213, 1823, 2609, 3691, 5189, 7247, 10037, 13931, 19289, 26627, 36683, 50441, 69403, 95401, 131129, 180179, 247501, 340057, 467063, 641371, 880603, 1209107, 1660097, 2279161, 3129011, 4295723, 5897291, 8095873, 11114263, 15257791, 20946017, 28754629, 39474179, 54189869, 74391461, 102123817, 140194277, 192456917, 264202273, 362693231, 497900099, 683510293, 938313161, 1288102441, 1768288259 };
/*     */ 
/*  86 */   public static final Customizer CLASS_BY_NAME = new Customizer() {
/*     */     public void customize(CodeEmitter e, Type type) {
/*  88 */       if (type.equals(Constants.TYPE_CLASS))
/*  89 */         e.invoke_virtual(Constants.TYPE_CLASS, KeyFactory.GET_NAME);
/*     */     }
/*  86 */   };
/*     */ 
/*  94 */   public static final Customizer OBJECT_BY_CLASS = new Customizer() {
/*     */     public void customize(CodeEmitter e, Type type) {
/*  96 */       e.invoke_virtual(Constants.TYPE_OBJECT, KeyFactory.GET_CLASS);
/*     */     }
/*  94 */   };
/*     */ 
/*     */   public static KeyFactory create(Class keyInterface)
/*     */   {
/* 104 */     return create(keyInterface, null);
/*     */   }
/*     */ 
/*     */   public static KeyFactory create(Class keyInterface, Customizer customizer) {
/* 108 */     return create(keyInterface.getClassLoader(), keyInterface, customizer);
/*     */   }
/*     */ 
/*     */   public static KeyFactory create(ClassLoader loader, Class keyInterface, Customizer customizer) {
/* 112 */     Generator gen = new Generator();
/* 113 */     gen.setInterface(keyInterface);
/* 114 */     gen.setCustomizer(customizer);
/* 115 */     gen.setClassLoader(loader);
/* 116 */     gen.setAttemptLoad(true);
/* 117 */     return gen.create(); } 
/* 121 */   public static class Generator extends AbstractClassGenerator { private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source(KeyFactory.class.getName());
/*     */     private Class keyInterface;
/*     */     private Customizer customizer;
/*     */     private int constant;
/*     */     private int multiplier;
/*     */ 
/* 128 */     public Generator() { super(); }
/*     */ 
/*     */     protected ClassLoader getDefaultClassLoader()
/*     */     {
/* 132 */       return this.keyInterface.getClassLoader();
/*     */     }
/*     */ 
/*     */     public void setCustomizer(Customizer customizer) {
/* 136 */       this.customizer = customizer;
/*     */     }
/*     */ 
/*     */     public void setInterface(Class keyInterface) {
/* 140 */       this.keyInterface = keyInterface;
/*     */     }
/*     */ 
/*     */     public KeyFactory create() {
/* 144 */       setNamePrefix(this.keyInterface.getName());
/* 145 */       return (KeyFactory)super.create(this.keyInterface.getName());
/*     */     }
/*     */ 
/*     */     public void setHashConstant(int constant) {
/* 149 */       this.constant = constant;
/*     */     }
/*     */ 
/*     */     public void setHashMultiplier(int multiplier) {
/* 153 */       this.multiplier = multiplier;
/*     */     }
/*     */ 
/*     */     protected Object firstInstance(Class type) {
/* 157 */       return ReflectUtils.newInstance(type);
/*     */     }
/*     */ 
/*     */     protected Object nextInstance(Object instance) {
/* 161 */       return instance;
/*     */     }
/*     */ 
/*     */     public void generateClass(ClassVisitor v) {
/* 165 */       ClassEmitter ce = new ClassEmitter(v);
/*     */ 
/* 167 */       Method newInstance = ReflectUtils.findNewInstance(this.keyInterface);
/* 168 */       if (!newInstance.getReturnType().equals(Object.class)) {
/* 169 */         throw new IllegalArgumentException("newInstance method must return Object");
/*     */       }
/*     */ 
/* 172 */       Type[] parameterTypes = TypeUtils.getTypes(newInstance.getParameterTypes());
/* 173 */       ce.begin_class(46, 1, getClassName(), KeyFactory.KEY_FACTORY, new Type[] { Type.getType(this.keyInterface) }, "<generated>");
/*     */ 
/* 179 */       EmitUtils.null_constructor(ce);
/* 180 */       EmitUtils.factory_method(ce, ReflectUtils.getSignature(newInstance));
/*     */ 
/* 182 */       int seed = 0;
/* 183 */       CodeEmitter e = ce.begin_method(1, TypeUtils.parseConstructor(parameterTypes), null, null);
/*     */ 
/* 187 */       e.load_this();
/* 188 */       e.super_invoke_constructor();
/* 189 */       e.load_this();
/* 190 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 191 */         seed += parameterTypes[i].hashCode();
/* 192 */         ce.declare_field(18, getFieldName(i), parameterTypes[i], null, null);
/*     */ 
/* 197 */         e.dup();
/* 198 */         e.load_arg(i);
/* 199 */         e.putfield(getFieldName(i));
/*     */       }
/* 201 */       e.return_value();
/* 202 */       e.end_method();
/*     */ 
/* 205 */       e = ce.begin_method(1, KeyFactory.HASH_CODE, null, null);
/* 206 */       int hc = this.constant != 0 ? this.constant : KeyFactory.PRIMES[(java.lang.Math.abs(seed) % KeyFactory.PRIMES.length)];
/* 207 */       int hm = this.multiplier != 0 ? this.multiplier : KeyFactory.PRIMES[(java.lang.Math.abs(seed * 13) % KeyFactory.PRIMES.length)];
/* 208 */       e.push(hc);
/* 209 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 210 */         e.load_this();
/* 211 */         e.getfield(getFieldName(i));
/* 212 */         EmitUtils.hash_code(e, parameterTypes[i], hm, this.customizer);
/*     */       }
/* 214 */       e.return_value();
/* 215 */       e.end_method();
/*     */ 
/* 218 */       e = ce.begin_method(1, KeyFactory.EQUALS, null, null);
/* 219 */       Label fail = e.make_label();
/* 220 */       e.load_arg(0);
/* 221 */       e.instance_of_this();
/* 222 */       e.if_jump(153, fail);
/* 223 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 224 */         e.load_this();
/* 225 */         e.getfield(getFieldName(i));
/* 226 */         e.load_arg(0);
/* 227 */         e.checkcast_this();
/* 228 */         e.getfield(getFieldName(i));
/* 229 */         EmitUtils.not_equals(e, parameterTypes[i], fail, this.customizer);
/*     */       }
/* 231 */       e.push(1);
/* 232 */       e.return_value();
/* 233 */       e.mark(fail);
/* 234 */       e.push(0);
/* 235 */       e.return_value();
/* 236 */       e.end_method();
/*     */ 
/* 239 */       e = ce.begin_method(1, KeyFactory.TO_STRING, null, null);
/* 240 */       e.new_instance(Constants.TYPE_STRING_BUFFER);
/* 241 */       e.dup();
/* 242 */       e.invoke_constructor(Constants.TYPE_STRING_BUFFER);
/* 243 */       for (int i = 0; i < parameterTypes.length; i++) {
/* 244 */         if (i > 0) {
/* 245 */           e.push(", ");
/* 246 */           e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.APPEND_STRING);
/*     */         }
/* 248 */         e.load_this();
/* 249 */         e.getfield(getFieldName(i));
/* 250 */         EmitUtils.append_string(e, parameterTypes[i], EmitUtils.DEFAULT_DELIMITERS, this.customizer);
/*     */       }
/* 252 */       e.invoke_virtual(Constants.TYPE_STRING_BUFFER, KeyFactory.TO_STRING);
/* 253 */       e.return_value();
/* 254 */       e.end_method();
/*     */ 
/* 256 */       ce.end_class();
/*     */     }
/*     */ 
/*     */     private String getFieldName(int arg) {
/* 260 */       return "FIELD_" + arg;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.KeyFactory
 * JD-Core Version:    0.6.0
 */
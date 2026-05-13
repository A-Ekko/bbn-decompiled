/*     */ package org.logicalcobwebs.cglib.proxy;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.Local;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.ObjectSwitchCallback;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ 
/*     */ class MethodInterceptorGenerator
/*     */   implements CallbackGenerator
/*     */ {
/*  26 */   public static final MethodInterceptorGenerator INSTANCE = new MethodInterceptorGenerator();
/*     */   static final String EMPTY_ARGS_NAME = "CGLIB$emptyArgs";
/*     */   static final String FIND_PROXY_NAME = "CGLIB$findMethodProxy";
/*  30 */   static final Class[] FIND_PROXY_TYPES = { Signature.class };
/*     */ 
/*  32 */   private static final Type ABSTRACT_METHOD_ERROR = TypeUtils.parseType("AbstractMethodError");
/*     */ 
/*  34 */   private static final Type METHOD = TypeUtils.parseType("java.lang.reflect.Method");
/*     */ 
/*  36 */   private static final Type METHOD_PROXY = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.MethodProxy");
/*     */ 
/*  38 */   private static final Type METHOD_INTERCEPTOR = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.MethodInterceptor");
/*     */ 
/*  40 */   private static final Signature GET_DECLARING_CLASS = TypeUtils.parseSignature("Class getDeclaringClass()");
/*     */ 
/*  42 */   private static final Signature GET_CLASS_LOADER = TypeUtils.parseSignature("ClassLoader getClassLoader()");
/*     */ 
/*  44 */   private static final Signature MAKE_PROXY = new Signature("create", METHOD_PROXY, new Type[] { Constants.TYPE_CLASS_LOADER, Constants.TYPE_CLASS, Constants.TYPE_CLASS, Constants.TYPE_STRING, Constants.TYPE_STRING, Constants.TYPE_STRING });
/*     */ 
/*  53 */   private static final Signature INTERCEPT = new Signature("intercept", Constants.TYPE_OBJECT, new Type[] { Constants.TYPE_OBJECT, METHOD, Constants.TYPE_OBJECT_ARRAY, METHOD_PROXY });
/*     */ 
/*  60 */   private static final Signature FIND_PROXY = new Signature("CGLIB$findMethodProxy", METHOD_PROXY, new Type[] { Constants.TYPE_SIGNATURE });
/*     */ 
/*  62 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*     */   private String getMethodField(Signature impl)
/*     */   {
/*  66 */     return impl.getName() + "$Method";
/*     */   }
/*     */   private String getMethodProxyField(Signature impl) {
/*  69 */     return impl.getName() + "$Proxy";
/*     */   }
/*     */ 
/*     */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
/*  73 */     Map sigMap = new HashMap();
/*  74 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/*  75 */       MethodInfo method = (MethodInfo)it.next();
/*  76 */       Signature impl = context.getImplSignature(method);
/*     */ 
/*  78 */       String methodField = getMethodField(impl);
/*  79 */       String methodProxyField = getMethodProxyField(impl);
/*     */ 
/*  81 */       sigMap.put(method.getSignature().toString(), methodProxyField);
/*  82 */       ce.declare_field(26, methodField, METHOD, null, null);
/*  83 */       ce.declare_field(26, methodProxyField, METHOD_PROXY, null, null);
/*  84 */       ce.declare_field(26, "CGLIB$emptyArgs", Constants.TYPE_OBJECT_ARRAY, null, null);
/*     */ 
/*  88 */       CodeEmitter e = ce.begin_method(16, impl, method.getExceptionTypes(), null);
/*     */ 
/*  92 */       if (TypeUtils.isAbstract(method.getModifiers())) {
/*  93 */         e.throw_exception(ABSTRACT_METHOD_ERROR, method.toString() + " is abstract");
/*     */       } else {
/*  95 */         e.load_this();
/*  96 */         e.load_args();
/*  97 */         e.super_invoke(method.getSignature());
/*     */       }
/*  99 */       e.return_value();
/* 100 */       e.end_method();
/*     */ 
/* 103 */       e = context.beginMethod(ce, method);
/* 104 */       Label nullInterceptor = e.make_label();
/* 105 */       context.emitCallback(e, context.getIndex(method));
/* 106 */       e.dup();
/* 107 */       e.ifnull(nullInterceptor);
/*     */ 
/* 109 */       e.load_this();
/* 110 */       e.getfield(methodField);
/*     */ 
/* 112 */       if (method.getSignature().getArgumentTypes().length == 0)
/* 113 */         e.getfield("CGLIB$emptyArgs");
/*     */       else {
/* 115 */         e.create_arg_array();
/*     */       }
/*     */ 
/* 118 */       e.getfield(methodProxyField);
/* 119 */       e.invoke_interface(METHOD_INTERCEPTOR, INTERCEPT);
/* 120 */       e.unbox_or_zero(method.getSignature().getReturnType());
/* 121 */       e.return_value();
/*     */ 
/* 123 */       e.mark(nullInterceptor);
/* 124 */       e.load_this();
/* 125 */       e.load_args();
/* 126 */       e.super_invoke(method.getSignature());
/* 127 */       e.return_value();
/* 128 */       e.end_method();
/*     */     }
/* 130 */     generateFindProxy(ce, sigMap);
/*     */   }
/*     */ 
/*     */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*     */   {
/* 144 */     Local thisclass = e.make_local();
/* 145 */     EmitUtils.load_class_this(e);
/* 146 */     e.dup();
/* 147 */     e.store_local(thisclass);
/* 148 */     e.invoke_virtual(Constants.TYPE_CLASS, GET_CLASS_LOADER);
/*     */ 
/* 150 */     e.push(0);
/* 151 */     e.newarray();
/* 152 */     e.putfield("CGLIB$emptyArgs");
/*     */ 
/* 154 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 155 */       e.dup();
/* 156 */       MethodInfo method = (MethodInfo)it.next();
/* 157 */       Signature impl = context.getImplSignature(method);
/* 158 */       EmitUtils.load_method(e, method);
/* 159 */       e.dup();
/* 160 */       e.putfield(getMethodField(impl));
/*     */ 
/* 162 */       Signature sig = method.getSignature();
/* 163 */       e.invoke_virtual(METHOD, GET_DECLARING_CLASS);
/* 164 */       e.load_local(thisclass);
/* 165 */       e.push(sig.getDescriptor());
/* 166 */       e.push(sig.getName());
/* 167 */       e.push(impl.getName());
/* 168 */       e.invoke_static(METHOD_PROXY, MAKE_PROXY);
/* 169 */       e.putfield(getMethodProxyField(impl));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateFindProxy(ClassEmitter ce, Map sigMap) {
/* 174 */     CodeEmitter e = ce.begin_method(9, FIND_PROXY, null, null);
/*     */ 
/* 178 */     e.load_arg(0);
/* 179 */     e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
/* 180 */     ObjectSwitchCallback callback = new ObjectSwitchCallback(e, sigMap) {
/*     */       public void processCase(Object key, Label end) {
/* 182 */         this.val$e.getfield((String)this.val$sigMap.get(key));
/* 183 */         this.val$e.return_value();
/*     */       }
/*     */       public void processDefault() {
/* 186 */         this.val$e.aconst_null();
/* 187 */         this.val$e.return_value();
/*     */       }
/*     */     };
/* 190 */     EmitUtils.string_switch(e, (String[])(String[])sigMap.keySet().toArray(new String[0]), 1, callback);
/*     */ 
/* 194 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MethodInterceptorGenerator
 * JD-Core Version:    0.6.0
 */
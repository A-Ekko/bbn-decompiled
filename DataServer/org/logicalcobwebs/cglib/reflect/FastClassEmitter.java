/*     */ package org.logicalcobwebs.cglib.reflect;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.Block;
/*     */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CollectionUtils;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.DuplicatesPredicate;
/*     */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*     */ import org.logicalcobwebs.cglib.core.MethodInfoTransformer;
/*     */ import org.logicalcobwebs.cglib.core.ObjectSwitchCallback;
/*     */ import org.logicalcobwebs.cglib.core.ProcessSwitchCallback;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.Transformer;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ import org.logicalcobwebs.cglib.core.VisibilityPredicate;
/*     */ 
/*     */ class FastClassEmitter extends ClassEmitter
/*     */ {
/*  26 */   private static final Signature CSTRUCT_CLASS = TypeUtils.parseConstructor("Class");
/*     */ 
/*  28 */   private static final Signature METHOD_GET_INDEX = TypeUtils.parseSignature("int getIndex(String, Class[])");
/*     */ 
/*  30 */   private static final Signature SIGNATURE_GET_INDEX = new Signature("getIndex", Type.INT_TYPE, new Type[] { Constants.TYPE_SIGNATURE });
/*     */ 
/*  32 */   private static final Signature TO_STRING = TypeUtils.parseSignature("String toString()");
/*     */ 
/*  34 */   private static final Signature CONSTRUCTOR_GET_INDEX = TypeUtils.parseSignature("int getIndex(Class[])");
/*     */ 
/*  36 */   private static final Signature INVOKE = TypeUtils.parseSignature("Object invoke(int, Object, Object[])");
/*     */ 
/*  38 */   private static final Signature NEW_INSTANCE = TypeUtils.parseSignature("Object newInstance(int, Object[])");
/*     */ 
/*  40 */   private static final Signature GET_MAX_INDEX = TypeUtils.parseSignature("int getMaxIndex()");
/*     */ 
/*  42 */   private static final Signature GET_SIGNATURE_WITHOUT_RETURN_TYPE = TypeUtils.parseSignature("String getSignatureWithoutReturnType(String, Class[])");
/*     */ 
/*  44 */   private static final Type FAST_CLASS = TypeUtils.parseType("org.logicalcobwebs.cglib.reflect.FastClass");
/*     */ 
/*  46 */   private static final Type ILLEGAL_ARGUMENT_EXCEPTION = TypeUtils.parseType("IllegalArgumentException");
/*     */ 
/*  48 */   private static final Type INVOCATION_TARGET_EXCEPTION = TypeUtils.parseType("java.lang.reflect.InvocationTargetException");
/*     */ 
/*  50 */   private static final Type[] INVOCATION_TARGET_EXCEPTION_ARRAY = { INVOCATION_TARGET_EXCEPTION };
/*     */   private static final int TOO_MANY_METHODS = 100;
/*     */ 
/*     */   public FastClassEmitter(ClassVisitor v, String className, Class type)
/*     */   {
/*  53 */     super(v);
/*     */ 
/*  55 */     begin_class(46, 1, className, FAST_CLASS, null, "<generated>");
/*     */ 
/*  58 */     CodeEmitter e = begin_method(1, CSTRUCT_CLASS, null, null);
/*  59 */     e.load_this();
/*  60 */     e.load_args();
/*  61 */     e.super_invoke_constructor(CSTRUCT_CLASS);
/*  62 */     e.return_value();
/*  63 */     e.end_method();
/*     */ 
/*  65 */     VisibilityPredicate vp = new VisibilityPredicate(type, false);
/*  66 */     List methods = ReflectUtils.addAllMethods(type, new ArrayList());
/*  67 */     CollectionUtils.filter(methods, vp);
/*  68 */     CollectionUtils.filter(methods, new DuplicatesPredicate());
/*  69 */     List constructors = new ArrayList(Arrays.asList(type.getDeclaredConstructors()));
/*  70 */     CollectionUtils.filter(constructors, vp);
/*     */ 
/*  73 */     emitIndexBySignature(methods);
/*     */ 
/*  76 */     emitIndexByClassArray(methods);
/*     */ 
/*  79 */     e = begin_method(1, CONSTRUCTOR_GET_INDEX, null, null);
/*  80 */     e.load_args();
/*  81 */     List info = CollectionUtils.transform(constructors, MethodInfoTransformer.getInstance());
/*  82 */     EmitUtils.constructor_switch(e, info, new GetIndexCallback(e, info));
/*  83 */     e.end_method();
/*     */ 
/*  86 */     e = begin_method(1, INVOKE, INVOCATION_TARGET_EXCEPTION_ARRAY, null);
/*  87 */     e.load_arg(1);
/*  88 */     e.checkcast(Type.getType(type));
/*  89 */     e.load_arg(0);
/*  90 */     invokeSwitchHelper(e, methods, 2);
/*  91 */     e.end_method();
/*     */ 
/*  94 */     e = begin_method(1, NEW_INSTANCE, INVOCATION_TARGET_EXCEPTION_ARRAY, null);
/*  95 */     e.new_instance(Type.getType(type));
/*  96 */     e.dup();
/*  97 */     e.load_arg(0);
/*  98 */     invokeSwitchHelper(e, constructors, 1);
/*  99 */     e.end_method();
/*     */ 
/* 102 */     e = begin_method(1, GET_MAX_INDEX, null, null);
/* 103 */     e.push(methods.size() - 1);
/* 104 */     e.return_value();
/* 105 */     e.end_method();
/*     */ 
/* 107 */     end_class();
/*     */   }
/*     */ 
/*     */   private void emitIndexBySignature(List methods)
/*     */   {
/* 112 */     CodeEmitter e = begin_method(1, SIGNATURE_GET_INDEX, null, null);
/* 113 */     List signatures = CollectionUtils.transform(methods, new Transformer() {
/*     */       public Object transform(Object obj) {
/* 115 */         return ReflectUtils.getSignature((Method)obj).toString();
/*     */       }
/*     */     });
/* 118 */     e.load_arg(0);
/* 119 */     e.invoke_virtual(Constants.TYPE_OBJECT, TO_STRING);
/* 120 */     signatureSwitchHelper(e, signatures);
/* 121 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void emitIndexByClassArray(List methods)
/*     */   {
/* 126 */     CodeEmitter e = begin_method(1, METHOD_GET_INDEX, null, null);
/* 127 */     if (methods.size() > 100)
/*     */     {
/* 129 */       List signatures = CollectionUtils.transform(methods, new Transformer() {
/*     */         public Object transform(Object obj) {
/* 131 */           String s = ReflectUtils.getSignature((Method)obj).toString();
/* 132 */           return s.substring(0, s.lastIndexOf(')') + 1);
/*     */         }
/*     */       });
/* 135 */       e.load_args();
/* 136 */       e.invoke_static(FAST_CLASS, GET_SIGNATURE_WITHOUT_RETURN_TYPE);
/* 137 */       signatureSwitchHelper(e, signatures);
/*     */     } else {
/* 139 */       e.load_args();
/* 140 */       List info = CollectionUtils.transform(methods, MethodInfoTransformer.getInstance());
/* 141 */       EmitUtils.method_switch(e, info, new GetIndexCallback(e, info));
/*     */     }
/* 143 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void signatureSwitchHelper(CodeEmitter e, List signatures) {
/* 147 */     ObjectSwitchCallback callback = new ObjectSwitchCallback(e, signatures)
/*     */     {
/*     */       public void processCase(Object key, Label end) {
/* 150 */         this.val$e.push(this.val$signatures.indexOf(key));
/* 151 */         this.val$e.return_value();
/*     */       }
/*     */       public void processDefault() {
/* 154 */         this.val$e.push(-1);
/* 155 */         this.val$e.return_value();
/*     */       }
/*     */     };
/* 158 */     EmitUtils.string_switch(e, (String[])(String[])signatures.toArray(new String[signatures.size()]), 1, callback);
/*     */   }
/*     */ 
/*     */   private static void invokeSwitchHelper(CodeEmitter e, List members, int arg)
/*     */   {
/* 165 */     List info = CollectionUtils.transform(members, MethodInfoTransformer.getInstance());
/* 166 */     Label illegalArg = e.make_label();
/* 167 */     Block block = e.begin_block();
/* 168 */     e.process_switch(getIntRange(info.size()), new ProcessSwitchCallback(info, e, arg, illegalArg) {
/*     */       public void processCase(int key, Label end) {
/* 170 */         MethodInfo method = (MethodInfo)this.val$info.get(key);
/* 171 */         Type[] types = method.getSignature().getArgumentTypes();
/* 172 */         for (int i = 0; i < types.length; i++) {
/* 173 */           this.val$e.load_arg(this.val$arg);
/* 174 */           this.val$e.aaload(i);
/* 175 */           this.val$e.unbox(types[i]);
/*     */         }
/* 177 */         this.val$e.invoke(method);
/* 178 */         if (!TypeUtils.isConstructor(method)) {
/* 179 */           this.val$e.box(method.getSignature().getReturnType());
/*     */         }
/* 181 */         this.val$e.return_value();
/*     */       }
/*     */       public void processDefault() {
/* 184 */         this.val$e.goTo(this.val$illegalArg);
/*     */       }
/*     */     });
/* 187 */     block.end();
/* 188 */     EmitUtils.wrap_throwable(block, INVOCATION_TARGET_EXCEPTION);
/* 189 */     e.mark(illegalArg);
/* 190 */     e.throw_exception(ILLEGAL_ARGUMENT_EXCEPTION, "Cannot find matching method/constructor");
/*     */   }
/*     */ 
/*     */   private static int[] getIntRange(int length)
/*     */   {
/* 217 */     int[] range = new int[length];
/* 218 */     for (int i = 0; i < length; i++) {
/* 219 */       range[i] = i;
/*     */     }
/* 221 */     return range;
/*     */   }
/*     */ 
/*     */   private static class GetIndexCallback
/*     */     implements ObjectSwitchCallback
/*     */   {
/*     */     private CodeEmitter e;
/* 195 */     private Map indexes = new HashMap();
/*     */ 
/*     */     public GetIndexCallback(CodeEmitter e, List methods) {
/* 198 */       this.e = e;
/* 199 */       int index = 0;
/* 200 */       for (Iterator it = methods.iterator(); it.hasNext(); )
/* 201 */         this.indexes.put(it.next(), new Integer(index++));
/*     */     }
/*     */ 
/*     */     public void processCase(Object key, Label end)
/*     */     {
/* 206 */       this.e.push(((Integer)this.indexes.get(key)).intValue());
/* 207 */       this.e.return_value();
/*     */     }
/*     */ 
/*     */     public void processDefault() {
/* 211 */       this.e.push(-1);
/* 212 */       this.e.return_value();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.reflect.FastClassEmitter
 * JD-Core Version:    0.6.0
 */
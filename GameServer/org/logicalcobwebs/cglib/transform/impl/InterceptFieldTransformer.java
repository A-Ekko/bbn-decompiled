/*     */ package org.logicalcobwebs.cglib.transform.impl;
/*     */ 
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.Constants;
/*     */ import org.logicalcobwebs.cglib.core.Local;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*     */ 
/*     */ public class InterceptFieldTransformer extends ClassEmitterTransformer
/*     */ {
/*     */   private static final String CALLBACK_FIELD = "$CGLIB_READ_WRITE_CALLBACK";
/*  32 */   private static final Type CALLBACK = TypeUtils.parseType("org.logicalcobwebs.cglib.transform.impl.InterceptFieldCallback");
/*     */ 
/*  34 */   private static final Type ENABLED = TypeUtils.parseType("org.logicalcobwebs.cglib.transform.impl.InterceptFieldEnabled");
/*     */ 
/*  36 */   private static final Signature ENABLED_SET = new Signature("setInterceptFieldCallback", Type.VOID_TYPE, new Type[] { CALLBACK });
/*     */ 
/*  38 */   private static final Signature ENABLED_GET = new Signature("getInterceptFieldCallback", CALLBACK, new Type[0]);
/*     */   private InterceptFieldFilter filter;
/*     */ 
/*     */   public InterceptFieldTransformer(InterceptFieldFilter filter)
/*     */   {
/*  44 */     this.filter = filter;
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile) {
/*  48 */     if (!TypeUtils.isInterface(access)) {
/*  49 */       super.begin_class(version, access, className, superType, TypeUtils.add(interfaces, ENABLED), sourceFile);
/*     */ 
/*  51 */       super.declare_field(130, "$CGLIB_READ_WRITE_CALLBACK", CALLBACK, null, null);
/*     */ 
/*  58 */       CodeEmitter e = super.begin_method(1, ENABLED_GET, null, null);
/*  59 */       e.load_this();
/*  60 */       e.getfield("$CGLIB_READ_WRITE_CALLBACK");
/*  61 */       e.return_value();
/*  62 */       e.end_method();
/*     */ 
/*  64 */       e = super.begin_method(1, ENABLED_SET, null, null);
/*  65 */       e.load_this();
/*  66 */       e.load_arg(0);
/*  67 */       e.putfield("$CGLIB_READ_WRITE_CALLBACK");
/*  68 */       e.return_value();
/*  69 */       e.end_method();
/*     */     } else {
/*  71 */       super.begin_class(version, access, className, superType, interfaces, sourceFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void declare_field(int access, String name, Type type, Object value, Attribute attrs) {
/*  76 */     super.declare_field(access, name, type, value, attrs);
/*  77 */     if (!TypeUtils.isStatic(access)) {
/*  78 */       if (this.filter.acceptRead(getClassType(), name)) {
/*  79 */         addReadMethod(name, type);
/*     */       }
/*  81 */       if (this.filter.acceptWrite(getClassType(), name))
/*  82 */         addWriteMethod(name, type);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addReadMethod(String name, Type type)
/*     */   {
/*  88 */     CodeEmitter e = super.begin_method(1, readMethodSig(name, type.getDescriptor()), null, null);
/*     */ 
/*  92 */     e.load_this();
/*  93 */     e.getfield(name);
/*  94 */     e.load_this();
/*  95 */     e.invoke_interface(ENABLED, ENABLED_GET);
/*  96 */     Label intercept = e.make_label();
/*  97 */     e.ifnonnull(intercept);
/*  98 */     e.return_value();
/*     */ 
/* 100 */     e.mark(intercept);
/* 101 */     Local result = e.make_local(type);
/* 102 */     e.store_local(result);
/* 103 */     e.load_this();
/* 104 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 105 */     e.load_this();
/* 106 */     e.push(name);
/* 107 */     e.load_local(result);
/* 108 */     e.invoke_interface(CALLBACK, readCallbackSig(type));
/* 109 */     if (!TypeUtils.isPrimitive(type)) {
/* 110 */       e.checkcast(type);
/*     */     }
/* 112 */     e.return_value();
/* 113 */     e.end_method();
/*     */   }
/*     */ 
/*     */   private void addWriteMethod(String name, Type type) {
/* 117 */     CodeEmitter e = super.begin_method(1, writeMethodSig(name, type.getDescriptor()), null, null);
/*     */ 
/* 121 */     e.load_this();
/* 122 */     e.dup();
/* 123 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 124 */     Label skip = e.make_label();
/* 125 */     e.ifnull(skip);
/*     */ 
/* 127 */     e.load_this();
/* 128 */     e.invoke_interface(ENABLED, ENABLED_GET);
/* 129 */     e.load_this();
/* 130 */     e.push(name);
/* 131 */     e.load_this();
/* 132 */     e.getfield(name);
/* 133 */     e.load_arg(0);
/* 134 */     e.invoke_interface(CALLBACK, writeCallbackSig(type));
/* 135 */     if (!TypeUtils.isPrimitive(type)) {
/* 136 */       e.checkcast(type);
/*     */     }
/* 138 */     Label go = e.make_label();
/* 139 */     e.goTo(go);
/* 140 */     e.mark(skip);
/* 141 */     e.load_arg(0);
/* 142 */     e.mark(go);
/* 143 */     e.putfield(name);
/* 144 */     e.return_value();
/* 145 */     e.end_method();
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions, Attribute attrs) {
/* 149 */     return new CodeEmitter(super.begin_method(access, sig, exceptions, attrs)) {
/*     */       public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/* 151 */         Type towner = TypeUtils.fromInternalName(owner);
/* 152 */         switch (opcode) {
/*     */         case 180:
/* 154 */           if (!InterceptFieldTransformer.this.filter.acceptRead(towner, name)) break;
/* 155 */           helper(towner, InterceptFieldTransformer.access$100(name, desc));
/* 156 */           return;
/*     */         case 181:
/* 160 */           if (!InterceptFieldTransformer.this.filter.acceptWrite(towner, name)) break;
/* 161 */           helper(towner, InterceptFieldTransformer.access$200(name, desc));
/* 162 */           return;
/*     */         }
/*     */ 
/* 166 */         super.visitFieldInsn(opcode, owner, name, desc);
/*     */       }
/*     */ 
/*     */       private void helper(Type owner, Signature sig) {
/* 170 */         invoke_virtual(owner, sig);
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   private static Signature readMethodSig(String name, String desc) {
/* 176 */     return new Signature("$cglib_read_" + name, "()" + desc);
/*     */   }
/*     */ 
/*     */   private static Signature writeMethodSig(String name, String desc) {
/* 180 */     return new Signature("$cglib_write_" + name, "(" + desc + ")V");
/*     */   }
/*     */ 
/*     */   private static Signature readCallbackSig(Type type) {
/* 184 */     Type remap = remap(type);
/* 185 */     return new Signature("read" + callbackName(remap), remap, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap });
/*     */   }
/*     */ 
/*     */   private static Signature writeCallbackSig(Type type)
/*     */   {
/* 193 */     Type remap = remap(type);
/* 194 */     return new Signature("write" + callbackName(remap), remap, new Type[] { Constants.TYPE_OBJECT, Constants.TYPE_STRING, remap, remap });
/*     */   }
/*     */ 
/*     */   private static Type remap(Type type)
/*     */   {
/* 203 */     switch (type.getSort()) {
/*     */     case 9:
/*     */     case 10:
/* 206 */       return Constants.TYPE_OBJECT;
/*     */     }
/* 208 */     return type;
/*     */   }
/*     */ 
/*     */   private static String callbackName(Type type)
/*     */   {
/* 213 */     return type == Constants.TYPE_OBJECT ? "Object" : TypeUtils.upperFirst(TypeUtils.getClassName(type));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.InterceptFieldTransformer
 * JD-Core Version:    0.6.0
 */
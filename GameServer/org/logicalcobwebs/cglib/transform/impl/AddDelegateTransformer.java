/*     */ package org.logicalcobwebs.cglib.transform.impl;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*     */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*     */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*     */ import org.logicalcobwebs.cglib.core.Signature;
/*     */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*     */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*     */ 
/*     */ public class AddDelegateTransformer extends ClassEmitterTransformer
/*     */ {
/*     */   private static final String DELEGATE = "$CGLIB_DELEGATE";
/*  30 */   private static final Signature CSTRUCT_OBJECT = TypeUtils.parseSignature("void <init>(Object)");
/*     */   private Class[] delegateIf;
/*     */   private Class delegateImpl;
/*     */   private Type delegateType;
/*     */ 
/*     */   public AddDelegateTransformer(Class[] delegateIf, Class delegateImpl)
/*     */   {
/*     */     try
/*     */     {
/*  40 */       delegateImpl.getConstructor(new Class[] { Object.class });
/*  41 */       this.delegateIf = delegateIf;
/*  42 */       this.delegateImpl = delegateImpl;
/*  43 */       this.delegateType = Type.getType(delegateImpl);
/*     */     } catch (NoSuchMethodException e) {
/*  45 */       throw new CodeGenerationException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void begin_class(int version, int access, String className, Type superType, Type[] interfaces, String sourceFile)
/*     */   {
/*  51 */     if (!TypeUtils.isInterface(access))
/*     */     {
/*  53 */       Type[] all = TypeUtils.add(interfaces, TypeUtils.getTypes(this.delegateIf));
/*  54 */       super.begin_class(version, access, className, superType, all, sourceFile);
/*     */ 
/*  56 */       declare_field(130, "$CGLIB_DELEGATE", this.delegateType, null, null);
/*     */ 
/*  61 */       for (int i = 0; i < this.delegateIf.length; i++) {
/*  62 */         Method[] methods = this.delegateIf[i].getMethods();
/*  63 */         for (int j = 0; j < methods.length; j++)
/*  64 */           if (Modifier.isAbstract(methods[j].getModifiers()))
/*  65 */             addDelegate(methods[j]);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  70 */       super.begin_class(version, access, className, superType, interfaces, sourceFile);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions, Attribute attrs) {
/*  75 */     CodeEmitter e = super.begin_method(access, sig, exceptions, attrs);
/*  76 */     if (sig.getName().equals("<init>"))
/*  77 */       return new CodeEmitter(e) {
/*  78 */         private boolean transformInit = true;
/*     */ 
/*  80 */         public void visitMethodInsn(int opcode, String owner, String name, String desc) { super.visitMethodInsn(opcode, owner, name, desc);
/*  81 */           if ((this.transformInit) && (opcode == 183)) {
/*  82 */             load_this();
/*  83 */             new_instance(AddDelegateTransformer.this.delegateType);
/*  84 */             dup();
/*  85 */             load_this();
/*  86 */             invoke_constructor(AddDelegateTransformer.this.delegateType, AddDelegateTransformer.CSTRUCT_OBJECT);
/*  87 */             putfield("$CGLIB_DELEGATE");
/*  88 */             this.transformInit = false;
/*     */           }
/*     */         }
/*     */       };
/*  93 */     return e;
/*     */   }
/*     */ 
/*     */   private void addDelegate(Method m)
/*     */   {
/*     */     try {
/*  99 */       Method delegate = this.delegateImpl.getMethod(m.getName(), m.getParameterTypes());
/* 100 */       if (!delegate.getReturnType().getName().equals(m.getReturnType().getName()))
/* 101 */         throw new IllegalArgumentException("Invalid delegate signature " + delegate);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 104 */       throw new CodeGenerationException(e);
/*     */     }
/*     */ 
/* 107 */     Signature sig = ReflectUtils.getSignature(m);
/* 108 */     Type[] exceptions = TypeUtils.getTypes(m.getExceptionTypes());
/* 109 */     CodeEmitter e = super.begin_method(1, sig, exceptions, null);
/* 110 */     e.load_this();
/* 111 */     e.getfield("$CGLIB_DELEGATE");
/* 112 */     e.load_args();
/* 113 */     e.invoke_virtual(this.delegateType, sig);
/* 114 */     e.return_value();
/* 115 */     e.end_method();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.AddDelegateTransformer
 * JD-Core Version:    0.6.0
 */
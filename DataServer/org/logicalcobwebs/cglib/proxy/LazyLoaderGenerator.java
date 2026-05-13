/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.logicalcobwebs.cglib.asm.Label;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*    */ import org.logicalcobwebs.cglib.core.ClassInfo;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ 
/*    */ class LazyLoaderGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 24 */   public static final LazyLoaderGenerator INSTANCE = new LazyLoaderGenerator();
/*    */ 
/* 26 */   private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
/*    */ 
/* 28 */   private static final Type LAZY_LOADER = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.LazyLoader");
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods)
/*    */   {
/* 32 */     Set indexes = new HashSet();
/* 33 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 34 */       MethodInfo method = (MethodInfo)it.next();
/* 35 */       if (!TypeUtils.isProtected(method.getModifiers()))
/*    */       {
/* 38 */         int index = context.getIndex(method);
/* 39 */         indexes.add(new Integer(index));
/* 40 */         CodeEmitter e = context.beginMethod(ce, method);
/* 41 */         e.load_this();
/* 42 */         e.dup();
/* 43 */         e.invoke_virtual_this(loadMethod(index));
/* 44 */         e.checkcast(method.getClassInfo().getType());
/* 45 */         e.load_args();
/* 46 */         e.invoke(method);
/* 47 */         e.return_value();
/* 48 */         e.end_method();
/*    */       }
/*    */     }
/*    */ 
/* 52 */     for (Iterator it = indexes.iterator(); it.hasNext(); ) {
/* 53 */       int index = ((Integer)it.next()).intValue();
/*    */ 
/* 55 */       String delegate = "CGLIB$LAZY_LOADER_" + index;
/* 56 */       ce.declare_field(2, delegate, Constants.TYPE_OBJECT, null, null);
/*    */ 
/* 58 */       CodeEmitter e = ce.begin_method(50, loadMethod(index), null, null);
/*    */ 
/* 64 */       e.load_this();
/* 65 */       e.getfield(delegate);
/* 66 */       e.dup();
/* 67 */       Label end = e.make_label();
/* 68 */       e.ifnonnull(end);
/* 69 */       e.pop();
/* 70 */       e.load_this();
/* 71 */       context.emitCallback(e, index);
/* 72 */       e.invoke_interface(LAZY_LOADER, LOAD_OBJECT);
/* 73 */       e.dup_x1();
/* 74 */       e.putfield(delegate);
/* 75 */       e.mark(end);
/* 76 */       e.return_value();
/* 77 */       e.end_method();
/*    */     }
/*    */   }
/*    */ 
/*    */   private Signature loadMethod(int index)
/*    */   {
/* 83 */     return new Signature("CGLIB$LOAD_PRIVATE_" + index, Constants.TYPE_OBJECT, Constants.TYPES_EMPTY);
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.LazyLoaderGenerator
 * JD-Core Version:    0.6.0
 */
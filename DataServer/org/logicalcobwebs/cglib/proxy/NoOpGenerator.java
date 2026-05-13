/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ 
/*    */ class NoOpGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 25 */   public static final NoOpGenerator INSTANCE = new NoOpGenerator();
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods) {
/* 28 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 29 */       MethodInfo method = (MethodInfo)it.next();
/* 30 */       if ((TypeUtils.isProtected(context.getOriginalModifiers(method))) && (TypeUtils.isPublic(method.getModifiers())))
/*    */       {
/* 32 */         CodeEmitter e = EmitUtils.begin_method(ce, method);
/* 33 */         e.load_this();
/* 34 */         e.load_args();
/* 35 */         e.super_invoke();
/* 36 */         e.return_value();
/* 37 */         e.end_method();
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.NoOpGenerator
 * JD-Core Version:    0.6.0
 */
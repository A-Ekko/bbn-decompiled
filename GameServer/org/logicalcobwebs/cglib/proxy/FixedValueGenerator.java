/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.ClassEmitter;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ 
/*    */ class FixedValueGenerator
/*    */   implements CallbackGenerator
/*    */ {
/* 23 */   public static final FixedValueGenerator INSTANCE = new FixedValueGenerator();
/* 24 */   private static final Type FIXED_VALUE = TypeUtils.parseType("org.logicalcobwebs.cglib.proxy.FixedValue");
/*    */ 
/* 26 */   private static final Signature LOAD_OBJECT = TypeUtils.parseSignature("Object loadObject()");
/*    */ 
/*    */   public void generate(ClassEmitter ce, CallbackGenerator.Context context, List methods)
/*    */   {
/* 30 */     for (Iterator it = methods.iterator(); it.hasNext(); ) {
/* 31 */       MethodInfo method = (MethodInfo)it.next();
/* 32 */       CodeEmitter e = context.beginMethod(ce, method);
/* 33 */       context.emitCallback(e, context.getIndex(method));
/* 34 */       e.invoke_interface(FIXED_VALUE, LOAD_OBJECT);
/* 35 */       e.unbox_or_zero(e.getReturnType());
/* 36 */       e.return_value();
/* 37 */       e.end_method();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void generateStatic(CodeEmitter e, CallbackGenerator.Context context, List methods)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.FixedValueGenerator
 * JD-Core Version:    0.6.0
 */
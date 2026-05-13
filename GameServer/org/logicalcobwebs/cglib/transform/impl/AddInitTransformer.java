/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*    */ 
/*    */ public class AddInitTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private MethodInfo info;
/*    */ 
/*    */   public AddInitTransformer(Method method)
/*    */   {
/* 37 */     this.info = ReflectUtils.getMethodInfo(method);
/*    */ 
/* 39 */     Type[] types = this.info.getSignature().getArgumentTypes();
/* 40 */     if ((types.length != 1) || (!types[0].equals(Constants.TYPE_OBJECT)) || (!this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)))
/*    */     {
/* 43 */       throw new IllegalArgumentException(method + " illegal signature");
/*    */     }
/*    */   }
/*    */ 
/*    */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions, Attribute attrs)
/*    */   {
/* 49 */     CodeEmitter emitter = super.begin_method(access, sig, exceptions, attrs);
/*    */ 
/* 52 */     if (sig.getName().equals("<init>"))
/* 53 */       return new CodeEmitter(emitter) {
/*    */         public void visitInsn(int opcode) {
/* 55 */           if (opcode == 177) {
/* 56 */             load_this();
/* 57 */             invoke(AddInitTransformer.this.info);
/*    */           }
/* 59 */           super.visitInsn(opcode);
/*    */         }
/*    */       };
/* 63 */     return emitter;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.AddInitTransformer
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.Block;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*    */ 
/*    */ public class UndeclaredThrowableTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private Type wrapper;
/*    */ 
/*    */   public UndeclaredThrowableTransformer(Class wrapper)
/*    */   {
/* 29 */     this.wrapper = Type.getType(wrapper);
/* 30 */     boolean found = false;
/* 31 */     Constructor[] cstructs = wrapper.getConstructors();
/* 32 */     for (int i = 0; i < cstructs.length; i++) {
/* 33 */       Class[] types = cstructs[i].getParameterTypes();
/* 34 */       if ((types.length == 1) && (types[0].equals(Throwable.class))) {
/* 35 */         found = true;
/* 36 */         break;
/*    */       }
/*    */     }
/* 39 */     if (!found)
/* 40 */       throw new IllegalArgumentException(wrapper + " does not have a single-arg constructor that takes a Throwable");
/*    */   }
/*    */ 
/*    */   public CodeEmitter begin_method(int access, Signature sig, Type[] exceptions, Attribute attrs) {
/* 44 */     CodeEmitter e = super.begin_method(access, sig, exceptions, attrs);
/* 45 */     if ((TypeUtils.isAbstract(access)) || (sig.equals(Constants.SIG_STATIC))) {
/* 46 */       return e;
/*    */     }
/* 48 */     return new CodeEmitter(e, exceptions)
/*    */     {
/*    */       private Block handler;
/*    */ 
/*    */       public void visitMaxs(int maxStack, int maxLocals) {
/* 54 */         this.handler.end();
/* 55 */         EmitUtils.wrap_undeclared_throwable(this, this.handler, this.val$exceptions, UndeclaredThrowableTransformer.this.wrapper);
/* 56 */         super.visitMaxs(maxStack, maxLocals);
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.UndeclaredThrowableTransformer
 * JD-Core Version:    0.6.0
 */
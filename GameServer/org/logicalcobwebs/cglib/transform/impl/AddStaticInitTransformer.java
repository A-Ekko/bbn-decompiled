/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ import org.logicalcobwebs.cglib.core.CodeEmitter;
/*    */ import org.logicalcobwebs.cglib.core.Constants;
/*    */ import org.logicalcobwebs.cglib.core.EmitUtils;
/*    */ import org.logicalcobwebs.cglib.core.MethodInfo;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ import org.logicalcobwebs.cglib.core.Signature;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ import org.logicalcobwebs.cglib.transform.ClassEmitterTransformer;
/*    */ 
/*    */ public class AddStaticInitTransformer extends ClassEmitterTransformer
/*    */ {
/*    */   private MethodInfo info;
/*    */ 
/*    */   public AddStaticInitTransformer(Method classInit)
/*    */   {
/* 30 */     this.info = ReflectUtils.getMethodInfo(classInit);
/* 31 */     if (!TypeUtils.isStatic(this.info.getModifiers())) {
/* 32 */       throw new IllegalArgumentException(classInit + " is not static");
/*    */     }
/* 34 */     Type[] types = this.info.getSignature().getArgumentTypes();
/* 35 */     if ((types.length != 1) || (!types[0].equals(Constants.TYPE_CLASS)) || (!this.info.getSignature().getReturnType().equals(Type.VOID_TYPE)))
/*    */     {
/* 38 */       throw new IllegalArgumentException(classInit + " illegal signature");
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void init() {
/* 43 */     if (!TypeUtils.isInterface(getAccess())) {
/* 44 */       CodeEmitter e = getStaticHook();
/* 45 */       EmitUtils.load_class_this(e);
/* 46 */       e.invoke(this.info);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.AddStaticInitTransformer
 * JD-Core Version:    0.6.0
 */
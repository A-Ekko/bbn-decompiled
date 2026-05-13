/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ 
/*    */ class MixinBeanEmitter extends MixinEmitter
/*    */ {
/*    */   public MixinBeanEmitter(ClassVisitor v, String className, Class[] classes)
/*    */   {
/* 28 */     super(v, className, classes, null);
/*    */   }
/*    */ 
/*    */   protected Class[] getInterfaces(Class[] classes) {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   protected Method[] getMethods(Class type) {
/* 36 */     return ReflectUtils.getPropertyMethods(ReflectUtils.getBeanProperties(type), true, true);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MixinBeanEmitter
 * JD-Core Version:    0.6.0
 */
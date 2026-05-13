/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.core.CollectionUtils;
/*    */ import org.logicalcobwebs.cglib.core.ReflectUtils;
/*    */ import org.logicalcobwebs.cglib.core.RejectModifierPredicate;
/*    */ 
/*    */ class MixinEverythingEmitter extends MixinEmitter
/*    */ {
/*    */   public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes)
/*    */   {
/* 33 */     super(v, className, classes, null);
/*    */   }
/*    */ 
/*    */   protected Class[] getInterfaces(Class[] classes) {
/* 37 */     List list = new ArrayList();
/* 38 */     for (int i = 0; i < classes.length; i++) {
/* 39 */       ReflectUtils.addAllInterfaces(classes[i], list);
/*    */     }
/* 41 */     return (Class[])(Class[])list.toArray(new Class[list.size()]);
/*    */   }
/*    */ 
/*    */   protected Method[] getMethods(Class type) {
/* 45 */     List methods = new ArrayList(Arrays.asList(type.getMethods()));
/* 46 */     CollectionUtils.filter(methods, new RejectModifierPredicate(24));
/* 47 */     return (Method[])(Method[])methods.toArray(new Method[methods.size()]);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.MixinEverythingEmitter
 * JD-Core Version:    0.6.0
 */
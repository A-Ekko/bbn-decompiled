/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import java.lang.reflect.Member;
/*    */ import java.lang.reflect.Modifier;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public class VisibilityPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private boolean protectedOk;
/*    */   private String pkg;
/*    */ 
/*    */   public VisibilityPredicate(Class source, boolean protectedOk)
/*    */   {
/* 26 */     this.protectedOk = protectedOk;
/* 27 */     this.pkg = TypeUtils.getPackageName(Type.getType(source));
/*    */   }
/*    */ 
/*    */   public boolean evaluate(Object arg) {
/* 31 */     int mod = (arg instanceof Member) ? ((Member)arg).getModifiers() : ((Integer)arg).intValue();
/* 32 */     if (Modifier.isPrivate(mod))
/* 33 */       return false;
/* 34 */     if (Modifier.isPublic(mod))
/* 35 */       return true;
/* 36 */     if (Modifier.isProtected(mod)) {
/* 37 */       return this.protectedOk;
/*    */     }
/* 39 */     return this.pkg.equals(TypeUtils.getPackageName(Type.getType(((Member)arg).getDeclaringClass())));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.VisibilityPredicate
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public abstract class MethodInfo
/*    */ {
/*    */   public abstract ClassInfo getClassInfo();
/*    */ 
/*    */   public abstract int getModifiers();
/*    */ 
/*    */   public abstract Signature getSignature();
/*    */ 
/*    */   public abstract Type[] getExceptionTypes();
/*    */ 
/*    */   public abstract Attribute getAttribute();
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 33 */     if (o == null)
/* 34 */       return false;
/* 35 */     if (!(o instanceof MethodInfo))
/* 36 */       return false;
/* 37 */     return getSignature().equals(((MethodInfo)o).getSignature());
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 41 */     return getSignature().hashCode();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 46 */     return getSignature().toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.MethodInfo
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Type;
/*    */ 
/*    */ public abstract class ClassInfo
/*    */ {
/*    */   public abstract Type getType();
/*    */ 
/*    */   public abstract Type getSuperType();
/*    */ 
/*    */   public abstract Type[] getInterfaces();
/*    */ 
/*    */   public abstract int getModifiers();
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 32 */     if (o == null)
/* 33 */       return false;
/* 34 */     if (!(o instanceof ClassInfo))
/* 35 */       return false;
/* 36 */     return getType().equals(((ClassInfo)o).getType());
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 40 */     return getType().hashCode();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 45 */     return getType().getClassName();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ClassInfo
 * JD-Core Version:    0.6.0
 */
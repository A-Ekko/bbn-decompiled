/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.ClassAdapter;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ 
/*    */ public abstract class AbstractClassTransformer extends ClassAdapter
/*    */   implements ClassTransformer
/*    */ {
/*    */   protected AbstractClassTransformer()
/*    */   {
/* 24 */     super(null);
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 28 */     this.cv = target;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.AbstractClassTransformer
 * JD-Core Version:    0.6.0
 */
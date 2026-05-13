/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.ClassAdapter;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ 
/*    */ public class ClassTransformerTee extends ClassAdapter
/*    */   implements ClassTransformer
/*    */ {
/*    */   private ClassVisitor branch;
/*    */ 
/*    */   public ClassTransformerTee(ClassVisitor branch)
/*    */   {
/* 25 */     super(null);
/* 26 */     this.branch = branch;
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 30 */     this.cv = new ClassVisitorTee(this.branch, target);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassTransformerTee
 * JD-Core Version:    0.6.0
 */
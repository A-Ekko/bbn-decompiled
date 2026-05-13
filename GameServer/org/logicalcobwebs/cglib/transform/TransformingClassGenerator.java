/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class TransformingClassGenerator
/*    */   implements ClassGenerator
/*    */ {
/*    */   private ClassGenerator gen;
/*    */   private ClassTransformer t;
/*    */ 
/*    */   public TransformingClassGenerator(ClassGenerator gen, ClassTransformer t)
/*    */   {
/* 27 */     this.gen = gen;
/* 28 */     this.t = t;
/*    */   }
/*    */ 
/*    */   public void generateClass(ClassVisitor v) throws Exception {
/* 32 */     this.t.setTarget(v);
/* 33 */     this.gen.generateClass(this.t);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.TransformingClassGenerator
 * JD-Core Version:    0.6.0
 */
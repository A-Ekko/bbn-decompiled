/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*    */ 
/*    */ public class MethodFilterTransformer extends AbstractClassTransformer
/*    */ {
/*    */   private MethodFilter filter;
/*    */   private ClassTransformer pass;
/*    */   private ClassVisitor direct;
/*    */ 
/*    */   public MethodFilterTransformer(MethodFilter filter, ClassTransformer pass)
/*    */   {
/* 26 */     this.filter = filter;
/* 27 */     this.pass = pass;
/* 28 */     super.setTarget(pass);
/*    */   }
/*    */ 
/*    */   public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
/* 32 */     return (this.filter.accept(access, name, desc, exceptions, attrs) ? this.pass : this.direct).visitMethod(access, name, desc, exceptions, attrs);
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor target) {
/* 36 */     this.pass.setTarget(target);
/* 37 */     this.direct = target;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.MethodFilterTransformer
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*    */ 
/*    */ public class ClassTransformerChain extends AbstractClassTransformer
/*    */ {
/*    */   private ClassTransformer[] chain;
/*    */ 
/*    */   public ClassTransformerChain(ClassTransformer[] chain)
/*    */   {
/* 24 */     this.chain = ((ClassTransformer[])(ClassTransformer[])chain.clone());
/*    */   }
/*    */ 
/*    */   public void setTarget(ClassVisitor v) {
/* 28 */     super.setTarget(this.chain[0]);
/* 29 */     ClassVisitor next = v;
/* 30 */     for (int i = this.chain.length - 1; i >= 0; i--) {
/* 31 */       this.chain[i].setTarget(next);
/* 32 */       next = this.chain[i];
/*    */     }
/*    */   }
/*    */ 
/*    */   public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs)
/*    */   {
/* 41 */     return this.cv.visitMethod(access, name, desc, exceptions, attrs);
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 45 */     StringBuffer sb = new StringBuffer();
/* 46 */     sb.append("ClassTransformerChain{");
/* 47 */     for (int i = 0; i < this.chain.length; i++) {
/* 48 */       if (i > 0) {
/* 49 */         sb.append(", ");
/*    */       }
/* 51 */       sb.append(this.chain[i].toString());
/*    */     }
/* 53 */     sb.append("}");
/* 54 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassTransformerChain
 * JD-Core Version:    0.6.0
 */
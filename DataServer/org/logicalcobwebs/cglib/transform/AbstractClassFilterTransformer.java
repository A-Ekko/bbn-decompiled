/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*    */ 
/*    */ public abstract class AbstractClassFilterTransformer extends AbstractClassTransformer
/*    */ {
/*    */   private ClassTransformer pass;
/*    */   private ClassVisitor target;
/*    */ 
/*    */   public void setTarget(ClassVisitor target)
/*    */   {
/* 25 */     super.setTarget(target);
/* 26 */     this.pass.setTarget(target);
/*    */   }
/*    */ 
/*    */   protected AbstractClassFilterTransformer(ClassTransformer pass) {
/* 30 */     this.pass = pass;
/*    */   }
/*    */   protected abstract boolean accept(int paramInt1, int paramInt2, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3);
/*    */ 
/*    */   public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
/* 36 */     this.target = (accept(version, access, name, superName, interfaces, sourceFile) ? this.pass : this.cv);
/* 37 */     this.target.visit(version, access, name, superName, interfaces, sourceFile);
/*    */   }
/*    */ 
/*    */   public void visitEnd() {
/* 41 */     this.target.visitEnd();
/* 42 */     this.target = null;
/*    */   }
/*    */ 
/*    */   public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
/* 46 */     this.target.visitField(access, name, desc, value, attrs);
/*    */   }
/*    */ 
/*    */   public void visitInnerClass(String name, String outerName, String innerName, int access) {
/* 50 */     this.target.visitInnerClass(name, outerName, innerName, access);
/*    */   }
/*    */ 
/*    */   public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
/* 54 */     return this.target.visitMethod(access, name, desc, exceptions, attrs);
/*    */   }
/*    */ 
/*    */   public void visitAttribute(Attribute attrs) {
/* 58 */     this.target.visitAttribute(attrs);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.AbstractClassFilterTransformer
 * JD-Core Version:    0.6.0
 */
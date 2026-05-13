/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*    */ 
/*    */ public class ClassVisitorTee
/*    */   implements ClassVisitor
/*    */ {
/*    */   private ClassVisitor cv1;
/*    */   private ClassVisitor cv2;
/*    */ 
/*    */   public ClassVisitorTee(ClassVisitor cv1, ClassVisitor cv2)
/*    */   {
/* 26 */     this.cv1 = cv1;
/* 27 */     this.cv2 = cv2;
/*    */   }
/*    */ 
/*    */   public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
/* 31 */     this.cv1.visit(version, access, name, superName, interfaces, sourceFile);
/* 32 */     this.cv2.visit(version, access, name, superName, interfaces, sourceFile);
/*    */   }
/*    */ 
/*    */   public void visitEnd() {
/* 36 */     this.cv1.visitEnd();
/* 37 */     this.cv2.visitEnd();
/* 38 */     this.cv1 = (this.cv2 = null);
/*    */   }
/*    */ 
/*    */   public void visitField(int access, String name, String desc, Object value, Attribute attrs) {
/* 42 */     this.cv1.visitField(access, name, desc, value, attrs);
/* 43 */     this.cv2.visitField(access, name, desc, value, attrs);
/*    */   }
/*    */ 
/*    */   public void visitInnerClass(String name, String outerName, String innerName, int access) {
/* 47 */     this.cv1.visitInnerClass(name, outerName, innerName, access);
/* 48 */     this.cv2.visitInnerClass(name, outerName, innerName, access);
/*    */   }
/*    */ 
/*    */   public CodeVisitor visitMethod(int access, String name, String desc, String[] exceptions, Attribute attrs) {
/* 52 */     CodeVisitor code1 = this.cv1.visitMethod(access, name, desc, exceptions, attrs);
/* 53 */     CodeVisitor code2 = this.cv2.visitMethod(access, name, desc, exceptions, attrs);
/* 54 */     if (code1 == null)
/* 55 */       return code2;
/* 56 */     if (code2 == null) {
/* 57 */       return code1;
/*    */     }
/* 59 */     return new CodeVisitorTee(code1, code2);
/*    */   }
/*    */ 
/*    */   public void visitAttribute(Attribute attrs)
/*    */   {
/* 64 */     this.cv1.visitAttribute(attrs);
/* 65 */     this.cv2.visitAttribute(attrs);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassVisitorTee
 * JD-Core Version:    0.6.0
 */
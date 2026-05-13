/*     */ package org.logicalcobwebs.cglib.transform;
/*     */ 
/*     */ import org.logicalcobwebs.cglib.asm.Attribute;
/*     */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ 
/*     */ public class CodeVisitorTee
/*     */   implements CodeVisitor
/*     */ {
/*     */   private CodeVisitor cv1;
/*     */   private CodeVisitor cv2;
/*     */ 
/*     */   public CodeVisitorTee(CodeVisitor cv1, CodeVisitor cv2)
/*     */   {
/*  26 */     this.cv1 = cv1;
/*  27 */     this.cv2 = cv2;
/*     */   }
/*     */ 
/*     */   public void visitFieldInsn(int opcode, String owner, String name, String desc) {
/*  31 */     this.cv1.visitFieldInsn(opcode, owner, name, desc);
/*  32 */     this.cv2.visitFieldInsn(opcode, owner, name, desc);
/*     */   }
/*     */ 
/*     */   public void visitIincInsn(int var, int increment) {
/*  36 */     this.cv1.visitIincInsn(var, increment);
/*  37 */     this.cv2.visitIincInsn(var, increment);
/*     */   }
/*     */ 
/*     */   public void visitInsn(int opcode) {
/*  41 */     this.cv1.visitInsn(opcode);
/*  42 */     this.cv2.visitInsn(opcode);
/*     */   }
/*     */ 
/*     */   public void visitIntInsn(int opcode, int operand) {
/*  46 */     this.cv1.visitIntInsn(opcode, operand);
/*  47 */     this.cv2.visitIntInsn(opcode, operand);
/*     */   }
/*     */ 
/*     */   public void visitJumpInsn(int opcode, Label label) {
/*  51 */     this.cv1.visitJumpInsn(opcode, label);
/*  52 */     this.cv2.visitJumpInsn(opcode, label);
/*     */   }
/*     */ 
/*     */   public void visitLabel(Label label) {
/*  56 */     this.cv1.visitLabel(label);
/*  57 */     this.cv2.visitLabel(label);
/*     */   }
/*     */ 
/*     */   public void visitLdcInsn(Object cst) {
/*  61 */     this.cv1.visitLdcInsn(cst);
/*  62 */     this.cv2.visitLdcInsn(cst);
/*     */   }
/*     */ 
/*     */   public void visitLineNumber(int line, Label start) {
/*  66 */     this.cv1.visitLineNumber(line, start);
/*  67 */     this.cv2.visitLineNumber(line, start);
/*     */   }
/*     */ 
/*     */   public void visitLocalVariable(String name, String desc, Label start, Label end, int index) {
/*  71 */     this.cv1.visitLocalVariable(name, desc, start, end, index);
/*  72 */     this.cv2.visitLocalVariable(name, desc, start, end, index);
/*     */   }
/*     */ 
/*     */   public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
/*  76 */     this.cv1.visitLookupSwitchInsn(dflt, keys, labels);
/*  77 */     this.cv2.visitLookupSwitchInsn(dflt, keys, labels);
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals) {
/*  81 */     this.cv1.visitMaxs(maxStack, maxLocals);
/*  82 */     this.cv2.visitMaxs(maxStack, maxLocals);
/*     */   }
/*     */ 
/*     */   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
/*  86 */     this.cv1.visitMethodInsn(opcode, owner, name, desc);
/*  87 */     this.cv2.visitMethodInsn(opcode, owner, name, desc);
/*     */   }
/*     */ 
/*     */   public void visitMultiANewArrayInsn(String desc, int dims) {
/*  91 */     this.cv1.visitMultiANewArrayInsn(desc, dims);
/*  92 */     this.cv2.visitMultiANewArrayInsn(desc, dims);
/*     */   }
/*     */ 
/*     */   public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {
/*  96 */     this.cv1.visitTableSwitchInsn(min, max, dflt, labels);
/*  97 */     this.cv2.visitTableSwitchInsn(min, max, dflt, labels);
/*     */   }
/*     */ 
/*     */   public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
/* 101 */     this.cv1.visitTryCatchBlock(start, end, handler, type);
/* 102 */     this.cv2.visitTryCatchBlock(start, end, handler, type);
/*     */   }
/*     */ 
/*     */   public void visitTypeInsn(int opcode, String desc) {
/* 106 */     this.cv1.visitTypeInsn(opcode, desc);
/* 107 */     this.cv2.visitTypeInsn(opcode, desc);
/*     */   }
/*     */ 
/*     */   public void visitVarInsn(int opcode, int var) {
/* 111 */     this.cv1.visitVarInsn(opcode, var);
/* 112 */     this.cv2.visitVarInsn(opcode, var);
/*     */   }
/*     */ 
/*     */   public void visitAttribute(Attribute attrs) {
/* 116 */     this.cv1.visitAttribute(attrs);
/* 117 */     this.cv2.visitAttribute(attrs);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.CodeVisitorTee
 * JD-Core Version:    0.6.0
 */
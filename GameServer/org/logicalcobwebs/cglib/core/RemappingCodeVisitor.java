/*     */ package org.logicalcobwebs.cglib.core;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.logicalcobwebs.cglib.asm.CodeAdapter;
/*     */ import org.logicalcobwebs.cglib.asm.CodeVisitor;
/*     */ import org.logicalcobwebs.cglib.asm.Label;
/*     */ import org.logicalcobwebs.cglib.asm.Type;
/*     */ 
/*     */ public class RemappingCodeVisitor extends CodeAdapter
/*     */ {
/*     */   private State state;
/*  23 */   private IntRef check = new IntRef(null);
/*     */ 
/*     */   public RemappingCodeVisitor(CodeVisitor v, int access, Type[] args)
/*     */   {
/*  50 */     super(v);
/*  51 */     this.state = new State(access, args);
/*     */   }
/*     */ 
/*     */   public RemappingCodeVisitor(RemappingCodeVisitor wrap) {
/*  55 */     super(wrap.cv);
/*  56 */     this.state = wrap.state;
/*     */   }
/*     */ 
/*     */   protected int nextLocal(int size) {
/*  60 */     int var = this.state.nextLocal;
/*  61 */     this.state.nextLocal += size;
/*  62 */     return var;
/*     */   }
/*     */ 
/*     */   private int remap(int var, int size) {
/*  66 */     if (var < this.state.firstLocal) {
/*  67 */       return var;
/*     */     }
/*  69 */     Integer value = remapHelper(var, size == 0 ? 1 : size);
/*  70 */     if (value == null) {
/*  71 */       if (size == 0) {
/*  72 */         value = remapHelper(var, 2);
/*  73 */         if (value == null)
/*  74 */           throw new IllegalStateException("Unknown local variable " + var);
/*     */       }
/*     */       else {
/*  77 */         IntRef ref = new IntRef(null);
/*  78 */         ref.key = getKey(var, size);
/*  79 */         this.state.locals.put(ref, value = new Integer(nextLocal(size)));
/*     */       }
/*     */     }
/*     */ 
/*  83 */     return value.intValue();
/*     */   }
/*     */ 
/*     */   private Integer remapHelper(int var, int size) {
/*  87 */     this.check.key = getKey(var, size);
/*  88 */     return (Integer)this.state.locals.get(this.check);
/*     */   }
/*     */ 
/*     */   private int getKey(int var, int size) {
/*  92 */     return size == 2 ? var ^ 0xFFFFFFFF : var;
/*     */   }
/*     */ 
/*     */   public void visitIincInsn(int var, int increment) {
/*  96 */     this.cv.visitIincInsn(remap(var, 1), increment);
/*     */   }
/*     */ 
/*     */   public void visitLocalVariable(String name, String desc, Label start, Label end, int index) {
/* 100 */     this.cv.visitLocalVariable(name, desc, start, end, remap(index, 0));
/*     */   }
/*     */ 
/*     */   public void visitVarInsn(int opcode, int var)
/*     */   {
/*     */     int size;
/* 105 */     switch (opcode) {
/*     */     case 22:
/*     */     case 24:
/*     */     case 55:
/*     */     case 57:
/* 110 */       size = 2;
/* 111 */       break;
/*     */     default:
/* 113 */       size = 1;
/*     */     }
/* 115 */     this.cv.visitVarInsn(opcode, remap(var, size));
/*     */   }
/*     */ 
/*     */   public void visitMaxs(int maxStack, int maxLocals) {
/* 119 */     this.cv.visitMaxs(0, 0);
/*     */   }
/*     */ 
/*     */   private static class IntRef
/*     */   {
/*     */     int key;
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/*  42 */       return this.key == ((IntRef)o).key;
/*     */     }
/*     */     public int hashCode() {
/*  45 */       return this.key;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class State
/*     */   {
/*  26 */     Map locals = new HashMap();
/*     */     int firstLocal;
/*     */     int nextLocal;
/*     */ 
/*     */     State(int access, Type[] args)
/*     */     {
/*  31 */       this.nextLocal = ((0x8 & access) != 0 ? 0 : 1);
/*  32 */       for (int i = 0; i < args.length; i++) {
/*  33 */         this.nextLocal += args[i].getSize();
/*     */       }
/*  35 */       this.firstLocal = this.nextLocal;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.RemappingCodeVisitor
 * JD-Core Version:    0.6.0
 */
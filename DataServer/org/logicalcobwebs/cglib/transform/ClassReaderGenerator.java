/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class ClassReaderGenerator
/*    */   implements ClassGenerator
/*    */ {
/*    */   private ClassReader r;
/*    */   private Attribute[] attrs;
/*    */   private boolean skipDebug;
/*    */ 
/*    */   public ClassReaderGenerator(ClassReader r, boolean skipDebug)
/*    */   {
/* 29 */     this(r, null, skipDebug);
/*    */   }
/*    */ 
/*    */   public ClassReaderGenerator(ClassReader r, Attribute[] attrs, boolean skipDebug) {
/* 33 */     this.r = r;
/* 34 */     if (attrs == null)
/* 35 */       attrs = new Attribute[0];
/* 36 */     this.attrs = attrs;
/* 37 */     this.skipDebug = skipDebug;
/*    */   }
/*    */ 
/*    */   public void generateClass(ClassVisitor v) {
/* 41 */     this.r.accept(v, this.attrs, this.skipDebug);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassReaderGenerator
 * JD-Core Version:    0.6.0
 */
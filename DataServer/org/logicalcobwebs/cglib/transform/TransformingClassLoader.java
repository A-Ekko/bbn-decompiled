/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*    */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*    */ 
/*    */ public class TransformingClassLoader extends AbstractClassLoader
/*    */ {
/*    */   private ClassTransformerFactory t;
/*    */ 
/*    */   public TransformingClassLoader(ClassLoader parent, ClassFilter filter, ClassTransformerFactory t)
/*    */   {
/* 26 */     super(parent, parent, filter);
/* 27 */     this.t = t;
/*    */   }
/*    */ 
/*    */   protected ClassGenerator getGenerator(ClassReader r) {
/* 31 */     ClassTransformer t2 = this.t.newInstance();
/* 32 */     return new TransformingClassGenerator(super.getGenerator(r), t2);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.TransformingClassLoader
 * JD-Core Version:    0.6.0
 */
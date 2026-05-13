/*    */ package org.logicalcobwebs.cglib.transform;
/*    */ 
/*    */ public class ClassFilterTransformer extends AbstractClassFilterTransformer
/*    */ {
/*    */   private ClassFilter filter;
/*    */ 
/*    */   public ClassFilterTransformer(ClassFilter filter, ClassTransformer pass)
/*    */   {
/* 24 */     super(pass);
/* 25 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   protected boolean accept(int version, int access, String name, String superName, String[] interfaces, String sourceFile) {
/* 29 */     return this.filter.accept(name.replace('/', '.'));
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.ClassFilterTransformer
 * JD-Core Version:    0.6.0
 */
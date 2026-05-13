/*    */ package org.logicalcobwebs.cglib.transform.impl;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.asm.Attribute;
/*    */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*    */ import org.logicalcobwebs.cglib.core.DefaultGeneratorStrategy;
/*    */ import org.logicalcobwebs.cglib.core.TypeUtils;
/*    */ import org.logicalcobwebs.cglib.transform.ClassTransformer;
/*    */ import org.logicalcobwebs.cglib.transform.MethodFilter;
/*    */ import org.logicalcobwebs.cglib.transform.MethodFilterTransformer;
/*    */ import org.logicalcobwebs.cglib.transform.TransformingClassGenerator;
/*    */ 
/*    */ public class UndeclaredThrowableStrategy extends DefaultGeneratorStrategy
/*    */ {
/*    */   private ClassTransformer t;
/* 43 */   private static final MethodFilter TRANSFORM_FILTER = new MethodFilter() {
/*    */     public boolean accept(int access, String name, String desc, String[] exceptions, Attribute attrs) {
/* 45 */       return (!TypeUtils.isPrivate(access)) && (name.indexOf('$') < 0);
/*    */     }
/* 43 */   };
/*    */ 
/*    */   public UndeclaredThrowableStrategy(Class wrapper)
/*    */   {
/* 39 */     this.t = new UndeclaredThrowableTransformer(wrapper);
/* 40 */     this.t = new MethodFilterTransformer(TRANSFORM_FILTER, this.t);
/*    */   }
/*    */ 
/*    */   protected ClassGenerator transform(ClassGenerator cg)
/*    */     throws Exception
/*    */   {
/* 50 */     return new TransformingClassGenerator(cg, this.t);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.impl.UndeclaredThrowableStrategy
 * JD-Core Version:    0.6.0
 */
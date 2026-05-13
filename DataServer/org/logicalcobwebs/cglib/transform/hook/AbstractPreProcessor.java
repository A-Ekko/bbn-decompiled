/*    */ package org.logicalcobwebs.cglib.transform.hook;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import org.codehaus.aspectwerkz.hook.ClassPreProcessor;
/*    */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*    */ import org.logicalcobwebs.cglib.asm.ClassWriter;
/*    */ import org.logicalcobwebs.cglib.core.ClassGenerator;
/*    */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*    */ import org.logicalcobwebs.cglib.core.DebuggingClassWriter;
/*    */ import org.logicalcobwebs.cglib.transform.ClassReaderGenerator;
/*    */ import org.logicalcobwebs.cglib.transform.ClassTransformer;
/*    */ import org.logicalcobwebs.cglib.transform.TransformingClassGenerator;
/*    */ 
/*    */ public abstract class AbstractPreProcessor
/*    */   implements ClassPreProcessor
/*    */ {
/*    */   public void initialize(Hashtable hashtable)
/*    */   {
/*    */   }
/*    */ 
/*    */   public byte[] preProcess(String name, byte[] abyte, ClassLoader caller)
/*    */   {
/*    */     try
/*    */     {
/* 31 */       ClassTransformer t = getClassTransformer(name);
/* 32 */       if (t == null)
/* 33 */         return abyte;
/* 34 */       ClassWriter w = new DebuggingClassWriter(true);
/* 35 */       ClassGenerator gen = new ClassReaderGenerator(new ClassReader(abyte), false);
/* 36 */       gen = new TransformingClassGenerator(gen, t);
/* 37 */       gen.generateClass(w);
/* 38 */       return w.toByteArray(); } catch (Exception e) {
/*    */     }
/* 40 */     throw new CodeGenerationException(e);
/*    */   }
/*    */ 
/*    */   protected abstract ClassTransformer getClassTransformer(String paramString);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.transform.hook.AbstractPreProcessor
 * JD-Core Version:    0.6.0
 */
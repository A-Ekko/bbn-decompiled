/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.logicalcobwebs.cglib.asm.ClassAdapter;
/*    */ import org.logicalcobwebs.cglib.asm.ClassReader;
/*    */ import org.logicalcobwebs.cglib.asm.ClassVisitor;
/*    */ 
/*    */ public class ClassNameReader
/*    */ {
/* 27 */   private static final EarlyExitException EARLY_EXIT = new EarlyExitException(null);
/*    */ 
/*    */   public static String getClassName(ClassReader r)
/*    */   {
/* 32 */     return getClassInfo(r)[0];
/*    */   }
/*    */ 
/*    */   public static String[] getClassInfo(ClassReader r)
/*    */   {
/* 37 */     List array = new ArrayList();
/*    */     try {
/* 39 */       r.accept(new ClassAdapter(null, array)
/*    */       {
/*    */         public void visit(int version, int access, String name, String superName, String[] interfaces, String sourceFile)
/*    */         {
/* 46 */           this.val$array.add(name.replace('/', '.'));
/* 47 */           if (superName != null) {
/* 48 */             this.val$array.add(superName.replace('/', '.'));
/*    */           }
/* 50 */           for (int i = 0; i < interfaces.length; i++) {
/* 51 */             this.val$array.add(interfaces[i].replace('/', '.'));
/*    */           }
/*    */ 
/* 54 */           throw ClassNameReader.EARLY_EXIT;
/*    */         }
/*    */       }
/*    */       , true);
/*    */     }
/*    */     catch (EarlyExitException e)
/*    */     {
/*    */     }
/*    */ 
/* 59 */     return (String[])(String[])array.toArray(new String[0]);
/*    */   }
/*    */ 
/*    */   private static class EarlyExitException extends RuntimeException
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.ClassNameReader
 * JD-Core Version:    0.6.0
 */
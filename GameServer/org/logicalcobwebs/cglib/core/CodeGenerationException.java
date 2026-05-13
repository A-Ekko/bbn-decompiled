/*    */ package org.logicalcobwebs.cglib.core;
/*    */ 
/*    */ public class CodeGenerationException extends RuntimeException
/*    */ {
/*    */   private Throwable cause;
/*    */ 
/*    */   public CodeGenerationException(Throwable cause)
/*    */   {
/* 25 */     super(cause.getClass().getName() + "-->" + cause.getMessage());
/* 26 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public Throwable getCause() {
/* 30 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.core.CodeGenerationException
 * JD-Core Version:    0.6.0
 */
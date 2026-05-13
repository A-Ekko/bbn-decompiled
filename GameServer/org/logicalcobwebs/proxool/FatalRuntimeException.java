/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ public class FatalRuntimeException extends RuntimeException
/*    */ {
/*    */   private Exception cause;
/*    */ 
/*    */   public FatalRuntimeException(Exception cause)
/*    */   {
/* 25 */     super(cause.getMessage());
/* 26 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public Throwable getCause()
/*    */   {
/* 33 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.FatalRuntimeException
 * JD-Core Version:    0.6.0
 */
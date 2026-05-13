/*    */ package org.logicalcobwebs.cglib.proxy;
/*    */ 
/*    */ import org.logicalcobwebs.cglib.core.CodeGenerationException;
/*    */ 
/*    */ public class UndeclaredThrowableException extends CodeGenerationException
/*    */ {
/*    */   public UndeclaredThrowableException(Throwable t)
/*    */   {
/* 30 */     super(t);
/*    */   }
/*    */ 
/*    */   public Throwable getUndeclaredThrowable() {
/* 34 */     return getCause();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.cglib.proxy.UndeclaredThrowableException
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.core.session;
/*    */ 
/*    */ public class IoSessionInitializationException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -1205810145763696189L;
/*    */ 
/*    */   public IoSessionInitializationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IoSessionInitializationException(String message, Throwable cause)
/*    */   {
/* 37 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public IoSessionInitializationException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ 
/*    */   public IoSessionInitializationException(Throwable cause) {
/* 45 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.IoSessionInitializationException
 * JD-Core Version:    0.6.0
 */
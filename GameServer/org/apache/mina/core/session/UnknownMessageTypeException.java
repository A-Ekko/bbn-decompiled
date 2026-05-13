/*    */ package org.apache.mina.core.session;
/*    */ 
/*    */ public class UnknownMessageTypeException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 3257290227428047158L;
/*    */ 
/*    */   public UnknownMessageTypeException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UnknownMessageTypeException(String message, Throwable cause)
/*    */   {
/* 36 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public UnknownMessageTypeException(String message) {
/* 40 */     super(message);
/*    */   }
/*    */ 
/*    */   public UnknownMessageTypeException(Throwable cause) {
/* 44 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.session.UnknownMessageTypeException
 * JD-Core Version:    0.6.0
 */
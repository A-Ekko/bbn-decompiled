/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ public class ProtocolCodecException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 5939878548186330695L;
/*    */ 
/*    */   public ProtocolCodecException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProtocolCodecException(String message)
/*    */   {
/* 43 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProtocolCodecException(Throwable cause)
/*    */   {
/* 50 */     super(cause);
/*    */   }
/*    */ 
/*    */   public ProtocolCodecException(String message, Throwable cause)
/*    */   {
/* 58 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolCodecException
 * JD-Core Version:    0.6.0
 */
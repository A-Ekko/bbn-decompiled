/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ public class ProtocolEncoderException extends ProtocolCodecException
/*    */ {
/*    */   private static final long serialVersionUID = 8752989973624459604L;
/*    */ 
/*    */   public ProtocolEncoderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProtocolEncoderException(String message)
/*    */   {
/* 42 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProtocolEncoderException(Throwable cause)
/*    */   {
/* 49 */     super(cause);
/*    */   }
/*    */ 
/*    */   public ProtocolEncoderException(String message, Throwable cause)
/*    */   {
/* 57 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolEncoderException
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ public class RecoverableProtocolDecoderException extends ProtocolDecoderException
/*    */ {
/*    */   private static final long serialVersionUID = -8172624045024880678L;
/*    */ 
/*    */   public RecoverableProtocolDecoderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RecoverableProtocolDecoderException(String message)
/*    */   {
/* 54 */     super(message);
/*    */   }
/*    */ 
/*    */   public RecoverableProtocolDecoderException(Throwable cause) {
/* 58 */     super(cause);
/*    */   }
/*    */ 
/*    */   public RecoverableProtocolDecoderException(String message, Throwable cause) {
/* 62 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.codec.RecoverableProtocolDecoderException
 * JD-Core Version:    0.6.0
 */
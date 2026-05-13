/*    */ package org.apache.mina.filter.codec;
/*    */ 
/*    */ public class ProtocolDecoderException extends ProtocolCodecException
/*    */ {
/*    */   private static final long serialVersionUID = 3545799879533408565L;
/*    */   private String hexdump;
/*    */ 
/*    */   public ProtocolDecoderException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ProtocolDecoderException(String message)
/*    */   {
/* 47 */     super(message);
/*    */   }
/*    */ 
/*    */   public ProtocolDecoderException(Throwable cause)
/*    */   {
/* 54 */     super(cause);
/*    */   }
/*    */ 
/*    */   public ProtocolDecoderException(String message, Throwable cause)
/*    */   {
/* 62 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 70 */     String message = super.getMessage();
/*    */ 
/* 72 */     if (message == null) {
/* 73 */       message = "";
/*    */     }
/*    */ 
/* 76 */     if (this.hexdump != null) {
/* 77 */       return message + (message.length() > 0 ? " " : "") + "(Hexdump: " + this.hexdump + ')';
/*    */     }
/*    */ 
/* 80 */     return message;
/*    */   }
/*    */ 
/*    */   public String getHexdump()
/*    */   {
/* 88 */     return this.hexdump;
/*    */   }
/*    */ 
/*    */   public void setHexdump(String hexdump)
/*    */   {
/* 95 */     if (this.hexdump != null) {
/* 96 */       throw new IllegalStateException("Hexdump cannot be set more than once.");
/*    */     }
/*    */ 
/* 99 */     this.hexdump = hexdump;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.codec.ProtocolDecoderException
 * JD-Core Version:    0.6.0
 */
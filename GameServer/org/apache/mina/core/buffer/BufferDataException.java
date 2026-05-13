/*    */ package org.apache.mina.core.buffer;
/*    */ 
/*    */ public class BufferDataException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -4138189188602563502L;
/*    */ 
/*    */   public BufferDataException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public BufferDataException(String message)
/*    */   {
/* 38 */     super(message);
/*    */   }
/*    */ 
/*    */   public BufferDataException(String message, Throwable cause) {
/* 42 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public BufferDataException(Throwable cause) {
/* 46 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.core.buffer.BufferDataException
 * JD-Core Version:    0.6.0
 */
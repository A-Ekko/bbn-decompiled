/*    */ package org.apache.mina.core;
/*    */ 
/*    */ public class RuntimeIoException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 9029092241311939548L;
/*    */ 
/*    */   public RuntimeIoException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RuntimeIoException(String message)
/*    */   {
/* 42 */     super(message);
/*    */   }
/*    */ 
/*    */   public RuntimeIoException(String message, Throwable cause) {
/* 46 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public RuntimeIoException(Throwable cause) {
/* 50 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.RuntimeIoException
 * JD-Core Version:    0.6.0
 */
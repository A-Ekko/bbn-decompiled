/*    */ package org.apache.mina.filter.keepalive;
/*    */ 
/*    */ public class KeepAliveRequestTimeoutException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -1985092764656546558L;
/*    */ 
/*    */   public KeepAliveRequestTimeoutException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public KeepAliveRequestTimeoutException(String message, Throwable cause)
/*    */   {
/* 38 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public KeepAliveRequestTimeoutException(String message) {
/* 42 */     super(message);
/*    */   }
/*    */ 
/*    */   public KeepAliveRequestTimeoutException(Throwable cause) {
/* 46 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutException
 * JD-Core Version:    0.6.0
 */
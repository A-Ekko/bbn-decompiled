/*    */ package org.apache.commons.logging;
/*    */ 
/*    */ public class LogConfigurationException extends RuntimeException
/*    */ {
/* 84 */   protected Throwable cause = null;
/*    */ 
/*    */   public LogConfigurationException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LogConfigurationException(String message)
/*    */   {
/* 49 */     super(message);
/*    */   }
/*    */ 
/*    */   public LogConfigurationException(Throwable cause)
/*    */   {
/* 62 */     this(cause == null ? null : cause.toString(), cause);
/*    */   }
/*    */ 
/*    */   public LogConfigurationException(String message, Throwable cause)
/*    */   {
/* 75 */     super(message + " (Caused by " + cause + ")");
/* 76 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public Throwable getCause()
/*    */   {
/* 92 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.LogConfigurationException
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.core.filterchain;
/*    */ 
/*    */ public class IoFilterLifeCycleException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = -5542098881633506449L;
/*    */ 
/*    */   public IoFilterLifeCycleException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public IoFilterLifeCycleException(String message)
/*    */   {
/* 38 */     super(message);
/*    */   }
/*    */ 
/*    */   public IoFilterLifeCycleException(String message, Throwable cause) {
/* 42 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public IoFilterLifeCycleException(Throwable cause) {
/* 46 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.filterchain.IoFilterLifeCycleException
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.filter.reqres;
/*    */ 
/*    */ public class RequestTimeoutException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 5546784978950631652L;
/*    */   private final Request request;
/*    */ 
/*    */   public RequestTimeoutException(Request request)
/*    */   {
/* 39 */     if (request == null) {
/* 40 */       throw new NullPointerException("request");
/*    */     }
/* 42 */     this.request = request;
/*    */   }
/*    */ 
/*    */   public RequestTimeoutException(Request request, String s)
/*    */   {
/* 49 */     super(s);
/* 50 */     if (request == null) {
/* 51 */       throw new NullPointerException("request");
/*    */     }
/* 53 */     this.request = request;
/*    */   }
/*    */ 
/*    */   public RequestTimeoutException(Request request, String message, Throwable cause)
/*    */   {
/* 61 */     super(message);
/* 62 */     initCause(cause);
/* 63 */     if (request == null) {
/* 64 */       throw new NullPointerException("request");
/*    */     }
/* 66 */     this.request = request;
/*    */   }
/*    */ 
/*    */   public RequestTimeoutException(Request request, Throwable cause)
/*    */   {
/* 73 */     initCause(cause);
/* 74 */     if (request == null) {
/* 75 */       throw new NullPointerException("request");
/*    */     }
/* 77 */     this.request = request;
/*    */   }
/*    */ 
/*    */   public Request getRequest()
/*    */   {
/* 84 */     return this.request;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.reqres.RequestTimeoutException
 * JD-Core Version:    0.6.0
 */
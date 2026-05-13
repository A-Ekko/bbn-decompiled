/*    */ package org.apache.mina.core.write;
/*    */ 
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class WriteTimeoutException extends WriteException
/*    */ {
/*    */   private static final long serialVersionUID = 3906931157944579121L;
/*    */ 
/*    */   public WriteTimeoutException(Collection<WriteRequest> requests, String message, Throwable cause)
/*    */   {
/* 39 */     super(requests, message, cause);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(Collection<WriteRequest> requests, String s) {
/* 43 */     super(requests, s);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(Collection<WriteRequest> requests, Throwable cause)
/*    */   {
/* 48 */     super(requests, cause);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(Collection<WriteRequest> requests) {
/* 52 */     super(requests);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(WriteRequest request, String message, Throwable cause)
/*    */   {
/* 57 */     super(request, message, cause);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(WriteRequest request, String s) {
/* 61 */     super(request, s);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(WriteRequest request, Throwable cause) {
/* 65 */     super(request, cause);
/*    */   }
/*    */ 
/*    */   public WriteTimeoutException(WriteRequest request) {
/* 69 */     super(request);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.write.WriteTimeoutException
 * JD-Core Version:    0.6.0
 */
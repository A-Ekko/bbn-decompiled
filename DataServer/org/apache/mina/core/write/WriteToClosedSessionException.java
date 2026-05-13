/*    */ package org.apache.mina.core.write;
/*    */ 
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class WriteToClosedSessionException extends WriteException
/*    */ {
/*    */   private static final long serialVersionUID = 5550204573739301393L;
/*    */ 
/*    */   public WriteToClosedSessionException(Collection<WriteRequest> requests, String message, Throwable cause)
/*    */   {
/* 38 */     super(requests, message, cause);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(Collection<WriteRequest> requests, String s)
/*    */   {
/* 43 */     super(requests, s);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(Collection<WriteRequest> requests, Throwable cause)
/*    */   {
/* 48 */     super(requests, cause);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(Collection<WriteRequest> requests) {
/* 52 */     super(requests);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(WriteRequest request, String message, Throwable cause)
/*    */   {
/* 57 */     super(request, message, cause);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(WriteRequest request, String s) {
/* 61 */     super(request, s);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(WriteRequest request, Throwable cause) {
/* 65 */     super(request, cause);
/*    */   }
/*    */ 
/*    */   public WriteToClosedSessionException(WriteRequest request) {
/* 69 */     super(request);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.write.WriteToClosedSessionException
 * JD-Core Version:    0.6.0
 */
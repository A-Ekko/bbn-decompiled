/*    */ package org.apache.mina.core.write;
/*    */ 
/*    */ import java.util.Collection;
/*    */ 
/*    */ public class NothingWrittenException extends WriteException
/*    */ {
/*    */   private static final long serialVersionUID = -6331979307737691005L;
/*    */ 
/*    */   public NothingWrittenException(Collection<WriteRequest> requests, String message, Throwable cause)
/*    */   {
/* 38 */     super(requests, message, cause);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(Collection<WriteRequest> requests, String s) {
/* 42 */     super(requests, s);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(Collection<WriteRequest> requests, Throwable cause)
/*    */   {
/* 47 */     super(requests, cause);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(Collection<WriteRequest> requests) {
/* 51 */     super(requests);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(WriteRequest request, String message, Throwable cause)
/*    */   {
/* 56 */     super(request, message, cause);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(WriteRequest request, String s) {
/* 60 */     super(request, s);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(WriteRequest request, Throwable cause) {
/* 64 */     super(request, cause);
/*    */   }
/*    */ 
/*    */   public NothingWrittenException(WriteRequest request) {
/* 68 */     super(request);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.write.NothingWrittenException
 * JD-Core Version:    0.6.0
 */
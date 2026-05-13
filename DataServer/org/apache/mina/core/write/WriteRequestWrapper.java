/*    */ package org.apache.mina.core.write;
/*    */ 
/*    */ import java.net.SocketAddress;
/*    */ import org.apache.mina.core.future.WriteFuture;
/*    */ 
/*    */ public class WriteRequestWrapper
/*    */   implements WriteRequest
/*    */ {
/*    */   private final WriteRequest parentRequest;
/*    */ 
/*    */   public WriteRequestWrapper(WriteRequest parentRequest)
/*    */   {
/* 40 */     if (parentRequest == null) {
/* 41 */       throw new NullPointerException("parentRequest");
/*    */     }
/* 43 */     this.parentRequest = parentRequest;
/*    */   }
/*    */ 
/*    */   public SocketAddress getDestination() {
/* 47 */     return this.parentRequest.getDestination();
/*    */   }
/*    */ 
/*    */   public WriteFuture getFuture() {
/* 51 */     return this.parentRequest.getFuture();
/*    */   }
/*    */ 
/*    */   public Object getMessage() {
/* 55 */     return this.parentRequest.getMessage();
/*    */   }
/*    */ 
/*    */   public WriteRequest getOriginalRequest() {
/* 59 */     return this.parentRequest.getOriginalRequest();
/*    */   }
/*    */ 
/*    */   public WriteRequest getParentRequest()
/*    */   {
/* 66 */     return this.parentRequest;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 71 */     if (getDestination() == null) {
/* 72 */       return getMessage().toString();
/*    */     }
/* 74 */     return getMessage().toString() + " => " + getDestination();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.core.write.WriteRequestWrapper
 * JD-Core Version:    0.6.0
 */
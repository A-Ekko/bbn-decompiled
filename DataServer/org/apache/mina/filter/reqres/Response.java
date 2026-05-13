/*    */ package org.apache.mina.filter.reqres;
/*    */ 
/*    */ public class Response
/*    */ {
/*    */   private final Request request;
/*    */   private final ResponseType type;
/*    */   private final Object message;
/*    */ 
/*    */   public Response(Request request, Object message, ResponseType type)
/*    */   {
/* 36 */     if (request == null) {
/* 37 */       throw new NullPointerException("request");
/*    */     }
/*    */ 
/* 40 */     if (message == null) {
/* 41 */       throw new NullPointerException("message");
/*    */     }
/*    */ 
/* 44 */     if (type == null) {
/* 45 */       throw new NullPointerException("type");
/*    */     }
/*    */ 
/* 48 */     this.request = request;
/* 49 */     this.type = type;
/* 50 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public Request getRequest() {
/* 54 */     return this.request;
/*    */   }
/*    */ 
/*    */   public ResponseType getType() {
/* 58 */     return this.type;
/*    */   }
/*    */ 
/*    */   public Object getMessage() {
/* 62 */     return this.message;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 67 */     return getRequest().getId().hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 72 */     if (o == this) {
/* 73 */       return true;
/*    */     }
/*    */ 
/* 76 */     if (o == null) {
/* 77 */       return false;
/*    */     }
/*    */ 
/* 80 */     if (!(o instanceof Response)) {
/* 81 */       return false;
/*    */     }
/*    */ 
/* 84 */     Response that = (Response)o;
/* 85 */     if (!getRequest().equals(that.getRequest())) {
/* 86 */       return false;
/*    */     }
/*    */ 
/* 89 */     return getType().equals(that.getType());
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 94 */     return "response: { requestId=" + getRequest().getId() + ", type=" + getType() + ", message=" + getMessage() + " }";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.filter.reqres.Response
 * JD-Core Version:    0.6.0
 */
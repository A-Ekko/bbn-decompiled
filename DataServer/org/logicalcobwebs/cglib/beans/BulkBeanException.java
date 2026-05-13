/*    */ package org.logicalcobwebs.cglib.beans;
/*    */ 
/*    */ public class BulkBeanException extends RuntimeException
/*    */ {
/*    */   private int index;
/*    */   private Throwable cause;
/*    */ 
/*    */   public BulkBeanException(String message, int index)
/*    */   {
/* 26 */     super(message);
/* 27 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public BulkBeanException(Throwable cause, int index) {
/* 31 */     super(cause.getMessage());
/* 32 */     this.index = index;
/* 33 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public int getIndex() {
/* 37 */     return this.index;
/*    */   }
/*    */ 
/*    */   public Throwable getCause() {
/* 41 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.cglib.beans.BulkBeanException
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.log4j.config;
/*    */ 
/*    */ public class PropertySetterException extends Exception
/*    */ {
/*    */   protected Throwable rootCause;
/*    */ 
/*    */   public PropertySetterException(String msg)
/*    */   {
/* 22 */     super(msg);
/*    */   }
/*    */ 
/*    */   public PropertySetterException(Throwable rootCause)
/*    */   {
/* 29 */     this.rootCause = rootCause;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 37 */     String msg = super.getMessage();
/* 38 */     if ((msg == null) && (this.rootCause != null)) {
/* 39 */       msg = this.rootCause.getMessage();
/*    */     }
/* 41 */     return msg;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.config.PropertySetterException
 * JD-Core Version:    0.6.0
 */
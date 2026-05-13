/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ThrowableInformation
/*    */   implements Serializable
/*    */ {
/*    */   static final long serialVersionUID = -4748765566864322735L;
/*    */   private transient Throwable throwable;
/*    */   private String[] rep;
/*    */ 
/*    */   public ThrowableInformation(Throwable throwable)
/*    */   {
/* 36 */     this.throwable = throwable;
/*    */   }
/*    */ 
/*    */   public Throwable getThrowable()
/*    */   {
/* 41 */     return this.throwable;
/*    */   }
/*    */ 
/*    */   public String[] getThrowableStrRep()
/*    */   {
/* 46 */     if (this.rep != null) {
/* 47 */       return (String[])this.rep.clone();
/*    */     }
/* 49 */     VectorWriter vw = new VectorWriter();
/* 50 */     this.throwable.printStackTrace(vw);
/* 51 */     this.rep = vw.toStringArray();
/* 52 */     return this.rep;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.ThrowableInformation
 * JD-Core Version:    0.6.0
 */
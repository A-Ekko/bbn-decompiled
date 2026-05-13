/*    */ package org.logicalcobwebs.concurrent;
/*    */ 
/*    */ public class TimeoutException extends InterruptedException
/*    */ {
/*    */   public final long duration;
/*    */ 
/*    */   public TimeoutException(long time)
/*    */   {
/* 38 */     this.duration = time;
/*    */   }
/*    */ 
/*    */   public TimeoutException(long time, String message)
/*    */   {
/* 46 */     super(message);
/* 47 */     this.duration = time;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.concurrent.TimeoutException
 * JD-Core Version:    0.6.0
 */
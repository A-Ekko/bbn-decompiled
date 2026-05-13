/*    */ package org.apache.mina.util;
/*    */ 
/*    */ public abstract class ExceptionMonitor
/*    */ {
/* 39 */   private static ExceptionMonitor instance = new DefaultExceptionMonitor();
/*    */ 
/*    */   public static ExceptionMonitor getInstance()
/*    */   {
/* 45 */     return instance;
/*    */   }
/*    */ 
/*    */   public static void setInstance(ExceptionMonitor monitor)
/*    */   {
/* 56 */     if (monitor == null) {
/* 57 */       monitor = new DefaultExceptionMonitor();
/*    */     }
/* 59 */     instance = monitor;
/*    */   }
/*    */ 
/*    */   public abstract void exceptionCaught(Throwable paramThrowable);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.ExceptionMonitor
 * JD-Core Version:    0.6.0
 */
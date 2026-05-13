/*    */ package org.apache.mina.util;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class DefaultExceptionMonitor extends ExceptionMonitor
/*    */ {
/* 37 */   private final Logger log = LoggerFactory.getLogger(DefaultExceptionMonitor.class);
/*    */ 
/*    */   public void exceptionCaught(Throwable cause)
/*    */   {
/* 42 */     if ((cause instanceof Error)) {
/* 43 */       throw ((Error)cause);
/*    */     }
/* 45 */     this.log.warn("Unexpected exception.", cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.util.DefaultExceptionMonitor
 * JD-Core Version:    0.6.0
 */
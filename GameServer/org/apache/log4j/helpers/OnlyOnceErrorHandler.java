/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import org.apache.log4j.Appender;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.spi.ErrorHandler;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class OnlyOnceErrorHandler
/*    */   implements ErrorHandler
/*    */ {
/* 41 */   final String WARN_PREFIX = "log4j warning: ";
/* 42 */   final String ERROR_PREFIX = "log4j error: ";
/*    */ 
/* 44 */   boolean firstTime = true;
/*    */ 
/*    */   public void setLogger(Logger logger)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void error(String message, Exception e, int errorCode)
/*    */   {
/* 68 */     error(message, e, errorCode, null);
/*    */   }
/*    */ 
/*    */   public void error(String message, Exception e, int errorCode, LoggingEvent event)
/*    */   {
/* 77 */     if (this.firstTime) {
/* 78 */       LogLog.error(message, e);
/* 79 */       this.firstTime = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 90 */     if (this.firstTime) {
/* 91 */       LogLog.error(message);
/* 92 */       this.firstTime = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setAppender(Appender appender)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void setBackupAppender(Appender appender)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.helpers.OnlyOnceErrorHandler
 * JD-Core Version:    0.6.0
 */
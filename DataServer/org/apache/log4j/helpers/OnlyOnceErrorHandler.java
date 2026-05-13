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
/* 32 */   final String WARN_PREFIX = "log4j warning: ";
/* 33 */   final String ERROR_PREFIX = "log4j error: ";
/*    */ 
/* 35 */   boolean firstTime = true;
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
/* 59 */     error(message, e, errorCode, null);
/*    */   }
/*    */ 
/*    */   public void error(String message, Exception e, int errorCode, LoggingEvent event)
/*    */   {
/* 68 */     if (this.firstTime) {
/* 69 */       LogLog.error(message, e);
/* 70 */       this.firstTime = false;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void error(String message)
/*    */   {
/* 81 */     if (this.firstTime) {
/* 82 */       LogLog.error(message);
/* 83 */       this.firstTime = false;
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

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.OnlyOnceErrorHandler
 * JD-Core Version:    0.6.0
 */
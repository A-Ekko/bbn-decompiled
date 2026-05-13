/*    */ package org.apache.log4j.lf5;
/*    */ 
/*    */ import org.apache.log4j.spi.ThrowableInformation;
/*    */ 
/*    */ public class Log4JLogRecord extends LogRecord
/*    */ {
/*    */   public boolean isSevereLevel()
/*    */   {
/* 58 */     boolean isSevere = false;
/*    */ 
/* 60 */     if ((LogLevel.ERROR.equals(getLevel())) || (LogLevel.FATAL.equals(getLevel())))
/*    */     {
/* 62 */       isSevere = true;
/*    */     }
/*    */ 
/* 65 */     return isSevere;
/*    */   }
/*    */ 
/*    */   public void setThrownStackTrace(ThrowableInformation throwableInfo)
/*    */   {
/* 79 */     String[] stackTraceArray = throwableInfo.getThrowableStrRep();
/*    */ 
/* 81 */     StringBuffer stackTrace = new StringBuffer();
/*    */ 
/* 84 */     for (int i = 0; i < stackTraceArray.length; i++) {
/* 85 */       String nextLine = stackTraceArray[i] + "\n";
/* 86 */       stackTrace.append(nextLine);
/*    */     }
/*    */ 
/* 89 */     this._thrownStackTrace = stackTrace.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.Log4JLogRecord
 * JD-Core Version:    0.6.0
 */
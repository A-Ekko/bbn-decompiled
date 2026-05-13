/*    */ package com.mysql.jdbc.log;
/*    */ 
/*    */ public class NullLogger
/*    */   implements Log
/*    */ {
/*    */   public NullLogger(String instanceName)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean isDebugEnabled()
/*    */   {
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isErrorEnabled()
/*    */   {
/* 59 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isFatalEnabled()
/*    */   {
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isInfoEnabled()
/*    */   {
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isTraceEnabled()
/*    */   {
/* 83 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean isWarnEnabled()
/*    */   {
/* 91 */     return false;
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logDebug(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logError(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logFatal(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logInfo(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logTrace(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void logWarn(Object msg, Throwable thrown)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.log.NullLogger
 * JD-Core Version:    0.6.0
 */
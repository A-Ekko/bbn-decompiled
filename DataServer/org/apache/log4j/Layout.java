/*    */ package org.apache.log4j;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ import org.apache.log4j.spi.OptionHandler;
/*    */ 
/*    */ public abstract class Layout
/*    */   implements OptionHandler
/*    */ {
/* 24 */   public static final String LINE_SEP = System.getProperty("line.separator");
/* 25 */   public static final int LINE_SEP_LEN = LINE_SEP.length();
/*    */ 
/*    */   public abstract String format(LoggingEvent paramLoggingEvent);
/*    */ 
/*    */   public String getContentType()
/*    */   {
/* 41 */     return "text/plain";
/*    */   }
/*    */ 
/*    */   public String getHeader()
/*    */   {
/* 49 */     return null;
/*    */   }
/*    */ 
/*    */   public String getFooter()
/*    */   {
/* 57 */     return null;
/*    */   }
/*    */ 
/*    */   public abstract boolean ignoresThrowable();
/*    */ 
/*    */   public abstract void activateOptions();
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Layout
 * JD-Core Version:    0.6.0
 */
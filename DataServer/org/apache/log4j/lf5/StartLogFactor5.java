/*    */ package org.apache.log4j.lf5;
/*    */ 
/*    */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*    */ 
/*    */ public class StartLogFactor5
/*    */ {
/*    */   public static final void main(String[] args)
/*    */   {
/* 48 */     LogBrokerMonitor monitor = new LogBrokerMonitor(LogLevel.getLog4JLevels());
/*    */ 
/* 51 */     monitor.setFrameSize(LF5Appender.getDefaultMonitorWidth(), LF5Appender.getDefaultMonitorHeight());
/*    */ 
/* 53 */     monitor.setFontSize(12);
/* 54 */     monitor.show();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.StartLogFactor5
 * JD-Core Version:    0.6.0
 */
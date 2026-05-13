/*    */ package org.apache.log4j.lf5;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*    */ 
/*    */ public class AppenderFinalizer
/*    */ {
/* 32 */   protected LogBrokerMonitor _defaultMonitor = null;
/*    */ 
/*    */   public AppenderFinalizer(LogBrokerMonitor defaultMonitor)
/*    */   {
/* 43 */     this._defaultMonitor = defaultMonitor;
/*    */   }
/*    */ 
/*    */   protected void finalize()
/*    */     throws Throwable
/*    */   {
/* 57 */     System.out.println("Disposing of the default LogBrokerMonitor instance");
/* 58 */     this._defaultMonitor.dispose();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.AppenderFinalizer
 * JD-Core Version:    0.6.0
 */
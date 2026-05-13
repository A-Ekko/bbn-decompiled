/*    */ package org.apache.log4j.lf5.util;
/*    */ 
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
/*    */ import org.apache.log4j.lf5.LogLevel;
/*    */ import org.apache.log4j.lf5.LogRecord;
/*    */ 
/*    */ public class AdapterLogRecord extends LogRecord
/*    */ {
/* 36 */   private static LogLevel severeLevel = null;
/*    */ 
/* 38 */   private static StringWriter sw = new StringWriter();
/* 39 */   private static PrintWriter pw = new PrintWriter(sw);
/*    */ 
/*    */   public void setCategory(String category)
/*    */   {
/* 52 */     super.setCategory(category);
/* 53 */     super.setLocation(getLocationInfo(category));
/*    */   }
/*    */ 
/*    */   public boolean isSevereLevel() {
/* 57 */     if (severeLevel == null) return false;
/* 58 */     return severeLevel.equals(getLevel());
/*    */   }
/*    */ 
/*    */   public static void setSevereLevel(LogLevel level) {
/* 62 */     severeLevel = level;
/*    */   }
/*    */ 
/*    */   public static LogLevel getSevereLevel() {
/* 66 */     return severeLevel;
/*    */   }
/*    */ 
/*    */   protected String getLocationInfo(String category)
/*    */   {
/* 73 */     String stackTrace = stackTraceToString(new Throwable());
/* 74 */     String line = parseLine(stackTrace, category);
/* 75 */     return line;
/*    */   }
/*    */ 
/*    */   protected String stackTraceToString(Throwable t) {
/* 79 */     String s = null;
/*    */ 
/* 81 */     synchronized (sw) {
/* 82 */       t.printStackTrace(pw);
/* 83 */       s = sw.toString();
/* 84 */       sw.getBuffer().setLength(0);
/*    */     }
/*    */ 
/* 87 */     return s;
/*    */   }
/*    */ 
/*    */   protected String parseLine(String trace, String category) {
/* 91 */     int index = trace.indexOf(category);
/* 92 */     if (index == -1) return null;
/* 93 */     trace = trace.substring(index);
/* 94 */     trace = trace.substring(0, trace.indexOf(")") + 1);
/* 95 */     return trace;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.AdapterLogRecord
 * JD-Core Version:    0.6.0
 */
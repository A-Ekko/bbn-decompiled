/*    */ package org.apache.log4j;
/*    */ 
/*    */ import java.io.OutputStreamWriter;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ public class ConsoleAppender extends WriterAppender
/*    */ {
/*    */   public static final String SYSTEM_OUT = "System.out";
/*    */   public static final String SYSTEM_ERR = "System.err";
/* 25 */   protected String target = "System.out";
/*    */ 
/*    */   public ConsoleAppender()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ConsoleAppender(Layout layout)
/*    */   {
/* 34 */     this(layout, "System.out");
/*    */   }
/*    */ 
/*    */   public ConsoleAppender(Layout layout, String target) {
/* 38 */     this.layout = layout;
/*    */ 
/* 40 */     if ("System.out".equals(target))
/* 41 */       setWriter(new OutputStreamWriter(System.out));
/* 42 */     else if ("System.err".equalsIgnoreCase(target))
/* 43 */       setWriter(new OutputStreamWriter(System.err));
/*    */     else
/* 45 */       targetWarn(target);
/*    */   }
/*    */ 
/*    */   public void setTarget(String value)
/*    */   {
/* 56 */     String v = value.trim();
/*    */ 
/* 58 */     if ("System.out".equalsIgnoreCase(v))
/* 59 */       this.target = "System.out";
/* 60 */     else if ("System.err".equalsIgnoreCase(v))
/* 61 */       this.target = "System.err";
/*    */     else
/* 63 */       targetWarn(value);
/*    */   }
/*    */ 
/*    */   public String getTarget()
/*    */   {
/* 75 */     return this.target;
/*    */   }
/*    */ 
/*    */   void targetWarn(String val) {
/* 79 */     LogLog.warn("[" + val + "] should be System.out or System.err.");
/* 80 */     LogLog.warn("Using previously set target, System.out by default.");
/*    */   }
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/* 85 */     if (this.target.equals("System.out"))
/* 86 */       setWriter(new OutputStreamWriter(System.out));
/*    */     else
/* 88 */       setWriter(new OutputStreamWriter(System.err));
/*    */   }
/*    */ 
/*    */   protected final void closeWriter()
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.ConsoleAppender
 * JD-Core Version:    0.6.0
 */
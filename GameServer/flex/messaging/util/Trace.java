/*    */ package flex.messaging.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class Trace
/*    */ {
/* 47 */   public static final boolean config = System.getProperty("trace.config") != null;
/*    */ 
/* 49 */   public static final boolean amf = System.getProperty("trace.amf") != null;
/* 50 */   public static final boolean remote = (amf) || (System.getProperty("trace.remote") != null);
/* 51 */   public static final boolean ssl = System.getProperty("trace.ssl") != null;
/*    */ 
/* 53 */   public static final boolean rtmp = System.getProperty("trace.rtmp") != null;
/* 54 */   public static final boolean command = (rtmp) || (System.getProperty("trace.command") != null);
/* 55 */   public static final boolean error = (rtmp) || (System.getProperty("trace.error") != null);
/* 56 */   public static final boolean message = (rtmp) || (System.getProperty("trace.message") != null);
/* 57 */   public static final boolean resolve = (rtmp) || (System.getProperty("trace.resolve") != null);
/* 58 */   public static final boolean transport = (rtmp) || (System.getProperty("trace.transport") != null);
/* 59 */   public static final boolean ack = (rtmp) || (System.getProperty("trace.ack") != null);
/* 60 */   public static final boolean io = (rtmp) || (System.getProperty("trace.io") != null);
/* 61 */   public static final boolean threadpool = (rtmp) || (System.getProperty("trace.threadpool") != null);
/*    */ 
/* 64 */   public static final boolean caller = System.getProperty("trace.caller") != null;
/*    */ 
/* 66 */   public static final String stackPrefix = System.getProperty("trace.stackPrefix");
/*    */ 
/* 69 */   public static int stackLines = 0;
/*    */   public static final boolean timeStamp;
/*    */ 
/*    */   public static void trace(String str)
/*    */   {
/* 85 */     if (timeStamp) {
/* 86 */       System.err.print(new Date());
/*    */     }
/* 88 */     if (caller) {
/* 89 */       System.err.print(ExceptionUtil.getCallAt(new Throwable(), 1) + " ");
/*    */     }
/* 91 */     System.err.println(str);
/*    */ 
/* 93 */     if (stackLines > 0)
/* 94 */       System.err.println(ExceptionUtil.getStackTraceLines(new Throwable(), stackLines));
/* 95 */     else if (stackPrefix != null)
/* 96 */       System.err.println(ExceptionUtil.getStackTraceUpTo(new Throwable(), stackPrefix));
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 72 */       stackLines = Integer.parseInt(System.getProperty("trace.stackLines"));
/*    */     }
/*    */     catch (NumberFormatException e)
/*    */     {
/*    */     }
/* 77 */     timeStamp = System.getProperty("trace.timeStamp") != null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.Trace
 * JD-Core Version:    0.6.0
 */
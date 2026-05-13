/*    */ package org.slf4j.helpers;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   public static final void reportFailure(String msg, Throwable t)
/*    */   {
/* 37 */     System.err.println(msg);
/* 38 */     System.err.println("Reported exception:");
/* 39 */     t.printStackTrace();
/*    */   }
/*    */ 
/*    */   public static final void reportFailure(String msg) {
/* 43 */     System.err.println("SLF4J: " + msg);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.helpers.Util
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class LogLog
/*     */ {
/*     */   public static final String DEBUG_KEY = "log4j.debug";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
/*  51 */   protected static boolean debugEnabled = false;
/*     */ 
/*  56 */   private static boolean quietMode = false;
/*     */   private static final String PREFIX = "log4j: ";
/*     */   private static final String ERR_PREFIX = "log4j:ERROR ";
/*     */   private static final String WARN_PREFIX = "log4j:WARN ";
/*     */ 
/*     */   public static void setInternalDebugging(boolean enabled)
/*     */   {
/*  80 */     debugEnabled = enabled;
/*     */   }
/*     */ 
/*     */   public static void debug(String msg)
/*     */   {
/*  90 */     if ((debugEnabled) && (!quietMode))
/*  91 */       System.out.println("log4j: " + msg);
/*     */   }
/*     */ 
/*     */   public static void debug(String msg, Throwable t)
/*     */   {
/* 102 */     if ((debugEnabled) && (!quietMode)) {
/* 103 */       System.out.println("log4j: " + msg);
/* 104 */       if (t != null)
/* 105 */         t.printStackTrace(System.out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void error(String msg)
/*     */   {
/* 118 */     if (quietMode)
/* 119 */       return;
/* 120 */     System.err.println("log4j:ERROR " + msg);
/*     */   }
/*     */ 
/*     */   public static void error(String msg, Throwable t)
/*     */   {
/* 131 */     if (quietMode) {
/* 132 */       return;
/*     */     }
/* 134 */     System.err.println("log4j:ERROR " + msg);
/* 135 */     if (t != null)
/* 136 */       t.printStackTrace();
/*     */   }
/*     */ 
/*     */   public static void setQuietMode(boolean quietMode)
/*     */   {
/* 149 */     quietMode = quietMode;
/*     */   }
/*     */ 
/*     */   public static void warn(String msg)
/*     */   {
/* 159 */     if (quietMode) {
/* 160 */       return;
/*     */     }
/* 162 */     System.err.println("log4j:WARN " + msg);
/*     */   }
/*     */ 
/*     */   public static void warn(String msg, Throwable t)
/*     */   {
/* 172 */     if (quietMode) {
/* 173 */       return;
/*     */     }
/* 175 */     System.err.println("log4j:WARN " + msg);
/* 176 */     if (t != null)
/* 177 */       t.printStackTrace();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  63 */     String key = OptionConverter.getSystemProperty("log4j.debug", null);
/*     */ 
/*  65 */     if (key == null) {
/*  66 */       key = OptionConverter.getSystemProperty("log4j.configDebug", null);
/*     */     }
/*     */ 
/*  69 */     if (key != null)
/*  70 */       debugEnabled = OptionConverter.toBoolean(key, true);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.LogLog
 * JD-Core Version:    0.6.0
 */
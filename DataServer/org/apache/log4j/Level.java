/*     */ package org.apache.log4j;
/*     */ 
/*     */ public class Level extends Priority
/*     */ {
/*  32 */   public static final Level OFF = new Level(2147483647, "OFF", 0);
/*     */ 
/*  38 */   public static final Level FATAL = new Level(50000, "FATAL", 0);
/*     */ 
/*  43 */   public static final Level ERROR = new Level(40000, "ERROR", 3);
/*     */ 
/*  48 */   public static final Level WARN = new Level(30000, "WARN", 4);
/*     */ 
/*  54 */   public static final Level INFO = new Level(20000, "INFO", 6);
/*     */ 
/*  60 */   public static final Level DEBUG = new Level(10000, "DEBUG", 7);
/*     */ 
/*  65 */   public static final Level ALL = new Level(-2147483648, "ALL", 7);
/*     */ 
/*     */   protected Level(int level, String levelStr, int syslogEquivalent)
/*     */   {
/*  72 */     super(level, levelStr, syslogEquivalent);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String sArg)
/*     */   {
/*  83 */     return toLevel(sArg, DEBUG);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(int val)
/*     */   {
/*  94 */     return toLevel(val, DEBUG);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(int val, Level defaultLevel)
/*     */   {
/* 104 */     switch (val) { case -2147483648:
/* 105 */       return ALL;
/*     */     case 10000:
/* 106 */       return DEBUG;
/*     */     case 20000:
/* 107 */       return INFO;
/*     */     case 30000:
/* 108 */       return WARN;
/*     */     case 40000:
/* 109 */       return ERROR;
/*     */     case 50000:
/* 110 */       return FATAL;
/*     */     case 2147483647:
/* 111 */       return OFF; }
/* 112 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String sArg, Level defaultLevel)
/*     */   {
/* 124 */     if (sArg == null) {
/* 125 */       return defaultLevel;
/*     */     }
/* 127 */     String s = sArg.toUpperCase();
/*     */ 
/* 129 */     if (s.equals("ALL")) return ALL;
/* 130 */     if (s.equals("DEBUG")) return DEBUG;
/*     */ 
/* 132 */     if (s.equals("INFO")) return INFO;
/* 133 */     if (s.equals("WARN")) return WARN;
/* 134 */     if (s.equals("ERROR")) return ERROR;
/* 135 */     if (s.equals("FATAL")) return FATAL;
/* 136 */     if (s.equals("OFF")) return OFF;
/* 137 */     return defaultLevel;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Level
 * JD-Core Version:    0.6.0
 */
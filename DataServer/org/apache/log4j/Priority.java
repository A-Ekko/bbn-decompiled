/*     */ package org.apache.log4j;
/*     */ 
/*     */ public class Priority
/*     */ {
/*     */   int level;
/*     */   String levelStr;
/*     */   int syslogEquivalent;
/*     */   public static final int OFF_INT = 2147483647;
/*     */   public static final int FATAL_INT = 50000;
/*     */   public static final int ERROR_INT = 40000;
/*     */   public static final int WARN_INT = 30000;
/*     */   public static final int INFO_INT = 20000;
/*     */   public static final int DEBUG_INT = 10000;
/*     */   public static final int ALL_INT = -2147483648;
/*  37 */   public static final Priority FATAL = new Level(50000, "FATAL", 0);
/*     */ 
/*  42 */   public static final Priority ERROR = new Level(40000, "ERROR", 3);
/*     */ 
/*  47 */   public static final Priority WARN = new Level(30000, "WARN", 4);
/*     */ 
/*  53 */   public static final Priority INFO = new Level(20000, "INFO", 6);
/*     */ 
/*  59 */   public static final Priority DEBUG = new Level(10000, "DEBUG", 7);
/*     */ 
/*     */   protected Priority(int level, String levelStr, int syslogEquivalent)
/*     */   {
/*  67 */     this.level = level;
/*  68 */     this.levelStr = levelStr;
/*  69 */     this.syslogEquivalent = syslogEquivalent;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  78 */     if ((o instanceof Priority)) {
/*  79 */       Priority r = (Priority)o;
/*  80 */       return this.level == r.level;
/*     */     }
/*  82 */     return false;
/*     */   }
/*     */ 
/*     */   public final int getSyslogEquivalent()
/*     */   {
/*  92 */     return this.syslogEquivalent;
/*     */   }
/*     */ 
/*     */   public boolean isGreaterOrEqual(Priority r)
/*     */   {
/* 108 */     return this.level >= r.level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority[] getAllPossiblePriorities()
/*     */   {
/* 120 */     return new Priority[] { FATAL, ERROR, Level.WARN, INFO, DEBUG };
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 131 */     return this.levelStr;
/*     */   }
/*     */ 
/*     */   public final int toInt()
/*     */   {
/* 140 */     return this.level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority toPriority(String sArg)
/*     */   {
/* 154 */     return Level.toLevel(sArg);
/*     */   }
/*     */ 
/*     */   public static Priority toPriority(int val)
/*     */   {
/* 165 */     return toPriority(val, DEBUG);
/*     */   }
/*     */ 
/*     */   public static Priority toPriority(int val, Priority defaultPriority)
/*     */   {
/* 175 */     return Level.toLevel(val, (Level)defaultPriority);
/*     */   }
/*     */ 
/*     */   public static Priority toPriority(String sArg, Priority defaultPriority)
/*     */   {
/* 186 */     return Level.toLevel(sArg, (Level)defaultPriority);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Priority
 * JD-Core Version:    0.6.0
 */
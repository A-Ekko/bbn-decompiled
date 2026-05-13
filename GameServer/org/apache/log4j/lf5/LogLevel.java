/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LogLevel
/*     */   implements Serializable
/*     */ {
/*  45 */   public static final LogLevel FATAL = new LogLevel("FATAL", 0);
/*  46 */   public static final LogLevel ERROR = new LogLevel("ERROR", 1);
/*  47 */   public static final LogLevel WARN = new LogLevel("WARN", 2);
/*  48 */   public static final LogLevel INFO = new LogLevel("INFO", 3);
/*  49 */   public static final LogLevel DEBUG = new LogLevel("DEBUG", 4);
/*     */ 
/*  52 */   public static final LogLevel SEVERE = new LogLevel("SEVERE", 1);
/*  53 */   public static final LogLevel WARNING = new LogLevel("WARNING", 2);
/*  54 */   public static final LogLevel CONFIG = new LogLevel("CONFIG", 4);
/*  55 */   public static final LogLevel FINE = new LogLevel("FINE", 5);
/*  56 */   public static final LogLevel FINER = new LogLevel("FINER", 6);
/*  57 */   public static final LogLevel FINEST = new LogLevel("FINEST", 7);
/*     */   protected String _label;
/*     */   protected int _precedence;
/*     */   private static LogLevel[] _log4JLevels;
/*     */   private static LogLevel[] _jdk14Levels;
/*     */   private static LogLevel[] _allDefaultLevels;
/*     */   private static Map _logLevelMap;
/*     */   private static Map _logLevelColorMap;
/*  72 */   private static Map _registeredLogLevelMap = new HashMap();
/*     */ 
/*     */   public LogLevel(String label, int precedence)
/*     */   {
/*  97 */     this._label = label;
/*  98 */     this._precedence = precedence;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 109 */     return this._label;
/*     */   }
/*     */ 
/*     */   public boolean encompasses(LogLevel level)
/*     */   {
/* 120 */     return level.getPrecedence() <= getPrecedence();
/*     */   }
/*     */ 
/*     */   public static LogLevel valueOf(String level)
/*     */     throws LogLevelFormatException
/*     */   {
/* 136 */     LogLevel logLevel = null;
/* 137 */     if (level != null) {
/* 138 */       level = level.trim().toUpperCase();
/* 139 */       logLevel = (LogLevel)_logLevelMap.get(level);
/*     */     }
/*     */ 
/* 143 */     if ((logLevel == null) && (_registeredLogLevelMap.size() > 0)) {
/* 144 */       logLevel = (LogLevel)_registeredLogLevelMap.get(level);
/*     */     }
/*     */ 
/* 147 */     if (logLevel == null) {
/* 148 */       StringBuffer buf = new StringBuffer();
/* 149 */       buf.append("Error while trying to parse (" + level + ") into");
/* 150 */       buf.append(" a LogLevel.");
/* 151 */       throw new LogLevelFormatException(buf.toString());
/*     */     }
/* 153 */     return logLevel;
/*     */   }
/*     */ 
/*     */   public static LogLevel register(LogLevel logLevel)
/*     */   {
/* 163 */     if (logLevel == null) return null;
/*     */ 
/* 166 */     if (_logLevelMap.get(logLevel.getLabel()) == null) {
/* 167 */       return (LogLevel)_registeredLogLevelMap.put(logLevel.getLabel(), logLevel);
/*     */     }
/*     */ 
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   public static void register(LogLevel[] logLevels) {
/* 174 */     if (logLevels != null)
/* 175 */       for (int i = 0; i < logLevels.length; i++)
/* 176 */         register(logLevels[i]);
/*     */   }
/*     */ 
/*     */   public static void register(List logLevels)
/*     */   {
/* 182 */     if (logLevels != null) {
/* 183 */       Iterator it = logLevels.iterator();
/* 184 */       while (it.hasNext())
/* 185 */         register((LogLevel)it.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 191 */     boolean equals = false;
/*     */ 
/* 193 */     if (((o instanceof LogLevel)) && 
/* 194 */       (getPrecedence() == ((LogLevel)o).getPrecedence()))
/*     */     {
/* 196 */       equals = true;
/*     */     }
/*     */ 
/* 201 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 205 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 209 */     return this._label;
/*     */   }
/*     */ 
/*     */   public void setLogLevelColorMap(LogLevel level, Color color)
/*     */   {
/* 215 */     _logLevelColorMap.remove(level);
/*     */ 
/* 217 */     if (color == null) {
/* 218 */       color = Color.black;
/*     */     }
/* 220 */     _logLevelColorMap.put(level, color);
/*     */   }
/*     */ 
/*     */   public static void resetLogLevelColorMap()
/*     */   {
/* 225 */     _logLevelColorMap.clear();
/*     */ 
/* 228 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/* 229 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ 
/*     */   public static List getLog4JLevels()
/*     */   {
/* 238 */     return Arrays.asList(_log4JLevels);
/*     */   }
/*     */ 
/*     */   public static List getJdk14Levels() {
/* 242 */     return Arrays.asList(_jdk14Levels);
/*     */   }
/*     */ 
/*     */   public static List getAllDefaultLevels() {
/* 246 */     return Arrays.asList(_allDefaultLevels);
/*     */   }
/*     */ 
/*     */   public static Map getLogLevelColorMap() {
/* 250 */     return _logLevelColorMap;
/*     */   }
/*     */ 
/*     */   protected int getPrecedence()
/*     */   {
/* 258 */     return this._precedence;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  78 */     _log4JLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG };
/*  79 */     _jdk14Levels = new LogLevel[] { SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  81 */     _allDefaultLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG, SEVERE, WARNING, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  84 */     _logLevelMap = new HashMap();
/*  85 */     for (int i = 0; i < _allDefaultLevels.length; i++) {
/*  86 */       _logLevelMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
/*     */     }
/*     */ 
/*  90 */     _logLevelColorMap = new HashMap();
/*  91 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/*  92 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.LogLevel
 * JD-Core Version:    0.6.0
 */
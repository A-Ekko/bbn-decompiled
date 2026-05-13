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
/*  36 */   public static final LogLevel FATAL = new LogLevel("FATAL", 0);
/*  37 */   public static final LogLevel ERROR = new LogLevel("ERROR", 1);
/*  38 */   public static final LogLevel WARN = new LogLevel("WARN", 2);
/*  39 */   public static final LogLevel INFO = new LogLevel("INFO", 3);
/*  40 */   public static final LogLevel DEBUG = new LogLevel("DEBUG", 4);
/*     */ 
/*  43 */   public static final LogLevel SEVERE = new LogLevel("SEVERE", 1);
/*  44 */   public static final LogLevel WARNING = new LogLevel("WARNING", 2);
/*  45 */   public static final LogLevel CONFIG = new LogLevel("CONFIG", 4);
/*  46 */   public static final LogLevel FINE = new LogLevel("FINE", 5);
/*  47 */   public static final LogLevel FINER = new LogLevel("FINER", 6);
/*  48 */   public static final LogLevel FINEST = new LogLevel("FINEST", 7);
/*     */   protected String _label;
/*     */   protected int _precedence;
/*     */   private static LogLevel[] _log4JLevels;
/*     */   private static LogLevel[] _jdk14Levels;
/*     */   private static LogLevel[] _allDefaultLevels;
/*     */   private static Map _logLevelMap;
/*     */   private static Map _logLevelColorMap;
/*  63 */   private static Map _registeredLogLevelMap = new HashMap();
/*     */ 
/*     */   public LogLevel(String label, int precedence)
/*     */   {
/*  88 */     this._label = label;
/*  89 */     this._precedence = precedence;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 100 */     return this._label;
/*     */   }
/*     */ 
/*     */   public boolean encompasses(LogLevel level)
/*     */   {
/* 111 */     return level.getPrecedence() <= getPrecedence();
/*     */   }
/*     */ 
/*     */   public static LogLevel valueOf(String level)
/*     */     throws LogLevelFormatException
/*     */   {
/* 127 */     LogLevel logLevel = null;
/* 128 */     if (level != null) {
/* 129 */       level = level.trim().toUpperCase();
/* 130 */       logLevel = (LogLevel)_logLevelMap.get(level);
/*     */     }
/*     */ 
/* 134 */     if ((logLevel == null) && (_registeredLogLevelMap.size() > 0)) {
/* 135 */       logLevel = (LogLevel)_registeredLogLevelMap.get(level);
/*     */     }
/*     */ 
/* 138 */     if (logLevel == null) {
/* 139 */       StringBuffer buf = new StringBuffer();
/* 140 */       buf.append("Error while trying to parse (" + level + ") into");
/* 141 */       buf.append(" a LogLevel.");
/* 142 */       throw new LogLevelFormatException(buf.toString());
/*     */     }
/* 144 */     return logLevel;
/*     */   }
/*     */ 
/*     */   public static LogLevel register(LogLevel logLevel)
/*     */   {
/* 154 */     if (logLevel == null) return null;
/*     */ 
/* 157 */     if (_logLevelMap.get(logLevel.getLabel()) == null) {
/* 158 */       return (LogLevel)_registeredLogLevelMap.put(logLevel.getLabel(), logLevel);
/*     */     }
/*     */ 
/* 161 */     return null;
/*     */   }
/*     */ 
/*     */   public static void register(LogLevel[] logLevels) {
/* 165 */     if (logLevels != null)
/* 166 */       for (int i = 0; i < logLevels.length; i++)
/* 167 */         register(logLevels[i]);
/*     */   }
/*     */ 
/*     */   public static void register(List logLevels)
/*     */   {
/* 173 */     if (logLevels != null) {
/* 174 */       Iterator it = logLevels.iterator();
/* 175 */       while (it.hasNext())
/* 176 */         register((LogLevel)it.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 182 */     boolean equals = false;
/*     */ 
/* 184 */     if (((o instanceof LogLevel)) && 
/* 185 */       (getPrecedence() == ((LogLevel)o).getPrecedence()))
/*     */     {
/* 187 */       equals = true;
/*     */     }
/*     */ 
/* 192 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 196 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 200 */     return this._label;
/*     */   }
/*     */ 
/*     */   public void setLogLevelColorMap(LogLevel level, Color color)
/*     */   {
/* 206 */     _logLevelColorMap.remove(level);
/*     */ 
/* 208 */     if (color == null) {
/* 209 */       color = Color.black;
/*     */     }
/* 211 */     _logLevelColorMap.put(level, color);
/*     */   }
/*     */ 
/*     */   public static void resetLogLevelColorMap()
/*     */   {
/* 216 */     _logLevelColorMap.clear();
/*     */ 
/* 219 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/* 220 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ 
/*     */   public static List getLog4JLevels()
/*     */   {
/* 229 */     return Arrays.asList(_log4JLevels);
/*     */   }
/*     */ 
/*     */   public static List getJdk14Levels() {
/* 233 */     return Arrays.asList(_jdk14Levels);
/*     */   }
/*     */ 
/*     */   public static List getAllDefaultLevels() {
/* 237 */     return Arrays.asList(_allDefaultLevels);
/*     */   }
/*     */ 
/*     */   public static Map getLogLevelColorMap() {
/* 241 */     return _logLevelColorMap;
/*     */   }
/*     */ 
/*     */   protected int getPrecedence()
/*     */   {
/* 249 */     return this._precedence;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  69 */     _log4JLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG };
/*  70 */     _jdk14Levels = new LogLevel[] { SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  72 */     _allDefaultLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG, SEVERE, WARNING, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  75 */     _logLevelMap = new HashMap();
/*  76 */     for (int i = 0; i < _allDefaultLevels.length; i++) {
/*  77 */       _logLevelMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
/*     */     }
/*     */ 
/*  81 */     _logLevelColorMap = new HashMap();
/*  82 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/*  83 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.LogLevel
 * JD-Core Version:    0.6.0
 */
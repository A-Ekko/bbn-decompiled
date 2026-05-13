/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ 
/*     */ public class LogMonitorAdapter
/*     */ {
/*     */   public static final int LOG4J_LOG_LEVELS = 0;
/*     */   public static final int JDK14_LOG_LEVELS = 1;
/*     */   private LogBrokerMonitor _logMonitor;
/*  40 */   private LogLevel _defaultLevel = null;
/*     */ 
/*     */   private LogMonitorAdapter(List userDefinedLevels)
/*     */   {
/*  48 */     this._defaultLevel = ((LogLevel)userDefinedLevels.get(0));
/*  49 */     this._logMonitor = new LogBrokerMonitor(userDefinedLevels);
/*     */ 
/*  51 */     this._logMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
/*     */ 
/*  53 */     this._logMonitor.setFontSize(12);
/*  54 */     this._logMonitor.show();
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(int loglevels)
/*     */   {
/*     */     LogMonitorAdapter adapter;
/*  69 */     if (loglevels == 1) {
/*  70 */       adapter = newInstance(LogLevel.getJdk14Levels());
/*  71 */       adapter.setDefaultLevel(LogLevel.FINEST);
/*  72 */       adapter.setSevereLevel(LogLevel.SEVERE);
/*     */     } else {
/*  74 */       adapter = newInstance(LogLevel.getLog4JLevels());
/*  75 */       adapter.setDefaultLevel(LogLevel.DEBUG);
/*  76 */       adapter.setSevereLevel(LogLevel.FATAL);
/*     */     }
/*  78 */     return adapter;
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(LogLevel[] userDefined)
/*     */   {
/*  90 */     if (userDefined == null) {
/*  91 */       return null;
/*     */     }
/*  93 */     return newInstance(Arrays.asList(userDefined));
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(List userDefinedLevels)
/*     */   {
/* 105 */     return new LogMonitorAdapter(userDefinedLevels);
/*     */   }
/*     */ 
/*     */   public void addMessage(LogRecord record)
/*     */   {
/* 114 */     this._logMonitor.addMessage(record);
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfRecords(int maxNumberOfRecords)
/*     */   {
/* 123 */     this._logMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
/*     */   }
/*     */ 
/*     */   public void setDefaultLevel(LogLevel level)
/*     */   {
/* 133 */     this._defaultLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getDefaultLevel()
/*     */   {
/* 142 */     return this._defaultLevel;
/*     */   }
/*     */ 
/*     */   public void setSevereLevel(LogLevel level)
/*     */   {
/* 151 */     AdapterLogRecord.setSevereLevel(level);
/*     */   }
/*     */ 
/*     */   public LogLevel getSevereLevel()
/*     */   {
/* 160 */     return AdapterLogRecord.getSevereLevel();
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, Throwable t, String NDC)
/*     */   {
/* 175 */     AdapterLogRecord record = new AdapterLogRecord();
/* 176 */     record.setCategory(category);
/* 177 */     record.setMessage(message);
/* 178 */     record.setNDC(NDC);
/* 179 */     record.setThrown(t);
/*     */ 
/* 181 */     if (level == null)
/* 182 */       record.setLevel(getDefaultLevel());
/*     */     else {
/* 184 */       record.setLevel(level);
/*     */     }
/*     */ 
/* 187 */     addMessage(record);
/*     */   }
/*     */ 
/*     */   public void log(String category, String message)
/*     */   {
/* 197 */     log(category, null, message);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, String NDC)
/*     */   {
/* 209 */     log(category, level, message, null, NDC);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, Throwable t)
/*     */   {
/* 222 */     log(category, level, message, t, null);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message)
/*     */   {
/* 233 */     log(category, level, message, null, null);
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 246 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 248 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 259 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 261 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 266 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 270 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.LogMonitorAdapter
 * JD-Core Version:    0.6.0
 */
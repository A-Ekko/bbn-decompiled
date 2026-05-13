/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class LF5Appender extends AppenderSkeleton
/*     */ {
/*     */   protected LogBrokerMonitor _logMonitor;
/*     */   protected static LogBrokerMonitor _defaultLogMonitor;
/*     */   protected static AppenderFinalizer _finalizer;
/*     */ 
/*     */   public LF5Appender()
/*     */   {
/*  69 */     this(getDefaultInstance());
/*     */   }
/*     */ 
/*     */   public LF5Appender(LogBrokerMonitor monitor)
/*     */   {
/*  83 */     if (monitor != null)
/*  84 */       this._logMonitor = monitor;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 100 */     String category = event.getLoggerName();
/* 101 */     String logMessage = event.getRenderedMessage();
/* 102 */     String nestedDiagnosticContext = event.getNDC();
/* 103 */     String threadDescription = event.getThreadName();
/* 104 */     String level = event.getLevel().toString();
/* 105 */     long time = event.timeStamp;
/* 106 */     LocationInfo locationInfo = event.getLocationInformation();
/*     */ 
/* 109 */     Log4JLogRecord record = new Log4JLogRecord();
/*     */ 
/* 111 */     record.setCategory(category);
/* 112 */     record.setMessage(logMessage);
/* 113 */     record.setLocation(locationInfo.fullInfo);
/* 114 */     record.setMillis(time);
/* 115 */     record.setThreadDescription(threadDescription);
/*     */ 
/* 117 */     if (nestedDiagnosticContext != null)
/* 118 */       record.setNDC(nestedDiagnosticContext);
/*     */     else {
/* 120 */       record.setNDC("");
/*     */     }
/*     */ 
/* 123 */     if (event.getThrowableInformation() != null) {
/* 124 */       record.setThrownStackTrace(event.getThrowableInformation());
/*     */     }
/*     */     try
/*     */     {
/* 128 */       record.setLevel(LogLevel.valueOf(level));
/*     */     }
/*     */     catch (LogLevelFormatException e)
/*     */     {
/* 132 */       record.setLevel(LogLevel.WARN);
/*     */     }
/*     */ 
/* 135 */     if (this._logMonitor != null)
/* 136 */       this._logMonitor.addMessage(record);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 153 */     return false;
/*     */   }
/*     */ 
/*     */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*     */   {
/* 170 */     this._logMonitor.setCallSystemExitOnClose(callSystemExitOnClose);
/*     */   }
/*     */ 
/*     */   public boolean equals(LF5Appender compareTo)
/*     */   {
/* 184 */     return this._logMonitor == compareTo.getLogBrokerMonitor();
/*     */   }
/*     */ 
/*     */   public LogBrokerMonitor getLogBrokerMonitor() {
/* 188 */     return this._logMonitor;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 192 */     new LF5Appender();
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfRecords(int maxNumberOfRecords) {
/* 196 */     _defaultLogMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
/*     */   }
/*     */ 
/*     */   protected static synchronized LogBrokerMonitor getDefaultInstance()
/*     */   {
/* 206 */     if (_defaultLogMonitor == null) {
/*     */       try {
/* 208 */         _defaultLogMonitor = new LogBrokerMonitor(LogLevel.getLog4JLevels());
/*     */ 
/* 210 */         _finalizer = new AppenderFinalizer(_defaultLogMonitor);
/*     */ 
/* 212 */         _defaultLogMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
/*     */ 
/* 214 */         _defaultLogMonitor.setFontSize(12);
/* 215 */         _defaultLogMonitor.show();
/*     */       }
/*     */       catch (SecurityException e) {
/* 218 */         _defaultLogMonitor = null;
/*     */       }
/*     */     }
/*     */ 
/* 222 */     return _defaultLogMonitor;
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 232 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 234 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 245 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 247 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 252 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 256 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.LF5Appender
 * JD-Core Version:    0.6.0
 */
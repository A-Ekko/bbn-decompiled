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
/*  60 */     this(getDefaultInstance());
/*     */   }
/*     */ 
/*     */   public LF5Appender(LogBrokerMonitor monitor)
/*     */   {
/*  74 */     if (monitor != null)
/*  75 */       this._logMonitor = monitor;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/*  91 */     String category = event.getLoggerName();
/*  92 */     String logMessage = event.getRenderedMessage();
/*  93 */     String nestedDiagnosticContext = event.getNDC();
/*  94 */     String threadDescription = event.getThreadName();
/*  95 */     String level = event.getLevel().toString();
/*  96 */     long time = event.timeStamp;
/*  97 */     LocationInfo locationInfo = event.getLocationInformation();
/*     */ 
/* 100 */     Log4JLogRecord record = new Log4JLogRecord();
/*     */ 
/* 102 */     record.setCategory(category);
/* 103 */     record.setMessage(logMessage);
/* 104 */     record.setLocation(locationInfo.fullInfo);
/* 105 */     record.setMillis(time);
/* 106 */     record.setThreadDescription(threadDescription);
/*     */ 
/* 108 */     if (nestedDiagnosticContext != null)
/* 109 */       record.setNDC(nestedDiagnosticContext);
/*     */     else {
/* 111 */       record.setNDC("");
/*     */     }
/*     */ 
/* 114 */     if (event.getThrowableInformation() != null) {
/* 115 */       record.setThrownStackTrace(event.getThrowableInformation());
/*     */     }
/*     */     try
/*     */     {
/* 119 */       record.setLevel(LogLevel.valueOf(level));
/*     */     }
/*     */     catch (LogLevelFormatException e)
/*     */     {
/* 123 */       record.setLevel(LogLevel.WARN);
/*     */     }
/*     */ 
/* 126 */     if (this._logMonitor != null)
/* 127 */       this._logMonitor.addMessage(record);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 144 */     return false;
/*     */   }
/*     */ 
/*     */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*     */   {
/* 161 */     this._logMonitor.setCallSystemExitOnClose(callSystemExitOnClose);
/*     */   }
/*     */ 
/*     */   public boolean equals(LF5Appender compareTo)
/*     */   {
/* 175 */     return this._logMonitor == compareTo.getLogBrokerMonitor();
/*     */   }
/*     */ 
/*     */   public LogBrokerMonitor getLogBrokerMonitor() {
/* 179 */     return this._logMonitor;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 183 */     new LF5Appender();
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfRecords(int maxNumberOfRecords) {
/* 187 */     _defaultLogMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
/*     */   }
/*     */ 
/*     */   protected static synchronized LogBrokerMonitor getDefaultInstance()
/*     */   {
/* 197 */     if (_defaultLogMonitor == null) {
/*     */       try {
/* 199 */         _defaultLogMonitor = new LogBrokerMonitor(LogLevel.getLog4JLevels());
/*     */ 
/* 201 */         _finalizer = new AppenderFinalizer(_defaultLogMonitor);
/*     */ 
/* 203 */         _defaultLogMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
/*     */ 
/* 205 */         _defaultLogMonitor.setFontSize(12);
/* 206 */         _defaultLogMonitor.show();
/*     */       }
/*     */       catch (SecurityException e) {
/* 209 */         _defaultLogMonitor = null;
/*     */       }
/*     */     }
/*     */ 
/* 213 */     return _defaultLogMonitor;
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 223 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 225 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 236 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 238 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 243 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 247 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.LF5Appender
 * JD-Core Version:    0.6.0
 */
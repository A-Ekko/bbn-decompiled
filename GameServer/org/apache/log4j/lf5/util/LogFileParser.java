/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Window;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.apache.log4j.lf5.Log4JLogRecord;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogLevelFormatException;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog;
/*     */ import org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog;
/*     */ 
/*     */ public class LogFileParser
/*     */   implements Runnable
/*     */ {
/*     */   public static final String RECORD_DELIMITER = "[slf5s.start]";
/*     */   public static final String ATTRIBUTE_DELIMITER = "[slf5s.";
/*     */   public static final String DATE_DELIMITER = "[slf5s.DATE]";
/*     */   public static final String THREAD_DELIMITER = "[slf5s.THREAD]";
/*     */   public static final String CATEGORY_DELIMITER = "[slf5s.CATEGORY]";
/*     */   public static final String LOCATION_DELIMITER = "[slf5s.LOCATION]";
/*     */   public static final String MESSAGE_DELIMITER = "[slf5s.MESSAGE]";
/*     */   public static final String PRIORITY_DELIMITER = "[slf5s.PRIORITY]";
/*     */   public static final String NDC_DELIMITER = "[slf5s.NDC]";
/*  63 */   private static SimpleDateFormat _sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss,S");
/*     */   private LogBrokerMonitor _monitor;
/*     */   LogFactor5LoadingDialog _loadDialog;
/*  66 */   private InputStream _in = null;
/*     */ 
/*     */   public LogFileParser(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/*  73 */     this(new FileInputStream(file));
/*     */   }
/*     */ 
/*     */   public LogFileParser(InputStream stream) throws IOException {
/*  77 */     this._in = stream;
/*     */   }
/*     */ 
/*     */   public void parse(LogBrokerMonitor monitor)
/*     */     throws RuntimeException
/*     */   {
/*  89 */     this._monitor = monitor;
/*  90 */     Thread t = new Thread(this);
/*  91 */     t.start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 100 */     int index = 0;
/* 101 */     int counter = 0;
/*     */ 
/* 103 */     boolean isLogFile = false;
/*     */ 
/* 105 */     this._loadDialog = new LogFactor5LoadingDialog(this._monitor.getBaseFrame(), "Loading file...");
/*     */     try
/*     */     {
/* 110 */       String logRecords = loadLogFile(this._in);
/*     */ 
/* 112 */       while ((counter = logRecords.indexOf("[slf5s.start]", index)) != -1) {
/* 113 */         LogRecord temp = createLogRecord(logRecords.substring(index, counter));
/* 114 */         isLogFile = true;
/*     */ 
/* 116 */         if (temp != null) {
/* 117 */           this._monitor.addMessage(temp);
/*     */         }
/*     */ 
/* 120 */         index = counter + "[slf5s.start]".length();
/*     */       }
/*     */ 
/* 123 */       if ((index < logRecords.length()) && (isLogFile)) {
/* 124 */         LogRecord temp = createLogRecord(logRecords.substring(index));
/*     */ 
/* 126 */         if (temp != null) {
/* 127 */           this._monitor.addMessage(temp);
/*     */         }
/*     */       }
/*     */ 
/* 131 */       if (!isLogFile) {
/* 132 */         throw new RuntimeException("Invalid log file format");
/*     */       }
/* 134 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 136 */           LogFileParser.this.destroyDialog();
/*     */         } } );
/*     */     }
/*     */     catch (RuntimeException e) {
/* 141 */       destroyDialog();
/* 142 */       displayError("Error - Invalid log file format.\nPlease see documentation on how to load log files.");
/*     */     }
/*     */     catch (IOException e) {
/* 145 */       destroyDialog();
/* 146 */       displayError("Error - Unable to load log file!");
/*     */     }
/*     */ 
/* 149 */     this._in = null;
/*     */   }
/*     */ 
/*     */   protected void displayError(String message)
/*     */   {
/* 156 */     LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(this._monitor.getBaseFrame(), message);
/*     */   }
/*     */ 
/*     */   private void destroyDialog()
/*     */   {
/* 165 */     this._loadDialog.hide();
/* 166 */     this._loadDialog.dispose();
/*     */   }
/*     */ 
/*     */   private String loadLogFile(InputStream stream)
/*     */     throws IOException
/*     */   {
/* 173 */     BufferedInputStream br = new BufferedInputStream(stream);
/*     */ 
/* 175 */     int count = 0;
/* 176 */     int size = br.available();
/*     */ 
/* 178 */     StringBuffer sb = null;
/* 179 */     if (size > 0)
/* 180 */       sb = new StringBuffer(size);
/*     */     else {
/* 182 */       sb = new StringBuffer(1024);
/*     */     }
/*     */ 
/* 185 */     while ((count = br.read()) != -1) {
/* 186 */       sb.append((char)count);
/*     */     }
/*     */ 
/* 189 */     br.close();
/* 190 */     br = null;
/* 191 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String parseAttribute(String name, String record)
/*     */   {
/* 197 */     int index = record.indexOf(name);
/*     */ 
/* 199 */     if (index == -1) {
/* 200 */       return null;
/*     */     }
/*     */ 
/* 203 */     return getAttribute(index, record);
/*     */   }
/*     */ 
/*     */   private long parseDate(String record) {
/*     */     try {
/* 208 */       String s = parseAttribute("[slf5s.DATE]", record);
/*     */ 
/* 210 */       if (s == null) {
/* 211 */         return 0L;
/*     */       }
/*     */ 
/* 214 */       Date d = _sdf.parse(s);
/*     */ 
/* 216 */       return d.getTime(); } catch (ParseException e) {
/*     */     }
/* 218 */     return 0L;
/*     */   }
/*     */ 
/*     */   private LogLevel parsePriority(String record)
/*     */   {
/* 223 */     String temp = parseAttribute("[slf5s.PRIORITY]", record);
/*     */ 
/* 225 */     if (temp != null) {
/*     */       try {
/* 227 */         return LogLevel.valueOf(temp);
/*     */       } catch (LogLevelFormatException e) {
/* 229 */         return LogLevel.DEBUG;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 234 */     return LogLevel.DEBUG;
/*     */   }
/*     */ 
/*     */   private String parseThread(String record) {
/* 238 */     return parseAttribute("[slf5s.THREAD]", record);
/*     */   }
/*     */ 
/*     */   private String parseCategory(String record) {
/* 242 */     return parseAttribute("[slf5s.CATEGORY]", record);
/*     */   }
/*     */ 
/*     */   private String parseLocation(String record) {
/* 246 */     return parseAttribute("[slf5s.LOCATION]", record);
/*     */   }
/*     */ 
/*     */   private String parseMessage(String record) {
/* 250 */     return parseAttribute("[slf5s.MESSAGE]", record);
/*     */   }
/*     */ 
/*     */   private String parseNDC(String record) {
/* 254 */     return parseAttribute("[slf5s.NDC]", record);
/*     */   }
/*     */ 
/*     */   private String parseThrowable(String record) {
/* 258 */     return getAttribute(record.length(), record);
/*     */   }
/*     */ 
/*     */   private LogRecord createLogRecord(String record) {
/* 262 */     if ((record == null) || (record.trim().length() == 0)) {
/* 263 */       return null;
/*     */     }
/*     */ 
/* 266 */     LogRecord lr = new Log4JLogRecord();
/* 267 */     lr.setMillis(parseDate(record));
/* 268 */     lr.setLevel(parsePriority(record));
/* 269 */     lr.setCategory(parseCategory(record));
/* 270 */     lr.setLocation(parseLocation(record));
/* 271 */     lr.setThreadDescription(parseThread(record));
/* 272 */     lr.setNDC(parseNDC(record));
/* 273 */     lr.setMessage(parseMessage(record));
/* 274 */     lr.setThrownStackTrace(parseThrowable(record));
/*     */ 
/* 276 */     return lr;
/*     */   }
/*     */ 
/*     */   private String getAttribute(int index, String record)
/*     */   {
/* 281 */     int start = record.lastIndexOf("[slf5s.", index - 1);
/*     */ 
/* 283 */     if (start == -1) {
/* 284 */       return record.substring(0, index);
/*     */     }
/*     */ 
/* 287 */     start = record.indexOf("]", start);
/*     */ 
/* 289 */     return record.substring(start + 1, index).trim();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.util.LogFileParser
 * JD-Core Version:    0.6.0
 */
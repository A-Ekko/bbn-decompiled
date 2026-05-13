/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.awt.Dialog;
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
/*  54 */   private static SimpleDateFormat _sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss,S");
/*     */   private LogBrokerMonitor _monitor;
/*     */   LogFactor5LoadingDialog _loadDialog;
/*  57 */   private InputStream _in = null;
/*     */ 
/*     */   public LogFileParser(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/*  64 */     this(new FileInputStream(file));
/*     */   }
/*     */ 
/*     */   public LogFileParser(InputStream stream) throws IOException {
/*  68 */     this._in = stream;
/*     */   }
/*     */ 
/*     */   public void parse(LogBrokerMonitor monitor)
/*     */     throws RuntimeException
/*     */   {
/*  80 */     this._monitor = monitor;
/*  81 */     Thread t = new Thread(this);
/*  82 */     t.start();
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  91 */     int index = 0;
/*  92 */     int counter = 0;
/*     */ 
/*  94 */     boolean isLogFile = false;
/*     */ 
/*  96 */     this._loadDialog = new LogFactor5LoadingDialog(this._monitor.getBaseFrame(), "Loading file...");
/*     */     try
/*     */     {
/* 101 */       String logRecords = loadLogFile(this._in);
/*     */       LogRecord temp;
/* 103 */       while ((counter = logRecords.indexOf("[slf5s.start]", index)) != -1) {
/* 104 */         temp = createLogRecord(logRecords.substring(index, counter));
/* 105 */         isLogFile = true;
/*     */ 
/* 107 */         if (temp != null) {
/* 108 */           this._monitor.addMessage(temp);
/*     */         }
/*     */ 
/* 111 */         index = counter + "[slf5s.start]".length();
/*     */       }
/*     */ 
/* 114 */       if ((index < logRecords.length()) && (isLogFile)) {
/* 115 */         temp = createLogRecord(logRecords.substring(index));
/*     */ 
/* 117 */         if (temp != null) {
/* 118 */           this._monitor.addMessage(temp);
/*     */         }
/*     */       }
/*     */ 
/* 122 */       if (!isLogFile) {
/* 123 */         throw new RuntimeException("Invalid log file format");
/*     */       }
/* 125 */       SwingUtilities.invokeLater(new Runnable() {
/*     */         public void run() {
/* 127 */           LogFileParser.this.destroyDialog();
/*     */         } } );
/*     */     }
/*     */     catch (RuntimeException e) {
/* 132 */       destroyDialog();
/* 133 */       displayError("Error - Invalid log file format.\nPlease see documentation on how to load log files.");
/*     */     }
/*     */     catch (IOException e) {
/* 136 */       destroyDialog();
/* 137 */       displayError("Error - Unable to load log file!");
/*     */     }
/*     */ 
/* 140 */     this._in = null;
/*     */   }
/*     */ 
/*     */   protected void displayError(String message)
/*     */   {
/* 147 */     LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(this._monitor.getBaseFrame(), message);
/*     */   }
/*     */ 
/*     */   private void destroyDialog()
/*     */   {
/* 156 */     this._loadDialog.hide();
/* 157 */     this._loadDialog.dispose();
/*     */   }
/*     */ 
/*     */   private String loadLogFile(InputStream stream)
/*     */     throws IOException
/*     */   {
/* 164 */     BufferedInputStream br = new BufferedInputStream(stream);
/*     */ 
/* 166 */     int count = 0;
/* 167 */     int size = br.available();
/*     */ 
/* 169 */     StringBuffer sb = null;
/* 170 */     if (size > 0)
/* 171 */       sb = new StringBuffer(size);
/*     */     else {
/* 173 */       sb = new StringBuffer(1024);
/*     */     }
/*     */ 
/* 176 */     while ((count = br.read()) != -1) {
/* 177 */       sb.append((char)count);
/*     */     }
/*     */ 
/* 180 */     br.close();
/* 181 */     br = null;
/* 182 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String parseAttribute(String name, String record)
/*     */   {
/* 188 */     int index = record.indexOf(name);
/*     */ 
/* 190 */     if (index == -1) {
/* 191 */       return null;
/*     */     }
/*     */ 
/* 194 */     return getAttribute(index, record);
/*     */   }
/*     */ 
/*     */   private long parseDate(String record) {
/*     */     try {
/* 199 */       String s = parseAttribute("[slf5s.DATE]", record);
/*     */ 
/* 201 */       if (s == null) {
/* 202 */         return 0L;
/*     */       }
/*     */ 
/* 205 */       Date d = _sdf.parse(s);
/*     */ 
/* 207 */       return d.getTime(); } catch (ParseException e) {
/*     */     }
/* 209 */     return 0L;
/*     */   }
/*     */ 
/*     */   private LogLevel parsePriority(String record)
/*     */   {
/* 214 */     String temp = parseAttribute("[slf5s.PRIORITY]", record);
/*     */ 
/* 216 */     if (temp != null) {
/*     */       try {
/* 218 */         return LogLevel.valueOf(temp);
/*     */       } catch (LogLevelFormatException e) {
/* 220 */         return LogLevel.DEBUG;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 225 */     return LogLevel.DEBUG;
/*     */   }
/*     */ 
/*     */   private String parseThread(String record) {
/* 229 */     return parseAttribute("[slf5s.THREAD]", record);
/*     */   }
/*     */ 
/*     */   private String parseCategory(String record) {
/* 233 */     return parseAttribute("[slf5s.CATEGORY]", record);
/*     */   }
/*     */ 
/*     */   private String parseLocation(String record) {
/* 237 */     return parseAttribute("[slf5s.LOCATION]", record);
/*     */   }
/*     */ 
/*     */   private String parseMessage(String record) {
/* 241 */     return parseAttribute("[slf5s.MESSAGE]", record);
/*     */   }
/*     */ 
/*     */   private String parseNDC(String record) {
/* 245 */     return parseAttribute("[slf5s.NDC]", record);
/*     */   }
/*     */ 
/*     */   private String parseThrowable(String record) {
/* 249 */     return getAttribute(record.length(), record);
/*     */   }
/*     */ 
/*     */   private LogRecord createLogRecord(String record) {
/* 253 */     if ((record == null) || (record.trim().length() == 0)) {
/* 254 */       return null;
/*     */     }
/*     */ 
/* 257 */     LogRecord lr = new Log4JLogRecord();
/* 258 */     lr.setMillis(parseDate(record));
/* 259 */     lr.setLevel(parsePriority(record));
/* 260 */     lr.setCategory(parseCategory(record));
/* 261 */     lr.setLocation(parseLocation(record));
/* 262 */     lr.setThreadDescription(parseThread(record));
/* 263 */     lr.setNDC(parseNDC(record));
/* 264 */     lr.setMessage(parseMessage(record));
/* 265 */     lr.setThrownStackTrace(parseThrowable(record));
/*     */ 
/* 267 */     return lr;
/*     */   }
/*     */ 
/*     */   private String getAttribute(int index, String record)
/*     */   {
/* 272 */     int start = record.lastIndexOf("[slf5s.", index - 1);
/*     */ 
/* 274 */     if (start == -1) {
/* 275 */       return record.substring(0, index);
/*     */     }
/*     */ 
/* 278 */     start = record.indexOf("]", start);
/*     */ 
/* 280 */     return record.substring(start + 1, index).trim();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.util.LogFileParser
 * JD-Core Version:    0.6.0
 */
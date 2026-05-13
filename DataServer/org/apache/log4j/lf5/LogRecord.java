/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringWriter;
/*     */ 
/*     */ public abstract class LogRecord
/*     */   implements Serializable
/*     */ {
/*  32 */   protected static long _seqCount = 0L;
/*     */   protected LogLevel _level;
/*     */   protected String _message;
/*     */   protected long _sequenceNumber;
/*     */   protected long _millis;
/*     */   protected String _category;
/*     */   protected String _thread;
/*     */   protected String _thrownStackTrace;
/*     */   protected Throwable _thrown;
/*     */   protected String _ndc;
/*     */   protected String _location;
/*     */ 
/*     */   public LogRecord()
/*     */   {
/*  56 */     this._millis = System.currentTimeMillis();
/*  57 */     this._category = "Debug";
/*  58 */     this._message = "";
/*  59 */     this._level = LogLevel.INFO;
/*  60 */     this._sequenceNumber = getNextId();
/*  61 */     this._thread = Thread.currentThread().toString();
/*  62 */     this._ndc = "";
/*  63 */     this._location = "";
/*     */   }
/*     */ 
/*     */   public LogLevel getLevel()
/*     */   {
/*  78 */     return this._level;
/*     */   }
/*     */ 
/*     */   public void setLevel(LogLevel level)
/*     */   {
/*  89 */     this._level = level;
/*     */   }
/*     */ 
/*     */   public abstract boolean isSevereLevel();
/*     */ 
/*     */   public boolean hasThrown()
/*     */   {
/* 102 */     Throwable thrown = getThrown();
/* 103 */     if (thrown == null) {
/* 104 */       return false;
/*     */     }
/* 106 */     String thrownString = thrown.toString();
/* 107 */     return (thrownString != null) && (thrownString.trim().length() != 0);
/*     */   }
/*     */ 
/*     */   public boolean isFatal()
/*     */   {
/* 114 */     return (isSevereLevel()) || (hasThrown());
/*     */   }
/*     */ 
/*     */   public String getCategory()
/*     */   {
/* 125 */     return this._category;
/*     */   }
/*     */ 
/*     */   public void setCategory(String category)
/*     */   {
/* 147 */     this._category = category;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 157 */     return this._message;
/*     */   }
/*     */ 
/*     */   public void setMessage(String message)
/*     */   {
/* 167 */     this._message = message;
/*     */   }
/*     */ 
/*     */   public long getSequenceNumber()
/*     */   {
/* 179 */     return this._sequenceNumber;
/*     */   }
/*     */ 
/*     */   public void setSequenceNumber(long number)
/*     */   {
/* 191 */     this._sequenceNumber = number;
/*     */   }
/*     */ 
/*     */   public long getMillis()
/*     */   {
/* 203 */     return this._millis;
/*     */   }
/*     */ 
/*     */   public void setMillis(long millis)
/*     */   {
/* 214 */     this._millis = millis;
/*     */   }
/*     */ 
/*     */   public String getThreadDescription()
/*     */   {
/* 227 */     return this._thread;
/*     */   }
/*     */ 
/*     */   public void setThreadDescription(String threadDescription)
/*     */   {
/* 240 */     this._thread = threadDescription;
/*     */   }
/*     */ 
/*     */   public String getThrownStackTrace()
/*     */   {
/* 261 */     return this._thrownStackTrace;
/*     */   }
/*     */ 
/*     */   public void setThrownStackTrace(String trace)
/*     */   {
/* 271 */     this._thrownStackTrace = trace;
/*     */   }
/*     */ 
/*     */   public Throwable getThrown()
/*     */   {
/* 282 */     return this._thrown;
/*     */   }
/*     */ 
/*     */   public void setThrown(Throwable thrown)
/*     */   {
/* 295 */     if (thrown == null) {
/* 296 */       return;
/*     */     }
/* 298 */     this._thrown = thrown;
/* 299 */     StringWriter sw = new StringWriter();
/* 300 */     PrintWriter out = new PrintWriter(sw);
/* 301 */     thrown.printStackTrace(out);
/* 302 */     out.flush();
/* 303 */     this._thrownStackTrace = sw.toString();
/*     */     try {
/* 305 */       out.close();
/* 306 */       sw.close();
/*     */     }
/*     */     catch (IOException e) {
/*     */     }
/* 310 */     out = null;
/* 311 */     sw = null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 318 */     StringBuffer buf = new StringBuffer();
/* 319 */     buf.append("LogRecord: [" + this._level + ", " + this._message + "]");
/* 320 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String getNDC()
/*     */   {
/* 329 */     return this._ndc;
/*     */   }
/*     */ 
/*     */   public void setNDC(String ndc)
/*     */   {
/* 338 */     this._ndc = ndc;
/*     */   }
/*     */ 
/*     */   public String getLocation()
/*     */   {
/* 347 */     return this._location;
/*     */   }
/*     */ 
/*     */   public void setLocation(String location)
/*     */   {
/* 356 */     this._location = location;
/*     */   }
/*     */ 
/*     */   public static synchronized void resetSequenceNumber()
/*     */   {
/* 364 */     _seqCount = 0L;
/*     */   }
/*     */ 
/*     */   protected static synchronized long getNextId()
/*     */   {
/* 372 */     _seqCount += 1L;
/* 373 */     return _seqCount;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.LogRecord
 * JD-Core Version:    0.6.0
 */
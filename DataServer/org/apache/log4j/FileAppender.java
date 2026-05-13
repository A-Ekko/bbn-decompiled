/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilterWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.QuietWriter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ 
/*     */ public class FileAppender extends WriterAppender
/*     */ {
/*  41 */   protected boolean fileAppend = true;
/*     */ 
/*  45 */   protected String fileName = null;
/*     */ 
/*  49 */   protected boolean bufferedIO = false;
/*     */ 
/*  53 */   protected int bufferSize = 8192;
/*     */ 
/*     */   public FileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  79 */     this.layout = layout;
/*  80 */     setFile(filename, append, bufferedIO, bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/*  95 */     this.layout = layout;
/*  96 */     setFile(filename, append, false, this.bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/* 107 */     this(layout, filename, true);
/*     */   }
/*     */ 
/*     */   public void setFile(String file)
/*     */   {
/* 122 */     String val = file.trim();
/* 123 */     this.fileName = val;
/*     */   }
/*     */ 
/*     */   public boolean getAppend()
/*     */   {
/* 131 */     return this.fileAppend;
/*     */   }
/*     */ 
/*     */   public String getFile()
/*     */   {
/* 138 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 149 */     if (this.fileName != null) {
/*     */       try {
/* 151 */         setFile(this.fileName, this.fileAppend, this.bufferedIO, this.bufferSize);
/*     */       }
/*     */       catch (IOException e) {
/* 154 */         this.errorHandler.error("setFile(" + this.fileName + "," + this.fileAppend + ") call failed.", e, 4);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 159 */       LogLog.warn("File option not set for appender [" + this.name + "].");
/* 160 */       LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeFile()
/*     */   {
/* 169 */     if (this.qw != null)
/*     */       try {
/* 171 */         this.qw.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 176 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean getBufferedIO()
/*     */   {
/* 190 */     return this.bufferedIO;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 199 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean flag)
/*     */   {
/* 216 */     this.fileAppend = flag;
/*     */   }
/*     */ 
/*     */   public void setBufferedIO(boolean bufferedIO)
/*     */   {
/* 231 */     this.bufferedIO = bufferedIO;
/* 232 */     if (bufferedIO)
/* 233 */       this.immediateFlush = false;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 243 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 264 */     LogLog.debug("setFile called: " + fileName + ", " + append);
/*     */ 
/* 267 */     if (bufferedIO) {
/* 268 */       setImmediateFlush(false);
/*     */     }
/*     */ 
/* 271 */     reset();
/* 272 */     Writer fw = createWriter(new FileOutputStream(fileName, append));
/* 273 */     if (bufferedIO) {
/* 274 */       fw = new BufferedWriter(fw, bufferSize);
/*     */     }
/* 276 */     setQWForFiles(fw);
/* 277 */     this.fileName = fileName;
/* 278 */     this.fileAppend = append;
/* 279 */     this.bufferedIO = bufferedIO;
/* 280 */     this.bufferSize = bufferSize;
/* 281 */     writeHeader();
/* 282 */     LogLog.debug("setFile ended");
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 293 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 302 */     closeFile();
/* 303 */     this.fileName = null;
/* 304 */     super.reset();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.FileAppender
 * JD-Core Version:    0.6.0
 */
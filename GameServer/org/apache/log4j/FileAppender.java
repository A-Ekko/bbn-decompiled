/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
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
/*  53 */   protected boolean fileAppend = true;
/*     */ 
/*  57 */   protected String fileName = null;
/*     */ 
/*  61 */   protected boolean bufferedIO = false;
/*     */ 
/*  66 */   protected int bufferSize = 8192;
/*     */ 
/*     */   public FileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  92 */     this.layout = layout;
/*  93 */     setFile(filename, append, bufferedIO, bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/* 108 */     this.layout = layout;
/* 109 */     setFile(filename, append, false, this.bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/* 120 */     this(layout, filename, true);
/*     */   }
/*     */ 
/*     */   public void setFile(String file)
/*     */   {
/* 135 */     String val = file.trim();
/* 136 */     this.fileName = val;
/*     */   }
/*     */ 
/*     */   public boolean getAppend()
/*     */   {
/* 144 */     return this.fileAppend;
/*     */   }
/*     */ 
/*     */   public String getFile()
/*     */   {
/* 151 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 162 */     if (this.fileName != null) {
/*     */       try {
/* 164 */         setFile(this.fileName, this.fileAppend, this.bufferedIO, this.bufferSize);
/*     */       }
/*     */       catch (IOException e) {
/* 167 */         this.errorHandler.error("setFile(" + this.fileName + "," + this.fileAppend + ") call failed.", e, 4);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 172 */       LogLog.warn("File option not set for appender [" + this.name + "].");
/* 173 */       LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeFile()
/*     */   {
/* 182 */     if (this.qw != null)
/*     */       try {
/* 184 */         this.qw.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 189 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean getBufferedIO()
/*     */   {
/* 203 */     return this.bufferedIO;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 212 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean flag)
/*     */   {
/* 229 */     this.fileAppend = flag;
/*     */   }
/*     */ 
/*     */   public void setBufferedIO(boolean bufferedIO)
/*     */   {
/* 244 */     this.bufferedIO = bufferedIO;
/* 245 */     if (bufferedIO)
/* 246 */       this.immediateFlush = false;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 256 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 277 */     LogLog.debug("setFile called: " + fileName + ", " + append);
/*     */ 
/* 280 */     if (bufferedIO) {
/* 281 */       setImmediateFlush(false);
/*     */     }
/*     */ 
/* 284 */     reset();
/* 285 */     FileOutputStream ostream = null;
/*     */     try
/*     */     {
/* 290 */       ostream = new FileOutputStream(fileName, append);
/*     */     }
/*     */     catch (FileNotFoundException ex)
/*     */     {
/* 297 */       String parentName = new File(fileName).getParent();
/* 298 */       if (parentName != null) {
/* 299 */         File parentDir = new File(parentName);
/* 300 */         if ((!parentDir.exists()) && (parentDir.mkdirs()))
/* 301 */           ostream = new FileOutputStream(fileName, append);
/*     */         else
/* 303 */           throw ex;
/*     */       }
/*     */       else {
/* 306 */         throw ex;
/*     */       }
/*     */     }
/* 309 */     Writer fw = createWriter(ostream);
/* 310 */     if (bufferedIO) {
/* 311 */       fw = new BufferedWriter(fw, bufferSize);
/*     */     }
/* 313 */     setQWForFiles(fw);
/* 314 */     this.fileName = fileName;
/* 315 */     this.fileAppend = append;
/* 316 */     this.bufferedIO = bufferedIO;
/* 317 */     this.bufferSize = bufferSize;
/* 318 */     writeHeader();
/* 319 */     LogLog.debug("setFile ended");
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 330 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 339 */     closeFile();
/* 340 */     this.fileName = null;
/* 341 */     super.reset();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.FileAppender
 * JD-Core Version:    0.6.0
 */
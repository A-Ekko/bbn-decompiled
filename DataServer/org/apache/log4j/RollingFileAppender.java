/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.CountingQuietWriter;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class RollingFileAppender extends FileAppender
/*     */ {
/*  33 */   protected long maxFileSize = 10485760L;
/*     */ 
/*  38 */   protected int maxBackupIndex = 1;
/*     */ 
/*     */   public RollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/*  60 */     super(layout, filename, append);
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/*  71 */     super(layout, filename);
/*     */   }
/*     */ 
/*     */   public int getMaxBackupIndex()
/*     */   {
/*  79 */     return this.maxBackupIndex;
/*     */   }
/*     */ 
/*     */   public long getMaximumFileSize()
/*     */   {
/*  90 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */   public void rollOver()
/*     */   {
/* 112 */     LogLog.debug("rolling over count=" + ((CountingQuietWriter)this.qw).getCount());
/* 113 */     LogLog.debug("maxBackupIndex=" + this.maxBackupIndex);
/*     */ 
/* 116 */     if (this.maxBackupIndex > 0)
/*     */     {
/* 118 */       File file = new File(this.fileName + '.' + this.maxBackupIndex);
/* 119 */       if (file.exists()) {
/* 120 */         file.delete();
/*     */       }
/*     */ 
/* 123 */       for (int i = this.maxBackupIndex - 1; i >= 1; i--) {
/* 124 */         file = new File(this.fileName + "." + i);
/* 125 */         if (file.exists()) {
/* 126 */           target = new File(this.fileName + '.' + (i + 1));
/* 127 */           LogLog.debug("Renaming file " + file + " to " + target);
/* 128 */           file.renameTo(target);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 133 */       File target = new File(this.fileName + "." + 1);
/*     */ 
/* 135 */       closeFile();
/*     */ 
/* 137 */       file = new File(this.fileName);
/* 138 */       LogLog.debug("Renaming file " + file + " to " + target);
/* 139 */       file.renameTo(target);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 145 */       setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
/*     */     }
/*     */     catch (IOException e) {
/* 148 */       LogLog.error("setFile(" + this.fileName + ", false) call failed.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 156 */     super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
/* 157 */     if (append) {
/* 158 */       File f = new File(fileName);
/* 159 */       ((CountingQuietWriter)this.qw).setCount(f.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxBackupIndex(int maxBackups)
/*     */   {
/* 175 */     this.maxBackupIndex = maxBackups;
/*     */   }
/*     */ 
/*     */   public void setMaximumFileSize(long maxFileSize)
/*     */   {
/* 192 */     this.maxFileSize = maxFileSize;
/*     */   }
/*     */ 
/*     */   public void setMaxFileSize(String value)
/*     */   {
/* 209 */     this.maxFileSize = OptionConverter.toFileSize(value, this.maxFileSize + 1L);
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 214 */     this.qw = new CountingQuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 225 */     super.subAppend(event);
/* 226 */     if ((this.fileName != null) && (((CountingQuietWriter)this.qw).getCount() >= this.maxFileSize))
/*     */     {
/* 228 */       rollOver();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.RollingFileAppender
 * JD-Core Version:    0.6.0
 */
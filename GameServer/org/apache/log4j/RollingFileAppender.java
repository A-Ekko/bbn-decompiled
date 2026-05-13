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
/*  43 */   protected long maxFileSize = 10485760L;
/*     */ 
/*  48 */   protected int maxBackupIndex = 1;
/*     */ 
/*  50 */   private long nextRollover = 0L;
/*     */ 
/*     */   public RollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/*  72 */     super(layout, filename, append);
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/*  83 */     super(layout, filename);
/*     */   }
/*     */ 
/*     */   public int getMaxBackupIndex()
/*     */   {
/*  91 */     return this.maxBackupIndex;
/*     */   }
/*     */ 
/*     */   public long getMaximumFileSize()
/*     */   {
/* 102 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */   public void rollOver()
/*     */   {
/* 124 */     if (this.qw != null) {
/* 125 */       long size = ((CountingQuietWriter)this.qw).getCount();
/* 126 */       LogLog.debug("rolling over count=" + size);
/*     */ 
/* 129 */       this.nextRollover = (size + this.maxFileSize);
/*     */     }
/* 131 */     LogLog.debug("maxBackupIndex=" + this.maxBackupIndex);
/*     */ 
/* 133 */     boolean renameSucceeded = true;
/*     */ 
/* 135 */     if (this.maxBackupIndex > 0)
/*     */     {
/* 137 */       File file = new File(this.fileName + '.' + this.maxBackupIndex);
/* 138 */       if (file.exists()) {
/* 139 */         renameSucceeded = file.delete();
/*     */       }
/*     */ 
/* 142 */       for (int i = this.maxBackupIndex - 1; (i >= 1) && (renameSucceeded); i--) {
/* 143 */         file = new File(this.fileName + "." + i);
/* 144 */         if (file.exists()) {
/* 145 */           File target = new File(this.fileName + '.' + (i + 1));
/* 146 */           LogLog.debug("Renaming file " + file + " to " + target);
/* 147 */           renameSucceeded = file.renameTo(target);
/*     */         }
/*     */       }
/*     */ 
/* 151 */       if (renameSucceeded)
/*     */       {
/* 153 */         File target = new File(this.fileName + "." + 1);
/*     */ 
/* 155 */         closeFile();
/*     */ 
/* 157 */         file = new File(this.fileName);
/* 158 */         LogLog.debug("Renaming file " + file + " to " + target);
/* 159 */         renameSucceeded = file.renameTo(target);
/*     */ 
/* 163 */         if (!renameSucceeded) {
/*     */           try {
/* 165 */             setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
/*     */           }
/*     */           catch (IOException e) {
/* 168 */             LogLog.error("setFile(" + this.fileName + ", true) call failed.", e);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 177 */     if (renameSucceeded)
/*     */     {
/*     */       try
/*     */       {
/* 181 */         setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
/* 182 */         this.nextRollover = 0L;
/*     */       }
/*     */       catch (IOException e) {
/* 185 */         LogLog.error("setFile(" + this.fileName + ", false) call failed.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 194 */     super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
/* 195 */     if (append) {
/* 196 */       File f = new File(fileName);
/* 197 */       ((CountingQuietWriter)this.qw).setCount(f.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxBackupIndex(int maxBackups)
/*     */   {
/* 213 */     this.maxBackupIndex = maxBackups;
/*     */   }
/*     */ 
/*     */   public void setMaximumFileSize(long maxFileSize)
/*     */   {
/* 230 */     this.maxFileSize = maxFileSize;
/*     */   }
/*     */ 
/*     */   public void setMaxFileSize(String value)
/*     */   {
/* 247 */     this.maxFileSize = OptionConverter.toFileSize(value, this.maxFileSize + 1L);
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 252 */     this.qw = new CountingQuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 263 */     super.subAppend(event);
/* 264 */     if ((this.fileName != null) && (this.qw != null)) {
/* 265 */       long size = ((CountingQuietWriter)this.qw).getCount();
/* 266 */       if ((size >= this.maxFileSize) && (size >= this.nextRollover))
/* 267 */         rollOver();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.RollingFileAppender
 * JD-Core Version:    0.6.0
 */
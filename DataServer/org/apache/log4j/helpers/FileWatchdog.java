/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.File;
/*    */ 
/*    */ public abstract class FileWatchdog extends Thread
/*    */ {
/*    */   public static final long DEFAULT_DELAY = 60000L;
/*    */   protected String filename;
/* 37 */   protected long delay = 60000L;
/*    */   File file;
/* 40 */   long lastModif = 0L;
/* 41 */   boolean warnedAlready = false;
/* 42 */   boolean interrupted = false;
/*    */ 
/*    */   protected FileWatchdog(String filename)
/*    */   {
/* 46 */     this.filename = filename;
/* 47 */     this.file = new File(filename);
/* 48 */     setDaemon(true);
/* 49 */     checkAndConfigure();
/*    */   }
/*    */ 
/*    */   public void setDelay(long delay)
/*    */   {
/* 57 */     this.delay = delay;
/*    */   }
/*    */ 
/*    */   protected abstract void doOnChange();
/*    */ 
/*    */   protected void checkAndConfigure()
/*    */   {
/*    */     boolean fileExists;
/*    */     try {
/* 68 */       fileExists = this.file.exists();
/*    */     } catch (SecurityException e) {
/* 70 */       LogLog.warn("Was not allowed to read check file existance, file:[" + this.filename + "].");
/*    */ 
/* 72 */       this.interrupted = true;
/* 73 */       return;
/*    */     }
/*    */ 
/* 76 */     if (fileExists) {
/* 77 */       long l = this.file.lastModified();
/* 78 */       if (l > this.lastModif) {
/* 79 */         this.lastModif = l;
/* 80 */         doOnChange();
/* 81 */         this.warnedAlready = false;
/*    */       }
/*    */     }
/* 84 */     else if (!this.warnedAlready) {
/* 85 */       LogLog.debug("[" + this.filename + "] does not exist.");
/* 86 */       this.warnedAlready = true;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/* 93 */     while (!this.interrupted) {
/*    */       try {
/* 95 */         Thread.currentThread(); Thread.sleep(this.delay);
/*    */       }
/*    */       catch (InterruptedException e) {
/*    */       }
/* 99 */       checkAndConfigure();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.helpers.FileWatchdog
 * JD-Core Version:    0.6.0
 */
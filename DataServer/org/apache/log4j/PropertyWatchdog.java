/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.FileWatchdog;
/*     */ 
/*     */ class PropertyWatchdog extends FileWatchdog
/*     */ {
/*     */   PropertyWatchdog(String filename)
/*     */   {
/* 665 */     super(filename);
/*     */   }
/*     */ 
/*     */   public void doOnChange()
/*     */   {
/* 673 */     new PropertyConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.PropertyWatchdog
 * JD-Core Version:    0.6.0
 */
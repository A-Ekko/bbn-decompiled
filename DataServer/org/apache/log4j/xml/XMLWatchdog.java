/*     */ package org.apache.log4j.xml;
/*     */ 
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.helpers.FileWatchdog;
/*     */ 
/*     */ class XMLWatchdog extends FileWatchdog
/*     */ {
/*     */   XMLWatchdog(String filename)
/*     */   {
/* 814 */     super(filename);
/*     */   }
/*     */ 
/*     */   public void doOnChange()
/*     */   {
/* 822 */     new DOMConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.xml.XMLWatchdog
 * JD-Core Version:    0.6.0
 */
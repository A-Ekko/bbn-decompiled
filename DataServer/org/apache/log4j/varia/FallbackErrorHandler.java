/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class FallbackErrorHandler
/*     */   implements ErrorHandler
/*     */ {
/*     */   Appender backup;
/*     */   Appender primary;
/*     */   Vector loggers;
/*     */ 
/*     */   public void setLogger(Logger logger)
/*     */   {
/*  47 */     LogLog.debug("FB: Adding logger [" + logger.getName() + "].");
/*  48 */     if (this.loggers == null) {
/*  49 */       this.loggers = new Vector();
/*     */     }
/*  51 */     this.loggers.addElement(logger);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode)
/*     */   {
/*  68 */     error(message, e, errorCode, null);
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode, LoggingEvent event)
/*     */   {
/*  77 */     LogLog.debug("FB: The following error reported: " + message, e);
/*  78 */     LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
/*  79 */     for (int i = 0; i < this.loggers.size(); i++) {
/*  80 */       Logger l = (Logger)this.loggers.elementAt(i);
/*  81 */       LogLog.debug("FB: Searching for [" + this.primary.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  84 */       LogLog.debug("FB: Replacing [" + this.primary.getName() + "] by [" + this.backup.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  86 */       l.removeAppender(this.primary);
/*  87 */       LogLog.debug("FB: Adding appender [" + this.backup.getName() + "] to logger " + l.getName());
/*     */ 
/*  89 */       l.addAppender(this.backup);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void error(String message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setAppender(Appender primary)
/*     */   {
/* 111 */     LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
/* 112 */     this.primary = primary;
/*     */   }
/*     */ 
/*     */   public void setBackupAppender(Appender backup)
/*     */   {
/* 120 */     LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
/* 121 */     this.backup = backup;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.FallbackErrorHandler
 * JD-Core Version:    0.6.0
 */
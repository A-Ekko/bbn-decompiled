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
/*  56 */     LogLog.debug("FB: Adding logger [" + logger.getName() + "].");
/*  57 */     if (this.loggers == null) {
/*  58 */       this.loggers = new Vector();
/*     */     }
/*  60 */     this.loggers.addElement(logger);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode)
/*     */   {
/*  77 */     error(message, e, errorCode, null);
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode, LoggingEvent event)
/*     */   {
/*  86 */     LogLog.debug("FB: The following error reported: " + message, e);
/*  87 */     LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
/*  88 */     if (this.loggers != null)
/*  89 */       for (int i = 0; i < this.loggers.size(); i++) {
/*  90 */         Logger l = (Logger)this.loggers.elementAt(i);
/*  91 */         LogLog.debug("FB: Searching for [" + this.primary.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  93 */         LogLog.debug("FB: Replacing [" + this.primary.getName() + "] by [" + this.backup.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  95 */         l.removeAppender(this.primary);
/*  96 */         LogLog.debug("FB: Adding appender [" + this.backup.getName() + "] to logger " + l.getName());
/*     */ 
/*  98 */         l.addAppender(this.backup);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void error(String message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setAppender(Appender primary)
/*     */   {
/* 121 */     LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
/* 122 */     this.primary = primary;
/*     */   }
/*     */ 
/*     */   public void setBackupAppender(Appender backup)
/*     */   {
/* 130 */     LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
/* 131 */     this.backup = backup;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.varia.FallbackErrorHandler
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class Jdk14Logger
/*     */   implements Log, Serializable
/*     */ {
/*  64 */   protected transient Logger logger = null;
/*     */ 
/*  70 */   protected String name = null;
/*     */ 
/*     */   public Jdk14Logger(String name)
/*     */   {
/*  52 */     this.name = name;
/*  53 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */   private void log(Level level, String msg, Throwable ex)
/*     */   {
/*  77 */     Logger logger = getLogger();
/*  78 */     if (logger.isLoggable(level))
/*     */     {
/*  80 */       Throwable dummyException = new Throwable();
/*  81 */       StackTraceElement[] locations = dummyException.getStackTrace();
/*     */ 
/*  83 */       String cname = "unknown";
/*  84 */       String method = "unknown";
/*  85 */       if ((locations != null) && (locations.length > 2)) {
/*  86 */         StackTraceElement caller = locations[2];
/*  87 */         cname = caller.getClassName();
/*  88 */         method = caller.getMethodName();
/*     */       }
/*  90 */       if (ex == null)
/*  91 */         logger.logp(level, cname, method, msg);
/*     */       else
/*  93 */         logger.logp(level, cname, method, msg, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 103 */     log(Level.FINE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable exception)
/*     */   {
/* 111 */     log(Level.FINE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 119 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable exception)
/*     */   {
/* 127 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 135 */     log(Level.SEVERE, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable exception)
/*     */   {
/* 143 */     log(Level.SEVERE, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/* 151 */     if (this.logger == null) {
/* 152 */       this.logger = Logger.getLogger(this.name);
/*     */     }
/* 154 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 162 */     log(Level.INFO, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable exception)
/*     */   {
/* 170 */     log(Level.INFO, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 178 */     return getLogger().isLoggable(Level.FINE);
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 186 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 194 */     return getLogger().isLoggable(Level.SEVERE);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 202 */     return getLogger().isLoggable(Level.INFO);
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 210 */     return getLogger().isLoggable(Level.FINEST);
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 218 */     return getLogger().isLoggable(Level.WARNING);
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 226 */     log(Level.FINEST, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable exception)
/*     */   {
/* 234 */     log(Level.FINEST, String.valueOf(message), exception);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 242 */     log(Level.WARNING, String.valueOf(message), null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable exception)
/*     */   {
/* 250 */     log(Level.WARNING, String.valueOf(message), exception);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.Jdk14Logger
 * JD-Core Version:    0.6.0
 */
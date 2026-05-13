/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ public class Log4JLogger
/*     */   implements Log, Serializable
/*     */ {
/*  43 */   private static final String FQCN = Log4JLogger.class.getName();
/*     */ 
/*  45 */   private static final boolean is12 = Priority.class.isAssignableFrom(Level.class);
/*     */ 
/*  48 */   private transient Logger logger = null;
/*     */ 
/*  51 */   private String name = null;
/*     */ 
/*     */   public Log4JLogger()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Log4JLogger(String name)
/*     */   {
/*  64 */     this.name = name;
/*  65 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */   public Log4JLogger(Logger logger)
/*     */   {
/*  71 */     this.name = logger.getName();
/*  72 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/*  84 */     if (is12)
/*  85 */       getLogger().log(FQCN, (Priority)Level.DEBUG, message, null);
/*     */     else
/*  87 */       getLogger().log(FQCN, Level.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/*  97 */     if (is12)
/*  98 */       getLogger().log(FQCN, (Priority)Level.DEBUG, message, t);
/*     */     else
/* 100 */       getLogger().log(FQCN, Level.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 109 */     if (is12)
/* 110 */       getLogger().log(FQCN, (Priority)Level.DEBUG, message, null);
/*     */     else
/* 112 */       getLogger().log(FQCN, Level.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 120 */     if (is12)
/* 121 */       getLogger().log(FQCN, (Priority)Level.DEBUG, message, t);
/*     */     else
/* 123 */       getLogger().log(FQCN, Level.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 132 */     if (is12)
/* 133 */       getLogger().log(FQCN, (Priority)Level.INFO, message, null);
/*     */     else
/* 135 */       getLogger().log(FQCN, Level.INFO, message, null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 144 */     if (is12)
/* 145 */       getLogger().log(FQCN, (Priority)Level.INFO, message, t);
/*     */     else
/* 147 */       getLogger().log(FQCN, Level.INFO, message, t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 156 */     if (is12)
/* 157 */       getLogger().log(FQCN, (Priority)Level.WARN, message, null);
/*     */     else
/* 159 */       getLogger().log(FQCN, Level.WARN, message, null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 168 */     if (is12)
/* 169 */       getLogger().log(FQCN, (Priority)Level.WARN, message, t);
/*     */     else
/* 171 */       getLogger().log(FQCN, Level.WARN, message, t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 180 */     if (is12)
/* 181 */       getLogger().log(FQCN, (Priority)Level.ERROR, message, null);
/*     */     else
/* 183 */       getLogger().log(FQCN, Level.ERROR, message, null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 192 */     if (is12)
/* 193 */       getLogger().log(FQCN, (Priority)Level.ERROR, message, t);
/*     */     else
/* 195 */       getLogger().log(FQCN, Level.ERROR, message, t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 204 */     if (is12)
/* 205 */       getLogger().log(FQCN, (Priority)Level.FATAL, message, null);
/*     */     else
/* 207 */       getLogger().log(FQCN, Level.FATAL, message, null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 216 */     if (is12)
/* 217 */       getLogger().log(FQCN, (Priority)Level.FATAL, message, t);
/*     */     else
/* 219 */       getLogger().log(FQCN, Level.FATAL, message, t);
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/* 228 */     if (this.logger == null) {
/* 229 */       this.logger = Logger.getLogger(this.name);
/*     */     }
/* 231 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 239 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 247 */     if (is12) {
/* 248 */       return getLogger().isEnabledFor((Priority)Level.ERROR);
/*     */     }
/* 250 */     return getLogger().isEnabledFor(Level.ERROR);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 259 */     if (is12) {
/* 260 */       return getLogger().isEnabledFor((Priority)Level.FATAL);
/*     */     }
/* 262 */     return getLogger().isEnabledFor(Level.FATAL);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 271 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 280 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 287 */     if (is12) {
/* 288 */       return getLogger().isEnabledFor((Priority)Level.WARN);
/*     */     }
/* 290 */     return getLogger().isEnabledFor(Level.WARN);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4JLogger
 * JD-Core Version:    0.6.0
 */
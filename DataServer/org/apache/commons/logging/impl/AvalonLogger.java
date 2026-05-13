/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.avalon.framework.logger.Logger;
/*     */ import org.apache.commons.logging.Log;
/*     */ 
/*     */ public class AvalonLogger
/*     */   implements Log, Serializable
/*     */ {
/*  49 */   private static Logger defaultLogger = null;
/*     */ 
/*  51 */   private transient Logger logger = null;
/*     */ 
/*  53 */   private String name = null;
/*     */ 
/*     */   public AvalonLogger(Logger logger)
/*     */   {
/*  61 */     this.name = this.name;
/*  62 */     this.logger = logger;
/*     */   }
/*     */ 
/*     */   public AvalonLogger(String name)
/*     */   {
/*  71 */     if (defaultLogger == null)
/*  72 */       throw new NullPointerException("default logger has to be specified if this constructor is used!");
/*  73 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/*  81 */     if (this.logger == null) {
/*  82 */       this.logger = defaultLogger.getChildLogger(this.name);
/*     */     }
/*  84 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public static void setDefaultLogger(Logger logger)
/*     */   {
/*  94 */     defaultLogger = logger;
/*     */   }
/*     */ 
/*     */   public void debug(Object o, Throwable t)
/*     */   {
/* 101 */     if (getLogger().isDebugEnabled()) getLogger().debug(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void debug(Object o)
/*     */   {
/* 108 */     if (getLogger().isDebugEnabled()) getLogger().debug(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public void error(Object o, Throwable t)
/*     */   {
/* 115 */     if (getLogger().isErrorEnabled()) getLogger().error(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void error(Object o)
/*     */   {
/* 122 */     if (getLogger().isErrorEnabled()) getLogger().error(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public void fatal(Object o, Throwable t)
/*     */   {
/* 129 */     if (getLogger().isFatalErrorEnabled()) getLogger().fatalError(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object o)
/*     */   {
/* 136 */     if (getLogger().isFatalErrorEnabled()) getLogger().fatalError(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public void info(Object o, Throwable t)
/*     */   {
/* 143 */     if (getLogger().isInfoEnabled()) getLogger().info(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void info(Object o)
/*     */   {
/* 150 */     if (getLogger().isInfoEnabled()) getLogger().info(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 157 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 164 */     return getLogger().isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 171 */     return getLogger().isFatalErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 178 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 185 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 192 */     return getLogger().isWarnEnabled();
/*     */   }
/*     */ 
/*     */   public void trace(Object o, Throwable t)
/*     */   {
/* 199 */     if (getLogger().isDebugEnabled()) getLogger().debug(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void trace(Object o)
/*     */   {
/* 206 */     if (getLogger().isDebugEnabled()) getLogger().debug(String.valueOf(o));
/*     */   }
/*     */ 
/*     */   public void warn(Object o, Throwable t)
/*     */   {
/* 213 */     if (getLogger().isWarnEnabled()) getLogger().warn(String.valueOf(o), t);
/*     */   }
/*     */ 
/*     */   public void warn(Object o)
/*     */   {
/* 220 */     if (getLogger().isWarnEnabled()) getLogger().warn(String.valueOf(o));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.AvalonLogger
 * JD-Core Version:    0.6.0
 */
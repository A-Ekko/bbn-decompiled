/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log.Hierarchy;
/*     */ import org.apache.log.Logger;
/*     */ 
/*     */ public class LogKitLogger
/*     */   implements Log, Serializable
/*     */ {
/*  47 */   protected transient Logger logger = null;
/*     */ 
/*  50 */   protected String name = null;
/*     */ 
/*     */   public LogKitLogger(String name)
/*     */   {
/*  63 */     this.name = name;
/*  64 */     this.logger = getLogger();
/*     */   }
/*     */ 
/*     */   public Logger getLogger()
/*     */   {
/*  76 */     if (this.logger == null) {
/*  77 */       this.logger = Hierarchy.getDefaultHierarchy().getLoggerFor(this.name);
/*     */     }
/*  79 */     return this.logger;
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/*  91 */     debug(message);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/*  99 */     debug(message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 107 */     if (message != null)
/* 108 */       getLogger().debug(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 117 */     if (message != null)
/* 118 */       getLogger().debug(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 127 */     if (message != null)
/* 128 */       getLogger().info(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 137 */     if (message != null)
/* 138 */       getLogger().info(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 147 */     if (message != null)
/* 148 */       getLogger().warn(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 157 */     if (message != null)
/* 158 */       getLogger().warn(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 167 */     if (message != null)
/* 168 */       getLogger().error(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 177 */     if (message != null)
/* 178 */       getLogger().error(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 187 */     if (message != null)
/* 188 */       getLogger().fatalError(String.valueOf(message));
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 197 */     if (message != null)
/* 198 */       getLogger().fatalError(String.valueOf(message), t);
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 207 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 215 */     return getLogger().isErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 223 */     return getLogger().isFatalErrorEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 231 */     return getLogger().isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 239 */     return getLogger().isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 247 */     return getLogger().isWarnEnabled();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.LogKitLogger
 * JD-Core Version:    0.6.0
 */
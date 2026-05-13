/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ 
/*     */ /** @deprecated */
/*     */ public final class Log4JCategoryLog
/*     */   implements Log
/*     */ {
/*  43 */   private static final String FQCN = Log4JCategoryLog.class.getName();
/*     */ 
/*  46 */   private Category category = null;
/*     */ 
/*     */   public Log4JCategoryLog()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Log4JCategoryLog(String name)
/*     */   {
/*  59 */     this.category = Category.getInstance(name);
/*     */   }
/*     */ 
/*     */   public Log4JCategoryLog(Category category)
/*     */   {
/*  65 */     this.category = category;
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/*  77 */     this.category.log(FQCN, Level.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/*  86 */     this.category.log(FQCN, Level.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/*  94 */     this.category.log(FQCN, Level.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 101 */     this.category.log(FQCN, Level.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 109 */     this.category.log(FQCN, Level.INFO, message, null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 117 */     this.category.log(FQCN, Level.INFO, message, t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 125 */     this.category.log(FQCN, Level.WARN, message, null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 133 */     this.category.log(FQCN, Level.WARN, message, t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 141 */     this.category.log(FQCN, Level.ERROR, message, null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 149 */     this.category.log(FQCN, Level.ERROR, message, t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 157 */     this.category.log(FQCN, Level.FATAL, message, null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 165 */     this.category.log(FQCN, Level.FATAL, message, t);
/*     */   }
/*     */ 
/*     */   public Category getCategory()
/*     */   {
/* 173 */     return this.category;
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 181 */     return this.category.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 189 */     return this.category.isEnabledFor(Level.ERROR);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 197 */     return this.category.isEnabledFor(Level.FATAL);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 205 */     return this.category.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 214 */     return this.category.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 221 */     return this.category.isEnabledFor(Level.WARN);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4JCategoryLog
 * JD-Core Version:    0.6.0
 */
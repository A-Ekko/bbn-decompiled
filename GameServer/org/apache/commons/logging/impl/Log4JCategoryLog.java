/*     */ package org.apache.commons.logging.impl;
/*     */ 
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ /** @deprecated */
/*     */ public final class Log4JCategoryLog
/*     */   implements Log
/*     */ {
/*  88 */   private static final String FQCN = Log4JCategoryLog.class.getName();
/*     */ 
/*  91 */   private Category category = null;
/*     */ 
/*     */   public Log4JCategoryLog()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Log4JCategoryLog(String name)
/*     */   {
/* 104 */     this.category = Category.getInstance(name);
/*     */   }
/*     */ 
/*     */   public Log4JCategoryLog(Category category)
/*     */   {
/* 110 */     this.category = category;
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 122 */     this.category.log(FQCN, Priority.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/* 131 */     this.category.log(FQCN, Priority.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/* 139 */     this.category.log(FQCN, Priority.DEBUG, message, null);
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/* 146 */     this.category.log(FQCN, Priority.DEBUG, message, t);
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/* 154 */     this.category.log(FQCN, Priority.INFO, message, null);
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/* 162 */     this.category.log(FQCN, Priority.INFO, message, t);
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/* 170 */     this.category.log(FQCN, Priority.WARN, message, null);
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/* 178 */     this.category.log(FQCN, Priority.WARN, message, t);
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/* 186 */     this.category.log(FQCN, Priority.ERROR, message, null);
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/* 194 */     this.category.log(FQCN, Priority.ERROR, message, t);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/* 202 */     this.category.log(FQCN, Priority.FATAL, message, null);
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/* 210 */     this.category.log(FQCN, Priority.FATAL, message, t);
/*     */   }
/*     */ 
/*     */   public Category getCategory()
/*     */   {
/* 218 */     return this.category;
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 226 */     return this.category.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isErrorEnabled()
/*     */   {
/* 234 */     return this.category.isEnabledFor(Priority.ERROR);
/*     */   }
/*     */ 
/*     */   public boolean isFatalEnabled()
/*     */   {
/* 242 */     return this.category.isEnabledFor(Priority.FATAL);
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 250 */     return this.category.isInfoEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 259 */     return this.category.isDebugEnabled();
/*     */   }
/*     */ 
/*     */   public boolean isWarnEnabled()
/*     */   {
/* 266 */     return this.category.isEnabledFor(Priority.WARN);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.commons.logging.impl.Log4JCategoryLog
 * JD-Core Version:    0.6.0
 */
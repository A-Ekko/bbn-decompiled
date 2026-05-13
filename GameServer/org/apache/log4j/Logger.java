/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class Logger extends Category
/*     */ {
/*  36 */   private static final String FQCN = Logger.class.getName();
/*     */ 
/*     */   protected Logger(String name)
/*     */   {
/*  41 */     super(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 105 */     return LogManager.getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 118 */     return LogManager.getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 136 */     return LogManager.getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 156 */     return LogManager.getLogger(name, factory);
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/* 167 */     if (this.repository.isDisabled(5000)) {
/* 168 */       return;
/*     */     }
/*     */ 
/* 171 */     if (Level.TRACE.isGreaterOrEqual(getEffectiveLevel()))
/* 172 */       forcedLog(FQCN, Level.TRACE, message, null);
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/* 189 */     if (this.repository.isDisabled(5000)) {
/* 190 */       return;
/*     */     }
/*     */ 
/* 193 */     if (Level.TRACE.isGreaterOrEqual(getEffectiveLevel()))
/* 194 */       forcedLog(FQCN, Level.TRACE, message, t);
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 206 */     if (this.repository.isDisabled(5000)) {
/* 207 */       return false;
/*     */     }
/*     */ 
/* 210 */     return Level.TRACE.isGreaterOrEqual(getEffectiveLevel());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.Logger
 * JD-Core Version:    0.6.0
 */
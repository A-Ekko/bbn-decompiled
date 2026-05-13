/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ 
/*     */ public class Logger extends Category
/*     */ {
/*  25 */   private static final String FQCN = Level.class.getName();
/*     */ 
/*     */   protected Logger(String name)
/*     */   {
/*  30 */     super(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/*  85 */     return LogManager.getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/*  94 */     return LogManager.getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 104 */     return LogManager.getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 124 */     return LogManager.getLogger(name, factory);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.Logger
 * JD-Core Version:    0.6.0
 */
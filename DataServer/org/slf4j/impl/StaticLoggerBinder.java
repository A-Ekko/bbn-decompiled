/*    */ package org.slf4j.impl;
/*    */ 
/*    */ import org.slf4j.ILoggerFactory;
/*    */ import org.slf4j.spi.LoggerFactoryBinder;
/*    */ 
/*    */ public class StaticLoggerBinder
/*    */   implements LoggerFactoryBinder
/*    */ {
/* 44 */   private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
/*    */ 
/* 61 */   public static String REQUESTED_API_VERSION = "1.5.8";
/*    */ 
/* 63 */   private static final String loggerFactoryClassStr = SimpleLoggerFactory.class.getName();
/*    */   private final ILoggerFactory loggerFactory;
/*    */ 
/*    */   public static final StaticLoggerBinder getSingleton()
/*    */   {
/* 52 */     return SINGLETON;
/*    */   }
/*    */ 
/*    */   private StaticLoggerBinder()
/*    */   {
/* 72 */     this.loggerFactory = new SimpleLoggerFactory();
/*    */   }
/*    */ 
/*    */   public ILoggerFactory getLoggerFactory() {
/* 76 */     return this.loggerFactory;
/*    */   }
/*    */ 
/*    */   public String getLoggerFactoryClassStr() {
/* 80 */     return loggerFactoryClassStr;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.impl.StaticLoggerBinder
 * JD-Core Version:    0.6.0
 */
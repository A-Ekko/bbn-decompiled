/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ public class DefaultRepositorySelector
/*    */   implements RepositorySelector
/*    */ {
/*    */   final LoggerRepository repository;
/*    */ 
/*    */   public DefaultRepositorySelector(LoggerRepository repository)
/*    */   {
/* 13 */     this.repository = repository;
/*    */   }
/*    */ 
/*    */   public LoggerRepository getLoggerRepository()
/*    */   {
/* 18 */     return this.repository;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.DefaultRepositorySelector
 * JD-Core Version:    0.6.0
 */
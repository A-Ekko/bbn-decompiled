/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ public class DefaultRepositorySelector
/*    */   implements RepositorySelector
/*    */ {
/*    */   final LoggerRepository repository;
/*    */ 
/*    */   public DefaultRepositorySelector(LoggerRepository repository)
/*    */   {
/* 29 */     this.repository = repository;
/*    */   }
/*    */ 
/*    */   public LoggerRepository getLoggerRepository()
/*    */   {
/* 34 */     return this.repository;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.DefaultRepositorySelector
 * JD-Core Version:    0.6.0
 */
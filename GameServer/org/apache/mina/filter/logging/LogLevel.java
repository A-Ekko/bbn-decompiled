/*    */ package org.apache.mina.filter.logging;
/*    */ 
/*    */ public enum LogLevel
/*    */ {
/* 35 */   TRACE(5), 
/*    */ 
/* 40 */   DEBUG(4), 
/*    */ 
/* 45 */   INFO(3), 
/*    */ 
/* 50 */   WARN(2), 
/*    */ 
/* 55 */   ERROR(1), 
/*    */ 
/* 60 */   NONE(0);
/*    */ 
/*    */   private int level;
/*    */ 
/*    */   private LogLevel(int level)
/*    */   {
/* 71 */     this.level = level;
/*    */   }
/*    */ 
/*    */   public int getLevel()
/*    */   {
/* 79 */     return this.level;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.filter.logging.LogLevel
 * JD-Core Version:    0.6.0
 */
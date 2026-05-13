/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ public final class RootLogger extends Logger
/*    */ {
/*    */   public RootLogger(Level level)
/*    */   {
/* 44 */     super("root");
/* 45 */     setLevel(level);
/*    */   }
/*    */ 
/*    */   public final Level getChainedLevel()
/*    */   {
/* 53 */     return this.level;
/*    */   }
/*    */ 
/*    */   public final void setLevel(Level level)
/*    */   {
/* 62 */     if (level == null) {
/* 63 */       LogLog.error("You have tried to set a null level to root.", new Throwable());
/*    */     }
/*    */     else
/* 66 */       this.level = level;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.RootLogger
 * JD-Core Version:    0.6.0
 */
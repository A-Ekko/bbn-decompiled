/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ /** @deprecated */
/*    */ public final class RootCategory extends Logger
/*    */ {
/*    */   public RootCategory(Level level)
/*    */   {
/* 36 */     super("root");
/* 37 */     setLevel(level);
/*    */   }
/*    */ 
/*    */   public final Level getChainedLevel()
/*    */   {
/* 48 */     return this.level;
/*    */   }
/*    */ 
/*    */   public final void setLevel(Level level)
/*    */   {
/* 59 */     if (level == null) {
/* 60 */       LogLog.error("You have tried to set a null level to root.", new Throwable());
/*    */     }
/*    */     else
/*    */     {
/* 64 */       this.level = level;
/*    */     }
/*    */   }
/*    */ 
/*    */   public final void setPriority(Level level)
/*    */   {
/* 71 */     setLevel(level);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.spi.RootCategory
 * JD-Core Version:    0.6.0
 */
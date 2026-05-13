/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ 
/*    */ public final class RootCategory extends Logger
/*    */ {
/*    */   public RootCategory(Level level)
/*    */   {
/* 35 */     super("root");
/* 36 */     setLevel(level);
/*    */   }
/*    */ 
/*    */   public final Level getChainedLevel()
/*    */   {
/* 47 */     return this.level;
/*    */   }
/*    */ 
/*    */   public final void setLevel(Level level)
/*    */   {
/* 58 */     if (level == null) {
/* 59 */       LogLog.error("You have tried to set a null level to root.", new Throwable());
/*    */     }
/*    */     else
/*    */     {
/* 63 */       this.level = level;
/*    */     }
/*    */   }
/*    */ 
/*    */   public final void setPriority(Level level)
/*    */   {
/* 70 */     setLevel(level);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.spi.RootCategory
 * JD-Core Version:    0.6.0
 */
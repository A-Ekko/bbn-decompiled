/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.Priority;
/*    */ import org.apache.log4j.helpers.OptionConverter;
/*    */ import org.apache.log4j.spi.Filter;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class LevelMatchFilter extends Filter
/*    */ {
/* 36 */   boolean acceptOnMatch = true;
/*    */   Level levelToMatch;
/*    */ 
/*    */   public void setLevelToMatch(String level)
/*    */   {
/* 45 */     this.levelToMatch = OptionConverter.toLevel(level, null);
/*    */   }
/*    */ 
/*    */   public String getLevelToMatch()
/*    */   {
/* 50 */     return this.levelToMatch == null ? null : this.levelToMatch.toString();
/*    */   }
/*    */ 
/*    */   public void setAcceptOnMatch(boolean acceptOnMatch)
/*    */   {
/* 55 */     this.acceptOnMatch = acceptOnMatch;
/*    */   }
/*    */ 
/*    */   public boolean getAcceptOnMatch()
/*    */   {
/* 60 */     return this.acceptOnMatch;
/*    */   }
/*    */ 
/*    */   public int decide(LoggingEvent event)
/*    */   {
/* 77 */     if (this.levelToMatch == null) {
/* 78 */       return 0;
/*    */     }
/*    */ 
/* 81 */     boolean matchOccured = false;
/* 82 */     if (this.levelToMatch.equals(event.getLevel())) {
/* 83 */       matchOccured = true;
/*    */     }
/*    */ 
/* 86 */     if (matchOccured) {
/* 87 */       if (this.acceptOnMatch) {
/* 88 */         return 1;
/*    */       }
/* 90 */       return -1;
/*    */     }
/* 92 */     return 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.varia.LevelMatchFilter
 * JD-Core Version:    0.6.0
 */
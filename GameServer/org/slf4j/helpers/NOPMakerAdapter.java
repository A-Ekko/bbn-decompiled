/*    */ package org.slf4j.helpers;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.slf4j.spi.MDCAdapter;
/*    */ 
/*    */ public class NOPMakerAdapter
/*    */   implements MDCAdapter
/*    */ {
/*    */   public void clear()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String get(String key)
/*    */   {
/* 22 */     return null;
/*    */   }
/*    */ 
/*    */   public void put(String key, String val) {
/*    */   }
/*    */ 
/*    */   public void remove(String key) {
/*    */   }
/*    */ 
/*    */   public Map getCopyOfContextMap() {
/* 32 */     return null;
/*    */   }
/*    */ 
/*    */   public void setContextMap(Map contextMap)
/*    */   {
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.slf4j.helpers.NOPMakerAdapter
 * JD-Core Version:    0.6.0
 */
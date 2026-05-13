/*    */ package org.slf4j.helpers;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.slf4j.ILoggerFactory;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class SubstituteLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/* 56 */   final List loggerNameList = new ArrayList();
/*    */ 
/*    */   public Logger getLogger(String name) {
/* 59 */     this.loggerNameList.add(name);
/* 60 */     return NOPLogger.NOP_LOGGER;
/*    */   }
/*    */ 
/*    */   public List getLoggerNameList() {
/* 64 */     return this.loggerNameList;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.helpers.SubstituteLoggerFactory
 * JD-Core Version:    0.6.0
 */
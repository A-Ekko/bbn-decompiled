/*    */ package org.slf4j.impl;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.slf4j.ILoggerFactory;
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class SimpleLoggerFactory
/*    */   implements ILoggerFactory
/*    */ {
/* 50 */   static final SimpleLoggerFactory INSTANCE = new SimpleLoggerFactory();
/*    */   Map loggerMap;
/*    */ 
/*    */   public SimpleLoggerFactory()
/*    */   {
/* 55 */     this.loggerMap = new HashMap();
/*    */   }
/*    */ 
/*    */   public Logger getLogger(String name)
/*    */   {
/* 62 */     Logger slogger = null;
/*    */ 
/* 64 */     synchronized (this) {
/* 65 */       slogger = (Logger)this.loggerMap.get(name);
/* 66 */       if (slogger == null) {
/* 67 */         slogger = new SimpleLogger(name);
/* 68 */         this.loggerMap.put(name, slogger);
/*    */       }
/*    */     }
/* 71 */     return slogger;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.impl.SimpleLoggerFactory
 * JD-Core Version:    0.6.0
 */
/*    */ package org.slf4j.impl;
/*    */ 
/*    */ import org.slf4j.helpers.NOPMakerAdapter;
/*    */ import org.slf4j.spi.MDCAdapter;
/*    */ 
/*    */ public class StaticMDCBinder
/*    */ {
/* 42 */   public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();
/*    */ 
/*    */   public MDCAdapter getMDCA()
/*    */   {
/* 52 */     return new NOPMakerAdapter();
/*    */   }
/*    */ 
/*    */   public String getMDCAdapterClassStr() {
/* 56 */     return NOPMakerAdapter.class.getName();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.impl.StaticMDCBinder
 * JD-Core Version:    0.6.0
 */
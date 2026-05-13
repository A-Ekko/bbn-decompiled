/*    */ package org.slf4j.impl;
/*    */ 
/*    */ import org.slf4j.IMarkerFactory;
/*    */ import org.slf4j.helpers.BasicMarkerFactory;
/*    */ import org.slf4j.spi.MarkerFactoryBinder;
/*    */ 
/*    */ public class StaticMarkerBinder
/*    */   implements MarkerFactoryBinder
/*    */ {
/* 45 */   public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
/*    */ 
/* 47 */   final IMarkerFactory markerFactory = new BasicMarkerFactory();
/*    */ 
/*    */   public IMarkerFactory getMarkerFactory()
/*    */   {
/* 57 */     return this.markerFactory;
/*    */   }
/*    */ 
/*    */   public String getMarkerFactoryClassStr()
/*    */   {
/* 65 */     return BasicMarkerFactory.class.getName();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.impl.StaticMarkerBinder
 * JD-Core Version:    0.6.0
 */
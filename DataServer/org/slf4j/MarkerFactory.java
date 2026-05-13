/*    */ package org.slf4j;
/*    */ 
/*    */ import org.slf4j.helpers.Util;
/*    */ import org.slf4j.impl.StaticMarkerBinder;
/*    */ 
/*    */ public class MarkerFactory
/*    */ {
/*    */   static IMarkerFactory markerFactory;
/*    */ 
/*    */   public static Marker getMarker(String name)
/*    */   {
/* 68 */     return markerFactory.getMarker(name);
/*    */   }
/*    */ 
/*    */   public static Marker getDetachedMarker(String name)
/*    */   {
/* 78 */     return markerFactory.getDetachedMarker(name);
/*    */   }
/*    */ 
/*    */   public static IMarkerFactory getIMarkerFactory()
/*    */   {
/* 90 */     return markerFactory;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 51 */       markerFactory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
/*    */     }
/*    */     catch (Exception e) {
/* 54 */       Util.reportFailure("Could not instantiate instance of class [" + StaticMarkerBinder.SINGLETON.getMarkerFactoryClassStr() + "]", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.MarkerFactory
 * JD-Core Version:    0.6.0
 */
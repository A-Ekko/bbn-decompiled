/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Adjustable;
/*    */ import java.awt.event.AdjustmentEvent;
/*    */ import java.awt.event.AdjustmentListener;
/*    */ 
/*    */ public class TrackingAdjustmentListener
/*    */   implements AdjustmentListener
/*    */ {
/* 37 */   protected int _lastMaximum = -1;
/*    */ 
/*    */   public void adjustmentValueChanged(AdjustmentEvent e)
/*    */   {
/* 52 */     Adjustable bar = e.getAdjustable();
/* 53 */     int currentMaximum = bar.getMaximum();
/* 54 */     if (bar.getMaximum() == this._lastMaximum) {
/* 55 */       return;
/*    */     }
/* 57 */     int bottom = bar.getValue() + bar.getVisibleAmount();
/*    */ 
/* 59 */     if (bottom + bar.getUnitIncrement() >= this._lastMaximum) {
/* 60 */       bar.setValue(bar.getMaximum());
/*    */     }
/* 62 */     this._lastMaximum = currentMaximum;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.TrackingAdjustmentListener
 * JD-Core Version:    0.6.0
 */
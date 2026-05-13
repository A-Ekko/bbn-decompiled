/*    */ package org.logicalcobwebs.proxool.admin;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.logicalcobwebs.proxool.util.AbstractListenerContainer;
/*    */ 
/*    */ public class CompositeStatisticsListener extends AbstractListenerContainer
/*    */   implements StatisticsListenerIF
/*    */ {
/* 26 */   static final Log LOG = LogFactory.getLog(CompositeStatisticsListener.class);
/*    */ 
/*    */   public void statistics(String alias, StatisticsIF statistics)
/*    */   {
/* 33 */     Object[] listeners = getListeners();
/*    */ 
/* 35 */     for (int i = 0; i < listeners.length; i++)
/*    */       try {
/* 37 */         StatisticsListenerIF statisticsListener = (StatisticsListenerIF)listeners[i];
/* 38 */         statisticsListener.statistics(alias, statistics);
/*    */       }
/*    */       catch (RuntimeException re) {
/* 41 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching statistics event", re);
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.CompositeStatisticsListener
 * JD-Core Version:    0.6.0
 */
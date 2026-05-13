/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.logicalcobwebs.proxool.util.AbstractListenerContainer;
/*    */ 
/*    */ public class CompositeStateListener extends AbstractListenerContainer
/*    */   implements StateListenerIF
/*    */ {
/* 26 */   static final Log LOG = LogFactory.getLog(CompositeStateListener.class);
/*    */ 
/*    */   public void upStateChanged(int upState)
/*    */   {
/* 33 */     Object[] listeners = getListeners();
/*    */ 
/* 35 */     for (int i = 0; i < listeners.length; i++)
/*    */       try {
/* 37 */         StateListenerIF stateListener = (StateListenerIF)listeners[i];
/* 38 */         stateListener.upStateChanged(upState);
/*    */       }
/*    */       catch (RuntimeException re) {
/* 41 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching upStateChanged event", re);
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.CompositeStateListener
 * JD-Core Version:    0.6.0
 */
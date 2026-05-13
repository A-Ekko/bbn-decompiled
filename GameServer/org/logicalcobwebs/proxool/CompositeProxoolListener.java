/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.logicalcobwebs.proxool.util.AbstractListenerContainer;
/*    */ 
/*    */ public class CompositeProxoolListener extends AbstractListenerContainer
/*    */   implements ProxoolListenerIF
/*    */ {
/* 28 */   static final Log LOG = LogFactory.getLog(CompositeProxoolListener.class);
/*    */ 
/*    */   public void onRegistration(ConnectionPoolDefinitionIF connectionPoolDefinition, Properties completeInfo)
/*    */   {
/* 36 */     Object[] listeners = getListeners();
/*    */ 
/* 38 */     for (int i = 0; i < listeners.length; i++)
/*    */       try {
/* 40 */         ProxoolListenerIF proxoolListener = (ProxoolListenerIF)listeners[i];
/* 41 */         proxoolListener.onRegistration(connectionPoolDefinition, (Properties)completeInfo.clone());
/*    */       }
/*    */       catch (RuntimeException re) {
/* 44 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onRegistration event", re);
/*    */       }
/*    */   }
/*    */ 
/*    */   public void onShutdown(String alias)
/*    */   {
/* 54 */     Object[] listeners = getListeners();
/*    */ 
/* 56 */     for (int i = 0; i < listeners.length; i++)
/*    */       try {
/* 58 */         ProxoolListenerIF proxoolListener = (ProxoolListenerIF)listeners[i];
/* 59 */         proxoolListener.onShutdown(alias);
/*    */       }
/*    */       catch (RuntimeException re) {
/* 62 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onShutdown event", re);
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.CompositeProxoolListener
 * JD-Core Version:    0.6.0
 */
/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.logicalcobwebs.proxool.util.AbstractListenerContainer;
/*    */ 
/*    */ public class CompositeConfigurationListener extends AbstractListenerContainer
/*    */   implements ConfigurationListenerIF
/*    */ {
/* 28 */   static final Log LOG = LogFactory.getLog(CompositeConfigurationListener.class);
/*    */ 
/*    */   public void definitionUpdated(ConnectionPoolDefinitionIF connectionPoolDefinition, Properties completeInfo, Properties changedInfo)
/*    */   {
/* 37 */     Object[] listeners = getListeners();
/*    */ 
/* 39 */     for (int i = 0; i < listeners.length; i++)
/*    */       try {
/* 41 */         ConfigurationListenerIF configurationListener = (ConfigurationListenerIF)listeners[i];
/* 42 */         configurationListener.definitionUpdated(connectionPoolDefinition, (Properties)completeInfo.clone(), (Properties)changedInfo.clone());
/*    */       }
/*    */       catch (RuntimeException re) {
/* 45 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching event", re);
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.CompositeConfigurationListener
 * JD-Core Version:    0.6.0
 */
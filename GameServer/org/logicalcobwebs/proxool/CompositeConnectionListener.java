/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.util.AbstractListenerContainer;
/*     */ 
/*     */ public class CompositeConnectionListener extends AbstractListenerContainer
/*     */   implements ConnectionListenerIF
/*     */ {
/*  29 */   static final Log LOG = LogFactory.getLog(CompositeConnectionListener.class);
/*     */ 
/*     */   public void onBirth(Connection connection)
/*     */     throws SQLException
/*     */   {
/*  36 */     Object[] listeners = getListeners();
/*     */ 
/*  38 */     for (int i = 0; i < listeners.length; i++)
/*     */       try {
/*  40 */         ConnectionListenerIF connectionListener = (ConnectionListenerIF)listeners[i];
/*  41 */         connectionListener.onBirth(connection);
/*     */       }
/*     */       catch (RuntimeException re) {
/*  44 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onBirth event", re);
/*     */       }
/*     */       catch (SQLException se) {
/*  47 */         LOG.warn("SQLException received from listener " + listeners[i] + " when dispatching onBirth event - event dispatching cancelled");
/*  48 */         throw se;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onDeath(Connection connection, int reasonCode)
/*     */     throws SQLException
/*     */   {
/*  58 */     Object[] listeners = getListeners();
/*     */ 
/*  60 */     for (int i = 0; i < listeners.length; i++)
/*     */       try {
/*  62 */         ConnectionListenerIF connectionListener = (ConnectionListenerIF)listeners[i];
/*  63 */         connectionListener.onDeath(connection, reasonCode);
/*     */       }
/*     */       catch (RuntimeException re) {
/*  66 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onDeath event", re);
/*     */       }
/*     */       catch (SQLException se) {
/*  69 */         LOG.warn("SQLException received from listener " + listeners[i] + " when dispatching onDeath event - event dispatching cancelled");
/*  70 */         throw se;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onExecute(String command, long elapsedTime)
/*     */   {
/*  80 */     Object[] listeners = getListeners();
/*     */ 
/*  82 */     for (int i = 0; i < listeners.length; i++)
/*     */       try {
/*  84 */         ConnectionListenerIF connectionListener = (ConnectionListenerIF)listeners[i];
/*  85 */         connectionListener.onExecute(command, elapsedTime);
/*     */       }
/*     */       catch (RuntimeException re) {
/*  88 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onExecute event", re);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void onFail(String command, Exception exception)
/*     */   {
/*  98 */     Object[] listeners = getListeners();
/*     */ 
/* 100 */     for (int i = 0; i < listeners.length; i++)
/*     */       try {
/* 102 */         ConnectionListenerIF connectionListener = (ConnectionListenerIF)listeners[i];
/* 103 */         connectionListener.onFail(command, exception);
/*     */       }
/*     */       catch (RuntimeException re) {
/* 106 */         LOG.warn("RuntimeException received from listener " + listeners[i] + " when dispatching onFail event", re);
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.CompositeConnectionListener
 * JD-Core Version:    0.6.0
 */
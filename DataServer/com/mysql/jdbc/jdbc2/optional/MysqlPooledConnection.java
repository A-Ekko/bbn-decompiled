/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import javax.sql.ConnectionEvent;
/*     */ import javax.sql.ConnectionEventListener;
/*     */ import javax.sql.PooledConnection;
/*     */ 
/*     */ public class MysqlPooledConnection
/*     */   implements PooledConnection
/*     */ {
/*     */   public static final int CONNECTION_ERROR_EVENT = 1;
/*     */   public static final int CONNECTION_CLOSED_EVENT = 2;
/*     */   private Hashtable eventListeners;
/*     */   private java.sql.Connection logicalHandle;
/*     */   private com.mysql.jdbc.Connection physicalConn;
/*     */ 
/*     */   public MysqlPooledConnection(com.mysql.jdbc.Connection connection)
/*     */   {
/*  75 */     this.logicalHandle = null;
/*  76 */     this.physicalConn = connection;
/*  77 */     this.eventListeners = new Hashtable(10);
/*     */   }
/*     */ 
/*     */   public synchronized void addConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/*  92 */     if (this.eventListeners != null)
/*  93 */       this.eventListeners.put(connectioneventlistener, connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized void removeConnectionEventListener(ConnectionEventListener connectioneventlistener)
/*     */   {
/* 108 */     if (this.eventListeners != null)
/* 109 */       this.eventListeners.remove(connectioneventlistener);
/*     */   }
/*     */ 
/*     */   public synchronized java.sql.Connection getConnection()
/*     */     throws SQLException
/*     */   {
/* 121 */     if (this.physicalConn == null)
/*     */     {
/* 123 */       SQLException sqlException = new SQLException("Physical Connection doesn't exist");
/*     */ 
/* 125 */       callListener(1, sqlException);
/*     */ 
/* 127 */       return null;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 132 */       if (this.logicalHandle != null) {
/* 133 */         ((ConnectionWrapper)this.logicalHandle).close(false);
/*     */       }
/*     */ 
/* 136 */       this.physicalConn.resetServerState();
/*     */ 
/* 138 */       this.logicalHandle = new ConnectionWrapper(this, this.physicalConn);
/*     */     } catch (SQLException sqlException) {
/* 140 */       callListener(1, sqlException);
/*     */ 
/* 142 */       return null;
/*     */     }
/*     */ 
/* 145 */     return this.logicalHandle;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/* 156 */     if (this.physicalConn != null) {
/* 157 */       this.physicalConn.close();
/*     */     }
/*     */ 
/* 160 */     this.physicalConn = null;
/*     */   }
/*     */ 
/*     */   protected synchronized void callListener(int eventType, SQLException sqlException)
/*     */   {
/* 178 */     if (this.eventListeners == null)
/*     */     {
/* 180 */       return;
/*     */     }
/*     */ 
/* 183 */     Enumeration enumeration = this.eventListeners.keys();
/* 184 */     ConnectionEvent connectionevent = new ConnectionEvent(this, sqlException);
/*     */ 
/* 187 */     while (enumeration.hasMoreElements())
/*     */     {
/* 189 */       ConnectionEventListener connectioneventlistener = (ConnectionEventListener)enumeration.nextElement();
/*     */ 
/* 191 */       ConnectionEventListener connectioneventlistener1 = (ConnectionEventListener)this.eventListeners.get(connectioneventlistener);
/*     */ 
/* 194 */       if (eventType == 2)
/* 195 */         connectioneventlistener1.connectionClosed(connectionevent);
/* 196 */       else if (eventType == 1)
/* 197 */         connectioneventlistener1.connectionErrorOccurred(connectionevent);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlPooledConnection
 * JD-Core Version:    0.6.0
 */
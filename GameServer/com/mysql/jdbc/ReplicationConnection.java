/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class ReplicationConnection
/*     */   implements java.sql.Connection
/*     */ {
/*     */   private Connection currentConnection;
/*     */   private Connection masterConnection;
/*     */   private Connection slavesConnection;
/*     */ 
/*     */   public ReplicationConnection(Properties masterProperties, Properties slaveProperties)
/*     */     throws SQLException
/*     */   {
/*  51 */     Driver driver = new Driver();
/*     */ 
/*  53 */     this.masterConnection = ((Connection)driver.connect("jdbc:mysql:///", masterProperties));
/*     */ 
/*  55 */     this.slavesConnection = ((Connection)driver.connect("jdbc:mysql:///", slaveProperties));
/*     */ 
/*  57 */     this.currentConnection = this.masterConnection;
/*     */   }
/*     */ 
/*     */   public synchronized void clearWarnings()
/*     */     throws SQLException
/*     */   {
/*  66 */     this.currentConnection.clearWarnings();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */     throws SQLException
/*     */   {
/*  75 */     this.masterConnection.close();
/*  76 */     this.slavesConnection.close();
/*     */   }
/*     */ 
/*     */   public synchronized void commit()
/*     */     throws SQLException
/*     */   {
/*  85 */     this.currentConnection.commit();
/*     */   }
/*     */ 
/*     */   public Statement createStatement()
/*     */     throws SQLException
/*     */   {
/*  94 */     return this.currentConnection.createStatement();
/*     */   }
/*     */ 
/*     */   public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 104 */     return this.currentConnection.createStatement(resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 116 */     return this.currentConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   public synchronized boolean getAutoCommit()
/*     */     throws SQLException
/*     */   {
/* 126 */     return this.currentConnection.getAutoCommit();
/*     */   }
/*     */ 
/*     */   public synchronized String getCatalog()
/*     */     throws SQLException
/*     */   {
/* 135 */     return this.currentConnection.getCatalog();
/*     */   }
/*     */ 
/*     */   public synchronized Connection getCurrentConnection() {
/* 139 */     return this.currentConnection;
/*     */   }
/*     */ 
/*     */   public synchronized int getHoldability()
/*     */     throws SQLException
/*     */   {
/* 148 */     return this.currentConnection.getHoldability();
/*     */   }
/*     */ 
/*     */   public synchronized Connection getMasterConnection() {
/* 152 */     return this.masterConnection;
/*     */   }
/*     */ 
/*     */   public synchronized DatabaseMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/* 161 */     return this.currentConnection.getMetaData();
/*     */   }
/*     */ 
/*     */   public synchronized Connection getSlavesConnection() {
/* 165 */     return this.slavesConnection;
/*     */   }
/*     */ 
/*     */   public synchronized int getTransactionIsolation()
/*     */     throws SQLException
/*     */   {
/* 174 */     return this.currentConnection.getTransactionIsolation();
/*     */   }
/*     */ 
/*     */   public synchronized Map getTypeMap()
/*     */     throws SQLException
/*     */   {
/* 183 */     return this.currentConnection.getTypeMap();
/*     */   }
/*     */ 
/*     */   public synchronized SQLWarning getWarnings()
/*     */     throws SQLException
/*     */   {
/* 192 */     return this.currentConnection.getWarnings();
/*     */   }
/*     */ 
/*     */   public synchronized boolean isClosed()
/*     */     throws SQLException
/*     */   {
/* 201 */     return this.currentConnection.isClosed();
/*     */   }
/*     */ 
/*     */   public synchronized boolean isReadOnly()
/*     */     throws SQLException
/*     */   {
/* 210 */     return this.currentConnection == this.slavesConnection;
/*     */   }
/*     */ 
/*     */   public synchronized String nativeSQL(String sql)
/*     */     throws SQLException
/*     */   {
/* 219 */     return this.currentConnection.nativeSQL(sql);
/*     */   }
/*     */ 
/*     */   public CallableStatement prepareCall(String sql)
/*     */     throws SQLException
/*     */   {
/* 228 */     return this.currentConnection.prepareCall(sql);
/*     */   }
/*     */ 
/*     */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 238 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 250 */     return this.currentConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql)
/*     */     throws SQLException
/*     */   {
/* 260 */     return this.currentConnection.prepareStatement(sql);
/*     */   }
/*     */ 
/*     */   public synchronized PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
/*     */     throws SQLException
/*     */   {
/* 270 */     return this.currentConnection.prepareStatement(sql, autoGeneratedKeys);
/*     */   }
/*     */ 
/*     */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 280 */     return this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
/*     */   }
/*     */ 
/*     */   public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*     */     throws SQLException
/*     */   {
/* 293 */     return this.currentConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
/*     */   }
/*     */ 
/*     */   public synchronized PreparedStatement prepareStatement(String sql, int[] columnIndexes)
/*     */     throws SQLException
/*     */   {
/* 304 */     return this.currentConnection.prepareStatement(sql, columnIndexes);
/*     */   }
/*     */ 
/*     */   public synchronized PreparedStatement prepareStatement(String sql, String[] columnNames)
/*     */     throws SQLException
/*     */   {
/* 315 */     return this.currentConnection.prepareStatement(sql, columnNames);
/*     */   }
/*     */ 
/*     */   public synchronized void releaseSavepoint(Savepoint savepoint)
/*     */     throws SQLException
/*     */   {
/* 325 */     this.currentConnection.releaseSavepoint(savepoint);
/*     */   }
/*     */ 
/*     */   public synchronized void rollback()
/*     */     throws SQLException
/*     */   {
/* 334 */     this.currentConnection.rollback();
/*     */   }
/*     */ 
/*     */   public synchronized void rollback(Savepoint savepoint)
/*     */     throws SQLException
/*     */   {
/* 343 */     this.currentConnection.rollback(savepoint);
/*     */   }
/*     */ 
/*     */   public synchronized void setAutoCommit(boolean autoCommit)
/*     */     throws SQLException
/*     */   {
/* 353 */     this.currentConnection.setAutoCommit(autoCommit);
/*     */   }
/*     */ 
/*     */   public synchronized void setCatalog(String catalog)
/*     */     throws SQLException
/*     */   {
/* 362 */     this.currentConnection.setCatalog(catalog);
/*     */   }
/*     */ 
/*     */   public synchronized void setHoldability(int holdability)
/*     */     throws SQLException
/*     */   {
/* 372 */     this.currentConnection.setHoldability(holdability);
/*     */   }
/*     */ 
/*     */   public synchronized void setReadOnly(boolean readOnly)
/*     */     throws SQLException
/*     */   {
/* 381 */     if (readOnly)
/* 382 */       switchToSlavesConnection();
/*     */     else
/* 384 */       switchToMasterConnection();
/*     */   }
/*     */ 
/*     */   public synchronized Savepoint setSavepoint()
/*     */     throws SQLException
/*     */   {
/* 394 */     return this.currentConnection.setSavepoint();
/*     */   }
/*     */ 
/*     */   public synchronized Savepoint setSavepoint(String name)
/*     */     throws SQLException
/*     */   {
/* 403 */     return this.currentConnection.setSavepoint(name);
/*     */   }
/*     */ 
/*     */   public synchronized void setTransactionIsolation(int level)
/*     */     throws SQLException
/*     */   {
/* 413 */     this.currentConnection.setTransactionIsolation(level);
/*     */   }
/*     */ 
/*     */   public synchronized void setTypeMap(Map arg0)
/*     */     throws SQLException
/*     */   {
/* 424 */     this.currentConnection.setTypeMap(arg0);
/*     */   }
/*     */ 
/*     */   private synchronized void switchToMasterConnection() throws SQLException {
/* 428 */     String slaveCatalog = this.slavesConnection.getCatalog();
/* 429 */     String masterCatalog = this.masterConnection.getCatalog();
/*     */ 
/* 431 */     if ((slaveCatalog != null) && (!slaveCatalog.equals(masterCatalog)))
/* 432 */       this.masterConnection.setCatalog(slaveCatalog);
/* 433 */     else if (masterCatalog != null) {
/* 434 */       this.masterConnection.setCatalog(masterCatalog);
/*     */     }
/*     */ 
/* 437 */     boolean slavesAutoCommit = this.slavesConnection.getAutoCommit();
/*     */ 
/* 439 */     if (this.masterConnection.getAutoCommit() != slavesAutoCommit) {
/* 440 */       this.masterConnection.setAutoCommit(slavesAutoCommit);
/*     */     }
/*     */ 
/* 443 */     int slavesTransactionIsolation = this.slavesConnection.getTransactionIsolation();
/*     */ 
/* 446 */     if (this.masterConnection.getTransactionIsolation() != slavesTransactionIsolation) {
/* 447 */       this.masterConnection.setTransactionIsolation(slavesTransactionIsolation);
/*     */     }
/*     */ 
/* 451 */     this.currentConnection = this.masterConnection;
/*     */   }
/*     */ 
/*     */   private synchronized void switchToSlavesConnection() throws SQLException {
/* 455 */     String slaveCatalog = this.slavesConnection.getCatalog();
/* 456 */     String masterCatalog = this.masterConnection.getCatalog();
/*     */ 
/* 458 */     if ((masterCatalog != null) && (!masterCatalog.equals(slaveCatalog)))
/* 459 */       this.slavesConnection.setCatalog(masterCatalog);
/* 460 */     else if (slaveCatalog != null) {
/* 461 */       this.slavesConnection.setCatalog(slaveCatalog);
/*     */     }
/*     */ 
/* 464 */     boolean masterAutoCommit = this.masterConnection.getAutoCommit();
/*     */ 
/* 466 */     if (this.slavesConnection.getAutoCommit() != masterAutoCommit) {
/* 467 */       this.slavesConnection.setAutoCommit(masterAutoCommit);
/*     */     }
/*     */ 
/* 470 */     int masterTransactionIsolation = this.masterConnection.getTransactionIsolation();
/*     */ 
/* 473 */     if (this.slavesConnection.getTransactionIsolation() != masterTransactionIsolation) {
/* 474 */       this.slavesConnection.setTransactionIsolation(masterTransactionIsolation);
/*     */     }
/*     */ 
/* 477 */     this.currentConnection = this.slavesConnection;
/*     */ 
/* 479 */     this.slavesConnection.setAutoCommit(this.masterConnection.getAutoCommit());
/*     */ 
/* 481 */     this.slavesConnection.setTransactionIsolation(this.masterConnection.getTransactionIsolation());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationConnection
 * JD-Core Version:    0.6.0
 */
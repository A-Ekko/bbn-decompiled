/*     */ package com.mysql.jdbc.jdbc2.optional;
/*     */ 
/*     */ import java.sql.CallableStatement;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.SQLWarning;
/*     */ import java.sql.Savepoint;
/*     */ import java.sql.Statement;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ConnectionWrapper extends WrapperBase
/*     */   implements java.sql.Connection
/*     */ {
/*  55 */   private com.mysql.jdbc.Connection mc = null;
/*     */ 
/*  57 */   private MysqlPooledConnection mpc = null;
/*     */ 
/*  59 */   private String invalidHandleStr = "Logical handle no longer valid";
/*     */   private boolean closed;
/*     */ 
/*     */   public ConnectionWrapper(MysqlPooledConnection mysqlPooledConnection, com.mysql.jdbc.Connection mysqlConnection)
/*     */     throws SQLException
/*     */   {
/*  76 */     this.mpc = mysqlPooledConnection;
/*  77 */     this.mc = mysqlConnection;
/*  78 */     this.closed = false;
/*  79 */     this.pooledConnection = this.mpc;
/*     */   }
/*     */ 
/*     */   public void setAutoCommit(boolean autoCommit)
/*     */     throws SQLException
/*     */   {
/*  89 */     checkClosed();
/*     */     try
/*     */     {
/*  92 */       this.mc.setAutoCommit(autoCommit);
/*     */     } catch (SQLException sqlException) {
/*  94 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean getAutoCommit()
/*     */     throws SQLException
/*     */   {
/* 105 */     checkClosed();
/*     */     try
/*     */     {
/* 108 */       return this.mc.getAutoCommit();
/*     */     } catch (SQLException sqlException) {
/* 110 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public void setCatalog(String catalog)
/*     */     throws SQLException
/*     */   {
/* 123 */     checkClosed();
/*     */     try
/*     */     {
/* 126 */       this.mc.setCatalog(catalog);
/*     */     } catch (SQLException sqlException) {
/* 128 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getCatalog()
/*     */     throws SQLException
/*     */   {
/* 142 */     checkClosed();
/*     */     try
/*     */     {
/* 145 */       return this.mc.getCatalog();
/*     */     } catch (SQLException sqlException) {
/* 147 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */     throws SQLException
/*     */   {
/* 160 */     return (this.closed) || (this.mc.isClosed());
/*     */   }
/*     */ 
/*     */   public boolean isMasterConnection() throws SQLException {
/* 164 */     return this.mc.isMasterConnection();
/*     */   }
/*     */ 
/*     */   public void setHoldability(int arg0)
/*     */     throws SQLException
/*     */   {
/* 171 */     checkClosed();
/*     */     try
/*     */     {
/* 174 */       this.mc.setHoldability(arg0);
/*     */     } catch (SQLException sqlException) {
/* 176 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getHoldability()
/*     */     throws SQLException
/*     */   {
/* 184 */     checkClosed();
/*     */     try
/*     */     {
/* 187 */       return this.mc.getHoldability();
/*     */     } catch (SQLException sqlException) {
/* 189 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 192 */     return 1;
/*     */   }
/*     */ 
/*     */   public long getIdleFor()
/*     */   {
/* 202 */     return this.mc.getIdleFor();
/*     */   }
/*     */ 
/*     */   public DatabaseMetaData getMetaData()
/*     */     throws SQLException
/*     */   {
/* 215 */     checkClosed();
/*     */     try
/*     */     {
/* 218 */       return this.mc.getMetaData();
/*     */     } catch (SQLException sqlException) {
/* 220 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 223 */     return null;
/*     */   }
/*     */ 
/*     */   public void setReadOnly(boolean readOnly)
/*     */     throws SQLException
/*     */   {
/* 233 */     checkClosed();
/*     */     try
/*     */     {
/* 236 */       this.mc.setReadOnly(readOnly);
/*     */     } catch (SQLException sqlException) {
/* 238 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */     throws SQLException
/*     */   {
/* 249 */     checkClosed();
/*     */     try
/*     */     {
/* 252 */       return this.mc.isReadOnly();
/*     */     } catch (SQLException sqlException) {
/* 254 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 257 */     return false;
/*     */   }
/*     */ 
/*     */   public Savepoint setSavepoint()
/*     */     throws SQLException
/*     */   {
/* 264 */     checkClosed();
/*     */     try
/*     */     {
/* 267 */       return this.mc.setSavepoint();
/*     */     } catch (SQLException sqlException) {
/* 269 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 272 */     return null;
/*     */   }
/*     */ 
/*     */   public Savepoint setSavepoint(String arg0)
/*     */     throws SQLException
/*     */   {
/* 279 */     checkClosed();
/*     */     try
/*     */     {
/* 282 */       return this.mc.setSavepoint(arg0);
/*     */     } catch (SQLException sqlException) {
/* 284 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 287 */     return null;
/*     */   }
/*     */ 
/*     */   public void setTransactionIsolation(int level)
/*     */     throws SQLException
/*     */   {
/* 297 */     checkClosed();
/*     */     try
/*     */     {
/* 300 */       this.mc.setTransactionIsolation(level);
/*     */     } catch (SQLException sqlException) {
/* 302 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getTransactionIsolation()
/*     */     throws SQLException
/*     */   {
/* 313 */     checkClosed();
/*     */     try
/*     */     {
/* 316 */       return this.mc.getTransactionIsolation();
/*     */     } catch (SQLException sqlException) {
/* 318 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 321 */     return 4;
/*     */   }
/*     */ 
/*     */   public void setTypeMap(Map map)
/*     */     throws SQLException
/*     */   {
/* 332 */     checkClosed();
/*     */     try
/*     */     {
/* 335 */       this.mc.setTypeMap(map);
/*     */     } catch (SQLException sqlException) {
/* 337 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Map getTypeMap()
/*     */     throws SQLException
/*     */   {
/* 348 */     checkClosed();
/*     */     try
/*     */     {
/* 351 */       return this.mc.getTypeMap();
/*     */     } catch (SQLException sqlException) {
/* 353 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 356 */     return null;
/*     */   }
/*     */ 
/*     */   public SQLWarning getWarnings()
/*     */     throws SQLException
/*     */   {
/* 366 */     checkClosed();
/*     */     try
/*     */     {
/* 369 */       return this.mc.getWarnings();
/*     */     } catch (SQLException sqlException) {
/* 371 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 374 */     return null;
/*     */   }
/*     */ 
/*     */   public void clearWarnings()
/*     */     throws SQLException
/*     */   {
/* 385 */     checkClosed();
/*     */     try
/*     */     {
/* 388 */       this.mc.clearWarnings();
/*     */     } catch (SQLException sqlException) {
/* 390 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 405 */     close(true);
/*     */   }
/*     */ 
/*     */   public void commit()
/*     */     throws SQLException
/*     */   {
/* 416 */     checkClosed();
/*     */     try
/*     */     {
/* 419 */       this.mc.commit();
/*     */     } catch (SQLException sqlException) {
/* 421 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Statement createStatement()
/*     */     throws SQLException
/*     */   {
/* 432 */     checkClosed();
/*     */     try
/*     */     {
/* 435 */       return new StatementWrapper(this, this.mpc, this.mc.createStatement());
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 438 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 441 */     return null;
/*     */   }
/*     */ 
/*     */   public Statement createStatement(int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 452 */     checkClosed();
/*     */     try
/*     */     {
/* 455 */       return new StatementWrapper(this, this.mpc, this.mc.createStatement(resultSetType, resultSetConcurrency));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 458 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 461 */     return null;
/*     */   }
/*     */ 
/*     */   public Statement createStatement(int arg0, int arg1, int arg2)
/*     */     throws SQLException
/*     */   {
/* 469 */     checkClosed();
/*     */     try
/*     */     {
/* 472 */       return new StatementWrapper(this, this.mpc, this.mc.createStatement(arg0, arg1, arg2));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 475 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 478 */     return null;
/*     */   }
/*     */ 
/*     */   public String nativeSQL(String sql)
/*     */     throws SQLException
/*     */   {
/* 488 */     checkClosed();
/*     */     try
/*     */     {
/* 491 */       return this.mc.nativeSQL(sql);
/*     */     } catch (SQLException sqlException) {
/* 493 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 496 */     return null;
/*     */   }
/*     */ 
/*     */   public CallableStatement prepareCall(String sql)
/*     */     throws SQLException
/*     */   {
/* 507 */     checkClosed();
/*     */     try
/*     */     {
/* 510 */       return new CallableStatementWrapper(this, this.mpc, this.mc.prepareCall(sql));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 513 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 516 */     return null;
/*     */   }
/*     */ 
/*     */   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 527 */     checkClosed();
/*     */     try
/*     */     {
/* 530 */       return new CallableStatementWrapper(this, this.mpc, this.mc.prepareCall(sql, resultSetType, resultSetConcurrency));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 533 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 536 */     return null;
/*     */   }
/*     */ 
/*     */   public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3)
/*     */     throws SQLException
/*     */   {
/* 544 */     checkClosed();
/*     */     try
/*     */     {
/* 547 */       return new CallableStatementWrapper(this, this.mpc, this.mc.prepareCall(arg0, arg1, arg2, arg3));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 550 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 553 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement clientPrepare(String sql) throws SQLException
/*     */   {
/* 558 */     checkClosed();
/*     */     try
/*     */     {
/* 561 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.clientPrepareStatement(sql));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 564 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 567 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement clientPrepare(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 573 */     checkClosed();
/*     */     try
/*     */     {
/* 576 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.clientPrepareStatement(sql, resultSetType, resultSetConcurrency));
/*     */     }
/*     */     catch (SQLException sqlException)
/*     */     {
/* 580 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 583 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql)
/*     */     throws SQLException
/*     */   {
/* 594 */     checkClosed();
/*     */     try
/*     */     {
/* 597 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(sql));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 600 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 603 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*     */     throws SQLException
/*     */   {
/* 614 */     checkClosed();
/*     */     try
/*     */     {
/* 617 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(sql, resultSetType, resultSetConcurrency));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 620 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 623 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3)
/*     */     throws SQLException
/*     */   {
/* 631 */     checkClosed();
/*     */     try
/*     */     {
/* 634 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(arg0, arg1, arg2, arg3));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 637 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 640 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String arg0, int arg1)
/*     */     throws SQLException
/*     */   {
/* 648 */     checkClosed();
/*     */     try
/*     */     {
/* 651 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 654 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 657 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String arg0, int[] arg1)
/*     */     throws SQLException
/*     */   {
/* 665 */     checkClosed();
/*     */     try
/*     */     {
/* 668 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 671 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 674 */     return null;
/*     */   }
/*     */ 
/*     */   public PreparedStatement prepareStatement(String arg0, String[] arg1)
/*     */     throws SQLException
/*     */   {
/* 682 */     checkClosed();
/*     */     try
/*     */     {
/* 685 */       return new PreparedStatementWrapper(this, this.mpc, this.mc.prepareStatement(arg0, arg1));
/*     */     }
/*     */     catch (SQLException sqlException) {
/* 688 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */ 
/* 691 */     return null;
/*     */   }
/*     */ 
/*     */   public void releaseSavepoint(Savepoint arg0)
/*     */     throws SQLException
/*     */   {
/* 698 */     checkClosed();
/*     */     try
/*     */     {
/* 701 */       this.mc.releaseSavepoint(arg0);
/*     */     } catch (SQLException sqlException) {
/* 703 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rollback()
/*     */     throws SQLException
/*     */   {
/* 714 */     checkClosed();
/*     */     try
/*     */     {
/* 717 */       this.mc.rollback();
/*     */     } catch (SQLException sqlException) {
/* 719 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void rollback(Savepoint arg0)
/*     */     throws SQLException
/*     */   {
/* 727 */     checkClosed();
/*     */     try
/*     */     {
/* 730 */       this.mc.rollback(arg0);
/*     */     } catch (SQLException sqlException) {
/* 732 */       checkAndFireConnectionError(sqlException);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void close(boolean fireClosedEvent) throws SQLException {
/* 737 */     synchronized (this.mpc) {
/* 738 */       if (this.closed) {
/* 739 */         return;
/*     */       }
/*     */ 
/* 742 */       if ((this.mc.getRollbackOnPooledClose()) && (!getAutoCommit()))
/*     */       {
/* 744 */         rollback();
/*     */       }
/*     */ 
/* 747 */       if (fireClosedEvent) {
/* 748 */         this.mpc.callListener(2, null);
/*     */       }
/*     */ 
/* 757 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkClosed() throws SQLException {
/* 762 */     if (this.closed)
/* 763 */       throw new SQLException(this.invalidHandleStr);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.ConnectionWrapper
 * JD-Core Version:    0.6.0
 */
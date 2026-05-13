/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import javax.sql.ConnectionPoolDataSource;
/*    */ import javax.sql.PooledConnection;
/*    */ 
/*    */ public class MysqlConnectionPoolDataSource extends MysqlDataSource
/*    */   implements ConnectionPoolDataSource
/*    */ {
/*    */   public synchronized PooledConnection getPooledConnection()
/*    */     throws SQLException
/*    */   {
/* 58 */     java.sql.Connection connection = getConnection();
/* 59 */     MysqlPooledConnection mysqlPooledConnection = new MysqlPooledConnection((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 62 */     return mysqlPooledConnection;
/*    */   }
/*    */ 
/*    */   public synchronized PooledConnection getPooledConnection(String s, String s1)
/*    */     throws SQLException
/*    */   {
/* 79 */     java.sql.Connection connection = getConnection(s, s1);
/* 80 */     MysqlPooledConnection mysqlPooledConnection = new MysqlPooledConnection((com.mysql.jdbc.Connection)connection);
/*    */ 
/* 83 */     return mysqlPooledConnection;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource
 * JD-Core Version:    0.6.0
 */
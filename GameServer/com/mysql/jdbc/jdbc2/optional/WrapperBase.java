/*    */ package com.mysql.jdbc.jdbc2.optional;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ abstract class WrapperBase
/*    */ {
/*    */   protected MysqlPooledConnection pooledConnection;
/*    */ 
/*    */   protected void checkAndFireConnectionError(SQLException sqlEx)
/*    */     throws SQLException
/*    */   {
/* 51 */     if ((this.pooledConnection != null) && 
/* 52 */       ("08S01".equals(sqlEx.getSQLState())))
/*    */     {
/* 54 */       this.pooledConnection.callListener(1, sqlEx);
/*    */     }
/*    */ 
/* 59 */     throw sqlEx;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.jdbc2.optional.WrapperBase
 * JD-Core Version:    0.6.0
 */
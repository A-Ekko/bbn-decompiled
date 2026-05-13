/*    */ package com.pst.db;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class DBConnection
/*    */ {
/*    */   public boolean excuteUpdate(String sql)
/*    */     throws SQLException
/*    */   {
/* 16 */     Connection conn = ConnectionPoolManager.getConnectionPool()
/* 17 */       .getConnection();
/* 18 */     Statement stmt = null;
/*    */ 
/* 20 */     stmt = conn.createStatement();
/* 21 */     int exs = stmt.executeUpdate(sql);
/*    */ 
/* 26 */     stmt.close();
/*    */ 
/* 31 */     conn.close();
/*    */ 
/* 36 */     return exs > 0;
/*    */   }
/*    */ 
/*    */   public boolean excuteCreate(String sql)
/*    */     throws SQLException
/*    */   {
/* 51 */     Connection conn = ConnectionPoolManager.getConnectionPool()
/* 52 */       .getConnection();
/* 53 */     Statement stmt = null;
/*    */ 
/* 55 */     stmt = conn.createStatement();
/*    */ 
/* 57 */     boolean boo = stmt.execute(sql);
/*    */ 
/* 63 */     stmt.close();
/*    */ 
/* 68 */     conn.close();
/*    */ 
/* 71 */     return boo;
/*    */   }
/*    */ 
/*    */   public ResultSet excuteQuery(String sql)
/*    */     throws SQLException
/*    */   {
/* 81 */     Connection conn = ConnectionPoolManager.getConnectionPool()
/* 82 */       .getConnection();
/* 83 */     Statement stmt = conn.createStatement();
/*    */ 
/* 85 */     ResultSet rs = stmt.executeQuery(sql);
/*    */ 
/* 88 */     return rs;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.DBConnection
 * JD-Core Version:    0.6.0
 */
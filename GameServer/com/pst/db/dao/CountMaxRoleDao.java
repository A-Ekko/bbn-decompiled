/*    */ package com.pst.db.dao;
/*    */ 
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class CountMaxRoleDao
/*    */ {
/*    */   public int getMaxRoleSize()
/*    */   {
/* 12 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 13 */     Statement stmt = null;
/* 14 */     ResultSet rs = null;
/* 15 */     int number = 0;
/*    */     try {
/* 17 */       stmt = con.createStatement();
/* 18 */       rs = stmt.executeQuery("select count(id) from role");
/* 19 */       if (rs.next())
/* 20 */         number = rs.getInt(1);
/*    */     }
/*    */     catch (SQLException e) {
/* 23 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 26 */         if (rs != null) rs.close();
/* 27 */         if (stmt != null) stmt.close();
/* 28 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (Exception e) {
/* 30 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 26 */         if (rs != null) rs.close();
/* 27 */         if (stmt != null) stmt.close();
/* 28 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (Exception e) {
/* 30 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 33 */     return number;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.CountMaxRoleDao
 * JD-Core Version:    0.6.0
 */
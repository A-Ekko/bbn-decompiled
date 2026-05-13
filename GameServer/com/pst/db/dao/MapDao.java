/*    */ package com.pst.db.dao;
/*    */ 
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MapDao
/*    */ {
/*    */   public List<Integer> getMaps()
/*    */   {
/* 15 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 16 */     Statement stmt = null;
/* 17 */     ResultSet rs = null;
/* 18 */     String sql = "SELECT m.id FROM map AS m";
/* 19 */     List maps = new ArrayList();
/*    */     try {
/* 21 */       stmt = con.createStatement();
/* 22 */       rs = stmt.executeQuery(sql);
/* 23 */       while (rs.next())
/* 24 */         maps.add(Integer.valueOf(rs.getInt("m.id")));
/*    */     }
/*    */     catch (SQLException e) {
/* 27 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 30 */         if (rs != null) rs.close();
/* 31 */         if (stmt != null) stmt.close();
/* 32 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (SQLException e) {
/* 34 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 30 */         if (rs != null) rs.close();
/* 31 */         if (stmt != null) stmt.close();
/* 32 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (SQLException e) {
/* 34 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 37 */     return maps;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.MapDao
 * JD-Core Version:    0.6.0
 */
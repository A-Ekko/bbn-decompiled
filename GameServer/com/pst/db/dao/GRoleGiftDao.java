/*    */ package com.pst.db.dao;
/*    */ 
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class GRoleGiftDao
/*    */ {
/*    */   public void clearRoleGiftData()
/*    */   {
/* 13 */     Connection conn = ConnectionPoolManager.getConnectionPool().getConnection();
/* 14 */     Statement stmt = null;
/*    */     try
/*    */     {
/* 17 */       stmt = conn.createStatement();
/* 18 */       String sql = "delete from role_gift_step";
/* 19 */       stmt.executeUpdate(sql);
/*    */     }
/*    */     catch (SQLException e)
/*    */     {
/* 23 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 27 */         if (stmt != null) stmt.close();
/* 28 */         if (conn != null) conn.close(); 
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 31 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 27 */         if (stmt != null) stmt.close();
/* 28 */         if (conn != null) conn.close(); 
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 31 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.GRoleGiftDao
 * JD-Core Version:    0.6.0
 */
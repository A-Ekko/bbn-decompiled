/*    */ package com.pst.db.dao;
/*    */ 
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ 
/*    */ public class ServerDao
/*    */ {
/*    */   public String getServerName()
/*    */   {
/* 13 */     String name = null;
/* 14 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 15 */     Statement stmt = null;
/* 16 */     ResultSet rs = null;
/* 17 */     String sql = "select c.value from config as c where c.key='server_name'";
/*    */     try {
/* 19 */       stmt = con.createStatement();
/* 20 */       rs = stmt.executeQuery(sql);
/* 21 */       while (rs.next())
/* 22 */         name = rs.getString("c.value");
/*    */     }
/*    */     catch (SQLException e) {
/* 25 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 28 */         if (rs != null)
/* 29 */           rs.close();
/* 30 */         if (stmt != null)
/* 31 */           stmt.close();
/* 32 */         if (con != null)
/* 33 */           con.close();
/*    */       } catch (SQLException e) {
/* 35 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 28 */         if (rs != null)
/* 29 */           rs.close();
/* 30 */         if (stmt != null)
/* 31 */           stmt.close();
/* 32 */         if (con != null)
/* 33 */           con.close();
/*    */       } catch (SQLException e) {
/* 35 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 38 */     return name;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.ServerDao
 * JD-Core Version:    0.6.0
 */
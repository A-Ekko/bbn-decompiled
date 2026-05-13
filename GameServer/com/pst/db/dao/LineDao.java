/*    */ package com.pst.db.dao;
/*    */ 
/*    */ import com.pst.core.line.entity.Line;
/*    */ import com.pst.core.util.IpUtil;
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LineDao
/*    */ {
/*    */   public List<Line> getLines()
/*    */   {
/* 16 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 17 */     Statement stmt = null;
/* 18 */     ResultSet rs = null;
/* 19 */     String sql = "select line_id,line_name,ip,port,status from config_lines order by line_id";
/* 20 */     List lines = new ArrayList();
/*    */     try
/*    */     {
/* 23 */       stmt = con.createStatement();
/* 24 */       rs = stmt.executeQuery(sql);
/* 25 */       IpUtil ipUtil = new IpUtil();
/* 26 */       while (rs.next()) {
/* 27 */         Line line = new Line();
/* 28 */         line.setId(rs.getInt("line_id"));
/* 29 */         line.setPort(rs.getInt("port"));
/* 30 */         line.setIp(ipUtil.longToIP(rs.getLong("ip")));
/* 31 */         line.setName(rs.getString("line_name"));
/* 32 */         line.setStatus(0);
/* 33 */         lines.add(line);
/*    */       }
/*    */     } catch (SQLException e) {
/* 36 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 39 */         if (rs != null)
/* 40 */           rs.close();
/* 41 */         if (stmt != null)
/* 42 */           stmt.close();
/* 43 */         if (con != null)
/* 44 */           con.close();
/*    */       } catch (SQLException e) {
/* 46 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 39 */         if (rs != null)
/* 40 */           rs.close();
/* 41 */         if (stmt != null)
/* 42 */           stmt.close();
/* 43 */         if (con != null)
/* 44 */           con.close();
/*    */       } catch (SQLException e) {
/* 46 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 49 */     return lines;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.LineDao
 * JD-Core Version:    0.6.0
 */
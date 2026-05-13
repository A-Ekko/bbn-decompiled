/*    */ package com.pst.task.timerTask;
/*    */ 
/*    */ import com.pst.core.line.store.SystemStore;
/*    */ import com.pst.db.ConnectionPool;
/*    */ import com.pst.db.ConnectionPoolManager;
/*    */ import java.sql.Connection;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TimerTask;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class PlayerOnlineCount extends TimerTask
/*    */ {
/* 16 */   private Logger logger = Logger.getLogger(PlayerOnlineCount.class);
/*    */ 
/*    */   public void run() {
/* 19 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 20 */     Statement stmt = null;
/*    */     try {
/* 22 */       stmt = con.createStatement();
/* 23 */       Iterator it = SystemStore.linemaps.keySet().iterator();
/* 24 */       long currTime = System.currentTimeMillis() / 1000L;
/* 25 */       while (it.hasNext())
/*    */       {
/* 27 */         String sId = (String)it.next();
/* 28 */         List scenePlayerNum = (List)SystemStore.linemaps.get(sId);
/* 29 */         if ((scenePlayerNum == null) || (scenePlayerNum.size() <= 0))
/*    */           continue;
/* 31 */         String[] lineStr = sId.split("-");
/* 32 */         int line = Integer.parseInt(lineStr[0]);
/* 33 */         int mapId = Integer.parseInt(lineStr[1]);
/* 34 */         String mapSql = "insert into statistics_map_role_num(save_time,map_id,line,role_number) values (" + currTime + "," + mapId + "," + line + "," + scenePlayerNum.size() + ")";
/* 35 */         stmt.addBatch(mapSql);
/*    */       }
/*    */ 
/* 38 */       stmt.executeBatch();
/*    */     } catch (SQLException e) {
/* 40 */       e.printStackTrace();
/*    */       try
/*    */       {
/* 43 */         if (stmt != null) stmt.close();
/* 44 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (Exception e) {
/* 46 */         e.printStackTrace();
/*    */       }
/*    */     }
/*    */     finally
/*    */     {
/*    */       try
/*    */       {
/* 43 */         if (stmt != null) stmt.close();
/* 44 */         if (con != null) con.close(); 
/*    */       }
/*    */       catch (Exception e) {
/* 46 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 49 */     this.logger.info("统计一个地图内玩家的数量");
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.task.timerTask.PlayerOnlineCount
 * JD-Core Version:    0.6.0
 */
/*     */ package com.pst.db.dao;
/*     */ 
/*     */ import com.pst.core.config.SystemConfig;
/*     */ import com.pst.db.ConnectionPool;
/*     */ import com.pst.db.ConnectionPoolManager;
/*     */ import java.io.PrintStream;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class BillDao
/*     */ {
/*  13 */   private CountMaxRoleDao countMaxRoleDao = new CountMaxRoleDao();
/*     */ 
/*     */   public void bill(int size)
/*     */   {
/*  22 */     if (SystemConfig.totalRole < size) {
/*  23 */       int number = this.countMaxRoleDao.getMaxRoleSize();
/*  24 */       size = number > size ? size : number;
/*  25 */       SystemConfig.totalRole = number;
/*  26 */       System.out.println("进行人物排行时查询的人物数为[" + SystemConfig.totalRole + "]");
/*     */     }
/*  28 */     String selectSql = "SELECT r.id,r.name,r.level,r.sex,r.popsinger_id,r.camp_id FROM role AS r ORDER BY r.level DESC,r.EXP DESC LIMIT 0," + size;
/*     */ 
/*  37 */     long time = System.currentTimeMillis();
/*  38 */     int index = 1;
/*     */ 
/*  46 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/*  47 */     Statement stmt = null;
/*  48 */     ResultSet rs = null;
/*  49 */     String insertSql = null;
/*     */     try {
/*  51 */       stmt = con.createStatement();
/*  52 */       rs = stmt.executeQuery(selectSql);
/*  53 */       while (rs.next()) {
/*  54 */         int roleId = rs.getInt("r.id");
/*  55 */         String name = rs.getString("r.name");
/*  56 */         int level = rs.getInt("r.level");
/*  57 */         int sex = rs.getInt("r.sex");
/*  58 */         int popsingerId = rs.getInt("r.popsinger_id");
/*  59 */         int campId = rs.getInt("r.camp_id");
/*  60 */         if (insertSql == null)
/*  61 */           insertSql = "INSERT INTO bill_role(bill_index,bill_time,role_id,role_name,role_level,role_sex,role_popsinger_id,camp_id)VALUES(" + 
/*  62 */             index++ + "," + time + "," + roleId + ",'" + name + "'," + level + "," + sex + "," + popsingerId + "," + campId + ")";
/*     */         else {
/*  64 */           insertSql = insertSql + ",(" + index++ + "," + time + "," + roleId + ",'" + name + "'," + level + "," + sex + "," + popsingerId + "," + campId + ")";
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  69 */       if (insertSql != null)
/*  70 */         stmt.executeUpdate(insertSql);
/*     */     }
/*     */     catch (SQLException e)
/*     */     {
/*  74 */       e.printStackTrace();
/*     */       try
/*     */       {
/*  77 */         if (rs != null) rs.close();
/*  78 */         if (stmt != null) stmt.close();
/*  79 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (SQLException e) {
/*  81 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/*  77 */         if (rs != null) rs.close();
/*  78 */         if (stmt != null) stmt.close();
/*  79 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (SQLException e) {
/*  81 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void wealth(int size)
/*     */   {
/*  94 */     if (SystemConfig.totalRole < size) {
/*  95 */       int number = this.countMaxRoleDao.getMaxRoleSize();
/*  96 */       size = number > size ? size : number;
/*  97 */       SystemConfig.totalRole = number;
/*  98 */       System.out.println("进行财富排行时查询的人物数为[" + SystemConfig.totalRole + "]");
/*     */     }
/* 100 */     String selectSql = "select r.id,r.name,r.popsinger_id,r.camp_id,r.money,r.level from role as r order by r.money desc limit 0," + size;
/*     */ 
/* 109 */     long systime = System.currentTimeMillis();
/* 110 */     int index = 1;
/*     */ 
/* 112 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 113 */     Statement stmt = null;
/* 114 */     ResultSet rs = null;
/* 115 */     String insertSql = null;
/*     */     try {
/* 117 */       stmt = con.createStatement();
/* 118 */       rs = stmt.executeQuery(selectSql);
/* 119 */       while (rs.next()) {
/* 120 */         int roleId = rs.getInt("r.id");
/* 121 */         String roleName = rs.getString("r.name");
/* 122 */         int pop = rs.getInt("r.popsinger_id");
/* 123 */         int camp = rs.getInt("r.camp_id");
/* 124 */         int level = rs.getInt("r.level");
/* 125 */         long money = rs.getLong("r.money");
/* 126 */         if (insertSql == null)
/* 127 */           insertSql = "insert into bill_wealth(bill_index,bill_time,role_id,role_name,popsinger_id,camp_id,coin,role_level)values(" + 
/* 128 */             index++ + "," + systime + "," + roleId + ",'" + roleName + "'," + pop + "," + camp + "," + money + "," + level + ")";
/*     */         else {
/* 130 */           insertSql = insertSql + ",(" + index++ + "," + systime + "," + roleId + ",'" + roleName + "'," + pop + "," + camp + "," + money + "," + level + ")";
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 136 */       if (insertSql != null)
/* 137 */         stmt.executeUpdate(insertSql);
/*     */     }
/*     */     catch (SQLException e) {
/* 140 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 143 */         if (rs != null) rs.close();
/* 144 */         if (stmt != null) stmt.close();
/* 145 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (Exception e) {
/* 147 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 143 */         if (rs != null) rs.close();
/* 144 */         if (stmt != null) stmt.close();
/* 145 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (Exception e) {
/* 147 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void arena(int size)
/*     */   {
/* 160 */     if (SystemConfig.totalRole < size) {
/* 161 */       int number = this.countMaxRoleDao.getMaxRoleSize();
/* 162 */       size = number > size ? size : number;
/* 163 */       SystemConfig.totalRole = number;
/* 164 */       System.out.println("进行竞技排行时查询的人物数为[" + SystemConfig.totalRole + "]");
/*     */     }
/* 166 */     String selectSql = "select r.id,r.name,r.level,r.popsinger_id,r.arena_integral,r.arena_round,r.arena_match_won,r.camp_id from role as r where r.arena_integral>0 order by r.arena_integral desc limit 0," + size;
/*     */ 
/* 171 */     long systime = System.currentTimeMillis();
/* 172 */     int index = 1;
/*     */ 
/* 174 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 175 */     Statement stmt = null;
/* 176 */     ResultSet rs = null;
/* 177 */     String insertSql = null;
/*     */     try {
/* 179 */       stmt = con.createStatement();
/* 180 */       rs = stmt.executeQuery(selectSql);
/* 181 */       while (rs.next()) {
/* 182 */         int roleId = rs.getInt("r.id");
/* 183 */         String roleName = rs.getString("r.name");
/* 184 */         int pop = rs.getInt("r.popsinger_id");
/* 185 */         int winCount = rs.getInt("r.arena_match_won");
/* 186 */         int level = rs.getInt("r.level");
/* 187 */         int integral = rs.getInt("r.arena_integral");
/* 188 */         int totalCount = rs.getInt("r.arena_round");
/* 189 */         int camp = rs.getInt("r.camp_id");
/* 190 */         int lostCount = totalCount - winCount;
/* 191 */         int percent = countPercent(winCount, totalCount);
/* 192 */         if (insertSql == null)
/* 193 */           insertSql = "insert into bill_arena(bill_index,bill_time,role_id,role_name,popsinger_id,win_count,integral,role_level,lost_count,percent,camp_id)values(" + 
/* 194 */             index++ + "," + systime + "," + roleId + ",'" + roleName + "'," + pop + "," + winCount + "," + integral + "," + level + "," + lostCount + "," + percent + "," + camp + ")";
/*     */         else {
/* 196 */           insertSql = insertSql + ",(" + index++ + "," + systime + "," + roleId + ",'" + roleName + "'," + pop + "," + winCount + "," + integral + "," + level + "," + lostCount + "," + percent + "," + camp + ")";
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 202 */       if (insertSql != null)
/* 203 */         stmt.executeUpdate(insertSql);
/*     */     }
/*     */     catch (SQLException e) {
/* 206 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 209 */         if (rs != null) rs.close();
/* 210 */         if (stmt != null) stmt.close();
/* 211 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (Exception e) {
/* 213 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 209 */         if (rs != null) rs.close();
/* 210 */         if (stmt != null) stmt.close();
/* 211 */         if (con != null) con.close(); 
/*     */       }
/*     */       catch (Exception e) {
/* 213 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void burst(int size)
/*     */   {
/* 225 */     if (SystemConfig.totalRole < size)
/*     */     {
/* 227 */       int number = this.countMaxRoleDao.getMaxRoleSize();
/* 228 */       size = number > size ? size : number;
/* 229 */       SystemConfig.totalRole = number;
/* 230 */       System.out.println("进行闯关排行时查询的人物数为[" + SystemConfig.totalRole + "]");
/*     */     }
/*     */ 
/* 233 */     String selectSql = "select t3.role_id, t3.burst_name, t3.burst_level,t4.name as role_name,t4.level as role_level ,t4.camp_id as role_camp ,t4.popsinger_id as role_popr  from (select t1.role_id, t2.name as burst_name, t2.suggest_level as burst_level,t2.suggest_level from `role_burst` as t1 left join `map_burst_base` as t2 on t2.id = t1.burst_id where t1.success_times>0 order by t2.suggest_level desc,t2.level_dif desc) as t3 left join role as t4 on t4.id = t3.role_id group by t3.role_id order by t3.suggest_level desc limit 0," + size;
/*     */ 
/* 238 */     int index = 1;
/*     */ 
/* 240 */     Connection con = ConnectionPoolManager.getConnectionPool().getConnection();
/* 241 */     Statement stmt = null;
/* 242 */     ResultSet rs = null;
/* 243 */     String insertSql = null;
/*     */ 
/* 245 */     String deleteSql = null;
/*     */     try
/*     */     {
/* 249 */       stmt = con.createStatement();
/*     */ 
/* 251 */       deleteSql = "DELETE FROM bill_burst";
/* 252 */       stmt.execute(deleteSql);
/*     */ 
/* 254 */       rs = stmt.executeQuery(selectSql);
/*     */ 
/* 256 */       ArrayList sqlArry = new ArrayList();
/*     */ 
/* 258 */       while (rs.next())
/*     */       {
/* 260 */         int role_id = rs.getInt("role_id");
/* 261 */         int burst_level = rs.getInt("burst_level");
/* 262 */         int role_level = rs.getInt("role_level");
/* 263 */         String burst_name = rs.getString("burst_name");
/* 264 */         String role_name = rs.getString("role_name");
/* 265 */         int role_camp = rs.getInt("role_camp");
/* 266 */         int role_popr = rs.getInt("role_popr");
/* 267 */         insertSql = "insert into bill_burst(role_id,burst_name,burst_level,role_name,role_level,role_camp,role_popr) values(" + role_id + ",'" + burst_name + "'," + burst_level + ",'" + role_name + "'," + role_level + "," + role_camp + "," + role_popr + ")";
/*     */ 
/* 269 */         sqlArry.add(insertSql);
/*     */       }
/*     */ 
/* 273 */       stmt = con.createStatement();
/*     */ 
/* 275 */       for (int i = 0; i < sqlArry.size(); i++)
/*     */       {
/* 277 */         stmt.execute((String)sqlArry.get(i));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 283 */       e.printStackTrace();
/*     */       try
/*     */       {
/* 289 */         if (rs != null) rs.close();
/* 290 */         if (stmt != null) stmt.close();
/* 291 */         if (con != null) con.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 295 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 289 */         if (rs != null) rs.close();
/* 290 */         if (stmt != null) stmt.close();
/* 291 */         if (con != null) con.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 295 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int countPercent(int winCount, int totalCount)
/*     */   {
/* 307 */     float percent = winCount / totalCount * 100.0F;
/* 308 */     return (int)percent;
/*     */   }
/*     */ 
/*     */   public static void main(String[] ags)
/*     */   {
/* 313 */     ConnectionPoolManager.initConnectionPool();
/* 314 */     BillDao dao = new BillDao();
/* 315 */     dao.burst(100);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.dao.BillDao
 * JD-Core Version:    0.6.0
 */
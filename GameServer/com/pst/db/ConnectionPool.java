/*    */ package com.pst.db;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import org.logicalcobwebs.proxool.ProxoolException;
/*    */ import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
/*    */ 
/*    */ public class ConnectionPool
/*    */ {
/* 12 */   private String connectionName = "proxool.mysql";
/*    */ 
/*    */   public ConnectionPool(String configFile, String connectionName)
/*    */   {
/* 20 */     this.connectionName = connectionName;
/*    */     try {
/* 22 */       PropertyConfigurator.configure(configFile);
/*    */     } catch (ProxoolException pe) {
/* 24 */       pe.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized Connection getConnection()
/*    */   {
/* 34 */     Connection conn = null;
/*    */     try {
/* 36 */       conn = DriverManager.getConnection(this.connectionName);
/*    */     } catch (SQLException se) {
/* 38 */       se.printStackTrace();
/* 39 */       return null;
/*    */     }
/*    */ 
/* 42 */     return conn;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.pst.db.ConnectionPool
 * JD-Core Version:    0.6.0
 */
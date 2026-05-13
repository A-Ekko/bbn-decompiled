/*    */ package com.pst.db;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import org.logicalcobwebs.proxool.ProxoolException;
/*    */ import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;
/*    */ 
/*    */ public class ConnectionPool
/*    */ {
/* 11 */   private String connectionName = "proxool.mysql";
/*    */ 
/*    */   public ConnectionPool(String configFile, String connectionName)
/*    */   {
/* 19 */     this.connectionName = connectionName;
/*    */     try {
/* 21 */       PropertyConfigurator.configure(configFile);
/*    */     } catch (ProxoolException pe) {
/* 23 */       pe.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized Connection getConnection()
/*    */     throws Exception
/*    */   {
/* 33 */     return DriverManager.getConnection(this.connectionName);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.db.ConnectionPool
 * JD-Core Version:    0.6.0
 */
/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.Driver;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class ReplicationDriver extends NonRegisteringReplicationDriver
/*    */   implements Driver
/*    */ {
/*    */   public ReplicationDriver()
/*    */     throws SQLException
/*    */   {
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 64 */       DriverManager.registerDriver(new NonRegisteringReplicationDriver());
/*    */     }
/*    */     catch (SQLException E) {
/* 67 */       throw new RuntimeException("Can't register driver!");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.ReplicationDriver
 * JD-Core Version:    0.6.0
 */
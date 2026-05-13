/*    */ package com.pst.db;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class ConnectionPoolManager
/*    */ {
/*    */   private static ConnectionPool connPool;
/*    */ 
/*    */   public static void initConnectionPool()
/*    */   {
/* 10 */     if (connPool == null)
/* 11 */       connPool = new ConnectionPool(System.getProperties().getProperty("user.dir") + "/resource/proxool.properties", "proxool.mysql");
/*    */   }
/*    */ 
/*    */   public static void initConnectionPool(String configFile, String connectionName)
/*    */   {
/* 21 */     if (connPool == null)
/* 22 */       connPool = new ConnectionPool(configFile, connectionName);
/*    */   }
/*    */ 
/*    */   public static ConnectionPool getConnectionPool()
/*    */   {
/* 27 */     return connPool;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.pst.db.ConnectionPoolManager
 * JD-Core Version:    0.6.0
 */
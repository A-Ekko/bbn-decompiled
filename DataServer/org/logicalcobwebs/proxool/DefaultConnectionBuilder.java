/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.DriverManager;
/*    */ import java.sql.SQLException;
/*    */ import java.util.Properties;
/*    */ 
/*    */ public class DefaultConnectionBuilder
/*    */   implements ConnectionBuilderIF
/*    */ {
/*    */   public Connection buildConnection(ConnectionPoolDefinitionIF cpd)
/*    */     throws SQLException
/*    */   {
/* 35 */     Connection realConnection = null;
/* 36 */     String url = cpd.getUrl();
/*    */ 
/* 38 */     Properties info = cpd.getDelegateProperties();
/* 39 */     return DriverManager.getConnection(url, info);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.DefaultConnectionBuilder
 * JD-Core Version:    0.6.0
 */
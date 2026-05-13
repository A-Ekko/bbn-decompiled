/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.ParameterMetaData;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class MysqlParameterMetadata
/*    */   implements ParameterMetaData
/*    */ {
/* 31 */   ResultSetMetaData metadata = null;
/* 32 */   int parameterCount = 0;
/*    */ 
/*    */   MysqlParameterMetadata(Field[] fieldInfo, int parameterCount)
/*    */   {
/* 36 */     this.metadata = new ResultSetMetaData(fieldInfo);
/*    */ 
/* 38 */     this.parameterCount = parameterCount;
/*    */   }
/*    */ 
/*    */   public int getParameterCount() throws SQLException {
/* 42 */     return this.parameterCount;
/*    */   }
/*    */ 
/*    */   public int isNullable(int arg0) throws SQLException {
/* 46 */     checkAvailable();
/*    */ 
/* 48 */     return this.metadata.isNullable(arg0);
/*    */   }
/*    */ 
/*    */   private void checkAvailable() throws SQLException {
/* 52 */     if (this.metadata == null)
/* 53 */       throw new SQLException("Parameter metadata not available for the given statement", "S1C00");
/*    */   }
/*    */ 
/*    */   public boolean isSigned(int arg0)
/*    */     throws SQLException
/*    */   {
/* 60 */     checkAvailable();
/*    */ 
/* 62 */     return this.metadata.isSigned(arg0);
/*    */   }
/*    */ 
/*    */   public int getPrecision(int arg0) throws SQLException {
/* 66 */     checkAvailable();
/*    */ 
/* 68 */     return this.metadata.getPrecision(arg0);
/*    */   }
/*    */ 
/*    */   public int getScale(int arg0) throws SQLException {
/* 72 */     checkAvailable();
/*    */ 
/* 74 */     return this.metadata.getScale(arg0);
/*    */   }
/*    */ 
/*    */   public int getParameterType(int arg0) throws SQLException {
/* 78 */     checkAvailable();
/*    */ 
/* 80 */     return this.metadata.getColumnType(arg0);
/*    */   }
/*    */ 
/*    */   public String getParameterTypeName(int arg0) throws SQLException {
/* 84 */     checkAvailable();
/*    */ 
/* 86 */     return this.metadata.getColumnTypeName(arg0);
/*    */   }
/*    */ 
/*    */   public String getParameterClassName(int arg0) throws SQLException {
/* 90 */     checkAvailable();
/*    */ 
/* 92 */     return this.metadata.getColumnClassName(arg0);
/*    */   }
/*    */ 
/*    */   public int getParameterMode(int arg0) throws SQLException {
/* 96 */     return 1;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.MysqlParameterMetadata
 * JD-Core Version:    0.6.0
 */
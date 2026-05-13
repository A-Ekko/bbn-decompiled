/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.rmi.server.UID;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Savepoint;
/*     */ 
/*     */ public class MysqlSavepoint
/*     */   implements Savepoint
/*     */ {
/*     */   private String savepointName;
/*     */ 
/*     */   private static String getUniqueId()
/*     */   {
/*  41 */     String uidStr = new UID().toString();
/*     */ 
/*  43 */     int uidLength = uidStr.length();
/*     */ 
/*  45 */     StringBuffer safeString = new StringBuffer(uidLength);
/*     */ 
/*  47 */     for (int i = 0; i < uidLength; i++) {
/*  48 */       char c = uidStr.charAt(i);
/*     */ 
/*  50 */       if ((Character.isLetter(c)) || (Character.isDigit(c)))
/*  51 */         safeString.append(c);
/*     */       else {
/*  53 */         safeString.append('_');
/*     */       }
/*     */     }
/*     */ 
/*  57 */     return safeString.toString();
/*     */   }
/*     */ 
/*     */   MysqlSavepoint()
/*     */     throws SQLException
/*     */   {
/*  71 */     this(getUniqueId());
/*     */   }
/*     */ 
/*     */   MysqlSavepoint(String name)
/*     */     throws SQLException
/*     */   {
/*  84 */     if ((name == null) || (name.length() == 0)) {
/*  85 */       throw new SQLException("Savepoint name can not be NULL or empty", "S1009");
/*     */     }
/*     */ 
/*  89 */     this.savepointName = name;
/*     */   }
/*     */ 
/*     */   public int getSavepointId()
/*     */     throws SQLException
/*     */   {
/*  96 */     throw new SQLException("Only named savepoints are supported.", "S1C00");
/*     */   }
/*     */ 
/*     */   public String getSavepointName()
/*     */     throws SQLException
/*     */   {
/* 104 */     return this.savepointName;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.MysqlSavepoint
 * JD-Core Version:    0.6.0
 */
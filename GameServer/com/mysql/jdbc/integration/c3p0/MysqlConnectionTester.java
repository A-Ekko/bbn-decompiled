/*     */ package com.mysql.jdbc.integration.c3p0;
/*     */ 
/*     */ import com.mchange.v2.c3p0.C3P0ProxyConnection;
/*     */ import com.mchange.v2.c3p0.QueryConnectionTester;
/*     */ import com.mysql.jdbc.CommunicationsException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public final class MysqlConnectionTester
/*     */   implements QueryConnectionTester
/*     */ {
/*     */   private static final long serialVersionUID = 3256444690067896368L;
/*  47 */   private static final Object[] NO_ARGS_ARRAY = new Object[0];
/*     */   private Method pingMethod;
/*     */ 
/*     */   public MysqlConnectionTester()
/*     */   {
/*     */     try
/*     */     {
/*  53 */       this.pingMethod = com.mysql.jdbc.Connection.class.getMethod("ping", null);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection con)
/*     */   {
/*  68 */     C3P0ProxyConnection castCon = (C3P0ProxyConnection)con;
/*     */     try
/*     */     {
/*  71 */       if (this.pingMethod != null) {
/*  72 */         castCon.rawConnectionOperation(this.pingMethod, C3P0ProxyConnection.RAW_CONNECTION, NO_ARGS_ARRAY);
/*     */       }
/*     */       else {
/*  75 */         Statement pingStatement = null;
/*     */         try
/*     */         {
/*  78 */           pingStatement = con.createStatement();
/*  79 */           pingStatement.executeQuery("SELECT 1").close();
/*     */         } finally {
/*  81 */           if (pingStatement != null) {
/*  82 */             pingStatement.close();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  87 */       return 0; } catch (Exception ex) {
/*     */     }
/*  89 */     return -1;
/*     */   }
/*     */ 
/*     */   public int statusOnException(java.sql.Connection arg0, Throwable throwable)
/*     */   {
/* 100 */     if ((throwable instanceof CommunicationsException)) {
/* 101 */       return -1;
/*     */     }
/*     */ 
/* 104 */     if ((throwable instanceof SQLException)) {
/* 105 */       String sqlState = ((SQLException)throwable).getSQLState();
/*     */ 
/* 107 */       if ((sqlState != null) && (sqlState.startsWith("08"))) {
/* 108 */         return -1;
/*     */       }
/*     */ 
/* 111 */       return 0;
/*     */     }
/*     */ 
/* 116 */     return -1;
/*     */   }
/*     */ 
/*     */   public int activeCheckConnection(java.sql.Connection arg0, String arg1)
/*     */   {
/* 126 */     return 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.integration.c3p0.MysqlConnectionTester
 * JD-Core Version:    0.6.0
 */
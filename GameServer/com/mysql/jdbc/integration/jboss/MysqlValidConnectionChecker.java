/*    */ package com.mysql.jdbc.integration.jboss;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.lang.reflect.Method;
/*    */ import java.sql.Connection;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.sql.Statement;
/*    */ import org.jboss.resource.adapter.jdbc.ValidConnectionChecker;
/*    */ 
/*    */ public final class MysqlValidConnectionChecker
/*    */   implements ValidConnectionChecker, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 3258689922776119348L;
/*    */   private Method pingMethod;
/* 46 */   private static final Object[] NO_ARGS_OBJECT_ARRAY = new Object[0];
/*    */ 
/*    */   public MysqlValidConnectionChecker()
/*    */   {
/*    */     try {
/* 51 */       Class mysqlConnection = Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.Connection");
/*    */ 
/* 55 */       this.pingMethod = mysqlConnection.getMethod("ping", null);
/*    */     }
/*    */     catch (Exception ex)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public SQLException isValidConnection(Connection conn)
/*    */   {
/* 67 */     if (this.pingMethod != null) {
/*    */       try {
/* 69 */         this.pingMethod.invoke(conn, NO_ARGS_OBJECT_ARRAY);
/*    */ 
/* 71 */         return null;
/*    */       } catch (Exception ex) {
/* 73 */         if ((ex instanceof SQLException)) {
/* 74 */           return (SQLException)ex;
/*    */         }
/*    */ 
/* 77 */         return new SQLException("Ping failed: " + ex.toString());
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 83 */     Statement pingStatement = null;
/*    */     try
/*    */     {
/* 86 */       pingStatement.executeQuery("SELECT 1").close();
/*    */ 
/* 88 */       localObject1 = null;
/*    */     }
/*    */     catch (SQLException sqlEx)
/*    */     {
/*    */       Object localObject1;
/* 90 */       return sqlEx;
/*    */     } finally {
/* 92 */       if (pingStatement != null)
/*    */         try {
/* 94 */           pingStatement.close();
/*    */         }
/*    */         catch (SQLException sqlEx)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.integration.jboss.MysqlValidConnectionChecker
 * JD-Core Version:    0.6.0
 */
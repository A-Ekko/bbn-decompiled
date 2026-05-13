/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.sql.Connection;
/*    */ import java.sql.Statement;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class DefaultConnectionValidator
/*    */   implements ConnectionValidatorIF
/*    */ {
/*    */   public boolean validate(ConnectionPoolDefinitionIF cpd, Connection connection)
/*    */   {
/* 36 */     String testSql = cpd.getHouseKeepingTestSql();
/* 37 */     if ((testSql == null) || (testSql.length() == 0)) {
/* 38 */       Log log = getPoolLog(cpd.getAlias());
/* 39 */       log.warn("Connection validation requested but house-keeping-test-sql not defined");
/* 40 */       return false;
/*    */     }
/*    */ 
/* 46 */     Statement st = null;
/*    */     try {
/* 48 */       st = connection.createStatement();
/* 49 */       st.execute(testSql);
/*    */ 
/* 51 */       int i = 1;
/*    */       return i;
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 56 */       Log log = getPoolLog(cpd.getAlias());
/* 57 */       if (log.isDebugEnabled()) {
/* 58 */         log.debug("A connection failed the validation test with error: " + t);
/*    */       }
/* 60 */       int j = 0;
/*    */       return j;
/*    */     }
/*    */     finally
/*    */     {
/* 63 */       if (st != null)
/*    */         try {
/* 65 */           st.close();
/*    */         }
/*    */         catch (Throwable t) {
/* 68 */           return false;
/*    */         } 
/* 68 */     }throw localObject;
/*    */   }
/*    */ 
/*    */   private Log getPoolLog(String poolAlias)
/*    */   {
/* 81 */     return LogFactory.getLog("org.logicalcobwebs.proxool." + poolAlias);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.DefaultConnectionValidator
 * JD-Core Version:    0.6.0
 */
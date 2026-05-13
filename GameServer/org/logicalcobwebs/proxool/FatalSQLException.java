/*    */ package org.logicalcobwebs.proxool;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class FatalSQLException extends SQLException
/*    */ {
/*    */   private SQLException cause;
/*    */ 
/*    */   public FatalSQLException(SQLException cause)
/*    */   {
/* 27 */     this(cause, cause.getMessage(), cause.getSQLState());
/*    */   }
/*    */ 
/*    */   public FatalSQLException(SQLException cause, String reason, String sqlState)
/*    */   {
/* 36 */     super(reason, sqlState);
/* 37 */     this.cause = cause;
/*    */   }
/*    */ 
/*    */   public Throwable getCause()
/*    */   {
/* 45 */     return this.cause;
/*    */   }
/*    */ 
/*    */   public SQLException getOriginalSQLException()
/*    */   {
/* 53 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.FatalSQLException
 * JD-Core Version:    0.6.0
 */
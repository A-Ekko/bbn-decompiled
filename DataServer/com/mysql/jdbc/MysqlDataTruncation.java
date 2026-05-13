/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ import java.sql.DataTruncation;
/*    */ 
/*    */ public class MysqlDataTruncation extends DataTruncation
/*    */ {
/*    */   private String message;
/*    */ 
/*    */   public MysqlDataTruncation(String message, int index, boolean parameter, boolean read, int dataSize, int transferSize)
/*    */   {
/* 60 */     super(index, parameter, read, dataSize, transferSize);
/*    */ 
/* 62 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 72 */     return super.getMessage() + ": " + this.message;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.MysqlDataTruncation
 * JD-Core Version:    0.6.0
 */
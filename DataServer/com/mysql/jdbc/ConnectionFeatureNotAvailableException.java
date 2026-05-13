/*    */ package com.mysql.jdbc;
/*    */ 
/*    */ public class ConnectionFeatureNotAvailableException extends CommunicationsException
/*    */ {
/*    */   public ConnectionFeatureNotAvailableException(Connection conn, long lastPacketSentTimeMs, Exception underlyingException)
/*    */   {
/* 47 */     super(conn, lastPacketSentTimeMs, underlyingException);
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 56 */     return "Feature not available in this distribution of Connector/J";
/*    */   }
/*    */ 
/*    */   public String getSQLState()
/*    */   {
/* 65 */     return "01S00";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionFeatureNotAvailableException
 * JD-Core Version:    0.6.0
 */
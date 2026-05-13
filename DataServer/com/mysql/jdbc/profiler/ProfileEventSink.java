/*    */ package com.mysql.jdbc.profiler;
/*    */ 
/*    */ import com.mysql.jdbc.Connection;
/*    */ import com.mysql.jdbc.log.Log;
/*    */ import java.sql.SQLException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ProfileEventSink
/*    */ {
/* 39 */   private static final Map CONNECTIONS_TO_SINKS = new HashMap();
/*    */ 
/* 41 */   private Connection ownerConnection = null;
/*    */ 
/* 43 */   private Log log = null;
/*    */ 
/*    */   public static synchronized ProfileEventSink getInstance(Connection conn)
/*    */   {
/* 54 */     ProfileEventSink sink = (ProfileEventSink)CONNECTIONS_TO_SINKS.get(conn);
/*    */ 
/* 57 */     if (sink == null) {
/* 58 */       sink = new ProfileEventSink(conn);
/* 59 */       CONNECTIONS_TO_SINKS.put(conn, sink);
/*    */     }
/*    */ 
/* 62 */     return sink;
/*    */   }
/*    */ 
/*    */   public void consumeEvent(ProfilerEvent evt)
/*    */   {
/* 72 */     if (evt.eventType == 0)
/* 73 */       this.log.logWarn(evt);
/*    */     else
/* 75 */       this.log.logInfo(evt);
/*    */   }
/*    */ 
/*    */   private ProfileEventSink(Connection conn)
/*    */   {
/* 80 */     this.ownerConnection = conn;
/*    */     try
/*    */     {
/* 83 */       this.log = this.ownerConnection.getLog();
/*    */     } catch (SQLException sqlEx) {
/* 85 */       throw new RuntimeException("Unable to get logger from connection");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfileEventSink
 * JD-Core Version:    0.6.0
 */
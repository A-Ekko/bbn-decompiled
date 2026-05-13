/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.net.BindException;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class CommunicationsException extends SQLException
/*     */ {
/*     */   private static final long DEFAULT_WAIT_TIMEOUT_SECONDS = 28800L;
/*     */   private static final int DUE_TO_TIMEOUT_FALSE = 0;
/*     */   private static final int DUE_TO_TIMEOUT_MAYBE = 2;
/*     */   private static final int DUE_TO_TIMEOUT_TRUE = 1;
/*     */   private String exceptionMessage;
/*     */ 
/*     */   public CommunicationsException(Connection conn, long lastPacketSentTimeMs, Exception underlyingException)
/*     */   {
/*  58 */     long serverTimeoutSeconds = 0L;
/*  59 */     boolean isInteractiveClient = false;
/*     */ 
/*  61 */     if (conn != null) {
/*  62 */       isInteractiveClient = conn.getInteractiveClient();
/*     */ 
/*  64 */       String serverTimeoutSecondsStr = null;
/*     */ 
/*  66 */       if (isInteractiveClient) {
/*  67 */         serverTimeoutSecondsStr = conn.getServerVariable("interactive_timeout");
/*     */       }
/*     */       else {
/*  70 */         serverTimeoutSecondsStr = conn.getServerVariable("wait_timeout");
/*     */       }
/*     */ 
/*  74 */       if (serverTimeoutSecondsStr != null) {
/*     */         try {
/*  76 */           serverTimeoutSeconds = Long.parseLong(serverTimeoutSecondsStr);
/*     */         }
/*     */         catch (NumberFormatException nfe) {
/*  79 */           serverTimeoutSeconds = 0L;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  84 */     StringBuffer exceptionMessageBuf = new StringBuffer();
/*     */ 
/*  86 */     if (lastPacketSentTimeMs == 0L) {
/*  87 */       lastPacketSentTimeMs = System.currentTimeMillis();
/*     */     }
/*     */ 
/*  90 */     long timeSinceLastPacket = (System.currentTimeMillis() - lastPacketSentTimeMs) / 1000L;
/*     */ 
/*  92 */     int dueToTimeout = 0;
/*     */ 
/*  94 */     StringBuffer timeoutMessageBuf = null;
/*     */ 
/*  96 */     if (serverTimeoutSeconds != 0L) {
/*  97 */       if (timeSinceLastPacket > serverTimeoutSeconds) {
/*  98 */         dueToTimeout = 1;
/*     */ 
/* 100 */         timeoutMessageBuf = new StringBuffer();
/*     */ 
/* 102 */         timeoutMessageBuf.append(Messages.getString("CommunicationsException.2"));
/*     */ 
/* 105 */         if (!isInteractiveClient) {
/* 106 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.3"));
/*     */         }
/*     */         else {
/* 109 */           timeoutMessageBuf.append(Messages.getString("CommunicationsException.4"));
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 114 */     else if (timeSinceLastPacket > 28800L) {
/* 115 */       dueToTimeout = 2;
/*     */ 
/* 117 */       timeoutMessageBuf = new StringBuffer();
/*     */ 
/* 119 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.5"));
/*     */ 
/* 121 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.6"));
/*     */ 
/* 123 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.7"));
/*     */ 
/* 125 */       timeoutMessageBuf.append(Messages.getString("CommunicationsException.8"));
/*     */     }
/*     */ 
/* 129 */     if ((dueToTimeout == 1) || (dueToTimeout == 2))
/*     */     {
/* 132 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.9"));
/*     */ 
/* 134 */       exceptionMessageBuf.append(timeSinceLastPacket);
/* 135 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.10"));
/*     */ 
/* 138 */       if (timeoutMessageBuf != null) {
/* 139 */         exceptionMessageBuf.append(timeoutMessageBuf);
/*     */       }
/*     */ 
/* 142 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.11"));
/*     */ 
/* 144 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.12"));
/*     */ 
/* 146 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.13"));
/*     */     }
/* 155 */     else if ((underlyingException instanceof BindException))
/*     */     {
/* 157 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.14"));
/*     */ 
/* 159 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.15"));
/*     */ 
/* 161 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.16"));
/*     */ 
/* 163 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.17"));
/*     */ 
/* 165 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.18"));
/*     */ 
/* 167 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.19"));
/*     */     }
/*     */ 
/* 172 */     if (exceptionMessageBuf.length() == 0)
/*     */     {
/* 174 */       exceptionMessageBuf.append(Messages.getString("CommunicationsException.20"));
/*     */ 
/* 177 */       if (underlyingException != null) {
/* 178 */         exceptionMessageBuf.append(Messages.getString("CommunicationsException.21"));
/*     */ 
/* 180 */         exceptionMessageBuf.append(Util.stackTraceToString(underlyingException));
/*     */       }
/*     */ 
/* 184 */       if ((conn != null) && (conn.getMaintainTimeStats()) && (!conn.getParanoid()))
/*     */       {
/* 186 */         exceptionMessageBuf.append("\n\nLast packet sent to the server was ");
/* 187 */         exceptionMessageBuf.append(System.currentTimeMillis() - lastPacketSentTimeMs);
/* 188 */         exceptionMessageBuf.append(" ms ago.");
/*     */       }
/*     */     }
/*     */ 
/* 192 */     this.exceptionMessage = exceptionMessageBuf.toString();
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 201 */     return this.exceptionMessage;
/*     */   }
/*     */ 
/*     */   public String getSQLState()
/*     */   {
/* 210 */     return "08S01";
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.CommunicationsException
 * JD-Core Version:    0.6.0
 */
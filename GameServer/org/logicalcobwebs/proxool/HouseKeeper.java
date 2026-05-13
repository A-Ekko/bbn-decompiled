/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.Statement;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ class HouseKeeper
/*     */ {
/*  24 */   private static final Log LOG = LogFactory.getLog(HouseKeeper.class);
/*     */   private ConnectionPool connectionPool;
/*     */   private long timeLastSwept;
/*     */ 
/*     */   public HouseKeeper(ConnectionPool connectionPool)
/*     */   {
/*  31 */     this.connectionPool = connectionPool;
/*     */   }
/*     */ 
/*     */   protected void sweep() throws ProxoolException {
/*  35 */     ConnectionPoolDefinitionIF definition = this.connectionPool.getDefinition();
/*  36 */     Log log = this.connectionPool.getLog();
/*  37 */     Statement testStatement = null;
/*     */     try
/*     */     {
/*  40 */       this.connectionPool.acquirePrimaryReadLock();
/*     */ 
/*  43 */       Connection connection = null;
/*  44 */       ProxyConnectionIF proxyConnection = null;
/*     */ 
/*  46 */       int recentlyStartedActiveConnectionCountTemp = 0;
/*     */ 
/*  49 */       int[] verifiedConnectionCountByState = new int[4];
/*     */ 
/*  51 */       ProxyConnectionIF[] proxyConnections = this.connectionPool.getProxyConnections();
/*  52 */       for (int i = 0; i < proxyConnections.length; i++) {
/*  53 */         proxyConnection = proxyConnections[i];
/*  54 */         connection = proxyConnection.getConnection();
/*     */ 
/*  56 */         if (!this.connectionPool.isConnectionPoolUp())
/*     */         {
/*     */           break;
/*     */         }
/*     */ 
/*  63 */         if (proxyConnection.setStatus(1, 3)) {
/*     */           try {
/*  65 */             testStatement = connection.createStatement();
/*     */ 
/*  68 */             if (proxyConnection.isReallyClosed()) {
/*  69 */               proxyConnection.setStatus(3, 0);
/*  70 */               this.connectionPool.removeProxyConnection(proxyConnection, 6, "it appears to be closed", true, true);
/*     */             }
/*     */ 
/*  73 */             String sql = definition.getHouseKeepingTestSql();
/*  74 */             if ((sql != null) && (sql.length() > 0))
/*     */             {
/*  76 */               boolean testResult = false;
/*     */               try {
/*  78 */                 testResult = testStatement.execute(sql);
/*     */               } finally {
/*  80 */                 if ((log.isDebugEnabled()) && (definition.isVerbose())) {
/*  81 */                   log.debug(this.connectionPool.displayStatistics() + " - Testing connection " + proxyConnection.getId() + (testResult ? ": True" : ": False"));
/*     */                 }
/*     */               }
/*     */             }
/*     */ 
/*  86 */             proxyConnection.setStatus(3, 1);
/*     */           }
/*     */           catch (Throwable t) {
/*  89 */             proxyConnection.setStatus(3, 0);
/*  90 */             this.connectionPool.removeProxyConnection(proxyConnection, 6, "it has problems: " + e, false, true);
/*     */           } finally {
/*     */             try {
/*  93 */               testStatement.close();
/*     */             }
/*     */             catch (Throwable t)
/*     */             {
/*     */             }
/*     */           }
/*     */         }
/* 100 */         if (proxyConnection.getAge() > definition.getMaximumConnectionLifetime()) {
/* 101 */           String reason = "age is " + proxyConnection.getAge() + "ms";
/*     */ 
/* 103 */           if (proxyConnection.setStatus(1, 3)) {
/* 104 */             if (proxyConnection.setStatus(3, 0))
/*     */             {
/* 106 */               this.connectionPool.expireProxyConnection(proxyConnection, 7, reason, false);
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 112 */             proxyConnection.markForExpiry(reason);
/* 113 */             if (log.isDebugEnabled()) {
/* 114 */               log.debug(this.connectionPool.displayStatistics() + " - #" + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " marked for expiry.");
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 122 */         if (proxyConnection.isActive())
/*     */         {
/* 124 */           long activeTime = System.currentTimeMillis() - proxyConnection.getTimeLastStartActive();
/*     */ 
/* 126 */           if (activeTime < definition.getRecentlyStartedThreshold())
/*     */           {
/* 132 */             recentlyStartedActiveConnectionCountTemp++;
/*     */           }
/*     */ 
/* 135 */           if (activeTime > definition.getMaximumActiveTime())
/*     */           {
/* 139 */             this.connectionPool.removeProxyConnection(proxyConnection, 1, "it has been active for too long", true, true);
/*     */             String lastSqlCallMsg;
/*     */             String lastSqlCallMsg;
/* 142 */             if (proxyConnection.getLastSqlCall() != null) {
/* 143 */               lastSqlCallMsg = ", and the last SQL it performed is '" + proxyConnection.getLastSqlCall() + "'.";
/*     */             }
/*     */             else
/*     */             {
/*     */               String lastSqlCallMsg;
/* 144 */               if (!proxyConnection.getDefinition().isTrace())
/* 145 */                 lastSqlCallMsg = ", but the last SQL it performed is unknown because the trace property is not enabled.";
/*     */               else
/* 147 */                 lastSqlCallMsg = ", but the last SQL it performed is unknown.";
/*     */             }
/* 149 */             log.warn("#" + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " was active for " + activeTime + " milliseconds and has been removed automaticaly. The Thread responsible was named '" + proxyConnection.getRequester() + "'" + lastSqlCallMsg);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 158 */         verifiedConnectionCountByState[proxyConnection.getStatus()] += 1;
/*     */       }
/*     */ 
/* 162 */       calculateUpState(recentlyStartedActiveConnectionCountTemp);
/*     */     }
/*     */     catch (Throwable e) {
/* 165 */       log.error("Housekeeping log.error( :", e);
/*     */     } finally {
/* 167 */       this.connectionPool.releasePrimaryReadLock();
/* 168 */       this.timeLastSwept = System.currentTimeMillis();
/* 169 */       if ((definition.isVerbose()) && 
/* 170 */         (log.isDebugEnabled())) {
/* 171 */         log.debug(this.connectionPool.displayStatistics() + " - House keeping triggerSweep done");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 176 */     PrototyperController.triggerSweep(definition.getAlias());
/*     */   }
/*     */ 
/*     */   private long getTimeSinceLastSweep()
/*     */   {
/* 185 */     return System.currentTimeMillis() - this.timeLastSwept;
/*     */   }
/*     */ 
/*     */   protected boolean isSweepDue()
/*     */   {
/* 195 */     if (this.connectionPool.isConnectionPoolUp()) {
/* 196 */       return getTimeSinceLastSweep() > this.connectionPool.getDefinition().getHouseKeepingSleepTime();
/*     */     }
/* 198 */     LOG.warn("House keeper is still being asked to sweep despite the connection pool being down");
/* 199 */     return false;
/*     */   }
/*     */ 
/*     */   private void calculateUpState(int recentlyStartedActiveConnectionCount)
/*     */   {
/*     */     try
/*     */     {
/* 207 */       int calculatedUpState = 0;
/*     */ 
/* 221 */       int availableConnectionCount = this.connectionPool.getAvailableConnectionCount();
/* 222 */       if ((availableConnectionCount > 0) || (recentlyStartedActiveConnectionCount > 0))
/*     */       {
/* 228 */         if (this.connectionPool.getTimeOfLastRefusal() > System.currentTimeMillis() - this.connectionPool.getDefinition().getOverloadWithoutRefusalLifetime())
/*     */         {
/* 230 */           calculatedUpState = 2;
/* 231 */         } else if (this.connectionPool.getActiveConnectionCount() > 0)
/*     */         {
/* 234 */           calculatedUpState = 1;
/*     */         }
/*     */       }
/*     */       else {
/* 238 */         calculatedUpState = 3;
/*     */       }
/*     */ 
/* 241 */       this.connectionPool.setUpState(calculatedUpState);
/*     */     }
/*     */     catch (Exception e) {
/* 244 */       LOG.error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getAlias()
/*     */   {
/* 253 */     return this.connectionPool.getDefinition().getAlias();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.HouseKeeper
 * JD-Core Version:    0.6.0
 */
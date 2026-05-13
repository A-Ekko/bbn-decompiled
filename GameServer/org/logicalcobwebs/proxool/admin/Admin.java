/*     */ package org.logicalcobwebs.proxool.admin;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
/*     */ import org.logicalcobwebs.proxool.ConnectionPoolStatisticsIF;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ 
/*     */ public class Admin
/*     */ {
/*  33 */   private static final Log LOG = LogFactory.getLog(Admin.class);
/*     */   private Log log;
/*  37 */   private Map statsRollers = new HashMap();
/*     */ 
/*  39 */   private CompositeStatisticsListener compositeStatisticsListener = new CompositeStatisticsListener();
/*     */ 
/*     */   public Admin(ConnectionPoolDefinitionIF definition)
/*     */     throws ProxoolException
/*     */   {
/*  46 */     this.log = LogFactory.getLog("org.logicalcobwebs.proxool.stats." + definition.getAlias());
/*     */ 
/*  48 */     StringTokenizer st = new StringTokenizer(definition.getStatistics(), ",");
/*  49 */     while (st.hasMoreTokens()) {
/*  50 */       String token = st.nextToken();
/*  51 */       this.statsRollers.put(token, new StatsRoller(definition.getAlias(), this.compositeStatisticsListener, token));
/*     */     }
/*     */ 
/*  54 */     if (definition.getStatisticsLogLevel() != null)
/*  55 */       this.compositeStatisticsListener.addListener(new StatisticsLogger(this.log, definition.getStatisticsLogLevel()));
/*     */   }
/*     */ 
/*     */   public void addStatisticsListener(StatisticsListenerIF statisticsListener)
/*     */   {
/*  61 */     this.compositeStatisticsListener.addListener(statisticsListener);
/*     */   }
/*     */ 
/*     */   public void connectionReturned(long activeTime)
/*     */   {
/*     */     try
/*     */     {
/*  71 */       Iterator i = this.statsRollers.values().iterator();
/*  72 */       while (i.hasNext()) {
/*  73 */         StatsRoller statsRoller = (StatsRoller)i.next();
/*  74 */         statsRoller.connectionReturned(activeTime);
/*     */       }
/*     */     } catch (Throwable e) {
/*  77 */       LOG.error("Stats connectionReturned call failed. Ignoring.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connectionRefused()
/*     */   {
/*     */     try
/*     */     {
/*  86 */       Iterator i = this.statsRollers.values().iterator();
/*  87 */       while (i.hasNext()) {
/*  88 */         StatsRoller statsRoller = (StatsRoller)i.next();
/*  89 */         statsRoller.connectionRefused();
/*     */       }
/*     */     } catch (Exception e) {
/*  92 */       LOG.error("Stats connectionRefused call failed. Ignoring.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public StatisticsIF getStatistics(String token)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return ((StatsRoller)this.statsRollers.get(token)).getCompleteStatistics(); } catch (NullPointerException e) {
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   public void cancelAll()
/*     */   {
/* 112 */     Iterator i = this.statsRollers.values().iterator();
/* 113 */     while (i.hasNext()) {
/* 114 */       StatsRoller statsRoller = (StatsRoller)i.next();
/* 115 */       statsRoller.cancel();
/*     */     }
/*     */   }
/*     */ 
/*     */   public StatisticsIF[] getStatistics() {
/* 120 */     List statistics = new Vector();
/* 121 */     Iterator i = this.statsRollers.values().iterator();
/* 122 */     while (i.hasNext()) {
/* 123 */       StatsRoller statsRoller = (StatsRoller)i.next();
/* 124 */       StatisticsIF s = statsRoller.getCompleteStatistics();
/* 125 */       if (s != null) {
/* 126 */         statistics.add(s);
/*     */       }
/*     */     }
/* 129 */     return (StatisticsIF[])(StatisticsIF[])statistics.toArray(new StatisticsIF[statistics.size()]);
/*     */   }
/*     */ 
/*     */   public static SnapshotIF getSnapshot(ConnectionPoolStatisticsIF cps, ConnectionPoolDefinitionIF cpd, Collection connectionInfos)
/*     */   {
/* 139 */     Snapshot s = new Snapshot(new Date());
/*     */ 
/* 141 */     s.setDateStarted(cps.getDateStarted());
/* 142 */     s.setActiveConnectionCount(cps.getActiveConnectionCount());
/* 143 */     s.setAvailableConnectionCount(cps.getAvailableConnectionCount());
/* 144 */     s.setOfflineConnectionCount(cps.getOfflineConnectionCount());
/* 145 */     s.setMaximumConnectionCount(cpd.getMaximumConnectionCount());
/* 146 */     s.setServedCount(cps.getConnectionsServedCount());
/* 147 */     s.setRefusedCount(cps.getConnectionsRefusedCount());
/* 148 */     s.setConnectionInfos(connectionInfos);
/* 149 */     s.setConnectionCount(cps.getConnectionCount());
/*     */ 
/* 163 */     return s;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.Admin
 * JD-Core Version:    0.6.0
 */
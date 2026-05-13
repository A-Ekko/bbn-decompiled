/*     */ package org.logicalcobwebs.proxool.admin;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ConnectionInfoIF;
/*     */ 
/*     */ class Snapshot
/*     */   implements SnapshotIF
/*     */ {
/*  25 */   private static final Log LOG = LogFactory.getLog(Snapshot.class);
/*     */   private Date dateStarted;
/*     */   private long servedCount;
/*     */   private long refusedCount;
/*     */   private int activeConnectionCount;
/*     */   private int availableConnectionCount;
/*     */   private int offlineConnectionCount;
/*     */   private int maximumConnectionCount;
/*     */   private Date snapshotDate;
/*     */   private Collection connectionInfos;
/*     */   private long connectionCount;
/*     */ 
/*     */   public Snapshot(Date snapshotDate)
/*     */   {
/*  51 */     this.snapshotDate = snapshotDate;
/*     */   }
/*     */ 
/*     */   public Date getDateStarted()
/*     */   {
/*  58 */     return this.dateStarted;
/*     */   }
/*     */ 
/*     */   public void setDateStarted(Date dateStarted)
/*     */   {
/*  65 */     this.dateStarted = dateStarted;
/*     */   }
/*     */ 
/*     */   public long getServedCount()
/*     */   {
/*  72 */     return this.servedCount;
/*     */   }
/*     */ 
/*     */   public void setServedCount(long servedCount)
/*     */   {
/*  79 */     this.servedCount = servedCount;
/*     */   }
/*     */ 
/*     */   public long getRefusedCount()
/*     */   {
/*  86 */     return this.refusedCount;
/*     */   }
/*     */ 
/*     */   public void setRefusedCount(long refusedCount)
/*     */   {
/*  93 */     this.refusedCount = refusedCount;
/*     */   }
/*     */ 
/*     */   public int getActiveConnectionCount()
/*     */   {
/* 100 */     return this.activeConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setActiveConnectionCount(int activeConnectionCount)
/*     */   {
/* 107 */     this.activeConnectionCount = activeConnectionCount;
/*     */   }
/*     */ 
/*     */   public int getAvailableConnectionCount()
/*     */   {
/* 114 */     return this.availableConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setAvailableConnectionCount(int availableConnectionCount)
/*     */   {
/* 121 */     this.availableConnectionCount = availableConnectionCount;
/*     */   }
/*     */ 
/*     */   public int getOfflineConnectionCount()
/*     */   {
/* 128 */     return this.offlineConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setOfflineConnectionCount(int offlineConnectionCount)
/*     */   {
/* 135 */     this.offlineConnectionCount = offlineConnectionCount;
/*     */   }
/*     */ 
/*     */   public int getMaximumConnectionCount()
/*     */   {
/* 142 */     return this.maximumConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setMaximumConnectionCount(int maximumConnectionCount)
/*     */   {
/* 149 */     this.maximumConnectionCount = maximumConnectionCount;
/*     */   }
/*     */ 
/*     */   public Date getSnapshotDate()
/*     */   {
/* 156 */     return this.snapshotDate;
/*     */   }
/*     */ 
/*     */   public ConnectionInfoIF[] getConnectionInfos()
/*     */   {
/* 163 */     return (ConnectionInfoIF[])(ConnectionInfoIF[])this.connectionInfos.toArray(new ConnectionInfoIF[this.connectionInfos.size()]);
/*     */   }
/*     */ 
/*     */   public void setConnectionInfos(Collection connectionInfos)
/*     */   {
/* 170 */     this.connectionInfos = connectionInfos;
/*     */   }
/*     */ 
/*     */   public ConnectionInfoIF getConnectionInfo(long id)
/*     */   {
/* 177 */     ConnectionInfoIF connectionInfo = null;
/* 178 */     ConnectionInfoIF[] connectionInfos = getConnectionInfos();
/* 179 */     for (int i = 0; i < connectionInfos.length; i++) {
/* 180 */       if (connectionInfos[i].getId() == id) {
/* 181 */         connectionInfo = connectionInfos[i];
/*     */       }
/*     */     }
/* 184 */     return connectionInfo;
/*     */   }
/*     */ 
/*     */   public boolean isDetail()
/*     */   {
/* 191 */     return this.connectionInfos != null;
/*     */   }
/*     */ 
/*     */   public long getConnectionCount() {
/* 195 */     return this.connectionCount;
/*     */   }
/*     */ 
/*     */   public void setConnectionCount(long connectionCount) {
/* 199 */     this.connectionCount = connectionCount;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.Snapshot
 * JD-Core Version:    0.6.0
 */
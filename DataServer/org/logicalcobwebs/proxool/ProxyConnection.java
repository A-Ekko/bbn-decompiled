/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.concurrent.Sync;
/*     */ import org.logicalcobwebs.concurrent.WriterPreferenceReadWriteLock;
/*     */ import org.logicalcobwebs.proxool.util.FastArrayList;
/*     */ 
/*     */ public class ProxyConnection
/*     */   implements ProxyConnectionIF
/*     */ {
/*     */   static final int STATUS_FORCE = -1;
/*  34 */   private WriterPreferenceReadWriteLock statusReadWriteLock = new WriterPreferenceReadWriteLock();
/*     */ 
/*  36 */   private static final Log LOG = LogFactory.getLog(ProxyConnection.class);
/*     */   private Connection connection;
/*     */   private String delegateUrl;
/*     */   private int mark;
/*     */   private String reasonForMark;
/*     */   private int status;
/*     */   private long id;
/*     */   private Date birthDate;
/*     */   private long timeLastStartActive;
/*     */   private long timeLastStopActive;
/*     */   private ConnectionPool connectionPool;
/*     */   private ConnectionPoolDefinitionIF definition;
/*     */   private String requester;
/*  62 */   private Set openStatements = new HashSet();
/*     */ 
/*  64 */   private DecimalFormat idFormat = new DecimalFormat("0000");
/*     */ 
/*  66 */   private List sqlCalls = new FastArrayList();
/*     */ 
/*  71 */   private boolean needToReset = false;
/*     */ 
/*     */   protected ProxyConnection(Connection connection, long id, String delegateUrl, ConnectionPool connectionPool, ConnectionPoolDefinitionIF definition, int status)
/*     */     throws SQLException
/*     */   {
/*  85 */     this.connection = connection;
/*  86 */     this.delegateUrl = delegateUrl;
/*  87 */     setId(id);
/*  88 */     this.connectionPool = connectionPool;
/*  89 */     this.definition = definition;
/*  90 */     setBirthTime(System.currentTimeMillis());
/*     */ 
/*  92 */     this.status = status;
/*  93 */     if (status == 2) {
/*  94 */       setTimeLastStartActive(System.currentTimeMillis());
/*     */     }
/*     */ 
/*  99 */     connectionPool.initialiseConnectionResetter(connection);
/*     */ 
/* 101 */     if (connection == null)
/* 102 */       throw new SQLException("Unable to create new connection");
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 113 */     if (obj != null) {
/* 114 */       if ((obj instanceof ProxyConnection))
/* 115 */         return this.connection.hashCode() == ((ProxyConnection)obj).getConnection().hashCode();
/* 116 */       if ((obj instanceof Connection)) {
/* 117 */         return this.connection.hashCode() == obj.hashCode();
/*     */       }
/* 119 */       return super.equals(obj);
/*     */     }
/*     */ 
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 132 */     return getStatus() != 2;
/*     */   }
/*     */ 
/*     */   protected void setNeedToReset(boolean needToReset)
/*     */   {
/* 144 */     this.needToReset = needToReset;
/*     */   }
/*     */ 
/*     */   protected ConnectionPool getConnectionPool()
/*     */   {
/* 152 */     return this.connectionPool;
/*     */   }
/*     */ 
/*     */   public ConnectionPoolDefinitionIF getDefinition()
/*     */   {
/* 160 */     return this.definition;
/*     */   }
/*     */ 
/*     */   protected void addOpenStatement(Statement statement)
/*     */   {
/* 170 */     this.openStatements.add(statement);
/*     */   }
/*     */ 
/*     */   public void registerClosedStatement(Statement statement)
/*     */   {
/* 177 */     if (this.openStatements.contains(statement))
/* 178 */       this.openStatements.remove(statement);
/*     */     else
/* 180 */       this.connectionPool.getLog().warn(this.connectionPool.displayStatistics() + " - #" + getId() + " registered a statement as closed which wasn't known to be open. This could happen if you close a statement twice.");
/*     */   }
/*     */ 
/*     */   public void reallyClose()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 190 */       this.connectionPool.registerRemovedConnection(getStatus());
/*     */ 
/* 192 */       this.connection.close();
/*     */     } catch (Throwable t) {
/* 194 */       this.connectionPool.getLog().error("#" + this.idFormat.format(getId()) + " encountered errors during destruction: ", t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isReallyClosed()
/*     */     throws SQLException
/*     */   {
/* 203 */     if (this.connection == null) {
/* 204 */       return true;
/*     */     }
/* 206 */     return this.connection.isClosed();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/*     */     try
/*     */     {
/* 215 */       boolean removed = false;
/* 216 */       if (isMarkedForExpiry()) {
/* 217 */         if (this.connectionPool.getLog().isDebugEnabled())
/* 218 */           this.connectionPool.getLog().debug("Closing connection quickly (without reset) because it's marked for expiry anyway");
/*     */       }
/*     */       else
/*     */       {
/* 222 */         Statement[] statements = (Statement[])(Statement[])this.openStatements.toArray(new Statement[this.openStatements.size()]);
/* 223 */         for (int j = 0; j < statements.length; j++) {
/* 224 */           Statement statement = statements[j];
/* 225 */           statement.close();
/* 226 */           if (this.connectionPool.getLog().isDebugEnabled()) {
/* 227 */             this.connectionPool.getLog().debug("Closing statement " + Integer.toHexString(statement.hashCode()) + " (belonging to connection " + getId() + ") automatically");
/*     */           }
/*     */         }
/* 230 */         this.openStatements.clear();
/*     */ 
/* 232 */         if (this.needToReset)
/*     */         {
/* 237 */           if (!this.connectionPool.resetConnection(this.connection, "#" + getId())) {
/* 238 */             this.connectionPool.removeProxyConnection(this, 5, "it couldn't be reset", true, true);
/* 239 */             removed = true;
/*     */           }
/* 241 */           this.needToReset = false;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 246 */       if (!removed)
/* 247 */         this.connectionPool.putConnection(this);
/*     */     }
/*     */     catch (Throwable t) {
/* 250 */       this.connectionPool.getLog().error("#" + this.idFormat.format(getId()) + " encountered errors during closure: ", t);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void open()
/*     */   {
/* 260 */     this.sqlCalls.clear();
/*     */   }
/*     */ 
/*     */   public int getMark() {
/* 264 */     return this.mark;
/*     */   }
/*     */ 
/*     */   public int getStatus() {
/* 268 */     return this.status;
/*     */   }
/*     */ 
/*     */   public boolean setStatus(int newStatus)
/*     */   {
/* 275 */     return setStatus(-1, newStatus);
/*     */   }
/*     */ 
/*     */   public boolean setStatus(int oldStatus, int newStatus)
/*     */   {
/* 282 */     boolean success = false;
/*     */     try {
/* 284 */       this.statusReadWriteLock.writeLock().acquire();
/* 285 */       this.connectionPool.acquireConnectionStatusWriteLock();
/* 286 */       if ((this.status == oldStatus) || (oldStatus == -1)) {
/* 287 */         this.connectionPool.changeStatus(this.status, newStatus);
/* 288 */         this.status = newStatus;
/* 289 */         success = true;
/*     */ 
/* 291 */         if (newStatus == oldStatus) {
/* 292 */           LOG.warn("Unexpected attempt to change status from " + oldStatus + " to " + newStatus + ". Why would you want to do that?");
/*     */         }
/* 294 */         else if (newStatus == 2)
/* 295 */           setTimeLastStartActive(System.currentTimeMillis());
/* 296 */         else if (oldStatus == 2)
/* 297 */           setTimeLastStopActive(System.currentTimeMillis());
/*     */       }
/*     */     }
/*     */     catch (InterruptedException e) {
/* 301 */       LOG.error("Unable to acquire write lock for status");
/*     */     } finally {
/* 303 */       this.connectionPool.releaseConnectionStatusWriteLock();
/* 304 */       this.statusReadWriteLock.writeLock().release();
/*     */     }
/* 306 */     return success;
/*     */   }
/*     */ 
/*     */   public long getId() {
/* 310 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(long id) {
/* 314 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public long getBirthTime()
/*     */   {
/* 321 */     return this.birthDate.getTime();
/*     */   }
/*     */ 
/*     */   public Date getBirthDate()
/*     */   {
/* 328 */     return this.birthDate;
/*     */   }
/*     */ 
/*     */   public long getAge()
/*     */   {
/* 335 */     return System.currentTimeMillis() - getBirthTime();
/*     */   }
/*     */ 
/*     */   public void setBirthTime(long birthTime)
/*     */   {
/* 342 */     this.birthDate = new Date(birthTime);
/*     */   }
/*     */ 
/*     */   public long getTimeLastStartActive()
/*     */   {
/* 349 */     return this.timeLastStartActive;
/*     */   }
/*     */ 
/*     */   public void setTimeLastStartActive(long timeLastStartActive)
/*     */   {
/* 356 */     this.timeLastStartActive = timeLastStartActive;
/* 357 */     setTimeLastStopActive(0L);
/*     */   }
/*     */ 
/*     */   public long getTimeLastStopActive()
/*     */   {
/* 364 */     return this.timeLastStopActive;
/*     */   }
/*     */ 
/*     */   public void setTimeLastStopActive(long timeLastStopActive)
/*     */   {
/* 371 */     this.timeLastStopActive = timeLastStopActive;
/*     */   }
/*     */ 
/*     */   public String getRequester()
/*     */   {
/* 378 */     return this.requester;
/*     */   }
/*     */ 
/*     */   public void setRequester(String requester)
/*     */   {
/* 385 */     this.requester = requester;
/*     */   }
/*     */ 
/*     */   public boolean isNull()
/*     */   {
/* 392 */     return getStatus() == 0;
/*     */   }
/*     */ 
/*     */   public boolean isAvailable()
/*     */   {
/* 399 */     return getStatus() == 1;
/*     */   }
/*     */ 
/*     */   public boolean isActive()
/*     */   {
/* 406 */     return getStatus() == 2;
/*     */   }
/*     */ 
/*     */   public boolean isOffline()
/*     */   {
/* 413 */     return getStatus() == 3;
/*     */   }
/*     */ 
/*     */   public void markForExpiry(String reason)
/*     */   {
/* 420 */     this.mark = 1;
/* 421 */     this.reasonForMark = reason;
/*     */   }
/*     */ 
/*     */   public boolean isMarkedForExpiry()
/*     */   {
/* 428 */     return getMark() == 1;
/*     */   }
/*     */ 
/*     */   public String getReasonForMark()
/*     */   {
/* 435 */     return this.reasonForMark;
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */   {
/* 442 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 449 */     return getId() + " is " + ConnectionPool.getStatusDescription(getStatus());
/*     */   }
/*     */ 
/*     */   public String getDelegateUrl()
/*     */   {
/* 456 */     return this.delegateUrl;
/*     */   }
/*     */ 
/*     */   public String getProxyHashcode()
/*     */   {
/* 463 */     return Integer.toHexString(hashCode());
/*     */   }
/*     */ 
/*     */   public String getDelegateHashcode()
/*     */   {
/* 470 */     if (this.connection != null) {
/* 471 */       return Integer.toHexString(this.connection.hashCode());
/*     */     }
/* 473 */     return null;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object o)
/*     */   {
/* 484 */     return new Long(((ConnectionInfoIF)o).getId()).compareTo(new Long(getId()));
/*     */   }
/*     */ 
/*     */   public String[] getSqlCalls() {
/* 488 */     return (String[])(String[])this.sqlCalls.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public String getLastSqlCall() {
/* 492 */     if ((this.sqlCalls != null) && (this.sqlCalls.size() > 0)) {
/* 493 */       return (String)this.sqlCalls.get(this.sqlCalls.size() - 1);
/*     */     }
/* 495 */     return null;
/*     */   }
/*     */ 
/*     */   public int getReasonCode()
/*     */   {
/* 500 */     return 0;
/*     */   }
/*     */ 
/*     */   public void addSqlCall(String sqlCall) {
/* 504 */     this.sqlCalls.add(sqlCall);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxyConnection
 * JD-Core Version:    0.6.0
 */
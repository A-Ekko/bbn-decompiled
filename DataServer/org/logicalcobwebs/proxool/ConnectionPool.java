/*      */ package org.logicalcobwebs.proxool;
/*      */ 
/*      */ import java.sql.Connection;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogFactory;
/*      */ import org.logicalcobwebs.concurrent.ReaderPreferenceReadWriteLock;
/*      */ import org.logicalcobwebs.concurrent.Sync;
/*      */ import org.logicalcobwebs.concurrent.WriterPreferenceReadWriteLock;
/*      */ import org.logicalcobwebs.proxool.admin.Admin;
/*      */ import org.logicalcobwebs.proxool.util.FastArrayList;
/*      */ 
/*      */ class ConnectionPool
/*      */   implements ConnectionPoolStatisticsIF
/*      */ {
/*   31 */   private static final Log LOG = LogFactory.getLog(ConnectionPool.class);
/*      */   private Log log;
/*   40 */   private ReaderPreferenceReadWriteLock connectionStatusReadWriteLock = new ReaderPreferenceReadWriteLock();
/*      */ 
/*   48 */   private WriterPreferenceReadWriteLock primaryReadWriteLock = new WriterPreferenceReadWriteLock();
/*      */ 
/*   50 */   private static final String[] STATUS_DESCRIPTIONS = { "NULL", "AVAILABLE", "ACTIVE", "OFFLINE" };
/*      */   private static final String MSG_MAX_CONNECTION_COUNT = "Couldn't get connection because we are at maximum connection count and there are none available";
/*      */   private List proxyConnections;
/*   59 */   private int nextAvailableConnection = 0;
/*      */ 
/*   61 */   private long connectionsServedCount = 0L;
/*      */ 
/*   63 */   private long connectionsRefusedCount = 0L;
/*      */ 
/*   66 */   private int[] connectionCountByState = new int[4];
/*      */   private ConnectionPoolDefinition definition;
/*   70 */   private CompositeConnectionListener compositeConnectionListener = new CompositeConnectionListener();
/*      */ 
/*   72 */   private CompositeStateListener compositeStateListener = new CompositeStateListener();
/*      */ 
/*   74 */   private long timeOfLastRefusal = 0L;
/*      */   private int upState;
/*      */   private static boolean loggedLegend;
/*      */   private Admin admin;
/*   82 */   private boolean locked = false;
/*      */ 
/*   84 */   private Date dateStarted = new Date();
/*      */ 
/*   86 */   private boolean connectionPoolUp = false;
/*      */   private Thread shutdownThread;
/*      */   private Prototyper prototyper;
/*      */   private ConnectionResetter connectionResetter;
/*      */   private ConnectionValidatorIF connectionValidator;
/*      */   protected static final boolean FORCE_EXPIRY = true;
/*      */   protected static final boolean REQUEST_EXPIRY = false;
/*      */ 
/*      */   protected ConnectionPool(ConnectionPoolDefinition definition)
/*      */     throws ProxoolException
/*      */   {
/*  109 */     FastArrayList fal = new FastArrayList();
/*  110 */     fal.setFast(true);
/*  111 */     this.proxyConnections = fal;
/*      */ 
/*  113 */     this.log = LogFactory.getLog("org.logicalcobwebs.proxool." + definition.getAlias());
/*  114 */     this.connectionResetter = new ConnectionResetter(this.log, definition.getDriver());
/*  115 */     setDefinition(definition);
/*      */ 
/*  117 */     this.connectionValidator = new DefaultConnectionValidator();
/*      */ 
/*  119 */     if (definition.getStatistics() != null) {
/*      */       try {
/*  121 */         this.admin = new Admin(definition);
/*      */       } catch (ProxoolException e) {
/*  123 */         this.log.error("Failed to initialise statistics", e);
/*      */       }
/*      */     }
/*      */ 
/*  127 */     ShutdownHook.init();
/*      */   }
/*      */ 
/*      */   protected void start() throws ProxoolException
/*      */   {
/*  132 */     this.connectionPoolUp = true;
/*  133 */     this.prototyper = new Prototyper(this);
/*  134 */     HouseKeeperController.register(this);
/*      */   }
/*      */ 
/*      */   protected Connection getConnection()
/*      */     throws SQLException
/*      */   {
/*  143 */     String requester = Thread.currentThread().getName();
/*      */     try
/*      */     {
/*  152 */       this.prototyper.quickRefuse();
/*      */     } catch (SQLException e) {
/*  154 */       this.connectionsRefusedCount += 1L;
/*  155 */       if (this.admin != null) {
/*  156 */         this.admin.connectionRefused();
/*      */       }
/*  158 */       this.log.info(displayStatistics() + " - " + "Couldn't get connection because we are at maximum connection count and there are none available");
/*  159 */       this.timeOfLastRefusal = System.currentTimeMillis();
/*  160 */       setUpState(2);
/*  161 */       throw e;
/*      */     }
/*      */ 
/*  164 */     this.prototyper.checkSimultaneousBuildThrottle();
/*      */ 
/*  166 */     ProxyConnection proxyConnection = null;
/*      */     try
/*      */     {
/*  171 */       for (int connectionsTried = 0; connectionsTried < this.proxyConnections.size(); connectionsTried++)
/*      */       {
/*      */         try
/*      */         {
/*  175 */           proxyConnection = (ProxyConnection)this.proxyConnections.get(this.nextAvailableConnection);
/*      */         }
/*      */         catch (ArrayIndexOutOfBoundsException e)
/*      */         {
/*  179 */           this.nextAvailableConnection = 0;
/*  180 */           proxyConnection = (ProxyConnection)this.proxyConnections.get(this.nextAvailableConnection);
/*      */         }
/*      */         catch (IndexOutOfBoundsException e) {
/*  183 */           this.nextAvailableConnection = 0;
/*  184 */           proxyConnection = (ProxyConnection)this.proxyConnections.get(this.nextAvailableConnection);
/*      */         }
/*      */ 
/*  189 */         if ((proxyConnection != null) && (proxyConnection.setStatus(1, 2)))
/*      */         {
/*  192 */           if ((getDefinition().isTestBeforeUse()) && 
/*  193 */             (!testConnection(proxyConnection)))
/*      */           {
/*  195 */             proxyConnection = null;
/*      */           }
/*      */ 
/*  198 */           if (proxyConnection != null) {
/*  199 */             this.nextAvailableConnection += 1;
/*  200 */             break;
/*      */           }
/*      */         } else {
/*  203 */           proxyConnection = null;
/*      */         }
/*  205 */         this.nextAvailableConnection += 1;
/*      */       }
/*      */ 
/*  208 */       if (proxyConnection == null)
/*      */         try
/*      */         {
/*  211 */           proxyConnection = this.prototyper.buildConnection(2, "on demand");
/*      */ 
/*  214 */           if ((getDefinition().isTestBeforeUse()) && 
/*  215 */             (!testConnection(proxyConnection)))
/*      */           {
/*  217 */             throw new SQLException("Created a new connection but it failed its test");
/*      */           }
/*      */         }
/*      */         catch (SQLException e) {
/*  221 */           throw e;
/*      */         } catch (ProxoolException e) {
/*  223 */           this.log.debug("Couldn't get connection", e);
/*  224 */           throw new SQLException(e.toString());
/*      */         } catch (Throwable e) {
/*  226 */           this.log.error("Couldn't get connection", e);
/*  227 */           throw new SQLException(e.toString());
/*      */         }
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*  232 */       throw e;
/*      */     } catch (Throwable t) {
/*  234 */       this.log.error("Problem getting connection", t);
/*  235 */       throw new SQLException(t.toString());
/*      */     } finally {
/*  237 */       if (proxyConnection != null) {
/*  238 */         this.connectionsServedCount += 1L;
/*  239 */         proxyConnection.setRequester(requester);
/*      */       } else {
/*  241 */         this.connectionsRefusedCount += 1L;
/*  242 */         if (this.admin != null) {
/*  243 */           this.admin.connectionRefused();
/*      */         }
/*  245 */         this.timeOfLastRefusal = System.currentTimeMillis();
/*  246 */         setUpState(2);
/*      */       }
/*      */     }
/*      */ 
/*  250 */     if (proxyConnection == null) {
/*  251 */       throw new SQLException("Unknown reason for not getting connection. Sorry.");
/*      */     }
/*      */ 
/*  254 */     if ((this.log.isDebugEnabled()) && (getDefinition().isVerbose())) {
/*  255 */       this.log.debug(displayStatistics() + " - Connection #" + proxyConnection.getId() + " served");
/*      */     }
/*      */ 
/*  259 */     proxyConnection.open();
/*      */ 
/*  261 */     return ProxyFactory.getWrappedConnection(proxyConnection);
/*      */   }
/*      */ 
/*      */   private boolean testConnection(ProxyConnectionIF proxyConnection)
/*      */   {
/*  274 */     if (this.connectionValidator == null) {
/*  275 */       return true;
/*      */     }
/*      */ 
/*  279 */     boolean success = this.connectionValidator.validate(getDefinition(), proxyConnection.getConnection());
/*      */ 
/*  281 */     if (success) {
/*  282 */       if (LOG.isDebugEnabled())
/*  283 */         LOG.debug(displayStatistics() + " - Connection #" + proxyConnection.getId() + " tested: OK");
/*      */     }
/*      */     else
/*      */     {
/*  287 */       proxyConnection.setStatus(0);
/*  288 */       removeProxyConnection(proxyConnection, 3, "it didn't pass the validation", false, true);
/*      */     }
/*      */ 
/*  292 */     return success;
/*      */   }
/*      */ 
/*      */   protected boolean addProxyConnection(ProxyConnectionIF proxyConnection)
/*      */   {
/*  302 */     boolean added = false;
/*      */     try {
/*  304 */       acquireConnectionStatusWriteLock();
/*  305 */       if (proxyConnection.getDefinition() == getDefinition()) {
/*  306 */         this.proxyConnections.add(proxyConnection);
/*  307 */         this.connectionCountByState[proxyConnection.getStatus()] += 1;
/*  308 */         added = true;
/*      */       }
/*      */     } finally {
/*  311 */       releaseConnectionStatusWriteLock();
/*      */     }
/*  313 */     return added;
/*      */   }
/*      */ 
/*      */   protected static String getStatusDescription(int status) {
/*      */     try {
/*  318 */       return STATUS_DESCRIPTIONS[status]; } catch (ArrayIndexOutOfBoundsException e) {
/*      */     }
/*  320 */     return "Unknown status: " + status;
/*      */   }
/*      */ 
/*      */   protected void putConnection(ProxyConnectionIF proxyConnection)
/*      */   {
/*  330 */     if (this.admin != null) {
/*  331 */       long now = System.currentTimeMillis();
/*  332 */       long start = proxyConnection.getTimeLastStartActive();
/*  333 */       if (now - start < 0L) {
/*  334 */         this.log.warn("Future start time detected. #" + proxyConnection.getId() + " start = " + new Date(start) + " (" + (now - start) + " milliseconds)");
/*      */       }
/*  336 */       else if (now - start > 1000000L) {
/*  337 */         this.log.warn("Suspiciously long active time. #" + proxyConnection.getId() + " start = " + new Date(start));
/*      */       }
/*  339 */       this.admin.connectionReturned(now - start);
/*      */     }
/*      */ 
/*  343 */     if (proxyConnection.isMarkedForExpiry()) {
/*  344 */       if (proxyConnection.setStatus(2, 0)) {
/*  345 */         expireProxyConnection(proxyConnection, proxyConnection.getReasonCode(), proxyConnection.getReasonForMark(), false);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  350 */       if (getDefinition().isTestAfterUse())
/*      */       {
/*  352 */         testConnection(proxyConnection);
/*      */       }
/*      */ 
/*  356 */       if (!proxyConnection.setStatus(2, 1)) {
/*  357 */         if (proxyConnection.getStatus() == 1)
/*      */         {
/*  361 */           this.log.warn("Unable to close connection " + proxyConnection.getId() + " - I suspect that it has been closed already. Closing it more" + " than once is unwise and should be avoided.");
/*      */         }
/*      */         else
/*      */         {
/*  365 */           this.log.warn("Unable to set status of connection " + proxyConnection.getId() + " from " + getStatusDescription(2) + " to " + getStatusDescription(1) + " because it's state was " + getStatusDescription(proxyConnection.getStatus()));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  373 */     if ((this.log.isDebugEnabled()) && (getDefinition().isVerbose()))
/*  374 */       this.log.debug(displayStatistics() + " - Connection #" + proxyConnection.getId() + " returned (now " + getStatusDescription(proxyConnection.getStatus()) + ")");
/*      */   }
/*      */ 
/*      */   protected void throwConnection(ProxyConnectionIF proxyConnection, int reasonCode, String reason)
/*      */   {
/*  382 */     expireConnectionAsSoonAsPossible(proxyConnection, reasonCode, reason, true);
/*      */   }
/*      */ 
/*      */   private ProxyConnectionIF getProxyConnection(int i)
/*      */   {
/*  387 */     return (ProxyConnectionIF)this.proxyConnections.get(i);
/*      */   }
/*      */ 
/*      */   protected ProxyConnectionIF[] getProxyConnections()
/*      */   {
/*  395 */     return (ProxyConnectionIF[])(ProxyConnectionIF[])this.proxyConnections.toArray(new ProxyConnectionIF[this.proxyConnections.size()]);
/*      */   }
/*      */ 
/*      */   protected void removeProxyConnection(ProxyConnectionIF proxyConnection, int reasonCode, String reason, boolean forceExpiry, boolean triggerSweep)
/*      */   {
/*  409 */     if ((forceExpiry) || (proxyConnection.isNull()))
/*      */     {
/*  411 */       proxyConnection.setStatus(0);
/*      */       try
/*      */       {
/*  416 */         onDeath(proxyConnection.getConnection(), reasonCode);
/*      */       } catch (SQLException e) {
/*  418 */         this.log.error("Problem during onDeath (ignored)", e);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  423 */         proxyConnection.reallyClose();
/*      */       } catch (SQLException e) {
/*  425 */         this.log.error(e);
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  430 */         if (isConnectionPoolUp()) {
/*  431 */           acquireConnectionStatusWriteLock();
/*      */         }
/*  433 */         this.proxyConnections.remove(proxyConnection);
/*      */       } finally {
/*  435 */         if (isConnectionPoolUp()) {
/*  436 */           releaseConnectionStatusWriteLock();
/*      */         }
/*      */       }
/*      */ 
/*  440 */       if (this.log.isDebugEnabled()) {
/*  441 */         this.log.debug(displayStatistics() + " - #" + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " removed because " + reason + ".");
/*      */       }
/*      */ 
/*  445 */       if (triggerSweep)
/*  446 */         PrototyperController.triggerSweep(getDefinition().getAlias());
/*      */     }
/*      */     else
/*      */     {
/*  450 */       this.log.error(displayStatistics() + " - #" + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " was not removed because isNull() was false.");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void expireProxyConnection(ProxyConnectionIF proxyConnection, int reasonCode, String reason, boolean forceExpiry)
/*      */   {
/*  456 */     removeProxyConnection(proxyConnection, reasonCode, reason, forceExpiry, true);
/*      */   }
/*      */ 
/*      */   protected void shutdown(int delay, String finalizerName)
/*      */     throws Throwable
/*      */   {
/*  465 */     String alias = getDefinition().getAlias();
/*      */     try
/*      */     {
/*  470 */       acquirePrimaryWriteLock();
/*      */ 
/*  472 */       if (this.connectionPoolUp)
/*      */       {
/*  474 */         this.connectionPoolUp = false;
/*  475 */         long startFinalize = System.currentTimeMillis();
/*  476 */         this.shutdownThread = Thread.currentThread();
/*      */ 
/*  478 */         if (delay > 0) {
/*  479 */           this.log.info("Shutting down '" + alias + "' pool started at " + this.dateStarted + " - waiting for " + delay + " milliseconds for everything to stop.  [ " + finalizerName + "]");
/*      */         }
/*      */         else
/*      */         {
/*  484 */           this.log.info("Shutting down '" + alias + "' pool immediately [" + finalizerName + "]");
/*      */         }
/*      */ 
/*  489 */         boolean connectionClosedManually = false;
/*      */         try
/*      */         {
/*      */           try {
/*  493 */             HouseKeeperController.cancel(alias);
/*      */           } catch (ProxoolException e) {
/*  495 */             this.log.error("Shutdown couldn't cancel house keeper", e);
/*      */           }
/*      */ 
/*  499 */           if (this.admin != null) {
/*  500 */             this.admin.cancelAll();
/*      */           }
/*      */ 
/*  505 */           if (this.connectionCountByState[2] != 0) {
/*  506 */             long endWait = startFinalize + delay;
/*  507 */             LOG.info("Waiting until " + new Date(endWait) + " for all connections to become inactive (active count is " + this.connectionCountByState[2] + ").");
/*      */             while (true)
/*      */             {
/*  510 */               long timeout = endWait - System.currentTimeMillis();
/*  511 */               if (timeout > 0L) {
/*  512 */                 synchronized (Thread.currentThread()) {
/*      */                   try {
/*  514 */                     Thread.currentThread().wait(timeout);
/*      */                   } catch (InterruptedException e) {
/*  516 */                     this.log.debug("Interrupted whilst sleeping.");
/*      */                   }
/*      */                 }
/*      */               }
/*  520 */               int activeCount = this.connectionCountByState[2];
/*  521 */               if (activeCount == 0) {
/*      */                 break;
/*      */               }
/*  524 */               if (System.currentTimeMillis() < endWait) {
/*  525 */                 LOG.info("Still waiting for active count to reach zero (currently " + activeCount + ").");
/*      */               }
/*      */               else {
/*  528 */                 LOG.warn("Shutdown waited for " + (System.currentTimeMillis() - startFinalize) + " milliseconds for all " + "the connections to become inactive but the active count is still " + activeCount + ". Shutting down anyway.");
/*      */ 
/*  532 */                 break;
/*      */               }
/*  534 */               Thread.sleep(100L);
/*      */             }
/*      */           }
/*      */ 
/*  538 */           this.prototyper.cancel();
/*      */ 
/*  541 */           for (int i = this.proxyConnections.size() - 1; i >= 0; i--) {
/*  542 */             long id = getProxyConnection(i).getId();
/*      */             try {
/*  544 */               connectionClosedManually = true;
/*  545 */               removeProxyConnection(getProxyConnection(i), 4, "of shutdown", true, false);
/*  546 */               if (this.log.isDebugEnabled())
/*  547 */                 this.log.debug("Connection #" + id + " closed");
/*      */             }
/*      */             catch (Throwable t) {
/*  550 */               if (this.log.isDebugEnabled()) {
/*  551 */                 this.log.debug("Problem closing connection #" + id, t);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*  558 */           this.log.error("Unknown problem finalizing pool", t);
/*      */         }
/*      */         finally {
/*  561 */           ConnectionPoolManager.getInstance().removeConnectionPool(alias);
/*      */ 
/*  563 */           if (this.log.isDebugEnabled()) {
/*  564 */             this.log.info("'" + alias + "' pool has been closed down by " + finalizerName + " in " + (System.currentTimeMillis() - startFinalize) + " milliseconds.");
/*      */ 
/*  566 */             if (!connectionClosedManually) {
/*  567 */               this.log.debug("No connections required manual removal.");
/*      */             }
/*      */           }
/*  570 */           super.finalize();
/*      */         }
/*      */       }
/*  573 */       else if (this.log.isDebugEnabled()) {
/*  574 */         this.log.debug("Ignoring duplicate attempt to shutdown '" + alias + "' pool by " + finalizerName);
/*      */       }
/*      */     }
/*      */     catch (Throwable t) {
/*  578 */       this.log.error(finalizerName + " couldn't shutdown pool", t);
/*      */     } finally {
/*  580 */       releasePrimaryWriteLock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getAvailableConnectionCount()
/*      */   {
/*  591 */     return this.connectionCountByState[1];
/*      */   }
/*      */ 
/*      */   public int getActiveConnectionCount()
/*      */   {
/*  601 */     return this.connectionCountByState[2];
/*      */   }
/*      */ 
/*      */   public int getOfflineConnectionCount()
/*      */   {
/*  611 */     return this.connectionCountByState[3];
/*      */   }
/*      */ 
/*      */   protected String displayStatistics()
/*      */   {
/*  616 */     if (!loggedLegend) {
/*  617 */       this.log.info("Proxool statistics legend: \"s - r  (a/t/o)\" > s=served, r=refused (only shown if non-zero), a=active, t=total, o=offline (being tested)");
/*  618 */       loggedLegend = true;
/*      */     }
/*      */ 
/*  621 */     StringBuffer statistics = new StringBuffer();
/*  622 */     statistics.append(FormatHelper.formatBigNumber(getConnectionsServedCount()));
/*      */ 
/*  624 */     if (getConnectionsRefusedCount() > 0L) {
/*  625 */       statistics.append(" -");
/*  626 */       statistics.append(FormatHelper.formatBigNumber(getConnectionsRefusedCount()));
/*      */     }
/*      */ 
/*  629 */     statistics.append(" (");
/*  630 */     statistics.append(FormatHelper.formatSmallNumber(getActiveConnectionCount()));
/*  631 */     statistics.append("/");
/*  632 */     statistics.append(FormatHelper.formatSmallNumber(getAvailableConnectionCount() + getActiveConnectionCount()));
/*  633 */     statistics.append("/");
/*  634 */     statistics.append(FormatHelper.formatSmallNumber(getOfflineConnectionCount()));
/*  635 */     statistics.append(")");
/*      */ 
/*  647 */     return statistics.toString();
/*      */   }
/*      */ 
/*      */   protected void expireAllConnections(int reasonCode, String reason, boolean merciful)
/*      */   {
/*  655 */     Set pcs = new HashSet();
/*  656 */     for (int i = this.proxyConnections.size() - 1; i >= 0; i--) {
/*  657 */       pcs.add(this.proxyConnections.get(i));
/*      */     }
/*      */ 
/*  660 */     Iterator i = pcs.iterator();
/*  661 */     while (i.hasNext()) {
/*  662 */       ProxyConnectionIF pc = (ProxyConnectionIF)i.next();
/*  663 */       expireConnectionAsSoonAsPossible(pc, reasonCode, reason, merciful);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void expireConnectionAsSoonAsPossible(ProxyConnectionIF proxyConnection, int reasonCode, String reason, boolean merciful) {
/*  668 */     if (proxyConnection.setStatus(1, 3)) {
/*  669 */       if (proxyConnection.setStatus(3, 0))
/*      */       {
/*  671 */         expireProxyConnection(proxyConnection, reasonCode, reason, false);
/*      */       }
/*      */ 
/*      */     }
/*  676 */     else if (merciful)
/*      */     {
/*  680 */       proxyConnection.markForExpiry(reason);
/*  681 */       if (this.log.isDebugEnabled()) {
/*  682 */         this.log.debug(displayStatistics() + " - #" + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " marked for expiry.");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  688 */       expireProxyConnection(proxyConnection, reasonCode, reason, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void registerRemovedConnection(int status)
/*      */   {
/*  695 */     this.prototyper.connectionRemoved();
/*  696 */     this.connectionCountByState[status] -= 1;
/*      */   }
/*      */ 
/*      */   protected void changeStatus(int oldStatus, int newStatus)
/*      */   {
/*  707 */     this.connectionCountByState[oldStatus] -= 1;
/*  708 */     this.connectionCountByState[newStatus] += 1;
/*      */ 
/*  712 */     if ((this.shutdownThread != null) && (this.connectionCountByState[2] == 0))
/*  713 */       synchronized (this.shutdownThread) {
/*  714 */         this.shutdownThread.notify();
/*      */       }
/*      */   }
/*      */ 
/*      */   public long getConnectionsServedCount()
/*      */   {
/*  721 */     return this.connectionsServedCount;
/*      */   }
/*      */ 
/*      */   public long getConnectionsRefusedCount() {
/*  725 */     return this.connectionsRefusedCount;
/*      */   }
/*      */ 
/*      */   protected ConnectionPoolDefinition getDefinition() {
/*  729 */     return this.definition;
/*      */   }
/*      */ 
/*      */   protected synchronized void setDefinition(ConnectionPoolDefinition definition)
/*      */     throws ProxoolException
/*      */   {
/*  737 */     this.definition = definition;
/*      */     try
/*      */     {
/*  740 */       Class.forName(definition.getDriver());
/*      */     } catch (ClassNotFoundException e) {
/*  742 */       this.log.error("Couldn't load class " + definition.getDriver(), e);
/*  743 */       throw new ProxoolException("Couldn't load class " + definition.getDriver());
/*      */     } catch (NullPointerException e) {
/*  745 */       this.log.error("Definition did not contain driver", e);
/*  746 */       throw new ProxoolException("Definition did not contain driver");
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setStateListener(StateListenerIF stateListener)
/*      */   {
/*  755 */     addStateListener(stateListener);
/*      */   }
/*      */ 
/*      */   public void addStateListener(StateListenerIF stateListener) {
/*  759 */     this.compositeStateListener.addListener(stateListener);
/*      */   }
/*      */ 
/*      */   public boolean removeStateListener(StateListenerIF stateListener) {
/*  763 */     return this.compositeStateListener.removeListener(stateListener);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setConnectionListener(ConnectionListenerIF connectionListener)
/*      */   {
/*  770 */     addConnectionListener(connectionListener);
/*      */   }
/*      */ 
/*      */   public void addConnectionListener(ConnectionListenerIF connectionListener) {
/*  774 */     this.compositeConnectionListener.addListener(connectionListener);
/*      */   }
/*      */ 
/*      */   public boolean removeConnectionListener(ConnectionListenerIF connectionListener) {
/*  778 */     return this.compositeConnectionListener.removeListener(connectionListener);
/*      */   }
/*      */ 
/*      */   protected void onBirth(Connection connection) throws SQLException
/*      */   {
/*  783 */     this.compositeConnectionListener.onBirth(connection);
/*      */   }
/*      */ 
/*      */   protected void onDeath(Connection connection, int reasonCode) throws SQLException
/*      */   {
/*  788 */     this.compositeConnectionListener.onDeath(connection, reasonCode);
/*      */   }
/*      */ 
/*      */   protected void onExecute(String command, long elapsedTime, Exception exception) throws SQLException
/*      */   {
/*  793 */     if (exception == null)
/*  794 */       this.compositeConnectionListener.onExecute(command, elapsedTime);
/*      */     else
/*  796 */       this.compositeConnectionListener.onFail(command, exception);
/*      */   }
/*      */ 
/*      */   protected boolean isConnectionListenedTo()
/*      */   {
/*  805 */     return !this.compositeConnectionListener.isEmpty();
/*      */   }
/*      */ 
/*      */   public String toString() {
/*  809 */     return getDefinition().toString();
/*      */   }
/*      */ 
/*      */   public int getUpState() {
/*  813 */     return this.upState;
/*      */   }
/*      */ 
/*      */   public void setUpState(int upState)
/*      */   {
/*  818 */     if (this.upState != upState) {
/*  819 */       this.compositeStateListener.upStateChanged(upState);
/*  820 */       this.upState = upState;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Collection getConnectionInfos() {
/*  825 */     Collection cis = null;
/*  826 */     cis = new TreeSet();
/*  827 */     Iterator i = this.proxyConnections.iterator();
/*  828 */     while (i.hasNext()) {
/*  829 */       ConnectionInfoIF connectionInfo = (ConnectionInfoIF)i.next();
/*  830 */       ConnectionInfo ci = new ConnectionInfo();
/*  831 */       ci.setAge(connectionInfo.getAge());
/*  832 */       ci.setBirthDate(connectionInfo.getBirthDate());
/*  833 */       ci.setId(connectionInfo.getId());
/*  834 */       ci.setMark(connectionInfo.getMark());
/*  835 */       ci.setRequester(connectionInfo.getRequester());
/*  836 */       ci.setStatus(connectionInfo.getStatus());
/*  837 */       ci.setTimeLastStartActive(connectionInfo.getTimeLastStartActive());
/*  838 */       ci.setTimeLastStopActive(connectionInfo.getTimeLastStopActive());
/*  839 */       ci.setDelegateUrl(connectionInfo.getDelegateUrl());
/*  840 */       ci.setProxyHashcode(connectionInfo.getProxyHashcode());
/*  841 */       ci.setDelegateHashcode(connectionInfo.getDelegateHashcode());
/*  842 */       String[] sqlCalls = connectionInfo.getSqlCalls();
/*  843 */       for (int j = 0; j < sqlCalls.length; j++) {
/*  844 */         ci.addSqlCall(sqlCalls[j]);
/*      */       }
/*  846 */       cis.add(ci);
/*      */     }
/*  848 */     return cis;
/*      */   }
/*      */ 
/*      */   public boolean expireConnection(long id, boolean forceExpiry)
/*      */   {
/*  858 */     boolean success = false;
/*  859 */     ProxyConnection proxyConnection = null;
/*      */ 
/*  862 */     for (int connectionsTried = 0; connectionsTried < this.proxyConnections.size(); connectionsTried++)
/*      */     {
/*      */       try
/*      */       {
/*  866 */         proxyConnection = (ProxyConnection)this.proxyConnections.get(this.nextAvailableConnection);
/*      */       } catch (IndexOutOfBoundsException e) {
/*  868 */         this.nextAvailableConnection = 0;
/*  869 */         proxyConnection = (ProxyConnection)this.proxyConnections.get(this.nextAvailableConnection);
/*      */       }
/*      */ 
/*  872 */       if (proxyConnection.getId() == id)
/*      */       {
/*  874 */         proxyConnection.setStatus(1, 3);
/*  875 */         proxyConnection.setStatus(3, 0);
/*  876 */         removeProxyConnection(proxyConnection, 2, "it was manually killed", forceExpiry, true);
/*  877 */         success = true;
/*  878 */         break;
/*      */       }
/*      */ 
/*  881 */       this.nextAvailableConnection += 1;
/*      */     }
/*      */ 
/*  884 */     if ((!success) && 
/*  885 */       (this.log.isDebugEnabled())) {
/*  886 */       this.log.debug(displayStatistics() + " - couldn't find " + FormatHelper.formatMediumNumber(proxyConnection.getId()) + " and I've just been asked to expire it");
/*      */     }
/*      */ 
/*  891 */     return success;
/*      */   }
/*      */ 
/*      */   public Log getLog() {
/*  895 */     return this.log;
/*      */   }
/*      */ 
/*      */   protected void initialiseConnectionResetter(Connection connection)
/*      */   {
/*  903 */     this.connectionResetter.initialise(connection);
/*      */   }
/*      */ 
/*      */   protected boolean resetConnection(Connection connection, String id)
/*      */     throws SQLException
/*      */   {
/*  915 */     if (connection.isClosed()) {
/*  916 */       return false;
/*      */     }
/*  918 */     return this.connectionResetter.reset(connection, id);
/*      */   }
/*      */ 
/*      */   public Date getDateStarted()
/*      */   {
/*  926 */     return this.dateStarted;
/*      */   }
/*      */ 
/*      */   protected Admin getAdmin()
/*      */   {
/*  934 */     return this.admin;
/*      */   }
/*      */ 
/*      */   protected boolean isLocked() {
/*  938 */     return this.locked;
/*      */   }
/*      */ 
/*      */   protected void lock() {
/*  942 */     this.locked = true;
/*      */   }
/*      */ 
/*      */   protected void unlock() {
/*  946 */     this.locked = false;
/*      */   }
/*      */ 
/*      */   protected void acquirePrimaryReadLock()
/*      */     throws InterruptedException
/*      */   {
/*  962 */     this.primaryReadWriteLock.readLock().acquire();
/*      */   }
/*      */ 
/*      */   protected void releasePrimaryReadLock()
/*      */   {
/*  983 */     this.primaryReadWriteLock.readLock().release();
/*      */   }
/*      */ 
/*      */   protected void acquirePrimaryWriteLock()
/*      */     throws InterruptedException
/*      */   {
/* 1003 */     this.primaryReadWriteLock.writeLock().acquire();
/*      */   }
/*      */ 
/*      */   protected void releasePrimaryWriteLock()
/*      */   {
/* 1029 */     this.primaryReadWriteLock.writeLock().release();
/*      */   }
/*      */ 
/*      */   protected boolean isConnectionPoolUp()
/*      */   {
/* 1044 */     return this.connectionPoolUp;
/*      */   }
/*      */ 
/*      */   protected long getTimeOfLastRefusal()
/*      */   {
/* 1056 */     return this.timeOfLastRefusal;
/*      */   }
/*      */ 
/*      */   protected void acquireConnectionStatusWriteLock()
/*      */   {
/*      */     try
/*      */     {
/* 1066 */       this.connectionStatusReadWriteLock.writeLock().acquire();
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/* 1073 */       this.log.error("Couldn't acquire connectionStatus write lock", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void releaseConnectionStatusWriteLock() {
/* 1078 */     this.connectionStatusReadWriteLock.writeLock().release();
/*      */   }
/*      */ 
/*      */   protected void acquireConnectionStatusReadLock()
/*      */   {
/*      */     try
/*      */     {
/* 1088 */       this.connectionStatusReadWriteLock.readLock().acquire();
/*      */     } catch (InterruptedException e) {
/* 1090 */       this.log.error("Couldn't acquire connectionStatus read lock", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean attemptConnectionStatusReadLock(long msecs) {
/*      */     try {
/* 1096 */       return this.connectionStatusReadWriteLock.readLock().attempt(msecs);
/*      */     } catch (InterruptedException e) {
/* 1098 */       this.log.error("Couldn't acquire connectionStatus read lock", e);
/* 1099 */     }return false;
/*      */   }
/*      */ 
/*      */   protected void releaseConnectionStatusReadLock()
/*      */   {
/* 1104 */     this.connectionStatusReadWriteLock.readLock().release();
/*      */   }
/*      */ 
/*      */   protected Prototyper getPrototyper()
/*      */   {
/* 1109 */     return this.prototyper;
/*      */   }
/*      */ 
/*      */   public long getConnectionCount() {
/* 1113 */     return getPrototyper().getConnectionCount();
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionPool
 * JD-Core Version:    0.6.0
 */
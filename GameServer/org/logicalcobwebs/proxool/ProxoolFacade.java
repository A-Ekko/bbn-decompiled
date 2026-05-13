/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.Connection;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.admin.Admin;
/*     */ import org.logicalcobwebs.proxool.admin.SnapshotIF;
/*     */ import org.logicalcobwebs.proxool.admin.StatisticsIF;
/*     */ import org.logicalcobwebs.proxool.admin.StatisticsListenerIF;
/*     */ 
/*     */ public class ProxoolFacade
/*     */ {
/*  41 */   private static final Log LOG = LogFactory.getLog(ProxoolFacade.class);
/*     */ 
/*  43 */   private static Map configurators = new HashMap();
/*     */ 
/*  45 */   private static CompositeProxoolListener compositeProxoolListener = new CompositeProxoolListener();
/*     */ 
/*  47 */   private static boolean versionLogged = false;
/*     */   private static Thread shutdownHook;
/*  59 */   private static boolean shutdownHookEnabled = true;
/*     */   private static final boolean MERCIFUL = true;
/*     */ 
/*     */   public static synchronized String registerConnectionPool(String url, Properties info)
/*     */     throws ProxoolException
/*     */   {
/*  69 */     return registerConnectionPool(url, info, true);
/*     */   }
/*     */ 
/*     */   protected static synchronized String registerConnectionPool(String url, Properties info, boolean explicitRegister)
/*     */     throws ProxoolException
/*     */   {
/*  82 */     String alias = getAlias(url);
/*     */ 
/*  84 */     if (!versionLogged) {
/*  85 */       versionLogged = true;
/*  86 */       LOG.info("Proxool " + Version.getVersion());
/*     */     }
/*     */     try
/*     */     {
/*  90 */       Class.forName(ProxoolDriver.class.getName());
/*     */     } catch (ClassNotFoundException e) {
/*  92 */       LOG.error("Couldn't load " + ProxoolDriver.class.getName());
/*     */     }
/*     */ 
/*  95 */     if (!ConnectionPoolManager.getInstance().isPoolExists(alias)) {
/*  96 */       ConnectionPoolDefinition cpd = new ConnectionPoolDefinition(url, info, explicitRegister);
/*  97 */       registerConnectionPool(cpd);
/*     */     } else {
/*  99 */       throw new ProxoolException("Attempt to register duplicate pool called '" + alias + "'");
/*     */     }
/*     */ 
/* 102 */     return alias;
/*     */   }
/*     */ 
/*     */   protected static synchronized void registerConnectionPool(ConnectionPoolDefinition connectionPoolDefinition) throws ProxoolException
/*     */   {
/* 107 */     if (!ConnectionPoolManager.getInstance().isPoolExists(connectionPoolDefinition.getAlias())) {
/* 108 */       Properties jndiProperties = extractJndiProperties(connectionPoolDefinition);
/* 109 */       ConnectionPool connectionPool = ConnectionPoolManager.getInstance().createConnectionPool(connectionPoolDefinition);
/* 110 */       connectionPool.start();
/* 111 */       compositeProxoolListener.onRegistration(connectionPoolDefinition, connectionPoolDefinition.getCompleteInfo());
/* 112 */       if (isConfiguredForJMX(connectionPoolDefinition.getCompleteInfo())) {
/* 113 */         registerForJmx(connectionPoolDefinition.getAlias(), connectionPoolDefinition.getCompleteInfo());
/*     */       }
/* 115 */       if (jndiProperties != null)
/* 116 */         registerDataSource(connectionPoolDefinition.getAlias(), jndiProperties);
/*     */     }
/*     */     else {
/* 119 */       LOG.debug("Ignoring duplicate attempt to register " + connectionPoolDefinition.getAlias() + " pool");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void registerConnectionPool(String url)
/*     */     throws ProxoolException
/*     */   {
/* 128 */     registerConnectionPool(url, null);
/*     */   }
/*     */ 
/*     */   protected static String getAlias(String url)
/*     */     throws ProxoolException
/*     */   {
/* 141 */     String alias = null;
/* 142 */     String prefix = "proxool.";
/*     */ 
/* 145 */     if (url.startsWith("proxool."))
/*     */     {
/* 148 */       int endOfPrefix = url.indexOf(":");
/*     */ 
/* 150 */       if (endOfPrefix > -1)
/* 151 */         alias = url.substring("proxool.".length(), endOfPrefix);
/*     */       else {
/* 153 */         alias = url.substring("proxool.".length());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 158 */     if ((alias == null) || (alias.length() == 0)) {
/* 159 */       throw new ProxoolException("The URL '" + url + "' is not in the correct form. It should be: 'proxool.alias:driver:url'");
/*     */     }
/*     */ 
/* 162 */     return alias;
/*     */   }
/*     */ 
/*     */   private static void removeConnectionPool(String finalizer, ConnectionPool connectionPool, int delay)
/*     */   {
/* 172 */     String alias = connectionPool.getDefinition().getAlias();
/* 173 */     if (connectionPool != null) {
/*     */       try {
/* 175 */         compositeProxoolListener.onShutdown(alias);
/* 176 */         connectionPool.shutdown(delay, finalizer);
/*     */       } catch (Throwable t) {
/* 178 */         LOG.error("Problem trying to shutdown '" + alias + "' connection pool", t);
/*     */       }
/*     */     }
/* 181 */     connectionPool = null;
/*     */   }
/*     */ 
/*     */   public static void removeConnectionPool(String alias, int delay)
/*     */     throws ProxoolException
/*     */   {
/* 191 */     removeConnectionPool(Thread.currentThread().getName(), ConnectionPoolManager.getInstance().getConnectionPool(alias), delay);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void removeAllConnectionPools(int delay)
/*     */   {
/* 200 */     shutdown(Thread.currentThread().getName(), delay);
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 209 */     shutdown(Thread.currentThread().getName(), 0);
/*     */   }
/*     */ 
/*     */   public static void shutdown(int delay)
/*     */   {
/* 218 */     shutdown(Thread.currentThread().getName(), delay);
/*     */   }
/*     */ 
/*     */   protected static void shutdown(String finalizer, int delay)
/*     */   {
/* 228 */     ConnectionPool[] cps = ConnectionPoolManager.getInstance().getConnectionPools();
/* 229 */     for (int i = 0; i < cps.length; i++) {
/* 230 */       removeConnectionPool(finalizer, cps[i], delay);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 235 */       if (shutdownHook != null)
/* 236 */         ShutdownHook.remove(shutdownHook);
/*     */     }
/*     */     catch (Throwable t) {
/* 239 */       if (LOG.isDebugEnabled()) {
/* 240 */         LOG.debug("Unanticipated error during removal of ShutdownHook. Ignoring it.", t);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 245 */     PrototyperController.shutdown();
/* 246 */     HouseKeeperController.shutdown();
/*     */   }
/*     */ 
/*     */   public static void disableShutdownHook()
/*     */   {
/* 255 */     shutdownHookEnabled = false;
/*     */   }
/*     */ 
/*     */   public static void enableShutdownHook()
/*     */   {
/* 264 */     shutdownHookEnabled = true;
/*     */   }
/*     */ 
/*     */   public static boolean isShutdownHookEnabled()
/*     */   {
/* 274 */     return shutdownHookEnabled;
/*     */   }
/*     */ 
/*     */   public static void removeConnectionPool(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 284 */     removeConnectionPool(alias, 0);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static ConnectionPoolStatisticsIF getConnectionPoolStatistics(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 295 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static String getConnectionPoolStatisticsDump(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 307 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias).displayStatistics();
/*     */   }
/*     */ 
/*     */   public static ConnectionPoolDefinitionIF getConnectionPoolDefinition(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 316 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias).getDefinition();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Collection getConnectionInfos(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 328 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias).getConnectionInfos();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void killAllConnections(String alias, boolean merciful)
/*     */     throws ProxoolException
/*     */   {
/* 341 */     killAllConnections(alias, "of thread " + Thread.currentThread().getName(), merciful);
/*     */   }
/*     */ 
/*     */   public static void killAllConnections(String alias, String reason, boolean merciful)
/*     */     throws ProxoolException
/*     */   {
/* 353 */     ConnectionPoolManager.getInstance().getConnectionPool(alias).expireAllConnections(2, reason, merciful);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void killAllConnections(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 364 */     killAllConnections(alias, "of thread " + Thread.currentThread().getName(), true);
/*     */   }
/*     */ 
/*     */   public static void killAllConnections(String alias, String reason)
/*     */     throws ProxoolException
/*     */   {
/* 374 */     killAllConnections(alias, reason, true);
/*     */   }
/*     */ 
/*     */   public static boolean killConnecton(String alias, long id, boolean merciful)
/*     */     throws ProxoolException
/*     */   {
/* 387 */     boolean forceExpiry = !merciful;
/* 388 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias).expireConnection(id, forceExpiry);
/*     */   }
/*     */ 
/*     */   public static boolean killConnecton(Connection connection, boolean merciful)
/*     */     throws ProxoolException
/*     */   {
/* 399 */     WrappedConnection wrappedConnection = ProxyFactory.getWrappedConnection(connection);
/* 400 */     if (wrappedConnection != null) {
/* 401 */       long id = wrappedConnection.getId();
/* 402 */       String alias = wrappedConnection.getAlias();
/* 403 */       return killConnecton(alias, id, merciful);
/*     */     }
/* 405 */     throw new ProxoolException("Attempt to kill unrecognised exception " + connection);
/*     */   }
/*     */ 
/*     */   public static void addProxoolListener(ProxoolListenerIF proxoolListener)
/*     */   {
/* 414 */     compositeProxoolListener.addListener(proxoolListener);
/*     */   }
/*     */ 
/*     */   public static boolean removeProxoolListener(ProxoolListenerIF proxoolListener)
/*     */   {
/* 423 */     return compositeProxoolListener.removeListener(proxoolListener);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void setStateListener(String alias, StateListenerIF stateListener) throws ProxoolException
/*     */   {
/* 430 */     addStateListener(alias, stateListener);
/*     */   }
/*     */ 
/*     */   public static void addStateListener(String alias, StateListenerIF stateListener)
/*     */     throws ProxoolException
/*     */   {
/* 440 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/* 441 */     cp.addStateListener(stateListener);
/*     */   }
/*     */ 
/*     */   public static boolean removeStateListener(String alias, StateListenerIF stateListener)
/*     */     throws ProxoolException
/*     */   {
/* 452 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/* 453 */     return cp.removeStateListener(stateListener);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void setConnectionListener(String alias, ConnectionListenerIF connectionListener) throws ProxoolException
/*     */   {
/* 460 */     addConnectionListener(alias, connectionListener);
/*     */   }
/*     */ 
/*     */   public static void addConnectionListener(String alias, ConnectionListenerIF connectionListener)
/*     */     throws ProxoolException
/*     */   {
/* 470 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/* 471 */     cp.addConnectionListener(connectionListener);
/*     */   }
/*     */ 
/*     */   public static boolean removeConnectionListener(String alias, ConnectionListenerIF connectionListener)
/*     */     throws ProxoolException
/*     */   {
/* 482 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/* 483 */     return cp.removeConnectionListener(connectionListener);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static void setConfigurationListener(String alias, ConfigurationListenerIF configurationListener) throws ProxoolException
/*     */   {
/* 490 */     addConfigurationListener(alias, configurationListener);
/*     */   }
/*     */ 
/*     */   public static void addConfigurationListener(String alias, ConfigurationListenerIF configurationListener)
/*     */     throws ProxoolException
/*     */   {
/* 500 */     if (ConnectionPoolManager.getInstance().isPoolExists(alias)) {
/* 501 */       CompositeConfigurationListener compositeConfigurationListener = (CompositeConfigurationListener)configurators.get(alias);
/*     */ 
/* 503 */       if (compositeConfigurationListener == null) {
/* 504 */         compositeConfigurationListener = new CompositeConfigurationListener();
/* 505 */         configurators.put(alias, compositeConfigurationListener);
/*     */       }
/* 507 */       compositeConfigurationListener.addListener(configurationListener);
/*     */     } else {
/* 509 */       throw new ProxoolException(ConnectionPoolManager.getInstance().getKnownPools(alias));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static void definitionUpdated(String alias, ConnectionPoolDefinitionIF connectionPoolDefinition, Properties completeInfo, Properties changedInfo)
/*     */   {
/* 523 */     CompositeConfigurationListener ccl = (CompositeConfigurationListener)configurators.get(alias);
/* 524 */     if (ccl != null)
/* 525 */       ccl.definitionUpdated(connectionPoolDefinition, completeInfo, changedInfo);
/*     */   }
/*     */ 
/*     */   public static boolean removeConfigurationListener(String alias, ConfigurationListenerIF configurationListener)
/*     */     throws ProxoolException
/*     */   {
/* 538 */     boolean removed = false;
/* 539 */     if (ConnectionPoolManager.getInstance().isPoolExists(alias)) {
/* 540 */       CompositeConfigurationListener compositeConfigurationListener = (CompositeConfigurationListener)configurators.get(alias);
/*     */ 
/* 542 */       if (compositeConfigurationListener != null)
/* 543 */         removed = compositeConfigurationListener.removeListener(configurationListener);
/*     */     }
/*     */     else {
/* 546 */       throw new ProxoolException(ConnectionPoolManager.getInstance().getKnownPools(alias));
/*     */     }
/* 548 */     return removed;
/*     */   }
/*     */ 
/*     */   public static void redefineConnectionPool(String url, Properties info)
/*     */     throws ProxoolException
/*     */   {
/* 566 */     String alias = getAlias(url);
/* 567 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*     */     try
/*     */     {
/* 570 */       ConnectionPoolDefinition cpd = (ConnectionPoolDefinition)cp.getDefinition().clone();
/* 571 */       cpd.redefine(url, info);
/* 572 */       cp.setDefinition(cpd);
/*     */     } catch (CloneNotSupportedException e) {
/* 574 */       throw new ProxoolException("Funny, why couldn't we clone a definition?", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void updateConnectionPool(String url, Properties info)
/*     */     throws ProxoolException
/*     */   {
/* 588 */     String alias = getAlias(url);
/* 589 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*     */     try
/*     */     {
/* 592 */       ConnectionPoolDefinition cpd = (ConnectionPoolDefinition)cp.getDefinition().clone();
/* 593 */       cpd.update(url, info);
/* 594 */       cp.setDefinition(cpd);
/*     */     } catch (CloneNotSupportedException e) {
/* 596 */       throw new ProxoolException("Funny, why couldn't we clone a definition?", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void finalize() throws Throwable {
/* 601 */     super.finalize();
/* 602 */     LOG.debug("Finalising");
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Statement getDelegateStatement(Statement statement)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/* 612 */       return ProxyFactory.getDelegateStatement(statement); } catch (IllegalArgumentException e) {
/*     */     }
/* 614 */     throw new ProxoolException("Statement argument is not one provided by Proxool (it's a " + statement.getClass() + ")");
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Connection getDelegateConnection(Connection connection)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/* 625 */       return ProxyFactory.getDelegateConnection(connection); } catch (IllegalArgumentException e) {
/*     */     }
/* 627 */     throw new ProxoolException("Connection argument is not one provided by Proxool (it's a " + connection.getClass() + ")");
/*     */   }
/*     */ 
/*     */   public static long getId(Connection connection)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/* 639 */       return ProxyFactory.getWrappedConnection(connection).getId();
/*     */     } catch (NullPointerException e) {
/* 641 */       throw new ProxoolException("Connection argument is not one provided by Proxool (it's a " + connection.getClass() + ")"); } catch (IllegalArgumentException e) {
/*     */     }
/* 643 */     throw new ProxoolException("Connection argument is not one provided by Proxool (it's a " + connection.getClass() + ")");
/*     */   }
/*     */ 
/*     */   public static String getAlias(Connection connection)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/* 655 */       return ProxyFactory.getWrappedConnection(connection).getAlias();
/*     */     } catch (NullPointerException e) {
/* 657 */       throw new ProxoolException("Connection argument is not one provided by Proxool (it's a " + connection.getClass() + ")"); } catch (IllegalArgumentException e) {
/*     */     }
/* 659 */     throw new ProxoolException("Connection argument is not one provided by Proxool (it's a " + connection.getClass() + ")");
/*     */   }
/*     */ 
/*     */   public static String[] getAliases()
/*     */   {
/* 669 */     return ConnectionPoolManager.getInstance().getConnectionPoolNames();
/*     */   }
/*     */ 
/*     */   public static StatisticsIF getStatistics(String alias, String token)
/*     */     throws ProxoolException
/*     */   {
/* 680 */     return ConnectionPoolManager.getInstance().getConnectionPool(alias).getAdmin().getStatistics(token);
/*     */   }
/*     */ 
/*     */   public static StatisticsIF[] getStatistics(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 690 */     Admin monitor = ConnectionPoolManager.getInstance().getConnectionPool(alias).getAdmin();
/* 691 */     if (monitor != null) {
/* 692 */       return monitor.getStatistics();
/*     */     }
/* 694 */     return new StatisticsIF[0];
/*     */   }
/*     */ 
/*     */   public static void addStatisticsListener(String alias, StatisticsListenerIF statisticsListener)
/*     */     throws ProxoolException
/*     */   {
/* 705 */     Admin monitor = ConnectionPoolManager.getInstance().getConnectionPool(alias).getAdmin();
/* 706 */     if (monitor != null)
/* 707 */       monitor.addStatisticsListener(statisticsListener);
/*     */     else
/* 709 */       throw new ProxoolException("Statistics are switched off, your can't add a listener");
/*     */   }
/*     */ 
/*     */   public static SnapshotIF getSnapshot(String alias, boolean detail)
/*     */     throws ProxoolException
/*     */   {
/* 725 */     SnapshotIF snapshot = null;
/* 726 */     ConnectionPool cp = ConnectionPoolManager.getInstance().getConnectionPool(alias);
/*     */ 
/* 728 */     if (detail) {
/*     */       try
/*     */       {
/* 731 */         long start = System.currentTimeMillis();
/* 732 */         if (cp.attemptConnectionStatusReadLock(10000L))
/* 733 */           snapshot = Admin.getSnapshot(cp, cp.getDefinition(), cp.getConnectionInfos());
/*     */         else
/* 735 */           LOG.warn("Give up waiting for detailed snapshot after " + (System.currentTimeMillis() - start) + " milliseconds. Serving standard snapshot instead.");
/*     */       }
/*     */       finally {
/* 738 */         cp.releaseConnectionStatusReadLock();
/*     */       }
/*     */     }
/* 741 */     if (snapshot == null) {
/* 742 */       snapshot = Admin.getSnapshot(cp, cp.getDefinition(), null);
/*     */     }
/*     */ 
/* 745 */     return snapshot;
/*     */   }
/*     */ 
/*     */   public static SnapshotIF getSnapshot(String alias)
/*     */     throws ProxoolException
/*     */   {
/* 754 */     return getSnapshot(alias, false);
/*     */   }
/*     */ 
/*     */   private static boolean registerForJmx(String alias, Properties properties)
/*     */   {
/* 760 */     boolean success = false;
/*     */     try {
/* 762 */       Class jmxHelperClass = Class.forName("org.logicalcobwebs.proxool.admin.jmx.ProxoolJMXHelper");
/* 763 */       Method registerMethod = jmxHelperClass.getDeclaredMethod("registerPool", new Class[] { String.class, Properties.class });
/* 764 */       registerMethod.invoke(null, new Object[] { alias, properties });
/* 765 */       success = true;
/*     */     } catch (Exception e) {
/* 767 */       LOG.error("JMX registration of " + alias + " pool failed.", e);
/*     */     }
/* 769 */     return success;
/*     */   }
/*     */ 
/*     */   private static boolean registerDataSource(String alias, Properties jndiProperties)
/*     */   {
/* 775 */     boolean success = false;
/*     */     try {
/* 777 */       Class jndiHelperClass = Class.forName("org.logicalcobwebs.proxool.admin.jndi.ProxoolJNDIHelper");
/* 778 */       Method registerMethod = jndiHelperClass.getDeclaredMethod("registerDatasource", new Class[] { String.class, Properties.class });
/*     */ 
/* 780 */       registerMethod.invoke(null, new Object[] { alias, jndiProperties });
/* 781 */       success = true;
/*     */     } catch (Exception e) {
/* 783 */       LOG.error("JNDI DataSource binding of " + alias + " pool failed.", e);
/*     */     }
/* 785 */     return success;
/*     */   }
/*     */ 
/*     */   private static Properties extractJndiProperties(ConnectionPoolDefinition connectionPoolDefinition)
/*     */   {
/* 796 */     if (connectionPoolDefinition.getJndiName() == null) {
/* 797 */       return null;
/*     */     }
/* 799 */     Properties jndiProperties = new Properties();
/* 800 */     jndiProperties.setProperty("jndi-name", connectionPoolDefinition.getJndiName());
/* 801 */     if (connectionPoolDefinition.getDelegateProperties() != null) {
/* 802 */       Properties delegateProperties = connectionPoolDefinition.getDelegateProperties();
/*     */ 
/* 805 */       String propertyName = null;
/* 806 */       List propertyNamesList = new ArrayList(10);
/* 807 */       Iterator keySetIterator = delegateProperties.keySet().iterator();
/* 808 */       while (keySetIterator.hasNext()) {
/* 809 */         propertyName = (String)keySetIterator.next();
/* 810 */         if (propertyName.startsWith("jndi-")) {
/* 811 */           propertyNamesList.add(propertyName);
/*     */         }
/*     */       }
/* 814 */       for (int i = 0; i < propertyNamesList.size(); i++) {
/* 815 */         propertyName = (String)propertyNamesList.get(i);
/* 816 */         if (propertyName.startsWith("jndi-")) {
/* 817 */           jndiProperties.setProperty(propertyName.substring("jndi-".length()), delegateProperties.getProperty(propertyName));
/*     */ 
/* 819 */           delegateProperties.remove(propertyName);
/*     */         }
/*     */       }
/*     */     }
/* 823 */     return jndiProperties;
/*     */   }
/*     */ 
/*     */   private static boolean isConfiguredForJMX(Properties poolProperties)
/*     */   {
/* 832 */     String jmxProperty = poolProperties.getProperty("proxool.jmx");
/*     */ 
/* 834 */     return (jmxProperty != null) && (jmxProperty.equalsIgnoreCase("true"));
/*     */   }
/*     */ 
/*     */   protected static void setShutdownHook(Thread t)
/*     */   {
/* 850 */     shutdownHook = t;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolFacade
 * JD-Core Version:    0.6.0
 */
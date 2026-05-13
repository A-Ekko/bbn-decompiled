/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Properties;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.naming.Context;
/*     */ import javax.naming.Name;
/*     */ import javax.naming.RefAddr;
/*     */ import javax.naming.Reference;
/*     */ import javax.naming.StringRefAddr;
/*     */ import javax.naming.spi.ObjectFactory;
/*     */ import javax.sql.DataSource;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class ProxoolDataSource
/*     */   implements DataSource, ObjectFactory
/*     */ {
/*  41 */   private static final Log LOG = LogFactory.getLog(ProxoolDataSource.class);
/*     */   private int loginTimeout;
/*     */   private PrintWriter logWriter;
/*     */   private String alias;
/*     */   private String driver;
/*     */   private String fatalSqlExceptionWrapperClass;
/*     */   private long houseKeepingSleepTime;
/*     */   private String houseKeepingTestSql;
/*     */   private long maximumActiveTime;
/*     */   private int maximumConnectionCount;
/*     */   private long maximumConnectionLifetime;
/*     */   private int minimumConnectionCount;
/*     */   private long overloadWithoutRefusalLifetime;
/*     */   private String password;
/*     */   private int prototypeCount;
/*     */   private long recentlyStartedThreshold;
/*     */   private int simultaneousBuildThrottle;
/*     */   private String statistics;
/*     */   private String statisticsLogLevel;
/*     */   private boolean trace;
/*     */   private String driverUrl;
/*     */   private String user;
/*     */   private boolean verbose;
/*     */   private boolean jmx;
/*     */   private String jmxAgentId;
/*     */   private boolean testBeforeUse;
/*     */   private boolean testAfterUse;
/*  70 */   private Properties delegateProperties = new Properties();
/*     */   private String fatalSqlExceptionsAsString;
/*     */ 
/*     */   public ProxoolDataSource()
/*     */   {
/*  79 */     reset();
/*     */   }
/*     */ 
/*     */   public ProxoolDataSource(String alias) {
/*  83 */     this.alias = alias;
/*     */   }
/*     */ 
/*     */   public Connection getConnection()
/*     */     throws SQLException
/*     */   {
/*  91 */     ConnectionPool cp = null;
/*     */     try {
/*  93 */       if (!ConnectionPoolManager.getInstance().isPoolExists(this.alias)) {
/*  94 */         registerPool();
/*     */       }
/*  96 */       cp = ConnectionPoolManager.getInstance().getConnectionPool(this.alias);
/*  97 */       return cp.getConnection();
/*     */     } catch (ProxoolException e) {
/*  99 */       LOG.error("Problem getting connection", e);
/* 100 */     }throw new SQLException(e.toString());
/*     */   }
/*     */ 
/*     */   private synchronized void registerPool()
/*     */     throws ProxoolException
/*     */   {
/* 110 */     if (!ConnectionPoolManager.getInstance().isPoolExists(this.alias)) {
/* 111 */       ConnectionPoolDefinition cpd = new ConnectionPoolDefinition();
/* 112 */       cpd.setAlias(getAlias());
/* 113 */       cpd.setDriver(getDriver());
/* 114 */       cpd.setFatalSqlExceptionsAsString(getFatalSqlExceptionsAsString());
/* 115 */       cpd.setFatalSqlExceptionWrapper(getFatalSqlExceptionWrapperClass());
/* 116 */       cpd.setHouseKeepingSleepTime(getHouseKeepingSleepTime());
/* 117 */       cpd.setHouseKeepingTestSql(getHouseKeepingTestSql());
/* 118 */       cpd.setMaximumActiveTime(getMaximumActiveTime());
/* 119 */       cpd.setMaximumConnectionCount(getMaximumConnectionCount());
/* 120 */       cpd.setMaximumConnectionLifetime(getMaximumConnectionLifetime());
/* 121 */       cpd.setMinimumConnectionCount(getMinimumConnectionCount());
/* 122 */       cpd.setOverloadWithoutRefusalLifetime(getOverloadWithoutRefusalLifetime());
/* 123 */       cpd.setPrototypeCount(getPrototypeCount());
/* 124 */       cpd.setRecentlyStartedThreshold(getRecentlyStartedThreshold());
/* 125 */       cpd.setSimultaneousBuildThrottle(getSimultaneousBuildThrottle());
/* 126 */       cpd.setStatistics(getStatistics());
/* 127 */       cpd.setStatisticsLogLevel(getStatisticsLogLevel());
/* 128 */       cpd.setTrace(isTrace());
/* 129 */       cpd.setUrl(getDriverUrl());
/* 130 */       cpd.setVerbose(isVerbose());
/* 131 */       cpd.setJmx(isJmx());
/* 132 */       cpd.setJmxAgentId(getJmxAgentId());
/* 133 */       cpd.setTestAfterUse(isTestAfterUse());
/* 134 */       cpd.setTestBeforeUse(isTestBeforeUse());
/* 135 */       cpd.setDelegateProperties(this.delegateProperties);
/*     */ 
/* 138 */       cpd.setUser(getUser());
/* 139 */       cpd.setPassword(getPassword());
/* 140 */       ProxoolFacade.registerConnectionPool(cpd);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getObjectInstance(Object refObject, Name name, Context context, Hashtable hashtable)
/*     */     throws Exception
/*     */   {
/* 147 */     if (!(refObject instanceof Reference)) {
/* 148 */       return null;
/*     */     }
/* 150 */     Reference reference = (Reference)refObject;
/*     */ 
/* 158 */     if (!ConnectionPoolManager.getInstance().isPoolExists(reference.get("proxool.alias").toString())) {
/* 159 */       populatePropertiesFromReference(reference);
/*     */     }
/* 161 */     return this;
/*     */   }
/*     */ 
/*     */   public String getAlias()
/*     */   {
/* 168 */     return this.alias;
/*     */   }
/*     */ 
/*     */   public void setAlias(String alias)
/*     */   {
/* 175 */     this.alias = alias;
/*     */   }
/*     */ 
/*     */   public String getDriverUrl()
/*     */   {
/* 182 */     return this.driverUrl;
/*     */   }
/*     */ 
/*     */   public void setDriverUrl(String url)
/*     */   {
/* 189 */     this.driverUrl = url;
/*     */   }
/*     */ 
/*     */   public String getDriver()
/*     */   {
/* 196 */     return this.driver;
/*     */   }
/*     */ 
/*     */   public void setDriver(String driver)
/*     */   {
/* 203 */     this.driver = driver;
/*     */   }
/*     */ 
/*     */   public long getMaximumConnectionLifetime()
/*     */   {
/* 210 */     return this.maximumConnectionLifetime;
/*     */   }
/*     */ 
/*     */   public void setMaximumConnectionLifetime(int maximumConnectionLifetime)
/*     */   {
/* 217 */     this.maximumConnectionLifetime = maximumConnectionLifetime;
/*     */   }
/*     */ 
/*     */   public int getPrototypeCount()
/*     */   {
/* 224 */     return this.prototypeCount;
/*     */   }
/*     */ 
/*     */   public void setPrototypeCount(int prototypeCount)
/*     */   {
/* 231 */     this.prototypeCount = prototypeCount;
/*     */   }
/*     */ 
/*     */   public int getMinimumConnectionCount()
/*     */   {
/* 238 */     return this.minimumConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setMinimumConnectionCount(int minimumConnectionCount)
/*     */   {
/* 245 */     this.minimumConnectionCount = minimumConnectionCount;
/*     */   }
/*     */ 
/*     */   public int getMaximumConnectionCount()
/*     */   {
/* 252 */     return this.maximumConnectionCount;
/*     */   }
/*     */ 
/*     */   public void setMaximumConnectionCount(int maximumConnectionCount)
/*     */   {
/* 259 */     this.maximumConnectionCount = maximumConnectionCount;
/*     */   }
/*     */ 
/*     */   public long getHouseKeepingSleepTime()
/*     */   {
/* 266 */     return this.houseKeepingSleepTime;
/*     */   }
/*     */ 
/*     */   public void setHouseKeepingSleepTime(int houseKeepingSleepTime)
/*     */   {
/* 273 */     this.houseKeepingSleepTime = houseKeepingSleepTime;
/*     */   }
/*     */ 
/*     */   public int getSimultaneousBuildThrottle()
/*     */   {
/* 280 */     return this.simultaneousBuildThrottle;
/*     */   }
/*     */ 
/*     */   public void setSimultaneousBuildThrottle(int simultaneousBuildThrottle)
/*     */   {
/* 287 */     this.simultaneousBuildThrottle = simultaneousBuildThrottle;
/*     */   }
/*     */ 
/*     */   public long getRecentlyStartedThreshold()
/*     */   {
/* 294 */     return this.recentlyStartedThreshold;
/*     */   }
/*     */ 
/*     */   public void setRecentlyStartedThreshold(int recentlyStartedThreshold)
/*     */   {
/* 301 */     this.recentlyStartedThreshold = recentlyStartedThreshold;
/*     */   }
/*     */ 
/*     */   public long getOverloadWithoutRefusalLifetime()
/*     */   {
/* 308 */     return this.overloadWithoutRefusalLifetime;
/*     */   }
/*     */ 
/*     */   public void setOverloadWithoutRefusalLifetime(int overloadWithoutRefusalLifetime)
/*     */   {
/* 315 */     this.overloadWithoutRefusalLifetime = overloadWithoutRefusalLifetime;
/*     */   }
/*     */ 
/*     */   public long getMaximumActiveTime()
/*     */   {
/* 322 */     return this.maximumActiveTime;
/*     */   }
/*     */ 
/*     */   public void setMaximumActiveTime(long maximumActiveTime)
/*     */   {
/* 329 */     this.maximumActiveTime = maximumActiveTime;
/*     */   }
/*     */ 
/*     */   public boolean isVerbose()
/*     */   {
/* 336 */     return this.verbose;
/*     */   }
/*     */ 
/*     */   public void setVerbose(boolean verbose)
/*     */   {
/* 343 */     this.verbose = verbose;
/*     */   }
/*     */ 
/*     */   public boolean isTrace()
/*     */   {
/* 350 */     return this.trace;
/*     */   }
/*     */ 
/*     */   public void setTrace(boolean trace)
/*     */   {
/* 357 */     this.trace = trace;
/*     */   }
/*     */ 
/*     */   public String getStatistics()
/*     */   {
/* 364 */     return this.statistics;
/*     */   }
/*     */ 
/*     */   public void setStatistics(String statistics)
/*     */   {
/* 371 */     this.statistics = statistics;
/*     */   }
/*     */ 
/*     */   public String getStatisticsLogLevel()
/*     */   {
/* 378 */     return this.statisticsLogLevel;
/*     */   }
/*     */ 
/*     */   public void setStatisticsLogLevel(String statisticsLogLevel)
/*     */   {
/* 385 */     this.statisticsLogLevel = statisticsLogLevel;
/*     */   }
/*     */ 
/*     */   public String getFatalSqlExceptionsAsString()
/*     */   {
/* 392 */     return this.fatalSqlExceptionsAsString;
/*     */   }
/*     */ 
/*     */   public void setFatalSqlExceptionsAsString(String fatalSqlExceptionsAsString)
/*     */   {
/* 399 */     this.fatalSqlExceptionsAsString = fatalSqlExceptionsAsString;
/*     */   }
/*     */ 
/*     */   public String getFatalSqlExceptionWrapperClass()
/*     */   {
/* 406 */     return this.fatalSqlExceptionWrapperClass;
/*     */   }
/*     */ 
/*     */   public void setFatalSqlExceptionWrapperClass(String fatalSqlExceptionWrapperClass)
/*     */   {
/* 413 */     this.fatalSqlExceptionWrapperClass = fatalSqlExceptionWrapperClass;
/*     */   }
/*     */ 
/*     */   public String getHouseKeepingTestSql()
/*     */   {
/* 420 */     return this.houseKeepingTestSql;
/*     */   }
/*     */ 
/*     */   public void setHouseKeepingTestSql(String houseKeepingTestSql)
/*     */   {
/* 427 */     this.houseKeepingTestSql = houseKeepingTestSql;
/*     */   }
/*     */ 
/*     */   public String getUser()
/*     */   {
/* 434 */     return this.user;
/*     */   }
/*     */ 
/*     */   public void setUser(String user)
/*     */   {
/* 441 */     this.user = user;
/*     */   }
/*     */ 
/*     */   public String getPassword()
/*     */   {
/* 448 */     return this.password;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/* 455 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public boolean isJmx()
/*     */   {
/* 462 */     return this.jmx;
/*     */   }
/*     */ 
/*     */   public void setJmx(boolean jmx)
/*     */   {
/* 469 */     this.jmx = jmx;
/*     */   }
/*     */ 
/*     */   public String getJmxAgentId()
/*     */   {
/* 476 */     return this.jmxAgentId;
/*     */   }
/*     */ 
/*     */   public void setJmxAgentId(String jmxAgentId)
/*     */   {
/* 483 */     this.jmxAgentId = jmxAgentId;
/*     */   }
/*     */ 
/*     */   public boolean isTestBeforeUse()
/*     */   {
/* 490 */     return this.testBeforeUse;
/*     */   }
/*     */ 
/*     */   public void setTestBeforeUse(boolean testBeforeUse)
/*     */   {
/* 497 */     this.testBeforeUse = testBeforeUse;
/*     */   }
/*     */ 
/*     */   public boolean isTestAfterUse()
/*     */   {
/* 504 */     return this.testAfterUse;
/*     */   }
/*     */ 
/*     */   public void setTestAfterUse(boolean testAfterUse)
/*     */   {
/* 511 */     this.testAfterUse = testAfterUse;
/*     */   }
/*     */ 
/*     */   public void setDelegateProperties(String properties)
/*     */   {
/* 521 */     StringTokenizer stOuter = new StringTokenizer(properties, ",");
/* 522 */     while (stOuter.hasMoreTokens()) {
/* 523 */       StringTokenizer stInner = new StringTokenizer(stOuter.nextToken(), "=");
/* 524 */       if (stInner.countTokens() == 1)
/*     */       {
/* 526 */         this.delegateProperties.put(stInner.nextToken().trim(), "");
/* 527 */       } else if (stInner.countTokens() == 2)
/* 528 */         this.delegateProperties.put(stInner.nextToken().trim(), stInner.nextToken().trim());
/*     */       else
/* 530 */         throw new IllegalArgumentException("Unexpected delegateProperties value: '" + properties + "'. Expected 'name=value'");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void populatePropertiesFromReference(Reference reference)
/*     */   {
/* 536 */     RefAddr property = reference.get("proxool.alias");
/* 537 */     if (property != null) {
/* 538 */       setAlias(property.getContent().toString());
/*     */     }
/* 540 */     property = reference.get("proxool.driver-class");
/* 541 */     if (property != null) {
/* 542 */       setDriver(property.getContent().toString());
/*     */     }
/* 544 */     property = reference.get("proxool.fatal-sql-exception-wrapper-class");
/* 545 */     if (property != null) {
/* 546 */       setFatalSqlExceptionWrapperClass(property.getContent().toString());
/*     */     }
/* 548 */     property = reference.get("proxool.house-keeping-sleep-time");
/* 549 */     if (property != null) {
/* 550 */       setHouseKeepingSleepTime(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 552 */     property = reference.get("proxool.house-keeping-test-sql");
/* 553 */     if (property != null) {
/* 554 */       setHouseKeepingTestSql(property.getContent().toString());
/*     */     }
/* 556 */     property = reference.get("proxool.maximum-connection-count");
/* 557 */     if (property != null) {
/* 558 */       setMaximumConnectionCount(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 560 */     property = reference.get("proxool.maximum-connection-lifetime");
/* 561 */     if (property != null) {
/* 562 */       setMaximumConnectionLifetime(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 564 */     property = reference.get("proxool.maximum-active-time");
/* 565 */     if (property != null) {
/* 566 */       setMaximumActiveTime(Long.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 568 */     property = reference.get("proxool.minimum-connection-count");
/* 569 */     if (property != null) {
/* 570 */       setMinimumConnectionCount(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 572 */     property = reference.get("proxool.overload-without-refusal-lifetime");
/* 573 */     if (property != null) {
/* 574 */       setOverloadWithoutRefusalLifetime(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 576 */     property = reference.get("password");
/* 577 */     if (property != null) {
/* 578 */       setPassword(property.getContent().toString());
/*     */     }
/* 580 */     property = reference.get("proxool.prototype-count");
/* 581 */     if (property != null) {
/* 582 */       setPrototypeCount(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 584 */     property = reference.get("proxool.recently-started-threshold");
/* 585 */     if (property != null) {
/* 586 */       setRecentlyStartedThreshold(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 588 */     property = reference.get("proxool.simultaneous-build-throttle");
/* 589 */     if (property != null) {
/* 590 */       setSimultaneousBuildThrottle(Integer.valueOf(property.getContent().toString()).intValue());
/*     */     }
/* 592 */     property = reference.get("proxool.statistics");
/* 593 */     if (property != null) {
/* 594 */       setStatistics(property.getContent().toString());
/*     */     }
/* 596 */     property = reference.get("proxool.statistics-log-level");
/* 597 */     if (property != null) {
/* 598 */       setStatisticsLogLevel(property.getContent().toString());
/*     */     }
/* 600 */     property = reference.get("proxool.trace");
/* 601 */     if (property != null) {
/* 602 */       setTrace("true".equalsIgnoreCase(property.getContent().toString()));
/*     */     }
/* 604 */     property = reference.get("proxool.driver-url");
/* 605 */     if (property != null) {
/* 606 */       setDriverUrl(property.getContent().toString());
/*     */     }
/* 608 */     property = reference.get("user");
/* 609 */     if (property != null) {
/* 610 */       setUser(property.getContent().toString());
/*     */     }
/* 612 */     property = reference.get("proxool.verbose");
/* 613 */     if (property != null) {
/* 614 */       setVerbose("true".equalsIgnoreCase(property.getContent().toString()));
/*     */     }
/* 616 */     property = reference.get("proxool.jmx");
/* 617 */     if (property != null) {
/* 618 */       setJmx("true".equalsIgnoreCase(property.getContent().toString()));
/*     */     }
/* 620 */     property = reference.get("proxool.jmx-agent-id");
/* 621 */     if (property != null) {
/* 622 */       setJmxAgentId(property.getContent().toString());
/*     */     }
/* 624 */     property = reference.get("proxool.test-before-use");
/* 625 */     if (property != null) {
/* 626 */       setTestBeforeUse("true".equalsIgnoreCase(property.getContent().toString()));
/*     */     }
/* 628 */     property = reference.get("proxool.test-after-use");
/* 629 */     if (property != null) {
/* 630 */       setTestAfterUse("true".equalsIgnoreCase(property.getContent().toString()));
/*     */     }
/*     */ 
/* 633 */     Enumeration e = reference.getAll();
/* 634 */     while (e.hasMoreElements()) {
/* 635 */       StringRefAddr stringRefAddr = (StringRefAddr)e.nextElement();
/* 636 */       String name = stringRefAddr.getType();
/* 637 */       String content = stringRefAddr.getContent().toString();
/* 638 */       if (name.indexOf("proxool.") != 0)
/* 639 */         this.delegateProperties.put(name, content);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void reset()
/*     */   {
/* 648 */     this.driverUrl = null;
/* 649 */     this.driver = null;
/* 650 */     this.maximumConnectionLifetime = 14400000L;
/* 651 */     this.prototypeCount = 0;
/* 652 */     this.minimumConnectionCount = 0;
/* 653 */     this.maximumConnectionCount = 15;
/* 654 */     this.houseKeepingSleepTime = 30000L;
/* 655 */     this.houseKeepingTestSql = null;
/* 656 */     this.simultaneousBuildThrottle = 10;
/* 657 */     this.recentlyStartedThreshold = 60000L;
/* 658 */     this.overloadWithoutRefusalLifetime = 60000L;
/* 659 */     this.maximumActiveTime = 300000L;
/* 660 */     this.verbose = false;
/* 661 */     this.trace = false;
/* 662 */     this.statistics = null;
/* 663 */     this.statisticsLogLevel = null;
/* 664 */     this.delegateProperties.clear();
/*     */   }
/*     */ 
/*     */   public PrintWriter getLogWriter() throws SQLException {
/* 668 */     return this.logWriter;
/*     */   }
/*     */ 
/*     */   public int getLoginTimeout() throws SQLException {
/* 672 */     return this.loginTimeout;
/*     */   }
/*     */ 
/*     */   public void setLogWriter(PrintWriter logWriter) throws SQLException {
/* 676 */     this.logWriter = logWriter;
/*     */   }
/*     */ 
/*     */   public void setLoginTimeout(int loginTimeout) throws SQLException {
/* 680 */     this.loginTimeout = loginTimeout;
/*     */   }
/*     */ 
/*     */   public Connection getConnection(String s, String s1) throws SQLException {
/* 684 */     throw new UnsupportedOperationException("You should configure the username and password within the proxool configuration and just call getConnection() instead.");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolDataSource
 * JD-Core Version:    0.6.0
 */
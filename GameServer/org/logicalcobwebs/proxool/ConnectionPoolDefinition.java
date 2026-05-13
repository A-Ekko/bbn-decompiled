/*      */ package org.logicalcobwebs.proxool;
/*      */ 
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.sql.Driver;
/*      */ import java.sql.DriverManager;
/*      */ import java.sql.SQLException;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import org.apache.commons.logging.Log;
/*      */ import org.apache.commons.logging.LogFactory;
/*      */ 
/*      */ class ConnectionPoolDefinition
/*      */   implements ConnectionPoolDefinitionIF
/*      */ {
/*   28 */   private static final Log LOG = LogFactory.getLog(ConnectionPoolDefinition.class);
/*      */ 
/*   33 */   private Log poolLog = LOG;
/*      */   private String alias;
/*      */   private String jndiName;
/*      */   private String initialContextFactory;
/*      */   private String providerUrl;
/*      */   private String securityAuthentication;
/*      */   private String securityPrincipal;
/*      */   private String securityCredentials;
/*   51 */   private Properties delegateProperties = new Properties();
/*      */ 
/*   53 */   private Properties completeInfo = new Properties();
/*      */ 
/*   55 */   private Properties changedInfo = new Properties();
/*      */   private boolean connectionPropertiesChanged;
/*      */   private String url;
/*      */   private String completeUrl;
/*      */   private String driver;
/*      */   private long maximumConnectionLifetime;
/*      */   private int prototypeCount;
/*      */   private int minimumConnectionCount;
/*      */   private int maximumConnectionCount;
/*      */   private long houseKeepingSleepTime;
/*      */   private int simultaneousBuildThrottle;
/*      */   private long recentlyStartedThreshold;
/*      */   private long overloadWithoutRefusalLifetime;
/*      */   private long maximumActiveTime;
/*      */   private boolean verbose;
/*      */   private boolean trace;
/*      */   private String statistics;
/*      */   private String statisticsLogLevel;
/*   96 */   private Set fatalSqlExceptions = new HashSet();
/*      */   private String fatalSqlExceptionsAsString;
/*  104 */   private String fatalSqlExceptionWrapper = null;
/*      */   private String houseKeepingTestSql;
/*      */   private boolean testBeforeUse;
/*      */   private boolean testAfterUse;
/*      */   private boolean jmx;
/*      */   private String jmxAgentId;
/*      */   private Class injectableConnectionInterface;
/*      */   private Class injectableStatementInterface;
/*      */   private Class injectablePreparedStatementInterface;
/*      */   private Class injectableCallableStatementInterface;
/*      */ 
/*      */   public ConnectionPoolDefinition()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected ConnectionPoolDefinition(String url, Properties info, boolean explicitRegister)
/*      */     throws ProxoolException
/*      */   {
/*  140 */     this.alias = ProxoolFacade.getAlias(url);
/*  141 */     this.poolLog = LogFactory.getLog("org.logicalcobwebs.proxool." + this.alias);
/*  142 */     reset();
/*  143 */     doChange(url, info, false, !explicitRegister);
/*      */   }
/*      */ 
/*      */   protected void update(String url, Properties info)
/*      */     throws ProxoolException
/*      */   {
/*  155 */     this.changedInfo.clear();
/*  156 */     this.connectionPropertiesChanged = false;
/*  157 */     this.poolLog.debug("Updating definition");
/*  158 */     doChange(url, info, false, false);
/*  159 */     if (this.connectionPropertiesChanged) {
/*  160 */       this.poolLog.info("Mercifully killing all current connections because of definition changes");
/*  161 */       ProxoolFacade.killAllConnections(this.alias, "of definition changes", true);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void redefine(String url, Properties info)
/*      */     throws ProxoolException
/*      */   {
/*  174 */     reset();
/*  175 */     this.changedInfo.clear();
/*  176 */     this.connectionPropertiesChanged = false;
/*  177 */     this.poolLog.debug("Redefining definition");
/*  178 */     doChange(url, info, false, false);
/*      */ 
/*  181 */     if ((getUrl() == null) || (getDriver() == null)) {
/*  182 */       throw new ProxoolException("The URL is not defined properly: " + getCompleteUrl());
/*      */     }
/*      */ 
/*  185 */     if (this.connectionPropertiesChanged) {
/*  186 */       LOG.info("Mercifully killing all current connections because of definition changes");
/*  187 */       ProxoolFacade.killAllConnections(this.alias, "definition has changed", true);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean doChange(String url, Properties info, boolean pretend, boolean implicitRegister) throws ProxoolException
/*      */   {
/*  193 */     boolean changed = false;
/*      */     try
/*      */     {
/*  196 */       int endOfPrefix = url.indexOf(':');
/*  197 */       int endOfDriver = url.indexOf(':', endOfPrefix + 1);
/*      */ 
/*  199 */       if ((endOfPrefix > -1) && (endOfDriver > -1)) {
/*  200 */         String driver = url.substring(endOfPrefix + 1, endOfDriver);
/*  201 */         if (isChanged(getDriver(), driver)) {
/*  202 */           changed = true;
/*  203 */           if (!pretend) {
/*  204 */             logChange(true, "proxool.driver", driver);
/*  205 */             setDriver(driver);
/*      */           }
/*      */         }
/*      */ 
/*  209 */         String delegateUrl = url.substring(endOfDriver + 1);
/*  210 */         if (isChanged(getUrl(), delegateUrl)) {
/*  211 */           changed = true;
/*  212 */           if (!pretend) {
/*  213 */             logChange(true, "proxool.url", delegateUrl);
/*  214 */             setUrl(delegateUrl);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IndexOutOfBoundsException e)
/*      */     {
/*  221 */       LOG.error("Invalid URL: '" + url + "'", e);
/*  222 */       throw new ProxoolException("Invalid URL: '" + url + "'");
/*      */     }
/*      */ 
/*  225 */     if (!pretend) {
/*  226 */       setCompleteUrl(url);
/*      */     }
/*      */ 
/*  229 */     if (info != null) {
/*  230 */       Enumeration e = info.propertyNames();
/*  231 */       while (e.hasMoreElements()) {
/*  232 */         String key = (String)e.nextElement();
/*  233 */         String value = info.getProperty(key);
/*  234 */         changed |= setAnyProperty(key, value, pretend);
/*  235 */         if (!pretend) {
/*  236 */           this.completeInfo.setProperty(key, value);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  241 */     if (!pretend) {
/*  242 */       ProxoolFacade.definitionUpdated(getAlias(), this, this.completeInfo, this.changedInfo);
/*      */     }
/*      */ 
/*  245 */     if (((getDriver() == null) || (getUrl() == null)) && (implicitRegister)) {
/*  246 */       throw new ProxoolException("Attempt to refer to a unregistered pool by its alias '" + getAlias() + "'");
/*      */     }
/*      */ 
/*  249 */     return changed;
/*      */   }
/*      */ 
/*      */   private void logChange(boolean proxoolProperty, String key, String value) {
/*  253 */     if (this.poolLog.isDebugEnabled()) {
/*  254 */       String displayValue = value;
/*  255 */       if (key.toLowerCase().indexOf("password") > -1) {
/*  256 */         displayValue = "********";
/*      */       }
/*  258 */       this.poolLog.debug((proxoolProperty ? "Recognised proxool property: " : "Delegating property to driver: ") + key + "=" + displayValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean setAnyProperty(String key, String value, boolean pretend) throws ProxoolException {
/*  263 */     boolean proxoolProperty = true;
/*  264 */     boolean changed = false;
/*      */ 
/*  267 */     changed = (changed) || (setHouseKeeperProperty(key, value, pretend));
/*  268 */     changed = (changed) || (setLoggingProperty(key, value, pretend));
/*  269 */     changed = (changed) || (setInjectableProperty(key, value, pretend));
/*  270 */     changed = (changed) || (setJndiProperty(key, value, pretend));
/*      */ 
/*  272 */     if (key.equals("user")) {
/*  273 */       proxoolProperty = false;
/*  274 */       if (isChanged(getUser(), value)) {
/*  275 */         changed = true;
/*  276 */         if (!pretend)
/*  277 */           setUser(value);
/*      */       }
/*      */     }
/*  280 */     else if (key.equals("password")) {
/*  281 */       proxoolProperty = false;
/*  282 */       if (isChanged(getPassword(), value)) {
/*  283 */         changed = true;
/*  284 */         if (!pretend)
/*  285 */           setPassword(value);
/*      */       }
/*      */     }
/*  288 */     else if (key.equals("proxool.driver")) {
/*  289 */       if (isChanged(getDriver(), value)) {
/*  290 */         changed = true;
/*  291 */         if (!pretend)
/*  292 */           setDriver(value);
/*      */       }
/*      */     }
/*  295 */     else if (key.equals("proxool.url")) {
/*  296 */       if (isChanged(getUrl(), value)) {
/*  297 */         changed = true;
/*  298 */         if (!pretend)
/*  299 */           setUrl(value);
/*      */       }
/*      */     }
/*  302 */     else if (key.equals("proxool.maximum-connection-count")) {
/*  303 */       if (getMaximumConnectionCount() != getInt(key, value)) {
/*  304 */         changed = true;
/*  305 */         if (!pretend)
/*  306 */           setMaximumConnectionCount(getInt(key, value));
/*      */       }
/*      */     }
/*  309 */     else if (key.equals("proxool.maximum-connection-lifetime")) {
/*  310 */       if (getMaximumConnectionLifetime() != getLong(key, value)) {
/*  311 */         changed = true;
/*  312 */         if (!pretend)
/*  313 */           setMaximumConnectionLifetime(getLong(key, value));
/*      */       }
/*      */     }
/*  316 */     else if (key.equals("proxool.maximum-new-connections")) {
/*  317 */       this.poolLog.warn("Use of proxool.maximum-new-connections is deprecated. Use more descriptive proxool.simultaneous-build-throttle instead.");
/*  318 */       if (getSimultaneousBuildThrottle() != getInt(key, value)) {
/*  319 */         changed = true;
/*  320 */         if (!pretend)
/*  321 */           setSimultaneousBuildThrottle(getInt(key, value));
/*      */       }
/*      */     }
/*  324 */     else if (key.equals("proxool.simultaneous-build-throttle")) {
/*  325 */       if (getSimultaneousBuildThrottle() != getInt(key, value)) {
/*  326 */         changed = true;
/*  327 */         setSimultaneousBuildThrottle(getInt(key, value));
/*      */       }
/*  329 */     } else if (key.equals("proxool.minimum-connection-count")) {
/*  330 */       if (getMinimumConnectionCount() != getInt(key, value)) {
/*  331 */         changed = true;
/*  332 */         if (!pretend)
/*  333 */           setMinimumConnectionCount(getInt(key, value));
/*      */       }
/*      */     }
/*  336 */     else if (key.equals("proxool.prototype-count")) {
/*  337 */       if (getPrototypeCount() != getInt(key, value)) {
/*  338 */         changed = true;
/*  339 */         if (!pretend)
/*  340 */           setPrototypeCount(getInt(key, value));
/*      */       }
/*      */     }
/*  343 */     else if (key.equals("proxool.recently-started-threshold")) {
/*  344 */       if (getRecentlyStartedThreshold() != getLong(key, value)) {
/*  345 */         changed = true;
/*  346 */         if (!pretend)
/*  347 */           setRecentlyStartedThreshold(getLong(key, value));
/*      */       }
/*      */     }
/*  350 */     else if (key.equals("proxool.overload-without-refusal-lifetime")) {
/*  351 */       if (getOverloadWithoutRefusalLifetime() != getLong(key, value)) {
/*  352 */         changed = true;
/*  353 */         if (!pretend)
/*  354 */           setOverloadWithoutRefusalLifetime(getLong(key, value));
/*      */       }
/*      */     }
/*  357 */     else if (key.equals("proxool.maximum-active-time")) {
/*  358 */       if (getMaximumActiveTime() != getLong(key, value)) {
/*  359 */         changed = true;
/*  360 */         if (!pretend)
/*  361 */           setMaximumActiveTime(getLong(key, value));
/*      */       }
/*      */     }
/*  364 */     else if (key.equals("proxool.fatal-sql-exception")) {
/*  365 */       if (isChanged(this.fatalSqlExceptionsAsString, value)) {
/*  366 */         changed = true;
/*  367 */         if (!pretend)
/*  368 */           setFatalSqlExceptionsAsString(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  371 */     else if (key.equals("proxool.fatal-sql-exception-wrapper-class")) {
/*  372 */       if (isChanged(this.fatalSqlExceptionWrapper, value)) {
/*  373 */         changed = true;
/*  374 */         if (!pretend)
/*  375 */           setFatalSqlExceptionWrapper(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  378 */     else if (key.equals("proxool.statistics")) {
/*  379 */       if (isChanged(getStatistics(), value)) {
/*  380 */         changed = true;
/*  381 */         if (!pretend)
/*  382 */           setStatistics(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  385 */     else if ((key.equals("proxool.statistics-log-level")) && 
/*  386 */       (isChanged(getStatisticsLogLevel(), value))) {
/*  387 */       changed = true;
/*  388 */       if (!pretend) {
/*  389 */         setStatisticsLogLevel(value.length() > 0 ? value : null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  394 */     if (!key.startsWith("proxool.")) {
/*  395 */       if (isChanged(getDelegateProperty(key), value)) {
/*  396 */         changed = true;
/*  397 */         if (!pretend) {
/*  398 */           setDelegateProperty(key, value);
/*      */         }
/*      */       }
/*  401 */       proxoolProperty = false;
/*      */     }
/*      */ 
/*  404 */     if ((changed) && (!pretend)) {
/*  405 */       logChange(proxoolProperty, key, value);
/*  406 */       this.changedInfo.setProperty(key, value);
/*      */     }
/*  408 */     return changed;
/*      */   }
/*      */ 
/*      */   private boolean setLoggingProperty(String key, String value, boolean pretend)
/*      */   {
/*  416 */     boolean changed = false;
/*  417 */     if (key.equals("proxool.debug-level")) {
/*  418 */       if ((value != null) && (value.equals("1"))) {
/*  419 */         this.poolLog.warn("Use of proxool.debug-level=1 is deprecated. Use proxool.verbose=true instead.");
/*  420 */         if (!isVerbose()) {
/*  421 */           changed = true;
/*  422 */           if (!pretend)
/*  423 */             setVerbose(true);
/*      */         }
/*      */       }
/*      */       else {
/*  427 */         this.poolLog.warn("Use of proxool.debug-level=0 is deprecated. Use proxool.verbose=false instead.");
/*  428 */         if (isVerbose()) {
/*  429 */           changed = true;
/*  430 */           if (!pretend)
/*  431 */             setVerbose(false);
/*      */         }
/*      */       }
/*      */     }
/*  435 */     else if (key.equals("proxool.verbose")) {
/*  436 */       boolean valueAsBoolean = Boolean.valueOf(value).booleanValue();
/*  437 */       if (isVerbose() != valueAsBoolean) {
/*  438 */         changed = true;
/*  439 */         if (!pretend)
/*  440 */           setVerbose(valueAsBoolean);
/*      */       }
/*      */     }
/*  443 */     else if (key.equals("proxool.trace")) {
/*  444 */       boolean valueAsBoolean = Boolean.valueOf(value).booleanValue();
/*  445 */       if (isTrace() != valueAsBoolean) {
/*  446 */         changed = true;
/*  447 */         if (!pretend) {
/*  448 */           setTrace(valueAsBoolean);
/*      */         }
/*      */       }
/*      */     }
/*  452 */     return changed;
/*      */   }
/*      */ 
/*      */   private boolean setInjectableProperty(String key, String value, boolean pretend)
/*      */   {
/*  460 */     boolean changed = false;
/*  461 */     if (key.equals("proxool.injectable-connection-interface")) {
/*  462 */       if (isChanged(getInjectableConnectionInterfaceName(), value)) {
/*  463 */         changed = true;
/*  464 */         if (!pretend)
/*  465 */           setInjectableConnectionInterfaceName(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  468 */     else if (key.equals("proxool.injectable-statement-interface")) {
/*  469 */       if (isChanged(getInjectableStatementInterfaceName(), value)) {
/*  470 */         changed = true;
/*  471 */         if (!pretend)
/*  472 */           setInjectableStatementInterfaceName(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  475 */     else if (key.equals("proxool.injectable-prepared-statement-interface")) {
/*  476 */       if (isChanged(getInjectablePreparedStatementInterfaceName(), value)) {
/*  477 */         changed = true;
/*  478 */         if (!pretend)
/*  479 */           setInjectablePreparedStatementInterfaceName(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  482 */     else if ((key.equals("proxool.injectable-callable-statement-interface")) && 
/*  483 */       (isChanged(getInjectableCallableStatememtInterfaceName(), value))) {
/*  484 */       changed = true;
/*  485 */       if (!pretend) {
/*  486 */         setInjectableCallableStatementInterfaceName(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*      */ 
/*  490 */     return changed;
/*      */   }
/*      */ 
/*      */   private boolean setHouseKeeperProperty(String key, String value, boolean pretend)
/*      */     throws ProxoolException
/*      */   {
/*  498 */     boolean changed = false;
/*  499 */     if (key.equals("proxool.house-keeping-sleep-time")) {
/*  500 */       if (getHouseKeepingSleepTime() != getLong(key, value)) {
/*  501 */         changed = true;
/*  502 */         if (!pretend)
/*  503 */           setHouseKeepingSleepTime(getLong(key, value));
/*      */       }
/*      */     }
/*  506 */     else if (key.equals("proxool.house-keeping-test-sql")) {
/*  507 */       if (isChanged(getHouseKeepingTestSql(), value)) {
/*  508 */         changed = true;
/*  509 */         if (!pretend)
/*  510 */           setHouseKeepingTestSql(value.length() > 0 ? value : null);
/*      */       }
/*      */     }
/*  513 */     else if (key.equals("proxool.test-before-use")) {
/*  514 */       boolean valueAsBoolean = Boolean.valueOf(value).booleanValue();
/*  515 */       if (isTestBeforeUse() != valueAsBoolean) {
/*  516 */         changed = true;
/*  517 */         if (!pretend)
/*  518 */           setTestBeforeUse(valueAsBoolean);
/*      */       }
/*      */     }
/*  521 */     else if (key.equals("proxool.test-after-use")) {
/*  522 */       boolean valueAsBoolean = Boolean.valueOf(value).booleanValue();
/*  523 */       if (isTestAfterUse() != valueAsBoolean) {
/*  524 */         changed = true;
/*  525 */         if (!pretend) {
/*  526 */           setTestAfterUse(valueAsBoolean);
/*      */         }
/*      */       }
/*      */     }
/*  530 */     return changed;
/*      */   }
/*      */ 
/*      */   private boolean setJndiProperty(String key, String value, boolean pretend)
/*      */   {
/*  538 */     boolean changed = false;
/*  539 */     if ((key.equals("proxool.jndi-name")) && 
/*  540 */       (isChanged(getJndiName(), value))) {
/*  541 */       changed = true;
/*  542 */       if (!pretend) {
/*  543 */         setJndiName(value.length() > 0 ? value : null);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  549 */     return changed;
/*      */   }
/*      */   private int getInt(String key, String value) throws ProxoolException {
/*      */     try {
/*  553 */       return Integer.parseInt(value); } catch (NumberFormatException e) {
/*      */     }
/*  555 */     throw new ProxoolException("'" + key + "' property must be an integer. Found '" + value + "' instead.");
/*      */   }
/*      */ 
/*      */   private long getLong(String key, String value) throws ProxoolException
/*      */   {
/*      */     try {
/*  561 */       return Long.parseLong(value); } catch (NumberFormatException e) {
/*      */     }
/*  563 */     throw new ProxoolException("'" + key + "' property must be a long. Found '" + value + "' instead.");
/*      */   }
/*      */ 
/*      */   private static boolean isChanged(String oldValue, String newValue)
/*      */   {
/*  568 */     boolean changed = false;
/*  569 */     if (oldValue == null) {
/*  570 */       if (newValue != null)
/*  571 */         changed = true;
/*      */     }
/*  573 */     else if (newValue == null)
/*  574 */       changed = true;
/*  575 */     else if (!oldValue.equals(newValue)) {
/*  576 */       changed = true;
/*      */     }
/*  578 */     return changed;
/*      */   }
/*      */ 
/*      */   protected Object clone()
/*      */     throws CloneNotSupportedException
/*      */   {
/*  587 */     ConnectionPoolDefinition clone = new ConnectionPoolDefinition();
/*      */ 
/*  589 */     clone.setCompleteUrl(this.completeUrl);
/*  590 */     clone.setDelegateProperties((Properties)this.delegateProperties.clone());
/*  591 */     clone.setCompleteInfo((Properties)this.completeInfo.clone());
/*  592 */     clone.clearChangedInfo();
/*      */ 
/*  594 */     clone.setAlias(this.alias);
/*  595 */     clone.setUrl(this.url);
/*  596 */     clone.setDriver(this.driver);
/*  597 */     clone.setMaximumConnectionLifetime(this.maximumConnectionLifetime);
/*  598 */     clone.setPrototypeCount(this.prototypeCount);
/*  599 */     clone.setMinimumConnectionCount(this.minimumConnectionCount);
/*  600 */     clone.setMaximumConnectionCount(this.maximumConnectionCount);
/*  601 */     clone.setHouseKeepingSleepTime(this.houseKeepingSleepTime);
/*  602 */     clone.setHouseKeepingTestSql(this.houseKeepingTestSql);
/*  603 */     clone.setTestAfterUse(this.testAfterUse);
/*  604 */     clone.setTestBeforeUse(this.testBeforeUse);
/*  605 */     clone.setSimultaneousBuildThrottle(this.simultaneousBuildThrottle);
/*  606 */     clone.setRecentlyStartedThreshold(this.recentlyStartedThreshold);
/*  607 */     clone.setOverloadWithoutRefusalLifetime(this.overloadWithoutRefusalLifetime);
/*  608 */     clone.setMaximumActiveTime(this.maximumActiveTime);
/*  609 */     clone.setVerbose(this.verbose);
/*  610 */     clone.setTrace(this.trace);
/*  611 */     clone.setStatistics(this.statistics);
/*  612 */     clone.setStatisticsLogLevel(this.statisticsLogLevel);
/*  613 */     clone.setFatalSqlExceptionsAsString(this.fatalSqlExceptionsAsString);
/*      */     try {
/*  615 */       clone.setFatalSqlExceptionWrapper(this.fatalSqlExceptionWrapper);
/*      */     } catch (ProxoolException e) {
/*  617 */       throw new IllegalArgumentException("Problem cloning fatalSqlExceptionWrapper: " + this.fatalSqlExceptionWrapper);
/*      */     }
/*  619 */     return clone;
/*      */   }
/*      */ 
/*      */   private void clearChangedInfo() {
/*  623 */     this.changedInfo.clear();
/*      */   }
/*      */ 
/*      */   private void reset()
/*      */   {
/*  630 */     this.completeUrl = null;
/*  631 */     this.delegateProperties.clear();
/*  632 */     this.completeInfo.clear();
/*  633 */     this.changedInfo.clear();
/*      */ 
/*  635 */     this.url = null;
/*  636 */     this.driver = null;
/*  637 */     this.maximumConnectionLifetime = 14400000L;
/*  638 */     this.prototypeCount = 0;
/*  639 */     this.minimumConnectionCount = 0;
/*  640 */     this.maximumConnectionCount = 15;
/*  641 */     this.houseKeepingSleepTime = 30000L;
/*  642 */     this.houseKeepingTestSql = null;
/*  643 */     this.testAfterUse = false;
/*  644 */     this.testBeforeUse = false;
/*  645 */     this.simultaneousBuildThrottle = 10;
/*  646 */     this.recentlyStartedThreshold = 60000L;
/*  647 */     this.overloadWithoutRefusalLifetime = 60000L;
/*  648 */     this.maximumActiveTime = 300000L;
/*  649 */     this.verbose = false;
/*  650 */     this.trace = false;
/*  651 */     this.statistics = null;
/*  652 */     this.statisticsLogLevel = null;
/*  653 */     this.fatalSqlExceptions.clear();
/*  654 */     this.fatalSqlExceptionWrapper = null;
/*      */   }
/*      */ 
/*      */   protected Properties getCompleteInfo()
/*      */   {
/*  662 */     return this.completeInfo;
/*      */   }
/*      */ 
/*      */   public void setCompleteInfo(Properties completeInfo)
/*      */   {
/*  671 */     this.completeInfo = completeInfo;
/*      */   }
/*      */ 
/*      */   public String getUser()
/*      */   {
/*  678 */     return getDelegateProperty("user");
/*      */   }
/*      */ 
/*      */   public void setUser(String user)
/*      */   {
/*  685 */     setDelegateProperty("user", user);
/*      */   }
/*      */ 
/*      */   public String getPassword()
/*      */   {
/*  692 */     return getDelegateProperty("password");
/*      */   }
/*      */ 
/*      */   public void setPassword(String password)
/*      */   {
/*  699 */     setDelegateProperty("password", password);
/*      */   }
/*      */ 
/*      */   public String getJdbcDriverVersion()
/*      */   {
/*      */     try
/*      */     {
/*  708 */       Driver driver = DriverManager.getDriver(getUrl());
/*  709 */       return driver.getMajorVersion() + "." + driver.getMinorVersion();
/*      */     } catch (SQLException e) {
/*  711 */       return "Trying to locate driver version for '" + getUrl() + "' caused: " + e.toString(); } catch (NullPointerException e) {
/*      */     }
/*  713 */     return "Couldn't locate driver for '" + getUrl() + "'!";
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  722 */     return getCompleteUrl();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public String getName()
/*      */   {
/*  730 */     return this.alias;
/*      */   }
/*      */ 
/*      */   public String getAlias()
/*      */   {
/*  737 */     return this.alias;
/*      */   }
/*      */ 
/*      */   public void setAlias(String alias)
/*      */   {
/*  744 */     this.alias = alias;
/*      */   }
/*      */ 
/*      */   public long getMaximumConnectionLifetime()
/*      */   {
/*  751 */     return this.maximumConnectionLifetime;
/*      */   }
/*      */ 
/*      */   public void setMaximumConnectionLifetime(long maximumConnectionLifetime)
/*      */   {
/*  758 */     this.maximumConnectionLifetime = maximumConnectionLifetime;
/*      */   }
/*      */ 
/*      */   public int getPrototypeCount()
/*      */   {
/*  765 */     return this.prototypeCount;
/*      */   }
/*      */ 
/*      */   public void setPrototypeCount(int prototypeCount)
/*      */   {
/*  772 */     this.prototypeCount = prototypeCount;
/*      */   }
/*      */ 
/*      */   public int getMinimumConnectionCount()
/*      */   {
/*  779 */     return this.minimumConnectionCount;
/*      */   }
/*      */ 
/*      */   public void setMinimumConnectionCount(int minimumConnectionCount)
/*      */   {
/*  786 */     this.minimumConnectionCount = minimumConnectionCount;
/*      */   }
/*      */ 
/*      */   public int getMaximumConnectionCount()
/*      */   {
/*  793 */     return this.maximumConnectionCount;
/*      */   }
/*      */ 
/*      */   public void setMaximumConnectionCount(int maximumConnectionCount)
/*      */   {
/*  800 */     this.maximumConnectionCount = maximumConnectionCount;
/*      */   }
/*      */ 
/*      */   public long getHouseKeepingSleepTime()
/*      */   {
/*  807 */     return this.houseKeepingSleepTime;
/*      */   }
/*      */ 
/*      */   public void setHouseKeepingSleepTime(long houseKeepingSleepTime)
/*      */   {
/*  814 */     this.houseKeepingSleepTime = houseKeepingSleepTime;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public int getMaximumNewConnections()
/*      */   {
/*  822 */     return this.simultaneousBuildThrottle;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setMaximumNewConnections(int maximumNewConnections)
/*      */   {
/*  830 */     this.simultaneousBuildThrottle = maximumNewConnections;
/*      */   }
/*      */ 
/*      */   public int getSimultaneousBuildThrottle()
/*      */   {
/*  837 */     return this.simultaneousBuildThrottle;
/*      */   }
/*      */ 
/*      */   public void setSimultaneousBuildThrottle(int simultaneousBuildThrottle)
/*      */   {
/*  844 */     this.simultaneousBuildThrottle = simultaneousBuildThrottle;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Properties getProperties()
/*      */   {
/*  852 */     return this.delegateProperties;
/*      */   }
/*      */ 
/*      */   public Properties getDelegateProperties()
/*      */   {
/*  859 */     return this.delegateProperties;
/*      */   }
/*      */ 
/*      */   public String getDelegateProperty(String name)
/*      */   {
/*  868 */     return getDelegateProperties().getProperty(name);
/*      */   }
/*      */ 
/*      */   public void setDelegateProperty(String name, String value)
/*      */   {
/*  878 */     this.connectionPropertiesChanged = true;
/*  879 */     getDelegateProperties().setProperty(name, value);
/*      */   }
/*      */ 
/*      */   public void setDelegateProperties(Properties delegateProperties)
/*      */   {
/*  888 */     this.delegateProperties = delegateProperties;
/*      */   }
/*      */ 
/*      */   public String getUrl()
/*      */   {
/*  895 */     return this.url;
/*      */   }
/*      */ 
/*      */   public void setUrl(String url)
/*      */   {
/*  902 */     this.url = url;
/*  903 */     this.connectionPropertiesChanged = true;
/*      */   }
/*      */ 
/*      */   public String getDriver()
/*      */   {
/*  910 */     return this.driver;
/*      */   }
/*      */ 
/*      */   public void setDriver(String driver)
/*      */   {
/*  917 */     this.driver = driver;
/*  918 */     this.connectionPropertiesChanged = true;
/*      */   }
/*      */ 
/*      */   public long getRecentlyStartedThreshold()
/*      */   {
/*  925 */     return this.recentlyStartedThreshold;
/*      */   }
/*      */ 
/*      */   public void setRecentlyStartedThreshold(long recentlyStartedThreshold)
/*      */   {
/*  932 */     this.recentlyStartedThreshold = recentlyStartedThreshold;
/*      */   }
/*      */ 
/*      */   public long getOverloadWithoutRefusalLifetime()
/*      */   {
/*  939 */     return this.overloadWithoutRefusalLifetime;
/*      */   }
/*      */ 
/*      */   public void setOverloadWithoutRefusalLifetime(long overloadWithoutRefusalLifetime)
/*      */   {
/*  946 */     this.overloadWithoutRefusalLifetime = overloadWithoutRefusalLifetime;
/*      */   }
/*      */ 
/*      */   public long getMaximumActiveTime()
/*      */   {
/*  953 */     return this.maximumActiveTime;
/*      */   }
/*      */ 
/*      */   public void setMaximumActiveTime(long maximumActiveTime)
/*      */   {
/*  960 */     this.maximumActiveTime = maximumActiveTime;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public int getDebugLevel()
/*      */   {
/*  968 */     return this.verbose ? 1 : 0;
/*      */   }
/*      */ 
/*      */   public boolean isVerbose()
/*      */   {
/*  975 */     return this.verbose;
/*      */   }
/*      */ 
/*      */   public void setVerbose(boolean verbose)
/*      */   {
/*  982 */     this.verbose = verbose;
/*      */   }
/*      */ 
/*      */   public boolean isTrace()
/*      */   {
/*  989 */     return this.trace;
/*      */   }
/*      */ 
/*      */   public void setTrace(boolean trace)
/*      */   {
/*  996 */     this.trace = trace;
/*      */   }
/*      */ 
/*      */   public String getCompleteUrl()
/*      */   {
/* 1003 */     return this.completeUrl;
/*      */   }
/*      */ 
/*      */   public void setCompleteUrl(String completeUrl)
/*      */   {
/* 1010 */     this.completeUrl = completeUrl;
/*      */   }
/*      */ 
/*      */   public void setFatalSqlExceptionsAsString(String fatalSqlExceptionsAsString)
/*      */   {
/* 1017 */     this.fatalSqlExceptionsAsString = fatalSqlExceptionsAsString;
/* 1018 */     this.fatalSqlExceptions.clear();
/* 1019 */     if (fatalSqlExceptionsAsString != null) {
/* 1020 */       StringTokenizer st = new StringTokenizer(fatalSqlExceptionsAsString, ",");
/* 1021 */       while (st.hasMoreTokens())
/* 1022 */         this.fatalSqlExceptions.add(st.nextToken().trim());
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set getFatalSqlExceptions()
/*      */   {
/* 1031 */     return this.fatalSqlExceptions;
/*      */   }
/*      */ 
/*      */   public String getFatalSqlExceptionWrapper()
/*      */   {
/* 1038 */     return this.fatalSqlExceptionWrapper;
/*      */   }
/*      */ 
/*      */   public void setFatalSqlExceptionWrapper(String fatalSqlExceptionWrapper)
/*      */     throws ProxoolException
/*      */   {
/*      */     try
/*      */     {
/* 1048 */       FatalSqlExceptionHelper.throwFatalSQLException(fatalSqlExceptionWrapper, new SQLException("Test"));
/*      */     }
/*      */     catch (SQLException e)
/*      */     {
/*      */     }
/*      */     catch (RuntimeException e) {
/*      */     }
/* 1055 */     this.fatalSqlExceptionWrapper = fatalSqlExceptionWrapper;
/*      */   }
/*      */ 
/*      */   public String getHouseKeepingTestSql()
/*      */   {
/* 1062 */     return this.houseKeepingTestSql;
/*      */   }
/*      */ 
/*      */   public void setHouseKeepingTestSql(String houseKeepingTestSql)
/*      */   {
/* 1069 */     this.houseKeepingTestSql = houseKeepingTestSql;
/*      */   }
/*      */ 
/*      */   public boolean isTestBeforeUse()
/*      */   {
/* 1076 */     return this.testBeforeUse;
/*      */   }
/*      */ 
/*      */   public void setTestBeforeUse(boolean testBeforeUse)
/*      */   {
/* 1083 */     this.testBeforeUse = testBeforeUse;
/*      */   }
/*      */ 
/*      */   public boolean isTestAfterUse()
/*      */   {
/* 1090 */     return this.testAfterUse;
/*      */   }
/*      */ 
/*      */   public void setTestAfterUse(boolean testAfterUse)
/*      */   {
/* 1097 */     this.testAfterUse = testAfterUse;
/*      */   }
/*      */ 
/*      */   public String getStatistics()
/*      */   {
/* 1104 */     return this.statistics;
/*      */   }
/*      */ 
/*      */   public void setStatistics(String statistics)
/*      */   {
/* 1111 */     this.statistics = statistics;
/*      */   }
/*      */ 
/*      */   public String getStatisticsLogLevel()
/*      */   {
/* 1118 */     return this.statisticsLogLevel;
/*      */   }
/*      */ 
/*      */   public void setStatisticsLogLevel(String statisticsLogLevel)
/*      */   {
/* 1125 */     this.statisticsLogLevel = statisticsLogLevel;
/*      */   }
/*      */ 
/*      */   public String getJndiName()
/*      */   {
/* 1130 */     return this.jndiName;
/*      */   }
/*      */ 
/*      */   public void setJndiName(String jndiName) {
/* 1134 */     this.jndiName = jndiName;
/*      */   }
/*      */ 
/*      */   public String getInitialContextFactory() {
/* 1138 */     return this.initialContextFactory;
/*      */   }
/*      */ 
/*      */   public void setInitialContextFactory(String initialContextFactory) {
/* 1142 */     this.initialContextFactory = initialContextFactory;
/*      */   }
/*      */ 
/*      */   public String getProviderUrl() {
/* 1146 */     return this.providerUrl;
/*      */   }
/*      */ 
/*      */   public void setProviderUrl(String providerUrl) {
/* 1150 */     this.providerUrl = providerUrl;
/*      */   }
/*      */ 
/*      */   public String getSecurityAuthentication() {
/* 1154 */     return this.securityAuthentication;
/*      */   }
/*      */ 
/*      */   public void setSecurityAuthentication(String securityAuthentication) {
/* 1158 */     this.securityAuthentication = securityAuthentication;
/*      */   }
/*      */ 
/*      */   public String getSecurityPrincipal() {
/* 1162 */     return this.securityPrincipal;
/*      */   }
/*      */ 
/*      */   public void setSecurityPrincipal(String securityPrincipal) {
/* 1166 */     this.securityPrincipal = securityPrincipal;
/*      */   }
/*      */ 
/*      */   public String getSecurityCredentials() {
/* 1170 */     return this.securityCredentials;
/*      */   }
/*      */ 
/*      */   public void setSecurityCredentials(String securityCredentials) {
/* 1174 */     this.securityCredentials = securityCredentials;
/*      */   }
/*      */ 
/*      */   public boolean isJmx()
/*      */   {
/* 1182 */     return this.jmx;
/*      */   }
/*      */ 
/*      */   public void setJmx(boolean jmx)
/*      */   {
/* 1189 */     this.jmx = jmx;
/*      */   }
/*      */ 
/*      */   public String getJmxAgentId()
/*      */   {
/* 1196 */     return this.jmxAgentId;
/*      */   }
/*      */ 
/*      */   public void setJmxAgentId(String jmxAgentId)
/*      */   {
/* 1203 */     this.jmxAgentId = jmxAgentId;
/*      */   }
/*      */ 
/*      */   public Class getInjectableConnectionInterface()
/*      */   {
/* 1210 */     return this.injectableConnectionInterface;
/*      */   }
/*      */ 
/*      */   public String getInjectableConnectionInterfaceName()
/*      */   {
/* 1217 */     if (getInjectableConnectionInterface() != null) {
/* 1218 */       return getInjectableConnectionInterface().getName();
/*      */     }
/* 1220 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInjectableConnectionInterfaceName(String injectableConnectionInterfaceName)
/*      */   {
/* 1229 */     this.injectableConnectionInterface = getInterface(injectableConnectionInterfaceName);
/*      */   }
/*      */ 
/*      */   public Class getInjectableStatementInterface()
/*      */   {
/* 1236 */     return this.injectableStatementInterface;
/*      */   }
/*      */ 
/*      */   public String getInjectableStatementInterfaceName()
/*      */   {
/* 1243 */     if (getInjectableStatementInterface() != null) {
/* 1244 */       return getInjectableStatementInterface().getName();
/*      */     }
/* 1246 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInjectableStatementInterfaceName(String injectableStatementInterfaceName)
/*      */   {
/* 1255 */     this.injectableStatementInterface = getInterface(injectableStatementInterfaceName);
/*      */   }
/*      */ 
/*      */   public Class getInjectablePreparedStatementInterface()
/*      */   {
/* 1262 */     return this.injectablePreparedStatementInterface;
/*      */   }
/*      */ 
/*      */   public String getInjectablePreparedStatementInterfaceName()
/*      */   {
/* 1269 */     if (getInjectablePreparedStatementInterface() != null) {
/* 1270 */       return getInjectablePreparedStatementInterface().getName();
/*      */     }
/* 1272 */     return null;
/*      */   }
/*      */ 
/*      */   public void setInjectablePreparedStatementInterfaceName(String injectablePreparedStatementInterfaceName)
/*      */   {
/* 1281 */     this.injectablePreparedStatementInterface = getInterface(injectablePreparedStatementInterfaceName);
/*      */   }
/*      */ 
/*      */   public String getInjectableCallableStatememtInterfaceName()
/*      */   {
/* 1288 */     if (getInjectableCallableStatementInterface() != null) {
/* 1289 */       return getInjectableCallableStatementInterface().getName();
/*      */     }
/* 1291 */     return null;
/*      */   }
/*      */ 
/*      */   public Class getInjectableCallableStatementInterface()
/*      */   {
/* 1299 */     return this.injectableCallableStatementInterface;
/*      */   }
/*      */ 
/*      */   public void setInjectableCallableStatementInterfaceName(String injectableCallableStatementInterfaceName)
/*      */   {
/* 1307 */     this.injectableCallableStatementInterface = getInterface(injectableCallableStatementInterfaceName);
/*      */   }
/*      */ 
/*      */   private Class getInterface(String className) {
/*      */     try {
/* 1312 */       Class clazz = null;
/* 1313 */       if ((className != null) && (className.length() > 0)) {
/* 1314 */         clazz = Class.forName(className);
/* 1315 */         if (!clazz.isInterface()) {
/* 1316 */           throw new IllegalArgumentException(className + " is a class. It must be an interface.");
/*      */         }
/* 1318 */         if (!Modifier.isPublic(clazz.getModifiers())) {
/* 1319 */           throw new IllegalArgumentException(className + " is a protected interface. It must be public.");
/*      */         }
/*      */       }
/* 1322 */       return clazz; } catch (ClassNotFoundException e) {
/*      */     }
/* 1324 */     throw new IllegalArgumentException(className + " couldn't be found");
/*      */   }
/*      */ 
/*      */   public boolean isEqual(String url, Properties info)
/*      */   {
/*      */     try
/*      */     {
/* 1340 */       return !doChange(url, info, true, false);
/*      */     } catch (ProxoolException e) {
/* 1342 */       LOG.error("Problem checking equality", e);
/* 1343 */     }return false;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionPoolDefinition
 * JD-Core Version:    0.6.0
 */
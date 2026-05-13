package org.logicalcobwebs.proxool;

import java.util.Properties;
import java.util.Set;

public abstract interface ConnectionPoolDefinitionIF
{
  public static final int DEFAULT_MAXIMUM_CONNECTION_LIFETIME = 14400000;
  public static final int DEFAULT_MAXIMUM_ACTIVE_TIME = 300000;
  public static final int DEFAULT_PROTOTYPE_COUNT = 0;
  public static final int DEFAULT_MINIMUM_CONNECTION_COUNT = 0;
  public static final int DEFAULT_MAXIMUM_CONNECTION_COUNT = 15;
  public static final int DEFAULT_HOUSE_KEEPING_SLEEP_TIME = 30000;

  /** @deprecated */
  public static final int DEFAULT_MAXIMUM_NEW_CONNECTIONS = 10;
  public static final int DEFAULT_SIMULTANEOUS_BUILD_THROTTLE = 10;
  public static final int DEFAULT_OVERLOAD_WITHOUT_REFUSAL_THRESHOLD = 60000;
  public static final int DEFAULT_RECENTLY_STARTED_THRESHOLD = 60000;
  public static final int DEBUG_LEVEL_QUIET = 0;
  public static final int DEBUG_LEVEL_LOUD = 1;
  public static final String USER_PROPERTY = "user";
  public static final String PASSWORD_PROPERTY = "password";
  public static final String FATAL_SQL_EXCEPTIONS_DELIMITER = ",";

  public abstract long getHouseKeepingSleepTime();

  public abstract int getMaximumConnectionCount();

  public abstract long getMaximumConnectionLifetime();

  /** @deprecated */
  public abstract int getMaximumNewConnections();

  public abstract int getSimultaneousBuildThrottle();

  public abstract int getMinimumConnectionCount();

  /** @deprecated */
  public abstract String getName();

  public abstract String getAlias();

  public abstract String getPassword();

  public abstract int getPrototypeCount();

  public abstract String getUrl();

  public abstract String getUser();

  public abstract String getJdbcDriverVersion();

  /** @deprecated */
  public abstract Properties getProperties();

  public abstract String getDriver();

  public abstract long getRecentlyStartedThreshold();

  public abstract long getOverloadWithoutRefusalLifetime();

  public abstract long getMaximumActiveTime();

  /** @deprecated */
  public abstract int getDebugLevel();

  public abstract Set getFatalSqlExceptions();

  public abstract String getHouseKeepingTestSql();

  public abstract boolean isTestBeforeUse();

  public abstract boolean isTestAfterUse();

  public abstract String getCompleteUrl();

  public abstract boolean isVerbose();

  public abstract boolean isTrace();

  public abstract String getStatistics();

  public abstract String getStatisticsLogLevel();

  public abstract Properties getDelegateProperties();

  public abstract String getDelegateProperty(String paramString);

  public abstract String getFatalSqlExceptionWrapper();

  public abstract String getInitialContextFactory();

  public abstract String getProviderUrl();

  public abstract String getSecurityAuthentication();

  public abstract String getSecurityPrincipal();

  public abstract String getSecurityCredentials();

  public abstract String getJndiName();

  public abstract boolean isJmx();

  public abstract String getJmxAgentId();

  public abstract Class getInjectableConnectionInterface();

  public abstract Class getInjectableStatementInterface();

  public abstract Class getInjectablePreparedStatementInterface();

  public abstract Class getInjectableCallableStatementInterface();
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF
 * JD-Core Version:    0.6.0
 */
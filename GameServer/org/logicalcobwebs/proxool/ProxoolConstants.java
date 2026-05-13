package org.logicalcobwebs.proxool;

public abstract interface ProxoolConstants
{
  public static final String PROXOOL = "proxool";
  public static final String PROXOOL_XML_NAMESPACE_URI = "The latest version is available at http://proxool.sourceforge.net/xml-namespace";
  public static final String ALIAS_DELIMITER = ".";
  public static final String PROPERTY_PREFIX = "proxool.";
  public static final String URL_DELIMITER = ":";
  public static final String USER_PROPERTY = "user";
  public static final String PASSWORD_PROPERTY = "password";
  public static final String ALIAS_PROPERTY = "proxool.alias";
  public static final String DELEGATE_DRIVER = "driver";
  public static final String DELEGATE_DRIVER_PROPERTY = "proxool.driver";
  public static final String DELEGATE_URL = "url";
  public static final String DELEGATE_URL_PROPERTY = "proxool.url";
  public static final String HOUSE_KEEPING_SLEEP_TIME = "house-keeping-sleep-time";
  public static final String HOUSE_KEEPING_SLEEP_TIME_PROPERTY = "proxool.house-keeping-sleep-time";
  public static final String HOUSE_KEEPING_TEST_SQL = "house-keeping-test-sql";
  public static final String HOUSE_KEEPING_TEST_SQL_PROPERTY = "proxool.house-keeping-test-sql";
  public static final String TEST_BEFORE_USE = "test-before-use";
  public static final String TEST_BEFORE_USE_PROPERTY = "proxool.test-before-use";
  public static final String TEST_AFTER_USE = "test-after-use";
  public static final String TEST_AFTER_USE_PROPERTY = "proxool.test-after-use";
  public static final String MAXIMUM_CONNECTION_COUNT = "maximum-connection-count";
  public static final String MAXIMUM_CONNECTION_COUNT_PROPERTY = "proxool.maximum-connection-count";
  public static final String MAXIMUM_CONNECTION_LIFETIME = "maximum-connection-lifetime";
  public static final String MAXIMUM_CONNECTION_LIFETIME_PROPERTY = "proxool.maximum-connection-lifetime";

  /** @deprecated */
  public static final String MAXIMUM_NEW_CONNECTIONS = "maximum-new-connections";

  /** @deprecated */
  public static final String MAXIMUM_NEW_CONNECTIONS_PROPERTY = "proxool.maximum-new-connections";
  public static final String SIMULTANEOUS_BUILD_THROTTLE = "simultaneous-build-throttle";
  public static final String SIMULTANEOUS_BUILD_THROTTLE_PROPERTY = "proxool.simultaneous-build-throttle";
  public static final String MINIMUM_CONNECTION_COUNT = "minimum-connection-count";
  public static final String MINIMUM_CONNECTION_COUNT_PROPERTY = "proxool.minimum-connection-count";
  public static final String PROTOTYPE_COUNT = "prototype-count";
  public static final String PROTOTYPE_COUNT_PROPERTY = "proxool.prototype-count";
  public static final String RECENTLY_STARTED_THRESHOLD = "recently-started-threshold";
  public static final String RECENTLY_STARTED_THRESHOLD_PROPERTY = "proxool.recently-started-threshold";
  public static final String OVERLOAD_WITHOUT_REFUSAL_LIFETIME = "overload-without-refusal-lifetime";
  public static final String OVERLOAD_WITHOUT_REFUSAL_LIFETIME_PROPERTY = "proxool.overload-without-refusal-lifetime";
  public static final String MAXIMUM_ACTIVE_TIME = "maximum-active-time";
  public static final String MAXIMUM_ACTIVE_TIME_PROPERTY = "proxool.maximum-active-time";
  public static final String INJECTABLE_CONNECTION_INTERFACE_NAME = "injectable-connection-interface";
  public static final String INJECTABLE_CONNECTION_INTERFACE_NAME_PROPERTY = "proxool.injectable-connection-interface";
  public static final String INJECTABLE_STATEMENT_INTERFACE_NAME = "injectable-statement-interface";
  public static final String INJECTABLE_STATEMENT_INTERFACE_NAME_PROPERTY = "proxool.injectable-statement-interface";
  public static final String INJECTABLE_PREPARED_STATEMENT_INTERFACE_NAME = "injectable-prepared-statement-interface";
  public static final String INJECTABLE_PREPARED_STATEMENT_INTERFACE_NAME_PROPERTY = "proxool.injectable-prepared-statement-interface";
  public static final String INJECTABLE_CALLABLE_STATEMENT_INTERFACE_NAME = "injectable-callable-statement-interface";
  public static final String INJECTABLE_CALLABLE_STATEMENT_INTERFACE_NAME_PROPERTY = "proxool.injectable-callable-statement-interface";

  /** @deprecated */
  public static final String DEBUG_LEVEL_PROPERTY = "proxool.debug-level";
  public static final String VERBOSE = "verbose";
  public static final String VERBOSE_PROPERTY = "proxool.verbose";
  public static final String TRACE = "trace";
  public static final String TRACE_PROPERTY = "proxool.trace";
  public static final String FATAL_SQL_EXCEPTION = "fatal-sql-exception";
  public static final String FATAL_SQL_EXCEPTION_PROPERTY = "proxool.fatal-sql-exception";
  public static final String FATAL_SQL_EXCEPTION_WRAPPER_CLASS = "fatal-sql-exception-wrapper-class";
  public static final String FATAL_SQL_EXCEPTION_WRAPPER_CLASS_PROPERTY = "proxool.fatal-sql-exception-wrapper-class";
  public static final String STATISTICS = "statistics";
  public static final String STATISTICS_PROPERTY = "proxool.statistics";
  public static final String STATISTICS_LOG_LEVEL = "statistics-log-level";
  public static final String STATISTICS_LOG_LEVEL_PROPERTY = "proxool.statistics-log-level";
  public static final String JNDI_NAME = "jndi-name";
  public static final String JNDI_PROPERTY_PREFIX = "jndi-";
  public static final String JNDI_NAME_PROPERTY = "proxool.jndi-name";
  public static final String STATISTICS_LOG_LEVEL_TRACE = "TRACE";
  public static final String STATISTICS_LOG_LEVEL_DEBUG = "DEBUG";
  public static final String STATISTICS_LOG_LEVEL_INFO = "INFO";
  public static final String DRIVER_PROPERTIES = "driver-properties";
  public static final String JMX = "jmx";
  public static final String JMX_PROPERTY = "proxool.jmx";
  public static final String JMX_AGENT_ID = "jmx-agent-id";
  public static final String JMX_AGENT_PROPERTY = "proxool.jmx-agent-id";
  public static final String ALIAS = "alias";
  public static final String DRIVER_CLASS = "driver-class";
  public static final String DRIVER_CLASS_PROPERTY = "proxool.driver-class";
  public static final String DRIVER_URL = "driver-url";
  public static final String DRIVER_URL_PROPERTY = "proxool.driver-url";
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ProxoolConstants
 * JD-Core Version:    0.6.0
 */
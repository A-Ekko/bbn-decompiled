/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Jdk14Logger;
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.StandardLogger;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.sql.DriverPropertyInfo;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Properties;
/*      */ import java.util.TreeMap;
/*      */ import javax.naming.RefAddr;
/*      */ import javax.naming.Reference;
/*      */ import javax.naming.StringRefAddr;
/*      */ 
/*      */ public class ConnectionProperties
/*      */ {
/*      */   private static final String CONNECTION_AND_AUTH_CATEGORY = "Connection/Authentication";
/*      */   private static final String DEBUGING_PROFILING_CATEGORY = "Debuging/Profiling";
/*      */   private static final String HA_CATEGORY = "High Availability and Clustering";
/*      */   private static final String MISC_CATEGORY = "Miscellaneous";
/*      */   private static final String PERFORMANCE_CATEGORY = "Performance Extensions";
/*      */   private static final String SECURITY_CATEGORY = "Security";
/*  540 */   private static final String[] PROPERTY_CATEGORIES = { "Connection/Authentication", "High Availability and Clustering", "Security", "Performance Extensions", "Debuging/Profiling", "Miscellaneous" };
/*      */ 
/*  544 */   private static final ArrayList PROPERTY_LIST = new ArrayList();
/*      */ 
/*  546 */   private static final String STANDARD_LOGGER_NAME = StandardLogger.class.getName();
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_CONVERT_TO_NULL = "convertToNull";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_EXCEPTION = "exception";
/*      */   protected static final String ZERO_DATETIME_BEHAVIOR_ROUND = "round";
/*  591 */   private BooleanConnectionProperty allowLoadLocalInfile = new BooleanConnectionProperty("allowLoadLocalInfile", true, "Should the driver allow use of 'LOAD DATA LOCAL INFILE...' (defaults to 'true').", "3.0.3", "Security", 2147483647);
/*      */ 
/*  597 */   private BooleanConnectionProperty allowMultiQueries = new BooleanConnectionProperty("allowMultiQueries", false, "Allow the use of ';' to delimit multiple queries during one statement (true/false, defaults to 'false'", "3.1.1", "Security", 1);
/*      */ 
/*  603 */   private BooleanConnectionProperty allowNanAndInf = new BooleanConnectionProperty("allowNanAndInf", false, "Should the driver allow NaN or +/- INF values in PreparedStatement.setDouble()?", "3.1.5", "Miscellaneous", -2147483648);
/*      */ 
/*  609 */   private BooleanConnectionProperty allowUrlInLocalInfile = new BooleanConnectionProperty("allowUrlInLocalInfile", false, "Should the driver allow URLs in 'LOAD DATA LOCAL INFILE' statements?", "3.1.4", "Security", 2147483647);
/*      */ 
/*  615 */   private BooleanConnectionProperty alwaysSendSetIsolation = new BooleanConnectionProperty("alwaysSendSetIsolation", true, "Should the driver always communicate with the database when  Connection.setTransactionIsolation() is called? If set to false, the driver will only communicate with the database when the requested transaction isolation is different than the whichever is newer, the last value that was set via Connection.setTransactionIsolation(), or the value that was read from the server when the connection was established.", "3.1.7", "Performance Extensions", 2147483647);
/*      */ 
/*  627 */   private BooleanConnectionProperty autoDeserialize = new BooleanConnectionProperty("autoDeserialize", false, "Should the driver automatically detect and de-serialize objects stored in BLOB fields?", "3.1.5", "Miscellaneous", -2147483648);
/*      */ 
/*  633 */   private BooleanConnectionProperty autoGenerateTestcaseScript = new BooleanConnectionProperty("autoGenerateTestcaseScript", false, "Should the driver dump the SQL it is executing, including server-side prepared statements to STDERR?", "3.1.9", "Debuging/Profiling", -2147483648);
/*      */ 
/*  639 */   private boolean autoGenerateTestcaseScriptAsBoolean = false;
/*      */ 
/*  641 */   private BooleanConnectionProperty autoReconnect = new BooleanConnectionProperty("autoReconnect", false, "Should the driver try to re-establish stale and/or dead connections?   If enabled the driver will throw an exception for a queries issued on a stale or dead connection,  which belong to the current transaction, but will attempt reconnect before the next query issued on the connection in a new transaction. The use of this feature is not recommended, because it has side effects related to session state and data consistency when applications don'thandle SQLExceptions properly, and is only designed to be used when you are unable to configure your application to handle SQLExceptions resulting from dead andstale connections properly. Alternatively, investigate setting the MySQL server variable \"wait_timeout\"to some high value rather than the default of 8 hours.", "1.1", "High Availability and Clustering", 0);
/*      */ 
/*  655 */   private BooleanConnectionProperty autoReconnectForPools = new BooleanConnectionProperty("autoReconnectForPools", false, "Use a reconnection strategy appropriate for connection pools (defaults to 'false')", "3.1.3", "High Availability and Clustering", 1);
/*      */ 
/*  661 */   private boolean autoReconnectForPoolsAsBoolean = false;
/*      */ 
/*  663 */   private MemorySizeConnectionProperty blobSendChunkSize = new MemorySizeConnectionProperty("blobSendChunkSize", 1048576, 1, 2147483647, "Chunk to use when sending BLOB/CLOBs via ServerPreparedStatements", "3.1.9", "Performance Extensions", -2147483648);
/*      */ 
/*  671 */   private BooleanConnectionProperty cacheCallableStatements = new BooleanConnectionProperty("cacheCallableStmts", false, "Should the driver cache the parsing stage of CallableStatements", "3.1.2", "Performance Extensions", -2147483648);
/*      */ 
/*  676 */   private BooleanConnectionProperty cachePreparedStatements = new BooleanConnectionProperty("cachePrepStmts", false, "Should the driver cache the parsing stage of PreparedStatements of client-side prepared statements, the \"check\" for suitability of server-side prepared  and server-side prepared statements themselves?", "3.0.10", "Performance Extensions", -2147483648);
/*      */ 
/*  684 */   private BooleanConnectionProperty cacheResultSetMetadata = new BooleanConnectionProperty("cacheResultSetMetadata", false, "Should the driver cache ResultSetMetaData for Statements and PreparedStatements? (Req. JDK-1.4+, true/false, default 'false')", "3.1.1", "Performance Extensions", -2147483648);
/*      */   private boolean cacheResultSetMetaDataAsBoolean;
/*  692 */   private BooleanConnectionProperty cacheServerConfiguration = new BooleanConnectionProperty("cacheServerConfiguration", false, "Should the driver cache the results of 'SHOW VARIABLES' and 'SHOW COLLATION' on a per-URL basis?", "3.1.5", "Performance Extensions", -2147483648);
/*      */ 
/*  699 */   private IntegerConnectionProperty callableStatementCacheSize = new IntegerConnectionProperty("callableStmtCacheSize", 100, 0, 2147483647, "If 'cacheCallableStmts' is enabled, how many callable statements should be cached?", "3.1.2", "Performance Extensions", 5);
/*      */ 
/*  707 */   private BooleanConnectionProperty capitalizeTypeNames = new BooleanConnectionProperty("capitalizeTypeNames", false, "Capitalize type names in DatabaseMetaData? (usually only useful when using WebObjects, true/false, defaults to 'false')", "2.0.7", "Miscellaneous", -2147483648);
/*      */ 
/*  713 */   private StringConnectionProperty characterEncoding = new StringConnectionProperty("characterEncoding", null, "If 'useUnicode' is set to true, what character encoding should the driver use when dealing with strings? (defaults is to 'autodetect')", "1.1g", "Miscellaneous", 5);
/*      */ 
/*  719 */   private String characterEncodingAsString = null;
/*      */ 
/*  721 */   private StringConnectionProperty characterSetResults = new StringConnectionProperty("characterSetResults", null, "Character set to tell the server to return results as.", "3.0.13", "Miscellaneous", 6);
/*      */ 
/*  726 */   private BooleanConnectionProperty clobberStreamingResults = new BooleanConnectionProperty("clobberStreamingResults", false, "This will cause a 'streaming' ResultSet to be automatically closed, and any outstanding data still streaming from the server to be discarded if another query is executed before all the data has been read from the server.", "3.0.9", "Miscellaneous", -2147483648);
/*      */ 
/*  734 */   private StringConnectionProperty connectionCollation = new StringConnectionProperty("connectionCollation", null, "If set, tells the server to use this collation via 'set collation_connection'", "3.0.13", "Miscellaneous", 7);
/*      */ 
/*  740 */   private IntegerConnectionProperty connectTimeout = new IntegerConnectionProperty("connectTimeout", 0, 0, 2147483647, "Timeout for socket connect (in milliseconds), with 0 being no timeout. Only works on JDK-1.4 or newer. Defaults to '0'.", "3.0.1", "Connection/Authentication", 9);
/*      */ 
/*  746 */   private BooleanConnectionProperty continueBatchOnError = new BooleanConnectionProperty("continueBatchOnError", true, "Should the driver continue processing batch commands if one statement fails. The JDBC spec allows either way (defaults to 'true').", "3.0.3", "Miscellaneous", -2147483648);
/*      */ 
/*  753 */   private BooleanConnectionProperty createDatabaseIfNotExist = new BooleanConnectionProperty("createDatabaseIfNotExist", false, "Creates the database given in the URL if it doesn't yet exist. Assumes  the configured user has permissions to create databases.", "3.1.9", "Miscellaneous", -2147483648);
/*      */ 
/*  760 */   private BooleanConnectionProperty detectServerPreparedStmts = new BooleanConnectionProperty("useServerPrepStmts", true, "Use server-side prepared statements if the server supports them? (defaults to 'true').", "3.1.0", "Miscellaneous", -2147483648);
/*      */ 
/*  766 */   private BooleanConnectionProperty dontTrackOpenResources = new BooleanConnectionProperty("dontTrackOpenResources", false, "The JDBC specification requires the driver to automatically track and close resources, however if your application doesn't do a good job of explicitly calling close() on statements or result sets, this can cause memory leakage. Setting this property to true relaxes this constraint, and can be more memory efficient for some applications.", "3.1.7", "Performance Extensions", -2147483648);
/*      */ 
/*  777 */   private BooleanConnectionProperty dumpQueriesOnException = new BooleanConnectionProperty("dumpQueriesOnException", false, "Should the driver dump the contents of the query sent to the server in the message for SQLExceptions?", "3.1.3", "Debuging/Profiling", -2147483648);
/*      */ 
/*  783 */   private BooleanConnectionProperty dynamicCalendars = new BooleanConnectionProperty("dynamicCalendars", false, "Should the driver retrieve the default calendar when required, or cache it per connection/session?", "3.1.5", "Performance Extensions", -2147483648);
/*      */ 
/*  790 */   private BooleanConnectionProperty elideSetAutoCommits = new BooleanConnectionProperty("elideSetAutoCommits", false, "If using MySQL-4.1 or newer, should the driver only issue 'set autocommit=n' queries when the server's state doesn't match the requested state by Connection.setAutoCommit(boolean)?", "3.1.3", "Performance Extensions", -2147483648);
/*      */ 
/*  796 */   private BooleanConnectionProperty emptyStringsConvertToZero = new BooleanConnectionProperty("emptyStringsConvertToZero", true, "Should the driver allow conversions from empty string fields to numeric values of '0'?", "3.1.8", "Miscellaneous", -2147483648);
/*      */ 
/*  802 */   private BooleanConnectionProperty emulateLocators = new BooleanConnectionProperty("emulateLocators", false, "N/A", "3.1.0", "Miscellaneous", -2147483648);
/*      */ 
/*  806 */   private BooleanConnectionProperty emulateUnsupportedPstmts = new BooleanConnectionProperty("emulateUnsupportedPstmts", true, "Should the driver detect prepared statements that are not supported by the server, and replace them with client-side emulated versions?", "3.1.7", "Miscellaneous", -2147483648);
/*      */ 
/*  813 */   private BooleanConnectionProperty enableDeprecatedAutoreconnect = new BooleanConnectionProperty("enableDeprecatedAutoreconnect", false, "Auto-reconnect functionality is deprecated starting with version 3.2, and will be removed in version 3.3. Set this property to 'true' to disable the check for the feature being configured.", "3.2.1", "High Availability and Clustering", -2147483648);
/*      */ 
/*  820 */   private BooleanConnectionProperty enablePacketDebug = new BooleanConnectionProperty("enablePacketDebug", false, "When enabled, a ring-buffer of 'packetDebugBufferSize' packets will be kept, and dumped when exceptions are thrown in key areas in the driver's code", "3.1.3", "Debuging/Profiling", -2147483648);
/*      */ 
/*  826 */   private BooleanConnectionProperty explainSlowQueries = new BooleanConnectionProperty("explainSlowQueries", false, "If 'logSlowQueries' is enabled, should the driver automatically issue an 'EXPLAIN' on the server and send the results to the configured log at a WARN level?", "3.1.2", "Debuging/Profiling", -2147483648);
/*      */ 
/*  834 */   private BooleanConnectionProperty failOverReadOnly = new BooleanConnectionProperty("failOverReadOnly", true, "When failing over in autoReconnect mode, should the connection be set to 'read-only'?", "3.0.12", "High Availability and Clustering", 2);
/*      */ 
/*  840 */   private BooleanConnectionProperty gatherPerformanceMetrics = new BooleanConnectionProperty("gatherPerfMetrics", false, "Should the driver gather performance metrics, and report them via the configured logger every 'reportMetricsIntervalMillis' milliseconds?", "3.1.2", "Debuging/Profiling", 1);
/*      */ 
/*  846 */   private boolean highAvailabilityAsBoolean = false;
/*      */ 
/*  848 */   private BooleanConnectionProperty holdResultsOpenOverStatementClose = new BooleanConnectionProperty("holdResultsOpenOverStatementClose", false, "Should the driver close result sets on Statement.close() as required by the JDBC specification?", "3.1.7", "Performance Extensions", -2147483648);
/*      */ 
/*  854 */   private BooleanConnectionProperty ignoreNonTxTables = new BooleanConnectionProperty("ignoreNonTxTables", false, "Ignore non-transactional table warning for rollback? (defaults to 'false').", "3.0.9", "Miscellaneous", -2147483648);
/*      */ 
/*  860 */   private IntegerConnectionProperty initialTimeout = new IntegerConnectionProperty("initialTimeout", 2, 1, 2147483647, "If autoReconnect is enabled, the initial time to wait between re-connect attempts (in seconds, defaults to '2').", "1.1", "High Availability and Clustering", 5);
/*      */ 
/*  867 */   private BooleanConnectionProperty isInteractiveClient = new BooleanConnectionProperty("interactiveClient", false, "Set the CLIENT_INTERACTIVE flag, which tells MySQL to timeout connections based on INTERACTIVE_TIMEOUT instead of WAIT_TIMEOUT", "3.1.0", "Connection/Authentication", -2147483648);
/*      */ 
/*  874 */   private BooleanConnectionProperty jdbcCompliantTruncation = new BooleanConnectionProperty("jdbcCompliantTruncation", true, "Should the driver throw java.sql.DataTruncation exceptions when data is truncated as is required by the JDBC specification when connected to a server that supports warnings(MySQL 4.1.0 and newer)?", "3.1.2", "Miscellaneous", -2147483648);
/*      */ 
/*  882 */   private MemorySizeConnectionProperty locatorFetchBufferSize = new MemorySizeConnectionProperty("locatorFetchBufferSize", 1048576, 0, 2147483647, "If 'emulateLocators' is configured to 'true', what size  buffer should be used when fetching BLOB data for getBinaryInputStream?", "3.2.1", "Performance Extensions", -2147483648);
/*      */ 
/*  891 */   private StringConnectionProperty loggerClassName = new StringConnectionProperty("logger", STANDARD_LOGGER_NAME, "The name of a class that implements '" + Log.class.getName() + "' that will be used to log messages to." + "(default is '" + STANDARD_LOGGER_NAME + "', which " + "logs to STDERR)", "3.1.1", "Debuging/Profiling", 0);
/*      */ 
/*  899 */   private BooleanConnectionProperty logSlowQueries = new BooleanConnectionProperty("logSlowQueries", false, "Should queries that take longer than 'slowQueryThresholdMillis' be logged?", "3.1.2", "Debuging/Profiling", -2147483648);
/*      */ 
/*  905 */   private BooleanConnectionProperty maintainTimeStats = new BooleanConnectionProperty("maintainTimeStats", true, "Should the driver maintain various internal timers to enable idle time calculations as well as more verbose error messages when the connection to the server fails? Setting this property to false removes at least two calls to System.getCurrentTimeMillis() per query.", "3.1.9", "Performance Extensions", 2147483647);
/*      */ 
/*  915 */   private boolean maintainTimeStatsAsBoolean = true;
/*      */ 
/*  917 */   private IntegerConnectionProperty maxQuerySizeToLog = new IntegerConnectionProperty("maxQuerySizeToLog", 2048, 0, 2147483647, "Controls the maximum length/size of a query that will get logged when profiling or tracing", "3.1.3", "Debuging/Profiling", 4);
/*      */ 
/*  925 */   private IntegerConnectionProperty maxReconnects = new IntegerConnectionProperty("maxReconnects", 3, 1, 2147483647, "Maximum number of reconnects to attempt if autoReconnect is true, default is '3'.", "1.1", "High Availability and Clustering", 4);
/*      */ 
/*  933 */   private IntegerConnectionProperty maxRows = new IntegerConnectionProperty("maxRows", -1, -1, 2147483647, "The maximum number of rows to return  (0, the default means return all rows).", "all versions", "Miscellaneous", -2147483648);
/*      */ 
/*  939 */   private int maxRowsAsInt = -1;
/*      */ 
/*  941 */   private IntegerConnectionProperty metadataCacheSize = new IntegerConnectionProperty("metadataCacheSize", 50, 1, 2147483647, "The number of queries to cacheResultSetMetadata for if cacheResultSetMetaData is set to 'true' (default 50)", "3.1.1", "Performance Extensions", 5);
/*      */ 
/*  950 */   private BooleanConnectionProperty noDatetimeStringSync = new BooleanConnectionProperty("noDatetimeStringSync", false, "Don't ensure that ResultSet.getDatetimeType().toString().equals(ResultSet.getString())", "3.1.7", "Miscellaneous", -2147483648);
/*      */ 
/*  956 */   private BooleanConnectionProperty nullCatalogMeansCurrent = new BooleanConnectionProperty("nullCatalogMeansCurrent", true, "When DatabaseMetadataMethods ask for a 'catalog' parameter, does the value null mean use the current catalog? (this is not JDBC-compliant, but follows legacy behavior from earlier versions of the driver)", "3.1.8", "Miscellaneous", -2147483648);
/*      */ 
/*  963 */   private BooleanConnectionProperty nullNamePatternMatchesAll = new BooleanConnectionProperty("nullNamePatternMatchesAll", true, "Should DatabaseMetaData methods that accept *pattern parameters treat null the same as '%'  (this is not JDBC-compliant, however older versions of the driver accepted this departure from the specification)", "3.1.8", "Miscellaneous", -2147483648);
/*      */ 
/*  970 */   private IntegerConnectionProperty packetDebugBufferSize = new IntegerConnectionProperty("packetDebugBufferSize", 20, 0, 2147483647, "The maximum number of packets to retain when 'enablePacketDebug' is true", "3.1.3", "Debuging/Profiling", 7);
/*      */ 
/*  978 */   private BooleanConnectionProperty paranoid = new BooleanConnectionProperty("paranoid", false, "Take measures to prevent exposure sensitive information in error messages and clear data structures holding sensitive data when possible? (defaults to 'false')", "3.0.1", "Security", -2147483648);
/*      */ 
/*  985 */   private BooleanConnectionProperty pedantic = new BooleanConnectionProperty("pedantic", false, "Follow the JDBC spec to the letter.", "3.0.0", "Miscellaneous", -2147483648);
/*      */ 
/*  989 */   private IntegerConnectionProperty preparedStatementCacheSize = new IntegerConnectionProperty("prepStmtCacheSize", 25, 0, 2147483647, "If prepared statement caching is enabled, how many prepared statements should be cached?", "3.0.10", "Performance Extensions", 10);
/*      */ 
/*  995 */   private IntegerConnectionProperty preparedStatementCacheSqlLimit = new IntegerConnectionProperty("prepStmtCacheSqlLimit", 256, 1, 2147483647, "If prepared statement caching is enabled, what's the largest SQL the driver will cache the parsing for?", "3.0.10", "Performance Extensions", 11);
/*      */ 
/* 1004 */   private StringConnectionProperty profileSql = new StringConnectionProperty("profileSql", null, "Deprecated, use 'profileSQL' instead. Trace queries and their execution/fetch times on STDERR (true/false) defaults to 'false'", "2.0.14", "Debuging/Profiling", 3);
/*      */ 
/* 1010 */   private BooleanConnectionProperty profileSQL = new BooleanConnectionProperty("profileSQL", false, "Trace queries and their execution/fetch times to the configured logger (true/false) defaults to 'false'", "3.1.0", "Debuging/Profiling", 1);
/*      */ 
/* 1016 */   private boolean profileSQLAsBoolean = false;
/*      */ 
/* 1018 */   private StringConnectionProperty propertiesTransform = new StringConnectionProperty("propertiesTransform", null, "An implementation of com.mysql.jdbc.ConnectionPropertiesTransform that the driver will use to modify URL properties passed to the driver before attempting a connection", "3.1.4", "Connection/Authentication", -2147483648);
/*      */ 
/* 1024 */   private IntegerConnectionProperty queriesBeforeRetryMaster = new IntegerConnectionProperty("queriesBeforeRetryMaster", 50, 1, 2147483647, "Number of queries to issue before falling back to master when failed over (when using multi-host failover). Whichever condition is met first, 'queriesBeforeRetryMaster' or 'secondsBeforeRetryMaster' will cause an attempt to be made to reconnect to the master. Defaults to 50.", "3.0.2", "High Availability and Clustering", 7);
/*      */ 
/* 1035 */   private BooleanConnectionProperty reconnectAtTxEnd = new BooleanConnectionProperty("reconnectAtTxEnd", false, "If autoReconnect is set to true, should the driver attempt reconnectionsat the end of every transaction?", "3.0.10", "High Availability and Clustering", 4);
/*      */ 
/* 1041 */   private boolean reconnectTxAtEndAsBoolean = false;
/*      */ 
/* 1043 */   private BooleanConnectionProperty relaxAutoCommit = new BooleanConnectionProperty("relaxAutoCommit", false, "If the version of MySQL the driver connects to does not support transactions, still allow calls to commit(), rollback() and setAutoCommit() (true/false, defaults to 'false')?", "2.0.13", "Miscellaneous", -2147483648);
/*      */ 
/* 1049 */   private IntegerConnectionProperty reportMetricsIntervalMillis = new IntegerConnectionProperty("reportMetricsIntervalMillis", 30000, 0, 2147483647, "If 'gatherPerfMetrics' is enabled, how often should they be logged (in ms)?", "3.1.2", "Debuging/Profiling", 3);
/*      */ 
/* 1057 */   private BooleanConnectionProperty requireSSL = new BooleanConnectionProperty("requireSSL", false, "Require SSL connection if useSSL=true? (defaults to 'false').", "3.1.0", "Security", 3);
/*      */ 
/* 1062 */   private BooleanConnectionProperty retainStatementAfterResultSetClose = new BooleanConnectionProperty("retainStatementAfterResultSetClose", false, "Should the driver retain the Statement reference in a ResultSet after ResultSet.close() has been called. This is not JDBC-compliant after JDBC-4.0.", "3.1.11", "Miscellaneous", -2147483648);
/*      */ 
/* 1069 */   private BooleanConnectionProperty rollbackOnPooledClose = new BooleanConnectionProperty("rollbackOnPooledClose", true, "Should the driver issue a rollback() when the logical connection in a pool is closed?", "3.0.15", "Miscellaneous", -2147483648);
/*      */ 
/* 1075 */   private BooleanConnectionProperty roundRobinLoadBalance = new BooleanConnectionProperty("roundRobinLoadBalance", false, "When autoReconnect is enabled, and failoverReadonly is false, should we pick hosts to connect to on a round-robin basis?", "3.1.2", "High Availability and Clustering", 5);
/*      */ 
/* 1081 */   private BooleanConnectionProperty runningCTS13 = new BooleanConnectionProperty("runningCTS13", false, "Enables workarounds for bugs in Sun's JDBC compliance testsuite version 1.3", "3.1.7", "Miscellaneous", -2147483648);
/*      */ 
/* 1087 */   private IntegerConnectionProperty secondsBeforeRetryMaster = new IntegerConnectionProperty("secondsBeforeRetryMaster", 30, 1, 2147483647, "How long should the driver wait, when failed over, before attempting to reconnect to the master server? Whichever condition is met first, 'queriesBeforeRetryMaster' or 'secondsBeforeRetryMaster' will cause an attempt to be made to reconnect to the master. Time in seconds, defaults to 30", "3.0.2", "High Availability and Clustering", 8);
/*      */ 
/* 1098 */   private StringConnectionProperty serverTimezone = new StringConnectionProperty("serverTimezone", null, "Override detection/mapping of timezone. Used when timezone from server doesn't map to Java timezone", "3.0.2", "Miscellaneous", -2147483648);
/*      */ 
/* 1104 */   private StringConnectionProperty sessionVariables = new StringConnectionProperty("sessionVariables", null, "A comma-separated list of name/value pairs to be sent as SET SESSION ... to  the server when the driver connects.", "3.1.8", "Miscellaneous", 2147483647);
/*      */ 
/* 1110 */   private IntegerConnectionProperty slowQueryThresholdMillis = new IntegerConnectionProperty("slowQueryThresholdMillis", 2000, 0, 2147483647, "If 'logSlowQueries' is enabled, how long should a query (in ms) before it is logged as 'slow'?", "3.1.2", "Debuging/Profiling", 9);
/*      */ 
/* 1118 */   private StringConnectionProperty socketFactoryClassName = new StringConnectionProperty("socketFactory", StandardSocketFactory.class.getName(), "The name of the class that the driver should use for creating socket connections to the server. This class must implement the interface 'com.mysql.jdbc.SocketFactory' and have public no-args constructor.", "3.0.3", "Connection/Authentication", 4);
/*      */ 
/* 1124 */   private IntegerConnectionProperty socketTimeout = new IntegerConnectionProperty("socketTimeout", 0, 0, 2147483647, "Timeout on network socket operations (0, the default means no timeout).", "3.0.1", "Connection/Authentication", 10);
/*      */ 
/* 1132 */   private BooleanConnectionProperty strictFloatingPoint = new BooleanConnectionProperty("strictFloatingPoint", false, "Used only in older versions of compliance test", "3.0.0", "Miscellaneous", -2147483648);
/*      */ 
/* 1137 */   private BooleanConnectionProperty strictUpdates = new BooleanConnectionProperty("strictUpdates", true, "Should the driver do strict checking (all primary keys selected) of updatable result sets (true, false, defaults to 'true')?", "3.0.4", "Miscellaneous", -2147483648);
/*      */ 
/* 1143 */   private BooleanConnectionProperty tinyInt1isBit = new BooleanConnectionProperty("tinyInt1isBit", true, "Should the driver treat the datatype TINYINT(1) as the BIT type (because the server silently converts BIT -> TINYINT(1) when creating tables)?", "3.0.16", "Miscellaneous", -2147483648);
/*      */ 
/* 1150 */   private BooleanConnectionProperty traceProtocol = new BooleanConnectionProperty("traceProtocol", false, "Should trace-level network protocol be logged?", "3.1.2", "Debuging/Profiling", -2147483648);
/*      */ 
/* 1155 */   private BooleanConnectionProperty transformedBitIsBoolean = new BooleanConnectionProperty("transformedBitIsBoolean", false, "If the driver converts TINYINT(1) to a different type, should it use BOOLEAN instead of BIT  for future compatibility with MySQL-5.0, as MySQL-5.0 has a BIT type?", "3.1.9", "Miscellaneous", -2147483648);
/*      */ 
/* 1162 */   private BooleanConnectionProperty useCompression = new BooleanConnectionProperty("useCompression", false, "Use zlib compression when communicating with the server (true/false)? Defaults to 'false'.", "3.0.17", "Connection/Authentication", -2147483648);
/*      */ 
/* 1168 */   private StringConnectionProperty useConfig = new StringConnectionProperty("useConfigs", null, "Load the comma-delimited list of configuration properties before parsing the URL or applying user-specified properties. These configurations are explained in the 'Configurations' of the documentation.", "3.1.5", "Connection/Authentication", 2147483647);
/*      */ 
/* 1175 */   private BooleanConnectionProperty useFastIntParsing = new BooleanConnectionProperty("useFastIntParsing", true, "Use internal String->Integer conversion routines to avoid excessive object creation?", "3.1.4", "Performance Extensions", -2147483648);
/*      */ 
/* 1181 */   private BooleanConnectionProperty useHostsInPrivileges = new BooleanConnectionProperty("useHostsInPrivileges", true, "Add '@hostname' to users in DatabaseMetaData.getColumn/TablePrivileges() (true/false), defaults to 'true'.", "3.0.2", "Miscellaneous", -2147483648);
/*      */ 
/* 1187 */   private BooleanConnectionProperty useLocalSessionState = new BooleanConnectionProperty("useLocalSessionState", false, "Should the driver refer to the internal values of autocommit and transaction isolation that are set  by Connection.setAutoCommit() and Connection.setTransactionIsolation(), rather than querying the database?", "3.1.7", "Performance Extensions", -2147483648);
/*      */ 
/* 1194 */   private BooleanConnectionProperty useNewIo = new BooleanConnectionProperty("useNewIO", false, "Should the driver use the java.nio.* interfaces for network communication (true/false), defaults to 'false'", "3.1.0", "Performance Extensions", -2147483648);
/*      */ 
/* 1200 */   private BooleanConnectionProperty useOldUTF8Behavior = new BooleanConnectionProperty("useOldUTF8Behavior", false, "Use the UTF-8 behavior the driver did when communicating with 4.0 and older servers", "3.1.6", "Miscellaneous", -2147483648);
/*      */ 
/* 1206 */   private boolean useOldUTF8BehaviorAsBoolean = false;
/*      */ 
/* 1208 */   private BooleanConnectionProperty useOnlyServerErrorMessages = new BooleanConnectionProperty("useOnlyServerErrorMessages", true, "Don't prepend 'standard' SQLState error messages to error messages returned by the server.", "3.0.15", "Miscellaneous", -2147483648);
/*      */ 
/* 1214 */   private BooleanConnectionProperty useReadAheadInput = new BooleanConnectionProperty("useReadAheadInput", true, "Use newer, optimized non-blocking, buffered input stream when reading from the server?", "3.1.5", "Performance Extensions", -2147483648);
/*      */ 
/* 1220 */   private BooleanConnectionProperty useSqlStateCodes = new BooleanConnectionProperty("useSqlStateCodes", true, "Use SQL Standard state codes instead of 'legacy' X/Open/SQL state codes (true/false), default is 'true'", "3.1.3", "Miscellaneous", -2147483648);
/*      */ 
/* 1226 */   private BooleanConnectionProperty useSSL = new BooleanConnectionProperty("useSSL", false, "Use SSL when communicating with the server (true/false), defaults to 'false'", "3.0.2", "Security", 2);
/*      */ 
/* 1232 */   private BooleanConnectionProperty useStreamLengthsInPrepStmts = new BooleanConnectionProperty("useStreamLengthsInPrepStmts", true, "Honor stream length parameter in PreparedStatement/ResultSet.setXXXStream() method calls (true/false, defaults to 'true')?", "3.0.2", "Miscellaneous", -2147483648);
/*      */ 
/* 1239 */   private BooleanConnectionProperty useTimezone = new BooleanConnectionProperty("useTimezone", false, "Convert time/date types between client and server timezones (true/false, defaults to 'false')?", "3.0.2", "Miscellaneous", -2147483648);
/*      */ 
/* 1245 */   private BooleanConnectionProperty useUltraDevWorkAround = new BooleanConnectionProperty("ultraDevHack", false, "Create PreparedStatements for prepareCall() when required, because UltraDev  is broken and issues a prepareCall() for _all_ statements? (true/false, defaults to 'false')", "2.0.3", "Miscellaneous", -2147483648);
/*      */ 
/* 1252 */   private BooleanConnectionProperty useUnbufferedInput = new BooleanConnectionProperty("useUnbufferedInput", true, "Don't use BufferedInputStream for reading data from the server", "3.0.11", "Miscellaneous", -2147483648);
/*      */ 
/* 1257 */   private BooleanConnectionProperty useUnicode = new BooleanConnectionProperty("useUnicode", false, "Should the driver use Unicode character encodings when handling strings? Should only be used when the driver can't determine the character set mapping, or you are trying to 'force' the driver to use a character set that MySQL either doesn't natively support (such as UTF-8), true/false, defaults to 'true'", "1.1g", "Miscellaneous", 0);
/*      */ 
/* 1264 */   private boolean useUnicodeAsBoolean = true;
/*      */ 
/* 1266 */   private BooleanConnectionProperty useUsageAdvisor = new BooleanConnectionProperty("useUsageAdvisor", false, "Should the driver issue 'usage' warnings advising proper and efficient usage of JDBC and MySQL Connector/J to the log (true/false, defaults to 'false')?", "3.1.1", "Debuging/Profiling", 10);
/*      */ 
/* 1272 */   private boolean useUsageAdvisorAsBoolean = false;
/*      */ 
/* 1274 */   private BooleanConnectionProperty yearIsDateType = new BooleanConnectionProperty("yearIsDateType", true, "Should the JDBC driver treat the MySQL type \"YEAR\" as a java.sql.Date, or as a SHORT?", "3.1.9", "Miscellaneous", -2147483648);
/*      */ 
/* 1280 */   private StringConnectionProperty zeroDateTimeBehavior = new StringConnectionProperty("zeroDateTimeBehavior", "exception", new String[] { "exception", "round", "convertToNull" }, "What should happen when the driver encounters DATETIME values that are composed entirely of zeroes (used by MySQL to represent invalid dates)? Valid values are 'exception', 'round' and 'convertToNull'.", "3.1.4", "Miscellaneous", -2147483648);
/*      */ 
/*      */   protected static DriverPropertyInfo[] exposeAsDriverPropertyInfo(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/*  587 */     return new ConnectionProperties() {  }
/*  587 */     .exposeAsDriverPropertyInfoInternal(info, slotsToReserve);
/*      */   }
/*      */ 
/*      */   protected DriverPropertyInfo[] exposeAsDriverPropertyInfoInternal(Properties info, int slotsToReserve)
/*      */     throws SQLException
/*      */   {
/* 1298 */     initializeProperties(info);
/*      */ 
/* 1300 */     int numProperties = PROPERTY_LIST.size();
/*      */ 
/* 1302 */     int listSize = numProperties + slotsToReserve;
/*      */ 
/* 1304 */     DriverPropertyInfo[] driverProperties = new DriverPropertyInfo[listSize];
/*      */ 
/* 1306 */     for (int i = slotsToReserve; i < listSize; i++) {
/* 1307 */       Field propertyField = (Field)PROPERTY_LIST.get(i - slotsToReserve);
/*      */       try
/*      */       {
/* 1311 */         ConnectionProperty propToExpose = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1314 */         if (info != null) {
/* 1315 */           propToExpose.initializeFrom(info);
/*      */         }
/*      */ 
/* 1318 */         propToExpose.syncDriverPropertyInfo();
/* 1319 */         driverProperties[i] = propToExpose;
/*      */       } catch (IllegalAccessException iae) {
/* 1321 */         throw new SQLException("Internal properties failure", "S1000");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1326 */     return driverProperties;
/*      */   }
/*      */ 
/*      */   protected Properties exposeAsProperties(Properties info) throws SQLException
/*      */   {
/* 1331 */     if (info == null) {
/* 1332 */       info = new Properties();
/*      */     }
/*      */ 
/* 1335 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1337 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 1338 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 1342 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1345 */         Object propValue = propToGet.getValueAsObject();
/*      */ 
/* 1347 */         if (propValue != null)
/* 1348 */           info.setProperty(propToGet.getPropertyName(), propValue.toString());
/*      */       }
/*      */       catch (IllegalAccessException iae)
/*      */       {
/* 1352 */         throw new SQLException("Internal properties failure", "S1000");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1357 */     return info;
/*      */   }
/*      */ 
/*      */   public String exposeAsXml()
/*      */     throws SQLException
/*      */   {
/* 1368 */     StringBuffer xmlBuf = new StringBuffer();
/* 1369 */     xmlBuf.append("<ConnectionProperties>");
/*      */ 
/* 1371 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 1373 */     int numCategories = PROPERTY_CATEGORIES.length;
/*      */ 
/* 1375 */     Map propertyListByCategory = new HashMap();
/*      */ 
/* 1377 */     for (int i = 0; i < numCategories; i++) {
/* 1378 */       propertyListByCategory.put(PROPERTY_CATEGORIES[i], new Map[] { new TreeMap(), new TreeMap() });
/*      */     }
/*      */ 
/* 1388 */     StringConnectionProperty userProp = new StringConnectionProperty("user", null, "The user to connect as", "all", "Connection/Authentication", -2147483647);
/*      */ 
/* 1392 */     StringConnectionProperty passwordProp = new StringConnectionProperty("password", null, "The password to use when connecting", "all", "Connection/Authentication", -2147483646);
/*      */ 
/* 1397 */     Map[] connectionSortMaps = (Map[])propertyListByCategory.get("Connection/Authentication");
/*      */ 
/* 1399 */     connectionSortMaps[0].put(new Integer(userProp.getOrder()), userProp);
/* 1400 */     connectionSortMaps[0].put(new Integer(passwordProp.getOrder()), passwordProp);
/*      */     try
/*      */     {
/* 1404 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 1405 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */ 
/* 1407 */         ConnectionProperty propToGet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 1409 */         Map[] sortMaps = (Map[])propertyListByCategory.get(propToGet.getCategoryName());
/*      */ 
/* 1411 */         int orderInCategory = propToGet.getOrder();
/*      */ 
/* 1413 */         if (orderInCategory == -2147483648)
/* 1414 */           sortMaps[1].put(propToGet.getPropertyName(), propToGet);
/*      */         else {
/* 1416 */           sortMaps[0].put(new Integer(orderInCategory), propToGet);
/*      */         }
/*      */       }
/*      */ 
/* 1420 */       for (int j = 0; j < numCategories; j++) {
/* 1421 */         Map[] sortMaps = (Map[])propertyListByCategory.get(PROPERTY_CATEGORIES[j]);
/*      */ 
/* 1423 */         Iterator orderedIter = sortMaps[0].values().iterator();
/* 1424 */         Iterator alphaIter = sortMaps[1].values().iterator();
/*      */ 
/* 1426 */         xmlBuf.append("\n <PropertyCategory name=\"");
/* 1427 */         xmlBuf.append(PROPERTY_CATEGORIES[j]);
/* 1428 */         xmlBuf.append("\">");
/*      */ 
/* 1430 */         while (orderedIter.hasNext()) {
/* 1431 */           ConnectionProperty propToGet = (ConnectionProperty)orderedIter.next();
/*      */ 
/* 1433 */           propToGet.syncDriverPropertyInfo();
/*      */ 
/* 1435 */           xmlBuf.append("\n  <Property name=\"");
/* 1436 */           xmlBuf.append(propToGet.getPropertyName());
/* 1437 */           xmlBuf.append("\" required=\"");
/* 1438 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1440 */           xmlBuf.append("\" default=\"");
/*      */ 
/* 1442 */           if (propToGet.getDefaultValue() != null) {
/* 1443 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */ 
/* 1446 */           xmlBuf.append("\" sortOrder=\"");
/* 1447 */           xmlBuf.append(propToGet.getOrder());
/* 1448 */           xmlBuf.append("\" since=\"");
/* 1449 */           xmlBuf.append(propToGet.sinceVersion);
/* 1450 */           xmlBuf.append("\">\n");
/* 1451 */           xmlBuf.append("    ");
/* 1452 */           xmlBuf.append(propToGet.description);
/* 1453 */           xmlBuf.append("\n  </Property>");
/*      */         }
/*      */ 
/* 1456 */         while (alphaIter.hasNext()) {
/* 1457 */           ConnectionProperty propToGet = (ConnectionProperty)alphaIter.next();
/*      */ 
/* 1459 */           propToGet.syncDriverPropertyInfo();
/*      */ 
/* 1461 */           xmlBuf.append("\n  <Property name=\"");
/* 1462 */           xmlBuf.append(propToGet.getPropertyName());
/* 1463 */           xmlBuf.append("\" required=\"");
/* 1464 */           xmlBuf.append(propToGet.required ? "Yes" : "No");
/*      */ 
/* 1466 */           xmlBuf.append("\" default=\"");
/*      */ 
/* 1468 */           if (propToGet.getDefaultValue() != null) {
/* 1469 */             xmlBuf.append(propToGet.getDefaultValue());
/*      */           }
/*      */ 
/* 1472 */           xmlBuf.append("\" sortOrder=\"alpha\" since=\"");
/* 1473 */           xmlBuf.append(propToGet.sinceVersion);
/* 1474 */           xmlBuf.append("\">\n");
/* 1475 */           xmlBuf.append("    ");
/* 1476 */           xmlBuf.append(propToGet.description);
/* 1477 */           xmlBuf.append("\n  </Property>");
/*      */         }
/*      */ 
/* 1480 */         xmlBuf.append("\n </PropertyCategory>");
/*      */       }
/*      */     } catch (IllegalAccessException iae) {
/* 1483 */       throw new SQLException("Internal properties failure", "S1000");
/*      */     }
/*      */ 
/* 1487 */     xmlBuf.append("\n</ConnectionProperties>");
/*      */ 
/* 1489 */     return xmlBuf.toString();
/*      */   }
/*      */ 
/*      */   public boolean getAllowLoadLocalInfile()
/*      */   {
/* 1498 */     return this.allowLoadLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowMultiQueries()
/*      */   {
/* 1507 */     return this.allowMultiQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getAllowNanAndInf()
/*      */   {
/* 1514 */     return this.allowNanAndInf.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAllowUrlInLocalInfile()
/*      */   {
/* 1521 */     return this.allowUrlInLocalInfile.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAlwaysSendSetIsolation()
/*      */   {
/* 1528 */     return this.alwaysSendSetIsolation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoDeserialize()
/*      */   {
/* 1535 */     return this.autoDeserialize.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getAutoGenerateTestcaseScript() {
/* 1539 */     return this.autoGenerateTestcaseScriptAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getAutoReconnectForPools()
/*      */   {
/* 1548 */     return this.autoReconnectForPoolsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getBlobSendChunkSize()
/*      */   {
/* 1555 */     return this.blobSendChunkSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCacheCallableStatements()
/*      */   {
/* 1564 */     return this.cacheCallableStatements.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCachePreparedStatements()
/*      */   {
/* 1573 */     return ((Boolean)this.cachePreparedStatements.getValueAsObject()).booleanValue();
/*      */   }
/*      */ 
/*      */   public boolean getCacheResultSetMetadata()
/*      */   {
/* 1583 */     return this.cacheResultSetMetaDataAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getCacheServerConfiguration()
/*      */   {
/* 1590 */     return this.cacheServerConfiguration.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getCallableStatementCacheSize()
/*      */   {
/* 1599 */     return this.callableStatementCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getCapitalizeTypeNames()
/*      */   {
/* 1608 */     return this.capitalizeTypeNames.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getCharacterSetResults()
/*      */   {
/* 1617 */     return this.characterSetResults.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getClobberStreamingResults()
/*      */   {
/* 1626 */     return this.clobberStreamingResults.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getConnectionCollation()
/*      */   {
/* 1635 */     return this.connectionCollation.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getConnectTimeout()
/*      */   {
/* 1644 */     return this.connectTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getContinueBatchOnError()
/*      */   {
/* 1653 */     return this.continueBatchOnError.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getCreateDatabaseIfNotExist() {
/* 1657 */     return this.createDatabaseIfNotExist.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDontTrackOpenResources()
/*      */   {
/* 1664 */     return this.dontTrackOpenResources.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDumpQueriesOnException()
/*      */   {
/* 1673 */     return this.dumpQueriesOnException.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getDynamicCalendars()
/*      */   {
/* 1680 */     return this.dynamicCalendars.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getElideSetAutoCommits()
/*      */   {
/* 1689 */     return this.elideSetAutoCommits.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmptyStringsConvertToZero() {
/* 1693 */     return this.emptyStringsConvertToZero.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateLocators()
/*      */   {
/* 1702 */     return this.emulateLocators.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEmulateUnsupportedPstmts()
/*      */   {
/* 1709 */     return this.emulateUnsupportedPstmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getEnablePacketDebug()
/*      */   {
/* 1718 */     return this.enablePacketDebug.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected String getEncoding()
/*      */   {
/* 1727 */     return this.characterEncodingAsString;
/*      */   }
/*      */ 
/*      */   public boolean getExplainSlowQueries()
/*      */   {
/* 1736 */     return this.explainSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getFailOverReadOnly()
/*      */   {
/* 1745 */     return this.failOverReadOnly.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getGatherPerformanceMetrics()
/*      */   {
/* 1754 */     return this.gatherPerformanceMetrics.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getHighAvailability()
/*      */   {
/* 1763 */     return this.highAvailabilityAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getHoldResultsOpenOverStatementClose()
/*      */   {
/* 1770 */     return this.holdResultsOpenOverStatementClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIgnoreNonTxTables()
/*      */   {
/* 1779 */     return this.ignoreNonTxTables.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getInitialTimeout()
/*      */   {
/* 1788 */     return this.initialTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getInteractiveClient()
/*      */   {
/* 1797 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getIsInteractiveClient()
/*      */   {
/* 1806 */     return this.isInteractiveClient.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getJdbcCompliantTruncation()
/*      */   {
/* 1815 */     return this.jdbcCompliantTruncation.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getLogger()
/*      */   {
/* 1824 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getLoggerClassName()
/*      */   {
/* 1833 */     return this.loggerClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public boolean getLogSlowQueries()
/*      */   {
/* 1842 */     return this.logSlowQueries.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getMaintainTimeStats() {
/* 1846 */     return this.maintainTimeStatsAsBoolean;
/*      */   }
/*      */ 
/*      */   public int getMaxQuerySizeToLog()
/*      */   {
/* 1855 */     return this.maxQuerySizeToLog.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxReconnects()
/*      */   {
/* 1864 */     return this.maxReconnects.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public int getMaxRows()
/*      */   {
/* 1873 */     return this.maxRowsAsInt;
/*      */   }
/*      */ 
/*      */   public int getMetadataCacheSize()
/*      */   {
/* 1883 */     return this.metadataCacheSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getNoDatetimeStringSync()
/*      */   {
/* 1890 */     return this.noDatetimeStringSync.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullCatalogMeansCurrent() {
/* 1894 */     return this.nullCatalogMeansCurrent.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getNullNamePatternMatchesAll() {
/* 1898 */     return this.nullNamePatternMatchesAll.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPacketDebugBufferSize()
/*      */   {
/* 1907 */     return this.packetDebugBufferSize.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getParanoid()
/*      */   {
/* 1916 */     return this.paranoid.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getPedantic()
/*      */   {
/* 1925 */     return this.pedantic.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSize()
/*      */   {
/* 1934 */     return ((Integer)this.preparedStatementCacheSize.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public int getPreparedStatementCacheSqlLimit()
/*      */   {
/* 1944 */     return ((Integer)this.preparedStatementCacheSqlLimit.getValueAsObject()).intValue();
/*      */   }
/*      */ 
/*      */   public boolean getProfileSql()
/*      */   {
/* 1954 */     return this.profileSQLAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getProfileSQL()
/*      */   {
/* 1963 */     return this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getPropertiesTransform()
/*      */   {
/* 1970 */     return this.propertiesTransform.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getQueriesBeforeRetryMaster()
/*      */   {
/* 1979 */     return this.queriesBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getReconnectAtTxEnd()
/*      */   {
/* 1988 */     return this.reconnectTxAtEndAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getRelaxAutoCommit()
/*      */   {
/* 1997 */     return this.relaxAutoCommit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getReportMetricsIntervalMillis()
/*      */   {
/* 2006 */     return this.reportMetricsIntervalMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getRequireSSL()
/*      */   {
/* 2015 */     return this.requireSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   protected boolean getRetainStatementAfterResultSetClose() {
/* 2019 */     return this.retainStatementAfterResultSetClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRollbackOnPooledClose()
/*      */   {
/* 2026 */     return this.rollbackOnPooledClose.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRoundRobinLoadBalance()
/*      */   {
/* 2035 */     return this.roundRobinLoadBalance.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getRunningCTS13()
/*      */   {
/* 2042 */     return this.runningCTS13.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public int getSecondsBeforeRetryMaster()
/*      */   {
/* 2051 */     return this.secondsBeforeRetryMaster.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getServerTimezone()
/*      */   {
/* 2060 */     return this.serverTimezone.getValueAsString();
/*      */   }
/*      */ 
/*      */   public String getSessionVariables()
/*      */   {
/* 2067 */     return this.sessionVariables.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSlowQueryThresholdMillis()
/*      */   {
/* 2076 */     return this.slowQueryThresholdMillis.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public String getSocketFactoryClassName()
/*      */   {
/* 2085 */     return this.socketFactoryClassName.getValueAsString();
/*      */   }
/*      */ 
/*      */   public int getSocketTimeout()
/*      */   {
/* 2094 */     return this.socketTimeout.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public boolean getStrictFloatingPoint()
/*      */   {
/* 2103 */     return this.strictFloatingPoint.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getStrictUpdates()
/*      */   {
/* 2112 */     return this.strictUpdates.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTinyInt1isBit()
/*      */   {
/* 2119 */     return this.tinyInt1isBit.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTraceProtocol()
/*      */   {
/* 2128 */     return this.traceProtocol.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getTransformedBitIsBoolean() {
/* 2132 */     return this.transformedBitIsBoolean.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseCompression()
/*      */   {
/* 2141 */     return this.useCompression.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseFastIntParsing()
/*      */   {
/* 2148 */     return this.useFastIntParsing.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseHostsInPrivileges()
/*      */   {
/* 2157 */     return this.useHostsInPrivileges.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseLocalSessionState()
/*      */   {
/* 2164 */     return this.useLocalSessionState.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseNewIo()
/*      */   {
/* 2173 */     return this.useNewIo.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseOldUTF8Behavior()
/*      */   {
/* 2180 */     return this.useOldUTF8BehaviorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseOnlyServerErrorMessages()
/*      */   {
/* 2187 */     return this.useOnlyServerErrorMessages.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseReadAheadInput()
/*      */   {
/* 2194 */     return this.useReadAheadInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseServerPreparedStmts()
/*      */   {
/* 2203 */     return this.detectServerPreparedStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSqlStateCodes()
/*      */   {
/* 2212 */     return this.useSqlStateCodes.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseSSL()
/*      */   {
/* 2221 */     return this.useSSL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseStreamLengthsInPrepStmts()
/*      */   {
/* 2230 */     return this.useStreamLengthsInPrepStmts.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseTimezone()
/*      */   {
/* 2239 */     return this.useTimezone.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUltraDevWorkAround()
/*      */   {
/* 2248 */     return this.useUltraDevWorkAround.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnbufferedInput()
/*      */   {
/* 2257 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public boolean getUseUnicode()
/*      */   {
/* 2266 */     return this.useUnicodeAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getUseUsageAdvisor()
/*      */   {
/* 2275 */     return this.useUsageAdvisorAsBoolean;
/*      */   }
/*      */ 
/*      */   public boolean getYearIsDateType() {
/* 2279 */     return this.yearIsDateType.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public String getZeroDateTimeBehavior()
/*      */   {
/* 2286 */     return this.zeroDateTimeBehavior.getValueAsString();
/*      */   }
/*      */ 
/*      */   protected void initializeFromRef(Reference ref)
/*      */     throws SQLException
/*      */   {
/* 2300 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2302 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 2303 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 2307 */         ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2310 */         if (ref != null)
/* 2311 */           propToSet.initializeFrom(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 2314 */         throw new SQLException("Internal properties failure", "S1000");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2319 */     postInitialization();
/*      */   }
/*      */ 
/*      */   protected void initializeProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 2332 */     if (info != null)
/*      */     {
/* 2334 */       String profileSqlLc = info.getProperty("profileSql");
/*      */ 
/* 2336 */       if (profileSqlLc != null) {
/* 2337 */         info.put("profileSQL", profileSqlLc);
/*      */       }
/*      */ 
/* 2340 */       Properties infoCopy = (Properties)info.clone();
/*      */ 
/* 2342 */       infoCopy.remove("HOST");
/* 2343 */       infoCopy.remove("user");
/* 2344 */       infoCopy.remove("password");
/* 2345 */       infoCopy.remove("DBNAME");
/* 2346 */       infoCopy.remove("PORT");
/* 2347 */       infoCopy.remove("profileSql");
/*      */ 
/* 2349 */       int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 2351 */       for (int i = 0; i < numPropertiesToSet; i++) {
/* 2352 */         Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */         try
/*      */         {
/* 2356 */           ConnectionProperty propToSet = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 2359 */           propToSet.initializeFrom(infoCopy);
/*      */         } catch (IllegalAccessException iae) {
/* 2361 */           throw new SQLException("Unable to initialize driver properties due to " + iae.toString(), "S1000");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2384 */       postInitialization();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void postInitialization()
/*      */     throws SQLException
/*      */   {
/* 2398 */     if (getLogger() == STANDARD_LOGGER_NAME) {
/* 2399 */       String environmentLoggerName = null;
/*      */       try
/*      */       {
/* 2402 */         environmentLoggerName = System.getProperty("com.mysql.jdbc.logger");
/*      */       }
/*      */       catch (Throwable noAccessToSystemProperties) {
/* 2405 */         environmentLoggerName = null;
/*      */       }
/*      */ 
/* 2408 */       if (environmentLoggerName != null)
/* 2409 */         setLogger(environmentLoggerName);
/*      */       else {
/*      */         try
/*      */         {
/* 2413 */           Class.forName("org.apache.log4j.Level");
/* 2414 */           setLogger("com.mysql.jdbc.log.Log4JLogger");
/*      */         }
/*      */         catch (Throwable t) {
/*      */           try {
/* 2418 */             Class.forName("java.util.logging.Level");
/* 2419 */             setLogger(Jdk14Logger.class.getName());
/*      */           }
/*      */           catch (Throwable t2) {
/* 2422 */             setLogger(STANDARD_LOGGER_NAME);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2429 */     if (this.profileSql.getValueAsObject() != null) {
/* 2430 */       this.profileSQL.initializeFrom(this.profileSql.getValueAsObject().toString());
/*      */     }
/*      */ 
/* 2434 */     this.reconnectTxAtEndAsBoolean = ((Boolean)this.reconnectAtTxEnd.getValueAsObject()).booleanValue();
/*      */ 
/* 2438 */     if (getMaxRows() == 0)
/*      */     {
/* 2441 */       this.maxRows.setValueAsObject(new Integer(-1));
/*      */     }
/*      */ 
/* 2447 */     String testEncoding = getEncoding();
/*      */ 
/* 2449 */     if (testEncoding != null)
/*      */     {
/*      */       try
/*      */       {
/* 2453 */         String testString = "abc";
/* 2454 */         testString.getBytes(testEncoding);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 2456 */         throw new SQLException("Unsupported character encoding '" + testEncoding + "'.", "0S100");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2464 */     if (((Boolean)this.cacheResultSetMetadata.getValueAsObject()).booleanValue()) {
/*      */       try
/*      */       {
/* 2467 */         Class.forName("java.util.LinkedHashMap");
/*      */       } catch (ClassNotFoundException cnfe) {
/* 2469 */         this.cacheResultSetMetadata.setValue(false);
/*      */       }
/*      */     }
/*      */ 
/* 2473 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */ 
/* 2475 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/* 2476 */     this.characterEncodingAsString = ((String)this.characterEncoding.getValueAsObject());
/*      */ 
/* 2478 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/* 2479 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */ 
/* 2481 */     this.maxRowsAsInt = ((Integer)this.maxRows.getValueAsObject()).intValue();
/*      */ 
/* 2483 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/* 2484 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */ 
/* 2486 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */ 
/* 2488 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */ 
/* 2490 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAllowLoadLocalInfile(boolean property)
/*      */   {
/* 2500 */     this.allowLoadLocalInfile.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setAllowMultiQueries(boolean property)
/*      */   {
/* 2509 */     this.allowMultiQueries.setValue(property);
/*      */   }
/*      */ 
/*      */   protected void setAllowNanAndInf(boolean flag)
/*      */   {
/* 2517 */     this.allowNanAndInf.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAllowUrlInLocalInfile(boolean flag)
/*      */   {
/* 2525 */     this.allowUrlInLocalInfile.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAlwaysSendSetIsolation(boolean flag)
/*      */   {
/* 2533 */     this.alwaysSendSetIsolation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoDeserialize(boolean flag)
/*      */   {
/* 2541 */     this.autoDeserialize.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoGenerateTestcaseScript(boolean flag) {
/* 2545 */     this.autoGenerateTestcaseScript.setValue(flag);
/* 2546 */     this.autoGenerateTestcaseScriptAsBoolean = this.autoGenerateTestcaseScript.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnect(boolean flag)
/*      */   {
/* 2557 */     this.autoReconnect.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForConnectionPools(boolean property)
/*      */   {
/* 2566 */     this.autoReconnectForPools.setValue(property);
/* 2567 */     this.autoReconnectForPoolsAsBoolean = this.autoReconnectForPools.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setAutoReconnectForPools(boolean flag)
/*      */   {
/* 2578 */     this.autoReconnectForPools.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setBlobSendChunkSize(String value)
/*      */     throws SQLException
/*      */   {
/* 2586 */     this.blobSendChunkSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setCacheCallableStatements(boolean flag)
/*      */   {
/* 2596 */     this.cacheCallableStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCachePreparedStatements(boolean flag)
/*      */   {
/* 2606 */     this.cachePreparedStatements.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCacheResultSetMetadata(boolean property)
/*      */   {
/* 2615 */     this.cacheResultSetMetadata.setValue(property);
/* 2616 */     this.cacheResultSetMetaDataAsBoolean = this.cacheResultSetMetadata.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setCacheServerConfiguration(boolean flag)
/*      */   {
/* 2625 */     this.cacheServerConfiguration.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCallableStatementCacheSize(int size)
/*      */   {
/* 2636 */     this.callableStatementCacheSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeDBMDTypes(boolean property)
/*      */   {
/* 2645 */     this.capitalizeTypeNames.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCapitalizeTypeNames(boolean flag)
/*      */   {
/* 2655 */     this.capitalizeTypeNames.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setCharacterEncoding(String encoding)
/*      */   {
/* 2665 */     this.characterEncoding.setValue(encoding);
/*      */   }
/*      */ 
/*      */   public void setCharacterSetResults(String characterSet)
/*      */   {
/* 2675 */     this.characterSetResults.setValue(characterSet);
/*      */   }
/*      */ 
/*      */   public void setClobberStreamingResults(boolean flag)
/*      */   {
/* 2685 */     this.clobberStreamingResults.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setConnectionCollation(String collation)
/*      */   {
/* 2695 */     this.connectionCollation.setValue(collation);
/*      */   }
/*      */ 
/*      */   public void setConnectTimeout(int timeoutMs)
/*      */   {
/* 2704 */     this.connectTimeout.setValue(timeoutMs);
/*      */   }
/*      */ 
/*      */   public void setContinueBatchOnError(boolean property)
/*      */   {
/* 2713 */     this.continueBatchOnError.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setCreateDatabaseIfNotExist(boolean flag) {
/* 2717 */     this.createDatabaseIfNotExist.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDetectServerPreparedStmts(boolean property)
/*      */   {
/* 2726 */     this.detectServerPreparedStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setDontTrackOpenResources(boolean flag)
/*      */   {
/* 2734 */     this.dontTrackOpenResources.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDumpQueriesOnException(boolean flag)
/*      */   {
/* 2744 */     this.dumpQueriesOnException.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setDynamicCalendars(boolean flag)
/*      */   {
/* 2752 */     this.dynamicCalendars.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setElideSetAutoCommits(boolean flag)
/*      */   {
/* 2762 */     this.elideSetAutoCommits.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmptyStringsConvertToZero(boolean flag) {
/* 2766 */     this.emptyStringsConvertToZero.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEmulateLocators(boolean property)
/*      */   {
/* 2775 */     this.emulateLocators.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setEmulateUnsupportedPstmts(boolean flag)
/*      */   {
/* 2783 */     this.emulateUnsupportedPstmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEnablePacketDebug(boolean flag)
/*      */   {
/* 2793 */     this.enablePacketDebug.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setEncoding(String property)
/*      */   {
/* 2802 */     this.characterEncoding.setValue(property);
/* 2803 */     this.characterEncodingAsString = this.characterEncoding.getValueAsString();
/*      */   }
/*      */ 
/*      */   public void setExplainSlowQueries(boolean flag)
/*      */   {
/* 2814 */     this.explainSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setFailOverReadOnly(boolean flag)
/*      */   {
/* 2824 */     this.failOverReadOnly.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setGatherPerformanceMetrics(boolean flag)
/*      */   {
/* 2834 */     this.gatherPerformanceMetrics.setValue(flag);
/*      */   }
/*      */ 
/*      */   protected void setHighAvailability(boolean property)
/*      */   {
/* 2843 */     this.autoReconnect.setValue(property);
/* 2844 */     this.highAvailabilityAsBoolean = this.autoReconnect.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setHoldResultsOpenOverStatementClose(boolean flag)
/*      */   {
/* 2852 */     this.holdResultsOpenOverStatementClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setIgnoreNonTxTables(boolean property)
/*      */   {
/* 2861 */     this.ignoreNonTxTables.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setInitialTimeout(int property)
/*      */   {
/* 2870 */     this.initialTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setIsInteractiveClient(boolean property)
/*      */   {
/* 2879 */     this.isInteractiveClient.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setJdbcCompliantTruncation(boolean flag)
/*      */   {
/* 2889 */     this.jdbcCompliantTruncation.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setLogger(String property)
/*      */   {
/* 2898 */     this.loggerClassName.setValueAsObject(property);
/*      */   }
/*      */ 
/*      */   public void setLoggerClassName(String className)
/*      */   {
/* 2908 */     this.loggerClassName.setValue(className);
/*      */   }
/*      */ 
/*      */   public void setLogSlowQueries(boolean flag)
/*      */   {
/* 2918 */     this.logSlowQueries.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setMaintainTimeStats(boolean flag) {
/* 2922 */     this.maintainTimeStats.setValue(flag);
/* 2923 */     this.maintainTimeStatsAsBoolean = this.maintainTimeStats.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setMaxQuerySizeToLog(int sizeInBytes)
/*      */   {
/* 2934 */     this.maxQuerySizeToLog.setValue(sizeInBytes);
/*      */   }
/*      */ 
/*      */   public void setMaxReconnects(int property)
/*      */   {
/* 2943 */     this.maxReconnects.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setMaxRows(int property)
/*      */   {
/* 2952 */     this.maxRows.setValue(property);
/* 2953 */     this.maxRowsAsInt = this.maxRows.getValueAsInt();
/*      */   }
/*      */ 
/*      */   public void setMetadataCacheSize(int value)
/*      */   {
/* 2964 */     this.metadataCacheSize.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNoDatetimeStringSync(boolean flag)
/*      */   {
/* 2972 */     this.noDatetimeStringSync.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setNullCatalogMeansCurrent(boolean value) {
/* 2976 */     this.nullCatalogMeansCurrent.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setNullNamePatternMatchesAll(boolean value) {
/* 2980 */     this.nullNamePatternMatchesAll.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setPacketDebugBufferSize(int size)
/*      */   {
/* 2990 */     this.packetDebugBufferSize.setValue(size);
/*      */   }
/*      */ 
/*      */   public void setParanoid(boolean property)
/*      */   {
/* 2999 */     this.paranoid.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPedantic(boolean property)
/*      */   {
/* 3008 */     this.pedantic.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSize(int cacheSize)
/*      */   {
/* 3018 */     this.preparedStatementCacheSize.setValue(cacheSize);
/*      */   }
/*      */ 
/*      */   public void setPreparedStatementCacheSqlLimit(int cacheSqlLimit)
/*      */   {
/* 3028 */     this.preparedStatementCacheSqlLimit.setValue(cacheSqlLimit);
/*      */   }
/*      */ 
/*      */   public void setProfileSql(boolean property)
/*      */   {
/* 3037 */     this.profileSQL.setValue(property);
/* 3038 */     this.profileSQLAsBoolean = this.profileSQL.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setProfileSQL(boolean flag)
/*      */   {
/* 3048 */     this.profileSQL.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setPropertiesTransform(String value)
/*      */   {
/* 3056 */     this.propertiesTransform.setValue(value);
/*      */   }
/*      */ 
/*      */   public void setQueriesBeforeRetryMaster(int property)
/*      */   {
/* 3065 */     this.queriesBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReconnectAtTxEnd(boolean property)
/*      */   {
/* 3074 */     this.reconnectAtTxEnd.setValue(property);
/* 3075 */     this.reconnectTxAtEndAsBoolean = this.reconnectAtTxEnd.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setRelaxAutoCommit(boolean property)
/*      */   {
/* 3085 */     this.relaxAutoCommit.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setReportMetricsIntervalMillis(int millis)
/*      */   {
/* 3095 */     this.reportMetricsIntervalMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setRequireSSL(boolean property)
/*      */   {
/* 3104 */     this.requireSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   protected void setRetainStatementAfterResultSetClose(boolean flag) {
/* 3108 */     this.retainStatementAfterResultSetClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRollbackOnPooledClose(boolean flag)
/*      */   {
/* 3116 */     this.rollbackOnPooledClose.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRoundRobinLoadBalance(boolean flag)
/*      */   {
/* 3126 */     this.roundRobinLoadBalance.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setRunningCTS13(boolean flag)
/*      */   {
/* 3134 */     this.runningCTS13.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setSecondsBeforeRetryMaster(int property)
/*      */   {
/* 3143 */     this.secondsBeforeRetryMaster.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setServerTimezone(String property)
/*      */   {
/* 3153 */     this.serverTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSessionVariables(String variables)
/*      */   {
/* 3161 */     this.sessionVariables.setValue(variables);
/*      */   }
/*      */ 
/*      */   public void setSlowQueryThresholdMillis(int millis)
/*      */   {
/* 3171 */     this.slowQueryThresholdMillis.setValue(millis);
/*      */   }
/*      */ 
/*      */   public void setSocketFactoryClassName(String property)
/*      */   {
/* 3180 */     this.socketFactoryClassName.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setSocketTimeout(int property)
/*      */   {
/* 3189 */     this.socketTimeout.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictFloatingPoint(boolean property)
/*      */   {
/* 3198 */     this.strictFloatingPoint.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setStrictUpdates(boolean property)
/*      */   {
/* 3207 */     this.strictUpdates.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setTinyInt1isBit(boolean flag)
/*      */   {
/* 3215 */     this.tinyInt1isBit.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTraceProtocol(boolean flag)
/*      */   {
/* 3225 */     this.traceProtocol.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setTransformedBitIsBoolean(boolean flag) {
/* 3229 */     this.transformedBitIsBoolean.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseCompression(boolean property)
/*      */   {
/* 3238 */     this.useCompression.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseFastIntParsing(boolean flag)
/*      */   {
/* 3246 */     this.useFastIntParsing.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseHostsInPrivileges(boolean property)
/*      */   {
/* 3255 */     this.useHostsInPrivileges.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseLocalSessionState(boolean flag)
/*      */   {
/* 3263 */     this.useLocalSessionState.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseNewIo(boolean property)
/*      */   {
/* 3272 */     this.useNewIo.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseOldUTF8Behavior(boolean flag)
/*      */   {
/* 3280 */     this.useOldUTF8Behavior.setValue(flag);
/* 3281 */     this.useOldUTF8BehaviorAsBoolean = this.useOldUTF8Behavior.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseOnlyServerErrorMessages(boolean flag)
/*      */   {
/* 3290 */     this.useOnlyServerErrorMessages.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseReadAheadInput(boolean flag)
/*      */   {
/* 3298 */     this.useReadAheadInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseServerPreparedStmts(boolean flag)
/*      */   {
/* 3308 */     this.detectServerPreparedStmts.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSqlStateCodes(boolean flag)
/*      */   {
/* 3318 */     this.useSqlStateCodes.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseSSL(boolean property)
/*      */   {
/* 3327 */     this.useSSL.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseStreamLengthsInPrepStmts(boolean property)
/*      */   {
/* 3336 */     this.useStreamLengthsInPrepStmts.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseTimezone(boolean property)
/*      */   {
/* 3345 */     this.useTimezone.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUltraDevWorkAround(boolean property)
/*      */   {
/* 3354 */     this.useUltraDevWorkAround.setValue(property);
/*      */   }
/*      */ 
/*      */   public void setUseUnbufferedInput(boolean flag)
/*      */   {
/* 3364 */     this.useUnbufferedInput.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setUseUnicode(boolean flag)
/*      */   {
/* 3374 */     this.useUnicode.setValue(flag);
/* 3375 */     this.useUnicodeAsBoolean = this.useUnicode.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setUseUsageAdvisor(boolean useUsageAdvisorFlag)
/*      */   {
/* 3385 */     this.useUsageAdvisor.setValue(useUsageAdvisorFlag);
/* 3386 */     this.useUsageAdvisorAsBoolean = this.useUsageAdvisor.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   public void setYearIsDateType(boolean flag)
/*      */   {
/* 3391 */     this.yearIsDateType.setValue(flag);
/*      */   }
/*      */ 
/*      */   public void setZeroDateTimeBehavior(String behavior)
/*      */   {
/* 3399 */     this.zeroDateTimeBehavior.setValue(behavior);
/*      */   }
/*      */ 
/*      */   protected void storeToRef(Reference ref) throws SQLException {
/* 3403 */     int numPropertiesToSet = PROPERTY_LIST.size();
/*      */ 
/* 3405 */     for (int i = 0; i < numPropertiesToSet; i++) {
/* 3406 */       Field propertyField = (Field)PROPERTY_LIST.get(i);
/*      */       try
/*      */       {
/* 3410 */         ConnectionProperty propToStore = (ConnectionProperty)propertyField.get(this);
/*      */ 
/* 3413 */         if (ref != null)
/* 3414 */           propToStore.storeTo(ref);
/*      */       }
/*      */       catch (IllegalAccessException iae) {
/* 3417 */         throw new SQLException("Huh?");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean useUnbufferedInput()
/*      */   {
/* 3428 */     return this.useUnbufferedInput.getValueAsBoolean();
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*      */     try
/*      */     {
/*  557 */       Field[] declaredFields = ConnectionProperties.class.getDeclaredFields();
/*      */ 
/*  560 */       for (int i = 0; i < declaredFields.length; i++) {
/*  561 */         if (!ConnectionProperty.class.isAssignableFrom(declaredFields[i].getType()))
/*      */           continue;
/*  563 */         PROPERTY_LIST.add(declaredFields[i]);
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/*  567 */       throw new RuntimeException(ex.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   class StringConnectionProperty extends ConnectionProperties.ConnectionProperty
/*      */   {
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  468 */       this(propertyNameToSet, defaultValueToSet, null, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     StringConnectionProperty(String propertyNameToSet, String defaultValueToSet, String[] allowableValuesToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  486 */       super(propertyNameToSet, defaultValueToSet, allowableValuesToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String getValueAsString()
/*      */     {
/*  492 */       return (String)this.valueAsObject;
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  499 */       return (this.allowableValues != null) && (this.allowableValues.length > 0);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  507 */       if (extractedValue != null) {
/*  508 */         validateStringValues(extractedValue);
/*      */ 
/*  510 */         this.valueAsObject = extractedValue;
/*      */       } else {
/*  512 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  520 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(String valueFlag) {
/*  524 */       this.valueAsObject = valueFlag;
/*      */     }
/*      */   }
/*      */ 
/*      */   class MemorySizeConnectionProperty extends ConnectionProperties.IntegerConnectionProperty
/*      */   {
/*      */     MemorySizeConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  417 */       super(propertyNameToSet, defaultValueToSet, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  424 */       if (extractedValue != null) {
/*  425 */         if ((extractedValue.endsWith("k")) || (extractedValue.endsWith("K")) || (extractedValue.endsWith("kb")) || (extractedValue.endsWith("Kb")) || (extractedValue.endsWith("kB")))
/*      */         {
/*  430 */           this.multiplier = 1024;
/*  431 */           int indexOfK = StringUtils.indexOfIgnoreCase(extractedValue, "k");
/*      */ 
/*  433 */           extractedValue = extractedValue.substring(0, indexOfK);
/*  434 */         } else if ((extractedValue.endsWith("m")) || (extractedValue.endsWith("M")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("mb")) || (extractedValue.endsWith("Mb")) || (extractedValue.endsWith("mB")))
/*      */         {
/*  440 */           this.multiplier = 1048576;
/*  441 */           int indexOfM = StringUtils.indexOfIgnoreCase(extractedValue, "m");
/*      */ 
/*  443 */           extractedValue = extractedValue.substring(0, indexOfM);
/*  444 */         } else if ((extractedValue.endsWith("g")) || (extractedValue.endsWith("G")) || (extractedValue.endsWith("gb")) || (extractedValue.endsWith("Gb")) || (extractedValue.endsWith("gB")))
/*      */         {
/*  449 */           this.multiplier = 1073741824;
/*  450 */           int indexOfG = StringUtils.indexOfIgnoreCase(extractedValue, "g");
/*      */ 
/*  452 */           extractedValue = extractedValue.substring(0, indexOfG);
/*      */         }
/*      */       }
/*      */ 
/*  456 */       super.initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void setValue(String value) throws SQLException {
/*  460 */       initializeFrom(value);
/*      */     }
/*      */   }
/*      */ 
/*      */   class IntegerConnectionProperty extends ConnectionProperties.ConnectionProperty
/*      */   {
/*      */     int multiplier;
/*      */     private final ConnectionProperties this$0;
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  313 */       super(propertyNameToSet, new Integer(defaultValueToSet), null, lowerBoundToSet, upperBoundToSet, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */ 
/*  312 */       this.this$0 = this$0;
/*      */ 
/*  307 */       this.multiplier = 1;
/*      */     }
/*      */ 
/*      */     IntegerConnectionProperty(String propertyNameToSet, int defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  331 */       this(propertyNameToSet, defaultValueToSet, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*  339 */       return null;
/*      */     }
/*      */ 
/*      */     int getLowerBound()
/*      */     {
/*  346 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getUpperBound()
/*      */     {
/*  353 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     int getValueAsInt() {
/*  357 */       return ((Integer)this.valueAsObject).intValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*  364 */       return false;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*  371 */       if (extractedValue != null) {
/*      */         try
/*      */         {
/*  374 */           int intValue = Double.valueOf(extractedValue).intValue();
/*      */ 
/*  385 */           this.valueAsObject = new Integer(intValue * this.multiplier);
/*      */         } catch (NumberFormatException nfe) {
/*  387 */           throw new SQLException("The connection property '" + getPropertyName() + "' only accepts integer values. The value '" + extractedValue + "' can not be converted to an integer.", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  395 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  403 */       return getUpperBound() != getLowerBound();
/*      */     }
/*      */ 
/*      */     void setValue(int valueFlag) {
/*  407 */       this.valueAsObject = new Integer(valueFlag);
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class ConnectionProperty extends DriverPropertyInfo
/*      */   {
/*      */     String[] allowableValues;
/*      */     String categoryName;
/*      */     Object defaultValue;
/*      */     int lowerBound;
/*      */     int order;
/*      */     String propertyName;
/*      */     String sinceVersion;
/*      */     int upperBound;
/*      */     Object valueAsObject;
/*      */ 
/*      */     ConnectionProperty(String propertyNameToSet, Object defaultValueToSet, String[] allowableValuesToSet, int lowerBoundToSet, int upperBoundToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*  143 */       super(null);
/*      */ 
/*  145 */       this.description = descriptionToSet;
/*  146 */       this.propertyName = propertyNameToSet;
/*  147 */       this.defaultValue = defaultValueToSet;
/*  148 */       this.valueAsObject = defaultValueToSet;
/*  149 */       this.allowableValues = allowableValuesToSet;
/*  150 */       this.lowerBound = lowerBoundToSet;
/*  151 */       this.upperBound = upperBoundToSet;
/*  152 */       this.required = false;
/*  153 */       this.sinceVersion = sinceVersionToSet;
/*  154 */       this.categoryName = category;
/*  155 */       this.order = orderInCategory;
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues() {
/*  159 */       return this.allowableValues;
/*      */     }
/*      */ 
/*      */     String getCategoryName()
/*      */     {
/*  166 */       return this.categoryName;
/*      */     }
/*      */ 
/*      */     Object getDefaultValue() {
/*  170 */       return this.defaultValue;
/*      */     }
/*      */ 
/*      */     int getLowerBound() {
/*  174 */       return this.lowerBound;
/*      */     }
/*      */ 
/*      */     int getOrder()
/*      */     {
/*  181 */       return this.order;
/*      */     }
/*      */ 
/*      */     String getPropertyName() {
/*  185 */       return this.propertyName;
/*      */     }
/*      */ 
/*      */     int getUpperBound() {
/*  189 */       return this.upperBound;
/*      */     }
/*      */ 
/*      */     Object getValueAsObject() {
/*  193 */       return this.valueAsObject;
/*      */     }
/*      */     abstract boolean hasValueConstraints();
/*      */ 
/*      */     void initializeFrom(Properties extractFrom) throws SQLException {
/*  199 */       String extractedValue = extractFrom.getProperty(getPropertyName());
/*  200 */       extractFrom.remove(getPropertyName());
/*  201 */       initializeFrom(extractedValue);
/*      */     }
/*      */ 
/*      */     void initializeFrom(Reference ref) throws SQLException {
/*  205 */       RefAddr refAddr = ref.get(getPropertyName());
/*      */ 
/*  207 */       if (refAddr != null) {
/*  208 */         String refContentAsString = (String)refAddr.getContent();
/*      */ 
/*  210 */         initializeFrom(refContentAsString);
/*      */       }
/*      */     }
/*      */ 
/*      */     abstract void initializeFrom(String paramString)
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean isRangeBased();
/*      */ 
/*      */     void setCategoryName(String categoryName)
/*      */     {
/*  223 */       this.categoryName = categoryName;
/*      */     }
/*      */ 
/*      */     void setOrder(int order)
/*      */     {
/*  231 */       this.order = order;
/*      */     }
/*      */ 
/*      */     void setValueAsObject(Object obj) {
/*  235 */       this.valueAsObject = obj;
/*      */     }
/*      */ 
/*      */     void storeTo(Reference ref) {
/*  239 */       if (getValueAsObject() != null)
/*  240 */         ref.add(new StringRefAddr(getPropertyName(), getValueAsObject().toString()));
/*      */     }
/*      */ 
/*      */     void syncDriverPropertyInfo()
/*      */     {
/*  250 */       this.choices = getAllowableValues();
/*  251 */       this.value = (this.valueAsObject != null ? this.valueAsObject.toString() : null);
/*      */     }
/*      */ 
/*      */     void validateStringValues(String valueToValidate) throws SQLException
/*      */     {
/*  256 */       String[] validateAgainst = getAllowableValues();
/*      */ 
/*  258 */       if (valueToValidate == null) {
/*  259 */         return;
/*      */       }
/*      */ 
/*  262 */       if ((validateAgainst == null) || (validateAgainst.length == 0)) {
/*  263 */         return;
/*      */       }
/*      */ 
/*  266 */       for (int i = 0; i < validateAgainst.length; i++) {
/*  267 */         if ((validateAgainst[i] != null) && (validateAgainst[i].equalsIgnoreCase(valueToValidate)))
/*      */         {
/*  269 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  273 */       StringBuffer errorMessageBuf = new StringBuffer();
/*      */ 
/*  275 */       errorMessageBuf.append("The connection property '");
/*  276 */       errorMessageBuf.append(getPropertyName());
/*  277 */       errorMessageBuf.append("' only accepts values of the form: ");
/*      */ 
/*  279 */       if (validateAgainst.length != 0) {
/*  280 */         errorMessageBuf.append("'");
/*  281 */         errorMessageBuf.append(validateAgainst[0]);
/*  282 */         errorMessageBuf.append("'");
/*      */ 
/*  284 */         for (int i = 1; i < validateAgainst.length - 1; i++) {
/*  285 */           errorMessageBuf.append(", ");
/*  286 */           errorMessageBuf.append("'");
/*  287 */           errorMessageBuf.append(validateAgainst[i]);
/*  288 */           errorMessageBuf.append("'");
/*      */         }
/*      */ 
/*  291 */         errorMessageBuf.append(" or '");
/*  292 */         errorMessageBuf.append(validateAgainst[(validateAgainst.length - 1)]);
/*      */ 
/*  294 */         errorMessageBuf.append("'");
/*      */       }
/*      */ 
/*  297 */       errorMessageBuf.append(". The value '");
/*  298 */       errorMessageBuf.append(valueToValidate);
/*  299 */       errorMessageBuf.append("' is not in this set.");
/*      */ 
/*  301 */       throw new SQLException(errorMessageBuf.toString(), "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */   class BooleanConnectionProperty extends ConnectionProperties.ConnectionProperty
/*      */   {
/*      */     BooleanConnectionProperty(String propertyNameToSet, boolean defaultValueToSet, String descriptionToSet, String sinceVersionToSet, String category, int orderInCategory)
/*      */     {
/*   70 */       super(propertyNameToSet, new Boolean(defaultValueToSet), null, 0, 0, descriptionToSet, sinceVersionToSet, category, orderInCategory);
/*      */     }
/*      */ 
/*      */     String[] getAllowableValues()
/*      */     {
/*   79 */       return new String[] { "true", "false", "yes", "no" };
/*      */     }
/*      */ 
/*      */     boolean getValueAsBoolean() {
/*   83 */       return ((Boolean)this.valueAsObject).booleanValue();
/*      */     }
/*      */ 
/*      */     boolean hasValueConstraints()
/*      */     {
/*   90 */       return true;
/*      */     }
/*      */ 
/*      */     void initializeFrom(String extractedValue)
/*      */       throws SQLException
/*      */     {
/*   97 */       if (extractedValue != null) {
/*   98 */         validateStringValues(extractedValue);
/*      */ 
/*  100 */         this.valueAsObject = new Boolean((extractedValue.equalsIgnoreCase("TRUE")) || (extractedValue.equalsIgnoreCase("YES")));
/*      */       }
/*      */       else
/*      */       {
/*  104 */         this.valueAsObject = this.defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean isRangeBased()
/*      */     {
/*  112 */       return false;
/*      */     }
/*      */ 
/*      */     void setValue(boolean valueFlag) {
/*  116 */       this.valueAsObject = new Boolean(valueFlag);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.ConnectionProperties
 * JD-Core Version:    0.6.0
 */
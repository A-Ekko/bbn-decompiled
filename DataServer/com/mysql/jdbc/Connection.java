/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.log.LogFactory;
/*      */ import com.mysql.jdbc.log.NullLogger;
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Savepoint;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.Stack;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TimeZone;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class Connection extends ConnectionProperties
/*      */   implements java.sql.Connection
/*      */ {
/* 1022 */   private static final Object CHARSET_CONVERTER_NOT_AVAILABLE_MARKER = new Object();
/*      */   public static Map charsetMap;
/*      */   protected static final String DEFAULT_LOGGER_CLASS = "com.mysql.jdbc.log.StandardLogger";
/*      */   private static final int HISTOGRAM_BUCKETS = 20;
/*      */   private static final String LOGGER_INSTANCE_NAME = "MySQL";
/* 1042 */   private static Map mapTransIsolationNameToValue = null;
/*      */   private static Map multibyteCharsetsMap;
/* 1048 */   private static final Log NULL_LOGGER = new NullLogger("MySQL");
/*      */   private static final String PING_COMMAND = "SELECT 1";
/*      */   private static Map roundRobinStatsMap;
/* 1057 */   private static final Map serverCollationByUrl = new HashMap();
/*      */ 
/* 1059 */   private static final Map serverConfigByUrl = new HashMap();
/*      */ 
/* 1153 */   private boolean autoCommit = true;
/*      */   private Map cachedPreparedStatementParams;
/* 1161 */   private String characterSetMetadata = null;
/*      */ 
/* 1167 */   private String characterSetResultsOnServer = null;
/*      */ 
/* 1174 */   private Map charsetConverterMap = new HashMap(CharsetMapping.getNumberOfCharsetsConfigured());
/*      */   private Map charsetToNumBytesMap;
/* 1184 */   private long connectionCreationTimeMillis = 0L;
/*      */   private int connectionId;
/* 1190 */   private String database = null;
/*      */ 
/* 1193 */   private DatabaseMetaData dbmd = null;
/*      */   private TimeZone defaultTimeZone;
/*      */   private ProfileEventSink eventSink;
/* 1200 */   private boolean executingFailoverReconnect = false;
/*      */ 
/* 1203 */   private boolean failedOver = false;
/*      */   private Throwable forceClosedReason;
/*      */   private Throwable forcedClosedLocation;
/* 1212 */   private boolean hasIsolationLevels = false;
/*      */ 
/* 1215 */   private boolean hasQuotedIdentifiers = false;
/*      */ 
/* 1218 */   private String host = null;
/*      */ 
/* 1221 */   private List hostList = null;
/*      */ 
/* 1224 */   private int hostListSize = 0;
/*      */ 
/* 1230 */   private String[] indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */ 
/* 1233 */   private MysqlIO io = null;
/*      */ 
/* 1235 */   private boolean isClientTzUTC = false;
/*      */ 
/* 1238 */   private boolean isClosed = true;
/*      */ 
/* 1241 */   private int isolationLevel = 2;
/*      */ 
/* 1243 */   private boolean isServerTzUTC = false;
/*      */ 
/* 1246 */   private long lastQueryFinishedTime = 0L;
/*      */ 
/* 1249 */   private Log log = NULL_LOGGER;
/*      */ 
/* 1255 */   private long longestQueryTimeMs = 0L;
/*      */ 
/* 1258 */   private boolean lowerCaseTableNames = false;
/*      */ 
/* 1261 */   private long masterFailTimeMillis = 0L;
/*      */ 
/* 1267 */   private int maxAllowedPacket = 65536;
/*      */ 
/* 1269 */   private long maximumNumberTablesAccessed = 0L;
/*      */ 
/* 1272 */   private boolean maxRowsChanged = false;
/*      */   private long metricsLastReportedMs;
/* 1277 */   private long minimumNumberTablesAccessed = 9223372036854775807L;
/*      */ 
/* 1280 */   private final Object mutex = new Object();
/*      */ 
/* 1283 */   private String myURL = null;
/*      */ 
/* 1286 */   private boolean needsPing = false;
/*      */ 
/* 1288 */   private int netBufferLength = 16384;
/*      */ 
/* 1290 */   private boolean noBackslashEscapes = false;
/*      */ 
/* 1292 */   private long numberOfPreparedExecutes = 0L;
/*      */ 
/* 1294 */   private long numberOfPrepares = 0L;
/*      */ 
/* 1296 */   private long numberOfQueriesIssued = 0L;
/*      */ 
/* 1298 */   private long numberOfResultSetsCreated = 0L;
/*      */   private long[] numTablesMetricsHistBreakpoints;
/*      */   private int[] numTablesMetricsHistCounts;
/* 1304 */   private long[] oldHistBreakpoints = null;
/*      */ 
/* 1306 */   private int[] oldHistCounts = null;
/*      */   private Map openStatements;
/*      */   private LRUCache parsedCallableStatementCache;
/* 1313 */   private boolean parserKnowsUnicode = false;
/*      */ 
/* 1316 */   private String password = null;
/*      */   private long[] perfMetricsHistBreakpoints;
/*      */   private int[] perfMetricsHistCounts;
/*      */   private Throwable pointOfOrigin;
/* 1326 */   private int port = 3306;
/*      */ 
/* 1332 */   private boolean preferSlaveDuringFailover = false;
/*      */ 
/* 1335 */   private Properties props = null;
/*      */ 
/* 1338 */   private long queriesIssuedFailedOver = 0L;
/*      */ 
/* 1341 */   private boolean readInfoMsg = false;
/*      */ 
/* 1344 */   private boolean readOnly = false;
/*      */ 
/* 1347 */   private TimeZone serverTimezoneTZ = null;
/*      */ 
/* 1350 */   private Map serverVariables = null;
/*      */ 
/* 1352 */   private long shortestQueryTimeMs = 9223372036854775807L;
/*      */   private Map statementsUsingMaxRows;
/* 1357 */   private double totalQueryTimeMs = 0.0D;
/*      */ 
/* 1360 */   private boolean transactionsSupported = false;
/*      */   private Map typeMap;
/* 1369 */   private boolean useAnsiQuotes = false;
/*      */ 
/* 1372 */   private String user = null;
/*      */ 
/* 1378 */   private boolean useServerPreparedStmts = false;
/*      */   private LRUCache serverSideStatementCheckCache;
/*      */   private LRUCache serverSideStatementCache;
/*      */ 
/*      */   protected static SQLException appendMessageToException(SQLException sqlEx, String messageToAppend)
/*      */   {
/* 1077 */     String origMessage = sqlEx.getMessage();
/* 1078 */     String sqlState = sqlEx.getSQLState();
/* 1079 */     int vendorErrorCode = sqlEx.getErrorCode();
/*      */ 
/* 1081 */     StringBuffer messageBuf = new StringBuffer(origMessage.length() + messageToAppend.length());
/*      */ 
/* 1083 */     messageBuf.append(origMessage);
/* 1084 */     messageBuf.append(messageToAppend);
/*      */ 
/* 1086 */     SQLException sqlExceptionWithNewMessage = new SQLException(messageBuf.toString(), sqlState, vendorErrorCode);
/*      */     try
/*      */     {
/* 1096 */       Method getStackTraceMethod = null;
/* 1097 */       Method setStackTraceMethod = null;
/* 1098 */       Object theStackTraceAsObject = null;
/*      */ 
/* 1100 */       Class stackTraceElementClass = Class.forName("java.lang.StackTraceElement");
/*      */ 
/* 1102 */       Class stackTraceElementArrayClass = java.lang.reflect.Array.newInstance(stackTraceElementClass, new int[] { 0 }).getClass();
/*      */ 
/* 1105 */       getStackTraceMethod = Throwable.class.getMethod("getStackTrace", new Class[0]);
/*      */ 
/* 1108 */       setStackTraceMethod = class$java$lang$Throwable.getMethod("setStackTrace", new Class[] { stackTraceElementArrayClass });
/*      */ 
/* 1111 */       if ((getStackTraceMethod != null) && (setStackTraceMethod != null)) {
/* 1112 */         theStackTraceAsObject = getStackTraceMethod.invoke(sqlEx, new Object[0]);
/*      */ 
/* 1114 */         setStackTraceMethod.invoke(sqlExceptionWithNewMessage, new Object[] { theStackTraceAsObject });
/*      */       }
/*      */     }
/*      */     catch (NoClassDefFoundError noClassDefFound)
/*      */     {
/*      */     }
/*      */     catch (NoSuchMethodException noSuchMethodEx)
/*      */     {
/*      */     }
/*      */     catch (Throwable catchAll) {
/*      */     }
/* 1125 */     return sqlExceptionWithNewMessage;
/*      */   }
/*      */ 
/*      */   private static synchronized int getNextRoundRobinHostIndex(String url, List hostList)
/*      */   {
/* 1130 */     if (roundRobinStatsMap == null) {
/* 1131 */       roundRobinStatsMap = new HashMap();
/*      */     }
/*      */ 
/* 1134 */     int[] index = (int[])roundRobinStatsMap.get(url);
/*      */ 
/* 1136 */     if (index == null) {
/* 1137 */       index = new int[1];
/* 1138 */       index[0] = -1;
/*      */ 
/* 1140 */       roundRobinStatsMap.put(url, index);
/*      */     }
/*      */ 
/* 1143 */     index[0] += 1;
/*      */ 
/* 1145 */     if (index[0] >= hostList.size()) {
/* 1146 */       index[0] = 0;
/*      */     }
/*      */ 
/* 1149 */     return index[0];
/*      */   }
/*      */ 
/*      */   Connection(String hostToConnectTo, int portToConnectTo, Properties info, String databaseToConnectTo, String url, NonRegisteringDriver d)
/*      */     throws SQLException
/*      */   {
/* 1405 */     this.connectionCreationTimeMillis = System.currentTimeMillis();
/* 1406 */     this.pointOfOrigin = new Throwable();
/*      */ 
/* 1418 */     this.log = LogFactory.getLogger(getLogger(), "MySQL");
/*      */ 
/* 1422 */     this.defaultTimeZone = TimeZone.getDefault();
/* 1423 */     if ("GMT".equalsIgnoreCase(this.defaultTimeZone.getID()))
/* 1424 */       this.isClientTzUTC = true;
/*      */     else {
/* 1426 */       this.isClientTzUTC = false;
/*      */     }
/*      */ 
/* 1429 */     this.openStatements = new HashMap();
/* 1430 */     this.serverVariables = new HashMap();
/* 1431 */     this.hostList = new ArrayList();
/*      */ 
/* 1433 */     if (hostToConnectTo == null) {
/* 1434 */       this.host = "localhost";
/* 1435 */       this.hostList.add(this.host);
/* 1436 */     } else if (hostToConnectTo.indexOf(",") != -1)
/*      */     {
/* 1438 */       StringTokenizer hostTokenizer = new StringTokenizer(hostToConnectTo, ",", false);
/*      */ 
/* 1441 */       while (hostTokenizer.hasMoreTokens())
/* 1442 */         this.hostList.add(hostTokenizer.nextToken().trim());
/*      */     }
/*      */     else {
/* 1445 */       this.host = hostToConnectTo;
/* 1446 */       this.hostList.add(this.host);
/*      */     }
/*      */ 
/* 1449 */     this.hostListSize = this.hostList.size();
/* 1450 */     this.port = portToConnectTo;
/*      */ 
/* 1452 */     if (databaseToConnectTo == null) {
/* 1453 */       databaseToConnectTo = "";
/*      */     }
/*      */ 
/* 1456 */     this.database = databaseToConnectTo;
/* 1457 */     this.myURL = url;
/* 1458 */     this.user = info.getProperty("user");
/* 1459 */     this.password = info.getProperty("password");
/*      */ 
/* 1462 */     if ((this.user == null) || (this.user.equals(""))) {
/* 1463 */       this.user = "";
/*      */     }
/*      */ 
/* 1466 */     if (this.password == null) {
/* 1467 */       this.password = "";
/*      */     }
/*      */ 
/* 1470 */     this.props = info;
/* 1471 */     initializeDriverProperties(info);
/*      */     try
/*      */     {
/* 1474 */       createNewIO(false);
/* 1475 */       this.dbmd = new DatabaseMetaData(this, this.database);
/*      */     } catch (SQLException ex) {
/* 1477 */       cleanup(new Throwable(), ex);
/*      */ 
/* 1480 */       throw ex;
/*      */     } catch (Exception ex) {
/* 1482 */       cleanup(new Throwable(), ex);
/*      */ 
/* 1484 */       StringBuffer mesg = new StringBuffer();
/*      */ 
/* 1486 */       if (getParanoid()) {
/* 1487 */         mesg.append("Cannot connect to MySQL server on ");
/* 1488 */         mesg.append(this.host);
/* 1489 */         mesg.append(":");
/* 1490 */         mesg.append(this.port);
/* 1491 */         mesg.append(".\n\n");
/* 1492 */         mesg.append("Make sure that there is a MySQL server ");
/* 1493 */         mesg.append("running on the machine/port you are trying ");
/* 1494 */         mesg.append("to connect to and that the machine this software is running on ");
/*      */ 
/* 1497 */         mesg.append("is able to connect to this host/port (i.e. not firewalled). ");
/*      */ 
/* 1499 */         mesg.append("Also make sure that the server has not been started with the --skip-networking ");
/*      */ 
/* 1502 */         mesg.append("flag.\n\n");
/*      */       } else {
/* 1504 */         mesg.append("Unable to connect to database.");
/*      */       }
/*      */ 
/* 1507 */       mesg.append("Underlying exception: \n\n");
/* 1508 */       mesg.append(ex.getClass().getName());
/*      */ 
/* 1510 */       if (!getParanoid()) {
/* 1511 */         mesg.append(Util.stackTraceToString(ex));
/*      */       }
/*      */ 
/* 1514 */       throw new SQLException(mesg.toString(), "08S01");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound)
/*      */   {
/* 1522 */     if (histogramCounts == null) {
/* 1523 */       createInitialHistogram(histogramCounts, histogramBreakpoints, currentLowerBound, currentUpperBound);
/*      */     }
/*      */ 
/* 1527 */     for (int i = 0; i < 20; i++)
/* 1528 */       if (histogramBreakpoints[i] >= value) {
/* 1529 */         histogramCounts[i] += numberOfTimes;
/*      */ 
/* 1531 */         break;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void addToPerformanceHistogram(long value, int numberOfTimes)
/*      */   {
/* 1537 */     checkAndCreatePerformanceHistogram();
/*      */ 
/* 1539 */     addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void addToTablesAccessedHistogram(long value, int numberOfTimes)
/*      */   {
/* 1546 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/* 1548 */     addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void buildCollationMapping()
/*      */     throws SQLException
/*      */   {
/* 1563 */     if (versionMeetsMinimum(4, 1, 0))
/*      */     {
/* 1565 */       TreeMap sortedCollationMap = null;
/*      */ 
/* 1567 */       if (getCacheServerConfiguration()) {
/* 1568 */         synchronized (serverConfigByUrl) {
/* 1569 */           sortedCollationMap = (TreeMap)serverCollationByUrl.get(getURL());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1574 */       Statement stmt = null;
/* 1575 */       ResultSet results = null;
/*      */       try
/*      */       {
/* 1578 */         if (sortedCollationMap == null) {
/* 1579 */           sortedCollationMap = new TreeMap();
/*      */ 
/* 1581 */           stmt = (Statement)createStatement();
/*      */ 
/* 1583 */           if (stmt.getMaxRows() != 0) {
/* 1584 */             stmt.setMaxRows(0);
/*      */           }
/*      */ 
/* 1587 */           results = (ResultSet)stmt.executeQuery("SHOW COLLATION");
/*      */ 
/* 1590 */           while (results.next()) {
/* 1591 */             String charsetName = results.getString(2);
/* 1592 */             Integer charsetIndex = new Integer(results.getInt(3));
/*      */ 
/* 1594 */             sortedCollationMap.put(charsetIndex, charsetName);
/*      */           }
/*      */ 
/* 1597 */           if (getCacheServerConfiguration()) {
/* 1598 */             synchronized (serverConfigByUrl) {
/* 1599 */               serverCollationByUrl.put(getURL(), sortedCollationMap);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1607 */         int highestIndex = ((Integer)sortedCollationMap.lastKey()).intValue();
/*      */ 
/* 1610 */         if (CharsetMapping.INDEX_TO_CHARSET.length > highestIndex) {
/* 1611 */           highestIndex = CharsetMapping.INDEX_TO_CHARSET.length;
/*      */         }
/*      */ 
/* 1614 */         this.indexToCharsetMapping = new String[highestIndex + 1];
/*      */ 
/* 1616 */         for (int i = 0; i < CharsetMapping.INDEX_TO_CHARSET.length; i++) {
/* 1617 */           this.indexToCharsetMapping[i] = CharsetMapping.INDEX_TO_CHARSET[i];
/*      */         }
/*      */ 
/* 1620 */         Iterator indexIter = sortedCollationMap.entrySet().iterator();
/* 1621 */         while (indexIter.hasNext()) {
/* 1622 */           Map.Entry indexEntry = (Map.Entry)indexIter.next();
/*      */ 
/* 1624 */           String mysqlCharsetName = (String)indexEntry.getValue();
/*      */ 
/* 1626 */           this.indexToCharsetMapping[((Integer)indexEntry.getKey()).intValue()] = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlCharsetName, this);
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (SQLException e)
/*      */       {
/* 1632 */         throw e;
/*      */       } finally {
/* 1634 */         if (results != null) {
/*      */           try {
/* 1636 */             results.close();
/*      */           }
/*      */           catch (SQLException sqlE)
/*      */           {
/*      */           }
/*      */         }
/* 1642 */         if (stmt != null)
/*      */           try {
/* 1644 */             stmt.close();
/*      */           }
/*      */           catch (SQLException sqlE)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1653 */       this.indexToCharsetMapping = CharsetMapping.INDEX_TO_CHARSET;
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized boolean canHandleAsServerPreparedStatement(String sql) throws SQLException
/*      */   {
/* 1659 */     if ((sql == null) || (sql.length() == 0)) {
/* 1660 */       return true;
/*      */     }
/*      */ 
/* 1663 */     if (getCachePreparedStatements()) {
/* 1664 */       Boolean flag = (Boolean)this.serverSideStatementCheckCache.get(sql);
/*      */ 
/* 1666 */       if (flag != null) {
/* 1667 */         return flag.booleanValue();
/*      */       }
/*      */     }
/*      */ 
/* 1671 */     boolean canHandleAsStatement = true;
/*      */ 
/* 1673 */     if ((!versionMeetsMinimum(5, 0, 7)) && ((StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "SELECT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(sql, "REPLACE"))))
/*      */     {
/* 1691 */       int currentPos = 0;
/* 1692 */       int statementLength = sql.length();
/* 1693 */       int lastPosToLook = statementLength - 7;
/* 1694 */       boolean allowBackslashEscapes = !this.noBackslashEscapes;
/* 1695 */       char quoteChar = this.useAnsiQuotes ? '"' : '\'';
/* 1696 */       boolean foundLimitWithPlaceholder = false;
/*      */ 
/* 1698 */       while (currentPos < lastPosToLook) {
/* 1699 */         int limitStart = StringUtils.indexOfIgnoreCaseRespectQuotes(currentPos, sql, "LIMIT ", quoteChar, allowBackslashEscapes);
/*      */ 
/* 1703 */         if (limitStart == -1)
/*      */         {
/*      */           break;
/*      */         }
/* 1707 */         currentPos = limitStart + 7;
/*      */ 
/* 1709 */         while (currentPos < statementLength) {
/* 1710 */           char c = sql.charAt(currentPos);
/*      */ 
/* 1717 */           if ((!Character.isDigit(c)) && (!Character.isWhitespace(c)) && (c != ',') && (c != '?'))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/* 1722 */           if (c == '?') {
/* 1723 */             foundLimitWithPlaceholder = true;
/* 1724 */             break;
/*      */           }
/*      */ 
/* 1727 */           currentPos++;
/*      */         }
/*      */       }
/*      */ 
/* 1731 */       canHandleAsStatement = !foundLimitWithPlaceholder;
/* 1732 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/* 1733 */       canHandleAsStatement = false;
/* 1734 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "DO")) {
/* 1735 */       canHandleAsStatement = false;
/* 1736 */     } else if (StringUtils.startsWithIgnoreCaseAndWs(sql, "SET")) {
/* 1737 */       canHandleAsStatement = false;
/*      */     }
/*      */ 
/* 1740 */     if ((getCachePreparedStatements()) && (sql.length() < getPreparedStatementCacheSqlLimit()))
/*      */     {
/* 1742 */       this.serverSideStatementCheckCache.put(sql, canHandleAsStatement ? Boolean.TRUE : Boolean.FALSE);
/*      */     }
/*      */ 
/* 1746 */     return canHandleAsStatement;
/*      */   }
/*      */ 
/*      */   public void changeUser(String userName, String newPassword)
/*      */     throws SQLException
/*      */   {
/* 1764 */     if ((userName == null) || (userName.equals(""))) {
/* 1765 */       userName = "";
/*      */     }
/*      */ 
/* 1768 */     if (newPassword == null) {
/* 1769 */       newPassword = "";
/*      */     }
/*      */ 
/* 1772 */     this.io.changeUser(userName, newPassword, this.database);
/* 1773 */     this.user = userName;
/* 1774 */     this.password = newPassword;
/*      */ 
/* 1776 */     if (versionMeetsMinimum(4, 1, 0))
/* 1777 */       configureClientCharacterSet();
/*      */   }
/*      */ 
/*      */   private void checkAndCreatePerformanceHistogram()
/*      */   {
/* 1782 */     if (this.perfMetricsHistCounts == null) {
/* 1783 */       this.perfMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1786 */     if (this.perfMetricsHistBreakpoints == null)
/* 1787 */       this.perfMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   private void checkAndCreateTablesAccessedHistogram()
/*      */   {
/* 1792 */     if (this.numTablesMetricsHistCounts == null) {
/* 1793 */       this.numTablesMetricsHistCounts = new int[20];
/*      */     }
/*      */ 
/* 1796 */     if (this.numTablesMetricsHistBreakpoints == null)
/* 1797 */       this.numTablesMetricsHistBreakpoints = new long[20];
/*      */   }
/*      */ 
/*      */   private void checkClosed() throws SQLException
/*      */   {
/* 1802 */     if (this.isClosed) {
/* 1803 */       StringBuffer messageBuf = new StringBuffer("No operations allowed after connection closed.");
/*      */ 
/* 1806 */       if ((this.forcedClosedLocation != null) || (this.forceClosedReason != null)) {
/* 1807 */         messageBuf.append("Connection was implicitly closed ");
/*      */       }
/*      */ 
/* 1811 */       if (this.forcedClosedLocation != null) {
/* 1812 */         messageBuf.append("\n\n");
/* 1813 */         messageBuf.append(" at (stack trace):\n");
/*      */ 
/* 1815 */         messageBuf.append(Util.stackTraceToString(this.forcedClosedLocation));
/*      */       }
/*      */ 
/* 1819 */       if (this.forceClosedReason != null) {
/* 1820 */         if (this.forcedClosedLocation != null)
/* 1821 */           messageBuf.append("\n\nDue ");
/*      */         else {
/* 1823 */           messageBuf.append("due ");
/*      */         }
/*      */ 
/* 1826 */         messageBuf.append("to underlying exception/error:\n");
/* 1827 */         messageBuf.append(Util.stackTraceToString(this.forceClosedReason));
/*      */       }
/*      */ 
/* 1831 */       throw new SQLException(messageBuf.toString(), "08003");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkServerEncoding()
/*      */     throws SQLException
/*      */   {
/* 1844 */     if ((getUseUnicode()) && (getEncoding() != null))
/*      */     {
/* 1846 */       return;
/*      */     }
/*      */ 
/* 1849 */     String serverEncoding = (String)this.serverVariables.get("character_set");
/*      */ 
/* 1852 */     if (serverEncoding == null)
/*      */     {
/* 1854 */       serverEncoding = (String)this.serverVariables.get("character_set_server");
/*      */     }
/*      */ 
/* 1858 */     String mappedServerEncoding = null;
/*      */ 
/* 1860 */     if (serverEncoding != null) {
/* 1861 */       mappedServerEncoding = CharsetMapping.getJavaEncodingForMysqlEncoding(serverEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */     }
/*      */ 
/* 1869 */     if ((!getUseUnicode()) && (mappedServerEncoding != null)) {
/* 1870 */       SingleByteCharsetConverter converter = getCharsetConverter(mappedServerEncoding);
/*      */ 
/* 1872 */       if (converter != null) {
/* 1873 */         setUseUnicode(true);
/* 1874 */         setEncoding(mappedServerEncoding);
/*      */ 
/* 1876 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1884 */     if (serverEncoding != null) {
/* 1885 */       if (mappedServerEncoding == null)
/*      */       {
/* 1888 */         if (Character.isLowerCase(serverEncoding.charAt(0))) {
/* 1889 */           char[] ach = serverEncoding.toCharArray();
/* 1890 */           ach[0] = Character.toUpperCase(serverEncoding.charAt(0));
/* 1891 */           setEncoding(new String(ach));
/*      */         }
/*      */       }
/*      */ 
/* 1895 */       if (mappedServerEncoding == null) {
/* 1896 */         throw new SQLException("Unknown character encoding on server '" + serverEncoding + "', use 'characterEncoding=' property " + " to provide correct mapping", "01S00");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1908 */         "abc".getBytes(mappedServerEncoding);
/* 1909 */         setEncoding(mappedServerEncoding);
/* 1910 */         setUseUnicode(true);
/*      */       } catch (UnsupportedEncodingException UE) {
/* 1912 */         throw new SQLException("The driver can not map the character encoding '" + getEncoding() + "' that your server is using " + "to a character encoding your JVM understands. You " + "can specify this mapping manually by adding \"useUnicode=true\" " + "as well as \"characterEncoding=[an_encoding_your_jvm_understands]\" " + "to your JDBC URL.", "0S100");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkTransactionIsolationLevel()
/*      */     throws SQLException
/*      */   {
/* 1932 */     String txIsolationName = null;
/*      */ 
/* 1934 */     if (versionMeetsMinimum(4, 0, 3))
/* 1935 */       txIsolationName = "tx_isolation";
/*      */     else {
/* 1937 */       txIsolationName = "transaction_isolation";
/*      */     }
/*      */ 
/* 1940 */     String s = (String)this.serverVariables.get(txIsolationName);
/*      */ 
/* 1942 */     if (s != null) {
/* 1943 */       Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 1945 */       if (intTI != null)
/* 1946 */         this.isolationLevel = intTI.intValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void cleanup(Throwable fromWhere, Throwable whyCleanedUp)
/*      */   {
/*      */     try
/*      */     {
/* 1961 */       if ((this.io != null) && (!isClosed()))
/* 1962 */         realClose(false, false, false, whyCleanedUp);
/* 1963 */       else if (this.io != null) {
/* 1964 */         this.io.forceClose();
/*      */       }
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*      */     }
/*      */ 
/* 1971 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public PreparedStatement clientPrepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 1996 */     return clientPrepareStatement(sql, 1005, 1007);
/*      */   }
/*      */ 
/*      */   public synchronized PreparedStatement clientPrepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2016 */     checkClosed();
/*      */ 
/* 2018 */     PreparedStatement pStmt = null;
/*      */ 
/* 2020 */     if (getCachePreparedStatements()) {
/* 2021 */       PreparedStatement.ParseInfo pStmtInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(sql);
/*      */ 
/* 2024 */       if (pStmtInfo == null) {
/* 2025 */         pStmt = new PreparedStatement(this, sql, this.database);
/*      */ 
/* 2028 */         PreparedStatement.ParseInfo parseInfo = pStmt.getParseInfo();
/*      */ 
/* 2030 */         if (parseInfo.statementLength < getPreparedStatementCacheSqlLimit()) {
/* 2031 */           if (this.cachedPreparedStatementParams.size() >= getPreparedStatementCacheSize()) {
/* 2032 */             Iterator oldestIter = this.cachedPreparedStatementParams.keySet().iterator();
/*      */ 
/* 2034 */             long lruTime = 9223372036854775807L;
/* 2035 */             String oldestSql = null;
/*      */ 
/* 2037 */             while (oldestIter.hasNext()) {
/* 2038 */               String sqlKey = (String)oldestIter.next();
/* 2039 */               PreparedStatement.ParseInfo lruInfo = (PreparedStatement.ParseInfo)this.cachedPreparedStatementParams.get(sqlKey);
/*      */ 
/* 2042 */               if (lruInfo.lastUsed < lruTime) {
/* 2043 */                 lruTime = lruInfo.lastUsed;
/* 2044 */                 oldestSql = sqlKey;
/*      */               }
/*      */             }
/*      */ 
/* 2048 */             if (oldestSql != null) {
/* 2049 */               this.cachedPreparedStatementParams.remove(oldestSql);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2054 */           this.cachedPreparedStatementParams.put(sql, pStmt.getParseInfo());
/*      */         }
/*      */       }
/*      */       else {
/* 2058 */         pStmtInfo.lastUsed = System.currentTimeMillis();
/* 2059 */         pStmt = new PreparedStatement(this, sql, this.database, pStmtInfo);
/*      */       }
/*      */     }
/*      */     else {
/* 2063 */       pStmt = new PreparedStatement(this, sql, this.database);
/*      */     }
/*      */ 
/* 2067 */     pStmt.setResultSetType(1005);
/* 2068 */     pStmt.setResultSetConcurrency(1007);
/*      */ 
/* 2070 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/* 2084 */     realClose(true, true, false, null);
/*      */   }
/*      */ 
/*      */   private void closeAllOpenStatements()
/*      */     throws SQLException
/*      */   {
/* 2094 */     SQLException postponedException = null;
/*      */ 
/* 2096 */     if (this.openStatements != null) {
/* 2097 */       List currentlyOpenStatements = new ArrayList();
/*      */ 
/* 2101 */       Iterator iter = this.openStatements.keySet().iterator();
/* 2102 */       while (iter.hasNext()) {
/* 2103 */         currentlyOpenStatements.add(iter.next());
/*      */       }
/*      */ 
/* 2106 */       int numStmts = currentlyOpenStatements.size();
/*      */ 
/* 2108 */       for (int i = 0; i < numStmts; i++) {
/* 2109 */         Statement stmt = (Statement)currentlyOpenStatements.get(i);
/*      */         try
/*      */         {
/* 2112 */           stmt.realClose(false);
/*      */         } catch (SQLException sqlEx) {
/* 2114 */           postponedException = sqlEx;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2119 */       if (postponedException != null)
/* 2120 */         throw postponedException;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void commit()
/*      */     throws SQLException
/*      */   {
/* 2140 */     checkClosed();
/*      */     try
/*      */     {
/* 2144 */       if ((this.autoCommit) && (!getRelaxAutoCommit()))
/* 2145 */         throw new SQLException("Can't call commit when autocommit=true");
/* 2146 */       if (this.transactionsSupported) {
/* 2147 */         execSQL(null, "commit", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (SQLException sqlException)
/*      */     {
/* 2153 */       if ("08S01".equals(sqlException.getSQLState()))
/*      */       {
/* 2155 */         throw new SQLException("Communications link failure during commit(). Transaction resolution unknown.", "08007");
/*      */       }
/*      */ 
/* 2160 */       throw sqlException;
/*      */     } finally {
/* 2162 */       this.needsPing = getReconnectAtTxEnd();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void configureCharsetProperties()
/*      */     throws SQLException
/*      */   {
/* 2175 */     if (getEncoding() != null)
/*      */     {
/*      */       try
/*      */       {
/* 2179 */         String testString = "abc";
/* 2180 */         testString.getBytes(getEncoding());
/*      */       }
/*      */       catch (UnsupportedEncodingException UE) {
/* 2183 */         String oldEncoding = getEncoding();
/*      */ 
/* 2185 */         setEncoding(CharsetMapping.getJavaEncodingForMysqlEncoding(oldEncoding, this));
/*      */ 
/* 2188 */         if (getEncoding() == null) {
/* 2189 */           throw new SQLException("Java does not support the MySQL character encoding  encoding '" + oldEncoding + "'.", "01S00");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 2196 */           String testString = "abc";
/* 2197 */           testString.getBytes(getEncoding());
/*      */         } catch (UnsupportedEncodingException encodingEx) {
/* 2199 */           throw new SQLException("Unsupported character encoding '" + getEncoding() + "'.", "01S00");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean configureClientCharacterSet()
/*      */     throws SQLException
/*      */   {
/* 2223 */     String realJavaEncoding = getEncoding();
/* 2224 */     boolean characterSetAlreadyConfigured = false;
/*      */     try
/*      */     {
/* 2227 */       if (versionMeetsMinimum(4, 1, 0)) {
/* 2228 */         characterSetAlreadyConfigured = true;
/*      */ 
/* 2230 */         setUseUnicode(true);
/*      */ 
/* 2232 */         configureCharsetProperties();
/* 2233 */         realJavaEncoding = getEncoding();
/*      */         try
/*      */         {
/* 2238 */           setEncoding(CharsetMapping.INDEX_TO_CHARSET[this.io.serverCharsetIndex]);
/*      */         } catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 2240 */           if (realJavaEncoding != null)
/*      */           {
/* 2242 */             setEncoding(realJavaEncoding);
/*      */           }
/* 2244 */           else throw new SQLException("Unknown initial character set index '" + this.io.serverCharsetIndex + "' received from server. Initial client character set can be forced via the 'characterEncoding' property.", "S1000");
/*      */ 
/*      */         }
/*      */ 
/* 2252 */         if (getEncoding() == null)
/*      */         {
/* 2254 */           setEncoding("ISO8859_1");
/*      */         }
/*      */ 
/* 2261 */         if (getUseUnicode()) {
/* 2262 */           if (realJavaEncoding != null)
/*      */           {
/* 2268 */             if ((realJavaEncoding.equalsIgnoreCase("UTF-8")) || (realJavaEncoding.equalsIgnoreCase("UTF8")))
/*      */             {
/* 2272 */               if (!getUseOldUTF8Behavior()) {
/* 2273 */                 execSQL(null, "SET NAMES utf8", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */               }
/*      */ 
/* 2280 */               setEncoding(realJavaEncoding);
/*      */             } else {
/* 2282 */               String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(realJavaEncoding.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 2297 */               if (mysqlEncodingName != null) {
/* 2298 */                 execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */               }
/*      */ 
/* 2309 */               setEncoding(realJavaEncoding);
/*      */             }
/* 2311 */           } else if (getEncoding() != null)
/*      */           {
/* 2315 */             String mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(getEncoding().toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 2319 */             execSQL(null, "SET NAMES " + mysqlEncodingName, -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */ 
/* 2325 */             realJavaEncoding = getEncoding();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2336 */         if (getCharacterSetResults() == null) {
/* 2337 */           execSQL(null, "SET character_set_results = NULL", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */         }
/*      */         else
/*      */         {
/* 2342 */           String charsetResults = getCharacterSetResults();
/* 2343 */           String mysqlEncodingName = null;
/*      */ 
/* 2345 */           if (("UTF-8".equalsIgnoreCase(charsetResults)) || ("UTF8".equalsIgnoreCase(charsetResults)))
/*      */           {
/* 2347 */             mysqlEncodingName = "utf8";
/*      */           }
/* 2349 */           else mysqlEncodingName = CharsetMapping.getMysqlEncodingForJavaEncoding(charsetResults.toUpperCase(Locale.ENGLISH), this);
/*      */ 
/* 2354 */           StringBuffer setBuf = new StringBuffer("SET character_set_results = ".length() + mysqlEncodingName.length());
/*      */ 
/* 2357 */           setBuf.append("SET character_set_results = ").append(mysqlEncodingName);
/*      */ 
/* 2360 */           execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */         }
/*      */ 
/* 2366 */         if (getConnectionCollation() != null) {
/* 2367 */           StringBuffer setBuf = new StringBuffer("SET collation_connection = ".length() + getConnectionCollation().length());
/*      */ 
/* 2370 */           setBuf.append("SET collation_connection = ").append(getConnectionCollation());
/*      */ 
/* 2373 */           execSQL(null, setBuf.toString(), -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2380 */         realJavaEncoding = getEncoding();
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 2388 */       setEncoding(realJavaEncoding);
/*      */     }
/*      */ 
/* 2391 */     return characterSetAlreadyConfigured;
/*      */   }
/*      */ 
/*      */   private void configureTimezone()
/*      */     throws SQLException
/*      */   {
/* 2402 */     String configuredTimeZoneOnServer = (String)this.serverVariables.get("timezone");
/*      */ 
/* 2405 */     if (configuredTimeZoneOnServer == null) {
/* 2406 */       configuredTimeZoneOnServer = (String)this.serverVariables.get("time_zone");
/*      */ 
/* 2409 */       if ("SYSTEM".equalsIgnoreCase(configuredTimeZoneOnServer)) {
/* 2410 */         configuredTimeZoneOnServer = (String)this.serverVariables.get("system_time_zone");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2415 */     if ((getUseTimezone()) && (configuredTimeZoneOnServer != null))
/*      */     {
/* 2417 */       String canoncicalTimezone = getServerTimezone();
/*      */ 
/* 2419 */       if ((canoncicalTimezone == null) || (canoncicalTimezone.length() == 0))
/*      */       {
/* 2421 */         String serverTimezoneStr = configuredTimeZoneOnServer;
/*      */         try
/*      */         {
/* 2424 */           canoncicalTimezone = TimeUtil.getCanoncialTimezone(serverTimezoneStr);
/*      */ 
/* 2427 */           if (canoncicalTimezone == null) {
/* 2428 */             throw new SQLException("Can't map timezone '" + serverTimezoneStr + "' to " + " canonical timezone.", "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */         catch (IllegalArgumentException iae)
/*      */         {
/* 2434 */           throw new SQLException(iae.getMessage(), "S1000");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2439 */       this.serverTimezoneTZ = TimeZone.getTimeZone(canoncicalTimezone);
/*      */ 
/* 2446 */       if ((!canoncicalTimezone.equalsIgnoreCase("GMT")) && (this.serverTimezoneTZ.getID().equals("GMT")))
/*      */       {
/* 2448 */         throw new SQLException("No timezone mapping entry for '" + canoncicalTimezone + "'", "S1009");
/*      */       }
/*      */ 
/* 2453 */       if ("GMT".equalsIgnoreCase(this.serverTimezoneTZ.getID()))
/* 2454 */         this.isServerTzUTC = true;
/*      */       else
/* 2456 */         this.isServerTzUTC = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createInitialHistogram(int[] counts, long[] breakpoints, long lowerBound, long upperBound)
/*      */   {
/* 2464 */     double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
/*      */ 
/* 2466 */     if (bucketSize < 1.0D) {
/* 2467 */       bucketSize = 1.0D;
/*      */     }
/*      */ 
/* 2470 */     for (int i = 0; i < 20; i++) {
/* 2471 */       breakpoints[i] = lowerBound;
/* 2472 */       lowerBound = ()(lowerBound + bucketSize);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected MysqlIO createNewIO(boolean isForReconnect)
/*      */     throws SQLException
/*      */   {
/* 2489 */     MysqlIO newIo = null;
/*      */ 
/* 2491 */     Properties mergedProps = new Properties();
/*      */ 
/* 2493 */     mergedProps = exposeAsProperties(this.props);
/*      */ 
/* 2495 */     long queriesIssuedFailedOverCopy = this.queriesIssuedFailedOver;
/* 2496 */     this.queriesIssuedFailedOver = 0L;
/*      */     try
/*      */     {
/* 2499 */       if ((!getHighAvailability()) && (!this.failedOver)) {
/* 2500 */         int hostIndex = 0;
/*      */ 
/* 2508 */         if (getRoundRobinLoadBalance()) {
/* 2509 */           hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */         }
/*      */ 
/* 2513 */         for (; hostIndex < this.hostListSize; hostIndex++) {
/*      */           try {
/* 2515 */             String newHostPortPair = (String)this.hostList.get(hostIndex);
/*      */ 
/* 2518 */             int newPort = 3306;
/*      */ 
/* 2520 */             String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */ 
/* 2522 */             String newHost = hostPortPair[0];
/*      */ 
/* 2524 */             if ((newHost == null) || (newHost.trim().length() == 0)) {
/* 2525 */               newHost = "localhost";
/*      */             }
/*      */ 
/* 2528 */             if (hostPortPair[1] != null) {
/*      */               try {
/* 2530 */                 newPort = Integer.parseInt(hostPortPair[1]);
/*      */               }
/*      */               catch (NumberFormatException nfe) {
/* 2533 */                 throw new SQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00");
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 2541 */             this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout());
/*      */ 
/* 2544 */             this.io.doHandshake(this.user, this.password, this.database);
/*      */ 
/* 2546 */             this.isClosed = false;
/*      */ 
/* 2549 */             boolean oldAutoCommit = getAutoCommit();
/* 2550 */             int oldIsolationLevel = this.isolationLevel;
/* 2551 */             boolean oldReadOnly = isReadOnly();
/* 2552 */             String oldCatalog = getCatalog();
/*      */ 
/* 2557 */             initializePropsFromServer(this.props);
/*      */ 
/* 2559 */             if (isForReconnect)
/*      */             {
/* 2561 */               setAutoCommit(oldAutoCommit);
/*      */ 
/* 2563 */               if (this.hasIsolationLevels) {
/* 2564 */                 setTransactionIsolation(oldIsolationLevel);
/*      */               }
/*      */ 
/* 2567 */               setCatalog(oldCatalog);
/*      */             }
/*      */ 
/* 2570 */             if (hostIndex != 0) {
/* 2571 */               setFailedOverState();
/* 2572 */               queriesIssuedFailedOverCopy = 0L;
/*      */             } else {
/* 2574 */               this.failedOver = false;
/* 2575 */               queriesIssuedFailedOverCopy = 0L;
/*      */ 
/* 2577 */               if (this.hostListSize > 1)
/* 2578 */                 setReadOnly(false);
/*      */               else {
/* 2580 */                 setReadOnly(oldReadOnly);
/*      */               }
/*      */             }
/*      */           }
/*      */           catch (SQLException sqlEx)
/*      */           {
/* 2586 */             if (this.io != null) {
/* 2587 */               this.io.forceClose();
/*      */             }
/*      */ 
/* 2590 */             String sqlState = sqlEx.getSQLState();
/*      */ 
/* 2592 */             if ((sqlState == null) || (!sqlState.equals("08S01")))
/*      */             {
/* 2595 */               throw sqlEx;
/*      */             }
/*      */ 
/* 2598 */             if (this.hostListSize - 1 == hostIndex)
/* 2599 */               throw sqlEx;
/*      */           }
/*      */           catch (Exception unknownException) {
/* 2602 */             if (this.io != null) {
/* 2603 */               this.io.forceClose();
/*      */             }
/*      */ 
/* 2606 */             if (this.hostListSize - 1 == hostIndex) {
/* 2607 */               throw new CommunicationsException(this, this.io != null ? this.io.getLastPacketSentTimeMs() : 0L, unknownException);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 2615 */         double timeout = getInitialTimeout();
/* 2616 */         boolean connectionGood = false;
/*      */ 
/* 2618 */         Exception connectionException = null;
/*      */ 
/* 2620 */         int hostIndex = 0;
/*      */ 
/* 2622 */         if (getRoundRobinLoadBalance()) {
/* 2623 */           hostIndex = getNextRoundRobinHostIndex(getURL(), this.hostList);
/*      */         }
/*      */ 
/* 2627 */         for (; (hostIndex < this.hostListSize) && (!connectionGood); hostIndex++)
/*      */         {
/* 2629 */           if ((this.preferSlaveDuringFailover) && (hostIndex == 0)) {
/* 2630 */             hostIndex++;
/*      */           }
/*      */ 
/* 2633 */           int attemptCount = 0;
/*      */           while (true) if ((attemptCount < getMaxReconnects()) && (!connectionGood)) {
/*      */               try {
/* 2636 */                 if (this.io != null) {
/* 2637 */                   this.io.forceClose();
/*      */                 }
/*      */ 
/* 2640 */                 String newHostPortPair = (String)this.hostList.get(hostIndex);
/*      */ 
/* 2643 */                 int newPort = 3306;
/*      */ 
/* 2645 */                 String[] hostPortPair = NonRegisteringDriver.parseHostPortPair(newHostPortPair);
/*      */ 
/* 2647 */                 String newHost = hostPortPair[0];
/*      */ 
/* 2649 */                 if ((newHost == null) || (newHost.trim().length() == 0)) {
/* 2650 */                   newHost = "localhost";
/*      */                 }
/*      */ 
/* 2653 */                 if (hostPortPair[1] != null) {
/*      */                   try {
/* 2655 */                     newPort = Integer.parseInt(hostPortPair[1]);
/*      */                   }
/*      */                   catch (NumberFormatException nfe) {
/* 2658 */                     throw new SQLException("Illegal connection port value '" + hostPortPair[1] + "'", "01S00");
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 2666 */                 this.io = new MysqlIO(newHost, newPort, mergedProps, getSocketFactoryClassName(), this, getSocketTimeout());
/*      */ 
/* 2669 */                 this.io.doHandshake(this.user, this.password, this.database);
/*      */ 
/* 2672 */                 pingInternal(false);
/* 2673 */                 this.isClosed = false;
/*      */ 
/* 2676 */                 boolean oldAutoCommit = getAutoCommit();
/* 2677 */                 int oldIsolationLevel = this.isolationLevel;
/* 2678 */                 boolean oldReadOnly = isReadOnly();
/* 2679 */                 String oldCatalog = getCatalog();
/*      */ 
/* 2684 */                 initializePropsFromServer(this.props);
/*      */ 
/* 2686 */                 if (isForReconnect)
/*      */                 {
/* 2688 */                   setAutoCommit(oldAutoCommit);
/*      */ 
/* 2690 */                   if (this.hasIsolationLevels) {
/* 2691 */                     setTransactionIsolation(oldIsolationLevel);
/*      */                   }
/*      */ 
/* 2694 */                   setCatalog(oldCatalog);
/*      */                 }
/*      */ 
/* 2697 */                 connectionGood = true;
/*      */ 
/* 2699 */                 if (hostIndex != 0) {
/* 2700 */                   setFailedOverState();
/* 2701 */                   queriesIssuedFailedOverCopy = 0L;
/*      */                 } else {
/* 2703 */                   this.failedOver = false;
/* 2704 */                   queriesIssuedFailedOverCopy = 0L;
/*      */ 
/* 2706 */                   if (this.hostListSize > 1)
/* 2707 */                     setReadOnly(false);
/*      */                   else {
/* 2709 */                     setReadOnly(oldReadOnly);
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (Exception IE)
/*      */               {
/* 2715 */                 connectionException = EEE;
/* 2716 */                 connectionGood = false;
/*      */ 
/* 2719 */                 if (!connectionGood)
/*      */                 {
/*      */                   try
/*      */                   {
/* 2724 */                     Thread.sleep(()timeout * 1000L);
/* 2725 */                     timeout *= 2.0D;
/*      */                   }
/*      */                   catch (InterruptedException IE)
/*      */                   {
/*      */                   }
/* 2634 */                   attemptCount++; continue;
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */ 
/*      */         }
/*      */ 
/* 2732 */         if (!connectionGood)
/*      */         {
/* 2734 */           throw new SQLException("Server connection failure during transaction. Due to underlying exception: '" + connectionException + "'." + (getParanoid() ? "" : Util.stackTraceToString(connectionException)) + "\nAttempted reconnect " + getMaxReconnects() + " times. Giving up.", "08001");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2747 */       if ((getParanoid()) && (!getHighAvailability()) && (this.hostListSize <= 1))
/*      */       {
/* 2749 */         this.password = null;
/* 2750 */         this.user = null;
/*      */       }
/*      */ 
/* 2753 */       if (isForReconnect)
/*      */       {
/* 2757 */         statementIter = this.openStatements.values().iterator();
/*      */ 
/* 2769 */         Stack serverPreparedStatements = null;
/*      */ 
/* 2771 */         while (statementIter.hasNext()) {
/* 2772 */           Object statementObj = statementIter.next();
/*      */ 
/* 2774 */           if ((statementObj instanceof ServerPreparedStatement)) {
/* 2775 */             if (serverPreparedStatements == null) {
/* 2776 */               serverPreparedStatements = new Stack();
/*      */             }
/*      */ 
/* 2779 */             serverPreparedStatements.add(statementObj);
/*      */           }
/*      */         }
/*      */ 
/* 2783 */         if (serverPreparedStatements != null) {
/* 2784 */           while (!serverPreparedStatements.isEmpty()) {
/* 2785 */             ((ServerPreparedStatement)serverPreparedStatements.pop()).rePrepare();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2791 */       Iterator statementIter = newIo;
/*      */       return statementIter; } finally { this.queriesIssuedFailedOver = queriesIssuedFailedOverCopy; } throw localObject1;
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement()
/*      */     throws SQLException
/*      */   {
/* 2807 */     return createStatement(1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 2825 */     checkClosed();
/*      */ 
/* 2827 */     Statement stmt = new Statement(this, this.database);
/* 2828 */     stmt.setResultSetType(resultSetType);
/* 2829 */     stmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 2831 */     return stmt;
/*      */   }
/*      */ 
/*      */   public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 2840 */     if ((getPedantic()) && 
/* 2841 */       (resultSetHoldability != 1)) {
/* 2842 */       throw new SQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/* 2848 */     return createStatement(resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   protected void dumpTestcaseQuery(String query) {
/* 2852 */     System.err.println(query);
/*      */   }
/*      */ 
/*      */   ResultSet execSQL(Statement callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, boolean queryIsSelectOnly, String catalog, boolean unpackFields)
/*      */     throws SQLException
/*      */   {
/* 2902 */     return execSQL(callingStatement, sql, maxRows, packet, resultSetType, resultSetConcurrency, streamResults, queryIsSelectOnly, catalog, unpackFields, 0);
/*      */   }
/*      */ 
/*      */   ResultSet execSQL(Statement callingStatement, String sql, int maxRows, Buffer packet, int resultSetType, int resultSetConcurrency, boolean streamResults, boolean queryIsSelectOnly, String catalog, boolean unpackFields, byte queryUsesVariables)
/*      */     throws SQLException
/*      */   {
/* 2916 */     synchronized (this.mutex) {
/* 2917 */       long queryStartTime = 0L;
/*      */ 
/* 2919 */       int endOfQueryPacketPosition = 0;
/*      */ 
/* 2921 */       if (packet != null) {
/* 2922 */         endOfQueryPacketPosition = packet.getPosition();
/*      */       }
/*      */ 
/* 2925 */       if (getGatherPerformanceMetrics()) {
/* 2926 */         queryStartTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 2929 */       this.lastQueryFinishedTime = 0L;
/*      */ 
/* 2931 */       if ((this.failedOver) && (this.autoCommit) && 
/* 2932 */         (shouldFallBack()) && (!this.executingFailoverReconnect)) {
/*      */         try {
/* 2934 */           this.executingFailoverReconnect = true;
/*      */ 
/* 2936 */           createNewIO(true);
/*      */ 
/* 2938 */           String connectedHost = this.io.getHost();
/*      */ 
/* 2940 */           if ((connectedHost != null) && (this.hostList.get(0).equals(connectedHost)))
/*      */           {
/* 2942 */             this.failedOver = false;
/* 2943 */             this.queriesIssuedFailedOver = 0L;
/* 2944 */             setReadOnly(false);
/*      */           }
/*      */         } finally {
/* 2947 */           this.executingFailoverReconnect = false;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2952 */       if (((getHighAvailability()) || (this.failedOver)) && ((this.autoCommit) || (getAutoReconnectForPools())) && (this.needsPing))
/*      */       {
/*      */         try
/*      */         {
/* 2956 */           pingInternal(false);
/*      */ 
/* 2958 */           this.needsPing = false;
/*      */         } catch (Exception Ex) {
/* 2960 */           createNewIO(true);
/*      */         }
/*      */       }
/*      */       try
/*      */       {
/* 2965 */         if (packet == null) {
/* 2966 */           encoding = null;
/*      */ 
/* 2968 */           if (getUseUnicode()) {
/* 2969 */             encoding = getEncoding();
/*      */           }
/*      */ 
/* 2972 */           ResultSet localResultSet = this.io.sqlQueryDirect(callingStatement, sql, encoding, null, maxRows, this, resultSetType, resultSetConcurrency, streamResults, catalog, unpackFields); jsr 338; return localResultSet;
/*      */         }
/*      */ 
/* 2978 */         String encoding = this.io.sqlQueryDirect(callingStatement, null, null, packet, maxRows, this, resultSetType, resultSetConcurrency, streamResults, catalog, unpackFields); jsr 303; return encoding;
/*      */       }
/*      */       catch (SQLException sqlE)
/*      */       {
/* 2985 */         if (getDumpQueriesOnException()) {
/* 2986 */           String extractedSql = extractSqlFromPacket(sql, packet, endOfQueryPacketPosition);
/*      */ 
/* 2988 */           StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/* 2990 */           messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/* 2992 */           messageBuf.append(extractedSql);
/*      */ 
/* 2994 */           sqlE = appendMessageToException(sqlE, messageBuf.toString());
/*      */         }
/*      */ 
/* 2997 */         if ((getHighAvailability()) || (this.failedOver)) {
/* 2998 */           this.needsPing = true;
/*      */         } else {
/* 3000 */           String sqlState = sqlE.getSQLState();
/*      */ 
/* 3002 */           if ((sqlState != null) && (sqlState.equals("08S01")))
/*      */           {
/* 3005 */             cleanup(new Throwable(), sqlE);
/*      */           }
/*      */         }
/*      */ 
/* 3009 */         throw sqlE;
/*      */       } catch (Exception ex) {
/* 3011 */         if ((getHighAvailability()) || (this.failedOver))
/* 3012 */           this.needsPing = true;
/* 3013 */         else if ((ex instanceof IOException)) {
/* 3014 */           cleanup(new Throwable(), ex);
/*      */         }
/*      */ 
/* 3017 */         String exceptionType = ex.getClass().getName();
/* 3018 */         String exceptionMessage = ex.getMessage();
/*      */ 
/* 3020 */         if (!getParanoid()) {
/* 3021 */           exceptionMessage = exceptionMessage + "\n\nNested Stack Trace:\n";
/* 3022 */           exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*      */         }
/*      */ 
/* 3025 */         throw new SQLException("Error during query: Unexpected Exception: " + exceptionType + " message given: " + exceptionMessage, "S1000");
/*      */       }
/*      */       finally
/*      */       {
/* 3031 */         jsr 6; } localObject3 = returnAddress; if (getMaintainTimeStats()) {
/* 3032 */         this.lastQueryFinishedTime = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 3035 */       if (this.failedOver) {
/* 3036 */         this.queriesIssuedFailedOver += 1L;
/*      */       }
/*      */ 
/* 3039 */       if (getGatherPerformanceMetrics()) {
/* 3040 */         long queryTime = System.currentTimeMillis() - queryStartTime;
/*      */ 
/* 3043 */         registerQueryExecutionTime(queryTime); } ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected String extractSqlFromPacket(String possibleSqlQuery, Buffer queryPacket, int endOfQueryPacketPosition)
/*      */     throws SQLException
/*      */   {
/* 3053 */     String extractedSql = null;
/*      */ 
/* 3055 */     if (possibleSqlQuery != null) {
/* 3056 */       if (possibleSqlQuery.length() > getMaxQuerySizeToLog()) {
/* 3057 */         StringBuffer truncatedQueryBuf = new StringBuffer(possibleSqlQuery.substring(0, getMaxQuerySizeToLog()));
/*      */ 
/* 3059 */         truncatedQueryBuf.append(Messages.getString("MysqlIO.25"));
/* 3060 */         extractedSql = truncatedQueryBuf.toString();
/*      */       } else {
/* 3062 */         extractedSql = possibleSqlQuery;
/*      */       }
/*      */     }
/*      */ 
/* 3066 */     if (extractedSql == null)
/*      */     {
/* 3070 */       int extractPosition = endOfQueryPacketPosition;
/*      */ 
/* 3072 */       boolean truncated = false;
/*      */ 
/* 3074 */       if (endOfQueryPacketPosition > getMaxQuerySizeToLog()) {
/* 3075 */         extractPosition = getMaxQuerySizeToLog();
/* 3076 */         truncated = true;
/*      */       }
/*      */ 
/* 3079 */       extractedSql = new String(queryPacket.getByteBuffer(), 5, extractPosition - 5);
/*      */ 
/* 3082 */       if (truncated) {
/* 3083 */         extractedSql = extractedSql + Messages.getString("MysqlIO.25");
/*      */       }
/*      */     }
/*      */ 
/* 3087 */     return extractedSql;
/*      */   }
/*      */ 
/*      */   protected void finalize()
/*      */     throws Throwable
/*      */   {
/* 3098 */     cleanup(new Throwable(), null);
/*      */   }
/*      */ 
/*      */   protected StringBuffer generateConnectionCommentBlock(StringBuffer buf) {
/* 3102 */     buf.append("/* conn id ");
/* 3103 */     buf.append(getId());
/* 3104 */     buf.append(" */ ");
/*      */ 
/* 3106 */     return buf;
/*      */   }
/*      */ 
/*      */   public synchronized int getActiveStatementCount() {
/* 3110 */     if (this.openStatements != null) {
/* 3111 */       return this.openStatements.size();
/*      */     }
/*      */ 
/* 3114 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean getAutoCommit()
/*      */     throws SQLException
/*      */   {
/* 3126 */     return this.autoCommit;
/*      */   }
/*      */ 
/*      */   public String getCatalog()
/*      */     throws SQLException
/*      */   {
/* 3141 */     return this.database;
/*      */   }
/*      */ 
/*      */   protected String getCharacterSetMetadata()
/*      */   {
/* 3148 */     return this.characterSetMetadata;
/*      */   }
/*      */ 
/*      */   synchronized SingleByteCharsetConverter getCharsetConverter(String javaEncodingName)
/*      */     throws SQLException
/*      */   {
/* 3161 */     if (javaEncodingName == null) {
/* 3162 */       return null;
/*      */     }
/*      */ 
/* 3165 */     SingleByteCharsetConverter converter = (SingleByteCharsetConverter)this.charsetConverterMap.get(javaEncodingName);
/*      */ 
/* 3168 */     if (converter == CHARSET_CONVERTER_NOT_AVAILABLE_MARKER) {
/* 3169 */       return null;
/*      */     }
/*      */ 
/* 3172 */     if (converter == null) {
/*      */       try {
/* 3174 */         converter = SingleByteCharsetConverter.getInstance(javaEncodingName, this);
/*      */ 
/* 3177 */         if (converter == null) {
/* 3178 */           this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */         }
/*      */ 
/* 3182 */         this.charsetConverterMap.put(javaEncodingName, converter);
/*      */       } catch (UnsupportedEncodingException unsupEncEx) {
/* 3184 */         this.charsetConverterMap.put(javaEncodingName, CHARSET_CONVERTER_NOT_AVAILABLE_MARKER);
/*      */ 
/* 3187 */         converter = null;
/*      */       }
/*      */     }
/*      */ 
/* 3191 */     return converter;
/*      */   }
/*      */ 
/*      */   protected String getCharsetNameForIndex(int charsetIndex)
/*      */     throws SQLException
/*      */   {
/* 3206 */     String charsetName = null;
/*      */ 
/* 3208 */     if (getUseOldUTF8Behavior()) {
/* 3209 */       return getEncoding();
/*      */     }
/*      */ 
/* 3212 */     if (charsetIndex != -1) {
/*      */       try {
/* 3214 */         charsetName = this.indexToCharsetMapping[charsetIndex];
/*      */ 
/* 3216 */         if ("sjis".equalsIgnoreCase(charsetName))
/*      */         {
/* 3218 */           if (CharsetMapping.isAliasForSjis(getEncoding()))
/* 3219 */             charsetName = getEncoding();
/*      */         }
/*      */       }
/*      */       catch (ArrayIndexOutOfBoundsException outOfBoundsEx) {
/* 3223 */         throw new SQLException("Unknown character set index for field '" + charsetIndex + "' received from server.", "S1000");
/*      */       }
/*      */ 
/* 3230 */       if (charsetName == null)
/* 3231 */         charsetName = getEncoding();
/*      */     }
/*      */     else {
/* 3234 */       charsetName = getEncoding();
/*      */     }
/*      */ 
/* 3237 */     return charsetName;
/*      */   }
/*      */ 
/*      */   protected TimeZone getDefaultTimeZone()
/*      */   {
/* 3246 */     return this.defaultTimeZone;
/*      */   }
/*      */ 
/*      */   public int getHoldability()
/*      */     throws SQLException
/*      */   {
/* 3253 */     return 2;
/*      */   }
/*      */ 
/*      */   int getId() {
/* 3257 */     return this.connectionId;
/*      */   }
/*      */ 
/*      */   public long getIdleFor()
/*      */   {
/* 3269 */     if (this.lastQueryFinishedTime == 0L) {
/* 3270 */       return 0L;
/*      */     }
/*      */ 
/* 3273 */     long now = System.currentTimeMillis();
/* 3274 */     long idleTime = now - this.lastQueryFinishedTime;
/*      */ 
/* 3276 */     return idleTime;
/*      */   }
/*      */ 
/*      */   protected MysqlIO getIO()
/*      */     throws SQLException
/*      */   {
/* 3287 */     if ((this.io == null) || (this.isClosed)) {
/* 3288 */       throw new SQLException("Operation not allowed on closed connection", "08003");
/*      */     }
/*      */ 
/* 3293 */     return this.io;
/*      */   }
/*      */ 
/*      */   public Log getLog()
/*      */     throws SQLException
/*      */   {
/* 3305 */     return this.log;
/*      */   }
/*      */ 
/*      */   int getMaxAllowedPacket()
/*      */   {
/* 3314 */     return this.maxAllowedPacket;
/*      */   }
/*      */ 
/*      */   protected synchronized int getMaxBytesPerChar(String javaCharsetName)
/*      */     throws SQLException
/*      */   {
/* 3320 */     String charset = CharsetMapping.getMysqlEncodingForJavaEncoding(javaCharsetName, this);
/*      */ 
/* 3323 */     if (versionMeetsMinimum(4, 1, 0)) {
/* 3324 */       if (this.charsetToNumBytesMap == null) {
/* 3325 */         this.charsetToNumBytesMap = new HashMap();
/*      */ 
/* 3327 */         java.sql.Statement stmt = null;
/* 3328 */         java.sql.ResultSet rs = null;
/*      */         try
/*      */         {
/* 3331 */           stmt = getMetadataSafeStatement();
/*      */ 
/* 3333 */           rs = stmt.executeQuery("SHOW CHARACTER SET");
/*      */ 
/* 3335 */           while (rs.next()) {
/* 3336 */             this.charsetToNumBytesMap.put(rs.getString("Charset"), new Integer(rs.getInt("Maxlen")));
/*      */           }
/*      */ 
/* 3340 */           rs.close();
/* 3341 */           rs = null;
/*      */ 
/* 3343 */           stmt.close();
/*      */ 
/* 3345 */           stmt = null;
/*      */         } finally {
/* 3347 */           if (rs != null) {
/* 3348 */             rs.close();
/* 3349 */             rs = null;
/*      */           }
/*      */ 
/* 3352 */           if (stmt != null) {
/* 3353 */             stmt.close();
/* 3354 */             stmt = null;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3359 */       Integer mbPerChar = (Integer)this.charsetToNumBytesMap.get(charset);
/*      */ 
/* 3362 */       if (mbPerChar != null) {
/* 3363 */         return mbPerChar.intValue();
/*      */       }
/*      */ 
/* 3366 */       return 1;
/*      */     }
/*      */ 
/* 3369 */     return 1;
/*      */   }
/*      */ 
/*      */   public java.sql.DatabaseMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 3383 */     checkClosed();
/*      */ 
/* 3385 */     return new DatabaseMetaData(this, this.database);
/*      */   }
/*      */ 
/*      */   protected java.sql.Statement getMetadataSafeStatement() throws SQLException {
/* 3389 */     java.sql.Statement stmt = createStatement();
/*      */ 
/* 3391 */     if (stmt.getMaxRows() != 0) {
/* 3392 */       stmt.setMaxRows(0);
/*      */     }
/*      */ 
/* 3395 */     stmt.setEscapeProcessing(false);
/*      */ 
/* 3397 */     return stmt;
/*      */   }
/*      */ 
/*      */   Object getMutex()
/*      */     throws SQLException
/*      */   {
/* 3408 */     if (this.io == null) {
/* 3409 */       throw new SQLException("Connection.close() has already been called. Invalid operation in this state.", "08003");
/*      */     }
/*      */ 
/* 3414 */     reportMetricsIfNeeded();
/*      */ 
/* 3416 */     return this.mutex;
/*      */   }
/*      */ 
/*      */   int getNetBufferLength()
/*      */   {
/* 3425 */     return this.netBufferLength;
/*      */   }
/*      */ 
/*      */   protected String getServerCharacterEncoding()
/*      */   {
/* 3434 */     return (String)this.serverVariables.get("character_set");
/*      */   }
/*      */ 
/*      */   int getServerMajorVersion() {
/* 3438 */     return this.io.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   int getServerMinorVersion() {
/* 3442 */     return this.io.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   int getServerSubMinorVersion() {
/* 3446 */     return this.io.getServerSubMinorVersion();
/*      */   }
/*      */ 
/*      */   public TimeZone getServerTimezoneTZ()
/*      */   {
/* 3455 */     return this.serverTimezoneTZ;
/*      */   }
/*      */ 
/*      */   String getServerVariable(String variableName) {
/* 3459 */     if (this.serverVariables != null) {
/* 3460 */       return (String)this.serverVariables.get(variableName);
/*      */     }
/*      */ 
/* 3463 */     return null;
/*      */   }
/*      */ 
/*      */   String getServerVersion() {
/* 3467 */     return this.io.getServerVersion();
/*      */   }
/*      */ 
/*      */   public int getTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 3479 */     if ((this.hasIsolationLevels) && (!getUseLocalSessionState())) {
/* 3480 */       java.sql.Statement stmt = null;
/* 3481 */       java.sql.ResultSet rs = null;
/*      */       try
/*      */       {
/* 3484 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 3486 */         String query = null;
/*      */ 
/* 3488 */         if (versionMeetsMinimum(4, 0, 3))
/* 3489 */           query = "SHOW VARIABLES LIKE 'tx_isolation'";
/*      */         else {
/* 3491 */           query = "SHOW VARIABLES LIKE 'transaction_isolation'";
/*      */         }
/*      */ 
/* 3494 */         rs = stmt.executeQuery(query);
/*      */ 
/* 3496 */         if (rs.next()) {
/* 3497 */           String s = rs.getString(2);
/*      */           int i;
/* 3499 */           if (s != null) {
/* 3500 */             Integer intTI = (Integer)mapTransIsolationNameToValue.get(s);
/*      */ 
/* 3503 */             if (intTI != null) {
/* 3504 */               i = intTI.intValue(); jsr 66;
/*      */             }
/*      */           }
/*      */ 
/* 3508 */           throw new SQLException("Could not map transaction isolation '" + s + " to a valid JDBC level.", "S1000");
/*      */         }
/*      */ 
/* 3514 */         throw new SQLException("Could not retrieve transaction isolation level from server", "S1000");
/*      */       }
/*      */       finally
/*      */       {
/* 3519 */         if (rs != null) {
/*      */           try {
/* 3521 */             rs.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3527 */           rs = null;
/*      */         }
/*      */ 
/* 3530 */         if (stmt != null) {
/*      */           try {
/* 3532 */             stmt.close();
/*      */           }
/*      */           catch (Exception ex)
/*      */           {
/*      */           }
/*      */ 
/* 3538 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3543 */     return this.isolationLevel;
/*      */   }
/*      */ 
/*      */   public synchronized Map getTypeMap()
/*      */     throws SQLException
/*      */   {
/* 3555 */     if (this.typeMap == null) {
/* 3556 */       this.typeMap = new HashMap();
/*      */     }
/*      */ 
/* 3559 */     return this.typeMap;
/*      */   }
/*      */ 
/*      */   String getURL() {
/* 3563 */     return this.myURL;
/*      */   }
/*      */ 
/*      */   String getUser() {
/* 3567 */     return this.user;
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 3580 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean hasSameProperties(Connection c) {
/* 3584 */     return this.props.equals(c.props);
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfPreparedExecutes()
/*      */   {
/* 3589 */     if (getGatherPerformanceMetrics()) {
/* 3590 */       this.numberOfPreparedExecutes += 1L;
/*      */ 
/* 3595 */       this.numberOfQueriesIssued += 1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfPrepares() {
/* 3600 */     if (getGatherPerformanceMetrics())
/* 3601 */       this.numberOfPrepares += 1L;
/*      */   }
/*      */ 
/*      */   protected void incrementNumberOfResultSetsCreated()
/*      */   {
/* 3606 */     if (getGatherPerformanceMetrics())
/* 3607 */       this.numberOfResultSetsCreated += 1L;
/*      */   }
/*      */ 
/*      */   private void initializeDriverProperties(Properties info)
/*      */     throws SQLException
/*      */   {
/* 3622 */     initializeProperties(info);
/*      */ 
/* 3624 */     this.log = LogFactory.getLogger(getLogger(), "MySQL");
/*      */ 
/* 3626 */     if ((getProfileSql()) || (getUseUsageAdvisor())) {
/* 3627 */       this.eventSink = ProfileEventSink.getInstance(this);
/*      */     }
/*      */ 
/* 3630 */     if (getCachePreparedStatements()) {
/* 3631 */       createPreparedStatementCaches();
/*      */     }
/*      */ 
/* 3634 */     if ((getNoDatetimeStringSync()) && (getUseTimezone()))
/* 3635 */       throw new SQLException("Can't enable noDatetimeSync and useTimezone configuration properties at the same time", "01S00");
/*      */   }
/*      */ 
/*      */   private void createPreparedStatementCaches()
/*      */   {
/* 3643 */     int cacheSize = getPreparedStatementCacheSize();
/*      */ 
/* 3645 */     this.cachedPreparedStatementParams = new HashMap(cacheSize);
/*      */ 
/* 3647 */     this.serverSideStatementCheckCache = new LRUCache(cacheSize);
/*      */ 
/* 3649 */     this.serverSideStatementCache = new LRUCache(cacheSize) {
/*      */       protected boolean removeEldestEntry(Map.Entry eldest) {
/* 3651 */         if (this.maxElements <= 1) {
/* 3652 */           return false;
/*      */         }
/*      */ 
/* 3655 */         boolean removeIt = super.removeEldestEntry(eldest);
/*      */ 
/* 3657 */         if (removeIt) {
/* 3658 */           ServerPreparedStatement ps = (ServerPreparedStatement)eldest.getValue();
/*      */ 
/* 3660 */           ps.isCached = false;
/* 3661 */           ps.setClosed(false);
/*      */           try
/*      */           {
/* 3664 */             ps.close();
/*      */           }
/*      */           catch (SQLException sqlEx)
/*      */           {
/*      */           }
/*      */         }
/* 3670 */         return removeIt;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private void initializePropsFromServer(Properties info)
/*      */     throws SQLException
/*      */   {
/* 3685 */     setSessionVariables();
/*      */ 
/* 3691 */     if (!versionMeetsMinimum(4, 1, 0)) {
/* 3692 */       setTransformedBitIsBoolean(false);
/*      */     }
/*      */ 
/* 3697 */     boolean clientCharsetIsConfigured = configureClientCharacterSet();
/*      */ 
/* 3699 */     this.parserKnowsUnicode = versionMeetsMinimum(4, 1, 0);
/*      */ 
/* 3704 */     if ((getUseServerPreparedStmts()) && (versionMeetsMinimum(4, 1, 0))) {
/* 3705 */       this.useServerPreparedStmts = true;
/*      */ 
/* 3707 */       if ((versionMeetsMinimum(5, 0, 0)) && (!versionMeetsMinimum(5, 0, 3))) {
/* 3708 */         this.useServerPreparedStmts = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3714 */     this.serverVariables.clear();
/*      */ 
/* 3719 */     if (versionMeetsMinimum(3, 21, 22)) {
/* 3720 */       loadServerVariables();
/*      */ 
/* 3722 */       buildCollationMapping();
/*      */ 
/* 3724 */       LicenseConfiguration.checkLicenseType(this.serverVariables);
/*      */ 
/* 3726 */       String lowerCaseTables = (String)this.serverVariables.get("lower_case_table_names");
/*      */ 
/* 3729 */       this.lowerCaseTableNames = (("on".equalsIgnoreCase(lowerCaseTables)) || ("1".equalsIgnoreCase(lowerCaseTables)) || ("2".equalsIgnoreCase(lowerCaseTables)));
/*      */ 
/* 3733 */       configureTimezone();
/*      */ 
/* 3735 */       if (this.serverVariables.containsKey("max_allowed_packet")) {
/* 3736 */         this.maxAllowedPacket = Integer.parseInt((String)this.serverVariables.get("max_allowed_packet"));
/*      */ 
/* 3740 */         int preferredBlobSendChunkSize = getBlobSendChunkSize();
/*      */ 
/* 3742 */         int allowedBlobSendChunkSize = Math.min(preferredBlobSendChunkSize, this.maxAllowedPacket) - 8192 - 11;
/*      */ 
/* 3747 */         setBlobSendChunkSize(String.valueOf(allowedBlobSendChunkSize));
/*      */       }
/*      */ 
/* 3750 */       if (this.serverVariables.containsKey("net_buffer_length")) {
/* 3751 */         this.netBufferLength = Integer.parseInt((String)this.serverVariables.get("net_buffer_length"));
/*      */       }
/*      */ 
/* 3756 */       checkTransactionIsolationLevel();
/*      */ 
/* 3764 */       if (!clientCharsetIsConfigured) {
/* 3765 */         checkServerEncoding();
/*      */       }
/*      */ 
/* 3768 */       this.io.checkForCharsetMismatch();
/*      */ 
/* 3770 */       if (this.serverVariables.containsKey("sql_mode")) {
/* 3771 */         int sqlMode = 0;
/*      */ 
/* 3773 */         String sqlModeAsString = (String)this.serverVariables.get("sql_mode");
/*      */         try
/*      */         {
/* 3776 */           sqlMode = Integer.parseInt(sqlModeAsString);
/*      */         }
/*      */         catch (NumberFormatException nfe)
/*      */         {
/* 3780 */           sqlMode = 0;
/*      */ 
/* 3782 */           if (sqlModeAsString != null) {
/* 3783 */             if (sqlModeAsString.indexOf("ANSI_QUOTES") != -1) {
/* 3784 */               sqlMode |= 4;
/*      */             }
/*      */ 
/* 3787 */             if (sqlModeAsString.indexOf("NO_BACKSLASH_ESCAPES") != -1) {
/* 3788 */               this.noBackslashEscapes = true;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 3793 */         if ((sqlMode & 0x4) > 0)
/* 3794 */           this.useAnsiQuotes = true;
/*      */         else {
/* 3796 */           this.useAnsiQuotes = false;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3801 */     if (versionMeetsMinimum(3, 23, 15)) {
/* 3802 */       this.transactionsSupported = true;
/* 3803 */       setAutoCommit(true);
/*      */     }
/*      */     else
/*      */     {
/* 3807 */       this.transactionsSupported = false;
/*      */     }
/*      */ 
/* 3810 */     if (versionMeetsMinimum(3, 23, 36))
/* 3811 */       this.hasIsolationLevels = true;
/*      */     else {
/* 3813 */       this.hasIsolationLevels = false;
/*      */     }
/*      */ 
/* 3816 */     this.hasQuotedIdentifiers = versionMeetsMinimum(3, 23, 6);
/*      */ 
/* 3818 */     this.io.resetMaxBuf();
/*      */ 
/* 3825 */     if (this.io.versionMeetsMinimum(4, 1, 0)) {
/* 3826 */       String characterSetResultsOnServerMysql = (String)this.serverVariables.get("character_set_results");
/*      */ 
/* 3829 */       if ((characterSetResultsOnServerMysql == null) || (StringUtils.startsWithIgnoreCaseAndWs(characterSetResultsOnServerMysql, "NULL")))
/*      */       {
/* 3832 */         String defaultMetadataCharsetMysql = (String)this.serverVariables.get("character_set_system");
/*      */ 
/* 3834 */         String defaultMetadataCharset = null;
/*      */ 
/* 3836 */         if (defaultMetadataCharsetMysql != null) {
/* 3837 */           defaultMetadataCharset = CharsetMapping.getJavaEncodingForMysqlEncoding(defaultMetadataCharsetMysql, this);
/*      */         }
/*      */         else
/*      */         {
/* 3841 */           defaultMetadataCharset = "UTF-8";
/*      */         }
/*      */ 
/* 3844 */         this.characterSetMetadata = defaultMetadataCharset;
/*      */       } else {
/* 3846 */         this.characterSetResultsOnServer = CharsetMapping.getJavaEncodingForMysqlEncoding(characterSetResultsOnServerMysql, this);
/*      */ 
/* 3849 */         this.characterSetMetadata = this.characterSetResultsOnServer;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3857 */     if ((versionMeetsMinimum(4, 1, 0)) && (!versionMeetsMinimum(4, 1, 10)) && (getAllowMultiQueries()))
/*      */     {
/* 3860 */       if (("ON".equalsIgnoreCase((String)this.serverVariables.get("query_cache_type"))) && (!"0".equalsIgnoreCase((String)this.serverVariables.get("query_cache_size"))))
/*      */       {
/* 3864 */         setAllowMultiQueries(false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean isClientTzUTC() {
/* 3870 */     return this.isClientTzUTC;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isClosed()
/*      */   {
/* 3879 */     return this.isClosed;
/*      */   }
/*      */ 
/*      */   public synchronized boolean isMasterConnection()
/*      */   {
/* 3890 */     return !this.failedOver;
/*      */   }
/*      */ 
/*      */   public boolean isNoBackslashEscapesSet()
/*      */   {
/* 3900 */     return this.noBackslashEscapes;
/*      */   }
/*      */ 
/*      */   boolean isReadInfoMsgEnabled() {
/* 3904 */     return this.readInfoMsg;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 3917 */     return this.readOnly;
/*      */   }
/*      */ 
/*      */   protected boolean isServerTzUTC() {
/* 3921 */     return this.isServerTzUTC;
/*      */   }
/*      */ 
/*      */   private void loadServerVariables()
/*      */     throws SQLException
/*      */   {
/* 3933 */     if (getCacheServerConfiguration()) {
/* 3934 */       synchronized (serverConfigByUrl) {
/* 3935 */         Map cachedVariableMap = (Map)serverConfigByUrl.get(getURL());
/*      */ 
/* 3937 */         if (cachedVariableMap != null) {
/* 3938 */           this.serverVariables = cachedVariableMap;
/*      */ 
/* 3940 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3945 */     Statement stmt = null;
/* 3946 */     ResultSet results = null;
/*      */     try
/*      */     {
/* 3949 */       stmt = (Statement)createStatement();
/* 3950 */       stmt.setEscapeProcessing(false);
/*      */ 
/* 3952 */       results = (ResultSet)stmt.executeQuery("SHOW VARIABLES");
/*      */ 
/* 3955 */       while (results.next()) {
/* 3956 */         this.serverVariables.put(results.getString(1), results.getString(2));
/*      */       }
/*      */ 
/* 3960 */       if (getCacheServerConfiguration())
/* 3961 */         synchronized (serverConfigByUrl) {
/* 3962 */           serverConfigByUrl.put(getURL(), this.serverVariables);
/*      */         }
/*      */     }
/*      */     catch (SQLException e) {
/* 3966 */       throw e;
/*      */     } finally {
/* 3968 */       if (results != null) {
/*      */         try {
/* 3970 */           results.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */       }
/* 3976 */       if (stmt != null)
/*      */         try {
/* 3978 */           stmt.close();
/*      */         }
/*      */         catch (SQLException sqlE)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean lowerCaseTableNames()
/*      */   {
/* 3992 */     return this.lowerCaseTableNames;
/*      */   }
/*      */ 
/*      */   void maxRowsChanged(Statement stmt)
/*      */   {
/* 4002 */     synchronized (this.mutex) {
/* 4003 */       if (this.statementsUsingMaxRows == null) {
/* 4004 */         this.statementsUsingMaxRows = new HashMap();
/*      */       }
/*      */ 
/* 4007 */       this.statementsUsingMaxRows.put(stmt, stmt);
/*      */ 
/* 4009 */       this.maxRowsChanged = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String nativeSQL(String sql)
/*      */     throws SQLException
/*      */   {
/* 4026 */     if (sql == null) {
/* 4027 */       return null;
/*      */     }
/*      */ 
/* 4030 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn());
/*      */ 
/* 4033 */     if ((escapedSqlResult instanceof String)) {
/* 4034 */       return (String)escapedSqlResult;
/*      */     }
/*      */ 
/* 4037 */     return ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */   }
/*      */ 
/*      */   private CallableStatement parseCallableStatement(String sql) throws SQLException
/*      */   {
/* 4042 */     Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, serverSupportsConvertFn());
/*      */ 
/* 4045 */     boolean isFunctionCall = false;
/* 4046 */     String parsedSql = null;
/*      */ 
/* 4048 */     if ((escapedSqlResult instanceof EscapeProcessorResult)) {
/* 4049 */       parsedSql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/* 4050 */       isFunctionCall = ((EscapeProcessorResult)escapedSqlResult).callingStoredFunction;
/*      */     } else {
/* 4052 */       parsedSql = (String)escapedSqlResult;
/* 4053 */       isFunctionCall = false;
/*      */     }
/*      */ 
/* 4056 */     return new CallableStatement(this, parsedSql, this.database, isFunctionCall);
/*      */   }
/*      */ 
/*      */   public boolean parserKnowsUnicode()
/*      */   {
/* 4066 */     return this.parserKnowsUnicode;
/*      */   }
/*      */ 
/*      */   public void ping()
/*      */     throws SQLException
/*      */   {
/* 4076 */     pingInternal(true);
/*      */   }
/*      */ 
/*      */   private void pingInternal(boolean checkForClosedConnection) throws SQLException
/*      */   {
/* 4081 */     if (checkForClosedConnection) {
/* 4082 */       checkClosed();
/*      */     }
/*      */ 
/* 4086 */     this.io.sendCommand(14, null, null, false, null);
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql)
/*      */     throws SQLException
/*      */   {
/* 4100 */     if (getUseUltraDevWorkAround()) {
/* 4101 */       return new UltraDevWorkAround(prepareStatement(sql));
/*      */     }
/*      */ 
/* 4104 */     return prepareCall(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4125 */     if (versionMeetsMinimum(5, 0, 0)) {
/* 4126 */       CallableStatement cStmt = null;
/*      */ 
/* 4128 */       if (!getCacheCallableStatements())
/*      */       {
/* 4130 */         cStmt = parseCallableStatement(sql);
/*      */       } else {
/* 4132 */         if (this.parsedCallableStatementCache == null) {
/* 4133 */           this.parsedCallableStatementCache = new LRUCache(getCallableStatementCacheSize());
/*      */         }
/*      */ 
/* 4137 */         CompoundCacheKey key = new CompoundCacheKey(getCatalog(), sql);
/*      */ 
/* 4139 */         CallableStatement.CallableStatementParamInfo cachedParamInfo = (CallableStatement.CallableStatementParamInfo)this.parsedCallableStatementCache.get(key);
/*      */ 
/* 4142 */         if (cachedParamInfo != null) {
/* 4143 */           cStmt = new CallableStatement(this, cachedParamInfo);
/*      */         } else {
/* 4145 */           cStmt = parseCallableStatement(sql);
/*      */ 
/* 4147 */           cachedParamInfo = cStmt.paramInfo;
/*      */ 
/* 4149 */           this.parsedCallableStatementCache.put(key, cachedParamInfo);
/*      */         }
/*      */       }
/*      */ 
/* 4153 */       cStmt.setResultSetType(resultSetType);
/* 4154 */       cStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 4156 */       return cStmt;
/*      */     }
/*      */ 
/* 4159 */     throw new SQLException("Callable statements not supported.", "S1C00");
/*      */   }
/*      */ 
/*      */   public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4169 */     if ((getPedantic()) && 
/* 4170 */       (resultSetHoldability != 1)) {
/* 4171 */       throw new SQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/* 4177 */     CallableStatement cStmt = (CallableStatement)prepareCall(sql, resultSetType, resultSetConcurrency);
/*      */ 
/* 4180 */     return cStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql)
/*      */     throws SQLException
/*      */   {
/* 4210 */     return prepareStatement(sql, 1003, 1007);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int autoGenKeyIndex)
/*      */     throws SQLException
/*      */   {
/* 4219 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4221 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys(autoGenKeyIndex == 1);
/*      */ 
/* 4224 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
/*      */     throws SQLException
/*      */   {
/* 4244 */     checkClosed();
/*      */ 
/* 4250 */     PreparedStatement pStmt = null;
/*      */ 
/* 4252 */     boolean canServerPrepare = true;
/*      */ 
/* 4254 */     if (getEmulateUnsupportedPstmts()) {
/* 4255 */       canServerPrepare = canHandleAsServerPreparedStatement(sql);
/*      */     }
/*      */ 
/* 4258 */     if ((this.useServerPreparedStmts) && (canServerPrepare)) {
/* 4259 */       if (getCachePreparedStatements()) {
/* 4260 */         pStmt = (ServerPreparedStatement)this.serverSideStatementCache.remove(sql);
/*      */ 
/* 4262 */         if (pStmt != null) {
/* 4263 */           pStmt.clearParameters();
/* 4264 */           ((ServerPreparedStatement)pStmt).setClosed(false);
/*      */         }
/*      */       }
/*      */ 
/* 4268 */       if (pStmt == null)
/*      */         try {
/* 4270 */           pStmt = new ServerPreparedStatement(this, sql, this.database);
/*      */ 
/* 4272 */           if ((getCachePreparedStatements()) && (sql.length() < getPreparedStatementCacheSqlLimit()))
/*      */           {
/* 4274 */             ((ServerPreparedStatement)pStmt).isCached = true;
/*      */           }
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 4278 */           if (getEmulateUnsupportedPstmts()) {
/* 4279 */             pStmt = clientPrepareStatement(sql);
/*      */ 
/* 4281 */             if ((getCachePreparedStatements()) && (sql.length() < getPreparedStatementCacheSqlLimit()))
/*      */             {
/* 4283 */               this.serverSideStatementCheckCache.put(sql, Boolean.FALSE);
/*      */             }
/*      */           } else {
/* 4286 */             throw sqlEx;
/*      */           }
/*      */         }
/*      */     }
/*      */     else {
/* 4291 */       pStmt = clientPrepareStatement(sql);
/*      */     }
/*      */ 
/* 4295 */     pStmt.setResultSetType(resultSetType);
/* 4296 */     pStmt.setResultSetConcurrency(resultSetConcurrency);
/*      */ 
/* 4298 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
/*      */     throws SQLException
/*      */   {
/* 4307 */     if ((getPedantic()) && 
/* 4308 */       (resultSetHoldability != 1)) {
/* 4309 */       throw new SQLException("HOLD_CUSRORS_OVER_COMMIT is only supported holdability level", "S1009");
/*      */     }
/*      */ 
/* 4315 */     return prepareStatement(sql, resultSetType, resultSetConcurrency);
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, int[] autoGenKeyIndexes)
/*      */     throws SQLException
/*      */   {
/* 4323 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4325 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyIndexes != null) && (autoGenKeyIndexes.length > 0));
/*      */ 
/* 4329 */     return pStmt;
/*      */   }
/*      */ 
/*      */   public java.sql.PreparedStatement prepareStatement(String sql, String[] autoGenKeyColNames)
/*      */     throws SQLException
/*      */   {
/* 4337 */     java.sql.PreparedStatement pStmt = prepareStatement(sql);
/*      */ 
/* 4339 */     ((PreparedStatement)pStmt).setRetrieveGeneratedKeys((autoGenKeyColNames != null) && (autoGenKeyColNames.length > 0));
/*      */ 
/* 4343 */     return pStmt;
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly, boolean issueRollback, boolean skipLocalTeardown, Throwable reason)
/*      */     throws SQLException
/*      */   {
/* 4358 */     SQLException sqlEx = null;
/*      */ 
/* 4360 */     if (isClosed()) {
/* 4361 */       return;
/*      */     }
/*      */ 
/* 4364 */     this.forceClosedReason = reason;
/*      */     try
/*      */     {
/* 4367 */       if (!skipLocalTeardown) {
/* 4368 */         if ((!getAutoCommit()) && (issueRollback)) {
/*      */           try {
/* 4370 */             rollback();
/*      */           } catch (SQLException ex) {
/* 4372 */             sqlEx = ex;
/*      */           }
/*      */         }
/*      */ 
/* 4376 */         reportMetrics();
/*      */ 
/* 4378 */         if (getUseUsageAdvisor()) {
/* 4379 */           if (!calledExplicitly) {
/* 4380 */             String message = "Connection implicitly closed by Driver. You should call Connection.close() from your code to free resources more efficiently and avoid resource leaks.";
/*      */ 
/* 4382 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/* 4389 */           long connectionLifeTime = System.currentTimeMillis() - this.connectionCreationTimeMillis;
/*      */ 
/* 4392 */           if (connectionLifeTime < 500L) {
/* 4393 */             String message = "Connection lifetime of < .5 seconds. You might be un-necessarily creating short-lived connections and should investigate connection pooling to be more efficient.";
/*      */ 
/* 4395 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", getCatalog(), getId(), -1, -1, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 4404 */           closeAllOpenStatements();
/*      */         } catch (SQLException ex) {
/* 4406 */           sqlEx = ex;
/*      */         }
/*      */ 
/* 4409 */         if (this.io != null)
/*      */           try {
/* 4411 */             this.io.quit();
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*      */           }
/*      */       }
/*      */       else {
/* 4418 */         this.io.forceClose();
/*      */       }
/*      */     } finally {
/* 4421 */       this.openStatements = null;
/* 4422 */       this.io = null;
/* 4423 */       this.isClosed = true;
/*      */     }
/*      */ 
/* 4426 */     if (sqlEx != null)
/* 4427 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected void registerQueryExecutionTime(long queryTimeMs)
/*      */   {
/* 4438 */     if (queryTimeMs > this.longestQueryTimeMs) {
/* 4439 */       this.longestQueryTimeMs = queryTimeMs;
/*      */ 
/* 4441 */       repartitionPerformanceHistogram();
/*      */     }
/*      */ 
/* 4444 */     addToPerformanceHistogram(queryTimeMs, 1);
/*      */ 
/* 4446 */     if (queryTimeMs < this.shortestQueryTimeMs) {
/* 4447 */       this.shortestQueryTimeMs = (queryTimeMs == 0L ? 1L : queryTimeMs);
/*      */     }
/*      */ 
/* 4450 */     this.numberOfQueriesIssued += 1L;
/*      */ 
/* 4452 */     this.totalQueryTimeMs += queryTimeMs;
/*      */   }
/*      */ 
/*      */   synchronized void registerStatement(Statement stmt)
/*      */   {
/* 4462 */     this.openStatements.put(stmt, stmt);
/*      */   }
/*      */ 
/*      */   public void releaseSavepoint(Savepoint arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound)
/*      */   {
/* 4475 */     if (this.oldHistCounts == null) {
/* 4476 */       this.oldHistCounts = new int[histCounts.length];
/* 4477 */       this.oldHistBreakpoints = new long[histBreakpoints.length];
/*      */     }
/*      */ 
/* 4480 */     for (int i = 0; i < histCounts.length; i++) {
/* 4481 */       this.oldHistCounts[i] = histCounts[i];
/*      */     }
/*      */ 
/* 4484 */     for (int i = 0; i < this.oldHistBreakpoints.length; i++) {
/* 4485 */       this.oldHistBreakpoints[i] = histBreakpoints[i];
/*      */     }
/*      */ 
/* 4488 */     createInitialHistogram(histCounts, histBreakpoints, currentLowerBound, currentUpperBound);
/*      */ 
/* 4491 */     for (int i = 0; i < 20; i++)
/* 4492 */       addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound);
/*      */   }
/*      */ 
/*      */   private void repartitionPerformanceHistogram()
/*      */   {
/* 4498 */     checkAndCreatePerformanceHistogram();
/*      */ 
/* 4500 */     repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, this.shortestQueryTimeMs == 9223372036854775807L ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
/*      */   }
/*      */ 
/*      */   private void repartitionTablesAccessedHistogram()
/*      */   {
/* 4507 */     checkAndCreateTablesAccessedHistogram();
/*      */ 
/* 4509 */     repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, this.minimumNumberTablesAccessed == 9223372036854775807L ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
/*      */   }
/*      */ 
/*      */   private void reportMetrics()
/*      */   {
/* 4517 */     if (getGatherPerformanceMetrics()) {
/* 4518 */       StringBuffer logMessage = new StringBuffer(256);
/*      */ 
/* 4520 */       logMessage.append("** Performance Metrics Report **\n");
/* 4521 */       logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
/*      */ 
/* 4523 */       logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
/*      */ 
/* 4525 */       logMessage.append("\nAverage query execution time: " + this.totalQueryTimeMs / this.numberOfQueriesIssued + " ms");
/*      */ 
/* 4529 */       logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
/*      */ 
/* 4531 */       logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
/*      */ 
/* 4533 */       logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
/*      */ 
/* 4535 */       logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
/*      */ 
/* 4538 */       if (this.perfMetricsHistBreakpoints != null) {
/* 4539 */         logMessage.append("\n\n\tTiming Histogram:\n");
/* 4540 */         int maxNumPoints = 20;
/* 4541 */         int highestCount = -2147483648;
/*      */ 
/* 4543 */         for (int i = 0; i < 20; i++) {
/* 4544 */           if (this.perfMetricsHistCounts[i] > highestCount) {
/* 4545 */             highestCount = this.perfMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4549 */         if (highestCount == 0) {
/* 4550 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4553 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4555 */           if (i == 0) {
/* 4556 */             logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4560 */             logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[(i + 1)] + " ms: \t" + this.perfMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4566 */           logMessage.append("\t");
/*      */ 
/* 4568 */           int numPointsToGraph = (int)(maxNumPoints * (this.perfMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4570 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4571 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4574 */           if (this.longestQueryTimeMs < this.perfMetricsHistCounts[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4579 */         if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
/* 4580 */           logMessage.append("\n\tbetween ");
/* 4581 */           logMessage.append(this.perfMetricsHistBreakpoints[18]);
/*      */ 
/* 4583 */           logMessage.append(" and ");
/* 4584 */           logMessage.append(this.perfMetricsHistBreakpoints[19]);
/*      */ 
/* 4586 */           logMessage.append(" ms: \t");
/* 4587 */           logMessage.append(this.perfMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4592 */       if (this.numTablesMetricsHistBreakpoints != null) {
/* 4593 */         logMessage.append("\n\n\tTable Join Histogram:\n");
/* 4594 */         int maxNumPoints = 20;
/* 4595 */         int highestCount = -2147483648;
/*      */ 
/* 4597 */         for (int i = 0; i < 20; i++) {
/* 4598 */           if (this.numTablesMetricsHistCounts[i] > highestCount) {
/* 4599 */             highestCount = this.numTablesMetricsHistCounts[i];
/*      */           }
/*      */         }
/*      */ 
/* 4603 */         if (highestCount == 0) {
/* 4604 */           highestCount = 1;
/*      */         }
/*      */ 
/* 4607 */         for (int i = 0; i < 19; i++)
/*      */         {
/* 4609 */           if (i == 0) {
/* 4610 */             logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */           else
/*      */           {
/* 4615 */             logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[(i + 1)] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
/*      */           }
/*      */ 
/* 4623 */           logMessage.append("\t");
/*      */ 
/* 4625 */           int numPointsToGraph = (int)(maxNumPoints * (this.numTablesMetricsHistCounts[i] / highestCount));
/*      */ 
/* 4627 */           for (int j = 0; j < numPointsToGraph; j++) {
/* 4628 */             logMessage.append("*");
/*      */           }
/*      */ 
/* 4631 */           if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[(i + 1)])
/*      */           {
/*      */             break;
/*      */           }
/*      */         }
/* 4636 */         if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
/* 4637 */           logMessage.append("\n\tbetween ");
/* 4638 */           logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
/*      */ 
/* 4640 */           logMessage.append(" and ");
/* 4641 */           logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
/*      */ 
/* 4643 */           logMessage.append(" tables: ");
/* 4644 */           logMessage.append(this.numTablesMetricsHistCounts[19]);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4649 */       this.log.logInfo(logMessage);
/*      */ 
/* 4651 */       this.metricsLastReportedMs = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void reportMetricsIfNeeded()
/*      */   {
/* 4660 */     if ((getGatherPerformanceMetrics()) && 
/* 4661 */       (System.currentTimeMillis() - this.metricsLastReportedMs > getReportMetricsIntervalMillis()))
/* 4662 */       reportMetrics();
/*      */   }
/*      */ 
/*      */   protected void reportNumberOfTablesAccessed(int numTablesAccessed)
/*      */   {
/* 4668 */     if (numTablesAccessed < this.minimumNumberTablesAccessed) {
/* 4669 */       this.minimumNumberTablesAccessed = numTablesAccessed;
/*      */     }
/*      */ 
/* 4672 */     if (numTablesAccessed > this.maximumNumberTablesAccessed) {
/* 4673 */       this.maximumNumberTablesAccessed = numTablesAccessed;
/*      */ 
/* 4675 */       repartitionTablesAccessedHistogram();
/*      */     }
/*      */ 
/* 4678 */     addToTablesAccessedHistogram(numTablesAccessed, 1);
/*      */   }
/*      */ 
/*      */   public void resetServerState()
/*      */     throws SQLException
/*      */   {
/* 4690 */     if (!getParanoid()) if ((this.io != null & versionMeetsMinimum(4, 0, 6)))
/*      */       {
/* 4692 */         changeUser(this.user, this.password);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void rollback()
/*      */     throws SQLException
/*      */   {
/* 4706 */     checkClosed();
/*      */     try
/*      */     {
/* 4710 */       if ((this.autoCommit) && (!getRelaxAutoCommit())) {
/* 4711 */         throw new SQLException("Can't call rollback when autocommit=true", "08003");
/*      */       }
/*      */ 
/* 4714 */       if (this.transactionsSupported)
/*      */         try {
/* 4716 */           rollbackNoChecks();
/*      */         }
/*      */         catch (SQLException sqlEx) {
/* 4719 */           if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196))
/*      */           {
/* 4721 */             throw sqlEx;
/*      */           }
/*      */         }
/*      */     }
/*      */     catch (SQLException sqlException) {
/* 4726 */       if ("08S01".equals(sqlException.getSQLState()))
/*      */       {
/* 4728 */         throw new SQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007");
/*      */       }
/*      */ 
/* 4733 */       throw sqlException;
/*      */     } finally {
/* 4735 */       this.needsPing = getReconnectAtTxEnd();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rollback(Savepoint savepoint)
/*      */     throws SQLException
/*      */   {
/* 4744 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 4745 */       checkClosed();
/*      */       try
/*      */       {
/* 4748 */         StringBuffer rollbackQuery = new StringBuffer("ROLLBACK TO SAVEPOINT ");
/*      */ 
/* 4750 */         rollbackQuery.append('`');
/* 4751 */         rollbackQuery.append(savepoint.getSavepointName());
/* 4752 */         rollbackQuery.append('`');
/*      */ 
/* 4754 */         java.sql.Statement stmt = null;
/*      */         try
/*      */         {
/* 4757 */           stmt = createStatement();
/*      */ 
/* 4759 */           stmt.executeUpdate(rollbackQuery.toString());
/*      */         } catch (SQLException sqlEx) {
/* 4761 */           int errno = sqlEx.getErrorCode();
/*      */ 
/* 4763 */           if (errno == 1181) {
/* 4764 */             String msg = sqlEx.getMessage();
/*      */ 
/* 4766 */             if (msg != null) {
/* 4767 */               int indexOfError153 = msg.indexOf("153");
/*      */ 
/* 4769 */               if (indexOfError153 != -1) {
/* 4770 */                 throw new SQLException("Savepoint '" + savepoint.getSavepointName() + "' does not exist", "S1009", errno);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 4780 */           if ((getIgnoreNonTxTables()) && (sqlEx.getErrorCode() != 1196))
/*      */           {
/* 4782 */             throw sqlEx;
/*      */           }
/*      */ 
/* 4785 */           if ("08S01".equals(sqlEx.getSQLState()))
/*      */           {
/* 4787 */             throw new SQLException("Communications link failure during rollback(). Transaction resolution unknown.", "08007");
/*      */           }
/*      */ 
/* 4792 */           throw sqlEx;
/*      */         } finally {
/* 4794 */           if (stmt != null) {
/*      */             try {
/* 4796 */               stmt.close();
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/*      */             }
/* 4801 */             stmt = null;
/*      */           }
/*      */         }
/*      */       } finally {
/* 4805 */         this.needsPing = getReconnectAtTxEnd();
/*      */       }
/*      */     } else {
/* 4808 */       throw new NotImplemented();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void rollbackNoChecks()
/*      */     throws SQLException
/*      */   {
/* 4819 */     execSQL(null, "rollback", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */   }
/*      */ 
/*      */   public ServerPreparedStatement serverPrepare(String sql)
/*      */     throws SQLException
/*      */   {
/* 4836 */     return new ServerPreparedStatement(this, sql, getCatalog());
/*      */   }
/*      */ 
/*      */   protected boolean serverSupportsConvertFn() throws SQLException {
/* 4840 */     return versionMeetsMinimum(4, 0, 2);
/*      */   }
/*      */ 
/*      */   public void setAutoCommit(boolean autoCommitFlag)
/*      */     throws SQLException
/*      */   {
/* 4866 */     checkClosed();
/*      */ 
/* 4868 */     if (getAutoReconnectForPools()) {
/* 4869 */       setHighAvailability(true);
/*      */     }
/*      */     try
/*      */     {
/* 4873 */       if (this.transactionsSupported)
/*      */       {
/* 4875 */         boolean needsSetOnServer = true;
/*      */ 
/* 4877 */         if ((getUseLocalSessionState()) && (this.autoCommit == autoCommitFlag))
/*      */         {
/* 4879 */           needsSetOnServer = false;
/* 4880 */         } else if (!getHighAvailability()) {
/* 4881 */           needsSetOnServer = getIO().isSetNeededForAutoCommitMode(autoCommitFlag);
/*      */         }
/*      */ 
/* 4892 */         this.autoCommit = autoCommitFlag;
/*      */ 
/* 4894 */         if (needsSetOnServer) {
/* 4895 */           execSQL(null, autoCommitFlag ? "SET autocommit=1" : "SET autocommit=0", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 4903 */         if ((!autoCommitFlag) && (!getRelaxAutoCommit())) {
/* 4904 */           throw new SQLException("MySQL Versions Older than 3.23.15 do not support transactions", "08003");
/*      */         }
/*      */ 
/* 4909 */         this.autoCommit = autoCommitFlag;
/*      */       }
/*      */     } finally {
/* 4912 */       if (getAutoReconnectForPools())
/* 4913 */         setHighAvailability(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCatalog(String catalog)
/*      */     throws SQLException
/*      */   {
/* 4934 */     checkClosed();
/*      */ 
/* 4936 */     if (catalog == null) {
/* 4937 */       throw new SQLException("Catalog can not be null", "S1009");
/*      */     }
/*      */ 
/* 4941 */     if (getUseLocalSessionState()) {
/* 4942 */       if (this.lowerCaseTableNames) {
/* 4943 */         if (this.database.equalsIgnoreCase(catalog)) {
/* 4944 */           return;
/*      */         }
/*      */       }
/* 4947 */       else if (this.database.equals(catalog)) {
/* 4948 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4953 */     String quotedId = this.dbmd.getIdentifierQuoteString();
/*      */ 
/* 4955 */     if ((quotedId == null) || (quotedId.equals(" "))) {
/* 4956 */       quotedId = "";
/*      */     }
/*      */ 
/* 4959 */     StringBuffer query = new StringBuffer("USE ");
/* 4960 */     query.append(quotedId);
/* 4961 */     query.append(catalog);
/* 4962 */     query.append(quotedId);
/*      */ 
/* 4964 */     execSQL(null, query.toString(), -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */ 
/* 4968 */     this.database = catalog;
/*      */   }
/*      */ 
/*      */   public void setFailedOver(boolean flag)
/*      */   {
/* 4976 */     this.failedOver = flag;
/*      */   }
/*      */ 
/*      */   private void setFailedOverState()
/*      */     throws SQLException
/*      */   {
/* 4986 */     if (getFailOverReadOnly()) {
/* 4987 */       setReadOnly(true);
/*      */     }
/*      */ 
/* 4990 */     this.queriesIssuedFailedOver = 0L;
/* 4991 */     this.failedOver = true;
/* 4992 */     this.masterFailTimeMillis = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public void setHoldability(int arg0)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setPreferSlaveDuringFailover(boolean flag)
/*      */   {
/* 5008 */     this.preferSlaveDuringFailover = flag;
/*      */   }
/*      */ 
/*      */   void setReadInfoMsgEnabled(boolean flag) {
/* 5012 */     this.readInfoMsg = flag;
/*      */   }
/*      */ 
/*      */   public void setReadOnly(boolean readOnlyFlag)
/*      */     throws SQLException
/*      */   {
/* 5026 */     checkClosed();
/* 5027 */     this.readOnly = readOnlyFlag;
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint()
/*      */     throws SQLException
/*      */   {
/* 5034 */     MysqlSavepoint savepoint = new MysqlSavepoint();
/*      */ 
/* 5036 */     setSavepoint(savepoint);
/*      */ 
/* 5038 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private void setSavepoint(MysqlSavepoint savepoint) throws SQLException
/*      */   {
/* 5043 */     if ((versionMeetsMinimum(4, 0, 14)) || (versionMeetsMinimum(4, 1, 1))) {
/* 5044 */       checkClosed();
/*      */ 
/* 5046 */       StringBuffer savePointQuery = new StringBuffer("SAVEPOINT ");
/* 5047 */       savePointQuery.append('`');
/* 5048 */       savePointQuery.append(savepoint.getSavepointName());
/* 5049 */       savePointQuery.append('`');
/*      */ 
/* 5051 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5054 */         stmt = createStatement();
/*      */ 
/* 5056 */         stmt.executeUpdate(savePointQuery.toString());
/*      */       } finally {
/* 5058 */         if (stmt != null) {
/*      */           try {
/* 5060 */             stmt.close();
/*      */           }
/*      */           catch (SQLException sqlEx)
/*      */           {
/*      */           }
/* 5065 */           stmt = null;
/*      */         }
/*      */       }
/*      */     } else {
/* 5069 */       throw new NotImplemented();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Savepoint setSavepoint(String name)
/*      */     throws SQLException
/*      */   {
/* 5077 */     MysqlSavepoint savepoint = new MysqlSavepoint(name);
/*      */ 
/* 5079 */     setSavepoint(savepoint);
/*      */ 
/* 5081 */     return savepoint;
/*      */   }
/*      */ 
/*      */   private void setSessionVariables()
/*      */     throws SQLException
/*      */   {
/* 5088 */     if ((versionMeetsMinimum(4, 0, 0)) && (getSessionVariables() != null)) {
/* 5089 */       List variablesToSet = StringUtils.split(getSessionVariables(), ",", "\"'", "\"'", false);
/*      */ 
/* 5092 */       int numVariablesToSet = variablesToSet.size();
/*      */ 
/* 5094 */       java.sql.Statement stmt = null;
/*      */       try
/*      */       {
/* 5097 */         stmt = getMetadataSafeStatement();
/*      */ 
/* 5099 */         for (int i = 0; i < numVariablesToSet; i++) {
/* 5100 */           String variableValuePair = (String)variablesToSet.get(i);
/*      */ 
/* 5102 */           if (variableValuePair.startsWith("@"))
/* 5103 */             stmt.executeUpdate("SET " + variableValuePair);
/*      */           else
/* 5105 */             stmt.executeUpdate("SET SESSION " + variableValuePair);
/*      */         }
/*      */       }
/*      */       finally {
/* 5109 */         if (stmt != null)
/* 5110 */           stmt.close();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTransactionIsolation(int level)
/*      */     throws SQLException
/*      */   {
/* 5126 */     checkClosed();
/*      */ 
/* 5128 */     if (this.hasIsolationLevels) {
/* 5129 */       String sql = null;
/*      */ 
/* 5131 */       boolean shouldSendSet = false;
/*      */ 
/* 5133 */       if (getAlwaysSendSetIsolation()) {
/* 5134 */         shouldSendSet = true;
/*      */       }
/* 5136 */       else if (level != this.isolationLevel) {
/* 5137 */         shouldSendSet = true;
/*      */       }
/*      */ 
/* 5141 */       if (getUseLocalSessionState()) {
/* 5142 */         shouldSendSet = this.isolationLevel != level;
/*      */       }
/*      */ 
/* 5145 */       if (shouldSendSet) {
/* 5146 */         switch (level) {
/*      */         case 0:
/* 5148 */           throw new SQLException("Transaction isolation level NONE not supported by MySQL");
/*      */         case 2:
/* 5152 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED";
/*      */ 
/* 5154 */           break;
/*      */         case 1:
/* 5157 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED";
/*      */ 
/* 5159 */           break;
/*      */         case 4:
/* 5162 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ";
/*      */ 
/* 5164 */           break;
/*      */         case 8:
/* 5167 */           sql = "SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE";
/*      */ 
/* 5169 */           break;
/*      */         case 3:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         default:
/* 5172 */           throw new SQLException("Unsupported transaction isolation level '" + level + "'", "S1C00");
/*      */         }
/*      */ 
/* 5177 */         execSQL(null, sql, -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */ 
/* 5182 */         this.isolationLevel = level;
/*      */       }
/*      */     } else {
/* 5185 */       throw new SQLException("Transaction Isolation Levels are not supported on MySQL versions older than 3.23.36.", "S1C00");
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setTypeMap(Map map)
/*      */     throws SQLException
/*      */   {
/* 5201 */     this.typeMap = map;
/*      */   }
/*      */ 
/*      */   private boolean shouldFallBack()
/*      */   {
/* 5212 */     long secondsSinceFailedOver = (System.currentTimeMillis() - this.masterFailTimeMillis) / 1000L;
/*      */ 
/* 5215 */     boolean tryFallback = (secondsSinceFailedOver >= getSecondsBeforeRetryMaster()) || (this.queriesIssuedFailedOver >= getQueriesBeforeRetryMaster());
/*      */ 
/* 5217 */     return tryFallback;
/*      */   }
/*      */ 
/*      */   public void shutdownServer()
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5228 */       this.io.sendCommand(8, null, null, false, null);
/*      */     } catch (Exception ex) {
/* 5230 */       throw new SQLException("Unhandled exception '" + ex.toString() + "'", "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean supportsIsolationLevel()
/*      */   {
/* 5241 */     return this.hasIsolationLevels;
/*      */   }
/*      */ 
/*      */   public boolean supportsQuotedIdentifiers()
/*      */   {
/* 5250 */     return this.hasQuotedIdentifiers;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */   {
/* 5259 */     return this.transactionsSupported;
/*      */   }
/*      */ 
/*      */   synchronized void unregisterStatement(Statement stmt)
/*      */   {
/* 5269 */     if (this.openStatements != null)
/* 5270 */       this.openStatements.remove(stmt);
/*      */   }
/*      */ 
/*      */   void unsetMaxRows(Statement stmt)
/*      */     throws SQLException
/*      */   {
/* 5285 */     synchronized (this.mutex) {
/* 5286 */       if (this.statementsUsingMaxRows != null) {
/* 5287 */         Object found = this.statementsUsingMaxRows.remove(stmt);
/*      */ 
/* 5289 */         if ((found != null) && (this.statementsUsingMaxRows.size() == 0))
/*      */         {
/* 5291 */           execSQL(null, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.database, true, 0);
/*      */ 
/* 5296 */           this.maxRowsChanged = false;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean useAnsiQuotedIdentifiers() {
/* 5303 */     return this.useAnsiQuotes;
/*      */   }
/*      */ 
/*      */   boolean useMaxRows()
/*      */   {
/* 5312 */     synchronized (this.mutex) {
/* 5313 */       return this.maxRowsChanged;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected synchronized void recachePreparedStatement(ServerPreparedStatement pstmt) {
/* 5318 */     this.serverSideStatementCache.put(pstmt.originalSql, pstmt);
/*      */   }
/*      */ 
/*      */   public boolean versionMeetsMinimum(int major, int minor, int subminor) throws SQLException
/*      */   {
/* 5323 */     checkClosed();
/*      */ 
/* 5325 */     return this.io.versionMeetsMinimum(major, minor, subminor);
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/* 1062 */     mapTransIsolationNameToValue = new HashMap(8);
/* 1063 */     mapTransIsolationNameToValue.put("READ-UNCOMMITED", new Integer(1));
/*      */ 
/* 1065 */     mapTransIsolationNameToValue.put("READ-UNCOMMITTED", new Integer(1));
/*      */ 
/* 1067 */     mapTransIsolationNameToValue.put("READ-COMMITTED", new Integer(2));
/*      */ 
/* 1069 */     mapTransIsolationNameToValue.put("REPEATABLE-READ", new Integer(4));
/*      */ 
/* 1071 */     mapTransIsolationNameToValue.put("SERIALIZABLE", new Integer(8));
/*      */   }
/*      */ 
/*      */   class UltraDevWorkAround
/*      */     implements java.sql.CallableStatement
/*      */   {
/*  146 */     private java.sql.PreparedStatement delegate = null;
/*      */ 
/*      */     UltraDevWorkAround(java.sql.PreparedStatement pstmt) {
/*  149 */       this.delegate = pstmt;
/*      */     }
/*      */ 
/*      */     public void addBatch() throws SQLException {
/*  153 */       this.delegate.addBatch();
/*      */     }
/*      */ 
/*      */     public void addBatch(String p1) throws SQLException {
/*  157 */       this.delegate.addBatch(p1);
/*      */     }
/*      */ 
/*      */     public void cancel() throws SQLException {
/*  161 */       this.delegate.cancel();
/*      */     }
/*      */ 
/*      */     public void clearBatch() throws SQLException {
/*  165 */       this.delegate.clearBatch();
/*      */     }
/*      */ 
/*      */     public void clearParameters() throws SQLException {
/*  169 */       this.delegate.clearParameters();
/*      */     }
/*      */ 
/*      */     public void clearWarnings() throws SQLException {
/*  173 */       this.delegate.clearWarnings();
/*      */     }
/*      */ 
/*      */     public void close() throws SQLException {
/*  177 */       this.delegate.close();
/*      */     }
/*      */ 
/*      */     public boolean execute() throws SQLException {
/*  181 */       return this.delegate.execute();
/*      */     }
/*      */ 
/*      */     public boolean execute(String p1) throws SQLException {
/*  185 */       return this.delegate.execute(p1);
/*      */     }
/*      */ 
/*      */     public boolean execute(String arg0, int arg1)
/*      */       throws SQLException
/*      */     {
/*  192 */       return this.delegate.execute(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public boolean execute(String arg0, int[] arg1)
/*      */       throws SQLException
/*      */     {
/*  199 */       return this.delegate.execute(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public boolean execute(String arg0, String[] arg1)
/*      */       throws SQLException
/*      */     {
/*  206 */       return this.delegate.execute(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public int[] executeBatch() throws SQLException {
/*  210 */       return this.delegate.executeBatch();
/*      */     }
/*      */ 
/*      */     public java.sql.ResultSet executeQuery() throws SQLException {
/*  214 */       return this.delegate.executeQuery();
/*      */     }
/*      */ 
/*      */     public java.sql.ResultSet executeQuery(String p1) throws SQLException
/*      */     {
/*  219 */       return this.delegate.executeQuery(p1);
/*      */     }
/*      */ 
/*      */     public int executeUpdate() throws SQLException {
/*  223 */       return this.delegate.executeUpdate();
/*      */     }
/*      */ 
/*      */     public int executeUpdate(String p1) throws SQLException {
/*  227 */       return this.delegate.executeUpdate(p1);
/*      */     }
/*      */ 
/*      */     public int executeUpdate(String arg0, int arg1)
/*      */       throws SQLException
/*      */     {
/*  234 */       return this.delegate.executeUpdate(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public int executeUpdate(String arg0, int[] arg1)
/*      */       throws SQLException
/*      */     {
/*  241 */       return this.delegate.executeUpdate(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public int executeUpdate(String arg0, String[] arg1)
/*      */       throws SQLException
/*      */     {
/*  249 */       return this.delegate.executeUpdate(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public java.sql.Array getArray(int p1) throws SQLException {
/*  253 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public java.sql.Array getArray(String arg0)
/*      */       throws SQLException
/*      */     {
/*  260 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public BigDecimal getBigDecimal(int p1) throws SQLException {
/*  264 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     /** @deprecated */
/*      */     public BigDecimal getBigDecimal(int p1, int p2)
/*      */       throws SQLException
/*      */     {
/*  281 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public BigDecimal getBigDecimal(String arg0)
/*      */       throws SQLException
/*      */     {
/*  288 */       return null;
/*      */     }
/*      */ 
/*      */     public Blob getBlob(int p1) throws SQLException {
/*  292 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Blob getBlob(String arg0)
/*      */       throws SQLException
/*      */     {
/*  299 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public boolean getBoolean(int p1) throws SQLException {
/*  303 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public boolean getBoolean(String arg0)
/*      */       throws SQLException
/*      */     {
/*  310 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public byte getByte(int p1) throws SQLException {
/*  314 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public byte getByte(String arg0)
/*      */       throws SQLException
/*      */     {
/*  321 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public byte[] getBytes(int p1) throws SQLException {
/*  325 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public byte[] getBytes(String arg0)
/*      */       throws SQLException
/*      */     {
/*  332 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Clob getClob(int p1) throws SQLException {
/*  336 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Clob getClob(String arg0)
/*      */       throws SQLException
/*      */     {
/*  343 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public java.sql.Connection getConnection() throws SQLException {
/*  347 */       return this.delegate.getConnection();
/*      */     }
/*      */ 
/*      */     public Date getDate(int p1) throws SQLException {
/*  351 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Date getDate(int p1, Calendar p2) throws SQLException
/*      */     {
/*  356 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Date getDate(String arg0)
/*      */       throws SQLException
/*      */     {
/*  363 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Date getDate(String arg0, Calendar arg1)
/*      */       throws SQLException
/*      */     {
/*  370 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public double getDouble(int p1) throws SQLException {
/*  374 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public double getDouble(String arg0)
/*      */       throws SQLException
/*      */     {
/*  381 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public int getFetchDirection() throws SQLException {
/*  385 */       return this.delegate.getFetchDirection();
/*      */     }
/*      */ 
/*      */     public int getFetchSize() throws SQLException {
/*  389 */       return this.delegate.getFetchSize();
/*      */     }
/*      */ 
/*      */     public float getFloat(int p1) throws SQLException {
/*  393 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public float getFloat(String arg0)
/*      */       throws SQLException
/*      */     {
/*  400 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public java.sql.ResultSet getGeneratedKeys()
/*      */       throws SQLException
/*      */     {
/*  407 */       return this.delegate.getGeneratedKeys();
/*      */     }
/*      */ 
/*      */     public int getInt(int p1) throws SQLException {
/*  411 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public int getInt(String arg0)
/*      */       throws SQLException
/*      */     {
/*  418 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public long getLong(int p1) throws SQLException {
/*  422 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public long getLong(String arg0)
/*      */       throws SQLException
/*      */     {
/*  429 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public int getMaxFieldSize() throws SQLException {
/*  433 */       return this.delegate.getMaxFieldSize();
/*      */     }
/*      */ 
/*      */     public int getMaxRows() throws SQLException {
/*  437 */       return this.delegate.getMaxRows();
/*      */     }
/*      */ 
/*      */     public ResultSetMetaData getMetaData() throws SQLException {
/*  441 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public boolean getMoreResults() throws SQLException {
/*  445 */       return this.delegate.getMoreResults();
/*      */     }
/*      */ 
/*      */     public boolean getMoreResults(int arg0)
/*      */       throws SQLException
/*      */     {
/*  452 */       return this.delegate.getMoreResults();
/*      */     }
/*      */ 
/*      */     public Object getObject(int p1) throws SQLException {
/*  456 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Object getObject(int p1, Map p2) throws SQLException
/*      */     {
/*  461 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Object getObject(String arg0)
/*      */       throws SQLException
/*      */     {
/*  468 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Object getObject(String arg0, Map arg1)
/*      */       throws SQLException
/*      */     {
/*  475 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public ParameterMetaData getParameterMetaData()
/*      */       throws SQLException
/*      */     {
/*  482 */       return this.delegate.getParameterMetaData();
/*      */     }
/*      */ 
/*      */     public int getQueryTimeout() throws SQLException {
/*  486 */       return this.delegate.getQueryTimeout();
/*      */     }
/*      */ 
/*      */     public Ref getRef(int p1) throws SQLException {
/*  490 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Ref getRef(String arg0)
/*      */       throws SQLException
/*      */     {
/*  497 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public java.sql.ResultSet getResultSet() throws SQLException {
/*  501 */       return this.delegate.getResultSet();
/*      */     }
/*      */ 
/*      */     public int getResultSetConcurrency() throws SQLException {
/*  505 */       return this.delegate.getResultSetConcurrency();
/*      */     }
/*      */ 
/*      */     public int getResultSetHoldability()
/*      */       throws SQLException
/*      */     {
/*  512 */       return this.delegate.getResultSetHoldability();
/*      */     }
/*      */ 
/*      */     public int getResultSetType() throws SQLException {
/*  516 */       return this.delegate.getResultSetType();
/*      */     }
/*      */ 
/*      */     public short getShort(int p1) throws SQLException {
/*  520 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public short getShort(String arg0)
/*      */       throws SQLException
/*      */     {
/*  527 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public String getString(int p1) throws SQLException {
/*  531 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public String getString(String arg0)
/*      */       throws SQLException
/*      */     {
/*  538 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Time getTime(int p1) throws SQLException {
/*  542 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Time getTime(int p1, Calendar p2) throws SQLException
/*      */     {
/*  547 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Time getTime(String arg0)
/*      */       throws SQLException
/*      */     {
/*  554 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Time getTime(String arg0, Calendar arg1)
/*      */       throws SQLException
/*      */     {
/*  561 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(int p1) throws SQLException {
/*  565 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(int p1, Calendar p2) throws SQLException
/*      */     {
/*  570 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(String arg0)
/*      */       throws SQLException
/*      */     {
/*  577 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public Timestamp getTimestamp(String arg0, Calendar arg1)
/*      */       throws SQLException
/*      */     {
/*  585 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public int getUpdateCount() throws SQLException {
/*  589 */       return this.delegate.getUpdateCount();
/*      */     }
/*      */ 
/*      */     public URL getURL(int arg0)
/*      */       throws SQLException
/*      */     {
/*  596 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public URL getURL(String arg0)
/*      */       throws SQLException
/*      */     {
/*  603 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public SQLWarning getWarnings() throws SQLException {
/*  607 */       return this.delegate.getWarnings();
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(int p1, int p2) throws SQLException {
/*  611 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(int p1, int p2, int p3) throws SQLException
/*      */     {
/*  616 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(int p1, int p2, String p3) throws SQLException
/*      */     {
/*  621 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(String arg0, int arg1)
/*      */       throws SQLException
/*      */     {
/*  629 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(String arg0, int arg1, int arg2)
/*      */       throws SQLException
/*      */     {
/*  637 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void registerOutParameter(String arg0, int arg1, String arg2)
/*      */       throws SQLException
/*      */     {
/*  645 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setArray(int p1, java.sql.Array p2) throws SQLException
/*      */     {
/*  650 */       this.delegate.setArray(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setAsciiStream(int p1, InputStream p2, int p3) throws SQLException
/*      */     {
/*  655 */       this.delegate.setAsciiStream(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setAsciiStream(String arg0, InputStream arg1, int arg2)
/*      */       throws SQLException
/*      */     {
/*  663 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setBigDecimal(int p1, BigDecimal p2) throws SQLException
/*      */     {
/*  668 */       this.delegate.setBigDecimal(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setBigDecimal(String arg0, BigDecimal arg1)
/*      */       throws SQLException
/*      */     {
/*  676 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setBinaryStream(int p1, InputStream p2, int p3) throws SQLException
/*      */     {
/*  681 */       this.delegate.setBinaryStream(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setBinaryStream(String arg0, InputStream arg1, int arg2)
/*      */       throws SQLException
/*      */     {
/*  689 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setBlob(int p1, Blob p2) throws SQLException {
/*  693 */       this.delegate.setBlob(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setBoolean(int p1, boolean p2) throws SQLException {
/*  697 */       this.delegate.setBoolean(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setBoolean(String arg0, boolean arg1)
/*      */       throws SQLException
/*      */     {
/*  704 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setByte(int p1, byte p2) throws SQLException {
/*  708 */       this.delegate.setByte(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setByte(String arg0, byte arg1)
/*      */       throws SQLException
/*      */     {
/*  715 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setBytes(int p1, byte[] p2) throws SQLException {
/*  719 */       this.delegate.setBytes(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setBytes(String arg0, byte[] arg1)
/*      */       throws SQLException
/*      */     {
/*  726 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setCharacterStream(int p1, Reader p2, int p3) throws SQLException
/*      */     {
/*  731 */       this.delegate.setCharacterStream(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setCharacterStream(String arg0, Reader arg1, int arg2)
/*      */       throws SQLException
/*      */     {
/*  739 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setClob(int p1, Clob p2) throws SQLException {
/*  743 */       this.delegate.setClob(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setCursorName(String p1) throws SQLException {
/*  747 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void setDate(int p1, Date p2) throws SQLException {
/*  751 */       this.delegate.setDate(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setDate(int p1, Date p2, Calendar p3) throws SQLException
/*      */     {
/*  756 */       this.delegate.setDate(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setDate(String arg0, Date arg1)
/*      */       throws SQLException
/*      */     {
/*  763 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setDate(String arg0, Date arg1, Calendar arg2)
/*      */       throws SQLException
/*      */     {
/*  771 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setDouble(int p1, double p2) throws SQLException {
/*  775 */       this.delegate.setDouble(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setDouble(String arg0, double arg1)
/*      */       throws SQLException
/*      */     {
/*  782 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setEscapeProcessing(boolean p1) throws SQLException {
/*  786 */       this.delegate.setEscapeProcessing(p1);
/*      */     }
/*      */ 
/*      */     public void setFetchDirection(int p1) throws SQLException {
/*  790 */       this.delegate.setFetchDirection(p1);
/*      */     }
/*      */ 
/*      */     public void setFetchSize(int p1) throws SQLException {
/*  794 */       this.delegate.setFetchSize(p1);
/*      */     }
/*      */ 
/*      */     public void setFloat(int p1, float p2) throws SQLException {
/*  798 */       this.delegate.setFloat(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setFloat(String arg0, float arg1)
/*      */       throws SQLException
/*      */     {
/*  805 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setInt(int p1, int p2) throws SQLException {
/*  809 */       this.delegate.setInt(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setInt(String arg0, int arg1)
/*      */       throws SQLException
/*      */     {
/*  816 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setLong(int p1, long p2) throws SQLException {
/*  820 */       this.delegate.setLong(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setLong(String arg0, long arg1)
/*      */       throws SQLException
/*      */     {
/*  827 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setMaxFieldSize(int p1) throws SQLException {
/*  831 */       this.delegate.setMaxFieldSize(p1);
/*      */     }
/*      */ 
/*      */     public void setMaxRows(int p1) throws SQLException {
/*  835 */       this.delegate.setMaxRows(p1);
/*      */     }
/*      */ 
/*      */     public void setNull(int p1, int p2) throws SQLException {
/*  839 */       this.delegate.setNull(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setNull(int p1, int p2, String p3) throws SQLException
/*      */     {
/*  844 */       this.delegate.setNull(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setNull(String arg0, int arg1)
/*      */       throws SQLException
/*      */     {
/*  851 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setNull(String arg0, int arg1, String arg2)
/*      */       throws SQLException
/*      */     {
/*  859 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setObject(int p1, Object p2) throws SQLException
/*      */     {
/*  864 */       this.delegate.setObject(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setObject(int p1, Object p2, int p3) throws SQLException
/*      */     {
/*  869 */       this.delegate.setObject(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setObject(int p1, Object p2, int p3, int p4) throws SQLException
/*      */     {
/*  874 */       this.delegate.setObject(p1, p2, p3, p4);
/*      */     }
/*      */ 
/*      */     public void setObject(String arg0, Object arg1)
/*      */       throws SQLException
/*      */     {
/*  881 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setObject(String arg0, Object arg1, int arg2)
/*      */       throws SQLException
/*      */     {
/*  889 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setObject(String arg0, Object arg1, int arg2, int arg3)
/*      */       throws SQLException
/*      */     {
/*  897 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setQueryTimeout(int p1) throws SQLException {
/*  901 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void setRef(int p1, Ref p2) throws SQLException {
/*  905 */       throw new SQLException("Not supported");
/*      */     }
/*      */ 
/*      */     public void setShort(int p1, short p2) throws SQLException {
/*  909 */       this.delegate.setShort(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setShort(String arg0, short arg1)
/*      */       throws SQLException
/*      */     {
/*  916 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setString(int p1, String p2) throws SQLException
/*      */     {
/*  921 */       this.delegate.setString(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setString(String arg0, String arg1)
/*      */       throws SQLException
/*      */     {
/*  928 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setTime(int p1, Time p2) throws SQLException {
/*  932 */       this.delegate.setTime(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setTime(int p1, Time p2, Calendar p3) throws SQLException
/*      */     {
/*  937 */       this.delegate.setTime(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setTime(String arg0, Time arg1)
/*      */       throws SQLException
/*      */     {
/*  944 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setTime(String arg0, Time arg1, Calendar arg2)
/*      */       throws SQLException
/*      */     {
/*  952 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setTimestamp(int p1, Timestamp p2) throws SQLException
/*      */     {
/*  957 */       this.delegate.setTimestamp(p1, p2);
/*      */     }
/*      */ 
/*      */     public void setTimestamp(int p1, Timestamp p2, Calendar p3) throws SQLException
/*      */     {
/*  962 */       this.delegate.setTimestamp(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setTimestamp(String arg0, Timestamp arg1)
/*      */       throws SQLException
/*      */     {
/*  970 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public void setTimestamp(String arg0, Timestamp arg1, Calendar arg2)
/*      */       throws SQLException
/*      */     {
/*  978 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     /** @deprecated */
/*      */     public void setUnicodeStream(int p1, InputStream p2, int p3)
/*      */       throws SQLException
/*      */     {
/*  996 */       this.delegate.setUnicodeStream(p1, p2, p3);
/*      */     }
/*      */ 
/*      */     public void setURL(int arg0, URL arg1)
/*      */       throws SQLException
/*      */     {
/* 1003 */       this.delegate.setURL(arg0, arg1);
/*      */     }
/*      */ 
/*      */     public void setURL(String arg0, URL arg1)
/*      */       throws SQLException
/*      */     {
/* 1010 */       throw new NotImplemented();
/*      */     }
/*      */ 
/*      */     public boolean wasNull() throws SQLException {
/* 1014 */       throw new SQLException("Not supported");
/*      */     }
/*      */   }
/*      */ 
/*      */   class CompoundCacheKey
/*      */   {
/*      */     String componentOne;
/*      */     String componentTwo;
/*      */     int hashCode;
/*      */ 
/*      */     CompoundCacheKey(String partOne, String partTwo)
/*      */     {
/*   97 */       this.componentOne = partOne;
/*   98 */       this.componentTwo = partTwo;
/*      */ 
/*  102 */       this.hashCode = ((this.componentOne != null ? this.componentOne : "") + this.componentTwo).hashCode();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj)
/*      */     {
/*  112 */       if ((obj instanceof CompoundCacheKey)) {
/*  113 */         CompoundCacheKey another = (CompoundCacheKey)obj;
/*      */ 
/*  115 */         boolean firstPartEqual = false;
/*      */ 
/*  117 */         if (this.componentOne == null)
/*  118 */           firstPartEqual = another.componentOne == null;
/*      */         else {
/*  120 */           firstPartEqual = this.componentOne.equals(another.componentOne);
/*      */         }
/*      */ 
/*  124 */         return (firstPartEqual) && (this.componentTwo.equals(another.componentTwo));
/*      */       }
/*      */ 
/*  128 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  137 */       return this.hashCode;
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.Connection
 * JD-Core Version:    0.6.0
 */
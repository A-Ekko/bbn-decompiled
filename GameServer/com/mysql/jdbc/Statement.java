/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import com.mysql.jdbc.util.LRUCache;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class Statement
/*      */   implements java.sql.Statement
/*      */ {
/*   76 */   protected static int statementCounter = 1;
/*      */   public static final byte USES_VARIABLES_FALSE = 0;
/*      */   public static final byte USES_VARIABLES_TRUE = 1;
/*      */   public static final byte USES_VARIABLES_UNKNOWN = -1;
/*      */   protected List batchedArgs;
/*   88 */   protected SingleByteCharsetConverter charConverter = null;
/*      */ 
/*   91 */   protected String charEncoding = null;
/*      */ 
/*   94 */   protected Connection connection = null;
/*      */ 
/*   97 */   protected String currentCatalog = null;
/*      */ 
/*  100 */   protected boolean doEscapeProcessing = true;
/*      */ 
/*  103 */   protected ProfileEventSink eventSink = null;
/*      */ 
/*  106 */   private int fetchSize = 0;
/*      */ 
/*  109 */   protected boolean isClosed = false;
/*      */ 
/*  112 */   protected long lastInsertId = -1L;
/*      */ 
/*  115 */   protected int maxFieldSize = MysqlIO.getMaxBuf();
/*      */ 
/*  121 */   protected int maxRows = -1;
/*      */ 
/*  124 */   protected boolean maxRowsChanged = false;
/*      */ 
/*  127 */   protected List openResults = new ArrayList();
/*      */ 
/*  130 */   protected boolean pedantic = false;
/*      */   protected Throwable pointOfOrigin;
/*  139 */   protected boolean profileSQL = false;
/*      */ 
/*  142 */   protected ResultSet results = null;
/*      */ 
/*  145 */   protected int resultSetConcurrency = 0;
/*      */   protected LRUCache resultSetMetadataCache;
/*  151 */   protected int resultSetType = 0;
/*      */   protected int statementId;
/*  157 */   protected int timeout = 0;
/*      */ 
/*  160 */   protected long updateCount = -1L;
/*      */ 
/*  163 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  166 */   protected SQLWarning warningChain = null;
/*      */ 
/*      */   public Statement(Connection c, String catalog)
/*      */     throws SQLException
/*      */   {
/*  180 */     if ((c == null) || (c.isClosed())) {
/*  181 */       throw new SQLException(Messages.getString("Statement.0"), "08003");
/*      */     }
/*      */ 
/*  185 */     this.connection = c;
/*  186 */     this.currentCatalog = catalog;
/*  187 */     this.pedantic = this.connection.getPedantic();
/*      */ 
/*  189 */     if (!this.connection.getDontTrackOpenResources()) {
/*  190 */       this.connection.registerStatement(this);
/*      */     }
/*      */ 
/*  196 */     if (this.connection != null) {
/*  197 */       this.maxFieldSize = this.connection.getMaxAllowedPacket();
/*      */     }
/*      */ 
/*  200 */     if (this.connection.getUseUnicode()) {
/*  201 */       this.charEncoding = this.connection.getEncoding();
/*      */ 
/*  203 */       this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */     }
/*      */ 
/*  207 */     boolean profiling = (this.connection.getProfileSql()) || (this.connection.getUseUsageAdvisor());
/*      */ 
/*  210 */     if ((this.connection.getAutoGenerateTestcaseScript()) || (profiling)) {
/*  211 */       this.statementId = (statementCounter++);
/*      */     }
/*      */ 
/*  214 */     if (profiling) {
/*  215 */       this.pointOfOrigin = new Throwable();
/*  216 */       this.profileSQL = this.connection.getProfileSql();
/*  217 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  218 */       this.eventSink = ProfileEventSink.getInstance(this.connection);
/*      */     }
/*      */ 
/*  221 */     int maxRowsConn = this.connection.getMaxRows();
/*      */ 
/*  223 */     if (maxRowsConn != -1)
/*  224 */       setMaxRows(maxRowsConn);
/*      */   }
/*      */ 
/*      */   public synchronized void addBatch(String sql)
/*      */     throws SQLException
/*      */   {
/*  238 */     if (this.batchedArgs == null) {
/*  239 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/*  242 */     if (sql != null)
/*  243 */       this.batchedArgs.add(sql);
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  269 */     if (this.isClosed)
/*  270 */       throw new SQLException(Messages.getString("Statement.49"), "08003");
/*      */   }
/*      */ 
/*      */   protected void checkForDml(String sql, char firstStatementChar)
/*      */     throws SQLException
/*      */   {
/*  289 */     if ((firstStatementChar == 'I') || (firstStatementChar == 'U') || (firstStatementChar == 'D') || (firstStatementChar == 'A') || (firstStatementChar == 'C'))
/*      */     {
/*  292 */       if ((StringUtils.startsWithIgnoreCaseAndWs(sql, "INSERT")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "UPDATE")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "DELETE")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "DROP")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "CREATE")) || (StringUtils.startsWithIgnoreCaseAndWs(sql, "ALTER")))
/*      */       {
/*  298 */         throw new SQLException(Messages.getString("Statement.57"), "S1009");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkNullOrEmptyQuery(String sql)
/*      */     throws SQLException
/*      */   {
/*  314 */     if (sql == null) {
/*  315 */       throw new SQLException(Messages.getString("Statement.59"), "S1009");
/*      */     }
/*      */ 
/*  319 */     if (sql.length() == 0)
/*  320 */       throw new SQLException(Messages.getString("Statement.61"), "S1009");
/*      */   }
/*      */ 
/*      */   public synchronized void clearBatch()
/*      */     throws SQLException
/*      */   {
/*  334 */     if (this.batchedArgs != null)
/*  335 */       this.batchedArgs.clear();
/*      */   }
/*      */ 
/*      */   public synchronized void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  347 */     this.warningChain = null;
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  366 */     realClose(true);
/*      */   }
/*      */ 
/*      */   protected void closeAllOpenResults()
/*      */   {
/*  373 */     if (this.openResults != null) {
/*  374 */       for (Iterator iter = this.openResults.iterator(); iter.hasNext(); ) {
/*  375 */         ResultSet element = (ResultSet)iter.next();
/*      */         try
/*      */         {
/*  378 */           element.realClose(false);
/*      */         } catch (SQLException sqlEx) {
/*  380 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */       }
/*      */ 
/*  384 */       this.openResults.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean createStreamingResultSet()
/*      */   {
/*  396 */     return (this.resultSetType == 1003) && (this.resultSetConcurrency == 1007) && (this.fetchSize == -2147483648);
/*      */   }
/*      */ 
/*      */   public void enableStreamingResults()
/*      */     throws SQLException
/*      */   {
/*  407 */     setFetchSize(-2147483648);
/*  408 */     setResultSetType(1003);
/*      */   }
/*      */ 
/*      */   public synchronized boolean execute(String sql)
/*      */     throws SQLException
/*      */   {
/*  426 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  428 */     checkClosed();
/*      */ 
/*  430 */     char firstNonWsChar = StringUtils.firstNonWsCharUc(sql);
/*      */ 
/*  432 */     boolean isSelect = true;
/*      */ 
/*  434 */     if (firstNonWsChar != 'S') {
/*  435 */       isSelect = false;
/*      */ 
/*  437 */       if (this.connection.isReadOnly()) {
/*  438 */         throw new SQLException(Messages.getString("Statement.27") + Messages.getString("Statement.28"), "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  444 */     if (this.doEscapeProcessing) {
/*  445 */       Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn());
/*      */ 
/*  448 */       if ((escapedSqlResult instanceof String))
/*  449 */         sql = (String)escapedSqlResult;
/*      */       else {
/*  451 */         sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */       }
/*      */     }
/*      */ 
/*  455 */     if ((this.results != null) && 
/*  456 */       (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*  457 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*  461 */     CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/*  463 */     ResultSet rs = null;
/*      */ 
/*  471 */     synchronized (this.connection.getMutex()) {
/*  472 */       String oldCatalog = null;
/*      */ 
/*  474 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/*  475 */         oldCatalog = this.connection.getCatalog();
/*  476 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/*  482 */       if (this.connection.getCacheResultSetMetadata()) {
/*  483 */         cachedMetaData = getCachedMetaData(sql);
/*      */       }
/*      */ 
/*  489 */       if (this.connection.useMaxRows()) {
/*  490 */         int rowLimit = -1;
/*      */ 
/*  492 */         if (isSelect) {
/*  493 */           if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/*  494 */             rowLimit = this.maxRows;
/*      */           }
/*  496 */           else if (this.maxRows <= 0) {
/*  497 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */           else
/*      */           {
/*  503 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows, -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  516 */           this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */         }
/*      */ 
/*  524 */         rs = this.connection.execSQL(this, sql, rowLimit, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), isSelect, this.currentCatalog, cachedMetaData == null);
/*      */       }
/*      */       else
/*      */       {
/*  529 */         rs = this.connection.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), isSelect, this.currentCatalog, cachedMetaData == null);
/*      */       }
/*      */ 
/*  535 */       if (oldCatalog != null) {
/*  536 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/*  540 */     this.lastInsertId = rs.getUpdateID();
/*      */ 
/*  542 */     if (rs != null) {
/*  543 */       this.results = rs;
/*      */ 
/*  545 */       rs.setFirstCharOfQuery(firstNonWsChar);
/*      */ 
/*  547 */       if (rs.reallyResult()) {
/*  548 */         if (cachedMetaData != null) {
/*  549 */           initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */         }
/*  552 */         else if (this.connection.getCacheResultSetMetadata()) {
/*  553 */           initializeResultsMetadataFromCache(sql, null, this.results);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  560 */     return (rs != null) && (rs.reallyResult());
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  568 */     if (returnGeneratedKeys == 1) {
/*  569 */       checkClosed();
/*      */ 
/*  571 */       synchronized (this.connection.getMutex())
/*      */       {
/*  575 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  577 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  580 */           boolean bool1 = execute(sql);
/*      */ 
/*  582 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  587 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/*  595 */     if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
/*  596 */       checkClosed();
/*      */ 
/*  598 */       synchronized (this.connection.getMutex())
/*      */       {
/*  602 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  604 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  607 */           boolean bool1 = execute(sql);
/*      */ 
/*  609 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  614 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public boolean execute(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/*  622 */     if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
/*  623 */       checkClosed();
/*      */ 
/*  625 */       synchronized (this.connection.getMutex())
/*      */       {
/*  629 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  631 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  634 */           boolean bool1 = execute(sql);
/*      */ 
/*  636 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return bool1; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  641 */     return execute(sql);
/*      */   }
/*      */ 
/*      */   public synchronized int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  659 */     if (this.connection.isReadOnly()) {
/*  660 */       throw new SQLException(Messages.getString("Statement.34") + Messages.getString("Statement.35"), "S1009");
/*      */     }
/*      */ 
/*  665 */     if ((this.results != null) && 
/*  666 */       (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*  667 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  672 */       int[] updateCounts = null;
/*      */ 
/*  674 */       if (this.batchedArgs != null) {
/*  675 */         nbrCommands = this.batchedArgs.size();
/*  676 */         updateCounts = new int[nbrCommands];
/*      */ 
/*  678 */         for (int i = 0; i < nbrCommands; i++) {
/*  679 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/*  682 */         SQLException sqlEx = null;
/*      */ 
/*  684 */         int commandIndex = 0;
/*      */ 
/*  686 */         for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*      */           try {
/*  688 */             updateCounts[commandIndex] = executeUpdate((String)this.batchedArgs.get(commandIndex));
/*      */           }
/*      */           catch (SQLException ex) {
/*  691 */             updateCounts[commandIndex] = -3;
/*      */ 
/*  693 */             if (this.connection.getContinueBatchOnError()) {
/*  694 */               sqlEx = ex;
/*      */             } else {
/*  696 */               int[] newUpdateCounts = new int[commandIndex];
/*  697 */               System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*  700 */               throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  707 */         if (sqlEx != null) {
/*  708 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  714 */       int nbrCommands = updateCounts != null ? updateCounts : new int[0];
/*      */       return nbrCommands; } finally { clearBatch(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet executeQuery(String sql)
/*      */     throws SQLException
/*      */   {
/*  733 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  735 */     checkClosed();
/*      */ 
/*  737 */     if (this.doEscapeProcessing) {
/*  738 */       Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn());
/*      */ 
/*  741 */       if ((escapedSqlResult instanceof String))
/*  742 */         sql = (String)escapedSqlResult;
/*      */       else {
/*  744 */         sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */       }
/*      */     }
/*      */ 
/*  748 */     char firstStatementChar = StringUtils.firstNonWsCharUc(sql);
/*      */ 
/*  750 */     checkForDml(sql, firstStatementChar);
/*      */ 
/*  752 */     if ((this.results != null) && 
/*  753 */       (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*  754 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*  758 */     CachedResultSetMetaData cachedMetaData = null;
/*      */ 
/*  766 */     synchronized (this.connection.getMutex()) {
/*  767 */       String oldCatalog = null;
/*      */ 
/*  769 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/*  770 */         oldCatalog = this.connection.getCatalog();
/*  771 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/*  777 */       if (this.connection.getCacheResultSetMetadata()) {
/*  778 */         cachedMetaData = getCachedMetaData(sql);
/*      */       }
/*      */ 
/*  781 */       if (this.connection.useMaxRows())
/*      */       {
/*  786 */         if (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1) {
/*  787 */           this.results = this.connection.execSQL(this, sql, this.maxRows, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), true, this.currentCatalog, cachedMetaData == null);
/*      */         }
/*      */         else
/*      */         {
/*  793 */           if (this.maxRows <= 0) {
/*  794 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */           else
/*      */           {
/*  802 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows, -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */ 
/*  812 */           this.results = this.connection.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), true, this.currentCatalog, cachedMetaData == null);
/*      */ 
/*  817 */           if (oldCatalog != null)
/*  818 */             this.connection.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */       else {
/*  822 */         this.results = this.connection.execSQL(this, sql, -1, null, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet(), true, this.currentCatalog, cachedMetaData == null);
/*      */       }
/*      */ 
/*  828 */       if (oldCatalog != null) {
/*  829 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/*  833 */     this.lastInsertId = this.results.getUpdateID();
/*      */ 
/*  842 */     if (cachedMetaData != null) {
/*  843 */       initializeResultsMetadataFromCache(sql, cachedMetaData, this.results);
/*      */     }
/*  846 */     else if (this.connection.getCacheResultSetMetadata()) {
/*  847 */       initializeResultsMetadataFromCache(sql, null, this.results);
/*      */     }
/*      */ 
/*  852 */     return this.results;
/*      */   }
/*      */ 
/*      */   public synchronized int executeUpdate(String sql)
/*      */     throws SQLException
/*      */   {
/*  871 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  873 */     checkClosed();
/*      */ 
/*  875 */     if (this.connection.isReadOnly()) {
/*  876 */       throw new SQLException(Messages.getString("Statement.42") + Messages.getString("Statement.43"), "S1009");
/*      */     }
/*      */ 
/*  881 */     if (StringUtils.startsWithIgnoreCaseAndWs(sql, "select")) {
/*  882 */       throw new SQLException(Messages.getString("Statement.46"), "01S03");
/*      */     }
/*      */ 
/*  886 */     char firstStatementChar = StringUtils.firstNonWsCharUc(sql);
/*      */ 
/*  888 */     if (this.doEscapeProcessing) {
/*  889 */       Object escapedSqlResult = EscapeProcessor.escapeSQL(sql, this.connection.serverSupportsConvertFn());
/*      */ 
/*  892 */       if ((escapedSqlResult instanceof String))
/*  893 */         sql = (String)escapedSqlResult;
/*      */       else {
/*  895 */         sql = ((EscapeProcessorResult)escapedSqlResult).escapedSql;
/*      */       }
/*      */     }
/*      */ 
/*  899 */     if ((this.results != null) && 
/*  900 */       (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*  901 */       this.results.realClose(false);
/*      */     }
/*      */ 
/*  908 */     ResultSet rs = null;
/*      */ 
/*  910 */     synchronized (this.connection.getMutex()) {
/*  911 */       String oldCatalog = null;
/*      */ 
/*  913 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/*  914 */         oldCatalog = this.connection.getCatalog();
/*  915 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/*  921 */       if (this.connection.useMaxRows()) {
/*  922 */         this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */       }
/*      */ 
/*  929 */       rs = this.connection.execSQL(this, sql, -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */ 
/*  938 */       if (oldCatalog != null) {
/*  939 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/*  943 */     this.results = rs;
/*      */ 
/*  945 */     rs.setFirstCharOfQuery(firstStatementChar);
/*      */ 
/*  947 */     this.updateCount = rs.getUpdateCount();
/*      */ 
/*  949 */     int truncatedUpdateCount = 0;
/*      */ 
/*  951 */     if (this.updateCount > 2147483647L)
/*  952 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/*  954 */       truncatedUpdateCount = (int)this.updateCount;
/*      */     }
/*      */ 
/*  957 */     this.lastInsertId = rs.getUpdateID();
/*      */ 
/*  959 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int returnGeneratedKeys)
/*      */     throws SQLException
/*      */   {
/*  967 */     if (returnGeneratedKeys == 1) {
/*  968 */       checkClosed();
/*      */ 
/*  970 */       synchronized (this.connection.getMutex())
/*      */       {
/*  974 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/*  976 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/*  979 */           int i = executeUpdate(sql);
/*      */ 
/*  981 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  986 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, int[] generatedKeyIndices)
/*      */     throws SQLException
/*      */   {
/*  994 */     if ((generatedKeyIndices != null) && (generatedKeyIndices.length > 0)) {
/*  995 */       checkClosed();
/*      */ 
/*  997 */       synchronized (this.connection.getMutex())
/*      */       {
/* 1001 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1003 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1006 */           int i = executeUpdate(sql);
/*      */ 
/* 1008 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1013 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   public int executeUpdate(String sql, String[] generatedKeyNames)
/*      */     throws SQLException
/*      */   {
/* 1021 */     if ((generatedKeyNames != null) && (generatedKeyNames.length > 0)) {
/* 1022 */       checkClosed();
/*      */ 
/* 1024 */       synchronized (this.connection.getMutex())
/*      */       {
/* 1028 */         boolean readInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*      */ 
/* 1030 */         this.connection.setReadInfoMsgEnabled(true);
/*      */         try
/*      */         {
/* 1033 */           int i = executeUpdate(sql);
/*      */ 
/* 1035 */           this.connection.setReadInfoMsgEnabled(readInfoMsgState); return i; } finally { this.connection.setReadInfoMsgEnabled(readInfoMsgState);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1040 */     return executeUpdate(sql);
/*      */   }
/*      */ 
/*      */   protected CachedResultSetMetaData getCachedMetaData(String sql)
/*      */   {
/* 1066 */     if (this.resultSetMetadataCache != null) {
/* 1067 */       return (CachedResultSetMetaData)this.resultSetMetadataCache.get(sql);
/*      */     }
/*      */ 
/* 1071 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 1083 */     return this.connection;
/*      */   }
/*      */ 
/*      */   public int getFetchDirection()
/*      */     throws SQLException
/*      */   {
/* 1095 */     return 1000;
/*      */   }
/*      */ 
/*      */   public synchronized int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 1107 */     return this.fetchSize;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1120 */     return getGeneratedKeysInternal();
/*      */   }
/*      */ 
/*      */   protected synchronized java.sql.ResultSet getGeneratedKeysInternal()
/*      */     throws SQLException
/*      */   {
/* 1130 */     Field[] fields = new Field[1];
/* 1131 */     fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 1132 */     fields[0].setConnection(this.connection);
/*      */ 
/* 1134 */     ArrayList rowSet = new ArrayList();
/*      */ 
/* 1136 */     long beginAt = getLastInsertID();
/* 1137 */     int numKeys = getUpdateCount();
/*      */ 
/* 1139 */     String serverInfo = this.results.getServerInfo();
/*      */ 
/* 1145 */     if ((numKeys > 0) && (this.results.getFirstCharOfQuery() == 'R') && (serverInfo != null) && (serverInfo.length() > 0))
/*      */     {
/* 1147 */       numKeys = getRecordCountFromInfo(serverInfo);
/*      */     }
/*      */ 
/* 1150 */     if ((beginAt > 0L) && (numKeys > 0)) {
/* 1151 */       for (int i = 0; i < numKeys; i++) {
/* 1152 */         byte[][] row = new byte[1][];
/* 1153 */         row[0] = Long.toString(beginAt++).getBytes();
/* 1154 */         rowSet.add(row);
/*      */       }
/*      */     }
/*      */ 
/* 1158 */     return new ResultSet(this.currentCatalog, fields, new RowDataStatic(rowSet), this.connection, this);
/*      */   }
/*      */ 
/*      */   protected int getId()
/*      */   {
/* 1168 */     return this.statementId;
/*      */   }
/*      */ 
/*      */   public synchronized long getLastInsertID()
/*      */   {
/* 1185 */     return this.lastInsertId;
/*      */   }
/*      */ 
/*      */   public synchronized long getLongUpdateCount()
/*      */   {
/* 1201 */     if (this.results == null) {
/* 1202 */       return -1L;
/*      */     }
/*      */ 
/* 1205 */     if (this.results.reallyResult()) {
/* 1206 */       return -1L;
/*      */     }
/*      */ 
/* 1209 */     return this.updateCount;
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxFieldSize()
/*      */     throws SQLException
/*      */   {
/* 1224 */     return this.maxFieldSize;
/*      */   }
/*      */ 
/*      */   public synchronized int getMaxRows()
/*      */     throws SQLException
/*      */   {
/* 1238 */     if (this.maxRows <= 0) {
/* 1239 */       return 0;
/*      */     }
/*      */ 
/* 1242 */     return this.maxRows;
/*      */   }
/*      */ 
/*      */   public boolean getMoreResults()
/*      */     throws SQLException
/*      */   {
/* 1255 */     return getMoreResults(1);
/*      */   }
/*      */ 
/*      */   public synchronized boolean getMoreResults(int current)
/*      */     throws SQLException
/*      */   {
/* 1263 */     if (this.results == null) {
/* 1264 */       return false;
/*      */     }
/*      */ 
/* 1267 */     ResultSet nextResultSet = this.results.getNextResultSet();
/*      */ 
/* 1269 */     switch (current)
/*      */     {
/*      */     case 1:
/* 1272 */       if (this.results == null) break;
/* 1273 */       this.results.close();
/* 1274 */       this.results.clearNextResult(); break;
/*      */     case 3:
/* 1281 */       if (this.results != null) {
/* 1282 */         this.results.close();
/* 1283 */         this.results.clearNextResult();
/*      */       }
/*      */ 
/* 1286 */       closeAllOpenResults();
/*      */ 
/* 1288 */       break;
/*      */     case 2:
/* 1291 */       if (!this.connection.getDontTrackOpenResources()) {
/* 1292 */         this.openResults.add(this.results);
/*      */       }
/*      */ 
/* 1295 */       this.results.clearNextResult();
/*      */ 
/* 1297 */       break;
/*      */     default:
/* 1300 */       throw new SQLException(Messages.getString("Statement.19"), "S1009");
/*      */     }
/*      */ 
/* 1304 */     this.results = nextResultSet;
/*      */ 
/* 1306 */     if (this.results == null) {
/* 1307 */       this.updateCount = -1L;
/* 1308 */       this.lastInsertId = -1L;
/* 1309 */     } else if (this.results.reallyResult()) {
/* 1310 */       this.updateCount = -1L;
/* 1311 */       this.lastInsertId = -1L;
/*      */     } else {
/* 1313 */       this.updateCount = this.results.getUpdateCount();
/* 1314 */       this.lastInsertId = this.results.getUpdateID();
/*      */     }
/*      */ 
/* 1317 */     return (this.results != null) && (this.results.reallyResult());
/*      */   }
/*      */ 
/*      */   public int getQueryTimeout()
/*      */     throws SQLException
/*      */   {
/* 1332 */     return this.timeout;
/*      */   }
/*      */ 
/*      */   private int getRecordCountFromInfo(String serverInfo)
/*      */   {
/* 1344 */     StringBuffer recordsBuf = new StringBuffer();
/* 1345 */     int recordsCount = 0;
/* 1346 */     int duplicatesCount = 0;
/*      */ 
/* 1348 */     char c = '\000';
/*      */ 
/* 1350 */     int length = serverInfo.length();
/* 1351 */     int i = 0;
/*      */ 
/* 1353 */     for (; i < length; i++) {
/* 1354 */       c = serverInfo.charAt(i);
/*      */ 
/* 1356 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 1361 */     recordsBuf.append(c);
/* 1362 */     i++;
/*      */ 
/* 1364 */     for (; i < length; i++) {
/* 1365 */       c = serverInfo.charAt(i);
/*      */ 
/* 1367 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 1371 */       recordsBuf.append(c);
/*      */     }
/*      */ 
/* 1374 */     recordsCount = Integer.parseInt(recordsBuf.toString());
/*      */ 
/* 1376 */     StringBuffer duplicatesBuf = new StringBuffer();
/*      */ 
/* 1378 */     for (; i < length; i++) {
/* 1379 */       c = serverInfo.charAt(i);
/*      */ 
/* 1381 */       if (Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/*      */     }
/* 1386 */     duplicatesBuf.append(c);
/* 1387 */     i++;
/*      */ 
/* 1389 */     for (; i < length; i++) {
/* 1390 */       c = serverInfo.charAt(i);
/*      */ 
/* 1392 */       if (!Character.isDigit(c))
/*      */       {
/*      */         break;
/*      */       }
/* 1396 */       duplicatesBuf.append(c);
/*      */     }
/*      */ 
/* 1399 */     duplicatesCount = Integer.parseInt(duplicatesBuf.toString());
/*      */ 
/* 1401 */     return recordsCount - duplicatesCount;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet getResultSet()
/*      */     throws SQLException
/*      */   {
/* 1414 */     return (this.results != null) && (this.results.reallyResult()) ? this.results : null;
/*      */   }
/*      */ 
/*      */   public synchronized int getResultSetConcurrency()
/*      */     throws SQLException
/*      */   {
/* 1427 */     return this.resultSetConcurrency;
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 1434 */     return 1;
/*      */   }
/*      */ 
/*      */   public synchronized int getResultSetType()
/*      */     throws SQLException
/*      */   {
/* 1446 */     return this.resultSetType;
/*      */   }
/*      */ 
/*      */   public synchronized int getUpdateCount()
/*      */     throws SQLException
/*      */   {
/* 1460 */     if (this.results == null) {
/* 1461 */       return -1;
/*      */     }
/*      */ 
/* 1464 */     if (this.results.reallyResult()) {
/* 1465 */       return -1;
/*      */     }
/*      */ 
/* 1468 */     int truncatedUpdateCount = 0;
/*      */ 
/* 1470 */     if (this.results.getUpdateCount() > 2147483647L)
/* 1471 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/* 1473 */       truncatedUpdateCount = (int)this.results.getUpdateCount();
/*      */     }
/*      */ 
/* 1476 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   public synchronized SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 1501 */     checkClosed();
/*      */ 
/* 1503 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 1504 */       SQLWarning pendingWarningsFromServer = SQLError.convertShowWarningsToSQLWarnings(this.connection);
/*      */ 
/* 1507 */       if (this.warningChain != null)
/* 1508 */         this.warningChain.setNextWarning(pendingWarningsFromServer);
/*      */       else {
/* 1510 */         this.warningChain = pendingWarningsFromServer;
/*      */       }
/*      */ 
/* 1513 */       return this.warningChain;
/*      */     }
/*      */ 
/* 1516 */     return this.warningChain;
/*      */   }
/*      */ 
/*      */   protected void initializeResultsMetadataFromCache(String sql, CachedResultSetMetaData cachedMetaData, ResultSet resultSet)
/*      */     throws SQLException
/*      */   {
/* 1536 */     synchronized (resultSet) {
/* 1537 */       if (cachedMetaData == null)
/*      */       {
/* 1539 */         cachedMetaData = new CachedResultSetMetaData();
/* 1540 */         cachedMetaData.fields = this.results.fields;
/*      */ 
/* 1544 */         resultSet.buildIndexMapping();
/*      */ 
/* 1546 */         cachedMetaData.columnNameToIndex = resultSet.columnNameToIndex;
/* 1547 */         cachedMetaData.fullColumnNameToIndex = resultSet.fullColumnNameToIndex;
/*      */ 
/* 1549 */         cachedMetaData.metadata = resultSet.getMetaData();
/*      */ 
/* 1551 */         if (this.resultSetMetadataCache == null) {
/* 1552 */           this.resultSetMetadataCache = new LRUCache(this.connection.getMetadataCacheSize());
/*      */         }
/*      */ 
/* 1556 */         this.resultSetMetadataCache.put(sql, cachedMetaData);
/*      */       }
/*      */       else {
/* 1559 */         resultSet.fields = cachedMetaData.fields;
/* 1560 */         resultSet.columnNameToIndex = cachedMetaData.columnNameToIndex;
/* 1561 */         resultSet.fullColumnNameToIndex = cachedMetaData.fullColumnNameToIndex;
/* 1562 */         resultSet.hasBuiltIndexMapping = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1579 */     if (this.isClosed) {
/* 1580 */       return;
/*      */     }
/*      */ 
/* 1583 */     if ((this.useUsageAdvisor) && 
/* 1584 */       (!calledExplicitly)) {
/* 1585 */       String message = Messages.getString("Statement.63") + Messages.getString("Statement.64");
/*      */ 
/* 1588 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connection.getId(), getId(), -1, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/* 1596 */     if ((this.results != null) && 
/* 1597 */       (this.connection != null) && (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*      */       try
/*      */       {
/* 1600 */         this.results.close();
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/*      */       }
/*      */     }
/*      */ 
/* 1607 */     if (this.connection != null) {
/* 1608 */       if (this.maxRowsChanged) {
/* 1609 */         this.connection.unsetMaxRows(this);
/*      */       }
/*      */ 
/* 1612 */       if (!this.connection.getDontTrackOpenResources()) {
/* 1613 */         this.connection.unregisterStatement(this);
/*      */       }
/*      */     }
/*      */ 
/* 1617 */     closeAllOpenResults();
/*      */ 
/* 1619 */     this.results = null;
/* 1620 */     this.connection = null;
/* 1621 */     this.warningChain = null;
/* 1622 */     this.openResults = null;
/* 1623 */     this.isClosed = true;
/*      */   }
/*      */ 
/*      */   public void setCursorName(String name)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setEscapeProcessing(boolean enable)
/*      */     throws SQLException
/*      */   {
/* 1659 */     this.doEscapeProcessing = enable;
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 1676 */     switch (direction) {
/*      */     case 1000:
/*      */     case 1001:
/*      */     case 1002:
/* 1680 */       break;
/*      */     default:
/* 1683 */       throw new SQLException(Messages.getString("Statement.5"), "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 1703 */     if (((rows < 0) && (rows != -2147483648)) || ((this.maxRows != 0) && (this.maxRows != -1) && (rows > getMaxRows())))
/*      */     {
/* 1706 */       throw new SQLException(Messages.getString("Statement.7"), "S1009");
/*      */     }
/*      */ 
/* 1710 */     this.fetchSize = rows;
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxFieldSize(int max)
/*      */     throws SQLException
/*      */   {
/* 1723 */     if (max < 0) {
/* 1724 */       throw new SQLException(Messages.getString("Statement.11"), "S1009");
/*      */     }
/*      */ 
/* 1728 */     int maxBuf = this.connection != null ? this.connection.getMaxAllowedPacket() : MysqlIO.getMaxBuf();
/*      */ 
/* 1731 */     if (max > maxBuf) {
/* 1732 */       throw new SQLException(Messages.getString("Statement.13", new Object[] { new Long(maxBuf) }), "S1009");
/*      */     }
/*      */ 
/* 1737 */     this.maxFieldSize = max;
/*      */   }
/*      */ 
/*      */   public synchronized void setMaxRows(int max)
/*      */     throws SQLException
/*      */   {
/* 1752 */     if ((max > 50000000) || (max < 0)) {
/* 1753 */       throw new SQLException(Messages.getString("Statement.15") + max + " > " + 50000000 + ".", "S1009");
/*      */     }
/*      */ 
/* 1759 */     if (max == 0) {
/* 1760 */       max = -1;
/*      */     }
/*      */ 
/* 1763 */     this.maxRows = max;
/* 1764 */     this.maxRowsChanged = true;
/*      */ 
/* 1766 */     if (this.maxRows == -1) {
/* 1767 */       this.connection.unsetMaxRows(this);
/* 1768 */       this.maxRowsChanged = false;
/*      */     }
/*      */     else
/*      */     {
/* 1775 */       this.connection.maxRowsChanged(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setQueryTimeout(int seconds)
/*      */     throws SQLException
/*      */   {
/* 1789 */     if (seconds < 0) {
/* 1790 */       throw new SQLException(Messages.getString("Statement.21"), "S1009");
/*      */     }
/*      */ 
/* 1794 */     this.timeout = seconds;
/*      */   }
/*      */ 
/*      */   synchronized void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 1804 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   synchronized void setResultSetType(int typeFlag)
/*      */   {
/* 1814 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   class CachedResultSetMetaData
/*      */   {
/*   63 */     Map columnNameToIndex = null;
/*      */     Field[] fields;
/*   69 */     Map fullColumnNameToIndex = null;
/*      */     ResultSetMetaData metadata;
/*      */ 
/*      */     CachedResultSetMetaData()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.Statement
 * JD-Core Version:    0.6.0
 */
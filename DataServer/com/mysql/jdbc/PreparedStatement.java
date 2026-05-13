/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.text.DateFormat;
/*      */ import java.text.ParsePosition;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class PreparedStatement extends Statement
/*      */   implements java.sql.PreparedStatement
/*      */ {
/*  308 */   private static final byte[] HEX_DIGITS = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
/*      */ 
/*  346 */   protected ArrayList batchedGeneratedKeys = null;
/*      */ 
/*  348 */   private DatabaseMetaData dbmd = null;
/*      */ 
/*  354 */   protected char firstCharOfStmt = '\000';
/*      */ 
/*  357 */   protected boolean hasLimitClause = false;
/*      */ 
/*  360 */   protected boolean isLoadDataQuery = false;
/*      */ 
/*  362 */   private boolean[] isNull = null;
/*      */ 
/*  364 */   private boolean[] isStream = null;
/*      */ 
/*  366 */   protected int numberOfExecutions = 0;
/*      */ 
/*  369 */   protected String originalSql = null;
/*      */   protected int parameterCount;
/*      */   protected MysqlParameterMetadata parameterMetaData;
/*  376 */   private InputStream[] parameterStreams = null;
/*      */ 
/*  378 */   private byte[][] parameterValues = (byte[][])null;
/*      */   private ParseInfo parseInfo;
/*      */   private java.sql.ResultSetMetaData pstmtResultMetaData;
/*  384 */   protected boolean retrieveGeneratedKeys = false;
/*      */ 
/*  386 */   private byte[][] staticSqlStrings = (byte[][])null;
/*      */ 
/*  388 */   private byte[] streamConvertBuf = new byte[4096];
/*      */ 
/*  390 */   private int[] streamLengths = null;
/*      */ 
/*  392 */   private SimpleDateFormat tsdf = null;
/*      */ 
/*  397 */   protected boolean useTrueBoolean = false;
/*      */   private boolean usingAnsiMode;
/*      */ 
/*      */   private static int readFully(Reader reader, char[] buf, int length)
/*      */     throws IOException
/*      */   {
/*  331 */     int numCharsRead = 0;
/*      */ 
/*  333 */     while (numCharsRead < length) {
/*  334 */       int count = reader.read(buf, numCharsRead, length - numCharsRead);
/*      */ 
/*  336 */       if (count < 0)
/*      */       {
/*      */         break;
/*      */       }
/*  340 */       numCharsRead += count;
/*      */     }
/*      */ 
/*  343 */     return numCharsRead;
/*      */   }
/*      */ 
/*      */   protected PreparedStatement(Connection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  414 */     super(conn, catalog);
/*      */   }
/*      */ 
/*      */   public PreparedStatement(Connection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  432 */     super(conn, catalog);
/*      */ 
/*  434 */     if (sql == null) {
/*  435 */       throw new SQLException(Messages.getString("PreparedStatement.0"), "S1009");
/*      */     }
/*      */ 
/*  439 */     this.originalSql = sql;
/*      */ 
/*  441 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/*  443 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/*  445 */     this.parseInfo = new ParseInfo(sql, this.connection, this.dbmd, this.charEncoding, this.charConverter);
/*      */ 
/*  448 */     initializeFromParseInfo();
/*      */   }
/*      */ 
/*      */   public PreparedStatement(Connection conn, String sql, String catalog, ParseInfo cachedParseInfo)
/*      */     throws SQLException
/*      */   {
/*  468 */     super(conn, catalog);
/*      */ 
/*  470 */     if (sql == null) {
/*  471 */       throw new SQLException(Messages.getString("PreparedStatement.1"), "S1009");
/*      */     }
/*      */ 
/*  475 */     this.originalSql = sql;
/*      */ 
/*  477 */     this.dbmd = this.connection.getMetaData();
/*      */ 
/*  479 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*      */ 
/*  481 */     this.parseInfo = cachedParseInfo;
/*      */ 
/*  483 */     this.usingAnsiMode = (!this.connection.useAnsiQuotedIdentifiers());
/*      */ 
/*  485 */     initializeFromParseInfo();
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  497 */     if (this.batchedArgs == null) {
/*  498 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/*  501 */     this.batchedArgs.add(new BatchParams(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull));
/*      */   }
/*      */ 
/*      */   protected String asSql()
/*      */     throws SQLException
/*      */   {
/*  507 */     return asSql(false);
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException {
/*  511 */     StringBuffer buf = new StringBuffer();
/*      */     try
/*      */     {
/*  514 */       for (int i = 0; i < this.parameterCount; i++) {
/*  515 */         if (this.charEncoding != null) {
/*  516 */           buf.append(new String(this.staticSqlStrings[i], this.charEncoding));
/*      */         }
/*      */         else {
/*  519 */           buf.append(new String(this.staticSqlStrings[i]));
/*      */         }
/*      */ 
/*  522 */         if ((this.parameterValues[i] == null) && (this.isStream[i] == 0)) {
/*  523 */           if (quoteStreamsAndUnknowns) {
/*  524 */             buf.append("'");
/*      */           }
/*      */ 
/*  527 */           buf.append("** NOT SPECIFIED **");
/*      */ 
/*  529 */           if (quoteStreamsAndUnknowns)
/*  530 */             buf.append("'");
/*      */         }
/*  532 */         else if (this.isStream[i] != 0) {
/*  533 */           if (quoteStreamsAndUnknowns) {
/*  534 */             buf.append("'");
/*      */           }
/*      */ 
/*  537 */           buf.append("** STREAM DATA **");
/*      */ 
/*  539 */           if (quoteStreamsAndUnknowns) {
/*  540 */             buf.append("'");
/*      */           }
/*      */         }
/*  543 */         else if (this.charConverter != null) {
/*  544 */           buf.append(this.charConverter.toString(this.parameterValues[i]));
/*      */         }
/*  547 */         else if (this.charEncoding != null) {
/*  548 */           buf.append(new String(this.parameterValues[i], this.charEncoding));
/*      */         }
/*      */         else {
/*  551 */           buf.append(StringUtils.toAsciiString(this.parameterValues[i]));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  558 */       if (this.charEncoding != null) {
/*  559 */         buf.append(new String(this.staticSqlStrings[this.parameterCount], this.charEncoding));
/*      */       }
/*      */       else
/*      */       {
/*  563 */         buf.append(StringUtils.toAsciiString(this.staticSqlStrings[this.parameterCount]));
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException uue)
/*      */     {
/*  568 */       throw new RuntimeException(Messages.getString("PreparedStatement.32") + this.charEncoding + Messages.getString("PreparedStatement.33"));
/*      */     }
/*      */ 
/*  574 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  588 */     for (int i = 0; i < this.parameterValues.length; i++) {
/*  589 */       this.parameterValues[i] = null;
/*  590 */       this.parameterStreams[i] = null;
/*  591 */       this.isStream[i] = false;
/*  592 */       this.isNull[i] = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  603 */     realClose(true);
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, Buffer packet, int size) throws SQLException
/*      */   {
/*  608 */     int lastwritten = 0;
/*      */ 
/*  610 */     for (int i = 0; i < size; i++) {
/*  611 */       byte b = buf[i];
/*      */ 
/*  613 */       if (b == 0)
/*      */       {
/*  615 */         if (i > lastwritten) {
/*  616 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*  620 */         packet.writeByte(92);
/*  621 */         packet.writeByte(48);
/*  622 */         lastwritten = i + 1;
/*      */       } else {
/*  624 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/*  627 */         if (i > lastwritten) {
/*  628 */           packet.writeBytesNoNull(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*  633 */         packet.writeByte(92);
/*  634 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  640 */     if (lastwritten < size)
/*  641 */       packet.writeBytesNoNull(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   private final void escapeblockFast(byte[] buf, ByteArrayOutputStream bytesOut, int size)
/*      */   {
/*  647 */     int lastwritten = 0;
/*      */ 
/*  649 */     for (int i = 0; i < size; i++) {
/*  650 */       byte b = buf[i];
/*      */ 
/*  652 */       if (b == 0)
/*      */       {
/*  654 */         if (i > lastwritten) {
/*  655 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*  659 */         bytesOut.write(92);
/*  660 */         bytesOut.write(48);
/*  661 */         lastwritten = i + 1;
/*      */       } else {
/*  663 */         if ((b != 92) && (b != 39) && ((this.usingAnsiMode) || (b != 34))) {
/*      */           continue;
/*      */         }
/*  666 */         if (i > lastwritten) {
/*  667 */           bytesOut.write(buf, lastwritten, i - lastwritten);
/*      */         }
/*      */ 
/*  671 */         bytesOut.write(92);
/*  672 */         lastwritten = i;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  678 */     if (lastwritten < size)
/*  679 */       bytesOut.write(buf, lastwritten, size - lastwritten);
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  695 */     if ((this.connection.isReadOnly()) && (this.firstCharOfStmt != 'S')) {
/*  696 */       throw new SQLException(Messages.getString("PreparedStatement.20") + Messages.getString("PreparedStatement.21"), "S1009");
/*      */     }
/*      */ 
/*  701 */     checkClosed();
/*      */ 
/*  703 */     ResultSet rs = null;
/*      */ 
/*  705 */     synchronized (this.connection.getMutex()) {
/*  706 */       clearWarnings();
/*      */ 
/*  708 */       this.batchedGeneratedKeys = null;
/*      */ 
/*  710 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/*  712 */       String oldCatalog = null;
/*      */ 
/*  714 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/*  715 */         oldCatalog = this.connection.getCatalog();
/*  716 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/*  719 */       boolean oldInfoMsgState = false;
/*      */ 
/*  721 */       if (this.retrieveGeneratedKeys) {
/*  722 */         oldInfoMsgState = this.connection.isReadInfoMsgEnabled();
/*  723 */         this.connection.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/*  735 */       if (this.connection.useMaxRows()) {
/*  736 */         int rowLimit = -1;
/*      */ 
/*  738 */         if (this.firstCharOfStmt == 'S') {
/*  739 */           if (this.hasLimitClause) {
/*  740 */             rowLimit = this.maxRows;
/*      */           }
/*  742 */           else if (this.maxRows <= 0) {
/*  743 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */           else
/*      */           {
/*  749 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows, -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  761 */           this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */         }
/*      */ 
/*  769 */         rs = executeInternal(rowLimit, sendPacket, createStreamingResultSet(), this.firstCharOfStmt == 'S', true);
/*      */       }
/*      */       else
/*      */       {
/*  773 */         rs = executeInternal(-1, sendPacket, createStreamingResultSet(), this.firstCharOfStmt == 'S', true);
/*      */       }
/*      */ 
/*  778 */       if (this.retrieveGeneratedKeys) {
/*  779 */         this.connection.setReadInfoMsgEnabled(oldInfoMsgState);
/*  780 */         rs.setFirstCharOfQuery('R');
/*      */       }
/*      */ 
/*  783 */       if (oldCatalog != null) {
/*  784 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */ 
/*  787 */       this.lastInsertId = rs.getUpdateID();
/*      */ 
/*  789 */       if (rs != null) {
/*  790 */         this.results = rs;
/*      */       }
/*      */     }
/*      */ 
/*  794 */     return (rs != null) && (rs.reallyResult());
/*      */   }
/*      */ 
/*      */   public int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  812 */     if (this.connection.isReadOnly()) {
/*  813 */       throw new SQLException(Messages.getString("PreparedStatement.25") + Messages.getString("PreparedStatement.26"), "S1009");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  819 */       clearWarnings();
/*      */ 
/*  821 */       int[] updateCounts = null;
/*      */ 
/*  823 */       if (this.batchedArgs != null) {
/*  824 */         nbrCommands = this.batchedArgs.size();
/*  825 */         updateCounts = new int[nbrCommands];
/*      */ 
/*  827 */         for (int i = 0; i < nbrCommands; i++) {
/*  828 */           updateCounts[i] = -3;
/*      */         }
/*      */ 
/*  831 */         SQLException sqlEx = null;
/*      */ 
/*  833 */         int commandIndex = 0;
/*      */ 
/*  835 */         if (this.retrieveGeneratedKeys) {
/*  836 */           this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */         }
/*      */ 
/*  839 */         for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  840 */           Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/*  842 */           if ((arg instanceof String)) {
/*  843 */             updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */           } else {
/*  845 */             BatchParams paramArg = (BatchParams)arg;
/*      */             try
/*      */             {
/*  848 */               updateCounts[commandIndex] = executeUpdate(paramArg.parameterStrings, paramArg.parameterStreams, paramArg.isStream, paramArg.streamLengths, paramArg.isNull);
/*      */ 
/*  854 */               if (this.retrieveGeneratedKeys) {
/*  855 */                 java.sql.ResultSet rs = null;
/*      */                 try
/*      */                 {
/*  862 */                   rs = super.getGeneratedKeys();
/*      */ 
/*  864 */                   while (rs.next()) {
/*  865 */                     this.batchedGeneratedKeys.add(new byte[][] { rs.getBytes(1) });
/*      */                   }
/*      */                 }
/*      */                 finally
/*      */                 {
/*  870 */                   if (rs != null)
/*  871 */                     rs.close();
/*      */                 }
/*      */               }
/*      */             }
/*      */             catch (SQLException ex) {
/*  876 */               updateCounts[commandIndex] = -3;
/*      */ 
/*  878 */               if (this.connection.getContinueBatchOnError()) {
/*  879 */                 sqlEx = ex;
/*      */               } else {
/*  881 */                 int[] newUpdateCounts = new int[commandIndex];
/*  882 */                 System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*  885 */                 throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  893 */         if (sqlEx != null) {
/*  894 */           throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  900 */       int nbrCommands = updateCounts != null ? updateCounts : new int[0];
/*      */       return nbrCommands; } finally { clearBatch(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   protected ResultSet executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, boolean unpackFields)
/*      */     throws SQLException
/*      */   {
/*  930 */     this.numberOfExecutions += 1;
/*      */ 
/*  933 */     ResultSet rs = this.connection.execSQL(this, null, maxRowsToRetrieve, sendPacket, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, false, this.currentCatalog, unpackFields);
/*      */ 
/*  938 */     return rs;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  951 */     checkClosed();
/*      */ 
/*  953 */     checkForDml(this.originalSql, this.firstCharOfStmt);
/*      */ 
/*  955 */     Statement.CachedResultSetMetaData cachedMetadata = null;
/*      */ 
/*  961 */     synchronized (this.connection.getMutex()) {
/*  962 */       clearWarnings();
/*      */ 
/*  964 */       this.batchedGeneratedKeys = null;
/*      */ 
/*  966 */       Buffer sendPacket = fillSendPacket();
/*      */ 
/*  968 */       if ((this.results != null) && 
/*  969 */         (!this.connection.getHoldResultsOpenOverStatementClose())) {
/*  970 */         this.results.realClose(false);
/*      */       }
/*      */ 
/*  974 */       String oldCatalog = null;
/*      */ 
/*  976 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/*  977 */         oldCatalog = this.connection.getCatalog();
/*  978 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/*  984 */       if (this.connection.getCacheResultSetMetadata()) {
/*  985 */         cachedMetadata = getCachedMetaData(this.originalSql);
/*      */       }
/*      */ 
/*  988 */       if (this.connection.useMaxRows())
/*      */       {
/*  995 */         if (this.hasLimitClause) {
/*  996 */           this.results = executeInternal(this.maxRows, sendPacket, createStreamingResultSet(), true, cachedMetadata == null);
/*      */         }
/*      */         else
/*      */         {
/* 1000 */           if (this.maxRows <= 0) {
/* 1001 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */           else
/*      */           {
/* 1009 */             this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=" + this.maxRows, -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */           }
/*      */ 
/* 1018 */           this.results = executeInternal(-1, sendPacket, createStreamingResultSet(), true, cachedMetadata == null);
/*      */ 
/* 1022 */           if (oldCatalog != null)
/* 1023 */             this.connection.setCatalog(oldCatalog);
/*      */         }
/*      */       }
/*      */       else {
/* 1027 */         this.results = executeInternal(-1, sendPacket, createStreamingResultSet(), true, cachedMetadata == null);
/*      */       }
/*      */ 
/* 1032 */       if (oldCatalog != null) {
/* 1033 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/* 1037 */     this.lastInsertId = this.results.getUpdateID();
/*      */ 
/* 1039 */     if (cachedMetadata != null) {
/* 1040 */       initializeResultsMetadataFromCache(this.originalSql, cachedMetadata, this.results);
/*      */     }
/* 1043 */     else if (this.connection.getCacheResultSetMetadata()) {
/* 1044 */       initializeResultsMetadataFromCache(this.originalSql, null, this.results);
/*      */     }
/*      */ 
/* 1049 */     return this.results;
/*      */   }
/*      */ 
/*      */   public synchronized int executeUpdate()
/*      */     throws SQLException
/*      */   {
/* 1064 */     return executeUpdate(true);
/*      */   }
/*      */ 
/*      */   protected synchronized int executeUpdate(boolean clearBatchedGeneratedKeysAndWarnings)
/*      */     throws SQLException
/*      */   {
/* 1074 */     if (clearBatchedGeneratedKeysAndWarnings) {
/* 1075 */       clearWarnings();
/* 1076 */       this.batchedGeneratedKeys = null;
/*      */     }
/*      */ 
/* 1079 */     return executeUpdate(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths, this.isNull);
/*      */   }
/*      */ 
/*      */   protected synchronized int executeUpdate(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths, boolean[] batchedIsNull)
/*      */     throws SQLException
/*      */   {
/* 1106 */     if (this.connection.isReadOnly()) {
/* 1107 */       throw new SQLException(Messages.getString("PreparedStatement.34") + Messages.getString("PreparedStatement.35"), "S1009");
/*      */     }
/*      */ 
/* 1112 */     checkClosed();
/*      */ 
/* 1114 */     if ((this.firstCharOfStmt == 'S') && (StringUtils.startsWithIgnoreCaseAndWs(this.originalSql, "SELECT")))
/*      */     {
/* 1117 */       throw new SQLException(Messages.getString("PreparedStatement.37"), "01S03");
/*      */     }
/*      */ 
/* 1121 */     if ((this.results != null) && 
/* 1122 */       (!this.connection.getHoldResultsOpenOverStatementClose())) {
/* 1123 */       this.results.realClose(false);
/*      */     }
/*      */ 
/* 1127 */     ResultSet rs = null;
/*      */ 
/* 1132 */     synchronized (this.connection.getMutex()) {
/* 1133 */       Buffer sendPacket = fillSendPacket(batchedParameterStrings, batchedParameterStreams, batchedIsStream, batchedStreamLengths);
/*      */ 
/* 1137 */       String oldCatalog = null;
/*      */ 
/* 1139 */       if (!this.connection.getCatalog().equals(this.currentCatalog)) {
/* 1140 */         oldCatalog = this.connection.getCatalog();
/* 1141 */         this.connection.setCatalog(this.currentCatalog);
/*      */       }
/*      */ 
/* 1147 */       if (this.connection.useMaxRows()) {
/* 1148 */         this.connection.execSQL(this, "SET OPTION SQL_SELECT_LIMIT=DEFAULT", -1, null, 1003, 1007, false, false, this.currentCatalog, true);
/*      */       }
/*      */ 
/* 1155 */       boolean oldInfoMsgState = false;
/*      */ 
/* 1157 */       if (this.retrieveGeneratedKeys) {
/* 1158 */         oldInfoMsgState = this.connection.isReadInfoMsgEnabled();
/* 1159 */         this.connection.setReadInfoMsgEnabled(true);
/*      */       }
/*      */ 
/* 1162 */       rs = executeInternal(-1, sendPacket, false, false, true);
/*      */ 
/* 1164 */       if (this.retrieveGeneratedKeys) {
/* 1165 */         this.connection.setReadInfoMsgEnabled(oldInfoMsgState);
/* 1166 */         rs.setFirstCharOfQuery(this.firstCharOfStmt);
/*      */       }
/*      */ 
/* 1169 */       if (oldCatalog != null) {
/* 1170 */         this.connection.setCatalog(oldCatalog);
/*      */       }
/*      */     }
/*      */ 
/* 1174 */     this.results = rs;
/*      */ 
/* 1176 */     this.updateCount = rs.getUpdateCount();
/*      */ 
/* 1178 */     int truncatedUpdateCount = 0;
/*      */ 
/* 1180 */     if (this.updateCount > 2147483647L)
/* 1181 */       truncatedUpdateCount = 2147483647;
/*      */     else {
/* 1183 */       truncatedUpdateCount = (int)this.updateCount;
/*      */     }
/*      */ 
/* 1186 */     this.lastInsertId = rs.getUpdateID();
/*      */ 
/* 1188 */     return truncatedUpdateCount;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/* 1201 */     return fillSendPacket(this.parameterValues, this.parameterStreams, this.isStream, this.streamLengths);
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/* 1225 */     Buffer sendPacket = this.connection.getIO().getSharedSendPacket();
/*      */ 
/* 1227 */     sendPacket.clear();
/*      */ 
/* 1229 */     sendPacket.writeByte(3);
/*      */ 
/* 1231 */     boolean useStreamLengths = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 1238 */     int ensurePacketSize = 0;
/*      */ 
/* 1240 */     for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 1241 */       if ((batchedIsStream[i] != 0) && (useStreamLengths)) {
/* 1242 */         ensurePacketSize += batchedStreamLengths[i];
/*      */       }
/*      */     }
/*      */ 
/* 1246 */     if (ensurePacketSize != 0) {
/* 1247 */       sendPacket.ensureCapacity(ensurePacketSize);
/*      */     }
/*      */ 
/* 1250 */     for (int i = 0; i < batchedParameterStrings.length; i++) {
/* 1251 */       if ((batchedParameterStrings[i] == null) && (batchedParameterStreams[i] == null))
/*      */       {
/* 1253 */         throw new SQLException(Messages.getString("PreparedStatement.40") + (i + 1), "07001");
/*      */       }
/*      */ 
/* 1258 */       sendPacket.writeBytesNoNull(this.staticSqlStrings[i]);
/*      */ 
/* 1260 */       if (batchedIsStream[i] != 0) {
/* 1261 */         streamToBytes(sendPacket, batchedParameterStreams[i], true, batchedStreamLengths[i], useStreamLengths);
/*      */       }
/*      */       else {
/* 1264 */         sendPacket.writeBytesNoNull(batchedParameterStrings[i]);
/*      */       }
/*      */     }
/*      */ 
/* 1268 */     sendPacket.writeBytesNoNull(this.staticSqlStrings[batchedParameterStrings.length]);
/*      */ 
/* 1271 */     return sendPacket;
/*      */   }
/*      */ 
/*      */   public byte[] getBytesRepresentation(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1287 */     if (this.isStream[parameterIndex] != 0) {
/* 1288 */       return streamToBytes(this.parameterStreams[parameterIndex], false, this.streamLengths[parameterIndex], this.connection.getUseStreamLengthsInPrepStmts());
/*      */     }
/*      */ 
/* 1293 */     byte[] parameterVal = this.parameterValues[parameterIndex];
/*      */ 
/* 1295 */     if (parameterVal == null) {
/* 1296 */       return null;
/*      */     }
/*      */ 
/* 1299 */     if ((parameterVal[0] == 39) && (parameterVal[(parameterVal.length - 1)] == 39))
/*      */     {
/* 1301 */       byte[] valNoQuotes = new byte[parameterVal.length - 2];
/* 1302 */       System.arraycopy(parameterVal, 1, valNoQuotes, 0, parameterVal.length - 2);
/*      */ 
/* 1305 */       return valNoQuotes;
/*      */     }
/*      */ 
/* 1308 */     return parameterVal;
/*      */   }
/*      */ 
/*      */   private final String getDateTimePattern(String dt, boolean toTime)
/*      */     throws Exception
/*      */   {
/* 1318 */     int dtLength = dt != null ? dt.length() : 0;
/*      */ 
/* 1320 */     if ((dtLength >= 8) && (dtLength <= 10)) {
/* 1321 */       int dashCount = 0;
/* 1322 */       boolean isDateOnly = true;
/*      */ 
/* 1324 */       for (int i = 0; i < dtLength; i++) {
/* 1325 */         char c = dt.charAt(i);
/*      */ 
/* 1327 */         if ((!Character.isDigit(c)) && (c != '-')) {
/* 1328 */           isDateOnly = false;
/*      */ 
/* 1330 */           break;
/*      */         }
/*      */ 
/* 1333 */         if (c == '-') {
/* 1334 */           dashCount++;
/*      */         }
/*      */       }
/*      */ 
/* 1338 */       if ((isDateOnly) && (dashCount == 2)) {
/* 1339 */         return "yyyy-MM-dd";
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1346 */     boolean colonsOnly = true;
/*      */ 
/* 1348 */     for (int i = 0; i < dtLength; i++) {
/* 1349 */       char c = dt.charAt(i);
/*      */ 
/* 1351 */       if ((!Character.isDigit(c)) && (c != ':')) {
/* 1352 */         colonsOnly = false;
/*      */ 
/* 1354 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1358 */     if (colonsOnly) {
/* 1359 */       return "HH:mm:ss";
/*      */     }
/*      */ 
/* 1368 */     StringReader reader = new StringReader(dt + " ");
/* 1369 */     ArrayList vec = new ArrayList();
/* 1370 */     ArrayList vecRemovelist = new ArrayList();
/* 1371 */     Object[] nv = new Object[3];
/*      */ 
/* 1373 */     nv[0] = new Character('y');
/* 1374 */     nv[1] = new StringBuffer();
/* 1375 */     nv[2] = new Integer(0);
/* 1376 */     vec.add(nv);
/*      */ 
/* 1378 */     if (toTime) {
/* 1379 */       nv = new Object[3];
/* 1380 */       nv[0] = new Character('h');
/* 1381 */       nv[1] = new StringBuffer();
/* 1382 */       nv[2] = new Integer(0);
/* 1383 */       vec.add(nv);
/*      */     }
/*      */     int z;
/* 1386 */     while ((z = reader.read()) != -1) {
/* 1387 */       char separator = (char)z;
/* 1388 */       int maxvecs = vec.size();
/*      */ 
/* 1390 */       for (int count = 0; count < maxvecs; count++) {
/* 1391 */         Object[] v = (Object[])vec.get(count);
/* 1392 */         int n = ((Integer)v[2]).intValue();
/* 1393 */         char c = getSuccessor(((Character)v[0]).charValue(), n);
/*      */ 
/* 1395 */         if (!Character.isLetterOrDigit(separator)) {
/* 1396 */           if ((c == ((Character)v[0]).charValue()) && (c != 'S')) {
/* 1397 */             vecRemovelist.add(v);
/*      */           } else {
/* 1399 */             ((StringBuffer)v[1]).append(separator);
/*      */ 
/* 1401 */             if ((c == 'X') || (c == 'Y'))
/* 1402 */               v[2] = new Integer(4);
/*      */           }
/*      */         }
/*      */         else {
/* 1406 */           if (c == 'X') {
/* 1407 */             c = 'y';
/* 1408 */             nv = new Object[3];
/* 1409 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('M');
/*      */ 
/* 1411 */             nv[0] = new Character('M');
/* 1412 */             nv[2] = new Integer(1);
/* 1413 */             vec.add(nv);
/* 1414 */           } else if (c == 'Y') {
/* 1415 */             c = 'M';
/* 1416 */             nv = new Object[3];
/* 1417 */             nv[1] = new StringBuffer(((StringBuffer)v[1]).toString()).append('d');
/*      */ 
/* 1419 */             nv[0] = new Character('d');
/* 1420 */             nv[2] = new Integer(1);
/* 1421 */             vec.add(nv);
/*      */           }
/*      */ 
/* 1424 */           ((StringBuffer)v[1]).append(c);
/*      */ 
/* 1426 */           if (c == ((Character)v[0]).charValue()) {
/* 1427 */             v[2] = new Integer(n + 1);
/*      */           } else {
/* 1429 */             v[0] = new Character(c);
/* 1430 */             v[2] = new Integer(1);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1435 */       int size = vecRemovelist.size();
/*      */ 
/* 1437 */       for (int i = 0; i < size; i++) {
/* 1438 */         Object[] v = (Object[])vecRemovelist.get(i);
/* 1439 */         vec.remove(v);
/*      */       }
/*      */ 
/* 1442 */       vecRemovelist.clear();
/*      */     }
/*      */ 
/* 1445 */     int size = vec.size();
/*      */ 
/* 1447 */     for (int i = 0; i < size; i++) {
/* 1448 */       Object[] v = (Object[])vec.get(i);
/* 1449 */       char c = ((Character)v[0]).charValue();
/* 1450 */       int n = ((Integer)v[2]).intValue();
/*      */ 
/* 1452 */       boolean bk = getSuccessor(c, n) != c;
/* 1453 */       boolean atEnd = ((c == 's') || (c == 'm') || ((c == 'h') && (toTime))) && (bk);
/* 1454 */       boolean finishesAtDate = (bk) && (c == 'd') && (!toTime);
/* 1455 */       boolean containsEnd = ((StringBuffer)v[1]).toString().indexOf('W') != -1;
/*      */ 
/* 1458 */       if (((!atEnd) && (!finishesAtDate)) || (containsEnd)) {
/* 1459 */         vecRemovelist.add(v);
/*      */       }
/*      */     }
/*      */ 
/* 1463 */     size = vecRemovelist.size();
/*      */ 
/* 1465 */     for (int i = 0; i < size; i++) {
/* 1466 */       vec.remove(vecRemovelist.get(i));
/*      */     }
/*      */ 
/* 1469 */     vecRemovelist.clear();
/* 1470 */     Object[] v = (Object[])vec.get(0);
/*      */ 
/* 1472 */     StringBuffer format = (StringBuffer)v[1];
/* 1473 */     format.setLength(format.length() - 1);
/*      */ 
/* 1475 */     return format.toString();
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet getGeneratedKeys()
/*      */     throws SQLException
/*      */   {
/* 1485 */     if (this.batchedGeneratedKeys == null) {
/* 1486 */       return super.getGeneratedKeys();
/*      */     }
/*      */ 
/* 1489 */     Field[] fields = new Field[1];
/* 1490 */     fields[0] = new Field("", "GENERATED_KEY", -5, 17);
/* 1491 */     fields[0].setConnection(this.connection);
/*      */ 
/* 1493 */     return new ResultSet(this.currentCatalog, fields, new RowDataStatic(this.batchedGeneratedKeys), this.connection, this);
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 1510 */     if (!StringUtils.startsWithIgnoreCaseAndNonAlphaNumeric(this.originalSql, "SELECT"))
/*      */     {
/* 1512 */       return null;
/*      */     }
/*      */ 
/* 1515 */     PreparedStatement mdStmt = null;
/* 1516 */     java.sql.ResultSet mdRs = null;
/*      */ 
/* 1518 */     if (this.pstmtResultMetaData == null) {
/*      */       try {
/* 1520 */         mdStmt = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog, this.parseInfo);
/*      */ 
/* 1523 */         mdStmt.setMaxRows(0);
/*      */ 
/* 1525 */         int paramCount = this.parameterValues.length;
/*      */ 
/* 1527 */         for (int i = 1; i <= paramCount; i++) {
/* 1528 */           mdStmt.setString(i, "");
/*      */         }
/*      */ 
/* 1531 */         boolean hadResults = mdStmt.execute();
/*      */ 
/* 1533 */         if (hadResults) {
/* 1534 */           mdRs = mdStmt.getResultSet();
/*      */ 
/* 1536 */           this.pstmtResultMetaData = mdRs.getMetaData();
/*      */         } else {
/* 1538 */           this.pstmtResultMetaData = new ResultSetMetaData(new Field[0]);
/*      */         }
/*      */       }
/*      */       finally {
/* 1542 */         SQLException sqlExRethrow = null;
/*      */ 
/* 1544 */         if (mdRs != null) {
/*      */           try {
/* 1546 */             mdRs.close();
/*      */           } catch (SQLException sqlEx) {
/* 1548 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */ 
/* 1551 */           mdRs = null;
/*      */         }
/*      */ 
/* 1554 */         if (mdStmt != null) {
/*      */           try {
/* 1556 */             mdStmt.close();
/*      */           } catch (SQLException sqlEx) {
/* 1558 */             sqlExRethrow = sqlEx;
/*      */           }
/*      */ 
/* 1561 */           mdStmt = null;
/*      */         }
/*      */ 
/* 1564 */         if (sqlExRethrow != null) {
/* 1565 */           throw sqlExRethrow;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1570 */     return this.pstmtResultMetaData;
/*      */   }
/*      */ 
/*      */   public synchronized ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/* 1578 */     if (this.parameterMetaData == null) {
/* 1579 */       this.parameterMetaData = new MysqlParameterMetadata(null, this.parameterCount);
/*      */     }
/*      */ 
/* 1583 */     return this.parameterMetaData;
/*      */   }
/*      */ 
/*      */   ParseInfo getParseInfo() {
/* 1587 */     return this.parseInfo;
/*      */   }
/*      */ 
/*      */   private final char getSuccessor(char c, int n) {
/* 1591 */     return (c == 's') && (n < 2) ? 's' : c == 'm' ? 's' : (c == 'm') && (n < 2) ? 'm' : c == 'H' ? 'm' : (c == 'H') && (n < 2) ? 'H' : c == 'd' ? 'H' : (c == 'd') && (n < 2) ? 'd' : c == 'M' ? 'd' : (c == 'M') && (n < 3) ? 'M' : (c == 'M') && (n == 2) ? 'Y' : c == 'y' ? 'M' : (c == 'y') && (n < 4) ? 'y' : (c == 'y') && (n == 2) ? 'X' : 'W';
/*      */   }
/*      */ 
/*      */   private final void hexEscapeBlock(byte[] buf, Buffer packet, int size)
/*      */     throws SQLException
/*      */   {
/* 1617 */     for (int i = 0; i < size; i++) {
/* 1618 */       byte b = buf[i];
/* 1619 */       int lowBits = (b & 0xFF) / 16;
/* 1620 */       int highBits = (b & 0xFF) % 16;
/*      */ 
/* 1622 */       packet.writeByte(HEX_DIGITS[lowBits]);
/* 1623 */       packet.writeByte(HEX_DIGITS[highBits]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeFromParseInfo() throws SQLException {
/* 1628 */     this.staticSqlStrings = this.parseInfo.staticSql;
/* 1629 */     this.hasLimitClause = this.parseInfo.foundLimitClause;
/* 1630 */     this.isLoadDataQuery = this.parseInfo.foundLoadData;
/* 1631 */     this.firstCharOfStmt = this.parseInfo.firstStmtChar;
/*      */ 
/* 1633 */     this.parameterCount = (this.staticSqlStrings.length - 1);
/*      */ 
/* 1635 */     this.parameterValues = new byte[this.parameterCount][];
/* 1636 */     this.parameterStreams = new InputStream[this.parameterCount];
/* 1637 */     this.isStream = new boolean[this.parameterCount];
/* 1638 */     this.streamLengths = new int[this.parameterCount];
/* 1639 */     this.isNull = new boolean[this.parameterCount];
/*      */ 
/* 1641 */     clearParameters();
/*      */ 
/* 1643 */     for (int j = 0; j < this.parameterCount; j++)
/* 1644 */       this.isStream[j] = false;
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/* 1649 */     return this.isNull[paramIndex];
/*      */   }
/*      */ 
/*      */   private final int readblock(InputStream i, byte[] b) throws SQLException {
/*      */     try {
/* 1654 */       return i.read(b); } catch (Throwable E) {
/*      */     }
/* 1656 */     throw new SQLException(Messages.getString("PreparedStatement.56") + E.getClass().getName(), "S1000");
/*      */   }
/*      */ 
/*      */   private final int readblock(InputStream i, byte[] b, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1664 */       int lengthToRead = length;
/*      */ 
/* 1666 */       if (lengthToRead > b.length) {
/* 1667 */         lengthToRead = b.length;
/*      */       }
/*      */ 
/* 1670 */       return i.read(b, 0, lengthToRead); } catch (Throwable E) {
/*      */     }
/* 1672 */     throw new SQLException(Messages.getString("PreparedStatement.55") + E.getClass().getName(), "S1000");
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1687 */     if ((this.useUsageAdvisor) && 
/* 1688 */       (this.numberOfExecutions <= 1)) {
/* 1689 */       String message = Messages.getString("PreparedStatement.43");
/*      */ 
/* 1691 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.currentCatalog, this.connection.getId(), getId(), -1, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/* 1699 */     super.realClose(calledExplicitly);
/*      */ 
/* 1701 */     this.dbmd = null;
/* 1702 */     this.originalSql = null;
/* 1703 */     this.staticSqlStrings = ((byte[][])null);
/* 1704 */     this.parameterValues = ((byte[][])null);
/* 1705 */     this.parameterStreams = null;
/* 1706 */     this.isStream = null;
/* 1707 */     this.streamLengths = null;
/* 1708 */     this.isNull = null;
/* 1709 */     this.streamConvertBuf = null;
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 1726 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1753 */     if (x == null)
/* 1754 */       setNull(parameterIndex, 12);
/*      */     else
/* 1756 */       setBinaryStream(parameterIndex, x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1774 */     if (x == null)
/* 1775 */       setNull(parameterIndex, 3);
/*      */     else
/* 1777 */       setInternal(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString(x)));
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1804 */     if (x == null) {
/* 1805 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1807 */       if ((parameterIndex < 1) || (parameterIndex > this.staticSqlStrings.length))
/*      */       {
/* 1809 */         throw new SQLException(Messages.getString("PreparedStatement.2") + parameterIndex + Messages.getString("PreparedStatement.3") + this.staticSqlStrings.length + Messages.getString("PreparedStatement.4"), "S1009");
/*      */       }
/*      */ 
/* 1816 */       this.parameterStreams[(parameterIndex - 1)] = x;
/* 1817 */       this.isStream[(parameterIndex - 1)] = true;
/* 1818 */       this.streamLengths[(parameterIndex - 1)] = length;
/* 1819 */       this.isNull[(parameterIndex - 1)] = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int i, Blob x)
/*      */     throws SQLException
/*      */   {
/* 1835 */     if (x == null) {
/* 1836 */       setNull(i, 2004);
/*      */     } else {
/* 1838 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 1840 */       bytesOut.write(39);
/* 1841 */       escapeblockFast(x.getBytes(1L, (int)x.length()), bytesOut, (int)x.length());
/*      */ 
/* 1843 */       bytesOut.write(39);
/*      */ 
/* 1845 */       setInternal(i, bytesOut.toByteArray());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1862 */     if (this.useTrueBoolean)
/* 1863 */       setInternal(parameterIndex, x ? "'1'" : "'0'");
/*      */     else
/* 1865 */       setInternal(parameterIndex, x ? "'t'" : "'f'");
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1882 */     setInternal(parameterIndex, String.valueOf(x));
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1899 */     setBytes(parameterIndex, x, true, true);
/*      */   }
/*      */ 
/*      */   protected void setBytes(int parameterIndex, byte[] x, boolean checkForIntroducer, boolean escapeForMBChars)
/*      */     throws SQLException
/*      */   {
/* 1905 */     if (x == null) {
/* 1906 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1908 */       String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 1910 */       if ((escapeForMBChars) && (this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding)))
/*      */       {
/* 1916 */         ByteArrayOutputStream bOut = new ByteArrayOutputStream(x.length * 2 + 3);
/*      */ 
/* 1918 */         bOut.write(120);
/* 1919 */         bOut.write(39);
/*      */ 
/* 1921 */         for (int i = 0; i < x.length; i++) {
/* 1922 */           int lowBits = (x[i] & 0xFF) / 16;
/* 1923 */           int highBits = (x[i] & 0xFF) % 16;
/*      */ 
/* 1925 */           bOut.write(HEX_DIGITS[lowBits]);
/* 1926 */           bOut.write(HEX_DIGITS[highBits]);
/*      */         }
/*      */ 
/* 1929 */         bOut.write(39);
/*      */ 
/* 1931 */         setInternal(parameterIndex, bOut.toByteArray());
/*      */ 
/* 1933 */         return;
/*      */       }
/*      */ 
/* 1937 */       int numBytes = x.length;
/*      */ 
/* 1939 */       int pad = 2;
/*      */ 
/* 1941 */       boolean needsIntroducer = (checkForIntroducer) && (this.connection.versionMeetsMinimum(4, 1, 0));
/*      */ 
/* 1944 */       if (needsIntroducer) {
/* 1945 */         pad += 7;
/*      */       }
/*      */ 
/* 1948 */       ByteArrayOutputStream bOut = new ByteArrayOutputStream(numBytes + pad);
/*      */ 
/* 1951 */       if (needsIntroducer) {
/* 1952 */         bOut.write(95);
/* 1953 */         bOut.write(98);
/* 1954 */         bOut.write(105);
/* 1955 */         bOut.write(110);
/* 1956 */         bOut.write(97);
/* 1957 */         bOut.write(114);
/* 1958 */         bOut.write(121);
/*      */       }
/* 1960 */       bOut.write(39);
/*      */ 
/* 1962 */       for (int i = 0; i < numBytes; i++) {
/* 1963 */         byte b = x[i];
/*      */ 
/* 1965 */         switch (b) {
/*      */         case 0:
/* 1967 */           bOut.write(92);
/* 1968 */           bOut.write(48);
/*      */ 
/* 1970 */           break;
/*      */         case 10:
/* 1973 */           bOut.write(92);
/* 1974 */           bOut.write(110);
/*      */ 
/* 1976 */           break;
/*      */         case 13:
/* 1979 */           bOut.write(92);
/* 1980 */           bOut.write(114);
/*      */ 
/* 1982 */           break;
/*      */         case 92:
/* 1985 */           bOut.write(92);
/* 1986 */           bOut.write(92);
/*      */ 
/* 1988 */           break;
/*      */         case 39:
/* 1991 */           bOut.write(92);
/* 1992 */           bOut.write(39);
/*      */ 
/* 1994 */           break;
/*      */         case 34:
/* 1997 */           bOut.write(92);
/* 1998 */           bOut.write(34);
/*      */ 
/* 2000 */           break;
/*      */         case 26:
/* 2003 */           bOut.write(92);
/* 2004 */           bOut.write(90);
/*      */ 
/* 2006 */           break;
/*      */         default:
/* 2009 */           bOut.write(b);
/*      */         }
/*      */       }
/*      */ 
/* 2013 */       bOut.write(39);
/*      */ 
/* 2015 */       setInternal(parameterIndex, bOut.toByteArray());
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscape(int parameterIndex, byte[] parameterAsBytes)
/*      */     throws SQLException
/*      */   {
/* 2033 */     byte[] parameterWithQuotes = new byte[parameterAsBytes.length + 2];
/* 2034 */     parameterWithQuotes[0] = 39;
/* 2035 */     System.arraycopy(parameterAsBytes, 0, parameterWithQuotes, 1, parameterAsBytes.length);
/*      */ 
/* 2037 */     parameterWithQuotes[(parameterAsBytes.length + 1)] = 39;
/*      */ 
/* 2039 */     setInternal(parameterIndex, parameterWithQuotes);
/*      */   }
/*      */ 
/*      */   protected void setBytesNoEscapeNoQuotes(int parameterIndex, byte[] parameterAsBytes) throws SQLException
/*      */   {
/* 2044 */     setInternal(parameterIndex, parameterAsBytes);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2072 */       if (reader == null) {
/* 2073 */         setNull(parameterIndex, -1);
/*      */       } else {
/* 2075 */         char[] c = null;
/* 2076 */         int len = 0;
/*      */ 
/* 2078 */         boolean useLength = this.connection.getUseStreamLengthsInPrepStmts();
/*      */ 
/* 2081 */         if ((useLength) && (length != -1)) {
/* 2082 */           c = new char[length];
/*      */ 
/* 2084 */           int numCharsRead = readFully(reader, c, length);
/*      */ 
/* 2089 */           setString(parameterIndex, new String(c, 0, numCharsRead));
/*      */         } else {
/* 2091 */           c = new char[4096];
/*      */ 
/* 2093 */           StringBuffer buf = new StringBuffer();
/*      */ 
/* 2095 */           while ((len = reader.read(c)) != -1) {
/* 2096 */             buf.append(c, 0, len);
/*      */           }
/*      */ 
/* 2099 */           setString(parameterIndex, buf.toString());
/*      */         }
/*      */       }
/*      */     } catch (IOException ioEx) {
/* 2103 */       throw new SQLException(ioEx.toString(), "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int i, Clob x)
/*      */     throws SQLException
/*      */   {
/* 2120 */     if (x == null) {
/* 2121 */       setNull(i, 2005);
/*      */ 
/* 2123 */       return;
/*      */     }
/*      */ 
/* 2126 */     setString(i, x.getSubString(1L, (int)x.length()));
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 2143 */     if (x == null) {
/* 2144 */       setNull(parameterIndex, 91);
/*      */     }
/*      */     else
/*      */     {
/* 2148 */       SimpleDateFormat dateFormatter = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
/*      */ 
/* 2150 */       setInternal(parameterIndex, dateFormatter.format(x));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2170 */     setDate(parameterIndex, x);
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 2187 */     if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */     {
/* 2190 */       throw new SQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009");
/*      */     }
/*      */ 
/* 2196 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 2213 */     setInternal(parameterIndex, StringUtils.fixDecimalExponent(String.valueOf(x)));
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 2230 */     setInternal(parameterIndex, String.valueOf(x));
/*      */   }
/*      */ 
/*      */   private final void setInternal(int paramIndex, byte[] val) throws SQLException
/*      */   {
/* 2235 */     if (this.isClosed) {
/* 2236 */       throw new SQLException(Messages.getString("PreparedStatement.48"), "S1009");
/*      */     }
/*      */ 
/* 2240 */     if (paramIndex < 1) {
/* 2241 */       throw new SQLException(Messages.getString("PreparedStatement.49") + paramIndex + Messages.getString("PreparedStatement.50"), "S1009");
/*      */     }
/*      */ 
/* 2245 */     if (paramIndex > this.parameterCount) {
/* 2246 */       throw new SQLException(Messages.getString("PreparedStatement.51") + paramIndex + Messages.getString("PreparedStatement.52") + this.parameterValues.length + Messages.getString("PreparedStatement.53"), "S1009");
/*      */     }
/*      */ 
/* 2253 */     this.isStream[(paramIndex - 1)] = false;
/* 2254 */     this.isNull[(paramIndex - 1)] = false;
/* 2255 */     this.parameterStreams[(paramIndex - 1)] = null;
/* 2256 */     this.parameterValues[(paramIndex - 1)] = val;
/*      */   }
/*      */ 
/*      */   private final void setInternal(int paramIndex, String val) throws SQLException
/*      */   {
/* 2261 */     byte[] parameterAsBytes = null;
/*      */ 
/* 2263 */     if (this.charConverter != null)
/* 2264 */       parameterAsBytes = this.charConverter.toBytes(val);
/*      */     else {
/* 2266 */       parameterAsBytes = StringUtils.getBytes(val, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */     }
/*      */ 
/* 2272 */     setInternal(paramIndex, parameterAsBytes);
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2288 */     setInternal(parameterIndex, String.valueOf(x));
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 2308 */     setInternal(parameterIndex, "null");
/* 2309 */     this.isNull[(parameterIndex - 1)] = true;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String arg)
/*      */     throws SQLException
/*      */   {
/* 2331 */     setNull(parameterIndex, sqlType);
/*      */   }
/*      */ 
/*      */   private void setNumericObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */     Number parameterAsNum;
/*      */     Number parameterAsNum;
/* 2337 */     if ((parameterObj instanceof Boolean)) {
/* 2338 */       parameterAsNum = ((Boolean)parameterObj).booleanValue() ? new Integer(1) : new Integer(0);
/*      */     }
/* 2341 */     else if ((parameterObj instanceof String))
/*      */     {
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/*      */       Number parameterAsNum;
/* 2342 */       switch (targetSqlType) {
/*      */       case -7:
/* 2344 */         boolean parameterAsBoolean = "true".equalsIgnoreCase((String)parameterObj);
/*      */ 
/* 2347 */         parameterAsNum = parameterAsBoolean ? new Integer(1) : new Integer(0);
/*      */ 
/* 2350 */         break;
/*      */       case -6:
/*      */       case 4:
/*      */       case 5:
/* 2355 */         parameterAsNum = Integer.valueOf((String)parameterObj);
/*      */ 
/* 2358 */         break;
/*      */       case -5:
/* 2361 */         parameterAsNum = Long.valueOf((String)parameterObj);
/*      */ 
/* 2364 */         break;
/*      */       case 7:
/* 2367 */         parameterAsNum = Float.valueOf((String)parameterObj);
/*      */ 
/* 2370 */         break;
/*      */       case 6:
/*      */       case 8:
/* 2374 */         parameterAsNum = Double.valueOf((String)parameterObj);
/*      */ 
/* 2377 */         break;
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 0:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       default:
/* 2382 */         parameterAsNum = new BigDecimal((String)parameterObj); break;
/*      */       }
/*      */     }
/*      */     else {
/* 2386 */       parameterAsNum = (Number)parameterObj;
/*      */     }
/*      */ 
/* 2389 */     switch (targetSqlType) {
/*      */     case -7:
/*      */     case -6:
/*      */     case 4:
/*      */     case 5:
/* 2394 */       setInt(parameterIndex, parameterAsNum.intValue());
/*      */ 
/* 2396 */       break;
/*      */     case -5:
/* 2399 */       setLong(parameterIndex, parameterAsNum.longValue());
/*      */ 
/* 2401 */       break;
/*      */     case 7:
/* 2404 */       setFloat(parameterIndex, parameterAsNum.floatValue());
/*      */ 
/* 2406 */       break;
/*      */     case 6:
/*      */     case 8:
/* 2410 */       setDouble(parameterIndex, parameterAsNum.doubleValue());
/*      */ 
/* 2412 */       break;
/*      */     case 2:
/*      */     case 3:
/* 2417 */       if ((parameterAsNum instanceof BigDecimal)) {
/* 2418 */         BigDecimal scaledBigDecimal = null;
/*      */         try
/*      */         {
/* 2421 */           scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex) {
/*      */           try {
/* 2425 */             scaledBigDecimal = ((BigDecimal)parameterAsNum).setScale(scale, 4);
/*      */           }
/*      */           catch (ArithmeticException arEx)
/*      */           {
/* 2429 */             throw new SQLException("Can't set scale of '" + scale + "' for DECIMAL argument '" + parameterAsNum + "'", "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2438 */         setBigDecimal(parameterIndex, scaledBigDecimal);
/* 2439 */       } else if ((parameterAsNum instanceof BigInteger)) {
/* 2440 */         setBigDecimal(parameterIndex, new BigDecimal((BigInteger)parameterAsNum, scale));
/*      */       }
/*      */       else
/*      */       {
/* 2446 */         setBigDecimal(parameterIndex, new BigDecimal(parameterAsNum.doubleValue()));
/*      */       }
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/* 2468 */     if (parameterObj == null) {
/* 2469 */       setNull(parameterIndex, 1111);
/*      */     }
/* 2471 */     else if ((parameterObj instanceof Byte))
/* 2472 */       setInt(parameterIndex, ((Byte)parameterObj).intValue());
/* 2473 */     else if ((parameterObj instanceof String))
/* 2474 */       setString(parameterIndex, (String)parameterObj);
/* 2475 */     else if ((parameterObj instanceof BigDecimal))
/* 2476 */       setBigDecimal(parameterIndex, (BigDecimal)parameterObj);
/* 2477 */     else if ((parameterObj instanceof Short))
/* 2478 */       setShort(parameterIndex, ((Short)parameterObj).shortValue());
/* 2479 */     else if ((parameterObj instanceof Integer))
/* 2480 */       setInt(parameterIndex, ((Integer)parameterObj).intValue());
/* 2481 */     else if ((parameterObj instanceof Long))
/* 2482 */       setLong(parameterIndex, ((Long)parameterObj).longValue());
/* 2483 */     else if ((parameterObj instanceof Float))
/* 2484 */       setFloat(parameterIndex, ((Float)parameterObj).floatValue());
/* 2485 */     else if ((parameterObj instanceof Double))
/* 2486 */       setDouble(parameterIndex, ((Double)parameterObj).doubleValue());
/* 2487 */     else if ((parameterObj instanceof byte[]))
/* 2488 */       setBytes(parameterIndex, (byte[])parameterObj);
/* 2489 */     else if ((parameterObj instanceof java.sql.Date))
/* 2490 */       setDate(parameterIndex, (java.sql.Date)parameterObj);
/* 2491 */     else if ((parameterObj instanceof Time))
/* 2492 */       setTime(parameterIndex, (Time)parameterObj);
/* 2493 */     else if ((parameterObj instanceof Timestamp))
/* 2494 */       setTimestamp(parameterIndex, (Timestamp)parameterObj);
/* 2495 */     else if ((parameterObj instanceof Boolean)) {
/* 2496 */       setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */     }
/* 2498 */     else if ((parameterObj instanceof InputStream))
/* 2499 */       setBinaryStream(parameterIndex, (InputStream)parameterObj, -1);
/* 2500 */     else if ((parameterObj instanceof Blob))
/* 2501 */       setBlob(parameterIndex, (Blob)parameterObj);
/* 2502 */     else if ((parameterObj instanceof Clob))
/* 2503 */       setClob(parameterIndex, (Clob)parameterObj);
/* 2504 */     else if ((parameterObj instanceof java.util.Date)) {
/* 2505 */       setTimestamp(parameterIndex, new Timestamp(((java.util.Date)parameterObj).getTime()));
/*      */     }
/*      */     else
/* 2508 */       setSerializableObject(parameterIndex, parameterObj);
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 2528 */     setObject(parameterIndex, parameterObj, targetSqlType, 0);
/*      */   }
/*      */ 
/*      */   public void setObject(int parameterIndex, Object parameterObj, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 2562 */     if (parameterObj == null)
/* 2563 */       setNull(parameterIndex, 1111);
/*      */     else
/*      */       try {
/* 2566 */         switch (targetSqlType)
/*      */         {
/*      */         case 16:
/* 2586 */           if ((parameterObj instanceof Boolean)) {
/* 2587 */             setBoolean(parameterIndex, ((Boolean)parameterObj).booleanValue());
/*      */           }
/* 2590 */           else if ((parameterObj instanceof String)) {
/* 2591 */             setBoolean(parameterIndex, ("true".equalsIgnoreCase((String)parameterObj)) || (!"0".equalsIgnoreCase((String)parameterObj)));
/*      */           }
/* 2595 */           else if ((parameterObj instanceof Number)) {
/* 2596 */             int intValue = ((Number)parameterObj).intValue();
/*      */ 
/* 2598 */             setBoolean(parameterIndex, intValue != 0);
/*      */           }
/*      */           else
/*      */           {
/* 2602 */             throw new SQLException("No conversion from " + parameterObj.getClass().getName() + " to Types.BOOLEAN possible.", "S1009");
/*      */           }
/*      */ 
/*      */         case -7:
/*      */         case -6:
/*      */         case -5:
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 8:
/* 2618 */           setNumericObject(parameterIndex, parameterObj, targetSqlType, scale);
/*      */ 
/* 2620 */           break;
/*      */         case -1:
/*      */         case 1:
/*      */         case 12:
/* 2625 */           if ((parameterObj instanceof BigDecimal)) {
/* 2626 */             setString(parameterIndex, StringUtils.fixDecimalExponent(StringUtils.consistentToString((BigDecimal)parameterObj)));
/*      */           }
/*      */           else
/*      */           {
/* 2632 */             setString(parameterIndex, parameterObj.toString());
/*      */           }
/*      */ 
/* 2635 */           break;
/*      */         case 2005:
/* 2639 */           if ((parameterObj instanceof Clob))
/* 2640 */             setClob(parameterIndex, (Clob)parameterObj);
/*      */           else {
/* 2642 */             setString(parameterIndex, parameterObj.toString());
/*      */           }
/*      */ 
/* 2645 */           break;
/*      */         case -4:
/*      */         case -3:
/*      */         case -2:
/*      */         case 2004:
/* 2652 */           if ((parameterObj instanceof byte[]))
/* 2653 */             setBytes(parameterIndex, (byte[])parameterObj);
/* 2654 */           else if ((parameterObj instanceof Blob))
/* 2655 */             setBlob(parameterIndex, (Blob)parameterObj);
/*      */           else {
/* 2657 */             setBytes(parameterIndex, StringUtils.getBytes(parameterObj.toString(), this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*      */           }
/*      */ 
/* 2664 */           break;
/*      */         case 91:
/*      */         case 93:
/*      */           java.util.Date parameterAsDate;
/*      */           java.util.Date parameterAsDate;
/* 2671 */           if ((parameterObj instanceof String)) {
/* 2672 */             ParsePosition pp = new ParsePosition(0);
/* 2673 */             DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, false), Locale.US);
/*      */ 
/* 2675 */             parameterAsDate = sdf.parse((String)parameterObj, pp);
/*      */           } else {
/* 2677 */             parameterAsDate = (java.util.Date)parameterObj;
/*      */           }
/*      */ 
/* 2680 */           switch (targetSqlType)
/*      */           {
/*      */           case 91:
/* 2683 */             if ((parameterAsDate instanceof java.sql.Date)) {
/* 2684 */               setDate(parameterIndex, (java.sql.Date)parameterAsDate);
/*      */             }
/*      */             else {
/* 2687 */               setDate(parameterIndex, new java.sql.Date(parameterAsDate.getTime()));
/*      */             }
/*      */ 
/* 2691 */             break;
/*      */           case 93:
/* 2695 */             if ((parameterAsDate instanceof Timestamp)) {
/* 2696 */               setTimestamp(parameterIndex, (Timestamp)parameterAsDate);
/*      */             }
/*      */             else {
/* 2699 */               setTimestamp(parameterIndex, new Timestamp(parameterAsDate.getTime()));
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2707 */           break;
/*      */         case 92:
/* 2711 */           if ((parameterObj instanceof String)) {
/* 2712 */             DateFormat sdf = new SimpleDateFormat(getDateTimePattern((String)parameterObj, true), Locale.US);
/*      */ 
/* 2714 */             setTime(parameterIndex, new Time(sdf.parse((String)parameterObj).getTime()));
/*      */           }
/* 2716 */           else if ((parameterObj instanceof Timestamp)) {
/* 2717 */             Timestamp xT = (Timestamp)parameterObj;
/* 2718 */             setTime(parameterIndex, new Time(xT.getTime()));
/*      */           } else {
/* 2720 */             setTime(parameterIndex, (Time)parameterObj);
/*      */           }
/*      */ 
/* 2723 */           break;
/*      */         case 1111:
/* 2726 */           setSerializableObject(parameterIndex, parameterObj);
/*      */ 
/* 2728 */           break;
/*      */         default:
/* 2731 */           throw new SQLException(Messages.getString("PreparedStatement.16"), "S1000");
/*      */         }
/*      */       }
/*      */       catch (Exception ex)
/*      */       {
/* 2736 */         if ((ex instanceof SQLException)) {
/* 2737 */           throw ((SQLException)ex);
/*      */         }
/*      */ 
/* 2740 */         throw new SQLException(Messages.getString("PreparedStatement.17") + parameterObj.getClass().toString() + Messages.getString("PreparedStatement.18") + ex.getClass().getName() + Messages.getString("PreparedStatement.19") + ex.getMessage(), "S1000");
/*      */       }
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 2765 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 2775 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   void setResultSetType(int typeFlag)
/*      */   {
/* 2785 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   protected void setRetrieveGeneratedKeys(boolean retrieveGeneratedKeys)
/*      */   {
/* 2794 */     this.retrieveGeneratedKeys = retrieveGeneratedKeys;
/*      */   }
/*      */ 
/*      */   private final void setSerializableObject(int parameterIndex, Object parameterObj)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 2812 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/* 2813 */       ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
/* 2814 */       objectOut.writeObject(parameterObj);
/* 2815 */       objectOut.flush();
/* 2816 */       objectOut.close();
/* 2817 */       bytesOut.flush();
/* 2818 */       bytesOut.close();
/*      */ 
/* 2820 */       byte[] buf = bytesOut.toByteArray();
/* 2821 */       ByteArrayInputStream bytesIn = new ByteArrayInputStream(buf);
/* 2822 */       setBinaryStream(parameterIndex, bytesIn, buf.length);
/*      */     } catch (Exception ex) {
/* 2824 */       throw new SQLException(Messages.getString("PreparedStatement.54") + ex.getClass().getName(), "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2843 */     setInternal(parameterIndex, String.valueOf(x));
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2861 */     if (x == null) {
/* 2862 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 2864 */       StringBuffer buf = new StringBuffer((int)(x.length() * 1.1D));
/* 2865 */       buf.append('\'');
/*      */ 
/* 2867 */       int stringLength = x.length();
/*      */ 
/* 2875 */       for (int i = 0; i < stringLength; i++) {
/* 2876 */         char c = x.charAt(i);
/*      */ 
/* 2878 */         switch (c) {
/*      */         case '\000':
/* 2880 */           buf.append('\\');
/* 2881 */           buf.append('0');
/*      */ 
/* 2883 */           break;
/*      */         case '\n':
/* 2886 */           buf.append('\\');
/* 2887 */           buf.append('n');
/*      */ 
/* 2889 */           break;
/*      */         case '\r':
/* 2892 */           buf.append('\\');
/* 2893 */           buf.append('r');
/*      */ 
/* 2895 */           break;
/*      */         case '\\':
/* 2898 */           buf.append('\\');
/* 2899 */           buf.append('\\');
/*      */ 
/* 2901 */           break;
/*      */         case '\'':
/* 2904 */           buf.append('\\');
/* 2905 */           buf.append('\'');
/*      */ 
/* 2907 */           break;
/*      */         case '"':
/* 2910 */           if (this.usingAnsiMode) {
/* 2911 */             buf.append('\\');
/*      */           }
/*      */ 
/* 2914 */           buf.append('"');
/*      */ 
/* 2916 */           break;
/*      */         case '\032':
/* 2919 */           buf.append('\\');
/* 2920 */           buf.append('Z');
/*      */ 
/* 2922 */           break;
/*      */         default:
/* 2925 */           buf.append(c);
/*      */         }
/*      */       }
/*      */ 
/* 2929 */       buf.append('\'');
/*      */ 
/* 2931 */       String parameterAsString = buf.toString();
/*      */ 
/* 2933 */       byte[] parameterAsBytes = null;
/*      */ 
/* 2935 */       if (!this.isLoadDataQuery) {
/* 2936 */         parameterAsBytes = StringUtils.getBytes(parameterAsString, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */       }
/*      */       else
/*      */       {
/* 2942 */         parameterAsBytes = parameterAsString.getBytes();
/*      */       }
/*      */ 
/* 2945 */       setInternal(parameterIndex, parameterAsBytes);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 2965 */     setTimeInternal(parameterIndex, x, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2982 */     setTimeInternal(parameterIndex, x, TimeZone.getDefault(), false);
/*      */   }
/*      */ 
/*      */   private void setTimeInternal(int parameterIndex, Time x, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 3002 */     if (x == null) {
/* 3003 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 3005 */       x = TimeUtil.changeTimezone(this.connection, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */ 
/* 3013 */       setInternal(parameterIndex, "'" + x.toString() + "'");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 3033 */     setTimestampInternal(parameterIndex, x, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 3050 */     setTimestampInternal(parameterIndex, x, TimeZone.getDefault(), false);
/*      */   }
/*      */ 
/*      */   private synchronized void setTimestampInternal(int parameterIndex, Timestamp x, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 3069 */     if (x == null) {
/* 3070 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 3072 */       String timestampString = null;
/* 3073 */       x = TimeUtil.changeTimezone(this.connection, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */ 
/* 3076 */       if (this.tsdf == null) {
/* 3077 */         this.tsdf = new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss''", Locale.US);
/*      */       }
/*      */ 
/* 3080 */       timestampString = this.tsdf.format(x);
/*      */ 
/* 3082 */       setInternal(parameterIndex, timestampString);
/*      */     }
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 3114 */     if (x == null)
/* 3115 */       setNull(parameterIndex, 12);
/*      */     else
/* 3117 */       setBinaryStream(parameterIndex, x, length);
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL arg)
/*      */     throws SQLException
/*      */   {
/* 3125 */     if (arg != null)
/* 3126 */       setString(parameterIndex, arg.toString());
/*      */     else
/* 3128 */       setNull(parameterIndex, 1);
/*      */   }
/*      */ 
/*      */   private final void streamToBytes(Buffer packet, InputStream in, boolean escape, int streamLength, boolean useLength)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 3136 */       String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 3138 */       boolean hexEscape = false;
/*      */ 
/* 3140 */       if ((this.connection.getUseUnicode()) && (connectionEncoding != null) && (CharsetMapping.isMultibyteCharset(connectionEncoding)) && (!this.connection.parserKnowsUnicode()))
/*      */       {
/* 3143 */         hexEscape = true;
/*      */       }
/*      */ 
/* 3146 */       if (streamLength == -1) {
/* 3147 */         useLength = false;
/*      */       }
/*      */ 
/* 3150 */       int bc = -1;
/*      */ 
/* 3152 */       if (useLength)
/* 3153 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       else {
/* 3155 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 3158 */       int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 3160 */       if (hexEscape)
/* 3161 */         packet.writeStringNoNull("x");
/* 3162 */       else if (this.connection.getIO().versionMeetsMinimum(4, 1, 0)) {
/* 3163 */         packet.writeStringNoNull("_binary");
/*      */       }
/*      */ 
/* 3166 */       if (escape) {
/* 3167 */         packet.writeByte(39);
/*      */       }
/*      */ 
/* 3170 */       while (bc > 0) {
/* 3171 */         if (hexEscape)
/* 3172 */           hexEscapeBlock(this.streamConvertBuf, packet, bc);
/* 3173 */         else if (escape)
/* 3174 */           escapeblockFast(this.streamConvertBuf, packet, bc);
/*      */         else {
/* 3176 */           packet.writeBytesNoNull(this.streamConvertBuf, 0, bc);
/*      */         }
/*      */ 
/* 3179 */         if (useLength) {
/* 3180 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 3182 */           if (bc > 0) {
/* 3183 */             lengthLeftToRead -= bc; continue;
/*      */           }
/*      */         }
/* 3186 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 3190 */       if (escape)
/* 3191 */         packet.writeByte(39);
/*      */     }
/*      */     finally {
/*      */       try {
/* 3195 */         in.close();
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/*      */       }
/* 3200 */       in = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private final byte[] streamToBytes(InputStream in, boolean escape, int streamLength, boolean useLength) throws SQLException
/*      */   {
/*      */     try {
/* 3207 */       if (streamLength == -1) {
/* 3208 */         useLength = false;
/*      */       }
/*      */ 
/* 3211 */       ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
/*      */ 
/* 3213 */       int bc = -1;
/*      */ 
/* 3215 */       if (useLength)
/* 3216 */         bc = readblock(in, this.streamConvertBuf, streamLength);
/*      */       else {
/* 3218 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 3221 */       int lengthLeftToRead = streamLength - bc;
/*      */ 
/* 3223 */       if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 3224 */         bytesOut.write(95);
/* 3225 */         bytesOut.write(98);
/* 3226 */         bytesOut.write(105);
/* 3227 */         bytesOut.write(110);
/* 3228 */         bytesOut.write(97);
/* 3229 */         bytesOut.write(114);
/* 3230 */         bytesOut.write(121);
/*      */       }
/*      */ 
/* 3233 */       if (escape) {
/* 3234 */         bytesOut.write(39);
/*      */       }
/*      */ 
/* 3237 */       while (bc > 0) {
/* 3238 */         if (escape)
/* 3239 */           escapeblockFast(this.streamConvertBuf, bytesOut, bc);
/*      */         else {
/* 3241 */           bytesOut.write(this.streamConvertBuf, 0, bc);
/*      */         }
/*      */ 
/* 3244 */         if (useLength) {
/* 3245 */           bc = readblock(in, this.streamConvertBuf, lengthLeftToRead);
/*      */ 
/* 3247 */           if (bc > 0) {
/* 3248 */             lengthLeftToRead -= bc; continue;
/*      */           }
/*      */         }
/* 3251 */         bc = readblock(in, this.streamConvertBuf);
/*      */       }
/*      */ 
/* 3255 */       if (escape) {
/* 3256 */         bytesOut.write(39);
/*      */       }
/*      */ 
/* 3259 */       byte[] arrayOfByte = bytesOut.toByteArray();
/*      */       return arrayOfByte;
/*      */     }
/*      */     finally
/*      */     {
/*      */       try
/*      */       {
/* 3262 */         in.close();
/*      */       }
/*      */       catch (IOException ioEx)
/*      */       {
/*      */       }
/* 3267 */       in = null; } throw localObject;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 3277 */     StringBuffer buf = new StringBuffer();
/* 3278 */     buf.append(super.toString());
/* 3279 */     buf.append(": ");
/*      */     try
/*      */     {
/* 3282 */       buf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 3284 */       buf.append("EXCEPTION: " + sqlEx.toString());
/*      */     }
/*      */ 
/* 3287 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   class ParseInfo
/*      */   {
/*  126 */     char firstStmtChar = '\000';
/*      */ 
/*  128 */     boolean foundLimitClause = false;
/*      */ 
/*  130 */     boolean foundLoadData = false;
/*      */ 
/*  132 */     long lastUsed = 0L;
/*      */ 
/*  134 */     int statementLength = 0;
/*      */ 
/*  136 */     byte[][] staticSql = (byte[][])null;
/*      */ 
/*      */     public ParseInfo(String sql, Connection conn, DatabaseMetaData dbmd, String encoding, SingleByteCharsetConverter converter)
/*      */       throws SQLException
/*      */     {
/*  144 */       if (sql == null) {
/*  145 */         throw new SQLException(Messages.getString("PreparedStatement.61"), "S1009");
/*      */       }
/*      */ 
/*  150 */       this.lastUsed = System.currentTimeMillis();
/*      */ 
/*  152 */       String quotedIdentifierString = dbmd.getIdentifierQuoteString();
/*      */ 
/*  154 */       char quotedIdentifierChar = '\000';
/*      */ 
/*  156 */       if ((quotedIdentifierString != null) && (!quotedIdentifierString.equals(" ")) && (quotedIdentifierString.length() > 0))
/*      */       {
/*  159 */         quotedIdentifierChar = quotedIdentifierString.charAt(0);
/*      */       }
/*      */ 
/*  162 */       this.statementLength = sql.length();
/*      */ 
/*  164 */       ArrayList endpointList = new ArrayList();
/*  165 */       boolean inQuotes = false;
/*  166 */       char quoteChar = '\000';
/*  167 */       boolean inQuotedId = false;
/*  168 */       int lastParmEnd = 0;
/*      */ 
/*  171 */       int pre1 = 0;
/*  172 */       int pre2 = 0;
/*      */ 
/*  174 */       int stopLookingForLimitClause = this.statementLength - 5;
/*      */ 
/*  176 */       this.foundLimitClause = false;
/*      */ 
/*  178 */       for (int i = 0; i < this.statementLength; i++) {
/*  179 */         char c = sql.charAt(i);
/*      */ 
/*  181 */         if ((this.firstStmtChar == 0) && (!Character.isWhitespace(c)))
/*      */         {
/*  184 */           this.firstStmtChar = Character.toUpperCase(c);
/*      */         }
/*      */ 
/*  189 */         if ((!inQuotes) && (quotedIdentifierChar != 0) && (c == quotedIdentifierChar))
/*      */         {
/*  191 */           inQuotedId = !inQuotedId;
/*      */         }
/*      */ 
/*  195 */         if (!inQuotedId) {
/*  196 */           if (inQuotes) {
/*  197 */             if (((c == '\'') || (c == '"')) && (c == quoteChar) && (pre1 == 92) && (pre2 != 92))
/*      */             {
/*  199 */               inQuotes = !inQuotes;
/*  200 */               quoteChar = '\000';
/*  201 */             } else if (((c == '\'') || (c == '"')) && (c == quoteChar) && (pre1 != 92))
/*      */             {
/*  203 */               inQuotes = !inQuotes;
/*  204 */               quoteChar = '\000';
/*      */             }
/*      */           }
/*  207 */           else if (((c == '\'') || (c == '"')) && (pre1 == 92) && (pre2 != 92))
/*      */           {
/*  209 */             inQuotes = true;
/*  210 */             quoteChar = c;
/*  211 */           } else if (((c == '\'') || (c == '"')) && (pre1 != 92))
/*      */           {
/*  213 */             inQuotes = true;
/*  214 */             quoteChar = c;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  219 */         if ((c == '?') && (!inQuotes)) {
/*  220 */           endpointList.add(new int[] { lastParmEnd, i });
/*  221 */           lastParmEnd = i + 1;
/*      */         }
/*      */ 
/*  224 */         if ((!inQuotes) && (i < stopLookingForLimitClause) && (
/*  225 */           (c == 'L') || (c == 'l'))) {
/*  226 */           char posI1 = sql.charAt(i + 1);
/*      */ 
/*  228 */           if ((posI1 == 'I') || (posI1 == 'i')) {
/*  229 */             char posM = sql.charAt(i + 2);
/*      */ 
/*  231 */             if ((posM == 'M') || (posM == 'm')) {
/*  232 */               char posI2 = sql.charAt(i + 3);
/*      */ 
/*  234 */               if ((posI2 == 'I') || (posI2 == 'i')) {
/*  235 */                 char posT = sql.charAt(i + 4);
/*      */ 
/*  237 */                 if ((posT == 'T') || (posT == 't')) {
/*  238 */                   this.foundLimitClause = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  246 */         pre2 = pre1;
/*  247 */         pre1 = c;
/*      */       }
/*      */ 
/*  250 */       if (this.firstStmtChar == 'L') {
/*  251 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/*  252 */           this.foundLoadData = true;
/*      */         else
/*  254 */           this.foundLoadData = false;
/*      */       }
/*      */       else {
/*  257 */         this.foundLoadData = false;
/*      */       }
/*      */ 
/*  260 */       endpointList.add(new int[] { lastParmEnd, this.statementLength });
/*  261 */       this.staticSql = new byte[endpointList.size()][];
/*  262 */       char[] asCharArray = null;
/*      */ 
/*  264 */       for (i = 0; i < this.staticSql.length; i++) {
/*  265 */         int[] ep = (int[])endpointList.get(i);
/*  266 */         int end = ep[1];
/*  267 */         int begin = ep[0];
/*  268 */         int len = end - begin;
/*      */ 
/*  270 */         if (this.foundLoadData) {
/*  271 */           if (asCharArray == null) {
/*  272 */             asCharArray = sql.toCharArray();
/*      */           }
/*      */ 
/*  275 */           String temp = new String(asCharArray, begin, len);
/*  276 */           this.staticSql[i] = temp.getBytes();
/*  277 */         } else if (encoding == null) {
/*  278 */           byte[] buf = new byte[len];
/*      */ 
/*  280 */           for (int j = 0; j < len; j++) {
/*  281 */             buf[j] = (byte)sql.charAt(begin + j);
/*      */           }
/*      */ 
/*  284 */           this.staticSql[i] = buf;
/*      */         }
/*  286 */         else if (converter != null) {
/*  287 */           this.staticSql[i] = StringUtils.getBytes(sql, converter, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), begin, len, PreparedStatement.this.connection.parserKnowsUnicode());
/*      */         }
/*      */         else
/*      */         {
/*  292 */           if (asCharArray == null) {
/*  293 */             asCharArray = sql.toCharArray();
/*      */           }
/*      */ 
/*  296 */           String temp = new String(asCharArray, begin, len);
/*      */ 
/*  298 */           this.staticSql[i] = StringUtils.getBytes(temp, encoding, PreparedStatement.this.connection.getServerCharacterEncoding(), PreparedStatement.this.connection.parserKnowsUnicode());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class EndPoint
/*      */   {
/*      */     int begin;
/*      */     int end;
/*      */ 
/*      */     EndPoint(int b, int e)
/*      */     {
/*  120 */       this.begin = b;
/*  121 */       this.end = e;
/*      */     }
/*      */   }
/*      */ 
/*      */   class BatchParams
/*      */   {
/*   81 */     boolean[] isNull = null;
/*      */ 
/*   83 */     boolean[] isStream = null;
/*      */ 
/*   85 */     InputStream[] parameterStreams = null;
/*      */ 
/*   87 */     byte[][] parameterStrings = (byte[][])null;
/*      */ 
/*   89 */     int[] streamLengths = null;
/*      */ 
/*      */     BatchParams(byte[][] strings, InputStream[] streams, boolean[] isStreamFlags, int[] lengths, boolean[] isNullFlags)
/*      */     {
/*   96 */       this.parameterStrings = new byte[strings.length][];
/*   97 */       this.parameterStreams = new InputStream[streams.length];
/*   98 */       this.isStream = new boolean[isStreamFlags.length];
/*   99 */       this.streamLengths = new int[lengths.length];
/*  100 */       this.isNull = new boolean[isNullFlags.length];
/*  101 */       System.arraycopy(strings, 0, this.parameterStrings, 0, strings.length);
/*      */ 
/*  103 */       System.arraycopy(streams, 0, this.parameterStreams, 0, streams.length);
/*      */ 
/*  105 */       System.arraycopy(isStreamFlags, 0, this.isStream, 0, isStreamFlags.length);
/*      */ 
/*  107 */       System.arraycopy(lengths, 0, this.streamLengths, 0, lengths.length);
/*  108 */       System.arraycopy(isNullFlags, 0, this.isNull, 0, isNullFlags.length);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.PreparedStatement
 * JD-Core Version:    0.6.0
 */
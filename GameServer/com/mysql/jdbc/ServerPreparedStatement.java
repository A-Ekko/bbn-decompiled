/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.log.Log;
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.BatchUpdateException;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class ServerPreparedStatement extends PreparedStatement
/*      */ {
/*      */   protected static final int BLOB_STREAM_READ_BUF_SIZE = 8192;
/*      */   private static final byte MAX_DATE_REP_LENGTH = 5;
/*      */   private static final byte MAX_DATETIME_REP_LENGTH = 12;
/*      */   private static final byte MAX_TIME_REP_LENGTH = 13;
/*  219 */   private Calendar dateTimeBindingCal = null;
/*      */ 
/*  226 */   private boolean detectedLongParameterSwitch = false;
/*      */   private int fieldCount;
/*  235 */   private boolean invalid = false;
/*      */   private SQLException invalidationException;
/*      */   private boolean isSelectQuery;
/*      */   private Buffer outByteBuffer;
/*      */   private BindValue[] parameterBindings;
/*      */   private Field[] parameterFields;
/*      */   private Field[] resultFields;
/*  255 */   private boolean sendTypesToServer = false;
/*      */   private long serverStatementId;
/*  261 */   private int stringTypeCode = 254;
/*      */   private boolean serverNeedsResetBeforeEachExecution;
/*  443 */   protected boolean isCached = false;
/*      */ 
/*      */   private static void storeTime(Buffer intoBuf, Time tm)
/*      */     throws SQLException
/*      */   {
/*  204 */     intoBuf.ensureCapacity(9);
/*  205 */     intoBuf.writeByte(8);
/*  206 */     intoBuf.writeByte(0);
/*  207 */     intoBuf.writeLong(0L);
/*      */ 
/*  209 */     Calendar cal = Calendar.getInstance();
/*  210 */     cal.setTime(tm);
/*  211 */     intoBuf.writeByte((byte)cal.get(11));
/*  212 */     intoBuf.writeByte((byte)cal.get(12));
/*  213 */     intoBuf.writeByte((byte)cal.get(13));
/*      */   }
/*      */ 
/*      */   public ServerPreparedStatement(Connection conn, String sql, String catalog)
/*      */     throws SQLException
/*      */   {
/*  280 */     super(conn, catalog);
/*      */ 
/*  282 */     checkNullOrEmptyQuery(sql);
/*      */ 
/*  284 */     this.isSelectQuery = StringUtils.startsWithIgnoreCaseAndWs(sql, "SELECT");
/*      */ 
/*  287 */     if (this.connection.versionMeetsMinimum(5, 0, 0)) {
/*  288 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(5, 0, 3));
/*      */     }
/*      */     else {
/*  291 */       this.serverNeedsResetBeforeEachExecution = (!this.connection.versionMeetsMinimum(4, 1, 10));
/*      */     }
/*      */ 
/*  295 */     this.useTrueBoolean = this.connection.versionMeetsMinimum(3, 21, 23);
/*  296 */     this.hasLimitClause = (StringUtils.indexOfIgnoreCase(sql, "LIMIT") != -1);
/*  297 */     this.firstCharOfStmt = StringUtils.firstNonWsCharUc(sql);
/*  298 */     this.originalSql = sql;
/*      */ 
/*  300 */     if (this.connection.versionMeetsMinimum(4, 1, 2))
/*  301 */       this.stringTypeCode = 253;
/*      */     else {
/*  303 */       this.stringTypeCode = 254;
/*      */     }
/*      */     try
/*      */     {
/*  307 */       serverPrepare(sql);
/*      */     } catch (SQLException sqlEx) {
/*  309 */       realClose(false);
/*      */ 
/*  311 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  313 */       realClose(false);
/*      */ 
/*  315 */       throw new SQLException(ex.toString(), "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void addBatch()
/*      */     throws SQLException
/*      */   {
/*  329 */     checkClosed();
/*      */ 
/*  331 */     if (this.batchedArgs == null) {
/*  332 */       this.batchedArgs = new ArrayList();
/*      */     }
/*      */ 
/*  335 */     this.batchedArgs.add(new BatchedBindValues(this.parameterBindings));
/*      */   }
/*      */ 
/*      */   protected String asSql(boolean quoteStreamsAndUnknowns) throws SQLException
/*      */   {
/*  340 */     PreparedStatement pStmtForSub = null;
/*      */     try
/*      */     {
/*  343 */       pStmtForSub = new PreparedStatement(this.connection, this.originalSql, this.currentCatalog);
/*      */ 
/*  346 */       int numParameters = pStmtForSub.parameterCount;
/*  347 */       int ourNumParameters = this.parameterCount;
/*      */ 
/*  349 */       for (i = 0; (i < numParameters) && (i < ourNumParameters); i++) {
/*  350 */         if (this.parameterBindings[i] != null) {
/*  351 */           if (this.parameterBindings[i].isNull) {
/*  352 */             pStmtForSub.setNull(i + 1, 0);
/*      */           } else {
/*  354 */             BindValue bindValue = this.parameterBindings[i];
/*      */ 
/*  359 */             switch (bindValue.bufferType)
/*      */             {
/*      */             case 1:
/*  362 */               pStmtForSub.setByte(i + 1, bindValue.byteBinding);
/*  363 */               break;
/*      */             case 2:
/*  365 */               pStmtForSub.setShort(i + 1, bindValue.shortBinding);
/*  366 */               break;
/*      */             case 3:
/*  368 */               pStmtForSub.setInt(i + 1, bindValue.intBinding);
/*  369 */               break;
/*      */             case 8:
/*  371 */               pStmtForSub.setLong(i + 1, bindValue.longBinding);
/*  372 */               break;
/*      */             case 4:
/*  374 */               pStmtForSub.setFloat(i + 1, bindValue.floatBinding);
/*  375 */               break;
/*      */             case 5:
/*  377 */               pStmtForSub.setDouble(i + 1, bindValue.doubleBinding);
/*      */ 
/*  379 */               break;
/*      */             case 6:
/*      */             case 7:
/*      */             default:
/*  381 */               pStmtForSub.setObject(i + 1, this.parameterBindings[i].value);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  389 */       i = pStmtForSub.asSql(quoteStreamsAndUnknowns);
/*      */     }
/*      */     finally
/*      */     {
/*      */       int i;
/*  391 */       if (pStmtForSub != null)
/*      */         try {
/*  393 */           pStmtForSub.close();
/*      */         }
/*      */         catch (SQLException sqlEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  407 */     if (this.invalid) {
/*  408 */       throw this.invalidationException;
/*      */     }
/*      */ 
/*  411 */     super.checkClosed();
/*      */   }
/*      */ 
/*      */   public void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  418 */     clearParametersInternal(true);
/*      */   }
/*      */ 
/*      */   private void clearParametersInternal(boolean clearServerParameters) throws SQLException
/*      */   {
/*  423 */     boolean hadLongData = false;
/*      */ 
/*  425 */     if (this.parameterBindings != null) {
/*  426 */       for (int i = 0; i < this.parameterCount; i++) {
/*  427 */         if ((this.parameterBindings[i] != null) && (this.parameterBindings[i].isLongData))
/*      */         {
/*  429 */           hadLongData = true;
/*      */         }
/*      */ 
/*  432 */         this.parameterBindings[i].reset();
/*      */       }
/*      */     }
/*      */ 
/*  436 */     if ((clearServerParameters) && (hadLongData)) {
/*  437 */       serverResetStatement();
/*      */ 
/*  439 */       this.detectedLongParameterSwitch = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setClosed(boolean flag)
/*      */   {
/*  446 */     this.isClosed = flag;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws SQLException
/*      */   {
/*  452 */     if (this.isCached) {
/*  453 */       this.isClosed = true;
/*  454 */       this.connection.recachePreparedStatement(this);
/*  455 */       return;
/*      */     }
/*      */ 
/*  458 */     realClose(true);
/*      */   }
/*      */ 
/*      */   private void dumpCloseForTestcase() {
/*  462 */     StringBuffer buf = new StringBuffer();
/*  463 */     this.connection.generateConnectionCommentBlock(buf);
/*  464 */     buf.append("DEALLOCATE PREPARE debug_stmt_");
/*  465 */     buf.append(this.statementId);
/*  466 */     buf.append(";\n");
/*      */ 
/*  468 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   private void dumpExecuteForTestcase() throws SQLException {
/*  472 */     StringBuffer buf = new StringBuffer();
/*      */ 
/*  474 */     for (int i = 0; i < this.parameterCount; i++) {
/*  475 */       this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  477 */       buf.append("SET @debug_stmt_param");
/*  478 */       buf.append(this.statementId);
/*  479 */       buf.append("_");
/*  480 */       buf.append(i);
/*  481 */       buf.append("=");
/*      */ 
/*  483 */       if (this.parameterBindings[i].isNull)
/*  484 */         buf.append("NULL");
/*      */       else {
/*  486 */         buf.append(this.parameterBindings[i].toString(true));
/*      */       }
/*      */ 
/*  489 */       buf.append(";\n");
/*      */     }
/*      */ 
/*  492 */     this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  494 */     buf.append("EXECUTE debug_stmt_");
/*  495 */     buf.append(this.statementId);
/*      */ 
/*  497 */     if (this.parameterCount > 0) {
/*  498 */       buf.append(" USING ");
/*  499 */       for (int i = 0; i < this.parameterCount; i++) {
/*  500 */         if (i > 0) {
/*  501 */           buf.append(", ");
/*      */         }
/*      */ 
/*  504 */         buf.append("@debug_stmt_param");
/*  505 */         buf.append(this.statementId);
/*  506 */         buf.append("_");
/*  507 */         buf.append(i);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  512 */     buf.append(";\n");
/*      */ 
/*  514 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   private void dumpPrepareForTestcase() throws SQLException
/*      */   {
/*  519 */     StringBuffer buf = new StringBuffer(this.originalSql.length() + 64);
/*      */ 
/*  521 */     this.connection.generateConnectionCommentBlock(buf);
/*      */ 
/*  523 */     buf.append("PREPARE debug_stmt_");
/*  524 */     buf.append(this.statementId);
/*  525 */     buf.append(" FROM \"");
/*  526 */     buf.append(this.originalSql);
/*  527 */     buf.append("\";\n");
/*      */ 
/*  529 */     this.connection.dumpTestcaseQuery(buf.toString());
/*      */   }
/*      */ 
/*      */   public synchronized int[] executeBatch()
/*      */     throws SQLException
/*      */   {
/*  536 */     if (this.connection.isReadOnly()) {
/*  537 */       throw new SQLException(Messages.getString("ServerPreparedStatement.2") + Messages.getString("ServerPreparedStatement.3"), "S1009");
/*      */     }
/*      */ 
/*  543 */     checkClosed();
/*      */ 
/*  545 */     synchronized (this.connection.getMutex()) {
/*  546 */       clearWarnings();
/*      */ 
/*  550 */       BindValue[] oldBindValues = this.parameterBindings;
/*      */       try
/*      */       {
/*  553 */         int[] updateCounts = null;
/*      */ 
/*  555 */         if (this.batchedArgs != null) {
/*  556 */           nbrCommands = this.batchedArgs.size();
/*  557 */           updateCounts = new int[nbrCommands];
/*      */ 
/*  559 */           if (this.retrieveGeneratedKeys) {
/*  560 */             this.batchedGeneratedKeys = new ArrayList(nbrCommands);
/*      */           }
/*      */ 
/*  563 */           for (int i = 0; i < nbrCommands; i++) {
/*  564 */             updateCounts[i] = -3;
/*      */           }
/*      */ 
/*  567 */           SQLException sqlEx = null;
/*      */ 
/*  569 */           int commandIndex = 0;
/*      */ 
/*  571 */           BindValue[] previousBindValuesForBatch = null;
/*      */ 
/*  573 */           for (commandIndex = 0; commandIndex < nbrCommands; commandIndex++) {
/*  574 */             Object arg = this.batchedArgs.get(commandIndex);
/*      */ 
/*  576 */             if ((arg instanceof String)) {
/*  577 */               updateCounts[commandIndex] = executeUpdate((String)arg);
/*      */             } else {
/*  579 */               this.parameterBindings = ((BatchedBindValues)arg).batchedParameterValues;
/*      */               try
/*      */               {
/*  586 */                 if (previousBindValuesForBatch != null) {
/*  587 */                   for (int j = 0; j < this.parameterBindings.length; j++) {
/*  588 */                     if (this.parameterBindings[j].bufferType != previousBindValuesForBatch[j].bufferType) {
/*  589 */                       this.sendTypesToServer = true;
/*      */ 
/*  591 */                       break;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 try
/*      */                 {
/*  597 */                   updateCounts[commandIndex] = executeUpdate(false);
/*      */                 } finally {
/*  599 */                   previousBindValuesForBatch = this.parameterBindings;
/*      */                 }
/*      */ 
/*  602 */                 if (this.retrieveGeneratedKeys) {
/*  603 */                   java.sql.ResultSet rs = null;
/*      */                   try
/*      */                   {
/*  615 */                     rs = getGeneratedKeysInternal();
/*      */ 
/*  617 */                     while (rs.next()) {
/*  618 */                       this.batchedGeneratedKeys.add(new byte[][] { rs.getBytes(1) });
/*      */                     }
/*      */                   }
/*      */                   finally
/*      */                   {
/*  623 */                     if (rs != null)
/*  624 */                       rs.close();
/*      */                   }
/*      */                 }
/*      */               }
/*      */               catch (SQLException ex) {
/*  629 */                 updateCounts[commandIndex] = -3;
/*      */ 
/*  631 */                 if (this.connection.getContinueBatchOnError()) {
/*  632 */                   sqlEx = ex;
/*      */                 } else {
/*  634 */                   int[] newUpdateCounts = new int[commandIndex];
/*  635 */                   System.arraycopy(updateCounts, 0, newUpdateCounts, 0, commandIndex);
/*      */ 
/*  638 */                   throw new BatchUpdateException(ex.getMessage(), ex.getSQLState(), ex.getErrorCode(), newUpdateCounts);
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  646 */           if (sqlEx != null) {
/*  647 */             throw new BatchUpdateException(sqlEx.getMessage(), sqlEx.getSQLState(), sqlEx.getErrorCode(), updateCounts);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  653 */         int nbrCommands = updateCounts != null ? updateCounts : new int[0]; jsr 16; return nbrCommands;
/*      */       } finally {
/*  655 */         jsr 6; } localObject4 = returnAddress; this.parameterBindings = oldBindValues;
/*  656 */       this.sendTypesToServer = true;
/*      */ 
/*  658 */       clearBatch(); ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ResultSet executeInternal(int maxRowsToRetrieve, Buffer sendPacket, boolean createStreamingResultSet, boolean queryIsSelectOnly, boolean unpackFields) throws SQLException
/*      */   {
/*  671 */     this.numberOfExecutions += 1;
/*      */     SQLException sqlEx;
/*      */     try {
/*  675 */       return serverExecute(maxRowsToRetrieve, createStreamingResultSet);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  678 */       if (this.connection.getEnablePacketDebug()) {
/*  679 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */ 
/*  682 */       if (this.connection.getDumpQueriesOnException()) {
/*  683 */         String extractedSql = toString();
/*  684 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  686 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/*  688 */         messageBuf.append(extractedSql);
/*      */ 
/*  690 */         sqlEx = Connection.appendMessageToException(sqlEx, messageBuf.toString());
/*      */       }
/*      */ 
/*  694 */       throw sqlEx;
/*      */     } catch (Exception ex) {
/*  696 */       if (this.connection.getEnablePacketDebug()) {
/*  697 */         this.connection.getIO().dumpPacketRingBuffer();
/*      */       }
/*      */ 
/*  700 */       sqlEx = new SQLException(ex.toString(), "S1000");
/*      */ 
/*  703 */       if (this.connection.getDumpQueriesOnException()) {
/*  704 */         String extractedSql = toString();
/*  705 */         StringBuffer messageBuf = new StringBuffer(extractedSql.length() + 32);
/*      */ 
/*  707 */         messageBuf.append("\n\nQuery being executed when exception was thrown:\n\n");
/*      */ 
/*  709 */         messageBuf.append(extractedSql);
/*      */ 
/*  711 */         sqlEx = Connection.appendMessageToException(sqlEx, messageBuf.toString());
/*      */       }
/*      */     }
/*      */ 
/*  715 */     throw sqlEx;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket()
/*      */     throws SQLException
/*      */   {
/*  723 */     return null;
/*      */   }
/*      */ 
/*      */   protected Buffer fillSendPacket(byte[][] batchedParameterStrings, InputStream[] batchedParameterStreams, boolean[] batchedIsStream, int[] batchedStreamLengths)
/*      */     throws SQLException
/*      */   {
/*  733 */     return null;
/*      */   }
/*      */ 
/*      */   private BindValue getBinding(int parameterIndex, boolean forLongData) throws SQLException
/*      */   {
/*  738 */     checkClosed();
/*      */ 
/*  740 */     if (this.parameterBindings.length == 0) {
/*  741 */       throw new SQLException(Messages.getString("ServerPreparedStatement.8"), "S1009");
/*      */     }
/*      */ 
/*  746 */     parameterIndex--;
/*      */ 
/*  748 */     if ((parameterIndex < 0) || (parameterIndex >= this.parameterBindings.length))
/*      */     {
/*  750 */       throw new SQLException(Messages.getString("ServerPreparedStatement.9") + (parameterIndex + 1) + Messages.getString("ServerPreparedStatement.10") + this.parameterBindings.length, "S1009");
/*      */     }
/*      */ 
/*  758 */     if (this.parameterBindings[parameterIndex] == null) {
/*  759 */       this.parameterBindings[parameterIndex] = new BindValue();
/*      */     }
/*  761 */     else if ((this.parameterBindings[parameterIndex].isLongData) && (!forLongData))
/*      */     {
/*  763 */       this.detectedLongParameterSwitch = true;
/*      */     }
/*      */ 
/*  767 */     this.parameterBindings[parameterIndex].isSet = true;
/*  768 */     this.parameterBindings[parameterIndex].boundBeforeExecutionNum = this.numberOfExecutions;
/*      */ 
/*  770 */     return this.parameterBindings[parameterIndex];
/*      */   }
/*      */ 
/*      */   synchronized byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  777 */     BindValue bindValue = getBinding(parameterIndex, false);
/*      */ 
/*  779 */     if (bindValue.isNull)
/*  780 */       return null;
/*  781 */     if (bindValue.isLongData) {
/*  782 */       throw new NotImplemented();
/*      */     }
/*  784 */     if (this.outByteBuffer == null) {
/*  785 */       this.outByteBuffer = Buffer.allocateNew(this.connection.getNetBufferLength(), false);
/*      */     }
/*      */ 
/*  789 */     this.outByteBuffer.clear();
/*      */ 
/*  791 */     int originalPosition = this.outByteBuffer.getPosition();
/*      */ 
/*  793 */     storeBinding(this.outByteBuffer, bindValue, this.connection.getIO());
/*      */ 
/*  795 */     int newPosition = this.outByteBuffer.getPosition();
/*      */ 
/*  797 */     int length = newPosition - originalPosition;
/*      */ 
/*  799 */     byte[] valueAsBytes = new byte[length];
/*      */ 
/*  801 */     System.arraycopy(this.outByteBuffer.getByteBuffer(), originalPosition, valueAsBytes, 0, length);
/*      */ 
/*  804 */     return valueAsBytes;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/*  812 */     checkClosed();
/*      */ 
/*  814 */     if (this.resultFields == null) {
/*  815 */       return null;
/*      */     }
/*      */ 
/*  818 */     return new ResultSetMetaData(this.resultFields);
/*      */   }
/*      */ 
/*      */   public synchronized ParameterMetaData getParameterMetaData()
/*      */     throws SQLException
/*      */   {
/*  825 */     checkClosed();
/*      */ 
/*  827 */     if (this.parameterMetaData == null) {
/*  828 */       this.parameterMetaData = new MysqlParameterMetadata(this.parameterFields, this.parameterCount);
/*      */     }
/*      */ 
/*  832 */     return this.parameterMetaData;
/*      */   }
/*      */ 
/*      */   boolean isNull(int paramIndex)
/*      */   {
/*  839 */     throw new IllegalArgumentException(Messages.getString("ServerPreparedStatement.7"));
/*      */   }
/*      */ 
/*      */   protected synchronized void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/*  853 */     if (this.isClosed) {
/*  854 */       return;
/*      */     }
/*      */ 
/*  858 */     if ((this.connection != null) && (this.connection.getAutoGenerateTestcaseScript()))
/*      */     {
/*  860 */       dumpCloseForTestcase();
/*      */     }
/*      */ 
/*  863 */     synchronized (this.connection.getMutex())
/*      */     {
/*  877 */       SQLException exceptionDuringClose = null;
/*      */ 
/*  880 */       if (calledExplicitly) {
/*      */         try
/*      */         {
/*  883 */           MysqlIO mysql = this.connection.getIO();
/*      */ 
/*  885 */           Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/*  887 */           packet.writeByte(25);
/*  888 */           packet.writeLong(this.serverStatementId);
/*      */ 
/*  890 */           mysql.sendCommand(25, null, packet, true, null);
/*      */         }
/*      */         catch (SQLException sqlEx) {
/*  893 */           exceptionDuringClose = sqlEx;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  899 */       super.realClose(calledExplicitly);
/*      */ 
/*  902 */       clearParametersInternal(false);
/*  903 */       this.parameterBindings = null;
/*      */ 
/*  905 */       this.parameterFields = null;
/*  906 */       this.resultFields = null;
/*      */ 
/*  908 */       if (exceptionDuringClose != null)
/*  909 */         throw exceptionDuringClose;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void rePrepare()
/*      */     throws SQLException
/*      */   {
/*  922 */     this.invalidationException = null;
/*      */     try
/*      */     {
/*  925 */       serverPrepare(this.originalSql);
/*      */     }
/*      */     catch (SQLException sqlEx) {
/*  928 */       this.invalidationException = sqlEx;
/*      */     } catch (Exception ex) {
/*  930 */       this.invalidationException = new SQLException(ex.toString(), "S1000");
/*      */     }
/*      */ 
/*  934 */     if (this.invalidationException != null) {
/*  935 */       this.invalid = true;
/*      */ 
/*  937 */       this.parameterBindings = null;
/*      */ 
/*  939 */       this.parameterFields = null;
/*  940 */       this.resultFields = null;
/*      */ 
/*  942 */       if (this.results != null) {
/*      */         try {
/*  944 */           this.results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/*      */       }
/*  950 */       if (this.connection != null) {
/*  951 */         if (this.maxRowsChanged) {
/*  952 */           this.connection.unsetMaxRows(this);
/*      */         }
/*      */ 
/*  955 */         if (!this.connection.getDontTrackOpenResources())
/*  956 */           this.connection.unregisterStatement(this);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ResultSet serverExecute(int maxRowsToRetrieve, boolean createStreamingResultSet)
/*      */     throws SQLException
/*      */   {
/*  997 */     synchronized (this.connection.getMutex()) {
/*  998 */       if (this.detectedLongParameterSwitch)
/*      */       {
/* 1000 */         boolean firstFound = false;
/* 1001 */         long boundTimeToCheck = 0L;
/*      */ 
/* 1003 */         for (int i = 0; i < this.parameterCount - 1; i++) {
/* 1004 */           if (this.parameterBindings[i].isLongData) {
/* 1005 */             if ((firstFound) && (boundTimeToCheck != this.parameterBindings[i].boundBeforeExecutionNum))
/*      */             {
/* 1007 */               throw new SQLException(Messages.getString("ServerPreparedStatement.11") + Messages.getString("ServerPreparedStatement.12"), "S1C00");
/*      */             }
/*      */ 
/* 1012 */             firstFound = true;
/* 1013 */             boundTimeToCheck = this.parameterBindings[i].boundBeforeExecutionNum;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1021 */         serverResetStatement();
/*      */       }
/*      */ 
/* 1025 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1026 */         if (!this.parameterBindings[i].isSet) {
/* 1027 */           throw new SQLException(Messages.getString("ServerPreparedStatement.13") + (i + 1) + Messages.getString("ServerPreparedStatement.14"), "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1037 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1038 */         if (this.parameterBindings[i].isLongData) {
/* 1039 */           serverLongData(i, this.parameterBindings[i]);
/*      */         }
/*      */       }
/*      */ 
/* 1043 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1044 */         dumpExecuteForTestcase();
/*      */       }
/*      */ 
/* 1050 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1052 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1054 */       packet.clear();
/* 1055 */       packet.writeByte(23);
/* 1056 */       packet.writeLong(this.serverStatementId);
/*      */ 
/* 1058 */       if (this.connection.versionMeetsMinimum(4, 1, 2)) {
/* 1059 */         packet.writeByte(0);
/* 1060 */         packet.writeLong(1L);
/*      */       }
/*      */ 
/* 1064 */       int nullCount = (this.parameterCount + 7) / 8;
/*      */ 
/* 1069 */       int nullBitsPosition = packet.getPosition();
/*      */ 
/* 1071 */       for (int i = 0; i < nullCount; i++) {
/* 1072 */         packet.writeByte(0);
/*      */       }
/*      */ 
/* 1075 */       byte[] nullBitsBuffer = new byte[nullCount];
/*      */ 
/* 1078 */       packet.writeByte(this.sendTypesToServer ? 1 : 0);
/*      */ 
/* 1080 */       if (this.sendTypesToServer)
/*      */       {
/* 1085 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1086 */           packet.writeInt(this.parameterBindings[i].bufferType);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1093 */       for (int i = 0; i < this.parameterCount; i++) {
/* 1094 */         if (!this.parameterBindings[i].isLongData) {
/* 1095 */           if (!this.parameterBindings[i].isNull) {
/* 1096 */             storeBinding(packet, this.parameterBindings[i], mysql);
/*      */           }
/*      */           else
/*      */           {
/*      */             int tmp493_492 = (i / 8);
/*      */             byte[] tmp493_486 = nullBitsBuffer; tmp493_486[tmp493_492] = (byte)(tmp493_486[tmp493_492] | 1 << (i & 0x7));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1107 */       int endPosition = packet.getPosition();
/* 1108 */       packet.setPosition(nullBitsPosition);
/* 1109 */       packet.writeBytesNoNull(nullBitsBuffer);
/* 1110 */       packet.setPosition(endPosition);
/*      */ 
/* 1112 */       long begin = 0L;
/*      */ 
/* 1114 */       if ((this.connection.getProfileSql()) || (this.connection.getLogSlowQueries()) || (this.connection.getGatherPerformanceMetrics()))
/*      */       {
/* 1117 */         begin = System.currentTimeMillis();
/*      */       }
/*      */ 
/* 1120 */       Buffer resultPacket = mysql.sendCommand(23, null, packet, false, null);
/*      */ 
/* 1125 */       this.connection.incrementNumberOfPreparedExecutes();
/*      */ 
/* 1127 */       if (this.connection.getProfileSql()) {
/* 1128 */         this.eventSink = ProfileEventSink.getInstance(this.connection);
/*      */ 
/* 1130 */         this.eventSink.consumeEvent(new ProfilerEvent(4, "", this.currentCatalog, this.connection.getId(), this.statementId, -1, System.currentTimeMillis(), (int)(System.currentTimeMillis() - begin), null, new Throwable(), null));
/*      */       }
/*      */ 
/* 1138 */       ResultSet rs = mysql.readAllResults(this, maxRowsToRetrieve, this.resultSetType, this.resultSetConcurrency, createStreamingResultSet, this.currentCatalog, resultPacket, true, this.fieldCount, true);
/*      */ 
/* 1145 */       if ((!createStreamingResultSet) && (this.serverNeedsResetBeforeEachExecution))
/*      */       {
/* 1147 */         serverResetStatement();
/*      */       }
/*      */ 
/* 1150 */       this.sendTypesToServer = false;
/* 1151 */       this.results = rs;
/*      */ 
/* 1153 */       if ((this.connection.getLogSlowQueries()) || (this.connection.getGatherPerformanceMetrics()))
/*      */       {
/* 1155 */         long elapsedTime = System.currentTimeMillis() - begin;
/*      */ 
/* 1157 */         if ((this.connection.getLogSlowQueries()) && (elapsedTime > this.connection.getSlowQueryThresholdMillis()))
/*      */         {
/* 1160 */           StringBuffer mesgBuf = new StringBuffer(48 + this.originalSql.length());
/*      */ 
/* 1162 */           mesgBuf.append(Messages.getString("ServerPreparedStatement.15"));
/*      */ 
/* 1164 */           mesgBuf.append(this.connection.getSlowQueryThresholdMillis());
/*      */ 
/* 1166 */           mesgBuf.append(Messages.getString("ServerPreparedStatement.16"));
/*      */ 
/* 1168 */           mesgBuf.append(this.originalSql);
/*      */ 
/* 1170 */           this.connection.getLog().logWarn(mesgBuf.toString());
/*      */ 
/* 1172 */           if (this.connection.getExplainSlowQueries()) {
/* 1173 */             String queryAsString = asSql(true);
/*      */ 
/* 1175 */             mysql.explainSlowQuery(queryAsString.getBytes(), queryAsString);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1180 */         if (this.connection.getGatherPerformanceMetrics()) {
/* 1181 */           this.connection.registerQueryExecutionTime(elapsedTime);
/*      */         }
/*      */       }
/*      */ 
/* 1185 */       return rs;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverLongData(int parameterIndex, BindValue longData)
/*      */     throws SQLException
/*      */   {
/* 1218 */     synchronized (this.connection.getMutex()) {
/* 1219 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1221 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1223 */       Object value = longData.value;
/*      */ 
/* 1225 */       if ((value instanceof byte[])) {
/* 1226 */         packet.clear();
/* 1227 */         packet.writeByte(24);
/* 1228 */         packet.writeLong(this.serverStatementId);
/* 1229 */         packet.writeInt(parameterIndex);
/*      */ 
/* 1231 */         packet.writeBytesNoNull((byte[])longData.value);
/*      */ 
/* 1233 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/* 1235 */       else if ((value instanceof InputStream)) {
/* 1236 */         storeStream(mysql, parameterIndex, packet, (InputStream)value);
/* 1237 */       } else if ((value instanceof Blob)) {
/* 1238 */         storeStream(mysql, parameterIndex, packet, ((Blob)value).getBinaryStream());
/*      */       }
/* 1240 */       else if ((value instanceof Reader)) {
/* 1241 */         storeReader(mysql, parameterIndex, packet, (Reader)value);
/*      */       } else {
/* 1243 */         throw new SQLException(Messages.getString("ServerPreparedStatement.18") + value.getClass().getName() + "'", "S1009");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverPrepare(String sql)
/*      */     throws SQLException
/*      */   {
/* 1252 */     synchronized (this.connection.getMutex()) {
/* 1253 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1255 */       if (this.connection.getAutoGenerateTestcaseScript()) {
/* 1256 */         dumpPrepareForTestcase();
/*      */       }
/*      */       try
/*      */       {
/* 1260 */         long begin = 0L;
/*      */ 
/* 1262 */         if (StringUtils.startsWithIgnoreCaseAndWs(sql, "LOAD DATA"))
/* 1263 */           this.isLoadDataQuery = true;
/*      */         else {
/* 1265 */           this.isLoadDataQuery = false;
/*      */         }
/*      */ 
/* 1268 */         if (this.connection.getProfileSql()) {
/* 1269 */           begin = System.currentTimeMillis();
/*      */         }
/*      */ 
/* 1272 */         String characterEncoding = null;
/* 1273 */         String connectionEncoding = this.connection.getEncoding();
/*      */ 
/* 1275 */         if ((!this.isLoadDataQuery) && (this.connection.getUseUnicode()) && (connectionEncoding != null))
/*      */         {
/* 1277 */           characterEncoding = connectionEncoding;
/*      */         }
/*      */ 
/* 1280 */         Buffer prepareResultPacket = mysql.sendCommand(22, sql, null, false, characterEncoding);
/*      */ 
/* 1284 */         if (this.connection.versionMeetsMinimum(4, 1, 1))
/*      */         {
/* 1289 */           prepareResultPacket.setPosition(1);
/*      */         }
/*      */         else
/*      */         {
/* 1293 */           prepareResultPacket.setPosition(0);
/*      */         }
/*      */ 
/* 1296 */         this.serverStatementId = prepareResultPacket.readLong();
/* 1297 */         this.fieldCount = prepareResultPacket.readInt();
/* 1298 */         this.parameterCount = prepareResultPacket.readInt();
/* 1299 */         this.parameterBindings = new BindValue[this.parameterCount];
/*      */ 
/* 1301 */         for (int i = 0; i < this.parameterCount; i++) {
/* 1302 */           this.parameterBindings[i] = new BindValue();
/*      */         }
/*      */ 
/* 1305 */         this.connection.incrementNumberOfPrepares();
/*      */ 
/* 1307 */         if (this.connection.getProfileSql()) {
/* 1308 */           this.eventSink = ProfileEventSink.getInstance(this.connection);
/*      */ 
/* 1311 */           this.eventSink.consumeEvent(new ProfilerEvent(2, "", this.currentCatalog, this.connection.getId(), this.statementId, -1, System.currentTimeMillis(), (int)(System.currentTimeMillis() - begin), null, new Throwable(), sql));
/*      */         }
/*      */ 
/* 1320 */         if ((this.parameterCount > 0) && 
/* 1321 */           (this.connection.versionMeetsMinimum(4, 1, 2)) && (!mysql.isVersion(5, 0, 0)))
/*      */         {
/* 1323 */           this.parameterFields = new Field[this.parameterCount];
/*      */ 
/* 1325 */           Buffer metaDataPacket = mysql.readPacket();
/*      */ 
/* 1327 */           int i = 0;
/*      */ 
/* 1330 */           while ((!metaDataPacket.isLastDataPacket()) && (i < this.parameterCount)) {
/* 1331 */             this.parameterFields[(i++)] = mysql.unpackField(metaDataPacket, false);
/*      */ 
/* 1333 */             metaDataPacket = mysql.readPacket();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1338 */         if (this.fieldCount > 0) {
/* 1339 */           this.resultFields = new Field[this.fieldCount];
/*      */ 
/* 1341 */           Buffer fieldPacket = mysql.readPacket();
/*      */ 
/* 1343 */           int i = 0;
/*      */ 
/* 1347 */           while ((!fieldPacket.isLastDataPacket()) && (i < this.fieldCount)) {
/* 1348 */             this.resultFields[(i++)] = mysql.unpackField(fieldPacket, false);
/*      */ 
/* 1350 */             fieldPacket = mysql.readPacket();
/*      */           }
/*      */         }
/*      */       } catch (SQLException sqlEx) {
/* 1354 */         if (this.connection.getDumpQueriesOnException()) {
/* 1355 */           StringBuffer messageBuf = new StringBuffer(this.originalSql.length() + 32);
/*      */ 
/* 1357 */           messageBuf.append("\n\nQuery being prepared when exception was thrown:\n\n");
/*      */ 
/* 1359 */           messageBuf.append(this.originalSql);
/*      */ 
/* 1361 */           sqlEx = Connection.appendMessageToException(sqlEx, messageBuf.toString());
/*      */         }
/*      */ 
/* 1365 */         throw sqlEx;
/*      */       }
/*      */       finally
/*      */       {
/* 1370 */         this.connection.getIO().clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void serverResetStatement() throws SQLException {
/* 1376 */     synchronized (this.connection.getMutex())
/*      */     {
/* 1378 */       MysqlIO mysql = this.connection.getIO();
/*      */ 
/* 1380 */       Buffer packet = mysql.getSharedSendPacket();
/*      */ 
/* 1382 */       packet.clear();
/* 1383 */       packet.writeByte(26);
/* 1384 */       packet.writeLong(this.serverStatementId);
/*      */       try
/*      */       {
/* 1387 */         mysql.sendCommand(26, null, packet, !this.connection.versionMeetsMinimum(4, 1, 2), null);
/*      */       }
/*      */       catch (SQLException sqlEx) {
/* 1390 */         throw sqlEx;
/*      */       } catch (Exception ex) {
/* 1392 */         throw new SQLException(ex.toString(), "S1000");
/*      */       }
/*      */       finally {
/* 1395 */         mysql.clearInputStream();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setArray(int i, Array x)
/*      */     throws SQLException
/*      */   {
/* 1404 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1413 */     checkClosed();
/*      */ 
/* 1415 */     if (x == null) {
/* 1416 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1418 */       BindValue binding = getBinding(parameterIndex, true);
/* 1419 */       setType(binding, 252);
/*      */ 
/* 1421 */       binding.value = x;
/* 1422 */       binding.isNull = false;
/* 1423 */       binding.isLongData = true;
/*      */ 
/* 1425 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1426 */         binding.bindLength = length;
/*      */       else
/* 1428 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(int parameterIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1438 */     checkClosed();
/*      */ 
/* 1440 */     if (x == null)
/* 1441 */       setNull(parameterIndex, 3);
/*      */     else
/* 1443 */       setString(parameterIndex, StringUtils.fixDecimalExponent(x.toString()));
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1454 */     checkClosed();
/*      */ 
/* 1456 */     if (x == null) {
/* 1457 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1459 */       BindValue binding = getBinding(parameterIndex, true);
/* 1460 */       setType(binding, 252);
/*      */ 
/* 1462 */       binding.value = x;
/* 1463 */       binding.isNull = false;
/* 1464 */       binding.isLongData = true;
/*      */ 
/* 1466 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1467 */         binding.bindLength = length;
/*      */       else
/* 1469 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBlob(int parameterIndex, Blob x)
/*      */     throws SQLException
/*      */   {
/* 1478 */     checkClosed();
/*      */ 
/* 1480 */     if (x == null) {
/* 1481 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1483 */       BindValue binding = getBinding(parameterIndex, true);
/* 1484 */       setType(binding, 252);
/*      */ 
/* 1486 */       binding.value = x;
/* 1487 */       binding.isNull = false;
/* 1488 */       binding.isLongData = true;
/*      */ 
/* 1490 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1491 */         binding.bindLength = x.length();
/*      */       else
/* 1493 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setBoolean(int parameterIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1502 */     setByte(parameterIndex, x ? 1 : 0);
/*      */   }
/*      */ 
/*      */   public void setByte(int parameterIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1509 */     checkClosed();
/*      */ 
/* 1511 */     BindValue binding = getBinding(parameterIndex, false);
/* 1512 */     setType(binding, 1);
/*      */ 
/* 1514 */     binding.value = null;
/* 1515 */     binding.byteBinding = x;
/* 1516 */     binding.isNull = false;
/* 1517 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setBytes(int parameterIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1524 */     checkClosed();
/*      */ 
/* 1526 */     if (x == null) {
/* 1527 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1529 */       BindValue binding = getBinding(parameterIndex, false);
/* 1530 */       setType(binding, 253);
/*      */ 
/* 1532 */       binding.value = x;
/* 1533 */       binding.isNull = false;
/* 1534 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(int parameterIndex, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1544 */     checkClosed();
/*      */ 
/* 1546 */     if (reader == null) {
/* 1547 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1549 */       BindValue binding = getBinding(parameterIndex, true);
/* 1550 */       setType(binding, 252);
/*      */ 
/* 1552 */       binding.value = reader;
/* 1553 */       binding.isNull = false;
/* 1554 */       binding.isLongData = true;
/*      */ 
/* 1556 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1557 */         binding.bindLength = length;
/*      */       else
/* 1559 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setClob(int parameterIndex, Clob x)
/*      */     throws SQLException
/*      */   {
/* 1568 */     checkClosed();
/*      */ 
/* 1570 */     if (x == null) {
/* 1571 */       setNull(parameterIndex, -2);
/*      */     } else {
/* 1573 */       BindValue binding = getBinding(parameterIndex, true);
/* 1574 */       setType(binding, 252);
/*      */ 
/* 1576 */       binding.value = x.getCharacterStream();
/* 1577 */       binding.isNull = false;
/* 1578 */       binding.isLongData = true;
/*      */ 
/* 1580 */       if (this.connection.getUseStreamLengthsInPrepStmts())
/* 1581 */         binding.bindLength = x.length();
/*      */       else
/* 1583 */         binding.bindLength = -1L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x)
/*      */     throws SQLException
/*      */   {
/* 1601 */     setDate(parameterIndex, x, null);
/*      */   }
/*      */ 
/*      */   public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1620 */     if (x == null) {
/* 1621 */       setNull(parameterIndex, 91);
/*      */     } else {
/* 1623 */       BindValue binding = getBinding(parameterIndex, false);
/* 1624 */       setType(binding, 10);
/*      */ 
/* 1626 */       binding.value = x;
/* 1627 */       binding.isNull = false;
/* 1628 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDouble(int parameterIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 1636 */     checkClosed();
/*      */ 
/* 1638 */     if ((!this.connection.getAllowNanAndInf()) && ((x == (1.0D / 0.0D)) || (x == (-1.0D / 0.0D)) || (Double.isNaN(x))))
/*      */     {
/* 1641 */       throw new SQLException("'" + x + "' is not a valid numeric or approximate numeric value", "S1009");
/*      */     }
/*      */ 
/* 1647 */     BindValue binding = getBinding(parameterIndex, false);
/* 1648 */     setType(binding, 5);
/*      */ 
/* 1650 */     binding.value = null;
/* 1651 */     binding.doubleBinding = x;
/* 1652 */     binding.isNull = false;
/* 1653 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setFloat(int parameterIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 1660 */     checkClosed();
/*      */ 
/* 1662 */     BindValue binding = getBinding(parameterIndex, false);
/* 1663 */     setType(binding, 4);
/*      */ 
/* 1665 */     binding.value = null;
/* 1666 */     binding.floatBinding = x;
/* 1667 */     binding.isNull = false;
/* 1668 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setInt(int parameterIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 1675 */     checkClosed();
/*      */ 
/* 1677 */     BindValue binding = getBinding(parameterIndex, false);
/* 1678 */     setType(binding, 3);
/*      */ 
/* 1680 */     binding.value = null;
/* 1681 */     binding.intBinding = x;
/* 1682 */     binding.isNull = false;
/* 1683 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setLong(int parameterIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 1690 */     checkClosed();
/*      */ 
/* 1692 */     BindValue binding = getBinding(parameterIndex, false);
/* 1693 */     setType(binding, 8);
/*      */ 
/* 1695 */     binding.value = null;
/* 1696 */     binding.longBinding = x;
/* 1697 */     binding.isNull = false;
/* 1698 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1705 */     checkClosed();
/*      */ 
/* 1707 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 1713 */     if (binding.bufferType == 0) {
/* 1714 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 1717 */     binding.value = null;
/* 1718 */     binding.isNull = true;
/* 1719 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setNull(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1727 */     checkClosed();
/*      */ 
/* 1729 */     BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 1735 */     if (binding.bufferType == 0) {
/* 1736 */       setType(binding, 6);
/*      */     }
/*      */ 
/* 1739 */     binding.value = null;
/* 1740 */     binding.isNull = true;
/* 1741 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setRef(int i, Ref x)
/*      */     throws SQLException
/*      */   {
/* 1748 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void setShort(int parameterIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 1755 */     checkClosed();
/*      */ 
/* 1757 */     BindValue binding = getBinding(parameterIndex, false);
/* 1758 */     setType(binding, 2);
/*      */ 
/* 1760 */     binding.value = null;
/* 1761 */     binding.shortBinding = x;
/* 1762 */     binding.isNull = false;
/* 1763 */     binding.isLongData = false;
/*      */   }
/*      */ 
/*      */   public void setString(int parameterIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 1770 */     checkClosed();
/*      */ 
/* 1772 */     if (x == null) {
/* 1773 */       setNull(parameterIndex, 1);
/*      */     } else {
/* 1775 */       BindValue binding = getBinding(parameterIndex, false);
/*      */ 
/* 1777 */       setType(binding, this.stringTypeCode);
/*      */ 
/* 1779 */       binding.value = x;
/* 1780 */       binding.isNull = false;
/* 1781 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 1798 */     setTimeInternal(parameterIndex, x, TimeZone.getDefault(), false);
/*      */   }
/*      */ 
/*      */   public void setTime(int parameterIndex, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1818 */     setTimeInternal(parameterIndex, x, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public void setTimeInternal(int parameterIndex, Time x, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 1838 */     if (x == null) {
/* 1839 */       setNull(parameterIndex, 92);
/*      */     } else {
/* 1841 */       BindValue binding = getBinding(parameterIndex, false);
/* 1842 */       setType(binding, 11);
/*      */ 
/* 1844 */       binding.value = TimeUtil.changeTimezone(this.connection, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */ 
/* 1846 */       binding.isNull = false;
/* 1847 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 1865 */     setTimestampInternal(parameterIndex, x, TimeZone.getDefault(), false);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1884 */     setTimestampInternal(parameterIndex, x, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   protected void setTimestampInternal(int parameterIndex, Timestamp x, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 1890 */     if (x == null) {
/* 1891 */       setNull(parameterIndex, 93);
/*      */     } else {
/* 1893 */       BindValue binding = getBinding(parameterIndex, false);
/* 1894 */       setType(binding, 12);
/*      */ 
/* 1896 */       binding.value = TimeUtil.changeTimezone(this.connection, x, tz, this.connection.getServerTimezoneTZ(), rollForward);
/*      */ 
/* 1898 */       binding.isNull = false;
/* 1899 */       binding.isLongData = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setType(BindValue oldValue, int bufferType) {
/* 1904 */     if (oldValue.bufferType != bufferType) {
/* 1905 */       this.sendTypesToServer = true;
/*      */     }
/*      */ 
/* 1908 */     oldValue.bufferType = bufferType;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setUnicodeStream(int parameterIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1932 */     checkClosed();
/*      */ 
/* 1934 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void setURL(int parameterIndex, URL x)
/*      */     throws SQLException
/*      */   {
/* 1941 */     checkClosed();
/*      */ 
/* 1943 */     setString(parameterIndex, x.toString());
/*      */   }
/*      */ 
/*      */   private void storeBinding(Buffer packet, BindValue bindValue, MysqlIO mysql)
/*      */     throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 1960 */       Object value = bindValue.value;
/*      */ 
/* 1965 */       switch (bindValue.bufferType)
/*      */       {
/*      */       case 1:
/* 1968 */         packet.writeByte(bindValue.byteBinding);
/* 1969 */         return;
/*      */       case 2:
/* 1971 */         packet.ensureCapacity(2);
/* 1972 */         packet.writeInt(bindValue.shortBinding);
/* 1973 */         return;
/*      */       case 3:
/* 1975 */         packet.ensureCapacity(4);
/* 1976 */         packet.writeLong(bindValue.intBinding);
/* 1977 */         return;
/*      */       case 8:
/* 1979 */         packet.ensureCapacity(8);
/* 1980 */         packet.writeLongLong(bindValue.longBinding);
/* 1981 */         return;
/*      */       case 4:
/* 1983 */         packet.ensureCapacity(4);
/* 1984 */         packet.writeFloat(bindValue.floatBinding);
/* 1985 */         return;
/*      */       case 5:
/* 1987 */         packet.ensureCapacity(8);
/* 1988 */         packet.writeDouble(bindValue.doubleBinding);
/* 1989 */         return;
/*      */       case 11:
/* 1991 */         storeTime(packet, (Time)value);
/* 1992 */         return;
/*      */       case 7:
/*      */       case 10:
/*      */       case 12:
/* 1996 */         storeDateTime(packet, (java.util.Date)value, mysql);
/* 1997 */         return;
/*      */       case 15:
/*      */       case 253:
/*      */       case 254:
/* 2001 */         if ((value instanceof byte[]))
/* 2002 */           packet.writeLenBytes((byte[])value);
/* 2003 */         else if (!this.isLoadDataQuery) {
/* 2004 */           packet.writeLenString((String)value, this.charEncoding, this.connection.getServerCharacterEncoding(), this.charConverter, this.connection.parserKnowsUnicode());
/*      */         }
/*      */         else
/*      */         {
/* 2009 */           packet.writeLenBytes(((String)value).getBytes());
/*      */         }
/*      */ 
/* 2012 */         return;
/*      */       }
/*      */     }
/*      */     catch (UnsupportedEncodingException uEE)
/*      */     {
/* 2017 */       throw new SQLException(Messages.getString("ServerPreparedStatement.22") + this.connection.getEncoding() + "'", "S1000");
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDataTime412AndOlder(Buffer intoBuf, java.util.Date dt)
/*      */     throws SQLException
/*      */   {
/* 2028 */     if (this.dateTimeBindingCal == null) {
/* 2029 */       this.dateTimeBindingCal = Calendar.getInstance();
/*      */     }
/*      */ 
/* 2032 */     this.dateTimeBindingCal.setTime(dt);
/*      */ 
/* 2034 */     intoBuf.ensureCapacity(8);
/* 2035 */     intoBuf.writeByte(7);
/*      */ 
/* 2037 */     int year = this.dateTimeBindingCal.get(1);
/* 2038 */     int month = this.dateTimeBindingCal.get(2) + 1;
/* 2039 */     int date = this.dateTimeBindingCal.get(5);
/*      */ 
/* 2041 */     intoBuf.writeInt(year);
/* 2042 */     intoBuf.writeByte((byte)month);
/* 2043 */     intoBuf.writeByte((byte)date);
/*      */ 
/* 2045 */     if ((dt instanceof java.sql.Date)) {
/* 2046 */       intoBuf.writeByte(0);
/* 2047 */       intoBuf.writeByte(0);
/* 2048 */       intoBuf.writeByte(0);
/*      */     } else {
/* 2050 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(11));
/*      */ 
/* 2052 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(12));
/*      */ 
/* 2054 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(13));
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeDateTime(Buffer intoBuf, java.util.Date dt, MysqlIO mysql)
/*      */     throws SQLException
/*      */   {
/* 2061 */     if (this.connection.versionMeetsMinimum(4, 1, 3))
/* 2062 */       storeDateTime413AndNewer(intoBuf, dt);
/*      */     else
/* 2064 */       storeDataTime412AndOlder(intoBuf, dt);
/*      */   }
/*      */ 
/*      */   private void storeDateTime413AndNewer(Buffer intoBuf, java.util.Date dt)
/*      */     throws SQLException
/*      */   {
/* 2072 */     if (this.dateTimeBindingCal == null) {
/* 2073 */       this.dateTimeBindingCal = Calendar.getInstance();
/*      */     }
/*      */ 
/* 2076 */     this.dateTimeBindingCal.setTime(dt);
/*      */ 
/* 2078 */     byte length = 7;
/*      */ 
/* 2080 */     intoBuf.ensureCapacity(length);
/*      */ 
/* 2082 */     if ((dt instanceof Timestamp)) {
/* 2083 */       length = 11;
/*      */     }
/*      */ 
/* 2086 */     intoBuf.writeByte(length);
/*      */ 
/* 2088 */     int year = this.dateTimeBindingCal.get(1);
/* 2089 */     int month = this.dateTimeBindingCal.get(2) + 1;
/* 2090 */     int date = this.dateTimeBindingCal.get(5);
/*      */ 
/* 2092 */     intoBuf.writeInt(year);
/* 2093 */     intoBuf.writeByte((byte)month);
/* 2094 */     intoBuf.writeByte((byte)date);
/*      */ 
/* 2096 */     if ((dt instanceof java.sql.Date)) {
/* 2097 */       intoBuf.writeByte(0);
/* 2098 */       intoBuf.writeByte(0);
/* 2099 */       intoBuf.writeByte(0);
/*      */     } else {
/* 2101 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(11));
/*      */ 
/* 2103 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(12));
/*      */ 
/* 2105 */       intoBuf.writeByte((byte)this.dateTimeBindingCal.get(13));
/*      */     }
/*      */ 
/* 2109 */     if (length == 11)
/* 2110 */       intoBuf.writeLong(((Timestamp)dt).getNanos());
/*      */   }
/*      */ 
/*      */   private void storeReader(MysqlIO mysql, int parameterIndex, Buffer packet, Reader inStream)
/*      */     throws SQLException
/*      */   {
/* 2119 */     int maxBytesChar = 2;
/*      */ 
/* 2121 */     if (this.connection.getEncoding() != null) {
/* 2122 */       maxBytesChar = this.connection.getMaxBytesPerChar(this.connection.getEncoding());
/*      */ 
/* 2125 */       if (maxBytesChar == 1) {
/* 2126 */         maxBytesChar = 2;
/*      */       }
/*      */     }
/*      */ 
/* 2130 */     char[] buf = new char[8192 / maxBytesChar];
/*      */ 
/* 2132 */     int numRead = 0;
/*      */ 
/* 2134 */     int bytesInPacket = 0;
/* 2135 */     int totalBytesRead = 0;
/* 2136 */     int bytesReadAtLastSend = 0;
/* 2137 */     int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */     try
/*      */     {
/* 2140 */       packet.clear();
/* 2141 */       packet.writeByte(24);
/* 2142 */       packet.writeLong(this.serverStatementId);
/* 2143 */       packet.writeInt(parameterIndex);
/*      */ 
/* 2145 */       boolean readAny = false;
/*      */ 
/* 2147 */       while ((numRead = inStream.read(buf)) != -1) {
/* 2148 */         readAny = true;
/*      */ 
/* 2150 */         byte[] valueAsBytes = StringUtils.getBytes(buf, null, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), 0, numRead, this.connection.parserKnowsUnicode());
/*      */ 
/* 2155 */         packet.writeBytesNoNull(valueAsBytes, 0, valueAsBytes.length);
/*      */ 
/* 2157 */         bytesInPacket += valueAsBytes.length;
/* 2158 */         totalBytesRead += valueAsBytes.length;
/*      */ 
/* 2160 */         if (bytesInPacket >= packetIsFullAt) {
/* 2161 */           bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2163 */           mysql.sendCommand(24, null, packet, true, null);
/*      */ 
/* 2166 */           bytesInPacket = 0;
/* 2167 */           packet.clear();
/* 2168 */           packet.writeByte(24);
/* 2169 */           packet.writeLong(this.serverStatementId);
/* 2170 */           packet.writeInt(parameterIndex);
/*      */         }
/*      */       }
/*      */ 
/* 2174 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2175 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */ 
/* 2179 */       if (!readAny)
/* 2180 */         mysql.sendCommand(24, null, packet, true, null);
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/* 2184 */       throw new SQLException(Messages.getString("ServerPreparedStatement.24") + ioEx.toString(), "S1000");
/*      */     }
/*      */     finally
/*      */     {
/* 2188 */       if (inStream != null)
/*      */         try {
/* 2190 */           inStream.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void storeStream(MysqlIO mysql, int parameterIndex, Buffer packet, InputStream inStream) throws SQLException
/*      */   {
/* 2200 */     byte[] buf = new byte[8192];
/*      */ 
/* 2202 */     int numRead = 0;
/*      */     try
/*      */     {
/* 2205 */       int bytesInPacket = 0;
/* 2206 */       int totalBytesRead = 0;
/* 2207 */       int bytesReadAtLastSend = 0;
/* 2208 */       int packetIsFullAt = this.connection.getBlobSendChunkSize();
/*      */ 
/* 2210 */       packet.clear();
/* 2211 */       packet.writeByte(24);
/* 2212 */       packet.writeLong(this.serverStatementId);
/* 2213 */       packet.writeInt(parameterIndex);
/*      */ 
/* 2215 */       boolean readAny = false;
/*      */ 
/* 2217 */       while ((numRead = inStream.read(buf)) != -1)
/*      */       {
/* 2219 */         readAny = true;
/*      */ 
/* 2221 */         packet.writeBytesNoNull(buf, 0, numRead);
/* 2222 */         bytesInPacket += numRead;
/* 2223 */         totalBytesRead += numRead;
/*      */ 
/* 2225 */         if (bytesInPacket >= packetIsFullAt) {
/* 2226 */           bytesReadAtLastSend = totalBytesRead;
/*      */ 
/* 2228 */           mysql.sendCommand(24, null, packet, true, null);
/*      */ 
/* 2231 */           bytesInPacket = 0;
/* 2232 */           packet.clear();
/* 2233 */           packet.writeByte(24);
/* 2234 */           packet.writeLong(this.serverStatementId);
/* 2235 */           packet.writeInt(parameterIndex);
/*      */         }
/*      */       }
/*      */ 
/* 2239 */       if (totalBytesRead != bytesReadAtLastSend) {
/* 2240 */         mysql.sendCommand(24, null, packet, true, null);
/*      */       }
/*      */ 
/* 2244 */       if (!readAny)
/* 2245 */         mysql.sendCommand(24, null, packet, true, null);
/*      */     }
/*      */     catch (IOException ioEx)
/*      */     {
/* 2249 */       throw new SQLException(Messages.getString("ServerPreparedStatement.25") + ioEx.toString(), "S1000");
/*      */     }
/*      */     finally
/*      */     {
/* 2253 */       if (inStream != null)
/*      */         try {
/* 2255 */           inStream.close();
/*      */         }
/*      */         catch (IOException ioEx)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 2267 */     StringBuffer toStringBuf = new StringBuffer();
/*      */ 
/* 2269 */     toStringBuf.append("com.mysql.jdbc.ServerPreparedStatement[");
/* 2270 */     toStringBuf.append(this.serverStatementId);
/* 2271 */     toStringBuf.append("] - ");
/*      */     try
/*      */     {
/* 2274 */       toStringBuf.append(asSql());
/*      */     } catch (SQLException sqlEx) {
/* 2276 */       toStringBuf.append(Messages.getString("ServerPreparedStatement.6"));
/* 2277 */       toStringBuf.append(sqlEx);
/*      */     }
/*      */ 
/* 2280 */     return toStringBuf.toString();
/*      */   }
/*      */ 
/*      */   static class BindValue
/*      */   {
/*   82 */     long boundBeforeExecutionNum = 0L;
/*      */     long bindLength;
/*      */     int bufferType;
/*      */     byte byteBinding;
/*      */     double doubleBinding;
/*      */     float floatBinding;
/*      */     int intBinding;
/*      */     boolean isLongData;
/*      */     boolean isNull;
/*  100 */     boolean isSet = false;
/*      */     long longBinding;
/*      */     short shortBinding;
/*      */     Object value;
/*      */ 
/*      */     BindValue()
/*      */     {
/*      */     }
/*      */ 
/*      */     BindValue(BindValue copyMe)
/*      */     {
/*  112 */       this.value = copyMe.value;
/*  113 */       this.isSet = copyMe.isSet;
/*  114 */       this.isLongData = copyMe.isLongData;
/*  115 */       this.isNull = copyMe.isNull;
/*  116 */       this.bufferType = copyMe.bufferType;
/*  117 */       this.bindLength = copyMe.bindLength;
/*  118 */       this.byteBinding = copyMe.byteBinding;
/*  119 */       this.shortBinding = copyMe.shortBinding;
/*  120 */       this.intBinding = copyMe.intBinding;
/*  121 */       this.longBinding = copyMe.longBinding;
/*  122 */       this.floatBinding = copyMe.floatBinding;
/*  123 */       this.doubleBinding = copyMe.doubleBinding;
/*      */     }
/*      */ 
/*      */     void reset() {
/*  127 */       this.isSet = false;
/*  128 */       this.value = null;
/*  129 */       this.isLongData = false;
/*      */ 
/*  131 */       this.byteBinding = 0;
/*  132 */       this.shortBinding = 0;
/*  133 */       this.intBinding = 0;
/*  134 */       this.longBinding = 0L;
/*  135 */       this.floatBinding = 0.0F;
/*  136 */       this.doubleBinding = 0.0D;
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  140 */       return toString(false);
/*      */     }
/*      */ 
/*      */     public String toString(boolean quoteIfNeeded) {
/*  144 */       if (this.isLongData) {
/*  145 */         return "' STREAM DATA '";
/*      */       }
/*      */ 
/*  148 */       switch (this.bufferType) {
/*      */       case 1:
/*  150 */         return String.valueOf(this.byteBinding);
/*      */       case 2:
/*  152 */         return String.valueOf(this.shortBinding);
/*      */       case 3:
/*  154 */         return String.valueOf(this.intBinding);
/*      */       case 8:
/*  156 */         return String.valueOf(this.longBinding);
/*      */       case 4:
/*  158 */         return String.valueOf(this.floatBinding);
/*      */       case 5:
/*  160 */         return String.valueOf(this.doubleBinding);
/*      */       case 7:
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       case 15:
/*      */       case 253:
/*      */       case 254:
/*  168 */         if (quoteIfNeeded) {
/*  169 */           return "'" + String.valueOf(this.value) + "'";
/*      */         }
/*  171 */         return String.valueOf(this.value);
/*      */       }
/*      */ 
/*  174 */       if ((this.value instanceof byte[])) {
/*  175 */         return "byte data";
/*      */       }
/*      */ 
/*  178 */       if (quoteIfNeeded) {
/*  179 */         return "'" + String.valueOf(this.value) + "'";
/*      */       }
/*  181 */       return String.valueOf(this.value);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class BatchedBindValues
/*      */   {
/*      */     ServerPreparedStatement.BindValue[] batchedParameterValues;
/*      */ 
/*      */     BatchedBindValues(ServerPreparedStatement.BindValue[] paramVals)
/*      */     {
/*   70 */       int numParams = paramVals.length;
/*      */ 
/*   72 */       this.batchedParameterValues = new ServerPreparedStatement.BindValue[numParams];
/*      */ 
/*   74 */       for (int i = 0; i < numParams; i++)
/*   75 */         this.batchedParameterValues[i] = new ServerPreparedStatement.BindValue(paramVals[i]);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.ServerPreparedStatement
 * JD-Core Version:    0.6.0
 */
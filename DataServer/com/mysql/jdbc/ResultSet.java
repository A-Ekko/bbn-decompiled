/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.DataTruncation;
/*      */ import java.sql.Date;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.SQLWarning;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.Calendar;
/*      */ import java.util.GregorianCalendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public class ResultSet
/*      */   implements java.sql.ResultSet
/*      */ {
/*  118 */   protected static int resultCounter = 1;
/*      */ 
/*  139 */   protected String catalog = null;
/*      */ 
/*  142 */   protected Map columnNameToIndex = null;
/*      */ 
/*  145 */   protected boolean[] columnUsed = null;
/*      */   protected Connection connection;
/*  152 */   protected int currentRow = -1;
/*      */   private TimeZone defaultTimeZone;
/*  157 */   protected boolean doingUpdates = false;
/*      */ 
/*  159 */   protected ProfileEventSink eventSink = null;
/*      */ 
/*  161 */   private Calendar fastDateCal = null;
/*      */ 
/*  164 */   protected int fetchDirection = 1000;
/*      */ 
/*  167 */   protected int fetchSize = 0;
/*      */   protected Field[] fields;
/*      */   protected char firstCharOfQuery;
/*  180 */   protected Map fullColumnNameToIndex = null;
/*      */ 
/*  182 */   protected boolean hasBuiltIndexMapping = false;
/*      */ 
/*  188 */   protected boolean isBinaryEncoded = false;
/*      */ 
/*  191 */   protected boolean isClosed = false;
/*      */ 
/*  193 */   protected ResultSet nextResultSet = null;
/*      */ 
/*  196 */   protected boolean onInsertRow = false;
/*      */   protected Statement owningStatement;
/*      */   protected Throwable pointOfOrigin;
/*  207 */   protected boolean profileSql = false;
/*      */ 
/*  213 */   protected boolean reallyResult = false;
/*      */   protected int resultId;
/*  219 */   protected int resultSetConcurrency = 0;
/*      */ 
/*  222 */   protected int resultSetType = 0;
/*      */   protected RowData rowData;
/*  231 */   protected String serverInfo = null;
/*      */   protected Calendar sessionCalendar;
/*  236 */   protected Object[] thisRow = null;
/*      */   protected long updateCount;
/*  250 */   protected long updateId = -1L;
/*      */ 
/*  252 */   private boolean useStrictFloatingPoint = false;
/*      */ 
/*  254 */   protected boolean useUsageAdvisor = false;
/*      */ 
/*  257 */   protected SQLWarning warningChain = null;
/*      */ 
/*  260 */   protected boolean wasNullFlag = false;
/*      */   protected java.sql.Statement wrapperStatement;
/*      */   protected boolean retainOwningStatement;
/*      */ 
/*      */   protected static BigInteger convertLongToUlong(long longVal)
/*      */   {
/*  125 */     byte[] asBytes = new byte[8];
/*  126 */     asBytes[7] = (byte)(int)(longVal & 0xFF);
/*  127 */     asBytes[6] = (byte)(int)(longVal >>> 8);
/*  128 */     asBytes[5] = (byte)(int)(longVal >>> 16);
/*  129 */     asBytes[4] = (byte)(int)(longVal >>> 24);
/*  130 */     asBytes[3] = (byte)(int)(longVal >>> 32);
/*  131 */     asBytes[2] = (byte)(int)(longVal >>> 40);
/*  132 */     asBytes[1] = (byte)(int)(longVal >>> 48);
/*  133 */     asBytes[0] = (byte)(int)(longVal >>> 56);
/*      */ 
/*  135 */     return new BigInteger(1, asBytes);
/*      */   }
/*      */ 
/*      */   public ResultSet(long updateCount, long updateID, Connection conn, Statement creatorStmt)
/*      */   {
/*  280 */     this.updateCount = updateCount;
/*  281 */     this.updateId = updateID;
/*  282 */     this.reallyResult = false;
/*  283 */     this.fields = new Field[0];
/*      */ 
/*  285 */     this.connection = conn;
/*  286 */     this.owningStatement = creatorStmt;
/*      */ 
/*  288 */     this.retainOwningStatement = false;
/*      */ 
/*  290 */     if (this.connection != null)
/*  291 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */   }
/*      */ 
/*      */   public ResultSet(String catalog, Field[] fields, RowData tuples, Connection conn, Statement creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  315 */     this.connection = conn;
/*      */ 
/*  317 */     if (this.connection != null) {
/*  318 */       this.useStrictFloatingPoint = this.connection.getStrictFloatingPoint();
/*      */ 
/*  320 */       setDefaultTimeZone(this.connection.getDefaultTimeZone());
/*      */     }
/*      */ 
/*  323 */     this.owningStatement = creatorStmt;
/*      */ 
/*  325 */     this.catalog = catalog;
/*  326 */     this.profileSql = this.connection.getProfileSql();
/*      */ 
/*  328 */     this.fields = fields;
/*  329 */     this.rowData = tuples;
/*  330 */     this.updateCount = this.rowData.size();
/*      */ 
/*  337 */     this.reallyResult = true;
/*      */ 
/*  340 */     if (this.rowData.size() > 0) {
/*  341 */       if ((this.updateCount == 1L) && 
/*  342 */         (this.thisRow == null)) {
/*  343 */         this.rowData.close();
/*  344 */         this.updateCount = -1L;
/*      */       }
/*      */     }
/*      */     else {
/*  348 */       this.thisRow = null;
/*      */     }
/*      */ 
/*  351 */     this.rowData.setOwner(this);
/*      */ 
/*  353 */     if ((this.profileSql) || (this.connection.getUseUsageAdvisor())) {
/*  354 */       this.columnUsed = new boolean[this.fields.length];
/*  355 */       this.pointOfOrigin = new Throwable();
/*  356 */       this.resultId = (resultCounter++);
/*  357 */       this.useUsageAdvisor = this.connection.getUseUsageAdvisor();
/*  358 */       this.eventSink = ProfileEventSink.getInstance(this.connection);
/*      */     }
/*      */ 
/*  361 */     if (this.connection.getGatherPerformanceMetrics()) {
/*  362 */       this.connection.incrementNumberOfResultSetsCreated();
/*      */ 
/*  364 */       Map tableNamesMap = new HashMap();
/*      */ 
/*  366 */       for (int i = 0; i < this.fields.length; i++) {
/*  367 */         Field f = this.fields[i];
/*      */ 
/*  369 */         String tableName = f.getOriginalTableName();
/*      */ 
/*  371 */         if (tableName == null) {
/*  372 */           tableName = f.getTableName();
/*      */         }
/*      */ 
/*  375 */         if (tableName != null) {
/*  376 */           if (this.connection.lowerCaseTableNames()) {
/*  377 */             tableName = tableName.toLowerCase();
/*      */           }
/*      */ 
/*  381 */           tableNamesMap.put(tableName, null);
/*      */         }
/*      */       }
/*      */ 
/*  385 */       this.connection.reportNumberOfTablesAccessed(tableNamesMap.size());
/*      */     }
/*      */ 
/*  388 */     this.retainOwningStatement = false;
/*      */ 
/*  390 */     if (this.connection != null)
/*  391 */       this.retainOwningStatement = this.connection.getRetainStatementAfterResultSetClose();
/*      */   }
/*      */ 
/*      */   public boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  435 */     checkClosed();
/*      */     boolean b;
/*      */     boolean b;
/*  439 */     if (this.rowData.size() == 0) {
/*  440 */       b = false;
/*      */     } else {
/*  442 */       if (row == 0) {
/*  443 */         throw new SQLException(Messages.getString("ResultSet.Cannot_absolute_position_to_row_0_110"), "S1009");
/*      */       }
/*      */ 
/*  449 */       if (this.onInsertRow) {
/*  450 */         this.onInsertRow = false;
/*      */       }
/*      */ 
/*  453 */       if (this.doingUpdates)
/*  454 */         this.doingUpdates = false;
/*      */       boolean b;
/*  457 */       if (row == 1) {
/*  458 */         b = first();
/*      */       }
/*      */       else
/*      */       {
/*      */         boolean b;
/*  459 */         if (row == -1) {
/*  460 */           b = last();
/*      */         }
/*      */         else
/*      */         {
/*      */           boolean b;
/*  461 */           if (row > this.rowData.size()) {
/*  462 */             afterLast();
/*  463 */             b = false;
/*      */           }
/*      */           else
/*      */           {
/*      */             boolean b;
/*  465 */             if (row < 0)
/*      */             {
/*  467 */               int newRowPosition = this.rowData.size() + row + 1;
/*      */               boolean b;
/*  469 */               if (newRowPosition <= 0) {
/*  470 */                 beforeFirst();
/*  471 */                 b = false;
/*      */               } else {
/*  473 */                 b = absolute(newRowPosition);
/*      */               }
/*      */             } else {
/*  476 */               row--;
/*  477 */               this.rowData.setCurrentRow(row);
/*  478 */               this.thisRow = this.rowData.getAt(row);
/*  479 */               b = true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  484 */     return b;
/*      */   }
/*      */ 
/*      */   private synchronized void addAWarning(SQLWarning warning)
/*      */   {
/*  491 */     if (this.warningChain == null) {
/*  492 */       this.warningChain = warning;
/*      */     } else {
/*  494 */       SQLWarning warningToAppendTo = this.warningChain;
/*      */ 
/*  496 */       while (warningToAppendTo.getNextWarning() != null) {
/*  497 */         warningToAppendTo = warningToAppendTo.getNextWarning();
/*      */       }
/*      */ 
/*  500 */       warningToAppendTo.setNextWarning(warning);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void afterLast()
/*      */     throws SQLException
/*      */   {
/*  518 */     checkClosed();
/*      */ 
/*  520 */     if (this.onInsertRow) {
/*  521 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/*  524 */     if (this.doingUpdates) {
/*  525 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*  528 */     if (this.rowData.size() != 0) {
/*  529 */       this.rowData.afterLast();
/*  530 */       this.thisRow = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  547 */     checkClosed();
/*      */ 
/*  549 */     if (this.onInsertRow) {
/*  550 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/*  553 */     if (this.doingUpdates) {
/*  554 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*  557 */     if (this.rowData.size() == 0) {
/*  558 */       return;
/*      */     }
/*      */ 
/*  561 */     this.rowData.beforeFirst();
/*  562 */     this.thisRow = null;
/*      */   }
/*      */ 
/*      */   protected void buildIndexMapping()
/*      */     throws SQLException
/*      */   {
/*  573 */     int numFields = this.fields.length;
/*  574 */     this.columnNameToIndex = new HashMap(numFields);
/*  575 */     this.fullColumnNameToIndex = new HashMap(numFields);
/*      */ 
/*  589 */     for (int i = numFields - 1; i >= 0; i--) {
/*  590 */       Integer index = new Integer(i);
/*  591 */       String columnName = this.fields[i].getName();
/*  592 */       String fullColumnName = this.fields[i].getFullName();
/*      */ 
/*  594 */       if (columnName != null) {
/*  595 */         this.columnNameToIndex.put(columnName, index);
/*  596 */         this.columnNameToIndex.put(columnName.toUpperCase(), index);
/*  597 */         this.columnNameToIndex.put(columnName.toLowerCase(), index);
/*      */       }
/*      */ 
/*  600 */       if (fullColumnName != null) {
/*  601 */         this.fullColumnNameToIndex.put(fullColumnName, index);
/*  602 */         this.fullColumnNameToIndex.put(fullColumnName.toUpperCase(), index);
/*      */ 
/*  604 */         this.fullColumnNameToIndex.put(fullColumnName.toLowerCase(), index);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  610 */     this.hasBuiltIndexMapping = true;
/*      */   }
/*      */ 
/*      */   public void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  626 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   protected final synchronized void checkClosed()
/*      */     throws SQLException
/*      */   {
/*  636 */     if (this.isClosed)
/*  637 */       throw new SQLException(Messages.getString("ResultSet.Operation_not_allowed_after_ResultSet_closed_144"), "S1000");
/*      */   }
/*      */ 
/*      */   protected final void checkColumnBounds(int columnIndex)
/*      */     throws SQLException
/*      */   {
/*  654 */     if ((columnIndex < 1) || (columnIndex > this.fields.length)) {
/*  655 */       throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */     }
/*      */ 
/*  662 */     if ((this.profileSql) || (this.useUsageAdvisor))
/*  663 */       this.columnUsed[(columnIndex - 1)] = true;
/*      */   }
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  675 */     checkClosed();
/*      */ 
/*  677 */     if ((!this.rowData.isDynamic()) && (this.rowData.size() == 0)) {
/*  678 */       throw new SQLException(Messages.getString("ResultSet.Illegal_operation_on_empty_result_set"), "S1000");
/*      */     }
/*      */ 
/*  684 */     if (this.rowData.isBeforeFirst()) {
/*  685 */       throw new SQLException(Messages.getString("ResultSet.Before_start_of_result_set_146"), "S1000");
/*      */     }
/*      */ 
/*  690 */     if (this.rowData.isAfterLast())
/*  691 */       throw new SQLException(Messages.getString("ResultSet.After_end_of_result_set_148"), "S1000");
/*      */   }
/*      */ 
/*      */   protected void clearNextResult()
/*      */   {
/*  702 */     this.nextResultSet = null;
/*      */   }
/*      */ 
/*      */   public void clearWarnings()
/*      */     throws SQLException
/*      */   {
/*  713 */     this.warningChain = null;
/*      */   }
/*      */ 
/*      */   public synchronized void close()
/*      */     throws SQLException
/*      */   {
/*  734 */     realClose(true);
/*      */   }
/*      */ 
/*      */   private int convertToZeroWithEmptyCheck()
/*      */     throws SQLException
/*      */   {
/*  741 */     if (this.connection.getEmptyStringsConvertToZero()) {
/*  742 */       return 0;
/*      */     }
/*      */ 
/*  745 */     throw new SQLException("Can't convert empty string ('') to numeric", "22018");
/*      */   }
/*      */ 
/*      */   protected final ResultSet copy()
/*      */     throws SQLException
/*      */   {
/*  753 */     ResultSet rs = new ResultSet(this.catalog, this.fields, this.rowData, this.connection, this.owningStatement);
/*      */ 
/*  756 */     return rs;
/*      */   }
/*      */ 
/*      */   public void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  770 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   private String extractStringFromNativeColumn(int columnIndex, int mysqlType)
/*      */     throws SQLException
/*      */   {
/*  782 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/*  784 */     if ((this.thisRow[columnIndexMinusOne] instanceof String)) {
/*  785 */       return (String)this.thisRow[columnIndexMinusOne];
/*      */     }
/*      */ 
/*  788 */     if (this.thisRow[columnIndexMinusOne] == null) {
/*  789 */       return null;
/*      */     }
/*      */ 
/*  792 */     String stringVal = null;
/*      */ 
/*  794 */     if ((this.connection != null) && (this.connection.getUseUnicode())) {
/*      */       try {
/*  796 */         String encoding = this.fields[columnIndexMinusOne].getCharacterSet();
/*      */ 
/*  799 */         if (encoding == null) {
/*  800 */           stringVal = new String((byte[])this.thisRow[columnIndexMinusOne]);
/*      */         }
/*      */         else {
/*  803 */           SingleByteCharsetConverter converter = this.connection.getCharsetConverter(encoding);
/*      */ 
/*  806 */           if (converter != null) {
/*  807 */             stringVal = converter.toString((byte[])this.thisRow[columnIndexMinusOne]);
/*      */           }
/*      */           else {
/*  810 */             stringVal = new String((byte[])this.thisRow[columnIndexMinusOne], encoding);
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (UnsupportedEncodingException E)
/*      */       {
/*  816 */         throw new SQLException(Messages.getString("ResultSet.Unsupported_character_encoding____138") + this.connection.getEncoding() + "'.", "0S100");
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  822 */       stringVal = StringUtils.toAsciiString((byte[])this.thisRow[columnIndexMinusOne]);
/*      */     }
/*      */ 
/*  826 */     return stringVal;
/*      */   }
/*      */ 
/*      */   private synchronized Date fastDateCreate(Calendar cal, int year, int month, int day)
/*      */   {
/*  831 */     if (cal == null) {
/*  832 */       if (this.fastDateCal == null) {
/*  833 */         this.fastDateCal = new GregorianCalendar(Locale.US);
/*  834 */         this.fastDateCal.setTimeZone(getDefaultTimeZone());
/*      */       }
/*      */ 
/*  837 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/*  840 */     return TimeUtil.fastDateCreate(cal, year, month, day);
/*      */   }
/*      */ 
/*      */   private synchronized Time fastTimeCreate(Calendar cal, int hour, int minute, int second)
/*      */   {
/*  845 */     if (cal == null) {
/*  846 */       if (this.fastDateCal == null) {
/*  847 */         this.fastDateCal = new GregorianCalendar(Locale.US);
/*  848 */         this.fastDateCal.setTimeZone(getDefaultTimeZone());
/*      */       }
/*      */ 
/*  851 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/*  854 */     return TimeUtil.fastTimeCreate(cal, hour, minute, second);
/*      */   }
/*      */ 
/*      */   private synchronized Timestamp fastTimestampCreate(Calendar cal, int year, int month, int day, int hour, int minute, int seconds, int secondsPart)
/*      */   {
/*  860 */     if (cal == null) {
/*  861 */       if (this.fastDateCal == null) {
/*  862 */         this.fastDateCal = new GregorianCalendar(Locale.US);
/*  863 */         this.fastDateCal.setTimeZone(getDefaultTimeZone());
/*      */       }
/*      */ 
/*  866 */       cal = this.fastDateCal;
/*      */     }
/*      */ 
/*  869 */     return TimeUtil.fastTimestampCreate(cal, year, month, day, hour, minute, seconds, secondsPart);
/*      */   }
/*      */ 
/*      */   public synchronized int findColumn(String columnName)
/*      */     throws SQLException
/*      */   {
/*  904 */     if (!this.hasBuiltIndexMapping) {
/*  905 */       buildIndexMapping();
/*      */     }
/*      */ 
/*  908 */     Integer index = (Integer)this.columnNameToIndex.get(columnName);
/*      */ 
/*  910 */     if (index == null) {
/*  911 */       index = (Integer)this.fullColumnNameToIndex.get(columnName);
/*      */     }
/*      */ 
/*  914 */     if (index != null) {
/*  915 */       return index.intValue() + 1;
/*      */     }
/*      */ 
/*  920 */     for (int i = 0; i < this.fields.length; i++) {
/*  921 */       if (this.fields[i].getName().equalsIgnoreCase(columnName))
/*  922 */         return i + 1;
/*  923 */       if (this.fields[i].getFullName().equalsIgnoreCase(columnName))
/*      */       {
/*  925 */         return i + 1;
/*      */       }
/*      */     }
/*      */ 
/*  929 */     throw new SQLException(Messages.getString("ResultSet.Column____112") + columnName + Messages.getString("ResultSet.___not_found._113"), "S0022");
/*      */   }
/*      */ 
/*      */   public boolean first()
/*      */     throws SQLException
/*      */   {
/*  949 */     checkClosed();
/*      */ 
/*  951 */     if (this.rowData.isEmpty()) {
/*  952 */       return false;
/*      */     }
/*      */ 
/*  955 */     if (this.onInsertRow) {
/*  956 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/*  959 */     if (this.doingUpdates) {
/*  960 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/*  963 */     this.rowData.beforeFirst();
/*  964 */     this.thisRow = this.rowData.next();
/*      */ 
/*  966 */     return true;
/*      */   }
/*      */ 
/*      */   public Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/*  983 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public Array getArray(String colName)
/*      */     throws SQLException
/*      */   {
/* 1000 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1029 */     checkRowPos();
/*      */ 
/* 1031 */     if (!this.isBinaryEncoded) {
/* 1032 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 1035 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getAsciiStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1050 */     return getAsciiStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1067 */     if (!this.isBinaryEncoded) {
/* 1068 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1071 */       if (stringVal != null) {
/* 1072 */         if (stringVal.length() == 0) {
/* 1073 */           BigDecimal val = new BigDecimal(convertToZeroWithEmptyCheck());
/*      */ 
/* 1075 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 1079 */           BigDecimal val = new BigDecimal(stringVal);
/*      */ 
/* 1081 */           return val;
/*      */         } catch (NumberFormatException ex) {
/* 1083 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { stringVal, new Integer(columnIndex) }), "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1091 */       return null;
/*      */     }
/*      */ 
/* 1094 */     return getNativeBigDecimal(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1115 */     if (!this.isBinaryEncoded) {
/* 1116 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1119 */       if (stringVal != null) {
/* 1120 */         if (stringVal.length() == 0) {
/* 1121 */           BigDecimal val = new BigDecimal(convertToZeroWithEmptyCheck());
/*      */           try
/*      */           {
/* 1124 */             return val.setScale(scale);
/*      */           } catch (ArithmeticException ex) {
/*      */             try {
/* 1127 */               return val.setScale(scale, 4);
/*      */             }
/*      */             catch (ArithmeticException arEx) {
/* 1130 */               throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____124") + stringVal + Messages.getString("ResultSet.___in_column__125") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1146 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 1148 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { new Integer(columnIndex), stringVal }), "S1009");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 1156 */           return val.setScale(scale);
/*      */         }
/*      */         catch (ArithmeticException ex)
/*      */         {
/*      */           try
/*      */           {
/*      */             BigDecimal val;
/* 1159 */             return val.setScale(scale, 4);
/*      */           } catch (ArithmeticException arithEx) {
/* 1161 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal", new Object[] { new Integer(columnIndex), stringVal }), "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1170 */       return null;
/*      */     }
/*      */ 
/* 1173 */     return getNativeBigDecimal(columnIndex, scale);
/*      */   }
/*      */ 
/*      */   public BigDecimal getBigDecimal(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1189 */     return getBigDecimal(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public BigDecimal getBigDecimal(String columnName, int scale)
/*      */     throws SQLException
/*      */   {
/* 1209 */     return getBigDecimal(findColumn(columnName), scale);
/*      */   }
/*      */ 
/*      */   private final BigDecimal getBigDecimalFromString(String stringVal, int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 1216 */     if (stringVal != null) {
/* 1217 */       if (stringVal.length() == 0) {
/* 1218 */         BigDecimal bdVal = new BigDecimal(convertToZeroWithEmptyCheck());
/*      */ 
/* 1220 */         return bdVal;
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1225 */         return new BigDecimal(stringVal).setScale(scale);
/*      */       } catch (ArithmeticException ex) {
/*      */         try {
/* 1228 */           return new BigDecimal(stringVal).setScale(scale, 4);
/*      */         }
/*      */         catch (ArithmeticException arEx) {
/* 1231 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____166") + stringVal + Messages.getString("ResultSet.___in_column__167") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/* 1243 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____166") + stringVal + Messages.getString("ResultSet.___in_column__167") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1255 */     return null;
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1276 */     checkRowPos();
/*      */ 
/* 1278 */     if (!this.isBinaryEncoded) {
/* 1279 */       byte[] b = getBytes(columnIndex);
/*      */ 
/* 1281 */       if (b != null) {
/* 1282 */         return new ByteArrayInputStream(b);
/*      */       }
/*      */ 
/* 1285 */       return null;
/*      */     }
/*      */ 
/* 1288 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public InputStream getBinaryStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1303 */     return getBinaryStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1318 */     if (!this.isBinaryEncoded) {
/* 1319 */       checkRowPos();
/*      */ 
/* 1321 */       if ((columnIndex < 1) || (columnIndex > this.fields.length)) {
/* 1322 */         throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1330 */         if (this.thisRow[(columnIndex - 1)] == null)
/* 1331 */           this.wasNullFlag = true;
/*      */         else
/* 1333 */           this.wasNullFlag = false;
/*      */       }
/*      */       catch (NullPointerException ex) {
/* 1336 */         this.wasNullFlag = true;
/*      */       }
/*      */ 
/* 1339 */       if (this.wasNullFlag) {
/* 1340 */         return null;
/*      */       }
/*      */ 
/* 1343 */       if (!this.connection.getEmulateLocators()) {
/* 1344 */         return new Blob((byte[])this.thisRow[(columnIndex - 1)]);
/*      */       }
/*      */ 
/* 1347 */       return new BlobFromLocator(this, columnIndex);
/*      */     }
/*      */ 
/* 1350 */     return getNativeBlob(columnIndex);
/*      */   }
/*      */ 
/*      */   public java.sql.Blob getBlob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1365 */     return getBlob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1380 */     if (!this.isBinaryEncoded) {
/* 1381 */       checkColumnBounds(columnIndex);
/*      */ 
/* 1388 */       int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 1390 */       Field field = this.fields[columnIndexMinusOne];
/*      */ 
/* 1392 */       if (field.getMysqlType() == 16) {
/* 1393 */         if (this.thisRow[columnIndexMinusOne] == null) {
/* 1394 */           this.wasNullFlag = true;
/*      */ 
/* 1396 */           return false;
/*      */         }
/*      */ 
/* 1399 */         this.wasNullFlag = false;
/*      */ 
/* 1401 */         if (((byte[])this.thisRow[columnIndexMinusOne]).length == 0) {
/* 1402 */           return false;
/*      */         }
/*      */ 
/* 1405 */         byte boolVal = ((byte[])this.thisRow[columnIndexMinusOne])[0];
/*      */ 
/* 1407 */         return boolVal > 0;
/*      */       }
/*      */ 
/* 1410 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1412 */       if ((stringVal != null) && (stringVal.length() > 0)) {
/* 1413 */         int c = Character.toLowerCase(stringVal.charAt(0));
/*      */ 
/* 1415 */         return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
/*      */       }
/*      */ 
/* 1419 */       return false;
/*      */     }
/*      */ 
/* 1422 */     return getNativeBoolean(columnIndex);
/*      */   }
/*      */ 
/*      */   public boolean getBoolean(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1437 */     return getBoolean(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final boolean getBooleanFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1442 */     if ((stringVal != null) && (stringVal.length() > 0)) {
/* 1443 */       int c = Character.toLowerCase(stringVal.charAt(0));
/*      */ 
/* 1445 */       return (c == 116) || (c == 121) || (c == 49) || (stringVal.equals("-1"));
/*      */     }
/*      */ 
/* 1449 */     return false;
/*      */   }
/*      */ 
/*      */   public byte getByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1464 */     if (!this.isBinaryEncoded) {
/* 1465 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1467 */       if ((this.wasNullFlag) || (stringVal == null)) {
/* 1468 */         return 0;
/*      */       }
/*      */ 
/* 1471 */       return getByteFromString(stringVal, columnIndex);
/*      */     }
/*      */ 
/* 1474 */     return getNativeByte(columnIndex);
/*      */   }
/*      */ 
/*      */   public byte getByte(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1489 */     return getByte(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte getByteFromString(String stringVal, int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1495 */     if ((stringVal != null) && (stringVal.length() == 0)) {
/* 1496 */       return (byte)convertToZeroWithEmptyCheck();
/*      */     }
/*      */     try
/*      */     {
/* 1500 */       int decimalIndex = stringVal.indexOf(".");
/*      */ 
/* 1502 */       if (decimalIndex != -1) {
/* 1503 */         double valueAsDouble = Double.parseDouble(stringVal);
/*      */ 
/* 1505 */         if ((this.connection.getJdbcCompliantTruncation()) && (
/* 1506 */           (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */         {
/* 1508 */           throwRangeException(stringVal, columnIndex, -6);
/*      */         }
/*      */ 
/* 1513 */         return (byte)(int)valueAsDouble;
/*      */       }
/*      */ 
/* 1516 */       long valueAsLong = Long.parseLong(stringVal);
/*      */ 
/* 1518 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 1519 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 1521 */         throwRangeException(String.valueOf(valueAsLong), columnIndex, -6);
/*      */       }
/*      */ 
/* 1526 */       return (byte)(int)valueAsLong; } catch (NumberFormatException NFE) {
/*      */     }
/* 1528 */     throw new SQLException(Messages.getString("ResultSet.Value____173") + stringVal + Messages.getString("ResultSet.___is_out_of_range_[-127,127]_174"), "S1009");
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1553 */     return getBytes(columnIndex, false);
/*      */   }
/*      */ 
/*      */   protected byte[] getBytes(int columnIndex, boolean noConversion) throws SQLException
/*      */   {
/* 1558 */     if (!this.isBinaryEncoded) {
/* 1559 */       checkRowPos();
/*      */       try
/*      */       {
/* 1562 */         if (this.thisRow[(columnIndex - 1)] == null)
/* 1563 */           this.wasNullFlag = true;
/*      */         else
/* 1565 */           this.wasNullFlag = false;
/*      */       }
/*      */       catch (NullPointerException E) {
/* 1568 */         this.wasNullFlag = true;
/*      */       } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 1570 */         throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */       }
/*      */ 
/* 1577 */       if (this.wasNullFlag) {
/* 1578 */         return null;
/*      */       }
/*      */ 
/* 1581 */       return (byte[])this.thisRow[(columnIndex - 1)];
/*      */     }
/*      */ 
/* 1584 */     return getNativeBytes(columnIndex, noConversion);
/*      */   }
/*      */ 
/*      */   public byte[] getBytes(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1599 */     return getBytes(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final byte[] getBytesFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1604 */     if (stringVal != null) {
/* 1605 */       return StringUtils.getBytes(stringVal, this.connection.getEncoding(), this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */     }
/*      */ 
/* 1611 */     return null;
/*      */   }
/*      */ 
/*      */   private synchronized Calendar getCalendarInstanceForSessionOrNew()
/*      */   {
/* 1619 */     if (this.connection.getDynamicCalendars()) {
/* 1620 */       return Calendar.getInstance();
/*      */     }
/*      */ 
/* 1623 */     if (this.sessionCalendar == null) {
/* 1624 */       this.sessionCalendar = Calendar.getInstance();
/*      */     }
/*      */ 
/* 1627 */     return this.sessionCalendar;
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1647 */     if (!this.isBinaryEncoded) {
/* 1648 */       String stringVal = getString(columnIndex);
/*      */ 
/* 1650 */       if (stringVal != null) {
/* 1651 */         return new StringReader(stringVal);
/*      */       }
/*      */ 
/* 1654 */       return null;
/*      */     }
/*      */ 
/* 1657 */     return getNativeCharacterStream(columnIndex);
/*      */   }
/*      */ 
/*      */   public Reader getCharacterStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1677 */     return getCharacterStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final Reader getCharacterStreamFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1682 */     if (stringVal != null) {
/* 1683 */       return new StringReader(stringVal);
/*      */     }
/*      */ 
/* 1686 */     return null;
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(int i)
/*      */     throws SQLException
/*      */   {
/* 1701 */     if (!this.isBinaryEncoded) {
/* 1702 */       String asString = getString(i);
/*      */ 
/* 1704 */       if (asString == null) {
/* 1705 */         return null;
/*      */       }
/*      */ 
/* 1708 */       return new Clob(asString);
/*      */     }
/*      */ 
/* 1711 */     return getNativeClob(i);
/*      */   }
/*      */ 
/*      */   public java.sql.Clob getClob(String colName)
/*      */     throws SQLException
/*      */   {
/* 1726 */     return getClob(findColumn(colName));
/*      */   }
/*      */ 
/*      */   private final java.sql.Clob getClobFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 1731 */     return new Clob(stringVal);
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/* 1744 */     return 1007;
/*      */   }
/*      */ 
/*      */   public String getCursorName()
/*      */     throws SQLException
/*      */   {
/* 1773 */     throw new SQLException(Messages.getString("ResultSet.Positioned_Update_not_supported"), "S1C00");
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 1790 */     return getDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   public Date getDate(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1811 */     if (this.isBinaryEncoded) {
/* 1812 */       return getNativeDate(columnIndex, cal != null ? cal.getTimeZone() : getDefaultTimeZone());
/*      */     }
/*      */ 
/* 1816 */     String stringVal = getStringInternal(columnIndex, false);
/*      */ 
/* 1818 */     if (stringVal == null) {
/* 1819 */       return null;
/*      */     }
/*      */ 
/* 1822 */     return getDateFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName)
/*      */     throws SQLException
/*      */   {
/* 1838 */     return getDate(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Date getDate(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1858 */     return getDate(findColumn(columnName), cal); } 
/*      */   private final Date getDateFromString(String stringVal, int columnIndex) throws SQLException { // Byte code:
/*      */     //   0: iconst_0
/*      */     //   1: istore_3
/*      */     //   2: iconst_0
/*      */     //   3: istore 4
/*      */     //   5: iconst_0
/*      */     //   6: istore 5
/*      */     //   8: aload_0
/*      */     //   9: iconst_0
/*      */     //   10: putfield 33	com/mysql/jdbc/ResultSet:wasNullFlag	Z
/*      */     //   13: aload_1
/*      */     //   14: ifnonnull +10 -> 24
/*      */     //   17: aload_0
/*      */     //   18: iconst_1
/*      */     //   19: putfield 33	com/mysql/jdbc/ResultSet:wasNullFlag	Z
/*      */     //   22: aconst_null
/*      */     //   23: areturn
/*      */     //   24: aload_1
/*      */     //   25: ldc 238
/*      */     //   27: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   30: ifne +39 -> 69
/*      */     //   33: aload_1
/*      */     //   34: ldc 239
/*      */     //   36: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   39: ifne +30 -> 69
/*      */     //   42: aload_1
/*      */     //   43: ldc 240
/*      */     //   45: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   48: ifne +21 -> 69
/*      */     //   51: aload_1
/*      */     //   52: ldc 241
/*      */     //   54: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   57: ifne +12 -> 69
/*      */     //   60: aload_1
/*      */     //   61: ldc 238
/*      */     //   63: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   66: ifeq +83 -> 149
/*      */     //   69: ldc 242
/*      */     //   71: aload_0
/*      */     //   72: getfield 37	com/mysql/jdbc/ResultSet:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   75: invokevirtual 243	com/mysql/jdbc/Connection:getZeroDateTimeBehavior	()Ljava/lang/String;
/*      */     //   78: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   81: ifeq +10 -> 91
/*      */     //   84: aload_0
/*      */     //   85: iconst_1
/*      */     //   86: putfield 33	com/mysql/jdbc/ResultSet:wasNullFlag	Z
/*      */     //   89: aconst_null
/*      */     //   90: areturn
/*      */     //   91: ldc 244
/*      */     //   93: aload_0
/*      */     //   94: getfield 37	com/mysql/jdbc/ResultSet:connection	Lcom/mysql/jdbc/Connection;
/*      */     //   97: invokevirtual 243	com/mysql/jdbc/Connection:getZeroDateTimeBehavior	()Ljava/lang/String;
/*      */     //   100: invokevirtual 190	java/lang/String:equals	(Ljava/lang/Object;)Z
/*      */     //   103: ifeq +37 -> 140
/*      */     //   106: new 68	java/sql/SQLException
/*      */     //   109: dup
/*      */     //   110: new 118	java/lang/StringBuffer
/*      */     //   113: dup
/*      */     //   114: invokespecial 119	java/lang/StringBuffer:<init>	()V
/*      */     //   117: ldc 245
/*      */     //   119: invokevirtual 121	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   122: aload_1
/*      */     //   123: invokevirtual 121	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   126: ldc 246
/*      */     //   128: invokevirtual 121	java/lang/StringBuffer:append	(Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   131: invokevirtual 124	java/lang/StringBuffer:toString	()Ljava/lang/String;
/*      */     //   134: ldc 71
/*      */     //   136: invokespecial 72	java/sql/SQLException:<init>	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   139: athrow
/*      */     //   140: aload_0
/*      */     //   141: aconst_null
/*      */     //   142: iconst_1
/*      */     //   143: iconst_1
/*      */     //   144: iconst_1
/*      */     //   145: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   148: areturn
/*      */     //   149: aload_0
/*      */     //   150: getfield 36	com/mysql/jdbc/ResultSet:fields	[Lcom/mysql/jdbc/Field;
/*      */     //   153: iload_2
/*      */     //   154: iconst_1
/*      */     //   155: isub
/*      */     //   156: aaload
/*      */     //   157: invokevirtual 186	com/mysql/jdbc/Field:getMysqlType	()I
/*      */     //   160: bipush 7
/*      */     //   162: if_icmpne +367 -> 529
/*      */     //   165: aload_1
/*      */     //   166: invokevirtual 152	java/lang/String:length	()I
/*      */     //   169: tableswitch	default:+326 -> 495, 2:+292->461, 3:+326->495, 4:+246->415, 5:+326->495, 6:+187->356, 7:+326->495, 8:+141->310, 9:+326->495, 10:+187->356, 11:+326->495, 12:+187->356, 13:+326->495, 14:+141->310, 15:+326->495, 16:+326->495, 17:+326->495, 18:+326->495, 19:+95->264, 20:+326->495, 21:+95->264
/*      */     //   265: iconst_0
/*      */     //   266: iconst_4
/*      */     //   267: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   270: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   273: istore_3
/*      */     //   274: aload_1
/*      */     //   275: iconst_5
/*      */     //   276: bipush 7
/*      */     //   278: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   281: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   284: istore 4
/*      */     //   286: aload_1
/*      */     //   287: bipush 8
/*      */     //   289: bipush 10
/*      */     //   291: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   294: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   297: istore 5
/*      */     //   299: aload_0
/*      */     //   300: aconst_null
/*      */     //   301: iload_3
/*      */     //   302: iload 4
/*      */     //   304: iload 5
/*      */     //   306: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   309: areturn
/*      */     //   310: aload_1
/*      */     //   311: iconst_0
/*      */     //   312: iconst_4
/*      */     //   313: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   316: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   319: istore_3
/*      */     //   320: aload_1
/*      */     //   321: iconst_4
/*      */     //   322: bipush 6
/*      */     //   324: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   327: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   330: istore 4
/*      */     //   332: aload_1
/*      */     //   333: bipush 6
/*      */     //   335: bipush 8
/*      */     //   337: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   340: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   343: istore 5
/*      */     //   345: aload_0
/*      */     //   346: aconst_null
/*      */     //   347: iload_3
/*      */     //   348: iload 4
/*      */     //   350: iload 5
/*      */     //   352: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   355: areturn
/*      */     //   356: aload_1
/*      */     //   357: iconst_0
/*      */     //   358: iconst_2
/*      */     //   359: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   362: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   365: istore_3
/*      */     //   366: iload_3
/*      */     //   367: bipush 69
/*      */     //   369: if_icmpgt +8 -> 377
/*      */     //   372: iload_3
/*      */     //   373: bipush 100
/*      */     //   375: iadd
/*      */     //   376: istore_3
/*      */     //   377: aload_1
/*      */     //   378: iconst_2
/*      */     //   379: iconst_4
/*      */     //   380: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   383: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   386: istore 4
/*      */     //   388: aload_1
/*      */     //   389: iconst_4
/*      */     //   390: bipush 6
/*      */     //   392: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   395: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   398: istore 5
/*      */     //   400: aload_0
/*      */     //   401: aconst_null
/*      */     //   402: iload_3
/*      */     //   403: sipush 1900
/*      */     //   406: iadd
/*      */     //   407: iload 4
/*      */     //   409: iload 5
/*      */     //   411: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   414: areturn
/*      */     //   415: aload_1
/*      */     //   416: iconst_0
/*      */     //   417: iconst_4
/*      */     //   418: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   421: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   424: istore_3
/*      */     //   425: iload_3
/*      */     //   426: bipush 69
/*      */     //   428: if_icmpgt +8 -> 436
/*      */     //   431: iload_3
/*      */     //   432: bipush 100
/*      */     //   434: iadd
/*      */     //   435: istore_3
/*      */     //   436: aload_1
/*      */     //   437: iconst_2
/*      */     //   438: iconst_4
/*      */     //   439: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   442: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   445: istore 4
/*      */     //   447: aload_0
/*      */     //   448: aconst_null
/*      */     //   449: iload_3
/*      */     //   450: sipush 1900
/*      */     //   453: iadd
/*      */     //   454: iload 4
/*      */     //   456: iconst_1
/*      */     //   457: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   460: areturn
/*      */     //   461: aload_1
/*      */     //   462: iconst_0
/*      */     //   463: iconst_2
/*      */     //   464: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   467: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   470: istore_3
/*      */     //   471: iload_3
/*      */     //   472: bipush 69
/*      */     //   474: if_icmpgt +8 -> 482
/*      */     //   477: iload_3
/*      */     //   478: bipush 100
/*      */     //   480: iadd
/*      */     //   481: istore_3
/*      */     //   482: aload_0
/*      */     //   483: aconst_null
/*      */     //   484: iload_3
/*      */     //   485: sipush 1900
/*      */     //   488: iadd
/*      */     //   489: iconst_1
/*      */     //   490: iconst_1
/*      */     //   491: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   494: areturn
/*      */     //   495: new 68	java/sql/SQLException
/*      */     //   498: dup
/*      */     //   499: ldc 250
/*      */     //   501: iconst_2
/*      */     //   502: anewarray 95	java/lang/Object
/*      */     //   505: dup
/*      */     //   506: iconst_0
/*      */     //   507: aload_1
/*      */     //   508: aastore
/*      */     //   509: dup
/*      */     //   510: iconst_1
/*      */     //   511: new 85	java/lang/Integer
/*      */     //   514: dup
/*      */     //   515: iload_2
/*      */     //   516: invokespecial 86	java/lang/Integer:<init>	(I)V
/*      */     //   519: aastore
/*      */     //   520: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   523: ldc 71
/*      */     //   525: invokespecial 72	java/sql/SQLException:<init>	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   528: athrow
/*      */     //   529: aload_0
/*      */     //   530: getfield 36	com/mysql/jdbc/ResultSet:fields	[Lcom/mysql/jdbc/Field;
/*      */     //   533: iload_2
/*      */     //   534: iconst_1
/*      */     //   535: isub
/*      */     //   536: aaload
/*      */     //   537: invokevirtual 186	com/mysql/jdbc/Field:getMysqlType	()I
/*      */     //   540: bipush 13
/*      */     //   542: if_icmpne +63 -> 605
/*      */     //   545: aload_1
/*      */     //   546: invokevirtual 152	java/lang/String:length	()I
/*      */     //   549: iconst_2
/*      */     //   550: if_icmpeq +11 -> 561
/*      */     //   553: aload_1
/*      */     //   554: invokevirtual 152	java/lang/String:length	()I
/*      */     //   557: iconst_1
/*      */     //   558: if_icmpne +28 -> 586
/*      */     //   561: aload_1
/*      */     //   562: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   565: istore_3
/*      */     //   566: iload_3
/*      */     //   567: bipush 69
/*      */     //   569: if_icmpgt +8 -> 577
/*      */     //   572: iload_3
/*      */     //   573: bipush 100
/*      */     //   575: iadd
/*      */     //   576: istore_3
/*      */     //   577: iload_3
/*      */     //   578: sipush 1900
/*      */     //   581: iadd
/*      */     //   582: istore_3
/*      */     //   583: goto +13 -> 596
/*      */     //   586: aload_1
/*      */     //   587: iconst_0
/*      */     //   588: iconst_4
/*      */     //   589: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   592: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   595: istore_3
/*      */     //   596: aload_0
/*      */     //   597: aconst_null
/*      */     //   598: iload_3
/*      */     //   599: iconst_1
/*      */     //   600: iconst_1
/*      */     //   601: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   604: areturn
/*      */     //   605: aload_0
/*      */     //   606: getfield 36	com/mysql/jdbc/ResultSet:fields	[Lcom/mysql/jdbc/Field;
/*      */     //   609: iload_2
/*      */     //   610: iconst_1
/*      */     //   611: isub
/*      */     //   612: aaload
/*      */     //   613: invokevirtual 186	com/mysql/jdbc/Field:getMysqlType	()I
/*      */     //   616: bipush 11
/*      */     //   618: if_icmpne +14 -> 632
/*      */     //   621: aload_0
/*      */     //   622: aconst_null
/*      */     //   623: sipush 1970
/*      */     //   626: iconst_1
/*      */     //   627: iconst_1
/*      */     //   628: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   631: areturn
/*      */     //   632: aload_1
/*      */     //   633: invokevirtual 152	java/lang/String:length	()I
/*      */     //   636: bipush 10
/*      */     //   638: if_icmpge +37 -> 675
/*      */     //   641: new 68	java/sql/SQLException
/*      */     //   644: dup
/*      */     //   645: ldc 250
/*      */     //   647: iconst_2
/*      */     //   648: anewarray 95	java/lang/Object
/*      */     //   651: dup
/*      */     //   652: iconst_0
/*      */     //   653: aload_1
/*      */     //   654: aastore
/*      */     //   655: dup
/*      */     //   656: iconst_1
/*      */     //   657: new 85	java/lang/Integer
/*      */     //   660: dup
/*      */     //   661: iload_2
/*      */     //   662: invokespecial 86	java/lang/Integer:<init>	(I)V
/*      */     //   665: aastore
/*      */     //   666: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   669: ldc 71
/*      */     //   671: invokespecial 72	java/sql/SQLException:<init>	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   674: athrow
/*      */     //   675: aload_1
/*      */     //   676: iconst_0
/*      */     //   677: iconst_4
/*      */     //   678: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   681: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   684: istore_3
/*      */     //   685: aload_1
/*      */     //   686: iconst_5
/*      */     //   687: bipush 7
/*      */     //   689: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   692: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   695: istore 4
/*      */     //   697: aload_1
/*      */     //   698: bipush 8
/*      */     //   700: bipush 10
/*      */     //   702: invokevirtual 248	java/lang/String:substring	(II)Ljava/lang/String;
/*      */     //   705: invokestatic 249	java/lang/Integer:parseInt	(Ljava/lang/String;)I
/*      */     //   708: istore 5
/*      */     //   710: aload_0
/*      */     //   711: aconst_null
/*      */     //   712: iload_3
/*      */     //   713: iload 4
/*      */     //   715: iload 5
/*      */     //   717: invokespecial 247	com/mysql/jdbc/ResultSet:fastDateCreate	(Ljava/util/Calendar;III)Ljava/sql/Date;
/*      */     //   720: areturn
/*      */     //   721: astore 6
/*      */     //   723: aload 6
/*      */     //   725: athrow
/*      */     //   726: astore 6
/*      */     //   728: new 68	java/sql/SQLException
/*      */     //   731: dup
/*      */     //   732: ldc 250
/*      */     //   734: iconst_2
/*      */     //   735: anewarray 95	java/lang/Object
/*      */     //   738: dup
/*      */     //   739: iconst_0
/*      */     //   740: aload_1
/*      */     //   741: aastore
/*      */     //   742: dup
/*      */     //   743: iconst_1
/*      */     //   744: new 85	java/lang/Integer
/*      */     //   747: dup
/*      */     //   748: iload_2
/*      */     //   749: invokespecial 86	java/lang/Integer:<init>	(I)V
/*      */     //   752: aastore
/*      */     //   753: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   756: ldc 71
/*      */     //   758: invokespecial 72	java/sql/SQLException:<init>	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   761: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   8	23	721	java/sql/SQLException
/*      */     //   24	90	721	java/sql/SQLException
/*      */     //   91	148	721	java/sql/SQLException
/*      */     //   149	309	721	java/sql/SQLException
/*      */     //   310	355	721	java/sql/SQLException
/*      */     //   356	414	721	java/sql/SQLException
/*      */     //   415	460	721	java/sql/SQLException
/*      */     //   461	494	721	java/sql/SQLException
/*      */     //   495	604	721	java/sql/SQLException
/*      */     //   605	631	721	java/sql/SQLException
/*      */     //   632	720	721	java/sql/SQLException
/*      */     //   8	23	726	java/lang/Exception
/*      */     //   24	90	726	java/lang/Exception
/*      */     //   91	148	726	java/lang/Exception
/*      */     //   149	309	726	java/lang/Exception
/*      */     //   310	355	726	java/lang/Exception
/*      */     //   356	414	726	java/lang/Exception
/*      */     //   415	460	726	java/lang/Exception
/*      */     //   461	494	726	java/lang/Exception
/*      */     //   495	604	726	java/lang/Exception
/*      */     //   605	631	726	java/lang/Exception
/*      */     //   632	720	726	java/lang/Exception } 
/* 2001 */   private synchronized TimeZone getDefaultTimeZone() { if (this.defaultTimeZone == null) {
/* 2002 */       this.defaultTimeZone = TimeZone.getDefault();
/*      */     }
/*      */ 
/* 2005 */     return this.defaultTimeZone;
/*      */   }
/*      */ 
/*      */   public double getDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2020 */     if (!this.isBinaryEncoded) {
/* 2021 */       return getDoubleInternal(columnIndex);
/*      */     }
/*      */ 
/* 2024 */     return getNativeDouble(columnIndex);
/*      */   }
/*      */ 
/*      */   public double getDouble(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2039 */     return getDouble(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final double getDoubleFromString(String stringVal, int columnIndex) throws SQLException
/*      */   {
/* 2044 */     return getDoubleInternal(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected double getDoubleInternal(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 2060 */     return getDoubleInternal(getString(colIndex), colIndex); } 
/*      */   protected double getDoubleInternal(String stringVal, int colIndex) throws SQLException { // Byte code:
/*      */     //   0: aload_1
/*      */     //   1: ifnonnull +5 -> 6
/*      */     //   4: dconst_0
/*      */     //   5: dreturn
/*      */     //   6: aload_1
/*      */     //   7: invokevirtual 152	java/lang/String:length	()I
/*      */     //   10: ifne +9 -> 19
/*      */     //   13: aload_0
/*      */     //   14: invokespecial 154	com/mysql/jdbc/ResultSet:convertToZeroWithEmptyCheck	()I
/*      */     //   17: i2d
/*      */     //   18: dreturn
/*      */     //   19: aload_1
/*      */     //   20: invokestatic 198	java/lang/Double:parseDouble	(Ljava/lang/String;)D
/*      */     //   23: dstore_3
/*      */     //   24: aload_0
/*      */     //   25: getfield 30	com/mysql/jdbc/ResultSet:useStrictFloatingPoint	Z
/*      */     //   28: ifeq +120 -> 148
/*      */     //   31: dload_3
/*      */     //   32: ldc2_w 258
/*      */     //   35: dcmpl
/*      */     //   36: ifne +10 -> 46
/*      */     //   39: ldc2_w 260
/*      */     //   42: dstore_3
/*      */     //   43: goto +105 -> 148
/*      */     //   46: dload_3
/*      */     //   47: ldc2_w 262
/*      */     //   50: dcmpl
/*      */     //   51: ifne +10 -> 61
/*      */     //   54: ldc2_w 264
/*      */     //   57: dstore_3
/*      */     //   58: goto +90 -> 148
/*      */     //   61: dload_3
/*      */     //   62: ldc2_w 266
/*      */     //   65: dcmpl
/*      */     //   66: ifne +10 -> 76
/*      */     //   69: ldc2_w 268
/*      */     //   72: dstore_3
/*      */     //   73: goto +75 -> 148
/*      */     //   76: dload_3
/*      */     //   77: ldc2_w 270
/*      */     //   80: dcmpl
/*      */     //   81: ifne +10 -> 91
/*      */     //   84: ldc2_w 272
/*      */     //   87: dstore_3
/*      */     //   88: goto +60 -> 148
/*      */     //   91: dload_3
/*      */     //   92: ldc2_w 274
/*      */     //   95: dcmpl
/*      */     //   96: ifne +10 -> 106
/*      */     //   99: ldc2_w 272
/*      */     //   102: dstore_3
/*      */     //   103: goto +45 -> 148
/*      */     //   106: dload_3
/*      */     //   107: ldc2_w 276
/*      */     //   110: dcmpl
/*      */     //   111: ifne +10 -> 121
/*      */     //   114: ldc2_w 278
/*      */     //   117: dstore_3
/*      */     //   118: goto +30 -> 148
/*      */     //   121: dload_3
/*      */     //   122: ldc2_w 280
/*      */     //   125: dcmpl
/*      */     //   126: ifne +10 -> 136
/*      */     //   129: ldc2_w 282
/*      */     //   132: dstore_3
/*      */     //   133: goto +15 -> 148
/*      */     //   136: dload_3
/*      */     //   137: ldc2_w 284
/*      */     //   140: dcmpl
/*      */     //   141: ifne +7 -> 148
/*      */     //   144: ldc2_w 278
/*      */     //   147: dstore_3
/*      */     //   148: dload_3
/*      */     //   149: dreturn
/*      */     //   150: astore_3
/*      */     //   151: new 68	java/sql/SQLException
/*      */     //   154: dup
/*      */     //   155: ldc_w 286
/*      */     //   158: iconst_2
/*      */     //   159: anewarray 95	java/lang/Object
/*      */     //   162: dup
/*      */     //   163: iconst_0
/*      */     //   164: aload_1
/*      */     //   165: aastore
/*      */     //   166: dup
/*      */     //   167: iconst_1
/*      */     //   168: new 85	java/lang/Integer
/*      */     //   171: dup
/*      */     //   172: iload_2
/*      */     //   173: invokespecial 86	java/lang/Integer:<init>	(I)V
/*      */     //   176: aastore
/*      */     //   177: invokestatic 96	com/mysql/jdbc/Messages:getString	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
/*      */     //   180: ldc 71
/*      */     //   182: invokespecial 72	java/sql/SQLException:<init>	(Ljava/lang/String;Ljava/lang/String;)V
/*      */     //   185: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   0	5	150	java/lang/NumberFormatException
/*      */     //   6	18	150	java/lang/NumberFormatException
/*      */     //   19	149	150	java/lang/NumberFormatException } 
/* 2131 */   public int getFetchDirection() throws SQLException { return this.fetchDirection;
/*      */   }
/*      */ 
/*      */   public int getFetchSize()
/*      */     throws SQLException
/*      */   {
/* 2143 */     return this.fetchSize;
/*      */   }
/*      */ 
/*      */   protected char getFirstCharOfQuery()
/*      */   {
/* 2153 */     return this.firstCharOfQuery;
/*      */   }
/*      */ 
/*      */   public float getFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2168 */     if (!this.isBinaryEncoded) {
/* 2169 */       String val = null;
/*      */ 
/* 2171 */       val = getString(columnIndex);
/*      */ 
/* 2173 */       return getFloatFromString(val, columnIndex);
/*      */     }
/*      */ 
/* 2176 */     return getNativeFloat(columnIndex);
/*      */   }
/*      */ 
/*      */   public float getFloat(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2191 */     return getFloat(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final float getFloatFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2197 */       if (val != null) {
/* 2198 */         if (val.length() == 0) {
/* 2199 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2202 */         float f = Float.parseFloat(val);
/*      */ 
/* 2204 */         if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2205 */           (f == 1.4E-45F) || (f == 3.4028235E+38F))) {
/* 2206 */           double valAsDouble = Double.parseDouble(val);
/*      */ 
/* 2208 */           if ((valAsDouble < 1.401298464324817E-045D) || (valAsDouble > 3.402823466385289E+038D))
/*      */           {
/* 2210 */             throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2216 */         return f;
/*      */       }
/*      */ 
/* 2219 */       return 0.0F;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2222 */         double valAsDouble = Double.parseDouble(val);
/*      */ 
/* 2224 */         if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2225 */           (valAsDouble < 1.401298464324817E-045D) || (valAsDouble > 3.402823466385289E+038D)))
/*      */         {
/* 2227 */           throwRangeException(String.valueOf(valAsDouble), columnIndex, 6);
/*      */         }
/*      */ 
/* 2232 */         return (float)valAsDouble;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2237 */     throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getFloat()_-____200") + val + Messages.getString("ResultSet.___in_column__201") + columnIndex, "S1009");
/*      */   }
/*      */ 
/*      */   public int getInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2258 */     if (!this.isBinaryEncoded) {
/* 2259 */       if (this.connection.getUseFastIntParsing()) {
/* 2260 */         checkRowPos();
/*      */         try
/*      */         {
/* 2263 */           if (this.thisRow[(columnIndex - 1)] == null)
/* 2264 */             this.wasNullFlag = true;
/*      */           else
/* 2266 */             this.wasNullFlag = false;
/*      */         }
/*      */         catch (NullPointerException E) {
/* 2269 */           this.wasNullFlag = true;
/*      */         } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 2271 */           throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */         }
/*      */ 
/* 2278 */         if (this.wasNullFlag) {
/* 2279 */           return 0;
/*      */         }
/*      */ 
/* 2282 */         byte[] intAsBytes = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 2284 */         if (intAsBytes.length == 0) {
/* 2285 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2288 */         boolean needsFullParse = false;
/*      */ 
/* 2290 */         for (int i = 0; i < intAsBytes.length; i++) {
/* 2291 */           if (((char)intAsBytes[i] != 'e') && ((char)intAsBytes[i] != 'E'))
/*      */             continue;
/* 2293 */           needsFullParse = true;
/*      */ 
/* 2295 */           break;
/*      */         }
/*      */ 
/* 2299 */         if (!needsFullParse) {
/*      */           try {
/* 2301 */             return parseIntWithOverflowCheck(columnIndex, intAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */             try {
/* 2306 */               return parseIntAsDouble(columnIndex, new String(intAsBytes));
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 2312 */               throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + new String(intAsBytes) + "'", "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2322 */       String val = null;
/*      */       try
/*      */       {
/* 2325 */         val = getString(columnIndex);
/*      */ 
/* 2327 */         if (val != null) {
/* 2328 */           if (val.length() == 0) {
/* 2329 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 2332 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 2334 */             return Integer.parseInt(val);
/*      */           }
/*      */ 
/* 2338 */           return parseIntAsDouble(columnIndex, val);
/*      */         }
/*      */ 
/* 2341 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2344 */           return parseIntAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 2349 */           throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____74") + val + "'", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2357 */     return getNativeInt(columnIndex);
/*      */   }
/*      */ 
/*      */   public int getInt(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2372 */     return getInt(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final int getIntFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2378 */       if (val != null)
/*      */       {
/* 2380 */         if (val.length() == 0) {
/* 2381 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2384 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 2386 */           int valueAsInt = Integer.parseInt(val);
/*      */ 
/* 2388 */           if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2389 */             (valueAsInt == -2147483648) || (valueAsInt == 2147483647)))
/*      */           {
/* 2391 */             long valueAsLong = Long.parseLong(val);
/*      */ 
/* 2393 */             if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */             {
/* 2395 */               throwRangeException(String.valueOf(valueAsLong), columnIndex, 4);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2402 */           return valueAsInt;
/*      */         }
/*      */ 
/* 2407 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2409 */         if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2410 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2412 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2417 */         return (int)valueAsDouble;
/*      */       }
/*      */ 
/* 2420 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 2423 */         double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 2425 */         if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2426 */           (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */         {
/* 2428 */           throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */         }
/*      */ 
/* 2433 */         return (int)valueAsDouble;
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2438 */     throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getInt()_-____206") + val + Messages.getString("ResultSet.___in_column__207") + columnIndex, "S1009");
/*      */   }
/*      */ 
/*      */   public long getLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2458 */     if (!this.isBinaryEncoded) {
/* 2459 */       if (this.connection.getUseFastIntParsing()) {
/* 2460 */         checkRowPos();
/*      */         try
/*      */         {
/* 2463 */           if (this.thisRow[(columnIndex - 1)] == null)
/* 2464 */             this.wasNullFlag = true;
/*      */           else
/* 2466 */             this.wasNullFlag = false;
/*      */         }
/*      */         catch (NullPointerException E) {
/* 2469 */           this.wasNullFlag = true;
/*      */         } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 2471 */           throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */         }
/*      */ 
/* 2478 */         if (this.wasNullFlag) {
/* 2479 */           return 0L;
/*      */         }
/*      */ 
/* 2482 */         byte[] longAsBytes = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 2484 */         if (longAsBytes.length == 0) {
/* 2485 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2488 */         boolean needsFullParse = false;
/*      */ 
/* 2490 */         for (int i = 0; i < longAsBytes.length; i++) {
/* 2491 */           if (((char)longAsBytes[i] != 'e') && ((char)longAsBytes[i] != 'E'))
/*      */             continue;
/* 2493 */           needsFullParse = true;
/*      */ 
/* 2495 */           break;
/*      */         }
/*      */ 
/* 2499 */         if (!needsFullParse) {
/*      */           try {
/* 2501 */             return parseLongWithOverflowCheck(columnIndex, longAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */             try {
/* 2506 */               return parseLongAsDouble(columnIndex, new String(longAsBytes));
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 2512 */               throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + new String(longAsBytes) + "'", "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2522 */       String val = null;
/*      */       try
/*      */       {
/* 2525 */         val = getString(columnIndex);
/*      */ 
/* 2527 */         if (val != null) {
/* 2528 */           if (val.length() == 0) {
/* 2529 */             return convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 2532 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 2533 */             return parseLongWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */ 
/* 2538 */           return parseLongAsDouble(columnIndex, val);
/*      */         }
/*      */ 
/* 2541 */         return 0L;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 2544 */           return parseLongAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 2549 */           throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____79") + val + "'", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2557 */     return getNativeLong(columnIndex);
/*      */   }
/*      */ 
/*      */   public long getLong(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2572 */     return getLong(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final long getLongFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 2578 */       if (val != null)
/*      */       {
/* 2580 */         if (val.length() == 0) {
/* 2581 */           return convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 2584 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1)) {
/* 2585 */           return parseLongWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */ 
/* 2589 */         return parseLongAsDouble(columnIndex, val);
/*      */       }
/*      */ 
/* 2592 */       return 0L;
/*      */     }
/*      */     catch (NumberFormatException nfe) {
/*      */       try {
/* 2596 */         return parseLongAsDouble(columnIndex, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 2601 */     throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getLong()_-____211") + val + Messages.getString("ResultSet.___in_column__212") + columnIndex, "S1009");
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSetMetaData getMetaData()
/*      */     throws SQLException
/*      */   {
/* 2620 */     checkClosed();
/*      */ 
/* 2622 */     return new ResultSetMetaData(this.fields);
/*      */   }
/*      */ 
/*      */   protected Array getNativeArray(int i)
/*      */     throws SQLException
/*      */   {
/* 2639 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeAsciiStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2669 */     checkRowPos();
/*      */ 
/* 2671 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2689 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 2691 */     int scale = this.fields[(columnIndex - 1)].getDecimals();
/*      */ 
/* 2693 */     return getBigDecimalFromString(stringVal, columnIndex, scale);
/*      */   }
/*      */ 
/*      */   protected BigDecimal getNativeBigDecimal(int columnIndex, int scale)
/*      */     throws SQLException
/*      */   {
/* 2712 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 2715 */     if (stringVal != null) {
/* 2716 */       if (stringVal.length() == 0) {
/* 2717 */         BigDecimal val = new BigDecimal(0.0D);
/*      */         try
/*      */         {
/* 2720 */           return val.setScale(scale);
/*      */         } catch (ArithmeticException ex) {
/*      */           try {
/* 2723 */             return val.setScale(scale, 4);
/*      */           } catch (ArithmeticException arEx) {
/* 2725 */             throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____124") + stringVal + Messages.getString("ResultSet.___in_column__125") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 2739 */         val = new BigDecimal(stringVal);
/*      */       }
/*      */       catch (NumberFormatException ex)
/*      */       {
/*      */         BigDecimal val;
/* 2741 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____119") + stringVal + "' in column " + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 2752 */         return val.setScale(scale);
/*      */       }
/*      */       catch (ArithmeticException ex)
/*      */       {
/*      */         try
/*      */         {
/*      */           BigDecimal val;
/* 2755 */           return val.setScale(scale, 4);
/*      */         } catch (ArithmeticException arEx) {
/* 2757 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____124") + stringVal + Messages.getString("ResultSet.___in_column__125") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2770 */     return null;
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeBinaryStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2792 */     checkRowPos();
/*      */ 
/* 2794 */     byte[] b = getNativeBytes(columnIndex, false);
/*      */ 
/* 2796 */     if (b != null) {
/* 2797 */       return new ByteArrayInputStream(b);
/*      */     }
/*      */ 
/* 2800 */     return null;
/*      */   }
/*      */ 
/*      */   protected java.sql.Blob getNativeBlob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2815 */     checkRowPos();
/*      */ 
/* 2817 */     checkColumnBounds(columnIndex);
/*      */     try
/*      */     {
/* 2820 */       if (this.thisRow[(columnIndex - 1)] == null)
/* 2821 */         this.wasNullFlag = true;
/*      */       else
/* 2823 */         this.wasNullFlag = false;
/*      */     }
/*      */     catch (NullPointerException ex) {
/* 2826 */       this.wasNullFlag = true;
/*      */     }
/*      */ 
/* 2829 */     if (this.wasNullFlag) {
/* 2830 */       return null;
/*      */     }
/*      */ 
/* 2833 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */ 
/* 2835 */     byte[] dataAsBytes = null;
/*      */ 
/* 2837 */     switch (mysqlType) {
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 2842 */       dataAsBytes = (byte[])this.thisRow[(columnIndex - 1)];
/*      */     }
/*      */ 
/* 2845 */     dataAsBytes = getNativeBytes(columnIndex, false);
/*      */ 
/* 2848 */     if (!this.connection.getEmulateLocators()) {
/* 2849 */       return new Blob(dataAsBytes);
/*      */     }
/*      */ 
/* 2852 */     return new BlobFromLocator(this, columnIndex);
/*      */   }
/*      */ 
/*      */   protected boolean getNativeBoolean(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2867 */     int columnIndexMinusOne = columnIndex - 1;
/*      */ 
/* 2869 */     Field field = this.fields[columnIndexMinusOne];
/*      */ 
/* 2871 */     if (field.getMysqlType() == 16) {
/* 2872 */       if (this.thisRow[columnIndexMinusOne] == null) {
/* 2873 */         this.wasNullFlag = true;
/*      */ 
/* 2875 */         return false;
/*      */       }
/*      */ 
/* 2878 */       this.wasNullFlag = false;
/*      */ 
/* 2880 */       if (((byte[])this.thisRow[columnIndexMinusOne]).length == 0) {
/* 2881 */         return false;
/*      */       }
/*      */ 
/* 2884 */       byte boolVal = ((byte[])this.thisRow[columnIndexMinusOne])[0];
/*      */ 
/* 2886 */       return (boolVal == -1) || (boolVal > 0);
/*      */     }
/*      */ 
/* 2890 */     switch (field.getSQLType()) {
/*      */     case -7:
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 16:
/* 2902 */       byte boolVal = getNativeByte(columnIndex);
/*      */ 
/* 2904 */       return (boolVal == -1) || (boolVal > 0);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/* 2906 */     case 15: } if (this.useUsageAdvisor) {
/* 2907 */       issueConversionViaParsingWarning("getBoolean()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 16, 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 2919 */     String stringVal = getNativeConvertToString(columnIndex, field);
/*      */ 
/* 2921 */     return getBooleanFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected byte getNativeByte(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2937 */     checkRowPos();
/*      */ 
/* 2939 */     checkColumnBounds(columnIndex);
/*      */ 
/* 2941 */     if (this.thisRow[(columnIndex - 1)] == null) {
/* 2942 */       this.wasNullFlag = true;
/*      */ 
/* 2944 */       return 0;
/*      */     }
/*      */     try
/*      */     {
/* 2948 */       if (this.thisRow[(columnIndex - 1)] == null)
/* 2949 */         this.wasNullFlag = true;
/*      */       else
/* 2951 */         this.wasNullFlag = false;
/*      */     }
/*      */     catch (NullPointerException E) {
/* 2954 */       this.wasNullFlag = true;
/*      */     }
/*      */ 
/* 2957 */     if (this.wasNullFlag) {
/* 2958 */       return 0;
/*      */     }
/*      */ 
/* 2961 */     columnIndex--;
/*      */ 
/* 2963 */     Field field = this.fields[columnIndex];
/*      */ 
/* 2965 */     switch (field.getMysqlType()) {
/*      */     case 1:
/* 2967 */       return ((byte[])this.thisRow[columnIndex])[0];
/*      */     case 2:
/*      */     case 13:
/* 2970 */       int valueAsShort = getNativeShort(columnIndex + 1);
/*      */ 
/* 2972 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2973 */         (valueAsShort < -128) || (valueAsShort > 127)))
/*      */       {
/* 2975 */         throwRangeException(String.valueOf(valueAsShort), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 2980 */       return (byte)valueAsShort;
/*      */     case 3:
/*      */     case 9:
/* 2983 */       int valueAsInt = getNativeInt(columnIndex + 1);
/*      */ 
/* 2985 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2986 */         (valueAsInt < -128) || (valueAsInt > 127))) {
/* 2987 */         throwRangeException(String.valueOf(valueAsInt), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 2992 */       return (byte)valueAsInt;
/*      */     case 4:
/* 2995 */       float valueAsFloat = getNativeFloat(columnIndex + 1);
/*      */ 
/* 2997 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 2998 */         (valueAsFloat < -128.0F) || (valueAsFloat > 127.0F)))
/*      */       {
/* 3001 */         throwRangeException(String.valueOf(valueAsFloat), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3006 */       return (byte)(int)valueAsFloat;
/*      */     case 5:
/* 3009 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 3011 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3012 */         (valueAsDouble < -128.0D) || (valueAsDouble > 127.0D)))
/*      */       {
/* 3014 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3019 */       return (byte)(int)valueAsDouble;
/*      */     case 8:
/* 3022 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 3024 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3025 */         (valueAsLong < -128L) || (valueAsLong > 127L)))
/*      */       {
/* 3027 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, -6);
/*      */       }
/*      */ 
/* 3032 */       return (byte)(int)valueAsLong;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3035 */     case 12: } if (this.useUsageAdvisor) {
/* 3036 */       issueConversionViaParsingWarning("getByte()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3046 */     return getByteFromString(getNativeString(columnIndex + 1), columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected byte[] getNativeBytes(int columnIndex, boolean noConversion)
/*      */     throws SQLException
/*      */   {
/* 3068 */     checkRowPos();
/*      */ 
/* 3070 */     checkColumnBounds(columnIndex);
/*      */     try
/*      */     {
/* 3073 */       if (this.thisRow[(columnIndex - 1)] == null)
/* 3074 */         this.wasNullFlag = true;
/*      */       else
/* 3076 */         this.wasNullFlag = false;
/*      */     }
/*      */     catch (NullPointerException E) {
/* 3079 */       this.wasNullFlag = true;
/*      */     }
/*      */ 
/* 3082 */     if (this.wasNullFlag) {
/* 3083 */       return null;
/*      */     }
/*      */ 
/* 3086 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 3088 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 3092 */     if (noConversion) {
/* 3093 */       mysqlType = 252;
/*      */     }
/*      */ 
/* 3096 */     switch (mysqlType) {
/*      */     case 16:
/*      */     case 249:
/*      */     case 250:
/*      */     case 251:
/*      */     case 252:
/* 3102 */       return (byte[])this.thisRow[(columnIndex - 1)];
/*      */     }
/*      */ 
/* 3105 */     int sqlType = field.getSQLType();
/*      */ 
/* 3107 */     if ((sqlType == -3) || (sqlType == -2)) {
/* 3108 */       return (byte[])this.thisRow[(columnIndex - 1)];
/*      */     }
/*      */ 
/* 3111 */     return getBytesFromString(getNativeString(columnIndex), columnIndex);
/*      */   }
/*      */ 
/*      */   protected Reader getNativeCharacterStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3132 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 3134 */     return getCharacterStreamFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected java.sql.Clob getNativeClob(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3149 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 3151 */     if (stringVal == null) {
/* 3152 */       return null;
/*      */     }
/*      */ 
/* 3155 */     return getClobFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   private String getNativeConvertToString(int columnIndex, Field field)
/*      */     throws SQLException
/*      */   {
/* 3163 */     int sqlType = field.getSQLType();
/* 3164 */     int mysqlType = field.getMysqlType();
/*      */ 
/* 3166 */     switch (sqlType) {
/*      */     case -7:
/*      */     case 16:
/* 3169 */       boolean booleanVal = getNativeBoolean(columnIndex);
/*      */ 
/* 3171 */       if (this.wasNullFlag) {
/* 3172 */         return null;
/*      */       }
/*      */ 
/* 3175 */       return String.valueOf(booleanVal);
/*      */     case -6:
/* 3178 */       byte tinyintVal = getNativeByte(columnIndex);
/*      */ 
/* 3180 */       if (this.wasNullFlag) {
/* 3181 */         return null;
/*      */       }
/*      */ 
/* 3184 */       if ((!field.isUnsigned()) || (tinyintVal >= 0)) {
/* 3185 */         return String.valueOf(tinyintVal);
/*      */       }
/*      */ 
/* 3188 */       short unsignedTinyVal = (short)(tinyintVal & 0xFF);
/*      */ 
/* 3190 */       return String.valueOf(unsignedTinyVal);
/*      */     case 5:
/* 3194 */       int intVal = getNativeInt(columnIndex);
/*      */ 
/* 3196 */       if (this.wasNullFlag) {
/* 3197 */         return null;
/*      */       }
/*      */ 
/* 3200 */       if ((!field.isUnsigned()) || (intVal >= 0)) {
/* 3201 */         return String.valueOf(intVal);
/*      */       }
/*      */ 
/* 3204 */       intVal &= 65535;
/*      */ 
/* 3206 */       return String.valueOf(intVal);
/*      */     case 4:
/* 3209 */       int intVal = getNativeInt(columnIndex);
/*      */ 
/* 3211 */       if (this.wasNullFlag) {
/* 3212 */         return null;
/*      */       }
/*      */ 
/* 3215 */       if ((!field.isUnsigned()) || (intVal >= 0))
/*      */       {
/* 3217 */         return String.valueOf(intVal);
/*      */       }
/*      */ 
/* 3220 */       long longVal = intVal & 0xFFFFFFFF;
/*      */ 
/* 3222 */       return String.valueOf(longVal);
/*      */     case -5:
/* 3226 */       if (!field.isUnsigned()) {
/* 3227 */         long longVal = getNativeLong(columnIndex);
/*      */ 
/* 3229 */         if (this.wasNullFlag) {
/* 3230 */           return null;
/*      */         }
/*      */ 
/* 3233 */         return String.valueOf(longVal);
/*      */       }
/*      */ 
/* 3236 */       long longVal = getNativeLong(columnIndex);
/*      */ 
/* 3238 */       if (this.wasNullFlag) {
/* 3239 */         return null;
/*      */       }
/*      */ 
/* 3242 */       return String.valueOf(convertLongToUlong(longVal));
/*      */     case 7:
/* 3244 */       float floatVal = getNativeFloat(columnIndex);
/*      */ 
/* 3246 */       if (this.wasNullFlag) {
/* 3247 */         return null;
/*      */       }
/*      */ 
/* 3250 */       return String.valueOf(floatVal);
/*      */     case 6:
/*      */     case 8:
/* 3254 */       double doubleVal = getNativeDouble(columnIndex);
/*      */ 
/* 3256 */       if (this.wasNullFlag) {
/* 3257 */         return null;
/*      */       }
/*      */ 
/* 3260 */       return String.valueOf(doubleVal);
/*      */     case 2:
/*      */     case 3:
/* 3264 */       String stringVal = StringUtils.toAsciiString((byte[])this.thisRow[(columnIndex - 1)]);
/*      */ 
/* 3269 */       if (stringVal != null) {
/* 3270 */         if (stringVal.length() == 0) {
/* 3271 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 3273 */           return val.toString();
/*      */         }
/*      */         try
/*      */         {
/* 3277 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 3279 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____86") + stringVal + Messages.getString("ResultSet.___in_column__87") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */         BigDecimal val;
/* 3290 */         return val.toString();
/*      */       }
/*      */ 
/* 3293 */       return null;
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 3299 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 3304 */       if (!field.isBlob())
/* 3305 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/* 3306 */       if (!field.isBinary()) {
/* 3307 */         return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */       }
/* 3309 */       byte[] data = getBytes(columnIndex);
/* 3310 */       Object obj = data;
/*      */ 
/* 3312 */       if ((data != null) && (data.length >= 2)) {
/* 3313 */         if ((data[0] == -84) && (data[1] == -19)) {
/*      */           try
/*      */           {
/* 3316 */             ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 3318 */             ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 3320 */             obj = objIn.readObject();
/* 3321 */             objIn.close();
/* 3322 */             bytesIn.close();
/*      */           } catch (ClassNotFoundException cnfe) {
/* 3324 */             throw new SQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"));
/*      */           }
/*      */           catch (IOException ex)
/*      */           {
/* 3331 */             obj = data;
/*      */           }
/*      */         }
/*      */ 
/* 3335 */         return obj.toString();
/*      */       }
/*      */ 
/* 3338 */       return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */     case 91:
/* 3344 */       if (mysqlType == 13) {
/* 3345 */         short shortVal = getNativeShort(columnIndex);
/*      */ 
/* 3347 */         if (!this.connection.getYearIsDateType())
/*      */         {
/* 3349 */           if (this.wasNullFlag) {
/* 3350 */             return null;
/*      */           }
/*      */ 
/* 3353 */           return String.valueOf(shortVal);
/*      */         }
/*      */ 
/* 3356 */         if (field.getLength() == 2L)
/*      */         {
/* 3358 */           if (shortVal <= 69) {
/* 3359 */             shortVal = (short)(shortVal + 100);
/*      */           }
/*      */ 
/* 3362 */           shortVal = (short)(shortVal + 1900);
/*      */         }
/*      */ 
/* 3365 */         return fastDateCreate(null, shortVal, 1, 1).toString();
/*      */       }
/*      */ 
/* 3369 */       Date dt = getNativeDate(columnIndex);
/*      */ 
/* 3371 */       if (dt == null) {
/* 3372 */         return null;
/*      */       }
/*      */ 
/* 3375 */       return String.valueOf(dt);
/*      */     case 92:
/* 3378 */       Time tm = getNativeTime(columnIndex, this.defaultTimeZone, false);
/*      */ 
/* 3380 */       if (tm == null) {
/* 3381 */         return null;
/*      */       }
/*      */ 
/* 3384 */       return String.valueOf(tm);
/*      */     case 93:
/* 3387 */       Timestamp tstamp = getNativeTimestamp(columnIndex, this.defaultTimeZone, false);
/*      */ 
/* 3390 */       if (tstamp == null) {
/* 3391 */         return null;
/*      */       }
/*      */ 
/* 3394 */       String result = String.valueOf(tstamp);
/*      */ 
/* 3396 */       if (!this.connection.getNoDatetimeStringSync()) {
/* 3397 */         return result;
/*      */       }
/*      */ 
/* 3400 */       if (!result.endsWith(".0")) break;
/* 3401 */       return result.substring(0, result.length() - 2);
/*      */     }
/*      */ 
/* 3405 */     return extractStringFromNativeColumn(columnIndex, mysqlType);
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3421 */     return getNativeDate(columnIndex, null);
/*      */   }
/*      */ 
/*      */   protected Date getNativeDate(int columnIndex, TimeZone tz)
/*      */     throws SQLException
/*      */   {
/* 3442 */     checkRowPos();
/* 3443 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3445 */     if (this.fields[(columnIndex - 1)].getMysqlType() == 10) {
/* 3446 */       byte[] bits = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 3448 */       if (bits == null) {
/* 3449 */         this.wasNullFlag = true;
/*      */ 
/* 3451 */         return null;
/*      */       }
/*      */ 
/* 3454 */       this.wasNullFlag = false;
/*      */ 
/* 3456 */       Date dateToReturn = null;
/*      */ 
/* 3458 */       int year = 0;
/* 3459 */       int month = 0;
/* 3460 */       int day = 0;
/*      */ 
/* 3462 */       int hour = 0;
/* 3463 */       int minute = 0;
/* 3464 */       int seconds = 0;
/*      */ 
/* 3466 */       if (bits.length != 0) {
/* 3467 */         year = bits[0] & 0xFF | (bits[1] & 0xFF) << 8;
/*      */ 
/* 3469 */         month = bits[2];
/* 3470 */         day = bits[3];
/*      */       }
/*      */ 
/* 3473 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 3474 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 3476 */           this.wasNullFlag = true;
/*      */ 
/* 3478 */           return null;
/* 3479 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 3481 */           throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Date", "S1009");
/*      */         }
/*      */ 
/* 3486 */         year = 1;
/* 3487 */         month = 1;
/* 3488 */         day = 1;
/*      */       }
/*      */ 
/* 3491 */       return TimeUtil.fastDateCreate(getCalendarInstanceForSessionOrNew(), year, month, day);
/*      */     }
/*      */ 
/* 3495 */     if (this.useUsageAdvisor) {
/* 3496 */       issueConversionViaParsingWarning("getDate()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 10 });
/*      */     }
/*      */ 
/* 3501 */     String stringVal = getNativeString(columnIndex);
/*      */ 
/* 3503 */     return getDateFromString(stringVal, columnIndex);
/*      */   }
/*      */ 
/*      */   protected double getNativeDouble(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3519 */     checkRowPos();
/* 3520 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3522 */     columnIndex--;
/*      */ 
/* 3524 */     if (this.thisRow[columnIndex] == null) {
/* 3525 */       this.wasNullFlag = true;
/*      */ 
/* 3527 */       return 0.0D;
/*      */     }
/*      */ 
/* 3530 */     this.wasNullFlag = false;
/*      */ 
/* 3532 */     switch (this.fields[columnIndex].getMysqlType()) {
/*      */     case 5:
/* 3534 */       byte[] bits = (byte[])this.thisRow[columnIndex];
/*      */ 
/* 3536 */       long valueAsLong = bits[0] & 0xFF | (bits[1] & 0xFF) << 8 | (bits[2] & 0xFF) << 16 | (bits[3] & 0xFF) << 24 | (bits[4] & 0xFF) << 32 | (bits[5] & 0xFF) << 40 | (bits[6] & 0xFF) << 48 | (bits[7] & 0xFF) << 56;
/*      */ 
/* 3545 */       return Double.longBitsToDouble(valueAsLong);
/*      */     case 1:
/* 3547 */       return getNativeByte(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 3550 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 3553 */       return getNativeInt(columnIndex + 1);
/*      */     case 8:
/* 3555 */       return getNativeLong(columnIndex + 1);
/*      */     case 4:
/* 3557 */       return getNativeFloat(columnIndex + 1);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3561 */     case 12: } if (this.useUsageAdvisor) {
/* 3562 */       issueConversionViaParsingWarning("getDouble()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3572 */     String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 3574 */     return getDoubleFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected float getNativeFloat(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3590 */     checkRowPos();
/* 3591 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3593 */     columnIndex--;
/*      */ 
/* 3595 */     if (this.thisRow[columnIndex] == null) {
/* 3596 */       this.wasNullFlag = true;
/*      */ 
/* 3598 */       return 0.0F;
/*      */     }
/*      */ 
/* 3601 */     this.wasNullFlag = false;
/*      */ 
/* 3604 */     switch (this.fields[columnIndex].getMysqlType()) {
/*      */     case 5:
/* 3606 */       return (float)getNativeDouble(columnIndex + 1);
/*      */     case 1:
/* 3608 */       return getNativeByte(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 3611 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 3614 */       return getNativeInt(columnIndex + 1);
/*      */     case 8:
/* 3616 */       return (float)getNativeLong(columnIndex + 1);
/*      */     case 4:
/* 3618 */       byte[] bits = (byte[])this.thisRow[columnIndex];
/*      */ 
/* 3620 */       int asInt = bits[0] & 0xFF | (bits[1] & 0xFF) << 8 | (bits[2] & 0xFF) << 16 | (bits[3] & 0xFF) << 24;
/*      */ 
/* 3623 */       return Float.intBitsToFloat(asInt);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3627 */     case 12: } if (this.useUsageAdvisor) {
/* 3628 */       issueConversionViaParsingWarning("getFloat()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3638 */     String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 3640 */     return getFloatFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected int getNativeInt(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3656 */     checkRowPos();
/* 3657 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3659 */     columnIndex--;
/*      */ 
/* 3661 */     if (this.thisRow[columnIndex] == null) {
/* 3662 */       this.wasNullFlag = true;
/*      */ 
/* 3664 */       return 0;
/*      */     }
/*      */ 
/* 3667 */     this.wasNullFlag = false;
/*      */ 
/* 3669 */     Field f = this.fields[columnIndex];
/*      */ 
/* 3671 */     switch (f.getMysqlType())
/*      */     {
/*      */     case 1:
/* 3674 */       byte tinyintVal = getNativeByte(columnIndex + 1);
/*      */ 
/* 3676 */       if ((!f.isUnsigned()) || (tinyintVal >= 0)) {
/* 3677 */         return tinyintVal;
/*      */       }
/*      */ 
/* 3680 */       return tinyintVal + 256;
/*      */     case 2:
/* 3682 */       short asShort = getNativeShort(columnIndex + 1);
/*      */ 
/* 3684 */       if ((!f.isUnsigned()) || (asShort >= 0)) {
/* 3685 */         return asShort;
/*      */       }
/*      */ 
/* 3688 */       return asShort + 65536;
/*      */     case 13:
/* 3690 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 3693 */       byte[] bits = (byte[])this.thisRow[columnIndex];
/*      */ 
/* 3695 */       int valueAsInt = bits[0] & 0xFF | (bits[1] & 0xFF) << 8 | (bits[2] & 0xFF) << 16 | (bits[3] & 0xFF) << 24;
/*      */ 
/* 3698 */       return valueAsInt;
/*      */     case 8:
/* 3700 */       long valueAsLong = getNativeLong(columnIndex + 1);
/*      */ 
/* 3702 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3703 */         (valueAsLong < -2147483648L) || (valueAsLong > 2147483647L)))
/*      */       {
/* 3705 */         throwRangeException(String.valueOf(valueAsLong), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 3710 */       return (int)valueAsLong;
/*      */     case 5:
/* 3712 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 3714 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3715 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 3717 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 3722 */       return (int)valueAsDouble;
/*      */     case 4:
/* 3724 */       double valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 3726 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3727 */         (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */       {
/* 3729 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, 4);
/*      */       }
/*      */ 
/* 3734 */       return (int)valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3738 */     case 12: } if (this.useUsageAdvisor) {
/* 3739 */       issueConversionViaParsingWarning("getInt()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3749 */     String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 3751 */     return getIntFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected long getNativeLong(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3767 */     checkRowPos();
/* 3768 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3770 */     columnIndex--;
/*      */ 
/* 3772 */     if (this.thisRow[columnIndex] == null) {
/* 3773 */       this.wasNullFlag = true;
/*      */ 
/* 3775 */       return 0L;
/*      */     }
/*      */ 
/* 3778 */     this.wasNullFlag = false;
/*      */ 
/* 3780 */     Field f = this.fields[columnIndex];
/*      */ 
/* 3782 */     switch (f.getMysqlType()) {
/*      */     case 1:
/* 3784 */       if (!f.isUnsigned()) {
/* 3785 */         return getNativeByte(columnIndex + 1);
/*      */       }
/*      */ 
/* 3788 */       return getNativeInt(columnIndex + 1);
/*      */     case 2:
/* 3790 */       if (!f.isUnsigned()) {
/* 3791 */         return getNativeShort(columnIndex + 1);
/*      */       }
/*      */ 
/* 3794 */       return getNativeInt(columnIndex + 1);
/*      */     case 13:
/* 3797 */       return getNativeShort(columnIndex + 1);
/*      */     case 3:
/*      */     case 9:
/* 3800 */       int asInt = getNativeInt(columnIndex + 1);
/*      */ 
/* 3802 */       if ((!f.isUnsigned()) || (asInt >= 0)) {
/* 3803 */         return asInt;
/*      */       }
/*      */ 
/* 3806 */       return asInt + 4294967296L;
/*      */     case 8:
/* 3809 */       byte[] bits = (byte[])this.thisRow[columnIndex];
/*      */ 
/* 3811 */       long valueAsLong = bits[0] & 0xFF | (bits[1] & 0xFF) << 8 | (bits[2] & 0xFF) << 16 | (bits[3] & 0xFF) << 24 | (bits[4] & 0xFF) << 32 | (bits[5] & 0xFF) << 40 | (bits[6] & 0xFF) << 48 | (bits[7] & 0xFF) << 56;
/*      */ 
/* 3830 */       return valueAsLong;
/*      */     case 5:
/* 3832 */       double valueAsDouble = getNativeDouble(columnIndex + 1);
/*      */ 
/* 3834 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3835 */         (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */       {
/* 3837 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 3842 */       return ()valueAsDouble;
/*      */     case 4:
/* 3844 */       double valueAsDouble = getNativeFloat(columnIndex + 1);
/*      */ 
/* 3846 */       if ((this.connection.getJdbcCompliantTruncation()) && (
/* 3847 */         (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */       {
/* 3849 */         throwRangeException(String.valueOf(valueAsDouble), columnIndex + 1, -5);
/*      */       }
/*      */ 
/* 3854 */       return ()valueAsDouble;
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3857 */     case 12: } if (this.useUsageAdvisor) {
/* 3858 */       issueConversionViaParsingWarning("getLong()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3868 */     String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 3870 */     return getLongFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected Ref getNativeRef(int i)
/*      */     throws SQLException
/*      */   {
/* 3888 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   protected short getNativeShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3903 */     checkRowPos();
/* 3904 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3906 */     columnIndex--;
/*      */ 
/* 3908 */     if (this.thisRow[columnIndex] == null) {
/* 3909 */       this.wasNullFlag = true;
/*      */ 
/* 3911 */       return 0;
/*      */     }
/*      */ 
/* 3914 */     this.wasNullFlag = false;
/*      */ 
/* 3917 */     switch (this.fields[columnIndex].getMysqlType()) {
/*      */     case 5:
/* 3919 */       return (short)(int)getNativeDouble(columnIndex + 1);
/*      */     case 1:
/* 3921 */       return (short)getNativeByte(columnIndex + 1);
/*      */     case 2:
/*      */     case 13:
/* 3924 */       byte[] bits = (byte[])this.thisRow[columnIndex];
/*      */ 
/* 3926 */       short shortVal = (short)(bits[0] & 0xFF | (bits[1] & 0xFF) << 8);
/*      */ 
/* 3928 */       return shortVal;
/*      */     case 3:
/*      */     case 9:
/* 3931 */       return (short)getNativeInt(columnIndex + 1);
/*      */     case 8:
/* 3933 */       return (short)(int)getNativeLong(columnIndex + 1);
/*      */     case 4:
/* 3935 */       return (short)(int)getNativeFloat(columnIndex + 1);
/*      */     case 6:
/*      */     case 7:
/*      */     case 10:
/*      */     case 11:
/* 3939 */     case 12: } if (this.useUsageAdvisor) {
/* 3940 */       issueConversionViaParsingWarning("getShort()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 5, 1, 2, 3, 8, 4 });
/*      */     }
/*      */ 
/* 3950 */     String stringVal = getNativeString(columnIndex + 1);
/*      */ 
/* 3952 */     return getShortFromString(stringVal, columnIndex + 1);
/*      */   }
/*      */ 
/*      */   protected String getNativeString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 3968 */     checkRowPos();
/* 3969 */     checkColumnBounds(columnIndex);
/*      */ 
/* 3971 */     if (this.fields == null) {
/* 3972 */       throw new SQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_133"), "S1002");
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 3979 */       if (this.thisRow[(columnIndex - 1)] == null) {
/* 3980 */         this.wasNullFlag = true;
/*      */ 
/* 3982 */         return null;
/*      */       }
/*      */ 
/* 3985 */       this.wasNullFlag = false;
/*      */     } catch (NullPointerException E) {
/* 3987 */       this.wasNullFlag = true;
/*      */ 
/* 3989 */       return null;
/*      */     }
/*      */ 
/* 3992 */     String stringVal = null;
/*      */ 
/* 3994 */     if ((this.thisRow[(columnIndex - 1)] instanceof String)) {
/* 3995 */       return (String)this.thisRow[(columnIndex - 1)];
/*      */     }
/*      */ 
/* 3998 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 4001 */     stringVal = getNativeConvertToString(columnIndex, field);
/*      */ 
/* 4003 */     if ((field.isZeroFill()) && (stringVal != null)) {
/* 4004 */       int origLength = stringVal.length();
/*      */ 
/* 4006 */       StringBuffer zeroFillBuf = new StringBuffer(origLength);
/*      */ 
/* 4008 */       long numZeros = field.getLength() - origLength;
/*      */ 
/* 4010 */       for (long i = 0L; i < numZeros; i += 1L) {
/* 4011 */         zeroFillBuf.append('0');
/*      */       }
/*      */ 
/* 4014 */       zeroFillBuf.append(stringVal);
/*      */ 
/* 4016 */       stringVal = zeroFillBuf.toString();
/*      */     }
/*      */ 
/* 4019 */     return stringVal;
/*      */   }
/*      */ 
/*      */   private Time getNativeTime(int columnIndex, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 4025 */     checkRowPos();
/* 4026 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4028 */     if (this.thisRow[(columnIndex - 1)] == null) {
/* 4029 */       this.wasNullFlag = true;
/*      */ 
/* 4031 */       return null;
/*      */     }
/* 4033 */     this.wasNullFlag = false;
/*      */ 
/* 4036 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */ 
/* 4038 */     if (mysqlType == 11)
/*      */     {
/* 4040 */       byte[] bits = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 4042 */       int length = bits.length;
/* 4043 */       int hour = 0;
/* 4044 */       int minute = 0;
/* 4045 */       int seconds = 0;
/*      */ 
/* 4047 */       if (length != 0)
/*      */       {
/* 4050 */         hour = bits[5];
/* 4051 */         minute = bits[6];
/* 4052 */         seconds = bits[7];
/*      */       }
/*      */ 
/* 4055 */       Time time = TimeUtil.fastTimeCreate(getCalendarInstanceForSessionOrNew(), hour, minute, seconds);
/*      */ 
/* 4059 */       Time adjustedTime = TimeUtil.changeTimezone(this.connection, time, this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/* 4062 */       return adjustedTime;
/*      */     }
/*      */ 
/* 4065 */     if (this.useUsageAdvisor) {
/* 4066 */       issueConversionViaParsingWarning("getTime()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 11 });
/*      */     }
/*      */ 
/* 4071 */     String strTime = getNativeString(columnIndex);
/*      */ 
/* 4073 */     return getTimeFromString(strTime, columnIndex, tz, rollForward);
/*      */   }
/*      */ 
/*      */   private Timestamp getNativeTimestamp(int columnIndex, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 4078 */     checkRowPos();
/* 4079 */     checkColumnBounds(columnIndex);
/*      */ 
/* 4081 */     if (this.thisRow[(columnIndex - 1)] == null) {
/* 4082 */       this.wasNullFlag = true;
/*      */ 
/* 4084 */       return null;
/*      */     }
/*      */ 
/* 4087 */     this.wasNullFlag = false;
/*      */ 
/* 4089 */     int mysqlType = this.fields[(columnIndex - 1)].getMysqlType();
/*      */ 
/* 4091 */     switch (mysqlType) {
/*      */     case 7:
/*      */     case 12:
/* 4094 */       byte[] bits = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 4096 */       int length = bits.length;
/*      */ 
/* 4098 */       int year = 0;
/* 4099 */       int month = 0;
/* 4100 */       int day = 0;
/*      */ 
/* 4102 */       int hour = 0;
/* 4103 */       int minute = 0;
/* 4104 */       int seconds = 0;
/*      */ 
/* 4106 */       int nanos = 0;
/*      */ 
/* 4108 */       if (length != 0) {
/* 4109 */         year = bits[0] & 0xFF | (bits[1] & 0xFF) << 8;
/* 4110 */         month = bits[2];
/* 4111 */         day = bits[3];
/*      */ 
/* 4113 */         if (length > 4) {
/* 4114 */           hour = bits[4];
/* 4115 */           minute = bits[5];
/* 4116 */           seconds = bits[6];
/*      */         }
/*      */ 
/* 4119 */         if (length > 7) {
/* 4120 */           nanos = bits[7] & 0xFF | (bits[8] & 0xFF) << 8 | (bits[9] & 0xFF) << 16 | (bits[10] & 0xFF) << 24;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4126 */       if ((year == 0) && (month == 0) && (day == 0)) {
/* 4127 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4129 */           this.wasNullFlag = true;
/*      */ 
/* 4131 */           return null;
/* 4132 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 4134 */           throw new SQLException("Value '0000-00-00' can not be represented as java.sql.Timestamp", "S1009");
/*      */         }
/*      */ 
/* 4139 */         year = 1;
/* 4140 */         month = 1;
/* 4141 */         day = 1;
/*      */       }
/*      */ 
/* 4144 */       Timestamp ts = TimeUtil.fastTimestampCreate(getCalendarInstanceForSessionOrNew(), year, month, day, hour, minute, seconds, nanos);
/*      */ 
/* 4148 */       Timestamp adjustedTs = TimeUtil.changeTimezone(this.connection, ts, this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */ 
/* 4151 */       return adjustedTs;
/*      */     }
/*      */ 
/* 4155 */     if (this.useUsageAdvisor) {
/* 4156 */       issueConversionViaParsingWarning("getTimestamp()", columnIndex, this.thisRow[columnIndex], this.fields[columnIndex], new int[] { 7, 12 });
/*      */     }
/*      */ 
/* 4162 */     String strTimestamp = getNativeString(columnIndex);
/*      */ 
/* 4164 */     return getTimestampFromString(columnIndex, strTimestamp, tz, rollForward);
/*      */   }
/*      */ 
/*      */   protected InputStream getNativeUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4192 */     checkRowPos();
/*      */ 
/* 4194 */     return getBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   protected URL getNativeURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 4201 */     String val = getString(colIndex);
/*      */ 
/* 4203 */     if (val == null) {
/* 4204 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 4208 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 4210 */     throw new SQLException(Messages.getString("ResultSet.Malformed_URL____141") + val + "'", "S1009");
/*      */   }
/*      */ 
/*      */   protected ResultSet getNextResultSet()
/*      */   {
/* 4222 */     return this.nextResultSet;
/*      */   }
/*      */ 
/*      */   public Object getObject(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4249 */     checkRowPos();
/*      */     try
/*      */     {
/* 4252 */       if (this.thisRow[(columnIndex - 1)] == null) {
/* 4253 */         this.wasNullFlag = true;
/*      */ 
/* 4255 */         return null;
/*      */       }
/*      */     } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 4258 */       throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */     }
/*      */ 
/* 4265 */     this.wasNullFlag = false;
/*      */ 
/* 4268 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 4276 */     if ((this.isBinaryEncoded) && (!(this.thisRow[(columnIndex - 1)] instanceof byte[])))
/*      */     {
/* 4284 */       if ((field.getSQLType() == -7) && (field.getLength() > 0L))
/*      */       {
/* 4288 */         return new Boolean(getBoolean(columnIndex));
/*      */       }
/*      */ 
/* 4291 */       Object columnValue = this.thisRow[(columnIndex - 1)];
/*      */ 
/* 4293 */       if (columnValue == null) {
/* 4294 */         this.wasNullFlag = true;
/*      */ 
/* 4296 */         return null;
/*      */       }
/*      */ 
/* 4299 */       return columnValue;
/*      */     }
/*      */ 
/* 4302 */     switch (field.getSQLType()) {
/*      */     case -7:
/*      */     case 16:
/* 4305 */       if ((field.getMysqlType() == 16) && (field.getLength() > 0L))
/*      */       {
/* 4307 */         return getBytes(columnIndex);
/*      */       }
/*      */ 
/* 4313 */       return new Boolean(getBoolean(columnIndex));
/*      */     case -6:
/* 4316 */       if (!field.isUnsigned()) {
/* 4317 */         return new Integer(getByte(columnIndex));
/*      */       }
/*      */ 
/* 4320 */       return new Integer(getInt(columnIndex));
/*      */     case 5:
/* 4324 */       return new Integer(getInt(columnIndex));
/*      */     case 4:
/* 4328 */       if (!field.isUnsigned()) {
/* 4329 */         return new Integer(getInt(columnIndex));
/*      */       }
/*      */ 
/* 4332 */       return new Long(getLong(columnIndex));
/*      */     case -5:
/* 4336 */       if (!field.isUnsigned()) {
/* 4337 */         return new Long(getLong(columnIndex));
/*      */       }
/*      */ 
/* 4340 */       String stringVal = getString(columnIndex);
/*      */ 
/* 4342 */       if (stringVal == null) {
/* 4343 */         return null;
/*      */       }
/*      */       try
/*      */       {
/* 4347 */         return new BigInteger(stringVal);
/*      */       } catch (NumberFormatException nfe) {
/* 4349 */         throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigInteger", new Object[] { new Integer(columnIndex), stringVal }), "S1009");
/*      */       }
/*      */ 
/*      */     case 2:
/*      */     case 3:
/* 4357 */       String stringVal = getString(columnIndex);
/*      */ 
/* 4361 */       if (stringVal != null) {
/* 4362 */         if (stringVal.length() == 0) {
/* 4363 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 4365 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 4369 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 4371 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____86") + stringVal + Messages.getString("ResultSet.___in_column__87") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */         BigDecimal val;
/* 4382 */         return val;
/*      */       }
/*      */ 
/* 4385 */       return null;
/*      */     case 7:
/* 4388 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/*      */     case 8:
/* 4392 */       return new Double(getDouble(columnIndex));
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 4397 */       if (!field.isOpaqueBinary()) {
/* 4398 */         return getString(columnIndex);
/*      */       }
/*      */ 
/* 4401 */       return getBytes(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 4406 */       if (field.getMysqlType() == 255)
/* 4407 */         return getBytes(columnIndex);
/* 4408 */       if ((field.isBinary()) || (field.isBlob())) {
/* 4409 */         byte[] data = getBytes(columnIndex);
/*      */ 
/* 4411 */         if (this.connection.getAutoDeserialize()) {
/* 4412 */           Object obj = data;
/*      */ 
/* 4414 */           if ((data != null) && (data.length >= 2)) {
/* 4415 */             if ((data[0] == -84) && (data[1] == -19))
/*      */               try
/*      */               {
/* 4418 */                 ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
/*      */ 
/* 4420 */                 ObjectInputStream objIn = new ObjectInputStream(bytesIn);
/*      */ 
/* 4422 */                 obj = objIn.readObject();
/* 4423 */                 objIn.close();
/* 4424 */                 bytesIn.close();
/*      */               } catch (ClassNotFoundException cnfe) {
/* 4426 */                 throw new SQLException(Messages.getString("ResultSet.Class_not_found___91") + cnfe.toString() + Messages.getString("ResultSet._while_reading_serialized_object_92"));
/*      */               }
/*      */               catch (IOException ex)
/*      */               {
/* 4433 */                 obj = data;
/*      */               }
/*      */             else {
/* 4436 */               return getString(columnIndex);
/*      */             }
/*      */           }
/*      */ 
/* 4440 */           return obj;
/*      */         }
/*      */ 
/* 4443 */         return data;
/*      */       }
/*      */ 
/*      */     case 91:
/* 4448 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 4450 */         return new Short(getShort(columnIndex));
/*      */       }
/*      */ 
/* 4453 */       return getDate(columnIndex);
/*      */     case 92:
/* 4456 */       return getTime(columnIndex);
/*      */     case 93:
/* 4459 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 4462 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Object getObject(int i, Map map)
/*      */     throws SQLException
/*      */   {
/* 4482 */     return getObject(i);
/*      */   }
/*      */ 
/*      */   public Object getObject(String columnName)
/*      */     throws SQLException
/*      */   {
/* 4509 */     return getObject(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Object getObject(String colName, Map map)
/*      */     throws SQLException
/*      */   {
/* 4529 */     return getObject(findColumn(colName), map);
/*      */   }
/*      */ 
/*      */   protected Object getObjectStoredProc(int columnIndex, int desiredSqlType) throws SQLException
/*      */   {
/* 4534 */     checkRowPos();
/*      */     try
/*      */     {
/* 4537 */       if (this.thisRow[(columnIndex - 1)] == null) {
/* 4538 */         this.wasNullFlag = true;
/*      */ 
/* 4540 */         return null;
/*      */       }
/*      */     } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 4543 */       throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */     }
/*      */ 
/* 4550 */     this.wasNullFlag = false;
/*      */ 
/* 4553 */     Field field = this.fields[(columnIndex - 1)];
/*      */ 
/* 4555 */     switch (desiredSqlType)
/*      */     {
/*      */     case -7:
/*      */     case 16:
/* 4561 */       return new Boolean(getBoolean(columnIndex));
/*      */     case -6:
/* 4564 */       return new Integer(getInt(columnIndex));
/*      */     case 5:
/* 4567 */       return new Integer(getInt(columnIndex));
/*      */     case 4:
/* 4571 */       if (field.isUnsigned()) {
/* 4572 */         return new Long(getLong(columnIndex));
/*      */       }
/*      */ 
/* 4575 */       return new Integer(getInt(columnIndex));
/*      */     case -5:
/* 4579 */       if (field.isUnsigned()) {
/* 4580 */         return getBigDecimal(columnIndex);
/*      */       }
/*      */ 
/* 4583 */       return new Long(getLong(columnIndex));
/*      */     case 2:
/*      */     case 3:
/* 4588 */       String stringVal = getString(columnIndex);
/*      */ 
/* 4591 */       if (stringVal != null) {
/* 4592 */         if (stringVal.length() == 0) {
/* 4593 */           BigDecimal val = new BigDecimal(0.0D);
/*      */ 
/* 4595 */           return val;
/*      */         }
/*      */         try
/*      */         {
/* 4599 */           val = new BigDecimal(stringVal);
/*      */         }
/*      */         catch (NumberFormatException ex)
/*      */         {
/*      */           BigDecimal val;
/* 4601 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_BigDecimal____86") + stringVal + Messages.getString("ResultSet.___in_column__87") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */         BigDecimal val;
/* 4612 */         return val;
/*      */       }
/*      */ 
/* 4615 */       return null;
/*      */     case 7:
/* 4618 */       return new Float(getFloat(columnIndex));
/*      */     case 6:
/* 4622 */       if (!this.connection.getRunningCTS13()) {
/* 4623 */         return new Double(getFloat(columnIndex));
/*      */       }
/* 4625 */       return new Float(getFloat(columnIndex));
/*      */     case 8:
/* 4632 */       return new Double(getDouble(columnIndex));
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 4637 */       return getString(columnIndex);
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/* 4642 */       return getBytes(columnIndex);
/*      */     case 91:
/* 4645 */       if ((field.getMysqlType() == 13) && (!this.connection.getYearIsDateType()))
/*      */       {
/* 4647 */         return new Short(getShort(columnIndex));
/*      */       }
/*      */ 
/* 4650 */       return getDate(columnIndex);
/*      */     case 92:
/* 4653 */       return getTime(columnIndex);
/*      */     case 93:
/* 4656 */       return getTimestamp(columnIndex);
/*      */     }
/*      */ 
/* 4659 */     return getString(columnIndex);
/*      */   }
/*      */ 
/*      */   protected Object getObjectStoredProc(int i, Map map, int desiredSqlType)
/*      */     throws SQLException
/*      */   {
/* 4665 */     return getObjectStoredProc(i, desiredSqlType);
/*      */   }
/*      */ 
/*      */   protected Object getObjectStoredProc(String columnName, int desiredSqlType) throws SQLException
/*      */   {
/* 4670 */     return getObjectStoredProc(findColumn(columnName), desiredSqlType);
/*      */   }
/*      */ 
/*      */   protected Object getObjectStoredProc(String colName, Map map, int desiredSqlType) throws SQLException
/*      */   {
/* 4675 */     return getObjectStoredProc(findColumn(colName), map, desiredSqlType);
/*      */   }
/*      */ 
/*      */   public Ref getRef(int i)
/*      */     throws SQLException
/*      */   {
/* 4692 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public Ref getRef(String colName)
/*      */     throws SQLException
/*      */   {
/* 4709 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public int getRow()
/*      */     throws SQLException
/*      */   {
/* 4726 */     checkClosed();
/*      */ 
/* 4728 */     int currentRowNumber = this.rowData.getCurrentRowNumber();
/* 4729 */     int row = 0;
/*      */ 
/* 4733 */     if (!this.rowData.isDynamic()) {
/* 4734 */       if ((currentRowNumber < 0) || (this.rowData.isAfterLast()) || (this.rowData.isEmpty()))
/*      */       {
/* 4736 */         row = 0;
/*      */       }
/* 4738 */       else row = currentRowNumber + 1;
/*      */     }
/*      */     else
/*      */     {
/* 4742 */       row = currentRowNumber + 1;
/*      */     }
/*      */ 
/* 4745 */     return row;
/*      */   }
/*      */ 
/*      */   protected String getServerInfo()
/*      */   {
/* 4754 */     return this.serverInfo;
/*      */   }
/*      */ 
/*      */   public short getShort(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4769 */     if (!this.isBinaryEncoded) {
/* 4770 */       if (this.connection.getUseFastIntParsing()) {
/* 4771 */         checkRowPos();
/*      */         try
/*      */         {
/* 4774 */           if (this.thisRow[(columnIndex - 1)] == null)
/* 4775 */             this.wasNullFlag = true;
/*      */           else
/* 4777 */             this.wasNullFlag = false;
/*      */         }
/*      */         catch (NullPointerException E) {
/* 4780 */           this.wasNullFlag = true;
/*      */         } catch (ArrayIndexOutOfBoundsException aioobEx) {
/* 4782 */           throw new SQLException(Messages.getString("ResultSet.Column_Index_out_of_range", new Object[] { new Integer(columnIndex), new Integer(this.fields.length) }), "S1009");
/*      */         }
/*      */ 
/* 4789 */         if (this.wasNullFlag) {
/* 4790 */           return 0;
/*      */         }
/*      */ 
/* 4793 */         byte[] shortAsBytes = (byte[])this.thisRow[(columnIndex - 1)];
/*      */ 
/* 4795 */         if (shortAsBytes.length == 0) {
/* 4796 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 4799 */         boolean needsFullParse = false;
/*      */ 
/* 4801 */         for (int i = 0; i < shortAsBytes.length; i++) {
/* 4802 */           if (((char)shortAsBytes[i] != 'e') && ((char)shortAsBytes[i] != 'E'))
/*      */             continue;
/* 4804 */           needsFullParse = true;
/*      */ 
/* 4806 */           break;
/*      */         }
/*      */ 
/* 4810 */         if (!needsFullParse) {
/*      */           try {
/* 4812 */             return parseShortWithOverflowCheck(columnIndex, shortAsBytes, null);
/*      */           }
/*      */           catch (NumberFormatException nfe)
/*      */           {
/*      */             try {
/* 4817 */               return parseShortAsDouble(columnIndex, new String(shortAsBytes));
/*      */             }
/*      */             catch (NumberFormatException newNfe)
/*      */             {
/* 4823 */               throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + new String(shortAsBytes) + "'", "S1009");
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 4833 */       String val = null;
/*      */       try
/*      */       {
/* 4836 */         val = getString(columnIndex);
/*      */ 
/* 4838 */         if (val != null)
/*      */         {
/* 4840 */           if (val.length() == 0) {
/* 4841 */             return (short)convertToZeroWithEmptyCheck();
/*      */           }
/*      */ 
/* 4844 */           if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */           {
/* 4846 */             return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */           }
/*      */ 
/* 4851 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */ 
/* 4854 */         return 0;
/*      */       } catch (NumberFormatException nfe) {
/*      */         try {
/* 4857 */           return parseShortAsDouble(columnIndex, val);
/*      */         }
/*      */         catch (NumberFormatException newNfe)
/*      */         {
/* 4862 */           throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____96") + val + "'", "S1009");
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4870 */     return getNativeShort(columnIndex);
/*      */   }
/*      */ 
/*      */   public short getShort(String columnName)
/*      */     throws SQLException
/*      */   {
/* 4885 */     return getShort(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   private final short getShortFromString(String val, int columnIndex) throws SQLException
/*      */   {
/*      */     try {
/* 4891 */       if (val != null)
/*      */       {
/* 4893 */         if (val.length() == 0) {
/* 4894 */           return (short)convertToZeroWithEmptyCheck();
/*      */         }
/*      */ 
/* 4897 */         if ((val.indexOf("e") == -1) && (val.indexOf("E") == -1) && (val.indexOf(".") == -1))
/*      */         {
/* 4899 */           return parseShortWithOverflowCheck(columnIndex, null, val);
/*      */         }
/*      */ 
/* 4903 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */ 
/* 4906 */       return 0;
/*      */     } catch (NumberFormatException nfe) {
/*      */       try {
/* 4909 */         return parseShortAsDouble(columnIndex, val);
/*      */       }
/*      */       catch (NumberFormatException newNfe) {
/*      */       }
/*      */     }
/* 4914 */     throw new SQLException(Messages.getString("ResultSet.Invalid_value_for_getShort()_-____217") + val + Messages.getString("ResultSet.___in_column__218") + columnIndex, "S1009");
/*      */   }
/*      */ 
/*      */   public java.sql.Statement getStatement()
/*      */     throws SQLException
/*      */   {
/* 4933 */     if ((this.isClosed) && (!this.retainOwningStatement)) {
/* 4934 */       throw new SQLException("Operation not allowed on closed ResultSet. Statements can be retained over result set closure by setting the connection property \"retainStatementAfterResultSetClose\" to \"true\".", "S1000");
/*      */     }
/*      */ 
/* 4942 */     if (this.wrapperStatement != null) {
/* 4943 */       return this.wrapperStatement;
/*      */     }
/*      */ 
/* 4946 */     return this.owningStatement;
/*      */   }
/*      */ 
/*      */   public String getString(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 4961 */     return getStringInternal(columnIndex, true);
/*      */   }
/*      */ 
/*      */   public String getString(String columnName)
/*      */     throws SQLException
/*      */   {
/* 4977 */     return getString(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   protected String getStringInternal(int columnIndex, boolean checkDateTypes) throws SQLException
/*      */   {
/* 4982 */     if (!this.isBinaryEncoded) {
/* 4983 */       checkRowPos();
/* 4984 */       checkColumnBounds(columnIndex);
/*      */ 
/* 4986 */       if (this.fields == null) {
/* 4987 */         throw new SQLException(Messages.getString("ResultSet.Query_generated_no_fields_for_ResultSet_99"), "S1002");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 4994 */         if (this.thisRow[(columnIndex - 1)] == null) {
/* 4995 */           this.wasNullFlag = true;
/*      */ 
/* 4997 */           return null;
/*      */         }
/*      */ 
/* 5000 */         this.wasNullFlag = false;
/*      */       } catch (NullPointerException E) {
/* 5002 */         this.wasNullFlag = true;
/*      */ 
/* 5004 */         return null;
/*      */       }
/*      */ 
/* 5007 */       String stringVal = null;
/* 5008 */       columnIndex--;
/*      */ 
/* 5010 */       String encoding = this.fields[columnIndex].getCharacterSet();
/*      */ 
/* 5012 */       if ((this.connection != null) && (this.connection.getUseUnicode())) {
/*      */         try {
/* 5014 */           if (encoding == null) {
/* 5015 */             stringVal = new String((byte[])this.thisRow[columnIndex]);
/*      */           }
/*      */           else {
/* 5018 */             SingleByteCharsetConverter converter = this.connection.getCharsetConverter(encoding);
/*      */ 
/* 5021 */             if (converter != null) {
/* 5022 */               stringVal = converter.toString((byte[])this.thisRow[columnIndex]);
/*      */             }
/*      */             else {
/* 5025 */               stringVal = new String((byte[])this.thisRow[columnIndex], encoding);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (UnsupportedEncodingException E)
/*      */         {
/* 5031 */           throw new SQLException(Messages.getString("ResultSet.Unsupported_character_encoding____101") + encoding + "'.", "0S100");
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 5037 */         stringVal = StringUtils.toAsciiString((byte[])this.thisRow[columnIndex]);
/*      */       }
/*      */ 
/* 5046 */       if (this.fields[columnIndex].getMysqlType() == 13) {
/* 5047 */         if (!this.connection.getYearIsDateType()) {
/* 5048 */           return stringVal;
/*      */         }
/*      */ 
/* 5051 */         Date dt = getDateFromString(stringVal, columnIndex + 1);
/*      */ 
/* 5053 */         if (dt == null) {
/* 5054 */           this.wasNullFlag = true;
/*      */ 
/* 5056 */           return null;
/*      */         }
/*      */ 
/* 5059 */         this.wasNullFlag = false;
/*      */ 
/* 5061 */         return dt.toString();
/*      */       }
/*      */ 
/* 5066 */       if ((checkDateTypes) && (!this.connection.getNoDatetimeStringSync())) {
/* 5067 */         switch (this.fields[columnIndex].getSQLType()) {
/*      */         case 92:
/* 5069 */           Time tm = getTimeFromString(stringVal, columnIndex + 1, getDefaultTimeZone(), false);
/*      */ 
/* 5072 */           if (tm == null) {
/* 5073 */             this.wasNullFlag = true;
/*      */ 
/* 5075 */             return null;
/*      */           }
/*      */ 
/* 5078 */           this.wasNullFlag = false;
/*      */ 
/* 5080 */           return tm.toString();
/*      */         case 91:
/* 5083 */           Date dt = getDateFromString(stringVal, columnIndex + 1);
/*      */ 
/* 5085 */           if (dt == null) {
/* 5086 */             this.wasNullFlag = true;
/*      */ 
/* 5088 */             return null;
/*      */           }
/*      */ 
/* 5091 */           this.wasNullFlag = false;
/*      */ 
/* 5093 */           return dt.toString();
/*      */         case 93:
/* 5095 */           Timestamp ts = getTimestampFromString(columnIndex + 1, stringVal, getDefaultTimeZone(), false);
/*      */ 
/* 5098 */           if (ts == null) {
/* 5099 */             this.wasNullFlag = true;
/*      */ 
/* 5101 */             return null;
/*      */           }
/*      */ 
/* 5104 */           this.wasNullFlag = false;
/*      */ 
/* 5106 */           return ts.toString();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 5112 */       return stringVal;
/*      */     }
/*      */ 
/* 5115 */     return getNativeString(columnIndex);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5130 */     return getTimeInternal(columnIndex, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Time getTime(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5150 */     return getTimeInternal(columnIndex, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5165 */     return getTime(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Time getTime(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5185 */     return getTime(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   private Time getTimeFromString(String timeAsString, int columnIndex, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/* 5190 */     int hr = 0;
/* 5191 */     int min = 0;
/* 5192 */     int sec = 0;
/*      */     try
/*      */     {
/* 5195 */       if (timeAsString == null) {
/* 5196 */         this.wasNullFlag = true;
/*      */ 
/* 5198 */         return null;
/* 5199 */       }if ((timeAsString.equals("0")) || (timeAsString.equals("0000-00-00")) || (timeAsString.equals("0000-00-00 00:00:00")) || (timeAsString.equals("00000000000000")))
/*      */       {
/* 5203 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5205 */           this.wasNullFlag = true;
/*      */ 
/* 5207 */           return null;
/* 5208 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5210 */           throw new SQLException("Value '" + timeAsString + " can not be represented as java.sql.Time", "S1009");
/*      */         }
/*      */ 
/* 5217 */         return fastTimeCreate(null, 0, 0, 0);
/*      */       }
/*      */ 
/* 5220 */       this.wasNullFlag = false;
/*      */ 
/* 5222 */       Field timeColField = this.fields[(columnIndex - 1)];
/*      */ 
/* 5224 */       if (timeColField.getMysqlType() == 7)
/*      */       {
/* 5226 */         int length = timeAsString.length();
/*      */ 
/* 5228 */         switch (length) {
/*      */         case 12:
/*      */         case 14:
/* 5231 */           hr = Integer.parseInt(timeAsString.substring(length - 6, length - 4));
/*      */ 
/* 5233 */           min = Integer.parseInt(timeAsString.substring(length - 4, length - 2));
/*      */ 
/* 5235 */           sec = Integer.parseInt(timeAsString.substring(length - 2, length));
/*      */ 
/* 5239 */           break;
/*      */         case 10:
/* 5242 */           hr = Integer.parseInt(timeAsString.substring(6, 8));
/* 5243 */           min = Integer.parseInt(timeAsString.substring(8, 10));
/* 5244 */           sec = 0;
/*      */ 
/* 5247 */           break;
/*      */         case 11:
/*      */         case 13:
/*      */         default:
/* 5250 */           throw new SQLException(Messages.getString("ResultSet.Timestamp_too_small_to_convert_to_Time_value_in_column__257") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").", "S1009");
/*      */         }
/*      */ 
/* 5259 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_TIMESTAMP_to_Time_with_getTime()_on_column__261") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 5266 */         if (this.warningChain == null)
/* 5267 */           this.warningChain = precisionLost;
/*      */         else
/* 5269 */           this.warningChain.setNextWarning(precisionLost);
/*      */       }
/* 5271 */       else if (timeColField.getMysqlType() == 12) {
/* 5272 */         hr = Integer.parseInt(timeAsString.substring(11, 13));
/* 5273 */         min = Integer.parseInt(timeAsString.substring(14, 16));
/* 5274 */         sec = Integer.parseInt(timeAsString.substring(17, 19));
/*      */ 
/* 5276 */         SQLWarning precisionLost = new SQLWarning(Messages.getString("ResultSet.Precision_lost_converting_DATETIME_to_Time_with_getTime()_on_column__264") + columnIndex + "(" + this.fields[(columnIndex - 1)] + ").");
/*      */ 
/* 5283 */         if (this.warningChain == null)
/* 5284 */           this.warningChain = precisionLost;
/*      */         else
/* 5286 */           this.warningChain.setNextWarning(precisionLost);
/*      */       } else {
/* 5288 */         if (timeColField.getMysqlType() == 10) {
/* 5289 */           return fastTimeCreate(null, 0, 0, 0);
/*      */         }
/*      */ 
/* 5293 */         if ((timeAsString.length() != 5) && (timeAsString.length() != 8))
/*      */         {
/* 5295 */           throw new SQLException(Messages.getString("ResultSet.Bad_format_for_Time____267") + timeAsString + Messages.getString("ResultSet.___in_column__268") + columnIndex, "S1009");
/*      */         }
/*      */ 
/* 5302 */         hr = Integer.parseInt(timeAsString.substring(0, 2));
/* 5303 */         min = Integer.parseInt(timeAsString.substring(3, 5));
/* 5304 */         sec = timeAsString.length() == 5 ? 0 : Integer.parseInt(timeAsString.substring(6));
/*      */       }
/*      */ 
/* 5308 */       return TimeUtil.changeTimezone(this.connection, fastTimeCreate(null, hr, min, sec), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */     }
/*      */     catch (Exception ex) {
/*      */     }
/* 5312 */     throw new SQLException(ex.toString(), "S1009");
/*      */   }
/*      */ 
/*      */   private Time getTimeInternal(int columnIndex, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 5333 */     if (this.isBinaryEncoded) {
/* 5334 */       return getNativeTime(columnIndex, tz, rollForward);
/*      */     }
/*      */ 
/* 5337 */     String timeAsString = getStringInternal(columnIndex, false);
/*      */ 
/* 5339 */     return getTimeFromString(timeAsString, columnIndex, tz, rollForward);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5355 */     return getTimestampInternal(columnIndex, getDefaultTimeZone(), false);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(int columnIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5377 */     return getTimestampInternal(columnIndex, cal.getTimeZone(), true);
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5393 */     return getTimestamp(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public Timestamp getTimestamp(String columnName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 5414 */     return getTimestamp(findColumn(columnName), cal);
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampFromString(int columnIndex, String timestampValue, TimeZone tz, boolean rollForward) throws SQLException
/*      */   {
/*      */     try
/*      */     {
/* 5421 */       this.wasNullFlag = false;
/*      */ 
/* 5423 */       if (timestampValue == null) {
/* 5424 */         this.wasNullFlag = true;
/*      */ 
/* 5426 */         return null;
/*      */       }
/*      */ 
/* 5429 */       int length = timestampValue.length();
/*      */ 
/* 5431 */       if ((length > 0) && (timestampValue.charAt(0) == '0') && ((timestampValue.equals("0000-00-00")) || (timestampValue.equals("0000-00-00 00:00:00")) || (timestampValue.equals("00000000000000")) || (timestampValue.equals("0"))))
/*      */       {
/* 5438 */         if ("convertToNull".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5440 */           this.wasNullFlag = true;
/*      */ 
/* 5442 */           return null;
/* 5443 */         }if ("exception".equals(this.connection.getZeroDateTimeBehavior()))
/*      */         {
/* 5445 */           throw new SQLException("Value '" + timestampValue + " can not be represented as java.sql.Timestamp", "S1009");
/*      */         }
/*      */ 
/* 5452 */         return fastTimestampCreate(null, 1, 1, 1, 0, 0, 0, 0);
/*      */       }
/* 5454 */       if (this.fields[(columnIndex - 1)].getMysqlType() == 13) {
/* 5455 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, Integer.parseInt(timestampValue.substring(0, 4)), 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       }
/*      */ 
/* 5461 */       if (timestampValue.endsWith(".")) {
/* 5462 */         timestampValue = timestampValue.substring(0, timestampValue.length() - 1);
/*      */       }
/*      */ 
/* 5467 */       switch (length) {
/*      */       case 19:
/*      */       case 20:
/*      */       case 21:
/*      */       case 22:
/*      */       case 23:
/*      */       case 24:
/*      */       case 25:
/*      */       case 26:
/* 5476 */         int year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5477 */         int month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 5479 */         int day = Integer.parseInt(timestampValue.substring(8, 10));
/* 5480 */         int hour = Integer.parseInt(timestampValue.substring(11, 13));
/*      */ 
/* 5482 */         int minutes = Integer.parseInt(timestampValue.substring(14, 16));
/*      */ 
/* 5484 */         int seconds = Integer.parseInt(timestampValue.substring(17, 19));
/*      */ 
/* 5487 */         int nanos = 0;
/*      */ 
/* 5489 */         if (length > 19) {
/* 5490 */           int decimalIndex = timestampValue.lastIndexOf('.');
/*      */ 
/* 5492 */           if (decimalIndex != -1) {
/* 5493 */             if (decimalIndex + 2 <= timestampValue.length()) {
/* 5494 */               nanos = Integer.parseInt(timestampValue.substring(decimalIndex + 1));
/*      */             }
/*      */             else {
/* 5497 */               throw new IllegalArgumentException();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 5507 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year, month, day, hour, minutes, seconds, nanos), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 14:
/* 5514 */         int year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5515 */         int month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 5517 */         int day = Integer.parseInt(timestampValue.substring(6, 8));
/* 5518 */         int hour = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 5520 */         int minutes = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 5522 */         int seconds = Integer.parseInt(timestampValue.substring(12, 14));
/*      */ 
/* 5525 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year, month, day, hour, minutes, seconds, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 12:
/* 5532 */         int year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5534 */         if (year <= 69) {
/* 5535 */           year += 100;
/*      */         }
/*      */ 
/* 5538 */         int month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 5540 */         int day = Integer.parseInt(timestampValue.substring(4, 6));
/* 5541 */         int hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 5542 */         int minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 5544 */         int seconds = Integer.parseInt(timestampValue.substring(10, 12));
/*      */ 
/* 5547 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year + 1900, month, day, hour, minutes, seconds, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 10:
/*      */         int minutes;
/*      */         int year;
/*      */         int month;
/*      */         int day;
/*      */         int hour;
/*      */         int minutes;
/* 5560 */         if ((this.fields[(columnIndex - 1)].getMysqlType() == 10) || (timestampValue.indexOf("-") != -1))
/*      */         {
/* 5562 */           int year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5563 */           int month = Integer.parseInt(timestampValue.substring(5, 7));
/*      */ 
/* 5565 */           int day = Integer.parseInt(timestampValue.substring(8, 10));
/* 5566 */           int hour = 0;
/* 5567 */           minutes = 0;
/*      */         } else {
/* 5569 */           year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5571 */           if (year <= 69) {
/* 5572 */             year += 100;
/*      */           }
/*      */ 
/* 5575 */           month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 5577 */           day = Integer.parseInt(timestampValue.substring(4, 6));
/* 5578 */           hour = Integer.parseInt(timestampValue.substring(6, 8));
/* 5579 */           minutes = Integer.parseInt(timestampValue.substring(8, 10));
/*      */ 
/* 5582 */           year += 1900;
/*      */         }
/*      */ 
/* 5585 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year, month, day, hour, minutes, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 8:
/* 5592 */         if (timestampValue.indexOf(":") != -1) {
/* 5593 */           int hour = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5595 */           int minutes = Integer.parseInt(timestampValue.substring(3, 5));
/*      */ 
/* 5597 */           int seconds = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 5600 */           return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, 70, 0, 1, hour, minutes, seconds, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */         }
/*      */ 
/* 5609 */         int year = Integer.parseInt(timestampValue.substring(0, 4));
/* 5610 */         int month = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 5612 */         int day = Integer.parseInt(timestampValue.substring(6, 8));
/*      */ 
/* 5614 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year - 1900, month - 1, day, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 6:
/* 5621 */         int year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5623 */         if (year <= 69) {
/* 5624 */           year += 100;
/*      */         }
/*      */ 
/* 5627 */         int month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 5629 */         int day = Integer.parseInt(timestampValue.substring(4, 6));
/*      */ 
/* 5631 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year + 1900, month, day, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 4:
/* 5638 */         int year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5640 */         if (year <= 69) {
/* 5641 */           year += 100;
/*      */         }
/*      */ 
/* 5644 */         int month = Integer.parseInt(timestampValue.substring(2, 4));
/*      */ 
/* 5647 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year + 1900, month, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 2:
/* 5654 */         int year = Integer.parseInt(timestampValue.substring(0, 2));
/*      */ 
/* 5656 */         if (year <= 69) {
/* 5657 */           year += 100;
/*      */         }
/*      */ 
/* 5660 */         return TimeUtil.changeTimezone(this.connection, fastTimestampCreate(null, year + 1900, 1, 1, 0, 0, 0, 0), this.connection.getServerTimezoneTZ(), tz, rollForward);
/*      */       case 3:
/*      */       case 5:
/*      */       case 7:
/*      */       case 9:
/*      */       case 11:
/*      */       case 13:
/*      */       case 15:
/*      */       case 16:
/*      */       case 17:
/* 5667 */       case 18: } throw new SQLException("Bad format for Timestamp '" + timestampValue + "' in column " + columnIndex + ".", "S1009");
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*      */     }
/*      */ 
/* 5674 */     throw new SQLException("Cannot convert value '" + timestampValue + "' from column " + columnIndex + " to TIMESTAMP.", "S1009");
/*      */   }
/*      */ 
/*      */   private Timestamp getTimestampInternal(int columnIndex, TimeZone tz, boolean rollForward)
/*      */     throws SQLException
/*      */   {
/* 5696 */     if (this.isBinaryEncoded) {
/* 5697 */       return getNativeTimestamp(columnIndex, tz, rollForward);
/*      */     }
/*      */ 
/* 5700 */     String timestampValue = getStringInternal(columnIndex, false);
/*      */ 
/* 5702 */     return getTimestampFromString(columnIndex, timestampValue, tz, rollForward);
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */     throws SQLException
/*      */   {
/* 5717 */     return this.resultSetType;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 5739 */     if (!this.isBinaryEncoded) {
/* 5740 */       checkRowPos();
/*      */ 
/* 5742 */       return getBinaryStream(columnIndex);
/*      */     }
/*      */ 
/* 5745 */     return getNativeBinaryStream(columnIndex);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public InputStream getUnicodeStream(String columnName)
/*      */     throws SQLException
/*      */   {
/* 5762 */     return getUnicodeStream(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   long getUpdateCount() {
/* 5766 */     return this.updateCount;
/*      */   }
/*      */ 
/*      */   long getUpdateID() {
/* 5770 */     return this.updateId;
/*      */   }
/*      */ 
/*      */   public URL getURL(int colIndex)
/*      */     throws SQLException
/*      */   {
/* 5777 */     String val = getString(colIndex);
/*      */ 
/* 5779 */     if (val == null) {
/* 5780 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 5784 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 5786 */     throw new SQLException(Messages.getString("ResultSet.Malformed_URL____104") + val + "'", "S1009");
/*      */   }
/*      */ 
/*      */   public URL getURL(String colName)
/*      */     throws SQLException
/*      */   {
/* 5796 */     String val = getString(colName);
/*      */ 
/* 5798 */     if (val == null) {
/* 5799 */       return null;
/*      */     }
/*      */     try
/*      */     {
/* 5803 */       return new URL(val); } catch (MalformedURLException mfe) {
/*      */     }
/* 5805 */     throw new SQLException(Messages.getString("ResultSet.Malformed_URL____107") + val + "'", "S1009");
/*      */   }
/*      */ 
/*      */   public SQLWarning getWarnings()
/*      */     throws SQLException
/*      */   {
/* 5832 */     return this.warningChain;
/*      */   }
/*      */ 
/*      */   public void insertRow()
/*      */     throws SQLException
/*      */   {
/* 5847 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/* 5864 */     checkClosed();
/*      */ 
/* 5866 */     boolean b = this.rowData.isAfterLast();
/*      */ 
/* 5868 */     return b;
/*      */   }
/*      */ 
/*      */   public boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/* 5885 */     checkClosed();
/*      */ 
/* 5887 */     return this.rowData.isBeforeFirst();
/*      */   }
/*      */ 
/*      */   public boolean isFirst()
/*      */     throws SQLException
/*      */   {
/* 5903 */     checkClosed();
/*      */ 
/* 5905 */     return this.rowData.isFirst();
/*      */   }
/*      */ 
/*      */   public boolean isLast()
/*      */     throws SQLException
/*      */   {
/* 5924 */     checkClosed();
/*      */ 
/* 5926 */     return this.rowData.isLast();
/*      */   }
/*      */ 
/*      */   private void issueConversionViaParsingWarning(String methodName, int columnIndex, Object value, Field fieldInfo, int[] typesWithNoParseConversion)
/*      */     throws SQLException
/*      */   {
/* 5937 */     StringBuffer message = new StringBuffer();
/* 5938 */     message.append("ResultSet type conversion via parsing detected when calling ");
/*      */ 
/* 5940 */     message.append(methodName);
/* 5941 */     message.append(" for column ");
/* 5942 */     message.append(columnIndex + 1);
/* 5943 */     message.append(", (column named '");
/* 5944 */     message.append(fieldInfo.getOriginalName());
/* 5945 */     message.append("' in table '");
/* 5946 */     message.append(fieldInfo.getOriginalTableName());
/* 5947 */     if ((this.owningStatement != null) && ((this.owningStatement instanceof PreparedStatement)))
/*      */     {
/* 5949 */       message.append("' created from query:\n\n");
/* 5950 */       message.append(((PreparedStatement)this.owningStatement).originalSql);
/*      */ 
/* 5952 */       message.append("\n\n");
/*      */     } else {
/* 5954 */       message.append(". ");
/*      */     }
/*      */ 
/* 5957 */     message.append("Java of column type is '");
/* 5958 */     message.append(value.getClass().getName());
/* 5959 */     message.append("', MySQL field type is ");
/* 5960 */     message.append(MysqlDefs.typeToName(fieldInfo.getMysqlType()));
/* 5961 */     message.append(".\n\nTypes that could be converted directly without parsing are:\n");
/*      */ 
/* 5964 */     for (int i = 0; i < typesWithNoParseConversion.length; i++) {
/* 5965 */       message.append(MysqlDefs.typeToName(typesWithNoParseConversion[i]));
/* 5966 */       message.append("\n");
/*      */     }
/*      */ 
/* 5969 */     this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connection.getId(), this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message.toString()));
/*      */   }
/*      */ 
/*      */   private void issueDataTruncationWarningIfConfigured(int columnIndex, int readSize, int truncatedToSize)
/*      */   {
/* 5981 */     DataTruncation dt = new DataTruncation(columnIndex, false, true, readSize, truncatedToSize);
/*      */   }
/*      */ 
/*      */   public boolean last()
/*      */     throws SQLException
/*      */   {
/* 5999 */     checkClosed();
/*      */ 
/* 6001 */     if (this.rowData.size() == 0) {
/* 6002 */       return false;
/*      */     }
/*      */ 
/* 6005 */     if (this.onInsertRow) {
/* 6006 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/* 6009 */     if (this.doingUpdates) {
/* 6010 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 6013 */     this.rowData.beforeLast();
/* 6014 */     this.thisRow = this.rowData.next();
/*      */ 
/* 6016 */     return true;
/*      */   }
/*      */ 
/*      */   public void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/* 6030 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/* 6059 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean next()
/*      */     throws SQLException
/*      */   {
/* 6078 */     checkClosed();
/*      */ 
/* 6080 */     if (this.onInsertRow) {
/* 6081 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/* 6084 */     if (this.doingUpdates) {
/* 6085 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 6090 */     if (!reallyResult())
/* 6091 */       throw new SQLException(Messages.getString("ResultSet.ResultSet_is_from_UPDATE._No_Data_115"), "S1000");
/*      */     boolean b;
/*      */     boolean b;
/* 6097 */     if (this.rowData.size() == 0) {
/* 6098 */       b = false;
/*      */     }
/*      */     else
/*      */     {
/*      */       boolean b;
/* 6100 */       if (!this.rowData.hasNext())
/*      */       {
/* 6102 */         this.rowData.next();
/* 6103 */         b = false;
/*      */       } else {
/* 6105 */         clearWarnings();
/* 6106 */         this.thisRow = this.rowData.next();
/* 6107 */         b = true;
/*      */       }
/*      */     }
/*      */ 
/* 6111 */     return b;
/*      */   }
/*      */ 
/*      */   private int parseIntAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
/*      */   {
/* 6116 */     if (val == null) {
/* 6117 */       return 0;
/*      */     }
/*      */ 
/* 6120 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 6122 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6123 */       (valueAsDouble < -2147483648.0D) || (valueAsDouble > 2147483647.0D)))
/*      */     {
/* 6125 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 4);
/*      */     }
/*      */ 
/* 6130 */     return (int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private int parseIntWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 6136 */     int intValue = 0;
/*      */ 
/* 6138 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 6139 */       return 0;
/*      */     }
/*      */ 
/* 6142 */     if (valueAsBytes != null)
/* 6143 */       intValue = StringUtils.getInt(valueAsBytes);
/*      */     else {
/* 6145 */       intValue = Integer.parseInt(valueAsString);
/*      */     }
/*      */ 
/* 6148 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6149 */       (intValue == -2147483648) || (intValue == 2147483647))) {
/* 6150 */       long valueAsLong = Long.parseLong(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 6154 */       if ((valueAsLong < -2147483648L) || (valueAsLong > 2147483647L))
/*      */       {
/* 6156 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndex, 4);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6163 */     return intValue;
/*      */   }
/*      */ 
/*      */   private long parseLongAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
/*      */   {
/* 6168 */     if (val == null) {
/* 6169 */       return 0L;
/*      */     }
/*      */ 
/* 6172 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 6174 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6175 */       (valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D)))
/*      */     {
/* 6177 */       throwRangeException(val, columnIndex, -5);
/*      */     }
/*      */ 
/* 6181 */     return ()valueAsDouble;
/*      */   }
/*      */ 
/*      */   private long parseLongWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 6188 */     long longValue = 0L;
/*      */ 
/* 6190 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 6191 */       return 0L;
/*      */     }
/*      */ 
/* 6194 */     if (valueAsBytes != null)
/* 6195 */       longValue = StringUtils.getLong(valueAsBytes);
/*      */     else {
/* 6197 */       longValue = Long.parseLong(valueAsString);
/*      */     }
/*      */ 
/* 6200 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6201 */       (longValue == -2147483648L) || (longValue == 2147483647L)))
/*      */     {
/* 6203 */       double valueAsDouble = Double.parseDouble(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 6207 */       if ((valueAsDouble < -9.223372036854776E+018D) || (valueAsDouble > 9.223372036854776E+018D))
/*      */       {
/* 6209 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndex, -5);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6216 */     return longValue;
/*      */   }
/*      */ 
/*      */   private short parseShortAsDouble(int columnIndex, String val) throws NumberFormatException, SQLException
/*      */   {
/* 6221 */     if (val == null) {
/* 6222 */       return 0;
/*      */     }
/*      */ 
/* 6225 */     double valueAsDouble = Double.parseDouble(val);
/*      */ 
/* 6227 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6228 */       (valueAsDouble < -32768.0D) || (valueAsDouble > 32767.0D)))
/*      */     {
/* 6230 */       throwRangeException(String.valueOf(valueAsDouble), columnIndex, 5);
/*      */     }
/*      */ 
/* 6235 */     return (short)(int)valueAsDouble;
/*      */   }
/*      */ 
/*      */   private short parseShortWithOverflowCheck(int columnIndex, byte[] valueAsBytes, String valueAsString)
/*      */     throws NumberFormatException, SQLException
/*      */   {
/* 6242 */     short shortValue = 0;
/*      */ 
/* 6244 */     if ((valueAsBytes == null) && (valueAsString == null)) {
/* 6245 */       return 0;
/*      */     }
/*      */ 
/* 6248 */     if (valueAsBytes != null)
/* 6249 */       shortValue = StringUtils.getShort(valueAsBytes);
/*      */     else {
/* 6251 */       shortValue = Short.parseShort(valueAsString);
/*      */     }
/*      */ 
/* 6254 */     if ((this.connection.getJdbcCompliantTruncation()) && (
/* 6255 */       (shortValue == -32768) || (shortValue == 32767))) {
/* 6256 */       long valueAsLong = Long.parseLong(valueAsString == null ? new String(valueAsBytes) : valueAsString);
/*      */ 
/* 6260 */       if ((valueAsLong < -32768L) || (valueAsLong > 32767L))
/*      */       {
/* 6262 */         throwRangeException(valueAsString == null ? new String(valueAsBytes) : valueAsString, columnIndex, 5);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 6269 */     return shortValue;
/*      */   }
/*      */ 
/*      */   public boolean prev()
/*      */     throws SQLException
/*      */   {
/* 6288 */     checkClosed();
/*      */ 
/* 6290 */     int rowIndex = this.rowData.getCurrentRowNumber();
/*      */ 
/* 6292 */     if (rowIndex - 1 >= 0) {
/* 6293 */       rowIndex--;
/* 6294 */       this.rowData.setCurrentRow(rowIndex);
/* 6295 */       this.thisRow = this.rowData.getAt(rowIndex);
/*      */ 
/* 6297 */       return true;
/* 6298 */     }if (rowIndex - 1 == -1) {
/* 6299 */       rowIndex--;
/* 6300 */       this.rowData.setCurrentRow(rowIndex);
/* 6301 */       this.thisRow = null;
/*      */ 
/* 6303 */       return false;
/*      */     }
/* 6305 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean previous()
/*      */     throws SQLException
/*      */   {
/* 6333 */     if (this.onInsertRow) {
/* 6334 */       this.onInsertRow = false;
/*      */     }
/*      */ 
/* 6337 */     if (this.doingUpdates) {
/* 6338 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 6341 */     return prev();
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 6354 */     if (this.isClosed) {
/* 6355 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 6359 */       if (this.useUsageAdvisor) {
/* 6360 */         if (!calledExplicitly) {
/* 6361 */           String message = Messages.getString("ResultSet.ResultSet_implicitly_closed_by_driver._150") + Messages.getString("ResultSet._n_nYou_should_close_ResultSets_explicitly_from_your_code_to_free_up_resources_in_a_more_efficient_manner._151");
/*      */ 
/* 6366 */           this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connection.getId(), this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */         }
/*      */ 
/* 6377 */         if (((this.rowData instanceof RowDataStatic)) && (!isLast()) && (!isAfterLast()) && (this.rowData.size() != 0))
/*      */         {
/* 6379 */           StringBuffer messageBuf = new StringBuffer(Messages.getString("ResultSet.Possible_incomplete_traversal_of_result_set._Cursor_was_left_on_row__154"));
/*      */ 
/* 6382 */           messageBuf.append(getRow());
/* 6383 */           messageBuf.append(Messages.getString("ResultSet._of__155"));
/* 6384 */           messageBuf.append(this.rowData.size());
/* 6385 */           messageBuf.append(Messages.getString("ResultSet._rows_when_it_was_closed._156"));
/*      */ 
/* 6388 */           messageBuf.append(Messages.getString("ResultSet._n_nYou_should_consider_re-formulating_your_query_to_return_only_the_rows_you_are_interested_in_using._157"));
/*      */ 
/* 6392 */           this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? Messages.getString("ResultSet.N/A_159") : this.owningStatement.currentCatalog, this.connection.getId(), this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0, null, this.pointOfOrigin, messageBuf.toString()));
/*      */         }
/*      */ 
/* 6408 */         if (this.columnUsed.length > 0) {
/* 6409 */           StringBuffer buf = new StringBuffer(Messages.getString("ResultSet.The_following_columns_were__160"));
/*      */ 
/* 6412 */           buf.append(Messages.getString("ResultSet._part_of_the_SELECT_statement_for_this_result_set,_but_were_161"));
/*      */ 
/* 6415 */           buf.append(Messages.getString("ResultSet._never_referenced___162"));
/*      */ 
/* 6418 */           boolean issueWarn = false;
/*      */ 
/* 6420 */           for (int i = 0; i < this.columnUsed.length; i++) {
/* 6421 */             if (this.columnUsed[i] == 0) {
/* 6422 */               if (!issueWarn)
/* 6423 */                 issueWarn = true;
/*      */               else {
/* 6425 */                 buf.append(", ");
/*      */               }
/*      */ 
/* 6428 */               buf.append(this.fields[i].getFullName());
/*      */             }
/*      */           }
/*      */ 
/* 6432 */           if (issueWarn) {
/* 6433 */             this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connection.getId(), this.owningStatement == null ? -1 : this.owningStatement.getId(), 0, System.currentTimeMillis(), 0, null, this.pointOfOrigin, buf.toString()));
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     finally
/*      */     {
/* 6446 */       SQLException exceptionDuringClose = null;
/*      */ 
/* 6448 */       if (this.rowData != null) {
/*      */         try {
/* 6450 */           this.rowData.close();
/*      */         } catch (SQLException sqlEx) {
/* 6452 */           exceptionDuringClose = sqlEx;
/*      */         }
/*      */       }
/*      */ 
/* 6456 */       this.rowData = null;
/* 6457 */       this.defaultTimeZone = null;
/* 6458 */       this.fields = null;
/* 6459 */       this.columnNameToIndex = null;
/* 6460 */       this.fullColumnNameToIndex = null;
/* 6461 */       this.eventSink = null;
/* 6462 */       this.warningChain = null;
/*      */ 
/* 6464 */       if (!this.retainOwningStatement) {
/* 6465 */         this.owningStatement = null;
/*      */       }
/*      */ 
/* 6468 */       this.catalog = null;
/* 6469 */       this.serverInfo = null;
/* 6470 */       this.thisRow = null;
/* 6471 */       this.fastDateCal = null;
/* 6472 */       this.connection = null;
/*      */ 
/* 6474 */       this.isClosed = true;
/*      */ 
/* 6476 */       if (exceptionDuringClose != null)
/* 6477 */         throw exceptionDuringClose;
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean reallyResult()
/*      */   {
/* 6483 */     if (this.rowData != null) {
/* 6484 */       return true;
/*      */     }
/*      */ 
/* 6487 */     return this.reallyResult;
/*      */   }
/*      */ 
/*      */   public void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 6511 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 6541 */     checkClosed();
/*      */ 
/* 6543 */     if (this.rowData.size() == 0) {
/* 6544 */       return false;
/*      */     }
/*      */ 
/* 6547 */     this.rowData.moveRowRelative(rows);
/* 6548 */     this.thisRow = this.rowData.getAt(this.rowData.getCurrentRowNumber());
/*      */ 
/* 6550 */     return (!this.rowData.isAfterLast()) && (!this.rowData.isBeforeFirst());
/*      */   }
/*      */ 
/*      */   public boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 6569 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 6587 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 6605 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   protected void setBinaryEncoded()
/*      */   {
/* 6613 */     this.isBinaryEncoded = true;
/*      */   }
/*      */ 
/*      */   private void setDefaultTimeZone(TimeZone defaultTimeZone) {
/* 6617 */     this.defaultTimeZone = defaultTimeZone;
/*      */   }
/*      */ 
/*      */   public void setFetchDirection(int direction)
/*      */     throws SQLException
/*      */   {
/* 6636 */     if ((direction != 1000) && (direction != 1001) && (direction != 1002))
/*      */     {
/* 6638 */       throw new SQLException(Messages.getString("ResultSet.Illegal_value_for_fetch_direction_64"), "S1009");
/*      */     }
/*      */ 
/* 6644 */     this.fetchDirection = direction;
/*      */   }
/*      */ 
/*      */   public void setFetchSize(int rows)
/*      */     throws SQLException
/*      */   {
/* 6664 */     if (rows < 0) {
/* 6665 */       throw new SQLException(Messages.getString("ResultSet.Value_must_be_between_0_and_getMaxRows()_66"), "S1009");
/*      */     }
/*      */ 
/* 6671 */     this.fetchSize = rows;
/*      */   }
/*      */ 
/*      */   protected void setFirstCharOfQuery(char c)
/*      */   {
/* 6682 */     this.firstCharOfQuery = c;
/*      */   }
/*      */ 
/*      */   protected void setNextResultSet(ResultSet nextResultSet)
/*      */   {
/* 6693 */     this.nextResultSet = nextResultSet;
/*      */   }
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 6703 */     this.resultSetConcurrency = concurrencyFlag;
/*      */   }
/*      */ 
/*      */   protected void setResultSetType(int typeFlag)
/*      */   {
/* 6714 */     this.resultSetType = typeFlag;
/*      */   }
/*      */ 
/*      */   protected void setServerInfo(String info)
/*      */   {
/* 6724 */     this.serverInfo = info;
/*      */   }
/*      */ 
/*      */   public void setWrapperStatement(java.sql.Statement wrapperStatement)
/*      */   {
/* 6732 */     this.wrapperStatement = wrapperStatement;
/*      */   }
/*      */ 
/*      */   private void throwRangeException(String valueAsString, int columnIndex, int jdbcType) throws SQLException
/*      */   {
/* 6737 */     String datatype = null;
/*      */ 
/* 6739 */     switch (jdbcType) {
/*      */     case -6:
/* 6741 */       datatype = "TINYINT";
/* 6742 */       break;
/*      */     case 5:
/* 6744 */       datatype = "SMALLINT";
/* 6745 */       break;
/*      */     case 4:
/* 6747 */       datatype = "INTEGER";
/* 6748 */       break;
/*      */     case -5:
/* 6750 */       datatype = "BIGINT";
/* 6751 */       break;
/*      */     case 7:
/* 6753 */       datatype = "REAL";
/* 6754 */       break;
/*      */     case 6:
/* 6756 */       datatype = "FLOAT";
/* 6757 */       break;
/*      */     case 8:
/* 6759 */       datatype = "DOUBLE";
/* 6760 */       break;
/*      */     case 3:
/* 6762 */       datatype = "DECIMAL";
/* 6763 */       break;
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 0:
/*      */     case 1:
/*      */     case 2:
/*      */     default:
/* 6765 */       datatype = " (JDBC type '" + jdbcType + "')";
/*      */     }
/*      */ 
/* 6768 */     throw new SQLException("'" + valueAsString + "' in column '" + columnIndex + "' is outside valid range for the datatype " + datatype + ".", "22003");
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 6779 */     if (this.reallyResult) {
/* 6780 */       return super.toString();
/*      */     }
/*      */ 
/* 6783 */     return "Result set representing update count of " + this.updateCount;
/*      */   }
/*      */ 
/*      */   public void updateArray(int arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 6790 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void updateArray(String arg0, Array arg1)
/*      */     throws SQLException
/*      */   {
/* 6797 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 6821 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 6843 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 6864 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 6883 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 6907 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 6929 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public void updateBlob(int arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 6936 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBlob(String arg0, java.sql.Blob arg1)
/*      */     throws SQLException
/*      */   {
/* 6943 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 6963 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 6981 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 7001 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 7019 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 7039 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 7057 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 7081 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 7103 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int arg0, java.sql.Clob arg1)
/*      */     throws SQLException
/*      */   {
/* 7110 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void updateClob(String columnName, java.sql.Clob clob)
/*      */     throws SQLException
/*      */   {
/* 7118 */     updateClob(findColumn(columnName), clob);
/*      */   }
/*      */ 
/*      */   public void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 7139 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 7158 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 7178 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 7196 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 7216 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 7234 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 7254 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 7272 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 7292 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 7310 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 7328 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 7344 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 7364 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 7389 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 7407 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 7430 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateRef(int arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 7437 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRef(String arg0, Ref arg1)
/*      */     throws SQLException
/*      */   {
/* 7444 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public void updateRow()
/*      */     throws SQLException
/*      */   {
/* 7458 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 7478 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 7496 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 7516 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 7534 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 7555 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 7574 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 7595 */     throw new NotUpdatable();
/*      */   }
/*      */ 
/*      */   public void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 7614 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 7629 */     return this.wasNullFlag;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.ResultSet
 * JD-Core Version:    0.6.0
 */
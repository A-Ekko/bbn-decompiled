/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*      */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ 
/*      */ public class UpdatableResultSet extends ResultSet
/*      */ {
/*   45 */   private static final byte[] STREAM_DATA_MARKER = "** STREAM DATA **".getBytes();
/*      */   private SingleByteCharsetConverter charConverter;
/*      */   private String charEncoding;
/*      */   private byte[][] defaultColumnValue;
/*   56 */   private PreparedStatement deleter = null;
/*      */ 
/*   58 */   private String deleteSQL = null;
/*      */ 
/*   60 */   private boolean initializedCharConverter = false;
/*      */ 
/*   63 */   private PreparedStatement inserter = null;
/*      */ 
/*   65 */   private String insertSQL = null;
/*      */ 
/*   68 */   private boolean isUpdatable = false;
/*      */ 
/*   71 */   private List primaryKeyIndicies = null;
/*      */   private String qualifiedAndQuotedTableName;
/*   75 */   private String quotedIdChar = null;
/*      */   private PreparedStatement refresher;
/*   80 */   private String refreshSQL = null;
/*      */   private byte[][] savedCurrentRow;
/*      */   private String tableOnlyName;
/*   88 */   private PreparedStatement updater = null;
/*      */ 
/*   91 */   private String updateSQL = null;
/*      */ 
/*      */   public UpdatableResultSet(long updateCount, long updateID, Connection conn, Statement creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  110 */     super(updateCount, updateID, conn, creatorStmt);
/*  111 */     checkUpdatability();
/*      */   }
/*      */ 
/*      */   public UpdatableResultSet(String catalog, Field[] fields, RowData tuples, Connection conn, Statement creatorStmt)
/*      */     throws SQLException
/*      */   {
/*  133 */     super(catalog, fields, tuples, conn, creatorStmt);
/*  134 */     checkUpdatability();
/*      */   }
/*      */ 
/*      */   public synchronized boolean absolute(int row)
/*      */     throws SQLException
/*      */   {
/*  176 */     return super.absolute(row);
/*      */   }
/*      */ 
/*      */   public synchronized void afterLast()
/*      */     throws SQLException
/*      */   {
/*  192 */     super.afterLast();
/*      */   }
/*      */ 
/*      */   public synchronized void beforeFirst()
/*      */     throws SQLException
/*      */   {
/*  208 */     super.beforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized void cancelRowUpdates()
/*      */     throws SQLException
/*      */   {
/*  222 */     checkClosed();
/*      */ 
/*  224 */     if (this.doingUpdates) {
/*  225 */       this.doingUpdates = false;
/*  226 */       this.updater.clearParameters();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void checkRowPos()
/*      */     throws SQLException
/*      */   {
/*  236 */     checkClosed();
/*      */ 
/*  238 */     if (!this.onInsertRow)
/*  239 */       super.checkRowPos();
/*      */   }
/*      */ 
/*      */   private void checkUpdatability()
/*      */     throws SQLException
/*      */   {
/*  250 */     String singleTableName = null;
/*  251 */     String catalogName = null;
/*      */ 
/*  253 */     int primaryKeyCount = 0;
/*      */ 
/*  255 */     if (this.fields.length > 0) {
/*  256 */       singleTableName = this.fields[0].getOriginalTableName();
/*  257 */       catalogName = this.fields[0].getDatabaseName();
/*      */ 
/*  259 */       if (singleTableName == null) {
/*  260 */         singleTableName = this.fields[0].getTableName();
/*  261 */         catalogName = this.catalog;
/*      */       }
/*      */ 
/*  264 */       if (this.fields[0].isPrimaryKey()) {
/*  265 */         primaryKeyCount++;
/*      */       }
/*      */ 
/*  271 */       for (int i = 1; i < this.fields.length; i++) {
/*  272 */         String otherTableName = this.fields[i].getOriginalTableName();
/*  273 */         String otherCatalogName = this.fields[i].getDatabaseName();
/*      */ 
/*  275 */         if (otherTableName == null) {
/*  276 */           otherTableName = this.fields[i].getTableName();
/*  277 */           otherCatalogName = this.catalog;
/*      */         }
/*      */ 
/*  280 */         if ((singleTableName == null) || (!otherTableName.equals(singleTableName)))
/*      */         {
/*  282 */           this.isUpdatable = false;
/*      */ 
/*  284 */           return;
/*      */         }
/*      */ 
/*  288 */         if ((catalogName == null) || (!otherCatalogName.equals(catalogName)))
/*      */         {
/*  290 */           this.isUpdatable = false;
/*      */ 
/*  292 */           return;
/*      */         }
/*      */ 
/*  295 */         if (this.fields[i].isPrimaryKey()) {
/*  296 */           primaryKeyCount++;
/*      */         }
/*      */       }
/*      */ 
/*  300 */       if ((singleTableName == null) || (singleTableName.length() == 0)) {
/*  301 */         this.isUpdatable = false;
/*      */ 
/*  303 */         return;
/*      */       }
/*      */     } else {
/*  306 */       this.isUpdatable = false;
/*      */ 
/*  308 */       return;
/*      */     }
/*      */ 
/*  314 */     if (primaryKeyCount == 0) {
/*  315 */       this.isUpdatable = false;
/*      */ 
/*  317 */       return;
/*      */     }
/*      */ 
/*  325 */     if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  326 */       this.catalog = this.fields[0].getDatabaseName();
/*      */ 
/*  328 */       if ((this.catalog == null) || (this.catalog.length() == 0)) {
/*  329 */         throw new SQLException(Messages.getString("UpdatableResultSet.43"), "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  335 */     if (this.connection.getStrictUpdates()) {
/*  336 */       DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  338 */       java.sql.ResultSet rs = null;
/*  339 */       HashMap primaryKeyNames = new HashMap();
/*      */       try
/*      */       {
/*  342 */         rs = dbmd.getPrimaryKeys(catalogName, null, singleTableName);
/*      */ 
/*  344 */         while (rs.next()) {
/*  345 */           String keyName = rs.getString(4);
/*  346 */           keyName = keyName.toUpperCase();
/*  347 */           primaryKeyNames.put(keyName, keyName);
/*      */         }
/*      */       } finally {
/*  350 */         if (rs != null) {
/*      */           try {
/*  352 */             rs.close();
/*      */           } catch (Exception ex) {
/*  354 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/*  357 */           rs = null;
/*      */         }
/*      */       }
/*      */ 
/*  361 */       if (primaryKeyNames.size() == 0) {
/*  362 */         this.isUpdatable = false;
/*      */ 
/*  364 */         return;
/*      */       }
/*      */ 
/*  370 */       for (int i = 0; i < this.fields.length; i++) {
/*  371 */         if (this.fields[i].isPrimaryKey()) {
/*  372 */           String columnNameUC = this.fields[i].getName().toUpperCase();
/*      */ 
/*  375 */           if (primaryKeyNames.remove(columnNameUC) != null)
/*      */             continue;
/*  377 */           String originalName = this.fields[i].getOriginalName();
/*      */ 
/*  379 */           if ((originalName == null) || 
/*  380 */             (primaryKeyNames.remove(originalName.toUpperCase()) != null)) {
/*      */             continue;
/*      */           }
/*  383 */           this.isUpdatable = false;
/*      */ 
/*  385 */           return;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  392 */       this.isUpdatable = primaryKeyNames.isEmpty();
/*      */ 
/*  394 */       return;
/*      */     }
/*      */ 
/*  397 */     this.isUpdatable = true;
/*      */   }
/*      */ 
/*      */   public synchronized void deleteRow()
/*      */     throws SQLException
/*      */   {
/*  413 */     checkClosed();
/*      */ 
/*  415 */     if (!this.isUpdatable) {
/*  416 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/*  419 */     if (this.onInsertRow)
/*  420 */       throw new SQLException(Messages.getString("UpdatableResultSet.1"));
/*  421 */     if (this.rowData.size() == 0)
/*  422 */       throw new SQLException(Messages.getString("UpdatableResultSet.2"));
/*  423 */     if (isBeforeFirst())
/*  424 */       throw new SQLException(Messages.getString("UpdatableResultSet.3"));
/*  425 */     if (isAfterLast()) {
/*  426 */       throw new SQLException(Messages.getString("UpdatableResultSet.4"));
/*      */     }
/*      */ 
/*  429 */     if (this.deleter == null) {
/*  430 */       if (this.deleteSQL == null) {
/*  431 */         generateStatements();
/*      */       }
/*      */ 
/*  434 */       this.deleter = this.connection.clientPrepareStatement(this.deleteSQL);
/*      */     }
/*      */ 
/*  438 */     this.deleter.clearParameters();
/*      */ 
/*  440 */     String characterEncoding = null;
/*      */ 
/*  442 */     if (this.connection.getUseUnicode()) {
/*  443 */       characterEncoding = this.connection.getEncoding();
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/*  450 */       int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/*  452 */       if (numKeys == 1) {
/*  453 */         int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/*  455 */         String currentVal = characterEncoding == null ? new String((byte[])this.thisRow[index]) : new String((byte[])this.thisRow[index], characterEncoding);
/*      */ 
/*  458 */         this.deleter.setString(1, currentVal);
/*      */       } else {
/*  460 */         for (int i = 0; i < numKeys; i++) {
/*  461 */           int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/*  463 */           String currentVal = characterEncoding == null ? new String((byte[])this.thisRow[index]) : new String((byte[])this.thisRow[index], characterEncoding);
/*      */ 
/*  467 */           this.deleter.setString(i + 1, currentVal);
/*      */         }
/*      */       }
/*      */ 
/*  471 */       this.deleter.executeUpdate();
/*  472 */       this.rowData.removeRow(this.rowData.getCurrentRowNumber());
/*      */     } catch (UnsupportedEncodingException encodingEx) {
/*  474 */       throw new SQLException(Messages.getString("UpdatableResultSet.39", new Object[] { this.charEncoding }), "S1009");
/*      */     }
/*      */   }
/*      */ 
/*      */   private synchronized void extractDefaultValues()
/*      */     throws SQLException
/*      */   {
/*  481 */     DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  483 */     java.sql.ResultSet columnsResultSet = null;
/*      */     try
/*      */     {
/*  486 */       columnsResultSet = dbmd.getColumns(this.catalog, null, this.tableOnlyName, "%");
/*      */ 
/*  489 */       HashMap columnNameToDefaultValueMap = new HashMap(this.fields.length);
/*      */ 
/*  492 */       while (columnsResultSet.next()) {
/*  493 */         String columnName = columnsResultSet.getString("COLUMN_NAME");
/*  494 */         byte[] defaultValue = columnsResultSet.getBytes("COLUMN_DEF");
/*      */ 
/*  496 */         columnNameToDefaultValueMap.put(columnName, defaultValue);
/*      */       }
/*      */ 
/*  499 */       int numFields = this.fields.length;
/*      */ 
/*  501 */       this.defaultColumnValue = new byte[numFields][];
/*      */ 
/*  503 */       for (int i = 0; i < numFields; i++) {
/*  504 */         String defValTableName = this.fields[i].getOriginalName();
/*      */ 
/*  506 */         if ((defValTableName == null) || (defValTableName.length() == 0))
/*      */         {
/*  508 */           defValTableName = this.fields[i].getName();
/*      */         }
/*      */ 
/*  511 */         if (defValTableName != null) {
/*  512 */           byte[] defaultVal = (byte[])columnNameToDefaultValueMap.get(defValTableName);
/*      */ 
/*  515 */           this.defaultColumnValue[i] = defaultVal;
/*      */         }
/*      */       }
/*      */     } finally {
/*  519 */       if (columnsResultSet != null) {
/*  520 */         columnsResultSet.close();
/*      */ 
/*  522 */         columnsResultSet = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean first()
/*      */     throws SQLException
/*      */   {
/*  541 */     return super.first();
/*      */   }
/*      */ 
/*      */   protected synchronized void generateStatements()
/*      */     throws SQLException
/*      */   {
/*  554 */     if (!this.isUpdatable) {
/*  555 */       this.doingUpdates = false;
/*  556 */       this.onInsertRow = false;
/*      */ 
/*  558 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/*  561 */     String quotedId = getQuotedIdChar();
/*      */ 
/*  563 */     if (this.fields[0].getOriginalTableName() != null) {
/*  564 */       StringBuffer tableNameBuffer = new StringBuffer();
/*      */ 
/*  566 */       String databaseName = this.fields[0].getDatabaseName();
/*      */ 
/*  568 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/*  569 */         tableNameBuffer.append(quotedId);
/*  570 */         tableNameBuffer.append(databaseName);
/*  571 */         tableNameBuffer.append(quotedId);
/*  572 */         tableNameBuffer.append('.');
/*      */       }
/*      */ 
/*  575 */       this.tableOnlyName = this.fields[0].getOriginalTableName();
/*      */ 
/*  577 */       tableNameBuffer.append(quotedId);
/*  578 */       tableNameBuffer.append(this.tableOnlyName);
/*  579 */       tableNameBuffer.append(quotedId);
/*      */ 
/*  581 */       this.qualifiedAndQuotedTableName = tableNameBuffer.toString();
/*      */     } else {
/*  583 */       StringBuffer tableNameBuffer = new StringBuffer();
/*      */ 
/*  585 */       this.tableOnlyName = this.fields[0].getTableName();
/*      */ 
/*  587 */       tableNameBuffer.append(quotedId);
/*  588 */       tableNameBuffer.append(this.tableOnlyName);
/*  589 */       tableNameBuffer.append(quotedId);
/*      */ 
/*  591 */       this.qualifiedAndQuotedTableName = tableNameBuffer.toString();
/*      */     }
/*      */ 
/*  594 */     this.primaryKeyIndicies = new ArrayList();
/*      */ 
/*  596 */     StringBuffer fieldValues = new StringBuffer();
/*  597 */     StringBuffer keyValues = new StringBuffer();
/*  598 */     StringBuffer columnNames = new StringBuffer();
/*  599 */     StringBuffer insertPlaceHolders = new StringBuffer();
/*  600 */     boolean firstTime = true;
/*  601 */     boolean keysFirstTime = true;
/*      */ 
/*  603 */     String equalsStr = this.connection.versionMeetsMinimum(3, 23, 0) ? "<=>" : "=";
/*      */ 
/*  606 */     for (int i = 0; i < this.fields.length; i++) {
/*  607 */       String originalColumnName = this.fields[i].getOriginalName();
/*  608 */       String columnName = null;
/*      */ 
/*  610 */       if ((this.connection.getIO().hasLongColumnInfo()) && (originalColumnName != null) && (originalColumnName.length() > 0))
/*      */       {
/*  613 */         columnName = originalColumnName;
/*      */       }
/*  615 */       else columnName = this.fields[i].getName();
/*      */ 
/*  618 */       if (this.fields[i].isPrimaryKey()) {
/*  619 */         this.primaryKeyIndicies.add(new Integer(i));
/*      */ 
/*  621 */         if (!keysFirstTime)
/*  622 */           keyValues.append(" AND ");
/*      */         else {
/*  624 */           keysFirstTime = false;
/*      */         }
/*      */ 
/*  627 */         keyValues.append(quotedId);
/*  628 */         keyValues.append(columnName);
/*  629 */         keyValues.append(quotedId);
/*  630 */         keyValues.append(equalsStr);
/*  631 */         keyValues.append("?");
/*      */       }
/*      */ 
/*  634 */       if (firstTime) {
/*  635 */         firstTime = false;
/*  636 */         fieldValues.append("SET ");
/*      */       } else {
/*  638 */         fieldValues.append(",");
/*  639 */         columnNames.append(",");
/*  640 */         insertPlaceHolders.append(",");
/*      */       }
/*      */ 
/*  643 */       insertPlaceHolders.append("?");
/*      */ 
/*  645 */       columnNames.append(quotedId);
/*  646 */       columnNames.append(columnName);
/*  647 */       columnNames.append(quotedId);
/*      */ 
/*  649 */       fieldValues.append(quotedId);
/*  650 */       fieldValues.append(columnName);
/*  651 */       fieldValues.append(quotedId);
/*  652 */       fieldValues.append("=?");
/*      */     }
/*      */ 
/*  655 */     this.updateSQL = ("UPDATE " + this.qualifiedAndQuotedTableName + " " + fieldValues.toString() + " WHERE " + keyValues.toString());
/*      */ 
/*  658 */     this.insertSQL = ("INSERT INTO " + this.qualifiedAndQuotedTableName + " (" + columnNames.toString() + ") VALUES (" + insertPlaceHolders.toString() + ")");
/*      */ 
/*  661 */     this.refreshSQL = ("SELECT " + columnNames.toString() + " FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */ 
/*  664 */     this.deleteSQL = ("DELETE FROM " + this.qualifiedAndQuotedTableName + " WHERE " + keyValues.toString());
/*      */   }
/*      */ 
/*      */   private synchronized SingleByteCharsetConverter getCharConverter()
/*      */     throws SQLException
/*      */   {
/*  671 */     if (!this.initializedCharConverter) {
/*  672 */       this.initializedCharConverter = true;
/*      */ 
/*  674 */       if (this.connection.getUseUnicode()) {
/*  675 */         this.charEncoding = this.connection.getEncoding();
/*  676 */         this.charConverter = this.connection.getCharsetConverter(this.charEncoding);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  681 */     return this.charConverter;
/*      */   }
/*      */ 
/*      */   public int getConcurrency()
/*      */     throws SQLException
/*      */   {
/*  694 */     return this.isUpdatable ? 1008 : 1007;
/*      */   }
/*      */ 
/*      */   private synchronized String getQuotedIdChar() throws SQLException {
/*  698 */     if (this.quotedIdChar == null) {
/*  699 */       boolean useQuotedIdentifiers = this.connection.supportsQuotedIdentifiers();
/*      */ 
/*  702 */       if (useQuotedIdentifiers) {
/*  703 */         DatabaseMetaData dbmd = this.connection.getMetaData();
/*  704 */         this.quotedIdChar = dbmd.getIdentifierQuoteString();
/*      */       } else {
/*  706 */         this.quotedIdChar = "";
/*      */       }
/*      */     }
/*      */ 
/*  710 */     return this.quotedIdChar;
/*      */   }
/*      */ 
/*      */   public synchronized void insertRow()
/*      */     throws SQLException
/*      */   {
/*  723 */     checkClosed();
/*      */ 
/*  725 */     if (!this.onInsertRow) {
/*  726 */       throw new SQLException(Messages.getString("UpdatableResultSet.7"));
/*      */     }
/*      */ 
/*  729 */     this.inserter.executeUpdate();
/*      */ 
/*  731 */     int numPrimaryKeys = 0;
/*      */ 
/*  733 */     if (this.primaryKeyIndicies != null) {
/*  734 */       numPrimaryKeys = this.primaryKeyIndicies.size();
/*      */     }
/*      */ 
/*  737 */     long autoIncrementId = this.inserter.getLastInsertID();
/*  738 */     int numFields = this.fields.length;
/*  739 */     byte[][] newRow = new byte[numFields][];
/*      */ 
/*  741 */     for (int i = 0; i < numFields; i++) {
/*  742 */       if (this.inserter.isNull(i))
/*  743 */         newRow[i] = null;
/*      */       else {
/*  745 */         newRow[i] = this.inserter.getBytesRepresentation(i);
/*      */       }
/*      */ 
/*  748 */       if ((numPrimaryKeys != 1) || (!this.fields[i].isPrimaryKey()) || (autoIncrementId <= 0L))
/*      */         continue;
/*  750 */       newRow[i] = String.valueOf(autoIncrementId).getBytes();
/*      */     }
/*      */ 
/*  754 */     this.rowData.addRow(newRow);
/*  755 */     resetInserter();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isAfterLast()
/*      */     throws SQLException
/*      */   {
/*  772 */     return super.isAfterLast();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isBeforeFirst()
/*      */     throws SQLException
/*      */   {
/*  789 */     return super.isBeforeFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isFirst()
/*      */     throws SQLException
/*      */   {
/*  805 */     return super.isFirst();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isLast()
/*      */     throws SQLException
/*      */   {
/*  824 */     return super.isLast();
/*      */   }
/*      */ 
/*      */   boolean isUpdatable() {
/*  828 */     return this.isUpdatable;
/*      */   }
/*      */ 
/*      */   public synchronized boolean last()
/*      */     throws SQLException
/*      */   {
/*  845 */     return super.last();
/*      */   }
/*      */ 
/*      */   public synchronized void moveToCurrentRow()
/*      */     throws SQLException
/*      */   {
/*  859 */     checkClosed();
/*      */ 
/*  861 */     if (!this.isUpdatable) {
/*  862 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/*  865 */     if (this.onInsertRow) {
/*  866 */       this.onInsertRow = false;
/*  867 */       this.thisRow = this.savedCurrentRow;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void moveToInsertRow()
/*      */     throws SQLException
/*      */   {
/*  889 */     checkClosed();
/*      */ 
/*  891 */     if (!this.isUpdatable) {
/*  892 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/*  895 */     if (this.inserter == null) {
/*  896 */       if (this.insertSQL == null) {
/*  897 */         generateStatements();
/*      */       }
/*      */ 
/*  900 */       this.inserter = this.connection.clientPrepareStatement(this.insertSQL);
/*      */ 
/*  902 */       extractDefaultValues();
/*  903 */       resetInserter();
/*      */     } else {
/*  905 */       resetInserter();
/*      */     }
/*      */ 
/*  908 */     int numFields = this.fields.length;
/*      */ 
/*  910 */     this.onInsertRow = true;
/*  911 */     this.doingUpdates = false;
/*  912 */     this.savedCurrentRow = ((byte[][])this.thisRow);
/*  913 */     this.thisRow = new byte[numFields][];
/*      */ 
/*  915 */     for (int i = 0; i < numFields; i++)
/*  916 */       if (this.defaultColumnValue[i] != null) {
/*  917 */         Field f = this.fields[i];
/*      */ 
/*  919 */         switch (f.getMysqlType())
/*      */         {
/*      */         case 7:
/*      */         case 10:
/*      */         case 11:
/*      */         case 12:
/*      */         case 14:
/*  926 */           if ((this.defaultColumnValue[i].length <= 7) || (this.defaultColumnValue[i][0] != 67) || (this.defaultColumnValue[i][1] != 85) || (this.defaultColumnValue[i][2] != 82) || (this.defaultColumnValue[i][3] != 82) || (this.defaultColumnValue[i][4] != 69) || (this.defaultColumnValue[i][5] != 78) || (this.defaultColumnValue[i][6] != 84) || (this.defaultColumnValue[i][7] != 95))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*  935 */           this.inserter.setBytesNoEscapeNoQuotes(i + 1, this.defaultColumnValue[i]);
/*      */ 
/*  938 */           break;
/*      */         case 8:
/*      */         case 9:
/*  941 */         case 13: } this.inserter.setBytes(i + 1, this.defaultColumnValue[i], false, false);
/*      */ 
/*  947 */         byte[] defaultValueCopy = new byte[this.defaultColumnValue[i].length];
/*  948 */         System.arraycopy(this.defaultColumnValue[i], 0, defaultValueCopy, 0, defaultValueCopy.length);
/*      */ 
/*  950 */         this.thisRow[i] = defaultValueCopy;
/*      */       } else {
/*  952 */         this.inserter.setNull(i + 1, 0);
/*  953 */         this.thisRow[i] = null;
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized boolean next()
/*      */     throws SQLException
/*      */   {
/*  978 */     return super.next();
/*      */   }
/*      */ 
/*      */   public synchronized boolean prev()
/*      */     throws SQLException
/*      */   {
/*  997 */     return super.prev();
/*      */   }
/*      */ 
/*      */   public synchronized boolean previous()
/*      */     throws SQLException
/*      */   {
/* 1019 */     return super.previous();
/*      */   }
/*      */ 
/*      */   protected void realClose(boolean calledExplicitly)
/*      */     throws SQLException
/*      */   {
/* 1032 */     SQLException sqlEx = null;
/*      */ 
/* 1034 */     if ((this.useUsageAdvisor) && 
/* 1035 */       (this.deleter == null) && (this.inserter == null) && (this.refresher == null) && (this.updater == null))
/*      */     {
/* 1037 */       this.eventSink = ProfileEventSink.getInstance(this.connection);
/*      */ 
/* 1039 */       String message = Messages.getString("UpdatableResultSet.34");
/*      */ 
/* 1041 */       this.eventSink.consumeEvent(new ProfilerEvent(0, "", this.owningStatement == null ? "N/A" : this.owningStatement.currentCatalog, this.connection.getId(), this.owningStatement == null ? -1 : this.owningStatement.getId(), this.resultId, System.currentTimeMillis(), 0, null, this.pointOfOrigin, message));
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1054 */       if (this.deleter != null)
/* 1055 */         this.deleter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1058 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1062 */       if (this.inserter != null)
/* 1063 */         this.inserter.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1066 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1070 */       if (this.refresher != null)
/* 1071 */         this.refresher.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1074 */       sqlEx = ex;
/*      */     }
/*      */     try
/*      */     {
/* 1078 */       if (this.updater != null)
/* 1079 */         this.updater.close();
/*      */     }
/*      */     catch (SQLException ex) {
/* 1082 */       sqlEx = ex;
/*      */     }
/*      */ 
/* 1085 */     super.realClose(calledExplicitly);
/*      */ 
/* 1087 */     if (sqlEx != null)
/* 1088 */       throw sqlEx;
/*      */   }
/*      */ 
/*      */   public synchronized void refreshRow()
/*      */     throws SQLException
/*      */   {
/* 1113 */     checkClosed();
/*      */ 
/* 1115 */     if (!this.isUpdatable) {
/* 1116 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/* 1119 */     if (this.onInsertRow)
/* 1120 */       throw new SQLException(Messages.getString("UpdatableResultSet.8"));
/* 1121 */     if (this.rowData.size() == 0)
/* 1122 */       throw new SQLException(Messages.getString("UpdatableResultSet.9"));
/* 1123 */     if (isBeforeFirst())
/* 1124 */       throw new SQLException(Messages.getString("UpdatableResultSet.10"));
/* 1125 */     if (isAfterLast()) {
/* 1126 */       throw new SQLException(Messages.getString("UpdatableResultSet.11"));
/*      */     }
/*      */ 
/* 1129 */     if (this.refresher == null) {
/* 1130 */       if (this.refreshSQL == null) {
/* 1131 */         generateStatements();
/*      */       }
/*      */ 
/* 1134 */       this.refresher = this.connection.clientPrepareStatement(this.refreshSQL);
/*      */     }
/*      */ 
/* 1138 */     this.refresher.clearParameters();
/*      */ 
/* 1140 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1142 */     if (numKeys == 1) {
/* 1143 */       byte[] dataFrom = null;
/* 1144 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/*      */ 
/* 1146 */       if (!this.doingUpdates) {
/* 1147 */         dataFrom = (byte[])this.thisRow[index];
/*      */       } else {
/* 1149 */         dataFrom = this.updater.getBytesRepresentation(index);
/*      */ 
/* 1152 */         if ((this.updater.isNull(index)) || (dataFrom.length == 0))
/* 1153 */           dataFrom = (byte[])this.thisRow[index];
/*      */         else {
/* 1155 */           dataFrom = stripBinaryPrefix(dataFrom);
/*      */         }
/*      */       }
/*      */ 
/* 1159 */       this.refresher.setBytesNoEscape(1, dataFrom);
/*      */     } else {
/* 1161 */       for (int i = 0; i < numKeys; i++) {
/* 1162 */         byte[] dataFrom = null;
/* 1163 */         int index = ((Integer)this.primaryKeyIndicies.get(i)).intValue();
/*      */ 
/* 1166 */         if (!this.doingUpdates) {
/* 1167 */           dataFrom = (byte[])this.thisRow[index];
/*      */         } else {
/* 1169 */           dataFrom = this.updater.getBytesRepresentation(index);
/*      */ 
/* 1172 */           if ((this.updater.isNull(index)) || (dataFrom.length == 0))
/* 1173 */             dataFrom = (byte[])this.thisRow[index];
/*      */           else {
/* 1175 */             dataFrom = stripBinaryPrefix(dataFrom);
/*      */           }
/*      */         }
/*      */ 
/* 1179 */         this.refresher.setBytesNoEscape(i + 1, dataFrom);
/*      */       }
/*      */     }
/*      */ 
/* 1183 */     java.sql.ResultSet rs = null;
/*      */     try
/*      */     {
/* 1186 */       rs = this.refresher.executeQuery();
/*      */ 
/* 1188 */       int numCols = rs.getMetaData().getColumnCount();
/*      */ 
/* 1190 */       if (rs.next()) {
/* 1191 */         for (int i = 0; i < numCols; i++) {
/* 1192 */           byte[] val = rs.getBytes(i + 1);
/*      */ 
/* 1194 */           if ((val == null) || (rs.wasNull()))
/* 1195 */             this.thisRow[i] = null;
/*      */           else
/* 1197 */             this.thisRow[i] = rs.getBytes(i + 1);
/*      */         }
/*      */       }
/*      */       else {
/* 1201 */         throw new SQLException(Messages.getString("UpdatableResultSet.12"), "S1000");
/*      */       }
/*      */     }
/*      */     finally
/*      */     {
/* 1206 */       if (rs != null)
/*      */         try {
/* 1208 */           rs.close();
/*      */         }
/*      */         catch (SQLException ex)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized boolean relative(int rows)
/*      */     throws SQLException
/*      */   {
/* 1243 */     return super.relative(rows);
/*      */   }
/*      */ 
/*      */   private void resetInserter() throws SQLException {
/* 1247 */     this.inserter.clearParameters();
/*      */ 
/* 1249 */     for (int i = 0; i < this.fields.length; i++)
/* 1250 */       this.inserter.setNull(i + 1, 0);
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowDeleted()
/*      */     throws SQLException
/*      */   {
/* 1270 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowInserted()
/*      */     throws SQLException
/*      */   {
/* 1288 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   public synchronized boolean rowUpdated()
/*      */     throws SQLException
/*      */   {
/* 1306 */     throw new NotImplemented();
/*      */   }
/*      */ 
/*      */   protected void setResultSetConcurrency(int concurrencyFlag)
/*      */   {
/* 1316 */     super.setResultSetConcurrency(concurrencyFlag);
/*      */   }
/*      */ 
/*      */   private byte[] stripBinaryPrefix(byte[] dataFrom)
/*      */   {
/* 1330 */     return StringUtils.stripEnclosure(dataFrom, "_binary'", "'");
/*      */   }
/*      */ 
/*      */   synchronized void syncUpdate()
/*      */     throws SQLException
/*      */   {
/* 1341 */     if (this.updater == null) {
/* 1342 */       if (this.updateSQL == null) {
/* 1343 */         generateStatements();
/*      */       }
/*      */ 
/* 1346 */       this.updater = this.connection.clientPrepareStatement(this.updateSQL);
/*      */     }
/*      */ 
/* 1350 */     int numFields = this.fields.length;
/* 1351 */     this.updater.clearParameters();
/*      */ 
/* 1353 */     for (int i = 0; i < numFields; i++) {
/* 1354 */       if (this.thisRow[i] != null) {
/* 1355 */         this.updater.setBytes(i + 1, (byte[])this.thisRow[i], this.fields[i].isBinary(), false);
/*      */       }
/*      */       else {
/* 1358 */         this.updater.setNull(i + 1, 0);
/*      */       }
/*      */     }
/*      */ 
/* 1362 */     int numKeys = this.primaryKeyIndicies.size();
/*      */ 
/* 1364 */     if (numKeys == 1) {
/* 1365 */       int index = ((Integer)this.primaryKeyIndicies.get(0)).intValue();
/* 1366 */       byte[] keyData = (byte[])this.thisRow[index];
/* 1367 */       this.updater.setBytes(numFields + 1, keyData, false, false);
/*      */     } else {
/* 1369 */       for (int i = 0; i < numKeys; i++) {
/* 1370 */         byte[] currentVal = (byte[])this.thisRow[((Integer)this.primaryKeyIndicies.get(i)).intValue()];
/*      */ 
/* 1373 */         if (currentVal != null) {
/* 1374 */           this.updater.setBytes(numFields + i + 1, currentVal, false, false);
/*      */         }
/*      */         else
/* 1377 */           this.updater.setNull(numFields + i + 1, 0);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1402 */     if (!this.onInsertRow) {
/* 1403 */       if (!this.doingUpdates) {
/* 1404 */         this.doingUpdates = true;
/* 1405 */         syncUpdate();
/*      */       }
/*      */ 
/* 1408 */       this.updater.setAsciiStream(columnIndex, x, length);
/*      */     } else {
/* 1410 */       this.inserter.setAsciiStream(columnIndex, x, length);
/* 1411 */       this.thisRow[(columnIndex - 1)] = STREAM_DATA_MARKER;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateAsciiStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1434 */     updateAsciiStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(int columnIndex, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1453 */     if (!this.onInsertRow) {
/* 1454 */       if (!this.doingUpdates) {
/* 1455 */         this.doingUpdates = true;
/* 1456 */         syncUpdate();
/*      */       }
/*      */ 
/* 1459 */       this.updater.setBigDecimal(columnIndex, x);
/*      */     } else {
/* 1461 */       this.inserter.setBigDecimal(columnIndex, x);
/*      */ 
/* 1463 */       if (x == null)
/* 1464 */         this.thisRow[(columnIndex - 1)] = null;
/*      */       else
/* 1466 */         this.thisRow[(columnIndex - 1)] = x.toString().getBytes();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBigDecimal(String columnName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1487 */     updateBigDecimal(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1509 */     if (!this.onInsertRow) {
/* 1510 */       if (!this.doingUpdates) {
/* 1511 */         this.doingUpdates = true;
/* 1512 */         syncUpdate();
/*      */       }
/*      */ 
/* 1515 */       this.updater.setBinaryStream(columnIndex, x, length);
/*      */     } else {
/* 1517 */       this.inserter.setBinaryStream(columnIndex, x, length);
/*      */ 
/* 1519 */       if (x == null)
/* 1520 */         this.thisRow[(columnIndex - 1)] = null;
/*      */       else
/* 1522 */         this.thisRow[(columnIndex - 1)] = STREAM_DATA_MARKER;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBinaryStream(String columnName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1546 */     updateBinaryStream(findColumn(columnName), x, length);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(int columnIndex, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1554 */     if (!this.onInsertRow) {
/* 1555 */       if (!this.doingUpdates) {
/* 1556 */         this.doingUpdates = true;
/* 1557 */         syncUpdate();
/*      */       }
/*      */ 
/* 1560 */       this.updater.setBlob(columnIndex, blob);
/*      */     } else {
/* 1562 */       this.inserter.setBlob(columnIndex, blob);
/*      */ 
/* 1564 */       if (blob == null)
/* 1565 */         this.thisRow[(columnIndex - 1)] = null;
/*      */       else
/* 1567 */         this.thisRow[(columnIndex - 1)] = STREAM_DATA_MARKER;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBlob(String columnName, Blob blob)
/*      */     throws SQLException
/*      */   {
/* 1577 */     updateBlob(findColumn(columnName), blob);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(int columnIndex, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1596 */     if (!this.onInsertRow) {
/* 1597 */       if (!this.doingUpdates) {
/* 1598 */         this.doingUpdates = true;
/* 1599 */         syncUpdate();
/*      */       }
/*      */ 
/* 1602 */       this.updater.setBoolean(columnIndex, x);
/*      */     } else {
/* 1604 */       this.inserter.setBoolean(columnIndex, x);
/*      */ 
/* 1606 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBoolean(String columnName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1627 */     updateBoolean(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(int columnIndex, byte x)
/*      */     throws SQLException
/*      */   {
/* 1646 */     if (!this.onInsertRow) {
/* 1647 */       if (!this.doingUpdates) {
/* 1648 */         this.doingUpdates = true;
/* 1649 */         syncUpdate();
/*      */       }
/*      */ 
/* 1652 */       this.updater.setByte(columnIndex, x);
/*      */     } else {
/* 1654 */       this.inserter.setByte(columnIndex, x);
/*      */ 
/* 1656 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateByte(String columnName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1677 */     updateByte(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(int columnIndex, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1696 */     if (!this.onInsertRow) {
/* 1697 */       if (!this.doingUpdates) {
/* 1698 */         this.doingUpdates = true;
/* 1699 */         syncUpdate();
/*      */       }
/*      */ 
/* 1702 */       this.updater.setBytes(columnIndex, x);
/*      */     } else {
/* 1704 */       this.inserter.setBytes(columnIndex, x);
/*      */ 
/* 1706 */       this.thisRow[(columnIndex - 1)] = x;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateBytes(String columnName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1726 */     updateBytes(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
/*      */     throws SQLException
/*      */   {
/* 1748 */     if (!this.onInsertRow) {
/* 1749 */       if (!this.doingUpdates) {
/* 1750 */         this.doingUpdates = true;
/* 1751 */         syncUpdate();
/*      */       }
/*      */ 
/* 1754 */       this.updater.setCharacterStream(columnIndex, x, length);
/*      */     } else {
/* 1756 */       this.inserter.setCharacterStream(columnIndex, x, length);
/*      */ 
/* 1758 */       if (x == null)
/* 1759 */         this.thisRow[(columnIndex - 1)] = null;
/*      */       else
/* 1761 */         this.thisRow[(columnIndex - 1)] = STREAM_DATA_MARKER;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateCharacterStream(String columnName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1785 */     updateCharacterStream(findColumn(columnName), reader, length);
/*      */   }
/*      */ 
/*      */   public void updateClob(int columnIndex, Clob clob)
/*      */     throws SQLException
/*      */   {
/* 1793 */     if (clob == null)
/* 1794 */       updateNull(columnIndex);
/*      */     else
/* 1796 */       updateCharacterStream(columnIndex, clob.getCharacterStream(), (int)clob.length());
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(int columnIndex, Date x)
/*      */     throws SQLException
/*      */   {
/* 1817 */     if (!this.onInsertRow) {
/* 1818 */       if (!this.doingUpdates) {
/* 1819 */         this.doingUpdates = true;
/* 1820 */         syncUpdate();
/*      */       }
/*      */ 
/* 1823 */       this.updater.setDate(columnIndex, x);
/*      */     } else {
/* 1825 */       this.inserter.setDate(columnIndex, x);
/*      */ 
/* 1827 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDate(String columnName, Date x)
/*      */     throws SQLException
/*      */   {
/* 1848 */     updateDate(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(int columnIndex, double x)
/*      */     throws SQLException
/*      */   {
/* 1867 */     if (!this.onInsertRow) {
/* 1868 */       if (!this.doingUpdates) {
/* 1869 */         this.doingUpdates = true;
/* 1870 */         syncUpdate();
/*      */       }
/*      */ 
/* 1873 */       this.updater.setDouble(columnIndex, x);
/*      */     } else {
/* 1875 */       this.inserter.setDouble(columnIndex, x);
/*      */ 
/* 1877 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateDouble(String columnName, double x)
/*      */     throws SQLException
/*      */   {
/* 1898 */     updateDouble(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(int columnIndex, float x)
/*      */     throws SQLException
/*      */   {
/* 1917 */     if (!this.onInsertRow) {
/* 1918 */       if (!this.doingUpdates) {
/* 1919 */         this.doingUpdates = true;
/* 1920 */         syncUpdate();
/*      */       }
/*      */ 
/* 1923 */       this.updater.setFloat(columnIndex, x);
/*      */     } else {
/* 1925 */       this.inserter.setFloat(columnIndex, x);
/*      */ 
/* 1927 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateFloat(String columnName, float x)
/*      */     throws SQLException
/*      */   {
/* 1948 */     updateFloat(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(int columnIndex, int x)
/*      */     throws SQLException
/*      */   {
/* 1967 */     if (!this.onInsertRow) {
/* 1968 */       if (!this.doingUpdates) {
/* 1969 */         this.doingUpdates = true;
/* 1970 */         syncUpdate();
/*      */       }
/*      */ 
/* 1973 */       this.updater.setInt(columnIndex, x);
/*      */     } else {
/* 1975 */       this.inserter.setInt(columnIndex, x);
/*      */ 
/* 1977 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateInt(String columnName, int x)
/*      */     throws SQLException
/*      */   {
/* 1998 */     updateInt(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(int columnIndex, long x)
/*      */     throws SQLException
/*      */   {
/* 2017 */     if (!this.onInsertRow) {
/* 2018 */       if (!this.doingUpdates) {
/* 2019 */         this.doingUpdates = true;
/* 2020 */         syncUpdate();
/*      */       }
/*      */ 
/* 2023 */       this.updater.setLong(columnIndex, x);
/*      */     } else {
/* 2025 */       this.inserter.setLong(columnIndex, x);
/*      */ 
/* 2027 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateLong(String columnName, long x)
/*      */     throws SQLException
/*      */   {
/* 2048 */     updateLong(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(int columnIndex)
/*      */     throws SQLException
/*      */   {
/* 2064 */     if (!this.onInsertRow) {
/* 2065 */       if (!this.doingUpdates) {
/* 2066 */         this.doingUpdates = true;
/* 2067 */         syncUpdate();
/*      */       }
/*      */ 
/* 2070 */       this.updater.setNull(columnIndex, 0);
/*      */     } else {
/* 2072 */       this.inserter.setNull(columnIndex, 0);
/*      */ 
/* 2074 */       this.thisRow[(columnIndex - 1)] = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateNull(String columnName)
/*      */     throws SQLException
/*      */   {
/* 2091 */     updateNull(findColumn(columnName));
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x)
/*      */     throws SQLException
/*      */   {
/* 2110 */     if (!this.onInsertRow) {
/* 2111 */       if (!this.doingUpdates) {
/* 2112 */         this.doingUpdates = true;
/* 2113 */         syncUpdate();
/*      */       }
/*      */ 
/* 2116 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2118 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2120 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(int columnIndex, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2145 */     if (!this.onInsertRow) {
/* 2146 */       if (!this.doingUpdates) {
/* 2147 */         this.doingUpdates = true;
/* 2148 */         syncUpdate();
/*      */       }
/*      */ 
/* 2151 */       this.updater.setObject(columnIndex, x);
/*      */     } else {
/* 2153 */       this.inserter.setObject(columnIndex, x);
/*      */ 
/* 2155 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x)
/*      */     throws SQLException
/*      */   {
/* 2176 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateObject(String columnName, Object x, int scale)
/*      */     throws SQLException
/*      */   {
/* 2199 */     updateObject(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateRow()
/*      */     throws SQLException
/*      */   {
/* 2213 */     if (!this.isUpdatable) {
/* 2214 */       throw new NotUpdatable();
/*      */     }
/*      */ 
/* 2217 */     if (this.doingUpdates) {
/* 2218 */       this.updater.executeUpdate();
/* 2219 */       refreshRow();
/* 2220 */       this.doingUpdates = false;
/*      */     }
/*      */ 
/* 2226 */     syncUpdate();
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(int columnIndex, short x)
/*      */     throws SQLException
/*      */   {
/* 2245 */     if (!this.onInsertRow) {
/* 2246 */       if (!this.doingUpdates) {
/* 2247 */         this.doingUpdates = true;
/* 2248 */         syncUpdate();
/*      */       }
/*      */ 
/* 2251 */       this.updater.setShort(columnIndex, x);
/*      */     } else {
/* 2253 */       this.inserter.setShort(columnIndex, x);
/*      */ 
/* 2255 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateShort(String columnName, short x)
/*      */     throws SQLException
/*      */   {
/* 2276 */     updateShort(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(int columnIndex, String x)
/*      */     throws SQLException
/*      */   {
/* 2295 */     if (!this.onInsertRow) {
/* 2296 */       if (!this.doingUpdates) {
/* 2297 */         this.doingUpdates = true;
/* 2298 */         syncUpdate();
/*      */       }
/*      */ 
/* 2301 */       this.updater.setString(columnIndex, x);
/*      */     } else {
/* 2303 */       this.inserter.setString(columnIndex, x);
/*      */ 
/* 2305 */       if (x == null) {
/* 2306 */         this.thisRow[(columnIndex - 1)] = null;
/*      */       }
/* 2308 */       else if (getCharConverter() != null) {
/* 2309 */         this.thisRow[(columnIndex - 1)] = StringUtils.getBytes(x, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode());
/*      */       }
/*      */       else
/*      */       {
/* 2314 */         this.thisRow[(columnIndex - 1)] = x.getBytes();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateString(String columnName, String x)
/*      */     throws SQLException
/*      */   {
/* 2336 */     updateString(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(int columnIndex, Time x)
/*      */     throws SQLException
/*      */   {
/* 2355 */     if (!this.onInsertRow) {
/* 2356 */       if (!this.doingUpdates) {
/* 2357 */         this.doingUpdates = true;
/* 2358 */         syncUpdate();
/*      */       }
/*      */ 
/* 2361 */       this.updater.setTime(columnIndex, x);
/*      */     } else {
/* 2363 */       this.inserter.setTime(columnIndex, x);
/*      */ 
/* 2365 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTime(String columnName, Time x)
/*      */     throws SQLException
/*      */   {
/* 2386 */     updateTime(findColumn(columnName), x);
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(int columnIndex, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2405 */     if (!this.onInsertRow) {
/* 2406 */       if (!this.doingUpdates) {
/* 2407 */         this.doingUpdates = true;
/* 2408 */         syncUpdate();
/*      */       }
/*      */ 
/* 2411 */       this.updater.setTimestamp(columnIndex, x);
/*      */     } else {
/* 2413 */       this.inserter.setTimestamp(columnIndex, x);
/*      */ 
/* 2415 */       this.thisRow[(columnIndex - 1)] = this.inserter.getBytesRepresentation(columnIndex - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void updateTimestamp(String columnName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 2436 */     updateTimestamp(findColumn(columnName), x);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.UpdatableResultSet
 * JD-Core Version:    0.6.0
 */
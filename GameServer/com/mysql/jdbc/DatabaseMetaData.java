/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.sql.ResultSetMetaData;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ public class DatabaseMetaData
/*      */   implements java.sql.DatabaseMetaData
/*      */ {
/*      */   private static final int DEFERRABILITY = 13;
/*      */   private static final int DELETE_RULE = 10;
/*      */   private static final int FK_NAME = 11;
/*      */   private static final int FKCOLUMN_NAME = 7;
/*      */   private static final int FKTABLE_CAT = 4;
/*      */   private static final int FKTABLE_NAME = 6;
/*      */   private static final int FKTABLE_SCHEM = 5;
/*      */   private static final int KEY_SEQ = 8;
/*      */   private static final int PK_NAME = 12;
/*      */   private static final int PKCOLUMN_NAME = 3;
/*      */   private static final int PKTABLE_CAT = 0;
/*      */   private static final int PKTABLE_NAME = 2;
/*      */   private static final int PKTABLE_SCHEM = 1;
/*      */   private static final String SUPPORTS_FK = "SUPPORTS_FK";
/*  377 */   private static final byte[] TABLE_AS_BYTES = "TABLE".getBytes();
/*      */   private static final int UPDATE_RULE = 9;
/*  381 */   private static final byte[] VIEW_AS_BYTES = "VIEW".getBytes();
/*      */   protected Connection conn;
/*  387 */   private String database = null;
/*      */ 
/*  390 */   private String quotedId = null;
/*      */ 
/*      */   public DatabaseMetaData(Connection connToSet, String databaseToSet)
/*      */   {
/*  401 */     this.conn = connToSet;
/*  402 */     this.database = databaseToSet;
/*      */     try
/*      */     {
/*  405 */       this.quotedId = (this.conn.supportsQuotedIdentifiers() ? getIdentifierQuoteString() : "");
/*      */     }
/*      */     catch (SQLException sqlEx)
/*      */     {
/*  411 */       AssertionFailedException.shouldNotHappen(sqlEx);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean allProceduresAreCallable()
/*      */     throws SQLException
/*      */   {
/*  424 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean allTablesAreSelectable()
/*      */     throws SQLException
/*      */   {
/*  435 */     return false;
/*      */   }
/*      */ 
/*      */   private java.sql.ResultSet buildResultSet(Field[] fields, ArrayList rows) throws SQLException
/*      */   {
/*  440 */     int fieldsLength = fields.length;
/*      */ 
/*  442 */     for (int i = 0; i < fieldsLength; i++) {
/*  443 */       fields[i].setConnection(this.conn);
/*      */     }
/*      */ 
/*  446 */     return new ResultSet(this.conn.getCatalog(), fields, new RowDataStatic(rows), this.conn, null);
/*      */   }
/*      */ 
/*      */   private void convertToJdbcFunctionList(String catalog, java.sql.ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex)
/*      */     throws SQLException
/*      */   {
/*  453 */     while (proceduresRs.next()) {
/*  454 */       boolean shouldAdd = true;
/*      */ 
/*  456 */       if (needsClientFiltering) {
/*  457 */         shouldAdd = false;
/*      */ 
/*  459 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  461 */         if ((db == null) && (procDb == null))
/*  462 */           shouldAdd = true;
/*  463 */         else if ((db != null & db.equals(procDb))) {
/*  464 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  468 */       if (shouldAdd) {
/*  469 */         String functionName = proceduresRs.getString(nameIndex);
/*  470 */         byte[][] rowData = new byte[8][];
/*  471 */         rowData[0] = (catalog == null ? null : s2b(catalog));
/*  472 */         rowData[1] = null;
/*  473 */         rowData[2] = s2b(functionName);
/*  474 */         rowData[3] = null;
/*  475 */         rowData[4] = null;
/*  476 */         rowData[5] = null;
/*  477 */         rowData[6] = null;
/*  478 */         rowData[7] = s2b(Integer.toString(2));
/*      */ 
/*  480 */         procedureRowsOrderedByName.put(functionName, rowData);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void convertToJdbcProcedureList(boolean fromSelect, String catalog, java.sql.ResultSet proceduresRs, boolean needsClientFiltering, String db, Map procedureRowsOrderedByName, int nameIndex)
/*      */     throws SQLException
/*      */   {
/*  488 */     while (proceduresRs.next()) {
/*  489 */       boolean shouldAdd = true;
/*      */ 
/*  491 */       if (needsClientFiltering) {
/*  492 */         shouldAdd = false;
/*      */ 
/*  494 */         String procDb = proceduresRs.getString(1);
/*      */ 
/*  496 */         if ((db == null) && (procDb == null))
/*  497 */           shouldAdd = true;
/*  498 */         else if ((db != null & db.equals(procDb))) {
/*  499 */           shouldAdd = true;
/*      */         }
/*      */       }
/*      */ 
/*  503 */       if (shouldAdd) {
/*  504 */         String procedureName = proceduresRs.getString(nameIndex);
/*  505 */         byte[][] rowData = new byte[8][];
/*  506 */         rowData[0] = (catalog == null ? null : s2b(catalog));
/*  507 */         rowData[1] = null;
/*  508 */         rowData[2] = s2b(procedureName);
/*  509 */         rowData[3] = null;
/*  510 */         rowData[4] = null;
/*  511 */         rowData[5] = null;
/*  512 */         rowData[6] = null;
/*      */ 
/*  514 */         boolean isFunction = fromSelect ? "FUNCTION".equalsIgnoreCase(proceduresRs.getString("type")) : false;
/*      */ 
/*  517 */         rowData[7] = s2b(isFunction ? Integer.toString(2) : Integer.toString(0));
/*      */ 
/*  521 */         procedureRowsOrderedByName.put(procedureName, rowData);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private byte[][] convertTypeDescriptorToProcedureRow(byte[] procNameAsBytes, String paramName, boolean isOutParam, boolean isInParam, boolean isReturnParam, TypeDescriptor typeDesc)
/*      */     throws SQLException
/*      */   {
/*  530 */     byte[][] row = new byte[14][];
/*  531 */     row[0] = null;
/*  532 */     row[1] = null;
/*  533 */     row[2] = procNameAsBytes;
/*  534 */     row[3] = s2b(paramName);
/*      */ 
/*  536 */     if ((isInParam) && (isOutParam))
/*  537 */       row[4] = s2b(String.valueOf(2));
/*  538 */     else if (isInParam)
/*  539 */       row[4] = s2b(String.valueOf(1));
/*  540 */     else if (isOutParam)
/*  541 */       row[4] = s2b(String.valueOf(4));
/*  542 */     else if (isReturnParam)
/*  543 */       row[4] = s2b(String.valueOf(5));
/*      */     else {
/*  545 */       row[4] = s2b(String.valueOf(0));
/*      */     }
/*  547 */     row[5] = s2b(Short.toString(typeDesc.dataType));
/*  548 */     row[6] = s2b(typeDesc.typeName);
/*  549 */     row[7] = s2b(Integer.toString(typeDesc.columnSize));
/*  550 */     row[8] = s2b(Integer.toString(typeDesc.bufferLength));
/*  551 */     row[9] = s2b(Integer.toString(typeDesc.decimalDigits));
/*  552 */     row[10] = s2b(Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/*  554 */     switch (typeDesc.nullability) {
/*      */     case 0:
/*  556 */       row[11] = s2b(Integer.toString(0));
/*      */ 
/*  558 */       break;
/*      */     case 1:
/*  561 */       row[11] = s2b(Integer.toString(1));
/*      */ 
/*  563 */       break;
/*      */     case 2:
/*  566 */       row[11] = s2b(Integer.toString(2));
/*      */ 
/*  568 */       break;
/*      */     default:
/*  571 */       throw new SQLException("Internal error while parsing callable statement metadata (unknown nullability value fount)", "S1000");
/*      */     }
/*      */ 
/*  575 */     row[12] = null;
/*  576 */     return row;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionCausesTransactionCommit()
/*      */     throws SQLException
/*      */   {
/*  588 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean dataDefinitionIgnoredInTransactions()
/*      */     throws SQLException
/*      */   {
/*  599 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean deletesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/*  614 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean doesMaxRowSizeIncludeBlobs()
/*      */     throws SQLException
/*      */   {
/*  627 */     return true;
/*      */   }
/*      */ 
/*      */   public List extractForeignKeyForTable(ArrayList rows, java.sql.ResultSet rs, String catalog)
/*      */     throws SQLException
/*      */   {
/*  645 */     byte[][] row = new byte[3][];
/*  646 */     row[0] = rs.getBytes(1);
/*  647 */     row[1] = s2b("SUPPORTS_FK");
/*      */ 
/*  649 */     String createTableString = rs.getString(2);
/*  650 */     StringTokenizer lineTokenizer = new StringTokenizer(createTableString, "\n");
/*      */ 
/*  652 */     StringBuffer commentBuf = new StringBuffer("comment; ");
/*  653 */     boolean firstTime = true;
/*      */ 
/*  655 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/*  657 */     if (quoteChar == null) {
/*  658 */       quoteChar = "`";
/*      */     }
/*      */ 
/*  661 */     while (lineTokenizer.hasMoreTokens()) {
/*  662 */       String line = lineTokenizer.nextToken().trim();
/*      */ 
/*  664 */       String constraintName = null;
/*      */ 
/*  666 */       if (StringUtils.startsWithIgnoreCase(line, "CONSTRAINT")) {
/*  667 */         boolean usingBackTicks = true;
/*  668 */         int beginPos = line.indexOf(quoteChar);
/*      */ 
/*  670 */         if (beginPos == -1) {
/*  671 */           beginPos = line.indexOf("\"");
/*  672 */           usingBackTicks = false;
/*      */         }
/*      */ 
/*  675 */         if (beginPos != -1) {
/*  676 */           int endPos = -1;
/*      */ 
/*  678 */           if (usingBackTicks)
/*  679 */             endPos = line.indexOf(quoteChar, beginPos + 1);
/*      */           else {
/*  681 */             endPos = line.indexOf("\"", beginPos + 1);
/*      */           }
/*      */ 
/*  684 */           if (endPos != -1) {
/*  685 */             constraintName = line.substring(beginPos + 1, endPos);
/*  686 */             line = line.substring(endPos + 1, line.length()).trim();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  692 */       if (line.startsWith("FOREIGN KEY")) {
/*  693 */         if (line.endsWith(",")) {
/*  694 */           line = line.substring(0, line.length() - 1);
/*      */         }
/*      */ 
/*  697 */         char quote = this.quotedId.charAt(0);
/*      */ 
/*  699 */         int indexOfFK = line.indexOf("FOREIGN KEY");
/*      */ 
/*  701 */         String localColumnName = null;
/*  702 */         String referencedCatalogName = this.quotedId + catalog + this.quotedId;
/*  703 */         String referencedTableName = null;
/*  704 */         String referencedColumnName = null;
/*      */ 
/*  707 */         if (indexOfFK != -1) {
/*  708 */           int afterFk = indexOfFK + "FOREIGN KEY".length();
/*      */ 
/*  710 */           int indexOfRef = StringUtils.indexOfIgnoreCaseRespectQuotes(afterFk, line, "REFERENCES", quote, true);
/*      */ 
/*  712 */           if (indexOfRef != -1)
/*      */           {
/*  714 */             int indexOfParenOpen = line.indexOf('(', afterFk);
/*  715 */             int indexOfParenClose = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfParenOpen, line, ")", quote, true);
/*      */ 
/*  717 */             if ((indexOfParenOpen != -1) && (indexOfParenClose == -1));
/*  721 */             localColumnName = line.substring(indexOfParenOpen + 1, indexOfParenClose);
/*      */ 
/*  723 */             int afterRef = indexOfRef + "REFERENCES".length();
/*      */ 
/*  725 */             int referencedColumnBegin = StringUtils.indexOfIgnoreCaseRespectQuotes(afterRef, line, "(", quote, true);
/*      */ 
/*  727 */             if (referencedColumnBegin != -1) {
/*  728 */               referencedTableName = line.substring(afterRef, referencedColumnBegin);
/*      */ 
/*  730 */               int referencedColumnEnd = StringUtils.indexOfIgnoreCaseRespectQuotes(referencedColumnBegin + 1, line, ")", quote, true);
/*      */ 
/*  732 */               if (referencedColumnEnd != -1) {
/*  733 */                 referencedColumnName = line.substring(referencedColumnBegin + 1, referencedColumnEnd);
/*      */               }
/*      */ 
/*  736 */               int indexOfCatalogSep = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referencedTableName, ".", quote, true);
/*      */ 
/*  738 */               if (indexOfCatalogSep != -1) {
/*  739 */                 referencedCatalogName = referencedTableName.substring(0, indexOfCatalogSep);
/*  740 */                 referencedTableName = referencedTableName.substring(indexOfCatalogSep + 1);
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  747 */         if (!firstTime)
/*  748 */           commentBuf.append("; ");
/*      */         else {
/*  750 */           firstTime = false;
/*      */         }
/*      */ 
/*  753 */         if (constraintName != null)
/*  754 */           commentBuf.append(constraintName);
/*      */         else {
/*  756 */           commentBuf.append("not_available");
/*      */         }
/*      */ 
/*  759 */         commentBuf.append("(");
/*  760 */         commentBuf.append(localColumnName);
/*  761 */         commentBuf.append(") REFER ");
/*  762 */         commentBuf.append(referencedCatalogName);
/*  763 */         commentBuf.append("/");
/*  764 */         commentBuf.append(referencedTableName);
/*  765 */         commentBuf.append("(");
/*  766 */         commentBuf.append(referencedColumnName);
/*  767 */         commentBuf.append(")");
/*      */ 
/*  769 */         int lastParenIndex = line.lastIndexOf(")");
/*      */ 
/*  771 */         if (lastParenIndex != line.length() - 1) {
/*  772 */           String cascadeOptions = cascadeOptions = line.substring(lastParenIndex + 1);
/*      */ 
/*  774 */           commentBuf.append(" ");
/*  775 */           commentBuf.append(cascadeOptions);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  780 */     row[2] = s2b(commentBuf.toString());
/*  781 */     rows.add(row);
/*      */ 
/*  783 */     return rows;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet extractForeignKeyFromCreateTable(String catalog, String tableName)
/*      */     throws SQLException
/*      */   {
/*  804 */     ArrayList tableList = new ArrayList();
/*  805 */     java.sql.ResultSet rs = null;
/*  806 */     Statement stmt = null;
/*      */ 
/*  808 */     if (tableName != null)
/*  809 */       tableList.add(tableName);
/*      */     else {
/*      */       try {
/*  812 */         rs = getTables(catalog, "", "%", new String[] { "TABLE" });
/*      */ 
/*  814 */         while (rs.next())
/*  815 */           tableList.add(rs.getString("TABLE_NAME"));
/*      */       }
/*      */       finally {
/*  818 */         if (rs != null) {
/*  819 */           rs.close();
/*      */         }
/*      */ 
/*  822 */         rs = null;
/*      */       }
/*      */     }
/*      */ 
/*  826 */     ArrayList rows = new ArrayList();
/*  827 */     Field[] fields = new Field[3];
/*  828 */     fields[0] = new Field("", "Name", 1, 2147483647);
/*  829 */     fields[1] = new Field("", "Type", 1, 255);
/*  830 */     fields[2] = new Field("", "Comment", 1, 2147483647);
/*      */ 
/*  832 */     int numTables = tableList.size();
/*  833 */     stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/*  835 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/*  837 */     if (quoteChar == null) {
/*  838 */       quoteChar = "`";
/*      */     }
/*      */     try
/*      */     {
/*  842 */       for (int i = 0; i < numTables; i++) {
/*  843 */         String tableToExtract = (String)tableList.get(i);
/*      */ 
/*  845 */         String query = "SHOW CREATE TABLE " + quoteChar + catalog + quoteChar + "." + quoteChar + tableToExtract + quoteChar;
/*      */ 
/*  849 */         rs = stmt.executeQuery(query);
/*      */ 
/*  851 */         while (rs.next())
/*  852 */           extractForeignKeyForTable(rows, rs, catalog);
/*      */       }
/*      */     }
/*      */     finally {
/*  856 */       if (rs != null) {
/*  857 */         rs.close();
/*      */       }
/*      */ 
/*  860 */       rs = null;
/*      */ 
/*  862 */       if (stmt != null) {
/*  863 */         stmt.close();
/*      */       }
/*      */ 
/*  866 */       stmt = null;
/*      */     }
/*      */ 
/*  869 */     return buildResultSet(fields, rows);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getAttributes(String arg0, String arg1, String arg2, String arg3)
/*      */     throws SQLException
/*      */   {
/*  877 */     Field[] fields = new Field[21];
/*  878 */     fields[0] = new Field("", "TYPE_CAT", 1, 32);
/*  879 */     fields[1] = new Field("", "TYPE_SCHEM", 1, 32);
/*  880 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/*  881 */     fields[3] = new Field("", "ATTR_NAME", 1, 32);
/*  882 */     fields[4] = new Field("", "DATA_TYPE", 5, 32);
/*  883 */     fields[5] = new Field("", "ATTR_TYPE_NAME", 1, 32);
/*  884 */     fields[6] = new Field("", "ATTR_SIZE", 4, 32);
/*  885 */     fields[7] = new Field("", "DECIMAL_DIGITS", 4, 32);
/*  886 */     fields[8] = new Field("", "NUM_PREC_RADIX", 4, 32);
/*  887 */     fields[9] = new Field("", "NULLABLE ", 4, 32);
/*  888 */     fields[10] = new Field("", "REMARKS", 1, 32);
/*  889 */     fields[11] = new Field("", "ATTR_DEF", 1, 32);
/*  890 */     fields[12] = new Field("", "SQL_DATA_TYPE", 4, 32);
/*  891 */     fields[13] = new Field("", "SQL_DATETIME_SUB", 4, 32);
/*  892 */     fields[14] = new Field("", "CHAR_OCTET_LENGTH", 4, 32);
/*  893 */     fields[15] = new Field("", "ORDINAL_POSITION", 4, 32);
/*  894 */     fields[16] = new Field("", "IS_NULLABLE", 1, 32);
/*  895 */     fields[17] = new Field("", "SCOPE_CATALOG", 1, 32);
/*  896 */     fields[18] = new Field("", "SCOPE_SCHEMA", 1, 32);
/*  897 */     fields[19] = new Field("", "SCOPE_TABLE", 1, 32);
/*  898 */     fields[20] = new Field("", "SOURCE_DATA_TYPE", 5, 32);
/*      */ 
/*  900 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable)
/*      */     throws SQLException
/*      */   {
/*  951 */     if (table == null) {
/*  952 */       throw new SQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/*  956 */     Field[] fields = new Field[8];
/*  957 */     fields[0] = new Field("", "SCOPE", 5, 5);
/*  958 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/*  959 */     fields[2] = new Field("", "DATA_TYPE", 5, 32);
/*  960 */     fields[3] = new Field("", "TYPE_NAME", 1, 32);
/*  961 */     fields[4] = new Field("", "COLUMN_SIZE", 4, 10);
/*  962 */     fields[5] = new Field("", "BUFFER_LENGTH", 4, 10);
/*  963 */     fields[6] = new Field("", "DECIMAL_DIGITS", 4, 10);
/*  964 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/*  966 */     ArrayList rows = new ArrayList();
/*  967 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/*  971 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/*  973 */         void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet results = null;
/*      */           try
/*      */           {
/*  976 */             StringBuffer queryBuf = new StringBuffer("SHOW COLUMNS FROM ");
/*      */ 
/*  978 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*  979 */             queryBuf.append(this.val$table);
/*  980 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*  981 */             queryBuf.append(" FROM ");
/*  982 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*  983 */             queryBuf.append(catalogStr.toString());
/*  984 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/*  986 */             results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/*  988 */             while (results.next()) {
/*  989 */               String keyType = results.getString("Key");
/*      */ 
/*  991 */               if ((keyType != null) && 
/*  992 */                 (StringUtils.startsWithIgnoreCase(keyType, "PRI")))
/*      */               {
/*  994 */                 byte[][] rowVal = new byte[8][];
/*  995 */                 rowVal[0] = Integer.toString(2).getBytes();
/*      */ 
/*  999 */                 rowVal[1] = results.getBytes("Field");
/*      */ 
/* 1001 */                 String type = results.getString("Type");
/* 1002 */                 int size = MysqlIO.getMaxBuf();
/* 1003 */                 int decimals = 0;
/*      */ 
/* 1008 */                 if (type.indexOf("enum") != -1) {
/* 1009 */                   String temp = type.substring(type.indexOf("("), type.indexOf(")"));
/*      */ 
/* 1012 */                   StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/* 1014 */                   int maxLength = 0;
/*      */ 
/* 1016 */                   while (tokenizer.hasMoreTokens()) {
/* 1017 */                     maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */                   }
/*      */ 
/* 1022 */                   size = maxLength;
/* 1023 */                   decimals = 0;
/* 1024 */                   type = "enum";
/* 1025 */                 } else if (type.indexOf("(") != -1) {
/* 1026 */                   if (type.indexOf(",") != -1) {
/* 1027 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(",")));
/*      */ 
/* 1031 */                     decimals = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
/*      */                   }
/*      */                   else
/*      */                   {
/* 1036 */                     size = Integer.parseInt(type.substring(type.indexOf("(") + 1, type.indexOf(")")));
/*      */                   }
/*      */ 
/* 1042 */                   type = type.substring(0, type.indexOf("("));
/*      */                 }
/*      */ 
/* 1046 */                 rowVal[2] = DatabaseMetaData.access$100(DatabaseMetaData.this, String.valueOf(MysqlDefs.mysqlToJavaType(type)));
/*      */ 
/* 1048 */                 rowVal[3] = DatabaseMetaData.access$100(DatabaseMetaData.this, type);
/* 1049 */                 rowVal[4] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1051 */                 rowVal[5] = Integer.toString(size + decimals).getBytes();
/*      */ 
/* 1053 */                 rowVal[6] = Integer.toString(decimals).getBytes();
/*      */ 
/* 1055 */                 rowVal[7] = Integer.toString(1).getBytes();
/*      */ 
/* 1060 */                 this.val$rows.add(rowVal);
/*      */               }
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 1066 */             if (results != null) {
/*      */               try {
/* 1068 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 1073 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  971 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 1079 */       if (stmt != null) {
/* 1080 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 1084 */     java.sql.ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 1086 */     return results;
/*      */   }
/*      */ 
/*      */   private void getCallStmtParameterTypes(String catalog, String procName, String parameterNamePattern, List resultRows)
/*      */     throws SQLException
/*      */   {
/* 1124 */     Statement paramRetrievalStmt = null;
/* 1125 */     java.sql.ResultSet paramRetrievalRs = null;
/*      */ 
/* 1128 */     if (parameterNamePattern == null) {
/* 1129 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1130 */         parameterNamePattern = "%";
/*      */       else {
/* 1132 */         throw new SQLException("Parameter/Column name pattern can not be NULL or empty.", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1138 */     byte[] procNameAsBytes = null;
/*      */     try
/*      */     {
/* 1141 */       procNameAsBytes = procName.getBytes("UTF-8");
/*      */     } catch (UnsupportedEncodingException ueEx) {
/* 1143 */       procNameAsBytes = s2b(procName);
/*      */     }
/*      */ 
/* 1148 */     String quoteChar = getIdentifierQuoteString();
/*      */ 
/* 1150 */     String storageDefnDelims = "(" + quoteChar;
/* 1151 */     String storageDefnClosures = ")" + quoteChar;
/*      */ 
/* 1154 */     String parameterDef = null;
/*      */ 
/* 1156 */     PreparedStatement paramRetrievalPreparedStatement = null;
/*      */     try
/*      */     {
/* 1159 */       paramRetrievalStmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 1161 */       if ((this.conn.lowerCaseTableNames()) && (catalog != null) && (catalog.length() != 0))
/*      */       {
/* 1167 */         String oldCatalog = this.conn.getCatalog();
/* 1168 */         java.sql.ResultSet rs = null;
/*      */         try
/*      */         {
/* 1171 */           this.conn.setCatalog(catalog);
/* 1172 */           rs = paramRetrievalStmt.executeQuery("SELECT DATABASE()");
/* 1173 */           rs.next();
/*      */ 
/* 1175 */           catalog = rs.getString(1);
/*      */         }
/*      */         finally
/*      */         {
/* 1179 */           this.conn.setCatalog(oldCatalog);
/*      */ 
/* 1181 */           rs.close();
/*      */         }
/*      */       }
/*      */ 
/* 1185 */       int dotIndex = -1;
/*      */ 
/* 1187 */       if (!" ".equals(quoteChar)) {
/* 1188 */         dotIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procName, ".", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */       }
/*      */       else
/*      */       {
/* 1192 */         dotIndex = procName.indexOf(".");
/*      */       }
/*      */ 
/* 1195 */       String dbName = null;
/*      */ 
/* 1197 */       if ((dotIndex != -1) && (dotIndex + 1 < procName.length())) {
/* 1198 */         dbName = procName.substring(0, dotIndex);
/* 1199 */         procName = procName.substring(dotIndex + 1);
/*      */       } else {
/* 1201 */         dbName = catalog;
/*      */       }
/*      */ 
/* 1204 */       StringBuffer procNameBuf = new StringBuffer();
/*      */ 
/* 1206 */       if (dbName != null) {
/* 1207 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1208 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1211 */         procNameBuf.append(dbName);
/*      */ 
/* 1213 */         if ((!" ".equals(quoteChar)) && (!dbName.startsWith(quoteChar))) {
/* 1214 */           procNameBuf.append(quoteChar);
/*      */         }
/*      */ 
/* 1217 */         procNameBuf.append(".");
/*      */       }
/*      */ 
/* 1220 */       boolean procNameIsNotQuoted = !procName.startsWith(quoteChar);
/*      */ 
/* 1222 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1223 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1226 */       procNameBuf.append(procName);
/*      */ 
/* 1228 */       if ((!" ".equals(quoteChar)) && (procNameIsNotQuoted)) {
/* 1229 */         procNameBuf.append(quoteChar);
/*      */       }
/*      */ 
/* 1232 */       boolean parsingFunction = false;
/*      */       try
/*      */       {
/* 1235 */         paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE PROCEDURE " + procNameBuf.toString());
/*      */ 
/* 1238 */         parsingFunction = false;
/*      */       } catch (SQLException sqlEx) {
/*      */         try {
/* 1241 */           paramRetrievalRs = paramRetrievalStmt.executeQuery("SHOW CREATE FUNCTION " + procNameBuf.toString());
/*      */ 
/* 1244 */           parsingFunction = true;
/*      */         } catch (SQLException ex) {
/* 1246 */           throw sqlEx;
/*      */         }
/*      */       }
/*      */ 
/* 1250 */       if (paramRetrievalRs.next()) {
/* 1251 */         String procedureDef = parsingFunction ? paramRetrievalRs.getString("Create Function") : paramRetrievalRs.getString("Create Procedure");
/*      */ 
/* 1255 */         int openParenIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "(", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1260 */         String beforeBegin = null;
/*      */ 
/* 1263 */         int beginIndex = 0;
/*      */ 
/* 1265 */         if (!parsingFunction) {
/* 1266 */           beginIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "\nbegin", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */         }
/*      */         else
/*      */         {
/* 1272 */           int returnsIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, " RETURNS ", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1277 */           beginIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(returnsIndex, procedureDef, "\nbegin", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1282 */           if (beginIndex == -1) {
/* 1283 */             beginIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "\n", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */           }
/*      */ 
/* 1292 */           if (beginIndex == -1) {
/* 1293 */             throw new SQLException("Driver requires declaration of procedure to either contain a '\\nbegin' or '\\n' to follow argument declaration, or SELECT privilege on mysql.proc to parse column types.", "S1000");
/*      */           }
/*      */ 
/* 1298 */           String returnsDefn = procedureDef.substring(returnsIndex + "RETURNS ".length(), beginIndex);
/*      */ 
/* 1300 */           TypeDescriptor returnDescriptor = new TypeDescriptor(returnsDefn, null);
/*      */ 
/* 1303 */           resultRows.add(convertTypeDescriptorToProcedureRow(procNameAsBytes, "", false, false, true, returnDescriptor));
/*      */ 
/* 1307 */           beginIndex = returnsIndex;
/*      */         }
/*      */ 
/* 1313 */         if (beginIndex != -1) {
/* 1314 */           beforeBegin = procedureDef.substring(0, beginIndex);
/*      */         } else {
/* 1316 */           beginIndex = StringUtils.indexOfIgnoreCaseRespectQuotes(0, procedureDef, "\n", quoteChar.charAt(0), !this.conn.isNoBackslashEscapesSet());
/*      */ 
/* 1320 */           if (beginIndex != -1)
/* 1321 */             beforeBegin = procedureDef.substring(0, beginIndex);
/*      */           else {
/* 1323 */             throw new SQLException("Driver requires declaration of procedure to either contain a '\\nbegin' or '\\n' to follow argument declaration, or SELECT privilege on mysql.proc to parse column types.", "S1000");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1330 */         int endParenIndex = beforeBegin.lastIndexOf(')');
/*      */ 
/* 1332 */         if ((openParenIndex == -1) || (endParenIndex == -1))
/*      */         {
/* 1334 */           throw new SQLException("Internal error when parsing callable statement metadata", "S1000");
/*      */         }
/*      */ 
/* 1339 */         parameterDef = procedureDef.substring(openParenIndex + 1, endParenIndex);
/*      */       }
/*      */     }
/*      */     finally {
/* 1343 */       SQLException sqlExRethrow = null;
/*      */ 
/* 1345 */       if (paramRetrievalRs != null) {
/*      */         try {
/* 1347 */           paramRetrievalRs.close();
/*      */         } catch (SQLException sqlEx) {
/* 1349 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1352 */         paramRetrievalRs = null;
/*      */       }
/*      */ 
/* 1355 */       if (paramRetrievalPreparedStatement != null) {
/*      */         try {
/* 1357 */           paramRetrievalPreparedStatement.close();
/*      */         } catch (SQLException sqlEx) {
/* 1359 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1362 */         paramRetrievalPreparedStatement = null;
/*      */       }
/*      */ 
/* 1365 */       if (paramRetrievalStmt != null) {
/*      */         try {
/* 1367 */           paramRetrievalStmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 1369 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/* 1372 */         paramRetrievalStmt = null;
/*      */       }
/*      */ 
/* 1375 */       if (sqlExRethrow != null) {
/* 1376 */         throw sqlExRethrow;
/*      */       }
/*      */     }
/*      */ 
/* 1380 */     if (parameterDef != null) {
/* 1381 */       List parseList = StringUtils.split(parameterDef, ",", storageDefnDelims, storageDefnClosures, true);
/*      */ 
/* 1384 */       int parseListLen = parseList.size();
/*      */ 
/* 1386 */       for (int i = 0; i < parseListLen; i++) {
/* 1387 */         String declaration = (String)parseList.get(i);
/*      */ 
/* 1389 */         StringTokenizer declarationTok = new StringTokenizer(declaration, " \t");
/*      */ 
/* 1392 */         String paramName = null;
/* 1393 */         boolean isOutParam = false;
/* 1394 */         boolean isInParam = false;
/*      */ 
/* 1396 */         if (declarationTok.hasMoreTokens()) {
/* 1397 */           String possibleParamName = declarationTok.nextToken();
/*      */ 
/* 1399 */           if (possibleParamName.equalsIgnoreCase("OUT")) {
/* 1400 */             isOutParam = true;
/*      */ 
/* 1402 */             if (declarationTok.hasMoreTokens())
/* 1403 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1405 */               throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */ 
/*      */           }
/* 1409 */           else if (possibleParamName.equalsIgnoreCase("INOUT")) {
/* 1410 */             isOutParam = true;
/* 1411 */             isInParam = true;
/*      */ 
/* 1413 */             if (declarationTok.hasMoreTokens())
/* 1414 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1416 */               throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */ 
/*      */           }
/* 1420 */           else if (possibleParamName.equalsIgnoreCase("IN")) {
/* 1421 */             isOutParam = false;
/* 1422 */             isInParam = true;
/*      */ 
/* 1424 */             if (declarationTok.hasMoreTokens())
/* 1425 */               paramName = declarationTok.nextToken();
/*      */             else {
/* 1427 */               throw new SQLException("Internal error when parsing callable statement metadata (missing parameter name)", "S1000");
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/* 1432 */             isOutParam = false;
/* 1433 */             isInParam = true;
/*      */ 
/* 1435 */             paramName = possibleParamName;
/*      */           }
/*      */ 
/* 1438 */           TypeDescriptor typeDesc = null;
/*      */ 
/* 1440 */           if (declarationTok.hasMoreTokens()) {
/* 1441 */             StringBuffer typeInfoBuf = new StringBuffer(declarationTok.nextToken());
/*      */ 
/* 1444 */             while (declarationTok.hasMoreTokens()) {
/* 1445 */               typeInfoBuf.append(declarationTok.nextToken());
/*      */             }
/*      */ 
/* 1448 */             String typeInfo = typeInfoBuf.toString();
/*      */ 
/* 1450 */             typeDesc = new TypeDescriptor(typeInfo, null);
/*      */           } else {
/* 1452 */             throw new SQLException("Internal error when parsing callable statement metadata (missing parameter type)", "S1000");
/*      */           }
/*      */ 
/* 1457 */           int wildCompareRes = StringUtils.wildCompare(paramName, parameterNamePattern);
/*      */ 
/* 1460 */           if (wildCompareRes != -1) {
/* 1461 */             byte[][] row = convertTypeDescriptorToProcedureRow(procNameAsBytes, paramName, isOutParam, isInParam, false, typeDesc);
/*      */ 
/* 1465 */             resultRows.add(row);
/*      */           }
/*      */         } else {
/* 1468 */           throw new SQLException("Internal error when parsing callable statement metadata (unknown output from 'SHOW CREATE PROCEDURE')", "S1000");
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getCascadeDeleteOption(String cascadeOptions)
/*      */   {
/* 1489 */     int onDeletePos = cascadeOptions.indexOf("ON DELETE");
/*      */ 
/* 1491 */     if (onDeletePos != -1) {
/* 1492 */       String deleteOptions = cascadeOptions.substring(onDeletePos, cascadeOptions.length());
/*      */ 
/* 1495 */       if (deleteOptions.startsWith("ON DELETE CASCADE"))
/* 1496 */         return 0;
/* 1497 */       if (deleteOptions.startsWith("ON DELETE SET NULL"))
/* 1498 */         return 2;
/* 1499 */       if (deleteOptions.startsWith("ON DELETE RESTRICT"))
/* 1500 */         return 1;
/* 1501 */       if (deleteOptions.startsWith("ON DELETE NO ACTION")) {
/* 1502 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 1506 */     return 3;
/*      */   }
/*      */ 
/*      */   private int getCascadeUpdateOption(String cascadeOptions)
/*      */   {
/* 1518 */     int onUpdatePos = cascadeOptions.indexOf("ON UPDATE");
/*      */ 
/* 1520 */     if (onUpdatePos != -1) {
/* 1521 */       String updateOptions = cascadeOptions.substring(onUpdatePos, cascadeOptions.length());
/*      */ 
/* 1524 */       if (updateOptions.startsWith("ON UPDATE CASCADE"))
/* 1525 */         return 0;
/* 1526 */       if (updateOptions.startsWith("ON UPDATE SET NULL"))
/* 1527 */         return 2;
/* 1528 */       if (updateOptions.startsWith("ON UPDATE RESTRICT"))
/* 1529 */         return 1;
/* 1530 */       if (updateOptions.startsWith("ON UPDATE NO ACTION")) {
/* 1531 */         return 3;
/*      */       }
/*      */     }
/*      */ 
/* 1535 */     return 3;
/*      */   }
/*      */ 
/*      */   protected IteratorWithCleanup getCatalogIterator(String catalogSpec)
/*      */     throws SQLException
/*      */   {
/*      */     IteratorWithCleanup allCatalogsIter;
/*      */     IteratorWithCleanup allCatalogsIter;
/* 1541 */     if (catalogSpec != null)
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 1542 */       if (!catalogSpec.equals("")) {
/* 1543 */         allCatalogsIter = new SingleStringIterator(catalogSpec);
/*      */       }
/*      */       else
/* 1546 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */     }
/*      */     else
/*      */     {
/*      */       IteratorWithCleanup allCatalogsIter;
/* 1548 */       if (this.conn.getNullCatalogMeansCurrent())
/* 1549 */         allCatalogsIter = new SingleStringIterator(this.database);
/*      */       else {
/* 1551 */         allCatalogsIter = new ResultSetIterator(getCatalogs(), 1);
/*      */       }
/*      */     }
/* 1554 */     return allCatalogsIter;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getCatalogs()
/*      */     throws SQLException
/*      */   {
/* 1573 */     java.sql.ResultSet results = null;
/* 1574 */     Statement stmt = null;
/*      */     try
/*      */     {
/* 1577 */       stmt = this.conn.createStatement();
/* 1578 */       stmt.setEscapeProcessing(false);
/* 1579 */       results = stmt.executeQuery("SHOW DATABASES");
/*      */ 
/* 1581 */       ResultSetMetaData resultsMD = results.getMetaData();
/* 1582 */       Field[] fields = new Field[1];
/* 1583 */       fields[0] = new Field("", "TABLE_CAT", 12, resultsMD.getColumnDisplaySize(1));
/*      */ 
/* 1586 */       ArrayList tuples = new ArrayList();
/*      */ 
/* 1588 */       while (results.next()) {
/* 1589 */         rowVal = new byte[1][];
/* 1590 */         rowVal[0] = results.getBytes(1);
/* 1591 */         tuples.add(rowVal);
/*      */       }
/*      */ 
/* 1594 */       rowVal = buildResultSet(fields, tuples);
/*      */     }
/*      */     finally
/*      */     {
/*      */       byte[][] rowVal;
/* 1596 */       if (results != null) {
/*      */         try {
/* 1598 */           results.close();
/*      */         } catch (SQLException sqlEx) {
/* 1600 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 1603 */         results = null;
/*      */       }
/*      */ 
/* 1606 */       if (stmt != null) {
/*      */         try {
/* 1608 */           stmt.close();
/*      */         } catch (SQLException sqlEx) {
/* 1610 */           AssertionFailedException.shouldNotHappen(sqlEx);
/*      */         }
/*      */ 
/* 1613 */         stmt = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCatalogSeparator()
/*      */     throws SQLException
/*      */   {
/* 1626 */     return ".";
/*      */   }
/*      */ 
/*      */   public String getCatalogTerm()
/*      */     throws SQLException
/*      */   {
/* 1637 */     return "database";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1684 */     Field[] fields = new Field[8];
/* 1685 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 1686 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 1687 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 1688 */     fields[3] = new Field("", "COLUMN_NAME", 1, 64);
/* 1689 */     fields[4] = new Field("", "GRANTOR", 1, 77);
/* 1690 */     fields[5] = new Field("", "GRANTEE", 1, 77);
/* 1691 */     fields[6] = new Field("", "PRIVILEGE", 1, 64);
/* 1692 */     fields[7] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 1694 */     StringBuffer grantQuery = new StringBuffer("SELECT c.host, c.db, t.grantor, c.user, c.table_name, c.column_name, c.column_priv from mysql.columns_priv c, mysql.tables_priv t where c.host = t.host and c.db = t.db and c.table_name = t.table_name ");
/*      */ 
/* 1701 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 1702 */       grantQuery.append(" AND c.db='");
/* 1703 */       grantQuery.append(catalog);
/* 1704 */       grantQuery.append("' ");
/*      */     }
/*      */ 
/* 1708 */     grantQuery.append(" AND c.table_name ='");
/* 1709 */     grantQuery.append(table);
/* 1710 */     grantQuery.append("' AND c.column_name like '");
/* 1711 */     grantQuery.append(columnNamePattern);
/* 1712 */     grantQuery.append("'");
/*      */ 
/* 1714 */     Statement stmt = null;
/* 1715 */     java.sql.ResultSet results = null;
/* 1716 */     ArrayList grantRows = new ArrayList();
/*      */     try
/*      */     {
/* 1719 */       stmt = this.conn.createStatement();
/* 1720 */       stmt.setEscapeProcessing(false);
/* 1721 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 1723 */       while (results.next()) {
/* 1724 */         String host = results.getString(1);
/* 1725 */         String db = results.getString(2);
/* 1726 */         String grantor = results.getString(3);
/* 1727 */         String user = results.getString(4);
/*      */ 
/* 1729 */         if ((user == null) || (user.length() == 0)) {
/* 1730 */           user = "%";
/*      */         }
/*      */ 
/* 1733 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 1735 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 1736 */           fullUser.append("@");
/* 1737 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 1740 */         String columnName = results.getString(6);
/* 1741 */         String allPrivileges = results.getString(7);
/*      */ 
/* 1743 */         if (allPrivileges != null) {
/* 1744 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 1746 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 1748 */           while (st.hasMoreTokens()) {
/* 1749 */             String privilege = st.nextToken().trim();
/* 1750 */             byte[][] tuple = new byte[8][];
/* 1751 */             tuple[0] = s2b(db);
/* 1752 */             tuple[1] = null;
/* 1753 */             tuple[2] = s2b(table);
/* 1754 */             tuple[3] = s2b(columnName);
/*      */ 
/* 1756 */             if (grantor != null)
/* 1757 */               tuple[4] = s2b(grantor);
/*      */             else {
/* 1759 */               tuple[4] = null;
/*      */             }
/*      */ 
/* 1762 */             tuple[5] = s2b(fullUser.toString());
/* 1763 */             tuple[6] = s2b(privilege);
/* 1764 */             tuple[7] = null;
/* 1765 */             grantRows.add(tuple);
/*      */           }
/*      */         }
/*      */       }
/*      */     } finally {
/* 1770 */       if (results != null) {
/*      */         try {
/* 1772 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 1777 */         results = null;
/*      */       }
/*      */ 
/* 1780 */       if (stmt != null) {
/*      */         try {
/* 1782 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 1787 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 1791 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 1855 */     if (columnNamePattern == null) {
/* 1856 */       if (this.conn.getNullNamePatternMatchesAll())
/* 1857 */         columnNamePattern = "%";
/*      */       else {
/* 1859 */         throw new SQLException("Column name pattern can not be NULL or empty.", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1865 */     String colPattern = columnNamePattern;
/*      */ 
/* 1867 */     Field[] fields = new Field[18];
/* 1868 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 1869 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 1870 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 1871 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 1872 */     fields[4] = new Field("", "DATA_TYPE", 5, 5);
/* 1873 */     fields[5] = new Field("", "TYPE_NAME", 1, 16);
/* 1874 */     fields[6] = new Field("", "COLUMN_SIZE", 4, Integer.toString(2147483647).length());
/*      */ 
/* 1876 */     fields[7] = new Field("", "BUFFER_LENGTH", 4, 10);
/* 1877 */     fields[8] = new Field("", "DECIMAL_DIGITS", 4, 10);
/* 1878 */     fields[9] = new Field("", "NUM_PREC_RADIX", 4, 10);
/* 1879 */     fields[10] = new Field("", "NULLABLE", 4, 10);
/* 1880 */     fields[11] = new Field("", "REMARKS", 1, 0);
/* 1881 */     fields[12] = new Field("", "COLUMN_DEF", 1, 0);
/* 1882 */     fields[13] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 1883 */     fields[14] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 1884 */     fields[15] = new Field("", "CHAR_OCTET_LENGTH", 4, Integer.toString(2147483647).length());
/*      */ 
/* 1886 */     fields[16] = new Field("", "ORDINAL_POSITION", 4, 10);
/* 1887 */     fields[17] = new Field("", "IS_NULLABLE", 1, 3);
/*      */ 
/* 1889 */     ArrayList rows = new ArrayList();
/* 1890 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 1894 */       new IterateBlock(getCatalogIterator(catalog), tableNamePattern, catalog, schemaPattern, colPattern, stmt, rows) { private final String val$tableNamePattern;
/*      */         private final String val$catalog;
/*      */         private final String val$schemaPattern;
/*      */         private final String val$colPattern;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 1897 */         void forEach(Object catalogStr) throws SQLException { ArrayList tableNameList = new ArrayList();
/*      */ 
/* 1899 */           if (this.val$tableNamePattern == null)
/*      */           {
/* 1901 */             java.sql.ResultSet tables = null;
/*      */             try
/*      */             {
/* 1904 */               tables = DatabaseMetaData.this.getTables(this.val$catalog, this.val$schemaPattern, "%", new String[0]);
/*      */ 
/* 1907 */               while (tables.next()) {
/* 1908 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 1910 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 1913 */               if (tables != null) {
/*      */                 try {
/* 1915 */                   tables.close();
/*      */                 } catch (Exception sqlEx) {
/* 1917 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 1921 */                 tables = null;
/*      */               }
/*      */             }
/*      */           } else {
/* 1925 */             java.sql.ResultSet tables = null;
/*      */             try
/*      */             {
/* 1928 */               tables = DatabaseMetaData.this.getTables(this.val$catalog, this.val$schemaPattern, this.val$tableNamePattern, new String[0]);
/*      */ 
/* 1931 */               while (tables.next()) {
/* 1932 */                 String tableNameFromList = tables.getString("TABLE_NAME");
/*      */ 
/* 1934 */                 tableNameList.add(tableNameFromList);
/*      */               }
/*      */             } finally {
/* 1937 */               if (tables != null) {
/*      */                 try {
/* 1939 */                   tables.close();
/*      */                 } catch (SQLException sqlEx) {
/* 1941 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 1945 */                 tables = null;
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1950 */           Iterator tableNames = tableNameList.iterator();
/*      */ 
/* 1952 */           while (tableNames.hasNext()) {
/* 1953 */             String tableName = (String)tableNames.next();
/*      */ 
/* 1955 */             java.sql.ResultSet results = null;
/*      */             try
/*      */             {
/* 1958 */               StringBuffer queryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 1960 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 1961 */                 queryBuf.append("FULL ");
/*      */               }
/*      */ 
/* 1964 */               queryBuf.append("COLUMNS FROM ");
/* 1965 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1966 */               queryBuf.append(tableName);
/* 1967 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1968 */               queryBuf.append(" FROM ");
/* 1969 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1970 */               queryBuf.append(catalogStr.toString());
/* 1971 */               queryBuf.append(DatabaseMetaData.this.quotedId);
/* 1972 */               queryBuf.append(" LIKE '");
/* 1973 */               queryBuf.append(this.val$colPattern);
/* 1974 */               queryBuf.append("'");
/*      */ 
/* 1981 */               boolean fixUpOrdinalsRequired = false;
/* 1982 */               Object ordinalFixUpMap = null;
/*      */ 
/* 1984 */               if (!this.val$colPattern.equals("%")) {
/* 1985 */                 fixUpOrdinalsRequired = true;
/*      */ 
/* 1987 */                 StringBuffer fullColumnQueryBuf = new StringBuffer("SHOW ");
/*      */ 
/* 1990 */                 if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 1991 */                   fullColumnQueryBuf.append("FULL ");
/*      */                 }
/*      */ 
/* 1994 */                 fullColumnQueryBuf.append("COLUMNS FROM ");
/* 1995 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 1996 */                 fullColumnQueryBuf.append(tableName);
/* 1997 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 1998 */                 fullColumnQueryBuf.append(" FROM ");
/* 1999 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/* 2000 */                 fullColumnQueryBuf.append(catalogStr.toString());
/*      */ 
/* 2002 */                 fullColumnQueryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2004 */                 results = this.val$stmt.executeQuery(fullColumnQueryBuf.toString());
/*      */ 
/* 2007 */                 ordinalFixUpMap = new HashMap();
/*      */ 
/* 2009 */                 int fullOrdinalPos = 1;
/*      */ 
/* 2011 */                 while (results.next()) {
/* 2012 */                   String fullOrdColName = results.getString("Field");
/*      */ 
/* 2015 */                   ((Map)ordinalFixUpMap).put(fullOrdColName, new Integer(fullOrdinalPos++));
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/* 2020 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 2022 */               int ordPos = 1;
/*      */ 
/* 2024 */               while (results.next()) {
/* 2025 */                 byte[][] rowVal = new byte[18][];
/* 2026 */                 rowVal[0] = DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$catalog);
/* 2027 */                 rowVal[1] = null;
/*      */ 
/* 2030 */                 rowVal[2] = DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$tableNamePattern);
/* 2031 */                 rowVal[3] = results.getBytes("Field");
/*      */ 
/* 2033 */                 DatabaseMetaData.TypeDescriptor typeDesc = new DatabaseMetaData.TypeDescriptor(DatabaseMetaData.this, results.getString("Type"), results.getString("Null"));
/*      */ 
/* 2037 */                 rowVal[4] = Short.toString(typeDesc.dataType).getBytes();
/*      */ 
/* 2041 */                 rowVal[5] = DatabaseMetaData.access$100(DatabaseMetaData.this, typeDesc.typeName);
/*      */ 
/* 2043 */                 rowVal[6] = DatabaseMetaData.access$100(DatabaseMetaData.this, Integer.toString(typeDesc.columnSize));
/*      */ 
/* 2045 */                 rowVal[7] = DatabaseMetaData.access$100(DatabaseMetaData.this, Integer.toString(typeDesc.bufferLength));
/*      */ 
/* 2047 */                 rowVal[8] = DatabaseMetaData.access$100(DatabaseMetaData.this, Integer.toString(typeDesc.decimalDigits));
/*      */ 
/* 2049 */                 rowVal[9] = DatabaseMetaData.access$100(DatabaseMetaData.this, Integer.toString(typeDesc.numPrecRadix));
/*      */ 
/* 2051 */                 rowVal[10] = DatabaseMetaData.access$100(DatabaseMetaData.this, Integer.toString(typeDesc.nullability));
/*      */                 try
/*      */                 {
/* 2062 */                   if (DatabaseMetaData.this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 2063 */                     rowVal[11] = results.getBytes("Comment");
/*      */                   }
/*      */                   else
/* 2066 */                     rowVal[11] = results.getBytes("Extra");
/*      */                 }
/*      */                 catch (Exception E) {
/* 2069 */                   rowVal[11] = new byte[0];
/*      */                 }
/*      */ 
/* 2073 */                 rowVal[12] = results.getBytes("Default");
/*      */ 
/* 2075 */                 rowVal[13] = { 48 };
/* 2076 */                 rowVal[14] = { 48 };
/* 2077 */                 rowVal[15] = rowVal[6];
/*      */ 
/* 2080 */                 if (!fixUpOrdinalsRequired) {
/* 2081 */                   rowVal[16] = Integer.toString(ordPos++).getBytes();
/*      */                 }
/*      */                 else {
/* 2084 */                   String origColName = results.getString("Field");
/*      */ 
/* 2086 */                   Integer realOrdinal = (Integer)((Map)ordinalFixUpMap).get(origColName);
/*      */ 
/* 2089 */                   if (realOrdinal != null) {
/* 2090 */                     rowVal[16] = realOrdinal.toString().getBytes();
/*      */                   }
/*      */                   else {
/* 2093 */                     throw new SQLException("Can not find column in full column list to determine true ordinal position.", "S1000");
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 2099 */                 rowVal[17] = DatabaseMetaData.access$100(DatabaseMetaData.this, typeDesc.isNullable);
/*      */ 
/* 2101 */                 this.val$rows.add(rowVal);
/*      */               }
/*      */             } finally {
/* 2104 */               if (results != null) {
/*      */                 try {
/* 2106 */                   results.close();
/*      */                 }
/*      */                 catch (Exception ex)
/*      */                 {
/*      */                 }
/* 2111 */                 results = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1894 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 2118 */       if (stmt != null) {
/* 2119 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 2123 */     java.sql.ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 2125 */     return results;
/*      */   }
/*      */ 
/*      */   public java.sql.Connection getConnection()
/*      */     throws SQLException
/*      */   {
/* 2136 */     return this.conn;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
/*      */     throws SQLException
/*      */   {
/* 2210 */     if (primaryTable == null) {
/* 2211 */       throw new SQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/* 2215 */     Field[] fields = new Field[14];
/* 2216 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2217 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2218 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2219 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2220 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2221 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2222 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2223 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2224 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2225 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2226 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2227 */     fields[11] = new Field("", "FK_NAME", 1, 0);
/* 2228 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2229 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */ 
/* 2231 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 2233 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2235 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 2239 */         new IterateBlock(getCatalogIterator(foreignCatalog), stmt, foreignTable, primaryTable, foreignCatalog, foreignSchema, primaryCatalog, primarySchema, tuples) { private final Statement val$stmt;
/*      */           private final String val$foreignTable;
/*      */           private final String val$primaryTable;
/*      */           private final String val$foreignCatalog;
/*      */           private final String val$foreignSchema;
/*      */           private final String val$primaryCatalog;
/*      */           private final String val$primarySchema;
/*      */           private final ArrayList val$tuples;
/*      */ 
/* 2242 */           void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 2249 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50)) {
/* 2250 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */               }
/*      */               else {
/* 2253 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 2255 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2256 */                 queryBuf.append(catalogStr.toString());
/* 2257 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2259 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 2263 */               String foreignTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$foreignTable);
/* 2264 */               String primaryTableWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$primaryTable);
/*      */ 
/* 2272 */               while (fkresults.next()) {
/* 2273 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 2275 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 2279 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 2282 */                   if (comment != null) {
/* 2283 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */                     String dummy;
/* 2286 */                     if (commentTokens.hasMoreTokens()) {
/* 2287 */                       dummy = commentTokens.nextToken();
/*      */                     }
/*      */ 
/* 2292 */                     while (commentTokens.hasMoreTokens()) {
/* 2293 */                       String keys = commentTokens.nextToken();
/*      */ 
/* 2295 */                       DatabaseMetaData.LocalAndReferencedColumns parsedInfo = DatabaseMetaData.this.parseTableStatusIntoLocalAndReferencedColumns(keys);
/*      */ 
/* 2299 */                       int keySeq = 0;
/*      */ 
/* 2301 */                       Iterator referencingColumns = parsedInfo.localColumnsList.iterator();
/* 2302 */                       Iterator referencedColumns = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 2304 */                       while (referencingColumns.hasNext()) {
/* 2305 */                         String referencingColumn = DatabaseMetaData.this.removeQuotedId(referencingColumns.next().toString());
/*      */ 
/* 2310 */                         byte[][] tuple = new byte[14][];
/* 2311 */                         tuple[4] = (this.val$foreignCatalog == null ? null : DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$foreignCatalog));
/*      */ 
/* 2313 */                         tuple[5] = (this.val$foreignSchema == null ? null : DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$foreignSchema));
/*      */ 
/* 2315 */                         String dummy = fkresults.getString("Name");
/*      */ 
/* 2318 */                         if (dummy.compareTo(foreignTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2323 */                         tuple[6] = DatabaseMetaData.access$100(DatabaseMetaData.this, dummy);
/*      */ 
/* 2325 */                         tuple[7] = DatabaseMetaData.access$100(DatabaseMetaData.this, referencingColumn);
/* 2326 */                         tuple[0] = (this.val$primaryCatalog == null ? null : DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$primaryCatalog));
/*      */ 
/* 2328 */                         tuple[1] = (this.val$primarySchema == null ? null : DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$primarySchema));
/*      */ 
/* 2334 */                         if (parsedInfo.referencedTable.compareTo(primaryTableWithCase) != 0)
/*      */                         {
/*      */                           continue;
/*      */                         }
/*      */ 
/* 2339 */                         tuple[2] = DatabaseMetaData.access$100(DatabaseMetaData.this, parsedInfo.referencedTable);
/* 2340 */                         tuple[3] = DatabaseMetaData.access$100(DatabaseMetaData.this, DatabaseMetaData.access$400(DatabaseMetaData.this, referencedColumns.next().toString()));
/* 2341 */                         tuple[8] = Integer.toString(keySeq).getBytes();
/*      */ 
/* 2344 */                         int[] actions = DatabaseMetaData.this.getForeignKeyActions(keys);
/*      */ 
/* 2346 */                         tuple[9] = Integer.toString(actions[1]).getBytes();
/*      */ 
/* 2348 */                         tuple[10] = Integer.toString(actions[0]).getBytes();
/*      */ 
/* 2350 */                         tuple[11] = null;
/* 2351 */                         tuple[12] = null;
/* 2352 */                         tuple[13] = Integer.toString(7).getBytes();
/*      */ 
/* 2356 */                         this.val$tuples.add(tuple);
/* 2357 */                         keySeq++;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally {
/* 2365 */               if (fkresults != null) {
/*      */                 try {
/* 2367 */                   fkresults.close();
/*      */                 } catch (Exception sqlEx) {
/* 2369 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2373 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2239 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 2379 */         if (stmt != null) {
/* 2380 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2385 */     java.sql.ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 2387 */     return results;
/*      */   }
/*      */ 
/*      */   public int getDatabaseMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 2394 */     return this.conn.getServerMajorVersion();
/*      */   }
/*      */ 
/*      */   public int getDatabaseMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 2401 */     return this.conn.getServerMinorVersion();
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductName()
/*      */     throws SQLException
/*      */   {
/* 2412 */     return "MySQL";
/*      */   }
/*      */ 
/*      */   public String getDatabaseProductVersion()
/*      */     throws SQLException
/*      */   {
/* 2423 */     return this.conn.getServerVersion();
/*      */   }
/*      */ 
/*      */   public int getDefaultTransactionIsolation()
/*      */     throws SQLException
/*      */   {
/* 2436 */     if (this.conn.supportsIsolationLevel()) {
/* 2437 */       return 2;
/*      */     }
/*      */ 
/* 2440 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getDriverMajorVersion()
/*      */   {
/* 2449 */     return NonRegisteringDriver.getMajorVersionInternal();
/*      */   }
/*      */ 
/*      */   public int getDriverMinorVersion()
/*      */   {
/* 2458 */     return NonRegisteringDriver.getMinorVersionInternal();
/*      */   }
/*      */ 
/*      */   public String getDriverName()
/*      */     throws SQLException
/*      */   {
/* 2469 */     return "MySQL-AB JDBC Driver";
/*      */   }
/*      */ 
/*      */   public String getDriverVersion()
/*      */     throws SQLException
/*      */   {
/* 2480 */     return "mysql-connector-java-3.1.11 ( $Date: 2005-09-21 18:20:03 +0000 (Wed, 21 Sep 2005) $, $Revision: 4287 $ )";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getExportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 2544 */     if (table == null) {
/* 2545 */       throw new SQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/* 2549 */     Field[] fields = new Field[14];
/* 2550 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2551 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2552 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2553 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2554 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2555 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2556 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2557 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2558 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2559 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2560 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2561 */     fields[11] = new Field("", "FK_NAME", 1, 255);
/* 2562 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2563 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */ 
/* 2565 */     ArrayList rows = new ArrayList();
/*      */ 
/* 2567 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2569 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 2573 */         new IterateBlock(getCatalogIterator(catalog), stmt, table, rows) { private final Statement val$stmt;
/*      */           private final String val$table;
/*      */           private final ArrayList val$rows;
/*      */ 
/* 2575 */           void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 2582 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 2585 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), null);
/*      */               }
/*      */               else {
/* 2588 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS FROM ");
/*      */ 
/* 2590 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2591 */                 queryBuf.append(catalogStr.toString());
/* 2592 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 2594 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 2599 */               String tableNameWithCase = DatabaseMetaData.this.getTableNameWithCase(this.val$table);
/*      */ 
/* 2605 */               while (fkresults.next()) {
/* 2606 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 2608 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 2612 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 2615 */                   if (comment != null) {
/* 2616 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 2619 */                     if (commentTokens.hasMoreTokens()) {
/* 2620 */                       commentTokens.nextToken();
/*      */ 
/* 2625 */                       while (commentTokens.hasMoreTokens()) {
/* 2626 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 2628 */                         DatabaseMetaData.this.getExportKeyResults(catalogStr.toString(), tableNameWithCase, keys, this.val$rows, fkresults.getString("Name"));
/*      */                       }
/*      */ 
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*      */             }
/*      */             finally
/*      */             {
/* 2642 */               if (fkresults != null) {
/*      */                 try {
/* 2644 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 2646 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2650 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2573 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 2656 */         if (stmt != null) {
/* 2657 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2662 */     java.sql.ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 2664 */     return results;
/*      */   }
/*      */ 
/*      */   private void getExportKeyResults(String catalog, String exportingTable, String keysComment, List tuples, String fkTableName)
/*      */     throws SQLException
/*      */   {
/* 2688 */     getResultsImpl(catalog, exportingTable, keysComment, tuples, fkTableName, true);
/*      */   }
/*      */ 
/*      */   public String getExtraNameCharacters()
/*      */     throws SQLException
/*      */   {
/* 2701 */     return "#@";
/*      */   }
/*      */ 
/*      */   private int[] getForeignKeyActions(String commentString)
/*      */   {
/* 2714 */     int[] actions = { 3, 3 };
/*      */ 
/* 2718 */     int lastParenIndex = commentString.lastIndexOf(")");
/*      */ 
/* 2720 */     if (lastParenIndex != commentString.length() - 1) {
/* 2721 */       String cascadeOptions = commentString.substring(lastParenIndex + 1).trim().toUpperCase(Locale.ENGLISH);
/*      */ 
/* 2724 */       actions[0] = getCascadeDeleteOption(cascadeOptions);
/* 2725 */       actions[1] = getCascadeUpdateOption(cascadeOptions);
/*      */     }
/*      */ 
/* 2728 */     return actions;
/*      */   }
/*      */ 
/*      */   public String getIdentifierQuoteString()
/*      */     throws SQLException
/*      */   {
/* 2741 */     if (this.conn.supportsQuotedIdentifiers()) {
/* 2742 */       if (!this.conn.useAnsiQuotedIdentifiers()) {
/* 2743 */         return "`";
/*      */       }
/*      */ 
/* 2746 */       return "\"";
/*      */     }
/*      */ 
/* 2749 */     return " ";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getImportedKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 2813 */     if (table == null) {
/* 2814 */       throw new SQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/* 2818 */     Field[] fields = new Field[14];
/* 2819 */     fields[0] = new Field("", "PKTABLE_CAT", 1, 255);
/* 2820 */     fields[1] = new Field("", "PKTABLE_SCHEM", 1, 0);
/* 2821 */     fields[2] = new Field("", "PKTABLE_NAME", 1, 255);
/* 2822 */     fields[3] = new Field("", "PKCOLUMN_NAME", 1, 32);
/* 2823 */     fields[4] = new Field("", "FKTABLE_CAT", 1, 255);
/* 2824 */     fields[5] = new Field("", "FKTABLE_SCHEM", 1, 0);
/* 2825 */     fields[6] = new Field("", "FKTABLE_NAME", 1, 255);
/* 2826 */     fields[7] = new Field("", "FKCOLUMN_NAME", 1, 32);
/* 2827 */     fields[8] = new Field("", "KEY_SEQ", 5, 2);
/* 2828 */     fields[9] = new Field("", "UPDATE_RULE", 5, 2);
/* 2829 */     fields[10] = new Field("", "DELETE_RULE", 5, 2);
/* 2830 */     fields[11] = new Field("", "FK_NAME", 1, 255);
/* 2831 */     fields[12] = new Field("", "PK_NAME", 1, 0);
/* 2832 */     fields[13] = new Field("", "DEFERRABILITY", 4, 2);
/*      */ 
/* 2834 */     ArrayList rows = new ArrayList();
/*      */ 
/* 2836 */     if (this.conn.versionMeetsMinimum(3, 23, 0))
/*      */     {
/* 2838 */       Statement stmt = this.conn.getMetadataSafeStatement();
/*      */       try
/*      */       {
/* 2842 */         new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */           private final Statement val$stmt;
/*      */           private final ArrayList val$rows;
/*      */ 
/* 2844 */           void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet fkresults = null;
/*      */             try
/*      */             {
/* 2851 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(3, 23, 50))
/*      */               {
/* 2854 */                 fkresults = DatabaseMetaData.this.extractForeignKeyFromCreateTable(catalogStr.toString(), this.val$table);
/*      */               }
/*      */               else {
/* 2857 */                 StringBuffer queryBuf = new StringBuffer("SHOW TABLE STATUS ");
/*      */ 
/* 2859 */                 queryBuf.append(" FROM ");
/* 2860 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2861 */                 queryBuf.append(catalogStr.toString());
/* 2862 */                 queryBuf.append(DatabaseMetaData.this.quotedId);
/* 2863 */                 queryBuf.append(" LIKE '");
/* 2864 */                 queryBuf.append(this.val$table);
/* 2865 */                 queryBuf.append("'");
/*      */ 
/* 2867 */                 fkresults = this.val$stmt.executeQuery(queryBuf.toString());
/*      */               }
/*      */ 
/* 2875 */               while (fkresults.next()) {
/* 2876 */                 String tableType = fkresults.getString("Type");
/*      */ 
/* 2878 */                 if ((tableType != null) && ((tableType.equalsIgnoreCase("innodb")) || (tableType.equalsIgnoreCase("SUPPORTS_FK"))))
/*      */                 {
/* 2882 */                   String comment = fkresults.getString("Comment").trim();
/*      */ 
/* 2885 */                   if (comment != null) {
/* 2886 */                     StringTokenizer commentTokens = new StringTokenizer(comment, ";", false);
/*      */ 
/* 2889 */                     if (commentTokens.hasMoreTokens()) {
/* 2890 */                       commentTokens.nextToken();
/*      */ 
/* 2895 */                       while (commentTokens.hasMoreTokens()) {
/* 2896 */                         String keys = commentTokens.nextToken();
/*      */ 
/* 2898 */                         DatabaseMetaData.this.getImportKeyResults(catalogStr.toString(), this.val$table, keys, this.val$rows);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */             finally
/*      */             {
/* 2907 */               if (fkresults != null) {
/*      */                 try {
/* 2909 */                   fkresults.close();
/*      */                 } catch (SQLException sqlEx) {
/* 2911 */                   AssertionFailedException.shouldNotHappen(sqlEx);
/*      */                 }
/*      */ 
/* 2915 */                 fkresults = null;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2842 */         .doForAll();
/*      */       }
/*      */       finally
/*      */       {
/* 2921 */         if (stmt != null) {
/* 2922 */           stmt.close();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2927 */     java.sql.ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 2929 */     return results;
/*      */   }
/*      */ 
/*      */   private void getImportKeyResults(String catalog, String importingTable, String keysComment, List tuples)
/*      */     throws SQLException
/*      */   {
/* 2951 */     getResultsImpl(catalog, importingTable, keysComment, tuples, null, false);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate)
/*      */     throws SQLException
/*      */   {
/* 3022 */     Field[] fields = new Field[13];
/* 3023 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3024 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3025 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3026 */     fields[3] = new Field("", "NON_UNIQUE", 1, 4);
/* 3027 */     fields[4] = new Field("", "INDEX_QUALIFIER", 1, 1);
/* 3028 */     fields[5] = new Field("", "INDEX_NAME", 1, 32);
/* 3029 */     fields[6] = new Field("", "TYPE", 1, 32);
/* 3030 */     fields[7] = new Field("", "ORDINAL_POSITION", 5, 5);
/* 3031 */     fields[8] = new Field("", "COLUMN_NAME", 1, 32);
/* 3032 */     fields[9] = new Field("", "ASC_OR_DESC", 1, 1);
/* 3033 */     fields[10] = new Field("", "CARDINALITY", 4, 10);
/* 3034 */     fields[11] = new Field("", "PAGES", 4, 10);
/* 3035 */     fields[12] = new Field("", "FILTER_CONDITION", 1, 32);
/*      */ 
/* 3037 */     ArrayList rows = new ArrayList();
/* 3038 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 3042 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, unique, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final boolean val$unique;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 3045 */         void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet results = null;
/*      */           try
/*      */           {
/* 3048 */             StringBuffer queryBuf = new StringBuffer("SHOW INDEX FROM ");
/*      */ 
/* 3050 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3051 */             queryBuf.append(this.val$table);
/* 3052 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3053 */             queryBuf.append(" FROM ");
/* 3054 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3055 */             queryBuf.append(catalogStr.toString());
/* 3056 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */             try
/*      */             {
/* 3059 */               results = this.val$stmt.executeQuery(queryBuf.toString());
/*      */             } catch (SQLException sqlEx) {
/* 3061 */               int errorCode = sqlEx.getErrorCode();
/*      */ 
/* 3065 */               if (!"42S02".equals(sqlEx.getSQLState()))
/*      */               {
/* 3068 */                 if (errorCode != 1146) {
/* 3069 */                   throw sqlEx;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 3074 */             while ((results != null) && (results.next())) {
/* 3075 */               byte[][] row = new byte[14][];
/* 3076 */               row[0] = (catalogStr.toString() == null ? new byte[0] : DatabaseMetaData.access$100(DatabaseMetaData.this, catalogStr.toString()));
/*      */ 
/* 3079 */               row[1] = null;
/* 3080 */               row[2] = results.getBytes("Table");
/*      */ 
/* 3082 */               boolean indexIsUnique = results.getInt("Non_unique") == 0;
/*      */ 
/* 3085 */               row[3] = (!indexIsUnique ? DatabaseMetaData.this.s2b("true") : DatabaseMetaData.access$100(DatabaseMetaData.this, "false"));
/*      */ 
/* 3087 */               row[4] = new byte[0];
/* 3088 */               row[5] = results.getBytes("Key_name");
/* 3089 */               row[6] = Integer.toString(3).getBytes();
/*      */ 
/* 3092 */               row[7] = results.getBytes("Seq_in_index");
/* 3093 */               row[8] = results.getBytes("Column_name");
/* 3094 */               row[9] = results.getBytes("Collation");
/* 3095 */               row[10] = results.getBytes("Cardinality");
/* 3096 */               row[11] = DatabaseMetaData.access$100(DatabaseMetaData.this, "0");
/* 3097 */               row[12] = null;
/*      */ 
/* 3099 */               if (this.val$unique) {
/* 3100 */                 if (indexIsUnique) {
/* 3101 */                   this.val$rows.add(row);
/*      */                 }
/*      */               }
/*      */               else
/* 3105 */                 this.val$rows.add(row);
/*      */             }
/*      */           }
/*      */           finally {
/* 3109 */             if (results != null) {
/*      */               try {
/* 3111 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 3116 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3042 */       .doForAll();
/*      */ 
/* 3122 */       java.sql.ResultSet indexInfo = buildResultSet(fields, rows);
/*      */ 
/* 3124 */       java.sql.ResultSet localResultSet1 = indexInfo;
/*      */       return localResultSet1;
/*      */     }
/*      */     finally
/*      */     {
/* 3126 */       if (stmt != null)
/* 3127 */         stmt.close(); 
/* 3127 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public int getJDBCMajorVersion()
/*      */     throws SQLException
/*      */   {
/* 3136 */     return 3;
/*      */   }
/*      */ 
/*      */   public int getJDBCMinorVersion()
/*      */     throws SQLException
/*      */   {
/* 3143 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxBinaryLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3154 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxCatalogNameLength()
/*      */     throws SQLException
/*      */   {
/* 3165 */     return 32;
/*      */   }
/*      */ 
/*      */   public int getMaxCharLiteralLength()
/*      */     throws SQLException
/*      */   {
/* 3176 */     return 16777208;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnNameLength()
/*      */     throws SQLException
/*      */   {
/* 3187 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInGroupBy()
/*      */     throws SQLException
/*      */   {
/* 3198 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInIndex()
/*      */     throws SQLException
/*      */   {
/* 3209 */     return 16;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 3220 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInSelect()
/*      */     throws SQLException
/*      */   {
/* 3231 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxColumnsInTable()
/*      */     throws SQLException
/*      */   {
/* 3242 */     return 512;
/*      */   }
/*      */ 
/*      */   public int getMaxConnections()
/*      */     throws SQLException
/*      */   {
/* 3253 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxCursorNameLength()
/*      */     throws SQLException
/*      */   {
/* 3264 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxIndexLength()
/*      */     throws SQLException
/*      */   {
/* 3275 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxProcedureNameLength()
/*      */     throws SQLException
/*      */   {
/* 3286 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxRowSize()
/*      */     throws SQLException
/*      */   {
/* 3297 */     return 2147483639;
/*      */   }
/*      */ 
/*      */   public int getMaxSchemaNameLength()
/*      */     throws SQLException
/*      */   {
/* 3308 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxStatementLength()
/*      */     throws SQLException
/*      */   {
/* 3319 */     return MysqlIO.getMaxBuf() - 4;
/*      */   }
/*      */ 
/*      */   public int getMaxStatements()
/*      */     throws SQLException
/*      */   {
/* 3330 */     return 0;
/*      */   }
/*      */ 
/*      */   public int getMaxTableNameLength()
/*      */     throws SQLException
/*      */   {
/* 3341 */     return 64;
/*      */   }
/*      */ 
/*      */   public int getMaxTablesInSelect()
/*      */     throws SQLException
/*      */   {
/* 3352 */     return 256;
/*      */   }
/*      */ 
/*      */   public int getMaxUserNameLength()
/*      */     throws SQLException
/*      */   {
/* 3363 */     return 16;
/*      */   }
/*      */ 
/*      */   public String getNumericFunctions()
/*      */     throws SQLException
/*      */   {
/* 3374 */     return "ABS,ACOS,ASIN,ATAN,ATAN2,BIT_COUNT,CEILING,COS,COT,DEGREES,EXP,FLOOR,LOG,LOG10,MAX,MIN,MOD,PI,POW,POWER,RADIANS,RAND,ROUND,SIN,SQRT,TAN,TRUNCATE";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getPrimaryKeys(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 3406 */     Field[] fields = new Field[6];
/* 3407 */     fields[0] = new Field("", "TABLE_CAT", 1, 255);
/* 3408 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 0);
/* 3409 */     fields[2] = new Field("", "TABLE_NAME", 1, 255);
/* 3410 */     fields[3] = new Field("", "COLUMN_NAME", 1, 32);
/* 3411 */     fields[4] = new Field("", "KEY_SEQ", 5, 5);
/* 3412 */     fields[5] = new Field("", "PK_NAME", 1, 32);
/*      */ 
/* 3414 */     if (table == null) {
/* 3415 */       throw new SQLException("Table not specified.", "S1009");
/*      */     }
/*      */ 
/* 3419 */     ArrayList rows = new ArrayList();
/* 3420 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */     try
/*      */     {
/* 3424 */       new IterateBlock(getCatalogIterator(catalog), table, stmt, rows) { private final String val$table;
/*      */         private final Statement val$stmt;
/*      */         private final ArrayList val$rows;
/*      */ 
/* 3426 */         void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet rs = null;
/*      */           try
/*      */           {
/* 3430 */             StringBuffer queryBuf = new StringBuffer("SHOW KEYS FROM ");
/*      */ 
/* 3432 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3433 */             queryBuf.append(this.val$table);
/* 3434 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3435 */             queryBuf.append(" FROM ");
/* 3436 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/* 3437 */             queryBuf.append(catalogStr.toString());
/* 3438 */             queryBuf.append(DatabaseMetaData.this.quotedId);
/*      */ 
/* 3440 */             rs = this.val$stmt.executeQuery(queryBuf.toString());
/*      */ 
/* 3442 */             ArrayList tuples = new ArrayList();
/* 3443 */             TreeMap sortMap = new TreeMap();
/*      */ 
/* 3445 */             while (rs.next()) {
/* 3446 */               String keyType = rs.getString("Key_name");
/*      */ 
/* 3448 */               if ((keyType != null) && (
/* 3449 */                 (keyType.equalsIgnoreCase("PRIMARY")) || (keyType.equalsIgnoreCase("PRI"))))
/*      */               {
/* 3451 */                 byte[][] tuple = new byte[6][];
/* 3452 */                 tuple[0] = (catalogStr.toString() == null ? new byte[0] : DatabaseMetaData.access$100(DatabaseMetaData.this, catalogStr.toString()));
/*      */ 
/* 3454 */                 tuple[1] = null;
/* 3455 */                 tuple[2] = DatabaseMetaData.access$100(DatabaseMetaData.this, this.val$table);
/*      */ 
/* 3457 */                 String columnName = rs.getString("Column_name");
/*      */ 
/* 3459 */                 tuple[3] = DatabaseMetaData.access$100(DatabaseMetaData.this, columnName);
/* 3460 */                 tuple[4] = DatabaseMetaData.access$100(DatabaseMetaData.this, rs.getString("Seq_in_index"));
/* 3461 */                 tuple[5] = DatabaseMetaData.access$100(DatabaseMetaData.this, keyType);
/* 3462 */                 sortMap.put(columnName, tuple);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 3468 */             Iterator sortedIterator = sortMap.values().iterator();
/*      */ 
/* 3470 */             while (sortedIterator.hasNext())
/* 3471 */               this.val$rows.add(sortedIterator.next());
/*      */           }
/*      */           finally
/*      */           {
/* 3475 */             if (rs != null) {
/*      */               try {
/* 3477 */                 rs.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 3482 */               rs = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3424 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 3488 */       if (stmt != null) {
/* 3489 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 3493 */     java.sql.ResultSet results = buildResultSet(fields, rows);
/*      */ 
/* 3495 */     return results;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
/*      */     throws SQLException
/*      */   {
/* 3568 */     Field[] fields = new Field[13];
/*      */ 
/* 3570 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/* 3571 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/* 3572 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/* 3573 */     fields[3] = new Field("", "COLUMN_NAME", 1, 0);
/* 3574 */     fields[4] = new Field("", "COLUMN_TYPE", 1, 0);
/* 3575 */     fields[5] = new Field("", "DATA_TYPE", 5, 0);
/* 3576 */     fields[6] = new Field("", "TYPE_NAME", 1, 0);
/* 3577 */     fields[7] = new Field("", "PRECISION", 4, 0);
/* 3578 */     fields[8] = new Field("", "LENGTH", 4, 0);
/* 3579 */     fields[9] = new Field("", "SCALE", 5, 0);
/* 3580 */     fields[10] = new Field("", "RADIX", 5, 0);
/* 3581 */     fields[11] = new Field("", "NULLABLE", 5, 0);
/* 3582 */     fields[12] = new Field("", "REMARKS", 1, 0);
/*      */ 
/* 3584 */     List proceduresToExtractList = new ArrayList();
/*      */ 
/* 3586 */     if (supportsStoredProcedures()) {
/* 3587 */       if ((procedureNamePattern.indexOf("%") == -1) && (procedureNamePattern.indexOf("?") == -1))
/*      */       {
/* 3589 */         proceduresToExtractList.add(procedureNamePattern);
/*      */       } else {
/* 3591 */         PreparedStatement procedureNameStmt = null;
/* 3592 */         java.sql.ResultSet procedureNameRs = null;
/*      */         try
/*      */         {
/* 3596 */           procedureNameRs = getProcedures(catalog, schemaPattern, procedureNamePattern);
/*      */ 
/* 3599 */           while (procedureNameRs.next()) {
/* 3600 */             proceduresToExtractList.add(procedureNameRs.getString(3));
/*      */           }
/*      */ 
/* 3608 */           Collections.sort(proceduresToExtractList);
/*      */         } finally {
/* 3610 */           SQLException rethrowSqlEx = null;
/*      */ 
/* 3612 */           if (procedureNameRs != null) {
/*      */             try {
/* 3614 */               procedureNameRs.close();
/*      */             } catch (SQLException sqlEx) {
/* 3616 */               rethrowSqlEx = sqlEx;
/*      */             }
/*      */           }
/*      */ 
/* 3620 */           if (procedureNameStmt != null) {
/*      */             try {
/* 3622 */               procedureNameStmt.close();
/*      */             } catch (SQLException sqlEx) {
/* 3624 */               rethrowSqlEx = sqlEx;
/*      */             }
/*      */           }
/*      */ 
/* 3628 */           if (rethrowSqlEx != null) {
/* 3629 */             throw rethrowSqlEx;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3635 */     ArrayList resultRows = new ArrayList();
/*      */ 
/* 3637 */     for (Iterator iter = proceduresToExtractList.iterator(); iter.hasNext(); ) {
/* 3638 */       String procName = (String)iter.next();
/*      */ 
/* 3640 */       getCallStmtParameterTypes(catalog, procName, columnNamePattern, resultRows);
/*      */     }
/*      */ 
/* 3644 */     return buildResultSet(fields, resultRows);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern)
/*      */     throws SQLException
/*      */   {
/* 3690 */     if ((procedureNamePattern == null) || (procedureNamePattern.length() == 0))
/*      */     {
/* 3692 */       if (this.conn.getNullNamePatternMatchesAll())
/* 3693 */         procedureNamePattern = "%";
/*      */       else {
/* 3695 */         throw new SQLException("Procedure name pattern can not be NULL or empty.", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3701 */     Field[] fields = new Field[8];
/* 3702 */     fields[0] = new Field("", "PROCEDURE_CAT", 1, 0);
/* 3703 */     fields[1] = new Field("", "PROCEDURE_SCHEM", 1, 0);
/* 3704 */     fields[2] = new Field("", "PROCEDURE_NAME", 1, 0);
/* 3705 */     fields[3] = new Field("", "reserved1", 1, 0);
/* 3706 */     fields[4] = new Field("", "reserved2", 1, 0);
/* 3707 */     fields[5] = new Field("", "reserved3", 1, 0);
/* 3708 */     fields[6] = new Field("", "REMARKS", 1, 0);
/* 3709 */     fields[7] = new Field("", "PROCEDURE_TYPE", 5, 0);
/*      */ 
/* 3711 */     ArrayList procedureRows = new ArrayList();
/*      */ 
/* 3713 */     if (supportsStoredProcedures()) {
/* 3714 */       String procNamePattern = procedureNamePattern;
/*      */ 
/* 3716 */       Map procedureRowsOrderedByName = new TreeMap();
/*      */ 
/* 3718 */       new IterateBlock(getCatalogIterator(catalog), procNamePattern, procedureRowsOrderedByName, procedureRows) { private final String val$procNamePattern;
/*      */         private final Map val$procedureRowsOrderedByName;
/*      */         private final ArrayList val$procedureRows;
/*      */ 
/* 3720 */         void forEach(Object catalogStr) throws SQLException { String db = catalogStr.toString();
/*      */ 
/* 3722 */           boolean fromSelect = false;
/* 3723 */           java.sql.ResultSet proceduresRs = null;
/* 3724 */           boolean needsClientFiltering = true;
/* 3725 */           PreparedStatement proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SELECT name, type FROM mysql.proc WHERE name like ? and db <=> ? ORDER BY name");
/*      */           try
/*      */           {
/* 3734 */             boolean hasTypeColumn = false;
/*      */ 
/* 3736 */             if (db != null)
/* 3737 */               proceduresStmt.setString(2, db);
/*      */             else {
/* 3739 */               proceduresStmt.setNull(2, 12);
/*      */             }
/*      */ 
/* 3742 */             int nameIndex = 1;
/*      */ 
/* 3744 */             if (proceduresStmt.getMaxRows() != 0) {
/* 3745 */               proceduresStmt.setMaxRows(0);
/*      */             }
/*      */ 
/* 3748 */             proceduresStmt.setString(1, this.val$procNamePattern);
/*      */             try
/*      */             {
/* 3751 */               proceduresRs = proceduresStmt.executeQuery();
/* 3752 */               fromSelect = true;
/* 3753 */               needsClientFiltering = false;
/* 3754 */               hasTypeColumn = true;
/*      */             }
/*      */             catch (SQLException sqlEx)
/*      */             {
/* 3762 */               proceduresStmt.close();
/*      */ 
/* 3764 */               fromSelect = false;
/*      */ 
/* 3766 */               if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 1))
/* 3767 */                 nameIndex = 2;
/*      */               else {
/* 3769 */                 nameIndex = 1;
/*      */               }
/*      */ 
/* 3772 */               proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SHOW PROCEDURE STATUS LIKE ?");
/*      */ 
/* 3775 */               if (proceduresStmt.getMaxRows() != 0) {
/* 3776 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 3779 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 3781 */               proceduresRs = proceduresStmt.executeQuery();
/*      */             }
/*      */ 
/* 3784 */             DatabaseMetaData.this.convertToJdbcProcedureList(fromSelect, db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex);
/*      */ 
/* 3788 */             if (!hasTypeColumn)
/*      */             {
/* 3790 */               if (proceduresStmt != null) {
/* 3791 */                 proceduresStmt.close();
/*      */               }
/*      */ 
/* 3794 */               proceduresStmt = DatabaseMetaData.this.conn.clientPrepareStatement("SHOW FUNCTION STATUS LIKE ?");
/*      */ 
/* 3797 */               if (proceduresStmt.getMaxRows() != 0) {
/* 3798 */                 proceduresStmt.setMaxRows(0);
/*      */               }
/*      */ 
/* 3801 */               proceduresStmt.setString(1, this.val$procNamePattern);
/*      */ 
/* 3803 */               proceduresRs = proceduresStmt.executeQuery();
/*      */ 
/* 3805 */               DatabaseMetaData.this.convertToJdbcFunctionList(db, proceduresRs, needsClientFiltering, db, this.val$procedureRowsOrderedByName, nameIndex);
/*      */             }
/*      */ 
/* 3813 */             Iterator proceduresIter = this.val$procedureRowsOrderedByName.values().iterator();
/*      */ 
/* 3816 */             while (proceduresIter.hasNext())
/* 3817 */               this.val$procedureRows.add(proceduresIter.next());
/*      */           }
/*      */           finally {
/* 3820 */             SQLException rethrowSqlEx = null;
/*      */ 
/* 3822 */             if (proceduresRs != null) {
/*      */               try {
/* 3824 */                 proceduresRs.close();
/*      */               } catch (SQLException sqlEx) {
/* 3826 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 3830 */             if (proceduresStmt != null) {
/*      */               try {
/* 3832 */                 proceduresStmt.close();
/*      */               } catch (SQLException sqlEx) {
/* 3834 */                 rethrowSqlEx = sqlEx;
/*      */               }
/*      */             }
/*      */ 
/* 3838 */             if (rethrowSqlEx != null)
/* 3839 */               throw rethrowSqlEx;
/*      */           }
/*      */         }
/*      */       }
/* 3718 */       .doForAll();
/*      */     }
/*      */ 
/* 3846 */     return buildResultSet(fields, procedureRows);
/*      */   }
/*      */ 
/*      */   public String getProcedureTerm()
/*      */     throws SQLException
/*      */   {
/* 3857 */     return "PROCEDURE";
/*      */   }
/*      */ 
/*      */   public int getResultSetHoldability()
/*      */     throws SQLException
/*      */   {
/* 3864 */     return 1;
/*      */   }
/*      */ 
/*      */   private void getResultsImpl(String catalog, String table, String keysComment, List tuples, String fkTableName, boolean isExport)
/*      */     throws SQLException
/*      */   {
/* 3871 */     LocalAndReferencedColumns parsedInfo = parseTableStatusIntoLocalAndReferencedColumns(keysComment);
/*      */ 
/* 3874 */     if ((isExport) && (!parsedInfo.referencedTable.equals(table))) {
/* 3875 */       return;
/*      */     }
/*      */ 
/* 3878 */     if (parsedInfo.localColumnsList.size() != parsedInfo.referencedColumnsList.size()) {
/* 3879 */       throw new SQLException("Error parsing foreign keys definition,number of local and referenced columns is not the same.", "S1000");
/*      */     }
/*      */ 
/* 3885 */     Iterator localColumnNames = parsedInfo.localColumnsList.iterator();
/* 3886 */     Iterator referColumnNames = parsedInfo.referencedColumnsList.iterator();
/*      */ 
/* 3888 */     int keySeqIndex = 1;
/*      */ 
/* 3890 */     while (localColumnNames.hasNext()) {
/* 3891 */       byte[][] tuple = new byte[14][];
/* 3892 */       String lColumnName = removeQuotedId(localColumnNames.next().toString());
/*      */ 
/* 3894 */       String rColumnName = removeQuotedId(referColumnNames.next().toString());
/*      */ 
/* 3896 */       tuple[4] = (catalog == null ? new byte[0] : s2b(catalog));
/*      */ 
/* 3898 */       tuple[5] = null;
/* 3899 */       tuple[6] = s2b(isExport ? fkTableName : table);
/* 3900 */       tuple[7] = s2b(lColumnName);
/* 3901 */       tuple[0] = s2b(parsedInfo.referencedCatalog);
/* 3902 */       tuple[1] = null;
/* 3903 */       tuple[2] = s2b(isExport ? table : parsedInfo.referencedTable);
/* 3904 */       tuple[3] = s2b(rColumnName);
/* 3905 */       tuple[8] = s2b(Integer.toString(keySeqIndex++));
/*      */ 
/* 3907 */       int[] actions = getForeignKeyActions(keysComment);
/*      */ 
/* 3909 */       tuple[9] = s2b(Integer.toString(actions[1]));
/* 3910 */       tuple[10] = s2b(Integer.toString(actions[0]));
/* 3911 */       tuple[11] = s2b(parsedInfo.constraintName);
/* 3912 */       tuple[12] = null;
/* 3913 */       tuple[13] = s2b(Integer.toString(7));
/*      */ 
/* 3915 */       tuples.add(tuple);
/*      */     }
/*      */   }
/*      */ 
/*      */   private LocalAndReferencedColumns parseTableStatusIntoLocalAndReferencedColumns(String keysComment)
/*      */     throws SQLException
/*      */   {
/* 3953 */     String columnsDelimitter = ",";
/*      */ 
/* 3955 */     char quoteChar = this.quotedId.length() == 0 ? '\000' : this.quotedId.charAt(0);
/*      */ 
/* 3958 */     int indexOfOpenParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysComment, "(", quoteChar, true);
/*      */ 
/* 3962 */     if (indexOfOpenParenLocalColumns == -1) {
/* 3963 */       throw new SQLException("Error parsing foreign keys definition, couldn't find start of local columns list.", "S1000");
/*      */     }
/*      */ 
/* 3968 */     String constraintName = removeQuotedId(keysComment.substring(0, indexOfOpenParenLocalColumns).trim());
/*      */ 
/* 3970 */     keysComment = keysComment.substring(indexOfOpenParenLocalColumns, keysComment.length());
/*      */ 
/* 3973 */     String keysCommentTrimmed = keysComment.trim();
/*      */ 
/* 3975 */     int indexOfCloseParenLocalColumns = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 3979 */     if (indexOfCloseParenLocalColumns == -1) {
/* 3980 */       throw new SQLException("Error parsing foreign keys definition, couldn't find end of local columns list.", "S1000");
/*      */     }
/*      */ 
/* 3985 */     String localColumnNamesString = keysCommentTrimmed.substring(1, indexOfCloseParenLocalColumns);
/*      */ 
/* 3988 */     int indexOfRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(0, keysCommentTrimmed, "REFER ", this.quotedId.charAt(0), true);
/*      */ 
/* 3991 */     if (indexOfRefer == -1) {
/* 3992 */       throw new SQLException("Error parsing foreign keys definition, couldn't find start of referenced tables list.", "S1000");
/*      */     }
/*      */ 
/* 3997 */     int indexOfOpenParenReferCol = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfRefer, keysCommentTrimmed, "(", quoteChar, false);
/*      */ 
/* 4001 */     if (indexOfOpenParenReferCol == -1) {
/* 4002 */       throw new SQLException("Error parsing foreign keys definition, couldn't find start of referenced columns list.", "S1000");
/*      */     }
/*      */ 
/* 4007 */     String referCatalogTableString = keysCommentTrimmed.substring(indexOfRefer + "REFER ".length(), indexOfOpenParenReferCol);
/*      */ 
/* 4010 */     int indexOfSlash = StringUtils.indexOfIgnoreCaseRespectQuotes(0, referCatalogTableString, "/", this.quotedId.charAt(0), false);
/*      */ 
/* 4013 */     if (indexOfSlash == -1) {
/* 4014 */       throw new SQLException("Error parsing foreign keys definition, couldn't find name of referenced catalog.", "S1000");
/*      */     }
/*      */ 
/* 4019 */     String referCatalog = removeQuotedId(referCatalogTableString.substring(0, indexOfSlash));
/*      */ 
/* 4021 */     String referTable = removeQuotedId(referCatalogTableString.substring(indexOfSlash + 1).trim());
/*      */ 
/* 4024 */     int indexOfCloseParenRefer = StringUtils.indexOfIgnoreCaseRespectQuotes(indexOfOpenParenReferCol, keysCommentTrimmed, ")", quoteChar, true);
/*      */ 
/* 4028 */     if (indexOfCloseParenRefer == -1) {
/* 4029 */       throw new SQLException("Error parsing foreign keys definition, couldn't find end of referenced columns list.", "S1000");
/*      */     }
/*      */ 
/* 4034 */     String referColumnNamesString = keysCommentTrimmed.substring(indexOfOpenParenReferCol + 1, indexOfCloseParenRefer);
/*      */ 
/* 4037 */     List referColumnsList = StringUtils.split(referColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 4039 */     List localColumnsList = StringUtils.split(localColumnNamesString, columnsDelimitter, this.quotedId, this.quotedId, false);
/*      */ 
/* 4042 */     return new LocalAndReferencedColumns(localColumnsList, referColumnsList, constraintName, referCatalog, referTable);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getSchemas()
/*      */     throws SQLException
/*      */   {
/* 4062 */     Field[] fields = new Field[1];
/* 4063 */     fields[0] = new Field("", "TABLE_SCHEM", 1, 0);
/*      */ 
/* 4065 */     ArrayList tuples = new ArrayList();
/* 4066 */     java.sql.ResultSet results = buildResultSet(fields, tuples);
/*      */ 
/* 4068 */     return results;
/*      */   }
/*      */ 
/*      */   public String getSchemaTerm()
/*      */     throws SQLException
/*      */   {
/* 4079 */     return "";
/*      */   }
/*      */ 
/*      */   public String getSearchStringEscape()
/*      */     throws SQLException
/*      */   {
/* 4097 */     return "\\";
/*      */   }
/*      */ 
/*      */   public String getSQLKeywords()
/*      */     throws SQLException
/*      */   {
/* 4109 */     return "AUTO_INCREMENT,BINARY,BLOB,ENUM,INFILE,LOAD,MEDIUMINT,OPTION,OUTFILE,REPLACE,SET,TEXT,UNSIGNED,ZEROFILL";
/*      */   }
/*      */ 
/*      */   public int getSQLStateType()
/*      */     throws SQLException
/*      */   {
/* 4131 */     if (this.conn.versionMeetsMinimum(4, 1, 0)) {
/* 4132 */       return 2;
/*      */     }
/*      */ 
/* 4135 */     if (this.conn.getUseSqlStateCodes()) {
/* 4136 */       return 2;
/*      */     }
/*      */ 
/* 4139 */     return 1;
/*      */   }
/*      */ 
/*      */   public String getStringFunctions()
/*      */     throws SQLException
/*      */   {
/* 4150 */     return "ASCII,BIN,BIT_LENGTH,CHAR,CHARACTER_LENGTH,CHAR_LENGTH,CONCAT,CONCAT_WS,CONV,ELT,EXPORT_SET,FIELD,FIND_IN_SET,HEX,INSERT,INSTR,LCASE,LEFT,LENGTH,LOAD_FILE,LOCATE,LOCATE,LOWER,LPAD,LTRIM,MAKE_SET,MATCH,MID,OCT,OCTET_LENGTH,ORD,POSITION,QUOTE,REPEAT,REPLACE,REVERSE,RIGHT,RPAD,RTRIM,SOUNDEX,SPACE,STRCMP,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING,SUBSTRING_INDEX,TRIM,UCASE,UPPER";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getSuperTables(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4164 */     Field[] fields = new Field[4];
/* 4165 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4166 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4167 */     fields[2] = new Field("", "TABLE_NAME", 1, 32);
/* 4168 */     fields[3] = new Field("", "SUPERTABLE_NAME", 1, 32);
/*      */ 
/* 4170 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getSuperTypes(String arg0, String arg1, String arg2)
/*      */     throws SQLException
/*      */   {
/* 4178 */     Field[] fields = new Field[6];
/* 4179 */     fields[0] = new Field("", "TABLE_CAT", 1, 32);
/* 4180 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 32);
/* 4181 */     fields[2] = new Field("", "TYPE_NAME", 1, 32);
/* 4182 */     fields[3] = new Field("", "SUPERTYPE_CAT", 1, 32);
/* 4183 */     fields[4] = new Field("", "SUPERTYPE_SCHEM", 1, 32);
/* 4184 */     fields[5] = new Field("", "SUPERTYPE_NAME", 1, 32);
/*      */ 
/* 4186 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public String getSystemFunctions()
/*      */     throws SQLException
/*      */   {
/* 4197 */     return "DATABASE,USER,SYSTEM_USER,SESSION_USER,PASSWORD,ENCRYPT,LAST_INSERT_ID,VERSION";
/*      */   }
/*      */ 
/*      */   private String getTableNameWithCase(String table) {
/* 4201 */     String tableNameWithCase = this.conn.lowerCaseTableNames() ? table.toLowerCase() : table;
/*      */ 
/* 4204 */     return tableNameWithCase;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern)
/*      */     throws SQLException
/*      */   {
/* 4244 */     if (tableNamePattern == null) {
/* 4245 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4246 */         tableNamePattern = "%";
/*      */       else {
/* 4248 */         throw new SQLException("Table name pattern can not be NULL or empty.", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4254 */     Field[] fields = new Field[7];
/* 4255 */     fields[0] = new Field("", "TABLE_CAT", 1, 64);
/* 4256 */     fields[1] = new Field("", "TABLE_SCHEM", 1, 1);
/* 4257 */     fields[2] = new Field("", "TABLE_NAME", 1, 64);
/* 4258 */     fields[3] = new Field("", "GRANTOR", 1, 77);
/* 4259 */     fields[4] = new Field("", "GRANTEE", 1, 77);
/* 4260 */     fields[5] = new Field("", "PRIVILEGE", 1, 64);
/* 4261 */     fields[6] = new Field("", "IS_GRANTABLE", 1, 3);
/*      */ 
/* 4263 */     StringBuffer grantQuery = new StringBuffer("SELECT host,db,table_name,grantor,user,table_priv from mysql.tables_priv ");
/*      */ 
/* 4265 */     grantQuery.append(" WHERE ");
/*      */ 
/* 4267 */     if ((catalog != null) && (catalog.length() != 0)) {
/* 4268 */       grantQuery.append(" db='");
/* 4269 */       grantQuery.append(catalog);
/* 4270 */       grantQuery.append("' AND ");
/*      */     }
/*      */ 
/* 4273 */     grantQuery.append("table_name like '");
/* 4274 */     grantQuery.append(tableNamePattern);
/* 4275 */     grantQuery.append("'");
/*      */ 
/* 4277 */     java.sql.ResultSet results = null;
/* 4278 */     ArrayList grantRows = new ArrayList();
/* 4279 */     Statement stmt = null;
/*      */     try
/*      */     {
/* 4282 */       stmt = this.conn.createStatement();
/* 4283 */       stmt.setEscapeProcessing(false);
/*      */ 
/* 4285 */       results = stmt.executeQuery(grantQuery.toString());
/*      */ 
/* 4287 */       while (results.next()) {
/* 4288 */         String host = results.getString(1);
/* 4289 */         String db = results.getString(2);
/* 4290 */         String table = results.getString(3);
/* 4291 */         String grantor = results.getString(4);
/* 4292 */         String user = results.getString(5);
/*      */ 
/* 4294 */         if ((user == null) || (user.length() == 0)) {
/* 4295 */           user = "%";
/*      */         }
/*      */ 
/* 4298 */         StringBuffer fullUser = new StringBuffer(user);
/*      */ 
/* 4300 */         if ((host != null) && (this.conn.getUseHostsInPrivileges())) {
/* 4301 */           fullUser.append("@");
/* 4302 */           fullUser.append(host);
/*      */         }
/*      */ 
/* 4305 */         String allPrivileges = results.getString(6);
/*      */ 
/* 4307 */         if (allPrivileges != null) {
/* 4308 */           allPrivileges = allPrivileges.toUpperCase(Locale.ENGLISH);
/*      */ 
/* 4310 */           StringTokenizer st = new StringTokenizer(allPrivileges, ",");
/*      */ 
/* 4312 */           while (st.hasMoreTokens()) {
/* 4313 */             String privilege = st.nextToken().trim();
/*      */ 
/* 4316 */             java.sql.ResultSet columnResults = null;
/*      */             try
/*      */             {
/* 4319 */               columnResults = getColumns(catalog, schemaPattern, table, "%");
/*      */ 
/* 4322 */               while (columnResults.next()) {
/* 4323 */                 byte[][] tuple = new byte[8][];
/* 4324 */                 tuple[0] = s2b(db);
/* 4325 */                 tuple[1] = null;
/* 4326 */                 tuple[2] = s2b(table);
/*      */ 
/* 4328 */                 if (grantor != null)
/* 4329 */                   tuple[3] = s2b(grantor);
/*      */                 else {
/* 4331 */                   tuple[3] = null;
/*      */                 }
/*      */ 
/* 4334 */                 tuple[4] = s2b(fullUser.toString());
/* 4335 */                 tuple[5] = s2b(privilege);
/* 4336 */                 tuple[6] = null;
/* 4337 */                 grantRows.add(tuple);
/*      */               }
/*      */             } finally {
/* 4340 */               if (columnResults != null)
/*      */                 try {
/* 4342 */                   columnResults.close();
/*      */                 }
/*      */                 catch (Exception ex) {
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 4352 */       if (results != null) {
/*      */         try {
/* 4354 */           results.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4359 */         results = null;
/*      */       }
/*      */ 
/* 4362 */       if (stmt != null) {
/*      */         try {
/* 4364 */           stmt.close();
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/*      */         }
/* 4369 */         stmt = null;
/*      */       }
/*      */     }
/*      */ 
/* 4373 */     return buildResultSet(fields, grantRows);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
/*      */     throws SQLException
/*      */   {
/* 4415 */     if (tableNamePattern == null) {
/* 4416 */       if (this.conn.getNullNamePatternMatchesAll())
/* 4417 */         tableNamePattern = "%";
/*      */       else {
/* 4419 */         throw new SQLException("Table name pattern can not be NULL or empty.", "S1009");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4425 */     Field[] fields = new Field[5];
/* 4426 */     fields[0] = new Field("", "TABLE_CAT", 12, 255);
/* 4427 */     fields[1] = new Field("", "TABLE_SCHEM", 12, 0);
/* 4428 */     fields[2] = new Field("", "TABLE_NAME", 12, 255);
/* 4429 */     fields[3] = new Field("", "TABLE_TYPE", 12, 5);
/* 4430 */     fields[4] = new Field("", "REMARKS", 12, 0);
/*      */ 
/* 4432 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 4434 */     Statement stmt = this.conn.getMetadataSafeStatement();
/*      */ 
/* 4436 */     String tableNamePat = tableNamePattern;
/*      */     try
/*      */     {
/* 4440 */       new IterateBlock(getCatalogIterator(catalog), stmt, tableNamePat, types, tuples) { private final Statement val$stmt;
/*      */         private final String val$tableNamePat;
/*      */         private final String[] val$types;
/*      */         private final ArrayList val$tuples;
/*      */ 
/* 4442 */         void forEach(Object catalogStr) throws SQLException { java.sql.ResultSet results = null;
/*      */           try
/*      */           {
/* 4446 */             if (!DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2)) {
/* 4447 */               results = this.val$stmt.executeQuery("SHOW TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr.toString() + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */             }
/*      */             else
/*      */             {
/* 4453 */               results = this.val$stmt.executeQuery("SHOW FULL TABLES FROM " + DatabaseMetaData.this.quotedId + catalogStr.toString() + DatabaseMetaData.this.quotedId + " LIKE '" + this.val$tableNamePat + "'");
/*      */             }
/*      */ 
/* 4460 */             boolean shouldReportTables = false;
/* 4461 */             boolean shouldReportViews = false;
/*      */ 
/* 4463 */             if ((this.val$types == null) || (this.val$types.length == 0)) {
/* 4464 */               shouldReportTables = true;
/* 4465 */               shouldReportViews = true;
/*      */             } else {
/* 4467 */               for (int i = 0; i < this.val$types.length; i++) {
/* 4468 */                 if ("TABLE".equalsIgnoreCase(this.val$types[i])) {
/* 4469 */                   shouldReportTables = true;
/*      */                 }
/*      */ 
/* 4472 */                 if ("VIEW".equalsIgnoreCase(this.val$types[i])) {
/* 4473 */                   shouldReportViews = true;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 4478 */             int typeColumnIndex = 0;
/* 4479 */             boolean hasTableTypes = false;
/*      */ 
/* 4481 */             if (DatabaseMetaData.this.conn.versionMeetsMinimum(5, 0, 2))
/*      */             {
/*      */               try
/*      */               {
/* 4486 */                 typeColumnIndex = results.findColumn("table_type");
/*      */ 
/* 4488 */                 hasTableTypes = true;
/*      */               }
/*      */               catch (SQLException sqlEx)
/*      */               {
/*      */                 try
/*      */                 {
/* 4500 */                   typeColumnIndex = results.findColumn("Type");
/*      */ 
/* 4502 */                   hasTableTypes = true;
/*      */                 } catch (SQLException sqlEx2) {
/* 4504 */                   hasTableTypes = false;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 4509 */             TreeMap tablesOrderedByName = null;
/* 4510 */             TreeMap viewsOrderedByName = null;
/*      */ 
/* 4512 */             while (results.next()) {
/* 4513 */               byte[][] row = new byte[5][];
/* 4514 */               row[0] = (catalogStr.toString() == null ? null : DatabaseMetaData.access$100(DatabaseMetaData.this, catalogStr.toString()));
/*      */ 
/* 4516 */               row[1] = null;
/* 4517 */               row[2] = results.getBytes(1);
/* 4518 */               row[4] = new byte[0];
/*      */ 
/* 4520 */               if (hasTableTypes) {
/* 4521 */                 String tableType = results.getString(typeColumnIndex);
/*      */ 
/* 4524 */                 if ((("table".equalsIgnoreCase(tableType)) || ("base table".equalsIgnoreCase(tableType))) && (shouldReportTables))
/*      */                 {
/* 4527 */                   row[3] = DatabaseMetaData.access$1000();
/*      */ 
/* 4529 */                   if (tablesOrderedByName == null) {
/* 4530 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4533 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 4535 */                 else if (("view".equalsIgnoreCase(tableType)) && (shouldReportViews))
/*      */                 {
/* 4537 */                   row[3] = DatabaseMetaData.access$1100();
/*      */ 
/* 4539 */                   if (viewsOrderedByName == null) {
/* 4540 */                     viewsOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4543 */                   viewsOrderedByName.put(results.getString(1), row);
/*      */                 }
/* 4545 */                 else if (!hasTableTypes)
/*      */                 {
/* 4547 */                   row[3] = DatabaseMetaData.access$1000();
/*      */ 
/* 4549 */                   if (tablesOrderedByName == null) {
/* 4550 */                     tablesOrderedByName = new TreeMap();
/*      */                   }
/*      */ 
/* 4553 */                   tablesOrderedByName.put(results.getString(1), row);
/*      */                 }
/*      */ 
/*      */               }
/* 4557 */               else if (shouldReportTables)
/*      */               {
/* 4559 */                 row[3] = DatabaseMetaData.access$1000();
/*      */ 
/* 4561 */                 if (tablesOrderedByName == null) {
/* 4562 */                   tablesOrderedByName = new TreeMap();
/*      */                 }
/*      */ 
/* 4565 */                 tablesOrderedByName.put(results.getString(1), row);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 4574 */             if (tablesOrderedByName != null) {
/* 4575 */               Iterator tablesIter = tablesOrderedByName.values().iterator();
/*      */ 
/* 4578 */               while (tablesIter.hasNext()) {
/* 4579 */                 this.val$tuples.add(tablesIter.next());
/*      */               }
/*      */             }
/*      */ 
/* 4583 */             if (viewsOrderedByName != null) {
/* 4584 */               Iterator viewsIter = viewsOrderedByName.values().iterator();
/*      */ 
/* 4587 */               while (viewsIter.hasNext())
/* 4588 */                 this.val$tuples.add(viewsIter.next());
/*      */             }
/*      */           }
/*      */           finally
/*      */           {
/* 4593 */             if (results != null) {
/*      */               try {
/* 4595 */                 results.close();
/*      */               }
/*      */               catch (Exception ex)
/*      */               {
/*      */               }
/* 4600 */               results = null;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 4440 */       .doForAll();
/*      */     }
/*      */     finally
/*      */     {
/* 4607 */       if (stmt != null) {
/* 4608 */         stmt.close();
/*      */       }
/*      */     }
/*      */ 
/* 4612 */     java.sql.ResultSet tables = buildResultSet(fields, tuples);
/*      */ 
/* 4614 */     return tables;
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getTableTypes()
/*      */     throws SQLException
/*      */   {
/* 4635 */     ArrayList tuples = new ArrayList();
/* 4636 */     Field[] fields = new Field[1];
/* 4637 */     fields[0] = new Field("", "TABLE_TYPE", 12, 5);
/*      */ 
/* 4639 */     byte[][] tableTypeRow = new byte[1][];
/* 4640 */     tableTypeRow[0] = TABLE_AS_BYTES;
/* 4641 */     tuples.add(tableTypeRow);
/*      */ 
/* 4643 */     if (this.conn.versionMeetsMinimum(5, 0, 1)) {
/* 4644 */       byte[][] viewTypeRow = new byte[1][];
/* 4645 */       viewTypeRow[0] = VIEW_AS_BYTES;
/* 4646 */       tuples.add(viewTypeRow);
/*      */     }
/*      */ 
/* 4649 */     byte[][] tempTypeRow = new byte[1][];
/* 4650 */     tempTypeRow[0] = s2b("LOCAL TEMPORARY");
/* 4651 */     tuples.add(tempTypeRow);
/*      */ 
/* 4653 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getTimeDateFunctions()
/*      */     throws SQLException
/*      */   {
/* 4664 */     return "DAYOFWEEK,WEEKDAY,DAYOFMONTH,DAYOFYEAR,MONTH,DAYNAME,MONTHNAME,QUARTER,WEEK,YEAR,HOUR,MINUTE,SECOND,PERIOD_ADD,PERIOD_DIFF,TO_DAYS,FROM_DAYS,DATE_FORMAT,TIME_FORMAT,CURDATE,CURRENT_DATE,CURTIME,CURRENT_TIME,NOW,SYSDATE,CURRENT_TIMESTAMP,UNIX_TIMESTAMP,FROM_UNIXTIME,SEC_TO_TIME,TIME_TO_SEC";
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getTypeInfo()
/*      */     throws SQLException
/*      */   {
/* 4773 */     Field[] fields = new Field[18];
/* 4774 */     fields[0] = new Field("", "TYPE_NAME", 1, 32);
/* 4775 */     fields[1] = new Field("", "DATA_TYPE", 5, 5);
/* 4776 */     fields[2] = new Field("", "PRECISION", 4, 10);
/* 4777 */     fields[3] = new Field("", "LITERAL_PREFIX", 1, 4);
/* 4778 */     fields[4] = new Field("", "LITERAL_SUFFIX", 1, 4);
/* 4779 */     fields[5] = new Field("", "CREATE_PARAMS", 1, 32);
/* 4780 */     fields[6] = new Field("", "NULLABLE", 5, 5);
/* 4781 */     fields[7] = new Field("", "CASE_SENSITIVE", 1, 3);
/* 4782 */     fields[8] = new Field("", "SEARCHABLE", 5, 3);
/* 4783 */     fields[9] = new Field("", "UNSIGNED_ATTRIBUTE", 1, 3);
/* 4784 */     fields[10] = new Field("", "FIXED_PREC_SCALE", 1, 3);
/* 4785 */     fields[11] = new Field("", "AUTO_INCREMENT", 1, 3);
/* 4786 */     fields[12] = new Field("", "LOCAL_TYPE_NAME", 1, 32);
/* 4787 */     fields[13] = new Field("", "MINIMUM_SCALE", 5, 5);
/* 4788 */     fields[14] = new Field("", "MAXIMUM_SCALE", 5, 5);
/* 4789 */     fields[15] = new Field("", "SQL_DATA_TYPE", 4, 10);
/* 4790 */     fields[16] = new Field("", "SQL_DATETIME_SUB", 4, 10);
/* 4791 */     fields[17] = new Field("", "NUM_PREC_RADIX", 4, 10);
/*      */ 
/* 4793 */     byte[][] rowVal = (byte[][])null;
/* 4794 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 4803 */     rowVal = new byte[18][];
/* 4804 */     rowVal[0] = s2b("BIT");
/* 4805 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 4808 */     rowVal[2] = s2b("1");
/* 4809 */     rowVal[3] = s2b("");
/* 4810 */     rowVal[4] = s2b("");
/* 4811 */     rowVal[5] = s2b("");
/* 4812 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4816 */     rowVal[7] = s2b("true");
/* 4817 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4821 */     rowVal[9] = s2b("false");
/* 4822 */     rowVal[10] = s2b("false");
/* 4823 */     rowVal[11] = s2b("false");
/* 4824 */     rowVal[12] = s2b("BIT");
/* 4825 */     rowVal[13] = s2b("0");
/* 4826 */     rowVal[14] = s2b("0");
/* 4827 */     rowVal[15] = s2b("0");
/* 4828 */     rowVal[16] = s2b("0");
/* 4829 */     rowVal[17] = s2b("10");
/* 4830 */     tuples.add(rowVal);
/*      */ 
/* 4835 */     rowVal = new byte[18][];
/* 4836 */     rowVal[0] = s2b("BOOL");
/* 4837 */     rowVal[1] = Integer.toString(-7).getBytes();
/*      */ 
/* 4840 */     rowVal[2] = s2b("1");
/* 4841 */     rowVal[3] = s2b("");
/* 4842 */     rowVal[4] = s2b("");
/* 4843 */     rowVal[5] = s2b("");
/* 4844 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4848 */     rowVal[7] = s2b("true");
/* 4849 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4853 */     rowVal[9] = s2b("false");
/* 4854 */     rowVal[10] = s2b("false");
/* 4855 */     rowVal[11] = s2b("false");
/* 4856 */     rowVal[12] = s2b("BOOL");
/* 4857 */     rowVal[13] = s2b("0");
/* 4858 */     rowVal[14] = s2b("0");
/* 4859 */     rowVal[15] = s2b("0");
/* 4860 */     rowVal[16] = s2b("0");
/* 4861 */     rowVal[17] = s2b("10");
/* 4862 */     tuples.add(rowVal);
/*      */ 
/* 4867 */     rowVal = new byte[18][];
/* 4868 */     rowVal[0] = s2b("TINYINT");
/* 4869 */     rowVal[1] = Integer.toString(-6).getBytes();
/*      */ 
/* 4872 */     rowVal[2] = s2b("3");
/* 4873 */     rowVal[3] = s2b("");
/* 4874 */     rowVal[4] = s2b("");
/* 4875 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 4876 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4880 */     rowVal[7] = s2b("false");
/* 4881 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4885 */     rowVal[9] = s2b("true");
/* 4886 */     rowVal[10] = s2b("false");
/* 4887 */     rowVal[11] = s2b("true");
/* 4888 */     rowVal[12] = s2b("TINYINT");
/* 4889 */     rowVal[13] = s2b("0");
/* 4890 */     rowVal[14] = s2b("0");
/* 4891 */     rowVal[15] = s2b("0");
/* 4892 */     rowVal[16] = s2b("0");
/* 4893 */     rowVal[17] = s2b("10");
/* 4894 */     tuples.add(rowVal);
/*      */ 
/* 4899 */     rowVal = new byte[18][];
/* 4900 */     rowVal[0] = s2b("BIGINT");
/* 4901 */     rowVal[1] = Integer.toString(-5).getBytes();
/*      */ 
/* 4904 */     rowVal[2] = s2b("19");
/* 4905 */     rowVal[3] = s2b("");
/* 4906 */     rowVal[4] = s2b("");
/* 4907 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 4908 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4912 */     rowVal[7] = s2b("false");
/* 4913 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4917 */     rowVal[9] = s2b("true");
/* 4918 */     rowVal[10] = s2b("false");
/* 4919 */     rowVal[11] = s2b("true");
/* 4920 */     rowVal[12] = s2b("BIGINT");
/* 4921 */     rowVal[13] = s2b("0");
/* 4922 */     rowVal[14] = s2b("0");
/* 4923 */     rowVal[15] = s2b("0");
/* 4924 */     rowVal[16] = s2b("0");
/* 4925 */     rowVal[17] = s2b("10");
/* 4926 */     tuples.add(rowVal);
/*      */ 
/* 4931 */     rowVal = new byte[18][];
/* 4932 */     rowVal[0] = s2b("LONG VARBINARY");
/* 4933 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 4936 */     rowVal[2] = s2b("16777215");
/* 4937 */     rowVal[3] = s2b("'");
/* 4938 */     rowVal[4] = s2b("'");
/* 4939 */     rowVal[5] = s2b("");
/* 4940 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4944 */     rowVal[7] = s2b("true");
/* 4945 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4949 */     rowVal[9] = s2b("false");
/* 4950 */     rowVal[10] = s2b("false");
/* 4951 */     rowVal[11] = s2b("false");
/* 4952 */     rowVal[12] = s2b("LONG VARBINARY");
/* 4953 */     rowVal[13] = s2b("0");
/* 4954 */     rowVal[14] = s2b("0");
/* 4955 */     rowVal[15] = s2b("0");
/* 4956 */     rowVal[16] = s2b("0");
/* 4957 */     rowVal[17] = s2b("10");
/* 4958 */     tuples.add(rowVal);
/*      */ 
/* 4963 */     rowVal = new byte[18][];
/* 4964 */     rowVal[0] = s2b("MEDIUMBLOB");
/* 4965 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 4968 */     rowVal[2] = s2b("16777215");
/* 4969 */     rowVal[3] = s2b("'");
/* 4970 */     rowVal[4] = s2b("'");
/* 4971 */     rowVal[5] = s2b("");
/* 4972 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 4976 */     rowVal[7] = s2b("true");
/* 4977 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 4981 */     rowVal[9] = s2b("false");
/* 4982 */     rowVal[10] = s2b("false");
/* 4983 */     rowVal[11] = s2b("false");
/* 4984 */     rowVal[12] = s2b("MEDIUMBLOB");
/* 4985 */     rowVal[13] = s2b("0");
/* 4986 */     rowVal[14] = s2b("0");
/* 4987 */     rowVal[15] = s2b("0");
/* 4988 */     rowVal[16] = s2b("0");
/* 4989 */     rowVal[17] = s2b("10");
/* 4990 */     tuples.add(rowVal);
/*      */ 
/* 4995 */     rowVal = new byte[18][];
/* 4996 */     rowVal[0] = s2b("LONGBLOB");
/* 4997 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5000 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5003 */     rowVal[3] = s2b("'");
/* 5004 */     rowVal[4] = s2b("'");
/* 5005 */     rowVal[5] = s2b("");
/* 5006 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5010 */     rowVal[7] = s2b("true");
/* 5011 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5015 */     rowVal[9] = s2b("false");
/* 5016 */     rowVal[10] = s2b("false");
/* 5017 */     rowVal[11] = s2b("false");
/* 5018 */     rowVal[12] = s2b("LONGBLOB");
/* 5019 */     rowVal[13] = s2b("0");
/* 5020 */     rowVal[14] = s2b("0");
/* 5021 */     rowVal[15] = s2b("0");
/* 5022 */     rowVal[16] = s2b("0");
/* 5023 */     rowVal[17] = s2b("10");
/* 5024 */     tuples.add(rowVal);
/*      */ 
/* 5029 */     rowVal = new byte[18][];
/* 5030 */     rowVal[0] = s2b("BLOB");
/* 5031 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5034 */     rowVal[2] = s2b("65535");
/* 5035 */     rowVal[3] = s2b("'");
/* 5036 */     rowVal[4] = s2b("'");
/* 5037 */     rowVal[5] = s2b("");
/* 5038 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5042 */     rowVal[7] = s2b("true");
/* 5043 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5047 */     rowVal[9] = s2b("false");
/* 5048 */     rowVal[10] = s2b("false");
/* 5049 */     rowVal[11] = s2b("false");
/* 5050 */     rowVal[12] = s2b("BLOB");
/* 5051 */     rowVal[13] = s2b("0");
/* 5052 */     rowVal[14] = s2b("0");
/* 5053 */     rowVal[15] = s2b("0");
/* 5054 */     rowVal[16] = s2b("0");
/* 5055 */     rowVal[17] = s2b("10");
/* 5056 */     tuples.add(rowVal);
/*      */ 
/* 5061 */     rowVal = new byte[18][];
/* 5062 */     rowVal[0] = s2b("TINYBLOB");
/* 5063 */     rowVal[1] = Integer.toString(-4).getBytes();
/*      */ 
/* 5066 */     rowVal[2] = s2b("255");
/* 5067 */     rowVal[3] = s2b("'");
/* 5068 */     rowVal[4] = s2b("'");
/* 5069 */     rowVal[5] = s2b("");
/* 5070 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5074 */     rowVal[7] = s2b("true");
/* 5075 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5079 */     rowVal[9] = s2b("false");
/* 5080 */     rowVal[10] = s2b("false");
/* 5081 */     rowVal[11] = s2b("false");
/* 5082 */     rowVal[12] = s2b("TINYBLOB");
/* 5083 */     rowVal[13] = s2b("0");
/* 5084 */     rowVal[14] = s2b("0");
/* 5085 */     rowVal[15] = s2b("0");
/* 5086 */     rowVal[16] = s2b("0");
/* 5087 */     rowVal[17] = s2b("10");
/* 5088 */     tuples.add(rowVal);
/*      */ 
/* 5094 */     rowVal = new byte[18][];
/* 5095 */     rowVal[0] = s2b("VARBINARY");
/* 5096 */     rowVal[1] = Integer.toString(-3).getBytes();
/*      */ 
/* 5099 */     rowVal[2] = s2b("255");
/* 5100 */     rowVal[3] = s2b("'");
/* 5101 */     rowVal[4] = s2b("'");
/* 5102 */     rowVal[5] = s2b("(M)");
/* 5103 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5107 */     rowVal[7] = s2b("true");
/* 5108 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5112 */     rowVal[9] = s2b("false");
/* 5113 */     rowVal[10] = s2b("false");
/* 5114 */     rowVal[11] = s2b("false");
/* 5115 */     rowVal[12] = s2b("VARBINARY");
/* 5116 */     rowVal[13] = s2b("0");
/* 5117 */     rowVal[14] = s2b("0");
/* 5118 */     rowVal[15] = s2b("0");
/* 5119 */     rowVal[16] = s2b("0");
/* 5120 */     rowVal[17] = s2b("10");
/* 5121 */     tuples.add(rowVal);
/*      */ 
/* 5127 */     rowVal = new byte[18][];
/* 5128 */     rowVal[0] = s2b("BINARY");
/* 5129 */     rowVal[1] = Integer.toString(-2).getBytes();
/*      */ 
/* 5132 */     rowVal[2] = s2b("255");
/* 5133 */     rowVal[3] = s2b("'");
/* 5134 */     rowVal[4] = s2b("'");
/* 5135 */     rowVal[5] = s2b("(M)");
/* 5136 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5140 */     rowVal[7] = s2b("true");
/* 5141 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5145 */     rowVal[9] = s2b("false");
/* 5146 */     rowVal[10] = s2b("false");
/* 5147 */     rowVal[11] = s2b("false");
/* 5148 */     rowVal[12] = s2b("BINARY");
/* 5149 */     rowVal[13] = s2b("0");
/* 5150 */     rowVal[14] = s2b("0");
/* 5151 */     rowVal[15] = s2b("0");
/* 5152 */     rowVal[16] = s2b("0");
/* 5153 */     rowVal[17] = s2b("10");
/* 5154 */     tuples.add(rowVal);
/*      */ 
/* 5159 */     rowVal = new byte[18][];
/* 5160 */     rowVal[0] = s2b("LONG VARCHAR");
/* 5161 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5164 */     rowVal[2] = s2b("16777215");
/* 5165 */     rowVal[3] = s2b("'");
/* 5166 */     rowVal[4] = s2b("'");
/* 5167 */     rowVal[5] = s2b("");
/* 5168 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5172 */     rowVal[7] = s2b("false");
/* 5173 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5177 */     rowVal[9] = s2b("false");
/* 5178 */     rowVal[10] = s2b("false");
/* 5179 */     rowVal[11] = s2b("false");
/* 5180 */     rowVal[12] = s2b("LONG VARCHAR");
/* 5181 */     rowVal[13] = s2b("0");
/* 5182 */     rowVal[14] = s2b("0");
/* 5183 */     rowVal[15] = s2b("0");
/* 5184 */     rowVal[16] = s2b("0");
/* 5185 */     rowVal[17] = s2b("10");
/* 5186 */     tuples.add(rowVal);
/*      */ 
/* 5191 */     rowVal = new byte[18][];
/* 5192 */     rowVal[0] = s2b("MEDIUMTEXT");
/* 5193 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5196 */     rowVal[2] = s2b("16777215");
/* 5197 */     rowVal[3] = s2b("'");
/* 5198 */     rowVal[4] = s2b("'");
/* 5199 */     rowVal[5] = s2b("");
/* 5200 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5204 */     rowVal[7] = s2b("false");
/* 5205 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5209 */     rowVal[9] = s2b("false");
/* 5210 */     rowVal[10] = s2b("false");
/* 5211 */     rowVal[11] = s2b("false");
/* 5212 */     rowVal[12] = s2b("MEDIUMTEXT");
/* 5213 */     rowVal[13] = s2b("0");
/* 5214 */     rowVal[14] = s2b("0");
/* 5215 */     rowVal[15] = s2b("0");
/* 5216 */     rowVal[16] = s2b("0");
/* 5217 */     rowVal[17] = s2b("10");
/* 5218 */     tuples.add(rowVal);
/*      */ 
/* 5223 */     rowVal = new byte[18][];
/* 5224 */     rowVal[0] = s2b("LONGTEXT");
/* 5225 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5228 */     rowVal[2] = Integer.toString(2147483647).getBytes();
/*      */ 
/* 5231 */     rowVal[3] = s2b("'");
/* 5232 */     rowVal[4] = s2b("'");
/* 5233 */     rowVal[5] = s2b("");
/* 5234 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5238 */     rowVal[7] = s2b("false");
/* 5239 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5243 */     rowVal[9] = s2b("false");
/* 5244 */     rowVal[10] = s2b("false");
/* 5245 */     rowVal[11] = s2b("false");
/* 5246 */     rowVal[12] = s2b("LONGTEXT");
/* 5247 */     rowVal[13] = s2b("0");
/* 5248 */     rowVal[14] = s2b("0");
/* 5249 */     rowVal[15] = s2b("0");
/* 5250 */     rowVal[16] = s2b("0");
/* 5251 */     rowVal[17] = s2b("10");
/* 5252 */     tuples.add(rowVal);
/*      */ 
/* 5257 */     rowVal = new byte[18][];
/* 5258 */     rowVal[0] = s2b("TEXT");
/* 5259 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5262 */     rowVal[2] = s2b("65535");
/* 5263 */     rowVal[3] = s2b("'");
/* 5264 */     rowVal[4] = s2b("'");
/* 5265 */     rowVal[5] = s2b("");
/* 5266 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5270 */     rowVal[7] = s2b("false");
/* 5271 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5275 */     rowVal[9] = s2b("false");
/* 5276 */     rowVal[10] = s2b("false");
/* 5277 */     rowVal[11] = s2b("false");
/* 5278 */     rowVal[12] = s2b("TEXT");
/* 5279 */     rowVal[13] = s2b("0");
/* 5280 */     rowVal[14] = s2b("0");
/* 5281 */     rowVal[15] = s2b("0");
/* 5282 */     rowVal[16] = s2b("0");
/* 5283 */     rowVal[17] = s2b("10");
/* 5284 */     tuples.add(rowVal);
/*      */ 
/* 5289 */     rowVal = new byte[18][];
/* 5290 */     rowVal[0] = s2b("TINYTEXT");
/* 5291 */     rowVal[1] = Integer.toString(-1).getBytes();
/*      */ 
/* 5294 */     rowVal[2] = s2b("255");
/* 5295 */     rowVal[3] = s2b("'");
/* 5296 */     rowVal[4] = s2b("'");
/* 5297 */     rowVal[5] = s2b("");
/* 5298 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5302 */     rowVal[7] = s2b("false");
/* 5303 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5307 */     rowVal[9] = s2b("false");
/* 5308 */     rowVal[10] = s2b("false");
/* 5309 */     rowVal[11] = s2b("false");
/* 5310 */     rowVal[12] = s2b("TINYTEXT");
/* 5311 */     rowVal[13] = s2b("0");
/* 5312 */     rowVal[14] = s2b("0");
/* 5313 */     rowVal[15] = s2b("0");
/* 5314 */     rowVal[16] = s2b("0");
/* 5315 */     rowVal[17] = s2b("10");
/* 5316 */     tuples.add(rowVal);
/*      */ 
/* 5321 */     rowVal = new byte[18][];
/* 5322 */     rowVal[0] = s2b("CHAR");
/* 5323 */     rowVal[1] = Integer.toString(1).getBytes();
/*      */ 
/* 5326 */     rowVal[2] = s2b("255");
/* 5327 */     rowVal[3] = s2b("'");
/* 5328 */     rowVal[4] = s2b("'");
/* 5329 */     rowVal[5] = s2b("(M)");
/* 5330 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5334 */     rowVal[7] = s2b("false");
/* 5335 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5339 */     rowVal[9] = s2b("false");
/* 5340 */     rowVal[10] = s2b("false");
/* 5341 */     rowVal[11] = s2b("false");
/* 5342 */     rowVal[12] = s2b("CHAR");
/* 5343 */     rowVal[13] = s2b("0");
/* 5344 */     rowVal[14] = s2b("0");
/* 5345 */     rowVal[15] = s2b("0");
/* 5346 */     rowVal[16] = s2b("0");
/* 5347 */     rowVal[17] = s2b("10");
/* 5348 */     tuples.add(rowVal);
/*      */ 
/* 5354 */     rowVal = new byte[18][];
/* 5355 */     rowVal[0] = s2b("NUMERIC");
/* 5356 */     rowVal[1] = Integer.toString(2).getBytes();
/*      */ 
/* 5359 */     rowVal[2] = s2b("17");
/* 5360 */     rowVal[3] = s2b("");
/* 5361 */     rowVal[4] = s2b("");
/* 5362 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5363 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5367 */     rowVal[7] = s2b("false");
/* 5368 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5372 */     rowVal[9] = s2b("false");
/* 5373 */     rowVal[10] = s2b("false");
/* 5374 */     rowVal[11] = s2b("true");
/* 5375 */     rowVal[12] = s2b("NUMERIC");
/* 5376 */     rowVal[13] = s2b("-308");
/* 5377 */     rowVal[14] = s2b("308");
/* 5378 */     rowVal[15] = s2b("0");
/* 5379 */     rowVal[16] = s2b("0");
/* 5380 */     rowVal[17] = s2b("10");
/* 5381 */     tuples.add(rowVal);
/*      */ 
/* 5386 */     rowVal = new byte[18][];
/* 5387 */     rowVal[0] = s2b("DECIMAL");
/* 5388 */     rowVal[1] = Integer.toString(3).getBytes();
/*      */ 
/* 5391 */     rowVal[2] = s2b("17");
/* 5392 */     rowVal[3] = s2b("");
/* 5393 */     rowVal[4] = s2b("");
/* 5394 */     rowVal[5] = s2b("[(M[,D])] [ZEROFILL]");
/* 5395 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5399 */     rowVal[7] = s2b("false");
/* 5400 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5404 */     rowVal[9] = s2b("false");
/* 5405 */     rowVal[10] = s2b("false");
/* 5406 */     rowVal[11] = s2b("true");
/* 5407 */     rowVal[12] = s2b("DECIMAL");
/* 5408 */     rowVal[13] = s2b("-308");
/* 5409 */     rowVal[14] = s2b("308");
/* 5410 */     rowVal[15] = s2b("0");
/* 5411 */     rowVal[16] = s2b("0");
/* 5412 */     rowVal[17] = s2b("10");
/* 5413 */     tuples.add(rowVal);
/*      */ 
/* 5418 */     rowVal = new byte[18][];
/* 5419 */     rowVal[0] = s2b("INTEGER");
/* 5420 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5423 */     rowVal[2] = s2b("10");
/* 5424 */     rowVal[3] = s2b("");
/* 5425 */     rowVal[4] = s2b("");
/* 5426 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5427 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5431 */     rowVal[7] = s2b("false");
/* 5432 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5436 */     rowVal[9] = s2b("true");
/* 5437 */     rowVal[10] = s2b("false");
/* 5438 */     rowVal[11] = s2b("true");
/* 5439 */     rowVal[12] = s2b("INTEGER");
/* 5440 */     rowVal[13] = s2b("0");
/* 5441 */     rowVal[14] = s2b("0");
/* 5442 */     rowVal[15] = s2b("0");
/* 5443 */     rowVal[16] = s2b("0");
/* 5444 */     rowVal[17] = s2b("10");
/* 5445 */     tuples.add(rowVal);
/*      */ 
/* 5450 */     rowVal = new byte[18][];
/* 5451 */     rowVal[0] = s2b("INT");
/* 5452 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5455 */     rowVal[2] = s2b("10");
/* 5456 */     rowVal[3] = s2b("");
/* 5457 */     rowVal[4] = s2b("");
/* 5458 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5459 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5463 */     rowVal[7] = s2b("false");
/* 5464 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5468 */     rowVal[9] = s2b("true");
/* 5469 */     rowVal[10] = s2b("false");
/* 5470 */     rowVal[11] = s2b("true");
/* 5471 */     rowVal[12] = s2b("INT");
/* 5472 */     rowVal[13] = s2b("0");
/* 5473 */     rowVal[14] = s2b("0");
/* 5474 */     rowVal[15] = s2b("0");
/* 5475 */     rowVal[16] = s2b("0");
/* 5476 */     rowVal[17] = s2b("10");
/* 5477 */     tuples.add(rowVal);
/*      */ 
/* 5482 */     rowVal = new byte[18][];
/* 5483 */     rowVal[0] = s2b("MEDIUMINT");
/* 5484 */     rowVal[1] = Integer.toString(4).getBytes();
/*      */ 
/* 5487 */     rowVal[2] = s2b("7");
/* 5488 */     rowVal[3] = s2b("");
/* 5489 */     rowVal[4] = s2b("");
/* 5490 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5491 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5495 */     rowVal[7] = s2b("false");
/* 5496 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5500 */     rowVal[9] = s2b("true");
/* 5501 */     rowVal[10] = s2b("false");
/* 5502 */     rowVal[11] = s2b("true");
/* 5503 */     rowVal[12] = s2b("MEDIUMINT");
/* 5504 */     rowVal[13] = s2b("0");
/* 5505 */     rowVal[14] = s2b("0");
/* 5506 */     rowVal[15] = s2b("0");
/* 5507 */     rowVal[16] = s2b("0");
/* 5508 */     rowVal[17] = s2b("10");
/* 5509 */     tuples.add(rowVal);
/*      */ 
/* 5514 */     rowVal = new byte[18][];
/* 5515 */     rowVal[0] = s2b("SMALLINT");
/* 5516 */     rowVal[1] = Integer.toString(5).getBytes();
/*      */ 
/* 5519 */     rowVal[2] = s2b("5");
/* 5520 */     rowVal[3] = s2b("");
/* 5521 */     rowVal[4] = s2b("");
/* 5522 */     rowVal[5] = s2b("[(M)] [UNSIGNED] [ZEROFILL]");
/* 5523 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5527 */     rowVal[7] = s2b("false");
/* 5528 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5532 */     rowVal[9] = s2b("true");
/* 5533 */     rowVal[10] = s2b("false");
/* 5534 */     rowVal[11] = s2b("true");
/* 5535 */     rowVal[12] = s2b("SMALLINT");
/* 5536 */     rowVal[13] = s2b("0");
/* 5537 */     rowVal[14] = s2b("0");
/* 5538 */     rowVal[15] = s2b("0");
/* 5539 */     rowVal[16] = s2b("0");
/* 5540 */     rowVal[17] = s2b("10");
/* 5541 */     tuples.add(rowVal);
/*      */ 
/* 5547 */     rowVal = new byte[18][];
/* 5548 */     rowVal[0] = s2b("FLOAT");
/* 5549 */     rowVal[1] = Integer.toString(7).getBytes();
/*      */ 
/* 5552 */     rowVal[2] = s2b("10");
/* 5553 */     rowVal[3] = s2b("");
/* 5554 */     rowVal[4] = s2b("");
/* 5555 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5556 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5560 */     rowVal[7] = s2b("false");
/* 5561 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5565 */     rowVal[9] = s2b("false");
/* 5566 */     rowVal[10] = s2b("false");
/* 5567 */     rowVal[11] = s2b("true");
/* 5568 */     rowVal[12] = s2b("FLOAT");
/* 5569 */     rowVal[13] = s2b("-38");
/* 5570 */     rowVal[14] = s2b("38");
/* 5571 */     rowVal[15] = s2b("0");
/* 5572 */     rowVal[16] = s2b("0");
/* 5573 */     rowVal[17] = s2b("10");
/* 5574 */     tuples.add(rowVal);
/*      */ 
/* 5579 */     rowVal = new byte[18][];
/* 5580 */     rowVal[0] = s2b("DOUBLE");
/* 5581 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 5584 */     rowVal[2] = s2b("17");
/* 5585 */     rowVal[3] = s2b("");
/* 5586 */     rowVal[4] = s2b("");
/* 5587 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5588 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5592 */     rowVal[7] = s2b("false");
/* 5593 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5597 */     rowVal[9] = s2b("false");
/* 5598 */     rowVal[10] = s2b("false");
/* 5599 */     rowVal[11] = s2b("true");
/* 5600 */     rowVal[12] = s2b("DOUBLE");
/* 5601 */     rowVal[13] = s2b("-308");
/* 5602 */     rowVal[14] = s2b("308");
/* 5603 */     rowVal[15] = s2b("0");
/* 5604 */     rowVal[16] = s2b("0");
/* 5605 */     rowVal[17] = s2b("10");
/* 5606 */     tuples.add(rowVal);
/*      */ 
/* 5611 */     rowVal = new byte[18][];
/* 5612 */     rowVal[0] = s2b("DOUBLE PRECISION");
/* 5613 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 5616 */     rowVal[2] = s2b("17");
/* 5617 */     rowVal[3] = s2b("");
/* 5618 */     rowVal[4] = s2b("");
/* 5619 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5620 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5624 */     rowVal[7] = s2b("false");
/* 5625 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5629 */     rowVal[9] = s2b("false");
/* 5630 */     rowVal[10] = s2b("false");
/* 5631 */     rowVal[11] = s2b("true");
/* 5632 */     rowVal[12] = s2b("DOUBLE PRECISION");
/* 5633 */     rowVal[13] = s2b("-308");
/* 5634 */     rowVal[14] = s2b("308");
/* 5635 */     rowVal[15] = s2b("0");
/* 5636 */     rowVal[16] = s2b("0");
/* 5637 */     rowVal[17] = s2b("10");
/* 5638 */     tuples.add(rowVal);
/*      */ 
/* 5643 */     rowVal = new byte[18][];
/* 5644 */     rowVal[0] = s2b("REAL");
/* 5645 */     rowVal[1] = Integer.toString(8).getBytes();
/*      */ 
/* 5648 */     rowVal[2] = s2b("17");
/* 5649 */     rowVal[3] = s2b("");
/* 5650 */     rowVal[4] = s2b("");
/* 5651 */     rowVal[5] = s2b("[(M,D)] [ZEROFILL]");
/* 5652 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5656 */     rowVal[7] = s2b("false");
/* 5657 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5661 */     rowVal[9] = s2b("false");
/* 5662 */     rowVal[10] = s2b("false");
/* 5663 */     rowVal[11] = s2b("true");
/* 5664 */     rowVal[12] = s2b("REAL");
/* 5665 */     rowVal[13] = s2b("-308");
/* 5666 */     rowVal[14] = s2b("308");
/* 5667 */     rowVal[15] = s2b("0");
/* 5668 */     rowVal[16] = s2b("0");
/* 5669 */     rowVal[17] = s2b("10");
/* 5670 */     tuples.add(rowVal);
/*      */ 
/* 5675 */     rowVal = new byte[18][];
/* 5676 */     rowVal[0] = s2b("VARCHAR");
/* 5677 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 5680 */     rowVal[2] = s2b("255");
/* 5681 */     rowVal[3] = s2b("'");
/* 5682 */     rowVal[4] = s2b("'");
/* 5683 */     rowVal[5] = s2b("(M)");
/* 5684 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5688 */     rowVal[7] = s2b("false");
/* 5689 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5693 */     rowVal[9] = s2b("false");
/* 5694 */     rowVal[10] = s2b("false");
/* 5695 */     rowVal[11] = s2b("false");
/* 5696 */     rowVal[12] = s2b("VARCHAR");
/* 5697 */     rowVal[13] = s2b("0");
/* 5698 */     rowVal[14] = s2b("0");
/* 5699 */     rowVal[15] = s2b("0");
/* 5700 */     rowVal[16] = s2b("0");
/* 5701 */     rowVal[17] = s2b("10");
/* 5702 */     tuples.add(rowVal);
/*      */ 
/* 5707 */     rowVal = new byte[18][];
/* 5708 */     rowVal[0] = s2b("ENUM");
/* 5709 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 5712 */     rowVal[2] = s2b("65535");
/* 5713 */     rowVal[3] = s2b("'");
/* 5714 */     rowVal[4] = s2b("'");
/* 5715 */     rowVal[5] = s2b("");
/* 5716 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5720 */     rowVal[7] = s2b("false");
/* 5721 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5725 */     rowVal[9] = s2b("false");
/* 5726 */     rowVal[10] = s2b("false");
/* 5727 */     rowVal[11] = s2b("false");
/* 5728 */     rowVal[12] = s2b("ENUM");
/* 5729 */     rowVal[13] = s2b("0");
/* 5730 */     rowVal[14] = s2b("0");
/* 5731 */     rowVal[15] = s2b("0");
/* 5732 */     rowVal[16] = s2b("0");
/* 5733 */     rowVal[17] = s2b("10");
/* 5734 */     tuples.add(rowVal);
/*      */ 
/* 5739 */     rowVal = new byte[18][];
/* 5740 */     rowVal[0] = s2b("SET");
/* 5741 */     rowVal[1] = Integer.toString(12).getBytes();
/*      */ 
/* 5744 */     rowVal[2] = s2b("64");
/* 5745 */     rowVal[3] = s2b("'");
/* 5746 */     rowVal[4] = s2b("'");
/* 5747 */     rowVal[5] = s2b("");
/* 5748 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5752 */     rowVal[7] = s2b("false");
/* 5753 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5757 */     rowVal[9] = s2b("false");
/* 5758 */     rowVal[10] = s2b("false");
/* 5759 */     rowVal[11] = s2b("false");
/* 5760 */     rowVal[12] = s2b("SET");
/* 5761 */     rowVal[13] = s2b("0");
/* 5762 */     rowVal[14] = s2b("0");
/* 5763 */     rowVal[15] = s2b("0");
/* 5764 */     rowVal[16] = s2b("0");
/* 5765 */     rowVal[17] = s2b("10");
/* 5766 */     tuples.add(rowVal);
/*      */ 
/* 5771 */     rowVal = new byte[18][];
/* 5772 */     rowVal[0] = s2b("DATE");
/* 5773 */     rowVal[1] = Integer.toString(91).getBytes();
/*      */ 
/* 5776 */     rowVal[2] = s2b("0");
/* 5777 */     rowVal[3] = s2b("'");
/* 5778 */     rowVal[4] = s2b("'");
/* 5779 */     rowVal[5] = s2b("");
/* 5780 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5784 */     rowVal[7] = s2b("false");
/* 5785 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5789 */     rowVal[9] = s2b("false");
/* 5790 */     rowVal[10] = s2b("false");
/* 5791 */     rowVal[11] = s2b("false");
/* 5792 */     rowVal[12] = s2b("DATE");
/* 5793 */     rowVal[13] = s2b("0");
/* 5794 */     rowVal[14] = s2b("0");
/* 5795 */     rowVal[15] = s2b("0");
/* 5796 */     rowVal[16] = s2b("0");
/* 5797 */     rowVal[17] = s2b("10");
/* 5798 */     tuples.add(rowVal);
/*      */ 
/* 5803 */     rowVal = new byte[18][];
/* 5804 */     rowVal[0] = s2b("TIME");
/* 5805 */     rowVal[1] = Integer.toString(92).getBytes();
/*      */ 
/* 5808 */     rowVal[2] = s2b("0");
/* 5809 */     rowVal[3] = s2b("'");
/* 5810 */     rowVal[4] = s2b("'");
/* 5811 */     rowVal[5] = s2b("");
/* 5812 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5816 */     rowVal[7] = s2b("false");
/* 5817 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5821 */     rowVal[9] = s2b("false");
/* 5822 */     rowVal[10] = s2b("false");
/* 5823 */     rowVal[11] = s2b("false");
/* 5824 */     rowVal[12] = s2b("TIME");
/* 5825 */     rowVal[13] = s2b("0");
/* 5826 */     rowVal[14] = s2b("0");
/* 5827 */     rowVal[15] = s2b("0");
/* 5828 */     rowVal[16] = s2b("0");
/* 5829 */     rowVal[17] = s2b("10");
/* 5830 */     tuples.add(rowVal);
/*      */ 
/* 5835 */     rowVal = new byte[18][];
/* 5836 */     rowVal[0] = s2b("DATETIME");
/* 5837 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 5840 */     rowVal[2] = s2b("0");
/* 5841 */     rowVal[3] = s2b("'");
/* 5842 */     rowVal[4] = s2b("'");
/* 5843 */     rowVal[5] = s2b("");
/* 5844 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5848 */     rowVal[7] = s2b("false");
/* 5849 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5853 */     rowVal[9] = s2b("false");
/* 5854 */     rowVal[10] = s2b("false");
/* 5855 */     rowVal[11] = s2b("false");
/* 5856 */     rowVal[12] = s2b("DATETIME");
/* 5857 */     rowVal[13] = s2b("0");
/* 5858 */     rowVal[14] = s2b("0");
/* 5859 */     rowVal[15] = s2b("0");
/* 5860 */     rowVal[16] = s2b("0");
/* 5861 */     rowVal[17] = s2b("10");
/* 5862 */     tuples.add(rowVal);
/*      */ 
/* 5867 */     rowVal = new byte[18][];
/* 5868 */     rowVal[0] = s2b("TIMESTAMP");
/* 5869 */     rowVal[1] = Integer.toString(93).getBytes();
/*      */ 
/* 5872 */     rowVal[2] = s2b("0");
/* 5873 */     rowVal[3] = s2b("'");
/* 5874 */     rowVal[4] = s2b("'");
/* 5875 */     rowVal[5] = s2b("[(M)]");
/* 5876 */     rowVal[6] = Integer.toString(1).getBytes();
/*      */ 
/* 5880 */     rowVal[7] = s2b("false");
/* 5881 */     rowVal[8] = Integer.toString(3).getBytes();
/*      */ 
/* 5885 */     rowVal[9] = s2b("false");
/* 5886 */     rowVal[10] = s2b("false");
/* 5887 */     rowVal[11] = s2b("false");
/* 5888 */     rowVal[12] = s2b("TIMESTAMP");
/* 5889 */     rowVal[13] = s2b("0");
/* 5890 */     rowVal[14] = s2b("0");
/* 5891 */     rowVal[15] = s2b("0");
/* 5892 */     rowVal[16] = s2b("0");
/* 5893 */     rowVal[17] = s2b("10");
/* 5894 */     tuples.add(rowVal);
/*      */ 
/* 5896 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types)
/*      */     throws SQLException
/*      */   {
/* 5942 */     Field[] fields = new Field[6];
/* 5943 */     fields[0] = new Field("", "TYPE_CAT", 12, 32);
/* 5944 */     fields[1] = new Field("", "TYPE_SCHEM", 12, 32);
/* 5945 */     fields[2] = new Field("", "TYPE_NAME", 12, 32);
/* 5946 */     fields[3] = new Field("", "CLASS_NAME", 12, 32);
/* 5947 */     fields[4] = new Field("", "DATA_TYPE", 12, 32);
/* 5948 */     fields[5] = new Field("", "REMARKS", 12, 32);
/*      */ 
/* 5950 */     ArrayList tuples = new ArrayList();
/*      */ 
/* 5952 */     return buildResultSet(fields, tuples);
/*      */   }
/*      */ 
/*      */   public String getURL()
/*      */     throws SQLException
/*      */   {
/* 5963 */     return this.conn.getURL();
/*      */   }
/*      */ 
/*      */   public String getUserName()
/*      */     throws SQLException
/*      */   {
/* 5974 */     if (this.conn.getUseHostsInPrivileges()) {
/* 5975 */       Statement stmt = null;
/* 5976 */       java.sql.ResultSet rs = null;
/*      */       try
/*      */       {
/* 5979 */         stmt = this.conn.createStatement();
/* 5980 */         stmt.setEscapeProcessing(false);
/*      */ 
/* 5982 */         rs = stmt.executeQuery("SELECT USER()");
/* 5983 */         rs.next();
/*      */ 
/* 5985 */         str = rs.getString(1);
/*      */       }
/*      */       finally
/*      */       {
/*      */         String str;
/* 5987 */         if (rs != null) {
/*      */           try {
/* 5989 */             rs.close();
/*      */           } catch (Exception ex) {
/* 5991 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 5994 */           rs = null;
/*      */         }
/*      */ 
/* 5997 */         if (stmt != null) {
/*      */           try {
/* 5999 */             stmt.close();
/*      */           } catch (Exception ex) {
/* 6001 */             AssertionFailedException.shouldNotHappen(ex);
/*      */           }
/*      */ 
/* 6004 */           stmt = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 6009 */     return this.conn.getUser();
/*      */   }
/*      */ 
/*      */   public java.sql.ResultSet getVersionColumns(String catalog, String schema, String table)
/*      */     throws SQLException
/*      */   {
/* 6048 */     Field[] fields = new Field[8];
/* 6049 */     fields[0] = new Field("", "SCOPE", 5, 5);
/* 6050 */     fields[1] = new Field("", "COLUMN_NAME", 1, 32);
/* 6051 */     fields[2] = new Field("", "DATA_TYPE", 5, 5);
/* 6052 */     fields[3] = new Field("", "TYPE_NAME", 1, 16);
/* 6053 */     fields[4] = new Field("", "COLUMN_SIZE", 1, 16);
/* 6054 */     fields[5] = new Field("", "BUFFER_LENGTH", 1, 16);
/* 6055 */     fields[6] = new Field("", "DECIMAL_DIGITS", 1, 16);
/* 6056 */     fields[7] = new Field("", "PSEUDO_COLUMN", 5, 5);
/*      */ 
/* 6058 */     return buildResultSet(fields, new ArrayList());
/*      */   }
/*      */ 
/*      */   public boolean insertsAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 6074 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isCatalogAtStart()
/*      */     throws SQLException
/*      */   {
/* 6086 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isReadOnly()
/*      */     throws SQLException
/*      */   {
/* 6097 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean locatorsUpdateCopy()
/*      */     throws SQLException
/*      */   {
/* 6104 */     return !this.conn.getEmulateLocators();
/*      */   }
/*      */ 
/*      */   public boolean nullPlusNonNullIsNull()
/*      */     throws SQLException
/*      */   {
/* 6116 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtEnd()
/*      */     throws SQLException
/*      */   {
/* 6127 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedAtStart()
/*      */     throws SQLException
/*      */   {
/* 6138 */     return (this.conn.versionMeetsMinimum(4, 0, 2)) && (!this.conn.versionMeetsMinimum(4, 0, 11));
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedHigh()
/*      */     throws SQLException
/*      */   {
/* 6150 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean nullsAreSortedLow()
/*      */     throws SQLException
/*      */   {
/* 6161 */     return !nullsAreSortedHigh();
/*      */   }
/*      */ 
/*      */   public boolean othersDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6174 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6187 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean othersUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6200 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownDeletesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6213 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownInsertsAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6226 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean ownUpdatesAreVisible(int type)
/*      */     throws SQLException
/*      */   {
/* 6239 */     return false;
/*      */   }
/*      */ 
/*      */   private byte[] s2b(String s)
/*      */     throws SQLException
/*      */   {
/* 6251 */     if (s == null) {
/* 6252 */       return null;
/*      */     }
/*      */ 
/* 6255 */     if ((this.conn != null) && (this.conn.getUseUnicode())) {
/*      */       try {
/* 6257 */         String encoding = this.conn.getEncoding();
/*      */ 
/* 6259 */         if (encoding == null) {
/* 6260 */           return s.getBytes();
/*      */         }
/*      */ 
/* 6263 */         SingleByteCharsetConverter converter = this.conn.getCharsetConverter(encoding);
/*      */ 
/* 6266 */         if (converter != null) {
/* 6267 */           return converter.toBytes(s);
/*      */         }
/*      */ 
/* 6270 */         return s.getBytes(encoding);
/*      */       } catch (UnsupportedEncodingException E) {
/* 6272 */         return s.getBytes();
/*      */       }
/*      */     }
/*      */ 
/* 6276 */     return s.getBytes();
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6288 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean storesLowerCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6300 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6312 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean storesMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6324 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6336 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean storesUpperCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6348 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithAddColumn()
/*      */     throws SQLException
/*      */   {
/* 6359 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsAlterTableWithDropColumn()
/*      */     throws SQLException
/*      */   {
/* 6370 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92EntryLevelSQL()
/*      */     throws SQLException
/*      */   {
/* 6382 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92FullSQL()
/*      */     throws SQLException
/*      */   {
/* 6393 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsANSI92IntermediateSQL()
/*      */     throws SQLException
/*      */   {
/* 6404 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsBatchUpdates()
/*      */     throws SQLException
/*      */   {
/* 6416 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 6428 */     return this.conn.versionMeetsMinimum(3, 22, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6439 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6450 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 6461 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCatalogsInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 6472 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsColumnAliasing()
/*      */     throws SQLException
/*      */   {
/* 6488 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert()
/*      */     throws SQLException
/*      */   {
/* 6499 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsConvert(int fromType, int toType)
/*      */     throws SQLException
/*      */   {
/* 6516 */     switch (fromType)
/*      */     {
/*      */     case -4:
/*      */     case -3:
/*      */     case -2:
/*      */     case -1:
/*      */     case 1:
/*      */     case 12:
/* 6527 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/*      */       case 93:
/*      */       case 1111:
/* 6547 */         return true;
/*      */       }
/*      */ 
/* 6550 */       return false;
/*      */     case -7:
/* 6557 */       return false;
/*      */     case -6:
/*      */     case -5:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/* 6573 */       switch (toType) {
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 12:
/* 6589 */         return true;
/*      */       case 0:
/*      */       case 9:
/*      */       case 10:
/* 6592 */       case 11: } return false;
/*      */     case 0:
/* 6597 */       return false;
/*      */     case 1111:
/* 6605 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 6612 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 6615 */       case 11: } return false;
/*      */     case 91:
/* 6621 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 6628 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 6631 */       case 11: } return false;
/*      */     case 92:
/* 6637 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/* 6644 */         return true;
/*      */       case 0:
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/*      */       case 9:
/*      */       case 10:
/* 6647 */       case 11: } return false;
/*      */     case 93:
/* 6656 */       switch (toType) {
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       case 1:
/*      */       case 12:
/*      */       case 91:
/*      */       case 92:
/* 6665 */         return true;
/*      */       }
/*      */ 
/* 6668 */       return false;
/*      */     }
/*      */ 
/* 6673 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsCoreSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 6685 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsCorrelatedSubqueries()
/*      */     throws SQLException
/*      */   {
/* 6697 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsDataDefinitionAndDataManipulationTransactions()
/*      */     throws SQLException
/*      */   {
/* 6710 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDataManipulationTransactionsOnly()
/*      */     throws SQLException
/*      */   {
/* 6722 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsDifferentTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 6735 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExpressionsInOrderBy()
/*      */     throws SQLException
/*      */   {
/* 6746 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsExtendedSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 6757 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsFullOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 6768 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsGetGeneratedKeys()
/*      */   {
/* 6777 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupBy()
/*      */     throws SQLException
/*      */   {
/* 6788 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByBeyondSelect()
/*      */     throws SQLException
/*      */   {
/* 6800 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsGroupByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 6811 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsIntegrityEnhancementFacility()
/*      */     throws SQLException
/*      */   {
/* 6822 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsLikeEscapeClause()
/*      */     throws SQLException
/*      */   {
/* 6834 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsLimitedOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 6846 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMinimumSQLGrammar()
/*      */     throws SQLException
/*      */   {
/* 6858 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6869 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMixedCaseQuotedIdentifiers()
/*      */     throws SQLException
/*      */   {
/* 6881 */     return !this.conn.lowerCaseTableNames();
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleOpenResults()
/*      */     throws SQLException
/*      */   {
/* 6888 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleResultSets()
/*      */     throws SQLException
/*      */   {
/* 6899 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsMultipleTransactions()
/*      */     throws SQLException
/*      */   {
/* 6911 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsNamedParameters()
/*      */     throws SQLException
/*      */   {
/* 6918 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsNonNullableColumns()
/*      */     throws SQLException
/*      */   {
/* 6930 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 6942 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenCursorsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 6954 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossCommit()
/*      */     throws SQLException
/*      */   {
/* 6966 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOpenStatementsAcrossRollback()
/*      */     throws SQLException
/*      */   {
/* 6978 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOrderByUnrelated()
/*      */     throws SQLException
/*      */   {
/* 6989 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsOuterJoins()
/*      */     throws SQLException
/*      */   {
/* 7000 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedDelete()
/*      */     throws SQLException
/*      */   {
/* 7011 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsPositionedUpdate()
/*      */     throws SQLException
/*      */   {
/* 7022 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetConcurrency(int type, int concurrency)
/*      */     throws SQLException
/*      */   {
/* 7040 */     switch (type) {
/*      */     case 1004:
/* 7042 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 7044 */         return true;
/*      */       }
/* 7046 */       throw new SQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */     case 1003:
/* 7051 */       if ((concurrency == 1007) || (concurrency == 1008))
/*      */       {
/* 7053 */         return true;
/*      */       }
/* 7055 */       throw new SQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */     case 1005:
/* 7060 */       return false;
/*      */     }
/* 7062 */     throw new SQLException("Illegal arguments to supportsResultSetConcurrency()", "S1009");
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetHoldability(int holdability)
/*      */     throws SQLException
/*      */   {
/* 7074 */     return holdability == 1;
/*      */   }
/*      */ 
/*      */   public boolean supportsResultSetType(int type)
/*      */     throws SQLException
/*      */   {
/* 7088 */     return type == 1004;
/*      */   }
/*      */ 
/*      */   public boolean supportsSavepoints()
/*      */     throws SQLException
/*      */   {
/* 7096 */     return (this.conn.versionMeetsMinimum(4, 0, 14)) || (this.conn.versionMeetsMinimum(4, 1, 1));
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInDataManipulation()
/*      */     throws SQLException
/*      */   {
/* 7108 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInIndexDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7119 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInPrivilegeDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7130 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInProcedureCalls()
/*      */     throws SQLException
/*      */   {
/* 7141 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSchemasInTableDefinitions()
/*      */     throws SQLException
/*      */   {
/* 7152 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsSelectForUpdate()
/*      */     throws SQLException
/*      */   {
/* 7163 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsStatementPooling()
/*      */     throws SQLException
/*      */   {
/* 7170 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsStoredProcedures()
/*      */     throws SQLException
/*      */   {
/* 7182 */     return this.conn.versionMeetsMinimum(5, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInComparisons()
/*      */     throws SQLException
/*      */   {
/* 7194 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInExists()
/*      */     throws SQLException
/*      */   {
/* 7206 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInIns()
/*      */     throws SQLException
/*      */   {
/* 7218 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsSubqueriesInQuantifieds()
/*      */     throws SQLException
/*      */   {
/* 7230 */     return this.conn.versionMeetsMinimum(4, 1, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsTableCorrelationNames()
/*      */     throws SQLException
/*      */   {
/* 7242 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactionIsolationLevel(int level)
/*      */     throws SQLException
/*      */   {
/* 7257 */     if (this.conn.supportsIsolationLevel()) {
/* 7258 */       switch (level) {
/*      */       case 1:
/*      */       case 2:
/*      */       case 4:
/*      */       case 8:
/* 7263 */         return true;
/*      */       case 3:
/*      */       case 5:
/*      */       case 6:
/* 7266 */       case 7: } return false;
/*      */     }
/*      */ 
/* 7270 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean supportsTransactions()
/*      */     throws SQLException
/*      */   {
/* 7282 */     return this.conn.supportsTransactions();
/*      */   }
/*      */ 
/*      */   public boolean supportsUnion()
/*      */     throws SQLException
/*      */   {
/* 7293 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean supportsUnionAll()
/*      */     throws SQLException
/*      */   {
/* 7304 */     return this.conn.versionMeetsMinimum(4, 0, 0);
/*      */   }
/*      */ 
/*      */   public boolean updatesAreDetected(int type)
/*      */     throws SQLException
/*      */   {
/* 7318 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFilePerTable()
/*      */     throws SQLException
/*      */   {
/* 7329 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean usesLocalFiles()
/*      */     throws SQLException
/*      */   {
/* 7340 */     return false;
/*      */   }
/*      */ 
/*      */   private String removeQuotedId(String s) {
/* 7344 */     if (s == null) {
/* 7345 */       return null;
/*      */     }
/*      */ 
/* 7348 */     if (this.quotedId.equals("")) {
/* 7349 */       return s;
/*      */     }
/*      */ 
/* 7352 */     s = s.trim();
/*      */ 
/* 7354 */     int frontOffset = 0;
/* 7355 */     int backOffset = s.length();
/* 7356 */     int quoteLength = this.quotedId.length();
/*      */ 
/* 7358 */     if (s.startsWith(this.quotedId)) {
/* 7359 */       frontOffset = quoteLength;
/*      */     }
/*      */ 
/* 7362 */     if (s.endsWith(this.quotedId)) {
/* 7363 */       backOffset -= quoteLength;
/*      */     }
/*      */ 
/* 7366 */     return s.substring(frontOffset, backOffset);
/*      */   }
/*      */ 
/*      */   class LocalAndReferencedColumns
/*      */   {
/*      */     List localColumnsList;
/*      */     List referencedColumnsList;
/*      */     String constraintName;
/*      */     String referencedTable;
/*      */     String referencedCatalog;
/*      */ 
/*      */     LocalAndReferencedColumns(List localColumns, List refColumns, String constName, String refCatalog, String refTable)
/*      */     {
/* 3928 */       this.localColumnsList = localColumns;
/* 3929 */       this.referencedColumnsList = refColumns;
/* 3930 */       this.constraintName = constName;
/* 3931 */       this.referencedTable = refTable;
/* 3932 */       this.referencedCatalog = refCatalog;
/*      */     }
/*      */   }
/*      */ 
/*      */   class TypeDescriptor
/*      */   {
/*      */     int bufferLength;
/*      */     int charOctetLength;
/*      */     int columnSize;
/*      */     short dataType;
/*      */     int decimalDigits;
/*      */     String isNullable;
/*      */     int nullability;
/*  163 */     int numPrecRadix = 10;
/*      */     String typeName;
/*      */ 
/*      */     TypeDescriptor(String typeInfo, String nullabilityInfo)
/*      */       throws SQLException
/*      */     {
/*  169 */       String mysqlType = "";
/*  170 */       String fullMysqlType = null;
/*      */ 
/*  172 */       if (typeInfo.indexOf("(") != -1)
/*  173 */         mysqlType = typeInfo.substring(0, typeInfo.indexOf("("));
/*      */       else {
/*  175 */         mysqlType = typeInfo;
/*      */       }
/*      */ 
/*  178 */       int indexOfUnsignedInMysqlType = StringUtils.indexOfIgnoreCase(mysqlType, "unsigned");
/*      */ 
/*  181 */       if (indexOfUnsignedInMysqlType != -1) {
/*  182 */         mysqlType = mysqlType.substring(0, indexOfUnsignedInMysqlType - 1);
/*      */       }
/*      */ 
/*  189 */       if (StringUtils.indexOfIgnoreCase(typeInfo, "unsigned") != -1)
/*  190 */         fullMysqlType = mysqlType + " unsigned";
/*      */       else {
/*  192 */         fullMysqlType = mysqlType;
/*      */       }
/*      */ 
/*  195 */       if (DatabaseMetaData.this.conn.getCapitalizeTypeNames()) {
/*  196 */         fullMysqlType = fullMysqlType.toUpperCase(Locale.ENGLISH);
/*      */       }
/*      */ 
/*  199 */       this.dataType = (short)MysqlDefs.mysqlToJavaType(mysqlType);
/*      */ 
/*  201 */       this.typeName = fullMysqlType;
/*      */ 
/*  204 */       if (typeInfo != null) {
/*  205 */         if ((StringUtils.startsWithIgnoreCase(typeInfo, "enum")) || (StringUtils.startsWithIgnoreCase(typeInfo, "set")))
/*      */         {
/*  207 */           String temp = typeInfo.substring(typeInfo.indexOf("("), typeInfo.lastIndexOf(")"));
/*      */ 
/*  209 */           StringTokenizer tokenizer = new StringTokenizer(temp, ",");
/*      */ 
/*  211 */           int maxLength = 0;
/*      */ 
/*  213 */           while (tokenizer.hasMoreTokens()) {
/*  214 */             maxLength = Math.max(maxLength, tokenizer.nextToken().length() - 2);
/*      */           }
/*      */ 
/*  218 */           this.columnSize = maxLength;
/*  219 */           this.decimalDigits = 0;
/*  220 */         } else if (typeInfo.indexOf(",") != -1)
/*      */         {
/*  222 */           this.columnSize = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, typeInfo.indexOf(",")));
/*      */ 
/*  225 */           this.decimalDigits = Integer.parseInt(typeInfo.substring(typeInfo.indexOf(",") + 1, typeInfo.indexOf(")")));
/*      */         }
/*      */         else
/*      */         {
/*  229 */           this.columnSize = 0;
/*      */ 
/*  232 */           if (typeInfo.indexOf("(") != -1) {
/*  233 */             int endParenIndex = typeInfo.indexOf(")");
/*      */ 
/*  235 */             if (endParenIndex == -1) {
/*  236 */               endParenIndex = typeInfo.length();
/*      */             }
/*      */ 
/*  239 */             this.columnSize = Integer.parseInt(typeInfo.substring(typeInfo.indexOf("(") + 1, endParenIndex));
/*      */ 
/*  243 */             if ((DatabaseMetaData.this.conn.getTinyInt1isBit()) && (this.columnSize == 1) && (StringUtils.startsWithIgnoreCase(typeInfo, 0, "tinyint")))
/*      */             {
/*  247 */               if (DatabaseMetaData.this.conn.getTransformedBitIsBoolean()) {
/*  248 */                 this.dataType = 16;
/*  249 */                 this.typeName = "BOOLEAN";
/*      */               } else {
/*  251 */                 this.dataType = -7;
/*  252 */                 this.typeName = "BIT";
/*      */               }
/*      */             }
/*  255 */           } else if (typeInfo.equalsIgnoreCase("tinyint")) {
/*  256 */             this.columnSize = 1;
/*  257 */           } else if (typeInfo.equalsIgnoreCase("smallint")) {
/*  258 */             this.columnSize = 6;
/*  259 */           } else if (typeInfo.equalsIgnoreCase("mediumint")) {
/*  260 */             this.columnSize = 6;
/*  261 */           } else if (typeInfo.equalsIgnoreCase("int")) {
/*  262 */             this.columnSize = 11;
/*  263 */           } else if (typeInfo.equalsIgnoreCase("integer")) {
/*  264 */             this.columnSize = 11;
/*  265 */           } else if (typeInfo.equalsIgnoreCase("bigint")) {
/*  266 */             this.columnSize = 25;
/*  267 */           } else if (typeInfo.equalsIgnoreCase("int24")) {
/*  268 */             this.columnSize = 25;
/*  269 */           } else if (typeInfo.equalsIgnoreCase("real")) {
/*  270 */             this.columnSize = 12;
/*  271 */           } else if (typeInfo.equalsIgnoreCase("float")) {
/*  272 */             this.columnSize = 12;
/*  273 */           } else if (typeInfo.equalsIgnoreCase("decimal")) {
/*  274 */             this.columnSize = 12;
/*  275 */           } else if (typeInfo.equalsIgnoreCase("numeric")) {
/*  276 */             this.columnSize = 12;
/*  277 */           } else if (typeInfo.equalsIgnoreCase("double")) {
/*  278 */             this.columnSize = 22;
/*  279 */           } else if (typeInfo.equalsIgnoreCase("char")) {
/*  280 */             this.columnSize = 1;
/*  281 */           } else if (typeInfo.equalsIgnoreCase("varchar")) {
/*  282 */             this.columnSize = 255;
/*  283 */           } else if (typeInfo.equalsIgnoreCase("date")) {
/*  284 */             this.columnSize = 10;
/*  285 */           } else if (typeInfo.equalsIgnoreCase("time")) {
/*  286 */             this.columnSize = 8;
/*  287 */           } else if (typeInfo.equalsIgnoreCase("timestamp")) {
/*  288 */             this.columnSize = 19;
/*  289 */           } else if (typeInfo.equalsIgnoreCase("datetime")) {
/*  290 */             this.columnSize = 19;
/*  291 */           } else if (typeInfo.equalsIgnoreCase("tinyblob")) {
/*  292 */             this.columnSize = 255;
/*  293 */           } else if (typeInfo.equalsIgnoreCase("blob")) {
/*  294 */             this.columnSize = 65535;
/*  295 */           } else if (typeInfo.equalsIgnoreCase("mediumblob")) {
/*  296 */             this.columnSize = 16277215;
/*  297 */           } else if (typeInfo.equalsIgnoreCase("longblob")) {
/*  298 */             this.columnSize = 2147483647;
/*  299 */           } else if (typeInfo.equalsIgnoreCase("tinytext")) {
/*  300 */             this.columnSize = 255;
/*  301 */           } else if (typeInfo.equalsIgnoreCase("text")) {
/*  302 */             this.columnSize = 65535;
/*  303 */           } else if (typeInfo.equalsIgnoreCase("mediumtext")) {
/*  304 */             this.columnSize = 16277215;
/*  305 */           } else if (typeInfo.equalsIgnoreCase("longtext")) {
/*  306 */             this.columnSize = 2147483647;
/*  307 */           } else if (typeInfo.equalsIgnoreCase("enum")) {
/*  308 */             this.columnSize = 255;
/*  309 */           } else if (typeInfo.equalsIgnoreCase("set")) {
/*  310 */             this.columnSize = 255;
/*      */           }
/*      */ 
/*  313 */           this.decimalDigits = 0;
/*      */         }
/*      */       } else {
/*  316 */         this.decimalDigits = 0;
/*  317 */         this.columnSize = 0;
/*      */       }
/*      */ 
/*  321 */       this.bufferLength = MysqlIO.getMaxBuf();
/*      */ 
/*  324 */       this.numPrecRadix = 10;
/*      */ 
/*  327 */       if (nullabilityInfo != null) {
/*  328 */         if (nullabilityInfo.equals("YES")) {
/*  329 */           this.nullability = 1;
/*  330 */           this.isNullable = "YES";
/*      */         }
/*      */         else
/*      */         {
/*  334 */           this.nullability = 0;
/*  335 */           this.isNullable = "NO";
/*      */         }
/*      */       } else {
/*  338 */         this.nullability = 0;
/*  339 */         this.isNullable = "NO";
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class SingleStringIterator extends DatabaseMetaData.IteratorWithCleanup
/*      */   {
/*  121 */     boolean onFirst = true;
/*      */     String value;
/*      */ 
/*      */     SingleStringIterator(String s)
/*      */     {
/*  125 */       super();
/*  126 */       this.value = s;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException
/*      */     {
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException
/*      */     {
/*  135 */       return this.onFirst;
/*      */     }
/*      */ 
/*      */     Object next() throws SQLException {
/*  139 */       this.onFirst = false;
/*  140 */       return this.value;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected class ResultSetIterator extends DatabaseMetaData.IteratorWithCleanup
/*      */   {
/*      */     int colIndex;
/*      */     java.sql.ResultSet resultSet;
/*      */ 
/*      */     ResultSetIterator(java.sql.ResultSet rs, int index)
/*      */     {
/*  102 */       super();
/*  103 */       this.resultSet = rs;
/*  104 */       this.colIndex = index;
/*      */     }
/*      */ 
/*      */     void close() throws SQLException {
/*  108 */       this.resultSet.close();
/*      */     }
/*      */ 
/*      */     boolean hasNext() throws SQLException {
/*  112 */       return this.resultSet.next();
/*      */     }
/*      */ 
/*      */     Object next() throws SQLException {
/*  116 */       return this.resultSet.getObject(this.colIndex);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract class IteratorWithCleanup
/*      */   {
/*      */     protected IteratorWithCleanup()
/*      */     {
/*      */     }
/*      */ 
/*      */     abstract void close()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract boolean hasNext()
/*      */       throws SQLException;
/*      */ 
/*      */     abstract Object next()
/*      */       throws SQLException;
/*      */   }
/*      */ 
/*      */   protected abstract class IterateBlock
/*      */   {
/*      */     DatabaseMetaData.IteratorWithCleanup iterator;
/*      */ 
/*      */     IterateBlock(DatabaseMetaData.IteratorWithCleanup i)
/*      */     {
/*   73 */       this.iterator = i;
/*      */     }
/*      */ 
/*      */     public void doForAll() throws SQLException {
/*      */       try {
/*   78 */         while (this.iterator.hasNext())
/*   79 */           forEach(this.iterator.next());
/*      */       }
/*      */       finally {
/*   82 */         this.iterator.close();
/*      */       }
/*      */     }
/*      */ 
/*      */     abstract void forEach(Object paramObject)
/*      */       throws SQLException;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.DatabaseMetaData
 * JD-Core Version:    0.6.0
 */
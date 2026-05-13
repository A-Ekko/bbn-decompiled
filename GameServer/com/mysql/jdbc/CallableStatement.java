/*      */ package com.mysql.jdbc;
/*      */ 
/*      */ import java.io.InputStream;
/*      */ import java.io.Reader;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.URL;
/*      */ import java.sql.Array;
/*      */ import java.sql.Blob;
/*      */ import java.sql.Clob;
/*      */ import java.sql.DatabaseMetaData;
/*      */ import java.sql.Date;
/*      */ import java.sql.ParameterMetaData;
/*      */ import java.sql.Ref;
/*      */ import java.sql.SQLException;
/*      */ import java.sql.Statement;
/*      */ import java.sql.Time;
/*      */ import java.sql.Timestamp;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ 
/*      */ public class CallableStatement extends PreparedStatement
/*      */   implements java.sql.CallableStatement
/*      */ {
/*      */   private static final int NOT_OUTPUT_PARAMETER_INDICATOR = -2147483648;
/*      */   private static final String PARAMETER_NAMESPACE_PREFIX = "@com_mysql_jdbc_outparam_";
/*  300 */   private boolean callingStoredFunction = false;
/*      */   private ResultSet functionReturnValueResults;
/*  304 */   private boolean hasOutputParams = false;
/*      */   private ResultSet outputParameterResults;
/*  310 */   private boolean outputParamWasNull = false;
/*      */   private int[] parameterIndexToRsIndex;
/*      */   protected CallableStatementParamInfo paramInfo;
/*      */   private CallableStatementParam returnValueParam;
/*      */ 
/*      */   private static String mangleParameterName(String origParameterName)
/*      */   {
/*  280 */     if (origParameterName == null) {
/*  281 */       return null;
/*      */     }
/*      */ 
/*  284 */     int offset = 0;
/*      */ 
/*  286 */     if ((origParameterName.length() > 0) && (origParameterName.charAt(0) == '@'))
/*      */     {
/*  288 */       offset = 1;
/*      */     }
/*      */ 
/*  291 */     StringBuffer paramNameBuf = new StringBuffer("@com_mysql_jdbc_outparam_".length() + origParameterName.length());
/*      */ 
/*  294 */     paramNameBuf.append("@com_mysql_jdbc_outparam_");
/*  295 */     paramNameBuf.append(origParameterName.substring(offset));
/*      */ 
/*  297 */     return paramNameBuf.toString();
/*      */   }
/*      */ 
/*      */   public CallableStatement(Connection conn, CallableStatementParamInfo paramInfo)
/*      */     throws SQLException
/*      */   {
/*  331 */     super(conn, paramInfo.nativeSql, paramInfo.catalogInUse);
/*      */ 
/*  333 */     this.paramInfo = paramInfo;
/*  334 */     this.callingStoredFunction = this.paramInfo.isFunctionCall;
/*      */   }
/*      */ 
/*      */   public CallableStatement(Connection conn, String catalog)
/*      */     throws SQLException
/*      */   {
/*  350 */     super(conn, catalog, null);
/*      */ 
/*  352 */     determineParameterTypes();
/*      */   }
/*      */ 
/*      */   public CallableStatement(Connection conn, String sql, String catalog, boolean isFunctionCall)
/*      */     throws SQLException
/*      */   {
/*  370 */     super(conn, sql, catalog);
/*      */ 
/*  372 */     this.callingStoredFunction = isFunctionCall;
/*      */ 
/*  374 */     determineParameterTypes();
/*      */   }
/*      */ 
/*      */   public void addBatch()
/*      */     throws SQLException
/*      */   {
/*  383 */     setOutParams();
/*      */ 
/*  385 */     super.addBatch();
/*      */   }
/*      */ 
/*      */   private CallableStatementParam checkIsOutputParam(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  391 */     if (this.callingStoredFunction) {
/*  392 */       if (paramIndex == 1)
/*      */       {
/*  394 */         if (this.returnValueParam == null) {
/*  395 */           this.returnValueParam = new CallableStatementParam("", 0, false, true, 12, "VARCHAR", 0, 0, 2, 5);
/*      */         }
/*      */ 
/*  401 */         return this.returnValueParam;
/*      */       }
/*      */ 
/*  405 */       paramIndex--;
/*      */     }
/*      */ 
/*  408 */     checkParameterIndexBounds(paramIndex);
/*      */ 
/*  410 */     int localParamIndex = paramIndex - 1;
/*      */ 
/*  412 */     CallableStatementParam paramDescriptor = this.paramInfo.getParameter(localParamIndex);
/*      */ 
/*  415 */     if (!paramDescriptor.isOut) {
/*  416 */       throw new SQLException(Messages.getString("CallableStatement.9") + paramIndex + Messages.getString("CallableStatement.10"), "S1009");
/*      */     }
/*      */ 
/*  422 */     this.hasOutputParams = true;
/*      */ 
/*  424 */     return paramDescriptor;
/*      */   }
/*      */ 
/*      */   private void checkParameterIndexBounds(int paramIndex)
/*      */     throws SQLException
/*      */   {
/*  435 */     this.paramInfo.checkBounds(paramIndex);
/*      */   }
/*      */ 
/*      */   private void checkStreamability()
/*      */     throws SQLException
/*      */   {
/*  447 */     if ((this.hasOutputParams) && (createStreamingResultSet()))
/*  448 */       throw new SQLException(Messages.getString("CallableStatement.14"), "S1C00");
/*      */   }
/*      */ 
/*      */   public synchronized void clearParameters()
/*      */     throws SQLException
/*      */   {
/*  454 */     super.clearParameters();
/*      */     try
/*      */     {
/*  457 */       if (this.outputParameterResults != null)
/*  458 */         this.outputParameterResults.close();
/*      */     }
/*      */     finally {
/*  461 */       this.outputParameterResults = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void determineParameterTypes() throws SQLException {
/*  466 */     java.sql.ResultSet paramTypesRs = null;
/*      */     try
/*      */     {
/*  469 */       String procName = extractProcedureName();
/*      */ 
/*  471 */       DatabaseMetaData dbmd = this.connection.getMetaData();
/*      */ 
/*  473 */       boolean useCatalog = false;
/*      */ 
/*  475 */       if (procName.indexOf(".") == -1) {
/*  476 */         useCatalog = true;
/*      */       }
/*      */ 
/*  479 */       paramTypesRs = dbmd.getProcedureColumns((this.connection.versionMeetsMinimum(5, 0, 2) & useCatalog) ? this.currentCatalog : null, null, procName, "%");
/*      */ 
/*  484 */       this.paramInfo = new CallableStatementParamInfo(paramTypesRs);
/*      */     } finally {
/*  486 */       SQLException sqlExRethrow = null;
/*      */ 
/*  488 */       if (paramTypesRs != null) {
/*      */         try {
/*  490 */           paramTypesRs.close();
/*      */         } catch (SQLException sqlEx) {
/*  492 */           sqlExRethrow = sqlEx;
/*      */         }
/*      */ 
/*  495 */         paramTypesRs = null;
/*      */       }
/*      */ 
/*  498 */       if (sqlExRethrow != null)
/*  499 */         throw sqlExRethrow;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean execute()
/*      */     throws SQLException
/*      */   {
/*  510 */     boolean returnVal = false;
/*      */ 
/*  512 */     checkClosed();
/*      */ 
/*  514 */     checkStreamability();
/*      */ 
/*  516 */     synchronized (this.connection.getMutex()) {
/*  517 */       setInOutParamsOnServer();
/*  518 */       setOutParams();
/*      */ 
/*  520 */       returnVal = super.execute();
/*      */ 
/*  522 */       if (this.callingStoredFunction) {
/*  523 */         this.functionReturnValueResults = this.results;
/*  524 */         this.functionReturnValueResults.next();
/*  525 */         this.results = null;
/*      */       }
/*      */ 
/*  528 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  531 */     if (!this.callingStoredFunction) {
/*  532 */       return returnVal;
/*      */     }
/*      */ 
/*  536 */     return false;
/*      */   }
/*      */ 
/*      */   public synchronized java.sql.ResultSet executeQuery()
/*      */     throws SQLException
/*      */   {
/*  545 */     checkClosed();
/*      */ 
/*  547 */     checkStreamability();
/*      */ 
/*  549 */     java.sql.ResultSet execResults = null;
/*      */ 
/*  551 */     synchronized (this.connection.getMutex()) {
/*  552 */       setInOutParamsOnServer();
/*  553 */       setOutParams();
/*      */ 
/*  555 */       execResults = super.executeQuery();
/*      */ 
/*  557 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  560 */     return execResults;
/*      */   }
/*      */ 
/*      */   public synchronized int executeUpdate()
/*      */     throws SQLException
/*      */   {
/*  569 */     int returnVal = -1;
/*      */ 
/*  571 */     checkClosed();
/*      */ 
/*  573 */     checkStreamability();
/*      */ 
/*  575 */     if (this.callingStoredFunction) {
/*  576 */       execute();
/*      */ 
/*  578 */       return -1;
/*      */     }
/*      */ 
/*  581 */     synchronized (this.connection.getMutex()) {
/*  582 */       setInOutParamsOnServer();
/*  583 */       setOutParams();
/*      */ 
/*  585 */       returnVal = super.executeUpdate();
/*      */ 
/*  587 */       retrieveOutParams();
/*      */     }
/*      */ 
/*  590 */     return returnVal;
/*      */   }
/*      */ 
/*      */   private String extractProcedureName() throws SQLException
/*      */   {
/*  595 */     int endCallIndex = StringUtils.indexOfIgnoreCase(this.originalSql, "CALL ");
/*      */ 
/*  597 */     int offset = 5;
/*      */ 
/*  599 */     if (endCallIndex == -1) {
/*  600 */       endCallIndex = StringUtils.indexOfIgnoreCase(this.originalSql, "SELECT ");
/*      */ 
/*  602 */       offset = 7;
/*      */     }
/*      */ 
/*  605 */     if (endCallIndex != -1) {
/*  606 */       StringBuffer nameBuf = new StringBuffer();
/*      */ 
/*  608 */       String trimmedStatement = this.originalSql.substring(endCallIndex + offset).trim();
/*      */ 
/*  611 */       int statementLength = trimmedStatement.length();
/*      */ 
/*  613 */       for (int i = 0; i < statementLength; i++) {
/*  614 */         char c = trimmedStatement.charAt(i);
/*      */ 
/*  616 */         if ((Character.isWhitespace(c)) || (c == '(') || (c == '?')) {
/*      */           break;
/*      */         }
/*  619 */         nameBuf.append(c);
/*      */       }
/*      */ 
/*  623 */       return nameBuf.toString();
/*      */     }
/*  625 */     throw new SQLException(Messages.getString("CallableStatement.1"), "S1000");
/*      */   }
/*      */ 
/*      */   private String fixParameterName(String paramNameIn)
/*      */     throws SQLException
/*      */   {
/*  642 */     if ((paramNameIn == null) || (paramNameIn.length() == 0)) {
/*  643 */       throw new SQLException(Messages.getString("CallableStatement.0") + paramNameIn == null ? Messages.getString("CallableStatement.15") : Messages.getString("CallableStatement.16"), "S1009");
/*      */     }
/*      */ 
/*  648 */     return mangleParameterName(paramNameIn);
/*      */   }
/*      */ 
/*      */   public synchronized Array getArray(int i)
/*      */     throws SQLException
/*      */   {
/*  663 */     ResultSet rs = getOutputParameters(i);
/*      */ 
/*  665 */     Array retValue = rs.getArray(mapOutputParameterIndexToRsIndex(i));
/*      */ 
/*  667 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  669 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Array getArray(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  677 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  680 */     Array retValue = rs.getArray(fixParameterName(parameterName));
/*      */ 
/*  682 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  684 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  692 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  694 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  697 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  699 */     return retValue;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public synchronized BigDecimal getBigDecimal(int parameterIndex, int scale)
/*      */     throws SQLException
/*      */   {
/*  720 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  722 */     BigDecimal retValue = rs.getBigDecimal(mapOutputParameterIndexToRsIndex(parameterIndex), scale);
/*      */ 
/*  725 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  727 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized BigDecimal getBigDecimal(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  735 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  738 */     BigDecimal retValue = rs.getBigDecimal(fixParameterName(parameterName));
/*      */ 
/*  740 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  742 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Blob getBlob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  749 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  751 */     Blob retValue = rs.getBlob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  754 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  756 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Blob getBlob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  763 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  766 */     Blob retValue = rs.getBlob(fixParameterName(parameterName));
/*      */ 
/*  768 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  770 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBoolean(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  778 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  780 */     boolean retValue = rs.getBoolean(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  783 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  785 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized boolean getBoolean(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  793 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  796 */     boolean retValue = rs.getBoolean(fixParameterName(parameterName));
/*      */ 
/*  798 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  800 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte getByte(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  807 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  809 */     byte retValue = rs.getByte(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  812 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  814 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte getByte(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  821 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  824 */     byte retValue = rs.getByte(fixParameterName(parameterName));
/*      */ 
/*  826 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  828 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte[] getBytes(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  835 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  837 */     byte[] retValue = rs.getBytes(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  840 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  842 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized byte[] getBytes(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  850 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  853 */     byte[] retValue = rs.getBytes(fixParameterName(parameterName));
/*      */ 
/*  855 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  857 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Clob getClob(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  864 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  866 */     Clob retValue = rs.getClob(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  869 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  871 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Clob getClob(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  878 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  881 */     Clob retValue = rs.getClob(fixParameterName(parameterName));
/*      */ 
/*  883 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  885 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  892 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  894 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  897 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  899 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*  907 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  909 */     Date retValue = rs.getDate(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/*  912 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  914 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  921 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  924 */     Date retValue = rs.getDate(fixParameterName(parameterName));
/*      */ 
/*  926 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  928 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Date getDate(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/*  937 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  940 */     Date retValue = rs.getDate(fixParameterName(parameterName), cal);
/*      */ 
/*  942 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  944 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized double getDouble(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  952 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  954 */     double retValue = rs.getDouble(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  957 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  959 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized double getDouble(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  967 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  970 */     double retValue = rs.getDouble(fixParameterName(parameterName));
/*      */ 
/*  972 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  974 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized float getFloat(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/*  981 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/*  983 */     float retValue = rs.getFloat(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/*  986 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/*  988 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized float getFloat(String parameterName)
/*      */     throws SQLException
/*      */   {
/*  996 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/*  999 */     float retValue = rs.getFloat(fixParameterName(parameterName));
/*      */ 
/* 1001 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1003 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized int getInt(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1010 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1012 */     int retValue = rs.getInt(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1015 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1017 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized int getInt(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1024 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1027 */     int retValue = rs.getInt(fixParameterName(parameterName));
/*      */ 
/* 1029 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1031 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized long getLong(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1038 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1040 */     long retValue = rs.getLong(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1043 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1045 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized long getLong(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1052 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1055 */     long retValue = rs.getLong(fixParameterName(parameterName));
/*      */ 
/* 1057 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1059 */     return retValue;
/*      */   }
/*      */ 
/*      */   private int getNamedParamIndex(String paramName, boolean forOut) throws SQLException
/*      */   {
/* 1064 */     if ((paramName == null) || (paramName.length() == 0)) {
/* 1065 */       throw new SQLException(Messages.getString("CallableStatement.2"), "S1009");
/*      */     }
/*      */ 
/* 1069 */     CallableStatementParam namedParamInfo = this.paramInfo.getParameter(paramName);
/*      */ 
/* 1072 */     if (this.paramInfo == null) {
/* 1073 */       throw new SQLException(Messages.getString("CallableStatement.3") + paramName + Messages.getString("CallableStatement.4"), "S1009");
/*      */     }
/*      */ 
/* 1078 */     if ((forOut) && (!namedParamInfo.isOut)) {
/* 1079 */       throw new SQLException(Messages.getString("CallableStatement.5") + paramName + Messages.getString("CallableStatement.6"), "S1009");
/*      */     }
/*      */ 
/* 1085 */     return namedParamInfo.index + 1;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1093 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/*      */ 
/* 1095 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1097 */     Object retVal = rs.getObjectStoredProc(mapOutputParameterIndexToRsIndex(parameterIndex), paramDescriptor.desiredJdbcType);
/*      */ 
/* 1101 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1103 */     return retVal;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(int parameterIndex, Map map)
/*      */     throws SQLException
/*      */   {
/* 1111 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1113 */     Object retVal = rs.getObject(mapOutputParameterIndexToRsIndex(parameterIndex), map);
/*      */ 
/* 1116 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1118 */     return retVal;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1126 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1129 */     Object retValue = rs.getObject(fixParameterName(parameterName));
/*      */ 
/* 1131 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1133 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Object getObject(String parameterName, Map map)
/*      */     throws SQLException
/*      */   {
/* 1142 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1145 */     Object retValue = rs.getObject(fixParameterName(parameterName), map);
/*      */ 
/* 1147 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1149 */     return retValue;
/*      */   }
/*      */ 
/*      */   private ResultSet getOutputParameters(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1163 */     this.outputParamWasNull = false;
/*      */ 
/* 1165 */     if ((paramIndex == 1) && (this.callingStoredFunction) && (this.returnValueParam != null))
/*      */     {
/* 1167 */       return this.functionReturnValueResults;
/*      */     }
/*      */ 
/* 1170 */     if (this.outputParameterResults == null) {
/* 1171 */       if (this.paramInfo.numberOfParameters() == 0) {
/* 1172 */         throw new SQLException(Messages.getString("CallableStatement.7"), "S1009");
/*      */       }
/*      */ 
/* 1176 */       throw new SQLException(Messages.getString("CallableStatement.8"), "S1000");
/*      */     }
/*      */ 
/* 1180 */     return this.outputParameterResults;
/*      */   }
/*      */ 
/*      */   public synchronized Ref getRef(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1188 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1190 */     Ref retValue = rs.getRef(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1193 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1195 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Ref getRef(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1202 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1205 */     Ref retValue = rs.getRef(fixParameterName(parameterName));
/*      */ 
/* 1207 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1209 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized short getShort(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1216 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1218 */     short retValue = rs.getShort(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1221 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1223 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized short getShort(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1231 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1234 */     short retValue = rs.getShort(fixParameterName(parameterName));
/*      */ 
/* 1236 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1238 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized String getString(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1246 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1248 */     String retValue = rs.getString(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1251 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1253 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized String getString(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1261 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1264 */     String retValue = rs.getString(fixParameterName(parameterName));
/*      */ 
/* 1266 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1268 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1275 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1277 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1280 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1282 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1290 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1292 */     Time retValue = rs.getTime(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1295 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1297 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1304 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1307 */     Time retValue = rs.getTime(fixParameterName(parameterName));
/*      */ 
/* 1309 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1311 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Time getTime(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1320 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1323 */     Time retValue = rs.getTime(fixParameterName(parameterName), cal);
/*      */ 
/* 1325 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1327 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1335 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1337 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1340 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1342 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(int parameterIndex, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1350 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1352 */     Timestamp retValue = rs.getTimestamp(mapOutputParameterIndexToRsIndex(parameterIndex), cal);
/*      */ 
/* 1355 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1357 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1365 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1368 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName));
/*      */ 
/* 1370 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1372 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized Timestamp getTimestamp(String parameterName, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1381 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1384 */     Timestamp retValue = rs.getTimestamp(fixParameterName(parameterName), cal);
/*      */ 
/* 1387 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1389 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized URL getURL(int parameterIndex)
/*      */     throws SQLException
/*      */   {
/* 1396 */     ResultSet rs = getOutputParameters(parameterIndex);
/*      */ 
/* 1398 */     URL retValue = rs.getURL(mapOutputParameterIndexToRsIndex(parameterIndex));
/*      */ 
/* 1401 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1403 */     return retValue;
/*      */   }
/*      */ 
/*      */   public synchronized URL getURL(String parameterName)
/*      */     throws SQLException
/*      */   {
/* 1410 */     ResultSet rs = getOutputParameters(0);
/*      */ 
/* 1413 */     URL retValue = rs.getURL(fixParameterName(parameterName));
/*      */ 
/* 1415 */     this.outputParamWasNull = rs.wasNull();
/*      */ 
/* 1417 */     return retValue;
/*      */   }
/*      */ 
/*      */   private int mapOutputParameterIndexToRsIndex(int paramIndex)
/*      */     throws SQLException
/*      */   {
/* 1423 */     if ((this.returnValueParam != null) && (paramIndex == 1)) {
/* 1424 */       return 1;
/*      */     }
/*      */ 
/* 1427 */     checkParameterIndexBounds(paramIndex);
/*      */ 
/* 1429 */     int localParamIndex = paramIndex - 1;
/*      */ 
/* 1431 */     int rsIndex = this.parameterIndexToRsIndex[localParamIndex];
/*      */ 
/* 1433 */     if (rsIndex == -2147483648) {
/* 1434 */       throw new SQLException(Messages.getString("CallableStatement.21") + paramIndex + Messages.getString("CallableStatement.22"), "S1009");
/*      */     }
/*      */ 
/* 1440 */     return rsIndex + 1;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1448 */     CallableStatementParam paramDescriptor = checkIsOutputParam(parameterIndex);
/* 1449 */     paramDescriptor.desiredJdbcType = sqlType;
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1457 */     registerOutParameter(parameterIndex, sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1466 */     checkIsOutputParam(parameterIndex);
/*      */   }
/*      */ 
/*      */   public synchronized void registerOutParameter(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/* 1475 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, int scale)
/*      */     throws SQLException
/*      */   {
/* 1484 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType);
/*      */   }
/*      */ 
/*      */   public void registerOutParameter(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/* 1493 */     registerOutParameter(getNamedParamIndex(parameterName, true), sqlType, typeName);
/*      */   }
/*      */ 
/*      */   private void retrieveOutParams()
/*      */     throws SQLException
/*      */   {
/* 1504 */     int numParameters = this.paramInfo.numberOfParameters();
/*      */ 
/* 1506 */     this.parameterIndexToRsIndex = new int[numParameters];
/*      */ 
/* 1508 */     for (int i = 0; i < numParameters; i++) {
/* 1509 */       this.parameterIndexToRsIndex[i] = -2147483648;
/*      */     }
/*      */ 
/* 1512 */     int localParamIndex = 0;
/*      */ 
/* 1514 */     if (numParameters > 0) {
/* 1515 */       StringBuffer outParameterQuery = new StringBuffer("SELECT ");
/*      */ 
/* 1517 */       boolean firstParam = true;
/* 1518 */       boolean hadOutputParams = false;
/*      */ 
/* 1520 */       Iterator paramIter = this.paramInfo.iterator();
/* 1521 */       while (paramIter.hasNext()) {
/* 1522 */         CallableStatementParam retrParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 1525 */         if (retrParamInfo.isOut) {
/* 1526 */           hadOutputParams = true;
/*      */ 
/* 1528 */           this.parameterIndexToRsIndex[retrParamInfo.index] = (localParamIndex++);
/*      */ 
/* 1530 */           String outParameterName = mangleParameterName(retrParamInfo.paramName);
/*      */ 
/* 1532 */           if (!firstParam)
/* 1533 */             outParameterQuery.append(",");
/*      */           else {
/* 1535 */             firstParam = false;
/*      */           }
/*      */ 
/* 1538 */           if (!outParameterName.startsWith("@")) {
/* 1539 */             outParameterQuery.append('@');
/*      */           }
/*      */ 
/* 1542 */           outParameterQuery.append(outParameterName);
/*      */         }
/*      */       }
/*      */ 
/* 1546 */       if (hadOutputParams)
/*      */       {
/* 1549 */         Statement outParameterStmt = null;
/* 1550 */         java.sql.ResultSet outParamRs = null;
/*      */         try
/*      */         {
/* 1553 */           outParameterStmt = this.connection.createStatement();
/* 1554 */           outParamRs = outParameterStmt.executeQuery(outParameterQuery.toString());
/*      */ 
/* 1556 */           this.outputParameterResults = ((ResultSet)outParamRs).copy();
/*      */ 
/* 1559 */           if (!this.outputParameterResults.next()) {
/* 1560 */             this.outputParameterResults.close();
/* 1561 */             this.outputParameterResults = null;
/*      */           }
/*      */         } finally {
/* 1564 */           if (outParameterStmt != null)
/* 1565 */             outParameterStmt.close();
/*      */         }
/*      */       }
/*      */       else {
/* 1569 */         this.outputParameterResults = null;
/*      */       }
/*      */     } else {
/* 1572 */       this.outputParameterResults = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAsciiStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1582 */     setAsciiStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBigDecimal(String parameterName, BigDecimal x)
/*      */     throws SQLException
/*      */   {
/* 1591 */     setBigDecimal(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBinaryStream(String parameterName, InputStream x, int length)
/*      */     throws SQLException
/*      */   {
/* 1600 */     setBinaryStream(getNamedParamIndex(parameterName, false), x, length);
/*      */   }
/*      */ 
/*      */   public void setBoolean(String parameterName, boolean x)
/*      */     throws SQLException
/*      */   {
/* 1607 */     setBoolean(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setByte(String parameterName, byte x)
/*      */     throws SQLException
/*      */   {
/* 1614 */     setByte(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setBytes(String parameterName, byte[] x)
/*      */     throws SQLException
/*      */   {
/* 1621 */     setBytes(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setCharacterStream(String parameterName, Reader reader, int length)
/*      */     throws SQLException
/*      */   {
/* 1630 */     setCharacterStream(getNamedParamIndex(parameterName, false), reader, length);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x)
/*      */     throws SQLException
/*      */   {
/* 1638 */     setDate(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setDate(String parameterName, Date x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1647 */     setDate(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setDouble(String parameterName, double x)
/*      */     throws SQLException
/*      */   {
/* 1654 */     setDouble(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setFloat(String parameterName, float x)
/*      */     throws SQLException
/*      */   {
/* 1661 */     setFloat(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   private void setInOutParamsOnServer()
/*      */     throws SQLException
/*      */   {
/* 1668 */     if (this.paramInfo.numParameters > 0) {
/* 1669 */       int parameterIndex = 0;
/*      */ 
/* 1671 */       Iterator paramIter = this.paramInfo.iterator();
/* 1672 */       while (paramIter.hasNext())
/*      */       {
/* 1674 */         CallableStatementParam inParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 1677 */         if ((inParamInfo.isOut) && (inParamInfo.isIn)) {
/* 1678 */           String inOutParameterName = mangleParameterName(inParamInfo.paramName);
/* 1679 */           StringBuffer queryBuf = new StringBuffer(4 + inOutParameterName.length() + 1 + 1);
/*      */ 
/* 1681 */           queryBuf.append("SET ");
/* 1682 */           queryBuf.append(inOutParameterName);
/* 1683 */           queryBuf.append("=?");
/*      */ 
/* 1685 */           PreparedStatement setPstmt = null;
/*      */           try
/*      */           {
/* 1688 */             setPstmt = this.connection.clientPrepareStatement(queryBuf.toString());
/*      */ 
/* 1691 */             byte[] parameterAsBytes = getBytesRepresentation(parameterIndex);
/*      */ 
/* 1694 */             if (parameterAsBytes != null) {
/* 1695 */               if ((parameterAsBytes.length > 8) && (parameterAsBytes[0] == 95) && (parameterAsBytes[1] == 98) && (parameterAsBytes[2] == 105) && (parameterAsBytes[3] == 110) && (parameterAsBytes[4] == 97) && (parameterAsBytes[5] == 114) && (parameterAsBytes[6] == 121) && (parameterAsBytes[7] == 39))
/*      */               {
/* 1704 */                 setPstmt.setBytesNoEscapeNoQuotes(1, parameterAsBytes);
/*      */               }
/*      */               else
/* 1707 */                 setPstmt.setBytes(1, parameterAsBytes);
/*      */             }
/*      */             else {
/* 1710 */               setPstmt.setNull(1, 0);
/*      */             }
/*      */ 
/* 1713 */             setPstmt.executeUpdate();
/*      */           } finally {
/* 1715 */             if (setPstmt != null) {
/* 1716 */               setPstmt.close();
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1732 */       parameterIndex++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setInt(String parameterName, int x)
/*      */     throws SQLException
/*      */   {
/* 1741 */     setInt(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setLong(String parameterName, long x)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setNull(String parameterName, int sqlType, String typeName)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x)
/*      */     throws SQLException
/*      */   {
/* 1769 */     setObject(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType)
/*      */     throws SQLException
/*      */   {
/* 1778 */     setObject(getNamedParamIndex(parameterName, false), x, targetSqlType);
/*      */   }
/*      */ 
/*      */   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
/*      */     throws SQLException
/*      */   {
/*      */   }
/*      */ 
/*      */   private void setOutParams()
/*      */     throws SQLException
/*      */   {
/* 1790 */     if (this.paramInfo.numParameters > 0) {
/* 1791 */       Iterator paramIter = this.paramInfo.iterator();
/* 1792 */       while (paramIter.hasNext()) {
/* 1793 */         CallableStatementParam outParamInfo = (CallableStatementParam)paramIter.next();
/*      */ 
/* 1796 */         if (outParamInfo.isOut) {
/* 1797 */           String outParameterName = mangleParameterName(outParamInfo.paramName);
/*      */ 
/* 1799 */           setBytesNoEscapeNoQuotes(outParamInfo.index + 1, StringUtils.getBytes(outParameterName, this.charConverter, this.charEncoding, this.connection.getServerCharacterEncoding(), this.connection.parserKnowsUnicode()));
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setShort(String parameterName, short x)
/*      */     throws SQLException
/*      */   {
/* 1814 */     setShort(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setString(String parameterName, String x)
/*      */     throws SQLException
/*      */   {
/* 1822 */     setString(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x)
/*      */     throws SQLException
/*      */   {
/* 1829 */     setTime(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTime(String parameterName, Time x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1838 */     setTime(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x)
/*      */     throws SQLException
/*      */   {
/* 1847 */     setTimestamp(getNamedParamIndex(parameterName, false), x);
/*      */   }
/*      */ 
/*      */   public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
/*      */     throws SQLException
/*      */   {
/* 1856 */     setTimestamp(getNamedParamIndex(parameterName, false), x, cal);
/*      */   }
/*      */ 
/*      */   public void setURL(String parameterName, URL val)
/*      */     throws SQLException
/*      */   {
/* 1863 */     setURL(getNamedParamIndex(parameterName, false), val);
/*      */   }
/*      */ 
/*      */   public synchronized boolean wasNull()
/*      */     throws SQLException
/*      */   {
/* 1870 */     return this.outputParamWasNull;
/*      */   }
/*      */ 
/*      */   class CallableStatementParamInfo
/*      */     implements ParameterMetaData
/*      */   {
/*      */     String catalogInUse;
/*      */     boolean isFunctionCall;
/*      */     String nativeSql;
/*      */     int numParameters;
/*      */     List parameterList;
/*      */     Map parameterMap;
/*      */ 
/*      */     CallableStatementParamInfo(java.sql.ResultSet paramTypesRs)
/*      */       throws SQLException
/*      */     {
/*  122 */       boolean hadRows = paramTypesRs.last();
/*      */ 
/*  124 */       this.nativeSql = CallableStatement.this.originalSql;
/*  125 */       this.catalogInUse = CallableStatement.this.currentCatalog;
/*  126 */       this.isFunctionCall = CallableStatement.this.callingStoredFunction;
/*      */ 
/*  128 */       if (hadRows) {
/*  129 */         this.numParameters = paramTypesRs.getRow();
/*      */ 
/*  131 */         this.parameterList = new ArrayList(this.numParameters);
/*  132 */         this.parameterMap = new HashMap(this.numParameters);
/*      */ 
/*  134 */         paramTypesRs.beforeFirst();
/*      */ 
/*  136 */         addParametersFromDBMD(paramTypesRs);
/*      */       } else {
/*  138 */         this.numParameters = 0;
/*      */       }
/*      */     }
/*      */ 
/*      */     private void addParametersFromDBMD(java.sql.ResultSet paramTypesRs) throws SQLException
/*      */     {
/*  144 */       int i = 0;
/*      */ 
/*  146 */       if (this.isFunctionCall)
/*      */       {
/*  148 */         paramTypesRs.next();
/*      */       }
/*      */ 
/*  151 */       while (paramTypesRs.next()) {
/*  152 */         String paramName = paramTypesRs.getString(4);
/*  153 */         int inOutModifier = paramTypesRs.getInt(5);
/*      */ 
/*  155 */         boolean isOutParameter = false;
/*  156 */         boolean isInParameter = false;
/*      */ 
/*  158 */         if (inOutModifier == 2) {
/*  159 */           isOutParameter = true;
/*  160 */           isInParameter = true;
/*  161 */         } else if (inOutModifier == 1) {
/*  162 */           isOutParameter = false;
/*  163 */           isInParameter = true;
/*  164 */         } else if (inOutModifier == 4) {
/*  165 */           isOutParameter = true;
/*  166 */           isInParameter = false;
/*      */         }
/*      */ 
/*  169 */         int jdbcType = paramTypesRs.getInt(6);
/*  170 */         String typeName = paramTypesRs.getString(7);
/*  171 */         int precision = paramTypesRs.getInt(8);
/*  172 */         int scale = paramTypesRs.getInt(10);
/*  173 */         short nullability = paramTypesRs.getShort(12);
/*      */ 
/*  175 */         CallableStatement.CallableStatementParam paramInfoToAdd = new CallableStatement.CallableStatementParam(CallableStatement.this, paramName, i++, isInParameter, isOutParameter, jdbcType, typeName, precision, scale, nullability, inOutModifier);
/*      */ 
/*  180 */         this.parameterList.add(paramInfoToAdd);
/*  181 */         this.parameterMap.put(paramName, paramInfoToAdd);
/*      */       }
/*      */     }
/*      */ 
/*      */     protected void checkBounds(int paramIndex) throws SQLException {
/*  186 */       int localParamIndex = paramIndex - 1;
/*      */ 
/*  188 */       if ((paramIndex < 0) || (localParamIndex >= this.numParameters))
/*      */       {
/*  190 */         throw new SQLException(Messages.getString("CallableStatement.11") + paramIndex + Messages.getString("CallableStatement.12") + this.numParameters + Messages.getString("CallableStatement.13"), "S1009");
/*      */       }
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  204 */       return super.clone();
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(int index) {
/*  208 */       return (CallableStatement.CallableStatementParam)this.parameterList.get(index);
/*      */     }
/*      */ 
/*      */     CallableStatement.CallableStatementParam getParameter(String name) {
/*  212 */       return (CallableStatement.CallableStatementParam)this.parameterMap.get(name);
/*      */     }
/*      */ 
/*      */     public String getParameterClassName(int arg0) throws SQLException
/*      */     {
/*  217 */       return null;
/*      */     }
/*      */ 
/*      */     public int getParameterCount() throws SQLException {
/*  221 */       return this.parameterList.size();
/*      */     }
/*      */ 
/*      */     public int getParameterMode(int arg0) throws SQLException {
/*  225 */       checkBounds(arg0);
/*      */ 
/*  227 */       return getParameter(arg0 - 1).inOutModifier;
/*      */     }
/*      */ 
/*      */     public int getParameterType(int arg0) throws SQLException {
/*  231 */       checkBounds(arg0);
/*      */ 
/*  233 */       return getParameter(arg0 - 1).jdbcType;
/*      */     }
/*      */ 
/*      */     public String getParameterTypeName(int arg0) throws SQLException {
/*  237 */       checkBounds(arg0);
/*      */ 
/*  239 */       return getParameter(arg0 - 1).typeName;
/*      */     }
/*      */ 
/*      */     public int getPrecision(int arg0) throws SQLException {
/*  243 */       checkBounds(arg0);
/*      */ 
/*  245 */       return getParameter(arg0 - 1).precision;
/*      */     }
/*      */ 
/*      */     public int getScale(int arg0) throws SQLException {
/*  249 */       checkBounds(arg0);
/*      */ 
/*  251 */       return getParameter(arg0 - 1).scale;
/*      */     }
/*      */ 
/*      */     public int isNullable(int arg0) throws SQLException {
/*  255 */       checkBounds(arg0);
/*      */ 
/*  257 */       return getParameter(arg0 - 1).nullability;
/*      */     }
/*      */ 
/*      */     public boolean isSigned(int arg0) throws SQLException {
/*  261 */       checkBounds(arg0);
/*      */ 
/*  263 */       return false;
/*      */     }
/*      */ 
/*      */     Iterator iterator() {
/*  267 */       return this.parameterList.iterator();
/*      */     }
/*      */ 
/*      */     int numberOfParameters() {
/*  271 */       return this.numParameters;
/*      */     }
/*      */   }
/*      */ 
/*      */   class CallableStatementParam
/*      */   {
/*      */     int desiredJdbcType;
/*      */     int index;
/*      */     int inOutModifier;
/*      */     boolean isIn;
/*      */     boolean isOut;
/*      */     int jdbcType;
/*      */     short nullability;
/*      */     String paramName;
/*      */     int precision;
/*      */     int scale;
/*      */     String typeName;
/*      */ 
/*      */     CallableStatementParam(String name, int idx, boolean in, boolean out, int jdbcType, String typeName, int precision, int scale, short nullability, int inOutModifier)
/*      */     {
/*   84 */       this.paramName = name;
/*   85 */       this.isIn = in;
/*   86 */       this.isOut = out;
/*   87 */       this.index = idx;
/*      */ 
/*   89 */       this.jdbcType = jdbcType;
/*   90 */       this.typeName = typeName;
/*   91 */       this.precision = precision;
/*   92 */       this.scale = scale;
/*   93 */       this.nullability = nullability;
/*   94 */       this.inOutModifier = inOutModifier;
/*      */     }
/*      */ 
/*      */     protected Object clone()
/*      */       throws CloneNotSupportedException
/*      */     {
/*  103 */       return super.clone();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.CallableStatement
 * JD-Core Version:    0.6.0
 */
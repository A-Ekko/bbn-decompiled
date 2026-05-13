/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ 
/*     */ public class Field
/*     */ {
/*     */   private static final int AUTO_INCREMENT_FLAG = 512;
/*     */   private static final int NO_CHARSET_INFO = -1;
/*     */   private byte[] buffer;
/*  50 */   private int charsetIndex = 0;
/*     */ 
/*  52 */   private String charsetName = null;
/*     */   private int colDecimals;
/*     */   private short colFlag;
/*  58 */   private String collationName = null;
/*     */ 
/*  60 */   private Connection connection = null;
/*     */ 
/*  62 */   private String databaseName = null;
/*     */ 
/*  64 */   private int databaseNameLength = -1;
/*     */ 
/*  67 */   private int databaseNameStart = -1;
/*     */ 
/*  69 */   private int defaultValueLength = -1;
/*     */ 
/*  72 */   private int defaultValueStart = -1;
/*     */ 
/*  74 */   private String fullName = null;
/*     */ 
/*  76 */   private String fullOriginalName = null;
/*     */ 
/*  78 */   private boolean isImplicitTempTable = false;
/*     */   private long length;
/*  82 */   private int mysqlType = -1;
/*     */   private String name;
/*     */   private int nameLength;
/*     */   private int nameStart;
/*  90 */   private String originalColumnName = null;
/*     */ 
/*  92 */   private int originalColumnNameLength = -1;
/*     */ 
/*  95 */   private int originalColumnNameStart = -1;
/*     */ 
/*  97 */   private String originalTableName = null;
/*     */ 
/*  99 */   private int originalTableNameLength = -1;
/*     */ 
/* 102 */   private int originalTableNameStart = -1;
/*     */ 
/* 104 */   private int precisionAdjustFactor = 0;
/*     */ 
/* 106 */   private int sqlType = -1;
/*     */   private String tableName;
/*     */   private int tableNameLength;
/*     */   private int tableNameStart;
/* 114 */   private boolean useOldNameMetadata = false;
/*     */ 
/*     */   Field(Connection conn, byte[] buffer, int databaseNameStart, int databaseNameLength, int tableNameStart, int tableNameLength, int originalTableNameStart, int originalTableNameLength, int nameStart, int nameLength, int originalColumnNameStart, int originalColumnNameLength, long length, int mysqlType, short colFlag, int colDecimals, int defaultValueStart, int defaultValueLength, int charsetIndex)
/*     */     throws SQLException
/*     */   {
/* 129 */     this.connection = conn;
/* 130 */     this.buffer = buffer;
/* 131 */     this.nameStart = nameStart;
/* 132 */     this.nameLength = nameLength;
/* 133 */     this.tableNameStart = tableNameStart;
/* 134 */     this.tableNameLength = tableNameLength;
/* 135 */     this.length = length;
/* 136 */     this.colFlag = colFlag;
/* 137 */     this.colDecimals = colDecimals;
/* 138 */     this.mysqlType = mysqlType;
/*     */ 
/* 141 */     this.databaseNameStart = databaseNameStart;
/* 142 */     this.databaseNameLength = databaseNameLength;
/*     */ 
/* 144 */     this.originalTableNameStart = originalTableNameStart;
/* 145 */     this.originalTableNameLength = originalTableNameLength;
/*     */ 
/* 147 */     this.originalColumnNameStart = originalColumnNameStart;
/* 148 */     this.originalColumnNameLength = originalColumnNameLength;
/*     */ 
/* 150 */     this.defaultValueStart = defaultValueStart;
/* 151 */     this.defaultValueLength = defaultValueLength;
/*     */ 
/* 155 */     this.charsetIndex = charsetIndex;
/*     */ 
/* 157 */     this.charsetName = this.connection.getCharsetNameForIndex(this.charsetIndex);
/*     */ 
/* 161 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */ 
/* 165 */     if (this.mysqlType == 252) {
/* 166 */       if ((this.charsetIndex == 63) || (!this.connection.versionMeetsMinimum(4, 1, 0)))
/*     */       {
/* 168 */         setBlobTypeBasedOnLength();
/* 169 */         this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */       }
/*     */       else {
/* 172 */         this.mysqlType = 253;
/* 173 */         this.sqlType = -1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 180 */     boolean isBinary = isBinary();
/*     */ 
/* 182 */     if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 253) && (isBinary) && (this.charsetIndex == 63))
/*     */     {
/* 186 */       if (isOpaqueBinary()) {
/* 187 */         this.sqlType = -3;
/*     */       }
/*     */     }
/*     */ 
/* 191 */     if ((this.connection.versionMeetsMinimum(4, 1, 0)) && (this.mysqlType == 254) && (isBinary) && (this.charsetIndex == 63))
/*     */     {
/* 201 */       if (isOpaqueBinary()) {
/* 202 */         this.sqlType = -2;
/*     */       }
/*     */     }
/*     */ 
/* 206 */     if ((this.sqlType == -6) && (this.length == 1L) && (this.connection.getTinyInt1isBit()))
/*     */     {
/* 209 */       if (conn.getTinyInt1isBit()) {
/* 210 */         if (conn.getTransformedBitIsBoolean())
/* 211 */           this.sqlType = 16;
/*     */         else {
/* 213 */           this.sqlType = -7;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 219 */     if (this.mysqlType == 16) {
/* 220 */       if (this.length == 0L) {
/* 221 */         this.sqlType = -7;
/*     */       } else {
/* 223 */         this.sqlType = -3;
/* 224 */         this.colFlag = (short)(this.colFlag | 0x80);
/* 225 */         this.colFlag = (short)(this.colFlag | 0x10);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 234 */     if ((this.sqlType == -4) && (!isBinary))
/* 235 */       this.sqlType = -1;
/* 236 */     else if ((this.sqlType == -3) && (!isBinary)) {
/* 237 */       this.sqlType = 12;
/*     */     }
/*     */ 
/* 243 */     if (!isUnsigned()) {
/* 244 */       switch (this.mysqlType) {
/*     */       case 0:
/*     */       case 246:
/* 247 */         this.precisionAdjustFactor = -1;
/*     */ 
/* 249 */         break;
/*     */       case 4:
/*     */       case 5:
/* 252 */         this.precisionAdjustFactor = 1;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 257 */       switch (this.mysqlType) {
/*     */       case 4:
/*     */       case 5:
/* 260 */         this.precisionAdjustFactor = 1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 266 */     checkForImplicitTemporaryTable();
/*     */   }
/*     */ 
/*     */   Field(Connection conn, byte[] buffer, int nameStart, int nameLength, int tableNameStart, int tableNameLength, int length, int mysqlType, short colFlag, int colDecimals)
/*     */     throws SQLException
/*     */   {
/* 275 */     this(conn, buffer, -1, -1, tableNameStart, tableNameLength, -1, -1, nameStart, nameLength, -1, -1, length, mysqlType, colFlag, colDecimals, -1, -1, -1);
/*     */   }
/*     */ 
/*     */   Field(String tableName, String columnName, int jdbcType, int length)
/*     */   {
/* 284 */     this.tableName = tableName;
/* 285 */     this.name = columnName;
/* 286 */     this.length = length;
/* 287 */     this.sqlType = jdbcType;
/* 288 */     this.colFlag = 0;
/* 289 */     this.colDecimals = 0;
/*     */   }
/*     */ 
/*     */   private void checkForImplicitTemporaryTable()
/*     */   {
/* 296 */     this.isImplicitTempTable = ((this.tableNameLength > 5) && (this.buffer[this.tableNameStart] == 35) && (this.buffer[(this.tableNameStart + 1)] == 115) && (this.buffer[(this.tableNameStart + 2)] == 113) && (this.buffer[(this.tableNameStart + 3)] == 108) && (this.buffer[(this.tableNameStart + 4)] == 95));
/*     */   }
/*     */ 
/*     */   public String getCharacterSet()
/*     */   {
/* 310 */     return this.charsetName;
/*     */   }
/*     */ 
/*     */   public synchronized String getCollation() throws SQLException {
/* 314 */     if ((this.collationName == null) && 
/* 315 */       (this.connection != null) && 
/* 316 */       (this.connection.versionMeetsMinimum(4, 1, 0))) {
/* 317 */       DatabaseMetaData dbmd = this.connection.getMetaData();
/*     */ 
/* 320 */       String quotedIdStr = dbmd.getIdentifierQuoteString();
/*     */ 
/* 322 */       if (" ".equals(quotedIdStr)) {
/* 323 */         quotedIdStr = "";
/*     */       }
/*     */ 
/* 326 */       String csCatalogName = getDatabaseName();
/* 327 */       String csTableName = getOriginalTableName();
/* 328 */       String csColumnName = getOriginalName();
/*     */ 
/* 330 */       if ((csCatalogName != null) && (csCatalogName.length() != 0) && (csTableName != null) && (csTableName.length() != 0) && (csColumnName != null) && (csColumnName.length() != 0))
/*     */       {
/* 334 */         StringBuffer queryBuf = new StringBuffer(csCatalogName.length() + csTableName.length() + 28);
/*     */ 
/* 337 */         queryBuf.append("SHOW FULL COLUMNS FROM ");
/* 338 */         queryBuf.append(quotedIdStr);
/* 339 */         queryBuf.append(csCatalogName);
/* 340 */         queryBuf.append(quotedIdStr);
/* 341 */         queryBuf.append(".");
/* 342 */         queryBuf.append(quotedIdStr);
/* 343 */         queryBuf.append(csTableName);
/* 344 */         queryBuf.append(quotedIdStr);
/*     */ 
/* 346 */         Statement collationStmt = null;
/* 347 */         ResultSet collationRs = null;
/*     */         try
/*     */         {
/* 350 */           collationStmt = this.connection.createStatement();
/*     */ 
/* 352 */           collationRs = collationStmt.executeQuery(queryBuf.toString());
/*     */ 
/* 355 */           while (collationRs.next()) {
/* 356 */             if (!csColumnName.equals(collationRs.getString("Field")))
/*     */               continue;
/* 358 */             this.collationName = collationRs.getString("Collation");
/*     */           }
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 365 */           if (collationRs != null) {
/* 366 */             collationRs.close();
/* 367 */             collationRs = null;
/*     */           }
/*     */ 
/* 370 */           if (collationStmt != null) {
/* 371 */             collationStmt.close();
/* 372 */             collationStmt = null;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 383 */     return this.collationName;
/*     */   }
/*     */ 
/*     */   public String getColumnLabel() throws SQLException {
/* 387 */     return getName();
/*     */   }
/*     */ 
/*     */   public String getDatabaseName()
/*     */     throws SQLException
/*     */   {
/* 396 */     if ((this.databaseName == null) && (this.databaseNameStart != -1) && (this.databaseNameLength != -1))
/*     */     {
/* 398 */       this.databaseName = getStringFromBytes(this.databaseNameStart, this.databaseNameLength);
/*     */     }
/*     */ 
/* 402 */     return this.databaseName;
/*     */   }
/*     */ 
/*     */   int getDecimals() {
/* 406 */     return this.colDecimals;
/*     */   }
/*     */ 
/*     */   public String getFullName()
/*     */     throws SQLException
/*     */   {
/* 415 */     if (this.fullName == null) {
/* 416 */       StringBuffer fullNameBuf = new StringBuffer(getTableName().length() + 1 + getName().length());
/*     */ 
/* 418 */       fullNameBuf.append(this.tableName);
/*     */ 
/* 421 */       fullNameBuf.append('.');
/* 422 */       fullNameBuf.append(this.name);
/* 423 */       this.fullName = fullNameBuf.toString();
/* 424 */       fullNameBuf = null;
/*     */     }
/*     */ 
/* 427 */     return this.fullName;
/*     */   }
/*     */ 
/*     */   public String getFullOriginalName()
/*     */     throws SQLException
/*     */   {
/* 436 */     getOriginalName();
/*     */ 
/* 438 */     if (this.originalColumnName == null) {
/* 439 */       return null;
/*     */     }
/*     */ 
/* 442 */     if (this.fullName == null) {
/* 443 */       StringBuffer fullOriginalNameBuf = new StringBuffer(getOriginalTableName().length() + 1 + getOriginalName().length());
/*     */ 
/* 446 */       fullOriginalNameBuf.append(this.originalTableName);
/*     */ 
/* 449 */       fullOriginalNameBuf.append('.');
/* 450 */       fullOriginalNameBuf.append(this.originalColumnName);
/* 451 */       this.fullOriginalName = fullOriginalNameBuf.toString();
/* 452 */       fullOriginalNameBuf = null;
/*     */     }
/*     */ 
/* 455 */     return this.fullOriginalName;
/*     */   }
/*     */ 
/*     */   public long getLength()
/*     */   {
/* 464 */     return this.length;
/*     */   }
/*     */ 
/*     */   public int getMaxBytesPerCharacter() throws SQLException {
/* 468 */     return this.connection.getMaxBytesPerChar(getCharacterSet());
/*     */   }
/*     */ 
/*     */   public int getMysqlType()
/*     */   {
/* 477 */     return this.mysqlType;
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */     throws SQLException
/*     */   {
/* 486 */     if (this.name == null) {
/* 487 */       this.name = getStringFromBytes(this.nameStart, this.nameLength);
/*     */     }
/*     */ 
/* 490 */     return this.name;
/*     */   }
/*     */ 
/*     */   public String getNameNoAliases() throws SQLException {
/* 494 */     if (this.useOldNameMetadata) {
/* 495 */       return getName();
/*     */     }
/*     */ 
/* 498 */     if ((this.connection != null) && (this.connection.versionMeetsMinimum(4, 1, 0)))
/*     */     {
/* 500 */       return getOriginalName();
/*     */     }
/*     */ 
/* 503 */     return getName();
/*     */   }
/*     */ 
/*     */   public String getOriginalName()
/*     */     throws SQLException
/*     */   {
/* 512 */     if ((this.originalColumnName == null) && (this.originalColumnNameStart != -1) && (this.originalColumnNameLength != -1))
/*     */     {
/* 515 */       this.originalColumnName = getStringFromBytes(this.originalColumnNameStart, this.originalColumnNameLength);
/*     */     }
/*     */ 
/* 519 */     return this.originalColumnName;
/*     */   }
/*     */ 
/*     */   public String getOriginalTableName()
/*     */     throws SQLException
/*     */   {
/* 528 */     if ((this.originalTableName == null) && (this.originalTableNameStart != -1) && (this.originalTableNameLength != -1))
/*     */     {
/* 531 */       this.originalTableName = getStringFromBytes(this.originalTableNameStart, this.originalTableNameLength);
/*     */     }
/*     */ 
/* 535 */     return this.originalTableName;
/*     */   }
/*     */ 
/*     */   public int getPrecisionAdjustFactor()
/*     */   {
/* 547 */     return this.precisionAdjustFactor;
/*     */   }
/*     */ 
/*     */   public int getSQLType()
/*     */   {
/* 556 */     return this.sqlType;
/*     */   }
/*     */ 
/*     */   private String getStringFromBytes(int stringStart, int stringLength)
/*     */     throws SQLException
/*     */   {
/* 565 */     if ((stringStart == -1) || (stringLength == -1)) {
/* 566 */       return null;
/*     */     }
/*     */ 
/* 569 */     String stringVal = null;
/*     */ 
/* 571 */     if (this.connection != null) {
/* 572 */       if (this.connection.getUseUnicode()) {
/* 573 */         String encoding = this.connection.getCharacterSetMetadata();
/*     */ 
/* 575 */         if (encoding == null) {
/* 576 */           encoding = this.connection.getEncoding();
/*     */         }
/*     */ 
/* 579 */         if (encoding != null) {
/* 580 */           SingleByteCharsetConverter converter = null;
/*     */ 
/* 582 */           if (this.connection != null) {
/* 583 */             converter = this.connection.getCharsetConverter(encoding);
/*     */           }
/*     */ 
/* 587 */           if (converter != null) {
/* 588 */             stringVal = converter.toString(this.buffer, stringStart, stringLength);
/*     */           }
/*     */           else
/*     */           {
/* 592 */             byte[] stringBytes = new byte[stringLength];
/*     */ 
/* 594 */             int endIndex = stringStart + stringLength;
/* 595 */             int pos = 0;
/*     */ 
/* 597 */             for (int i = stringStart; i < endIndex; i++) {
/* 598 */               stringBytes[(pos++)] = this.buffer[i];
/*     */             }
/*     */             try
/*     */             {
/* 602 */               stringVal = new String(stringBytes, encoding);
/*     */             } catch (UnsupportedEncodingException ue) {
/* 604 */               throw new RuntimeException(Messages.getString("Field.12") + encoding + Messages.getString("Field.13"));
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 611 */           stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 616 */         stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 621 */       stringVal = StringUtils.toAsciiString(this.buffer, stringStart, stringLength);
/*     */     }
/*     */ 
/* 625 */     return stringVal;
/*     */   }
/*     */ 
/*     */   public String getTable()
/*     */     throws SQLException
/*     */   {
/* 634 */     return getTableName();
/*     */   }
/*     */ 
/*     */   public String getTableName()
/*     */     throws SQLException
/*     */   {
/* 643 */     if (this.tableName == null) {
/* 644 */       this.tableName = getStringFromBytes(this.tableNameStart, this.tableNameLength);
/*     */     }
/*     */ 
/* 648 */     return this.tableName;
/*     */   }
/*     */ 
/*     */   public String getTableNameNoAliases() throws SQLException {
/* 652 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 653 */       return getOriginalTableName();
/*     */     }
/*     */ 
/* 656 */     return getTableName();
/*     */   }
/*     */ 
/*     */   public boolean isAutoIncrement()
/*     */   {
/* 665 */     return (this.colFlag & 0x200) > 0;
/*     */   }
/*     */ 
/*     */   public boolean isBinary()
/*     */   {
/* 674 */     return (this.colFlag & 0x80) > 0;
/*     */   }
/*     */ 
/*     */   public boolean isBlob()
/*     */   {
/* 683 */     return (this.colFlag & 0x10) > 0;
/*     */   }
/*     */ 
/*     */   private boolean isImplicitTemporaryTable()
/*     */   {
/* 692 */     return this.isImplicitTempTable;
/*     */   }
/*     */ 
/*     */   public boolean isMultipleKey()
/*     */   {
/* 701 */     return (this.colFlag & 0x8) > 0;
/*     */   }
/*     */ 
/*     */   boolean isNotNull() {
/* 705 */     return (this.colFlag & 0x1) > 0;
/*     */   }
/*     */ 
/*     */   boolean isOpaqueBinary()
/*     */     throws SQLException
/*     */   {
/* 715 */     if ((this.charsetIndex == 63) && (isBinary()) && ((getMysqlType() == 254) || (getMysqlType() == 253)))
/*     */     {
/* 719 */       if (this.originalTableNameLength == 0) {
/* 720 */         return false;
/*     */       }
/*     */ 
/* 726 */       return !isImplicitTemporaryTable();
/*     */     }
/*     */ 
/* 729 */     return (this.connection.versionMeetsMinimum(4, 1, 0)) && ("binary".equalsIgnoreCase(getCharacterSet()));
/*     */   }
/*     */ 
/*     */   public boolean isPrimaryKey()
/*     */   {
/* 740 */     return (this.colFlag & 0x2) > 0;
/*     */   }
/*     */ 
/*     */   boolean isReadOnly()
/*     */     throws SQLException
/*     */   {
/* 750 */     if (this.connection.versionMeetsMinimum(4, 1, 0)) {
/* 751 */       String orgColumnName = getOriginalName();
/* 752 */       String orgTableName = getOriginalTableName();
/*     */ 
/* 754 */       return (orgColumnName == null) || (orgColumnName.length() <= 0) || (orgTableName == null) || (orgTableName.length() <= 0);
/*     */     }
/*     */ 
/* 758 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isUniqueKey()
/*     */   {
/* 767 */     return (this.colFlag & 0x4) > 0;
/*     */   }
/*     */ 
/*     */   public boolean isUnsigned()
/*     */   {
/* 776 */     return (this.colFlag & 0x20) > 0;
/*     */   }
/*     */ 
/*     */   public boolean isZeroFill()
/*     */   {
/* 785 */     return (this.colFlag & 0x40) > 0;
/*     */   }
/*     */ 
/*     */   private void setBlobTypeBasedOnLength()
/*     */   {
/* 794 */     if (this.length == 255L)
/* 795 */       this.mysqlType = 249;
/* 796 */     else if (this.length == 65535L)
/* 797 */       this.mysqlType = 252;
/* 798 */     else if (this.length == 16777215L)
/* 799 */       this.mysqlType = 250;
/* 800 */     else if (this.length == 4294967295L)
/* 801 */       this.mysqlType = 251;
/*     */   }
/*     */ 
/*     */   public void setConnection(Connection conn)
/*     */   {
/* 812 */     this.connection = conn;
/*     */ 
/* 814 */     this.charsetName = this.connection.getEncoding();
/*     */   }
/*     */ 
/*     */   void setMysqlType(int type) {
/* 818 */     this.mysqlType = type;
/* 819 */     this.sqlType = MysqlDefs.mysqlToJavaType(this.mysqlType);
/*     */   }
/*     */ 
/*     */   protected void setUseOldNameMetadata(boolean useOldNameMetadata) {
/* 823 */     this.useOldNameMetadata = useOldNameMetadata;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 833 */       return getDatabaseName() + " . " + getTableName() + "(" + getOriginalTableName() + ") . " + getName() + "(" + getOriginalName() + ")" + ", Mysql type: " + getMysqlType();
/*     */     } catch (SQLException sqlEx) {
/*     */     }
/* 836 */     return super.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.Field
 * JD-Core Version:    0.6.0
 */
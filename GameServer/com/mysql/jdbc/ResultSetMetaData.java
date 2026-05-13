/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class ResultSetMetaData
/*     */   implements java.sql.ResultSetMetaData
/*     */ {
/*     */   Field[] fields;
/*     */ 
/*     */   private static int clampedGetLength(Field f)
/*     */   {
/*  42 */     long fieldLength = f.getLength();
/*     */ 
/*  44 */     if (fieldLength > 2147483647L) {
/*  45 */       fieldLength = 2147483647L;
/*     */     }
/*     */ 
/*  48 */     return (int)fieldLength;
/*     */   }
/*     */ 
/*     */   private static final boolean isDecimalType(int type)
/*     */   {
/*  60 */     switch (type) {
/*     */     case -7:
/*     */     case -6:
/*     */     case -5:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*  71 */       return true;
/*     */     case -4:
/*     */     case -3:
/*     */     case -2:
/*     */     case -1:
/*     */     case 0:
/*  74 */     case 1: } return false;
/*     */   }
/*     */ 
/*     */   public ResultSetMetaData(Field[] fields)
/*     */   {
/*  86 */     this.fields = fields;
/*     */   }
/*     */ 
/*     */   public String getCatalogName(int column)
/*     */     throws SQLException
/*     */   {
/* 101 */     Field f = getField(column);
/*     */ 
/* 103 */     String database = f.getDatabaseName();
/*     */ 
/* 105 */     return database == null ? "" : database;
/*     */   }
/*     */ 
/*     */   public String getColumnCharacterEncoding(int column)
/*     */     throws SQLException
/*     */   {
/* 122 */     String mysqlName = getColumnCharacterSet(column);
/*     */ 
/* 124 */     String javaName = null;
/*     */ 
/* 126 */     if (mysqlName != null) {
/* 127 */       javaName = CharsetMapping.getJavaEncodingForMysqlEncoding(mysqlName, null);
/*     */     }
/*     */ 
/* 131 */     return javaName;
/*     */   }
/*     */ 
/*     */   public String getColumnCharacterSet(int column)
/*     */     throws SQLException
/*     */   {
/* 146 */     return getField(column).getCharacterSet();
/*     */   }
/*     */ 
/*     */   public String getColumnClassName(int column)
/*     */     throws SQLException
/*     */   {
/* 172 */     Field f = getField(column);
/*     */ 
/* 206 */     switch (f.getSQLType()) {
/*     */     case -7:
/*     */     case 16:
/* 209 */       return "java.lang.Boolean";
/*     */     case -6:
/* 213 */       if (!f.isUnsigned()) {
/* 214 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 217 */       return "java.lang.Integer";
/*     */     case 5:
/* 221 */       if (!f.isUnsigned()) {
/* 222 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 225 */       return "java.lang.Integer";
/*     */     case 4:
/* 229 */       if (!f.isUnsigned()) {
/* 230 */         return "java.lang.Integer";
/*     */       }
/*     */ 
/* 233 */       return "java.lang.Long";
/*     */     case -5:
/* 237 */       if (!f.isUnsigned()) {
/* 238 */         return "java.lang.Long";
/*     */       }
/*     */ 
/* 241 */       return "java.math.BigInteger";
/*     */     case 2:
/*     */     case 3:
/* 245 */       return "java.math.BigDecimal";
/*     */     case 7:
/* 248 */       return "java.lang.Float";
/*     */     case 6:
/*     */     case 8:
/* 252 */       return "java.lang.Double";
/*     */     case -1:
/*     */     case 1:
/*     */     case 12:
/* 257 */       if (!f.isOpaqueBinary()) {
/* 258 */         return "java.lang.String";
/*     */       }
/*     */ 
/* 261 */       return "[B";
/*     */     case -4:
/*     */     case -3:
/*     */     case -2:
/* 267 */       if (f.getMysqlType() == 255)
/* 268 */         return "[B";
/* 269 */       if ((f.isBinary()) || (f.isBlob())) {
/* 270 */         return "[B";
/*     */       }
/* 272 */       return "java.lang.String";
/*     */     case 91:
/* 276 */       return "java.sql.Date";
/*     */     case 92:
/* 279 */       return "java.sql.Time";
/*     */     case 93:
/* 282 */       return "java.sql.Timestamp";
/*     */     }
/*     */ 
/* 285 */     return "java.lang.Object";
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */     throws SQLException
/*     */   {
/* 298 */     return this.fields.length;
/*     */   }
/*     */ 
/*     */   public int getColumnDisplaySize(int column)
/*     */     throws SQLException
/*     */   {
/* 313 */     Field f = getField(column);
/*     */ 
/* 315 */     int lengthInBytes = clampedGetLength(f);
/*     */ 
/* 317 */     return lengthInBytes / f.getMaxBytesPerCharacter();
/*     */   }
/*     */ 
/*     */   public String getColumnLabel(int column)
/*     */     throws SQLException
/*     */   {
/* 332 */     return getColumnName(column);
/*     */   }
/*     */ 
/*     */   public String getColumnName(int column)
/*     */     throws SQLException
/*     */   {
/* 347 */     return getField(column).getName();
/*     */   }
/*     */ 
/*     */   public int getColumnType(int column)
/*     */     throws SQLException
/*     */   {
/* 364 */     return getField(column).getSQLType();
/*     */   }
/*     */ 
/*     */   public String getColumnTypeName(int column)
/*     */     throws SQLException
/*     */   {
/* 379 */     Field field = getField(column);
/*     */ 
/* 381 */     int mysqlType = field.getMysqlType();
/*     */ 
/* 383 */     switch (mysqlType) {
/*     */     case 16:
/* 385 */       return "BIT";
/*     */     case 0:
/*     */     case 246:
/* 388 */       return field.isUnsigned() ? "DECIMAL UNSIGNED" : "DECIMAL";
/*     */     case 1:
/* 391 */       return field.isUnsigned() ? "TINYINT UNSIGNED" : "TINYINT";
/*     */     case 2:
/* 394 */       return field.isUnsigned() ? "SMALLINT UNSIGNED" : "SMALLINT";
/*     */     case 3:
/* 397 */       return field.isUnsigned() ? "INTEGER UNSIGNED" : "INTEGER";
/*     */     case 4:
/* 400 */       return field.isUnsigned() ? "FLOAT UNSIGNED" : "FLOAT";
/*     */     case 5:
/* 403 */       return field.isUnsigned() ? "DOUBLE UNSIGNED" : "DOUBLE";
/*     */     case 6:
/* 406 */       return "NULL";
/*     */     case 7:
/* 409 */       return "TIMESTAMP";
/*     */     case 8:
/* 412 */       return field.isUnsigned() ? "BIGINT UNSIGNED" : "BIGINT";
/*     */     case 9:
/* 415 */       return field.isUnsigned() ? "MEDIUMINT UNSIGNED" : "MEDIUMINT";
/*     */     case 10:
/* 418 */       return "DATE";
/*     */     case 11:
/* 421 */       return "TIME";
/*     */     case 12:
/* 424 */       return "DATETIME";
/*     */     case 249:
/* 427 */       return "TINYBLOB";
/*     */     case 250:
/* 430 */       return "MEDIUMBLOB";
/*     */     case 251:
/* 433 */       return "LONGBLOB";
/*     */     case 252:
/* 436 */       if (getField(column).isBinary()) {
/* 437 */         return "BLOB";
/*     */       }
/*     */ 
/* 440 */       return "TEXT";
/*     */     case 15:
/* 443 */       return "VARCHAR";
/*     */     case 253:
/* 446 */       return "VARCHAR";
/*     */     case 254:
/* 449 */       return "CHAR";
/*     */     case 247:
/* 452 */       return "ENUM";
/*     */     case 13:
/* 455 */       return "YEAR";
/*     */     case 248:
/* 458 */       return "SET";
/*     */     }
/*     */ 
/* 461 */     return "UNKNOWN";
/*     */   }
/*     */ 
/*     */   protected Field getField(int columnIndex)
/*     */     throws SQLException
/*     */   {
/* 477 */     if ((columnIndex < 1) || (columnIndex > this.fields.length)) {
/* 478 */       throw new SQLException(Messages.getString("ResultSetMetaData.46"), "S1002");
/*     */     }
/*     */ 
/* 482 */     return this.fields[(columnIndex - 1)];
/*     */   }
/*     */ 
/*     */   public int getPrecision(int column)
/*     */     throws SQLException
/*     */   {
/* 497 */     Field f = getField(column);
/*     */ 
/* 503 */     if (isDecimalType(f.getSQLType())) {
/* 504 */       if (f.getDecimals() > 0) {
/* 505 */         return clampedGetLength(f) - 1 + f.getPrecisionAdjustFactor();
/*     */       }
/*     */ 
/* 508 */       return clampedGetLength(f) + f.getPrecisionAdjustFactor();
/*     */     }
/*     */ 
/* 511 */     switch (f.getMysqlType()) {
/*     */     case 249:
/*     */     case 250:
/*     */     case 251:
/*     */     case 252:
/* 516 */       return clampedGetLength(f);
/*     */     }
/*     */ 
/* 523 */     return clampedGetLength(f) / f.getMaxBytesPerCharacter();
/*     */   }
/*     */ 
/*     */   public int getScale(int column)
/*     */     throws SQLException
/*     */   {
/* 540 */     Field f = getField(column);
/*     */ 
/* 542 */     if (isDecimalType(f.getSQLType())) {
/* 543 */       return f.getDecimals();
/*     */     }
/*     */ 
/* 546 */     return 0;
/*     */   }
/*     */ 
/*     */   public String getSchemaName(int column)
/*     */     throws SQLException
/*     */   {
/* 563 */     return "";
/*     */   }
/*     */ 
/*     */   public String getTableName(int column)
/*     */     throws SQLException
/*     */   {
/* 578 */     return getField(column).getTableName();
/*     */   }
/*     */ 
/*     */   public boolean isAutoIncrement(int column)
/*     */     throws SQLException
/*     */   {
/* 593 */     Field f = getField(column);
/*     */ 
/* 595 */     return f.isAutoIncrement();
/*     */   }
/*     */ 
/*     */   public boolean isCaseSensitive(int column)
/*     */     throws SQLException
/*     */   {
/* 610 */     Field field = getField(column);
/*     */ 
/* 612 */     int sqlType = field.getSQLType();
/*     */ 
/* 614 */     switch (sqlType) {
/*     */     case -7:
/*     */     case -6:
/*     */     case -5:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/* 626 */       return false;
/*     */     case -1:
/*     */     case 1:
/*     */     case 12:
/* 632 */       if (field.isBinary()) {
/* 633 */         return true;
/*     */       }
/*     */ 
/* 636 */       String collationName = field.getCollation();
/*     */ 
/* 638 */       return (collationName != null) && (!collationName.endsWith("_ci"));
/*     */     }
/*     */ 
/* 641 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isCurrency(int column)
/*     */     throws SQLException
/*     */   {
/* 657 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDefinitelyWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 672 */     return isWritable(column);
/*     */   }
/*     */ 
/*     */   public int isNullable(int column)
/*     */     throws SQLException
/*     */   {
/* 687 */     if (!getField(column).isNotNull()) {
/* 688 */       return 1;
/*     */     }
/*     */ 
/* 691 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly(int column)
/*     */     throws SQLException
/*     */   {
/* 706 */     return getField(column).isReadOnly();
/*     */   }
/*     */ 
/*     */   public boolean isSearchable(int column)
/*     */     throws SQLException
/*     */   {
/* 725 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isSigned(int column)
/*     */     throws SQLException
/*     */   {
/* 740 */     Field f = getField(column);
/* 741 */     int sqlType = f.getSQLType();
/*     */ 
/* 743 */     switch (sqlType) {
/*     */     case -6:
/*     */     case -5:
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 753 */       return !f.isUnsigned();
/*     */     case 91:
/*     */     case 92:
/*     */     case 93:
/* 758 */       return false;
/*     */     }
/*     */ 
/* 761 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isWritable(int column)
/*     */     throws SQLException
/*     */   {
/* 783 */     return !isReadOnly(column);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 792 */     StringBuffer toStringBuf = new StringBuffer();
/* 793 */     toStringBuf.append(super.toString());
/* 794 */     toStringBuf.append(" - Field level information: ");
/*     */ 
/* 796 */     for (int i = 0; i < this.fields.length; i++) {
/* 797 */       toStringBuf.append("\n\t");
/* 798 */       toStringBuf.append(this.fields[i].toString());
/*     */     }
/*     */ 
/* 801 */     return toStringBuf.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     com.mysql.jdbc.ResultSetMetaData
 * JD-Core Version:    0.6.0
 */
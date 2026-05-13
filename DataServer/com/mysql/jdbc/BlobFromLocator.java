/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.sql.Blob;
/*     */ import java.sql.DatabaseMetaData;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class BlobFromLocator
/*     */   implements Blob
/*     */ {
/*  55 */   private String blobColumnName = null;
/*     */   private ResultSet creatorResultSet;
/*  60 */   private int numColsInResultSet = 0;
/*     */ 
/*  62 */   private int numPrimaryKeys = 0;
/*     */ 
/*  64 */   private List primaryKeyColumns = null;
/*     */ 
/*  66 */   private List primaryKeyValues = null;
/*     */   private String quotedId;
/*  70 */   private String tableName = null;
/*     */ 
/*     */   BlobFromLocator(ResultSet creatorResultSetToSet, int blobColumnIndex)
/*     */     throws SQLException
/*     */   {
/*  77 */     this.creatorResultSet = creatorResultSetToSet;
/*     */ 
/*  79 */     this.numColsInResultSet = this.creatorResultSet.fields.length;
/*  80 */     this.quotedId = this.creatorResultSet.connection.getMetaData().getIdentifierQuoteString();
/*     */ 
/*  83 */     if (this.numColsInResultSet > 1) {
/*  84 */       this.primaryKeyColumns = new ArrayList();
/*  85 */       this.primaryKeyValues = new ArrayList();
/*     */ 
/*  87 */       for (int i = 0; i < this.numColsInResultSet; i++)
/*  88 */         if (this.creatorResultSet.fields[i].isPrimaryKey()) {
/*  89 */           StringBuffer keyName = new StringBuffer();
/*  90 */           keyName.append(this.quotedId);
/*     */ 
/*  92 */           String originalColumnName = this.creatorResultSet.fields[i].getOriginalName();
/*     */ 
/*  95 */           if ((this.creatorResultSet.connection.getIO().hasLongColumnInfo()) && (originalColumnName != null) && (originalColumnName.length() > 0))
/*     */           {
/*  99 */             keyName.append(originalColumnName);
/*     */           }
/* 101 */           else keyName.append(this.creatorResultSet.fields[i].getName());
/*     */ 
/* 105 */           keyName.append(this.quotedId);
/*     */ 
/* 107 */           this.primaryKeyColumns.add(keyName.toString());
/* 108 */           this.primaryKeyValues.add(this.creatorResultSet.getString(i + 1));
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 113 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 116 */     this.numPrimaryKeys = this.primaryKeyColumns.size();
/*     */ 
/* 118 */     if (this.numPrimaryKeys == 0) {
/* 119 */       notEnoughInformationInQuery();
/*     */     }
/*     */ 
/* 122 */     if (this.creatorResultSet.fields[0].getOriginalTableName() != null) {
/* 123 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 125 */       String databaseName = this.creatorResultSet.fields[0].getDatabaseName();
/*     */ 
/* 128 */       if ((databaseName != null) && (databaseName.length() > 0)) {
/* 129 */         tableNameBuffer.append(this.quotedId);
/* 130 */         tableNameBuffer.append(databaseName);
/* 131 */         tableNameBuffer.append(this.quotedId);
/* 132 */         tableNameBuffer.append('.');
/*     */       }
/*     */ 
/* 135 */       tableNameBuffer.append(this.quotedId);
/* 136 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getOriginalTableName());
/*     */ 
/* 138 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 140 */       this.tableName = tableNameBuffer.toString();
/*     */     } else {
/* 142 */       StringBuffer tableNameBuffer = new StringBuffer();
/*     */ 
/* 144 */       tableNameBuffer.append(this.quotedId);
/* 145 */       tableNameBuffer.append(this.creatorResultSet.fields[0].getTableName());
/*     */ 
/* 147 */       tableNameBuffer.append(this.quotedId);
/*     */ 
/* 149 */       this.tableName = tableNameBuffer.toString();
/*     */     }
/*     */ 
/* 152 */     this.blobColumnName = this.creatorResultSet.getString(blobColumnIndex);
/*     */   }
/*     */ 
/*     */   public InputStream getBinaryStream()
/*     */     throws SQLException
/*     */   {
/* 164 */     return new ByteArrayInputStream(getBytes(1L, (int)length()));
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(long pos, int length)
/*     */     throws SQLException
/*     */   {
/* 183 */     java.sql.ResultSet blobRs = null;
/* 184 */     PreparedStatement pStmt = null;
/*     */ 
/* 187 */     StringBuffer query = new StringBuffer("SELECT SUBSTRING(");
/* 188 */     query.append(this.blobColumnName);
/* 189 */     query.append(", ");
/* 190 */     query.append(pos);
/* 191 */     query.append(", ");
/* 192 */     query.append(length);
/* 193 */     query.append(") FROM ");
/* 194 */     query.append(this.tableName);
/* 195 */     query.append(" WHERE ");
/*     */ 
/* 197 */     query.append((String)this.primaryKeyColumns.get(0));
/* 198 */     query.append(" = ?");
/*     */ 
/* 200 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 201 */       query.append(" AND ");
/* 202 */       query.append((String)this.primaryKeyColumns.get(i));
/* 203 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 208 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 211 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 212 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 215 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 217 */       if (blobRs.next()) {
/* 218 */         i = ((ResultSet)blobRs).getBytes(1, true); jsr 26;
/*     */       }
/*     */ 
/* 221 */       throw new SQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally
/*     */     {
/* 225 */       if (blobRs != null) {
/*     */         try {
/* 227 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 232 */         blobRs = null;
/*     */       }
/*     */ 
/* 235 */       if (pStmt != null) {
/*     */         try {
/* 237 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 242 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long length()
/*     */     throws SQLException
/*     */   {
/* 257 */     java.sql.ResultSet blobRs = null;
/* 258 */     PreparedStatement pStmt = null;
/*     */ 
/* 261 */     StringBuffer query = new StringBuffer("SELECT LENGTH(");
/* 262 */     query.append(this.blobColumnName);
/* 263 */     query.append(") FROM ");
/* 264 */     query.append(this.tableName);
/* 265 */     query.append(" WHERE ");
/*     */ 
/* 267 */     query.append((String)this.primaryKeyColumns.get(0));
/* 268 */     query.append(" = ?");
/*     */ 
/* 270 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 271 */       query.append(" AND ");
/* 272 */       query.append((String)this.primaryKeyColumns.get(i));
/* 273 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 278 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 281 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 282 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 285 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 287 */       if (blobRs.next()) {
/* 288 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 291 */       throw new SQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally
/*     */     {
/* 295 */       if (blobRs != null) {
/*     */         try {
/* 297 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 302 */         blobRs = null;
/*     */       }
/*     */ 
/* 305 */       if (pStmt != null) {
/*     */         try {
/* 307 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 312 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notEnoughInformationInQuery() throws SQLException {
/* 318 */     throw new SQLException("Emulated BLOB locators must come from a ResultSet with only one table selected, and all primary keys selected", "S1000");
/*     */   }
/*     */ 
/*     */   public long position(byte[] pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 327 */     java.sql.ResultSet blobRs = null;
/* 328 */     PreparedStatement pStmt = null;
/*     */ 
/* 331 */     StringBuffer query = new StringBuffer("SELECT LOCATE(");
/* 332 */     query.append("?, ");
/* 333 */     query.append(this.blobColumnName);
/* 334 */     query.append(", ");
/* 335 */     query.append(start);
/* 336 */     query.append(") FROM ");
/* 337 */     query.append(this.tableName);
/* 338 */     query.append(" WHERE ");
/*     */ 
/* 340 */     query.append((String)this.primaryKeyColumns.get(0));
/* 341 */     query.append(" = ?");
/*     */ 
/* 343 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 344 */       query.append(" AND ");
/* 345 */       query.append((String)this.primaryKeyColumns.get(i));
/* 346 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 351 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 353 */       pStmt.setBytes(1, pattern);
/*     */ 
/* 355 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 356 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 359 */       blobRs = pStmt.executeQuery();
/*     */ 
/* 361 */       if (blobRs.next()) {
/* 362 */         i = blobRs.getLong(1); jsr 26;
/*     */       }
/*     */ 
/* 365 */       throw new SQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */     }
/*     */     finally
/*     */     {
/* 369 */       if (blobRs != null) {
/*     */         try {
/* 371 */           blobRs.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 376 */         blobRs = null;
/*     */       }
/*     */ 
/* 379 */       if (pStmt != null) {
/*     */         try {
/* 381 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 386 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long position(Blob pattern, long start)
/*     */     throws SQLException
/*     */   {
/* 406 */     return position(pattern.getBytes(0L, (int)pattern.length()), start);
/*     */   }
/*     */ 
/*     */   public OutputStream setBinaryStream(long indexToWriteAt)
/*     */     throws SQLException
/*     */   {
/* 414 */     throw new NotImplemented();
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes)
/*     */     throws SQLException
/*     */   {
/* 421 */     return setBytes(writeAt, bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public int setBytes(long writeAt, byte[] bytes, int offset, int length)
/*     */     throws SQLException
/*     */   {
/* 429 */     PreparedStatement pStmt = null;
/*     */ 
/* 431 */     if (offset + length > bytes.length) {
/* 432 */       length = bytes.length - offset;
/*     */     }
/*     */ 
/* 435 */     byte[] bytesToWrite = new byte[length];
/* 436 */     System.arraycopy(bytes, offset, bytesToWrite, 0, length);
/*     */ 
/* 439 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 440 */     query.append(this.tableName);
/* 441 */     query.append(" SET ");
/* 442 */     query.append(this.blobColumnName);
/* 443 */     query.append(" = INSERT(");
/* 444 */     query.append(this.blobColumnName);
/* 445 */     query.append(", ");
/* 446 */     query.append(writeAt);
/* 447 */     query.append(", ");
/* 448 */     query.append(length);
/* 449 */     query.append(", ?) WHERE ");
/*     */ 
/* 451 */     query.append((String)this.primaryKeyColumns.get(0));
/* 452 */     query.append(" = ?");
/*     */ 
/* 454 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 455 */       query.append(" AND ");
/* 456 */       query.append((String)this.primaryKeyColumns.get(i));
/* 457 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 462 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 465 */       pStmt.setBytes(1, bytesToWrite);
/*     */ 
/* 467 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 468 */         pStmt.setString(i + 2, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 471 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 473 */       if (rowsUpdated != 1) {
/* 474 */         throw new SQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 479 */       if (pStmt != null) {
/*     */         try {
/* 481 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 486 */         pStmt = null;
/*     */       }
/*     */     }
/*     */ 
/* 490 */     return (int)length();
/*     */   }
/*     */ 
/*     */   public void truncate(long length)
/*     */     throws SQLException
/*     */   {
/* 497 */     PreparedStatement pStmt = null;
/*     */ 
/* 500 */     StringBuffer query = new StringBuffer("UPDATE ");
/* 501 */     query.append(this.tableName);
/* 502 */     query.append(" SET ");
/* 503 */     query.append(this.blobColumnName);
/* 504 */     query.append(" = LEFT(");
/* 505 */     query.append(this.blobColumnName);
/* 506 */     query.append(", ");
/* 507 */     query.append(length);
/* 508 */     query.append(") WHERE ");
/*     */ 
/* 510 */     query.append((String)this.primaryKeyColumns.get(0));
/* 511 */     query.append(" = ?");
/*     */ 
/* 513 */     for (int i = 1; i < this.numPrimaryKeys; i++) {
/* 514 */       query.append(" AND ");
/* 515 */       query.append((String)this.primaryKeyColumns.get(i));
/* 516 */       query.append(" = ?");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 521 */       pStmt = this.creatorResultSet.connection.prepareStatement(query.toString());
/*     */ 
/* 524 */       for (int i = 0; i < this.numPrimaryKeys; i++) {
/* 525 */         pStmt.setString(i + 1, (String)this.primaryKeyValues.get(i));
/*     */       }
/*     */ 
/* 528 */       int rowsUpdated = pStmt.executeUpdate();
/*     */ 
/* 530 */       if (rowsUpdated != 1) {
/* 531 */         throw new SQLException("BLOB data not found! Did primary keys change?", "S1000");
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 536 */       if (pStmt != null) {
/*     */         try {
/* 538 */           pStmt.close();
/*     */         }
/*     */         catch (SQLException sqlEx)
/*     */         {
/*     */         }
/* 543 */         pStmt = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.BlobFromLocator
 * JD-Core Version:    0.6.0
 */
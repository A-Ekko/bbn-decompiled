/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import com.mysql.jdbc.profiler.ProfileEventSink;
/*     */ import com.mysql.jdbc.profiler.ProfilerEvent;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class RowDataDynamic
/*     */   implements RowData
/*     */ {
/*     */   private int columnCount;
/*     */   private Field[] fields;
/*  53 */   private int index = -1;
/*     */   private MysqlIO io;
/*  57 */   private boolean isAfterEnd = false;
/*     */ 
/*  59 */   private boolean isAtEnd = false;
/*     */ 
/*  61 */   private boolean isBinaryEncoded = false;
/*     */   private Object[] nextRow;
/*     */   private ResultSet owner;
/*  67 */   private boolean streamerClosed = false;
/*     */ 
/*     */   public RowDataDynamic(MysqlIO io, int colCount, Field[] fields, boolean isBinaryEncoded)
/*     */     throws SQLException
/*     */   {
/*  88 */     this.io = io;
/*  89 */     this.columnCount = colCount;
/*  90 */     this.isBinaryEncoded = isBinaryEncoded;
/*  91 */     this.fields = fields;
/*  92 */     nextRecord();
/*     */   }
/*     */ 
/*     */   public void addRow(byte[][] row)
/*     */     throws SQLException
/*     */   {
/* 104 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void afterLast()
/*     */     throws SQLException
/*     */   {
/* 114 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeFirst()
/*     */     throws SQLException
/*     */   {
/* 124 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void beforeLast()
/*     */     throws SQLException
/*     */   {
/* 134 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws SQLException
/*     */   {
/* 145 */     boolean hadMore = false;
/* 146 */     int howMuchMore = 0;
/*     */ 
/* 149 */     while (hasNext()) {
/* 150 */       next();
/* 151 */       hadMore = true;
/* 152 */       howMuchMore++;
/*     */ 
/* 154 */       if (howMuchMore % 100 == 0) {
/* 155 */         Thread.yield();
/*     */       }
/*     */     }
/*     */ 
/* 159 */     if (this.owner != null) {
/* 160 */       Connection conn = this.owner.connection;
/*     */ 
/* 162 */       if ((conn != null) && (conn.getUseUsageAdvisor()) && 
/* 163 */         (hadMore))
/*     */       {
/* 165 */         ProfileEventSink eventSink = ProfileEventSink.getInstance(conn);
/*     */ 
/* 168 */         eventSink.consumeEvent(new ProfilerEvent(0, "", this.owner.owningStatement == null ? "N/A" : this.owner.owningStatement.currentCatalog, conn.getId(), this.owner.owningStatement == null ? -1 : this.owner.owningStatement.getId(), -1, System.currentTimeMillis(), 0, null, null, Messages.getString("RowDataDynamic.2") + howMuchMore + Messages.getString("RowDataDynamic.3") + Messages.getString("RowDataDynamic.4") + Messages.getString("RowDataDynamic.5") + Messages.getString("RowDataDynamic.6") + this.owner.pointOfOrigin));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 197 */     this.fields = null;
/* 198 */     this.owner = null;
/*     */   }
/*     */ 
/*     */   public Object[] getAt(int ind)
/*     */     throws SQLException
/*     */   {
/* 211 */     notSupported();
/*     */ 
/* 213 */     return null;
/*     */   }
/*     */ 
/*     */   public int getCurrentRowNumber()
/*     */     throws SQLException
/*     */   {
/* 224 */     notSupported();
/*     */ 
/* 226 */     return -1;
/*     */   }
/*     */ 
/*     */   public ResultSet getOwner()
/*     */   {
/* 233 */     return this.owner;
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */     throws SQLException
/*     */   {
/* 244 */     boolean hasNext = this.nextRow != null;
/*     */ 
/* 246 */     if ((!hasNext) && (!this.streamerClosed)) {
/* 247 */       this.io.closeStreamer(this);
/* 248 */       this.streamerClosed = true;
/*     */     }
/*     */ 
/* 251 */     return hasNext;
/*     */   }
/*     */ 
/*     */   public boolean isAfterLast()
/*     */     throws SQLException
/*     */   {
/* 262 */     return this.isAfterEnd;
/*     */   }
/*     */ 
/*     */   public boolean isBeforeFirst()
/*     */     throws SQLException
/*     */   {
/* 273 */     return this.index < 0;
/*     */   }
/*     */ 
/*     */   public boolean isDynamic()
/*     */   {
/* 285 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */     throws SQLException
/*     */   {
/* 296 */     notSupported();
/*     */ 
/* 298 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFirst()
/*     */     throws SQLException
/*     */   {
/* 309 */     notSupported();
/*     */ 
/* 311 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLast()
/*     */     throws SQLException
/*     */   {
/* 322 */     notSupported();
/*     */ 
/* 324 */     return false;
/*     */   }
/*     */ 
/*     */   public void moveRowRelative(int rows)
/*     */     throws SQLException
/*     */   {
/* 336 */     notSupported();
/*     */   }
/*     */ 
/*     */   public Object[] next()
/*     */     throws SQLException
/*     */   {
/* 347 */     if (this.index != 2147483647) {
/* 348 */       this.index += 1;
/*     */     }
/*     */ 
/* 351 */     Object[] ret = this.nextRow;
/* 352 */     nextRecord();
/*     */ 
/* 354 */     return ret;
/*     */   }
/*     */ 
/*     */   private void nextRecord() throws SQLException
/*     */   {
/*     */     try {
/* 360 */       if (!this.isAtEnd)
/*     */       {
/* 362 */         this.nextRow = this.io.nextRow(this.fields, this.columnCount, this.isBinaryEncoded, 1007);
/*     */ 
/* 366 */         if (this.nextRow == null)
/* 367 */           this.isAtEnd = true;
/*     */       }
/*     */       else {
/* 370 */         this.isAfterEnd = true;
/*     */       }
/*     */     }
/*     */     catch (SQLException sqlEx) {
/* 374 */       throw sqlEx;
/*     */     } catch (Exception ex) {
/* 376 */       String exceptionType = ex.getClass().getName();
/* 377 */       String exceptionMessage = ex.getMessage();
/*     */ 
/* 379 */       exceptionMessage = exceptionMessage + Messages.getString("RowDataDynamic.7");
/* 380 */       exceptionMessage = exceptionMessage + Util.stackTraceToString(ex);
/*     */ 
/* 382 */       throw new SQLException(Messages.getString("RowDataDynamic.8") + exceptionType + Messages.getString("RowDataDynamic.9") + exceptionMessage, "S1000");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void notSupported()
/*     */     throws SQLException
/*     */   {
/* 390 */     throw new OperationNotSupportedException();
/*     */   }
/*     */ 
/*     */   public void removeRow(int ind)
/*     */     throws SQLException
/*     */   {
/* 402 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setCurrentRow(int rowNumber)
/*     */     throws SQLException
/*     */   {
/* 417 */     notSupported();
/*     */   }
/*     */ 
/*     */   public void setOwner(ResultSet rs)
/*     */   {
/* 424 */     this.owner = rs;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 433 */     return -1;
/*     */   }
/*     */ 
/*     */   class OperationNotSupportedException extends SQLException
/*     */   {
/*     */     OperationNotSupportedException()
/*     */     {
/*  44 */       super("S1009");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.RowDataDynamic
 * JD-Core Version:    0.6.0
 */
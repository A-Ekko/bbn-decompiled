/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.LogRecordFilter;
/*     */ import org.apache.log4j.lf5.PassingLogRecordFilter;
/*     */ 
/*     */ public class FilteredLogTableModel extends AbstractTableModel
/*     */ {
/*  40 */   protected LogRecordFilter _filter = new PassingLogRecordFilter();
/*  41 */   protected List _allRecords = new ArrayList();
/*     */   protected List _filteredRecords;
/*  43 */   protected int _maxNumberOfLogRecords = 5000;
/*  44 */   protected String[] _colNames = { "Date", "Thread", "Message #", "Level", "NDC", "Category", "Message", "Location", "Thrown" };
/*     */ 
/*     */   public void setLogRecordFilter(LogRecordFilter filter)
/*     */   {
/*  71 */     this._filter = filter;
/*     */   }
/*     */ 
/*     */   public LogRecordFilter getLogRecordFilter() {
/*  75 */     return this._filter;
/*     */   }
/*     */ 
/*     */   public String getColumnName(int i) {
/*  79 */     return this._colNames[i];
/*     */   }
/*     */ 
/*     */   public int getColumnCount() {
/*  83 */     return this._colNames.length;
/*     */   }
/*     */ 
/*     */   public int getRowCount() {
/*  87 */     return getFilteredRecords().size();
/*     */   }
/*     */ 
/*     */   public int getTotalRowCount() {
/*  91 */     return this._allRecords.size();
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int row, int col) {
/*  95 */     LogRecord record = getFilteredRecord(row);
/*  96 */     return getColumn(col, record);
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfLogRecords(int maxNumRecords) {
/* 100 */     if (maxNumRecords > 0)
/* 101 */       this._maxNumberOfLogRecords = maxNumRecords;
/*     */   }
/*     */ 
/*     */   public synchronized boolean addLogRecord(LogRecord record)
/*     */   {
/* 108 */     this._allRecords.add(record);
/*     */ 
/* 110 */     if (!this._filter.passes(record)) {
/* 111 */       return false;
/*     */     }
/* 113 */     getFilteredRecords().add(record);
/* 114 */     fireTableRowsInserted(getRowCount(), getRowCount());
/* 115 */     trimRecords();
/* 116 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void refresh()
/*     */   {
/* 124 */     this._filteredRecords = createFilteredRecordsList();
/* 125 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public synchronized void fastRefresh() {
/* 129 */     this._filteredRecords.remove(0);
/* 130 */     fireTableRowsDeleted(0, 0);
/*     */   }
/*     */ 
/*     */   public synchronized void clear()
/*     */   {
/* 138 */     this._allRecords.clear();
/* 139 */     this._filteredRecords.clear();
/* 140 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   protected List getFilteredRecords()
/*     */   {
/* 148 */     if (this._filteredRecords == null) {
/* 149 */       refresh();
/*     */     }
/* 151 */     return this._filteredRecords;
/*     */   }
/*     */ 
/*     */   protected List createFilteredRecordsList() {
/* 155 */     List result = new ArrayList();
/* 156 */     Iterator records = this._allRecords.iterator();
/*     */ 
/* 158 */     while (records.hasNext()) {
/* 159 */       LogRecord current = (LogRecord)records.next();
/* 160 */       if (this._filter.passes(current)) {
/* 161 */         result.add(current);
/*     */       }
/*     */     }
/* 164 */     return result;
/*     */   }
/*     */ 
/*     */   protected LogRecord getFilteredRecord(int row) {
/* 168 */     List records = getFilteredRecords();
/* 169 */     int size = records.size();
/* 170 */     if (row < size) {
/* 171 */       return (LogRecord)records.get(row);
/*     */     }
/*     */ 
/* 177 */     return (LogRecord)records.get(size - 1);
/*     */   }
/*     */ 
/*     */   protected Object getColumn(int col, LogRecord lr)
/*     */   {
/* 182 */     if (lr == null) {
/* 183 */       return "NULL Column";
/*     */     }
/* 185 */     String date = new Date(lr.getMillis()).toString();
/* 186 */     switch (col) {
/*     */     case 0:
/* 188 */       return date + " (" + lr.getMillis() + ")";
/*     */     case 1:
/* 190 */       return lr.getThreadDescription();
/*     */     case 2:
/* 192 */       return new Long(lr.getSequenceNumber());
/*     */     case 3:
/* 194 */       return lr.getLevel();
/*     */     case 4:
/* 196 */       return lr.getNDC();
/*     */     case 5:
/* 198 */       return lr.getCategory();
/*     */     case 6:
/* 200 */       return lr.getMessage();
/*     */     case 7:
/* 202 */       return lr.getLocation();
/*     */     case 8:
/* 204 */       return lr.getThrownStackTrace();
/*     */     }
/* 206 */     String message = "The column number " + col + "must be between 0 and 8";
/* 207 */     throw new IllegalArgumentException(message);
/*     */   }
/*     */ 
/*     */   protected void trimRecords()
/*     */   {
/* 218 */     if (needsTrimming())
/* 219 */       trimOldestRecords();
/*     */   }
/*     */ 
/*     */   protected boolean needsTrimming()
/*     */   {
/* 224 */     return this._allRecords.size() > this._maxNumberOfLogRecords;
/*     */   }
/*     */ 
/*     */   protected void trimOldestRecords() {
/* 228 */     synchronized (this._allRecords) {
/* 229 */       int trim = numberOfRecordsToTrim();
/* 230 */       if (trim > 1) {
/* 231 */         List oldRecords = this._allRecords.subList(0, trim);
/*     */ 
/* 233 */         oldRecords.clear();
/* 234 */         refresh();
/*     */       } else {
/* 236 */         this._allRecords.remove(0);
/* 237 */         fastRefresh();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int numberOfRecordsToTrim()
/*     */   {
/* 247 */     return this._allRecords.size() - this._maxNumberOfLogRecords;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.FilteredLogTableModel
 * JD-Core Version:    0.6.0
 */
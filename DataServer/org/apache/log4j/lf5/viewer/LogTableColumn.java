/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LogTableColumn
/*     */   implements Serializable
/*     */ {
/*  27 */   public static final LogTableColumn DATE = new LogTableColumn("Date");
/*  28 */   public static final LogTableColumn THREAD = new LogTableColumn("Thread");
/*  29 */   public static final LogTableColumn MESSAGE_NUM = new LogTableColumn("Message #");
/*  30 */   public static final LogTableColumn LEVEL = new LogTableColumn("Level");
/*  31 */   public static final LogTableColumn NDC = new LogTableColumn("NDC");
/*  32 */   public static final LogTableColumn CATEGORY = new LogTableColumn("Category");
/*  33 */   public static final LogTableColumn MESSAGE = new LogTableColumn("Message");
/*  34 */   public static final LogTableColumn LOCATION = new LogTableColumn("Location");
/*  35 */   public static final LogTableColumn THROWN = new LogTableColumn("Thrown");
/*     */   protected String _label;
/*  53 */   private static LogTableColumn[] _log4JColumns = { DATE, THREAD, MESSAGE_NUM, LEVEL, NDC, CATEGORY, MESSAGE, LOCATION, THROWN };
/*     */ 
/*  56 */   private static Map _logTableColumnMap = new HashMap();
/*     */ 
/*     */   public LogTableColumn(String label)
/*     */   {
/*  65 */     this._label = label;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/*  76 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static LogTableColumn valueOf(String column)
/*     */     throws LogTableColumnFormatException
/*     */   {
/*  89 */     LogTableColumn tableColumn = null;
/*  90 */     if (column != null) {
/*  91 */       column = column.trim();
/*  92 */       tableColumn = (LogTableColumn)_logTableColumnMap.get(column);
/*     */     }
/*     */ 
/*  95 */     if (tableColumn == null) {
/*  96 */       StringBuffer buf = new StringBuffer();
/*  97 */       buf.append("Error while trying to parse (" + column + ") into");
/*  98 */       buf.append(" a LogTableColumn.");
/*  99 */       throw new LogTableColumnFormatException(buf.toString());
/*     */     }
/* 101 */     return tableColumn;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 106 */     boolean equals = false;
/*     */ 
/* 108 */     if (((o instanceof LogTableColumn)) && 
/* 109 */       (getLabel() == ((LogTableColumn)o).getLabel()))
/*     */     {
/* 111 */       equals = true;
/*     */     }
/*     */ 
/* 115 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 119 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 123 */     return this._label;
/*     */   }
/*     */ 
/*     */   public static List getLogTableColumns()
/*     */   {
/* 131 */     return Arrays.asList(_log4JColumns);
/*     */   }
/*     */ 
/*     */   public static LogTableColumn[] getLogTableColumnArray() {
/* 135 */     return _log4JColumns;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  58 */     for (int i = 0; i < _log4JColumns.length; i++)
/*  59 */       _logTableColumnMap.put(_log4JColumns[i].getLabel(), _log4JColumns[i]);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTableColumn
 * JD-Core Version:    0.6.0
 */
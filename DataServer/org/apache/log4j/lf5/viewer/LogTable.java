/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.util.Enumeration;
/*     */ import java.util.EventObject;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import org.apache.log4j.lf5.util.DateFormatManager;
/*     */ 
/*     */ public class LogTable extends JTable
/*     */ {
/*  42 */   protected int _rowHeight = 30;
/*     */   protected JTextArea _detailTextArea;
/*  46 */   protected int _numCols = 9;
/*  47 */   protected TableColumn[] _tableColumns = new TableColumn[this._numCols];
/*  48 */   protected int[] _colWidths = { 40, 40, 40, 70, 70, 360, 440, 200, 60 };
/*  49 */   protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
/*  50 */   protected int _colDate = 0;
/*  51 */   protected int _colThread = 1;
/*  52 */   protected int _colMessageNum = 2;
/*  53 */   protected int _colLevel = 3;
/*  54 */   protected int _colNDC = 4;
/*  55 */   protected int _colCategory = 5;
/*  56 */   protected int _colMessage = 6;
/*  57 */   protected int _colLocation = 7;
/*  58 */   protected int _colThrown = 8;
/*     */ 
/*  60 */   protected DateFormatManager _dateFormatManager = null;
/*     */ 
/*     */   public LogTable(JTextArea detailTextArea)
/*     */   {
/*  73 */     init();
/*     */ 
/*  75 */     this._detailTextArea = detailTextArea;
/*     */ 
/*  77 */     setModel(new FilteredLogTableModel());
/*     */ 
/*  79 */     Enumeration columns = getColumnModel().getColumns();
/*  80 */     int i = 0;
/*  81 */     while (columns.hasMoreElements()) {
/*  82 */       TableColumn col = (TableColumn)columns.nextElement();
/*  83 */       col.setCellRenderer(new LogTableRowRenderer());
/*  84 */       col.setPreferredWidth(this._colWidths[i]);
/*     */ 
/*  86 */       this._tableColumns[i] = col;
/*  87 */       i++;
/*     */     }
/*     */ 
/*  90 */     ListSelectionModel rowSM = getSelectionModel();
/*  91 */     rowSM.addListSelectionListener(new LogTableListSelectionListener(this));
/*     */   }
/*     */ 
/*     */   public DateFormatManager getDateFormatManager()
/*     */   {
/* 104 */     return this._dateFormatManager;
/*     */   }
/*     */ 
/*     */   public void setDateFormatManager(DateFormatManager dfm)
/*     */   {
/* 111 */     this._dateFormatManager = dfm;
/*     */   }
/*     */ 
/*     */   public synchronized void clearLogRecords()
/*     */   {
/* 119 */     getFilteredLogTableModel().clear();
/*     */   }
/*     */ 
/*     */   public FilteredLogTableModel getFilteredLogTableModel() {
/* 123 */     return (FilteredLogTableModel)getModel();
/*     */   }
/*     */ 
/*     */   public void setDetailedView()
/*     */   {
/* 129 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 131 */     for (int f = 0; f < this._numCols; f++) {
/* 132 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/*     */ 
/* 135 */     for (int i = 0; i < this._numCols; i++) {
/* 136 */       model.addColumn(this._tableColumns[i]);
/*     */     }
/*     */ 
/* 139 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setView(List columns) {
/* 143 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 146 */     for (int f = 0; f < this._numCols; f++) {
/* 147 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/* 149 */     Iterator selectedColumns = columns.iterator();
/* 150 */     Vector columnNameAndNumber = getColumnNameAndNumber();
/* 151 */     while (selectedColumns.hasNext())
/*     */     {
/* 153 */       model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
/*     */     }
/*     */ 
/* 157 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 161 */     super.setFont(font);
/* 162 */     Graphics g = getGraphics();
/* 163 */     if (g != null) {
/* 164 */       FontMetrics fm = g.getFontMetrics(font);
/* 165 */       int height = fm.getHeight();
/* 166 */       this._rowHeight = (height + height / 3);
/* 167 */       setRowHeight(this._rowHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 179 */     setRowHeight(this._rowHeight);
/* 180 */     setSelectionMode(0);
/*     */   }
/*     */ 
/*     */   protected Vector getColumnNameAndNumber()
/*     */   {
/* 185 */     Vector columnNameAndNumber = new Vector();
/* 186 */     for (int i = 0; i < this._colNames.length; i++) {
/* 187 */       columnNameAndNumber.add(i, this._colNames[i]);
/*     */     }
/* 189 */     return columnNameAndNumber;
/*     */   }
/*     */ 
/*     */   class LogTableListSelectionListener
/*     */     implements ListSelectionListener
/*     */   {
/*     */     protected JTable _table;
/*     */ 
/*     */     public LogTableListSelectionListener(JTable table)
/*     */     {
/* 204 */       this._table = table;
/*     */     }
/*     */ 
/*     */     public void valueChanged(ListSelectionEvent e)
/*     */     {
/* 209 */       if (e.getValueIsAdjusting()) {
/* 210 */         return;
/*     */       }
/*     */ 
/* 213 */       ListSelectionModel lsm = (ListSelectionModel)e.getSource();
/* 214 */       if (!lsm.isSelectionEmpty())
/*     */       {
/* 217 */         StringBuffer buf = new StringBuffer();
/* 218 */         int selectedRow = lsm.getMinSelectionIndex();
/*     */ 
/* 220 */         for (int i = 0; i < LogTable.this._numCols - 1; i++) {
/* 221 */           String value = "";
/* 222 */           Object obj = this._table.getModel().getValueAt(selectedRow, i);
/* 223 */           if (obj != null) {
/* 224 */             value = obj.toString();
/*     */           }
/*     */ 
/* 227 */           buf.append(LogTable.this._colNames[i] + ":");
/* 228 */           buf.append("\t");
/*     */ 
/* 230 */           if ((i == LogTable.this._colThread) || (i == LogTable.this._colMessage) || (i == LogTable.this._colLevel)) {
/* 231 */             buf.append("\t");
/*     */           }
/*     */ 
/* 234 */           if ((i == LogTable.this._colDate) || (i == LogTable.this._colNDC)) {
/* 235 */             buf.append("\t\t");
/*     */           }
/*     */ 
/* 243 */           buf.append(value);
/* 244 */           buf.append("\n");
/*     */         }
/* 246 */         buf.append(LogTable.this._colNames[(LogTable.this._numCols - 1)] + ":\n");
/* 247 */         Object obj = this._table.getModel().getValueAt(selectedRow, LogTable.this._numCols - 1);
/* 248 */         if (obj != null) {
/* 249 */           buf.append(obj.toString());
/*     */         }
/*     */ 
/* 252 */         LogTable.this._detailTextArea.setText(buf.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTable
 * JD-Core Version:    0.6.0
 */
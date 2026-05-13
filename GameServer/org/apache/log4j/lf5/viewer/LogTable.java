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
/*     */   private static final long serialVersionUID = 4867085140195148458L;
/*  52 */   protected int _rowHeight = 30;
/*     */   protected JTextArea _detailTextArea;
/*  56 */   protected int _numCols = 9;
/*  57 */   protected TableColumn[] _tableColumns = new TableColumn[this._numCols];
/*  58 */   protected int[] _colWidths = { 40, 40, 40, 70, 70, 360, 440, 200, 60 };
/*  59 */   protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
/*  60 */   protected int _colDate = 0;
/*  61 */   protected int _colThread = 1;
/*  62 */   protected int _colMessageNum = 2;
/*  63 */   protected int _colLevel = 3;
/*  64 */   protected int _colNDC = 4;
/*  65 */   protected int _colCategory = 5;
/*  66 */   protected int _colMessage = 6;
/*  67 */   protected int _colLocation = 7;
/*  68 */   protected int _colThrown = 8;
/*     */ 
/*  70 */   protected DateFormatManager _dateFormatManager = null;
/*     */ 
/*     */   public LogTable(JTextArea detailTextArea)
/*     */   {
/*  83 */     init();
/*     */ 
/*  85 */     this._detailTextArea = detailTextArea;
/*     */ 
/*  87 */     setModel(new FilteredLogTableModel());
/*     */ 
/*  89 */     Enumeration columns = getColumnModel().getColumns();
/*  90 */     int i = 0;
/*  91 */     while (columns.hasMoreElements()) {
/*  92 */       TableColumn col = (TableColumn)columns.nextElement();
/*  93 */       col.setCellRenderer(new LogTableRowRenderer());
/*  94 */       col.setPreferredWidth(this._colWidths[i]);
/*     */ 
/*  96 */       this._tableColumns[i] = col;
/*  97 */       i++;
/*     */     }
/*     */ 
/* 100 */     ListSelectionModel rowSM = getSelectionModel();
/* 101 */     rowSM.addListSelectionListener(new LogTableListSelectionListener(this));
/*     */   }
/*     */ 
/*     */   public DateFormatManager getDateFormatManager()
/*     */   {
/* 114 */     return this._dateFormatManager;
/*     */   }
/*     */ 
/*     */   public void setDateFormatManager(DateFormatManager dfm)
/*     */   {
/* 121 */     this._dateFormatManager = dfm;
/*     */   }
/*     */ 
/*     */   public synchronized void clearLogRecords()
/*     */   {
/* 129 */     getFilteredLogTableModel().clear();
/*     */   }
/*     */ 
/*     */   public FilteredLogTableModel getFilteredLogTableModel() {
/* 133 */     return (FilteredLogTableModel)getModel();
/*     */   }
/*     */ 
/*     */   public void setDetailedView()
/*     */   {
/* 139 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 141 */     for (int f = 0; f < this._numCols; f++) {
/* 142 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/*     */ 
/* 145 */     for (int i = 0; i < this._numCols; i++) {
/* 146 */       model.addColumn(this._tableColumns[i]);
/*     */     }
/*     */ 
/* 149 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setView(List columns) {
/* 153 */     TableColumnModel model = getColumnModel();
/*     */ 
/* 156 */     for (int f = 0; f < this._numCols; f++) {
/* 157 */       model.removeColumn(this._tableColumns[f]);
/*     */     }
/* 159 */     Iterator selectedColumns = columns.iterator();
/* 160 */     Vector columnNameAndNumber = getColumnNameAndNumber();
/* 161 */     while (selectedColumns.hasNext())
/*     */     {
/* 163 */       model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
/*     */     }
/*     */ 
/* 167 */     sizeColumnsToFit(-1);
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 171 */     super.setFont(font);
/* 172 */     Graphics g = getGraphics();
/* 173 */     if (g != null) {
/* 174 */       FontMetrics fm = g.getFontMetrics(font);
/* 175 */       int height = fm.getHeight();
/* 176 */       this._rowHeight = (height + height / 3);
/* 177 */       setRowHeight(this._rowHeight);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 189 */     setRowHeight(this._rowHeight);
/* 190 */     setSelectionMode(0);
/*     */   }
/*     */ 
/*     */   protected Vector getColumnNameAndNumber()
/*     */   {
/* 195 */     Vector columnNameAndNumber = new Vector();
/* 196 */     for (int i = 0; i < this._colNames.length; i++) {
/* 197 */       columnNameAndNumber.add(i, this._colNames[i]);
/*     */     }
/* 199 */     return columnNameAndNumber;
/*     */   }
/*     */ 
/*     */   class LogTableListSelectionListener
/*     */     implements ListSelectionListener
/*     */   {
/*     */     protected JTable _table;
/*     */ 
/*     */     public LogTableListSelectionListener(JTable table)
/*     */     {
/* 214 */       this._table = table;
/*     */     }
/*     */ 
/*     */     public void valueChanged(ListSelectionEvent e)
/*     */     {
/* 219 */       if (e.getValueIsAdjusting()) {
/* 220 */         return;
/*     */       }
/*     */ 
/* 223 */       ListSelectionModel lsm = (ListSelectionModel)e.getSource();
/* 224 */       if (!lsm.isSelectionEmpty())
/*     */       {
/* 227 */         StringBuffer buf = new StringBuffer();
/* 228 */         int selectedRow = lsm.getMinSelectionIndex();
/*     */ 
/* 230 */         for (int i = 0; i < LogTable.this._numCols - 1; i++) {
/* 231 */           String value = "";
/* 232 */           Object obj = this._table.getModel().getValueAt(selectedRow, i);
/* 233 */           if (obj != null) {
/* 234 */             value = obj.toString();
/*     */           }
/*     */ 
/* 237 */           buf.append(LogTable.this._colNames[i] + ":");
/* 238 */           buf.append("\t");
/*     */ 
/* 240 */           if ((i == LogTable.this._colThread) || (i == LogTable.this._colMessage) || (i == LogTable.this._colLevel)) {
/* 241 */             buf.append("\t");
/*     */           }
/*     */ 
/* 244 */           if ((i == LogTable.this._colDate) || (i == LogTable.this._colNDC)) {
/* 245 */             buf.append("\t\t");
/*     */           }
/*     */ 
/* 253 */           buf.append(value);
/* 254 */           buf.append("\n");
/*     */         }
/* 256 */         buf.append(LogTable.this._colNames[(LogTable.this._numCols - 1)] + ":\n");
/* 257 */         Object obj = this._table.getModel().getValueAt(selectedRow, LogTable.this._numCols - 1);
/* 258 */         if (obj != null) {
/* 259 */           buf.append(obj.toString());
/*     */         }
/*     */ 
/* 262 */         LogTable.this._detailTextArea.setText(buf.toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTable
 * JD-Core Version:    0.6.0
 */
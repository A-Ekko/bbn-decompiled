/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EventObject;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.CellEditorListener;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.EventListenerList;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.tree.TreeCellEditor;
/*     */ 
/*     */ public class CategoryAbstractCellEditor
/*     */   implements TableCellEditor, TreeCellEditor
/*     */ {
/*  38 */   protected EventListenerList _listenerList = new EventListenerList();
/*     */   protected Object _value;
/*  40 */   protected ChangeEvent _changeEvent = null;
/*  41 */   protected int _clickCountToStart = 1;
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  56 */     return this._value;
/*     */   }
/*     */ 
/*     */   public void setCellEditorValue(Object value) {
/*  60 */     this._value = value;
/*     */   }
/*     */ 
/*     */   public void setClickCountToStart(int count) {
/*  64 */     this._clickCountToStart = count;
/*     */   }
/*     */ 
/*     */   public int getClickCountToStart() {
/*  68 */     return this._clickCountToStart;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject anEvent)
/*     */   {
/*  74 */     return (!(anEvent instanceof MouseEvent)) || 
/*  73 */       (((MouseEvent)anEvent).getClickCount() >= this._clickCountToStart);
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject anEvent)
/*     */   {
/*  84 */     return (isCellEditable(anEvent)) && (
/*  82 */       (anEvent == null) || (((MouseEvent)anEvent).getClickCount() >= this._clickCountToStart));
/*     */   }
/*     */ 
/*     */   public boolean stopCellEditing()
/*     */   {
/*  91 */     fireEditingStopped();
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   public void cancelCellEditing() {
/*  96 */     fireEditingCanceled();
/*     */   }
/*     */ 
/*     */   public void addCellEditorListener(CellEditorListener l) {
/* 100 */     this._listenerList.add(CellEditorListener.class, l);
/*     */   }
/*     */ 
/*     */   public void removeCellEditorListener(CellEditorListener l) {
/* 104 */     this._listenerList.remove(CellEditorListener.class, l);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
/*     */   {
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   protected void fireEditingStopped()
/*     */   {
/* 126 */     Object[] listeners = this._listenerList.getListenerList();
/*     */ 
/* 128 */     for (int i = listeners.length - 2; i >= 0; i -= 2)
/* 129 */       if (listeners[i] == CellEditorListener.class) {
/* 130 */         if (this._changeEvent == null) {
/* 131 */           this._changeEvent = new ChangeEvent(this);
/*     */         }
/*     */ 
/* 134 */         ((CellEditorListener)listeners[(i + 1)]).editingStopped(this._changeEvent);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void fireEditingCanceled()
/*     */   {
/* 140 */     Object[] listeners = this._listenerList.getListenerList();
/*     */ 
/* 142 */     for (int i = listeners.length - 2; i >= 0; i -= 2)
/* 143 */       if (listeners[i] == CellEditorListener.class) {
/* 144 */         if (this._changeEvent == null) {
/* 145 */           this._changeEvent = new ChangeEvent(this);
/*     */         }
/*     */ 
/* 148 */         ((CellEditorListener)listeners[(i + 1)]).editingCanceled(this._changeEvent);
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryAbstractCellEditor
 * JD-Core Version:    0.6.0
 */
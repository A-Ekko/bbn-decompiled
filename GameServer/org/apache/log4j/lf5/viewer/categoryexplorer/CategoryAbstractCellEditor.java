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
/*  47 */   protected EventListenerList _listenerList = new EventListenerList();
/*     */   protected Object _value;
/*  49 */   protected ChangeEvent _changeEvent = null;
/*  50 */   protected int _clickCountToStart = 1;
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  65 */     return this._value;
/*     */   }
/*     */ 
/*     */   public void setCellEditorValue(Object value) {
/*  69 */     this._value = value;
/*     */   }
/*     */ 
/*     */   public void setClickCountToStart(int count) {
/*  73 */     this._clickCountToStart = count;
/*     */   }
/*     */ 
/*     */   public int getClickCountToStart() {
/*  77 */     return this._clickCountToStart;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(EventObject anEvent)
/*     */   {
/*  83 */     return (!(anEvent instanceof MouseEvent)) || 
/*  82 */       (((MouseEvent)anEvent).getClickCount() >= this._clickCountToStart);
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject anEvent)
/*     */   {
/*  93 */     return (isCellEditable(anEvent)) && (
/*  91 */       (anEvent == null) || (((MouseEvent)anEvent).getClickCount() >= this._clickCountToStart));
/*     */   }
/*     */ 
/*     */   public boolean stopCellEditing()
/*     */   {
/* 100 */     fireEditingStopped();
/* 101 */     return true;
/*     */   }
/*     */ 
/*     */   public void cancelCellEditing() {
/* 105 */     fireEditingCanceled();
/*     */   }
/*     */ 
/*     */   public void addCellEditorListener(CellEditorListener l) {
/* 109 */     this._listenerList.add(CellEditorListener.class, l);
/*     */   }
/*     */ 
/*     */   public void removeCellEditorListener(CellEditorListener l) {
/* 113 */     this._listenerList.remove(CellEditorListener.class, l);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
/*     */   {
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */   {
/* 128 */     return null;
/*     */   }
/*     */ 
/*     */   protected void fireEditingStopped()
/*     */   {
/* 135 */     Object[] listeners = this._listenerList.getListenerList();
/*     */ 
/* 137 */     for (int i = listeners.length - 2; i >= 0; i -= 2)
/* 138 */       if (listeners[i] == CellEditorListener.class) {
/* 139 */         if (this._changeEvent == null) {
/* 140 */           this._changeEvent = new ChangeEvent(this);
/*     */         }
/*     */ 
/* 143 */         ((CellEditorListener)listeners[(i + 1)]).editingStopped(this._changeEvent);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void fireEditingCanceled()
/*     */   {
/* 149 */     Object[] listeners = this._listenerList.getListenerList();
/*     */ 
/* 151 */     for (int i = listeners.length - 2; i >= 0; i -= 2)
/* 152 */       if (listeners[i] == CellEditorListener.class) {
/* 153 */         if (this._changeEvent == null) {
/* 154 */           this._changeEvent = new ChangeEvent(this);
/*     */         }
/*     */ 
/* 157 */         ((CellEditorListener)listeners[(i + 1)]).editingCanceled(this._changeEvent);
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryAbstractCellEditor
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Adjustable;
/*     */ import java.awt.Component;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.TableModel;
/*     */ 
/*     */ public class LF5SwingUtils
/*     */ {
/*     */   public static void selectRow(int row, JTable table, JScrollPane pane)
/*     */   {
/*  53 */     if ((table == null) || (pane == null)) {
/*  54 */       return;
/*     */     }
/*  56 */     if (!contains(row, table.getModel())) {
/*  57 */       return;
/*     */     }
/*  59 */     moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
/*  60 */     selectRow(row, table.getSelectionModel());
/*     */ 
/*  64 */     repaintLater(table);
/*     */   }
/*     */ 
/*     */   public static void makeScrollBarTrack(Adjustable scrollBar)
/*     */   {
/*  72 */     if (scrollBar == null) {
/*  73 */       return;
/*     */     }
/*  75 */     scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
/*     */   }
/*     */ 
/*     */   public static void makeVerticalScrollBarTrack(JScrollPane pane)
/*     */   {
/*  84 */     if (pane == null) {
/*  85 */       return;
/*     */     }
/*  87 */     makeScrollBarTrack(pane.getVerticalScrollBar());
/*     */   }
/*     */ 
/*     */   protected static boolean contains(int row, TableModel model)
/*     */   {
/*  94 */     if (model == null) {
/*  95 */       return false;
/*     */     }
/*  97 */     if (row < 0) {
/*  98 */       return false;
/*     */     }
/*     */ 
/* 101 */     return row < model.getRowCount();
/*     */   }
/*     */ 
/*     */   protected static void selectRow(int row, ListSelectionModel model)
/*     */   {
/* 107 */     if (model == null) {
/* 108 */       return;
/*     */     }
/* 110 */     model.setSelectionInterval(row, row);
/*     */   }
/*     */ 
/*     */   protected static void moveAdjustable(int location, Adjustable scrollBar) {
/* 114 */     if (scrollBar == null) {
/* 115 */       return;
/*     */     }
/* 117 */     scrollBar.setValue(location);
/*     */   }
/*     */ 
/*     */   protected static void repaintLater(JComponent component)
/*     */   {
/* 125 */     SwingUtilities.invokeLater(new Runnable(component) { private final JComponent val$component;
/*     */ 
/* 127 */       public void run() { this.val$component.repaint();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LF5SwingUtils
 * JD-Core Version:    0.6.0
 */
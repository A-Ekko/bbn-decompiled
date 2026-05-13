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
/*  62 */     if ((table == null) || (pane == null)) {
/*  63 */       return;
/*     */     }
/*  65 */     if (!contains(row, table.getModel())) {
/*  66 */       return;
/*     */     }
/*  68 */     moveAdjustable(row * table.getRowHeight(), pane.getVerticalScrollBar());
/*  69 */     selectRow(row, table.getSelectionModel());
/*     */ 
/*  73 */     repaintLater(table);
/*     */   }
/*     */ 
/*     */   public static void makeScrollBarTrack(Adjustable scrollBar)
/*     */   {
/*  81 */     if (scrollBar == null) {
/*  82 */       return;
/*     */     }
/*  84 */     scrollBar.addAdjustmentListener(new TrackingAdjustmentListener());
/*     */   }
/*     */ 
/*     */   public static void makeVerticalScrollBarTrack(JScrollPane pane)
/*     */   {
/*  93 */     if (pane == null) {
/*  94 */       return;
/*     */     }
/*  96 */     makeScrollBarTrack(pane.getVerticalScrollBar());
/*     */   }
/*     */ 
/*     */   protected static boolean contains(int row, TableModel model)
/*     */   {
/* 103 */     if (model == null) {
/* 104 */       return false;
/*     */     }
/* 106 */     if (row < 0) {
/* 107 */       return false;
/*     */     }
/*     */ 
/* 110 */     return row < model.getRowCount();
/*     */   }
/*     */ 
/*     */   protected static void selectRow(int row, ListSelectionModel model)
/*     */   {
/* 116 */     if (model == null) {
/* 117 */       return;
/*     */     }
/* 119 */     model.setSelectionInterval(row, row);
/*     */   }
/*     */ 
/*     */   protected static void moveAdjustable(int location, Adjustable scrollBar) {
/* 123 */     if (scrollBar == null) {
/* 124 */       return;
/*     */     }
/* 126 */     scrollBar.setValue(location);
/*     */   }
/*     */ 
/*     */   protected static void repaintLater(JComponent component)
/*     */   {
/* 134 */     SwingUtilities.invokeLater(new Runnable(component) { private final JComponent val$component;
/*     */ 
/* 136 */       public void run() { this.val$component.repaint();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LF5SwingUtils
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EventObject;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeCellEditor;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryImmediateEditor extends DefaultTreeCellEditor
/*     */ {
/*     */   private CategoryNodeRenderer renderer;
/*  44 */   protected Icon editingIcon = null;
/*     */ 
/*     */   public CategoryImmediateEditor(JTree tree, CategoryNodeRenderer renderer, CategoryNodeEditor editor)
/*     */   {
/*  56 */     super(tree, renderer, editor);
/*  57 */     this.renderer = renderer;
/*  58 */     renderer.setIcon(null);
/*  59 */     renderer.setLeafIcon(null);
/*  60 */     renderer.setOpenIcon(null);
/*  61 */     renderer.setClosedIcon(null);
/*     */ 
/*  63 */     this.editingIcon = null;
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject e)
/*     */   {
/*  70 */     boolean rv = false;
/*     */ 
/*  72 */     if ((e instanceof MouseEvent)) {
/*  73 */       MouseEvent me = (MouseEvent)e;
/*  74 */       TreePath path = this.tree.getPathForLocation(me.getX(), me.getY());
/*     */ 
/*  76 */       CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*     */ 
/*  79 */       rv = node.isLeaf();
/*     */     }
/*  81 */     return rv;
/*     */   }
/*     */ 
/*     */   public boolean inCheckBoxHitRegion(MouseEvent e) {
/*  85 */     TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
/*     */ 
/*  87 */     if (path == null) {
/*  88 */       return false;
/*     */     }
/*  90 */     CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*  91 */     boolean rv = false;
/*     */ 
/*  97 */     Rectangle bounds = this.tree.getRowBounds(this.lastRow);
/*  98 */     Dimension checkBoxOffset = this.renderer.getCheckBoxOffset();
/*     */ 
/* 101 */     bounds.translate(this.offset + checkBoxOffset.width, checkBoxOffset.height);
/*     */ 
/* 104 */     rv = bounds.contains(e.getPoint());
/*     */ 
/* 106 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean canEditImmediately(EventObject e)
/*     */   {
/* 114 */     boolean rv = false;
/*     */ 
/* 116 */     if ((e instanceof MouseEvent)) {
/* 117 */       MouseEvent me = (MouseEvent)e;
/* 118 */       rv = inCheckBoxHitRegion(me);
/*     */     }
/*     */ 
/* 121 */     return rv;
/*     */   }
/*     */ 
/*     */   protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
/*     */   {
/* 128 */     this.offset = 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryImmediateEditor
 * JD-Core Version:    0.6.0
 */
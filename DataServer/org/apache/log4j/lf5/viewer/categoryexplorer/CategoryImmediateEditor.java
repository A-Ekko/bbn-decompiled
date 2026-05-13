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
/*  35 */   protected Icon editingIcon = null;
/*     */ 
/*     */   public CategoryImmediateEditor(JTree tree, CategoryNodeRenderer renderer, CategoryNodeEditor editor)
/*     */   {
/*  47 */     super(tree, renderer, editor);
/*  48 */     this.renderer = renderer;
/*  49 */     renderer.setIcon(null);
/*  50 */     renderer.setLeafIcon(null);
/*  51 */     renderer.setOpenIcon(null);
/*  52 */     renderer.setClosedIcon(null);
/*     */ 
/*  54 */     this.editingIcon = null;
/*     */   }
/*     */ 
/*     */   public boolean shouldSelectCell(EventObject e)
/*     */   {
/*  61 */     boolean rv = false;
/*     */ 
/*  63 */     if ((e instanceof MouseEvent)) {
/*  64 */       MouseEvent me = (MouseEvent)e;
/*  65 */       TreePath path = this.tree.getPathForLocation(me.getX(), me.getY());
/*     */ 
/*  67 */       CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*     */ 
/*  70 */       rv = node.isLeaf();
/*     */     }
/*  72 */     return rv;
/*     */   }
/*     */ 
/*     */   public boolean inCheckBoxHitRegion(MouseEvent e) {
/*  76 */     TreePath path = this.tree.getPathForLocation(e.getX(), e.getY());
/*     */ 
/*  78 */     if (path == null) {
/*  79 */       return false;
/*     */     }
/*  81 */     CategoryNode node = (CategoryNode)path.getLastPathComponent();
/*  82 */     boolean rv = false;
/*     */ 
/*  88 */     Rectangle bounds = this.tree.getRowBounds(this.lastRow);
/*  89 */     Dimension checkBoxOffset = this.renderer.getCheckBoxOffset();
/*     */ 
/*  92 */     bounds.translate(this.offset + checkBoxOffset.width, checkBoxOffset.height);
/*     */ 
/*  95 */     rv = bounds.contains(e.getPoint());
/*     */ 
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean canEditImmediately(EventObject e)
/*     */   {
/* 105 */     boolean rv = false;
/*     */ 
/* 107 */     if ((e instanceof MouseEvent)) {
/* 108 */       MouseEvent me = (MouseEvent)e;
/* 109 */       rv = inCheckBoxHitRegion(me);
/*     */     }
/*     */ 
/* 112 */     return rv;
/*     */   }
/*     */ 
/*     */   protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row)
/*     */   {
/* 119 */     this.offset = 0;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryImmediateEditor
 * JD-Core Version:    0.6.0
 */
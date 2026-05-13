/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelEvent;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryExplorerTree extends JTree
/*     */ {
/*     */   private static final long serialVersionUID = 8066257446951323576L;
/*     */   protected CategoryExplorerModel _model;
/*  45 */   protected boolean _rootAlreadyExpanded = false;
/*     */ 
/*     */   public CategoryExplorerTree(CategoryExplorerModel model)
/*     */   {
/*  59 */     super(model);
/*     */ 
/*  61 */     this._model = model;
/*  62 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerTree()
/*     */   {
/*  71 */     CategoryNode rootNode = new CategoryNode("Categories");
/*     */ 
/*  73 */     this._model = new CategoryExplorerModel(rootNode);
/*     */ 
/*  75 */     setModel(this._model);
/*     */ 
/*  77 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerModel getExplorerModel()
/*     */   {
/*  85 */     return this._model;
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent e)
/*     */   {
/*     */     try {
/*  91 */       return super.getToolTipText(e); } catch (Exception ex) {
/*     */     }
/*  93 */     return "";
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/* 104 */     putClientProperty("JTree.lineStyle", "Angled");
/*     */ 
/* 108 */     CategoryNodeRenderer renderer = new CategoryNodeRenderer();
/* 109 */     setEditable(true);
/* 110 */     setCellRenderer(renderer);
/*     */ 
/* 112 */     CategoryNodeEditor editor = new CategoryNodeEditor(this._model);
/*     */ 
/* 114 */     setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));
/*     */ 
/* 117 */     setShowsRootHandles(true);
/*     */ 
/* 119 */     setToolTipText("");
/*     */ 
/* 121 */     ensureRootExpansion();
/*     */   }
/*     */ 
/*     */   protected void expandRootNode()
/*     */   {
/* 126 */     if (this._rootAlreadyExpanded) {
/* 127 */       return;
/*     */     }
/* 129 */     this._rootAlreadyExpanded = true;
/* 130 */     TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
/* 131 */     expandPath(path);
/*     */   }
/*     */ 
/*     */   protected void ensureRootExpansion() {
/* 135 */     this._model.addTreeModelListener(new TreeModelAdapter() {
/*     */       public void treeNodesInserted(TreeModelEvent e) {
/* 137 */         CategoryExplorerTree.this.expandRootNode();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree
 * JD-Core Version:    0.6.0
 */
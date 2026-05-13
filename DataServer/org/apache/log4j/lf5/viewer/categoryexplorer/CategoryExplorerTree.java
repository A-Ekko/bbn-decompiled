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
/*     */   protected CategoryExplorerModel _model;
/*  35 */   protected boolean _rootAlreadyExpanded = false;
/*     */ 
/*     */   public CategoryExplorerTree(CategoryExplorerModel model)
/*     */   {
/*  49 */     super(model);
/*     */ 
/*  51 */     this._model = model;
/*  52 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerTree()
/*     */   {
/*  61 */     CategoryNode rootNode = new CategoryNode("Categories");
/*     */ 
/*  63 */     this._model = new CategoryExplorerModel(rootNode);
/*     */ 
/*  65 */     setModel(this._model);
/*     */ 
/*  67 */     init();
/*     */   }
/*     */ 
/*     */   public CategoryExplorerModel getExplorerModel()
/*     */   {
/*  75 */     return this._model;
/*     */   }
/*     */ 
/*     */   public String getToolTipText(MouseEvent e)
/*     */   {
/*     */     try {
/*  81 */       return super.getToolTipText(e); } catch (Exception ex) {
/*     */     }
/*  83 */     return "";
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/*  94 */     putClientProperty("JTree.lineStyle", "Angled");
/*     */ 
/*  98 */     CategoryNodeRenderer renderer = new CategoryNodeRenderer();
/*  99 */     setEditable(true);
/* 100 */     setCellRenderer(renderer);
/*     */ 
/* 102 */     CategoryNodeEditor editor = new CategoryNodeEditor(this._model);
/*     */ 
/* 104 */     setCellEditor(new CategoryImmediateEditor(this, new CategoryNodeRenderer(), editor));
/*     */ 
/* 107 */     setShowsRootHandles(true);
/*     */ 
/* 109 */     setToolTipText("");
/*     */ 
/* 111 */     ensureRootExpansion();
/*     */   }
/*     */ 
/*     */   protected void expandRootNode()
/*     */   {
/* 116 */     if (this._rootAlreadyExpanded) {
/* 117 */       return;
/*     */     }
/* 119 */     this._rootAlreadyExpanded = true;
/* 120 */     TreePath path = new TreePath(this._model.getRootCategoryNode().getPath());
/* 121 */     expandPath(path);
/*     */   }
/*     */ 
/*     */   protected void ensureRootExpansion() {
/* 125 */     this._model.addTreeModelListener(new TreeModelAdapter() {
/*     */       public void treeNodesInserted(TreeModelEvent e) {
/* 127 */         CategoryExplorerTree.this.expandRootNode();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree
 * JD-Core Version:    0.6.0
 */
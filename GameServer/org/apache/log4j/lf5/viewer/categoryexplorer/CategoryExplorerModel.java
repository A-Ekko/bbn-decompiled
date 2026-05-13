/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.AWTEventMulticaster;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ 
/*     */ public class CategoryExplorerModel extends DefaultTreeModel
/*     */ {
/*     */   private static final long serialVersionUID = -3413887384316015901L;
/*  52 */   protected boolean _renderFatal = true;
/*  53 */   protected ActionListener _listener = null;
/*  54 */   protected ActionEvent _event = new ActionEvent(this, 1001, "Nodes Selection changed");
/*     */ 
/*     */   public CategoryExplorerModel(CategoryNode node)
/*     */   {
/*  67 */     super(node);
/*     */   }
/*     */ 
/*     */   public void addLogRecord(LogRecord lr)
/*     */   {
/*  74 */     CategoryPath path = new CategoryPath(lr.getCategory());
/*  75 */     addCategory(path);
/*  76 */     CategoryNode node = getCategoryNode(path);
/*  77 */     node.addRecord();
/*  78 */     if ((this._renderFatal) && (lr.isFatal())) {
/*  79 */       TreeNode[] nodes = getPathToRoot(node);
/*  80 */       int len = nodes.length;
/*     */ 
/*  85 */       for (int i = 1; i < len - 1; i++) {
/*  86 */         CategoryNode parent = (CategoryNode)nodes[i];
/*  87 */         parent.setHasFatalChildren(true);
/*  88 */         nodeChanged(parent);
/*     */       }
/*  90 */       node.setHasFatalRecords(true);
/*  91 */       nodeChanged(node);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CategoryNode getRootCategoryNode() {
/*  96 */     return (CategoryNode)getRoot();
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(String category) {
/* 100 */     CategoryPath path = new CategoryPath(category);
/* 101 */     return getCategoryNode(path);
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(CategoryPath path)
/*     */   {
/* 108 */     CategoryNode root = (CategoryNode)getRoot();
/* 109 */     CategoryNode parent = root;
/*     */ 
/* 111 */     for (int i = 0; i < path.size(); i++) {
/* 112 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 115 */       Enumeration children = parent.children();
/*     */ 
/* 117 */       boolean categoryAlreadyExists = false;
/* 118 */       while (children.hasMoreElements()) {
/* 119 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 120 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 122 */         String pathLC = element.getTitle().toLowerCase();
/* 123 */         if (title.equals(pathLC)) {
/* 124 */           categoryAlreadyExists = true;
/*     */ 
/* 126 */           parent = node;
/* 127 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 131 */       if (!categoryAlreadyExists) {
/* 132 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 136 */     return parent;
/*     */   }
/*     */ 
/*     */   public boolean isCategoryPathActive(CategoryPath path)
/*     */   {
/* 144 */     CategoryNode root = (CategoryNode)getRoot();
/* 145 */     CategoryNode parent = root;
/* 146 */     boolean active = false;
/*     */ 
/* 148 */     for (int i = 0; i < path.size(); i++) {
/* 149 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 152 */       Enumeration children = parent.children();
/*     */ 
/* 154 */       boolean categoryAlreadyExists = false;
/* 155 */       active = false;
/*     */ 
/* 157 */       while (children.hasMoreElements()) {
/* 158 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 159 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 161 */         String pathLC = element.getTitle().toLowerCase();
/* 162 */         if (title.equals(pathLC)) {
/* 163 */           categoryAlreadyExists = true;
/*     */ 
/* 165 */           parent = node;
/*     */ 
/* 167 */           if (!parent.isSelected()) break;
/* 168 */           active = true; break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 175 */       if ((!active) || (!categoryAlreadyExists)) {
/* 176 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 180 */     return active;
/*     */   }
/*     */ 
/*     */   public CategoryNode addCategory(CategoryPath path)
/*     */   {
/* 192 */     CategoryNode root = (CategoryNode)getRoot();
/* 193 */     CategoryNode parent = root;
/*     */ 
/* 195 */     for (int i = 0; i < path.size(); i++) {
/* 196 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 199 */       Enumeration children = parent.children();
/*     */ 
/* 201 */       boolean categoryAlreadyExists = false;
/* 202 */       while (children.hasMoreElements()) {
/* 203 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 204 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 206 */         String pathLC = element.getTitle().toLowerCase();
/* 207 */         if (title.equals(pathLC)) {
/* 208 */           categoryAlreadyExists = true;
/*     */ 
/* 210 */           parent = node;
/* 211 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 215 */       if (categoryAlreadyExists)
/*     */         continue;
/* 217 */       CategoryNode newNode = new CategoryNode(element.getTitle());
/*     */ 
/* 225 */       insertNodeInto(newNode, parent, parent.getChildCount());
/* 226 */       refresh(newNode);
/*     */ 
/* 229 */       parent = newNode;
/*     */     }
/*     */ 
/* 234 */     return parent;
/*     */   }
/*     */ 
/*     */   public void update(CategoryNode node, boolean selected) {
/* 238 */     if (node.isSelected() == selected) {
/* 239 */       return;
/*     */     }
/*     */ 
/* 242 */     if (selected)
/* 243 */       setParentSelection(node, true);
/*     */     else
/* 245 */       setDescendantSelection(node, false);
/*     */   }
/*     */ 
/*     */   public void setDescendantSelection(CategoryNode node, boolean selected)
/*     */   {
/* 250 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 252 */     while (descendants.hasMoreElements()) {
/* 253 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/*     */ 
/* 255 */       if (current.isSelected() != selected) {
/* 256 */         current.setSelected(selected);
/* 257 */         nodeChanged(current);
/*     */       }
/*     */     }
/* 260 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public void setParentSelection(CategoryNode node, boolean selected) {
/* 264 */     TreeNode[] nodes = getPathToRoot(node);
/* 265 */     int len = nodes.length;
/*     */ 
/* 270 */     for (int i = 1; i < len; i++) {
/* 271 */       CategoryNode parent = (CategoryNode)nodes[i];
/* 272 */       if (parent.isSelected() != selected) {
/* 273 */         parent.setSelected(selected);
/* 274 */         nodeChanged(parent);
/*     */       }
/*     */     }
/* 277 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public synchronized void addActionListener(ActionListener l)
/*     */   {
/* 282 */     this._listener = AWTEventMulticaster.add(this._listener, l);
/*     */   }
/*     */ 
/*     */   public synchronized void removeActionListener(ActionListener l) {
/* 286 */     this._listener = AWTEventMulticaster.remove(this._listener, l);
/*     */   }
/*     */ 
/*     */   public void resetAllNodeCounts() {
/* 290 */     Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
/*     */ 
/* 292 */     while (nodes.hasMoreElements()) {
/* 293 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 294 */       current.resetNumberOfContainedRecords();
/* 295 */       nodeChanged(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TreePath getTreePathToRoot(CategoryNode node)
/*     */   {
/* 306 */     if (node == null) {
/* 307 */       return null;
/*     */     }
/* 309 */     return new TreePath(getPathToRoot(node));
/*     */   }
/*     */ 
/*     */   protected void notifyActionListeners()
/*     */   {
/* 316 */     if (this._listener != null)
/* 317 */       this._listener.actionPerformed(this._event);
/*     */   }
/*     */ 
/*     */   protected void refresh(CategoryNode node)
/*     */   {
/* 325 */     SwingUtilities.invokeLater(new Runnable(node) { private final CategoryNode val$node;
/*     */ 
/* 327 */       public void run() { CategoryExplorerModel.this.nodeChanged(this.val$node);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel
 * JD-Core Version:    0.6.0
 */
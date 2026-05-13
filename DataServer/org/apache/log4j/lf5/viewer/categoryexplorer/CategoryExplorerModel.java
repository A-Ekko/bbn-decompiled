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
/*  41 */   protected boolean _renderFatal = true;
/*  42 */   protected ActionListener _listener = null;
/*  43 */   protected ActionEvent _event = new ActionEvent(this, 1001, "Nodes Selection changed");
/*     */ 
/*     */   public CategoryExplorerModel(CategoryNode node)
/*     */   {
/*  56 */     super(node);
/*     */   }
/*     */ 
/*     */   public void addLogRecord(LogRecord lr)
/*     */   {
/*  63 */     CategoryPath path = new CategoryPath(lr.getCategory());
/*  64 */     addCategory(path);
/*  65 */     CategoryNode node = getCategoryNode(path);
/*  66 */     node.addRecord();
/*  67 */     if ((this._renderFatal) && (lr.isFatal())) {
/*  68 */       TreeNode[] nodes = getPathToRoot(node);
/*  69 */       int len = nodes.length;
/*     */ 
/*  74 */       for (int i = 1; i < len - 1; i++) {
/*  75 */         CategoryNode parent = (CategoryNode)nodes[i];
/*  76 */         parent.setHasFatalChildren(true);
/*  77 */         nodeChanged(parent);
/*     */       }
/*  79 */       node.setHasFatalRecords(true);
/*  80 */       nodeChanged(node);
/*     */     }
/*     */   }
/*     */ 
/*     */   public CategoryNode getRootCategoryNode() {
/*  85 */     return (CategoryNode)getRoot();
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(String category) {
/*  89 */     CategoryPath path = new CategoryPath(category);
/*  90 */     return getCategoryNode(path);
/*     */   }
/*     */ 
/*     */   public CategoryNode getCategoryNode(CategoryPath path)
/*     */   {
/*  97 */     CategoryNode root = (CategoryNode)getRoot();
/*  98 */     CategoryNode parent = root;
/*     */ 
/* 100 */     for (int i = 0; i < path.size(); i++) {
/* 101 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 104 */       Enumeration children = parent.children();
/*     */ 
/* 106 */       boolean categoryAlreadyExists = false;
/* 107 */       while (children.hasMoreElements()) {
/* 108 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 109 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 111 */         String pathLC = element.getTitle().toLowerCase();
/* 112 */         if (title.equals(pathLC)) {
/* 113 */           categoryAlreadyExists = true;
/*     */ 
/* 115 */           parent = node;
/* 116 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 120 */       if (!categoryAlreadyExists) {
/* 121 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 125 */     return parent;
/*     */   }
/*     */ 
/*     */   public boolean isCategoryPathActive(CategoryPath path)
/*     */   {
/* 133 */     CategoryNode root = (CategoryNode)getRoot();
/* 134 */     CategoryNode parent = root;
/* 135 */     boolean active = false;
/*     */ 
/* 137 */     for (int i = 0; i < path.size(); i++) {
/* 138 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 141 */       Enumeration children = parent.children();
/*     */ 
/* 143 */       boolean categoryAlreadyExists = false;
/* 144 */       active = false;
/*     */ 
/* 146 */       while (children.hasMoreElements()) {
/* 147 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 148 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 150 */         String pathLC = element.getTitle().toLowerCase();
/* 151 */         if (title.equals(pathLC)) {
/* 152 */           categoryAlreadyExists = true;
/*     */ 
/* 154 */           parent = node;
/*     */ 
/* 156 */           if (!parent.isSelected()) break;
/* 157 */           active = true; break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 164 */       if ((!active) || (!categoryAlreadyExists)) {
/* 165 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 169 */     return active;
/*     */   }
/*     */ 
/*     */   public CategoryNode addCategory(CategoryPath path)
/*     */   {
/* 181 */     CategoryNode root = (CategoryNode)getRoot();
/* 182 */     CategoryNode parent = root;
/*     */ 
/* 184 */     for (int i = 0; i < path.size(); i++) {
/* 185 */       CategoryElement element = path.categoryElementAt(i);
/*     */ 
/* 188 */       Enumeration children = parent.children();
/*     */ 
/* 190 */       boolean categoryAlreadyExists = false;
/* 191 */       while (children.hasMoreElements()) {
/* 192 */         CategoryNode node = (CategoryNode)children.nextElement();
/* 193 */         String title = node.getTitle().toLowerCase();
/*     */ 
/* 195 */         String pathLC = element.getTitle().toLowerCase();
/* 196 */         if (title.equals(pathLC)) {
/* 197 */           categoryAlreadyExists = true;
/*     */ 
/* 199 */           parent = node;
/* 200 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 204 */       if (categoryAlreadyExists)
/*     */         continue;
/* 206 */       CategoryNode newNode = new CategoryNode(element.getTitle());
/*     */ 
/* 214 */       insertNodeInto(newNode, parent, parent.getChildCount());
/* 215 */       refresh(newNode);
/*     */ 
/* 218 */       parent = newNode;
/*     */     }
/*     */ 
/* 223 */     return parent;
/*     */   }
/*     */ 
/*     */   public void update(CategoryNode node, boolean selected) {
/* 227 */     if (node.isSelected() == selected) {
/* 228 */       return;
/*     */     }
/*     */ 
/* 231 */     if (selected)
/* 232 */       setParentSelection(node, true);
/*     */     else
/* 234 */       setDescendantSelection(node, false);
/*     */   }
/*     */ 
/*     */   public void setDescendantSelection(CategoryNode node, boolean selected)
/*     */   {
/* 239 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 241 */     while (descendants.hasMoreElements()) {
/* 242 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/*     */ 
/* 244 */       if (current.isSelected() != selected) {
/* 245 */         current.setSelected(selected);
/* 246 */         nodeChanged(current);
/*     */       }
/*     */     }
/* 249 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public void setParentSelection(CategoryNode node, boolean selected) {
/* 253 */     TreeNode[] nodes = getPathToRoot(node);
/* 254 */     int len = nodes.length;
/*     */ 
/* 259 */     for (int i = 1; i < len; i++) {
/* 260 */       CategoryNode parent = (CategoryNode)nodes[i];
/* 261 */       if (parent.isSelected() != selected) {
/* 262 */         parent.setSelected(selected);
/* 263 */         nodeChanged(parent);
/*     */       }
/*     */     }
/* 266 */     notifyActionListeners();
/*     */   }
/*     */ 
/*     */   public synchronized void addActionListener(ActionListener l)
/*     */   {
/* 271 */     this._listener = AWTEventMulticaster.add(this._listener, l);
/*     */   }
/*     */ 
/*     */   public synchronized void removeActionListener(ActionListener l) {
/* 275 */     this._listener = AWTEventMulticaster.remove(this._listener, l);
/*     */   }
/*     */ 
/*     */   public void resetAllNodeCounts() {
/* 279 */     Enumeration nodes = getRootCategoryNode().depthFirstEnumeration();
/*     */ 
/* 281 */     while (nodes.hasMoreElements()) {
/* 282 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 283 */       current.resetNumberOfContainedRecords();
/* 284 */       nodeChanged(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TreePath getTreePathToRoot(CategoryNode node)
/*     */   {
/* 295 */     if (node == null) {
/* 296 */       return null;
/*     */     }
/* 298 */     return new TreePath(getPathToRoot(node));
/*     */   }
/*     */ 
/*     */   protected void notifyActionListeners()
/*     */   {
/* 305 */     if (this._listener != null)
/* 306 */       this._listener.actionPerformed(this._event);
/*     */   }
/*     */ 
/*     */   protected void refresh(CategoryNode node)
/*     */   {
/* 314 */     SwingUtilities.invokeLater(new Runnable(node) { private final CategoryNode val$node;
/*     */ 
/* 316 */       public void run() { CategoryExplorerModel.this.nodeChanged(this.val$node);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel
 * JD-Core Version:    0.6.0
 */
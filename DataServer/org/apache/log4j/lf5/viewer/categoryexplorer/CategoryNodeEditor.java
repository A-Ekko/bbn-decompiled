/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public class CategoryNodeEditor extends CategoryAbstractCellEditor
/*     */ {
/*     */   protected CategoryNodeEditorRenderer _renderer;
/*     */   protected CategoryNode _lastEditedNode;
/*     */   protected JCheckBox _checkBox;
/*     */   protected CategoryExplorerModel _categoryModel;
/*     */   protected JTree _tree;
/*     */ 
/*     */   public CategoryNodeEditor(CategoryExplorerModel model)
/*     */   {
/*  52 */     this._renderer = new CategoryNodeEditorRenderer();
/*  53 */     this._checkBox = this._renderer.getCheckBox();
/*  54 */     this._categoryModel = model;
/*     */ 
/*  56 */     this._checkBox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  58 */         CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
/*  59 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*  63 */     this._renderer.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/*  65 */         if ((e.getModifiers() & 0x4) != 0) {
/*  66 */           CategoryNodeEditor.this.showPopup(CategoryNodeEditor.this._lastEditedNode, e.getX(), e.getY());
/*     */         }
/*  68 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
/*     */   {
/*  80 */     this._lastEditedNode = ((CategoryNode)value);
/*  81 */     this._tree = tree;
/*     */ 
/*  83 */     return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  90 */     return this._lastEditedNode.getUserObject();
/*     */   }
/*     */ 
/*     */   protected JMenuItem createPropertiesMenuItem(CategoryNode node)
/*     */   {
/*  97 */     JMenuItem result = new JMenuItem("Properties");
/*  98 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 100 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.showPropertiesDialog(this.val$node);
/*     */       }
/*     */     });
/* 103 */     return result;
/*     */   }
/*     */ 
/*     */   protected void showPropertiesDialog(CategoryNode node) {
/* 107 */     JOptionPane.showMessageDialog(this._tree, getDisplayedProperties(node), "Category Properties: " + node.getTitle(), -1);
/*     */   }
/*     */ 
/*     */   protected Object getDisplayedProperties(CategoryNode node)
/*     */   {
/* 116 */     ArrayList result = new ArrayList();
/* 117 */     result.add("Category: " + node.getTitle());
/* 118 */     if (node.hasFatalRecords()) {
/* 119 */       result.add("Contains at least one fatal LogRecord.");
/*     */     }
/* 121 */     if (node.hasFatalChildren()) {
/* 122 */       result.add("Contains descendants with a fatal LogRecord.");
/*     */     }
/* 124 */     result.add("LogRecords in this category alone: " + node.getNumberOfContainedRecords());
/*     */ 
/* 126 */     result.add("LogRecords in descendant categories: " + node.getNumberOfRecordsFromChildren());
/*     */ 
/* 128 */     result.add("LogRecords in this category including descendants: " + node.getTotalNumberOfRecords());
/*     */ 
/* 130 */     return result.toArray();
/*     */   }
/*     */ 
/*     */   protected void showPopup(CategoryNode node, int x, int y) {
/* 134 */     JPopupMenu popup = new JPopupMenu();
/* 135 */     popup.setSize(150, 400);
/*     */ 
/* 139 */     if (node.getParent() == null) {
/* 140 */       popup.add(createRemoveMenuItem());
/* 141 */       popup.addSeparator();
/*     */     }
/* 143 */     popup.add(createSelectDescendantsMenuItem(node));
/* 144 */     popup.add(createUnselectDescendantsMenuItem(node));
/* 145 */     popup.addSeparator();
/* 146 */     popup.add(createExpandMenuItem(node));
/* 147 */     popup.add(createCollapseMenuItem(node));
/* 148 */     popup.addSeparator();
/* 149 */     popup.add(createPropertiesMenuItem(node));
/* 150 */     popup.show(this._renderer, x, y);
/*     */   }
/*     */ 
/*     */   protected JMenuItem createSelectDescendantsMenuItem(CategoryNode node) {
/* 154 */     JMenuItem selectDescendants = new JMenuItem("Select All Descendant Categories");
/*     */ 
/* 156 */     selectDescendants.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 159 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this._categoryModel.setDescendantSelection(this.val$node, true);
/*     */       }
/*     */     });
/* 163 */     return selectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createUnselectDescendantsMenuItem(CategoryNode node) {
/* 167 */     JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Categories");
/*     */ 
/* 169 */     unselectDescendants.addActionListener(new ActionListener(node) {
/*     */       private final CategoryNode val$node;
/*     */ 
/* 173 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this._categoryModel.setDescendantSelection(this.val$node, false);
/*     */       }
/*     */     });
/* 178 */     return unselectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createExpandMenuItem(CategoryNode node) {
/* 182 */     JMenuItem result = new JMenuItem("Expand All Descendant Categories");
/* 183 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 185 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.expandDescendants(this.val$node);
/*     */       }
/*     */     });
/* 188 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createCollapseMenuItem(CategoryNode node) {
/* 192 */     JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
/* 193 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 195 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.collapseDescendants(this.val$node);
/*     */       }
/*     */     });
/* 198 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createRemoveMenuItem()
/*     */   {
/* 211 */     JMenuItem result = new JMenuItem("Remove All Empty Categories");
/* 212 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 214 */         while (CategoryNodeEditor.this.removeUnusedNodes() > 0);
/*     */       }
/*     */     });
/* 217 */     return result;
/*     */   }
/*     */ 
/*     */   protected void expandDescendants(CategoryNode node) {
/* 221 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 223 */     while (descendants.hasMoreElements()) {
/* 224 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 225 */       expand(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void collapseDescendants(CategoryNode node) {
/* 230 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 232 */     while (descendants.hasMoreElements()) {
/* 233 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 234 */       collapse(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int removeUnusedNodes()
/*     */   {
/* 242 */     int count = 0;
/* 243 */     CategoryNode root = this._categoryModel.getRootCategoryNode();
/* 244 */     Enumeration enum = root.depthFirstEnumeration();
/* 245 */     while (enum.hasMoreElements()) {
/* 246 */       CategoryNode node = (CategoryNode)enum.nextElement();
/* 247 */       if ((!node.isLeaf()) || (node.getNumberOfContainedRecords() != 0) || (node.getParent() == null))
/*     */         continue;
/* 249 */       this._categoryModel.removeNodeFromParent(node);
/* 250 */       count++;
/*     */     }
/*     */ 
/* 254 */     return count;
/*     */   }
/*     */ 
/*     */   protected void expand(CategoryNode node) {
/* 258 */     this._tree.expandPath(getTreePath(node));
/*     */   }
/*     */ 
/*     */   protected TreePath getTreePath(CategoryNode node) {
/* 262 */     return new TreePath(node.getPath());
/*     */   }
/*     */ 
/*     */   protected void collapse(CategoryNode node) {
/* 266 */     this._tree.collapsePath(getTreePath(node));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor
 * JD-Core Version:    0.6.0
 */
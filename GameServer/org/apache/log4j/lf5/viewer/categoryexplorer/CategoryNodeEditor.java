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
/*  61 */     this._renderer = new CategoryNodeEditorRenderer();
/*  62 */     this._checkBox = this._renderer.getCheckBox();
/*  63 */     this._categoryModel = model;
/*     */ 
/*  65 */     this._checkBox.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  67 */         CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
/*  68 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*  72 */     this._renderer.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/*  74 */         if ((e.getModifiers() & 0x4) != 0) {
/*  75 */           CategoryNodeEditor.this.showPopup(CategoryNodeEditor.this._lastEditedNode, e.getX(), e.getY());
/*     */         }
/*  77 */         CategoryNodeEditor.this.stopCellEditing();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
/*     */   {
/*  89 */     this._lastEditedNode = ((CategoryNode)value);
/*  90 */     this._tree = tree;
/*     */ 
/*  92 */     return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
/*     */   }
/*     */ 
/*     */   public Object getCellEditorValue()
/*     */   {
/*  99 */     return this._lastEditedNode.getUserObject();
/*     */   }
/*     */ 
/*     */   protected JMenuItem createPropertiesMenuItem(CategoryNode node)
/*     */   {
/* 106 */     JMenuItem result = new JMenuItem("Properties");
/* 107 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 109 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.showPropertiesDialog(this.val$node);
/*     */       }
/*     */     });
/* 112 */     return result;
/*     */   }
/*     */ 
/*     */   protected void showPropertiesDialog(CategoryNode node) {
/* 116 */     JOptionPane.showMessageDialog(this._tree, getDisplayedProperties(node), "Category Properties: " + node.getTitle(), -1);
/*     */   }
/*     */ 
/*     */   protected Object getDisplayedProperties(CategoryNode node)
/*     */   {
/* 125 */     ArrayList result = new ArrayList();
/* 126 */     result.add("Category: " + node.getTitle());
/* 127 */     if (node.hasFatalRecords()) {
/* 128 */       result.add("Contains at least one fatal LogRecord.");
/*     */     }
/* 130 */     if (node.hasFatalChildren()) {
/* 131 */       result.add("Contains descendants with a fatal LogRecord.");
/*     */     }
/* 133 */     result.add("LogRecords in this category alone: " + node.getNumberOfContainedRecords());
/*     */ 
/* 135 */     result.add("LogRecords in descendant categories: " + node.getNumberOfRecordsFromChildren());
/*     */ 
/* 137 */     result.add("LogRecords in this category including descendants: " + node.getTotalNumberOfRecords());
/*     */ 
/* 139 */     return result.toArray();
/*     */   }
/*     */ 
/*     */   protected void showPopup(CategoryNode node, int x, int y) {
/* 143 */     JPopupMenu popup = new JPopupMenu();
/* 144 */     popup.setSize(150, 400);
/*     */ 
/* 148 */     if (node.getParent() == null) {
/* 149 */       popup.add(createRemoveMenuItem());
/* 150 */       popup.addSeparator();
/*     */     }
/* 152 */     popup.add(createSelectDescendantsMenuItem(node));
/* 153 */     popup.add(createUnselectDescendantsMenuItem(node));
/* 154 */     popup.addSeparator();
/* 155 */     popup.add(createExpandMenuItem(node));
/* 156 */     popup.add(createCollapseMenuItem(node));
/* 157 */     popup.addSeparator();
/* 158 */     popup.add(createPropertiesMenuItem(node));
/* 159 */     popup.show(this._renderer, x, y);
/*     */   }
/*     */ 
/*     */   protected JMenuItem createSelectDescendantsMenuItem(CategoryNode node) {
/* 163 */     JMenuItem selectDescendants = new JMenuItem("Select All Descendant Categories");
/*     */ 
/* 165 */     selectDescendants.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 168 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this._categoryModel.setDescendantSelection(this.val$node, true);
/*     */       }
/*     */     });
/* 172 */     return selectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createUnselectDescendantsMenuItem(CategoryNode node) {
/* 176 */     JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Categories");
/*     */ 
/* 178 */     unselectDescendants.addActionListener(new ActionListener(node) {
/*     */       private final CategoryNode val$node;
/*     */ 
/* 182 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this._categoryModel.setDescendantSelection(this.val$node, false);
/*     */       }
/*     */     });
/* 187 */     return unselectDescendants;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createExpandMenuItem(CategoryNode node) {
/* 191 */     JMenuItem result = new JMenuItem("Expand All Descendant Categories");
/* 192 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 194 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.expandDescendants(this.val$node);
/*     */       }
/*     */     });
/* 197 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createCollapseMenuItem(CategoryNode node) {
/* 201 */     JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
/* 202 */     result.addActionListener(new ActionListener(node) { private final CategoryNode val$node;
/*     */ 
/* 204 */       public void actionPerformed(ActionEvent e) { CategoryNodeEditor.this.collapseDescendants(this.val$node);
/*     */       }
/*     */     });
/* 207 */     return result;
/*     */   }
/*     */ 
/*     */   protected JMenuItem createRemoveMenuItem()
/*     */   {
/* 220 */     JMenuItem result = new JMenuItem("Remove All Empty Categories");
/* 221 */     result.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 223 */         while (CategoryNodeEditor.this.removeUnusedNodes() > 0);
/*     */       }
/*     */     });
/* 226 */     return result;
/*     */   }
/*     */ 
/*     */   protected void expandDescendants(CategoryNode node) {
/* 230 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 232 */     while (descendants.hasMoreElements()) {
/* 233 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 234 */       expand(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void collapseDescendants(CategoryNode node) {
/* 239 */     Enumeration descendants = node.depthFirstEnumeration();
/*     */ 
/* 241 */     while (descendants.hasMoreElements()) {
/* 242 */       CategoryNode current = (CategoryNode)descendants.nextElement();
/* 243 */       collapse(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int removeUnusedNodes()
/*     */   {
/* 251 */     int count = 0;
/* 252 */     CategoryNode root = this._categoryModel.getRootCategoryNode();
/* 253 */     Enumeration enumeration = root.depthFirstEnumeration();
/* 254 */     while (enumeration.hasMoreElements()) {
/* 255 */       CategoryNode node = (CategoryNode)enumeration.nextElement();
/* 256 */       if ((node.isLeaf()) && (node.getNumberOfContainedRecords() == 0) && (node.getParent() != null))
/*     */       {
/* 258 */         this._categoryModel.removeNodeFromParent(node);
/* 259 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 263 */     return count;
/*     */   }
/*     */ 
/*     */   protected void expand(CategoryNode node) {
/* 267 */     this._tree.expandPath(getTreePath(node));
/*     */   }
/*     */ 
/*     */   protected TreePath getTreePath(CategoryNode node) {
/* 271 */     return new TreePath(node.getPath());
/*     */   }
/*     */ 
/*     */   protected void collapse(CategoryNode node) {
/* 275 */     this._tree.collapsePath(getTreePath(node));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor
 * JD-Core Version:    0.6.0
 */
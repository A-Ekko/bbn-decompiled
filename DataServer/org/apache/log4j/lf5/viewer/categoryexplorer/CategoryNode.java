/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ 
/*     */ public class CategoryNode extends DefaultMutableTreeNode
/*     */ {
/*  31 */   protected boolean _selected = true;
/*  32 */   protected int _numberOfContainedRecords = 0;
/*  33 */   protected int _numberOfRecordsFromChildren = 0;
/*  34 */   protected boolean _hasFatalChildren = false;
/*  35 */   protected boolean _hasFatalRecords = false;
/*     */ 
/*     */   public CategoryNode(String title)
/*     */   {
/*  49 */     setUserObject(title);
/*     */   }
/*     */ 
/*     */   public String getTitle()
/*     */   {
/*  56 */     return (String)getUserObject();
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean s) {
/*  60 */     if (s != this._selected)
/*  61 */       this._selected = s;
/*     */   }
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/*  66 */     return this._selected;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsSelected()
/*     */   {
/*  73 */     Enumeration children = children();
/*  74 */     while (children.hasMoreElements()) {
/*  75 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  76 */       node.setSelected(true);
/*  77 */       node.setAllDescendantsSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setAllDescendantsDeSelected()
/*     */   {
/*  85 */     Enumeration children = children();
/*  86 */     while (children.hasMoreElements()) {
/*  87 */       CategoryNode node = (CategoryNode)children.nextElement();
/*  88 */       node.setSelected(false);
/*  89 */       node.setAllDescendantsDeSelected();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  94 */     return getTitle();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/*  98 */     if ((obj instanceof CategoryNode)) {
/*  99 */       CategoryNode node = (CategoryNode)obj;
/* 100 */       String tit1 = getTitle().toLowerCase();
/* 101 */       String tit2 = node.getTitle().toLowerCase();
/*     */ 
/* 103 */       if (tit1.equals(tit2)) {
/* 104 */         return true;
/*     */       }
/*     */     }
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 111 */     return getTitle().hashCode();
/*     */   }
/*     */ 
/*     */   public void addRecord() {
/* 115 */     this._numberOfContainedRecords += 1;
/* 116 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   public int getNumberOfContainedRecords() {
/* 120 */     return this._numberOfContainedRecords;
/*     */   }
/*     */ 
/*     */   public void resetNumberOfContainedRecords() {
/* 124 */     this._numberOfContainedRecords = 0;
/* 125 */     this._numberOfRecordsFromChildren = 0;
/* 126 */     this._hasFatalRecords = false;
/* 127 */     this._hasFatalChildren = false;
/*     */   }
/*     */ 
/*     */   public boolean hasFatalRecords() {
/* 131 */     return this._hasFatalRecords;
/*     */   }
/*     */ 
/*     */   public boolean hasFatalChildren() {
/* 135 */     return this._hasFatalChildren;
/*     */   }
/*     */ 
/*     */   public void setHasFatalRecords(boolean flag) {
/* 139 */     this._hasFatalRecords = flag;
/*     */   }
/*     */ 
/*     */   public void setHasFatalChildren(boolean flag) {
/* 143 */     this._hasFatalChildren = flag;
/*     */   }
/*     */ 
/*     */   protected int getTotalNumberOfRecords()
/*     */   {
/* 151 */     return getNumberOfRecordsFromChildren() + getNumberOfContainedRecords();
/*     */   }
/*     */ 
/*     */   protected void addRecordFromChild()
/*     */   {
/* 158 */     this._numberOfRecordsFromChildren += 1;
/* 159 */     addRecordToParent();
/*     */   }
/*     */ 
/*     */   protected int getNumberOfRecordsFromChildren() {
/* 163 */     return this._numberOfRecordsFromChildren;
/*     */   }
/*     */ 
/*     */   protected void addRecordToParent() {
/* 167 */     TreeNode parent = getParent();
/* 168 */     if (parent == null) {
/* 169 */       return;
/*     */     }
/* 171 */     ((CategoryNode)parent).addRecordFromChild();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNode
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ import org.apache.log4j.lf5.LogRecord;
/*    */ import org.apache.log4j.lf5.LogRecordFilter;
/*    */ 
/*    */ public class CategoryExplorerLogRecordFilter
/*    */   implements LogRecordFilter
/*    */ {
/*    */   protected CategoryExplorerModel _model;
/*    */ 
/*    */   public CategoryExplorerLogRecordFilter(CategoryExplorerModel model)
/*    */   {
/* 43 */     this._model = model;
/*    */   }
/*    */ 
/*    */   public boolean passes(LogRecord record)
/*    */   {
/* 57 */     CategoryPath path = new CategoryPath(record.getCategory());
/* 58 */     return this._model.isCategoryPathActive(path);
/*    */   }
/*    */ 
/*    */   public void reset()
/*    */   {
/* 65 */     resetAllNodes();
/*    */   }
/*    */ 
/*    */   protected void resetAllNodes()
/*    */   {
/* 73 */     Enumeration nodes = this._model.getRootCategoryNode().depthFirstEnumeration();
/*    */ 
/* 75 */     while (nodes.hasMoreElements()) {
/* 76 */       CategoryNode current = (CategoryNode)nodes.nextElement();
/* 77 */       current.resetNumberOfContainedRecords();
/* 78 */       this._model.nodeChanged(current);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerLogRecordFilter
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTree;
/*    */ 
/*    */ public class CategoryNodeEditorRenderer extends CategoryNodeRenderer
/*    */ {
/*    */   private static final long serialVersionUID = -6094804684259929574L;
/*    */ 
/*    */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*    */   {
/* 59 */     Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*    */ 
/* 63 */     return c;
/*    */   }
/*    */ 
/*    */   public JCheckBox getCheckBox() {
/* 67 */     return this._checkBox;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditorRenderer
 * JD-Core Version:    0.6.0
 */
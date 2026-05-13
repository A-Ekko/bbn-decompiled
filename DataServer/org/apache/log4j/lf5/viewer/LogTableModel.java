/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import javax.swing.table.DefaultTableModel;
/*    */ 
/*    */ public class LogTableModel extends DefaultTableModel
/*    */ {
/*    */   public LogTableModel(Object[] colNames, int numRows)
/*    */   {
/* 39 */     super(colNames, numRows);
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(int row, int column)
/*    */   {
/* 47 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTableModel
 * JD-Core Version:    0.6.0
 */
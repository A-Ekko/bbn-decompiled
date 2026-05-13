/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.util.Map;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ import org.apache.log4j.lf5.LogLevel;
/*    */ import org.apache.log4j.lf5.LogRecord;
/*    */ 
/*    */ public class LogTableRowRenderer extends DefaultTableCellRenderer
/*    */ {
/* 35 */   protected boolean _highlightFatal = true;
/* 36 */   protected Color _color = new Color(230, 230, 230);
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
/*    */   {
/* 57 */     if (row % 2 == 0)
/* 58 */       setBackground(this._color);
/*    */     else {
/* 60 */       setBackground(Color.white);
/*    */     }
/*    */ 
/* 63 */     FilteredLogTableModel model = (FilteredLogTableModel)table.getModel();
/* 64 */     LogRecord record = model.getFilteredRecord(row);
/*    */ 
/* 66 */     setForeground(getLogLevelColor(record.getLevel()));
/*    */ 
/* 68 */     return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
/*    */   }
/*    */ 
/*    */   protected Color getLogLevelColor(LogLevel level)
/*    */   {
/* 80 */     return (Color)LogLevel.getLogLevelColorMap().get(level);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogTableRowRenderer
 * JD-Core Version:    0.6.0
 */
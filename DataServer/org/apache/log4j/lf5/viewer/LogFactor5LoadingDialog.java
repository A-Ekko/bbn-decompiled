/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class LogFactor5LoadingDialog extends LogFactor5Dialog
/*    */ {
/*    */   public LogFactor5LoadingDialog(JFrame jframe, String message)
/*    */   {
/* 40 */     super(jframe, "LogFactor5", false);
/*    */ 
/* 42 */     JPanel bottom = new JPanel();
/* 43 */     bottom.setLayout(new FlowLayout());
/*    */ 
/* 45 */     JPanel main = new JPanel();
/* 46 */     main.setLayout(new GridBagLayout());
/* 47 */     wrapStringOnPanel(message, main);
/*    */ 
/* 49 */     getContentPane().add(main, "Center");
/* 50 */     getContentPane().add(bottom, "South");
/* 51 */     show();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog
 * JD-Core Version:    0.6.0
 */
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
/* 49 */     super(jframe, "LogFactor5", false);
/*    */ 
/* 51 */     JPanel bottom = new JPanel();
/* 52 */     bottom.setLayout(new FlowLayout());
/*    */ 
/* 54 */     JPanel main = new JPanel();
/* 55 */     main.setLayout(new GridBagLayout());
/* 56 */     wrapStringOnPanel(message, main);
/*    */ 
/* 58 */     getContentPane().add(main, "Center");
/* 59 */     getContentPane().add(bottom, "South");
/* 60 */     show();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog
 * JD-Core Version:    0.6.0
 */
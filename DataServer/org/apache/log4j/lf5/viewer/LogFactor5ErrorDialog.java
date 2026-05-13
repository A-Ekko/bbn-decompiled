/*    */ package org.apache.log4j.lf5.viewer;
/*    */ 
/*    */ import java.awt.Container;
/*    */ import java.awt.Dialog;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class LogFactor5ErrorDialog extends LogFactor5Dialog
/*    */ {
/*    */   public LogFactor5ErrorDialog(JFrame jframe, String message)
/*    */   {
/* 41 */     super(jframe, "Error", true);
/*    */ 
/* 43 */     JButton ok = new JButton("Ok");
/* 44 */     ok.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 46 */         LogFactor5ErrorDialog.this.hide();
/*    */       }
/*    */     });
/* 50 */     JPanel bottom = new JPanel();
/* 51 */     bottom.setLayout(new FlowLayout());
/* 52 */     bottom.add(ok);
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

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog
 * JD-Core Version:    0.6.0
 */
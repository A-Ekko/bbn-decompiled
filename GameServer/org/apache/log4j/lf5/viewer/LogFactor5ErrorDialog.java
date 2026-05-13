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
/* 50 */     super(jframe, "Error", true);
/*    */ 
/* 52 */     JButton ok = new JButton("Ok");
/* 53 */     ok.addActionListener(new ActionListener() {
/*    */       public void actionPerformed(ActionEvent e) {
/* 55 */         LogFactor5ErrorDialog.this.hide();
/*    */       }
/*    */     });
/* 59 */     JPanel bottom = new JPanel();
/* 60 */     bottom.setLayout(new FlowLayout());
/* 61 */     bottom.add(ok);
/*    */ 
/* 63 */     JPanel main = new JPanel();
/* 64 */     main.setLayout(new GridBagLayout());
/* 65 */     wrapStringOnPanel(message, main);
/*    */ 
/* 67 */     getContentPane().add(main, "Center");
/* 68 */     getContentPane().add(bottom, "South");
/* 69 */     show();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5ErrorDialog
 * JD-Core Version:    0.6.0
 */
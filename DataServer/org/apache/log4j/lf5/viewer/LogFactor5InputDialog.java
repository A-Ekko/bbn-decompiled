/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.KeyAdapter;
/*     */ import java.awt.event.KeyEvent;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.text.JTextComponent;
/*     */ 
/*     */ public class LogFactor5InputDialog extends LogFactor5Dialog
/*     */ {
/*     */   public static final int SIZE = 30;
/*     */   private JTextField _textField;
/*     */ 
/*     */   public LogFactor5InputDialog(JFrame jframe, String title, String label)
/*     */   {
/*  53 */     this(jframe, title, label, 30);
/*     */   }
/*     */ 
/*     */   public LogFactor5InputDialog(JFrame jframe, String title, String label, int size)
/*     */   {
/*  65 */     super(jframe, title, true);
/*     */ 
/*  67 */     JPanel bottom = new JPanel();
/*  68 */     bottom.setLayout(new FlowLayout());
/*     */ 
/*  70 */     JPanel main = new JPanel();
/*  71 */     main.setLayout(new FlowLayout());
/*  72 */     main.add(new JLabel(label));
/*  73 */     this._textField = new JTextField(size);
/*  74 */     main.add(this._textField);
/*     */ 
/*  76 */     addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/*  78 */         if (e.getKeyCode() == 10)
/*  79 */           LogFactor5InputDialog.this.hide();
/*     */       }
/*     */     });
/*  84 */     JButton ok = new JButton("Ok");
/*  85 */     ok.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  87 */         LogFactor5InputDialog.this.hide();
/*     */       }
/*     */     });
/*  91 */     JButton cancel = new JButton("Cancel");
/*  92 */     cancel.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/*  94 */         LogFactor5InputDialog.this.hide();
/*     */ 
/*  98 */         LogFactor5InputDialog.this._textField.setText("");
/*     */       }
/*     */     });
/* 102 */     bottom.add(ok);
/* 103 */     bottom.add(cancel);
/* 104 */     getContentPane().add(main, "Center");
/* 105 */     getContentPane().add(bottom, "South");
/* 106 */     pack();
/* 107 */     centerWindow(this);
/* 108 */     show();
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 115 */     String s = this._textField.getText();
/*     */ 
/* 117 */     if ((s != null) && (s.trim().length() == 0)) {
/* 118 */       return null;
/*     */     }
/*     */ 
/* 121 */     return s;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5InputDialog
 * JD-Core Version:    0.6.0
 */
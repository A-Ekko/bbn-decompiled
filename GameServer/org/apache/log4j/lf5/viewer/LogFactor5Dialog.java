/*     */ package org.apache.log4j.lf5.viewer;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dialog;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import javax.swing.JDialog;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public abstract class LogFactor5Dialog extends JDialog
/*     */ {
/*  35 */   protected static final Font DISPLAY_FONT = new Font("Arial", 1, 12);
/*     */ 
/*     */   protected LogFactor5Dialog(JFrame jframe, String message, boolean modal)
/*     */   {
/*  48 */     super(jframe, message, modal);
/*     */   }
/*     */ 
/*     */   public void show()
/*     */   {
/*  55 */     pack();
/*  56 */     minimumSizeDialog(this, 200, 100);
/*  57 */     centerWindow(this);
/*  58 */     super.show();
/*     */   }
/*     */ 
/*     */   protected void centerWindow(Window win)
/*     */   {
/*  69 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/*  72 */     if (screenDim.width < win.getSize().width) {
/*  73 */       win.setSize(screenDim.width, win.getSize().height);
/*     */     }
/*     */ 
/*  76 */     if (screenDim.height < win.getSize().height) {
/*  77 */       win.setSize(win.getSize().width, screenDim.height);
/*     */     }
/*     */ 
/*  81 */     int x = (screenDim.width - win.getSize().width) / 2;
/*  82 */     int y = (screenDim.height - win.getSize().height) / 2;
/*  83 */     win.setLocation(x, y);
/*     */   }
/*     */ 
/*     */   protected void wrapStringOnPanel(String message, Container container)
/*     */   {
/*  88 */     GridBagConstraints c = getDefaultConstraints();
/*  89 */     c.gridwidth = 0;
/*     */ 
/*  91 */     c.insets = new Insets(0, 0, 0, 0);
/*  92 */     GridBagLayout gbLayout = (GridBagLayout)container.getLayout();
/*     */ 
/*  95 */     while (message.length() > 0) {
/*  96 */       int newLineIndex = message.indexOf('\n');
/*     */       String line;
/*  98 */       if (newLineIndex >= 0) {
/*  99 */         String line = message.substring(0, newLineIndex);
/* 100 */         message = message.substring(newLineIndex + 1);
/*     */       } else {
/* 102 */         line = message;
/* 103 */         message = "";
/*     */       }
/* 105 */       Label label = new Label(line);
/* 106 */       label.setFont(DISPLAY_FONT);
/* 107 */       gbLayout.setConstraints(label, c);
/* 108 */       container.add(label);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected GridBagConstraints getDefaultConstraints() {
/* 113 */     GridBagConstraints constraints = new GridBagConstraints();
/* 114 */     constraints.weightx = 1.0D;
/* 115 */     constraints.weighty = 1.0D;
/* 116 */     constraints.gridheight = 1;
/*     */ 
/* 118 */     constraints.insets = new Insets(4, 4, 4, 4);
/*     */ 
/* 120 */     constraints.fill = 0;
/*     */ 
/* 122 */     constraints.anchor = 17;
/*     */ 
/* 124 */     return constraints;
/*     */   }
/*     */ 
/*     */   protected void minimumSizeDialog(Component component, int minWidth, int minHeight)
/*     */   {
/* 131 */     if (component.getSize().width < minWidth) {
/* 132 */       component.setSize(minWidth, component.getSize().height);
/*     */     }
/* 134 */     if (component.getSize().height < minHeight)
/* 135 */       component.setSize(component.getSize().width, minHeight);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5Dialog
 * JD-Core Version:    0.6.0
 */
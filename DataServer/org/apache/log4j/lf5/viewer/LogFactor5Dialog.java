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
/*  26 */   protected static final Font DISPLAY_FONT = new Font("Arial", 1, 12);
/*     */ 
/*     */   protected LogFactor5Dialog(JFrame jframe, String message, boolean modal)
/*     */   {
/*  39 */     super(jframe, message, modal);
/*     */   }
/*     */ 
/*     */   public void show()
/*     */   {
/*  46 */     pack();
/*  47 */     minimumSizeDialog(this, 200, 100);
/*  48 */     centerWindow(this);
/*  49 */     super.show();
/*     */   }
/*     */ 
/*     */   protected void centerWindow(Window win)
/*     */   {
/*  60 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/*     */ 
/*  63 */     if (screenDim.width < win.getSize().width) {
/*  64 */       win.setSize(screenDim.width, win.getSize().height);
/*     */     }
/*     */ 
/*  67 */     if (screenDim.height < win.getSize().height) {
/*  68 */       win.setSize(win.getSize().width, screenDim.height);
/*     */     }
/*     */ 
/*  72 */     int x = (screenDim.width - win.getSize().width) / 2;
/*  73 */     int y = (screenDim.height - win.getSize().height) / 2;
/*  74 */     win.setLocation(x, y);
/*     */   }
/*     */ 
/*     */   protected void wrapStringOnPanel(String message, Container container)
/*     */   {
/*  79 */     GridBagConstraints c = getDefaultConstraints();
/*  80 */     c.gridwidth = 0;
/*     */ 
/*  82 */     c.insets = new Insets(0, 0, 0, 0);
/*  83 */     GridBagLayout gbLayout = (GridBagLayout)container.getLayout();
/*     */ 
/*  86 */     while (message.length() > 0) {
/*  87 */       int newLineIndex = message.indexOf('\n');
/*     */       String line;
/*  89 */       if (newLineIndex >= 0) {
/*  90 */         line = message.substring(0, newLineIndex);
/*  91 */         message = message.substring(newLineIndex + 1);
/*     */       } else {
/*  93 */         line = message;
/*  94 */         message = "";
/*     */       }
/*  96 */       Label label = new Label(line);
/*  97 */       label.setFont(DISPLAY_FONT);
/*  98 */       gbLayout.setConstraints(label, c);
/*  99 */       container.add(label);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected GridBagConstraints getDefaultConstraints() {
/* 104 */     GridBagConstraints constraints = new GridBagConstraints();
/* 105 */     constraints.weightx = 1.0D;
/* 106 */     constraints.weighty = 1.0D;
/* 107 */     constraints.gridheight = 1;
/*     */ 
/* 109 */     constraints.insets = new Insets(4, 4, 4, 4);
/*     */ 
/* 111 */     constraints.fill = 0;
/*     */ 
/* 113 */     constraints.anchor = 17;
/*     */ 
/* 115 */     return constraints;
/*     */   }
/*     */ 
/*     */   protected void minimumSizeDialog(Component component, int minWidth, int minHeight)
/*     */   {
/* 122 */     if (component.getSize().width < minWidth) {
/* 123 */       component.setSize(minWidth, component.getSize().height);
/*     */     }
/* 125 */     if (component.getSize().height < minHeight)
/* 126 */       component.setSize(component.getSize().width, minHeight);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogFactor5Dialog
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.net.URL;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.tree.DefaultTreeCellRenderer;
/*     */ 
/*     */ public class CategoryNodeRenderer extends DefaultTreeCellRenderer
/*     */ {
/*  29 */   public static final Color FATAL_CHILDREN = new Color(189, 113, 0);
/*     */ 
/*  34 */   protected JCheckBox _checkBox = new JCheckBox();
/*  35 */   protected JPanel _panel = new JPanel();
/*  36 */   protected static ImageIcon _sat = null;
/*     */ 
/*     */   public CategoryNodeRenderer()
/*     */   {
/*  47 */     this._panel.setBackground(UIManager.getColor("Tree.textBackground"));
/*     */ 
/*  49 */     if (_sat == null)
/*     */     {
/*  51 */       String resource = "/org/apache/log4j/lf5/viewer/images/channelexplorer_satellite.gif";
/*     */ 
/*  53 */       URL satURL = getClass().getResource(resource);
/*     */ 
/*  55 */       _sat = new ImageIcon(satURL);
/*     */     }
/*     */ 
/*  58 */     setOpaque(false);
/*  59 */     this._checkBox.setOpaque(false);
/*  60 */     this._panel.setOpaque(false);
/*     */ 
/*  64 */     this._panel.setLayout(new FlowLayout(0, 0, 0));
/*  65 */     this._panel.add(this._checkBox);
/*  66 */     this._panel.add(this);
/*     */ 
/*  68 */     setOpenIcon(_sat);
/*  69 */     setClosedIcon(_sat);
/*  70 */     setLeafIcon(_sat);
/*     */   }
/*     */ 
/*     */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */   {
/*  82 */     CategoryNode node = (CategoryNode)value;
/*     */ 
/*  87 */     super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
/*     */ 
/*  91 */     if (row == 0)
/*     */     {
/*  93 */       this._checkBox.setVisible(false);
/*     */     } else {
/*  95 */       this._checkBox.setVisible(true);
/*  96 */       this._checkBox.setSelected(node.isSelected());
/*     */     }
/*  98 */     String toolTip = buildToolTip(node);
/*  99 */     this._panel.setToolTipText(toolTip);
/* 100 */     if (node.hasFatalChildren()) {
/* 101 */       setForeground(FATAL_CHILDREN);
/*     */     }
/* 103 */     if (node.hasFatalRecords()) {
/* 104 */       setForeground(Color.red);
/*     */     }
/*     */ 
/* 107 */     return this._panel;
/*     */   }
/*     */ 
/*     */   public Dimension getCheckBoxOffset() {
/* 111 */     return new Dimension(0, 0);
/*     */   }
/*     */ 
/*     */   protected String buildToolTip(CategoryNode node)
/*     */   {
/* 119 */     StringBuffer result = new StringBuffer();
/* 120 */     result.append(node.getTitle()).append(" contains a total of ");
/* 121 */     result.append(node.getTotalNumberOfRecords());
/* 122 */     result.append(" LogRecords.");
/* 123 */     result.append(" Right-click for more info.");
/* 124 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeRenderer
 * JD-Core Version:    0.6.0
 */
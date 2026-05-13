/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.Document;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ class ControlPanel extends JPanel
/*     */ {
/*  31 */   private static final Category LOG = Category.getInstance(ControlPanel.class);
/*     */ 
/*     */   ControlPanel(MyTableModel aModel)
/*     */   {
/*  40 */     setBorder(BorderFactory.createTitledBorder("Controls: "));
/*  41 */     GridBagLayout gridbag = new GridBagLayout();
/*  42 */     GridBagConstraints c = new GridBagConstraints();
/*  43 */     setLayout(gridbag);
/*     */ 
/*  46 */     c.ipadx = 5;
/*  47 */     c.ipady = 5;
/*     */ 
/*  50 */     c.gridx = 0;
/*  51 */     c.anchor = 13;
/*     */ 
/*  53 */     c.gridy = 0;
/*  54 */     JLabel label = new JLabel("Filter Level:");
/*  55 */     gridbag.setConstraints(label, c);
/*  56 */     add(label);
/*     */ 
/*  58 */     c.gridy += 1;
/*  59 */     label = new JLabel("Filter Thread:");
/*  60 */     gridbag.setConstraints(label, c);
/*  61 */     add(label);
/*     */ 
/*  63 */     c.gridy += 1;
/*  64 */     label = new JLabel("Filter Category:");
/*  65 */     gridbag.setConstraints(label, c);
/*  66 */     add(label);
/*     */ 
/*  68 */     c.gridy += 1;
/*  69 */     label = new JLabel("Filter NDC:");
/*  70 */     gridbag.setConstraints(label, c);
/*  71 */     add(label);
/*     */ 
/*  73 */     c.gridy += 1;
/*  74 */     label = new JLabel("Filter Message:");
/*  75 */     gridbag.setConstraints(label, c);
/*  76 */     add(label);
/*     */ 
/*  79 */     c.weightx = 1.0D;
/*     */ 
/*  81 */     c.gridx = 1;
/*  82 */     c.anchor = 17;
/*     */ 
/*  84 */     c.gridy = 0;
/*  85 */     Priority[] allPriorities = Priority.getAllPossiblePriorities();
/*  86 */     JComboBox priorities = new JComboBox(allPriorities);
/*  87 */     Priority lowest = allPriorities[(allPriorities.length - 1)];
/*  88 */     priorities.setSelectedItem(lowest);
/*  89 */     aModel.setPriorityFilter(lowest);
/*  90 */     gridbag.setConstraints(priorities, c);
/*  91 */     add(priorities);
/*  92 */     priorities.setEditable(false);
/*  93 */     priorities.addActionListener(new ActionListener(aModel, priorities) { private final MyTableModel val$aModel;
/*     */       private final JComboBox val$priorities;
/*     */ 
/*  95 */       public void actionPerformed(ActionEvent aEvent) { this.val$aModel.setPriorityFilter((Priority)this.val$priorities.getSelectedItem());
/*     */       }
/*     */     });
/* 101 */     c.fill = 2;
/* 102 */     c.gridy += 1;
/* 103 */     JTextField threadField = new JTextField("");
/* 104 */     threadField.getDocument().addDocumentListener(new DocumentListener(aModel, threadField) { private final MyTableModel val$aModel;
/*     */       private final JTextField val$threadField;
/*     */ 
/* 106 */       public void insertUpdate(DocumentEvent aEvent) { this.val$aModel.setThreadFilter(this.val$threadField.getText()); }
/*     */ 
/*     */       public void removeUpdate(DocumentEvent aEvente) {
/* 109 */         this.val$aModel.setThreadFilter(this.val$threadField.getText());
/*     */       }
/*     */       public void changedUpdate(DocumentEvent aEvent) {
/* 112 */         this.val$aModel.setThreadFilter(this.val$threadField.getText());
/*     */       }
/*     */     });
/* 115 */     gridbag.setConstraints(threadField, c);
/* 116 */     add(threadField);
/*     */ 
/* 118 */     c.gridy += 1;
/* 119 */     JTextField catField = new JTextField("");
/* 120 */     catField.getDocument().addDocumentListener(new DocumentListener(aModel, catField) { private final MyTableModel val$aModel;
/*     */       private final JTextField val$catField;
/*     */ 
/* 122 */       public void insertUpdate(DocumentEvent aEvent) { this.val$aModel.setCategoryFilter(this.val$catField.getText()); }
/*     */ 
/*     */       public void removeUpdate(DocumentEvent aEvent) {
/* 125 */         this.val$aModel.setCategoryFilter(this.val$catField.getText());
/*     */       }
/*     */       public void changedUpdate(DocumentEvent aEvent) {
/* 128 */         this.val$aModel.setCategoryFilter(this.val$catField.getText());
/*     */       }
/*     */     });
/* 131 */     gridbag.setConstraints(catField, c);
/* 132 */     add(catField);
/*     */ 
/* 134 */     c.gridy += 1;
/* 135 */     JTextField ndcField = new JTextField("");
/* 136 */     ndcField.getDocument().addDocumentListener(new DocumentListener(aModel, ndcField) { private final MyTableModel val$aModel;
/*     */       private final JTextField val$ndcField;
/*     */ 
/* 138 */       public void insertUpdate(DocumentEvent aEvent) { this.val$aModel.setNDCFilter(this.val$ndcField.getText()); }
/*     */ 
/*     */       public void removeUpdate(DocumentEvent aEvent) {
/* 141 */         this.val$aModel.setNDCFilter(this.val$ndcField.getText());
/*     */       }
/*     */       public void changedUpdate(DocumentEvent aEvent) {
/* 144 */         this.val$aModel.setNDCFilter(this.val$ndcField.getText());
/*     */       }
/*     */     });
/* 147 */     gridbag.setConstraints(ndcField, c);
/* 148 */     add(ndcField);
/*     */ 
/* 150 */     c.gridy += 1;
/* 151 */     JTextField msgField = new JTextField("");
/* 152 */     msgField.getDocument().addDocumentListener(new DocumentListener(aModel, msgField) { private final MyTableModel val$aModel;
/*     */       private final JTextField val$msgField;
/*     */ 
/* 154 */       public void insertUpdate(DocumentEvent aEvent) { this.val$aModel.setMessageFilter(this.val$msgField.getText()); }
/*     */ 
/*     */       public void removeUpdate(DocumentEvent aEvent) {
/* 157 */         this.val$aModel.setMessageFilter(this.val$msgField.getText());
/*     */       }
/*     */       public void changedUpdate(DocumentEvent aEvent) {
/* 160 */         this.val$aModel.setMessageFilter(this.val$msgField.getText());
/*     */       }
/*     */     });
/* 165 */     gridbag.setConstraints(msgField, c);
/* 166 */     add(msgField);
/*     */ 
/* 169 */     c.weightx = 0.0D;
/* 170 */     c.fill = 2;
/* 171 */     c.anchor = 13;
/* 172 */     c.gridx = 2;
/*     */ 
/* 174 */     c.gridy = 0;
/* 175 */     JButton exitButton = new JButton("Exit");
/* 176 */     exitButton.setMnemonic('x');
/* 177 */     exitButton.addActionListener(ExitAction.INSTANCE);
/* 178 */     gridbag.setConstraints(exitButton, c);
/* 179 */     add(exitButton);
/*     */ 
/* 181 */     c.gridy += 1;
/* 182 */     JButton clearButton = new JButton("Clear");
/* 183 */     clearButton.setMnemonic('c');
/* 184 */     clearButton.addActionListener(new ActionListener(aModel) { private final MyTableModel val$aModel;
/*     */ 
/* 186 */       public void actionPerformed(ActionEvent aEvent) { this.val$aModel.clear();
/*     */       }
/*     */     });
/* 189 */     gridbag.setConstraints(clearButton, c);
/* 190 */     add(clearButton);
/*     */ 
/* 192 */     c.gridy += 1;
/* 193 */     JButton toggleButton = new JButton("Pause");
/* 194 */     toggleButton.setMnemonic('p');
/* 195 */     toggleButton.addActionListener(new ActionListener(aModel, toggleButton) { private final MyTableModel val$aModel;
/*     */       private final JButton val$toggleButton;
/*     */ 
/* 197 */       public void actionPerformed(ActionEvent aEvent) { this.val$aModel.toggle();
/* 198 */         this.val$toggleButton.setText(this.val$aModel.isPaused() ? "Resume" : "Pause");
/*     */       }
/*     */     });
/* 202 */     gridbag.setConstraints(toggleButton, c);
/* 203 */     add(toggleButton);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.ControlPanel
 * JD-Core Version:    0.6.0
 */
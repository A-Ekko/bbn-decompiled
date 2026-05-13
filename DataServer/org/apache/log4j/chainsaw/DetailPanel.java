/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.text.Format;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Date;
/*     */ import java.util.EventObject;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JEditorPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.text.JTextComponent;
/*     */ import org.apache.log4j.Category;
/*     */ 
/*     */ class DetailPanel extends JPanel
/*     */   implements ListSelectionListener
/*     */ {
/*  32 */   private static final Category LOG = Category.getInstance(DetailPanel.class);
/*     */ 
/*  36 */   private static final MessageFormat FORMATTER = new MessageFormat("<b>Time:</b> <code>{0,time,medium}</code>&nbsp;&nbsp;<b>Priority:</b> <code>{1}</code>&nbsp;&nbsp;<b>Thread:</b> <code>{2}</code>&nbsp;&nbsp;<b>NDC:</b> <code>{3}</code><br><b>Category:</b> <code>{4}</code><br><b>Location:</b> <code>{5}</code><br><b>Message:</b><pre>{6}</pre><b>Throwable:</b><pre>{7}</pre>");
/*     */   private final MyTableModel mModel;
/*     */   private final JEditorPane mDetails;
/*     */ 
/*     */   DetailPanel(JTable aTable, MyTableModel aModel)
/*     */   {
/*  60 */     this.mModel = aModel;
/*  61 */     setLayout(new BorderLayout());
/*  62 */     setBorder(BorderFactory.createTitledBorder("Details: "));
/*     */ 
/*  64 */     this.mDetails = new JEditorPane();
/*  65 */     this.mDetails.setEditable(false);
/*  66 */     this.mDetails.setContentType("text/html");
/*  67 */     add(new JScrollPane(this.mDetails), "Center");
/*     */ 
/*  69 */     ListSelectionModel rowSM = aTable.getSelectionModel();
/*  70 */     rowSM.addListSelectionListener(this);
/*     */   }
/*     */ 
/*     */   public void valueChanged(ListSelectionEvent aEvent)
/*     */   {
/*  76 */     if (aEvent.getValueIsAdjusting()) {
/*  77 */       return;
/*     */     }
/*     */ 
/*  80 */     ListSelectionModel lsm = (ListSelectionModel)aEvent.getSource();
/*  81 */     if (lsm.isSelectionEmpty()) {
/*  82 */       this.mDetails.setText("Nothing selected");
/*     */     } else {
/*  84 */       int selectedRow = lsm.getMinSelectionIndex();
/*  85 */       EventDetails e = this.mModel.getEventDetails(selectedRow);
/*  86 */       Object[] args = { new Date(e.getTimeStamp()), e.getPriority(), escape(e.getThreadName()), escape(e.getNDC()), escape(e.getCategoryName()), escape(e.getLocationDetails()), escape(e.getMessage()), escape(getThrowableStrRep(e)) };
/*     */ 
/*  97 */       this.mDetails.setText(FORMATTER.format(args));
/*  98 */       this.mDetails.setCaretPosition(0);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getThrowableStrRep(EventDetails aEvent)
/*     */   {
/* 113 */     String[] strs = aEvent.getThrowableStrRep();
/* 114 */     if (strs == null) {
/* 115 */       return null;
/*     */     }
/*     */ 
/* 118 */     StringBuffer sb = new StringBuffer();
/* 119 */     for (int i = 0; i < strs.length; i++) {
/* 120 */       sb.append(strs[i]).append("\n");
/*     */     }
/*     */ 
/* 123 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private String escape(String aStr)
/*     */   {
/* 133 */     if (aStr == null) {
/* 134 */       return null;
/*     */     }
/*     */ 
/* 137 */     StringBuffer buf = new StringBuffer();
/* 138 */     for (int i = 0; i < aStr.length(); i++) {
/* 139 */       char c = aStr.charAt(i);
/* 140 */       switch (c) {
/*     */       case '<':
/* 142 */         buf.append("&lt;");
/* 143 */         break;
/*     */       case '>':
/* 145 */         buf.append("&gt;");
/* 146 */         break;
/*     */       case '"':
/* 148 */         buf.append("&quot;");
/* 149 */         break;
/*     */       case '&':
/* 151 */         buf.append("&amp;");
/* 152 */         break;
/*     */       default:
/* 154 */         buf.append(c);
/*     */       }
/*     */     }
/*     */ 
/* 158 */     return buf.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.DetailPanel
 * JD-Core Version:    0.6.0
 */
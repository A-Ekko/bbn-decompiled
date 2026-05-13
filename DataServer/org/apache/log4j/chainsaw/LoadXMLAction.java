/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.apache.log4j.Category;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ class LoadXMLAction extends AbstractAction
/*     */ {
/*  34 */   private static final Category LOG = Category.getInstance(LoadXMLAction.class);
/*     */   private final JFrame mParent;
/*  44 */   private final JFileChooser mChooser = new JFileChooser();
/*     */   private final XMLReader mParser;
/*     */   private final XMLFileHandler mHandler;
/*     */ 
/*     */   LoadXMLAction(JFrame aParent, MyTableModel aModel)
/*     */     throws SAXException, ParserConfigurationException
/*     */   {
/*  46 */     this.mChooser.setMultiSelectionEnabled(false);
/*  47 */     this.mChooser.setFileSelectionMode(0);
/*     */ 
/*  67 */     this.mParent = aParent;
/*  68 */     this.mHandler = new XMLFileHandler(aModel);
/*  69 */     this.mParser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
/*  70 */     this.mParser.setContentHandler(this.mHandler);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent aIgnore)
/*     */   {
/*  78 */     LOG.info("load file called");
/*  79 */     if (this.mChooser.showOpenDialog(this.mParent) == 0) {
/*  80 */       LOG.info("Need to load a file");
/*  81 */       File chosen = this.mChooser.getSelectedFile();
/*  82 */       LOG.info("loading the contents of " + chosen.getAbsolutePath());
/*     */       try {
/*  84 */         int num = loadFile(chosen.getAbsolutePath());
/*  85 */         JOptionPane.showMessageDialog(this.mParent, "Loaded " + num + " events.", "CHAINSAW", 1);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*  91 */         LOG.warn("caught an exception loading the file", e);
/*  92 */         JOptionPane.showMessageDialog(this.mParent, "Error parsing file - " + e.getMessage(), "CHAINSAW", 0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int loadFile(String aFile)
/*     */     throws SAXException, IOException
/*     */   {
/* 112 */     synchronized (this.mParser)
/*     */     {
/* 114 */       StringBuffer buf = new StringBuffer();
/* 115 */       buf.append("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
/* 116 */       buf.append("<!DOCTYPE log4j:eventSet ");
/* 117 */       buf.append("[<!ENTITY data SYSTEM \"file:///");
/* 118 */       buf.append(aFile);
/* 119 */       buf.append("\">]>\n");
/* 120 */       buf.append("<log4j:eventSet xmlns:log4j=\"Claira\">\n");
/* 121 */       buf.append("&data;\n");
/* 122 */       buf.append("</log4j:eventSet>\n");
/*     */ 
/* 124 */       InputSource is = new InputSource(new StringReader(buf.toString()));
/*     */ 
/* 126 */       this.mParser.parse(is);
/* 127 */       return this.mHandler.getNumEvents();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.LoadXMLAction
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.Component;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ import java.util.Properties;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTable;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ 
/*     */ public class Main extends JFrame
/*     */ {
/*     */   private static final int DEFAULT_PORT = 4445;
/*     */   public static final String PORT_PROP_NAME = "chainsaw.port";
/*  44 */   private static final Category LOG = Category.getInstance(Main.class);
/*     */ 
/*     */   private Main()
/*     */   {
/*  51 */     super("CHAINSAW - Log4J Log Viewer");
/*     */ 
/*  53 */     MyTableModel model = new MyTableModel();
/*     */ 
/*  56 */     JMenuBar menuBar = new JMenuBar();
/*  57 */     setJMenuBar(menuBar);
/*  58 */     JMenu menu = new JMenu("File");
/*  59 */     menuBar.add(menu);
/*     */     try
/*     */     {
/*  62 */       LoadXMLAction lxa = new LoadXMLAction(this, model);
/*  63 */       JMenuItem loadMenuItem = new JMenuItem("Load file...");
/*  64 */       menu.add(loadMenuItem);
/*  65 */       loadMenuItem.addActionListener(lxa);
/*     */     } catch (NoClassDefFoundError e) {
/*  67 */       LOG.info("Missing classes for XML parser", e);
/*  68 */       JOptionPane.showMessageDialog(this, "XML parser not in classpath - unable to load XML events.", "CHAINSAW", 0);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  74 */       LOG.info("Unable to create the action to load XML files", e);
/*  75 */       JOptionPane.showMessageDialog(this, "Unable to create a XML parser - unable to load XML events.", "CHAINSAW", 0);
/*     */     }
/*     */ 
/*  82 */     JMenuItem exitMenuItem = new JMenuItem("Exit");
/*  83 */     menu.add(exitMenuItem);
/*  84 */     exitMenuItem.addActionListener(ExitAction.INSTANCE);
/*     */ 
/*  87 */     ControlPanel cp = new ControlPanel(model);
/*  88 */     getContentPane().add(cp, "North");
/*     */ 
/*  91 */     JTable table = new JTable(model);
/*  92 */     table.setSelectionMode(0);
/*  93 */     JScrollPane scrollPane = new JScrollPane(table);
/*  94 */     scrollPane.setBorder(BorderFactory.createTitledBorder("Events: "));
/*  95 */     scrollPane.setPreferredSize(new Dimension(900, 300));
/*     */ 
/*  98 */     JPanel details = new DetailPanel(table, model);
/*  99 */     details.setPreferredSize(new Dimension(900, 300));
/*     */ 
/* 102 */     JSplitPane jsp = new JSplitPane(0, scrollPane, details);
/*     */ 
/* 104 */     getContentPane().add(jsp, "Center");
/*     */ 
/* 106 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent aEvent) {
/* 108 */         ExitAction.INSTANCE.actionPerformed(null);
/*     */       }
/*     */     });
/* 112 */     pack();
/* 113 */     setVisible(true);
/*     */ 
/* 115 */     setupReceiver(model);
/*     */   }
/*     */ 
/*     */   private void setupReceiver(MyTableModel aModel)
/*     */   {
/* 124 */     int port = 4445;
/* 125 */     String strRep = System.getProperty("chainsaw.port");
/* 126 */     if (strRep != null) {
/*     */       try {
/* 128 */         port = Integer.parseInt(strRep);
/*     */       } catch (NumberFormatException nfe) {
/* 130 */         LOG.fatal("Unable to parse chainsaw.port property with value " + strRep + ".");
/*     */ 
/* 132 */         JOptionPane.showMessageDialog(this, "Unable to parse port number from '" + strRep + "', quitting.", "CHAINSAW", 0);
/*     */ 
/* 138 */         System.exit(1);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 143 */       LoggingReceiver lr = new LoggingReceiver(aModel, port);
/* 144 */       lr.start();
/*     */     } catch (IOException e) {
/* 146 */       LOG.fatal("Unable to connect to socket server, quiting", e);
/* 147 */       JOptionPane.showMessageDialog(this, "Unable to create socket on port " + port + ", quitting.", "CHAINSAW", 0);
/*     */ 
/* 152 */       System.exit(1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initLog4J()
/*     */   {
/* 164 */     Properties props = new Properties();
/* 165 */     props.setProperty("log4j.rootCategory", "DEBUG, A1");
/* 166 */     props.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
/*     */ 
/* 168 */     props.setProperty("log4j.appender.A1.layout", "org.apache.log4j.TTCCLayout");
/*     */ 
/* 170 */     PropertyConfigurator.configure(props);
/*     */   }
/*     */ 
/*     */   public static void main(String[] aArgs)
/*     */   {
/* 179 */     initLog4J();
/* 180 */     new Main();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.Main
 * JD-Core Version:    0.6.0
 */
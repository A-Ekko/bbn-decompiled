/*      */ package org.apache.log4j.lf5.viewer;
/*      */ 
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.EventObject;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.Vector;
/*      */ import javax.swing.AbstractButton;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBoxMenuItem;
/*      */ import javax.swing.JColorChooser;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JFileChooser;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.JScrollBar;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSplitPane;
/*      */ import javax.swing.JTable;
/*      */ import javax.swing.JTextArea;
/*      */ import javax.swing.JToolBar;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.text.JTextComponent;
/*      */ import org.apache.log4j.lf5.LogLevel;
/*      */ import org.apache.log4j.lf5.LogRecord;
/*      */ import org.apache.log4j.lf5.LogRecordFilter;
/*      */ import org.apache.log4j.lf5.util.DateFormatManager;
/*      */ import org.apache.log4j.lf5.util.LogFileParser;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
/*      */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
/*      */ import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
/*      */ import org.apache.log4j.lf5.viewer.configure.MRUFileManager;
/*      */ 
/*      */ public class LogBrokerMonitor
/*      */ {
/*      */   public static final String DETAILED_VIEW = "Detailed";
/*      */   protected JFrame _logMonitorFrame;
/*   69 */   protected int _logMonitorFrameWidth = 550;
/*   70 */   protected int _logMonitorFrameHeight = 500;
/*      */   protected LogTable _table;
/*      */   protected CategoryExplorerTree _categoryExplorerTree;
/*      */   protected String _searchText;
/*   74 */   protected String _NDCTextFilter = "";
/*   75 */   protected LogLevel _leastSevereDisplayedLogLevel = LogLevel.DEBUG;
/*      */   protected JScrollPane _logTableScrollPane;
/*      */   protected JLabel _statusLabel;
/*   79 */   protected Object _lock = new Object();
/*      */   protected JComboBox _fontSizeCombo;
/*   82 */   protected int _fontSize = 10;
/*   83 */   protected String _fontName = "Dialog";
/*   84 */   protected String _currentView = "Detailed";
/*      */ 
/*   86 */   protected boolean _loadSystemFonts = false;
/*   87 */   protected boolean _trackTableScrollPane = true;
/*      */   protected Dimension _lastTableViewportSize;
/*   89 */   protected boolean _callSystemExitOnClose = false;
/*   90 */   protected List _displayedLogBrokerProperties = new Vector();
/*      */ 
/*   92 */   protected Map _logLevelMenuItems = new HashMap();
/*   93 */   protected Map _logTableColumnMenuItems = new HashMap();
/*      */ 
/*   95 */   protected List _levels = null;
/*   96 */   protected List _columns = null;
/*   97 */   protected boolean _isDisposed = false;
/*      */ 
/*   99 */   protected ConfigurationManager _configurationManager = null;
/*  100 */   protected MRUFileManager _mruFileManager = null;
/*  101 */   protected File _fileLocation = null;
/*      */ 
/*      */   public LogBrokerMonitor(List logLevels)
/*      */   {
/*  116 */     this._levels = logLevels;
/*  117 */     this._columns = LogTableColumn.getLogTableColumns();
/*      */ 
/*  121 */     String callSystemExitOnClose = System.getProperty("monitor.exit");
/*      */ 
/*  123 */     if (callSystemExitOnClose == null) {
/*  124 */       callSystemExitOnClose = "false";
/*      */     }
/*  126 */     callSystemExitOnClose = callSystemExitOnClose.trim().toLowerCase();
/*      */ 
/*  128 */     if (callSystemExitOnClose.equals("true")) {
/*  129 */       this._callSystemExitOnClose = true;
/*      */     }
/*      */ 
/*  132 */     initComponents();
/*      */ 
/*  135 */     this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
/*      */   }
/*      */ 
/*      */   public void show(int delay)
/*      */   {
/*  149 */     if (this._logMonitorFrame.isVisible()) {
/*  150 */       return;
/*      */     }
/*      */ 
/*  153 */     SwingUtilities.invokeLater(new Runnable(delay) { private final int val$delay;
/*      */ 
/*  155 */       public void run() { Thread.yield();
/*  156 */         LogBrokerMonitor.this.pause(this.val$delay);
/*  157 */         LogBrokerMonitor.this._logMonitorFrame.setVisible(true); } } );
/*      */   }
/*      */ 
/*      */   public void show()
/*      */   {
/*  163 */     show(0);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  170 */     this._logMonitorFrame.dispose();
/*  171 */     this._isDisposed = true;
/*      */ 
/*  173 */     if (this._callSystemExitOnClose == true)
/*  174 */       System.exit(0);
/*      */   }
/*      */ 
/*      */   public void hide()
/*      */   {
/*  182 */     this._logMonitorFrame.setVisible(false);
/*      */   }
/*      */ 
/*      */   public DateFormatManager getDateFormatManager()
/*      */   {
/*  189 */     return this._table.getDateFormatManager();
/*      */   }
/*      */ 
/*      */   public void setDateFormatManager(DateFormatManager dfm)
/*      */   {
/*  196 */     this._table.setDateFormatManager(dfm);
/*      */   }
/*      */ 
/*      */   public boolean getCallSystemExitOnClose()
/*      */   {
/*  204 */     return this._callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*      */   {
/*  212 */     this._callSystemExitOnClose = callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void addMessage(LogRecord lr)
/*      */   {
/*  221 */     if (this._isDisposed == true)
/*      */     {
/*  224 */       return;
/*      */     }
/*      */ 
/*  227 */     SwingUtilities.invokeLater(new Runnable(lr) { private final LogRecord val$lr;
/*      */ 
/*  229 */       public void run() { LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().addLogRecord(this.val$lr);
/*  230 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().addLogRecord(this.val$lr);
/*  231 */         LogBrokerMonitor.this.updateStatusLabel(); } } );
/*      */   }
/*      */ 
/*      */   public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords)
/*      */   {
/*  237 */     this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
/*      */   }
/*      */ 
/*      */   public JFrame getBaseFrame() {
/*  241 */     return this._logMonitorFrame;
/*      */   }
/*      */ 
/*      */   public void setTitle(String title) {
/*  245 */     this._logMonitorFrame.setTitle(title + " - LogFactor5");
/*      */   }
/*      */ 
/*      */   public void setFrameSize(int width, int height) {
/*  249 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  250 */     if ((0 < width) && (width < screen.width)) {
/*  251 */       this._logMonitorFrameWidth = width;
/*      */     }
/*  253 */     if ((0 < height) && (height < screen.height)) {
/*  254 */       this._logMonitorFrameHeight = height;
/*      */     }
/*  256 */     updateFrameSize();
/*      */   }
/*      */ 
/*      */   public void setFontSize(int fontSize) {
/*  260 */     changeFontSizeCombo(this._fontSizeCombo, fontSize);
/*      */   }
/*      */ 
/*      */   public void addDisplayedProperty(Object messageLine)
/*      */   {
/*  266 */     this._displayedLogBrokerProperties.add(messageLine);
/*      */   }
/*      */ 
/*      */   public Map getLogLevelMenuItems() {
/*  270 */     return this._logLevelMenuItems;
/*      */   }
/*      */ 
/*      */   public Map getLogTableColumnMenuItems() {
/*  274 */     return this._logTableColumnMenuItems;
/*      */   }
/*      */ 
/*      */   public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
/*  278 */     return getLogTableColumnMenuItem(column);
/*      */   }
/*      */ 
/*      */   public CategoryExplorerTree getCategoryExplorerTree() {
/*  282 */     return this._categoryExplorerTree;
/*      */   }
/*      */ 
/*      */   public String getNDCTextFilter()
/*      */   {
/*  288 */     return this._NDCTextFilter;
/*      */   }
/*      */ 
/*      */   public void setNDCLogRecordFilter(String textFilter)
/*      */   {
/*  295 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(textFilter));
/*      */   }
/*      */ 
/*      */   protected void setSearchText(String text)
/*      */   {
/*  303 */     this._searchText = text;
/*      */   }
/*      */ 
/*      */   protected void setNDCTextFilter(String text)
/*      */   {
/*  310 */     if (text == null)
/*  311 */       this._NDCTextFilter = "";
/*      */     else
/*  313 */       this._NDCTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void sortByNDC()
/*      */   {
/*  321 */     String text = this._NDCTextFilter;
/*  322 */     if ((text == null) || (text.length() == 0)) {
/*  323 */       return;
/*      */     }
/*      */ 
/*  327 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(text));
/*      */   }
/*      */ 
/*      */   protected void findSearchText()
/*      */   {
/*  332 */     String text = this._searchText;
/*  333 */     if ((text == null) || (text.length() == 0)) {
/*  334 */       return;
/*      */     }
/*  336 */     int startRow = getFirstSelectedRow();
/*  337 */     int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());
/*      */ 
/*  342 */     selectRow(foundRow);
/*      */   }
/*      */ 
/*      */   protected int getFirstSelectedRow() {
/*  346 */     return this._table.getSelectionModel().getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   protected void selectRow(int foundRow) {
/*  350 */     if (foundRow == -1) {
/*  351 */       String message = this._searchText + " not found.";
/*  352 */       JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);
/*      */ 
/*  358 */       return;
/*      */     }
/*  360 */     LF5SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
/*      */   }
/*      */ 
/*      */   protected int findRecord(int startRow, String searchText, List records)
/*      */   {
/*  368 */     if (startRow < 0)
/*  369 */       startRow = 0;
/*      */     else {
/*  371 */       startRow++;
/*      */     }
/*  373 */     int len = records.size();
/*      */ 
/*  375 */     for (int i = startRow; i < len; i++) {
/*  376 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  377 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  381 */     len = startRow;
/*  382 */     for (int i = 0; i < len; i++) {
/*  383 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  384 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  388 */     return -1;
/*      */   }
/*      */ 
/*      */   protected boolean matches(LogRecord record, String text)
/*      */   {
/*  396 */     String message = record.getMessage();
/*  397 */     String NDC = record.getNDC();
/*      */ 
/*  399 */     if (((message == null) && (NDC == null)) || (text == null)) {
/*  400 */       return false;
/*      */     }
/*      */ 
/*  404 */     return (message.toLowerCase().indexOf(text.toLowerCase()) != -1) || (NDC.toLowerCase().indexOf(text.toLowerCase()) != -1);
/*      */   }
/*      */ 
/*      */   protected void refresh(JTextArea textArea)
/*      */   {
/*  416 */     String text = textArea.getText();
/*  417 */     textArea.setText("");
/*  418 */     textArea.setText(text);
/*      */   }
/*      */ 
/*      */   protected void refreshDetailTextArea() {
/*  422 */     refresh(this._table._detailTextArea);
/*      */   }
/*      */ 
/*      */   protected void clearDetailTextArea() {
/*  426 */     this._table._detailTextArea.setText("");
/*      */   }
/*      */ 
/*      */   protected int changeFontSizeCombo(JComboBox box, int requestedSize)
/*      */   {
/*  435 */     int len = box.getItemCount();
/*      */ 
/*  438 */     Object selectedObject = box.getItemAt(0);
/*  439 */     int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
/*  440 */     for (int i = 0; i < len; i++) {
/*  441 */       Object currentObject = box.getItemAt(i);
/*  442 */       int currentValue = Integer.parseInt(String.valueOf(currentObject));
/*  443 */       if ((selectedValue < currentValue) && (currentValue <= requestedSize)) {
/*  444 */         selectedValue = currentValue;
/*  445 */         selectedObject = currentObject;
/*      */       }
/*      */     }
/*  448 */     box.setSelectedItem(selectedObject);
/*  449 */     return selectedValue;
/*      */   }
/*      */ 
/*      */   protected void setFontSizeSilently(int fontSize)
/*      */   {
/*  456 */     this._fontSize = fontSize;
/*  457 */     setFontSize(this._table._detailTextArea, fontSize);
/*  458 */     selectRow(0);
/*  459 */     setFontSize(this._table, fontSize);
/*      */   }
/*      */ 
/*      */   protected void setFontSize(Component component, int fontSize) {
/*  463 */     Font oldFont = component.getFont();
/*  464 */     Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);
/*      */ 
/*  466 */     component.setFont(newFont);
/*      */   }
/*      */ 
/*      */   protected void updateFrameSize() {
/*  470 */     this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
/*  471 */     centerFrame(this._logMonitorFrame);
/*      */   }
/*      */ 
/*      */   protected void pause(int millis) {
/*      */     try {
/*  476 */       Thread.sleep(millis);
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initComponents()
/*      */   {
/*  486 */     this._logMonitorFrame = new JFrame("LogFactor5");
/*      */ 
/*  488 */     this._logMonitorFrame.setDefaultCloseOperation(0);
/*      */ 
/*  490 */     String resource = "/org/apache/log4j/lf5/viewer/images/lf5_small_icon.gif";
/*      */ 
/*  492 */     URL lf5IconURL = getClass().getResource(resource);
/*      */ 
/*  494 */     if (lf5IconURL != null) {
/*  495 */       this._logMonitorFrame.setIconImage(new ImageIcon(lf5IconURL).getImage());
/*      */     }
/*  497 */     updateFrameSize();
/*      */ 
/*  502 */     JTextArea detailTA = createDetailTextArea();
/*  503 */     JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
/*  504 */     this._table = new LogTable(detailTA);
/*  505 */     setView(this._currentView, this._table);
/*  506 */     this._table.setFont(new Font(this._fontName, 0, this._fontSize));
/*  507 */     this._logTableScrollPane = new JScrollPane(this._table);
/*      */ 
/*  509 */     if (this._trackTableScrollPane) {
/*  510 */       this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
/*      */     }
/*      */ 
/*  519 */     JSplitPane tableViewerSplitPane = new JSplitPane();
/*  520 */     tableViewerSplitPane.setOneTouchExpandable(true);
/*  521 */     tableViewerSplitPane.setOrientation(0);
/*  522 */     tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
/*  523 */     tableViewerSplitPane.setRightComponent(detailTAScrollPane);
/*      */ 
/*  531 */     tableViewerSplitPane.setDividerLocation(350);
/*      */ 
/*  537 */     this._categoryExplorerTree = new CategoryExplorerTree();
/*      */ 
/*  539 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());
/*      */ 
/*  541 */     JScrollPane categoryExplorerTreeScrollPane = new JScrollPane(this._categoryExplorerTree);
/*      */ 
/*  543 */     categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));
/*      */ 
/*  546 */     this._mruFileManager = new MRUFileManager();
/*      */ 
/*  552 */     JSplitPane splitPane = new JSplitPane();
/*  553 */     splitPane.setOneTouchExpandable(true);
/*  554 */     splitPane.setRightComponent(tableViewerSplitPane);
/*  555 */     splitPane.setLeftComponent(categoryExplorerTreeScrollPane);
/*      */ 
/*  557 */     splitPane.setDividerLocation(130);
/*      */ 
/*  562 */     this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
/*  563 */     this._logMonitorFrame.getContentPane().add(splitPane, "Center");
/*  564 */     this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
/*      */ 
/*  566 */     this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");
/*      */ 
/*  569 */     makeLogTableListenToCategoryExplorer();
/*  570 */     addTableModelProperties();
/*      */ 
/*  575 */     this._configurationManager = new ConfigurationManager(this, this._table);
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createLogRecordFilter()
/*      */   {
/*  580 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  582 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  583 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  588 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createNDCLogRecordFilter(String text)
/*      */   {
/*  594 */     this._NDCTextFilter = text;
/*  595 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  597 */         String NDC = record.getNDC();
/*  598 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  599 */         if ((NDC == null) || (LogBrokerMonitor.this._NDCTextFilter == null))
/*  600 */           return false;
/*  601 */         if (NDC.toLowerCase().indexOf(LogBrokerMonitor.this._NDCTextFilter.toLowerCase()) == -1) {
/*  602 */           return false;
/*      */         }
/*  604 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  610 */     return result;
/*      */   }
/*      */ 
/*      */   protected void updateStatusLabel()
/*      */   {
/*  615 */     this._statusLabel.setText(getRecordsDisplayedMessage());
/*      */   }
/*      */ 
/*      */   protected String getRecordsDisplayedMessage() {
/*  619 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*  620 */     return getStatusText(model.getRowCount(), model.getTotalRowCount());
/*      */   }
/*      */ 
/*      */   protected void addTableModelProperties() {
/*  624 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*      */ 
/*  626 */     addDisplayedProperty(new Object() {
/*      */       public String toString() {
/*  628 */         return LogBrokerMonitor.this.getRecordsDisplayedMessage();
/*      */       }
/*      */     });
/*  631 */     addDisplayedProperty(new Object(model) { private final FilteredLogTableModel val$model;
/*      */ 
/*  633 */       public String toString() { return "Maximum number of displayed LogRecords: " + this.val$model._maxNumberOfLogRecords; }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected String getStatusText(int displayedRows, int totalRows)
/*      */   {
/*  640 */     StringBuffer result = new StringBuffer();
/*  641 */     result.append("Displaying: ");
/*  642 */     result.append(displayedRows);
/*  643 */     result.append(" records out of a total of: ");
/*  644 */     result.append(totalRows);
/*  645 */     result.append(" records.");
/*  646 */     return result.toString();
/*      */   }
/*      */ 
/*      */   protected void makeLogTableListenToCategoryExplorer() {
/*  650 */     ActionListener listener = new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  652 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  653 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     };
/*  656 */     this._categoryExplorerTree.getExplorerModel().addActionListener(listener);
/*      */   }
/*      */ 
/*      */   protected JPanel createStatusArea() {
/*  660 */     JPanel statusArea = new JPanel();
/*  661 */     JLabel status = new JLabel("No log records to display.");
/*      */ 
/*  663 */     this._statusLabel = status;
/*  664 */     status.setHorizontalAlignment(2);
/*      */ 
/*  666 */     statusArea.setBorder(BorderFactory.createEtchedBorder());
/*  667 */     statusArea.setLayout(new FlowLayout(0, 0, 0));
/*  668 */     statusArea.add(status);
/*      */ 
/*  670 */     return statusArea;
/*      */   }
/*      */ 
/*      */   protected JTextArea createDetailTextArea() {
/*  674 */     JTextArea detailTA = new JTextArea();
/*  675 */     detailTA.setFont(new Font("Monospaced", 0, 14));
/*  676 */     detailTA.setTabSize(3);
/*  677 */     detailTA.setLineWrap(true);
/*  678 */     detailTA.setWrapStyleWord(false);
/*  679 */     return detailTA;
/*      */   }
/*      */ 
/*      */   protected JMenuBar createMenuBar() {
/*  683 */     JMenuBar menuBar = new JMenuBar();
/*  684 */     menuBar.add(createFileMenu());
/*  685 */     menuBar.add(createEditMenu());
/*  686 */     menuBar.add(createLogLevelMenu());
/*  687 */     menuBar.add(createViewMenu());
/*  688 */     menuBar.add(createConfigureMenu());
/*  689 */     menuBar.add(createHelpMenu());
/*      */ 
/*  691 */     return menuBar;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelMenu() {
/*  695 */     JMenu result = new JMenu("Log Level");
/*  696 */     result.setMnemonic('l');
/*  697 */     Iterator levels = getLogLevels();
/*  698 */     while (levels.hasNext()) {
/*  699 */       result.add(getMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  702 */     result.addSeparator();
/*  703 */     result.add(createAllLogLevelsMenuItem());
/*  704 */     result.add(createNoLogLevelsMenuItem());
/*  705 */     result.addSeparator();
/*  706 */     result.add(createLogLevelColorMenu());
/*  707 */     result.add(createResetLogLevelColorMenuItem());
/*      */ 
/*  709 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogLevelsMenuItem() {
/*  713 */     JMenuItem result = new JMenuItem("Show all LogLevels");
/*  714 */     result.setMnemonic('s');
/*  715 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  717 */         LogBrokerMonitor.this.selectAllLogLevels(true);
/*  718 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  719 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  722 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogLevelsMenuItem() {
/*  726 */     JMenuItem result = new JMenuItem("Hide all LogLevels");
/*  727 */     result.setMnemonic('h');
/*  728 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  730 */         LogBrokerMonitor.this.selectAllLogLevels(false);
/*  731 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  732 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  735 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelColorMenu() {
/*  739 */     JMenu colorMenu = new JMenu("Configure LogLevel Colors");
/*  740 */     colorMenu.setMnemonic('c');
/*  741 */     Iterator levels = getLogLevels();
/*  742 */     while (levels.hasNext()) {
/*  743 */       colorMenu.add(createSubMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  746 */     return colorMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createResetLogLevelColorMenuItem() {
/*  750 */     JMenuItem result = new JMenuItem("Reset LogLevel Colors");
/*  751 */     result.setMnemonic('r');
/*  752 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  755 */         LogLevel.resetLogLevelColorMap();
/*      */ 
/*  758 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*      */       }
/*      */     });
/*  761 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogLevels(boolean selected) {
/*  765 */     Iterator levels = getLogLevels();
/*  766 */     while (levels.hasNext())
/*  767 */       getMenuItem((LogLevel)levels.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getMenuItem(LogLevel level)
/*      */   {
/*  772 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)(JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
/*  773 */     if (result == null) {
/*  774 */       result = createMenuItem(level);
/*  775 */       this._logLevelMenuItems.put(level, result);
/*      */     }
/*  777 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSubMenuItem(LogLevel level) {
/*  781 */     JMenuItem result = new JMenuItem(level.toString());
/*  782 */     LogLevel logLevel = level;
/*  783 */     result.setMnemonic(level.toString().charAt(0));
/*  784 */     result.addActionListener(new ActionListener(result, logLevel) { private final JMenuItem val$result;
/*      */       private final LogLevel val$logLevel;
/*      */ 
/*  786 */       public void actionPerformed(ActionEvent e) { LogBrokerMonitor.this.showLogLevelColorChangeDialog(this.val$result, this.val$logLevel);
/*      */       }
/*      */     });
/*  790 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showLogLevelColorChangeDialog(JMenuItem result, LogLevel level)
/*      */   {
/*  795 */     JMenuItem menuItem = result;
/*  796 */     Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose LogLevel Color", result.getForeground());
/*      */ 
/*  801 */     if (newColor != null)
/*      */     {
/*  803 */       level.setLogLevelColorMap(level, newColor);
/*  804 */       this._table.getFilteredLogTableModel().refresh();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createMenuItem(LogLevel level)
/*      */   {
/*  810 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
/*  811 */     result.setSelected(true);
/*  812 */     result.setMnemonic(level.toString().charAt(0));
/*  813 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  815 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  816 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  819 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createViewMenu()
/*      */   {
/*  824 */     JMenu result = new JMenu("View");
/*  825 */     result.setMnemonic('v');
/*  826 */     Iterator columns = getLogTableColumns();
/*  827 */     while (columns.hasNext()) {
/*  828 */       result.add(getLogTableColumnMenuItem((LogTableColumn)columns.next()));
/*      */     }
/*      */ 
/*  831 */     result.addSeparator();
/*  832 */     result.add(createAllLogTableColumnsMenuItem());
/*  833 */     result.add(createNoLogTableColumnsMenuItem());
/*  834 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
/*  838 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)(JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
/*  839 */     if (result == null) {
/*  840 */       result = createLogTableColumnMenuItem(column);
/*  841 */       this._logTableColumnMenuItems.put(column, result);
/*      */     }
/*  843 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
/*  847 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
/*      */ 
/*  849 */     result.setSelected(true);
/*  850 */     result.setMnemonic(column.toString().charAt(0));
/*  851 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  854 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  855 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  858 */     return result;
/*      */   }
/*      */ 
/*      */   protected List updateView() {
/*  862 */     ArrayList updatedList = new ArrayList();
/*  863 */     Iterator columnIterator = this._columns.iterator();
/*  864 */     while (columnIterator.hasNext()) {
/*  865 */       LogTableColumn column = (LogTableColumn)columnIterator.next();
/*  866 */       JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
/*      */ 
/*  868 */       if (result.isSelected()) {
/*  869 */         updatedList.add(column);
/*      */       }
/*      */     }
/*      */ 
/*  873 */     return updatedList;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogTableColumnsMenuItem() {
/*  877 */     JMenuItem result = new JMenuItem("Show all Columns");
/*  878 */     result.setMnemonic('s');
/*  879 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  881 */         LogBrokerMonitor.this.selectAllLogTableColumns(true);
/*      */ 
/*  883 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  884 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  887 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogTableColumnsMenuItem() {
/*  891 */     JMenuItem result = new JMenuItem("Hide all Columns");
/*  892 */     result.setMnemonic('h');
/*  893 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  895 */         LogBrokerMonitor.this.selectAllLogTableColumns(false);
/*      */ 
/*  897 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  898 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  901 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogTableColumns(boolean selected) {
/*  905 */     Iterator columns = getLogTableColumns();
/*  906 */     while (columns.hasNext())
/*  907 */       getLogTableColumnMenuItem((LogTableColumn)columns.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JMenu createFileMenu()
/*      */   {
/*  912 */     JMenu fileMenu = new JMenu("File");
/*  913 */     fileMenu.setMnemonic('f');
/*      */ 
/*  915 */     fileMenu.add(createOpenMI());
/*  916 */     fileMenu.add(createOpenURLMI());
/*  917 */     fileMenu.addSeparator();
/*  918 */     fileMenu.add(createCloseMI());
/*  919 */     createMRUFileListMI(fileMenu);
/*  920 */     fileMenu.addSeparator();
/*  921 */     fileMenu.add(createExitMI());
/*  922 */     return fileMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenMI()
/*      */   {
/*  930 */     JMenuItem result = new JMenuItem("Open...");
/*  931 */     result.setMnemonic('o');
/*  932 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  934 */         LogBrokerMonitor.this.requestOpen();
/*      */       }
/*      */     });
/*  937 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenURLMI()
/*      */   {
/*  945 */     JMenuItem result = new JMenuItem("Open URL...");
/*  946 */     result.setMnemonic('u');
/*  947 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  949 */         LogBrokerMonitor.this.requestOpenURL();
/*      */       }
/*      */     });
/*  952 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createCloseMI() {
/*  956 */     JMenuItem result = new JMenuItem("Close");
/*  957 */     result.setMnemonic('c');
/*  958 */     result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
/*  959 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  961 */         LogBrokerMonitor.this.requestClose();
/*      */       }
/*      */     });
/*  964 */     return result;
/*      */   }
/*      */ 
/*      */   protected void createMRUFileListMI(JMenu menu)
/*      */   {
/*  973 */     String[] files = this._mruFileManager.getMRUFileList();
/*      */ 
/*  975 */     if (files != null) {
/*  976 */       menu.addSeparator();
/*  977 */       for (int i = 0; i < files.length; i++) {
/*  978 */         JMenuItem result = new JMenuItem(i + 1 + " " + files[i]);
/*  979 */         result.setMnemonic(i + 1);
/*  980 */         result.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent e) {
/*  982 */             LogBrokerMonitor.this.requestOpenMRU(e);
/*      */           }
/*      */         });
/*  985 */         menu.add(result);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JMenuItem createExitMI() {
/*  991 */     JMenuItem result = new JMenuItem("Exit");
/*  992 */     result.setMnemonic('x');
/*  993 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  995 */         LogBrokerMonitor.this.requestExit();
/*      */       }
/*      */     });
/*  998 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createConfigureMenu() {
/* 1002 */     JMenu configureMenu = new JMenu("Configure");
/* 1003 */     configureMenu.setMnemonic('c');
/* 1004 */     configureMenu.add(createConfigureSave());
/* 1005 */     configureMenu.add(createConfigureReset());
/* 1006 */     configureMenu.add(createConfigureMaxRecords());
/*      */ 
/* 1008 */     return configureMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureSave() {
/* 1012 */     JMenuItem result = new JMenuItem("Save");
/* 1013 */     result.setMnemonic('s');
/* 1014 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1016 */         LogBrokerMonitor.this.saveConfiguration();
/*      */       }
/*      */     });
/* 1020 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureReset() {
/* 1024 */     JMenuItem result = new JMenuItem("Reset");
/* 1025 */     result.setMnemonic('r');
/* 1026 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1028 */         LogBrokerMonitor.this.resetConfiguration();
/*      */       }
/*      */     });
/* 1032 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureMaxRecords() {
/* 1036 */     JMenuItem result = new JMenuItem("Set Max Number of Records");
/* 1037 */     result.setMnemonic('m');
/* 1038 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1040 */         LogBrokerMonitor.this.setMaxRecordConfiguration();
/*      */       }
/*      */     });
/* 1044 */     return result;
/*      */   }
/*      */ 
/*      */   protected void saveConfiguration()
/*      */   {
/* 1049 */     this._configurationManager.save();
/*      */   }
/*      */ 
/*      */   protected void resetConfiguration() {
/* 1053 */     this._configurationManager.reset();
/*      */   }
/*      */ 
/*      */   protected void setMaxRecordConfiguration() {
/* 1057 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);
/*      */ 
/* 1060 */     String temp = inputDialog.getText();
/*      */ 
/* 1062 */     if (temp != null)
/*      */       try {
/* 1064 */         setMaxNumberOfLogRecords(Integer.parseInt(temp));
/*      */       } catch (NumberFormatException e) {
/* 1066 */         LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");
/*      */ 
/* 1069 */         setMaxRecordConfiguration();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected JMenu createHelpMenu()
/*      */   {
/* 1076 */     JMenu helpMenu = new JMenu("Help");
/* 1077 */     helpMenu.setMnemonic('h');
/* 1078 */     helpMenu.add(createHelpProperties());
/* 1079 */     return helpMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpProperties() {
/* 1083 */     String title = "LogFactor5 Properties";
/* 1084 */     JMenuItem result = new JMenuItem("LogFactor5 Properties");
/* 1085 */     result.setMnemonic('l');
/* 1086 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1088 */         LogBrokerMonitor.this.showPropertiesDialog("LogFactor5 Properties");
/*      */       }
/*      */     });
/* 1091 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showPropertiesDialog(String title) {
/* 1095 */     JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
/*      */   }
/*      */ 
/*      */   protected JMenu createEditMenu()
/*      */   {
/* 1104 */     JMenu editMenu = new JMenu("Edit");
/* 1105 */     editMenu.setMnemonic('e');
/* 1106 */     editMenu.add(createEditFindMI());
/* 1107 */     editMenu.add(createEditFindNextMI());
/* 1108 */     editMenu.addSeparator();
/* 1109 */     editMenu.add(createEditSortNDCMI());
/* 1110 */     editMenu.add(createEditRestoreAllNDCMI());
/* 1111 */     return editMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindNextMI() {
/* 1115 */     JMenuItem editFindNextMI = new JMenuItem("Find Next");
/* 1116 */     editFindNextMI.setMnemonic('n');
/* 1117 */     editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
/* 1118 */     editFindNextMI.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1120 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1123 */     return editFindNextMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindMI() {
/* 1127 */     JMenuItem editFindMI = new JMenuItem("Find");
/* 1128 */     editFindMI.setMnemonic('f');
/* 1129 */     editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));
/*      */ 
/* 1131 */     editFindMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1134 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);
/*      */ 
/* 1141 */         LogBrokerMonitor.this.setSearchText(inputValue);
/* 1142 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1147 */     return editFindMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditSortNDCMI()
/*      */   {
/* 1154 */     JMenuItem editSortNDCMI = new JMenuItem("Sort by NDC");
/* 1155 */     editSortNDCMI.setMnemonic('s');
/* 1156 */     editSortNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1159 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Sort by this NDC: ", "Sort Log Records by NDC", 3);
/*      */ 
/* 1166 */         LogBrokerMonitor.this.setNDCTextFilter(inputValue);
/* 1167 */         LogBrokerMonitor.this.sortByNDC();
/* 1168 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1169 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1174 */     return editSortNDCMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditRestoreAllNDCMI()
/*      */   {
/* 1180 */     JMenuItem editRestoreAllNDCMI = new JMenuItem("Restore all NDCs");
/* 1181 */     editRestoreAllNDCMI.setMnemonic('r');
/* 1182 */     editRestoreAllNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1185 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().setLogRecordFilter(LogBrokerMonitor.this.createLogRecordFilter());
/*      */ 
/* 1187 */         LogBrokerMonitor.this.setNDCTextFilter("");
/* 1188 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1189 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1193 */     return editRestoreAllNDCMI;
/*      */   }
/*      */ 
/*      */   protected JToolBar createToolBar() {
/* 1197 */     JToolBar tb = new JToolBar();
/* 1198 */     tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/* 1199 */     JComboBox fontCombo = new JComboBox();
/* 1200 */     JComboBox fontSizeCombo = new JComboBox();
/* 1201 */     this._fontSizeCombo = fontSizeCombo;
/*      */ 
/* 1203 */     ClassLoader cl = getClass().getClassLoader();
/* 1204 */     if (cl == null) {
/* 1205 */       cl = ClassLoader.getSystemClassLoader();
/*      */     }
/* 1207 */     URL newIconURL = cl.getResource("org/apache/log4j/lf5/viewer/images/channelexplorer_new.gif");
/*      */ 
/* 1210 */     ImageIcon newIcon = null;
/*      */ 
/* 1212 */     if (newIconURL != null) {
/* 1213 */       newIcon = new ImageIcon(newIconURL);
/*      */     }
/*      */ 
/* 1216 */     JButton newButton = new JButton("Clear Log Table");
/*      */ 
/* 1218 */     if (newIcon != null) {
/* 1219 */       newButton.setIcon(newIcon);
/*      */     }
/*      */ 
/* 1222 */     newButton.setToolTipText("Clear Log Table.");
/*      */ 
/* 1225 */     newButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1228 */         LogBrokerMonitor.this._table.clearLogRecords();
/* 1229 */         LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().resetAllNodeCounts();
/* 1230 */         LogBrokerMonitor.this.updateStatusLabel();
/* 1231 */         LogBrokerMonitor.this.clearDetailTextArea();
/* 1232 */         LogRecord.resetSequenceNumber();
/*      */       }
/*      */     });
/* 1237 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*      */     String[] fonts;
/*      */     String[] fonts;
/* 1242 */     if (this._loadSystemFonts) {
/* 1243 */       fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*      */     }
/*      */     else {
/* 1246 */       fonts = tk.getFontList();
/*      */     }
/*      */ 
/* 1249 */     for (int j = 0; j < fonts.length; j++) {
/* 1250 */       fontCombo.addItem(fonts[j]);
/*      */     }
/*      */ 
/* 1253 */     fontCombo.setSelectedItem(this._fontName);
/*      */ 
/* 1255 */     fontCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1259 */         JComboBox box = (JComboBox)e.getSource();
/* 1260 */         String font = (String)box.getSelectedItem();
/* 1261 */         LogBrokerMonitor.this._table.setFont(new Font(font, 0, LogBrokerMonitor.this._fontSize));
/* 1262 */         LogBrokerMonitor.this._fontName = font;
/*      */       }
/*      */     });
/* 1267 */     fontSizeCombo.addItem("8");
/* 1268 */     fontSizeCombo.addItem("9");
/* 1269 */     fontSizeCombo.addItem("10");
/* 1270 */     fontSizeCombo.addItem("12");
/* 1271 */     fontSizeCombo.addItem("14");
/* 1272 */     fontSizeCombo.addItem("16");
/* 1273 */     fontSizeCombo.addItem("18");
/* 1274 */     fontSizeCombo.addItem("24");
/*      */ 
/* 1276 */     fontSizeCombo.setSelectedItem(String.valueOf(this._fontSize));
/* 1277 */     fontSizeCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1280 */         JComboBox box = (JComboBox)e.getSource();
/* 1281 */         String size = (String)box.getSelectedItem();
/* 1282 */         int s = Integer.valueOf(size).intValue();
/*      */ 
/* 1284 */         LogBrokerMonitor.this.setFontSizeSilently(s);
/* 1285 */         LogBrokerMonitor.this.refreshDetailTextArea();
/* 1286 */         LogBrokerMonitor.this._fontSize = s;
/*      */       }
/*      */     });
/* 1291 */     tb.add(new JLabel(" Font: "));
/* 1292 */     tb.add(fontCombo);
/* 1293 */     tb.add(fontSizeCombo);
/* 1294 */     tb.addSeparator();
/* 1295 */     tb.addSeparator();
/* 1296 */     tb.add(newButton);
/*      */ 
/* 1298 */     newButton.setAlignmentY(0.5F);
/* 1299 */     newButton.setAlignmentX(0.5F);
/*      */ 
/* 1301 */     fontCombo.setMaximumSize(fontCombo.getPreferredSize());
/* 1302 */     fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());
/*      */ 
/* 1305 */     return tb;
/*      */   }
/*      */ 
/*      */   protected void setView(String viewString, LogTable table)
/*      */   {
/* 1323 */     if ("Detailed".equals(viewString)) {
/* 1324 */       table.setDetailedView();
/*      */     } else {
/* 1326 */       String message = viewString + "does not match a supported view.";
/* 1327 */       throw new IllegalArgumentException(message);
/*      */     }
/* 1329 */     this._currentView = viewString;
/*      */   }
/*      */ 
/*      */   protected JComboBox createLogLevelCombo() {
/* 1333 */     JComboBox result = new JComboBox();
/* 1334 */     Iterator levels = getLogLevels();
/* 1335 */     while (levels.hasNext()) {
/* 1336 */       result.addItem(levels.next());
/*      */     }
/* 1338 */     result.setSelectedItem(this._leastSevereDisplayedLogLevel);
/*      */ 
/* 1340 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1342 */         JComboBox box = (JComboBox)e.getSource();
/* 1343 */         LogLevel level = (LogLevel)box.getSelectedItem();
/* 1344 */         LogBrokerMonitor.this.setLeastSevereDisplayedLogLevel(level);
/*      */       }
/*      */     });
/* 1347 */     result.setMaximumSize(result.getPreferredSize());
/* 1348 */     return result;
/*      */   }
/*      */ 
/*      */   protected void setLeastSevereDisplayedLogLevel(LogLevel level) {
/* 1352 */     if ((level == null) || (this._leastSevereDisplayedLogLevel == level)) {
/* 1353 */       return;
/*      */     }
/* 1355 */     this._leastSevereDisplayedLogLevel = level;
/* 1356 */     this._table.getFilteredLogTableModel().refresh();
/* 1357 */     updateStatusLabel();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void trackTableScrollPane()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void centerFrame(JFrame frame)
/*      */   {
/* 1375 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/* 1376 */     Dimension comp = frame.getSize();
/*      */ 
/* 1378 */     frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
/*      */   }
/*      */ 
/*      */   protected void requestOpen()
/*      */   {
/*      */     JFileChooser chooser;
/*      */     JFileChooser chooser;
/* 1390 */     if (this._fileLocation == null)
/* 1391 */       chooser = new JFileChooser();
/*      */     else {
/* 1393 */       chooser = new JFileChooser(this._fileLocation);
/*      */     }
/*      */ 
/* 1396 */     int returnVal = chooser.showOpenDialog(this._logMonitorFrame);
/* 1397 */     if (returnVal == 0) {
/* 1398 */       File f = chooser.getSelectedFile();
/* 1399 */       if (loadLogFile(f)) {
/* 1400 */         this._fileLocation = chooser.getSelectedFile();
/* 1401 */         this._mruFileManager.set(f);
/* 1402 */         updateMRUList();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestOpenURL()
/*      */   {
/* 1412 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Open URL", "URL:");
/*      */ 
/* 1414 */     String temp = inputDialog.getText();
/*      */     LogFactor5ErrorDialog error;
/* 1416 */     if (temp != null) {
/* 1417 */       if (temp.indexOf("://") == -1) {
/* 1418 */         temp = "http://" + temp;
/*      */       }
/*      */       try
/*      */       {
/* 1422 */         URL url = new URL(temp);
/* 1423 */         if (loadLogFile(url)) {
/* 1424 */           this._mruFileManager.set(url);
/* 1425 */           updateMRUList();
/*      */         }
/*      */       } catch (MalformedURLException e) {
/* 1428 */         error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL.");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateMRUList()
/*      */   {
/* 1439 */     JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
/* 1440 */     menu.removeAll();
/* 1441 */     menu.add(createOpenMI());
/* 1442 */     menu.add(createOpenURLMI());
/* 1443 */     menu.addSeparator();
/* 1444 */     menu.add(createCloseMI());
/* 1445 */     createMRUFileListMI(menu);
/* 1446 */     menu.addSeparator();
/* 1447 */     menu.add(createExitMI());
/*      */   }
/*      */ 
/*      */   protected void requestClose() {
/* 1451 */     setCallSystemExitOnClose(false);
/* 1452 */     closeAfterConfirm(); } 
/* 1459 */   protected void requestOpenMRU(ActionEvent e) { String file = e.getActionCommand();
/* 1460 */     StringTokenizer st = new StringTokenizer(file);
/* 1461 */     String num = st.nextToken().trim();
/* 1462 */     file = st.nextToken("\n");
/*      */     LogFactor5ErrorDialog error;
/*      */     try { int index = Integer.parseInt(num) - 1;
/*      */ 
/* 1467 */       InputStream in = this._mruFileManager.getInputStream(index);
/* 1468 */       LogFileParser lfp = new LogFileParser(in);
/* 1469 */       lfp.parse(this);
/*      */ 
/* 1471 */       this._mruFileManager.moveToTop(index);
/* 1472 */       updateMRUList();
/*      */     } catch (Exception me)
/*      */     {
/* 1475 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Unable to load file " + file);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestExit()
/*      */   {
/* 1482 */     this._mruFileManager.save();
/* 1483 */     setCallSystemExitOnClose(true);
/* 1484 */     closeAfterConfirm();
/*      */   }
/*      */ 
/*      */   protected void closeAfterConfirm() {
/* 1488 */     StringBuffer message = new StringBuffer();
/*      */ 
/* 1490 */     if (!this._callSystemExitOnClose) {
/* 1491 */       message.append("Are you sure you want to close the logging ");
/* 1492 */       message.append("console?\n");
/* 1493 */       message.append("(Note: This will not shut down the Virtual Machine,\n");
/* 1494 */       message.append("or the Swing event thread.)");
/*      */     } else {
/* 1496 */       message.append("Are you sure you want to exit?\n");
/* 1497 */       message.append("This will shut down the Virtual Machine.\n");
/*      */     }
/*      */ 
/* 1500 */     String title = "Are you sure you want to dispose of the Logging Console?";
/*      */ 
/* 1503 */     if (this._callSystemExitOnClose == true) {
/* 1504 */       title = "Are you sure you want to exit?";
/*      */     }
/* 1506 */     int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), title, 2, 3, null);
/*      */ 
/* 1515 */     if (value == 0)
/* 1516 */       dispose();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogLevels()
/*      */   {
/* 1521 */     return this._levels.iterator();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogTableColumns() {
/* 1525 */     return this._columns.iterator();
/*      */   }
/*      */   protected boolean loadLogFile(File file) {
/* 1532 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try { LogFileParser lfp = new LogFileParser(file);
/* 1535 */       lfp.parse(this);
/* 1536 */       ok = true;
/*      */     } catch (IOException e) {
/* 1538 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading " + file.getName());
/*      */     }
/*      */ 
/* 1542 */     return ok;
/*      */   }
/*      */   protected boolean loadLogFile(URL url) {
/* 1549 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try { LogFileParser lfp = new LogFileParser(url.openStream());
/* 1552 */       lfp.parse(this);
/* 1553 */       ok = true;
/*      */     } catch (IOException e) {
/* 1555 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL:" + url.getFile());
/*      */     }
/*      */ 
/* 1558 */     return ok;
/*      */   }
/*      */ 
/*      */   class LogBrokerMonitorWindowAdaptor extends WindowAdapter
/*      */   {
/*      */     protected LogBrokerMonitor _monitor;
/*      */ 
/*      */     public LogBrokerMonitorWindowAdaptor(LogBrokerMonitor monitor)
/*      */     {
/* 1572 */       this._monitor = monitor;
/*      */     }
/*      */ 
/*      */     public void windowClosing(WindowEvent ev) {
/* 1576 */       this._monitor.requestClose();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogBrokerMonitor
 * JD-Core Version:    0.6.0
 */
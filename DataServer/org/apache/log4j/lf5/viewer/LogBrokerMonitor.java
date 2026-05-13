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
/*   60 */   protected int _logMonitorFrameWidth = 550;
/*   61 */   protected int _logMonitorFrameHeight = 500;
/*      */   protected LogTable _table;
/*      */   protected CategoryExplorerTree _categoryExplorerTree;
/*      */   protected String _searchText;
/*   65 */   protected String _NDCTextFilter = "";
/*   66 */   protected LogLevel _leastSevereDisplayedLogLevel = LogLevel.DEBUG;
/*      */   protected JScrollPane _logTableScrollPane;
/*      */   protected JLabel _statusLabel;
/*   70 */   protected Object _lock = new Object();
/*      */   protected JComboBox _fontSizeCombo;
/*   73 */   protected int _fontSize = 10;
/*   74 */   protected String _fontName = "Dialog";
/*   75 */   protected String _currentView = "Detailed";
/*      */ 
/*   77 */   protected boolean _loadSystemFonts = false;
/*   78 */   protected boolean _trackTableScrollPane = true;
/*      */   protected Dimension _lastTableViewportSize;
/*   80 */   protected boolean _callSystemExitOnClose = false;
/*   81 */   protected List _displayedLogBrokerProperties = new Vector();
/*      */ 
/*   83 */   protected Map _logLevelMenuItems = new HashMap();
/*   84 */   protected Map _logTableColumnMenuItems = new HashMap();
/*      */ 
/*   86 */   protected List _levels = null;
/*   87 */   protected List _columns = null;
/*   88 */   protected boolean _isDisposed = false;
/*      */ 
/*   90 */   protected ConfigurationManager _configurationManager = null;
/*   91 */   protected MRUFileManager _mruFileManager = null;
/*   92 */   protected File _fileLocation = null;
/*      */ 
/*      */   public LogBrokerMonitor(List logLevels)
/*      */   {
/*  107 */     this._levels = logLevels;
/*  108 */     this._columns = LogTableColumn.getLogTableColumns();
/*      */ 
/*  112 */     String callSystemExitOnClose = System.getProperty("monitor.exit");
/*      */ 
/*  114 */     if (callSystemExitOnClose == null) {
/*  115 */       callSystemExitOnClose = "false";
/*      */     }
/*  117 */     callSystemExitOnClose = callSystemExitOnClose.trim().toLowerCase();
/*      */ 
/*  119 */     if (callSystemExitOnClose.equals("true")) {
/*  120 */       this._callSystemExitOnClose = true;
/*      */     }
/*      */ 
/*  123 */     initComponents();
/*      */ 
/*  126 */     this._logMonitorFrame.addWindowListener(new LogBrokerMonitorWindowAdaptor(this));
/*      */   }
/*      */ 
/*      */   public void show(int delay)
/*      */   {
/*  140 */     if (this._logMonitorFrame.isVisible()) {
/*  141 */       return;
/*      */     }
/*      */ 
/*  144 */     SwingUtilities.invokeLater(new Runnable(delay) { private final int val$delay;
/*      */ 
/*  146 */       public void run() { Thread.yield();
/*  147 */         LogBrokerMonitor.this.pause(this.val$delay);
/*  148 */         LogBrokerMonitor.this._logMonitorFrame.setVisible(true); } } );
/*      */   }
/*      */ 
/*      */   public void show()
/*      */   {
/*  154 */     show(0);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  161 */     this._logMonitorFrame.dispose();
/*  162 */     this._isDisposed = true;
/*      */ 
/*  164 */     if (this._callSystemExitOnClose == true)
/*  165 */       System.exit(0);
/*      */   }
/*      */ 
/*      */   public void hide()
/*      */   {
/*  173 */     this._logMonitorFrame.setVisible(false);
/*      */   }
/*      */ 
/*      */   public DateFormatManager getDateFormatManager()
/*      */   {
/*  180 */     return this._table.getDateFormatManager();
/*      */   }
/*      */ 
/*      */   public void setDateFormatManager(DateFormatManager dfm)
/*      */   {
/*  187 */     this._table.setDateFormatManager(dfm);
/*      */   }
/*      */ 
/*      */   public boolean getCallSystemExitOnClose()
/*      */   {
/*  195 */     return this._callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*      */   {
/*  203 */     this._callSystemExitOnClose = callSystemExitOnClose;
/*      */   }
/*      */ 
/*      */   public void addMessage(LogRecord lr)
/*      */   {
/*  212 */     if (this._isDisposed == true)
/*      */     {
/*  215 */       return;
/*      */     }
/*      */ 
/*  218 */     SwingUtilities.invokeLater(new Runnable(lr) { private final LogRecord val$lr;
/*      */ 
/*  220 */       public void run() { LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().addLogRecord(this.val$lr);
/*  221 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().addLogRecord(this.val$lr);
/*  222 */         LogBrokerMonitor.this.updateStatusLabel(); } } );
/*      */   }
/*      */ 
/*      */   public void setMaxNumberOfLogRecords(int maxNumberOfLogRecords)
/*      */   {
/*  228 */     this._table.getFilteredLogTableModel().setMaxNumberOfLogRecords(maxNumberOfLogRecords);
/*      */   }
/*      */ 
/*      */   public JFrame getBaseFrame() {
/*  232 */     return this._logMonitorFrame;
/*      */   }
/*      */ 
/*      */   public void setTitle(String title) {
/*  236 */     this._logMonitorFrame.setTitle(title + " - LogFactor5");
/*      */   }
/*      */ 
/*      */   public void setFrameSize(int width, int height) {
/*  240 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/*  241 */     if ((0 < width) && (width < screen.width)) {
/*  242 */       this._logMonitorFrameWidth = width;
/*      */     }
/*  244 */     if ((0 < height) && (height < screen.height)) {
/*  245 */       this._logMonitorFrameHeight = height;
/*      */     }
/*  247 */     updateFrameSize();
/*      */   }
/*      */ 
/*      */   public void setFontSize(int fontSize) {
/*  251 */     changeFontSizeCombo(this._fontSizeCombo, fontSize);
/*      */   }
/*      */ 
/*      */   public void addDisplayedProperty(Object messageLine)
/*      */   {
/*  257 */     this._displayedLogBrokerProperties.add(messageLine);
/*      */   }
/*      */ 
/*      */   public Map getLogLevelMenuItems() {
/*  261 */     return this._logLevelMenuItems;
/*      */   }
/*      */ 
/*      */   public Map getLogTableColumnMenuItems() {
/*  265 */     return this._logTableColumnMenuItems;
/*      */   }
/*      */ 
/*      */   public JCheckBoxMenuItem getTableColumnMenuItem(LogTableColumn column) {
/*  269 */     return getLogTableColumnMenuItem(column);
/*      */   }
/*      */ 
/*      */   public CategoryExplorerTree getCategoryExplorerTree() {
/*  273 */     return this._categoryExplorerTree;
/*      */   }
/*      */ 
/*      */   public String getNDCTextFilter()
/*      */   {
/*  279 */     return this._NDCTextFilter;
/*      */   }
/*      */ 
/*      */   public void setNDCLogRecordFilter(String textFilter)
/*      */   {
/*  286 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(textFilter));
/*      */   }
/*      */ 
/*      */   protected void setSearchText(String text)
/*      */   {
/*  294 */     this._searchText = text;
/*      */   }
/*      */ 
/*      */   protected void setNDCTextFilter(String text)
/*      */   {
/*  301 */     if (text == null)
/*  302 */       this._NDCTextFilter = "";
/*      */     else
/*  304 */       this._NDCTextFilter = text;
/*      */   }
/*      */ 
/*      */   protected void sortByNDC()
/*      */   {
/*  312 */     String text = this._NDCTextFilter;
/*  313 */     if ((text == null) || (text.length() == 0)) {
/*  314 */       return;
/*      */     }
/*      */ 
/*  318 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createNDCLogRecordFilter(text));
/*      */   }
/*      */ 
/*      */   protected void findSearchText()
/*      */   {
/*  323 */     String text = this._searchText;
/*  324 */     if ((text == null) || (text.length() == 0)) {
/*  325 */       return;
/*      */     }
/*  327 */     int startRow = getFirstSelectedRow();
/*  328 */     int foundRow = findRecord(startRow, text, this._table.getFilteredLogTableModel().getFilteredRecords());
/*      */ 
/*  333 */     selectRow(foundRow);
/*      */   }
/*      */ 
/*      */   protected int getFirstSelectedRow() {
/*  337 */     return this._table.getSelectionModel().getMinSelectionIndex();
/*      */   }
/*      */ 
/*      */   protected void selectRow(int foundRow) {
/*  341 */     if (foundRow == -1) {
/*  342 */       String message = this._searchText + " not found.";
/*  343 */       JOptionPane.showMessageDialog(this._logMonitorFrame, message, "Text not found", 1);
/*      */ 
/*  349 */       return;
/*      */     }
/*  351 */     LF5SwingUtils.selectRow(foundRow, this._table, this._logTableScrollPane);
/*      */   }
/*      */ 
/*      */   protected int findRecord(int startRow, String searchText, List records)
/*      */   {
/*  359 */     if (startRow < 0)
/*  360 */       startRow = 0;
/*      */     else {
/*  362 */       startRow++;
/*      */     }
/*  364 */     int len = records.size();
/*      */ 
/*  366 */     for (int i = startRow; i < len; i++) {
/*  367 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  368 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  372 */     len = startRow;
/*  373 */     for (int i = 0; i < len; i++) {
/*  374 */       if (matches((LogRecord)records.get(i), searchText)) {
/*  375 */         return i;
/*      */       }
/*      */     }
/*      */ 
/*  379 */     return -1;
/*      */   }
/*      */ 
/*      */   protected boolean matches(LogRecord record, String text)
/*      */   {
/*  387 */     String message = record.getMessage();
/*  388 */     String NDC = record.getNDC();
/*      */ 
/*  390 */     if (((message == null) && (NDC == null)) || (text == null)) {
/*  391 */       return false;
/*      */     }
/*      */ 
/*  395 */     return (message.toLowerCase().indexOf(text.toLowerCase()) != -1) || (NDC.toLowerCase().indexOf(text.toLowerCase()) != -1);
/*      */   }
/*      */ 
/*      */   protected void refresh(JTextArea textArea)
/*      */   {
/*  407 */     String text = textArea.getText();
/*  408 */     textArea.setText("");
/*  409 */     textArea.setText(text);
/*      */   }
/*      */ 
/*      */   protected void refreshDetailTextArea() {
/*  413 */     refresh(this._table._detailTextArea);
/*      */   }
/*      */ 
/*      */   protected void clearDetailTextArea() {
/*  417 */     this._table._detailTextArea.setText("");
/*      */   }
/*      */ 
/*      */   protected int changeFontSizeCombo(JComboBox box, int requestedSize)
/*      */   {
/*  426 */     int len = box.getItemCount();
/*      */ 
/*  429 */     Object selectedObject = box.getItemAt(0);
/*  430 */     int selectedValue = Integer.parseInt(String.valueOf(selectedObject));
/*  431 */     for (int i = 0; i < len; i++) {
/*  432 */       Object currentObject = box.getItemAt(i);
/*  433 */       int currentValue = Integer.parseInt(String.valueOf(currentObject));
/*  434 */       if ((selectedValue < currentValue) && (currentValue <= requestedSize)) {
/*  435 */         selectedValue = currentValue;
/*  436 */         selectedObject = currentObject;
/*      */       }
/*      */     }
/*  439 */     box.setSelectedItem(selectedObject);
/*  440 */     return selectedValue;
/*      */   }
/*      */ 
/*      */   protected void setFontSizeSilently(int fontSize)
/*      */   {
/*  447 */     this._fontSize = fontSize;
/*  448 */     setFontSize(this._table._detailTextArea, fontSize);
/*  449 */     selectRow(0);
/*  450 */     setFontSize(this._table, fontSize);
/*      */   }
/*      */ 
/*      */   protected void setFontSize(Component component, int fontSize) {
/*  454 */     Font oldFont = component.getFont();
/*  455 */     Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), fontSize);
/*      */ 
/*  457 */     component.setFont(newFont);
/*      */   }
/*      */ 
/*      */   protected void updateFrameSize() {
/*  461 */     this._logMonitorFrame.setSize(this._logMonitorFrameWidth, this._logMonitorFrameHeight);
/*  462 */     centerFrame(this._logMonitorFrame);
/*      */   }
/*      */ 
/*      */   protected void pause(int millis) {
/*      */     try {
/*  467 */       Thread.sleep(millis);
/*      */     }
/*      */     catch (InterruptedException e)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void initComponents()
/*      */   {
/*  477 */     this._logMonitorFrame = new JFrame("LogFactor5");
/*      */ 
/*  479 */     this._logMonitorFrame.setDefaultCloseOperation(0);
/*      */ 
/*  481 */     String resource = "/org/apache/log4j/lf5/viewer/images/lf5_small_icon.gif";
/*      */ 
/*  483 */     URL lf5IconURL = getClass().getResource(resource);
/*      */ 
/*  485 */     if (lf5IconURL != null) {
/*  486 */       this._logMonitorFrame.setIconImage(new ImageIcon(lf5IconURL).getImage());
/*      */     }
/*  488 */     updateFrameSize();
/*      */ 
/*  493 */     JTextArea detailTA = createDetailTextArea();
/*  494 */     JScrollPane detailTAScrollPane = new JScrollPane(detailTA);
/*  495 */     this._table = new LogTable(detailTA);
/*  496 */     setView(this._currentView, this._table);
/*  497 */     this._table.setFont(new Font(this._fontName, 0, this._fontSize));
/*  498 */     this._logTableScrollPane = new JScrollPane(this._table);
/*      */ 
/*  500 */     if (this._trackTableScrollPane) {
/*  501 */       this._logTableScrollPane.getVerticalScrollBar().addAdjustmentListener(new TrackingAdjustmentListener());
/*      */     }
/*      */ 
/*  510 */     JSplitPane tableViewerSplitPane = new JSplitPane();
/*  511 */     tableViewerSplitPane.setOneTouchExpandable(true);
/*  512 */     tableViewerSplitPane.setOrientation(0);
/*  513 */     tableViewerSplitPane.setLeftComponent(this._logTableScrollPane);
/*  514 */     tableViewerSplitPane.setRightComponent(detailTAScrollPane);
/*      */ 
/*  522 */     tableViewerSplitPane.setDividerLocation(350);
/*      */ 
/*  528 */     this._categoryExplorerTree = new CategoryExplorerTree();
/*      */ 
/*  530 */     this._table.getFilteredLogTableModel().setLogRecordFilter(createLogRecordFilter());
/*      */ 
/*  532 */     JScrollPane categoryExplorerTreeScrollPane = new JScrollPane(this._categoryExplorerTree);
/*      */ 
/*  534 */     categoryExplorerTreeScrollPane.setPreferredSize(new Dimension(130, 400));
/*      */ 
/*  537 */     this._mruFileManager = new MRUFileManager();
/*      */ 
/*  543 */     JSplitPane splitPane = new JSplitPane();
/*  544 */     splitPane.setOneTouchExpandable(true);
/*  545 */     splitPane.setRightComponent(tableViewerSplitPane);
/*  546 */     splitPane.setLeftComponent(categoryExplorerTreeScrollPane);
/*      */ 
/*  548 */     splitPane.setDividerLocation(130);
/*      */ 
/*  553 */     this._logMonitorFrame.getRootPane().setJMenuBar(createMenuBar());
/*  554 */     this._logMonitorFrame.getContentPane().add(splitPane, "Center");
/*  555 */     this._logMonitorFrame.getContentPane().add(createToolBar(), "North");
/*      */ 
/*  557 */     this._logMonitorFrame.getContentPane().add(createStatusArea(), "South");
/*      */ 
/*  560 */     makeLogTableListenToCategoryExplorer();
/*  561 */     addTableModelProperties();
/*      */ 
/*  566 */     this._configurationManager = new ConfigurationManager(this, this._table);
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createLogRecordFilter()
/*      */   {
/*  571 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  573 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  574 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  579 */     return result;
/*      */   }
/*      */ 
/*      */   protected LogRecordFilter createNDCLogRecordFilter(String text)
/*      */   {
/*  585 */     this._NDCTextFilter = text;
/*  586 */     LogRecordFilter result = new LogRecordFilter() {
/*      */       public boolean passes(LogRecord record) {
/*  588 */         String NDC = record.getNDC();
/*  589 */         CategoryPath path = new CategoryPath(record.getCategory());
/*  590 */         if ((NDC == null) || (LogBrokerMonitor.this._NDCTextFilter == null))
/*  591 */           return false;
/*  592 */         if (NDC.toLowerCase().indexOf(LogBrokerMonitor.this._NDCTextFilter.toLowerCase()) == -1) {
/*  593 */           return false;
/*      */         }
/*  595 */         return (LogBrokerMonitor.this.getMenuItem(record.getLevel()).isSelected()) && (LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().isCategoryPathActive(path));
/*      */       }
/*      */     };
/*  601 */     return result;
/*      */   }
/*      */ 
/*      */   protected void updateStatusLabel()
/*      */   {
/*  606 */     this._statusLabel.setText(getRecordsDisplayedMessage());
/*      */   }
/*      */ 
/*      */   protected String getRecordsDisplayedMessage() {
/*  610 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*  611 */     return getStatusText(model.getRowCount(), model.getTotalRowCount());
/*      */   }
/*      */ 
/*      */   protected void addTableModelProperties() {
/*  615 */     FilteredLogTableModel model = this._table.getFilteredLogTableModel();
/*      */ 
/*  617 */     addDisplayedProperty(new Object() {
/*      */       public String toString() {
/*  619 */         return LogBrokerMonitor.this.getRecordsDisplayedMessage();
/*      */       }
/*      */     });
/*  622 */     addDisplayedProperty(new Object(model) { private final FilteredLogTableModel val$model;
/*      */ 
/*  624 */       public String toString() { return "Maximum number of displayed LogRecords: " + this.val$model._maxNumberOfLogRecords; }
/*      */     });
/*      */   }
/*      */ 
/*      */   protected String getStatusText(int displayedRows, int totalRows)
/*      */   {
/*  631 */     StringBuffer result = new StringBuffer();
/*  632 */     result.append("Displaying: ");
/*  633 */     result.append(displayedRows);
/*  634 */     result.append(" records out of a total of: ");
/*  635 */     result.append(totalRows);
/*  636 */     result.append(" records.");
/*  637 */     return result.toString();
/*      */   }
/*      */ 
/*      */   protected void makeLogTableListenToCategoryExplorer() {
/*  641 */     ActionListener listener = new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  643 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  644 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     };
/*  647 */     this._categoryExplorerTree.getExplorerModel().addActionListener(listener);
/*      */   }
/*      */ 
/*      */   protected JPanel createStatusArea() {
/*  651 */     JPanel statusArea = new JPanel();
/*  652 */     JLabel status = new JLabel("No log records to display.");
/*      */ 
/*  654 */     this._statusLabel = status;
/*  655 */     status.setHorizontalAlignment(2);
/*      */ 
/*  657 */     statusArea.setBorder(BorderFactory.createEtchedBorder());
/*  658 */     statusArea.setLayout(new FlowLayout(0, 0, 0));
/*  659 */     statusArea.add(status);
/*      */ 
/*  661 */     return statusArea;
/*      */   }
/*      */ 
/*      */   protected JTextArea createDetailTextArea() {
/*  665 */     JTextArea detailTA = new JTextArea();
/*  666 */     detailTA.setFont(new Font("Monospaced", 0, 14));
/*  667 */     detailTA.setTabSize(3);
/*  668 */     detailTA.setLineWrap(true);
/*  669 */     detailTA.setWrapStyleWord(false);
/*  670 */     return detailTA;
/*      */   }
/*      */ 
/*      */   protected JMenuBar createMenuBar() {
/*  674 */     JMenuBar menuBar = new JMenuBar();
/*  675 */     menuBar.add(createFileMenu());
/*  676 */     menuBar.add(createEditMenu());
/*  677 */     menuBar.add(createLogLevelMenu());
/*  678 */     menuBar.add(createViewMenu());
/*  679 */     menuBar.add(createConfigureMenu());
/*  680 */     menuBar.add(createHelpMenu());
/*      */ 
/*  682 */     return menuBar;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelMenu() {
/*  686 */     JMenu result = new JMenu("Log Level");
/*  687 */     result.setMnemonic('l');
/*  688 */     Iterator levels = getLogLevels();
/*  689 */     while (levels.hasNext()) {
/*  690 */       result.add(getMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  693 */     result.addSeparator();
/*  694 */     result.add(createAllLogLevelsMenuItem());
/*  695 */     result.add(createNoLogLevelsMenuItem());
/*  696 */     result.addSeparator();
/*  697 */     result.add(createLogLevelColorMenu());
/*  698 */     result.add(createResetLogLevelColorMenuItem());
/*      */ 
/*  700 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogLevelsMenuItem() {
/*  704 */     JMenuItem result = new JMenuItem("Show all LogLevels");
/*  705 */     result.setMnemonic('s');
/*  706 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  708 */         LogBrokerMonitor.this.selectAllLogLevels(true);
/*  709 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  710 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  713 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogLevelsMenuItem() {
/*  717 */     JMenuItem result = new JMenuItem("Hide all LogLevels");
/*  718 */     result.setMnemonic('h');
/*  719 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  721 */         LogBrokerMonitor.this.selectAllLogLevels(false);
/*  722 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  723 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  726 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createLogLevelColorMenu() {
/*  730 */     JMenu colorMenu = new JMenu("Configure LogLevel Colors");
/*  731 */     colorMenu.setMnemonic('c');
/*  732 */     Iterator levels = getLogLevels();
/*  733 */     while (levels.hasNext()) {
/*  734 */       colorMenu.add(createSubMenuItem((LogLevel)levels.next()));
/*      */     }
/*      */ 
/*  737 */     return colorMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createResetLogLevelColorMenuItem() {
/*  741 */     JMenuItem result = new JMenuItem("Reset LogLevel Colors");
/*  742 */     result.setMnemonic('r');
/*  743 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  746 */         LogLevel.resetLogLevelColorMap();
/*      */ 
/*  749 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*      */       }
/*      */     });
/*  752 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogLevels(boolean selected) {
/*  756 */     Iterator levels = getLogLevels();
/*  757 */     while (levels.hasNext())
/*  758 */       getMenuItem((LogLevel)levels.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getMenuItem(LogLevel level)
/*      */   {
/*  763 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logLevelMenuItems.get(level);
/*  764 */     if (result == null) {
/*  765 */       result = createMenuItem(level);
/*  766 */       this._logLevelMenuItems.put(level, result);
/*      */     }
/*  768 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createSubMenuItem(LogLevel level) {
/*  772 */     JMenuItem result = new JMenuItem(level.toString());
/*  773 */     LogLevel logLevel = level;
/*  774 */     result.setMnemonic(level.toString().charAt(0));
/*  775 */     result.addActionListener(new ActionListener(result, logLevel) { private final JMenuItem val$result;
/*      */       private final LogLevel val$logLevel;
/*      */ 
/*  777 */       public void actionPerformed(ActionEvent e) { LogBrokerMonitor.this.showLogLevelColorChangeDialog(this.val$result, this.val$logLevel);
/*      */       }
/*      */     });
/*  781 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showLogLevelColorChangeDialog(JMenuItem result, LogLevel level)
/*      */   {
/*  786 */     JMenuItem menuItem = result;
/*  787 */     Color newColor = JColorChooser.showDialog(this._logMonitorFrame, "Choose LogLevel Color", result.getForeground());
/*      */ 
/*  792 */     if (newColor != null)
/*      */     {
/*  794 */       level.setLogLevelColorMap(level, newColor);
/*  795 */       this._table.getFilteredLogTableModel().refresh();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createMenuItem(LogLevel level)
/*      */   {
/*  801 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(level.toString());
/*  802 */     result.setSelected(true);
/*  803 */     result.setMnemonic(level.toString().charAt(0));
/*  804 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  806 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/*  807 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/*  810 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createViewMenu()
/*      */   {
/*  815 */     JMenu result = new JMenu("View");
/*  816 */     result.setMnemonic('v');
/*  817 */     Iterator columns = getLogTableColumns();
/*  818 */     while (columns.hasNext()) {
/*  819 */       result.add(getLogTableColumnMenuItem((LogTableColumn)columns.next()));
/*      */     }
/*      */ 
/*  822 */     result.addSeparator();
/*  823 */     result.add(createAllLogTableColumnsMenuItem());
/*  824 */     result.add(createNoLogTableColumnsMenuItem());
/*  825 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem getLogTableColumnMenuItem(LogTableColumn column) {
/*  829 */     JCheckBoxMenuItem result = (JCheckBoxMenuItem)this._logTableColumnMenuItems.get(column);
/*  830 */     if (result == null) {
/*  831 */       result = createLogTableColumnMenuItem(column);
/*  832 */       this._logTableColumnMenuItems.put(column, result);
/*      */     }
/*  834 */     return result;
/*      */   }
/*      */ 
/*      */   protected JCheckBoxMenuItem createLogTableColumnMenuItem(LogTableColumn column) {
/*  838 */     JCheckBoxMenuItem result = new JCheckBoxMenuItem(column.toString());
/*      */ 
/*  840 */     result.setSelected(true);
/*  841 */     result.setMnemonic(column.toString().charAt(0));
/*  842 */     result.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  845 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  846 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  849 */     return result;
/*      */   }
/*      */ 
/*      */   protected List updateView() {
/*  853 */     ArrayList updatedList = new ArrayList();
/*  854 */     Iterator columnIterator = this._columns.iterator();
/*  855 */     while (columnIterator.hasNext()) {
/*  856 */       LogTableColumn column = (LogTableColumn)columnIterator.next();
/*  857 */       JCheckBoxMenuItem result = getLogTableColumnMenuItem(column);
/*      */ 
/*  859 */       if (result.isSelected()) {
/*  860 */         updatedList.add(column);
/*      */       }
/*      */     }
/*      */ 
/*  864 */     return updatedList;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createAllLogTableColumnsMenuItem() {
/*  868 */     JMenuItem result = new JMenuItem("Show all Columns");
/*  869 */     result.setMnemonic('s');
/*  870 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  872 */         LogBrokerMonitor.this.selectAllLogTableColumns(true);
/*      */ 
/*  874 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  875 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  878 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createNoLogTableColumnsMenuItem() {
/*  882 */     JMenuItem result = new JMenuItem("Hide all Columns");
/*  883 */     result.setMnemonic('h');
/*  884 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  886 */         LogBrokerMonitor.this.selectAllLogTableColumns(false);
/*      */ 
/*  888 */         List selectedColumns = LogBrokerMonitor.this.updateView();
/*  889 */         LogBrokerMonitor.this._table.setView(selectedColumns);
/*      */       }
/*      */     });
/*  892 */     return result;
/*      */   }
/*      */ 
/*      */   protected void selectAllLogTableColumns(boolean selected) {
/*  896 */     Iterator columns = getLogTableColumns();
/*  897 */     while (columns.hasNext())
/*  898 */       getLogTableColumnMenuItem((LogTableColumn)columns.next()).setSelected(selected);
/*      */   }
/*      */ 
/*      */   protected JMenu createFileMenu()
/*      */   {
/*  903 */     JMenu fileMenu = new JMenu("File");
/*  904 */     fileMenu.setMnemonic('f');
/*      */ 
/*  906 */     fileMenu.add(createOpenMI());
/*  907 */     fileMenu.add(createOpenURLMI());
/*  908 */     fileMenu.addSeparator();
/*  909 */     fileMenu.add(createCloseMI());
/*  910 */     createMRUFileListMI(fileMenu);
/*  911 */     fileMenu.addSeparator();
/*  912 */     fileMenu.add(createExitMI());
/*  913 */     return fileMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenMI()
/*      */   {
/*  921 */     JMenuItem result = new JMenuItem("Open...");
/*  922 */     result.setMnemonic('o');
/*  923 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  925 */         LogBrokerMonitor.this.requestOpen();
/*      */       }
/*      */     });
/*  928 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createOpenURLMI()
/*      */   {
/*  936 */     JMenuItem result = new JMenuItem("Open URL...");
/*  937 */     result.setMnemonic('u');
/*  938 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  940 */         LogBrokerMonitor.this.requestOpenURL();
/*      */       }
/*      */     });
/*  943 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createCloseMI() {
/*  947 */     JMenuItem result = new JMenuItem("Close");
/*  948 */     result.setMnemonic('c');
/*  949 */     result.setAccelerator(KeyStroke.getKeyStroke("control Q"));
/*  950 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  952 */         LogBrokerMonitor.this.requestClose();
/*      */       }
/*      */     });
/*  955 */     return result;
/*      */   }
/*      */ 
/*      */   protected void createMRUFileListMI(JMenu menu)
/*      */   {
/*  964 */     String[] files = this._mruFileManager.getMRUFileList();
/*      */ 
/*  966 */     if (files != null) {
/*  967 */       menu.addSeparator();
/*  968 */       for (int i = 0; i < files.length; i++) {
/*  969 */         JMenuItem result = new JMenuItem(i + 1 + " " + files[i]);
/*  970 */         result.setMnemonic(i + 1);
/*  971 */         result.addActionListener(new ActionListener() {
/*      */           public void actionPerformed(ActionEvent e) {
/*  973 */             LogBrokerMonitor.this.requestOpenMRU(e);
/*      */           }
/*      */         });
/*  976 */         menu.add(result);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JMenuItem createExitMI() {
/*  982 */     JMenuItem result = new JMenuItem("Exit");
/*  983 */     result.setMnemonic('x');
/*  984 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  986 */         LogBrokerMonitor.this.requestExit();
/*      */       }
/*      */     });
/*  989 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenu createConfigureMenu() {
/*  993 */     JMenu configureMenu = new JMenu("Configure");
/*  994 */     configureMenu.setMnemonic('c');
/*  995 */     configureMenu.add(createConfigureSave());
/*  996 */     configureMenu.add(createConfigureReset());
/*  997 */     configureMenu.add(createConfigureMaxRecords());
/*      */ 
/*  999 */     return configureMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureSave() {
/* 1003 */     JMenuItem result = new JMenuItem("Save");
/* 1004 */     result.setMnemonic('s');
/* 1005 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1007 */         LogBrokerMonitor.this.saveConfiguration();
/*      */       }
/*      */     });
/* 1011 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureReset() {
/* 1015 */     JMenuItem result = new JMenuItem("Reset");
/* 1016 */     result.setMnemonic('r');
/* 1017 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1019 */         LogBrokerMonitor.this.resetConfiguration();
/*      */       }
/*      */     });
/* 1023 */     return result;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createConfigureMaxRecords() {
/* 1027 */     JMenuItem result = new JMenuItem("Set Max Number of Records");
/* 1028 */     result.setMnemonic('m');
/* 1029 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1031 */         LogBrokerMonitor.this.setMaxRecordConfiguration();
/*      */       }
/*      */     });
/* 1035 */     return result;
/*      */   }
/*      */ 
/*      */   protected void saveConfiguration()
/*      */   {
/* 1040 */     this._configurationManager.save();
/*      */   }
/*      */ 
/*      */   protected void resetConfiguration() {
/* 1044 */     this._configurationManager.reset();
/*      */   }
/*      */ 
/*      */   protected void setMaxRecordConfiguration() {
/* 1048 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Set Max Number of Records", "", 10);
/*      */ 
/* 1051 */     String temp = inputDialog.getText();
/*      */ 
/* 1053 */     if (temp != null)
/*      */       try {
/* 1055 */         setMaxNumberOfLogRecords(Integer.parseInt(temp));
/*      */       } catch (NumberFormatException e) {
/* 1057 */         LogFactor5ErrorDialog error = new LogFactor5ErrorDialog(getBaseFrame(), "'" + temp + "' is an invalid parameter.\nPlease try again.");
/*      */ 
/* 1060 */         setMaxRecordConfiguration();
/*      */       }
/*      */   }
/*      */ 
/*      */   protected JMenu createHelpMenu()
/*      */   {
/* 1067 */     JMenu helpMenu = new JMenu("Help");
/* 1068 */     helpMenu.setMnemonic('h');
/* 1069 */     helpMenu.add(createHelpProperties());
/* 1070 */     return helpMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createHelpProperties() {
/* 1074 */     String title = "LogFactor5 Properties";
/* 1075 */     JMenuItem result = new JMenuItem("LogFactor5 Properties");
/* 1076 */     result.setMnemonic('l');
/* 1077 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1079 */         LogBrokerMonitor.this.showPropertiesDialog("LogFactor5 Properties");
/*      */       }
/*      */     });
/* 1082 */     return result;
/*      */   }
/*      */ 
/*      */   protected void showPropertiesDialog(String title) {
/* 1086 */     JOptionPane.showMessageDialog(this._logMonitorFrame, this._displayedLogBrokerProperties.toArray(), title, -1);
/*      */   }
/*      */ 
/*      */   protected JMenu createEditMenu()
/*      */   {
/* 1095 */     JMenu editMenu = new JMenu("Edit");
/* 1096 */     editMenu.setMnemonic('e');
/* 1097 */     editMenu.add(createEditFindMI());
/* 1098 */     editMenu.add(createEditFindNextMI());
/* 1099 */     editMenu.addSeparator();
/* 1100 */     editMenu.add(createEditSortNDCMI());
/* 1101 */     editMenu.add(createEditRestoreAllNDCMI());
/* 1102 */     return editMenu;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindNextMI() {
/* 1106 */     JMenuItem editFindNextMI = new JMenuItem("Find Next");
/* 1107 */     editFindNextMI.setMnemonic('n');
/* 1108 */     editFindNextMI.setAccelerator(KeyStroke.getKeyStroke("F3"));
/* 1109 */     editFindNextMI.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1111 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1114 */     return editFindNextMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditFindMI() {
/* 1118 */     JMenuItem editFindMI = new JMenuItem("Find");
/* 1119 */     editFindMI.setMnemonic('f');
/* 1120 */     editFindMI.setAccelerator(KeyStroke.getKeyStroke("control F"));
/*      */ 
/* 1122 */     editFindMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1125 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Find text: ", "Search Record Messages", 3);
/*      */ 
/* 1132 */         LogBrokerMonitor.this.setSearchText(inputValue);
/* 1133 */         LogBrokerMonitor.this.findSearchText();
/*      */       }
/*      */     });
/* 1138 */     return editFindMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditSortNDCMI()
/*      */   {
/* 1145 */     JMenuItem editSortNDCMI = new JMenuItem("Sort by NDC");
/* 1146 */     editSortNDCMI.setMnemonic('s');
/* 1147 */     editSortNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1150 */         String inputValue = JOptionPane.showInputDialog(LogBrokerMonitor.this._logMonitorFrame, "Sort by this NDC: ", "Sort Log Records by NDC", 3);
/*      */ 
/* 1157 */         LogBrokerMonitor.this.setNDCTextFilter(inputValue);
/* 1158 */         LogBrokerMonitor.this.sortByNDC();
/* 1159 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1160 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1165 */     return editSortNDCMI;
/*      */   }
/*      */ 
/*      */   protected JMenuItem createEditRestoreAllNDCMI()
/*      */   {
/* 1171 */     JMenuItem editRestoreAllNDCMI = new JMenuItem("Restore all NDCs");
/* 1172 */     editRestoreAllNDCMI.setMnemonic('r');
/* 1173 */     editRestoreAllNDCMI.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1176 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().setLogRecordFilter(LogBrokerMonitor.this.createLogRecordFilter());
/*      */ 
/* 1178 */         LogBrokerMonitor.this.setNDCTextFilter("");
/* 1179 */         LogBrokerMonitor.this._table.getFilteredLogTableModel().refresh();
/* 1180 */         LogBrokerMonitor.this.updateStatusLabel();
/*      */       }
/*      */     });
/* 1184 */     return editRestoreAllNDCMI;
/*      */   }
/*      */ 
/*      */   protected JToolBar createToolBar() {
/* 1188 */     JToolBar tb = new JToolBar();
/* 1189 */     tb.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
/* 1190 */     JComboBox fontCombo = new JComboBox();
/* 1191 */     JComboBox fontSizeCombo = new JComboBox();
/* 1192 */     this._fontSizeCombo = fontSizeCombo;
/*      */ 
/* 1194 */     ClassLoader cl = getClass().getClassLoader();
/* 1195 */     if (cl == null) {
/* 1196 */       cl = ClassLoader.getSystemClassLoader();
/*      */     }
/* 1198 */     URL newIconURL = cl.getResource("org/apache/log4j/lf5/viewer/images/channelexplorer_new.gif");
/*      */ 
/* 1201 */     ImageIcon newIcon = null;
/*      */ 
/* 1203 */     if (newIconURL != null) {
/* 1204 */       newIcon = new ImageIcon(newIconURL);
/*      */     }
/*      */ 
/* 1207 */     JButton newButton = new JButton("Clear Log Table");
/*      */ 
/* 1209 */     if (newIcon != null) {
/* 1210 */       newButton.setIcon(newIcon);
/*      */     }
/*      */ 
/* 1213 */     newButton.setToolTipText("Clear Log Table.");
/*      */ 
/* 1216 */     newButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1219 */         LogBrokerMonitor.this._table.clearLogRecords();
/* 1220 */         LogBrokerMonitor.this._categoryExplorerTree.getExplorerModel().resetAllNodeCounts();
/* 1221 */         LogBrokerMonitor.this.updateStatusLabel();
/* 1222 */         LogBrokerMonitor.this.clearDetailTextArea();
/* 1223 */         LogRecord.resetSequenceNumber();
/*      */       }
/*      */     });
/* 1228 */     Toolkit tk = Toolkit.getDefaultToolkit();
/*      */     String[] fonts;
/* 1233 */     if (this._loadSystemFonts) {
/* 1234 */       fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
/*      */     }
/*      */     else {
/* 1237 */       fonts = tk.getFontList();
/*      */     }
/*      */ 
/* 1240 */     for (int j = 0; j < fonts.length; j++) {
/* 1241 */       fontCombo.addItem(fonts[j]);
/*      */     }
/*      */ 
/* 1244 */     fontCombo.setSelectedItem(this._fontName);
/*      */ 
/* 1246 */     fontCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1250 */         JComboBox box = (JComboBox)e.getSource();
/* 1251 */         String font = (String)box.getSelectedItem();
/* 1252 */         LogBrokerMonitor.this._table.setFont(new Font(font, 0, LogBrokerMonitor.this._fontSize));
/* 1253 */         LogBrokerMonitor.this._fontName = font;
/*      */       }
/*      */     });
/* 1258 */     fontSizeCombo.addItem("8");
/* 1259 */     fontSizeCombo.addItem("9");
/* 1260 */     fontSizeCombo.addItem("10");
/* 1261 */     fontSizeCombo.addItem("12");
/* 1262 */     fontSizeCombo.addItem("14");
/* 1263 */     fontSizeCombo.addItem("16");
/* 1264 */     fontSizeCombo.addItem("18");
/* 1265 */     fontSizeCombo.addItem("24");
/*      */ 
/* 1267 */     fontSizeCombo.setSelectedItem(String.valueOf(this._fontSize));
/* 1268 */     fontSizeCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1271 */         JComboBox box = (JComboBox)e.getSource();
/* 1272 */         String size = (String)box.getSelectedItem();
/* 1273 */         int s = Integer.valueOf(size).intValue();
/*      */ 
/* 1275 */         LogBrokerMonitor.this.setFontSizeSilently(s);
/* 1276 */         LogBrokerMonitor.this.refreshDetailTextArea();
/* 1277 */         LogBrokerMonitor.this._fontSize = s;
/*      */       }
/*      */     });
/* 1282 */     tb.add(new JLabel(" Font: "));
/* 1283 */     tb.add(fontCombo);
/* 1284 */     tb.add(fontSizeCombo);
/* 1285 */     tb.addSeparator();
/* 1286 */     tb.addSeparator();
/* 1287 */     tb.add(newButton);
/*      */ 
/* 1289 */     newButton.setAlignmentY(0.5F);
/* 1290 */     newButton.setAlignmentX(0.5F);
/*      */ 
/* 1292 */     fontCombo.setMaximumSize(fontCombo.getPreferredSize());
/* 1293 */     fontSizeCombo.setMaximumSize(fontSizeCombo.getPreferredSize());
/*      */ 
/* 1296 */     return tb;
/*      */   }
/*      */ 
/*      */   protected void setView(String viewString, LogTable table)
/*      */   {
/* 1314 */     if ("Detailed".equals(viewString)) {
/* 1315 */       table.setDetailedView();
/*      */     } else {
/* 1317 */       String message = viewString + "does not match a supported view.";
/* 1318 */       throw new IllegalArgumentException(message);
/*      */     }
/* 1320 */     this._currentView = viewString;
/*      */   }
/*      */ 
/*      */   protected JComboBox createLogLevelCombo() {
/* 1324 */     JComboBox result = new JComboBox();
/* 1325 */     Iterator levels = getLogLevels();
/* 1326 */     while (levels.hasNext()) {
/* 1327 */       result.addItem(levels.next());
/*      */     }
/* 1329 */     result.setSelectedItem(this._leastSevereDisplayedLogLevel);
/*      */ 
/* 1331 */     result.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1333 */         JComboBox box = (JComboBox)e.getSource();
/* 1334 */         LogLevel level = (LogLevel)box.getSelectedItem();
/* 1335 */         LogBrokerMonitor.this.setLeastSevereDisplayedLogLevel(level);
/*      */       }
/*      */     });
/* 1338 */     result.setMaximumSize(result.getPreferredSize());
/* 1339 */     return result;
/*      */   }
/*      */ 
/*      */   protected void setLeastSevereDisplayedLogLevel(LogLevel level) {
/* 1343 */     if ((level == null) || (this._leastSevereDisplayedLogLevel == level)) {
/* 1344 */       return;
/*      */     }
/* 1346 */     this._leastSevereDisplayedLogLevel = level;
/* 1347 */     this._table.getFilteredLogTableModel().refresh();
/* 1348 */     updateStatusLabel();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void trackTableScrollPane()
/*      */   {
/*      */   }
/*      */ 
/*      */   protected void centerFrame(JFrame frame)
/*      */   {
/* 1366 */     Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
/* 1367 */     Dimension comp = frame.getSize();
/*      */ 
/* 1369 */     frame.setLocation((screen.width - comp.width) / 2, (screen.height - comp.height) / 2);
/*      */   }
/*      */ 
/*      */   protected void requestOpen()
/*      */   {
/*      */     JFileChooser chooser;
/* 1381 */     if (this._fileLocation == null)
/* 1382 */       chooser = new JFileChooser();
/*      */     else {
/* 1384 */       chooser = new JFileChooser(this._fileLocation);
/*      */     }
/*      */ 
/* 1387 */     int returnVal = chooser.showOpenDialog(this._logMonitorFrame);
/* 1388 */     if (returnVal == 0) {
/* 1389 */       File f = chooser.getSelectedFile();
/* 1390 */       if (loadLogFile(f)) {
/* 1391 */         this._fileLocation = chooser.getSelectedFile();
/* 1392 */         this._mruFileManager.set(f);
/* 1393 */         updateMRUList();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestOpenURL()
/*      */   {
/* 1403 */     LogFactor5InputDialog inputDialog = new LogFactor5InputDialog(getBaseFrame(), "Open URL", "URL:");
/*      */ 
/* 1405 */     String temp = inputDialog.getText();
/*      */     LogFactor5ErrorDialog error;
/* 1407 */     if (temp != null) {
/* 1408 */       if (temp.indexOf("://") == -1) {
/* 1409 */         temp = "http://" + temp;
/*      */       }
/*      */       try
/*      */       {
/* 1413 */         URL url = new URL(temp);
/* 1414 */         if (loadLogFile(url)) {
/* 1415 */           this._mruFileManager.set(url);
/* 1416 */           updateMRUList();
/*      */         }
/*      */       } catch (MalformedURLException e) {
/* 1419 */         error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL.");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void updateMRUList()
/*      */   {
/* 1430 */     JMenu menu = this._logMonitorFrame.getJMenuBar().getMenu(0);
/* 1431 */     menu.removeAll();
/* 1432 */     menu.add(createOpenMI());
/* 1433 */     menu.add(createOpenURLMI());
/* 1434 */     menu.addSeparator();
/* 1435 */     menu.add(createCloseMI());
/* 1436 */     createMRUFileListMI(menu);
/* 1437 */     menu.addSeparator();
/* 1438 */     menu.add(createExitMI());
/*      */   }
/*      */ 
/*      */   protected void requestClose() {
/* 1442 */     setCallSystemExitOnClose(false);
/* 1443 */     closeAfterConfirm(); } 
/* 1450 */   protected void requestOpenMRU(ActionEvent e) { String file = e.getActionCommand();
/* 1451 */     StringTokenizer st = new StringTokenizer(file);
/* 1452 */     String num = st.nextToken().trim();
/* 1453 */     file = st.nextToken("\n");
/*      */     LogFactor5ErrorDialog error;
/*      */     try { int index = Integer.parseInt(num) - 1;
/*      */ 
/* 1458 */       InputStream in = this._mruFileManager.getInputStream(index);
/* 1459 */       LogFileParser lfp = new LogFileParser(in);
/* 1460 */       lfp.parse(this);
/*      */ 
/* 1462 */       this._mruFileManager.moveToTop(index);
/* 1463 */       updateMRUList();
/*      */     } catch (Exception me)
/*      */     {
/* 1466 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Unable to load file " + file);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void requestExit()
/*      */   {
/* 1473 */     this._mruFileManager.save();
/* 1474 */     setCallSystemExitOnClose(true);
/* 1475 */     closeAfterConfirm();
/*      */   }
/*      */ 
/*      */   protected void closeAfterConfirm() {
/* 1479 */     StringBuffer message = new StringBuffer();
/*      */ 
/* 1481 */     if (!this._callSystemExitOnClose) {
/* 1482 */       message.append("Are you sure you want to close the logging ");
/* 1483 */       message.append("console?\n");
/* 1484 */       message.append("(Note: This will not shut down the Virtual Machine,\n");
/* 1485 */       message.append("or the Swing event thread.)");
/*      */     } else {
/* 1487 */       message.append("Are you sure you want to exit?\n");
/* 1488 */       message.append("This will shut down the Virtual Machine.\n");
/*      */     }
/*      */ 
/* 1491 */     String title = "Are you sure you want to dispose of the Logging Console?";
/*      */ 
/* 1494 */     if (this._callSystemExitOnClose == true) {
/* 1495 */       title = "Are you sure you want to exit?";
/*      */     }
/* 1497 */     int value = JOptionPane.showConfirmDialog(this._logMonitorFrame, message.toString(), title, 2, 3, null);
/*      */ 
/* 1506 */     if (value == 0)
/* 1507 */       dispose();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogLevels()
/*      */   {
/* 1512 */     return this._levels.iterator();
/*      */   }
/*      */ 
/*      */   protected Iterator getLogTableColumns() {
/* 1516 */     return this._columns.iterator();
/*      */   }
/*      */   protected boolean loadLogFile(File file) {
/* 1523 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try { LogFileParser lfp = new LogFileParser(file);
/* 1526 */       lfp.parse(this);
/* 1527 */       ok = true;
/*      */     } catch (IOException e) {
/* 1529 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading " + file.getName());
/*      */     }
/*      */ 
/* 1533 */     return ok;
/*      */   }
/*      */   protected boolean loadLogFile(URL url) {
/* 1540 */     boolean ok = false;
/*      */     LogFactor5ErrorDialog error;
/*      */     try { LogFileParser lfp = new LogFileParser(url.openStream());
/* 1543 */       lfp.parse(this);
/* 1544 */       ok = true;
/*      */     } catch (IOException e) {
/* 1546 */       error = new LogFactor5ErrorDialog(getBaseFrame(), "Error reading URL:" + url.getFile());
/*      */     }
/*      */ 
/* 1549 */     return ok;
/*      */   }
/*      */ 
/*      */   class LogBrokerMonitorWindowAdaptor extends WindowAdapter
/*      */   {
/*      */     protected LogBrokerMonitor _monitor;
/*      */ 
/*      */     public LogBrokerMonitorWindowAdaptor(LogBrokerMonitor monitor)
/*      */     {
/* 1563 */       this._monitor = monitor;
/*      */     }
/*      */ 
/*      */     public void windowClosing(WindowEvent ev) {
/* 1567 */       this._monitor.requestClose();
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.LogBrokerMonitor
 * JD-Core Version:    0.6.0
 */
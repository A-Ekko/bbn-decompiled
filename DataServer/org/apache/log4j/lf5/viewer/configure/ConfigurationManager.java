/*     */ package org.apache.log4j.lf5.viewer.configure;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JCheckBoxMenuItem;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogLevelFormatException;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.lf5.viewer.LogTable;
/*     */ import org.apache.log4j.lf5.viewer.LogTableColumn;
/*     */ import org.apache.log4j.lf5.viewer.LogTableColumnFormatException;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerModel;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryExplorerTree;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNode;
/*     */ import org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class ConfigurationManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "lf5_configuration.xml";
/*     */   private static final String NAME = "name";
/*     */   private static final String PATH = "path";
/*     */   private static final String SELECTED = "selected";
/*     */   private static final String EXPANDED = "expanded";
/*     */   private static final String CATEGORY = "category";
/*     */   private static final String FIRST_CATEGORY_NAME = "Categories";
/*     */   private static final String LEVEL = "level";
/*     */   private static final String COLORLEVEL = "colorlevel";
/*     */   private static final String COLOR = "color";
/*     */   private static final String RED = "red";
/*     */   private static final String GREEN = "green";
/*     */   private static final String BLUE = "blue";
/*     */   private static final String COLUMN = "column";
/*     */   private static final String NDCTEXTFILTER = "searchtext";
/*  73 */   private LogBrokerMonitor _monitor = null;
/*  74 */   private LogTable _table = null;
/*     */ 
/*     */   public ConfigurationManager(LogBrokerMonitor monitor, LogTable table)
/*     */   {
/*  81 */     this._monitor = monitor;
/*  82 */     this._table = table;
/*  83 */     load();
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  90 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/*  91 */     CategoryNode root = model.getRootCategoryNode();
/*     */ 
/*  93 */     StringBuffer xml = new StringBuffer(2048);
/*  94 */     openXMLDocument(xml);
/*  95 */     openConfigurationXML(xml);
/*  96 */     processLogRecordFilter(this._monitor.getNDCTextFilter(), xml);
/*  97 */     processLogLevels(this._monitor.getLogLevelMenuItems(), xml);
/*  98 */     processLogLevelColors(this._monitor.getLogLevelMenuItems(), LogLevel.getLogLevelColorMap(), xml);
/*     */ 
/* 100 */     processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);
/* 101 */     processConfigurationNode(root, xml);
/* 102 */     closeConfigurationXML(xml);
/* 103 */     store(xml.toString());
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 107 */     deleteConfigurationFile();
/* 108 */     collapseTree();
/* 109 */     selectAllNodes();
/*     */   }
/*     */ 
/*     */   public static String treePathToString(TreePath path)
/*     */   {
/* 114 */     StringBuffer sb = new StringBuffer();
/* 115 */     CategoryNode n = null;
/* 116 */     Object[] objects = path.getPath();
/* 117 */     for (int i = 1; i < objects.length; i++) {
/* 118 */       n = (CategoryNode)objects[i];
/* 119 */       if (i > 1) {
/* 120 */         sb.append(".");
/*     */       }
/* 122 */       sb.append(n.getTitle());
/*     */     }
/* 124 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 131 */     File file = new File(getFilename());
/* 132 */     if (file.exists())
/*     */       try {
/* 134 */         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 136 */         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 137 */         Document doc = docBuilder.parse(file);
/* 138 */         processRecordFilter(doc);
/* 139 */         processCategories(doc);
/* 140 */         processLogLevels(doc);
/* 141 */         processLogLevelColors(doc);
/* 142 */         processLogTableColumns(doc);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 146 */         System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void processRecordFilter(Document doc)
/*     */   {
/* 159 */     NodeList nodeList = doc.getElementsByTagName("searchtext");
/*     */ 
/* 162 */     Node n = nodeList.item(0);
/*     */ 
/* 165 */     if (n == null) {
/* 166 */       return;
/*     */     }
/*     */ 
/* 169 */     NamedNodeMap map = n.getAttributes();
/* 170 */     String text = getValue(map, "name");
/*     */ 
/* 172 */     if ((text == null) || (text.equals(""))) {
/* 173 */       return;
/*     */     }
/* 175 */     this._monitor.setNDCLogRecordFilter(text);
/*     */   }
/*     */ 
/*     */   protected void processCategories(Document doc) {
/* 179 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 180 */     CategoryExplorerModel model = tree.getExplorerModel();
/* 181 */     NodeList nodeList = doc.getElementsByTagName("category");
/*     */ 
/* 184 */     NamedNodeMap map = nodeList.item(0).getAttributes();
/* 185 */     int j = getValue(map, "name").equalsIgnoreCase("Categories") ? 1 : 0;
/*     */ 
/* 188 */     for (int i = nodeList.getLength() - 1; i >= j; i--) {
/* 189 */       Node n = nodeList.item(i);
/* 190 */       map = n.getAttributes();
/* 191 */       CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
/* 192 */       chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/* 193 */       if (getValue(map, "expanded").equalsIgnoreCase("true"));
/* 194 */       tree.expandPath(model.getTreePathToRoot(chnode));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevels(Document doc)
/*     */   {
/* 200 */     NodeList nodeList = doc.getElementsByTagName("level");
/* 201 */     Map menuItems = this._monitor.getLogLevelMenuItems();
/*     */ 
/* 203 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 204 */       Node n = nodeList.item(i);
/* 205 */       NamedNodeMap map = n.getAttributes();
/* 206 */       String name = getValue(map, "name");
/*     */       try {
/* 208 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(LogLevel.valueOf(name));
/*     */ 
/* 210 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */       }
/*     */       catch (LogLevelFormatException e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevelColors(Document doc) {
/* 218 */     NodeList nodeList = doc.getElementsByTagName("colorlevel");
/* 219 */     Map logLevelColors = LogLevel.getLogLevelColorMap();
/*     */ 
/* 221 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 222 */       Node n = nodeList.item(i);
/*     */ 
/* 225 */       if (n == null) {
/* 226 */         return;
/*     */       }
/*     */ 
/* 229 */       NamedNodeMap map = n.getAttributes();
/* 230 */       String name = getValue(map, "name");
/*     */       try {
/* 232 */         LogLevel level = LogLevel.valueOf(name);
/* 233 */         int red = Integer.parseInt(getValue(map, "red"));
/* 234 */         int green = Integer.parseInt(getValue(map, "green"));
/* 235 */         int blue = Integer.parseInt(getValue(map, "blue"));
/* 236 */         Color c = new Color(red, green, blue);
/* 237 */         if (level != null)
/* 238 */           level.setLogLevelColorMap(level, c);
/*     */       }
/*     */       catch (LogLevelFormatException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogTableColumns(Document doc)
/*     */   {
/* 248 */     NodeList nodeList = doc.getElementsByTagName("column");
/* 249 */     Map menuItems = this._monitor.getLogTableColumnMenuItems();
/* 250 */     List selectedColumns = new ArrayList();
/* 251 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 252 */       Node n = nodeList.item(i);
/*     */ 
/* 255 */       if (n == null) {
/* 256 */         return;
/*     */       }
/* 258 */       NamedNodeMap map = n.getAttributes();
/* 259 */       String name = getValue(map, "name");
/*     */       try {
/* 261 */         LogTableColumn column = LogTableColumn.valueOf(name);
/* 262 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(column);
/*     */ 
/* 264 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */ 
/* 266 */         if (item.isSelected()) {
/* 267 */           selectedColumns.add(column);
/*     */         }
/*     */       }
/*     */       catch (LogTableColumnFormatException e)
/*     */       {
/*     */       }
/* 273 */       if (selectedColumns.isEmpty())
/* 274 */         this._table.setDetailedView();
/*     */       else
/* 276 */         this._table.setView(selectedColumns);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getValue(NamedNodeMap map, String attr)
/*     */   {
/* 283 */     Node n = map.getNamedItem(attr);
/* 284 */     return n.getNodeValue();
/*     */   }
/*     */ 
/*     */   protected void collapseTree()
/*     */   {
/* 289 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 290 */     for (int i = tree.getRowCount() - 1; i > 0; i--)
/* 291 */       tree.collapseRow(i);
/*     */   }
/*     */ 
/*     */   protected void selectAllNodes()
/*     */   {
/* 296 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/* 297 */     CategoryNode root = model.getRootCategoryNode();
/* 298 */     Enumeration all = root.breadthFirstEnumeration();
/* 299 */     CategoryNode n = null;
/* 300 */     while (all.hasMoreElements()) {
/* 301 */       n = (CategoryNode)all.nextElement();
/* 302 */       n.setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void store(String s)
/*     */   {
/*     */     try {
/* 309 */       PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
/* 310 */       writer.print(s);
/* 311 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 314 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void deleteConfigurationFile()
/*     */   {
/*     */     try {
/* 321 */       File f = new File(getFilename());
/* 322 */       if (f.exists())
/* 323 */         f.delete();
/*     */     }
/*     */     catch (SecurityException e) {
/* 326 */       System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 332 */     String home = System.getProperty("user.home");
/* 333 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 335 */     return home + sep + "lf5" + sep + "lf5_configuration.xml";
/*     */   }
/*     */ 
/*     */   private void processConfigurationNode(CategoryNode node, StringBuffer xml)
/*     */   {
/* 342 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/*     */ 
/* 344 */     Enumeration all = node.breadthFirstEnumeration();
/* 345 */     CategoryNode n = null;
/* 346 */     while (all.hasMoreElements()) {
/* 347 */       n = (CategoryNode)all.nextElement();
/* 348 */       exportXMLElement(n, model.getTreePathToRoot(n), xml);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processLogLevels(Map logLevelMenuItems, StringBuffer xml)
/*     */   {
/* 354 */     xml.append("\t<loglevels>\r\n");
/* 355 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 356 */     while (it.hasNext()) {
/* 357 */       LogLevel level = (LogLevel)it.next();
/* 358 */       JCheckBoxMenuItem item = (JCheckBoxMenuItem)logLevelMenuItems.get(level);
/* 359 */       exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 362 */     xml.append("\t</loglevels>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogLevelColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
/* 366 */     xml.append("\t<loglevelcolors>\r\n");
/*     */ 
/* 368 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 369 */     while (it.hasNext()) {
/* 370 */       LogLevel level = (LogLevel)it.next();
/*     */ 
/* 372 */       Color color = (Color)logLevelColors.get(level);
/* 373 */       exportLogLevelColorXMLElement(level.getLabel(), color, xml);
/*     */     }
/*     */ 
/* 376 */     xml.append("\t</loglevelcolors>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml)
/*     */   {
/* 381 */     xml.append("\t<logtablecolumns>\r\n");
/* 382 */     Iterator it = logTableColumnMenuItems.iterator();
/* 383 */     while (it.hasNext()) {
/* 384 */       LogTableColumn column = (LogTableColumn)it.next();
/* 385 */       JCheckBoxMenuItem item = this._monitor.getTableColumnMenuItem(column);
/* 386 */       exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 389 */     xml.append("\t</logtablecolumns>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogRecordFilter(String text, StringBuffer xml)
/*     */   {
/* 395 */     xml.append("\t<").append("searchtext").append(" ");
/* 396 */     xml.append("name").append("=\"").append(text).append("\"");
/* 397 */     xml.append("/>\r\n");
/*     */   }
/*     */ 
/*     */   private void openXMLDocument(StringBuffer xml) {
/* 401 */     xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
/*     */   }
/*     */ 
/*     */   private void openConfigurationXML(StringBuffer xml) {
/* 405 */     xml.append("<configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void closeConfigurationXML(StringBuffer xml) {
/* 409 */     xml.append("</configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
/* 413 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/*     */ 
/* 415 */     xml.append("\t<").append("category").append(" ");
/* 416 */     xml.append("name").append("=\"").append(node.getTitle()).append("\" ");
/* 417 */     xml.append("path").append("=\"").append(treePathToString(path)).append("\" ");
/* 418 */     xml.append("expanded").append("=\"").append(tree.isExpanded(path)).append("\" ");
/* 419 */     xml.append("selected").append("=\"").append(node.isSelected()).append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
/* 423 */     xml.append("\t\t<").append("level").append(" ").append("name");
/* 424 */     xml.append("=\"").append(label).append("\" ");
/* 425 */     xml.append("selected").append("=\"").append(selected);
/* 426 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
/* 430 */     xml.append("\t\t<").append("colorlevel").append(" ").append("name");
/* 431 */     xml.append("=\"").append(label).append("\" ");
/* 432 */     xml.append("red").append("=\"").append(color.getRed()).append("\" ");
/* 433 */     xml.append("green").append("=\"").append(color.getGreen()).append("\" ");
/* 434 */     xml.append("blue").append("=\"").append(color.getBlue());
/* 435 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogTableColumnXMLElement(String label, boolean selected, StringBuffer xml) {
/* 439 */     xml.append("\t\t<").append("column").append(" ").append("name");
/* 440 */     xml.append("=\"").append(label).append("\" ");
/* 441 */     xml.append("selected").append("=\"").append(selected);
/* 442 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.ConfigurationManager
 * JD-Core Version:    0.6.0
 */
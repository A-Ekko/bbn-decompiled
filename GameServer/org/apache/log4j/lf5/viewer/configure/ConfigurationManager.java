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
/*     */   private static final String RED = "red";
/*     */   private static final String GREEN = "green";
/*     */   private static final String BLUE = "blue";
/*     */   private static final String COLUMN = "column";
/*     */   private static final String NDCTEXTFILTER = "searchtext";
/*  81 */   private LogBrokerMonitor _monitor = null;
/*  82 */   private LogTable _table = null;
/*     */ 
/*     */   public ConfigurationManager(LogBrokerMonitor monitor, LogTable table)
/*     */   {
/*  89 */     this._monitor = monitor;
/*  90 */     this._table = table;
/*  91 */     load();
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  98 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/*  99 */     CategoryNode root = model.getRootCategoryNode();
/*     */ 
/* 101 */     StringBuffer xml = new StringBuffer(2048);
/* 102 */     openXMLDocument(xml);
/* 103 */     openConfigurationXML(xml);
/* 104 */     processLogRecordFilter(this._monitor.getNDCTextFilter(), xml);
/* 105 */     processLogLevels(this._monitor.getLogLevelMenuItems(), xml);
/* 106 */     processLogLevelColors(this._monitor.getLogLevelMenuItems(), LogLevel.getLogLevelColorMap(), xml);
/*     */ 
/* 108 */     processLogTableColumns(LogTableColumn.getLogTableColumns(), xml);
/* 109 */     processConfigurationNode(root, xml);
/* 110 */     closeConfigurationXML(xml);
/* 111 */     store(xml.toString());
/*     */   }
/*     */ 
/*     */   public void reset() {
/* 115 */     deleteConfigurationFile();
/* 116 */     collapseTree();
/* 117 */     selectAllNodes();
/*     */   }
/*     */ 
/*     */   public static String treePathToString(TreePath path)
/*     */   {
/* 122 */     StringBuffer sb = new StringBuffer();
/* 123 */     CategoryNode n = null;
/* 124 */     Object[] objects = path.getPath();
/* 125 */     for (int i = 1; i < objects.length; i++) {
/* 126 */       n = (CategoryNode)objects[i];
/* 127 */       if (i > 1) {
/* 128 */         sb.append(".");
/*     */       }
/* 130 */       sb.append(n.getTitle());
/*     */     }
/* 132 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 139 */     File file = new File(getFilename());
/* 140 */     if (file.exists())
/*     */       try {
/* 142 */         DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/* 144 */         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/* 145 */         Document doc = docBuilder.parse(file);
/* 146 */         processRecordFilter(doc);
/* 147 */         processCategories(doc);
/* 148 */         processLogLevels(doc);
/* 149 */         processLogLevelColors(doc);
/* 150 */         processLogTableColumns(doc);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 154 */         System.err.println("Unable process configuration file at " + getFilename() + ". Error Message=" + e.getMessage());
/*     */       }
/*     */   }
/*     */ 
/*     */   protected void processRecordFilter(Document doc)
/*     */   {
/* 167 */     NodeList nodeList = doc.getElementsByTagName("searchtext");
/*     */ 
/* 170 */     Node n = nodeList.item(0);
/*     */ 
/* 173 */     if (n == null) {
/* 174 */       return;
/*     */     }
/*     */ 
/* 177 */     NamedNodeMap map = n.getAttributes();
/* 178 */     String text = getValue(map, "name");
/*     */ 
/* 180 */     if ((text == null) || (text.equals(""))) {
/* 181 */       return;
/*     */     }
/* 183 */     this._monitor.setNDCLogRecordFilter(text);
/*     */   }
/*     */ 
/*     */   protected void processCategories(Document doc) {
/* 187 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 188 */     CategoryExplorerModel model = tree.getExplorerModel();
/* 189 */     NodeList nodeList = doc.getElementsByTagName("category");
/*     */ 
/* 192 */     NamedNodeMap map = nodeList.item(0).getAttributes();
/* 193 */     int j = getValue(map, "name").equalsIgnoreCase("Categories") ? 1 : 0;
/*     */ 
/* 196 */     for (int i = nodeList.getLength() - 1; i >= j; i--) {
/* 197 */       Node n = nodeList.item(i);
/* 198 */       map = n.getAttributes();
/* 199 */       CategoryNode chnode = model.addCategory(new CategoryPath(getValue(map, "path")));
/* 200 */       chnode.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/* 201 */       if (getValue(map, "expanded").equalsIgnoreCase("true"));
/* 202 */       tree.expandPath(model.getTreePathToRoot(chnode));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevels(Document doc)
/*     */   {
/* 208 */     NodeList nodeList = doc.getElementsByTagName("level");
/* 209 */     Map menuItems = this._monitor.getLogLevelMenuItems();
/*     */ 
/* 211 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 212 */       Node n = nodeList.item(i);
/* 213 */       NamedNodeMap map = n.getAttributes();
/* 214 */       String name = getValue(map, "name");
/*     */       try {
/* 216 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(LogLevel.valueOf(name));
/*     */ 
/* 218 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */       }
/*     */       catch (LogLevelFormatException e) {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogLevelColors(Document doc) {
/* 226 */     NodeList nodeList = doc.getElementsByTagName("colorlevel");
/* 227 */     LogLevel.getLogLevelColorMap();
/*     */ 
/* 229 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 230 */       Node n = nodeList.item(i);
/*     */ 
/* 233 */       if (n == null) {
/* 234 */         return;
/*     */       }
/*     */ 
/* 237 */       NamedNodeMap map = n.getAttributes();
/* 238 */       String name = getValue(map, "name");
/*     */       try {
/* 240 */         LogLevel level = LogLevel.valueOf(name);
/* 241 */         int red = Integer.parseInt(getValue(map, "red"));
/* 242 */         int green = Integer.parseInt(getValue(map, "green"));
/* 243 */         int blue = Integer.parseInt(getValue(map, "blue"));
/* 244 */         Color c = new Color(red, green, blue);
/* 245 */         if (level != null)
/* 246 */           level.setLogLevelColorMap(level, c);
/*     */       }
/*     */       catch (LogLevelFormatException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void processLogTableColumns(Document doc)
/*     */   {
/* 256 */     NodeList nodeList = doc.getElementsByTagName("column");
/* 257 */     Map menuItems = this._monitor.getLogTableColumnMenuItems();
/* 258 */     List selectedColumns = new ArrayList();
/* 259 */     for (int i = 0; i < nodeList.getLength(); i++) {
/* 260 */       Node n = nodeList.item(i);
/*     */ 
/* 263 */       if (n == null) {
/* 264 */         return;
/*     */       }
/* 266 */       NamedNodeMap map = n.getAttributes();
/* 267 */       String name = getValue(map, "name");
/*     */       try {
/* 269 */         LogTableColumn column = LogTableColumn.valueOf(name);
/* 270 */         JCheckBoxMenuItem item = (JCheckBoxMenuItem)menuItems.get(column);
/*     */ 
/* 272 */         item.setSelected(getValue(map, "selected").equalsIgnoreCase("true"));
/*     */ 
/* 274 */         if (item.isSelected()) {
/* 275 */           selectedColumns.add(column);
/*     */         }
/*     */       }
/*     */       catch (LogTableColumnFormatException e)
/*     */       {
/*     */       }
/* 281 */       if (selectedColumns.isEmpty())
/* 282 */         this._table.setDetailedView();
/*     */       else
/* 284 */         this._table.setView(selectedColumns);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getValue(NamedNodeMap map, String attr)
/*     */   {
/* 291 */     Node n = map.getNamedItem(attr);
/* 292 */     return n.getNodeValue();
/*     */   }
/*     */ 
/*     */   protected void collapseTree()
/*     */   {
/* 297 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/* 298 */     for (int i = tree.getRowCount() - 1; i > 0; i--)
/* 299 */       tree.collapseRow(i);
/*     */   }
/*     */ 
/*     */   protected void selectAllNodes()
/*     */   {
/* 304 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/* 305 */     CategoryNode root = model.getRootCategoryNode();
/* 306 */     Enumeration all = root.breadthFirstEnumeration();
/* 307 */     CategoryNode n = null;
/* 308 */     while (all.hasMoreElements()) {
/* 309 */       n = (CategoryNode)all.nextElement();
/* 310 */       n.setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void store(String s)
/*     */   {
/*     */     try {
/* 317 */       PrintWriter writer = new PrintWriter(new FileWriter(getFilename()));
/* 318 */       writer.print(s);
/* 319 */       writer.close();
/*     */     }
/*     */     catch (IOException e) {
/* 322 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void deleteConfigurationFile()
/*     */   {
/*     */     try {
/* 329 */       File f = new File(getFilename());
/* 330 */       if (f.exists())
/* 331 */         f.delete();
/*     */     }
/*     */     catch (SecurityException e) {
/* 334 */       System.err.println("Cannot delete " + getFilename() + " because a security violation occured.");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 340 */     String home = System.getProperty("user.home");
/* 341 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 343 */     return home + sep + "lf5" + sep + "lf5_configuration.xml";
/*     */   }
/*     */ 
/*     */   private void processConfigurationNode(CategoryNode node, StringBuffer xml)
/*     */   {
/* 350 */     CategoryExplorerModel model = this._monitor.getCategoryExplorerTree().getExplorerModel();
/*     */ 
/* 352 */     Enumeration all = node.breadthFirstEnumeration();
/* 353 */     CategoryNode n = null;
/* 354 */     while (all.hasMoreElements()) {
/* 355 */       n = (CategoryNode)all.nextElement();
/* 356 */       exportXMLElement(n, model.getTreePathToRoot(n), xml);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processLogLevels(Map logLevelMenuItems, StringBuffer xml)
/*     */   {
/* 362 */     xml.append("\t<loglevels>\r\n");
/* 363 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 364 */     while (it.hasNext()) {
/* 365 */       LogLevel level = (LogLevel)it.next();
/* 366 */       JCheckBoxMenuItem item = (JCheckBoxMenuItem)logLevelMenuItems.get(level);
/* 367 */       exportLogLevelXMLElement(level.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 370 */     xml.append("\t</loglevels>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogLevelColors(Map logLevelMenuItems, Map logLevelColors, StringBuffer xml) {
/* 374 */     xml.append("\t<loglevelcolors>\r\n");
/*     */ 
/* 376 */     Iterator it = logLevelMenuItems.keySet().iterator();
/* 377 */     while (it.hasNext()) {
/* 378 */       LogLevel level = (LogLevel)it.next();
/*     */ 
/* 380 */       Color color = (Color)logLevelColors.get(level);
/* 381 */       exportLogLevelColorXMLElement(level.getLabel(), color, xml);
/*     */     }
/*     */ 
/* 384 */     xml.append("\t</loglevelcolors>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogTableColumns(List logTableColumnMenuItems, StringBuffer xml)
/*     */   {
/* 389 */     xml.append("\t<logtablecolumns>\r\n");
/* 390 */     Iterator it = logTableColumnMenuItems.iterator();
/* 391 */     while (it.hasNext()) {
/* 392 */       LogTableColumn column = (LogTableColumn)it.next();
/* 393 */       JCheckBoxMenuItem item = this._monitor.getTableColumnMenuItem(column);
/* 394 */       exportLogTableColumnXMLElement(column.getLabel(), item.isSelected(), xml);
/*     */     }
/*     */ 
/* 397 */     xml.append("\t</logtablecolumns>\r\n");
/*     */   }
/*     */ 
/*     */   private void processLogRecordFilter(String text, StringBuffer xml)
/*     */   {
/* 403 */     xml.append("\t<").append("searchtext").append(" ");
/* 404 */     xml.append("name").append("=\"").append(text).append("\"");
/* 405 */     xml.append("/>\r\n");
/*     */   }
/*     */ 
/*     */   private void openXMLDocument(StringBuffer xml) {
/* 409 */     xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n");
/*     */   }
/*     */ 
/*     */   private void openConfigurationXML(StringBuffer xml) {
/* 413 */     xml.append("<configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void closeConfigurationXML(StringBuffer xml) {
/* 417 */     xml.append("</configuration>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportXMLElement(CategoryNode node, TreePath path, StringBuffer xml) {
/* 421 */     CategoryExplorerTree tree = this._monitor.getCategoryExplorerTree();
/*     */ 
/* 423 */     xml.append("\t<").append("category").append(" ");
/* 424 */     xml.append("name").append("=\"").append(node.getTitle()).append("\" ");
/* 425 */     xml.append("path").append("=\"").append(treePathToString(path)).append("\" ");
/* 426 */     xml.append("expanded").append("=\"").append(tree.isExpanded(path)).append("\" ");
/* 427 */     xml.append("selected").append("=\"").append(node.isSelected()).append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelXMLElement(String label, boolean selected, StringBuffer xml) {
/* 431 */     xml.append("\t\t<").append("level").append(" ").append("name");
/* 432 */     xml.append("=\"").append(label).append("\" ");
/* 433 */     xml.append("selected").append("=\"").append(selected);
/* 434 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogLevelColorXMLElement(String label, Color color, StringBuffer xml) {
/* 438 */     xml.append("\t\t<").append("colorlevel").append(" ").append("name");
/* 439 */     xml.append("=\"").append(label).append("\" ");
/* 440 */     xml.append("red").append("=\"").append(color.getRed()).append("\" ");
/* 441 */     xml.append("green").append("=\"").append(color.getGreen()).append("\" ");
/* 442 */     xml.append("blue").append("=\"").append(color.getBlue());
/* 443 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ 
/*     */   private void exportLogTableColumnXMLElement(String label, boolean selected, StringBuffer xml) {
/* 447 */     xml.append("\t\t<").append("column").append(" ").append("name");
/* 448 */     xml.append("=\"").append(label).append("\" ");
/* 449 */     xml.append("selected").append("=\"").append(selected);
/* 450 */     xml.append("\"/>\r\n");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.ConfigurationManager
 * JD-Core Version:    0.6.0
 */
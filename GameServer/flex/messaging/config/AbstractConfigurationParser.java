/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.util.FileUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public abstract class AbstractConfigurationParser
/*     */   implements ConfigurationParser, ConfigurationConstants
/*     */ {
/*     */   protected ServicesConfiguration config;
/*     */   protected DocumentBuilder docBuilder;
/*     */   protected ConfigurationFileResolver fileResolver;
/*     */   protected TokenReplacer tokenReplacer;
/*  54 */   private Map fileByDocument = new HashMap();
/*     */ 
/*     */   protected AbstractConfigurationParser()
/*     */   {
/*  58 */     initializeDocumentBuilder();
/*  59 */     this.tokenReplacer = new TokenReplacer();
/*     */   }
/*     */ 
/*     */   public void parse(String path, ConfigurationFileResolver fileResolver, ServicesConfiguration config)
/*     */   {
/*  64 */     this.config = config;
/*  65 */     this.fileResolver = fileResolver;
/*  66 */     Document doc = loadDocument(path, fileResolver.getConfigurationFile(path));
/*  67 */     initializeExpressionQuery();
/*  68 */     parseTopLevelConfig(doc);
/*     */   }
/*     */ 
/*     */   public void reportTokens()
/*     */   {
/*  73 */     this.tokenReplacer.reportTokens();
/*     */   }
/*     */ 
/*     */   protected void initializeDocumentBuilder()
/*     */   {
/*  78 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*  79 */     factory.setIgnoringComments(true);
/*  80 */     factory.setIgnoringElementContentWhitespace(true);
/*     */     try
/*     */     {
/*  84 */       this.docBuilder = factory.newDocumentBuilder();
/*     */     }
/*     */     catch (ParserConfigurationException ex)
/*     */     {
/*  89 */       ConfigurationException e = new ConfigurationException();
/*  90 */       e.setMessage(10100);
/*  91 */       e.setRootCause(ex);
/*  92 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Document loadDocument(String path, InputStream in)
/*     */   {
/*     */     ConfigurationException e;
/*     */     try
/*     */     {
/* 102 */       if (!in.markSupported()) {
/* 103 */         in = new BufferedInputStream(in);
/*     */       }
/*     */ 
/* 107 */       String encoding = FileUtils.consumeBOM(in, null);
/*     */       Document doc;
/*     */       Document doc;
/* 108 */       if (("UTF-8".equals(encoding)) || ("UTF-16".equals(encoding)))
/*     */       {
/* 110 */         InputSource inputSource = new InputSource(in);
/* 111 */         inputSource.setEncoding(encoding);
/* 112 */         doc = this.docBuilder.parse(inputSource);
/*     */       }
/*     */       else
/*     */       {
/* 116 */         doc = this.docBuilder.parse(in);
/*     */       }
/*     */ 
/* 119 */       addFileByDocument(path, doc);
/* 120 */       doc.getDocumentElement().normalize();
/* 121 */       return doc;
/*     */     }
/*     */     catch (SAXParseException ex)
/*     */     {
/* 125 */       Integer line = new Integer(ex.getLineNumber());
/* 126 */       Integer col = new Integer(ex.getColumnNumber());
/* 127 */       String message = ex.getMessage();
/*     */ 
/* 130 */       ConfigurationException e = new ConfigurationException();
/* 131 */       e.setMessage(10102, new Object[] { line, col, message });
/* 132 */       e.setRootCause(ex);
/* 133 */       throw e;
/*     */     }
/*     */     catch (SAXException ex)
/*     */     {
/* 137 */       ConfigurationException e = new ConfigurationException();
/* 138 */       e.setMessage(ex.getMessage());
/* 139 */       e.setRootCause(ex);
/* 140 */       throw e;
/*     */     }
/*     */     catch (IOException ex)
/*     */     {
/* 144 */       e = new ConfigurationException();
/* 145 */       e.setMessage(ex.getMessage());
/* 146 */       e.setRootCause(ex);
/* 147 */     }throw e;
/*     */   }
/*     */ 
/*     */   protected void addFileByDocument(String path, Node node)
/*     */   {
/* 153 */     String shortPath = path;
/* 154 */     if ((shortPath != null) && ((shortPath.indexOf("/") != -1) || (shortPath.indexOf("\\") != -1)))
/*     */     {
/* 156 */       int start = 0;
/* 157 */       start = shortPath.lastIndexOf("/");
/* 158 */       if (start == -1)
/*     */       {
/* 160 */         start = shortPath.lastIndexOf("\\");
/*     */       }
/* 162 */       shortPath = path.substring(start + 1);
/*     */     }
/* 164 */     this.fileByDocument.put(node, shortPath);
/*     */   }
/*     */ 
/*     */   protected String getSourceFileOf(Node node)
/*     */   {
/* 169 */     return (String)this.fileByDocument.get(node.getOwnerDocument());
/*     */   }
/*     */ 
/*     */   protected abstract void parseTopLevelConfig(Document paramDocument);
/*     */ 
/*     */   protected abstract void initializeExpressionQuery();
/*     */ 
/*     */   protected abstract Node selectSingleNode(Node paramNode, String paramString);
/*     */ 
/*     */   protected abstract NodeList selectNodeList(Node paramNode, String paramString);
/*     */ 
/*     */   protected abstract Object evaluateExpression(Node paramNode, String paramString);
/*     */ 
/*     */   public ConfigMap properties(NodeList properties)
/*     */   {
/* 199 */     return properties(properties, "uknown file");
/*     */   }
/*     */ 
/*     */   public ConfigMap properties(NodeList properties, String sourceFileName)
/*     */   {
/* 204 */     int length = properties.getLength();
/* 205 */     ConfigMap map = new ConfigMap(length);
/*     */ 
/* 207 */     for (int p = 0; p < length; p++)
/*     */     {
/* 209 */       Node prop = properties.item(p);
/* 210 */       String propName = prop.getNodeName();
/*     */ 
/* 212 */       if (propName == null)
/*     */         continue;
/* 214 */       propName = propName.trim();
/* 215 */       if (prop.getNodeType() == 1)
/*     */       {
/* 217 */         NodeList attributes = selectNodeList(prop, "@*");
/* 218 */         NodeList children = selectNodeList(prop, "*");
/* 219 */         if ((children.getLength() > 0) || (attributes.getLength() > 0))
/*     */         {
/* 221 */           ConfigMap childMap = new ConfigMap();
/*     */ 
/* 223 */           if (children.getLength() > 0) {
/* 224 */             childMap.addProperties(properties(children, sourceFileName));
/*     */           }
/* 226 */           if (attributes.getLength() > 0) {
/* 227 */             childMap.addProperties(properties(attributes, sourceFileName));
/*     */           }
/* 229 */           map.addProperty(propName, childMap);
/*     */         }
/*     */         else
/*     */         {
/* 234 */           this.tokenReplacer.replaceToken(prop, sourceFileName);
/* 235 */           String propValue = evaluateExpression(prop, ".").toString();
/* 236 */           map.addProperty(propName, propValue);
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 242 */         this.tokenReplacer.replaceToken(prop, sourceFileName);
/* 243 */         map.addProperty(propName, prop.getNodeValue());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 248 */     return map;
/*     */   }
/*     */ 
/*     */   public String getAttributeOrChildElement(Node node, String name)
/*     */   {
/* 253 */     String attr = evaluateExpression(node, "@" + name).toString().trim();
/* 254 */     if (attr.length() == 0)
/*     */     {
/* 256 */       attr = evaluateExpression(node, name).toString().trim();
/*     */     }
/* 258 */     return attr;
/*     */   }
/*     */ 
/*     */   public void allowedChildElements(Node node, String[] allowed)
/*     */   {
/* 263 */     NodeList children = selectNodeList(node, "*");
/*     */ 
/* 265 */     String unexpected = unexpected(children, allowed);
/* 266 */     if (unexpected != null)
/*     */     {
/* 269 */       ConfigurationException ex = new ConfigurationException();
/* 270 */       Object[] args = { unexpected, node.getNodeName(), getSourceFilename(node) };
/* 271 */       ex.setMessage(10106, args);
/* 272 */       throw ex;
/*     */     }
/*     */ 
/* 275 */     NodeList textNodes = selectNodeList(node, "text()");
/* 276 */     for (int i = 0; i < textNodes.getLength(); i++)
/*     */     {
/* 278 */       String text = evaluateExpression(textNodes.item(i), ".").toString().trim();
/* 279 */       if (text.length() <= 0) {
/*     */         continue;
/*     */       }
/* 282 */       ConfigurationException ex = new ConfigurationException();
/* 283 */       Object[] args = { text, node.getNodeName(), getSourceFilename(node) };
/* 284 */       ex.setMessage(11104, args);
/* 285 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void allowedAttributes(Node node, String[] allowed)
/*     */   {
/* 292 */     NodeList attributes = selectNodeList(node, "@*");
/* 293 */     String unexpectedAttribute = unexpected(attributes, allowed);
/* 294 */     if (unexpectedAttribute != null)
/*     */     {
/* 297 */       ConfigurationException ex = new ConfigurationException();
/* 298 */       Object[] args = { unexpectedAttribute, node.getNodeName(), getSourceFilename(node) };
/*     */ 
/* 300 */       ex.setMessage(10107, args);
/* 301 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getSourceFilename(Node node)
/*     */   {
/* 307 */     return getSourceFileOf(node);
/*     */   }
/*     */ 
/*     */   public void allowedAttributesOrElements(Node node, String[] allowed)
/*     */   {
/* 312 */     allowedAttributes(node, allowed);
/* 313 */     allowedChildElements(node, allowed);
/*     */   }
/*     */ 
/*     */   public void requiredAttributesOrElements(Node node, String[] required)
/*     */   {
/* 318 */     String nodeName = node.getNodeName();
/* 319 */     NodeList attributes = selectNodeList(node, "@*");
/*     */ 
/* 321 */     List list = new ArrayList();
/* 322 */     for (int i = 0; i < required.length; i++)
/*     */     {
/* 324 */       list.add(required[i]);
/*     */     }
/*     */ 
/* 327 */     String missingAttribute = null;
/*     */     do
/*     */     {
/* 331 */       missingAttribute = required(attributes, list);
/* 332 */       if (missingAttribute == null)
/*     */         continue;
/* 334 */       Node child = selectSingleNode(node, missingAttribute);
/* 335 */       if (child != null)
/*     */       {
/* 337 */         list.remove(missingAttribute);
/*     */       }
/*     */       else
/*     */       {
/* 342 */         ConfigurationException ex = new ConfigurationException();
/* 343 */         ex.setMessage(10105, new Object[] { missingAttribute, nodeName });
/* 344 */         throw ex;
/*     */       }
/*     */     }
/*     */ 
/* 348 */     while ((missingAttribute != null) && (list.size() > 0));
/*     */   }
/*     */ 
/*     */   public void requiredChildElements(Node node, String[] required)
/*     */   {
/* 353 */     String nodeName = node.getNodeName();
/* 354 */     NodeList children = selectNodeList(node, "*");
/*     */ 
/* 356 */     List list = new ArrayList();
/* 357 */     for (int i = 0; i < required.length; i++)
/*     */     {
/* 359 */       list.add(required[i]);
/*     */     }
/*     */ 
/* 362 */     String missing = required(children, list);
/* 363 */     if (missing != null)
/*     */     {
/* 366 */       ConfigurationException ex = new ConfigurationException();
/* 367 */       ex.setMessage(10104, new Object[] { missing, nodeName });
/* 368 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String unexpected(NodeList attributes, String[] allowed)
/*     */   {
/* 378 */     for (int i = 0; i < attributes.getLength(); i++)
/*     */     {
/* 380 */       Node attrib = attributes.item(i);
/* 381 */       String attribName = attrib.getNodeName();
/* 382 */       boolean match = false;
/*     */ 
/* 384 */       for (int j = 0; j < allowed.length; j++)
/*     */       {
/* 386 */         String a = allowed[j];
/* 387 */         if (!a.equals(attribName))
/*     */           continue;
/* 389 */         match = true;
/* 390 */         break;
/*     */       }
/*     */ 
/* 394 */       if (!match)
/*     */       {
/* 396 */         return attribName;
/*     */       }
/*     */ 
/* 400 */       this.tokenReplacer.replaceToken(attrib, getSourceFilename(attrib));
/*     */     }
/*     */ 
/* 403 */     return null;
/*     */   }
/*     */ 
/*     */   public String required(NodeList attributes, List required)
/*     */   {
/* 412 */     for (int i = 0; i < required.size(); i++)
/*     */     {
/* 414 */       boolean found = false;
/* 415 */       String req = (String)required.get(i);
/*     */ 
/* 417 */       Node attrib = null;
/* 418 */       for (int j = 0; j < attributes.getLength(); j++)
/*     */       {
/* 420 */         attrib = attributes.item(j);
/* 421 */         String attribName = attrib.getNodeName();
/*     */ 
/* 423 */         if (!req.equals(attribName))
/*     */           continue;
/* 425 */         found = true;
/* 426 */         break;
/*     */       }
/*     */ 
/* 430 */       if (!found)
/*     */       {
/* 432 */         return req;
/*     */       }
/*     */ 
/* 436 */       this.tokenReplacer.replaceToken(attrib, getSourceFilename(attrib));
/*     */     }
/*     */ 
/* 440 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isValidID(String id)
/*     */   {
/* 455 */     if (id != null)
/*     */     {
/* 457 */       if ((id.length() > 0) && (id.length() < 256))
/*     */       {
/* 459 */         char[] chars = id.toCharArray();
/* 460 */         for (int i = 0; i < chars.length; i++)
/*     */         {
/* 462 */           char c = chars[i];
/* 463 */           if (",;:".indexOf(c) != -1)
/*     */           {
/* 465 */             return false;
/*     */           }
/*     */         }
/*     */ 
/* 469 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 473 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.AbstractConfigurationParser
 * JD-Core Version:    0.6.0
 */
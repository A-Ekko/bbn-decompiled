/*     */ package org.logicalcobwebs.proxool.configuration;
/*     */ 
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ public class XMLConfigurator extends DefaultHandler
/*     */ {
/*  93 */   private static final Log LOG = LogFactory.getLog(XMLConfigurator.class);
/*     */ 
/*  95 */   private StringBuffer content = new StringBuffer();
/*     */   private String poolName;
/*     */   private String driverClass;
/*     */   private String driverUrl;
/* 103 */   private Properties properties = new Properties();
/*     */   private static final String PROXOOL = "proxool";
/*     */   private static final String DRIVER_PROPERTIES = "driver-properties";
/*     */   private static final String PROPERTY = "property";
/*     */   private static final String NAME = "name";
/*     */   private static final String VALUE = "value";
/*     */   private boolean insideDelegateProperties;
/*     */   private boolean insideProxool;
/*     */ 
/*     */   public void startElement(String uri, String lname, String qname, Attributes attributes)
/*     */     throws SAXException
/*     */   {
/* 123 */     this.content.setLength(0);
/*     */ 
/* 125 */     if (!namespaceOk(uri)) {
/* 126 */       return;
/*     */     }
/*     */ 
/* 129 */     String elementName = getElementName(uri, lname, qname);
/*     */ 
/* 131 */     if (elementName.equals("proxool")) {
/* 132 */       if (this.insideProxool) {
/* 133 */         throw new SAXException("A <proxool> element can't contain another <proxool> element.");
/*     */       }
/* 135 */       this.insideProxool = true;
/* 136 */       this.properties.clear();
/* 137 */       this.driverClass = null;
/* 138 */       this.driverUrl = null;
/*     */     }
/*     */ 
/* 141 */     if (this.insideProxool)
/* 142 */       if (elementName.equals("driver-properties"))
/* 143 */         this.insideDelegateProperties = true;
/* 144 */       else if ((this.insideDelegateProperties) && 
/* 145 */         (elementName.equals("property")))
/* 146 */         setDriverProperty(attributes);
/*     */   }
/*     */ 
/*     */   public void characters(char[] chars, int start, int length)
/*     */     throws SAXException
/*     */   {
/* 156 */     if (this.insideProxool)
/* 157 */       this.content.append(chars, start, length);
/*     */   }
/*     */ 
/*     */   public void endElement(String uri, String lname, String qname)
/*     */     throws SAXException
/*     */   {
/* 165 */     if (!namespaceOk(uri)) {
/* 166 */       return;
/*     */     }
/*     */ 
/* 169 */     String elementName = getElementName(uri, lname, qname);
/*     */ 
/* 172 */     if (elementName.equals("proxool"))
/*     */     {
/* 175 */       if ((this.driverClass == null) || (this.driverUrl == null)) {
/* 176 */         throw new SAXException("You must define the driver-class and the driver-url.");
/*     */       }
/*     */ 
/* 180 */       StringBuffer url = new StringBuffer();
/* 181 */       url.append("proxool");
/* 182 */       if (this.poolName != null) {
/* 183 */         url.append(".");
/* 184 */         url.append(this.poolName);
/*     */       }
/* 186 */       url.append(":");
/* 187 */       url.append(this.driverClass);
/* 188 */       url.append(":");
/* 189 */       url.append(this.driverUrl);
/* 190 */       if (LOG.isDebugEnabled()) {
/* 191 */         LOG.debug("Created url: " + url);
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 196 */         ProxoolFacade.registerConnectionPool(url.toString(), this.properties);
/*     */       } catch (ProxoolException e) {
/* 198 */         throw new SAXException(e);
/*     */       }
/*     */ 
/* 203 */       this.insideProxool = false;
/*     */     }
/*     */ 
/* 206 */     if ((this.insideProxool) && (!elementName.equals("proxool")))
/* 207 */       if (elementName.equals("driver-properties"))
/* 208 */         this.insideDelegateProperties = false;
/* 209 */       else if (!this.insideDelegateProperties)
/* 210 */         setProxoolProperty(elementName, this.content.toString().trim());
/*     */   }
/*     */ 
/*     */   private void setProxoolProperty(String localName, String value)
/*     */   {
/* 216 */     if (localName.equals("alias")) {
/* 217 */       this.poolName = value;
/* 218 */     } else if (localName.equals("driver-class")) {
/* 219 */       this.driverClass = value;
/* 220 */     } else if (localName.equals("driver-url")) {
/* 221 */       this.driverUrl = value;
/*     */     } else {
/* 223 */       if (LOG.isDebugEnabled()) {
/* 224 */         LOG.debug("Setting property 'proxool." + localName + "' to value '" + value + "'.");
/*     */       }
/* 226 */       this.properties.put("proxool." + localName, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setDriverProperty(Attributes attributes) throws SAXException {
/* 231 */     String name = attributes.getValue("name");
/* 232 */     String value = attributes.getValue("value");
/* 233 */     if ((name == null) || (name.length() < 1) || (value == null)) {
/* 234 */       throw new SAXException("Name or value attribute missing from property element.Name: '" + name + "' Value: '" + value + "'.");
/*     */     }
/*     */ 
/* 237 */     if (LOG.isDebugEnabled()) {
/* 238 */       if (name.toLowerCase().indexOf("password") > -1)
/* 239 */         LOG.debug("Adding driver property: " + name + "=" + "*******");
/*     */       else {
/* 241 */         LOG.debug("Adding driver property: " + name + "=" + value);
/*     */       }
/*     */     }
/* 244 */     this.properties.put(name, value);
/*     */   }
/*     */ 
/*     */   public void warning(SAXParseException e)
/*     */     throws SAXException
/*     */   {
/* 252 */     LOG.debug("The saxparser reported a warning.", e);
/*     */   }
/*     */ 
/*     */   public void error(SAXParseException e)
/*     */     throws SAXException
/*     */   {
/* 261 */     throw e;
/*     */   }
/*     */ 
/*     */   public void fatalError(SAXParseException e)
/*     */     throws SAXException
/*     */   {
/* 270 */     throw e;
/*     */   }
/*     */ 
/*     */   private String getElementName(String uri, String lname, String qname)
/*     */   {
/* 275 */     if ((uri == null) || ("".equals(uri))) {
/* 276 */       return qname;
/*     */     }
/* 278 */     return lname;
/*     */   }
/*     */ 
/*     */   private boolean namespaceOk(String uri)
/*     */   {
/* 283 */     return (uri == null) || (uri.length() == 0) || (uri.equals("The latest version is available at http://proxool.sourceforge.net/xml-namespace"));
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.configuration.XMLConfigurator
 * JD-Core Version:    0.6.0
 */
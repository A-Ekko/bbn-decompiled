/*     */ package org.logicalcobwebs.proxool.configuration;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.avalon.framework.activity.Disposable;
/*     */ import org.apache.avalon.framework.component.Component;
/*     */ import org.apache.avalon.framework.configuration.Configurable;
/*     */ import org.apache.avalon.framework.configuration.Configuration;
/*     */ import org.apache.avalon.framework.configuration.ConfigurationException;
/*     */ import org.apache.avalon.framework.thread.ThreadSafe;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.AttributesImpl;
/*     */ 
/*     */ public class AvalonConfigurator
/*     */   implements Component, Configurable, ThreadSafe, Disposable
/*     */ {
/*  51 */   private static final Log LOG = LogFactory.getLog(AvalonConfigurator.class);
/*     */ 
/*  56 */   public static final String ROLE = AvalonConfigurator.class.getName();
/*     */   public static final String CLOSE_ON_DISPOSE_ATTRIBUTE = "close-on-dispose";
/*  66 */   private boolean closeOnDispose = true;
/*  67 */   private final List configuredPools = new ArrayList(3);
/*     */ 
/*     */   public void configure(Configuration configuration)
/*     */     throws ConfigurationException
/*     */   {
/*  76 */     XMLConfigurator xmlConfigurator = new XMLConfigurator();
/*  77 */     this.closeOnDispose = configuration.getAttributeAsBoolean("close-on-dispose", true);
/*  78 */     Configuration[] children = configuration.getChildren();
/*  79 */     for (int i = 0; i < children.length; i++) {
/*  80 */       if (!children[i].getName().equals("proxool")) {
/*  81 */         throw new ConfigurationException("Found element named " + children[i].getName() + ". Only " + "proxool" + " top level elements are alowed.");
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  86 */       xmlConfigurator.startDocument();
/*  87 */       reportProperties(xmlConfigurator, configuration.getChildren());
/*  88 */       xmlConfigurator.endDocument();
/*     */     } catch (SAXException e) {
/*  90 */       throw new ConfigurationException("", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/*  99 */     LOG.info("Disposing.");
/* 100 */     if (this.closeOnDispose) {
/* 101 */       Iterator configuredPools = this.configuredPools.iterator();
/* 102 */       String alias = null;
/* 103 */       while (configuredPools.hasNext()) {
/* 104 */         alias = (String)configuredPools.next();
/* 105 */         LOG.info("Closing connection pool '" + alias + "'.");
/*     */         try {
/* 107 */           ProxoolFacade.removeConnectionPool(alias);
/*     */         } catch (ProxoolException e) {
/* 109 */           LOG.error("Closing of connection pool '" + alias + "' failed.", e);
/*     */         }
/*     */       }
/*     */     } else {
/* 113 */       LOG.info("close-on-dispose attribute is not set, so configured pools will not be closed.");
/*     */     }
/* 115 */     LOG.info("Disposed.");
/*     */   }
/*     */ 
/*     */   private void reportProperties(XMLConfigurator xmlConfigurator, Configuration[] properties)
/*     */     throws ConfigurationException, SAXException
/*     */   {
/* 121 */     Configuration[] children = null;
/* 122 */     String value = null;
/* 123 */     String namespace = null;
/* 124 */     for (int i = 0; i < properties.length; i++) {
/* 125 */       Configuration configuration = properties[i];
/* 126 */       namespace = configuration.getNamespace();
/* 127 */       if (namespace == null) {
/* 128 */         namespace = "";
/*     */       }
/* 130 */       if (LOG.isDebugEnabled()) {
/* 131 */         LOG.debug("Reporting element start for " + configuration.getName());
/*     */       }
/* 133 */       String lName = namespace.length() == 0 ? "" : configuration.getName();
/* 134 */       String qName = namespace.length() == 0 ? configuration.getName() : "";
/*     */ 
/* 136 */       xmlConfigurator.startElement(namespace, lName, qName, getAttributes(configuration));
/* 137 */       children = configuration.getChildren();
/*     */ 
/* 140 */       if ((children == null) || (children.length < 1)) {
/* 141 */         value = configuration.getValue(null);
/* 142 */         if (value != null)
/* 143 */           xmlConfigurator.characters(value.toCharArray(), 0, value.length());
/*     */       }
/*     */       else {
/* 146 */         reportProperties(xmlConfigurator, children);
/*     */       }
/* 148 */       xmlConfigurator.endElement(namespace, lName, qName);
/* 149 */       if ((lName.equals("proxool")) || (qName.equals("proxool"))) {
/* 150 */         Configuration conf = configuration.getChild("alias", false);
/* 151 */         if (conf != null) {
/* 152 */           if (LOG.isDebugEnabled()) {
/* 153 */             LOG.debug("Adding to configured pools: " + conf.getValue());
/*     */           }
/* 155 */           this.configuredPools.add(conf.getValue());
/*     */         } else {
/* 157 */           LOG.error("proxool element was missing required element 'alias'");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Attributes getAttributes(Configuration configuration)
/*     */     throws ConfigurationException
/*     */   {
/* 166 */     AttributesImpl attributes = new AttributesImpl();
/* 167 */     String[] avalonAttributeNames = configuration.getAttributeNames();
/* 168 */     if ((avalonAttributeNames != null) && (avalonAttributeNames.length > 0)) {
/* 169 */       for (int i = 0; i < avalonAttributeNames.length; i++) {
/* 170 */         if (LOG.isDebugEnabled()) {
/* 171 */           LOG.debug("Adding attribute " + avalonAttributeNames[i] + " with value " + configuration.getAttribute(avalonAttributeNames[i]));
/*     */         }
/*     */ 
/* 174 */         attributes.addAttribute("", avalonAttributeNames[i], avalonAttributeNames[i], "CDATA", configuration.getAttribute(avalonAttributeNames[i]));
/*     */ 
/* 176 */         LOG.debug("In attributes: " + avalonAttributeNames[i] + " with value " + attributes.getValue(avalonAttributeNames[i]));
/*     */       }
/*     */     }
/*     */ 
/* 180 */     return attributes;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.configuration.AvalonConfigurator
 * JD-Core Version:    0.6.0
 */
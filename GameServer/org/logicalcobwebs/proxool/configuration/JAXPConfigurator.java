/*     */ package org.logicalcobwebs.proxool.configuration;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXNotRecognizedException;
/*     */ import org.xml.sax.SAXNotSupportedException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ public class JAXPConfigurator
/*     */ {
/*  42 */   private static final Log LOG = LogFactory.getLog(JAXPConfigurator.class);
/*     */   private static final boolean NAMESPACE_AWARE = true;
/*     */ 
/*     */   public static void configure(String xmlFileName, boolean validate)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/*  53 */       if (LOG.isDebugEnabled()) {
/*  54 */         LOG.debug("Configuring from xml file: " + xmlFileName);
/*     */       }
/*  56 */       configure(new InputSource(new FileReader(xmlFileName)), validate);
/*     */     } catch (FileNotFoundException e) {
/*  58 */       throw new ProxoolException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void configure(InputSource inputSource, boolean validate)
/*     */     throws ProxoolException
/*     */   {
/*     */     try
/*     */     {
/*  70 */       SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
/*  71 */       if (LOG.isDebugEnabled()) {
/*  72 */         LOG.debug("SAXParserFactory class: " + saxParserFactory.getClass().getName());
/*     */       }
/*  74 */       saxParserFactory.setValidating(validate);
/*  75 */       SAXParser saxParser = saxParserFactory.newSAXParser();
/*  76 */       if (LOG.isDebugEnabled()) {
/*  77 */         LOG.debug("sax parser class" + saxParser.getClass().getName());
/*     */       }
/*  79 */       XMLReader xmlReader = saxParser.getXMLReader();
/*  80 */       if (LOG.isDebugEnabled()) {
/*  81 */         LOG.debug("XML reader class: " + xmlReader.getClass().getName());
/*     */       }
/*  83 */       XMLConfigurator xmlConfigurator = new XMLConfigurator();
/*  84 */       xmlReader.setErrorHandler(xmlConfigurator);
/*  85 */       setSAXFeature(xmlReader, "http://xml.org/sax/features/namespaces", true);
/*  86 */       setSAXFeature(xmlReader, "http://xml.org/sax/features/namespace-prefixes", false);
/*  87 */       saxParser.parse(inputSource, xmlConfigurator);
/*     */     } catch (ParserConfigurationException pce) {
/*  89 */       throw new ProxoolException("Parser configuration failed", pce);
/*     */     } catch (SAXException se) {
/*  91 */       throw new ProxoolException("Parsing failed.", se);
/*     */     } catch (IOException ioe) {
/*  93 */       throw new ProxoolException("Parsing failed.", ioe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void configure(Reader reader, boolean validate)
/*     */     throws ProxoolException
/*     */   {
/* 104 */     if (LOG.isDebugEnabled()) {
/* 105 */       LOG.debug("Configuring from reader: " + reader);
/*     */     }
/* 107 */     configure(new InputSource(reader), validate);
/*     */   }
/*     */ 
/*     */   private static void setSAXFeature(XMLReader xmlReader, String feature, boolean state)
/*     */   {
/* 113 */     if (LOG.isDebugEnabled())
/* 114 */       LOG.debug("Setting sax feature: '" + feature + "'. State: " + state + ".");
/*     */     try
/*     */     {
/* 117 */       xmlReader.setFeature(feature, state);
/*     */     } catch (SAXNotRecognizedException e) {
/* 119 */       LOG.warn("Feature: '" + feature + "' not recognised by xml reader " + xmlReader + ".", e);
/*     */     } catch (SAXNotSupportedException e) {
/* 121 */       LOG.warn("Feature: '" + feature + "' not supported by xml reader " + xmlReader + ".", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.configuration.JAXPConfigurator
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.InputSource;
/*     */ 
/*     */ public class XMLUtil
/*     */ {
/*  23 */   public static String INDENT_XML = "no";
/*  24 */   public static String OMIT_XML_DECLARATION = "yes";
/*     */ 
/*     */   public static String documentToString(Document document)
/*     */     throws IOException
/*     */   {
/*  40 */     String xml = null;
/*     */     try
/*     */     {
/*  44 */       DOMSource dom = new DOMSource(document);
/*  45 */       StringWriter writer = new StringWriter();
/*  46 */       StreamResult output = new StreamResult(writer);
/*     */ 
/*  49 */       TransformerFactory factory = TransformerFactory.newInstance();
/*  50 */       Transformer transformer = factory.newTransformer();
/*     */ 
/*  53 */       transformer.setOutputProperty("indent", INDENT_XML);
/*     */ 
/*  58 */       transformer.setOutputProperty("omit-xml-declaration", OMIT_XML_DECLARATION);
/*     */ 
/*  60 */       transformer.transform(dom, output);
/*     */ 
/*  62 */       xml = writer.toString();
/*     */     }
/*     */     catch (TransformerException te)
/*     */     {
/*  66 */       throw new IOException("Error serializing Document as String: " + te.getMessageAndLocation());
/*     */     }
/*  68 */     return xml;
/*     */   }
/*     */ 
/*     */   public static Document stringToDocument(String xml)
/*     */   {
/*  80 */     return stringToDocument(xml, true);
/*     */   }
/*     */ 
/*     */   public static Document stringToDocument(String xml, boolean nameSpaceAware)
/*     */   {
/*  94 */     Document document = null;
/*     */     try
/*     */     {
/*  97 */       if (xml != null)
/*     */       {
/*  99 */         StringReader reader = new StringReader(xml);
/* 100 */         InputSource input = new InputSource(reader);
/* 101 */         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/* 102 */         factory.setNamespaceAware(nameSpaceAware);
/* 103 */         factory.setValidating(false);
/* 104 */         DocumentBuilder builder = factory.newDocumentBuilder();
/*     */ 
/* 106 */         document = builder.parse(input);
/*     */       }
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 111 */       throw new MessageException("Error deserializing XML type " + ex.getMessage());
/*     */     }
/*     */ 
/* 114 */     return document;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.XMLUtil
 * JD-Core Version:    0.6.0
 */
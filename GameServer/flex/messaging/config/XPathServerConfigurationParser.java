/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import javax.xml.xpath.XPath;
/*     */ import javax.xml.xpath.XPathConstants;
/*     */ import javax.xml.xpath.XPathExpressionException;
/*     */ import javax.xml.xpath.XPathFactory;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class XPathServerConfigurationParser extends ServerConfigurationParser
/*     */ {
/*     */   private XPath xpath;
/*     */ 
/*     */   protected void initializeExpressionQuery()
/*     */   {
/*  45 */     this.xpath = XPathFactory.newInstance().newXPath();
/*     */   }
/*     */ 
/*     */   protected Node selectSingleNode(Node source, String expression)
/*     */   {
/*     */     try
/*     */     {
/*  52 */       return (Node)this.xpath.evaluate(expression, source, XPathConstants.NODE);
/*     */     }
/*     */     catch (XPathExpressionException expressionException)
/*     */     {
/*     */     }
/*  57 */     throw wrapException(expressionException);
/*     */   }
/*     */ 
/*     */   protected NodeList selectNodeList(Node source, String expression)
/*     */   {
/*     */     try
/*     */     {
/*  65 */       return (NodeList)this.xpath.evaluate(expression, source, XPathConstants.NODESET);
/*     */     }
/*     */     catch (XPathExpressionException expressionException)
/*     */     {
/*     */     }
/*  70 */     throw wrapException(expressionException);
/*     */   }
/*     */ 
/*     */   protected Object evaluateExpression(Node source, String expression)
/*     */   {
/*     */     try
/*     */     {
/*  78 */       return this.xpath.evaluate(expression, source, XPathConstants.STRING);
/*     */     }
/*     */     catch (XPathExpressionException expressionException) {
/*     */     }
/*  82 */     throw wrapException(expressionException);
/*     */   }
/*     */ 
/*     */   private ConfigurationException wrapException(XPathExpressionException exception)
/*     */   {
/*  89 */     ConfigurationException result = new ConfigurationException();
/*  90 */     result.setDetails(10101);
/*  91 */     result.setRootCause(exception);
/*  92 */     return result;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  97 */     MessagingConfiguration config = new MessagingConfiguration();
/*  98 */     XPathServerConfigurationParser parser = new XPathServerConfigurationParser();
/*  99 */     parser.parse(args[0], new LocalFileResolver(), config);
/* 100 */     System.out.println(config.toString());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.XPathServerConfigurationParser
 * JD-Core Version:    0.6.0
 */
/*    */ package flex.messaging.config;
/*    */ 
/*    */ import javax.xml.xpath.XPath;
/*    */ import javax.xml.xpath.XPathConstants;
/*    */ import javax.xml.xpath.XPathExpressionException;
/*    */ import javax.xml.xpath.XPathFactory;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
/*    */ 
/*    */ public class XPathClientConfigurationParser extends ClientConfigurationParser
/*    */ {
/*    */   private XPath xpath;
/*    */ 
/*    */   protected void initializeExpressionQuery()
/*    */   {
/* 45 */     this.xpath = XPathFactory.newInstance().newXPath();
/*    */   }
/*    */ 
/*    */   protected Node selectSingleNode(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 52 */       return (Node)this.xpath.evaluate(expression, source, XPathConstants.NODE);
/*    */     }
/*    */     catch (XPathExpressionException expressionException) {
/*    */     }
/* 56 */     throw wrapException(expressionException);
/*    */   }
/*    */ 
/*    */   protected NodeList selectNodeList(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 64 */       return (NodeList)this.xpath.evaluate(expression, source, XPathConstants.NODESET);
/*    */     }
/*    */     catch (XPathExpressionException expressionException) {
/*    */     }
/* 68 */     throw wrapException(expressionException);
/*    */   }
/*    */ 
/*    */   protected Object evaluateExpression(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 76 */       return this.xpath.evaluate(expression, source, XPathConstants.STRING);
/*    */     }
/*    */     catch (XPathExpressionException expressionException) {
/*    */     }
/* 80 */     throw wrapException(expressionException);
/*    */   }
/*    */ 
/*    */   private ConfigurationException wrapException(XPathExpressionException exception)
/*    */   {
/* 86 */     ConfigurationException result = new ConfigurationException();
/* 87 */     result.setDetails(10101);
/* 88 */     result.setRootCause(exception);
/* 89 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.XPathClientConfigurationParser
 * JD-Core Version:    0.6.0
 */
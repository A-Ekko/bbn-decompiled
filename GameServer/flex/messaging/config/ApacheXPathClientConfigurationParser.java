/*    */ package flex.messaging.config;
/*    */ 
/*    */ import javax.xml.transform.TransformerException;
/*    */ import org.apache.xpath.CachedXPathAPI;
/*    */ import org.w3c.dom.Node;
/*    */ import org.w3c.dom.NodeList;
/*    */ 
/*    */ public class ApacheXPathClientConfigurationParser extends ClientConfigurationParser
/*    */ {
/*    */   private CachedXPathAPI xpath;
/*    */ 
/*    */   protected void initializeExpressionQuery()
/*    */   {
/* 42 */     this.xpath = new CachedXPathAPI();
/*    */   }
/*    */ 
/*    */   protected Node selectSingleNode(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 49 */       return this.xpath.selectSingleNode(source, expression);
/*    */     }
/*    */     catch (TransformerException transformerException) {
/*    */     }
/* 53 */     throw wrapException(transformerException);
/*    */   }
/*    */ 
/*    */   protected NodeList selectNodeList(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 61 */       return this.xpath.selectNodeList(source, expression);
/*    */     }
/*    */     catch (TransformerException transformerException) {
/*    */     }
/* 65 */     throw wrapException(transformerException);
/*    */   }
/*    */ 
/*    */   protected Object evaluateExpression(Node source, String expression)
/*    */   {
/*    */     try
/*    */     {
/* 73 */       return this.xpath.eval(source, expression);
/*    */     }
/*    */     catch (TransformerException transformerException) {
/*    */     }
/* 77 */     throw wrapException(transformerException);
/*    */   }
/*    */ 
/*    */   private ConfigurationException wrapException(TransformerException exception)
/*    */   {
/* 83 */     ConfigurationException result = new ConfigurationException();
/* 84 */     result.setDetails(10101);
/* 85 */     result.setRootCause(exception);
/* 86 */     return result;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ApacheXPathClientConfigurationParser
 * JD-Core Version:    0.6.0
 */
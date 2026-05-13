/*    */ package org.apache.log4j.xml;
/*    */ 
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ import org.xml.sax.ErrorHandler;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.SAXParseException;
/*    */ 
/*    */ public class SAXErrorHandler
/*    */   implements ErrorHandler
/*    */ {
/*    */   public void error(SAXParseException ex)
/*    */   {
/* 18 */     LogLog.error("Parsing error on line " + ex.getLineNumber() + " and column " + ex.getColumnNumber());
/*    */ 
/* 20 */     LogLog.error(ex.getMessage(), ex.getException());
/*    */   }
/*    */ 
/*    */   public void fatalError(SAXParseException ex)
/*    */   {
/* 26 */     error(ex);
/*    */   }
/*    */ 
/*    */   public void warning(SAXParseException ex)
/*    */   {
/* 31 */     LogLog.warn("Parsing error on line " + ex.getLineNumber() + " and column " + ex.getColumnNumber());
/*    */ 
/* 33 */     LogLog.warn(ex.getMessage(), ex.getException());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.xml.SAXErrorHandler
 * JD-Core Version:    0.6.0
 */
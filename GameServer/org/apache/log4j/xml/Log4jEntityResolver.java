/*    */ package org.apache.log4j.xml;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ import org.xml.sax.EntityResolver;
/*    */ import org.xml.sax.InputSource;
/*    */ 
/*    */ public class Log4jEntityResolver
/*    */   implements EntityResolver
/*    */ {
/*    */   public InputSource resolveEntity(String publicId, String systemId)
/*    */   {
/* 37 */     if (systemId.endsWith("log4j.dtd")) {
/* 38 */       Class clazz = getClass();
/* 39 */       InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");
/* 40 */       if (in == null) {
/* 41 */         LogLog.warn("Could not find [log4j.dtd] using [" + clazz.getClassLoader() + "] class loader, parsed without DTD.");
/*    */ 
/* 43 */         in = new ByteArrayInputStream(new byte[0]);
/*    */       }
/* 45 */       return new InputSource(in);
/*    */     }
/* 47 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.xml.Log4jEntityResolver
 * JD-Core Version:    0.6.0
 */
/*    */ package org.apache.mina.proxy.handlers.http;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class HttpProxyResponse
/*    */ {
/*    */   public final String httpVersion;
/*    */   public final String statusLine;
/*    */   public final int statusCode;
/*    */   public final Map<String, List<String>> headers;
/*    */   public String body;
/*    */ 
/*    */   protected HttpProxyResponse(String httpVersion, String statusLine, Map<String, List<String>> headers)
/*    */   {
/* 45 */     this.httpVersion = httpVersion;
/* 46 */     this.statusLine = statusLine;
/*    */ 
/* 48 */     this.statusCode = (statusLine.charAt(0) == ' ' ? Integer.parseInt(statusLine.substring(1, 4)) : Integer.parseInt(statusLine.substring(0, 3)));
/*    */ 
/* 52 */     this.headers = headers;
/*    */   }
/*    */ 
/*    */   public final String getHttpVersion()
/*    */   {
/* 59 */     return this.httpVersion;
/*    */   }
/*    */ 
/*    */   public final int getStatusCode()
/*    */   {
/* 66 */     return this.statusCode;
/*    */   }
/*    */ 
/*    */   public final String getStatusLine()
/*    */   {
/* 73 */     return this.statusLine;
/*    */   }
/*    */ 
/*    */   public String getBody()
/*    */   {
/* 80 */     return this.body;
/*    */   }
/*    */ 
/*    */   public void setBody(String body)
/*    */   {
/* 87 */     this.body = body;
/*    */   }
/*    */ 
/*    */   public final Map<String, List<String>> getHeaders()
/*    */   {
/* 94 */     return this.headers;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.HttpProxyResponse
 * JD-Core Version:    0.6.0
 */
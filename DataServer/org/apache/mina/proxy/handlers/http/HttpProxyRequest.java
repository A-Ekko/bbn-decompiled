/*     */ package org.apache.mina.proxy.handlers.http;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.proxy.ProxyAuthException;
/*     */ import org.apache.mina.proxy.handlers.ProxyRequest;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class HttpProxyRequest extends ProxyRequest
/*     */ {
/*  41 */   private static final Logger logger = LoggerFactory.getLogger(HttpProxyRequest.class);
/*     */   public final String httpVerb;
/*     */   public final String httpURI;
/*     */   private String httpVersion;
/*     */   private String host;
/*     */   private Map<String, List<String>> headers;
/*     */   private transient Map<String, String> properties;
/*     */ 
/*     */   public HttpProxyRequest(InetSocketAddress endpointAddress)
/*     */   {
/*  82 */     this(endpointAddress, "HTTP/1.0", null);
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion)
/*     */   {
/*  94 */     this(endpointAddress, httpVersion, null);
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(InetSocketAddress endpointAddress, String httpVersion, Map<String, List<String>> headers)
/*     */   {
/* 107 */     this.httpVerb = "CONNECT";
/* 108 */     if (!endpointAddress.isUnresolved()) {
/* 109 */       this.httpURI = (endpointAddress.getHostName() + ":" + endpointAddress.getPort());
/*     */     }
/*     */     else {
/* 112 */       this.httpURI = (endpointAddress.getAddress().getHostAddress() + ":" + endpointAddress.getPort());
/*     */     }
/*     */ 
/* 116 */     this.httpVersion = httpVersion;
/* 117 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(String httpURI)
/*     */   {
/* 127 */     this("GET", httpURI, "HTTP/1.0", null);
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(String httpURI, String httpVersion)
/*     */   {
/* 138 */     this("GET", httpURI, httpVersion, null);
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion)
/*     */   {
/* 151 */     this(httpVerb, httpURI, httpVersion, null);
/*     */   }
/*     */ 
/*     */   public HttpProxyRequest(String httpVerb, String httpURI, String httpVersion, Map<String, List<String>> headers)
/*     */   {
/* 166 */     this.httpVerb = httpVerb;
/* 167 */     this.httpURI = httpURI;
/* 168 */     this.httpVersion = httpVersion;
/* 169 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */   public final String getHttpVerb()
/*     */   {
/* 176 */     return this.httpVerb;
/*     */   }
/*     */ 
/*     */   public String getHttpVersion()
/*     */   {
/* 183 */     return this.httpVersion;
/*     */   }
/*     */ 
/*     */   public void setHttpVersion(String httpVersion)
/*     */   {
/* 192 */     this.httpVersion = httpVersion;
/*     */   }
/*     */ 
/*     */   public final synchronized String getHost()
/*     */   {
/* 199 */     if (this.host == null) {
/* 200 */       if ((getEndpointAddress() != null) && (!getEndpointAddress().isUnresolved()))
/*     */       {
/* 202 */         this.host = getEndpointAddress().getHostName();
/*     */       }
/*     */ 
/* 205 */       if ((this.host == null) && (this.httpURI != null)) {
/*     */         try {
/* 207 */           this.host = new URL(this.httpURI).getHost();
/*     */         } catch (MalformedURLException e) {
/* 209 */           logger.debug("Malformed URL", e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 214 */     return this.host;
/*     */   }
/*     */ 
/*     */   public final String getHttpURI()
/*     */   {
/* 221 */     return this.httpURI;
/*     */   }
/*     */ 
/*     */   public final Map<String, List<String>> getHeaders()
/*     */   {
/* 228 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public final void setHeaders(Map<String, List<String>> headers)
/*     */   {
/* 235 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getProperties()
/*     */   {
/* 242 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public void setProperties(Map<String, String> properties)
/*     */   {
/* 249 */     this.properties = properties;
/*     */   }
/*     */ 
/*     */   public void checkRequiredProperties(String[] propNames)
/*     */     throws ProxyAuthException
/*     */   {
/* 257 */     StringBuilder sb = new StringBuilder();
/* 258 */     for (String propertyName : propNames) {
/* 259 */       if (this.properties.get(propertyName) == null) {
/* 260 */         sb.append(propertyName).append(' ');
/*     */       }
/*     */     }
/* 263 */     if (sb.length() > 0) {
/* 264 */       sb.append("property(ies) missing in request");
/* 265 */       throw new ProxyAuthException(sb.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toHttpString()
/*     */   {
/* 273 */     StringBuilder sb = new StringBuilder();
/*     */ 
/* 275 */     sb.append(getHttpVerb()).append(' ').append(getHttpURI()).append(' ').append(getHttpVersion()).append("\r\n");
/*     */ 
/* 278 */     boolean hostHeaderFound = false;
/*     */ 
/* 280 */     if (getHeaders() != null) {
/* 281 */       for (Iterator i$ = getHeaders().entrySet().iterator(); i$.hasNext(); ) { header = (Map.Entry)i$.next();
/*     */ 
/* 283 */         if (!hostHeaderFound) {
/* 284 */           hostHeaderFound = ((String)header.getKey()).equalsIgnoreCase("host");
/*     */         }
/*     */ 
/* 287 */         for (String value : (List)header.getValue())
/* 288 */           sb.append((String)header.getKey()).append(": ").append(value).append("\r\n");
/*     */       }
/*     */       Map.Entry header;
/* 293 */       if ((!hostHeaderFound) && (getHttpVersion() == "HTTP/1.1"))
/*     */       {
/* 295 */         sb.append("Host: ").append(getHost()).append("\r\n");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 300 */     sb.append("\r\n");
/*     */ 
/* 302 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.HttpProxyRequest
 * JD-Core Version:    0.6.0
 */
/*     */ package org.apache.mina.proxy.handlers.http.basic;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.proxy.ProxyAuthException;
/*     */ import org.apache.mina.proxy.handlers.http.AbstractAuthLogicHandler;
/*     */ import org.apache.mina.proxy.handlers.http.HttpProxyRequest;
/*     */ import org.apache.mina.proxy.handlers.http.HttpProxyResponse;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.StringUtilities;
/*     */ import org.apache.mina.util.Base64;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class HttpBasicAuthLogicHandler extends AbstractAuthLogicHandler
/*     */ {
/*  46 */   private static final Logger logger = LoggerFactory.getLogger(HttpBasicAuthLogicHandler.class);
/*     */ 
/*     */   public HttpBasicAuthLogicHandler(ProxyIoSession proxyIoSession)
/*     */     throws ProxyAuthException
/*     */   {
/*  54 */     super(proxyIoSession);
/*     */ 
/*  56 */     if ((this.request == null) || (!(this.request instanceof HttpProxyRequest))) {
/*  57 */       throw new IllegalArgumentException("request parameter should be a non null HttpProxyRequest instance");
/*     */     }
/*     */ 
/*  61 */     ((HttpProxyRequest)this.request).checkRequiredProperties(new String[] { "USER", "PWD" });
/*     */   }
/*     */ 
/*     */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*     */     throws ProxyAuthException
/*     */   {
/*  72 */     logger.debug(" doHandshake()");
/*     */ 
/*  74 */     if (this.step > 0) {
/*  75 */       throw new ProxyAuthException("Authentication request already sent");
/*     */     }
/*     */ 
/*  79 */     HttpProxyRequest req = (HttpProxyRequest)this.request;
/*  80 */     Map headers = req.getHeaders() != null ? req.getHeaders() : new HashMap();
/*     */ 
/*  83 */     String username = (String)req.getProperties().get("USER");
/*     */ 
/*  85 */     String password = (String)req.getProperties().get("PWD");
/*     */ 
/*  88 */     StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "Basic " + createAuthorization(username, password), true);
/*     */ 
/*  91 */     StringUtilities.addValueToHeader(headers, "Keep-Alive", "300", true);
/*     */ 
/*  93 */     StringUtilities.addValueToHeader(headers, "Proxy-Connection", "keep-Alive", true);
/*     */ 
/*  95 */     req.setHeaders(headers);
/*     */ 
/*  97 */     writeRequest(nextFilter, req);
/*  98 */     this.step += 1;
/*     */   }
/*     */ 
/*     */   public static String createAuthorization(String username, String password)
/*     */   {
/* 110 */     return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
/*     */   }
/*     */ 
/*     */   public void handleResponse(HttpProxyResponse response)
/*     */     throws ProxyAuthException
/*     */   {
/* 120 */     if (response.getStatusCode() != 407)
/* 121 */       throw new ProxyAuthException("Received error response code (" + response.getStatusLine() + ").");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.basic.HttpBasicAuthLogicHandler
 * JD-Core Version:    0.6.0
 */
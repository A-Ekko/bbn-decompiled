/*     */ package org.apache.mina.proxy.handlers.http;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.mina.core.filterchain.IoFilter.NextFilter;
/*     */ import org.apache.mina.proxy.ProxyAuthException;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.StringUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class HttpSmartProxyHandler extends AbstractHttpLogicHandler
/*     */ {
/*  42 */   private static final Logger logger = LoggerFactory.getLogger(HttpSmartProxyHandler.class);
/*     */ 
/*  48 */   private boolean requestSent = false;
/*     */   private AbstractAuthLogicHandler authHandler;
/*     */ 
/*     */   public HttpSmartProxyHandler(ProxyIoSession proxyIoSession)
/*     */   {
/*  56 */     super(proxyIoSession);
/*     */   }
/*     */ 
/*     */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*     */     throws ProxyAuthException
/*     */   {
/*  64 */     logger.debug(" doHandshake()");
/*     */ 
/*  66 */     if (this.authHandler != null) {
/*  67 */       this.authHandler.doHandshake(nextFilter);
/*     */     } else {
/*  69 */       if (this.requestSent) {
/*  70 */         throw new ProxyAuthException("Authentication request already sent");
/*     */       }
/*     */ 
/*  74 */       logger.debug("  sending HTTP request");
/*     */ 
/*  77 */       HttpProxyRequest req = (HttpProxyRequest)getProxyIoSession().getRequest();
/*     */ 
/*  79 */       Map headers = req.getHeaders() != null ? req.getHeaders() : new HashMap();
/*     */ 
/*  82 */       StringUtilities.addValueToHeader(headers, "Keep-Alive", "300", true);
/*     */ 
/*  84 */       StringUtilities.addValueToHeader(headers, "Proxy-Connection", "keep-Alive", true);
/*     */ 
/*  86 */       req.setHeaders(headers);
/*     */ 
/*  88 */       writeRequest(nextFilter, req);
/*  89 */       this.requestSent = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void autoSelectAuthHandler(HttpProxyResponse response)
/*     */     throws ProxyAuthException
/*     */   {
/* 101 */     List values = (List)response.getHeaders().get("Proxy-Authenticate");
/*     */     Iterator i$;
/* 103 */     if ((values == null) || (values.size() == 0)) {
/* 104 */       this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(getProxyIoSession());
/*     */     }
/* 107 */     else if (getProxyIoSession().getPreferedOrder() == null) {
/* 108 */       for (String proxyAuthHeader : values) {
/* 109 */         proxyAuthHeader = proxyAuthHeader.toLowerCase();
/*     */         try
/*     */         {
/* 114 */           if (proxyAuthHeader.contains("ntlm")) {
/* 115 */             this.authHandler = HttpAuthenticationMethods.NTLM.getNewHandler(getProxyIoSession());
/*     */ 
/* 117 */             break;
/* 118 */           }if (proxyAuthHeader.contains("digest")) {
/* 119 */             this.authHandler = HttpAuthenticationMethods.DIGEST.getNewHandler(getProxyIoSession());
/*     */ 
/* 121 */             break;
/* 122 */           }if (proxyAuthHeader.contains("basic")) {
/* 123 */             this.authHandler = HttpAuthenticationMethods.BASIC.getNewHandler(getProxyIoSession());
/*     */ 
/* 125 */             break;
/*     */           }
/*     */         } catch (Exception ex) {
/* 128 */           logger.debug("Following exception occured:", ex);
/*     */         }
/*     */       }
/*     */ 
/* 132 */       if (this.authHandler == null) {
/* 133 */         this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(getProxyIoSession());
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 138 */       for (i$ = getProxyIoSession().getPreferedOrder().iterator(); i$.hasNext(); ) { method = (HttpAuthenticationMethods)i$.next();
/*     */ 
/* 140 */         if (this.authHandler != null)
/*     */         {
/*     */           break;
/*     */         }
/* 144 */         if (method == HttpAuthenticationMethods.NO_AUTH) {
/* 145 */           this.authHandler = HttpAuthenticationMethods.NO_AUTH.getNewHandler(getProxyIoSession());
/*     */ 
/* 147 */           break;
/*     */         }
/*     */ 
/* 150 */         for (String proxyAuthHeader : values) {
/* 151 */           proxyAuthHeader = proxyAuthHeader.toLowerCase();
/*     */           try
/*     */           {
/* 155 */             if ((proxyAuthHeader.contains("basic")) && (method == HttpAuthenticationMethods.BASIC))
/*     */             {
/* 157 */               this.authHandler = HttpAuthenticationMethods.BASIC.getNewHandler(getProxyIoSession());
/*     */ 
/* 159 */               break;
/* 160 */             }if ((proxyAuthHeader.contains("digest")) && (method == HttpAuthenticationMethods.DIGEST))
/*     */             {
/* 162 */               this.authHandler = HttpAuthenticationMethods.DIGEST.getNewHandler(getProxyIoSession());
/*     */ 
/* 164 */               break;
/* 165 */             }if ((proxyAuthHeader.contains("ntlm")) && (method == HttpAuthenticationMethods.NTLM))
/*     */             {
/* 167 */               this.authHandler = HttpAuthenticationMethods.NTLM.getNewHandler(getProxyIoSession());
/*     */ 
/* 169 */               break;
/*     */             }
/*     */           } catch (Exception ex) {
/* 172 */             logger.debug("Following exception occured:", ex);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     HttpAuthenticationMethods method;
/* 179 */     if (this.authHandler == null)
/* 180 */       throw new ProxyAuthException("Unknown authentication mechanism(s): " + values);
/*     */   }
/*     */ 
/*     */   public void handleResponse(HttpProxyResponse response)
/*     */     throws ProxyAuthException
/*     */   {
/* 193 */     if ((!isHandshakeComplete()) && (("close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Proxy-Connection"))) || ("close".equalsIgnoreCase(StringUtilities.getSingleValuedHeader(response.getHeaders(), "Connection")))))
/*     */     {
/* 200 */       getProxyIoSession().setReconnectionNeeded(true);
/*     */     }
/*     */ 
/* 203 */     if (response.getStatusCode() == 407) {
/* 204 */       if (this.authHandler == null) {
/* 205 */         autoSelectAuthHandler(response);
/*     */       }
/* 207 */       this.authHandler.handleResponse(response);
/*     */     } else {
/* 209 */       throw new ProxyAuthException("Received error response code (" + response.getStatusLine() + ").");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.HttpSmartProxyHandler
 * JD-Core Version:    0.6.0
 */
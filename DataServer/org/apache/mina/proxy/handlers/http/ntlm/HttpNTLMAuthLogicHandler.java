/*     */ package org.apache.mina.proxy.handlers.http.ntlm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class HttpNTLMAuthLogicHandler extends AbstractAuthLogicHandler
/*     */ {
/*  48 */   private static final Logger logger = LoggerFactory.getLogger(HttpNTLMAuthLogicHandler.class);
/*     */ 
/*  54 */   private byte[] challengePacket = null;
/*     */ 
/*     */   public HttpNTLMAuthLogicHandler(ProxyIoSession proxyIoSession)
/*     */     throws ProxyAuthException
/*     */   {
/*  61 */     super(proxyIoSession);
/*     */ 
/*  63 */     if ((this.request == null) || (!(this.request instanceof HttpProxyRequest))) {
/*  64 */       throw new IllegalArgumentException("request parameter should be a non null HttpProxyRequest instance");
/*     */     }
/*     */ 
/*  68 */     ((HttpProxyRequest)this.request).checkRequiredProperties(new String[] { "USER", "PWD", "DOMAIN", "WORKSTATION" });
/*     */   }
/*     */ 
/*     */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*     */     throws ProxyAuthException
/*     */   {
/*  80 */     logger.debug(" doHandshake()");
/*     */ 
/*  82 */     if ((this.step > 0) && (this.challengePacket == null)) {
/*  83 */       throw new IllegalStateException("Challenge packet not received");
/*     */     }
/*  85 */     HttpProxyRequest req = (HttpProxyRequest)this.request;
/*  86 */     Map headers = req.getHeaders() != null ? req.getHeaders() : new HashMap();
/*     */ 
/*  89 */     String domain = (String)req.getProperties().get("DOMAIN");
/*     */ 
/*  91 */     String workstation = (String)req.getProperties().get("WORKSTATION");
/*     */ 
/*  94 */     if (this.step > 0) {
/*  95 */       logger.debug("  sending NTLM challenge response");
/*     */ 
/*  97 */       byte[] challenge = NTLMUtilities.extractChallengeFromType2Message(this.challengePacket);
/*     */ 
/*  99 */       int serverFlags = NTLMUtilities.extractFlagsFromType2Message(this.challengePacket);
/*     */ 
/* 102 */       String username = (String)req.getProperties().get("USER");
/*     */ 
/* 104 */       String password = (String)req.getProperties().get("PWD");
/*     */ 
/* 107 */       byte[] authenticationPacket = NTLMUtilities.createType3Message(username, password, challenge, domain, workstation, Integer.valueOf(serverFlags), null);
/*     */ 
/* 111 */       StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "NTLM " + new String(Base64.encodeBase64(authenticationPacket)), true);
/*     */     }
/*     */     else
/*     */     {
/* 118 */       logger.debug("  sending HTTP request");
/*     */ 
/* 120 */       byte[] negotiationPacket = NTLMUtilities.createType1Message(workstation, domain, null, null);
/*     */ 
/* 122 */       StringUtilities.addValueToHeader(headers, "Proxy-Authorization", "NTLM " + new String(Base64.encodeBase64(negotiationPacket)), true);
/*     */     }
/*     */ 
/* 133 */     StringUtilities.addValueToHeader(headers, "Keep-Alive", "300", true);
/*     */ 
/* 135 */     StringUtilities.addValueToHeader(headers, "Proxy-Connection", "keep-Alive", true);
/*     */ 
/* 137 */     req.setHeaders(headers);
/*     */ 
/* 139 */     writeRequest(nextFilter, req);
/* 140 */     this.step += 1;
/*     */   }
/*     */ 
/*     */   private String getNTLMHeader(HttpProxyResponse response)
/*     */   {
/* 148 */     List values = (List)response.getHeaders().get("Proxy-Authenticate");
/*     */ 
/* 150 */     for (String s : values) {
/* 151 */       if (s.startsWith("NTLM")) {
/* 152 */         return s;
/*     */       }
/*     */     }
/*     */ 
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public void handleResponse(HttpProxyResponse response)
/*     */     throws ProxyAuthException
/*     */   {
/* 165 */     if (this.step == 0) {
/* 166 */       String challengeResponse = getNTLMHeader(response);
/* 167 */       this.step = 1;
/*     */ 
/* 169 */       if ((challengeResponse == null) || (challengeResponse.length() < 5))
/*     */       {
/* 172 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 178 */     if (this.step == 1)
/*     */     {
/* 181 */       String challengeResponse = getNTLMHeader(response);
/*     */ 
/* 183 */       if ((challengeResponse == null) || (challengeResponse.length() < 5)) {
/* 184 */         throw new ProxyAuthException("Unexpected error while reading server challenge !");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 189 */         this.challengePacket = Base64.decodeBase64(challengeResponse.substring(5).getBytes(this.proxyIoSession.getCharsetName()));
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/* 193 */         throw new ProxyAuthException("Unable to decode the base64 encoded NTLM challenge", e);
/*     */       }
/*     */ 
/* 196 */       this.step = 2;
/*     */     } else {
/* 198 */       throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.ntlm.HttpNTLMAuthLogicHandler
 * JD-Core Version:    0.6.0
 */
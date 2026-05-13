/*     */ package org.apache.mina.proxy.handlers.http.digest;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.SecureRandom;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class HttpDigestAuthLogicHandler extends AbstractAuthLogicHandler
/*     */ {
/*  52 */   private static final Logger logger = LoggerFactory.getLogger(HttpDigestAuthLogicHandler.class);
/*     */ 
/*  58 */   private HashMap<String, String> directives = null;
/*     */   private HttpProxyResponse response;
/*     */   private static SecureRandom rnd;
/*     */ 
/*     */   public HttpDigestAuthLogicHandler(ProxyIoSession proxyIoSession)
/*     */     throws ProxyAuthException
/*     */   {
/*  78 */     super(proxyIoSession);
/*     */ 
/*  80 */     if ((this.request == null) || (!(this.request instanceof HttpProxyRequest))) {
/*  81 */       throw new IllegalArgumentException("request parameter should be a non null HttpProxyRequest instance");
/*     */     }
/*     */ 
/*  85 */     ((HttpProxyRequest)this.request).checkRequiredProperties(new String[] { "USER", "PWD" });
/*     */   }
/*     */ 
/*     */   public void doHandshake(IoFilter.NextFilter nextFilter)
/*     */     throws ProxyAuthException
/*     */   {
/*  92 */     logger.debug(" doHandshake()");
/*     */ 
/*  94 */     if ((this.step > 0) && (this.directives == null)) {
/*  95 */       throw new ProxyAuthException("Authentication challenge not received");
/*     */     }
/*     */ 
/*  98 */     HttpProxyRequest req = (HttpProxyRequest)this.request;
/*  99 */     Map headers = req.getHeaders() != null ? req.getHeaders() : new HashMap();
/*     */ 
/* 102 */     if (this.step > 0) {
/* 103 */       logger.debug("  sending DIGEST challenge response");
/*     */ 
/* 105 */       HashMap map = new HashMap();
/* 106 */       map.put("username", req.getProperties().get("USER"));
/*     */ 
/* 108 */       StringUtilities.copyDirective(this.directives, map, "realm");
/* 109 */       StringUtilities.copyDirective(this.directives, map, "uri");
/* 110 */       StringUtilities.copyDirective(this.directives, map, "opaque");
/* 111 */       StringUtilities.copyDirective(this.directives, map, "nonce");
/* 112 */       String algorithm = StringUtilities.copyDirective(this.directives, map, "algorithm");
/*     */ 
/* 116 */       if ((algorithm != null) && (!"md5".equalsIgnoreCase(algorithm)) && (!"md5-sess".equalsIgnoreCase(algorithm)))
/*     */       {
/* 118 */         throw new ProxyAuthException("Unknown algorithm required by server");
/*     */       }
/*     */ 
/* 123 */       String qop = (String)this.directives.get("qop");
/* 124 */       if (qop != null) {
/* 125 */         StringTokenizer st = new StringTokenizer(qop, ",");
/* 126 */         String token = null;
/*     */ 
/* 128 */         while (st.hasMoreTokens()) {
/* 129 */           String tk = st.nextToken();
/* 130 */           if ("auth".equalsIgnoreCase(token)) {
/*     */             break;
/*     */           }
/* 133 */           int pos = Arrays.binarySearch(DigestUtilities.SUPPORTED_QOPS, tk);
/*     */ 
/* 135 */           if (pos > -1) {
/* 136 */             token = tk;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 141 */         if (token != null) {
/* 142 */           map.put("qop", token);
/*     */ 
/* 144 */           byte[] nonce = new byte[8];
/* 145 */           rnd.nextBytes(nonce);
/*     */           try
/*     */           {
/* 148 */             String cnonce = new String(Base64.encodeBase64(nonce), this.proxyIoSession.getCharsetName());
/*     */ 
/* 151 */             map.put("cnonce", cnonce);
/*     */           } catch (UnsupportedEncodingException e) {
/* 153 */             throw new ProxyAuthException("Unable to encode cnonce", e);
/*     */           }
/*     */         }
/*     */         else {
/* 157 */           throw new ProxyAuthException("No supported qop option available");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 162 */       map.put("nc", "00000001");
/* 163 */       map.put("uri", req.getHttpURI());
/*     */       try
/*     */       {
/* 167 */         map.put("response", DigestUtilities.computeResponseValue(this.proxyIoSession.getSession(), map, req.getHttpVerb().toUpperCase(), (String)req.getProperties().get("PWD"), this.proxyIoSession.getCharsetName(), this.response.getBody()));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 176 */         throw new ProxyAuthException("Digest response computing failed", e);
/*     */       }
/*     */ 
/* 181 */       StringBuilder sb = new StringBuilder("Digest ");
/* 182 */       boolean addSeparator = false;
/*     */ 
/* 184 */       for (String key : map.keySet())
/*     */       {
/* 186 */         if (addSeparator)
/* 187 */           sb.append(", ");
/*     */         else {
/* 189 */           addSeparator = true;
/*     */         }
/*     */ 
/* 192 */         boolean quotedValue = (!"qop".equals(key)) && (!"nc".equals(key));
/*     */ 
/* 194 */         sb.append(key);
/* 195 */         if (quotedValue)
/* 196 */           sb.append("=\"").append((String)map.get(key)).append('"');
/*     */         else {
/* 198 */           sb.append('=').append((String)map.get(key));
/*     */         }
/*     */       }
/*     */ 
/* 202 */       StringUtilities.addValueToHeader(headers, "Proxy-Authorization", sb.toString(), true);
/*     */     }
/*     */ 
/* 206 */     StringUtilities.addValueToHeader(headers, "Keep-Alive", "300", true);
/*     */ 
/* 208 */     StringUtilities.addValueToHeader(headers, "Proxy-Connection", "keep-Alive", true);
/*     */ 
/* 210 */     req.setHeaders(headers);
/*     */ 
/* 212 */     writeRequest(nextFilter, req);
/* 213 */     this.step += 1;
/*     */   }
/*     */ 
/*     */   public void handleResponse(HttpProxyResponse response)
/*     */     throws ProxyAuthException
/*     */   {
/* 220 */     this.response = response;
/*     */ 
/* 222 */     if (this.step == 0) {
/* 223 */       if ((response.getStatusCode() != 401) && (response.getStatusCode() != 407))
/*     */       {
/* 225 */         throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
/*     */       }
/*     */ 
/* 232 */       List values = (List)response.getHeaders().get("Proxy-Authenticate");
/*     */ 
/* 234 */       String challengeResponse = null;
/*     */ 
/* 236 */       for (String s : values) {
/* 237 */         if (s.startsWith("Digest")) {
/* 238 */           challengeResponse = s;
/* 239 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 243 */       if (challengeResponse == null) {
/* 244 */         throw new ProxyAuthException("Server doesn't support digest authentication method !");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 249 */         this.directives = StringUtilities.parseDirectives(challengeResponse.substring(7).getBytes(this.proxyIoSession.getCharsetName()));
/*     */       }
/*     */       catch (Exception e) {
/* 252 */         throw new ProxyAuthException("Parsing of server digest directives failed", e);
/*     */       }
/*     */ 
/* 255 */       this.step = 1;
/*     */     } else {
/* 257 */       throw new ProxyAuthException("Received unexpected response code (" + response.getStatusLine() + ").");
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  70 */       rnd = SecureRandom.getInstance("SHA1PRNG");
/*     */     } catch (NoSuchAlgorithmException e) {
/*  72 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.digest.HttpDigestAuthLogicHandler
 * JD-Core Version:    0.6.0
 */
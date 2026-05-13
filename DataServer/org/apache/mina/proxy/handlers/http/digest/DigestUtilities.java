/*     */ package org.apache.mina.proxy.handlers.http.digest;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.HashMap;
/*     */ import javax.security.sasl.AuthenticationException;
/*     */ import org.apache.mina.core.session.IoSession;
/*     */ import org.apache.mina.proxy.session.ProxyIoSession;
/*     */ import org.apache.mina.proxy.utils.ByteUtilities;
/*     */ import org.apache.mina.proxy.utils.StringUtilities;
/*     */ 
/*     */ public class DigestUtilities
/*     */ {
/*  43 */   public static final String SESSION_HA1 = DigestUtilities.class + ".SessionHA1";
/*     */   private static MessageDigest md5;
/*     */   public static final String[] SUPPORTED_QOPS;
/*     */ 
/*     */   public static String computeResponseValue(IoSession session, HashMap<String, String> map, String method, String pwd, String charsetName, String body)
/*     */     throws AuthenticationException, UnsupportedEncodingException
/*     */   {
/*  70 */     boolean isMD5Sess = "md5-sess".equalsIgnoreCase(StringUtilities.getDirectiveValue(map, "algorithm", false));
/*     */     byte[] hA1;
/*  73 */     if ((isMD5Sess) || (session.getAttribute(SESSION_HA1) == null))
/*     */     {
/*  75 */       StringBuilder sb = new StringBuilder();
/*  76 */       sb.append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "username", true))).append(':');
/*     */ 
/*  81 */       String realm = StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "realm", false));
/*     */ 
/*  83 */       if (realm != null) {
/*  84 */         sb.append(realm);
/*     */       }
/*     */ 
/*  87 */       sb.append(':').append(pwd);
/*     */ 
/*  89 */       if (isMD5Sess)
/*     */       {
/*     */         byte[] prehA1;
/*  91 */         synchronized (md5) {
/*  92 */           md5.reset();
/*  93 */           prehA1 = md5.digest(sb.toString().getBytes(charsetName));
/*     */         }
/*     */ 
/*  96 */         sb = new StringBuilder();
/*  97 */         sb.append(ByteUtilities.asHex(prehA1));
/*  98 */         sb.append(':').append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "nonce", true)));
/*     */ 
/* 101 */         sb.append(':').append(StringUtilities.stringTo8859_1(StringUtilities.getDirectiveValue(map, "cnonce", true)));
/*     */         byte[] hA1;
/* 105 */         synchronized (md5) {
/* 106 */           md5.reset();
/* 107 */           hA1 = md5.digest(sb.toString().getBytes(charsetName));
/*     */         }
/*     */ 
/* 110 */         session.setAttribute(SESSION_HA1, hA1);
/*     */       }
/*     */       else
/*     */       {
/*     */         byte[] hA1;
/* 112 */         synchronized (md5) {
/* 113 */           md5.reset();
/* 114 */           hA1 = md5.digest(sb.toString().getBytes(charsetName));
/*     */         }
/*     */       }
/*     */     } else {
/* 118 */       hA1 = (byte[])(byte[])session.getAttribute(SESSION_HA1);
/*     */     }
/*     */ 
/* 121 */     StringBuilder sb = new StringBuilder(method);
/* 122 */     sb.append(':');
/* 123 */     sb.append(StringUtilities.getDirectiveValue(map, "uri", false));
/*     */ 
/* 125 */     String qop = StringUtilities.getDirectiveValue(map, "qop", false);
/* 126 */     if ("auth-int".equalsIgnoreCase(qop)) {
/* 127 */       ProxyIoSession proxyIoSession = (ProxyIoSession)session.getAttribute(ProxyIoSession.PROXY_SESSION);
/*     */       byte[] hEntity;
/* 131 */       synchronized (md5) {
/* 132 */         md5.reset();
/* 133 */         hEntity = md5.digest(body.getBytes(proxyIoSession.getCharsetName()));
/*     */       }
/*     */ 
/* 136 */       sb.append(':').append(hEntity);
/*     */     }
/*     */     byte[] hA2;
/* 140 */     synchronized (md5) {
/* 141 */       md5.reset();
/* 142 */       hA2 = md5.digest(sb.toString().getBytes(charsetName));
/*     */     }
/*     */ 
/* 145 */     sb = new StringBuilder();
/* 146 */     sb.append(ByteUtilities.asHex(hA1));
/* 147 */     sb.append(':').append(StringUtilities.getDirectiveValue(map, "nonce", true));
/*     */ 
/* 149 */     sb.append(":00000001:");
/*     */ 
/* 151 */     sb.append(StringUtilities.getDirectiveValue(map, "cnonce", true));
/* 152 */     sb.append(':').append(qop).append(':');
/* 153 */     sb.append(ByteUtilities.asHex(hA2));
/*     */     byte[] hFinal;
/* 156 */     synchronized (md5) {
/* 157 */       md5.reset();
/* 158 */       hFinal = md5.digest(sb.toString().getBytes(charsetName));
/*     */     }
/*     */ 
/* 161 */     return (String)(String)ByteUtilities.asHex(hFinal);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  51 */       md5 = MessageDigest.getInstance("MD5");
/*     */     } catch (NoSuchAlgorithmException e) {
/*  53 */       throw new RuntimeException(e);
/*     */     }
/*     */ 
/*  57 */     SUPPORTED_QOPS = new String[] { "auth", "auth-int" };
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.mina.proxy.handlers.http.digest.DigestUtilities
 * JD-Core Version:    0.6.0
 */
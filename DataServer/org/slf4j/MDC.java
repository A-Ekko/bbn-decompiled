/*     */ package org.slf4j;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.slf4j.helpers.Util;
/*     */ import org.slf4j.impl.StaticMDCBinder;
/*     */ import org.slf4j.spi.MDCAdapter;
/*     */ 
/*     */ public class MDC
/*     */ {
/*     */   static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
/*     */   static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
/*     */   static MDCAdapter mdcAdapter;
/*     */ 
/*     */   public static void put(String key, String val)
/*     */     throws IllegalArgumentException
/*     */   {
/* 102 */     if (key == null) {
/* 103 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/* 105 */     if (mdcAdapter == null) {
/* 106 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 109 */     mdcAdapter.put(key, val);
/*     */   }
/*     */ 
/*     */   public static String get(String key)
/*     */     throws IllegalArgumentException
/*     */   {
/* 122 */     if (key == null) {
/* 123 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */ 
/* 126 */     if (mdcAdapter == null) {
/* 127 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 130 */     return mdcAdapter.get(key);
/*     */   }
/*     */ 
/*     */   public static void remove(String key)
/*     */     throws IllegalArgumentException
/*     */   {
/* 142 */     if (key == null) {
/* 143 */       throw new IllegalArgumentException("key parameter cannot be null");
/*     */     }
/*     */ 
/* 146 */     if (mdcAdapter == null) {
/* 147 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 150 */     mdcAdapter.remove(key);
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 157 */     if (mdcAdapter == null) {
/* 158 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 161 */     mdcAdapter.clear();
/*     */   }
/*     */ 
/*     */   public static Map getCopyOfContextMap()
/*     */   {
/* 172 */     if (mdcAdapter == null) {
/* 173 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 176 */     return mdcAdapter.getCopyOfContextMap();
/*     */   }
/*     */ 
/*     */   public static void setContextMap(Map contextMap)
/*     */   {
/* 188 */     if (mdcAdapter == null) {
/* 189 */       throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
/*     */     }
/*     */ 
/* 192 */     mdcAdapter.setContextMap(contextMap);
/*     */   }
/*     */ 
/*     */   public static MDCAdapter getMDCAdapter()
/*     */   {
/* 203 */     return mdcAdapter;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/*  72 */       mdcAdapter = StaticMDCBinder.SINGLETON.getMDCA();
/*     */     } catch (NoClassDefFoundError ncde) {
/*  74 */       String msg = ncde.getMessage();
/*  75 */       if ((msg != null) && (msg.indexOf("org/slf4j/impl/StaticMDCBinder") != -1)) {
/*  76 */         Util.reportFailure("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
/*     */ 
/*  78 */         Util.reportFailure("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
/*     */       }
/*     */ 
/*  82 */       throw ncde;
/*     */     }
/*     */     catch (Exception e) {
/*  85 */       Util.reportFailure("Could not bind with an instance of class [" + StaticMDCBinder.SINGLETON.getMDCAdapterClassStr() + "]", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.slf4j.MDC
 * JD-Core Version:    0.6.0
 */
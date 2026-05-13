/*     */ package org.apache.mina.proxy.utils;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.security.sasl.AuthenticationException;
/*     */ import javax.security.sasl.SaslException;
/*     */ 
/*     */ public class StringUtilities
/*     */ {
/*     */   public static String getDirectiveValue(HashMap<String, String> directivesMap, String directive, boolean mandatory)
/*     */     throws AuthenticationException
/*     */   {
/*  48 */     String value = (String)directivesMap.get(directive);
/*  49 */     if (value == null) {
/*  50 */       if (mandatory) {
/*  51 */         throw new AuthenticationException("\"" + directive + "\" mandatory directive is missing");
/*     */       }
/*     */ 
/*  54 */       return "";
/*     */     }
/*     */ 
/*  58 */     return value;
/*     */   }
/*     */ 
/*     */   public static String copyDirective(HashMap<String, String> directives, StringBuilder sb, String directive)
/*     */   {
/*  66 */     String directiveValue = (String)directives.get(directive);
/*  67 */     if (directiveValue != null) {
/*  68 */       sb.append(directive).append(" = \"").append(directiveValue).append("\", ");
/*     */     }
/*     */ 
/*  72 */     return directiveValue;
/*     */   }
/*     */ 
/*     */   public static String copyDirective(HashMap<String, String> src, HashMap<String, String> dst, String directive)
/*     */   {
/*  80 */     String directiveValue = (String)src.get(directive);
/*  81 */     if (directiveValue != null) {
/*  82 */       dst.put(directive, directiveValue);
/*     */     }
/*     */ 
/*  85 */     return directiveValue;
/*     */   }
/*     */ 
/*     */   public static HashMap<String, String> parseDirectives(byte[] buf)
/*     */     throws SaslException
/*     */   {
/*  98 */     HashMap map = new HashMap();
/*  99 */     boolean gettingKey = true;
/* 100 */     boolean gettingQuotedValue = false;
/* 101 */     boolean expectSeparator = false;
/*     */ 
/* 104 */     ByteArrayOutputStream key = new ByteArrayOutputStream(10);
/* 105 */     ByteArrayOutputStream value = new ByteArrayOutputStream(10);
/*     */ 
/* 107 */     int i = skipLws(buf, 0);
/* 108 */     while (i < buf.length) {
/* 109 */       byte bch = buf[i];
/*     */ 
/* 111 */       if (gettingKey) {
/* 112 */         if (bch == 44) {
/* 113 */           if (key.size() != 0) {
/* 114 */             throw new SaslException("Directive key contains a ',':" + key);
/*     */           }
/*     */ 
/* 119 */           i = skipLws(buf, i + 1); continue;
/* 120 */         }if (bch == 61) {
/* 121 */           if (key.size() == 0) {
/* 122 */             throw new SaslException("Empty directive key");
/*     */           }
/*     */ 
/* 125 */           gettingKey = false;
/* 126 */           i = skipLws(buf, i + 1);
/*     */ 
/* 129 */           if (i < buf.length) {
/* 130 */             if (buf[i] == 34) {
/* 131 */               gettingQuotedValue = true;
/* 132 */               i++; continue;
/*     */             }
/*     */           }
/* 135 */           throw new SaslException("Valueless directive found: " + key.toString());
/*     */         }
/*     */ 
/* 138 */         if (isLws(bch))
/*     */         {
/* 140 */           i = skipLws(buf, i + 1);
/*     */ 
/* 143 */           if (i < buf.length) {
/* 144 */             if (buf[i] != 61) {
/* 145 */               throw new SaslException("'=' expected after key: " + key.toString());
/*     */             }
/*     */           }
/*     */ 
/* 149 */           throw new SaslException("'=' expected after key: " + key.toString());
/*     */         }
/*     */ 
/* 153 */         key.write(bch);
/* 154 */         i++; continue;
/*     */       }
/* 156 */       if (gettingQuotedValue)
/*     */       {
/* 158 */         if (bch == 92)
/*     */         {
/* 160 */           i++;
/* 161 */           if (i < buf.length) {
/* 162 */             value.write(buf[i]);
/* 163 */             i++; continue;
/*     */           }
/*     */ 
/* 166 */           throw new SaslException("Unmatched quote found for directive: " + key.toString() + " with value: " + value.toString());
/*     */         }
/*     */ 
/* 171 */         if (bch == 34)
/*     */         {
/* 173 */           i++;
/* 174 */           gettingQuotedValue = false;
/* 175 */           expectSeparator = true; continue;
/*     */         }
/* 177 */         value.write(bch);
/* 178 */         i++; continue;
/*     */       }
/* 180 */       if ((isLws(bch)) || (bch == 44))
/*     */       {
/* 182 */         extractDirective(map, key.toString(), value.toString());
/* 183 */         key.reset();
/* 184 */         value.reset();
/* 185 */         gettingKey = true;
/* 186 */         gettingQuotedValue = expectSeparator = 0;
/* 187 */         i = skipLws(buf, i + 1); continue;
/* 188 */       }if (expectSeparator) {
/* 189 */         throw new SaslException("Expecting comma or linear whitespace after quoted string: \"" + value.toString() + "\"");
/*     */       }
/*     */ 
/* 193 */       value.write(bch);
/* 194 */       i++;
/*     */     }
/*     */ 
/* 198 */     if (gettingQuotedValue) {
/* 199 */       throw new SaslException("Unmatched quote found for directive: " + key.toString() + " with value: " + value.toString());
/*     */     }
/*     */ 
/* 204 */     if (key.size() > 0) {
/* 205 */       extractDirective(map, key.toString(), value.toString());
/*     */     }
/*     */ 
/* 208 */     return map;
/*     */   }
/*     */ 
/*     */   private static void extractDirective(HashMap<String, String> map, String key, String value)
/*     */     throws SaslException
/*     */   {
/* 222 */     if (map.get(key) != null) {
/* 223 */       throw new SaslException("Peer sent more than one " + key + " directive");
/*     */     }
/*     */ 
/* 226 */     map.put(key, value);
/*     */   }
/*     */ 
/*     */   public static boolean isLws(byte b)
/*     */   {
/* 239 */     switch (b) {
/*     */     case 9:
/*     */     case 10:
/*     */     case 13:
/*     */     case 32:
/* 244 */       return true;
/*     */     }
/*     */ 
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   private static int skipLws(byte[] buf, int start)
/*     */   {
/* 256 */     for (int i = start; i < buf.length; i++) {
/* 257 */       if (!isLws(buf[i])) {
/* 258 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 262 */     return i;
/*     */   }
/*     */ 
/*     */   public static String stringTo8859_1(String str)
/*     */     throws UnsupportedEncodingException
/*     */   {
/* 275 */     if (str == null) {
/* 276 */       return "";
/*     */     }
/* 278 */     return new String(str.getBytes("UTF8"), "8859_1");
/*     */   }
/*     */ 
/*     */   public static String getSingleValuedHeader(Map<String, List<String>> headers, String key)
/*     */   {
/* 284 */     List values = (List)headers.get(key);
/*     */ 
/* 286 */     if (values == null) {
/* 287 */       return null;
/*     */     }
/* 289 */     if (values.size() > 1) {
/* 290 */       throw new IllegalArgumentException("Header with key [\"" + key + "\"] isn't single valued !");
/*     */     }
/*     */ 
/* 293 */     return (String)values.get(0);
/*     */   }
/*     */ 
/*     */   public static void addValueToHeader(Map<String, List<String>> headers, String key, String value, boolean singleValued)
/*     */   {
/* 300 */     List values = (List)headers.get(key);
/*     */ 
/* 302 */     if (values == null) {
/* 303 */       values = new ArrayList(1);
/* 304 */       headers.put(key, values);
/*     */     }
/*     */ 
/* 307 */     if ((singleValued) && (values.size() == 1))
/* 308 */       values.set(0, value);
/*     */     else
/* 310 */       values.add(value);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.mina.proxy.utils.StringUtilities
 * JD-Core Version:    0.6.0
 */
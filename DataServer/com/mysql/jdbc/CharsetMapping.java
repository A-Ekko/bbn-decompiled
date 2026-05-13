/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class CharsetMapping
/*     */ {
/*  51 */   private static final Properties CHARSET_CONFIG = new Properties();
/*     */   public static final String[] INDEX_TO_CHARSET;
/*     */   private static final Map JAVA_TO_MYSQL_CHARSET_MAP;
/*     */   private static final Map JAVA_UC_TO_MYSQL_CHARSET_MAP;
/*     */   private static final Map MULTIBYTE_CHARSETS;
/*     */   private static final Map MYSQL_TO_JAVA_CHARSET_MAP;
/*     */ 
/*     */   static final String getJavaEncodingForMysqlEncoding(String mysqlEncoding, Connection conn)
/*     */     throws SQLException
/*     */   {
/* 328 */     return (String)MYSQL_TO_JAVA_CHARSET_MAP.get(mysqlEncoding);
/*     */   }
/*     */ 
/*     */   static final String getMysqlEncodingForJavaEncoding(String javaEncodingUC, Connection conn) throws SQLException
/*     */   {
/* 333 */     List mysqlEncodings = (List)JAVA_UC_TO_MYSQL_CHARSET_MAP.get(javaEncodingUC);
/*     */ 
/* 337 */     if (mysqlEncodings != null) {
/* 338 */       Iterator iter = mysqlEncodings.iterator();
/*     */ 
/* 340 */       VersionedStringProperty versionedProp = null;
/*     */ 
/* 342 */       while (iter.hasNext()) {
/* 343 */         VersionedStringProperty propToCheck = (VersionedStringProperty)iter.next();
/*     */ 
/* 346 */         if (conn == null)
/*     */         {
/* 349 */           return propToCheck.toString();
/*     */         }
/*     */ 
/* 352 */         if ((versionedProp != null) && (!versionedProp.preferredValue) && 
/* 353 */           (versionedProp.majorVersion == propToCheck.majorVersion) && (versionedProp.minorVersion == propToCheck.minorVersion) && (versionedProp.subminorVersion == propToCheck.subminorVersion))
/*     */         {
/* 356 */           return versionedProp.toString();
/*     */         }
/*     */ 
/* 360 */         if (!propToCheck.isOkayForVersion(conn)) break;
/* 361 */         if (propToCheck.preferredValue) {
/* 362 */           return propToCheck.toString();
/*     */         }
/*     */ 
/* 365 */         versionedProp = propToCheck;
/*     */       }
/*     */ 
/* 371 */       if (versionedProp != null) {
/* 372 */         return versionedProp.toString();
/*     */       }
/*     */     }
/*     */ 
/* 376 */     return null;
/*     */   }
/*     */ 
/*     */   static final int getNumberOfCharsetsConfigured() {
/* 380 */     return MYSQL_TO_JAVA_CHARSET_MAP.size() / 2;
/*     */   }
/*     */ 
/*     */   static final boolean isAliasForSjis(String encoding)
/*     */   {
/* 385 */     return ("SJIS".equalsIgnoreCase(encoding)) || ("WINDOWS-31J".equalsIgnoreCase(encoding)) || ("MS932".equalsIgnoreCase(encoding)) || ("SHIFT_JIS".equalsIgnoreCase(encoding)) || ("CP943".equalsIgnoreCase(encoding));
/*     */   }
/*     */ 
/*     */   static final boolean isMultibyteCharset(String javaEncodingName)
/*     */   {
/* 394 */     String javaEncodingNameUC = javaEncodingName.toUpperCase(Locale.ENGLISH);
/*     */ 
/* 397 */     return MULTIBYTE_CHARSETS.containsKey(javaEncodingNameUC);
/*     */   }
/*     */ 
/*     */   private static void populateMapWithKeyValuePairs(String configKey, Map mapToPopulate, boolean addVersionedProperties, boolean addUppercaseKeys)
/*     */   {
/* 403 */     String javaToMysqlConfig = CHARSET_CONFIG.getProperty(configKey);
/*     */ 
/* 405 */     if (javaToMysqlConfig != null) {
/* 406 */       List mappings = StringUtils.split(javaToMysqlConfig, ",", true);
/*     */ 
/* 408 */       if (mappings != null) {
/* 409 */         Iterator mappingsIter = mappings.iterator();
/*     */ 
/* 411 */         while (mappingsIter.hasNext()) {
/* 412 */           String aMapping = (String)mappingsIter.next();
/*     */ 
/* 414 */           List parsedPair = StringUtils.split(aMapping, "=", true);
/*     */ 
/* 416 */           if (parsedPair.size() == 2) {
/* 417 */             String key = parsedPair.get(0).toString();
/* 418 */             String value = parsedPair.get(1).toString();
/*     */ 
/* 420 */             if (addVersionedProperties) {
/* 421 */               List versionedProperties = (List)mapToPopulate.get(key);
/*     */ 
/* 424 */               if (versionedProperties == null) {
/* 425 */                 versionedProperties = new ArrayList();
/* 426 */                 mapToPopulate.put(key, versionedProperties);
/*     */               }
/*     */ 
/* 429 */               VersionedStringProperty verProp = new VersionedStringProperty(value);
/*     */ 
/* 431 */               versionedProperties.add(verProp);
/*     */ 
/* 433 */               if (addUppercaseKeys) {
/* 434 */                 String keyUc = key.toUpperCase(Locale.ENGLISH);
/*     */ 
/* 436 */                 versionedProperties = (List)mapToPopulate.get(keyUc);
/*     */ 
/* 439 */                 if (versionedProperties == null) {
/* 440 */                   versionedProperties = new ArrayList();
/* 441 */                   mapToPopulate.put(keyUc, versionedProperties);
/*     */                 }
/*     */ 
/* 445 */                 versionedProperties.add(verProp);
/*     */               }
/*     */             } else {
/* 448 */               mapToPopulate.put(key, value);
/*     */ 
/* 450 */               if (addUppercaseKeys)
/* 451 */                 mapToPopulate.put(key.toUpperCase(Locale.ENGLISH), value);
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 456 */             throw new RuntimeException("Syntax error in Charsets.properties resource for token \"" + aMapping + "\".");
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 463 */         throw new RuntimeException("Missing/corrupt entry for \"" + configKey + "\" in Charsets.properties.");
/*     */       }
/*     */     }
/*     */     else {
/* 467 */       throw new RuntimeException("Could not find configuration value \"" + configKey + "\" in Charsets.properties resource");
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  69 */     InputStream inStream = CharsetMapping.class.getResourceAsStream("Charsets.properties");
/*     */ 
/*  72 */     if (inStream == null) {
/*  73 */       throw new RuntimeException("Unable to initialize character set mapping tables");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  78 */       CHARSET_CONFIG.load(inStream);
/*  79 */       inStream.close();
/*  80 */       inStream = null;
/*     */     } catch (IOException ioEx) {
/*  82 */       throw new RuntimeException("Unable to initialize character set mapping tables");
/*     */     }
/*     */     finally
/*     */     {
/*  86 */       if (inStream != null) {
/*     */         try {
/*  88 */           inStream.close();
/*     */         }
/*     */         catch (IOException ioEx)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*  95 */     HashMap javaToMysqlMap = new HashMap();
/*     */ 
/*  97 */     populateMapWithKeyValuePairs("javaToMysqlMappings", javaToMysqlMap, true, false);
/*     */ 
/*  99 */     JAVA_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(javaToMysqlMap);
/*     */ 
/* 101 */     HashMap mysqlToJavaMap = new HashMap();
/*     */ 
/* 103 */     Set keySet = JAVA_TO_MYSQL_CHARSET_MAP.keySet();
/*     */ 
/* 105 */     Iterator javaCharsets = keySet.iterator();
/*     */ 
/* 107 */     while (javaCharsets.hasNext()) {
/* 108 */       Object javaEncodingName = javaCharsets.next();
/* 109 */       List mysqlEncodingList = (List)JAVA_TO_MYSQL_CHARSET_MAP.get(javaEncodingName);
/*     */ 
/* 112 */       Iterator mysqlEncodings = mysqlEncodingList.iterator();
/*     */ 
/* 114 */       String mysqlEncodingName = null;
/*     */ 
/* 116 */       while (mysqlEncodings.hasNext()) {
/* 117 */         VersionedStringProperty mysqlProp = (VersionedStringProperty)mysqlEncodings.next();
/*     */ 
/* 119 */         mysqlEncodingName = mysqlProp.toString();
/*     */ 
/* 121 */         mysqlToJavaMap.put(mysqlEncodingName, javaEncodingName);
/* 122 */         mysqlToJavaMap.put(mysqlEncodingName.toUpperCase(Locale.ENGLISH), javaEncodingName);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 128 */     mysqlToJavaMap.put("cp932", "Windows-31J");
/* 129 */     mysqlToJavaMap.put("CP932", "Windows-31J");
/*     */ 
/* 131 */     MYSQL_TO_JAVA_CHARSET_MAP = Collections.unmodifiableMap(mysqlToJavaMap);
/*     */ 
/* 133 */     HashMap ucMap = new HashMap(JAVA_TO_MYSQL_CHARSET_MAP.size());
/*     */ 
/* 135 */     Iterator javaNamesKeys = JAVA_TO_MYSQL_CHARSET_MAP.keySet().iterator();
/*     */ 
/* 137 */     while (javaNamesKeys.hasNext()) {
/* 138 */       String key = (String)javaNamesKeys.next();
/*     */ 
/* 140 */       ucMap.put(key.toUpperCase(Locale.ENGLISH), JAVA_TO_MYSQL_CHARSET_MAP.get(key));
/*     */     }
/*     */ 
/* 144 */     JAVA_UC_TO_MYSQL_CHARSET_MAP = Collections.unmodifiableMap(ucMap);
/*     */ 
/* 150 */     HashMap tempMapMulti = new HashMap();
/*     */ 
/* 152 */     populateMapWithKeyValuePairs("multibyteCharsets", tempMapMulti, false, true);
/*     */ 
/* 155 */     MULTIBYTE_CHARSETS = Collections.unmodifiableMap(tempMapMulti);
/*     */ 
/* 157 */     INDEX_TO_CHARSET = new String[99];
/*     */     try
/*     */     {
/* 160 */       INDEX_TO_CHARSET[1] = getJavaEncodingForMysqlEncoding("big5", null);
/* 161 */       INDEX_TO_CHARSET[2] = getJavaEncodingForMysqlEncoding("czech", null);
/* 162 */       INDEX_TO_CHARSET[3] = getJavaEncodingForMysqlEncoding("dec8", null);
/* 163 */       INDEX_TO_CHARSET[4] = getJavaEncodingForMysqlEncoding("dos", null);
/* 164 */       INDEX_TO_CHARSET[5] = getJavaEncodingForMysqlEncoding("german1", null);
/*     */ 
/* 166 */       INDEX_TO_CHARSET[6] = getJavaEncodingForMysqlEncoding("hp8", null);
/* 167 */       INDEX_TO_CHARSET[7] = getJavaEncodingForMysqlEncoding("koi8_ru", null);
/*     */ 
/* 169 */       INDEX_TO_CHARSET[8] = getJavaEncodingForMysqlEncoding("latin1", null);
/*     */ 
/* 171 */       INDEX_TO_CHARSET[9] = getJavaEncodingForMysqlEncoding("latin2", null);
/*     */ 
/* 173 */       INDEX_TO_CHARSET[10] = getJavaEncodingForMysqlEncoding("swe7", null);
/* 174 */       INDEX_TO_CHARSET[11] = getJavaEncodingForMysqlEncoding("usa7", null);
/* 175 */       INDEX_TO_CHARSET[12] = getJavaEncodingForMysqlEncoding("ujis", null);
/* 176 */       INDEX_TO_CHARSET[13] = getJavaEncodingForMysqlEncoding("sjis", null);
/* 177 */       INDEX_TO_CHARSET[14] = getJavaEncodingForMysqlEncoding("cp1251", null);
/*     */ 
/* 179 */       INDEX_TO_CHARSET[15] = getJavaEncodingForMysqlEncoding("danish", null);
/*     */ 
/* 181 */       INDEX_TO_CHARSET[16] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*     */ 
/* 183 */       INDEX_TO_CHARSET[18] = getJavaEncodingForMysqlEncoding("tis620", null);
/*     */ 
/* 185 */       INDEX_TO_CHARSET[19] = getJavaEncodingForMysqlEncoding("euc_kr", null);
/*     */ 
/* 187 */       INDEX_TO_CHARSET[20] = getJavaEncodingForMysqlEncoding("estonia", null);
/*     */ 
/* 189 */       INDEX_TO_CHARSET[21] = getJavaEncodingForMysqlEncoding("hungarian", null);
/*     */ 
/* 191 */       INDEX_TO_CHARSET[22] = getJavaEncodingForMysqlEncoding("koi8_ukr", null);
/*     */ 
/* 193 */       INDEX_TO_CHARSET[23] = getJavaEncodingForMysqlEncoding("win1251ukr", null);
/*     */ 
/* 195 */       INDEX_TO_CHARSET[24] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*     */ 
/* 197 */       INDEX_TO_CHARSET[25] = getJavaEncodingForMysqlEncoding("greek", null);
/*     */ 
/* 199 */       INDEX_TO_CHARSET[26] = getJavaEncodingForMysqlEncoding("win1250", null);
/*     */ 
/* 201 */       INDEX_TO_CHARSET[27] = getJavaEncodingForMysqlEncoding("croat", null);
/*     */ 
/* 203 */       INDEX_TO_CHARSET[28] = getJavaEncodingForMysqlEncoding("gbk", null);
/* 204 */       INDEX_TO_CHARSET[29] = getJavaEncodingForMysqlEncoding("cp1257", null);
/*     */ 
/* 206 */       INDEX_TO_CHARSET[30] = getJavaEncodingForMysqlEncoding("latin5", null);
/*     */ 
/* 208 */       INDEX_TO_CHARSET[31] = getJavaEncodingForMysqlEncoding("latin1_de", null);
/*     */ 
/* 210 */       INDEX_TO_CHARSET[32] = getJavaEncodingForMysqlEncoding("armscii8", null);
/*     */ 
/* 212 */       INDEX_TO_CHARSET[33] = getJavaEncodingForMysqlEncoding("utf8", null);
/* 213 */       INDEX_TO_CHARSET[34] = getJavaEncodingForMysqlEncoding("win1250ch", null);
/*     */ 
/* 215 */       INDEX_TO_CHARSET[35] = getJavaEncodingForMysqlEncoding("ucs2", null);
/* 216 */       INDEX_TO_CHARSET[36] = getJavaEncodingForMysqlEncoding("cp866", null);
/*     */ 
/* 218 */       INDEX_TO_CHARSET[37] = getJavaEncodingForMysqlEncoding("keybcs2", null);
/*     */ 
/* 220 */       INDEX_TO_CHARSET[38] = getJavaEncodingForMysqlEncoding("macce", null);
/*     */ 
/* 222 */       INDEX_TO_CHARSET[39] = getJavaEncodingForMysqlEncoding("macroman", null);
/*     */ 
/* 224 */       INDEX_TO_CHARSET[40] = getJavaEncodingForMysqlEncoding("pclatin2", null);
/*     */ 
/* 226 */       INDEX_TO_CHARSET[41] = getJavaEncodingForMysqlEncoding("latvian", null);
/*     */ 
/* 228 */       INDEX_TO_CHARSET[42] = getJavaEncodingForMysqlEncoding("latvian1", null);
/*     */ 
/* 230 */       INDEX_TO_CHARSET[43] = getJavaEncodingForMysqlEncoding("maccebin", null);
/*     */ 
/* 232 */       INDEX_TO_CHARSET[44] = getJavaEncodingForMysqlEncoding("macceciai", null);
/*     */ 
/* 234 */       INDEX_TO_CHARSET[45] = getJavaEncodingForMysqlEncoding("maccecias", null);
/*     */ 
/* 236 */       INDEX_TO_CHARSET[46] = getJavaEncodingForMysqlEncoding("maccecsas", null);
/*     */ 
/* 238 */       INDEX_TO_CHARSET[47] = getJavaEncodingForMysqlEncoding("latin1bin", null);
/*     */ 
/* 240 */       INDEX_TO_CHARSET[48] = getJavaEncodingForMysqlEncoding("latin1cias", null);
/*     */ 
/* 242 */       INDEX_TO_CHARSET[49] = getJavaEncodingForMysqlEncoding("latin1csas", null);
/*     */ 
/* 244 */       INDEX_TO_CHARSET[50] = getJavaEncodingForMysqlEncoding("cp1251bin", null);
/*     */ 
/* 246 */       INDEX_TO_CHARSET[51] = getJavaEncodingForMysqlEncoding("cp1251cias", null);
/*     */ 
/* 248 */       INDEX_TO_CHARSET[52] = getJavaEncodingForMysqlEncoding("cp1251csas", null);
/*     */ 
/* 250 */       INDEX_TO_CHARSET[53] = getJavaEncodingForMysqlEncoding("macromanbin", null);
/*     */ 
/* 252 */       INDEX_TO_CHARSET[54] = getJavaEncodingForMysqlEncoding("macromancias", null);
/*     */ 
/* 254 */       INDEX_TO_CHARSET[55] = getJavaEncodingForMysqlEncoding("macromanciai", null);
/*     */ 
/* 256 */       INDEX_TO_CHARSET[56] = getJavaEncodingForMysqlEncoding("macromancsas", null);
/*     */ 
/* 258 */       INDEX_TO_CHARSET[57] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*     */ 
/* 260 */       INDEX_TO_CHARSET[63] = getJavaEncodingForMysqlEncoding("binary", null);
/*     */ 
/* 262 */       INDEX_TO_CHARSET[64] = getJavaEncodingForMysqlEncoding("armscii", null);
/*     */ 
/* 264 */       INDEX_TO_CHARSET[65] = getJavaEncodingForMysqlEncoding("ascii", null);
/*     */ 
/* 266 */       INDEX_TO_CHARSET[66] = getJavaEncodingForMysqlEncoding("cp1250", null);
/*     */ 
/* 268 */       INDEX_TO_CHARSET[67] = getJavaEncodingForMysqlEncoding("cp1256", null);
/*     */ 
/* 270 */       INDEX_TO_CHARSET[68] = getJavaEncodingForMysqlEncoding("cp866", null);
/*     */ 
/* 272 */       INDEX_TO_CHARSET[69] = getJavaEncodingForMysqlEncoding("dec8", null);
/* 273 */       INDEX_TO_CHARSET[70] = getJavaEncodingForMysqlEncoding("greek", null);
/*     */ 
/* 275 */       INDEX_TO_CHARSET[71] = getJavaEncodingForMysqlEncoding("hebrew", null);
/*     */ 
/* 277 */       INDEX_TO_CHARSET[72] = getJavaEncodingForMysqlEncoding("hp8", null);
/* 278 */       INDEX_TO_CHARSET[73] = getJavaEncodingForMysqlEncoding("keybcs2", null);
/*     */ 
/* 280 */       INDEX_TO_CHARSET[74] = getJavaEncodingForMysqlEncoding("koi8r", null);
/*     */ 
/* 282 */       INDEX_TO_CHARSET[75] = getJavaEncodingForMysqlEncoding("koi8ukr", null);
/*     */ 
/* 284 */       INDEX_TO_CHARSET[77] = getJavaEncodingForMysqlEncoding("latin2", null);
/*     */ 
/* 286 */       INDEX_TO_CHARSET[78] = getJavaEncodingForMysqlEncoding("latin5", null);
/*     */ 
/* 288 */       INDEX_TO_CHARSET[79] = getJavaEncodingForMysqlEncoding("latin7", null);
/*     */ 
/* 290 */       INDEX_TO_CHARSET[80] = getJavaEncodingForMysqlEncoding("cp850", null);
/*     */ 
/* 292 */       INDEX_TO_CHARSET[81] = getJavaEncodingForMysqlEncoding("cp852", null);
/*     */ 
/* 294 */       INDEX_TO_CHARSET[82] = getJavaEncodingForMysqlEncoding("swe7", null);
/* 295 */       INDEX_TO_CHARSET[83] = getJavaEncodingForMysqlEncoding("utf8", null);
/* 296 */       INDEX_TO_CHARSET[84] = getJavaEncodingForMysqlEncoding("big5", null);
/* 297 */       INDEX_TO_CHARSET[85] = getJavaEncodingForMysqlEncoding("euckr", null);
/*     */ 
/* 299 */       INDEX_TO_CHARSET[86] = getJavaEncodingForMysqlEncoding("gb2312", null);
/*     */ 
/* 301 */       INDEX_TO_CHARSET[87] = getJavaEncodingForMysqlEncoding("gbk", null);
/* 302 */       INDEX_TO_CHARSET[88] = getJavaEncodingForMysqlEncoding("sjis", null);
/* 303 */       INDEX_TO_CHARSET[89] = getJavaEncodingForMysqlEncoding("tis620", null);
/*     */ 
/* 305 */       INDEX_TO_CHARSET[90] = getJavaEncodingForMysqlEncoding("ucs2", null);
/* 306 */       INDEX_TO_CHARSET[91] = getJavaEncodingForMysqlEncoding("ujis", null);
/* 307 */       INDEX_TO_CHARSET[92] = getJavaEncodingForMysqlEncoding("geostd8", null);
/*     */ 
/* 309 */       INDEX_TO_CHARSET[93] = getJavaEncodingForMysqlEncoding("geostd8", null);
/*     */ 
/* 311 */       INDEX_TO_CHARSET[94] = getJavaEncodingForMysqlEncoding("latin1", null);
/*     */ 
/* 313 */       INDEX_TO_CHARSET[95] = getJavaEncodingForMysqlEncoding("cp932", null);
/*     */ 
/* 315 */       INDEX_TO_CHARSET[96] = getJavaEncodingForMysqlEncoding("cp932", null);
/*     */ 
/* 317 */       INDEX_TO_CHARSET[97] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*     */ 
/* 319 */       INDEX_TO_CHARSET[98] = getJavaEncodingForMysqlEncoding("eucjpms", null);
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.CharsetMapping
 * JD-Core Version:    0.6.0
 */
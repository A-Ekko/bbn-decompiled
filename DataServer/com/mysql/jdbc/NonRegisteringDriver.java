/*     */ package com.mysql.jdbc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URLDecoder;
/*     */ import java.sql.Driver;
/*     */ import java.sql.DriverPropertyInfo;
/*     */ import java.sql.SQLException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class NonRegisteringDriver
/*     */   implements Driver
/*     */ {
/*     */   public static final String DBNAME_PROPERTY_KEY = "DBNAME";
/*     */   public static final boolean DEBUG = false;
/*     */   public static final int HOST_NAME_INDEX = 0;
/*     */   public static final String HOST_PROPERTY_KEY = "HOST";
/*     */   public static final String PASSWORD_PROPERTY_KEY = "password";
/*     */   public static final int PORT_NUMBER_INDEX = 1;
/*     */   public static final String PORT_PROPERTY_KEY = "PORT";
/*     */   public static final String PROPERTIES_TRANSFORM_KEY = "propertiesTransform";
/*     */   public static final boolean TRACE = false;
/*     */   public static final String USE_CONFIG_PROPERTY_KEY = "useConfigs";
/*     */   public static final String USER_PROPERTY_KEY = "user";
/*     */ 
/*     */   static int getMajorVersionInternal()
/*     */   {
/* 121 */     return safeIntParse("3");
/*     */   }
/*     */ 
/*     */   static int getMinorVersionInternal()
/*     */   {
/* 130 */     return safeIntParse("1");
/*     */   }
/*     */ 
/*     */   protected static String[] parseHostPortPair(String hostPortPair)
/*     */     throws SQLException
/*     */   {
/* 149 */     int portIndex = hostPortPair.indexOf(":");
/*     */ 
/* 151 */     String[] splitValues = new String[2];
/*     */ 
/* 153 */     String hostname = null;
/*     */ 
/* 155 */     if (portIndex != -1) {
/* 156 */       if (portIndex + 1 < hostPortPair.length()) {
/* 157 */         String portAsString = hostPortPair.substring(portIndex + 1);
/* 158 */         hostname = hostPortPair.substring(0, portIndex);
/*     */ 
/* 160 */         splitValues[0] = hostname;
/*     */ 
/* 162 */         splitValues[1] = portAsString;
/*     */       } else {
/* 164 */         throw new SQLException(Messages.getString("NonRegisteringDriver.37"), "01S00");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 169 */       splitValues[0] = hostPortPair;
/* 170 */       splitValues[1] = null;
/*     */     }
/*     */ 
/* 173 */     return splitValues;
/*     */   }
/*     */ 
/*     */   private static int safeIntParse(String intAsString) {
/*     */     try {
/* 178 */       return Integer.parseInt(intAsString); } catch (NumberFormatException nfe) {
/*     */     }
/* 180 */     return 0;
/*     */   }
/*     */ 
/*     */   public NonRegisteringDriver()
/*     */     throws SQLException
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean acceptsURL(String url)
/*     */     throws SQLException
/*     */   {
/* 210 */     return parseURL(url, null) != null;
/*     */   }
/*     */ 
/*     */   public java.sql.Connection connect(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 259 */     Properties props = null;
/*     */ 
/* 261 */     if ((props = parseURL(url, info)) == null) {
/* 262 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 266 */       Connection newConn = new Connection(host(props), port(props), props, database(props), url, this);
/*     */ 
/* 269 */       return newConn;
/*     */     }
/*     */     catch (SQLException sqlEx)
/*     */     {
/* 273 */       throw sqlEx; } catch (Exception ex) {
/*     */     }
/* 275 */     throw new SQLException(Messages.getString("NonRegisteringDriver.17") + ex.toString() + Messages.getString("NonRegisteringDriver.18"), "08001");
/*     */   }
/*     */ 
/*     */   public String database(Properties props)
/*     */   {
/* 292 */     return props.getProperty("DBNAME");
/*     */   }
/*     */ 
/*     */   public int getMajorVersion()
/*     */   {
/* 301 */     return getMajorVersionInternal();
/*     */   }
/*     */ 
/*     */   public int getMinorVersion()
/*     */   {
/* 310 */     return getMinorVersionInternal();
/*     */   }
/*     */ 
/*     */   public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
/*     */     throws SQLException
/*     */   {
/* 341 */     if (info == null) {
/* 342 */       info = new Properties();
/*     */     }
/*     */ 
/* 345 */     if ((url != null) && (url.startsWith("jdbc:mysql://"))) {
/* 346 */       info = parseURL(url, info);
/*     */     }
/*     */ 
/* 349 */     DriverPropertyInfo hostProp = new DriverPropertyInfo("HOST", info.getProperty("HOST"));
/*     */ 
/* 351 */     hostProp.required = true;
/* 352 */     hostProp.description = Messages.getString("NonRegisteringDriver.3");
/*     */ 
/* 354 */     DriverPropertyInfo portProp = new DriverPropertyInfo("PORT", info.getProperty("PORT", "3306"));
/*     */ 
/* 356 */     portProp.required = false;
/* 357 */     portProp.description = Messages.getString("NonRegisteringDriver.7");
/*     */ 
/* 359 */     DriverPropertyInfo dbProp = new DriverPropertyInfo("DBNAME", info.getProperty("DBNAME"));
/*     */ 
/* 361 */     dbProp.required = false;
/* 362 */     dbProp.description = "Database name";
/*     */ 
/* 364 */     DriverPropertyInfo userProp = new DriverPropertyInfo("user", info.getProperty("user"));
/*     */ 
/* 366 */     userProp.required = true;
/* 367 */     userProp.description = Messages.getString("NonRegisteringDriver.13");
/*     */ 
/* 369 */     DriverPropertyInfo passwordProp = new DriverPropertyInfo("password", info.getProperty("password"));
/*     */ 
/* 372 */     passwordProp.required = true;
/* 373 */     passwordProp.description = Messages.getString("NonRegisteringDriver.16");
/*     */ 
/* 376 */     DriverPropertyInfo[] dpi = ConnectionProperties.exposeAsDriverPropertyInfo(info, 5);
/*     */ 
/* 379 */     dpi[0] = hostProp;
/* 380 */     dpi[1] = portProp;
/* 381 */     dpi[2] = dbProp;
/* 382 */     dpi[3] = userProp;
/* 383 */     dpi[4] = passwordProp;
/*     */ 
/* 385 */     return dpi;
/*     */   }
/*     */ 
/*     */   public String host(Properties props)
/*     */   {
/* 402 */     return props.getProperty("HOST", "localhost");
/*     */   }
/*     */ 
/*     */   public boolean jdbcCompliant()
/*     */   {
/* 418 */     return false;
/*     */   }
/*     */ 
/*     */   public Properties parseURL(String url, Properties defaults)
/*     */     throws SQLException
/*     */   {
/* 436 */     Properties urlProps = defaults != null ? new Properties(defaults) : new Properties();
/*     */ 
/* 439 */     if (url == null) {
/* 440 */       return null;
/*     */     }
/*     */ 
/* 443 */     if (!StringUtils.startsWithIgnoreCase(url, "jdbc:mysql://"))
/*     */     {
/* 445 */       return null;
/*     */     }
/*     */ 
/* 452 */     int index = url.indexOf("?");
/*     */ 
/* 454 */     if (index != -1) {
/* 455 */       String paramString = url.substring(index + 1, url.length());
/* 456 */       url = url.substring(0, index);
/*     */ 
/* 458 */       StringTokenizer queryParams = new StringTokenizer(paramString, "&");
/*     */ 
/* 460 */       while (queryParams.hasMoreTokens()) {
/* 461 */         String parameterValuePair = queryParams.nextToken();
/*     */ 
/* 463 */         int indexOfEquals = StringUtils.indexOfIgnoreCase(0, parameterValuePair, "=");
/*     */ 
/* 465 */         String parameter = null;
/* 466 */         String value = null;
/*     */ 
/* 468 */         if (indexOfEquals != -1) {
/* 469 */           parameter = parameterValuePair.substring(0, indexOfEquals);
/*     */ 
/* 471 */           if (indexOfEquals + 1 < parameterValuePair.length()) {
/* 472 */             value = parameterValuePair.substring(indexOfEquals + 1);
/*     */           }
/*     */         }
/*     */ 
/* 476 */         if ((value != null) && (value.length() > 0) && (parameter != null) && (parameter.length() > 0)) {
/*     */           try
/*     */           {
/* 479 */             urlProps.put(parameter, URLDecoder.decode(value, "UTF-8"));
/*     */           }
/*     */           catch (UnsupportedEncodingException badEncoding) {
/* 482 */             urlProps.put(parameter, value);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 488 */     url = url.substring(13);
/*     */ 
/* 490 */     String hostStuff = null;
/*     */ 
/* 492 */     int slashIndex = url.indexOf("/");
/*     */ 
/* 494 */     if (slashIndex != -1) {
/* 495 */       hostStuff = url.substring(0, slashIndex);
/*     */ 
/* 497 */       if (slashIndex + 1 < url.length())
/* 498 */         urlProps.put("DBNAME", url.substring(slashIndex + 1, url.length()));
/*     */     }
/*     */     else
/*     */     {
/* 502 */       return null;
/*     */     }
/*     */ 
/* 505 */     if ((hostStuff != null) && (hostStuff.length() > 0)) {
/* 506 */       urlProps.put("HOST", hostStuff);
/*     */     }
/*     */ 
/* 509 */     String propertiesTransformClassName = urlProps.getProperty("propertiesTransform");
/*     */ 
/* 512 */     if (propertiesTransformClassName != null) {
/*     */       try {
/* 514 */         ConnectionPropertiesTransform propTransformer = (ConnectionPropertiesTransform)Class.forName(propertiesTransformClassName).newInstance();
/*     */ 
/* 517 */         urlProps = propTransformer.transformProperties(urlProps);
/*     */       } catch (InstantiationException e) {
/* 519 */         throw new SQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */       }
/*     */       catch (IllegalAccessException e)
/*     */       {
/* 526 */         throw new SQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */       }
/*     */       catch (ClassNotFoundException e)
/*     */       {
/* 533 */         throw new SQLException("Unable to create properties transform instance '" + propertiesTransformClassName + "' due to underlying exception: " + e.toString(), "01S00");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 545 */     String configNames = null;
/*     */ 
/* 547 */     if (defaults != null) {
/* 548 */       configNames = defaults.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 551 */     if (configNames == null) {
/* 552 */       configNames = urlProps.getProperty("useConfigs");
/*     */     }
/*     */ 
/* 555 */     if (configNames != null) {
/* 556 */       List splitNames = StringUtils.split(configNames, ",", true);
/*     */ 
/* 558 */       Properties configProps = new Properties();
/*     */ 
/* 560 */       Iterator namesIter = splitNames.iterator();
/*     */ 
/* 562 */       while (namesIter.hasNext()) {
/* 563 */         String configName = (String)namesIter.next();
/*     */         try
/*     */         {
/* 566 */           InputStream configAsStream = getClass().getResourceAsStream("configs/" + configName + ".properties");
/*     */ 
/* 570 */           if (configAsStream == null) {
/* 571 */             throw new SQLException("Can't find configuration template named '" + configName + "'", "01S00");
/*     */           }
/*     */ 
/* 576 */           configProps.load(configAsStream);
/*     */         } catch (IOException ioEx) {
/* 578 */           throw new SQLException("Unable to load configuration template '" + configName + "' due to underlying IOException: " + ioEx, "01S00");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 587 */       Iterator propsIter = urlProps.keySet().iterator();
/*     */ 
/* 589 */       while (propsIter.hasNext()) {
/* 590 */         String key = propsIter.next().toString();
/* 591 */         String property = urlProps.getProperty(key);
/* 592 */         configProps.setProperty(key, property);
/*     */       }
/*     */ 
/* 595 */       urlProps = configProps;
/*     */     }
/*     */ 
/* 600 */     if (defaults != null) {
/* 601 */       Iterator propsIter = defaults.keySet().iterator();
/*     */ 
/* 603 */       while (propsIter.hasNext()) {
/* 604 */         String key = propsIter.next().toString();
/* 605 */         String property = defaults.getProperty(key);
/* 606 */         urlProps.setProperty(key, property);
/*     */       }
/*     */     }
/*     */ 
/* 610 */     return urlProps;
/*     */   }
/*     */ 
/*     */   public int port(Properties props)
/*     */   {
/* 622 */     return Integer.parseInt(props.getProperty("PORT", "3306"));
/*     */   }
/*     */ 
/*     */   public String property(String name, Properties props)
/*     */   {
/* 636 */     return props.getProperty(name);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     com.mysql.jdbc.NonRegisteringDriver
 * JD-Core Version:    0.6.0
 */
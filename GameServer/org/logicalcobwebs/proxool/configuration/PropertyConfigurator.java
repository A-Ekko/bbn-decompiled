/*     */ package org.logicalcobwebs.proxool.configuration;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ 
/*     */ public class PropertyConfigurator
/*     */ {
/*  79 */   private static final Log LOG = LogFactory.getLog(PropertyConfigurator.class);
/*     */   protected static final String PREFIX = "jdbc";
/*     */   private static final String DOT = ".";
/*     */   private static final String EXAMPLE_FORMAT = "jdbc*.*";
/*     */ 
/*     */   public static void configure(String filename)
/*     */     throws ProxoolException
/*     */   {
/*  93 */     Properties properties = new Properties();
/*     */     try {
/*  95 */       properties.load(new FileInputStream(filename));
/*     */     } catch (IOException e) {
/*  97 */       throw new ProxoolException("Couldn't load property file " + filename);
/*     */     }
/*  99 */     configure(properties);
/*     */   }
/*     */ 
/*     */   public static void configure(Properties properties)
/*     */     throws ProxoolException
/*     */   {
/* 108 */     Map propertiesMap = new HashMap();
/* 109 */     Iterator allPropertyKeysIterator = properties.keySet().iterator();
/* 110 */     Properties proxoolProperties = null;
/*     */ 
/* 112 */     while (allPropertyKeysIterator.hasNext()) {
/* 113 */       String key = (String)allPropertyKeysIterator.next();
/* 114 */       String value = properties.getProperty(key);
/*     */ 
/* 116 */       if (key.startsWith("jdbc")) {
/* 117 */         int a = key.indexOf(".");
/* 118 */         if (a == -1) {
/* 119 */           throw new ProxoolException("Property " + key + " must be of the format " + "jdbc*.*");
/*     */         }
/* 121 */         String tag = key.substring(0, a);
/* 122 */         String name = key.substring(a + 1);
/* 123 */         proxoolProperties = (Properties)propertiesMap.get(tag);
/* 124 */         if (proxoolProperties == null) {
/* 125 */           proxoolProperties = new Properties();
/* 126 */           propertiesMap.put(tag, proxoolProperties);
/*     */         }
/* 128 */         proxoolProperties.put(name, value);
/*     */       }
/*     */     }
/*     */ 
/* 132 */     Iterator tags = propertiesMap.keySet().iterator();
/* 133 */     while (tags.hasNext()) {
/* 134 */       proxoolProperties = (Properties)propertiesMap.get(tags.next());
/*     */ 
/* 138 */       String driverClass = proxoolProperties.getProperty("proxool.driver-class");
/* 139 */       String driverUrl = proxoolProperties.getProperty("proxool.driver-url");
/* 140 */       if ((driverClass == null) || (driverUrl == null)) {
/* 141 */         throw new ProxoolException("You must define the proxool.driver-class and the proxool.driver-url.");
/*     */       }
/*     */ 
/* 144 */       String alias = proxoolProperties.getProperty("proxool.alias");
/*     */ 
/* 147 */       StringBuffer url = new StringBuffer();
/* 148 */       url.append("proxool");
/* 149 */       if (alias != null) {
/* 150 */         url.append(".");
/* 151 */         url.append(alias);
/* 152 */         proxoolProperties.remove("proxool.alias");
/*     */       }
/* 154 */       url.append(":");
/* 155 */       url.append(driverClass);
/* 156 */       proxoolProperties.remove("proxool.driver-class");
/* 157 */       url.append(":");
/* 158 */       url.append(driverUrl);
/* 159 */       proxoolProperties.remove("proxool.driver-url");
/* 160 */       if (LOG.isDebugEnabled()) {
/* 161 */         LOG.debug("Created url: " + url);
/*     */       }
/*     */ 
/* 164 */       ProxoolFacade.registerConnectionPool(url.toString(), proxoolProperties);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.configuration.PropertyConfigurator
 * JD-Core Version:    0.6.0
 */
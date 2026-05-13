/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.DefaultRepositorySelector;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.NOPLoggerRepository;
/*     */ import org.apache.log4j.spi.RepositorySelector;
/*     */ import org.apache.log4j.spi.RootLogger;
/*     */ 
/*     */ public class LogManager
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
/*     */   static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
/*  75 */   private static Object guard = null;
/*     */   private static RepositorySelector repositorySelector;
/*     */ 
/*     */   public static void setRepositorySelector(RepositorySelector selector, Object guard)
/*     */     throws IllegalArgumentException
/*     */   {
/* 158 */     if ((guard != null) && (guard != guard)) {
/* 159 */       throw new IllegalArgumentException("Attempted to reset the LoggerFactory without possessing the guard.");
/*     */     }
/*     */ 
/* 163 */     if (selector == null) {
/* 164 */       throw new IllegalArgumentException("RepositorySelector must be non-null.");
/*     */     }
/*     */ 
/* 167 */     guard = guard;
/* 168 */     repositorySelector = selector;
/*     */   }
/*     */ 
/*     */   public static LoggerRepository getLoggerRepository()
/*     */   {
/* 174 */     if (repositorySelector == null) {
/* 175 */       repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
/* 176 */       guard = null;
/* 177 */       LogLog.error("LogMananger.repositorySelector was null likely due to error in class reloading, using NOPLoggerRepository.");
/*     */     }
/* 179 */     return repositorySelector.getLoggerRepository();
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 189 */     return getLoggerRepository().getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 199 */     return getLoggerRepository().getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 209 */     return getLoggerRepository().getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 220 */     return getLoggerRepository().getLogger(name, factory);
/*     */   }
/*     */ 
/*     */   public static Logger exists(String name)
/*     */   {
/* 226 */     return getLoggerRepository().exists(name);
/*     */   }
/*     */ 
/*     */   public static Enumeration getCurrentLoggers()
/*     */   {
/* 232 */     return getLoggerRepository().getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 238 */     getLoggerRepository().shutdown();
/*     */   }
/*     */ 
/*     */   public static void resetConfiguration()
/*     */   {
/* 244 */     getLoggerRepository().resetConfiguration();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  80 */     Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
/*  81 */     repositorySelector = new DefaultRepositorySelector(h);
/*     */ 
/*  84 */     String override = OptionConverter.getSystemProperty("log4j.defaultInitOverride", null);
/*     */ 
/*  89 */     if ((override == null) || ("false".equalsIgnoreCase(override)))
/*     */     {
/*  91 */       String configurationOptionStr = OptionConverter.getSystemProperty("log4j.configuration", null);
/*     */ 
/*  95 */       String configuratorClassName = OptionConverter.getSystemProperty("log4j.configuratorClass", null);
/*     */ 
/*  99 */       URL url = null;
/*     */ 
/* 104 */       if (configurationOptionStr == null) {
/* 105 */         url = Loader.getResource("log4j.xml");
/* 106 */         if (url == null)
/* 107 */           url = Loader.getResource("log4j.properties");
/*     */       }
/*     */       else {
/*     */         try {
/* 111 */           url = new URL(configurationOptionStr);
/*     */         }
/*     */         catch (MalformedURLException ex)
/*     */         {
/* 115 */           url = Loader.getResource(configurationOptionStr);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 122 */       if (url != null) {
/* 123 */         LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");
/*     */         try {
/* 125 */           OptionConverter.selectAndConfigure(url, configuratorClassName, getLoggerRepository());
/*     */         }
/*     */         catch (NoClassDefFoundError e) {
/* 128 */           LogLog.warn("Error during default initialization", e);
/*     */         }
/*     */       } else {
/* 131 */         LogLog.debug("Could not find resource: [" + configurationOptionStr + "].");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.LogManager
 * JD-Core Version:    0.6.0
 */
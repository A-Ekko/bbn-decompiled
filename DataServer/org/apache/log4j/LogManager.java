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
/*     */ import org.apache.log4j.spi.RepositorySelector;
/*     */ import org.apache.log4j.spi.RootCategory;
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
/*  64 */   private static Object guard = null;
/*     */   private static RepositorySelector repositorySelector;
/*     */ 
/*     */   public static void setRepositorySelector(RepositorySelector selector, Object guard)
/*     */     throws IllegalArgumentException
/*     */   {
/* 143 */     if ((guard != null) && (guard != guard)) {
/* 144 */       throw new IllegalArgumentException("Attempted to reset the LoggerFactory without possessing the guard.");
/*     */     }
/*     */ 
/* 148 */     if (selector == null) {
/* 149 */       throw new IllegalArgumentException("RepositorySelector must be non-null.");
/*     */     }
/*     */ 
/* 152 */     guard = guard;
/* 153 */     repositorySelector = selector;
/*     */   }
/*     */ 
/*     */   public static LoggerRepository getLoggerRepository()
/*     */   {
/* 159 */     return repositorySelector.getLoggerRepository();
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 169 */     return repositorySelector.getLoggerRepository().getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 179 */     return repositorySelector.getLoggerRepository().getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 189 */     return repositorySelector.getLoggerRepository().getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 200 */     return repositorySelector.getLoggerRepository().getLogger(name, factory);
/*     */   }
/*     */ 
/*     */   public static Logger exists(String name)
/*     */   {
/* 206 */     return repositorySelector.getLoggerRepository().exists(name);
/*     */   }
/*     */ 
/*     */   public static Enumeration getCurrentLoggers()
/*     */   {
/* 212 */     return repositorySelector.getLoggerRepository().getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 218 */     repositorySelector.getLoggerRepository().shutdown();
/*     */   }
/*     */ 
/*     */   public static void resetConfiguration()
/*     */   {
/* 224 */     repositorySelector.getLoggerRepository().resetConfiguration();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  69 */     Hierarchy h = new Hierarchy(new RootCategory(Level.DEBUG));
/*  70 */     repositorySelector = new DefaultRepositorySelector(h);
/*     */ 
/*  73 */     String override = OptionConverter.getSystemProperty("log4j.defaultInitOverride", null);
/*     */ 
/*  78 */     if ((override == null) || ("false".equalsIgnoreCase(override)))
/*     */     {
/*  80 */       String configurationOptionStr = OptionConverter.getSystemProperty("log4j.configuration", null);
/*     */ 
/*  84 */       String configuratorClassName = OptionConverter.getSystemProperty("log4j.configuratorClass", null);
/*     */ 
/*  88 */       URL url = null;
/*     */ 
/*  93 */       if (configurationOptionStr == null) {
/*  94 */         url = Loader.getResource("log4j.xml");
/*  95 */         if (url == null)
/*  96 */           url = Loader.getResource("log4j.properties");
/*     */       }
/*     */       else {
/*     */         try {
/* 100 */           url = new URL(configurationOptionStr);
/*     */         }
/*     */         catch (MalformedURLException ex)
/*     */         {
/* 104 */           url = Loader.getResource(configurationOptionStr);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 111 */       if (url != null) {
/* 112 */         LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");
/* 113 */         OptionConverter.selectAndConfigure(url, configuratorClassName, getLoggerRepository());
/*     */       }
/*     */       else {
/* 116 */         LogLog.debug("Could not find resource: [" + configurationOptionStr + "].");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.LogManager
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.LocalizedException;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import flex.messaging.util.Trace;
/*     */ import java.io.File;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ 
/*     */ public class FlexConfigurationManager
/*     */   implements ConfigurationManager
/*     */ {
/*     */   static final String DEFAULT_CONFIG_PATH = "/WEB-INF/flex/services-config.xml";
/*  52 */   protected String configurationPath = null;
/*  53 */   protected ConfigurationFileResolver configurationResolver = null;
/*  54 */   protected ConfigurationParser parser = null;
/*     */ 
/*     */   public MessagingConfiguration getMessagingConfiguration(ServletConfig servletConfig)
/*     */   {
/*  58 */     MessagingConfiguration config = new MessagingConfiguration();
/*     */ 
/*  60 */     if (servletConfig != null)
/*     */     {
/*  62 */       String serverInfo = servletConfig.getServletContext().getServerInfo();
/*  63 */       config.getSecuritySettings().setServerInfo(serverInfo);
/*     */     }
/*     */ 
/*  66 */     verifyMinimumJavaVersion();
/*     */ 
/*  68 */     this.parser = getConfigurationParser(servletConfig);
/*     */ 
/*  70 */     if (this.parser == null)
/*     */     {
/*  73 */       LocalizedException lme = new LocalizedException();
/*  74 */       lme.setMessage(10138);
/*  75 */       throw lme;
/*     */     }
/*     */ 
/*  78 */     setupConfigurationPathAndResolver(servletConfig);
/*  79 */     this.parser.parse(this.configurationPath, this.configurationResolver, config);
/*     */ 
/*  81 */     if (servletConfig != null)
/*     */     {
/*  83 */       config.getSystemSettings().setPaths(servletConfig.getServletContext());
/*     */     }
/*     */ 
/*  86 */     return config;
/*     */   }
/*     */ 
/*     */   public void reportTokens()
/*     */   {
/*  91 */     this.parser.reportTokens();
/*     */   }
/*     */ 
/*     */   protected ConfigurationParser getConfigurationParser(ServletConfig servletConfig)
/*     */   {
/*  96 */     ConfigurationParser parser = null;
/*  97 */     Class parserClass = null;
/*  98 */     String className = null;
/*     */ 
/* 101 */     if (servletConfig != null)
/*     */     {
/* 103 */       String p = servletConfig.getInitParameter("services.configuration.parser");
/* 104 */       if (p != null)
/*     */       {
/* 106 */         className = p.trim();
/*     */         try
/*     */         {
/* 109 */           parserClass = ClassUtil.createClass(className);
/* 110 */           parser = (ConfigurationParser)parserClass.newInstance();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 114 */           if (Trace.config)
/*     */           {
/* 116 */             Trace.trace("Could not load configuration parser as: " + className);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 124 */     if (parser == null)
/*     */     {
/*     */       try
/*     */       {
/* 128 */         ClassUtil.createClass("org.apache.xpath.CachedXPathAPI");
/* 129 */         className = "flex.messaging.config.ApacheXPathServerConfigurationParser";
/* 130 */         parserClass = ClassUtil.createClass(className);
/* 131 */         parser = (ConfigurationParser)parserClass.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 135 */         if (Trace.config)
/*     */         {
/* 137 */           Trace.trace("Could not load configuration parser as: " + className);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 143 */     if (parser == null)
/*     */     {
/*     */       try
/*     */       {
/* 147 */         className = "flex.messaging.config.XPathServerConfigurationParser";
/* 148 */         parserClass = ClassUtil.createClass(className);
/*     */ 
/* 150 */         ClassUtil.createClass("javax.xml.xpath.XPathExpressionException");
/*     */ 
/* 152 */         parser = (ConfigurationParser)parserClass.newInstance();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 156 */         if (Trace.config)
/*     */         {
/* 158 */           Trace.trace("Could not load configuration parser as: " + className);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 163 */     if ((Trace.config) && (parser != null))
/*     */     {
/* 165 */       Trace.trace("Services Configuration Parser: " + parser.getClass().getName());
/*     */     }
/*     */ 
/* 168 */     return parser;
/*     */   }
/*     */ 
/*     */   protected void setupConfigurationPathAndResolver(ServletConfig servletConfig)
/*     */   {
/* 180 */     if (servletConfig != null)
/*     */     {
/* 182 */       String p = servletConfig.getInitParameter("services.configuration.file");
/* 183 */       if ((p == null) || (p.trim().length() == 0))
/*     */       {
/* 186 */         this.configurationPath = "/WEB-INF/flex/services-config.xml";
/* 187 */         this.configurationResolver = new ServletResourceResolver(servletConfig.getServletContext());
/*     */       }
/*     */       else
/*     */       {
/* 192 */         this.configurationPath = p.trim();
/*     */ 
/* 197 */         boolean isWindows = File.separator.equals("\\");
/* 198 */         boolean isServletResource = (isWindows) && (this.configurationPath.startsWith("/"));
/* 199 */         if ((isServletResource) || (!isWindows))
/*     */         {
/* 201 */           ServletResourceResolver resolver = new ServletResourceResolver(servletConfig.getServletContext());
/* 202 */           boolean available = resolver.isAvailable(this.configurationPath, isServletResource);
/* 203 */           if (available)
/*     */           {
/* 206 */             this.configurationResolver = resolver;
/*     */           }
/*     */           else
/*     */           {
/* 211 */             this.configurationResolver = new LocalFileResolver(LocalFileResolver.SERVER);
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 217 */           this.configurationResolver = new LocalFileResolver(LocalFileResolver.SERVER);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 225 */       this.configurationPath = "/WEB-INF/flex/services-config.xml";
/* 226 */       this.configurationResolver = new ServletResourceResolver(servletConfig.getServletContext());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void verifyMinimumJavaVersion()
/*     */     throws ConfigurationException
/*     */   {
/*     */     try
/*     */     {
/* 236 */       boolean minimum = false;
/* 237 */       String version = System.getProperty("java.version");
/* 238 */       String vendor = System.getProperty("java.vendor");
/*     */ 
/* 240 */       version = version.replace('.', ':');
/* 241 */       version = version.replace('_', ':');
/* 242 */       String[] split = version.split(":");
/*     */ 
/* 244 */       int first = Integer.parseInt(split[0]);
/* 245 */       if (first > 1)
/*     */       {
/* 247 */         minimum = true;
/*     */       }
/* 249 */       else if (first == 1)
/*     */       {
/* 251 */         int second = Integer.parseInt(split[1]);
/* 252 */         if (second > 4)
/*     */         {
/* 254 */           minimum = true;
/*     */         }
/* 256 */         else if (second == 4)
/*     */         {
/* 258 */           int third = Integer.parseInt(split[2]);
/* 259 */           if (third > 2)
/*     */           {
/* 261 */             minimum = true;
/*     */           }
/* 263 */           else if (third == 2)
/*     */           {
/* 265 */             if ((vendor != null) && (vendor.indexOf("Sun") != -1))
/*     */             {
/* 268 */               int fourth = Integer.parseInt(split[3]);
/* 269 */               if (fourth >= 6)
/*     */               {
/* 271 */                 minimum = true;
/*     */               }
/*     */ 
/*     */             }
/*     */             else
/*     */             {
/* 277 */               minimum = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 283 */       if (!minimum)
/*     */       {
/* 285 */         ConfigurationException cx = new ConfigurationException();
/*     */ 
/* 287 */         if ((vendor != null) && (vendor.indexOf("Sun") != -1))
/*     */         {
/* 290 */           cx.setMessage(10139, new Object[] { System.getProperty("java.version") });
/*     */         }
/*     */         else
/*     */         {
/* 295 */           cx.setMessage(10140, new Object[] { System.getProperty("java.version") });
/*     */         }
/*     */ 
/* 298 */         throw cx;
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 303 */       if ((t instanceof ConfigurationException))
/*     */       {
/* 305 */         throw ((ConfigurationException)t);
/*     */       }
/*     */ 
/* 309 */       if (Trace.config)
/*     */       {
/* 311 */         Trace.trace("Could not verified required java version. version=" + System.getProperty("java.version"));
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.FlexConfigurationManager
 * JD-Core Version:    0.6.0
 */
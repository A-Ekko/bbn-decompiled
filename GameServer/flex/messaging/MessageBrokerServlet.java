/*     */ package flex.messaging;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*     */ import flex.management.MBeanLifecycleManager;
/*     */ import flex.messaging.config.ConfigurationManager;
/*     */ import flex.messaging.config.FlexConfigurationManager;
/*     */ import flex.messaging.config.MessagingConfiguration;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.log.ServletLogTarget;
/*     */ import flex.messaging.services.AuthenticationService;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.Trace;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.security.Principal;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.UnavailableException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class MessageBrokerServlet extends HttpServlet
/*     */ {
/*     */   static final long serialVersionUID = -5293855229461612246L;
/*     */   public static final String LOG_CATEGORY_STARTUP_BROKER = "Startup.MessageBroker";
/*     */   private MessageBroker broker;
/*  67 */   private static String FLEXDIR = "/WEB-INF/flex/";
/*     */ 
/*     */   public void init(ServletConfig servletConfig)
/*     */     throws ServletException, UnavailableException
/*     */   {
/*  78 */     super.init(servletConfig);
/*     */ 
/*  81 */     FlexContext.setThreadLocalObjects(null, null, null, null, null, servletConfig);
/*     */ 
/*  83 */     ServletLogTarget.setServletContext(servletConfig.getServletContext());
/*     */ 
/*  85 */     ClassLoader loader = getClassLoader();
/*     */     String useCCLoader;
/*  89 */     if (((useCCLoader = servletConfig.getInitParameter("useContextClassLoader")) != null) && (useCCLoader.equalsIgnoreCase("true")))
/*     */     {
/*  91 */       loader = Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/*  97 */       ConfigurationManager configManager = loadMessagingConfiguration(servletConfig);
/*     */ 
/* 100 */       MessagingConfiguration config = configManager.getMessagingConfiguration(servletConfig);
/*     */ 
/* 103 */       config.createLogAndTargets();
/*     */ 
/* 106 */       this.broker = config.createBroker(servletConfig.getInitParameter("messageBrokerId"), loader);
/*     */ 
/* 109 */       FlexContext.setThreadLocalObjects(null, null, this.broker, null, null, servletConfig);
/*     */ 
/* 111 */       setupInternalPathResolver();
/* 112 */       this.broker.setFlexWritePath(getFlexWritePath(servletConfig));
/*     */ 
/* 115 */       this.broker.setInitServletContext(servletConfig.getServletContext());
/*     */ 
/* 117 */       Logger logger = Log.getLogger("Configuration");
/* 118 */       if (Log.isInfo())
/*     */       {
/* 120 */         logger.info(VersionInfo.buildMessage());
/*     */       }
/*     */ 
/* 125 */       config.configureBroker(this.broker);
/*     */ 
/* 127 */       long timeBeforeStartup = 0L;
/* 128 */       if (Log.isDebug())
/*     */       {
/* 130 */         timeBeforeStartup = System.currentTimeMillis();
/* 131 */         Log.getLogger("Startup.MessageBroker").debug("MessageBroker with id '{0}' is starting.", new Object[] { this.broker.getId() });
/*     */       }
/*     */ 
/* 136 */       synchronized (HttpFlexSession.mapLock)
/*     */       {
/* 138 */         if (servletConfig.getServletContext().getAttribute("LCDS_HTTP_TO_FLEX_SESSION_MAP") == null) {
/* 139 */           servletConfig.getServletContext().setAttribute("LCDS_HTTP_TO_FLEX_SESSION_MAP", new ConcurrentHashMap());
/*     */         }
/*     */       }
/* 142 */       this.broker.start();
/*     */ 
/* 144 */       if (Log.isDebug())
/*     */       {
/* 146 */         long timeAfterStartup = System.currentTimeMillis();
/* 147 */         Long diffMillis = new Long(timeAfterStartup - timeBeforeStartup);
/* 148 */         Log.getLogger("Startup.MessageBroker").debug("MessageBroker with id '{0}' is ready (startup time: '{1}' ms)", new Object[] { this.broker.getId(), diffMillis });
/*     */       }
/*     */ 
/* 153 */       configManager.reportTokens();
/*     */ 
/* 156 */       config.reportUnusedProperties();
/*     */     }
/*     */     catch (Exception re)
/*     */     {
/* 161 */       destroy();
/* 162 */       System.err.println("**** MessageBrokerServlet failed to initialize due to runtime exception: " + ExceptionUtil.exceptionToString(re));
/* 163 */       throw new UnavailableException(re.getMessage());
/*     */     }
/*     */     finally
/*     */     {
/* 167 */       FlexContext.clearThreadLocalObjects();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setupInternalPathResolver()
/*     */   {
/* 173 */     this.broker.setInternalPathResolver(new MessageBroker.InternalPathResolver()
/*     */     {
/*     */       public InputStream resolve(String filename)
/*     */       {
/* 178 */         InputStream is = MessageBrokerServlet.this.getServletContext().getResourceAsStream(MessageBrokerServlet.FLEXDIR + filename);
/* 179 */         return is;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private ConfigurationManager loadMessagingConfiguration(ServletConfig servletConfig)
/*     */   {
/* 187 */     ConfigurationManager manager = null;
/* 188 */     Class managerClass = null;
/* 189 */     String className = null;
/*     */ 
/* 192 */     if (servletConfig != null)
/*     */     {
/* 194 */       String p = servletConfig.getInitParameter("services.configuration.manager");
/* 195 */       if (p != null)
/*     */       {
/* 197 */         className = p.trim();
/*     */         try
/*     */         {
/* 200 */           managerClass = ClassUtil.createClass(className);
/* 201 */           manager = (ConfigurationManager)managerClass.newInstance();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/* 205 */           if (Trace.config)
/*     */           {
/* 207 */             Trace.trace("Could not load configuration manager as: " + className);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 213 */     if (manager == null)
/*     */     {
/* 215 */       manager = new FlexConfigurationManager();
/*     */     }
/*     */ 
/* 218 */     return manager;
/*     */   }
/*     */ 
/*     */   protected String getFlexWritePath(ServletConfig servletConfig)
/*     */   {
/* 232 */     String resolved = null;
/* 233 */     String path = FLEXDIR;
/*     */ 
/* 235 */     if (servletConfig != null)
/*     */     {
/* 237 */       String p = servletConfig.getInitParameter("flex.write.path");
/* 238 */       if (p != null)
/*     */       {
/* 240 */         path = p.trim();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 245 */     if (path.startsWith("/"))
/*     */     {
/* 247 */       String realPath = servletConfig.getServletContext().getRealPath(path);
/*     */       try
/*     */       {
/* 251 */         File f = new File(realPath);
/* 252 */         if ((f != null) && (f.exists()) && (f.isAbsolute()))
/*     */         {
/* 254 */           resolved = realPath;
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 264 */     boolean isWindows = File.separator.equals("\\");
/*     */ 
/* 267 */     if ((resolved == null) && ((!isWindows) || (!path.startsWith("/"))))
/*     */     {
/*     */       try
/*     */       {
/* 271 */         File f = new File(path);
/* 272 */         if ((f != null) && (f.exists()) && (f.isAbsolute()))
/*     */         {
/* 274 */           resolved = path;
/*     */         }
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 283 */     return resolved;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 292 */     if (this.broker != null)
/*     */     {
/* 294 */       this.broker.stop();
/* 295 */       if (this.broker.isManaged())
/*     */       {
/* 297 */         MBeanLifecycleManager.unregisterRuntimeMBeans(this.broker);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void service(HttpServletRequest req, HttpServletResponse res)
/*     */   {
/*     */     try
/*     */     {
/* 313 */       this.broker.initThreadLocals();
/*     */ 
/* 318 */       FlexContext.setThreadLocalObjects(null, null, this.broker, req, res, getServletConfig());
/* 319 */       HttpFlexSession fs = HttpFlexSession.getFlexSession(req);
/* 320 */       Principal principal = null;
/* 321 */       if (FlexContext.isPerClientAuthentication())
/*     */       {
/* 323 */         principal = FlexContext.getUserPrincipal();
/*     */       }
/*     */       else
/*     */       {
/* 327 */         principal = fs.getUserPrincipal();
/*     */       }
/*     */ 
/* 330 */       if ((principal == null) && (req.getHeader("Authorization") != null))
/*     */       {
/* 332 */         String encoded = req.getHeader("Authorization");
/* 333 */         if (encoded.indexOf("Basic") > -1)
/*     */         {
/* 335 */           encoded = encoded.substring(6);
/*     */           try
/*     */           {
/* 338 */             AuthenticationService.decodeAndLogin(encoded, this.broker.getLoginManager());
/*     */           }
/*     */           catch (Exception e)
/*     */           {
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 347 */       String contextPath = req.getContextPath();
/* 348 */       String pathInfo = req.getPathInfo();
/* 349 */       String endpointPath = req.getServletPath();
/* 350 */       if (pathInfo != null) {
/* 351 */         endpointPath = endpointPath + pathInfo;
/*     */       }
/* 353 */       Endpoint endpoint = null;
/*     */       try
/*     */       {
/* 356 */         endpoint = this.broker.getEndpoint(endpointPath, contextPath);
/*     */       }
/*     */       catch (MessageException me)
/*     */       {
/*     */         try
/*     */         {
/* 362 */           res.sendError(404);
/*     */         }
/*     */         catch (IOException ignore) {
/*     */         }
/*     */       }
/* 367 */       if (endpoint != null)
/*     */       {
/*     */         try
/*     */         {
/* 371 */           if (Log.isInfo())
/*     */           {
/* 373 */             Log.getLogger("Endpoint.General").info("Channel endpoint {0} received request.", new Object[] { endpoint.getId() });
/*     */           }
/*     */ 
/* 377 */           endpoint.service(req, res);
/*     */         }
/*     */         catch (UnsupportedOperationException ue)
/*     */         {
/*     */           try
/*     */           {
/* 383 */             res.sendError(405);
/*     */           }
/*     */           catch (IOException ignore)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */         try
/*     */         {
/* 393 */           res.sendError(403);
/*     */         }
/*     */         catch (IOException ignore)
/*     */         {
/*     */         }
/*     */     }
/*     */     finally
/*     */     {
/* 401 */       FlexContext.clearThreadLocalObjects();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ClassLoader getClassLoader()
/*     */   {
/* 412 */     return getClass().getClassLoader();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageBrokerServlet
 * JD-Core Version:    0.6.0
 */
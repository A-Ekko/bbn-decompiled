/*     */ package org.logicalcobwebs.proxool.configuration;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Properties;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ 
/*     */ public class ServletConfigurator extends HttpServlet
/*     */ {
/*  92 */   private static final Log LOG = LogFactory.getLog(ServletConfigurator.class);
/*     */   private static final String XML_FILE_PROPERTY = "xmlFile";
/*     */   private static final String PROPERTY_FILE_PROPERTY = "propertyFile";
/*     */   private static final String AUTO_SHUTDOWN_PROPERTY = "autoShutdown";
/* 100 */   private boolean autoShutdown = true;
/*     */ 
/*     */   public void init(ServletConfig servletConfig) throws ServletException {
/* 103 */     super.init(servletConfig);
/*     */ 
/* 105 */     String appDir = servletConfig.getServletContext().getRealPath("/");
/*     */ 
/* 107 */     Properties properties = new Properties();
/*     */ 
/* 109 */     Enumeration names = servletConfig.getInitParameterNames();
/* 110 */     while (names.hasMoreElements()) {
/* 111 */       String name = (String)names.nextElement();
/* 112 */       String value = servletConfig.getInitParameter(name);
/*     */ 
/* 114 */       if (name.equals("xmlFile"))
/*     */         try {
/* 116 */           File file = new File(value);
/* 117 */           if (file.isAbsolute())
/* 118 */             JAXPConfigurator.configure(value, false);
/*     */           else
/* 120 */             JAXPConfigurator.configure(appDir + File.separator + value, false);
/*     */         }
/*     */         catch (ProxoolException e) {
/* 123 */           LOG.error("Problem configuring " + value, e);
/*     */         }
/* 125 */       else if (name.equals("propertyFile"))
/*     */         try {
/* 127 */           File file = new File(value);
/* 128 */           if (file.isAbsolute())
/* 129 */             PropertyConfigurator.configure(value);
/*     */           else
/* 131 */             PropertyConfigurator.configure(appDir + File.separator + value);
/*     */         }
/*     */         catch (ProxoolException e) {
/* 134 */           LOG.error("Problem configuring " + value, e);
/*     */         }
/* 136 */       else if (name.equals("autoShutdown"))
/* 137 */         this.autoShutdown = Boolean.valueOf(value).booleanValue();
/* 138 */       else if (name.startsWith("jdbc")) {
/* 139 */         properties.setProperty(name, value);
/*     */       }
/*     */     }
/*     */ 
/* 143 */     if (properties.size() > 0)
/*     */       try {
/* 145 */         PropertyConfigurator.configure(properties);
/*     */       } catch (ProxoolException e) {
/* 147 */         LOG.error("Problem configuring using init properties", e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 163 */     if (this.autoShutdown)
/* 164 */       ProxoolFacade.shutdown(0);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.logicalcobwebs.proxool.configuration.ServletConfigurator
 * JD-Core Version:    0.6.0
 */
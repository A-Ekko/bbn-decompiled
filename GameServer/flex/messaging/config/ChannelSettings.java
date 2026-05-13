/*     */ package flex.messaging.config;
/*     */ 
/*     */ import flex.messaging.util.StringUtils;
/*     */ 
/*     */ public class ChannelSettings extends PropertiesSettings
/*     */ {
/*     */   protected String id;
/*     */   protected boolean remote;
/*     */   protected String serverId;
/*     */   private String sourceFile;
/*     */   protected SecurityConstraint constraint;
/*     */   protected String uri;
/*     */   protected int port;
/*     */   protected String endpointType;
/*     */   protected String clientType;
/*     */   protected String parsedUri;
/*     */   protected boolean contextParsed;
/*     */   protected String parsedClientUri;
/*     */   protected boolean clientContextParsed;
/*     */ 
/*     */   public ChannelSettings(String id)
/*     */   {
/*  53 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  58 */     return this.id;
/*     */   }
/*     */ 
/*     */   public boolean isRemote()
/*     */   {
/*  63 */     return this.remote;
/*     */   }
/*     */ 
/*     */   public void setRemote(boolean value)
/*     */   {
/*  68 */     this.remote = value;
/*     */   }
/*     */ 
/*     */   public String getServerId()
/*     */   {
/*  73 */     return this.serverId;
/*     */   }
/*     */ 
/*     */   public void setServerId(String value)
/*     */   {
/*  78 */     this.serverId = value;
/*     */   }
/*     */ 
/*     */   public String getClientType()
/*     */   {
/*  83 */     return this.clientType;
/*     */   }
/*     */ 
/*     */   public void setClientType(String type)
/*     */   {
/*  88 */     this.clientType = type;
/*     */   }
/*     */ 
/*     */   String getSourceFile()
/*     */   {
/*  94 */     return this.sourceFile;
/*     */   }
/*     */ 
/*     */   void setSourceFile(String sourceFile)
/*     */   {
/*  99 */     this.sourceFile = sourceFile;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 110 */     return this.port;
/*     */   }
/*     */ 
/*     */   public String getUri()
/*     */   {
/* 115 */     return this.uri;
/*     */   }
/*     */ 
/*     */   public void setUri(String uri)
/*     */   {
/* 120 */     this.uri = uri;
/* 121 */     this.port = parsePort(this, uri);
/* 122 */     this.contextParsed = false;
/* 123 */     this.clientContextParsed = false;
/*     */   }
/*     */ 
/*     */   public String getClientParsedUri(String contextPath)
/*     */   {
/* 128 */     if (!this.clientContextParsed) {
/* 129 */       parseClientUri(this, contextPath);
/*     */     }
/* 131 */     return this.parsedClientUri;
/*     */   }
/*     */ 
/*     */   public String getEndpointType()
/*     */   {
/* 136 */     return this.endpointType;
/*     */   }
/*     */ 
/*     */   public void setEndpointType(String type)
/*     */   {
/* 141 */     this.endpointType = type;
/*     */   }
/*     */ 
/*     */   public SecurityConstraint getConstraint()
/*     */   {
/* 146 */     return this.constraint;
/*     */   }
/*     */ 
/*     */   public void setConstraint(SecurityConstraint constraint)
/*     */   {
/* 151 */     this.constraint = constraint;
/*     */   }
/*     */ 
/*     */   private static void parseClientUri(ChannelSettings cs, String contextPath)
/*     */   {
/* 160 */     if (!cs.clientContextParsed)
/*     */     {
/* 162 */       String channelEndpoint = cs.getUri().trim();
/*     */ 
/* 165 */       channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", "{context.root}");
/*     */ 
/* 167 */       if ((contextPath == null) && (channelEndpoint.indexOf("{context.root}") != -1))
/*     */       {
/* 170 */         ConfigurationException e = new ConfigurationException();
/* 171 */         e.setMessage(11120, new Object[] { cs.getId() });
/* 172 */         throw e;
/*     */       }
/*     */ 
/* 177 */       if ((contextPath != null) && (!contextPath.startsWith("/")))
/*     */       {
/* 179 */         contextPath = "/" + contextPath;
/*     */       }
/*     */ 
/* 184 */       if (channelEndpoint.indexOf("/{context.root}") != -1)
/*     */       {
/* 188 */         if (("/".equals(contextPath)) && (!"/{context.root}".equals(channelEndpoint))) {
/* 189 */           contextPath = "";
/*     */         }
/* 191 */         channelEndpoint = StringUtils.substitute(channelEndpoint, "/{context.root}", contextPath);
/*     */       }
/*     */       else
/*     */       {
/* 198 */         if (("/".equals(contextPath)) && (!"{context.root}".equals(channelEndpoint))) {
/* 199 */           contextPath = "";
/*     */         }
/* 201 */         channelEndpoint = StringUtils.substitute(channelEndpoint, "{context.root}", contextPath);
/*     */       }
/*     */ 
/* 204 */       cs.parsedClientUri = channelEndpoint;
/* 205 */       cs.clientContextParsed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static int parsePort(ChannelSettings cs, String url)
/*     */   {
/* 220 */     int port = 0;
/*     */ 
/* 224 */     int start = url.indexOf(":/");
/* 225 */     if (start > 0)
/*     */     {
/* 228 */       start += 3;
/* 229 */       int end = url.indexOf('/', start);
/*     */ 
/* 232 */       String snp = end == -1 ? url.substring(start) : url.substring(start, end);
/*     */ 
/* 235 */       int delim = snp.indexOf("]");
/* 236 */       delim = delim > -1 ? snp.indexOf(":", delim) : snp.indexOf(":");
/*     */ 
/* 238 */       if (delim > 0)
/*     */       {
/*     */         try
/*     */         {
/* 242 */           int p = Integer.parseInt(snp.substring(delim + 1));
/* 243 */           if (p > 0) {
/* 244 */             port = p;
/*     */           }
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 253 */     return port;
/*     */   }
/*     */ 
/*     */   public static String removeTokens(String url)
/*     */   {
/* 266 */     String channelEndpoint = url.toLowerCase().trim();
/*     */ 
/* 269 */     if ((channelEndpoint.startsWith("http://")) || (channelEndpoint.startsWith("https://")) || (channelEndpoint.startsWith("rtmp://")) || (channelEndpoint.startsWith("rtmps://")))
/*     */     {
/* 273 */       int nextSlash = channelEndpoint.indexOf('/', 8);
/*     */ 
/* 276 */       if ((nextSlash > 0) && (nextSlash != channelEndpoint.length() - 1)) {
/* 277 */         channelEndpoint = channelEndpoint.substring(nextSlash);
/*     */       }
/*     */     }
/*     */ 
/* 281 */     channelEndpoint = StringUtils.substitute(channelEndpoint, "{context-root}", "{context.root}");
/*     */ 
/* 284 */     if (channelEndpoint.startsWith("{context.root}"))
/*     */     {
/* 286 */       channelEndpoint = channelEndpoint.substring("{context.root}".length());
/*     */     }
/* 288 */     else if (channelEndpoint.startsWith("/{context.root}"))
/*     */     {
/* 290 */       channelEndpoint = channelEndpoint.substring("/{context.root}".length());
/*     */     }
/*     */ 
/* 294 */     if (channelEndpoint.endsWith("/"))
/*     */     {
/* 296 */       channelEndpoint = channelEndpoint.substring(0, channelEndpoint.length() - 1);
/*     */     }
/* 298 */     return channelEndpoint;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ChannelSettings
 * JD-Core Version:    0.6.0
 */
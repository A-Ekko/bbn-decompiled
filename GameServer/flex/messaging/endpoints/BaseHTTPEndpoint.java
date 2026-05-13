/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.HttpFlexSession;
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.client.FlexClient;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.endpoints.amf.AMFFilter;
/*     */ import flex.messaging.io.amf.ActionContext;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.util.SettingsReplaceUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.net.SocketException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public abstract class BaseHTTPEndpoint extends AbstractEndpoint
/*     */ {
/*     */   private static final String ADD_NO_CACHE_HEADERS = "add-no-cache-headers";
/*     */   private static final String REDIRECT_URL = "redirect-url";
/*     */   private static final String INVALIDATE_SESSION_ON_DISCONNECT = "invalidate-session-on-disconnect";
/*     */   protected EndpointControl controller;
/*     */   protected AMFFilter filterChain;
/* 153 */   protected boolean addNoCacheHeaders = true;
/*     */   protected boolean loginAfterDisconnect;
/*     */   protected boolean invalidateSessionOnDisconnect;
/*     */   protected String redirectURL;
/*     */ 
/*     */   public BaseHTTPEndpoint()
/*     */   {
/*  69 */     this(false);
/*     */   }
/*     */ 
/*     */   public BaseHTTPEndpoint(boolean enableManagement)
/*     */   {
/*  80 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*  98 */     super.initialize(id, properties);
/*     */ 
/* 100 */     if ((properties == null) || (properties.size() == 0)) {
/* 101 */       return;
/*     */     }
/*     */ 
/* 104 */     this.addNoCacheHeaders = properties.getPropertyAsBoolean("add-no-cache-headers", true);
/* 105 */     this.redirectURL = properties.getPropertyAsString("redirect-url", null);
/* 106 */     this.invalidateSessionOnDisconnect = properties.getPropertyAsBoolean("invalidate-session-on-disconnect", false);
/*     */ 
/* 108 */     this.loginAfterDisconnect = properties.getPropertyAsBoolean("login-after-disconnect", false);
/*     */ 
/* 110 */     validateEndpointProtocol();
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 119 */     if (isStarted()) {
/* 120 */       return;
/*     */     }
/* 122 */     super.start();
/*     */ 
/* 124 */     this.filterChain = createFilterChain();
/*     */   }
/*     */ 
/*     */   public boolean isAddNoCacheHeaders()
/*     */   {
/* 163 */     return this.addNoCacheHeaders;
/*     */   }
/*     */ 
/*     */   public void setAddNoCacheHeaders(boolean addNoCacheHeaders)
/*     */   {
/* 173 */     this.addNoCacheHeaders = addNoCacheHeaders;
/*     */   }
/*     */ 
/*     */   public boolean isInvalidateSessionOnDisconnect()
/*     */   {
/* 202 */     return this.invalidateSessionOnDisconnect;
/*     */   }
/*     */ 
/*     */   public void setInvalidateSessionOnDisconnect(boolean value)
/*     */   {
/* 215 */     this.invalidateSessionOnDisconnect = value;
/*     */   }
/*     */ 
/*     */   public String getRedirectURL()
/*     */   {
/* 231 */     return this.redirectURL;
/*     */   }
/*     */ 
/*     */   public void setRedirectURL(String redirectURL)
/*     */   {
/* 241 */     this.redirectURL = redirectURL;
/*     */   }
/*     */ 
/*     */   public void service(HttpServletRequest req, HttpServletResponse res)
/*     */   {
/* 258 */     super.service(req, res);
/*     */     try
/*     */     {
/* 263 */       setThreadLocals();
/*     */ 
/* 266 */       ActionContext context = new ActionContext();
/*     */ 
/* 270 */       context.setRecordMessageSizes(isRecordMessageSizes());
/* 271 */       context.setRecordMessageTimes(isRecordMessageTimes());
/*     */ 
/* 274 */       this.filterChain.invoke(context);
/*     */ 
/* 278 */       if (isManaged())
/*     */       {
/* 280 */         this.controller.addToBytesDeserialized(context.getDeserializedBytes());
/* 281 */         this.controller.addToBytesSerialized(context.getSerializedBytes());
/*     */       }
/*     */ 
/* 284 */       if (context.getStatus() != 2)
/*     */       {
/* 286 */         if (this.addNoCacheHeaders) {
/* 287 */           addNoCacheHeaders(req, res);
/*     */         }
/* 289 */         ByteArrayOutputStream outBuffer = context.getResponseOutput();
/* 290 */         res.setContentType(getResponseContentType());
/* 291 */         res.setContentLength(outBuffer.size());
/* 292 */         outBuffer.writeTo(res.getOutputStream());
/* 293 */         res.flushBuffer();
/*     */       }
/* 298 */       else if (this.redirectURL != null)
/*     */       {
/*     */         try
/*     */         {
/* 303 */           this.redirectURL = SettingsReplaceUtil.replaceContextPath(this.redirectURL, req.getContextPath());
/* 304 */           res.sendRedirect(this.redirectURL);
/*     */         }
/*     */         catch (IllegalStateException alreadyFlushed)
/*     */         {
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (SocketException se)
/*     */     {
/* 315 */       this.log.info(se.getMessage());
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 319 */       this.log.error(t.getMessage(), t);
/*     */     }
/*     */     finally
/*     */     {
/* 323 */       clearThreadLocals();
/*     */     }
/*     */   }
/*     */ 
/*     */   public ConfigMap describeEndpoint()
/*     */   {
/* 337 */     ConfigMap endpointConfig = super.describeEndpoint();
/*     */ 
/* 339 */     boolean createdProperties = false;
/* 340 */     ConfigMap properties = endpointConfig.getPropertyAsMap("properties", null);
/*     */ 
/* 342 */     if (properties == null)
/*     */     {
/* 344 */       properties = new ConfigMap();
/* 345 */       createdProperties = true;
/*     */     }
/*     */ 
/* 348 */     if (this.loginAfterDisconnect)
/*     */     {
/* 350 */       ConfigMap loginAfterDisconnect = new ConfigMap();
/*     */ 
/* 352 */       loginAfterDisconnect.addProperty("", "true");
/* 353 */       properties.addProperty("login-after-disconnect", loginAfterDisconnect);
/*     */     }
/*     */ 
/* 356 */     if ((createdProperties) && (properties.size() > 0)) {
/* 357 */       endpointConfig.addProperty("properties", properties);
/*     */     }
/* 359 */     return endpointConfig;
/*     */   }
/*     */ 
/*     */   protected abstract AMFFilter createFilterChain();
/*     */ 
/*     */   protected abstract String getResponseContentType();
/*     */ 
/*     */   protected Message handleChannelDisconnect(CommandMessage disconnectCommand)
/*     */   {
/* 386 */     HttpFlexSession session = (HttpFlexSession)FlexContext.getFlexSession();
/* 387 */     FlexClient flexClient = FlexContext.getFlexClient();
/*     */     String endpointId;
/*     */     Iterator iter;
/* 391 */     if (flexClient.isValid())
/*     */     {
/* 393 */       endpointId = getId();
/* 394 */       List messageClients = flexClient.getMessageClients();
/* 395 */       for (iter = messageClients.iterator(); iter.hasNext(); )
/*     */       {
/* 397 */         MessageClient messageClient = (MessageClient)iter.next();
/* 398 */         if (messageClient.getEndpointId().equals(endpointId))
/*     */         {
/* 400 */           messageClient.setClientChannelDisconnected(true);
/* 401 */           messageClient.invalidate();
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 407 */     if ((session.isValid()) && (isInvalidateSessionOnDisconnect())) {
/* 408 */       session.invalidate(false);
/*     */     }
/* 410 */     return super.handleChannelDisconnect(disconnectCommand);
/*     */   }
/*     */ 
/*     */   protected void validateEndpointProtocol()
/*     */   {
/* 415 */     if ((isSecure()) && (!this.url.startsWith("https:")))
/*     */     {
/* 417 */       ConfigurationException ce = new ConfigurationException();
/* 418 */       ce.setMessage(11100, new Object[] { this.url, "https" });
/* 419 */       throw ce;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.BaseHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
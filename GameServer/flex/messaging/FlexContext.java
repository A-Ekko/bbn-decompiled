/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.messaging.client.FlexClient;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.security.LoginManager;
/*     */ import java.security.Principal;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class FlexContext
/*     */ {
/*  41 */   private static ThreadLocal flexClients = new ThreadLocal();
/*  42 */   private static ThreadLocal sessions = new ThreadLocal();
/*  43 */   private static ThreadLocal messageBrokers = new ThreadLocal();
/*  44 */   private static ThreadLocal responses = new ThreadLocal();
/*  45 */   private static ThreadLocal requests = new ThreadLocal();
/*  46 */   private static ThreadLocal tunnelRequests = new ThreadLocal();
/*  47 */   private static ThreadLocal servletConfigs = new ThreadLocal();
/*  48 */   private static ThreadLocal messageFromPeer = new ThreadLocal();
/*     */   private static ServletConfig lastGoodServletConfig;
/*     */ 
/*     */   public static void setThreadLocalObjects(FlexClient flexClient, FlexSession session, MessageBroker broker, HttpServletRequest request, HttpServletResponse response, ServletConfig servletConfig)
/*     */   {
/*  66 */     flexClients.set(flexClient);
/*  67 */     sessions.set(session);
/*  68 */     messageBrokers.set(broker);
/*  69 */     requests.set(request);
/*  70 */     responses.set(response);
/*  71 */     servletConfigs.set(servletConfig);
/*  72 */     messageFromPeer.set(Boolean.FALSE);
/*  73 */     if (servletConfig != null)
/*  74 */       lastGoodServletConfig = servletConfig;
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalObjects(FlexClient flexClient, FlexSession session, MessageBroker broker)
/*     */   {
/*  83 */     setThreadLocalObjects(flexClient, session, broker, null, null, null);
/*     */   }
/*     */ 
/*     */   public static void clearThreadLocalObjects()
/*     */   {
/*  92 */     setThreadLocalObjects(null, null, null);
/*  93 */     TypeMarshallingContext.clearThreadLocalObjects();
/*     */   }
/*     */ 
/*     */   public static HttpServletRequest getHttpRequest()
/*     */   {
/* 103 */     return (HttpServletRequest)requests.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalHttpRequest(HttpServletRequest value)
/*     */   {
/* 112 */     requests.set(value);
/*     */   }
/*     */ 
/*     */   public static HttpServletResponse getHttpResponse()
/*     */   {
/* 122 */     return (HttpServletResponse)responses.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalHttpResponse(HttpServletResponse value)
/*     */   {
/* 131 */     responses.set(value);
/*     */   }
/*     */ 
/*     */   public static HttpServletRequest getTunnelHttpRequest()
/*     */   {
/* 141 */     return (HttpServletRequest)tunnelRequests.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalTunnelHttpRequest(HttpServletRequest value)
/*     */   {
/* 150 */     tunnelRequests.set(value);
/*     */   }
/*     */ 
/*     */   public static ServletConfig getServletConfig()
/*     */   {
/* 159 */     if (servletConfigs.get() != null)
/*     */     {
/* 161 */       return (ServletConfig)servletConfigs.get();
/*     */     }
/* 163 */     return lastGoodServletConfig;
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalServletConfig(ServletConfig value)
/*     */   {
/* 172 */     servletConfigs.set(value);
/*     */   }
/*     */ 
/*     */   public static ServletContext getServletContext()
/*     */   {
/* 180 */     return getServletConfig().getServletContext();
/*     */   }
/*     */ 
/*     */   public static FlexClient getFlexClient()
/*     */   {
/* 188 */     return (FlexClient)flexClients.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalFlexClient(FlexClient flexClient)
/*     */   {
/* 197 */     flexClients.set(flexClient);
/*     */   }
/*     */ 
/*     */   public static FlexSession getFlexSession()
/*     */   {
/* 205 */     return (FlexSession)sessions.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalSession(FlexSession session)
/*     */   {
/* 214 */     sessions.set(session);
/*     */   }
/*     */ 
/*     */   public static MessageBroker getMessageBroker()
/*     */   {
/* 224 */     return (MessageBroker)messageBrokers.get();
/*     */   }
/*     */ 
/*     */   public static void setThreadLocalMessageBroker(MessageBroker value)
/*     */   {
/* 233 */     messageBrokers.set(value);
/*     */   }
/*     */ 
/*     */   public static boolean isMessageFromPeer()
/*     */   {
/* 242 */     return ((Boolean)messageFromPeer.get()).booleanValue();
/*     */   }
/*     */ 
/*     */   public static void setMessageFromPeer(boolean value)
/*     */   {
/* 255 */     messageFromPeer.set(Boolean.valueOf(value));
/*     */   }
/*     */ 
/*     */   public static boolean isPerClientAuthentication()
/*     */   {
/* 264 */     if (getMessageBroker().getLoginManager() != null) {
/* 265 */       return getMessageBroker().getLoginManager().isPerClientAuthentication();
/*     */     }
/* 267 */     return false;
/*     */   }
/*     */ 
/*     */   public static Principal getUserPrincipal()
/*     */   {
/* 279 */     if (isPerClientAuthentication())
/*     */     {
/* 281 */       FlexClient client = getFlexClient();
/* 282 */       if (client != null)
/* 283 */         return client.getUserPrincipal();
/*     */     }
/*     */     else
/*     */     {
/* 287 */       FlexSession session = getFlexSession();
/* 288 */       if (session != null)
/* 289 */         return session.getUserPrincipal();
/*     */     }
/* 291 */     return null;
/*     */   }
/*     */ 
/*     */   public static void setUserPrincipal(Principal userPrincipal)
/*     */   {
/* 303 */     if (isPerClientAuthentication())
/* 304 */       getFlexClient().setUserPrincipal(userPrincipal);
/*     */     else
/* 306 */       getFlexSession().setUserPrincipal(userPrincipal);
/*     */   }
/*     */ 
/*     */   public static void clear()
/*     */   {
/* 314 */     clearThreadLocalObjects();
/* 315 */     lastGoodServletConfig = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexContext
 * JD-Core Version:    0.6.0
 */
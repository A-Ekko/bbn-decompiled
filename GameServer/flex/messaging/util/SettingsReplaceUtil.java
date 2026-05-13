/*     */ package flex.messaging.util;
/*     */ 
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SettingsReplaceUtil
/*     */ {
/*     */   private static final int TOKEN_NOT_SUPPORTED = 10129;
/*     */   private static final int TOKEN_NOT_SUPPORTED_ANY = 10130;
/*     */   private static final int PARSE_ERROR_DYNAMIC_URL = 10131;
/*     */   public static final String SLASH_CONTEXT_PATH_TOKEN = "/{context.root}";
/*     */   public static final String CONTEXT_PATH_TOKEN = "{context.root}";
/*     */   public static final String CONTEXT_PATH_ALT_TOKEN = "{context-root}";
/*     */   public static final String SERVER_NAME_TOKEN = "{server.name}";
/*     */   public static final String SERVER_NAME_ALT_TOKEN = "{server-name}";
/*     */   public static final String SERVER_PORT_TOKEN = "{server.port}";
/*     */   public static final String SERVER_PORT_ALT_TOKEN = "{server-port}";
/*     */ 
/*     */   public static String replaceContextPath(String url, String contextPath)
/*     */   {
/*  56 */     String token = "{context.root}";
/*  57 */     int contextIndex = url.indexOf("{context-root}");
/*  58 */     if (contextIndex != -1)
/*     */     {
/*  60 */       token = "{context-root}";
/*  61 */       url = StringUtils.substitute(url, "{context-root}", "{context.root}");
/*     */     }
/*  63 */     contextIndex = url.indexOf("{context.root}");
/*     */ 
/*  65 */     if ((contextPath == null) && (contextIndex != -1))
/*     */     {
/*  67 */       MessageException me = new MessageException();
/*  68 */       if (FlexContext.getHttpRequest() == null)
/*  69 */         me.setMessage(10129, "0", new Object[] { token });
/*     */       else
/*  71 */         me.setMessage(10129, new Object[] { token });
/*  72 */       throw me;
/*     */     }
/*  74 */     if (contextPath != null)
/*     */     {
/*  76 */       if (contextIndex == 0)
/*     */       {
/*  78 */         url = contextPath + url.substring("{context.root}".length());
/*     */       }
/*  80 */       else if (contextIndex > 0)
/*     */       {
/*  83 */         if (url.indexOf("/{context.root}") != -1)
/*     */         {
/*  85 */           url = StringUtils.substitute(url, "/{context.root}", contextPath);
/*     */         }
/*     */         else
/*     */         {
/*  89 */           url = StringUtils.substitute(url, "{context.root}", contextPath);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  94 */     return url;
/*     */   }
/*     */ 
/*     */   public static String replaceAllTokensGivenServerName(String url, String contextPath, String serverName, String serverPort, String serverProtocol)
/*     */   {
/* 100 */     if (url.startsWith("/"))
/*     */     {
/* 102 */       url = serverProtocol + "://{server.name}:{server.port}" + url;
/*     */     }
/* 104 */     url = replaceContextPath(url, contextPath);
/*     */ 
/* 106 */     String token = "{server.name}";
/* 107 */     int serverNameIndex = url.indexOf("{server-name}");
/* 108 */     if (serverNameIndex != -1)
/*     */     {
/* 110 */       token = "{server-name}";
/* 111 */       url = StringUtils.substitute(url, "{server-name}", "{server.name}");
/*     */     }
/*     */ 
/* 114 */     serverNameIndex = url.indexOf("{server.name}");
/* 115 */     if ((serverName == null) && (serverNameIndex != -1))
/*     */     {
/* 117 */       MessageException me = new MessageException();
/* 118 */       me.setMessage(10129, new Object[] { token });
/* 119 */       throw me;
/*     */     }
/* 121 */     if ((serverName != null) && (serverNameIndex != -1))
/*     */     {
/* 123 */       url = StringUtils.substitute(url, "{server.name}", serverName);
/*     */     }
/*     */ 
/* 126 */     token = "{server.port}";
/* 127 */     int serverPortIndex = url.indexOf("{server-port}");
/* 128 */     if (serverPortIndex != -1)
/*     */     {
/* 130 */       token = "{server-port}";
/* 131 */       url = StringUtils.substitute(url, "{server-port}", "{server.port}");
/*     */     }
/*     */ 
/* 134 */     serverPortIndex = url.indexOf("{server.port}");
/* 135 */     if ((serverPort == null) && (serverPortIndex != -1))
/*     */     {
/* 137 */       MessageException me = new MessageException();
/* 138 */       me.setMessage(10129, new Object[] { token });
/* 139 */       throw me;
/*     */     }
/* 141 */     if ((serverPort != null) && (serverPortIndex != -1))
/*     */     {
/* 143 */       url = StringUtils.substitute(url, "{server.port}", serverPort);
/*     */     }
/*     */ 
/* 146 */     return updateIPv6(url);
/*     */   }
/*     */ 
/*     */   public static Set replaceAllTokensCalculateServerName(List urls, String contextPath)
/*     */   {
/* 151 */     List contextParsedUrls = new ArrayList(urls.size());
/* 152 */     Set newURLs = new HashSet(urls.size());
/*     */ 
/* 155 */     for (int i = 0; i < urls.size(); i++)
/*     */     {
/* 157 */       String url = (String)urls.get(i);
/* 158 */       url = url.toLowerCase().trim();
/* 159 */       url = replaceContextPath(url, contextPath);
/* 160 */       contextParsedUrls.add(url);
/*     */     }
/*     */ 
/* 164 */     replaceServerNameWithLocalHost(contextParsedUrls, newURLs);
/*     */ 
/* 166 */     return newURLs;
/*     */   }
/*     */ 
/*     */   public static void replaceServerNameWithLocalHost(List urls, Set newURLs)
/*     */   {
/* 176 */     for (Iterator iterator = urls.iterator(); iterator.hasNext(); )
/*     */     {
/* 178 */       String url = (String)iterator.next();
/* 179 */       url = url.toLowerCase().trim();
/*     */ 
/* 181 */       String token = "{server.port}";
/* 182 */       int serverPortIndex = url.indexOf("{server-port}");
/* 183 */       if (serverPortIndex != -1)
/*     */       {
/* 185 */         token = "{server-port}";
/* 186 */         url = StringUtils.substitute(url, "{server-port}", "{server.port}");
/*     */       }
/*     */ 
/* 189 */       serverPortIndex = url.indexOf("{server.port}");
/* 190 */       if (serverPortIndex != -1)
/*     */       {
/* 192 */         MessageException me = new MessageException();
/* 193 */         me.setMessage(10130, new Object[] { token });
/* 194 */         throw me;
/*     */       }
/*     */ 
/* 197 */       if (url.indexOf("{server-name}") != 0)
/*     */       {
/* 199 */         StringUtils.substitute(url, "{server-name}", "{server.name}");
/*     */       }
/*     */ 
/* 202 */       if (url.indexOf("{server.name}") != 0)
/*     */       {
/*     */         try
/*     */         {
/* 207 */           addLocalServerURL(url, "localhost", newURLs);
/* 208 */           addLocalServerURL(url, "127.0.0.1", newURLs);
/* 209 */           addLocalServerURL(url, "[::1]", newURLs);
/*     */ 
/* 211 */           InetAddress local = InetAddress.getLocalHost();
/* 212 */           addInetAddress(local, url, newURLs);
/*     */ 
/* 216 */           Enumeration e = NetworkInterface.getNetworkInterfaces();
/* 217 */           while (e.hasMoreElements())
/*     */           {
/* 219 */             NetworkInterface address = (NetworkInterface)e.nextElement();
/* 220 */             Enumeration e2 = address.getInetAddresses();
/* 221 */             while (e2.hasMoreElements())
/*     */             {
/* 223 */               local = (InetAddress)e2.nextElement();
/* 224 */               addInetAddress(local, url, newURLs);
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 230 */           MessageException me = new MessageException();
/* 231 */           me.setMessage(10131);
/* 232 */           throw me;
/*     */         }
/*     */       }
/*     */       else
/* 236 */         addParsedURL(url, newURLs);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void addInetAddress(InetAddress local, String url, Set newURLs)
/*     */     throws Exception
/*     */   {
/* 243 */     String localHostAddress = local.getHostAddress();
/* 244 */     if (localHostAddress != null)
/*     */     {
/* 246 */       addLocalServerURL(url, localHostAddress, newURLs);
/*     */     }
/*     */ 
/* 249 */     String localHostName = local.getHostName();
/* 250 */     if (localHostName != null)
/*     */     {
/* 252 */       addLocalServerURL(url, localHostName, newURLs);
/*     */ 
/* 254 */       InetAddress[] addrs = InetAddress.getAllByName(localHostName);
/* 255 */       for (int i = 0; i < addrs.length; i++)
/*     */       {
/* 257 */         InetAddress addr = addrs[i];
/* 258 */         String hostName = addr.getHostName();
/* 259 */         if (!hostName.equals(localHostName))
/*     */         {
/* 261 */           addLocalServerURL(url, hostName, newURLs);
/*     */         }
/* 263 */         String hostAddress = addr.getHostAddress();
/* 264 */         if (hostAddress.equals(localHostAddress))
/*     */           continue;
/* 266 */         addLocalServerURL(url, hostAddress, newURLs);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void addLocalServerURL(String url, String sub, Set newURLs)
/*     */   {
/* 274 */     String toSub = null;
/*     */ 
/* 277 */     if (sub.indexOf(":") != -1)
/*     */     {
/* 279 */       StringBuffer ipv6Sub = new StringBuffer("[");
/* 280 */       ipv6Sub.append(sub);
/* 281 */       ipv6Sub.append("]");
/* 282 */       toSub = ipv6Sub.toString();
/*     */     }
/*     */     else
/*     */     {
/* 286 */       toSub = sub;
/*     */     }
/*     */ 
/* 289 */     String newUrl = StringUtils.substitute(url, "{server.name}", toSub);
/* 290 */     addParsedURL(newUrl, newURLs);
/*     */   }
/*     */ 
/*     */   private static void addParsedURL(String url, Set newURLs)
/*     */   {
/* 295 */     if (!newURLs.contains(url))
/*     */     {
/* 297 */       newURLs.add(updateIPv6(url));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String updateIPv6(String src)
/*     */   {
/* 304 */     if ((src != null) && (src.indexOf('[') != -1) && (src.indexOf(']') != -1))
/*     */     {
/* 307 */       int start = src.indexOf('[');
/* 308 */       int end = src.indexOf(']');
/*     */ 
/* 310 */       StringBuffer updated = new StringBuffer(src.substring(0, start + 1));
/* 311 */       updated.append(updateToLongForm(src.substring(start + 1, end)));
/* 312 */       updated.append(src.substring(end));
/*     */ 
/* 314 */       return updated.toString();
/*     */     }
/*     */ 
/* 318 */     return src;
/*     */   }
/*     */ 
/*     */   protected static String updateToLongForm(String src)
/*     */   {
/* 325 */     int numberOfTokens = 0;
/* 326 */     int doubleColonIndex = src.indexOf("::", 0);
/* 327 */     if (doubleColonIndex != -1)
/*     */     {
/* 329 */       String[] hexTokens = src.split("\\:");
/* 330 */       for (int i = 0; i < hexTokens.length; i++)
/*     */       {
/* 332 */         if (!hexTokens[i].equals("")) {
/* 333 */           numberOfTokens++;
/*     */         }
/*     */       }
/*     */ 
/* 337 */       int numberOfMissingZeros = 8 - numberOfTokens;
/* 338 */       if (numberOfMissingZeros > 0)
/*     */       {
/* 340 */         String replacement = "";
/* 341 */         if (!src.startsWith("::"))
/* 342 */           replacement = ":";
/* 343 */         while (numberOfMissingZeros-- > 0)
/* 344 */           replacement = replacement + "0:";
/* 345 */         src = src.replaceFirst("\\::", replacement);
/*     */       }
/*     */     }
/*     */ 
/* 349 */     return src;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.util.SettingsReplaceUtil
 * JD-Core Version:    0.6.0
 */
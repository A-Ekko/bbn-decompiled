/*      */ package flex.messaging.endpoints;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*      */ import flex.messaging.FlexContext;
/*      */ import flex.messaging.FlexSession;
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.client.EndpointPushNotifier;
/*      */ import flex.messaging.client.FlexClient;
/*      */ import flex.messaging.client.FlushResult;
/*      */ import flex.messaging.client.UserAgentSettings;
/*      */ import flex.messaging.config.ConfigMap;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.AcknowledgeMessage;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.messages.MessagePerformanceInfo;
/*      */ import flex.messaging.messages.MessagePerformanceUtils;
/*      */ import flex.messaging.util.TimeoutManager;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.servlet.ServletOutputStream;
/*      */ import javax.servlet.http.HttpServletRequest;
/*      */ import javax.servlet.http.HttpServletResponse;
/*      */ 
/*      */ public abstract class BaseStreamingHTTPEndpoint extends BaseHTTPEndpoint
/*      */ {
/*   76 */   private static final byte[] CRLF_BYTES = { 13, 10 };
/*      */   private static final byte ZERO_BYTE = 48;
/*      */   private static final byte NULL_BYTE = 0;
/*      */   private static final String COMMAND_PARAM_NAME = "command";
/*      */   private static final String OPEN_COMMAND = "open";
/*      */   private static final String CLOSE_COMMAND = "close";
/*      */   private static final String STREAM_ID_PARAM_NAME = "streamId";
/*      */   private static final String VERSION_PARAM_NAME = "version";
/*      */   private static final String USER_AGENT_HEADER_NAME = "User-Agent";
/*      */   private static final String HTTP_1_0 = "HTTP/1.0";
/*      */   private static final String STREAMING_THREAD_NAME_EXTENSION = "-in-streaming-mode";
/*      */   private static final String MAX_STREAMING_CONNECTIONS_PER_SESSION = "max-streaming-connections-per-session";
/*      */   private static final String IDLE_TIMEOUT_MINUTES = "idle-timeout-minutes";
/*      */   private static final String KICKSTART_BYTES = "kickstart-bytes";
/*      */   private static final String MATCH_ON = "match-on";
/*      */   private static final String MAX_STREAMING_CLIENTS = "max-streaming-clients";
/*      */   private static final String SERVER_TO_CLIENT_HEARTBEAT_MILLIS = "server-to-client-heartbeat-millis";
/*      */   private static final String USER_AGENT = "user-agent";
/*      */   private static final String USER_AGENT_SETTINGS = "user-agent-settings";
/*      */   private static final int DEFAULT_CONNECTIONS_PER_SESSION = -1;
/*      */   private static final int DEFAULT_SERVER_TO_CLIENT_HEARTBEAT_MILLIS = 5000;
/*      */   private static final int DEFAULT_IDLE_TIMEOUT_MINUTES = 0;
/*      */   private static final int DEFAULT_MAX_STREAMING_CLIENTS = 10;
/*      */   public static final String POLL_NOT_SUPPORTED_CODE = "Server.PollNotSupported";
/*      */   public static final int POLL_NOT_SUPPORTED_MESSAGE = 10034;
/*  293 */   protected final Object lock = new Object();
/*      */   protected Map userAgentSettings;
/*  305 */   private volatile boolean canStream = true;
/*      */   private volatile TimeoutManager pushNotifierTimeoutManager;
/*      */   private ConcurrentHashMap currentStreamingRequests;
/*  329 */   private long serverToClientHeartbeatMillis = 5000L;
/*      */ 
/*  359 */   private int idleTimeoutMinutes = 0;
/*      */ 
/*  390 */   private int maxStreamingClients = 10;
/*      */   protected int streamingClientsCount;
/*      */ 
/*      */   public BaseStreamingHTTPEndpoint()
/*      */   {
/*  169 */     this(false);
/*      */   }
/*      */ 
/*      */   public BaseStreamingHTTPEndpoint(boolean enableManagement)
/*      */   {
/*  180 */     super(enableManagement);
/*  181 */     setIdleTimeoutMinutes(this.idleTimeoutMinutes);
/*      */ 
/*  183 */     putUserAgentSettings(UserAgentSettings.getAgent("MSIE"));
/*  184 */     putUserAgentSettings(UserAgentSettings.getAgent("Firefox"));
/*      */   }
/*      */ 
/*      */   public void initialize(String id, ConfigMap properties)
/*      */   {
/*  202 */     super.initialize(id, properties);
/*      */ 
/*  204 */     if ((properties == null) || (properties.size() == 0)) {
/*  205 */       return;
/*      */     }
/*      */ 
/*  208 */     this.serverToClientHeartbeatMillis = properties.getPropertyAsLong("server-to-client-heartbeat-millis", 5000L);
/*  209 */     setServerToClientHeartbeatMillis(this.serverToClientHeartbeatMillis);
/*      */ 
/*  212 */     int idleTimeoutMinutes = properties.getPropertyAsInt("idle-timeout-minutes", 0);
/*  213 */     setIdleTimeoutMinutes(idleTimeoutMinutes);
/*      */ 
/*  215 */     ConfigMap userAgents = properties.getPropertyAsMap("user-agent-settings", null);
/*      */     Iterator iter;
/*  216 */     if (userAgents != null)
/*      */     {
/*  218 */       List userAgent = userAgents.getPropertyAsList("user-agent", null);
/*  219 */       if (userAgent != null)
/*      */       {
/*  221 */         for (iter = userAgent.iterator(); iter.hasNext(); )
/*      */         {
/*  223 */           ConfigMap agent = (ConfigMap)iter.next();
/*  224 */           String matchOn = agent.getPropertyAsString("match-on", null);
/*  225 */           int kickstartBytes = agent.getPropertyAsInt("kickstart-bytes", 0);
/*  226 */           int connectionsPerSession = agent.getPropertyAsInt("max-streaming-connections-per-session", -1);
/*  227 */           if (matchOn != null)
/*      */           {
/*  229 */             UserAgentSettings ua = UserAgentSettings.getAgent(matchOn);
/*  230 */             ua.setKickstartBytes(kickstartBytes);
/*  231 */             ua.setMaxStreamingConnectionsPerSession(connectionsPerSession);
/*  232 */             putUserAgentSettings(ua);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  239 */     this.maxStreamingClients = properties.getPropertyAsInt("max-streaming-clients", 10);
/*      */ 
/*  242 */     this.canStream = (this.maxStreamingClients > 0);
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  247 */     if (isStarted()) {
/*  248 */       return;
/*      */     }
/*  250 */     super.start();
/*      */ 
/*  252 */     if (this.idleTimeoutMinutes > 0) {
/*  253 */       this.pushNotifierTimeoutManager = new TimeoutManager();
/*      */     }
/*  255 */     this.currentStreamingRequests = new ConcurrentHashMap();
/*      */   }
/*      */ 
/*      */   public void stop()
/*      */   {
/*  263 */     if (!isStarted()) {
/*  264 */       return;
/*      */     }
/*      */ 
/*  267 */     if (this.pushNotifierTimeoutManager != null)
/*      */     {
/*  269 */       this.pushNotifierTimeoutManager.shutdown();
/*  270 */       this.pushNotifierTimeoutManager = null;
/*      */     }
/*      */ 
/*  274 */     for (Iterator iter = this.currentStreamingRequests.values().iterator(); iter.hasNext(); )
/*      */     {
/*  276 */       EndpointPushNotifier notifier = (EndpointPushNotifier)iter.next();
/*  277 */       notifier.close();
/*      */     }
/*  279 */     this.currentStreamingRequests = null;
/*      */ 
/*  281 */     super.stop();
/*      */   }
/*      */ 
/*      */   public long getServerToClientHeartbeatMillis()
/*      */   {
/*  338 */     return this.serverToClientHeartbeatMillis;
/*      */   }
/*      */ 
/*      */   public void setServerToClientHeartbeatMillis(long serverToClientHeartbeatMillis)
/*      */   {
/*  350 */     if (serverToClientHeartbeatMillis < 0L)
/*  351 */       serverToClientHeartbeatMillis = 0L;
/*  352 */     this.serverToClientHeartbeatMillis = serverToClientHeartbeatMillis;
/*      */   }
/*      */ 
/*      */   public int getIdleTimeoutMinutes()
/*      */   {
/*  370 */     return this.idleTimeoutMinutes;
/*      */   }
/*      */ 
/*      */   public void setIdleTimeoutMinutes(int idleTimeoutMinutes)
/*      */   {
/*  383 */     this.idleTimeoutMinutes = idleTimeoutMinutes;
/*      */   }
/*      */ 
/*      */   public int getMaxStreamingClients()
/*      */   {
/*  401 */     return this.maxStreamingClients;
/*      */   }
/*      */ 
/*      */   public void setMaxStreamingClients(int maxStreamingClients)
/*      */   {
/*  413 */     this.maxStreamingClients = maxStreamingClients;
/*  414 */     this.canStream = (this.streamingClientsCount < maxStreamingClients);
/*      */   }
/*      */ 
/*      */   public int getStreamingClientsCount()
/*      */   {
/*  430 */     return this.streamingClientsCount;
/*      */   }
/*      */ 
/*      */   public ConfigMap describeEndpoint()
/*      */   {
/*  450 */     return super.describeEndpoint();
/*      */   }
/*      */ 
/*      */   public void service(HttpServletRequest req, HttpServletResponse res)
/*      */   {
/*  466 */     String command = req.getParameter("command");
/*  467 */     if (command != null)
/*  468 */       serviceStreamingRequest(req, res);
/*      */     else
/*  470 */       super.service(req, res);
/*      */   }
/*      */ 
/*      */   public UserAgentSettings getUserAgentSettings(String matchOn)
/*      */   {
/*  484 */     return this.userAgentSettings == null ? null : (UserAgentSettings)this.userAgentSettings.get(matchOn);
/*      */   }
/*      */ 
/*      */   public Collection getUserAgentSettings()
/*      */   {
/*  496 */     return this.userAgentSettings == null ? null : this.userAgentSettings.values();
/*      */   }
/*      */ 
/*      */   public void putUserAgentSettings(UserAgentSettings userAgent)
/*      */   {
/*  506 */     if (this.userAgentSettings == null)
/*  507 */       this.userAgentSettings = new HashMap();
/*  508 */     this.userAgentSettings.put(userAgent.getMatchOn(), userAgent);
/*      */   }
/*      */ 
/*      */   public void removeUserAgentSettings(UserAgentSettings userAgent)
/*      */   {
/*  517 */     if ((this.userAgentSettings != null) && (userAgent != null))
/*  518 */       this.userAgentSettings.remove(userAgent.getMatchOn());
/*      */   }
/*      */ 
/*      */   protected void addPerformanceInfo(Message message)
/*      */   {
/*  536 */     MessagePerformanceInfo mpiOriginal = MessagePerformanceUtils.getMPII(message);
/*  537 */     if (mpiOriginal == null) {
/*  538 */       return;
/*      */     }
/*      */ 
/*  542 */     MessagePerformanceInfo mpip = null;
/*  543 */     mpip = (MessagePerformanceInfo)mpiOriginal.clone();
/*      */     try
/*      */     {
/*  547 */       MessagePerformanceUtils.setMPIP(message, mpip);
/*  548 */       MessagePerformanceUtils.setMPII(message, null);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  552 */       if (Log.isDebug()) {
/*  553 */         this.log.debug("MPI exception while streaming the message: " + e.toString());
/*      */       }
/*      */     }
/*      */ 
/*  557 */     long serializationOverhead = 0L;
/*  558 */     MessagePerformanceInfo mpio = null;
/*  559 */     mpio = new MessagePerformanceInfo();
/*  560 */     if (mpip.recordMessageTimes)
/*      */     {
/*  562 */       mpio.sendTime = System.currentTimeMillis();
/*  563 */       mpio.infoType = "OUT";
/*      */     }
/*  565 */     mpio.pushedFlag = true;
/*  566 */     MessagePerformanceUtils.setMPIO(message, mpio);
/*      */ 
/*  569 */     if (mpip.recordMessageSizes)
/*      */     {
/*      */       try
/*      */       {
/*  575 */         serializationOverhead = System.currentTimeMillis();
/*  576 */         mpio.messageSize = getMessageSizeForPerformanceInfo(message);
/*      */ 
/*  579 */         if (mpip.recordMessageTimes)
/*      */         {
/*  581 */           serializationOverhead = System.currentTimeMillis() - serializationOverhead;
/*  582 */           mpip.addToOverhead(serializationOverhead);
/*  583 */           mpiOriginal.addToOverhead(serializationOverhead);
/*  584 */           mpio.sendTime = System.currentTimeMillis();
/*      */         }
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/*  589 */         this.log.debug("MPI exception while streaming the message: " + e.toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected long getMessageSizeForPerformanceInfo(Message message)
/*      */   {
/*  606 */     return 0L;
/*      */   }
/*      */ 
/*      */   protected FlushResult handleFlexClientPoll(FlexClient flexClient, CommandMessage pollCommand)
/*      */   {
/*  618 */     MessageException me = new MessageException();
/*  619 */     me.setMessage(10034);
/*  620 */     me.setDetails(10034);
/*  621 */     me.setCode("Server.PollNotSupported");
/*  622 */     throw me;
/*      */   }
/*      */ 
/*      */   protected void handleFlexClientStreamingOpenRequest(HttpServletRequest req, HttpServletResponse res, FlexClient flexClient)
/*      */   {
/*  635 */     FlexSession session = FlexContext.getFlexSession();
/*  636 */     if ((this.canStream) && (session.canStream))
/*      */     {
/*  644 */       boolean thisThreadCanStream = false;
/*  645 */       synchronized (this.lock)
/*      */       {
/*  647 */         this.streamingClientsCount += 1;
/*  648 */         if (this.streamingClientsCount == this.maxStreamingClients)
/*      */         {
/*  650 */           thisThreadCanStream = true;
/*  651 */           this.canStream = false;
/*      */         }
/*  653 */         else if (this.streamingClientsCount > this.maxStreamingClients)
/*      */         {
/*  655 */           thisThreadCanStream = false;
/*  656 */           this.streamingClientsCount -= 1;
/*      */         }
/*      */         else
/*      */         {
/*  661 */           thisThreadCanStream = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  667 */       if (!thisThreadCanStream)
/*      */       {
/*  669 */         if (Log.isError()) {
/*  670 */           this.log.error("Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '" + flexClient.getId() + "' because " + "max-streaming-clients" + " limit of '" + this.maxStreamingClients + "' has been reached.");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  676 */           res.sendError(400);
/*      */         }
/*      */         catch (IOException ignore) {
/*      */         }
/*  680 */         return;
/*      */       }
/*      */ 
/*  684 */       byte[] kickStartBytesToStream = null;
/*  685 */       String userAgentValue = req.getHeader("User-Agent");
/*  686 */       String userAgent = null;
/*  687 */       if (userAgentValue != null)
/*      */       {
/*  690 */         int bestMatchLength = 0;
/*  691 */         for (Iterator iter = this.userAgentSettings.keySet().iterator(); iter.hasNext(); )
/*      */         {
/*  693 */           String userAgentMatch = (String)iter.next();
/*  694 */           if (userAgentValue.indexOf(userAgentMatch) != -1)
/*      */           {
/*  696 */             int matchLength = userAgentMatch.length();
/*  697 */             if (matchLength > bestMatchLength)
/*      */             {
/*  699 */               bestMatchLength = matchLength;
/*  700 */               userAgent = userAgentMatch;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  705 */         if (userAgent != null)
/*      */         {
/*  707 */           UserAgentSettings ua = (UserAgentSettings)this.userAgentSettings.get(userAgent);
/*  708 */           if (ua != null)
/*      */           {
/*  710 */             synchronized (session)
/*      */             {
/*  712 */               session.maxConnectionsPerSession = ua.getMaxStreamingConnectionsPerSession();
/*      */             }
/*  714 */             int kickStartBytes = ua.getKickstartBytes();
/*  715 */             if (kickStartBytes > 0)
/*      */             {
/*      */               try
/*      */               {
/*  721 */                 int chunkLengthHeaderSize = Integer.toHexString(kickStartBytes).getBytes("ASCII").length;
/*  722 */                 int chunkOverhead = chunkLengthHeaderSize + 4;
/*  723 */                 int minimumKickstartBytes = kickStartBytes - chunkOverhead;
/*  724 */                 kickStartBytesToStream = new byte[minimumKickstartBytes > 0 ? minimumKickstartBytes : kickStartBytes];
/*      */               }
/*      */               catch (UnsupportedEncodingException ignore)
/*      */               {
/*  729 */                 kickStartBytesToStream = new byte[kickStartBytes];
/*      */               }
/*  731 */               Arrays.fill(kickStartBytesToStream, 0);
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  738 */       synchronized (session)
/*      */       {
/*  740 */         session.streamingConnectionsCount += 1;
/*  741 */         if (session.streamingConnectionsCount == session.maxConnectionsPerSession)
/*      */         {
/*  743 */           thisThreadCanStream = true;
/*  744 */           session.canStream = false;
/*      */         }
/*  746 */         else if (session.streamingConnectionsCount > session.maxConnectionsPerSession)
/*      */         {
/*  748 */           thisThreadCanStream = false;
/*  749 */           session.streamingConnectionsCount -= 1;
/*  750 */           synchronized (this.lock)
/*      */           {
/*  753 */             this.streamingClientsCount -= 1;
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  759 */           thisThreadCanStream = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  765 */       if (!thisThreadCanStream)
/*      */       {
/*  767 */         if (Log.isError()) {
/*  768 */           this.log.error("Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '" + flexClient.getId() + "' because " + "max-streaming-connections-per-session" + " limit of '" + session.maxConnectionsPerSession + "' for user-agent '" + userAgent + "' has been reached.");
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/*  774 */           res.sendError(400);
/*      */         }
/*      */         catch (IOException ignore)
/*      */         {
/*      */         }
/*      */ 
/*  780 */         return;
/*      */       }
/*      */ 
/*  783 */       Thread currentThread = Thread.currentThread();
/*  784 */       String threadName = currentThread.getName();
/*  785 */       EndpointPushNotifier notifier = null;
/*  786 */       boolean suppressIOExceptionLogging = false;
/*      */       try
/*      */       {
/*  789 */         currentThread.setName(threadName + "-in-streaming-mode");
/*      */ 
/*  792 */         if (this.addNoCacheHeaders)
/*  793 */           addNoCacheHeaders(req, res);
/*  794 */         res.setContentType(getResponseContentType());
/*  795 */         res.setHeader("Connection", "close");
/*  796 */         res.setHeader("Transfer-Encoding", "chunked");
/*  797 */         ServletOutputStream os = res.getOutputStream();
/*  798 */         res.flushBuffer();
/*      */ 
/*  801 */         if (kickStartBytesToStream != null)
/*      */         {
/*  803 */           if (Log.isDebug()) {
/*  804 */             this.log.debug("Endpoint with id '" + getId() + "' is streaming " + kickStartBytesToStream.length + " bytes (not counting chunk encoding overhead) to kick-start the streaming connection for FlexClient with id '" + flexClient.getId() + "'.");
/*      */           }
/*      */ 
/*  808 */           streamChunk(kickStartBytesToStream, os, res);
/*      */         }
/*      */ 
/*  812 */         setThreadLocals();
/*      */         try
/*      */         {
/*  818 */           notifier = new EndpointPushNotifier(this, flexClient);
/*      */         }
/*      */         catch (MessageException me)
/*      */         {
/*  822 */           if (me.getNumber() == 10033)
/*      */           {
/*  824 */             if (Log.isWarn()) {
/*  825 */               this.log.warn("Endpoint with id '" + getId() + "' received a duplicate streaming connection request from, FlexClient with id '" + flexClient.getId() + "'. Faulting request.");
/*      */             }
/*      */ 
/*  829 */             synchronized (this.lock)
/*      */             {
/*  831 */               this.streamingClientsCount -= 1;
/*  832 */               this.canStream = (this.streamingClientsCount < this.maxStreamingClients);
/*  833 */               synchronized (session)
/*      */               {
/*  835 */                 session.streamingConnectionsCount -= 1;
/*  836 */                 session.canStream = (session.streamingConnectionsCount < session.maxConnectionsPerSession);
/*      */               }
/*      */             }
/*      */             try
/*      */             {
/*  841 */               res.sendError(400);
/*      */             }
/*      */             catch (IOException ignore)
/*      */             {
/*  847 */               jsr 586;
/*      */             }
/*      */           }
/*      */         }
/*  850 */         notifier.setIdleTimeoutMinutes(this.idleTimeoutMinutes);
/*  851 */         notifier.setLogCategory(getLogCategory());
/*  852 */         monitorTimeout(notifier);
/*  853 */         this.currentStreamingRequests.put(notifier.getNotifierId(), notifier);
/*      */ 
/*  856 */         AcknowledgeMessage connectAck = new AcknowledgeMessage();
/*  857 */         connectAck.setBody(notifier.getNotifierId());
/*  858 */         connectAck.setCorrelationId("open");
/*  859 */         ArrayList toPush = new ArrayList(1);
/*  860 */         toPush.add(connectAck);
/*  861 */         streamMessages(toPush, os, res);
/*      */ 
/*  864 */         if (Log.isDebug()) {
/*  865 */           Log.getLogger("Endpoint.FlexSession").info("Number of streaming clients for FlexSession with id '" + session.getId() + "' is " + session.streamingConnectionsCount + ".");
/*      */         }
/*      */ 
/*  868 */         if (Log.isDebug()) {
/*  869 */           this.log.debug("Number of streaming clients for endpoint with id '" + getId() + "' is " + this.streamingClientsCount + ".");
/*      */         }
/*      */ 
/*  873 */         while (!notifier.isClosed())
/*      */         {
/*  876 */           synchronized (notifier.pushNeeded)
/*      */           {
/*      */             try
/*      */             {
/*  882 */               streamMessages(notifier.drainMessages(), os, res);
/*      */ 
/*  884 */               notifier.pushNeeded.wait(this.serverToClientHeartbeatMillis);
/*      */ 
/*  886 */               List messages = notifier.drainMessages();
/*      */ 
/*  889 */               if ((messages == null) && (this.serverToClientHeartbeatMillis > 0L))
/*      */               {
/*      */                 try
/*      */                 {
/*  893 */                   os.write(0);
/*  894 */                   res.flushBuffer();
/*      */                 }
/*      */                 catch (IOException e)
/*      */                 {
/*  898 */                   if (Log.isWarn()) {
/*  899 */                     this.log.warn("Endpoint with id '" + getId() + "' is closing the streaming connection to FlexClient with id '" + flexClient.getId() + "' because endpoint encountered a socket write error" + ", possibly due to an unresponsive FlexClient.");
/*      */                   }
/*      */ 
/*  902 */                   break;
/*      */                 }
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  910 */                 notifier.updateLastUse();
/*      */ 
/*  912 */                 streamMessages(messages, os, res);
/*      */               }
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*  917 */               if (Log.isWarn())
/*  918 */                 this.log.warn("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' has been interrupted and the streaming connection will be closed.");
/*  919 */               os.close();
/*  920 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  927 */           flexClient.updateLastUse();
/*      */         }
/*  929 */         if (Log.isDebug())
/*  930 */           this.log.debug("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' is releasing connection and returning to the request handler pool.");
/*  931 */         suppressIOExceptionLogging = true;
/*      */ 
/*  933 */         streamChunk(null, os, res);
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  937 */         if ((Log.isWarn()) && (!suppressIOExceptionLogging))
/*  938 */           this.log.warn("Streaming thread '" + threadName + "' for endpoint with id '" + getId() + "' is closing connection due to an IO error.", e);
/*      */       }
/*      */       finally
/*      */       {
/*  942 */         currentThread.setName(threadName);
/*      */ 
/*  946 */         synchronized (this.lock)
/*      */         {
/*  948 */           this.streamingClientsCount -= 1;
/*  949 */           this.canStream = (this.streamingClientsCount < this.maxStreamingClients);
/*  950 */           synchronized (session)
/*      */           {
/*  952 */             session.streamingConnectionsCount -= 1;
/*  953 */             session.canStream = (session.streamingConnectionsCount < session.maxConnectionsPerSession);
/*      */           }
/*      */         }
/*      */ 
/*  957 */         if (notifier != null)
/*      */         {
/*  959 */           this.currentStreamingRequests.remove(notifier.getNotifierId());
/*  960 */           notifier.close();
/*      */         }
/*      */ 
/*  964 */         if (Log.isDebug()) {
/*  965 */           Log.getLogger("Endpoint.FlexSession").info("Number of streaming clients for FlexSession with id '" + session.getId() + "' is " + session.streamingConnectionsCount + ".");
/*      */         }
/*      */ 
/*  968 */         if (Log.isDebug()) {
/*  969 */           this.log.debug("Number of streaming clients for endpoint with id '" + getId() + "' is " + this.streamingClientsCount + ".");
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  975 */       if (Log.isError())
/*      */       {
/*  977 */         String logString = null;
/*  978 */         if (!this.canStream)
/*      */         {
/*  980 */           logString = "Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '" + flexClient.getId() + "' because " + "max-streaming-clients" + " limit of '" + this.maxStreamingClients + "' has been reached.";
/*      */         }
/*  984 */         else if (!session.canStream)
/*      */         {
/*  986 */           logString = "Endpoint with id '" + getId() + "' cannot grant streaming connection to FlexClient with id '" + flexClient.getId() + "' because " + "max-streaming-connections-per-session" + " limit of '" + session.maxConnectionsPerSession + "' has been reached.";
/*      */         }
/*      */ 
/*  990 */         if (logString != null) {
/*  991 */           this.log.error(logString);
/*      */         }
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  997 */         res.sendError(400);
/*      */       }
/*      */       catch (IOException ignore)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void handleFlexClientStreamingCloseRequest(HttpServletRequest req, HttpServletResponse res, FlexClient flexClient, String streamId)
/*      */   {
/* 1015 */     if (streamId != null)
/*      */     {
/* 1017 */       EndpointPushNotifier notifier = (EndpointPushNotifier)flexClient.getEndpointPushHandler(getId());
/* 1018 */       if ((notifier != null) && (notifier.getNotifierId().equals(streamId)))
/* 1019 */         notifier.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void serviceStreamingRequest(HttpServletRequest req, HttpServletResponse res)
/*      */   {
/* 1042 */     String command = req.getParameter("command");
/*      */ 
/* 1045 */     if (req.getProtocol().equals("HTTP/1.0"))
/*      */     {
/* 1047 */       if (Log.isError()) {
/* 1048 */         this.log.error("Endpoint with id '" + getId() + "' cannot service the streaming request made with " + " HTTP 1.0. Only HTTP 1.1 is supported.");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1054 */         res.sendError(400);
/*      */       }
/*      */       catch (IOException ignore) {
/*      */       }
/* 1058 */       return;
/*      */     }
/*      */ 
/* 1061 */     if ((!command.equals("open")) && (!command.equals("close")))
/*      */     {
/* 1063 */       if (Log.isError()) {
/* 1064 */         this.log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as the supplied command '" + command + "' is invalid.");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1070 */         res.sendError(400);
/*      */       }
/*      */       catch (IOException ignore) {
/*      */       }
/* 1074 */       return;
/*      */     }
/*      */ 
/* 1077 */     String flexClientId = req.getParameter("DSId");
/* 1078 */     if (flexClientId == null)
/*      */     {
/* 1080 */       if (Log.isError()) {
/* 1081 */         this.log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as no FlexClient id" + " has been supplied in the request.");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1087 */         res.sendError(400);
/*      */       }
/*      */       catch (IOException ignore) {
/*      */       }
/* 1091 */       return;
/*      */     }
/*      */ 
/* 1100 */     FlexClient flexClient = null;
/* 1101 */     List flexClients = FlexContext.getFlexSession().getFlexClients();
/* 1102 */     boolean validFlexClientId = false;
/* 1103 */     for (Iterator iter = flexClients.iterator(); iter.hasNext(); )
/*      */     {
/* 1105 */       flexClient = (FlexClient)iter.next();
/* 1106 */       if ((!flexClient.getId().equals(flexClientId)) || (!flexClient.isValid()))
/*      */         continue;
/* 1108 */       validFlexClientId = true;
/*      */     }
/*      */ 
/* 1112 */     if ((!command.equals("close")) && (!validFlexClientId))
/*      */     {
/* 1114 */       if (Log.isError()) {
/* 1115 */         this.log.error("Endpoint with id '" + getId() + "' cannot service the streaming request as either the supplied" + " FlexClient id '" + flexClientId + " is not valid, or the FlexClient with that id is not valid.");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1121 */         res.sendError(400);
/*      */       }
/*      */       catch (IOException ignore) {
/*      */       }
/* 1125 */       return;
/*      */     }
/*      */ 
/* 1128 */     if (flexClient != null)
/*      */     {
/* 1130 */       if (command.equals("open"))
/* 1131 */         handleFlexClientStreamingOpenRequest(req, res, flexClient);
/* 1132 */       else if (command.equals("close"))
/* 1133 */         handleFlexClientStreamingCloseRequest(req, res, flexClient, req.getParameter("streamId"));
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void streamChunk(byte[] bytes, ServletOutputStream os, HttpServletResponse response)
/*      */     throws IOException
/*      */   {
/* 1152 */     if ((bytes != null) && (bytes.length > 0))
/*      */     {
/* 1154 */       byte[] chunkLength = Integer.toHexString(bytes.length).getBytes("ASCII");
/* 1155 */       os.write(chunkLength);
/* 1156 */       os.write(CRLF_BYTES);
/* 1157 */       os.write(bytes);
/* 1158 */       os.write(CRLF_BYTES);
/* 1159 */       response.flushBuffer();
/*      */     }
/*      */     else
/*      */     {
/* 1163 */       os.write(48);
/* 1164 */       os.write(CRLF_BYTES);
/* 1165 */       response.flushBuffer();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected abstract void streamMessages(List paramList, ServletOutputStream paramServletOutputStream, HttpServletResponse paramHttpServletResponse)
/*      */     throws IOException;
/*      */ 
/*      */   private void monitorTimeout(EndpointPushNotifier notifier)
/*      */   {
/* 1192 */     if (this.pushNotifierTimeoutManager != null)
/* 1193 */       this.pushNotifierTimeoutManager.scheduleTimeout(notifier);
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.BaseStreamingHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
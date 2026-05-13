/*     */ package flex.messaging.client;
/*     */ 
/*     */ public class UserAgentSettings
/*     */ {
/*     */   public static final String GENERIC_MSIE_USER_AGENT = "MSIE";
/*     */   public static final int MSIE_KICKSTART_BYTES = 2048;
/*     */   public static final String GENERIC_FIREFOX_USER_AGENT = "Firefox";
/*     */   public static final int FIREFOX_KICKSTART_BYTES = 0;
/*     */   public static final int DEFAULT_MAX_STREAMING_CONNECTIONS_PER_SESSION = 1;
/*     */   private String matchOn;
/*     */   private int kickstartBytes;
/*  66 */   private int maxStreamingConnectionsPerSession = 1;
/*     */ 
/*     */   public static UserAgentSettings getAgent(String matchOn)
/*     */   {
/*  75 */     UserAgentSettings userAgent = new UserAgentSettings();
/*  76 */     userAgent.setMatchOn(matchOn);
/*  77 */     userAgent.setMaxStreamingConnectionsPerSession(1);
/*  78 */     if ("MSIE".equals(matchOn))
/*  79 */       userAgent.setKickstartBytes(2048);
/*  80 */     else if ("Firefox".equals(matchOn))
/*  81 */       userAgent.setKickstartBytes(0);
/*  82 */     return userAgent;
/*     */   }
/*     */ 
/*     */   public String getMatchOn()
/*     */   {
/*  92 */     return this.matchOn;
/*     */   }
/*     */ 
/*     */   public void setMatchOn(String matchOn)
/*     */   {
/* 102 */     this.matchOn = matchOn;
/*     */   }
/*     */ 
/*     */   public int getKickstartBytes()
/*     */   {
/* 114 */     return this.kickstartBytes;
/*     */   }
/*     */ 
/*     */   public void setKickstartBytes(int kickstartBytes)
/*     */   {
/* 126 */     if (kickstartBytes < 0)
/* 127 */       kickstartBytes = 0;
/* 128 */     this.kickstartBytes = kickstartBytes;
/*     */   }
/*     */ 
/*     */   public int getMaxStreamingConnectionsPerSession()
/*     */   {
/* 139 */     return this.maxStreamingConnectionsPerSession;
/*     */   }
/*     */ 
/*     */   public void setMaxStreamingConnectionsPerSession(int maxStreamingConnectionsPerSession)
/*     */   {
/* 151 */     this.maxStreamingConnectionsPerSession = maxStreamingConnectionsPerSession;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.UserAgentSettings
 * JD-Core Version:    0.6.0
 */
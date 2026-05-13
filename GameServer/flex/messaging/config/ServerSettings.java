/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class ServerSettings
/*     */ {
/*     */   private static final int INVALID_CLUSTER_MESSAGE_ROUTING = 11121;
/*     */   private boolean allowSubtopics;
/*     */   private boolean broadcastRoutingMode;
/*     */   private int maxCacheSize;
/*  37 */   private long messageTTL = -1L;
/*     */   private boolean isDurable;
/*     */   private String subtopicSeparator;
/*     */ 
/*     */   public ServerSettings()
/*     */   {
/*  46 */     this.broadcastRoutingMode = false;
/*  47 */     this.isDurable = false;
/*  48 */     this.maxCacheSize = 0;
/*  49 */     this.subtopicSeparator = ".";
/*     */   }
/*     */ 
/*     */   public boolean getAllowSubtopics()
/*     */   {
/*  59 */     return this.allowSubtopics;
/*     */   }
/*     */ 
/*     */   public void setAllowSubtopics(boolean value)
/*     */   {
/*  69 */     this.allowSubtopics = value;
/*     */   }
/*     */ 
/*     */   public boolean isBroadcastRoutingMode()
/*     */   {
/*  79 */     return this.broadcastRoutingMode;
/*     */   }
/*     */ 
/*     */   public void setBroadcastRoutingMode(String routingMode)
/*     */   {
/*  90 */     if (routingMode.equalsIgnoreCase("broadcast")) {
/*  91 */       this.broadcastRoutingMode = true;
/*  92 */     } else if (routingMode.equalsIgnoreCase("server-to-server")) {
/*  93 */       this.broadcastRoutingMode = false;
/*     */     }
/*     */     else {
/*  96 */       ConfigurationException ce = new ConfigurationException();
/*  97 */       ce.setMessage(11121, new Object[] { routingMode });
/*  98 */       throw ce;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxCacheSize()
/*     */   {
/* 109 */     return this.maxCacheSize;
/*     */   }
/*     */ 
/*     */   public void setMaxCacheSize(int size)
/*     */   {
/* 120 */     this.maxCacheSize = size;
/*     */   }
/*     */ 
/*     */   public long getMessageTTL()
/*     */   {
/* 130 */     return this.messageTTL;
/*     */   }
/*     */ 
/*     */   public void setMessageTTL(long ttl)
/*     */   {
/* 140 */     this.messageTTL = ttl;
/*     */   }
/*     */ 
/*     */   public boolean isDurable()
/*     */   {
/* 150 */     return this.isDurable;
/*     */   }
/*     */ 
/*     */   public void setDurable(boolean durable)
/*     */   {
/* 160 */     this.isDurable = durable;
/*     */   }
/*     */ 
/*     */   public String getSubtopicSeparator()
/*     */   {
/* 170 */     return this.subtopicSeparator;
/*     */   }
/*     */ 
/*     */   public void setSubtopicSeparator(String value)
/*     */   {
/* 181 */     this.subtopicSeparator = value;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServerSettings
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class NetworkSettings
/*     */ {
/*     */   protected String clusterId;
/*     */   protected ThrottleSettings throttleSettings;
/*     */   protected int subscriptionTimeoutMinutes;
/*  32 */   protected boolean sharedBackend = true;
/*  33 */   private boolean sharedBackendSet = false;
/*     */   public static final String NETWORK_ELEMENT = "network";
/*     */   public static final String SUBSCRIPTION_TIMEOUT_MINUTES = "subscription-timeout-minutes";
/*     */   public static final String SESSION_TIMEOUT = "session-timeout";
/*     */   public static final int DEFAULT_TIMEOUT = 0;
/*     */ 
/*     */   public NetworkSettings()
/*     */   {
/*  46 */     this.throttleSettings = new ThrottleSettings();
/*  47 */     this.subscriptionTimeoutMinutes = 0;
/*     */   }
/*     */ 
/*     */   public String getClusterId()
/*     */   {
/*  57 */     return this.clusterId;
/*     */   }
/*     */ 
/*     */   public void setClusterId(String id)
/*     */   {
/*  67 */     this.clusterId = id;
/*     */   }
/*     */ 
/*     */   public int getSubscriptionTimeoutMinutes()
/*     */   {
/*  77 */     return this.subscriptionTimeoutMinutes;
/*     */   }
/*     */ 
/*     */   public void setSubscriptionTimeoutMinutes(int value)
/*     */   {
/*  90 */     this.subscriptionTimeoutMinutes = value;
/*     */   }
/*     */ 
/*     */   public boolean isSharedBackend()
/*     */   {
/* 100 */     return this.sharedBackend;
/*     */   }
/*     */ 
/*     */   public void setSharedBackend(boolean sharedBackend)
/*     */   {
/* 110 */     this.sharedBackend = sharedBackend;
/* 111 */     this.sharedBackendSet = true;
/*     */   }
/*     */ 
/*     */   public boolean isSharedBackendSet()
/*     */   {
/* 120 */     return this.sharedBackendSet;
/*     */   }
/*     */ 
/*     */   public ThrottleSettings getThrottleSettings()
/*     */   {
/* 130 */     return this.throttleSettings;
/*     */   }
/*     */ 
/*     */   public void setThrottleSettings(ThrottleSettings t)
/*     */   {
/* 140 */     this.throttleSettings = t;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.NetworkSettings
 * JD-Core Version:    0.6.0
 */
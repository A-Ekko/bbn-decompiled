/*     */ package flex.management.runtime.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.messaging.Destination;
/*     */ import java.util.Date;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public class MessageDestinationControl extends DestinationControl
/*     */   implements MessageDestinationControlMBean
/*     */ {
/*     */   private static final String TYPE = "MessageDestination";
/*     */   private ObjectName messageCache;
/*     */   private ObjectName throttleManager;
/*     */   private ObjectName subscriptionManager;
/*  43 */   private int serviceMessageCount = 0;
/*     */   private Date lastServiceMessageTimestamp;
/*     */   private long serviceMessageStart;
/*  46 */   private int serviceCommandCount = 0;
/*     */   private Date lastServiceCommandTimestamp;
/*     */   private long serviceCommandStart;
/*  49 */   private int serviceMessageFromAdapterCount = 0;
/*     */   private Date lastServiceMessageFromAdapterTimestamp;
/*     */   private long serviceMessageFromAdapterStart;
/*     */ 
/*     */   public MessageDestinationControl(Destination destination, BaseControl parent)
/*     */   {
/*  60 */     super(destination, parent);
/*  61 */     this.serviceMessageStart = System.currentTimeMillis();
/*  62 */     this.serviceCommandStart = this.serviceMessageStart;
/*  63 */     this.serviceMessageFromAdapterStart = this.serviceMessageStart;
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*  68 */     String name = getObjectName().getCanonicalName();
/*     */ 
/*  70 */     String[] pollablePerInterval = { "ServiceCommandCount", "ServiceMessageCount", "ServiceMessageFromAdapterCount" };
/*     */ 
/*  72 */     String[] pollableGeneral = { "ServiceCommandFrequency", "ServiceMessageFrequency", "ServiceMessageFromAdapterFrequency", "LastServiceCommandTimestamp", "LastServiceMessageTimestamp", "LastServiceMessageFromAdapterTimestamp" };
/*     */ 
/*  76 */     getRegistrar().registerObjects(new int[] { 151, 50 }, name, pollablePerInterval);
/*     */ 
/*  79 */     getRegistrar().registerObjects(151, name, pollableGeneral);
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  89 */     return "MessageDestination";
/*     */   }
/*     */ 
/*     */   public ObjectName getMessageCache()
/*     */   {
/*  98 */     return this.messageCache;
/*     */   }
/*     */ 
/*     */   public void setMessageCache(ObjectName value)
/*     */   {
/* 108 */     this.messageCache = value;
/*     */   }
/*     */ 
/*     */   public ObjectName getThrottleManager()
/*     */   {
/* 117 */     return this.throttleManager;
/*     */   }
/*     */ 
/*     */   public void setThrottleManager(ObjectName value)
/*     */   {
/* 127 */     this.throttleManager = value;
/*     */   }
/*     */ 
/*     */   public ObjectName getSubscriptionManager()
/*     */   {
/* 136 */     return this.subscriptionManager;
/*     */   }
/*     */ 
/*     */   public void setSubscriptionManager(ObjectName value)
/*     */   {
/* 146 */     this.subscriptionManager = value;
/*     */   }
/*     */ 
/*     */   public Integer getServiceMessageCount()
/*     */   {
/* 155 */     return new Integer(this.serviceMessageCount);
/*     */   }
/*     */ 
/*     */   public void resetServiceMessageCount()
/*     */   {
/* 164 */     this.serviceMessageStart = System.currentTimeMillis();
/* 165 */     this.serviceMessageCount = 0;
/* 166 */     this.lastServiceMessageTimestamp = null;
/*     */   }
/*     */ 
/*     */   public void incrementServiceMessageCount()
/*     */   {
/* 174 */     this.serviceMessageCount += 1;
/* 175 */     this.lastServiceMessageTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public Date getLastServiceMessageTimestamp()
/*     */   {
/* 184 */     return this.lastServiceMessageTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getServiceMessageFrequency()
/*     */   {
/* 193 */     if (this.serviceMessageCount > 0)
/*     */     {
/* 195 */       double runtime = differenceInMinutes(this.serviceMessageStart, System.currentTimeMillis());
/* 196 */       return new Double(this.serviceMessageCount / runtime);
/*     */     }
/*     */ 
/* 200 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getServiceCommandCount()
/*     */   {
/* 210 */     return new Integer(this.serviceCommandCount);
/*     */   }
/*     */ 
/*     */   public void resetServiceCommandCount()
/*     */   {
/* 219 */     this.serviceCommandStart = System.currentTimeMillis();
/* 220 */     this.serviceCommandCount = 0;
/* 221 */     this.lastServiceCommandTimestamp = null;
/*     */   }
/*     */ 
/*     */   public void incrementServiceCommandCount()
/*     */   {
/* 229 */     this.serviceCommandCount += 1;
/* 230 */     this.lastServiceCommandTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public Date getLastServiceCommandTimestamp()
/*     */   {
/* 239 */     return this.lastServiceCommandTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getServiceCommandFrequency()
/*     */   {
/* 248 */     if (this.serviceCommandCount > 0)
/*     */     {
/* 250 */       double runtime = differenceInMinutes(this.serviceCommandStart, System.currentTimeMillis());
/* 251 */       return new Double(this.serviceCommandCount / runtime);
/*     */     }
/*     */ 
/* 255 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getServiceMessageFromAdapterCount()
/*     */   {
/* 265 */     return new Integer(this.serviceMessageFromAdapterCount);
/*     */   }
/*     */ 
/*     */   public void resetServiceMessageFromAdapterCount()
/*     */   {
/* 274 */     this.serviceMessageFromAdapterStart = System.currentTimeMillis();
/* 275 */     this.serviceMessageFromAdapterCount = 0;
/* 276 */     this.lastServiceMessageFromAdapterTimestamp = null;
/*     */   }
/*     */ 
/*     */   public void incrementServiceMessageFromAdapterCount()
/*     */   {
/* 284 */     this.serviceMessageFromAdapterCount += 1;
/* 285 */     this.lastServiceMessageFromAdapterTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public Date getLastServiceMessageFromAdapterTimestamp()
/*     */   {
/* 294 */     return this.lastServiceMessageFromAdapterTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getServiceMessageFromAdapterFrequency()
/*     */   {
/* 303 */     if (this.serviceMessageFromAdapterCount > 0)
/*     */     {
/* 305 */       double runtime = differenceInMinutes(this.serviceMessageFromAdapterStart, System.currentTimeMillis());
/* 306 */       return new Double(this.serviceMessageFromAdapterCount / runtime);
/*     */     }
/*     */ 
/* 310 */     return new Double(0.0D);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.MessageDestinationControl
 * JD-Core Version:    0.6.0
 */
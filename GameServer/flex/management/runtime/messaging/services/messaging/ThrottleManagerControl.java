/*     */ package flex.management.runtime.messaging.services.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.messaging.services.messaging.ThrottleManager;
/*     */ import java.util.Date;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public class ThrottleManagerControl extends BaseControl
/*     */   implements ThrottleManagerControlMBean
/*     */ {
/*     */   private ThrottleManager throttleManager;
/*     */   private long clientIncomingMessageThrottleStart;
/*     */   private int clientIncomingMessageThrottleCount;
/*     */   private Date lastClientIncomingMessageThrottleTimestamp;
/*     */   private long clientOutgoingMessageThrottleStart;
/*     */   private int clientOutgoingMessageThrottleCount;
/*     */   private Date lastClientOutgoingMessageThrottleTimestamp;
/*     */   private long destinationIncomingMessageThrottleStart;
/*     */   private int destinationIncomingMessageThrottleCount;
/*     */   private Date lastDestinationIncomingMessageThrottleTimestamp;
/*     */   private long destinationOutgoingMessageThrottleStart;
/*     */   private int destinationOutgoingMessageThrottleCount;
/*     */   private Date lastDestinationOutgoingMessageThrottleTimestamp;
/*     */ 
/*     */   public ThrottleManagerControl(ThrottleManager throttleManager, BaseControl parent)
/*     */   {
/*  59 */     super(parent);
/*  60 */     this.throttleManager = throttleManager;
/*  61 */     this.clientIncomingMessageThrottleStart = System.currentTimeMillis();
/*  62 */     this.clientOutgoingMessageThrottleStart = this.clientIncomingMessageThrottleStart;
/*  63 */     this.destinationIncomingMessageThrottleStart = this.clientIncomingMessageThrottleStart;
/*  64 */     this.destinationOutgoingMessageThrottleStart = this.clientIncomingMessageThrottleStart;
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*  69 */     String name = getObjectName().getCanonicalName();
/*  70 */     String[] attributes = { "ClientIncomingMessageThrottleCount", "ClientIncomingMessageThrottleFrequency", "ClientOutgoingMessageThrottleCount", "ClientOutgoingMessageThrottleFrequency", "DestinationIncomingMessageThrottleCount", "DestinationIncomingMessageThrottleFrequency", "DestinationOutgoingMessageThrottleCount", "DestinationOutgoingMessageThrottleFrequency", "LastClientIncomingMessageThrottleTimestamp", "LastClientOutgoingMessageThrottleTimestamp", "LastDestinationIncomingMessageThrottleTimestamp", "LastDestinationOutgoingMessageThrottleTimestamp" };
/*     */ 
/*  79 */     getRegistrar().registerObjects(151, name, attributes);
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  88 */     return this.throttleManager.getId();
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  97 */     return "ThrottleManager";
/*     */   }
/*     */ 
/*     */   public Integer getClientIncomingMessageThrottleCount()
/*     */   {
/* 106 */     return new Integer(this.clientIncomingMessageThrottleCount);
/*     */   }
/*     */ 
/*     */   public void incrementClientIncomingMessageThrottleCount()
/*     */   {
/* 114 */     this.clientIncomingMessageThrottleCount += 1;
/* 115 */     this.lastClientIncomingMessageThrottleTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public void resetClientIncomingMessageThrottleCount()
/*     */   {
/* 124 */     this.clientIncomingMessageThrottleStart = System.currentTimeMillis();
/* 125 */     this.clientIncomingMessageThrottleCount = 0;
/* 126 */     this.lastClientIncomingMessageThrottleTimestamp = null;
/*     */   }
/*     */ 
/*     */   public Date getLastClientIncomingMessageThrottleTimestamp()
/*     */   {
/* 135 */     return this.lastClientIncomingMessageThrottleTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getClientIncomingMessageThrottleFrequency()
/*     */   {
/* 144 */     if (this.clientIncomingMessageThrottleCount > 0)
/*     */     {
/* 146 */       double runtime = differenceInMinutes(this.clientIncomingMessageThrottleStart, System.currentTimeMillis());
/* 147 */       return new Double(this.clientIncomingMessageThrottleCount / runtime);
/*     */     }
/*     */ 
/* 151 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getClientOutgoingMessageThrottleCount()
/*     */   {
/* 161 */     return new Integer(this.clientOutgoingMessageThrottleCount);
/*     */   }
/*     */ 
/*     */   public void incrementClientOutgoingMessageThrottleCount()
/*     */   {
/* 169 */     this.clientOutgoingMessageThrottleCount += 1;
/* 170 */     this.lastClientOutgoingMessageThrottleTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public void resetClientOutgoingMessageThrottleCount()
/*     */   {
/* 179 */     this.clientOutgoingMessageThrottleStart = System.currentTimeMillis();
/* 180 */     this.clientOutgoingMessageThrottleCount = 0;
/* 181 */     this.lastClientOutgoingMessageThrottleTimestamp = null;
/*     */   }
/*     */ 
/*     */   public Date getLastClientOutgoingMessageThrottleTimestamp()
/*     */   {
/* 190 */     return this.lastClientOutgoingMessageThrottleTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getClientOutgoingMessageThrottleFrequency()
/*     */   {
/* 199 */     if (this.clientOutgoingMessageThrottleCount > 0)
/*     */     {
/* 201 */       double runtime = differenceInMinutes(this.clientOutgoingMessageThrottleStart, System.currentTimeMillis());
/* 202 */       return new Double(this.clientOutgoingMessageThrottleCount / runtime);
/*     */     }
/*     */ 
/* 206 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getDestinationIncomingMessageThrottleCount()
/*     */   {
/* 216 */     return new Integer(this.destinationIncomingMessageThrottleCount);
/*     */   }
/*     */ 
/*     */   public void incrementDestinationIncomingMessageThrottleCount()
/*     */   {
/* 224 */     this.destinationIncomingMessageThrottleCount += 1;
/* 225 */     this.lastDestinationIncomingMessageThrottleTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public void resetDestinationIncomingMessageThrottleCount()
/*     */   {
/* 234 */     this.destinationIncomingMessageThrottleStart = System.currentTimeMillis();
/* 235 */     this.destinationIncomingMessageThrottleCount = 0;
/* 236 */     this.lastDestinationIncomingMessageThrottleTimestamp = null;
/*     */   }
/*     */ 
/*     */   public Date getLastDestinationIncomingMessageThrottleTimestamp()
/*     */   {
/* 245 */     return this.lastDestinationIncomingMessageThrottleTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getDestinationIncomingMessageThrottleFrequency()
/*     */   {
/* 254 */     if (this.destinationIncomingMessageThrottleCount > 0)
/*     */     {
/* 256 */       double runtime = differenceInMinutes(this.destinationIncomingMessageThrottleStart, System.currentTimeMillis());
/* 257 */       return new Double(this.destinationIncomingMessageThrottleCount / runtime);
/*     */     }
/*     */ 
/* 261 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getDestinationOutgoingMessageThrottleCount()
/*     */   {
/* 271 */     return new Integer(this.destinationOutgoingMessageThrottleCount);
/*     */   }
/*     */ 
/*     */   public void incrementDestinationOutgoingMessageThrottleCount()
/*     */   {
/* 279 */     this.destinationOutgoingMessageThrottleCount += 1;
/* 280 */     this.lastDestinationOutgoingMessageThrottleTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public void resetDestinationOutgoingMessageThrottleCount()
/*     */   {
/* 289 */     this.destinationOutgoingMessageThrottleStart = System.currentTimeMillis();
/* 290 */     this.destinationOutgoingMessageThrottleCount = 0;
/* 291 */     this.lastDestinationOutgoingMessageThrottleTimestamp = null;
/*     */   }
/*     */ 
/*     */   public Date getLastDestinationOutgoingMessageThrottleTimestamp()
/*     */   {
/* 300 */     return this.lastDestinationOutgoingMessageThrottleTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getDestinationOutgoingMessageThrottleFrequency()
/*     */   {
/* 305 */     if (this.destinationOutgoingMessageThrottleCount > 0)
/*     */     {
/* 307 */       double runtime = differenceInMinutes(this.destinationOutgoingMessageThrottleStart, System.currentTimeMillis());
/* 308 */       return new Double(this.destinationOutgoingMessageThrottleCount / runtime);
/*     */     }
/*     */ 
/* 312 */     return new Double(0.0D);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.ThrottleManagerControl
 * JD-Core Version:    0.6.0
 */
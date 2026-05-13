/*     */ package flex.management.runtime.messaging.endpoints;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.messaging.endpoints.BaseStreamingHTTPEndpoint;
/*     */ import java.util.Date;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public abstract class StreamingEndpointControl extends EndpointControl
/*     */   implements StreamingEndpointControlMBean
/*     */ {
/*     */   private int pushCount;
/*     */   private Date lastPushTimeStamp;
/*     */   private long pushStart;
/*     */ 
/*     */   public StreamingEndpointControl(BaseStreamingHTTPEndpoint endpoint, BaseControl parent)
/*     */   {
/*  47 */     super(endpoint, parent);
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*  52 */     super.onRegistrationComplete();
/*     */ 
/*  54 */     String name = getObjectName().getCanonicalName();
/*  55 */     String[] generalPollables = { "LastPushTimestamp", "PushCount", "PushFrequency", "StreamingClientsCount" };
/*     */ 
/*  57 */     getRegistrar().registerObjects(101, name, generalPollables);
/*  58 */     getRegistrar().registerObject(100, name, "MaxStreamingClients");
/*     */   }
/*     */ 
/*     */   public Integer getMaxStreamingClients()
/*     */   {
/*  67 */     int maxStreamingClientsCount = ((BaseStreamingHTTPEndpoint)this.endpoint).getMaxStreamingClients();
/*  68 */     return new Integer(maxStreamingClientsCount);
/*     */   }
/*     */ 
/*     */   public Integer getPushCount()
/*     */   {
/*  77 */     return new Integer(this.pushCount);
/*     */   }
/*     */ 
/*     */   public void resetPushCount()
/*     */   {
/*  86 */     this.pushStart = System.currentTimeMillis();
/*  87 */     this.pushCount = 0;
/*  88 */     this.lastPushTimeStamp = null;
/*     */   }
/*     */ 
/*     */   public void incrementPushCount()
/*     */   {
/*  96 */     this.pushCount += 1;
/*  97 */     this.lastPushTimeStamp = new Date();
/*     */   }
/*     */ 
/*     */   public Date getLastPushTimestamp()
/*     */   {
/* 106 */     return this.lastPushTimeStamp;
/*     */   }
/*     */ 
/*     */   public Double getPushFrequency()
/*     */   {
/* 115 */     if (this.pushCount > 0)
/*     */     {
/* 117 */       double runtime = differenceInMinutes(this.pushStart, System.currentTimeMillis());
/* 118 */       return new Double(this.pushCount / runtime);
/*     */     }
/*     */ 
/* 122 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public Integer getStreamingClientsCount()
/*     */   {
/* 132 */     int streamingClientsCount = ((BaseStreamingHTTPEndpoint)this.endpoint).getStreamingClientsCount();
/* 133 */     return new Integer(streamingClientsCount);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.StreamingEndpointControl
 * JD-Core Version:    0.6.0
 */
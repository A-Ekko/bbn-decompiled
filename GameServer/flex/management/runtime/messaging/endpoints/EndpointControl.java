/*     */ package flex.management.runtime.messaging.endpoints;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.management.runtime.messaging.MessageBrokerControl;
/*     */ import flex.messaging.config.SecurityConstraint;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public abstract class EndpointControl extends BaseControl
/*     */   implements EndpointControlMBean
/*     */ {
/*     */   protected Endpoint endpoint;
/*     */   private int serviceMessageCount;
/*     */   private Date lastServiceMessageTimestamp;
/*     */   private long serviceMessageStart;
/*  42 */   private long bytesDeserialized = 0L;
/*  43 */   private long bytesSerialized = 0L;
/*     */ 
/*     */   public EndpointControl(Endpoint endpoint, BaseControl parent)
/*     */   {
/*  54 */     super(parent);
/*  55 */     this.endpoint = endpoint;
/*  56 */     this.serviceMessageStart = System.currentTimeMillis();
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*  62 */     String name = getObjectName().getCanonicalName();
/*  63 */     String[] generalNames = { "SecurityConstraint" };
/*  64 */     String[] generalPollables = { "ServiceMessageCount", "LastServiceMessageTimestamp", "ServiceMessageFrequency" };
/*  65 */     String[] pollableGraphByInterval = { "BytesDeserialized", "BytesSerialized" };
/*     */ 
/*  67 */     getRegistrar().registerObjects(100, name, generalNames);
/*     */ 
/*  69 */     getRegistrar().registerObjects(101, name, generalPollables);
/*     */ 
/*  71 */     getRegistrar().registerObjects(new int[] { 50, 101 }, name, pollableGraphByInterval);
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  81 */     return this.endpoint.getId();
/*     */   }
/*     */ 
/*     */   public Boolean isRunning()
/*     */   {
/*  90 */     return Boolean.valueOf(this.endpoint.isStarted());
/*     */   }
/*     */ 
/*     */   public Date getStartTimestamp()
/*     */   {
/*  99 */     return this.startTimestamp;
/*     */   }
/*     */ 
/*     */   public Integer getServiceMessageCount()
/*     */   {
/* 108 */     return new Integer(this.serviceMessageCount);
/*     */   }
/*     */ 
/*     */   public void resetServiceMessageCount()
/*     */   {
/* 117 */     this.serviceMessageStart = System.currentTimeMillis();
/* 118 */     this.serviceMessageCount = 0;
/* 119 */     this.lastServiceMessageTimestamp = null;
/*     */   }
/*     */ 
/*     */   public void incrementServiceMessageCount()
/*     */   {
/* 127 */     this.serviceMessageCount += 1;
/* 128 */     this.lastServiceMessageTimestamp = new Date();
/*     */   }
/*     */ 
/*     */   public Date getLastServiceMessageTimestamp()
/*     */   {
/* 137 */     return this.lastServiceMessageTimestamp;
/*     */   }
/*     */ 
/*     */   public Double getServiceMessageFrequency()
/*     */   {
/* 146 */     if (this.serviceMessageCount > 0)
/*     */     {
/* 148 */       double runtime = differenceInMinutes(this.serviceMessageStart, System.currentTimeMillis());
/* 149 */       return new Double(this.serviceMessageCount / runtime);
/*     */     }
/*     */ 
/* 153 */     return new Double(0.0D);
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */     throws Exception
/*     */   {
/* 163 */     MessageBrokerControl parent = (MessageBrokerControl)getParentControl();
/* 164 */     parent.removeEndpoint(getObjectName());
/*     */   }
/*     */ 
/*     */   public String getURI()
/*     */   {
/* 169 */     return this.endpoint.getUrl();
/*     */   }
/*     */ 
/*     */   public String getSecurityConstraint()
/*     */   {
/* 174 */     return getSecurityConstraintOf(this.endpoint);
/*     */   }
/*     */ 
/*     */   public static String getSecurityConstraintOf(Endpoint endpoint)
/*     */   {
/* 179 */     String result = "None";
/*     */ 
/* 181 */     SecurityConstraint constraint = endpoint.getSecurityConstraint();
/* 182 */     if (constraint != null)
/*     */     {
/* 184 */       String authMethod = constraint.getMethod();
/* 185 */       if (authMethod != null)
/*     */       {
/* 187 */         StringBuffer buffer = new StringBuffer();
/* 188 */         buffer.append(authMethod);
/*     */ 
/* 190 */         List roles = constraint.getRoles();
/* 191 */         if ((roles != null) && (!roles.isEmpty()))
/*     */         {
/* 193 */           buffer.append(':');
/* 194 */           for (int i = 0; i < roles.size(); i++)
/*     */           {
/* 196 */             if (i > 0)
/*     */             {
/* 198 */               buffer.append(',');
/*     */             }
/* 200 */             buffer.append(' ');
/* 201 */             buffer.append(roles.get(i));
/*     */           }
/*     */         }
/* 204 */         result = buffer.toString();
/*     */       }
/*     */     }
/* 207 */     return result;
/*     */   }
/*     */ 
/*     */   public Long getBytesDeserialized()
/*     */   {
/* 215 */     return new Long(this.bytesDeserialized);
/*     */   }
/*     */ 
/*     */   public void addToBytesDeserialized(int bytesDeserialized)
/*     */   {
/* 222 */     this.bytesDeserialized += bytesDeserialized;
/*     */   }
/*     */ 
/*     */   public Long getBytesSerialized()
/*     */   {
/* 230 */     return new Long(this.bytesSerialized);
/*     */   }
/*     */ 
/*     */   public void addToBytesSerialized(int bytesSerialized)
/*     */   {
/* 237 */     this.bytesSerialized += bytesSerialized;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.EndpointControl
 * JD-Core Version:    0.6.0
 */
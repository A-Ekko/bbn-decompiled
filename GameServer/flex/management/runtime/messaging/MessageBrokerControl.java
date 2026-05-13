/*     */ package flex.management.runtime.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.AMFEndpoint;
/*     */ import flex.messaging.endpoints.AbstractEndpoint;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.endpoints.HTTPEndpoint;
/*     */ import flex.messaging.endpoints.StreamingAMFEndpoint;
/*     */ import flex.messaging.endpoints.StreamingHTTPEndpoint;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import javax.management.ObjectName;
/*     */ 
/*     */ public class MessageBrokerControl extends BaseControl
/*     */   implements MessageBrokerControlMBean
/*     */ {
/*  48 */   private static final Object classMutex = new Object();
/*     */   private static final String TYPE = "MessageBroker";
/*  50 */   private static int instanceCount = 0;
/*     */   private String id;
/*     */   private MessageBroker broker;
/*     */   private List endpointNames;
/*     */   private List amfEndpoints;
/*     */   private List httpEndpoints;
/*     */   private List enterpriseEndpoints;
/*     */   private List streamingAmfEndpoints;
/*     */   private List streamingHttpEndpoints;
/*     */   private List services;
/*     */   private Integer flexSessionCount;
/*     */   private Integer maxFlexSessionsInCurrentHour;
/*     */ 
/*     */   public MessageBrokerControl(MessageBroker broker)
/*     */   {
/*  71 */     super(null);
/*  72 */     this.broker = broker;
/*  73 */     this.endpointNames = new ArrayList();
/*  74 */     this.amfEndpoints = new ArrayList();
/*  75 */     this.httpEndpoints = new ArrayList();
/*  76 */     this.enterpriseEndpoints = new ArrayList();
/*  77 */     this.streamingAmfEndpoints = new ArrayList();
/*  78 */     this.streamingHttpEndpoints = new ArrayList();
/*  79 */     this.services = new ArrayList();
/*  80 */     synchronized (classMutex)
/*     */     {
/*  82 */       this.id = ("MessageBroker" + ++instanceCount);
/*     */     }
/*     */ 
/*  85 */     setRegistrar(new AdminConsoleDisplayRegistrar(this));
/*     */   }
/*     */ 
/*     */   protected void onRegistrationComplete()
/*     */   {
/*  90 */     String name = getObjectName().getCanonicalName();
/*  91 */     getRegistrar().registerObject(2, name, "FlexSessionCount");
/*  92 */     getRegistrar().registerObjects(new int[] { 2, 50 }, name, new String[] { "AMFThroughput", "HTTPThroughput", "EnterpriseThroughput" });
/*     */ 
/*  95 */     getRegistrar().registerObject(1, name, "MaxFlexSessionsInCurrentHour");
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/* 104 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/* 113 */     return "MessageBroker";
/*     */   }
/*     */ 
/*     */   public Boolean isRunning()
/*     */   {
/* 122 */     return Boolean.valueOf(this.broker.isStarted());
/*     */   }
/*     */ 
/*     */   public Date getStartTimestamp()
/*     */   {
/* 131 */     return this.startTimestamp;
/*     */   }
/*     */ 
/*     */   public ObjectName[] getEndpoints()
/*     */     throws IOException
/*     */   {
/* 140 */     int size = this.endpointNames.size();
/* 141 */     ObjectName[] endpointNameObjects = new ObjectName[size];
/* 142 */     for (int i = 0; i < size; i++)
/*     */     {
/* 144 */       endpointNameObjects[i] = ((ObjectName)this.endpointNames.get(i));
/*     */     }
/* 146 */     return endpointNameObjects;
/*     */   }
/*     */ 
/*     */   public void addEndpoint(Endpoint value)
/*     */   {
/* 156 */     if ((value instanceof AMFEndpoint))
/* 157 */       this.amfEndpoints.add(value);
/* 158 */     else if ((value instanceof HTTPEndpoint))
/* 159 */       this.httpEndpoints.add(value);
/* 160 */     else if ((value instanceof StreamingAMFEndpoint))
/* 161 */       this.streamingAmfEndpoints.add(value);
/* 162 */     else if ((value instanceof StreamingHTTPEndpoint))
/* 163 */       this.streamingHttpEndpoints.add(value);
/*     */     else {
/* 165 */       this.enterpriseEndpoints.add(value);
/*     */     }
/* 167 */     this.endpointNames.add(value.getControl().getObjectName());
/*     */   }
/*     */ 
/*     */   public void removeEndpoint(ObjectName value)
/*     */   {
/* 177 */     this.endpointNames.remove(value);
/*     */   }
/*     */ 
/*     */   public ObjectName[] getServices()
/*     */     throws IOException
/*     */   {
/* 186 */     int size = this.services.size();
/* 187 */     ObjectName[] serviceNames = new ObjectName[size];
/* 188 */     for (int i = 0; i < size; i++)
/*     */     {
/* 190 */       serviceNames[i] = ((ObjectName)this.services.get(i));
/*     */     }
/* 192 */     return serviceNames;
/*     */   }
/*     */ 
/*     */   public void addService(ObjectName value)
/*     */   {
/* 202 */     this.services.add(value);
/*     */   }
/*     */ 
/*     */   public void removeService(ObjectName value)
/*     */   {
/* 212 */     this.services.remove(value);
/*     */   }
/*     */ 
/*     */   public Integer getFlexSessionCount()
/*     */   {
/* 221 */     return this.flexSessionCount;
/*     */   }
/*     */ 
/*     */   public void setFlexSessionCount(int connectionCount)
/*     */   {
/* 231 */     this.flexSessionCount = new Integer(connectionCount);
/*     */   }
/*     */ 
/*     */   public Integer getMaxFlexSessionsInCurrentHour()
/*     */   {
/* 240 */     return this.maxFlexSessionsInCurrentHour;
/*     */   }
/*     */ 
/*     */   public void setMaxFlexSessionsInCurrentHour(int currentConnectionCountMax)
/*     */   {
/* 245 */     this.maxFlexSessionsInCurrentHour = new Integer(currentConnectionCountMax);
/*     */   }
/*     */ 
/*     */   public Integer getEnterpriseConnectionCount()
/*     */     throws IOException
/*     */   {
/* 253 */     int connections = 0;
/*     */ 
/* 260 */     return new Integer(connections);
/*     */   }
/*     */ 
/*     */   public Long getAMFThroughput()
/*     */     throws IOException
/*     */   {
/* 268 */     return new Long(calculateEndpointThroughput(this.amfEndpoints));
/*     */   }
/*     */ 
/*     */   public Long getHTTPThroughput()
/*     */     throws IOException
/*     */   {
/* 276 */     return new Long(calculateEndpointThroughput(this.httpEndpoints));
/*     */   }
/*     */ 
/*     */   public Long getEnterpriseThroughput()
/*     */     throws IOException
/*     */   {
/* 284 */     return new Long(calculateEndpointThroughput(this.enterpriseEndpoints));
/*     */   }
/*     */ 
/*     */   public Long getStreamingAMFThroughput()
/*     */     throws IOException
/*     */   {
/* 292 */     return new Long(calculateEndpointThroughput(this.streamingAmfEndpoints));
/*     */   }
/*     */ 
/*     */   public Long getStreamingHTTPThroughput()
/*     */     throws IOException
/*     */   {
/* 300 */     return new Long(calculateEndpointThroughput(this.streamingHttpEndpoints));
/*     */   }
/*     */ 
/*     */   private long calculateEndpointThroughput(List endpoints)
/*     */   {
/* 305 */     long throughput = 0L;
/*     */ 
/* 307 */     for (int i = 0; i < endpoints.size(); i++)
/*     */     {
/* 310 */       if ((endpoints.get(i) instanceof AbstractEndpoint)) {
/* 311 */         throughput += ((AbstractEndpoint)endpoints.get(i)).getThroughput();
/*     */       }
/*     */     }
/* 314 */     return throughput;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.MessageBrokerControl
 * JD-Core Version:    0.6.0
 */
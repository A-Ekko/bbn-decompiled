/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import flex.management.runtime.messaging.endpoints.AMFEndpointControl;
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.amf.AMFFilter;
/*     */ import flex.messaging.endpoints.amf.BatchProcessFilter;
/*     */ import flex.messaging.endpoints.amf.LegacyFilter;
/*     */ import flex.messaging.endpoints.amf.MessageBrokerFilter;
/*     */ import flex.messaging.endpoints.amf.SerializationFilter;
/*     */ import flex.messaging.endpoints.amf.SessionFilter;
/*     */ 
/*     */ public class AMFEndpoint extends BasePollingHTTPEndpoint
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Endpoint.AMF";
/*     */ 
/*     */   public AMFEndpoint()
/*     */   {
/*  56 */     this(false);
/*     */   }
/*     */ 
/*     */   public AMFEndpoint(boolean enableManagement)
/*     */   {
/*  67 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   protected AMFFilter createFilterChain()
/*     */   {
/*  82 */     AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
/*  83 */     AMFFilter batchFilter = new BatchProcessFilter();
/*  84 */     AMFFilter sessionFilter = new SessionFilter();
/*  85 */     AMFFilter envelopeFilter = new LegacyFilter(this);
/*  86 */     AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);
/*     */ 
/*  88 */     serializationFilter.setNext(batchFilter);
/*  89 */     batchFilter.setNext(sessionFilter);
/*  90 */     sessionFilter.setNext(envelopeFilter);
/*  91 */     envelopeFilter.setNext(messageBrokerFilter);
/*     */ 
/*  93 */     return serializationFilter;
/*     */   }
/*     */ 
/*     */   protected String getResponseContentType()
/*     */   {
/* 103 */     return "application/x-amf";
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 113 */     return "Endpoint.AMF";
/*     */   }
/*     */ 
/*     */   protected String getDeserializerClassName()
/*     */   {
/* 123 */     return "flex.messaging.io.amf.AmfMessageDeserializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerClassName()
/*     */   {
/* 133 */     return "flex.messaging.io.amf.AmfMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerJava15ClassName()
/*     */   {
/* 143 */     return "flex.messaging.io.amf.Java15AmfMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected void setupEndpointControl(MessageBroker broker)
/*     */   {
/* 155 */     this.controller = new AMFEndpointControl(this, broker.getControl());
/* 156 */     this.controller.register();
/* 157 */     setControl(this.controller);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.AMFEndpoint
 * JD-Core Version:    0.6.0
 */
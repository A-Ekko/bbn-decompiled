/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.management.runtime.messaging.endpoints.HTTPEndpointControl;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.amf.AMFFilter;
/*     */ import flex.messaging.endpoints.amf.BatchProcessFilter;
/*     */ import flex.messaging.endpoints.amf.MessageBrokerFilter;
/*     */ import flex.messaging.endpoints.amf.SerializationFilter;
/*     */ import flex.messaging.endpoints.amf.SessionFilter;
/*     */ import flex.messaging.messages.Message;
/*     */ 
/*     */ public class HTTPEndpoint extends BasePollingHTTPEndpoint
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Endpoint.HTTP";
/*     */ 
/*     */   public HTTPEndpoint()
/*     */   {
/*  58 */     this(false);
/*     */   }
/*     */ 
/*     */   public HTTPEndpoint(boolean enableManagement)
/*     */   {
/*  69 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   public Message convertToSmallMessage(Message message)
/*     */   {
/*  78 */     return message;
/*     */   }
/*     */ 
/*     */   protected AMFFilter createFilterChain()
/*     */   {
/*  92 */     AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
/*  93 */     AMFFilter batchFilter = new BatchProcessFilter();
/*  94 */     AMFFilter sessionFilter = new SessionFilter();
/*  95 */     AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);
/*     */ 
/*  97 */     serializationFilter.setNext(batchFilter);
/*  98 */     batchFilter.setNext(sessionFilter);
/*  99 */     sessionFilter.setNext(messageBrokerFilter);
/*     */ 
/* 101 */     return serializationFilter;
/*     */   }
/*     */ 
/*     */   protected String getResponseContentType()
/*     */   {
/* 109 */     return "application/xml";
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 119 */     return "Endpoint.HTTP";
/*     */   }
/*     */ 
/*     */   protected String getDeserializerClassName()
/*     */   {
/* 129 */     return "flex.messaging.io.amfx.AmfxMessageDeserializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerClassName()
/*     */   {
/* 139 */     return "flex.messaging.io.amfx.AmfxMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerJava15ClassName()
/*     */   {
/* 149 */     return "flex.messaging.io.amfx.Java15AmfxMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected void setupEndpointControl(MessageBroker broker)
/*     */   {
/* 161 */     this.controller = new HTTPEndpointControl(this, broker.getControl());
/* 162 */     this.controller.register();
/* 163 */     setControl(this.controller);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.HTTPEndpoint
 * JD-Core Version:    0.6.0
 */
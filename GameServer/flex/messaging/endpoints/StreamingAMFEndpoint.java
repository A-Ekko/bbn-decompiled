/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.management.runtime.messaging.endpoints.StreamingAMFEndpointControl;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.amf.AMFFilter;
/*     */ import flex.messaging.endpoints.amf.BatchProcessFilter;
/*     */ import flex.messaging.endpoints.amf.LegacyFilter;
/*     */ import flex.messaging.endpoints.amf.MessageBrokerFilter;
/*     */ import flex.messaging.endpoints.amf.SerializationFilter;
/*     */ import flex.messaging.endpoints.amf.SessionFilter;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.io.amf.Amf3Output;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class StreamingAMFEndpoint extends BaseStreamingHTTPEndpoint
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Endpoint.StreamingAMF";
/*     */ 
/*     */   public StreamingAMFEndpoint()
/*     */   {
/*  85 */     this(false);
/*     */   }
/*     */ 
/*     */   public StreamingAMFEndpoint(boolean enableManagement)
/*     */   {
/*  96 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   protected AMFFilter createFilterChain()
/*     */   {
/* 111 */     AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
/* 112 */     AMFFilter batchFilter = new BatchProcessFilter();
/* 113 */     AMFFilter sessionFilter = new SessionFilter();
/* 114 */     AMFFilter envelopeFilter = new LegacyFilter(this);
/* 115 */     AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);
/*     */ 
/* 117 */     serializationFilter.setNext(batchFilter);
/* 118 */     batchFilter.setNext(sessionFilter);
/* 119 */     sessionFilter.setNext(envelopeFilter);
/* 120 */     envelopeFilter.setNext(messageBrokerFilter);
/*     */ 
/* 122 */     return serializationFilter;
/*     */   }
/*     */ 
/*     */   protected String getResponseContentType()
/*     */   {
/* 130 */     return "application/x-amf";
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 140 */     return "Endpoint.StreamingAMF";
/*     */   }
/*     */ 
/*     */   protected long getMessageSizeForPerformanceInfo(Message message)
/*     */   {
/* 154 */     Amf3Output amfOut = new Amf3Output(this.serializationContext);
/* 155 */     ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 156 */     DataOutputStream dataOutStream = new DataOutputStream(outStream);
/* 157 */     amfOut.setOutputStream(dataOutStream);
/*     */     try
/*     */     {
/* 160 */       amfOut.writeObject(message);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 164 */       if (Log.isDebug())
/* 165 */         this.log.debug("MPI exception while retrieving the size of the serialized message: " + e.toString());
/*     */     }
/* 167 */     return dataOutStream.size();
/*     */   }
/*     */ 
/*     */   protected String getDeserializerClassName()
/*     */   {
/* 177 */     return "flex.messaging.io.amf.AmfMessageDeserializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerClassName()
/*     */   {
/* 187 */     return "flex.messaging.io.amf.AmfMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerJava15ClassName()
/*     */   {
/* 197 */     return "flex.messaging.io.amf.Java15AmfMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected void setupEndpointControl(MessageBroker broker)
/*     */   {
/* 209 */     this.controller = new StreamingAMFEndpointControl(this, broker.getControl());
/* 210 */     this.controller.register();
/* 211 */     setControl(this.controller);
/*     */   }
/*     */ 
/*     */   protected void streamMessages(List messages, ServletOutputStream os, HttpServletResponse response)
/*     */     throws IOException
/*     */   {
/* 224 */     if ((messages == null) || (messages.isEmpty())) {
/* 225 */       return;
/*     */     }
/*     */ 
/* 228 */     TypeMarshallingContext.setTypeMarshaller(getTypeMarshaller());
/* 229 */     for (Iterator iter = messages.iterator(); iter.hasNext(); )
/*     */     {
/* 231 */       Amf3Output amfOut = new Amf3Output(this.serializationContext);
/* 232 */       ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 233 */       DataOutputStream dataOutStream = new DataOutputStream(outStream);
/* 234 */       amfOut.setOutputStream(dataOutStream);
/*     */ 
/* 236 */       Message message = (Message)iter.next();
/*     */ 
/* 239 */       if ((isRecordMessageSizes()) || (isRecordMessageTimes())) {
/* 240 */         addPerformanceInfo(message);
/*     */       }
/* 242 */       if (Log.isDebug()) {
/* 243 */         this.log.debug("Endpoint with id '" + getId() + "' is streaming message: " + message);
/*     */       }
/* 245 */       amfOut.writeObject(message);
/* 246 */       dataOutStream.flush();
/* 247 */       byte[] messageBytes = outStream.toByteArray();
/* 248 */       streamChunk(messageBytes, os, response);
/*     */ 
/* 251 */       if (isManaged())
/*     */       {
/* 253 */         ((StreamingAMFEndpointControl)this.controller).incrementPushCount();
/*     */       }
/*     */     }
/* 256 */     TypeMarshallingContext.setTypeMarshaller(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.StreamingAMFEndpoint
 * JD-Core Version:    0.6.0
 */
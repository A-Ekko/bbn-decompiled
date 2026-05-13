/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import flex.management.runtime.messaging.endpoints.EndpointControl;
/*     */ import flex.management.runtime.messaging.endpoints.StreamingHTTPEndpointControl;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.amf.AMFFilter;
/*     */ import flex.messaging.endpoints.amf.BatchProcessFilter;
/*     */ import flex.messaging.endpoints.amf.MessageBrokerFilter;
/*     */ import flex.messaging.endpoints.amf.SerializationFilter;
/*     */ import flex.messaging.endpoints.amf.SessionFilter;
/*     */ import flex.messaging.io.TypeMarshallingContext;
/*     */ import flex.messaging.io.amfx.AmfxOutput;
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
/*     */ public class StreamingHTTPEndpoint extends BaseStreamingHTTPEndpoint
/*     */ {
/*     */   public static final String LOG_CATEGORY = "Endpoint.StreamingHTTP";
/*     */ 
/*     */   public StreamingHTTPEndpoint()
/*     */   {
/*  78 */     this(false);
/*     */   }
/*     */ 
/*     */   public StreamingHTTPEndpoint(boolean enableManagement)
/*     */   {
/*  89 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   protected AMFFilter createFilterChain()
/*     */   {
/* 103 */     AMFFilter serializationFilter = new SerializationFilter(getLogCategory());
/* 104 */     AMFFilter batchFilter = new BatchProcessFilter();
/* 105 */     AMFFilter sessionFilter = new SessionFilter();
/* 106 */     AMFFilter messageBrokerFilter = new MessageBrokerFilter(this);
/*     */ 
/* 108 */     serializationFilter.setNext(batchFilter);
/* 109 */     batchFilter.setNext(sessionFilter);
/* 110 */     sessionFilter.setNext(messageBrokerFilter);
/*     */ 
/* 112 */     return serializationFilter;
/*     */   }
/*     */ 
/*     */   protected String getResponseContentType()
/*     */   {
/* 120 */     return "application/xml";
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 130 */     return "Endpoint.StreamingHTTP";
/*     */   }
/*     */ 
/*     */   protected long getMessageSizeForPerformanceInfo(Message message)
/*     */   {
/* 144 */     AmfxOutput amfxOut = new AmfxOutput(this.serializationContext);
/* 145 */     ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 146 */     DataOutputStream dataOutStream = new DataOutputStream(outStream);
/* 147 */     amfxOut.setOutputStream(dataOutStream);
/*     */     try
/*     */     {
/* 150 */       amfxOut.writeObject(message);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 154 */       if (Log.isDebug())
/* 155 */         this.log.debug("MPI exception while retrieving the size of the serialized message: " + e.toString());
/*     */     }
/* 157 */     return dataOutStream.size();
/*     */   }
/*     */ 
/*     */   protected String getDeserializerClassName()
/*     */   {
/* 167 */     return "flex.messaging.io.amfx.AmfxMessageDeserializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerClassName()
/*     */   {
/* 177 */     return "flex.messaging.io.amfx.AmfxMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected String getSerializerJava15ClassName()
/*     */   {
/* 187 */     return "flex.messaging.io.amfx.Java15AmfxMessageSerializer";
/*     */   }
/*     */ 
/*     */   protected void setupEndpointControl(MessageBroker broker)
/*     */   {
/* 199 */     this.controller = new StreamingHTTPEndpointControl(this, broker.getControl());
/* 200 */     this.controller.register();
/* 201 */     setControl(this.controller);
/*     */   }
/*     */ 
/*     */   protected void streamMessages(List messages, ServletOutputStream os, HttpServletResponse response)
/*     */     throws IOException
/*     */   {
/* 214 */     if ((messages == null) || (messages.isEmpty())) {
/* 215 */       return;
/*     */     }
/*     */ 
/* 218 */     TypeMarshallingContext.setTypeMarshaller(getTypeMarshaller());
/* 219 */     for (Iterator iter = messages.iterator(); iter.hasNext(); )
/*     */     {
/* 221 */       AmfxOutput amfxOut = new AmfxOutput(this.serializationContext);
/* 222 */       ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 223 */       DataOutputStream dataOutStream = new DataOutputStream(outStream);
/* 224 */       amfxOut.setOutputStream(dataOutStream);
/*     */ 
/* 226 */       Message message = (Message)iter.next();
/*     */ 
/* 229 */       if ((isRecordMessageSizes()) || (isRecordMessageTimes())) {
/* 230 */         addPerformanceInfo(message);
/*     */       }
/* 232 */       if (Log.isDebug()) {
/* 233 */         this.log.debug("Endpoint with id '" + getId() + "' is streaming message: " + message);
/*     */       }
/* 235 */       amfxOut.writeObject(message);
/* 236 */       dataOutStream.flush();
/* 237 */       byte[] messageBytes = outStream.toByteArray();
/* 238 */       streamChunk(messageBytes, os, response);
/*     */ 
/* 241 */       if (isManaged())
/*     */       {
/* 243 */         ((StreamingHTTPEndpointControl)this.controller).incrementPushCount();
/*     */       }
/*     */     }
/* 246 */     TypeMarshallingContext.setTypeMarshaller(null);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.StreamingHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
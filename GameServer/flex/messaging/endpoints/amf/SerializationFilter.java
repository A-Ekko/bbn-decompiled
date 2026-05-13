/*     */ package flex.messaging.endpoints.amf;
/*     */ 
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.io.MessageDeserializer;
/*     */ import flex.messaging.io.MessageSerializer;
/*     */ import flex.messaging.io.SerializationContext;
/*     */ import flex.messaging.io.SerializationException;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.io.amf.ActionContext;
/*     */ import flex.messaging.io.amf.ActionMessage;
/*     */ import flex.messaging.io.amf.AmfTrace;
/*     */ import flex.messaging.io.amf.MessageBody;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.ErrorMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.messages.MessagePerformanceInfo;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.List;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ 
/*     */ public class SerializationFilter extends AMFFilter
/*     */ {
/*     */   private static final int UNHANDLED_ERROR = 10306;
/*     */   private static final int REQUEST_ERROR = 10307;
/*     */   private static final int RESPONSE_ERROR = 10308;
/*     */   private boolean isDebug;
/*     */   private Logger logger;
/*     */ 
/*     */   public SerializationFilter(String logCategory)
/*     */   {
/*  68 */     this.isDebug = Log.isDebug();
/*  69 */     if (logCategory == null)
/*  70 */       logCategory = "Endpoint.General";
/*  71 */     this.logger = Log.getLogger(logCategory);
/*     */   }
/*     */ 
/*     */   public void invoke(ActionContext context) throws IOException
/*     */   {
/*  76 */     boolean success = false;
/*     */ 
/*  79 */     AmfTrace debugTrace = this.isDebug ? new AmfTrace() : null;
/*     */ 
/*  82 */     context.setResponseMessage(new ActionMessage());
/*  83 */     SerializationContext sc = SerializationContext.getSerializationContext();
/*     */     try
/*     */     {
/*  88 */       MessageDeserializer deserializer = sc.newMessageDeserializer();
/*     */ 
/*  91 */       InputStream in = FlexContext.getHttpRequest().getInputStream();
/*  92 */       deserializer.initialize(sc, in, debugTrace);
/*     */ 
/*  95 */       int reqLen = FlexContext.getHttpRequest().getContentLength();
/*  96 */       context.setDeserializedBytes(reqLen);
/*     */ 
/*  99 */       if (context.isMPIenabled())
/*     */       {
/* 101 */         MessagePerformanceInfo mpi = new MessagePerformanceInfo();
/* 102 */         mpi.recordMessageSizes = context.isRecordMessageSizes();
/* 103 */         mpi.recordMessageTimes = context.isRecordMessageTimes();
/* 104 */         if (context.isRecordMessageTimes())
/* 105 */           mpi.receiveTime = System.currentTimeMillis();
/* 106 */         if (context.isRecordMessageSizes()) {
/* 107 */           mpi.messageSize = reqLen;
/*     */         }
/* 109 */         context.setMPII(mpi);
/*     */       }
/*     */ 
/* 112 */       ActionMessage m = new ActionMessage();
/* 113 */       context.setRequestMessage(m);
/* 114 */       deserializer.readMessage(m, context);
/* 115 */       success = true;
/*     */     }
/*     */     catch (EOFException respMsg)
/*     */     {
/*     */       ActionMessage respMsg;
/* 119 */       context.setStatus(2);
/*     */     }
/*     */     catch (IOException exc)
/*     */     {
/*     */       ActionMessage respMsg;
/* 123 */       if (this.isDebug) {
/* 124 */         this.logger.debug("IOException reading message - client closed socket before sending the message?");
/*     */       }
/* 126 */       throw exc;
/*     */     }
/*     */     catch (Throwable respMsg)
/*     */     {
/* 130 */       deserializationError(context, t);
/*     */     }
/*     */     finally
/*     */     {
/*     */       ActionMessage respMsg;
/* 135 */       ActionMessage respMsg = context.getResponseMessage();
/* 136 */       respMsg.setVersion(context.getVersion());
/*     */ 
/* 138 */       if (this.isDebug) {
/* 139 */         this.logger.debug(debugTrace.toString());
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 144 */       if (success)
/*     */       {
/* 146 */         this.next.invoke(context);
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 151 */       unhandledError(context, t);
/*     */     }
/*     */     finally
/*     */     {
/* 156 */       if (context.getStatus() != 2)
/*     */       {
/* 158 */         ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
/* 159 */         ActionMessage respMesg = context.getResponseMessage();
/*     */ 
/* 161 */         if (this.isDebug)
/*     */         {
/* 163 */           debugTrace = new AmfTrace();
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 169 */           long serializationOverhead = 0L;
/* 170 */           if (context.isRecordMessageTimes())
/*     */           {
/* 173 */             context.getMPIO().sendTime = System.currentTimeMillis();
/* 174 */             if (context.isRecordMessageSizes())
/* 175 */               serializationOverhead = System.currentTimeMillis();
/*     */           }
/* 177 */           MessageSerializer serializer = sc.newMessageSerializer();
/* 178 */           serializer.initialize(sc, outBuffer, debugTrace);
/* 179 */           serializer.writeMessage(respMesg);
/*     */ 
/* 182 */           context.setSerializedBytes(outBuffer.size());
/*     */ 
/* 185 */           if (context.isRecordMessageSizes())
/*     */           {
/*     */             try
/*     */             {
/* 189 */               context.getMPIO().messageSize = outBuffer.size();
/*     */ 
/* 192 */               if (context.isRecordMessageTimes())
/*     */               {
/* 194 */                 serializationOverhead = System.currentTimeMillis() - serializationOverhead;
/* 195 */                 context.getMPIO().addToOverhead(serializationOverhead);
/* 196 */                 context.getMPIO().sendTime = System.currentTimeMillis();
/*     */               }
/*     */ 
/* 200 */               outBuffer = new ByteArrayOutputStream();
/* 201 */               respMesg = context.getResponseMessage();
/* 202 */               serializer = sc.newMessageSerializer();
/* 203 */               serializer.initialize(sc, outBuffer, debugTrace);
/* 204 */               serializer.writeMessage(respMesg);
/*     */             }
/*     */             catch (Exception e)
/*     */             {
/* 208 */               if (this.isDebug)
/* 209 */                 this.logger.debug("MPI set up error: " + e.toString());
/*     */             }
/*     */           }
/* 212 */           context.setResponseOutput(outBuffer);
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/* 216 */           serializationError(context, e);
/*     */         }
/*     */         finally
/*     */         {
/* 220 */           if (this.isDebug)
/* 221 */             this.logger.debug(debugTrace.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void deserializationError(ActionContext context, Throwable t)
/*     */   {
/* 232 */     context.setStatus(1);
/*     */ 
/* 235 */     MessageBody responseBody = new MessageBody();
/* 236 */     if (context.getMessageNumber() < context.getRequestMessage().getBodyCount())
/*     */     {
/* 238 */       responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());
/*     */     }
/*     */ 
/* 242 */     if (context.getVersion() == 0)
/*     */     {
/* 244 */       context.setVersion(3);
/*     */     }
/*     */ 
/* 248 */     context.getResponseMessage().addBody(responseBody);
/*     */     String message;
/*     */     MessageException methodResult;
/*     */     String message;
/* 253 */     if ((t instanceof MessageException))
/*     */     {
/* 255 */       MessageException methodResult = (MessageException)t;
/* 256 */       message = methodResult.getMessage();
/*     */     }
/*     */     else
/*     */     {
/* 261 */       methodResult = new SerializationException();
/* 262 */       methodResult.setMessage(10307);
/* 263 */       methodResult.setRootCause(t);
/* 264 */       message = methodResult.getMessage();
/*     */     }
/* 266 */     responseBody.setData(methodResult.createErrorMessage());
/* 267 */     responseBody.setReplyMethod("/onStatus");
/*     */ 
/* 269 */     if (Log.isError())
/* 270 */       this.logger.error(message + StringUtils.NEWLINE + ExceptionUtil.toString(t));
/*     */   }
/*     */ 
/*     */   private void unhandledError(ActionContext context, Throwable t)
/*     */   {
/* 281 */     ActionMessage responseMessage = new ActionMessage();
/* 282 */     context.setResponseMessage(responseMessage);
/*     */ 
/* 284 */     MessageBody responseBody = new MessageBody();
/* 285 */     responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());
/*     */ 
/* 287 */     context.getResponseMessage().addBody(responseBody);
/*     */     MessageException methodResult;
/*     */     MessageException methodResult;
/* 291 */     if ((t instanceof MessageException))
/*     */     {
/* 293 */       methodResult = (MessageException)t;
/*     */     }
/*     */     else
/*     */     {
/* 298 */       methodResult = new SerializationException();
/* 299 */       methodResult.setMessage(10306);
/* 300 */       methodResult.setRootCause(t);
/*     */     }
/*     */ 
/* 303 */     responseBody.setData(methodResult);
/* 304 */     responseBody.setReplyMethod("/onStatus");
/*     */ 
/* 306 */     this.logger.info(t.getMessage());
/*     */   }
/*     */ 
/*     */   private void serializationError(ActionContext context, Throwable t)
/*     */   {
/* 317 */     ActionMessage responseMessage = new ActionMessage();
/* 318 */     context.setResponseMessage(responseMessage);
/*     */ 
/* 320 */     int bodyCount = context.getRequestMessage().getBodyCount();
/* 321 */     for (context.setMessageNumber(0); context.getMessageNumber() < bodyCount; context.incrementMessageNumber())
/*     */     {
/* 323 */       MessageBody responseBody = new MessageBody();
/* 324 */       responseBody.setTargetURI(context.getRequestMessageBody().getResponseURI());
/* 325 */       context.getResponseMessage().addBody(responseBody);
/*     */       Object methodResult;
/*     */       Object methodResult;
/* 329 */       if ((t instanceof MessageException))
/*     */       {
/* 331 */         methodResult = ((MessageException)t).createErrorMessage();
/*     */       }
/*     */       else
/*     */       {
/* 335 */         String message = "An error occurred while serializing server response(s).";
/* 336 */         if (t.getMessage() != null)
/*     */         {
/* 338 */           message = t.getMessage();
/* 339 */           if (message == null) {
/* 340 */             message = t.toString();
/*     */           }
/*     */         }
/* 343 */         methodResult = new MessageException(message, t).createErrorMessage();
/*     */       }
/*     */ 
/* 346 */       if (context.isLegacy())
/*     */       {
/* 348 */         if ((methodResult instanceof ErrorMessage))
/*     */         {
/* 350 */           ErrorMessage error = (ErrorMessage)methodResult;
/* 351 */           ASObject aso = new ASObject();
/* 352 */           aso.put("message", error.faultString);
/* 353 */           aso.put("code", error.faultCode);
/* 354 */           aso.put("details", error.faultDetail);
/* 355 */           aso.put("rootCause", error.rootCause);
/* 356 */           methodResult = aso;
/*     */         }
/* 358 */         else if ((methodResult instanceof Message))
/*     */         {
/* 360 */           methodResult = ((Message)methodResult).getBody();
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 365 */         Object data = context.getRequestMessageBody().getData();
/* 366 */         if ((data instanceof List))
/*     */         {
/* 368 */           data = ((List)data).get(0);
/*     */         }
/* 370 */         else if (data.getClass().isArray())
/*     */         {
/* 372 */           data = Array.get(data, 0);
/*     */         }
/*     */ 
/* 376 */         if ((data instanceof Message))
/*     */         {
/* 378 */           Message inMessage = (Message)data;
/* 379 */           if (inMessage.getClientId() != null)
/*     */           {
/* 381 */             ((ErrorMessage)methodResult).setClientId(inMessage.getClientId().toString());
/*     */           }
/* 383 */           if (inMessage.getMessageId() != null)
/*     */           {
/* 385 */             ((ErrorMessage)methodResult).setCorrelationId(inMessage.getMessageId());
/* 386 */             ((ErrorMessage)methodResult).setDestination(inMessage.getDestination());
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 391 */       responseBody.setData(methodResult);
/* 392 */       responseBody.setReplyMethod("/onStatus");
/*     */     }
/*     */ 
/* 395 */     if (Log.isError()) {
/* 396 */       this.logger.error("Exception occurred during serialization: " + ExceptionUtil.toString(t));
/*     */     }
/*     */ 
/* 399 */     SerializationContext sc = SerializationContext.getSerializationContext();
/* 400 */     MessageSerializer serializer = sc.newMessageSerializer();
/* 401 */     ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
/* 402 */     AmfTrace debugTrace = this.isDebug ? new AmfTrace() : null;
/* 403 */     serializer.initialize(sc, outBuffer, debugTrace);
/*     */     try
/*     */     {
/* 407 */       serializer.writeMessage(context.getResponseMessage());
/* 408 */       context.setResponseOutput(outBuffer);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 413 */       MessageException ex = new MessageException();
/* 414 */       ex.setMessage(10308);
/* 415 */       ex.setRootCause(e);
/* 416 */       throw ex;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.SerializationFilter
 * JD-Core Version:    0.6.0
 */
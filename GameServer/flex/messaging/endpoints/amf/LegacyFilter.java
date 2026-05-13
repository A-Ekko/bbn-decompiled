/*     */ package flex.messaging.endpoints.amf;
/*     */ 
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.endpoints.BaseHTTPEndpoint;
/*     */ import flex.messaging.io.amf.ASObject;
/*     */ import flex.messaging.io.amf.ActionContext;
/*     */ import flex.messaging.io.amf.ActionMessage;
/*     */ import flex.messaging.io.amf.MessageBody;
/*     */ import flex.messaging.io.amf.MessageHeader;
/*     */ import flex.messaging.messages.ErrorMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.messages.RemotingMessage;
/*     */ import flex.messaging.security.LoginManager;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LegacyFilter extends AMFFilter
/*     */ {
/*     */   public static final String LEGACY_ENVELOPE_FLAG_KEY = "_flag";
/*     */   public static final String LEGACY_ENVELOPE_FLAG_VALUE = "Envelope";
/*     */   public static final String LEGACY_SECURITY_HEADER_NAME = "Credentials";
/*     */   public static final String LEGACY_SECURITY_PRINCIPAL = "userid";
/*     */   public static final String LEGACY_SECURITY_CREDENTIALS = "password";
/*     */   private BaseHTTPEndpoint endpoint;
/*     */ 
/*     */   public LegacyFilter(BaseHTTPEndpoint endpoint)
/*     */   {
/*  74 */     this.endpoint = endpoint;
/*     */   }
/*     */ 
/*     */   public void invoke(ActionContext context) throws IOException
/*     */   {
/*  79 */     MessageBody requestBody = context.getRequestMessageBody();
/*  80 */     context.setLegacy(true);
/*     */ 
/*  83 */     Object data = requestBody.getData();
/*  84 */     List newParams = null;
/*     */ 
/*  87 */     if (data != null)
/*     */     {
/*  89 */       if (data.getClass().isArray())
/*     */       {
/*  91 */         int paramLength = Array.getLength(data);
/*  92 */         if (paramLength == 1)
/*     */         {
/*  94 */           Object obj = Array.get(data, 0);
/*  95 */           if ((obj != null) && ((obj instanceof Message)))
/*     */           {
/*  97 */             context.setLegacy(false);
/*  98 */             newParams = new ArrayList();
/*  99 */             newParams.add(obj);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 104 */         if (newParams == null)
/*     */         {
/* 106 */           newParams = new ArrayList();
/* 107 */           for (int i = 0; i < paramLength; i++)
/*     */           {
/*     */             try
/*     */             {
/* 111 */               newParams.add(Array.get(data, i));
/*     */             }
/*     */             catch (Throwable t)
/*     */             {
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 119 */       else if ((data instanceof List))
/*     */       {
/* 121 */         List paramList = (List)data;
/* 122 */         if (paramList.size() == 1)
/*     */         {
/* 124 */           Object obj = paramList.get(0);
/* 125 */           if ((obj != null) && ((obj instanceof Message)))
/*     */           {
/* 127 */             context.setLegacy(false);
/* 128 */             newParams = new ArrayList();
/* 129 */             newParams.add(obj);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 134 */         if (newParams == null)
/*     */         {
/* 136 */           newParams = (List)data;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 143 */     if (newParams == null)
/*     */     {
/* 145 */       newParams = new ArrayList();
/* 146 */       newParams.add(data);
/*     */     }
/*     */ 
/* 150 */     if (context.isLegacy())
/*     */     {
/* 152 */       newParams = legacyRequest(context, newParams);
/*     */     }
/*     */ 
/* 155 */     requestBody.setData(newParams);
/*     */ 
/* 158 */     this.next.invoke(context);
/*     */ 
/* 161 */     if (context.isLegacy())
/*     */     {
/* 163 */       MessageBody responseBody = context.getResponseMessageBody();
/* 164 */       Object response = responseBody.getData();
/*     */ 
/* 166 */       if ((response instanceof ErrorMessage))
/*     */       {
/* 168 */         ErrorMessage error = (ErrorMessage)response;
/* 169 */         ASObject aso = new ASObject();
/* 170 */         aso.put("message", error.faultString);
/* 171 */         aso.put("code", error.faultCode);
/* 172 */         aso.put("details", error.faultDetail);
/* 173 */         aso.put("rootCause", error.rootCause);
/* 174 */         response = aso;
/*     */       }
/* 176 */       else if ((response instanceof Message))
/*     */       {
/* 178 */         response = ((Message)response).getBody();
/*     */       }
/* 180 */       responseBody.setData(response);
/*     */     }
/*     */   }
/*     */ 
/*     */   private List legacyRequest(ActionContext context, List oldParams)
/*     */   {
/* 186 */     List newParams = new ArrayList(1);
/* 187 */     Map headerMap = new HashMap();
/* 188 */     Object body = oldParams;
/* 189 */     Message message = null;
/* 190 */     MessageBody requestBody = context.getRequestMessageBody();
/*     */ 
/* 193 */     List packetHeaders = context.getRequestMessage().getHeaders();
/* 194 */     packetCredentials(packetHeaders, headerMap);
/*     */ 
/* 198 */     if (oldParams.size() == 1)
/*     */     {
/* 200 */       Object obj = oldParams.get(0);
/*     */ 
/* 202 */       if ((obj != null) && ((obj instanceof ASObject)))
/*     */       {
/* 204 */         ASObject aso = (ASObject)obj;
/*     */ 
/* 207 */         if (isEnvelope(aso))
/*     */         {
/* 209 */           body = aso.get("data");
/*     */ 
/* 212 */           Object h = aso.get("headers");
/* 213 */           if ((h != null) && ((h instanceof List)))
/*     */           {
/* 215 */             readEnvelopeHeaders((List)h, headerMap);
/* 216 */             envelopeCredentials(headerMap);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 223 */     message = createMessage(requestBody, body, headerMap);
/* 224 */     newParams.add(message);
/* 225 */     return newParams;
/*     */   }
/*     */ 
/*     */   private boolean isEnvelope(ASObject aso)
/*     */   {
/* 230 */     String flag = null;
/* 231 */     Object f = aso.get("_flag");
/* 232 */     if ((f != null) && ((f instanceof String))) {
/* 233 */       flag = (String)f;
/*     */     }
/*     */ 
/* 237 */     return (flag != null) && (flag.equalsIgnoreCase("Envelope"));
/*     */   }
/*     */ 
/*     */   private RemotingMessage createMessage(MessageBody messageBody, Object body, Map headerMap)
/*     */   {
/* 246 */     RemotingMessage remotingMessage = new RemotingMessage();
/*     */ 
/* 248 */     remotingMessage.setMessageId("");
/* 249 */     remotingMessage.setBody(body);
/* 250 */     remotingMessage.setHeaders(headerMap);
/*     */ 
/* 253 */     String targetURI = messageBody.getTargetURI();
/*     */ 
/* 255 */     int dotIndex = targetURI.lastIndexOf(".");
/* 256 */     if (dotIndex > 0)
/*     */     {
/* 258 */       String destination = targetURI.substring(0, dotIndex);
/* 259 */       remotingMessage.setDestination(destination);
/*     */     }
/*     */ 
/* 262 */     if (targetURI.length() > dotIndex)
/*     */     {
/* 264 */       String operation = targetURI.substring(dotIndex + 1);
/* 265 */       remotingMessage.setOperation(operation);
/*     */     }
/*     */ 
/* 268 */     return remotingMessage;
/*     */   }
/*     */ 
/*     */   private Map readEnvelopeHeaders(List headers, Map headerMap)
/*     */   {
/* 274 */     int count = headers.size();
/*     */ 
/* 276 */     for (int i = 0; i < count; i++)
/*     */     {
/* 278 */       Object obj = headers.get(i);
/*     */ 
/* 281 */       if ((obj == null) || (!(obj instanceof List)))
/*     */         continue;
/* 283 */       List h = (List)obj;
/*     */ 
/* 285 */       Object name = null;
/*     */ 
/* 287 */       Object data = null;
/*     */ 
/* 289 */       int numFields = h.size();
/*     */ 
/* 292 */       if (numFields != 3)
/*     */         continue;
/* 294 */       name = h.get(0);
/*     */ 
/* 296 */       if ((name == null) || (!(name instanceof String))) {
/*     */         continue;
/*     */       }
/* 299 */       data = h.get(2);
/* 300 */       headerMap.put(name, data);
/*     */     }
/*     */ 
/* 306 */     return headerMap;
/*     */   }
/*     */ 
/*     */   private void envelopeCredentials(Map headers)
/*     */   {
/* 312 */     Object obj = headers.get("Credentials");
/* 313 */     if ((obj != null) && ((obj instanceof ASObject)))
/*     */     {
/* 315 */       ASObject header = (ASObject)obj;
/* 316 */       String principal = (String)header.get("userid");
/* 317 */       Object credentials = header.get("password");
/* 318 */       this.endpoint.getMessageBroker().getLoginManager().login(principal, credentials.toString());
/*     */     }
/* 320 */     headers.remove("Credentials");
/*     */   }
/*     */ 
/*     */   private void packetCredentials(List packetHeaders, Map headers)
/*     */   {
/*     */     Iterator iter;
/* 325 */     if (packetHeaders.size() > 0)
/*     */     {
/* 327 */       for (iter = packetHeaders.iterator(); iter.hasNext(); )
/*     */       {
/* 329 */         MessageHeader header = (MessageHeader)iter.next();
/* 330 */         if (header.getName().equals("Credentials"))
/*     */         {
/* 332 */           Map loginInfo = (Map)header.getData();
/* 333 */           String principal = loginInfo.get("userid").toString();
/* 334 */           Object credentials = loginInfo.get("password");
/* 335 */           this.endpoint.getMessageBroker().getLoginManager().login(principal, credentials.toString());
/* 336 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.LegacyFilter
 * JD-Core Version:    0.6.0
 */
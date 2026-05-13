/*    */ package flex.messaging.endpoints.amf;
/*    */ 
/*    */ import flex.messaging.FlexContext;
/*    */ import flex.messaging.FlexSession;
/*    */ import flex.messaging.MessageException;
/*    */ import flex.messaging.endpoints.AbstractEndpoint;
/*    */ import flex.messaging.io.amf.ActionContext;
/*    */ import flex.messaging.io.amf.ActionMessage;
/*    */ import flex.messaging.io.amf.MessageBody;
/*    */ import flex.messaging.log.Log;
/*    */ import flex.messaging.log.Logger;
/*    */ import flex.messaging.messages.CommandMessage;
/*    */ import flex.messaging.messages.ErrorMessage;
/*    */ import flex.messaging.messages.Message;
/*    */ import flex.messaging.messages.MessagePerformanceUtils;
/*    */ import flex.messaging.security.SecurityException;
/*    */ import flex.messaging.util.StringUtils;
/*    */ import flex.messaging.util.UUIDUtils;
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.List;
/*    */ 
/*    */ public class MessageBrokerFilter extends AMFFilter
/*    */ {
/*    */   private static final int UNHANDLED_ERROR = 10000;
/*    */   static final String LOG_CATEGORY = "Message.General";
/*    */   protected AbstractEndpoint endpoint;
/*    */ 
/*    */   public MessageBrokerFilter(AbstractEndpoint endpoint)
/*    */   {
/* 57 */     this.endpoint = endpoint;
/*    */   }
/*    */ 
/*    */   public void invoke(ActionContext context)
/*    */   {
/* 62 */     MessageBody request = context.getRequestMessageBody();
/* 63 */     MessageBody response = context.getResponseMessageBody();
/*    */ 
/* 65 */     Object data = request.getData();
/* 66 */     if ((data instanceof List))
/*    */     {
/* 68 */       data = ((List)data).get(0);
/*    */     }
/* 70 */     else if (data.getClass().isArray())
/*    */     {
/* 72 */       data = Array.get(data, 0);
/*    */     }
/*    */     Message inMessage;
/* 76 */     if ((data instanceof Message))
/*    */     {
/* 78 */       inMessage = (Message)data;
/*    */     }
/*    */     else
/*    */     {
/* 82 */       throw new MessageException("Request was not of type flex.messaging.messages.Message");
/*    */     }
/*    */     Message inMessage;
/* 85 */     Object outMessage = null;
/*    */ 
/* 87 */     String replyMethodName = "/onStatus";
/*    */     try
/*    */     {
/* 92 */       this.endpoint.setupFlexClient(inMessage);
/*    */ 
/* 96 */       if ((inMessage.getClientId() == null) && ((!(inMessage instanceof CommandMessage)) || (((CommandMessage)inMessage).getOperation() != 2)))
/*    */       {
/* 99 */         Object clientId = UUIDUtils.createUUID();
/* 100 */         inMessage.setClientId(clientId);
/*    */       }
/*    */ 
/* 108 */       if ((inMessage instanceof CommandMessage))
/*    */       {
/* 110 */         CommandMessage command = (CommandMessage)inMessage;
/* 111 */         if ((command.getOperation() == 2) && (context.getRequestMessage().getBodyCount() != 1)) {
/* 112 */           command.setHeader("DSSuppressPollWait", Boolean.TRUE);
/*    */         }
/*    */ 
/*    */       }
/*    */ 
/* 117 */       if (context.isMPIenabled()) {
/* 118 */         MessagePerformanceUtils.setupMPII(context, inMessage);
/*    */       }
/*    */ 
/* 121 */       outMessage = this.endpoint.serviceMessage(inMessage);
/*    */ 
/* 124 */       if ((outMessage instanceof ErrorMessage))
/*    */       {
/* 126 */         context.setStatus(1);
/* 127 */         replyMethodName = "/onStatus";
/*    */       }
/*    */       else
/*    */       {
/* 131 */         replyMethodName = "/onResult";
/*    */       }
/*    */     }
/*    */     catch (MessageException e)
/*    */     {
/* 136 */       context.setStatus(1);
/* 137 */       replyMethodName = "/onStatus";
/*    */ 
/* 139 */       outMessage = e.createErrorMessage();
/* 140 */       ((ErrorMessage)outMessage).setCorrelationId(inMessage.getMessageId());
/* 141 */       ((ErrorMessage)outMessage).setDestination(inMessage.getDestination());
/* 142 */       ((ErrorMessage)outMessage).setClientId(inMessage.getClientId());
/*    */ 
/* 144 */       if ((e instanceof SecurityException))
/*    */       {
/* 146 */         if (Log.isDebug()) {
/* 147 */           Log.getLogger("Message.General").debug("Security error for message: " + e.toString() + StringUtils.NEWLINE + "  incomingMessage: " + inMessage + StringUtils.NEWLINE + "  errorReply: " + outMessage);
/*    */         }
/*    */ 
/*    */       }
/* 152 */       else if ((e.getCode() != null) && (e.getCode().equals("Server.Processing.NotSubscribed")))
/*    */       {
/* 154 */         if (Log.isDebug()) {
/* 155 */           Log.getLogger("Message.General").debug("Client not subscribed: " + e.toString() + StringUtils.NEWLINE + "  incomingMessage: " + inMessage + StringUtils.NEWLINE + "  errorReply: " + outMessage);
/*    */         }
/*    */ 
/*    */       }
/* 160 */       else if (Log.isError()) {
/* 161 */         Log.getLogger("Message.General").error("Error handling message: " + e.toString() + StringUtils.NEWLINE + "  incomingMessage: " + inMessage + StringUtils.NEWLINE + "  errorReply: " + outMessage);
/*    */       }
/*    */ 
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 171 */       context.setStatus(1);
/* 172 */       replyMethodName = "/onStatus";
/*    */ 
/* 174 */       String lmeMessage = t.getMessage();
/* 175 */       if (lmeMessage == null) {
/* 176 */         lmeMessage = t.getClass().getName();
/*    */       }
/* 178 */       MessageException lme = new MessageException();
/* 179 */       lme.setMessage(10000, new Object[] { lmeMessage });
/*    */ 
/* 181 */       outMessage = lme.createErrorMessage();
/* 182 */       ((ErrorMessage)outMessage).setCorrelationId(inMessage.getMessageId());
/* 183 */       ((ErrorMessage)outMessage).setDestination(inMessage.getDestination());
/* 184 */       ((ErrorMessage)outMessage).setClientId(inMessage.getClientId());
/*    */ 
/* 186 */       if (Log.isError())
/*    */       {
/* 188 */         StringBuffer sb = new StringBuffer();
/* 189 */         StackTraceElement[] el = t.getStackTrace();
/* 190 */         if (el != null)
/*    */         {
/* 192 */           for (int i = 0; i < el.length; i++)
/*    */           {
/* 194 */             sb.append("    ");
/* 195 */             sb.append(el[i].toString());
/* 196 */             sb.append(StringUtils.NEWLINE);
/*    */           }
/*    */         }
/* 199 */         Log.getLogger("Message.General").error("Unhandled error when processing a message: " + t.toString() + StringUtils.NEWLINE + "  incomingMessage: " + inMessage + StringUtils.NEWLINE + "  errorReply: " + outMessage + StringUtils.NEWLINE + "  stackTrace for: " + t.toString() + StringUtils.NEWLINE + sb.toString());
/*    */       }
/*    */ 
/*    */     }
/*    */     finally
/*    */     {
/* 211 */       if ((context.isRecordMessageSizes()) || (context.isRecordMessageTimes()))
/*    */       {
/* 213 */         MessagePerformanceUtils.updateOutgoingMPI(context, inMessage, outMessage);
/*    */       }
/*    */ 
/* 219 */       FlexSession session = FlexContext.getFlexSession();
/* 220 */       if ((session != null) && (session.useSmallMessages()) && (!context.isLegacy()) && (context.getVersion() >= 3) && ((outMessage instanceof Message)))
/*    */       {
/* 225 */         outMessage = this.endpoint.convertToSmallMessage((Message)outMessage);
/*    */       }
/*    */ 
/* 228 */       response.setReplyMethod(replyMethodName);
/* 229 */       response.setData(outMessage);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.amf.MessageBrokerFilter
 * JD-Core Version:    0.6.0
 */
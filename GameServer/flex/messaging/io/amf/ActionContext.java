/*     */ package flex.messaging.io.amf;
/*     */ 
/*     */ import flex.messaging.messages.MessagePerformanceInfo;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ public class ActionContext
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 2300156738426801921L;
/*     */   private int messageNumber;
/*     */   private ActionMessage requestMessage;
/*     */   private ActionMessage responseMessage;
/*     */   private ByteArrayOutputStream outBuffer;
/*     */   private int status;
/*     */   private int version;
/*     */   private boolean legacy;
/*     */   public boolean isPush;
/*     */   public boolean isDebug;
/*     */   private int deserializedBytes;
/*     */   private int serializedBytes;
/*     */   private boolean recordMessageSizes;
/*     */   private boolean recordMessageTimes;
/*     */   private MessagePerformanceInfo mpii;
/*     */   private MessagePerformanceInfo mpio;
/*     */ 
/*     */   public ActionContext()
/*     */   {
/*  82 */     this.status = 0;
/*     */   }
/*     */ 
/*     */   public boolean isLegacy()
/*     */   {
/*  87 */     return this.legacy;
/*     */   }
/*     */ 
/*     */   public void setLegacy(boolean legacy)
/*     */   {
/*  92 */     this.legacy = legacy;
/*     */   }
/*     */ 
/*     */   public int getMessageNumber()
/*     */   {
/*  97 */     return this.messageNumber;
/*     */   }
/*     */ 
/*     */   public void setMessageNumber(int messageNumber)
/*     */   {
/* 102 */     this.messageNumber = messageNumber;
/*     */   }
/*     */ 
/*     */   public MessageBody getRequestMessageBody()
/*     */   {
/* 107 */     return this.requestMessage.getBody(this.messageNumber);
/*     */   }
/*     */ 
/*     */   public ActionMessage getRequestMessage()
/*     */   {
/* 112 */     return this.requestMessage;
/*     */   }
/*     */ 
/*     */   public void setRequestMessage(ActionMessage requestMessage)
/*     */   {
/* 117 */     this.requestMessage = requestMessage;
/*     */   }
/*     */ 
/*     */   public ActionMessage getResponseMessage()
/*     */   {
/* 122 */     return this.responseMessage;
/*     */   }
/*     */ 
/*     */   public MessageBody getResponseMessageBody()
/*     */   {
/* 127 */     return this.responseMessage.getBody(this.messageNumber);
/*     */   }
/*     */ 
/*     */   public void setResponseMessage(ActionMessage responseMessage)
/*     */   {
/* 132 */     this.responseMessage = responseMessage;
/*     */   }
/*     */ 
/*     */   public void setResponseOutput(ByteArrayOutputStream out)
/*     */   {
/* 137 */     this.outBuffer = out;
/*     */   }
/*     */ 
/*     */   public ByteArrayOutputStream getResponseOutput()
/*     */   {
/* 142 */     return this.outBuffer;
/*     */   }
/*     */ 
/*     */   public int getStatus()
/*     */   {
/* 147 */     return this.status;
/*     */   }
/*     */ 
/*     */   public void setStatus(int status)
/*     */   {
/* 152 */     this.status = status;
/*     */   }
/*     */ 
/*     */   public void setVersion(int v)
/*     */   {
/* 157 */     this.version = v;
/*     */   }
/*     */ 
/*     */   public int getVersion()
/*     */   {
/* 162 */     return this.version;
/*     */   }
/*     */ 
/*     */   public void incrementMessageNumber()
/*     */   {
/* 167 */     this.messageNumber += 1;
/*     */   }
/*     */ 
/*     */   public int getDeserializedBytes()
/*     */   {
/* 172 */     return this.deserializedBytes;
/*     */   }
/*     */ 
/*     */   public void setDeserializedBytes(int deserializedBytes)
/*     */   {
/* 177 */     this.deserializedBytes = deserializedBytes;
/*     */   }
/*     */ 
/*     */   public int getSerializedBytes()
/*     */   {
/* 182 */     return this.serializedBytes;
/*     */   }
/*     */ 
/*     */   public void setSerializedBytes(int serializedBytes)
/*     */   {
/* 187 */     this.serializedBytes = serializedBytes;
/*     */   }
/*     */ 
/*     */   public MessagePerformanceInfo getMPII()
/*     */   {
/* 192 */     return this.mpii;
/*     */   }
/*     */ 
/*     */   public void setMPII(MessagePerformanceInfo mpii)
/*     */   {
/* 197 */     this.mpii = mpii;
/*     */   }
/*     */ 
/*     */   public MessagePerformanceInfo getMPIO()
/*     */   {
/* 202 */     return this.mpio;
/*     */   }
/*     */ 
/*     */   public void setMPIO(MessagePerformanceInfo mpio)
/*     */   {
/* 207 */     this.mpio = mpio;
/*     */   }
/*     */ 
/*     */   public boolean isRecordMessageSizes()
/*     */   {
/* 212 */     return this.recordMessageSizes;
/*     */   }
/*     */ 
/*     */   public void setRecordMessageSizes(boolean recordMessageSizes)
/*     */   {
/* 217 */     this.recordMessageSizes = recordMessageSizes;
/*     */   }
/*     */ 
/*     */   public boolean isRecordMessageTimes()
/*     */   {
/* 222 */     return this.recordMessageTimes;
/*     */   }
/*     */ 
/*     */   public boolean isMPIenabled()
/*     */   {
/* 227 */     return (this.recordMessageTimes) || (this.recordMessageSizes);
/*     */   }
/*     */ 
/*     */   public void setRecordMessageTimes(boolean recordMessageTimes)
/*     */   {
/* 232 */     this.recordMessageTimes = recordMessageTimes;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.amf.ActionContext
 * JD-Core Version:    0.6.0
 */
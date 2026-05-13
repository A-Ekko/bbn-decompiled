/*     */ package flex.messaging.client;
/*     */ 
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.messages.Message;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class FlexClientOutboundQueueProcessor
/*     */ {
/*     */   private FlexClient client;
/*     */   private String endpointId;
/*     */ 
/*     */   public void setEndpointId(String value)
/*     */   {
/*  68 */     this.endpointId = value;
/*     */   }
/*     */ 
/*     */   public String getEndpointId()
/*     */   {
/*  78 */     return this.endpointId;
/*     */   }
/*     */ 
/*     */   public void setFlexClient(FlexClient value)
/*     */   {
/*  89 */     this.client = value;
/*     */   }
/*     */ 
/*     */   public FlexClient getFlexClient()
/*     */   {
/*  99 */     return this.client;
/*     */   }
/*     */ 
/*     */   public void initialize(ConfigMap properties)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void add(List outboundQueue, Message message)
/*     */   {
/* 119 */     outboundQueue.add(message);
/*     */   }
/*     */ 
/*     */   public FlushResult flush(List outboundQueue)
/*     */   {
/* 132 */     FlushResult flushResult = new FlushResult();
/* 133 */     ArrayList messagesToFlush = new ArrayList();
/* 134 */     for (Iterator iter = outboundQueue.iterator(); iter.hasNext(); )
/*     */     {
/* 136 */       Message message = (Message)iter.next();
/* 137 */       if (!isMessageExpired(message))
/* 138 */         messagesToFlush.add(message);
/*     */     }
/* 140 */     flushResult.setMessages(messagesToFlush);
/* 141 */     outboundQueue.clear();
/* 142 */     return flushResult;
/*     */   }
/*     */ 
/*     */   public FlushResult flush(MessageClient client, List outboundQueue)
/*     */   {
/* 157 */     FlushResult flushResult = new FlushResult();
/* 158 */     List messagesForClient = new ArrayList();
/* 159 */     Message message = null;
/* 160 */     for (Iterator iter = outboundQueue.iterator(); iter.hasNext(); )
/*     */     {
/* 162 */       message = (Message)iter.next();
/* 163 */       if (!message.getClientId().equals(client.getClientId()))
/*     */         continue;
/* 165 */       iter.remove();
/* 166 */       if (!isMessageExpired(message)) {
/* 167 */         messagesForClient.add(message);
/*     */       }
/*     */     }
/* 170 */     flushResult.setMessages(messagesForClient);
/* 171 */     return flushResult;
/*     */   }
/*     */ 
/*     */   public boolean isMessageExpired(Message message)
/*     */   {
/* 189 */     return (message.getTimeToLive() > 0L) && (System.currentTimeMillis() - message.getTimestamp() >= message.getTimeToLive());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClientOutboundQueueProcessor
 * JD-Core Version:    0.6.0
 */
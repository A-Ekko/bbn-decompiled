/*     */ package flex.messaging.client;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.FlexSession;
/*     */ import flex.messaging.FlexSessionListener;
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.MessageClientListener;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.util.TimeoutAbstractObject;
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class EndpointPushNotifier extends TimeoutAbstractObject
/*     */   implements EndpointPushHandler, FlexSessionListener, MessageClientListener
/*     */ {
/*  96 */   public final Object pushNeeded = new Object();
/*     */   private volatile boolean closed;
/*     */   private volatile boolean closing;
/*     */   private int idleTimeoutMinutes;
/*     */   private final Endpoint endpoint;
/*     */   private final FlexClient flexClient;
/*     */   private final FlexSession flexSession;
/* 144 */   private final Object lock = new Object();
/*     */ 
/* 150 */   private String logCategory = "Endpoint.General";
/*     */   private List messages;
/* 162 */   private final CopyOnWriteArrayList messageClients = new CopyOnWriteArrayList();
/*     */   private final String notifierId;
/*     */ 
/*     */   public EndpointPushNotifier(Endpoint endpoint, FlexClient flexClient)
/*     */   {
/*  77 */     this.notifierId = UUIDUtils.createUUID(false);
/*  78 */     this.endpoint = endpoint;
/*  79 */     this.flexClient = flexClient;
/*  80 */     flexClient.registerEndpointPushHandler(this, endpoint.getId());
/*  81 */     this.flexSession = FlexContext.getFlexSession();
/*  82 */     if (this.flexSession != null)
/*  83 */       this.flexSession.addSessionDestroyedListener(this);
/*  84 */     updateLastUse();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 181 */     synchronized (this.lock)
/*     */     {
/* 183 */       if ((this.closed) || (this.closing)) {
/* 184 */         return;
/*     */       }
/* 186 */       this.closing = true;
/*     */     }
/*     */ 
/* 189 */     cancelTimeout();
/*     */ 
/* 191 */     if (this.flexSession != null) {
/* 192 */       this.flexSession.removeSessionDestroyedListener(this);
/*     */     }
/*     */ 
/* 195 */     synchronized (this.pushNeeded)
/*     */     {
/* 197 */       this.flexClient.unregisterEndpointPushHandler(this, this.endpoint.getId());
/*     */     }
/*     */ 
/* 201 */     ArrayList list = new ArrayList(1);
/* 202 */     CommandMessage disconnect = new CommandMessage(12);
/* 203 */     list.add(disconnect);
/* 204 */     pushMessages(list);
/*     */ 
/* 209 */     for (Iterator iter = this.messageClients.iterator(); iter.hasNext(); ) {
/* 210 */       ((MessageClient)iter.next()).invalidate();
/*     */     }
/*     */ 
/* 215 */     synchronized (this.lock)
/*     */     {
/* 217 */       this.closed = true;
/* 218 */       this.closing = false;
/*     */     }
/* 220 */     synchronized (this.pushNeeded)
/*     */     {
/* 222 */       this.pushNeeded.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List drainMessages()
/*     */   {
/* 237 */     synchronized (this.pushNeeded)
/*     */     {
/* 239 */       List messagesToPush = this.messages;
/* 240 */       this.messages = null;
/* 241 */       return messagesToPush;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isClosed()
/*     */   {
/* 252 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public Endpoint getEndpoint()
/*     */   {
/* 262 */     return this.endpoint;
/*     */   }
/*     */ 
/*     */   public int getIdleTimeoutMinutes()
/*     */   {
/* 272 */     return this.idleTimeoutMinutes;
/*     */   }
/*     */ 
/*     */   public void setIdleTimeoutMinutes(int idleTimeoutMinutes)
/*     */   {
/* 282 */     this.idleTimeoutMinutes = idleTimeoutMinutes;
/*     */   }
/*     */ 
/*     */   public String getLogCategory()
/*     */   {
/* 292 */     return this.logCategory;
/*     */   }
/*     */ 
/*     */   public void setLogCategory(String logCategory)
/*     */   {
/* 303 */     this.logCategory = logCategory;
/*     */   }
/*     */ 
/*     */   public String getNotifierId()
/*     */   {
/* 313 */     return this.notifierId;
/*     */   }
/*     */ 
/*     */   public long getTimeoutPeriod()
/*     */   {
/* 324 */     return this.idleTimeoutMinutes * 60 * 1000;
/*     */   }
/*     */ 
/*     */   public void messageClientCreated(MessageClient messageClient)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void messageClientDestroyed(MessageClient messageClient)
/*     */   {
/* 342 */     unregisterMessageClient(messageClient);
/*     */   }
/*     */ 
/*     */   public void pushMessages(List messagesToPush)
/*     */   {
/* 354 */     if (!messagesToPush.isEmpty())
/*     */     {
/* 356 */       synchronized (this.pushNeeded)
/*     */       {
/* 359 */         if (this.messages == null)
/* 360 */           this.messages = messagesToPush;
/*     */         else {
/* 362 */           this.messages.addAll(messagesToPush);
/*     */         }
/*     */ 
/* 366 */         if (!this.closing)
/* 367 */           this.pushNeeded.notifyAll();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void registerMessageClient(MessageClient messageClient)
/*     */   {
/* 379 */     if (messageClient != null)
/*     */     {
/* 381 */       if (this.messageClients.addIfAbsent(messageClient))
/* 382 */         messageClient.addMessageClientDestroyedListener(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionCreated(FlexSession flexSession)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void sessionDestroyed(FlexSession flexSession)
/*     */   {
/* 405 */     if (Log.isInfo()) {
/* 406 */       Log.getLogger(this.logCategory).info("Endpoint with id '" + this.endpoint.getId() + "' is closing" + " a streaming connection for the FlexClient with id '" + this.flexClient.getId() + "'" + " since its associated session has been destroyed.");
/*     */     }
/*     */ 
/* 409 */     close();
/*     */   }
/*     */ 
/*     */   public void timeout()
/*     */   {
/* 419 */     if (Log.isInfo()) {
/* 420 */       Log.getLogger(this.logCategory).info("Endpoint with id '" + this.endpoint.getId() + "' is timing out" + " a streaming connection for the FlexClient with id '" + this.flexClient.getId() + "'");
/*     */     }
/* 422 */     close();
/*     */   }
/*     */ 
/*     */   public void unregisterMessageClient(MessageClient messageClient)
/*     */   {
/* 432 */     if (messageClient != null)
/*     */     {
/* 434 */       messageClient.removeMessageClientDestroyedListener(this);
/* 435 */       this.messageClients.remove(messageClient);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.EndpointPushNotifier
 * JD-Core Version:    0.6.0
 */
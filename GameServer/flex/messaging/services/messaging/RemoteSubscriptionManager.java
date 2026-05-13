/*     */ package flex.messaging.services.messaging;
/*     */ 
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.MessageDestination;
/*     */ import flex.messaging.cluster.RemoveNodeListener;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class RemoteSubscriptionManager extends SubscriptionManager
/*     */   implements RemoveNodeListener
/*     */ {
/*  41 */   private Object syncLock = new Object();
/*     */ 
/*  47 */   private static final Object initRemoteSubscriptionsLock = new Object();
/*     */ 
/*     */   public RemoteSubscriptionManager(MessageDestination destination)
/*     */   {
/*  51 */     this(destination, false);
/*     */   }
/*     */ 
/*     */   public RemoteSubscriptionManager(MessageDestination destination, boolean enableManagement)
/*     */   {
/*  56 */     super(destination, enableManagement);
/*     */   }
/*     */ 
/*     */   public void setSessionTimeout(long sessionConfigValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   public long getSessionTimeout()
/*     */   {
/*  65 */     return 0L;
/*     */   }
/*     */ 
/*     */   public void addSubscriber(String flexClientId, Object clientId, String selector, String subtopic)
/*     */   {
/*  70 */     synchronized (this.syncLock)
/*     */     {
/*  78 */       if (this.allSubscriptions.get(clientId) != null)
/*  79 */         super.addSubscriber(clientId, selector, subtopic, null);
/*  80 */       else if (Log.isDebug())
/*  81 */         Log.getLogger("Service.Message").debug("Ignoring new remote subscription for server: " + clientId + " whose subscription state we have not yet received.  selector: " + selector + " subtopic: " + subtopic);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeSubscriber(String flexClientId, Object clientId, String selector, String subtopic, String endpoint)
/*     */   {
/*  87 */     synchronized (this.syncLock)
/*     */     {
/*  90 */       if (this.allSubscriptions.get(clientId) != null)
/*  91 */         super.removeSubscriber(clientId, selector, subtopic, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void sendSubscriptionToPeer(boolean subscribe, String selector, String subtopic)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected MessageClient newMessageClient(Object clientId, String endpointId)
/*     */   {
/* 102 */     return new RemoteMessageClient(clientId, this.destination, endpointId);
/*     */   }
/*     */ 
/*     */   public void setSubscriptionState(Object state, Object address)
/*     */   {
/* 114 */     MessageClient client = newMessageClient(address, null);
/*     */ 
/* 116 */     if (Log.isDebug()) {
/* 117 */       Log.getLogger("Service.Message").debug("Received subscription state for destination: " + this.destination.getId() + " from server: " + address + StringUtils.NEWLINE + state);
/*     */     }
/*     */ 
/* 126 */     synchronized (this.syncLock)
/*     */     {
/* 128 */       this.allSubscriptions.put(address, client);
/*     */ 
/* 130 */       List list = (List)state;
/*     */ 
/* 132 */       for (int i = 0; i < list.size(); i += 2)
/*     */       {
/* 134 */         addSubscriber(null, address, (String)list.get(i), (String)list.get(i + 1));
/*     */       }
/*     */     }
/* 137 */     synchronized (initRemoteSubscriptionsLock)
/*     */     {
/* 139 */       initRemoteSubscriptionsLock.notifyAll();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void waitForSubscriptions(Object addr)
/*     */   {
/* 151 */     if (getSubscriber(addr) == null)
/*     */     {
/* 153 */       synchronized (initRemoteSubscriptionsLock)
/*     */       {
/*     */         try
/*     */         {
/* 157 */           if (Log.isDebug()) {
/* 158 */             Log.getLogger("Service.Message").debug("Waiting for subscriptions from cluster node: " + addr + " for destination: " + this.destination.getId());
/*     */           }
/* 160 */           initRemoteSubscriptionsLock.wait(5000L);
/*     */ 
/* 162 */           if (Log.isDebug())
/* 163 */             Log.getLogger("Service.Message").debug("Done waiting for subscriptions from cluster node: " + addr + " for destination: " + this.destination.getId());
/*     */         } catch (InterruptedException exc) {
/*     */         }
/*     */       }
/* 167 */       if ((getSubscriber(addr) == null) && (Log.isWarn()))
/* 168 */         Log.getLogger("Service.Message").warn("No response yet from request subscriptions request for server: " + addr + " for destination: " + this.destination.getId());
/*     */     }
/* 170 */     else if (Log.isDebug()) {
/* 171 */       Log.getLogger("Service.Message").debug("Already have subscriptions from server: " + addr + " for destination: " + this.destination.getId());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeClusterNode(Object address)
/*     */   {
/* 180 */     if (Log.isDebug()) {
/* 181 */       Log.getLogger("Service.Message").debug("Cluster node: " + address + " subscriptions being removed for destination:" + this.destination.getId() + " before: " + StringUtils.NEWLINE + getDebugSubscriptionState());
/*     */     }
/* 183 */     MessageClient client = getSubscriber(address);
/* 184 */     if (client != null)
/*     */     {
/* 186 */       client.invalidate();
/*     */     }
/*     */ 
/* 189 */     if (Log.isDebug())
/* 190 */       Log.getLogger("Service.Message").debug("Cluster node: " + address + " subscriptions being removed for destination:" + this.destination.getId() + " after: " + StringUtils.NEWLINE + getDebugSubscriptionState());
/*     */   }
/*     */ 
/*     */   protected void monitorTimeout(MessageClient client)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.RemoteSubscriptionManager
 * JD-Core Version:    0.6.0
 */
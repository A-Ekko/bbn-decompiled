/*     */ package flex.management.runtime.messaging.services.messaging;
/*     */ 
/*     */ import flex.management.BaseControl;
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.services.messaging.SubscriptionManager;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SubscriptionManagerControl extends BaseControl
/*     */   implements SubscriptionManagerControlMBean
/*     */ {
/*     */   private SubscriptionManager subscriptionManager;
/*     */   private Long sessionDuration;
/*     */ 
/*     */   public SubscriptionManagerControl(SubscriptionManager subscriptionManager, BaseControl parent)
/*     */   {
/*  48 */     super(parent);
/*  49 */     this.subscriptionManager = subscriptionManager;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  58 */     return this.subscriptionManager.getId();
/*     */   }
/*     */ 
/*     */   public String getType()
/*     */   {
/*  67 */     return "SubscriptionManager";
/*     */   }
/*     */ 
/*     */   public Integer getSubscriberCount()
/*     */   {
/*  76 */     Set subscriberIds = this.subscriptionManager.getSubscriberIds();
/*  77 */     if (subscriberIds != null)
/*     */     {
/*  79 */       return new Integer(subscriberIds.size());
/*     */     }
/*     */ 
/*  83 */     return new Integer(0);
/*     */   }
/*     */ 
/*     */   public String[] getSubscriberIds()
/*     */   {
/*  93 */     Set subscriberIds = this.subscriptionManager.getSubscriberIds();
/*  94 */     if (subscriberIds != null)
/*     */     {
/*  96 */       String[] ids = new String[subscriberIds.size()];
/*  97 */       return (String[])(String[])subscriberIds.toArray(ids);
/*     */     }
/*     */ 
/* 101 */     return new String[0];
/*     */   }
/*     */ 
/*     */   public void removeSubscriber(String subscriberId)
/*     */   {
/* 111 */     MessageClient subscriber = this.subscriptionManager.getSubscriber(subscriberId);
/* 112 */     if (subscriber != null)
/*     */     {
/* 114 */       this.subscriptionManager.removeSubscriber(subscriber);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllSubscribers()
/*     */   {
/* 124 */     String[] subscriberIds = getSubscriberIds();
/* 125 */     int length = subscriberIds.length;
/* 126 */     for (int i = 0; i < length; i++)
/*     */     {
/* 128 */       removeSubscriber(subscriberIds[i]);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.SubscriptionManagerControl
 * JD-Core Version:    0.6.0
 */
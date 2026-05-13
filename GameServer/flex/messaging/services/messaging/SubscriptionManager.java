/*     */ package flex.messaging.services.messaging;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageClient;
/*     */ import flex.messaging.MessageDestination;
/*     */ import flex.messaging.MessageException;
/*     */ import flex.messaging.client.FlexClient;
/*     */ import flex.messaging.config.ServerSettings;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.AsyncMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.security.MessagingSecurity;
/*     */ import flex.messaging.services.MessageService;
/*     */ import flex.messaging.services.ServiceAdapter;
/*     */ import flex.messaging.services.ServiceException;
/*     */ import flex.messaging.services.messaging.selector.JMSSelector;
/*     */ import flex.messaging.services.messaging.selector.JMSSelectorException;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import flex.messaging.util.TimeoutManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class SubscriptionManager extends ManageableComponent
/*     */ {
/*     */   public static final String TYPE = "SubscriptionManager";
/*  58 */   private static final Object classMutex = new Object();
/*  59 */   private static int instanceCount = 0;
/*     */   protected final MessageDestination destination;
/*     */   private long subscriptionTimeoutMillis;
/*  65 */   protected final Map allSubscriptions = new ConcurrentHashMap();
/*     */ 
/*  67 */   private final TopicSubscription globalSubscribers = new TopicSubscription();
/*     */ 
/*  69 */   private final Map subscribersPerSubtopic = new ConcurrentHashMap();
/*     */ 
/*  71 */   private final Map subscribersPerSubtopicWildcard = new ConcurrentHashMap();
/*     */   private static final int SUBTOPICS_NOT_SUPPORTED = 10553;
/*     */   private TimeoutManager subscriberSessionManager;
/*     */ 
/*     */   public SubscriptionManager(MessageDestination destination)
/*     */   {
/*  82 */     this(destination, false);
/*     */   }
/*     */ 
/*     */   public SubscriptionManager(MessageDestination destination, boolean enableManagement)
/*     */   {
/*  87 */     super(enableManagement);
/*  88 */     synchronized (classMutex)
/*     */     {
/*  90 */       super.setId("SubscriptionManager" + ++instanceCount);
/*     */     }
/*  92 */     this.destination = destination;
/*     */ 
/*  94 */     this.subscriptionTimeoutMillis = 0L;
/*     */   }
/*     */ 
/*     */   public void setId(String id)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 108 */     synchronized (this)
/*     */     {
/* 110 */       if (!this.allSubscriptions.isEmpty())
/*     */       {
/* 112 */         Iterator iter = this.allSubscriptions.entrySet().iterator();
/* 113 */         while (iter.hasNext())
/*     */         {
/* 115 */           Map.Entry subscription = (Map.Entry)iter.next();
/* 116 */           removeSubscriber((MessageClient)subscription.getValue());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSubscriptionTimeoutMillis(long value)
/*     */   {
/* 124 */     this.subscriptionTimeoutMillis = value;
/* 125 */     if (this.subscriptionTimeoutMillis > 0L)
/*     */     {
/* 127 */       this.subscriberSessionManager = new TimeoutManager();
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getSubscriptionTimeoutMillis()
/*     */   {
/* 133 */     return this.subscriptionTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public Object getSubscriptionState()
/*     */   {
/* 149 */     ArrayList subState = new ArrayList();
/*     */ 
/* 151 */     if ((this.globalSubscribers.defaultSubscriptions != null) && (!this.globalSubscribers.defaultSubscriptions.isEmpty()))
/*     */     {
/* 154 */       subState.add(null);
/* 155 */       subState.add(null);
/*     */     }
/* 157 */     if (this.globalSubscribers.selectorSubscriptions != null)
/*     */     {
/* 159 */       Iterator it = this.globalSubscribers.selectorSubscriptions.keySet().iterator();
/* 160 */       while (it.hasNext())
/*     */       {
/* 162 */         subState.add(it.next());
/* 163 */         subState.add(null);
/*     */       }
/*     */     }
/* 166 */     addSubscriptionState(subState, this.subscribersPerSubtopic);
/* 167 */     addSubscriptionState(subState, this.subscribersPerSubtopicWildcard);
/*     */ 
/* 169 */     if (Log.isDebug()) {
/* 170 */       Log.getLogger("Service.Message").debug("Retrieved subscription state to send to new cluster member for destination: " + this.destination.getId() + ": " + StringUtils.NEWLINE + subState);
/*     */     }
/* 172 */     return subState;
/*     */   }
/*     */ 
/*     */   private void addSubscriptionState(List subState, Map subsPerSubtopic)
/*     */   {
/* 177 */     for (Iterator it = subsPerSubtopic.entrySet().iterator(); it.hasNext(); )
/*     */     {
/* 179 */       Map.Entry entry = (Map.Entry)it.next();
/* 180 */       subtopic = (Subtopic)entry.getKey();
/* 181 */       TopicSubscription tc = (TopicSubscription)entry.getValue();
/*     */ 
/* 183 */       if ((tc.defaultSubscriptions != null) && (!tc.defaultSubscriptions.isEmpty()))
/*     */       {
/* 185 */         subState.add(null);
/* 186 */         subState.add(subtopic.toString());
/*     */       }
/* 188 */       if (tc.selectorSubscriptions != null)
/*     */       {
/* 190 */         for (sit = tc.selectorSubscriptions.keySet().iterator(); sit.hasNext(); )
/*     */         {
/* 192 */           subState.add(sit.next());
/* 193 */           subState.add(subtopic.toString());
/*     */         }
/*     */       }
/*     */     }
/*     */     Subtopic subtopic;
/*     */     Iterator sit;
/*     */   }
/*     */ 
/*     */   protected String getDebugSubscriptionState() {
/* 202 */     StringBuffer sb = new StringBuffer();
/*     */ 
/* 204 */     sb.append(" global subscriptions: " + this.globalSubscribers + StringUtils.NEWLINE);
/* 205 */     sb.append(" regular subtopic subscriptions: " + this.subscribersPerSubtopic + StringUtils.NEWLINE);
/* 206 */     sb.append(" wildcard subtopic subscriptions: " + this.subscribersPerSubtopicWildcard + StringUtils.NEWLINE);
/* 207 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public Set getSubscriberIds()
/*     */   {
/* 212 */     return this.allSubscriptions.keySet();
/*     */   }
/*     */ 
/*     */   public Set getSubscriberIds(Message message, boolean evalSelector)
/*     */   {
/* 217 */     Set ids = new LinkedHashSet();
/*     */ 
/* 219 */     Object subtopicObj = message.getHeader("DSSubtopic");
/*     */ 
/* 221 */     if ((subtopicObj instanceof Object[])) {
/* 222 */       subtopicObj = Arrays.asList((Object[])(Object[])subtopicObj);
/*     */     }
/* 224 */     if ((subtopicObj instanceof String))
/*     */     {
/* 226 */       String subtopicString = (String)subtopicObj;
/*     */ 
/* 228 */       if (subtopicString.length() > 0)
/* 229 */         addSubtopicSubscribers(subtopicString, message, ids, evalSelector);
/*     */       else
/* 231 */         addTopicSubscribers(this.globalSubscribers, message, ids, evalSelector);
/*     */     }
/* 233 */     else if ((subtopicObj instanceof List))
/*     */     {
/* 235 */       List subtopicList = (List)subtopicObj;
/* 236 */       for (int i = 0; i < subtopicList.size(); i++)
/* 237 */         addSubtopicSubscribers((String)subtopicList.get(i), message, ids, evalSelector);
/*     */     }
/*     */     else {
/* 240 */       addTopicSubscribers(this.globalSubscribers, message, ids, evalSelector);
/*     */     }
/* 242 */     return ids;
/*     */   }
/*     */ 
/*     */   public Set getSubscriberIds(String subtopicPattern, Map messageHeaders)
/*     */   {
/* 249 */     Message msg = new AsyncMessage();
/* 250 */     msg.setHeader("DSSubtopic", subtopicPattern);
/* 251 */     if (messageHeaders != null)
/* 252 */       msg.setHeaders(messageHeaders);
/* 253 */     return getSubscriberIds(msg, true);
/*     */   }
/*     */ 
/*     */   void addSubtopicSubscribers(String subtopicString, Message message, Set ids, boolean evalSelector)
/*     */   {
/* 260 */     if (!this.destination.getServerSettings().getAllowSubtopics())
/*     */     {
/* 263 */       ServiceException se = new ServiceException();
/* 264 */       se.setMessage(10553, new Object[] { subtopicString, this.destination.getId() });
/* 265 */       throw se;
/*     */     }
/* 267 */     Subtopic subtopic = getSubtopic(subtopicString);
/*     */ 
/* 269 */     ServiceAdapter adapter = this.destination.getAdapter();
/* 270 */     if ((adapter instanceof MessagingSecurity))
/*     */     {
/* 272 */       if (!((MessagingSecurity)adapter).allowSend(subtopic))
/*     */       {
/* 274 */         ServiceException se = new ServiceException();
/* 275 */         se.setMessage(10558, new Object[] { subtopicString });
/* 276 */         throw se;
/*     */       }
/*     */     }
/*     */ 
/* 280 */     TopicSubscription ts = (TopicSubscription)this.subscribersPerSubtopic.get(subtopic);
/* 281 */     addTopicSubscribers(ts, message, ids, evalSelector);
/*     */ 
/* 287 */     Set subtopics = this.subscribersPerSubtopicWildcard.keySet();
/* 288 */     for (Iterator iter = subtopics.iterator(); iter.hasNext(); )
/*     */     {
/* 290 */       Subtopic st = (Subtopic)iter.next();
/* 291 */       if (st.matches(subtopic))
/*     */       {
/* 293 */         ts = (TopicSubscription)this.subscribersPerSubtopicWildcard.get(st);
/* 294 */         addTopicSubscribers(ts, message, ids, evalSelector);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void addTopicSubscribers(TopicSubscription ts, Message message, Set ids, boolean evalSelector)
/*     */   {
/* 301 */     if (ts == null) {
/* 302 */       return;
/*     */     }
/* 304 */     Map subs = ts.defaultSubscriptions;
/* 305 */     if (subs != null)
/* 306 */       ids.addAll(subs.keySet());
/*     */     Iterator sit;
/* 307 */     if (ts.selectorSubscriptions != null)
/*     */     {
/* 309 */       for (sit = ts.selectorSubscriptions.entrySet().iterator(); sit.hasNext(); )
/*     */       {
/* 311 */         Map.Entry entry = (Map.Entry)sit.next();
/* 312 */         String selector = (String)entry.getKey();
/* 313 */         subs = (Map)entry.getValue();
/*     */ 
/* 315 */         if (!evalSelector) {
/* 316 */           ids.addAll(subs.keySet());
/*     */         }
/*     */         else {
/* 319 */           JMSSelector jmsSel = new JMSSelector(selector);
/*     */           try
/*     */           {
/* 323 */             if (jmsSel.match(message))
/* 324 */               ids.addAll(subs.keySet());
/*     */           }
/*     */           catch (JMSSelectorException jmse)
/*     */           {
/* 328 */             if (Log.isWarn())
/* 329 */               Log.getLogger("Message.Selector").warn("Error processing message selector: " + jmsSel + StringUtils.NEWLINE + "  incomingMessage: " + message + StringUtils.NEWLINE + "  selector: " + selector);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageClient getSubscriber(Object clientId)
/*     */   {
/* 349 */     MessageClient client = (MessageClient)this.allSubscriptions.get(clientId);
/* 350 */     if ((client != null) && (!client.isTimingOut()))
/* 351 */       monitorTimeout(client);
/* 352 */     return client;
/*     */   }
/*     */ 
/*     */   public void removeSubscriber(MessageClient client)
/*     */   {
/* 363 */     client.invalidate();
/*     */ 
/* 365 */     if (getSubscriber(client.getClientId()) != null)
/* 366 */       Log.getLogger("Service.Message").error("Failed to remove client: " + client.getClientId());
/*     */   }
/*     */ 
/*     */   public void addSubscriber(Object clientId, String selector, String subtopicString, String endpointId)
/*     */   {
/* 371 */     Subtopic subtopic = getSubtopic(subtopicString);
/* 372 */     MessageClient client = null;
/*     */     try
/*     */     {
/* 380 */       boolean subscriptionAlreadyExists = getSubscriber(clientId) != null;
/* 381 */       client = getMessageClient(clientId, endpointId);
/*     */ 
/* 383 */       FlexClient flexClient = FlexContext.getFlexClient();
/* 384 */       if (subscriptionAlreadyExists)
/*     */       {
/* 389 */         if ((flexClient != null) && (!flexClient.getId().equals(client.getFlexClient().getId())))
/*     */         {
/* 391 */           ServiceException se = new ServiceException();
/* 392 */           se.setMessage(10559, new Object[] { clientId });
/* 393 */           throw se;
/*     */         }
/*     */ 
/* 398 */         client.resetEndpoint(endpointId);
/*     */       }
/*     */ 
/* 401 */       ServiceAdapter adapter = this.destination.getAdapter();
/* 402 */       client.updateLastUse();
/*     */       TopicSubscription topicSub;
/*     */       TopicSubscription topicSub;
/* 404 */       if (subtopic == null)
/*     */       {
/* 406 */         topicSub = this.globalSubscribers;
/*     */       }
/*     */       else
/*     */       {
/* 410 */         if (!this.destination.getServerSettings().getAllowSubtopics())
/*     */         {
/* 413 */           ServiceException se = new ServiceException();
/* 414 */           se.setMessage(10553, new Object[] { subtopicString, this.destination.getId() });
/* 415 */           throw se;
/*     */         }
/*     */ 
/* 418 */         if (((adapter instanceof MessagingSecurity)) && (subtopic != null))
/*     */         {
/* 420 */           if (!((MessagingSecurity)adapter).allowSubscribe(subtopic))
/*     */           {
/* 422 */             ServiceException se = new ServiceException();
/* 423 */             se.setMessage(10557, new Object[] { subtopicString });
/* 424 */             throw se;
/*     */           }
/*     */         }
/*     */         Map map;
/*     */         Map map;
/* 433 */         if (subtopic.containsSubtopicWildcard())
/* 434 */           map = this.subscribersPerSubtopicWildcard;
/*     */         else {
/* 436 */           map = this.subscribersPerSubtopic;
/*     */         }
/* 438 */         topicSub = (TopicSubscription)map.get(subtopic);
/*     */ 
/* 440 */         if (topicSub == null)
/*     */         {
/* 442 */           synchronized (this)
/*     */           {
/* 444 */             topicSub = (TopicSubscription)map.get(subtopic);
/* 445 */             if (topicSub == null)
/*     */             {
/* 447 */               topicSub = new TopicSubscription();
/* 448 */               map.put(subtopic, topicSub);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       Map subs;
/* 455 */       if (selector == null)
/*     */       {
/* 457 */         Map subs = topicSub.defaultSubscriptions;
/* 458 */         if (subs == null)
/*     */         {
/* 460 */           synchronized (this)
/*     */           {
/* 462 */             if ((subs = topicSub.defaultSubscriptions) == null) {
/* 463 */               topicSub.defaultSubscriptions = (subs = new ConcurrentHashMap());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 470 */         if (topicSub.selectorSubscriptions == null)
/*     */         {
/* 472 */           synchronized (this)
/*     */           {
/* 474 */             if (topicSub.selectorSubscriptions == null)
/* 475 */               topicSub.selectorSubscriptions = new ConcurrentHashMap();
/*     */           }
/*     */         }
/* 478 */         subs = (Map)topicSub.selectorSubscriptions.get(selector);
/* 479 */         if (subs == null)
/*     */         {
/* 481 */           synchronized (this)
/*     */           {
/* 483 */             if ((subs = (Map)topicSub.selectorSubscriptions.get(selector)) == null) {
/* 484 */               topicSub.selectorSubscriptions.put(selector, subs = new ConcurrentHashMap());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 490 */       if (subs.containsKey(clientId))
/*     */       {
/* 493 */         if (Log.isWarn())
/* 494 */           Log.getLogger("Message.Selector").warn("Client: " + clientId + " already subscribed to: " + this.destination.getId() + " selector: " + selector + " subtopic: " + subtopicString);
/*     */       }
/*     */       else
/*     */       {
/* 498 */         client.addSubscription(selector, subtopicString);
/* 499 */         synchronized (this)
/*     */         {
/* 509 */           if ((subs.isEmpty()) && (this.destination.isClustered()) && (!this.destination.getServerSettings().isBroadcastRoutingMode()))
/*     */           {
/* 511 */             sendSubscriptionToPeer(true, selector, subtopicString);
/* 512 */           }subs.put(clientId, client);
/*     */         }
/* 514 */         monitorTimeout(client);
/*     */       }
/*     */     }
/*     */     finally {
/* 518 */       releaseMessageClient(client);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeSubscriber(Object clientId, String selector, String subtopicString, String endpointId)
/*     */   {
/* 525 */     MessageClient client = (MessageClient)this.allSubscriptions.get(clientId);
/* 526 */     if (client == null)
/*     */     {
/* 531 */       return;
/*     */     }
/*     */ 
/* 534 */     Subtopic subtopic = getSubtopic(subtopicString);
/*     */ 
/* 537 */     Map map = null;
/*     */     try
/*     */     {
/* 541 */       client = getMessageClient(clientId, endpointId);
/*     */       TopicSubscription topicSub;
/*     */       TopicSubscription topicSub;
/* 543 */       if (subtopic == null)
/*     */       {
/* 545 */         topicSub = this.globalSubscribers;
/*     */       }
/*     */       else
/*     */       {
/* 549 */         if (subtopic.containsSubtopicWildcard())
/* 550 */           map = this.subscribersPerSubtopicWildcard;
/*     */         else {
/* 552 */           map = this.subscribersPerSubtopic;
/*     */         }
/* 554 */         topicSub = (TopicSubscription)map.get(subtopic);
/*     */ 
/* 556 */         if (topicSub == null)
/* 557 */           throw new MessageException("Client: " + clientId + " not subscribed to subtopic: " + subtopic);
/*     */       }
/*     */       Map subs;
/*     */       Map subs;
/* 560 */       if (selector == null)
/* 561 */         subs = topicSub.defaultSubscriptions;
/*     */       else {
/* 563 */         subs = (Map)topicSub.selectorSubscriptions.get(selector);
/*     */       }
/* 565 */       if ((subs == null) || (subs.get(clientId) == null)) {
/* 566 */         throw new MessageException("Client: " + clientId + " not subscribed to destination with selector: " + selector);
/*     */       }
/* 568 */       synchronized (this)
/*     */       {
/* 570 */         subs.remove(clientId);
/* 571 */         if ((subs.isEmpty()) && (this.destination.isClustered()) && (!this.destination.getServerSettings().isBroadcastRoutingMode()))
/*     */         {
/* 573 */           sendSubscriptionToPeer(false, selector, subtopicString);
/*     */         }
/* 575 */         if (subs.isEmpty())
/*     */         {
/* 577 */           if (selector != null)
/*     */           {
/* 579 */             if ((topicSub.selectorSubscriptions != null) && (topicSub.selectorSubscriptions.isEmpty())) {
/* 580 */               topicSub.selectorSubscriptions.remove(selector);
/*     */             }
/*     */           }
/* 583 */           if ((subtopic != null) && ((topicSub.selectorSubscriptions == null) || (topicSub.selectorSubscriptions.isEmpty())) && ((topicSub.defaultSubscriptions == null) || (topicSub.defaultSubscriptions.isEmpty())))
/*     */           {
/* 587 */             if (((topicSub.selectorSubscriptions == null) || (topicSub.selectorSubscriptions.isEmpty())) && ((topicSub.defaultSubscriptions == null) || (topicSub.defaultSubscriptions.isEmpty())))
/*     */             {
/* 589 */               map.remove(subtopic);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 594 */       if (client.removeSubscription(selector, subtopicString))
/*     */       {
/* 596 */         this.allSubscriptions.remove(clientId);
/* 597 */         client.invalidate();
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 602 */       releaseMessageClient(client);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected MessageClient newMessageClient(Object clientId, String endpointId)
/*     */   {
/* 608 */     return new MessageClient(clientId, this.destination, endpointId);
/*     */   }
/*     */ 
/*     */   public MessageClient registerMessageClient(Object clientId, String endpointId)
/*     */   {
/* 619 */     MessageClient client = getMessageClient(clientId, endpointId);
/*     */ 
/* 621 */     monitorTimeout(client);
/*     */ 
/* 628 */     if (client.isRegistered())
/* 629 */       releaseMessageClient(client);
/*     */     else {
/* 631 */       client.setRegistered(true);
/*     */     }
/* 633 */     return client;
/*     */   }
/*     */ 
/*     */   public MessageClient getMessageClient(Object clientId, String endpointId)
/*     */   {
/* 638 */     synchronized (this.allSubscriptions)
/*     */     {
/* 640 */       MessageClient client = (MessageClient)this.allSubscriptions.get(clientId);
/* 641 */       if (client == null)
/*     */       {
/* 643 */         client = newMessageClient(clientId, endpointId);
/* 644 */         this.allSubscriptions.put(clientId, client);
/*     */       }
/*     */ 
/* 647 */       client.incrementReferences();
/* 648 */       return client;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void releaseMessageClient(MessageClient client)
/*     */   {
/* 654 */     if (client == null) {
/* 655 */       return;
/*     */     }
/* 657 */     synchronized (this.allSubscriptions)
/*     */     {
/* 659 */       if (client.decrementReferences())
/*     */       {
/* 661 */         this.allSubscriptions.remove(client.getClientId());
/* 662 */         client.invalidate();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void monitorTimeout(MessageClient client)
/*     */   {
/* 669 */     if (this.subscriberSessionManager != null)
/*     */     {
/* 671 */       synchronized (client)
/*     */       {
/* 673 */         if (!client.isTimingOut())
/*     */         {
/* 675 */           this.subscriberSessionManager.scheduleTimeout(client);
/* 676 */           client.setTimingOut(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private Subtopic getSubtopic(String subtopic)
/*     */   {
/* 684 */     if (subtopic == null) {
/* 685 */       return null;
/*     */     }
/* 687 */     return new Subtopic(subtopic, this.destination.getServerSettings().getSubtopicSeparator());
/*     */   }
/*     */ 
/*     */   protected void sendSubscriptionToPeer(boolean subscribe, String selector, String subtopic)
/*     */   {
/* 696 */     if (Log.isDebug()) {
/* 697 */       Log.getLogger("Service.Message").debug("Sending subscription to peers for subscribe? " + subscribe + " selector: " + selector + " subtopic: " + subtopic);
/*     */     }
/* 699 */     ((MessageService)this.destination.getService()).sendSubscribeFromPeer(this.destination.getId(), subscribe ? Boolean.TRUE : Boolean.FALSE, selector, subtopic);
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 722 */     return "Service.Message";
/*     */   }
/*     */ 
/*     */   static class TopicSubscription
/*     */   {
/*     */     Map defaultSubscriptions;
/*     */     Map selectorSubscriptions;
/*     */ 
/*     */     public String toString()
/*     */     {
/* 712 */       StringBuffer sb = new StringBuffer();
/*     */ 
/* 714 */       sb.append("default subscriptions: " + this.defaultSubscriptions + StringUtils.NEWLINE);
/* 715 */       sb.append("selector subscriptions: " + this.selectorSubscriptions + StringUtils.NEWLINE);
/* 716 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.SubscriptionManager
 * JD-Core Version:    0.6.0
 */
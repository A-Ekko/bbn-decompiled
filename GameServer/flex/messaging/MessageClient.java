/*     */ package flex.messaging;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
/*     */ import flex.messaging.client.FlexClient;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import flex.messaging.messages.Message;
/*     */ import flex.messaging.services.MessageService;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.services.messaging.SubscriptionManager;
/*     */ import flex.messaging.services.messaging.ThrottleManager;
/*     */ import flex.messaging.services.messaging.selector.JMSSelector;
/*     */ import flex.messaging.services.messaging.selector.JMSSelectorException;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import flex.messaging.util.TimeoutAbstractObject;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class MessageClient extends TimeoutAbstractObject
/*     */   implements Serializable
/*     */ {
/*     */   public static final String MESSAGE_CLIENT_LOG_CATEGORY = "Client.MessageClient";
/*     */   static final long serialVersionUID = 3730240451524954453L;
/*  91 */   private static final CopyOnWriteArrayList createdListeners = new CopyOnWriteArrayList();
/*     */   private volatile boolean clientChannelDisconnected;
/*     */   protected final Object clientId;
/*     */   protected final MessageDestination destination;
/*     */   protected final String destinationId;
/*     */   private volatile transient CopyOnWriteArrayList destroyedListeners;
/*     */   private String endpointId;
/*     */   private final transient FlexClient flexClient;
/*     */   private transient FlexSession flexSession;
/*     */   private boolean invalidating;
/* 284 */   protected Object lock = new Object();
/*     */   private volatile boolean attemptingInvalidationClientNotification;
/*     */   private transient int numReferences;
/* 307 */   protected final Set subscriptions = new TreeSet();
/*     */   protected boolean valid;
/*     */   private volatile boolean willTimeout;
/* 327 */   private volatile boolean registered = false;
/*     */ 
/*     */   public static void addMessageClientCreatedListener(MessageClientListener listener)
/*     */   {
/* 108 */     if (listener != null)
/* 109 */       createdListeners.addIfAbsent(listener);
/*     */   }
/*     */ 
/*     */   public static void removeMessageClientCreatedListener(MessageClientListener listener)
/*     */   {
/* 121 */     if (listener != null)
/* 122 */       createdListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   private static boolean equalStrings(String a, String b)
/*     */   {
/* 136 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */ 
/*     */   private static int compareStrings(String a, String b)
/*     */   {
/* 144 */     if (a == b) {
/* 145 */       return 0;
/*     */     }
/* 147 */     if ((a != null) && (b != null)) {
/* 148 */       return a.compareTo(b);
/*     */     }
/* 150 */     if (a == null) {
/* 151 */       return -1;
/*     */     }
/* 153 */     return 1;
/*     */   }
/*     */ 
/*     */   public MessageClient(Object clientId, MessageDestination destination, String endpointId)
/*     */   {
/* 172 */     this(clientId, destination, endpointId, true);
/*     */   }
/*     */ 
/*     */   public MessageClient(Object clientId, MessageDestination destination, String endpointId, boolean useSession)
/*     */   {
/* 186 */     this.valid = true;
/* 187 */     this.clientId = clientId;
/* 188 */     this.destination = destination;
/* 189 */     this.endpointId = endpointId;
/* 190 */     this.destinationId = destination.getId();
/* 191 */     updateLastUse();
/*     */     Iterator iter;
/* 194 */     if (useSession)
/*     */     {
/* 196 */       this.flexSession = FlexContext.getFlexSession();
/* 197 */       this.flexSession.registerMessageClient(this);
/*     */ 
/* 199 */       this.flexClient = FlexContext.getFlexClient();
/* 200 */       this.flexClient.registerMessageClient(this);
/*     */ 
/* 203 */       if (!createdListeners.isEmpty())
/*     */       {
/* 206 */         for (iter = createdListeners.iterator(); iter.hasNext(); )
/* 207 */           ((MessageClientListener)iter.next()).messageClientCreated(this);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 212 */       this.flexClient = null;
/* 213 */       this.flexSession = null;
/*     */ 
/* 215 */       this.lock = new Object();
/*     */     }
/*     */ 
/* 219 */     if (Log.isDebug())
/* 220 */       Log.getLogger("Client.MessageClient").debug("MessageClient created with clientId '" + this.clientId + "' for destination '" + this.destinationId + "'.");
/*     */   }
/*     */ 
/*     */   public Object getClientId()
/*     */   {
/* 342 */     return this.clientId;
/*     */   }
/*     */ 
/*     */   public String getDestinationId()
/*     */   {
/* 352 */     return this.destinationId;
/*     */   }
/*     */ 
/*     */   public String getEndpointId()
/*     */   {
/* 362 */     return this.endpointId;
/*     */   }
/*     */ 
/*     */   public FlexClient getFlexClient()
/*     */   {
/* 372 */     return this.flexClient;
/*     */   }
/*     */ 
/*     */   public FlexSession getFlexSession()
/*     */   {
/* 382 */     synchronized (this.lock)
/*     */     {
/* 384 */       return this.flexSession;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSubscriptionCount()
/*     */   {
/*     */     int count;
/* 395 */     synchronized (this.lock)
/*     */     {
/* 397 */       count = this.subscriptions != null ? this.subscriptions.size() : 0;
/*     */     }
/*     */ 
/* 400 */     return count;
/*     */   }
/*     */ 
/*     */   public boolean isAttemptingInvalidationClientNotification()
/*     */   {
/* 412 */     return this.attemptingInvalidationClientNotification;
/*     */   }
/*     */ 
/*     */   public void setClientChannelDisconnected(boolean value)
/*     */   {
/* 425 */     this.clientChannelDisconnected = value;
/*     */   }
/*     */ 
/*     */   public boolean isClientChannelDisconnected()
/*     */   {
/* 433 */     return this.clientChannelDisconnected;
/*     */   }
/*     */ 
/*     */   public void setRegistered(boolean reg)
/*     */   {
/* 445 */     this.registered = reg;
/*     */   }
/*     */ 
/*     */   public boolean isRegistered()
/*     */   {
/* 453 */     return this.registered;
/*     */   }
/*     */ 
/*     */   public void addMessageClientDestroyedListener(MessageClientListener listener)
/*     */   {
/* 465 */     if (listener != null)
/*     */     {
/* 467 */       checkValid();
/*     */ 
/* 469 */       if (this.destroyedListeners == null)
/*     */       {
/* 471 */         synchronized (this.lock)
/*     */         {
/* 473 */           if (this.destroyedListeners == null) {
/* 474 */             this.destroyedListeners = new CopyOnWriteArrayList();
/*     */           }
/*     */         }
/*     */       }
/* 478 */       this.destroyedListeners.addIfAbsent(listener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeMessageClientDestroyedListener(MessageClientListener listener)
/*     */   {
/* 492 */     if ((listener != null) && (this.destroyedListeners != null))
/* 493 */       this.destroyedListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void addSubscription(String selector, String subtopic)
/*     */   {
/* 505 */     synchronized (this.lock)
/*     */     {
/* 507 */       checkValid();
/*     */ 
/* 509 */       incrementReferences();
/* 510 */       this.subscriptions.add(new SubscriptionInfo(selector, subtopic));
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean removeSubscription(String selector, String subtopic)
/*     */   {
/* 524 */     synchronized (this.lock)
/*     */     {
/* 526 */       if (this.subscriptions.remove(new SubscriptionInfo(selector, subtopic)))
/* 527 */         return decrementReferences();
/* 528 */       if (Log.isError())
/* 529 */         Log.getLogger("Service.Message").error("Error - unable to find subscription to remove for MessageClient: " + this.clientId + " selector: " + selector + " subtopic: " + subtopic);
/* 530 */       return this.numReferences == 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void incrementReferences()
/*     */   {
/* 542 */     synchronized (this.lock)
/*     */     {
/* 544 */       this.numReferences += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean decrementReferences()
/*     */   {
/* 554 */     synchronized (this.lock)
/*     */     {
/* 556 */       if (--this.numReferences == 0)
/*     */       {
/* 558 */         cancelTimeout();
/* 559 */         if (this.destination.getThrottleManager() != null)
/* 560 */           this.destination.getThrottleManager().removeClientThrottleMark(this.clientId);
/* 561 */         return true;
/*     */       }
/* 563 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resetEndpoint(String newEndpointId)
/*     */   {
/* 577 */     if (this.flexClient != null) {
/* 578 */       this.flexClient.unregisterMessageClient(this);
/*     */     }
/* 580 */     synchronized (this.lock)
/*     */     {
/* 582 */       this.endpointId = newEndpointId;
/* 583 */       this.flexSession = FlexContext.getFlexSession();
/*     */     }
/*     */ 
/* 586 */     if (this.flexClient != null)
/* 587 */       this.flexClient.registerMessageClient(this);
/*     */   }
/*     */ 
/*     */   public boolean testMessage(Message message)
/*     */   {
/* 604 */     String subtopic = (String)message.getHeader("DSSubtopic");
/*     */     Iterator it;
/* 605 */     synchronized (this.lock)
/*     */     {
/* 607 */       for (it = this.subscriptions.iterator(); it.hasNext(); )
/*     */       {
/* 609 */         SubscriptionInfo si = (SubscriptionInfo)it.next();
/* 610 */         String csel = si.selector;
/* 611 */         String csub = si.subtopic;
/*     */ 
/* 613 */         if (equalStrings(csub, subtopic))
/*     */         {
/* 615 */           if (csel == null) {
/* 616 */             return true;
/*     */           }
/* 618 */           JMSSelector selector = new JMSSelector(csel);
/*     */           try
/*     */           {
/* 621 */             if (selector.match(message))
/*     */             {
/* 623 */               return true;
/*     */             }
/*     */ 
/*     */           }
/*     */           catch (JMSSelectorException jmse)
/*     */           {
/* 629 */             if (Log.isWarn())
/*     */             {
/* 631 */               Log.getLogger("Message.Selector").warn("Error processing message selector: " + jmse.toString() + StringUtils.NEWLINE + "  incomingMessage: " + message + StringUtils.NEWLINE + "  selector: " + csel + StringUtils.NEWLINE);
/*     */             }
/*     */ 
/* 636 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 641 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 651 */     synchronized (this.lock)
/*     */     {
/* 653 */       return this.valid;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invalidate()
/*     */   {
/* 662 */     invalidate(false);
/*     */   }
/*     */ 
/*     */   public void invalidate(boolean notifyClient)
/*     */   {
/* 677 */     synchronized (this.lock)
/*     */     {
/* 679 */       if ((!this.valid) || (this.invalidating)) {
/* 680 */         return;
/*     */       }
/* 682 */       this.invalidating = true;
/* 683 */       cancelTimeout();
/*     */     }
/*     */ 
/* 687 */     this.attemptingInvalidationClientNotification = notifyClient;
/*     */ 
/* 690 */     if ((notifyClient) && (this.flexClient != null) && (this.flexClient.isValid()))
/*     */     {
/* 692 */       CommandMessage msg = new CommandMessage();
/* 693 */       msg.setDestination(this.destination.getId());
/* 694 */       msg.setClientId(this.clientId);
/* 695 */       msg.setOperation(10);
/* 696 */       Object subscriberIds = new TreeSet();
/* 697 */       ((Set)subscriberIds).add(this.clientId);
/*     */       try
/*     */       {
/* 700 */         ((MessageService)this.destination.getService()).pushMessageToClients(this.destination, (Set)subscriberIds, msg, false);
/*     */       }
/*     */       catch (MessageException ignore)
/*     */       {
/*     */       }
/*     */     }
/* 706 */     if ((this.destroyedListeners != null) && (!this.destroyedListeners.isEmpty()))
/*     */     {
/* 708 */       for (Iterator iter = this.destroyedListeners.iterator(); iter.hasNext(); )
/*     */       {
/* 710 */         ((MessageClientListener)iter.next()).messageClientDestroyed(this);
/*     */       }
/* 712 */       this.destroyedListeners.clear();
/*     */     }
/*     */ 
/* 720 */     ArrayList unsubMessages = new ArrayList();
/*     */     Iterator iter;
/* 721 */     synchronized (this.lock)
/*     */     {
/* 723 */       for (iter = this.subscriptions.iterator(); iter.hasNext(); )
/*     */       {
/* 725 */         SubscriptionInfo subInfo = (SubscriptionInfo)iter.next();
/* 726 */         CommandMessage unsubMessage = new CommandMessage();
/* 727 */         unsubMessage.setDestination(this.destination.getId());
/* 728 */         unsubMessage.setClientId(this.clientId);
/* 729 */         unsubMessage.setOperation(1);
/* 730 */         unsubMessage.setHeader("DSSubscriptionInvalidated", Boolean.TRUE);
/* 731 */         unsubMessage.setHeader("DSSelector", subInfo.selector);
/* 732 */         unsubMessage.setHeader("DSSubtopic", subInfo.subtopic);
/* 733 */         unsubMessages.add(unsubMessage);
/*     */       }
/*     */     }
/*     */ 
/* 737 */     for (Iterator iter = unsubMessages.iterator(); iter.hasNext(); ) {
/* 738 */       this.destination.getService().serviceCommand((CommandMessage)iter.next());
/*     */     }
/* 740 */     synchronized (this.lock)
/*     */     {
/* 743 */       int remainingSubscriptionCount = this.subscriptions.size();
/* 744 */       if ((remainingSubscriptionCount > 0) && (Log.isError())) {
/* 745 */         Log.getLogger("Client.MessageClient").error("MessageClient: " + getClientId() + " failed to remove " + remainingSubscriptionCount + " subscription(s) during invalidation");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 750 */     if (this.registered) {
/* 751 */       this.destination.getSubscriptionManager().releaseMessageClient(this);
/*     */     }
/* 753 */     synchronized (this.lock)
/*     */     {
/* 755 */       this.valid = false;
/* 756 */       this.invalidating = false;
/*     */     }
/*     */ 
/* 759 */     if (Log.isDebug())
/* 760 */       Log.getLogger("Client.MessageClient").debug("MessageClient with clientId '" + this.clientId + "' for destination '" + this.destinationId + "' has been invalidated.");
/*     */   }
/*     */ 
/*     */   public void invalidate(Message message)
/*     */   {
/* 771 */     if (message != null)
/*     */     {
/* 773 */       message.setDestination(this.destination.getId());
/* 774 */       message.setClientId(this.clientId);
/*     */ 
/* 776 */       Set subscriberIds = new TreeSet();
/* 777 */       subscriberIds.add(this.clientId);
/*     */       try
/*     */       {
/* 780 */         ((MessageService)this.destination.getService()).pushMessageToClients(this.destination, subscriberIds, message, false);
/*     */       }
/*     */       catch (MessageException ignore) {
/*     */       }
/* 784 */       invalidate(true);
/*     */     }
/*     */     else
/*     */     {
/* 788 */       invalidate();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 803 */     if ((o instanceof MessageClient))
/*     */     {
/* 805 */       MessageClient c = (MessageClient)o;
/* 806 */       if ((c != null) && (c.getClientId().equals(this.clientId)))
/* 807 */         return true;
/*     */     }
/* 809 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 821 */     return getClientId().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 832 */     return String.valueOf(this.clientId);
/*     */   }
/*     */ 
/*     */   public long getTimeoutPeriod()
/*     */   {
/* 846 */     return this.destination.getSubscriptionManager().getSubscriptionTimeoutMillis();
/*     */   }
/*     */ 
/*     */   public void timeout()
/*     */   {
/* 857 */     invalidate(true);
/*     */   }
/*     */ 
/*     */   public boolean isTimingOut()
/*     */   {
/* 866 */     return this.willTimeout;
/*     */   }
/*     */ 
/*     */   public void setTimingOut(boolean value)
/*     */   {
/* 875 */     this.willTimeout = value;
/*     */   }
/*     */ 
/*     */   private void checkValid()
/*     */   {
/* 890 */     synchronized (this.lock)
/*     */     {
/* 892 */       if (!this.valid)
/*     */       {
/* 894 */         throw new RuntimeException("MessageClient has been invalidated.");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected static class SubscriptionInfo
/*     */     implements Comparable
/*     */   {
/*     */     public String selector;
/*     */     public String subtopic;
/*     */ 
/*     */     SubscriptionInfo(String sel, String sub)
/*     */     {
/* 916 */       this.selector = sel;
/* 917 */       this.subtopic = sub;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 922 */       if ((o instanceof SubscriptionInfo))
/*     */       {
/* 924 */         SubscriptionInfo other = (SubscriptionInfo)o;
/* 925 */         return (MessageClient.access$000(other.selector, this.selector)) && (MessageClient.access$000(other.subtopic, this.subtopic));
/*     */       }
/*     */ 
/* 928 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 933 */       return (this.selector == null ? 0 : this.selector.hashCode()) + (this.subtopic == null ? 1 : this.subtopic.hashCode());
/*     */     }
/*     */ 
/*     */     public int compareTo(Object o)
/*     */     {
/* 944 */       SubscriptionInfo other = (SubscriptionInfo)o;
/*     */       int result;
/* 947 */       if ((result = MessageClient.access$100(other.selector, this.selector)) != 0)
/* 948 */         return result;
/* 949 */       if ((result = MessageClient.access$100(other.subtopic, this.subtopic)) != 0) {
/* 950 */         return result;
/*     */       }
/* 952 */       return 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.MessageClient
 * JD-Core Version:    0.6.0
 */
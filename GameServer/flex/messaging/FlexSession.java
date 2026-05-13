/*      */ package flex.messaging;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
/*      */ import flex.messaging.client.FlexClient;
/*      */ import flex.messaging.client.FlexClientListener;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.security.LoginManager;
/*      */ import flex.messaging.util.TimeoutAbstractObject;
/*      */ import java.security.Principal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ public abstract class FlexSession extends TimeoutAbstractObject
/*      */   implements FlexClientListener, MessageClientListener
/*      */ {
/*      */   public static final String FLEX_SESSION_LOG_CATEGORY = "Endpoint.FlexSession";
/*   63 */   private static final CopyOnWriteArrayList createdListeners = new CopyOnWriteArrayList();
/*      */   private static final int FLEX_SESSION_INVALIDATED = 10019;
/*  127 */   protected final Object lock = new Object();
/*      */   private HashMap attributes;
/*      */   private volatile CopyOnWriteArrayList attributeListeners;
/*      */   private boolean creationNotified;
/*      */   private volatile CopyOnWriteArrayList destroyedListeners;
/*  152 */   private final CopyOnWriteArrayList flexClients = new CopyOnWriteArrayList();
/*      */   private boolean invalidating;
/*      */   private volatile CopyOnWriteArrayList messageClients;
/*      */   private volatile Map remoteCredentials;
/*  173 */   protected boolean valid = true;
/*      */   public volatile Object asyncPoll;
/*      */   private Principal userPrincipal;
/*  257 */   public volatile boolean canStream = true;
/*      */ 
/*  268 */   public int maxConnectionsPerSession = 1;
/*      */   public int streamingConnectionsCount;
/*      */   private boolean useSmallMessages;
/*      */   public volatile Object waitMonitor;
/*      */ 
/*      */   public static void addSessionCreatedListener(FlexSessionListener listener)
/*      */   {
/*  101 */     if (listener != null)
/*  102 */       createdListeners.addIfAbsent(listener);
/*      */   }
/*      */ 
/*      */   public static void removeSessionCreatedListener(FlexSessionListener listener)
/*      */   {
/*  114 */     if (listener != null)
/*  115 */       createdListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public Principal getUserPrincipal()
/*      */   {
/*  223 */     synchronized (this.lock)
/*      */     {
/*  225 */       checkValid();
/*  226 */       return this.userPrincipal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setUserPrincipal(Principal userPrincipal)
/*      */   {
/*  239 */     synchronized (this.lock)
/*      */     {
/*  241 */       checkValid();
/*  242 */       this.userPrincipal = userPrincipal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean useSmallMessages()
/*      */   {
/*  310 */     return this.useSmallMessages;
/*      */   }
/*      */ 
/*      */   public void setUseSmallMessages(boolean value)
/*      */   {
/*  318 */     this.useSmallMessages = value;
/*      */   }
/*      */ 
/*      */   public void addSessionAttributeListener(FlexSessionAttributeListener listener)
/*      */   {
/*  353 */     if (listener != null)
/*      */     {
/*  355 */       checkValid();
/*      */ 
/*  357 */       if (this.attributeListeners == null)
/*      */       {
/*  359 */         synchronized (this.lock)
/*      */         {
/*  361 */           if (this.attributeListeners == null) {
/*  362 */             this.attributeListeners = new CopyOnWriteArrayList();
/*      */           }
/*      */         }
/*      */       }
/*  366 */       this.attributeListeners.addIfAbsent(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addSessionDestroyedListener(FlexSessionListener listener)
/*      */   {
/*  382 */     if (listener != null)
/*      */     {
/*  384 */       checkValid();
/*      */ 
/*  386 */       if (this.destroyedListeners == null)
/*      */       {
/*  388 */         synchronized (this.lock)
/*      */         {
/*  390 */           if (this.destroyedListeners == null) {
/*  391 */             this.destroyedListeners = new CopyOnWriteArrayList();
/*      */           }
/*      */         }
/*      */       }
/*  395 */       this.destroyedListeners.addIfAbsent(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getAttribute(String name)
/*      */   {
/*  408 */     synchronized (this.lock)
/*      */     {
/*  410 */       checkValid();
/*      */ 
/*  412 */       return this.attributes == null ? null : this.attributes.get(name);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Enumeration getAttributeNames()
/*      */   {
/*  423 */     synchronized (this.lock)
/*      */     {
/*  425 */       checkValid();
/*      */ 
/*  427 */       if (this.attributes == null) {
/*  428 */         return Collections.enumeration(Collections.EMPTY_LIST);
/*      */       }
/*      */ 
/*  432 */       return Collections.enumeration(new ArrayList(this.attributes.keySet()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void messageClientCreated(MessageClient messageClient)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void messageClientDestroyed(MessageClient messageClient)
/*      */   {
/*  454 */     unregisterMessageClient(messageClient);
/*      */   }
/*      */ 
/*      */   public abstract boolean isPushSupported();
/*      */ 
/*      */   public void push(Message message)
/*      */   {
/*  474 */     throw new UnsupportedOperationException("Push not supported.");
/*      */   }
/*      */ 
/*      */   public void removeAttribute(String name)
/*      */   {
/*      */     Object value;
/*  486 */     synchronized (this.lock)
/*      */     {
/*  488 */       checkValid();
/*      */ 
/*  490 */       value = this.attributes != null ? this.attributes.remove(name) : null;
/*      */     }
/*      */ 
/*  494 */     if (value == null) {
/*  495 */       return;
/*      */     }
/*  497 */     notifyAttributeUnbound(name, value);
/*  498 */     notifyAttributeRemoved(name, value);
/*      */   }
/*      */ 
/*      */   public void removeSessionAttributeListener(FlexSessionAttributeListener listener)
/*      */   {
/*  509 */     if ((listener != null) && (this.attributeListeners != null))
/*  510 */       this.attributeListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public void removeSessionDestroyedListener(FlexSessionListener listener)
/*      */   {
/*  523 */     if ((listener != null) && (this.destroyedListeners != null))
/*  524 */       this.destroyedListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, Object value)
/*      */   {
/*  536 */     if (value == null)
/*      */     {
/*  538 */       removeAttribute(name);
/*  539 */       return;
/*      */     }
/*      */     Object oldValue;
/*  545 */     synchronized (this.lock)
/*      */     {
/*  547 */       checkValid();
/*      */ 
/*  549 */       if (this.attributes == null) {
/*  550 */         this.attributes = new HashMap();
/*      */       }
/*  552 */       oldValue = this.attributes.put(name, value);
/*      */     }
/*      */ 
/*  555 */     if (oldValue == null)
/*      */     {
/*  557 */       notifyAttributeBound(name, value);
/*  558 */       notifyAttributeAdded(name, value);
/*      */     }
/*      */     else
/*      */     {
/*  562 */       notifyAttributeUnbound(name, oldValue);
/*  563 */       notifyAttributeReplaced(name, oldValue);
/*  564 */       notifyAttributeBound(name, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void putRemoteCredentials(FlexRemoteCredentials credentials)
/*      */   {
/*  575 */     if (credentials != null)
/*      */     {
/*  578 */       if (this.remoteCredentials == null)
/*      */       {
/*  580 */         synchronized (this.lock)
/*      */         {
/*  584 */           if (this.remoteCredentials == null)
/*  585 */             this.remoteCredentials = new HashMap(4);
/*      */         }
/*      */       }
/*  588 */       synchronized (this.remoteCredentials)
/*      */       {
/*  590 */         Object serviceMap = (Map)this.remoteCredentials.get(credentials.getService());
/*  591 */         if (serviceMap == null)
/*      */         {
/*  595 */           serviceMap = new HashMap(7);
/*  596 */           this.remoteCredentials.put(credentials.getService(), serviceMap);
/*      */         }
/*  598 */         ((Map)serviceMap).put(credentials.getDestination(), credentials);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public FlexRemoteCredentials getRemoteCredentials(String serviceId, String destinationId)
/*      */   {
/*  612 */     if ((serviceId != null) && (destinationId != null))
/*      */     {
/*  614 */       if (this.remoteCredentials == null)
/*  615 */         return null;
/*  616 */       synchronized (this.remoteCredentials)
/*      */       {
/*  618 */         Map serviceMap = (Map)this.remoteCredentials.get(serviceId);
/*  619 */         return serviceMap != null ? (FlexRemoteCredentials)serviceMap.get(destinationId) : null;
/*      */       }
/*      */     }
/*  622 */     return null;
/*      */   }
/*      */ 
/*      */   public void clearRemoteCredentials(String serviceId, String destinationId)
/*      */   {
/*  633 */     if ((serviceId != null) && (destinationId != null))
/*      */     {
/*  635 */       if (this.remoteCredentials == null)
/*  636 */         return;
/*  637 */       synchronized (this.remoteCredentials)
/*      */       {
/*  639 */         Map serviceMap = (Map)this.remoteCredentials.get(serviceId);
/*  640 */         if (serviceMap != null)
/*      */         {
/*  642 */           serviceMap.put(destinationId, null);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void invalidate()
/*      */   {
/*  653 */     synchronized (this.lock)
/*      */     {
/*  655 */       if ((!this.valid) || (this.invalidating)) {
/*  656 */         return;
/*      */       }
/*  658 */       this.invalidating = true;
/*  659 */       cancelTimeout();
/*      */     }
/*      */     Iterator iter;
/*  663 */     if (!this.flexClients.isEmpty())
/*      */     {
/*  665 */       for (iter = this.flexClients.iterator(); iter.hasNext(); ) {
/*  666 */         unregisterFlexClient((FlexClient)iter.next());
/*      */       }
/*      */     }
/*      */ 
/*  670 */     if ((this.messageClients != null) && (!this.messageClients.isEmpty()))
/*      */     {
/*  672 */       for (Iterator iter = this.messageClients.iterator(); iter.hasNext(); )
/*      */       {
/*  674 */         MessageClient messageClient = (MessageClient)iter.next();
/*  675 */         messageClient.removeMessageClientDestroyedListener(this);
/*  676 */         messageClient.invalidate();
/*      */       }
/*  678 */       this.messageClients.clear();
/*      */     }
/*      */ 
/*  682 */     if ((this.destroyedListeners != null) && (!this.destroyedListeners.isEmpty()))
/*      */     {
/*  684 */       for (Iterator iter = this.destroyedListeners.iterator(); iter.hasNext(); )
/*      */       {
/*  686 */         ((FlexSessionListener)iter.next()).sessionDestroyed(this);
/*      */       }
/*  688 */       this.destroyedListeners.clear();
/*      */     }
/*      */ 
/*  692 */     if ((this.attributes != null) && (!this.attributes.isEmpty()))
/*      */     {
/*  694 */       Object[] keys = this.attributes.keySet().toArray();
/*  695 */       for (int i = 0; i < keys.length; i++) {
/*  696 */         removeAttribute((String)keys[i]);
/*      */       }
/*  698 */       this.attributes = null;
/*      */     }
/*      */ 
/*  701 */     internalInvalidate();
/*      */ 
/*  703 */     synchronized (this.lock)
/*      */     {
/*  705 */       this.valid = false;
/*  706 */       this.invalidating = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void internalInvalidate()
/*      */   {
/*      */   }
/*      */ 
/*      */   public List getFlexClients()
/*      */   {
/*  727 */     List currentFlexClients = null;
/*  728 */     synchronized (this.lock)
/*      */     {
/*  730 */       checkValid();
/*      */ 
/*  732 */       currentFlexClients = new ArrayList(this.flexClients);
/*      */     }
/*  734 */     return currentFlexClients;
/*      */   }
/*      */ 
/*      */   public List getMessageClients()
/*      */   {
/*  747 */     List currentMessageClients = null;
/*  748 */     synchronized (this.lock)
/*      */     {
/*  750 */       checkValid();
/*      */ 
/*  752 */       currentMessageClients = this.messageClients != null ? new ArrayList(this.messageClients) : new ArrayList();
/*      */     }
/*      */ 
/*  755 */     return currentMessageClients;
/*      */   }
/*      */ 
/*      */   public abstract String getId();
/*      */ 
/*      */   public boolean isUserInRole(String role)
/*      */   {
/*  773 */     ArrayList list = new ArrayList();
/*  774 */     list.add(role);
/*  775 */     return FlexContext.getMessageBroker().getLoginManager().checkRoles(this.userPrincipal, list);
/*      */   }
/*      */ 
/*      */   public boolean isValid()
/*      */   {
/*  785 */     synchronized (this.lock)
/*      */     {
/*  787 */       return this.valid;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clientCreated(FlexClient flexClient)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void clientDestroyed(FlexClient flexClient)
/*      */   {
/*  812 */     unregisterFlexClient(flexClient);
/*      */   }
/*      */ 
/*      */   public void registerFlexClient(FlexClient flexClient)
/*      */   {
/*  823 */     if (this.flexClients.addIfAbsent(flexClient))
/*      */     {
/*  825 */       flexClient.addClientDestroyedListener(this);
/*  826 */       flexClient.registerFlexSession(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unregisterFlexClient(FlexClient flexClient)
/*      */   {
/*  838 */     if (this.flexClients.remove(flexClient))
/*      */     {
/*  840 */       flexClient.removeClientDestroyedListener(this);
/*  841 */       flexClient.unregisterFlexSession(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerMessageClient(MessageClient messageClient)
/*      */   {
/*  853 */     if (this.messageClients == null)
/*      */     {
/*  855 */       synchronized (this.lock)
/*      */       {
/*  857 */         if (this.messageClients == null) {
/*  858 */           this.messageClients = new CopyOnWriteArrayList();
/*      */         }
/*      */       }
/*      */     }
/*  862 */     if (this.messageClients.addIfAbsent(messageClient))
/*  863 */       messageClient.addMessageClientDestroyedListener(this);
/*      */   }
/*      */ 
/*      */   public void unregisterMessageClient(MessageClient messageClient)
/*      */   {
/*  874 */     if (this.messageClients != null)
/*      */     {
/*  876 */       if (this.messageClients.remove(messageClient))
/*  877 */         messageClient.removeMessageClientDestroyedListener(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void timeout()
/*      */   {
/*  888 */     invalidate();
/*      */   }
/*      */ 
/*      */   protected void checkValid()
/*      */   {
/*  902 */     synchronized (this.lock)
/*      */     {
/*  904 */       if (!this.valid)
/*      */       {
/*  906 */         LocalizedException e = new LocalizedException();
/*  907 */         e.setMessage(10019);
/*  908 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyAttributeAdded(String name, Object value)
/*      */   {
/*      */     FlexSessionBindingEvent event;
/*      */     Iterator iter;
/*  921 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/*  923 */       event = new FlexSessionBindingEvent(this, name, value);
/*      */ 
/*  925 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/*  926 */         ((FlexSessionAttributeListener)iter.next()).attributeAdded(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyAttributeBound(String name, Object value)
/*      */   {
/*  938 */     if ((value != null) && ((value instanceof FlexSessionBindingListener)))
/*      */     {
/*  940 */       FlexSessionBindingEvent bindingEvent = new FlexSessionBindingEvent(this, name);
/*  941 */       ((FlexSessionBindingListener)value).valueBound(bindingEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyAttributeRemoved(String name, Object value)
/*      */   {
/*      */     FlexSessionBindingEvent event;
/*      */     Iterator iter;
/*  953 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/*  955 */       event = new FlexSessionBindingEvent(this, name, value);
/*      */ 
/*  957 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/*  958 */         ((FlexSessionAttributeListener)iter.next()).attributeRemoved(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyAttributeReplaced(String name, Object value)
/*      */   {
/*      */     FlexSessionBindingEvent event;
/*      */     Iterator iter;
/*  970 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/*  972 */       event = new FlexSessionBindingEvent(this, name, value);
/*      */ 
/*  974 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/*  975 */         ((FlexSessionAttributeListener)iter.next()).attributeReplaced(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyAttributeUnbound(String name, Object value)
/*      */   {
/*  987 */     if ((value != null) && ((value instanceof FlexSessionBindingListener)))
/*      */     {
/*  989 */       FlexSessionBindingEvent bindingEvent = new FlexSessionBindingEvent(this, name);
/*  990 */       ((FlexSessionBindingListener)value).valueUnbound(bindingEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void notifyCreated()
/*      */   {
/* 1003 */     synchronized (this.lock)
/*      */     {
/* 1005 */       if (this.creationNotified) {
/* 1006 */         return;
/*      */       }
/* 1008 */       this.creationNotified = true;
/*      */     }
/*      */     Iterator iter;
/* 1011 */     if (!createdListeners.isEmpty())
/*      */     {
/* 1014 */       for (iter = createdListeners.iterator(); iter.hasNext(); )
/* 1015 */         ((FlexSessionListener)iter.next()).sessionCreated(this);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FlexSession
 * JD-Core Version:    0.6.0
 */
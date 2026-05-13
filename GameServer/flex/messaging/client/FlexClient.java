/*      */ package flex.messaging.client;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;
/*      */ import flex.messaging.FlexContext;
/*      */ import flex.messaging.FlexSession;
/*      */ import flex.messaging.FlexSessionListener;
/*      */ import flex.messaging.MessageClient;
/*      */ import flex.messaging.MessageClientListener;
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.util.TimeoutAbstractObject;
/*      */ import flex.messaging.util.UUIDUtils;
/*      */ import java.security.Principal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TimerTask;
/*      */ 
/*      */ public class FlexClient extends TimeoutAbstractObject
/*      */   implements FlexSessionListener, MessageClientListener
/*      */ {
/*      */   public static final String FLEX_CLIENT_LOG_CATEGORY = "Client.FlexClient";
/*      */   public static final String NULL_FLEXCLIENT_ID = "nil";
/*      */   private static final int FLEX_CLIENT_INVALIDATED = 10027;
/*      */   private static final int ENDPOINT_PUSH_HANDLER_ALREADY_REGISTERED = 10033;
/*      */   private static final String POLL_WAIT_THREAD_NAME_EXTENSION = "-in-poll-wait";
/*   93 */   private static final CopyOnWriteArrayList createdListeners = new CopyOnWriteArrayList();
/*      */   private volatile Map attributes;
/*      */   private volatile CopyOnWriteArrayList attributeListeners;
/*      */   private volatile CopyOnWriteArrayList destroyedListeners;
/*      */   private final FlexClientManager flexClientManager;
/*      */   private final String id;
/*      */   volatile boolean invalidating;
/*  202 */   private final Object lock = new Object();
/*      */   private volatile CopyOnWriteArrayList messageClients;
/*  213 */   private final Map outboundQueues = new HashMap(1);
/*      */   private Map endpointPushHandlers;
/*  227 */   private final CopyOnWriteArrayList sessions = new CopyOnWriteArrayList();
/*      */   private boolean valid;
/*      */   private Principal userPrincipal;
/*      */ 
/*      */   public static void addClientCreatedListener(FlexClientListener listener)
/*      */   {
/*  111 */     if (listener != null)
/*  112 */       createdListeners.addIfAbsent(listener);
/*      */   }
/*      */ 
/*      */   public static void removeClientCreatedListener(FlexClientListener listener)
/*      */   {
/*  124 */     if (listener != null)
/*  125 */       createdListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public FlexClient(FlexClientManager manager)
/*      */   {
/*  142 */     this(manager, UUIDUtils.createUUID());
/*      */   }
/*      */ 
/*      */   public FlexClient(FlexClientManager manager, String id)
/*      */   {
/*  154 */     this.id = id;
/*  155 */     this.flexClientManager = manager;
/*  156 */     updateLastUse();
/*  157 */     this.valid = true;
/*      */ 
/*  159 */     if (Log.isDebug())
/*  160 */       Log.getLogger("Client.FlexClient").debug("FlexClient created with id '" + this.id + "'.");
/*      */   }
/*      */ 
/*      */   public void addClientAttributeListener(FlexClientAttributeListener listener)
/*      */   {
/*  257 */     if (listener != null)
/*      */     {
/*  259 */       checkValid();
/*      */ 
/*  261 */       if (this.attributeListeners == null)
/*      */       {
/*  263 */         synchronized (this.lock)
/*      */         {
/*  265 */           if (this.attributeListeners == null) {
/*  266 */             this.attributeListeners = new CopyOnWriteArrayList();
/*      */           }
/*      */         }
/*      */       }
/*  270 */       this.attributeListeners.addIfAbsent(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addClientDestroyedListener(FlexClientListener listener)
/*      */   {
/*  286 */     if (listener != null)
/*      */     {
/*  288 */       checkValid();
/*      */ 
/*  290 */       if (this.destroyedListeners == null)
/*      */       {
/*  292 */         synchronized (this.lock)
/*      */         {
/*  294 */           if (this.destroyedListeners == null) {
/*  295 */             this.destroyedListeners = new CopyOnWriteArrayList();
/*      */           }
/*      */         }
/*      */       }
/*  299 */       this.destroyedListeners.addIfAbsent(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Object getAttribute(String name)
/*      */   {
/*  312 */     synchronized (this.lock)
/*      */     {
/*  314 */       checkValid();
/*      */ 
/*  316 */       updateLastUse();
/*      */ 
/*  318 */       return this.attributes == null ? null : this.attributes.get(name);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Enumeration getAttributeNames()
/*      */   {
/*  329 */     synchronized (this.lock)
/*      */     {
/*  331 */       checkValid();
/*      */ 
/*  333 */       updateLastUse();
/*      */ 
/*  335 */       if (this.attributes == null) {
/*  336 */         return Collections.enumeration(Collections.EMPTY_LIST);
/*      */       }
/*      */ 
/*  340 */       return Collections.enumeration(new ArrayList(this.attributes.keySet()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public EndpointPushHandler getEndpointPushHandler(String endpointId)
/*      */   {
/*  356 */     synchronized (this.lock)
/*      */     {
/*  358 */       if ((this.endpointPushHandlers != null) && (this.endpointPushHandlers.containsKey(endpointId)))
/*  359 */         return (EndpointPushHandler)this.endpointPushHandlers.get(endpointId);
/*  360 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getLastUse()
/*      */   {
/*  375 */     synchronized (this.lock)
/*      */     {
/*  377 */       long currentLastUse = super.getLastUse();
/*  378 */       long idleTime = System.currentTimeMillis() - currentLastUse;
/*  379 */       if (idleTime < this.flexClientManager.getFlexClientTimeoutMillis())
/*  380 */         return currentLastUse;
/*      */       EndpointQueue queue;
/*      */       Iterator iter;
/*  382 */       if (!this.outboundQueues.isEmpty())
/*      */       {
/*  384 */         queue = null;
/*  385 */         for (iter = this.outboundQueues.values().iterator(); iter.hasNext(); )
/*      */         {
/*  387 */           queue = (EndpointQueue)iter.next();
/*  388 */           if (queue.pushSession != null)
/*  389 */             return System.currentTimeMillis();
/*  390 */           if (queue.asyncPoll != null)
/*  391 */             return System.currentTimeMillis();
/*  392 */           if ((this.endpointPushHandlers != null) && (this.endpointPushHandlers.containsKey(queue.endpointId))) {
/*  393 */             return System.currentTimeMillis();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  398 */       FlexSession session = null;
/*  399 */       for (Iterator iter = this.sessions.iterator(); iter.hasNext(); )
/*      */       {
/*  401 */         session = (FlexSession)iter.next();
/*  402 */         Object monitor = session.waitMonitor;
/*  403 */         if ((monitor != null) && ((monitor instanceof EndpointQueue)))
/*      */         {
/*  405 */           EndpointQueue queue = (EndpointQueue)monitor;
/*  406 */           if (queue.flexClient.equals(this)) {
/*  407 */             return System.currentTimeMillis();
/*      */           }
/*      */         }
/*      */       }
/*  411 */       return currentLastUse;
/*      */     }
/*      */   }
/*      */ 
/*      */   public Principal getUserPrincipal()
/*      */   {
/*  426 */     synchronized (this.lock)
/*      */     {
/*  428 */       checkValid();
/*  429 */       return this.userPrincipal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setUserPrincipal(Principal userPrincipal)
/*      */   {
/*  443 */     synchronized (this.lock)
/*      */     {
/*  445 */       checkValid();
/*  446 */       this.userPrincipal = userPrincipal;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void invalidate()
/*      */   {
/*  455 */     synchronized (this.lock)
/*      */     {
/*  457 */       if ((!this.valid) || (this.invalidating)) {
/*  458 */         return;
/*      */       }
/*  460 */       this.invalidating = true;
/*  461 */       this.flexClientManager.removeFlexClient(this);
/*  462 */       cancelTimeout();
/*      */     }
/*      */     Iterator iter;
/*  466 */     if (!this.sessions.isEmpty())
/*      */     {
/*  468 */       for (iter = this.sessions.iterator(); iter.hasNext(); ) {
/*  469 */         unregisterFlexSession((FlexSession)iter.next());
/*      */       }
/*      */     }
/*      */ 
/*  473 */     if ((this.messageClients != null) && (!this.messageClients.isEmpty()))
/*      */     {
/*  475 */       for (Iterator iter = this.messageClients.iterator(); iter.hasNext(); )
/*      */       {
/*  477 */         MessageClient messageClient = (MessageClient)iter.next();
/*  478 */         messageClient.removeMessageClientDestroyedListener(this);
/*  479 */         messageClient.invalidate();
/*      */       }
/*  481 */       this.messageClients.clear();
/*      */     }
/*      */ 
/*  485 */     if ((this.destroyedListeners != null) && (!this.destroyedListeners.isEmpty()))
/*      */     {
/*  487 */       for (Iterator iter = this.destroyedListeners.iterator(); iter.hasNext(); )
/*      */       {
/*  489 */         ((FlexClientListener)iter.next()).clientDestroyed(this);
/*      */       }
/*  491 */       this.destroyedListeners.clear();
/*      */     }
/*      */ 
/*  495 */     if ((this.attributes != null) && (!this.attributes.isEmpty()))
/*      */     {
/*  497 */       Object[] keys = this.attributes.keySet().toArray();
/*  498 */       for (int i = 0; i < keys.length; i++) {
/*  499 */         removeAttribute((String)keys[i]);
/*      */       }
/*      */     }
/*      */ 
/*  503 */     if ((this.endpointPushHandlers != null) && (!this.endpointPushHandlers.isEmpty()))
/*      */     {
/*  505 */       for (Iterator iter = this.endpointPushHandlers.values().iterator(); iter.hasNext(); )
/*      */       {
/*  507 */         ((EndpointPushHandler)iter.next()).close();
/*      */       }
/*  509 */       this.endpointPushHandlers = null;
/*      */     }
/*      */ 
/*  512 */     synchronized (this.lock)
/*      */     {
/*  514 */       this.valid = false;
/*  515 */       this.invalidating = false;
/*      */     }
/*      */ 
/*  518 */     if (Log.isDebug())
/*  519 */       Log.getLogger("Client.FlexClient").debug("FlexClient with id '" + this.id + "' has been invalidated.");
/*      */   }
/*      */ 
/*      */   public boolean isValid()
/*      */   {
/*  529 */     synchronized (this.lock)
/*      */     {
/*  531 */       return this.valid;
/*      */     }
/*      */   }
/*      */ 
/*      */   public List getFlexSessions()
/*      */   {
/*  545 */     List currentSessions = null;
/*  546 */     synchronized (this.lock)
/*      */     {
/*  548 */       checkValid();
/*      */ 
/*  550 */       updateLastUse();
/*      */ 
/*  552 */       currentSessions = new ArrayList(this.sessions);
/*      */     }
/*  554 */     return currentSessions;
/*      */   }
/*      */ 
/*      */   public int getSessionCount()
/*      */   {
/*      */     int sessionCount;
/*  563 */     synchronized (this.lock)
/*      */     {
/*  565 */       sessionCount = this.sessions != null ? this.sessions.size() : 0;
/*      */     }
/*  567 */     return sessionCount;
/*      */   }
/*      */ 
/*      */   public int getSubscriptionCount()
/*      */   {
/*  575 */     int count = 0;
/*      */     Iterator iter;
/*  576 */     synchronized (this.lock)
/*      */     {
/*  579 */       if ((this.messageClients != null) && (!this.messageClients.isEmpty()))
/*      */       {
/*  581 */         for (iter = this.messageClients.iterator(); iter.hasNext(); )
/*      */         {
/*  583 */           MessageClient messageClient = (MessageClient)iter.next();
/*  584 */           count += messageClient.getSubscriptionCount();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  589 */     return count;
/*      */   }
/*      */ 
/*      */   public List getMessageClients()
/*      */   {
/*  602 */     List currentMessageClients = null;
/*  603 */     synchronized (this.lock)
/*      */     {
/*  605 */       checkValid();
/*      */ 
/*  607 */       updateLastUse();
/*      */ 
/*  609 */       currentMessageClients = this.messageClients != null ? new ArrayList(this.messageClients) : new ArrayList();
/*      */     }
/*      */ 
/*  612 */     return currentMessageClients;
/*      */   }
/*      */ 
/*      */   public String getId()
/*      */   {
/*  622 */     return this.id;
/*      */   }
/*      */ 
/*      */   public long getTimeoutPeriod()
/*      */   {
/*  633 */     return this.flexClientManager.getFlexClientTimeoutMillis();
/*      */   }
/*      */ 
/*      */   public void messageClientCreated(MessageClient messageClient)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void messageClientDestroyed(MessageClient messageClient)
/*      */   {
/*  654 */     unregisterMessageClient(messageClient);
/*      */   }
/*      */ 
/*      */   public FlushResult poll(String endpointId)
/*      */   {
/*  672 */     FlushResult flushResult = null;
/*  673 */     synchronized (this.lock)
/*      */     {
/*  675 */       checkValid();
/*      */ 
/*  677 */       EndpointQueue queue = (EndpointQueue)this.outboundQueues.get(endpointId);
/*      */ 
/*  679 */       if (queue != null)
/*      */       {
/*  681 */         if (!queue.messages.isEmpty()) {
/*  682 */           flushResult = internalFlush(queue);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  687 */         throwNotSubscribedException(endpointId);
/*      */       }
/*      */     }
/*  690 */     return flushResult;
/*      */   }
/*      */ 
/*      */   public TimeoutAbstractObject pollAsync(String endpointId, AsyncPollHandler handler, long waitIntervalMillis)
/*      */   {
/*  708 */     EndpointQueue queue = null;
/*  709 */     TimeoutAbstractObject asyncPollTask = null;
/*  710 */     synchronized (this.lock)
/*      */     {
/*  712 */       checkValid();
/*      */ 
/*  714 */       queue = (EndpointQueue)this.outboundQueues.get(endpointId);
/*      */ 
/*  717 */       if (queue != null)
/*      */       {
/*  719 */         if (!queue.messages.isEmpty())
/*      */         {
/*  721 */           handler.asyncPollComplete(internalFlush(queue));
/*      */         }
/*      */         else
/*      */         {
/*  726 */           FlexSession session = FlexContext.getFlexSession();
/*  727 */           synchronized (session)
/*      */           {
/*  729 */             if (session.asyncPoll != null)
/*      */             {
/*  732 */               AsyncPollWithTimeout parkedPoll = (AsyncPollWithTimeout)session.asyncPoll;
/*  733 */               if (parkedPoll.getFlexClient().equals(this))
/*      */               {
/*  735 */                 PollFlushResult result = new PollFlushResult();
/*  736 */                 result.setClientProcessingSuppressed(true);
/*  737 */                 handler.asyncPollComplete(result);
/*      */               }
/*      */               else
/*      */               {
/*  741 */                 PollFlushResult result = new PollFlushResult();
/*  742 */                 result.setAvoidBusyPolling(true);
/*  743 */                 completeAsyncPoll(parkedPoll, result);
/*      */               }
/*      */             }
/*  746 */             AsyncPollWithTimeout asyncPoll = new AsyncPollWithTimeout(this, session, queue, handler, waitIntervalMillis);
/*  747 */             session.asyncPoll = asyncPoll;
/*  748 */             queue.asyncPoll = asyncPoll;
/*  749 */             asyncPollTask = asyncPoll;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  756 */         throwNotSubscribedException(endpointId);
/*      */       }
/*      */     }
/*  759 */     return asyncPollTask;
/*      */   }
/*      */ 
/*      */   public FlushResult pollWithWait(String endpointId, FlexSession session, PollWaitListener listener, long waitIntervalMillis)
/*      */   {
/*  781 */     EndpointQueue queue = null;
/*  782 */     synchronized (this.lock)
/*      */     {
/*  784 */       checkValid();
/*      */ 
/*  786 */       queue = (EndpointQueue)this.outboundQueues.get(endpointId);
/*      */ 
/*  789 */       if ((queue != null) && (!queue.messages.isEmpty())) {
/*  790 */         return internalFlush(queue);
/*      */       }
/*      */     }
/*      */ 
/*  794 */     if (queue != null)
/*      */     {
/*  796 */       synchronized (session)
/*      */       {
/*  805 */         if (session.waitMonitor != null)
/*      */         {
/*  807 */           EndpointQueue waitingQueue = (EndpointQueue)session.waitMonitor;
/*      */ 
/*  809 */           if (waitingQueue.flexClient.equals(this))
/*      */           {
/*  811 */             PollFlushResult result = new PollFlushResult();
/*  812 */             result.setClientProcessingSuppressed(true);
/*  813 */             return result;
/*      */           }
/*      */ 
/*  818 */           waitingQueue.avoidBusyPolling = true;
/*      */ 
/*  821 */           synchronized (session.waitMonitor)
/*      */           {
/*  824 */             session.waitMonitor.notifyAll();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  829 */         session.waitMonitor = queue;
/*      */       }
/*      */ 
/*  835 */       waitIntervalMillis = waitIntervalMillis == -1L ? 0L : waitIntervalMillis;
/*  836 */       String threadName = Thread.currentThread().getName();
/*      */       try
/*      */       {
/*  839 */         boolean didWait = false;
/*  840 */         boolean avoidBusyPolling = false;
/*  841 */         synchronized (queue)
/*      */         {
/*  844 */           if (queue.messages.isEmpty())
/*      */           {
/*  846 */             if (Log.isDebug()) {
/*  847 */               Log.getLogger("Client.FlexClient").debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id + "' is waiting for new messages to arrive.");
/*      */             }
/*      */ 
/*  850 */             didWait = true;
/*      */ 
/*  853 */             Thread currentThread = Thread.currentThread();
/*  854 */             currentThread.setName(threadName + "-in-poll-wait");
/*      */ 
/*  856 */             if (listener != null) {
/*  857 */               listener.waitStart(queue);
/*      */             }
/*  859 */             queue.wait(waitIntervalMillis);
/*      */ 
/*  862 */             currentThread.setName(threadName);
/*      */ 
/*  864 */             if (listener != null) {
/*  865 */               listener.waitEnd(queue);
/*      */             }
/*  867 */             if (queue.avoidBusyPolling)
/*      */             {
/*  869 */               avoidBusyPolling = true;
/*  870 */               queue.avoidBusyPolling = false;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*  875 */         synchronized (session)
/*      */         {
/*  877 */           if (session.waitMonitor.equals(queue)) {
/*  878 */             session.waitMonitor = null;
/*      */           }
/*      */         }
/*  881 */         if (Log.isDebug())
/*      */         {
/*  883 */           if (didWait) {
/*  884 */             Log.getLogger("Client.FlexClient").debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id + "' is done waiting for new messages to arrive and is flushing the outbound queue.");
/*      */           }
/*      */           else {
/*  887 */             Log.getLogger("Client.FlexClient").debug("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id + "' didn't need to wait and is flushing the outbound queue.");
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  892 */         Object result = null;
/*  893 */         synchronized (this.lock)
/*      */         {
/*  895 */           result = internalFlush(queue);
/*      */         }
/*  897 */         if (avoidBusyPolling)
/*      */         {
/*  899 */           PollFlushResult swappedPollResult = new PollFlushResult();
/*  900 */           if (result != null)
/*      */           {
/*  902 */             swappedPollResult.setMessages(((FlushResult)result).getMessages());
/*  903 */             swappedPollResult.setNextFlushWaitTimeMillis(((FlushResult)result).getNextFlushWaitTimeMillis());
/*      */           }
/*  905 */           swappedPollResult.setAvoidBusyPolling(true);
/*  906 */           result = swappedPollResult;
/*      */         }
/*  908 */         return result;
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/*  912 */         if (Log.isWarn()) {
/*  913 */           Log.getLogger("Client.FlexClient").warn("Poll wait thread '" + threadName + "' for FlexClient with id '" + this.id + "' could not finish waiting for new messages to arrive " + "because it was interrupted: " + e.toString());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  921 */       throwNotSubscribedException(endpointId);
/*      */     }
/*  923 */     return (FlushResult)(FlushResult)(FlushResult)null;
/*      */   }
/*      */ 
/*      */   public FlushResult poll(MessageClient client)
/*      */   {
/*  938 */     FlushResult flushResult = null;
/*  939 */     synchronized (this.lock)
/*      */     {
/*  941 */       checkValid();
/*      */ 
/*  943 */       String endpointId = client.getEndpointId();
/*  944 */       EndpointQueue queue = (EndpointQueue)this.outboundQueues.get(endpointId);
/*  945 */       if (queue != null)
/*      */       {
/*      */         try
/*      */         {
/*  949 */           flushResult = internalFlush(queue, client);
/*      */         }
/*      */         catch (RuntimeException e)
/*      */         {
/*  953 */           if (Log.isError())
/*  954 */             Log.getLogger("Client.FlexClient").error("Failed to flush an outbound queue for MessageClient '" + client.getClientId() + "' for FlexClient '" + getId() + "'.", e);
/*  955 */           throw e;
/*      */         }
/*  957 */         if (flushResult != null)
/*  958 */           flushResult.setNextFlushWaitTimeMillis(0);
/*      */       }
/*      */       else
/*      */       {
/*  962 */         throwNotSubscribedException(endpointId);
/*      */       }
/*      */     }
/*  965 */     return flushResult;
/*      */   }
/*      */ 
/*      */   public void push(Message message, MessageClient messageClient)
/*      */   {
/*  979 */     synchronized (this.lock)
/*      */     {
/*  982 */       if (!this.valid) {
/*  983 */         return;
/*      */       }
/*  985 */       updateLastUse();
/*      */ 
/*  988 */       EndpointQueue queue = (EndpointQueue)this.outboundQueues.get(messageClient.getEndpointId());
/*      */ 
/*  993 */       if (queue != null)
/*      */       {
/*      */         boolean empty;
/*  995 */         synchronized (queue)
/*      */         {
/*      */           try
/*      */           {
/* 1000 */             queue.processor.add(queue.messages, message);
/* 1001 */             empty = queue.messages.isEmpty();
/*      */           }
/*      */           catch (RuntimeException e)
/*      */           {
/* 1005 */             if (Log.isError())
/* 1006 */               Log.getLogger("Client.FlexClient").error("Failed to add a message to an outbound queue for FlexClient '" + getId() + "'.", e);
/* 1007 */             throw e;
/*      */           }
/*      */ 
/* 1010 */           if (!empty) {
/* 1011 */             queue.notifyAll();
/*      */           }
/*      */         }
/* 1014 */         if (!empty)
/*      */         {
/* 1016 */           if (queue.asyncPoll != null)
/*      */           {
/* 1018 */             completeAsyncPoll(queue.asyncPoll, internalFlush(queue));
/*      */           }
/* 1020 */           else if ((!empty) && (queue.flushTask == null) && ((queue.pushSession != null) || ((this.endpointPushHandlers != null) && (this.endpointPushHandlers.containsKey(queue.endpointId)))))
/*      */           {
/* 1025 */             directFlush(queue);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerEndpointPushHandler(EndpointPushHandler handler, String endpointId)
/*      */   {
/* 1042 */     synchronized (this.lock)
/*      */     {
/* 1044 */       if (this.endpointPushHandlers == null) {
/* 1045 */         this.endpointPushHandlers = new HashMap(1);
/*      */       }
/* 1047 */       if (this.endpointPushHandlers.containsKey(endpointId))
/*      */       {
/* 1049 */         MessageException me = new MessageException();
/* 1050 */         me.setMessage(10033, new Object[] { getId(), endpointId });
/* 1051 */         throw me;
/*      */       }
/*      */ 
/* 1054 */       this.endpointPushHandlers.put(endpointId, handler);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerFlexSession(FlexSession session)
/*      */   {
/* 1066 */     if (this.sessions.addIfAbsent(session))
/*      */     {
/* 1068 */       session.addSessionDestroyedListener(this);
/* 1069 */       session.registerFlexClient(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerMessageClient(MessageClient messageClient)
/*      */   {
/* 1081 */     if (this.messageClients == null)
/*      */     {
/* 1083 */       synchronized (this.lock)
/*      */       {
/* 1085 */         if (this.messageClients == null) {
/* 1086 */           this.messageClients = new CopyOnWriteArrayList();
/*      */         }
/*      */       }
/*      */     }
/* 1090 */     if (this.messageClients.addIfAbsent(messageClient))
/*      */     {
/* 1092 */       messageClient.addMessageClientDestroyedListener(this);
/* 1093 */       String endpointId = messageClient.getEndpointId();
/*      */ 
/* 1098 */       synchronized (this.lock)
/*      */       {
/* 1100 */         getOrCreateEndpointQueueAndRegisterSubscription(messageClient, endpointId);
/* 1101 */         if (this.endpointPushHandlers != null)
/*      */         {
/* 1103 */           EndpointPushHandler handler = (EndpointPushHandler)this.endpointPushHandlers.get(endpointId);
/* 1104 */           if (handler != null)
/* 1105 */             handler.registerMessageClient(messageClient);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeAttribute(String name)
/*      */   {
/*      */     Object value;
/* 1120 */     synchronized (this.lock)
/*      */     {
/* 1122 */       checkValid();
/*      */ 
/* 1124 */       updateLastUse();
/*      */ 
/* 1126 */       value = this.attributes != null ? this.attributes.remove(name) : null;
/*      */     }
/*      */ 
/* 1130 */     if (value == null) {
/* 1131 */       return;
/*      */     }
/* 1133 */     notifyAttributeUnbound(name, value);
/* 1134 */     notifyAttributeRemoved(name, value);
/*      */   }
/*      */ 
/*      */   public void removeClientAttributeListener(FlexClientAttributeListener listener)
/*      */   {
/* 1145 */     if ((listener != null) && (this.attributeListeners != null))
/* 1146 */       this.attributeListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public void removeClientDestroyedListener(FlexClientListener listener)
/*      */   {
/* 1159 */     if ((listener != null) && (this.destroyedListeners != null))
/* 1160 */       this.destroyedListeners.remove(listener);
/*      */   }
/*      */ 
/*      */   public void sessionCreated(FlexSession session)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void sessionDestroyed(FlexSession session)
/*      */   {
/* 1184 */     unregisterFlexSession(session);
/*      */   }
/*      */ 
/*      */   public void setAttribute(String name, Object value)
/*      */   {
/* 1196 */     if (value == null)
/*      */     {
/* 1198 */       removeAttribute(name);
/* 1199 */       return;
/*      */     }
/*      */     Object oldValue;
/* 1205 */     synchronized (this.lock)
/*      */     {
/* 1207 */       checkValid();
/*      */ 
/* 1209 */       updateLastUse();
/*      */ 
/* 1211 */       if (this.attributes == null) {
/* 1212 */         this.attributes = new HashMap();
/*      */       }
/* 1214 */       oldValue = this.attributes.put(name, value);
/*      */     }
/*      */ 
/* 1217 */     if (oldValue == null)
/*      */     {
/* 1219 */       notifyAttributeBound(name, value);
/* 1220 */       notifyAttributeAdded(name, value);
/*      */     }
/*      */     else
/*      */     {
/* 1224 */       notifyAttributeUnbound(name, oldValue);
/* 1225 */       notifyAttributeReplaced(name, oldValue);
/* 1226 */       notifyAttributeBound(name, value);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void timeout()
/*      */   {
/* 1237 */     invalidate();
/*      */   }
/*      */ 
/*      */   public void unregisterEndpointPushHandler(EndpointPushHandler handler, String endpointId)
/*      */   {
/* 1249 */     synchronized (this.lock)
/*      */     {
/* 1251 */       if (this.endpointPushHandlers == null) {
/* 1252 */         return;
/*      */       }
/* 1254 */       if (this.endpointPushHandlers.get(endpointId).equals(handler))
/* 1255 */         this.endpointPushHandlers.remove(endpointId);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unregisterFlexSession(FlexSession session)
/*      */   {
/* 1267 */     if (this.sessions.remove(session))
/*      */     {
/* 1269 */       session.removeSessionDestroyedListener(this);
/* 1270 */       session.unregisterFlexClient(this);
/*      */ 
/* 1272 */       if (this.sessions.isEmpty())
/* 1273 */         invalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unregisterMessageClient(MessageClient messageClient)
/*      */   {
/* 1285 */     if (this.messageClients != null)
/*      */     {
/* 1287 */       if (this.messageClients.remove(messageClient))
/*      */       {
/* 1289 */         messageClient.removeMessageClientDestroyedListener(this);
/* 1290 */         String endpointId = messageClient.getEndpointId();
/*      */ 
/* 1292 */         synchronized (this.lock)
/*      */         {
/* 1294 */           EndpointQueue queue = (EndpointQueue)this.outboundQueues.get(endpointId);
/* 1295 */           if (queue != null)
/*      */           {
/* 1298 */             queue.messageClientRefCount -= 1;
/*      */             Object messageClientId;
/*      */             Iterator iter;
/* 1302 */             if (!messageClient.isAttemptingInvalidationClientNotification())
/*      */             {
/* 1304 */               messageClientId = messageClient.getClientId();
/* 1305 */               for (iter = queue.messages.iterator(); iter.hasNext(); )
/*      */               {
/* 1307 */                 Message message = (Message)iter.next();
/* 1308 */                 if (message.getClientId().equals(messageClientId)) {
/* 1309 */                   iter.remove();
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/* 1314 */             if (queue.messageClientRefCount == 0)
/*      */             {
/* 1316 */               if ((queue.messages.isEmpty()) || (messageClient.isClientChannelDisconnected()))
/*      */               {
/* 1318 */                 if (queue.asyncPoll != null)
/*      */                 {
/* 1320 */                   FlushResult flushResult = internalFlush(queue);
/*      */ 
/* 1324 */                   if (!messageClient.isAttemptingInvalidationClientNotification())
/*      */                   {
/* 1326 */                     CommandMessage msg = new CommandMessage();
/* 1327 */                     msg.setClientId(messageClient.getClientId());
/* 1328 */                     msg.setOperation(10);
/* 1329 */                     List messages = flushResult.getMessages();
/* 1330 */                     if (messages == null)
/* 1331 */                       messages = new ArrayList(1);
/* 1332 */                     messages.add(msg);
/*      */                   }
/* 1334 */                   completeAsyncPoll(queue.asyncPoll, flushResult);
/*      */                 }
/*      */ 
/* 1338 */                 this.outboundQueues.remove(endpointId);
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1348 */             synchronized (queue)
/*      */             {
/* 1350 */               queue.notifyAll();
/*      */             }
/*      */           }
/*      */ 
/* 1354 */           if (this.endpointPushHandlers != null)
/*      */           {
/* 1356 */             EndpointPushHandler handler = (EndpointPushHandler)this.endpointPushHandlers.get(endpointId);
/* 1357 */             if (handler != null)
/* 1358 */               handler.unregisterMessageClient(messageClient);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void notifyCreated()
/*      */   {
/*      */     Iterator iter;
/* 1377 */     if (!createdListeners.isEmpty())
/*      */     {
/* 1380 */       for (iter = createdListeners.iterator(); iter.hasNext(); )
/* 1381 */         ((FlexClientListener)iter.next()).clientCreated(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void completeAsyncPoll(AsyncPollWithTimeout asyncPoll, FlushResult result)
/*      */   {
/* 1399 */     synchronized (this.lock)
/*      */     {
/* 1401 */       asyncPoll.cancelTimeout();
/* 1402 */       EndpointQueue queue = asyncPoll.getEndpointQueue();
/* 1403 */       if (queue.asyncPoll.equals(asyncPoll))
/* 1404 */         queue.asyncPoll = null;
/* 1405 */       FlexSession session = asyncPoll.getFlexSession();
/* 1406 */       synchronized (session)
/*      */       {
/* 1408 */         if (session.asyncPoll.equals(asyncPoll))
/* 1409 */           session.asyncPoll = null;
/*      */       }
/* 1411 */       asyncPoll.getHandler().asyncPollComplete(result);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void directFlush(EndpointQueue queue)
/*      */   {
/* 1422 */     synchronized (this.lock)
/*      */     {
/* 1425 */       if (!this.valid) {
/* 1426 */         return;
/*      */       }
/*      */ 
/* 1430 */       if (queue.flushTask != null) {
/* 1431 */         queue.flushTask = null;
/*      */       }
/* 1433 */       FlushResult flushResult = internalFlush(queue);
/* 1434 */       if (flushResult != null)
/*      */       {
/* 1437 */         List messages = flushResult.getMessages();
/* 1438 */         if ((messages != null) && (!messages.isEmpty()))
/*      */         {
/* 1441 */           updateLastUse();
/*      */           Iterator iter;
/* 1443 */           if (queue.pushSession != null)
/*      */           {
/* 1445 */             for (iter = messages.iterator(); iter.hasNext(); )
/* 1446 */               queue.pushSession.push((Message)iter.next());
/*      */           }
/* 1448 */           else if (this.endpointPushHandlers != null)
/*      */           {
/* 1450 */             EndpointPushHandler handler = (EndpointPushHandler)this.endpointPushHandlers.get(queue.endpointId);
/* 1451 */             handler.pushMessages(messages);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1456 */         int flushWaitTime = flushResult.getNextFlushWaitTimeMillis();
/* 1457 */         if (flushWaitTime > 0)
/*      */         {
/* 1460 */           queue.flushTask = new FlexClientFlushTask(queue);
/* 1461 */           this.flexClientManager.scheduleFlush(queue.flushTask, flushWaitTime);
/*      */         }
/*      */         else
/*      */         {
/* 1466 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private EndpointQueue getOrCreateEndpointQueueAndRegisterSubscription(MessageClient messageClient, String endpointId)
/*      */   {
/* 1477 */     EndpointQueue newQueue = null;
/* 1478 */     if (!this.outboundQueues.containsKey(endpointId))
/*      */     {
/* 1480 */       newQueue = new EndpointQueue();
/* 1481 */       newQueue.flexClient = this;
/* 1482 */       newQueue.endpointId = endpointId;
/* 1483 */       newQueue.messages = new ArrayList();
/* 1484 */       FlexSession session = messageClient.getFlexSession();
/* 1485 */       if (session.isPushSupported())
/* 1486 */         newQueue.pushSession = session;
/* 1487 */       newQueue.processor = this.flexClientManager.createOutboundQueueProcessor(this, endpointId);
/* 1488 */       newQueue.messageClientRefCount = 1;
/*      */ 
/* 1490 */       this.outboundQueues.put(endpointId, newQueue);
/*      */     }
/*      */     else
/*      */     {
/* 1494 */       newQueue = (EndpointQueue)this.outboundQueues.get(endpointId);
/* 1495 */       newQueue.messageClientRefCount += 1;
/*      */ 
/* 1498 */       FlexSession session = messageClient.getFlexSession();
/* 1499 */       if (session.isPushSupported())
/* 1500 */         newQueue.pushSession = session;
/*      */     }
/* 1502 */     return newQueue;
/*      */   }
/*      */ 
/*      */   private FlushResult internalFlush(EndpointQueue queue)
/*      */   {
/* 1511 */     return internalFlush(queue, null);
/*      */   }
/*      */ 
/*      */   private FlushResult internalFlush(EndpointQueue queue, MessageClient client)
/*      */   {
/* 1522 */     FlushResult flushResult = null;
/*      */     try
/*      */     {
/* 1525 */       flushResult = client == null ? queue.processor.flush(queue.messages) : queue.processor.flush(client, queue.messages);
/*      */ 
/* 1527 */       shutdownQueue(queue);
/*      */ 
/* 1530 */       List messages = flushResult != null ? flushResult.getMessages() : null;
/* 1531 */       if ((messages != null) && (!messages.isEmpty()))
/* 1532 */         updateLastUse();
/*      */     }
/*      */     catch (RuntimeException e)
/*      */     {
/* 1536 */       if (Log.isError())
/* 1537 */         Log.getLogger("Client.FlexClient").error("Failed to flush an outbound queue for FlexClient '" + getId() + "'.", e);
/* 1538 */       throw e;
/*      */     }
/* 1540 */     return flushResult;
/*      */   }
/*      */ 
/*      */   private void notifyAttributeAdded(String name, Object value)
/*      */   {
/*      */     FlexClientBindingEvent event;
/*      */     Iterator iter;
/* 1552 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/* 1554 */       event = new FlexClientBindingEvent(this, name, value);
/*      */ 
/* 1556 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/* 1557 */         ((FlexClientAttributeListener)iter.next()).attributeAdded(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyAttributeBound(String name, Object value)
/*      */   {
/* 1570 */     if ((value != null) && ((value instanceof FlexClientBindingListener)))
/*      */     {
/* 1572 */       FlexClientBindingEvent bindingEvent = new FlexClientBindingEvent(this, name);
/* 1573 */       ((FlexClientBindingListener)value).valueBound(bindingEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyAttributeRemoved(String name, Object value)
/*      */   {
/*      */     FlexClientBindingEvent event;
/*      */     Iterator iter;
/* 1586 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/* 1588 */       event = new FlexClientBindingEvent(this, name, value);
/*      */ 
/* 1590 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/* 1591 */         ((FlexClientAttributeListener)iter.next()).attributeRemoved(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyAttributeReplaced(String name, Object value)
/*      */   {
/*      */     FlexClientBindingEvent event;
/*      */     Iterator iter;
/* 1604 */     if ((this.attributeListeners != null) && (!this.attributeListeners.isEmpty()))
/*      */     {
/* 1606 */       event = new FlexClientBindingEvent(this, name, value);
/*      */ 
/* 1608 */       for (iter = this.attributeListeners.iterator(); iter.hasNext(); )
/* 1609 */         ((FlexClientAttributeListener)iter.next()).attributeReplaced(event);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void notifyAttributeUnbound(String name, Object value)
/*      */   {
/* 1622 */     if ((value != null) && ((value instanceof FlexClientBindingListener)))
/*      */     {
/* 1624 */       FlexClientBindingEvent bindingEvent = new FlexClientBindingEvent(this, name);
/* 1625 */       ((FlexClientBindingListener)value).valueUnbound(bindingEvent);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkValid()
/*      */   {
/* 1635 */     synchronized (this.lock)
/*      */     {
/* 1637 */       if (!this.valid)
/*      */       {
/* 1639 */         MessageException e = new MessageException();
/* 1640 */         e.setMessage(10027);
/* 1641 */         throw e;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean shutdownQueue(EndpointQueue queue)
/*      */   {
/* 1656 */     if ((queue.messageClientRefCount == 0) && (queue.messages.isEmpty()))
/*      */     {
/* 1658 */       this.outboundQueues.remove(queue.endpointId);
/*      */ 
/* 1660 */       synchronized (queue)
/*      */       {
/* 1662 */         queue.notifyAll();
/*      */       }
/* 1664 */       return true;
/*      */     }
/*      */ 
/* 1667 */     return false;
/*      */   }
/*      */ 
/*      */   private void throwNotSubscribedException(String endpointId)
/*      */   {
/* 1678 */     MessageException e = new MessageException();
/* 1679 */     e.setMessage(10028, new Object[] { endpointId });
/* 1680 */     e.setCode("Server.Processing.NotSubscribed");
/* 1681 */     throw e;
/*      */   }
/*      */ 
/*      */   public static class EndpointQueue
/*      */   {
/*      */     public FlexClient flexClient;
/*      */     public String endpointId;
/*      */     public List messages;
/*      */     public FlexClientOutboundQueueProcessor processor;
/*      */     public FlexClient.AsyncPollWithTimeout asyncPoll;
/*      */     public FlexSession pushSession;
/*      */     public TimerTask flushTask;
/*      */     public int messageClientRefCount;
/*      */     public boolean avoidBusyPolling;
/*      */   }
/*      */ 
/*      */   class FlexClientFlushTask extends TimerTask
/*      */   {
/*      */     private final FlexClient.EndpointQueue queue;
/*      */ 
/*      */     public FlexClientFlushTask(FlexClient.EndpointQueue queue)
/*      */     {
/* 1754 */       this.queue = queue;
/*      */     }
/*      */ 
/*      */     public synchronized void run()
/*      */     {
/* 1761 */       FlexContext.setThreadLocalFlexClient(FlexClient.this);
/* 1762 */       FlexClient.this.directFlush(this.queue);
/* 1763 */       FlexContext.setThreadLocalFlexClient(null);
/*      */     }
/*      */   }
/*      */ 
/*      */   class AsyncPollWithTimeout extends TimeoutAbstractObject
/*      */   {
/*      */     private final FlexClient flexClient;
/*      */     private final FlexSession session;
/*      */     private final FlexClient.EndpointQueue queue;
/*      */     private final AsyncPollHandler handler;
/*      */ 
/*      */     public AsyncPollWithTimeout(FlexClient flexClient, FlexSession session, FlexClient.EndpointQueue queue, AsyncPollHandler handler, long waitIntervalMillis)
/*      */     {
/* 1702 */       this.flexClient = flexClient;
/* 1703 */       this.session = session;
/* 1704 */       this.queue = queue;
/* 1705 */       this.handler = handler;
/* 1706 */       setTimeoutPeriod(waitIntervalMillis);
/* 1707 */       FlexClient.this.flexClientManager.monitorAsyncPollTimeout(this);
/*      */     }
/*      */ 
/*      */     public FlexClient getFlexClient()
/*      */     {
/* 1714 */       return this.flexClient;
/*      */     }
/*      */ 
/*      */     public FlexSession getFlexSession()
/*      */     {
/* 1721 */       return this.session;
/*      */     }
/*      */ 
/*      */     public FlexClient.EndpointQueue getEndpointQueue()
/*      */     {
/* 1728 */       return this.queue;
/*      */     }
/*      */ 
/*      */     public AsyncPollHandler getHandler()
/*      */     {
/* 1735 */       return this.handler;
/*      */     }
/*      */ 
/*      */     public void timeout()
/*      */     {
/* 1740 */       FlexClient.this.completeAsyncPoll(this, null);
/*      */     }
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClient
 * JD-Core Version:    0.6.0
 */
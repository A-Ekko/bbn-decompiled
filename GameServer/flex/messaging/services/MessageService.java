/*      */ package flex.messaging.services;
/*      */ 
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.locks.ReadWriteLock;
/*      */ import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import flex.management.runtime.messaging.MessageDestinationControl;
/*      */ import flex.management.runtime.messaging.services.MessageServiceControl;
/*      */ import flex.messaging.Destination;
/*      */ import flex.messaging.FlexContext;
/*      */ import flex.messaging.MessageBroker;
/*      */ import flex.messaging.MessageClient;
/*      */ import flex.messaging.MessageDestination;
/*      */ import flex.messaging.MessageException;
/*      */ import flex.messaging.client.FlexClient;
/*      */ import flex.messaging.client.FlushResult;
/*      */ import flex.messaging.cluster.Cluster;
/*      */ import flex.messaging.cluster.ClusterManager;
/*      */ import flex.messaging.config.ServerSettings;
/*      */ import flex.messaging.log.Log;
/*      */ import flex.messaging.log.Logger;
/*      */ import flex.messaging.messages.AcknowledgeMessage;
/*      */ import flex.messaging.messages.AsyncMessage;
/*      */ import flex.messaging.messages.CommandMessage;
/*      */ import flex.messaging.messages.Message;
/*      */ import flex.messaging.messages.MessagePerformanceUtils;
/*      */ import flex.messaging.services.messaging.MessagingConstants;
/*      */ import flex.messaging.services.messaging.RemoteSubscriptionManager;
/*      */ import flex.messaging.services.messaging.SubscriptionManager;
/*      */ import flex.messaging.services.messaging.Subtopic;
/*      */ import flex.messaging.services.messaging.ThrottleManager;
/*      */ import flex.messaging.services.messaging.adapters.MessagingAdapter;
/*      */ import flex.messaging.services.messaging.adapters.MessagingSecurityConstraintManager;
/*      */ import flex.messaging.services.messaging.selector.JMSSelector;
/*      */ import flex.messaging.util.StringUtils;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ 
/*      */ public class MessageService extends AbstractService
/*      */   implements MessagingConstants
/*      */ {
/*      */   public static final String LOG_CATEGORY = "Service.Message";
/*      */   public static final String TIMING_LOG_CATEGORY = "Message.Timing";
/*      */   public static final String NOT_SUBSCRIBED_CODE = "Server.Processing.NotSubscribed";
/*      */   private static final int BAD_SELECTOR = 10550;
/*      */   private static final int NOT_SUBSCRIBED = 10551;
/*      */   private static final int UNKNOWN_COMMAND = 10552;
/*      */   private MessageServiceControl controller;
/*   83 */   private ReadWriteLock subscribeLock = new ReentrantReadWriteLock();
/*      */ 
/*      */   public MessageService()
/*      */   {
/*   96 */     super(false);
/*      */   }
/*      */ 
/*      */   public MessageService(boolean enableManagement)
/*      */   {
/*  107 */     super(enableManagement);
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  118 */     String serviceType = getClass().getName();
/*  119 */     ClusterManager clm = getMessageBroker().getClusterManager();
/*      */ 
/*  121 */     super.start();
/*      */ 
/*  129 */     for (Iterator it = this.destinations.keySet().iterator(); it.hasNext(); )
/*      */     {
/*  131 */       String destName = (String)it.next();
/*  132 */       MessageDestination dest = (MessageDestination)getDestination(destName);
/*  133 */       if ((!dest.getServerSettings().isBroadcastRoutingMode()) && (dest.isClustered())) {
/*  134 */         initRemoteSubscriptions(destName);
/*      */       }
/*      */     }
/*      */ 
/*  138 */     for (Iterator it = this.destinations.keySet().iterator(); it.hasNext(); )
/*      */     {
/*  140 */       String destName = (String)it.next();
/*  141 */       MessageDestination dest = (MessageDestination)getDestination(destName);
/*  142 */       if ((!dest.getServerSettings().isBroadcastRoutingMode()) && (dest.isClustered()))
/*      */       {
/*  144 */         List members = clm.getClusterMemberAddresses(serviceType, destName);
/*  145 */         for (int i = 0; i < members.size(); i++)
/*      */         {
/*  147 */           Object addr = members.get(i);
/*  148 */           if (clm.getLocalAddress(serviceType, destName).equals(addr))
/*      */             continue;
/*  150 */           RemoteSubscriptionManager subMgr = dest.getRemoteSubscriptionManager();
/*  151 */           subMgr.waitForSubscriptions(addr);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Destination createDestination(String id)
/*      */   {
/*  175 */     MessageDestination destination = new MessageDestination();
/*  176 */     destination.setId(id);
/*  177 */     destination.setManaged(isManaged());
/*  178 */     destination.setService(this);
/*      */ 
/*  180 */     return destination;
/*      */   }
/*      */ 
/*      */   public void addDestination(Destination destination)
/*      */   {
/*  191 */     MessageDestination messageDestination = (MessageDestination)destination;
/*  192 */     super.addDestination(messageDestination);
/*      */   }
/*      */ 
/*      */   public Object serviceMessage(Message message)
/*      */   {
/*  203 */     return serviceMessage(message, true);
/*      */   }
/*      */ 
/*      */   public Object serviceMessage(Message message, boolean throttle)
/*      */   {
/*  211 */     Object result = null;
/*      */ 
/*  213 */     incrementMessageCount(false, message);
/*      */ 
/*  215 */     MessageDestination dest = (MessageDestination)getDestination(message);
/*      */ 
/*  219 */     int throttleResult = !throttle ? 0 : dest.getThrottleManager().throttleIncomingMessage(message);
/*      */ 
/*  222 */     if (throttleResult != 1)
/*      */     {
/*  228 */       Object subtopicObj = message.getHeader("DSSubtopic");
/*      */ 
/*  230 */       if ((subtopicObj instanceof Object[])) {
/*  231 */         subtopicObj = Arrays.asList((Object[])(Object[])subtopicObj);
/*      */       }
/*  233 */       if ((subtopicObj instanceof String))
/*      */       {
/*  235 */         String subtopicString = (String)subtopicObj;
/*  236 */         testProducerSubtopic(dest, subtopicString);
/*      */       }
/*  238 */       else if ((subtopicObj instanceof List))
/*      */       {
/*  240 */         List subtopicList = (List)subtopicObj;
/*      */ 
/*  242 */         for (int i = 0; i < subtopicList.size(); i++) {
/*  243 */           testProducerSubtopic(dest, (String)subtopicList.get(i));
/*      */         }
/*      */       }
/*      */ 
/*  247 */       ServerSettings destServerSettings = dest.getServerSettings();
/*  248 */       if (destServerSettings.getMessageTTL() >= 0L) {
/*  249 */         message.setTimeToLive(destServerSettings.getMessageTTL());
/*      */       }
/*  251 */       long start = 0L;
/*  252 */       if (Log.isDebug()) {
/*  253 */         start = System.currentTimeMillis();
/*      */       }
/*      */ 
/*  256 */       ServiceAdapter adapter = dest.getAdapter();
/*  257 */       if ((adapter instanceof MessagingAdapter)) {
/*  258 */         ((MessagingAdapter)adapter).getSecurityConstraintManager().assertSendAuthorization();
/*      */       }
/*  260 */       MessagePerformanceUtils.markServerPreAdapterTime(message);
/*  261 */       result = adapter.invoke(message);
/*  262 */       MessagePerformanceUtils.markServerPostAdapterTime(message);
/*      */ 
/*  264 */       if (Log.isDebug())
/*      */       {
/*  266 */         long end = System.currentTimeMillis();
/*  267 */         Log.getLogger("Message.Timing").debug("After invoke service: " + getId() + "; execution time = " + (end - start) + "ms");
/*      */       }
/*      */     }
/*  270 */     return result;
/*      */   }
/*      */ 
/*      */   public Object serviceCommand(CommandMessage message)
/*      */   {
/*  278 */     incrementMessageCount(true, message);
/*  279 */     Object commandResult = super.serviceCommonCommands(message);
/*  280 */     if (commandResult == null)
/*      */     {
/*  282 */       commandResult = manageSubscriptions(message);
/*      */     }
/*  284 */     return commandResult;
/*      */   }
/*      */ 
/*      */   public void serviceMessageFromAdapter(Message message, boolean sendToAllSubscribers)
/*      */   {
/*  304 */     if (isManaged())
/*      */     {
/*  306 */       MessageDestinationControl control = (MessageDestinationControl)getDestination(message.getDestination()).getControl();
/*  307 */       control.incrementServiceMessageFromAdapterCount();
/*      */     }
/*      */ 
/*  312 */     if (sendToAllSubscribers)
/*      */     {
/*  314 */       pushMessageToClients(message, false);
/*  315 */       sendPushMessageFromPeer(message, false);
/*      */     }
/*      */     else
/*      */     {
/*  321 */       Set subscriberIds = new TreeSet();
/*  322 */       subscriberIds.add(message.getClientId());
/*  323 */       pushMessageToClients(subscriberIds, message, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendPushMessageFromPeer(Message message, boolean evalSelector)
/*      */   {
/*  341 */     MessageDestination destination = (MessageDestination)getDestination(message);
/*      */     ClusterManager clm;
/*      */     Iterator it;
/*  343 */     if (destination.isClustered())
/*      */     {
/*  345 */       clm = getMessageBroker().getClusterManager();
/*  346 */       if (destination.getServerSettings().isBroadcastRoutingMode())
/*      */       {
/*  348 */         if (Log.isDebug()) {
/*  349 */           Log.getLogger("Service.Message").debug("Broadcasting message to peer servers: " + message + " evalSelector: " + evalSelector);
/*      */         }
/*  351 */         clm.invokeServiceOperation(getClass().getName(), message.getDestination(), "pushMessageFromPeer", new Object[] { message, Boolean.valueOf(evalSelector) });
/*      */       }
/*      */       else
/*      */       {
/*  356 */         RemoteSubscriptionManager mgr = destination.getRemoteSubscriptionManager();
/*  357 */         Set serverAddresses = mgr.getSubscriberIds(message, evalSelector);
/*      */ 
/*  359 */         if (Log.isDebug()) {
/*  360 */           Log.getLogger("Service.Message").debug("Sending message to peer servers: " + serverAddresses + StringUtils.NEWLINE + " message: " + message + StringUtils.NEWLINE + " evalSelector: " + evalSelector);
/*      */         }
/*  362 */         for (it = serverAddresses.iterator(); it.hasNext(); )
/*      */         {
/*  364 */           Object remoteAddress = it.next();
/*      */ 
/*  366 */           clm.invokePeerToPeerOperation(getClass().getName(), message.getDestination(), "pushMessageFromPeerToPeer", new Object[] { message, Boolean.valueOf(evalSelector) }, remoteAddress);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void pushMessageFromPeerToPeer(AsyncMessage message, Boolean evalSelector, Object address)
/*      */   {
/*  381 */     pushMessageFromPeer(message, evalSelector);
/*      */   }
/*      */ 
/*      */   public void pushMessageFromPeer(AsyncMessage message, Boolean evalSelector)
/*      */   {
/*  391 */     if (Log.isDebug()) {
/*  392 */       Log.getLogger("Service.Message").debug("Received message from peer server: " + message + " evalSelector: " + evalSelector);
/*      */     }
/*      */ 
/*  396 */     FlexContext.setMessageFromPeer(true);
/*      */ 
/*  400 */     pushMessageToClients(message, evalSelector.booleanValue());
/*      */ 
/*  402 */     FlexContext.setMessageFromPeer(false);
/*      */   }
/*      */ 
/*      */   public void pushMessageToClients(Message message, boolean evalSelector)
/*      */   {
/*  415 */     MessageDestination destination = (MessageDestination)getDestination(message);
/*  416 */     SubscriptionManager subscriptionManager = destination.getSubscriptionManager();
/*  417 */     Set subscriberIds = subscriptionManager.getSubscriberIds(message, evalSelector);
/*      */ 
/*  419 */     if (Log.isDebug()) {
/*  420 */       Log.getLogger("Service.Message").debug("Sending message: " + message + StringUtils.NEWLINE + "    to subscribed clientIds: " + subscriberIds);
/*      */     }
/*  422 */     if ((subscriberIds != null) && (!subscriberIds.isEmpty()))
/*      */     {
/*  425 */       pushMessageToClients(destination, subscriberIds, message, false);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set getSubscriberIds(Message message, boolean evalSelector)
/*      */   {
/*  455 */     MessageDestination destination = (MessageDestination)getDestination(message);
/*  456 */     SubscriptionManager subscriptionManager = destination.getSubscriptionManager();
/*  457 */     return subscriptionManager.getSubscriberIds(message, evalSelector);
/*      */   }
/*      */ 
/*      */   public Set getSubscriberIds(String destinationId, String subtopicPattern, Map messageHeaders)
/*      */   {
/*  467 */     MessageDestination destination = (MessageDestination)getDestination(destinationId);
/*  468 */     SubscriptionManager subscriptionManager = destination.getSubscriptionManager();
/*  469 */     return subscriptionManager.getSubscriberIds(subtopicPattern, messageHeaders);
/*      */   }
/*      */ 
/*      */   public void pushMessageToClients(Set subscriberIds, Message message, boolean evalSelector)
/*      */   {
/*  486 */     MessageDestination destination = (MessageDestination)getDestination(message);
/*  487 */     pushMessageToClients(destination, subscriberIds, message, evalSelector);
/*      */   }
/*      */ 
/*      */   public void pushMessageToClients(MessageDestination destination, Set subscriberIds, Message message, boolean evalSelector)
/*      */   {
/*      */     int throttleResult;
/*      */     SubscriptionManager subscriptionManager;
/*      */     Iterator clientIter;
/*  498 */     if (subscriberIds != null)
/*      */     {
/*  501 */       throttleResult = destination.getThrottleManager().throttleOutgoingMessage(message, null);
/*  502 */       if (throttleResult != 1)
/*      */       {
/*  504 */         subscriptionManager = destination.getSubscriptionManager();
/*      */ 
/*  506 */         for (clientIter = subscriberIds.iterator(); clientIter.hasNext(); )
/*      */         {
/*  508 */           Object clientId = clientIter.next();
/*  509 */           MessageClient client = subscriptionManager.getSubscriber(clientId);
/*      */ 
/*  512 */           if ((client == null) || (!client.isValid()))
/*      */           {
/*  514 */             if (Log.isDebug()) {
/*  515 */               Log.getLogger("Service.Message").debug("Warning: could not find MessageClient for clientId in pushMessageToClients: " + clientId + " for destination: " + destination.getId()); continue;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  520 */           pushMessageToClient(client, destination, message, evalSelector, throttleResult);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   void pushMessageToClient(MessageClient client, MessageDestination destination, Message message, boolean evalSelector, int throttleResult)
/*      */   {
/*  534 */     if ((evalSelector) && (!client.testMessage(message)))
/*      */     {
/*  536 */       return;
/*      */     }
/*      */ 
/*  539 */     if (throttleResult == 0)
/*      */     {
/*      */       try
/*      */       {
/*  545 */         throttleResult = destination.getThrottleManager().throttleOutgoingMessage(message, client.getClientId());
/*      */       }
/*      */       catch (MessageException e)
/*      */       {
/*  551 */         Log.getLogger("Service.Message").error(e.getMessage(), e);
/*  552 */         throttleResult = 1;
/*      */       }
/*      */     }
/*      */ 
/*  556 */     if (throttleResult != 1)
/*      */     {
/*      */       try
/*      */       {
/*  562 */         if (!(message instanceof CommandMessage)) {
/*  563 */           client.updateLastUse();
/*      */         }
/*      */ 
/*  566 */         Map messageHeaders = message.getHeaders();
/*  567 */         messageHeaders.remove("DSId");
/*  568 */         messageHeaders.remove("DSEndpoint");
/*      */ 
/*  578 */         Message messageForClient = (Message)message.clone();
/*      */ 
/*  582 */         MessagePerformanceUtils.markServerPrePushTime(message);
/*  583 */         MessagePerformanceUtils.markServerPostAdapterTime(message);
/*  584 */         MessagePerformanceUtils.markServerPostAdapterExternalTime(message);
/*      */ 
/*  587 */         messageForClient.setClientId(client.getClientId());
/*      */ 
/*  589 */         if (Log.isDebug()) {
/*  590 */           Log.getLogger("Service.Message").debug("Routing message to FlexClient id:" + client.getFlexClient().getId() + "', MessageClient id: " + client.getClientId());
/*      */         }
/*  592 */         getMessageBroker().routeMessageToMessageClient(messageForClient, client);
/*      */       }
/*      */       catch (MessageException ignore)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void initRemoteSubscriptions(String destinationId)
/*      */   {
/*  607 */     ClusterManager clm = getMessageBroker().getClusterManager();
/*  608 */     String serviceType = getClass().getName();
/*  609 */     MessageDestination dest = (MessageDestination)getDestination(destinationId);
/*      */ 
/*  611 */     Cluster cluster = clm.getCluster(serviceType, destinationId);
/*  612 */     if (cluster != null) {
/*  613 */       cluster.addRemoveNodeListener(dest.getRemoteSubscriptionManager());
/*      */     }
/*  615 */     List members = clm.getClusterMemberAddresses(serviceType, destinationId);
/*  616 */     for (int i = 0; i < members.size(); i++)
/*      */     {
/*  618 */       Object addr = members.get(i);
/*  619 */       if (!clm.getLocalAddress(serviceType, destinationId).equals(addr))
/*  620 */         requestSubscriptions(destinationId, addr);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void requestSubscriptions(String destinationId, Object remoteAddress)
/*      */   {
/*  633 */     ClusterManager clm = getMessageBroker().getClusterManager();
/*  634 */     clm.invokePeerToPeerOperation(getClass().getName(), destinationId, "sendSubscriptions", new Object[] { destinationId }, remoteAddress);
/*      */   }
/*      */ 
/*      */   public void sendSubscriptions(String destinationId, Object remoteAddress)
/*      */   {
/*  647 */     MessageDestination destination = (MessageDestination)getDestination(destinationId);
/*      */ 
/*  654 */     if (destination == null)
/*      */     {
/*  656 */       if (Log.isError())
/*  657 */         Log.getLogger("Service.Message").error("Destination: " + destinationId + " does not exist on this server but we received a request for the subscription info from a peer server where the destination exists as clustered.  Check the cluster configuration for this destination and make sure it matches on all servers.");
/*  658 */       return;
/*      */     }
/*  660 */     if (!destination.isClustered())
/*      */     {
/*  662 */       if (Log.isError())
/*  663 */         Log.getLogger("Service.Message").error("Destination: " + destinationId + " is not clustered on this server but we received a request for the subscription info from a peer server which is clustered.  Check the cluster configuration for this destination and make sure it matches on all servers.");
/*  664 */       return;
/*      */     }
/*      */ 
/*  667 */     RemoteSubscriptionManager subMgr = destination.getRemoteSubscriptionManager();
/*      */ 
/*  676 */     subMgr.setSubscriptionState(Collections.EMPTY_LIST, remoteAddress);
/*      */     try
/*      */     {
/*  685 */       this.subscribeLock.writeLock().lock();
/*      */       Object subscriptions;
/*      */       Object subscriptions;
/*  687 */       if ((destination instanceof MessageDestination))
/*  688 */         subscriptions = destination.getSubscriptionManager().getSubscriptionState();
/*      */       else
/*  690 */         subscriptions = null;
/*  691 */       ClusterManager clm = getMessageBroker().getClusterManager();
/*  692 */       clm.invokePeerToPeerOperation(getClass().getName(), destinationId, "receiveSubscriptions", new Object[] { destinationId, subscriptions }, remoteAddress);
/*      */     }
/*      */     finally
/*      */     {
/*  698 */       this.subscribeLock.writeLock().unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void receiveSubscriptions(String destinationId, Object subscriptions, Object senderAddress)
/*      */   {
/*  710 */     Destination destination = getDestination(destinationId);
/*  711 */     if ((destination instanceof MessageDestination))
/*      */     {
/*  713 */       ((MessageDestination)destination).getRemoteSubscriptionManager().setSubscriptionState(subscriptions, senderAddress);
/*      */     }
/*  715 */     else if (subscriptions != null)
/*      */     {
/*  717 */       if (Log.isError())
/*  718 */         Log.getLogger("Service.Message").error("receiveSubscriptions called with non-null value but destination: " + destinationId + " is not a MessageDestination");
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sendSubscribeFromPeer(String destinationId, Boolean subscribe, String selector, String subtopic)
/*      */   {
/*  730 */     ClusterManager clm = getMessageBroker().getClusterManager();
/*      */ 
/*  732 */     String serviceType = getClass().getName();
/*      */ 
/*  734 */     clm.invokeServiceOperation(serviceType, destinationId, "subscribeFromPeer", new Object[] { destinationId, subscribe, selector, subtopic, clm.getLocalAddress(serviceType, destinationId) });
/*      */   }
/*      */ 
/*      */   public void subscribeFromPeer(String destinationId, Boolean subscribe, String selector, String subtopic, Object remoteAddress)
/*      */   {
/*  745 */     Destination destination = getDestination(destinationId);
/*      */ 
/*  747 */     RemoteSubscriptionManager subMgr = ((MessageDestination)destination).getRemoteSubscriptionManager();
/*      */ 
/*  749 */     if ((destination instanceof MessageDestination))
/*      */     {
/*  751 */       if (Log.isDebug())
/*  752 */         Log.getLogger("Service.Message").debug("Received subscription from peer: " + remoteAddress + " subscribe? " + subscribe + " selector: " + selector + " subtopic: " + subtopic);
/*  753 */       if (subscribe.booleanValue())
/*  754 */         subMgr.addSubscriber(remoteAddress, selector, subtopic, null);
/*      */       else
/*  756 */         subMgr.removeSubscriber(remoteAddress, selector, subtopic, null);
/*      */     }
/*  758 */     else if (Log.isError()) {
/*  759 */       Log.getLogger("Service.Message").error("subscribeFromPeer called with destination: " + destinationId + " that is not a MessageDestination");
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void incrementMessageCount(boolean commandMessage, Message message)
/*      */   {
/*  780 */     if (isManaged())
/*      */     {
/*  782 */       MessageDestinationControl control = (MessageDestinationControl)getDestination(message.getDestination()).getControl();
/*  783 */       if (commandMessage)
/*      */       {
/*  785 */         control.incrementServiceCommandCount();
/*      */       }
/*      */       else
/*      */       {
/*  789 */         control.incrementServiceMessageCount();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected Message manageSubscriptions(CommandMessage command)
/*      */   {
/*  803 */     Message replyMessage = null;
/*      */ 
/*  805 */     MessageDestination destination = (MessageDestination)getDestination(command);
/*  806 */     SubscriptionManager subscriptionManager = destination.getSubscriptionManager();
/*      */ 
/*  808 */     Object clientId = command.getClientId();
/*  809 */     String endpointId = (String)command.getHeader("DSEndpoint");
/*      */ 
/*  811 */     String subtopicString = (String)command.getHeader("DSSubtopic");
/*      */ 
/*  813 */     ServiceAdapter adapter = destination.getAdapter();
/*      */ 
/*  815 */     if (command.getOperation() == 0)
/*      */     {
/*  817 */       String selectorExpr = (String)command.getHeader("DSSelector");
/*      */ 
/*  819 */       getMessageBroker().inspectChannel(command, destination);
/*      */ 
/*  822 */       if ((adapter instanceof MessagingAdapter)) {
/*  823 */         ((MessagingAdapter)adapter).getSecurityConstraintManager().assertSubscribeAuthorization();
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  832 */         this.subscribeLock.readLock().lock();
/*      */ 
/*  834 */         if (adapter.handlesSubscriptions())
/*      */         {
/*  836 */           replyMessage = (Message)adapter.manage(command);
/*      */         }
/*      */         else
/*      */         {
/*  840 */           testSelector(selectorExpr, command);
/*      */         }
/*      */ 
/*  850 */         subscriptionManager.addSubscriber(clientId, selectorExpr, subtopicString, endpointId);
/*      */       }
/*      */       finally
/*      */       {
/*  854 */         this.subscribeLock.readLock().unlock();
/*      */       }
/*      */ 
/*  857 */       if (replyMessage == null)
/*  858 */         replyMessage = new AcknowledgeMessage();
/*      */     }
/*  860 */     else if (command.getOperation() == 1)
/*      */     {
/*  863 */       if ((adapter instanceof MessagingAdapter)) {
/*  864 */         ((MessagingAdapter)adapter).getSecurityConstraintManager().assertSubscribeAuthorization();
/*      */       }
/*  866 */       String selectorExpr = (String)command.getHeader("DSSelector");
/*      */       try
/*      */       {
/*  870 */         this.subscribeLock.readLock().lock();
/*      */ 
/*  872 */         if (adapter.handlesSubscriptions())
/*      */         {
/*  874 */           replyMessage = (Message)adapter.manage(command);
/*      */         }
/*  876 */         subscriptionManager.removeSubscriber(clientId, selectorExpr, subtopicString, endpointId);
/*      */       }
/*      */       finally
/*      */       {
/*  880 */         this.subscribeLock.readLock().unlock();
/*      */       }
/*      */ 
/*  883 */       if (replyMessage == null)
/*  884 */         replyMessage = new AcknowledgeMessage();
/*      */     }
/*  886 */     else if (command.getOperation() == 11)
/*      */     {
/*  888 */       getMessageBroker().inspectChannel(command, destination);
/*      */ 
/*  891 */       if ((adapter instanceof MessagingAdapter)) {
/*  892 */         ((MessagingAdapter)adapter).getSecurityConstraintManager().assertSubscribeAuthorization();
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  901 */         this.subscribeLock.readLock().lock();
/*      */ 
/*  903 */         if (adapter.handlesSubscriptions())
/*      */         {
/*  905 */           replyMessage = (Message)adapter.manage(command);
/*      */         }
/*      */ 
/*  909 */         Object[] adds = getObjectArrayFromHeader(command.getHeader("DSAddSub"));
/*  910 */         Object[] rems = getObjectArrayFromHeader(command.getHeader("DSRemSub"));
/*      */ 
/*  912 */         if (adds != null)
/*      */         {
/*  914 */           for (int i = 0; i < adds.length; i++)
/*      */           {
/*  916 */             String ss = (String)adds[i];
/*  917 */             int ix = ss.indexOf("_;_");
/*  918 */             if (ix == -1)
/*      */               continue;
/*  920 */             String subtopic = ix == 0 ? null : ss.substring(0, ix);
/*  921 */             String selector = ss.substring(ix + "_;_".length());
/*  922 */             if (selector.length() == 0) {
/*  923 */               selector = null;
/*      */             }
/*  925 */             subscriptionManager.addSubscriber(clientId, selector, subtopic, endpointId);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  931 */         if (rems != null)
/*      */         {
/*  933 */           for (int i = 0; i < rems.length; i++)
/*      */           {
/*  935 */             String ss = (String)rems[i];
/*  936 */             int ix = ss.indexOf("_;_");
/*  937 */             if (ix == -1)
/*      */               continue;
/*  939 */             String subtopic = ix == 0 ? null : ss.substring(0, ix);
/*  940 */             String selector = ss.substring(ix + "_;_".length());
/*  941 */             if (selector.length() == 0) {
/*  942 */               selector = null;
/*      */             }
/*  944 */             subscriptionManager.removeSubscriber(clientId, selector, subtopic, endpointId);
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       finally
/*      */       {
/*  951 */         this.subscribeLock.readLock().unlock();
/*      */       }
/*      */ 
/*  954 */       if (replyMessage == null)
/*  955 */         replyMessage = new AcknowledgeMessage();
/*      */     }
/*  957 */     else if (command.getOperation() == 2)
/*      */     {
/*  963 */       MessageClient client = null;
/*      */       try
/*      */       {
/*  966 */         client = subscriptionManager.getMessageClient(clientId, endpointId);
/*      */ 
/*  968 */         if (client != null)
/*      */         {
/*      */           MessageBroker broker;
/*      */           Iterator iter;
/*  970 */           if (adapter.handlesSubscriptions())
/*      */           {
/*  972 */             List missedMessages = (List)adapter.manage(command);
/*  973 */             if ((missedMessages != null) && (!missedMessages.isEmpty()))
/*      */             {
/*  975 */               broker = getMessageBroker();
/*  976 */               for (iter = missedMessages.iterator(); iter.hasNext(); )
/*  977 */                 broker.routeMessageToMessageClient((Message)iter.next(), client);
/*      */             }
/*      */           }
/*  980 */           FlushResult flushResult = client.getFlexClient().poll(client);
/*  981 */           List messagesToReturn = flushResult != null ? flushResult.getMessages() : null;
/*  982 */           if ((messagesToReturn != null) && (!messagesToReturn.isEmpty()))
/*      */           {
/*  984 */             replyMessage = new CommandMessage(4);
/*  985 */             replyMessage.setBody(messagesToReturn.toArray());
/*      */           }
/*      */           else
/*      */           {
/*  989 */             replyMessage = new AcknowledgeMessage();
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  995 */           ServiceException se = new ServiceException();
/*  996 */           se.setCode("Server.Processing.NotSubscribed");
/*  997 */           se.setMessage(10551, new Object[] { destination.getId() });
/*  998 */           throw se;
/*      */         }
/*      */       }
/*      */       finally
/*      */       {
/* 1003 */         subscriptionManager.releaseMessageClient(client);
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1008 */       ServiceException se = new ServiceException();
/* 1009 */       se.setMessage(10552, new Object[] { new Integer(command.getOperation()) });
/* 1010 */       throw se;
/*      */     }
/*      */ 
/* 1013 */     return replyMessage;
/*      */   }
/*      */ 
/*      */   protected String getLogCategory()
/*      */   {
/* 1024 */     return "Service.Message";
/*      */   }
/*      */ 
/*      */   protected void setupServiceControl(MessageBroker broker)
/*      */   {
/* 1035 */     this.controller = new MessageServiceControl(this, broker.getControl());
/* 1036 */     this.controller.register();
/* 1037 */     setControl(this.controller);
/*      */   }
/*      */ 
/*      */   private void testSelector(String selectorExpression, Message msg)
/*      */   {
/*      */     try
/*      */     {
/* 1051 */       JMSSelector selector = new JMSSelector(selectorExpression);
/* 1052 */       selector.match(msg);
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1056 */       ServiceException se = new ServiceException();
/* 1057 */       se.setMessage(10550, new Object[] { selectorExpression });
/* 1058 */       se.setRootCause(e);
/* 1059 */       throw se;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void testProducerSubtopic(MessageDestination dest, String subtopicString)
/*      */   {
/* 1065 */     if ((subtopicString != null) && (subtopicString.length() > 0))
/*      */     {
/* 1067 */       Subtopic subtopic = new Subtopic(subtopicString, dest.getServerSettings().getSubtopicSeparator());
/* 1068 */       if (subtopic.containsSubtopicWildcard())
/*      */       {
/* 1070 */         ServiceException se = new ServiceException();
/* 1071 */         se.setMessage(10556, new Object[] { subtopicString });
/* 1072 */         throw se;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object[] getObjectArrayFromHeader(Object header)
/*      */   {
/* 1079 */     if ((header instanceof Object[]))
/* 1080 */       return (Object[])(Object[])header;
/* 1081 */     if ((header instanceof List))
/* 1082 */       return ((List)header).toArray();
/* 1083 */     if (header == null) {
/* 1084 */       return null;
/*      */     }
/* 1086 */     ServiceException se = new ServiceException();
/* 1087 */     se.setMessage("Invalid header: " + header + " in message.  expected array or list and found: " + header.getClass().getName());
/* 1088 */     throw se;
/*      */   }
/*      */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.MessageService
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.cluster;
/*     */ 
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.util.ExceptionUtil;
/*     */ import flex.messaging.util.StringUtils;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.Vector;
/*     */ import org.jgroups.Address;
/*     */ import org.jgroups.ChannelException;
/*     */ import org.jgroups.JChannel;
/*     */ import org.jgroups.JChannelFactory;
/*     */ import org.jgroups.Message;
/*     */ import org.jgroups.View;
/*     */ import org.jgroups.blocks.MessageDispatcher;
/*     */ import org.jgroups.blocks.RequestHandler;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class JGroupsCluster extends Cluster
/*     */   implements RequestHandler
/*     */ {
/*     */   private final MessageDispatcher broadcastDispatcher;
/*     */   private final List broadcastHandlers;
/*     */   private final JChannel clusterChannel;
/*     */   private final ClusterManager clusterManager;
/*     */   private final ClusterMembershipListener clusterMembershipListener;
/*     */   private final Map clusterNodes;
/*     */   private final String clusterId;
/*     */ 
/*     */   public JGroupsCluster(ClusterManager clusterManager, String clusterId, Element props)
/*     */   {
/*  69 */     this.broadcastHandlers = new ArrayList();
/*  70 */     this.clusterManager = clusterManager;
/*  71 */     this.clusterMembershipListener = new ClusterMembershipListener(this);
/*  72 */     this.clusterNodes = Collections.synchronizedMap(new HashMap());
/*  73 */     this.clusterId = clusterId;
/*     */ 
/*  75 */     if (Log.isDebug()) {
/*  76 */       Log.getLogger("Service.Cluster").debug("Joining cluster with id: " + clusterId);
/*     */     }
/*  78 */     configureBroadcastHandlers();
/*     */     try
/*     */     {
/*  82 */       JChannelFactory channelFactory = new JChannelFactory(props);
/*  83 */       this.clusterChannel = ((JChannel)channelFactory.createChannel());
/*     */ 
/*  85 */       this.clusterChannel.setOpt(3, Boolean.FALSE);
/*     */ 
/*  87 */       this.broadcastDispatcher = new MessageDispatcher(this.clusterChannel, null, this.clusterMembershipListener, this);
/*  88 */       this.clusterChannel.connect(clusterId);
/*     */     }
/*     */     catch (ChannelException cex)
/*     */     {
/*  92 */       ClusterException cx = new ClusterException();
/*  93 */       cx.setMessage(10200, new Object[] { clusterId, props });
/*  94 */       cx.setRootCause(cex);
/*  95 */       throw cx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List getAllEndpoints(String serviceType, String destName)
/*     */   {
/* 107 */     List channelToEndpointMaps = new ArrayList();
/*     */     Iterator iter;
/* 109 */     synchronized (this.clusterNodes)
/*     */     {
/* 111 */       for (iter = this.clusterNodes.keySet().iterator(); iter.hasNext(); )
/*     */       {
/* 113 */         Address addr = (Address)iter.next();
/* 114 */         if (!this.clusterMembershipListener.isZombie(addr))
/*     */         {
/* 116 */           ClusterNode node = (ClusterNode)this.clusterNodes.get(addr);
/* 117 */           Map nodeEndpoints = node.getEndpoints(serviceType, destName);
/*     */ 
/* 120 */           if (nodeEndpoints.size() > 0)
/*     */           {
/* 122 */             for (Iterator iter1 = channelToEndpointMaps.iterator(); iter1.hasNext(); )
/*     */             {
/* 124 */               Map nodeEndpoints2 = (Map)iter1.next();
/* 125 */               for (iter2 = nodeEndpoints2.values().iterator(); iter2.hasNext(); )
/*     */               {
/* 127 */                 endpointUrl = (String)iter2.next();
/* 128 */                 if (nodeEndpoints.containsValue(endpointUrl))
/*     */                 {
/* 131 */                   for (iter3 = nodeEndpoints.values().iterator(); iter3.hasNext(); )
/*     */                   {
/* 133 */                     String endpointUrl2 = (String)iter3.next();
/* 134 */                     if (endpointUrl2.equals(endpointUrl))
/* 135 */                       iter3.remove();
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */             Iterator iter2;
/*     */             String endpointUrl;
/*     */             Iterator iter3;
/* 140 */             if (nodeEndpoints.size() > 0)
/*     */             {
/* 142 */               channelToEndpointMaps.add(nodeEndpoints);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 148 */     return channelToEndpointMaps;
/*     */   }
/*     */ 
/*     */   public void destroy()
/*     */   {
/*     */     try
/*     */     {
/* 158 */       this.clusterChannel.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   void configureBroadcastHandlers()
/*     */   {
/* 171 */     this.broadcastHandlers.add(new RemoteEndpointHandler());
/* 172 */     this.broadcastHandlers.add(new ServiceOperationHandler());
/*     */   }
/*     */ 
/*     */   void addClusterNode(Address address)
/*     */   {
/* 184 */     if (Log.isDebug())
/*     */     {
/* 186 */       Log.getLogger("Service.Cluster").debug("Cluster node from address " + address + " joined the cluster for " + this.clusterId);
/*     */     }
/*     */   }
/*     */ 
/*     */   void removeClusterNode(Address address)
/*     */   {
/* 198 */     this.clusterNodes.remove(address);
/*     */ 
/* 200 */     sendRemoveNodeListener(address);
/*     */ 
/* 202 */     if (Log.isDebug())
/*     */     {
/* 204 */       Log.getLogger("Service.Cluster").debug("Cluster node from address " + address + " abandoned the cluster for " + this.clusterId);
/*     */     }
/*     */   }
/*     */ 
/*     */   Address getJGroupsLocalAddress()
/*     */   {
/* 214 */     return this.clusterChannel.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public Object getLocalAddress()
/*     */   {
/* 219 */     return getJGroupsLocalAddress();
/*     */   }
/*     */ 
/*     */   public void addLocalEndpointForChannel(String serviceType, String destName, String channelId, String endpointUrl, int endpointPort)
/*     */   {
/* 229 */     if (Log.isDebug()) {
/* 230 */       Log.getLogger("Service.Cluster").debug("Adding clustered destination endpoint. cluster-id=" + this.clusterId + " destination=" + destName + " channelId=" + channelId + " endpoint url=" + endpointUrl + " endpointPort=" + endpointPort);
/*     */     }
/*     */ 
/* 234 */     Address myAddr = getJGroupsLocalAddress();
/* 235 */     ClusterNode myNode = getNodeForAddress(myAddr);
/* 236 */     endpointUrl = canonicalizeUrl(channelId, endpointUrl, endpointPort, myNode);
/* 237 */     myNode.addEndpoint(serviceType, destName, channelId, endpointUrl);
/* 238 */     broadcastClusterOperation("addEndpointForChannel", serviceType, destName, channelId, endpointUrl, null);
/*     */   }
/*     */ 
/*     */   void addEndpointForChannel(Address address, String serviceType, String destName, String channelId, String endpointUrl)
/*     */   {
/* 249 */     ClusterNode node = getNodeForAddress(address);
/* 250 */     if (!node.containsEndpoint(serviceType, destName, channelId, endpointUrl))
/*     */     {
/* 252 */       node.addEndpoint(serviceType, destName, channelId, endpointUrl);
/* 253 */       broadcastMyEndpoints(address);
/*     */     }
/*     */   }
/*     */ 
/*     */   void broadcastMyEndpoints(Address address)
/*     */   {
/* 263 */     Vector destination = new Vector();
/* 264 */     destination.add(address);
/* 265 */     ClusterNode myNode = getNodeForAddress(this.clusterChannel.getLocalAddress());
/* 266 */     Map destKeyToChannelMap = myNode.getDestKeyToChannelMap();
/*     */     Iterator destIt;
/*     */     String serviceType;
/*     */     String destName;
/*     */     Map channelEndpoints;
/*     */     Iterator iter;
/* 267 */     synchronized (destKeyToChannelMap)
/*     */     {
/* 269 */       for (destIt = destKeyToChannelMap.keySet().iterator(); destIt.hasNext(); )
/*     */       {
/* 271 */         String destKey = (String)destIt.next();
/* 272 */         int ix = destKey.indexOf(":");
/* 273 */         serviceType = destKey.substring(0, ix);
/* 274 */         destName = destKey.substring(ix + 1);
/* 275 */         channelEndpoints = myNode.getEndpoints(serviceType, destName);
/* 276 */         for (iter = channelEndpoints.keySet().iterator(); iter.hasNext(); )
/*     */         {
/* 278 */           String channelId = (String)iter.next();
/* 279 */           String endpointUrl = (String)channelEndpoints.get(channelId);
/* 280 */           broadcastClusterOperation("addEndpointForChannel", serviceType, destName, channelId, endpointUrl, destination);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void broadcastClusterOperation(String clusterOperation, String serviceType, String destName, String channelId, String endpointUrl, Vector destinations)
/*     */   {
/* 293 */     List operationInfo = new ArrayList();
/* 294 */     operationInfo.add(serviceType);
/* 295 */     operationInfo.add(destName);
/* 296 */     operationInfo.add(channelId);
/* 297 */     operationInfo.add(endpointUrl);
/* 298 */     broadcastOperation(RemoteEndpointHandler.class.getName(), clusterOperation, operationInfo, destinations);
/*     */   }
/*     */ 
/*     */   public void broadcastServiceOperation(String serviceOperation, Object[] params)
/*     */   {
/* 308 */     ArrayList operationInfo = new ArrayList();
/* 309 */     operationInfo.addAll(Arrays.asList(params));
/* 310 */     broadcastOperation(ServiceOperationHandler.class.getName(), serviceOperation, operationInfo, null);
/*     */   }
/*     */ 
/*     */   public void sendPointToPointServiceOperation(String serviceOperation, Object[] params, Object targetAddress)
/*     */   {
/* 320 */     ArrayList operationInfo = new ArrayList();
/* 321 */     operationInfo.addAll(Arrays.asList(params));
/*     */ 
/* 323 */     operationInfo.add(getJGroupsLocalAddress());
/* 324 */     Vector targetDestination = new Vector();
/* 325 */     if (targetAddress != null)
/*     */     {
/* 327 */       targetDestination.add(targetAddress);
/*     */     }
/*     */     else
/*     */     {
/* 331 */       for (int i = 0; i < this.clusterChannel.getView().getMembers().size(); i++)
/*     */       {
/* 333 */         Address a = (Address)this.clusterChannel.getView().getMembers().get(i);
/* 334 */         if (a.equals(getJGroupsLocalAddress()))
/*     */           continue;
/* 336 */         targetDestination.add(a);
/* 337 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 341 */     broadcastOperation(ServiceOperationHandler.class.getName(), serviceOperation, operationInfo, targetDestination);
/*     */   }
/*     */ 
/*     */   public List getMemberAddresses()
/*     */   {
/* 349 */     return this.clusterChannel.getView().getMembers();
/*     */   }
/*     */ 
/*     */   private void broadcastOperation(String handlerClass, String operationName, List operationParams, Vector destinations)
/*     */   {
/*     */     try
/*     */     {
/* 359 */       operationParams.add(0, handlerClass);
/* 360 */       operationParams.add(1, operationName);
/* 361 */       Message operationMessage = new Message(null, getJGroupsLocalAddress(), (Serializable)operationParams);
/*     */ 
/* 363 */       this.broadcastDispatcher.castMessage(destinations, operationMessage, 6, 0L);
/*     */     }
/*     */     catch (IllegalArgumentException iae)
/*     */     {
/* 367 */       String message = iae.getMessage();
/* 368 */       String notSerializableType = null;
/* 369 */       if ((message != null) && (message.startsWith("java.io.NotSerializableException"))) {
/* 370 */         notSerializableType = message.substring(message.indexOf(": ") + 2);
/*     */       }
/* 372 */       if (notSerializableType != null)
/*     */       {
/* 374 */         ClusterException cx = new ClusterException();
/* 375 */         cx.setMessage(10212, new Object[] { this.clusterId, notSerializableType });
/* 376 */         cx.setRootCause(iae);
/* 377 */         throw cx;
/*     */       }
/*     */ 
/* 381 */       ClusterException cx = new ClusterException();
/* 382 */       cx.setMessage(10204, new Object[] { this.clusterId });
/* 383 */       cx.setRootCause(iae);
/* 384 */       throw cx;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 389 */       ClusterException cx = new ClusterException();
/* 390 */       cx.setMessage(10204, new Object[] { this.clusterId });
/* 391 */       cx.setRootCause(e);
/* 392 */       throw cx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object handle(Message msg)
/*     */   {
/* 401 */     if (msg.getSrc() != getJGroupsLocalAddress())
/*     */     {
/* 403 */       List operationInfo = (List)msg.getObject();
/* 404 */       String handlerClass = (String)operationInfo.get(0);
/* 405 */       String operationName = (String)operationInfo.get(1);
/*     */       try
/*     */       {
/* 410 */         FlexContext.setThreadLocalObjects(null, null, this.clusterManager.getMessageBroker(), null, null, null);
/*     */ 
/* 412 */         for (iter = this.broadcastHandlers.iterator(); iter.hasNext(); )
/*     */         {
/* 414 */           BroadcastHandler handler = (BroadcastHandler)iter.next();
/* 415 */           if ((handler.getClass().getName().equals(handlerClass)) && (handler.isSupportedOperation(operationName)))
/*     */           {
/* 417 */             handler.handleBroadcast(msg.getSrc(), operationInfo.subList(1, operationInfo.size()));
/* 418 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */         Iterator iter;
/* 424 */         FlexContext.clearThreadLocalObjects();
/*     */       }
/*     */     }
/* 427 */     return null;
/*     */   }
/*     */ 
/*     */   private ClusterNode getNodeForAddress(Address addr)
/*     */   {
/* 436 */     synchronized (this.clusterNodes)
/*     */     {
/* 438 */       ClusterNode node = (ClusterNode)this.clusterNodes.get(addr);
/* 439 */       if (node == null)
/*     */       {
/* 441 */         node = new ClusterNode(addr);
/* 442 */         this.clusterNodes.put(addr, node);
/*     */       }
/* 444 */       return node;
/*     */     }
/*     */   }
/*     */ 
/*     */   private String canonicalizeUrl(String channelId, String endpointUrl, int endpointPort, ClusterNode myNode)
/*     */   {
/* 454 */     if (endpointUrl.startsWith("/"))
/*     */     {
/* 456 */       ClusterException cx = new ClusterException();
/* 457 */       cx.setMessage(10203, new Object[] { channelId });
/* 458 */       throw cx;
/*     */     }
/*     */ 
/* 461 */     if (endpointUrl.indexOf(":///") != -1)
/*     */     {
/* 463 */       endpointUrl = StringUtils.substitute(endpointUrl, ":///", "://" + myNode.getHost() + "/");
/*     */     }
/*     */ 
/* 467 */     if ((endpointPort != 0) && (endpointUrl.indexOf("" + endpointPort) == -1))
/*     */     {
/* 469 */       StringBuffer sb = new StringBuffer(endpointUrl);
/* 470 */       sb.insert(endpointUrl.indexOf("/", endpointUrl.indexOf("://") + 3), ":" + endpointPort);
/* 471 */       endpointUrl = sb.toString();
/*     */     }
/*     */ 
/* 474 */     return endpointUrl;
/*     */   }
/*     */ 
/*     */   class ServiceOperationHandler
/*     */     implements BroadcastHandler
/*     */   {
/* 506 */     String[] supportedOperations = { "pushMessageFromPeer", "peerSyncAndPush", "requestAdapterState", "receiveAdapterState", "sendSubscriptions", "receiveSubscriptions", "subscribeFromPeer", "pushMessageFromPeerToPeer", "peerSyncAndPushOneToPeer" };
/*     */ 
/*     */     ServiceOperationHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void handleBroadcast(Object sender, List params)
/*     */     {
/*     */       try
/*     */       {
/* 519 */         String serviceType = (String)params.get(1);
/*     */ 
/* 523 */         String destName = (String)params.get(2);
/* 524 */         Service svc = JGroupsCluster.this.clusterManager.getMessageBroker().getServiceByType(serviceType);
/* 525 */         if (svc != null)
/*     */         {
/* 527 */           String methodName = (String)params.get(0);
/* 528 */           Object[] paramValues = params.subList(3, params.size()).toArray();
/* 529 */           Method[] svcMethods = svc.getClass().getMethods();
/*     */ 
/* 534 */           for (int i = 0; i < svcMethods.length; i++)
/*     */           {
/* 536 */             if (!svcMethods[i].getName().equals(methodName))
/*     */               continue;
/* 538 */             svcMethods[i].invoke(svc, paramValues);
/* 539 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (InvocationTargetException ite)
/*     */       {
/* 546 */         Throwable th = ite.getCause();
/* 547 */         if (Log.isError())
/*     */         {
/* 549 */           Log.getLogger("Service.Cluster").error("Error handling message pushed from cluster: " + th);
/* 550 */           Log.getLogger("Service.Cluster").error("Exception=" + ExceptionUtil.toString(th));
/*     */         }
/* 552 */         ClusterException cx = new ClusterException();
/* 553 */         cx.setMessage(10205, new Object[] { JGroupsCluster.access$100(JGroupsCluster.this) });
/* 554 */         cx.setRootCause(th);
/* 555 */         throw cx;
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 559 */         e.printStackTrace();
/* 560 */         ClusterException cx = new ClusterException();
/* 561 */         cx.setMessage(10205, new Object[] { JGroupsCluster.access$100(JGroupsCluster.this) });
/* 562 */         cx.setRootCause(e);
/* 563 */         throw cx;
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isSupportedOperation(String name)
/*     */     {
/* 569 */       for (int i = 0; i < this.supportedOperations.length; i++)
/*     */       {
/* 571 */         if (name.equals(this.supportedOperations[i]))
/*     */         {
/* 573 */           return true;
/*     */         }
/*     */       }
/* 576 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   class RemoteEndpointHandler
/*     */     implements BroadcastHandler
/*     */   {
/*     */     RemoteEndpointHandler()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void handleBroadcast(Object sender, List params)
/*     */     {
/* 487 */       JGroupsCluster.this.addEndpointForChannel((Address)sender, (String)params.get(1), (String)params.get(2), (String)params.get(3), (String)params.get(4));
/*     */     }
/*     */ 
/*     */     public boolean isSupportedOperation(String name)
/*     */     {
/* 494 */       return name.equals("addEndpointForChannel");
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.JGroupsCluster
 * JD-Core Version:    0.6.0
 */
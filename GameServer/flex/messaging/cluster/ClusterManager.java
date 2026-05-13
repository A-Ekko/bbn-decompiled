/*     */ package flex.messaging.cluster;
/*     */ 
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.ClusterSettings;
/*     */ import flex.messaging.config.NetworkSettings;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class ClusterManager
/*     */ {
/*     */   private MessageBroker broker;
/*     */   private Map clusters;
/*     */   private Map clustersForDestination;
/*     */   private Map clusterConfig;
/*     */   private Map clusterSettings;
/*     */   private Map backendSharedForDestination;
/*     */   private Cluster defaultCluster;
/*     */   private String defaultClusterId;
/*     */ 
/*     */   public ClusterManager(MessageBroker broker)
/*     */   {
/*  70 */     this.broker = broker;
/*  71 */     this.clusters = new HashMap();
/*  72 */     this.clusterConfig = new HashMap();
/*  73 */     this.clusterSettings = new HashMap();
/*  74 */     this.clustersForDestination = new HashMap();
/*  75 */     this.backendSharedForDestination = new HashMap();
/*     */   }
/*     */ 
/*     */   public MessageBroker getMessageBroker()
/*     */   {
/*  80 */     return this.broker;
/*     */   }
/*     */ 
/*     */   public Cluster getDefaultCluster()
/*     */   {
/*  85 */     return this.defaultCluster;
/*     */   }
/*     */ 
/*     */   public String getDefaultClusterId()
/*     */   {
/*  90 */     return this.defaultClusterId;
/*     */   }
/*     */ 
/*     */   public void invokeServiceOperation(String serviceType, String destinationName, String operationName, Object[] params)
/*     */   {
/*  96 */     Cluster c = getCluster(serviceType, destinationName);
/*  97 */     ArrayList newParams = new ArrayList(Arrays.asList(params));
/*  98 */     newParams.add(0, serviceType);
/*  99 */     newParams.add(1, destinationName);
/* 100 */     c.broadcastServiceOperation(operationName, newParams.toArray());
/*     */   }
/*     */ 
/*     */   public void invokePeerToPeerOperation(String serviceType, String destinationName, String operationName, Object[] params, Object targetAddress)
/*     */   {
/* 106 */     Cluster c = getCluster(serviceType, destinationName);
/* 107 */     ArrayList newParams = new ArrayList(Arrays.asList(params));
/* 108 */     newParams.add(0, serviceType);
/* 109 */     newParams.add(1, destinationName);
/* 110 */     c.sendPointToPointServiceOperation(operationName, newParams.toArray(), targetAddress);
/*     */   }
/*     */ 
/*     */   public boolean isDestinationClustered(String serviceType, String destinationName)
/*     */   {
/* 115 */     return getCluster(serviceType, destinationName) != null;
/*     */   }
/*     */ 
/*     */   public boolean isBackendShared(String serviceType, String destinationName)
/*     */   {
/* 120 */     String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
/*     */ 
/* 122 */     Boolean shared = (Boolean)this.backendSharedForDestination.get(destKey);
/*     */ 
/* 124 */     if (shared == null) {
/* 125 */       return false;
/*     */     }
/* 127 */     return shared.booleanValue();
/*     */   }
/*     */ 
/*     */   public List getClusterMemberAddresses(String serviceType, String destinationName)
/*     */   {
/* 132 */     Cluster c = getCluster(serviceType, destinationName);
/* 133 */     if (c == null) {
/* 134 */       return Collections.EMPTY_LIST;
/*     */     }
/* 136 */     return c.getMemberAddresses();
/*     */   }
/*     */ 
/*     */   public void prepareCluster(ClusterSettings settings)
/*     */   {
/* 141 */     if (settings.getPropsFileName() == null)
/*     */     {
/* 143 */       ClusterException cx = new ClusterException();
/* 144 */       cx.setMessage(10201, new Object[] { settings.getClusterName(), settings.getPropsFileName() });
/* 145 */       throw cx;
/*     */     }
/*     */ 
/*     */     InputStream propsFile;
/*     */     try
/*     */     {
/* 152 */       propsFile = this.broker.resolveInternalPath(settings.getPropsFileName());
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 156 */       ClusterException cx = new ClusterException();
/* 157 */       cx.setMessage(10208, new Object[] { settings.getPropsFileName() });
/* 158 */       cx.setRootCause(t);
/* 159 */       throw cx;
/*     */     }
/*     */ 
/* 162 */     if (propsFile == null)
/*     */     {
/* 164 */       ClusterException cx = new ClusterException();
/* 165 */       cx.setMessage(10208, new Object[] { settings.getPropsFileName() });
/* 166 */       throw cx;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 172 */       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/* 173 */       factory.setNamespaceAware(false);
/* 174 */       factory.setValidating(false);
/* 175 */       DocumentBuilder builder = factory.newDocumentBuilder();
/* 176 */       Document doc = builder.parse(propsFile);
/* 177 */       if (settings.isDefault())
/*     */       {
/* 179 */         this.defaultClusterId = settings.getClusterName();
/*     */       }
/* 181 */       this.clusterConfig.put(settings.getClusterName(), doc.getDocumentElement());
/* 182 */       this.clusterSettings.put(settings.getClusterName(), settings);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/* 186 */       ClusterException cx = new ClusterException();
/* 187 */       cx.setMessage(10213);
/* 188 */       cx.setRootCause(ex);
/* 189 */       throw cx;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object getLocalAddress(String serviceType, String destinationName)
/*     */   {
/* 197 */     Cluster c = getCluster(serviceType, destinationName);
/* 198 */     if (c == null) {
/* 199 */       return null;
/*     */     }
/* 201 */     return c.getLocalAddress();
/*     */   }
/*     */ 
/*     */   public Cluster getClusterById(String clusterId)
/*     */   {
/* 206 */     return (Cluster)this.clusters.get(clusterId);
/*     */   }
/*     */ 
/*     */   public Cluster getCluster(String serviceType, String destinationName)
/*     */   {
/* 211 */     Cluster cluster = null;
/*     */     try
/*     */     {
/* 214 */       String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
/*     */ 
/* 216 */       cluster = (Cluster)this.clustersForDestination.get(destKey);
/*     */ 
/* 218 */       if (cluster == null)
/* 219 */         cluster = this.defaultCluster;
/*     */     }
/*     */     catch (NoClassDefFoundError nex)
/*     */     {
/* 223 */       ClusterException cx = new ClusterException();
/* 224 */       cx.setMessage(10202, new Object[] { destinationName });
/* 225 */       cx.setRootCause(nex);
/* 226 */       throw cx;
/*     */     }
/* 228 */     return cluster;
/*     */   }
/*     */ 
/*     */   public void destroyClusters()
/*     */   {
/* 233 */     for (Iterator iter = this.clusters.keySet().iterator(); iter.hasNext(); )
/*     */     {
/* 235 */       Cluster cluster = (Cluster)this.clusters.get(iter.next());
/* 236 */       cluster.destroy();
/* 237 */       iter.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clusterDestinationChannel(String clusterId, String serviceType, String destinationName, String channelId, String endpointUrl, int endpointPort, boolean sharedBackend)
/*     */   {
/* 244 */     Cluster cluster = getClusterById(clusterId);
/* 245 */     String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
/* 246 */     if (cluster == null)
/*     */     {
/* 248 */       if (!this.clusterConfig.containsKey(clusterId))
/*     */       {
/* 250 */         ClusterException cx = new ClusterException();
/* 251 */         cx.setMessage(10207, new Object[] { destinationName, clusterId });
/* 252 */         throw cx;
/*     */       }
/* 254 */       cluster = createCluster(clusterId, serviceType, destinationName);
/*     */     }
/*     */     else
/*     */     {
/* 258 */       this.clustersForDestination.put(destKey, cluster);
/*     */     }
/* 260 */     this.backendSharedForDestination.put(destKey, sharedBackend ? Boolean.TRUE : Boolean.FALSE);
/*     */ 
/* 262 */     if (cluster.getURLLoadBalancing())
/* 263 */       cluster.addLocalEndpointForChannel(serviceType, destinationName, channelId, endpointUrl, endpointPort);
/*     */   }
/*     */ 
/*     */   public void clusterDestination(Destination destination)
/*     */   {
/* 269 */     String clusterId = destination.getNetworkSettings().getClusterId();
/* 270 */     String serviceType = destination.getServiceType();
/* 271 */     String destinationName = destination.getId();
/* 272 */     boolean sharedBackend = destination.getNetworkSettings().isSharedBackend();
/* 273 */     List channelIds = destination.getChannels();
/*     */ 
/* 275 */     if (clusterId == null) {
/* 276 */       clusterId = getDefaultClusterId();
/*     */     }
/* 278 */     ClusterSettings cls = (ClusterSettings)this.clusterSettings.get(clusterId);
/*     */ 
/* 280 */     if (cls == null)
/*     */     {
/* 282 */       ClusterException ce = new ClusterException();
/* 283 */       ce.setMessage(10217, new Object[] { destination.getId(), clusterId });
/* 284 */       throw ce;
/*     */     }
/*     */ 
/* 287 */     for (Iterator iter = channelIds.iterator(); iter.hasNext(); )
/*     */     {
/* 289 */       String channelId = (String)iter.next();
/* 290 */       Endpoint endpoint = this.broker.getEndpoint(channelId);
/* 291 */       String endpointUrl = endpoint.getUrl();
/* 292 */       int endpointPort = endpoint.getPort();
/*     */ 
/* 298 */       if (cls.getURLLoadBalancing())
/*     */       {
/* 301 */         int tokenStart = endpointUrl.indexOf("{");
/* 302 */         if (tokenStart != -1)
/*     */         {
/* 304 */           int tokenEnd = endpointUrl.indexOf("}", tokenStart);
/* 305 */           if (tokenEnd == -1)
/* 306 */             tokenEnd = endpointUrl.length();
/*     */           else {
/* 308 */             tokenEnd++;
/*     */           }
/* 310 */           ClusterException ce = new ClusterException();
/* 311 */           ce.setMessage(10209, new Object[] { destination.getId(), channelId, endpointUrl.substring(tokenStart, tokenEnd) });
/* 312 */           throw ce;
/*     */         }
/*     */       }
/*     */ 
/* 316 */       clusterDestinationChannel(clusterId, serviceType, destinationName, channelId, endpointUrl, endpointPort, sharedBackend);
/*     */     }
/*     */   }
/*     */ 
/*     */   public List getEndpointsForDestination(String serviceType, String destinationName)
/*     */   {
/* 322 */     Cluster c = getCluster(serviceType, destinationName);
/* 323 */     if (c != null)
/*     */     {
/* 325 */       return c.getAllEndpoints(serviceType, destinationName);
/*     */     }
/* 327 */     return null;
/*     */   }
/*     */ 
/*     */   private Cluster createCluster(String clusterId, String serviceType, String destinationName)
/*     */   {
/* 332 */     String destKey = Cluster.getClusterDestinationKey(serviceType, destinationName);
/* 333 */     Element propsFile = (Element)this.clusterConfig.get(clusterId);
/* 334 */     ClusterSettings cls = (ClusterSettings)this.clusterSettings.get(clusterId);
/* 335 */     Cluster cluster = null;
/* 336 */     Class clusterClass = ClassUtil.createClass(cls.getImplementationClass());
/* 337 */     Constructor clusterConstructor = null;
/*     */     try
/*     */     {
/* 340 */       clusterConstructor = clusterClass.getConstructor(new Class[] { ClusterManager.class, String.class, Element.class });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 344 */       ClusterException cx = new ClusterException();
/* 345 */       cx.setMessage(10210);
/* 346 */       cx.setRootCause(e);
/* 347 */       throw cx;
/*     */     }
/*     */     try
/*     */     {
/* 351 */       cluster = (Cluster)clusterConstructor.newInstance(new Object[] { this, clusterId, propsFile });
/* 352 */       cluster.setURLLoadBalancing(cls.getURLLoadBalancing());
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 356 */       ClusterException cx = new ClusterException();
/* 357 */       cx.setMessage(10211);
/* 358 */       cx.setRootCause(e);
/* 359 */       throw cx;
/*     */     }
/* 361 */     this.clustersForDestination.put(destKey, cluster);
/* 362 */     this.clusters.put(clusterId, cluster);
/*     */ 
/* 364 */     if ((this.defaultClusterId != null) && (this.defaultClusterId.equals(clusterId))) {
/* 365 */       this.defaultCluster = cluster;
/*     */     }
/* 367 */     return cluster;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.ClusterManager
 * JD-Core Version:    0.6.0
 */
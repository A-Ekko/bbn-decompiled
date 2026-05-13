/*     */ package flex.messaging.cluster;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.jgroups.Address;
/*     */ import org.jgroups.stack.IpAddress;
/*     */ 
/*     */ public class ClusterNode
/*     */ {
/*     */   private final String host;
/*     */   private final Map destKeyToChannelMap;
/*     */ 
/*     */   ClusterNode(Address address)
/*     */   {
/*  43 */     IpAddress addr = (IpAddress)address;
/*  44 */     this.host = addr.getIpAddress().getCanonicalHostName();
/*  45 */     this.destKeyToChannelMap = new HashMap();
/*     */   }
/*     */ 
/*     */   String getHost()
/*     */   {
/*  50 */     return this.host;
/*     */   }
/*     */ 
/*     */   Map getDestKeyToChannelMap()
/*     */   {
/*  55 */     return this.destKeyToChannelMap;
/*     */   }
/*     */ 
/*     */   Map getEndpoints(String serviceType, String destName)
/*     */   {
/*  60 */     String destKey = serviceType + ":" + destName;
/*  61 */     synchronized (this.destKeyToChannelMap)
/*     */     {
/*  63 */       Map channelEndpoints = (Map)this.destKeyToChannelMap.get(destKey);
/*  64 */       if (channelEndpoints == null)
/*     */       {
/*  66 */         channelEndpoints = new HashMap();
/*  67 */         this.destKeyToChannelMap.put(destKey, channelEndpoints);
/*     */       }
/*  69 */       return channelEndpoints;
/*     */     }
/*     */   }
/*     */ 
/*     */   void addEndpoint(String serviceType, String destName, String channelId, String endpointUrl)
/*     */   {
/*  75 */     String destKey = serviceType + ":" + destName;
/*  76 */     synchronized (this.destKeyToChannelMap)
/*     */     {
/*  78 */       Map channelEndpoints = getEndpoints(serviceType, destName);
/*  79 */       channelEndpoints.put(channelId, endpointUrl);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean containsEndpoint(String serviceType, String destName, String channelId, String endpointUrl)
/*     */   {
/*  85 */     Map channelEndpoints = getEndpoints(serviceType, destName);
/*  86 */     return (channelEndpoints.containsKey(channelId)) && (channelEndpoints.get(channelId).equals(endpointUrl));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  91 */     StringBuffer sb = new StringBuffer("ClusterNode[");
/*     */     Iterator iter;
/*  92 */     synchronized (this.destKeyToChannelMap)
/*     */     {
/*  94 */       for (iter = this.destKeyToChannelMap.keySet().iterator(); iter.hasNext(); )
/*     */       {
/*  96 */         String destKey = (String)iter.next();
/*  97 */         sb.append(" channels for ");
/*  98 */         sb.append(destKey);
/*  99 */         sb.append("(");
/* 100 */         Map channelEndpoints = (Map)this.destKeyToChannelMap.get(destKey);
/* 101 */         for (Iterator dit = channelEndpoints.keySet().iterator(); dit.hasNext(); )
/*     */         {
/* 103 */           String channelId = (String)dit.next();
/* 104 */           String endpointUrl = (String)channelEndpoints.get(channelId);
/* 105 */           sb.append(channelId);
/* 106 */           sb.append("=");
/* 107 */           sb.append(endpointUrl);
/* 108 */           if (dit.hasNext())
/* 109 */             sb.append(", ");
/*     */         }
/* 111 */         sb.append(")");
/*     */       }
/*     */     }
/* 114 */     sb.append(" ]");
/* 115 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.ClusterNode
 * JD-Core Version:    0.6.0
 */
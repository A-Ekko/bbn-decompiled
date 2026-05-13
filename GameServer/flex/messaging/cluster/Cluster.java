/*    */ package flex.messaging.cluster;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ public abstract class Cluster
/*    */ {
/*    */   public static final String LOG_CATEGORY = "Service.Cluster";
/* 36 */   List removeNodeListeners = Collections.synchronizedList(new ArrayList());
/*    */   boolean def;
/*    */   boolean urlLoadBalancing;
/*    */ 
/*    */   static String getClusterDestinationKey(String serviceType, String destinationName)
/*    */   {
/* 50 */     StringBuffer sb = new StringBuffer();
/* 51 */     sb.append(serviceType);
/* 52 */     sb.append(":");
/* 53 */     sb.append(destinationName);
/* 54 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   public void addRemoveNodeListener(RemoveNodeListener listener)
/*    */   {
/* 59 */     this.removeNodeListeners.add(listener);
/*    */   }
/*    */ 
/*    */   protected void sendRemoveNodeListener(Object address)
/*    */   {
/* 64 */     synchronized (this.removeNodeListeners) {
/* 65 */       for (int i = 0; i < this.removeNodeListeners.size(); i++)
/* 66 */         ((RemoveNodeListener)this.removeNodeListeners.get(i)).removeClusterNode(address);
/*    */     }
/*    */   }
/*    */ 
/*    */   public boolean isDefault()
/*    */   {
/* 76 */     return this.def;
/*    */   }
/*    */ 
/*    */   public void setDefault(boolean d)
/*    */   {
/* 81 */     this.def = d;
/*    */   }
/*    */ 
/*    */   public boolean getURLLoadBalancing()
/*    */   {
/* 86 */     return this.urlLoadBalancing;
/*    */   }
/*    */ 
/*    */   public void setURLLoadBalancing(boolean u)
/*    */   {
/* 91 */     this.urlLoadBalancing = u;
/*    */   }
/*    */ 
/*    */   public abstract void destroy();
/*    */ 
/*    */   public abstract List getAllEndpoints(String paramString1, String paramString2);
/*    */ 
/*    */   public abstract List getMemberAddresses();
/*    */ 
/*    */   public abstract Object getLocalAddress();
/*    */ 
/*    */   public abstract void broadcastServiceOperation(String paramString, Object[] paramArrayOfObject);
/*    */ 
/*    */   public abstract void sendPointToPointServiceOperation(String paramString, Object[] paramArrayOfObject, Object paramObject);
/*    */ 
/*    */   public abstract void addLocalEndpointForChannel(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt);
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.Cluster
 * JD-Core Version:    0.6.0
 */
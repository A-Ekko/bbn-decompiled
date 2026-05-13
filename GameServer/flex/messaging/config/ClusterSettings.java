/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class ClusterSettings
/*     */ {
/*     */   public static final String CLUSTER_ELEMENT = "cluster";
/*     */   public static final String REF_ATTR = "ref";
/*     */   public static final String SHARED_BACKEND_ATTR = "shared-backend";
/*     */   public static final String DEFAULT_ELEMENT = "default";
/*     */   public static final String URL_LOAD_BALANCING = "url-load-balancing";
/*     */   public static final String IMPLEMENTATION_CLASS = "class";
/*     */   public static final String JGROUPS_CLUSTER = "flex.messaging.cluster.JGroupsCluster";
/*     */   private String clusterName;
/*     */   private String propsFileName;
/*     */   private String implementationClass;
/*     */   private boolean def;
/*     */   private boolean urlLoadBalancing;
/*     */ 
/*     */   public ClusterSettings()
/*     */   {
/*  46 */     this.def = false;
/*  47 */     this.urlLoadBalancing = true;
/*  48 */     this.implementationClass = "flex.messaging.cluster.JGroupsCluster";
/*     */   }
/*     */ 
/*     */   public String getClusterName()
/*     */   {
/*  58 */     return this.clusterName;
/*     */   }
/*     */ 
/*     */   public void setClusterName(String clusterName)
/*     */   {
/*  68 */     this.clusterName = clusterName;
/*     */   }
/*     */ 
/*     */   public boolean isDefault()
/*     */   {
/*  78 */     return this.def;
/*     */   }
/*     */ 
/*     */   public void setDefault(boolean def)
/*     */   {
/*  88 */     this.def = def;
/*     */   }
/*     */ 
/*     */   public String getPropsFileName()
/*     */   {
/*  98 */     return this.propsFileName;
/*     */   }
/*     */ 
/*     */   public void setPropsFileName(String propsFileName)
/*     */   {
/* 108 */     this.propsFileName = propsFileName;
/*     */   }
/*     */ 
/*     */   public boolean getURLLoadBalancing()
/*     */   {
/* 118 */     return this.urlLoadBalancing;
/*     */   }
/*     */ 
/*     */   public void setURLLoadBalancing(boolean ulb)
/*     */   {
/* 128 */     this.urlLoadBalancing = ulb;
/*     */   }
/*     */ 
/*     */   public void setImplementationClass(String className)
/*     */   {
/* 140 */     this.implementationClass = className;
/*     */   }
/*     */ 
/*     */   public String getImplementationClass()
/*     */   {
/* 152 */     return this.implementationClass;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ClusterSettings
 * JD-Core Version:    0.6.0
 */
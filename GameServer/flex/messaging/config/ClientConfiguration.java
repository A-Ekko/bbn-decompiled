/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ClientConfiguration
/*     */   implements ServicesConfiguration
/*     */ {
/*     */   protected final Map channelSettings;
/*     */   protected final List defaultChannels;
/*     */   protected final List serviceSettings;
/*     */   protected LoggingSettings loggingSettings;
/*     */   protected Map configPaths;
/*     */   protected final Map clusterSettings;
/*     */ 
/*     */   public ClientConfiguration()
/*     */   {
/*  20 */     this.channelSettings = new HashMap();
/*  21 */     this.defaultChannels = new ArrayList(4);
/*  22 */     this.clusterSettings = new HashMap();
/*  23 */     this.serviceSettings = new ArrayList();
/*  24 */     this.configPaths = new HashMap();
/*     */   }
/*     */ 
/*     */   public void addChannelSettings(String id, ChannelSettings settings)
/*     */   {
/*  33 */     this.channelSettings.put(id, settings);
/*     */   }
/*     */ 
/*     */   public ChannelSettings getChannelSettings(String ref)
/*     */   {
/*  38 */     return (ChannelSettings)this.channelSettings.get(ref);
/*     */   }
/*     */ 
/*     */   public Map getAllChannelSettings()
/*     */   {
/*  43 */     return this.channelSettings;
/*     */   }
/*     */ 
/*     */   public void addDefaultChannel(String id)
/*     */   {
/*  51 */     this.defaultChannels.add(id);
/*     */   }
/*     */ 
/*     */   public List getDefaultChannels()
/*     */   {
/*  56 */     return this.defaultChannels;
/*     */   }
/*     */ 
/*     */   public void addServiceSettings(ServiceSettings settings)
/*     */   {
/*  65 */     this.serviceSettings.add(settings);
/*     */   }
/*     */ 
/*     */   public ServiceSettings getServiceSettings(String serviceType)
/*     */   {
/*  70 */     for (Iterator iter = this.serviceSettings.iterator(); iter.hasNext(); )
/*     */     {
/*  72 */       ServiceSettings serviceSettings = (ServiceSettings)iter.next();
/*  73 */       if (serviceSettings.getId().equals(serviceType))
/*  74 */         return serviceSettings;
/*     */     }
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */   public List getAllServiceSettings()
/*     */   {
/*  81 */     return this.serviceSettings;
/*     */   }
/*     */ 
/*     */   public void addClusterSettings(ClusterSettings settings)
/*     */   {
/*     */     Iterator it;
/*  90 */     if (settings.isDefault())
/*     */     {
/*  92 */       for (it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */       {
/*  94 */         ClusterSettings cs = (ClusterSettings)it.next();
/*     */ 
/*  96 */         if (cs.isDefault())
/*     */         {
/*  98 */           ConfigurationException cx = new ConfigurationException();
/*  99 */           cx.setMessage(10214, new Object[] { settings.getClusterName(), cs.getClusterName() });
/* 100 */           throw cx;
/*     */         }
/*     */       }
/*     */     }
/* 104 */     if (this.clusterSettings.containsKey(settings.getClusterName()))
/*     */     {
/* 106 */       ConfigurationException cx = new ConfigurationException();
/* 107 */       cx.setMessage(10206, new Object[] { settings.getClusterName() });
/* 108 */       throw cx;
/*     */     }
/* 110 */     this.clusterSettings.put(settings.getClusterName(), settings);
/*     */   }
/*     */ 
/*     */   public ClusterSettings getClusterSettings(String clusterId)
/*     */   {
/* 115 */     for (Iterator it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */     {
/* 117 */       ClusterSettings cs = (ClusterSettings)it.next();
/* 118 */       if (cs.getClusterName() == clusterId)
/* 119 */         return cs;
/* 120 */       if ((cs.getClusterName() != null) && (cs.getClusterName().equals(clusterId)))
/* 121 */         return cs;
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */ 
/*     */   public ClusterSettings getDefaultCluster()
/*     */   {
/* 128 */     for (Iterator it = this.clusterSettings.values().iterator(); it.hasNext(); )
/*     */     {
/* 130 */       ClusterSettings cs = (ClusterSettings)it.next();
/* 131 */       if (cs.isDefault())
/* 132 */         return cs;
/*     */     }
/* 134 */     return null;
/*     */   }
/*     */ 
/*     */   public void setLoggingSettings(LoggingSettings settings)
/*     */   {
/* 142 */     this.loggingSettings = settings;
/*     */   }
/*     */ 
/*     */   public LoggingSettings getLoggingSettings()
/*     */   {
/* 147 */     return this.loggingSettings;
/*     */   }
/*     */ 
/*     */   public void addConfigPath(String path, long modified)
/*     */   {
/* 153 */     this.configPaths.put(path, new Long(modified));
/*     */   }
/*     */ 
/*     */   public Map getConfigPaths()
/*     */   {
/* 158 */     return this.configPaths;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ClientConfiguration
 * JD-Core Version:    0.6.0
 */
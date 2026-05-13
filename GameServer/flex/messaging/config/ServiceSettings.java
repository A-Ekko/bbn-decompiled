/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ServiceSettings extends PropertiesSettings
/*     */ {
/*     */   private final String id;
/*     */   private String sourceFile;
/*     */   private String className;
/*     */   private AdapterSettings defaultAdapterSettings;
/*     */   private final Map adapterSettings;
/*     */   private final List defaultChannels;
/*     */   private final Map destinationSettings;
/*     */   private SecurityConstraint securityConstraint;
/*     */ 
/*     */   public ServiceSettings(String id)
/*     */   {
/*  55 */     this.id = id;
/*  56 */     this.destinationSettings = new HashMap();
/*  57 */     this.adapterSettings = new HashMap(2);
/*  58 */     this.defaultChannels = new ArrayList(4);
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  63 */     return this.id;
/*     */   }
/*     */ 
/*     */   String getSourceFile()
/*     */   {
/*  68 */     return this.sourceFile;
/*     */   }
/*     */ 
/*     */   void setSourceFile(String sourceFile)
/*     */   {
/*  73 */     this.sourceFile = sourceFile;
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  78 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void setClassName(String name)
/*     */   {
/*  83 */     this.className = name;
/*     */   }
/*     */ 
/*     */   public AdapterSettings getDefaultAdapter()
/*     */   {
/*  91 */     return this.defaultAdapterSettings;
/*     */   }
/*     */ 
/*     */   public AdapterSettings getAdapterSettings(String id)
/*     */   {
/*  96 */     return (AdapterSettings)this.adapterSettings.get(id);
/*     */   }
/*     */ 
/*     */   public Map getAllAdapterSettings()
/*     */   {
/* 101 */     return this.adapterSettings;
/*     */   }
/*     */ 
/*     */   public void addAdapterSettings(AdapterSettings a)
/*     */   {
/* 106 */     this.adapterSettings.put(a.getId(), a);
/* 107 */     if (a.isDefault())
/*     */     {
/* 109 */       this.defaultAdapterSettings = a;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addDefaultChannel(ChannelSettings c)
/*     */   {
/* 118 */     this.defaultChannels.add(c);
/*     */   }
/*     */ 
/*     */   public List getDefaultChannels()
/*     */   {
/* 123 */     return this.defaultChannels;
/*     */   }
/*     */ 
/*     */   public SecurityConstraint getConstraint()
/*     */   {
/* 139 */     return this.securityConstraint;
/*     */   }
/*     */ 
/*     */   public void setConstraint(SecurityConstraint sc)
/*     */   {
/* 152 */     this.securityConstraint = sc;
/*     */   }
/*     */ 
/*     */   public Map getDestinationSettings()
/*     */   {
/* 160 */     return this.destinationSettings;
/*     */   }
/*     */ 
/*     */   public void addDestinationSettings(DestinationSettings dest)
/*     */   {
/* 165 */     this.destinationSettings.put(dest.getId(), dest);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.ServiceSettings
 * JD-Core Version:    0.6.0
 */
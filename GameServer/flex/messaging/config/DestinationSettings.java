/*     */ package flex.messaging.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class DestinationSettings extends PropertiesSettings
/*     */ {
/*     */   public static final String SERVER_ELEMENT = "server";
/*     */   private final String id;
/*     */   private String sourceFile;
/*     */   private List channelSettings;
/*     */   private AdapterSettings adapterSettings;
/*     */   private SecurityConstraint constraint;
/*     */ 
/*     */   public DestinationSettings(String id)
/*     */   {
/*  60 */     this.id = id;
/*  61 */     this.channelSettings = new ArrayList();
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  71 */     return this.id;
/*     */   }
/*     */ 
/*     */   String getSourceFile()
/*     */   {
/*  80 */     return this.sourceFile;
/*     */   }
/*     */ 
/*     */   void setSourceFile(String sourceFile)
/*     */   {
/*  89 */     this.sourceFile = sourceFile;
/*     */   }
/*     */ 
/*     */   public void addChannelSettings(ChannelSettings c)
/*     */   {
/* 107 */     if (c != null)
/*     */     {
/* 109 */       this.channelSettings.add(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setChannelSettings(List settings)
/*     */   {
/* 121 */     this.channelSettings = settings;
/*     */   }
/*     */ 
/*     */   public List getChannelSettings()
/*     */   {
/* 132 */     return this.channelSettings;
/*     */   }
/*     */ 
/*     */   public SecurityConstraint getConstraint()
/*     */   {
/* 148 */     return this.constraint;
/*     */   }
/*     */ 
/*     */   public void setConstraint(SecurityConstraint sc)
/*     */   {
/* 161 */     this.constraint = sc;
/*     */   }
/*     */ 
/*     */   public void setAdapterSettings(AdapterSettings a)
/*     */   {
/* 177 */     this.adapterSettings = a;
/*     */   }
/*     */ 
/*     */   public AdapterSettings getAdapterSettings()
/*     */   {
/* 189 */     return this.adapterSettings;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.DestinationSettings
 * JD-Core Version:    0.6.0
 */
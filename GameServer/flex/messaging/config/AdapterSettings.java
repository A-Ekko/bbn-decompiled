/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class AdapterSettings extends PropertiesSettings
/*     */ {
/*     */   private final String id;
/*     */   private String sourceFile;
/*     */   private String className;
/*     */   private boolean defaultAdapter;
/*     */ 
/*     */   public AdapterSettings(String id)
/*     */   {
/*  51 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  61 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  72 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void setClassName(String name)
/*     */   {
/*  84 */     this.className = name;
/*     */   }
/*     */ 
/*     */   public boolean isDefault()
/*     */   {
/*  96 */     return this.defaultAdapter;
/*     */   }
/*     */ 
/*     */   public void setDefault(boolean b)
/*     */   {
/* 111 */     this.defaultAdapter = b;
/*     */   }
/*     */ 
/*     */   String getSourceFile()
/*     */   {
/* 120 */     return this.sourceFile;
/*     */   }
/*     */ 
/*     */   void setSourceFile(String file)
/*     */   {
/* 129 */     this.sourceFile = file;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.AdapterSettings
 * JD-Core Version:    0.6.0
 */
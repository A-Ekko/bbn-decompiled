/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ 
/*     */ public class FactoryInstance
/*     */ {
/*     */   private static final int INVALID_SCOPE = 10653;
/*     */   private FlexFactory factory;
/*     */   private String id;
/*  37 */   private String scope = "request";
/*     */   private String source;
/*     */   private ConfigMap properties;
/*     */ 
/*     */   public FactoryInstance(FlexFactory factory, String id, ConfigMap properties)
/*     */   {
/*  57 */     this.factory = factory;
/*  58 */     this.id = id;
/*  59 */     this.properties = properties;
/*     */   }
/*     */ 
/*     */   public String getId()
/*     */   {
/*  67 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setScope(String scope)
/*     */   {
/*  76 */     this.scope = scope;
/*     */ 
/*  78 */     if ((!"session".equals(scope)) && (!"application".equals(scope)) && (!"request".equals(scope)))
/*     */     {
/*  84 */       ConfigurationException ex = new ConfigurationException();
/*  85 */       ex.setMessage(10653, new Object[] { this.id, "'request', 'session', or 'application'" });
/*  86 */       throw ex;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getScope()
/*     */   {
/*  92 */     return this.scope;
/*     */   }
/*     */ 
/*     */   public void setSource(String source)
/*     */   {
/* 102 */     this.source = source;
/*     */   }
/*     */ 
/*     */   public String getSource() {
/* 106 */     return this.source;
/*     */   }
/*     */ 
/*     */   public Class getInstanceClass()
/*     */   {
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   public ConfigMap getProperties()
/*     */   {
/* 139 */     return this.properties;
/*     */   }
/*     */ 
/*     */   public Object lookup()
/*     */   {
/* 150 */     return this.factory.lookup(this);
/*     */   }
/*     */ 
/*     */   public void operationComplete(Object instance)
/*     */   {
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FactoryInstance
 * JD-Core Version:    0.6.0
 */
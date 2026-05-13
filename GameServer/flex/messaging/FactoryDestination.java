/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.ConfigurationException;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.services.Service;
/*     */ 
/*     */ public abstract class FactoryDestination extends Destination
/*     */ {
/*     */   private static final String FACTORY = "factory";
/*     */   private static final String DEFAULT_FACTORY = "java";
/*  31 */   private static int INVALID_FACTORY = 11103;
/*  32 */   private static int FACTORY_CANNOT_BE_RETURNED = 11118;
/*     */   private FlexFactory factory;
/*     */   private String source;
/*  37 */   private String scope = "request";
/*     */ 
/*  40 */   private String factoryId = "java";
/*     */   private FactoryInstance factoryInstance;
/*     */   private ConfigMap factoryProperties;
/*     */ 
/*     */   public FactoryDestination()
/*     */   {
/*  55 */     this(false);
/*     */   }
/*     */ 
/*     */   public FactoryDestination(boolean enableManagement)
/*     */   {
/*  66 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*  82 */     super.initialize(id, properties);
/*     */ 
/*  84 */     if ((properties == null) || (properties.size() == 0)) {
/*  85 */       return;
/*     */     }
/*     */ 
/*  88 */     this.factoryProperties = properties;
/*     */ 
/*  90 */     this.factoryId = properties.getPropertyAsString("factory", this.factoryId);
/*  91 */     this.scope = properties.getPropertyAsString("scope", this.scope);
/*  92 */     this.source = properties.getPropertyAsString("source", this.source);
/*     */ 
/*  94 */     if (this.source == null)
/*  95 */       this.source = getId();
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 104 */     if (isValid()) {
/* 105 */       return;
/*     */     }
/* 107 */     super.validate();
/*     */ 
/* 109 */     if (this.factory == null)
/*     */     {
/* 111 */       if (this.factoryId == null)
/*     */       {
/* 113 */         this.factoryId = "java";
/*     */       }
/* 115 */       MessageBroker broker = getService().getMessageBroker();
/* 116 */       FlexFactory f = broker.getFactory(this.factoryId);
/* 117 */       if (f == null)
/*     */       {
/* 119 */         ConfigurationException ex = new ConfigurationException();
/* 120 */         ex.setMessage(INVALID_FACTORY, new Object[] { getId(), this.factoryId });
/* 121 */         throw ex;
/*     */       }
/* 123 */       this.factory = f;
/*     */     }
/*     */ 
/* 126 */     if (this.scope == null) {
/* 127 */       this.scope = "request";
/*     */     }
/* 129 */     if (this.source == null)
/* 130 */       this.source = getId();
/*     */   }
/*     */ 
/*     */   public FlexFactory getFactory()
/*     */   {
/* 146 */     if (this.factory == null)
/*     */     {
/* 148 */       if (this.factoryId == null)
/*     */       {
/* 150 */         this.factoryId = "java";
/*     */       }
/* 152 */       if (getService() == null)
/*     */       {
/* 155 */         ConfigurationException ex = new ConfigurationException();
/* 156 */         ex.setMessage(FACTORY_CANNOT_BE_RETURNED, new Object[] { "Service" });
/* 157 */         throw ex;
/*     */       }
/* 159 */       if (getService().getMessageBroker() == null)
/*     */       {
/* 162 */         ConfigurationException ex = new ConfigurationException();
/* 163 */         ex.setMessage(FACTORY_CANNOT_BE_RETURNED, new Object[] { "MessageBroker" });
/* 164 */         throw ex;
/*     */       }
/* 166 */       MessageBroker broker = getService().getMessageBroker();
/* 167 */       FlexFactory f = broker.getFactory(this.factoryId);
/* 168 */       if (f == null)
/*     */       {
/* 170 */         ConfigurationException ex = new ConfigurationException();
/* 171 */         ex.setMessage(INVALID_FACTORY, new Object[] { getId(), this.factoryId });
/* 172 */         throw ex;
/*     */       }
/* 174 */       this.factory = f;
/*     */     }
/* 176 */     return this.factory;
/*     */   }
/*     */ 
/*     */   public void setFactory(String id)
/*     */   {
/* 188 */     if (isStarted())
/*     */     {
/* 190 */       MessageBroker broker = getService().getMessageBroker();
/* 191 */       FlexFactory factory = broker.getFactory(id);
/* 192 */       if (factory == null)
/*     */       {
/* 194 */         ConfigurationException ex = new ConfigurationException();
/* 195 */         ex.setMessage(INVALID_FACTORY, new Object[] { getId(), factory });
/* 196 */         throw ex;
/*     */       }
/* 198 */       setFactory(factory);
/*     */     }
/* 200 */     this.factoryId = id;
/*     */   }
/*     */ 
/*     */   public void setFactory(FlexFactory factory)
/*     */   {
/* 210 */     this.factory = factory;
/*     */   }
/*     */ 
/*     */   public FactoryInstance getFactoryInstance()
/*     */   {
/* 223 */     return getFactoryInstance(this.factoryProperties);
/*     */   }
/*     */ 
/*     */   private FactoryInstance getFactoryInstance(ConfigMap properties)
/*     */   {
/* 234 */     if (this.factoryInstance == null) {
/* 235 */       this.factoryInstance = createFactoryInstance(properties);
/*     */     }
/* 237 */     return this.factoryInstance;
/*     */   }
/*     */ 
/*     */   private FactoryInstance createFactoryInstance(ConfigMap properties)
/*     */   {
/* 247 */     if (properties == null) {
/* 248 */       properties = new ConfigMap();
/*     */     }
/* 250 */     properties.put("source", this.source);
/* 251 */     properties.put("scope", this.scope);
/* 252 */     FactoryInstance factoryInstance = getFactory().createFactoryInstance(getId(), properties);
/* 253 */     return factoryInstance;
/*     */   }
/*     */ 
/*     */   public String getScope()
/*     */   {
/* 263 */     return this.scope;
/*     */   }
/*     */ 
/*     */   public void setScope(String scope)
/*     */   {
/* 275 */     if (this.factoryInstance != null)
/*     */     {
/* 277 */       if (("application".equals(this.scope)) && (!"application".equals(scope)))
/*     */       {
/* 280 */         if (Log.isWarn()) {
/* 281 */           Log.getLogger(getLogCategory()).warn("Current scope is application and it cannot be changed to " + scope + " once factory instance is initialized.");
/*     */         }
/*     */ 
/* 285 */         return;
/*     */       }
/* 287 */       if ((!"application".equals(this.scope)) && ("application".equals(scope)))
/*     */       {
/* 290 */         if (Log.isWarn()) {
/* 291 */           Log.getLogger(getLogCategory()).warn("Current scope is " + this.scope + " and it cannot be changed to " + "application" + " once factory instance is initialized.");
/*     */         }
/*     */ 
/* 295 */         return;
/*     */       }
/* 297 */       this.factoryInstance.setScope(scope);
/*     */     }
/* 299 */     this.scope = scope;
/*     */   }
/*     */ 
/*     */   public String getSource()
/*     */   {
/* 309 */     return this.source;
/*     */   }
/*     */ 
/*     */   public void setSource(String source)
/*     */   {
/* 321 */     if (this.factoryInstance != null)
/*     */     {
/* 323 */       if ("application".equals(this.scope))
/*     */       {
/* 325 */         if (Log.isWarn()) {
/* 326 */           Log.getLogger(getLogCategory()).warn("Source of the destination cannot be changed once factory instance is already initialized and it has application scope");
/*     */         }
/*     */ 
/* 330 */         return;
/*     */       }
/* 332 */       this.factoryInstance.setSource(source);
/*     */     }
/* 334 */     this.source = source;
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 347 */     super.stop();
/*     */ 
/* 350 */     if ((this.factory != null) && ((this.factory instanceof DestructibleFlexFactory)))
/* 351 */       ((DestructibleFlexFactory)this.factory).destroyFactoryInstance(this.factoryInstance);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.FactoryDestination
 * JD-Core Version:    0.6.0
 */
/*     */ package flex.messaging.client;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
/*     */ import flex.management.Manageable;
/*     */ import flex.management.ManageableComponent;
/*     */ import flex.management.runtime.messaging.client.FlexClientManagerControl;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.FlexClientSettings;
/*     */ import flex.messaging.endpoints.AbstractEndpoint;
/*     */ import flex.messaging.endpoints.Endpoint;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.util.ClassUtil;
/*     */ import flex.messaging.util.TimeoutAbstractObject;
/*     */ import flex.messaging.util.TimeoutManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.Timer;
/*     */ import java.util.TimerTask;
/*     */ 
/*     */ public class FlexClientManager extends ManageableComponent
/*     */ {
/*     */   public static final String TYPE = "FlexClientManager";
/*     */   private final MessageBroker broker;
/*     */   private FlexClientManagerControl controller;
/* 111 */   private final Map flexClients = new ConcurrentHashMap();
/*     */   private Timer flushScheduler;
/* 117 */   private final Object flushInitLock = new Object();
/*     */   private volatile TimeoutManager flexClientTimeoutManager;
/*     */   private volatile long flexClientTimeoutMillis;
/*     */ 
/*     */   public FlexClientManager()
/*     */   {
/*  60 */     this(MessageBroker.getMessageBroker(null));
/*     */   }
/*     */ 
/*     */   public FlexClientManager(MessageBroker broker)
/*     */   {
/*  67 */     this(false, broker);
/*     */   }
/*     */ 
/*     */   public FlexClientManager(boolean enableManagement, MessageBroker mbroker)
/*     */   {
/*  75 */     super(enableManagement);
/*     */ 
/*  77 */     super.setId("FlexClientManager");
/*     */ 
/*  80 */     this.broker = (mbroker != null ? mbroker : MessageBroker.getMessageBroker(null));
/*     */ 
/*  82 */     FlexClientSettings flexClientSettings = this.broker.getFlexClientSettings();
/*  83 */     if (flexClientSettings != null)
/*     */     {
/*  86 */       setFlexClientTimeoutMillis(flexClientSettings.getTimeoutMinutes() * 60L * 1000L);
/*     */     }
/*     */ 
/*  89 */     setParent(this.broker);
/*     */   }
/*     */ 
/*     */   public String[] getClientIds()
/*     */   {
/* 141 */     String[] ids = new String[this.flexClients.size()];
/* 142 */     ArrayList idList = new ArrayList(this.flexClients.keySet());
/*     */ 
/* 144 */     for (int i = 0; i < this.flexClients.size(); i++)
/*     */     {
/* 146 */       ids[i] = ((String)idList.get(i));
/*     */     }
/*     */ 
/* 149 */     return ids;
/*     */   }
/*     */ 
/*     */   public int getFlexClientCount()
/*     */   {
/* 161 */     return this.flexClients.size();
/*     */   }
/*     */ 
/*     */   public long getFlexClientTimeoutMillis()
/*     */   {
/* 175 */     return this.flexClientTimeoutMillis;
/*     */   }
/*     */ 
/*     */   public void setFlexClientTimeoutMillis(long value)
/*     */   {
/* 185 */     if (value < 1L) {
/* 186 */       value = 0L;
/*     */     }
/* 188 */     synchronized (this)
/*     */     {
/* 190 */       this.flexClientTimeoutMillis = value;
/*     */     }
/*     */   }
/*     */ 
/*     */   public MessageBroker getMessageBroker()
/*     */   {
/* 205 */     return this.broker;
/*     */   }
/*     */ 
/*     */   public FlexClient getFlexClient(String id)
/*     */   {
/* 219 */     FlexClient flexClient = null;
/*     */ 
/* 221 */     if (id != null)
/*     */     {
/* 223 */       flexClient = (FlexClient)this.flexClients.get(id);
/* 224 */       if (flexClient != null)
/*     */       {
/* 226 */         if ((flexClient.isValid()) && (!flexClient.invalidating))
/*     */         {
/* 228 */           flexClient.updateLastUse();
/* 229 */           return flexClient;
/*     */         }
/*     */ 
/* 233 */         this.flexClients.remove(id);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 238 */     synchronized (this)
/*     */     {
/* 240 */       if (id != null)
/*     */       {
/* 242 */         flexClient = (FlexClient)this.flexClients.get(id);
/* 243 */         if (flexClient != null)
/*     */         {
/* 245 */           flexClient.updateLastUse();
/* 246 */           return flexClient;
/*     */         }
/*     */ 
/* 250 */         flexClient = new FlexClient(this, id);
/*     */       }
/*     */       else
/*     */       {
/* 255 */         flexClient = new FlexClient(this);
/*     */       }
/* 257 */       this.flexClients.put(flexClient.getId(), flexClient);
/* 258 */       if (this.flexClientTimeoutMillis > 0L)
/* 259 */         this.flexClientTimeoutManager.scheduleTimeout(flexClient);
/* 260 */       flexClient.notifyCreated();
/* 261 */       return flexClient;
/*     */     }
/*     */   }
/*     */ 
/*     */   public FlexClientOutboundQueueProcessor createOutboundQueueProcessor(FlexClient flexClient, String endpointId)
/*     */   {
/* 275 */     FlexClientOutboundQueueProcessor processor = null;
/*     */     try
/*     */     {
/* 279 */       Endpoint endpoint = this.broker.getEndpoint(endpointId);
/* 280 */       if ((endpoint instanceof AbstractEndpoint))
/*     */       {
/* 282 */         Class processorClass = ((AbstractEndpoint)endpoint).getFlexClientOutboundQueueProcessorClass();
/* 283 */         if (processorClass != null)
/*     */         {
/* 285 */           Object instance = ClassUtil.createDefaultInstance(processorClass, null);
/* 286 */           if ((instance instanceof FlexClientOutboundQueueProcessor))
/*     */           {
/* 288 */             processor = (FlexClientOutboundQueueProcessor)instance;
/* 289 */             processor.setFlexClient(flexClient);
/* 290 */             processor.setEndpointId(endpointId);
/* 291 */             processor.initialize(((AbstractEndpoint)endpoint).getFlexClientOutboundQueueProcessorConfig());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 298 */       if (Log.isWarn()) {
/* 299 */         Log.getLogger("Client.FlexClient").warn("Failed to create custom outbound queue processor for FlexClient with id '" + flexClient.getId() + "'. Using default queue processor.", t);
/*     */       }
/*     */     }
/* 302 */     if (processor == null)
/*     */     {
/* 304 */       processor = new FlexClientOutboundQueueProcessor();
/* 305 */       processor.setFlexClient(flexClient);
/* 306 */       processor.setEndpointId(endpointId);
/*     */     }
/*     */ 
/* 309 */     return processor;
/*     */   }
/*     */ 
/*     */   public void monitorAsyncPollTimeout(TimeoutAbstractObject asyncPollTimeout)
/*     */   {
/* 320 */     this.flexClientTimeoutManager.scheduleTimeout(asyncPollTimeout);
/*     */   }
/*     */ 
/*     */   public void scheduleFlush(TimerTask flushTask, int waitInterval)
/*     */   {
/* 331 */     synchronized (this.flushInitLock)
/*     */     {
/* 333 */       if (this.flushScheduler == null) {
/* 334 */         this.flushScheduler = new Timer(true);
/*     */       }
/*     */     }
/* 337 */     this.flushScheduler.schedule(flushTask, waitInterval);
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 345 */     if (isManaged())
/*     */     {
/* 347 */       this.controller = new FlexClientManagerControl(getParent().getControl(), this);
/* 348 */       setControl(this.controller);
/* 349 */       this.controller.register();
/*     */     }
/*     */ 
/* 352 */     String baseId = getId();
/* 353 */     this.flexClientTimeoutManager = new TimeoutManager(new ThreadFactory(baseId) { int counter = 1;
/*     */       private final String val$baseId;
/*     */ 
/* 358 */       public synchronized Thread newThread(Runnable runnable) { Thread t = new Thread(runnable);
/* 359 */         t.setName(this.val$baseId + "-TimeoutThread-" + this.counter++);
/* 360 */         return t;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 370 */     if (this.controller != null)
/*     */     {
/* 372 */       this.controller.unregister();
/*     */     }
/*     */ 
/* 375 */     if (this.flushScheduler != null) {
/* 376 */       this.flushScheduler.cancel();
/*     */     }
/* 378 */     if (this.flexClientTimeoutManager != null)
/* 379 */       this.flexClientTimeoutManager.shutdown();
/*     */   }
/*     */ 
/*     */   protected String getLogCategory()
/*     */   {
/* 393 */     return "Client.FlexClient";
/*     */   }
/*     */ 
/*     */   void removeFlexClient(FlexClient flexClient)
/*     */   {
/* 411 */     if (flexClient != null)
/*     */     {
/* 413 */       String id = flexClient.getId();
/* 414 */       synchronized (id)
/*     */       {
/* 416 */         FlexClient storedClient = (FlexClient)this.flexClients.get(id);
/*     */ 
/* 419 */         if (storedClient == flexClient)
/* 420 */           this.flexClients.remove(id);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.FlexClientManager
 * JD-Core Version:    0.6.0
 */
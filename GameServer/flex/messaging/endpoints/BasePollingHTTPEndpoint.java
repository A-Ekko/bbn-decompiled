/*     */ package flex.messaging.endpoints;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.client.FlexClient;
/*     */ import flex.messaging.client.FlushResult;
/*     */ import flex.messaging.client.PollFlushResult;
/*     */ import flex.messaging.client.PollWaitListener;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import flex.messaging.messages.CommandMessage;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public abstract class BasePollingHTTPEndpoint extends BaseHTTPEndpoint
/*     */   implements PollWaitListener
/*     */ {
/*     */   private static final String POLLING_ENABLED = "polling-enabled";
/*     */   private static final String POLLING_INTERVAL_MILLIS = "polling-interval-millis";
/*     */   private static final String POLLING_INTERVAL_SECONDS = "polling-interval-seconds";
/*     */   private static final String MAX_WAITING_POLL_REQUESTS = "max-waiting-poll-requests";
/*     */   private static final String WAIT_INTERVAL_MILLIS = "wait-interval-millis";
/*     */   private static final String CLIENT_WAIT_INTERVAL_MILLIS = "client-wait-interval-millis";
/*     */   private static final int DEFAULT_WAIT_FOR_EXCESS_POLL_WAIT_CLIENTS = 3000;
/*     */   private volatile boolean canWait;
/* 142 */   protected final Object lock = new Object();
/*     */   private boolean waitEnabled;
/*     */   protected int waitingPollRequestsCount;
/*     */   private ConcurrentHashMap currentWaitedRequests;
/* 171 */   protected int clientWaitInterval = 0;
/*     */ 
/* 209 */   protected int maxWaitingPollRequests = 0;
/*     */   protected boolean piggybackingEnabled;
/*     */   protected boolean pollingEnabled;
/*     */   protected long pollingIntervalMillis;
/* 271 */   protected long waitInterval = 0L;
/*     */ 
/*     */   public BasePollingHTTPEndpoint()
/*     */   {
/*  70 */     this(false);
/*     */   }
/*     */ 
/*     */   public BasePollingHTTPEndpoint(boolean enableManagement)
/*     */   {
/*  81 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/*  99 */     super.initialize(id, properties);
/*     */ 
/* 101 */     if ((properties == null) || (properties.size() == 0)) {
/* 102 */       return;
/*     */     }
/*     */ 
/* 105 */     this.pollingEnabled = properties.getPropertyAsBoolean("polling-enabled", false);
/* 106 */     this.pollingIntervalMillis = properties.getPropertyAsLong("polling-interval-millis", -1L);
/* 107 */     long pollingIntervalSeconds = properties.getPropertyAsLong("polling-interval-seconds", -1L);
/* 108 */     if (pollingIntervalSeconds > -1L) {
/* 109 */       this.pollingIntervalMillis = (pollingIntervalSeconds * 1000L);
/*     */     }
/*     */ 
/* 112 */     this.piggybackingEnabled = properties.getPropertyAsBoolean("piggybacking-enabled", false);
/*     */ 
/* 115 */     this.maxWaitingPollRequests = properties.getPropertyAsInt("max-waiting-poll-requests", 0);
/* 116 */     this.waitInterval = properties.getPropertyAsLong("wait-interval-millis", 0L);
/* 117 */     this.clientWaitInterval = properties.getPropertyAsInt("client-wait-interval-millis", 0);
/*     */ 
/* 120 */     if ((this.maxWaitingPollRequests > 0) && ((this.waitInterval == -1L) || (this.waitInterval > 0L)))
/*     */     {
/* 122 */       this.waitEnabled = true;
/* 123 */       this.canWait = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getClientWaitInterval()
/*     */   {
/* 184 */     return this.clientWaitInterval;
/*     */   }
/*     */ 
/*     */   public void setClientWaitInterval(int value)
/*     */   {
/* 202 */     this.clientWaitInterval = value;
/*     */   }
/*     */ 
/*     */   public int getMaxWaitingPollRequests()
/*     */   {
/* 217 */     return this.maxWaitingPollRequests;
/*     */   }
/*     */ 
/*     */   public void setMaxWaitingPollRequests(int maxWaitingPollRequests)
/*     */   {
/* 229 */     this.maxWaitingPollRequests = maxWaitingPollRequests;
/* 230 */     if ((maxWaitingPollRequests > 0) && ((this.waitInterval == -1L) || (this.waitInterval > 0L)))
/*     */     {
/* 232 */       this.waitEnabled = true;
/* 233 */       this.canWait = (this.waitingPollRequestsCount < maxWaitingPollRequests);
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getWaitInterval()
/*     */   {
/* 279 */     return this.waitInterval;
/*     */   }
/*     */ 
/*     */   public void setWaitInterval(long waitInterval)
/*     */   {
/* 291 */     this.waitInterval = waitInterval;
/* 292 */     if ((this.maxWaitingPollRequests > 0) && ((waitInterval == -1L) || (waitInterval > 0L)))
/*     */     {
/* 294 */       this.waitEnabled = true;
/* 295 */       this.canWait = (this.waitingPollRequestsCount < this.maxWaitingPollRequests);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ConfigMap describeEndpoint()
/*     */   {
/* 314 */     ConfigMap endpointConfig = super.describeEndpoint();
/*     */ 
/* 316 */     boolean createdProperties = false;
/* 317 */     ConfigMap properties = endpointConfig.getPropertyAsMap("properties", null);
/*     */ 
/* 319 */     if (properties == null)
/*     */     {
/* 321 */       properties = new ConfigMap();
/* 322 */       createdProperties = true;
/*     */     }
/*     */ 
/* 325 */     if (this.pollingEnabled)
/*     */     {
/* 327 */       ConfigMap pollingEnabled = new ConfigMap();
/*     */ 
/* 329 */       pollingEnabled.addProperty("", "true");
/* 330 */       properties.addProperty("polling-enabled", pollingEnabled);
/*     */     }
/*     */ 
/* 333 */     if (this.pollingIntervalMillis > -1L)
/*     */     {
/* 335 */       ConfigMap pollingInterval = new ConfigMap();
/*     */ 
/* 337 */       pollingInterval.addProperty("", String.valueOf(this.pollingIntervalMillis));
/* 338 */       properties.addProperty("polling-interval-millis", pollingInterval);
/*     */     }
/*     */ 
/* 341 */     if (this.piggybackingEnabled)
/*     */     {
/* 343 */       ConfigMap piggybackingEnabled = new ConfigMap();
/*     */ 
/* 345 */       piggybackingEnabled.addProperty("", String.valueOf(piggybackingEnabled));
/* 346 */       properties.addProperty("piggybacking-enabled", piggybackingEnabled);
/*     */     }
/*     */ 
/* 349 */     if ((createdProperties) && (properties.size() > 0)) {
/* 350 */       endpointConfig.addProperty("properties", properties);
/*     */     }
/* 352 */     return endpointConfig;
/*     */   }
/*     */ 
/*     */   public void start()
/*     */   {
/* 363 */     if (isStarted()) {
/* 364 */       return;
/*     */     }
/* 366 */     super.start();
/*     */ 
/* 368 */     this.currentWaitedRequests = new ConcurrentHashMap();
/*     */   }
/*     */ 
/*     */   public void stop()
/*     */   {
/* 378 */     if (!isStarted()) {
/* 379 */       return;
/*     */     }
/*     */ 
/* 382 */     for (Iterator iter = this.currentWaitedRequests.keySet().iterator(); iter.hasNext(); )
/*     */     {
/* 384 */       Object notifier = iter.next();
/* 385 */       synchronized (notifier)
/*     */       {
/* 387 */         notifier.notifyAll();
/*     */       }
/*     */     }
/* 390 */     this.currentWaitedRequests = null;
/*     */ 
/* 392 */     super.stop();
/*     */   }
/*     */ 
/*     */   public void waitStart(Object notifier)
/*     */   {
/* 400 */     this.currentWaitedRequests.put(notifier, Boolean.TRUE);
/*     */   }
/*     */ 
/*     */   public void waitEnd(Object notifier)
/*     */   {
/* 408 */     this.currentWaitedRequests.remove(notifier);
/*     */   }
/*     */ 
/*     */   protected FlushResult handleFlexClientPoll(FlexClient flexClient, CommandMessage pollCommand)
/*     */   {
/* 430 */     FlushResult flushResult = null;
/* 431 */     if ((this.canWait) && (!pollCommand.headerExists("DSSuppressPollWait")))
/*     */     {
/* 438 */       boolean thisThreadCanWait = false;
/* 439 */       synchronized (this.lock)
/*     */       {
/* 441 */         this.waitingPollRequestsCount += 1;
/* 442 */         if (this.waitingPollRequestsCount == this.maxWaitingPollRequests)
/*     */         {
/* 444 */           thisThreadCanWait = true;
/* 445 */           this.canWait = false;
/*     */         }
/* 447 */         else if (this.waitingPollRequestsCount > this.maxWaitingPollRequests)
/*     */         {
/* 449 */           thisThreadCanWait = false;
/* 450 */           this.waitingPollRequestsCount -= 1;
/* 451 */           this.canWait = false;
/*     */         }
/*     */         else
/*     */         {
/* 456 */           thisThreadCanWait = true;
/*     */         }
/*     */       }
/*     */ 
/* 460 */       if (thisThreadCanWait)
/*     */       {
/* 462 */         if (Log.isDebug()) {
/* 463 */           this.log.debug("Number of waiting threads for endpoint with id '" + getId() + "' is " + this.waitingPollRequestsCount + ".");
/*     */         }
/*     */         try
/*     */         {
/* 467 */           flushResult = flexClient.pollWithWait(getId(), FlexContext.getFlexSession(), this, this.waitInterval);
/* 468 */           if (flushResult != null)
/*     */           {
/* 471 */             if (((flushResult instanceof PollFlushResult)) && (((PollFlushResult)flushResult).isAvoidBusyPolling()) && (flushResult.getNextFlushWaitTimeMillis() < 3000))
/*     */             {
/* 474 */               flushResult.setNextFlushWaitTimeMillis(3000);
/*     */             }
/* 476 */             else if ((this.clientWaitInterval > 0) && (flushResult.getNextFlushWaitTimeMillis() == 0))
/*     */             {
/* 479 */               flushResult.setNextFlushWaitTimeMillis(this.clientWaitInterval);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */         finally
/*     */         {
/* 486 */           synchronized (this.lock)
/*     */           {
/* 488 */             this.waitingPollRequestsCount -= 1;
/* 489 */             if (this.waitingPollRequestsCount < this.maxWaitingPollRequests) {
/* 490 */               this.canWait = true;
/*     */             }
/*     */           }
/* 493 */           if (Log.isDebug())
/* 494 */             this.log.debug("Number of waiting threads for endpoint with id '" + getId() + "' is " + this.waitingPollRequestsCount + ".");
/*     */         }
/*     */       }
/*     */     }
/* 498 */     else if ((Log.isDebug()) && (this.waitEnabled))
/*     */     {
/* 500 */       if (pollCommand.headerExists("DSSuppressPollWait"))
/* 501 */         this.log.debug("Suppressing poll wait for this request because it is part of a batch of messages to process.");
/*     */       else {
/* 503 */         this.log.debug("Max waiting poll requests limit '" + this.maxWaitingPollRequests + "' has been reached for endpoint '" + getId() + "'. FlexClient with id '" + flexClient.getId() + "' will poll with no wait.");
/*     */       }
/*     */     }
/*     */ 
/* 507 */     if (flushResult == null)
/*     */     {
/* 509 */       flushResult = super.handleFlexClientPoll(flexClient, pollCommand);
/*     */ 
/* 512 */       if ((flushResult != null) && (this.waitEnabled) && (this.pollingIntervalMillis < 3000L)) {
/* 513 */         flushResult.setNextFlushWaitTimeMillis(3000);
/*     */       }
/*     */     }
/* 516 */     return flushResult;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.BasePollingHTTPEndpoint
 * JD-Core Version:    0.6.0
 */
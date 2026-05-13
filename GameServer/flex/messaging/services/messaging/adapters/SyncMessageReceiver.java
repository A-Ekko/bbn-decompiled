/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.Executors;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
/*     */ import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
/*     */ import flex.messaging.log.Log;
/*     */ import flex.messaging.log.Logger;
/*     */ import javax.jms.JMSException;
/*     */ import javax.jms.Message;
/*     */ 
/*     */ class SyncMessageReceiver
/*     */   implements MessageReceiver
/*     */ {
/*     */   private ScheduledExecutorService messageReceiverService;
/*  38 */   private boolean isScheduled = false;
/*     */   private JMSConsumer jmsConsumer;
/*     */   private int syncMaxReceiveThreads;
/*     */   private long syncReceiveIntervalMillis;
/*     */   private long syncReceiveWaitMillis;
/*     */ 
/*     */   public SyncMessageReceiver(JMSConsumer jmsConsumer)
/*     */   {
/*  52 */     this.jmsConsumer = jmsConsumer;
/*  53 */     this.syncReceiveIntervalMillis = 100L;
/*  54 */     this.syncReceiveWaitMillis = 0L;
/*  55 */     this.syncMaxReceiveThreads = 1;
/*     */   }
/*     */ 
/*     */   public long getSyncReceiveIntervalMillis()
/*     */   {
/*  65 */     return this.syncReceiveIntervalMillis;
/*     */   }
/*     */ 
/*     */   public void setSyncReceiveIntervalMillis(long syncReceiveIntervalMillis)
/*     */   {
/*  77 */     if (syncReceiveIntervalMillis < 1L)
/*  78 */       syncReceiveIntervalMillis = 100L;
/*  79 */     this.syncReceiveIntervalMillis = syncReceiveIntervalMillis;
/*     */   }
/*     */ 
/*     */   public long getSyncReceiveWaitMillis()
/*     */   {
/*  89 */     return this.syncReceiveWaitMillis;
/*     */   }
/*     */ 
/*     */   public void setSyncReceiveWaitMillis(long syncReceiveWaitMillis)
/*     */   {
/* 102 */     if (syncReceiveWaitMillis < -1L)
/* 103 */       syncReceiveWaitMillis = 0L;
/* 104 */     this.syncReceiveWaitMillis = syncReceiveWaitMillis;
/*     */   }
/*     */ 
/*     */   public void startReceive()
/*     */   {
/* 112 */     if (!this.isScheduled)
/*     */     {
/* 114 */       if (Log.isDebug()) {
/* 115 */         Log.getLogger("Service.Message.JMS").debug(Thread.currentThread() + " JMS consumer sync receive thread for JMS destination '" + this.jmsConsumer.destinationJndiName + "' is starting to poll the JMS server for new messages.");
/*     */       }
/*     */ 
/* 119 */       ThreadFactory mrtf = new MessageReceiveThreadFactory();
/* 120 */       this.messageReceiverService = Executors.newScheduledThreadPool(this.syncMaxReceiveThreads, mrtf);
/* 121 */       this.messageReceiverService.scheduleAtFixedRate(new MessageReceiveThread(), this.syncReceiveIntervalMillis, this.syncReceiveIntervalMillis, TimeUnit.MILLISECONDS);
/* 122 */       this.isScheduled = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void stopReceive()
/*     */   {
/* 131 */     if (this.messageReceiverService != null)
/* 132 */       this.messageReceiverService.shutdown();
/*     */   }
/*     */ 
/*     */   private Message receiveMessage()
/*     */     throws JMSException
/*     */   {
/* 140 */     if (this.syncReceiveWaitMillis == -1L)
/* 141 */       return this.jmsConsumer.receive();
/* 142 */     if (this.syncReceiveWaitMillis == 0L)
/* 143 */       return this.jmsConsumer.receiveNoWait();
/* 144 */     if (this.syncReceiveWaitMillis > 0L)
/* 145 */       return this.jmsConsumer.receive(this.syncReceiveWaitMillis);
/* 146 */     return null;
/*     */   }
/*     */ 
/*     */   class MessageReceiveThread
/*     */     implements Runnable
/*     */   {
/*     */     MessageReceiveThread()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/* 189 */           Message message = SyncMessageReceiver.this.receiveMessage();
/* 190 */           if (message == null) break;
/* 191 */           SyncMessageReceiver.this.jmsConsumer.onMessage(message);
/*     */         }
/*     */       }
/*     */       catch (JMSException jmsEx)
/*     */       {
/* 196 */         SyncMessageReceiver.this.jmsConsumer.onException(jmsEx);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   class MessageReceiveThreadFactory
/*     */     implements ThreadFactory
/*     */   {
/*     */     private int receiveThreadCount;
/*     */ 
/*     */     MessageReceiveThreadFactory()
/*     */     {
/*     */     }
/*     */ 
/*     */     public synchronized Thread newThread(Runnable r)
/*     */     {
/* 168 */       Thread t = new Thread(r);
/* 169 */       t.setName("MessageReceiveThread-" + this.receiveThreadCount++);
/* 170 */       if (Log.isDebug())
/* 171 */         Log.getLogger("Service.Message.JMS").debug("Created message receive thread: " + t.getName());
/* 172 */       return t;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.SyncMessageReceiver
 * JD-Core Version:    0.6.0
 */
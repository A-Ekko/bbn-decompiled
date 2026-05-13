/*     */ package flex.messaging;
/*     */ 
/*     */ import flex.management.runtime.messaging.MessageBrokerControl;
/*     */ 
/*     */ public class SessionMetricsTracker
/*     */   implements FlexSessionListener
/*     */ {
/*     */   private int connectionCount;
/*     */   private int currentConnectionCountMax;
/*     */   private MessageBroker messageBroker;
/*     */ 
/*     */   public SessionMetricsTracker(MessageBroker messageBroker)
/*     */   {
/*  41 */     this.messageBroker = messageBroker;
/*     */   }
/*     */ 
/*     */   public synchronized void sessionCreated(FlexSession session)
/*     */   {
/*  75 */     session.addSessionDestroyedListener(this);
/*  76 */     this.connectionCount += 1;
/*  77 */     if (this.connectionCount > this.currentConnectionCountMax)
/*  78 */       this.currentConnectionCountMax = this.connectionCount;
/*  79 */     if (this.messageBroker.isManaged())
/*     */     {
/*  81 */       ((MessageBrokerControl)this.messageBroker.getControl()).setFlexSessionCount(this.connectionCount);
/*  82 */       ((MessageBrokerControl)this.messageBroker.getControl()).setMaxFlexSessionsInCurrentHour(this.currentConnectionCountMax);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void sessionDestroyed(FlexSession session)
/*     */   {
/*  89 */     session.removeSessionDestroyedListener(this);
/*  90 */     this.connectionCount -= 1;
/*  91 */     if (this.messageBroker.isManaged())
/*     */     {
/*  93 */       ((MessageBrokerControl)this.messageBroker.getControl()).setFlexSessionCount(this.connectionCount);
/*  94 */       ((MessageBrokerControl)this.messageBroker.getControl()).setMaxFlexSessionsInCurrentHour(this.currentConnectionCountMax);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void start()
/*     */   {
/* 103 */     FlexSession.addSessionCreatedListener(this);
/*     */   }
/*     */ 
/*     */   public synchronized void stop()
/*     */   {
/* 111 */     FlexSession.removeSessionCreatedListener(this);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.SessionMetricsTracker
 * JD-Core Version:    0.6.0
 */
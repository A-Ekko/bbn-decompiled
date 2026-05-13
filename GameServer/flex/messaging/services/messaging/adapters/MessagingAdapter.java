/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.Destination;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.security.MessagingSecurity;
/*     */ import flex.messaging.services.Service;
/*     */ import flex.messaging.services.ServiceAdapter;
/*     */ import flex.messaging.services.messaging.Subtopic;
/*     */ 
/*     */ public abstract class MessagingAdapter extends ServiceAdapter
/*     */   implements MessagingSecurity
/*     */ {
/*     */   private MessagingSecurityConstraintManager constraintManager;
/*     */ 
/*     */   public MessagingAdapter()
/*     */   {
/*  91 */     this(false);
/*     */   }
/*     */ 
/*     */   public MessagingAdapter(boolean enableManagement)
/*     */   {
/* 102 */     super(enableManagement);
/*     */   }
/*     */ 
/*     */   public void initialize(String id, ConfigMap properties)
/*     */   {
/* 120 */     super.initialize(id, properties);
/*     */ 
/* 122 */     if ((properties == null) || (properties.size() == 0)) {
/* 123 */       return;
/*     */     }
/* 125 */     ConfigMap server = properties.getPropertyAsMap("server", null);
/* 126 */     if (server != null)
/*     */     {
/* 128 */       if (this.constraintManager == null)
/* 129 */         this.constraintManager = new MessagingSecurityConstraintManager(getDestination().getService().getMessageBroker());
/* 130 */       this.constraintManager.createConstraints(server);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void validate()
/*     */   {
/* 140 */     if (isValid()) {
/* 141 */       return;
/*     */     }
/* 143 */     super.validate();
/*     */ 
/* 147 */     if (this.constraintManager == null)
/* 148 */       this.constraintManager = new MessagingSecurityConstraintManager(getDestination().getService().getMessageBroker());
/*     */   }
/*     */ 
/*     */   public boolean allowSubscribe(Subtopic subtopic)
/*     */   {
/* 170 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean allowSend(Subtopic subtopic)
/*     */   {
/* 186 */     return true;
/*     */   }
/*     */ 
/*     */   public MessagingSecurityConstraintManager getSecurityConstraintManager()
/*     */   {
/* 196 */     return this.constraintManager;
/*     */   }
/*     */ 
/*     */   public void setSecurityConstraintManager(MessagingSecurityConstraintManager constraintManager)
/*     */   {
/* 206 */     this.constraintManager = constraintManager;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.MessagingAdapter
 * JD-Core Version:    0.6.0
 */
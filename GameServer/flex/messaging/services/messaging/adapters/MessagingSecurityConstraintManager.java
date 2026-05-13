/*     */ package flex.messaging.services.messaging.adapters;
/*     */ 
/*     */ import flex.messaging.FlexContext;
/*     */ import flex.messaging.MessageBroker;
/*     */ import flex.messaging.config.ConfigMap;
/*     */ import flex.messaging.config.SecurityConstraint;
/*     */ import flex.messaging.config.SecuritySettings;
/*     */ import flex.messaging.security.LoginManager;
/*     */ import flex.messaging.security.SecurityException;
/*     */ 
/*     */ public final class MessagingSecurityConstraintManager
/*     */ {
/*     */   private static final String SEND_SECURITY_CONSTRAINT = "send-security-constraint";
/*     */   private static final String SUBSCRIBE_SECURITY_CONSTRAINT = "subscribe-security-constraint";
/*     */   private static final int NO_SEC_CONSTRAINT = 10062;
/*     */   private LoginManager loginManager;
/*     */   private SecuritySettings securitySettings;
/*     */   private SecurityConstraint sendConstraint;
/*     */   private SecurityConstraint subscribeConstraint;
/*     */ 
/*     */   public MessagingSecurityConstraintManager(MessageBroker broker)
/*     */   {
/*  50 */     this.loginManager = broker.getLoginManager();
/*  51 */     this.securitySettings = broker.getSecuritySettings();
/*     */   }
/*     */ 
/*     */   public void setSendConstraint(String ref)
/*     */   {
/*  62 */     validateConstraint(ref);
/*  63 */     this.sendConstraint = this.securitySettings.getConstraint(ref);
/*     */   }
/*     */ 
/*     */   public void setSubscribeConstraint(String ref)
/*     */   {
/*  74 */     validateConstraint(ref);
/*  75 */     this.subscribeConstraint = this.securitySettings.getConstraint(ref);
/*     */   }
/*     */ 
/*     */   public void assertSendAuthorization()
/*     */   {
/*  81 */     checkConstraint(this.sendConstraint);
/*     */   }
/*     */ 
/*     */   public void assertSubscribeAuthorization()
/*     */   {
/*  87 */     checkConstraint(this.subscribeConstraint);
/*     */   }
/*     */ 
/*     */   public void createConstraints(ConfigMap serverSettings)
/*     */   {
/*  94 */     ConfigMap send = serverSettings.getPropertyAsMap("send-security-constraint", null);
/*  95 */     if (send != null)
/*     */     {
/*  97 */       String ref = send.getPropertyAsString("ref", null);
/*  98 */       if (ref != null) {
/*  99 */         this.sendConstraint = this.securitySettings.getConstraint(ref);
/*     */       }
/*     */     }
/*     */ 
/* 103 */     ConfigMap subscribe = serverSettings.getPropertyAsMap("subscribe-security-constraint", null);
/* 104 */     if (subscribe != null)
/*     */     {
/* 106 */       String ref = subscribe.getPropertyAsString("ref", null);
/* 107 */       if (ref != null)
/* 108 */         this.subscribeConstraint = this.securitySettings.getConstraint(ref);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkConstraint(SecurityConstraint constraint)
/*     */   {
/* 114 */     if ((constraint != null) && (!FlexContext.isMessageFromPeer()))
/*     */     {
/*     */       try
/*     */       {
/* 118 */         this.loginManager.checkConstraint(constraint);
/*     */       }
/*     */       catch (SecurityException e)
/*     */       {
/* 122 */         throw e;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void validateConstraint(String ref)
/*     */   {
/* 131 */     if (this.securitySettings.getConstraint(ref) == null)
/*     */     {
/* 134 */       SecurityException se = new SecurityException();
/* 135 */       se.setMessage(10062, new Object[] { ref });
/* 136 */       throw se;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.MessagingSecurityConstraintManager
 * JD-Core Version:    0.6.0
 */
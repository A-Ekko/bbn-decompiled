/*     */ package flex.messaging.config;
/*     */ 
/*     */ public class LoginCommandSettings
/*     */ {
/*     */   public static final String SERVER_MATCH_OVERRIDE = "all";
/*     */   private String className;
/*     */   private String server;
/*     */   private boolean perClientAuthentication;
/*     */ 
/*     */   public LoginCommandSettings()
/*     */   {
/*  39 */     this.perClientAuthentication = false;
/*     */   }
/*     */ 
/*     */   public String getClassName()
/*     */   {
/*  49 */     return this.className;
/*     */   }
/*     */ 
/*     */   public void setClassName(String className)
/*     */   {
/*  59 */     this.className = className;
/*     */   }
/*     */ 
/*     */   public String getServer()
/*     */   {
/*  69 */     return this.server;
/*     */   }
/*     */ 
/*     */   public void setServer(String server)
/*     */   {
/*  79 */     this.server = server;
/*     */   }
/*     */ 
/*     */   public boolean isPerClientAuthentication()
/*     */   {
/*  90 */     return this.perClientAuthentication;
/*     */   }
/*     */ 
/*     */   public void setPerClientAuthentication(boolean perClientAuthentication)
/*     */   {
/* 101 */     this.perClientAuthentication = perClientAuthentication;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.LoginCommandSettings
 * JD-Core Version:    0.6.0
 */
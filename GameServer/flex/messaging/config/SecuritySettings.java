/*    */ package flex.messaging.config;
/*    */ 
/*    */ import flex.messaging.security.SecurityException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SecuritySettings
/*    */ {
/*    */   private static final int NO_SEC_CONSTRAINT = 10062;
/*    */   private String serverInfo;
/*    */   private Map loginCommandSettings;
/*    */   private Map constraints;
/*    */ 
/*    */   public SecuritySettings()
/*    */   {
/* 40 */     this.constraints = new HashMap();
/* 41 */     this.loginCommandSettings = new HashMap();
/*    */   }
/*    */ 
/*    */   public void addConstraint(SecurityConstraint sc)
/*    */   {
/* 46 */     this.constraints.put(sc.getId(), sc);
/*    */   }
/*    */ 
/*    */   public SecurityConstraint getConstraint(String ref)
/*    */   {
/* 53 */     if (this.constraints.get(ref) == null)
/*    */     {
/* 56 */       SecurityException se = new SecurityException();
/* 57 */       se.setMessage(10062, new Object[] { ref });
/* 58 */       throw se;
/*    */     }
/* 60 */     return (SecurityConstraint)this.constraints.get(ref);
/*    */   }
/*    */ 
/*    */   public void addLoginCommandSettings(LoginCommandSettings lcs)
/*    */   {
/* 65 */     this.loginCommandSettings.put(lcs.getServer(), lcs);
/*    */   }
/*    */ 
/*    */   public Map getLoginCommands()
/*    */   {
/* 70 */     return this.loginCommandSettings;
/*    */   }
/*    */ 
/*    */   public void setServerInfo(String s)
/*    */   {
/* 75 */     this.serverInfo = s;
/*    */   }
/*    */ 
/*    */   public String getServerInfo()
/*    */   {
/* 80 */     return this.serverInfo;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.config.SecuritySettings
 * JD-Core Version:    0.6.0
 */
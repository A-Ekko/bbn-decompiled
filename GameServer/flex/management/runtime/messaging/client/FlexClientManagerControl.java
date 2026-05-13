/*    */ package flex.management.runtime.messaging.client;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.management.runtime.AdminConsoleDisplayRegistrar;
/*    */ import flex.messaging.client.FlexClient;
/*    */ import flex.messaging.client.FlexClientManager;
/*    */ import javax.management.ObjectName;
/*    */ 
/*    */ public class FlexClientManagerControl extends BaseControl
/*    */   implements FlexClientManagerControlMBean
/*    */ {
/*    */   private FlexClientManager flexClientManager;
/*    */ 
/*    */   public FlexClientManagerControl(BaseControl parent, FlexClientManager manager)
/*    */   {
/* 19 */     super(parent);
/* 20 */     this.flexClientManager = manager;
/*    */   }
/*    */ 
/*    */   public void onRegistrationComplete()
/*    */   {
/* 25 */     String name = getObjectName().getCanonicalName();
/* 26 */     getRegistrar().registerObject(2, name, "FlexClientCount");
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 34 */     return this.flexClientManager.getId();
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 42 */     return this.flexClientManager.getId();
/*    */   }
/*    */ 
/*    */   public String[] getClientIds()
/*    */   {
/* 50 */     return this.flexClientManager.getClientIds();
/*    */   }
/*    */ 
/*    */   public Long getClientLastUse(String clientId)
/*    */   {
/* 58 */     return new Long(this.flexClientManager.getFlexClient(clientId).getLastUse());
/*    */   }
/*    */ 
/*    */   public Integer getClientSessionCount(String clientId)
/*    */   {
/* 66 */     return new Integer(this.flexClientManager.getFlexClient(clientId).getSessionCount());
/*    */   }
/*    */ 
/*    */   public Integer getClientSubscriptionCount(String clientId)
/*    */   {
/* 74 */     return new Integer(this.flexClientManager.getFlexClient(clientId).getSubscriptionCount());
/*    */   }
/*    */ 
/*    */   public Integer getFlexClientCount()
/*    */   {
/* 82 */     return new Integer(this.flexClientManager.getFlexClientCount());
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.client.FlexClientManagerControl
 * JD-Core Version:    0.6.0
 */
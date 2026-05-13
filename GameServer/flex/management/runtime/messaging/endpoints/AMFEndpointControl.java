/*    */ package flex.management.runtime.messaging.endpoints;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.messaging.endpoints.AMFEndpoint;
/*    */ 
/*    */ public class AMFEndpointControl extends EndpointControl
/*    */   implements AMFEndpointControlMBean
/*    */ {
/*    */   private static final String TYPE = "AMFEndpoint";
/*    */ 
/*    */   public AMFEndpointControl(AMFEndpoint endpoint, BaseControl parent)
/*    */   {
/* 44 */     super(endpoint, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 53 */     return "AMFEndpoint";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.AMFEndpointControl
 * JD-Core Version:    0.6.0
 */
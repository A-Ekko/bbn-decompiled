/*    */ package flex.management.runtime.messaging.endpoints;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.messaging.endpoints.HTTPEndpoint;
/*    */ 
/*    */ public class HTTPEndpointControl extends EndpointControl
/*    */   implements HTTPEndpointControlMBean
/*    */ {
/*    */   private static final String TYPE = "HTTPEndpoint";
/*    */ 
/*    */   public HTTPEndpointControl(HTTPEndpoint endpoint, BaseControl parent)
/*    */   {
/* 44 */     super(endpoint, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 53 */     return "HTTPEndpoint";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.HTTPEndpointControl
 * JD-Core Version:    0.6.0
 */
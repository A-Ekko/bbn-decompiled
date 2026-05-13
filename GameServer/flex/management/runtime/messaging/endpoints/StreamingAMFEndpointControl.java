/*    */ package flex.management.runtime.messaging.endpoints;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.messaging.endpoints.StreamingAMFEndpoint;
/*    */ 
/*    */ public class StreamingAMFEndpointControl extends StreamingEndpointControl
/*    */   implements StreamingAMFEndpointControlMBean
/*    */ {
/*    */   private static final String TYPE = "StreamingAMFEndpoint";
/*    */ 
/*    */   public StreamingAMFEndpointControl(StreamingAMFEndpoint endpoint, BaseControl parent)
/*    */   {
/* 42 */     super(endpoint, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 51 */     return "StreamingAMFEndpoint";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.StreamingAMFEndpointControl
 * JD-Core Version:    0.6.0
 */
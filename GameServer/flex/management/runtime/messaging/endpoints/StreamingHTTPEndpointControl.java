/*    */ package flex.management.runtime.messaging.endpoints;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.messaging.endpoints.StreamingHTTPEndpoint;
/*    */ 
/*    */ public class StreamingHTTPEndpointControl extends StreamingEndpointControl
/*    */   implements StreamingHTTPEndpointControlMBean
/*    */ {
/*    */   private static final String TYPE = "StreamingHTTPEndpoint";
/*    */ 
/*    */   public StreamingHTTPEndpointControl(StreamingHTTPEndpoint endpoint, BaseControl parent)
/*    */   {
/* 42 */     super(endpoint, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 51 */     return "StreamingHTTPEndpoint";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.StreamingHTTPEndpointControl
 * JD-Core Version:    0.6.0
 */
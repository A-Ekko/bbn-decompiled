/*    */ package flex.management.runtime.messaging.services;
/*    */ 
/*    */ import flex.management.BaseControl;
/*    */ import flex.messaging.services.MessageService;
/*    */ 
/*    */ public class MessageServiceControl extends ServiceControl
/*    */   implements MessageServiceControlMBean
/*    */ {
/*    */   private static final String TYPE = "MessageService";
/*    */ 
/*    */   public MessageServiceControl(MessageService service, BaseControl parent)
/*    */   {
/* 44 */     super(service, parent);
/*    */   }
/*    */ 
/*    */   public String getType()
/*    */   {
/* 53 */     return "MessageService";
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.MessageServiceControl
 * JD-Core Version:    0.6.0
 */
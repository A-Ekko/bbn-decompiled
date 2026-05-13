/*    */ package flex.messaging.services.messaging.adapters;
/*    */ 
/*    */ import flex.management.runtime.messaging.services.messaging.adapters.ActionScriptAdapterControl;
/*    */ import flex.messaging.Destination;
/*    */ import flex.messaging.MessageDestination;
/*    */ import flex.messaging.messages.Message;
/*    */ import flex.messaging.services.MessageService;
/*    */ 
/*    */ public class ActionScriptAdapter extends MessagingAdapter
/*    */ {
/*    */   private ActionScriptAdapterControl controller;
/*    */ 
/*    */   public void setDestination(Destination destination)
/*    */   {
/* 64 */     Destination dest = (MessageDestination)destination;
/* 65 */     super.setDestination(dest);
/*    */   }
/*    */ 
/*    */   public Object invoke(Message message)
/*    */   {
/* 79 */     MessageService msgService = (MessageService)getDestination().getService();
/* 80 */     msgService.pushMessageToClients(message, true);
/* 81 */     msgService.sendPushMessageFromPeer(message, true);
/* 82 */     return null;
/*    */   }
/*    */ 
/*    */   protected void setupAdapterControl(Destination destination)
/*    */   {
/* 93 */     this.controller = new ActionScriptAdapterControl(this, destination.getControl());
/* 94 */     this.controller.register();
/* 95 */     setControl(this.controller);
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.adapters.ActionScriptAdapter
 * JD-Core Version:    0.6.0
 */
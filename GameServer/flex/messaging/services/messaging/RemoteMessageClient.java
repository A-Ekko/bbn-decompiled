/*    */ package flex.messaging.services.messaging;
/*    */ 
/*    */ import flex.messaging.MessageClient;
/*    */ import flex.messaging.MessageClient.SubscriptionInfo;
/*    */ import flex.messaging.MessageDestination;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class RemoteMessageClient extends MessageClient
/*    */ {
/*    */   public RemoteMessageClient(Object clientId, MessageDestination destination, String endpointId)
/*    */   {
/* 34 */     super(clientId, destination, endpointId, false);
/*    */   }
/*    */ 
/*    */   public void invalidate()
/*    */   {
/*    */     Iterator it;
/* 43 */     synchronized (this.lock)
/*    */     {
/* 45 */       if (!this.valid) {
/* 46 */         return;
/*    */       }
/* 48 */       for (it = this.subscriptions.iterator(); it.hasNext(); )
/*    */       {
/* 50 */         MessageClient.SubscriptionInfo si = (MessageClient.SubscriptionInfo)it.next();
/*    */ 
/* 52 */         this.destination.getRemoteSubscriptionManager().removeSubscriber(this.clientId, si.selector, si.subtopic, null);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.services.messaging.RemoteMessageClient
 * JD-Core Version:    0.6.0
 */
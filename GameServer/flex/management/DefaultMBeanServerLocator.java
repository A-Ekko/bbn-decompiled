/*    */ package flex.management;
/*    */ 
/*    */ import flex.messaging.log.Log;
/*    */ import flex.messaging.log.Logger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.MBeanServerFactory;
/*    */ 
/*    */ public class DefaultMBeanServerLocator
/*    */   implements MBeanServerLocator
/*    */ {
/*    */   private MBeanServer server;
/*    */ 
/*    */   public synchronized MBeanServer getMBeanServer()
/*    */   {
/* 47 */     if (this.server == null)
/*    */     {
/* 50 */       ArrayList servers = MBeanServerFactory.findMBeanServer(null);
/* 51 */       if (servers.size() > 0)
/*    */       {
/* 53 */         Iterator iterator = servers.iterator();
/* 54 */         this.server = ((MBeanServer)iterator.next());
/*    */       }
/*    */       else
/*    */       {
/* 59 */         this.server = MBeanServerFactory.createMBeanServer();
/*    */       }
/* 61 */       if (Log.isDebug())
/* 62 */         Log.getLogger("Management.MBeanServer").debug("Using MBeanServer: " + this.server);
/*    */     }
/* 64 */     return this.server;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.DefaultMBeanServerLocator
 * JD-Core Version:    0.6.0
 */
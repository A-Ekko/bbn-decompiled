/*    */ package flex.management;
/*    */ 
/*    */ import flex.messaging.log.Log;
/*    */ import flex.messaging.log.Logger;
/*    */ import java.lang.management.ManagementFactory;
/*    */ import javax.management.MBeanServer;
/*    */ 
/*    */ public class PlatformMBeanServerLocator
/*    */   implements MBeanServerLocator
/*    */ {
/*    */   private MBeanServer server;
/*    */ 
/*    */   public synchronized MBeanServer getMBeanServer()
/*    */   {
/* 43 */     if (this.server == null)
/*    */     {
/* 45 */       this.server = ManagementFactory.getPlatformMBeanServer();
/* 46 */       if (Log.isDebug())
/* 47 */         Log.getLogger("Management.MBeanServer").debug("Using MBeanServer: " + this.server);
/*    */     }
/* 49 */     return this.server;
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.PlatformMBeanServerLocator
 * JD-Core Version:    0.6.0
 */
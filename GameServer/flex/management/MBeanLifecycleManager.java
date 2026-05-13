/*    */ package flex.management;
/*    */ 
/*    */ import flex.messaging.MessageBroker;
/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.ObjectName;
/*    */ 
/*    */ public class MBeanLifecycleManager
/*    */ {
/*    */   public static void unregisterRuntimeMBeans(MessageBroker broker)
/*    */   {
/* 45 */     MBeanServer server = MBeanServerLocatorFactory.getMBeanServerLocator().getMBeanServer();
/* 46 */     ObjectName brokerMBean = broker.getControl().getObjectName();
/* 47 */     String domain = brokerMBean.getDomain();
/*    */     try
/*    */     {
/* 50 */       ObjectName pattern = new ObjectName(domain + ":*");
/* 51 */       Set names = server.queryNames(pattern, null);
/* 52 */       Iterator iter = names.iterator();
/* 53 */       while (iter.hasNext())
/*    */       {
/* 55 */         ObjectName on = (ObjectName)iter.next();
/* 56 */         server.unregisterMBean(on);
/*    */       }
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.MBeanLifecycleManager
 * JD-Core Version:    0.6.0
 */
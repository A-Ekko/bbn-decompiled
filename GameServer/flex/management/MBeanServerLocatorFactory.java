/*    */ package flex.management;
/*    */ 
/*    */ import flex.messaging.log.Log;
/*    */ import flex.messaging.log.Logger;
/*    */ import flex.messaging.util.ClassUtil;
/*    */ 
/*    */ public class MBeanServerLocatorFactory
/*    */ {
/*    */   private static MBeanServerLocator locator;
/*    */ 
/*    */   public static synchronized MBeanServerLocator getMBeanServerLocator()
/*    */   {
/* 56 */     if (locator == null)
/*    */     {
/* 60 */       instantiateLocator("flex.management.WebSphereMBeanServerLocator", new String[] { "com.ibm.websphere.management.AdminServiceFactory" });
/*    */ 
/* 63 */       if (locator == null) {
/* 64 */         instantiateLocator("flex.management.PlatformMBeanServerLocator", new String[] { "java.lang.management.ManagementFactory" });
/*    */       }
/*    */ 
/* 67 */       if (locator == null) {
/* 68 */         instantiateLocator("flex.management.DefaultMBeanServerLocator", null);
/*    */       }
/* 70 */       if (Log.isDebug())
/* 71 */         Log.getLogger("Management.General").debug("Using MBeanServerLocator: " + locator.getClass().getName());
/*    */     }
/* 73 */     return locator;
/*    */   }
/*    */ 
/*    */   private static void instantiateLocator(String locatorClassName, String[] dependencyClassNames)
/*    */   {
/*    */     try
/*    */     {
/* 93 */       if (dependencyClassNames != null)
/*    */       {
/* 95 */         for (int i = 0; i < dependencyClassNames.length; i++) {
/* 96 */           ClassUtil.createClass(dependencyClassNames[i]);
/*    */         }
/*    */       }
/* 99 */       Class locatorClass = ClassUtil.createClass(locatorClassName);
/* 100 */       locator = (MBeanServerLocator)locatorClass.newInstance();
/*    */     }
/*    */     catch (Throwable t)
/*    */     {
/* 104 */       if (Log.isDebug())
/* 105 */         Log.getLogger("Management.MBeanServer").debug("Not using MBeanServerLocator: " + locatorClassName + ". Reason: " + t.getMessage());
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.MBeanServerLocatorFactory
 * JD-Core Version:    0.6.0
 */
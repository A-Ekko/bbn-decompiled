/*    */ package org.apache.log4j.jmx;
/*    */ 
/*    */ import com.sun.jdmk.comm.CommunicatorServer;
/*    */ import com.sun.jdmk.comm.HtmlAdaptorServer;
/*    */ import javax.management.MBeanServer;
/*    */ import javax.management.MBeanServerFactory;
/*    */ import javax.management.ObjectName;
/*    */ import org.apache.log4j.Category;
/*    */ import org.apache.log4j.Logger;
/*    */ 
/*    */ public class Agent
/*    */ {
/* 30 */   static Logger log = Logger.getLogger(Agent.class);
/*    */ 
/*    */   public void start()
/*    */   {
/* 39 */     MBeanServer server = MBeanServerFactory.createMBeanServer();
/* 40 */     HtmlAdaptorServer html = new HtmlAdaptorServer();
/*    */     try
/*    */     {
/* 43 */       log.info("Registering HtmlAdaptorServer instance.");
/* 44 */       server.registerMBean(html, new ObjectName("Adaptor:name=html,port=8082"));
/* 45 */       log.info("Registering HierarchyDynamicMBean instance.");
/* 46 */       HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
/* 47 */       server.registerMBean(hdm, new ObjectName("log4j:hiearchy=default"));
/*    */     }
/*    */     catch (Exception e) {
/* 50 */       log.error("Problem while regitering MBeans instances.", e);
/* 51 */       return;
/*    */     }
/* 53 */     html.start();
/*    */   }
/*    */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.jmx.Agent
 * JD-Core Version:    0.6.0
 */
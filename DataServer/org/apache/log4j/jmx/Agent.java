package org.apache.log4j.jmx;

import com.sun.jdmk.comm.CommunicatorServer;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.log4j.Category;

public class Agent
{
  static Category log = Category.getInstance(Agent.class);

  public void start()
  {
    MBeanServer localMBeanServer = MBeanServerFactory.createMBeanServer();
    HtmlAdaptorServer localHtmlAdaptorServer = new HtmlAdaptorServer();
    try
    {
      log.info("Registering HtmlAdaptorServer instance.");
      localMBeanServer.registerMBean(localHtmlAdaptorServer, new ObjectName("Adaptor:name=html,port=8082"));
      log.info("Registering HierarchyDynamicMBean instance.");
      HierarchyDynamicMBean localHierarchyDynamicMBean = new HierarchyDynamicMBean();
      localMBeanServer.registerMBean(localHierarchyDynamicMBean, new ObjectName("log4j:hiearchy=default"));
    }
    catch (Exception localException)
    {
      log.error("Problem while regitering MBeans instances.", localException);
      return;
    }
    localHtmlAdaptorServer.start();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jmx.Agent
 * JD-Core Version:    0.6.0
 */
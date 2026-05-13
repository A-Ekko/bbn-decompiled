package org.apache.log4j.jmx;

import java.lang.reflect.Constructor;
import java.util.Vector;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerRepository;

public class HierarchyDynamicMBean extends AbstractDynamicMBean
  implements HierarchyEventListener, NotificationBroadcaster
{
  static final String ADD_APPENDER = "addAppender.";
  static final String THRESHOLD = "threshold";
  private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
  private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
  private Vector vAttributes = new Vector();
  private String dClassName = getClass().getName();
  private String dDescription = "This MBean acts as a management facade for org.apache.log4j.Hierarchy.";
  private NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();
  private LoggerRepository hierarchy = LogManager.getLoggerRepository();
  private static Logger log = Logger.getLogger(HierarchyDynamicMBean.class);

  public HierarchyDynamicMBean()
  {
    buildDynamicMBeanInfo();
  }

  private void buildDynamicMBeanInfo()
  {
    Constructor[] arrayOfConstructor = getClass().getConstructors();
    this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", arrayOfConstructor[0]);
    this.vAttributes.add(new MBeanAttributeInfo("threshold", "java.lang.String", "The \"threshold\" state of the hiearchy.", true, true, false));
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[1];
    arrayOfMBeanParameterInfo[0] = new MBeanParameterInfo("name", "java.lang.String", "Create a logger MBean");
    this.dOperations[0] = new MBeanOperationInfo("addLoggerMBean", "addLoggerMBean(): add a loggerMBean", arrayOfMBeanParameterInfo, "javax.management.ObjectName", 1);
  }

  public ObjectName addLoggerMBean(String paramString)
  {
    Logger localLogger = Category.exists(paramString);
    if (localLogger != null)
      return addLoggerMBean(localLogger);
    return null;
  }

  ObjectName addLoggerMBean(Logger paramLogger)
  {
    String str = paramLogger.getName();
    ObjectName localObjectName = null;
    try
    {
      LoggerDynamicMBean localLoggerDynamicMBean = new LoggerDynamicMBean(paramLogger);
      localObjectName = new ObjectName("log4j", "logger", str);
      this.server.registerMBean(localLoggerDynamicMBean, localObjectName);
      NotificationFilterSupport localNotificationFilterSupport = new NotificationFilterSupport();
      localNotificationFilterSupport.enableType("addAppender." + paramLogger.getName());
      log.debug("---Adding logger [" + str + "] as listener.");
      this.nbs.addNotificationListener(localLoggerDynamicMBean, localNotificationFilterSupport, null);
      this.vAttributes.add(new MBeanAttributeInfo("logger=" + str, "javax.management.ObjectName", "The " + str + " logger.", true, true, false));
    }
    catch (Exception localException)
    {
      log.error("Couls not add loggerMBean for [" + str + "].");
    }
    return localObjectName;
  }

  public void addNotificationListener(NotificationListener paramNotificationListener, NotificationFilter paramNotificationFilter, Object paramObject)
  {
    this.nbs.addNotificationListener(paramNotificationListener, paramNotificationFilter, paramObject);
  }

  protected Logger getLogger()
  {
    return log;
  }

  public MBeanInfo getMBeanInfo()
  {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = new MBeanAttributeInfo[this.vAttributes.size()];
    this.vAttributes.toArray(arrayOfMBeanAttributeInfo);
    return new MBeanInfo(this.dClassName, this.dDescription, arrayOfMBeanAttributeInfo, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
  }

  public MBeanNotificationInfo[] getNotificationInfo()
  {
    return this.nbs.getNotificationInfo();
  }

  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
    if (paramString.equals("addLoggerMBean"))
      return addLoggerMBean((String)paramArrayOfObject[0]);
    throw new ReflectionException(new NoSuchMethodException(paramString), "Cannot find the operation " + paramString + " in " + this.dClassName);
  }

  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
    log.debug("Called getAttribute with [" + paramString + "].");
    if (paramString.equals("threshold"))
      return this.hierarchy.getThreshold();
    if (paramString.startsWith("logger"))
    {
      int i = paramString.indexOf("%3D");
      String str = paramString;
      if (i > 0)
        str = paramString.substring(0, i) + '=' + paramString.substring(i + 3);
      try
      {
        return new ObjectName("log4j:" + str);
      }
      catch (Exception localException)
      {
        log.error("Could not create ObjectName" + str);
      }
    }
    throw new AttributeNotFoundException("Cannot find " + paramString + " attribute in " + this.dClassName);
  }

  public void addAppenderEvent(Category paramCategory, Appender paramAppender)
  {
    log.debug("addAppenderEvent called: logger=" + paramCategory.getName() + ", appender=" + paramAppender.getName());
    Notification localNotification = new Notification("addAppender." + paramCategory.getName(), this, 0L);
    localNotification.setUserData(paramAppender);
    log.debug("sending notification.");
    this.nbs.sendNotification(localNotification);
  }

  public void removeAppenderEvent(Category paramCategory, Appender paramAppender)
  {
    log.debug("removeAppenderCalled: logger=" + paramCategory.getName() + ", appender=" + paramAppender.getName());
  }

  public void postRegister(Boolean paramBoolean)
  {
    log.debug("postRegister is called.");
    this.hierarchy.addHierarchyEventListener(this);
    Logger localLogger = this.hierarchy.getRootLogger();
    addLoggerMBean(localLogger);
  }

  public void removeNotificationListener(NotificationListener paramNotificationListener)
    throws ListenerNotFoundException
  {
    this.nbs.removeNotificationListener(paramNotificationListener);
  }

  public void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    if (paramAttribute == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
    String str = paramAttribute.getName();
    Object localObject = paramAttribute.getValue();
    if (str == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
    if (str.equals("threshold"))
    {
      Level localLevel = OptionConverter.toLevel((String)localObject, this.hierarchy.getThreshold());
      this.hierarchy.setThreshold(localLevel);
    }
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jmx.HierarchyDynamicMBean
 * JD-Core Version:    0.6.0
 */
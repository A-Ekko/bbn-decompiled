package org.apache.log4j.jmx;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.Vector;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.OptionConverter;

public class LoggerDynamicMBean extends AbstractDynamicMBean
  implements NotificationListener
{
  private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
  private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
  private Vector dAttributes = new Vector();
  private String dClassName = getClass().getName();
  private String dDescription = "This MBean acts as a management facade for a org.apache.log4j.Logger instance.";
  private static Logger cat = Logger.getLogger(LoggerDynamicMBean.class);
  private Logger logger;

  public LoggerDynamicMBean(Logger paramLogger)
  {
    this.logger = paramLogger;
    buildDynamicMBeanInfo();
  }

  public void handleNotification(Notification paramNotification, Object paramObject)
  {
    cat.debug("Received notification: " + paramNotification.getType());
    registerAppenderMBean((Appender)paramNotification.getUserData());
  }

  private void buildDynamicMBeanInfo()
  {
    Constructor[] arrayOfConstructor = getClass().getConstructors();
    this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", arrayOfConstructor[0]);
    this.dAttributes.add(new MBeanAttributeInfo("name", "java.lang.String", "The name of this Logger.", true, false, false));
    this.dAttributes.add(new MBeanAttributeInfo("priority", "java.lang.String", "The priority of this logger.", true, true, false));
    MBeanParameterInfo[] arrayOfMBeanParameterInfo = new MBeanParameterInfo[2];
    arrayOfMBeanParameterInfo[0] = new MBeanParameterInfo("class name", "java.lang.String", "add an appender to this logger");
    arrayOfMBeanParameterInfo[1] = new MBeanParameterInfo("appender name", "java.lang.String", "name of the appender");
    this.dOperations[0] = new MBeanOperationInfo("addAppender", "addAppender(): add an appender", arrayOfMBeanParameterInfo, "void", 1);
  }

  protected Logger getLogger()
  {
    return this.logger;
  }

  public MBeanInfo getMBeanInfo()
  {
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = new MBeanAttributeInfo[this.dAttributes.size()];
    this.dAttributes.toArray(arrayOfMBeanAttributeInfo);
    MBeanInfo localMBeanInfo = new MBeanInfo(this.dClassName, this.dDescription, arrayOfMBeanAttributeInfo, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
    return localMBeanInfo;
  }

  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    if (paramString.equals("addAppender"))
    {
      addAppender((String)paramArrayOfObject[0], (String)paramArrayOfObject[1]);
      return "Hello world.";
    }
    return null;
  }

  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
    if (paramString.equals("name"))
      return this.logger.getName();
    if (paramString.equals("priority"))
    {
      Level localLevel = this.logger.getLevel();
      if (localLevel == null)
        return null;
      return localLevel.toString();
    }
    if (paramString.startsWith("appender="))
      try
      {
        return new ObjectName("log4j:" + paramString);
      }
      catch (Exception localException)
      {
        cat.error("Could not create ObjectName" + paramString);
      }
    throw new AttributeNotFoundException("Cannot find " + paramString + " attribute in " + this.dClassName);
  }

  void addAppender(String paramString1, String paramString2)
  {
    cat.debug("addAppender called with " + paramString1 + ", " + paramString2);
    Appender localAppender = (Appender)OptionConverter.instantiateByClassName(paramString1, Appender.class, null);
    localAppender.setName(paramString2);
    this.logger.addAppender(localAppender);
  }

  public void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
  {
    if (paramAttribute == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
    String str1 = paramAttribute.getName();
    Object localObject = paramAttribute.getValue();
    if (str1 == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
    if (str1.equals("priority"))
    {
      if ((localObject instanceof String))
      {
        String str2 = (String)localObject;
        Level localLevel = this.logger.getLevel();
        if (str2.equalsIgnoreCase("NULL"))
          localLevel = null;
        else
          localLevel = OptionConverter.toLevel(str2, localLevel);
        this.logger.setPriority(localLevel);
      }
    }
    else
      throw new AttributeNotFoundException("Attribute " + str1 + " not found in " + getClass().getName());
  }

  void appenderMBeanRegistration()
  {
    Enumeration localEnumeration = this.logger.getAllAppenders();
    while (localEnumeration.hasMoreElements())
    {
      Appender localAppender = (Appender)localEnumeration.nextElement();
      registerAppenderMBean(localAppender);
    }
  }

  void registerAppenderMBean(Appender paramAppender)
  {
    String str = paramAppender.getName();
    cat.debug("Adding AppenderMBean for appender named " + str);
    ObjectName localObjectName = null;
    try
    {
      AppenderDynamicMBean localAppenderDynamicMBean = new AppenderDynamicMBean(paramAppender);
      localObjectName = new ObjectName("log4j", "appender", str);
      this.server.registerMBean(localAppenderDynamicMBean, localObjectName);
      this.dAttributes.add(new MBeanAttributeInfo("appender=" + str, "javax.management.ObjectName", "The " + str + " appender.", true, true, false));
    }
    catch (Exception localException)
    {
      cat.error("Could not add appenderMBean for [" + str + "].", localException);
    }
  }

  public void postRegister(Boolean paramBoolean)
  {
    appenderMBeanRegistration();
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jmx.LoggerDynamicMBean
 * JD-Core Version:    0.6.0
 */
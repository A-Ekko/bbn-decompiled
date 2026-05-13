package org.apache.log4j.jmx;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Hashtable;
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
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

public class AppenderDynamicMBean extends AbstractDynamicMBean
{
  private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
  private Vector dAttributes = new Vector();
  private String dClassName = getClass().getName();
  private Hashtable dynamicProps = new Hashtable(5);
  private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[2];
  private String dDescription = "This MBean acts as a management facade for log4j appenders.";
  private static Logger cat = Logger.getLogger(AppenderDynamicMBean.class);
  private Appender appender;

  public AppenderDynamicMBean(Appender paramAppender)
    throws IntrospectionException
  {
    this.appender = paramAppender;
    buildDynamicMBeanInfo();
  }

  private void buildDynamicMBeanInfo()
    throws IntrospectionException
  {
    Constructor[] arrayOfConstructor = getClass().getConstructors();
    this.dConstructors[0] = new MBeanConstructorInfo("AppenderDynamicMBean(): Constructs a AppenderDynamicMBean instance", arrayOfConstructor[0]);
    BeanInfo localBeanInfo = Introspector.getBeanInfo(this.appender.getClass());
    PropertyDescriptor[] arrayOfPropertyDescriptor = localBeanInfo.getPropertyDescriptors();
    int i = arrayOfPropertyDescriptor.length;
    for (int j = 0; j < i; j++)
    {
      localObject = arrayOfPropertyDescriptor[j].getName();
      Method localMethod1 = arrayOfPropertyDescriptor[j].getReadMethod();
      Method localMethod2 = arrayOfPropertyDescriptor[j].getWriteMethod();
      if (localMethod1 == null)
        continue;
      Class localClass = localMethod1.getReturnType();
      if (!isSupportedType(localClass))
        continue;
      String str;
      if (localClass.isAssignableFrom(Priority.class))
        str = "java.lang.String";
      else
        str = localClass.getName();
      this.dAttributes.add(new MBeanAttributeInfo((String)localObject, str, "Dynamic", true, localMethod2 != null, false));
      this.dynamicProps.put(localObject, new MethodUnion(localMethod1, localMethod2));
    }
    Object localObject = new MBeanParameterInfo[0];
    this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an appender", localObject, "void", 1);
    localObject = new MBeanParameterInfo[1];
    localObject[0] = new MBeanParameterInfo("layout class", "java.lang.String", "layout class");
    this.dOperations[1] = new MBeanOperationInfo("setLayout", "setLayout(): add a layout", localObject, "void", 1);
  }

  private boolean isSupportedType(Class paramClass)
  {
    if (paramClass.isPrimitive())
      return true;
    if (paramClass == String.class)
      return true;
    return paramClass.isAssignableFrom(Priority.class);
  }

  public MBeanInfo getMBeanInfo()
  {
    cat.debug("getMBeanInfo called.");
    MBeanAttributeInfo[] arrayOfMBeanAttributeInfo = new MBeanAttributeInfo[this.dAttributes.size()];
    this.dAttributes.toArray(arrayOfMBeanAttributeInfo);
    return new MBeanInfo(this.dClassName, this.dDescription, arrayOfMBeanAttributeInfo, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
  }

  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    Object localObject;
    if ((paramString.equals("activateOptions")) && ((this.appender instanceof OptionHandler)))
    {
      localObject = (OptionHandler)this.appender;
      ((OptionHandler)localObject).activateOptions();
      return "Options activated.";
    }
    if (paramString.equals("setLayout"))
    {
      localObject = (Layout)OptionConverter.instantiateByClassName((String)paramArrayOfObject[0], Layout.class, null);
      this.appender.setLayout((Layout)localObject);
      registerLayoutMBean((Layout)localObject);
    }
    return null;
  }

  void registerLayoutMBean(Layout paramLayout)
  {
    if (paramLayout == null)
      return;
    String str = this.appender.getName() + ",layout=" + paramLayout.getClass().getName();
    cat.debug("Adding LayoutMBean:" + str);
    ObjectName localObjectName = null;
    try
    {
      LayoutDynamicMBean localLayoutDynamicMBean = new LayoutDynamicMBean(paramLayout);
      localObjectName = new ObjectName("log4j:appender=" + str);
      this.server.registerMBean(localLayoutDynamicMBean, localObjectName);
      this.dAttributes.add(new MBeanAttributeInfo("appender=" + str, "javax.management.ObjectName", "The " + str + " layout.", true, true, false));
    }
    catch (Exception localException)
    {
      cat.error("Could not add DynamicLayoutMBean for [" + str + "].", localException);
    }
  }

  protected Logger getLogger()
  {
    return cat;
  }

  public Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException
  {
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
    cat.debug("getAttribute called with [" + paramString + "].");
    if (paramString.startsWith("appender=" + this.appender.getName() + ",layout"))
      try
      {
        return new ObjectName("log4j:" + paramString);
      }
      catch (Exception localException1)
      {
        cat.error("attributeName", localException1);
      }
    MethodUnion localMethodUnion = (MethodUnion)this.dynamicProps.get(paramString);
    if ((localMethodUnion != null) && (localMethodUnion.readMethod != null))
      try
      {
        return localMethodUnion.readMethod.invoke(this.appender, null);
      }
      catch (Exception localException2)
      {
        return null;
      }
    throw new AttributeNotFoundException("Cannot find " + paramString + " attribute in " + this.dClassName);
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
    MethodUnion localMethodUnion = (MethodUnion)this.dynamicProps.get(str);
    if ((localMethodUnion != null) && (localMethodUnion.writeMethod != null))
    {
      Object[] arrayOfObject = new Object[1];
      Class[] arrayOfClass = localMethodUnion.writeMethod.getParameterTypes();
      if (arrayOfClass[0] == Priority.class)
        localObject = OptionConverter.toLevel((String)localObject, (Level)getAttribute(str));
      arrayOfObject[0] = localObject;
      try
      {
        localMethodUnion.writeMethod.invoke(this.appender, arrayOfObject);
      }
      catch (Exception localException)
      {
        cat.error("FIXME", localException);
      }
    }
    else if (!str.endsWith(".layout"))
    {
      throw new AttributeNotFoundException("Attribute " + str + " not found in " + getClass().getName());
    }
  }

  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    cat.debug("preRegister called. Server=" + paramMBeanServer + ", name=" + paramObjectName);
    this.server = paramMBeanServer;
    registerLayoutMBean(this.appender.getLayout());
    return paramObjectName;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jmx.AppenderDynamicMBean
 * JD-Core Version:    0.6.0
 */
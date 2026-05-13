package org.apache.log4j.jmx;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;

public abstract class AbstractDynamicMBean
  implements DynamicMBean, MBeanRegistration
{
  String dClassName;
  MBeanServer server;

  public AttributeList getAttributes(String[] paramArrayOfString)
  {
    if (paramArrayOfString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"), "Cannot invoke a getter of " + this.dClassName);
    AttributeList localAttributeList = new AttributeList();
    if (paramArrayOfString.length == 0)
      return localAttributeList;
    for (int i = 0; i < paramArrayOfString.length; i++)
      try
      {
        Object localObject = getAttribute(paramArrayOfString[i]);
        localAttributeList.add(new Attribute(paramArrayOfString[i], localObject));
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    return localAttributeList;
  }

  public AttributeList setAttributes(AttributeList paramAttributeList)
  {
    if (paramAttributeList == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"), "Cannot invoke a setter of " + this.dClassName);
    AttributeList localAttributeList = new AttributeList();
    if (paramAttributeList.isEmpty())
      return localAttributeList;
    Iterator localIterator = paramAttributeList.iterator();
    while (localIterator.hasNext())
    {
      Attribute localAttribute = (Attribute)localIterator.next();
      try
      {
        setAttribute(localAttribute);
        String str = localAttribute.getName();
        Object localObject = getAttribute(str);
        localAttributeList.add(new Attribute(str, localObject));
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    return localAttributeList;
  }

  protected abstract Logger getLogger();

  public void postDeregister()
  {
    getLogger().debug("postDeregister is called.");
  }

  public void postRegister(Boolean paramBoolean)
  {
  }

  public void preDeregister()
  {
    getLogger().debug("preDeregister called.");
  }

  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
  {
    getLogger().debug("preRegister called. Server=" + paramMBeanServer + ", name=" + paramObjectName);
    this.server = paramMBeanServer;
    return paramObjectName;
  }

  public abstract void setAttribute(Attribute paramAttribute)
    throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;

  public abstract Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException;

  public abstract MBeanInfo getMBeanInfo();

  public abstract Object getAttribute(String paramString)
    throws AttributeNotFoundException, MBeanException, ReflectionException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.jmx.AbstractDynamicMBean
 * JD-Core Version:    0.6.0
 */
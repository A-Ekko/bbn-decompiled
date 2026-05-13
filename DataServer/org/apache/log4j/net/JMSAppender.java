package org.apache.log4j.net;

import java.util.Hashtable;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

public class JMSAppender extends AppenderSkeleton
{
  String securityPrincipalName;
  String securityCredentials;
  String initialContextFactoryName;
  String urlPkgPrefixes;
  String providerURL;
  String topicBindingName;
  String tcfBindingName;
  String userName;
  String password;
  boolean locationInfo;
  TopicConnection topicConnection;
  TopicSession topicSession;
  TopicPublisher topicPublisher;

  public void setTopicConnectionFactoryBindingName(String paramString)
  {
    this.tcfBindingName = paramString;
  }

  public String getTopicConnectionFactoryBindingName()
  {
    return this.tcfBindingName;
  }

  public void setTopicBindingName(String paramString)
  {
    this.topicBindingName = paramString;
  }

  public String getTopicBindingName()
  {
    return this.topicBindingName;
  }

  public boolean getLocationInfo()
  {
    return this.locationInfo;
  }

  public void activateOptions()
  {
    try
    {
      LogLog.debug("Getting initial context.");
      InitialContext localInitialContext;
      if (this.initialContextFactoryName != null)
      {
        localObject = new Properties();
        ((Hashtable)localObject).put("java.naming.factory.initial", this.initialContextFactoryName);
        if (this.providerURL != null)
          ((Hashtable)localObject).put("java.naming.provider.url", this.providerURL);
        else
          LogLog.warn("You have set InitialContextFactoryName option but not the ProviderURL. This is likely to cause problems.");
        if (this.urlPkgPrefixes != null)
          ((Hashtable)localObject).put("java.naming.factory.url.pkgs", this.urlPkgPrefixes);
        if (this.securityPrincipalName != null)
        {
          ((Hashtable)localObject).put("java.naming.security.principal", this.securityPrincipalName);
          if (this.securityCredentials != null)
            ((Hashtable)localObject).put("java.naming.security.credentials", this.securityCredentials);
          else
            LogLog.warn("You have set SecurityPrincipalName option but not the SecurityCredentials. This is likely to cause problems.");
        }
        localInitialContext = new InitialContext((Hashtable)localObject);
      }
      else
      {
        localInitialContext = new InitialContext();
      }
      LogLog.debug("Looking up [" + this.tcfBindingName + "]");
      TopicConnectionFactory localTopicConnectionFactory = (TopicConnectionFactory)lookup(localInitialContext, this.tcfBindingName);
      LogLog.debug("About to create TopicConnection.");
      if (this.userName != null)
        this.topicConnection = localTopicConnectionFactory.createTopicConnection(this.userName, this.password);
      else
        this.topicConnection = localTopicConnectionFactory.createTopicConnection();
      LogLog.debug("Creating TopicSession, non-transactional, in AUTO_ACKNOWLEDGE mode.");
      this.topicSession = this.topicConnection.createTopicSession(false, 1);
      LogLog.debug("Looking up topic name [" + this.topicBindingName + "].");
      Object localObject = (Topic)lookup(localInitialContext, this.topicBindingName);
      LogLog.debug("Creating TopicPublisher.");
      this.topicPublisher = this.topicSession.createPublisher((Topic)localObject);
      LogLog.debug("Starting TopicConnection.");
      this.topicConnection.start();
      localInitialContext.close();
    }
    catch (Exception localException)
    {
      this.errorHandler.error("Error while activating options for appender named [" + this.name + "].", localException, 0);
    }
  }

  protected Object lookup(Context paramContext, String paramString)
    throws NamingException
  {
    try
    {
      return paramContext.lookup(paramString);
    }
    catch (NameNotFoundException localNameNotFoundException)
    {
      LogLog.error("Could not find name [" + paramString + "].");
    }
    throw localNameNotFoundException;
  }

  protected boolean checkEntryConditions()
  {
    String str = null;
    if (this.topicConnection == null)
      str = "No TopicConnection";
    else if (this.topicSession == null)
      str = "No TopicSession";
    else if (this.topicPublisher == null)
      str = "No TopicPublisher";
    if (str != null)
    {
      this.errorHandler.error(str + " for JMSAppender named [" + this.name + "].");
      return false;
    }
    return true;
  }

  public synchronized void close()
  {
    if (this.closed)
      return;
    LogLog.debug("Closing appender [" + this.name + "].");
    this.closed = true;
    try
    {
      if (this.topicSession != null)
        this.topicSession.close();
      if (this.topicConnection != null)
        this.topicConnection.close();
    }
    catch (Exception localException)
    {
      LogLog.error("Error while closing JMSAppender [" + this.name + "].", localException);
    }
    this.topicPublisher = null;
    this.topicSession = null;
    this.topicConnection = null;
  }

  public void append(LoggingEvent paramLoggingEvent)
  {
    if (!checkEntryConditions())
      return;
    try
    {
      ObjectMessage localObjectMessage = this.topicSession.createObjectMessage();
      if (this.locationInfo)
        paramLoggingEvent.getLocationInformation();
      localObjectMessage.setObject(paramLoggingEvent);
      this.topicPublisher.publish(localObjectMessage);
    }
    catch (Exception localException)
    {
      this.errorHandler.error("Could not publish message in JMSAppender [" + this.name + "].", localException, 0);
    }
  }

  public String getInitialContextFactoryName()
  {
    return this.initialContextFactoryName;
  }

  public void setInitialContextFactoryName(String paramString)
  {
    this.initialContextFactoryName = paramString;
  }

  public String getProviderURL()
  {
    return this.providerURL;
  }

  public void setProviderURL(String paramString)
  {
    this.providerURL = paramString;
  }

  String getURLPkgPrefixes()
  {
    return this.urlPkgPrefixes;
  }

  public void setURLPkgPrefixes(String paramString)
  {
    this.urlPkgPrefixes = paramString;
  }

  public String getSecurityCredentials()
  {
    return this.securityCredentials;
  }

  public void setSecurityCredentials(String paramString)
  {
    this.securityCredentials = paramString;
  }

  public String getSecurityPrincipalName()
  {
    return this.securityPrincipalName;
  }

  public void setSecurityPrincipalName(String paramString)
  {
    this.securityPrincipalName = paramString;
  }

  public String getUserName()
  {
    return this.userName;
  }

  public void setUserName(String paramString)
  {
    this.userName = paramString;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String paramString)
  {
    this.password = paramString;
  }

  public void setLocationInfo(boolean paramBoolean)
  {
    this.locationInfo = paramBoolean;
  }

  public boolean requiresLayout()
  {
    return false;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.JMSAppender
 * JD-Core Version:    0.6.0
 */
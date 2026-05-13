package org.apache.log4j.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.log4j.Category;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;

public class JMSSink
  implements MessageListener
{
  static Logger logger = Logger.getLogger(JMSSink.class);

  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    if (paramArrayOfString.length != 5)
      usage("Wrong number of arguments.");
    String str1 = paramArrayOfString[0];
    String str2 = paramArrayOfString[1];
    String str3 = paramArrayOfString[2];
    String str4 = paramArrayOfString[3];
    String str5 = paramArrayOfString[4];
    if (str5.endsWith(".xml"))
    {
      new DOMConfigurator();
      DOMConfigurator.configure(str5);
    }
    else
    {
      new PropertyConfigurator();
      PropertyConfigurator.configure(str5);
    }
    new JMSSink(str1, str2, str3, str4);
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Type \"exit\" to quit JMSSink.");
    String str6;
    do
      str6 = localBufferedReader.readLine();
    while (!str6.equalsIgnoreCase("exit"));
    System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
  }

  public JMSSink(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      InitialContext localInitialContext = new InitialContext();
      TopicConnectionFactory localTopicConnectionFactory = (TopicConnectionFactory)lookup(localInitialContext, paramString1);
      TopicConnection localTopicConnection = localTopicConnectionFactory.createTopicConnection(paramString3, paramString4);
      localTopicConnection.start();
      TopicSession localTopicSession = localTopicConnection.createTopicSession(false, 1);
      Topic localTopic = (Topic)localInitialContext.lookup(paramString2);
      TopicSubscriber localTopicSubscriber = localTopicSession.createSubscriber(localTopic);
      localTopicSubscriber.setMessageListener(this);
    }
    catch (Exception localException)
    {
      logger.error("Could not read JMS message.", localException);
    }
  }

  public void onMessage(Message paramMessage)
  {
    try
    {
      if ((paramMessage instanceof ObjectMessage))
      {
        ObjectMessage localObjectMessage = (ObjectMessage)paramMessage;
        LoggingEvent localLoggingEvent = (LoggingEvent)localObjectMessage.getObject();
        Logger localLogger = Logger.getLogger(localLoggingEvent.getLoggerName());
        localLogger.callAppenders(localLoggingEvent);
      }
      else
      {
        logger.warn("Received message is of type " + paramMessage.getJMSType() + ", was expecting ObjectMessage.");
      }
    }
    catch (JMSException localJMSException)
    {
      logger.error("Exception thrown while processing incoming message.", localJMSException);
    }
  }

  protected static Object lookup(Context paramContext, String paramString)
    throws NamingException
  {
    try
    {
      return paramContext.lookup(paramString);
    }
    catch (NameNotFoundException localNameNotFoundException)
    {
      logger.error("Could not find name [" + paramString + "].");
    }
    throw localNameNotFoundException;
  }

  static void usage(String paramString)
  {
    System.err.println(paramString);
    System.err.println("Usage: java " + JMSSink.class.getName() + " TopicConnectionFactoryBindingName TopicBindingName username password configFile");
    System.exit(1);
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.JMSSink
 * JD-Core Version:    0.6.0
 */
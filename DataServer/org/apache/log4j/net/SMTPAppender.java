package org.apache.log4j.net;

import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CyclicBuffer;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

public class SMTPAppender extends AppenderSkeleton
{
  private String to;
  private String from;
  private String subject;
  private String smtpHost;
  private int bufferSize = 512;
  private boolean locationInfo = false;
  protected CyclicBuffer cb = new CyclicBuffer(this.bufferSize);
  protected Message msg;
  protected TriggeringEventEvaluator evaluator;

  public SMTPAppender()
  {
    this(new DefaultEvaluator());
  }

  public SMTPAppender(TriggeringEventEvaluator paramTriggeringEventEvaluator)
  {
    this.evaluator = paramTriggeringEventEvaluator;
  }

  public void activateOptions()
  {
    Properties localProperties = new Properties(System.getProperties());
    if (this.smtpHost != null)
      localProperties.put("mail.smtp.host", this.smtpHost);
    Session localSession = Session.getInstance(localProperties, null);
    this.msg = new MimeMessage(localSession);
    try
    {
      if (this.from != null)
        this.msg.setFrom(getAddress(this.from));
      else
        this.msg.setFrom();
      this.msg.setRecipients(Message.RecipientType.TO, parseAddress(this.to));
      if (this.subject != null)
        this.msg.setSubject(this.subject);
    }
    catch (MessagingException localMessagingException)
    {
      LogLog.error("Could not activate SMTPAppender options.", localMessagingException);
    }
  }

  public void append(LoggingEvent paramLoggingEvent)
  {
    if (!checkEntryConditions())
      return;
    paramLoggingEvent.getThreadName();
    paramLoggingEvent.getNDC();
    if (this.locationInfo)
      paramLoggingEvent.getLocationInformation();
    this.cb.add(paramLoggingEvent);
    if (this.evaluator.isTriggeringEvent(paramLoggingEvent))
      sendBuffer();
  }

  protected boolean checkEntryConditions()
  {
    if (this.msg == null)
    {
      this.errorHandler.error("Message object not configured.");
      return false;
    }
    if (this.evaluator == null)
    {
      this.errorHandler.error("No TriggeringEventEvaluator is set for appender [" + this.name + "].");
      return false;
    }
    if (this.layout == null)
    {
      this.errorHandler.error("No layout set for appender named [" + this.name + "].");
      return false;
    }
    return true;
  }

  public synchronized void close()
  {
    this.closed = true;
  }

  InternetAddress getAddress(String paramString)
  {
    try
    {
      return new InternetAddress(paramString);
    }
    catch (AddressException localAddressException)
    {
      this.errorHandler.error("Could not parse address [" + paramString + "].", localAddressException, 6);
    }
    return null;
  }

  InternetAddress[] parseAddress(String paramString)
  {
    try
    {
      return InternetAddress.parse(paramString, true);
    }
    catch (AddressException localAddressException)
    {
      this.errorHandler.error("Could not parse address [" + paramString + "].", localAddressException, 6);
    }
    return null;
  }

  public String getTo()
  {
    return this.to;
  }

  public boolean requiresLayout()
  {
    return true;
  }

  protected void sendBuffer()
  {
    try
    {
      MimeBodyPart localMimeBodyPart = new MimeBodyPart();
      StringBuffer localStringBuffer = new StringBuffer();
      String str = this.layout.getHeader();
      if (str != null)
        localStringBuffer.append(str);
      int i = this.cb.length();
      for (int j = 0; j < i; j++)
      {
        localObject = this.cb.get();
        localStringBuffer.append(this.layout.format((LoggingEvent)localObject));
        if (!this.layout.ignoresThrowable())
          continue;
        String[] arrayOfString = ((LoggingEvent)localObject).getThrowableStrRep();
        if (arrayOfString == null)
          continue;
        for (int k = 0; k < arrayOfString.length; k++)
          localStringBuffer.append(arrayOfString[k]);
      }
      str = this.layout.getFooter();
      if (str != null)
        localStringBuffer.append(str);
      localMimeBodyPart.setContent(localStringBuffer.toString(), this.layout.getContentType());
      Object localObject = new MimeMultipart();
      ((Multipart)localObject).addBodyPart(localMimeBodyPart);
      this.msg.setContent((Multipart)localObject);
      this.msg.setSentDate(new Date());
      Transport.send(this.msg);
    }
    catch (Exception localException)
    {
      LogLog.error("Error occured while sending e-mail notification.", localException);
    }
  }

  public String getEvaluatorClass()
  {
    return this.evaluator == null ? null : this.evaluator.getClass().getName();
  }

  public String getFrom()
  {
    return this.from;
  }

  public String getSubject()
  {
    return this.subject;
  }

  public void setFrom(String paramString)
  {
    this.from = paramString;
  }

  public void setSubject(String paramString)
  {
    this.subject = paramString;
  }

  public void setBufferSize(int paramInt)
  {
    this.bufferSize = paramInt;
    this.cb.resize(paramInt);
  }

  public void setSMTPHost(String paramString)
  {
    this.smtpHost = paramString;
  }

  public String getSMTPHost()
  {
    return this.smtpHost;
  }

  public void setTo(String paramString)
  {
    this.to = paramString;
  }

  public int getBufferSize()
  {
    return this.bufferSize;
  }

  public void setEvaluatorClass(String paramString)
  {
    this.evaluator = ((TriggeringEventEvaluator)OptionConverter.instantiateByClassName(paramString, TriggeringEventEvaluator.class, this.evaluator));
  }

  public void setLocationInfo(boolean paramBoolean)
  {
    this.locationInfo = paramBoolean;
  }

  public boolean getLocationInfo()
  {
    return this.locationInfo;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.net.SMTPAppender
 * JD-Core Version:    0.6.0
 */
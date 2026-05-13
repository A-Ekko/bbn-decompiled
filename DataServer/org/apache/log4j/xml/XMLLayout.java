package org.apache.log4j.xml;

import org.apache.log4j.Layout;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

public class XMLLayout extends Layout
{
  private final int DEFAULT_SIZE = 256;
  private final int UPPER_LIMIT = 2048;
  private StringBuffer buf = new StringBuffer(256);
  private boolean locationInfo = false;

  public void setLocationInfo(boolean paramBoolean)
  {
    this.locationInfo = paramBoolean;
  }

  public boolean getLocationInfo()
  {
    return this.locationInfo;
  }

  public void activateOptions()
  {
  }

  public String format(LoggingEvent paramLoggingEvent)
  {
    if (this.buf.capacity() > 2048)
      this.buf = new StringBuffer(256);
    else
      this.buf.setLength(0);
    this.buf.append("<log4j:event logger=\"");
    this.buf.append(paramLoggingEvent.getLoggerName());
    this.buf.append("\" timestamp=\"");
    this.buf.append(paramLoggingEvent.timeStamp);
    this.buf.append("\" level=\"");
    this.buf.append(paramLoggingEvent.getLevel());
    this.buf.append("\" thread=\"");
    this.buf.append(paramLoggingEvent.getThreadName());
    this.buf.append("\">\r\n");
    this.buf.append("<log4j:message><![CDATA[");
    Transform.appendEscapingCDATA(this.buf, paramLoggingEvent.getRenderedMessage());
    this.buf.append("]]></log4j:message>\r\n");
    String str = paramLoggingEvent.getNDC();
    if (str != null)
    {
      this.buf.append("<log4j:NDC><![CDATA[");
      this.buf.append(str);
      this.buf.append("]]></log4j:NDC>\r\n");
    }
    String[] arrayOfString = paramLoggingEvent.getThrowableStrRep();
    if (arrayOfString != null)
    {
      this.buf.append("<log4j:throwable><![CDATA[");
      for (int i = 0; i < arrayOfString.length; i++)
      {
        this.buf.append(arrayOfString[i]);
        this.buf.append("\r\n");
      }
      this.buf.append("]]></log4j:throwable>\r\n");
    }
    if (this.locationInfo)
    {
      LocationInfo localLocationInfo = paramLoggingEvent.getLocationInformation();
      this.buf.append("<log4j:locationInfo class=\"");
      this.buf.append(localLocationInfo.getClassName());
      this.buf.append("\" method=\"");
      this.buf.append(Transform.escapeTags(localLocationInfo.getMethodName()));
      this.buf.append("\" file=\"");
      this.buf.append(localLocationInfo.getFileName());
      this.buf.append("\" line=\"");
      this.buf.append(localLocationInfo.getLineNumber());
      this.buf.append("\"/>\r\n");
    }
    this.buf.append("</log4j:event>\r\n\r\n");
    return this.buf.toString();
  }

  public boolean ignoresThrowable()
  {
    return false;
  }
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.xml.XMLLayout
 * JD-Core Version:    0.6.0
 */
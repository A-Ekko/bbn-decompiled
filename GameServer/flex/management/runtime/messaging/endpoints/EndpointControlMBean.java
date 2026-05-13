package flex.management.runtime.messaging.endpoints;

import flex.management.BaseControlMBean;
import java.io.IOException;
import java.util.Date;

public abstract interface EndpointControlMBean extends BaseControlMBean
{
  public abstract Boolean isRunning()
    throws IOException;

  public abstract Date getStartTimestamp()
    throws IOException;

  public abstract Integer getServiceMessageCount()
    throws IOException;

  public abstract void resetServiceMessageCount()
    throws IOException;

  public abstract Date getLastServiceMessageTimestamp()
    throws IOException;

  public abstract Double getServiceMessageFrequency()
    throws IOException;

  public abstract String getURI()
    throws IOException;

  public abstract String getSecurityConstraint()
    throws IOException;

  public abstract Long getBytesDeserialized()
    throws IOException;

  public abstract Long getBytesSerialized()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.EndpointControlMBean
 * JD-Core Version:    0.6.0
 */
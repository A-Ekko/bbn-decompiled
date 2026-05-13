package flex.management.runtime.messaging.services.messaging;

import flex.management.BaseControlMBean;
import java.io.IOException;
import java.util.Date;

public abstract interface ThrottleManagerControlMBean extends BaseControlMBean
{
  public abstract Integer getClientIncomingMessageThrottleCount()
    throws IOException;

  public abstract void resetClientIncomingMessageThrottleCount()
    throws IOException;

  public abstract Date getLastClientIncomingMessageThrottleTimestamp()
    throws IOException;

  public abstract Double getClientIncomingMessageThrottleFrequency()
    throws IOException;

  public abstract Integer getClientOutgoingMessageThrottleCount()
    throws IOException;

  public abstract void resetClientOutgoingMessageThrottleCount()
    throws IOException;

  public abstract Date getLastClientOutgoingMessageThrottleTimestamp()
    throws IOException;

  public abstract Double getClientOutgoingMessageThrottleFrequency()
    throws IOException;

  public abstract Integer getDestinationIncomingMessageThrottleCount()
    throws IOException;

  public abstract void resetDestinationIncomingMessageThrottleCount()
    throws IOException;

  public abstract Date getLastDestinationIncomingMessageThrottleTimestamp()
    throws IOException;

  public abstract Double getDestinationIncomingMessageThrottleFrequency()
    throws IOException;

  public abstract Integer getDestinationOutgoingMessageThrottleCount()
    throws IOException;

  public abstract void resetDestinationOutgoingMessageThrottleCount()
    throws IOException;

  public abstract Date getLastDestinationOutgoingMessageThrottleTimestamp()
    throws IOException;

  public abstract Double getDestinationOutgoingMessageThrottleFrequency()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.ThrottleManagerControlMBean
 * JD-Core Version:    0.6.0
 */
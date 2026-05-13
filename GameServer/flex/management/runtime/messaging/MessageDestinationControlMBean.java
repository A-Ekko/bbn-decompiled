package flex.management.runtime.messaging;

import java.io.IOException;
import java.util.Date;
import javax.management.ObjectName;

public abstract interface MessageDestinationControlMBean extends DestinationControlMBean
{
  public abstract ObjectName getMessageCache()
    throws IOException;

  public abstract ObjectName getThrottleManager()
    throws IOException;

  public abstract ObjectName getSubscriptionManager()
    throws IOException;

  public abstract Integer getServiceMessageCount()
    throws IOException;

  public abstract void resetServiceMessageCount()
    throws IOException;

  public abstract Date getLastServiceMessageTimestamp()
    throws IOException;

  public abstract Double getServiceMessageFrequency()
    throws IOException;

  public abstract Integer getServiceCommandCount()
    throws IOException;

  public abstract void resetServiceCommandCount()
    throws IOException;

  public abstract Date getLastServiceCommandTimestamp()
    throws IOException;

  public abstract Double getServiceCommandFrequency()
    throws IOException;

  public abstract Integer getServiceMessageFromAdapterCount()
    throws IOException;

  public abstract void resetServiceMessageFromAdapterCount()
    throws IOException;

  public abstract Date getLastServiceMessageFromAdapterTimestamp()
    throws IOException;

  public abstract Double getServiceMessageFromAdapterFrequency()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.MessageDestinationControlMBean
 * JD-Core Version:    0.6.0
 */
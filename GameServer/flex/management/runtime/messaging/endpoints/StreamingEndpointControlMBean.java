package flex.management.runtime.messaging.endpoints;

import java.io.IOException;
import java.util.Date;

public abstract interface StreamingEndpointControlMBean extends EndpointControlMBean
{
  public abstract Integer getMaxStreamingClients()
    throws IOException;

  public abstract Integer getPushCount()
    throws IOException;

  public abstract void resetPushCount()
    throws IOException;

  public abstract Date getLastPushTimestamp()
    throws IOException;

  public abstract Double getPushFrequency()
    throws IOException;

  public abstract Integer getStreamingClientsCount()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.endpoints.StreamingEndpointControlMBean
 * JD-Core Version:    0.6.0
 */
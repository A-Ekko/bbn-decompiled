package flex.management.runtime.messaging;

import flex.management.BaseControlMBean;
import java.io.IOException;
import java.util.Date;
import javax.management.ObjectName;

public abstract interface MessageBrokerControlMBean extends BaseControlMBean
{
  public abstract Boolean isRunning()
    throws IOException;

  public abstract Date getStartTimestamp()
    throws IOException;

  public abstract ObjectName[] getEndpoints()
    throws IOException;

  public abstract ObjectName[] getServices()
    throws IOException;

  public abstract Integer getFlexSessionCount()
    throws IOException;

  public abstract Integer getMaxFlexSessionsInCurrentHour()
    throws IOException;

  public abstract Integer getEnterpriseConnectionCount()
    throws IOException;

  public abstract Long getAMFThroughput()
    throws IOException;

  public abstract Long getHTTPThroughput()
    throws IOException;

  public abstract Long getEnterpriseThroughput()
    throws IOException;

  public abstract Long getStreamingAMFThroughput()
    throws IOException;

  public abstract Long getStreamingHTTPThroughput()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.MessageBrokerControlMBean
 * JD-Core Version:    0.6.0
 */
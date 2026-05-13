package flex.management.runtime.messaging.services;

import flex.management.BaseControlMBean;
import java.io.IOException;
import java.util.Date;

public abstract interface ServiceAdapterControlMBean extends BaseControlMBean
{
  public abstract Boolean isRunning()
    throws IOException;

  public abstract Date getStartTimestamp()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.ServiceAdapterControlMBean
 * JD-Core Version:    0.6.0
 */
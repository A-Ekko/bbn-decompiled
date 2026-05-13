package flex.management.runtime.messaging;

import flex.management.BaseControlMBean;
import java.io.IOException;
import java.util.Date;
import javax.management.ObjectName;

public abstract interface DestinationControlMBean extends BaseControlMBean
{
  public abstract ObjectName getAdapter()
    throws IOException;

  public abstract Boolean isRunning()
    throws IOException;

  public abstract Date getStartTimestamp()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.DestinationControlMBean
 * JD-Core Version:    0.6.0
 */
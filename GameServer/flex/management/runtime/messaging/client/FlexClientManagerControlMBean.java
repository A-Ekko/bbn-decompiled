package flex.management.runtime.messaging.client;

import flex.management.BaseControlMBean;
import java.io.IOException;

public abstract interface FlexClientManagerControlMBean extends BaseControlMBean
{
  public abstract String[] getClientIds()
    throws IOException;

  public abstract Integer getClientSubscriptionCount(String paramString)
    throws IOException;

  public abstract Integer getClientSessionCount(String paramString)
    throws IOException;

  public abstract Long getClientLastUse(String paramString)
    throws IOException;

  public abstract Integer getFlexClientCount()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.client.FlexClientManagerControlMBean
 * JD-Core Version:    0.6.0
 */
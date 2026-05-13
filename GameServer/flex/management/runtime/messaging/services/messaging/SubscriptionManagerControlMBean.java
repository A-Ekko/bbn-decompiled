package flex.management.runtime.messaging.services.messaging;

import flex.management.BaseControlMBean;
import java.io.IOException;

public abstract interface SubscriptionManagerControlMBean extends BaseControlMBean
{
  public abstract Integer getSubscriberCount()
    throws IOException;

  public abstract String[] getSubscriberIds()
    throws IOException;

  public abstract void removeSubscriber(String paramString)
    throws IOException;

  public abstract void removeAllSubscribers()
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.SubscriptionManagerControlMBean
 * JD-Core Version:    0.6.0
 */
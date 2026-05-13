package flex.management.runtime.messaging.services.messaging.adapters;

import flex.management.runtime.messaging.services.ServiceAdapterControlMBean;
import java.io.IOException;

public abstract interface JMSAdapterControlMBean extends ServiceAdapterControlMBean
{
  public abstract Integer getTopicProducerCount()
    throws IOException;

  public abstract Integer getTopicConsumerCount()
    throws IOException;

  public abstract String[] getTopicConsumerIds()
    throws IOException;

  public abstract Integer getQueueProducerCount()
    throws IOException;

  public abstract Integer getQueueConsumerCount()
    throws IOException;

  public abstract String[] getQueueConsumerIds()
    throws IOException;

  public abstract void removeConsumer(String paramString)
    throws IOException;
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.management.runtime.messaging.services.messaging.adapters.JMSAdapterControlMBean
 * JD-Core Version:    0.6.0
 */
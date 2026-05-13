package flex.messaging.client;

import flex.messaging.MessageClient;
import java.util.List;

public abstract interface EndpointPushHandler
{
  public abstract void close();

  public abstract void pushMessages(List paramList);

  public abstract void registerMessageClient(MessageClient paramMessageClient);

  public abstract void unregisterMessageClient(MessageClient paramMessageClient);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.client.EndpointPushHandler
 * JD-Core Version:    0.6.0
 */
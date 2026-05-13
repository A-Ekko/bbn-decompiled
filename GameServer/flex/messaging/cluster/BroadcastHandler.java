package flex.messaging.cluster;

import java.util.List;

public abstract interface BroadcastHandler
{
  public abstract void handleBroadcast(Object paramObject, List paramList);

  public abstract boolean isSupportedOperation(String paramString);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.cluster.BroadcastHandler
 * JD-Core Version:    0.6.0
 */
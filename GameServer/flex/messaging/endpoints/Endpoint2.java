package flex.messaging.endpoints;

import flex.messaging.Server;

public abstract interface Endpoint2 extends Endpoint
{
  public abstract Server getServer();

  public abstract void setServer(Server paramServer);
}

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.endpoints.Endpoint2
 * JD-Core Version:    0.6.0
 */